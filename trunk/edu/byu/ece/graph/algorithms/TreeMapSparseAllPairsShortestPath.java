package edu.byu.ece.graph.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
public class TreeMapSparseAllPairsShortestPath {

	public TreeMapSparseAllPairsShortestPath(BasicGraph graph) {
		_graph = graph;
		_treemap = new TreeMap<NumberedNode,TreeMap<NumberedNode, Integer>>();
		_nodesToNumberedNodes = new HashMap<Object, NumberedNode>();
		
		Collection<Object> objs = _graph.getNodes();
		int i = 0;
		for (Object o : objs) {
			NumberedNode n = new NumberedNode (o,i);
			TreeMap<NumberedNode, Integer> oHash = new TreeMap<NumberedNode, Integer>();
			_treemap.put(n, oHash);
			_nodesToNumberedNodes.put(o, n);
			i++;
		}
		initialize(_nodesToNumberedNodes);
	}

	public Collection<NumberedNode> getNodes() {
		return _treemap.keySet();
	}
	
	public static TreeMapSparseAllPairsShortestPath shortestPath(BasicGraph graph, int iterations) {
		TreeMapSparseAllPairsShortestPath mat = new TreeMapSparseAllPairsShortestPath(graph);
		return mat.crossMultiply(iterations);
	}

	protected void initialize(HashMap<Object, NumberedNode> nodesToNumberedNodes) {
		for (Edge e : _graph.getEdges()) {
			NumberedNode source = nodesToNumberedNodes.get(e.getSource());
			NumberedNode sink = nodesToNumberedNodes.get(e.getSink());
			setValue(_treemap, source, sink, 1);
		}
		for(Object n : _graph.getNodes()) {
			NumberedNode nn = nodesToNumberedNodes.get(n);
			setValue(_treemap, nn, nn, 0);
		}			
	}	
	
	protected TreeMapSparseAllPairsShortestPath crossMultiply(int iterations) {
		TreeMapSparseAllPairsShortestPath curMatrix = this;
		for ( int i = 0; i < iterations; i ++) {
			curMatrix = curMatrix.crossMultiply();
		}
		return curMatrix;
	}
	
	protected TreeMapSparseAllPairsShortestPath crossMultiply() {

		//we need an empty TreeMap to store the results
		TreeMap<NumberedNode,TreeMap<NumberedNode,Integer>> newTree = getEmptyTreeMap();		
		for (NumberedNode i : getNodes()) {
			TreeMap<NumberedNode, Integer> iMap = _treemap.get(i);
			for (NumberedNode j : getNodes() ) {
				int d_old = Integer.MAX_VALUE;
				Integer d_ij = getValue(i,j);
				if (d_ij != null)
					d_old = d_ij.intValue();
				List<NumberedNode> keys = new ArrayList<NumberedNode>(iMap.keySet());
				for (int idx=0; idx<keys.size(); idx++) {
					NumberedNode k = keys.get(idx);
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
						setValue(newTree, i,j, dik+wkj);
					}
					else {
						setValue(newTree, i,j, d_old);
					}
				}
			}
		}
		//now we can replace the old _treemap with the squared version
		_treemap = newTree;
		return this;
	}
	
	protected TreeMap<NumberedNode,TreeMap<NumberedNode,Integer>> getEmptyTreeMap() {
		Collection<NumberedNode> nodes = getNodes();		
		TreeMap<NumberedNode,TreeMap<NumberedNode,Integer>> empty  = new TreeMap<NumberedNode,TreeMap<NumberedNode,Integer>>();
		for (NumberedNode n1 : nodes) {
			TreeMap<NumberedNode, Integer> oTree = new TreeMap<NumberedNode, Integer>();
			empty.put(n1, oTree);
		}
		return empty;
	}
	
	protected Integer getValue(NumberedNode source, NumberedNode sink) {
		TreeMap<NumberedNode, Integer> t = _treemap.get(source);
		Integer i = t.get(sink);
		return i;
	}
	
	public Integer getValue(Object source, Object sink) {
		Integer retVal = null;
		NumberedNode sourceNode = _nodesToNumberedNodes.get(source);
		NumberedNode sinkNode = _nodesToNumberedNodes.get(sink);
		if (sinkNode == null || sourceNode == null) {
			retVal = null;
		}
		else {
			retVal = getValue(sourceNode, sinkNode);
		}
		return retVal;
	}

	/**
	 * Set the value of an entry into the shortest path matrix.
	 */
	protected void setValue(TreeMap<NumberedNode, TreeMap<NumberedNode,Integer>> treemap, NumberedNode source, NumberedNode sink, int val) {
		TreeMap<NumberedNode, Integer> t = treemap.get(source);
		t.put(sink, new Integer(val));
	}
	
	public void printAdjacencyMatrix() {
		String retVal = "";
		List<NumberedNode> nodes = new ArrayList<NumberedNode>(getNodes());
		for(NumberedNode c1 : nodes) {
			retVal += "[ ";
			for(int i=0; i<nodes.size(); i++) {
				NumberedNode c2 = nodes.get(i);
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
	protected TreeMap<NumberedNode,TreeMap<NumberedNode,Integer>> _treemap;
	
	/**
	 * The graph that is used for the shortest path computation.
	 */
	BasicGraph _graph;

	/**
	 * Maps graph nodes to NumberedNodes (used for initialization
	 * and external lookups)
	 */
	HashMap<Object, NumberedNode> _nodesToNumberedNodes;
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