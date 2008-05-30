/*
 * An interface for a device utilization tracker
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

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;

//////////////////////////////////////////////////////////////////////////
//// DeviceUtilizationTracker
/**
 * An interface for a device utilization tracker.
 * 
 * @author Keith Morgan
 * @version $Id$
 */
public interface DeviceUtilizationTracker {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    public void addSingleInstance(EdifCellInstance eci) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, UnsupportedResourceTypeException;

    public void addSingleInstances(Collection<EdifCellInstance> ecis) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, UnsupportedResourceTypeException;

    public void cacheCurrentUtilizationNumbers();

    public void decrementResourceCount(String resourceType) throws UnsupportedResourceTypeException;

    public void decrementResourceCount(EdifCellInstance eci) throws UnsupportedResourceTypeException;

    public void decrementVoterCount() throws UnsupportedResourceTypeException;

    public void excludeCellTypeFromNMR(EdifCellInstance eci);

    public void excludeCellTypeFromNMR(String cellType);

    public void excludeInstanceFromNMR(EdifCellInstance eci);

    public void excludeInstanceFromNMR(String instanceName);

    public Collection<EdifCellInstance> getCurrentInstances();

    public Collection<EdifCellInstance> getCurrentNMRInstances();

    public int getResourceLimit(EdifCell cell) throws UnsupportedResourceTypeException;

    public int getResourceLimit(EdifCellInstance eci) throws UnsupportedResourceTypeException;

    public String getResourceType(EdifCellInstance eci);

    public double getResourceUtilization(EdifCell cell) throws UnsupportedResourceTypeException;

    public double getResourceUtilization(EdifCellInstance eci) throws UnsupportedResourceTypeException;

    public int getVoterCount();

    public void incrementResourceCount(String resourceType) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, UnsupportedResourceTypeException;

    public void incrementResourceCount(EdifCellInstance eci) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, UnsupportedResourceTypeException;

    public void incrementVoterCount() throws OverutilizationHardStopException, OverutilizationEstimatedStopException;

    public boolean removeSingleInstance(EdifCellInstance eci) throws UnsupportedResourceTypeException;

    public boolean removeSingleInstances(Collection<EdifCellInstance> ecis) throws UnsupportedResourceTypeException;

    public boolean removeNMRInstance(EdifCellInstance eci, int replicationFactor)
            throws UnsupportedResourceTypeException;

    public boolean removeNMRInstances(Collection<EdifCellInstance> ecis, int replicationFactor)
            throws UnsupportedResourceTypeException;

    public void revertToCachedUtilizationNumbers();

    public void nmrInstance(EdifCellInstance eci, int replicationFactor) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, UnsupportedResourceTypeException, DuplicateNMRRequestException;

    public void nmrInstancesAtomic(Collection<EdifCellInstance> ecis, int replicationFactor)
            throws OverutilizationEstimatedStopException, OverutilizationHardStopException,
            UnsupportedResourceTypeException, DuplicateNMRRequestException;

    public void nmrInstancesAsManyAsPossible(Collection<EdifCellInstance> ecis, int replicationFactor) throws OverutilizationEstimatedStopException, OverutilizationHardStopException,
            UnsupportedResourceTypeException, DuplicateNMRRequestException;

    public String toString();
}
