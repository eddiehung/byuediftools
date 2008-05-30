/*
 * Logic implementation of a RAM16X4S
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
package edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.RAM;

import byucc.jhdl.Logic.Logic;
import byucc.jhdl.Xilinx.Virtex.fdcpe;
import byucc.jhdl.Xilinx.Virtex.fdpe;
import byucc.jhdl.base.CellInterface;
import byucc.jhdl.base.Node;
import byucc.jhdl.base.Wire;

/**
 * Logic implementation of a RAM16X4S
 * 
 * @author Nathan Rollins
 * @version $Id$
 */

public class RAM16X4S_logic extends Logic {
    public static CellInterface cell_interface[] = { in("WE", 1), in("D0", 1), in("D1", 1), in("D2", 1), in("D3", 1),
            in("WCLK", 1), in("A0", 1), in("A1", 1), in("A2", 1), in("A3", 1), out("O0", 1), out("O1", 1),
            out("O2", 1), out("O3", 1), };

    protected boolean cellInterfaceDeterminesUniqueNetlistStructure() {
        return true;
    }

    public RAM16X4S_logic(Node parent, Wire we, Wire d0, Wire d1, Wire d2, Wire d3, Wire wclk, Wire a0, Wire a1,
            Wire a2, Wire a3, Wire o0, Wire o1, Wire o2, Wire o3, int init1, int init2, int init3, int init4) {
        super(parent);

        connect("WE", we);
        connect("D0", d0);
        connect("D1", d1);
        connect("D2", d2);
        connect("D3", d3);
        connect("WCLK", wclk);
        connect("A0", a0);
        connect("A1", a1);
        connect("A2", a2);
        connect("A3", a3);
        connect("O0", o0);
        connect("O1", o1);
        connect("O2", o2);
        connect("O3", o3);

        int MIN = 16;
        int WIDTH = 16;
        int[] inits = { init1, init2, init3, init4 };

        Wire abus = concat(a3, a2, a1, a0);
        Wire dbus = concat(d3, d2, d1, d0);
        Wire rdec = wire(WIDTH, "rdec");
        Wire regout[] = new Wire[4];
        Wire to[] = new Wire[4];

        new decode4x16(this, abus, vcc(), rdec);

        for (int i = 0; i < 4; i++) {
            regout[i] = wire(WIDTH, "regout_" + i);
            to[i] = wire(WIDTH, "to_" + i);
        }

        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < WIDTH; i++) {
                if (((inits[(j * WIDTH / MIN) + (i / MIN)] >> i % MIN) & 1) == 1) {
                    new fdpe(this, wclk, dbus.gw(j), and(we, rdec.gw(i)), gnd(), regout[j].gw(i));
                } else {
                    new fdcpe(this, wclk, dbus.gw(j), and(we, rdec.gw(i)), gnd(), gnd(), regout[j].gw(i));
                }
                and_o(regout[j].gw(i), rdec.gw(i), to[j].gw(i));
            }
        }
        or_o(to[0], o0);
        or_o(to[1], o1);
        or_o(to[2], o2);
        or_o(to[3], o3);
    }
}
