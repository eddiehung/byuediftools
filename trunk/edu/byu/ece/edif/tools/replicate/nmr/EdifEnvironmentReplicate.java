package edu.byu.ece.edif.tools.replicate.nmr;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DetectionDomain;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DetectionOutputMerger;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DetectionOutputSpecification;
import edu.byu.ece.edif.tools.replicate.wiring.ModuloIterationWiringPolicy;
import edu.byu.ece.edif.tools.replicate.wiring.MultiPortConnection;
import edu.byu.ece.edif.tools.replicate.wiring.NetManager;
import edu.byu.ece.edif.tools.replicate.wiring.PortConnection;
import edu.byu.ece.edif.tools.replicate.wiring.PreMitigatedPortGroup;
import edu.byu.ece.edif.tools.replicate.wiring.SinglePortConnection;
import edu.byu.ece.edif.tools.replicate.wiring.WiringPolicy;
import edu.byu.ece.edif.tools.sterilize.lutreplace.EdifEnvironmentCopy;

/**
 * This class extends the EdifEnvironmentCopy to provide design replication. An entire
 * EdifEnvironment is copied, but replicated copies of various Edif objectsare made where appropriate. Just as
 * the EdifEnvironmentCopy class, the EdifEnvironmentReplicate class performs the replication/copy starting
 * with the createEdifEnvironment() method (called from the replicate() method in this case).
 * This starts a recursive call tree including calls to createTopCell, copyEdifCell, addEdifPorts,
 * addChildEdifCellInstances, addChildEdifCellInstance, addNets, and addNet.
 * 
 * A design that is replicated using this class may or may not be completely flattened. In any
 * case, replication will occur only at the top-level. That is, cells that contain hierarchy will
 * be replicated as an atomic unit. 
 * 
 * To make this work, the EdifEnvironmentReplicate class overrides the addEdifPorts,
 * addChildEdifCellInstance, and addNet methods to do port, instance, and net replication if the cell
 * being operated on is the top cell but call the super (EdifEnvironmentCopy) methods otherwise. The
 * copyEdifCell method is also overridden to do what the super method would do then additionally maintain a port
 * mapping needed for the wiring step (wiring is performed in addNet). Also, the copyEdifCell method calls
 * the addOrgans method (if operating on the top cell) before getting to the addNets step. A getNewEdifCell(EdifCell)
 * method has also been added to see if an EdifCell has already been copied to the new EdifEnvironment and
 * return it if it has but copy and then return it if it hasn't.
 */
public class EdifEnvironmentReplicate extends EdifEnvironmentCopy {
    
    public EdifEnvironmentReplicate(EdifEnvironment oldEnvironment, ReplicationDescription desc, NMRArchitecture arch) throws EdifNameConflictException {
        this(oldEnvironment, desc, arch, null);
    }
    
    public EdifEnvironmentReplicate(EdifEnvironment oldEnvironment, ReplicationDescription desc, NMRArchitecture arch, EdifNameable topCellName) throws EdifNameConflictException {
        super(oldEnvironment);
        _origTopCell = oldEnvironment.getTopCell();
        _desc = desc;
        _instanceMap = new LinkedHashMap<EdifCellInstance, List<EdifCellInstance>>();
        _netMap = new LinkedHashMap<EdifNet, List<EdifNet>>();
        _replicatedPortMap = new LinkedHashMap<EdifPort, List<EdifPort>>();
        _arch = arch;
        _topCellName = topCellName;
    }
    
    /**
     * Perform the replication specified by the ReplicationDescription given in the
     * constructor. This is the public method that must be called to perform the
     * replication.
     * 
     * @return
     * @throws EdifNameConflictException
     */
    public EdifEnvironment replicate() throws EdifNameConflictException {
    	// Call the super.createEdifEnvironment
    	createEdifEnvironment();       
    	return _newEnv;
    }

