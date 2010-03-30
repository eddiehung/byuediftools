package edu.byu.ece.edif.util.generate;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifSingleBitPort;


public class InstancePort {

	public static Map<EdifCellInstance, Map<EdifSingleBitPort, InstancePort>> _cache = new LinkedHashMap<EdifCellInstance, Map<EdifSingleBitPort, InstancePort>>();
	
	public static InstancePort get(EdifPort port, EdifCellInstance instance) {
		EdifSingleBitPort esbp = port.getSingleBitPort(0);
		return get(esbp, instance);
	}
	
	public static InstancePort get(EdifPort port, int bitIndex, EdifCellInstance instance) {
		EdifSingleBitPort esbp = port.getSingleBitPort(bitIndex);
		return get(esbp, instance);
	}
	
	public static InstancePort get(EdifSingleBitPort esbp, EdifCellInstance instance) {
		Map<EdifSingleBitPort, InstancePort> innerMap = _cache.get(instance);
		if (innerMap == null) {
			innerMap = new LinkedHashMap<EdifSingleBitPort, InstancePort>();
			_cache.put(instance, innerMap);
		}
		
		InstancePort connection = innerMap.get(esbp);
		if (connection == null) {
			connection = new InstancePort(esbp, instance);
			innerMap.put(esbp, connection);
		}
		return connection;
	}
	
	private InstancePort(EdifSingleBitPort esbp, EdifCellInstance instance) {
		_esbp = esbp;
		_instance = instance;
	}
	
	public EdifSingleBitPort getSingleBitPort() {
		return _esbp;
	}
	
	public EdifCellInstance getInstance() {
		return _instance;
	}
	
	protected EdifSingleBitPort _esbp;
	protected EdifCellInstance _instance;
}
