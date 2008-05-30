/*
 * Provides a TMR method that includes design flattening.
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
package edu.byu.ece.edif.tools.replicate.nmr.tmr;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
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
import edu.byu.ece.edif.core.EdifUtils;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.tools.EdifTools;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCellInstance;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.AbstractDeviceUtilizationTracker;
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
import edu.byu.ece.edif.tools.replicate.nmr.dwc.FlattenDWC;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxPartValidator;
import edu.byu.ece.edif.tools.sterilize.fmap.FmapRemover;
import edu.byu.ece.edif.tools.sterilize.halflatch.EdifHalfLatchRemover;
import edu.byu.ece.edif.tools.sterilize.halflatch.HalfLatchArchitecture;
import edu.byu.ece.edif.tools.sterilize.halflatch.HalfLatchFlattenedEdifCell;
import edu.byu.ece.edif.tools.sterilize.halflatch.SequentialEdifHalfLatchRemover;
import edu.byu.ece.edif.tools.sterilize.halflatch.XilinxHalfLatchArchitecture;
import edu.byu.ece.edif.util.clockdomain.ClockDomainParser;
import edu.byu.ece.edif.util.graph.EdifCellBadCutGroupings;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollection;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollectionGraph;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollectionLink;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.graph.EdifOutputPortRefGraph;
import edu.byu.ece.edif.util.iob.AbstractIOBAnalyzer;
import edu.byu.ece.edif.util.iob.XilinxVirtexIOBAnalyzer;
import edu.byu.ece.edif.util.jsap.TMRCommandParser;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.dfs.DepthFirstTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * Provides a TMR method that includes design flattening. This class will
 * perform TMR as follows:
 * <ol>
 * <li> Parse and merge the top-level EIDF file
 * <li> Flatten the top-level EDIF file
 * <li> Create an EdifCellGraph from the flattened file
 * <li> Identify cuts
 * </ol>
 * 
 * @author Michael Wirthlin
 * @author Brian Pratt
 * @author <a href="jcarroll@byu.net">James Carroll</a>
 * @see FlattenDWC
 * @see TMREdifCell
 * @see TMRCommandParser
 * @version 0.2.4 - 16 Feb 2007
 * @since Created on May 20, 2005
 * @deprecated
 */
public class FlattenTMR {

    // Version Number & History
    // v0.1.0 -  8 Mar 2006 - released to LANL (without version number)
    // v0.1.1 -  9 Mar 2006 - added version info
    // v0.1.2 - 17 Apr 2006 - added automatic IOB handling, log file, fixed rename issues
    // v0.1.3 -  3 May 2006 - added FB, Input to FB, FB Output section selections. Complete partial TMR matches "Full TMR"
    // v0.1.4 - 11 May 2006 - added option for naming tmr domain report, added Utilization Expansion Factor
    // v0.1.5 - 12 May 2006 - moved some internal classes, removed debug printout in FlattenTMR main
    // v0.1.6 - 18 May 2006 - added option to ignore INOUT port restriction, added partial feedback I/O depth-first options
    // v0.1.7 - 23 May 2006 - added DLLs to resources tracked for Virtex parts, added options for ordering of SCC additions
    // v0.1.8 - 13 Jun 2006 - added triplication status to reports, added available space utilization factor, added force triplicate options, fixed bug in NMRGraphUtilities that could cause a class cast exception
    // v0.1.9 -  3 Jul 2006 - fixed bug causing error in Map (tool now doesn't allow cuts between MUXCYs), changes to EDIF
    // v0.2.0 - 25 Jul 2006 - new command-line argument parser (not backwards compatible), added half-latch removal option, added configuration file support
    // v0.2.1 - 28 Jul 2006 - disallows voting between MUXF5, MUXF6, MUXF7, MUXF8; added unused cell trimming
    // v0.2.2 - 25 Sep 2006 - Now using NMREdifCell (not TMREdifCell); added fmap removal; added Multiple EDIF Creation; hierarchical force/exclude from tmr
    // v0.2.3 - 12 Oct 2006 - Fixed bugs in writeConfig (NullPointerException), NMRGraphUtilities (ConcurrentModificationException), and added inputs to MUXF6 as XilinxBadCutConnection 
    // v0.2.4 - 16 Feb 2007 - Fixed bugs in EdifHalfLatchRemover: added support for BlackBoxes, removed addition of "_hl" suffix; added option to allow user to specify suffixes for replicated design elements; fixed issue with Design name in output file; fixed bug in NMRGraphUtilities; created new HalfLatchFlattenedEdifCell class, tool now flattens before HL removal; HL removal now recognizes IOB registers; Added option to ignore feedback through IOBs; Port components are automatically triplicated/not triplicated with the port; Fixed bug in which bad cuts were possible when using --no_tmr_x options
    // v0.2.5 - 13 Jul 2007 - Added new options for cutset determination (reduces voter count); Fixed bug in which not all instances were guaranteed to have the same triplication status; Added options to force or exclude full clock domains; Added Virtex4 support; Now allows all valid part number constructions

    public static final String VERSION = "0.2.5";

    public static final String RELEASE_DATE = "13 July 2007";

    public static final String REVISION = "$Revision:151 $";

