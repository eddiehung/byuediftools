package edu.byu.ece.edif.util.generate;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPrintWriter;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.NamedObject;


public class CircuitGenerator {

	public CircuitGenerator(String name) {
		_name = name;
		_portMap = new LinkedHashMap<String, EdifPort>();
		_netMap = new LinkedHashMap<InstancePort, GenerationNet>();
		_generationNets = new LinkedHashSet<GenerationNet>();
		
		
		try {
			_environment = new EdifEnvironment(name);
			_mainLibrary = new EdifLibrary(_environment.getLibraryManager(), "work");
			_topCell = new EdifCell(_mainLibrary, name);
			_topDesign = new EdifDesign(name);
			_topDesign.setTopCellInstance(new EdifCellInstance(name, null, _topCell));
			_environment.setTopDesign(_topDesign);
			
		} catch (InvalidEdifNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EdifNameConflictException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		_environment.setVersion("0.1");
		_environment.setProgram("BYU EDIF Circuit Generator");
		_environment.setAuthor("BYU Configurable Computing Laboratory");
	}
	
	public String getName() {
		return _name;
	}
	
	public EdifCellInstance addXilinxInstance(String primName, String instanceName) {
		EdifCell xilinxCell = getXilinxCell(primName);
		EdifNameable uniqueName = _topCell.getUniqueInstanceNameable(NamedObject.createValidEdifNameable(instanceName));
		EdifCellInstance instance = new EdifCellInstance(uniqueName, _topCell, xilinxCell);
		addInstance(instance);
		return instance;
	}
	
	public void addInstance(EdifCellInstance instance) {
		_topCell.addSubCellUniqueName(instance);
	}
	
	public void addPort(String name, int direction) {
		addPort(name, 1, direction);
	}
	
	public void addPort(String name, int width, int direction) {
		if (_portMap.containsKey(name)) {
			throw new EdifRuntimeException("Trying to add a duplicate port: " + name + " to " + _topCell);
		}
		EdifNameable validName = NamedObject.createValidEdifNameable(name);
		EdifPort port = _topCell.addPortUniqueName(validName, width, direction);
		_portMap.put(name, port);
	}
	
	public InstancePort getPort(String name) {
		return getPort(name, 0);
	}
	
	public InstancePort getPort(String name, int bitIndex) {
		EdifPort port = _portMap.get(name);
		EdifSingleBitPort esbp = port.getSingleBitPort(bitIndex);
		return InstancePort.get(esbp, null);
	}
	
	public EdifCell getXilinxCell(String name) {
		return XilinxLibrary.findOrAddXilinxPrimitive(_mainLibrary.getLibraryManager(), name);
	}
	
	public void connect(EdifCellInstance inst0, String port0_name, EdifCellInstance inst1, String port1_name) {
		EdifCell cell0 = inst0.getCellType();
		EdifCell cell1 = inst1.getCellType();
		EdifPort port0 = cell0.getPort(port0_name);
		EdifPort port1 = cell1.getPort(port1_name);
		
		InstancePort connection0 = InstancePort.get(port0, inst0);
		InstancePort connection1 = InstancePort.get(port1, inst1);
		connect(connection0, connection1);
	}
	
	public void connect(EdifCellInstance inst0, String port0_name, int index0, EdifCellInstance inst1, String port1_name, int index1) {
		EdifCell cell0 = inst0.getCellType();
		EdifCell cell1 = inst1.getCellType();
		EdifPort port0 = cell0.getPort(port0_name);
		EdifPort port1 = cell1.getPort(port1_name);
		EdifSingleBitPort esbp0 = port0.getSingleBitPort(index0);
		EdifSingleBitPort esbp1 = port1.getSingleBitPort(index1);
		
		InstancePort connection0 = InstancePort.get(esbp0, inst0);
		InstancePort connection1 = InstancePort.get(esbp1, inst1);
		connect(connection0, connection1);
	}
	
	public void connect(EdifCellInstance inst0, String port0_name, InstancePort connection1) {
		EdifCell cell0 = inst0.getCellType();
		EdifPort port0 = cell0.getPort(port0_name);

		InstancePort connection0 = InstancePort.get(port0, inst0);
		connect(connection0, connection1);
	}

	public void connect(EdifCellInstance inst0, String port0_name, int index0, InstancePort connection1) {
		EdifCell cell0 = inst0.getCellType();
		EdifPort port0 = cell0.getPort(port0_name);
		EdifSingleBitPort esbp0 = port0.getSingleBitPort(index0);
		
		InstancePort connection0 = InstancePort.get(esbp0, inst0);
		connect(connection0, connection1);
	}
	
	public void connect(InstancePort connection0, InstancePort connection1) {
		
		if (connection0 == connection1) {
			return;
		}
		
		GenerationNet net0 = _netMap.get(connection0);
		GenerationNet net1 = _netMap.get(connection1);
		
		if (net0 == null && net1 == null) {
			GenerationNet net = new GenerationNet();
			net.addConnection(connection0);
			net.addConnection(connection1);
			_netMap.put(connection0, net);
			_netMap.put(connection1, net);
			_generationNets.add(net);
		}
		
		else if (net0 != null && net1 == null) {
			net0.addConnection(connection1);
			_netMap.put(connection1, net0);
		}
		
		else if (net0 == null && net1 != null) {
			net1.addConnection(connection0);
			_netMap.put(connection0, net1);
		}
		
		else { // net0 != null && net1 != null
			Set<InstancePort> connections1 = net1.getConnections();
			for (InstancePort connection : connections1) {
				net0.addConnection(connection);
				_netMap.put(connection, net0);
			}
			_generationNets.remove(net1);
		}
	}
	
	public InstancePort globalGND() {
		if (_globalGND == null) {
			EdifCellInstance gnd = addXilinxInstance("GND", "gnd");
			_globalGND = InstancePort.get(gnd.getCellType().getPort("G"), gnd);
		}
		return _globalGND;
	}
	
	public InstancePort globalVCC() {
		if (_globalVCC == null) {
			EdifCellInstance vcc = addXilinxInstance("VCC", "vcc");
			_globalVCC = InstancePort.get(vcc.getCellType().getPort("P"), vcc);
		}
		return _globalVCC;
	}
	
	public void realizeNets() {
		for (GenerationNet net : _generationNets) {
			net.realizeNet(_topCell);
		}
	}
	
	public void toEdif(String fileName) throws IOException {
		EdifPrintWriter epw = new EdifPrintWriter(fileName);
		_environment.toEdif(epw);
	}
	
	protected String _name;
	
	protected EdifEnvironment _environment;
	protected EdifCell _topCell;
	protected EdifDesign _topDesign;
	protected EdifLibrary _mainLibrary;
	
	protected Map<String, EdifPort> _portMap;
	
	protected Map<InstancePort, GenerationNet> _netMap;
	protected Set<GenerationNet> _generationNets;
	
	protected InstancePort _globalGND = null;
	protected InstancePort _globalVCC = null;
}
