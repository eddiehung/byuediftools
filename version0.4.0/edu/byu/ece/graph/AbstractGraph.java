/*
 * Directed graph with methods for basic topology traversal operations.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * An abstract implementation of a directed graph that contains default methods
 * for basic topology traversal operations.
 * <p>
 * This class has no state and provides a default implementation for the
 * following methods: {@link #getSuccessors}, {@link #getPredecessors},
 * {@link #getAncestors(Object)}, {@link #getAncestors(Collection)},
 * {@link #getDescendents(Object)}, and {@link #getDescendents(Collection)}.
 * <p>
 * These methods all rely on the following two unimplemented methods to operate
 * correctly: {@link DirectedGraph#getInputEdges(Object)} and
 * {@link DirectedGraph#getOutputEdges(Object)}.
 * <p>
 * This class also provides a default toString and toDotty method.
 * 
 * @author wirthlin
 */
public abstract class AbstractGraph implements DirectedGraph {

    /**
     * @return a Collection of node objects in the graph that are direct
     * successors of the node passed in as a parameter. The Collection should be
     * empty if there are no successors.
     */
    public Collection getSuccessors(Object node) {
        Collection successors = new ArrayList();

        /**
         * To get the successor nodes:
         * <ul>
         * <li>Get the links in which this node is the source.
         * <li>Get the sink nodes of the links.
         * </ul>
         */
        for (Edge link : getOutputEdges(node)) {
            successors.add(link.getSink());
        }
        return successors;
    }

    /**
     * @return a Collection of node objects in the graph that are direct
     * predecessors of the node passed in as a parameter. The Collection should
     * be empty if there are no predecessors.
     */
    public Collection getPredecessors(Object node) {
        Collection predecessors = new ArrayList();

        /**
         * To get the successor nodes:
         * <ul>
         * <li>Get the links in which this node is the sink.
         * <li>Get the source nodes of the links.
         * </ul>
         */
        for (Edge link : getInputEdges(node)) {
            predecessors.add(link.getSource());
        }

        return predecessors;
    }

    /**
     * @return a Collection of node objects in the graph that are ancestors of
     * the node passed in as a parameter. Ancestors are all nodes that can reach
     * the node. The Collection should be empty if there are no ancestors.
     */
    public Collection getAncestors(Object node) {
        return _getAncestorsOrDescendents(node, true);
    }

    /**
     * @return a Collection of node objects in the graph that are ancestors of
     * each of the nodes passed in as a parameter. Ancestors are all nodes that
     * can reach any of the nodes in the Collection. The Collection should be
     * empty if there are no ancestors. Note: this method is not part of the
     * DirectedGraph interface.
     */
    public Collection getAncestors(Collection nodes) {
        return _getAncestorsOrDescendents(nodes, true);
    }

    /**
     * @return a Collection of node objects in the graph that are descendants of
     * the node passed in as a parameter. Descendants are all nodes that are
     * reachable from the node. The Collection should be empty if there are no
     * descendants.
     */
    public Collection getDescendents(Object node) {
        return _getAncestorsOrDescendents(node, false);
    }

    /**
     * @return a Collection of node objects in the graph that are descendants of
     * any of the Nodes in the Collection passed in as a parameter. Descendants
     * are all nodes that are reachable from any node in the Collection. The
     * Collection should be empty if there are no Descendants. Note: this method
     * is not part of the DirectedGraph interface.
     */
    public Collection getDescendents(Collection nodes) {
        return _getAncestorsOrDescendents(nodes, false);
    }

    /**
     * Uses the AbstractGraphToDotty.graphToDotty method to create a Dotty
     * version of this graph.
     */
    public void toDotty(String filename) {
        AbstractGraphToDotty.graphToDotty(this, filename);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Nodes = (" + getNodes().size() + ") {\r\n");
        for (Iterator i = getNodes().iterator(); i.hasNext();) {
            sb.append("  " + i.next() + "\r\n");
        }
        sb.append("}\r\n");
        sb.append("Edges = (" + getEdges().size() + ") {\r\n");
        for (Iterator i = getEdges().iterator(); i.hasNext();) {
            sb.append("  " + i.next() + "\r\n");
        }
        sb.append("}\r\n");
        return sb.toString();
    }

    /**
     * This helper method will provide an ordered list of all the ancestors or
     * Descendants.
     * <p>
     * The algorithm for finding ancestors and descendants is the same and this
     * algorithm will differentiate between them based on the boolean flag.
     * <p>
     * TODO: We need to review this method and make sure that it returns nodes
     * in a topological order. It is not clear whether any other ordering makes
     * sense.
     * 
     * @param nodes
     * @param predecessors
     * @return
     */
    protected List _getAncestorsOrDescendents(Collection nodes, boolean predecessors) {

        // Create an empty list of the relations. This is the list
        // that will be returned at the end of this method.
        List relations = new ArrayList();
        Stack s = new Stack();

        // Push all direct relatives onto stack
        for (Iterator i = nodes.iterator(); i.hasNext();) {
            Object node = i.next();
            Collection relatives;
            if (predecessors)
                relatives = getPredecessors(node);
            else
                relatives = getSuccessors(node);

            for (Iterator j = relatives.iterator(); j.hasNext();) {
                Object directRelative = j.next();
                if (!s.contains(directRelative) && !relations.contains(directRelative))
                    s.push(directRelative);
            }
        }

        while (!s.isEmpty()) {
            Object relation = s.pop();
            if (!s.contains(relation) && !relations.contains(relation))
                relations.add(relation);

            Collection relatives;
            if (predecessors)
                relatives = getPredecessors(relation);
            else
                relatives = getSuccessors(relation);

            for (Iterator i = relatives.iterator(); i.hasNext();) {
                Object relative = i.next();
                if (!s.contains(relative) && !relations.contains(relative))
                    s.push(relative);
            }
        }
        return relations;
    }

    /**
     * Provides the ancestors or descendants of a single Node.
     */
    protected List _getAncestorsOrDescendents(Object node, boolean predecessors) {
        Collection c = new ArrayList(1);
        c.add(node);
        return _getAncestorsOrDescendents(c, predecessors);
    }

}
