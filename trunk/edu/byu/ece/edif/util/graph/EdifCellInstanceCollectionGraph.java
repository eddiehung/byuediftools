/*
 * A graph view of an EdifCell where graph nodes are collections of instances.
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.graph.BasicGraph;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.HierarchicalGraph;
import edu.byu.ece.graph.NodeEdgeMap;

/**
 * A graph view of an EdifCell object in which the graph nodes are collections
 * of instances (EdifCellInstanceCollection objects). This class requires an
 * instance of the EdifCellInstanceGraph during construction. This connectivity
 * information is used to determine the edge relationships between grouped
 * nodes.
 * <p>
 * TODO: I think this class should be named "EdifCellGroupConnectivity". The use
 * of "instance" is confusing later on as I can't distinguish it from the
 * EdifCellInstanceGraph.
 */
public class EdifCellInstanceCollectionGraph extends AbstractEdifGraph implements HierarchicalGraph {

    public EdifCellInstanceCollectionGraph(EdifCell cell, EdifCellInstanceGroupings groupings,
            boolean includeTopLevelPorts) {
        this(new EdifCellInstanceGraph(cell), groupings, includeTopLevelPorts);
    }

    /**
     * Default to include Top Level Ports in the graph.
     */
    public EdifCellInstanceCollectionGraph(EdifCellInstanceGraph ecic, EdifCellInstanceGroupings groupings) {
        this(ecic, groupings, true);
    }

    public EdifCellInstanceCollectionGraph(EdifCellInstanceGraph ecic, EdifCellInstanceGroupings groupings,
            boolean includeTopLevelPorts) {
        super(groupings.getNumberGroups());
        _cell = ecic.getCell();
        _ecic = ecic;
        _groupings = groupings;
        _topLevelPortNodes = new LinkedHashSet();
        _init(ecic, includeTopLevelPorts);
    }

    // Copy Constructor
    protected EdifCellInstanceCollectionGraph(EdifCellInstanceCollectionGraph groupConn) {
        super(groupConn);
        _cell = groupConn._cell;
        _topLevelPortNodes = new LinkedHashSet(groupConn._topLevelPortNodes);
        _groupings = new EdifCellInstanceGroupings(groupConn._groupings);
        //_nodeSourceMap = _copyCollectionMap(ecigc._nodeSourceMap);
        //_nodeSinkMap = _copyCollectionMap(ecigc._nodeSinkMap);
        //_portSourceMap = _copyCollectionMap(ecigc._portSourceMap);
        //_portSinkMap = _copyCollectionMap(ecigc._portSinkMap);
    }

    public Object clone() {
        return new EdifCellInstanceCollectionGraph(this);
    }

    /**
     * @param node A node to look for in the inner graph
     * @return true if the given node is found in the inner graph
     */
    public boolean containsInnerNode(Object node) {
        // TODO: should this match the getParentNode method? The two methods
        //   refer to the same thing but access the result in different ways.
        //   (this way is faster)
        return _ecic.containsNode(node);
    }

    public EdifCell getCell() {
        return _cell;
    }

    /**
     * Removes the given node Object from the graph
     * 
     * @param edge The node Object to remove
     * @param removeAllEdges If true, removes ALL Edges that refer to this node,
     * not just the ones mapped from this node (the easy-to-get-to Edges). In
     * other words, true removes ALL references to this node, while false may
     * leave some Edges that refer to this node.
     */
    public void removeNode(Object node, boolean removeAllEdges) {
        // Simply remove the node from each of the edgeMaps
        super.removeNode(node, removeAllEdges);
        // Attempt to remove the node from the top level port
        // It doesn't matter if it isn't a top level port
        _topLevelPortNodes.remove(node);
    }

    public Collection getTopLevelPortNodes() {
        return new ArrayList(_topLevelPortNodes);
    }

    public boolean isNodeTopLevelPort(Object node) {
        if (node instanceof EdifSingleBitPort)
            return true;
        else
            return false;
    }

    public EdifCellInstanceGroupings getGroupings() {
        return _groupings;
    }

