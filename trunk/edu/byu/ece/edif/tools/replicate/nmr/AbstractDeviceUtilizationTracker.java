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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;

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
        _maxUtilizationMap = new LinkedHashMap<String, Integer>();
        _currentUtilizationMap = new LinkedHashMap<String, Double>();
        _numInstances = 0;
        _origNumInstances = 0;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    public void addSingleCell(EdifCell cell) throws OverutilizationEstimatedStopException, OverutilizationHardStopException, UnsupportedResourceTypeException {
        /*
         * We can go ahead and try to add the instance if it is a primitive
         * otherwise we need to call addInstances on the collection of sub-cell
         * instances within eci.
         */

        if (cell.isLeafCell() == true) {
            String resourceType = _resourceMapper.getResourceType(cell);
            if (resourceType == null) {
                Collection<String> bbResources = cell.getBlackBoxResources();
                if (bbResources == null) {
                    System.out.println("WARNING: Cell " + cell
                            + " does not map to a Xilinx primitve. If it is a black box,"
                            + " it's internal utilization will not be tracked.");
                } else {
                    System.out.println("WARNING:\n\t Cell " + cell
                            + " does not map to a Xilinx primitve, and is assumed to be a black box "
                            + "There is utilization information about this blackbox, and that will be "
                            + "used to estimate the total utilization. \n");
                    
                    cacheCurrentUtilization();
                    
                    try {
                        for (String type : bbResources) {
                            incrementResourceCount(type);
                        }
                    } catch (OverutilizationEstimatedStopException e) {
                        restoreCurrentUtilization();
                        throw e;
                    } catch (OverutilizationHardStopException e) {
                        restoreCurrentUtilization();
                        throw e;
                    } catch (UnsupportedResourceTypeException e) {
                        restoreCurrentUtilization();
                        throw e;
                    }
                }
            }
            else {
                try {
                incrementResourceCount(resourceType);
                } catch (UnsupportedResourceTypeException e) {
                    throw new UnsupportedResourceTypeException("Unsupported resource type for cell: " + cell);
                }
            }
            _origNumInstances++;
        }
        else
            addSingleInstances(cell.getSubCellList());
    }
    
    /**
     * This function adds the instance eci to the normal cell instances list and
     * adds the appropriate utilization to the device utilization count.
     * 
     * @param eci Instance to add to the instances list.
     */
    public void addSingleInstance(EdifCellInstance eci) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, UnsupportedResourceTypeException {
        addSingleCell(eci.getCellType());
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
         * This will need to 'cache' _origNumInstances and the current utilization.
         * The result operation will be backed out of in any exceptions occur.
         */
        if (ecis == null)
            return;
        int cachedOrigNumInstances = _origNumInstances;
        cacheCurrentUtilization();
        for (EdifCellInstance eci : ecis) {
            try {
                addSingleInstance(eci);
            } catch (OverutilizationEstimatedStopException e) {
                _origNumInstances = cachedOrigNumInstances;
                restoreCurrentUtilization();
                throw e;
            } catch (OverutilizationHardStopException e) {
                _origNumInstances = cachedOrigNumInstances;
                restoreCurrentUtilization();
                throw e;
            } catch (UnsupportedResourceTypeException e) {
                _origNumInstances = cachedOrigNumInstances;
                restoreCurrentUtilization();
                throw e;
            }
        }
    }

    protected void cacheCurrentUtilization() {
        _cachedCurrentUtilizationMap = (HashMap<String, Double>) _currentUtilizationMap.clone();
    }
    
    protected void restoreCurrentUtilization() {
        if (_cachedCurrentUtilizationMap != null)
        _currentUtilizationMap = _cachedCurrentUtilizationMap;        
    }

    public int getResourceLimit(EdifCellInstance eci) throws UnsupportedResourceTypeException {
        return getResourceLimit(eci.getCellType());
    }

    public int getResourceLimit(EdifCell cell) throws UnsupportedResourceTypeException {
        String resourceType = _resourceMapper.getResourceType(cell);
        try {
            return getResourceLimit(resourceType);
        } catch (UnsupportedResourceTypeException e) {
            throw new UnsupportedResourceTypeException("Resource type for cell: " + cell.toString() + " not supported in the specified part.");
        }
    }

    protected int getResourceLimit(String resourceType) {
        Integer maxUtilizationI = _maxUtilizationMap.get(resourceType);
        if (maxUtilizationI == null) {
            throw new UnsupportedResourceTypeException("");
        }
        return maxUtilizationI.intValue();
    }

    public double getResourceUtilization(EdifCellInstance eci) throws UnsupportedResourceTypeException {
        return getResourceUtilization(eci.getCellType());
    }

    public double getResourceUtilization(EdifCell cell) throws UnsupportedResourceTypeException {
        String resourceType = _resourceMapper.getResourceType(cell);
        try {
            return getResourceUtilization(resourceType);
        } catch (UnsupportedResourceTypeException e) {
            throw new UnsupportedResourceTypeException("Resource type for cell: " + cell.toString() + " not supported in the specified part.");
        }
    }

    protected double getResourceUtilization(String resourceType) throws UnsupportedResourceTypeException {
        Double currentUtilizationD = _currentUtilizationMap.get(resourceType);
        if (currentUtilizationD == null) {
            throw new UnsupportedResourceTypeException("");
        }
        return currentUtilizationD.doubleValue();
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

//    /**
//     * A method to check if the given EdifCellInstance object will be skipped
//     * during replication.
//     * 
//     * @param eci The EdifCellInstance object to check
//     * @return true if this Instance will be ignored when calling nmrInstance
//     * and nmrInstances
//     */
//    public boolean isExcludedFromNMR(EdifCellInstance eci) {
//        if (_excludeFromNMRInstances.contains((eci.getName())))
//            return true;
//        if (_excludeFromNMRCellTypes.contains(eci.getType().toLowerCase()))
//            return true;
//
//        return false;
//    }

    /**
     * This function removes the utilization corresponding to eci from the
     * device utilization count.
     * 
     * @param eci Instance to remove from instances list.
     * @return True if successful in finding & removing eci, false otherwise.
     */
    public void removeSingleInstance(EdifCellInstance eci) throws UnsupportedResourceTypeException {
        String resourceType = _resourceMapper.getResourceType(eci);
        try {
            decrementResourceCount(resourceType);
        } catch (UnsupportedResourceTypeException e) {
            throw new UnsupportedResourceTypeException("Unsupported resource type for instance " + eci);
        }
        
        _origNumInstances--;
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
    public void removeSingleInstances(Collection<EdifCellInstance> ecis) throws UnsupportedResourceTypeException {
        int cachedOrigNumInstances = _origNumInstances;
        cacheCurrentUtilization();
        for (EdifCellInstance eci : ecis) {
            try {
                removeSingleInstance(eci);
            } catch (UnsupportedResourceTypeException e) {
                _origNumInstances = cachedOrigNumInstances;
                restoreCurrentUtilization();
            }
        }
    }

    /**
     * This function removes the instance eci from the list of instances to be
     * replicated.
     * 
     * @param eci Instance to remove from replication list.
     * @param replicationFactor represents the n factor of NMR
     * @return True if successful in finding & removing eci, false otherwise.
     */
    public void removeNMRInstance(EdifCellInstance eci, int replicationFactor)
            throws UnsupportedResourceTypeException {
        String resourceType = _resourceMapper.getResourceType(eci);
        try {
            for (int index = 1; index < replicationFactor; index++) {
                decrementResourceCount(resourceType);
                _numInstances--;
            }
        } catch (UnsupportedResourceTypeException e) {
            throw new UnsupportedResourceTypeException("Unsupported resource type for instance: " + eci);
        }
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
    public void removeNMRInstances(Collection<EdifCellInstance> ecis, int replicationFactor)
            throws UnsupportedResourceTypeException {
        int cachedNumInstances = _numInstances;        
        cacheCurrentUtilization();
        for (EdifCellInstance eci : ecis) {
            try {
                removeNMRInstance(eci, replicationFactor);
            } catch (UnsupportedResourceTypeException e) {
                _numInstances = cachedNumInstances;
                restoreCurrentUtilization();
                throw e;
            }
        }
    }

//    public void revertToCachedUtilizationNumbers() {
//        _currentUtilizationMap = _cachedCurrentUtilizationMap;
//        _maxUtilizationMap = _cachedMaxUtilizationMap;
//    }

    public void nmrCell(EdifCell cell, int replicationFactor) throws OverutilizationEstimatedStopException, OverutilizationHardStopException, UnsupportedResourceTypeException {
        /*
         * We can go ahead and try to add the instance if it is a primitive
         * otherwise we need to call addInstances on the collection of sub-cell
         * instances within eci.
         */
        if (cell.isLeafCell() == true) {
            String resourceType = _resourceMapper.getResourceType(cell);
            if (resourceType == null) {
                Collection<String> bbResources = cell.getBlackBoxResources();
                if (bbResources == null) {
                    System.out.println("WARNING: Cell " + cell
                            + " does not map to a Xilinx primitve. If it is a black box,"
                            + " it's internal utilization will not be tracked.");
                }
                else {
                    System.out.println("WARNING:\n\t Cell " + cell
                            + " does not map to a Xilinx primitve, and is assumed to be a black box "
                            + "There is utilization information about this blackbox, and that will be "
                            + "used to estimate the total utilization. \n");
                    cacheCurrentUtilization();
                    try {
                        for (int i = 1; i < replicationFactor; i++) {
                            for (String type : bbResources) {
                                incrementResourceCount(type);
                            }
                        }
                    } catch (OverutilizationEstimatedStopException e) {
                        restoreCurrentUtilization();
                        throw e;
                    } catch (OverutilizationHardStopException e) {
                        restoreCurrentUtilization();
                        throw e;
                    } catch (UnsupportedResourceTypeException e) {
                        restoreCurrentUtilization();
                        throw e;
                    }
                }
            }
            else {
                // The instance needs to be counted an additional
                // replicationFactor - 1 times to be replicated
                try {
                    for (int i = 1; i < replicationFactor; i++) {
                        incrementResourceCount(resourceType);
                    }
                } catch (UnsupportedResourceTypeException e) {
                    throw new UnsupportedResourceTypeException("Unsupported resource type for cell: " + cell);
                }
            }
            _numInstances += (replicationFactor - 1);
        }
        else
            nmrInstancesAtomic(cell.getSubCellList(), replicationFactor);
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
            OverutilizationHardStopException, UnsupportedResourceTypeException {
        
        nmrCell(eci.getCellType(), replicationFactor);
    }

    /**
     * Adds all instances in the collection ecis for replication. IMPORTANT:
     * Will only replicate all instances if it can, otherwise it will not
     * replicate any of them.
     * 
     * @param ecis Collection of instances to be added for replication.
     * @param replicationFactor N factor for NMR
     * @throws OverutilizationEstimatedStopException
     * @throws OverutilizationHardStopException
     */
    public void nmrInstancesAtomic(Collection<EdifCellInstance> ecis, int replicationFactor)
            throws OverutilizationEstimatedStopException, OverutilizationHardStopException,
            UnsupportedResourceTypeException {
        nmrInstances(ecis, false, false, replicationFactor, null);
    }

    /**
     * Adds as many instances as it can from the collection ecis for
     * replication, skipping instances which cause estimated stops and/or hard
     * stops. (The logic here is that even if there are hard stops there might
     * be soft logic that could still be added and vice-versa.)
     * 
     * @param ecis Collection of instances to be added for replication.
     * @param replicationFactor N factor for NMR
     * @throws OverutilizationEstimatedStopException
     * @throws OverutilizationHardStopException
     */
    public void nmrInstancesAsManyAsPossible(Collection<EdifCellInstance> ecis, int replicationFactor, Collection<EdifCellInstance> actuallyReplicated)
            throws OverutilizationEstimatedStopException, OverutilizationHardStopException,
            UnsupportedResourceTypeException {
        nmrInstances(ecis, true, true, replicationFactor, actuallyReplicated);
    }

    /**
     * Adds the instances in the collection ecis for replication. Optionally
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
    private void nmrInstances(Collection<EdifCellInstance> ecis, boolean skipEstimatedStops, boolean skipHardStops,
            int replicationFactor, Collection<EdifCellInstance> actuallyReplicated) throws OverutilizationEstimatedStopException, OverutilizationHardStopException,
            UnsupportedResourceTypeException {
        if (ecis == null)
            return;
        List<EdifCellInstance> replicated = new ArrayList<EdifCellInstance>();
        
        int cachedNumInstances = _numInstances;
        cacheCurrentUtilization();
        
        for (EdifCellInstance eci : ecis) {
            try {
                nmrInstance(eci, replicationFactor);
            } catch (OverutilizationEstimatedStopException m) {
                if (skipEstimatedStops == true)
                    continue;
                _numInstances = cachedNumInstances;
                restoreCurrentUtilization();
                throw m;
            } catch (OverutilizationHardStopException m) {
                if (skipHardStops == true)
                    continue;
                _numInstances = cachedNumInstances;
                restoreCurrentUtilization();
                throw m;
            } 
            // reaches here if the instance was successfully added
            replicated.add(eci);
        }
        
        // return to the caller the instances that actually got added in case anything was skipped
        if ((skipEstimatedStops || skipHardStops) && actuallyReplicated != null) {
            actuallyReplicated.addAll(replicated);
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
        // Initialize the resource tracker with the utilization of this instance
        addSingleCell(cell);
    }

    protected void setDesiredCoverageFactor(double factorValue) {
        coverage_factor = factorValue;
    }

    //2 just so it never gets in the way if you don't specify it
    protected double coverage_factor = 2.0;

    ///////////////////////////////////////////////////////////////////
    ////                         protected variables               ////

    protected HashMap<String, Double> _cachedCurrentUtilizationMap = null;

    protected HashMap<String, Double> _currentUtilizationMap;

    protected HashMap<String, Integer> _maxUtilizationMap;
    
    protected int _numInstances;
    
    protected int _origNumInstances;
    
    protected ResourceMapper _resourceMapper;
}