    public static void main(String[] args) throws IOException, OverutilizationException {
        long startTime;
        startTime = System.currentTimeMillis();

        /*
         * 1. Preliminary work (parse arguments, setup log file, print version)
         */
        // 1.a) Parse arguments
        _parser = new TMRCommandParser();
        _commands = _parser.parse(args);

        /*
         * 1.b) Setup log file and print command-line argument information to
         * log file. If the user has activated Multiple EDIF Creation, this step
         * is delayed because a separate log file is created for each iteration.
         */
        if (!_parser.usingMultipleEDIF()) {
            createLogFile(_commands.getString(TMRCommandParser.LOG));
            // 1.c) Print version info
            println(getVersionInfo());
            // Print CVS revision number to log file only
            println(REVISION, true);
        }

        // Set up options for using bad cut connections and port ref graph	
        final boolean useBadCutConn = _commands.getBoolean(TMRCommandParser.USE_BAD_CUT_CONN);
        final boolean HighestFanoutCutset = _commands.getBoolean(TMRCommandParser.HIGHEST_FANOUT_CUTSET);
        final boolean HighestFFFanoutCutset = _commands.getBoolean(TMRCommandParser.HIGHEST_FF_FANOUT_CUTSET);

        // 2. Create TMR architecture
        NMRArchitecture tmrArch = getArchitecture(_commands.getString(TMRCommandParser.PART));

        // 3. Load EDIF and merge multiple EDIF files
        EdifCell cell = XilinxMergeParser.parseAndMergeXilinx(_commands.getString(TMRCommandParser.INPUT_FILE), Arrays
                .asList(_commands.getStringArray(TMRCommandParser.DIR)), Arrays.asList(_commands
                .getStringArray(TMRCommandParser.FILE)), System.out);
        if (reportTiming)
            startTime = reportTime(startTime, "EDIF Parsing");

        /*
         * 4. Check for EdifCell with top-level Inout EdifPorts (We don't handle
         * these for now.) TODO: This will crash if we are using Multiple EDIF
         * Creation.
         */
        // Top-level INOUT ports are handled fine now.
        // TODO: Remove this option from the NMRCommandParser
        //		if (NMRUtilities.cellHasInoutPorts(cell)) {
        //			if (_result.getBoolean(TMRCommandParser.NO_IN_OUT_CHECK)) {
        //				println("WARNING: Your design contains INOUT ports. BL-TMR does not currently support INOUT ports.");
        //			} else {
        //				println("ERROR: BL-TMR does not currently support INOUT ports. Use "
        //						+ TMRCommandParser.NO_IN_OUT_CHECK
        //						+ " to force TMR with INOUT ports.");
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
        if (_debug)
            NMRUtilities.createOutputFile("flat.edf", flatCell);
        if (reportTiming)
            startTime = reportTime(startTime, "Flattening");

        /*
         * 7. Create the cell connectivity data structure Include top-level
         * ports and source-to-source Edges (needed for IOB analysis)
         */
        EdifCellInstanceGraph eciConnectivityGraph = new EdifCellInstanceGraph(flatCell, true, true);
        if (reportTiming)
            startTime = reportTime(startTime, "Connectivity Graph Creation");

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
        if (reportTiming)
            startTime = reportTime(startTime, "IOB Analysis");

        /*
         * 8. Remove half-latches, if desired
         */
        Collection<String> forceInstanceStrings = new ArrayList<String>();
        if (_commands.getBoolean(TMRCommandParser.REMOVE_HL)) {
            //long startTime, elapsedTime;
            //startTime = System.currentTimeMillis();
            println("Removing half-latches...");
            // TODO: Ensure we are using Xilinx (the only supported architecture)
            HalfLatchArchitecture hlArchitecture = null;
            if (packInputRegs || packOutputRegs) {
                // Send the (possibly large) list to the log file only
                println("\tThe following flip-flops were treated as IOB " + "registers during half-latch removal: "
                        + iobAnalyzer.getAllIOBRegisters(), true);
                hlArchitecture = new XilinxHalfLatchArchitecture(flatCell, iobAnalyzer);
            } else {
                hlArchitecture = new XilinxHalfLatchArchitecture(flatCell);
            }

            String hlPortName = _commands.getString(TMRCommandParser.HL_PORT_NAME);
            boolean hlUsePort = false;
            if (hlPortName != null)
                hlUsePort = true;

            // Set up a SequentialEdifHalfLatchRemover (BHP: I'm not sure the other
            //   alternative--Topological...--works anymore)
            EdifHalfLatchRemover edifHalfLatchRemover = new SequentialEdifHalfLatchRemover(hlArchitecture, _commands
                    .getInt(TMRCommandParser.HL_CONSTANT), hlUsePort, hlPortName);
            // Remove Half-latches
            flatCell = edifHalfLatchRemover.removeHalfLatches(flatCell);

            // Force triplication of internal half-latch constant or port ibuf
            EdifCellInstance safeConstantInstance;
            if (hlUsePort)
                safeConstantInstance = ((HalfLatchFlattenedEdifCell) flatCell).getSafeConstantPortBufferInstance();
            else
                safeConstantInstance = ((HalfLatchFlattenedEdifCell) flatCell).getSafeConstantGeneratorCell();
            /*
             * Set up this instance for triplication. Add only the instance name
             * because this instance is at the top level and does not need any
             * hierarchy information.
             */
            forceInstanceStrings.add(safeConstantInstance.getName());

            //elapsedTime = System.currentTimeMillis() - startTime;
            //System.out.println("Half-latch removal took "+NMRUtilities.msToString(elapsedTime));

            /*
             * Since we replaced the flatCell object with the
             * HalfLatchFlattenedEdifCell object, we'll need to re-create the
             * Connectivity graph and the IOBAnalyzer object
             */
            eciConnectivityGraph = new EdifCellInstanceGraph(flatCell, true, true);
            iobAnalyzer = new XilinxVirtexIOBAnalyzer(flatCell, eciConnectivityGraph);
            /*
             * Delete the source-to-source Edges. We don't need them after the
             * IOB analysis
             */
            eciConnectivityGraph.removeSourceToSourceEdges();

            if (_debug)
                NMRUtilities.createOutputFile("hl.edf", flatCell);
            if (reportTiming)
                startTime = reportTime(startTime, "Half-latch removal");
        }

        /*
         * 9. Prune all unused cells in the Library (Left over from flatten and
         * half-latch removal)
         */
        cell.getLibrary().getLibraryManager().pruneNonReferencedCells(flatCell);

        /*
         * TODO: is this different from the calculation on line 489? 9.5. Create
         * BadCutGroupConnectivity to be used with NMRGraphUtilities and
         * PartialTMR
         */
        EdifCellBadCutGroupings badCutGroupings = null;
        EdifCellInstanceCollectionGraph badCutGroupConn = null;
        // Create BadCutGroupings
        badCutGroupings = new EdifCellBadCutGroupings(flatCell, tmrArch, eciConnectivityGraph);
        if (useBadCutConn) {
            // Create Group Connectivity (include top level ports)
            badCutGroupConn = new EdifCellInstanceCollectionGraph(eciConnectivityGraph, badCutGroupings, true);
            if (reportTiming)
                startTime = reportTime(startTime, "Bad Cut Grouping and Connectivity creation");
        }

        double low, high, inc;
        long low_long, high_long, inc_long;
        NMRUtilities.UtilizationFactor factorType = _parser.getFactorType();

        /*
         * If we are using multiple EDIF, set the low, high and increment values
         * as specified on the command-line; otherwise, set them to the value
         * specified on the command line.
         */
        if (_parser.usingMultipleEDIF()) {
            low = _commands.getDouble(TMRCommandParser.LOW);
            high = _commands.getDouble(TMRCommandParser.HIGH);
            inc = _commands.getDouble(TMRCommandParser.INC);
        } else {
            high = low = _commands.getDouble(TMRCommandParser.FACTOR_VALUE);
            // The value of inc shouldn't matter, but just in case...
            inc = 1.0;
        }

        /*
         * Get the design name (that is, the input file name without any suffix)
         * in preparation for Multiple EDIF Creation.
         */
        String design = new File(_commands.getString(TMRCommandParser.INPUT_FILE)).getName();
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
            // Print status info.
            System.out.println("Processing: " + factorType + " " + factorValue);
            /*
             * 10. Create folder and log file for current iteration.
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
                createLogFile(directory.toString() + "/" + design + ".bltmr.log");
                // 1.c) Print version info
                println(getVersionInfo());
                // Print CVS revision number to log file only
                println(REVISION, true);
            }

            /*
             * 11. Create resource tracker.
             */
            DeviceUtilizationTracker duTracker = null;
            try {
                duTracker = DeviceParser.createXilinxDeviceUtilizationTracker(flatCell, _commands
                        .getString(TMRCommandParser.PART), _commands.getDouble(TMRCommandParser.MERGE_FACTOR),
                        _commands.getDouble(TMRCommandParser.OPTIMIZATION_FACTOR), factorValue, factorType);
            } catch (OverutilizationException e) {
                String errmsg = new String("ERROR: Original cell " + flatCell + " could not fit into specified part "
                        + _commands.getString(TMRCommandParser.PART) + "\n." + e);
                throw new OverutilizationException(errmsg);
            }
            if (reportTiming)
                startTime = reportTime(startTime, "Resource Tracker Creation");

            /*
             * 12. Triplicate top-level ports based on command line arguments.
             * This method will exit if all requested ports cannot be
             * triplicated. The IOB instances (regs, resistors, BUFs) will be
             * force-triplicated or force-not-triplicated based on the port's
             * triplication status.
             */
            _portsToTriplicate = triplicatePorts(flatCell, duTracker, iobAnalyzer);

            /*
             * 13. Prevent and force triplication of specific cell types and
             * instances.
             */
            // 13a. Exclude instances, clock domains, and cell types from TMR as requested by user
            println("");
            excludeFromTriplication(flatCell, duTracker);

            /*
             * 13b. Add instances and cell types for which the user wants to
             * force triplication.
             */
            forceTriplication(forceInstanceStrings, flatCell, duTracker);

            if (reportTiming)
                startTime = reportTime(startTime, "Port Triplication and Force Triplication");

            /*
             * 13c. Make sure all instances in the same bad cut group have the
             * same triplication status.
             */
            try {
                unifyBadCutGroups(badCutGroupings, duTracker);
            } catch (IllegalArgumentException e4) {
                System.out.println(e4.getMessage());
                System.exit(1);
            }

            // Begin Feedback and Partial TMR Analysis
            println("");
            println("Analyzing design . . .");

            /*
             * 14. Perform feedback analysis.
             */

            // Create the cell connectivity data structure
            // Generate cutset using the PortRefGraph, if specified
            Collection<EdifPortRef> PRGcuts = null;
            if (HighestFanoutCutset || HighestFFFanoutCutset) {
                System.out.println("Creating Graph . . .");
                EdifOutputPortRefGraph graph = new EdifOutputPortRefGraph(flatCell);

                if (reportTiming)
                    startTime = reportTime(startTime, "EdifOutputPortRefGraph creation");
                // Create SCC
                System.out.println("SCC Decomposition. . .");
                SCCDepthFirstSearch PRGsccDFS = new SCCDepthFirstSearch(graph);
                if (reportTiming)
                    startTime = reportTime(startTime, "EdifOutputPortRefGraph SCC Decomposition");
                // SCC Decomposition
                if (HighestFFFanoutCutset)
                    PRGcuts = NMRGraphUtilities.createDecomposeValidCutSetFFFanout(graph, PRGsccDFS, tmrArch);
                else
                    PRGcuts = NMRGraphUtilities.createDecomposeValidCutSetFanout(graph, PRGsccDFS, tmrArch);
                //System.out.println(cuts.size()+" cuts to disconnect");
                if (reportTiming)
                    startTime = reportTime(startTime, "EdifOutputPortRefGraph cutset determination");
            }

            // BHP: Special hack for designs with CLKDLLs to triplicate. Remove clock dll feedback
            //			Collection<EdifCellInstanceEdge> edgesToRemove = new ArrayList<EdifCellInstanceEdge>();
            //			for (EdifCellInstanceEdge edge : eciConnectivityGraph.getEdges()) {
            //				String sinkPortName = edge.getSinkEPR().getPort().getName();
            //				if (sinkPortName.equals("CLKFB")) {
            //					edgesToRemove.add(edge);
            //					System.out.println("#### Removing edge: "+edge);
            //				}
            //			}
            //			eciConnectivityGraph.removeEdges(edgesToRemove);

            // 14a. Create SCCs.
            // TODO: Move the SCC DFS outside the Multiple EDIF creation loop?
            //   (Does this ever change? Do we modify it in here?)
            SCCDepthFirstSearch sccDFS = null;
            if (useBadCutConn)
                sccDFS = new SCCDepthFirstSearch(badCutGroupConn);
            else
                sccDFS = new SCCDepthFirstSearch(eciConnectivityGraph);
            if (reportTiming)
                startTime = reportTime(startTime, "SCC Partitioning");

            /*
             * 14b. Do IOB feedback analysis here along with re-computation of
             * the SCC DFS if any IOB feedback is cut. Report to the user on the
             * results of the analysis.
             */
            Collection<EdifCellInstanceEdge> possibleIOBFeedbackEdges = iobAnalyzer.getIOBFeedbackEdges();

            // Find the possible feedback edges that are contained in the SCCs
            //   (i.e. those that are contained in feedback)
            Collection<DepthFirstTree> sccs = sccDFS.getTrees();
            // Collect all feedback edges into a single Collection.
            //   Use a HashSet because we are doing multiple look-ups in a large Set
            Collection<Edge> edgesInFeedback = new LinkedHashSet<Edge>();
            Collection<EdifCellInstanceEdge> esslsInFeedback = new LinkedHashSet<EdifCellInstanceEdge>();
            for (DepthFirstTree scc : sccs) {
                for (Edge edge : scc.getEdges()) {
                    if (edge instanceof EdifCellInstanceEdge) {
                        edgesInFeedback.add(edge);
                        esslsInFeedback.add((EdifCellInstanceEdge) edge);
                    } else if (edge instanceof EdifCellInstanceCollectionLink) {
                        edgesInFeedback.add(edge);
                        esslsInFeedback.addAll(((EdifCellInstanceCollectionLink) edge).getLinks());
                    } else {
                        throw new EdifRuntimeException("Unhandled Edge type: " + edge.getClass());
                    }
                }
            }
            // Collect all matching "possible" IOB feedback edges
            Collection<Edge> iobFeedbackEdges = new ArrayList();
            for (EdifCellInstanceEdge possibleIOBFeedbackEdge : possibleIOBFeedbackEdges) {
                if (esslsInFeedback.contains(possibleIOBFeedbackEdge))
                    iobFeedbackEdges.add(possibleIOBFeedbackEdge);
            }
            // If there are any IOB feedback edges:
            // 1. Report to user (warning)
            // 2. Remove these edges from the graph and recompute the SCCs
            //    (If the user has chosen this option)
            if (iobFeedbackEdges.size() > 0) {
                println("");
                if (_commands.getBoolean(TMRCommandParser.NO_IOB_FB)) {
                    // Report the IOB feedback edges found
                    System.out.println("Found IOBs in feedback (see log file for details)");
                    // Send the (possibly large) list to the log file only
                    println("The following IOBs were found in feedback structures: " + iobFeedbackEdges, true);
                    println("\tThese IOBs will be excluded from feedback analysis.");
                    eciConnectivityGraph.removeEdges(iobFeedbackEdges);
                    /*
                     * TODO: Update this to work with BadCutGroupConnectivity
                     * and EdifOutputPortRefGraph
                     */
                    if (useBadCutConn) {
                        // Re-create BadCutGroupings
                        badCutGroupings = new EdifCellBadCutGroupings(flatCell, tmrArch, eciConnectivityGraph);
                        // Re-create Group Connectivity (include top level ports)
                        badCutGroupConn = new EdifCellInstanceCollectionGraph(eciConnectivityGraph, badCutGroupings,
                                true);
                        sccDFS = new SCCDepthFirstSearch(badCutGroupConn);
                    } else {
                        sccDFS = new SCCDepthFirstSearch(eciConnectivityGraph);
                    }
                } else {
                    // Report the IOB feedback edges found
                    System.out.println("WARNING: Found IOBs in feedback (see log file for details)");
                    // Send the (possibly large) list to the log file only
                    println("WARNING: Found the following IOBs in feedback: " + iobFeedbackEdges, true);
                    println("\tUse the \"" + TMRCommandParser.NO_IOB_FB
                            + "\" flag to exclude these IOBs from feedback analysis.");
                }
                println("");
            }
            if (reportTiming)
                startTime = reportTime(startTime, "IOB Analysis");

            // 14c. Perform feedback analysis and determine cut set.
            List<Edge> cutSet = new ArrayList<Edge>();

            boolean allSCCInstancesTriplicated;
            if (_commands.getBoolean(TMRCommandParser.FULL_TMR)
                    || !_commands.getBoolean(TMRCommandParser.NO_TMR_FEEDBACK)) {
                allSCCInstancesTriplicated = NMRGraphUtilities.tmrSCCsUsingSCCDecomposition(sccDFS, tmrArch, duTracker,
                        _commands.getBoolean(TMRCommandParser.DO_SCC_DECOMPOSITION), _commands
                                .getInt(TMRCommandParser.SCC_SORT_TYPE), cutSet);
                // Print results for SCC triplication
                if (!_commands.getBoolean(TMRCommandParser.FULL_TMR)) {
                    if (allSCCInstancesTriplicated)
                        println("\tFeedback section will be fully triplicated");
                    else
                        println("\tFeedback section will be partially triplicated");
                }
            } else {
                /*
                 * This only happens if the user selected the "notmrFeedback"
                 * option, in which no cuts are needed.
                 */
                allSCCInstancesTriplicated = false;
                println("\tFeedback section will not be triplicated");
            }

            if (reportTiming)
                startTime = reportTime(startTime, "Feedback Analysis and Triplication");

            // Insert more voters!
            //cutSet = VoterInsertion.insertVotersByLogicLevels(eciConnectivityGraph, tmrArch, cutSet, 60);
            //if (reportTiming) startTime = reportTime(startTime, "More frequent voter insertion");

            // 15. Do either full TMR or partial TMR
            if (_commands.getBoolean(TMRCommandParser.FULL_TMR)) {
                println("\tFull TMR requested.");
                for (Iterator instancesIterator = flatCell.cellInstanceIterator(); instancesIterator.hasNext();) {
                    EdifCellInstance eci = (EdifCellInstance) instancesIterator.next();
                    try {
                        duTracker.nmrInstance(eci, _replicationFactor);
                    } catch (OverutilizationEstimatedStopException e1) {
                        String errmsg = new String("ERROR: Instance " + eci
                                + " not added to resource tracker. Full TMR will not fit in part "
                                + _commands.getString(TMRCommandParser.PART) + ".\n" + e1);
                        throw new OverutilizationException(errmsg);
                    } catch (OverutilizationHardStopException e2) {
                        println("WARNING: Instance " + eci
                                + " not added to resource tracker due to hard resource constraints in part "
                                + _commands.getString(TMRCommandParser.PART) + ".\n" + e2);
                    } catch (DuplicateNMRRequestException e3) {
                        // TODO: Do we really want to just ignore this?
                        /*
                         * For now we have to ignore because feedback already
                         * added instances to the resource tracker
                         */
                        // throw new EdifRuntimeException(e3.toString());
                    }
                }
            } else {
                // Partial TMR selection
                // SCC Inputs and Outputs plus Feed-Forward section
                int[] triplicationStatus = null;
                triplicationStatus = PartialInputOutputFeedForwardTMR.tmrSCCInputAndOutput(flatCell,
                        eciConnectivityGraph, badCutGroupings, duTracker, tmrArch, sccDFS, !_commands
                                .getBoolean(TMRCommandParser.NO_TMR_INPUT_TO_FEEDBACK), !_commands
                                .getBoolean(TMRCommandParser.NO_TMR_FEEDBACK_OUTPUT), !_commands
                                .getBoolean(TMRCommandParser.NO_TMR_FEED_FORWARD), _commands
                                .getInt(TMRCommandParser.INPUT_ADDITION_TYPE), _commands
                                .getInt(TMRCommandParser.OUTPUT_ADDITION_TYPE));

                // Report on results of triplication
                reportTriplicationStatus(triplicationStatus);
            }
            if (reportTiming)
                startTime = reportTime(startTime, "Full/Partial TMR Analysis");

            // 16. Get the set of cells to triplicate
            Collection<EdifCellInstance> actualCellInstancesToTriplicate = duTracker.getCurrentNMRInstances();

            // DEBUG
            // Collection cellsNotToTriplicate = new
            // ArrayList(flatCell.getSubCellList());
            // cellsNotToTriplicate.removeAll(actualCellInstancesToTriplicate);
            // System.out.println("Instances NOT triplicated: " +
            // cellsNotToTriplicate);
            // System.out.println("Instances triplicated: " +
            // actualCellInstancesToTriplicate);

            /*
             * 17. Determine where to cut the feedback
             */
            Collection<EdifPortRef> portRefsToCut = null;
            if (HighestFanoutCutset || HighestFFFanoutCutset)
                portRefsToCut = PRGcuts;
            else
                portRefsToCut = NMRUtilities.getPortRefsToCutFromEdges(cutSet, eciConnectivityGraph, tmrArch);
            // DEBUG:
            //System.out.println("Cutting "+portRefsToCut.size()+" EdifPortRefs");
            if (reportTiming)
                startTime = reportTime(startTime, "Cutset EPRs from Edges");

            /*
             * 18. Triplicate design, Cut Feedback & Report
             */
            // 18.a) Triplicate & Cut feedback!
            println("");
            println("Triplicating design . . .");

            EdifCell newCell = null;
            int numberOfVoters = 0;
            int numberOfTriplicatedInstances = 0;
            int numberOfTriplicatedNets = 0;
            int numberOfTriplicatedPorts = 0;

            TMREdifCell tmrCell = null;
            try {
                if (_commands.contains(TMRCommandParser.TMR_SUFFIX))
                    // Use user-supplied suffixes
                    tmrCell = new TMREdifCell(cell.getLibrary(), flatCell.getName() + "_TMR", flatCell, tmrArch,
                            _portsToTriplicate, actualCellInstancesToTriplicate, portRefsToCut, _commands
                                    .getStringArray(TMRCommandParser.TMR_SUFFIX));
                else
                    // Use default suffixes
                    tmrCell = new TMREdifCell(cell.getLibrary(), flatCell.getName() + "_TMR", flatCell, tmrArch,
                            _portsToTriplicate, actualCellInstancesToTriplicate, portRefsToCut);
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            }
            numberOfVoters = tmrCell.getVoters().size();
            numberOfTriplicatedInstances = tmrCell.getReplicatedInstances().size();
            numberOfTriplicatedNets = tmrCell.getReplicatedNets().size();
            numberOfTriplicatedPorts = tmrCell.getReplicatedPorts().size();
            newCell = tmrCell;
            if (reportTiming)
                startTime = reportTime(startTime, "TMR");

            // 18.b) Write domain report
            if (_parser.usingMultipleEDIF()) {
                tmrCell.printDomainReport(directory + "/" + directory.getName() + "_domain_report.txt");
            } else {
                tmrCell.printDomainReport(_commands.getString(TMRCommandParser.DOMAIN_REPORT));
            }

            // 18.c) Print summary of TMR performed
            int numberOfOriginalInstances = flatCell.getSubCellList().size();
            println("\tAdded " + numberOfVoters + " voters.");
            println("\t" + numberOfTriplicatedInstances + " instances out of " + numberOfOriginalInstances
                    + " cells triplicated (" + (numberOfTriplicatedInstances * 100 / numberOfOriginalInstances)
                    + "% coverage)");
            println("\t" + 2 * numberOfTriplicatedInstances + " new instances added to design. ");
            println("\t" + numberOfTriplicatedNets + " nets triplicated (" + 2 * numberOfTriplicatedNets
                    + " new nets added).");
            println("\t" + numberOfTriplicatedPorts + " ports triplicated.");

            // 19. Set TMR instance to top cell
            EdifCellInstance tmrInstance = null;
            EdifDesign newDesign = null;
            try {
                tmrInstance = new EdifCellInstance("tmr_" + cell.getName(), null, newCell);
                newDesign = new EdifDesign(newCell.getEdifNameable());
            } catch (InvalidEdifNameException e1) {
                e1.toRuntime();
            }
            newDesign.setTopCellInstance(tmrInstance);
            // copy design properties
            EdifDesign oldDesign = cell.getLibrary().getLibraryManager().getEdifEnvironment().getTopDesign();
            if (oldDesign.getPropertyList() != null) {
                for (Object o : oldDesign.getPropertyList().values()) {
                    Property p = (Property) o;
                    newDesign.addProperty((Property) p.clone());
                }
            }
            cell.getLibrary().getLibraryManager().getEdifEnvironment().setTopDesign(newDesign);

            /*
             * flatCell is no longer needed after tmr, so it deletes itself from
             * the library it belongs to. TODO: this is a test TODO: not sure
             * why this is needed... duTracker should update itself. Right?
             * 
             * instead of deleting flat cell, we need to update the utilization
             * tracker.
             */
            //flatCell.getLibrary().deleteCell(flatCell, true);
            {//scope make duTracker
                try {
                    duTracker = DeviceParser.createXilinxDeviceUtilizationTracker(flatCell, _commands
                            .getString(TMRCommandParser.PART), _commands.getDouble(TMRCommandParser.MERGE_FACTOR),
                            _commands.getDouble(TMRCommandParser.OPTIMIZATION_FACTOR), factorValue, factorType);
                } catch (OverutilizationException e) {
                    String errmsg = new String("ERROR: Original cell " + flatCell
                            + " could not fit into specified part " + _commands.getString(TMRCommandParser.PART)
                            + "\n." + e);
                    throw new OverutilizationException(errmsg);
                }
            }//scope make duTracker

            /*
             * flatCell is no longer needed after tmr, so it deletes itself from
             * the library it belongs to.
             */
            flatCell.getLibrary().deleteCell(flatCell, true);

            // 20. Print Report to stdout if desired; always print to log file
            int tmrPrimitives = EdifUtils.countRecursivePrimitives(newCell);
            int flatPrimitives = EdifUtils.countRecursivePrimitives(flatCell);
            int tmrNets = EdifUtils.countRecursiveNets(newCell);
            int flatNets = EdifUtils.countRecursiveNets(flatCell);
            int tmrPortRefs = EdifUtils.countPortRefs(newCell, true);
            int flatPortRefs = EdifUtils.countPortRefs(flatCell, true);
            StringBuffer summBuf = new StringBuffer();
            summBuf.append("\nTMR circuit contains:\n");
            summBuf.append("\t" + tmrPrimitives + " primitives (" + (100 * (tmrPrimitives - flatPrimitives))
                    / flatPrimitives + "% increase)\n");
            summBuf.append("\t" + tmrNets + " nets (" + (100 * (tmrNets - flatNets)) / flatNets + "% increase)\n");
            summBuf.append("\t" + tmrPortRefs + " net connections (" + (100 * (tmrPortRefs - flatPortRefs))
                    / flatPortRefs + "% increase)\n");
            summBuf.append("\nPost TMR utilization estimate:\n" + duTracker);

            //println(summBuf.toString(), !_result.getBoolean(TMRCommandParser.SUMMARY));
            /*
             * Always print to stdout and log file. BHP: There is so much info
             * printed already, this extra bit shouldn't be offensive.
             */
            // TODO: Remove --summary option from command line
            println(summBuf.toString());

            // 21. Write output file
            String outputFileName = null;
            if (_parser.usingMultipleEDIF()) {
                outputFileName = directory + "/" + directory.getName() + ".edf";
            } else {
                outputFileName = _commands.getString(TMRCommandParser.OUTPUT_FILE);
            }
            NMRUtilities.createOutputFile(outputFileName, newCell);
            println("Wrote output file to " + outputFileName);

            /*
             * 22. Delete tmrCell for further iterations of Multiple EDIF
             * Creation.
             */
            tmrCell.getLibrary().deleteCell(tmrCell, true);

            if (reportTiming)
                startTime = reportTime(startTime, "Everything else");
        }
    }

    /**
     * @return a String of the version and date of this release of the BL-TMR
     * tool.
     */
    public static String getVersionInfo() {
        return "BL-TMR Tool version " + FlattenTMR.VERSION + ", " + FlattenTMR.RELEASE_DATE;
    }

    // ///////////////////////////////////////////////////////////////////
    // Protected Methods

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
     * @param eciConnectivityGraph
     * @param sccDFS
     * @param actualCellInstancesToTriplicate
     */
    protected static int countPersistentFlipFlops(EdifCellInstanceGraph eciConnectivityGraph,
            SCCDepthFirstSearch sccDFS, Collection<EdifCellInstance> actualCellInstancesToTriplicate) {
        //
        // Count the number of persistent flip-flops
        //

        // Get SCC instances
        Collection<EdifCellInstance> persistentECIs = new ArrayList<EdifCellInstance>();
        for (DepthFirstTree scc : sccDFS.getTrees()) {
            for (Object node : scc.getNodes()) {
                if (node instanceof EdifCellInstance) {
                    persistentECIs.add((EdifCellInstance) node);
                }
            }
        }

        // Get input-to-SCC instances
        Collection ancestors = eciConnectivityGraph.getAncestors(persistentECIs);
        for (Object node : ancestors) {
            if (node instanceof EdifCellInstance) {
                persistentECIs.add((EdifCellInstance) node);
            }
        }

        // Count SCC flip-flops
        //int count = EdifTools.countXilinxFlipFlops(sccECIs);
        // Count input-to-SCC flip-flops
        //count += EdifTools.countXilinxFlipFlops(inputECIs);

        // Remove all instances that will be triplicated (these can't be
        //   in persistence!)
        persistentECIs.removeAll(actualCellInstancesToTriplicate);

        // Count the number of flip-flops
        int ffCount = EdifTools.countXilinxFlipFlops(persistentECIs);

        System.out.println("Found " + ffCount + " flip-flops in the persistent sections of the given Edif design.");

        return ffCount;
    }

    /**
     * @param flatCell
     * @param duTracker
     */
    protected static void excludeFromTriplication(EdifCell flatCell, DeviceUtilizationTracker duTracker) {
        Collection<EdifCellInstance> excludeInstances = new ArrayList();
        if (_commands.contains(TMRCommandParser.NO_TMR_I)) {
            for (String instanceName : _commands.getStringArray(TMRCommandParser.NO_TMR_I)) {
                println("Excluding instance " + instanceName + " from TMR");
                // Get Collection of instances
                Collection<FlattenedEdifCellInstance> instances = ((FlattenedEdifCell) flatCell)
                        .getInstancesWithin(instanceName);
                // Give user warning if no match was found
                if (instances == null || instances.isEmpty())
                    println("\tWARNING: No match for instance " + instanceName);
                else
                    excludeInstances.addAll(instances);
            }
        }
        // Get Collection of instances based on cell types to exclude
        if (_commands.contains(TMRCommandParser.NO_TMR_C)) {
            Collection<String> excludeCellTypes = Arrays.asList(_commands.getStringArray(TMRCommandParser.NO_TMR_C));

            for (String excludeCellType : excludeCellTypes) {
                println("Excluding cell type " + excludeCellType + " from TMR");
                // Need to package the excludeCellType String in a Collection
                Collection excludeCellTypeColl = new ArrayList(1);
                excludeCellTypeColl.add(excludeCellType);

                Collection<FlattenedEdifCellInstance> instances = ((FlattenedEdifCell) flatCell)
                        .getInstancesWithinCellTypes(excludeCellTypeColl);
                // Give user warning if no match was found
                if (instances == null || instances.isEmpty())
                    println("\tWARNING: No match for cell type " + excludeCellType);
                else
                    excludeInstances.addAll(instances);
            }
        }

        // Add all instances from specified clock domains to exclusion list
        if (_commands.contains(TMRCommandParser.NO_TMR_CLK)) {
            ClockDomainParser domainParser = null;
            Collection<String> excludeClockDomains = Arrays.asList(_commands
                    .getStringArray(TMRCommandParser.NO_TMR_CLK));
            try {
                domainParser = new ClockDomainParser((FlattenedEdifCell) flatCell);
            }

            catch (InvalidEdifNameException e) {
                e.toRuntime();
            }

            Map<EdifNet, Set<EdifCellInstance>> clockDomainMap = domainParser.getECIMap();
            Set<EdifNet> clocks = clockDomainMap.keySet();

            for (String netName : excludeClockDomains) {
                Iterator i = clocks.iterator();
                Collection<EdifCellInstance> instances = new ArrayList();
                while (i.hasNext()) {
                    EdifNet net = (EdifNet) i.next();
                    if (net.getName().equals(netName)) {
                        println("Excluding clock domain " + netName + " from TMR.");
                        instances.addAll(clockDomainMap.get(net));
                    }
                }
                if (instances.isEmpty())
                    println("\tWARNING: No match for clock domain " + netName);
                else
                    excludeInstances.addAll(instances);
            }

        }

        // Add each instance to the exclude list
        for (EdifCellInstance excludeInstance : excludeInstances) {
            if (_debug)
                System.out.println("Excluding instance " + excludeInstance + " from TMR.");
            duTracker.excludeInstanceFromNMR(excludeInstance);
        }
    }

    /**
     * Start with all the top-level ports of the original cell, remove ports
     * marked to not be triplicated, and return the remaining set of ports,
     * which is the set of ports to be triplicated.
     * 
     * @param cell The original, un-triplicated EdifCell
     * @param tripInputs Should inputs be triplicated?
     * @param tripOutputs Should outputs be triplicated?
     * @param noTriplicate A Collection of String objects of port names that
     * will not be triplicated.
     * @return a Set of EdifPort objects to be triplicated
     */
    protected static Set<EdifPort> filterPortsToTriplicate(EdifCell cell, boolean tripInputs, boolean tripOutputs,
            Collection<String> noTriplicate) {

        Set<EdifPort> ports = new LinkedHashSet<EdifPort>();

        if (!tripInputs && !tripOutputs)
            return ports; // Return empty Set

        println("");
        println("Port Triplication:");
        for (EdifPort port : cell.getPortList()) {
            //System.out.println(port.getClass().getName());
            print("\t" + port);
            if (noTriplicate.contains(port.getName())) {
                println(" : in list to not triplicate.");
                continue;
            }
            if (tripInputs && port.isInput()) {
                println(" : triplicate input port");
                ports.add(port);
            } else if (tripOutputs && port.isOutput()) {
                println(" : triplicate output port");
                ports.add(port);
            } else
                println(" : nothing being done");
        }
        return ports;
    }

    /**
     * Forces certain EdifCellInstances and cell types to be triplicated based
     * on the command line arguments provided by the user. Instance names are
     * case sensitive, cell types are not.
     * 
     * @param forceInstanceStrings
     * @param flatCell
     * @param duTracker
     */
    protected static void forceTriplication(Collection<String> forceInstanceStrings, EdifCell flatCell,
            DeviceUtilizationTracker duTracker) {
        Collection<EdifCellInstance> forceInstances = new ArrayList();
        // Add Strings from command line to forceInstance Collection
        if (_commands.contains(TMRCommandParser.TMR_I))
            forceInstanceStrings.addAll(Arrays.asList(_commands.getStringArray(TMRCommandParser.TMR_I)));
        // Get EdifCellInstances that correspond to these Strings
        for (String instanceName : forceInstanceStrings) {
            println("Forcing triplication of instance " + instanceName);
            // Get Collection of instances
            Collection<FlattenedEdifCellInstance> instances = ((FlattenedEdifCell) flatCell)
                    .getInstancesWithin(instanceName);
            // Give user warning if no match was found
            if (instances == null || instances.isEmpty())
                println("\tWARNING: No match for instance " + instanceName);
            else
                forceInstances.addAll(instances);
        }
        // Get Collection of instances based on cell types to exclude
        if (_commands.contains(TMRCommandParser.TMR_C)) {
            Collection<String> forceCellTypes = Arrays.asList(_commands.getStringArray(TMRCommandParser.TMR_C));
            for (String forceCellType : forceCellTypes) {
                println("Forcing triplication of cell type " + forceCellType);
                // Need to package the forceCellType String in a Collection
                Collection forceCellTypeColl = new ArrayList(1);
                forceCellTypeColl.add(forceCellType);

                Collection<FlattenedEdifCellInstance> instances = ((FlattenedEdifCell) flatCell)
                        .getInstancesWithinCellTypes(forceCellTypes);
                // Give user warning if no match was found
                if (instances == null || instances.isEmpty())
                    println("\tWARNING: No match for cell type " + forceCellType);
                else
                    forceInstances.addAll(instances);
            }
        }

        // Add all instances from specified clock domains to forced triplication list
        if (_commands.contains(TMRCommandParser.TMR_CLK)) {
            ClockDomainParser domainParser = null;
            Collection<String> forceClockDomains = Arrays.asList(_commands.getStringArray(TMRCommandParser.TMR_CLK));
            try {
                domainParser = new ClockDomainParser((FlattenedEdifCell) flatCell);
            }

            catch (InvalidEdifNameException e) {
                e.toRuntime();
            }

            Map<EdifNet, Set<EdifCellInstance>> clockDomainMap = domainParser.getECIMap();
            Set<EdifNet> clocks = clockDomainMap.keySet();

            for (String netName : forceClockDomains) {
                Iterator i = clocks.iterator();
                Collection<EdifCellInstance> instances = new ArrayList();
                while (i.hasNext()) {
                    EdifNet net = (EdifNet) i.next();
                    if (net.getName().equals(netName)) {
                        println("Forcing triplication of clock domain " + netName);
                        instances.addAll(clockDomainMap.get(net));
                    }
                }
                if (instances.isEmpty())
                    println("\tWARNING: No match for clock domain " + netName);
                else
                    forceInstances.addAll(instances);
            }

        }

        // Triplicate each of these EdifCellInstances
        for (EdifCellInstance forceInstance : forceInstances) {
            try {
                duTracker.nmrInstance(forceInstance, _replicationFactor);
                if (_debug)
                    System.out.println("Forcing triplication of instance: " + forceInstance);
            } catch (DuplicateNMRRequestException e1) {
                // Already TMR'd
            } catch (OverutilizationEstimatedStopException e2) {
                /*
                 * This instance will not fit in the device. (Device is full)
                 */
                println("WARNING: Could not add instance " + forceInstance + ". Device full.");
            } catch (OverutilizationHardStopException e3) {
                // There are no more resources available for this instance.
                println("WARNING: Could not add instance " + forceInstance + ". No resources of type "
                        + forceInstance.getType() + " available.");
            }
        }
    }

    /**
     * Return a TMRArchitecture object for the specified part.
     * 
     * @param partString The specified part
     * @return A TMRArchtecture object
     */
    protected static XilinxTMRArchitecture getArchitecture(String partString) {
        String technologyString = XilinxPartValidator.getTechnologyFromPart(partString);
        if (technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX)
                || technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX2)
                || technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX4))
            return new XilinxTMRArchitecture();
        throw new EdifRuntimeException("Invalid Technology: " + technologyString + ". Valid technologies include "
                + NMRUtilities.VIRTEX + ", " + NMRUtilities.VIRTEX2 + ", and " + NMRUtilities.VIRTEX4 + ".");
    }

    /**
     * Print a description of the command-line arguments used to the log file.
     */
    protected static void logArgs() {
        _log.println("Tool Options Used:");
        _log.println("\tInput file: " + _commands.getString(TMRCommandParser.INPUT_FILE));
        _log.println("\tOutput file: " + _commands.getString(TMRCommandParser.OUTPUT_FILE));
        if (_commands.contains(TMRCommandParser.TMR_C))
            _log.println("\tForcing triplicating of cell types: "
                    + Arrays.asList(_commands.getStringArray(TMRCommandParser.TMR_C)));
        if (_commands.contains(TMRCommandParser.TMR_I))
            _log.println("\tForcing triplicating of cell instances: "
                    + Arrays.asList(_commands.getStringArray(TMRCommandParser.TMR_I)));
        if (_commands.contains(TMRCommandParser.NO_TMR_C))
            _log.println("\tNot triplicating cell types: "
                    + Arrays.asList(_commands.getStringArray(TMRCommandParser.NO_TMR_C)));
        if (_commands.contains(TMRCommandParser.NO_TMR_I))
            _log.println("\tNot triplicating cell instances: "
                    + Arrays.asList(_commands.getStringArray(TMRCommandParser.NO_TMR_I)));
        if (_commands.getBoolean(TMRCommandParser.TMR_INPORTS))
            _log.println("\tTriplicating input ports");
        if (_commands.getBoolean(TMRCommandParser.TMR_OUTPORTS))
            _log.println("\tTriplicating output ports");
        if (_commands.contains(TMRCommandParser.NO_TMR_P))
            _log.println("\tNot triplicating ports: "
                    + Arrays.asList(_commands.getStringArray(TMRCommandParser.NO_TMR_P)));
        if (_commands.getBoolean(TMRCommandParser.NO_IOB_FB))
            _log.println("\tFeedback through IOBs will be ignored.");
        if (_commands.getBoolean(TMRCommandParser.FULL_TMR))
            _log.println("\tPerforming full TMR. Skipping partial TMR evaluation.");
        else {
            _log.println("\tPartial TMR options:");
            if (_commands.getBoolean(TMRCommandParser.NO_TMR_FEEDBACK))
                _log.println("\t\tExcluding feedback structures from triplication.");
            if (_commands.getBoolean(TMRCommandParser.NO_TMR_INPUT_TO_FEEDBACK))
                _log.println("\t\tExcluding logic driving feedback structures from triplication.");
            if (_commands.getBoolean(TMRCommandParser.NO_TMR_FEEDBACK_OUTPUT))
                _log.println("\t\tExcluding logic driven by feedback structures from triplication.");
            if (_commands.getBoolean(TMRCommandParser.NO_TMR_FEED_FORWARD))
                _log.println("\t\tExcluding feed forward logic from triplication.");
            _log.println("\t\tInput Addition Type: " + _commands.getInt(TMRCommandParser.INPUT_ADDITION_TYPE));
            _log.println("\t\tOutput Addition Type: " + _commands.getInt(TMRCommandParser.OUTPUT_ADDITION_TYPE));
            _log.println("\t\tSCC Sort Type: " + _commands.getInt(TMRCommandParser.SCC_SORT_TYPE));
            if (_commands.getBoolean(TMRCommandParser.DO_SCC_DECOMPOSITION))
                _log.println("\t\tWill decompose strongly-connected components (SCCs)");
            _log.println("\t\tMerge Factor: " + _commands.getDouble(TMRCommandParser.MERGE_FACTOR));
            _log.println("\t\tOptimization Factor: " + _commands.getDouble(TMRCommandParser.OPTIMIZATION_FACTOR));

            // Print utilization factor type and value
            UtilizationFactor type = _parser.getFactorType();
            String verboseFactorType = NMRUtilities.getVerboseUtilizationFactor(type);
            String factorValue = ((Double) _commands.getDouble(TMRCommandParser.FACTOR_VALUE)).toString();
            _log.println("\t\t" + verboseFactorType + ": " + factorValue);

        }
        if (_commands.getBoolean(TMRCommandParser.REMOVE_HL)) {
            _log.println("\tHalf-latch removal options:");
            _log.println("\t\tHalf-latch constant: " + _commands.getInt(TMRCommandParser.HL_CONSTANT));
            if (_commands.userSpecified(TMRCommandParser.HL_PORT_NAME))
                _log.println("\t\tHalf-latch constant port: " + _commands.getString(TMRCommandParser.HL_PORT_NAME));
            if (_parser.packInputRegisters() && _parser.packOutputRegisters())
                _log.println("\t\tBoth input and output flip-flops will be treated as IOB registers");
            else if (_parser.packInputRegisters())
                _log.println("\t\tInput flip-flops will be treated as IOB registers");
            else if (_parser.packOutputRegisters())
                _log.println("\t\tOutput flip-flops will be treated as IOB registers");
            else
                _log.println("\t\tNo flip-flops will be treated as IOB registers");
        }

        if (_commands.getBoolean(TMRCommandParser.NO_IN_OUT_CHECK))
            //_log.println("\tIgnoring restriction on designs with INOUT ports.");
            _log.println("\tNote: The " + TMRCommandParser.NO_IN_OUT_CHECK + " option is no longer necessary.");

        _log.println("\tPart: " + _commands.getString(TMRCommandParser.PART));
        _log.println("\tLog file: " + _commands.getString(TMRCommandParser.LOG));
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

    /**
     * @param triplicationStatus An array of int's describing the level of
     * triplication (none, some, or all) for each section of the circuit
     */
    protected static void reportTriplicationStatus(int[] triplicationStatus) {
        // Input to Feedback
        switch (triplicationStatus[PartialInputOutputFeedForwardTMR.INPUT_TO_FEEDBACK]) {
        case PartialInputOutputFeedForwardTMR.NONE:
            println("\tInput to Feedback section will not be triplicated");
            break;
        case PartialInputOutputFeedForwardTMR.SOME:
            println("\tInput to Feedback section will be partially triplicated");
            break;
        case PartialInputOutputFeedForwardTMR.ALL:
            println("\tInput to Feedback section will be fully triplicated");
            break;
        }
        // Feedback Output
        switch (triplicationStatus[PartialInputOutputFeedForwardTMR.FEEDBACK_OUTPUT]) {
        case PartialInputOutputFeedForwardTMR.NONE:
            println("\tFeedback Output section will not be triplicated");
            break;
        case PartialInputOutputFeedForwardTMR.SOME:
            println("\tFeedback Output section will be partially triplicated");
            break;
        case PartialInputOutputFeedForwardTMR.ALL:
            println("\tFeedback Output section will be fully triplicated");
            break;
        }
        // Feed Forward
        switch (triplicationStatus[PartialInputOutputFeedForwardTMR.FEED_FORWARD]) {
        case PartialInputOutputFeedForwardTMR.NONE:
            println("\tFeed Forward section will not be triplicated");
            break;
        case PartialInputOutputFeedForwardTMR.SOME:
            println("\tFeed Forward section will be partially triplicated");
            break;
        case PartialInputOutputFeedForwardTMR.ALL:
            println("\tFeed Forward section will be fully triplicated");
            break;
        }
    }

    /**
     * Adds the chosen top-level ports and associated BUF (IOB) instances to the
     * DeviceUtilizationTracker object given. The FlattenTMRArguments object is
     * used to determine which ports to triplicate and the EdifCellInstanceGraph
     * object is used to find the connected BUF Instances, if any.
     * 
     * @param flatCell
     * @param duTracker
     * @param graph
     * @return {@link #_portsToTriplicate}
     */
    protected static Set<EdifPort> triplicatePorts(EdifCell flatCell, DeviceUtilizationTracker duTracker,
            AbstractIOBAnalyzer iobAnalyzer) {
        // Determine which ports to triplicate
        // *** TODO: This needs to be moved!
        // Identify BOARD specific ports to triplicate
        _noTriplicatePorts = new LinkedHashSet<String>();
        for (String port : NMRUtilities.SLAAC1V_PORTS_NOT_TO_REPLICATE)
            _noTriplicatePorts.add(port);

        _noTriplicatePorts.addAll(Arrays.asList(_commands.getStringArray(TMRCommandParser.NO_TMR_P)));

        // Filter the set of ports to triplicate
        _portsToTriplicate = filterPortsToTriplicate(flatCell, _commands.getBoolean(TMRCommandParser.TMR_INPORTS),
                _commands.getBoolean(TMRCommandParser.TMR_OUTPORTS), _noTriplicatePorts);
        // Create list of ports NOT to triplicate
        _portsNotToTriplicate = flatCell.getPortList();
        _portsNotToTriplicate.removeAll(_portsToTriplicate);

        // Add ports for triplication (with associated IBUF/OBUF instances)
        // Use the IOB Analyzer to find the BUFs and other IOB instances
        _iobInstancesToTriplicate = new LinkedHashSet();
        for (EdifPort port : _portsToTriplicate) {
            _iobInstancesToTriplicate.addAll(iobAnalyzer.getIOBInstances(port.getSingleBitPortList()));
        }

        try {
            duTracker.nmrInstancesAtomic(_iobInstancesToTriplicate, _replicationFactor);
        } catch (DuplicateNMRRequestException e1) {
            // Already TMR'd
            System.out.println("WARNING: Duplicate TMR Port request. Should not get here: " + e1);
        } catch (OverutilizationEstimatedStopException e2) {
            // DeviceUtilizationTracker says to stop adding instances for tmr
            System.out.println("WARNING: Device full when adding Ports. Should not get here. " + e2);
        } catch (OverutilizationHardStopException e3) {
            // Hit some hard limit.
            System.out.println("ERROR: Could not triplicate " + _portsToTriplicate.size() + " top-level ports"
                    + " due to resource constraints: " + _portsToTriplicate);
            System.exit(1);
        }

        // Register the port IOB instances to skip with the DeviceUtilizationTracker
        _iobInstancesNotToTriplicate = new LinkedHashSet();
        for (EdifPort port : _portsNotToTriplicate) {
            _iobInstancesNotToTriplicate.addAll(iobAnalyzer.getIOBInstances(port.getSingleBitPortList()));
        }
        for (EdifCellInstance eci : _iobInstancesNotToTriplicate) {
            duTracker.excludeInstanceFromNMR(eci);
        }

        return _portsToTriplicate;
    }

    /**
     * Adds the chosen top-level ports and associated BUF (IOB) instances to the
     * DeviceUtilizationTracker object given. The FlattenTMRArguments object is
     * used to determine which ports to triplicate and the EdifCellInstanceGraph
     * object is used to find the connected BUF Instances, if any.
     * 
     * @param flatCell
     * @param duTracker
     * @param graph
     * @return {@link #_portsToTriplicate}
     */
    protected static Set<EdifPort> triplicatePorts_old(EdifCell flatCell, DeviceUtilizationTracker duTracker,
            EdifCellInstanceGraph graph) {
        // Determine which ports to triplicate
        // *** TODO: This needs to be moved!
        // Identify BOARD specific ports to triplicate
        _noTriplicatePorts = new LinkedHashSet<String>();
        for (String port : NMRUtilities.SLAAC1V_PORTS_NOT_TO_REPLICATE)
            _noTriplicatePorts.add(port);

        _noTriplicatePorts.addAll(Arrays.asList(_commands.getStringArray(TMRCommandParser.NO_TMR_P)));

        // Filter the set of ports to triplicate
        _portsToTriplicate = filterPortsToTriplicate(flatCell, _commands.getBoolean(TMRCommandParser.TMR_INPORTS),
                _commands.getBoolean(TMRCommandParser.TMR_OUTPORTS), _noTriplicatePorts);
        // Create list of ports NOT to triplicate
        _portsNotToTriplicate = flatCell.getPortList();
        _portsNotToTriplicate.removeAll(_portsToTriplicate);

        // Add ports for triplication (with associated IBUF/OBUF instances)
        // Use the EdifCellInstanceGraph graph to find the BUFs
        _iobInstancesToTriplicate = NMRUtilities.getPortBufs(_portsToTriplicate, graph);
        try {
            duTracker.nmrInstancesAtomic(_iobInstancesToTriplicate, _replicationFactor);
        } catch (DuplicateNMRRequestException e1) {
            // Already TMR'd
            System.out.println("WARNING: Duplicate TMR Port request. Should not get here: " + e1);
        } catch (OverutilizationEstimatedStopException e2) {
            // DeviceUtilizationTracker says to stop adding instances for tmr
            System.out.println("WARNING: Device full when adding Ports. Should not get here. " + e2);
        } catch (OverutilizationHardStopException e3) {
            // Hit some hard limit.
            System.out.println("ERROR: Could not triplicate " + _portsToTriplicate.size() + " top-level ports"
                    + " due to resource constraints: " + _portsToTriplicate);
            System.exit(1);
        }

        // Register the port BUFs to skip with the DeviceUtilizationTracker
        _iobInstancesNotToTriplicate = NMRUtilities.getPortBufs(_portsNotToTriplicate, graph);
        for (EdifCellInstance eci : _iobInstancesNotToTriplicate) {
            duTracker.excludeInstanceFromNMR(eci);
        }

        return _portsToTriplicate;
    }

    // /////////////////////////////////////////////////////////////
    // Protected Members

    /**
     * Makes sure that the ECIs within a BadCutGroup all have the same
     * triplication status.
     * 
     * @author Derrick Gibelyou
     * @param badCutGroupings
     * @param duTracker
     * @throws DuplicateNMRRequestException
     */
    protected static void unifyBadCutGroups(EdifCellBadCutGroupings badCutGroupings, DeviceUtilizationTracker duTracker)
            throws IllegalArgumentException {
        /*
         * make sure that the badcut groups all have the same triplication
         * status
         */

        Iterator<EdifCellInstanceCollection> bc_it = badCutGroupings.getInstanceGroups().iterator();
        EdifCellInstanceCollection badCuteci;
        Collection<EdifCellInstance> trip_eci = duTracker.getCurrentNMRInstances();
        //EdifCellInstanceCollection ex_eci = duTracker.
        /*
         * Walk through all the collections. If there is more than one cell in
         * the collection make sure they all have the same triplication status.
         */
        while (bc_it.hasNext()) {
            badCuteci = bc_it.next();
            int i = badCuteci.size();
            if (i > 1) {
                if (_debug)
                    println("starting new Group");
                Collection<EdifCellInstance> excluded = new ArrayList();
                Collection<EdifCellInstance> included = new ArrayList();
                Iterator<EdifCellInstance> eci_it = badCuteci.iterator();
                while (eci_it.hasNext()) {
                    EdifCellInstance eci = eci_it.next();
                    if (((AbstractDeviceUtilizationTracker) duTracker).isExcludedFromNMR(eci)) {
                        if (_debug)
                            println("\t" + eci + "is excluded");
                        excluded.add(eci);
                    }
                    if (trip_eci.contains(eci)) {
                        if (_debug)
                            println("\t" + eci + "is included");
                        included.add(eci);
                    }
                }
                //user asked for conflicting triplication status in same bad cut group
                if (!excluded.isEmpty() && !included.isEmpty()) {
                    Iterator<EdifCellInstance> it = excluded.iterator();
                    String error = "\nExcluded Cell(s): \n";
                    while (it.hasNext()) {
                        error = error + "\t" + it.next().toString() + "\n";
                    }

                    it = included.iterator();
                    error += "\nIncluded Cell(s): \n";
                    while (it.hasNext()) {
                        error = error + "\t" + it.next().toString() + "\n";
                    }

                    throw new IllegalArgumentException("\n\nCan't force triplication and "
                            + "force exclusion in same slice:" + error);
                }
                //exclude the whole group
                if (!excluded.isEmpty()) {
                    System.out.println("Excluding all cells in bad cut " + "group. See log for details");
                    eci_it = badCuteci.iterator();
                    while (eci_it.hasNext()) {
                        EdifCellInstance eci = eci_it.next();
                        duTracker.excludeInstanceFromNMR(eci);
                        println("\talso excluding: " + eci, !_debug);
                    }
                }
                //include the whole group
                if (!included.isEmpty()) {
                    System.out.println("Including all cells in bad cut " + "group. See log for details");
                    eci_it = badCuteci.iterator();
                    EdifCellInstance forceInstance;
                    while (eci_it.hasNext()) {
                        forceInstance = eci_it.next();
                        try {
                            duTracker.nmrInstance(forceInstance, _replicationFactor);
                            println("Forcing triplication of bad cut group instance: " + forceInstance, !_debug);
                        } catch (DuplicateNMRRequestException e1) {
                            // Already TMR'd
                        } catch (OverutilizationEstimatedStopException e2) {
                            /*
                             * This instance will not fit in the device. (Device
                             * is full)
                             */
                            println("WARNING: Could not add instance " + forceInstance + ". Device full.");
                        } catch (OverutilizationHardStopException e3) {
                            // There are no more resources available for this instance.
                            println("WARNING: Could not add instance " + forceInstance + ". No resources of type "
                                    + forceInstance.getType() + " available.");
                        }
                    }// end iteration loop
                }// end include whole group

            }//end if( more than one in group)
        }//end top while loop
    }

    /**
     * Set of EdifCellInstance objects to be triplicated
     */
    protected static Set<EdifCellInstance> _iobInstancesToTriplicate;

    /**
     * Set of EdifCellInstance objects that should <i>not</i> be triplicated
     */
    protected static Set<EdifCellInstance> _iobInstancesNotToTriplicate;

    /**
     * PrintStream for logfile
     */

    protected static PrintStream _log;

    /**
     * Set of String names of ports that should <i>not</i> be triplicated
     */
    protected static Set<String> _noTriplicatePorts;

    /**
     * TMRCommandParser used to parse the command-line arguments of FlattenTMR.
     */
    protected static TMRCommandParser _parser;

    /**
     * Set of EdifPort objects to be triplicated.
     */
    protected static Set<EdifPort> _portsToTriplicate;

    /**
     * Collection of EdifPort objects to <i>not</i> be triplicated
     */
    protected static Collection<EdifPort> _portsNotToTriplicate;

    /**
     * JSAPResult used to access the command-line arguments after being parsed.
     */
    protected static JSAPResult _commands;

    /**
     * @param startTime
     * @return
     */
    private static long reportTime(long startTime, String description) {
        long elapsedTime;
        elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("TIMESTAMP: " + description + " took " + NMRUtilities.msToString(elapsedTime));
        startTime = System.currentTimeMillis();
        return startTime;
    }

    /**
     * Use to enable or disable debugging print statements
     */
    private static final boolean _debug = false;

    private static final boolean reportTiming = false;

    /**
     * Use to indicate triplication
     */
    private static final int _replicationFactor = 3;

}
