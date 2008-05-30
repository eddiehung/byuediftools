/*
 * A basic implementation of the HierarchicalInstance interface.
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

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;

//////////////////////////////////////////////////////////////////////////
//// InstanceNode
/**
 * A basic implementation of the HierarchicalInstance interface. This
 * implementation is meant to be used by the FlattenedEdifCell class -- for this
 * reason, nodes are not created recursively; rather, each node is created
 * individually by the breadth first search traversal in the flattening process.
 * This is done so that the hierarchy need only be traversed once.
 */
public class InstanceNode implements HierarchicalInstance {

    /**
     * Construct a new top level InstanceNode based on the given
     * EdifCellInstance. Record the nets of the original isntance as
     * HierarchicalNet objects
     * 
     * @param instance the EdifCellInstance which corresponds to the node to be
     * created
     */
    public InstanceNode(EdifCellInstance instance) {
        this(null, instance);
    }

    /**
     * Construct a new InstanceNode based on the given EdifCellInstance and with
     * the given parent InstanceNode. Record the nets of the original instance
     * as HierarchicalNet objects.
     * 
     * @param parent the parent InstanceNode
     * @param instance the EdifCellInstance which corresponds to the node to be
     * created
     */
    public InstanceNode(InstanceNode parent, EdifCellInstance instance) {
        _parent = parent;
        _self = instance;

        // record nets
        Collection<EdifNet> nets = getCellType().getNetList();
        if (nets != null)
            for (EdifNet net : nets) {
                addHierarchicalNet(net);
            }
    }

    /**
     * Copy Constructor
     * 
     * @param instanceNode the InstanceNode to copy
     */
    public InstanceNode(InstanceNode instanceNode) {
        _parent = instanceNode._parent;
        _self = instanceNode._self;
        _children = new ArrayList(instanceNode._children);
        _nets = new ArrayList(instanceNode._nets);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Construct a new InstanceNode based on the given EdifCellInstance and add
     * it as a child of this node. This will only be done if the given
     * EdifCellInstance is actually a child of this InstanceNode.
     * 
     * @param childInstance the EdifCellInstance on which to base the child node
     * to be created
     * @return a reference to the newly created child node, or null if none was
     * created (if the given EdifCellInstance was not actually a child of this
     * node)
     */
    public InstanceNode addChild(EdifCellInstance childInstance) {
        if (!getCellType().contains(childInstance)) {
            System.err.println("error: attempting to add a child to the wrong parent");
            return null;
        }
        return addChildNoCheck(childInstance);
    }

    /**
     * Add a Child Node without checking to see if it belongs in this Cell
     * 
     * @return a reference to the newly created child node
     */
    public InstanceNode addChildNoCheck(EdifCellInstance childInstance) {
        InstanceNode child = new InstanceNode(this, childInstance);
        if (_children == null)
            _children = new ArrayList<HierarchicalInstance>();
        _children.add(child);
        return child;
    }

    /**
     * @return a Collection of HierarchicalInstances representing the children
     * of this node
     */
    public Collection<HierarchicalInstance> getChildren() {
        if (_children == null)
            return new ArrayList<HierarchicalInstance>(0);
        else
            return _children;
    }

    /**
     * @return the cell type of the EdifCellInstance which corresponds to this
     * node
     */
    public EdifCell getCellType() {
        return _self.getCellType();
    }

    /**
     * @return a Collection of HierarchicalNets which correspond to the nets in
     * the original instance
     */
    public Collection<HierarchicalNet> getHierarchicalNets() {
        return _nets;
    }

    /**
     * @return a reference to the EdifCellInstance which corresponds to this
     * node
     */
    public EdifCellInstance getInstance() {
        return _self;
    }

    /**
     * @return a reference to this node's parent HierarchicalInstance
     */
    public HierarchicalInstance getParent() {
        return _parent;
    }

    /**
     * @return a boolean representing whether or not this is a leaf node (i.e.
     * the cell type of the corresponding EdifCellInstance is a leaf cell)
     */
    public boolean isLeafNode() {
        return getCellType().isLeafCell();
    }

    public String toString() {
        return _self.toString();
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private methods                   ////

    /**
     * Given an EdifNet, construct a corresponding HierarchicalNet and add it to
     * the list of nets.
     * 
     * @param net the EdifNet on which to base the HierarchicalNet
     * @return a boolean indicating whether the add was successful (the add will
     * fail if this node already contains a HierarchicalNet based on the given
     * EdifNet).
     */
    private boolean addHierarchicalNet(EdifNet net) {
        HierarchicalNet hierarchicalNet = new UniqueHierarchyNet(this, net);
        if (_nets == null)
            _nets = new ArrayList<HierarchicalNet>();
        else if (_nets.contains(hierarchicalNet))
            return false;
        _nets.add(hierarchicalNet);
        return true;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /**
     * A Collection of child nodes
     */
    private Collection<HierarchicalInstance> _children;

    /**
     * A Collection of HierarchicalNets which correspond to nets in the original
     * instance
     */
    private Collection<HierarchicalNet> _nets;

    /**
     * A reference to the node's parent InstanceNode
     */
    private InstanceNode _parent;

    /**
     * A reference to the original EdifCellInstance
     */
    private EdifCellInstance _self;
}
