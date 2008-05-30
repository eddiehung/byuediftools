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
package edu.byu.ece.edif.arch.xilinx;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import byucc.jhdl.base.Cell;
import byucc.jhdl.base.CellList;
import byucc.jhdl.base.Wire;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.util.parse.EdifParser;

/**
 * This class is used for converting hierarchical EDIF names into valid
 * flattened Xilinx names.
 * <p>
 * The rules for Xilinx Xpower names are as follows:
 * <ol>
 * <li> Outputs For top-level "outputs" use the top-level instance names (old
 * names) for instances having only one output that drive the output signal
 * <p>
 * What about instances with multiple outputs?
 * <li> Signals
 * <ol>
 * <li> Top-level signals: driven by ibufs/top-level instances:
 * <ul>
 * <li> use the top-level net name (old name) (not instance name)
 * </ul>
 * <li> For names with hierarchy:
 * <ol>
 * <li> find the highest level of hierarchy for the net
 * <li> find the hierarchy of this level. Add net name. (IE: if any of the net's
 * portRefs refer to top level ports, don't add the port)
 * <li> append "old names" of each instance (separated by "/")
 * <li> append final net name
 * <li> Special case extension
 * <ul>
 * <li> driver &amp; sinks are lower levels of hierarchy
 * </ul>
 * </ol>
 * </ol>
 * <li> Logic
 * <ol>
 * <li> Top-level and hierarchical names:
 * <ol>
 * <li> find cell instance hierarchy
 * <li> append "old names" of each instance hierarchy level (separated by "/")
 * <li> Special case extensions
 * <ul>
 * <li> NOTE: "_rt" & ".WSGEN" specaial case extensions hacked
 * </ul>
 * </ol>
 * </ol>
 * <li> Inputs
 * <ul>
 * <li>Use top-level instance names (old names)
 * </ul>
 * <li> IOs
 * <ul>
 * <li>Use top-level instance names (old names)
 * </ul>
 * </ol>
 * Create a "Map" between:
 * <ul>
 * <li>key: List: (ordered List of EdifCellInstance objects/Nets)
 * <li>value: String (corresponding Xilinx String)
 * </ul>
 * 
 * @author Nathan Rollins
 * @since Created on Jun 8, 2005
 */
public class Edif2XilinxName {

    /**
     * @param fileName - the name of the EDIF file to parse
     */
    public Edif2XilinxName(String fileName) {
        _nameObjectMap = new LinkedHashMap();
        _innerNetsMapCache = new LinkedHashMap();
        EdifCellInstance topInstance = parseCell(fileName);
        InstanceHierarchy instHier = new InstanceHierarchy(topInstance);
        createSignalMap(instHier);
    }

    public Edif2XilinxName(EdifCellInstance top) {
        _nameObjectMap = new LinkedHashMap();
        _innerNetsMapCache = new LinkedHashMap();
        _topInstHier = new InstanceHierarchy(top);
        //InstanceHierarchy instHier = new InstanceHierarchy(top);
        createSignalMap(_topInstHier);
    }

    public Edif2XilinxName(InstanceHierarchy hier) {
        _nameObjectMap = new LinkedHashMap();
        _innerNetsMapCache = new LinkedHashMap();
        _topInstHier = hier;
        //createSignalMap(hier);
    }

    /**
     * @param hier
     */
    public void createSignalMap(InstanceHierarchy hier) {
        InstanceHierarchy[] instances = hier.getChildren();
        NetHierarchy[] nets = hier.getNets();
        EdifCellInstance eci = hier.getSelf();

        if (nets != null)
            for (int i = 0; i < nets.length; i++) {
                EdifNet net = nets[i].getEdifNet();
                ArrayList names = getAllEdifNetNames(net, hier, nets[i], false);

                for (Iterator it = names.iterator(); it.hasNext();) {
                    String name = (String) it.next();
                    _nameObjectMap.put(name, nets[i]);
                }
            }

        if (instances != null)
            for (int i = 0; i < instances.length; i++)
                createSignalMap(instances[i]);

    }

