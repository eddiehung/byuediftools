/*
 * An abstract class that represents an EdifCell as a Graph data structure.
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

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.graph.BasicGraph;

/**
 * An abstract class that represents an EdifCell as a Graph data structure. This
 * abstract class is used for Edif graph classes that store the connectivity
 * information as a Mapping between node objects and their input and output
 * edges. Default implementations are provided for getAcestors, getDescendents,
 * getSuccessors, and getPredecessor. Extending classes must implement:
 * getInputEdges, getOutputEdges, getNodes and getEdges. This class also
 * provides method for determining all ancestors and descendents.
 * 
 * @since Created on Jan 23, 2006
 */
public abstract class AbstractEdifGraph extends BasicGraph {

    public AbstractEdifGraph() {
        super();
    }

    public AbstractEdifGraph(int i) {
        super(i);
    }

    public AbstractEdifGraph(AbstractEdifGraph graph) {
        super(graph);
    }

    /**
     * Provide the EdifCell object that this graph is based on.
     * 
     * @return EdifCell object used to create this graph.
     */
    public abstract EdifCell getCell();

    //public abstract Collection getTopLevelPortNodes();

    //public abstract boolean isNodeTopLevelPort(Object node);

}
