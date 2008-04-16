/* * Copyright (c) 2008 Brigham Young University
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
package edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.RAM.test;
/*

*/

import edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.RAM.*;
import byucc.jhdl.base.*;
import byucc.jhdl.Logic.*;
import byucc.jhdl.Xilinx.Virtex2.*;

/**
 *
 * Instances a logic and a JHDL RAMX1D.  Used for
 * testing purposes
 *
 *  @author Nathan Rollins
 *  @version $Id:jhdlsimpletest.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class jhdlsimpletest extends Logic {
    public static CellInterface cell_interface[] = {
        in("indata", 8),
        in("we", 1),
        in("clk", 1),
        in("wa", 7),
        in("ra", 7),
        out("o16x1dlogic", 2),
        out("o16x1djhdl", 2),

    };
    
    public jhdlsimpletest(Node parent,
                          Wire indata,
                          Wire we,
                          Wire clk,
                          Wire wa,
                          Wire ra,
                          Wire o16x1dlogic,
                          Wire o16x1djhdl) {

        super(parent);

        connect("indata", indata);
        connect("we", we);
        connect("clk", clk);
        connect("wa", wa);
        connect("ra", ra);
        connect("o16x1dlogic", o16x1dlogic);
        connect("o16x1djhdl", o16x1djhdl);
        
        int init16[] = {0x1234, 0x5678, 0x90ab, 0xcdef, 0x1234, 0x5678, 0x90ab, 0xcdef};
        int init32[] = {0x12345678, 0x90abcdef, 0x12345678, 0x90abcdef, 
                        0x12345678, 0x90abcdef, 0x12345678, 0x90abcdef};

        String sinit16[] = {"1234", "5678", "90ab", "cdef", "1234", 
                            "5678", "90ab", "cdef"};
        String sinit32[] = {"12345678", "90abcdef", "12345678", "90abcdef", 
                            "12345678", "90abcdef", "12345678", "90abcdef"};

        Wire r16x1dlogic = wire(2, "r16x1dlogic");
        Wire r16x1djhdl = wire(2, "r16x1djhdl");


        new RAM16X1D_logic(this, we, indata.gw(0), clk, wa.gw(0), wa.gw(1), wa.gw(2), wa.gw(3), 
                           ra.gw(0), ra.gw(1), ra.gw(2), ra.gw(3), 
                           r16x1dlogic.gw(0), r16x1dlogic.gw(1), init16[0]);
        
        new ram16x1d(this, indata.gw(0), we, wa.range(3, 0),  ra.range(3, 0), 
                     r16x1djhdl.gw(0), r16x1djhdl.gw(1), clk, sinit16[0]);

        buf_o(r16x1dlogic, o16x1dlogic);
        buf_o(r16x1djhdl, o16x1djhdl);

    }
}

