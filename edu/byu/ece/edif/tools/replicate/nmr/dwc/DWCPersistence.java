/*
 * Provides a NMR method for design flattening and persistent error lines.
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
package edu.byu.ece.edif.tools.replicate.nmr.dwc;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.arch.xilinx.XilinxMergeParser;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.EdifUtils;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.ijmr.IJMREdifCell;
import edu.byu.ece.edif.tools.replicate.ijmr.TMRDWCEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.DeviceParser;
import edu.byu.ece.edif.tools.replicate.nmr.DeviceUtilizationTracker;
import edu.byu.ece.edif.tools.replicate.nmr.DuplicateNMRRequestException;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.NMRGraphUtilities;
import edu.byu.ece.edif.tools.replicate.nmr.NMRUtilities;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationEstimatedStopException;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationException;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationHardStopException;
import edu.byu.ece.edif.tools.replicate.nmr.NMRUtilities.UtilizationFactor;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.FlattenTMR;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.PartialInputOutputFeedForwardTMR;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.XilinxTMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxPartValidator;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxResourceMapper;
import edu.byu.ece.edif.tools.sterilize.fmap.FmapRemover;
import edu.byu.ece.edif.tools.sterilize.halflatch.EdifHalfLatchRemover;
import edu.byu.ece.edif.tools.sterilize.halflatch.HalfLatchArchitecture;
import edu.byu.ece.edif.tools.sterilize.halflatch.HalfLatchFlattenedEdifCell;
import edu.byu.ece.edif.tools.sterilize.halflatch.SequentialEdifHalfLatchRemover;
import edu.byu.ece.edif.tools.sterilize.halflatch.XilinxHalfLatchArchitecture;
import edu.byu.ece.edif.util.clockdomain.ClockDomainParser;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.graph.EdifOutputPortRefGraph;
import edu.byu.ece.edif.util.iob.AbstractIOBAnalyzer;
import edu.byu.ece.edif.util.iob.XilinxVirtexIOBAnalyzer;
import edu.byu.ece.edif.util.jsap.DWCCommandParser;
import edu.byu.ece.edif.util.jsap.NMRCommandParser;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.dfs.BasicDepthFirstSearchTree;
import edu.byu.ece.graph.dfs.DepthFirstTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * Provides a NMR method that includes design flattening and support for
 * persistent error lines. This class will perform DWC as follows:
 * <ol>
 * <li> Parse and merge the top-level EDIF file
 * <li> Flatten the top-level EDIF file
 * <li> Create an EdifCellGraph from the flattened file
 * <li> Identify cuts
 * </ol>
 * 
 * @see IJMREdifCell
 * @see TMRDWCEdifCell
 * @see FlattenTMR
 * @version 0.0.1 - 30 July 2007
 */

public class DWCPersistence {

    public static final String VERSION = "0.0.1";

    public static final String RELEASE_DATE = "Not Yet Released";

    public static final String REVISION = "$Revision:198 $";

