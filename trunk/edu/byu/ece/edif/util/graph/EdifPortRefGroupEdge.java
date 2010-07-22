package edu.byu.ece.edif.util.graph;

import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.graph.Edge;

/**
 * This class is an Edge for EdifPortRefGroupGraph objects. It is
 * fairly similar to EdifCellInstanceEdge, with the main difference
 * being that the Edge contains references to the source and sink nodes
 * as well as the source and sink EdifPortRefs. This is necessary since
 * each EdifCellInstance may have more than one EdifPortRefGroupNode,
 * so we can't rely on simply fetching the EdifCellInstance that belongs
 * to the source and sink EdifPortRefs. In addition, all source and sink
 * nodes must be EdifPortRefGroup objects rather than just Objects,
 * 
 * @author whowes
 *
 */
public class EdifPortRefGroupEdge implements EdifPortRefEdge {
    /**
     * Construct a new link from a given source and sink EdifPortRef
     * object.
     */
    public EdifPortRefGroupEdge(EdifPortRef source, EdifPortRefGroupNode sourceNode, EdifPortRef sink, EdifPortRefGroupNode sinkNode) {
        _sourceEPR = source;
        _sinkEPR = sink;
        _sourceNode = sourceNode;
        _sinkNode = sinkNode;
    }

	public EdifNet getNet() {
		return _sinkEPR.getNet();
	}

	public EdifPortRef getSinkEPR() {
		return _sinkEPR;
	}

	public EdifPortRef getSourceEPR() {
		return _sourceEPR;
	}

    /**
     * Return the sink node object associated with this link. 
     */
    public EdifPortRefGroupNode getSink() {
        return _sinkNode;
    }

    /**
     * Return the source node object associated with this link. 
     */
    public EdifPortRefGroupNode getSource() {
    	return _sourceNode;
    }

    /**
     * Create a edge that is the inverse of this edge (i.e. the source and sinks
     * are reversed.
     */
	public Edge invert() {
        return new EdifPortRefGroupEdge(_sinkEPR, _sinkNode, _sourceEPR, _sourceNode);
	}
	
    public String toString() {
        return (_sourceEPR + "->" + _sinkEPR);
    }

    /**
     * The EdifPortRef object associated with the source of this edge.
     */
    protected EdifPortRef _sourceEPR;
    /**
     * The EdifPortRef object associated with the sink of this edge.
     */
    protected EdifPortRef _sinkEPR;
    /**
     * The EdifPortRefListNode or EdifSingleBitPort object associated with the source of this edge.
     */
    protected EdifPortRefGroupNode _sourceNode;
    /**
     * The EdifPortRefListNode or EdifSingleBitPort object associated with the sink of this edge.
     */
    protected EdifPortRefGroupNode _sinkNode;
}
