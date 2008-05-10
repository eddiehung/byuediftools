/*
 * Map between a JHDL Wire and a Collection of Xilinx names for the wire.
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
package edu.byu.ece.edif.arch.xilinx;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import byucc.jhdl.base.Cell;
import byucc.jhdl.base.HelperLibrary;
import byucc.jhdl.base.NodeList;
import byucc.jhdl.base.Wire;
import byucc.jhdl.base.WireList;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.util.export.jhdl.Edi2JHDL;

/**
 * This class creates a Map object where the key of the Map is a JHDL Wire
 * object. The value of the Map is a Collection of String objects where each
 * String represents a possible Xilinx name for the given JHDL Wire. All Wire
 * objects within the JHDL hierarchy will be in this Map.
 * 
 * @author nhr2
 * @since Created on Jun 27, 2005
 */
public class JHDL2XilinxNameMap extends HashMap {

    /**
     * Create the JHDL - Xilinx Map
     * 
     * @param root The root JHDL Cell object
     * @param topInstance The EdifCellInstance object used to create this JHDL
     * Cell object.
     */
    public JHDL2XilinxNameMap(Cell root, EdifCellInstance topInstance) {
        super();
        _root = root;
        _edifHierarchy = new InstanceHierarchy(topInstance);
        _edif2Xilinx = new Edif2XilinxName(_edifHierarchy);
        _getAllEdifSignalNames(_root, _edifHierarchy);
    }

    /**
     * This method recursively traverses the hierarchy of root cell to create
     * JHDL Wire to Xilinx name mappings.
     * 
     * @param root
     * @return A Map between Wire (key) and a List (value) of possible Xilinx
     * names
     */
    protected void _getAllEdifSignalNames(Cell root, InstanceHierarchy hier) {
        Map retval = new LinkedHashMap();
        NodeList children = root.getChildren();
        WireList wires = root.getWires();

        for (children.init(); !children.atEnd(); children.next()) {
            Cell child = (Cell) children.getNode();
            _getAllEdifSignalNames(child, hier);
        }
        for (wires.init(); !wires.atEnd(); wires.next()) {
            Wire wire = (Wire) wires.getWire();
            ArrayList names = (ArrayList) getEdifNetNames(wire, hier);
            if (!names.isEmpty()) {
                ArrayList values = new ArrayList();
                HashSet set = new LinkedHashSet();
                values = (ArrayList) get(wire);
                if (values == null)
                    values = new ArrayList();
                values.addAll(names);
                set.addAll(values);
                values = new ArrayList();
                values.addAll(set);
                put(wire, values);
            }
        }
    }

    /**
     * This method recursively traverses the hierarchy of root cell to create
     * JHDL Wire to Xilinx name mappings.
     * 
     * @param root
     * @return A Map between Wire (key) and a List (value) of possible Xilinx
     * names
     */
    public Map getAllEdifSignalNames(Cell root, InstanceHierarchy hier) {
        Map retval = new LinkedHashMap();
        NodeList children = root.getChildren();
        WireList wires = root.getWires();

        for (children.init(); !children.atEnd(); children.next()) {
            Cell child = (Cell) children.getNode();
            retval.putAll(getAllEdifSignalNames(child, hier));
        }
        for (wires.init(); !wires.atEnd(); wires.next()) {
            Wire wire = (Wire) wires.getWire();
            ArrayList names = (ArrayList) getEdifNetNames(wire, hier);
            if (!names.isEmpty()) {
                ArrayList values = (ArrayList) retval.get(wire);
                values.addAll(names);
                HashSet set = new LinkedHashSet();
                set.addAll(values);
                values = new ArrayList();
                values.addAll(set);
                retval.put(wire, values);
            }
        }
        return retval;
    }

    /**
     * This method returns the Net name of a given JHDL Wire.
     * 
     * @param wire
     * @return
     */
    public ArrayList getEdifNetNames(Wire wire, InstanceHierarchy hier) {
        ArrayList retval = new ArrayList();
        ArrayList cells = new ArrayList();
        Wire trueWire = HelperLibrary.getTopWire(wire.gw(0));
        int wireWidth = wire.getWidth();
        Cell parent2 = wire.getParentCell();

        while (parent2 != null) {
            EdifCellInstance eci = (EdifCellInstance) parent2.getPropertyValue(Edi2JHDL.EDIF_CELL_INSTANCE_PROPERTY);
            if (eci != null) {
                cells.add(0, eci);
            }
            if (parent2.getParent() != null)
                if (parent2.getParent().getParent() == null)
                    break;
            parent2 = parent2.getParentCell();
        }

        InstanceHierarchy cellHier = hier.getHierarchyNode(cells);

        if (cellHier != null || cells.isEmpty()) {
            EdifNet net = (EdifNet) HelperLibrary.getPropertyFromTopWire(wire.gw(0), Edi2JHDL.EDIF_NET_PROPERTY);

            ArrayList names = null;
            if (net != null) {
                NetHierarchy netHier = new NetHierarchy(cellHier, net);
                names = _edif2Xilinx.getAllJHDLNetNames(netHier, false);
                retval.addAll(names);
                names = _edif2Xilinx.getOutputPortInstances(netHier, cellHier, false);
                retval.addAll(names);
            }
        }

        return retval;
    }

    public void printEdifNetMap() {

        for (Iterator it = this.keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            ArrayList values = (ArrayList) this.get(key);
            System.out.println("KEY: " + key);
            for (Iterator it2 = values.iterator(); it2.hasNext();) {
                String name = (String) it2.next();
                System.out.println("\t " + name);
            }
        }
    }

    public static ArrayList createValueListFromMap(Map JHDLMap) {
        ArrayList retval = new ArrayList();

        for (Iterator keyIt = JHDLMap.keySet().iterator(); keyIt.hasNext();) {
            Object key = keyIt.next();
            ArrayList values = (ArrayList) JHDLMap.get(key);
            for (Iterator valueIt = values.iterator(); valueIt.hasNext();) {
                String name = (String) valueIt.next();
                retval.add(name);
            }
        }

        return retval;
    }

    public static void checkXPowerNameMatch(Map JHDLMap, List xpowerNames) {
        String fileName1 = "xpowerMatchResults.txt";
        PrintWriter fp = Edif2XilinxName.openWriteFile(fileName1);
        int matches = 0;
        int mismatches = 0;
        ArrayList values = createValueListFromMap(JHDLMap);

        for (Iterator it = xpowerNames.iterator(); it.hasNext();) {
            String xpName = (String) it.next();
            if (values.contains(xpName))
                matches++;
            else {
                mismatches++;
                fp.println(xpName);
            }
        }
        fp.println("\nMATCHES: " + matches + "  MISMATCHES: " + mismatches);
        fp.close();
    }

    private Cell _root;

    private InstanceHierarchy _edifHierarchy;

    private Edif2XilinxName _edif2Xilinx;
}
