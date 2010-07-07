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
 * to the source and sink EdifPortRefs.
 * 
 * @author whowes
 *
 */
public class EdifPortRefGroupEdge implements EdifPortRefEdge {
    /**
     * Construct a new link from a given source and sink EdifPortRef
     * object.
     */
    public EdifPortRefGroupEdge(EdifPortRef source, Object sourceNode, EdifPortRef sink, Object sinkNode) {
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
     * Return the sink node object associated with this link. If the sink is a
     * top-level port, this method will return the EdifSingleBitPort associated
     * with this link. If the sink is not a top-level port, this method will
     * return the EdifPortRefListNode associated with the source EdifPortRef
     * object.
     */
    public Object getSink() {
        return _sinkNode;
    }

    /**
     * Return the source node object associated with this link. If the source is
     * a top-level port, this method will return the EdifSingleBitPort
     * associated with this link. If the source is not a top-level port, this
     * method will return the EdifPortRefListNode associated with the source
     * EdifPortRef object.
     */
    public Object getSource() {
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
    protected Object _sourceNode;
    /**
     * The EdifPortRefListNode or EdifSingleBitPort object associated with the sink of this edge.
     */
    protected Object _sinkNode;
}
