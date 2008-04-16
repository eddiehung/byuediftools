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
 * Logic implementation of a RAM16X8D
 *
 *  @author Nathan Rollins
 *  @version $Id$
 */

public class RAM16X8D_logic extends Logic {
    public static CellInterface cell_interface[] = {
        in("WE", 1),
        in("D", 8),
        in("WCLK", 1),
        in("A0", 1),
        in("A1", 1),
        in("A2", 1),
        in("A3", 1),
        in("DPRA0", 1),
        in("DPRA1", 1),
        in("DPRA2", 1),
        in("DPRA3", 1),
        out("SPO", 8),
        out("DPO", 8),
    };

    protected boolean cellInterfaceDeterminesUniqueNetlistStructure() {
        return true;
    }

    public RAM16X8D_logic(Node parent, 
                          Wire we,
                          Wire d,
                          Wire wclk,
                          Wire a0,
                          Wire a1,
                          Wire a2,
                          Wire a3,
                          Wire dpra0,
                          Wire dpra1,
                          Wire dpra2,
                          Wire dpra3,
                          Wire spo,
                          Wire dpo,
                          int init1,
                          int init2,
                          int init3,
                          int init4,
                          int init5,
                          int init6,
                          int init7,
                          int init8) {
        super(parent);
        
        connect("WE", we);
        connect("D", d);
        connect("WCLK", wclk);
        connect("A0", a0);
        connect("A1", a1);
        connect("A2", a2);
        connect("A3", a3);
        connect("DPRA0", dpra0);
        connect("DPRA1", dpra1);
        connect("DPRA2", dpra2);
        connect("DPRA3", dpra3);
        connect("SPO", spo);
        connect("DPO", dpo);

        int MIN = 16;
        int WIDTH = 16;
        int[] inits = {init1, init2, init3, init4, init5,
                       init6, init7, init8};

        Wire abus = concat(a3, a2, a1, a0);
        Wire dprabus = concat(dpra3, dpra2, dpra1, dpra0);
        Wire rdec = wire(WIDTH, "rdec");
        Wire wdec = wire(WIDTH, "wdec");
        Wire regout[] = new Wire[8];
        Wire tspo[] = new Wire[8];
        Wire tdpo[] = new Wire[8];

        new decode4x16(this, abus, vcc(), wdec);
        new decode4x16(this, dprabus, vcc(), rdec);

        for (int i = 0; i < 8; i++) {
            regout[i] = wire(WIDTH, "regout_"+i);
            tspo[i] = wire(WIDTH, "tspo_"+i);
            tdpo[i] = wire(WIDTH, "tdpo_"+i);
        }

        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < WIDTH; i++) {
                if ( ( (inits[(j*WIDTH/MIN) + (i/MIN)] >> i%MIN) & 1 ) == 1 ) {
                    new fdpe(this, 
                             wclk, 
                             d.gw(j), 
                             and(we, wdec.gw(i)), 
                             gnd(), 
                             regout[j].gw(i));
                }
                else {
                    new fdcpe(this, 
                              wclk, 
                              d.gw(j),
                              and(we, wdec.gw(i)), 
                              gnd(), 
                              gnd(),
                              regout[j].gw(i));
                }
                and_o(regout[j].gw(i), wdec.gw(i), tspo[j].gw(i));
                and_o(regout[j].gw(i), rdec.gw(i), tdpo[j].gw(i));
            }
            or_o(tspo[j], spo.gw(j));
            or_o(tdpo[j], dpo.gw(j));
        }
    }
}