    public static void main(String[] args) throws IOException, OverutilizationException {

        /*
         * 1. Preliminary work (parse arguments, setup log file, print version.
         */
        // 1.a) Parse arguments
        _parser = new DWCCommandParser();
        _result = _parser.parse(args);
        boolean doPersistence = _result.getBoolean(DWCCommandParser.PERSISTENCE);
        boolean highestFFFanoutCutset = _result.getBoolean(DWCCommandParser.HIGHEST_FF_FANOUT_CUTSET);
        boolean highestFanoutCutset = _result.getBoolean(DWCCommandParser.HIGHEST_FANOUT_CUTSET);
        boolean registerDetection = _result.getBoolean(DWCCommandParser.REGISTER_DETECTION);
        boolean noObufs = _result.getBoolean(DWCCommandParser.NO_OBUFS);

        /*
         * 1.b) Setup log file and print command-line argument information to
         * log file. If the user has activated Multiple EDIF Creation, this step
         * is delayed because a separate log file is created for each iteration.
         */
        if (!_parser.usingMultipleEDIF()) {
            createLogFile(_result.getString(DWCCommandParser.LOG));
            // 1.c) Print version info
            println(getVersionInfo());
            // Print CVS revision number to log file only
            println(REVISION, true);
        }

        // 2. Create DWC architecture
        NMRArchitecture dwcArch = getArchitecture(_result.getString(DWCCommandParser.PART));

        // 3. Load EDIF and merge multiple EDIF files
        EdifCell cell = XilinxMergeParser.parseAndMergeXilinx(_result.getString(DWCCommandParser.INPUT_FILE), Arrays
                .asList(_result.getStringArray(DWCCommandParser.DIR)), Arrays.asList(_result
                .getStringArray(DWCCommandParser.FILE)), System.out);

        /*
         * 4. Check for EdifCell with top-level Inout EdifPorts (We don't handle
         * these for now.) TODO: This will crash if we are using Multiple EDIF
         * Creation.
         */
        // Top-level INOUT ports are handled fine now.
        // TODO: Remove this option from the NMRCommandParser
        //		if (NMRUtilities.cellHasInoutPorts(cell)) {
        //			if (_result.getBoolean(DWCCommandParser.NO_IN_OUT_CHECK)) {
        //				println("WARNING: Your design contains INOUT ports. BLDwc does not currently support INOUT ports.");
        //			} else {
        //				println("ERROR: BLDwc does not currently support INOUT ports. Use "
        //						+ DWCCommandParser.NO_IN_OUT_CHECK
        //						+ " to force DWC with INOUT ports.");
        //				System.exit(1);
        //			}
        //		}
        /*
         * 5. Remove fmaps.
         */
        FmapRemover.removeFmaps(cell.getLibrary().getLibraryManager().getEdifEnvironment());

        /*
         * 6. Flatten design
         */
        FlattenedEdifCell flatCell = NMRUtilities.flattenCell(cell, System.out);

        /*
         * 7. Create the cell connectivity data structure Include top-level
         * ports and source-to-source Edges (needed for IOB analysis)
         */
        EdifCellInstanceGraph eciConnectivityGraph = new EdifCellInstanceGraph(flatCell, true, true);

        /*
         * 8. Analyze IOBs of the flattened EdifCell TODO: Add IOBAnalyzer
         * objects for other architectures/technologies
         */
        // Pack IOBs or not?
        boolean packInputRegs = _parser.packInputRegisters();
        boolean packOutputRegs = _parser.packOutputRegisters();
        AbstractIOBAnalyzer iobAnalyzer = new XilinxVirtexIOBAnalyzer(flatCell, eciConnectivityGraph, packInputRegs,
                packOutputRegs);
        // Delete the source-to-source Edges. We don't need them after the
        //   IOB analysis
        eciConnectivityGraph.removeSourceToSourceEdges();

        /*
         * 7. Remove half-latches, if desired
         */
        Collection<String> forceInstances = new ArrayList<String>();
        if (_result.getBoolean(DWCCommandParser.REMOVE_HL)) {
            println("Removing half-latches...");
            // TODO: Ensure we are using Xilinx (the only supported
            // architecture)
            HalfLatchArchitecture hlArchitecture = null;
            // Pack IOBs or not?
            if (packInputRegs || packOutputRegs) {
                // Send the (possibly large) list to the log file only
                println("\tThe following flip-flops were treated as IOB " + "registers during half-latch removal: "
                        + iobAnalyzer.getAllIOBRegisters(), true);
                hlArchitecture = new XilinxHalfLatchArchitecture(flatCell, iobAnalyzer);
            } else {
                hlArchitecture = new XilinxHalfLatchArchitecture(flatCell);
            }

            String hlPortName = _result.getString(DWCCommandParser.HL_PORT_NAME);
            boolean hlUsePort = false;
            if (hlPortName != null)
                hlUsePort = true;

            // Set up a SequentialEdifHalfLatchRemover (BHP: I'm not sure the other
            //   alternative--Topological...--works anymore)
            EdifHalfLatchRemover edifHalfLatchRemover = new SequentialEdifHalfLatchRemover(hlArchitecture, _result
                    .getInt(DWCCommandParser.HL_CONSTANT), hlUsePort, hlPortName);
            // Remove Half-latches
            flatCell = edifHalfLatchRemover.removeHalfLatches(flatCell);

            // Force triplication of internal half-latch constant or port ibuf
            EdifCellInstance safeConstantInstance;
            if (hlUsePort)
                safeConstantInstance = ((HalfLatchFlattenedEdifCell) flatCell).getSafeConstantPortBufferInstance();
            else
                safeConstantInstance = ((HalfLatchFlattenedEdifCell) flatCell).getSafeConstantGeneratorCell();
            // Set up this instance for triplication.
            // Add only the instance name because this instance is at the top
            // level and does not need any hierarchy information.
            forceInstances.add(safeConstantInstance.getName());

            //elapsedTime = System.currentTimeMillis() - startTime;
            //System.out.println("Half-latch removal took "+NMRUtilities.msToString(elapsedTime));

            // Since we replaced the flatCell object with the HalfLatchFlattenedEdifCell
            //   object, we'll need to re-create the Connectivity graph and the
            //   IOBAnalyzer object
            eciConnectivityGraph = new EdifCellInstanceGraph(flatCell, true, true);
            iobAnalyzer = new XilinxVirtexIOBAnalyzer(flatCell, eciConnectivityGraph);
            // Delete the source-to-source Edges. We don't need them after the
            //   IOB analysis
            eciConnectivityGraph.removeSourceToSourceEdges();

        }

        /*
         * 7.a Get clock net
         */
        ClockDomainParser cdp = null;
        try {
            cdp = new ClockDomainParser(flatCell, eciConnectivityGraph);
        } catch (InvalidEdifNameException e4) {
            e4.toRuntime();
        }
        EdifNet clockNet = cdp.getECIMap().keySet().iterator().next();

        // 8. Prune all unused cells in the Library
        cell.getLibrary().getLibraryManager().pruneNonReferencedCells(flatCell);

        double low, high, inc;
        long low_long, high_long, inc_long;
        NMRUtilities.UtilizationFactor factorType = _parser.getFactorType();

        /*
         * If we are using multiple EDIF, set the low, high and increment values
         * as specified on the command-line; otherwise, set them to the value
         * specified on the command line.
         */
        if (_parser.usingMultipleEDIF()) {
            low = _result.getDouble(DWCCommandParser.LOW);
            high = _result.getDouble(DWCCommandParser.HIGH);
            inc = _result.getDouble(DWCCommandParser.INC);
        } else {
            high = low = _result.getDouble(DWCCommandParser.FACTOR_VALUE);
            // The value of inc shouldn't matter, but just in case...
            inc = 1.0;
        }

        /*
         * Get the design name (that is, the input file name without any suffix)
         * in preparation for Multiple EDIF Creation.
         */
        String design = new File(_result.getString(DWCCommandParser.INPUT_FILE)).getName();
        design = NMRUtilities.removeEdifSuffixes(design);

        /*
         * Intermediate step of multiplying and dividing by 10^7 is to increase
         * precision (i.e. to avoid values such as 0.41000000003).
         */
        low_long = (long) (low * NMRUtilities.PRECISION_FACTOR);
        high_long = (long) (high * NMRUtilities.PRECISION_FACTOR);
        inc_long = (long) (inc * NMRUtilities.PRECISION_FACTOR);

        for (long factorValue_long = low_long; factorValue_long <= high_long; factorValue_long += inc_long) {

            double factorValue = (double) factorValue_long / (double) NMRUtilities.PRECISION_FACTOR;

            // Print status info
            System.out.println("Processing: " + factorType + " " + factorValue);
            /*
             * 9. Create folder and log file for current iteration.
             */
            File directory = null;
            if (_parser.usingMultipleEDIF()) {
                directory = new File(System.getProperty("user.dir") + "/" + design + "_"
                        + factorType.toString().toLowerCase() + "_"
                        + ((Double) factorValue).toString().replace('.', '_'));
                if (directory.exists()) {
                    System.err.println("Warning: Directory " + directory.getName() + " already exists.");
                    // Attempt to create the directory. Warn the user upon
                    // failure.
                } else if (!directory.mkdir()) {
                    throw new EdifRuntimeException("Unable to created directory: " + directory.toString()
                            + ". Perhaps you do not have write permission.");
                }
                createLogFile(directory.toString() + "/" + design + ".BLDwc.log");
                // 1.c) Print version info
                println(getVersionInfo());
                // Print CVS revision number to log file only
                println(REVISION, true);
            }

            /*
             * 10. Create resource tracker.
             */
            DeviceUtilizationTracker duTracker = null;
            try {
                duTracker = DeviceParser.createXilinxDeviceUtilizationTracker(flatCell, _result
                        .getString(DWCCommandParser.PART), _result.getDouble(DWCCommandParser.MERGE_FACTOR), _result
                        .getDouble(DWCCommandParser.OPTIMIZATION_FACTOR), factorValue, factorType);
            } catch (OverutilizationException e) {
                String errmsg = new String("ERROR: Original cell " + flatCell + " could not fit into specified part "
                        + _result.getString(DWCCommandParser.PART) + "\n." + e);
                throw new OverutilizationException(errmsg);
            }

            /*
             * 12. Duplicate top-level ports based on command line arguments
             * This method will exit if all requested ports cannot be
             * duplicated.
             */
            _portsToDuplicate = duplicatePorts(flatCell, duTracker, eciConnectivityGraph);

            /*
             * 13. Prevent and force triplication of specific cell types and
             * instances.
             */
            // 13a. Exclude instances and cell types from DWC as requested by user
            if (_result.contains(DWCCommandParser.NO_DWC_I)) {
                for (String instance : _result.getStringArray(DWCCommandParser.NO_DWC_I)) {
                    duTracker.excludeInstanceFromNMR(instance);
                }
            }
            if (_result.contains(DWCCommandParser.NO_DWC_C)) {
                for (String celltype : _result.getStringArray(DWCCommandParser.NO_DWC_C)) {
                    duTracker.excludeCellTypeFromNMR(celltype);
                }
            }

            /*
             * 13b. Add instances and cell types for which the user wants to
             * force duplication
             */
            forceDuplication(flatCell, duTracker);

            println("");
            println("Analyzing design . . .");

            /*
             * 14. Perform feedback analysis
             */
            // 14a. Create SCCs
            SCCDepthFirstSearch sccDFS = new SCCDepthFirstSearch(eciConnectivityGraph);
            // 14b. Perform feedback analysis and determine cut set
            List<Edge> cutSet = new ArrayList<Edge>();
            boolean allSCCInstancesDuplicated;
            // TODO : alter this for DWC to possibly determine a different cut set than TMR
            if (_result.getBoolean(DWCCommandParser.FULL_DWC) || !_result.getBoolean(DWCCommandParser.NO_DWC_FEEDBACK)) {
                allSCCInstancesDuplicated = NMRGraphUtilities.nmrSCCsUsingSCCDecomposition(sccDFS, dwcArch, duTracker,
                        _result.getBoolean(DWCCommandParser.DO_SCC_DECOMPOSITION), _result
                                .getInt(DWCCommandParser.SCC_SORT_TYPE), cutSet, 2);
                // Print results for SCC duplication
                if (!_result.getBoolean(DWCCommandParser.FULL_DWC)) {
                    if (allSCCInstancesDuplicated)
                        println("\tFeedback section will be fully duplicated");
                    else
                        println("\tFeedback section will be partially duplicated");
                }
            } else {
                /*
                 * This only happens if the user selected the "nodwcFeedback"
                 * option, in which no cuts are needed.
                 */
                allSCCInstancesDuplicated = false;
                println("\tFeedback section will not be duplicated");
            }

            // 15. Do either full dwc or partial dwc
            if (_result.getBoolean(DWCCommandParser.FULL_DWC)) {
                println("\tFull DWC requested.");
                for (Iterator instancesIterator = flatCell.cellInstanceIterator(); instancesIterator.hasNext();) {
                    EdifCellInstance eci = (EdifCellInstance) instancesIterator.next();
                    try {
                        // TODO : this nmrInstance isn't fully functioning yet
                        duTracker.nmrInstance(eci, _replicationFactor);
                    } catch (OverutilizationEstimatedStopException e1) {
                        String errmsg = new String("ERROR: Instance " + eci
                                + " not added to resource tracker. Full DWC will not fit in part "
                                + _result.getString(DWCCommandParser.PART) + ".\n" + e1);
                        throw new OverutilizationException(errmsg);
                    } catch (OverutilizationHardStopException e2) {
                        println("WARNING: Instance " + eci
                                + " not added to resource tracker due to hard resource constraints in part "
                                + _result.getString(DWCCommandParser.PART) + ".\n" + e2);
                    } catch (DuplicateNMRRequestException e3) {
                        /*
                         * TODO: Do we really want to just ignore this? For now
                         * we have to ignore because feedback already added
                         * instances to the resource tracker.
                         */
                        //throw new EdifRuntimeException(e3.toString());
                    }
                }
            } else {
                // TODO : handle the partial DWC section
                // Partial DWC selection
                // SCC Inputs and Outputs plus Feed-Forward section
                int[] duplicationStatus = PartialInputOutputFeedForwardTMR.tmrSCCInputAndOutput(flatCell,
                        eciConnectivityGraph, duTracker, dwcArch, sccDFS, !_result
                                .getBoolean(DWCCommandParser.NO_DWC_INPUT_TO_FEEDBACK), !_result
                                .getBoolean(DWCCommandParser.NO_DWC_FEEDBACK_OUTPUT), !_result
                                .getBoolean(DWCCommandParser.NO_DWC_FEED_FORWARD), _result
                                .getInt(DWCCommandParser.INPUT_ADDITION_TYPE), _result
                                .getInt(DWCCommandParser.OUTPUT_ADDITION_TYPE));

                // Report on results of duplication
                reportDuplicationStatus(duplicationStatus);
            }

            // 16. Get the set of cells to duplicate
            Collection<EdifCellInstance> actualCellInstancesToDuplicate = duTracker.getCurrentNMRInstances();

            // DEBUG
            //		Collection<EdifCell> cellsNotToDuplicate = new ArrayList<EdifCell>(
            //				flatCell.getSubCellList());
            //		cellsNotToDuplicate.removeAll(actualCellInstancesToDuplicate);
            //		System.out
            //				.println("Instances NOT duplicated: " + cellsNotToDuplicate);
            //		System.out.println("Instances duplicated: "
            //				+ actualCellInstancesToDuplicate);

            /*
             * 17. Determine where to place CheckPoints in the feedback
             * (Analagous to TMR cutset)
             */
            // output checkpoints are now handled in the TMRDWCEdifCell
            Collection<EdifPortRef> persistentPortRefsToCompare = new ArrayList<EdifPortRef>();

            if (doPersistence) {
                if (highestFFFanoutCutset || highestFanoutCutset) {
                    EdifOutputPortRefGraph graph = new EdifOutputPortRefGraph(flatCell);
                    SCCDepthFirstSearch PRGsccDFS = new SCCDepthFirstSearch(graph);
                    if (highestFFFanoutCutset)
                        persistentPortRefsToCompare = NMRGraphUtilities.createDecomposeValidCutSetFFFanout(graph,
                                PRGsccDFS, dwcArch);
                    else
                        persistentPortRefsToCompare = NMRGraphUtilities.createDecomposeValidCutSetFanout(graph,
                                PRGsccDFS, dwcArch);
                } else {
                    for (Iterator i = sccDFS.getTopologicallySortedTreeList().iterator(); i.hasNext();) {
                        BasicDepthFirstSearchTree scc = (BasicDepthFirstSearchTree) i.next();
                        cutSet.addAll(NMRGraphUtilities.createDecomposeValidCutSet(eciConnectivityGraph, scc, dwcArch));
                    }
                    persistentPortRefsToCompare = NMRUtilities.getPortRefsToCutFromEdges(cutSet, eciConnectivityGraph,
                            dwcArch);
                }
            }
            /*
             * 18. Duplicate design, Cut Feedback & Report
             */
            // Duplicate & Cut feedback!
            println("");
            println("Duplicating design . . .");

            EdifCell newCell = null;
            int numberOfCompares = 0;
            int numberOfOutputCompares = 0;
            int numberOfPersistentCompares = 0;
            int numberOfDuplicatedInstances = 0;
            int numberOfDuplicatedNets = 0;
            int numberOfDuplicatedPorts = 0;

            TMRDWCEdifCell dwcCell = null;

            Collection<DepthFirstTree> trees = sccDFS.getTrees();
            Set<EdifCellInstance> feedbackPlusInput = new LinkedHashSet<EdifCellInstance>();
            for (DepthFirstTree tree : trees) {
                for (Object o : tree.getNodes()) {
                    if (o instanceof EdifCellInstance) {
                        feedbackPlusInput.add((EdifCellInstance) o);
                    }
                }
            }
            feedbackPlusInput.addAll(eciConnectivityGraph.getAncestors(feedbackPlusInput));

            // shuffling data structures in order to use TMRDWCEdifCell class
            Map<Integer, List<EdifPort>> portsToDuplicateMap = new LinkedHashMap<Integer, List<EdifPort>>();
            Map<Integer, List<EdifCellInstance>> instancesToDuplicateMap = new LinkedHashMap<Integer, List<EdifCellInstance>>();
            List<EdifPort> portsToDuplicateList = new ArrayList<EdifPort>();
            portsToDuplicateList.addAll(_portsToDuplicate);
            List<EdifCellInstance> instancesToDuplicateList = new ArrayList<EdifCellInstance>();
            instancesToDuplicateList.addAll(actualCellInstancesToDuplicate);
            // Map will not include any ports or instances to triplicate
            portsToDuplicateMap.put(new Integer(2), portsToDuplicateList);
            portsToDuplicateMap.put(new Integer(3), new ArrayList<EdifPort>());
            instancesToDuplicateMap.put(new Integer(2), instancesToDuplicateList);
            instancesToDuplicateMap.put(new Integer(3), new ArrayList<EdifCellInstance>());

            try {
                if (_result.contains(DWCCommandParser.DWC_SUFFIX)) {
                    // Use user-supplied suffixes
                    // The TMR stuff is so that we can use the TMRDWCEdifCell class
                    Map<Integer, List<String>> replicationSuffixMap = new LinkedHashMap<Integer, List<String>>();
                    List<String> tmrSuffixes = new ArrayList<String>();
                    tmrSuffixes.add("_TMR_0");
                    tmrSuffixes.add("_TMR_1");
                    tmrSuffixes.add("_TMR_2");
                    replicationSuffixMap.put(new Integer(3), tmrSuffixes);
                    replicationSuffixMap.put(new Integer(2), Arrays.asList(_result
                            .getStringArray(DWCCommandParser.DWC_SUFFIX)));
                    dwcCell = new TMRDWCEdifCell(cell.getLibrary(), flatCell.getName() + "_DWC", flatCell,
                            feedbackPlusInput, new XilinxTMRArchitecture(), dwcArch, portsToDuplicateMap,
                            instancesToDuplicateMap, persistentPortRefsToCompare, _result
                                    .getBoolean(DWCCommandParser.USE_DRC), packOutputRegs, registerDetection, clockNet,
                            replicationSuffixMap, noObufs);
                } else {
                    // Use default suffixes
                    dwcCell = new TMRDWCEdifCell(cell.getLibrary(), flatCell.getName() + "_DWC", flatCell,
                            feedbackPlusInput, new XilinxTMRArchitecture(), dwcArch, portsToDuplicateMap,
                            instancesToDuplicateMap, persistentPortRefsToCompare, _result
                                    .getBoolean(DWCCommandParser.USE_DRC), packOutputRegs, registerDetection, clockNet,
                            noObufs);
                }
            } catch (EdifNameConflictException e2) {
                e2.toRuntime();
            } catch (InvalidEdifNameException e2) {
                e2.toRuntime();
            }

            numberOfCompares = dwcCell.getComparators().size();
            numberOfOutputCompares = dwcCell.getNonPersistentErrorNets().size();
            numberOfPersistentCompares = dwcCell.getPersistentErrorNets().size();
            numberOfDuplicatedInstances = dwcCell.getReplicatedInstances().size();
            numberOfDuplicatedNets = dwcCell.getReplicatedNets().size();
            numberOfDuplicatedPorts = dwcCell.getReplicatedPorts().size();
            newCell = dwcCell;

            // 18.b) Write domain report
            if (_parser.usingMultipleEDIF()) {
                dwcCell.printDomainReport(directory + "/" + directory.getName() + "_domain_report.txt");
            } else {
                dwcCell.printDomainReport(_result.getString(DWCCommandParser.DOMAIN_REPORT));
            }

            // 18.c) Print summary of DWC performed
            int numberOfOriginalInstances = flatCell.getSubCellList().size();
            print("\tAdded " + numberOfCompares + " compares ");

            if (doPersistence)
                println("(" + numberOfPersistentCompares + " persistent, " + numberOfOutputCompares
                        + " non-persistent)");
            else
                println("");
            println("\t" + numberOfDuplicatedInstances + " instances out of " + numberOfOriginalInstances
                    + " cells duplicated (" + (numberOfDuplicatedInstances * 100 / numberOfOriginalInstances)
                    + "% coverage)");
            println("\t" + 2 * numberOfDuplicatedInstances + " new instances added to design. ");
            println("\t" + numberOfDuplicatedNets + " nets duplicated (" + 2 * numberOfDuplicatedNets
                    + " new nets added).");
            println("\t" + numberOfDuplicatedPorts + " ports duplicated.");

            // 19. Set DWC instance to top cell
            EdifCellInstance dwcInstance = null;
            EdifDesign newDesign = null;
            try {
                dwcInstance = new EdifCellInstance("dwc_" + flatCell.getName(), null, newCell);
                newDesign = new EdifDesign(newCell.getEdifNameable());
            } catch (InvalidEdifNameException e1) {
                e1.toRuntime();
            }
            newDesign.setTopCellInstance(dwcInstance);
            // copy design properties
            EdifDesign oldDesign = flatCell.getLibrary().getLibraryManager().getEdifEnvironment().getTopDesign();
            if (oldDesign.getPropertyList() != null) {
                for (Iterator it = oldDesign.getPropertyList().values().iterator(); it.hasNext();) {
                    Property p = (Property) it.next();
                    newDesign.addProperty((Property) p.clone());
                }
            }
            flatCell.getLibrary().getLibraryManager().getEdifEnvironment().setTopDesign(newDesign);

            /*
             * flatCell is no longer needed after dwc, so it deletes itself from
             * the library it belongs to.
             */
            flatCell.getLibrary().deleteCell(flatCell, true);

            // 20. Print Report to stdout if desired; always print to log file
            int dwcPrimitives = EdifUtils.countRecursivePrimitives(newCell);
            int flatPrimitives = EdifUtils.countRecursivePrimitives(flatCell);
            int dwcNets = EdifUtils.countRecursiveNets(newCell);
            int flatNets = EdifUtils.countRecursiveNets(flatCell);
            int dwcPortRefs = EdifUtils.countPortRefs(newCell, true);
            int flatPortRefs = EdifUtils.countPortRefs(flatCell, true);
            StringBuffer summBuf = new StringBuffer();
            summBuf.append("\nDWC circuit contains:\n");
            summBuf.append("\t" + dwcPrimitives + " primitives (" + (100 * (dwcPrimitives - flatPrimitives))
                    / flatPrimitives + "% increase)\n");
            summBuf.append("\t" + dwcNets + " nets (" + (100 * (dwcNets - flatNets)) / flatNets + "% increase)\n");
            summBuf.append("\t" + dwcPortRefs + " net connections (" + (100 * (dwcPortRefs - flatPortRefs))
                    / flatPortRefs + "% increase)\n");
            summBuf.append("\nPost DWC utilization estimate:\n" + duTracker);

            //println(summBuf.toString(), !_result.getBoolean(DWCCommandParser.SUMMARY));
            // Always print to stdout and log file. BHP: There is so much info 
            //   printed already, this extra bit shouldn't be offensive.
            // TODO: Remove --summary option from command line
            println(summBuf.toString());

            // 21. Write output file
            String outputFileName = null;
            if (_parser.usingMultipleEDIF()) {
                outputFileName = directory + "/" + directory.getName() + ".edf";
            } else {
                outputFileName = _result.getString(DWCCommandParser.OUTPUT_FILE);
            }
            NMRUtilities.createOutputFile(outputFileName, newCell);
            println("Wrote output file to " + outputFileName);

            /*
             * 22. Delete dwcCell for further iterations of Multiple EDIF
             * Creation
             */
            dwcCell.getLibrary().deleteCell(newCell, true);

        }

    }// end main

