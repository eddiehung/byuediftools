/*
 * Keeps track of "Bad Cut" connections.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;

/**
 * This class keeps track of "Bad Cut" connections. The user registers bad cut
 * connections with the add methods and later may look up a connection pair and
 * compare it against the register bad cut connections.
 * <p>
 * A connection is a pair of EdifPortRefs which are connected by a Net. Some
 * connections may not be cut due to architecture restrictions.
 * <p>
 * Connections are represented internally by Strings which provide for fast
 * lookup in the internal HashMap and HashSet Collections since equivalent
 * strings have the same hashCode.
 * 
 * @author Brian Pratt
 */
public class BadCutConnections implements Serializable {

    public BadCutConnections() {
        _badConnectionMap = new LinkedHashMap<String, Set<String>>();
    }

    /**
     * Adds a Bad Cut Connection to this object.
     * 
     * @param sourceCellType The String name of the source EdifCell
     * @param sourcePortName The String name of the source EdifPort
     * @param sinkCellType The String name of the sink EdifCell
     * @param sinkPortName The String name of the sink EdifPort
     */
    public void addBadCutConnection(String sourceCellType, String sourcePortName, String sinkCellType,
            String sinkPortName) {
        // Create the Strings to enter into the Map
        String source = _getEPRName(sourceCellType, sourcePortName);
        String sink = _getEPRName(sinkCellType, sinkPortName);

        // Check if there is an entry for this source yet
        HashSet sinkEPRSet = (HashSet) _badConnectionMap.get(source);
        // If not, create one
        if (sinkEPRSet == null) {
            sinkEPRSet = new LinkedHashSet();
            _badConnectionMap.put(source, sinkEPRSet);
        }
        // Add this sink to the value Set
        sinkEPRSet.add(sink);
    }

    /**
     * Adds a Bad Cut Connection to this object.
     * 
     * @param sourceEPR The source EdifPortRef of the connection
     * @param sinkEPR The sink EdifPortRef of the connection
     */
    public void addBadCutConnection(EdifPortRef sourceEPR, EdifPortRef sinkEPR) {

        // Get the Edif Cell and Port behind each EPR
        EdifCellInstance sourceECI = sourceEPR.getCellInstance();
        EdifCellInstance sinkECI = sinkEPR.getCellInstance();
        if (sourceECI == null || sinkECI == null)
            return;
        EdifCell sourceEC = sourceECI.getCellType();
        EdifCell sinkEC = sinkECI.getCellType();
        if (sourceEC == null || sinkEC == null)
            return;
        EdifSingleBitPort sourceESBP = sourceEPR.getSingleBitPort();
        EdifSingleBitPort sinkESBP = sinkEPR.getSingleBitPort();
        if (sourceESBP == null || sinkESBP == null)
            return;
        EdifPort sourceEP = sourceESBP.getParent();
        EdifPort sinkEP = sinkESBP.getParent();
        if (sourceEP == null || sinkEP == null)
            return;

        String sourceCellType = sourceECI.getType();
        String sourcePortName = sourceEP.getName();
        String sinkCellType = sinkECI.getType();
        String sinkPortName = sinkEP.getName();

        addBadCutConnection(sourceCellType, sourcePortName, sinkCellType, sinkPortName);
    }

    /**
     * Determines whether the given set of Strings describe a Bad Cut Connection
     * as contained in this object.
     * 
     * @param sourceCellType The String name of the source EdifCell
     * @param sourcePortName The String name of the source EdifPort
     * @param sinkCellType The String name of the sink EdifCell
     * @param sinkPortName The String name of the sink EdifPort
     * @return true if this connection is registered as a Bad Cut Connection
     */
    public boolean isBadCutConnection(String sourceCellType, String sourcePortName, String sinkCellType,
            String sinkPortName) {

        // Get the names of the source and sink to look up
        String sourceName = (_getEPRName(sourceCellType, sourcePortName)).toUpperCase();
        String sinkName = (_getEPRName(sinkCellType, sinkPortName)).toUpperCase();

        // Does this source have an entry in the Map?
        HashSet<String> sinkEPRSet = (HashSet<String>) _badConnectionMap.get(sourceName);
        // Check for wildcard set
        HashSet<String> wildcardSet = (HashSet<String>) _badConnectionMap.get(WILDCARD);

        // Is this sink in the sinkEPRSet for this source?
        // If so, this is a known bad cut connection. Return true.
        if ((sinkEPRSet != null && (sinkEPRSet.contains(sinkName) || sinkEPRSet.contains(WILDCARD)))
                || (wildcardSet != null && wildcardSet.contains(sinkName)))
            return true;
        else
            return false;
    }

