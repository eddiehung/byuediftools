/*
 * Defines resource capacity and utilization of a Xilinx Virtex family FPGA.
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
import edu.byu.ece.edif.tools.replicate.nmr.DeviceUtilizationTracker;
import edu.byu.ece.edif.tools.replicate.nmr.DuplicateNMRRequestException;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationEstimatedStopException;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationException;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationHardStopException;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.XilinxTMRArchitecture;
import edu.byu.ece.edif.util.graph.EdifCellBadCutGroupings;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;

/////////////////////////////////////////////////////////////////////////
//// XilinxVirtexDeviceUtilizationTracker
/**
 * Defines the resource capacity and utilization of a part in the Xilinx Virtex
 * FPGA family.
 * 
 * @author Keith Morgan
 * @version $Id: XilinxVirtexDeviceUtilizationTracker.java 151 2008-04-02
 * 16:27:55Z jamesfcarroll $
 * @since Created on Dec 22, 2005
 * @link http://www.xilinx.com/products/silicon_solutions/fpgas/virtex/virtex_e_em/resources/virtex_e_em_pkgs.htm
 * @link http://direct.xilinx.com/bvdocs/publications/ds003-1.pdf
 */
public class XilinxVirtexDeviceUtilizationTracker extends XilinxDeviceUtilizationTracker {

    public XilinxVirtexDeviceUtilizationTracker(EdifCell cell, String part)
            throws OverutilizationEstimatedStopException, OverutilizationHardStopException, IllegalArgumentException {
        this(cell, part, DEFAULT_MERGE_FACTOR, DEFAULT_OPTIMIZATION_FACTOR, DEFAULT_DESIRED_UTILIZATION_FACTOR);
    }

