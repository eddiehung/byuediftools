package edu.byu.ece.edif.tools.replicate.nmr;

import java.io.ObjectStreamException;
import java.io.PrintStream;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.PartialInputOutputFeedForwardNMR;
import edu.byu.ece.edif.util.graph.EdifCellBadCutGroupings;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifNMRSelectionCommandGroup;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * This class implements partial TMR/DWC/etc.
 */
public class PartialNMRSelectionStrategy implements NMRSelectionStrategy {

	protected static PartialNMRSelectionStrategy _instance = null;

	protected PartialNMRSelectionStrategy() {

	}

	public static PartialNMRSelectionStrategy getInstance() {
		if (_instance == null)
			_instance = new PartialNMRSelectionStrategy();
		return _instance;
	}

	public boolean selectNMR(ReplicationUtilizationTracker rTracker,
			EdifCell topCell, EdifCellInstanceGraph eciConnectivityGraph,
			EdifCellBadCutGroupings badCutGroupings,
			SCCDepthFirstSearch sccDFS, NMRArchitecture nmrArch,
			PrintStream out, PrintStream err, JSAPResult result,
			ReplicationType replicationType, boolean override) {

		out.println("");
		out.println("Analyzing design . . .");

		// Determine NMR of feedback section
		boolean allSCCInstancesReplicated;
		if (!JEdifNMRSelectionCommandGroup.noNMRFeedback(result)) {
			allSCCInstancesReplicated = NMRGraphUtilities
					.nmrSCCsUsingSCCDecomposition(sccDFS, nmrArch, rTracker,
							JEdifNMRSelectionCommandGroup
									.doSCCDecomposition(result),
							JEdifNMRSelectionCommandGroup
									.getSCCSortType(result), replicationType,
							override);

			// Print results for SCC replication
			if (allSCCInstancesReplicated)
				out.println("\tFeedback section will be fully replicated");
			else
				out.println("\tFeedback section will be partially replicated");
		}

		else {
			/*
			 * This only happens if the user selected the "nonmrFeedback"
			 * option, in which no cuts are needed.
			 */
			allSCCInstancesReplicated = false;
			out.println("\tFeedback section will not be replicated");
		}

		// Partial NMR selection
		// SCC Inputs and Outputs plus Feed-Forward section
		int[] replicationStatus = null;
		replicationStatus = PartialInputOutputFeedForwardNMR
				.nmrSCCInputAndOutput(
						topCell,
						eciConnectivityGraph,
						badCutGroupings,
						rTracker,
						nmrArch,
						sccDFS,
						!JEdifNMRSelectionCommandGroup
								.noNMRinputToFeedback(result),
						!JEdifNMRSelectionCommandGroup
								.noNMRfeedbackOutput(result),
						!JEdifNMRSelectionCommandGroup.noNMRfeedForward(result),
						JEdifNMRSelectionCommandGroup
								.getInputAdditionType(result),
						JEdifNMRSelectionCommandGroup
								.getOutputAdditionType(result),
						replicationType, override);

		// Report on results of replication
		reportReplicationStatus(replicationStatus, out);
		return true;
	}

	/**
	 * @param replicationStatus
	 *            An array of int's describing the level of replication (none,
	 *            some, or all) for each section of the circuit
	 */
	// TODO: print out percentages and split into persistent and non-persistent
	protected static void reportReplicationStatus(int[] replicationStatus,
			PrintStream out) {
		// Input to Feedback
		switch (replicationStatus[PartialInputOutputFeedForwardNMR.INPUT_TO_FEEDBACK]) {
		case PartialInputOutputFeedForwardNMR.NONE:
			out.println("\tInput to Feedback section will not be replicated");
			break;
		case PartialInputOutputFeedForwardNMR.SOME:
			out
					.println("\tInput to Feedback section will be partially replicated");
			break;
		case PartialInputOutputFeedForwardNMR.ALL:
			out.println("\tInput to Feedback section will be fully replicated");
			break;
		}
		// Feedback Output
		switch (replicationStatus[PartialInputOutputFeedForwardNMR.FEEDBACK_OUTPUT]) {
		case PartialInputOutputFeedForwardNMR.NONE:
			out.println("\tFeedback Output section will not be replicated");
			break;
		case PartialInputOutputFeedForwardNMR.SOME:
			out
					.println("\tFeedback Output section will be partially replicated");
			break;
		case PartialInputOutputFeedForwardNMR.ALL:
			out.println("\tFeedback Output section will be fully replicated");
			break;
		}
		// Feed Forward
		switch (replicationStatus[PartialInputOutputFeedForwardNMR.FEED_FORWARD]) {
		case PartialInputOutputFeedForwardNMR.NONE:
			out.println("\tFeed Forward section will not be replicated");
			break;
		case PartialInputOutputFeedForwardNMR.SOME:
			out.println("\tFeed Forward section will be partially replicated");
			break;
		case PartialInputOutputFeedForwardNMR.ALL:
			out.println("\tFeed Forward section will be fully replicated");
			break;
		}
	}
	
	/**
	 * This method ensures that during deserialization, the _instance variable will be referred
	 * to and possibly modified so that extra instances (more than necessary) don't get created.
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		PartialNMRSelectionStrategy instance = _instance;
		if (instance == null) {
			instance = getInstance();
		}
		return instance;
	}
}
