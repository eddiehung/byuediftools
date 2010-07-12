package edu.byu.ece.edif.util.graph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import edu.byu.ece.edif.arch.xilinx.XilinxMergeParser;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifPrintWriter;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.graph.BasicGraph;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * 
 * This class is designed to provide a mathematical graph view of an
 * EdifCell similar to that provided by EdifCellInstanceGraph. The main
 * difference is that full connectivity is not assumed between all input
 * ports of an cell and all of its output ports - for example, an
 * EdifCellInstance with two outputs may assume that a subset of inputs
 * are dependences for one output, while a different subset of the
 * inputs are dependences for the other output.
 * 
 * In order to provide for this new assumption, most nodes of this graph
 * (EdifPortRefGroupNode) are an extension of List<EdifPortRef>. In many 
 * cases, such as for a 4-input LUT, these nodes may simply contain all 
 * input and output EdifPortRefs, but more complex structures may use 
 * more than one EdifPortRefGroupNode to represent less simplistic 
 * connectivity. As in EdifCellInstanceGraph, nodes representing top
 * level ports are represented as EdifSingleBitPort objects.
 * 
 * This class contains a Map with EdifPortRef as the key and 
 * EdifPortRefGroupEdge as the value. This is necessary to avoid
 * constant searching for the EdifPortRefGroup to which an 
 * EdifPortRef object belongs (wasn't necessary in EdifCellInstanceGraph
 * since the EdifPortRef has a reference to its EdifCellInstance)
 * 
 * TODO: most of the methods in this class are almost identical to the
 * corresponding methods in EdifCellInstanceGraph. Can some of this
 * functionality be put into AbstractEdifGraph? The main differences
 * from this class are in _init(), setupInstancePortRefNodes() (not in
 * EdifCellInstanceGraph), and removeIOBufs() (see below)
 * 
 * TODO: The removeIOBufs() method is not currently implemented.
 * It shouldn't be hard to do, but I noticed that there are no
 * references to the corresponding method of EdifCellInstanceGraph,
 * so I wasn't sure if it was necessary.
 * 
 * TODO: add code for splitting cells (see setupInstancePortRefNodes())
 * 
 * @author whowes
 *
 */

public class EdifPortRefGroupGraph extends AbstractEdifGraph {
    /**
     * Construct a new EdifPortRefGroupGraph from an EdifCell. Default to include
     * Top Level Ports in the graph and NOT create source-to-source Edges in the
     * Graph or initially split nodes.
     * 
     * @param c
     */
    public EdifPortRefGroupGraph(EdifCell c) {
        this(c, true, false, false);
    }

    /**
     * Construct a new EdifPortRefGroupGraph from an EdifCell. Default to NOT
     * create source-to-source Edges in the Graph or initially split nodes.
     * 
     * @param c
     */
    public EdifPortRefGroupGraph(EdifCell c, boolean includeTopLevelPorts) {
        this(c, includeTopLevelPorts, false, false);
    }

    /**
     * Construct a new EdifPortRefGroupGraph object from an EdifCell. Default to
     * NOT initially split nodes
     * 
     * @param c
     */
    public EdifPortRefGroupGraph(EdifCell c, boolean includeTopLevelPorts, boolean createSourceToSourceEdges) {
    	this(c, includeTopLevelPorts, createSourceToSourceEdges, false);
    }
    
    /**
     * Construct a new EdifPortRefGroupGraph object from an EdifCell.
     * 
     * @param c
     */
    public EdifPortRefGroupGraph(EdifCell c, boolean includeTopLevelPorts, boolean createSourceToSourceEdges,
    		boolean splitNodes) {
        super(c.getSubCellList().size());
        _cell = c;
        _sourceToSourceEdges = new ArrayList<EdifPortRefGroupEdge>();
        _eprToGroupMap = new HashMap<EdifPortRef, List<EdifPortRefGroupNode>>();
        _init(includeTopLevelPorts, createSourceToSourceEdges, splitNodes);
        _hasSplitNodes = splitNodes;
    }
	
	@Override
	public EdifCell getCell() {
		return _cell;
	}
	
    /**
     * @see edu.byu.ece.graph.BasicGraph#getEdges()
     */
    @Override
    public Collection<EdifPortRefGroupEdge> getEdges() {
        return (Collection<EdifPortRefGroupEdge>) super.getEdges();
    }
    
