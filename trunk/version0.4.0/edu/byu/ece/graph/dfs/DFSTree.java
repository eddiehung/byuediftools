/*
 * Depth-first search tree.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;

/**
 * This class represents a depth-first tree.
 * <p>
 * TODO: Change this class name to EdifCellDFSTree
 * 
 * @author wirthlin
 * @since Created on Jun 3, 2005
 */
public class DFSTree extends ArrayList {

    public DFSTree(EdifCellInstance eci) {
        super();
        _root = eci;
    }

    public DFSTree(int i) {
        super(i);
    }

    public Collection getBackEdges() {
        return _backEdges;
    }

    public void addBackEdge(Object o) {
        _backEdges.add(o);
    }

    public int getInstanceDepth(EdifCellInstance eci) {
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
    protected void _visitNonrecursive(EdifCellInstance eci1, boolean invertDirection, Collection otherTrees,
            EdifCellInstanceGraph ic) {

        Collection visited = new ArrayList();

        Stack s = new Stack();
        //int depth = 0;
        s.push(eci1);
        _depthMap.put(eci1, new Integer(0));
        if (DEBUG)
            System.out.println("Pushing " + eci1);

        // Continue processing instances as long as there is something
        // on the stack.
        while (!s.isEmpty()) {

            /**
             * Get the top instance on the stack. Do not pop off the item at
             * this time. An item on the top of the stack may be in one of two
             * different states:
             * <ol>
             * <li> when it is seen for the first time (i.e. before it has been
             * visited). In this case, mark the instance as having been visited
             * and process each of its children (add children to stack if
             * necessary).
             * <li> when it is seen for the second time after each of its
             * children have been processed (i.e. after it has been visited). In
             * this case, mark the instance as being finished and pop from the
             * stack.
             * </ol>
             */
            EdifCellInstance eci = (EdifCellInstance) s.peek();
            if (DEBUG)
                System.out.println("peek=" + eci + " size=" + s.size());

            if (visited.contains(eci)) {
                // already visited - mark finished and pop
                if (_finished.contains(eci)) {
                    // already finished. 
                    s.pop();
                    System.err.println("Warning: A finished node is on the stack " + eci);
                    if (DEBUG)
                        System.out.println("Popping and Ignoring cell " + eci);
                    //throw new RuntimeException("branch node "+eci+" "+
                    //		_visited.contains(eci));
                } else {
                    _finished.add(eci);
                    Object p = s.pop();
                    if (DEBUG)
                        System.out.println("Finished " + eci + " pop=" + p);
                }
            } else {
                // Cell has not yet been visited.
                visited.add(eci);
                add(eci);

                Collection targetEPRs;
                if (invertDirection)
                    targetEPRs = ic.getEPRsWhichReferenceOutputPortsOfSourcesOfECI(eci);
                else
                    targetEPRs = ic.getEPRsWhichReferenceInputPortsOfSinksOfECI(eci);

                int depth = ((Integer) (_depthMap.get(eci))).intValue();
                if (DEBUG) {
                    System.out.print("\t(" + depth + ") Visiting " + eci + " " + targetEPRs.size() + " eprs:");
                    for (Iterator i = targetEPRs.iterator(); i.hasNext();)
                        System.out.print(i.next() + " ");
                    System.out.println();
                }

                int unvisited_successors = 0;
                for (Iterator i = targetEPRs.iterator(); i.hasNext();) {
                    EdifPortRef epr = (EdifPortRef) i.next();
                    EdifCellInstance successor = epr.getCellInstance();

                    if (successor == null)
                        continue; // connects to top-level port

                    if (successor != null) {
                        if (otherTrees.contains(successor)) {
                            // a forward/cross edge
                            _forwardCrossEdges.add(epr);
                        } else if (visited.contains(successor)) {
                            _backEdges.add(epr);
                        } else {
                            s.push(successor);
                            _depthMap.put(successor, new Integer(depth + 1));
                            if (DEBUG)
                                System.out.println("Pushing " + successor);
                            unvisited_successors++;
                        }
                    }
                }
            }
        }
    }

    // TODO: fix variables
    public static boolean DEBUG = false;

    Collection _forwardCrossEdges = new ArrayList();

    /**
     * The key to this map is a ECI. the Value is an Integer object that
     * represents the depth in the tree of the node.
     */
    Map _depthMap = new LinkedHashMap();

    /**
     * An ordered List of the finish times of the depth first search.
     */
    List _finished = new ArrayList();

    /**
     * A collection of edges in the tree that go back inside of the tree (i.e.
     * feedback edges).
     */
    Collection _backEdges = new ArrayList();

    /**
     * The root of the DFSTree.
     */
    EdifCellInstance _root;

}
