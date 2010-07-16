/*
 * Data structure for representing a simple directed graph.
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.util.graph.AbstractEdifGraph;

/**
 * Data structure for representing a simple directed graph.
 * <p>
 * This class contains the state associated with edges and nodes. The edges are
 * represented within {@link NodeEdgeMap} objects and all connectivity
 * information is obtained by querying these Map objects.
 * <p>
 * The actual objects used for edges and nodes can be specified by an
 * application-specific graph data structure.
 */
public class BasicGraph extends AbstractGraph implements Cloneable {

    /**
     * Create an empty graph with no nodes or edges.
     */
    public BasicGraph() {
        _nodeSourceMap = new NodeEdgeMap(true);
        _nodeSinkMap = new NodeEdgeMap(false);
        _nodes = new LinkedHashSet();
    }

    /**
     * Create an empty graph with no nodes or edges. Provide internal capacity
     * for the number of nodes specified in the node count parameter.
     */
    public BasicGraph(int nodeCount) {
        _nodeSourceMap = new NodeEdgeMap(nodeCount, true);
        _nodeSinkMap = new NodeEdgeMap(nodeCount, false);
        _nodes = new LinkedHashSet(nodeCount);
    }

    /**
     * Create an empty graph initialized with the nodes passed in as a
     * Collection but with no edges.
     */
    public BasicGraph(Collection nodes) {
        this(nodes.size());
        _nodes.addAll(nodes);
    }

    /**
     * Create new graph that is a copy of the graph passed in as a parameter.
     */
    public BasicGraph(BasicGraph graph) {
        _nodeSinkMap = (NodeEdgeMap) graph._nodeSinkMap.clone();
        _nodeSourceMap = (NodeEdgeMap) graph._nodeSourceMap.clone();
        _nodes = (HashSet) graph._nodes.clone();
    }

    /**
     * Add a node object.
     */
    public void addNode(Object node) {
        if (_nodes.contains(node)) {
        	throw new RuntimeException("Node " + node + " already member of graph");
        }
        _nodes.add(node);
    }

    /**
     * Add a collection of node objects.
     */
    public void addNodes(Collection nodes) {
        for (Object node : nodes) {
            addNode(node);
        }
    }

    /**
     * Adds the given Edge from the graph
     * 
     * @param edge The Edge object to add
     * @return true if the edge was successfully added (If any of the internal
     * maps changed)
     */
    public boolean addEdge(Edge edge) {
        if (!containsNode(edge.getSink()))
            throw new RuntimeException("Missing sink node in graph:" + edge.getSink());
        if (!containsNode(edge.getSource()))
            throw new RuntimeException("Missing source node in graph:" + edge.getSource());
        boolean retVal = false;

        retVal |= _nodeSourceMap.addEdge(edge);
        retVal |= _nodeSinkMap.addEdge(edge);

        return retVal;
    }

    /**
     * Clone the BasicGraph object.
     */
    public Object clone() {
        return new BasicGraph(this);
    }

    public boolean containsNode(Object o) {
        if (_nodes.contains(o))
            return true;
        return false;
    }

    /**
     * Returns the first edge found that corresponds to the given source and
     * sink objects. Returns null if no edge is found.
     * <p>
     * TODO: What if there are multiple edges for the same source and sink?
     */
    public Edge getEdge(Object source, Object sink) {
        // Search through all maps for the given source and sink
        Collection edges = null;
        edges = (Collection) _nodeSourceMap.get(source);

        if (edges != null) {
            // Found edges with this source object, look for matching sink
            for (Iterator i = edges.iterator(); i.hasNext();) {
                Edge edge = (Edge) i.next();
                if (edge.getSink() == sink)
                    return edge;
            }
        }
        return null;
    }