    /**
     * @return a String of the version and date of this release of the BLDwc
     * tool.
     */
    public static String getVersionInfo() {
        return "BLDwc Tool version " + DWCPersistence.VERSION + ", " + DWCPersistence.RELEASE_DATE;
    }

    /**
     * Create the File object for the given filename and call {@link #logArgs()}.
     * 
     * @param filename The filename
     */
    protected static void createLogFile(String filename) {
        _log = NMRUtilities.createLogFile(filename);
        logArgs();
    }

    /**
     * Adds the chosen top-level ports and associated BUF (IOB) instances to the
     * DeviceUtilizationTracker object given. The ports to duplicate are
     * determined by the DWCCommandParser and the EdifCellInstanceGraph object
     * is used to find connected BUF instances, if any.<br>
     * TODO: alter this to only duplicate
     * 
     * @param flatCell
     * @param duTracker
     * @param graph
     * @return {@link #_portsToDuplicate}
     */
    protected static Set<EdifPort> duplicatePorts(EdifCell flatCell, DeviceUtilizationTracker duTracker,
            EdifCellInstanceGraph graph) {
        // Determine which ports to duplicate
        // *** TODO: This needs to be moved!
        // Identify BOARD specific ports to duplicate
        _noDuplicatePorts = new LinkedHashSet<String>();
        for (String port : NMRUtilities.SLAAC1V_PORTS_NOT_TO_REPLICATE)
            _noDuplicatePorts.add(port);

        // add the ports specified by the command line to the not duplicate Set
        _noDuplicatePorts.addAll(Arrays.asList(_result.getStringArray(DWCCommandParser.NO_DWC_P)));

        // TODO : alter this to duplicate
        // Filter the set of ports to duplicate
        _portsToDuplicate = filterPortsToDuplicate(flatCell, _result.getBoolean(DWCCommandParser.DWC_INPORTS), _result
                .getBoolean(DWCCommandParser.DWC_OUTPORTS), _noDuplicatePorts);
        // Create list of ports NOT to duplicate
        _portsNotToDuplicate = flatCell.getPortList();
        _portsNotToDuplicate.removeAll(_portsToDuplicate);

        // Add ports for duplication (with associated IBUF/OBUF instances)
        // Use the EdifCellInstanceGraph graph to find the BUFs
        _bufsToDuplicate = NMRUtilities.getPortBufs(_portsToDuplicate, graph);
        try {
            duTracker.nmrInstances(_bufsToDuplicate, _replicationFactor);
            // TODO : change this to a NMR exception
        } catch (DuplicateNMRRequestException e1) {
            // Already TMR'd
            System.out.println("WARNING: Duplicate DWC Port request. Should not get here: " + e1);
        } catch (OverutilizationEstimatedStopException e2) {
            // DeviceUtilizationTracker says to stop adding instances for dwc
            System.out.println("WARNING: Device full when adding Ports. Should not get here. " + e2);
        } catch (OverutilizationHardStopException e3) {
            // Hit some hard limit.
            System.out.println("ERROR: Could not duplicate " + _portsToDuplicate.size() + " top-level ports"
                    + " due to resource constraints: " + _portsToDuplicate);
            System.exit(1);
        }

        // Register the port BUFs to skip with the DeviceUtilizationTracker
        _bufsNotToDuplicate = NMRUtilities.getPortBufs(_portsNotToDuplicate, graph);
        for (EdifCellInstance eci : _bufsNotToDuplicate) {
            duTracker.excludeInstanceFromNMR(eci);
        }

        return _portsToDuplicate;
    }

