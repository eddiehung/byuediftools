/*
 * A directed graph data structure.
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
import java.util.Collection;

/**
 * Specifies a directed graph data structure.
 * <p>
 * TODO:
 * <ul>
 * <li> Create a new sub-interface ModifiableDirectedGraph
 * <li> Methods to change graph (addEdge, addNode, removeEdge, removeNode)
 * <li> Sort methods
 * </ul>
 */

public interface DirectedGraph extends Serializable {

    /**
     * Determines if the given object is contained as a node within the graph.
     * 
     * @param node Object to test for graph inclusion.
     * @return true if the node is contained within the graph and false if the
     * node is not contained within the graph.
     */
    public boolean containsNode(Object node);

    /**
     * Return a Collection of all nodes in the graph. The class type of the
     * objects in the Collection are not specified.
     */
    public Collection getNodes();

    /**
     * Return a Collection of all edges in the graph. The objects in this
     * collection are of type Edge.
     */
    public Collection<? extends Edge> getEdges();

    /**
     * Return a Collection of node objects in the graph that are direct
     * successors of the node passed in as a parameter. The Collection should be
     * empty if there are no successors.
     */
    public Collection getSuccessors(Object node);

    /**
     * Return a Collection of node objects in the graph that are direct
     * predecessors of the node passed in as a parameter. The Collection should
     * be empty if there are no predecessors.
     */
    public Collection getPredecessors(Object node);

    // public boolean hasSuccessors(Object node);
    // public boolean hasPredecessors(Object node);

    /**
     * Return a Collection of node objects in the graph that are descendants of
     * the node passed in as a parameter. Descendants are all nodes that are
     * reachable from the node. The Collection should be empty if there are no
     * Descendants.
     * <p>
     * This is the same as "reachableNodes" in ptolemy.graph.
     */
    public Collection getDescendents(Object node);

    /**
     * Return a new DirectedGraph object that is a sub-graph of this graph. The
     * sub-graph is based on the Collection of nodes passed in as a parameter.
     * 
     * @param nodes
     * @return a new DirectedGraph object that is a sub-graph of this graph. The
     * sub-graph is based on the Collection of nodes passed in as a parameter.
     */
    public DirectedGraph getSubGraph(Collection nodes);

    /**
     * Return a Collection of node objects in the graph that are ancestors of
     * the node passed in as a parameter. Ancestors are all nodes that can reach
     * the node. The Collection should be empty if there are no ancestors.
     * <p>
     * This is the same as "backwardReachableNodes" in ptolemy.graph.
     */
    public Collection getAncestors(Object node);

    /**
     * Return a Collection of Edge objects that are "inputs" to the given node
     * (i.e. edge goes into the node). The given node is considered the "sink"
     * of the edge. This method will return an empty Collection if there are no
     * input edges.
     */
    public Collection<Edge> getInputEdges(Object node);

    /**
     * Return a Collection of Edge objects that are "outputs" to the given node
     * (i.e. edge goes out of the node). The given node is considered the
     * "source" of the edge. This method will return an empty Collection if
     * there are no output edges.
     */
    public Collection<Edge> getOutputEdges(Object node);

    /**
     * Return the Edge associated with the given source,sink pair. If there is
     * no edge, return null.
     * <p>
     * TODO: What if there is more than one edge?
     */
    public Edge getEdge(Object source, Object sink);

    public DirectedGraph invert();

}
