/*
 * Interface for a mathematical edge between two objects.
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
package edu.byu.ece.graph;

import java.io.Serializable;

/**
 * An interface representing a mathematical edge between two objects.
 * <p>
 * An edge object contains a source object and a sink object. The actual
 * implementation of this interface can return an object of any type.
 * 
 */
public interface Edge extends Serializable {

    /**
     * Return the source object of a mathematical edge.
     * 
     * @return Source object.
     */
    public Object getSource();

    /**
     * Return the sink object of a mathematical edge.
     * 
     * @return sink object.
     */
    public Object getSink();

    /**
     * Return a new edge object that is the inverse of this object. The
     * resulting Edge will swap the source and sink objects.
     * 
     * @return sink object.
     */
    public Edge invert();

}