    public void createLogicMap(InstanceHierarchy hier) {
        InstanceHierarchy[] instances = hier.getChildren();
        NetHierarchy[] nets = hier.getNets();

        EdifCellInstance eci = hier.getSelf();
        String logicName = hier.getInstanceName(true);

        for (int i = 0; i < nets.length; i++) {
            EdifNet net = nets[i].getEdifNet();

            if (net.hasTopLevelPortConnection()) {
                continue;
            }
            Collection drivers = net.getNetDrivers();
            if (drivers.size() >= 1) {
                EdifPortRef epr = (EdifPortRef) drivers.iterator().next();
                EdifCellInstance inst = epr.getCellInstance();
                String signalName = nets[i].getFullNetName(false);

                _nameObjectMap.put(signalName, nets[i]);

                if (isRTObject(net)) {
                    _nameObjectMap.put(signalName + "_rt", nets[i]);
                }
                if (isWSGENObject(net)) {
                    _nameObjectMap.put(signalName + ".WSGEN", nets[i]);
                }
            }
        }
        for (int i = 0; i < instances.length; i++) {
            EdifCellInstance inst = instances[i].getSelf();
            String name = instances[i].getInstanceName(false);

            ArrayList suffixes = (ArrayList) getXilinxNameSuffix(eci);
            if (!suffixes.isEmpty()) {
                for (Iterator it = suffixes.iterator(); it.hasNext();) {
                    String suffix = (String) it.next();
                    _nameObjectMap.put(logicName + suffix, instances[i]);
                }
            } else {
                _nameObjectMap.put(logicName, instances[i]);
            }
            createLogicMap(instances[i]);
        }
    }

    public static EdifNet getInnerMostNet(EdifNet net) {
        EdifNet retval = net;

        if (net != null) {
            Collection drivers = net.getNetDrivers();

            if (drivers != null && drivers.size() > 0) {
                EdifPortRef epr = (EdifPortRef) drivers.iterator().next();
                if (epr != null) {
                    EdifSingleBitPort port = epr.getSingleBitPort();
                    EdifNet inNet = port.getInnerNet();
                    if (inNet != null) {
                        EdifNet tempNet = getInnerMostNet(inNet);
                        if (tempNet != null)
                            retval = tempNet;
                    }
                }
            }
        }
        return retval;
    }

    public boolean drivesTopLevelOutputPort(NetHierarchy netHier) {
        boolean retval = false;
        EdifNet net = netHier.getEdifNet();
        Collection portrefs = net.getOutputPortRefs();
        InstanceHierarchy hier = netHier.getInstanceHierarchy();

        EdifNet prenet = net;
        net = getInnerMostNet(net);
        EdifCellInstance eci = hier.getParent().getSelf();
        if (eci != null && net != null) {
            //			Map outerNets = eci.getOuterNets();
            Map innerNets = (Map) _innerNetsMapCache.get(eci);
            if (innerNets == null) {
                innerNets = eci.getInnerNets();
                _innerNetsMapCache.put(eci, innerNets);
            }
            if (innerNets.containsValue(net) && net != prenet) {
                retval = true;
            }
        }

        return retval;
    }

    public ArrayList getAllJHDLNetNames(NetHierarchy netHier, boolean includeTop) {
        ArrayList retval = new ArrayList();
        EdifNet net = netHier.getEdifNet();
        InstanceHierarchy hier = netHier.getInstanceHierarchy();
        Collection drivers = net.getNetDrivers();
        if (drivers.size() >= 1) {
            EdifPortRef epr = (EdifPortRef) drivers.iterator().next();
            String signalName = "";
            signalName = netHier.getFullNetName(includeTop);
            EdifCellInstance inst = epr.getCellInstance();
            String slash = "";
            String logicName = "";

            if (inst != null) {
                if (hier != null) {
                    slash = (hier.isTop()) ? "" : "/";
                    logicName = hier.getInstanceName(includeTop) + slash + inst.getOldName();
                } else
                    logicName = inst.getOldName();

                if (Pattern.matches("LUT\\d_L", (inst.getType().toUpperCase()))
                        || (inst.getType().toUpperCase()).startsWith("MUXCY_")
                        || (inst.getType().toUpperCase()).startsWith("XORCY_L")) {
                    retval.add(logicName + "/O");

                }
                if ((inst.getType().toUpperCase()).startsWith("IBUFDS_LVPECL_33")) {
                    retval.add(logicName + "/IBUFDS/SLAVEBUF.DIFFIN");
                }
            }
            retval.add(signalName);
            if (isRTObject(net)) {
                retval.add(signalName + "_rt");
            }
        }
        return retval;
    }

