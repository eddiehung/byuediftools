package edu.byu.ece.edif.util.graph;

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
import edu.byu.ece.edif.core.NamedPropertyObject;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.graph.BasicGraph;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * This class is designed to provide a mathematical graph view of an EdifCell
 * similar to that provided by EdifCellInstanceGraph. The main difference is
 * that full connectivity is not assumed between all input ports of an cell and
 * all of its output ports - for example, an EdifCellInstance with two outputs
 * may assume that a subset of inputs are dependences for one output, while a
 * different subset of the inputs are dependences for the other output. 
 * 
 * <p> In order to provide for this new assumption, nodes of this graph
 * (EdifPortRefGroupNode) are an extension of List<EdifPortRef>. In many cases,
 * such as for a 4-input LUT, these nodes may simply contain all input and
 * output EdifPortRefs, but more complex structures may use more than one
 * EdifPortRefGroupNode to represent less simplistic connectivity. 
 * 
 * <p> Unlike in EdifCellInstanceGraph, nodes representing top level ports are also
 * represented as EdifPortRefGroupNode objects (with the EdifCellInstance
 * reference being null and instead, containing a reference to an
 * EdifSingleBitPort). 
 * 
 * <p> This class contains two Maps with EdifCellInstance/EdifSingleBitPort 
 * as keys and EdifPortRefGroupEdge as the value. This is necessary to avoid constant
 * searching for the EdifPortRefGroup to which an EdifPortRef object belongs
 * (wasn't necessary in EdifCellInstanceGraph since the EdifPortRef has a
 * reference to its EdifCellInstance). 
 * 
 * <p>TODO: The removeIOBufs() method is not currently implemented. It 
 * shouldn't be hard to do, but I noticed that there are no
 * references to the corresponding method of EdifCellInstanceGraph,
 * so I wasn't sure if it was necessary. 
 * 
 * @author whowes
 */

public class EdifPortRefGroupGraph extends AbstractEdifGraph {
    /**
     * Construct a new EdifPortRefGroupGraph from an EdifCell. Default to
     * include Top Level Ports in the graph and NOT create source-to-source
     * Edges in the Graph.
     * 
     * @param c
     */
    public EdifPortRefGroupGraph(EdifCell c) {
        this(c, true, false);
    }

    /**
     * Construct a new EdifPortRefGroupGraph from an EdifCell. Default to NOT
     * create source-to-source Edges in the Graph.
     * 
     * @param c
     */
    public EdifPortRefGroupGraph(EdifCell c, boolean includeTopLevelPorts) {
        this(c, includeTopLevelPorts, false);
    }

