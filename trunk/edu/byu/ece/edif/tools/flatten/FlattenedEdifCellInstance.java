/*
 * Represents a flattened EdifCellInstance
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

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.InvalidEdifNameException;

//////////////////////////////////////////////////////////////////////////
//// FlattenedEdifCellInstance
/**
 * Represents a flattened EdifCellInstance. This class is used so that each
 * flattened version of an EdifCellInstance will have a reference to its
 * corresponding HierarchicalInstance node.
 * <p>
 * TODO: Remove this class? Nobody uses this reference to the hierarchical
 * instance
 */
public class FlattenedEdifCellInstance extends EdifCellInstance {

    public FlattenedEdifCellInstance(EdifNameable name, EdifCell parent, EdifCell type,
            HierarchicalInstance hierarchicalInstance) {
        super(name, parent, type);
        _hierarchicalInstance = hierarchicalInstance;
    }

    public FlattenedEdifCellInstance(EdifNameable name, EdifCell parent, HierarchicalInstance hierarchicalInstance) {
        super(name, parent);
        _hierarchicalInstance = hierarchicalInstance;
    }

    public FlattenedEdifCellInstance(EdifNameable name, HierarchicalInstance hierarchicalInstance) {
        super(name);
        _hierarchicalInstance = hierarchicalInstance;
    }

    public FlattenedEdifCellInstance(String name, EdifCell parent, EdifCell type,
            HierarchicalInstance hierarchicalInstance) throws InvalidEdifNameException {
        super(name, parent, type);
        _hierarchicalInstance = hierarchicalInstance;
    }

    public FlattenedEdifCellInstance(String name, EdifCell parent, HierarchicalInstance hierarchicalInstance)
            throws InvalidEdifNameException {
        super(name, parent);
        _hierarchicalInstance = hierarchicalInstance;
    }

    public FlattenedEdifCellInstance(String name, HierarchicalInstance hierarchicalInstance)
            throws InvalidEdifNameException {
        super(name);
        _hierarchicalInstance = hierarchicalInstance;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Get the HierarchicalInstance node associated with this instance.
     */
    public HierarchicalInstance getHierarchicalInstance() {
        return _hierarchicalInstance;
    }

    /**
     * Set the HierarchicalInstance node associated with this instance.
     */
    public void setHierarchicalInstance(HierarchicalInstance instance) {
        _hierarchicalInstance = instance;
    }

    /**
     * Creates a string showing the hierarchy of the EdifCell. This method was
     * created to return a valid EDIF name.
     * 
     * @return The full hierarchical name of the EdifCell.
     */
    public String getHierarchicalEdifName() {
        HierarchicalInstance hi = _hierarchicalInstance;
        String retVal = hi.getInstance().getName();
        hi = hi.getParent();
        while (hi.getParent() != null) {
            retVal = hi.getInstance().getName() + "/" + retVal;
            hi = hi.getParent();
        }
        return retVal;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /**
     * The HierarchicalInstance node associated with this instance
     */
    private HierarchicalInstance _hierarchicalInstance;
}
