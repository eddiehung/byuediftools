package edu.byu.ece.edif.tools.replicate.nmr.dwc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.byu.ece.edif.tools.replicate.wiring.SinglePortConnection;

/**
 * A DetectionOutputSpecification indicates what to do with a particular group
 * of detection outputs (the group could consist of multiple detection domains).
 * A DetectionOutputSpecification indicates whether to create a new port for the output
 * or use an existing one, whether to insert an output register, whether to insert an
 * output buffer, and what clock net to use for any output registers.
 */
public class DetectionOutputSpecification implements Serializable {
	
	public DetectionOutputSpecification(DetectionType type, List<SinglePortConnection> outputConnection, DetectionDomain domain, DetectionOutputMerger merger, boolean insertOreg, boolean insertObuf, String clockNetName) {
		_detectionType = type;
		_outputConnection = outputConnection;
		_detectionDomains = new ArrayList<DetectionDomain>(1);
		_detectionDomains.add(domain);
		_detectionOutputMerger = merger;
		_needsPortCreated = false;
		_portNameToCreate = null;
		_insertOreg = insertOreg;
		_insertObuf = insertObuf;
		_clockNetName = clockNetName;
	}
	
	public DetectionOutputSpecification(DetectionType type, List<SinglePortConnection> outputConnection, List<DetectionDomain> detectionDomains, DetectionOutputMerger merger, boolean insertOreg, boolean insertObuf, String clockNetName) {
		_detectionType = type;
		_outputConnection = outputConnection;
		_detectionDomains = detectionDomains;
		_detectionOutputMerger = merger;
		_needsPortCreated = false;
		_portNameToCreate = null;
		_insertOreg = insertOreg;
		_insertObuf = insertObuf;
		_clockNetName = clockNetName;
	}
	
	public DetectionOutputSpecification(DetectionType type, String portNameToCreate, DetectionDomain domain, DetectionOutputMerger merger, boolean insertOreg, boolean insertObuf, String clockNetName) {
		_detectionType = type;
		_outputConnection = null;
		_detectionDomains = new ArrayList<DetectionDomain>(1);
		_detectionDomains.add(domain);
		_detectionOutputMerger = merger;
		_needsPortCreated = true;
		_portNameToCreate = portNameToCreate;
		_insertOreg = insertOreg;
		_insertObuf = insertObuf;
		_clockNetName = clockNetName;
	}
	
	public DetectionOutputSpecification(DetectionType type, String portNameToCreate, List<DetectionDomain> detectionDomains, DetectionOutputMerger merger, boolean insertOreg, boolean insertObuf, String clockNetName) {
		_detectionType = type;
		_outputConnection = null;
		_detectionDomains = detectionDomains;
		_detectionOutputMerger = merger;
		_needsPortCreated = true;
		_portNameToCreate = portNameToCreate;
		_insertOreg = insertOreg;
		_insertObuf = insertObuf;
		_clockNetName = clockNetName;
	}
	
	
	public DetectionType getDetectionType() {
		return _detectionType;
	}
    
	public List<SinglePortConnection> getOutputConnection() {
		return _outputConnection;
	}
	
	public void addDetectionDomain(DetectionDomain domain) {
		_detectionDomains.add(domain);
	}
	
	public void addDetectionDomains(Collection<DetectionDomain> domains) {
		_detectionDomains.addAll(domains);
	}
	
	public boolean containsDomain(DetectionDomain domain) {
		return _detectionDomains.contains(domain);
	}
	
	public List<DetectionDomain> getDetectionDomains() {
		return _detectionDomains;
	}
	
	public DetectionOutputMerger getMerger() {
		return _detectionOutputMerger;
	}
	
	public boolean needsPortCreated() {
		return _needsPortCreated;
	}
	
	public String getPortNameToCreate() {
		return _portNameToCreate;
	}
	
	public boolean shouldInsertObuf() {
		return _insertObuf;
	}
	
	public boolean shouldInsertOreg() {
		return _insertOreg;
	}
	
	public String getClockNetName() {
		return _clockNetName;
	}
	
    protected DetectionType _detectionType;
    
    protected List<SinglePortConnection> _outputConnection;
    
    protected List<DetectionDomain> _detectionDomains;
    
    protected DetectionOutputMerger _detectionOutputMerger;
    
    protected boolean _needsPortCreated;
    
    protected String _portNameToCreate;
    
    protected boolean _insertOreg;
    
    protected boolean _insertObuf;
    
    protected String _clockNetName;    
    
}
