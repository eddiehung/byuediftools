/*
 * Represents a specific instance of an EdifCell object.
 * 
 * Copyright (c) 2008 Brigham Young University
 * 
 * This file is part of the BYU EDIF Tools.
 * 
 * BYU EDIF Tools is free software: you may redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * 
 * BYU EDIF Tools is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * A copy of the GNU General Public License is included with the BYU EDIF Tools.
 * It can be found at /edu/byu/edif/doc/gpl2.txt. You may also get a copy of the
 * license at <http://www.gnu.org/licenses/>.
 * 
 */
package edu.byu.ece.edif.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import edu.byu.ece.edif.util.merge.EdifMergeParser;

//////////////////////////////////////////////////////////////////////////
////EdifCellInstance
/**
 * Represents a specific instance of an EdifCell object. This object contains a
 * reference to the EdifCell that is instanced and a reference to the parent
 * EdifCell object that instanced this cell.
 * <p>
 * 
 * @version $Id:EdifCellInstance.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class EdifCellInstance extends NamedPropertyObject implements EdifOut {

    /**
     * Construct an Empty, named EdifCellInstance Object.
     * 
     * @param name The name of this EdifCellInstance Object
     * @throws InvalidEdifNameException
     */
    public EdifCellInstance(String name) throws InvalidEdifNameException {
        super(name);
    }

    /**
     * Construct an Empty, named EdifCellInstance Object.
     * 
     * @param name The Object that holds name information for this
     * EdifCellInstance Object
     */
    public EdifCellInstance(EdifNameable name) {
        super(name);
    }

    /**
     * Construct a typeless, named EdifCellInstace Object with the specified
     * parent.
     * 
     * @param name The name of this EdifCellInstance Object
     * @param parent The EdifCell Object that contains this EdifCellInstance
     * @throws InvalidEdifNameException
     */
    public EdifCellInstance(String name, EdifCell parent) throws InvalidEdifNameException {
        this(name);
        setParent(parent);
    }

    /**
     * Construct a typeless, named EdifCellInstace Object with the specified
     * parent.
     * 
     * @param name The Object that holds name information for this
     * EdifCellInstance Object
     * @param parent The EdifCell Object that contains this EdifCellInstace
     */
    public EdifCellInstance(EdifNameable name, EdifCell parent) {
        this(name);
        setParent(parent);
    }

    /**
     * Construct a typed, named EdifCellInstace Object with the specified
     * parent.
     * 
     * @param name The name of this EdifCellInstance Object
     * @param parent The EdifCell Object that contains this EdifCellInstace
     * @param type The type of this EdifCellInstance Object, or the EdifCell
     * Object that this EdifCellInstance is instancing
     * @throws InvalidEdifNameException
     */
    public EdifCellInstance(String name, EdifCell parent, EdifCell type) throws InvalidEdifNameException {
        this(name, parent);
        _cellType = type;
    }

    /**
     * Construct a typed, named EdifCellInstace Object with the specified
     * parent.
     * 
     * @param name The Object that holds name information for this
     * EdifCellInstance Object
     * @param parent The EdifCell Object that contains this EdifCellInstace
     * @param type The type of this EdifCellInstance Object, or the EdifCell
     * Object that this EdifCellInstance is instancing
     */
    public EdifCellInstance(EdifNameable name, EdifCell parent, EdifCell type) {
        this(name, parent);
        _cellType = type;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Return a deep copy of this EdifCellInstance using the passed-in EdifCell
     * as the parent. All new Objects are created using the 'new' statement,
     * except Strings.
     * 
     * @param parent The EdifCell that will contain the new EdifCellInstance
     * @return A new EdifCellInstance Object
     */
    public EdifCellInstance copy(EdifCell parent) {
        EdifNameable newName = getEdifNameable();
        EdifCellInstance eci = new EdifCellInstance(newName, parent, _cellType);
        eci.copyProperties(this);
        return eci;
    }

    /**
     * This method will determine the parent cell of an EdifCellInstance and
     * search through the nets of this parent cell. The EdifPortRefs of those
     * nets that connect to the given EdifCellInstance are returned in a
     * Collection.
     * 
     * @return A Collection of EdifPortRefs
     */
    public Collection<EdifPortRef> getAllEPRs() {
        Collection<EdifPortRef> returnRefs = new LinkedHashSet<EdifPortRef>();
        EdifCell parentCell = getParent();

        if (parentCell != null) {
            Collection<EdifNet> nets = parentCell.getNetList();
            for (EdifNet net : nets) {
                Collection<EdifPortRef> attachedPortRefs = net.getAttachedPortRefs(this);
                if (attachedPortRefs.size() > 0)
                    for (EdifPortRef epr : attachedPortRefs)
                        returnRefs.add(epr);
            }
        }
        return returnRefs;
    }

    /**
     * Return the cell that this EdifCellInstance object is referring to.
     * 
     * @return An EdifCell Object Referring to the type of this
     * EdifCellInstance, or the EdifCell that this EdifCellInstance is
     * instancing
     */
    public EdifCell getCellType() {
        return _cellType;
    }

    /**
     * This method will determine the parent cell of this instance and search
     * through the nets of this parent cell for that net that connects to the
     * EdifSingleBitPort specified as the parameter. This method will return
     * null if no net exists or if the EdifSingleBit port is not owned by the
     * cell type of this instance.
     * 
     * @param esbp
     * @return The EdifNet object that is connected to the given
     * EdifSingleBitPort. Returns null when there is no net connected to this
     * port or when the port is not owned by the cellType of this instance.
     */
    public EdifNet getEdifCellInstanceNet(EdifSingleBitPort esbp) {
        // Make sure esbp is owned by the cell type     
        if (!this.getCellType().getInterface().contains(esbp))
            return null;
        // ArrayList connectedNets = new ArrayList();
        EdifCell parentCell = getParent();
        for (EdifNet net : parentCell.getNetList()) {
            if (net.isAttached(this, esbp))
                return net;
        }
        return null;
    }

    /**
     * Return a Collection with a List object for each primitive instance within
     * the hierarchy of this cell instance.
     * 
     * @return A Collection of hierarchical primitives
     */
    public Collection<List<EdifCellInstance>> getHierarchicalPrimitiveList() {
        Collection<List<EdifCellInstance>> cellPrims = getCellType().getHierarchicalPrimitiveList();
        if (cellPrims == null) {
            // cell type is a primitive
            List<EdifCellInstance> newList = new ArrayList<EdifCellInstance>(1);
            newList.add(this);
            Collection<List<EdifCellInstance>> c = new ArrayList<List<EdifCellInstance>>(1);
            c.add(newList);
            return c;
        } else {
            // iterate over every list and add this
            for (List<EdifCellInstance> l : cellPrims) {
                l.add(0, this);
            }
            return cellPrims;
        }
    }

    public Map<EdifSingleBitPort, EdifNet> getInnerNets() {
        EdifCell parentCell = getParent();
        Map<EdifSingleBitPort, EdifNet> innerNets = new LinkedHashMap<EdifSingleBitPort, EdifNet>();

        if (parentCell != null) {
            innerNets = new LinkedHashMap<EdifSingleBitPort, EdifNet>(parentCell.getInterface().getTotalInterfaceBits());

            for (EdifNet net : parentCell.getNetList()) {
                for (EdifPortRef epr : net.getAttachedPortRefs(this)) {
                    EdifSingleBitPort key = epr.getSingleBitPort();
                    innerNets.put(key, key.getInnerNet());
                }
            }
        }
        return innerNets;
    }

    /**
     * This method will determine the parent cell of an EdifCellInstance and
     * search through the nets of this parent cell. The input EdifPortRefs of
     * those nets that connect to the given EdifCellInstance are returned in a
     * Collection.
     * 
     * @return A Collection of input EdifPortRefs
     */
    public Collection<EdifPortRef> getInputEPRs() {
        Collection<EdifPortRef> returnRefs = new LinkedHashSet<EdifPortRef>();
        EdifCell parentCell = getParent();

        if (parentCell != null) {
            Collection<EdifNet> nets = parentCell.getNetList();
            for (EdifNet net : nets) {
                Collection<EdifPortRef> attachedPortRefs = net.getAttachedPortRefs(this);
                if (attachedPortRefs.size() > 0)
                    for (EdifPortRef epr : attachedPortRefs)
                        if (epr.getPort().isInput())
                            returnRefs.add(epr);
            }
        }
        return returnRefs;
    }

    /**
     * @return A Map where the key is a EdifSingleBitPort of the instance and
     * the value is the EdifPortRef Object connected to single-bit port.
     */
    public Map<EdifSingleBitPort, EdifPortRef> getOuterEPRs() {
        EdifCell parentCell = getParent();
        Map<EdifSingleBitPort, EdifPortRef> outerNets = new LinkedHashMap<EdifSingleBitPort, EdifPortRef>();

        if (parentCell != null) {
            outerNets = new LinkedHashMap<EdifSingleBitPort, EdifPortRef>(parentCell.getInterface()
                    .getTotalInterfaceBits());

            for (EdifNet net : parentCell.getNetList()) {
                for (EdifPortRef epr : net.getAttachedPortRefs(this)) {
                    outerNets.put(epr.getSingleBitPort(), epr);
                }
            }
        }
        return outerNets;
    }

    /**
     * This method will determine the parent cell of an EdifCellInstance and
     * search through the nets of this parent cell. Those nets that connect to
     * the given EdifCellInstance are returned as a List. These nets are termed
     * "outer nets" because they are the "outside" nets that connect to a given
     * instance of a particular cell.
     * 
     * @return A Map where the key is a EdifSingleBitPort of the instance and
     * the value is the EdifNet Object connected to single-bit port.
     */
    public Map<EdifSingleBitPort, EdifNet> getOuterNets() {
        EdifCell parentCell = getParent();
        Map<EdifSingleBitPort, EdifNet> outerNets = new LinkedHashMap<EdifSingleBitPort, EdifNet>();

        if (parentCell != null) {
            outerNets = new LinkedHashMap<EdifSingleBitPort, EdifNet>(parentCell.getInterface().getTotalInterfaceBits());

            for (EdifNet net : parentCell.getNetList()) {
                for (EdifPortRef epr : net.getAttachedPortRefs(this)) {
                    outerNets.put(epr.getSingleBitPort(), net);
                }
            }
        }
        return outerNets;
    }

    /**
     * This method will determine the parent cell of an EdifCellInstance and
     * search through the nets of this parent cell. The output EdifPortRefs of
     * those nets that connect to the given EdifCellInstance are returned in a
     * Collection.
     * 
     * @return A Collection of output EdifPortRefs
     */
    public Collection<EdifPortRef> getOutputEPRs() {
        Collection<EdifPortRef> returnRefs = new LinkedHashSet<EdifPortRef>();
        EdifCell parentCell = getParent();

        if (parentCell != null) {
            Collection<EdifNet> nets = parentCell.getNetList();
            for (EdifNet net : nets) {
                Collection<EdifPortRef> attachedPortRefs = net.getAttachedPortRefs(this);
                if (attachedPortRefs.size() > 0)
                    for (EdifPortRef epr : attachedPortRefs)
                        if (epr.getPort().isOutput())
                            returnRefs.add(epr);
            }
        }
        return returnRefs;
    }

    /**
     * Return the parent cell that is referring to the cell that this
     * EdifCellInstance object is referring to.
     * 
     * @return The EdifCell Object that contains this EdifCellInstance
     */
    public EdifCell getParent() {
        return _parent;
    }

    /**
     * @return If any of the nets of the EdifCell type of the EdifCellInstance
     * contain an EdifPortRef that refers to the given EdifSingleBitPort, the
     * EdifPortRef is returned. Otherwise, null is returned.
     * @param port An EdifSingleBitPort on the EdifCellInstance to which the
     * desired EdifPortRef makes reference.
     */
    public EdifPortRef getPortRef(EdifSingleBitPort port) {
        EdifPortRef matchingPort = null;
        for (EdifNet net : getCellType().getNetList()) {
            for (EdifPortRef pr : net.getConnectedPortRefs()) {
                if (pr.getSingleBitPort() == port)
                    matchingPort = pr;
            }
        }
        return matchingPort;
    }

    /**
     * Return the type of this cell as a String.
     * 
     * @return A String representing what type of cell this is.
     */
    public String getType() {
        return _cellType.getName();
    }

    /**
     * Return true if the current instance matches within the passed-in
     * collection of EdifCellInstance Objects. Equality done by doing a deep
     * compare on the cell types of each instance, and the name of each
     * instance.
     * 
     * @param instanceList This EdifCellInstance Object will be compared against
     * all the elements in this passed-in Collection to see if a match exists
     * within the Collection
     */
    public boolean matchesWithin(Collection<EdifCellInstance> instanceList) {
        boolean result = true;
        if (instanceList != null)
            for (EdifCellInstance eci : instanceList) {
                // equality checking done by comparing both cell types
                if (!(NamedObjectCompare.equals(this, eci) && this.getCellType().equalsName(eci.getCellType())))
                    result = false;
            }
        return result;
    }

    /**
     * Modify the internal cell reference used by this EdifCellInstance.
     * 
     * @param newCellRef An EdifCell Object--The cell this EdifCellInstance
     * object will now refer to
     */
    public void modifyCellRef(EdifCell newCellRef) {
        modifyCellRef(newCellRef, true);
    }

    /**
     * Modify the internal cell reference used by this EdifCellInstance.
     * 
     * @param newCellRef An EdifCell Object--The cell this EdifCellInstance
     * object will now refer to
     * @param check If true, will check to see if the interfaces are equal, and
     * if it's ok to make the switch. For speed, and if it's known that the
     * match will succeed, use false
     */
    public void modifyCellRef(EdifCell newCellRef, boolean check) {
        // Begin by making sure that the cell interface of the new
        // cell reference is the same as the cell interface of the old
        // cell reference.
        if (check && !getCellType().equalsInterface(newCellRef)
                && !EdifMergeParser.matchingCellInterfaceBusExpansion(getCellType(), newCellRef))
            throw new EdifRuntimeException("Attempting to modify the type of instance " + getName() + ":" + getType()
                    + " with an incompatible type");

        _cellType = newCellRef;
    }

    /**
     * Writes the EDIF representation of this EdifCellInstance object to the
     * {@link EdifPrintWriter} passed as a parameter.
     * 
     * @param epw EdifPrintWriter to which EDIF will be written
     */
    public void toEdif(EdifPrintWriter epw) {

        epw.printIndent("(instance ");
        getEdifNameable().toEdif(epw);

        epw.println();

        epw.incrIndent();
        epw.printIndent("(viewRef ");
        if (getCellType().isPrimitive())
            epw.print("PRIM ");
        else if (getCellType().isLeafCell())
            epw.print("black_box ");
        else
            epw.print("netlist ");
        epw.print("(cellRef " + _cellType.getName());

        if (_cellType == null || _cellType.getLibrary() == null) {
            System.out.println(this);
            System.out.println("CELL TYPE: " + _cellType);
            System.out.println("PARENT: " + _parent);
            System.out.println("LIB: " + _cellType.getLibrary());
            System.out.println("PARENT LIB: " + _parent.getLibrary());
            epw.flush();

            //            EdifLibrary lib = this.getParent().getLibrary();
            //    		EdifLibraryManager elm = null;
            //    		if (lib != null) {
            //    			elm = lib.getLibraryManager();
            //    			lib = elm.getLibrary("VIRTEX");
            //    		}
            //    		if (lib == null) {
            //    			lib = new EdifLibrary("VIRTEX");
            //    		}    		
            //    		elm = lib.getLibraryManager();
            //    		lib.addCell(_cellType);
            //			_cellType.setLibrary(lib);
            //			this.getCellType().setLibrary(lib);
            //    		if (!lib.containsCellByName(this.getCellType().getName().toUpperCase())) {
            //    			elm.addCellDeep(this.getCellType(), lib);
            //    		}
        }
        if (_cellType.getLibrary() != _parent.getLibrary()) {
            epw.println(" (libraryRef " + _cellType.getLibrary().getName() + ")))");
        } else
            epw.println(" ))");

        PropertyList pl = getPropertyList();
        if (pl != null) {
            for (Property p : pl.values()) {
                p.toEdif(epw);
            }
        }

        epw.decrIndent();
        epw.printlnIndent(")");

    }

    /**
     * Return a String representation of the EdifCellInstance object. This
     * simply corresponds to the name of the EdifCellInstance and its type.
     * 
     * @return String description of the EdifCell object
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getEdifNameable());
        sb.append("(");
        sb.append(_cellType.getEdifNameable());
        sb.append(")");
        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////
    ////                   package-private methods                 ////
    /**
     * Sets the parent cell that is referring to the cell this EdifCellInstance
     * object is referring to.
     * <p>
     * TODO: It would be nice to remove this method. Is there a way we can make
     * the parent atomic and disallow changing at runtime? If not, we need to
     * comment why here as part of the method.
     * 
     * @param p An EdifCell Object--The parent referring to the cell of this
     * EdifCellInstance.
     */
    // package scope
    void setParent(EdifCell p) {
        _parent = p;
    }

    ///////////////////////////////////////////////////////////////////
    // Private methods

    /**
     * This EdifCell reference refers to the type of EdifCell that is instanced.
     */
    private EdifCell _cellType;

    /**
     * This EdifCell reference refers to the the parent EdifCell object that
     * instanced the cell.
     */
    private EdifCell _parent;

}
