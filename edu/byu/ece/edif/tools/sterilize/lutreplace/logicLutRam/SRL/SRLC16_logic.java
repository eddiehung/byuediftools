/*
 * TODO: Insert class description here.
 *
 *

 * Copyright (c) 2008 Brigham Young University
 *
 * This file is part of the BYU EDIF Tools.
 * 
 * BYU EDIF Tools is free software: you may redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * BYU EDIF Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License is included with the BYU
 * EDIF Tools. It can be found at /edu/byu/edif/doc/gpl2.txt. You may
 * also get a copy of the license at <http://www.gnu.org/licenses/>.
 *
 */
package edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.SRL;
/*

*/

import byucc.jhdl.base.*;
import byucc.jhdl.Logic.*;
import byucc.jhdl.Xilinx.Virtex.*;

/**
 *
 * Logic implementation of an SRLC16
 *
 *  @author Nathan Rollins
 *  @version $Id$
 */

public class SRLC16_logic extends Logic {
    public static CellInterface cell_interface[] = {
        in("D", 1),
        in("CLK", 1),
        in("A0", 1),
        in("A1", 1),
        in("A2", 1),
        in("A3", 1),
        out("Q", 1),
        out("Q15", 1),
    };

    protected boolean cellInterfaceDeterminesUniqueNetlistStructure() {
        return true;
    }

    public SRLC16_logic(Node parent,
                        Wire d,
                        Wire clk,
                        Wire a0,
                        Wire a1,
                        Wire a2,
                        Wire a3,
                        Wire q,
                        Wire q15,
                        int init) {
        super(parent);

        connect("D", d);
        connect("CLK", clk);
        connect("A0", a0);
        connect("A1", a1);
        connect("A2", a2);
        connect("A3", a3);
        connect("Q", q);
        connect("Q15", q15);

        Wire abus = concat(a3, a2, a1, a0);
        Wire prop = wire(4, "prop");
        Wire q15s = wire(4, "q15s");

        new srl4_logic(this, 
                       d, 
                       vcc(),
                       clk,
                       concat(or(abus.gw(1), abus.gw(2), abus.gw(3)),
                              or(abus.gw(0), abus.gw(2), abus.gw(3))),
                       prop.gw(0),
                       q15s.gw(0),
                       (init & 0xF));
        new srl4_logic(this, 
                       q15s.gw(0), 
                       vcc(),
                       clk,
                       concat(or( and(abus.gw(1), abus.gw(2)), abus.gw(3)),
                              or( and(abus.gw(0), abus.gw(2)), abus.gw(3))), 
                       prop.gw(1),
                       q15s.gw(1),
                       ( (init >> 4) & 0xF));
        new srl4_logic(this, 
                       q15s.gw(1),
                       vcc(),
                       clk,
                       concat(and(abus.gw(3), or(abus.gw(1), abus.gw(2))),
                              and(abus.gw(3), or(abus.gw(0), abus.gw(2)))),
                       prop.gw(2),
                       q15s.gw(2),
                       ( (init >> 8) & 0xF));
        new srl4_logic(this, 
                       q15s.gw(2),
                       vcc(),
                       clk,
                       concat(and(abus.gw(3), abus.gw(2), abus.gw(1)),
                              and(abus.gw(3), abus.gw(2), abus.gw(0))),
                       prop.gw(3),
                       q15s.gw(3),
                       ( (init >> 12) & 0xF));
        
        new mux4(this,
                 prop.gw(0),
                 prop.gw(1),
                 prop.gw(2),
                 prop.gw(3),
                 abus.range(3, 2),
                 q);
        buf_o(q15s.gw(3), q15);
    }
}
