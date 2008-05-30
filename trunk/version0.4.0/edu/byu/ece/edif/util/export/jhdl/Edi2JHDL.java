/*
 * Generate a JHDL circuit from EDIF for the designated EdifCellInstance.
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
package edu.byu.ece.edif.util.export.jhdl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import byucc.jhdl.Logic.Logic;
import byucc.jhdl.Xilinx.gnd;
import byucc.jhdl.base.Cell;
import byucc.jhdl.base.CellInterface;
import byucc.jhdl.base.HelperLibrary;
import byucc.jhdl.base.Node;
import byucc.jhdl.base.Wire;
import edu.byu.ece.edif.core.BasicEdifBusNamingPolicy;
import edu.byu.ece.edif.core.BasicEdifBusNetNamingPolicy;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifTypedValue;
import edu.byu.ece.edif.core.NamedObjectCompare;
import edu.byu.ece.edif.core.StringTypedValue;

/**
 * This class is used to generate a JHDL circuit from an EDIF object. Generates
 * a JHDL circuit for the designated EdifCellInstance.
 * <p>
 * TO-DO:
 * <ol>
 * <li>Add property support
 * <li>Modify according to coding standard
 * </ol>
 * 
 * @author Welson Sun, Tyler Anderson
 * @version $Id$
 */
public class Edi2JHDL {

    /**
     * Constructs an Edi2JHDL Object with the specified name.
     * 
     * @param techName The technology for JHDL
     */
    public Edi2JHDL(String techName) {
        technology = techName;
    }

    /** Instances to Mapping of Ports to portRef array * */
    public Map createInstanceToPortRefArrayMapDebug(EdifCell parent, EdifCellInstance thisInst, boolean debug) {
        Map instToMap = new LinkedHashMap();
        Map portsToPortRefArray = new LinkedHashMap();
        instToMap.put(thisInst, portsToPortRefArray);
        if (debug) {
            System.out.println("MAKE MAP OF MAPS");
            System.out.println("\tINST: " + thisInst);
        }

        // iterate over all the ports of the given EdifCellInstance
        // - for each port create an array of EdifPortRefs - length equal to port width
        // - add the empty array to the 'portsToPortRefArray' Map:
        //	  key: EdifPort
        //    value: EdifPortRef array
        for (Iterator p = thisInst.getCellType().getPortList().iterator(); p.hasNext();) {
            EdifPort port = (EdifPort) p.next();
            EdifPortRef[] portRefArray = new EdifPortRef[port.getWidth()];
            portsToPortRefArray.put(port, portRefArray);
            if (debug) {
                System.out.println("\tPORT: " + port);
            }
        }

        // iterate over all the EdifCellInstances in the 'parent' heirarcy
        // - for each instance create a new LinkedHashMap called 'portsTOPortRefArray'
        // - add the instance to the 'instToMap' HashMap:
        //    key: EdifCellInstance
        //    value: 'portsToPortRefArray' HashMap
        for (Iterator c = parent.cellInstanceIterator(); c.hasNext();) {
            EdifCellInstance eci = (EdifCellInstance) c.next();
            portsToPortRefArray = new LinkedHashMap();
            instToMap.put(eci, portsToPortRefArray);
            if (debug) {
                System.out.println("\tECI: " + eci);
            }

            // iterate over all the ports of the current EdifCellInstance
            // - for each port create an array of EdifPortRefs - length equal to port width
            // - add the empty array to the 'portsToPortRefArray' Map:
            //	  key: EdifPort
            //    value: EdifPortRef array
            for (Iterator p = eci.getCellType().getPortList().iterator(); p.hasNext();) {
                EdifPort port = (EdifPort) p.next();
                EdifPortRef[] portRefArray = new EdifPortRef[port.getWidth()];
                portsToPortRefArray.put(port, portRefArray);

                if (debug) {
                    System.out.println("\t\tECI PORT: " + port);
                }
            }
        }
        // Loop over all nets
        //     Loop over each port ref
        //         Loop over each port
        //             When a portRef's port matches the above
        //             port then add to the mapping

        // Iterate over all the EdifNets in the 'parent' hierarchy
        for (Iterator n = parent.netListIterator(); n.hasNext();) {
            EdifNet net = (EdifNet) n.next();

            if (debug) {
                System.out.println("\tNET: " + net);
            }

            // Iterate over all the EdifPortRefs associated with the current net
            for (EdifPortRef epr : net.getConnectedPortRefs()) {

                // grab the EdifCellInstance associated with the current portRef
                EdifCellInstance eci = epr.getCellInstance();

                if (debug) {
                    System.out.println("\t\tEPR: " + epr);
                    System.out.println("\t\tECI: " + eci);
                }

                // If the current instance is null - make it the 'given' instance
                // get the 'portsToPortRefArray' Map for the current instance
                if (eci == null)
                    eci = thisInst;
                portsToPortRefArray = (Map) instToMap.get(eci);

                if (debug) {
                    System.out.println("\t\t\tMAP KEYS: " + portsToPortRefArray.keySet());
                    System.out.println("\t\t\tMAP VALS: ");

                    for (Iterator valIt = portsToPortRefArray.values().iterator(); valIt.hasNext();) {
                        EdifPortRef[] portref = (EdifPortRef[]) valIt.next();
                        for (int i = 0; i < portref.length; i++) {
                            System.out.println("\t\t\t\t" + i + " " + portref[i]);
                        }
                    }
                }

                // Iterate over all the EdifPorts for the current EdifCellInstance
                // - This loop is necessary since the EdifPorts which are the keys to
                //   the 'portsToPortRefArray' are DIFFERENT EdifPorts than the ones
                //   grabbed from the current EdifPortRef
                //   - else we could just grab the EdifSingleBitPort from the current
                //     EdifPortRef and add that to the appropriate location in the
                //     EdifPortRef array
                // NOTE: there should always be a match after ending this loop

                boolean found = false;
                for (Iterator p = eci.getCellType().getPortList().iterator(); p.hasNext();) {
                    EdifPort port = (EdifPort) p.next();

                    // Grab the EdifPortRef array from the 'portsToPortRefArray' Map 
                    EdifPortRef[] portRefArray = (EdifPortRef[]) portsToPortRefArray.get(port);

                    if (debug) {
                        System.out.println("\t\t\tPORT: " + port);
                        for (int i = 0; i < portRefArray.length; i++) {
                            System.out.println("\t\t\t\t" + i + " " + portRefArray[i]);
                        }
                    }

                    // If the current EdifPortRef's EdifPort matches the EdifPort
                    // in the current loop iteration - we have a match
                    // Add the current EdifPortRef to the appropriate EdifPortRef array
                    // location
                    //                    if(port == epr.getPort()){
                    if (port.getName().equalsIgnoreCase(epr.getPort().getName())
                            && port.getDirection() == epr.getPort().getDirection()) {
                        found = true;
                        int bm = epr.getBusMember();
                        if (debug) {
                            System.out.println("\t\t\tBUS MEMBER: " + bm);
                        }
                        if (bm < 0)
                            bm = 0;

                        // actually add the EdifPortRef to the array
                        portRefArray[bm] = epr;
                        break;
                    } else if (debug) {
                        System.out.println("\t\t\tNOT EQUAL: " + epr.getPort());
                        System.out.println("\t\t\t\tEPR INTERFACE: " + epr.getPort().getName());
                        System.out.println("\t\t\t\tport INTERFACE: " + port.getName());
                    }
                }
                if (!found) {
                    System.out.println("ERROR: NO PORT MATCH FOUND");
                    System.exit(-1);
                }
            }
        }

        return instToMap;
    }

