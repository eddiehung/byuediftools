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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import edu.byu.ece.edif.core.BasicEdifBusNamingPolicy;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxResourceMapper;

public class XilinxTools {

    public static EdifSingleBitPort getClkPort(EdifCellInstance eci) {
        EdifSingleBitPort retval = null;

        Collection<EdifPort> ports = eci.getCellType().getInputPorts();
        for (EdifPort port : ports) {
            if (clkPortsList.contains(BasicEdifBusNamingPolicy.getBusBaseNameStatic(port.getName()).toUpperCase())) {
                retval = port.getSingleBitPort(0);
                break;
            }
        }
        if (retval == null) {
            System.err.println("ERROR: NO CLK PORT FOUND FOR INSTANCE - " + eci);
        }

        return retval;
    }

    /**
     * Collect the input EdifPortRefs that the given EdifCellInstance drives
     * 
     * @param eci
     * @return
     */
    public static Collection<EdifPortRef> getSinkInputPortRefs(EdifCellInstance eci) {
        Collection<EdifPortRef> retval = new LinkedHashSet<EdifPortRef>();

        Map<EdifSingleBitPort, EdifPortRef> eprMap = eci.getOuterEPRs();
        for (EdifSingleBitPort port : eprMap.keySet()) {
            if (port.getParent().isOutput()) {
                EdifPortRef value = eprMap.get(port);
                EdifNet net = value.getNet();
                retval.addAll(net.getSinkPortRefs(true, true));
            }
        }

        return retval;
    }

    /**
     * Collect the output EdifPortRefs that drive the given EdifCellInstance
     * 
     * @param eci
     * @return
     */
    public static Collection<EdifPortRef> getSourceOutputPortRefs(EdifCellInstance eci) {
        Collection<EdifPortRef> retval = new LinkedHashSet<EdifPortRef>();

        Map<EdifSingleBitPort, EdifPortRef> eprMap = eci.getOuterEPRs();
        for (EdifSingleBitPort port : eprMap.keySet()) {
            if (port.getParent().isInput()) {
                EdifPortRef value = eprMap.get(port);
                EdifNet net = value.getNet();
                retval.addAll(net.getSinkPortRefs(true, true));
            }
        }

        return retval;
    }

    /**
     * Get the output EdifSingleBitPort for the FF EdifCell NOTE: We make the
     * following 2 assumptions:
     * <ol>
     * <li> a FF sink will have ONLY one output port
     * <li> the single output port will ALWAYS be one bit wide.
     * </ol>
     */
    public static EdifSingleBitPort getRegisterOutputPort(EdifCell cell) {
        EdifSingleBitPort returnPort = null;

        Collection<EdifPort> sinkOutPorts = cell.getOutputPorts();
        returnPort = (sinkOutPorts.iterator().next()).getSingleBitPort(0);

        return returnPort;
    }

    /**
     * This method simply determines if there is a clock port of any kind
     * associated with the given EdifCell. In other words, we want to know if
     * this EdifCell is a register.
     * 
     * @param cell
     * @return
     */
    public static boolean hasClockPort(EdifCell cell) {
        boolean retval = false;

        Collection<EdifPort> ports = cell.getInputPorts();
        for (EdifPort port : ports) {
            String nodeName = BasicEdifBusNamingPolicy.getBusBaseNameStatic(port.getName()).toUpperCase();

            if (clkPortsList.contains(nodeName)) {
                retval = true;
                break;
            }
        }

        return retval;
    }

    /**
     * Finds out if the given EdifNet is used as a clock net anywhere. TODO:
     * Tighten up the definition of a Clock Net. This method would currently
     * match a Net driving any port with the name "c," for example. Check for
     * known sequential elements? Check driver instead? (this would cause a
     * global reset using a BUFG to register as a clock net, though)
     * 
     * @param net
     * @return
     */
    public static boolean isClockNet(EdifNet net) {
        boolean retval = false;

        Collection<EdifPortRef> sinkEPRs = net.getSinkPortRefs(true, true);
        for (EdifPortRef epr : sinkEPRs) {
            String portName = BasicEdifBusNamingPolicy.getBusBaseNameStatic(
                    epr.getSingleBitPort().getParent().getName()).toUpperCase();
            if (clkPortsList.contains(portName)) {
                retval = true;
                break;
            }
        }

        return retval;
    }

