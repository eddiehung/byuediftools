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
package edu.byu.ece.edif.util.export.jhdl;

import java.util.ArrayList;
import java.util.Iterator;

import byucc.jhdl.Logic.Logic;
import byucc.jhdl.base.Cell;
import byucc.jhdl.base.Wire;
import edu.byu.ece.edif.core.EdifPort;

/**
 * Represents a Black box for the new JHDL circuit being built. TO DO:
 * <ul>
 * <li>Provide methods for addign ports. You need to keep track of the ports
 * you add
 * </ul>
 * 
 * @version $Id$
 */

public class EdifBlackBoxCell extends Logic {

    /**
     * Constructs an EdifBlackBoxCell with the specified parent and name.
     * 
     * @param parent The parent JHDL Cell Object of this object
     * @param name The name of this Object
     */
    public EdifBlackBoxCell(Cell parent, String name) {
        super(parent, name);
    }

    /**
     * Connects the passed-in EdifPort to the passed-in JHDL Wire.
     * 
     * @param port The EdifPort Object to connect to the wire
     * @param wire The JHDL Wire Object to connect to the port
     * @return The created inner wire
     */
    public Wire connect(EdifPort port, Wire wire) {
        String portName = Edi2JHDL.JHDL_ID(port.getName());
        Wire inner_wire = super.connect(portName, wire);
        if (port.getDirection() == EdifPort.OUT || port.getDirection() == EdifPort.INOUT)
            _outputWires.add(inner_wire);
        return inner_wire;
    }

    /**
     * Calls putWires().
     * 
     * @see EdifBlackBoxCell#putWires
     */
    public void clock() {
        putWires();
    }

    /**
     * Calls putWires().
     * 
     * @see EdifBlackBoxCell#putWires
     */
    public void reset() {
        putWires();
    }

    /**
     * Puts 0 on each wire.
     */
    protected void putWires() {
        for (Iterator i = _outputWires.iterator(); i.hasNext();) {
            Wire w = (Wire) i.next();
            w.put(this, 0);
        }
    }

    /** ArrayList of output wires for this Object. * */
    private ArrayList _outputWires = new ArrayList(3);
}
