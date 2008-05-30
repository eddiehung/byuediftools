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

import java.util.Collection;
import java.util.LinkedList;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;

public class PartialReplicationDescription {
    public static final long serialVersionUID = 42L;

    public EdifCell cellToReplicate;

    public Collection<Replication> portsToReplicate;

    public Collection<Replication> instancesToReplicate;

    public Collection<EdifPortRef> portRefsToCut;

    public String toString() {
        return (cellToReplicate + "\n\n\n" + portsToReplicate + "\n\n\n" + instancesToReplicate + "\n\n\n" + portRefsToCut);

        //return portRefsToCut.toString();
    }

    public PartialReplicationDescription() {
        cellToReplicate = null;
        portsToReplicate = new LinkedList<Replication>();
        instancesToReplicate = new LinkedList<Replication>();
        portRefsToCut = new LinkedList<EdifPortRef>();
    }

    public void add(Replication rep) {
        if (rep.getEdifType() == Replication.EdifType.INSTANCE)
            instancesToReplicate.add(rep);
        else if (rep.getEdifType() == Replication.EdifType.PORT)
            portsToReplicate.add(rep);
        //else System.out.println ("nothing added!");
    }

    public void addAll(Collection<Replication> reps) {
        for (Replication rep : reps)
            this.add(rep);
    }

    public void addPortRefs(EdifPortRef epr) {
        portRefsToCut.add(epr);
    }

    public void addPortRefs(Collection<EdifPortRef> eprColl) {
        portRefsToCut.addAll(eprColl);
    }

    public Collection<EdifPortRef> getPortRefs() {
        return portRefsToCut;
    }

    public Collection<EdifCellInstance> getInstances(ReplicationType type) {
        Collection<EdifCellInstance> ecis = new LinkedList<EdifCellInstance>();
        for (Replication rep : instancesToReplicate) {
            if (rep.getReplicationType() == type)
                ecis.add(rep.getEdifCellInstance());
        }
        return ecis;
    }

    public Collection<EdifPort> getPorts(ReplicationType type) {
        Collection<EdifPort> ports = new LinkedList<EdifPort>();
        for (Replication rep : portsToReplicate) {
            if (rep.getReplicationType() == type)
                ports.add(rep.getEdifPort());
        }
        return ports;
    }

}