    public static boolean isPossibleClockNet(EdifNet net) {
        // Check sinks for clock port-like names
        boolean retval = isClockNet(net);

        // Check for BUFG driver
        Collection<EdifPortRef> sourceEPRs = net.getSourcePortRefs(true, true);
        for (EdifPortRef epr : sourceEPRs) {
            EdifCell sourceCell = epr.getPort().getEdifCell();
            if (XilinxResourceMapper.getResourceType(sourceCell).equals(XilinxResourceMapper.BUFG)) {
                retval = true;
                break;
            }
        }

        return retval;
    }

    /**
     * This method simply determines if the given port is a clock port
     * 
     * @param port
     * @return
     */
    public static boolean isClockPort(EdifSingleBitPort port) {
        boolean retval = false;

        String portName = BasicEdifBusNamingPolicy.getBusBaseNameStatic(port.getParent().getName()).toUpperCase();
        if (clkPortsList.contains(portName))
            retval = true;

        return retval;
    }

    /**
     * Determines if the given EdifCellInstance is used to drive any clock
     * ports.
     * 
     * @param eci
     * @return
     */
    public static boolean isClockSourceCell(EdifCellInstance eci) {
        boolean retval = false;

        Map<EdifSingleBitPort, EdifPortRef> outerNets = eci.getOuterEPRs();
        for (EdifSingleBitPort port : outerNets.keySet()) {
            EdifPortRef epr = outerNets.get(port);

            if (epr.getSingleBitPort().getParent().isOutput()) {
                EdifNet net = epr.getNet();
                if (isClockNet(net)) {
                    retval = true;
                    break;
                }
            }
        }

        return retval;
    }

    /**
     * Determines if the given EdifCell is a constant '1' or '0' (IE: GND or
     * VCC).
     * 
     * @param cell
     * @return
     */
    public static boolean isConstantInput(EdifCell cell) {
        boolean retval = false;

        String type = cell.getName().toUpperCase().trim();
        if (constantEdifCellsList.contains(type))
            retval = true;

        return retval;
    }

    /**
     * Determines if the given EdifCell is a register of any kind.
     * 
     * @param cell
     * @return
     */
    public static boolean isRegisterCell(EdifCell cell) {
        boolean retval = false;

        String cellname = cell.getName().toUpperCase();
        if (registerList.contains(cellname)) {
            retval = true;
        } else {
            retval = hasClockPort(cell);
        }

        return retval;
    }

    public static boolean isRestrictedPair(EdifCellInstance src, EdifCellInstance sink) {
        boolean retval = false;

        if (src != null && sink != null) {
            String srcName = src.getType().toUpperCase();
            Collection<String> sinkList = restrictedMap.get(srcName);

            if (sinkList != null) {
                if (srcName.equalsIgnoreCase(MULT_AND))
                    retval = true;
                else {
                    String sinkName = sink.getType().toUpperCase();
                    if (sinkList.contains(sinkName))
                        retval = true;
                }
            }
        }
        return retval;
    }