    /**
	 * Do the same things as the super (EdifEnvironmentCopy) method but also maintain a replicated
	 * port
	 * mapping of old to new ports. Create the NetManager that will be used in the wiring
	 * algorithm if this is the top cell. Also, call the addOrgans method if operating
	 * on the top cell.
	 */
	public EdifCell copyEdifCell(EdifCell origCell, EdifLibrary destLibrary, EdifNameable name) throws EdifNameConflictException {
	    
	    EdifNameable topCellName = name;
	    if (origCell == _origTopCell && _topCellName != null) {
	        topCellName = _topCellName;
	    }
	    EdifCell newCell = new EdifCell(destLibrary, topCellName);
	    _cellMap.put(origCell, newCell);
	    // copy properties
	    newCell.copyProperties(origCell);
	    // copy primitive status
	    if (origCell.isPrimitive())
	        newCell.setPrimitive();
	    if (origCell == _origTopCell) {
	        EdifDesign oldDesign = _origEnv.getTopDesign();
	        EdifCellInstance newTopInstance = new EdifCellInstance(newCell.getEdifNameable(), null, newCell);
	        EdifDesign newDesign = new EdifDesign(newCell.getEdifNameable());
	        newDesign.setTopCellInstance(newTopInstance);
	        // copy design properties
	        newDesign.copyProperties(oldDesign);
	        _newEnv.setTopDesign(newDesign);
	    }
	    
	    // Call the local version of addEdifPorts
	    addEdifPorts(origCell, newCell);
	
	    // maintain port mapping form old environment to new environment/new copies
	    if (origCell != _origTopCell) {
	    	for (EdifPort oldPort : origCell.getPortList()) {
	    		List<EdifPort> newPorts = new ArrayList<EdifPort>();
	    		PreMitigatedPortGroup portGroup = _desc.getPortGroup(oldPort);
	    		if (portGroup != null) { // pre-mitigated port
	    			if (oldPort == portGroup.getFirstPort()) { // only take action on the first port in the group
	    				for (EdifPort port : portGroup.getPorts()) {
	    					EdifPort newPort = newCell.getMatchingPort(port);
	    					if (newPort == null)
	    						throw new EdifRuntimeException("Unexpected: port names of cell do not match after copy");
	    					newPorts.add(newPort);
	    				}
	    			}
	    		}
	    		else { // just a regular port
	    			EdifPort newPort = newCell.getMatchingPort(oldPort);
	    			if (newPort == null) {
	    				throw new EdifRuntimeException("Unexpected: port names of cell do not match after copy");
	    			}
	    			newPorts.add(newPort);
	    		}
	    		_replicatedPortMap.put(oldPort, newPorts);            
	    	}
	    }
	
	    // This method is part of the super class. It calls addChildEdifCellInstance which is overloaded.
	    addChildEdifCellInstances(origCell, newCell);
	
	    if (origCell == _origTopCell) {
	        _netManager = new NetManager(newCell);
	        addOrgans(newCell);
	    }
	    addNets(origCell, newCell);
	    if (origCell == _origTopCell) {
	        // at this point everything else should be done and we can start wiring up any detector nets
	        addDetectionWiring(newCell);
	    }
	    return newCell;
	}

	/**
     * Either replicate the given instance in the new design (if operating in the top cell) or copy it over exactly.
     */
    protected void addChildEdifCellInstance(EdifCell origCell, EdifCell newCell, EdifCellInstance oldInstance) throws EdifNameConflictException {
        if (_desc.shouldIgnoreInstance(oldInstance))
        	// Some instances should not be copied. This will not copy instances that have been tagged as ignore
            return;
        if (origCell != _origTopCell)
        	// if it is not the top cell, call super
            super.addChildEdifCellInstance(origCell, newCell, oldInstance);
        else {
        	// Process the top cell
            EdifCell newCellDefinition = getNewEdifCell(oldInstance.getCellType());
            List<EdifCellInstance> newInstances = null;
            ReplicationType replicationType = _desc.getReplicationType(oldInstance);
            if (replicationType != null) { // marked for replication
                // If instance was tagged for replication, call the replicate method of the replication type.
            	newInstances = replicationType.replicate(oldInstance, newCellDefinition,  newCell);
            }
            else if (EdifReplicationPropertyReader.isPreMitigatedInstance(oldInstance)) { // pre-mitigated instance
            	// Perform a single instance copy
            	newInstances = UnityReplicationType.getInstance().replicate(oldInstance, newCellDefinition, newCell);
            }
            else // not premitigated or not tagged for replication: perform a single instance copy
            	newInstances = UnityReplicationType.getInstance().replicate(oldInstance, newCellDefinition, newCell);
            _instanceMap.put(oldInstance, newInstances);
        }
    }

