/*
 * Collection of nodes and a collection of back edges to break the cycles.
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
package edu.byu.ece.graph.dfs;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import edu.byu.ece.graph.AbstractGraph;
import edu.byu.ece.graph.DirectedGraph;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.NodeEdgeMap;

/**
 * Represents a single depth-first tree within a depth first search.
 * <p>
 * This class contains its own state and does not inherit any state from its
 * super classes.
 * <p>
 * This tree is created from a root node from an DirectedGraph object. Upon
 * construction, this class will evaluate the topology of the graph at a
 * particular "root" node and create a new graph with the same nodes but edges
 * representing the traversal of the depth-first search.
 * <p>
 * All of the edges in the original circuit are classified in this search as
 * follows:
 * <ul>
 * <li> Successor edge: an edge that leads to a direct successor in the depth
 * first traversal.
 * <li> Forward edge: an edge that leads to a successor (but not direct
 * successor) in the depth first traversal.
 * <li> Cross edge: An edge that leads to a node in a different
 * DepthFirstSearchTree (not this tree).
 * <li> Back edge: An edge that leads to a predecessor node in the depth first
 * tree (a feedback edge).
 * </ul>
 * 
 * @author Michael Wirthlin
 * @since Created on Jun 3, 2005
 */
public class BasicDepthFirstSearchTree extends AbstractGraph implements DepthFirstTree, Comparable {

    /**
     * Create a BasicDepthFirstSearchTree with the given DepthFirstSearchForest
     * as the parent of the Tree and the given node as the root. The
     * DirectedGraph is used to fill the Tree, which can be inverted or not, as
     * specified by invertDirection.
     * 
     * @param parent The DepthFirstSearchForest to which this
     * BasicDepthFirstSearchTree belongs
     * @param graph
     * @param node The root node
     * @param invertDirection Specifies whether the Tree should be inverted or
     * not.
     */
    public BasicDepthFirstSearchTree(DepthFirstSearchForest parent, DirectedGraph graph, Object node,
            boolean invertDirection) {
        super();
        _parent = parent;
        _root = node;
        //_tempNodes = new ArrayList(1);
        _invert = invertDirection;
        _visitNonrecursive(node, graph);
    }

    /**
     * Create a BasicDepthFirstSearchTree with the given DepthFirstSearchForest
     * as the parent of the Tree and the given node as the root. The Tree is
     * created without being inverted.
     * 
     * @param parent The DepthFirstSearchForest to which this
     * BasicDepthFirstSearchTree belongs
     * @param graph A DirectedGraph
     * @param node The root node
     */
    public BasicDepthFirstSearchTree(DepthFirstSearchForest parent, DirectedGraph graph, Object node) {
        this(parent, graph, node, false);
    }

    public static boolean DEBUG = false;

    public void addNode(Object node) {
        if (_nodes == null)
            _nodes = new ArrayList();
        _nodes.add(node);
    }

    public int compareTo(Object o) {
        if (this.getNodes().size() >= ((DepthFirstTree) o).getNodes().size())
            return 1;
        return -1;
    }

    public boolean containsNode(Object o) {
        if (_nodes == null)
            return false;
        return _nodes.contains(o);
    }

    public Collection getAllEdges() {
        Collection edges = new ArrayList();
        edges.addAll(getEdges());
        if (_crossEdges != null)
            edges.addAll(_crossEdges.getEdges());
        return edges;
    }

    /**
     * Returns all of the back edges of this Map. This is, all the edges that
     * lead to a predecessor node in the depth first tree (a feedback edge). The
     * returned Collection is guaranteed not to be null.
     * <p>
     * Implements {@link DepthFirstTree#getBackEdges()}.
     * 
     * @return A Collection of the Edges in this map. If the Map is empty, this
     * method will return an empty but non-null Collection.
     */
    public Collection<Edge> getBackEdges() {
        Collection<Edge> result;
        if (_backEdges != null)
            result = _backEdges.getEdges();
        else
            result = new ArrayList(0);
        return result;
    }

    public Collection getCrossEdges() {
        return _crossEdges.getEdges();
    }

    /**
     * @return A Map between each node in the depth-first tree and an Integer
     * the depth of the node. The depth is calculated as the <i>shortest</i>
     * path from a root node. The depth of the root is zero.
     */
    public Map<Object, Integer> getDepthMap() {
        Map<Object, Integer> map = new LinkedHashMap<Object, Integer>(getNodes().size());

        for (Object node : getTopologicalSort()) {
            Integer iDepth = map.get(node);
            if (iDepth == null) {
                iDepth = new Integer(0);
                map.put(node, iDepth);
            }
            int depth = iDepth.intValue();
            if (DEBUG)
                System.out.println("\tNode " + node + " has depth=" + depth + ".");
            for (Object successor : getSuccessors(node)) {
                Integer jDepth = map.get(successor);
                if (jDepth == null || jDepth.intValue() < depth + 1) {
                    jDepth = new Integer(depth + 1);
                    map.put(successor, jDepth);
                }
            }
        }
        return map;
    }

