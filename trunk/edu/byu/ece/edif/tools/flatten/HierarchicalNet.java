/*
 * Interface for a reference to an original EdifNet in an instance hierarchy.
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
//// HierarchicalNet
/**
 * A interface for representing a unique reference to an original EdifNet in an
 * instance hierarchy. There is no unique object for a unique net in the Edif
 * data structure (although it can be inferred). Objects implementing this
 * interface represent a unique net within the hierarchy.
 */
public interface HierarchicalNet extends Serializable {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * @return a reference to the original EdifNet
     */
    public String getOriginalNetName();

    public String getOriginalOldNetName();

    /**
     * @return a reference to the parent HierarchicalInstance node
     */
    public HierarchicalInstance getParent();
}
