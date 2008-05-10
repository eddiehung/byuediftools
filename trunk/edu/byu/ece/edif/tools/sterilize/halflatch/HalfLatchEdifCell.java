/*
 * TODO: Insert class description here.
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
package edu.byu.ece.edif.tools.sterilize.halflatch;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import edu.byu.ece.edif.arch.xilinx.XilinxMergeParser;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifPrintWriter;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.core.RenamedObject;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;

public class HalfLatchEdifCell extends EdifCell {

    /**
     * Full constructor
     * 
     * @throws InvalidEdifNameException
     * @throws EdifNameConflictException
     */
    public HalfLatchEdifCell(EdifLibrary lib, String name, HalfLatchArchitecture hlArchitecture,
            int safeConstantPolarity) throws EdifNameConflictException, InvalidEdifNameException {
        this(lib, new NamedObject(name), hlArchitecture, safeConstantPolarity);
    }

    /**
     * Full constructor
     * 
     * @throws EdifNameConflictException
     */
    public HalfLatchEdifCell(EdifLibrary lib, EdifNameable name, HalfLatchArchitecture hlArchitecture,
            int safeConstantPolarity) throws EdifNameConflictException {
        super(lib, name);
        _hlArchitecture = hlArchitecture;
        _safeConstantPolarity = safeConstantPolarity;
    }

    /**
     * Copy constructor
     * 
     * @throws InvalidEdifNameException
     * @throws EdifNameConflictException
     */
    public HalfLatchEdifCell(EdifLibrary lib, EdifCell cell, HalfLatchArchitecture hlArchitecture,
            int safeConstantPolarity) throws EdifNameConflictException, InvalidEdifNameException {
        this(lib, cell, new RenamedObject(cell.getName() + "_hl", cell.getOldName()), hlArchitecture,
                safeConstantPolarity);
        //this(lib, cell, cell.getName()+"_hl", hlArchitecture, safeConstantPolarity);
    }

    /**
     * Copy constructor
     * 
     * @throws InvalidEdifNameException
     * @throws EdifNameConflictException
     */
    public HalfLatchEdifCell(EdifLibrary lib, EdifCell cell, String name, HalfLatchArchitecture hlArchitecture,
            int safeConstantPolarity) throws EdifNameConflictException, InvalidEdifNameException {
        this(lib, cell, new NamedObject(name), hlArchitecture, safeConstantPolarity);
    }

    /**
     * Copy constructor
     * 
     * @throws InvalidEdifNameException
     * @throws EdifNameConflictException
     */
    public HalfLatchEdifCell(EdifLibrary lib, EdifCell cell, EdifNameable name, HalfLatchArchitecture hlArchitecture,
            int safeConstantPolarity) throws EdifNameConflictException, InvalidEdifNameException {
        super(lib, cell, name);
        _hlArchitecture = hlArchitecture;
        _safeConstantPolarity = safeConstantPolarity;
        initSubCellLists();
        if (_debug)
            System.out.println("Constructed new hl edif cell " + this + " with polarity " + _safeConstantPolarity);
    }

    /**
     * Create a collection of the primitive and non-primitive sub-cell instances
     * BHP: Include BlackBox sub-cells in the list of primitives? They should be
     * treated exactly the same, I believe.
     */
    private void initSubCellLists() {
        // Create a collection of the primitive and non-primitive sub-cell instances
        _primitiveSubCellList = new ArrayList<EdifCellInstance>();
        _nonPrimitiveSubCellList = new ArrayList<EdifCellInstance>();
        for (EdifCellInstance eci : getSubCellList()) {
            if (eci.getCellType().isPrimitive() == true)
                //if (eci.getCellType().isPrimitive() == true || eci.getCellType().isBlackBox() == true)
                _primitiveSubCellList.add(eci);
            else
                _nonPrimitiveSubCellList.add(eci);
        }
    }

    /**
     * General algorithm as developed by Keith Morgan, Nathan Rollins, Brian
     * Pratt & Dr. Wirthlin
     * <p>
     * For each sub-cell instance do...
     * <p>
     * Fix the sub-cell instance based on the following cases...
     * <ol>
     * <li>case 1 - not primitive
     * <ol>
     * <li> Modify the cell reference of each instance to point to
     * EdifHalfLatchCell version of the original cell type (otherwise all of the
     * instances are pointing to the old cells which don't have access to
     * methods to create the 'safe constant' port, net, etc...)
     * <li> connect 'safe constant' net** in this cell to the 'safe constant'
     * port** in cell instance
     * <p>
     * TODO: If the call to fixPotentialHalfLatches were based on hierarchy,
     * this could only be done when children cells (no matter their level in the
     * hierarchy) need the safe constant passed down.
     * </ol>
     * <li>case 2 - primitive
     * <ul>
     * <li>if half-latch prone primitive (i.e. fd)
     * <ol>
     * <li> replace with safe primitive (i.e. fdcpe)
     * <li> reattach existing nets to ports on new primitive
     * <li> attach 'safe constant' net** to to unconnected ports on new
     * primitive
     * </ol>
     * <li>else if constant primitive (i.e. gnd or vcc)
     * <ol>
     * <li> reconnect everything the constant cell drives to the 'safe constant
     * net'**
     * <li> delete the constant cell
     * </ol>
     * <li>else
     * <ul>
     * <li>do nothing
     * </ul>
     * </ul>
     * </ol>
     * End For
     * <p>
     * NOTE: The 'safe constant' net, inverted 'safe constant' net, 'safe
     * constant' net inverter, and 'safe constant' port are NOT created until
     * they are requested for the first time.
     */
    //	public int fixPotentialHalfLatches(Map origCellToHalfLatchCellMap) {    	
    //		if (_debug) System.out.println ("FIXING POTENTIAL HALF LATCHES FOR CELL " + this.getName());
    //		
    //		// For a description of this algorithm, see above.
    //		int numConstantPortsToDriveWithSafeConstant = 0;
    //		
    //		// Get a collection of all of the sub-cell instances for 'this' cell
    //		Object[] subCellListArray = this.getSubCellList().toArray();
    //
    //		// For each child cell intance in 'this' cell do...
    //		for (int i=0; i<subCellListArray.length; i++) {
    //
    //			// We need a reference to the ith sub-cell instance and a 
    //			// string representing the type of cell this instance is.
    //			EdifCellInstance eci = (EdifCellInstance)subCellListArray[i];
    //			String eciType = eci.getType();			
    //			
    //			// case 1 - the ith cell instance is NOT primitive
    //			// i.   modify the cell reference of each instance to point it's original
    //			//      cell type's EdifHalfLatchCell version (otherwise all of the
    //			//      instances are pointing to the old cells which don't have access
    //			//      to methods to create the 'safe constant' port, net, etc...)
    //			// ii.  connect 'safe constant' net** in this cell to the 
    //			//      'safe constant' port** in cell instance
    //			if (eci.getCellType().isPrimitive() == false) {
    //				if (_debug) System.out.println("Attempting to modify reference for cell instance " + eci + " of type " + eciType);
    //				// i. modify the cell reference
    //				modifyInstanceCellReference(eci, origCellToHalfLatchCellMap);
    //				if (_debug) System.out.println("Instance " + eci + " now has type " + eci.getType());
    //				// ii. connect safe constant ports with the safe constant net
    //				fixNonPrimitive(eci);
    //			}
    //			// case 2 - primitive
    //			// if half-latch prone primitive (i.e. fd)
    //			//    i.   replace with safe primitive (i.e. fdcpe)
    //			//    ii.  reattach existing nets to ports on new primitive
    //			//    iii. attach 'safe constant' net** to to unconnected ports on new primitive
    //			// else if constant primitive (i.e. gnd or vcc)
    //			//    i.   reconnect everything the constant cell drives to the 
    //			//         'safe constant net'**
    //			//    ii.  delete the constant cell
    //			// else
    //			//    do nothing
    //			else {
    //				// if the ith sub-cell instance is a half-latch prone primitive (i.e fd flip-flop)
    //				if (this.getHalfLatchArchitecture().cellRequiresReplacement(eciType) == true) {
    //					if (_debug) System.out.println("Replacing sensitive prim " + eci);
    //					replaceSensitivePrimitive(eci, this.getHalfLatchArchitecture());
    //					this.deleteSubCell(eci);
    //					numConstantPortsToDriveWithSafeConstant++;
    //				}
    //				// else if the ith sub-cell instance is a constant primitive
    //				else if (this.getHalfLatchArchitecture().isConstantCell(eciType) == true) {
    //					if (_debug) System.out.println("Replacing constant cell " + eci);
    //					int constantCellConstantValue = this.getHalfLatchArchitecture().getConstantCellValue(eciType);
    //					replaceConstantPrimitive(eci, constantCellConstantValue);
    //					this.deleteSubCell(eci);
    //					numConstantPortsToDriveWithSafeConstant++;
    //				}
    //				// otherwise do nothing
    //				else {
    //					if (_debug) System.out.println("Doing nothing");
    //				}
    //
    //			}
    //
    //		} // End For
    //
    //		return numConstantPortsToDriveWithSafeConstant;
    //		
    //    }
    /**
     * This function modifies the passed in cell instance eci so that the cell
     * it references (in other words, the cell of which it is an instance) is
     * actually now the EdifHalfLatchCell version.
     */
    public void modifyInstanceCellReference(EdifCellInstance eci, Map origCellToHalfLatchCellMap) {
        // 1. Get the type of cell this instance originally was
        String origCellType = eci.getCellType().getName();
        // 2. Get the modified or HalfLatch version of this original cell type
        EdifCell newHalfLatchEdifCell = (HalfLatchEdifCell) origCellToHalfLatchCellMap.get(origCellType);
        // 3. Modify the cell reference for this specific cell instance to point to the new or HalfLatch version of this cell
        eci.modifyCellRef(newHalfLatchEdifCell, false);
    }

    /**
     * This function replaces the sub-cell instance passed in with the
     * half-latch safe version. i.e. An FD flip-flop gets replaced with an FDCPE
     */
    public int replaceSensitivePrimitive(EdifCellInstance eci) {
        int numPortsToDriveWithSafeConstant = 0;

        // 1. Find out what the 'safe primitive' is for this sensitive primitive
        String safePrimitiveType = this.getHalfLatchArchitecture().getPrimitiveReplacementType(eci);
        EdifLibraryManager elm = getLibrary().getLibraryManager();
        EdifCell safePrimitive = this.getHalfLatchArchitecture().findOrAddPrimitiveReplacementCell(elm,
                safePrimitiveType);

        // 2. Create new safe primitive instance & add it to the current cell (this)
        // Primitive must be renamed to prevent name conflict
        String safePrimitiveInstanceName = null;
        String origName = eci.getName();
        // Check for the SENSITIVE suffix and remove it. If it is not there,
        //   tack on and "_hl" to the original name.
        if (origName.endsWith(SENSITIVE_SUFFIX))
            safePrimitiveInstanceName = origName.substring(0, origName.length() - SENSITIVE_SUFFIX.length());
        else
            safePrimitiveInstanceName = origName + "_hl";

        EdifCellInstance safePrimitiveInstance = null;
        try {
            safePrimitiveInstance = new EdifCellInstance(safePrimitiveInstanceName, this, safePrimitive);
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }
        try {
            this.addSubCell(safePrimitiveInstance);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }

        // 3. Replace existing cell instance with new 'safe primitive' instance
        //    (copy the intance's property list, and reconnect existing nets to new instance)
        EdifTools.replaceCellInstance(this, eci, safePrimitiveInstance);
        //if (_graph == null)
        //    _graph = new EdifCellInstanceGraph(this);
        // Use the Connectivity graph to do this faster
        //EdifTools.replaceCellInstance(this, _graph, eci, safePrimitiveInstance);

        // 4. Connect 'safe constant' net to unconnected ports on new safe primitive
        String[] unconnectedPorts = this.getHalfLatchArchitecture().getPrimitiveReplacementFloatingPorts(eci);
        // 4.1 For each unconnected port in the new safe primitive do...
        for (int i = 0; i < unconnectedPorts.length; i++) {
            // 4.2 Get the edif port (and single bit version) corresponding to the unconnected port name in the string array unconnectedPorts
            EdifPort ep = safePrimitiveInstance.getCellType().getPort(unconnectedPorts[i]);
            EdifSingleBitPort esbp = ep.getSingleBitPort(0);
            int defaultPortValue = this.getHalfLatchArchitecture().getPrimitiveReplacementFloatingPortDefaultValue(eci,
                    unconnectedPorts[i]);
            // 4.3 Create a new port ref connecting the 'safe constant' net to the unconnected port of the new 'safe' primitive 
            EdifPortRef epr = new EdifPortRef(findOrAddSafeConstantNet(defaultPortValue), esbp, safePrimitiveInstance);
            findOrAddSafeConstantNet(defaultPortValue).addPortConnection(epr);
        }
        numPortsToDriveWithSafeConstant = unconnectedPorts.length;

        // 5. Delete the old primitive cell instance
        this.deleteSubCell(eci);

        return numPortsToDriveWithSafeConstant;
    }

    /**
     * Connect 'safe constant' net in the current cell to safe constant port in
     * cell instance passed in.
     */
    public void connectNonPrimitiveToSafeConstant(EdifCellInstance eci) {
        // 1. Get the cell for the non-primitive instance passed in
        HalfLatchEdifCell nonPrimitiveHLCell = (HalfLatchEdifCell) eci.getCellType();
        // 2. Get the 'safe constant' port for this cell (should have already been created)
        EdifPort eciSafeConstantPort = nonPrimitiveHLCell.findOrAddSafeConstantPort();
        // 3. Get the single-bit version of the 'safe constant' port
        EdifSingleBitPort eciSafeConstantSingleBitPort = eciSafeConstantPort.getSingleBitPort(0);
        // 4. Create a new port ref connecting the 'safe constant' net to the 'safe constant' port of the non-primitive passed in
        EdifPortRef epr = new EdifPortRef(findOrAddSafeConstantNet(), eciSafeConstantSingleBitPort, eci);
        findOrAddSafeConstantNet().addPortConnection(epr);
    }

    /**
     * Connect everything the constant primitive drives to the 'safe constant'
     * net. Disconnect all connections to the constant primitive. Delete the
     * constant primitive.
     */
    public int replaceConstantPrimitive(EdifCellInstance eci) {
        return replaceConstantPrimitiveWithoutGraph(eci);
        //if (_graph == null)
        //	_graph = new EdifCellInstanceGraph(this);
        //return replaceConstantPrimitiveUsingGraph(eci);
    }

    /**
     * Connect everything the constant primitive drives to the 'safe constant'
     * net. Disconnect all connections to the constant primitive. Delete the
     * constant primitive.
     */
    public int replaceConstantPrimitiveWithoutGraph(EdifCellInstance eci) {
        int numPortsToDriveWithSafeConstant = 0;

        // First we need the constant value of this cell instance (gnd=0, vcc=1)
        int constantValue = this.getHalfLatchArchitecture().getConstantCellValue(eci.getType());

        // 1. Iterate over all port refs in the current cell (this) 
        Object[] thisCellPortRefs = this.getPortRefs().toArray();
        for (int i = 0; i < thisCellPortRefs.length; i++) {
            EdifPortRef ithCellEPR = (EdifPortRef) thisCellPortRefs[i];
            // If the cell instance of the ith epr matches the cell instance of the cell instance passed in ...
            EdifCellInstance epr_eci = ithCellEPR.getCellInstance();
            if (epr_eci != null && eci.getName().compareToIgnoreCase(epr_eci.getName()) == 0) {
                // Get the net associated with the port ref
                EdifNet oldNet = ithCellEPR.getNet();
                // Iterate over all ports for the current net
                // Replace the current port ref with a port ref to the 'safe constant' net
                if (_debug)
                    System.out.println("Found port ref " + ithCellEPR + " attached to constant cell " + eci);
                if (_debug)
                    System.out.println("Net " + oldNet + " has the following connections...");
                Object[] oldNetPortRefs = oldNet.getConnectedPortRefs().toArray();
                for (int j = 0; j < oldNetPortRefs.length; j++) {
                    EdifPortRef jthNetEPR = (EdifPortRef) oldNetPortRefs[j];
                    if (_debug)
                        System.out.println(jthNetEPR);
                    EdifSingleBitPort jthNetEPRSingleBitPort = jthNetEPR.getSingleBitPort();
                    int port_direction = jthNetEPRSingleBitPort.getParent().getDirection();
                    // Check for connections to top-level ports (if this net drives an output of this cell)
                    if (jthNetEPR.getCellInstance() == null
                            && (port_direction == EdifPort.OUT || port_direction == EdifPort.INOUT)) {
                        // TODO: How do we count the number of ports this port connection will drive?
                        //  We'll ignore this for now...
                        EdifPortRef newPortRef = new EdifPortRef(this.findOrAddSafeConstantNet(constantValue),
                                jthNetEPRSingleBitPort, null);
                        this.findOrAddSafeConstantNet(constantValue).addPortConnection(newPortRef);
                        if (_debug)
                            System.out.println("Adding port ref " + newPortRef + " to safe constant net");
                        // TODO: This creates an empty pass-through cell. Would it be better to 
                        //  route around this cell (and delete it if it is now empty)?
                    }
                    // We only want to keep the net / port ref if it was driven by the old
                    // constant net.  Sources can be thrown away since they
                    // attach to the constant cell instance
                    else if (port_direction == EdifPort.IN || port_direction == EdifPort.INOUT) {
                        EdifCellInstance jthNetEPRCellInstance = jthNetEPR.getCellInstance();
                        // We need to count how many ports this port connection
                        // will drive.  If it connects to a primitive, then only one.
                        // If it connects to a non-primitive, then we need to count
                        // how many things are driven from that port inside the 
                        // non-primitive cell.
                        if (jthNetEPRCellInstance.getCellType().isPrimitive() == false) {
                            //HalfLatchEdifCell hlCell = (HalfLatchEdifCell) origCellToHalfLatchCellMap.get(jthNetEPRCellInstance.getType());
                            HalfLatchEdifCell hlCell = (HalfLatchEdifCell) jthNetEPRCellInstance.getCellType();
                            numPortsToDriveWithSafeConstant += hlCell.getNumConstantPortsToDriveWithSafeConstant();
                        } else {
                            numPortsToDriveWithSafeConstant++;
                        }
                        EdifPortRef newPortRef = new EdifPortRef(this.findOrAddSafeConstantNet(constantValue),
                                jthNetEPRSingleBitPort, jthNetEPRCellInstance);
                        this.findOrAddSafeConstantNet(constantValue).addPortConnection(newPortRef);
                        if (_debug)
                            System.out.println("Adding port ref " + newPortRef + " to safe constant net");
                    }
                    // Delete the port ref from the unsafe constant net to the non-constant cell instance
                    oldNet.deletePortConnection(jthNetEPR);
                }
                // Delete the old port ref (this gets rid of the port ref to the constant instance)
                oldNet.deletePortConnection(ithCellEPR);
                // Delete old Net (if empty)
                if (oldNet.getConnectedPortRefs().isEmpty()) {
                    this.deleteNet(oldNet);
                }
            }
        }
        // 2. Delete the constant cell primitive instance
        this.deleteSubCell(eci);

        return numPortsToDriveWithSafeConstant;
    }

    /**
     * Connect everything the constant primitive drives to the 'safe constant'
     * net. Disconnect all connections to the constant primitive. Delete the
     * constant primitive.
     */
    public int replaceConstantPrimitiveUsingGraph(EdifCellInstance eci) {
        int numPortsToDriveWithSafeConstant = 0;
        if (_graph == null)
            throw new EdifRuntimeException("No ConnectivityGraph available.");

        // First we need the constant value of this cell instance (gnd=0, vcc=1)
        int constantValue = this.getHalfLatchArchitecture().getConstantCellValue(eci.getType());

        // 1. Iterate over all port refs in the current cell (this) 
        //		Use Connectivity graph to find the correct PortRefs.
        Collection oldPortRefs = new ArrayList();
        oldPortRefs.addAll(_graph.getEPRsWhichReferenceInputPortsOfECI(eci));
        oldPortRefs.addAll(_graph.getEPRsWhichReferenceOutputPortsOfECI(eci));
        for (Iterator i = oldPortRefs.iterator(); i.hasNext();) {
            EdifPortRef ithEPR = (EdifPortRef) i.next();
            // Get the net associated with the port ref
            EdifNet oldNet = ithEPR.getNet();
            // Iterate over all ports for the current net
            // Replace the current port ref with a port ref to the 'safe constant' net
            if (_debug)
                System.out.println("Found port ref " + ithEPR + " attached to constant cell " + eci);
            if (_debug)
                System.out.println("Net " + oldNet + " has the following connections...");
            Object[] oldNetPortRefs = oldNet.getConnectedPortRefs().toArray();
            for (int j = 0; j < oldNetPortRefs.length; j++) {
                EdifPortRef jthNetEPR = (EdifPortRef) oldNetPortRefs[j];
                if (_debug)
                    System.out.println(jthNetEPR);
                EdifSingleBitPort jthNetEPRSingleBitPort = jthNetEPR.getSingleBitPort();
                int port_direction = jthNetEPRSingleBitPort.getParent().getDirection();
                // Check for connections to top-level ports (if this net drives an output of this cell)
                if (jthNetEPR.getCellInstance() == null
                        && (port_direction == EdifPort.OUT || port_direction == EdifPort.INOUT)) {
                    // TODO: How do we count the number of ports this port connection will drive?
                    //  We'll ignore this for now...
                    EdifPortRef newPortRef = new EdifPortRef(this.findOrAddSafeConstantNet(constantValue),
                            jthNetEPRSingleBitPort, null);
                    this.findOrAddSafeConstantNet(constantValue).addPortConnection(newPortRef);
                    if (_debug)
                        System.out.println("Adding port ref " + newPortRef + " to safe constant net");
                    // TODO: This creates an empty pass-through cell. Would it be better to 
                    //  route around this cell (and delete it if it is now empty)?
                }
                // We only want to keep the net / port ref if it was driven by the old
                // constant net.  Sources can be thrown away since they
                // attach to the constant cell instance
                else if (port_direction == EdifPort.IN || port_direction == EdifPort.INOUT) {
                    EdifCellInstance jthNetEPRCellInstance = jthNetEPR.getCellInstance();
                    // We need to count how many ports this port connection
                    // will drive.  If it connects to a primitive, then only one.
                    // If it connects to a non-primitive, then we need to count
                    // how many things are driven from that port inside the 
                    // non-primitive cell.
                    if (jthNetEPRCellInstance.getCellType().isPrimitive() == false) {
                        //HalfLatchEdifCell hlCell = (HalfLatchEdifCell) origCellToHalfLatchCellMap.get(jthNetEPRCellInstance.getType());
                        HalfLatchEdifCell hlCell = (HalfLatchEdifCell) jthNetEPRCellInstance.getCellType();
                        numPortsToDriveWithSafeConstant += hlCell.getNumConstantPortsToDriveWithSafeConstant();
                    } else {
                        numPortsToDriveWithSafeConstant++;
                    }
                    EdifPortRef newPortRef = new EdifPortRef(this.findOrAddSafeConstantNet(constantValue),
                            jthNetEPRSingleBitPort, jthNetEPRCellInstance);
                    this.findOrAddSafeConstantNet(constantValue).addPortConnection(newPortRef);
                    if (_debug)
                        System.out.println("Adding port ref " + newPortRef + " to safe constant net");
                }
                // Delete the port ref from the unsafe constant net to the non-constant cell instance
                oldNet.deletePortConnection(jthNetEPR);
            }
            // Delete the old port ref (this gets rid of the port ref to the constant instance)
            oldNet.deletePortConnection(ithEPR);
            // Delete old Net (if empty)
            if (oldNet.getConnectedPortRefs().isEmpty()) {
                this.deleteNet(oldNet);
            }
        }
        // 2. Delete the constant cell primitive instance
        this.deleteSubCell(eci);

        return numPortsToDriveWithSafeConstant;
    }

    /**
     * Create a safe constant generating element to be added to 'this' cell. The
     * element is architecture specific (for example, in a Xilinx architecture
     * an all 0 LUT will be used). Disconnect the 'safe constant' port and drive
     * everything it was driving with the new 'safe constant' generator. TODO:
     * This should probably automatically call the removeSafeConstantPortBuffer
     * method.
     */
    public void addSafeConstantGeneratorCell() {
        if (_safeConstantGeneratorCell == null) {
            // Add a 'safe constant' cell to the top cell we just found with the given polarity
            _safeConstantGeneratorCell = this.getHalfLatchArchitecture().addConstantCellInstance(this,
                    getSafeConstantPolarity());

            // Find and delete the connection between this cell's 'safe constant' port and 'safe constant' net			
            this.removeSafeConstantPort();

            // Now add a connection from the 'safe constant' cell we just created to the 'safe constant' net
            String safeConstantCellOutputPortName = this.getHalfLatchArchitecture().getSafeConstantCellOutputPortName();
            EdifSingleBitPort safeConstantCellOutputPort = _safeConstantGeneratorCell.getCellType().getPort(
                    safeConstantCellOutputPortName).getSingleBitPort(0);
            EdifPortRef new_epr = new EdifPortRef(findOrAddSafeConstantNet(), safeConstantCellOutputPort,
                    _safeConstantGeneratorCell);
            findOrAddSafeConstantNet().addPortConnection(new_epr);
        }
    }

    public EdifCellInstance getSafeConstantGeneratorCell() {
        return _safeConstantGeneratorCell;
    }

    public void removeSafeConstantGeneratorCell() {
        if (_safeConstantGeneratorCell != null) {
            // Get all of the port refs which reference the constant cell instance
            // and delete them (they are deleted from the corresponding net)
            Object[] allAttachedPortRefs = this.getSafeConstantGeneratorCell().getAllEPRs().toArray();
            for (int i = 0; i < allAttachedPortRefs.length; i++) {
                EdifPortRef epr = (EdifPortRef) allAttachedPortRefs[i];
                epr.getNet().deletePortConnection(epr);
            }
            // Delete the constant cell instance from 'this' cell
            this.deleteSubCell(_safeConstantGeneratorCell);
            // Set the reference to the constant cell instance to null
            _safeConstantGeneratorCell = null;
        }
    }

    /**
     * This method should be called on the top-level cell if an internal
     * constant cell is not going to be generated to drive the safe constant
     * distribution network. The top-level port needs to be buffered so it will
     * be kept.
     */
    public void bufferSafeConstantPort() {
        if (_safeConstantGeneratorCell != null)
            throw new EdifRuntimeException(
                    "Error: Cannot buffer safe-constant port in cell which already had port removed and internal constant generator source added.");

        if (_inputBufferCellInstance == null) {

            // Add an ibuf cell instance
            EdifLibraryManager elm = this.getLibrary().getLibraryManager();
            EdifCell inputBufferCell = this.getHalfLatchArchitecture().findOrAddPrimitiveInputBufferCell(elm);
            try {
                _inputBufferCellInstance = new EdifCellInstance("safeConstantInputBuffer", this, inputBufferCell);
                this.addSubCell(_inputBufferCellInstance);
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }

            // Create a net to connect the top-level port to the ibuf
            // Connect the top-level port to the new net.
            // Connect the ibuf input port to the new net.
            String constantPortToBufferNetName = null;
            if (_safeConstantPolarity == 0)
                constantPortToBufferNetName = _safeConstantZeroNetName + "_i";
            else
                constantPortToBufferNetName = _safeConstantOneNetName + "_i";
            // create a net to connect the top-level port to the ibuf
            EdifNet constantPortToBufferNet = null;
            try {
                constantPortToBufferNet = new EdifNet(constantPortToBufferNetName);
                // add the net to 'this' cell
                this.addNet(constantPortToBufferNet);
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }
            // get a reference to the top-level 'safe constant' port of 'this' cell
            EdifSingleBitPort topLevelSafePort = findOrAddSafeConstantPort().getSingleBitPort(0);
            // make a port ref object to connect the port to the net
            EdifPortRef constantPortToBufferNetEPR = new EdifPortRef(constantPortToBufferNet, topLevelSafePort, null);
            // add the port ref to the net
            constantPortToBufferNet.addPortConnection(constantPortToBufferNetEPR);
            // get a reference to the input port of the new input buffer cell
            EdifSingleBitPort inputBufferInputPort = inputBufferCell.getPort(
                    this.getHalfLatchArchitecture().getPrimitiveInputBufferCellInputPortName()).getSingleBitPort(0);
            // make a port ref object to connect the port to the net
            EdifPortRef bufferNetToBufferEPR = new EdifPortRef(constantPortToBufferNet, inputBufferInputPort,
                    _inputBufferCellInstance);
            // add the port ref to the net
            constantPortToBufferNet.addPortConnection(bufferNetToBufferEPR);

            // Connect the ibuf to the internal 'safe constant' net
            // TODO: Should this port ref really be _safeConstantNetSafeConstantPortEPR???
            // get a reference to the output port of the new input buffer cell
            EdifSingleBitPort inputBufferOutputPort = inputBufferCell.getPort(
                    this.getHalfLatchArchitecture().getPrimitiveInputBufferOutputBufferName()).getSingleBitPort(0);
            // make a port ref object to connect the port to the net
            // add the port ref to the net
            this.findOrAddSafeConstantNet().addPortConnection(
                    new EdifPortRef(this.findOrAddSafeConstantNet(), inputBufferOutputPort, _inputBufferCellInstance));

            // Delete the port connection between the safe constant port
            // and the safe constant net
            this.deleteSafeConstantNetSafeConstantPortEPR();
        }
    }

    public EdifCellInstance getSafeConstantPortBufferInstance() {
        return _inputBufferCellInstance;
    }

    /**
     * TODO: This should probably automatically call the
     * removeSafeConstantGeneratorCell method.
     */
    public void removeSafeConstantPortBuffer() {
        if (_inputBufferCellInstance != null) {
            // Get all of the port refs which reference the input buffer cell instance
            // and delete them (they are deleted from the corresponding net)
            Object[] allAttachedPortRefs = this.getSafeConstantPortBufferInstance().getAllEPRs().toArray();
            for (int i = 0; i < allAttachedPortRefs.length; i++) {
                EdifPortRef epr = (EdifPortRef) allAttachedPortRefs[i];
                epr.getNet().deletePortConnection(epr);
            }
            // Delete the input buffer cell instance from 'this' cell
            this.deleteSubCell(_inputBufferCellInstance);
            // Set the reference to the input buffer cell instance to null
            _inputBufferCellInstance = null;
            // Re-attach the constant port to the constant net
            this.findOrAddSafeConstantNetSafeConstantPortEPR();
        }

    }

    public EdifPort getSafeConstantPort() {
        return _safeConstantPort;
    }

    public EdifPort renameSafeConstantPort(String portName) {
        // Remove the old 'safe constant' port
        if (_safeConstantPort != null) {
            removeSafeConstantPort();
        }
        // Create the new 'safe constant' port with the specified name
        addSafeConstantPort(portName);

        return _safeConstantPort;
    }

    public EdifPort findOrAddSafeConstantPort() {
        EdifPort result;
        if (_safeConstantPort == null)
            result = addSafeConstantPort();
        else
            result = _safeConstantPort;
        return result;
    }

    /**
     * This method should NOT be called directly. It is meant only as a utility
     * function for findOrAddSafeConstantPort(). This method pre-determines the
     * safeConstantPort's name. If the safe constant port for 'this' cell has
     * not already been created and added, then this method call will first
     * create and add the port to 'this' cell before returning a reference to
     * it.
     */
    private EdifPort addSafeConstantPort() {
        EdifPort result;
        if (_safeConstantPort == null) {
            String portName = null;
            if (_debug)
                System.out.println("Adding safe constant port to " + this + " with polarity " + _safeConstantPolarity);
            if (_safeConstantPolarity == 0)
                portName = _safeConstantZeroPortName;
            else
                portName = _safeConstantOnePortName;
            result = addSafeConstantPort(portName);
        } else
            result = _safeConstantPort;
        return result;
    }

    /**
     * This method should NOT be called directly. It is meant only as a utility
     * function for addSafeConstantPort(). If the safe constant port for 'this'
     * cell has not already been created and added, then this method call will
     * first create and add the port to 'this' cell before returning a reference
     * to it.
     */
    private EdifPort addSafeConstantPort(String portName) {
        if (_safeConstantPort == null) {
            if (portName == null)
                throw new EdifRuntimeException("String for new safe constant port is null");
            try {
                _safeConstantPort = addPort(portName, 1, EdifPort.IN);
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }
            // connect the new 'safe constant' port to the 'safe constant' net of this cell
            EdifSingleBitPort thisSafeConstantPort = _safeConstantPort.getSingleBitPort(0);
            _safeConstantNetSafeConstantPortEPR = new EdifPortRef(this.findOrAddSafeConstantNet(),
                    thisSafeConstantPort, null);
            _safeConstantNetNorm.addPortConnection(_safeConstantNetSafeConstantPortEPR);
        }
        return _safeConstantPort;
    }

    private void removeSafeConstantPort() {
        if (_safeConstantPort != null) {

            // TODO: Wouldn't this be easier if we just iterated through
            // all of the port's portRefs and deleted all of them???
            Object[] portRefs = this.getPortRefs().toArray();
            EdifPort safeConstantPort = this.getSafeConstantPort();
            for (int i = 0; i < portRefs.length; i++) {
                EdifPortRef epr = (EdifPortRef) portRefs[i];
                if (epr.getPort().equals(safeConstantPort))
                    epr.getNet().deletePortConnection(epr);
            }
            /*
             * EdifNet connectedNet = this.getSafeConstantPort().getInnerNet();
             * EdifPortRef safeConstantPortSafeConstantNetEPR =
             * connectedNet.getEdifPortRef(null,
             * _safeConstantPort.getSingleBitPort(0)); if
             * (this.getSafeConstantNetSafeConstantPortEPR().equals(safeConstantPortSafeConstantNetEPR)) {
             * System.out.println ("Deleting safe constant net - safe constant
             * port EPR"); _safeConstantNetSafeConstantPortEPR = null; }
             * safeConstantPortSafeConstantNetEPR.getNet().deletePortConnection(safeConstantPortSafeConstantNetEPR);
             */
            // Delete the actual port
            this.deletePort(_safeConstantPort);
            _safeConstantNetSafeConstantPortEPR = null;
            _safeConstantPort = null;
        }
    }

    public EdifNet getSafeConstantNet(int polarity) {
        EdifNet result;
        if (polarity == 0 && _safeConstantPolarity == 0 || polarity == 1 && _safeConstantPolarity == 1)
            result = getSafeConstantNet();
        else
            result = getSafeConstantNetInv();
        return result;
    }

    public EdifNet findOrAddSafeConstantNet(int polarity) {
        EdifNet result;
        if (polarity == 0 && _safeConstantPolarity == 0 || polarity == 1 && _safeConstantPolarity == 1)
            result = findOrAddSafeConstantNetNormPolarity();
        else
            result = findOrAddSafeConstantNetInvPolarity();
        return result;
    }

    public EdifNet getSafeConstantNet() {
        return _safeConstantNetNorm;
    }

    public EdifNet findOrAddSafeConstantNet() {
        return findOrAddSafeConstantNetNormPolarity();
    }

    /**
     * If the safe constant net with normal polarity for 'this' cell has not
     * already been created and added, then this method call will first create
     * and add the net to 'this' cell before returning a reference to it.
     */
    private EdifNet findOrAddSafeConstantNetNormPolarity() {
        EdifNet result;
        if (_safeConstantNetNorm == null)
            result = addSafeConstantNetNormPolarity();
        else
            result = _safeConstantNetNorm;
        return result;
    }

    /**
     * This method should NOT be directly called. It is meant as a utility
     * function for the function findOrAddSafeConstantNetNormPolarity()
     */
    private EdifNet addSafeConstantNetNormPolarity() {
        if (_safeConstantNetNorm == null) {
            String netName = null;
            if (_safeConstantPolarity == 0)
                netName = _safeConstantZeroNetName;
            else
                netName = _safeConstantOneNetName;
            try {
                _safeConstantNetNorm = new EdifNet(netName);
                addNet(_safeConstantNetNorm);
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }

        }
        return _safeConstantNetNorm;
    }

    public EdifNet getSafeConstantNetInv() {
        return _safeConstantNetInv;
    }

    public EdifNet findOrAddSafeConstantNetInv() {
        return findOrAddSafeConstantNetInvPolarity();
    }

    /**
     * If the safe constant inverter and or the safe constant net with inverted
     * polarity for 'this' cell have not already been created and added, then
     * this method call will first create and add the inverter and net (attached
     * to the output of the inverter) to 'this' cell before returning a
     * reference to the inverted polarity net.
     */
    private EdifNet findOrAddSafeConstantNetInvPolarity() {
        EdifNet result;
        if (_safeConstantNetInv == null) {
            addSafeConstantInverter();
            result = addSafeConstantNetInvPolarity();
        } else
            result = _safeConstantNetInv;
        return result;
    }

    /**
     * This method should NOT be directly called. It is meant as a utility
     * function for the function findOrAddSafeConstantNetInvPolarity()
     */
    private EdifNet addSafeConstantNetInvPolarity() {
        if (_safeConstantNetInv == null) {
            String netName = null;
            if (_safeConstantPolarity == 0)
                netName = _safeConstantOneNetName;
            else
                netName = _safeConstantZeroNetName;
            try {
                _safeConstantNetInv = new EdifNet(netName);
                addNet(_safeConstantNetInv);
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }

        }
        return _safeConstantNetInv;
    }

    /**
     * If the safe constant net with *normal* polarity for 'this' cell has not
     * already been created and added, then this method call will first create
     * and add the net to 'this' cell. By default, this method will create a net
     * which is attached to the output of the inverter this method is supposed
     * to create.
     */
    private void addSafeConstantInverter() {
        if (_safeConstantInverterInstance == null) {
            // create an inverter
            EdifCell inverterCell = this.getHalfLatchArchitecture().findOrAddPrimitiveInverterCell(
                    this.getLibrary().getLibraryManager());
            try {
                _safeConstantInverterInstance = new EdifCellInstance("HL_INV", this, inverterCell);
                this.addSubCell(_safeConstantInverterInstance);
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }

            // get a copy of the original safe constant net
            EdifNet thisSafeConstantNet = this.findOrAddSafeConstantNet();
            // create a new net (hereafter referred to as 'safe constant' net inv
            EdifNet thisSafeConstantNetInv = this.addSafeConstantNetInvPolarity();
            // connect the normal polarity safe constant net to the input of the inverter		
            EdifSingleBitPort inverterInputPort = inverterCell.getPort(
                    this.getHalfLatchArchitecture().getPrimitiveInverterCellInputPortName()).getSingleBitPort(0);
            EdifPortRef constantNetInverterInputEPR = new EdifPortRef(thisSafeConstantNet, inverterInputPort,
                    _safeConstantInverterInstance);
            thisSafeConstantNet.addPortConnection(constantNetInverterInputEPR);
            // connect the inverse polarity safe constant net to the output of the inverter
            EdifSingleBitPort inverterOutputPort = inverterCell.getPort(
                    this.getHalfLatchArchitecture().getPrimitiveInverterCellOutputPortName()).getSingleBitPort(0);
            EdifPortRef constantInvNetInverterOutputEPR = new EdifPortRef(thisSafeConstantNetInv, inverterOutputPort,
                    _safeConstantInverterInstance);
            thisSafeConstantNetInv.addPortConnection(constantInvNetInverterOutputEPR);
        }
    }

    public int getSafeConstantPolarity() {
        return _safeConstantPolarity;
    }

    public HalfLatchArchitecture getHalfLatchArchitecture() {
        return _hlArchitecture;
    }

    public EdifPortRef getSafeConstantNetSafeConstantPortEPR() {
        return _safeConstantNetSafeConstantPortEPR;
    }

    public EdifPortRef findOrAddSafeConstantNetSafeConstantPortEPR() {
        if (_safeConstantNetSafeConstantPortEPR == null) {
            if (_safeConstantNetNorm != null) {
                _safeConstantNetNorm.addPortConnection(new EdifPortRef(this.findOrAddSafeConstantNet(), this
                        .findOrAddSafeConstantPort().getSingleBitPort(0), null));
                System.out.println("Re-adding port ref from port to safe constant net");
            } else
                this.addSafeConstantNetNormPolarity();
        }
        return _safeConstantNetSafeConstantPortEPR;
    }

    public void deleteSafeConstantNetSafeConstantPortEPR() {
        if (_safeConstantNetSafeConstantPortEPR != null) {
            _safeConstantNetSafeConstantPortEPR.getNet().deletePortConnection(_safeConstantNetSafeConstantPortEPR);
            _safeConstantNetSafeConstantPortEPR = null;
        }
    }

    public Collection getPrimitiveSubCellList() {
        if (_primitiveSubCellList == null)
            initSubCellLists();
        return _primitiveSubCellList;
    }

    public Collection getNonPrimitiveSubCellList() {
        if (_nonPrimitiveSubCellList == null)
            initSubCellLists();
        return _nonPrimitiveSubCellList;
    }

    public boolean isFixed() {
        return _fixed;
    }

    public void setNumConstantPortsToDriveWithSafeConstant(int num) {
        _numConstantPortsToDriveWithSafeConstant = num;
        _fixed = true;
    }

    public int getNumConstantPortsToDriveWithSafeConstant() {
        return _numConstantPortsToDriveWithSafeConstant;
    }

    private EdifCellInstanceGraph _graph = null;

    private Collection<EdifCellInstance> _primitiveSubCellList = null;

    private Collection<EdifCellInstance> _nonPrimitiveSubCellList = null;

    private EdifPort _safeConstantPort = null;

    private EdifNet _safeConstantNetNorm = null;

    private EdifNet _safeConstantNetInv = null;

    private EdifCellInstance _safeConstantInverterInstance = null;

    private EdifPortRef _safeConstantNetSafeConstantPortEPR = null;

    private EdifCellInstance _safeConstantGeneratorCell = null;

    private EdifCellInstance _inputBufferCellInstance = null;

    private HalfLatchArchitecture _hlArchitecture = null;

    private int _safeConstantPolarity = 0;

    private boolean _fixed = false;

    private int _numConstantPortsToDriveWithSafeConstant = 0;

    public static final String _safeConstantZeroNetName = "safeConstantNet_zero";

    public static final String _safeConstantOneNetName = "safeConstantNet_one";

    public static final String _safeConstantZeroPortName = "safeConstantPort_zero";

    public static final String _safeConstantOnePortName = "safeConstantPort_one";

    public static final String SENSITIVE_SUFFIX = "_SENSITIVE";

    private static final boolean _debug = false;

    public static int test(String inputEdifFilename, String outputEdifFilename) {
        String error_report = "";

        int safeConstantPolarity = 1;
        String safeConstantPortName = null, safeConstantNetName = null;
        if (safeConstantPolarity == 1) {
            safeConstantPortName = _safeConstantOnePortName;
            safeConstantNetName = _safeConstantOneNetName;
        } else {
            safeConstantPortName = _safeConstantZeroPortName;
            safeConstantNetName = _safeConstantZeroNetName;
        }

        // Load EDIF
        String[] args = new String[1];
        args[0] = inputEdifFilename;
        EdifCell top_cell = XilinxMergeParser.parseAndMergeXilinx(args);
        EdifEnvironment environment = top_cell.getLibrary().getLibraryManager().getEdifEnvironment();

        XilinxHalfLatchArchitecture hlArchitecture = new XilinxHalfLatchArchitecture(top_cell);

        // This should do the actual half latch removal
        SequentialEdifHalfLatchRemover sequentialEdifHalfLatchRemover = new SequentialEdifHalfLatchRemover(
                hlArchitecture, safeConstantPolarity, true);
        EdifEnvironment halflatchSafeEdifEnvironment = sequentialEdifHalfLatchRemover.removeHalfLatches(environment);

        // Now we make all of our checks

        /*
         * All non-primitive cells should be of type HalfLatchEdifCell and
         * consequently should also have a safe constant net with the same name
         * as the variable safeConstantNetName.
         * 
         * In addition, the safeConstantNet should connect to a port in the
         * current cell with the same name as safeConstantPortName AND to a
         * like-named port in ALL non-primitive sub-cell instances of the
         * current cell.
         */
        EdifLibraryManager elm = top_cell.getLibrary().getLibraryManager();
        for (Iterator i = elm.iterator(); i.hasNext();) {
            EdifLibrary lib = (EdifLibrary) i.next();
            for (Iterator j = lib.iterator(); j.hasNext();) {
                EdifCell cell = (EdifCell) j.next();
                if (cell.isPrimitive() == false) {
                    if (testHasSafeNet(cell, safeConstantNetName) == true) {
                        if (testHasSafePort(cell, safeConstantPortName) == false)
                            error_report += ("\n Error: HalfLatchEdifCell: Cell " + cell.getName()
                                    + " has no safe constant port, but it should have one named " + safeConstantPortName);
                        Collection<EdifCellInstance> nonConnectedNonPrimitiveSubCells = new ArrayList<EdifCellInstance>();
                        for (Iterator m = cell.getSubCellList().iterator(); m.hasNext();) {
                            EdifCellInstance subcell_eci = (EdifCellInstance) m.next();
                            if ((subcell_eci.getCellType().isPrimitive() == false)
                                    && (testHasSafePort(subcell_eci.getCellType(), safeConstantPortName) == false))
                                nonConnectedNonPrimitiveSubCells.add(subcell_eci);
                        }
                        if (nonConnectedNonPrimitiveSubCells.size() > 0)
                            error_report += ("\n Error: HalfLatchEdifCell: Cell "
                                    + cell.getName()
                                    + " has the following non-primitive sub-cell instances with no safe constant port... " + nonConnectedNonPrimitiveSubCells);
                    } else {
                        error_report += ("\n Error: HalfLatchEdifCell: Cell " + cell.getName()
                                + " has no safe constant net, but it should have one named " + safeConstantNetName);
                    }
                }
            }
        }

        // All primitive cells should not be sensitive primitives
        // && should not be constant primitives
        for (Iterator i = elm.iterator(); i.hasNext();) {
            EdifLibrary lib = (EdifLibrary) i.next();
            Collection cellInstances = lib.findCellInstancesOf(lib);
            if (cellInstances != null) {
                for (Iterator j = cellInstances.iterator(); j.hasNext();) {
                    EdifCellInstance eci = (EdifCellInstance) j.next();
                    if (hlArchitecture.isConstantCell(eci.getType()) == true)
                        error_report += ("\n Error: HalfLatchEdifCell: Design has remaining constant cell instance " + eci
                                .getName());
                    else if (hlArchitecture.cellRequiresReplacement(eci) == true)
                        error_report += ("\n Error: HalfLatchEdifCell: Design has remaining half-latch prone primitive cell instance " + eci
                                .getName());
                }
            }
        }

        //elm.validateOrder();

        // If the user specified a non-null filename for the output edif
        // then write the modified edif to file.
        if (outputEdifFilename != "") {
            try {
                EdifPrintWriter epw = new EdifPrintWriter(new FileOutputStream(outputEdifFilename));
                top_cell.getLibrary().getLibraryManager().getEdifEnvironment().toEdif(epw);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e);
                System.exit(1);
            }
        }

        // Print the error report
        int result;
        if (error_report != "") {
            System.out.println(error_report);
            result = -1;
        } else {
            System.out.println("Success: HalfLatchEdifCell: All tests passed");
            result = 0;
        }
        return result;
    }

    private static boolean testHasSafeNet(EdifCell cell, String safeConstantNetName) {
        boolean safeNetFound = false;
        for (Iterator k = cell.getNetList().iterator(); k.hasNext();) {
            EdifNet net = (EdifNet) k.next();
            if (net.getName().compareToIgnoreCase(safeConstantNetName) == 0) {
                safeNetFound = true;
            }
        }
        return safeNetFound;
    }

    private static boolean testHasSafePort(EdifCell cell, String safeConstantPortName) {
        boolean safePortFound = false;
        for (Iterator l = cell.getPortList().iterator(); l.hasNext();) {
            EdifPort port = (EdifPort) l.next();
            if (port.getName().compareToIgnoreCase(safeConstantPortName) == 0) {
                safePortFound = true;
            }
        }
        return safePortFound;
    }

    public static void main(String[] args) {
        HalfLatchEdifCell.test(args[0], args[1]);
    }

}
