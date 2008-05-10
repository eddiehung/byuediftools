/*
 * Defines resource capacity and utilization of Xilinx Virtex4 part.
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
import edu.byu.ece.edif.tools.flatten.NewFlattenedEdifCell;
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
//// XilinxVirtex4DeviceUtilizationTracker
/**
 * Defines the resource capacity and utilization of a part in the Xilinx Virtex
 * FPGA family.
 * 
 * @author Keith Morgan, Jonathan Johnson
 * @version $Id: XilinxVirtex4DeviceUtilizationTracker.java 151 2008-04-02
 * 16:27:55Z jamesfcarroll $
 * @link: http://www.xilinx.com/products/silicon_solutions/fpgas/virtex/virtex4/product_table.htm
 * @link: http://direct.xilinx.com/bvdocs/publications/ds112.pdf
 */

public class XilinxVirtex4DeviceUtilizationTracker extends XilinxDeviceUtilizationTracker {

    public XilinxVirtex4DeviceUtilizationTracker(EdifCell cell, String part)
            throws OverutilizationEstimatedStopException, OverutilizationHardStopException, IllegalArgumentException {
        this(cell, part, DEFAULT_MERGE_FACTOR, DEFAULT_OPTIMIZATION_FACTOR, DEFAULT_DESIRED_UTILIZATION_FACTOR);
    }

