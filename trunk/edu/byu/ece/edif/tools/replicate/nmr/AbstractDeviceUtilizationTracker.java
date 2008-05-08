/*
 * An abstract implementation of a device utilization tracker.
 * 
 * Copyright (c) 2008 Brigham Young University
 * 
 * This file is part of the BYU EDIF Tools.
 * 
 * BYU EDIF Tools is free software: you may redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * 
 * BYU EDIF Tools is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * A copy of the GNU General Public License is included with the BYU EDIF Tools.
 * It can be found at /edu/byu/edif/doc/gpl2.txt. You may also get a copy of the
 * license at <http://www.gnu.org/licenses/>.
 * 
 */
package edu.byu.ece.edif.tools.replicate.nmr;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxResourceMapper;

/////////////////////////////////////////////////////////////////////////
//// AbstractDeviceUtilizationTracker
/**
 * An abstract implementation of a device utilization tracker.
 * 
 * @author Keith Morgan
 * @version $Id: AbstractDeviceUtilizationTracker.java 162 2008-04-02 21:45:37Z
 * jamesfcarroll $
 */
public abstract class AbstractDeviceUtilizationTracker implements DeviceUtilizationTracker {

    public AbstractDeviceUtilizationTracker() {
        _currentInstances = new LinkedHashSet<EdifCellInstance>();
        _maxUtilizationMap = new LinkedHashMap<String, Integer>();
        _currentUtilizationMap = new LinkedHashMap<String, Double>();

        _currentNMRInstances = new LinkedHashSet<EdifCellInstance>();
        _excludeFromNMRInstances = new LinkedHashSet<String>();
        _excludeFromNMRCellTypes = new LinkedHashSet<String>();
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * This function adds the instance eci to the normal cell instances list and
     * adds the appropriate utilization to the device utilization count.
     * 
     * @param eci Instance to add to the instances list.
     */

    public void addSingleInstance(EdifCellInstance eci) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, UnsupportedResourceTypeException {
        /*
         * We can go ahead and try to add the instance if it is a primitive
         * otherwise we need to call addInstances on the collection of sub-cell
         * instances within eci.
         */

        //if (eci.getCellType().isPrimitive() == true) {
        if (eci.getCellType().isLeafCell() == true) {
            incrementResourceCount(eci);
        }
        //      else if (eci.getCellType().isLeafCell() == true) {
        //          System.out.println ("WARNING: In ResourceTracker->addInstance, non-primitive leaf cell being added.  No utilization information will be added for this cell instance.");
        //      }
        else
            addSingleInstances(eci.getCellType().getSubCellList());
        // We get here if the instance fits in the device
        _currentInstances.add(eci);
    }

    /**
     * This function adds a collection of instances to add to the normal cell
     * instances list. It also adds the appropriate utilization for each
     * instance in the collection to the device utilization count. If any of the
     * instances cause an overutilization exception, none of the instances will
     * be added.
     * 
     * @param ecis Collection of instances to add to instances list.
     */
    public void addSingleInstances(Collection<EdifCellInstance> ecis) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, UnsupportedResourceTypeException {
        /*
         * This will need to 'cache' _currentInstancesList. They will be
         * committed only if all instances in ecis are added without an
         * exception occurring.
         */
        if (ecis == null)
            return;
        HashSet<EdifCellInstance> cachedCurrentInstances = (HashSet<EdifCellInstance>) _currentInstances.clone();
        cacheCurrentUtilizationNumbers();
        for (EdifCellInstance eci : (Collection<EdifCellInstance>) ecis) {
            try {
                addSingleInstance(eci);
            } catch (OverutilizationEstimatedStopException m) {
                _currentInstances = cachedCurrentInstances;
                revertToCachedUtilizationNumbers();
                throw m;
            } catch (OverutilizationHardStopException m) {
                _currentInstances = cachedCurrentInstances;
                revertToCachedUtilizationNumbers();
                throw m;
            }
        }
    }

    public void cacheCurrentUtilizationNumbers() {
        _cachedCurrentUtilizationMap = (HashMap<String, Double>) _currentUtilizationMap.clone();
        _cachedMaxUtilizationMap = (HashMap<String, Integer>) _maxUtilizationMap.clone();
    }

    /**
     * Adds the given EdifCellInstance object to the list of Instances that
     * should be skipped when calling the nmrInstance and nmrInstances methods.
     * 
     * @param eci The EdifCellInstance object that should be ignored during
     * replication
     */
    public void excludeInstanceFromNMR(EdifCellInstance eci) {
        excludeInstanceFromNMR(eci.getName());
    }

    /**
     * Adds the given EdifCellInstance object to the list of Instances that
     * should be skipped when calling the tmrInstance and tmrInstances methods.
     * 
     * @param instanceName The String name of the EdifCellInstance object that
     * should be ignored during triplication
     */
    public void excludeInstanceFromNMR(String instanceName) {
        _excludeFromNMRInstances.add(instanceName);
    }

    /**
     * Adds the CellType of the given EdifCellInstance object to the list of
     * Types that should be skipped when calling the nmrInstance and
     * nmrInstances methods. <b>All</b> of the instances of this same type will
     * be skipped.
     * 
     * @param eci The EdifCellInstance object whose CellType should be ignored
     * during replication
     */
    public void excludeCellTypeFromNMR(EdifCellInstance eci) {
        excludeCellTypeFromNMR(eci.getType());
    }

    /**
     * Adds the given EdifCellInstance object to the list of Instances that
     * should be skipped when calling the nmrInstance and nmrInstances methods.
     * <b>All</b> of the instances of this type will be skipped.
     * 
     * @param cellType A String representing the Cell Type that should be
     * ignored during replication
     */
    public void excludeCellTypeFromNMR(String cellType) {
        _excludeFromNMRCellTypes.add(cellType.toLowerCase());
    }

    public Collection<EdifCellInstance> getCurrentInstances() {
        return _currentInstances;
    }

    // TODO : this should replace the TMR version of the method
    public Collection<EdifCellInstance> getCurrentNMRInstances() {
        return _currentNMRInstances;
    }

    public int getResourceLimit(EdifCellInstance eci) throws UnsupportedResourceTypeException {
        String resourceType = XilinxResourceMapper.getResourceType(eci);
        try {
            return getResourceLimit(resourceType);
        } catch (UnsupportedResourceTypeException e) {
            throw new UnsupportedResourceTypeException("ECI " + eci + " has resource type " + resourceType
                    + " which is not supported in the specified part.");
        }
    }

    public int getResourceLimit(EdifCell cell) throws UnsupportedResourceTypeException {
        String resourceType = XilinxResourceMapper.getResourceType(cell);
        try {
            return getResourceLimit(resourceType);
        } catch (UnsupportedResourceTypeException e) {
            throw new UnsupportedResourceTypeException("Cell " + cell + " has resource type " + resourceType
                    + " which is not supported in the specified part.");
        }
    }

    public int getResourceLimit(String resourceType) {
        if (resourceType == null) {
            throw new UnsupportedResourceTypeException("");
        } else if (resourceType.compareToIgnoreCase("") == 0) {
            return -1;
        } else {
            Integer maxUtilizationI = _maxUtilizationMap.get(resourceType);
            if (maxUtilizationI == null)
                throw new UnsupportedResourceTypeException("Resource type " + resourceType
                        + " which is not supported in the specified part.");
            return maxUtilizationI.intValue();
        }
    }

    public double getResourceUtilization(EdifCellInstance eci) throws UnsupportedResourceTypeException {
        String resourceType = XilinxResourceMapper.getResourceType(eci);
        try {
            return getResourceUtilization(resourceType);
        } catch (UnsupportedResourceTypeException e) {
            throw new UnsupportedResourceTypeException("ECI " + eci + " has resource type " + resourceType
                    + " which is not supported in the specified part.");
        }
    }

    public double getResourceUtilization(EdifCell cell) throws UnsupportedResourceTypeException {
        String resourceType = XilinxResourceMapper.getResourceType(cell);
        try {
            return getResourceUtilization(resourceType);
        } catch (UnsupportedResourceTypeException e) {
            throw new UnsupportedResourceTypeException("Cell " + cell + " has resource type " + resourceType
                    + " which is not supported in the specified part.");
        }
    }

    public double getResourceUtilization(String resourceType) throws UnsupportedResourceTypeException {
        if (resourceType == null) {
            throw new UnsupportedResourceTypeException("Type " + resourceType
                    + " is not supported in the specified part.");
        } else if (resourceType.compareToIgnoreCase("") == 0) {
            // TODO: What should be done here???
            //System.out.println("WARNING: Do not know resource type for cell type: "+eci.getCellType().getName());
            return -1.0;
        } else {
            // TODO: had to change line below to get code to compile
            Double currentUtilizationD = _currentUtilizationMap.get(resourceType);
            if (currentUtilizationD == null)
                throw new UnsupportedResourceTypeException("Type " + resourceType
                        + " is not supported in the specified part.");
            return currentUtilizationD.doubleValue();
        }
    }

    public double getResourceUtilizationRatio(EdifCellInstance eci) throws UnsupportedResourceTypeException {
        return getResourceUtilizationRatio(eci.getCellType());
    }

    public double getResourceUtilizationRatio(EdifCell cell) throws UnsupportedResourceTypeException {
        return getResourceUtilization(cell) / getResourceLimit(cell);
    }

    /**
     * Return the utilization ratio of the given resource type, which is
     * calculated as from the current usage of resource divided by the resource
     * limit
     * 
     * @param resourceType String name of the specified resource
     * @return the utilization ratio of the given resource type, which is
     * calculated as from the current usage of resource divided by the resource
     * limit
     * @throws UnsupportedResourceTypeException
     */
    public double getResourceUtilizationRatio(String resourceType) throws UnsupportedResourceTypeException {
        return getResourceUtilization(resourceType) / getResourceLimit(resourceType);
    }

    /**
     * A method to check if the given EdifCellInstance object will be skipped
     * during replication.
     * 
     * @param eci The EdifCellInstance object to check
     * @return true if this Instance will be ignored when calling nmrInstance
     * and nmrInstances
     */
    public boolean isExcludedFromNMR(EdifCellInstance eci) {
        if (_excludeFromNMRInstances.contains((eci.getName())))
            return true;
        if (_excludeFromNMRCellTypes.contains(eci.getType().toLowerCase()))
            return true;

        return false;
    }

    /**
     * This function removes the instance eci from the normal list of cell
     * instances. It also removes the utilization corresponding to eci from the
     * device utilization count.
     * 
     * @param eci Instance to remove from instances list.
     * @return True if successful in finding & removing eci, false otherwise.
     */
    public boolean removeSingleInstance(EdifCellInstance eci) throws UnsupportedResourceTypeException {
        if (_currentInstances.contains(eci)) {
            _currentInstances.remove(eci);
            decrementResourceCount(eci);
            return true;
        }
        return false;
    }

    /**
     * This function removes a collection of instances from the normal list of
     * cell instances. It also removes the utilization corresponding to each eci
     * from the device utilization count.
     * 
     * @param ecis Collection of instances to be removed.
     * @return True if successful in finding & removing *ALL* ecis, false
     * otherwise.
     */
    public boolean removeSingleInstances(Collection<EdifCellInstance> ecis) throws UnsupportedResourceTypeException {
        HashSet<EdifCellInstance> cachedCurrentInstances = (HashSet<EdifCellInstance>) _currentInstances.clone();
        cacheCurrentUtilizationNumbers();
        for (EdifCellInstance eci : (Collection<EdifCellInstance>) ecis) {
            if (removeSingleInstance(eci) == false) {
                _currentInstances = cachedCurrentInstances;
                revertToCachedUtilizationNumbers();
                return false;
            }
        }
        return true;
    }

    /**
     * This function removes the instance eci from the list of instances to be
     * replicated.
     * 
     * @param eci Instance to remove from replication list.
     * @param replicationFactor represents the n factor of NMR
     * @return True if successful in finding & removing eci, false otherwise.
     */
    public boolean removeNMRInstance(EdifCellInstance eci, int replicationFactor)
            throws UnsupportedResourceTypeException {
        for (EdifCellInstance eci2 : _currentNMRInstances) {
            if (eci.equals(eci2)) {
                _currentNMRInstances.remove(eci2);
                /*
                 * This instance was counted twice for triplication so it's
                 * count needs to be decremented twice for removal.
                 */
                for (int index = 1; index < replicationFactor; index++) {
                    decrementResourceCount(eci);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * This function removes a collection of instances from the list of
     * instances to be replicated.
     * 
     * @param ecis Collection of instances to be removed.
     * @param replicationFactor the N factor of NMR
     * @return True if successful in finding & removing *ALL* ecis, false
     * otherwise.
     */
    public boolean removeNMRInstances(Collection<EdifCellInstance> ecis, int replicationFactor)
            throws UnsupportedResourceTypeException {
        HashSet<EdifCellInstance> cachedCurrentNMRInstances = (HashSet<EdifCellInstance>) _currentNMRInstances.clone();
        //DeviceUtilization cachedDeviceUtilization = (DeviceUtilization)_deviceUtilization.clone();
        cacheCurrentUtilizationNumbers();
        for (EdifCellInstance eci : ecis) {
            if (removeNMRInstance(eci, replicationFactor) == false) {
                _currentNMRInstances = cachedCurrentNMRInstances;
                //_deviceUtilization = cachedDeviceUtilization;
                revertToCachedUtilizationNumbers();
                return false;
            }
        }
        return true;
    }

    public void revertToCachedUtilizationNumbers() {
        _currentUtilizationMap = _cachedCurrentUtilizationMap;
        _maxUtilizationMap = _cachedMaxUtilizationMap;
    }

    /**
     * This function adds the instance eci to the list of instances to be
     * replicated. It also adds the appropriate utilization to the device
     * utilization count. (The utilization is counted twice to account for the
     * two additional resources required for triplication.)
     * 
     * @param eci Instance to add to triplication list.
     * @param replicationFactor factor of replication for NMR (i/e TMR is 3, DWC
     * is 2, etc.)
     */
    public void nmrInstance(EdifCellInstance eci, int replicationFactor) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, UnsupportedResourceTypeException, DuplicateNMRRequestException {

        if (_currentNMRInstances.contains(eci))
            // TODO : change this exception eventually
            throw new DuplicateNMRRequestException("Duplicate NMR request for cell instance " + eci + ".");
        // Check for this Instance/Type in the exclude Sets
        if (isExcludedFromNMR(eci)) {
            //System.out.println("### Skipping exluded instance: "+eci);
            return; // Do not replicate this Instance
        }
        /*
         * We can go ahead and try to add the instance if it is a primitive
         * otherwise we need to call addInstances on the collection of sub-cell
         * instances within eci.
         */
        //if (eci.getCellType().isPrimitive() == true) {
        if (eci.getCellType().isLeafCell() == true) {
            // The instance needs to be counted an additional
            // replicationFactor - 1 times to be replicated
            for (int i = 1; i < replicationFactor; i++) {
                incrementResourceCount(eci);
            }
        }
        //      else if (eci.getCellType().isLeafCell() == true) {
        //          System.out.println ("WARNING: In ResourceTracker->addInstance, non-primitive leaf cell being added.  No utilization information will be added for this cell instance.");
        //      }           
        else
            nmrInstances(eci.getCellType().getSubCellList(), replicationFactor);
        // We get here if the instance fits in the device,
        _currentNMRInstances.add(eci);
    }

    /**
     * Adds all instances in the collection ecis for replication.
     * 
     * @param ecis Collection of instances to be added for replication.
     * @param replicationFactor N factor for NMR
     * @throws OverutilizationEstimatedStopException
     * @throws OverutilizationHardStopException
     */
    public void nmrInstances(Collection<EdifCellInstance> ecis, int replicationFactor)
            throws OverutilizationEstimatedStopException, OverutilizationHardStopException,
            UnsupportedResourceTypeException, DuplicateNMRRequestException {
        nmrInstances(ecis, false, false, replicationFactor);
    }

    /**
     * Adds all instances in the collection ecis for replication. Optionally
     * will ignore instances which cause estimated stops and/or hard stops.
     * 
     * @param ecis Collection of instances to be added for replication.
     * @param skipEstimatedStops boolean flag to optionally ignore estimated
     * stops.
     * @param skipHardStops boolean flag to optionally ignore hard stops.
     * @param replicationFactor the N factor in NMR
     * @throws OverutilizationEstimatedStopException
     * @throws OverutilizationHardStopException
     */
    public void nmrInstances(Collection<EdifCellInstance> ecis, boolean skipEstimatedStops, boolean skipHardStops,
            int replicationFactor) throws OverutilizationEstimatedStopException, OverutilizationHardStopException,
            UnsupportedResourceTypeException, DuplicateNMRRequestException {
        if (ecis == null)
            return;
        HashSet<EdifCellInstance> cachedCurrentNMRInstances = (HashSet<EdifCellInstance>) _currentNMRInstances.clone();
        // DeviceUtilization cachedDeviceUtilization =
        // (DeviceUtilization)_deviceUtilization.clone();
        cacheCurrentUtilizationNumbers();
        for (EdifCellInstance eci : ecis) {
            try {
                nmrInstance(eci, replicationFactor);
            } catch (OverutilizationEstimatedStopException m) {
                if (skipEstimatedStops == true)
                    continue;
                _currentNMRInstances = cachedCurrentNMRInstances;
                // _deviceUtilization = cachedDeviceUtilization;
                revertToCachedUtilizationNumbers();
                throw m;
            } catch (OverutilizationHardStopException m) {
                if (skipHardStops == true)
                    continue;
                _currentNMRInstances = cachedCurrentNMRInstances;
                // _deviceUtilization = cachedDeviceUtilization;
                revertToCachedUtilizationNumbers();
                throw m;
            } catch (DuplicateNMRRequestException m) {
                _currentNMRInstances = cachedCurrentNMRInstances;
                // _deviceUtilization = cachedDeviceUtilization;
                revertToCachedUtilizationNumbers();
                throw m;
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Device Utilization:\n");

        // Iterate through all of the resource types...
        Set<String> resources = _currentUtilizationMap.keySet();
        for (String resourceType : resources) {
            int used = (int) getResourceUtilization(resourceType);
            if (used > 0) {
                sb.append(resourceType + ": " + used + " out of " + getResourceLimit(resourceType) + " ("
                        + (int) (100.0 * getResourceUtilizationRatio(resourceType)) + "%).\n");
            }
        }
        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////
    ////                         protected methods                 ////

    protected void addResourceForTracking(String resourceType, double currentUtilization, int maxUtilization) {
        _maxUtilizationMap.put(resourceType, new Integer(maxUtilization));
        _currentUtilizationMap.put(resourceType, new Double(currentUtilization));
    }

    protected void _init(EdifCell cell) throws OverutilizationEstimatedStopException, OverutilizationHardStopException {
        // This ordering matters!!!!
        // Get the top cell instance from the top cell
        EdifCellInstance eci = cell.getLibrary().getLibraryManager().getEdifEnvironment().getTopCellInstance();
        // Initialize the resource tracker with the utilization of this instance
        total_instances = cell.getSubCellList().size();
        addSingleInstance(eci);
    }

    protected void setDesiredCoverageFactor(double factorValue) {
        coverage_factor = factorValue;
    }

    protected double total_instances;

    //2 just so it never gets in the way if you don't specify it
    protected double coverage_factor = 2.0;

    ///////////////////////////////////////////////////////////////////
    ////                         protected variables               ////

    protected HashMap<String, Double> _cachedCurrentUtilizationMap;

    protected HashMap<String, Integer> _cachedMaxUtilizationMap;

    protected HashSet<EdifCellInstance> _currentInstances;

    protected HashSet<EdifCellInstance> _currentNMRInstances;

    protected HashMap<String, Double> _currentUtilizationMap;

    protected HashSet<String> _excludeFromNMRInstances;

    protected HashSet<String> _excludeFromNMRCellTypes;

    protected HashMap<String, Integer> _maxUtilizationMap;
}
