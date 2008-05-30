/*
 * A basic implementation of the HierarchicalNet interface
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

import edu.byu.ece.edif.core.EdifNet;

//////////////////////////////////////////////////////////////////////////
//// UniqueHierarchyNet
/**
 * A basic implementation of the HierarchicalNet interface.
 */
public class UniqueHierarchyNet implements HierarchicalNet {

    /**
     * Construct a new UniqueHierarchyNet based on the given original EdifNet
     * and with the given parent HierarchicalInstance
     * 
     * @param parent the parent HierarchicalInstance of the net
     * @param originalNet the original EdifNet
     */
    public UniqueHierarchyNet(HierarchicalInstance parent, EdifNet originalNet) {
        _parent = parent;
        _originalNet = originalNet;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * @return a boolean representing the equality of another object with this
     * UniqueHierarchyNet. Equality is checked via the == operator on both the
     * parent HierarchicalInstance and the original EdifNet
     */
    public boolean equals(Object o) {
        if (!(o instanceof UniqueHierarchyNet))
            return false;
        UniqueHierarchyNet hierarchyNet = (UniqueHierarchyNet) o;
        return (_parent == hierarchyNet._parent && _originalNet == hierarchyNet._originalNet);
    }

    /**
     * @return a reference to the original EdifNet
     */
    public EdifNet getOriginalNet() {
        return _originalNet;
    }

    /**
     * @return a reference to the parent HierarchicalInstance
     */
    public HierarchicalInstance getParent() {
        return _parent;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /**
     * A reference to the original EdifNet
     */
    private EdifNet _originalNet;

    /**
     * A reference to the parent HierarchicalInstance
     */
    private HierarchicalInstance _parent;
}
