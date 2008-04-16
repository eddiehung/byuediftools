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
 * Logic implementation of a RAM64X2D
 *
 *  @author Nathan Rollins
 *  @version $Id$
 */

public class RAM64X2D_logic extends Logic {
    public static CellInterface cell_interface[] = {
        in("WE", 1),
        in("D0", 1),
        in("D1", 1),
        in("WCLK", 1),
        in("A0", 1),
        in("A1", 1),
        in("A2", 1),
        in("A3", 1),
        in("A4", 1),
        in("A5", 1),
        in("DPRA0", 1),
        in("DPRA1", 1),
        in("DPRA2", 1),
        in("DPRA3", 1),
        in("DPRA4", 1),
        in("DPRA5", 1),
        out("SPO0", 1),
        out("SPO1", 1),
        out("DPO0", 1),
        out("DPO1", 1),
    };

    protected boolean cellInterfaceDeterminesUniqueNetlistStructure() {
        return true;
    }

    public RAM64X2D_logic(Node parent, 
                          Wire we,
                          Wire d0,
                          Wire d1,
                          Wire wclk,
                          Wire a0,
                          Wire a1,
                          Wire a2,
                          Wire a3,
                          Wire a4,
                          Wire a5,
                          Wire dpra0,
                          Wire dpra1,
                          Wire dpra2,
                          Wire dpra3,
                          Wire dpra4,
                          Wire dpra5,
                          Wire spo0,
                          Wire spo1,
                          Wire dpo0,
                          Wire dpo1,
                          int init1,
                          int init2,
                          int init3,
                          int init4) {
        super(parent);
        
        connect("WE", we);
        connect("D0", d0);
        connect("D1", d1);
        connect("WCLK", wclk);
        connect("A0", a0);
        connect("A1", a1);
        connect("A2", a2);
        connect("A3", a3);
        connect("A4", a4);
        connect("A5", a5);
        connect("DPRA0", dpra0);
        connect("DPRA1", dpra1);
        connect("DPRA2", dpra2);
        connect("DPRA3", dpra3);
        connect("DPRA4", dpra4);
        connect("DPRA5", dpra5);
        connect("SPO0", spo0);
        connect("SPO1", spo1);
        connect("DPO0", dpo0);
        connect("DPO1", dpo1);

        int MIN = 32;
        int WIDTH = 64;
        int[] inits = {init1, init2, init3, init4};

        Wire abus = concat(a5, a4, a3, a2, a1, a0);
        Wire dbus = concat(d1, d0);
        Wire dprabus = concat(dpra5, dpra4, dpra3, dpra2, dpra1, dpra0);
        Wire rdec = wire(WIDTH, "rdec");
        Wire wdec = wire(WIDTH, "wdec");
        Wire regout[] = new Wire[2];
        Wire tspo[] = new Wire[2];
        Wire tdpo[] = new Wire[2];

        new decode6x64(this, abus, vcc(), wdec);
        new decode6x64(this, dprabus, vcc(), rdec);

        for (int i = 0; i < 2; i++) {
            regout[i] = wire(WIDTH, "regout_"+i);
            tspo[i] = wire(WIDTH, "tspo_"+i);
            tdpo[i] = wire(WIDTH, "tdpo_"+i);
        }

        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < WIDTH; i++) {
                if ( ( (inits[(j*WIDTH/MIN) + (i/MIN)] >> i%MIN) & 1 ) == 1 ) {
                    new fdpe(this, 
                             wclk, 
                             dbus.gw(j), 
                             and(we, wdec.gw(i)), 
                             gnd(), 
                             regout[j].gw(i));
                }
                else {
                    new fdcpe(this, 
                              wclk, 
                              dbus.gw(j), 
                              and(we, wdec.gw(i)), 
                              gnd(), 
                              gnd(),
                              regout[j].gw(i));
                }
                and_o(regout[j].gw(i), wdec.gw(i), tspo[j].gw(i));
                and_o(regout[j].gw(i), rdec.gw(i), tdpo[j].gw(i));
            }
        }
        or_o(tspo[0], spo0);
        or_o(tspo[1], spo1);
        or_o(tdpo[0], dpo0);
        or_o(tdpo[1], dpo1);
    }
}
