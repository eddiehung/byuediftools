package edu.byu.ece.edif.tools.replicate.nmr.dwc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.tools.replicate.nmr.OrganSpecification;
import edu.byu.ece.edif.tools.replicate.wiring.PortConnection;

/**
 * A detection domain is a group of detectors whose outputs will all
 * be merged into a single detection output. Each output is a List of
 * PortConnections (since each detector may have a multi-bit width output).
 */
public class DetectionDomain implements Serializable {
    
	public DetectionDomain(DetectionType type) {
		_detectionType = type;
		_inputConnections = new ArrayList<List<PortConnection>>();
		_detectors = new LinkedHashSet<OrganSpecification>();
	}
	
	public void addInputConnection(List<PortConnection> connection) {
		if (connection.size() != _detectionType.getSignalWidth()) {
			throw new EdifRuntimeException("Unexpected: detection domain input does not match signal width of detection type");
		}
		_inputConnections.add(connection);
	}
	
	public List<List<PortConnection>> getInputConnections() {
		return _inputConnections;
	}
	
	public void addDetector(OrganSpecification detector) {
		_detectors.add(detector);
	}
	
	public void addDetectors(Collection<OrganSpecification> detectors) {
		_detectors.addAll(detectors);
	}
	
	public Collection<OrganSpecification> getDetectorSpecifications() {
		return _detectors;
	}
	
    protected DetectionType _detectionType;
    protected List<List<PortConnection>> _inputConnections; // each List<PortConnection> must have the same size as _detectionType.getSignalWidth()
    protected Set<OrganSpecification> _detectors;
}
