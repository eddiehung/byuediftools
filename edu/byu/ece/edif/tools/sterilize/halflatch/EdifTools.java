/*
 * Tools used by the Half Latch Removal classes
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
import edu.byu.ece.edif.core.BasicEdifBusNetNamingPolicy;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;

/**
 * This class contains tools used by the Half Latch Removal classes. Most of
 * these were modified from various EDIF classes to work in special cases. Many
 * could be added to the EDIF base classes in the future.
 */
public class EdifTools {

    public static void replaceCellInstance(EdifCell parentCell, EdifCellInstance oldCellInstance,
            EdifCellInstance newCellInstance) {

        //		Make sure new instance type is in library
        EdifLibraryManager elm = newCellInstance.getCellType().getLibrary().getLibraryManager();
        EdifCell safePrimitive = XilinxLibrary.findOrAddXilinxPrimitive(elm, newCellInstance.getType());

        //		Copy all of the properties from the old cell instance to the new cell instance
        edu.byu.ece.edif.core.PropertyList oldPropertyList = oldCellInstance.getPropertyList();
        if (oldPropertyList != null) {
            for (Iterator plIterator = oldPropertyList.values().iterator(); plIterator.hasNext();) {
                edu.byu.ece.edif.core.Property p = (edu.byu.ece.edif.core.Property) plIterator.next();
                newCellInstance.addProperty(p);
            }
        }

        //		Replace net connections to connect to new cell.
        //		Iterate over all port refs in the parent cell.
        //		If the cell instance of the port ref matches the "oldCellInstance"
        //		then swap the Edif Port Ref so that it points to the new instance.
        String oldCellInstanceName = oldCellInstance.getName();
        for (Iterator i = parentCell.getPortRefs().iterator(); i.hasNext();) {
            EdifPortRef epr = (EdifPortRef) i.next();
            EdifCellInstance epr_eci = epr.getCellInstance();
            //			epr_eci is null if the current port ref points to a top-level port 
            if (epr_eci != null && oldCellInstanceName.compareToIgnoreCase(epr_eci.getName()) == 0) {
                EdifTools.swapEdifPortRef(epr, newCellInstance);
            }
        }

    }

    public static void replaceCellInstance(EdifCell parentCell, EdifCellInstanceGraph graph,
            EdifCellInstance oldCellInstance, EdifCellInstance newCellInstance) {

        //		Make sure new instance type is in library
        EdifLibraryManager elm = newCellInstance.getCellType().getLibrary().getLibraryManager();
        EdifCell safePrimitive = XilinxLibrary.findOrAddXilinxPrimitive(elm, newCellInstance.getType());

        //		Copy all of the properties from the old cell instance to the new cell instance
        edu.byu.ece.edif.core.PropertyList oldPropertyList = oldCellInstance.getPropertyList();
        if (oldPropertyList != null) {
            for (Iterator plIterator = oldPropertyList.values().iterator(); plIterator.hasNext();) {
                edu.byu.ece.edif.core.Property p = (edu.byu.ece.edif.core.Property) plIterator.next();
                newCellInstance.addProperty(p);
            }
        }

        //		Replace net connections to connect to new cell.
        //		Use Connectivity graph to find the correct PortRefs.
        Collection oldPortRefs = new ArrayList();
        oldPortRefs.addAll(graph.getEPRsWhichReferenceInputPortsOfECI(oldCellInstance));
        oldPortRefs.addAll(graph.getEPRsWhichReferenceOutputPortsOfECI(oldCellInstance));
        for (Iterator i = oldPortRefs.iterator(); i.hasNext();) {
            EdifPortRef epr = (EdifPortRef) i.next();
            EdifTools.swapEdifPortRef(epr, newCellInstance);
        }

    }

