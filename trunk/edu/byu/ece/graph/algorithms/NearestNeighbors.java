package edu.byu.ece.graph.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

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
		
		// Copy the original graph. This graph will be changed throughout the algorithm.
		// Note that the node objects and edge objects in this new graph are the same objects
		// in the original graph.
		BasicGraph workingGraph = (BasicGraph) graph.clone();

		// Maintain a list of the edges that need to be saved after the decomposition. These
		// edge objects also appear in the original graph.
		ArrayList<Edge> edgesToSave = new ArrayList<Edge>();
		do {
			// Pick a node that hasn't been visited yet (it doesn't matter which one)
			Object root = workingGraph.getNodes().iterator().next();
			
			// Find its nearest n neigbhors
			Set<Object> neighbors = nearestNeighbors(workingGraph, root, maxDistance);
			// Create a sub graph of this graph (use the small sub graph call since it
			// it is likely that the graph is much smaller than the original graph).
			BasicGraph subgraph = workingGraph.getSmallSubGraph(neighbors);
			// Compute the SCC on this sub graph
			SCCDepthFirstSearch sccDFS = new SCCDepthFirstSearch(subgraph);
			// Get the SCCs
			List<DepthFirstTree> sccs = sccDFS.getTopologicallySortedTreeList();
			// For each SCC:
			//  - Remove the nodes in the working graph
			//  - Save the edges
			int nodesRemoved = 0; int edgesSaved = 0;
			for (DepthFirstTree t : sccs) {
				BasicDepthFirstSearchTree tree = (BasicDepthFirstSearchTree) t;
				workingGraph.removeNodes(tree.getNodes());				
				nodesRemoved+=tree.getNodes().size();
				edgesToSave.addAll(tree.getAllEdges());
				edgesSaved += tree.getAllEdges().size();
			}
			if (DEBUG1)
				if (nodesRemoved > 0) System.out.println(nodesRemoved + " nodes removed from "+sccs.size()+" sccs "+
						edgesSaved+" edges saved");
				else System.out.println("No SCCs found - only one node removed");
					
			// Remove the root node (if it has not already been removed)
			workingGraph.removeNode(root);			
		} while (workingGraph.getNodes().size() > 0);
		
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
		return new SCCDepthFirstSearch(workingGraph);
	}

	public static Set<Object> nearestNeighbors(DirectedGraph graph, Object node, int maxDistance) {

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
        HashSet<Object> visited = new HashSet<Object>();
        //ArrayList<Object> finished = new ArrayList<Object>();
        HashMap<Object,Integer> depthMap = new HashMap<Object,Integer>();
        depthMap.put(node, 0);
        
        if (DEBUG)
            System.out.println("New TREE with root=" + node);
        
        // Continue processing until the stack is empty
        while (!s.isEmpty()) {
        	Object obj = s.peek();
        	
        	if (visited.contains(obj)) {
        		// already visited - pop and move on.
    			s.pop();
        	} else {
        		/*
        		 * Cell has not yet been visited. Mark as visited and add successors to the stack
        		 */
        		if (DEBUG) System.out.println("visiting " + obj);

        		visited.add(obj);
        		Collection<Edge> targetLinks = graph.getOutputEdges(obj);

        		if (DEBUG)
        			if (targetLinks.size() > 0) System.out.print("\tPUSH links: ");
        			else System.out.print("\tNO LINKS to Push.");

        		// Add successors if max distance has not been violated
        		int curDepth = depthMap.get(obj);
        		if (curDepth < maxDistance) {
        			for (Edge edge : targetLinks) {
        				if (DEBUG) System.out.print(edge + " ");
        				Object sink = edge.getSink();
        				if (!visited.contains(sink)) {
        					s.push(sink);
        					depthMap.put(sink, curDepth+1);
        				}
        			}
        		}
        		if (DEBUG) System.out.println();
        	}
        }
        return visited;
	}

}
