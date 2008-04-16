/*
 * TODO: Insert class description here.
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
package edu.byu.ece.edif.tools.replicate;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.tools.replicate.nmr.EdifNameableStringReference;

public abstract class AbstractReplication implements Replication {
    public static final long serialVersionUID = 42L;

    public AbstractReplication(Object toBeReplicated) throws ReplicationException {
        if (toBeReplicated instanceof EdifCellInstance)
            _edifType = EdifType.INSTANCE;
        else if (toBeReplicated instanceof EdifPort)
            _edifType = EdifType.PORT;
        else if (toBeReplicated instanceof EdifNameableStringReference)
            _edifType = EdifType.STRINGREF;
        else
            throw new ReplicationException("Unsupported Edif object");
        _toBeReplicated = toBeReplicated;
    }

    public EdifPort getEdifPort() {
        if (_edifType == EdifType.PORT)
            return (EdifPort) _toBeReplicated;
        else
            return null;
    }

    public EdifCellInstance getEdifCellInstance() {
        if (_edifType == EdifType.INSTANCE)
            return (EdifCellInstance) _toBeReplicated;
        else
            return null;
    }

    public EdifNameableStringReference getStringRef() {
        if (_edifType == EdifType.STRINGREF)
            return (EdifNameableStringReference) _toBeReplicated;
        else
            return null;
    }

    public EdifType getEdifType() {
        return _edifType;
    }

    public ReplicationType getReplicationType() {
        return _replicationType;
    }

    public void setObjectToBeReplicated(Object toBeReplicated) throws ReplicationException {
        if (toBeReplicated instanceof EdifCellInstance)
            _edifType = EdifType.INSTANCE;
        else if (toBeReplicated instanceof EdifPort)
            _edifType = EdifType.PORT;
        else if (toBeReplicated instanceof EdifNameableStringReference)
            _edifType = EdifType.STRINGREF;
        else
            throw new ReplicationException("Unsupported Edif object");
        _toBeReplicated = toBeReplicated;
    }

    protected Object _toBeReplicated;

    protected EdifType _edifType;

    protected ReplicationType _replicationType;

}
