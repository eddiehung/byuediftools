/*
 * Represents an edge corresponding between two different EdifPortRef objects.
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
package edu.byu.ece.edif.util.graph;

import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.graph.Edge;

/**
 * Represents an edge corresponding between two different EdifPortRef objects.
 * Provides a method to obtain an EdifPortRef for both the source and sink of
 * the Edge. Note that the nodes attached to this edge do not necessarily have
 * to be the EdifPortRef objects obtained through these methods.
 * 
 * @author wirthlin
 */
public interface EdifPortRefEdge extends Edge {

    /**
     * @return The EdifPortRef associated with the "sink" of this edge.
     */
    public EdifPortRef getSinkEPR();

    /**
     * @return The EdifPortRef associated with the "source" of this edge.
     */
    public EdifPortRef getSourceEPR();

    public EdifNet getNet();
}
