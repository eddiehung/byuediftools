/*
 * An EdifLink class used to show the connections between nodes.
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
import java.util.LinkedHashSet;

import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.graph.Edge;

/**
 * An EdifLink class used in EdifCellInstanceCollectionGraph to show the
 * connections between nodes (any object). It is basically a collection of
 * EdifSourceSinkLinks that share the same source and sink. (A collection of its
 * sibling)
 * <p>
 * The source and sink objects could be any object, but will generally be
 * EdifCellInstanceCollection or EdifSingleBitPort objects.
 * 
 * @author Brian Pratt
 */
public class EdifCellInstanceCollectionLink implements Edge {

    public EdifCellInstanceCollectionLink(Object source, Object sink, EdifCellInstanceGroupings groups) {
        _source = source;
        _sink = sink;
        _groups = groups;
        _links = new LinkedHashSet();
    }

    public static boolean isTopLevel(Object obj) {
        if (obj instanceof EdifSingleBitPort)
            return true;
        else
            return false;
    }

    /**
     * Adds an EdifCellInstanceEdge to this CollectionLink
     * <p>
     * WARNING: This method does not enforce that the added EdifCellInstanceEdge
     * has the same source and sink as this collection.
     */
    public void addLinkNoCheck(EdifCellInstanceEdge link) {
        _links.add(link);
    }

    public boolean addLink(EdifCellInstanceEdge link) {
        EdifCellInstanceCollection linkSourceGroup = _groups.getGroup(link.getSource());
        EdifCellInstanceCollection linkSinkGroup = _groups.getGroup(link.getSink());
        if (!_sink.equals(linkSinkGroup) || !_source.equals(linkSourceGroup))
            return false;
        else {
            _links.add(link);
            return true;
        }
    }

    /**
     * Add a Collection of EdifCellInstanceEdge objects. Only adds those links
     * that match this CollectionLink.
     * 
     * @param links A Collection of EdifCellInstanceEdge objects
     * @return false if not *all* links were successfully added
     */
    public boolean addLinks(Collection links) {
        boolean retVal = true;
        Iterator linkIter = _links.iterator();
        while (linkIter.hasNext()) {
            EdifCellInstanceEdge link = (EdifCellInstanceEdge) linkIter.next();
            if (addLink(link) == false)
                retVal = false;
        }
        return retVal;
    }

    public Object getSource() {
        return _source;
    }

    public Object getSink() {
        return _sink;
    }

    public boolean isSourceTopLevel() {
        return isTopLevel(_source);
    }

    public boolean isSinkTopLevel() {
        return isTopLevel(_sink);
    }

    public Collection<EdifCellInstanceEdge> getLinks() {
        return _links;
    }

    /**
     * Returns a Collection of EdifNets corresponding to the underlying
     * connections in this EdifLink.
     */
    public Collection<EdifNet> getNets() {
        Collection<EdifNet> nets = new ArrayList<EdifNet>(_links.size());
        Iterator linkIter = _links.iterator();
        while (linkIter.hasNext()) {
            EdifCellInstanceEdge link = (EdifCellInstanceEdge) linkIter.next();
            nets.add(link.getNet());
        }
        return nets;
    }

    public void setSource(Object node) {
        _source = node;
    }

    public void setSink(Object node) {
        _sink = node;
    }

    /**
     * NOTE: A new EdifCellInstanceCollectionLink object is created as well as
     * new EdifCellInstanceEdge objects for each of the inner edges
     * 
     * @return An inverted version of this EdifCellInstanceCollectionLink
     */
    public EdifCellInstanceCollectionLink invert() {
        // Create inverted Edge
        EdifCellInstanceCollectionLink inverted = new EdifCellInstanceCollectionLink(_sink, _source, _groups);
        // Add inverted Edges
        for (EdifCellInstanceEdge edge : _links) {
            inverted.addLinkNoCheck(edge.invert());
        }

        return inverted;
    }

    public String toString() {
        return (_source + "->" + _sink);
    }

    // A Collection of EdifSourceSinkLinks that make up this EdifLink between
    //  collections. Each EdifLink must have the same source and sink 
    //  InstanceCollections.
    protected Collection<EdifCellInstanceEdge> _links;

    // The source and sink (can be either a top-level port or a group i.e. EdifCellInstanceCollection)
    protected Object _source;

    protected Object _sink;

    // The groupings to which all of the links in _links belong
    protected EdifCellInstanceGroupings _groups;

}
