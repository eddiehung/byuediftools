/*
 * Maps "bad primitives" to replacement primitives, specifically in Xilinx IOBs.
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
import java.util.LinkedHashMap;

/**
 * This class provides a mapping from "Problem Primitives" to their replacement
 * primitives and is specifically designed for primitives that will be placed in
 * the IOBs of a Xilinx part.
 * <p>
 * The registers in the IOBs do not have both a reset and preset line, so the
 * replacement flip-flops in this mapping only have one of the two (e.g. FDCE
 * vs. FDCPE for a CLB register)
 * <p>
 * TODO:
 * <ul>
 * <li>What to do about invalid primitives???
 * <li>Should they be handled as CLB regs? Throw an exception?
 * <li>BHP: For now, I just silently replace handle them as CLB registers
 * </ul>
 * 
 * @since Created on Oct 31, 2005
 */
public class XilinxIOBProblemPrimitiveMap extends AbstractProblemPrimitiveMap implements Serializable {

    private XilinxIOBProblemPrimitiveMap() {
        // Generate the internal maps
        // Map 1 maps primitive names to the corresponding 'safe primitive' name
        // Map 2 maps primitive names to the will-be floating port names once it is replaced by its corresponding 'safe primitive'
        // Map 3 maps primitive names to the will-be floating port default values (once it is replaced by its corresponding 'safe primitive')
        generateMaps();
    }

    public static XilinxIOBProblemPrimitiveMap getXilinxIOBProblemPrimitiveMap() {
        if (_singletonInstance == null)
            _singletonInstance = new XilinxIOBProblemPrimitiveMap();

        return _singletonInstance;
    }

    private void generateMaps() {
        _safePrimitiveMap = new LinkedHashMap();
        _floatingPortMap = new LinkedHashMap();
        _floatingPortDefaultValueMap = new LinkedHashMap();

        /*
         * For each primitive entry, there should be a corresponding call to add
         * values to *both* the _safePrimitiveMap and _floatingPortMap maps.
         */

        /*
         * IMPORTANT: This class requires that the primitive strings be entered
         * with UPPERCASE letters
         */

        // OBUF
        _safePrimitiveMap.put("OBUF", "OBUFT");
        final String[] obuf_floating_ports = { "T" };
        final int[] obuf_floating_ports_defaults = { 0 };
        _floatingPortMap.put("OBUF", obuf_floating_ports);
        _floatingPortDefaultValueMap.put("OBUF", obuf_floating_ports_defaults);

        // FD
        _safePrimitiveMap.put("FD", "FDCE");
        final String[] fd_floating_ports = { "CE", "CLR" };
        final int[] fd_floating_ports_defaults = { 1, 0 };
        _floatingPortMap.put("FD", fd_floating_ports);
        _floatingPortDefaultValueMap.put("FD", fd_floating_ports_defaults);

        // FD_1
        _safePrimitiveMap.put("FD_1", "FDCE_1");
        final String[] fd_1_floating_ports = { "CE", "CLR" };
        final int[] fd_1_floating_ports_defaults = { 1, 0 };
        _floatingPortMap.put("FD_1", fd_1_floating_ports);
        _floatingPortDefaultValueMap.put("FD_1", fd_1_floating_ports_defaults);

        // FDC
        _safePrimitiveMap.put("FDC", "FDCE");
        final String[] fdc_floating_ports = { "CE" };
        final int[] fdc_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDC", fdc_floating_ports);
        _floatingPortDefaultValueMap.put("FDC", fdc_floating_ports_defaults);

        // FDC_1
        _safePrimitiveMap.put("FDC_1", "FDCE_1");
        final String[] fdc_1_floating_ports = { "CE" };
        final int[] fdc_1_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDC_1", fdc_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDC_1", fdc_1_floating_ports_defaults);

        // FDCE is Safe

        // FDCE_1 is Safe

        // FDCP is invalid for an IOB register - Handle as CLB register
        _safePrimitiveMap.put("FDCP", "FDCPE");
        final String[] fdcp_floating_ports = { "CE" };
        final int[] fdcp_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDCP", fdcp_floating_ports);
        _floatingPortDefaultValueMap.put("FDCP", fdcp_floating_ports_defaults);