    /**
     * Either replicate the ports of the cell in the new design (if operating on the top cell) or copy
     * ports over exactly.
     */
    protected void addEdifPorts(EdifCell origCell, EdifCell newCell) throws EdifNameConflictException {
        if (origCell != _origTopCell) {
        	// If it is not the top cell, use the default port adding code (i.e., super).
            super.addEdifPorts(origCell, newCell);
        }
        else {
        	// Custom port replication for the top cell
            for (EdifPort oldPort : origCell.getPortList()) {
                List<EdifPort> newPorts = null;
                ReplicationType portRepType = _desc.getReplicationType(oldPort);
                PreMitigatedPortGroup portGroup = _desc.getPortGroup(oldPort);
                if (portRepType != null) {
                	// For this port, a replication type was specified. Call the custom
                	// replication type code to  do the port replication
                    newPorts = portRepType.replicate(oldPort, newCell);
                }
                else if (portGroup != null) { // pre-mitigated port
                    if (oldPort == portGroup.getFirstPort()) { // only take action on the first port in the group
                        newPorts = new ArrayList<EdifPort>();
                        for (EdifPort port : portGroup.getPorts()) {
                            newPorts.add(UnityReplicationType.getInstance().replicate(port, newCell).iterator().next());
                        }
                    }
                }
                else { // if it's not to be replicated and isn't part of a pre-mitigated port group, just copy it over
                	if (newPorts == null)
                		newPorts = new ArrayList<EdifPort>(1);
                	newPorts.add(UnityReplicationType.getInstance().replicate(oldPort, newCell).iterator().next());
                }
                if (newPorts != null && newPorts.size() != 0) {
                    _replicatedPortMap.put(oldPort, newPorts);
                }
            }
        }
    }