    public Edge getEdge(Object source, Object sink) {
        throw new RuntimeException("getEdges currently not supported");
    }

    public Collection<Edge> getEdges() {
        Collection<Edge> edges = new ArrayList<Edge>();
        if (_forwardEdges != null)
            edges.addAll(_forwardEdges.getEdges());
        if (_backEdges != null)
            edges.addAll(_backEdges.getEdges());
        if (_successorEdges != null)
            edges.addAll(_successorEdges.getEdges());
        return edges;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.byu.ece.graph.dfs.DepthFirstTree#getFinishList()
     */
    public List getFinishList() {
        return new ArrayList(_finished);
    }

    /**
     * @return A Collection of all the forward edges in the Tree. A forward edge
     * is one that leads to a successor (but not direct successor) in the depth
     * first traversal. Implements {@link DepthFirstTree#getForwardEdges()}.
     */
    public Collection getForwardEdges() {
        return _forwardEdges.getEdges();
    }

    public Collection getInputEdges(Object node) {
        throw new RuntimeException("getEdges currently not supported");
    }

    /**
     * Identifies the back edge that has the longest distance. The distance of a
     * back edge is defined as the depth (see {@link #getDepthMap()}) of the
     * source of the edge minus the depth of the sink of the edge.
     * <p>
     * This method will return null if there are no back edges in the graph.
     * 
     * @return the back edge that has the longest distance, or null if there are
     * no back edges in the graph.
     */
    public Edge getLongestBackEdge() {

        // Find Edge with longest distance
        Map<Object, Integer> depthMap = getDepthMap();
        Edge furthestLink = null;
        int furthest_distance = -1;

        /*
         * Iterate over all back edges. Determine the depth of the source and
         * the depth of the sink. Find the distance between them; keep only the
         * longest distance seen thus far.
         */
        for (Edge edge : getBackEdges()) {
            Object sourceNode = edge.getSource();
            Object sinkNode = edge.getSink();
            int sourceNodeDepth = depthMap.get(sourceNode);
            int sinkNodeDepth = depthMap.get(sinkNode);
            int edgeDepth = sourceNodeDepth - sinkNodeDepth;
            if (edgeDepth > furthest_distance) {
                furthest_distance = edgeDepth;
                furthestLink = edge;
            }
        }

        // Note that the farthestSource will actually correspond
        // to a cell with an "Input" (the last DFS was done
        // in reverse order). The EPR is actually an output.
        //System.out.println("dist="+furthest_distance+
        //		" Link="+furthestLink);    
        return furthestLink;
    }

    public Collection getNodes() {
        return new ArrayList(_nodes);
    }

    /**
     * @return the Edge objects that go out of the given node. To preserve the
     * acyclic nature of this graph and to insure that the source and sink of
     * all edges are within the graph, this method will return only the forward
     * and successor edges of the depth first search.
     */
    public Collection getOutputEdges(Object node) {
        Collection edges = new ArrayList();
        if (_forwardEdges != null)
            edges.addAll(_forwardEdges.getEdges(node));
        if (_successorEdges != null)
            edges.addAll(_successorEdges.getEdges(node));
        return edges;
    }

    public DepthFirstSearchForest getParent() {
        return _parent;
    }

    public Object getRoot() {
        return _root;
    }

    public DirectedGraph getSubGraph(Collection nodes) {
        throw new RuntimeException("getEdges currently not supported");
    }

    public Collection getSuccessorEdges() {
        return _successorEdges.getEdges();
    }

    /**
     * Provide a new List of nodes that appear in a reverse order of the finish
     * list.
     * 
     * @return The list of nodes.
     */
    public List getTopologicalSort() {
        return reverseList(_finished);
    }

    public DirectedGraph invert() {
        throw new RuntimeException("invert currently not supported");
    }

    /**
     * Create a new List that is a reverse of the List provided as a parameter.
     * 
     * @return The reversed list.
     */
    public static List reverseList(List list) {
        int listSize = list.size();
        Vector reverseList = new Vector(listSize);
        for (int i = 0; i < listSize; i++) {
            Object o = list.get(listSize - i - 1);
            reverseList.insertElementAt(o, i);
        }
        return reverseList;
    }

    /**
     * Print all the edges (source node -> sink node) in the given map to the
     * given PrintStream object.
     * 
     * @param edges Map from source to destination nodes.
     * @param out PrintStream used to print the nodes.
     * @see #printEdges(Map, PrintStream, Map)
     */
    public void printEdges(Map edges, PrintStream out) {
        if (edges == null) {
            out.println("\t\tNOT RECORDED");
            return;
        }
        for (Iterator j = edges.keySet().iterator(); j.hasNext();) {
            Object source = j.next();
            Collection links = (Collection) edges.get(source);
            for (Iterator k = links.iterator(); k.hasNext();) {
                Edge link = (Edge) k.next();
                Object sinkNode;
                /*
                 * if (_invert) { sinkNode = link.getSource(); } else { sinkNode =
                 * link.getSink(); }
                 */
                sinkNode = link.getSink();
                out.println("\t\t" + source.toString() + "->" + sinkNode.toString());
            }
        }
    }

    /**
     * Print all the edges in the map, along with the depth of the edge.
     * 
     * @param edges Map from source to destination nodes.
     * @param out Map from source to destination nodes.
     * @param depthMap
     * @see #printEdges(Map, PrintStream)
     */
    public void printEdges(Map edges, PrintStream out, Map depthMap) {
        if (edges == null) {
            out.println("\t\tNOT RECORDED");
            return;
        }
        for (Iterator j = edges.keySet().iterator(); j.hasNext();) {
            Object source = j.next();
            Collection links = (Collection) edges.get(source);
            int sourceDepth = ((Integer) depthMap.get(source)).intValue();
            for (Iterator k = links.iterator(); k.hasNext();) {
                Edge link = (Edge) k.next();
                Object sinkNode = null;
                /*
                 * if (_invert) { node = link.getSource(); } else { node =
                 * link.getSink(); }
                 */
                sinkNode = link.getSink();
                int sinkDepth = ((Integer) depthMap.get(sinkNode)).intValue();
                out.println("\t\t" + source.toString() + "->" + sinkNode.toString() + " dist="
                        + (sourceDepth - sinkDepth));
            }
        }
    }

    public void print(PrintStream out) {

        Map m = getDepthMap();

        out.println("Tree of size " + this.getNodes().size() + " (visit order)");
        for (Iterator j = this.getNodes().iterator(); j.hasNext();) {
            Object eci = j.next();
            int depth = ((Integer) m.get(eci)).intValue();
            out.println("\t" + eci + " depth=" + depth);
        }
        out.println("\tSuccessor Edges");
        //printEdges(_successorEdges,out);
        out.println("\tBack Edges");
        //printEdges(_backEdges,out,m);
        out.println("\tCross Edges");
        //printEdges(_crossEdges,out);
        out.println("\tForward Edges");
        //printEdges(_forwardEdges,out);
        out.println("\tFinish List");
        for (Iterator j = _finished.iterator(); j.hasNext();) {
            Object eci = j.next();
            System.out.println("\t\t" + eci.toString());
        }
        out.flush();
    }

    /**
     * Trims the capacity of the finished list to its current size.
     * 
     * @see {@link ArrayList#trimToSize()}.
     */
    public void trimToSize() {
        _finished.trimToSize();
        //if (_forwardEdges != null) _forwardEdges.trimToSize();
        //if (_crossEdges != null) _crossEdges.trimToSize();
        //if (_backEdges != null) _backEdges.trimToSize();
        //if (_successorEdges != null) _successorEdges.trimToSize();
    }

    /**
     * The back edges of this graph.
     */
    protected NodeEdgeMap _backEdges = null;

    /**
     * The cross edges of this graph.
     */
    protected NodeEdgeMap _crossEdges = null;

    /** The forward edges of this graph. */
    protected NodeEdgeMap _forwardEdges = null;

    /**
     * The parent "forest" of DFSTree objects.
     */
    protected DepthFirstSearchForest _parent;

    /**
     * The root of the DFSTree.
     */
    protected Object _root;

    /** The successor edges of this graph. */
    protected NodeEdgeMap _successorEdges = null;

    /**
     * Perform a non-recursive depth-first visit of nodes in a DirectedGraph.
     * This method uses an internal stack to manage the traversal through the
     * circuit instead of recursion. Although the algorithm implementation is
     * less readable than a traditional recursive algorithm, it leads to
     * significantly lower memory requirements (the recursive version would kill
     * the JVM stack for large designs).
     * 
     * @param node The root node
     * @param ic The DirectedGraph to get the output edges
     */
    protected void _visitNonrecursive(Object node, DirectedGraph ic) {

        /**
         * An internal stack is used to reduce the overhead of recursion. Using
         * a stack is tricky as we need to mimic the behavior of the recursion.
         * Two different items are placed on the stack:
         * <ol>
         * <li> A Node in the the depth first search. A Node appears on the top
         * of the stack when it is first visited OR after all of its children
         * have been visited.
         * <li> A Edge object representing a connection from a source node to a
         * target node. When this is on the top of the stack, the edge will be
         * removed from the stack and "processed". Processing an edge involves
         * the following steps:
         * <ol>
         * <li> Create the appropriate DFS "edge" corresponding to this link
         * (i.e. forward, cross, back, etc.).
         * <li> Push target Node on the stack if it has not been visited yet.
         * </ol>
         * </ol>
         */
        Stack<Object> s = new Stack<Object>();
        // Initialize the stack with the root Node
        s.push(node);
        if (DEBUG)
            System.out.println("New TREE with root=" + node);

        // Continue processing until the stack is empty
        while (!s.isEmpty()) {

            Object obj = s.peek();

            if (obj instanceof Edge) {
                // Object on stack is an edge

                Edge edge = (Edge) obj;
                s.pop();

                // Determine source and sink nodes
                Object targetNode = null;
                targetNode = edge.getSink();

                if (_parent != null && _parent.containsNode(targetNode)) {
                    // Edge goes to a node in another DFS tree.
                    // Mark edge as a cross edge
                    if (_crossEdges == null)
                        _crossEdges = new NodeEdgeMap(true);
                    _crossEdges.addEdge(edge);
                    if (DEBUG)
                        System.out.println("\tCrossEdge to " + edge);
                } else if (this.containsNode(targetNode)) {
                    // The target already been visited

                    if (_finished.contains(targetNode)) {
                        /*
                         * If the successor is finished, then this is a
                         * "forward" edge.
                         */
                        if (_forwardEdges == null)
                            _forwardEdges = new NodeEdgeMap(true);
                        _forwardEdges.addEdge(edge);
                        if (DEBUG)
                            System.out.println("\tForwardEdge to " + edge);
                    } else {
                        /*
                         * If the successor is has not finished, the successor
                         * is also an ancestor. This makes the edge a "back"
                         * edge.
                         */
                        if (_backEdges == null)
                            _backEdges = new NodeEdgeMap(true);
                        _backEdges.addEdge(edge);
                        if (DEBUG)
                            System.out.println("\tBackEdge to " + edge);
                    }
                } else {
                    // The target has not been visited yet.
                    s.push(targetNode);
                    if (_successorEdges == null)
                        _successorEdges = new NodeEdgeMap(true);
                    _successorEdges.addEdge(edge);
                    if (DEBUG)
                        System.out.println("\tSuccessorEdge:" + edge);
                    if (DEBUG)
                        System.out.println("\t\tPushing node:" + targetNode);
                }
            } else {
                // Object on stack is a node

                if (this.containsNode(obj)) {
                    // already visited - mark finished and pop

                    if (_finished.contains(obj)) {
                        // already finished. 
                        s.pop();
                        System.out.println("Warning: A finished node is on the stack " + obj);
                        if (DEBUG)
                            System.out.println("Popping and Ignoring cell " + obj);
                    } else {
                        _finished.add(obj);
                        Object p = s.pop();
                        if (DEBUG)
                            System.out.println("Finished " + obj + " pop=" + p);
                    }
                } else {
                    /*
                     * Cell has not yet been visited. Mark as visited add add
                     * all outgoing nets to stack.
                     */
                    if (DEBUG)
                        System.out.println("visiting " + obj);

                    //this.addNode(obj);
                    if (_nodes == null)
                        _nodes = new ArrayList();
                    _nodes.add(obj);

                    Collection targetLinks = ic.getOutputEdges(obj);

                    if (DEBUG)
                        if (targetLinks.size() > 0)
                            System.out.print("\tPUSH links: ");
                        else
                            System.out.print("\tNO LINKS to Push.");

                    for (Iterator i = targetLinks.iterator(); i.hasNext();) {
                        Edge link = (Edge) i.next();
                        if (DEBUG)
                            System.out.print(link + " ");

                        s.push(link);
                    }
                    if (DEBUG)
                        System.out.println();
                }
            }
        }
        trimToSize();
    }

    /**
     * An ordered List of the finish times of the depth first search.
     */
    ArrayList _finished = new ArrayList(1);

    boolean _invert;

    ArrayList _nodes = null;

}
