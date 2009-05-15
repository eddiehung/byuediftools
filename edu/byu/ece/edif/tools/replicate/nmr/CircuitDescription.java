package edu.byu.ece.edif.tools.replicate.nmr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.util.graph.EdifCellBadCutGroupings;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.graph.EdifPortRefEdge;
import edu.byu.ece.edif.util.iob.IOBAnalyzer;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * The CircuitDescription class keeps track of circuit analysis information so
 * that some analysis can be performed once and then saved for later executables.
 * (This class is serializable).
 */
public class CircuitDescription implements Serializable {
	
	public CircuitDescription(IOBAnalyzer iobAnalyzer, EdifCellBadCutGroupings badCutGroupings, EdifCellInstanceGraph instanceGraph, SCCDepthFirstSearch sccDFS, NMRArchitecture nmrArch) {
		_iobAnalyzer = iobAnalyzer;
		_badCutGroupings = badCutGroupings;
		_instanceGraph = instanceGraph;
		_sccDFS = sccDFS;		
		_nmrArch = nmrArch;
	}

	public void setRemoveIOBFeedback(boolean removeIOBFeedback) {
		_removeIOBFeedback = removeIOBFeedback;
	}
	
	public boolean shouldRemoveIOBFeedback() {
		return _removeIOBFeedback;
	}
	
	public void setIOBFeedbackEdges(Collection<EdifPortRefEdge> iobFeedbackEdges) {
		_IOBFeedbackEdges = iobFeedbackEdges;
	}
	
	public Collection<EdifPortRefEdge> getIOBFeedbackEdges() {
		return _IOBFeedbackEdges;
	}
	
	public Collection<EdifPortRef[]> getIOBFeedbackPortRefs() {
		List<EdifPortRef[]> eprs = new ArrayList<EdifPortRef[]>();
		for (EdifPortRefEdge edge : _IOBFeedbackEdges) {
			EdifPortRef[] pair = new EdifPortRef[2];
			pair[0] = edge.getSourceEPR();
			pair[1] = edge.getSinkEPR();
			eprs.add(pair);
		}
		return eprs;
	}
	
	public IOBAnalyzer getIOBAnalyzer() {
		return _iobAnalyzer;
	}
	
	public EdifCellBadCutGroupings getBadCutGroupings() {
		return _badCutGroupings;
	}
	
	public EdifCellInstanceGraph getInstanceGraph() {
		return _instanceGraph;
	}
	
	public SCCDepthFirstSearch getDepthFirstSearch() {
		return _sccDFS;
	}

	public NMRArchitecture getNMRArchitecture() {
		return _nmrArch;
	}
	
	public void setIOBAnalyzer(IOBAnalyzer iobAnalyzer) {
		_iobAnalyzer = iobAnalyzer;
	}
	
	public void setBadCutGroupings(EdifCellBadCutGroupings badCutGroupings) {
		_badCutGroupings = badCutGroupings;
	}
	
	public void setInstanceGraph(EdifCellInstanceGraph instanceGraph) {
		_instanceGraph = instanceGraph;
	}
	
	public void setDepthFirstSearch(SCCDepthFirstSearch sccDFS) {
		_sccDFS = sccDFS;
	}
	
	protected IOBAnalyzer _iobAnalyzer;
	
	protected EdifCellBadCutGroupings _badCutGroupings;
	
	protected EdifCellInstanceGraph _instanceGraph;
	
	protected SCCDepthFirstSearch _sccDFS;
	
	protected Collection<EdifPortRefEdge> _IOBFeedbackEdges;
	
	protected boolean _removeIOBFeedback;
	
	protected NMRArchitecture _nmrArch;

}