    /**
     * @param edge An Edge object in thie HierarchicalGraph
     * @return A Collection of Edge objects corresponding to the given edge's
     * inner edges (those that make up this edge)
     */
    public Collection<EdifCellInstanceEdge> getInnerEdges(Edge edge) {
        if (edge instanceof EdifCellInstanceCollectionLink)
            return ((EdifCellInstanceCollectionLink) edge).getLinks();
        else
            return null; // Not a valid EdifCellInstanceCollectionLink edge
    }

    /**
     * @return The inside graph that this graph is built upon
     */
    public EdifCellInstanceGraph getInnerGraph() {
        return _ecic;
    }

    /**
     * @param node A node in this HierarchicalGraph
     * @return A Collection of nodes from the inner graph that correspond to the
     * given node in this graph
     */
    public Collection getInnerNodes(Object node) {
        if (node instanceof EdifCellInstanceCollection)
            return (EdifCellInstanceCollection) node;
        else
            return null; // Not a node in this graph!
    }

    /**
     * @param node A node in this HierarchicalGraph
     * @return A DirectedGraph object which is the subgraph corresponding to the
     * given node's inner nodes or null if the node passed in isn't of the
     * correct type
     */
    public EdifCellInstanceGraph getNodeGraph(Object node) {
        if (node instanceof EdifCellInstanceCollection)
            return _ecic.getSubGraph((EdifCellInstanceCollection) node);
        else
            return null; // Not a node in this graph!
    }

    /**
     * @param node A node in the inner graph
     * @return The node in this HierarchicalGraph that corresponds to the given
     * inner node or null if not found.
     */
    public Object getParentNode(Object node) {
        // Search for this node in the groupings
        for (EdifCellInstanceCollection group : _groupings.getInstanceGroups()) {
            if (group.contains(node))
                return group;
        }
        return null;
    }

    /**
     * Creates an EdifCellInstanceCollectionGraph object that contains only the
     * information linking the given Collection of EdifCellInstanceCollection
     * objects.
     * 
     * @param ecics The Collection of EdifCellInstanceCollection objects of
     * interest
     * @return A new EdifCellInstanceCollectionGraph object containing only the
     * given ecics
     */
    public BasicGraph getSubGraph(Collection eciColls) {
        // Create copy of this Connectivity object to prune 
        EdifCellInstanceCollectionGraph ecigc = (EdifCellInstanceCollectionGraph) super.getSubGraph(eciColls);

        // Prune all references to unwanted Nodes and Edges from Groupings
        //   (This is a new groupings object and will not modify the original)
        ecigc._groupings.retainGroups(eciColls);

        return ecigc;
    }

    /**
     * Merges the two passed-in groups by adding the contents of group1 into
     * group2. group1 is deleted and group2 remains.
     * 
     * @param group1 The group to remove
     * @param group2 The group to add to
     * @return A reference to the expanded group (group2)
     */
    public EdifCellInstanceCollection mergeGroupIntoGroup(EdifCellInstanceCollection group1,
            EdifCellInstanceCollection group2) {
        // Modify the groupings object
        _groupings.mergeGroupIntoGroup(group1, group2);

        // Keep track of group2's new source and sink nodes
        Collection newSourceNodes = new LinkedHashSet();
        Collection newSinkNodes = new LinkedHashSet();

        // Update the edgeMaps to reflect the changes
        //  - Throw away internal edges
        //  - Modify new external edges to refer to the group

        // Examine group1's input edges
        Collection inputEdges = getInputEdges(group1);
        for (Iterator i = inputEdges.iterator(); i.hasNext();) {
            EdifCellInstanceCollectionLink inputEdge = (EdifCellInstanceCollectionLink) i.next();
            // Was this Edge completely absorbed into group2, or should we 
            //   just change the sink node on the Edge to group2?

            // Remove the Edge from the graph and re-add it if necessary
            removeEdge(inputEdge);
            // This node's new sink group is group2
            inputEdge.setSink(group2);
            Object sourceNode = inputEdge.getSource();
            // If the source of this node is group2, the edge 
            //   is no longer needed. Don't re-add it.
            if (sourceNode != group2) {
                addEdge(inputEdge);
                newSourceNodes.add(sourceNode);
            }
        }
        // Examine group1's output edges
        Collection outputEdges = getOutputEdges(group1);
        for (Iterator i = outputEdges.iterator(); i.hasNext();) {
            EdifCellInstanceCollectionLink outputEdge = (EdifCellInstanceCollectionLink) i.next();
            // Was this Edge completely absorbed into group2, or should we 
            //   just change the source node on the Edge to group2?

            // Remove the Edge from the graph and re-add it if necessary
            removeEdge(outputEdge);
            // This node's new source group is group2
            outputEdge.setSource(group2);
            // If the source of this node is group2, the edge 
            //   is no longer needed. Don't re-add it.
            Object sinkNode = outputEdge.getSink();
            if (sinkNode != group2) {
                addEdge(outputEdge);
                newSinkNodes.add(sinkNode);
            }
        }

        // Remove group1 from this graph, but not Edges from other Mappings
        // We can use the "fast" method because all other Edges have already
        //  been removed.
        removeNode(group1, false); // false means to do a simple removal

        // See if any CollectionLinks need to be merged and merge them
        // (If there are any CollectionLinks with the same source and sink)
        // Only check the source/sink nodes that have possibly changed
        _mergeEdges(group2, newSourceNodes, true);
        _mergeEdges(group2, newSinkNodes, false);

        return group2;
    }

