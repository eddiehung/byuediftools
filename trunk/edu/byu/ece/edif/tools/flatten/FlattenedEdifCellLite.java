/*
 * Represents a flattened EdifCell object
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
package edu.byu.ece.edif.tools.flatten;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifPrintWriter;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.util.merge.EdifMergeParser;

/**
 * Represents a flattened EdifCell object; that is, an EdifCell with no
 * subcells, composed entirely of primitives. This class will create a new
 * flattened EdifCell object during construction that represents a flattened
 * version of the original EdifCell passed into the constructor. This class
 * maintains associations between instances in the flattened cell to the
 * corresponding instances in the original cell.
 * <p>
 * This class also contains a main function which can be used to create a new
 * flattened EDIF netlist of any given cell.
 * <p>
 * TODO:
 * <ul>
 * <li> create a "connection block" (or some other better name) class that
 * describes the connections to top-level ports. While the map works fine, such
 * a connection block would be more explicit and provide the documentation and
 * more intuitive method names for use (this is difficult code to follow).
 * <li> Create an interface for a hierarchical instance object. This interface
 * would contain many of the methods described in the
 * edu.byu.ece.edif.arch.xilinx.InstanceHierarchy class. "InstanceHierachical" (or
 * whatever)
 * <ul>
 * <li> public Collection<InstanceHierarchical> getChildren
 * <li> public EdifCellInstance getInstance
 * <li> public EdifCell getInstanceCellType
 * <li> public InstanceHierarchical getParent
 * </ul>
 * <li> Create a class that can create and parse hierarchical names from an
 * instance of this interface. A default class can be created that provides a
 * standard separator. Variations of this class can use the rename or edif name.
 * <ul>
 * <li> Given an arbitrary InstanceHierarchical, provide a valid instance string
 * <li> Using the same "instance naming strategy" as the above, parse a given
 * name and "find" the InstanceHierarchical object associated with this name
 * from within a "top-level" InstanceHierarchical object.
 * </ul>
 * <li> Create a tree of hierarchical instance objects from the original
 * hierarchical cell while constructing the flattened cell.
 * <li> Extend the EdifCellInstance class for instances used in this class.
 * "FlattenedEdifCellInstance"
 * <ul>
 * <li> Return an instance hierarchy object associated with its "sister"
 * instance.
 * </ul>
 * </ul>
 * 
 * @version $Id:FlattenedEdifCellLite.java 130 2008-03-31 16:23:42Z jamesfcarroll $
 * @author Mike Wirthlin
 */
public class FlattenedEdifCellLite extends EdifCell {

    public FlattenedEdifCellLite(EdifCell cell) throws EdifNameConflictException, InvalidEdifNameException {
        this(cell.getLibrary(), cell);
    }

    public FlattenedEdifCellLite(EdifLibrary lib, EdifCell cell) throws EdifNameConflictException, InvalidEdifNameException {

        // 1. Create a new unique name for the flattened cell. This
        // name will be based on a unique name in the library holding
        // the original cell.

        // 2. Copy Interface of cell
        super(lib, cell.getName() + "_flat", cell.getInterface());

        _origCell = cell;
        flatten_top();

    }

    /**
     * @return the original, non-flattened EdifCell used to create this
     * flattened EdifCell.
     */
    public EdifCell getOriginalCell() {
        return _origCell;
    }

