/*
 * Defines resource capacity and utilization of a Xilinx Virtex2 family FPGA.
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

import java.util.Collection;
import java.util.Iterator;

import edu.byu.ece.edif.arch.xilinx.XilinxMergeParser;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.DeviceParser;
import edu.byu.ece.edif.tools.replicate.nmr.DeviceUtilizationTracker;
import edu.byu.ece.edif.tools.replicate.nmr.DuplicateNMRRequestException;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationEstimatedStopException;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationException;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationHardStopException;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.XilinxTMRArchitecture;
import edu.byu.ece.edif.util.graph.EdifCellBadCutGroupings;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;

//////////////////////////////////////////////////////////////////////////
//// XilinxVirtexIIDeviceUtilizationTracker
/**
 * Defines the resource capacity and utilization of a part in the Xilinx Virtex2
 * FPGA family.
 * 
 * @author Keith Morgan, Jonathan Johnson
 * @version $Id: XilinxVirtexIIDeviceUtilizationTracker.java 151 2008-04-02
 * 16:27:55Z jamesfcarroll $
 * @link http://www.xilinx.com/products/virtex/v2packages.htm
 * @link http://www.xilinx.com/products/silicon_solutions/fpgas/virtex/virtex_ii_platform_fpgas/product_table.htm
 */

public class XilinxVirtexIIDeviceUtilizationTracker extends XilinxDeviceUtilizationTracker {

    public XilinxVirtexIIDeviceUtilizationTracker(EdifCell cell, String part)
            throws OverutilizationEstimatedStopException, OverutilizationHardStopException, IllegalArgumentException {
        this(cell, part, DEFAULT_MERGE_FACTOR, DEFAULT_OPTIMIZATION_FACTOR, DEFAULT_DESIRED_UTILIZATION_FACTOR);
    }