    /**
     * Obtain a Collection of EdifPortRef objects that are connected to the
     * input ports of this instance. If there are no connections, return a
     * non-null empty Collection.
     * <p>
     * Note: this method is not part of the DirectedGraph interface.
     */
    public Collection<EdifPortRef> getEdifPortRefs(EdifPort port) {
        // Get all links in which port is the sink of the link.
        Collection<Edge> edges = getInputEdges(port);
        // return a collection of EPRs that are the "sources" to
        // this "sink" instance.
        return _createEPRCollection(edges, false);
    }
    
    /**
     * Obtain a Collection of EdifPortRef objects that are connected to the
     * input ports of this instance. If there are no connections, return a
     * non-null empty Collection.
     * <p>
     * Note: this method is not part of the DirectedGraph interface.
     */
    public Collection<EdifPortRef> getEPRsWhichReferenceInputPortsOfECI(EdifCellInstance eci) {

        // Get all links in which eci is the sink of the link.
        Collection edges = getInputEdges(eci);
        // return a collection of EPRs that are the "sources" to
        // this "sink" instance.
        return _createEPRCollection(edges, false);
    }
    
    /**
     * Obtain a collection of EdifPortRef objects that are connected to the true
     * output ports of this instance. If there are no connections, return a
     * non-null empty Collection.
     * <p>
     * Note: this method is not part of the DirectedGraph interface.
     */
    public Collection<EdifPortRef> getEPRsWhichReferenceOutputPortsOfECI(EdifCellInstance eci) {

        // Get all links in which eci is the source of the link.
        Collection edges = getOutputEdges(eci);
        // return a collection of EPRs that are the "sinks" to
        // this "source" instance.
        return _createEPRCollection(edges, true);

    }
    
    /**
     * Obtain the "sink" EdifPortRef objects that are driven by the given
     * EdifCellInstance. The EdifPortRef objects returned are those EPRs that
     * are connected to EdifCellInstance ports (not top-level ports).
     * <p>
     * Note: this method is not part of the DirectedGraph interface.
     */
    public Collection<EdifPortRef> getEPRsWhichReferenceInputPortsOfSinksOfECI(EdifCellInstance eci) {

        // Get all links in which eci is the source of the link.
        Collection edges = getOutputEdges(eci);
        // return a collection of EPRs that are the "sinks" to
        // this "source" instance.
        return _createEPRCollection(edges, false);
    }
    
    /**
     * Obtain the "source" EdifPortRef objects that drive the given
     * EdifCellInstance. These source EPRs may be driven by different ECIs. This
     * method can be used to find the ECIs that drive the given parameter ECI.
     * <p>
     * Note: this method is not part of the DirectedGraph interface.
     */
    public Collection<EdifPortRef> getEPRsWhichReferenceOutputPortsOfSourcesOfECI(EdifCellInstance eci) {

        // Get all links in which eci is the sink of the link.
        Collection edges = getInputEdges(eci);
        // return a collection of EPRs that are the "sources" to
        // this "sink" instance.
        return _createEPRCollection(edges, true);

    }
    
    /**
     * Returns the input Edges which drive the given EdifSingleBitPort.
     * 
     * @param node The node in the graph to examine
     * @param epr The EdifPortRef whose driver edges are desired
     * @return A Collection of Edges in the graph which drive the given node and
     * that are attached to the named SingleBitPort
     */
    public Collection<EdifPortRefGroupEdge> getInputEdges(Object node, EdifPortRef epr) {
        Collection<EdifPortRefGroupEdge> inputEdges = new ArrayList<EdifPortRefGroupEdge>();
        Collection<Edge> allInputEdges = getInputEdges(node);
        for (Edge inputEdge : allInputEdges) {
            // The Edge should be an EdifPortRefGroupEdge
            if (!(inputEdge instanceof EdifPortRefGroupEdge))
                continue;
            EdifPortRef eprToCheck = ((EdifPortRefGroupEdge) inputEdge).getSinkEPR();
            if (epr == eprToCheck) {
                // Add any matches to the Collection
                inputEdges.add((EdifPortRefGroupEdge) inputEdge);
            }
        }

        return inputEdges;
    }
    