    /**
     * Called by copyEdifCell->addNets
     * 
     * Either wire replicated instances according to the wiring algorithm and WiringPolicies specified in
     * the ReplicationDescription (if working in the top cell) or copy nets exactly.
     */
    protected EdifNet addNet(EdifCell origCell, EdifCell newCell, EdifNet oldNet) throws EdifNameConflictException {

    	if (_desc.shouldIgnoreNet(oldNet))
    		// Net has been tagged as ignore. Do not copy (for example, the net between a GND and a pretriplicated port)
    		return null;
        if (origCell != _origTopCell)
        	// If this is not the top cell, call the super.addNet
            return super.addNet(origCell, newCell, oldNet);
        else {

        	//////////////// wiring algorithm ////////////////
            
            // separate portRefs into drivers (including INOUT) and sinks
            Collection<EdifPortRef> origDrivers = oldNet.getSourcePortRefs(true, true);
            Collection<EdifPortRef> origSinks = oldNet.getSinkPortRefs(false, true);
            
            if (_desc.shouldIgnorePortRefsInNet(oldNet)) {
            	// Some nets need to have some of their port refs ignored but not all of them. This
            	// has a list of these port refs to ignore.
            	origSinks.removeAll(_desc.getPortRefsToIgnore(oldNet));
            }
            
            // 1. Determine the replication type of the net (based on the replication type of the drivers)
            ReplicationType replicationType = null;
            for (EdifPortRef driver : origDrivers) {
                ReplicationType driverType = _desc.getReplicationType(driver);
                if (replicationType == null)
                    replicationType = driverType;
                else {
                    if (replicationType != driverType) {
                        throw new EdifRuntimeException("Unexpected: Net drivers have different ReplicationTypes");
                    }
                }
            }
            
            // 2. Determine driver connections
            
            // A list of port connections for the drivers. These port connections will not include connections for sinks.
            List<PortConnection> driverConnections = new ArrayList<PortConnection>();
            for (int i = 0; i < replicationType.getReplicationFactor(); i++) {
                // create MultiPortConnections and give them net names derived from the replication type and domain
            	// At this point, we don't have a net. We just have an empty list of connections for each domain of the new net.
                driverConnections.add(new MultiPortConnection(replicationType.getReplicationNetNameable(oldNet.getEdifNameable(), i)));
            }
            
            // setup driver connections. We will fill in the PortConnections for the list created above.
            for (EdifPortRef origDriver : origDrivers) {
                EdifCellInstance instance = origDriver.getCellInstance();
                EdifPort port = origDriver.getPort();
                                
                if (instance == null || EdifReplicationPropertyReader.isPreMitigatedInstance(instance)) { // either a top-level or a pre-mitigated instance
                	// If the instance associated with the driver of this net is null, it is a top-level port. This means
                	// that there are no instances to iterate over (i.e. there is only one instance. Instead, you need to iterate
                	// over the replicated ports.
                	//
                	// If the instance is premitigated, again, you don't iterate over instance. Instead, like a top-level instance,
                	// you need to iterate over the replicated ports of the premitigated instance.
                	EdifCellInstance newInstance = (instance == null) ? null : _instanceMap.get(instance).get(0);
                    if (_replicatedPortMap.get(port) != null) {
                    	// get the replicated ports in the top-level or premitigated instance
                        Iterator<EdifPort> portIt = _replicatedPortMap.get(port).iterator();
                        // There should be the same number of ports as driver connections
                        Iterator<PortConnection> mpcIt = driverConnections.iterator();
                        while (portIt.hasNext() && mpcIt.hasNext()) { // the two collections have the same number of elements
                            EdifPort connectionPort = portIt.next();
                            MultiPortConnection mpc = (MultiPortConnection) mpcIt.next();
                            // Add a connection to the multiport connection.
                            mpc.addConnection(newInstance, connectionPort.getSingleBitPort(origDriver.getBusMember()));                       
                        }
                    }
                }
                else { // not a top-level port and not a pre-mitigated instance
                	// In this case, the instance of the driver is replicated. Iterate over the replicated instances, not the ports.
                	EdifPort newPort = _replicatedPortMap.get(port).get(0);
                    Iterator<EdifCellInstance> instanceIt = _instanceMap.get(instance).iterator();
                    Iterator<PortConnection> mpcIt = driverConnections.iterator();
                    while (instanceIt.hasNext() && mpcIt.hasNext()) { // the two collections have the same number of elements
                        EdifCellInstance connectionInstance = instanceIt.next();
                        MultiPortConnection mpc = (MultiPortConnection) mpcIt.next();
                        mpc.addConnection(connectionInstance, newPort.getSingleBitPort(origDriver.getBusMember()));
                    }
                }
            }
            
            // 3. Organ insertion

            // Now we need to see if there were any organs created for this net and wire their inputs appropriately. In the process
            // we will also see which of the sinks need organ outputs in preparation for wiring up the sinks
            //
            // Some of the sinks will be getting organ outputs and some will be getting the outputs straight from the drivers.
            // This map indicates for each original sink port ref which PortConnections it will be connected to.
            Map<EdifPortRef, List<PortConnection>> sinkConnectionSources = new LinkedHashMap<EdifPortRef, List<PortConnection>>();
            Collection<OrganSpecification> organSpecifications = _desc.getOrganSpecifications(oldNet);
            if (organSpecifications != null) { // if there is an organ associated with this net.
                for (OrganSpecification organSpecification : organSpecifications) { // can have multiple organs in a net
                    Organ organType = organSpecification.getOrganType();
                    // The organs have already been created and PortConnection objects for their outputs have been created.
                    // Wire the inputs to the organ from the driver connections. The wires are actually created here.
                    organType.wireInputs(organSpecification, oldNet, driverConnections, _netManager, replicationType);
                    
                    List<PortConnection> organOutputs = organType.getOutputs(organSpecification);
                    List<EdifPortRef> sinksGettingOrganOutputs = organSpecification.getSinksGettingOrganOutputs();
                    // Deciding whether the sinks should get the organ outputs (i.e. voter) or the inputs to the voter
                    // (which are driver connections, i.e. bypass the organ).
                    for (EdifPortRef origSink : origSinks) {
                        if (sinksGettingOrganOutputs != null &&sinksGettingOrganOutputs.contains(origSink)) {
                            sinkConnectionSources.put(origSink, organOutputs);
                        }
                    }
                }
                
            }
            // This loop will iterate over all sinks of the given driver and make the connections.
            for (EdifPortRef origSink : origSinks) {

            	// check to see if sink is already being driven by a driver (this could only be an organ output at this point).
            	if (sinkConnectionSources.get(origSink) == null)
                    sinkConnectionSources.put(origSink, driverConnections);
                List<PortConnection> sinkConnectionSinks = new ArrayList<PortConnection>();
                EdifCellInstance instance = origSink.getCellInstance();
                EdifPort port = origSink.getPort();
                if (instance == null || EdifReplicationPropertyReader.isPreMitigatedInstance(instance)) { // top-level port or pre-mitigated instance
                	// For top-level ports or pre-mitiaged instances, iterate over replicated ports.
                	EdifCellInstance newInstance = (instance == null) ? null : _instanceMap.get(instance).get(0);
                    for (EdifPort newPort : _replicatedPortMap.get(port)) {
                        PortConnection sinkConnectionSink = new SinglePortConnection(newPort.getSingleBitPort(origSink.getBusMember()), newInstance);
                        sinkConnectionSinks.add(sinkConnectionSink);
                    }
                }
                else { // not a top-level port and not a pre-mitigated instance
                	// For replicated instances, iterate over the instances
                    List<EdifPort> newPortList = _replicatedPortMap.get(port);
                    if (newPortList == null)
                    	throw new EdifRuntimeException("Unexpected: nothing in _portMap for " + port);
                    EdifPort newPort = newPortList.get(0);
                    for (EdifCellInstance newInstance : _instanceMap.get(instance)) {
                        PortConnection sinkConnectionSink = new SinglePortConnection(newPort.getSingleBitPort(origSink.getBusMember()), newInstance);
                        sinkConnectionSinks.add(sinkConnectionSink);
                    }
                }
                WiringPolicy wiringPolicy = _desc.getWiringPolicy(origSink);

                // default wiring policy where unspecified is ModuloIterationWiringPolicy
                if (wiringPolicy == null)
                	wiringPolicy = ModuloIterationWiringPolicy.getInstance();
                // Do the actual wiring
                wiringPolicy.connectSourcesToSinks(sinkConnectionSources.get(origSink), sinkConnectionSinks, _netManager);
            }
            
            // When the net is composed only of drivers, they need to be wired together
            // (this happens when the net connects INOUT ports to each other)
            if (origSinks.size() == 0) {
                for (PortConnection driverConnection : driverConnections) {
                    // Calling getNet creates a net that binds all of the drivers for
                    // the given domain together. (driverConnection is really an instance
                    // of MultiPortConnection where the multiple connections in each instance
                    // are the multiple drivers for each domain).
                    _netManager.getNet(driverConnection);
                }
            }            
        }

        return null; // this works because EdifEnvironmentCopy never uses the return value
    }