    // This method will iterate over all port refs of the given net
    // and replace references from oldInst to reference of newInst
    public static void swapEdifPortRef(EdifNet net, EdifCellInstance oldInst, EdifCellInstance newInst) {
        List portRefList = new ArrayList();

        for (EdifPortRef epr : net.getConnectedPortRefs()) {
            //if (epr.getCellInstance() == oldInst) {
            if (epr.getCellInstance() != null)
                if (epr.getCellInstance().getName().equals(oldInst.getName())) {
                    portRefList.add(epr);
                }
        }

        for (Iterator i = portRefList.iterator(); i.hasNext();) {
            EdifPortRef epr = (EdifPortRef) i.next();
            swapEdifPortRef(epr, newInst);
        }

    }

    // This method will delete the given EdifPortRef from the EdifNet
    // and add a new EdifPortRef to the neweci. This method assumes
    // that the neweci has a Port of the same name/type as the
    // EdifPort represented by connection.
    public static void swapEdifPortRef(EdifPortRef connection, EdifCellInstance neweci) {

        EdifNet net = connection.getNet();
        net.deletePortConnection(connection);
        EdifSingleBitPort newPort = null;
        EdifPort oldPort = connection.getPort();

        for (Iterator portIt = neweci.getCellType().getPortList().iterator(); portIt.hasNext();) {
            EdifPort port = (EdifPort) portIt.next();
            if (port.equals(oldPort)) {
                newPort = port.getSingleBitPort(connection.getBusMember());
                break;
            }
        }

        if (newPort == null)
            throw new EdifRuntimeException("Can't find port");

        EdifPortRef newRef = new EdifPortRef(net, newPort, neweci);
        net.addPortConnection(newRef);
    }

    /**
     * This method will return the EdifNet that connects to the given EdifPort
     * BIT of the given EdifCellInstance in the given EdifCell
     */
    public static EdifNet getOuterNet(EdifCell cell, EdifCellInstance eci, EdifPort port, int portBit) {
        for (Iterator i = cell.netListIterator(); i.hasNext();) {
            EdifNet net = (EdifNet) i.next();
            if (isAttached(net, eci, port, portBit))
                return net;
        }
        // No net attached - return null
        return null;
    }

    // Overloaded method - for single-bit ports
    public static EdifNet getOuterNet(EdifCell cell, EdifCellInstance eci, EdifPort port) {
        return getOuterNet(cell, eci, port, -1);
    }

    /**
     * FROM EDIFCELLINSTANCE - MODIFIED TO WORK ON AN EDIFCELL INSTEAD Returns
     * the port ref that refers to the passed-in EdifPort.
     * 
     * @param port The EdifPort Object that will be searched for within this
     * EdifCellInstance
     * @return The corresponding EdifPortRef
     */
    public static EdifPortRef getPortRef(EdifCell cell, EdifPort port) {
        for (Iterator i = cell.netListIterator(); i.hasNext();) {
            EdifNet net = (EdifNet) i.next();

            for (Iterator i2 = net.getConnectedPortRefs().iterator(); i2.hasNext();) {
                EdifPortRef pr = (EdifPortRef) i2.next();

                if ((pr.getPort()).equals(port))
                    return pr;
            }
        }
        return null;
    }

    /**
     * FROM EDIFCELL - MODIFIED TO WORK WITH MULTI-BIT EDIFPORTS This method
     * will return the EdifNet that is connected to the given port on the given
     * instance.
     * 
     * @param instance The EdifCellInstance Object that one of the returned
     * EdifNet Object's EdifPortRef Objects connects to
     * @param port The EdifPort Object that one of the returned EdifNet Object's
     * EdifPortRef Objects refers to
     * @param bit The bit of the EdifPort that you want the EdifNet connected to
     * @return An EdifNet Object that has an EdifPortRef Object that refers to
     * the passed-in EdifPort Object, and connects to the passed-in
     * EdifCellInstance Object
     */
    public static EdifNet getInstancePortNet(EdifCell cell, EdifCellInstance instance, EdifPort port, int bit) {

        for (Iterator i = cell.netListIterator(); i.hasNext();) {
            EdifNet net = (EdifNet) i.next();
            if (isAttached(net, instance, port, bit))
                return net;
        }
        return null;
    }

    /* ESBP */
    public static EdifNet getInstancePortNet(EdifCell cell, EdifCellInstance instance, EdifSingleBitPort port) {

        for (Iterator i = cell.netListIterator(); i.hasNext();) {
            EdifNet net = (EdifNet) i.next();
            if (isAttached(net, instance, port))
                return net;
        }
        return null;
    }