    /**
     * Returns the input Edges which drive the given EdifSingleBitPort.
     * 
     * @param node The node in the graph to examine
     * @param singleBitPortName The name of the SingleBitPort whose driver is
     * desired
     * @return A Collection of Edges in the graph which drive the given node and
     * that are attached to the named SingleBitPort
     */
    public Collection<EdifPortRefGroupEdge> getInputEdges(Object node, String singleBitPortName) {
        Collection<EdifPortRefGroupEdge> inputEdges = new ArrayList<EdifPortRefGroupEdge>();
        Collection<Edge> allInputEdges = getInputEdges(node);
        for (Edge inputEdge : allInputEdges) {
            // The Edge should be an EdifPortRefGroupEdge
            if (!(inputEdge instanceof EdifPortRefGroupEdge))
                continue;
            EdifPortRef epr = ((EdifPortRefGroupEdge) inputEdge).getSinkEPR();
            if (epr != null) {
                // Add any matches to the Collection
                if (epr.getSingleBitPort().getPortName().equalsIgnoreCase(singleBitPortName))
                    inputEdges.add((EdifPortRefGroupEdge) inputEdge);
            }
        }

        return inputEdges;
    }
    
    /**
     * Returns the output Edges which are driven by the given EdifSingleBitPort.
     * 
     * @param node The node in the graph to examine
     * @param epr The EdifPortRef whose sink edges are desired
     * @return A Collection of Edges in the graph which are driven by the given
     * node and that are attached to the named SingleBitPort
     */
    public Collection<EdifPortRefGroupEdge> getOutputEdges(Object node, EdifPortRef epr) {
        Collection<EdifPortRefGroupEdge> outputEdges = new ArrayList<EdifPortRefGroupEdge>();
        Collection<Edge> allOutputEdges = getOutputEdges(node);
        for (Edge outputEdge : allOutputEdges) {
            // The Edge should be an EdifPortRefGroupEdge
            if (!(outputEdge instanceof EdifPortRefGroupEdge))
                continue;
            EdifPortRef eprToCheck = ((EdifPortRefGroupEdge) outputEdge).getSourceEPR();
            if (epr == eprToCheck) {
                // Add any matches to the Collection
                outputEdges.add((EdifPortRefGroupEdge) outputEdge);
            }
        }

        return outputEdges;
    }
	
    /**
     * Returns the output Edges which are driven by the given EdifSingleBitPort.
     * 
     * @param node The node in the graph to examine
     * @param singleBitPortName The name of the SingleBitPort whose sink is
     * desired
     * @return A Collection of Edges in the graph which are driven by the given
     * node and that are attached to the named SingleBitPort
     */
    public Collection<EdifPortRefGroupEdge> getOutputEdges(Object node, String singleBitPortName) {
        Collection<EdifPortRefGroupEdge> outputEdges = new ArrayList<EdifPortRefGroupEdge>();
        Collection<Edge> allOutputEdges = getOutputEdges(node);
        for (Edge outputEdge : allOutputEdges) {
            // The Edge should be an EdifPortRefGroupEdge
            if (!(outputEdge instanceof EdifPortRefGroupEdge))
                continue;
            EdifPortRef epr = ((EdifPortRefGroupEdge) outputEdge).getSourceEPR();
            if (epr != null) {
                // Add any matches to the Collection
                if (epr.getSingleBitPort().getPortName().equals(singleBitPortName))
                    outputEdges.add((EdifPortRefGroupEdge) outputEdge);
            }
        }

        return outputEdges;
    }
    
    /**
     * Returns the source nodes which drive the given EdifPort.
     * 
     * @param node The node in the graph to examine
     * @param portName The name of the EdifPort whose drivers are desired
     * @return A Collection of Objects (nodes) in the graph which drive the
     * Edges connected to the given node and that are attached to the named
     * EdifPort
     */
    public Collection getPredecessors(Object node, String portName) {
        Collection predecessors = new ArrayList();
        Collection<Edge> inputEdges = getInputEdges(node);
        for (Edge inputEdge : inputEdges) {
            // The Edge should be an EdifPortRefGroupEdge
            if (!(inputEdge instanceof EdifPortRefGroupEdge))
                continue;
            EdifPortRef epr = ((EdifPortRefGroupEdge) inputEdge).getSinkEPR();
            if (epr != null) {
                // Add any matches to the Collection
                if (epr.getSingleBitPort().getPortName().equalsIgnoreCase(portName))
                    predecessors.add(inputEdge.getSource());
            }
        }

        return predecessors;
    }
    
