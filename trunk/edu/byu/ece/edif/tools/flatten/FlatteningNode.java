package edu.byu.ece.edif.tools.flatten;

import java.io.Serializable;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;


/**
 * Represents an EdifCellInstance during cell flattening. Note that while this
 * usually represents an instance in the hierarchy (including the top-level instance),
 * it can be created from an EdifCell. A FlatteningNode will only be created from an
 * EdifCell when an EdifCell is to be flattened but there is no corresponding
 * EdifCellInstance for the EdifCell (i.e., when you want to create a flattened
 * cell from an EdifCell that is not the top-level EdifCellInstance).
 * 
 * TODO:
 * - This class seems similar to InstanceNode class. Why is this needed instead? Seems redundant.
 */
public class FlatteningNode implements Serializable {

	public FlatteningNode(EdifCell origCell) {
		_instanceNode = new InstanceNode(null, origCell);
		_origInstance = null;
		_origCell = origCell;
	}
	
	public FlatteningNode(EdifCellInstance origInstance) {
		_instanceNode = new InstanceNode(origInstance);
		_origInstance = origInstance;
		_origCell = null;
	}
	
	public FlatteningNode(InstanceNode instanceNode, EdifCellInstance origInstance) {
		_instanceNode = instanceNode;
		_origInstance = origInstance;
	}
	
	
	
	public FlatteningNode addChild(EdifCellInstance childInstance) {
		InstanceNode childInstanceNode = _instanceNode.addChild(childInstance);
		return new FlatteningNode(childInstanceNode, childInstance);
	}
	
	public InstanceNode getInstanceNode() {
		return _instanceNode;
	}

	public EdifCell getCellType() {
		if (_origInstance == null)
			return _origCell;
		return _origInstance.getCellType();
	}
	
	public EdifCellInstance getOrigInstance() {
		return _origInstance;
	}
	
	private InstanceNode _instanceNode;
	
	private EdifCellInstance _origInstance;

	private EdifCell _origCell;
	
}
