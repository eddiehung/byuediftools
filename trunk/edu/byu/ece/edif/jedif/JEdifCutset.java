/*
 * Takes a JEdif file and determines where to cut feedback to insert voters
 * 
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

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.replicate.PartialReplicationDescription;
import edu.byu.ece.edif.tools.replicate.PartialReplicationStringDescription;
import edu.byu.ece.edif.tools.replicate.ReplicationException;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.NMRGraphUtilities;
import edu.byu.ece.edif.util.graph.AbstractEdifGraph;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollectionLink;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.graph.EdifOutputPortRefGraph;
import edu.byu.ece.edif.util.graph.EdifPortRefEdge;
import edu.byu.ece.edif.util.jsap.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.CutFeedbackCommandGroup;
import edu.byu.ece.edif.util.jsap.EDIFMain;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.MergeParserCommandGroup;
import edu.byu.ece.edif.util.jsap.OutputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.PTMRFileCommandGroup;
import edu.byu.ece.edif.util.jsap.SharedIOBAnalysis;
import edu.byu.ece.edif.util.jsap.TechnologyCommandGroup;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.dfs.BasicDepthFirstSearchTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * Takes a JEdif file and determines where to cut feedback to insert voters
 * 
 * @author Derrick Gibelyou
 */
public class JEdifCutset extends EDIFMain {
    private static boolean DEBUG = false;

