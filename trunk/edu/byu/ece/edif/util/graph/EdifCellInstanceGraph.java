/*
 * Provides a mathematical graph view of an EdifCell.
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
package edu.byu.ece.edif.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifPrintWriter;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.graph.Edge;

/**
 * Provides a mathematical graph view of an EdifCell. This graph view of the
 * EdifCell includes Node objects for each top-level port (EdifSingleBitPort)
 * and each sub-cell (EdifCellInstance). The edges between these nodes are
 * represented as {@link EdifCellInstanceEdge} objects. These objects represent
 * an edge as a connection between a source EdifPortRef object and a sink
 * EdifPortRef object.
 * <p>
 * This graph structure creates a <i>single</i> node for each EdifCellInstance
 * object and makes the assumption of <i>full connectivity<i> between each of
 * the input ports of a EdifCellInstance and each of the output ports of the
 * instance (i.e. there is a graph "path" from every input to every output).
 * This may be overly conservative and suggest greater connectivity than
 * actually exists. If the lack of connectivity between some input ports and
 * some output ports needs to be represented, a different graph data structure
 * needs to be used. Other suggested data structures that could be created
 * include: - Represent nodes only as {@link EdifPortRef} objects and represent
 * connectivity between cells between edif port ref nodes. Create hierarchy to
 * group these nodes as instances.
 * <p>
 * The major work associated with this class is figuring out the connectivity of
 * the EdifCell object. The EDIF data structure stores connectivity information
 * only within the EdifNet object. Because of this, the Collection of EdifNet
 * objects must be searched completely to determine connectivity between any
 * given EdifCellInstance objects. This search, order N, must be performed every
 * time a connectivity query is made. This class is used to perform this
 * connectivity search once and cache the result within HashMap objects to
 * facilitate more efficient connectivity queries at a later time.
 * <p>
 * The data structures within this class are created by iterating through all of
 * the EdifNet objects. Each EdifPortRef object associated with the EdifNet
 * object is associated with either an EdifCellInstance object (i.e. a
 * connection to a port of an instance) or it is associated with a top-level
 * EdifPort object (i.e. a connection to a top-level port). This connectivity is
 * stored as a Map object between EdifCellInstances (or top level ports) and a
 * Collection of corresponding EdifCellInstanceEdge objects.
 */
public class EdifCellInstanceGraph extends AbstractEdifGraph {

    /**
     * Construct a new connectivity object from a EdifCell. Default to include
     * Top Level Ports in the graph and NOT create source-to-source Edges in the
     * Graph.
     * 
     * @param c
     */
    public EdifCellInstanceGraph(EdifCell c) {
        this(c, true);
    }

    /**
     * Construct a new connectivity object from a EdifCell. Default to NOT
     * create source-to-source Edges in the Graph.
     * 
     * @param c
     */
    public EdifCellInstanceGraph(EdifCell c, boolean includeTopLevelPorts) {
        this(c, includeTopLevelPorts, false);
    }

    /**
     * Construct a new connectivity object from a EdifCell.
     * 
     * @param c
     */
    public EdifCellInstanceGraph(EdifCell c, boolean includeTopLevelPorts, boolean createSourceToSourceEdges) {
        super(c.getSubCellList().size());
        _cell = c;
        _sourceToSourceEdges = new ArrayList<EdifCellInstanceEdge>();
        //_topLevelPortNodes = new LinkedHashSet();
        _init(includeTopLevelPorts, createSourceToSourceEdges);
    }

    /**
     * Create a new connectivity object from an existing connectivity object
     * (i.e. a copy constructor).
     * 
     * @param ecic
     */
    public EdifCellInstanceGraph(EdifCellInstanceGraph ecic) {
        super(ecic);
        _cell = ecic._cell;
        //_topLevelPortNodes = new LinkedHashSet(ecic._topLevelPortNodes);

        //_portSinkMap = _copyCollectionMap(ecic._portSinkMap);
        //_portSourceMap = _copyCollectionMap(ecic._portSourceMap);
        //_nodeSinkMap = _copyCollectionMap(ecic._nodeSinkMap);
        //_nodeSourceMap = _copyCollectionMap(ecic._nodeSourceMap);

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        return new EdifCellInstanceGraph(this);
    }

    /**
     * Return the EdifCell object that this connectivity object was created
     * from.
     * 
     * @see edu.byu.ece.edif.util.graph.AbstractEdifGraph#getCell()
     */
    @Override
    public EdifCell getCell() {
        return _cell;
    }

