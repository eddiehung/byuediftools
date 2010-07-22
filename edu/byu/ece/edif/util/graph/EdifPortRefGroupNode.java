package edu.byu.ece.edif.util.graph;

import java.util.ArrayList;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;


/**
 * This class is the typical node for EdifPortRefGroupGraph. It
 * is essentially a List of EdifPortRef objects which belong to 
 * the same EdifCellInstance (or EdifSingleBitPort, if the node
 * represents a top-level port.) It is possible for a single
 * EdifCellInstance to "own" more than one EdifPortRefGroupNode,
 * such as in cases where certain outputs of the EdifCellInstance
 * do not depend on all of the inputs.
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
		_esbp = null;
	}
	
	/**
	 * Constructor
	 * 
	 * @param esbp EdifSingleBitPort which "owns" allEdifPortRef
	 * objects in this node
	 */
	public EdifPortRefGroupNode(EdifSingleBitPort esbp) {
		super();
		_eci = null;
		_esbp = esbp;
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
	 * Retrieves the EdifCellInstance that owns the EdifPortRef
	 * objects in this node
	 * 
	 * @return A reference to the EdifCellInstance that "owns"
	 * all EdifPortRef objects in this node
	 */
	public EdifSingleBitPort getEdifSingleBitPort() {
		return _esbp;
	}

	/**
	 * Reference to this object's EdifCellInstance (if there is one)
	 */
	private EdifCellInstance _eci;
	
	/**
	 * Reference to this object's EdifCellInstance (if there is one)
	 */
	private EdifSingleBitPort _esbp;
	
	public String toString() {		
		//this allows equivalence testing with EdifCellInstanceGraph
		//using SCCDepthFirstSearch's createSCCString()
		return _eci.toString();
	}
}
