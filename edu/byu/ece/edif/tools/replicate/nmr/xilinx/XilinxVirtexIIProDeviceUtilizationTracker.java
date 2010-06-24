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
import edu.byu.ece.edif.arch.xilinx.parts.XilinxPartLookup;
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
import edu.byu.ece.edif.util.graph.EdifCellBadCutGroupings;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;

//////////////////////////////////////////////////////////////////////////
//// XilinxVirtexIIDeviceUtilizationTracker
/**
 * Defines the resource capacity and utilization of a part in the Xilinx Virtex2
 * FPGA family.
 * 
 * @author Keith Morgan, Jonathan Johnson
 * @version $Id: XilinxVirtexIIProDeviceUtilizationTracker.java 151 2008-04-02
 * 16:27:55Z jamesfcarroll $
 * @link http://www.xilinx.com/products/virtex/v2packages.htm
 * @link http://www.xilinx.com/products/silicon_solutions/fpgas/virtex/virtex_ii_platform_fpgas/product_table.htm
 */

public class XilinxVirtexIIProDeviceUtilizationTracker extends XilinxDeviceUtilizationTracker {

    public XilinxVirtexIIProDeviceUtilizationTracker(EdifCell cell, String part)
            throws OverutilizationEstimatedStopException, OverutilizationHardStopException, IllegalArgumentException {
        this(cell, part, DEFAULT_MERGE_FACTOR, DEFAULT_OPTIMIZATION_FACTOR, DEFAULT_DESIRED_UTILIZATION_FACTOR);
    }