    public static ArrayList getAllNetNames(EdifNet net, InstanceHierarchy hier, NetHierarchy netHier, boolean includeTop) {
        ArrayList retval = new ArrayList();
        //	if (!net.hasTopLevelPortConnection()) {
        Collection drivers = net.getNetDrivers();
        if (drivers.size() >= 1) {
            EdifPortRef epr = (EdifPortRef) drivers.iterator().next();
            String signalName = "";
            signalName = netHier.getFullNetName(includeTop);
            EdifCellInstance inst = epr.getCellInstance();
            String slash = "";
            String logicName = "";

            if (inst != null) {
                if (hier != null) {
                    slash = (hier.isTop()) ? "" : "/";
                    logicName = hier.getInstanceName(includeTop) + slash + inst.getOldName();
                } else
                    logicName = inst.getOldName();

                if (Pattern.matches("LUT\\d_L", (inst.getType().toUpperCase()))
                        || (inst.getType().toUpperCase()).startsWith("MUXCY_")
                        || (inst.getType().toUpperCase()).startsWith("XORCY_L")) {
                    retval.add(logicName + "/O");

                }
                if ((inst.getType().toUpperCase()).startsWith("IBUFDS_LVPECL_33")) {
                    retval.add(logicName + "/IBUFDS/SLAVEBUF.DIFFIN");
                }
            }
            retval.add(signalName);
            if (isRTObject(net)) {
                retval.add(signalName + "_rt");
            }
        }
        //	}
        return retval;
    }

    public static ArrayList getAllEdifNetNames(EdifNet net, InstanceHierarchy hier, NetHierarchy netHier,
            boolean includeTop) {
        ArrayList retval = new ArrayList();
        Collection drivers = net.getNetDrivers();
        if (drivers.size() >= 1) {
            EdifPortRef epr = (EdifPortRef) drivers.iterator().next();
            String signalName = "";
            signalName = netHier.getFullNetName(includeTop);
            EdifCellInstance inst = epr.getCellInstance();
            //EdifCellInstance inst = hier.getSelf();
            String slash = "";
            String logicName = "";
            boolean isinst = false;

            if (inst != null) {
                if (hier != null) {
                    slash = (hier.isTop()) ? "" : "/";
                    logicName = hier.getInstanceName(includeTop) + slash + inst.getOldName();
                } else
                    logicName = inst.getOldName();

                if (Pattern.matches("LUT\\d_L", (inst.getType().toUpperCase()))
                        || (inst.getType().toUpperCase()).startsWith("MUXCY_")
                        || (inst.getType().toUpperCase()).startsWith("XORCY_L")) {
                    isinst = true;
                    //					if (drivesTopLevelOutputPort(netHier))
                    //						retval.add(logicName+"/O");
                }
                if ((inst.getType().toUpperCase()).startsWith("IBUFDS_LVPECL_33")) {
                    retval.add(logicName + "/IBUFDS/SLAVEBUF.DIFFIN");
                }
            }
            if (!isinst) {
                retval.add(signalName);
                if (isRTObject(net)) {
                    retval.add(signalName + "_rt");
                }
            }
        }
        return retval;
    }

    public static ArrayList getAllNetNamesSimple(EdifNet net, NetHierarchy netHier, boolean includeTop) {
        ArrayList retval = new ArrayList();
        Collection drivers = net.getNetDrivers();
        if (drivers.size() >= 1) {
            EdifPortRef epr = (EdifPortRef) drivers.iterator().next();
            String signalName = "";
            signalName = netHier.getFullNetName(includeTop);

            retval.add(signalName);
            if (isRTObject(net)) {
                retval.add(signalName + "_rt");
            }
        }
        return retval;
    }