    public XilinxVirtexIIDeviceUtilizationTracker(EdifCell cell, String part, double mergeFactor,
            double optimizationFactor, double desiredUtilizationFactor) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, IllegalArgumentException {
        super(mergeFactor, optimizationFactor, desiredUtilizationFactor);

        String archName = "xc2v";

        String[] devNames = { "40", "80", "250", "500", "1000", "1500", "2000", "3000", "4000", "6000", "8000" };

        String[] xc2v40Package = { "CS144", "FG256" };
        String[] xc2v80Package = { "CS144", "FG256" };
        String[] xc2v250Package = { "CS144", "FG256", "FG456" };
        String[] xc2v500Package = { "FG256", "FG456" };
        String[] xc2v1000Package = { "FG256", "FG456", "FF896", "BG575" };
        String[] xc2v1500Package = { "FG676", "FF896", "BG575" };
        String[] xc2v2000Package = { "FG676", "FF896", "BG575", "BF957" };
        String[] xc2v3000Package = { "BG728", "FG676", "FF1152", "BF957" };
        String[] xc2v4000Package = { "FF1152", "FF1517", "BF957" };
        String[] xc2v6000Package = { "FF1152", "FF1517", "BF957" };
        String[] xc2v8000Package = { "FF1152", "FF1517" };
        String[][] packageNames = { xc2v40Package, xc2v80Package, xc2v250Package, xc2v500Package, xc2v1000Package,
                xc2v1500Package, xc2v2000Package, xc2v3000Package, xc2v4000Package, xc2v6000Package, xc2v8000Package };

        XilinxPartValidator validator = new XilinxPartValidator(archName, devNames, packageNames);
        part = validator.validate(part);

        if (part.compareToIgnoreCase("XC2V40CS144") == 0)
            _init(cell, 512, 512, 4, 4, 4, 88);
        else if (part.compareToIgnoreCase("XC2V40FG256") == 0)
            _init(cell, 512, 512, 4, 4, 4, 88);

        else if (part.compareToIgnoreCase("XC2V80CS144") == 0)
            _init(cell, 1024, 1024, 8, 8, 4, 92);
        else if (part.compareToIgnoreCase("XC2V80FG256") == 0)
            _init(cell, 1024, 1024, 8, 8, 4, 120);

        else if (part.compareToIgnoreCase("XC2V250CS144") == 0)
            _init(cell, 3072, 3072, 24, 24, 8, 92);
        else if (part.compareToIgnoreCase("XC2V250FG256") == 0)
            _init(cell, 3072, 3072, 24, 24, 8, 172);
        else if (part.compareToIgnoreCase("XC2V250FG456") == 0)
            _init(cell, 3072, 3072, 24, 24, 8, 264);

        else if (part.compareToIgnoreCase("XC2V500FG256") == 0)
            _init(cell, 6144, 6144, 32, 32, 8, 172);
        else if (part.compareToIgnoreCase("XC2V500FG456") == 0)
            _init(cell, 6144, 6133, 32, 32, 8, 264);

        else if (part.compareToIgnoreCase("XC2V1000BG575") == 0)
            _init(cell, 10240, 10240, 40, 40, 8, 328);
        else if (part.compareToIgnoreCase("XC2V1000FG256") == 0)
            _init(cell, 10240, 10240, 40, 40, 8, 172);
        else if (part.compareToIgnoreCase("XC2V1000FG456") == 0)
            _init(cell, 10240, 10240, 40, 40, 8, 324);
        else if (part.compareToIgnoreCase("XC2V1000FF896") == 0)
            _init(cell, 10240, 10240, 40, 40, 8, 432);

        else if (part.compareToIgnoreCase("XC2V1500BG575") == 0)
            _init(cell, 15360, 15360, 48, 48, 8, 392);
        else if (part.compareToIgnoreCase("XC2V1500FG676") == 0)
            _init(cell, 15360, 15360, 48, 48, 8, 392);
        else if (part.compareToIgnoreCase("XC2V1500FF896") == 0)
            _init(cell, 15360, 15360, 48, 48, 8, 528);

        else if (part.compareToIgnoreCase("XC2V2000BG575") == 0)
            _init(cell, 21504, 21504, 56, 56, 8, 408);
        else if (part.compareToIgnoreCase("XC2V2000FG676") == 0)
            _init(cell, 21504, 21504, 56, 56, 8, 456);
        else if (part.compareToIgnoreCase("XC2V2000FF896") == 0)
            _init(cell, 21504, 21504, 56, 56, 8, 624);
        else if (part.compareToIgnoreCase("XC2V2000BF957") == 0)
            _init(cell, 21504, 21504, 56, 56, 8, 624);

        else if (part.compareToIgnoreCase("XC2V3000BG728") == 0)
            _init(cell, 28672, 28672, 96, 96, 12, 516);
        else if (part.compareToIgnoreCase("XC2V3000FG676") == 0)
            _init(cell, 28672, 28672, 96, 96, 12, 484);
        else if (part.compareToIgnoreCase("XC2V3000FF1152") == 0)
            _init(cell, 28672, 28672, 96, 96, 12, 720);
        else if (part.compareToIgnoreCase("XC2V3000BF957") == 0)
            _init(cell, 28672, 28672, 96, 96, 12, 684);

        else if (part.compareToIgnoreCase("XC2V4000FF1152") == 0)
            _init(cell, 46080, 46080, 120, 120, 12, 824);
        else if (part.compareToIgnoreCase("XC2V4000FF1517") == 0)
            _init(cell, 46080, 46080, 120, 120, 12, 912);
        else if (part.compareToIgnoreCase("XC2V4000BF957") == 0)
            _init(cell, 46080, 46080, 120, 120, 12, 684);

        else if (part.compareToIgnoreCase("XC2V6000FF1152") == 0)
            _init(cell, 67584, 67584, 144, 144, 12, 824);
        else if (part.compareToIgnoreCase("XC2V6000FF1517") == 0)
            _init(cell, 67584, 67584, 144, 144, 12, 1104);
        else if (part.compareToIgnoreCase("XC2V6000BF957") == 0)
            _init(cell, 67584, 67584, 144, 144, 12, 684);

        else if (part.compareToIgnoreCase("XC2V8000FF1152") == 0)
            _init(cell, 93184, 93184, 168, 168, 12, 824);
        else if (part.compareToIgnoreCase("XC2V8000FF1517") == 0)
            _init(cell, 93184, 93184, 168, 168, 12, 1108);

        else
            throw new IllegalArgumentException("Part name " + part
                    + " does not match the specified Xilinx Virtex 2 technology group.\n");
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    public static void main(String args[]) throws DuplicateNMRRequestException {

        // Constants...
        final String vendor = "xilinx";
        final String family = "virtex2";
        final String part = "XC2V8000FF1517";
        // *** Comment out the above name and
        //     use the following part if you want the SSRA to run
        //     out of resources...

        // Load EDIF
        // note: expecting arg0 to be the name of an edif file
        // note: the -L arg is needed if there are child edif files
        EdifCell topCell = XilinxMergeParser.parseAndMergeXilinx(args);

        // Flatten the cell...
        System.out.println("Flattening the cell...");
        try {
            topCell = new FlattenedEdifCell(topCell);
        } catch (EdifNameConflictException e5) {
            e5.toRuntime();
        } catch (InvalidEdifNameException e5) {
            e5.toRuntime();
        }

        // Create a TMR architecture object for the part
        // specified in the "constants" section of this function 
        System.out.println("Creating new TMR architecture object for part " + part + " of family " + family
                + " from vendor " + vendor);
        NMRArchitecture tmrArch = new XilinxTMRArchitecture();

        // Create an instance connectivity object
        System.out.println("Creating a connectivity object...");
        EdifCellInstanceGraph ecic = new EdifCellInstanceGraph(topCell);

        // Create groups based on bad cuts
        System.out.println("Grouping the cell based on bad cuts...");
        EdifCellBadCutGroupings ecbcg = new EdifCellBadCutGroupings(topCell, tmrArch, ecic);

        // Create a group connectivity object
        // this isn't needed for this example, but this is how you
        // would create one...
        //EdifCellInstanceCollectionGraph ecigc = new EdifCellInstanceCollectionGraph(ecic, ecbcg);

        //        // Create a resource limits object for the given device
        //        System.out.println ("Creating a resource limit information object for part "+part+" of family " + family + " from vendor "+vendor);
        //        //DeviceUtilization normalResourceCount = DeviceUtilization.createDeviceUtilizationObject(vendor, family, part);
        //        DeviceUtilization normalResourceCount = new XilinxVirtexDeviceUtilization(part);
        //        System.out.println ("Static utilization (should be null)...");
        //        System.out.println (normalResourceCount);

        // Get the resource utilization of the device for the cell
        // note: this is without TMR
        // note: the utilization is calculated at object creation time
        System.out.println("Calculating normal resource utilization of cell " + topCell);
        DeviceUtilizationTracker duTracker = null;
        try {
            duTracker = DeviceParser.createXilinxDeviceUtilizationTracker(topCell, family, part);
        } catch (OverutilizationException e) {
            throw new EdifRuntimeException("ERROR: Initial contents of cell " + topCell + " do not fit into part "
                    + part);
        }
        System.out.println("Normal utilization for cell " + topCell);
        System.out.println(duTracker);

        // Get a collection of all the bad cut groups in the cell...
        System.out.println("Getting a reference to all of the bad cut groups in the cell...");
        Collection groups = ecbcg.getInstanceGroups();

        // Iterate over all of the groups and try to add each one
        // to the resource tracker
        System.out.println("Iterating over each bad cut group and trying to add each to the TMR resource tracker...");
        for (Iterator i = groups.iterator(); i.hasNext();) {
            Collection group = (Collection) i.next();
            try {
                duTracker.nmrInstances(group, _replicationFactor);
            } catch (OverutilizationEstimatedStopException e1) {
                System.out
                        .println("WARNING: Group of instances not added to resource tracker due to estimated resource constraints. "
                                + e1);
                // The following could be a "continue" or "break" 
                // depending on how you want to proceed...
                // presumably you want to believe the resource tracker
                // meaning that it correctly is predicting
                // that you are out of resources
                break;
            } catch (OverutilizationHardStopException e2) {
                System.out.println("WARNING: Group of instances not added to resource tracker.");
                System.out.println(e2);
                // This call adds everything else in the group
                // except those instances which cause hard stops
                try {
                    duTracker.nmrInstances(group, false, true, _replicationFactor);
                } catch (OverutilizationEstimatedStopException e3) {
                    System.out
                            .println("WARNING: Group of instances not added to resource tracker due to estimated resource constraints. "
                                    + e3);
                    // The following could be a "continue" or "break" 
                    // depending on how you want to proceed...
                    // presumably you want to believe the resource tracker
                    // meaning that it correctly is predicting
                    // that you are out of resources
                    break;
                } catch (OverutilizationHardStopException e4) {
                    // !!! Shouldn't get here because we called tmrInstances
                    //     with the flag set to skip hard stops
                    throw new EdifRuntimeException("ERROR: Shouldn't get here! " + e4);
                }
            }
        }

        System.out.println("Utilization for cell " + topCell + " after TMR...");
        System.out.println(duTracker);

    }

    ///////////////////////////////////////////////////////////////////
    ////                         protected methods                 ////

    /**
     * @param cell
     * @param maxLUTs
     * @param maxFFs
     * @param maxBlockRAMs
     * @param maxMult
     * @param maxDCM
     * @param maxIO
     * @param maxBUFG
     * @throws OverutilizationEstimatedStopException
     * @throws OverutilizationHardStopException
     * @throws IllegalArgumentException
     */
    protected void _init(EdifCell cell, int maxLUTs, int maxFFs, int maxBlockRAMs, int maxMult, int maxDCM, int maxIO)
            throws OverutilizationEstimatedStopException, OverutilizationHardStopException, IllegalArgumentException {
        // !!! Ordering matters, the super call to _init should be last !!!
        addResourceForTracking(XilinxResourceMapper.LUT, 0.0, maxLUTs);
        addResourceForTracking(XilinxResourceMapper.FF, 0.0, maxFFs);
        addResourceForTracking(XilinxResourceMapper.BRAM, 0.0, maxBlockRAMs);
        addResourceForTracking(XilinxResourceMapper.MULT, 0.0, maxMult);
        addResourceForTracking(XilinxResourceMapper.DCM, 0.0, maxDCM);
        addResourceForTracking(XilinxResourceMapper.IO, 0.0, maxIO);
        addResourceForTracking(XilinxResourceMapper.RES, 0.0, maxIO); // One per IOB
        addResourceForTracking(XilinxResourceMapper.BUFG, 0.0, _v2maxClk);
        super._init(cell);
    }

    /**
     * Use to indicate triplication
     */
    private static final int _replicationFactor = 3;

    // Number of global clock lines available in any Virtex 2 device
    public static final int _v2maxClk = 16;
}