    /**
     * Add the circuitry for merging the detection signals into the specified output(s). 
     */
    protected void addDetectionWiring(EdifCell newCell) {

    	/* A single detection domain could be used to create more than one top-level output. Because
    	 * of this, multiple DetectionOutputMergers may be needed for each DetectionDomain. (for example,
    	 * you may want to handle the detection domain in more than one way: 'or' for standard merging,
    	 * and some other application specific merging function (even, all, etc.). The following map
    	 * links detectionoutputmerges with each detection domain.
    	 *  
    	 * The point of the map is that each unique pair of detection domain and detection output merger
    	 * gets its own unique list of PortConnections. 
    	 * 
    	 * The purpose of the cache is to prevent multiple mergers from being created for each DetectionDomain
    	 */    	
    	Map<DetectionDomain, Map<DetectionOutputMerger, List<PortConnection>>> domainOutputsCache =
    		new LinkedHashMap<DetectionDomain, Map<DetectionOutputMerger, List<PortConnection>>>();

    	for (DetectionOutputSpecification os : _desc.getDetectionOutputSpecifications()) {
    		// Iterate over each top-level detection output and create an enplty list of port connections
    		// (a single list of multi-bit connections).
    		//
    		// Each DetectionOutputSpecification may merge more than one DetectionDomain. Iterate over
    		// all detection domains that are associated with this DetectionOutputSpecification.
    		List<List<PortConnection>> domainOutputs = new ArrayList<List<PortConnection>>();
    		for (DetectionDomain detectionDomain : os.getDetectionDomains()) {
    			// check to see if the domain has been merged with the correct merger. If it has,
    			// get the merged outputs from the chache. If not, create the merging circuitry and add
    			// it to the cache.
    			List<PortConnection> cachedDomainOutput = null;
    			Map<DetectionOutputMerger, List<PortConnection>> cacheMap = domainOutputsCache.get(detectionDomain);
    			if (cacheMap != null) {
    				cachedDomainOutput = cacheMap.get(os.getMerger());
    			}
    			else {
    				cacheMap = new LinkedHashMap<DetectionOutputMerger, List<PortConnection>>();
    				domainOutputsCache.put(detectionDomain, cacheMap);
    			}
    			if (cachedDomainOutput == null) {
    				// domain has not been merged, perform the merge
    				List<List<PortConnection>> domainConnections = new ArrayList<List<PortConnection>>();
    				List<List<PortConnection>> inputConnections = detectionDomain.getInputConnections();
    				if (inputConnections != null)
    					domainConnections.addAll(inputConnections);
    				for (OrganSpecification dOrganSpec : detectionDomain.getDetectorSpecifications()) {
    					domainConnections.add(dOrganSpec.getOrganType().getOutputs(dOrganSpec));
    				}
    				// Merge a single domain and cache it
    				cachedDomainOutput = os.getMerger().mergeOutputs(domainConnections, newCell, _netManager);
    				cacheMap.put(os.getMerger(), cachedDomainOutput);
    			}
    			domainOutputs.add(cachedDomainOutput);
    		}
    		// We are still iterating over output specifications. This line merges all of the domains in
    		// a single output specification.
    		List<PortConnection> specificationOutput = os.getMerger().mergeOutputs(domainOutputs, newCell, _netManager);
    		
    		// check to see if an EdifPort needs to be created for this DetectionOutputSpecification.
    		// Create it if needed; otherwise, use os.getOutputConnection()
    		List<SinglePortConnection> outputConnection = null;
    		if (os.needsPortCreated()) {
    			String portName = os.getPortNameToCreate();
    			int signalWidth = os.getDetectionType().getSignalWidth();
    			EdifPort port = null;
    			try {
    				port = newCell.addPort(NamedObject.createValidEdifNameable(portName), signalWidth, EdifPort.OUT);
    			} catch (EdifNameConflictException e) {
    				// shouldn't get here because the cell was already checked for a port with this name
    				throw new EdifRuntimeException("Detection port conflict");
    			}
    			outputConnection = new ArrayList<SinglePortConnection>(signalWidth);
    			for (int i = 0; i < signalWidth; i++) {
    				SinglePortConnection connection = new SinglePortConnection(port.getSingleBitPort(i), null);
    				outputConnection.add(connection);
    			}
    		}
    		else {
    			// convert references in old PortConnections to references in the new EdifEnvironment
    			// 
    			// Get new version of port that was copied in hte new edif environment
    			outputConnection = new ArrayList<SinglePortConnection>();
    			List<SinglePortConnection> oldCellOutputConnection = os.getOutputConnection();
    			for (SinglePortConnection pc : oldCellOutputConnection) {
    				EdifSingleBitPort oldEsbp = pc.getSingleBitPort();
    				EdifCellInstance oldInstance = pc.getInstance();
    				EdifPort oldPort = oldEsbp.getParent();
    				int bitNum = oldEsbp.bitPosition();
    				List<EdifPort> newPorts = _replicatedPortMap.get(oldPort);
    				EdifPort newPort = null;
    				if (newPorts != null)
    					newPort = newPorts.get(0); // there should only be one!
    				else
    					throw new EdifRuntimeException("Error: cannot find detection port in new EdifEnvironment");
    				List<EdifCellInstance> newInstances = _instanceMap.get(oldInstance);
    				if (oldInstance != null && newInstances == null)
    					throw new EdifRuntimeException("Error: cannot find detection port instance in new EdifEnvironment");
    				EdifCellInstance newInstance = null;
    				if (newInstances != null)
    					newInstance = newInstances.get(0);
    				if (newPorts.size() > 1 || (newInstances != null && (newInstances.size() > 1)))
    					throw new EdifRuntimeException("Error: a detection output port was replicated");
    				EdifSingleBitPort newEsbp = newPort.getSingleBitPort(bitNum);
    				SinglePortConnection newPc = new SinglePortConnection(newEsbp, newInstance);
    				outputConnection.add(newPc);
    			}
    		}
    		
    		if (specificationOutput != null) { // this will be null if there wasn't anything to merge 
    			List<PortConnection> preparedOutputs = _arch.prepareForDetectionOutput(specificationOutput, os.shouldInsertOreg(), os.shouldInsertObuf(), os.getClockNetName(), _netManager);
    			ModuloIterationWiringPolicy.getInstance().connectSourcesToSinks(preparedOutputs, outputConnection, _netManager);
    		}
    	}
    }