    /**
     * @see edu.byu.ece.graph.BasicGraph#getEdges()
     */
    @Override
    public Collection<EdifCellInstanceEdge> getEdges() {
        return (Collection<EdifCellInstanceEdge>) super.getEdges();
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
    public Collection<EdifCellInstanceEdge> getInputEdges(Object node, EdifPortRef epr) {
        Collection<EdifCellInstanceEdge> inputEdges = new ArrayList<EdifCellInstanceEdge>();
        Collection<Edge> allInputEdges = getInputEdges(node);
        for (Edge inputEdge : allInputEdges) {
            // The Edge should be an EdifCellInstanceEdge
            if (!(inputEdge instanceof EdifCellInstanceEdge))
                continue;
            EdifPortRef eprToCheck = ((EdifCellInstanceEdge) inputEdge).getSinkEPR();
            if (epr == eprToCheck) {
                // Add any matches to the Collection
                inputEdges.add((EdifCellInstanceEdge) inputEdge);
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
    public Collection<EdifCellInstanceEdge> getInputEdges(Object node, String singleBitPortName) {
        Collection<EdifCellInstanceEdge> inputEdges = new ArrayList<EdifCellInstanceEdge>();
        Collection<Edge> allInputEdges = getInputEdges(node);
        for (Edge inputEdge : allInputEdges) {
            // The Edge should be an EdifCellInstanceEdge
            if (!(inputEdge instanceof EdifCellInstanceEdge))
                continue;
            EdifPortRef epr = ((EdifCellInstanceEdge) inputEdge).getSinkEPR();
            if (epr != null) {
                // Add any matches to the Collection
                if (epr.getSingleBitPort().getPortName().equalsIgnoreCase(singleBitPortName))
                    inputEdges.add((EdifCellInstanceEdge) inputEdge);
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
    public Collection<EdifCellInstanceEdge> getOutputEdges(Object node, EdifPortRef epr) {
        Collection<EdifCellInstanceEdge> outputEdges = new ArrayList<EdifCellInstanceEdge>();
        Collection<Edge> allOutputEdges = getOutputEdges(node);
        for (Edge outputEdge : allOutputEdges) {
            // The Edge should be an EdifCellInstanceEdge
            if (!(outputEdge instanceof EdifCellInstanceEdge))
                continue;
            EdifPortRef eprToCheck = ((EdifCellInstanceEdge) outputEdge).getSourceEPR();
            if (epr == eprToCheck) {
                // Add any matches to the Collection
                outputEdges.add((EdifCellInstanceEdge) outputEdge);
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
    public Collection<EdifCellInstanceEdge> getOutputEdges(Object node, String singleBitPortName) {
        Collection<EdifCellInstanceEdge> outputEdges = new ArrayList<EdifCellInstanceEdge>();
        Collection<Edge> allOutputEdges = getOutputEdges(node);
        for (Edge outputEdge : allOutputEdges) {
            // The Edge should be an EdifCellInstanceEdge
            if (!(outputEdge instanceof EdifCellInstanceEdge))
                continue;
            EdifPortRef epr = ((EdifCellInstanceEdge) outputEdge).getSourceEPR();
            if (epr != null) {
                // Add any matches to the Collection
                if (epr.getSingleBitPort().getPortName().equals(singleBitPortName))
                    outputEdges.add((EdifCellInstanceEdge) outputEdge);
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
            // The Edge should be an EdifCellInstanceEdge
            if (!(inputEdge instanceof EdifCellInstanceEdge))
                continue;
            EdifPortRef epr = ((EdifCellInstanceEdge) inputEdge).getSinkEPR();
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
            // The Edge should be an EdifCellInstanceEdge
            if (!(inputEdge instanceof EdifCellInstanceEdge))
                continue;
            EdifPortRef sinkEPR = ((EdifCellInstanceEdge) inputEdge).getSinkEPR();
            if (sinkEPR != null) {
                // Add any matches to the Collection
                if (sinkEPR.equals(epr))
                    predecessors.add(inputEdge.getSource());
            }
        }

        return predecessors;
    }

    public Collection<EdifCellInstanceEdge> getSourceToSourceEdges() {
        return new ArrayList<EdifCellInstanceEdge>(_sourceToSourceEdges);
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
            // The Edge should be an EdifCellInstanceEdge
            if (!(outputEdge instanceof EdifCellInstanceEdge))
                continue;
            EdifPortRef sourceEPR = ((EdifCellInstanceEdge) outputEdge).getSourceEPR();
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
            // The Edge should be an EdifCellInstanceEdge
            if (!(outputEdge instanceof EdifCellInstanceEdge))
                continue;
            EdifPortRef epr = ((EdifCellInstanceEdge) outputEdge).getSourceEPR();
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
    public EdifCellInstanceGraph getSubGraph(Collection nodeCollection) {
        return (EdifCellInstanceGraph) super.getSubGraph(nodeCollection);
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
     * Initializes the connectivity maps for the given cell. Note that this
     * happens only once and that this does not track changes in the EdifCell.
     * If the cell changes, this connectivity will be stale.
     */
    protected void _init(boolean includeTopLevelPorts, boolean createSourceToSourceEdges) {

        // Add Nodes
        this.addNodes(_cell.getSubCellList());
        if (includeTopLevelPorts) {
            // TODO: create an EdifCell.getSingleBitPortList()
            for (EdifPort p : _cell.getPortList())
                this.addNodes(p.getSingleBitPortList());
        }

        // Iterate over each net in the cell
        for (EdifNet net : _cell.getNetList()) {

            // Find the EPRs that are sources (INOUTs included)
            Collection<EdifPortRef> sourceEPRs = net.getSourcePortRefs(true, true);
            // Find the EPRs that are sources (INOUTs exluded)
            Collection<EdifPortRef> sourceOnlyEPRs = net.getSourcePortRefs(false, true);
            // Find the EPRs that are sinks
            Collection<EdifPortRef> sinkEPRs = net.getSinkPortRefs(true, true);

            // Create a EdifCellInstanceEdge for each pair
            for (EdifPortRef sourceEPR : sourceEPRs) {
                EdifCellInstance sourceECI = sourceEPR.getCellInstance();

                // Skip if either the source or sink is a Top-Level Port
                //   and we've asked not to include them
                if (!includeTopLevelPorts && sourceECI == null)
                    continue;

                // Normal Edges: Source to sink
                for (EdifPortRef sinkEPR : sinkEPRs) {

                    // Skip if source and sink are same (i.e. tri-state)
                    if (sinkEPR == sourceEPR)
                        continue;
                    EdifCellInstance sinkECI = sinkEPR.getCellInstance();

                    // Skip if either the source or sink is a Top-Level Port
                    //   and we've asked not to include them
                    if (!includeTopLevelPorts && sinkECI == null)
                        continue;

                    EdifCellInstanceEdge edge = new EdifCellInstanceEdge(sourceEPR, sinkEPR);
                    this.addEdge(edge);
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

                        EdifCellInstanceEdge edge = new EdifCellInstanceEdge(sourceEPR, source2EPR);
                        this.addEdge(edge);
                        // Also add this Edge to the special list of soure-to-source Edges
                        _sourceToSourceEdges.add(edge);
                    }
                }
            }
        }
        //System.out.println("Done with eci init");
    }

    /**
     * Create a Collection of EdifPortRef objects from a Collection of
     * EdifCellInstanceEdge objects. If keepSource is true, return the
     * EdifPortRef objects that are the "sources" of the links. If false, return
     * the EdifPortRef objects that are the "sinks" of the links.
     * 
     * @param links The Collection of EdifCellInstanceEdge objects
     * @param keepSource If true, get the sources. If false, get the sinks
     * @return The Collection of EdifPortRef objects
     */
    protected Collection<EdifPortRef> _createEPRCollection(Collection links, boolean keepSource) {
        ArrayList<EdifPortRef> eprs = new ArrayList<EdifPortRef>(links.size());
        for (EdifCellInstanceEdge link : (Collection<EdifCellInstanceEdge>) links) {
            if (keepSource)
                eprs.add(link.getSourceEPR());
            else
                eprs.add(link.getSinkEPR());
        }

        return eprs;
    }

    /**
     * The cell corresponding to these connections.
     */
    protected EdifCell _cell;

    /**
     * A Collection of the source-to-source Edges in the Graph (Edges between
     * drivers of the same Net)
     */
    protected Collection<EdifCellInstanceEdge> _sourceToSourceEdges;

    /**
     * A Collection to keep track of the Top Level Port Nodes in this graph
     */
    //protected Collection _topLevelPortNodes;
}
