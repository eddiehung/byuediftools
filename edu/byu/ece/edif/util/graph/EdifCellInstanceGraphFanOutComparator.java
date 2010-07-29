package edu.byu.ece.edif.util.graph;

import java.util.Comparator;

import edu.byu.ece.graph.DirectedGraph;

/**
 * A comparator that compares the fan out of EdifNet objects
 */
public class EdifCellInstanceGraphFanOutComparator implements Comparator {	
	
	public EdifCellInstanceGraphFanOutComparator(DirectedGraph graph) {
		_graph = graph;
	}
	
	public int compare(Object eci1, Object eci2) {
		int net1FanOut = _graph.getOutputEdges(eci1).size();
		int net2FanOut = _graph.getOutputEdges(eci2).size();
		
		if (net1FanOut > net2FanOut)
			return -1;
		if (net1FanOut < net2FanOut)
			return 1;
		return 0;
	}
	
	public boolean equals(Object o) {
		if (o == this)
			return true;
		return false;
	}
	
	
	DirectedGraph _graph;
}
