package edu.byu.ece.edif.util.generate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.NamedObject;


public class GenerationNet {
	
	protected static int netCount = 0;

	public GenerationNet() {
		_connections = new LinkedHashSet<InstancePort>();
		_realized = false;
	}
	
	public GenerationNet(String name) {
		this();
		_name = name;
	}
	
	public void addConnection(InstancePort connection) {
		_connections.add(connection);
	}
	
	public boolean removeConnection(InstancePort connection) {
		return _connections.remove(connection);
	}
	
	public Set<InstancePort> getConnections() {
		return Collections.unmodifiableSet(_connections);
	}
		
	public void realizeNet(EdifCell parent) {
		if (!_realized && _connections.size() < 2)
			return;
		
		EdifNameable uniqueName = null;
		if (_name == null) {
			uniqueName = parent.getUniqueNetNameable(findSuitableName());
		}
		else {
			uniqueName = parent.getUniqueNetNameable(NamedObject.createValidEdifNameable(_name));
		}
		 
		EdifNet net = new EdifNet(uniqueName, parent);
		for (InstancePort instancePort : _connections) {
			EdifPortRef portRef = new EdifPortRef(net, instancePort.getSingleBitPort(), instancePort.getInstance());
			net.addPortConnection(portRef);
		}
		try {
			parent.addNet(net);
		} catch (EdifNameConflictException e) {
			// can't get here because we already made sure the name is unique
		}
		
		_realized = true;
	}

	protected EdifNameable findSuitableName() {
		String name = null;
		InstancePort driverConnection = null;
		for (InstancePort connection : _connections) {
			if (driverConnection == null) {
				driverConnection = connection;
			}
			else {
				int direction = connection.getSingleBitPort().getParent().getDirection();
				if (direction == EdifPort.OUT || direction == EdifPort.INOUT) {
					driverConnection = connection;
				}
			}
		}
		if (driverConnection != null) {
			EdifCellInstance instance = driverConnection.getInstance();
			String instanceName = null;
			if (instance != null) {
				instanceName = instance.getName();
			}
			else {
				instanceName = "top";
			}
			name = instanceName + "_" + driverConnection.getSingleBitPort().getPortName() + "_" + driverConnection.getSingleBitPort().bitPosition();
		}
		else {
			name = "net_" + (netCount++);
		}
		EdifNameable result = NamedObject.createValidEdifNameable(name);
		return result;
	}
	
	protected Set<InstancePort> _connections;
	protected String _name = null;
	protected boolean _realized;
}
