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

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxResourceMapper;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;

public abstract class XilinxIOBAnalyzer extends AbstractIOBAnalyzer {

    /**
     * Creates a new XilinxIOB object and fills in the appropriate information
     * using the given EdifCellInstanceGraph graph.
     * 
     * @param esbp The EdifSingleBitPort object to generate the XilinxIOB from
     * @param graph The EdifCellInstanceGraph graph to use
     * @param packInputRegisters Packs registers into input IOBs
     * @param packOutputRegisters Packs register into output IOBs
     * @return a newly-created XilinxIOB object with all IOB information
     */
    public static XilinxVirtexIOB createXilinxIOBFromPort(EdifSingleBitPort esbp, EdifCellInstanceGraph graph,
            boolean packInputRegisters, boolean packOutputRegisters) {
        //				
        //      Input Procedure:
        //      - Create new XilinxIOB object
        //      - Grab all successors of ESBP
        //        - If there is a single successor and it is of type IO or IBUFG, add as inBUF
        //        -  Else if there is a single successor and it is a FF, add as InputReg
        //        -  Else if there are multiple successors and only one FF, add it as InputReg
        //        -  Else do nothing
        //      - Grab all successors of IBUF (if found)
        //        - If there is a single successor and it is a FF, add as InputReg
        //        -  Else if there are multiple successors and only one FF, add it as InputReg
        //        -  Else do nothing
        //      - Grab all predecessors of ESBP
        //        - If any of the predecessors is a resistor type, add as resistor
        //          (I assume there can be only one)
        //        
        //      Output Procedure:
        //      - Create new XilinxIOB object
        //      - Grab all predecessors of ESBP
        //        - If any of the predecessors is a resistor type, add as resistor
        //          (I assume there can be only one)
        //        - If there is a predecessor of type IO, add as outBUF
        //        -  Else if there is a predecessor of port 'I' and it is a FF, and it drives no other outputs, add it as OutputReg
        //        -  Else if there is a predecessor of port 'T' and it is a FF, and it drives no other outputs, add it as TristateReg
        //        -  Else do nothing
        //      - Grab all predecessors of OBUF (if found)
        //        - If there is a predecessor of port 'I' and it is a FF, and it drives no other outputs, add it as OutputReg
        //        -  Else if there is a predecessor of port 'T' and it is a FF, and it drives no other outputs, add it as TristateReg
        //        -  Else do nothing
        //       
        //      Inout Procedure:
        //      (special case: There may be an IOBUF, or and IBUF and OBUF or nothing)
        //      - Create new XilinxIOB object
        //      - Do Input procedure
        //      - Do Output procedure
        //      - Clean-up
        //        - If inBUF and outBUF are the same object, add it as inoutBUF and
        //          remove references from inBUF and outBUF
        //        
        //      Caveats:
        //      - All registers must have same clock in order to be packed
        //      - An IOBUF will show up as an IO element which is both a predecessor 
        //        and a successor of the top-level port
        //     
        //				 
        XilinxVirtexIOB xiob = new XilinxVirtexIOB(esbp);
        Collection<?> portPredecessors = graph.getPredecessors(esbp);
        Collection<?> portSuccessors = graph.getSuccessors(esbp);
        if (_debug)
            System.out.println("Predecessors of Port " + esbp + ": " + portPredecessors);
        if (_debug) {
            System.out.println("Successors of Port " + esbp + ": " + portSuccessors);
            System.out.println("Successor Edges of Port " + esbp + ": " + graph.getOutputEdges(esbp));
        }

        // Three cases: Input, Output, or Inout Port
        // For the Inout case, do both the Input and Output cases and 
        //   fix up the results.
        EdifPort port = esbp.getParent();
        if (port.isInput()) {
            // Check for inBuf (using port)
            // If there is an I/O Buffer, it is the only successor
            if (portSuccessors.size() == 1) {
                Object successor = portSuccessors.iterator().next();
                // Exclude top-level ports
                if (successor instanceof EdifCellInstance) {
                    EdifCellInstance eci = (EdifCellInstance) successor;
                    String resourceType = XilinxResourceMapper.getInstance().getResourceType(eci);
                    if (resourceType.equals(XilinxResourceMapper.IO) || resourceType.equals(XilinxResourceMapper.IBUFG)) {
                        xiob.setIBUF(eci, graph);
                        if (_debug)
                            System.out.println("Found IBUF: " + eci);
                    }
                }
            }

            // Check for clock port (If so, can't pack registers in the IOB)
            if (xiob.canPack() && packInputRegisters) { // Only pack if user specified so
                // Check for input Reg (using inBuf if it exists or port otherwise)
                Object source = null;
                EdifCellInstance ibuf = xiob.getIBUF();
                if (ibuf != null)
                    source = ibuf;
                else
                    source = esbp;
                EdifCellInstance inReg = findInputIOBRegister(source, graph);
                if (inReg != null)
                    xiob.setInputReg(inReg);
            }
        }//if (port.isInput()) 
        if (port.isOutput()) {
            // Check for outBuf (using port)
            // If there is an I/O Buffer, it is a predecessor (but not necessarily
            //   the only predecessor--a resistor might be one too)
            for (Object predecessor : portPredecessors) {
                // Exclude top-level ports
                if (predecessor instanceof EdifCellInstance) {
                    EdifCellInstance eci = (EdifCellInstance) predecessor;
                    // Check for an I/O typed object
                    if (XilinxResourceMapper.IO.equals(XilinxResourceMapper.getInstance().getResourceType(eci)))
                        xiob.setOBUF(eci);
                }
            }

            // Check for output Reg (using outBuf if it exists or port otherwise)
            if (packOutputRegisters) { // Only pack if user specified so
                Object source = null;
                EdifCellInstance obuf = xiob.getOBUF();
                if (obuf != null) {
                    if (_debug)
                        System.out.println("Found OBUF: " + obuf);
                    source = obuf;
                } else
                    source = esbp;
                EdifCellInstance outReg = findOutputIOBRegister(source, graph, true);
                if (outReg != null)
                    xiob.setOutputReg(outReg);
                EdifCellInstance triReg = findOutputIOBRegister(source, graph, false);
                if (triReg != null)
                    xiob.setTristateReg(triReg);
            }

        }
        if (port.isInOut()) {
            // Both Input and Output sections were previously run
            // An IOBUF will show up as an IO element which is both a predecessor 
            //    and a successor of the top-level port
            // Clean-up
            //  - If inBUF and outBUF are the same object, add it as inoutBUF and
            //    remove references from inBUF and outBUF
            //  -   Else do nothing. This inout port was implemented with an
            //      IBUF and an OBUF separately or not fully used as an inout.
            EdifCellInstance ibuf = xiob.getIBUF();
            EdifCellInstance obuf = xiob.getOBUF();
            if (ibuf != null && ibuf.equals(obuf)) {
                if (_debug)
                    System.out.println("Merged IBUF and OBUF");
                xiob.setIOBUF(ibuf);
                xiob.setIBUF(null);
                xiob.setOBUF(null);
            }
        }

        // Check for resistor (same for all types of ports)
        // A pullup/pulldown/keeper will show up as a predecessor
        for (Object predecessor : portPredecessors) {
            // Exclude top-level ports
            if (predecessor instanceof EdifCellInstance) {
                EdifCellInstance eci = (EdifCellInstance) predecessor;
                // Check for a "Resistor" typed object
                if (XilinxResourceMapper.RES.equals(XilinxResourceMapper.getInstance().getResourceType(eci))) {
                    if (_debug)
                        System.out.println("Found Resistor element: " + eci);
                    xiob.setResistor(eci);
                }
            }
        }

        // TODO: Add check to make sure the clock going to all of the IOB
        //   registers is the same (otherwise, dump them all?)

        return xiob;
    }

