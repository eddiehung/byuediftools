/*
 * TODO: Insert class description here.
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
package edu.byu.ece.edif.tools.replicate.nmr.tmr;

import java.util.Collection;
import java.util.LinkedList;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;

public class PartialTMRDescription {
    public static final long serialVersionUID = 42L;

    public EdifCell cellToTriplicate;

    public Collection<EdifPort> portsToTriplicate;

    public Collection<EdifCellInstance> instancesToTriplicate;

    public Collection<EdifPortRef> portRefsToCut;

    public String toString() {
        return (cellToTriplicate + "\n\n\n" + portsToTriplicate + "\n\n\n" + instancesToTriplicate + "\n\n\n" + portRefsToCut);

        //return portRefsToCut.toString();
    }

    public PartialTMRDescription() {
        cellToTriplicate = null;
        portsToTriplicate = new LinkedList<EdifPort>();
        instancesToTriplicate = new LinkedList<EdifCellInstance>();
        portRefsToCut = new LinkedList<EdifPortRef>();
    }

}