    /**
     * Add organs specified by ReplicationDescription
     * 
     * @param newTopCell
     */
    protected void addOrgans(EdifCell newTopCell) {
        for (EdifNet net : _origTopCell.getNetList()) {
            Collection<OrganSpecification> organSpecifications = _desc.getOrganSpecifications(net);
            if (organSpecifications != null) {
                for (OrganSpecification organSpec : organSpecifications) {
                    organSpec.getOrganType().createOrgan(organSpec, net, newTopCell);
                }
            }
        }
    }

    /**
     * This method will get the EdifCell in the new EdifEnvironment that corresponds to the given
     * EdifCell from the old EdifEnvironment. It does so by first checking to see if the given
     * old cell has already been copied (_cellMap) and returning the new copy if it has. If the
     * given cell has not already been copied, a copy is made and the copy is returned.
     * 
     * The intention is that the copyEdifCell method not be used directly -- this will ensure that
     * we don't accidently create duplicate copies of the same cell and at the same time only copy
     * the cells that are actually needed in the new environment.
     * 
     * @param oldCell
     * @return
     * @throws EdifNameConflictException 
     */
    protected EdifCell getNewEdifCell(EdifCell oldCell) throws EdifNameConflictException {
       EdifCell newCell = _cellMap.get(oldCell);
       if (newCell == null) {
           newCell = copyEdifCell(oldCell);
           _cellMap.put(oldCell, newCell);
       }
       return newCell;
    }
    
    public void printDomainReport(String fileName) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
        for (EdifCellInstance origInstance : _instanceMap.keySet()) {
            int domain = 0;
            for (EdifCellInstance eci : _instanceMap.get(origInstance)) {
                pw.print(eci.getName());
                pw.print("\t");
                pw.print(eci.getCellType().getName());
                pw.print("\t");
                pw.println("" + domain);
                domain++;
            }
        }
        pw.close();
    }

    protected NMRArchitecture _arch;
    protected ReplicationDescription _desc;
    protected EdifCell _origTopCell;
    protected Map<EdifCellInstance, List<EdifCellInstance>> _instanceMap;
    protected Map<EdifNet, List<EdifNet>> _netMap;

    /** Map between original EdifPort and a List of replicated EdifPort objects. **/
    protected Map<EdifPort, List<EdifPort>> _replicatedPortMap;
    protected NetManager _netManager;
    protected EdifNameable _topCellName;
}
