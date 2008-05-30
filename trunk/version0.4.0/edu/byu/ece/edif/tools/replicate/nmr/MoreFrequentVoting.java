/*
 * Methods used to insert voters in a design represented in an EdifGraph
 * structure.
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
package edu.byu.ece.edif.tools.replicate.nmr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.util.graph.EdifCellBadCutGroupings;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.graph.Edge;

/**
 * This class contains methods used to insert voters in a design represented in
 * an EdifGraph structure. The class gives the user the ability to specify the
 * desired frequency of voters to be inserted throughout the design.
 * 
 * @author Brian Pratt
 */
public class MoreFrequentVoting {

    /**
     * Locates the Edges where voters should be inserted in a graph given a
     * threshold of levels of logic between voters. Does a breadth-first search
     * to find the voter locations.
     * <p>
     * NOTE: this algorithm is not sophisticated and can give poor results if
     * the wrong threshold is given as the design will not be partitioned
     * evenly.
     * 
     * @param connectivityGraph
     * @param nmrArch
     * @param insertionThreshold
     * @return A Collection of EdifEdges where voters should be inserted
     */
    public static Collection<Edge> insertVotersByLogicLevels(EdifCellInstanceGraph connectivityGraph,
            NMRArchitecture nmrArch, int insertionThreshold) {

        return insertVotersByLogicLevels(connectivityGraph, nmrArch, null, insertionThreshold);
    }

    /**
     * Locates the Edges where voters should be inserted in a graph given a
     * threshold of levels of logic between voters. Does a breadth-first search
     * to find the voter locations.
     * <p>
     * NOTE: this algorithm is not sophisticated and can give poor results if
     * the wrong threshold is given as the design will not be partitioned
     * evenly.
     * 
     * @param connectivityGraph
     * @param nmrArch
     * @param previouslyCutEdges
     * @param insertionThreshold
     * @return A Collection of EdifEdges where voters should be inserted
     */
    public static Collection<Edge> insertVotersByLogicLevels(EdifCellInstanceGraph connectivityGraph,
            NMRArchitecture nmrArch, Collection<Edge> previouslyCutEdges, int insertionThreshold) {
        Collection<Edge> startEdges = new ArrayList<Edge>();

        // Create graph with no top-level ports
        // (We don't want to insert voters in-between ports and IOBs)
        EdifCellInstanceGraph newConnectivityGraph = (EdifCellInstanceGraph) connectivityGraph.clone();
        newConnectivityGraph.removeTopLevelPortNodes();

        // Find output edges of graph
        Collection outputNodes = connectivityGraph.getNodesWithNoOutputEdges();
        for (Object node : outputNodes) {
            startEdges.addAll(connectivityGraph.getInputEdges(node));
        }

        return insertVotersByLogicLevels(connectivityGraph, nmrArch, startEdges, previouslyCutEdges,
                insertionThreshold, false, true);
    }

    /**
     * Determines the locations where voters should be inserted in a given
     * design (represented by an EdifGraph). The frequency of voters is
     * determined by the 'insertionThreshold' argument.
     * <p>
     * As an example, this method can be used on a component graph (SCCs are
     * collapsed), with the "startEdges" Collection containing all of the
     * EdifEdges which are connected to the top-level outputs of the (flattened)
     * graph, with false and true passed in for the forward and backward
     * parameters, respectively.
     * <p>
     * TODO: Should this method include resource tracking information? (for each
     * voter that is added) Can we estimate how may voters will be inserted for
     * a given graph and insertionThreshold?
     * 
     * @param graph
     * @param nmrArch
     * @param startEdges A Collection of EdifEdges the voter insertion should
     * start from
     * @param previouslyCutEdges
     * @param insertionThreshold
     * @param forward
     * @param backward
     * @return A Collection of EdifEdges where voters should be inserted
     */
    public static Collection<Edge> insertVotersByLogicLevels(EdifCellInstanceGraph graph, NMRArchitecture nmrArch,
            Collection<Edge> startEdges, Collection<Edge> previouslyCutEdges, int insertionThreshold, boolean forward,
            boolean backward) {

        Collection<Edge> voterEdges = new LinkedHashSet();
        // Replace previouslyCutEdges with a Collection to manipulate
        previouslyCutEdges = new LinkedHashSet(previouslyCutEdges);

        // Basic Algorithm:
        // - Iterate over all EdifEdges in startEdges
        //    1. Do depth-first search starting from startEdge
        //       - Do two DFSs if both forward and backward are true
        //       a. Decrement insertionThreshold at each new level
        //       b. If threshold reaches zero, insert voter (tag Edge)
        //       c. If pre/suc-ceding edge has voter, stop DFS of that branch
        for (Edge startEdge : startEdges) {
            Collection<Edge> newVoterEdges = new LinkedHashSet();
            if (forward)
                newVoterEdges.addAll(breadthFirstVoterInsertion(graph, nmrArch, startEdge, previouslyCutEdges,
                        insertionThreshold, true));
            if (backward)
                newVoterEdges.addAll(breadthFirstVoterInsertion(graph, nmrArch, startEdge, previouslyCutEdges,
                        insertionThreshold, false));

            // Add this round to the global set of voter edges
            voterEdges.addAll(newVoterEdges);
            // These are now previouslyCutEdges
            previouslyCutEdges.addAll(newVoterEdges);
        }

        return voterEdges;
    }

