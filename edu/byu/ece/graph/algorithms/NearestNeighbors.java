package edu.byu.ece.graph.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import edu.byu.ece.edif.util.graph.EdifCellInstanceGraphFanOutComparator;
import edu.byu.ece.edif.util.graph.EdifNetFanOutComparator;
import edu.byu.ece.edif.util.graph.GraphNodeSizeComparator;
import edu.byu.ece.graph.BasicGraph;
import edu.byu.ece.graph.DirectedGraph;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.dfs.BasicDepthFirstSearchTree;
import edu.byu.ece.graph.dfs.DepthFirstTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

public class NearestNeighbors {

	public static boolean DEBUG1 = true;
	public static boolean DEBUG = false;
	
	public static SCCDepthFirstSearch nearestNeighborDecomposition(BasicGraph graph, int maxDistance) {
		
		// Copy the original graph - this graph will be changed throughout the algorithm.
		// Note that the node objects and edge objects in this new graph are the same objects
		// in the original graph. Only the topology information is different.
		BasicGraph workingGraph = (BasicGraph) graph.clone();

		// Maintain a list of the edges that need to be saved after the decomposition. These
		// edge objects also appear in the original graph.
		ArrayList<Edge> edgesToSave = new ArrayList<Edge>();
		
		// Create a sorted list of nodes
		if (DEBUG1) System.out.println("Starting Sort");
		ArrayList sortedNodeList = new ArrayList(workingGraph.getNodes());
		Collections.sort(sortedNodeList, new EdifCellInstanceGraphFanOutComparator(graph));
		Collections.reverse(sortedNodeList);

		if (DEBUG1) System.out.println("Starting Decomposition");
		
		do {
			// Pick a node that hasn't been visited yet (it doesn't matter which one)
			// Object root = workingGraph.getNodes().iterator().next();
			Object root = sortedNodeList.get(0);
				
			// Find its nearest n neigbhors of the root node
			ArrayList roots = new ArrayList(1);
			roots.add(root);
			Set<Object> neighbors = nearestNeighbors(workingGraph, roots, maxDistance);
			// Create a sub graph of this graph (use the small sub graph call since it
			// it is likely that the graph is much smaller than the original graph).
			BasicGraph subgraph = workingGraph.getSmallSubGraph(neighbors);
			// Compute the SCC on this sub graph
			SCCDepthFirstSearch sccDFS = new SCCDepthFirstSearch(subgraph);
			// Get the SCCs
			List<DepthFirstTree> sccs = sccDFS.getTopologicallySortedTreeList();
			// For each SCC:
			//  - Remove the nodes in the working graph
			//  - Save the edges of teh SCC
			int nodesRemoved = 0; int edgesSaved = 0;
			if (sccs.size() > 0) {
				for (DepthFirstTree t : sccs) {
					BasicDepthFirstSearchTree tree = (BasicDepthFirstSearchTree) t;
					nodesRemoved+=tree.getNodes().size();
					edgesSaved += tree.getAllEdges().size();
					edgesToSave.addAll(tree.getEdges()); // Note that this does not get the "cross" edges between trees
					workingGraph.removeNodes(tree.getNodes());				
					sortedNodeList.removeAll(tree.getNodes());
				}
				if (DEBUG1)
					if (nodesRemoved > 0) System.out.println("SCC: "+ nodesRemoved + " nodes removed from "+sccs.size()+" sccs (" + 
							(nodesRemoved / sccs.size())+" nodes/scc) "
							+ edgesSaved+" edges saved ("+(edgesSaved/sccs.size())+" edges/scc) ("+
							neighbors.size()+" neighbors)");
			} else {
				workingGraph.removeNode(root);
				sortedNodeList.remove(root);
				if (DEBUG)
					System.out.println("NO SCCs found - root node removed (graph size="+workingGraph.getNodes().size()+")");
			}
			
		} while (sortedNodeList.size() > 0);
		
		// We now have a list of edges that need to be saved. Create a new graph that is a copy of
		// the original graph but remove the edges that are not to be saved
		workingGraph = (BasicGraph) graph.clone();
		if (DEBUG1) System.out.println(edgesToSave.size() + " edges saved out of "+graph.getEdges().size());
		for (Edge e : workingGraph.getEdges()) {
			if (!edgesToSave.contains(e)) {
				workingGraph.removeEdge(e);
			}
		}

		// Perform a final SCC decomposition on the graph 
		if (DEBUG1) System.out.println("Neighbor SCC Decomposition");
		return new SCCDepthFirstSearch(workingGraph);
	}


	public static Set<Object> nearestNeighbors(DirectedGraph graph, Collection<Object> nodes, int maxDistance) {

		// Create a stack to manage the depth first search instead of using recursion
		// (the recursive version is very slow in Java)
		Stack<Object> s = new Stack<Object>();
		// Create a map between objects within the stack and their depth. The depth is needed
		// to limit the length of the depth first search.
		HashMap<Object,Integer> depthMap = new HashMap<Object,Integer>();        
		// Initialize the stack with the root nodes and the depths
        for (Object o : nodes) {
        	s.push(o);
        	depthMap.put(o, 0);
        	if (DEBUG) System.out.println("\nRoot Node:"+o);
        }
        // Create a set of objects that have already been visited
        HashSet<Object> visited = new HashSet<Object>();
                
        // Continue processing until the stack is empty (it is initialized with the root nodes)
        while (!s.isEmpty()) {
        	
        	// Check the top of the stack
        	Object obj = s.peek();

        	
        	if (visited.contains(obj)) {
        		// The object at the top of the stack has already been visited - pop it off and move on.
    			s.pop();
        	} else {
        		/*
        		 * Cell has not yet been visited. Mark as visited and add successors to the stack
        		 */
        		if (DEBUG) System.out.println("visiting depth " +depthMap.get(obj) + " " + obj);

        		visited.add(obj);
        		Collection<Edge> targetLinks = graph.getOutputEdges(obj);

        		// Determine the depth of the current node
        		int curDepth = depthMap.get(obj);
        		// If the depth of the current node is less than the max distance, add all of the
        		// direct successors of the current node.
        		if (curDepth < maxDistance) {
        			for (Edge edge : targetLinks) {
        				Object successor = edge.getSink();
        				if (DEBUG) System.out.println("\tChild "+successor);
        				// if the successor hasn't been visited, put it on the stack and
        				// give it a depth of 1 + the current depth of the node
        				if (!visited.contains(successor)) {
        					s.push(successor);
        					depthMap.put(successor, curDepth+1);
        					if (DEBUG) System.out.println("\t\tNever Visited");
        				} else {
        					if (DEBUG) System.out.println("\t\tAlready Visited");
        				}
        			}
        		}
        	}
        }
        return visited;
	}

}