    /**
     * This method creates a mapping between an EdifCellInstance and all its
     * corresponding EdifPortRefs. The Returned Map has
     * <ul>
     * <li>key: EdifCellInstance
     * <li>value: Map
     * </ul>
     * <p>
     * For each key a Map value is returned. This map has
     * <ul>
     * <li> key: EdifPort
     * <li> value: EdifPortRef[]
     * </ul>
     * 
     * @param parent - the parent EdifCell
     * @param mapInst - the EdifCellInstance to create the Map from
     * @return
     */
    public Map createInstanceToPortRefArrayMap(EdifCell parent, EdifCellInstance mapInst) {
        Map instToMap = new LinkedHashMap();
        Map portsToPortRefArray = new LinkedHashMap();
        instToMap.put(mapInst, portsToPortRefArray);
        for (Iterator p = mapInst.getCellType().getPortList().iterator(); p.hasNext();) {
            EdifPort port = (EdifPort) p.next();
            EdifPortRef[] portRefArray = new EdifPortRef[port.getWidth()];
            portsToPortRefArray.put(port, portRefArray);
        }

        for (Iterator c = parent.cellInstanceIterator(); c.hasNext();) {
            EdifCellInstance eci = (EdifCellInstance) c.next();
            portsToPortRefArray = new LinkedHashMap();
            instToMap.put(eci, portsToPortRefArray);

            for (Iterator p = eci.getCellType().getPortList().iterator(); p.hasNext();) {
                EdifPort port = (EdifPort) p.next();
                EdifPortRef[] portRefArray = new EdifPortRef[port.getWidth()];
                portsToPortRefArray.put(port, portRefArray);
            }
        }
        // Loop over all nets
        //     Loop over each port ref
        //         Loop over each port
        //             When a portRef's port matches the above
        //             port then add to the mapping
        for (Iterator n = parent.netListIterator(); n.hasNext();) {
            EdifNet net = (EdifNet) n.next();

            for (EdifPortRef epr : net.getConnectedPortRefs()) {

                EdifCellInstance eci = epr.getCellInstance();
                if (eci == null)
                    eci = mapInst;
                portsToPortRefArray = (Map) instToMap.get(eci);

                for (Iterator p = eci.getCellType().getPortList().iterator(); p.hasNext();) {
                    EdifPort port = (EdifPort) p.next();
                    EdifPortRef[] portRefArray = (EdifPortRef[]) portsToPortRefArray.get(port);

                    //if(port == epr.getPort()){
                    if (port.getName().equalsIgnoreCase(epr.getPort().getName())
                            && port.getDirection() == epr.getPort().getDirection()) {
                        int bm = epr.getBusMember();
                        if (bm < 0)
                            bm = 0;
                        portRefArray[bm] = epr;
                    }
                }
            }
        }

        return instToMap;
    }

    /**
     * This method will search through the EdifPorts of two EdifCells and looks
     * for ports with the same 'base name'. A HashMap is returned with the
     * EdifPorts that are similar between the two EdifCells. The HashMap that is
     * returned organizes the similar ports. The HashMap has:
     * <ul>
     * <li>key: xilinxCell EdifPort
     * <li>value: ArrayList of actualCell EdifPorts
     * </ul>
     * <p>
     * This HashMap links the single multi-bit ports with the multiple
     * single-bit ports.
     * 
     * @param xilinxCell - 'xilinx' EdifCell
     * @param actualCell - 'actual' EdifCell
     * @return a HashMap linking similar ports
     */
    public HashMap getMatchingPorts(EdifCell xilinxCell, EdifCell actualCell) {
        HashMap retval = new LinkedHashMap();

        for (Iterator portIt1 = xilinxCell.getPortList().iterator(); portIt1.hasNext();) {
            EdifPort port1 = (EdifPort) portIt1.next();
            String basePort1 = BasicEdifBusNamingPolicy.getBusBaseNameStatic(port1.getName());

            for (Iterator portIt2 = actualCell.getPortList().iterator(); portIt2.hasNext();) {
                EdifPort port2 = (EdifPort) portIt2.next();
                String basePort2 = BasicEdifBusNetNamingPolicy.getBusBaseNameStatic(port2.getName());

                if (basePort1.equalsIgnoreCase(basePort2)) {
                    ArrayList portlist = (ArrayList) retval.get(port1);
                    if (portlist == null) {
                        portlist = new ArrayList();
                    }
                    portlist.add(port2);
                    retval.put(port1, portlist);
                }
            }
        }

        return retval;
    }

    /**
     * This method compares EdifPorts from two EdifCells to determine if they
     * are the same.
     * 
     * @param xilinxCell - first EdifCell
     * @param actualCell - second EdifCell
     * @return - true if both have the same EdifPorts, else false
     */
    public boolean isAlmostSameEdifCell(EdifCell xilinxCell, EdifCell actualCell) {
        boolean retval = false;

        Map portMap = getMatchingPorts(xilinxCell, actualCell);

        if (portMap != null) {
            retval = true;
            for (Iterator portIt = xilinxCell.getPortList().iterator(); portIt.hasNext();) {
                EdifPort port = (EdifPort) portIt.next();
                if (!portMap.keySet().contains(port)) {
                    retval = false;
                    break;
                }
            }
        }
        return retval;
    }

    /**
     * This method compares EdifPorts from two EdifCells to determine if they
     * are the same.
     * 
     * @param xilinxCell - first EdifCell
     * @param actualCell - second EdifCell
     * @return - true if both have the same EdifPorts, else false
     */
    public boolean isSameEdifCell(EdifCell xilinxCell, EdifCell actualCell) {
        boolean retval = false;
        ArrayList xilinxPorts = (ArrayList) xilinxCell.getPortList();
        ArrayList actualPorts = (ArrayList) actualCell.getPortList();

        if (xilinxPorts.size() == actualPorts.size()) {
            for (Iterator portIt = xilinxPorts.iterator(); portIt.hasNext();) {
                EdifPort port = (EdifPort) portIt.next();
                String xilinxPortName = port.getName().toUpperCase();

                retval = false;
                for (Iterator portIt2 = actualPorts.iterator(); portIt2.hasNext();) {
                    EdifPort port2 = (EdifPort) portIt2.next();
                    String actualPortName = port2.getName().toUpperCase();

                    /**
                     * Comparing port names is insufficient. In some cases a
                     * match is NOT found when one should be. For example (taken
                     * from actual case):
                     * <ul>
                     * <li>xilinxPortName = DOPA
                     * <li> actualPortName = DOPA_0
                     * </ul>
                     * <p>
                     * To fix this problem, we will use 'startsWith()' instead
                     * of 'equals()'
                     */
                    if (actualPortName.startsWith(xilinxPortName)) {
                        retval = true;
                        break;
                    }
                }
                if (!retval) {
                    System.out.println("CELLS NOT SAME 2: " + port);
                    System.out.println("\t" + actualPorts);
                    break;
                }
            }
        }
        return retval;
    }

