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
package edu.byu.ece.edif.tools.sterilize.halflatch;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;

/**
 * @since Created on Oct 31, 2005
 */
public class XilinxProblemPrimitiveMap implements ProblemPrimitiveMap, Serializable {

    /**
     * Constructs a new XilinxProblemPrimitiveMap object, which contains sub
     * maps corresponding to IOB primitives and CLB primitives.
     * 
     * @param cell The EdifCell this XilinxProblemPrimitiveMap should be
     * associated with.
     */
    public XilinxProblemPrimitiveMap(EdifCell cell) {
        _cell = cell;

        // Analyze Design
        // - An EdifCell is passed in
        // - Find special case primitives
        //   - Find IOB registers
        //     - Create ConnectivityGraph
        //     - Grab IOB registers (assuming top level ports of this Cell are TOP top level ports???)
        //        (maybe we need to take a boolen value saying if this is the TOP level cell or not)
        //   - No other special cases for now
        _iobPrimitives = new LinkedHashSet<EdifCellInstance>();

        // Generate sub maps for CLB and IOB primitives
        _clbMap = XilinxCLBProblemPrimitiveMap.getXilinxCLBProblemPrimitiveMap();
        _iobMap = XilinxIOBProblemPrimitiveMap.getXilinxIOBProblemPrimitiveMap();
    }

    /**
     * Add the given EdifCellInstances to the the list of IOB primitives in this
     * ProblemPrimitiveMap. These instances will be handled as IOB instances
     * rather than CLB instances.
     * 
     * @param iobRegs A Collection of EdifCellInstances that correspond to
     * registers to be placed in an IOB
     */
    public void addIOBRegisters(Collection<EdifCellInstance> iobRegs) {
        // Could do a check here to make sure they belong to this EdifCell
        //  (An assertion--would have to throw a RuntimeException or something)
        _iobPrimitives.addAll(iobRegs);
    }

    public String getPrimitiveReplacementType(EdifCellInstance primitiveECI) {
        String primitiveReplacementType = null;
        // Choose the correct ProblemPrimitiveMap to use
        if (_iobPrimitives.contains(primitiveECI))
            primitiveReplacementType = _iobMap.getPrimitiveReplacementType(primitiveECI);
        else
            primitiveReplacementType = _clbMap.getPrimitiveReplacementType(primitiveECI);

        return primitiveReplacementType;
    }

    public String[] getPrimitiveReplacementFloatingPorts(EdifCellInstance primitiveECI) {
        String[] primitiveReplacementFloatingPorts = null;
        // Choose the correct ProblemPrimitiveMap to use
        if (_iobPrimitives.contains(primitiveECI))
            primitiveReplacementFloatingPorts = _iobMap.getPrimitiveReplacementFloatingPorts(primitiveECI);
        else
            primitiveReplacementFloatingPorts = _clbMap.getPrimitiveReplacementFloatingPorts(primitiveECI);

        return primitiveReplacementFloatingPorts;
    }

    public int getPrimitiveReplacementFloatingPortDefaultValue(EdifCellInstance primitiveECI, String floatingPort) {
        if (primitiveECI == null || floatingPort == null)
            return -1;
        int primitiveReplacementFloatingPortDefaultValue = -1;

        // Choose the correct ProblemPrimitiveMap to use
        if (_iobPrimitives.contains(primitiveECI))
            primitiveReplacementFloatingPortDefaultValue = _iobMap.getPrimitiveReplacementFloatingPortDefaultValue(
                    primitiveECI, floatingPort);
        else
            primitiveReplacementFloatingPortDefaultValue = _clbMap.getPrimitiveReplacementFloatingPortDefaultValue(
                    primitiveECI, floatingPort);

        return primitiveReplacementFloatingPortDefaultValue;
    }

    // The EdifCell object this Map is associated with
    EdifCell _cell;

