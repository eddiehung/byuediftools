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

import edu.byu.ece.edif.core.NamedPropertyObject;
import edu.byu.ece.edif.tools.replicate.nmr.EdifNameableStringReference;

public class Triplication extends AbstractReplication {
    public static final long serialVersionUID = 42L;

    public Triplication(Object toBeReplicated) throws ReplicationException {
        super(toBeReplicated);
        _replicationFactor = 3;
        _replicationType = ReplicationType.TRIPLICATE;
    }

    public int getReplicationFactor() {
        return _replicationFactor;
    }

    public Triplication toStringReference() throws ReplicationException {
        if (!(_toBeReplicated instanceof EdifNameableStringReference))
            return new Triplication(new EdifNameableStringReference((NamedPropertyObject) _toBeReplicated));
        else
            return null; //should it be implemented this way, or return this (or a clone)?
    }

    protected int _replicationFactor;
}