    /**
     * Start with all the top-level ports of the original cell, remove ports
     * marked to not be duplicated, and return the remaining set of ports, which
     * is the set of ports to be duplicated.
     * <p>
     * TODO: alter to duplicate
     * <p>
     * TODO: What if we have an EdifSingleBitPort ??
     * 
     * @param cell The original, un-duplicated EdifCell
     * @param dupInputs Should inputs be duplicated?
     * @param dupOutputs Should outputs be duplicated?
     * @param noDuplicate A Collection of String objects of port names that will
     * not be duplicated.
     * @return a Set of EdifPort objects to be duplicated
     */
    protected static Set<EdifPort> filterPortsToDuplicate(EdifCell cell, boolean dupInputs, boolean dupOutputs,
            Collection<String> noDuplicate) {

        Set<EdifPort> ports = new LinkedHashSet<EdifPort>();

        if (!dupInputs && !dupOutputs)
            return ports; // Return empty Set
        for (EdifPort port : cell.getPortList()) {
            System.out.println(port.getClass().getName());
            System.out.print(port);
            if (noDuplicate.contains(port.getName())) {
                System.out.println(" : in list to not duplicate.");
                continue;
            }
            if (dupInputs && port.isInput()) {
                System.out.println(" : duplicate input port");
                ports.add(port);
            } else if (dupOutputs && port.isOutput()) {
                System.out.println(" : duplicate output port");
                ports.add(port);
            } else
                System.out.println(" : nothing being done");
        }
        return ports;
    }