    public XilinxVirtexIIProDeviceUtilizationTracker(EdifCell cell, String part, double mergeFactor,
            double optimizationFactor, double desiredUtilizationFactor) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, IllegalArgumentException {
        super(mergeFactor, optimizationFactor, desiredUtilizationFactor);

        String archName = "xc2vp";
        //"EasyPath cost reduction" parts only valid on p30 and up
        if (part.contains("xce2vp"))
            part = part.replace("e", "");

        String[] devNames = { "2", "4", "7", "20", "30", "40", "50", "70", "100" };

        String[] xc2vp2Package = { "FG256", "FG456", "FF672" };
        String[] xc2vp4Package = xc2vp2Package;
        String[] xc2vp7Package = { "FG456", "FF672", "FF896" };
        String[] xc2vp20Package = { "FG676", "FF672", "FF896" };
        String[] xc2vp30Package = xc2vp20Package;
        String[] xc2vp40Package = { "FG676", "FF1152", "FF1148" };
        String[] xc2vp50Package = { "FF1152", "FF1148", "FF1517" };
        String[] xc2vp70Package = { "FF1517", "FF1704" };
        String[] xc2vp100Package = { "FF1704", "FF1696" };
        String[][] packageNames = { xc2vp2Package, xc2vp4Package, xc2vp7Package, xc2vp20Package, xc2vp30Package,
                xc2vp40Package, xc2vp50Package, xc2vp70Package, xc2vp100Package };

        //XilinxPartValidator validator = new XilinxPartValidator(archName, devNames, packageNames);
        //part = validator.validate(part);
        part = XilinxPartLookup.getPartFromPartName(part).getPartNameNoSpeedGrade();
        
        // LUTs, BlockRAMs, Mult, DCM, PPC, Transeiver, IO
        if (part.compareToIgnoreCase("XC2VP2FG256") == 0)
            _init(cell, 3168, 12, 12, 4, 0, 4, 140);
        else if (part.compareToIgnoreCase("XC2VP2FG456") == 0)
            _init(cell, 3168, 12, 12, 4, 0, 4, 156);
        else if (part.compareToIgnoreCase("XC2VP2FF672") == 0)
            _init(cell, 3168, 12, 12, 4, 0, 4, 204);

        else if (part.compareToIgnoreCase("XC2VP4FG256") == 0)
            _init(cell, 6768, 28, 28, 4, 1, 4, 140);
        else if (part.compareToIgnoreCase("XC2VP4FG456") == 0)
            _init(cell, 6768, 28, 28, 4, 1, 4, 248);
        else if (part.compareToIgnoreCase("XC2VP4FF672") == 0)
            _init(cell, 6768, 28, 28, 4, 1, 4, 348);

        else if (part.compareToIgnoreCase("XC2VP7FG456") == 0)
            _init(cell, 11088, 44, 44, 4, 1, 8, 248);
        else if (part.compareToIgnoreCase("XC2VP7FF672") == 0)
            _init(cell, 11088, 44, 44, 4, 1, 8, 396);
        else if (part.compareToIgnoreCase("XC2VP7FF896") == 0)
            _init(cell, 11088, 44, 44, 4, 1, 8, 396);

        else if (part.compareToIgnoreCase("XC2VP20FG676") == 0)
            _init(cell, 20880, 88, 88, 8, 2, 8, 404);
        else if (part.compareToIgnoreCase("XC2VP20FF672") == 0)
            _init(cell, 20880, 88, 88, 8, 2, 8, 556);
        else if (part.compareToIgnoreCase("XC2VP20FF896") == 0)
            _init(cell, 20880, 88, 88, 8, 2, 8, 564);

        else if (part.compareToIgnoreCase("XC2VP30FG676") == 0)
            _init(cell, 30816, 136, 136, 8, 2, 8, 416);
        else if (part.compareToIgnoreCase("XC2VP30FF672") == 0)
            _init(cell, 30816, 136, 136, 8, 2, 8, 556);
        else if (part.compareToIgnoreCase("XC2VP30FF896") == 0)
            _init(cell, 30816, 136, 136, 8, 2, 8, 644);

        else if (part.compareToIgnoreCase("XC2VP40FG676") == 0)
            _init(cell, 46632, 162, 162, 8, 2, 8, 416);
        else if (part.compareToIgnoreCase("XC2VP40FF1152") == 0)
            _init(cell, 46632, 162, 162, 8, 2, 12, 692);
        else if (part.compareToIgnoreCase("XC2VP40FF1148") == 0)
            _init(cell, 46632, 162, 162, 8, 2, 0, 804);

        else if (part.compareToIgnoreCase("XC2VP50FF1152") == 0)
            _init(cell, 53136, 232, 232, 8, 2, 16, 692);
        else if (part.compareToIgnoreCase("XC2VP50FF1148") == 0)
            _init(cell, 53136, 232, 232, 8, 2, 0, 812);
        else if (part.compareToIgnoreCase("XC2VP50FF1517") == 0)
            _init(cell, 53136, 232, 232, 8, 2, 16, 852);

        else if (part.compareToIgnoreCase("XC2VP70FF1517") == 0)
            _init(cell, 74448, 328, 328, 8, 2, 16, 964);
        else if (part.compareToIgnoreCase("XC2VP70FF1704") == 0)
            _init(cell, 74448, 328, 328, 8, 2, 20, 996);

        else if (part.compareToIgnoreCase("XC2VP100FF1704") == 0)
            _init(cell, 99216, 444, 444, 12, 2, 20, 1040);
        else if (part.compareToIgnoreCase("XC2VP100FF1696") == 0)
            _init(cell, 99216, 444, 444, 12, 2, 0, 1164);

        else
            throw new IllegalArgumentException("Part name " + part
                    + " does not match the specified Xilinx Virtex 2 pro technology group.\n");
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
        NMRArchitecture tmrArch = new XilinxNMRArchitecture();

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
                duTracker.nmrInstancesAsManyAsPossible(group, _replicationFactor, null);
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
                    duTracker.nmrInstancesAsManyAsPossible(group, _replicationFactor, null);
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
    protected void _init(EdifCell cell, int maxCells, int maxBlockRAMs, int maxMult, int maxDCM, int maxPPC,
            int maxTranseiver, int maxIO) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, IllegalArgumentException {
        // !!! Ordering matters, the super call to _init should be last !!!
        addResourceForTracking(XilinxResourceMapper.LUT, 0.0, maxCells);
        addResourceForTracking(XilinxResourceMapper.FF, 0.0, maxCells);
        addResourceForTracking(XilinxResourceMapper.BRAM, 0.0, maxBlockRAMs);
        addResourceForTracking(XilinxResourceMapper.MULT, 0.0, maxMult);
        addResourceForTracking(XilinxResourceMapper.DCM, 0.0, maxDCM);
        addResourceForTracking(XilinxResourceMapper.IO, 0.0, maxIO);
        addResourceForTracking(XilinxResourceMapper.RES, 0.0, maxIO); // One per IOB
        addResourceForTracking(XilinxResourceMapper.BUFG, 0.0, _v2maxClk);
        addResourceForTracking(XilinxResourceMapper.IBUFG, 0.0, _v2maxClk);

        super._init(cell);
    }

    /**
     * Use to indicate triplication
     */
    private static final int _replicationFactor = 3;

    // Number of global clock lines available in any Virtex 2 device
    public static final int _v2maxClk = 16;
}