    /**
     * Returns a collection of edges found that correspond to the given source
     * and sink objects. Returns empty Collection if no edges are found.
     */
    public Collection<? extends Edge> getEdges(Object source, Object sink) {
        // Search through all maps for the given source and sink
        Collection<Edge> returnEdges = new LinkedHashSet<Edge>();
        Collection<Edge> edges = null;
        edges = (Collection<Edge>) _nodeSourceMap.get(source);

        if (edges != null) {
            // Found edges with this source object, look for matching sink
            for (Edge edge : edges) {
                if (edge.getSink() == sink)
                    returnEdges.add(edge);
            }
        }
        return returnEdges;
    }

    public Collection<? extends Edge> getEdges() {
        return _nodeSourceMap.getEdges();
        //return _nodeSinkMap.getEdges();
    }

    /**
     * Return all links in which the specified node Object is the "sink".
     * 
     * @see DirectedGraph#getInputEdges(Object)
     */
    public Collection getInputEdges(Object node) {
        return _nodeSinkMap.getEdges(node);
    }

    /**
     * Return all the node objects in the graph.
     */
    public Collection getNodes() {
        return new ArrayList(_nodes);
    }

    /**
     * @return A Collection of Nodes that have no incoming Edges (input Nodes)
     */
    public Collection getNodesWithNoInputEdges() {
        Collection inputNodes = new ArrayList();
        for (Object node : _nodes) {
            if (getInputEdges(node).size() == 0)
                inputNodes.add(node);
        }
        return inputNodes;
    }

    /**
     * @return A Collection of Nodes that have no outgoing Edges (output Nodes)
     */
    public Collection getNodesWithNoOutputEdges() {
        Collection outputNodes = new ArrayList();
        for (Object node : _nodes) {
            if (getOutputEdges(node).size() == 0)
                outputNodes.add(node);
        }
        return outputNodes;
    }

    /**
     * Return all links in which the specified node Object is the "source".
     */
    public Collection getOutputEdges(Object node) {
        //return _returnValidCollection(_nodeSourceMap,node);
        return _nodeSourceMap.getEdges(node);
    }

    /**
     * Returns a Collection of nodes which have no predecessors.
     * 
     * @return
     */
    public Collection getSinkNodes() {
        Collection<Object> sinks = new LinkedHashSet<Object>();

        Collection nodes = this.getNodes();
        for (Object node : nodes) {
            Collection outputs = this.getOutputEdges(node);
            if (outputs == null || outputs.isEmpty())
                sinks.add(node);
        }
        return sinks;
    }

    /**
     * Returns a Collection of nodes which have no predecessors.
     * 
     * @return
     */
    public Collection getSourceNodes() {
        Collection<Object> sources = new LinkedHashSet<Object>();

        Collection nodes = this.getNodes();
        for (Object node : nodes) {
            Collection inputs = this.getInputEdges(node);
            if (inputs == null || inputs.isEmpty())
                sources.add(node);
        }
        return sources;
    }

    /**
     * Creates a sub-graph of the given graph from a sub-set of nodes in the
     * graph. The resulting graph contains a sub-set of nodes and only those
     * edges that connect nodes in the sub-graph.
     * 
     * This implementation copies the entire graph using the clone method
     * and then removes nodes that are not in the collection. If a small
     * sub graph is created, it is more efficient to use the getSmallSubGraph
     * method.
	 *
     */
    public BasicGraph getSubGraph(Collection nodeCollection) {

        // Create copy of this Connectivity object to prune 
        BasicGraph absGraph = (BasicGraph) this.clone();

        // Remove unneeded nodes
        absGraph._nodes.retainAll(nodeCollection);

        // Make sure remaining edges (those that connect to other nodes) are
        // removed as well
        absGraph._nodeSourceMap.retainEdgesForNodes(nodeCollection);
        absGraph._nodeSinkMap.retainEdgesForNodes(nodeCollection);

        return absGraph;
    }


