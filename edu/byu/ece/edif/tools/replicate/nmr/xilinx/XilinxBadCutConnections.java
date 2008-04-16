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
package edu.byu.ece.edif.tools.replicate.nmr.xilinx;

import edu.byu.ece.edif.tools.replicate.nmr.BadCutConnections;

public class XilinxBadCutConnections extends BadCutConnections {

    /**
     * Internal function to keep the lookup values consistent for a given
     * EdifCell, EdifPortRef pair
     * <p>
     * This Xilinx-specific version simplifies this by lumping certain groups of
     * Cells together such as FlipFlops, MUXCY_L and MUXCY, etc.
     * <p>
     * These should be used in XilinxNMRArchitecture in order to simplify the
     * look-up of bad cut connections.
     * 
     * @return A standardized String to use in the internal Map
     */
    protected String _getEPRName(String cellType, String portName) {
        if (cellType.startsWith("FD"))
            return "FD_" + portName;
        else if (cellType.startsWith("MUXCY"))
            return "MUXCY_" + portName;
        else if (cellType.startsWith("MUXF"))
            // include up through MUXF#, but don't include _D, _L, etc.
            return cellType.substring(0, 4) + "_" + portName;
        else if (cellType.startsWith("LUT"))
            // include up through LUT#, but don't include _D, _L, etc.
            return cellType.substring(0, 3) + "_" + portName;
        else if (cellType.startsWith("XORCY"))
            return "XORCY_" + portName;
        else if (cellType.startsWith("IBUF"))
            return "IBUF_" + portName;
        else if (cellType.startsWith("OBUF"))
            return "OBUF_" + portName;
        else if (cellType.startsWith("IOBUF"))
            return "IOBUF_" + portName;
        else
            // Default. Return full cell type mixed wih port name
            return cellType + "_" + portName;
    }
}
