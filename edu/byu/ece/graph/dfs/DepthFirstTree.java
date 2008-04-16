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
package edu.byu.ece.graph.dfs;

import java.util.Collection;
import java.util.List;

import edu.byu.ece.graph.DirectedGraph;



/**
 * A graph that represents a single tree from a depth first search. This tree is
 * created from a root node from a DirectedGraph object. As this interface
 * extends DirectedGraph, all graph methods are available and this tree responds
 * like a conventional graph.
 * 
 * The edges in the graph are all classified into one of the following
 * catagories:
 * <ul>
 * <li> Successor edge: an edge that leads to a direct successor in the depth
 * first traversal,
 * <li> Forward edge: an edge that leads to a successor (but not direct
 * successor) in the depth first traversal,
 * <li> Cross edge: An edge that leads to a node in a different Depth First tree
 * (not this tree),
 * <li> Back edge: An edge that leads to a predecessor node in the depth first
 * tree (a feedback edge).
 * </ul>
 * 
 * By definition, the tree is acyclic and the traditional graph methods
 * (getInputEdges, getOutputEdges, getSuccessors, etc.) will only use the
 * successor and forward edges. The cross edges are not considered part of this
 * graph as they lead to nodes that do not belong in the graph. The back edges
 * are also not included within the graph as they would make the tree acyclic.
 * These edges, however, are available through accessor methods in the
 * interface.
 */
public interface DepthFirstTree extends DirectedGraph {

	/** 
	 * Return the back edges from the tree. 
	 */
	public Collection getBackEdges();

	/** 
	 * Return all forward edges from the tree. 
	 */
	public Collection getForwardEdges();

	/**
	 * Return all cross edges from the tree. 
	 */
	public Collection getCrossEdges();

	/**
	 * Return all successor edges from the tree. 
	 */
	public Collection getSuccessorEdges();
	
	/**
	 * Return a sorted list of nodes in the tree based on their depth-first
	 * search finish time. The nodes that finish first are at the top of this
	 * list.
	 */
    public List getFinishList();

	/**
	 * Get the parent depth-first search graph. 
	 */	
	public DepthFirstSearchForest getParent();
	
	/**
	 * Get the root node of the tree 
	 */	
	public Object getRoot();

	/**
	 * Return a topological sort of the given depth first traversal. This is
	 * simply the reverse of the finish list.
	 * 
	 * @return A topological sort of the given depth first traversal. This is
	 *         simply the reverse of the finish list.
	 */
    public List getTopologicalSort();
    
}
