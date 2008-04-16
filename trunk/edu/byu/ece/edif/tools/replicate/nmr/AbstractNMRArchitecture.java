/*
 * Abstract representation of the NMR architecture data structure.
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
package edu.byu.ece.edif.tools.replicate.nmr;

import java.io.Serializable;

import edu.byu.ece.edif.core.EdifPortRef;

/**
 * Abstract representation of the NMR architecture data structure. It is
 * intended that this data structure be implemented by any and all types of
 * architectures. This class, in addition to the NMRArchitecture interface
 * define the methods which must be implemented by a given architecture. This
 * class also provides functional helper methods to avoid replicating certain
 * code in numerous locations.
 * 
 * @author Mike Wirthlin and Keith Morgan
 */
public abstract class AbstractNMRArchitecture implements NMRArchitecture, Serializable {

    public AbstractNMRArchitecture() {
        _badCutConnections = new BadCutConnections();
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Determines whether the given set of Strings describe a Bad Cut Connection
     * as contained in this object. The EdifPortRef objects may be passed in
     * either source, sink or sink, source order. This method attempts to sort
     * things out. If there are discrepancies (inout ports or non-connected
     * ports), epr1 is assumed to be the source and epr2 is assumed to be the
     * sink.
     * 
     * @param epr1 The first EdifPortRef of the connection to check
     * @param epr2 The second EdifPortRef of the connection to check
     * @return true if this connection is registered as a Bad Cut Connection in
     * this object.
     */
    public boolean isBadCutConnection(EdifPortRef epr1, EdifPortRef epr2) {
        return _badCutConnections.isBadCutConnection(epr1, epr2);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         protected variables               ////

    /**
     * Object which contains descriptions of all Bad Cut Connections for this
     * architecture
     */
    protected BadCutConnections _badCutConnections;
}
