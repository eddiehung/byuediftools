package edu.byu.ece.edif.tools.replicate.nmr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import edu.byu.ece.edif.core.EdifCellInstance;

/**
 * This class expects the UtilizationTracker to be pre initialized with the contents of
 * the //unreplicated// cell. If this class is to be used on a second pass of the
 * NMRSelection tool, previously made ReplicationType assignments will have to be made
 * through this class before beginning the desired replication selection strategy.
 * 
 * This class is intended to be used by an NMRSelectionStrategy in order to keep adding
 * instances to be replicated until the device is full. Once the device is full, the
 * selection strategy can use the getReplicationTypes and getInstancesWithReplicationType
 * to know what to put in the ReplicationDescription object.
 */
public class ReplicationUtilizationTracker {

    public ReplicationUtilizationTracker(DeviceUtilizationTracker tracker) {
        _tracker = tracker;
        _replicationMap = new LinkedHashMap<ReplicationType, List<EdifCellInstance>>();
        _instanceMap = new LinkedHashMap<EdifCellInstance, ReplicationType>();
        _exclusionSet = new LinkedHashSet<EdifCellInstance>();
    }

    /**
     * @param eci
     * @param repType
     * @param override
     * @return
     */
    protected void addToPrivateDataStructures(EdifCellInstance eci, ReplicationType repType) {
    	ReplicationType storedRepType = _instanceMap.get(eci);
    	if (storedRepType != null) {
    		_instanceMap.remove(eci);
    		List<EdifCellInstance> instanceList = _replicationMap.get(storedRepType);
    		instanceList.remove(eci);
    	}

    	List<EdifCellInstance> instanceList = _replicationMap.get(repType);
    	if (instanceList == null) {
    		instanceList = new ArrayList<EdifCellInstance>();
    		_replicationMap.put(repType, instanceList);
    	}
    	instanceList.add(eci);
    	_instanceMap.put(eci, repType);
    }
    
    public void addToTracker(EdifCellInstance eci, ReplicationType repType, boolean override) throws OverutilizationEstimatedStopException, OverutilizationHardStopException, UnsupportedResourceTypeException, DuplicateNMRRequestException {
    	if (!(_exclusionSet.contains(eci))) {
    		ReplicationType storedRepType = _instanceMap.get(eci);
    		if (storedRepType != null) {
    			if (!override)
    				throw new DuplicateNMRRequestException("Trying to select an instance for replication type: " + repType + " when the instance is already selected for " + storedRepType);
    			_tracker.removeNMRInstance(eci, storedRepType.getReplicationFactor());
    		}
    		_tracker.nmrInstance(eci, repType.getReplicationFactor());
    		addToPrivateDataStructures(eci, repType);
    	}
    }
    
    /**
     * Add the instances as an atomic group and return whether or not the instances were able to be added.
     * 
     * @param instances
     * @param repType
     * @param override
     * @return
     * @throws OverutilizationEstimatedStopException
     * @throws OverutilizationHardStopException
     * @throws UnsupportedResourceTypeException
     * @throws DuplicateNMRRequestException 
     */
    public void addToTrackerAtomic(Collection<EdifCellInstance> instances, ReplicationType repType, boolean override) throws OverutilizationEstimatedStopException, OverutilizationHardStopException, UnsupportedResourceTypeException, DuplicateNMRRequestException {

    	List<EdifCellInstance> instancesToRevert = new ArrayList<EdifCellInstance>();
    	List<ReplicationType> replicationTypesToRevert = new ArrayList<ReplicationType>();
    	List<EdifCellInstance> filteredInstances = new ArrayList<EdifCellInstance>();
    	// check for duplicate instances and filter instances by exclusion list
    	for (EdifCellInstance eci : instances) {
    		if (!_exclusionSet.contains(eci)) {
    			filteredInstances.add(eci);
    			if (_instanceMap.containsKey(eci)) {
    				if (!override) {
    					throw new DuplicateNMRRequestException("Trying to select an instance for replication type: " + repType + " when the instance is already selected for " + _instanceMap.get(eci));
    				}
    				else {
    					ReplicationType replicationType = _instanceMap.get(eci);
    					instancesToRevert.add(eci);
    					replicationTypesToRevert.add(replicationType);
    					_tracker.removeNMRInstance(eci, replicationType.getReplicationFactor());
    				}
    			}
    		}
    	}
    	
    	try {
    		_tracker.nmrInstancesAtomic(filteredInstances, repType.getReplicationFactor());
    	} catch (OverutilizationEstimatedStopException e) {
    		int size = instancesToRevert.size();
    		for (int i = 0; i < size; i++) {
    			EdifCellInstance eci = instancesToRevert.get(i);
    			ReplicationType revertType = replicationTypesToRevert.get(i);
    			_tracker.nmrInstance(eci, revertType.getReplicationFactor());
    		}
    		throw e;
    	} catch (OverutilizationHardStopException e) {
    		int size = instancesToRevert.size();
    		for (int i = 0; i < size; i++) {
    			EdifCellInstance eci = instancesToRevert.get(i);
    			ReplicationType revertType = replicationTypesToRevert.get(i);
    			_tracker.nmrInstance(eci, revertType.getReplicationFactor());
    		}
    		throw e;
    	}
    	
    	// if we get here with no exceptions, all of the instances were added
    	for (EdifCellInstance eci : filteredInstances) {
    		addToPrivateDataStructures(eci, repType);
    	}
    	
    }
    
