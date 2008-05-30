/*
 * Represents an EDIF cell definition.
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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.byu.ece.edif.test.regression.EdifDifference;

//////////////////////////////////////////////////////////////////////////
////EdifCell
/**
 * Represents an EDIF cell definition. This class is not tied to any target
 * language or implementation (i.e. VHDL, JHDL, etc).
 * <p>
 * This implementation of EdifCell contains Lists of all {@link
 * EdifCellInstance}, {@link EdifNet}, and {@link EdifPort} objects associated
 * with this EdifCell object. Additionally, this class contains a reference to
 * the {@link EdifLibrary} object which owns the EdifCell object.
 * <p>
 * This class may allocate a large amount of memory to represent a given cell.
 * To help control the memory allocation, this class implements the
 * {@link Trimable} interface for trimming unneeded memory.
 * <p>
 * Sample EDIF code for an EdifCell:
 * 
 * <pre>
 *  (cell graycount (cellType GENERIC)
 * 	 (view netlist (viewType NETLIST)
 * 		 (interface
 * 			 (port G_5322 (direction INPUT))
 * 			 ...
 * 		 )
 * 		 (contents
 * 		 	(instance (rename count_0 &quot;count[0]&quot;) (viewRef PRIM (cellRef FDCE (libraryRef UNILIB)))
 * 		 		...
 * 		 		(net (rename write_addrgray_pre_0 &quot;WRITE_ADDRGRAY_PRE(0)&quot;) (joined
 * 		 			(portRef (member write_addrgray_pre 3))
 * 		 			(portRef Q (instanceRef count_gray_0))
 * 		 			)
 * 		 		)
 * 		 		...
 * 		 	)
 * 		 )
 * 	 )
 *  )
 * </pre>
 * 
 * <p>
 * <B>Definitions</b>
 * <p>
 * There are a number of terms that are used to describe the various components
 * of an EdifCell. This list serves as the definition of these terms to insure
 * consistent and understandable usage.
 * <ul>
 * <li> <b><a name="outerport">Outer Port</a></b> Refers to a top-level
 * EdifPort object of a EdifCell.
 * <li> <b><a name="innerport">Inner Port</a></b> Refers to a EdifPort
 * associated with one of the EdifCellInstances instanced within the EdifCell.
 * </ul>
 * <p>
 * <B>To Do:</b>
 * <p>
 * <ul>
 * <li>Highest Priority
 * <ul>
 * </ul>
 * <li>Moderate Priority
 * <ul>
 * <li> Keep list in EdifCell of instances of a particular cell
 * <li> Keep reference to parent? - EdifPort? EdifPortRef?
 * <li> Methods for checking the consistency of an EdifCell (will learn over
 * time)
 * <ul>
 * <li> <strike>disconnectedChildren() (return a list of cells disconnected)</strike> -
 * done
 * <li> <strike>danglingNets()</strike> - done
 * <li> <strike>unconnectedPorts()</strike> - done
 * </ul>
 * <li> Methods for "changing" internal structure of cell (ports,children,etc.)
 * <li> More control over toString(). Also, make default toString() print a
 * simple name rather than everything.
 * <li> Deal with character arrays instead of Strings (save space)
 * <li> Provide a way of making the isPrimitive atomic.
 * <li> In EdifCell#DeleteSubCell, delete all references to nets? Change the
 * name of this method to <code>removeEdifCellInstance</code>? - added a flag
 * to remove references in EdifNets to the deleted EdifCellInstance
 * </ul>
 * <li>Low Priority
 * <ul>
 * <li> Analyze the memory consumption. Can be improved.
 * <li> Update the UML diagram
 * </ul>
 * <li><b>TMR</b>
 * <ul>
 * <li> should we have a separate list for input ports and output ports, rather
 * than _portlist? Maybe this wouldn't work for inout ports. Would it make sense
 * to have 3 separate lists (in,out,inout)? This would speed up some of the TMR
 * algorithm.
 * </ul>
 * </ul>
 * <B>Name Space:</b>
 * <p>
 * <ul>
 * <li> EdifNet objects
 * <li> EdifCellInstance objects
 * <li> EdifPort objects
 * </ul>
 * 
 * @version $Id:EdifCell.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 * @see EdifPort
 * @see EdifCellInstance
 * @see EdifNet
 * @see EdifLibrary
 */
public class EdifCell extends NamedPropertyObject implements EdifOut, Trimable {
    public static final long serialVersionUID = 55;

    public EdifCell(EdifLibrary lib, String name) throws EdifNameConflictException, InvalidEdifNameException {
        super(name);
        _interface = new EdifCellInterface(this);
        lib.addCell(this);
    }

    public EdifCell(EdifLibrary lib, EdifNameable name) throws EdifNameConflictException {
        super(name);
        _interface = new EdifCellInterface(this);
        lib.addCell(this);
    }

    public EdifCell(EdifLibrary lib, String name, EdifCellInterface iface) throws EdifNameConflictException,
            InvalidEdifNameException {
        super(name);
        _interface = iface.copy(this);
        lib.addCell(this);
    }

    public EdifCell(EdifLibrary lib, String name, boolean isPrimitive) throws EdifNameConflictException,
            InvalidEdifNameException {
        this(lib, name);
        _isPrimitive = isPrimitive;
    }

    public EdifCell(EdifLibrary lib, EdifNameable name, boolean isPrimitive) throws EdifNameConflictException {
        this(lib, name);
        _isPrimitive = isPrimitive;
    }

    public EdifCell(EdifLibrary lib, EdifCell cell) throws EdifNameConflictException, InvalidEdifNameException {
        this(lib, cell, cell.getName());
    }

    /**
     * @deprecated Only called by the constructor below.
     */
    public EdifCell(EdifLibrary lib, EdifCell cell, String name) throws EdifNameConflictException,
            InvalidEdifNameException {
        this(lib, name);
        // Set the primitive status of cell 
        _isPrimitive = cell.isPrimitive();

        copyCellInternals(cell);
    }

    /**
     * @deprecated This method is only used by the old HalfLatchEdifCell
     * constructor and the old lut replacement. This method should be
     * removed once these old methods and classes are removed.
     */
    public EdifCell(EdifLibrary lib, EdifCell cell, EdifNameable name) throws EdifNameConflictException,
            InvalidEdifNameException {
        this(lib, name);
        // Set the primitive status of cell 
        _isPrimitive = cell.isPrimitive();

        copyCellInternals(cell);
    }

