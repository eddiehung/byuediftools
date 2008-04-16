/*
 * An edge from an EdifPortRef object to an EdifPortRef for one EdifNet object.
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

import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;

/**
 * Represents a mathematical graph edge between a source EdifPortRef object and
 * a sink EdifPortRef object associated with a single EdifNet object.
 * <p>
 * This is different from a EdifNet object in that this object only keeps track
 * of a source sink pair. A single EdifNet object may represent many
 * EdifCellInstanceEdge objects. Specifically, there is one EdifCellInstanceEdge
 * object for each source/sink pair in the net.
 */
public class EdifCellInstanceEdge implements EdifPortRefEdge {

    /**
     * Construct a new link from a given source and sink EdifPortRef object.
     */
    public EdifCellInstanceEdge(EdifPortRef source, EdifPortRef sink) {
        _sourceEPR = source;
        _sinkEPR = sink;
    }

    /**
     * Indiciate if the given object is a EdifSingleBitPort object.
     */
    public static boolean isEdifSingleBitPort(Object obj) {
        if (obj instanceof EdifSingleBitPort)
            return true;
        else
            return false;
    }

    /**
     * Return the source node object assoicated with this link. If the source is
     * a top-level port, this method will return the EdifSingleBitPort
     * associated with this link. If the source is not a top-level port, this
     * method will return the EdifCellInstance associated with the source
     * EdifPortRef object.
     */
    public Object getSource() {
        if (_sourceEPR.isTopLevelPortRef())
            return _sourceEPR.getSingleBitPort();
        return _sourceEPR.getCellInstance();
    }

    /**
     * Return the sink node object associated with this link. If the sink is a
     * top-level port, this method will return the EdifSingleBitPort associated
     * with this link. If the sink is not a top-level port, this method will
     * return the EdifCellInstance associated with the source EdifPortRef
     * object.
     */
    public Object getSink() {
        if (_sinkEPR.isTopLevelPortRef())
            return _sinkEPR.getSingleBitPort();
        return _sinkEPR.getCellInstance();
    }

    /**
     * Return true if source is associated with a top-level port. Return false
     * if source is associated with an EdifCellInstance.
     */
    public boolean isSourceTopLevel() {
        return isEdifSingleBitPort(getSource());
    }

    /**
     * Return true if sink is associated with a top-level port. Return false if
     * sink is associated with an EdifCellInstance.
     */
    public boolean isSinkTopLevel() {
        return isEdifSingleBitPort(getSink());
    }

    /**
     * Return the EdifPortRef object associated with the source of this link.
     */
    public EdifPortRef getSourceEPR() {
        return _sourceEPR;
    }

    /**
     * Return the EdifPortRef object associated with the sink of this link.
     */
    public EdifPortRef getSinkEPR() {
        return _sinkEPR;
    }

    /**
     * Return the EdifNet object associated with this link.
     */
    public EdifNet getNet() {
        return _sinkEPR.getNet();
    }

    public String toString() {
        return (_sourceEPR + "->" + _sinkEPR);
    }

    /**
     * Create a edge that is the inverse of this edge (i.e. the source and sinks
     * are reversed.
     */
    public EdifCellInstanceEdge invert() {
        return new EdifCellInstanceEdge(_sinkEPR, _sourceEPR);
    }

    /**
     * The EdifPortRef object associated with the source of this edge.
     */
    protected EdifPortRef _sourceEPR;

    /**
     * The EdifPortRef object associated with the sink of this edge.
     */
    protected EdifPortRef _sinkEPR;

}