    public static ArrayList getAllPortNames(EdifNet net, InstanceHierarchy hier, NetHierarchy netHier,
            boolean includeTop) {
        ArrayList retval = new ArrayList();

        return retval;
    }

    public ArrayList getOutputPortInstances(NetHierarchy netHier, InstanceHierarchy hier, boolean includeTop) {
        ArrayList retval = new ArrayList();

        EdifNet net = netHier.getEdifNet();
        Collection drivers = net.getNetDrivers();
        if (drivers.size() >= 1) {
            EdifPortRef epr = (EdifPortRef) drivers.iterator().next();
            String signalName = "";
            signalName = netHier.getFullNetName(includeTop);
            EdifCellInstance inst = hier.getSelf();
            String logicName = "";
            boolean isinst = false;

            if (inst != null) {
                if (hier != null) {
                    logicName = hier.getInstanceName(includeTop);
                } else
                    logicName = inst.getOldName();

                if (Pattern.matches("LUT\\d_L", (inst.getType().toUpperCase()))
                        || (inst.getType().toUpperCase()).startsWith("MUXCY_")
                        || (inst.getType().toUpperCase()).startsWith("XORCY_L")) {
                    isinst = true;
                    if (drivesTopLevelOutputPort(netHier)) {
                        retval.add(logicName + "/O");
                    }
                }
                if ((inst.getType().toUpperCase()).startsWith("IBUFDS_LVPECL_33")) {
                    retval.add(logicName + "/IBUFDS/SLAVEBUF.DIFFIN");
                }
            }
            if (!isinst) {
                retval.add(signalName);
                if (isRTObject(net)) {
                    retval.add(signalName + "_rt");
                }
            }
        }

        return retval;
    }

    public Map getNameObjectMap() {
        return _nameObjectMap;
    }

    /**
     * For some cell instances Xilinx adds one or more extensions to the "Logic"
     * name TODO: Describe each rule in more detail.
     * 
     * @param cellinst
     * @return
     */
    public static List getXilinxNameSuffix(EdifCellInstance cellinst) {
        ArrayList retval = new ArrayList();

        if ((cellinst.getType().toUpperCase()).startsWith("SRL16")) {
            retval.add("/SRL16E");
        } else if ((cellinst.getType().toUpperCase()).startsWith("RAM16X1D")) {
            retval.add("/SP");
            retval.add("/SP.WSGEN");
            retval.add("/DP");
        } else if ((cellinst.getType().toUpperCase()).startsWith("RAMB")) {
            retval.add(".A");
            retval.add(".B");
        } else if ((cellinst.getType().toUpperCase()).startsWith("IBUFDS_LVPECL_33")) {
            retval.add("/IBUFDS/IBUFDS");
            retval.add("/IBUFDS/SLAVEBUF.DIFFIN");
        } else if ((cellinst.getType().toUpperCase()).startsWith("BUFGP")) {
            retval.add("/IBUFG");
        } else if ((cellinst.getType().toUpperCase()).startsWith("BUFGDLL")) {
            retval.add("/IBUFG");
        } else if ((cellinst.getType().toUpperCase()).startsWith("IOBUF_F_24")) {
            retval.add("/IBUF");
        }
        return retval;
    }

    /**
     * Xilinx adds a "_rt" extension to some signals. This method attempts to
     * determine if the current net requires this extension. This extension
     * seems to be applied to signals which are within a CLB.
     * 
     * @param net
     * @return
     */
    public static boolean isRTObject(EdifNet net) {
        boolean retval = false;
        Collection drivers = net.getNetDrivers();
        Collection driven = net.getNetDriven();
        String[] sources = { "MUXCY", "FDCE", "LUT", "INV" };
        String[] sinks = { "FDCE", "LUT", "XORCY", "XORCY" };

        EdifCellInstance ecidriver = ((EdifPortRef) drivers.iterator().next()).getCellInstance();

        if (ecidriver != null) {
            for (int i = 0; i < sources.length; i++) {
                if ((ecidriver.getType().toUpperCase()).startsWith(sources[i])) {
                    for (Iterator it = driven.iterator(); it.hasNext();) {
                        EdifCellInstance ecidriven = ((EdifPortRef) it.next()).getCellInstance();
                        if (ecidriven != null)
                            if ((ecidriven.getType().toUpperCase()).startsWith(sinks[i])) {
                                retval = true;
                                break;
                            }
                    }
                }
                if (retval)
                    break;
            }
        }
        return retval;
    }