    /**
     * Add an {@link EdifNet} object to the EdifCell. This sets the parent of
     * the EdifNet object to the current EdifCell object.
     * 
     * @param net EdifNet object to add to the EdifCell
     * @throws EdifNameConflictException
     */
    public boolean addNet(EdifNet net) throws EdifNameConflictException {
        if (_netList == null)
            _netList = new EdifNameSpaceMap<EdifNet>(BasicEdifNameClashPolicy.anyButOldWithNewClashPolicy);

        if (_netList.contains(net)) {
            System.err.println("Adding same net twice to same cell, addNet aborted");
            return false;
        }

        _netList.addElement(net);
        net.setParent(this);

        return true;
    }

    /**
     * Adds a new port to the EdifCellInterface.
     * 
     * @param name The name of the new port to add
     * @param width The width of the new port to add
     * @param direction The direction of the new port to add
     * @throws EdifNameConflictException
     */
    public EdifPort addPort(EdifNameable name, int width, int direction) throws EdifNameConflictException {
        return _interface.addPort(name, width, direction);
    }

    /**
     * Adds a new port to the EdifCellInterface.
     * 
     * @param name The name of the new port to add
     * @param width The width of the new port to add
     * @param direction The direction of the new port to add
     * @throws InvalidEdifNameException
     * @throws EdifNameConflictException
     */
    public EdifPort addPort(String name, int width, int direction) throws InvalidEdifNameException,
            EdifNameConflictException {
        return _interface.addPort(name, width, direction);
    }

    public EdifPort addPortUniqueName(EdifNameable name, int width, int direction) {
        return _interface.addPortUniqueName(name, width, direction);
    }

    public EdifPort addPortUniqueName(String name, int width, int direction) throws InvalidEdifNameException {
        return _interface.addPortUniqueName(name, width, direction);
    }

    /**
     * Add an {@link EdifCellInstance} object to the EdifCell. The parent of the
     * EdifCellInstance is set to the current EdifCell object.
     * <p>
     * TODO: we need to give the user the option of not allowing renaming. TODO:
     * create a method addSubCell (EdifCell, name) that creates the
     * edifcellinstance automatically for the user.
     * 
     * @param cellInstance EdifCellInstance object to add to the EdifCell
     * @throws EdifNameConflictException
     */
    public boolean addSubCell(EdifCellInstance cellInstance) throws EdifNameConflictException {
        if (_cellInstanceList == null)
            _cellInstanceList = new EdifNameSpaceMap<EdifCellInstance>(
                    BasicEdifNameClashPolicy.anyButOldWithNewClashPolicy);
        if (_cellInstanceList.contains(cellInstance)) {
            System.err.println("Adding same instance twice to same cell, addSubCell aborted");
            return false;
        }

        _cellInstanceList.addElement(cellInstance);
        cellInstance.setParent(this);
        return true;
    }

    public EdifNameable addSubCellUniqueName(EdifCellInstance cellInstance) {
        if (_cellInstanceList == null)
            _cellInstanceList = new EdifNameSpaceMap<EdifCellInstance>(
                    BasicEdifNameClashPolicy.anyButOldWithNewClashPolicy);
        if (_cellInstanceList.nameClash(cellInstance)) {
            EdifNameable newName = _cellInstanceList.getUniqueNameable(cellInstance.getEdifNameable());
            cellInstance.rename(newName);
        }
        cellInstance.setParent(this);
        try {
            _cellInstanceList.addElement(cellInstance);
        } catch (EdifNameConflictException e) {
            // will never get here because a unique name has been used
            e.toRuntime();
        }
        return cellInstance.getEdifNameable();
    }

    /**
     * Returns an Iterator of this cell's cellInstances.
     * 
     * @return An Iterator Object of all the EdifCellInstances contained within
     * this EdifCell Object
     */
    public Iterator<EdifCellInstance> cellInstanceIterator() {
        if (_cellInstanceList != null)
            return _cellInstanceList.values().iterator();
        return (new ArrayList<EdifCellInstance>(0)).iterator();
    }

    /**
     * Checks to see if the passed-in EdifCellInstance is already contained
     * within this EdifCell. Equality is done using ==.
     * 
     * @param contain The EdifCellInstance to compare against the
     * EdifCellInstance Objects contained within this EdifCell Object to check
     * to see if it already exists in this EdifCell
     * @return True if the passed-in EdifCellInstance is already contained
     * within this EdifCell Object
     */
    public boolean contains(EdifCellInstance contain) {
        for (Iterator i = getSubCellList().iterator(); i.hasNext();) {
            EdifCellInstance eci = (EdifCellInstance) i.next();

            if (eci == contain)
                return true;
        }
        return false;
    }

    /**
     * Checks to see if the passed-in EdifPort is already contained within this
     * EdifCell. Equality is done using ==.
     * 
     * @param contain The EdifPort to compare against the EdifPort Objects
     * contained within this EdifCell Object to check to see if it already
     * exists in this EdifCell
     * @return True if the passed-in EdifPort is already contained within this
     * EdifCell Object
     */
    public boolean contains(EdifPort contain) {
        return _interface.contains(contain);
    }

    /**
     * Checks to see if the passed-in EdifNet is already contained within this
     * EdifCell. Equality is done using ==.
     * 
     * @param contain The EdifNet to compare against the EdifNet Objects
     * contained within this EdifCell Object to check to see if it already
     * exists in this EdifCell
     * @return True if the passed-in EdifNet is already contained within this
     * EdifCell Object
     */
    public boolean contains(EdifNet contain) {
        for (EdifNet net : getNetList()) {

            if (net == contain)
                return true;
        }
        return false;
    }

    /**
     * Delete the given net from this EdifCell.
     * 
     * @param delete The EdifNet to delete
     * @return True if it was deleted, false otherwise
     */
    public boolean deleteNet(EdifNet delete) {
        if (_netList == null)
            return false;
        return (_netList.remove(delete));
    }

    /**
     * Delete the given port from this EdifCell.
     * 
     * @param delete The EdifPort to delete
     */
    public void deletePort(EdifPort delete) {
        _interface.deletePort(delete);
    }

    /**
     * Remove an EdifCellInstance object from the current EdifCell object.
     * Return <code>true</code> if the instance was found and successfully
     * removed, otherwise <code>false</code>.
     * 
     * @param cellInstance EdifCellInstance object to remove from the current
     * EdifCell object.
     * @param removeRefsInNets Deletes all references to this instance in all
     * the nets within this EdifCell, if true
     * @return boolean indicating whether the EdifCellInstance object was
     * successfully found and removed
     */
    public boolean deleteSubCell(EdifCellInstance cellInstance, boolean removeRefsInNets) {
        if (_cellInstanceList == null)
            return false;

        if (removeRefsInNets) {

            for (EdifNet net : getNetList()) {
                ArrayList<EdifPortRef> remove = new ArrayList<EdifPortRef>(2);

                for (EdifPortRef epr : net.getConnectedPortRefs()) {
                    if (epr.getCellInstance() == cellInstance)
                        remove.add(epr);
                }
                net.getConnectedPortRefs().removeAll(remove);
            }
        }

        return (_cellInstanceList.remove(cellInstance.getName().toLowerCase()) != null);
    }