    // BHP: What about Instances that are not triplicated? We don't need voters there.
    //      What does TMR do with a cut where the Instances go 1-to-1?
    //      A: NMREdifCell handles this already. Though it is extra work to 
    //         add cuts throughout the design, it should not adversely affect
    //         the triplicated circuit. (Unless there are pockets of TMR that
    //         we are out of sync with...)
    // BHP: Could just use one stack and use RPN-style. (Push Instances, push threshold.
    //      Same threshold applies to all Instances until another Integer is seen)
    public static Set<Edge> breadthFirstVoterInsertion(EdifCellInstanceGraph graph, NMRArchitecture nmrArch,
            Edge startEdge, Collection<Edge> previouslyCutEdges, int insertionThreshold, boolean forward) {
        // Basic Algorithm:
        // - Start from 'startEdge'
        //   1. Initialize stack with 'startEdge' at level 'insertionThreshold'
        //   2. Pop one Edge from stack, loop until stack is empty
        //      a. Pop threshold value from threshold stack
        //   3. Get all successors of this Edge (if 'forward' is true, 
        //      else get predecessors)
        //      a. Throw away EdifEdges that are in voterEdges Collection
        //      b. If threshold-1 is zero, add remaining EdifEdges to voterEdges
        //      c. Push EdifEdges onto the stack
        //         i) If threshold-1 is zero, push insertionThreshold onto stack for each 
        //         ii) Else push threshold-1 onto threshold stack for each
        //   4. Repeat from #2 until stack is empty

        Set<Edge> voterEdges = new LinkedHashSet();

        Stack<Edge> edgeStack = new Stack<Edge>();
        Stack<Integer> logicLevelStack = new Stack<Integer>();

        // 1. 
        edgeStack.push(startEdge);
        logicLevelStack.push(insertionThreshold);
        // Keep a list of visited nodes to avoid traversing loops infinitely
        Set visitedEdges = new LinkedHashSet();

        while (!edgeStack.empty()) {
            // 2.
            Edge currentEdge = edgeStack.pop();
            Integer currentLogicLevel = logicLevelStack.pop();
            // Tag edge as visited
            visitedEdges.add(currentEdge);

            // 3.
            Collection<Edge> adjacentEdges = new ArrayList<Edge>();
            if (forward)
                adjacentEdges = graph.getOutputEdges(currentEdge.getSink());
            else
                adjacentEdges = graph.getInputEdges(currentEdge.getSource());
            for (Edge adjacentEdge : adjacentEdges) {
                // 3a.
                /*
                 * TODO: Should we continue past previously-tagged "voterEdges"
                 * and just stop traversing when a visited Edge is found? Should
                 * the count just be reset at a voterEdge?
                 */
                if (previouslyCutEdges.contains(adjacentEdge) || visitedEdges.contains(adjacentEdge))
                    continue;
                // 3b.
                // 3c.
                edgeStack.push(adjacentEdge);
                // No voter yet
                if (currentLogicLevel - 1 > 0) {
                    // Don't need a voter yet. Count down one level.
                    logicLevelStack.push(currentLogicLevel - 1);
                }
                // Time to add voter
                else {
                    // Check for bad cut edges. Don't count as voter edge.
                    /*
                     * TODO: Or should we enforce that the incoming graph be a
                     * badCutGrouping Graph?
                     */
                    if (EdifCellBadCutGroupings.isBadCutEdge(adjacentEdge, nmrArch)) {
                        logicLevelStack.push(1); // Vote on next available edge
                    } else {
                        // Tag voter edges and start the count over.
                        voterEdges.add(adjacentEdge);
                        //System.out.println("Adding Voter Edge: "+adjacentEdge);
                        logicLevelStack.push(insertionThreshold);
                    }
                }
            }
        }

        return voterEdges;
    }

