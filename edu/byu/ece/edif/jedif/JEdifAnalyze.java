package edu.byu.ece.edif.jedif;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.replicate.nmr.CircuitDescription;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.util.graph.EdifCellBadCutGroupings;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollectionGraph;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollectionLink;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.graph.EdifPortRefEdge;
import edu.byu.ece.edif.util.iob.IOBAnalyzer;
import edu.byu.ece.edif.util.iob.XilinxVirtexIOBAnalyzer;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.CircuitDescriptionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.IOBCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifAnalyzeCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifParserCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.TechnologyCommandGroup;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.dfs.DepthFirstTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * A main method class that analyzes the flattened EDIF cell in preparation for future TMR steps.
 * It creates a circuit description file which is a serialized version of a CircuitDescription object.
 * 
 */
public class JEdifAnalyze extends EDIFMain {
    
	public static PrintStream out;
	public static PrintStream err;
	
	public static void main(String[] args) {
		
		out = System.out;
		err = System.err;
		
		EXECUTABLE_NAME = "JEdifAnalyze";
		TOOL_SUMMARY_STRING = "Perform some basic circuit analysis necessary for subsequent executables (i.e. JEdifNMRSelection and JEdifVoterSelection)";
		
		printProgramExecutableString(out);
		
		EdifCommandParser parser = new EdifCommandParser();
		
		// option for input .jedif file
		parser.addCommands(new JEdifParserCommandGroup());
		parser.addCommands(new IOBCommandGroup());
		parser.addCommands(new JEdifAnalyzeCommandGroup());
		parser.addCommands(new TechnologyCommandGroup());
		parser.addCommands(new LogFileCommandGroup("analyze.log"));
		parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));
		
		JSAPResult result = parser.parse(args, System.err);
		if (!result.success())
			System.exit(1);
		LogFileCommandGroup.CreateLog(result);
        printProgramExecutableString(LogFile.log());
		out = LogFile.out();
		err = LogFile.err();
		
		EdifEnvironment env = JEdifParserCommandGroup.getEdifEnvironment(result, out);
		
		boolean packInputRegs = IOBCommandGroup.packInputRegisters(result);
		boolean packOutputRegs = IOBCommandGroup.packOutputRegisters(result);
		boolean remove_iob_feedback = JEdifAnalyzeCommandGroup.noIOBFB(result);
		boolean use_bad_cut_conn = JEdifAnalyzeCommandGroup.badCutConn(result);
		String outputFilename = JEdifAnalyzeCommandGroup.getOutputFilename(result);
		TechnologyCommandGroup.getPartFromEDIF(result, env);
		NMRArchitecture arch = TechnologyCommandGroup.getArch(result);

		
		EdifCell topCell = env.getTopCell();
		
		EdifCellInstanceGraph instanceGraph = new EdifCellInstanceGraph(topCell, true, true);
		
		// TODO: is this hard coded for Virtex? Does this need to be modified for other architectures?
		IOBAnalyzer iobAnalyzer = new XilinxVirtexIOBAnalyzer(topCell, instanceGraph, packInputRegs, packOutputRegs);
		
		// We don't need source to source edges after the analyer is done with the graph.
		// Once we have completed IOB analysis, we no longer need the source to source edges (it is needed
		// for IOB analysis)
		instanceGraph.removeSourceToSourceEdges();
		
		badCutGroupings = null;
		bad_cut_conn = null;
		sccDFS = null;
		badCutGroupings = new EdifCellBadCutGroupings(topCell, arch, instanceGraph);
		if (use_bad_cut_conn) {
			bad_cut_conn = new EdifCellInstanceCollectionGraph(instanceGraph, badCutGroupings, true);
			sccDFS = new SCCDepthFirstSearch(bad_cut_conn);
		}
		else {
			sccDFS = new SCCDepthFirstSearch(instanceGraph);
		}
		
		List<EdifPortRefEdge> iobFeedbackEdges = iobFeedbackAnalysis(iobAnalyzer, instanceGraph, use_bad_cut_conn, remove_iob_feedback, arch, topCell);
		CircuitDescription cDesc = new CircuitDescription(iobAnalyzer, badCutGroupings, instanceGraph, sccDFS, arch);
		cDesc.setIOBFeedbackEdges(iobFeedbackEdges);
		cDesc.setRemoveIOBFeedback(remove_iob_feedback);

		CircuitDescriptionCommandGroup.writeCircuitDescription(outputFilename, env, cDesc, out);
		out.println();
	}


	private static List<EdifPortRefEdge> iobFeedbackAnalysis(IOBAnalyzer iobAnalyzer, EdifCellInstanceGraph eciConnectivityGraph, boolean useBadCutConn, boolean removeIOBFeedback, NMRArchitecture nmrArch, EdifCell topCell) {

		List<EdifPortRefEdge> iobFeedbackEdges = new ArrayList<EdifPortRefEdge>();

		out.println("Performing IOB Feedback Analysis");
		//   Do IOB feedback analysis here along with recomputation
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
			out.println();
			if (removeIOBFeedback) {
				// Report the IOB feedback edges found
				out.println("Found IOBs in feedback (see log file for details)");
				// Send the (possibly large) list to the log file only
				LogFile.log().println("The following IOBs were found in feedback structures: " + iobFeedbackEdges);
				out.println("\tThese IOBs will be excluded from feedback analysis.");
				eciConnectivityGraph.removeEdges(iobFeedbackEdges);
				if (useBadCutConn) {
					// Re-create BadCutGroupings
					badCutGroupings = new EdifCellBadCutGroupings(topCell, nmrArch, eciConnectivityGraph);
					// Re-create Group Connectivity (include top level ports)
					bad_cut_conn = new EdifCellInstanceCollectionGraph(eciConnectivityGraph, badCutGroupings, true);
					sccDFS = new SCCDepthFirstSearch(bad_cut_conn);
				} else {
					sccDFS = new SCCDepthFirstSearch(eciConnectivityGraph);
				}
			} else {
				// Report the IOB feedback edges found
				out.println("WARNING: Found IOBs in feedback (see log file for details)");
				// Send the (possibly large) list to the log file only
				LogFile.log().println("WARNING: Found the following IOBs in feedback: " + iobFeedbackEdges);
				out.println("\tUse the \"" + JEdifAnalyzeCommandGroup.NO_IOB_FB
						+ "\" flag to exclude these IOBs from feedback analysis.");
			}

		}
		
		return iobFeedbackEdges;
	}
	
	protected static EdifCellBadCutGroupings badCutGroupings = null;
	protected static EdifCellInstanceCollectionGraph bad_cut_conn = null;
	protected static SCCDepthFirstSearch sccDFS = null;
}