    public boolean deleteSubCell(EdifCellInstance cellInstance) {
        return deleteSubCell(cellInstance, false);
    }

    /**
     * Two EdifCell objects are considered edifEqual if
     * <ul>
     * <li> They are both primitive or are both non-privative </li>
     * <li> They contain EdifCellInterface objects which are edifEqual </li>
     * <li> Each of the EdifNet objects within the EdifCell are edifEqual </li>
     * <li> Each of the EdifCellInstance objects within the EdifCell are
     * edifEqual </li>
     * </ul>
     * 
     * @param o Comparison EdifCell object
     * @return true of the two EdifCell objects are edifEqual
     */
    public boolean edifEquals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EdifCell))
            return false;

        EdifCell that = (EdifCell) o;

        return this.isPrimitive() == that.isPrimitive()
                && (this.getName() == null ? that.getName() == null : this.getName().equals(that.getName()))
                && (this.getInterface() == null ? that.getInterface() == null : this.getInterface().edifEquals(
                        that.getInterface()))
                && edifEqualLibraryName(that) // TODO: Why do we check the library name?
                && (this.getPropertyList() == null ? that.getPropertyList() == null : this.getPropertyList().equals(
                        that.getPropertyList())) && edifEqualCellInstanceList(that) && edifEqualNetList(that);

    }

    public List<EdifDifference> edifDiff(Object o) {
        return edifDiff(o, true);
    }

    public List<EdifDifference> edifDiff(Object o, boolean verbose) {
        List<EdifDifference> differences = new ArrayList<EdifDifference>();
        if (this == o)
            return differences; // no differences!
        if (!(o instanceof EdifCell)) {
            differences.add(new EdifDifference(this.toString(), o.toString()));
            return differences;
        }

        EdifCell that = (EdifCell) o;

        if (!this.getName().equals(that.getName()))
            differences.add(new EdifDifference(this.getName(), that.getName()));

        if (!verbose)
            return differences;

        if (this.isPrimitive() != that.isPrimitive()) {
            differences.add(new EdifDifference("isPrimative: " + ((Boolean) this.isPrimitive()).toString(),
                    "isPrimitive: " + ((Boolean) that.isPrimitive()).toString()));
        }
        if (!(this.getInterface() == null ? that.getInterface() == null : this.getInterface().edifEquals(
                that.getInterface()))) {
            differences.add(new EdifDifference(this.getInterface(), that.getInterface()));
            //differences.add(this.getInterface().edifDiff(that.getInterface()));
        }
        if (!edifEqualLibraryName(that))
            differences.add(new EdifDifference(this.getLibrary(), that.getLibrary())); // TODO: add a more intelligent string here later.
        if (!(this.getPropertyList() == null ? that.getPropertyList() == null : this.getPropertyList().equals(
                that.getPropertyList())))
            differences.add(new EdifDifference(this.getPropertyList(), that.getPropertyList()));
        if (!edifEqualCellInstanceList(that)) {
            differences.add(new EdifDifference(this.getName() + ": Cell instance list differs", that.getName()
                    + ": Cell instance list differs"));
            differences.addAll(this.edifDiffCellInstanceList(that));
        }
        if (!edifEqualNetList(that)) {
            differences.add(new EdifDifference(this.getName() + ": Net list differs", that.getName()
                    + ": Net list differs"));
            differences.addAll(this.edifDiffNetList(that));
        }

        return differences;
    }

    /**
     * Determine if the given EdifCell has the same interface as the current
     * EdifCell. This is an overloaded method that defaults to not comparing by
     * possible bus.
     * 
     * @see EdifCellInterface#equals(EdifCellInterface)
     * @param cell The cell used for the interface comparison
     * @return True if the interfaces are equal
     */
    public boolean equalsInterface(EdifCell cell) {
        return _interface.equals(cell.getInterface());
    }

    /**
     * This method will determine if the name of this EdifCell is the same as
     * the name of the passed in EdifCell.
     * 
     * @param cell Cell to compare
     * @return Returns true if the names are the same.
     */
    public boolean equalsName(EdifCell cell) {
        // check equality of name
        return NamedObjectCompare.equals(this, cell);
    }

    /**
     * Return a Collection of the EdifCellInstance objects in this EdifCell
     * <p>
     * TODO: Return a new object rather than the actual _cellInstanceList
     * 
     * @return
     */
    public Collection<EdifCellInstance> getCellInstanceList() {
        if (_cellInstanceList == null)
            return null;
        else
            return _cellInstanceList.values();
    }

    public Collection<EdifCellInstance> getSafeCellInstanceList() {
        if (_cellInstanceList == null)
            return new ArrayList<EdifCellInstance>();
        else
            return _cellInstanceList.values();
    }

    /**
     * This function returns all EdifCellInstances within this EdifCell object
     * that reference a given cell in this library (i.e. the same type as the
     * given cell). If no EdifCellInstances of this type are found, this method
     * will return null.
     * 
     * @param type The EdifCell Object to find references to within this
     * EdifCell
     * @return A Collection of EdifCellInstances contained within this EdifCell
     * that reference the passed-in EdifCell
     */
    public Collection<EdifCellInstance> findCellInstancesOf(EdifCell type) {
        Collection<EdifCellInstance> cellInstances = new ArrayList<EdifCellInstance>();
        if (getSubCellList() != null) {

            for (EdifCellInstance eci : getSubCellList()) {

                if (eci.getCellType() != null)
                    if (type == eci.getCellType())
                        cellInstances.add(eci);
            }
        }

        if (cellInstances.size() == 0)
            return null;
        return cellInstances;
    }

    /**
     * Return the EdifCellInstance contained within this EdifCell Object that
     * matches the passed-in name.
     * 
     * @param name The String used to match the returned EdifCellInstance
     * @return The EdifCellInstance Object whose name matches the passed-in name
     */
    public EdifCellInstance getCellInstance(String name) {
        if (_cellInstanceList == null)
            return null;
        return (EdifCellInstance) _cellInstanceList.get(name);
    }

    /**
     * Dangling net is not connected to any outputs. Outer ports labeled IN and
     * inner ports labeled OUT are inputs. Outer ports labeled OUT and inner
     * ports labeled IN are outputs. Each net must have at least one output and
     * one input.
     * <p>
     * Note: By default (false and false gets passed to this method) this method
     * will only return nets with only inputs or nets with only outputs. Also,
     * if inputNoOutput is true then the value of outputsOnly gets over-ridden.
     * 
     * @param inputNoOutput Only returns nets with input(s) and no outputs, if
     * true
     * @param outputsOnly If true, this method will only return nets with
     * outputs and no inputs
     * @return A Collection of EdifNet Objects that have no outputs (they are
     * dangling)
     */
    public Collection getDanglingNets(boolean inputNoOutput, boolean outputsOnly) {
        Collection dang = new ArrayList();
        for (Iterator i = netListIterator(); i.hasNext();) {
            EdifNet net = (EdifNet) i.next();

            boolean hasOutput = false;
            boolean hasInput = false;
            for (EdifPortRef epr : net.getConnectedPortRefs()) {

                if (epr.getPort().isOutput() && epr.getCellInstance() == null || epr.getPort().isInput()
                        && epr.getCellInstance() != null) {
                    hasOutput = true;
                } else if (epr.getPort().isInput() && epr.getCellInstance() == null || epr.getPort().isOutput()
                        && epr.getCellInstance() != null) {
                    hasInput = true;
                }
            }
            if (inputNoOutput) {
                if (hasInput && !hasOutput)
                    dang.add(net);
            } else if (outputsOnly) {
                if (!hasInput)
                    dang.add(net);
            } else if (!hasOutput || !hasInput)
                dang.add(net);
        }

        if (dang.size() == 0)
            return null;
        return dang;
    }

    /**
     * Dangling net is not connected to any outputs. Outer ports labeled IN and
     * inner ports labeled OUT are inputs. Outer ports labeled OUT and inner
     * ports labeled IN are outputs. Each net must have at least one output and
     * one input.
     * <p>
     * Note: By default this method will only return nets with only inputs or
     * nets with only outputs.
     * 
     * @return A Collection of EdifNet Objects that have no outputs (they are
     * dangling)
     * @see #getDanglingNets(boolean,boolean)
     */
    public Collection getDanglingNets() {
        return getDanglingNets(false, false);
    }

    /**
     * Check the equality of two EdifCell objects. Two EdifCell objects match if
     * their names are the same and their interfaces are the same. If the
     * equalsStructure flag is set, this method will also check the equality of
     * the cell internal structure.
     * 
     * @param cell EdifCell object to compare against the current EdifCell
     * object
     * @param equalsStructure If this parameter is true, check the structure of
     * the two EdifCell objects as part of the equality condition.
     * @param equalsInterfaceOnly If true, the equality comparison will be made
     * by interface only, excluding name and structure
     * @param comparePossibleBus If true, the equalsInterface will look for a
     * possible bus match, meaning if one interface has 32 ports with the same
     * base name that correspond to a bus and the interface has one port with 32
     * members that has the same name as the 32 port's base names and they all
     * have the same direction, etc then this will count as a match
     * @param equalsProperties If true, the two cells property lists will also
     * be compared for equality
     * @param ignoreBusDirection If true, the matching algorithm will ignore the
     * direction of the passed-in ports
     * @return True if this EdifCell object and the passed-in one are equal
     */

    //    public boolean equals(EdifCell cell, boolean equalsInterfaceOnly, boolean comparePossibleBus) {
    //
    //        boolean ignoreBusDirection = true;
    //
    //        if (equalsInterfaceOnly)
    //            return equalsInterface(cell, comparePossibleBus, ignoreBusDirection); // check equality of name 
    //        if (!NamedObjectCompare.equals(this, cell))
    //            return false;
    //
    //        if (!equalsInterface(cell, comparePossibleBus, ignoreBusDirection))
    //            return false;
    //
    //        return true;
    //    }
    /**
     * Return a Collection with a List object for each primitive instance within
     * the hierarchy of this cell. Leaf cells will return a null Collection.
     * 
     * @return A Collection of List Objects of EdifCellInstance Objects that are
     * in order of hierarchy down to primitives
     */
    public Collection getHierarchicalPrimitiveList() {
        // exit out of terminal case
        if (isLeafCell())
            return null;
        // 0. Create Collection
        Collection cellPrimitives = new ArrayList();

        // 1. Create a Map between an EdifCell and a Collection. The
        // key to the map is a EdifCell and the Value is a Collection
        // of Lists where each List is the hierarchy list.
        Map cellListMap = new LinkedHashMap();

        // 2. Iterate over each EdifCellInstance in the cell. 
        //System.out.println("Begin "+getName());
        for (Iterator i = cellInstanceIterator(); i.hasNext();) {
            EdifCellInstance inst = (EdifCellInstance) i.next();
            EdifCell instType = inst.getCellType();

            //System.out.println(inst.getName()+"-"instType.getName());
            // Obtain the list of hierarchical primitives for each
            // cell instance.
            //System.out.println("\tSub "+inst.getName()+"("+inst.getType()+")");
            Collection cellPrims = null;
            if (cellListMap.containsKey(instType)) {
                cellPrims = (Collection) cellListMap.get(instType);
            } else {
                cellPrims = instType.getHierarchicalPrimitiveList();
                if (cellPrims != null) {
                    //System.out.println(inst.getName()+"-"+inst.getType());
                    cellListMap.put(instType, cellPrims);
                }
            }

            if (cellPrims == null) {
                // cell instance is a primitive. Create a new List
                // with a single element (i.e. the primitive).
                List newPrim = new ArrayList(1);
                newPrim.add(inst);
                // add single member list to Collection of primitives.
                cellPrimitives.add(newPrim);
            } else {
                // cell instance is not a primitive. Get each List
                // associated with this primitive and append the cell
                // instance to the list.
                //System.out.println(inst.getName()+"*"+inst.getType());
                for (Iterator j = cellPrims.iterator(); j.hasNext();) {
                    List prim = (List) j.next();
                    // Copy list
                    List primCopy = new ArrayList(prim.size() + 1);
                    primCopy.addAll(prim);
                    // put inst at top of primitive list
                    primCopy.add(0, inst);
                    // add list to list of primitives
                    cellPrimitives.add(primCopy);
                }
            }
        }
        return cellPrimitives;
    }

    /**
     * Return a Collection of all EdifCells instanced by the EdifCellInstances
     * contained within this EdifCell Object.
     * 
     * @return A Collection of all the different EdifCell "types" instanced as
     * sub-cells within the current EdifCell
     */
    public Collection getInnerCells() {
        Set set = new LinkedHashSet();
        for (Iterator i = _cellInstanceList.values().iterator(); i.hasNext();) {
            EdifCellInstance eci = (EdifCellInstance) i.next();
            set.add(eci.getCellType());
        }
        return set;
    }

    /**
     * @return A Collection object containing all input ports
     */
    public Collection getInputPorts() {
        return _interface.getInputPorts();
    }

    /**
     * Return the EdifCellInstance Object that matches the passed-in name, or
     * null if not found.
     * 
     * @param name The name of the EdifCellInstance to return
     * @return The EdifCellInstance Object that matches the passed-in name or
     * null
     */
    public EdifCellInstance getInstance(String name) {
        if (_cellInstanceList == null)
            return null;
        else
            return (EdifCellInstance) _cellInstanceList.get(name);
    }

    /**
     * Return all unique EdifCell objects that are instanced within this
     * EdifCell object. This method will iterate through all of the
     * EdifCellInstances and collect each unique EdifCell object that is
     * instanced.
     * 
     * @return A Collection of EdifCell Objects
     */
    public Collection<EdifCell> getInstancedCellTypes() {
        if (_cellInstanceList == null)
            return null;

        Collection<EdifCell> cellTypes = new ArrayList<EdifCell>();

        for (EdifCellInstance eci : _cellInstanceList) {
            EdifCell cell = eci.getCellType();
            if (!cellTypes.contains(cell))
                cellTypes.add(cell);
        }
        if (cellTypes.size() == 0)
            return null;
        return cellTypes;
    }

    /**
     * This method will return the EdifNet that is connected to the given port
     * on the given instance.
     * 
     * @param instance The EdifCellInstance Object that one of the returned
     * EdifNet Object's EdifPortRef Objects connects to
     * @param port The EdifPort Object that one of the returned EdifNet Object's
     * EdifPortRef Objects refers to
     * @return An EdifNet Object that has an EdifPortRef Object that refers to
     * the passed-in EdifPort Object, and connects to the passed-in
     * EdifCellInstance Object
     */
    public EdifNet getInstancePortNet(EdifCellInstance instance, EdifPort port) {
        for (Iterator i = netListIterator(); i.hasNext();) {
            EdifNet net = (EdifNet) i.next();
            if (net.isAttached(instance, port))
                return net;
        }
        return null;
    }

    /**
     * Return the EdifCellInterface of this EdifCell.
     * 
     * @return An EdifCellInterface Object that defines the port interface of
     * this EdifCell
     */
    public EdifCellInterface getInterface() {
        return _interface;
    }

    /**
     * Return the {@link EdifLibrary} object to which the current EdifCell
     * object belongs.
     * 
     * @return EdifLibrary object to which the current EdifCell object belongs
     */
    public EdifLibrary getLibrary() {
        return _library;
    }

    /**
     * Return the EdifPort object which defines a port on the current EdifCell
     * object and corresponds to the given name. If no such EdifPort is found, a
     * null object is returned.
     * 
     * @param port The EdifPort to find a match for within this
     * EdifCellInterface
     * @return The EdifPort corresponding to the passed-in port
     */
    public EdifPort getMatchingPort(EdifPort port) {
        return _interface.getMatchingPort(port);
    }

    /**
     * Return the net that maps to the given name.
     * 
     * @param name The key name of the EdifNet to retrieve
     * @return An EdifNet Object mapped to the given name
     */
    public EdifNet getNet(String name) {
        if (_netList == null)
            return null;
        return (EdifNet) _netList.get(name);
    }

    /**
     * Return a List containing all of the EdifNet objects which this EdifCell
     * object currently owns.
     * 
     * @return A Collection containing the EdifNet objects owned by the current
     * EdifCell object
     */
    public Collection<EdifNet> getNetList() {
        if (_netList != null)
            return _netList.values();
        return null;
    }

    /**
     * @return a Collection Object of the output ports
     */
    public Collection<EdifPort> getOutputPorts() {
        return _interface.getOutputPorts();
    }

    /**
     * Return the EdifPort object which defines a port on the current EdifCell
     * object and corresponds to the given name. If no such EdifPort is found, a
     * null object is returned.
     * 
     * @param name A string indicating the name of the EdifPort object desired
     * to be found
     * @return The EdifPort corresponding to the given name
     */
    public EdifPort getPort(String name) {
        return _interface.getPort(name);
    }

    /**
     * Return a Collection containing all of the EdifPort objects currently
     * associated with this EdifCell object.
     * 
     * @return Collection containing the EdifPort objects associated with the
     * current EdifCell object
     */
    public Collection<EdifPort> getPortList() {
        return _interface.getPortList();
    }

    /**
     * @return a collection of <em>all</em> EdifPortRef objects owned by this
     * cell. This method will iterate over each Net in the Cell and include all
     * EdifPortRef objects owned by each Net.
     * @see EdifNet#getPortRefIterator
     */
    public Collection getPortRefs() {
        Collection portRefs = new ArrayList();
        if (getNetList() != null) {
            for (Iterator i = getNetList().iterator(); i.hasNext();) {
                EdifNet net = (EdifNet) i.next();
                for (EdifPortRef epr : net.getConnectedPortRefs()) {
                    portRefs.add(epr);
                }
            }
        }
        return portRefs;
    }

    /**
     * Return a sorted Collection containing all of the EdifNet objects
     * currently by this EdifCell object. The objects in the Collection are
     * sorted by their names.
     * 
     * @return Sorted Collection containing the EdifNet objects owned by the
     * current EdifCell object
     */
    public Collection getSortedNetList() {
        if (_netList == null)
            return null;
        ArrayList sort = new ArrayList(_netList.values());
        Collections.sort(sort, new NamedObjectCompare());
        return sort;
    }

    /**
     * Return a sorted Collection containing all of the EdifPort objects
     * currently associated with this EdifCell object. The objects in the
     * Collection are sorted by their names.
     * 
     * @return Sorted Collection containing the EdifPort objects associated with
     * the current EdifCell object
     */
    public Collection getSortedPortList() {
        return _interface.getSortedPortList();
    }

    /**
     * Return a TreeMap of all {@link Property} objects corresponding to
     * properties of the current EdifCell object.
     * 
     * @return TreeMap containing all properties of the EdifCell object in the
     * form of Property objects
     */
    public TreeMap getSortedPropertyList() {
        if (getPropertyList() == null)
            return null;
        return (new TreeMap(getPropertyList()));
    }

    /**
     * Return a Collection of EdifCellInstance objects owned and instantiated by
     * the current EdifCell object. The EdifCellInstance objects are sorted
     * based on their names.
     * 
     * @return sorted Collection containing all EdifCellInstance objects owned
     * by this EdifCell
     * @see #getSubCellList()
     */
    public Collection getSortedSubCellList() {
        if (_cellInstanceList == null)
            return new ArrayList(0);
        ArrayList sort = new ArrayList(_cellInstanceList.values());
        Collections.sort(sort, new NamedObjectCompare());
        return sort;
    }

    /**
     * Return a Collection of EdifCellInstance objects owned and instantiated by
     * the current EdifCell object.
     * 
     * @return A Collection containing all EdifCellInstance objects owned by
     * this EdifCell. If this EdifCell contains no EdifCellInstances, returns an
     * empty (non-null) Collection.
     * @see #getSortedSubCellList()
     */
    public Collection<EdifCellInstance> getSubCellList() {
        if (_cellInstanceList != null)
            return _cellInstanceList.values();
        return new ArrayList(0);
    }

    /**
     * Return a Collection of EdifCellInstance objects that are not connected to
     * any EdifNet in the Cell.
     */
    public Collection getUnconnectedInstances() {
        Collection unconnectedInstances = new ArrayList();

        Collection portRefs = getPortRefs();

        for (Iterator i = getSubCellList().iterator(); i.hasNext();) {
            EdifCellInstance eci = (EdifCellInstance) i.next();
            EdifCellInstance eprInst = null;
            for (Iterator j = portRefs.iterator(); j.hasNext() && eci != eprInst;) {
                EdifPortRef epr = (EdifPortRef) j.next();
                eprInst = epr.getCellInstance();
            }
            if (eprInst != eci)
                unconnectedInstances.add(eci);
        }
        return unconnectedInstances;
    }

    public Map<EdifCellInstance, Collection<EdifPort>> getUnconnectedInnerPorts() {
        Map<EdifCellInstance, Collection<EdifPort>> cellInstToUnconPorts = new LinkedHashMap<EdifCellInstance, Collection<EdifPort>>();

        for (Iterator<EdifCellInstance> c = cellInstanceIterator(); c.hasNext();) {
            EdifCellInstance eci = c.next();
            Collection<EdifPort> uncon = new ArrayList<EdifPort>();

            for (EdifPort port : eci.getCellType().getPortList()) {
                boolean connected = false;

                for (Iterator n = netListIterator(); n.hasNext();) {
                    EdifNet net = (EdifNet) n.next();

                    for (EdifPortRef epr : net.getConnectedPortRefs()) {
                        if (epr.getPort() == port && epr.getCellInstance() == eci) {
                            connected = true;
                            break;
                        }
                    }
                    if (connected)
                        break;
                }
                if (!connected)
                    uncon.add(port);
            }
            if (uncon.size() != 0)
                cellInstToUnconPorts.put(eci, uncon);
        }
        if (cellInstToUnconPorts.size() == 0)
            return null;
        return cellInstToUnconPorts;
    }

    public Collection<EdifPort> getUnconnectedOuterPorts() {
        Collection<EdifPort> unconnectedPorts = new ArrayList<EdifPort>();
        for (EdifPort port : getPortList()) {
            boolean connected = false;
            for (Iterator n = netListIterator(); n.hasNext();) {
                EdifNet net = (EdifNet) n.next();
                for (EdifPortRef epr : net.getConnectedPortRefs()) {
                    if (epr.getPort() == port) {
                        connected = true;
                        break;
                    }
                }
                if (connected)
                    break;
            }
            if (!connected)
                unconnectedPorts.add(port);
        }
        return unconnectedPorts;
    }

    public EdifNameable getUniqueInstanceNameable(EdifNameable en) {
        if (_cellInstanceList == null)
            return en;
        return _cellInstanceList.getUniqueNameable(en);
    }

    public EdifNameable getUniqueInstanceNameable(EdifCellInstance instance) {
        if (_cellInstanceList == null)
            return instance.getEdifNameable();
        return _cellInstanceList.getUniqueNameable(instance);
    }

    public EdifNameable getUniqueNetNameable(EdifNameable en) {
        if (_netList == null)
            return en;
        return _netList.getUniqueNameable(en);
    }

    public EdifNameable getUniqueNetNameable(EdifNet net) {
        if (_netList == null)
            return net.getEdifNameable();
        return _netList.getUniqueNameable(net);
    }

    /**
     * Return <code>true</code> if the current EdifCell object is not a
     * primitive, has no children in its EdifCellInstance list and no EdifNet
     * objects in its EdifNet list; otherwise, returns <code>false</code>.
     * 
     * @return boolean indicating if this EdifCell is a black box
     */
    public boolean isBlackBox() {
        if (_isPrimitive == false && (_cellInstanceList == null || _cellInstanceList.size() == 0)
                && (_netList == null || _netList.size() == 0))
            return true;
        return false;
    }

    /**
     * @return true if this EdifCell is flat; that is, if all the cells within
     * this cell are leaf cells.
     */
    public boolean isFlat() {
        boolean ret = true;
        for (EdifCellInstance eci : _cellInstanceList) {
            ret = eci.getCellType().isLeafCell();
            if (!ret)
                break;
        }
        return ret;

    }

    /**
     * Return <code>true</code> if the current EdifCell object has no children
     * in its EdifCellInstance list; otherwise, returns <code>false</code>.
     * 
     * @return boolean indicating if this EdifCell is a leaf cell
     */
    public boolean isLeafCell() {
        // TODO: Strictly speaking, this method should not check for an empty 
        //   netList. Only cell hierarchy should be considered.
        if ((_cellInstanceList == null || _cellInstanceList.size() == 0) && (_netList == null || _netList.size() == 0))
            return true;
        return false;
    }

    /**
     * Return whether or not this is a known primitive cell. Note that this is
     * different than a "black box". A black box is a an empty EdifCell whose
     * contents have not been defined.
     * 
     * @return True if this cell is a primitive
     */
    public boolean isPrimitive() {
        return _isPrimitive;
    }

    /**
     * Find all instances within the EdifCell of type oldType and change them to
     * type newType if their interfaces match.
     * 
     * @param oldType EdifCell type to change from
     * @param newType EdifCell type to change to
     */
    public void modifyCellType(EdifCell oldType, EdifCell newType) {

        // Iterate over all EdifCellInstances of type oldType
        Collection oldTypeInstances = findCellInstancesOf(oldType);
        if (oldTypeInstances != null)
            for (Iterator i = oldTypeInstances.iterator(); i.hasNext();) {
                EdifCellInstance eci = (EdifCellInstance) i.next();
                if (eci.getCellType() == oldType)
                    eci.modifyCellRef(newType);
            }
    }

    /**
     * Return an iterator of this cell's nets.
     * 
     * @return An Iterator Object of all the EdifNets contained in this EdifCell
     */
    public Iterator netListIterator() {
        if (_netList != null)
            return _netList.iterator();
        return (new ArrayList(0)).iterator();
    }

    /**
     * This method sets the interface of the cell. While this may be dangerous
     * to allow changes to the interface, it allows the parser to set the
     * interface after the object has been created.
     * 
     * @param iface
     */
    public void setInterface(EdifCellInterface iface) {
        _interface = iface;
    }

    /**
     * Sets this cell to primitive.
     */
    public void setPrimitive() {
        _isPrimitive = true;
    }

    /**
     * Tag this cell as primitive if it matches a cell in the passed-in library.
     * 
     * @param primitives library of primitives that will be compared to this
     * cell.
     */
    public void tagAsPrimitive(EdifLibrary primitives) {
        if (!isLeafCell())
            return;
        for (Iterator i = primitives.iterator(); i.hasNext();) {
            EdifCell cell = (EdifCell) i.next();
            if (this.equalsName(cell)) {
                _isPrimitive = true;
                return;
            }
        }
    }

    /**
     * Write the EDIF representation of this EdifCell object to the
     * {@link EdifPrintWriter} passed as a parameter.
     * 
     * @param epw EdifPrintWriter to which EDIF will be written
     */
    public void toEdif(EdifPrintWriter epw) {
        Iterator it;

        epw.printIndent("(cell ");
        // HOW DO I CHOOSE WHICH super method I call?
        getEdifNameable().toEdif(epw); // print cell name
        epw.println();

        epw.incrIndent(); // indent 1
        epw.printlnIndent("(cellType GENERIC)");
        epw.printIndent("(view ");
        if (isPrimitive())
            epw.print("PRIM");
        else if (isLeafCell())
            epw.print("black_box");
        else
            epw.print("netlist");
        epw.println();
        epw.incrIndent(); // indent 2
        epw.printlnIndent("(viewType NETLIST)");

        _interface.toEdif(epw);

        epw.decrIndent(); // indent 2

        if ((_cellInstanceList != null && !_cellInstanceList.isEmpty()) || (_netList != null && !_netList.isEmpty())) {

            epw.incrIndent(); // indent 2
            epw.printlnIndent("(contents");

            // Print instances & nets
            if (_cellInstanceList != null && !_cellInstanceList.isEmpty()) {

                it = _cellInstanceList.iterator();
                epw.incrIndent(); // indent 3
                while (it.hasNext()) {
                    EdifCellInstance eci = (EdifCellInstance) it.next();
                    epw.flush();
                    eci.toEdif(epw);
                }
                epw.decrIndent(); // indent 3
            } else {
                System.err.println("WARNING: Cell " + this + " has no internal instances");
            }

            if (_netList != null && !_netList.isEmpty()) {
                epw.incrIndent(); // indent 3
                it = _netList.values().iterator();
                while (it.hasNext()) {
                    EdifNet en = ((EdifNet) it.next());
                    en.toEdif(epw);
                }
                epw.decrIndent(); // indent 3
            } else {
                System.err.println("WARNING: Cell " + this + " has no internal nets");
            }

            epw.printlnIndent(")"); //End of contents
            epw.decrIndent(); // indent 2

        }

        Map l = getPropertyList();
        if (l != null) {
            it = l.values().iterator();
            while (it.hasNext()) {
                Property p = (Property) it.next();
                p.toEdif(epw);
            }
        }

        epw.printlnIndent(")"); //End of view
        epw.decrIndent();
        epw.printlnIndent(")"); //End of cell

    }

    /**
     * @return A String representing this Object's name and port interface
     */
    @Override
    public String toString() {
        return getEdifNameable().toString();
    }

    /**
     * This method will trim the size of all collections contained by this
     * class. Trimming each collection will reduce the memory consumed by this
     * object by getting rid of all unused collection references. This method
     * will also call trimToSize on each of the EdifNet objects in the _netList
     * collection.
     */
    public void trimToSize() {
        if (_cellInstanceList != null && (_cellInstanceList instanceof Trimable))
            ((Trimable) _cellInstanceList).trimToSize();
        if (_netList != null) {
            if (_netList instanceof Trimable)
                ((Trimable) _netList).trimToSize();
            for (Iterator i = netListIterator(); i.hasNext();)
                ((EdifNet) i.next()).trimToSize();
        }
        _interface.trimToSize();
    }

    ///////////////////////////////////////////////////////////////////
    ////                  package private methods                  ////

    /**
     * @deprecated Only used by deprecated methods. Do not use in new
     * methods. 
     * 
     * @param cell
     * @throws EdifNameConflictException
     */
    protected void copyCellInternals(EdifCell cell) throws EdifNameConflictException {
        // Copy the cell properties
        if (cell.getPropertyList() != null)
            for (Iterator vals = cell.getPropertyList().values().iterator(); vals.hasNext();)
                addProperty((Property) ((Property) vals.next()).clone());

        // Copy the interface
        _interface = cell._interface.copy(this);

        // Copy the instances
        for (Iterator i = cell.cellInstanceIterator(); i.hasNext();)
            addSubCell(((EdifCellInstance) i.next()).copy(this));

        // Copy the nets
        for (Iterator i = cell.netListIterator(); i.hasNext();)
            addNet(((EdifNet) i.next()).copy(this));
    }

    /**
     * Set the library for this EdifCell object. This method should only be
     * called by {@link EdifLibrary#addCell(EdifCell)} after adding the cell to
     * the library.
     * 
     * @param library EdifLibrary object to set as the library of the current
     * EdifCell
     */
    void setLibrary(EdifLibrary library) {
        _library = library;
    }

    /**
     * Check to see if the name of the EdifLibrary of this EdifCell matches the
     * name of the EdifLibrary of that EdifCell
     * 
     * @param that Comparison EdifCell object
     * @return true if the two EdifLibrary objects have the same name
     */
    private boolean edifEqualLibraryName(EdifCell that) {
        boolean result;

        if (this.getLibrary() == null)
            result = (that.getLibrary() == null);
        else if (that.getLibrary() == null)
            result = false;
        else if (this.getLibrary().getName() == null)
            result = (that.getLibrary().getName() == null);
        else
            result = this.getLibrary().getName().equals(that.getLibrary().getName());

        return result;
    }

    /**
     * Two EdifNet lists are considered edifEqual if
     * <ul>
     * <li> Each of the EdifNet objects are edifEqual</li>
     * </ul>
     * 
     * @param that Comparison EdifCell object
     * @return true if the EdifNet lists in this EdifCell and that EdifCell are
     * edifEqual
     */
    private boolean edifEqualNetList(EdifCell that) {
        Collection<EdifNet> thisNetList = this.getNetList();
        Collection<EdifNet> thatNetList = that.getNetList();

        if (thisNetList == null && thatNetList != null)
            return false;
        if (thisNetList != null && thatNetList == null)
            return false;

        // Make sure that each of the Nets in this EdifCell match one
        // of the Nets in that EdifCell
        if (thisNetList != null)
            for (EdifNet thisNet : thisNetList) {
                boolean result = false;
                for (EdifNet thatNet : thatNetList) {
                    if (thisNet.edifEquals(thatNet)) {
                        result = true;
                        break;
                    }
                }
                if (result == false)
                    return false;
            }
        return true;
    }

    private List<EdifDifference> edifDiffNetList(EdifCell that) {
        List<EdifDifference> differences = new ArrayList<EdifDifference>();
        Collection<EdifNet> thisNetList = this.getNetList();
        Collection<EdifNet> thatNetList = that.getNetList();

        if ((thisNetList == null && thatNetList != null) || (thisNetList != null && thatNetList == null)) {
            differences.add(new EdifDifference(thisNetList, thatNetList));
            return differences;
        }

        // Find all the Nets in thisCell that aren't in thatCell
        if (thisNetList != null)
            for (EdifNet thisNet : thisNetList) {
                boolean result = false;
                for (EdifNet thatNet : thatNetList) {
                    if (thisNet.edifEquals(thatNet)) {
                        result = true;
                        break;
                    }
                }
                if (result == false) {
                    differences.add(new EdifDifference(thisNet, "Not found in cell: " + that.getName()));
                    //differences.addAll(thisNet.edifDiff(thatNet));
                }
            }

        // Find all the Nets in thatCell that aren't in thisCell
        if (thatNetList != null)
            for (EdifNet thatNet : thatNetList) {
                boolean result = false;
                for (EdifNet thisNet : thisNetList) {
                    if (thatNet.edifEquals(thisNet)) {
                        result = true;
                        break;
                    }
                }
                if (result == false) {
                    differences.add(new EdifDifference("Not found in cell: " + this.getName(), thatNet));
                    //differences.addAll(thisNet.edifDiff(thatNet));
                }
            }
        return differences;

    }

    /**
     * Two EdifCellInstance lists are considered edifEqual if
     * <ul>
     * <li> Each of the EdifCellInstance objects are edifEqual</li>
     * </ul>
     * 
     * @param that Comparison EdifCell object
     * @return true if the EdifCellInstance lists are edifEqual
     */
    private boolean edifEqualCellInstanceList(EdifCell that) {
        Collection<EdifCellInstance> thisCellInstanceList = this.getCellInstanceList();
        Collection<EdifCellInstance> thatCellInstanceList = that.getCellInstanceList();

        if (thisCellInstanceList == null && thatCellInstanceList != null)
            return false;
        if (thisCellInstanceList != null && thatCellInstanceList == null)
            return false;

        // Make sure that each of the Cell Instances in this EdifCell match one
        // of the CellInstances in that EdifCell
        if (thisCellInstanceList != null)
            for (EdifCellInstance thisEci : thisCellInstanceList) {
                boolean result = false;
                for (EdifCellInstance thatEci : thatCellInstanceList) {
                    if (thisEci.edifEquals(thatEci)) {
                        result = true;
                        break;
                    }
                }
                if (result == false)
                    return false;
            }
        return true;
    }

    private List<EdifDifference> edifDiffCellInstanceList(EdifCell that) {
        List<EdifDifference> differences = new ArrayList<EdifDifference>();
        Collection<EdifCellInstance> thisCellInstanceList = this.getCellInstanceList();
        Collection<EdifCellInstance> thatCellInstanceList = that.getCellInstanceList();

        if (thisCellInstanceList == null && thatCellInstanceList != null) {
            differences.add(new EdifDifference(thisCellInstanceList, "non-null"));
        }
        if (thisCellInstanceList != null && thatCellInstanceList == null) {
            differences.add(new EdifDifference("non-null", thatCellInstanceList));
        }

        // Make sure that each of the Cell Instances in this EdifCell match one
        // of the CellInstances in that EdifCell
        if (thisCellInstanceList != null)
            for (EdifCellInstance thisEci : thisCellInstanceList) {
                boolean result = false;
                for (EdifCellInstance thatEci : thatCellInstanceList) {
                    if (thisEci.edifEquals(thatEci)) {
                        result = true;
                        break;
                    }
                }
                if (result == false) {
                    differences.add(new EdifDifference(thisEci, ""));
                }
            }

        if (thatCellInstanceList != null)
            for (EdifCellInstance thatEci : thatCellInstanceList) {
                boolean result = false;
                for (EdifCellInstance thisEci : thisCellInstanceList) {
                    if (thatEci.edifEquals(thisEci)) {
                        result = true;
                        break;
                    }
                }
                if (result == false) {
                    differences.add(new EdifDifference("", thatEci));
                }
            }

        return differences;
    }

    
    
    /**
     * This List is used to hold all of the instanced children within the cell
     * (EdifCellInstance). No space is initially allocated for this list since
     * primitive cells will not contain cell instances.
     */
    private EdifNameSpaceMap<EdifCellInstance> _cellInstanceList;

    private Collection<String> _bbInstanceList = new ArrayList<String>();

    public void addBlackBoxResource(String type) {
        _bbInstanceList.add(type);
    }

    public Collection<String> getBlackBoxResources() {
        return _bbInstanceList;
    }

    /**
     * The port interface of this EdifCell.
     */
    private EdifCellInterface _interface;

    /**
     * This flag indicates that the given EdifCell is a library "primitive" and
     * does not contain any internal hierarchy (i.e. cell instances or nets).
     * Note that a "primitive" EdifCell is different than an EdifCell that has
     * no internal cell instances or nets. An "empty" EdifCell that is not a
     * primitive corresponds to a "black box" or an EdifCell object whose
     * contents have not been defined. It is called a "black box" because we do
     * not know what is inside of it. A primitive, however, is known to be a
     * leaf-cell.
     * <p>
     * This field is atomic, meaning that it can only be set in the constructor.
     * 
     * @see EdifCell#isLeafCell
     * @see EdifCell#isPrimitive
     */
    private boolean _isPrimitive = false;

    /**
     * The EdifCell is defined within an Edif_Library. This field member defines
     * the Edif_Library that owns this cell.
     */
    private EdifLibrary _library;

    /**
     * This List is used to hold all the internal nets in this cell (EdifNet).
     */
    private EdifNameSpaceMap<EdifNet> _netList;
}