    /**
     * Returns the source nodes which drive the given EdifPortRef.
     * 
     * @param node The node in the graph to examine
     * @param epr The EdifPortRef whose drivers are desired
     * @return A Collection of Objects (nodes) in the graph which drive the
     * Edges connected to the given node and that are attached to the given
     * EdifPortRef
     */
    public Collection getPredecessors(Object node, EdifPortRef epr) {
        Collection predecessors = new ArrayList();
        Collection<Edge> inputEdges = getInputEdges(node);
        for (Edge inputEdge : inputEdges) {
            // The Edge should be an EdifPortRefGroupEdge
            if (!(inputEdge instanceof EdifPortRefGroupEdge))
                continue;
            EdifPortRef sinkEPR = ((EdifPortRefGroupEdge) inputEdge).getSinkEPR();
            if (sinkEPR != null) {
                // Add any matches to the Collection
                if (sinkEPR.equals(epr))
                    predecessors.add(inputEdge.getSource());
            }
        }

        return predecessors;
    }
    
    public Collection<EdifPortRefGroupEdge> getSourceToSourceEdges() {
        return new ArrayList<EdifPortRefGroupEdge>(_sourceToSourceEdges);
    }
    
    /**
     * Returns the sink nodes driven by the given EdifPort.
     * 
     * @param node The node in the graph to examine
     * @param portName The name of the EdifPort whose sinks are desired
     * @return A Collection of Objects (nodes) in the graph which is driven by
     * the Edges connected to the given node and that are attached to the named
     * EdifPort
     */
    public Collection getSuccessors(Object node, EdifPortRef epr) {
        Collection successors = new ArrayList();
        Collection<Edge> outputEdges = getOutputEdges(node);
        for (Edge outputEdge : outputEdges) {
            // The Edge should be an EdifPortRefGroupEdge
            if (!(outputEdge instanceof EdifPortRefGroupEdge))
                continue;
            EdifPortRef sourceEPR = ((EdifPortRefGroupEdge) outputEdge).getSourceEPR();
            if (sourceEPR != null) {
                // Add any matches to the Collection
                if (sourceEPR.equals(epr))
                    successors.add(outputEdge.getSink());
            }
        }

        return successors;
    }
    
    /**
     * Returns the sink nodes driven by the given EdifPortRef.
     * 
     * @param node The node in the graph to examine
     * @param epr The EdifPortRef whose sinks are desired
     * @return A Collection of Objects (nodes) in the graph which is driven by
     * the Edges connected to the given node and that are attached to the named
     * EdifPort
     */
    public Collection getSuccessors(Object node, String portName) {
        Collection successors = new ArrayList();
        Collection<Edge> outputEdges = getOutputEdges(node);
        for (Edge outputEdge : outputEdges) {
            // The Edge should be an EdifPortRefGroupEdge
            if (!(outputEdge instanceof EdifPortRefGroupEdge))
                continue;
            EdifPortRef epr = ((EdifPortRefGroupEdge) outputEdge).getSourceEPR();
            if (epr != null) {
                // Add any matches to the Collection
                if (epr.getSingleBitPort().getPortName().equals(portName))
                    successors.add(outputEdge.getSink());
            }
        }

        return successors;
    }
    
    /**
	 * Finds and retrieves all of the top-level port nodes in the graph
	 */
	public Collection<EdifSingleBitPort> getTopLevelPortNodes() {
		Collection<EdifSingleBitPort> topLevelPortNodes = new ArrayList<EdifSingleBitPort>();
		for (Object node : getNodes()) {
			if (node instanceof EdifSingleBitPort)
				topLevelPortNodes.add((EdifSingleBitPort) node);
		}
		return topLevelPortNodes;
	}
	
    @Override
    public BasicGraph getSubGraph(Collection nodeCollection) {
        return super.getSubGraph(nodeCollection);
    }
    
    /**
     * Removes all IBUFs, BUFGPs, and OBUFs from an EDIF design/graph. This
     * removal is essential when importing an EDIF file as a EdifCell into a
     * larger design. If these BUFs are not taken out, the Xilinx tools will
     * attempt to connect the ports of the imported EDIF design to pins.
     *
     * TODO: implement this method (?)
     */
    public void removeIOBufs() {

    }
    
    /**
     * Removes all of the top-level port nodes from the graph
     */
    public void removeTopLevelPortNodes() {
        for (Object node : getNodes()) {
            if (node instanceof EdifSingleBitPort)
                this.removeNode(node, true);
        }
    }
    