    /**
     * This method will change a name to a JHDL qualified name.
     * 
     * @param id The String to make JHDL-valid
     * @return A JHDL-valid String
     */
    public static String JHDL_ID(String id) {
        return byucc.jhdl.base.Util.makeLegalJHDLIdentifier(id).toLowerCase();
    }

    public Cell toJHDLCircuit(Cell parent, EdifCellInstance cellInstance, Wire[] portWires) {
        return toJHDLCircuit(parent, cellInstance, portWires, false);
    }

    /**
     * This method will create a JHDL Cell for an EdifCellInstance object, as a
     * sub JHDL Cell of the "parent" JHDL Cell, and connect all the passed in
     * JHDL Wires to the created JHDL Cell's ports, and finally, it will return
     * this created JHDL Cell.
     * 
     * @param parent a <code>Cell</code>
     * @param cellInstance an <code>EdifCellInstance</code>
     * @param portWires a <code>Wire[]</code>
     * @return a <code>Cell</code>
     */
    public Cell toJHDLCircuit(Cell parent, EdifCellInstance cellInstance, Wire[] portWires,
            boolean addEdifObjectAsProperty) {

        /**
         * NR - START
         * <p>
         * Solving Problem: Multiple single-bit ports in EDIF need to be a
         * single multiple-bit port for JHDL constructors
         * <p>
         * This problem only appears with BRAMs
         * <p>
         * Check the passed-in EdifCell to see if it's a BRAM type
         * <ul>
         * <li>instCell - passed-in EdifCell
         * <li>xilinxCell - xilinx EdifCell (if passed-in is a BRAM type) or
         * passed-in EdifCell
         * <li>Map one2many -
         * <ul>
         * <li>key: xilinxCell EdifPort
         * <li>value: ArrayList of instCell EdifPorts
         * </ul>
         * </ul>
         */
        EdifCell xilinxCell = cellInstance.getCellType();
        EdifCell instCell = cellInstance.getCellType();
        EdifLibrary xilinxLib = edu.byu.ece.edif.arch.xilinx.XilinxLibrary.library;
        boolean samePorts = true;
        boolean debug = false;

        if ((cellInstance.getType().toUpperCase().startsWith("RAMB") || cellInstance.getType().toUpperCase()
                .startsWith("MULT18"))) {
            xilinxCell = xilinxLib.getCell(cellInstance.getType());
        }
        if (!isSameEdifCell(xilinxCell, instCell)) {
            //if (xilinxCell != instCell) {
            if (!isAlmostSameEdifCell(xilinxCell, instCell)) {
                System.err.println("ERROR: conflicting cells - " + instCell.getInterface() + " AND "
                        + xilinxCell.getInterface());
                System.exit(-1);
            }
            samePorts = false;
        }
        //if (instCell.getName().equalsIgnoreCase("buffer_comp"))
        //	debug = true;

        Map one2many = getMatchingPorts(xilinxCell, instCell);
        /*
         * NR -END
         */

        /*
         * Step 1. Create the CellInterface[] The relation between portList of
         * the cellInstance, CellInterface[] and portWires is: Each EdifPort in
         * the cellInstance's portList will create a corresponding CellInstance,
         * and the caller should create a one to one Wire array according to
         * this portList
         * 
         * NR - iterate over xilinxCell ports instead of instCell ports
         */

        CellInterface[] jhdlCellInterface = null;
        int i = 0;
        Iterator it = null;
        int portlength = (portWires == null) ? 0 : portWires.length;

        if (portWires != null) {
            jhdlCellInterface = new CellInterface[portWires.length];

            it = xilinxCell.getSortedPortList().iterator();
            while (it.hasNext()) {
                EdifPort port = (EdifPort) it.next();
                switch (port.getDirection()) {
                case EdifPort.IN:
                    jhdlCellInterface[i] = Cell.in(JHDL_ID(port.getName()), port.getWidth());
                    break;
                case EdifPort.OUT:
                    jhdlCellInterface[i] = Cell.out(JHDL_ID(port.getName()), port.getWidth());
                    break;
                case EdifPort.INOUT:
                    jhdlCellInterface[i] = Cell.inout(JHDL_ID(port.getName()), port.getWidth());
                    break;
                }
                i++;
            }
        }

        /*
         * Step 2. Create the JHDL Cell
         */
        String jhdlInstanceName = cellInstance.getOldName();
        Cell jhdlInstance = parent.pushHierarchyNoImplicitPorts(jhdlCellInterface, JHDL_ID(xilinxCell.getClass()
                .toString()), true, JHDL_ID(jhdlInstanceName));

        /*
         * Step 3. Connect the passed in "Wire[] portWires" with the ports
         * 
         * NR - iterate over xilinxCell ports instead of instCell ports
         * 
         */
        if (portWires != null) {
            i = 0;
            it = xilinxCell.getSortedPortList().iterator();
            while (it.hasNext()) {
                EdifPort port = (EdifPort) it.next();
                jhdlInstance.connect(JHDL_ID(port.getName()), portWires[i]);
                i++;
            }
        }

        /*
         * Step 4. Create the internal wires The internal wires correspond to
         * the nets inside an EdifCell, which is made up of two parts:
         * 
         * 1. If a net connects to the parent Cell's port, we just get the wire
         * from the passed in portWires[] 2. If a net ONLY connects to the
         * parent Cell's subcells, we will create a new Wire
         */
        HashMap internalWires = new LinkedHashMap();

        //Iterate each net
        it = instCell.getNetList().iterator();
        while (it.hasNext()) {
            EdifNet net = (EdifNet) it.next();

            //Check if the net is connected to the parent Cell's port,
            //if is, record the EdifPortRef
            boolean connectParentPort = false;
            EdifPortRef portRefToParentPort = null;
            Iterator itPortRef = net.getConnectedPortRefs().iterator();
            while (itPortRef.hasNext()) {
                EdifPortRef portRef = (EdifPortRef) itPortRef.next();
                EdifPort parentPortRefPort = null;
                EdifPort portRefPort = portRef.getPort();

                if (portRef.getCellInstance() == null) {

                    // Case for when two top level ports are connected
                    // to each other
                    if (connectParentPort) {
                        Collection ports = xilinxCell.getSortedPortList();
                        ArrayList arrayPorts = new ArrayList(ports);

                        // We already have the first top level port
                        // from the previous iteration. Now we will
                        // find the index of both within the portWires
                        // array.
                        int prBM = portRef.getBusMember();
                        int prtpBM = portRefToParentPort.getBusMember();
                        int prtp = 0, pr = 0;
                        boolean prtpHave = false, prHave = false;

                        /*
                         * NR -START
                         */
                        if (!samePorts) {
                            for (i = 0; i < portlength; i++) {
                                EdifPort port = (EdifPort) arrayPorts.get(i);
                                ArrayList singlePorts = (ArrayList) one2many.get(port);
                                if (singlePorts.contains(parentPortRefPort) && !prtpHave) {
                                    prtpHave = true;
                                    prtp = i;
                                    prtpBM = singlePorts.indexOf(parentPortRefPort);
                                }
                                if (singlePorts.contains(portRefPort) && !prHave) {
                                    prHave = true;
                                    pr = i;
                                    prBM = singlePorts.indexOf(portRefPort);
                                }
                            }
                        }
                        /*
                         * NR -END
                         */

                        else {
                            for (i = 0; i < portlength; i++) {
                                EdifPort port = (EdifPort) arrayPorts.get(i);
                                if (portRefToParentPort.getPort() == port && !prtpHave) {
                                    prtpHave = true;
                                    prtp = i;
                                }
                                if (portRef.getPort() == port && !prHave) {
                                    prHave = true;
                                    pr = i;
                                }
                            }
                        }

                        // Retrieve the busMember of each port ref,
                        // and join the appropriate wires.

                        Wire w1, w2;
                        if (prBM == -1 && prtpBM == -1) {
                            w1 = portWires[pr];
                            w2 = portWires[prtp];
                            portWires[pr] = portWires[prtp];
                        } else if (prBM != -1 && prtpBM == -1) {
                            w1 = portWires[pr].gw(prBM);
                            w2 = portWires[prtp];
                            portWires[prtp] = portWires[pr].gw(prBM);
                        } else if (prBM == -1 && prtpBM != -1) {
                            w1 = portWires[pr];
                            w2 = portWires[prtp].gw(prtpBM);
                            portWires[pr] = portWires[prtp].gw(prtpBM);
                        } else {
                            w1 = portWires[pr].gw(prBM);
                            w2 = portWires[prtp].gw(prtpBM);
                        }
                        if (w1 != w2) {
                            jhdlInstance.join(w1, w2);
                        }
                    }

                    connectParentPort = true;
                    portRefToParentPort = portRef;
                    parentPortRefPort = portRefToParentPort.getPort();
                    //break;
                }
            }

            //Because the order of the portWires[] is the same as the order 
            //of the ports of the CellInstance, we first find the EdifPort
            //that the net is linking with, by the same index, we can get
            //the Wire in the portWires[]
            //
            // TODO: fix the top-level port busmember bug
            if (connectParentPort) {
                Collection ports = xilinxCell.getSortedPortList();
                ArrayList arrayPorts = new ArrayList(ports);

                for (i = 0; i < portWires.length; i++) {
                    EdifPort port = (EdifPort) arrayPorts.get(i);

                    /*
                     * NR -START
                     */
                    if (!samePorts) {
                        ArrayList singlePorts = (ArrayList) one2many.get(port);
                        boolean isSame = false;
                        if (portRefToParentPort != null)
                            isSame = singlePorts.contains(portRefToParentPort.getPort());
                        if (isSame) {
                            int busMember = singlePorts.indexOf(portRefToParentPort);
                            if (busMember != -1) {
                                Wire currWire = portWires[i].gw(busMember);
                                EdifNet n = portRefToParentPort.getNet();
                                addNetProperties(n, currWire, addEdifObjectAsProperty);
                                internalWires.put(net.getName(), currWire);
                            } else {
                                EdifNet n = cellInstance.getPortRef(
                                        portRefToParentPort.getPort().getSingleBitPort(busMember)).getNet();
                                addNetProperties(n, portWires[i], addEdifObjectAsProperty);
                                internalWires.put(net.getName(), portWires[i]);
                            }
                        }
                    }
                    /*
                     * NR -END
                     */

                    else {
                        if (port == portRefToParentPort.getPort()) {
                            int busMember = portRefToParentPort.getBusMember();
                            if (busMember != -1) {
                                Wire currWire = portWires[i].gw(busMember);
                                EdifNet n = portRefToParentPort.getNet();
                                addNetProperties(n, currWire, addEdifObjectAsProperty);
                                internalWires.put(net.getName(), currWire);
                            } else {
                                EdifNet n = cellInstance.getPortRef(port.getSingleBitPort(busMember)).getNet();
                                addNetProperties(n, portWires[i], addEdifObjectAsProperty);
                                internalWires.put(net.getName(), portWires[i]);
                            }
                        }
                    }
                }
            }

            //The net only connects the sub cells
            else {
                Wire wire = Logic.wire(jhdlInstance, JHDL_ID(net.getName()));
                addNetProperties(net, wire, addEdifObjectAsProperty);
                internalWires.put(net.getName(), wire);
                if (debug)
                    System.out.println("NET: " + net + "  WIRE: " + wire);
            }
        }

        /*
         * Step 5. Connect the sub cells
         */
        //Iterate each sub cell
        if (instCell.getSubCellList().size() != 0) {

            Map instToMap = createInstanceToPortRefArrayMap(instCell, cellInstance);
            //Map instToMap = createInstanceToPortRefArrayMapDebug(instCell, cellInstance, true);

            it = instCell.getSubCellList().iterator();
            while (it.hasNext()) {
                EdifCellInstance subcell = (EdifCellInstance) it.next();

                //Create the arrayList of Wire to connect with the
                //ports of the sub cell
                ArrayList subCellWires = new ArrayList();

                createSubCellWires(subcell,//The current sub cell
                        cellInstance,//The current cell
                        subCellWires,//The Wire arrayList
                        jhdlInstance,//The JHDL Cell of cellInstance
                        internalWires,//The base of the subCellWires
                        instToMap);

                if (subcell.getCellType().isLeafCell()) {
                    if (debug)
                        System.out.println("LEAFCELL: " + subcell);
                    createLeafCell(jhdlInstance, subcell, subCellWires, addEdifObjectAsProperty);
                } else {
                    if (debug)
                        System.out.println("NON-LEAFCELL: " + subcell);
                    Wire[] sub_portWires = new Wire[subCellWires.size()];
                    for (int j = 0; j < subCellWires.size(); j++) {
                        sub_portWires[j] = (Wire) subCellWires.get(j);
                    }
                    toJHDLCircuit(jhdlInstance, subcell, sub_portWires, addEdifObjectAsProperty);
                }
            }//End while
        }

        //** Step 6. Add properties
        addCellInstanceProperties(cellInstance, jhdlInstance, addEdifObjectAsProperty);

        //** Step 7. Pop out the sub cell.
        parent.popHierarchy();

        return jhdlInstance;
    }