        // FDCP_1 is invalid for an IOB register - Handle as CLB register
        _safePrimitiveMap.put("FDCP_1", "FDCPE_1");
        final String[] fdcp_1_floating_ports = { "CE" };
        final int[] fdcp_1_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDCP_1", fdcp_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDCP_1", fdcp_1_floating_ports_defaults);

        // FDE
        _safePrimitiveMap.put("FDE", "FDCE");
        final String[] fde_floating_ports = { "CLR" };
        final int[] fde_floating_ports_defaults = { 0 };
        _floatingPortMap.put("FDE", fde_floating_ports);
        _floatingPortDefaultValueMap.put("FDE", fde_floating_ports_defaults);

        // FDE_1
        _safePrimitiveMap.put("FDE_1", "FDCE_1");
        final String[] fde_1_floating_ports = { "CLR" };
        final int[] fde_1_floating_ports_defaults = { 0 };
        _floatingPortMap.put("FDE_1", fde_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDE_1", fde_1_floating_ports_defaults);

        // FDP
        _safePrimitiveMap.put("FDP", "FDPE");
        final String[] fdp_floating_ports = { "CE" };
        final int[] fdp_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDP", fdp_floating_ports);
        _floatingPortDefaultValueMap.put("FDP", fdp_floating_ports_defaults);

        // FDP_1
        _safePrimitiveMap.put("FDP_1", "FDPE_1");
        final String[] fdp_1_floating_ports = { "CE" };
        final int[] fdp_1_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDP_1", fdp_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDP_1", fdp_1_floating_ports_defaults);

        // FDPE is Safe

        // FDPE_1 is Safe

        // FDR
        _safePrimitiveMap.put("FDR", "FDRE");
        final String[] fdr_floating_ports = { "CE" };
        final int[] fdr_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDR", fdr_floating_ports);
        _floatingPortDefaultValueMap.put("FDR", fdr_floating_ports_defaults);

        // FDR_1
        _safePrimitiveMap.put("FDR_1", "FDRE_1");
        final String[] fdr_1_floating_ports = { "CE" };
        final int[] fdr_1_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDR_1", fdr_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDR_1", fdr_1_floating_ports_defaults);

        // FDRE is Safe

        // FDRE_1 is Safe

        // FDRS is invalid for an IOB register - Handle as CLB register
        _safePrimitiveMap.put("FDRS", "FDRSE");
        final String[] fdrs_floating_ports = { "CE" };
        final int[] fdrs_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDRS", fdrs_floating_ports);
        _floatingPortDefaultValueMap.put("FDRS", fdrs_floating_ports_defaults);

        // FDRS_1 is invalid for an IOB register - Handle as CLB register
        _safePrimitiveMap.put("FDRS_1", "FDRSE_1");
        final String[] fdrs_1_floating_ports = { "CE" };
        final int[] fdrs_1_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDRS_1", fdrs_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDRS_1", fdrs_1_floating_ports_defaults);

        // FDS
        _safePrimitiveMap.put("FDS", "FDSE");
        final String[] fds_floating_ports = { "CE" };
        final int[] fds_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDS", fds_floating_ports);
        _floatingPortDefaultValueMap.put("FDS", fds_floating_ports_defaults);

        // FDS_1
        _safePrimitiveMap.put("FDS_1", "FDSE_1");
        final String[] fds_1_floating_ports = { "CE" };
        final int[] fds_1_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDS_1", fds_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDS_1", fds_1_floating_ports_defaults);

        // FDSE is Safe

        // FDSE_1 is Safe
    }

    // This class is a Singleton. Only one object of this type should ever exist.
    private static XilinxIOBProblemPrimitiveMap _singletonInstance = null;

    /*
     * Code to test this class
     */
    public static int test() {

        //		ProblemPrimitiveMap xilinxProblemPrimitiveMap = new XilinxIOBProblemPrimitiveMap();
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
        XilinxIOBProblemPrimitiveMap.test();
    }
}