    public XilinxVirtex4DeviceUtilizationTracker(EdifCell cell, String part, double mergeFactor,
            double optimizationFactor, double desiredUtilizationFactor) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, IllegalArgumentException {
        super(mergeFactor, optimizationFactor, desiredUtilizationFactor);

        String archName = "xc4v";
        String[] devNames = { "lx15", "lx25", "lx40", "lx60", "lx80", "lx100", "lx160", "lx200", "sx25", "sx35",
                "sx55", "fx12", "fx20", "fx40", "fx60", "fx100", "fx140" };

        String[] xc4vlx15Package = { "SF363", "FF668" };
        String[] xc4vlx25Package = { "SF363", "FF668" };
        String[] xc4vlx40Package = { "FF668", "FF1148" };
        String[] xc4vlx60Package = { "FF668", "FF1148" };
        String[] xc4vlx80Package = { "FF1148" };
        String[] xc4vlx100Package = { "FF1148", "FF1513" };
        String[] xc4vlx160Package = { "FF1148", "FF1513" };
        String[] xc4vlx200Package = { "FF1513" };

        String[] xc4vsx25Package = { "FF668" };
        String[] xc4vsx35Package = { "FF668" };
        String[] xc4vsx55Package = { "FF1148" };

        String[] xc4vfx12Package = { "SF363", "FF668" };
        String[] xc4vfx20Package = { "FF672" };
        String[] xc4vfx40Package = { "FF672", "FF1152" };
        String[] xc4vfx60Package = { "FF672", "FF1152" };
        String[] xc4vfx100Package = { "FF1152", "FF1517" };
        String[] xc4vfx140Package = { "FF1517", "FF1760" };

        String[][] packageNames = { xc4vlx15Package, xc4vlx25Package, xc4vlx40Package, xc4vlx60Package,
                xc4vlx80Package, xc4vlx100Package, xc4vlx160Package, xc4vlx200Package, xc4vsx25Package,
                xc4vsx35Package, xc4vsx55Package, xc4vfx12Package, xc4vfx20Package, xc4vfx40Package, xc4vfx60Package,
                xc4vfx100Package, xc4vfx140Package };

        XilinxPartValidator validator = new XilinxPartValidator(archName, devNames, packageNames);
        part = validator.validate(part);

        if (part.compareToIgnoreCase("XC4VLX15SF363") == 0)
            _init(cell, 12288, 12288, 48, 4, 4, 240, 32);
        else if (part.compareToIgnoreCase("XC4VLX15FF668") == 0)
            _init(cell, 12288, 12288, 48, 4, 4, 320, 32);

        else if (part.compareToIgnoreCase("XC4VLX25SF363") == 0)
            _init(cell, 21504, 21504, 72, 8, 8, 240, 48);
        else if (part.compareToIgnoreCase("XC4VLX25FF668") == 0)
            _init(cell, 21504, 21504, 72, 8, 8, 448, 48);

        else if (part.compareToIgnoreCase("XC4VLX40FF668") == 0)
            _init(cell, 36864, 36864, 96, 8, 8, 448, 64);
        else if (part.compareToIgnoreCase("XC4VLX40FF1148") == 0)
            _init(cell, 36864, 36864, 96, 8, 8, 640, 64);

        else if (part.compareToIgnoreCase("XC4VLX60FF668") == 0)
            _init(cell, 53248, 53248, 160, 8, 8, 448, 64);
        else if (part.compareToIgnoreCase("XC4VLX60FF1148") == 0)
            _init(cell, 53248, 53248, 160, 8, 8, 640, 64);

        else if (part.compareToIgnoreCase("XC4VLX80FF1148") == 0)
            _init(cell, 71680, 71680, 200, 12, 12, 768, 80);

        else if (part.compareToIgnoreCase("XC4VLX100FF1148") == 0)
            _init(cell, 98304, 98304, 240, 12, 12, 768, 96);
        else if (part.compareToIgnoreCase("XC4VLX100FF1513") == 0)
            _init(cell, 98304, 98304, 240, 12, 12, 960, 96);

        else if (part.compareToIgnoreCase("XC4VLX160FF1148") == 0)
            _init(cell, 135168, 135168, 288, 12, 12, 768, 96);
        else if (part.compareToIgnoreCase("XC4VLX160FF1513") == 0)
            _init(cell, 135168, 135168, 288, 12, 12, 960, 96);

        else if (part.compareToIgnoreCase("XC4VLX200FF1513") == 0)
            _init(cell, 178176, 178176, 336, 12, 12, 960, 96);
        ////
        else if (part.compareToIgnoreCase("XC4VSX25FF668") == 0)
            _init(cell, 20480, 20480, 128, 4, 4, 320, 128);

        else if (part.compareToIgnoreCase("XC4VSX35FF668") == 0)
            _init(cell, 30720, 30720, 192, 8, 8, 448, 192);

        else if (part.compareToIgnoreCase("XC4VSX55FF1148") == 0)
            _init(cell, 49152, 49152, 320, 8, 8, 640, 512);
        ////
        else if (part.compareToIgnoreCase("XC4VFX12SF363") == 0)
            _init(cell, 10944, 10944, 36, 4, 4, 240, 32, 1, 2, 0);
        else if (part.compareToIgnoreCase("XC4VFX12FF668") == 0)
            _init(cell, 10944, 10944, 36, 4, 4, 320, 32, 1, 2, 0);

        else if (part.compareToIgnoreCase("XC4VFX20FF672") == 0)
            _init(cell, 17088, 17088, 68, 4, 4, 320, 32, 1, 2, 8);

        else if (part.compareToIgnoreCase("XC4VFX40FF672") == 0)
            _init(cell, 37248, 37248, 144, 8, 8, 352, 48, 2, 4, 12);
        else if (part.compareToIgnoreCase("XC4VFX40FF1152") == 0)
            _init(cell, 37248, 37248, 144, 8, 8, 448, 48, 2, 4, 12);

        else if (part.compareToIgnoreCase("XC4VFX60FF672") == 0)
            _init(cell, 50560, 50560, 232, 12, 12, 352, 128, 2, 4, 16);
        else if (part.compareToIgnoreCase("XC4VFX60FF1152") == 0)
            _init(cell, 50560, 50560, 232, 12, 12, 576, 128, 2, 4, 16);

        else if (part.compareToIgnoreCase("XC4VFX100FF1152") == 0)
            _init(cell, 84352, 84352, 376, 12, 12, 576, 160, 2, 4, 20);
        else if (part.compareToIgnoreCase("XC4VFX100FF1517") == 0)
            _init(cell, 84352, 84352, 376, 12, 12, 768, 160, 2, 4, 20);

        else if (part.compareToIgnoreCase("XC4VFX140FF1517") == 0)
            _init(cell, 126336, 126336, 552, 20, 20, 768, 192, 2, 4, 24);

        else
            throw new IllegalArgumentException("Part name " + part
                    + " does not match the specified Xilinx Virtex 4 technology group.\n");
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    public static void main(String args[]) throws DuplicateNMRRequestException {
        // Constants...
        final String vendor = "xilinx";
        final String family = "virtex4";
        final String part = "XC4VFX140FFG1517";
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
            topCell = new NewFlattenedEdifCell(topCell);
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
                /*
                 * The following could be a "continue" or "break" depending on
                 * how you want to proceed... presumably you want to believe the
                 * resource tracker meaning that it correctly is predicting that
                 * you are out of resources
                 */
                break;
            } catch (OverutilizationHardStopException e2) {
                System.out.println("WARNING: Group of instances not added to resource tracker.");
                System.out.println(e2);
                /*
                 * This call adds everything else in the group except those
                 * instances which cause hard stops
                 */
                try {
                    duTracker.nmrInstances(group, false, true, _replicationFactor);
                } catch (OverutilizationEstimatedStopException e3) {
                    System.out
                            .println("WARNING: Group of instances not added to resource tracker due to estimated resource constraints. "
                                    + e3);
                    /*
                     * The following could be a "continue" or "break" depending
                     * on how you want to proceed... presumably you want to
                     * believe the resource tracker meaning that it correctly is
                     * predicting that you are out of resources
                     */
                    break;
                } catch (OverutilizationHardStopException e4) {
                    /*
                     * !!! Shouldn't get here because we called tmrInstances
                     * with the flag set to skip hard stops
                     */
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
     * @throws OverutilizationEstimatedStopException
     * @throws OverutilizationHardStopException
     * @throws IllegalArgumentException
     */

    protected void _init(EdifCell cell, int maxLUTs, int maxFFs, int maxBlockRAMs, int maxMult, int maxDCM, int maxIO,
            int maxDSPs, int maxPPC, int maxEthernet, int maxGBT) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, IllegalArgumentException {
        // !!! Ordering matters, the super call to _init should be last !!!
        addResourceForTracking(XilinxResourceMapper.LUT, 0.0, maxLUTs);
        addResourceForTracking(XilinxResourceMapper.FF, 0.0, maxFFs);
        addResourceForTracking(XilinxResourceMapper.BRAM, 0.0, maxBlockRAMs);
        addResourceForTracking(XilinxResourceMapper.MULT, 0.0, maxMult);
        addResourceForTracking(XilinxResourceMapper.DCM, 0.0, maxDCM);
        addResourceForTracking(XilinxResourceMapper.IO, 0.0, maxIO);
        addResourceForTracking(XilinxResourceMapper.RES, 0.0, maxIO); // One per IOB
        addResourceForTracking(XilinxResourceMapper.BUFG, 0.0, _v4maxClk);
        addResourceForTracking(XilinxResourceMapper.DSP, 0.0, maxDSPs);
        addResourceForTracking(XilinxResourceMapper.ICAP, 0.0, MAX_ICAP);
        addResourceForTracking(XilinxResourceMapper.FRAME_ECC, 0.0, MAX_FRAME_ECC);
        addResourceForTracking(XilinxResourceMapper.PPC, 0.0, maxPPC);
        addResourceForTracking(XilinxResourceMapper.ETHERNET, 0.0, maxEthernet);
        addResourceForTracking(XilinxResourceMapper.TRANSEIVER, 0.0, maxGBT);

        super._init(cell);
    }

    protected void _init(EdifCell cell, int maxLUTs, int maxFFs, int maxBlockRAMs, int maxMult, int maxDCM, int maxIO,
            int maxDSPs) throws OverutilizationEstimatedStopException, OverutilizationHardStopException,
            IllegalArgumentException {
        _init(cell, maxLUTs, maxFFs, maxBlockRAMs, maxMult, maxDCM, maxIO, maxDSPs, 0, 0, 0);
    }

    /**
     * Use to indicate triplication
     */
    private static final int _replicationFactor = 3;

    // Number of global clock lines available in any V4 device
    public static final int _v4maxClk = 32;

    public static final int MAX_ICAP = 1;

    public static final int MAX_FRAME_ECC = 1;

    public static final int MAX_PPC = 2;
}