    /**
     * An array of all the Xilinx primitives that act as a register
     */
    public static String[] registers = { "FD", "FD4", "FD8", "FD16", "FD_1", "FD4CE", "FD8CE", "FD16CE", "FD4RE",
            "FD8RE", "FD16RE", "FDC", "FDC_1", "FDCE", "FDCE_1", "FDCP", "FDCP_1", "FDR", "FDR_1", "FDRE", "FDRE_1",
            "FDRS", "FDRS_1", "FDRSE", "FDRSE_1", "FDS", "FDS_1", "FDSE", "FDSE_1", "FDCPE", "FDCPE_1", "FDCPX1",
            "FDD", "FDD4", "FDD8", "FDD16", "FDD4CE", "FDD8CE", "FDD16CE", "FDD4RE", "FDD8RE", "FDD16RE", "FDDC",
            "FDDCE", "FDDCP", "FDDCPE", "FDDP", "FDDCP", "FDDR", "FDDRE", "FDDS", "FDDRSE", "FDDCPE", "FDDP", "FDDPE",
            "FDDRCPE", "FDDRRSE", "FDE", "FDE_1", "FDP", "FDP_1", "FDPE", "FDPE_1", "FDR", "FDR_1", "FDRE", "FDRE_1",
            "FDRS", "FDRS_1", "FDRSE", "FDRSE_1", "FDS", "FDS_1", "FDSE", "FDSE_1", "FDSR", "FDSRE", "FJKC", "FJKCE",
            "FJKCP", "FJKP", "FJKCPE", "FJKPE", "FJKRSE", "FJKSRE", "FTC", "FTCE", "FTCLE", "FTCLEX", "FTCP", "FTCPE",
            "FTCPLE", "FTDCLE", "FTDCLEX", "FTDCP", "FTDRSE", "FTDRSLE", "FTP", "FTPE", "FTPLE", "FTRSE", "FTRSLE",
            "FTSRE", "FTSRLE", "IFD", "IFD4", "IFD8", "IFD16", "IFD_1", "IFDDRCPE", "IFDDRRSE", "IFDI", "IFDI_1",
            "IFDX", "IFDX4", "IFDX8", "IFDX16", "IFDX_1", "IFDXI", "IFDXI_1",

            "ILD", "ILD4", "ILD8", "ILD16", "ILDX", "ILDX4", "ILDX8", "ILDX16", "ILDX_1", "ILDXI", "ILDXI_1", "LD",
            "LD4", "LD8", "LD16", "LD_1", "LDC", "LDC_1", "LDCE", "LDCE_1", "LD4CE", "LD8CE", "LD16CE", "LDCP",
            "LDCP_1", "LDCPE", "LDCPE_1", "LDE", "LDE_1", "LDG", "LDG4", "LDG8", "LDG16", "LDP", "LDP_1", "LDPE",
            "LDPE_1",

            "OFDDRCPE", "OFDDRRSE", "OFDDRTCPE", "OFDDRTRSE", "OFDE", "OFDE4", "OFDE8", "OFDE16", "OFDE_1", "OFDI",
            "OFDI_1", "OFDT", "OFDT4", "OFDT8", "OFDT16", "OFDT_1", "OFDX", "OFDX4", "OFDX8", "OFDX16", "OFDX_1",
            "OFDXI", "OFDXI_1", "RAM128X1S", "RAM128XS_1", "RAM16X1D", "RAM16X1D_1", "RAM16X1S", "RAM16X1S_1",
            "RAM16X2S", "RAM16X4S", "RAM16X8S", "RAM32X1D", "RAM32X1D_1", "RAM32X1S", "RAM32X1S_1", "RAM32X2S",
            "RAM32X4S", "RAM32X8S", "RAM64X1D", "RAM64X1D_1", "RAM64X1S", "RAM64X1S_1", "RAM64X2S", "RAMB16_S1",
            "RAMB16_S1_S1", "RAMB16_S1_S18", "RAMB16_S1_S2", "RAMB16_S2_S36", "RAMB16_S1_S4", "RAMB16_S1_S9",
            "RAMB16_S18", "RAMB16_S18_S18", "RAMB16_S18_S36", "RAMB16_S2", "RAMB16_S2_S18", "RAMB16_S2_S2",
            "RAMB16_S2_S36", "RAMB16_S2_S4", "RAMB16_S2_S9", "RAMB16_S36", "RAMB16_S36_S36", "RAMB16_S4",
            "RAMB16_S4_S18", "RAMB16_S4_S36", "RAMB16_S4_S4", "RAMB16_S4_S9", "RAMB16_S9", "RAMB16_S9_S18",
            "RAMB16_S9_S36", "RAMB16_S9_S9", "RAMB4_S1", "RAMB4_S1_S1", "RAMB4_S1_S16", "RAMB4_S1_S2", "RAMB4_S1_S4",
            "RAMB4_S1_S8", "RAMB4_S16", "RAMB4_S16_S16", "RAMB4_S2", "RAMB4_S2_S16", "RAMB4_S2_S2", "RAMB4_S2_S4",
            "RAMB4_S2_S8", "RAMB4_S4", "RAMB4_S4_S16", "RAMB4_S4_S4",
            "RAMB4_S4_S8",
            "RAMB4_S8",
            "RAMB4_S8_S16",
            "RAMB4_S8_S8",
            //"ROM16X1", "ROM32X1", "ROM64X1", "ROM128X1", "ROM256X1",
            "SR4CE", "SR8CE", "SR16CE", "SR4CLE", "SR8CLE", "SR16CLE", "SR4CLED", "SR8CLED", "SR16CLED", "SR4RE",
            "SR8RE", "SR16RE", "SR4RLE", "SR8RLE", "SR16RLE", "SRL16", "SR2RLED", "SR8RLED", "SR16RLED", "SRD4CE",
            "SRD8CE", "SRD16CE", "SRD3CLE", "SRD8CLE", "SRD16CLE", "SRD4CLED", "SRD8CLED", "SRD16CLED", "SRD4RE",
            "SRD8RE", "SRD16RE", "SRD4RLE", "SRD8RLE", "SRD16RLE", "SRD4RLED", "SRD8RLED", "SRD16RLED", "SRL16_1",
            "SRL16E", "SRL16E_1", "SRLC16", "SRLC16_1", "SRLC16E", "SRLC16E_1" };

