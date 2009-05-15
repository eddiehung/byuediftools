package edu.byu.ece.edif.tools.replicate.nmr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DetectionDomain;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DetectionOutputMerger;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DetectionOutputSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DetectionType;
import edu.byu.ece.edif.tools.replicate.wiring.PreMitigatedPortGroup;
import edu.byu.ece.edif.tools.replicate.wiring.SinglePortConnection;
import edu.byu.ece.edif.tools.replicate.wiring.WiringPolicy;

/**
 * A ReplicationDescription persists across multiple executable runs (it gets serialized) and
 * contains information about what types of replication to perform, where to insert voters,
 * where to insert detectors, what do to with detection error signal outputs, a feedback cutset,
 * etc.
 */
public class ReplicationDescription implements Serializable {

    /**
     * Create an empty ReplicationDescription.
     */
    public ReplicationDescription() {

        _instancesToReplicate = new LinkedHashMap<EdifCellInstance, ReplicationType>();

        _portsToReplicate = new LinkedHashMap<EdifPort, ReplicationType>();

        _organsToInsert = new LinkedHashMap<EdifNet, Set<OrganSpecification>>();
        
        _wiringPolicies = new LinkedHashMap<EdifPortRef, WiringPolicy>();
        
        _detectionDomains = new LinkedHashSet<DetectionDomain>();
        
        _detectionOutputSpecifications0 = new LinkedHashMap<List<SinglePortConnection>, DetectionOutputSpecification>();
        
        _detectionOutputSpecifications1 = new LinkedHashMap<String, DetectionOutputSpecification>();
        
        _portGroups = new LinkedHashMap<EdifPort, PreMitigatedPortGroup>();
        
        _alreadySetPortGroups = false;
        
        _instancesToIgnore = new LinkedHashSet<EdifCellInstance>();
        
        _netsToIgnore = new LinkedHashSet<EdifNet>();
        
        _portRefsToIgnore = new LinkedHashMap<EdifNet, Set<EdifPortRef>>();
        
        _cutsetReference = null;

    }
    
    /**
     * Mark the given EdifCellInstance with the specified ReplicationType.
     *
     * Previous assignments will be overridden.
     * 
     * @param instance
     * @param replicationType
     */
    public void addInstance(EdifCellInstance instance, ReplicationType replicationType) {
       _instancesToReplicate.put(instance, replicationType); 
    }

    /**
     * Mark the given EdifCellInstances with the specified ReplicationType.
     *
     * Previous assignments will be overridden.
     * 
     * @param instances
     * @param replicationType
     */
    public void addInstances(Collection<EdifCellInstance> instances, ReplicationType replicationType) {
       for (EdifCellInstance instance : instances) {
           addInstance(instance, replicationType);
       }
    }

    /**
     * Associate an OrganSpecification with the given EdifNet. Multiple
     * OrganSpecifications may be associated with each net.
     * 
     * @param net
     * @param organSpec
     */
    public void addOrganSpecification(EdifNet net, OrganSpecification organSpec) {
        Set<OrganSpecification> specList = _organsToInsert.get(net);
        if (specList == null) {
            specList = new LinkedHashSet<OrganSpecification>();
            _organsToInsert.put(net, specList);
        }
        specList.add(organSpec);
    }
    
    public void addOrganSpecifications(EdifNet net, Collection<OrganSpecification> organSpecs) {
    	if (organSpecs == null)
    		return;
    	Set<OrganSpecification> specList = _organsToInsert.get(net);
    	if (specList == null) {
    		specList = new LinkedHashSet<OrganSpecification>();
    		_organsToInsert.put(net, specList);
    	}
    	specList.addAll(organSpecs);
    }

    /**
     * Mark the given EdifPort with the specified ReplicationType.
     * 
     * Previous assignments will be overridden.
     * 
     * @param port
     * @param replicationType
     */
    public void addPort(EdifPort port, ReplicationType replicationType) {
        _portsToReplicate.put(port, replicationType);
    }

