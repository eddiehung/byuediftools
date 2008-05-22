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
import edu.byu.ece.edif.tools.replicate.Duplication;
import edu.byu.ece.edif.tools.replicate.Feedback;
import edu.byu.ece.edif.tools.replicate.PartialReplicationDescription;
import edu.byu.ece.edif.tools.replicate.PartialReplicationStringDescription;
import edu.byu.ece.edif.tools.replicate.Replication;
import edu.byu.ece.edif.tools.replicate.ReplicationException;
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
import edu.byu.ece.edif.tools.replicate.nmr.dwc.XilinxDWCArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.PartialInputOutputFeedForwardTMR;
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
import edu.byu.ece.edif.util.jsap.TechnologyCommandGroup;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.dfs.DepthFirstTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

public class JEdifDWCAnalysis extends EDIFMain {
    public static void main(String args[]) {

        // Define the print streams for this program
        out = System.out;
        err = System.out;

        // Print executable heading
        EXECUTABLE_NAME = "JEdifDWCAnalysis";
        TOOL_SUMMARY_STRING = "Determines which ports and instances in the design will be duplicated.";
        printProgramExecutableString(out);

        // Parse command line options
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new JEdifParserCommandGroup());
        parser.addCommands(new OutputFileCommandGroup());
        parser.addCommands(new JEdifDWCAnalysisCommandGroup());
        parser.addCommands(new TechnologyCommandGroup());
        parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));
        parser.addCommands(new LogFileCommandGroup("JEdifDWCAnalysis.log"));
        JSAPResult result = parser.parse(args, err);
        if (!result.success())
            System.exit(1);

        LogFileCommandGroup.CreateLog(result);
        out = LogFile.out();
        err = LogFile.err();

        printProgramExecutableString(LogFile.log());

        // Step 1: Get EdifEnvironment from JEdif File
        EdifEnvironment env = JEdifParserCommandGroup.getEdifEnvironment(result, out);
        if (env == null)
            err.println("Invalid .jedif file.");
        JEdifQuery.printEdifStats(env, out);
        out.println("Processing: " + JEdifDWCAnalysisCommandGroup.getFactorType(result) + " "
                + JEdifDWCAnalysisCommandGroup.getFactorValue(result));
        EdifCell flatCell = env.getTopCell();

        TechnologyCommandGroup.getPartFromEDIF(result, env);

        PartialReplicationDescription ptmrd = null;

        try {
            ptmrd = dwc(env, flatCell, result);
            // Step 8: Make and serialize PartialReplicationStringDescription into output file
            makeOutputFile(ptmrd);
        } catch (ReplicationException e) {
            e.toRuntime();
        }

        // Create IOB Analysis output file
        if (!result.userSpecified(JEdifDWCAnalysisCommandGroup.IOB_OUTPUT_FILE)) {
            String name = JEdifParserCommandGroup.getInputFileName(result);
            name = name.substring(0, name.lastIndexOf('.'));
            OutputFileCommandGroup.serializeObject(out, name + ".iob", siob);
        } else {
            OutputFileCommandGroup.serializeObject(out, JEdifDWCAnalysisCommandGroup.getIOBName(result), siob);
        }

    }

    public static SharedIOBAnalysis getSharedIOB() {
        return siob;
    }

    public static PartialReplicationDescription dwc(EdifEnvironment env, EdifCell flatCell, JSAPResult result)
            throws ReplicationException {
        global_result = result;
        out = LogFile.out();
        err = LogFile.out();
        iobFeedbackEdges = new ArrayList<EdifPortRefEdge>();

        NMRArchitecture dwcArch = getArchitecture(TechnologyCommandGroup.getTech(global_result));
        out.println("Using part " + env.getTopDesign().getProperty("PART").getValue());

        //Step 2: Make resource tracker & add the flattened top cell to it

        DeviceUtilizationTracker duTracker = null;

        try {
            duTracker = DeviceParser.createXilinxDeviceUtilizationTracker(flatCell, TechnologyCommandGroup
                    .getPart(result), JEdifDWCAnalysisCommandGroup.getMergeFactor(result), JEdifDWCAnalysisCommandGroup
                    .getOptimizationFactor(result), JEdifDWCAnalysisCommandGroup.getFactorValue(result),
                    JEdifTMRAnalysisCommandGroup.ignoreHardResourceUtilizationLimits(result),
                    JEdifTMRAnalysisCommandGroup.ignoreSoftLogicUtilizationLimit(result), JEdifDWCAnalysisCommandGroup
                            .getFactorType(result));
        } catch (OverutilizationException e) {
            String errmsg = new String("ERROR: Original cell " + flatCell + " could not fit into specified part "
                    + TechnologyCommandGroup.getPart(result) + "\n." + e);
            err.println(errmsg);
            System.exit(1);
        }
        // Step 3: Account for ports/instances to triplicate/exclude from DWC	
        // Set up necessary data structures
        EdifCellInstanceGraph eciConnectivityGraph = new EdifCellInstanceGraph(flatCell, true, true);
        AbstractIOBAnalyzer iobAnalyzer = new XilinxVirtexIOBAnalyzer((FlattenedEdifCell) flatCell,
                eciConnectivityGraph);

        // Delete the source-to-source Edges. We don't need them after the
        // IOB analysis
        eciConnectivityGraph.removeSourceToSourceEdges();

        // Triplicate ports and force triplicate/exclude from triplication
        _portsToDuplicate = duplicatePorts(flatCell, duTracker, iobAnalyzer);
        excludeFromDuplication(flatCell, duTracker, env);
        forceDuplication(flatCell, duTracker, env);

        // Create BadCutGroupings
        EdifCellBadCutGroupings badCutGroupings = null;
        badCutGroupings = new EdifCellBadCutGroupings(flatCell, dwcArch, eciConnectivityGraph);
        // Unify the bad cut groupings
        try {
            unifyBadCutGroups(badCutGroupings, duTracker);
        } catch (IllegalArgumentException e4) {
            System.out.println(e4.getMessage());
            System.exit(1);
        }

        // Make the bad cut groupings, etc.
        boolean useBadCutConn = JEdifDWCAnalysisCommandGroup.badCutConn(global_result);

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
                dwcArch, flatCell);

        // Step 4a (if fullDWC): Determine full DWC
        boolean success = true;
        if (JEdifDWCAnalysisCommandGroup.fullDWC(result))
            success = fullDWC(duTracker, flatCell);
        else
            // Step 4b (if !fullDWC): determine partial DWC
            partialDWC(duTracker, flatCell, eciConnectivityGraph, badCutGroupings, sccDFS, dwcArch);
        if (!success)
            System.exit(1);

        // Make the SharedIOBAnalysis object
        boolean noIOBFB = JEdifDWCAnalysisCommandGroup.noIOBFB(result);
        siob = new SharedIOBAnalysis(iobFeedbackEdges, noIOBFB);

        // Step 5: Print out DWC statistics from the Resource Tracker
        printDWCstats(duTracker, flatCell);
        // Step 6: Get the lists of instances/ports to TMR from resource tracker
        Collection<EdifCellInstance> actualCellInstancesToDuplicate = duTracker.getCurrentNMRInstances();
        // Step 7: Make PartialReplicationDescription from lists of instances/ports to duplicate
        PartialReplicationDescription ptmrd = new PartialReplicationDescription();
        ptmrd.cellToReplicate = flatCell;
        for (EdifPort port : _portsToDuplicate)
            ptmrd.add(new Duplication(port));
        for (EdifCellInstance eci : actualCellInstancesToDuplicate) {
            ptmrd.add(new Duplication(eci));
        }
        ptmrd.portRefsToCut = null;
        // Create List of all instances in persistent sections of the design
        Collection<DepthFirstTree> trees = sccDFS.getTrees();
        Set<EdifCellInstance> feedbackPlusInput = new LinkedHashSet<EdifCellInstance>();
        for (DepthFirstTree tree : trees) {
            for (Object o : tree.getNodes()) {
                if (o instanceof EdifCellInstance) {
                    feedbackPlusInput.add((EdifCellInstance) o);
                }
            }
        }
        Collection<EdifCellInstance> ancestors = eciConnectivityGraph.getAncestors(feedbackPlusInput);
        feedbackPlusInput.addAll(ancestors);
        for (Object a : ancestors) {
            if (!(a instanceof EdifCellInstance))
                feedbackPlusInput.remove(a);
        }
        Collection<Replication> feedbackPlusInputReplications = new ArrayList<Replication>();
        for (EdifCellInstance eci : feedbackPlusInput) {
            feedbackPlusInputReplications.add(new Feedback(eci));
        }
        ptmrd.addAll(feedbackPlusInputReplications);
        return ptmrd;
    }

    /**
     * Performs partial DWC analysis on the design.
     * 
     * @param logger
     * @param duTracker
     * @param flatCell
     * @param eciConnectivityGraph
     * @param badCutGroupings
     * @param iobAnalyzer
     * @param tmrArch
     */
    protected static void partialDWC(DeviceUtilizationTracker duTracker, EdifCell flatCell,
            EdifCellInstanceGraph eciConnectivityGraph, EdifCellBadCutGroupings badCutGroupings,
            SCCDepthFirstSearch sccDFS, NMRArchitecture dwcArch) {

        out.println("");
        out.println("Analyzing design . . .");

        // Determine DWC of feedback section
        boolean allSCCInstancesDuplicated;
        // TODO: cutSet will not be needed in this executable - remove and change NMRGraphUtilities?
        List<Edge> cutSet = new ArrayList<Edge>();
        if (!JEdifDWCAnalysisCommandGroup.noDWCFeedback(global_result)) {
            allSCCInstancesDuplicated = NMRGraphUtilities.nmrSCCsUsingSCCDecomposition(sccDFS, dwcArch, duTracker,
                    JEdifDWCAnalysisCommandGroup.doSCCDecomposition(global_result), JEdifDWCAnalysisCommandGroup
                            .getSCCSortType(global_result), cutSet, 2);

            // Print results for SCC triplication
            if (allSCCInstancesDuplicated)
                out.println("\tFeedback section will be fully duplicated");
            else
                out.println("\tFeedback section will be partially duplicated");
        }

        else {
            /*
             * This only happens if the user selected the "noDWCFeedback"
             * option, in which no cuts are needed.
             */
            allSCCInstancesDuplicated = false;
            out.println("\tFeedback section will not be duplicated");
        }

        // Partial DWC selection
        // SCC Inputs and Outputs plus Feed-Forward section
        int[] duplicationStatus = null;
        duplicationStatus = PartialInputOutputFeedForwardTMR.tmrSCCInputAndOutput(flatCell, eciConnectivityGraph,
                badCutGroupings, duTracker, dwcArch, sccDFS, !JEdifDWCAnalysisCommandGroup
                        .noDWCinputToFeedback(global_result), !JEdifDWCAnalysisCommandGroup
                        .noDWCfeedbackOutput(global_result), !JEdifDWCAnalysisCommandGroup
                        .noDWCfeedForward(global_result), JEdifDWCAnalysisCommandGroup
                        .getInputAdditionType(global_result), JEdifDWCAnalysisCommandGroup
                        .getOutputAdditionType(global_result));

        // Report on results of triplication
        reportDuplicationStatus(duplicationStatus);
    }

    private static void iobFeedbackAnalysis(AbstractIOBAnalyzer iobAnalyzer,
            EdifCellInstanceGraph eciConnectivityGraph, SCCDepthFirstSearch sccDFS, boolean useBadCutConn,
            EdifCellBadCutGroupings badCutGroupings, EdifCellInstanceCollectionGraph badCutGroupConn,
            NMRArchitecture tmrArch, EdifCell flatCell) {
        boolean noIOBFB = JEdifDWCAnalysisCommandGroup.noIOBFB(global_result);
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
                out.println("\tUse the \"" + JEdifDWCAnalysisCommandGroup.NO_IOB_FB
                        + "\" flag to exclude these IOBs from feedback analysis.");
            }

        }

        //		String output_filename = JEdifTMRAnalysisCommandGroup.getIOBOutputFilename(result);
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
     * Performs full DWC analysis on the design.
     * 
     * @param duTracker The device utilization tracker
     * @param flatCell The flattened EDIFCell object
     * @return true if operation was successful, false otherwise.
     */
    protected static boolean fullDWC(DeviceUtilizationTracker duTracker, EdifCell flatCell) {
        out.println("Full DWC requested.");
        for (Iterator instancesIterator = flatCell.cellInstanceIterator(); instancesIterator.hasNext();) {
            EdifCellInstance eci = (EdifCellInstance) instancesIterator.next();
            try {
                duTracker.nmrInstance(eci, _replicationFactor);
            } catch (OverutilizationEstimatedStopException e1) {
                String errmsg = new String("ERROR: Instance " + eci
                        + " not added to resource tracker. Full DWC will not fit in part "
                        + TechnologyCommandGroup.getPart(global_result) + ".\n" + e1);
                err.println(errmsg);
                return false;
            } catch (OverutilizationHardStopException e2) {
                out.println("WARNING: Instance " + eci
                        + " not added to resource tracker due to hard resource constraints in part "
                        + TechnologyCommandGroup.getPart(global_result) + ".\n" + e2);
            } catch (DuplicateNMRRequestException e3) {
                // Ignore - likely it has been force duplicated already
            }
        }
        out.println(duTracker);
        return true;
    }

    /**
     * Marks specified ports for duplication. IOB instances will be force
     * duplicated or force not duplicated based on the duplication status of
     * their associated ports.
     * 
     * @param duTracker The device utilization tracker
     * @param flatCell The flattened EDIFCell object
     * @return Set of all EdifPorts in the design to be duplicated.
     */
    protected static Set<EdifPort> duplicatePorts(EdifCell flatCell, DeviceUtilizationTracker duTracker,
            AbstractIOBAnalyzer iobAnalyzer) {
        // add IOBAnalyzer to parameters? 
        // Determine which ports to duplicate
        // Identify BOARD specific ports to duplicate
        _noDuplicatePorts = new LinkedHashSet<String>();
        for (String port : NMRUtilities.SLAAC1V_PORTS_NOT_TO_REPLICATE)
            _noDuplicatePorts.add(port);

        _noDuplicatePorts.addAll(Arrays.asList(JEdifDWCAnalysisCommandGroup.getNoDWCp(global_result)));

        // Filter the set of ports to duplicate
        _portsToDuplicate = filterPortsToDuplicate(flatCell, JEdifDWCAnalysisCommandGroup.dwcInports(global_result),
                JEdifDWCAnalysisCommandGroup.dwcOutports(global_result), JEdifDWCAnalysisCommandGroup
                        .dwcPorts(global_result), _noDuplicatePorts);
        // Create list of ports NOT to duplicate
        _portsNotToDuplicate = flatCell.getPortList();
        _portsNotToDuplicate.removeAll(_portsToDuplicate);

        // Add ports for duplication (with associated IBUF/OBUF instances)
        // Use the IOB Analyzer to find the BUFs and other IOB instances
        Set<EdifCellInstance> _iobInstancesToDuplicate = new LinkedHashSet<EdifCellInstance>();
        for (EdifPort port : _portsToDuplicate) {
            _iobInstancesToDuplicate.addAll(iobAnalyzer.getIOBInstances(port.getSingleBitPortList()));
        }

        try {
            duTracker.nmrInstancesAtomic(_iobInstancesToDuplicate, _replicationFactor);
        } catch (DuplicateNMRRequestException e1) {
            // Already TMR'd
            System.out.println("WARNING: Duplicate DWC Port request. Should not get here: " + e1);
        } catch (OverutilizationEstimatedStopException e2) {
            // DeviceUtilizationTracker says to stop adding instances for dwc
            System.out.println("WARNING: Device full when adding Ports. Should not get here. " + e2);
        } catch (OverutilizationHardStopException e3) {
            // Hit some hard limit.
            System.out.println("ERROR: Could not triplicate " + _portsToDuplicate.size() + " top-level ports"
                    + " due to resource constraints: " + _portsToDuplicate);
            System.exit(1);
        }

        // Register the port IOB instances to skip with the DeviceUtilizationTracker
        Set<EdifCellInstance> _iobInstancesNotToDuplicate = new LinkedHashSet<EdifCellInstance>();
        for (EdifPort port : _portsNotToDuplicate) {
            _iobInstancesNotToDuplicate.addAll(iobAnalyzer.getIOBInstances(port.getSingleBitPortList()));
        }
        for (EdifCellInstance eci : _iobInstancesNotToDuplicate) {
            duTracker.excludeInstanceFromNMR(eci);
        }
        return _portsToDuplicate;
    }

    /**
     * Start with all the top-level ports of the original cell, remove ports
     * marked to not be duplicated, and return the remaining set of ports, which
     * is the set of ports to be duplicated.
     * 
     * @param cell The original, un-triplicated EdifCell
     * @param tripInputs Should inputs be triplicated?
     * @param tripOutputs Should outputs be triplicated?
     * @param noTriplicate A Collection of String objects of port names that
     * will not be triplicated.
     * @return a Set of EdifPort objects to be triplicated
     */
    protected static Set<EdifPort> filterPortsToDuplicate(EdifCell cell, boolean dupInputs, boolean dupOutputs,
            Collection<String> noDuplicate, Collection<String> duplicate) {

        Set<EdifPort> ports = new LinkedHashSet<EdifPort>();

        if (!dupInputs && !dupOutputs)
            return ports; // Return empty Set

        out.println("");
        out.println("Port Duplication:");
        for (EdifPort port : cell.getPortList()) {
            //System.out.println(port.getClass().getName());
            out.print("\t" + port);
            if (noDuplicate.contains(port.getName())) {
                out.println(" : in list to not duplicate.");
                continue;
            }
            if (duplicate.contains(port.getName())) {
                String dir;
                if (port.isInput())
                    dir = "in";
                else
                    dir = "out";

                out.println(" : duplicate " + dir + "put port as requested");
                ports.add(port);
                continue;
            }
            if (dupInputs && port.isInput()) {
                out.println(" : duplicate input port");
                ports.add(port);
            } else if (dupOutputs && port.isOutput()) {
                out.println(" : duplicate output port");
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
    protected static void excludeFromDuplication(EdifCell flatCell, DeviceUtilizationTracker duTracker,
            EdifEnvironment env) {
        Collection<EdifCellInstance> excludeInstances = new ArrayList<EdifCellInstance>();
        if (JEdifDWCAnalysisCommandGroup.noDWCi(global_result)) {
            for (String instanceName : JEdifDWCAnalysisCommandGroup.getNoDWCi(global_result)) {
                out.println("Excluding instance " + instanceName + " from DWC");
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
        if (JEdifDWCAnalysisCommandGroup.noDWCc(global_result)) {
            Collection<String> excludeCellTypes = Arrays.asList(JEdifDWCAnalysisCommandGroup.getNoDWCc(global_result));

            for (String excludeCellType : excludeCellTypes) {
                out.println("Excluding cell type " + excludeCellType + " from DWC");
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
        if (JEdifDWCAnalysisCommandGroup.noDWCclk(global_result)) {
            Collection<String> excludeClockDomains = Arrays.asList(JEdifDWCAnalysisCommandGroup
                    .getNoDWCclk(global_result));

            JEdifClockDomain domainParser = new JEdifClockDomain(env);
            Map<EdifNet, Set<EdifCellInstance>> clockDomainMap = domainParser.getECIMap();
            Set<EdifNet> clocks = clockDomainMap.keySet();

            for (String netName : excludeClockDomains) {
                Iterator i = clocks.iterator();
                Collection<EdifCellInstance> instances = new ArrayList<EdifCellInstance>();
                while (i.hasNext()) {
                    EdifNet net = (EdifNet) i.next();
                    if (net.getName().equals(netName)) {
                        out.println("Excluding clock domain " + netName + " from DWC.");
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
     * Forces certain EdifCellInstances and cell types to be duplicated based on
     * the command line arguments provided by the user. Instance names are case
     * sensitive, cell types are not.
     * 
     * @param forceInstanceStrings
     * @param flatCell
     * @param duTracker
     */
    protected static void forceDuplication(EdifCell flatCell, DeviceUtilizationTracker duTracker, EdifEnvironment env) {
        Collection<String> forceInstanceStrings = new ArrayList<String>();
        Collection<EdifCellInstance> forceInstances = new ArrayList<EdifCellInstance>();
        // Add Strings from command line to forceInstance Collection
        if (JEdifDWCAnalysisCommandGroup.dwcI(global_result))
            forceInstanceStrings.addAll(Arrays.asList(JEdifDWCAnalysisCommandGroup.getDWCi(global_result)));
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
        if (JEdifDWCAnalysisCommandGroup.dwcC(global_result)) {
            Collection<String> forceCellTypes = Arrays.asList(JEdifDWCAnalysisCommandGroup.getDWCc(global_result));
            for (String forceCellType : forceCellTypes) {
                out.println("Forcing duplication of cell type " + forceCellType);
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
        if (JEdifDWCAnalysisCommandGroup.dwcClk(global_result)) {
            Collection<String> forceClockDomains = Arrays.asList(JEdifDWCAnalysisCommandGroup.getDWCclk(global_result));

            JEdifClockDomain domainParser = new JEdifClockDomain(env);

            Map<EdifNet, Set<EdifCellInstance>> clockDomainMap = domainParser.getECIMap();
            Set<EdifNet> clocks = clockDomainMap.keySet();

            for (String netName : forceClockDomains) {
                Iterator i = clocks.iterator();
                Collection<EdifCellInstance> instances = new ArrayList<EdifCellInstance>();
                while (i.hasNext()) {
                    EdifNet net = (EdifNet) i.next();
                    if (net.getName().equals(netName)) {
                        out.println("Forcing duplication of clock domain " + netName);
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
     * Return a DWCArchitecture object for the specified technology.
     * 
     * @param technologyString The specified technology
     * @return A DWCArchtecture object
     */
    protected static XilinxDWCArchitecture getArchitecture(String technologyString) {
        if (technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX)
                || technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX2)
                || technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX2PRO)
                || technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX4))
            return new XilinxDWCArchitecture();
        throw new EdifRuntimeException("Invalid Technology: " + technologyString + ". Valid technologies include "
                + NMRUtilities.VIRTEX + ", " + NMRUtilities.VIRTEX2 + ", and " + NMRUtilities.VIRTEX4 + ".");
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
     * @param duplicationStatus An array of int's describing the level of
     * duplication (none, some, or all) for each section of the circuit
     */
    // TODO: print out percentages and split into persistent and non-persistent   
    protected static void reportDuplicationStatus(int[] duplicationStatus) {
        // Input to Feedback
        switch (duplicationStatus[PartialInputOutputFeedForwardTMR.INPUT_TO_FEEDBACK]) {
        case PartialInputOutputFeedForwardTMR.NONE:
            out.println("\tInput to Feedback section will not be duplicated");
            break;
        case PartialInputOutputFeedForwardTMR.SOME:
            out.println("\tInput to Feedback section will be partially duplicated");
            break;
        case PartialInputOutputFeedForwardTMR.ALL:
            out.println("\tInput to Feedback section will be fully duplicated");
            break;
        }
        // Feedback Output
        switch (duplicationStatus[PartialInputOutputFeedForwardTMR.FEEDBACK_OUTPUT]) {
        case PartialInputOutputFeedForwardTMR.NONE:
            out.println("\tFeedback Output section will not be duplicated");
            break;
        case PartialInputOutputFeedForwardTMR.SOME:
            out.println("\tFeedback Output section will be partially duplicated");
            break;
        case PartialInputOutputFeedForwardTMR.ALL:
            out.println("\tFeedback Output section will be fully duplicated");
            break;
        }
        // Feed Forward
        switch (duplicationStatus[PartialInputOutputFeedForwardTMR.FEED_FORWARD]) {
        case PartialInputOutputFeedForwardTMR.NONE:
            out.println("\tFeed Forward section will not be duplicated");
            break;
        case PartialInputOutputFeedForwardTMR.SOME:
            out.println("\tFeed Forward section will be partially duplicated");
            break;
        case PartialInputOutputFeedForwardTMR.ALL:
            out.println("\tFeed Forward section will be fully duplicated");
            break;
        }
    }

    /**
     * Prints out useful statistics about the projected DWC to be done
     */
    protected static void printDWCstats(DeviceUtilizationTracker duTracker, EdifCell flatCell) {
        //TODO: we can probably print more useful stats than this
        //how to best do it with only the resource tracker and flat cell?
        out.println();
        out.println("Estimated utilization (after duplication, before adding comparators):");
        out.println(duTracker.toString());
    }

    /**
     * Creates a serialized PartialReplicationStringDescription file.
     * 
     * @param ptmrd
     */
    private static void makeOutputFile(PartialReplicationDescription ptmrd) throws ReplicationException {
        PartialReplicationStringDescription stringDesc = new PartialReplicationStringDescription(ptmrd);
        if (!global_result.userSpecified(OutputFileCommandGroup.OUTPUT_OPTION)) {
            String name = MergeParserCommandGroup.getInputFileName(global_result);
            name = name.substring(0, name.lastIndexOf('.'));
            OutputFileCommandGroup.serializeObject(out, name + ".ptmr", stringDesc);
        } else {
            OutputFileCommandGroup.serializeObject(out, global_result, stringDesc);
        }

    }

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

    protected static JSAPResult global_result;

    protected static PrintStream out;

    protected static PrintStream err;

    protected static SharedIOBAnalysis siob;

    protected static List<EdifPortRefEdge> iobFeedbackEdges;

    protected static final int _replicationFactor = 2;
}