    /**
     * Initializes the connectivity maps for the given cell. Note that this
     * happens only once and that this does not track changes in the EdifCell.
     * If the cell changes, this connectivity will be stale.
     */
    protected void _init(EdifCellInstanceGraph ecic, boolean includeTopLevelPorts) {
        // Add Nodes
        this.addNodes(_groupings.getInstanceGroups());
        if (includeTopLevelPorts) {
            // TODO: create an EdifCell.getSingleBitPortList()
            for (Iterator i = _cell.getPortList().iterator(); i.hasNext();) {
                EdifPort p = (EdifPort) i.next();
                this.addNodes(p.getSingleBitPortList());
            }
        }

        // Double mapping: source node -> sink node -> collection link
        Map nodesToCollectionLink = new LinkedHashMap();

        // Iterate over each Edge in the InstanceConnectivity
        for (Iterator i = ecic.getEdges().iterator(); i.hasNext();) {
            EdifCellInstanceEdge edge = (EdifCellInstanceEdge) i.next();

            // Skip if either the source or sink is a Top-Level Port
            //   and we've asked not to include them
            if (!includeTopLevelPorts && (edge.isSourceTopLevel() || edge.isSinkTopLevel()))
                continue;

            // Grab the Source and Sink Groups/Ports
            Object source = _groupings.getGroup(edge.getSource());
            if (source == null) // Top Level Port
                source = edge.getSourceEPR().getSingleBitPort();
            Object sink = _groupings.getGroup(edge.getSink());
            if (sink == null) // Top Level Port
                sink = edge.getSinkEPR().getSingleBitPort();
            // Don't create a CollectionLink if they are identical
            if (source == sink)
                continue;

            // Create new or add to an existing CollectionEdge
            EdifCellInstanceCollectionLink collEdge = _addEdgeToCollectionLinksMap(edge, source, sink,
                    nodesToCollectionLink, _groupings);
            if (collEdge == null)
                continue;

            // Add the link to the appropriate Maps (source and sink Maps of
            //   groups or ports)
            // Add the link to source map
            if (edge.isSourceTopLevel()) {
                // Top-level port
                _topLevelPortNodes.add(source);
            }
            // add link to sink map
            if (edge.isSinkTopLevel()) {
                // Top-level port
                _topLevelPortNodes.add(sink);
            }

            // Add the new Edge to the graph
            addEdge(collEdge);
        }
    }

