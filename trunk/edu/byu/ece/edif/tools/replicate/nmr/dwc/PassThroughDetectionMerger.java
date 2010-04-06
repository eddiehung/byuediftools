package edu.byu.ece.edif.tools.replicate.nmr.dwc;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.tools.replicate.wiring.NetManager;
import edu.byu.ece.edif.tools.replicate.wiring.PortConnection;

public class PassThroughDetectionMerger extends DetectionOutputMerger {


	protected PassThroughDetectionMerger() {

	}

	public static PassThroughDetectionMerger getInstance() {
		if (_instance == null) {
			_instance = new PassThroughDetectionMerger();
		}
		return _instance;
	}

	@Override
	public List<PortConnection> mergeOutputs(List<List<PortConnection>> outputs, EdifCell topCell, NetManager netManager) {
		if (outputs.size() < 1)
			return null;
		// Just put all of the PortConnections into a single list
		// We want them all to be top level outputs
		List<PortConnection> outputPortConnections = new ArrayList<PortConnection>();

		for (List<PortConnection> output : outputs) {
			for (PortConnection portConn : output)
				outputPortConnections.add(portConn);
		}
		
		return outputPortConnections;
	}

	/**
	 * This method ensures that during deserialization, the _instance variable will be referred
	 * to and possibly modified so that extra instances (more than necessary) don't get created.
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		PassThroughDetectionMerger instance = _instance;
		if (instance == null) {
			instance = getInstance();
		}
		return instance;
	}
	
	protected static PassThroughDetectionMerger _instance;
}
