package edu.byu.ece.edif.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;


/**
 * This class is the node for EdifPortRefGroupGraph. It
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
public class EdifPortRefGroupNode {
	
	/**
	 * Constructor
	 * 
	 * @param eci EdifCellInstance which "owns" allEdifPortRef
	 * objects in this node
	 */
	public EdifPortRefGroupNode(EdifCellInstance eci, boolean isSplit) {
		_eci = eci;
		_esbp = null;
		_isSplit = isSplit;
	}
	
	/**
	 * Constructor
	 * 
	 * @param esbp EdifSingleBitPort which "owns" allEdifPortRef
	 * objects in this node
	 */
	public EdifPortRefGroupNode(EdifSingleBitPort esbp,  boolean isSplit) {
		_eci = null;
		_esbp = esbp;
		_isSplit = isSplit;
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
	
	public void add(EdifPortRef epr) {
		if (_portRefs == null) {
			_portRefs = new ArrayList<EdifPortRef>();
		}
		_portRefs.add(epr);
	}
	
	public void addAll(Collection<EdifPortRef> eprs) {
		if (_portRefs == null) {
			_portRefs = new ArrayList<EdifPortRef>();
		}
		_portRefs.addAll(eprs);
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

	public boolean isSplitNode() {
		return _isSplit;
	}
	
	/**
	 * Reference to this object's EdifCellInstance (if there is one)
	 */
	private EdifCellInstance _eci;
	
	/**
	 * Reference to this object's EdifCellInstance (if there is one)
	 */
	private EdifSingleBitPort _esbp;
	
	/**
	 * Denotes whether this node is split (if it is, the same ECI/ESBP
	 * might have multiple nodes)
	 */
	private boolean _isSplit;
	
	private ArrayList<EdifPortRef> _portRefs;
	
	public String toString() {		
		//this allows equivalence testing with EdifCellInstanceGraph
		//using SCCDepthFirstSearch's createSCCString()
		if (_eci != null) {
			return _eci.toString();
		}
		else {
			return _esbp.toString();
		}
	}
}
