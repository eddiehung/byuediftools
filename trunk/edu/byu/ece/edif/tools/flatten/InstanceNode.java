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
import java.util.List;

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
     * EdifCellInstance. Record the nets of the original instance as
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
        _isLeafNode = instance.getCellType().isLeafCell();
        _origInstanceName = instance.getName();
        _origInstanceOldName = instance.getOldName();
        _origCellTypeName = instance.getCellType().getName();
    }

    /**
     * This is a constructor for the top-level cell that has not yet been
     * 'instanced'
     */
    public InstanceNode(InstanceNode parent, EdifCell instance) {
        _parent = parent;
        _isLeafNode = instance.isLeafCell();
        _origInstanceName = null; // not instanced, so has no name
        _origInstanceOldName = null;
        _origCellTypeName = instance.getName();
    }

    /**
     * Copy Constructor
     * 
     * @param instanceNode the InstanceNode to copy
     */
    public InstanceNode(InstanceNode instanceNode) {
        _parent = instanceNode._parent;
        _origInstanceName = instanceNode._origInstanceName;
        _origCellTypeName = instanceNode._origCellTypeName;
        _origInstanceOldName = instanceNode._origInstanceOldName;
        _children = new ArrayList<HierarchicalInstance>(instanceNode._children);
        _netList = new ArrayList<HierarchicalNet>(instanceNode._netList);
        _isLeafNode = instanceNode._isLeafNode;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Construct a new InstanceNode based on the given EdifCellInstance and add
     * it as a child of this node.
     * 
     * @param childInstance the EdifCellInstance on which to base the child node
     * to be created
     * @return a reference to the newly created child node
     */
    public InstanceNode addChild(EdifCellInstance childInstance) {
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
     * @return the name of the cell type of the original EdifCellInstance which
     * corresponds to this node.
     */
    public String getCellTypeName() {
        return _origCellTypeName;
    }

    /**
     * @return a Collection of HierarchicalNets which correspond to the nets in
     * the original instance
     */
    public Collection<HierarchicalNet> getHierarchicalNets() {
        return _netList;
    }

    /**
     * @return the name of the original EdifCellInstance which corresponds to this node
     */
    public String getInstanceName() {
        return _origInstanceName;
    }

    public String getInstanceOldName() {
    	return _origInstanceOldName;
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
        return _isLeafNode;
    }

    public String toString() {
        return "Original Instance Name: " + _origInstanceName + ", Original Cell Type: " + _origCellTypeName;
    }

    /**
     * Given an EdifNet, construct a corresponding HierarchicalNet and add it to
     * the list of nets.
     * 
     * @param net the EdifNet on which to base the HierarchicalNet
     * @return a boolean indicating whether the add was successful (the add will
     * fail if this node already contains a HierarchicalNet based on the given
     * EdifNet).
     */
    public HierarchicalNet addHierarchicalNet(EdifNet net) {
        if (_netList == null)
            _netList = new ArrayList<HierarchicalNet>();
        HierarchicalNet hierarchicalNet = new UniqueHierarchyNet(this, net);
        _netList.add(hierarchicalNet);
        return hierarchicalNet;
    }

    
    ///////////////////////////////////////////////////////////////////
    ////                         private methods                   ////

    
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
    private List<HierarchicalNet> _netList;

    /**
     * A reference to the node's parent InstanceNode
     */
    private InstanceNode _parent;

    /**
     * The name of the original EdifCellInstance
     */
    private String _origInstanceName;
    
    /**
     * The old name of the EdifCellInstance
     */
    private String _origInstanceOldName;
    
    /**
     * The name of the cell type of the original EdifCellInstance
     */
    private String _origCellTypeName;
    
    private boolean _isLeafNode;
}