    public void addToTrackerAsManyAsPossible(Collection<EdifCellInstance> instances, ReplicationType repType, boolean override) throws OverutilizationEstimatedStopException, OverutilizationHardStopException, UnsupportedResourceTypeException, DuplicateNMRRequestException {
    	// first check to see if there are any duplicate nmr requests and throw the exception if they are
    	// (that way we won't have to back out any changes if we find one later)
    	if (!override) {
    		for (EdifCellInstance eci : instances) {
    			if (!_exclusionSet.contains(eci)) {
    				ReplicationType storedRepType = _instanceMap.get(eci);
    				if (storedRepType != null)
    					throw new DuplicateNMRRequestException("Trying to select an instance for replication type: " + repType + " when the instance is already selected for " + storedRepType);
    			}
    		}
    	}
    	
    	for (EdifCellInstance eci : instances) {
    		if (!_exclusionSet.contains(eci)) {
    			// check for previous replication type assignment and remove it to try to fit in
    			// the new assignment (it will be added back if the new assignment doesn't fit)
    			ReplicationType storedRepType = _instanceMap.get(eci);
    			if (storedRepType != null) {
    				_tracker.removeNMRInstance(eci, storedRepType.getReplicationFactor());
    			}
    			
    			// try to add the new assignment
    			try {
    				_tracker.nmrInstance(eci, repType.getReplicationFactor());
    			} catch (OverutilizationEstimatedStopException e) {
    				// if there was a previous assignment, restore it
    				if (storedRepType != null) {
    					_tracker.nmrInstance(eci, storedRepType.getReplicationFactor());
    				}
    				continue;
    			} catch (OverutilizationHardStopException e) {
    				// if there was a previous assignment, restore it
    				if (storedRepType != null) {
    					_tracker.nmrInstance(eci, storedRepType.getReplicationFactor());
    				}
    				continue;
    			}
    			// if the assignment fit successfully, add it to the data structures
    			addToPrivateDataStructures(eci, repType);
    		}
    	}
    }
    
    public boolean isExcluded(EdifCellInstance eci) {
    	return _exclusionSet.contains(eci);
    }
    
    public void excludeInstance(EdifCellInstance eci) {
    	_exclusionSet.add(eci);
    }
    
    public boolean unexcludeInstance(EdifCellInstance eci) {
    	return _exclusionSet.remove(eci);
    }
    
    public Collection<ReplicationType> getReplicationTypes() {
    	return _replicationMap.keySet();
    }
    
    public List<EdifCellInstance> getInstancesWithReplicationType(ReplicationType repType) {
    	List<EdifCellInstance> result = _replicationMap.get(repType);
    	if (result == null)
    		result = new ArrayList<EdifCellInstance>(0);
    	return result;
    }
    
    public Map<EdifCellInstance, ReplicationType> getInstanceReplicationMap() {
    	return new LinkedHashMap<EdifCellInstance, ReplicationType>(_instanceMap);
    }
    
    protected DeviceUtilizationTracker _tracker;
    
    protected Map<ReplicationType, List<EdifCellInstance>> _replicationMap;
    
    protected Map<EdifCellInstance, ReplicationType> _instanceMap;
    
    protected HashSet<EdifCellInstance> _exclusionSet;
    
}
