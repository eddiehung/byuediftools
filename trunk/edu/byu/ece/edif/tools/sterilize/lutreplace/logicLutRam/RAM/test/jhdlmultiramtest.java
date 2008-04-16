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
package edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.RAM.test;
/*

*/

import byucc.jhdl.base.*;
import byucc.jhdl.Logic.*;
import byucc.jhdl.Xilinx.Virtex.*;

/**
 *
 * Instances all JHDL RAMs.  Used for testing puposes.
 *
 *  @author Nathan Rollins
 *  @version $Id:jhdlmultiramtest.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class jhdlmultiramtest extends Logic {
    public static CellInterface cell_interface[] = {
        in("indata", 8),
        in("we", 1),
        in("clk", 1),
        in("wa", 7),
        in("ra", 7),
        out("o16x1d", 6),
        out("o16x1d1", 6),
        out("o16x1s", 3),
        out("o16x1s1", 3),
        out("o32x1d", 6),
        out("o32x1d1", 6),
        out("o32x1s", 3),
        out("o32x1s1", 3),
        out("o64x1d", 6),
        out("o64x1d1", 6),
        out("o64x1s", 3),
        out("o64x1s1", 3),
        out("o64x2s", 6),
        out("o128x1s", 3),
        out("o128x1s1", 3),

    };
    
    public jhdlmultiramtest(Node parent,
                            Wire indata,
                            Wire we,
                            Wire clk,
                            Wire wa,
                            Wire ra,
                            Wire o16x1d,
                            Wire o16x1d1,
                            Wire o16x1s,
                            Wire o16x1s1,
                            Wire o32x1d,
                            Wire o32x1d1,
                            Wire o32x1s,
                            Wire o32x1s1,
                            Wire o64x1d,
                            Wire o64x1d1,
                            Wire o64x1s,
                            Wire o64x1s1,
                            Wire o64x2s,                      
                            Wire o128x1s,
                            Wire o128x1s1) {
        
        super(parent);

        connect("indata", indata);
        connect("we", we);
        connect("clk", clk);
        connect("wa", wa);
        connect("ra", ra);
        connect("o16x1d", o16x1d);
        connect("o16x1d1", o16x1d1);
        connect("o16x1s", o16x1s);
        connect("o16x1s1", o16x1s1);
        connect("o32x1d", o32x1d);
        connect("o32x1d1", o32x1d1);
        connect("o32x1s", o32x1s);
        connect("o32x1s1", o32x1s1);
        connect("o64x1d", o64x1d);
        connect("o64x1d1", o64x1d1);
        connect("o64x1s", o64x1s);
        connect("o64x1s1", o64x1s1);
        connect("o64x2s", o64x2s);
        connect("o128x1s", o128x1s);
        connect("o128x1s1", o128x1s1);

        
        String init16[] = {"1234", "5678", "90ab", "cdef", "1234", 
                           "5678", "90ab", "cdef"};
        String init32[] = {"12345678", "90abcdef", "12345678", "90abcdef", 
                           "12345678", "90abcdef", "12345678", "90abcdef"};

        Wire r16x1d = wire(6, "r16x1d");
        Wire r16x1d1 = wire(6, "r16x1d1");
        Wire r16x1s = wire(3, "r16x1s");
        Wire r16x1s1 = wire(3, "r16x1s1");

        Wire r32x1d = wire(6, "r32x1d");
        Wire r32x1d1 = wire(6, "r32x1d1");
        Wire r32x1s = wire(3, "r32x1s");
        Wire r32x1s1 = wire(3, "r32x1s1");

        Wire r64x1d = wire(6, "r64x1d");
        Wire r64x1d1 = wire(6, "r64x1d1");
        Wire r64x1s = wire(3, "r64x1s");
        Wire r64x1s1 = wire(3, "r64x1s1");
        Wire r64x2s = wire(6, "r64x2s");
                
        Wire r128x1s = wire(3, "r128x1s");
        Wire r128x1s1 = wire(3, "r128x1s1");

        new ram16x1d(this, indata.gw(0), we, wa.range(3, 0),  ra.range(3, 0), 
                     r16x1d.gw(0), r16x1d.gw(1), clk, init16[0]);
        new ram16x1d(this, indata.gw(0), we, wa.range(3, 0),  ra.range(3, 0), 
                     r16x1d.gw(2), r16x1d.gw(3), clk, init16[0]);
        new ram16x1d(this, indata.gw(0), we, wa.range(3, 0),  ra.range(3, 0), 
                     r16x1d.gw(4), r16x1d.gw(5), clk, init16[0]);
        new ram16x1d_1(this, indata.gw(0), we, wa.range(3, 0), ra.range(3, 0), r16x1d1.gw(0), 
                       r16x1d1.gw(1), clk, init16[0]);
        new ram16x1d_1(this, indata.gw(0), we, wa.range(3, 0), ra.range(3, 0), r16x1d1.gw(2), 
                       r16x1d1.gw(3), clk, init16[0]);
        new ram16x1d_1(this, indata.gw(0), we, wa.range(3, 0), ra.range(3, 0), r16x1d1.gw(4), 
                       r16x1d1.gw(5), clk, init16[0]);
        
        new ram16x1s(this, indata.gw(0), we, ra.range(3, 0), r16x1s.gw(0), clk, init16[0]);
        new ram16x1s(this, indata.gw(0), we, ra.range(3, 0), r16x1s.gw(1), clk, init16[0]);
        new ram16x1s(this, indata.gw(0), we, ra.range(3, 0), r16x1s.gw(2), clk, init16[0]);
        new ram16x1s_1(this, indata.gw(0), we, ra.range(3, 0), r16x1s1.gw(0), clk, init16[0]);
        new ram16x1s_1(this, indata.gw(0), we, ra.range(3, 0), r16x1s1.gw(1), clk, init16[0]);
        new ram16x1s_1(this, indata.gw(0), we, ra.range(3, 0), r16x1s1.gw(2), clk, init16[0]);

        new byucc.jhdl.Xilinx.Virtex2.ram32x1d(this, indata.gw(0), we, wa.range(4, 0), 
                                               ra.range(4, 0), r32x1d.gw(0), 
                                               r32x1d.gw(1), clk, init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram32x1d(this, indata.gw(0), we, wa.range(4, 0), 
                                               ra.range(4, 0), r32x1d.gw(2), 
                                               r32x1d.gw(3), clk, init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram32x1d(this, indata.gw(0), we, wa.range(4, 0), 
                                               ra.range(4, 0), r32x1d.gw(4), 
                                               r32x1d.gw(5), clk, init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram32x1d_1(this, indata.gw(0), we, wa.range(4, 0), 
                                                 ra.range(4, 0), r32x1d1.gw(0), 
                                                 r32x1d1.gw(1), clk, init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram32x1d_1(this, indata.gw(0), we, wa.range(4, 0), 
                                                 ra.range(4, 0), r32x1d1.gw(2), 
                                                 r32x1d1.gw(3), clk, init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram32x1d_1(this, indata.gw(0), we, wa.range(4, 0), 
                                                 ra.range(4, 0), r32x1d1.gw(4), 
                                                 r32x1d1.gw(5), clk, init32[0]);

        new ram32x1s(this, indata.gw(0), we, ra.range(4, 0), r32x1s.gw(0), clk, init32[0]);
        new ram32x1s(this, indata.gw(0), we, ra.range(4, 0), r32x1s.gw(1), clk, init32[0]);
        new ram32x1s(this, indata.gw(0), we, ra.range(4, 0), r32x1s.gw(2), clk, init32[0]);
        new ram32x1s_1(this, indata.gw(0), we, ra.range(4, 0), r32x1s1.gw(0), clk, init32[0]);
        new ram32x1s_1(this, indata.gw(0), we, ra.range(4, 0), r32x1s1.gw(1), clk, init32[0]);
        new ram32x1s_1(this, indata.gw(0), we, ra.range(4, 0), r32x1s1.gw(2), clk, init32[0]);

        new byucc.jhdl.Xilinx.Virtex2.ram64x1d(this, indata.gw(0), we, wa.range(5, 0), 
                                               ra.range(5, 0), r64x1d.gw(0), 
                                               r64x1d.gw(1), clk, init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram64x1d(this, indata.gw(0), we, wa.range(5, 0), 
                                               ra.range(5, 0), r64x1d.gw(2), 
                                               r64x1d.gw(3), clk, init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram64x1d(this, indata.gw(0), we, wa.range(5, 0), 
                                               ra.range(5, 0), r64x1d.gw(4), 
                                               r64x1d.gw(5), clk, init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram64x1d_1(this, indata.gw(0), we, wa.range(5, 0), 
                                                 ra.range(5, 0), r64x1d1.gw(0), 
                                                 r64x1d1.gw(1), clk, init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram64x1d_1(this, indata.gw(0), we, wa.range(5, 0), 
                                                 ra.range(5, 0), r64x1d1.gw(2), 
                                                 r64x1d1.gw(3), clk, init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram64x1d_1(this, indata.gw(0), we, wa.range(5, 0), 
                                                 ra.range(5, 0), r64x1d1.gw(4), 
                                                 r64x1d1.gw(5), clk, init32[1]+init32[0]);

        new byucc.jhdl.Xilinx.Virtex2.ram64x1s(this, indata.gw(0), we, ra.range(5, 0), 
                                               r64x1s.gw(0), clk, init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram64x1s(this, indata.gw(0), we, ra.range(5, 0), 
                                               r64x1s.gw(1), clk, init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram64x1s(this, indata.gw(0), we, ra.range(5, 0), 
                                               r64x1s.gw(2), clk, init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram64x1s_1(this, indata.gw(0), we, ra.range(5, 0), 
                                                 r64x1s1.gw(0), clk, init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram64x1s_1(this, indata.gw(0), we, ra.range(5, 0), 
                                                 r64x1s1.gw(1), clk, init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram64x1s_1(this, indata.gw(0), we, ra.range(5, 0), 
                                                 r64x1s1.gw(2), clk, init32[1]+init32[0]);
        
        new byucc.jhdl.Xilinx.Virtex2.ram64x2s(this, indata.range(1, 0), we, ra.range(5, 0), 
                                               r64x2s.range(1, 0), clk,
                                               init32[3]+init32[2], init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram64x2s(this, indata.range(1, 0), we, ra.range(5, 0), 
                                               r64x2s.range(3, 2), clk,
                                               init32[3]+init32[2], init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram64x2s(this, indata.range(1, 0), we, ra.range(5, 0), 
                                               r64x2s.range(5, 4), clk,
                                               init32[3]+init32[2], init32[1]+init32[0]);

        new byucc.jhdl.Xilinx.Virtex2.ram128x1s(this, indata.gw(0), we, ra.range(6, 0), 
                                                r128x1s.gw(0), clk, 
                                                init32[3]+init32[2]+init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram128x1s(this, indata.gw(0), we, ra.range(6, 0), 
                                                r128x1s.gw(1), clk, 
                                                init32[3]+init32[2]+init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram128x1s(this, indata.gw(0), we, ra.range(6, 0), 
                                                r128x1s.gw(2), clk, 
                                                init32[3]+init32[2]+init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram128x1s_1(this, indata.gw(0), we, ra.range(6, 0), 
                                                  r128x1s1.gw(0), clk,
                                                  init32[3]+init32[2]+init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram128x1s_1(this, indata.gw(0), we, ra.range(6, 0), 
                                                  r128x1s1.gw(1), clk,
                                                  init32[3]+init32[2]+init32[1]+init32[0]);
        new byucc.jhdl.Xilinx.Virtex2.ram128x1s_1(this, indata.gw(0), we, ra.range(6, 0), 
                                                  r128x1s1.gw(2), clk,
                                                  init32[3]+init32[2]+init32[1]+init32[0]);


        buf_o(r16x1d, o16x1d);
        buf_o(r16x1d1, o16x1d1);
        buf_o(r16x1s, o16x1s);
        buf_o(r16x1s1, o16x1s1);
        buf_o(r32x1d, o32x1d);
        buf_o(r32x1d1, o32x1d1);
        buf_o(r32x1s, o32x1s);
        buf_o(r32x1s1, o32x1s1);
        buf_o(r64x1d, o64x1d);
        buf_o(r64x1d1, o64x1d1);
        buf_o(r64x1s, o64x1s);
        buf_o(r64x1s1, o64x1s1);
        buf_o(r128x1s, o128x1s);
        buf_o(r128x1s1, o128x1s1);
        buf_o(r64x2s, o64x2s);

    }
}

