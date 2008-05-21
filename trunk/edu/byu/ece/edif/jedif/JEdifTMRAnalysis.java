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
package edu.byu.ece.edif.jedif;

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

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCellInstance;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.PartialReplicationDescription;
import edu.byu.ece.edif.tools.replicate.PartialReplicationStringDescription;
import edu.byu.ece.edif.tools.replicate.Replication;
import edu.byu.ece.edif.tools.replicate.ReplicationException;
import edu.byu.ece.edif.tools.replicate.Triplication;
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
import edu.byu.ece.edif.tools.replicate.nmr.tmr.PartialInputOutputFeedForwardTMR;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.XilinxTMRArchitecture;
import edu.byu.ece.edif.util.graph.EdifCellBadCutGroupings;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollection;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollectionGraph;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollectionLink;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.graph.EdifPortRefEdge;
import edu.byu.ece.edif.util.iob.AbstractIOBAnalyzer;
import edu.byu.ece.edif.util.iob.XilinxVirtexIOBAnalyzer;
import edu.byu.ece.edif.util.jsap.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.EDIFMain;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.MergeParserCommandGroup;
import edu.byu.ece.edif.util.jsap.OutputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.SharedIOBAnalysis;
import edu.byu.ece.edif.util.jsap.TMRCommandParser;
import edu.byu.ece.edif.util.jsap.TechnologyCommandGroup;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.dfs.DepthFirstTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