    /**
     * FROM EDIFNET - MODIFIED TO WORK WITH MULTI-BIT EDIFPORTS This method will
     * return true if the given EdifPort&Bit/EdifCellInstance combination are
     * attached to the given EdifNet.
     * 
     * @param cell An EdifCellInstance Object to check if one of the
     * EdifPortRefs in this EdifNet Object connect to a port of that instance
     * @param port An EdifPort Object to check if one of the EdifPortRefs in
     * this EdifNet Object refer to that port
     * @return True if this EdifNet is attached to the passed-in
     * EdifPort/EdifCellInstance combination
     */
    public static boolean isAttached(EdifNet net, EdifCellInstance cell, EdifPort port, int portBit) {

        Iterator i = net.getConnectedPortRefs().iterator();
        while (i.hasNext()) {
            EdifPortRef epr = (EdifPortRef) i.next();
            if (epr.getCellInstance() == cell
                    && BasicEdifBusNetNamingPolicy.getBusBaseNameStatic(epr.getPort().getName()).toLowerCase().equals(
                            BasicEdifBusNetNamingPolicy.getBusBaseNameStatic(port.getName()).toLowerCase())
                    && epr.getBusMember() == portBit)
                return true;

        }
        return false;
    }

    /* ESBP */
    public static boolean isAttached(EdifNet net, EdifCellInstance cell, EdifSingleBitPort port) {

        Iterator i = net.getConnectedPortRefs().iterator();
        while (i.hasNext()) {
            EdifPortRef epr = (EdifPortRef) i.next();
            if (epr.getCellInstance() == cell
                    && BasicEdifBusNetNamingPolicy.getBusBaseNameStatic(epr.getPort().getName()).toLowerCase().equals(
                            BasicEdifBusNetNamingPolicy.getBusBaseNameStatic(port.getParent().getName()).toLowerCase())
                    &&
                    /* epr.getPort() == port && */
                    /* Checking the name instead - seems to work better */
                    epr.getBusMember() == port.bitPosition())
                return true;

        }
        return false;
    }

    /**
     * FROM EDIFNET - MODIFIED TO COMPARE CELL INSTANCE NAMES INSTEAD OF CELL
     * INSTANCE OBJECTS
     */
    public static boolean isAttached(EdifNet net, EdifCellInstance cell) {
        Iterator i = net.getConnectedPortRefs().iterator();
        while (i.hasNext()) {
            EdifPortRef epr = (EdifPortRef) i.next();
            if (epr.getCellInstance() != null) {
                if (epr.getCellInstance().getName().equals(cell.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method will iterate through all of the nets in cell and return those
     * nets that are connected to inst. This could be added to EdifCell.
     */
    public static Collection getAttachedNets(EdifCell cell, EdifCellInstance inst) {

        ArrayList list = new ArrayList();
        for (Iterator i = cell.getNetList().iterator(); i.hasNext();) {
            EdifNet net = (EdifNet) i.next();
            //if (net.isAttached(inst))
            if (isAttached(net, inst))
                list.add(net);
        }

        return list;
    }

    /*
     * This method removes all dangling Nets and unconnected CellInstances from
     * the passed EdifCell.
     */
    public static void cleanUpCell(EdifCell cell) {
        // Delete Dangling Nets
        Collection nets = cell.getDanglingNets();
        if (nets != null) {
            for (Iterator i = nets.iterator(); i.hasNext();) {
                EdifNet net = (EdifNet) i.next();
                if (net != null)
                    cell.deleteNet(net);
            }
        }
        // Delelte Unconnected Sub Cells
        Collection ecis = cell.getUnconnectedInstances();
        if (ecis != null) {
            for (Iterator i = ecis.iterator(); i.hasNext();) {
                EdifCellInstance inst = (EdifCellInstance) i.next();
                if (inst != null)
                    cell.deleteSubCell(inst);
            }
        }
    }

    private static int debug_level = 0;

}
