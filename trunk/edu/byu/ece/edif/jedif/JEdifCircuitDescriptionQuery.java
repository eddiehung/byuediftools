package edu.byu.ece.edif.jedif;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.tools.replicate.nmr.CircuitDescription;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.CircuitDescriptionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifParserCommandGroup;
import edu.byu.ece.graph.dfs.DepthFirstTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

public class JEdifCircuitDescriptionQuery extends EDIFMain {

	public static void main(String[] args) {
		PrintStream out = System.out;
		PrintStream err = System.err;
		
		EXECUTABLE_NAME = "JEdifCircuitDescriptionQuery";
		TOOL_SUMMARY_STRING = "Print out some information contained in the circuit description file";

		printProgramExecutableString(out);

		EdifCommandParser parser = new EdifCommandParser();

		parser.addCommands(new JEdifParserCommandGroup());
		
		// option for input ReplicationDescription file
		parser.addCommands(new CircuitDescriptionCommandGroup());

		JSAPResult result = parser.parse(args, err);
		if (!result.success())
			System.exit(1);
		
		EdifEnvironment referenceEnv = JEdifParserCommandGroup.getEdifEnvironment(result, out);
		
		CircuitDescription cdesc = CircuitDescriptionCommandGroup.getCircuitDescription(result, referenceEnv, out);
		
		SCCDepthFirstSearch scc_dfs = cdesc.getDepthFirstSearch();
		
		Collection<DepthFirstTree> SCCs = scc_dfs.getTrees();
		
		out.println("Number of SCCs: " + SCCs.size());
		
		if (SCCs.size() > 0) {
			out.print("SCC sizes: " );
			Iterator<DepthFirstTree> it = SCCs.iterator();
			while (it.hasNext()) {
				DepthFirstTree SCC = it.next();
				out.print(SCC.getNodes().size());
				if (it.hasNext()) {
					out.print(", ");
				}
			}
			out.println();
		}
	}	
}