public class JEdifTMRAnalysis extends EDIFMain {
    public static void main(String args[]) {

        // Define the print streams for this program
        out = System.out;
        err = System.out;

        // Print executable heading
        EXECUTABLE_NAME = "JEdifTMRAnalysis";
        TOOL_SUMMARY_STRING = "Determines which ports and instances in the design will be triplicated.";
        printProgramExecutableString(out);

        // Parse command line options
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new JEdifParserCommandGroup());
        parser.addCommands(new OutputFileCommandGroup());
        parser.addCommands(new JEdifTMRAnalysisCommandGroup());
        parser.addCommands(new TechnologyCommandGroup());
        parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));
        parser.addCommands(new LogFileCommandGroup("JEdifTMRAnalysis.log"));
        JSAPResult result = parser.parse(args, err);
        if (!result.success())
            System.exit(1);

        //TODO: add logfile functionality
        LogFileCommandGroup.CreateLog(result);
        out = LogFile.out();
        err = LogFile.err();

        printProgramExecutableString(LogFile.log());

        // Step 1: Get EdifEnvironment from JEdif File
        EdifEnvironment env = JEdifParserCommandGroup.getEdifEnvironment(result, out);
        if (env == null)
            err.println("Invalid .jedif file.");
        JEdifQuery.printEdifStats(env, out);
        out.println("Processing: " + JEdifTMRAnalysisCommandGroup.getFactorType(result) + " "
                + JEdifTMRAnalysisCommandGroup.getFactorValue(result));
        EdifCell flatCell = env.getTopCell();

        TechnologyCommandGroup.getPartFromEDIF(result, env);

        PartialReplicationDescription ptmrd = tmr(env, flatCell, result);

        // Step 8: Make and serialize PartialTMRStringDescription into output file
        makeOutputFile(ptmrd);

        // Create IOB Analysis output file
        if (!result.userSpecified(JEdifTMRAnalysisCommandGroup.IOB_OUTPUT_FILE)) {
            String name = JEdifParserCommandGroup.getInputFileName(result);
            name = name.substring(0, name.lastIndexOf('.'));
            OutputFileCommandGroup.serializeObject(out, name + ".iob", siob);
        } else {
            OutputFileCommandGroup.serializeObject(out, JEdifTMRAnalysisCommandGroup.getIOBName(result), siob);
        }

    }

    public static SharedIOBAnalysis getSharedIOB() {
        return siob;
    }

    public static PartialReplicationDescription tmr(EdifEnvironment env, EdifCell flatCell, JSAPResult result) {
        global_result = result;
        debug = LogFile.debug();
        out = LogFile.out();
        err = LogFile.warn();
        iobFeedbackEdges = new ArrayList<EdifPortRefEdge>();

        NMRArchitecture tmrArch = getArchitecture(TechnologyCommandGroup.getTech(global_result));
        out.println("Using part " + env.getTopDesign().getProperty("PART").getValue());

        //Step 2: Make resource tracker & add the flattened top cell to it

        DeviceUtilizationTracker duTracker = null;

        try {
            duTracker = DeviceParser.createXilinxDeviceUtilizationTracker(flatCell, TechnologyCommandGroup
                    .getPart(result), JEdifTMRAnalysisCommandGroup.getMergeFactor(result), JEdifTMRAnalysisCommandGroup
                    .getOptimizationFactor(result), JEdifTMRAnalysisCommandGroup.getFactorValue(result),
                    JEdifTMRAnalysisCommandGroup.ignoreHardResourceUtilizationLimits(result),
                    JEdifTMRAnalysisCommandGroup.ignoreSoftLogicUtilizationLimit(result), JEdifTMRAnalysisCommandGroup
                            .getFactorType(result));
        } catch (OverutilizationException e) {
            String errmsg = new String("ERROR: Original cell " + flatCell + " could not fit into specified part "
                    + TechnologyCommandGroup.getPart(result) + "\n." + e);
            err.println(errmsg);
            System.exit(1);
        }

        out.println("Utilization before Triplication: \n" + duTracker);
        // Step 3: Account for ports/instances to triplicate/exclude from TMR	
        // Set up necessary data structures
        EdifCellInstanceGraph eciConnectivityGraph = new EdifCellInstanceGraph(flatCell, true, true);
        AbstractIOBAnalyzer iobAnalyzer = new XilinxVirtexIOBAnalyzer((FlattenedEdifCell) flatCell,
                eciConnectivityGraph);

        // Delete the source-to-source Edges. We don't need them after the
        // IOB analysis
        eciConnectivityGraph.removeSourceToSourceEdges();

        // Triplicate ports and force triplicate/exclude from triplication
        _portsToTriplicate = triplicatePorts(flatCell, duTracker, iobAnalyzer);
        excludeFromTriplication(flatCell, duTracker, env);
        forceTriplication(flatCell, duTracker, env);

        // Create BadCutGroupings
        EdifCellBadCutGroupings badCutGroupings = null;
        badCutGroupings = new EdifCellBadCutGroupings(flatCell, tmrArch, eciConnectivityGraph);
        // Unify the bad cut groupings
        try {
            unifyBadCutGroups(badCutGroupings, duTracker);
        } catch (IllegalArgumentException e4) {
            System.out.println(e4.getMessage());
            System.exit(1);
        }

        // Step 4a (if fullTMR): Determine full TMR
        boolean success = true;
        if (JEdifTMRAnalysisCommandGroup.fullTMR(result))
            success = fullTMR(duTracker, flatCell);
        else
            // Step 4b (if !fullTMR): determine partial TMR
            partialTMR(duTracker, flatCell, eciConnectivityGraph, badCutGroupings, iobAnalyzer, tmrArch);
        if (!success)
            System.exit(1);

        // Make the SharedIOBAnalysis object
        boolean noIOBFB = JEdifTMRAnalysisCommandGroup.noIOBFB(result);
        siob = new SharedIOBAnalysis(iobFeedbackEdges, noIOBFB);

        // Step 5: Print out TMR statistics from the Resource Tracker
        printTMRstats(duTracker, flatCell);
        // Step 6: Get the lists of instances/ports to TMR from resource tracker
        Collection<EdifCellInstance> actualCellInstancesToTriplicate = duTracker.getCurrentNMRInstances();
        // Step 7: Make PartialTMRDescription from lists of instances/ports to TMR
        Collection<Replication> replications = new ArrayList<Replication>();
        try {
            for (EdifCellInstance eci : actualCellInstancesToTriplicate) {
                replications.add(new Triplication(eci));
            }
            for (EdifPort port : _portsToTriplicate) {
                replications.add(new Triplication(port));
            }
        } catch (ReplicationException e) {
            e.toRuntime();
        }
        PartialReplicationDescription ptmrd = new PartialReplicationDescription();
        ptmrd.cellToReplicate = flatCell;
        ptmrd.addAll(replications);
        ptmrd.portRefsToCut = null;
        return ptmrd;
    }

    /**
     * Performs partial TMR analysis on the design.
     * 
     * @param logger
     * @param duTracker
     * @param flatCell
     * @param eciConnectivityGraph
     * @param badCutGroupings
     * @param iobAnalyzer
     * @param tmrArch
     */
    protected static void partialTMR(DeviceUtilizationTracker duTracker, EdifCell flatCell,
            EdifCellInstanceGraph eciConnectivityGraph, EdifCellBadCutGroupings badCutGroupings,
            AbstractIOBAnalyzer iobAnalyzer, NMRArchitecture tmrArch) {
        // Make the bad cut groupings, etc.
        boolean useBadCutConn = JEdifTMRAnalysisCommandGroup.badCutConn(global_result);

        EdifCellInstanceCollectionGraph badCutGroupConn = null;

        if (useBadCutConn) {
            // Create Group Connectivity (include top level ports)
            badCutGroupConn = new EdifCellInstanceCollectionGraph(eciConnectivityGraph, badCutGroupings, true);
        }

        // Create SCCs
        SCCDepthFirstSearch sccDFS = null;
        if (useBadCutConn)
            sccDFS = new SCCDepthFirstSearch(badCutGroupConn);
        else
            sccDFS = new SCCDepthFirstSearch(eciConnectivityGraph);

        // Perform IOB Feedback Analysis - leads to an output file for JEdifCutset
        iobFeedbackAnalysis(iobAnalyzer, eciConnectivityGraph, sccDFS, useBadCutConn, badCutGroupings, badCutGroupConn,
                tmrArch, flatCell);

        out.println("");
        out.println("Analyzing design . . .");

        // Determine TMR of feedback section
        boolean allSCCInstancesTriplicated;
        // TODO: cutSet will not be needed in this executable - remove and change NMRGraphUtilities?
        List<Edge> cutSet = new ArrayList<Edge>();
        if (!JEdifTMRAnalysisCommandGroup.noTMRFeedback(global_result)) {
            allSCCInstancesTriplicated = NMRGraphUtilities.tmrSCCsUsingSCCDecomposition(sccDFS, tmrArch, duTracker,
                    JEdifTMRAnalysisCommandGroup.doSCCDecomposition(global_result), JEdifTMRAnalysisCommandGroup
                            .getSCCSortType(global_result), cutSet);

            // Print results for SCC triplication
            if (allSCCInstancesTriplicated)
                out.println("\tFeedback section will be fully triplicated");
            else
                out.println("\tFeedback section will be partially triplicated");
        }

        else {
            /*
             * This only happens if the user selected the "notmrFeedback"
             * option, in which no cuts are needed.
             */
            allSCCInstancesTriplicated = false;
            out.println("\tFeedback section will not be triplicated");
        }

        // Partial TMR selection
        // SCC Inputs and Outputs plus Feed-Forward section
        int[] triplicationStatus = null;
        triplicationStatus = PartialInputOutputFeedForwardTMR.tmrSCCInputAndOutput(flatCell, eciConnectivityGraph,
                badCutGroupings, duTracker, tmrArch, sccDFS, !JEdifTMRAnalysisCommandGroup
                        .noTMRinputToFeedback(global_result), !JEdifTMRAnalysisCommandGroup
                        .noTMRfeedbackOutput(global_result), !JEdifTMRAnalysisCommandGroup
                        .noTMRfeedForward(global_result), JEdifTMRAnalysisCommandGroup
                        .getInputAdditionType(global_result), JEdifTMRAnalysisCommandGroup
                        .getOutputAdditionType(global_result));

        // Report on results of triplication
        reportTriplicationStatus(triplicationStatus);
    }

    private static void iobFeedbackAnalysis(AbstractIOBAnalyzer iobAnalyzer,
            EdifCellInstanceGraph eciConnectivityGraph, SCCDepthFirstSearch sccDFS, boolean useBadCutConn,
            EdifCellBadCutGroupings badCutGroupings, EdifCellInstanceCollectionGraph badCutGroupConn,
            NMRArchitecture tmrArch, EdifCell flatCell) {
        boolean noIOBFB = JEdifTMRAnalysisCommandGroup.noIOBFB(global_result);
        out.println("Performing IOB Feedback Analysis");
        //14b. Do IOB feedback analysis here along with recomputation
        //   of the SCC DFS if any IOB feedback is cut. Report to the user
        //   on the results of the analysis.
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
        for (EdifCellInstanceEdge possibleIOBFeedbackEdge : possibleIOBFeedbackEdges) {
            if (esslsInFeedback.contains(possibleIOBFeedbackEdge))
                iobFeedbackEdges.add(possibleIOBFeedbackEdge);
        }
        // If there are any IOB feedback edges:
        // 1. Report to user (warning)
        // 2. Remove these edges from the graph and recompute the SCCs
        //    (If the user has chosen this option)
        if (iobFeedbackEdges.size() > 0) {
            // TODO: put in logging functionality with other executables' style
            out.println("");
            if (noIOBFB) {
                // Report the IOB feedback edges found
                out.println("Found IOBs in feedback (see log file for details)");
                // Send the (possibly large) list to the log file only
                LogFile.log().println("The following IOBs were found in feedback structures: " + iobFeedbackEdges);
                out.println("\tThese IOBs will be excluded from feedback analysis.");
                eciConnectivityGraph.removeEdges(iobFeedbackEdges);
                // TODO: Update this to work with BadCutGroupConnectivity and
                //   EdifOutputPortRefGraph
                if (useBadCutConn) {
                    // Re-create BadCutGroupings
                    badCutGroupings = new EdifCellBadCutGroupings(flatCell, tmrArch, eciConnectivityGraph);
                    // Re-create Group Connectivity (include top level ports)
                    badCutGroupConn = new EdifCellInstanceCollectionGraph(eciConnectivityGraph, badCutGroupings, true);
                    sccDFS = new SCCDepthFirstSearch(badCutGroupConn);
                } else {
                    sccDFS = new SCCDepthFirstSearch(eciConnectivityGraph);
                }
            } else {
                // Report the IOB feedback edges found
                System.out.println("WARNING: Found IOBs in feedback (see log file for details)");
                // Send the (possibly large) list to the log file only
                LogFile.log().println("WARNING: Found the following IOBs in feedback: " + iobFeedbackEdges);
                out.println("\tUse the \"" + TMRCommandParser.NO_IOB_FB
                        + "\" flag to exclude these IOBs from feedback analysis.");
            }

        }

        //        String output_filename = JEdifTMRAnalysisCommandGroup.getIOBOutputFilename(result);
        //        out.print("Creating file " + output_filename + " . . .");
        //        FileOutputStream fos = null;
        //        ObjectOutputStream out_object = null;
        //        try {
        //            fos = new FileOutputStream(output_filename);
        //            out_object = new ObjectOutputStream(fos);
        //            out_object.writeObject(siob);
        //            out_object.close();
        //        } catch (IOException ex) {
        //            ex.printStackTrace();
        //        }
        //        out.println("Done");

    }

    /**
     * Performs full TMR analysis on the design.
     * 
     * @param duTracker The device utilization tracker
     * @param flatCell The flattened EDIFCell object
     * @return true if operation was successful, false otherwise.
     */
    protected static boolean fullTMR(DeviceUtilizationTracker duTracker, EdifCell flatCell) {
        out.println("Full TMR requested.");
        for (Iterator instancesIterator = flatCell.cellInstanceIterator(); instancesIterator.hasNext();) {
            EdifCellInstance eci = (EdifCellInstance) instancesIterator.next();
            try {
                duTracker.nmrInstance(eci, _replicationFactor);
            } catch (OverutilizationEstimatedStopException e1) {
                String errmsg = new String("ERROR: Instance " + eci
                        + " not added to resource tracker. Full TMR will not fit in part "
                        + TechnologyCommandGroup.getPart(global_result) + ".\n" + e1);
                err.println(errmsg);
                return false;
            } catch (OverutilizationHardStopException e2) {
                out.println("WARNING: Instance " + eci
                        + " not added to resource tracker due to hard resource constraints in part "
                        + TechnologyCommandGroup.getPart(global_result) + ".\n" + e2);
            } catch (DuplicateNMRRequestException e3) {
                // Ignore - likely it has been force triplicated already
            }
        }
        //out.println (duTracker);
        return true;
    }

    /**
     * Marks specified ports for triplication. IOB instances will be force
     * triplicated or force not triplicated based on the triplication status of
     * their associated ports.
     * 
     * @param duTracker The device utilization tracker
     * @param flatCell The flattened EDIFCell object
     * @return Set of all EdifPorts in the design to be triplicated.
     */
    protected static Set<EdifPort> triplicatePorts(EdifCell flatCell, DeviceUtilizationTracker duTracker,
            AbstractIOBAnalyzer iobAnalyzer) {
        // add IOBAnalyzer to parameters? 
        // Determine which ports to triplicate
        // Identify BOARD specific ports to triplicate
        _noTriplicatePorts = new LinkedHashSet<String>();
        for (String port : NMRUtilities.SLAAC1V_PORTS_NOT_TO_REPLICATE)
            _noTriplicatePorts.add(port);

        _noTriplicatePorts.addAll(Arrays.asList(JEdifTMRAnalysisCommandGroup.getNoTMRp(global_result)));

        // Filter the set of ports to triplicate
        _portsToTriplicate = filterPortsToTriplicate(flatCell, JEdifTMRAnalysisCommandGroup.tmrInports(global_result),
                JEdifTMRAnalysisCommandGroup.tmrOutports(global_result), JEdifTMRAnalysisCommandGroup
                        .tmrPorts(global_result), _noTriplicatePorts);
        // Create list of ports NOT to triplicate
        _portsNotToTriplicate = flatCell.getPortList();
        _portsNotToTriplicate.removeAll(_portsToTriplicate);

        // Add ports for triplication (with associated IBUF/OBUF instances)
        // Use the IOB Analyzer to find the BUFs and other IOB instances
        Set<EdifCellInstance> _iobInstancesToTriplicate = new LinkedHashSet<EdifCellInstance>();
        for (EdifPort port : _portsToTriplicate) {
            _iobInstancesToTriplicate.addAll(iobAnalyzer.getIOBInstances(port.getSingleBitPortList()));
        }

        try {
            duTracker.nmrInstances(_iobInstancesToTriplicate, _replicationFactor);
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
        Set<EdifCellInstance> _iobInstancesNotToTriplicate = new LinkedHashSet<EdifCellInstance>();
        for (EdifPort port : _portsNotToTriplicate) {
            _iobInstancesNotToTriplicate.addAll(iobAnalyzer.getIOBInstances(port.getSingleBitPortList()));
        }
        for (EdifCellInstance eci : _iobInstancesNotToTriplicate) {
            duTracker.excludeInstanceFromNMR(eci);
            debug.println("Excluding instance for iob register packing: " + eci);
        }
        return _portsToTriplicate;
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
            Collection<String> noTriplicate, Collection<String> triplicate) {

        Set<EdifPort> ports = new LinkedHashSet<EdifPort>();

        if (!tripInputs && !tripOutputs)
            return ports; // Return empty Set

        out.println("");
        out.println("Port Triplication:");
        for (EdifPort port : cell.getPortList()) {
            //System.out.println(port.getClass().getName());
            out.print("\t" + port);
            if (noTriplicate.contains(port.getName())) {
                out.println(" : in list to not triplicate.");
                continue;
            }
            if (triplicate.contains(port.getName())) {
                String dir;
                if (port.isInput())
                    dir = "in";
                else
                    dir = "out";

                out.println(" : triplicate " + dir + "put port as requested");
                ports.add(port);
                continue;
            }
            if (tripInputs && port.isInput()) {
                out.println(" : triplicate input port");
                ports.add(port);
            } else if (tripOutputs && port.isOutput()) {
                out.println(" : triplicate output port");
                ports.add(port);
            } else
                out.println(" : nothing being done");
        }
        return ports;
    }

    /**
     * @param flatCell
     * @param duTracker
     */
    protected static void excludeFromTriplication(EdifCell flatCell, DeviceUtilizationTracker duTracker,
            EdifEnvironment env) {
        Collection<EdifCellInstance> excludeInstances = new ArrayList<EdifCellInstance>();
        if (JEdifTMRAnalysisCommandGroup.noTMRi(global_result)) {
            for (String instanceName : JEdifTMRAnalysisCommandGroup.getNoTMRi(global_result)) {
                out.println("Excluding instance " + instanceName + " from TMR");
                // Get Collection of instances
                Collection<FlattenedEdifCellInstance> instances = ((FlattenedEdifCell) flatCell)
                        .getInstancesWithin(instanceName);
                // Give user warning if no match was found
                if (instances == null || instances.isEmpty())
                    out.println("\tWARNING: No match for instance " + instanceName);
                else
                    excludeInstances.addAll(instances);
            }
        }
        // Get Collection of instances based on cell types to exclude
        if (JEdifTMRAnalysisCommandGroup.noTMRc(global_result)) {
            Collection<String> excludeCellTypes = Arrays.asList(JEdifTMRAnalysisCommandGroup.getNoTMRc(global_result));

            for (String excludeCellType : excludeCellTypes) {
                out.println("Excluding cell type " + excludeCellType + " from TMR");
                // Need to package the excludeCellType String in a Collection
                Collection<String> excludeCellTypeColl = new ArrayList<String>(1);
                excludeCellTypeColl.add(excludeCellType);

                Collection<FlattenedEdifCellInstance> instances = ((FlattenedEdifCell) flatCell)
                        .getInstancesWithinCellTypes(excludeCellTypeColl);
                // Give user warning if no match was found
                if (instances == null || instances.isEmpty())
                    out.println("\tWARNING: No match for cell type " + excludeCellType);
                else
                    excludeInstances.addAll(instances);
            }
        }

        // Add all instances from specified clock domains to exclusion list
        if (JEdifTMRAnalysisCommandGroup.noTMRclk(global_result)) {
            Collection<String> excludeClockDomains = Arrays.asList(JEdifTMRAnalysisCommandGroup
                    .getNoTMRclk(global_result));

            JEdifClockDomain domainParser = new JEdifClockDomain(env);
            Map<EdifNet, Set<EdifCellInstance>> clockDomainMap = domainParser.getECIMap();
            Set<EdifNet> clocks = clockDomainMap.keySet();

            for (String netName : excludeClockDomains) {
                Iterator i = clocks.iterator();
                Collection<EdifCellInstance> instances = new ArrayList<EdifCellInstance>();
                while (i.hasNext()) {
                    EdifNet net = (EdifNet) i.next();
                    if (net.getName().equals(netName)) {
                        out.println("Excluding clock domain " + netName + " from TMR.");
                        instances.addAll(clockDomainMap.get(net));
                    }
                }
                if (instances.isEmpty())
                    out.println("\tWARNING: No match for clock domain " + netName);
                else
                    excludeInstances.addAll(instances);
            }

        }

        // Add each instance to the exclude list
        for (EdifCellInstance excludeInstance : excludeInstances) {
            duTracker.excludeInstanceFromNMR(excludeInstance);
        }
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
    protected static void forceTriplication(EdifCell flatCell, DeviceUtilizationTracker duTracker, EdifEnvironment env) {
        Collection<String> forceInstanceStrings = new ArrayList<String>();
        Collection<EdifCellInstance> forceInstances = new ArrayList<EdifCellInstance>();
        // Add Strings from command line to forceInstance Collection
        if (JEdifTMRAnalysisCommandGroup.tmrI(global_result))
            forceInstanceStrings.addAll(Arrays.asList(JEdifTMRAnalysisCommandGroup.getTMRi(global_result)));
        // Get EdifCellInstances that correspond to these Strings
        for (String instanceName : forceInstanceStrings) {
            out.println("Forcing triplication of instance " + instanceName);
            // Get Collection of instances
            Collection<FlattenedEdifCellInstance> instances = ((FlattenedEdifCell) flatCell)
                    .getInstancesWithin(instanceName);
            // Give user warning if no match was found
            if (instances == null || instances.isEmpty())
                out.println("\tWARNING: No match for instance " + instanceName);
            else
                forceInstances.addAll(instances);
        }
        // Get Collection of instances based on cell types to exclude
        if (JEdifTMRAnalysisCommandGroup.tmrC(global_result)) {
            Collection<String> forceCellTypes = Arrays.asList(JEdifTMRAnalysisCommandGroup.getTMRc(global_result));
            for (String forceCellType : forceCellTypes) {
                out.println("Forcing triplication of cell type " + forceCellType);
                // Need to package the forceCellType String in a Collection
                Collection<String> forceCellTypeColl = new ArrayList<String>(1);
                forceCellTypeColl.add(forceCellType);

                Collection<FlattenedEdifCellInstance> instances = ((FlattenedEdifCell) flatCell)
                        .getInstancesWithinCellTypes(forceCellTypes);
                // Give user warning if no match was found
                if (instances == null || instances.isEmpty())
                    out.println("\tWARNING: No match for cell type " + forceCellType);
                else
                    forceInstances.addAll(instances);
            }
        }

        // Add all instances from specified clock domains to forced triplication list
        if (JEdifTMRAnalysisCommandGroup.tmrClk(global_result)) {
            Collection<String> forceClockDomains = Arrays.asList(JEdifTMRAnalysisCommandGroup.getTMRclk(global_result));

            JEdifClockDomain domainParser = new JEdifClockDomain(env);

            Map<EdifNet, Set<EdifCellInstance>> clockDomainMap = domainParser.getECIMap();
            Set<EdifNet> clocks = clockDomainMap.keySet();

            for (String netName : forceClockDomains) {
                Iterator i = clocks.iterator();
                Collection<EdifCellInstance> instances = new ArrayList<EdifCellInstance>();
                while (i.hasNext()) {
                    EdifNet net = (EdifNet) i.next();
                    if (net.getName().equals(netName)) {
                        out.println("Forcing triplication of clock domain " + netName);
                        instances.addAll(clockDomainMap.get(net));
                    }
                }
                if (instances.isEmpty())
                    out.println("\tWARNING: No match for clock domain " + netName);
                else
                    forceInstances.addAll(instances);
            }

        }

        // Triplicate each of these EdifCellInstances
        for (EdifCellInstance forceInstance : forceInstances) {
            try {
                duTracker.nmrInstance(forceInstance, _replicationFactor);
            } catch (DuplicateNMRRequestException e1) {
                // Already TMR'd
            } catch (OverutilizationEstimatedStopException e2) {
                /*
                 * This instance will not fit in the device. (Device is full)
                 */
                out.println("WARNING: Could not add instance " + forceInstance + ". Device full.");
            } catch (OverutilizationHardStopException e3) {
                // There are no more resources available for this instance.
                out.println("WARNING: Could not add instance " + forceInstance + ". No resources of type "
                        + forceInstance.getType() + " available.");
            }
        }
    }

    /**
     * Return a TMRArchitecture object for the specified technology.
     * 
     * @param technologyString The specified technology
     * @return A TMRArchtecture object
     */
    protected static XilinxTMRArchitecture getArchitecture(String technologyString) {
        if (technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX)
                || technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX2PRO)
                || technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX2)
                || technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX4))
            return new XilinxTMRArchitecture();

        throw new EdifRuntimeException("Invalid Technology: " + technologyString + ". Valid technologies include "
                + NMRUtilities.VIRTEX + ", " + NMRUtilities.VIRTEX2 + ", and " + NMRUtilities.VIRTEX2PRO + ", and "
                + NMRUtilities.VIRTEX4 + ".");
    }

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
        // walk through all the collections.
        // if there is more than one cell in the collection make sure they all have the 
        // same triplication status.
        while (bc_it.hasNext()) {
            badCuteci = bc_it.next();
            int i = badCuteci.size();
            if (i > 1) {
                Collection<EdifCellInstance> excluded = new ArrayList<EdifCellInstance>();
                Collection<EdifCellInstance> included = new ArrayList<EdifCellInstance>();
                Iterator<EdifCellInstance> eci_it = badCuteci.iterator();
                while (eci_it.hasNext()) {
                    EdifCellInstance eci = eci_it.next();
                    if (((AbstractDeviceUtilizationTracker) duTracker).isExcludedFromNMR(eci)) {
                        excluded.add(eci);
                    }
                    if (trip_eci.contains(eci)) {
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
                    LogFile.out().println("Excluding all cells in bad cut " + "group. See log for details");
                    //logger.println(excluded.toString(),LogFile.LOG);
                    // Previously-excluded
                    LogFile.log().println("User requested exclusion of instances: " + excluded);
                    // Newly-added:
                    Collection newExclusions = new ArrayList(badCuteci);
                    newExclusions.removeAll(excluded);
                    LogFile.log().println("The following instances will also be excluded: " + newExclusions);
                    eci_it = badCuteci.iterator();
                    while (eci_it.hasNext()) {
                        EdifCellInstance eci = eci_it.next();
                        duTracker.excludeInstanceFromNMR(eci);
                    }
                }
                //include the whole group
                if (!included.isEmpty()) {
                    LogFile.out().println("Including all cells in bad cut " + "group. See log for details");
                    LogFile.log().println(included.toString());
                    eci_it = badCuteci.iterator();
                    EdifCellInstance forceInstance;
                    while (eci_it.hasNext()) {
                        forceInstance = eci_it.next();
                        try {
                            duTracker.nmrInstance(forceInstance, _replicationFactor);
                        } catch (DuplicateNMRRequestException e1) {
                            // Already TMR'd
                        } catch (OverutilizationEstimatedStopException e2) {
                            /*
                             * This instance will not fit in the device. (Device
                             * is full)
                             */
                            LogFile.out()
                                    .println("WARNING: Could not add instance " + forceInstance + ". Device full.");
                        } catch (OverutilizationHardStopException e3) {
                            // There are no more resources available for this instance.
                            LogFile.out().println(
                                    "WARNING: Could not add instance " + forceInstance + ". No resources of type "
                                            + forceInstance.getType() + " available.");
                        }
                    }// end iteration loop
                }// end include whole group

            }//end if( more than one in group)
        }//end top while loop
    }

    /**
     * @param triplicationStatus An array of int's describing the level of
     * triplication (none, some, or all) for each section of the circuit
     */
    // TODO: print out percentages and split into persistent and non-persistent   
    protected static void reportTriplicationStatus(int[] triplicationStatus) {
        // Input to Feedback
        switch (triplicationStatus[PartialInputOutputFeedForwardTMR.INPUT_TO_FEEDBACK]) {
        case PartialInputOutputFeedForwardTMR.NONE:
            out.println("\tInput to Feedback section will not be triplicated");
            break;
        case PartialInputOutputFeedForwardTMR.SOME:
            out.println("\tInput to Feedback section will be partially triplicated");
            break;
        case PartialInputOutputFeedForwardTMR.ALL:
            out.println("\tInput to Feedback section will be fully triplicated");
            break;
        }
        // Feedback Output
        switch (triplicationStatus[PartialInputOutputFeedForwardTMR.FEEDBACK_OUTPUT]) {
        case PartialInputOutputFeedForwardTMR.NONE:
            out.println("\tFeedback Output section will not be triplicated");
            break;
        case PartialInputOutputFeedForwardTMR.SOME:
            out.println("\tFeedback Output section will be partially triplicated");
            break;
        case PartialInputOutputFeedForwardTMR.ALL:
            out.println("\tFeedback Output section will be fully triplicated");
            break;
        }
        // Feed Forward
        switch (triplicationStatus[PartialInputOutputFeedForwardTMR.FEED_FORWARD]) {
        case PartialInputOutputFeedForwardTMR.NONE:
            out.println("\tFeed Forward section will not be triplicated");
            break;
        case PartialInputOutputFeedForwardTMR.SOME:
            out.println("\tFeed Forward section will be partially triplicated");
            break;
        case PartialInputOutputFeedForwardTMR.ALL:
            out.println("\tFeed Forward section will be fully triplicated");
            break;
        }
    }

    /**
     * Prints out useful statistics about the projected TMR to be done
     */
    protected static void printTMRstats(DeviceUtilizationTracker duTracker, EdifCell flatCell) {
        //TODO: we can probably print more useful stats than this
        //how to best do it with only the resource tracker and flat cell?
        out.println();
        out.println("Estimated utilization (after triplication, before adding voters):");
        out.println(duTracker.toString());
    }

    /**
     * Creates a serialized PartialTMRStringDescription file.
     * 
     * @param ptmrd
     */
    private static void makeOutputFile(PartialReplicationDescription ptmrd) {
        PartialReplicationStringDescription stringDesc = null;
        try {
            stringDesc = new PartialReplicationStringDescription(ptmrd);
        } catch (ReplicationException e) {
            e.toRuntime();
        }
        if (!global_result.userSpecified(OutputFileCommandGroup.OUTPUT_OPTION)) {
            String name = MergeParserCommandGroup.getInputFileName(global_result);
            name = name.substring(0, name.lastIndexOf('.'));
            OutputFileCommandGroup.serializeObject(out, name + ".ptmr", stringDesc);
        } else {
            OutputFileCommandGroup.serializeObject(out, global_result, stringDesc);
        }

    }

    /**
     * Set of String names of ports that should <i>not</i> be triplicated
     */
    protected static Set<String> _noTriplicatePorts;

    /**
     * Set of EdifPort objects to be triplicated.
     */
    protected static Set<EdifPort> _portsToTriplicate;

    /**
     * Collection of EdifPort objects to <i>not</i> be triplicated
     */
    protected static Collection<EdifPort> _portsNotToTriplicate;

    protected static JSAPResult global_result;

    protected static PrintStream out;

    protected static PrintStream debug;

    protected static PrintStream err;

    protected static SharedIOBAnalysis siob;

    protected static List<EdifPortRefEdge> iobFeedbackEdges;

    protected static final int _replicationFactor = 3;
}
