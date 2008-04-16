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
import java.util.Iterator;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.StringTypedValue;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxNMRArchitecture;

public class XilinxTMRArchitecture extends XilinxNMRArchitecture {

    public XilinxTMRArchitecture() {
        super();
    }

    // Javadoc comment inherited.
    public EdifCellInstance createVoter(EdifCell parent, String instanceName, EdifNet inputs[], EdifNet output) {

        // 1. Obtain a reference to the EdifCell voter object
        EdifCell voter = getVoterCell(parent);

        // 2. Create a new instance
        EdifCellInstance voterCellInstance = null;
        try {
            voterCellInstance = new EdifCellInstance(instanceName, parent, voter);
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }
        voterCellInstance.addProperty(new edu.byu.ece.edif.core.Property("init",
        //new StringTypedValue("E8E8"))); // this is for a LUT4
                new StringTypedValue("E8"))); // this is for a LUT3
        try {
            parent.addSubCell(voterCellInstance);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
        //voters.add(voterCellInstance);

        // Hook up inputs to I0, I1, I2 ports
        if (inputs.length != 3)
            throw new EdifRuntimeException("Need 3 nets for voter input");
        for (int i = 0; i < 3; i++) {
            EdifNet inputNet = inputs[i];
            EdifPortRef epr = inputNet.addPortConnection(voterCellInstance, "I" + i);
            if (false)
                System.out.println("Adding voter input " + i + " to net " + inputNet + "(" + epr + ")");
        }
        // Hook up outputs to O port
        output.addPortConnection(voterCellInstance, "O");

        return voterCellInstance;

    }

    /**
     * Return the EdifCell object that is the voter. For now, this is hard coded
     * to the Xilinx LUT3.
     * 
     * @param parent The parent EdifCell object that will instance voters. This
     * parent is used as a starting point when searching for the voter
     * libraries.
     * @return voter EdifCell
     */
    protected EdifCell getVoterCell(EdifCell parent) {
        return getLUT3VoterCell(parent);
    }

    /**
     * Creates a LUT3 implementation of a voter cell if it has not been defined
     * yet. Adds it to the library to which the parent cell belongs. If the
     * voter cell has already been built, a reference to that cell is simply
     * returned.
     * 
     * @param parent The parent cell from which all libraries will be extracted.
     * @return A voter cell implemented as a LUT3.
     */
    protected EdifCell getLUT3VoterCell(EdifCell parent) {

        if (_voterCell != null)
            return _voterCell;

        String votercellname = "LUT3";

        // Step #1 - Get Xilinx primitive
        EdifLibrary xilinxLibrary = edu.byu.ece.edif.arch.xilinx.XilinxLibrary.library;
        //new edu.byu.ece.edif.arch.xilinx.XilinxLibrary();
        EdifCell voter = xilinxLibrary.getCell(votercellname);

        // Step #2 - Search for the Xilinx primitive in library manager
        Collection matchingVoterCells = parent.getLibrary().getLibraryManager().getCells(votercellname);

        // Iterate over all cells to see if the primitive exists
        if (matchingVoterCells != null) {
            // Iterate over the matching cells and see if it exists
            for (Iterator i = matchingVoterCells.iterator(); i.hasNext();) {
                EdifCell ce = (EdifCell) i.next();
                if (ce.equalsInterface(voter)) {
                    // The LUT exists - tag it as the votercell
                    _voterCell = ce;
                    return _voterCell;
                }
            }
        }

        // Step #3 - The primitive does not exist in our library. Add it.
        EdifLibrary lib = parent.getLibrary().getLibraryManager().getFirstPrimitiveLibrary();
        if (lib == null)
            lib = parent.getLibrary();
        //System.out.println("Destination library "+lib.getName());

        try {
            lib.addCell(voter);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
        _voterCell = voter;

        return _voterCell;
    }

}