    public static void main(String[] args) {

        // Define the print streams for this program
        PrintStream out = System.out;
        PrintStream err = System.err;

        // Print executable heading
        EXECUTABLE_NAME = "JEdifCutset";
        TOOL_SUMMARY_STRING = "Identifies PortRefs to cut feedback for possible voter insertion";
        printProgramExecutableString(out);

        // Parse command line options
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new JEdifParserCommandGroup());
        parser.addCommands(new CutFeedbackCommandGroup());
        parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));
        parser.addCommands(new TechnologyCommandGroup());
        parser.addCommands(new OutputFileCommandGroup());
        parser.addCommands(new PTMRFileCommandGroup());
        LogFileCommandGroup loggerCG = new LogFileCommandGroup();
        parser.addCommands(loggerCG);

        JSAPResult result = parser.parse(args, err);
        if (!result.success())
            System.exit(1);

        LogFileCommandGroup.CreateLog(result);
        out = LogFile.out();
        err = LogFile.err();

        printProgramExecutableString(LogFile.log());

        // can be used to log currently used options, as well as to create help files
        //loggerCG.logOptions(parser.getCommands(),logger);
        LogFile.log().println("These command-line options were used:" + Arrays.asList(args) + "\n");

        //		logger.println("This is debug stuff",logger.DEBUG);
        //		logger.println("This is log stuff",logger.LOG);
        //		logger.println("This is normal output");
        //		logger.println("WARNING: this is bad",logger.WARN);
        //		logger.println("ERROR: this is bad. exiting now",logger.ERR);
        //		//System.exit(0);

        // Get IOB analysis information
        SharedIOBAnalysis iobAnalysis = CutFeedbackCommandGroup.getIOBAnalysis(result, out);

        // Create EdifEnvironment data structure
        EdifEnvironment top = JEdifParserCommandGroup.getEdifEnvironment(result, out);
        EdifCell flatCell = top.getTopCell();

        TechnologyCommandGroup.getPartFromEDIF(result, top);
        PartialReplicationDescription ptmr = null;

        try {
            ptmr = PTMRFileCommandGroup.getPartialReplicationDescription(result, out).getDescription(top);
        } catch (ReplicationException e) {
            e.toRuntime();
        }

        Collection<EdifPortRef> PRGcuts = getValidCutset(result, flatCell, iobAnalysis);
        ptmr.portRefsToCut = PRGcuts;

        //create pTMR data file for next step
        PartialReplicationStringDescription ptmrs = null;
        try {
            ptmrs = new PartialReplicationStringDescription(ptmr);
        } catch (ReplicationException e) {
            e.toRuntime();
        }

        if (!result.userSpecified(OutputFileCommandGroup.OUTPUT_OPTION)) {
            String name = MergeParserCommandGroup.getInputFileName(result);
            name = name.substring(0, name.lastIndexOf('.'));
            OutputFileCommandGroup.serializeObject(out, name + ".ptmr", ptmrs);
        } else {
            OutputFileCommandGroup.serializeObject(out, result, ptmrs);
        }

        //InputOutputFileCommandGroup.serizalizeObject(out, result, PRGcuts);
        LogFile.out().println("Done");

    }

    /**
     * Determines a valid cutset to remove all feedback, using various
     * algorithms
     * 
     * @param out: PrintStream for messages
     * @param HighestFFFanoutCutset: boolean to use this algorithm
     * @param HighestFanoutCutset: boolean to use this algorithm
     * @param tmrArch: Technology Architecture for determining bad cuts
     * @param flatCell: cell to operate on
     * @return Collection(EdifPortRef) to be cut
     */

    public static Collection<EdifPortRef> getValidCutset(JSAPResult result, EdifCell flatCell,
            SharedIOBAnalysis iobAnalysis) {

        //get command-line options
        boolean HighestFFFanoutCutset = CutFeedbackCommandGroup.getFFFanout(result);
        boolean HighestFanoutCutset = CutFeedbackCommandGroup.getFanout(result);
        NMRArchitecture tmrArch = TechnologyCommandGroup.getArch(result);
        //boolean cutIOBFeedback = CutFeedbackCommandGroup.getCutIOB(result);

        Collection<EdifPortRef> PRGcuts = null;
        List<Edge> cutSet = new ArrayList<Edge>();
        long startTime = System.currentTimeMillis();

        /*
         * TODO:Intense rewriting to equate EdifConnectivity and
         * EdifPortRefGraphs
         */
        if (HighestFFFanoutCutset || HighestFanoutCutset) {
            //create graph and scc
            EdifOutputPortRefGraph graph = new EdifOutputPortRefGraph(flatCell);
            removeIOBFeedback(flatCell, iobAnalysis, graph);

            //			  // DEBUG (BHP)
            //            // Create DebugSubGraph
            //            //DebugSubGraph.getSubGraph("data_iobuf_31", 3, flatCell, true, false);
            //            // Find desired EdifCellInstance
            //            String eciName = "data_iobuf_31";
            //            System.out.println("Searching for instance " + eciName + "...");
            //            EdifCellInstance eci = flatCell.getCellInstance(eciName);
            //            if (eci == null) {
            //                System.out.println("Could not find EdifCellInstance: " + eciName);
            //            } else {
            //                // Create a Collection of this instances neighbors, up to "depth"
            //                //   in all directions.
            //                Collection neighbors = DebugSubGraph.getNeighbors(eci, 3, graph, true);
            //                // Create subgraph and return
            //                BasicGraph debugGraph = graph.getSubGraph(neighbors);
            //                debugGraph.toDotty("debugGraph.dot");
            //            }

            //			// BHP: Special hack for designs with CLKDLLs to triplicate. Remove clock dll feedback
            //            Collection<EdifPortRefEdge> edgesToRemove = new ArrayList<EdifPortRefEdge>();
            //            for (EdifPortRefEdge edge : graph.getEdges()) {
            //                String sinkPortName = edge.getSinkEPR().getPort().getName();
            //                if (sinkPortName.equals("CLKFB")) {
            //                    edgesToRemove.add(edge);
            //                    System.out.println("#### Removing edge: " + edge);
            //                }
            //            }
            //            graph.removeEdges(edgesToRemove);

            LogFile.out().println("SCC Decomposition. . .");
            //iobFeedback(graph,cutIOBFeedback,flatCell,out,out);
            SCCDepthFirstSearch PRGsccDFS = new SCCDepthFirstSearch(graph);
            startTime = LogFileCommandGroup.reportTime(startTime, "SCCDecomposition", System.out);

            LogFile.out().println("Done. Finding Cutset...");

            if (HighestFFFanoutCutset)
                PRGcuts = NMRGraphUtilities.createDecomposeValidCutSetFFFanout(graph, PRGsccDFS, tmrArch);
            else
                PRGcuts = NMRGraphUtilities.createDecomposeValidCutSetFanout(graph, PRGsccDFS, tmrArch);

        }

        else { // old algorithm
            //create graph and scc
            EdifCellInstanceGraph eciConnectivityGraph = new EdifCellInstanceGraph(flatCell, true, false);

            LogFile.out().println("SCC Decomposition. . .");
            //if(iobAnalysis != null) {
            removeIOBFeedback(flatCell, iobAnalysis, eciConnectivityGraph);

            //			// DEBUG (BHP)
            //            // Create DebugSubGraph
            //            //DebugSubGraph.getSubGraph("data_iobuf_31", 3, flatCell, true, false);
            //            // Find desired EdifCellInstance
            //            String eciName = "data_iobuf_31";
            //            System.out.println("Searching for instance " + eciName + "...");
            //            EdifCellInstance eci = flatCell.getCellInstance(eciName);
            //            if (eci == null) {
            //                System.out.println("Could not find EdifCellInstance: " + eciName);
            //            } else {
            //                // Create a Collection of this instances neighbors, up to "depth"
            //                //   in all directions.
            //                Collection neighbors = DebugSubGraph.getNeighbors(eci, 2, eciConnectivityGraph, true);
            //                // Create subgraph and return
            //                BasicGraph debugGraph = eciConnectivityGraph.getSubGraph(neighbors);
            //                debugGraph.toDotty("debugGraph.dot");
            //            }

            //			// BHP: Special hack for designs with CLKDLLs to triplicate. Remove clock dll feedback
            //            Collection<EdifCellInstanceEdge> edgesToRemove = new ArrayList<EdifCellInstanceEdge>();
            //            for (EdifCellInstanceEdge edge : eciConnectivityGraph.getEdges()) {
            //                String sinkPortName = edge.getSinkEPR().getPort().getName();
            //                if (sinkPortName.equals("CLKFB")) {
            //                    edgesToRemove.add(edge);
            //                    System.out.println("#### Removing edge: " + edge);
            //                }
            //            }
            //            eciConnectivityGraph.removeEdges(edgesToRemove);

            SCCDepthFirstSearch PRGsccDFS = new SCCDepthFirstSearch(eciConnectivityGraph);
            startTime = LogFileCommandGroup.reportTime(startTime, "SCCDecomposition", System.out);

            LogFile.out().println("Done. Finding Cutset");

            for (Iterator i = PRGsccDFS.getTopologicallySortedTreeList().iterator(); i.hasNext();) {
                BasicDepthFirstSearchTree scc = (BasicDepthFirstSearchTree) i.next();
                cutSet.addAll(NMRGraphUtilities.createDecomposeValidCutSet(eciConnectivityGraph, scc, tmrArch));
            }
            PRGcuts = getPortRefsToCutFromEdges(cutSet, eciConnectivityGraph, tmrArch);
        }
        startTime = LogFileCommandGroup.reportTime(startTime, "finding cutset", System.out);

        return PRGcuts;
    }

    private static void removeIOBFeedback(EdifCell flatCell, SharedIOBAnalysis iobAnalysis, AbstractEdifGraph graph) {
        boolean cutIOBFeedback = iobAnalysis.removeIOBfeedback();
        if (cutIOBFeedback) {

            Collection<EdifPortRef[]> epr_pairs = iobAnalysis.getIOBFeedbackPortRefs(flatCell);
            for (EdifPortRef[] epr_pair : epr_pairs) {
                //EdifPortRef src = getEPRFromReference(flatCell,essl.getSourceEPR());
                //EdifPortRef sink =getEPRFromReference(flatCell,essl.getSinkEPR());
                EdifPortRef src = epr_pair[0];
                EdifPortRef sink = epr_pair[1];

                if (graph instanceof EdifOutputPortRefGraph) {
                    //System.out.println("removing iob feedback pr");
                    Collection<EdifPortRefEdge> o4 = graph.getOutputEdges(src);
                    for (EdifPortRefEdge se : o4) {
                        if (sink.equals(se.getSinkEPR()))
                            graph.removeEdge(se);
                    }
                }

                if (graph instanceof EdifCellInstanceGraph) {
                    //System.out.println("removing iob feedback ecic");

                    Object src_node = src.getCellInstance();
                    Object sink_node = sink.getCellInstance();
                    //Object sink_node= essl.getSinkEPR().getCellInstance();
                    if (src_node == null)
                        src_node = src.getSingleBitPort();
                    if (sink_node == null)
                        sink_node = sink.getSingleBitPort();

                    Edge e = graph.getEdge(src_node, sink_node);
                    if (e != null)
                        graph.removeEdge(e);
                }
            }
        }
    }

    /**
     * @param cutSet List of EdifEdges to convert
     * @param graph: EdifCellInstanceGraph
     * @param nmrArch: Architecture to determine bad cuts
     * @return EdifPortRef collection of PortRefs to cut
     */
    protected static Collection<EdifPortRef> getPortRefsToCutFromEdges(List<Edge> cutSet, EdifCellInstanceGraph graph,
            NMRArchitecture nmrArch) {
        Collection<EdifPortRef> portRefsToCut = new LinkedHashSet<EdifPortRef>();
        Collection<EdifCellInstanceEdge> esslCutSet = new ArrayList<EdifCellInstanceEdge>(cutSet.size());

        // alwaysChooseSinks = true uses the old, brain-dead method
        // alwaysChooseSinks = false uses a smarter method
        boolean alwaysChooseSinks = false;
        boolean debug = false;

        // Convert CollectionLinks to SourceSinkLinks, if necessary
        for (Edge edge : cutSet) {
            if (edge instanceof EdifCellInstanceCollectionLink) {
                esslCutSet.addAll(((EdifCellInstanceCollectionLink) edge).getLinks());
            } else if (edge instanceof EdifCellInstanceEdge)
                esslCutSet.add((EdifCellInstanceEdge) edge);
            else
                ;
            //throw new EdifRuntimeException("Unknown Edge type: "+edge.getClass());
        }

        // Always choose the Sink EPR
        if (alwaysChooseSinks) {
            // System.out.println("Getting cutset port refs...");
            for (EdifCellInstanceEdge edge : esslCutSet) {
                EdifPortRef cutEPR = edge.getSinkEPR();
                // Double-check that the Sink is an Input EPR (driven, not drives)
                if (cutEPR.isDriverPortRef()) {
                    LogFile.log().println("WARNING: PortRef chosen for TMR is a " + "signal driver: " + cutEPR);
                    // TODO: Do this more gracefully!
                    cutEPR = edge.getSourceEPR();
                }
                portRefsToCut.add(cutEPR);
            }
        }
        // Try and be smart, choosing the Driver EPR unless not possible
        //   because of bad cuts
        else {
            // Iterate over all cut edges. For each edge:
            // - Check if the source portRef has been cut (or checked?)
            //   - If cut, skip this one
            //   - else if checked (and not cut), cut sink
            //   - else Check all Edges coming from this portRef
            //     - If *any* are bad cuts, cut sink and mark this EPR as checked
            Collection<EdifPortRef> checkedPortRefs = new LinkedHashSet<EdifPortRef>();
            for (EdifCellInstanceEdge edge : esslCutSet) {
                // Get Edge driver
                EdifPortRef cutEPR = edge.getSourceEPR();
                EdifPortRef noCutEPR = edge.getSinkEPR();
                // Double-check that the Source is an Output EPR (driver, not driven)
                if (!cutEPR.isDriverPortRef()) {
                    throw new EdifRuntimeException("ERROR " + "(getPortRefsToCutFromEdges): Edge source is "
                            + "not a signal driver: " + cutEPR);
                    //cutEPR = edge.getSinkEPR();
                    //noCutEPR = edge.getSourceEPR();
                }

                // - Check if the source portRef has been cut (or checked?)
                //   - If cut, skip this one
                if (portRefsToCut.contains(cutEPR)) {
                    if (debug)
                        LogFile.debug().println("### SKIP THIS ONE (SAVE TIME!!!)");
                    continue;
                }
                //   - else if checked (and not cut), cut sink
                else if (checkedPortRefs.contains(cutEPR)) {
                    if (debug)
                        LogFile.debug().println("### HAD TO CUT A SINK!!!");
                    cutEPR = noCutEPR;
                }
                //   - else Check all Edges coming from this portRef
                else {
                    // First visit of this EPR. Mark this EPR as checked.
                    checkedPortRefs.add(cutEPR);
                    // If *any* are bad cuts, cut sink (and move on)
                    for (EdifCellInstanceEdge otherEdge : graph.getOutputEdges(edge.getSource(), cutEPR)) {
                        if (nmrArch.isBadCutConnection(otherEdge.getSourceEPR(), otherEdge.getSinkEPR())) {
                            if (debug)
                                LogFile.debug().println("### ONE OF THE EDGES IS A BAD CUT!!!");
                            cutEPR = noCutEPR;
                            break;
                        }
                    }
                }

                portRefsToCut.add(cutEPR);
            }

        }

        if (debug)
            LogFile.debug().println("Cutting " + portRefsToCut.size() + " PortRefs");

        return portRefsToCut;
    }

}