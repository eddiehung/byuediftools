package edu.byu.ece.edif.util.generate;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifSingleBitPort;


public abstract class Module {

	public Module(String name, CircuitGenerator parent) {
		_name = name;
		_parent = parent;
		_ports = new LinkedHashMap<String, Map<Integer, InstancePort>>();
		_instances = new LinkedHashSet<EdifCellInstance>();
	}
	

	
	protected abstract void _buildModule();
	
	public InstancePort getPort(String name) {
		return _ports.get(name).get(0);
	}
	
	public InstancePort getPort(String name, int index) {
		return _ports.get(name).get(index);
	}
	
	protected void addPort(String name, int outerIndex, EdifCellInstance instance, String instancePortName, int innerIndex) {
		EdifCell cell = instance.getCellType();
		EdifPort instancePort = cell.getPort(instancePortName);
		EdifSingleBitPort esbp = instancePort.getSingleBitPort(innerIndex);
		InstancePort connection = InstancePort.get(esbp, instance);
		connectPort(name, outerIndex, connection);
	}
	
	protected void connectPort(String name, int outerIndex, InstancePort connection) {
		Map<Integer, InstancePort> innerMap = _ports.get(name);
		if (innerMap == null) {
			innerMap = new LinkedHashMap<Integer, InstancePort>();
			_ports.put(name, innerMap);
		}
		InstancePort existingConnection = innerMap.get(outerIndex);
		if (existingConnection != null) {
			_parent.connect(connection, existingConnection);
		}
		else {
			innerMap.put(outerIndex, connection);
		}
	}

	protected void addPort( String name, int outerIndex, EdifCellInstance instance, String instancePortName) {
		addPort(name, outerIndex, instance, instancePortName, 0);
	}
	
	protected void addPort(String name, EdifCellInstance instance, String instancePortName, int innerIndex) {
		addPort(name, 0, instance, instancePortName, innerIndex);
	}
	
	protected void addPort(String name, EdifCellInstance instance, String instancePortName) {
		addPort(name, 0, instance, instancePortName, 0);
	}
	
	protected void addPort(String name, InstancePort connection) {
		connectPort(name, 0, connection);
	}
	
	protected String _name;
	protected CircuitGenerator _parent;
	protected Map<String, Map<Integer, InstancePort>> _ports;
	protected Set<EdifCellInstance> _instances;
	
}
