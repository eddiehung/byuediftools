package edu.byu.ece.edif.tools.replicate.nmr;

import java.io.PrintStream;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.util.graph.EdifCellBadCutGroupings;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * An NMRSelectionStrategy chooses portions of a design for replication based on input
 * from the user. The most basic strategy is FullNMRSelectionStrategy which does full
 * replication. PartialNMRSelectionStrategy implements partial TMR/DWC/etc.
 */
public interface NMRSelectionStrategy {

	public boolean selectNMR(ReplicationUtilizationTracker rTracker, EdifCell topCell, EdifCellInstanceGraph eciConnectivityGraph, EdifCellBadCutGroupings badCutGroupings, SCCDepthFirstSearch sccDFS, NMRArchitecture nmrArch, PrintStream out, PrintStream err, JSAPResult result, ReplicationType replicationType, boolean override);
	
}
