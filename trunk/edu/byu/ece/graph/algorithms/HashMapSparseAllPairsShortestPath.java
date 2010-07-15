package edu.byu.ece.graph.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import edu.byu.ece.graph.BasicGraph;
import edu.byu.ece.graph.Edge;

public class HashMapSparseAllPairsShortestPath {
	public HashMapSparseAllPairsShortestPath(BasicGraph graph) {
		_graph = graph;
		
		Collection<Object> objs = _graph.getNodes();
		
		_hash = new HashMap<Object,HashMap<Object, Integer>>(objs.size());
		for (Object o : objs) {
			HashMap<Object, Integer> oHash = new HashMap<Object,Integer>();
			_hash.put(o, oHash);
		}
		initialize();
	}

	public Collection<Object> getNodes() {
		return _hash.keySet();
	}

	public static HashMapSparseAllPairsShortestPath shortestPath(BasicGraph graph, int iterations) {
		HashMapSparseAllPairsShortestPath  mat = new HashMapSparseAllPairsShortestPath(graph);
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
	
	protected HashMapSparseAllPairsShortestPath crossMultiply(int iterations) {
		HashMapSparseAllPairsShortestPath curMatrix = this;
		for ( int i = 0; i < iterations; i ++) {
			curMatrix = curMatrix.crossMultiply();
		}
		return curMatrix;
	}
	
	protected HashMapSparseAllPairsShortestPath crossMultiply() {
		//we need an empty HashMap to store the results
		HashMap<Object,HashMap<Object,Integer>> newHash = getEmptyHash();
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
						setValue(newHash, i,j, dik+wkj);
					}
					else {
						setValue(newHash, i,j, d_old);
					}
				}
			}
		}
		//now we can replace the old _hash with the squared version
		_hash = newHash;
		return this;
	}
	
	protected HashMap<Object,HashMap<Object,Integer>> getEmptyHash() {
		Collection<Object> objs = getNodes();		
		HashMap<Object,HashMap<Object,Integer>> empty  = new HashMap<Object,HashMap<Object, Integer>>(objs.size());
		for (Object o1 : objs) {
			HashMap<Object, Integer> oHash = new HashMap<Object,Integer>();
			empty.put(o1, oHash);
		}
		return empty;
	}
	
	public Integer getValue(Object source, Object sink) {
		HashMap<Object, Integer> h = _hash.get(source);
		Integer i = h.get(sink);
		return i;
	}

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
		
	protected HashMap<Object,HashMap<Object,Integer>> _hash;
	BasicGraph _graph;
}