    /**
     * Creates a sub-graph of the given graph from a sub-set of nodes in the
     * graph. The resulting graph contains a sub-set of nodes and only those
     * edges that connect nodes in the sub-graph.
     * 
     * This implementation copies individual nodes and the copies the 
     * relevant topology. This method is efficient for small sub graphs.
     */
    public BasicGraph getSmallSubGraph(Collection nodes) {
        BasicGraph absGraph = new BasicGraph();
        absGraph.addNodes(nodes);
        for (Object node : nodes) {
            for (Edge o : (Collection<Edge>) _nodeSourceMap.getEdges(node)) {
                try {
                    absGraph.addEdge(o);
                } catch (RuntimeException ex) {
                    //System.out.println(ex);
                    //missing a node, can't add that particular edge
                }
            }
            for (Edge o : (Collection<Edge>) _nodeSinkMap.getEdges(node)) {
                try {
                    absGraph.addEdge(o);
                } catch (RuntimeException ex) {
                    //System.out.println(ex);
                    //this (sinkmap) may be unessisary, but I would rather be safe.
                }
            }
        }
        //absGraph
        return absGraph;
    }

    public DirectedGraph invert() {

        // Create an empty graph with capacity for the same number of nodes
        // in this graph.
        BasicGraph invertedGraph = (BasicGraph) this.clone();

        // Clear NodeEdgeMaps and re-fill with inverted Edges
        Collection edges = getEdges();
        invertedGraph._nodeSourceMap = new NodeEdgeMap(getEdges().size(), true);
        invertedGraph._nodeSinkMap = new NodeEdgeMap(getEdges().size(), false);

        // TODO: This is very slow for large graphs. Is there another way?
        for (Iterator i = edges.iterator(); i.hasNext();) {
            Edge e = (Edge) i.next();
            Edge newEdge = e.invert();
            invertedGraph._nodeSourceMap.addEdge(newEdge);
            invertedGraph._nodeSinkMap.addEdge(newEdge);
        }
        //invertedGraph._nodeSinkMap = _nodeSourceMap.invert();
        //invertedGraph._nodeSourceMap = _nodeSinkMap.invert();
        return invertedGraph;
    }

    public void removeEdges(Collection edges) {
        for (Iterator i = edges.iterator(); i.hasNext();)
            removeEdge((Edge) i.next());
    }

    /**
     * Removes the given Edge from the graph
     * 
     * @param edge The Edge object to remove
     * @return true if the edge was successfully removed (If any of the internal
     * maps changed)
     */
    public boolean removeEdge(Edge edge) {
        boolean retVal = false;

        // Search through all edgeMaps and remove all references to this edge
        // The true parameter means to look up the source of the Edge in 
        //   the given map; false means to look up the sink
        //retVal |= _removeEdgeFromMap(edge, _nodeSourceMap, true);
        //retVal |= _removeEdgeFromMap(edge, _nodeSinkMap, false);
        retVal |= _nodeSourceMap.removeEdge(edge);
        retVal |= _nodeSinkMap.removeEdge(edge);

        return retVal;
    }

    /**
     * Removes the given node Object from the graph Also removes ALL Edges in
     * the graph that refer to this node
     * 
     * @param node The node Object to remove
     */
    public void removeNode(Object node) {
        removeNode(node, true);
    }

    /**
     * Removes a Collection of nodes from the graph and all Edges
     * incident to these nodes.
     * 
     */
    public void removeNodes(Collection nodes) {
    	for (Object node : nodes) {
    		removeNode(node, true);
    	}
    }
        
    /**
     * Removes the given node Object from the graph
     * 
     * @param node The node Object to remove
     * @param removeAllEdges If true, removes ALL Edges that refer to this node,
     * not just the ones mapped from this node (the easy-to-get-to Edges). In
     * other words, true removes ALL references to this node, while false may
     * leave some Edges that refer to this node.
     */
    public void removeNode(Object node, boolean removeAllEdges) {
        // Remove the node key from each of the edgeMaps
        // Save the Collections of edges removed
        Collection outputEdges = (Collection) _nodeSourceMap.remove(node);
        Collection inputEdges = (Collection) _nodeSinkMap.remove(node);
        // Remove other Edges that connect to this node using the outputEdges
        //   and inputEdges Collections
        if (removeAllEdges) {
            // Output Edges of "node" may be referenced by another node as
            //   a sink of that Edge.'
            if (outputEdges != null)
                for (Iterator i = outputEdges.iterator(); i.hasNext();) {
                    Edge edge = (Edge) i.next();
                    //_nodeSinkMap.removeEdge(edge);
                    removeEdge(edge);
                }
            // Input Edges of "node" may be referenced by another node as
            //   a source of that Edge.
            if (inputEdges != null)
                for (Iterator i = inputEdges.iterator(); i.hasNext();) {
                    Edge edge = (Edge) i.next();
                    //_nodeSinkMap.removeEdge(edge);
                    removeEdge(edge);
                }
        }
        _nodes.remove(node);
    }

