/*
 * Manages net connectivity during cell flattening
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
package edu.byu.ece.edif.tools.flatten;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.byu.ece.edif.core.EdifSingleBitPort;

//////////////////////////////////////////////////////////////////////////
//// NetConnections
/**
 * Manages net connectivity during cell flattening. Keeps track of which nets
 * connect to nets on a higher level and also of which nets need to be joined.
 * This class operates on PseudoNets rather than real nets.
 */
public class NetConnections {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Add a connection from the given PseudoNet to the given InstanceNode on
     * the given EdifSingleBitPort. This will create an entry in a map specific
     * to the InstanceNode that will be used in the next lower level of
     * hierarchy to know which net to connect to.
     */
    public void addConnection(PseudoNet newNet, InstanceNode instanceNode, EdifSingleBitPort esbp) {
        Map<EdifSingleBitPort, PseudoNet> portNetMap = _connections.get(instanceNode);
        if (portNetMap == null) {
            portNetMap = new LinkedHashMap<EdifSingleBitPort, PseudoNet>();
            _connections.put(instanceNode, portNetMap);
        }
        portNetMap.put(esbp, newNet);
    }

    /**
     * Find out if the given PseudoNet is part of a group of nets that should be
     * joined and return that group if it is.
     * 
     * @param net the PseudoNet to check for joining
     * @return a Collection representing the group of PseudoNets to be joined
     * that the given net belongs to or null if it is not part of any list of
     * nets to be joined
     */
    public Collection<PseudoNet> getJoinedNetList(PseudoNet net) {
        for (Collection<PseudoNet> list : _joinedNetLists) {
            if (list.contains(net))
                return list;
        }
        return null;
    }

    /**
     * Mark the given PseudoNets as needing to be joined. This method does not
     * actually join the nets. Nets may be marked sequentially (i.e. marking
     * nets A and B and then marking nets B and C will cause nets A, B, and C to
     * all be marked together).
     * 
     * @param joinedNets a Collection of PseudoNets which should marked as
     * needing to be joined
     */
    public void markToJoin(Collection<PseudoNet> joinedNets) {
        Collection<PseudoNet> matchingList = null;
        for (Collection<PseudoNet> joinedList : _joinedNetLists) {
            for (PseudoNet net : joinedNets) {
                if (joinedList.contains(net)) {
                    matchingList = joinedList;
                    break;
                }
            }
            if (matchingList != null)
                break;
        }
        if (matchingList != null)
            matchingList.addAll(joinedNets);
        else
            _joinedNetLists.add(joinedNets);
    }

    /**
     * Given an InstanceNode and an EdifSingleBitPort, find the PseudoNet in the
     * next higher level of hierarchy that connects to the instance on the given
     * port. The query is made to a map specific to the InstanceNode.
     * 
     * @param instanceNode the InstanceNode to lookup
     * @param esbp the EdifSingleBitPort by which the instance is connected to
     * the next higher level
     * @return the PseudoNet from the next higher level of hierarchy that
     * connects to the instance on the given port or null if none is found
     */
    public PseudoNet query(InstanceNode instanceNode, EdifSingleBitPort esbp) {
        Map<EdifSingleBitPort, PseudoNet> portNetMap = _connections.get(instanceNode);
        if (portNetMap == null)
            return null;
        return portNetMap.get(esbp);
    }

    /**
     * This map holds all of the net connectivity information for determining
     * which nets match nets on a higher level of hierarchy
     */
    Map<InstanceNode, Map<EdifSingleBitPort, PseudoNet>> _connections = new LinkedHashMap<InstanceNode, Map<EdifSingleBitPort, PseudoNet>>();

    /**
     * A List of Collections of PseudoNets representing groups of PseudoNets
     * that need to be joined
     */
    List<Collection<PseudoNet>> _joinedNetLists = new ArrayList<Collection<PseudoNet>>();
}