    /**
     * Uses the "source" node given as an input port and finds any flip-flop
     * instances which qualify as IOB register (those that the Xilinx tools
     * would "pack" into the IOBs)
     * 
     * @param source The top-level input port or the IBUF object that the port
     * is attached to
     * @param graph The EdifCellInstanceGraph graph for the parent Cell
     * @return The EdifCellInstance object of the IOB register for this port, or
     * null if there is none
     */
    protected static EdifCellInstance findInputIOBRegister(Object source, EdifCellInstanceGraph graph) {
        Collection<?> successors = null;
        successors = graph.getSuccessors(source);

        if (_debug)
            System.out.println("Successors of IOB " + source + ": " + successors);

        EdifCellInstance iobReg = null;
        // An attached register is a valid Input IOB register if it is the 
        //   *only* register in the set of successors
        for (Object successor : successors) {
            // Ignore any object which are not EdifCellInstances (Top-level ports)
            if (!(successor instanceof EdifCellInstance))
                continue;
            // See if this neighbor is a register
            EdifCellInstance eci = (EdifCellInstance) successor;
            if (XilinxResourceMapper.FF.equals(XilinxResourceMapper.getInstance().getResourceType(eci))) {
                // If any registers have already been found, there is not a valid
                //   IOB register (there can only be one)
                if (iobReg != null)
                    return null;
                // Check for IOB property. If IOB=FALSE, don't pack this register
                if (!hasIOBFalseProperty(eci)) {
                    iobReg = eci;
                } else if (_debug)
                    System.out.println("Input IOB register has IOB=FALSE propery. This register will not be packed: "
                            + iobReg);
            }
        }
        
        if (_debug && iobReg != null)
            System.out.println("Input IOB register: " + iobReg);
        return iobReg;
    }

