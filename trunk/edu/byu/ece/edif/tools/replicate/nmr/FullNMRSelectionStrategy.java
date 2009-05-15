package edu.byu.ece.edif.tools.replicate.nmr;

import java.io.ObjectStreamException;
import java.io.PrintStream;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.util.graph.EdifCellBadCutGroupings;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.jsap.commandgroups.TechnologyCommandGroup;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

public class FullNMRSelectionStrategy implements NMRSelectionStrategy {

	protected static FullNMRSelectionStrategy _instance = null;
	
	protected FullNMRSelectionStrategy() {
		
	}
	
	public static FullNMRSelectionStrategy getInstance() {
		if (_instance == null)
			_instance = new FullNMRSelectionStrategy();
		return _instance;
	}

	public boolean selectNMR(ReplicationUtilizationTracker rTracker,
			EdifCell topCell, EdifCellInstanceGraph eciConnectivityGraph,
			EdifCellBadCutGroupings badCutGroupings,
			SCCDepthFirstSearch sccDFS, NMRArchitecture nmrArch,
			PrintStream out, PrintStream err, JSAPResult result,
			ReplicationType replicationType, boolean override) {


		out.println("Full NMR requested.");
		for (EdifCellInstance eci : topCell.getSubCellList()) {
			try {
				rTracker.addToTracker(eci, replicationType, override);
			} catch (OverutilizationEstimatedStopException e1) {
				String errmsg = new String("ERROR: Instance " + eci
						+ " not added to resource tracker. Full NMR will not fit in part "
						+ TechnologyCommandGroup.getPart(result) + ".\n" + e1);
				err.println(errmsg);
				return false;
			} catch (OverutilizationHardStopException e2) {
				out.println("WARNING: Instance " + eci
						+ " not added to resource tracker due to hard resource constraints in part "
						+ TechnologyCommandGroup.getPart(result) + ".\n" + e2);
			} catch (DuplicateNMRRequestException e3) {
				// Ignore - likely it has been force replicated already
			}
		}
		
		return true;
	}
	
	/**
	 * This method ensures that during deserialization, the _instance variable will be referred
	 * to and possibly modified so that extra instances (more than necessary) don't get created.
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		FullNMRSelectionStrategy instance = _instance;
		if (instance == null) {
			instance = getInstance();
		}
		return instance;
	}
}
