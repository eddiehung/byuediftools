package edu.byu.ece.edif.tools.replicate.nmr;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.util.graph.AbstractEdifGraph;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollectionLink;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.graph.EdifOutputPortRefGraph;
import edu.byu.ece.edif.util.graph.EdifPortRefEdge;
import edu.byu.ece.edif.util.jsap.commandgroups.CutFeedbackCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.TechnologyCommandGroup;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.dfs.BasicDepthFirstSearchTree;
import edu.byu.ece.graph.dfs.DepthFirstTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

public class CutsetComputation {
	
	/**
     * Determines a valid cutset to remove all feedback, using various
     * algorithms
  	 * 
  	 * The JSAPResult object passed into this method should have been obtained from a
  	 * command parser using the CutFeedbackCommandGroup.
  	 * 
     * @return Collection(EdifPortRef) to be cut
     */
    public static Collection<EdifPortRef> getValidCutset(JSAPResult result, EdifCell flatCell, CircuitDescription cDesc, PrintStream out) {
        boolean debug = false;

        //get command-line options
        boolean HighestFFFanoutCutset = CutFeedbackCommandGroup.getFFFanout(result);
        boolean HighestFFFaninCutset = CutFeedbackCommandGroup.getFFFanin(result);
        boolean HighestFanoutCutset = CutFeedbackCommandGroup.getFanout(result);
        boolean ConnectivityCutset = CutFeedbackCommandGroup.getConnectivity(result);
        boolean basicDecomposition = CutFeedbackCommandGroup.getBasicDecomposition(result);
        boolean AfterFlipFlopsCutset = CutFeedbackCommandGroup.getAfterFFCutset(result);
        boolean BeforeFlipFlopsCutset = CutFeedbackCommandGroup.getBeforeFFCutset(result);
        boolean HighestFFFaninOutputCutset = CutFeedbackCommandGroup.getFFFaninOuput(result);
        
        NMRArchitecture nmrArch = TechnologyCommandGroup.getArch(result);

        Collection<EdifPortRef> PRGcuts = null;
        List<Edge> cutSet = new ArrayList<Edge>();
        long startTime = 0;

        if (HighestFFFaninCutset) {
            EdifCellInstanceGraph graph = new EdifCellInstanceGraph(flatCell);
            removeClockFeedback(graph);
            removeIOBFeedback(cDesc, graph);
            LogFile.out().println("Finding FF fan-in cutset. . .");
            startTime = System.currentTimeMillis();
            SCCDepthFirstSearch PRGsccDFS = new SCCDepthFirstSearch(graph);
            PRGcuts = NMRGraphUtilities.createHighestFFFaninCutset(graph, PRGsccDFS, nmrArch);
            startTime = LogFileCommandGroup.reportTime(startTime, "finding highest FF fan-in cutset", out);
            
            SCCDepthFirstSearch checkSCCDFS = new SCCDepthFirstSearch(graph);
            if (checkSCCDFS.getTrees().size() > 0) {
                out.println("Warning: unable to cut all feedback using highest FF fan-in cutset. Using basic decomposition for remaining feedback.");
                PRGcuts.addAll(getPortRefsToCutFromEdges(NMRGraphUtilities.createBasicDecompositionCutset(graph, checkSCCDFS, nmrArch), graph, nmrArch));
            }
            checkSCCDFS = new SCCDepthFirstSearch(graph);
            if (checkSCCDFS.getTrees().size() > 0) {                
                throw new EdifRuntimeException("Error: unable to cut all feedback using highest FF fan-in cutset and basic decomposition.");
            }
        }
        
        else if (HighestFFFaninOutputCutset) {
            EdifCellInstanceGraph graph = new EdifCellInstanceGraph(flatCell);
            removeClockFeedback(graph);
            removeIOBFeedback(cDesc, graph);
            LogFile.out().println("Finding FF fan-in output cutest. . .");
            startTime = System.currentTimeMillis();
            SCCDepthFirstSearch PRGsccDFS = new SCCDepthFirstSearch(graph);
            PRGcuts = NMRGraphUtilities.createHighestFFFaninOutputCutset(graph, PRGsccDFS, nmrArch);
            startTime = LogFileCommandGroup.reportTime(startTime, "finding highest FF fan-in output cutset", out);
            
            SCCDepthFirstSearch checkSCCDFS = new SCCDepthFirstSearch(graph);
            if (checkSCCDFS.getTrees().size() > 0)
                throw new EdifRuntimeException("Error: unable to cut all feedback using highest FF fan-in output cutset");            
        }
        
        else if (AfterFlipFlopsCutset) {
            EdifCellInstanceGraph graph = new EdifCellInstanceGraph(flatCell);
            removeClockFeedback(graph);
            removeIOBFeedback(cDesc, graph);
            LogFile.out().println("Computing after FFs cutset. . .");
            startTime = System.currentTimeMillis();
            PRGcuts = NMRGraphUtilities.createAfterFFsCutset(graph, nmrArch);
            startTime = LogFileCommandGroup.reportTime(startTime, "computing after FFs cutset", out);
            
            SCCDepthFirstSearch checkSCCDFS = new SCCDepthFirstSearch(graph);
            if (checkSCCDFS.getTrees().size() > 0)
                throw new EdifRuntimeException("Error: unable to cut all feedback using after FFs cutset");
        }
        
        else if (BeforeFlipFlopsCutset) {
            EdifCellInstanceGraph graph = new EdifCellInstanceGraph(flatCell);
            removeClockFeedback(graph);
            removeIOBFeedback(cDesc, graph);
            LogFile.out().println("Computing before FFs cutset. . .");
            startTime = System.currentTimeMillis();
            PRGcuts = NMRGraphUtilities.createBeforeFFsCutset(graph, nmrArch);
            startTime = LogFileCommandGroup.reportTime(startTime, "computing before FFs cutset", out);
            
            SCCDepthFirstSearch checkSCCDFS = new SCCDepthFirstSearch(graph);
            if (checkSCCDFS.getTrees().size() > 0) {
                out.println("Warning: unable to cut all feedback by placing voters before FFs. Using basic decomposition for remaining feedback.");
                PRGcuts.addAll(getPortRefsToCutFromEdges(NMRGraphUtilities.createBasicDecompositionCutset(graph, checkSCCDFS, nmrArch), graph, nmrArch));
            }
            
            checkSCCDFS = new SCCDepthFirstSearch(graph);
            if (checkSCCDFS.getTrees().size() > 0) {
                throw new EdifRuntimeException("Error: unable to cut all feedback using before flip-flops cutset and basic decomposition.");                
            }
            
        }

        else if (HighestFFFanoutCutset || HighestFanoutCutset) {
            //create graph and scc
            EdifOutputPortRefGraph graph = new EdifOutputPortRefGraph(flatCell);

            removeClockFeedback(graph);

            removeIOBFeedback(cDesc, graph);
            
           
            if (HighestFanoutCutset) {
                LogFile.out().println("Finding highest fanout cutset. . .");
                startTime = System.currentTimeMillis();
                SCCDepthFirstSearch PRGsccDFS = new SCCDepthFirstSearch(graph);
                PRGcuts = NMRGraphUtilities.createDecomposeValidCutSetFanout(graph, PRGsccDFS, nmrArch);
                startTime = LogFileCommandGroup.reportTime(startTime, "finding highest fan-out cutset", out);
            } else if (HighestFFFanoutCutset){
                LogFile.out().println("Finding FF Fanout Cutset...");
                startTime = System.currentTimeMillis();
                SCCDepthFirstSearch PRGsccDFS = new SCCDepthFirstSearch(graph);
                PRGcuts = NMRGraphUtilities.createDecomposeValidCutSetFFFanout(graph, PRGsccDFS, nmrArch);            
                startTime = LogFileCommandGroup.reportTime(startTime, "finding highest FF fan-out cutset", out);
            }
            
        }

        else if (ConnectivityCutset){ // old algorithm
            //create graph and scc
            EdifCellInstanceGraph eciConnectivityGraph = new EdifCellInstanceGraph(flatCell, true, false);

            removeClockFeedback(eciConnectivityGraph);
            removeIOBFeedback(cDesc, eciConnectivityGraph);
            
            LogFile.out().println("Finding connectivity cutset. . .");
            startTime = System.currentTimeMillis();
            SCCDepthFirstSearch PRGsccDFS = new SCCDepthFirstSearch(eciConnectivityGraph);
            for (Iterator<DepthFirstTree> i = PRGsccDFS.getTopologicallySortedTreeList().iterator(); i.hasNext();) {
                BasicDepthFirstSearchTree scc = (BasicDepthFirstSearchTree) i.next();
                cutSet.addAll(NMRGraphUtilities.createDecomposeValidCutSet(eciConnectivityGraph, scc, nmrArch));
            }
            PRGcuts = getPortRefsToCutFromEdges(cutSet, eciConnectivityGraph, nmrArch);
            startTime = LogFileCommandGroup.reportTime(startTime, "finding connectivity cutset", out);
        }

        else if (basicDecomposition) {
            EdifCellInstanceGraph eciConnectivityGraph = new EdifCellInstanceGraph(flatCell, true, false);

            removeClockFeedback(eciConnectivityGraph);
            removeIOBFeedback(cDesc, eciConnectivityGraph);

            LogFile.out().println("Finding basic decomposition cutset. . .");
            startTime = System.currentTimeMillis();   
            SCCDepthFirstSearch sccDFS = new SCCDepthFirstSearch(eciConnectivityGraph);
            PRGcuts = getPortRefsToCutFromEdges(NMRGraphUtilities.createBasicDecompositionCutset(eciConnectivityGraph, sccDFS, nmrArch), eciConnectivityGraph, nmrArch);
            startTime = LogFileCommandGroup.reportTime(startTime, "finding basic decomposition cutset", out);

            SCCDepthFirstSearch checkSCCDFS = new SCCDepthFirstSearch(eciConnectivityGraph);
            if (checkSCCDFS.getTrees().size() > 0)
                throw new EdifRuntimeException("Error: unable to cut all feedback using basic decomposition cutset");
        }


        if (debug) {
            for (EdifPortRef epr : PRGcuts) {
                LogFile.debug().println("" + epr);
            }
        }
        return PRGcuts;
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
                                LogFile.debug().println("### ONE OF THE EDGES IS A BAD CUT!!! " + noCutEPR);
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

    /**
     * Takes a EdifCollectionGraph or a EdifOutputPortRefGraph and removes any
     * SinkEPRs named "CLKFB" (the clock feedback port on a DCM
     * 
     * @param EdifCollectionGraph or a EdifOutputPortRefGraph
     */
    private static void removeClockFeedback(AbstractEdifGraph graph) {
        for (EdifPortRefEdge edge : (Collection<EdifPortRefEdge>) graph.getEdges()) {
            if (edge.getSinkEPR().getPort().getName().equals("CLKFB")) {
                graph.removeEdge(edge);
            }
        }
    }
    
    private static void removeIOBFeedback(CircuitDescription cDesc, AbstractEdifGraph graph) {
        boolean cutIOBFeedback = cDesc.shouldRemoveIOBFeedback();
        if (cutIOBFeedback) {

            Collection<EdifPortRef[]> epr_pairs = cDesc.getIOBFeedbackPortRefs();
            for (EdifPortRef[] epr_pair : epr_pairs) {
                EdifPortRef src = epr_pair[0];
                EdifPortRef sink = epr_pair[1];

                if (graph instanceof EdifOutputPortRefGraph) {
                    Collection<EdifPortRefEdge> o4 = graph.getOutputEdges(src);
                    for (EdifPortRefEdge se : o4) {
                        if (sink.equals(se.getSinkEPR()))
                            graph.removeEdge(se);
                    }
                }

                if (graph instanceof EdifCellInstanceGraph) {
                    Object src_node = src.getCellInstance();
                    Object sink_node = sink.getCellInstance();

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
}
