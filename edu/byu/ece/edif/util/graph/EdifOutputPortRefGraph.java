/*
 * Provides a graph view of an EdifCell using EdifPortRef objects as nodes.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.byu.ece.edif.arch.xilinx.XilinxMergeParser;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.tools.flatten.NewFlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.NMRGraphUtilities;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.XilinxTMRArchitecture;
import edu.byu.ece.graph.BasicEdge;
import edu.byu.ece.graph.BasicGraph;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.NodeEdgeMap;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * Provides a mathematical graph view of an EdifCell that uses EdifPortRef
 * objects as nodes of the graph. Edges within this graph represent connectivity
 * between ports of the circuit (i.e. EdifCellInstanceEdge objects) or implied
 * connectivity between input ports of cell and its corresponding output ports
 * (i.e. EdifCellInstanceEdge).
 * <p>
 * This is similar to the EdifCellInstanceGraph graph in that this graph
 * represents the connectivity of an EdifCell object. However, the organization
 * of the graph is much different. There are no objects for EdifCell instances
 * and there are implied edges representing connectivity within an
 * EdifCellInstance. EdifCellInstance objects can be obtained by dereferencing
 * the corresponding EdifPortRef object. EdifNet objects are represented as
 * EdifPortRef source Nodes in the graph.
 */
public class EdifOutputPortRefGraph extends AbstractEdifGraph {