    /**
     * Performs a topological sort on the given acyclic AbstractEdifGraph. The
     * sorted node Objects are returned in an ordered List.
     * 
     * @param graph The acyclic AbstractEdifGraph to sort
     * @return A sorted List of the node Objects of the graph
     * @throws EdifRuntimeException In the case of a cyclic graph
     */
    /*
    public static List topologicalSort(AbstractEdifGraph graph) {
        //		First, find a list of "start nodes" which have no incoming edges and 
        //		  insert them into a queue Q
        //		  
        //		while graph is nonempty
        //		    if Q is empty output error message
        //		    remove a node n from Q
        //		    output n
        //		    for each node m with an edge e from n to m
        //		        remove edge e from the graph
        //		        if m has no other incoming edges
        //		            insert m into Q
        //		    remove node m from graph
        Collection nodes = graph.getNodes();
        List sortedList = new ArrayList(nodes.size());
        // Keep track of which Edges have been "removed" instead of actually
        //   modifying the graph (removing individual Edges from the graph
        //   would be very expensive)
        //Set removedEdges = new LinkedHashSet();
        Collection removedEdges = new ArrayList();

        // Initialize the Queue with all of the nodes with no incoming edges
        LinkedList Q = new LinkedList();
        Iterator nodesIt = nodes.iterator();
        while (nodesIt.hasNext()) {
            Object node = nodesIt.next();
            if (graph.getInputEdges(node).size() == 0)
                Q.addLast(node);
        }

        // Main while() loop
        while (nodes.size() != 0) {
            if (Q.isEmpty()) // Cyclic graph (no nodes left with zero input edges)
                throw new EdifRuntimeException("Cannot topologically sort cyclic graphs.");
            // Grab a Node with no input Edges
            Object node = Q.removeFirst();
            //System.out.println("INPUT NODE: "+node);
            sortedList.add(node);
            Collection outEdges = graph.getOutputEdges(node);
            // Don't include "removed" Edges
            outEdges.removeAll(removedEdges);

            // Follow the output Edges of this Node
            Iterator outEdgesIt = outEdges.iterator();
            while (outEdgesIt.hasNext()) {
                Edge outEdge = (Edge) outEdgesIt.next();
                Object sinkNode = outEdge.getSink();
                // "Remove" this Edge from the graph
                removedEdges.add(outEdge);

                // If this node no longer contains any input edges, 
                //   add it to the Queue
                Collection inputEdges = (Collection) graph.getInputEdges(sinkNode);
                // Don't include "removed" Edges
                inputEdges.removeAll(removedEdges);
                if (inputEdges.size() == 0) {
                    Q.addLast(sinkNode);
                }
            }
            // "Remove" the Node from the graph
            nodes.remove(node);
        }

        return sortedList;
    }
    */

    /**
     * A Map between graph "node" objects (which are implementation specific)
     * and Collection objects (value). The Collection objects contain Edge
     * objects. The Collection of Edge objects associated with a given node
     * object are those in which the given node is the "source" of the Edge.
     */
    protected NodeEdgeMap _nodeSourceMap;

    /**
     * A Map between graph "node" objects (which are implementation specific)
     * and Collection objects (value). The Collection objects contain Edge
     * objects. The Collection of Edge objects associated with a given node
     * object are those in which the given node is the "sink" of the Edge.
     */
    protected NodeEdgeMap _nodeSinkMap;

    /**
     * The Set of Node objects contained within the graph.
     */
    protected HashSet _nodes;

}