    /**
     * Uses the "sink" node given as an output port and finds any flip-flop
     * instances which qualify as IOB register (those that the Xilinx tools
     * would "pack" into the IOBs)
     * 
     * @param source The top-level output port or the OBUF object that the port
     * is attached to
     * @param graph The EdifCellInstanceGraph graph for the parent Cell
     * @param type A boolean value which specifies if an "output" register or a
     * "tristate" register should be searched for
     * @return The EdifCellInstance object of the IOB register for this port, or
     * null if there is none
     */
    protected static EdifCellInstance findOutputIOBRegister(Object sink, EdifCellInstanceGraph graph, boolean type) {
        Collection<?> predecessors = null;
        // Two cases:
        // 1. FF attached directly to port
        // 2. FF attached to IOB input 'I'    - type = true
        // (3. FF attached to IOB input 'T')  - type = false
        if (sink instanceof EdifCellInstance) {
            // Sink is an I/O buffer
            if (type == true)
                predecessors = graph.getPredecessors(sink, "I");
            else
                predecessors = graph.getPredecessors(sink, "T");
            // TODO: Add Debug statements to check this out
        } else {
            // Sink is a top-level port
            predecessors = graph.getPredecessors(sink);
            // TODO: Add Debug statements to check this out
        }

        if (_debug)
            System.out.println("Predecessors of " + sink + "[" + (type ? "I" : "T") + "]: " + predecessors);

        for (Object predecessor : predecessors) {
            // Ignore any object which are not EdifCellInstances (Top-level ports)
            if (!(predecessor instanceof EdifCellInstance))
                continue;
            // See if this neighbor is a register
            EdifCellInstance eci = (EdifCellInstance) predecessor;
            if (XilinxResourceMapper.FF.equals(XilinxResourceMapper.getInstance().getResourceType(eci))) {
                // This sink is driven by a register
                // Make sure this register doesn't drive anything else (otherwise
                //   it cannot be placed in an IOB)
                if (graph.getSuccessors(eci).size() != 1)
                    continue;
                // Check for IOB property. If IOB=FALSE, don't pack this register
                if (!hasIOBFalseProperty(eci)) {
                    if (_debug)
                        System.out.println("Output IOB register: " + eci);
                    return eci;
                }
            }
        }

        return null;
    }

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