    /**
     * Forces certain EdifCellInstances and cell types to be duplicated based on
     * the command line arguments provided by the user. Instance names are case
     * sensitive, cell types are not.
     * 
     * @param flatCell
     * @param duTracker
     */
    protected static void forceDuplication(EdifCell flatCell, DeviceUtilizationTracker duTracker) {
        // Grab set of instances to force duplication from the arguments, if any

        Set<String> instancesToForceDuplication = new LinkedHashSet<String>(0);
        for (String instance : _result.getStringArray(DWCCommandParser.DWC_I)) {
            instancesToForceDuplication.add(instance);
        }

        // Grab set of cell types to force duplication from the arguments, if any.
        // Lower-case all of the contained Strings for easier comparison.
        HashSet<String> celltypesToForceDuplication = new LinkedHashSet<String>();
        for (String type : _result.getStringArray(DWCCommandParser.DWC_C)) {
            celltypesToForceDuplication.add(type.toLowerCase());
        }

        for (Iterator i = flatCell.cellInstanceIterator(); i.hasNext();) {
            EdifCellInstance eci = (EdifCellInstance) i.next();
            if (celltypesToForceDuplication.contains(eci.getType().toLowerCase())
                    || instancesToForceDuplication.contains(eci.getName()))
                // Duplicate instances that matched the command line arguments
                try {
                    // TODO : alter the duTracker
                    duTracker.nmrInstance(eci, _replicationFactor);
                    if (debug)
                        System.out.println("Forcing duplication of instance: " + eci);
                    // TODO : alter the exceptions
                } catch (DuplicateNMRRequestException e1) {
                    // Already TMR'd
                } catch (OverutilizationEstimatedStopException e2) {
                    // This instance will not fit in the device. (Device is
                    // full)
                    println("WARNING: Could not add instance " + eci + ". Device full.");
                } catch (OverutilizationHardStopException e3) {
                    // There are no more resources available for this instance
                    println("WARNING: Could not add instance " + eci + ". No resources of type " + eci.getType()
                            + " available.");
                }
        }

    }