    public static boolean isRTObject(Wire wire) {
        boolean retval = false;
        Cell driver = wire.getSourceCell();
        CellList driven = wire.getSinkCells();
        String[] sources = { "MUXCY", "FDCE", "LUT", "INV" };
        String[] sinks = { "FDCE", "LUT", "XORCY", "XORCY" };

        if (driver != null && driven != null) {
            for (int i = 0; i < sources.length; i++) {
                if ((driver.getCellName().toUpperCase()).startsWith(sources[i])) {
                    for (Iterator it = driven.iterator(); it.hasNext();) {
                        Cell sinkCell = (Cell) it.next();
                        if ((sinkCell.getCellName().toUpperCase()).startsWith(sinks[i])) {
                            retval = true;
                            break;
                        }
                    }
                }
                if (retval)
                    break;
            }
        }
        return retval;
    }

    /**
     * Xilinx adds a ".WSGEN" extension to some signals. This method attempts to
     * determine if the current net requires this extension. The majority of the
     * signals that have this extension added are nets which are driven by by a
     * bus member, or are nets which only drive bus members.
     * 
     * @param net
     * @return
     */
    public static boolean isWSGENObject(EdifNet net) {
        boolean retval = true;
        Collection drivers = net.getNetDrivers();
        Collection driven = net.getNetDriven();

        for (Iterator it = drivers.iterator(); it.hasNext();) {
            EdifPortRef portref = (EdifPortRef) it.next();
            EdifCell celldrivers = portref.getCellInstance().getCellType();
            if (portref.isSingleBitPortRef() && portref.isDriverPortRef()) {
                retval = false;
            }
        }

        if (!retval) {
            for (Iterator it = driven.iterator(); it.hasNext();) {
                EdifPortRef portref = (EdifPortRef) it.next();
                EdifCell celldriven = portref.getCellInstance().getCellType();
                if (portref.isSingleBitPortRef()) {
                    retval = false;
                }
            }
        }

        return retval;

    }

    /**
     * This is a helper method to reduce redundant and cluttered code.
     * 
     * @param fileName
     * @return
     */
    public static PrintWriter openWriteFile(String fileName) {
        PrintWriter fp;
        try {
            fp = new PrintWriter(new FileOutputStream(fileName));
        } catch (IOException e) {
            throw new RuntimeException("ERROR: Cannot create file:" + fileName + " for writing");
        }
        return fp;
    }

    /**
     * simply returns the EdifEnvironment from the EDIF file name
     * 
     * @param fileName
     * @return
     */
    public static EdifCellInstance parseCell(String fileName) {
        try {
            EdifEnvironment env = EdifParser.translate(fileName);
            return env.getTopCellInstance();
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            return null;
        }
    }

    /**
     * This method prints the signal names parsed from the EDIF file
     * 
     * @param names
     */
    public void printMapNames() {
        String fileName = "EDIFNames.txt";
        PrintWriter fp = openWriteFile(fileName);

        fp.println("EDIF Names:\n");
        for (Iterator it = _nameObjectMap.keySet().iterator(); it.hasNext();) {
            String name = (String) it.next();
            fp.println(name);
        }
        fp.close();
    }

    /**
     * This is the main Map of the object. The key is a hierarchical Edif String
     * name and the value is an EdifObject (EdifNet or EdifCellInstance). This
     * is a one to one mapping.
     */
    protected Map _nameObjectMap;

    protected Map _innerNetsMapCache;

    protected InstanceHierarchy _topInstHier;
}