    public XilinxVirtexDeviceUtilizationTracker(EdifCell cell, String part, double mergeFactor,
            double optimizationFactor, double desiredUtilizationFactor) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, IllegalArgumentException {
        super(mergeFactor, optimizationFactor, desiredUtilizationFactor);

        String archName = "xcv";

        String[] devNames = { "50", "100", "150", "200", "300", "400", "600", "800", "1000" };

        String[] xcv50Package = { "CS144", "TQ144", "PQ240", "BG256", "FG256" };
        String[] xcv100Package = { "CS144", "TQ144", "PQ240", "BG256", "FG256" };
        String[] xcv150Package = { "PQ240", "BG256", "BG352", "FG256", "FG456" };
        String[] xcv200Package = { "PQ240", "BG256", "BG352", "FG256", "FG456" };
        String[] xcv300Package = { "PQ240", "BG352", "BG432", "FG456" };
        String[] xcv400Package = { "HQ240", "BG432", "BG560", "FG676" };
        String[] xcv600Package = { "HQ240", "BG432", "BG560", "FG676", "FG680" };
        String[] xcv800Package = { "HQ240", "BG432", "BG560", "FG676", "FG680" };
        String[] xcv1000Package = { "BG560", "FG680" };
        String[][] packageNames = { xcv50Package, xcv100Package, xcv150Package, xcv200Package, xcv300Package,
                xcv400Package, xcv600Package, xcv800Package, xcv1000Package };

        XilinxPartValidator validator = new XilinxPartValidator(archName, devNames, packageNames);
        part = validator.validate(part);

        if (part.compareToIgnoreCase("XCV50CS144") == 0)
            _init(cell, 1536, 1536, 8, 94, 4);
        else if (part.compareToIgnoreCase("XCV50TQ144") == 0)
            _init(cell, 1536, 1536, 8, 98, 4);
        else if (part.compareToIgnoreCase("XCV50PQ240") == 0)
            _init(cell, 1536, 1536, 8, 166, 4);
        else if (part.compareToIgnoreCase("XCV50BG256") == 0)
            _init(cell, 1536, 1536, 8, 180, 4);
        else if (part.compareToIgnoreCase("XCV50FG256") == 0)
            _init(cell, 1536, 1536, 8, 176, 4);

        else if (part.compareToIgnoreCase("XCV100CS144") == 0)
            _init(cell, 2400, 2400, 10, 94, 4);
        else if (part.compareToIgnoreCase("XCV100TQ144") == 0)
            _init(cell, 2400, 2400, 10, 98, 4);
        else if (part.compareToIgnoreCase("XCV100PQ240") == 0)
            _init(cell, 2400, 2400, 10, 166, 4);
        else if (part.compareToIgnoreCase("XCV100BG256") == 0)
            _init(cell, 2400, 2400, 10, 180, 4);
        else if (part.compareToIgnoreCase("XCV100FG256") == 0)
            _init(cell, 2400, 2400, 10, 176, 4);

        else if (part.compareToIgnoreCase("XCV150PQ240") == 0)
            _init(cell, 3456, 3456, 12, 166, 4);
        else if (part.compareToIgnoreCase("XCV150BG256") == 0)
            _init(cell, 3456, 3456, 12, 180, 4);
        else if (part.compareToIgnoreCase("XCV150BG352") == 0)
            _init(cell, 3456, 3456, 12, 260, 4);
        else if (part.compareToIgnoreCase("XCV150FG256") == 0)
            _init(cell, 3456, 3456, 12, 176, 4);
        else if (part.compareToIgnoreCase("XCV150FG456") == 0)
            _init(cell, 3456, 3456, 12, 260, 4);

        else if (part.compareToIgnoreCase("XCV200PQ240") == 0)
            _init(cell, 4704, 4704, 14, 166, 4);
        else if (part.compareToIgnoreCase("XCV200BG256") == 0)
            _init(cell, 4704, 4704, 14, 180, 4);
        else if (part.compareToIgnoreCase("XCV200BG352") == 0)
            _init(cell, 4704, 4704, 14, 260, 4);
        else if (part.compareToIgnoreCase("XCV200FG256") == 0)
            _init(cell, 4704, 4704, 14, 176, 4);
        else if (part.compareToIgnoreCase("XCV200FG456") == 0)
            _init(cell, 4704, 4704, 14, 284, 4);

        else if (part.compareToIgnoreCase("XCV300PQ240") == 0)
            _init(cell, 6144, 6144, 16, 166, 4);
        else if (part.compareToIgnoreCase("XCV300BG352") == 0)
            _init(cell, 6144, 6144, 16, 260, 4);
        else if (part.compareToIgnoreCase("XCV300BG432") == 0)
            _init(cell, 6144, 6144, 16, 316, 4);
        else if (part.compareToIgnoreCase("XCV300FG456") == 0)
            _init(cell, 6144, 6144, 16, 312, 4);

        else if (part.compareToIgnoreCase("XCV400HQ240") == 0)
            _init(cell, 9600, 9600, 20, 166, 4);
        else if (part.compareToIgnoreCase("XCV400BG432") == 0)
            _init(cell, 9600, 9600, 20, 316, 4);
        else if (part.compareToIgnoreCase("XCV400BG560") == 0)
            _init(cell, 9600, 9600, 20, 404, 4);
        else if (part.compareToIgnoreCase("XCV400FG676") == 0)
            _init(cell, 9600, 9600, 20, 404, 4);

        else if (part.compareToIgnoreCase("XCV600HQ240") == 0)
            _init(cell, 13824, 13824, 24, 166, 4);
        else if (part.compareToIgnoreCase("XCV600BG432") == 0)
            _init(cell, 13824, 13824, 24, 316, 4);
        else if (part.compareToIgnoreCase("XCV600BG560") == 0)
            _init(cell, 13824, 13824, 24, 404, 4);
        else if (part.compareToIgnoreCase("XCV600FG676") == 0)
            _init(cell, 13824, 13824, 24, 444, 4);
        else if (part.compareToIgnoreCase("XCV600FG680") == 0)
            _init(cell, 13824, 13824, 24, 512, 4);

        else if (part.compareToIgnoreCase("XCV800HQ240") == 0)
            _init(cell, 18816, 18816, 28, 166, 4);
        else if (part.compareToIgnoreCase("XCV800BG432") == 0)
            _init(cell, 18816, 18816, 28, 316, 4);
        else if (part.compareToIgnoreCase("XCV800BG560") == 0)
            _init(cell, 18816, 18816, 28, 404, 4);
        else if (part.compareToIgnoreCase("XCV800FG676") == 0)
            _init(cell, 18816, 18816, 28, 444, 4);
        else if (part.compareToIgnoreCase("XCV800FG680") == 0)
            _init(cell, 18816, 18816, 28, 512, 4);

        else if (part.compareToIgnoreCase("XCV1000BG560") == 0)
            _init(cell, 24576, 24576, 32, 404, 4);
        else if (part.compareToIgnoreCase("XCV1000FG680") == 0)
            _init(cell, 24576, 24576, 32, 512, 4);
        else
            throw new IllegalArgumentException(
                    "Part name "
                            + part
                            + " does not match the specified Xilinx Virtex technology group.\n"
                            + "\tNote: You must specify the technology option on the command line to use technologies other than Virtex 1 (i.e. '--technology Virtex4').\n");
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    public static void main(String args[]) throws DuplicateNMRRequestException {

        // Constants...
        final String vendor = "xilinx";
        final String technology = "virtex";
        final String part = "XCV1000FG680";
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
        System.out.println("Creating new TMR architecture object for part " + part + " of technology " + technology
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
        //        System.out.println ("Creating a resource limit information object for part "+part+" of technology "+technology+" from vendor "+vendor);
        //        //DeviceUtilization normalResourceCount = DeviceUtilization.createDeviceUtilizationObject(vendor, technology, part);
        //        DeviceUtilization normalResourceCount = new XilinxVirtexDeviceUtilization(part);
        //        System.out.println ("Static utilization (should be null)...");
        //        System.out.println (normalResourceCount);

        // Get the resource utilization of the device for the cell
        // note: this is without TMR
        // note: the utilization is calculated at object creation time
        System.out.println("Calculating normal resource utilization of cell " + topCell);
        DeviceUtilizationTracker duTracker = null;
        try {
            duTracker = new XilinxVirtexDeviceUtilizationTracker(topCell, part);
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

    protected void _init(EdifCell cell, int maxLUTs, int maxFFs, int maxBlockRAMs, int maxIO, int maxDLLs)
            throws OverutilizationEstimatedStopException, OverutilizationHardStopException, IllegalArgumentException {
        // !!! Ordering matters, the super call to _init should be last !!!
        addResourceForTracking(XilinxResourceMapper.LUT, 0.0, maxLUTs);
        addResourceForTracking(XilinxResourceMapper.FF, 0.0, maxFFs);
        addResourceForTracking(XilinxResourceMapper.BRAM, 0.0, maxBlockRAMs);
        addResourceForTracking(XilinxResourceMapper.IO, 0.0, maxIO);
        addResourceForTracking(XilinxResourceMapper.RES, 0.0, maxIO); // One per IOB
        addResourceForTracking(XilinxResourceMapper.DLL, 0.0, maxDLLs);
        addResourceForTracking(XilinxResourceMapper.BUFG, 0.0, _v1maxClk);
        super._init(cell);
    }

    /**
     * Use to indicate triplication
     */
    private static final int _replicationFactor = 3;

    // Number of global clock lines available in any Virtex device
    public static final int _v1maxClk = 4;
}
