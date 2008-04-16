/*
 * 
 *

 * Copyright (c) 2008 Brigham Young University
 *
 * This file is part of the BYU EDIF Tools.
 * 
 * BYU EDIF Tools is free software: you may redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * BYU EDIF Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License is included with the BYU
 * EDIF Tools. It can be found at /edu/byu/edif/doc/gpl2.txt. You may
 * also get a copy of the license at <http://www.gnu.org/licenses/>.
 *
 */
package edu.byu.ece.graph;

import java.util.Collection;


public interface HierarchicalGraph extends DirectedGraph {
	/**
	 * @param node A node to look for in the inner graph
	 * @return true if the given node is found in the inner graph
	 */
	public boolean containsInnerNode(Object node);

	/**
	 * @return The inside graph that this graph is built upon
	 */
	public DirectedGraph getInnerGraph();

	/**
	 * @param edge An Edge object in thie HierarchicalGraph
	 * @return A Collection of Edge objects corresponding to the given
	 *   edge's inner edges (those that make up this edge)
	 */
	public Collection<? extends Edge> getInnerEdges(Edge edge);

	/**
	 * @param node A node in this HierarchicalGraph
	 * @return A Collection of nodes from the inner graph that correspond
	 *   to the given node in this graph
	 */
	public Collection getInnerNodes(Object node);

	/**
	 * @param node A node in this HierarchicalGraph
	 * @return A DirectedGraph object which is the subgraph corresponding to
	 *   the given node's inner nodes
	 */
	public DirectedGraph getNodeGraph(Object node);

	/**
	 * @param node A node in the inner graph
	 * @return The node in this HierarchicalGraph that corresponds to the
	 *   given inner node
	 */
	public Object getParentNode(Object node);
}
