/*
 * A depth first search algorithm using the Edif classes only.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.graph.dfs.DFSTree;

/**
 * A depth first search algorithm using the Edif classes only. This requires
 * much less memory and is much faster than the Ptolemy graph version.
 * <p>
 * TODO:
 * <ul>
 * <li>Create new package for DFS stuff (edu.byu.ece.edif.util.graph.dfs)
 * <li>Move much of the DFS code to DFSTree
 * <ul>
 * <li>Clean up relationship between classes
 * <li>DFSForest (parent of DFSTree objects)
 * <li>DFSTree
 * <ul>
 * <li>Class for SCC decomposition
 * </ul>
 * </ul>
 * </ul>
 * 
 * @author Mike Wirthlin
 * @version $Id:EdifCellDFS.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class EdifCellDFS {

    public EdifCellDFS(EdifCell c) {
        this(c, new EdifCellInstanceGraph(c), null, false);
    }

    public EdifCellDFS(EdifCell c, EdifCellInstanceGraph ecic) {
        this(c, ecic, null, false);
    }

    /**
     * Creates a depth-first forest of trees from the directed graph.
     * 
     * @param g The directed possibly cyclic graph to search
     * @param order The order to process the Nodes
     * @param invertDirection Perform the depth first search in an inverted
     * order (i.e. invert the direction of all directed edges)
     */
    public EdifCellDFS(Collection c, EdifCellInstanceGraph ecic, List order, boolean invertDirection) {
        _instances = c;
        _ic = ecic;
        _search(order, invertDirection);
    }

    public EdifCellDFS(EdifCell c, EdifCellInstanceGraph ecic, List order, boolean invertDirection) {
        this(c.getSubCellList(), ecic, order, invertDirection);
    }

    public EdifCellDFS(EdifCell c, List order, boolean invertDirection) {
        this(c.getSubCellList(), new EdifCellInstanceGraph(c), order, invertDirection);
    }

    public List getFinishList() {
        return _finished;
    }

    public List getBackEdges() {
        return _backEdges;
    }

    public List getReverseFinishList() {
        int listSize = _finished.size();
        Vector reverseList = new Vector(listSize);
        for (int i = 0; i < listSize; i++) {
            Object o = _finished.get(listSize - i - 1);
            reverseList.insertElementAt(o, i);
        }
        return reverseList;
    }

    /**
     * Provides a Collection of Nodes in this graph where each Node is a root of
     * a spanning tree.
     */
    public Collection getTreeRoots() {
        return _treeRoots;
    }

    public static List invertList(List list) {
        //System.out.println("Before invert");
        //for (int i = 0; i < list.size(); i++)
        //	System.out.println(i + " " + list.get(i));
        int lsize = list.size();
        ArrayList newlist = new ArrayList(lsize);
        for (int i = 0; i < lsize; i++)
            newlist.add(i, list.get(lsize - i - 1));
        //System.out.println("After invert");
        //for (int i = 0; i < newlist.size(); i++)
        //	System.out.println(i + " " + newlist.get(i));
        return newlist;
    }

    /**
     * Graph like data structure
     * <ol>
     * <li> Create more useful "trees" and return
     * <ul>
     * <li> keep "net" connectivity information to recreate cycle
     * <li> feedback edges for "breaking" cycle
     * </ul>
     * <li> Find cut-sets in wiring
     * <li> Identify simple adder feedback cycles
     * <ul>
     * <li> extract counters and incrementers (combine 2-cell sccs)
     * <li> report as counters
     * <li> Ability to create "smart" adders/incrementers
     * <li> Find all 2-cell SCCs (Many of the larger SCCs have MUXCYs and are
     * probably part of adders)
     * <li> Identify appropriate carry/next stage signal
     * <li> see if signal goes to another SCC (if so, combine)
     * </ul>
     * <li> Characterize each SCC (or super SCC)
     * <ul>
     * <li> # logic predecessors (cone of logic)
     * <li> # logic successors
     * <li> # SCC successors
     * <li> # feed-forward successors
     * </ul>
     * </ol>
     * Graph structure:
     * <ul>
     * <li> backward reachable instances (from a set of instances)
     * <li> forward reachable instances (from a set of instances)
     * <li> predecessor/successor "edges" (EPRS?)
     * <li> predecessor/successor instances (from a single instance)
     * <li> sink and source node(s)
     * <li> topological sort
     * <li> depth first search
     * <li> Represent a "collection" of instances as a node
     * <li> SCC decomposition
     * </ul>
     */
    public static Collection sccDecomposition(EdifCell c) {
        return sccDecomposition(c, c.getSubCellList());
    }

    public static Collection sccDecomposition(EdifCell cell, Collection c) {
        EdifCellInstanceGraph ecic = new EdifCellInstanceGraph(cell);
        return sccDecomposition(c, ecic);
    }

    public static Collection sccDecomposition(Collection c, EdifCellInstanceGraph ecic) {
        //EdifCellInstanceGraph ecic = new EdifCellInstanceGraph(cell);

        //EdifCellDFS dfs1 = new EdifCellDFS(cell,ecic,initialVisitOrder,false);
        EdifCellDFS dfs1 = new EdifCellDFS(c, ecic, null, false);
        List order = invertList(dfs1.getFinishList());

        //dfs1 = new EdifCellDFS(cell,ecic,order,true);
        dfs1 = new EdifCellDFS(c, ecic, order, true);

        Collection sccs = new ArrayList();
        for (Iterator i = dfs1.getTreeRoots().iterator(); i.hasNext();) {
            DFSTree tree = (DFSTree) i.next();
            if (tree.size() > 1) {
                //DFSTree validTree  = createValidCutset(tree);
                //sccs.add(validTree);
                sccs.add(tree);
            }
        }
        return sccs;
    }

    public void removeSingleNodeTrees() {
        Collection toRemove = new ArrayList();
        for (Iterator i = getTreeRoots().iterator(); i.hasNext();) {
            DFSTree tree = (DFSTree) i.next();
            if (tree.size() == 1) {
                toRemove.add(tree);
            }
        }
        for (Iterator i = toRemove.iterator(); i.hasNext();) {
            _treeRoots.remove(i.next());
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("trees=" + getTreeRoots().size() + " back edges=" + getBackEdges().size());
        return sb.toString();
    }

    /**
     * This method performs the top-level depth first search on directed graph.
     * The order in which the graph Nodes are visited is determined by the List
     * parameter. This method will sequentially visit each Node in the list and
     * call the recursive _visit method. The _visit method will visit all
     * "successor" Nodes of the chosen Node (excluding feedback). When visit is
     * completed, this method will choose the next unvisited item in the list.
     * Each call to _visit from this method will create a new spanning tree in
     * the depth first search.
     * 
     * @param order Visit order of Nodes within the Graph
     * @param invertDirection Direction of edges to visit. When true, depth
     * first traversal will follow the opposite direction of the Edges. When
     * false, the traversal will follow the forward direction of the Edges.
     */
    protected void _search(List order, boolean invertDirection) {

        int nodeCount = _instances.size();
        _visited = new ArrayList(nodeCount);
        _finished = new ArrayList(nodeCount);
        _backEdges = new ArrayList();
        _forwardCrossEdges = new ArrayList();
        _treeRoots = new ArrayList();
        Iterator i = null;
        if (order == null)
            i = _instances.iterator();
        else
            i = order.iterator();

        int previous = 0;
        // Iterate over all cells in the circuit.
        for (; i.hasNext();) {
            EdifCellInstance eci = (EdifCellInstance) i.next();

            // If the cell has been visited, skip it. Otherwise,
            // the cell is a root of a new depth first search tree.
            if (_visited.contains(eci))
                continue;

            if (DEBUG)
                System.out.print("New Tree Node " + eci);
            DFSTree tree = _visitNonrecursive(eci, invertDirection);
            //_visit(eci,invertDirection);
            if (DEBUG)
                System.out.println(" nodes=" + (_finished.size() - previous));
            previous = _finished.size();
            //_treeRoots.add(eci);
            _treeRoots.add(tree);
        }
    }

    protected boolean isVisited(EdifCellInstance eci) {
        for (Iterator i = _trees.iterator(); i.hasNext();) {
            DFSTree tree = (DFSTree) i.next();
            if (tree.contains(eci))
                return true;
        }
        return false;
    }

    protected void _search2(List order, boolean invertDirection) {

        int nodeCount = _instances.size();

        _trees = new ArrayList();

        // Node iterator
        Iterator i = null;
        if (order == null)
            i = _instances.iterator();
        else
            i = order.iterator();

        // Iterate over all cells in the circuit.
        for (; i.hasNext();) {
            EdifCellInstance eci = (EdifCellInstance) i.next();

            // If the cell is a part of a previous tree,
            // skip it and move on.
            if (isVisited(eci))
                continue;

            if (DEBUG)
                System.out.print("New Tree Node " + eci);
            //DFSTree tree = new DFSTree(this,eci,invertDirection) 
            //_trees.add(tree);
        }

    }

    public int getDepth(EdifCellInstance eci) {
        Integer i = (Integer) _depthMap.get(eci);
        if (i == null)
            return -1;
        return i.intValue();
    }

    /**
     * Perform a non-recursive depth-first visit of instances in an EdifCell.
     * This method uses an internal stack to manage the traversal through the
     * circuit instead of recursion. This leads to significantly lower memory
     * requirements (the recursive version would kill the JVM stack for large
     * designs).
     * 
     * @param eci1
     * @param invertDirection
     * @return
     */
    protected DFSTree _visitNonrecursive(EdifCellInstance eci1, boolean invertDirection) {

        Stack s = new Stack();
        s.push(eci1);
        _depthMap.put(eci1, new Integer(0));
        if (DEBUG)
            System.out.println("Pushing " + eci1);
        DFSTree tree = new DFSTree(eci1);

        // Continue processing instances as long as there is something
        // on the stack.
        while (!s.isEmpty()) {

            EdifCellInstance eci = (EdifCellInstance) s.peek();
            if (DEBUG)
                System.out.println("peek=" + eci + " size=" + s.size());

            if (_visited.contains(eci)) {
                // already visited - mark finished and pop
                Object p = s.pop();
                if (_finished.contains(eci)) {
                    // already finished. 
                    if (DEBUG)
                        System.out.println("Popping and Ignoring cell " + eci);
                    //throw new RuntimeException("branch node "+eci+" "+
                    //		_visited.contains(eci));
                    //System.err.println("Finished on stack "+p);
                    // This is happening . . .
                } else {
                    _finished.add(eci);
                    if (DEBUG)
                        System.out.println("Finished " + eci + " pop=" + p);
                }
            } else {

                // Cell has not yet been visited. Add to the visit list.
                _visited.add(eci);
                tree.add(eci);
                int depth = ((Integer) (_depthMap.get(eci))).intValue();

                Collection targetEPRs;
                if (invertDirection)
                    targetEPRs = _ic.getEPRsWhichReferenceOutputPortsOfSourcesOfECI(eci);
                else
                    targetEPRs = _ic.getEPRsWhichReferenceInputPortsOfSinksOfECI(eci);

                if (DEBUG) {
                    System.out.print("\t(" + depth + ") Visiting " + eci + " " + targetEPRs.size() + " eprs:");
                    for (Iterator i = targetEPRs.iterator(); i.hasNext();)
                        System.out.print(i.next() + " ");
                    System.out.println();
                }

                int successors = 0;
                for (Iterator i = targetEPRs.iterator(); i.hasNext();) {
                    EdifPortRef epr = (EdifPortRef) i.next();
                    EdifCellInstance successor = epr.getCellInstance();

                    if (successor == null)
                        continue; // connects to top-level port

                    if (successor != null) {
                        if (_visited.contains(successor)) {
                            if (_finished.contains(successor)) {
                                // a forward/cross edge
                                _forwardCrossEdges.add(epr);
                            } else {
                                // a backward edge
                                _backEdges.add(epr);
                                tree.addBackEdge(epr);
                                //System.out.println("\tBack "+epr.getCellInstance().getCellType().getName()+
                                //		" "+epr);
                            }
                        } else {
                            s.push(successor);
                            _depthMap.put(successor, new Integer(depth + 1));
                            if (DEBUG)
                                System.out.println("Pushing " + successor);
                            successors++;
                        }
                    }
                }
                if (successors > 0) {
                    //_depth++;
                } else {
                    // terminal node
                    if (_finished.contains(eci))
                        throw new RuntimeException("terminal node");
                    _finished.add(eci);
                    Object p = s.pop();
                    if (DEBUG)
                        System.out.println("Finished " + eci + " pop=" + p);
                }
            }
        }
        return tree;
    }

    /**
     * The original graph used by this algorithm.
     */
    //protected EdifCell _origCell;
    protected Collection _instances;

    /**
     * A list of Nodes in the graph in the order in which they are first
     * visited.
     */
    protected List _visited;

    /**
     * A list of Nodes in the graph in the order in which they finish
     */
    protected List _finished;

    /**
     * A list of root Nodes of trees in the forest.
     */
    protected Collection _treeRoots;

    /**
     * A Collection of edges in the graph that complete a cycle. TODO: there is
     * no ordering in this List so change the List to a Collection
     */
    protected List _backEdges;

    protected Collection _forwardCrossEdges;

    protected EdifCellInstanceGraph _ic;

    /**
     * The key to this map is a ECI. the Value is an Integer object that
     * represents the depth in the tree of the node.
     */
    Map _depthMap = new LinkedHashMap();

    protected static boolean DEBUG = false;

    protected Collection _trees;

}
