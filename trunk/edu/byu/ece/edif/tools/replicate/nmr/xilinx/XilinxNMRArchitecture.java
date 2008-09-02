/*
 * A Xilinx implementation of NMR to be implemented for different Xilinx parts.
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
package edu.byu.ece.edif.tools.replicate.nmr.xilinx;

import edu.byu.ece.edif.arch.xilinx.XilinxTools;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.tools.replicate.nmr.AbstractNMRArchitecture;

/**
 * Provides a Xilinx implementation of NMR. It is intended that this class be
 * implemented by the different families of Xilinx parts.
 * 
 * @since Created on May 23, 2005
 */
public abstract class XilinxNMRArchitecture extends AbstractNMRArchitecture {

    public XilinxNMRArchitecture() {
        // Initialize the set of known Bad Cut Connections for Xilinx parts
        _badCutConnections = new XilinxBadCutConnections();
        _init();
    }

    //	public boolean isTriplicatableCellType(EdifCellInstance eci) {
    //        boolean result;
    //        String cellTypeName = eci.getCellType().getName();
    //        if (XILINX_NON_TRIPLICATABLE_CELLS.contains(cellTypeName))
    //            result = false;
    //        else
    //            result = true;
    //        return result;
    //    }
    //
    //    // List of non-triplicatable cell-types in the Xilinx architecture. HashSet
    //    // String look-up is fast.
    //    static protected final HashSet<String> XILINX_NON_TRIPLICATABLE_CELLS;
    //    static {
    //        XILINX_NON_TRIPLICATABLE_CELLS = new LinkedHashSet<String>();
    //        XILINX_NON_TRIPLICATABLE_CELLS.add("BUFG");
    //        XILINX_NON_TRIPLICATABLE_CELLS.add("IBUF");
    //        XILINX_NON_TRIPLICATABLE_CELLS.add("IBUFG");
    //        XILINX_NON_TRIPLICATABLE_CELLS.add("OBUF");
    //        XILINX_NON_TRIPLICATABLE_CELLS.add("OBUFG");
    //        XILINX_NON_TRIPLICATABLE_CELLS.add("OBUFT");
    //        XILINX_NON_TRIPLICATABLE_CELLS.add("CAPTURE_VIRTEX");
    //        XILINX_NON_TRIPLICATABLE_CELLS.add("STARTUP_VIRTEX");
    //        XILINX_NON_TRIPLICATABLE_CELLS.add("GND");
    //        XILINX_NON_TRIPLICATABLE_CELLS.add("VCC");
    //    }

    // Javadoc comment inherited from TMRArchitecture.
    public boolean isAFlipFlop(String cellType) {
        for (int i = 0; i < XILINX_FLIP_FLOPS.length; i++) {
            if (cellType.compareToIgnoreCase(XILINX_FLIP_FLOPS[i]) == 0)
                return true;
        }
        return false;
    }

    public boolean isBRAM(String cellType) {
        boolean result;
        if (cellType.toLowerCase().matches("ramb"))
            result = true;
        else
            result = false;
        return result;
    }

    /**
     * Initialize the set of Bad Cut Connections for this architecture. This
     * list can be less verbose when utilizing the shortcuts enabled in the
     * XilinxBadCutConnections class. E.g. MUXCY, MUXCY_L, and MUXCY_D can all
     * be treated the same with the name MUXCY.
     */
    // TODO: Verify and completely fill in this set
    protected void _init() {
        // These cuts are just plain illegal
        _badCutConnections.addBadCutConnection("MULT_AND", "LO", "MUXCY", "DI");
        _badCutConnections.addBadCutConnection("MUXCY", "LO", "XORCY", "CI");
        _badCutConnections.addBadCutConnection("MUXCY", "O", "XORCY", "CI");
        // These cuts are sometimes legal, but it's a lot easier to just 
        // call all cuts in the carry chain "bad"
        _badCutConnections.addBadCutConnection("MUXCY", "O", "MUXCY", "CI");
        _badCutConnections.addBadCutConnection("MUXCY", "LO", "MUXCY", "CI");
        // Not necessary?
        // An XORCY output *can* exit the slice and drive another flip-flop
        //   Maybe the LO output cannot, though...
        //_badCutConnections.addBadCutConnection("XORCY", "LO", "FD", "D");
        //_badCutConnections.addBadCutConnection("XORCY", "O", "FD", "D");
        //_badCutConnections.addBadCutConnection("LUT", "LO", "FD", "D");
        //_badCutConnections.addBadCutConnection("LUT", "O", "FD", "D");

        // Prevent cuts between MUXF?'s
        _badCutConnections.addBadCutConnection("MUXF5", "O", "MUXF6", "I0");
        _badCutConnections.addBadCutConnection("MUXF5", "O", "MUXF6", "I1");
        _badCutConnections.addBadCutConnection("MUXF6", "O", "MUXF7", "I0");
        _badCutConnections.addBadCutConnection("MUXF6", "O", "MUXF7", "I1");
        _badCutConnections.addBadCutConnection("MUXF7", "O", "MUXF8", "I0");
        _badCutConnections.addBadCutConnection("MUXF7", "O", "MUXF8", "I1");

        // Prevent all cuts on inputs to MUXF6's I1 port
        _badCutConnections.addBadCutConnection("*", "*", "MUXF6", "I1");

        // Prevent cuts before input buffers and after output buffers
        // Q: Is this necessary? I can't remember why I did this and in another
        //    design (the 2nd Astrium design) it doesn't seem to be necessary.
        // A: The idea here is to prevent cuts at the pad. In the afore-
        //    mentioned design, the IOBUFs cause a problem because normal
        //    elements can be attached to the "I" and "O" ports
        _badCutConnections.addBadCutConnection("*", "*", "IBUF", "I");
        //_badCutConnections.addBadCutConnection("*", "*", "IOBUF", "I");
        //_badCutConnections.addBadCutConnection("IOBUF", "O", "*", "*");
        _badCutConnections.addBadCutConnection("OBUF", "O", "*", "*");
        //DSP ports in V4 and greater
        _badCutConnections.addBadCutConnection("DSP48", "PCOUT", "DSP48", "PCIN");
        _badCutConnections.addBadCutConnection("DSP48", "BCOUT", "DSP48", "BCIN");

        _badCutConnections.addBadCutConnection("*", "*", "IDELAYCTL", "REFCLK");
        _badCutConnections.addBadCutConnection("*", "*", "IDELAYCTL", "RST");
        _badCutConnections.addBadCutConnection("BUFG", "O", "IDELAYCTL", "REFCLK");

        _badCutConnections.addBadCutConnection("DCM", "PSEN", "*", "*");

    }

    /**
     * @return true if the given EdifNet is a clock net, false otherwise
     */
    public boolean isClockNet(EdifNet net) {
        return XilinxTools.isClockNet(net);
    }

    /** The EdifCell object that implements the majority voting. */
    protected EdifCell _voterCell;

    /**
     * List of all flip-flop primitives in the Xilinx library.
     */
    protected final String XILINX_FLIP_FLOPS[] = { "FDC", "FDC_1", "FD", "FD_1", "FDCE", "FDCE_1", "FDCP", "FDCP_1",
            "FDCPE", "FDCPE_1", "FDCPX1", "FDCPX1_1", "FDDRCPE", "FDDRRSE", "FDE", "FDE_1", "FDP", "FDP_1", "FDPE",
            "FDPE_1", "FDR", "FDR_1", "FDRE", "FDRE_1", "FDRS", "FDRS_1", "FDRSE", "FDRSE_1", "FDS", "FDS_1", "FDSE",
            "FDSE_1" };

}
