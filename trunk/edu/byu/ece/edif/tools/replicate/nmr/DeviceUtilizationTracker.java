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
    
    public void addSingleCell(EdifCell cell) throws OverutilizationEstimatedStopException, OverutilizationHardStopException, UnsupportedResourceTypeException;

    public void decrementResourceCount(String resourceType) throws UnsupportedResourceTypeException;

    public int getResourceLimit(EdifCell cell) throws UnsupportedResourceTypeException;

    public int getResourceLimit(EdifCellInstance eci) throws UnsupportedResourceTypeException;

    public double getResourceUtilization(EdifCell cell) throws UnsupportedResourceTypeException;

    public double getResourceUtilization(EdifCellInstance eci) throws UnsupportedResourceTypeException;

    public void incrementResourceCount(String resourceType) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, UnsupportedResourceTypeException;

    public void removeSingleInstance(EdifCellInstance eci) throws UnsupportedResourceTypeException;

    public void removeSingleInstances(Collection<EdifCellInstance> ecis) throws UnsupportedResourceTypeException;

    public void removeNMRInstance(EdifCellInstance eci, int replicationFactor)
            throws UnsupportedResourceTypeException;

    public void removeNMRInstances(Collection<EdifCellInstance> ecis, int replicationFactor)
            throws UnsupportedResourceTypeException;

    public void nmrInstance(EdifCellInstance eci, int replicationFactor) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, UnsupportedResourceTypeException;

    public void nmrCell(EdifCell cell, int replicationFactor) throws OverutilizationEstimatedStopException, OverutilizationHardStopException, UnsupportedResourceTypeException;
    
    public void nmrInstancesAtomic(Collection<EdifCellInstance> ecis, int replicationFactor)
            throws OverutilizationEstimatedStopException, OverutilizationHardStopException,
            UnsupportedResourceTypeException;

    public void nmrInstancesAsManyAsPossible(Collection<EdifCellInstance> ecis, int replicationFactor, Collection<EdifCellInstance> actuallyReplicated) throws OverutilizationEstimatedStopException, OverutilizationHardStopException,
            UnsupportedResourceTypeException;

    public String toString();
}