    /**
     * Construct a new EdifPortRefGroupGraph object from an EdifCell.
     * 
     * @param c
     */
    public EdifPortRefGroupGraph(EdifCell c, boolean includeTopLevelPorts, boolean createSourceToSourceEdges) {
        super(c.getSubCellList().size());
        _cell = c;
        _sourceToSourceEdges = new ArrayList<EdifPortRefGroupEdge>();
        _eciToGroupMap = new HashMap<EdifCellInstance, EdifPortRefGroupNodeWrapper>();
        _esbpToGroupMap = new HashMap<EdifSingleBitPort, EdifPortRefGroupNodeWrapper>();
        _init(includeTopLevelPorts, createSourceToSourceEdges);
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
    public Collection<EdifPortRefGroupEdge> getInputEdges(EdifPortRefGroupNode node, EdifPortRef epr) {
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
    public Collection<EdifPortRefGroupEdge> getInputEdges(EdifPortRefGroupNode node, String singleBitPortName) {
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
    public Collection<EdifPortRefGroupEdge> getOutputEdges(EdifPortRefGroupNode node, EdifPortRef epr) {
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
    public Collection<EdifPortRefGroupEdge> getOutputEdges(EdifPortRefGroupNode node, String singleBitPortName) {
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
     * @return A Collection of EdifPortRefGroupNode (nodes) in the graph which
     * drive the Edges connected to the given node and that are attached to the
     * named EdifPort
     */
    public Collection<EdifPortRefGroupNode> getPredecessors(EdifPortRefGroupNode node, String portName) {
        Collection<EdifPortRefGroupNode> predecessors = new ArrayList<EdifPortRefGroupNode>();
        Collection<EdifPortRefGroupEdge> inputEdges = getInputEdges(node);
        for (EdifPortRefGroupEdge inputEdge : inputEdges) {
            // The Edge should be an EdifPortRefGroupEdge
            EdifPortRef epr = inputEdge.getSinkEPR();
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
     * @return A Collection of EdifPortRefGroupNode (nodes) in the graph which
     * drive the Edges connected to the given node and that are attached to the
     * given EdifPortRef
     */
    public Collection<EdifPortRefGroupNode> getPredecessors(EdifPortRefGroupNode node, EdifPortRef epr) {
        Collection<EdifPortRefGroupNode> predecessors = new ArrayList<EdifPortRefGroupNode>();
        Collection<EdifPortRefGroupEdge> inputEdges = getInputEdges(node);
        for (EdifPortRefGroupEdge inputEdge : inputEdges) {
            EdifPortRef sinkEPR = inputEdge.getSinkEPR();
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
     * @return A Collection of EdifPortRefGroupNode (nodes) in the graph which
     * is driven by the Edges connected to the given node and that are attached
     * to the named EdifPort
     */
    public Collection<EdifPortRefGroupNode> getSuccessors(EdifPortRefGroupNode node, EdifPortRef epr) {
        Collection<EdifPortRefGroupNode> successors = new ArrayList<EdifPortRefGroupNode>();
        Collection<EdifPortRefGroupEdge> outputEdges = getOutputEdges(node);
        for (EdifPortRefGroupEdge outputEdge : outputEdges) {
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
     * @return A Collection of EdifPortRefGroupNode (nodes) in the graph which
     * is driven by the Edges connected to the given node and that are attached
     * to the named EdifPort
     */
    public Collection<EdifPortRefGroupNode> getSuccessors(EdifPortRefGroupNode node, String portName) {
        Collection<EdifPortRefGroupNode> successors = new ArrayList<EdifPortRefGroupNode>();
        Collection<EdifPortRefGroupEdge> outputEdges = getOutputEdges(node);
        for (EdifPortRefGroupEdge outputEdge : outputEdges) {
            EdifPortRef epr = outputEdge.getSourceEPR();
            if (epr != null) {
                // Add any matches to the Collection
                if (epr.getSingleBitPort().getPortName().equals(portName))
                    successors.add(outputEdge.getSink());
            }
        }

        return successors;
    }

    @Override
    public BasicGraph getSubGraph(Collection nodeCollection) {
        return super.getSubGraph(nodeCollection);
    }

    /**
     * Removes all IBUFs, BUFGPs, and OBUFs from an EDIF design/graph. This
     * removal is essential when importing an EDIF file as a EdifCell into a
     * larger design. If these BUFs are not taken out, the Xilinx tools will
     * attempt to connect the ports of the imported EDIF design to pins. TODO:
     * implement this method (?)
     */
    public void removeIOBufs() {

    }

    /**
     * Removes all of the top-level port nodes from the graph
     */
    public void removeTopLevelPortNodes() {
        for (EdifPortRefGroupNode node : (Collection<EdifPortRefGroupNode>) getNodes()) {
            if (node.getEdifCellInstance() == null)
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
     * @param eci
     * @return a List of all nodes that correspond to this EdifCellInstance. If
     * the graph does not contain any split nodes, this List will only contain
     * one node.
     */
    public List<EdifPortRefGroupNode> getInstanceNodes(EdifCellInstance eci) {
        EdifPortRefGroupNodeWrapper nodes = _eciToGroupMap.get(eci);
        List<EdifPortRefGroupNode> nodesList = new ArrayList<EdifPortRefGroupNode>();
        if (nodes.isSingleNode()) {
            nodesList.add(nodes.getNode());
        }
        else { 
            nodesList.addAll(nodes.getNodes());
        }
        return nodesList;
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
            Collection<NamedPropertyObject> addedNodes = new LinkedHashSet<NamedPropertyObject>(this.getNodes().size());
            Collection<EdifNet> addedNets = new LinkedHashSet<EdifNet>();

            /*
             * Iterate over all the nodes adding EdifCellInstances and
             * EdifPorts.
             */
            for (EdifPortRefGroupNode node : (Collection<EdifPortRefGroupNode>) getNodes()) {

                /*
                 * Add EdifCellInstance
                 */
                if (node.getEdifCellInstance() != null) {
                    EdifCellInstance eci = node.getEdifCellInstance();
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
                else {
                    EdifSingleBitPort port = node.getEdifSingleBitPort();
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

    public void splitNode(EdifPortRefGroupNode nodeToSplit, List<List<EdifPortRef>> groupsToSplit) {
        Collection<EdifPortRefGroupEdge> inputEdges = getInputEdges(nodeToSplit);
        Collection<EdifPortRefGroupEdge> outputEdges = getOutputEdges(nodeToSplit);
        HashMap<EdifPortRef, EdifPortRefGroupNode> eprToNewNodeMap = new HashMap<EdifPortRef, EdifPortRefGroupNode>();
        HashMap<List<EdifPortRef>, EdifPortRefGroupNode> eprGroupsToNewNodeMap = new HashMap<List<EdifPortRef>, EdifPortRefGroupNode>();

        // remove the old, split node and all edges that refer to it
        removeNode(nodeToSplit);
        for (Edge e : inputEdges) {
            removeEdge(e);
            // if we have any self-loops, they will show up in both input and
            // output edge collections
            // we only want to handle them once, as input edges
            if (outputEdges.contains(e)) {
                outputEdges.remove(e);
                // System.out.println("Removed output edge (self-loop)");
            }
        }
        for (Edge e : outputEdges) {
            removeEdge(e);
        }

        // create a new node for each requested group of PortRefs
        for (List<EdifPortRef> eprs : groupsToSplit) {
            // create and add the new node
            EdifPortRefGroupNode newNode = new EdifPortRefGroupNode(nodeToSplit.getEdifCellInstance(), true);
            newNode.addAll(eprs);
            addNode(newNode);
            eprGroupsToNewNodeMap.put(eprs, newNode);

            // for each output port ref in the group, map it to the new node
            for (EdifPortRef epr : eprs) {
                boolean hasDriverEPR = false;
                if (epr.isDriverPortRef()) {
                    eprToNewNodeMap.put(epr, newNode);
                    if (hasDriverEPR) {
                        System.out.println("Warning: this group has more than one driver EPR");
                    }
                    hasDriverEPR = true;
                }
            }
        }

        for (List<EdifPortRef> eprs : groupsToSplit) {
            EdifPortRefGroupNode newNode = eprGroupsToNewNodeMap.get(eprs);

            // update the ECI->port ref group map
            EdifPortRefGroupNodeWrapper eciMapWrapper = _eciToGroupMap.get(nodeToSplit.getEdifCellInstance());
            ArrayList<EdifPortRefGroupNode> nodesList = new ArrayList<EdifPortRefGroupNode>();
            if(eciMapWrapper != null) {
                if(eciMapWrapper.isSingleNode())
                    nodesList.add(eciMapWrapper.getNode());
                else
                    nodesList.addAll(eciMapWrapper.getNodes());
            }
            nodesList.remove(nodeToSplit);
            nodesList.add(newNode);
            _eciToGroupMap.put(nodeToSplit.getEdifCellInstance(), new EdifPortRefGroupNodeWrapper(nodesList));
            
            // create and add new input edges
            // this may include former self-loops that are now split into
            // multiple nodes
            for (EdifPortRefGroupEdge inEdge : inputEdges) {
                EdifPortRefGroupNode sourceNode = inEdge.getSource();
                EdifPortRef sourceEPR = inEdge.getSourceEPR();
                if (sourceNode == nodeToSplit) { // found reference to removed
                    // node
                    // the new source is the node containing the driver port ref
                    sourceNode = eprToNewNodeMap.get(sourceEPR);
                }
                for (EdifPortRef epr : eprs) {
                    if (inEdge.getSinkEPR().equals(epr)) {
                        addEdge(new EdifPortRefGroupEdge(sourceEPR, sourceNode, epr, newNode));
                    }
                }
            }
            // create and add new output edges (all self-feedback should be
            // handled already
            // since all former self-edges were handled in the input edge case)
            for (EdifPortRefGroupEdge outEdge : outputEdges) {
                EdifPortRefGroupNode sinkNode = outEdge.getSink();
                EdifPortRef sinkEPR = outEdge.getSinkEPR();
                if (sinkNode == nodeToSplit) {
                    System.out.println("Warning: sink node was removed - this self-loop was not handled properly!");
                }
                for (EdifPortRef epr : eprs) {
                    if (outEdge.getSourceEPR().equals(epr)) {
                        addEdge(new EdifPortRefGroupEdge(epr, newNode, sinkEPR, sinkNode));
                    }
                }
            }
        }
    }

    // TODO: Implement this method
    public void joinNodes(List<EdifPortRefGroupNode> nodesToJoin) {

    }

    /**
     * Initializes the connectivity maps for the given cell. Note that this
     * happens only once and that this does not track changes in the EdifCell.
     * If the cell changes, this connectivity will be stale.
     */
    protected void _init(boolean includeTopLevelPorts, boolean createSourceToSourceEdges) {
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
                EdifPortRefGroupNodeWrapper srcNodeObj;
                EdifPortRefGroupNode srcNodeWrapped;

                EdifCellInstance sourceECI = sourceEPR.getCellInstance();
                // Skip if either the source or sink is a Top-Level Port
                // and we've asked not to include them
                // Otherwise, get the objects for the sink/source

                if (!includeTopLevelPorts && sourceECI == null)
                    continue;
                else {
                    if (sourceECI != null) { // this node corresponds to an
                        // instance
                        srcNodeObj = _eciToGroupMap.get(sourceECI);
                        if (srcNodeObj == null) {
                            srcNodeWrapped = new EdifPortRefGroupNode(sourceECI, false);
                            addNode(srcNodeWrapped);
                            _eciToGroupMap.put(sourceECI, new EdifPortRefGroupNodeWrapper(srcNodeWrapped));
                        } else {
                            srcNodeWrapped = srcNodeObj.getNode();
                        }
                    } else { // this node corresponds to a single-bit port
                        EdifSingleBitPort sourceESBP = sourceEPR.getSingleBitPort();
                        srcNodeObj = _esbpToGroupMap.get(sourceESBP);
                        if (srcNodeObj == null) {
                            srcNodeWrapped = new EdifPortRefGroupNode(sourceESBP, false);
                            addNode(srcNodeWrapped);
                            _esbpToGroupMap.put(sourceESBP, new EdifPortRefGroupNodeWrapper(srcNodeWrapped));
                        } else {
                            srcNodeWrapped = srcNodeObj.getNode();
                        }
                    }
                }

                // Normal Edges: Source to sink
                for (EdifPortRef sinkEPR : sinkEPRs) {
                    EdifPortRefGroupNodeWrapper sinkNodeWrapped;
                    EdifPortRefGroupNode sinkNode;

                    // Skip if source and sink are same (i.e. tri-state)
                    if (sinkEPR == sourceEPR)
                        continue;
                    EdifCellInstance sinkECI = sinkEPR.getCellInstance();

                    // Skip if either the source or sink is a Top-Level Port
                    // and we've asked not to include them
                    if (!includeTopLevelPorts && sinkECI == null)
                        continue;
                    // find the EdifPortRefGroupNode or EdifSingleBitPort for the sink
                    else {
                        if (sinkECI != null) { // this node corresponds to an instance
                            sinkNodeWrapped = _eciToGroupMap.get(sinkECI);
                            if (sinkNodeWrapped == null) {
                                sinkNode = new EdifPortRefGroupNode(sinkECI, false);
                                addNode(sinkNode);
                                _eciToGroupMap.put(sinkECI, new EdifPortRefGroupNodeWrapper(sinkNode));
                            } else {
                                sinkNode = sinkNodeWrapped.getNode();
                            }
                        } else { // this node corresponds to a single-bit port
                            EdifSingleBitPort sinkESBP = sinkEPR.getSingleBitPort();
                            sinkNodeWrapped = _esbpToGroupMap.get(sinkESBP);
                            if (sinkNodeWrapped == null) {
                                sinkNode = new EdifPortRefGroupNode(sinkESBP, false);
                                addNode(sinkNode);
                                _esbpToGroupMap.put(sinkESBP, new EdifPortRefGroupNodeWrapper(sinkNode));
                            } else {
                                sinkNode = sinkNodeWrapped.getNode();
                            }
                        }
                    }

                    EdifPortRefGroupEdge edge = new EdifPortRefGroupEdge(sourceEPR, srcNodeWrapped, sinkEPR, sinkNode);
                    this.addEdge(edge);
                }

                // Source to Source Edges. If two Edges drive the same Net,
                // these are Edges between (back and forth) the two (or more)
                // sources.
                // Only do this step if createSourceToSourceEdges is true
                if (createSourceToSourceEdges) {
                    // Only look at source-only EPRs. I/O EPRs already have
                    // edges.
                    for (EdifPortRef source2EPR : sourceOnlyEPRs) {

                        // Skip if source and sink are same (i.e. tri-state)
                        if (source2EPR == sourceEPR)
                            continue;
                        EdifCellInstance source2ECI = source2EPR.getCellInstance();
                        // Skip if either the source or sink is a Top-Level Port
                        // and we've asked not to include them
                        EdifPortRefGroupNode src2Node;
                        if (!includeTopLevelPorts && source2ECI == null)
                            continue;
                        else {
                            EdifPortRefGroupNodeWrapper source2Obj;
                            if (source2ECI != null) { // this node corresponds
                                // to an instance
                                source2Obj = _eciToGroupMap.get(source2ECI);
                                if (source2Obj == null) {
                                    src2Node = new EdifPortRefGroupNode(source2ECI, false);
                                    addNode(src2Node);
                                    _eciToGroupMap.put(source2ECI, new EdifPortRefGroupNodeWrapper(src2Node));
                                } else {
                                    src2Node = source2Obj.getNode();
                                }
                            } else { // this node corresponds to a single-bit
                                // port
                                EdifSingleBitPort source2ESBP = source2EPR.getSingleBitPort();
                                source2Obj = _esbpToGroupMap.get(source2ESBP);
                                if (source2Obj == null) {
                                    src2Node = new EdifPortRefGroupNode(source2ESBP, false);
                                    addNode(src2Node);
                                    _esbpToGroupMap.put(source2ESBP, new EdifPortRefGroupNodeWrapper(src2Node));
                                } else {
                                    src2Node = source2Obj.getNode();
                                }
                            }
                        }

                        EdifPortRefGroupEdge edge = new EdifPortRefGroupEdge(sourceEPR, srcNodeWrapped, source2EPR, src2Node);
                        this.addEdge(edge);
                        // Also add this Edge to the special list of
                        // source-to-source Edges
                        _sourceToSourceEdges.add(edge);
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

    // usage: args[0] should be the filepath of the benchmarks
    // subsequent args values are benchmark names
    // all assumed to have the .edf extension
    public static void main(String[] args) {
        String filePath = args[0];
        String extension = ".edf";
        String[] benchmarks = new String[args.length - 1];
        System.out.println("File path: " + filePath);
        for (int i = 1; i < args.length; i++) {
            benchmarks[i - 1] = args[i];
        }
        for (String benchmark : benchmarks) {
            System.out.println("Benchmark: " + benchmark);
            // 1. Parse the EDIF file and merge any associated black boxes
            System.out.println("Parsing . . .");
            String[] parseAndMergeArgs = { filePath + benchmark + extension };
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
            long startTime = System.currentTimeMillis();
            EdifCellInstanceGraph eciGraph = new EdifCellInstanceGraph(flat_cell, true, true);
            long afterECI = System.currentTimeMillis();
            System.out.println("ECI graph took: " + (afterECI - startTime) + " ms");
            long beforePRG = System.currentTimeMillis();
            EdifPortRefGroupGraph prgGraph = new EdifPortRefGroupGraph(flat_cell, true, true);
            long afterPRG = System.currentTimeMillis();
            System.out.println("PRG graph took: " + (afterPRG - beforePRG) + " ms.");
        
            // 4. Do an SCC decomposition on each graph
            System.out.println("Computing SCCs . . .");
            SCCDepthFirstSearch eciSCCs = new SCCDepthFirstSearch(eciGraph);
            SCCDepthFirstSearch prgSCCs = new SCCDepthFirstSearch(prgGraph);
            if(eciSCCs.createSCCString().equals(prgSCCs.createSCCString())) {
                System.out.println("Identical SCCs!");
            }
            else {
                System.out.println("SCCs Differ!");
            }           
        }
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
     * Maps all EdifCellInstances to one or more EdifPortRefGroupNodes. The
     * value may be either an EdifPortRefGroupNode or a
     * List<EdifPortRefGroupNode> (this is done to speed up creation of the
     * graph, as creating a new List for each node is very slow - it's only a
     * List if a node has been split)
     */
    protected Map<EdifCellInstance, EdifPortRefGroupNodeWrapper> _eciToGroupMap;

    /**
     * Maps EdifSingleBitPorts to one or more EdifPortRefGroupNodes. The value
     * may be either an EdifPortRefGroupNode or a List<EdifPortRefGroupNode>
     * (this is done to speed up creation of the graph, as creating a new List
     * for each node is very slow - it's only a List if a node has been split)
     */
    protected Map<EdifSingleBitPort, EdifPortRefGroupNodeWrapper> _esbpToGroupMap;
}
