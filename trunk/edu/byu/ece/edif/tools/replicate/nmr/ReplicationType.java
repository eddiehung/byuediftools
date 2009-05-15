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
package edu.byu.ece.edif.tools.replicate.nmr;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;

/**
 * In this framework, a ReplicationType represents the number of times to
 * replicate things (replication factor), how to restore signals (i.e. voting),
 * and how to name replicated objects. The class also contains code for performing
 * replications.
 */
public interface ReplicationType extends Serializable {
    
    /**
     * @return the replication factor of the replication type
     */
    public int getReplicationFactor();
    
    /**
     * Give a list of organ specifications to implement a "force_no_restore" on the
     * net. Generally, this will just be <code>null</code> because not restoring doesn't
     * require doing anything. This method exists solely so that the replication type can
     * know about anit_restore locations just in case it needs to for some project later on.
     * 
     * @param net
     * @param desc
     * @return
     */
    public List<OrganSpecification> antiRestore(EdifNet net, ReplicationDescription desc);

    /**
     * Give a list of organ specifications to implement a forced restoration on the given net.
     * @param net
     * @param forceRestoreRefs
     * @param desc
     * @return
     */
    public List<OrganSpecification> forceRestore(EdifNet net, List<EdifPortRef> forceRestoreRefs, ReplicationDescription desc);
    
    /**
     * Give a list of organ specifications to implement a forced restoration on the given net.
     * This version of the method checks to see if any of prevSpecs can be reused.
     * @param net
     * @param forceRestoreRefs
     * @param prevSpecs
     * @param desc
     * @return
     */
    public List<OrganSpecification> forceRestore(EdifNet net, List<EdifPortRef> forceRestoreRefs, Collection<OrganSpecification> prevSpecs, ReplicationDescription desc);
    
    /**
     * Give a list of organ specifications to implement the default restoration for the given
     * net. A default restoration is any restoration needed as a consequence of a scale down
     * or up in replication factor (i.e. a triplicated partition feeds into a duplicated partition).
     * 
     * @param net
     * @param desc
     * @return
     */
    public List<OrganSpecification> defaultRestore(EdifNet net, ReplicationDescription desc);
    
    /**
     * Replicate the given cell instance
     * 
     * @param oldInstance
     * @param newCellDefinition
     * @param newTopCell
     * @return
     * @throws EdifNameConflictException
     */
    public List<EdifCellInstance> replicate(EdifCellInstance oldInstance, EdifCell newCellDefinition, EdifCell newTopCell) throws EdifNameConflictException;
    
    /**
     * Replicate the given port
     * 
     * @param oldPort
     * @param newTopCell
     * @return
     * @throws EdifNameConflictException
     */
    public List<EdifPort> replicate(EdifPort oldPort, EdifCell newTopCell) throws EdifNameConflictException;
   
    /**
     * Get a replicated name for the given instance name
     * 
     * @param origName
     * @param domain
     * @return
     */
    public EdifNameable getReplicationInstanceNameable(EdifNameable origName, int domain);

    /**
     * Get a replicated name for the given port name
     * 
     * @param origName
     * @param domain
     * @return
     */
    public EdifNameable getReplicationPortNameable(EdifNameable origName, int domain);

    /**
     * Get a replicated name for the given net name
     * @param origName
     * @param domain
     * @return
     */
    public EdifNameable getReplicationNetNameable(EdifNameable origName, int domain);
    
}
