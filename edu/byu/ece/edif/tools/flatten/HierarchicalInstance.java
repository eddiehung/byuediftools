/*
 * An interface for representing a node in a hierarchy of instances.
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

import java.io.Serializable;
import java.util.Collection;

//////////////////////////////////////////////////////////////////////////
//// HierarchicalInstance
/**
 * An interface for representing a node in a hierarchy of instances. This
 * is a simple tree data structure for retaining naming information of
 * a flattened circuit.
 * 
 */
public interface HierarchicalInstance extends Serializable {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * @return the name of the cell type of the original EdifCellInstance
     * associated with this node
     */
    public String getCellTypeName();

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * @return a Collection of all of the node's children
     */
    public Collection<HierarchicalInstance> getChildren();

    /**
     * @return a Collection of all of the HierarchicalNets associated with the
     * node
     */
    public Collection<HierarchicalNet> getHierarchicalNets();

    /**
     * @return the name of the original EdifCellInstance associated with this node
     */
    public String getInstanceName();

    /**
     * @return the *old* name of the original EdifCellInstance associated with this node
     */
    public String getInstanceOldName();
    
    /**
     * @return this node's parent HierarchicalInstance
     */
    public HierarchicalInstance getParent();

    /**
     * @return a boolean indicating whether this node is a leaf node
     */
    public boolean isLeafNode();
}