    /**
     * Construct a new connectivity object from a EdifCell. Default to include
     * Top Level Ports in the graph and NOT create source-to-source Edges in the
     * Graph.
     * 
     * @param c
     */
    public EdifOutputPortRefGraph(EdifCell c) {
        _cell = c;
        _init(false, false);
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
    public Collection<EdifPortRefEdge> getEdges() {
        return (Collection<EdifPortRefEdge>) super.getEdges();
    }

    /**
     * Creates a sub-graph of the given graph from a sub-set of nodes in the
     * graph. The resulting graph contains a sub-set of nodes and only those
     * edges that connect nodes in the sub-graph.
     */

    public BasicGraph getSubGraph(Collection nodeCollection) {

        // Create copy of this Connectivity object to prune 
        EdifOutputPortRefGraph absGraph = (EdifOutputPortRefGraph) this.clone();

        // Remove unneeded nodes
        absGraph._nodes.retainAll(nodeCollection);

        // Make sure remaining edges (those that connect to other nodes) are
        // removed as well
        absGraph._nodeSourceMap.retainEdgesForNodes(nodeCollection);
        absGraph._nodeSinkMap.retainEdgesForNodes(nodeCollection);

        return absGraph;
    }

    public Object clone() {
        return new EdifOutputPortRefGraph(this);
    }

    public EdifOutputPortRefGraph(EdifOutputPortRefGraph graph) {
        _nodeSinkMap = (NodeEdgeMap) graph._nodeSinkMap.clone();
        _nodeSourceMap = (NodeEdgeMap) graph._nodeSourceMap.clone();
        _nodes = (HashSet) graph._nodes.clone();
    }

    /**
     * Initializes the connectivity maps for the given cell. Note that this
     * happens only once and that this does not track changes in the EdifCell.
     * If the cell changes, this connectivity will be stale.
     */
    protected void _init(boolean createSourceToSourceEdges, boolean createSinkLeafNodes) {

        if (debug)
            System.out.println("Creating object map");
        _portRefMap = new ObjectPortRefMap(_cell);
        if (debug)
            System.out.println("Creating graph");

        // Add Nodes (source EPRs)
        for (EdifNet net : _cell.getNetList()) {
            // Find the EPRs that are sources
            Collection<EdifPortRef> sourceEPRs = net.getSourcePortRefs(true, true);
            for (EdifPortRef epr : sourceEPRs) {
                if (!this.containsNode(epr)) {
                    if (debug)
                        System.out.println("Adding node " + epr);
                    addNode(epr);
                }
            }
        }

        // Iterate over each net in the cell.
        for (EdifNet net : _cell.getNetList()) {

            // Find the EPRs that are sources
            Collection<EdifPortRef> sourceEPRs = net.getSourcePortRefs(true, true);
            // Find the EPRs that are sinks
            Collection<EdifPortRef> sinkEPRs = net.getSinkPortRefs(true, true);

            for (EdifPortRef sourceEPR : sourceEPRs) {
                // Normal Edges: Source to sink
                for (EdifPortRef sinkEPR : sinkEPRs) {

                    // Skip if source and sink are same (i.e. tri-state)
                    if (sinkEPR == sourceEPR)
                        continue;
                    //NetEdge nedge = new NetEdge(sourceEPR, sinkEPR);

                    // The sinks of these nets are attached to EPRs that are not
                    // nodes in the graph (only source EPRs are nodes in the graph).
                    // The next step is to find the "source" EPR node that is
                    // associated with this "sink" EPR.
                    //
                    // Find "source" node associated with this sink
                    // - If this EPR is attached to an instance, find *all* of the
                    //   EPR nodes associated with this instance that
                    //   drive other nets
                    // - If this EPR is a top-level port (i.e. output port)
                    //   add a "dead" node for it
                    if (sinkEPR.isTopLevelPortRef() && createSinkLeafNodes) {
                        // top-level EPR (output port)
                        this.addNode(sinkEPR);
                        Edge nedge = new SourceEPREdge(sourceEPR, sinkEPR);
                        this.addEdge(nedge);
                        // tag this sink node?
                        if (debug)
                            System.out.println("Adding edge to top-level sink" + nedge);
                    } else {
                        // Instance. Find all input EPRs associated with
                        // this node.
                        Collection<EdifPortRef> sourcePortRefs = _portRefMap.getInstanceOutputPortRefs(sinkEPR
                                .getCellInstance());
                        if ((sourcePortRefs == null || sourcePortRefs.size() == 0)) {
                            if (debug)
                                System.out.println("No Source Sink found");
                            if (createSinkLeafNodes) {
                                this.addNode(sinkEPR);
                                Edge nedge = new SourceEPREdge(sourceEPR, sinkEPR);
                                this.addEdge(nedge);
                            }
                        } else {
                            // create edges
                            for (EdifPortRef seprs : sourcePortRefs) {
                                Edge nedge = new SourceEPREdge(sourceEPR, seprs, sinkEPR);
                                if (debug)
                                    System.out.println("Adding edge " + nedge);
                                this.addEdge(nedge);
                            }
                        }
                    }

                }

                // Source to Source Edges. If two Edges drive the same Net, 
                //   these are Edges between (back and forth) the two (or more)
                //   sources.
                // Only do this step if createSourceToSourceEdges is true

                //                 if (createSourceToSourceEdges) {
                //                    // Skip if source and sink are same (i.e. tri-state)
                //                    for (EdifPortRef source2EPR : sourceEPRs) {
                //                        if (source2EPR == sourceEPR)
                //                            continue;
                //                        EdifCellInstance source2ECI = source2EPR.getCellInstance();
                //
                //                        NetEdge edge = new NetEdge(sourceEPR, source2EPR);
                //                        // Also add this Edge to the special list of source-to-source Edges
                //                        this.addEdge(edge);
                //                        _sourceToSourceEdges.add(edge);
                //                    }
                //                }

            }
        }
    }

    protected boolean debug = false;

    /**
     * @param args
     */
    public static void main(String args[]) {

        // 1. Parse the EDIF file and merge any associated black boxes
        System.out.println("Parsing . . .");
        EdifCell cell = XilinxMergeParser.parseAndMergeXilinx(args);

        // 2. Flatten the design into a single EDIF cell
        System.out.println("Starting Flattening . . .");
        EdifCell flat_cell = null;
        try {
            flat_cell = new NewFlattenedEdifCell(cell);
        } catch (EdifNameConflictException e1) {
            e1.toRuntime();
        } catch (InvalidEdifNameException e1) {
            e1.toRuntime();
        }

        // 3. Create the cell connectivity data structure         
        System.out.println("Creating Graph . . .");
        EdifOutputPortRefGraph graph = new EdifOutputPortRefGraph(flat_cell);

        // 6. Create SCC
        System.out.println("SCC Decomposition. . .");
        SCCDepthFirstSearch sccDFS = new SCCDepthFirstSearch(graph);
        sccDFS.printSCCs();

        // 7. SCC Decomposition
        NMRArchitecture tmrArch = new XilinxTMRArchitecture();
        Collection<EdifPortRef> cuts = NMRGraphUtilities.createDecomposeValidCutSetFanout(graph, sccDFS, tmrArch);
        System.out.println(cuts.size() + " cuts to disconnect");

        Collection<EdifPortRef> cuts2 = NMRGraphUtilities.createDecomposeValidCutSetFFFanout(graph, sccDFS, tmrArch);
        System.out.println(cuts2.size() + " cuts to disconnect");
    }

    /**
     * The cell corresponding to these connections.
     */
    protected EdifCell _cell;

    ObjectPortRefMap _portRefMap;

}

/**
 * Provides a mapping between EdifCellInstance and top-level EdifSingleBitPort
 * objects and EdifPortRef objects
 * 
 * @author wirthlin
 */
class ObjectPortRefMap implements Serializable {

