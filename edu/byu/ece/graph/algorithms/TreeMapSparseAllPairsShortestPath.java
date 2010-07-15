package edu.byu.ece.graph.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import edu.byu.ece.graph.BasicGraph;
import edu.byu.ece.graph.Edge;

/**
 * Performs an all pairs shortest path computation on a BasicGraph. This algorithm
 * assumes a 'sparse' graph where the graph is not fully connected. The data structure
 * grows at run-time to avoid the need to allocate a full NxM array of shortest path
 * distances. 
 * 
 * This class can also perform a shortest path in a limited number of iterations.
 */
public class SparseAllPairsShortestPath {

	public SparseAllPairsShortestPath(BasicGraph graph) {
		_graph = graph;
		
		Collection<Object> objs = _graph.getNodes();
		
		//_hash = new HashMap<Object,HashMap<Object, Integer>>(objs.size());
		_hash = new TreeMap<NumberedNode,TreeMap<NumberedNode, Integer>>();
		int i = 0;
		for (Object o : objs) {
			NumberedNode n = new NumberedNode (o,i);
			//HashMap<Object, Integer> oHash = new HashMap<Object,Integer>();
			TreeMap<NumberedNode, Integer> oHash = new TreeMap<NumberedNode, Integer>();
			//_hash.put(o, oHash);
			_hash.put(n, oHash);
			i++;
		}
		initialize();
	}

	public Collection<Object> getNodes() {
		return _hash.keySet();
	}

	public static SparseAllPairsShortestPath shortestPath(BasicGraph graph, int iterations) {
		SparseAllPairsShortestPath  mat = new SparseAllPairsShortestPath(graph);
		return mat.crossMultiply(iterations);
	}

	protected void initialize() {
		for (Edge e : _graph.getEdges()) {
			setValue(_hash, e.getSource(), e.getSink(), 1);
		}
		for(Object n : _graph.getNodes()) {
			setValue(_hash, n, n, 0);
		}			
	}	
	
	protected SparseAllPairsShortestPath crossMultiply(int iterations) {
		SparseAllPairsShortestPath curMatrix = this;
		for ( int i = 0; i < iterations; i ++) {
			curMatrix = curMatrix.crossMultiply();
		}
		return curMatrix;
	}
	
	protected SparseAllPairsShortestPath crossMultiply() {

		//we need a copy of _hash b/c we can't modify the source matrix
		//while multiplying it - leads to incorrect results
		HashMap<Object,HashMap<Object,Integer>> newHash = copyHash();

		
		for (Object i : getNodes()) {
			HashMap<Object, Integer> iMap = _hash.get(i);
			for (Object j : getNodes() ) {
				int d_old = Integer.MAX_VALUE;
				Integer d_ij = getValue(i,j);
				if (d_ij != null)
					d_old = d_ij.intValue();
				List<Object> keys = new ArrayList<Object>(iMap.keySet());
				for (int idx=0; idx<keys.size(); idx++) {
					Object k = keys.get(idx);
					int dik = Integer.MAX_VALUE;
					Integer d_ik = getValue(i,k);
					if (d_ik  != null)
						dik = d_ik.intValue();
					if (dik == Integer.MAX_VALUE)
						continue;
					int wkj = Integer.MAX_VALUE;
					Integer d_kj = getValue(k,j);					
					if (d_kj != null)
						wkj = d_kj.intValue();
					if (wkj == Integer.MAX_VALUE)
						continue;
					if (dik + wkj < d_old) {
						//System.out.println("dik: " + dik + " wkj: " + wkj);
						setValue(newHash, i,j, dik+wkj);
						//System.out.println("New value for " + i + ", " + j + " is " + dik+wkj);
					}
				}
			}
		}
		//now we can replace the old _hash with the squared version
		_hash = newHash;
		return this;
	}
	
	protected HashMap<Object,HashMap<Object,Integer>> copyHash() {
		Collection<Object> objs = getNodes();		
		HashMap<Object,HashMap<Object,Integer>> copied  = new HashMap<Object,HashMap<Object, Integer>>(objs.size());
		for (Object o1 : objs) {
			HashMap<Object, Integer> oHash = new HashMap<Object,Integer>();
			copied.put(o1, oHash);
			for (Object o2 : getNodes()) {
				Integer value = getValue(o1, o2);
				if (value != null) {
					setValue(copied, o1, o2, new Integer(value.intValue()));
				}
			}
		}
		return copied;
	}
	
	public Integer getValue(Object source, Object sink) {
		HashMap<Object, Integer> h = _hash.get(source);
		Integer i = h.get(sink);
		return i;
	}

	/**
	 * Set the value of an entry into the shortest path matrix.
	 */
	protected void setValue(HashMap<Object, HashMap<Object,Integer>> hash, Object source, Object sink, int val) {
		HashMap<Object, Integer> h = hash.get(source);
		h.put(sink, new Integer(val));
	}
	
	//only prints out an adjacency matrix for nodes that implement Comparable
	//(if they don't then there is no ordering since HashMap doesn't preserve it)
	public void printAdjacencyMatrix() {
		String retVal = "";
		List<Comparable> nodes = new LinkedList<Comparable>();
		for(Object node : getNodes()) {
			if (!(node instanceof Comparable)) {
				System.out.println("Cannot print adjacency matrix if nodes do not implement Comparable");
				return;
			}
			nodes.add((Comparable)node);
		}
		Collections.sort(nodes);
		for(Comparable c1 : nodes) {
			retVal += "[ ";
			for(int i=0; i<nodes.size(); i++) {
				Comparable c2 = nodes.get(i);
				if(getValue(c1, c2) == null) {
					retVal += "\u221E ";
				}
				else {
					retVal += getValue(c1, c2) + " ";
				}
			}
			retVal += "]\n";
		}		
		System.out.println(retVal);
	}

	/**
	 * The 2-dimensional data structure that contains the shortest path distances.
	 * A Map is used to allow the data structure to grow - entries for non infinite distances
	 * are created.
	 */
	protected TreeMap<NumberedNode,TreeMap<NumberedNode,Integer>> _hash;

	
	/**
	 * The graph that is used for the shortest path computation.
	 */
	BasicGraph _graph;

}

class NumberedNode implements Comparable {
	
	public NumberedNode(Object node, int i) {
		this.node = node;
		nodeNum = i;
	}

	public int compareTo(Object o) {
		NumberedNode n = (NumberedNode) o;
		if (n.nodeNum > nodeNum) return 1;
		if (n.nodeNum < nodeNum) return -1;
		return 0;
	}
	
	Object node;
	int nodeNum;
}