package edu.byu.ece.edif.tools.flatten;

import java.io.Serializable;

import edu.byu.ece.edif.core.EdifCellInstance;

public class FlatteningNode implements Serializable {

	public FlatteningNode(EdifCellInstance origInstance) {
		_instanceNode = new InstanceNode(origInstance);
		_origInstance = origInstance;
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
	
	public EdifCellInstance getOrigInstance() {
		return _origInstance;
	}
	
	private InstanceNode _instanceNode;
	
	private EdifCellInstance _origInstance;
}
