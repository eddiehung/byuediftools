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
package edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.RAM;
/*

*/

import byucc.jhdl.base.*;
import byucc.jhdl.Logic.*;
import byucc.jhdl.Xilinx.Virtex.*;

/**
 *
 * Logic implementation of a RAM128X1S_1
 *
 *  @author Nathan Rollins
 *  @version $Id$
 */

public class RAM128X1S_1_logic extends Logic {
    public static CellInterface cell_interface[] = {
        in("WE", 1),
        in("D", 1),
        in("WCLK", 1),
        in("A0", 1),
        in("A1", 1),
        in("A2", 1),
        in("A3", 1),
        in("A4", 1),
        in("A5", 1),
        in("A6", 1),
        out("O", 1),
    };

    protected boolean cellInterfaceDeterminesUniqueNetlistStructure() {
        return true;
    }

    public RAM128X1S_1_logic(Node parent, 
                             Wire we,
                             Wire d,
                             Wire wclk,
                             Wire a0,
                             Wire a1,
                             Wire a2,
                             Wire a3,
                             Wire a4,
                             Wire a5,
                             Wire a6,
                             Wire o,
                             int init1,
                             int init2,
                             int init3,
                             int init4) {
        super(parent);

        connect("WE", we);
        connect("D", d);
        connect("WCLK", wclk);
        connect("A0", a0);
        connect("A1", a1);
        connect("A2", a2);
        connect("A3", a3);
        connect("A4", a4);
        connect("A5", a5);
        connect("A6", a6);
        connect("O", o);        

        int MIN = 32;
        int WIDTH = 128;
        int[] inits = {init1, init2, init3, init4};

        Wire abus = concat(a6, a5, a4, a3, a2, a1, a0);
        Wire rdec = wire(WIDTH, "rdec");
        Wire regout = wire(WIDTH, "regout");
        Wire to = wire(WIDTH, "to");

        new decode7x128(this, abus, vcc(), rdec);

        for (int i = 0; i < WIDTH; i++) {
            if ( ( (inits[i/MIN] >> i%MIN) & 1 ) == 1 ) {
                new fdpe_1(this, 
                           wclk, 
                           d, 
                           and(we, rdec.gw(i)), 
                           gnd(), 
                           regout.gw(i));
            }
            else {
                new fdcpe_1(this, 
                            wclk, 
                            d, 
                            and(we, rdec.gw(i)), 
                            gnd(), 
                            gnd(),
                            regout.gw(i));
            }
            and_o(regout.gw(i), rdec.gw(i), to.gw(i));
        }
        or_o(to, o);
    }
}