    /**
     * Return a DWCArchitecture object for the specified part.
     * 
     * @param partString The specified part
     * @return A DWCArchtecture object
     */
    protected static XilinxDWCArchitecture getArchitecture(String partString) {
        String technologyString = XilinxPartValidator.getTechnologyFromPart(partString);
        if (technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX)
                || technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX2))
            return new XilinxDWCArchitecture();
        throw new EdifRuntimeException("Invalid Technology: " + technologyString + ". Valid technologies include "
                + NMRUtilities.VIRTEX + " and " + NMRUtilities.VIRTEX2 + ".");
    }

    /**
     * Return a Collection of all the EdifPortRef objects associated with a
     * top-level output port. These fall in two categories: those connected to
     * an IOB (Input/Output Buffer) and those connected directly to the
     * top-level Port. In the former case, the EdifPortRef returned in the
     * Collection is the PortRef on the input to the IOB. In the latter case, it
     * is the PortRef on the top-level output Port itself. This is analogous to
     * the TMR methods to created a "cutset" but instead of inserting voters,
     * these nets are connected to comparator inputs.
     * 
     * @param cell The EdifCell
     * @param graph The connectivity graph
     * @return A Set of EdifPortRefs. It is anticipated that comparators will be
     * placed at each of these portRefs
     * @throws InvalidParameterException if the given EdifCell does not match
     * the EdifCell in the connectivity graph.
     */
    // this isn't being used right now because it's done in TMRDWCEdifCell
    protected static Set<EdifPortRef> getOutputPortRefs(EdifCell cell, EdifCellInstanceGraph graph) {
        if (!cell.equals(graph.getCell())) {
            throw new InvalidParameterException(
                    "Invalid parameters. The EdifCell of the EdifCellInstanceGraph object must match the given EdifCell object.");
        }

        Set<EdifPortRef> result = new LinkedHashSet<EdifPortRef>();
        Collection<EdifPort> topLevelPorts = cell.getOutputPorts();
        for (EdifPort port : topLevelPorts) {
            Collection<EdifSingleBitPort> esbps = port.getSingleBitPortList();
            for (EdifSingleBitPort sbport : esbps) {
                Collection<EdifCellInstance> insts = graph.getPredecessors(sbport);
                for (EdifCellInstance inst : insts) {
                    if (XilinxResourceMapper.getResourceType(inst) == XilinxResourceMapper.IO) {
                        Collection<EdifPortRef> eprfs = graph.getEPRsWhichReferenceInputPortsOfECI(inst);
                        for (EdifPortRef epr : eprfs) {
                            if (debug)
                                System.out.println("IN " + epr.toString());
                            if (epr.getPort().getName().compareToIgnoreCase("i") == 0) {
                                //System.out.println("EPR:\n" + epr);
                                result.add(epr);
                            }
                        }
                        Collection<EdifPortRef> eprfs2 = graph.getEPRsWhichReferenceOutputPortsOfECI(inst);
                        for (EdifPortRef epr : eprfs2) {
                            if (debug)
                                System.out.println("OUT " + epr.toString());
                            if (epr.getPort().getName().compareToIgnoreCase("i") == 0) {
                                //System.out.println("EPR:\n" + epr);
                                result.add(epr);
                            }
                        }
                    } else {
                        result.addAll(graph.getEdifPortRefs(port));
                    }
                }
            }
        }
        if (debug)
            System.out.println("SIZE:  " + result.size());
        return result;
    }

    /**
     * Print a description of the command-line arguments used to the log file.
     */
    protected static void logArgs() {
        _log.println("Tool Options Used:");
        _log.println("\tInput file: " + _result.getString(DWCCommandParser.INPUT_FILE));
        _log.println("\tOutput file: " + _result.getString(DWCCommandParser.OUTPUT_FILE));
        if (_result.contains(DWCCommandParser.DWC_C))
            _log.println("\tForcing duplication of cell types: "
                    + Arrays.asList(_result.getStringArray(DWCCommandParser.DWC_C)));
        if (_result.contains(DWCCommandParser.DWC_I))
            _log.println("\tForcing duplication of cell instances: "
                    + Arrays.asList(_result.getStringArray(DWCCommandParser.DWC_I)));
        if (_result.contains(DWCCommandParser.NO_DWC_C))
            _log.println("\tNot duplicating cell types: "
                    + Arrays.asList(_result.getStringArray(DWCCommandParser.NO_DWC_C)));
        if (_result.contains(DWCCommandParser.NO_DWC_I))
            _log.println("\tNot duplicating cell instances: "
                    + Arrays.asList(_result.getStringArray(DWCCommandParser.NO_DWC_I)));
        if (_result.getBoolean(DWCCommandParser.DWC_INPORTS))
            _log.println("\tDuplicating input ports");
        if (_result.getBoolean(DWCCommandParser.DWC_OUTPORTS))
            _log.println("\tDuplicating output ports");
        if (_result.contains(DWCCommandParser.NO_DWC_P))
            _log
                    .println("\tNot duplicating ports: "
                            + Arrays.asList(_result.getStringArray(DWCCommandParser.NO_DWC_P)));
        if (_result.getBoolean(DWCCommandParser.FULL_DWC))
            _log.println("\tPerforming full DWC. Skipping partial DWC evaluation.");
        else {
            _log.println("\tPartial DWC options:");
            if (_result.getBoolean(DWCCommandParser.NO_DWC_FEEDBACK))
                _log.println("\t\tExcluding feedback structures from duplication.");
            if (_result.getBoolean(DWCCommandParser.NO_DWC_INPUT_TO_FEEDBACK))
                _log.println("\t\tExcluding logic driving feedback structures from duplication.");
            if (_result.getBoolean(DWCCommandParser.NO_DWC_FEEDBACK_OUTPUT))
                _log.println("\t\tExcluding logic driven by feedback structures from duplication.");
            if (_result.getBoolean(DWCCommandParser.NO_DWC_FEED_FORWARD))
                _log.println("\t\tExcluding feed forward logic from duplication.");
            _log.println("\t\tInput Addition Type: " + _result.getInt(DWCCommandParser.INPUT_ADDITION_TYPE));
            _log.println("\t\tOutput Addition Type: " + _result.getInt(DWCCommandParser.OUTPUT_ADDITION_TYPE));
            _log.println("\t\tSCC Sort Type: " + _result.getInt(DWCCommandParser.SCC_SORT_TYPE));
            if (_result.getBoolean(DWCCommandParser.DO_SCC_DECOMPOSITION))
                _log.println("\t\tWill decompose strongly-connected components (SCCs)");
            _log.println("\t\tMerge Factor: " + _result.getDouble(DWCCommandParser.MERGE_FACTOR));
            _log.println("\t\tOptimization Factor: " + _result.getDouble(DWCCommandParser.OPTIMIZATION_FACTOR));

            // Print utilization factor type and value
            UtilizationFactor type = _parser.getFactorType();
            String verboseFactorType = NMRUtilities.getVerboseUtilizationFactor(type);
            String factorValue = ((Double) _result.getDouble(NMRCommandParser.FACTOR_VALUE)).toString();
            _log.println("\t\t" + verboseFactorType + factorValue);

        }
        if (_result.getBoolean(DWCCommandParser.REMOVE_HL)) {
            _log.println("\tHalf-latch removal options:");
            _log.println("\t\tHalf-latch constant: " + _result.getInt(DWCCommandParser.HL_CONSTANT));
            if (_result.userSpecified(DWCCommandParser.HL_PORT_NAME))
                _log.println("\t\tHalf-latch constant port: " + _result.getString(DWCCommandParser.HL_PORT_NAME));
            if (_parser.packInputRegisters() && _parser.packOutputRegisters())
                _log.println("\t\tBoth input and output flip-flops will be treated as IOB registers");
            else if (_parser.packInputRegisters())
                _log.println("\t\tInput flip-flops will be treated as IOB registers");
            else if (_parser.packOutputRegisters())
                _log.println("\t\tOutput flip-flops will be treated as IOB registers");
            else
                _log.println("\t\tNo flip-flops will be treated as IOB registers");
        }

        if (_result.getBoolean(DWCCommandParser.NO_IN_OUT_CHECK))
            _log.println("\tIgnoring restriction on designs with INOUT ports (NOT recommended).");
        if (_result.getBoolean(DWCCommandParser.USE_DRC)) {
            _log.println("\tUsing Dual Rail Checker");
        } else {
            _log.println("\tUsing Single Rail Checker");
        }

        _log.println("\tPart: " + _result.getString(DWCCommandParser.PART));
        _log.println("\tLog file: " + _result.getString(DWCCommandParser.LOG));
        _log.println();
    }

    /**
     * Write the given String to both a logfile (as a StringBuffer) and to
     * standard output.
     */
    protected static void print(String str) {
        print(str, false);
    }

    /**
     * Write the given String to both a log file (as a StringBuffer) and
     * optionally to standard output.
     * 
     * @param str The String to be printed.
     * @param logOnly If true, only send the String to the log file. If false
     * send the String to both the log file as well as stdout.
     */
    protected static void print(String str, boolean logOnly) {
        _log.print(str);
        if (!logOnly)
            System.out.print(str);
    }

    /**
     * Write the given String to both a logfile (as a StringBuffer) and to
     * standard output.
     */
    protected static void println(String str) {
        println(str, false);
    }

    /**
     * Write the given String to both a log file (as a StringBuffer) and
     * optionally to standard output.
     * 
     * @param str The String to be printed.
     * @param logOnly If true, only send the String to the log file. If false
     * send the String to both the log file as well as stdout.
     */
    protected static void println(String str, boolean logOnly) {
        _log.println(str);
        if (!logOnly)
            System.out.println(str);
    }

    // /////////////////////////////////////////////////////////////
    // Protected Members

    /**
     * TODO: alter for duplication
     * 
     * @param duplicationStatus An array of int's describing the level of
     * duplication (none, some, or all) for each section of the circuit
     */
    protected static void reportDuplicationStatus(int[] duplicationStatus) {
        // Input to Feedback
        switch (duplicationStatus[PartialInputOutputFeedForwardTMR.INPUT_TO_FEEDBACK]) {
        case PartialInputOutputFeedForwardTMR.NONE:
            println("\tInput to Feedback section will not be duplicated");
            break;
        case PartialInputOutputFeedForwardTMR.SOME:
            println("\tInput to Feedback section will be partially duplicated");
            break;
        case PartialInputOutputFeedForwardTMR.ALL:
            println("\tInput to Feedback section will be fully duplicated");
            break;
        }
        // Feedback Output
        switch (duplicationStatus[PartialInputOutputFeedForwardTMR.FEEDBACK_OUTPUT]) {
        case PartialInputOutputFeedForwardTMR.NONE:
            println("\tFeedback Output section will not be duplicated");
            break;
        case PartialInputOutputFeedForwardTMR.SOME:
            println("\tFeedback Output section will be partially duplicated");
            break;
        case PartialInputOutputFeedForwardTMR.ALL:
            println("\tFeedback Output section will be fully duplicated");
            break;
        }
        // Feed Forward
        switch (duplicationStatus[PartialInputOutputFeedForwardTMR.FEED_FORWARD]) {
        case PartialInputOutputFeedForwardTMR.NONE:
            println("\tFeed Forward section will not be duplicated");
            break;
        case PartialInputOutputFeedForwardTMR.SOME:
            println("\tFeed Forward section will be partially duplicated");
            break;
        case PartialInputOutputFeedForwardTMR.ALL:
            println("\tFeed Forward section will be fully duplicated");
            break;
        }
    }

    /**
     * Set of EdifCellInstance objects to be duplicated
     */
    protected static Set<EdifCellInstance> _bufsToDuplicate;

    /**
     * Set of EdifCellInstance objects that should <i>not</i> be duplicated
     */
    protected static Set<EdifCellInstance> _bufsNotToDuplicate;

    /**
     * PrintStream for logfile
     */
    protected static PrintStream _log;

    /**
     * Set of String names of ports that should <i>not</i> be duplicated
     */
    protected static Set<String> _noDuplicatePorts;

    /**
     * Set of EdifPort objects to be duplicated.
     */
    protected static Set<EdifPort> _portsToDuplicate;

    /**
     * Collection of EdifPort objects to <i>not</i> be duplicated
     */
    protected static Collection<EdifPort> _portsNotToDuplicate;

    /**
     * DWCCommandParser used to parse the command-line arguments of FlattenDWC.
     */
    protected static DWCCommandParser _parser;

    /**
     * JSAPResult used to access the command-line arguments after being parsed.
     */
    protected static JSAPResult _result;

    /**
     * Use to enable or disable debugging print statements
     */
    private static boolean debug = false;

    /**
     * Use to indicate duplication
     */
    private static final int _replicationFactor = 2;
}