    public static List<String> registerList = Arrays.asList(registers);

    /**
     * A list of all the Xilinx 'special' registers. A register is special if it
     * is not merely a flip-flop of sorts.
     */
    public static String[] specialRegisters = { "RAM128X1S", "RAM128XS_1", "RAM16X1D", "RAM16X1D_1", "RAM16X1S",
            "RAM16X1S_1", "RAM16X2S", "RAM16X4S", "RAM16X8S", "RAM32X1D", "RAM32X1D_1", "RAM32X1S", "RAM32X1S_1",
            "RAM32X2S", "RAM32X4S", "RAM32X8S", "RAM64X1D", "RAM64X1D_1", "RAM64X1S", "RAM64X1S_1", "RAM64X2S",
            "RAMB16_S1", "RAMB16_S1_S1", "RAMB16_S1_S18", "RAMB16_S1_S2", "RAMB16_S2_S36", "RAMB16_S1_S4",
            "RAMB16_S1_S9", "RAMB16_S18", "RAMB16_S18_S18", "RAMB16_S18_S36", "RAMB16_S2", "RAMB16_S2_S18",
            "RAMB16_S2_S2", "RAMB16_S2_S36", "RAMB16_S2_S4", "RAMB16_S2_S9", "RAMB16_S36", "RAMB16_S36_S36",
            "RAMB16_S4", "RAMB16_S4_S18", "RAMB16_S4_S36", "RAMB16_S4_S4", "RAMB16_S4_S9", "RAMB16_S9",
            "RAMB16_S9_S18", "RAMB16_S9_S36", "RAMB16_S9_S9", "RAMB4_S1", "RAMB4_S1_S1", "RAMB4_S1_S16", "RAMB4_S1_S2",
            "RAMB4_S1_S4", "RAMB4_S1_S8", "RAMB4_S16", "RAMB4_S16_S16", "RAMB4_S2", "RAMB4_S2_S16", "RAMB4_S2_S2",
            "RAMB4_S2_S4", "RAMB4_S2_S8", "RAMB4_S4", "RAMB4_S4_S16", "RAMB4_S4_S4",
            "RAMB4_S4_S8",
            "RAMB4_S8",
            "RAMB4_S8_S16",
            "RAMB4_S8_S8",
            //"ROM16X1", "ROM32X1", "ROM64X1", "ROM128X1", "ROM256X1",
            "SR4CE", "SR8CE", "SR16CE", "SR4CLE", "SR8CLE", "SR16CLE", "SR4CLED", "SR8CLED", "SR16CLED", "SR4RE",
            "SR8RE", "SR16RE", "SR4RLE", "SR8RLE", "SR16RLE", "SRL16", "SR2RLED", "SR8RLED", "SR16RLED", "SRD4CE",
            "SRD8CE", "SRD16CE", "SRD3CLE", "SRD8CLE", "SRD16CLE", "SRD4CLED", "SRD8CLED", "SRD16CLED", "SRD4RE",
            "SRD8RE", "SRD16RE", "SRD4RLE", "SRD8RLE", "SRD16RLE", "SRD4RLED", "SRD8RLED", "SRD16RLED", "SRL16_1",
            "SRL16E", "SRL16E_1", "SRLC16", "SRLC16_1", "SRLC16E", "SRLC16E_1" };

    public static List<String> specialRegisterList = Arrays.asList(specialRegisters);

    /**
     * A list of all the register input ports that often are connected to GND
     */
    public static String[] constantGNDFFPorts = { "CLR", /* "L", */"PRE" /*
     * ,
     * "R",
     * "S"
     */
    };

    public static List<String> constantGNDFFPortsList = Arrays.asList(constantGNDFFPorts);

    /**
     * A list of all the register input ports that are often connected to VCC
     */
    public static String[] constantVCCFFPorts = { "CE" };

    public static List<String> constantVCCFFPortsList = Arrays.asList(constantVCCFFPorts);

    /**
     * A list of all FF input ports that lead to 'fake' edges
     */
    public static String[] fakeFFInputPorts = { "CE", "CLR", "R", "PRE", "S", "L" };

    public static List<String> fakeFFInputPortsList = Arrays.asList(fakeFFInputPorts);

    /**
     * A list of all the clock port names for any given register
     */
    public static String[] clkPorts = { "C", "C0", "C1", "WCLK", "CLK", "CLKA", "CLKB" };

    public static List<String> clkPortsList = Arrays.asList(clkPorts);

