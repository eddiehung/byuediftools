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
 * Logic implementation of a RAM32X1D
 *
 *  @author Nathan Rollins
 *  @version $Id$
 */

public class RAM32X1D_logic extends Logic {
    public static CellInterface cell_interface[] = {
        in("WE", 1),
        in("D", 1),
        in("WCLK", 1),
        in("A0", 1),
        in("A1", 1),
        in("A2", 1),
        in("A3", 1),
        in("A4", 1),
        in("DPRA0", 1),
        in("DPRA1", 1),
        in("DPRA2", 1),
        in("DPRA3", 1),
        in("DPRA4", 1),
        out("SPO", 1),
        out("DPO", 1),
    };

    protected boolean cellInterfaceDeterminesUniqueNetlistStructure() {
        return true;
    }

    public RAM32X1D_logic(Node parent, 
                          Wire we,
                          Wire d,
                          Wire wclk,
                          Wire a0,
                          Wire a1,
                          Wire a2,
                          Wire a3,
                          Wire a4,
                          Wire dpra0,
                          Wire dpra1,
                          Wire dpra2,
                          Wire dpra3,
                          Wire dpra4,
                          Wire spo,
                          Wire dpo,
                          int init) {
        super(parent);
        
        connect("WE", we);
        connect("D", d);
        connect("WCLK", wclk);
        connect("A0", a0);
        connect("A1", a1);
        connect("A2", a2);
        connect("A3", a3);
        connect("A4", a4);
        connect("DPRA0", dpra0);
        connect("DPRA1", dpra1);
        connect("DPRA2", dpra2);
        connect("DPRA3", dpra3);
        connect("DPRA4", dpra4);
        connect("SPO", spo);
        connect("DPO", dpo);

        int MIN = 32;
        int WIDTH = 32;
        int[] inits = {init};

        Wire abus = concat(a4, a3, a2, a1, a0);
        Wire dprabus = concat(dpra4, dpra3, dpra2, dpra1, dpra0);
        Wire rdec = wire(WIDTH, "rdec");
        Wire wdec = wire(WIDTH, "wdec");
        Wire regout = wire(WIDTH, "regout");
        Wire tspo = wire(WIDTH, "tspo");
        Wire tdpo = wire(WIDTH, "tdpo");

        new decode5x32(this, abus, vcc(), wdec);
        new decode5x32(this, dprabus, vcc(), rdec);

        for (int i = 0; i < WIDTH; i++) {
            if ( ( (inits[i/MIN] >> i%MIN) & 1 ) == 1 ) {
                new fdpe(this, 
                         wclk, 
                         d, 
                         and(we, wdec.gw(i)), 
                         gnd(), 
                         regout.gw(i));
            }
            else {
                new fdcpe(this, 
                          wclk, 
                          d, 
                          and(we, wdec.gw(i)), 
                          gnd(), 
                          gnd(),
                          regout.gw(i));
            }
            and_o(regout.gw(i), wdec.gw(i), tspo.gw(i));
            and_o(regout.gw(i), rdec.gw(i), tdpo.gw(i));
        }
        or_o(tspo, spo);
        or_o(tdpo, dpo);
    }
}
