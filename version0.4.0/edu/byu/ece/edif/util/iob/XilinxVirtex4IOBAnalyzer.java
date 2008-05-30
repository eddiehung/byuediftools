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
package edu.byu.ece.edif.util.iob;

import java.util.Collection;

import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;

public class XilinxVirtex4IOBAnalyzer extends XilinxIOBAnalyzer {
    /**
     * Analyzes the EdifCell, extracting the IOB instances from the Connectivity
     * graph. Assumes the EdifCell is a top-level cell (that the top-level ports
     * will be connected to I/O pads).
     */
    protected void analyze() {
        // Steps:
        // Iterate over top-level ports
        // - Look for I/O BUF instance attached to port (IBUF, OBUF, IOBUF, etc.)
        //   - There may not be one (this is still valid)
        // - Look for Resistor/Keeper instance attached to port
        //   - These drive the port, whether input or output
        //   - There may not be one
        // - Look for input register attached to IBUF (or IOBUF)
        //   - Or attached directly to the port if no I/O BUF
        // - Look for output register attached to OBUF (or IOBUF)
        //   - Or attached directly to the port if no I/O BUF
        // - Look for tristate register attached to OBUFT (or IOBUFT???)
        //   - Or attached directly to the port if no I/O BUF???

        // Iterate over all top-level ports
        for (EdifPort port : _graph.getCell().getPortList()) {
            for (EdifSingleBitPort esbp : port.getSingleBitPortList()) {
                // - Look for I/O BUF instance attached to port (IBUF, OBUF, IOBUF, etc.)
                //   - There may not be one (this is still valid)
                XilinxVirtexIOB xiob = createXilinxIOBFromPort(esbp, _graph, _packInputRegs, _packOutputRegs);
                // Add to mapping
                _iobMap.put(esbp, xiob);

                // If port is Inout, Get IOB feedback cut location(s)
                if (esbp.getParent().isInOut()) {
                    // Type 1: IOBUF instance. Cut output ('O' port) connection.
                    // Type 2: IBUF and OBUF. Cut input ('I' port) connection on IBUF.
                    // Type 3: No BUF instances. Cut connection(s) driven by top-level port.
                    // (Type 4: Only one of IBUF and OBUF but still has both connections?)
                    Collection<EdifCellInstanceEdge> outputEdges = null;
                    if (xiob.getIOBUF() != null) {
                        outputEdges = _graph.getOutputEdges(xiob.getIOBUF(), "O");
                    } else if (xiob.getIBUF() != null && xiob.getOBUF() != null) {
                        outputEdges = _graph.getInputEdges(xiob.getIBUF(), "I");
                    } else {
                        outputEdges = _graph.getOutputEdges(esbp);
                    }
                    _iobFeedbackEdges.addAll(outputEdges);
                    _feedbackIOBMap.put(esbp, outputEdges);
                }
            }
        }
    }

}
