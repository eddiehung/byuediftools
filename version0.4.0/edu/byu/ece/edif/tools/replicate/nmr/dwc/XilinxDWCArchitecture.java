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
package edu.byu.ece.edif.tools.replicate.nmr.dwc;

import java.util.Collection;

import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxNMRArchitecture;

/**
 * Compare circuit work:
 * <ul>
 * <li> allow either single rail or double rail circuits (provide both examples)
 * <li> Allow the ability to merge two comparators into a single LUT if
 * necessary.
 * <li> Separate the "isBadCutconnection" API from the TMR code and include it
 * as part of the Xilinx library. Make some interface for determining
 * architecture specific cuts and have code for Xilinx.
 * </ul>
 */
public class XilinxDWCArchitecture extends XilinxNMRArchitecture {

    public XilinxDWCArchitecture() {
        super();
    }

    /**
     * Creates an EdifCellInstance object that performs the majority voter
     * function. This method also hooks up the 2 input wires and the output wire
     * that are passed in as parameters.
     * 
     * @param parent The parent EdifCell object in which the voter will be
     * inserted.
     * @param name The instance name for the new voter.
     * @param inputs An array of 2 EdifNet objects. The 2 inputs to the voter
     * should be connected to these nets.
     * @param output An EdifNet object that should be connected to the output of
     * the voter.
     * @return voter object
     */
    public EdifCellInstance createVoter(EdifCell parent, String name, EdifNet[] inputs, EdifNet output) {
        // 1. Obtain a reference to the EdifCell voter object
        EdifCell voter = getVoterCell(parent);

        // 2. Create a new instance
        EdifCellInstance voterCellInstance = null;
        try {
            voterCellInstance = new EdifCellInstance(name, parent, voter);
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }
        //voterCellInstance.addProperty(new byucc.edif.Property("init",
        //new StringTypedValue("6FF6"))); // this is for a LUT4
        //new StringTypedValue("6"))); // this is for a LUT2
        try {
            parent.addSubCell(voterCellInstance);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
        //voters.add(voterCellInstance);

        // Hook up inputs to I0, I1 ports
        if (inputs.length != 2)
            throw new EdifRuntimeException("Need 2 nets for compare input");
        for (int i = 0; i < 2; i++) {
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
     * Creates an EdifCellInstance object that performs the merging of two voter
     * lines function. This method also hooks up the 2 input wires and the
     * output wire that are passed in as parameters.
     * 
     * @param parent The parent EdifCell object in which the voter will be
     * inserted.
     * @param name The instance name for the new merger.
     * @param inputs An array of 2 EdifNet objects. The 2 inputs to the merger
     * should be connected to these nets.
     * @param output An EdifNet object that should be connected to the output of
     * the merger.
     * @return voter merger
     */
    public EdifCellInstance createMerger(EdifCell parent, String name, EdifNet[] inputs, EdifNet output) {
        // 1. Obtain a reference to the EdifCell voter object
        EdifCell merger = getMergerCell(parent);

        // 2. Create a new instance
        EdifCellInstance mergerCellInstance = null;
        try {
            mergerCellInstance = new EdifCellInstance(name, parent, merger);
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }
        //mergerCellInstance.addProperty(new byucc.edif.Property("init",
        //new StringTypedValue("6FF6"))); // this is for a LUT4
        //new StringTypedValue("6"))); // this is for a LUT2
        try {
            parent.addSubCell(mergerCellInstance);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
        //voters.add(voterCellInstance);

        // Hook up inputs to I0, I1 ports
        if (inputs.length != 2)
            throw new EdifRuntimeException("Need 2 nets for compare input");
        for (int i = 0; i < 2; i++) {
            EdifNet inputNet = inputs[i];
            EdifPortRef epr = inputNet.addPortConnection(mergerCellInstance, "I" + i);
            if (false)
                System.out.println("Adding voter input " + i + " to net " + inputNet + "(" + epr + ")");
        }

        // Hook up outputs to O port
        output.addPortConnection(mergerCellInstance, "O");

        return mergerCellInstance;

    }

    /**
     * Creates an EdifCellInstance object that performs the merging of two voter
     * lines function. This method also hooks up a single input wire and the
     * output wire that are passed in as parameters.
     * 
     * @param parent The parent EdifCell object in which the OBUF will be
     * inserted.
     * @param name The instance name for the new merger.
     * @param inputs An EdifNet object. The single input to the OBUF should be
     * connected to these nets.
     * @param output An EdifNet object that should be connected to the output of
     * the OBUF.
     * @return voter OBUF
     */
    public EdifCellInstance createOBUF(EdifCell parent, String name, EdifNet input, EdifNet output) {
        // 1. Obtain a reference to the EdifCell voter object
        EdifCell obuf = getOBUFCell(parent);

        // 2. Create a new instance
        EdifCellInstance obufCellInstance = null;
        try {
            obufCellInstance = new EdifCellInstance(name, parent, obuf);
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }

        try {
            parent.addSubCell(obufCellInstance);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
        //voters.add(voterCellInstance);

        // Hook up input to I port
        EdifNet inputNet = input;
        EdifPortRef epr = inputNet.addPortConnection(obufCellInstance, "I");
        if (false)
            System.out.println("Adding voter input to net " + inputNet + "(" + epr + ")");

        // Hook up outputs to O port
        output.addPortConnection(obufCellInstance, "O");

        return obufCellInstance;

    }

    /**
     * Return the EdifCell object that is the voter. For now, this is hard coded
     * to the Xilinx XOR2 primitive.
     * 
     * @param parent The parent EdifCell object that will instance voters. This
     * parent is used as a starting point when searching for the voter
     * libraries.
     * @return voter EdifCell
     */
    protected EdifCell getVoterCell(EdifCell parent) {
        return getXORVoterCell(parent);
    }

    /**
     * Creates a XOR implementation of a compare cell if it has not been defined
     * yet. Adds it to the library to which the parent cell belongs. If the
     * voter cell has already been built, a reference to that cell is simply
     * returned.
     * 
     * @param parent The parent cell from which all libraries will be extracted.
     * @return A voter cell implemented as a XOR2.
     */
    protected EdifCell getXORVoterCell(EdifCell parent) {

        /*
         * If the compare cell has already been defined, return the associated
         * Edif Cell.
         */
        if (_voterCell != null)
            return _voterCell;

        //String voterCellName = "LUT" + ports; 
        String voterCellName = "XOR2";

        // Step #1 - Get Xilinx primitive
        EdifLibrary xilinxLibrary = XilinxLibrary.library;

        EdifCell compare = xilinxLibrary.getCell(voterCellName);

        // Step #2 - Search for the Xilinx primitive in library manager
        Collection<EdifCell> matchingVoterCells = parent.getLibrary().getLibraryManager().getCells(voterCellName);

        // Iterate over all cells to see if the primitive exists
        if (matchingVoterCells != null) {
            // Iterate over the matching cells and see if it exists
            for (EdifCell ce : matchingVoterCells) {
                if (ce.equalsInterface(compare)) {
                    // The LUT exists - tag it as the voter cell
                    _voterCell = ce;
                    return _voterCell;
                }
            }
        }

        // Step #3 - The primitive does not exist in our library. Add it.
        EdifLibrary lib = parent.getLibrary().getLibraryManager().getFirstPrimitiveLibrary();
        if (lib == null)
            lib = parent.getLibrary();
        //System.out.println("Destination library "+lib.getName()); // debug

        try {
            lib.addCell(compare);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
        _voterCell = compare;

        return _voterCell;
    }

    /**
     * Return the EdifCell object that is the merger. For now, this is hard
     * coded to the Xilinx OR2 primitive.
     * 
     * @param parent The parent EdifCell object that will instance mergers. This
     * parent is used as a starting point when searching for the merger
     * libraries.
     * @return merger EdifCell
     */
    protected EdifCell getMergerCell(EdifCell parent) {
        return getORMergerCell(parent);
    }

    /**
     * Creates a OR2 implementation of a merger cell if it has not been defined
     * yet. Adds it to the library to which the parent cell belongs. If the
     * merger cell has already been built, a reference to that cell is simply
     * returned.
     * 
     * @param parent The parent cell from which all libraries will be extracted.
     * @return A merger cell implemented as a OR2.
     */
    protected EdifCell getORMergerCell(EdifCell parent) {

        /*
         * If the merger cell has already been defined, return the associated
         * Edif Cell.
         */
        if (_mergerCell != null)
            return _mergerCell;

        //String voterCellName = "LUT" + ports; 
        String voterCellName = "OR2";

        // Step #1 - Get Xilinx primitive
        EdifLibrary xilinxLibrary = XilinxLibrary.library;

        EdifCell compare = xilinxLibrary.getCell(voterCellName);

        // Step #2 - Search for the Xilinx primitive in library manager
        Collection<EdifCell> matchingVoterCells = parent.getLibrary().getLibraryManager().getCells(voterCellName);

        // Iterate over all cells to see if the primitive exists
        if (matchingVoterCells != null) {
            // Iterate over the matching cells and see if it exists
            for (EdifCell ce : matchingVoterCells) {
                if (ce.equalsInterface(compare)) {
                    // The AND exists - tag it as the merger cell
                    _mergerCell = ce;
                    return _mergerCell;
                }
            }
        }

        // Step #3 - The primitive does not exist in our library. Add it.
        EdifLibrary lib = parent.getLibrary().getLibraryManager().getFirstPrimitiveLibrary();
        if (lib == null)
            lib = parent.getLibrary();
        //System.out.println("Destination library "+lib.getName()); // debug

        try {
            lib.addCell(compare);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
        _mergerCell = compare;

        return _mergerCell;
    }

    /**
     * Creates a AND2 implementation of a merger cell if it has not been defined
     * yet. Adds it to the library to which the parent cell belongs. If the
     * merger cell has already been built, a reference to that cell is simply
     * returned.
     * 
     * @param parent The parent cell from which all libraries will be extracted.
     * @return A merger cell implemented as a AND2.
     */
    protected EdifCell getOBUFCell(EdifCell parent) {

        /*
         * If the obuf cell has already been defined, return the associated Edif
         * Cell.
         */
        if (_obufCell != null)
            return _obufCell;

        String obufCellName = "OBUF";

        // Step #1 - Get Xilinx primitive
        EdifLibrary xilinxLibrary = XilinxLibrary.library;

        EdifCell buf = xilinxLibrary.getCell(obufCellName);

        // Step #2 - Search for the Xilinx primitive in library manager
        Collection<EdifCell> matchingOBUFCells = parent.getLibrary().getLibraryManager().getCells(obufCellName);

        // Iterate over all cells to see if the primitive exists
        if (matchingOBUFCells != null) {
            // Iterate over the matching cells and see if it exists
            for (EdifCell ce : matchingOBUFCells) {
                if (ce.equalsInterface(buf)) {
                    // The AND exists - tag it as the merger cell
                    _obufCell = ce;
                    return _obufCell;
                }
            }
        }

        // Step #3 - The primitive does not exist in our library. Add it.
        EdifLibrary lib = parent.getLibrary().getLibraryManager().getFirstPrimitiveLibrary();
        if (lib == null)
            lib = parent.getLibrary();
        //System.out.println("Destination library "+lib.getName()); // debug

        try {
            lib.addCell(buf);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
        _obufCell = buf;

        return _obufCell;
    }

    /**
     * The EdifCell object that implements the merging.
     */
    protected EdifCell _mergerCell;

    /**
     * The EdifCell object that is the OBUF.
     */
    protected EdifCell _obufCell;

}
