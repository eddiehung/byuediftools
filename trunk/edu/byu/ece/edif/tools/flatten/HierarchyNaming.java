/*
 * Allows accessing hierarchical instances and nets by their hierarchical names.
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

//////////////////////////////////////////////////////////////////////////
//// HierarchyNaming
/**
 * An interface for accessing hierarchical instances and nets by their
 * hierarchical names.
 */
public interface HierarchyNaming extends Serializable {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Given the head node of a HierarchicalInstance hierarchy and a complete
     * hierarchical instance name, get the corresponding HierarchicalInstance
     * node.
     * 
     * @param head the head node of the HierarchicalInstance hierarchy
     * @param name the complete hierarchical name of the desired
     * HierarchicalInstance node
     * @return the HierarchicalInstance node corresponding to the given name
     */
    public HierarchicalInstance getHierarchicalInstance(HierarchicalInstance head, String name);

    /**
     * Given a HierarchicalInstance node, get it's complete hierarchical name.
     * 
     * @param node the HierarchicalInstance node whose name is desired
     * @return the complete hierarchical name of the HierarchicalInstance node
     */
    public String getHierarchicalInstanceName(HierarchicalInstance node);

    /**
     * Given the head node of a HierarchicalInstance hierachy and a complete
     * hierarchical net name, get the corresponding HierarchicalNet.
     * 
     * @param head the head node of the HierarchicalInstance hierarchy
     * @param name the complete hierarchical name of the desired HierarchicalNet
     * @return the HierarchicalNet corresponding to the given name
     */
    public HierarchicalNet getHierarchicalNet(HierarchicalInstance head, String name);

    /**
     * Given a HierarchicalNet, get it's complete hierarchical name.
     * 
     * @param hierarchicalNet the HierarchicalNet whose name is desired
     * @return the complete hierarchical name of the HierarchicalNet
     */
    public String getHierarchicalNetName(HierarchicalNet hierarchicalNet);

}