    // Sub Maps for the different parts of the chip
    XilinxCLBProblemPrimitiveMap _clbMap;

    XilinxIOBProblemPrimitiveMap _iobMap;

    // Collections to store special cases
    Set<EdifCellInstance> _iobPrimitives;

    // Local Debug
    public static final boolean _debug = false;

    /*
     * Code to test this class
     */
    public static int test() {

        //		ProblemPrimitiveMap xilinxProblemPrimitiveMap = new XilinxProblemPrimitiveMap();
        //
        //        String error_report = "";
        //
        //        // Test Case 1
        //        // Get replacement cell type for an 'FD' primitive flip-flop (should be an FDCPE)
        //        // Get the unconnected ports in the replacement cell (should be [ce, pre, clr])
        //        // Get the unconnected ports default values (should be ce=1, pre=0, clr=0)		
        //        final String test_cell_1 = "fd";
        //        final String test_cell_1_replacement = "fdcpe";
        //        final String[] test_cell_1_replacement_unconnected_ports = { "clr", "ce", "pre" };
        //        final int[] test_cell_1_replacement_unconnected_ports_default_values = { 0, 1, 0 };
        //
        //        String tc1_replacement_type = xilinxProblemPrimitiveMap.getPrimitiveReplacementType(test_cell_1);
        //        if (tc1_replacement_type == null || tc1_replacement_type.compareToIgnoreCase(test_cell_1_replacement) != 0)
        //            error_report += ("\n Error: XilinxProblemPrimitiveMap: " + tc1_replacement_type
        //                    + " is the incorrect replacement type for cell " + test_cell_1 + ".  Should be "
        //                    + test_cell_1_replacement + " .");
        //        String[] tc1_replacement_unconnected_ports = xilinxProblemPrimitiveMap
        //                .getPrimitiveReplacementFloatingPorts(test_cell_1);
        //        if (tc1_replacement_unconnected_ports == null) {
        //            error_report += ("\n Error: XilinxProblemPrimitiveMap: Could not find unconnected ports for " + test_cell_1);
        //        } else {
        //            for (int i = 0; i < test_cell_1_replacement_unconnected_ports.length; i++) {
        //                boolean portFound = false;
        //                for (int j = 0; j < tc1_replacement_unconnected_ports.length; j++) {
        //                    if (test_cell_1_replacement_unconnected_ports[i]
        //                            .compareToIgnoreCase(tc1_replacement_unconnected_ports[j]) == 0)
        //                        portFound = true;
        //                }
        //                if (portFound == false)
        //                    error_report += ("\n Error: XilinxProblemPrimitiveMap: Could not find expected unconnected port "
        //                            + test_cell_1_replacement_unconnected_ports[i] + " for cell " + test_cell_1);
        //            }
        //        }
        //        for (int i = 0; i < test_cell_1_replacement_unconnected_ports.length; i++) {
        //            int portIDefaultValue = xilinxProblemPrimitiveMap.getPrimitiveReplacementFloatingPortDefaultValue(
        //                    test_cell_1, test_cell_1_replacement_unconnected_ports[i]);
        //            if (portIDefaultValue != test_cell_1_replacement_unconnected_ports_default_values[i])
        //                error_report += ("\n Error: XilinxProblemPrimitiveMap: Cell " + test_cell_1
        //                        + " has unconnected replacement cell port " + test_cell_1_replacement_unconnected_ports[i]
        //                        + " with default value " + test_cell_1_replacement_unconnected_ports_default_values[i]
        //                        + " but method returned " + portIDefaultValue + " as default.");
        //        }
        //
        //        if (error_report != "") {
        //            System.out.println(error_report);
        //            return -1;
        //        } else {
        //            System.out.println("Success: XilinxProblemPrimitiveMap: All tests passed");
        //            return 0;
        //        }

        return 0;
    }

    public static void main(String[] args) {
        XilinxProblemPrimitiveMap.test();
    }
}
