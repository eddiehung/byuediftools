/*
 * Instances a logic and a JHDL RAMX1S. Used for testing purposes.
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
package edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.RAM.test;

import byucc.jhdl.Logic.Logic;
import byucc.jhdl.Xilinx.Virtex2.ram128x1s_1;
import byucc.jhdl.base.CellInterface;
import byucc.jhdl.base.Node;
import byucc.jhdl.base.Wire;
import edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.RAM.RAM128X1S_1_logic;

/**
 * Instances a logic and a JHDL RAMX1S. Used for testing purposes.
 * 
 * @author Nathan Rollins
 * @version $Id:jhdlsimpletests.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class jhdlsimpletests extends Logic {
    public static CellInterface cell_interface[] = { in("indata", 8), in("we", 1), in("clk", 1), in("ra", 7),
            out("o16x1dlogic", 1), out("o16x1djhdl", 1),

    };

    public jhdlsimpletests(Node parent, Wire indata, Wire we, Wire clk, Wire ra, Wire o16x1dlogic, Wire o16x1djhdl) {

        super(parent);

        connect("indata", indata);
        connect("we", we);
        connect("clk", clk);
        connect("ra", ra);
        connect("o16x1dlogic", o16x1dlogic);
        connect("o16x1djhdl", o16x1djhdl);

        int init16[] = { 0x1234, 0x5678, 0x90ab, 0xcdef, 0x1234, 0x5678, 0x90ab, 0xcdef };
        int init32[] = { 0x12345678, 0x90abcdef, 0x12345678, 0x90abcdef, 0x12345678, 0x90abcdef, 0x12345678, 0x90abcdef };

        String sinit16[] = { "1234", "5678", "90ab", "cdef", "1234", "5678", "90ab", "cdef" };
        String sinit32[] = { "12345678", "90abcdef", "12345678", "90abcdef", "12345678", "90abcdef", "12345678",
                "90abcdef" };

        Wire r16x1dlogic = wire(1, "r16x1dlogic");
        Wire r16x1djhdl = wire(1, "r16x1djhdl");

        new RAM128X1S_1_logic(this, we, indata.gw(0), clk, ra.gw(0), ra.gw(1), ra.gw(2), ra.gw(3), ra.gw(4), ra.gw(5),
                ra.gw(6), r16x1dlogic.gw(0), init32[0], init32[1], init32[2], init32[3]);

        new ram128x1s_1(this, indata.gw(0), we, ra.range(6, 0), r16x1djhdl.gw(0), clk, sinit32[3] + sinit32[2]
                + sinit32[1] + sinit32[0]);

        buf_o(r16x1dlogic, o16x1dlogic);
        buf_o(r16x1djhdl, o16x1djhdl);

    }
}