    /**
     * Determines whether the given set of Strings describe a Bad Cut Connection
     * as contained in this object.
     * <p>
     * The EdifPortRef objects may be passed in either source, sink or sink,
     * source order. This method attempts to sort things out. If there are
     * discrepancies (inout ports or non-connected ports), epr1 is assumed to be
     * the source and epr2 is assumed to be the sink.
     * 
     * @param epr1 The first EdifPortRef of the connection to check
     * @param epr2 The second EdifPortRef of the connection to check
     * @return true if this connection is registered as a Bad Cut Connection in
     * this object.
     */
    public boolean isBadCutConnection(EdifPortRef epr1, EdifPortRef epr2) {
        // Figure out which epr is the source and which is the sink
        // If there are discrepancies (inout ports, non-connected ports), 
        //   assume epr1 to be the source and epr2 to be the sink.
        EdifPortRef sourceEPR, sinkEPR;
        if (epr1.isDriverPortRef()) {
            sourceEPR = epr1;
            sinkEPR = epr2;
        } else if (epr2.isDriverPortRef()) {
            sourceEPR = epr2;
            sinkEPR = epr1;
        } else {
            sourceEPR = epr1;
            sinkEPR = epr2;
        }

        // Get the Edif Cell and Port behind each EPR
        // What should we do with top-level ports?
        //  1. Return false? (all cuts adjacent to top-level ports are okay)
        //  2. Return true? (all ... are bad)
        //  3. Send an empty string through for the cell type and port name
        //     (allow other end of connection to be limited)
        /*
         * Should top-level ports be included in BadCut Connections? We
         * currently assume that they don't in the BadCutGroupings and related
         * classes. -- BHP
         */
        EdifCellInstance sourceECI = sourceEPR.getCellInstance();
        EdifCellInstance sinkECI = sinkEPR.getCellInstance();
        if (sourceECI == null || sinkECI == null)
            return false;
        EdifCell sourceEC = sourceECI.getCellType();
        EdifCell sinkEC = sinkECI.getCellType();
        if (sourceEC == null || sinkEC == null)
            return false;
        EdifSingleBitPort sourceESBP = sourceEPR.getSingleBitPort();
        EdifSingleBitPort sinkESBP = sinkEPR.getSingleBitPort();
        if (sourceESBP == null || sinkESBP == null)
            return false;
        EdifPort sourceEP = sourceESBP.getParent();
        EdifPort sinkEP = sinkESBP.getParent();
        if (sourceEP == null || sinkEP == null)
            return false;

        /*
         * Grab the Strings representing the Cell and Port and look them up in
         * the Map
         */
        String sourceCellType = sourceECI.getType();
        String sourcePortName = sourceEP.getName();
        String sinkCellType = sinkECI.getType();
        String sinkPortName = sinkEP.getName();

        return isBadCutConnection(sourceCellType, sourcePortName, sinkCellType, sinkPortName);
    }

    /**
     * Internal function to keep the lookup values consistent for a given
     * EdifCell, EdifPortRef pair
     * 
     * @return A standardized String to use in the internal Map
     */
    protected String _getEPRName(String cellType, String portName) {
        return cellType + "_" + portName;
    }

    /**
     * A Map that describes a set of bad cut connections. Key: String
     * representing the source EPR (CellType and PortName) Value: Set of Strings
     * representing the sink EPRs for the Key that would cause a bad cut
     * connection
     */
    protected HashMap<String, Set<String>> _badConnectionMap;

    // A String representing any input or output
    public static String WILDCARD = "*_*";
}
