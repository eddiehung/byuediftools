/*
 * Logic implementation of a 4 input MUX; used as a building block for SRL16s.
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
package edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.SRL;

import byucc.jhdl.Logic.Logic;
import byucc.jhdl.base.CellInterface;
import byucc.jhdl.base.Node;
import byucc.jhdl.base.Wire;

/**
 * Logic implementation of a 4 input MUX. Used as a building block for SRL16s.
 * Note that this was needed to eliminate problems caused by using m2_1 Xilinx
 * macros.
 * 
 * @author Nathan Rollins
 * @version $Id$
 */

public class mux4 extends Logic {

    public static CellInterface cell_interface[] = { in("a", 1), in("b", 1), in("c", 1), in("d", 1), in("sel", 2),
            out("o", 1), };

    protected boolean cellInterfaceDeterminesUniqueNetlistStructure() {
        return true;
    }

    public mux4(Node parent, Wire a, Wire b, Wire c, Wire d, Wire sel, Wire o) {
        super(parent);

        connect("a", a);
        connect("b", b);
        connect("c", c);
        connect("d", d);
        connect("sel", sel);
        connect("o", o);

        Wire muxab = wire(1, "muxab");
        Wire muxcd = wire(1, "muxcd");

        mux2(a, b, sel.gw(0), muxab);
        mux2(c, d, sel.gw(0), muxcd);
        mux2(muxab, muxcd, sel.gw(1), o);
    }

    private void mux2(Wire in1, Wire in2, Wire select, Wire out) {

        or_o(and(in1, not(select)), and(in2, select), out);
    }
}