    /**
     * Mark the given EdifPorts with the specified ReplicationType.
     * 
     * Previous assignments will be overridden.
     * 
     * @param ports
     * @param replicationType
     */
    public void addPorts(Collection<EdifPort> ports, ReplicationType replicationType) {
        for (EdifPort port : ports) {
            addPort(port, replicationType);
        }
    }

    /**
     * Associate the given WiringPolicy with the given EdifPortRef.
     * 
     * @param portRef
     * @param wiringPolicy
     */
    public void addWiringPolicy(EdifPortRef portRef, WiringPolicy wiringPolicy) {
        _wiringPolicies.put(portRef, wiringPolicy);
    }
    
    public Map<EdifCellInstance, ReplicationType> getInstanceReplicationMap() {
    	return new LinkedHashMap<EdifCellInstance, ReplicationType>(_instancesToReplicate);
    }
    
    public void setInstanceReplicationMap(Map<EdifCellInstance, ReplicationType> instancesToReplicate) {
    	_instancesToReplicate = new LinkedHashMap<EdifCellInstance, ReplicationType>(instancesToReplicate);
    }
    
    /**
     * Get the OrganSpecifications associated with the given EdifNet. Return <code>null</code>
     * if there are no OrganSpecifications associated with the net.
     * 
     * @param net
     * @return
     */
    public Set<OrganSpecification> getOrganSpecifications(EdifNet net) {
        return _organsToInsert.get(net);
    }
    
    public Map<EdifNet, Set<OrganSpecification>> getOrganSpecifications() {
    	return new LinkedHashMap<EdifNet, Set<OrganSpecification>>(_organsToInsert);
    }

    /**
     * Get the ReplicationType associated with the given EdifPort. Return <code>null</code>
     * if there is no ReplicationType associated with the EdifPort.
     * 
     * @param port
     * @return
     */
    public ReplicationType getReplicationType(EdifPort port) {
        return _portsToReplicate.get(port);
    }
    
    public Map<EdifPort, ReplicationType> getPortReplicationMap() {
    	return new LinkedHashMap<EdifPort, ReplicationType>(_portsToReplicate);
    }

    /**
     * Get the ReplicationType associated with the given EdifCellInstance. Return <code>null</code>
     * if there is no ReplicationType associated with the EdifPort.
     * 
     * @param instance
     * @return
     */
    public ReplicationType getReplicationType(EdifCellInstance instance) {
        return _instancesToReplicate.get(instance);
    }
    
    /**
     * Get the ReplicationType associated with the given source/sink connection. This method
     * should never return <code>null</code>
     * 
     * @param epr
     * @return
     */
    public ReplicationType getReplicationType(EdifPortRef epr) {
    	if (!_alreadySetPortGroups)
    		throw new EdifRuntimeException("Error: Attempting to get the replication type of a portRef without first setting port group information. (Did you run JEdifNMRSelection yet?)");
        ReplicationType result = null;
        EdifCellInstance eci = epr.getCellInstance();
        EdifPort port = epr.getPort();
        PreMitigatedPortGroup portGroup = _portGroups.get(port);
        if (eci == null) {
            if (portGroup != null) {
                result = portGroup.getReplicationType();
            }
            else {
                result = getReplicationType(port);
                if (result == null)
                    result = UnityReplicationType.getInstance();
            }
        }
        else {
            if (EdifReplicationPropertyReader.isPreMitigatedInstance(eci)) {
                if (portGroup == null)
                    result = UnityReplicationType.getInstance();
                else {
                	result = portGroup.getReplicationType();
                }
            }
            else {
                result = getReplicationType(eci);
                if (result == null)
                	result = UnityReplicationType.getInstance();
            }
        }
        return result;
    }
    
    /**
     * Get the WiringPolicy associated with the given EdifPortRef. Return <code>null</code>
     * if there is no WiringPolicy associated with the EdifPortRef.
     * 
     * @param portRef
     * @return
     */
    public WiringPolicy getWiringPolicy(EdifPortRef portRef) {
       return _wiringPolicies.get(portRef); 
    }
    