    /**
     * Removes all of the source-to-source Edges (if any) in the graph. Also
     * removes all references to these Edges in this class.
     */
    public void removeSourceToSourceEdges() {
        if (_sourceToSourceEdges == null)
            return;

        removeEdges(_sourceToSourceEdges);
        _sourceToSourceEdges.clear();
    }
    
    /**
     * This method creates an EDIF file from the graph.
     * 
     * @param filename - the name of the EDIF file created.
     */
    public void toEdif(String filename) {

        try {

            /*
             * Create the EDIF environment, design, library, and top-level cell
             * and cell-instance.
             */
            EdifDesign design = new EdifDesign("ROOT");
            EdifEnvironment env = new EdifEnvironment(_cell.getName());
            EdifLibrary lib = new EdifLibrary(env.getLibraryManager(), _cell.getName() + "_lib");
            EdifCell topCell = new EdifCell(lib, _cell.getName());
            EdifCellInstance topInstance = new EdifCellInstance(topCell.getName(), topCell, topCell);
            design.setTopCellInstance(topInstance);
            env.setTopDesign(design);
            lib.addCell(topInstance.getCellType());

            /*
             * These Collections are used to ensure we don't try to add the same
             * EdifCellInstance, EdifPort, or EdifNet more than once.
             */
            Collection addedNodes = new LinkedHashSet(this.getNodes().size());
            Collection<EdifNet> addedNets = new LinkedHashSet();

            /*
             * Iterate over all the nodes adding EdifCellInstances and
             * EdifPorts.
             */
            for (Object node : this.getNodes()) {

                /*
                 * Add EdifCellInstance
                 */
                if (node instanceof EdifPortRefGroupNode) {
                    EdifCellInstance eci = ((EdifPortRefGroupNode)node).getEdifCellInstance();
                    if (!addedNodes.contains(eci)) {
                        addedNodes.add(eci);
                        topCell.addSubCell(eci);
                        if (!lib.containsCell(eci.getCellType()))
                            lib.addCell(eci.getCellType());
                    }
                }

                /*
                 * Add EdifPort
                 */
                else if (node instanceof EdifSingleBitPort) {
                    EdifSingleBitPort port = (EdifSingleBitPort) node;
                    EdifPort parent = port.getParent();
                    if (!addedNodes.contains(parent)) {
                        addedNodes.add(parent);
                        topCell.addPort(parent.getName(), parent.getWidth(), parent.getDirection());
                    }
                }
            }

            /*
             * Iterate over all the graph edges - adding EdifNets.
             */
            Collection<EdifPortRefGroupEdge> nets = this.getEdges();
            for (EdifPortRefGroupEdge link : nets) {
                EdifNet net = link.getNet();
                if (!addedNets.contains(net)) {
                    addedNets.add(net);
                    topCell.addNet(net);
                }
            }

            /*
             * Write out the EDIF to a file.
             */
            env.toEdif(new EdifPrintWriter(filename));

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
	protected List<EdifPortRefGroupNode> getUnsplitNodesFromECI(EdifCellInstance eci) {
		ArrayList<EdifPortRefGroupNode> nodes = new ArrayList<EdifPortRefGroupNode>();
		
		//Each node has all EPRs for an ECI - a one-to-one relationship
		//between ECIs and nodes.
		EdifPortRefGroupNode node = new EdifPortRefGroupNode(eci);
		node.addAll(eci.getAllEPRs());
		nodes.add(node);
		
		//add all EPR/node relationships to the map of EPRs to nodes
		for(EdifPortRef epr : eci.getAllEPRs()) {
			List<EdifPortRefGroupNode> eprgnList = _eprToGroupMap.get(epr);
			if (eprgnList == null) {
				eprgnList = new ArrayList<EdifPortRefGroupNode>();
			}
			eprgnList.add(node);
			_eprToGroupMap.put(epr, eprgnList);
		}
		
		return nodes;
	}
	
	protected List<EdifPortRefGroupNode> getSplitNodesFromECI(EdifCellInstance eci) {
		ArrayList<EdifPortRefGroupNode> nodes = new ArrayList<EdifPortRefGroupNode>();

		//group EPRs in some way (this is device specific)
		List<Collection<EdifPortRef>> portRefGroups = getEPRSplitGroups(eci);
		
		for (Collection<EdifPortRef> portRefGroup : portRefGroups) {
			EdifPortRefGroupNode node = new EdifPortRefGroupNode(eci);
			node.addAll(portRefGroup);
			nodes.add(node);
			//add all EPR/node relationships to the map of EPRs to nodes
			for(EdifPortRef epr : portRefGroup) {
				List<EdifPortRefGroupNode> eprgnList = _eprToGroupMap.get(epr);
				if (eprgnList == null) {
					eprgnList = new ArrayList<EdifPortRefGroupNode>();
				}
				eprgnList.add(node);
				_eprToGroupMap.put(epr, eprgnList);
			}
		}		
		return nodes;
	}
	
	
	// TODO: The next four methods would be able to split/join nodes after the 
	// graph is created (right now, a boolean in the constructor allows for nodes
	// to be split or not in _init, but it may be useful sometime to do this after
	// the graph is created...
	protected void splitAllEligibleNodes() {
		
	}
	
	protected List<EdifPortRefGroupNode> splitNode(EdifPortRefGroupNode nodeToSplit) {
		return null;
	}
	
	protected void joinAllSplitNodes() {
		
	}

	protected EdifPortRefGroupNode joinSplitNode(List<EdifPortRefGroupNode> nodesToJoin) {
		return null;
	}
	
	/**
     * Initializes the connectivity maps for the given cell. Note that this
     * happens only once and that this does not track changes in the EdifCell.
     * If the cell changes, this connectivity will be stale.
     */
    protected void _init(boolean includeTopLevelPorts, boolean createSourceToSourceEdges, boolean splitNodes) {
        // Add Nodes
    	for (EdifCellInstance eci : _cell.getSubCellList()) {
    		List<EdifPortRefGroupNode> nodes;
    		if(!splitNodes) {
    			nodes = getUnsplitNodesFromECI(eci);
    		}
    		else {
    			nodes = getSplitNodesFromECI(eci);
    		}
    		this.addNodes(nodes);
    	}

        if (includeTopLevelPorts) {
            for (EdifPort p : _cell.getPortList())
                this.addNodes(p.getSingleBitPortList());
        }

        // Iterate over each net in the cell
        for (EdifNet net : _cell.getNetList()) {

            // Find the EPRs that are sources (INOUTs included)
            Collection<EdifPortRef> sourceEPRs = net.getSourcePortRefs(true, true);
            // Find the EPRs that are sources (INOUTs excluded)
            Collection<EdifPortRef> sourceOnlyEPRs = net.getSourcePortRefs(false, true);
            // Find the EPRs that are sinks
            Collection<EdifPortRef> sinkEPRs = net.getSinkPortRefs(true, true);

            // Create a EdifPortRefGroupEdge for each pair
            for (EdifPortRef sourceEPR : sourceEPRs) {
            	List srcNodes = new ArrayList<Object>();
                EdifCellInstance sourceECI = sourceEPR.getCellInstance();
                // Skip if either the source or sink is a Top-Level Port
                //   and we've asked not to include them
                // Otherwise, get the objects for the sink/source
                if (!includeTopLevelPorts && sourceECI == null)
                    continue;
                else if (sourceECI == null) //sink is a top level port
                	srcNodes.add(sourceEPR.getSingleBitPort());
                else {
                	srcNodes = _eprToGroupMap.get(sourceEPR);
                }
                                
                for(Object srcNode : srcNodes) {
	                // Normal Edges: Source to sink
	                for (EdifPortRef sinkEPR : sinkEPRs) {
	                	List sinkNodes = new ArrayList<Object>();
	                	
	                    // Skip if source and sink are same (i.e. tri-state)
	                    if (sinkEPR == sourceEPR)
	                        continue;
	                    EdifCellInstance sinkECI = sinkEPR.getCellInstance();
	
	                    // Skip if either the source or sink is a Top-Level Port
	                    //   and we've asked not to include them
	                    if (!includeTopLevelPorts && sinkECI == null)
	                        continue;
	                    //find the EdifPortRefGroupNode or EdifSingleBitPort for the sink
	                    if (sinkECI == null) { //sink is a top level port
	                    	sinkNodes.add(sinkEPR.getSingleBitPort());
	                    }
	                    else {
	                    	sinkNodes = _eprToGroupMap.get(sinkEPR);
	                    	/*if (sinkNode == null) {
	                    		System.out.println("sink EPR not in map!");
	                    		System.exit(-1);
	                    	}*/
	                    }
	                    for (Object sinkNode : sinkNodes) {
	                    	EdifPortRefGroupEdge edge = new EdifPortRefGroupEdge(sourceEPR, srcNode, sinkEPR, sinkNode);
	                    	this.addEdge(edge);
	                    }
  
	                }
	                
	                // Source to Source Edges. If two Edges drive the same Net, 
	                //   these are Edges between (back and forth) the two (or more)
	                //   sources.
	                // Only do this step if createSourceToSourceEdges is true
	                if (createSourceToSourceEdges) {
	                    // Only look at source-only EPRs. I/O EPRs already have edges.
	                    for (EdifPortRef source2EPR : sourceOnlyEPRs) {
	
	                        // Skip if source and sink are same (i.e. tri-state)
	                        if (source2EPR == sourceEPR)
	                            continue;
	                        EdifCellInstance source2ECI = source2EPR.getCellInstance();
	                        // Skip if either the source or sink is a Top-Level Port
	                        //   and we've asked not to include them
	                        if (!includeTopLevelPorts && source2ECI == null)
	                            continue;
	                        Object src2Node;
	                        if (source2ECI == null) { //sink is a top level port
	                        	src2Node = source2EPR.getSingleBitPort();
	                        }
	                        else {
	                        	src2Node = _eprToGroupMap.get(source2EPR);
	                        }
	                        
	                        EdifPortRefGroupEdge edge = new EdifPortRefGroupEdge(sourceEPR, srcNode, source2EPR, src2Node);
	                        this.addEdge(edge);
	                        // Also add this Edge to the special list of source-to-source Edges
	                        _sourceToSourceEdges.add(edge);
	                    }
	                }
                }
            }
        }
    }
	
    /**
     * Create a Collection of EdifPortRef objects from a Collection of
     * EdifPortRefGroupEdge objects. If keepSource is true, return the
     * EdifPortRef objects that are the "sources" of the links. If false, return
     * the EdifPortRef objects that are the "sinks" of the links.
     * 
     * @param links The Collection of EdifPortRefGroupEdge objects
     * @param keepSource If true, get the sources. If false, get the sinks
     * @return The Collection of EdifPortRef objects
     */
    protected Collection<EdifPortRef> _createEPRCollection(Collection links, boolean keepSource) {
        ArrayList<EdifPortRef> eprs = new ArrayList<EdifPortRef>(links.size());
        for (EdifPortRefGroupEdge link : (Collection<EdifPortRefGroupEdge>) links) {
            if (keepSource)
                eprs.add(link.getSourceEPR());
            else
                eprs.add(link.getSinkEPR());
        }

        return eprs;
    }
    
    //usage: args[0] should be the filepath of the benchmarks
    //subsequent args values are benchmark names
    //all assumed to have the .edf extension
    public static void main(String[] args) {
    	String filePath = args[0];
    	String extension = ".edf";
    	String[] benchmarks = new String[args.length - 1];
    	System.out.println("File path: " + filePath);
    	for (int i=1; i<args.length; i++) {
    		benchmarks[i-1] = args[i];
    	}
    	for (String benchmark : benchmarks) {
    		System.out.println("Benchmark: " + benchmark);
            // 1. Parse the EDIF file and merge any associated black boxes
            System.out.println("Parsing . . .");
            String[] parseAndMergeArgs = {filePath + benchmark + extension};
            EdifCell cell = XilinxMergeParser.parseAndMergeXilinx(parseAndMergeArgs);

            // 2. Flatten the design into a single EDIF cell
            System.out.println("Starting Flattening . . .");
            EdifCell flat_cell = null;
            try {
                flat_cell = new FlattenedEdifCell(cell);
            } catch (EdifNameConflictException e1) {
                e1.toRuntime();
            } catch (InvalidEdifNameException e1) {
                e1.toRuntime();
            }

            // 3. Create the graph data structures         
            System.out.println("Creating Graphs . . .");
            EdifPortRefGroupGraph prgGraph = new EdifPortRefGroupGraph(flat_cell, true, true, true);
            EdifCellInstanceGraph eciGraph = new EdifCellInstanceGraph(flat_cell, true, true);
            
            // 4. Do an SCC depth first search on each graph
            System.out.println("Finding SCCs . . .");
            SCCDepthFirstSearch prgSCC = new SCCDepthFirstSearch(prgGraph);
            SCCDepthFirstSearch eciSCC = new SCCDepthFirstSearch(eciGraph);        

            // 5. Print out the SCCs
            String prgSCCstring = prgSCC.createSCCString();
            String eciSCCstring = eciSCC.createSCCString();
            try {
            	FileWriter prgWriter = new FileWriter(filePath + benchmark + "_prgSCCs.txt");
            	FileWriter eciWriter = new FileWriter(filePath + benchmark + "_eciSCCs.txt");
				prgWriter.write(prgSCCstring);
				eciWriter.write(eciSCCstring);
				prgWriter.close();
				eciWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
            //System.out.println("PRG SCCs: " + prgSCCstring);
            //System.out.println("ECI SCCs: " + eciSCCstring);
            if(prgSCCstring.equals(eciSCCstring))
            	System.out.println("SCCs are identical!");
            else
            	System.out.println("SCCs differ!");
    	}
    }
    
    //TODO: This should be moved to another (Xilinx-specific) class!
    protected List<Collection<EdifPortRef>> getEPRSplitGroups(EdifCellInstance eci) {
    	List<Collection<EdifPortRef>> eprGroups = new ArrayList<Collection<EdifPortRef>>();
    	if(eci.getCellType().getName().equals("LUT6_2")) {
    		eprGroups = splitXilinxLUT6_2(eci);
    		//System.out.println("nodes for LUT6_2");
    	}
    	else {
    		eprGroups.add(eci.getAllEPRs());
    	}
    	return eprGroups;
    }
    
    //TODO: This should be moved to another (Xilinx-specific) class!
    protected List<Collection<EdifPortRef>> splitXilinxLUT6_2(EdifCellInstance eci) {
    	/* TODO: find other don't cares with XilinxInitAttribute(?)
    	 * right now, just makes the most basic assumption
    	 * (O5 never cares about I5, while O6 may care about all inputs)
    	 * 
    	 * Would be something like this:
    	 * 1- get init String from eci
    	 * 2- split init String into the one for O5 and the one for O6
    	 * 3- get don't cares for O5 and O6
    	 * 4- create a group with EPRs for O5 and all O5 "do care" inputs
    	 * (will never include I5), and another with EPRs for O6 and all O6
    	 * "do care" inputs (may include any input.) 
    	 */
    	
    	List<Collection<EdifPortRef>> groups = new ArrayList<Collection<EdifPortRef>>();
    	List<EdifPortRef> o6PortRefs = new ArrayList<EdifPortRef>();
    	List<EdifPortRef> o5PortRefs = new ArrayList<EdifPortRef>();
    	for(EdifPortRef epr : eci.getAllEPRs()) {
    		//first two cases: each group cares only about one output
    		if (!epr.getSingleBitPort().getPortName().equalsIgnoreCase("O5")) {
    			o5PortRefs.add(epr);
    		}
    		else if (!epr.getSingleBitPort().getPortName().equalsIgnoreCase("O6")) {
    			o6PortRefs.add(epr);
    		}
    		//input that's not I5 case: both care about it
    		else if (!epr.getSingleBitPort().getPortName().equalsIgnoreCase("I5")) {
    			o5PortRefs.add(epr);
        		o6PortRefs.add(epr);
    		}
    		else { //I5 input case: only O6 cares about it
        		o6PortRefs.add(epr);
    		}
    	}
		groups.add(o5PortRefs);
		groups.add(o6PortRefs);
    	return groups;
    }
    
    /**
     * The cell corresponding to these connections.
     */
	protected EdifCell _cell;
	
    /**
     * A Collection of the source-to-source Edges in the Graph (Edges between
     * drivers of the same Net)
     */
    protected Collection<EdifPortRefGroupEdge> _sourceToSourceEdges;
    
    /**
     * Maps all EdifPortRef objects that don't hook up to a top level
     * port to the EdifPortRefGroupNode(s) to which it belongs 
     * (essentially a cache to reduce searches)
     */
    protected Map<EdifPortRef, List<EdifPortRefGroupNode>> _eprToGroupMap;
    
    /**
     * Maps all EdifCellInstances to one or more EdifPortRefGroupNodes.
     * This helps us keep track of which ECIs have been split into multiple
     * nodes and can be merged later.
     * 
     * TODO: determine if we need this! Since I haven't implemented joining
     * code yet, I don't create the structure or use it anywhere yet.
     */
    protected Map<EdifCellInstance, List<EdifPortRefGroupNode>> _eciToGroupMap;
    
    /**
     * Denotes whether this graph contains split nodes or if there is a
     * one-to-one relationship between nodes and EdifCellInstances.
     */
    protected boolean _hasSplitNodes;
}
