/*
 * Specifies an interface for architectures supported under the NMR algorithms.
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

import java.util.List;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.tools.replicate.wiring.NetManager;
import edu.byu.ece.edif.tools.replicate.wiring.PortConnection;

/**
 * Specifies an interface for architectures that are supported under the NMR
 * algorithms.
 * 
 * @author Mike Wirthlin and Keith Morgan
 * @since Created on May 20, 2005
 */
public interface NMRArchitecture {

    /**
     * Determines whether the connection between the given EdifPortRef object
     * pair is an invalid place to "cut" the circuit and insert a voter.
     * 
     * @param epr1 The first EdifPortRef in the connection
     * @param epr2 The second EdifPortRef in the connection
     * @return true if a cut at this point is invalid. false if the cut is
     * valid.
     */
    public boolean isBadCutConnection(EdifPortRef epr1, EdifPortRef epr2);

    /**
     * @return true if the given EdifNet is a clock net, false otherwise
     */
    public boolean isClockNet(EdifNet net);
    
    public Organ getDefaultRestoringOrganForReplicationType(Class<? extends ReplicationType> c);
    
    public boolean isAFlipFlop(EdifCell cellType);

    public boolean isBRAM(EdifCell cellType);
    
    public List<PortConnection> prepareForDetectionOutput(List<? extends PortConnection> unpreparedOutput, boolean registerDetection, boolean addObuf, String clockNetName, NetManager netManager);

}
