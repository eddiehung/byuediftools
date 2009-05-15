/*
 * A net in the process of being created during the cell flattening process.
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
import java.util.LinkedHashSet;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.NamedPropertyObject;

//////////////////////////////////////////////////////////////////////////
//// PseudoNet
/**
 * Represents a net in the process of being created during the cell flattening
 * process. Contains information about the net's very top level ports as well as
 * any other connections. This class' usefulness comes from that fact that many
 * of these nets may need to be joined in the flattening process. It is cleaner
 * and easier to join multiple PseudoNets into one EdifNet than to join multiple
 * EdifNets after they have already been created.
 */
public class PseudoNet extends NamedPropertyObject {

    /**
     * Construct a new PseudoNet with the given parent and name.
     * 
     * @param parent the EdifCell to which this PseudoNet belongs
     * @param name an EdifNameable representing the net's name
     */
    //    public PseudoNet(EdifCell parent, EdifNameable name) {
    //        super(name);
    //        _parent = parent;
    //        _topLevelConnections = new LinkedHashSet<EdifSingleBitPort>();
    //        _connections = new LinkedHashSet<Connection>();
    //    }
    public PseudoNet(EdifCell parent, EdifNet originalNet) {
        super(originalNet.getEdifNameable());
        _parent = parent;
        _topLevelConnections = new LinkedHashSet<EdifSingleBitPort>();
        _connections = new LinkedHashSet<Connection>();
        _originalNets = new LinkedHashSet<EdifNet>();
        _originalNets.add(originalNet);
        _hierarchicalNets = new LinkedHashSet<HierarchicalNet>();
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Add a connection to the PseudoNet.
     * 
     * @param instance the EdifCellInstance to which the net should connect
     * @param esbp the EdifSingleBitPort the instance to which the net should
     * connect
     */
    public void addConnection(EdifCellInstance instance, EdifSingleBitPort esbp) {
        _connections.add(new Connection(instance, esbp));
    }

    /**
     * Register an EdifNet as one of the original nets that corresponds to this
     * new one
     * 
     * @param originalNet the original EdifNet to register
     */
    public void addOriginalNet(EdifNet originalNet) {
        _originalNets.add(originalNet);
    }

    public void addHierarchicalNet(HierarchicalNet hierarchicalNet) {
    	_hierarchicalNets.add(hierarchicalNet);
    }
    
    public Set<HierarchicalNet> getHierarchicalNets() {
    	return _hierarchicalNets;
    }
    
    /**
     * Add a top level connection to the PseudoNet. This refers to a top level
     * port of the top level cell in the design.
     * 
     * @param esbp the EdifSingleBitPort on the top cell to connect to
     */
    public void addTopLevelConnection(EdifSingleBitPort esbp) {
        _topLevelConnections.add(esbp);
    }

    /**
     * @return a Collection of all original EdifNets that correspond to this new
     * PseudoNet
     */
    public Collection<EdifNet> getOriginalNets() {
        return _originalNets;
    }

    /**
     * Create EdifPortRefs which represent this PseudoNets's connection
     * information as applied to the given EdifNet. This is used to create a new
     * EdifNet with this PseudoNet's connectivity.
     * 
     * @param net the EdifNet to use as a reference in creating the EdifPortRefs
     * @return a Collection of EdifPortRefs representing connectivity as applied
     * to the given EdifNet
     */
    public Collection<EdifPortRef> getPortRefs(EdifNet net) {
        Collection<EdifPortRef> portRefs = new ArrayList<EdifPortRef>();
        for (EdifSingleBitPort esbp : _topLevelConnections) {
            EdifPortRef epr = new EdifPortRef(net, esbp, null);
            portRefs.add(epr);
        }
        for (Connection connection : _connections) {
            EdifPortRef epr = new EdifPortRef(net, connection.getEsbp(), connection.getInstance());
            portRefs.add(epr);
        }
        return portRefs;
    }

    /**
     * Apply this PseudoNet's connectivity to the given EdifNet
     * 
     * @param net the EdifNet to which this PseudoNet's connectivity should be
     * applied
     */
    public void insertPortRefs(EdifNet net) {
        for (EdifPortRef epr : getPortRefs(net)) {
            net.addPortConnection(epr);
        }
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /**
     * Represents the rest of the net's connections
     */
    private Set<Connection> _connections;

    /**
     * A List of every original net that corresponds to this new one
     */
    private Set<EdifNet> _originalNets;

    /**
     * A reference to the net's parent cell
     */
    private EdifCell _parent;

    /**
     * Represents the net's very top level connections
     */
    private Set<EdifSingleBitPort> _topLevelConnections;
    
    private Set<HierarchicalNet> _hierarchicalNets;

    ///////////////////////////////////////////////////////////////////
    ////                         private classes                   ////

    /**
     * Represents a non top level connection on a PseudoNet. This class is a
     * data structure for holding an EdifCellInstance-EdifSingleBitPort pair.
     */
    private class Connection {

        /**
         * Construct a new Connection with the given EdifCellInstance and
         * EdifSingleBitPort.
         * 
         * @param instance the EdifCellInstance to which the connection is made
         * @param esbp the EdifSingleBitPort on which the connection is made
         */
        public Connection(EdifCellInstance instance, EdifSingleBitPort esbp) {
            _instance = instance;
            _esbp = esbp;
        }

        ///////////////////////////////////////////////////////////////////
        ////                         public methods                    ////

        /**
         * @return the Connection's EdifSingleBitPort
         */
        public EdifSingleBitPort getEsbp() {
            return _esbp;
        }

        /**
         * @return the Connection's EdifCellInstance
         */
        public EdifCellInstance getInstance() {
            return _instance;
        }

        /**
         * @return a boolean representing whether or not the Connection object
         * is equal to another. Equality is done using == on both the
         * EdifCellInstance and the EdifSingleBitPort
         */
        public boolean equals(Object o) {
            if (!(o instanceof Connection))
                return false;
            Connection c = (Connection) o;
            return (c._instance == _instance && c._esbp == _esbp);
        }

        ///////////////////////////////////////////////////////////////////
        ////                         private variables                 ////

        /**
         * The Connection's EdifCellInstance
         */
        private EdifCellInstance _instance;

        /**
         * The Connection's EdifSingleBitPort
         */
        private EdifSingleBitPort _esbp;
    }
}