    /**
     * Add a detection organ specification. This has two effects. The first is to put
     * the organ specification into the Net->OrganSpecifications map. The second is to
     * associate the organ specification with the given detection domain in the list
     * of detection domains.
     * 
     * @param detectionDomain
     * @param net
     * @param organSpecification
     */
    public void addDetectionOrganSpecification(DetectionDomain detectionDomain, EdifNet net, OrganSpecification organSpecification) {
        if (!_detectionDomains.contains(detectionDomain))
        	_detectionDomains.add(detectionDomain);
        detectionDomain.addDetector(organSpecification);
        addOrganSpecification(net, organSpecification);       
    }
    
    public void addDetectionOrganSpecifications(DetectionDomain detectionDomain, EdifNet net, List<OrganSpecification> organSpecifications) {
    	if (!_detectionDomains.contains(detectionDomain))
    		_detectionDomains.add(detectionDomain);
    	detectionDomain.addDetectors(organSpecifications);
    	addOrganSpecifications(net, organSpecifications);
    }
    
    public void associateDetectionOutputWithDomain(DetectionType detectionType, List<SinglePortConnection> outputConnection, DetectionDomain domain, DetectionOutputMerger merger, boolean insertOreg, boolean insertObuf, String clockNetName) {
    	List<DetectionDomain> domains = new ArrayList<DetectionDomain>(1);
    	domains.add(domain);
    	associateDetectionOutputWithDomains(detectionType, outputConnection, domains, merger, insertOreg, insertObuf, clockNetName);
    }
    
    public void associateDetectionOutputWithDomain(DetectionType detectionType, String portNameToCreate, DetectionDomain domain, DetectionOutputMerger merger, boolean insertOreg, boolean insertObuf, String clockNetName) {
    	List<DetectionDomain> domains = new ArrayList<DetectionDomain>(1);
    	domains.add(domain);
    	associateDetectionOutputWithDomains(detectionType, portNameToCreate, domains, merger, insertOreg, insertObuf, clockNetName);
    }
    
    public void associateDetectionOutputWithDomains(DetectionType detectionType, List<SinglePortConnection> outputConnection, List<DetectionDomain> domains, DetectionOutputMerger merger, boolean insertOreg, boolean insertObuf, String clockNetName) {
    	DetectionOutputSpecification dos = null;//_detectionOutputSpecifications.get(outputConnection);
    	for (List<SinglePortConnection> oc : _detectionOutputSpecifications0.keySet()) {
    		DetectionOutputSpecification os = _detectionOutputSpecifications0.get(oc);
    		if (oc.equals(outputConnection))
    			dos = os;
    	}
    	if (dos == null) {
    		dos = new DetectionOutputSpecification(detectionType, outputConnection, domains, merger, insertOreg, insertObuf, clockNetName);
    		_detectionOutputSpecifications0.put(outputConnection, dos);
    	}
    	else {
    		if (!merger.equals(dos.getMerger()))
    			throw new EdifRuntimeException("Error: cannot mix detection mergers for a single detection output port");
    		if (dos.getDetectionType().getSignalWidth() != detectionType.getSignalWidth())
    			throw new EdifRuntimeException("Error: cannot mix detection signal widths (rail type)");
    		dos.addDetectionDomains(domains);
    	}
    }
    
    public void associateDetectionOutputWithDomains(DetectionType detectionType, String portNameToCreate, List<DetectionDomain> domains, DetectionOutputMerger merger, boolean insertOreg, boolean insertObuf, String clockNetName) {
    	DetectionOutputSpecification dos = _detectionOutputSpecifications1.get(portNameToCreate);
    	if (dos == null) {
    		dos = new DetectionOutputSpecification(detectionType, portNameToCreate, domains, merger, insertOreg, insertObuf, clockNetName);
    		_detectionOutputSpecifications1.put(portNameToCreate, dos);
    	}
    	else {
    		if (!merger.equals(dos.getMerger()))
    			throw new EdifRuntimeException("Error: cannot mix detection mergers for a single detection output port");
    		if (dos.getDetectionType().getSignalWidth() != detectionType.getSignalWidth())
    			throw new EdifRuntimeException("Error: cannot mix detection signal widths (rail type)");
    		dos.addDetectionDomains(domains);
    	}
    }
    