    public ObjectPortRefMap(EdifCell cell) {
        _cell = cell;
        _init();
    }

    public Collection<EdifPortRef> getInstanceInputPortRefs(EdifCellInstance eci) {
        return _instanceInputMap.get(eci);
    }

    public Collection<EdifPortRef> getInstanceOutputPortRefs(EdifCellInstance eci) {
        return _instanceOutputMap.get(eci);
    }

    protected void addInstanceEPRMap(Map<EdifCellInstance, Collection<EdifPortRef>> map, EdifPortRef epr) {
        if (epr.isTopLevelPortRef())
            return;
        EdifCellInstance eci = epr.getCellInstance();
        Collection<EdifPortRef> refCollection = map.get(eci);
        if (refCollection == null) {
            refCollection = new ArrayList<EdifPortRef>();
            map.put(eci, refCollection);
        }
        refCollection.add(epr);
    }

    protected void addPortEPRMap(Map<EdifSingleBitPort, Collection<EdifPortRef>> map, EdifPortRef epr) {
        if (!epr.isTopLevelPortRef())
            return;
        EdifSingleBitPort port = epr.getSingleBitPort();
        Collection<EdifPortRef> refCollection = map.get(port);
        if (refCollection == null) {
            refCollection = new ArrayList<EdifPortRef>();
            map.put(port, refCollection);
        }
        refCollection.add(epr);
    }

    protected void addEPRMap(EdifPortRef ref) {
        if (ref.isTopLevelPortRef()) {
            if (ref.getPort().isInOut())
                addPortEPRMap(_portInoutMap, ref);
            if (ref.getPort().isInputOnly())
                addPortEPRMap(_portInputMap, ref);
            if (ref.getPort().isOutputOnly())
                addPortEPRMap(_portOutputMap, ref);
        } else {
            if (ref.getPort().isInOut())
                addInstanceEPRMap(_instanceInoutMap, ref);
            if (ref.getPort().isInputOnly())
                addInstanceEPRMap(_instanceInputMap, ref);
            if (ref.getPort().isOutputOnly())
                addInstanceEPRMap(_instanceOutputMap, ref);
        }
    }

    protected void _init() {
        for (EdifPortRef epr : (Collection<EdifPortRef>) _cell.getPortRefs())
            addEPRMap(epr);
    }

    protected EdifCell _cell;

    protected Map<EdifCellInstance, Collection<EdifPortRef>> _instanceInputMap = new LinkedHashMap<EdifCellInstance, Collection<EdifPortRef>>();

    protected Map<EdifCellInstance, Collection<EdifPortRef>> _instanceOutputMap = new LinkedHashMap<EdifCellInstance, Collection<EdifPortRef>>();

    protected Map<EdifCellInstance, Collection<EdifPortRef>> _instanceInoutMap = new LinkedHashMap<EdifCellInstance, Collection<EdifPortRef>>();

    protected Map<EdifSingleBitPort, Collection<EdifPortRef>> _portInputMap = new LinkedHashMap<EdifSingleBitPort, Collection<EdifPortRef>>();

    protected Map<EdifSingleBitPort, Collection<EdifPortRef>> _portOutputMap = new LinkedHashMap<EdifSingleBitPort, Collection<EdifPortRef>>();

    protected Map<EdifSingleBitPort, Collection<EdifPortRef>> _portInoutMap = new LinkedHashMap<EdifSingleBitPort, Collection<EdifPortRef>>();
}

/**
 * Allows sink object to be different from sink EPR.
 */
class SourceEPREdge extends BasicEdge implements EdifPortRefEdge {

    // This constructor used when sinkEPR is the node sink
    public SourceEPREdge(EdifPortRef sourceEPR, EdifPortRef sinkEPR) {
        this(sourceEPR, sinkEPR, sinkEPR);
    }

    public SourceEPREdge(EdifPortRef sourceEPR, Object sink, EdifPortRef sinkEPR) {
        super(sourceEPR, sink);
        _sinkEPR = sinkEPR;
    }

    public EdifPortRef getSinkEPR() {
        return _sinkEPR;
    }

    public EdifPortRef getSourceEPR() {
        return (EdifPortRef) getSource();
    }

    public EdifNet getNet() {
        return ((EdifPortRef) getSource()).getNet();
    }

    protected EdifPortRef _sinkEPR;
}