    /**
     * Add properties to the JHDL Cell according to the EdifCellInstance. It
     * retrieves the properties from the EdifCellInstance and adds them to the
     * JHDL Cell as well.
     * 
     * @param cellInst Takes all of this EdifCell's Property Objects, and adds
     * them to the JHDL Cell
     * @param cell The JHDL Cell that will get all of the EdifCell's Properties
     * @param addEdifCellInstanceAsProperty If true, this flag adds a property
     * to the JHDL cell whose key is held by the string {@link
     * #EDIF_CELL_INSTANCE_PROPERTY}, and whose value is an EdifCellInstance.
     */
    protected void addCellInstanceProperties(EdifCellInstance cellInst, Cell cell, boolean addEdifCellInstanceAsProperty) {
        if (addEdifCellInstanceAsProperty)
            cell.addProperty(EDIF_CELL_INSTANCE_PROPERTY, cellInst);
        if (cellInst.getPropertyList() == null)
            return;
        Iterator it = cellInst.getPropertyList().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry propertyPair = (Map.Entry) it.next();
            String key = (String) propertyPair.getKey();
            edu.byu.ece.edif.core.Property propertyValue = (edu.byu.ece.edif.core.Property) propertyPair.getValue();
            EdifTypedValue value = (EdifTypedValue) propertyValue.getValue();
            String strValue = value.toString();
            if (strValue != null)
                cell.replaceProperty(key, strValue);
        }
    }

    protected void addNetProperties(EdifNet net, Wire wire, boolean addEdifCellInstanceAsProperty) {
        if (addEdifCellInstanceAsProperty)
            HelperLibrary.addPropertyToTopWire(wire, Edi2JHDL.EDIF_NET_PROPERTY, net);
        if (net.getPropertyList() == null)
            return;
        for (Iterator it = net.getPropertyList().entrySet().iterator(); it.hasNext();) {
            Map.Entry propertyPair = (Map.Entry) it.next();

            String key = (String) propertyPair.getKey();
            edu.byu.ece.edif.core.Property propertyValue = (edu.byu.ece.edif.core.Property) propertyPair.getValue();
            EdifTypedValue value = (EdifTypedValue) propertyValue.getValue();
            String strValue = value.toString();
            if (strValue != null)
                wire.replaceProperty(key, strValue);
        }
    }

    /**
     * Create a black box for the EDIF cell that is not in the JHDL library.
     * This is done by Creating an empty JHDL Cell, {@link EdifBlackBoxCell},
     * inserting the ports, and connecting them to wires. On each clock, this
     * black box cell will put 0s on each of it's out/inout wires.
     * 
     * @param parent The parent cell to the black box cellInstance
     * @param cellInstance The EdifCellInstance referring to a black box
     * EdifCell
     * @param portWires An ArrayList of Wire Objects
     * @return A JHDL representation of the EdifCellInstance
     */
    protected Cell createBlackBoxModel(Cell parent, EdifCellInstance cellInstance, Wire[] portWires,
            boolean addEdifCellInstanceAsProperty) {

        // Create empty cell
        EdifBlackBoxCell newcell = new EdifBlackBoxCell(parent, JHDL_ID(cellInstance.getName()));

        // Add ports
        Iterator it = cellInstance.getCellType().getSortedPortList().iterator();
        while (it.hasNext()) {
            EdifPort port = (EdifPort) it.next();
            CellInterface ci = null;
            switch (port.getDirection()) {
            case EdifPort.IN:
                ci = Cell.in(JHDL_ID(port.getName()), port.getWidth());
                break;
            case EdifPort.OUT:
                ci = Cell.out(JHDL_ID(port.getName()), port.getWidth());
                break;
            case EdifPort.INOUT:
                ci = Cell.inout(JHDL_ID(port.getName()), port.getWidth());
                break;
            }
            newcell.addPort(ci);
        }

        // Connect Wires to ports
        int i = 0;
        it = cellInstance.getCellType().getSortedPortList().iterator();
        while (it.hasNext()) {
            EdifPort port = (EdifPort) it.next();
            newcell.connect(port, portWires[i]);
            i++;
        }
        if (i == 0)
            System.err.println("WARNING: Cells without ports are not viewable in JHDL!");

        addCellInstanceProperties(cellInstance, newcell, addEdifCellInstanceAsProperty);
        return newcell;
    }

    /**
     * Create the leaf cell as the sub cell of the parent cell. This is done by:
     * <ol>
     * <li>Collecting initialization properties for block rams, flip flops
     * luts, rams, and roms
     * <li>Retrieve the class to load
     * <li>Retrieve the class and object array for obtaining and initializing
     * the constructor
     * <li>Retrieve the INIT and INIT_XX properties for the various memories
     * <li>Fill in the class and object arrays with the arguments to pass to
     * the constructor for JHDL class initialization
     * <li>Finally, retrieve and initialize the JHDL class
     * </ol>
     * 
     * @param parent The parent of the created leaf cell
     * @param leafCell The EdifCellInstance that represents the leafCell
     * @param wires An ArrayList of Wire Objects
     */
    protected void createLeafCell(Cell parent, EdifCellInstance leafCell, ArrayList wires,
            boolean addEdifCellInstanceAsProperty) {
        // The jhdl cell to load
        Cell jhdlCell = null;
        // The class (cell type) to instantiate
        Class loadClass = null;

        // Name of the "JHDL" class to instantiate
        String className = JHDL_ID(leafCell.getType());

        // Argument size of the constructor to use for instantiation
        int argSize = argSize = 2 * wires.size() + 2;
        // Memory depth for memories (flip flops and lets get one,
        // some rams and roms get one, and the others get more than one
        int memDepth = -1;

        // RAMB16_S potentially have parity INIT strings
        int parityDepth = -1;

        // The class name of the BRAMs (because JHDL treats them different)
        String rambStr = null;

        // Default initialization Strings for the memories
        String initDefault = null;
        // Default initialization Strings for BRAMs
        String rambInitDefault = "00000000000000000000000000000000" + "00000000000000000000000000000000";

        /** Special cases for memory initialization */

        // lut
        if (className.startsWith("lut")) {
            initDefault = "0";
            // The # of inputs for the lut
            int lutInputNo = className.charAt(3) - '0';
            int initNo = (int) Math.pow(2, lutInputNo) / 4 - 1;
            for (int i = 0; i < initNo; initDefault += "0", i++)
                ;
            argSize = 2 * wires.size() + 3;
            memDepth = 1;
        }
        // Virtex2 RAMB16
        else if (className.startsWith("ramb16")) {
            initDefault = rambInitDefault;
            argSize = 2 * wires.size() + 3;
            memDepth = 64;

            // check to see if we have parity bit ports
            if ((className.toUpperCase().indexOf("S9") > -1) || (className.toUpperCase().indexOf("S18") > -1)
                    || (className.toUpperCase().indexOf("S36") > -1)) {
                parityDepth = 8;
                argSize++;
            }

            // Check to see if this BRAM is dual ported or not
            if (className.indexOf("_s") != className.lastIndexOf("_s"))
                rambStr = "RAMB16_S_S";
            else
                rambStr = "RAMB16_S";
        }
        // Virtex RAMB4
        else if (className.startsWith("ramb4")) {
            initDefault = rambInitDefault;
            argSize = 2 * wires.size() + 3;
            // RAMB4 doesn't exist in Virtex2, so they must be
            // repackaged as a RAMB16. The RAMB16 constructor will tie
            // the upper, unused address lines to gnd.
            if (technology.equals("Virtex2")) {
                memDepth = 64;

                // check to see if we have parity bit ports
                if ((className.toUpperCase().indexOf("S9") > -1) || (className.toUpperCase().indexOf("S18") > -1)
                        || (className.toUpperCase().indexOf("S36") > -1)) {
                    parityDepth = 8;
                    argSize++;
                }

                // Check to see if this BRAM is dual ported or not
                if (className.indexOf("_s") != className.lastIndexOf("_s"))
                    rambStr = "RAMB16_S_S";
                else
                    rambStr = "RAMB16_S";
                System.err.println("NOTICE: Virtex RAMB4 is being " + "retargetted to Virtex2 RAMB16. This "
                        + "will waste 75% of Virtex2 RAMB16 " + "capacity.");
            } else {
                memDepth = 16;
                // Check to see if this BRAM is dual ported or not
                if (className.indexOf("_s") != className.lastIndexOf("_s"))
                    rambStr = "RAMB4Dual";
                else
                    rambStr = "RAMB4Single";
            }
        }
        // Flip Flop
        else if (className.startsWith("fd") || (className.startsWith("ifd") && !className.startsWith("ifddr"))
                || (className.startsWith("ofd") && !className.startsWith("ofddr"))) {
            argSize = 2 * wires.size() + 3;
            memDepth = 1;
            initDefault = "R";
        }
        // ramMxN and romMxNs
        else if (className.startsWith("ram") || className.startsWith("rom")) {
            initDefault = parseMemMxNPropertyLength(leafCell.getCellType().getName());
            argSize = 2 * wires.size() + 3;
            memDepth = parseMemDepth(leafCell.getCellType().getName());
        }

        /* Retrieve the class */

        // Class name should be had by now, so retrieve the class
        try {
            // If a block ram string was specified, then load the bram
            if (rambStr != null) {
                loadClass = Class.forName("byucc.jhdl.Xilinx." + technology + "." + rambStr);
                //System.out.println("RAM: "+rambStr);
                // Otherwise, just use the classname, as the other JHDL
                // classes are used similarly
            } else
                loadClass = Class.forName("byucc.jhdl.Xilinx." + technology + "." + className);
            //System.out.println("CLASS: "+loadClass);
        } catch (ClassNotFoundException e3) {
            // If the class cannot be found then create a black box

            System.err.println("\nWARNING: " + leafCell.getType() + " is a leaf cell " + "but no corresponding JHDL "
                    + "class for it, create a " + "black box here.");
            //Create black box
            Wire[] sub_portWires = new Wire[wires.size()];
            for (int j = 0; j < wires.size(); j++)
                sub_portWires[j] = (Wire) wires.get(j);

            createBlackBoxModel(parent, leafCell, sub_portWires, addEdifCellInstanceAsProperty);
            parent.popHierarchy();
            return;
        }

        /* Objects for Constructor retrieval and class instantiation */

        // Objects used to find and instantiate the constructor for
        // the loaded class
        Object[] args = new Object[argSize];
        Class[] arg_class = new Class[argSize];
        Constructor loadClassConstructor = null;

        // Set the known parent and name for the cell to instantiate
        args[0] = parent;
        args[1] = JHDL_ID(leafCell.getName());
        // Set the class of these arguments
        arg_class[0] = Node.class;
        arg_class[1] = String.class;

        /* Memory initialization retrieval */

        // Memory initialization retrieval
        int initPos = argSize - 1;
        String initString;

        if (memDepth > 0) {
            if (parityDepth > 0) {
                initString = "INITP";
                _getPropertyINITString(leafCell, initDefault, initString, argSize, initPos, parityDepth, arg_class,
                        args);
                initPos--;
            }
            initString = "INIT";
            _getPropertyINITString(leafCell, initDefault, initString, argSize, initPos, memDepth, arg_class, args);
        }

        /* Argument/class type collection */

        /*
         * NR - START<p> <p>Solving Problem: Multiple single-bit ports in EDIF
         * need to be a single multiple-bit port for JHDL constructors
         * 
         * <p>This problem only appears with BRAMs <p>Check the passed-in
         * EdifCell to see if it's a BRAM type <ul><li>instCell - passed-in
         * EdifCell <li>xilinxCell - xilinx EdifCell (if passed-in is a BRAM
         * type) or passed-in EdifCell</ul>
         */
        EdifLibrary xilinxLib = edu.byu.ece.edif.arch.xilinx.XilinxLibrary.library;
        EdifCell instCell = leafCell.getCellType();
        EdifCell xilinxCell = leafCell.getCellType();

        if ((leafCell.getType().toUpperCase().startsWith("RAMB") || leafCell.getType().toUpperCase().startsWith(
                "MULT18"))) {
            xilinxCell = xilinxLib.getCell(leafCell.getType());
            //System.out.println("XIL: "+xilinxCell+"  INST: "+instCell);
        }
        //if (xilinxCell != instCell) {
        if (!isSameEdifCell(xilinxCell, instCell)) {
            if (!isAlmostSameEdifCell(xilinxCell, instCell)) {
                System.err.println("ERROR: conflicting cells - " + instCell.getInterface() + " AND "
                        + xilinxCell.getInterface());
                System.exit(-1);
            }
        }
        /*
         * NR -END
         */

        /*
         * Collect the rest of the arguments to instantiate the class, as well
         * as set the class type of each argument
         */
        Iterator it = xilinxCell.getSortedPortList().iterator();
        int i = 1;
        while (it.hasNext()) {
            EdifPort port = (EdifPort) it.next();
            arg_class[2 * i] = String.class;
            arg_class[2 * i + 1] = Wire.class;
            String str;

            /*
             * Collect because you don't want to send all base names to the
             * constructor or your ports won't match up in the JHDL constructor.
             */

            //	        Collection ports = (port.getCellInterface().findSinglePortsWithMatchingBaseName(port.getBaseName()));
            //            if (ports != null && ports.size() == 1)
            //                str = JHDL_ID(port.getBaseName());
            //            else
            str = JHDL_ID(port.getName());
            Object obj = wires.get(i - 1);
            args[2 * i] = str;
            args[2 * i + 1] = (Wire) obj;
            i++;
        }

        /** * Retrieve constructor and instantiate instance of JHDL Cell ** */

        // Retrieve the constructor of the class using the class types
        // of each argument. Then, instantiate the JHDL Cell using
        // this constructor, passing in the collected arguments.
        try {

            //	        if (rambStr != null && rambStr.equalsIgnoreCase("RAMB16_S_S"))
            //                for (int j = 0; j < arg_class.length; j++)
            //                    System.out.println("    " + arg_class[j] + " " + args[j]);

            loadClassConstructor = loadClass.getConstructor(arg_class);
            jhdlCell = (Cell) loadClassConstructor.newInstance(args);

        } catch (NoSuchMethodException e1) {
            // If the constructor isn't found, print out a fatal error

            System.err.println("Attempting to load class: " + loadClass + " with args:");
            for (int j = 0; j < arg_class.length; j++)
                System.err.println("    " + arg_class[j] + " " + args[j]);
            System.err.println("Cannot find constructor which " + "matches these parameters, " + "exiting...");
            System.err.println("EdifCell Name: " + leafCell.getCellType().getName());
            System.err.println("Class Name: " + className);
            System.exit(1);
        } catch (InvocationTargetException e3) {
            // Error instantiating the Cell
            e3.getTargetException().printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        // TEST'S IF JHDL isNetlistLeaf() method returns correct value. 
        //	    Cell c;
        //        NodeList nl = jhdlCell.getChildren();
        //        boolean b = false;
        //        for (nl.init(), c = nl.getCell(); !nl.atEnd(); nl.next(), c = nl.getCell())
        //            if (c.isNetlistLeaf()) {
        //                System.out.println(c);
        //                b = true;
        //            }
        //        if (b) {
        //            System.out.println("Parent fault: " + jhdlCell);
        //            System.out.println("--------------------");
        //        }

        addCellInstanceProperties(leafCell, jhdlCell, addEdifCellInstanceAsProperty);
        // Done, the cell has been loaded, and can be viewed later
        // when cvt loads up
    }

    /**
     * Creates an ArrayList of JHDL Wire Objects to connect with the ports of
     * the sub cell.
     * 
     * @param subcell The subcell to the parentCell for creating wires
     * @param parentCell The parent to the subcell
     * @param wires The Collection that will contain the created Wire Objects
     * @param jhdlInstance Used to create the JDHL Wire objects
     * @param internalWires JHDL Wire Objects
     */
    protected void createSubCellWires(EdifCellInstance subcell, EdifCellInstance parentCell, ArrayList wires,
            Cell jhdlInstance, HashMap internalWires, Map instToMap) {

        /**
         * NR - START
         * <p>
         * Solving Problem: Multiple single-bit ports in EDIF need to be a
         * single multiple-bit port for JHDL constructors
         * <p>
         * This problem only appears with BRAMs
         * <p>
         * Check the passed-in EdifCell to see if it's a BRAM type
         * <ul>
         * <li>instCell - passed-in EdifCell
         * <li>xilinxCell - xilinx EdifCell (if passed-in is a BRAM type) or
         * passed-in EdifCell
         * <li>Map one2many
         * <ul>
         * <li> key: xilinxCell EdifPort
         * <li> value: ArrayList of instCell EdifPorts
         * </ul>
         * </ul>
         */
        EdifLibrary xilinxLib = edu.byu.ece.edif.arch.xilinx.XilinxLibrary.library;
        EdifCell instCell = subcell.getCellType();
        EdifCell xilinxCell = subcell.getCellType();

        boolean debug = false;

        boolean samePorts = false;
        boolean diffPorts = false;
        //		if (subcell.getName().equalsIgnoreCase("b218"))
        //			debug = true;
        if ((subcell.getType().toUpperCase().startsWith("RAMB") || subcell.getType().toUpperCase().startsWith("MULT18"))) {
            xilinxCell = xilinxLib.getCell(subcell.getType());
            //debug = true;
        }
        if (debug) {
            System.out.println("SUBCELL: " + subcell);
            System.out.println("JHDL INST: " + jhdlInstance);
            System.out.println("XILINX INST: " + xilinxCell);
            System.out.println("INST: " + instCell);
            System.out.println("PARENT: " + parentCell);
        }

        if (!isSameEdifCell(xilinxCell, instCell)) {
            if (!isAlmostSameEdifCell(xilinxCell, instCell)) {
                System.err.println("ERROR: conflicting cells - " + instCell.getInterface() + "\n AND \n"
                        + xilinxCell.getInterface());
                System.exit(-1);
            }
            diffPorts = true;
        }
        if (xilinxCell != instCell) {
            samePorts = true;
        }
        Map one2many = getMatchingPorts(xilinxCell, instCell);
        //printOne2Many((HashMap)one2many);
        /*
         * NR -END
         */

        //Iterate each port of the sub cell
        Iterator it = xilinxCell.getSortedPortList().iterator();
        while (it.hasNext()) {
            EdifPort port = (EdifPort) it.next();
            EdifPort oldPort = port;
            if (debug) {
                System.out.println("PORT: " + port);
            }

            if (port.getWidth() == 1) { //One bit wide port

                if (debug) {
                    System.out.println("SINGLE BIT");
                }
                /**
                 * NR -START
                 * <ul>
                 * <li>port - xilinxCell EdifPort
                 * <li>oldPort - instCell EdifPort
                 * </ul>
                 * <p>
                 * Note that 'oldPort' is usually used
                 */
                if (diffPorts || samePorts) {
                    ArrayList singlePorts = (ArrayList) one2many.get(port);
                    oldPort = (EdifPort) singlePorts.get(0);
                }
                /*
                 * NR -END
                 */

                Wire wire;
                //Get the connected net name to this port
                String connectedNetName = getNetNameFromPort(oldPort, -1, subcell, instToMap);
                if (connectedNetName == null) {
                    //If not connected
                    if (port.getDirection() == EdifPort.IN) {
                        wire = Logic.wire(jhdlInstance);
                        new gnd(jhdlInstance, wire);
                        if (debug) {
                            System.out.println("INPUT GND");
                        }
                    } else {
                        if (debug) {
                            System.out.println("INPUT NC");
                        }
                        wire = Logic.nc(jhdlInstance);
                    }

                } else {
                    //If connected
                    wire = (Wire) internalWires.get(connectedNetName);
                    if (debug) {
                        System.out.println("NET: " + connectedNetName);
                        System.out.println("\tWIRE: " + wire);
                    }
                }

                wires.add(wire);
            } else { //Bus port

                if (debug) {
                    System.out.println("BUS");
                }
                Wire busWire;

                /*
                 * NR -START
                 */
                if (diffPorts || samePorts) {
                    ArrayList singlePorts = (ArrayList) one2many.get(port);
                    oldPort = (EdifPort) singlePorts.get(0);
                }
                /*
                 * NR -END
                 */

                //Get the connected net name to this port member 0
                String connectedNetName = getNetNameFromPort(oldPort, 0, subcell, instToMap);
                if (connectedNetName == null) {
                    if (port.getDirection() == EdifPort.IN) {
                        busWire = Logic.wire(jhdlInstance);
                        new gnd(jhdlInstance, busWire);
                        if (debug) {
                            System.out.println("INPUT GND");
                        }
                    } else {
                        busWire = Logic.nc(jhdlInstance);
                        if (debug) {
                            System.out.println("INPUT NC");
                        }
                    }
                } else {
                    busWire = (Wire) internalWires.get(connectedNetName);
                    if (debug) {
                        System.out.println("NET: " + connectedNetName);
                        System.out.println("\tWIRE: " + busWire);
                    }
                }

                //For the rest bus port members
                for (int j = 1; j < port.getWidth(); j++) {

                    if (debug) {
                        System.out.println("BUS <" + j + ">");
                    }

                    /**
                     * NR -START
                     * <p>
                     * Since we have a bus, we have to check for 2 things:
                     * <ol>
                     * <li> 'actual Cell' is truly different from the xilinx
                     * cell (IE: the actual cell has multiple single bit ports
                     * where we want a single multiple-bit port)
                     * <ul>
                     * <li> HERE we grab the appropriate actual cell's
                     * single-bit port to pass to 'getNetNameFromPort()'
                     * </ul>
                     * <li> 'actual Cell' appears to be the same as the xilinx
                     * cell but is reported as being different.
                     * <ul>
                     * <li> HERE we can't just run the 'default' case since we
                     * still must first grab the actual cell's port (IE the
                     * 'oldPort')
                     * </ul>
                     * </ol>
                     */
                    if (diffPorts) {
                        ArrayList singlePorts = (ArrayList) one2many.get(port);
                        oldPort = (EdifPort) singlePorts.get(j);
                        connectedNetName = getNetNameFromPort(oldPort, 0, subcell, instToMap);
                        if (debug) {
                            System.out.println("DIFF - OLDPORT: " + oldPort);
                        }
                    } else if (samePorts) {
                        ArrayList singlePorts = (ArrayList) one2many.get(port);
                        oldPort = (EdifPort) singlePorts.get(0);
                        connectedNetName = getNetNameFromPort(oldPort, j, subcell, instToMap);
                        if (debug) {
                            System.out.println("SAME - OLDPORT: " + oldPort);
                        }
                    }
                    /*
                     * NR -END
                     */

                    else {
                        connectedNetName = getNetNameFromPort(oldPort, j, subcell, instToMap);
                        if (debug) {
                            System.out.println("NEITHER - OLDPORT: " + oldPort);
                        }
                    }
                    Wire wire;
                    if (connectedNetName == null) {
                        if (port.getDirection() == EdifPort.IN) {
                            wire = Logic.wire(jhdlInstance);
                            new gnd(jhdlInstance, wire);
                            if (debug) {
                                System.out.println("\tINPUT GND");
                            }
                        } else {
                            wire = Logic.nc(jhdlInstance);
                            if (debug) {
                                System.out.println("\tINPUT NC");
                            }
                        }
                    } else {
                        wire = (Wire) internalWires.get(connectedNetName);
                        if (debug) {
                            System.out.println("\tNET: " + connectedNetName);
                            System.out.println("\t\tWIRE: " + wire);
                        }
                    }
                    //Concat the wire to the busWire
                    busWire = Logic.concat(jhdlInstance, wire, busWire);

                }
                wires.add(busWire);
            }//End a port
        }//End while each port
    }//End method

    /**
     * It is important to retrieve INIT strings from the EDIF file to pass to
     * JHDL constructors. There is more than one type of INIT property in EDIF
     * so this method was created to grab different types of INIT strings (IE:
     * INITP).
     * 
     * @param leafCell
     * @param initDefault
     * @param initString
     * @param argSize
     * @param initPos
     * @param initDepth
     * @param arg_class
     * @param args
     * @return
     */
    protected String _getPropertyINITString(EdifCellInstance leafCell, String initDefault, String initString,
            int argSize, int initPos, int initDepth, Class[] arg_class, Object[] args) {

        // Memory initialization retrieval
        edu.byu.ece.edif.core.Property p;
        // Used for memories with only one INIT property
        if (initDepth == 1) {
            //initString = "INIT";
            arg_class[initPos] = String.class;
            p = leafCell.getProperty(initString);
            if (p != null) {
                StringTypedValue v = (StringTypedValue) p.getValue();
                args[initPos] = v.getStringValue();
            } else {
                args[initPos] = initDefault;
            }
        }
        // Used for memories with multiple INIT properties
        else if (initDepth > 1) {
            arg_class[initPos] = String[].class;
            args[initPos] = new String[initDepth];
            int j = 0;
            int initTen = (initDepth < 16) ? 1 : (int) Math.ceil(initDepth / 16);

            for (int k = 0; k < initTen; k++) {
                boolean first = true;
                for (char i = '0'; i <= 'f' && j < initDepth; i++, j++) {
                    if (i > '9' && first) {
                        first = false;
                        i = 'a';
                    }
                    initString = (initString + k) + i;
                    p = leafCell.getProperty(initString);
                    if (p != null) {
                        StringTypedValue v = (StringTypedValue) p.getValue();
                        ((String[]) args[initPos])[j] = v.getStringValue();
                    } else {
                        ((String[]) args[initPos])[j] = initDefault;
                    }
                }
            }
        }

        return initString;
    }

    /**
     * Get the connected net name that the port (can be a bus port member also)
     * of the subCellInstance connects to in the nets of the parent
     * CellInstance.
     * 
     * @param port The EdifPort Object owned by the subCellInstance
     * @param busMember The bus member of the EdifPortRef
     * @param subCellInstance The EdifCellInstance owning port
     */
    protected String getNetNameFromPort(EdifPort port, int busMember, EdifCellInstance subCellInstance, Map instToMap) {

        String retval = null;
        Map portsToPortRefArray = (Map) instToMap.get(subCellInstance);
        EdifPortRef[] portRefArray = (EdifPortRef[]) portsToPortRefArray.get(port);

        if (portsToPortRefArray == null) {
            System.out.println("NULL MAP: " + subCellInstance);
        }

        if (portRefArray == null) {
            System.out.println("NULL ARRAY: " + port);
            if (portsToPortRefArray != null) {
                System.out.println("MAP: ");
                System.out.println("\t" + portsToPortRefArray.keySet());
            }
        } else {
            if (busMember < 0)
                busMember = 0;
            EdifPortRef epr = portRefArray[busMember];
            if (epr != null)
                retval = epr.getNet().getName();
        }

        return retval;
    }

    /**
     * Returns true if the EdifCell contains a port with named after the given
     * String.
     * 
     * @param cell The cell whose EdifPort Objects will be checked
     * @return True if the EdifCell contains a port named after the given String
     */
    private boolean containsPortName(EdifCell cell, String portName) {
        for (Iterator it = cell.getPortList().iterator(); it.hasNext();) {
            EdifPort port = (EdifPort) it.next();
            if (NamedObjectCompare.equals(port, portName))
                return true;
        }
        return false;
    }

    /**
     * Method used to parse the depth of the ram or rom (in ramMxN its the
     * number associated with 'N'.
     * 
     * @param name The name of the ram or rom
     * @return The depth of the ram or rom
     */
    private static int parseMemDepth(String name) {
        name = name.toLowerCase();
        int start = name.lastIndexOf('x') + 1;
        int end = start + 1;

        while (end < name.length() && name.charAt(end) >= '0' && name.charAt(end) <= '9')
            end++;

        String rangeStr = name.substring(start, end);
        int depth = Integer.parseInt(rangeStr);
        return depth;
    }

    /**
     * In ramMxN, this method returns the 'M' for ram or roms.
     * 
     * @param name The name of the class to parse
     * @return The initialization String for this ram or rom
     */
    private static String parseMemMxNPropertyLength(String name) {
        String ret = "";

        int end = name.lastIndexOf('x');
        if (end < 3)
            return null;
        int start = end - 1;

        while (start > 0 && name.charAt(start - 1) >= '0' && name.charAt(start - 1) <= '9')
            start--;

        String rangeStr = name.substring(start, end);
        int length = Integer.parseInt(rangeStr) / 4;

        for (int i = 0; i < length; i++)
            ret += "0";

        return ret;
    }

    /**
     * The JHDL Cell property name used for holding original EDIF instance
     * object. In JHDL, the user can obtain a reference to the EDIFCellInstance
     * object by querying this property. This property will only be added to the
     * JHDL cell when the appropriate flags in the building methods are set.
     */
    public static final String EDIF_CELL_INSTANCE_PROPERTY = "edifCellInstance";

    /**
     * The JHDL Net property name used for holding original EDIF EDIFNet. In
     * JHDL, the user can obtain a reference to the EDIFNet object by querying
     * this property. This property will only be added to the JHDL cell when the
     * appropriate flags in the building methods are set.
     */
    public static final String EDIF_NET_PROPERTY = "edifNet";

    /**
     * the targeting technology name *
     */
    private String technology;
}