    /**
     * Obtains the correct EdifEdges in the graph corresponding to the given
     * EdifPortRef objects.
     * 
     * @param graph The EdifCellInstanceGraph graph the Edges are located in
     * @param cutSet The EdifPortRefs to obtain the EdifEdges of
     * @return A Collection of EdifEdges corresponding to the given EdifPortRef
     * objects in the given graph
     */
    public static Collection<Edge> getEdifEdgesFromPortRefs(EdifCellInstanceGraph graph, Collection<EdifPortRef> cutSet) {
        // If this graph is an EdifCellInstanceGraph graph, find the 
        //   EdifCellInstances from the EdifPortRefs and grab the output edges
        Collection<Edge> cutEdges = new LinkedHashSet<Edge>(cutSet.size());
        for (EdifPortRef cutEPR : cutSet) {
            EdifCellInstance eci = cutEPR.getCellInstance();
            if (eci != null) {
                cutEdges.addAll(graph.getOutputEdges(eci));
            } else { // Top-level port
                // This should not happen
            }
        }

        return cutEdges;
    }

    /**
     * Determines locations to insert voters in order to partition the graph
     * into X partitions. The spacing between voters is chosen so as to try and
     * create partitions of equal size.
     * 
     * @param connectivityGraph
     * @param nmrArch
     * @param previouslyCutEdges
     * @param numberOfPartitions
     * @return A Collection of EdifEdges where voters should be inserted
     */
    public static Collection<Edge> partitionGraphWithVoters(EdifCellInstanceGraph connectivityGraph,
            NMRArchitecture nmrArch, Collection<Edge> previouslyCutEdges, int numberOfPartitions) {
        int insertionThreshold = 0;
        Collection<Edge> startEdges = new ArrayList<Edge>();

        // Create graph with no top-level ports
        // (We don't want to insert voters in-between ports and IOBs)
        EdifCellInstanceGraph newConnectivityGraph = (EdifCellInstanceGraph) connectivityGraph.clone();
        newConnectivityGraph.removeTopLevelPortNodes();

        // Find output edges of graph
        Collection outputNodes = connectivityGraph.getNodesWithNoOutputEdges();
        for (Object node : outputNodes) {
            startEdges.addAll(connectivityGraph.getInputEdges(node));
        }

        // Count the number of logic levels in this circuit to determine the
        //   voter insertion threshold.
        //   - Count max depth? min? average?
        // Store all depths in a sorted set
        TreeSet<Integer> depths = new TreeSet<Integer>();
        // Stacks are cheaper than recursion
        Stack nodeStack = new Stack();
        Stack<Integer> depthStack = new Stack<Integer>();
        // Initialize stack
        for (Object node : outputNodes) {
            nodeStack.push(node);
            depthStack.push(0);
        }
        int depths_sum = 0;
        int num_paths = 0;
        // Traverse the entire design
        // Keep a list of visited nodes to avoid traversing loops infinitely
        Set visitedNodes = new LinkedHashSet();

        /*
         * TODO: This doesn't necessarily find the maximum path length. It does
         * not check all possible paths because of the visitedNodes list (which
         * is necessary to detect and avoid feedback).
         */
        while (!nodeStack.isEmpty()) {
            Object node = nodeStack.pop();
            Integer depth = depthStack.pop();
            visitedNodes.add(node);

            Collection predecessors = connectivityGraph.getPredecessors(node);
            // Check if we've hit the end of a line
            if (predecessors.isEmpty()) {
                /*
                 * NOTE: This would also end at constant nodes (anything without
                 * an input) This could be modified to check for top-level input
                 * nodes only
                 */
                depths.add(depth);
                depths_sum += depth;
                num_paths++;
            } else
                // Push the predecessors onto the stack, incrementing the depth count
                for (Object pred : predecessors) {
                    // Don't push onto stack if the node has been visited already
                    if (!visitedNodes.contains(pred)) {
                        nodeStack.push(pred);
                        depthStack.push(depth + 1);
                    }
                }

            // DEBUG
            //if (visitedNodes.size() % 1000 == 0)
            //	System.out.print("\n\tvisitedNodes.size() = "+visitedNodes.size());
        }
        // Calculate the max, min, median, and average depth
        int max_depth = depths.last();
        int min_depth = depths.first();
        Object[] depths_array = depths.toArray();
        int median_depth = (Integer) depths_array[depths.size() / 2];
        int average_depth = depths_sum / num_paths;
        //System.out.println("max_depth="+max_depth+", min_depth="+min_depth+", median_depth="+median_depth+", average_depth="+average_depth);

        // Set insertion threshold by...?
        // TODO: figure out the best way to do this. Or make it an option?
        // Divide the depth by the number of partitions requested.
        if (numberOfPartitions > 1)
            insertionThreshold = max_depth / numberOfPartitions;
        else
            insertionThreshold = max_depth + 1; // One partition only

        //System.out.print("(insertionThreshold="+insertionThreshold+")");
        return insertVotersByLogicLevels(connectivityGraph, nmrArch, startEdges, previouslyCutEdges,
                insertionThreshold, false, true);
    }

}
