package edu.byu.ece.edif.util.graph;

import java.util.ArrayList;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPortRef;


/**
 * This class is the typical node for EdifPortRefGroupGraph. It
 * is essentially a List of EdifPortRef objects which belong to 
 * the same EdifCellInstance. It is possible for a single
 * EdifCellInstance to "own" more than one EdifPortRefGroupNode,
 * such as in cases where certain outputs of the EdifCellInstance
 * do not depend on all of the inputs.
 * 
 * This is not the only type of node that can be in an 
 * EdifPortRefGroupGraph; as in an EdifCellInstanceGraph, nodes
 * for top-level ports are represented by EdifSingleBitPort 
 * objects.
 * 
 * @author whowes
 *
 */
public class EdifPortRefGroupNode extends ArrayList<EdifPortRef> {
	
	/**
	 * Constructor
	 * 
	 * @param eci EdifCellInstance which "owns" allEdifPortRef
	 * objects in this node
	 */
	public EdifPortRefGroupNode(EdifCellInstance eci) {
		super();
		_eci = eci;
	}
	
	/**
	 * Retrieves the EdifCellInstance that owns the EdifPortRef
	 * objects in this node
	 * 
	 * @return A reference to the EdifCellInstance that "owns"
	 * all EdifPortRef objects in this node
	 */
	public EdifCellInstance getEdifCellInstance() {
		return _eci;
	}

	/**
	 * Reference to this object's EdifCellInstance
	 */
	private EdifCellInstance _eci;
	
	public String toString() {		
		//this allows equivalence testing with EdifCellInstanceGraph
		//using SCCDepthFirstSearch's createSCCString()
		return _eci.toString();
	}
}
