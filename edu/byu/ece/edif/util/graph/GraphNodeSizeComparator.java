package edu.byu.ece.edif.util.graph;

import java.util.Comparator;

import edu.byu.ece.graph.DirectedGraph;

/**
 * A comparator that compares DirectedGraph objects in terms
 * of the node count. 
 */
public class GraphNodeSizeComparator implements Comparator<DirectedGraph> {	
	
	public int compare(DirectedGraph graph1, DirectedGraph graph2) {
		if (graph1.getNodes().size() > graph2.getNodes().size())
			return 1;
		if (graph1.getNodes().size() < graph2.getNodes().size())
			return -1;
		return 0;
	}
	
	public boolean equals(Object o) {
		if (o == this)
			return true;
		return false;
	}
	
}