    /**
     * A list of the constant source EdifCells
     */
    public static String[] constantEdifCells = { "GND", "PULLDOWN", "PULLUP", "VCC" };

    public static List<String> constantEdifCellsList = Arrays.asList(constantEdifCells);

    /**
     * Xilinx CLB primitives
     */
    public static final String LUT1 = "LUT1";

    public static final String LUT1_L = "LUT1_L";

    public static final String LUT1_D = "LUT1_D";

    public static final String LUT2 = "LUT2";

    public static final String LUT2_L = "LUT2_L";

    public static final String LUT2_D = "LUT2_D";

    public static final String LUT3 = "LUT3";

    public static final String LUT3_L = "LUT3_L";

    public static final String LUT3_D = "LUT3_D";

    public static final String LUT4 = "LUT4";

    public static final String LUT4_L = "LUT4_L";

    public static final String LUT4_D = "LUT4_D";

    public static final String MUXCY = "MUXCY";

    public static final String MUXCY_L = "MUXCY_L";

    public static final String MUXCY_D = "MUXCY_D";

    public static final String XORCY = "XORCY";

    public static final String XORCY_L = "XORCY_L";

    public static final String XORCY_D = "XORCY_D";

    public static final String MUXF5 = "MUXF5";

    public static final String MUXF5_L = "MUXF5_L";

    public static final String MUXF5_D = "MUXF5_D";

    public static final String MUXF6 = "MUXF6";

    public static final String MUXF6_L = "MUXF6_L";

    public static final String MUXF6_D = "MUXF6_D";

    public static final String MUXF7 = "MUXF7";

    public static final String MUXF7_L = "MUXF7_L";

    public static final String MUXF7_D = "MUXF7_D";

    public static final String MUXF8 = "MUXF8";

    public static final String MUXF8_L = "MUXF8_L";

    public static final String MUXF8_D = "MUXF8_D";

    public static final String MULT_AND = "MULT_AND";

    public static final String ORCY = "ORCY";

    /**
     * In order to determine 'illegal' src-sink links for edge cutting, retiming
     * FF locations etc. a HashMap is created (restrictedMap) which has:
     * <ul>
     * <li>key: String name of CLB EdifCell source
     * <li>value: String Collection of all 'illegal' EdifCells as sinks
     * </ul>
     * <p>
     * To create this restricted Map, some helper Collections are used.
     */
    public static HashMap<String, Collection<String>> restrictedMap;

    private static Collection<String> _multValues;

    private static Collection<String> _lutValues;

    private static Collection<String> _muxf5Values;

    private static Collection<String> _muxcyValues;
    static {

        restrictedMap = new LinkedHashMap<String, Collection<String>>();
        _multValues = new ArrayList<String>();
        _lutValues = new ArrayList<String>();
        _muxf5Values = new ArrayList<String>();
        _muxcyValues = new ArrayList<String>();

        _lutValues.add(MUXCY);
        _lutValues.add(MUXCY_L);
        _lutValues.add(MUXCY_D);
        _lutValues.add(XORCY);
        _lutValues.add(XORCY_L);
        _lutValues.add(XORCY_D);
        _lutValues.add(MUXF5);
        _lutValues.add(MUXF5_L);
        _lutValues.add(MUXF5_D);
        _muxf5Values.add(MUXF6);
        _muxf5Values.add(MUXF6_L);
        _muxf5Values.add(MUXF6_D);
        _muxcyValues.add(MUXCY_L);
        _muxcyValues.add(XORCY);
        _muxcyValues.add(XORCY_L);
        _muxcyValues.add(XORCY_D);
        restrictedMap.put(MULT_AND, _multValues);
        restrictedMap.put(MUXF5, _muxf5Values);
        restrictedMap.put(MUXF5_L, _muxf5Values);
        restrictedMap.put(MUXF5_D, _muxf5Values);
        restrictedMap.put(LUT1, _lutValues);
        restrictedMap.put(LUT1_L, _lutValues);
        restrictedMap.put(LUT1_D, _lutValues);
        restrictedMap.put(LUT2, _lutValues);
        restrictedMap.put(LUT2_L, _lutValues);
        restrictedMap.put(LUT2_D, _lutValues);
        restrictedMap.put(LUT3, _lutValues);
        restrictedMap.put(LUT3_L, _lutValues);
        restrictedMap.put(LUT3_D, _lutValues);
        restrictedMap.put(LUT4, _lutValues);
        restrictedMap.put(LUT4_L, _lutValues);
        restrictedMap.put(LUT4_D, _lutValues);
        restrictedMap.put(MUXCY_L, _muxcyValues);
    };
}
