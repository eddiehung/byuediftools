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
import java.util.LinkedHashMap;

/**
 * @since Created on Oct 31, 2005
 */
public class XilinxCLBProblemPrimitiveMap extends AbstractProblemPrimitiveMap implements Serializable {

    private XilinxCLBProblemPrimitiveMap() {
        // Generate the internal maps
        // Map 1 maps primitive names to the corresponding 'safe primitive' name
        // Map 2 maps primitive names to the will-be floating port names once it is replaced by its corresponding 'safe primitive'
        // Map 3 maps primitive names to the will-be floating port default values (once it is replaced by its corresponding 'safe primitive')
        generateMaps();
    }

    public static XilinxCLBProblemPrimitiveMap getXilinxCLBProblemPrimitiveMap() {
        if (_singletonInstance == null)
            _singletonInstance = new XilinxCLBProblemPrimitiveMap();

        return _singletonInstance;
    }

    private void generateMaps() {
        _safePrimitiveMap = new LinkedHashMap();
        _floatingPortMap = new LinkedHashMap();
        _floatingPortDefaultValueMap = new LinkedHashMap();

        // For each primitive entry,
        // there should be a corresponding call
        // to add values to *both* the _safePrimitiveMap
        // and _floatingPortMap maps.

        // *** This class requires that the primitive strings
        //     be entered with UPPERCASE letters ***

        // OBUF
        _safePrimitiveMap.put("OBUF", "OBUFT");
        final String[] obuf_floating_ports = { "T" };
        final int[] obuf_floating_ports_defaults = { 0 };
        _floatingPortMap.put("OBUF", obuf_floating_ports);
        _floatingPortDefaultValueMap.put("OBUF", obuf_floating_ports_defaults);

        // FD
        _safePrimitiveMap.put("FD", "FDCPE");
        final String[] fd_floating_ports = { "CE", "PRE", "CLR" };
        final int[] fd_floating_ports_defaults = { 1, 0, 0 };
        _floatingPortMap.put("FD", fd_floating_ports);
        _floatingPortDefaultValueMap.put("FD", fd_floating_ports_defaults);

        // FD_1
        _safePrimitiveMap.put("FD_1", "FDCPE_1");
        final String[] fd_1_floating_ports = { "CE", "PRE", "CLR" };
        final int[] fd_1_floating_ports_defaults = { 1, 0, 0 };
        _floatingPortMap.put("FD_1", fd_1_floating_ports);
        _floatingPortDefaultValueMap.put("FD_1", fd_1_floating_ports_defaults);

        // FDC
        _safePrimitiveMap.put("FDC", "FDCPE");
        final String[] fdc_floating_ports = { "PRE", "CE" };
        final int[] fdc_floating_ports_defaults = { 0, 1 };
        _floatingPortMap.put("FDC", fdc_floating_ports);
        _floatingPortDefaultValueMap.put("FDC", fdc_floating_ports_defaults);

        // FDC_1
        _safePrimitiveMap.put("FDC_1", "FDCPE_1");
        final String[] fdc_1_floating_ports = { "PRE", "CE" };
        final int[] fdc_1_floating_ports_defaults = { 0, 1 };
        _floatingPortMap.put("FDC_1", fdc_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDC_1", fdc_1_floating_ports_defaults);

        // FDCE
        _safePrimitiveMap.put("FDCE", "FDCPE");
        final String[] fdce_floating_ports = { "PRE" };
        final int[] fdce_floating_ports_defaults = { 0 };
        _floatingPortMap.put("FDCE", fdce_floating_ports);
        _floatingPortDefaultValueMap.put("FDCE", fdce_floating_ports_defaults);

        // FDCE_1
        _safePrimitiveMap.put("FDCE_1", "FDCPE_1");
        final String[] fdce_1_floating_ports = { "PRE" };
        final int[] fdce_1_floating_ports_defaults = { 0 };
        _floatingPortMap.put("FDCE_1", fdce_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDCE_1", fdce_1_floating_ports_defaults);

        // FDCP
        _safePrimitiveMap.put("FDCP", "FDCPE");
        final String[] fdcp_floating_ports = { "CE" };
        final int[] fdcp_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDCP", fdcp_floating_ports);
        _floatingPortDefaultValueMap.put("FDCP", fdcp_floating_ports_defaults);

        // FDCP_1
        _safePrimitiveMap.put("FDCP_1", "FDCPE_1");
        final String[] fdcp_1_floating_ports = { "CE" };
        final int[] fdcp_1_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDCP_1", fdcp_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDCP_1", fdcp_1_floating_ports_defaults);

        // FDE
        _safePrimitiveMap.put("FDE", "FDCPE");
        final String[] fde_floating_ports = { "PRE", "CLR" };
        final int[] fde_floating_ports_defaults = { 0, 0 };
        _floatingPortMap.put("FDE", fde_floating_ports);
        _floatingPortDefaultValueMap.put("FDE", fde_floating_ports_defaults);

        // FDE_1
        _safePrimitiveMap.put("FDE_1", "FDCPE_1");
        final String[] fde_1_floating_ports = { "PRE", "CLR" };
        final int[] fde_1_floating_ports_defaults = { 0, 0 };
        _floatingPortMap.put("FDE_1", fde_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDE_1", fde_1_floating_ports_defaults);

        // FDP
        _safePrimitiveMap.put("FDP", "FDCPE");
        final String[] fdp_floating_ports = { "CLR", "CE" };
        final int[] fdp_floating_ports_defaults = { 0, 1 };
        _floatingPortMap.put("FDP", fdp_floating_ports);
        _floatingPortDefaultValueMap.put("FDP", fdp_floating_ports_defaults);

        // FDP_1
        _safePrimitiveMap.put("FDP_1", "FDCPE_1");
        final String[] fdp_1_floating_ports = { "CLR", "CE" };
        final int[] fdp_1_floating_ports_defaults = { 0, 1 };
        _floatingPortMap.put("FDP_1", fdp_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDP_1", fdp_1_floating_ports_defaults);

        // FDPE
        _safePrimitiveMap.put("FDPE", "FDCPE");
        final String[] fdpe_floating_ports = { "CLR" };
        final int[] fdpe_floating_ports_defaults = { 0 };
        _floatingPortMap.put("FDPE", fdpe_floating_ports);
        _floatingPortDefaultValueMap.put("FDPE", fdpe_floating_ports_defaults);

        // FDPE_1
        _safePrimitiveMap.put("FDPE_1", "FDCPE_1");
        final String[] fdpe_1_floating_ports = { "CLR" };
        final int[] fdpe_1_floating_ports_defaults = { 0 };
        _floatingPortMap.put("FDPE_1", fdpe_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDPE_1", fdpe_1_floating_ports_defaults);

        // FDR
        _safePrimitiveMap.put("FDR", "FDRSE");
        final String[] fdr_floating_ports = { "S", "CE" };
        final int[] fdr_floating_ports_defaults = { 0, 1 };
        _floatingPortMap.put("FDR", fdr_floating_ports);
        _floatingPortDefaultValueMap.put("FDR", fdr_floating_ports_defaults);

        // FDR_1
        _safePrimitiveMap.put("FDR_1", "FDRSE_1");
        final String[] fdr_1_floating_ports = { "S", "CE" };
        final int[] fdr_1_floating_ports_defaults = { 0, 1 };
        _floatingPortMap.put("FDR_1", fdr_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDR_1", fdr_1_floating_ports_defaults);

        // FDRE
        _safePrimitiveMap.put("FDRE", "FDRSE");
        final String[] fdre_floating_ports = { "S" };
        final int[] fdre_floating_ports_defaults = { 0 };
        _floatingPortMap.put("FDRE", fdre_floating_ports);
        _floatingPortDefaultValueMap.put("FDRE", fdre_floating_ports_defaults);

        // FDRE_1
        _safePrimitiveMap.put("FDRE_1", "FDRSE_1");
        final String[] fdre_1_floating_ports = { "S" };
        final int[] fdre_1_floating_ports_defaults = { 0 };
        _floatingPortMap.put("FDRE_1", fdre_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDRE_1", fdre_1_floating_ports_defaults);

        // FDRS
        _safePrimitiveMap.put("FDRS", "FDRSE");
        final String[] fdrs_floating_ports = { "CE" };
        final int[] fdrs_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDRS", fdrs_floating_ports);
        _floatingPortDefaultValueMap.put("FDRS", fdrs_floating_ports_defaults);

        // FDRS_1
        _safePrimitiveMap.put("FDRS_1", "FDRSE_1");
        final String[] fdrs_1_floating_ports = { "CE" };
        final int[] fdrs_1_floating_ports_defaults = { 1 };
        _floatingPortMap.put("FDRS_1", fdrs_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDRS_1", fdrs_1_floating_ports_defaults);

        // FDS
        _safePrimitiveMap.put("FDS", "FDRSE");
        final String[] fds_floating_ports = { "R", "CE" };
        final int[] fds_floating_ports_defaults = { 0, 1 };
        _floatingPortMap.put("FDS", fds_floating_ports);
        _floatingPortDefaultValueMap.put("FDS", fds_floating_ports_defaults);

        // FDS_1
        _safePrimitiveMap.put("FDS_1", "FDRSE_1");
        final String[] fds_1_floating_ports = { "R", "CE" };
        final int[] fds_1_floating_ports_defaults = { 0, 1 };
        _floatingPortMap.put("FDS_1", fds_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDS_1", fds_1_floating_ports_defaults);

        // FDSE
        _safePrimitiveMap.put("FDSE", "FDRSE");
        final String[] fdse_floating_ports = { "R" };
        final int[] fdse_floating_ports_defaults = { 0 };
        _floatingPortMap.put("FDSE", fdse_floating_ports);
        _floatingPortDefaultValueMap.put("FDSE", fdse_floating_ports_defaults);

        // FDSE_1
        _safePrimitiveMap.put("FDSE_1", "FDRSE_1");
        final String[] fdse_1_floating_ports = { "R" };
        final int[] fdse_1_floating_ports_defaults = { 0 };
        _floatingPortMap.put("FDSE_1", fdse_1_floating_ports);
        _floatingPortDefaultValueMap.put("FDSE_1", fdse_1_floating_ports_defaults);
    }

    // This class is a Singleton. Only one object of this type should ever exist.
    private static XilinxCLBProblemPrimitiveMap _singletonInstance = null;

    /*
     * Code to test this class
     */
    public static int test() {

        //		ProblemPrimitiveMap xilinxProblemPrimitiveMap = new XilinxCLBProblemPrimitiveMap();
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
        XilinxCLBProblemPrimitiveMap.test();
    }
}