    /**
     * Create a new EdifNet object in the flattened hierarchy that corresponds
     * to a net in the original hierarchical circuit.
     * 
     * @param oldNet Original net in _origCell
     * @return New EdifNet object in this EdifCell
     */
    protected EdifNet createNewNet(EdifNet oldNet) {

        EdifNet newNet = null;

        newNet = new EdifNet(getUniqueNetNameable(oldNet), this);

        try {
            addNet(newNet);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
        newNet.copyProperties(oldNet); //copyProperties(oldNet,newNet);
        if (debug)
            System.out.println("Adding new net " + newNet + " based on " + oldNet);
        return newNet;
    }

    /**
     * This method will create the new flattened circuit. This is called by the
     * constructor. This method will perform a number of tasks related to the
     * top-level cell and then recursively call descend_top.
     */
    protected void flatten_top() {

        _newOldNetMap = new LinkedHashMap();
        _newOldInstanceMap = new LinkedHashMap();

        // key: EdifSingleBitPort, value: new flattened EdifNet.
        // This Map is used to associate the nets connected to the top-level port
        // when traversing the hierarchy.
        Map portNetMap = new LinkedHashMap();

        // Iterate over all nets in the top level design to find those that
        // are connected to top-level ports.
        for (Iterator netIterator = _origCell.getNetList().iterator(); netIterator.hasNext();) {
            EdifNet origNet = (EdifNet) netIterator.next();
            EdifNet newNet = null;

            // Iterate over all port refs and identify those that are connected
            // to a top-level port.
            for (EdifPortRef portRef : origNet.getConnectedPortRefs()) {

                if (portRef.isTopLevelPortRef()) {

                    // Create a new net if one has not already been created
                    if (newNet == null) {
                        // create new net
                        newNet = createNewNet(origNet);
                        //_oldNewNetMap.put(origNet,newNet);
                        Collection oldNets = new ArrayList();
                        oldNets.add(origNet);
                        _newOldNetMap.put(newNet, oldNets);
                    }

                    // Connect top-level nets to top-level ports
                    EdifSingleBitPort oldSPort = portRef.getSingleBitPort();
                    EdifPort oldPort = portRef.getPort();
                    EdifPort newPort = getMatchingPort(oldPort);
                    if (newPort == null)
                        System.err.println("Missing port " + oldPort);
                    EdifSingleBitPort newSPort = newPort.getSingleBitPort(oldSPort.bitPosition());
                    //                    EdifSingleBitPort newSPort = 
                    //                        this.getMatchingPort(oldSPort.getParent()).
                    //                        	getSingleBitPort(oldSPort.bitPosition());
                    EdifPortRef epr = new EdifPortRef(newNet, newSPort, null);
                    newNet.addPortConnection(epr);
                    if (debug)
                        System.out.println("Adding top-level port connection " + epr);
                    // Associate new top-level net with *old* top-level single
                    // port
                    portNetMap.put(oldSPort, newNet);
                    //System.out.println(newNet);
                }
            }
        }

        // 4. Desecend into hierarchy
        descend_top(_origCell, portNetMap);

    }

    /**
     * This method recursively inserts the contents of each cell instance of the
     * origCell into the cell.
     * 
     * @param portNetMap Key: EdifSingleBitPort (each single-bit port of the
     * entire cell interface is included as a key). Value: new flattened EdifNet
     * This map lets the method know which nets are already connected to the
     * cell top-level ports.
     */
    protected Collection descend_top(EdifCell origCell, Map<EdifSingleBitPort, EdifNet> portNetMap) {

        // 1. create the following Map
        //    key: EdifCellInstances at this level (includes all of them) 
        //    value: a Collection of EdifPortRefs that attach to instances
        //
        // This will simplify finding connected nets later on in the algorithm
        //
        // We are only preparing this map to be populated -> as we iterate through the nets
        // within this cell (origCell), we will populate this map.
        //

        //    	Map cellInstancePortRefMap = new LinkedHashMap();
        //        for (Iterator edifCellInstanceIterator = origCell.cellInstanceIterator(); edifCellInstanceIterator.hasNext();) {
        //            EdifCellInstance cellInstance = (EdifCellInstance) edifCellInstanceIterator.next();
        //            Collection portRefs = new ArrayList();
        //            cellInstancePortRefMap.put(cellInstance, portRefs);
        //        }

        //we'll be using this collection for populating the _newOldNetMap
        Collection oldNets = null;

        // This Map creates a relationship between EdifNet objects in
        // origCell and the associated EdifNet objects in the flatCell.
        // Internal nets are created and added to flatCell
        //
        // key: EdifNet from origCell, value: EdifNet in flatCell
        Map oldNetNewNetMap = new LinkedHashMap();

        // A Collection of List objects that contain the EdifNets in the 
        // next-higher level that need to be merged (shorted nets). Each
        // List contains EdifNets that should be merged and for which
        // the portNetMap should be updated. The first element in each list
        // is the final Net to use. This Collection will be returned 
        // at the end of this method.
        Collection<List> shortedNets = new ArrayList();

        // 2. Add new internal nets
        // Iterate over all nets at current level of hieararchy
        for (Iterator netIterator = origCell.getNetList().iterator(); netIterator.hasNext();) {
            EdifNet origNet = (EdifNet) netIterator.next();
            EdifNet newNet = null;

            // Get a list of top-level single bit ports attached to this net
            ArrayList ports = new ArrayList(1); // most nets will have only one top-level port connection
            for (EdifPortRef portRef : origNet.getConnectedPortRefs()) {
                if (portRef.isTopLevelPortRef()) {
                    // Net connects to a top-level port 
                    ports.add(portRef.getSingleBitPort());
                }
            }

            if (ports.size() > 0) {
                // This net is attached to at least one top-level port. Find the
                // NEW net created earlier that is now in the instancePortNetMap
                // associated with this port.

                // Determine how many unique nets in the existing "flatCell"
                // are connected to ports associated with this net.
                ArrayList uniqueNets = new ArrayList(ports.size());
                for (Iterator i = ports.iterator(); i.hasNext();) {
                    EdifSingleBitPort sPort = (EdifSingleBitPort) i.next();
                    // This may return null or a net. Null if it is not connected
                    // at the top level, and a net if it is connected
                    Object o = portNetMap.get(sPort);
                    if (o != null && !uniqueNets.contains(o))
                        uniqueNets.add(o);
                }

                // The number of unique nets in the flatCell can be
                // catagorized into one of 3 different cases 
                //   unique nets = 0: 
                //      The port(s) at this level are not connected to any net
                //      in higher levels of hierarchy (i.e. an unconnected port at
                //      this level). For this case, create a new net in the
                //      flattened cell but do not connect it to anythin that has already
                //      been created in the flattened cell.
                //   unique nets = 1:
                //      For this case, the net is connected to exactly one net at
                //      the higher level of hierarchy. We need to get a reference to the
                //      net in the flattened cell that corresponds to the net at the higher
                //      level of hierarchy.
                //   unique nets > 1: 
                //      For this case, the net at this lower level in the hiearchy is
                //      essentially "shorting" or connecting two different nets at the
                //      higher level of hierarchy. In this case, we need to remove the
                //      extra nets and make all connections on all nets attached to
                //      only a single net.

                switch (uniqueNets.size()) {

                case 0: // No higher level net connected to current Net
                    //System.err.println(" 0 higher level nets="+origNet.getName());

                    //String newNetName = getLibrary()
                    //.getNameSpaceResolver().returnUniqueName(
                    //		origNet.getName());

                    //String newNetName =
                    // ((EdifNameable)origNet.getEdifNameable()).getName()+net_index++;
                    newNet = createNewNet(origNet);
                    oldNets = new ArrayList();
                    oldNets.add(origNet);
                    _newOldNetMap.put(newNet, oldNets);
                    if (debug)
                        System.out.println("Creating new net (no higher-level connectivity)");

                    break;

                case 1: // One and only one higher level net connected to current net
                    //System.err.println(" 1 higher level net");
                    EdifSingleBitPort port = (EdifSingleBitPort) ports.get(0);
                    newNet = (EdifNet) portNetMap.get(port);
                    if (newNet == null)
                        System.err.println("*** Can't find matching net!");
                    else {
                        oldNets = (Collection) _newOldNetMap.get(newNet);
                        oldNets.add(origNet);
                        //we shouldn't have to do this, but just in case...
                        _newOldNetMap.put(newNet, oldNets);
                    }
                    if (debug)
                        System.out.println("Hierarchical net found");
                    break;

                default: // More than one higher level nets connected to current net
                    //System.err.println(" Multiple level nets");

                    // Choose and keep first net in list (this is referred to as newNet)
                    //System.out.println("%%% merging multiple nets into one!");
                    Iterator netI = uniqueNets.iterator();
                    newNet = (EdifNet) netI.next();

                    // 1. Create a new List of "shorted" nets. Put newNet 
                    //    at the top of the list.
                    // 2. Do NOT do any reconnecting at this level of the 
                    //    hierarchy.
                    // 3. Add the List to the Collection of "shorts"
                    // 4. Update the newOldNetMap

                    //we need to combine all of the oldNets Collections for each newNet
                    Collection oldNetsCombined = (Collection) _newOldNetMap.get(newNet);

                    while (netI.hasNext()) {
                        EdifNet extraNet = (EdifNet) netI.next();
                        // System.err.println("Net shortening - need to DO");

                        // Copy port ref connections from redundant net to newNet
                        for (EdifPortRef pRef : extraNet.getConnectedPortRefs()) {
                            EdifPortRef newPRef = new EdifPortRef(newNet, pRef.getSingleBitPort(), pRef
                                    .getCellInstance());
                            newNet.addPortConnection(newPRef);
                        }

                        //what about copying over the properties from extraNet??
                        // TODO: check for conflicts. (more error checking here)
                        newNet.copyProperties(extraNet);

                        //get the newOldNetMapping, at the same time as removing the old mapping
                        oldNets = (Collection) _newOldNetMap.remove(extraNet);

                        //do we need to make sure that nothing gets added twice??
                        oldNetsCombined.addAll(oldNets);

                        // Remove net
                        deleteNet(extraNet);
                    }
                    //do this just in case - is it really necessary??
                    _newOldNetMap.put(newNet, oldNetsCombined);

                    // Add new set of shorted nets to Collection
                    shortedNets.add(uniqueNets);

                    break;
                }
                //_oldNewNetMap.put(origNet, newNet);

            } else {
                // This net is NOT attached to any top-level ports
                // (i.e. a local net in this level of hierarchy)

                newNet = createNewNet(origNet);

                oldNets = new ArrayList(1);
                oldNets.add(origNet);
                _newOldNetMap.put(newNet, oldNets);

            }

            oldNetNewNetMap.put(origNet, newNet);
        }

        // 1. Add Cells
        // Iterate over all subcells
        for (Iterator i = origCell.getSubCellList().iterator(); i.hasNext();) {
            EdifCellInstance origCellInst = (EdifCellInstance) i.next();
            EdifCell origSubCellType = origCellInst.getCellType();

            if (origSubCellType.isLeafCell()) {

                // Process a leaf cell. Specifically, add a new
                // EdifCellInstance to flatCell of the same type.

                // Create an instance.
                EdifCellInstance newCellInst = null;
                newCellInst = new EdifCellInstance(origCellInst.getEdifNameable(), this, origCellInst.getCellType());

                this.addSubCellUniqueName(newCellInst);

                _newOldInstanceMap.put(newCellInst, origCellInst);

                if (debug)
                    System.out.println("\tProcessing leaf " + origCell.getName() + "." + origCellInst.getName()
                            + " new=" + newCellInst.getName() + " subcells=" + this.getSubCellList().size());

                // Copy properties of EdifCellInstance
                newCellInst.copyProperties(origCellInst); //copyProperties(origCellInst,prim);

                // wire up primitive
                // first, grab all of the wires connected to the primitive in origCell
                //Collection portRef = (Collection) cellInstancePortRefMap.get(origCellInst);
                Collection portRefs = origCellInst.getAllEPRs();
                for (Iterator pRefIterator = portRefs.iterator(); pRefIterator.hasNext();) {
                    EdifPortRef epr = (EdifPortRef) pRefIterator.next();
                    EdifNet origNet = epr.getNet();
                    EdifNet newNet = (EdifNet) oldNetNewNetMap.get(origNet);

                    //System.out.println("port");

                    //now make a connection to this instance
                    // unless the net is null (meaning that we shouldn't make a connection)
                    if (newNet != null) {

                        // Connect top-level nets to top-level ports
                        EdifSingleBitPort oldSPort = epr.getSingleBitPort();
                        EdifPort oldPort = epr.getPort();
                        EdifPort newPort = newCellInst.getCellType().getMatchingPort(oldPort);
                        if (newPort == null) {
                            System.err.println("Missing port " + oldPort);
                            System.out.println(this.getInterface());
                            System.exit(1);
                        }
                        //EdifSingleBitPort newSPort = newPort.getSingleBitPort(oldSPort.bitPosition());

                        //EdifPortRef newEpr = new EdifPortRef(newNet, newSPort, newCellInst);
                        newNet.addPortConnection(newCellInst, newPort, oldSPort.bitPosition());
                        //addPortConnection(newEpr);
                        //System.out.println("net="+newNet);
                    } else
                        System.err.println("Warning(descend_top): related net not found");
                }
            } else {
                // Process a hierarchical cell.
                // 1. Create portNetMap for instance
                // 2. Call recursively
                if (debug)
                    System.out.println("\tProcessing Hierarchical cell " + origCell.getName() + "."
                            + origCellInst.getName());

                /**
                 * EdifSingleBitPort (each single-bit port of the entire cell
                 * interface is included as a key). Value: new flattened EdifNet
                 * This map lets the method know which nets are already
                 * connected to the cell top-level ports.
                 */
                Map<EdifSingleBitPort, EdifNet> portRefMap = new LinkedHashMap();
                for (Iterator k = origCell.getNetList().iterator(); k.hasNext();) {
                    EdifNet net = (EdifNet) k.next();
                    for (EdifPortRef epr : net.getConnectedPortRefs()) {
                        if (epr.getCellInstance() != origCellInst)
                            continue;
                        EdifSingleBitPort esbp = epr.getSingleBitPort();
                        EdifNet oldNet = epr.getNet();
                        EdifNet newNet = (EdifNet) oldNetNewNetMap.get(oldNet);
                        if (newNet == null)
                            System.err.println("Can't find net");
                        portRefMap.put(esbp, newNet);
                    }
                }

                //now do the recursive call
                Collection<List> newShortedNets = descend_top(origCellInst.getCellType(), portRefMap);

                // See if there were any shorts. If so, reconnect the nets
                // at this level and update portRefMap.
                if (!newShortedNets.isEmpty()) {
                    if (debug)
                        System.out.println("Shorted Nets to combine found");

                    // Iterate over all shorted Net Lists
                    for (List<EdifNet> netsToCombine : newShortedNets) {
                        Iterator netsToCombineIt = netsToCombine.iterator();
                        EdifNet newNet = null;
                        Collection<EdifNet> extraNets = new ArrayList(netsToCombine.size() - 1);

                        // TODO: Reconnect Nets (Already done at lower level???)
                        // Separate new Net from extra Nets
                        newNet = (EdifNet) netsToCombineIt.next();
                        for (; netsToCombineIt.hasNext();) {
                            EdifNet extraNet = (EdifNet) netsToCombineIt.next();
                            extraNets.add(extraNet);
                        }

                        // Update portRefMap and oldNetNewNetMap
                        // Replace any references in the portNetMap to the
                        // extraNets with a reference to the newNet
                        Object[] ports = portNetMap.keySet().toArray();
                        for (int idx = 0; idx < ports.length; idx++) {
                            EdifSingleBitPort esbp = (EdifSingleBitPort) ports[idx];
                            // Does this port connect to one of the extra Nets?
                            if (extraNets.contains(portNetMap.get(esbp))) {
                                // If so, re-map to use the new Net
                                portNetMap.remove(esbp);
                                portNetMap.put(esbp, newNet);
                            }
                        }
                        // Replace any references in the oldNetNewNetMap to the
                        // extraNets with a reference to the newNet
                        Object[] oldNetsSet = oldNetNewNetMap.keySet().toArray();
                        for (int idx = 0; idx < oldNetsSet.length; idx++) {
                            EdifNet oldNet = (EdifNet) oldNetsSet[idx];
                            // Does this old Net map to one of the extra Nets?
                            if (extraNets.contains(oldNetNewNetMap.get(oldNet))) {
                                // If so, re-map to use the new Net
                                oldNetNewNetMap.remove(oldNet);
                                oldNetNewNetMap.put(oldNet, newNet);
                            }
                        }
                    }

                    // Need to pass these up the hierarchy so everybody
                    //  gets the changes (their Net maps have already been
                    //  set up)
                    shortedNets.addAll(newShortedNets);

                } // Handle Shorts
            } // Process hierarchical cell
        } // Iterate over all subcells

        return shortedNets;
    }

    private static boolean debug = false;

    public static FlattenedEdifCellLite getFlatCell(String[] args) {
        /***********************************************************************
         * The value of the string printed out when there is a problem with the
         * argument string.
         **********************************************************************/
        String usageString = "Usage: java FlattenedEdifCellLite <top file> [-L <search directory>]* [-f <filename>]* [-o <outputfilename>]";

        if (args.length < 1) {
            System.out.println(usageString);
            System.exit(1);
        }

        System.out.println("Parsing File " + args[0] + ". . .");
        EdifEnvironment top = EdifMergeParser.getMergedEdifEnvironment(args[0], args);

        System.out.println("Flattening . . .");
        EdifCell cell = top.getTopCell();
        FlattenedEdifCellLite flat_cell = null;
        try {
            flat_cell = new FlattenedEdifCellLite(cell);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }

        return flat_cell;
    }

    public static void main(String[] args) throws IOException {

        FlattenedEdifCellLite flat_cell = getFlatCell(args);
        EdifCell cell = flat_cell.getOriginalCell();
        EdifEnvironment top = cell.getLibrary().getLibraryManager().getEdifEnvironment();

        EdifCellInstance flat_instance = null;
        EdifDesign new_design = null;
        try {
            flat_instance = new EdifCellInstance("flat_" + cell.getName(), null, flat_cell);
            new_design = new EdifDesign("ROOT");
        } catch (InvalidEdifNameException e1) {
            e1.toRuntime();
        }

        new_design.setTopCellInstance(flat_instance);
        top.setTopDesign(new_design);
        cell.getLibrary().deleteCell(cell, true);

        System.out.println("Writing new file . . .");
        Set outputFile = EdifMergeParser.parseArguments(args, "-o");

        String OutputFileName = null;
        if (outputFile == null || outputFile.size() < 1)
            OutputFileName = new String("flat.edf");
        else
            OutputFileName = (String) outputFile.iterator().next();

        try {
            EdifPrintWriter epw = new EdifPrintWriter(new FileOutputStream(OutputFileName));

            //top.getLibraryManager().pruneNonReferencedCells();
            top.toEdif(epw);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
            System.exit(1);
        }

        System.out.println("Done");
        System.out.println("Instances: orig=" + edu.byu.ece.edif.core.EdifUtils.countRecursivePrimitives(cell) + " flat="
                + edu.byu.ece.edif.core.EdifUtils.countRecursivePrimitives(flat_cell));
        System.out.println("Port Refs: orig=" + edu.byu.ece.edif.core.EdifUtils.countPortRefs(cell, true) + " flat="
                + edu.byu.ece.edif.core.EdifUtils.countPortRefs(flat_cell, true));

        System.out.println();
    }

    /**
     * The original, non-flattened EdifCell used to create this flattened
     * EdifCell.
     */
    protected EdifCell _origCell;

    //I'm not sure that the _oldNewNetMap will work on a global basis -> potentially,
    // a single old net could map to multiple new nets, for example when a single
    // cell is instanced multiple times. dej
    //protected Map _oldNewNetMap;

    /**
     * A Map between EdifNet objects in the new flattened EdifCell and the
     * corresponding EdifNet in the original non-flattened EdifCell. Note that a
     * single new EdifNet may correspond to multiple EdifNet objects. This Map
     * will record only one.
     */
    protected Map _newOldNetMap;

    /**
     * A Map between EdifCellInstances in the new flattened EdifCell and the
     * corresponding EdifCellInstance in the original non-flattened EdifCell.
     */
    protected Map _newOldInstanceMap;

}