    /**
     * A double-mapping to keep track of CollectionLink objects based on their
     * source and sink groups. The mapping is: source group -> map2, map2: sink
     * group -> CollectionLink
     * 
     * @param edge The EdifCellInstanceEdge to add
     * @param nodesToCollectionLink The double Map
     * @param groupings The associated EdifCellInstanceGroupings object
     * @return The EdifCellInstanceCollectionLink object that this edge was
     * added to.
     */
    protected static EdifCellInstanceCollectionLink _addEdgeToCollectionLinksMap(EdifCellInstanceEdge edge,
            Object sourceGroup, Object sinkGroup, Map nodesToCollectionLink, EdifCellInstanceGroupings groupings) {

        // Look up source group, create a new Map entry if none exists
        Map sinkToCollectionLinkMap = (Map) nodesToCollectionLink.get(sourceGroup);
        if (sinkToCollectionLinkMap == null) {
            sinkToCollectionLinkMap = new LinkedHashMap(); // TODO: Set initial size?
            nodesToCollectionLink.put(sourceGroup, sinkToCollectionLinkMap);
        }

        // Look up sink group, create a new CollectionLink entry if none exists
        EdifCellInstanceCollectionLink collLink = (EdifCellInstanceCollectionLink) sinkToCollectionLinkMap
                .get(sinkGroup);
        if (collLink == null) {
            collLink = new EdifCellInstanceCollectionLink(sourceGroup, sinkGroup, groupings);
            sinkToCollectionLinkMap.put(sinkGroup, collLink);
        }
        // Add new Edge to the CollectionLink and return the CollectionLink
        collLink.addLinkNoCheck(edge);

        return collLink;
    }

    /**
     * Examines the input or output Edges (depending on checkSourceEdges) for
     * CollectionLink Edges that should be merged. Only checks for duplicate
     * Edges with the given new nodes as predecessor/successor nodes.
     */
    protected void _mergeEdges(EdifCellInstanceCollection group, Collection newConnectedNodes, boolean checkSourceEdges) {
        // Create a Map to keep track of Edges that correspond to each Node
        NodeEdgeMap nodeMap = new NodeEdgeMap(newConnectedNodes.size(), checkSourceEdges);
        // Fill the map with any input/output edges from the group
        Collection edges;
        if (checkSourceEdges)
            edges = getInputEdges(group);
        else
            edges = getOutputEdges(group);
        for (Iterator i = edges.iterator(); i.hasNext();) {
            Edge edge = (Edge) i.next();
            // Only add the Edge if the source/sink is contained in the newNodes Collection
            if ((checkSourceEdges && newConnectedNodes.contains(edge.getSource()))
                    || (!checkSourceEdges && newConnectedNodes.contains(edge.getSink())))
                nodeMap.addEdge(edge);
        }

        // Now merge any sets of Edges that are bigger than one
        //for (Iterator i=nodeMap.values().iterator(); i.hasNext(); ) {
        for (Iterator i = newConnectedNodes.iterator(); i.hasNext();) {
            Object node = i.next();
            Collection edgeColl = (Collection) nodeMap.get(node);
            if (edgeColl.size() > 1) {
                // Now merge all the edges in this Collection
                if (checkSourceEdges)
                    _mergeEdgesNoCheck(edgeColl, node, group);
                else
                    _mergeEdgesNoCheck(edgeColl, group, node);
            }
        }

    }

    /**
     * Merges the given Collection of EdifCellInstanceCollectionLink objects
     * into one. WARNING: This method assumes that the Edges have the same
     * source and sink. It will not check this assumption.
     */
    protected void _mergeEdgesNoCheck(Collection edges, Object source, Object sink) {
        // Create new EdifCellInstanceCollectionLink
        EdifCellInstanceCollectionLink newEdge = new EdifCellInstanceCollectionLink(source, sink, _groupings);
        // Add the new Edge to the graph
        addEdge(newEdge);
        // Iterate over all the Edges
        for (Iterator i = edges.iterator(); i.hasNext();) {
            EdifCellInstanceCollectionLink oldEdge = (EdifCellInstanceCollectionLink) i.next();
            // - add the internal Edges (EdifCellInstanceEdge objects) to the new Edge
            newEdge.addLinks(oldEdge.getLinks());
            // - remove this Edge from the graph
            removeEdge(oldEdge);
        }
    }

    protected EdifCell _cell;

    protected EdifCellInstanceGraph _ecic;

    protected EdifCellInstanceGroupings _groupings;

    /**
     * A Collection to keep track of the Top Level Port Nodes in this graph
     */
    protected Collection _topLevelPortNodes;

    public static boolean DEBUG = false;

}
