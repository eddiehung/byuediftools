/*
 * Map between node objects in a DirectedGraph and a Collection of Edge objects.
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
package edu.byu.ece.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Represents a Map between node objects within a {@link DirectedGraph} object
 * and a Collection of Edge objects. The node objects can be a class of any type
 * while the edge objects within the Collections must be an Edge object.
 * <p>
 * The Map used between nodes and edges will correspond to either the source
 * nodes of the edge or the sink nodes of the edge. If it uses the source node,
 * this class is "source" mapped. This means that the key of the Map is a source
 * node for all Edge objects in the associated Collection.
 * <p>
 * If it uses the sink node, this class is "sink" mapped. This means that the
 * key of the Map is a sink node for all Edge objects in the associated
 * Collection.
 * <p>
 * The mapping must be determined during construction and cannot be changed.
 * <p>
 * WARNING: If the hash code of a key (node) to this Map changes during run
 * time, the Map will fail to operate correctly. Make sure that the hash code of
 * any node Object <b>cannot</b> change over time.
 */
public class NodeEdgeMap extends LinkedHashMap implements Cloneable {

    /**
     * Construct a new object with an initial capacity.
     * 
     * @param size The initial capacity of the Map
     * @param sourceMapped The mapping mode of the object. true indicates a
     * "source" mapped mapping while false indicates a "sink" mapped mapping.
     */
    public NodeEdgeMap(int size, boolean sourceMapped) {
        super(size);
        _sourceMapped = sourceMapped;
    }

    /**
     * Construct a new object.
     * 
     * @param sourceMapped The mapping mode of the object. true indicates a
     * "source" mapped mapping while false indicates a "sink" mapped mapping.
     */
    public NodeEdgeMap(boolean sourceMapped) {
        super();
        _sourceMapped = sourceMapped;
    }

    /**
     * Construct a new object based on the parameter (copy constructor).
     * 
     * @param map The NodeEdgeMap to copy
     */
    public NodeEdgeMap(NodeEdgeMap map) {
        super(map.size());
        _sourceMapped = map._sourceMapped;
        for (Iterator i = map.keySet().iterator(); i.hasNext();) {
            Object key = i.next();
            Collection c = (Collection) map.get(key);
            HashSet newCollection = new LinkedHashSet(c);
            this.put(key, newCollection);
        }
    }

    /**
     * Clone this object. Overrides {@link Object#clone()}
     */
    @Override
    public Object clone() {
        return new NodeEdgeMap(this);
    }

    /**
     * Add an edge to the Map. This method map this Edge to the source node
     * object if this object is "source" mapped or to the sink node object if
     * this object is "sink" mapped.
     * 
     * @param edge The edge to add.
     */
    public boolean addEdge(Edge edge) {
        Object key = null;
        if (_sourceMapped)
            key = edge.getSource();
        else
            key = edge.getSink();

        Collection c = (Collection) this.get(key);
        if (c == null) {
            c = new LinkedHashSet();
            this.put(key, c);
        }
        return c.add(edge);
    }

    /**
     * Remove the given edge from the Map. Returns a true if the Edge was found
     * in the map and false otherwise.
     * 
     * @param edge The edge to remove
     * @return true if the edge was removed from the Map and false if the edge
     * was not removed from the Map.
     */
    public boolean removeEdge(Edge edge) {

        Object node;
        if (_sourceMapped)
            node = edge.getSource();
        else
            node = edge.getSink();
        Collection edgeCollection = (Collection) get(node);

        // Remove edge from the Collection returned
        // Return false if the edge was not found
        boolean removed = false;
        if (edgeCollection != null)
            removed = edgeCollection.remove(edge);
        else {
            return false;
        }
        put(node, edgeCollection);
        return removed;
    }

    /**
     * Returns all of the Edges of this Map. The returned Collection is
     * guaranteed not to be null.
     * 
     * @return A Collection of the Edges in this map. If the Map is empty, this
     * method will return an empty but non-null Collection.
     */
    public Collection<Edge> getEdges() {
        Collection<Edge> edges = new LinkedHashSet<Edge>();
        for (Object o : this.values()) {
            edges.addAll((Collection) o);
        }
        return edges;
    }

    /**
     * Return all of the Nodes in this Map. This is the same as the keySet of
     * the Map.
     * 
     * @return A Collection of all Nodes in this Map.
     */
    public Collection getNodes() {
        return keySet();
    }

    /**
     * Removes all Edges from the given Map (node Object -> Collection of Edges)
     * that do not attach to any of the node Objects passed in.
     * <p>
     * If the parameter checkSource is true, only the source of each Edge is
     * examined, if false, only the sink is examined.
     */
    public void retainEdgesForNodes(Collection nodes) {

        // Remove all nodes as keys that are not in the collection
        keySet().retainAll(nodes);

        // Iterate over all Map entries
        for (Iterator i = keySet().iterator(); i.hasNext();) {
            Object node = i.next();
            Collection<Edge> edgeCollection = (Collection<Edge>) this.get(node);
            Collection edgesToRemove = new ArrayList();

            // Remove edges from the given Collection of edges
            for (Edge edge : edgeCollection) {

                /*
                 * If this object is sinkMapped, I want to check the *source*
                 * objects associated with each of the nodes in the collection.
                 * If the source of this edge is NOT in the collection, mark the
                 * edge to be removed.
                 */

                if (!_sourceMapped) {
                    if (!nodes.contains(edge.getSource()))
                        edgesToRemove.add(edge);
                }
                /*
                 * If this object is sourceMapped, I want to check the *sink*
                 * objects associated with each of the nodes in the collection.
                 * If the sink of this edge is NOT in the collection, mark the
                 * edge to be removed.
                 */
                else {
                    if (!nodes.contains(edge.getSink()))
                        edgesToRemove.add(edge);
                }
            }
            edgeCollection.removeAll(edgesToRemove);
            //this.put(node,edgeCollection);
        }
    }

    /**
     * Create a new NodeEdgeMap such that the edges are all inverted and the
     * sourceMapped tag is flipped.
     * <p>
     * TODO: get rid of this function?
     * 
     * @return The NodeEdgeMap with all the edges inverted and the opposite
     * sourceMapped property.
     */
    public NodeEdgeMap invert() {
        // Create a new map with inverted _sourceMapped mapping
        NodeEdgeMap invertedMap = new NodeEdgeMap(!_sourceMapped);
        // Invert each Edge and put it in the new Map
        for (Edge edge : getEdges()) {
            invertedMap.addEdge(edge.invert());
        }
        return invertedMap;
    }

    /**
     * Helper function that returns a non-null Collection object that is the
     * "value" of the given object in the map. If the map returns a null, this
     * method will return an empty but non-null Collection.
     * 
     * @param o The given object
     * @return A Collection that is the "value" of the given object in the map.
     * If the map returns a null, this method will return an empty but non-null
     * Collection.
     */
    public Collection getEdges(Object o) {
        Collection links = (Collection) this.get(o);
        if (links != null)
            return new LinkedHashSet(links);
        return new LinkedHashSet(0);
    }

    // TODO
    public void trimToSize() {
        return;

        //        Map<Object, ArrayList> newMap;
        //        for (Iterator i = keySet().iterator(); i.hasNext();) {
        //            Object o = i.next();
        //            Collection c = (Collection) this.get(o);
        //            ArrayList list = new ArrayList(c);
        //            newMap.put(o, list);
        //        }

    }

    protected boolean _sourceMapped;

}