    public Collection<DetectionOutputSpecification> getDetectionOutputSpecifications() {
    	List<DetectionOutputSpecification> result = new ArrayList<DetectionOutputSpecification>();
    	result.addAll(_detectionOutputSpecifications0.values());
    	result.addAll(_detectionOutputSpecifications1.values());
        return result;
    }
    
    public void setPortGroups(Collection<PreMitigatedPortGroup> portGroups) {
        for (PreMitigatedPortGroup portGroup : portGroups) {
        	for (EdifPort port : portGroup.getPorts()) {
        		PreMitigatedPortGroup prevGroup = _portGroups.get(port);
        		if (prevGroup != null)
        			throw new EdifRuntimeException("Error: each port can only be part of a single port group.");
        		_portGroups.put(port, portGroup);
        	}
        }
        _alreadySetPortGroups = true;
    }
    
    public PreMitigatedPortGroup getPortGroup(EdifPort port) {
    	return _portGroups.get(port);
    }
    
    public boolean isPreMitigatedPort(EdifPort port) {
        return (_portGroups.get(port) != null);
    }
    
    public boolean alreadySetPortGroups() {
    	return _alreadySetPortGroups;
    }
    
    public void markInstancesToIgnore(Collection<EdifCellInstance> instances) {
        _instancesToIgnore.addAll(instances);
    }
    
    public void markNetsToIgnore(Collection<EdifNet> nets) {
        _netsToIgnore.addAll(nets);
    }
    
    public void markPortRefsToIgnore(Map<EdifNet, Set<EdifPortRef>> portRefs) {
        _portRefsToIgnore.putAll(portRefs);
    }
    
    public boolean shouldIgnoreInstance(EdifCellInstance eci) {
        return _instancesToIgnore.contains(eci);
    }
    
    public boolean shouldIgnoreNet(EdifNet net) {
        return _netsToIgnore.contains(net);
    }
    
    public boolean shouldIgnorePortRefsInNet(EdifNet net) {
        return _portRefsToIgnore.containsKey(net);
    }
    
    public Collection<EdifPortRef> getPortRefsToIgnore(EdifNet net) {
        Set<EdifPortRef> result = _portRefsToIgnore.get(net);
        if (result == null)
            result = new HashSet<EdifPortRef>(0);
        return result;
    }
    
    public boolean shouldIgnorePortRef(EdifNet net, EdifPortRef epr) {
        boolean result = false;
        Set<EdifPortRef> eprs = _portRefsToIgnore.get(net);
        if (eprs != null) {
            if (eprs.contains(epr))
                result = true;
        }
        return result;
    }
    
    public void setCutsetReference(Collection<EdifPortRef> cutset) {
    	_cutsetReference = cutset;
    }
    
    public Collection<EdifPortRef> getCutsetReference() {
    	return _cutsetReference;
    }
    
    protected Map<EdifCellInstance, ReplicationType> _instancesToReplicate;

    protected Map<EdifPort, ReplicationType> _portsToReplicate;

    protected Map<EdifNet, Set<OrganSpecification>> _organsToInsert;
    
    protected Map<EdifPortRef, WiringPolicy>  _wiringPolicies; // wiring policy for each sink (default if unspecified)
    
    protected Set<DetectionDomain> _detectionDomains;
    
    protected Map<List<SinglePortConnection>, DetectionOutputSpecification> _detectionOutputSpecifications0;
    
    protected Map<String, DetectionOutputSpecification> _detectionOutputSpecifications1;
    
    protected boolean _alreadySetPortGroups;
    
    protected Map<EdifPort, PreMitigatedPortGroup> _portGroups;
    
    protected Set<EdifCellInstance> _instancesToIgnore;
    
    protected Set<EdifNet> _netsToIgnore;
    
    protected Map<EdifNet, Set<EdifPortRef>> _portRefsToIgnore;
    
    /**
     * This cutset is included for reference only. (Useful for persistence detection in
     * JEdifDetectionSelection and also for JEdifMoreFrequentVoting). This list is
     * NOT used to create voters directly. It is used (before even becoming part
     * of the ReplicationDescription) to create OrganSpecifications which are added
     * to the ReplicationDescription and then used to create voters.
     */
    protected Collection<EdifPortRef> _cutsetReference;

}
