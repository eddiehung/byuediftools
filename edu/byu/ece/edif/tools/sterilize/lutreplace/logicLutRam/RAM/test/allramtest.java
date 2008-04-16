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

import edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.RAM.*;
import byucc.jhdl.base.*;
import byucc.jhdl.Logic.*;

/**
 *
 * Instances all logic RAMs - to be used to create a LUTRAM replacement library.
 *
 *  @author Nathan Rollins
 *  @version $Id:allramtest.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class allramtest extends Logic {
    public static CellInterface cell_interface[] = {
        in("indata", 8),
        in("we", 1),
        in("clk", 1),
        in("wa", 7),
        in("ra", 7),
        out("o16x1d", 2),
        out("o16x1d1", 2),
        out("o16x1s", 1),
        out("o16x1s1", 1),
        out("o16x2d", 4),
        out("o16x2s", 2),
        out("o16x4d", 8),
        out("o16x4s", 4),
        out("o16x8d", 16),
        out("o16x8s", 8),
        out("o32x1d", 2),
        out("o32x1d1", 2),
        out("o32x1s", 1),
        out("o32x1s1", 1),
        out("o32x2s", 2),
        out("o32x4s", 4),
        out("o32x8s", 8),
        out("o64x1d", 2),
        out("o64x1d1", 2),
        out("o64x1s", 1),
        out("o64x1s1", 1),
        out("o64x2s", 2),
        out("o128x1s", 1),
        out("o128x1s1", 1),

    };
    
    public allramtest(Node parent,
                      Wire indata,
                      Wire we,
                      Wire clk,
                      Wire wa,
                      Wire ra,
                      Wire o16x1d,
                      Wire o16x1d1,
                      Wire o16x1s,
                      Wire o16x1s1,
                      Wire o16x2d,
                      Wire o16x2s,
                      Wire o16x4d,
                      Wire o16x4s,
                      Wire o16x8d,
                      Wire o16x8s,                      
                      Wire o32x1d,
                      Wire o32x1d1,
                      Wire o32x1s,
                      Wire o32x1s1,
                      Wire o32x2s,
                      Wire o32x4s,
                      Wire o32x8s,                    
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
        connect("o16x2d", o16x2d);
        connect("o16x2s", o16x2s);
        connect("o16x4d", o16x4d);
        connect("o16x4s", o16x4s);
        connect("o16x8d", o16x8d);
        connect("o16x8s", o16x8s);
        connect("o32x1d", o32x1d);
        connect("o32x1d1", o32x1d1);
        connect("o32x1s", o32x1s);
        connect("o32x1s1", o32x1s1);
        connect("o32x2s", o32x2s);
        connect("o32x4s", o32x4s);
        connect("o32x8s", o32x8s);
        connect("o64x1d", o64x1d);
        connect("o64x1d1", o64x1d1);
        connect("o64x1s", o64x1s);
        connect("o64x1s1", o64x1s1);
        connect("o64x2s", o64x2s);
        connect("o128x1s", o128x1s);
        connect("o128x1s1", o128x1s1);

        
        //int init16[] = {0x1234, 0x5678, 0x90ab, 0xcdef, 0x1234, 0x5678, 0x90ab, 0xcdef};
        //int init32[] = {0x12345678, 0x90abcdef, 0x12345678, 0x90abcdef, 
        //                0x12345678, 0x90abcdef, 0x12345678, 0x90abcdef};
        int init16[] = {0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000};
        int init32[] = {0x0000000, 0x0000000, 0x0000000, 0x0000000,
                        0x0000000, 0x0000000, 0x0000000, 0x0000000};

        Wire r16x1d = wire(2, "r16x1d");
        Wire r16x1d1 = wire(2, "r16x1d1");
        Wire r16x1s = wire(1, "r16x1s");
        Wire r16x1s1 = wire(1, "r16x1s1");
        Wire r16x2d = wire(4, "r16x2d");
        Wire r16x2s = wire(2, "r16x2s");
        Wire r16x4d = wire(8, "r16x4d");
        Wire r16x4s = wire(4, "r16x4s");
        Wire r16x8d = wire(16, "r16x8d");
        Wire r16x8s = wire(8, "r16x8s");

        Wire r32x1d = wire(2, "r32x1d");
        Wire r32x1d1 = wire(2, "r32x1d1");
        Wire r32x1s = wire(1, "r32x1s");
        Wire r32x1s1 = wire(1, "r32x1s1");
        Wire r32x2s = wire(2, "r32x2s");
        Wire r32x4s = wire(4, "r32x4s");
        Wire r32x8s = wire(8, "r32x8s");

        Wire r64x1d = wire(2, "r64x1d");
        Wire r64x1d1 = wire(2, "r64x1d1");
        Wire r64x1s = wire(1, "r64x1s");
        Wire r64x1s1 = wire(1, "r64x1s1");
        Wire r64x2s = wire(2, "r64x2s");
                
        Wire r128x1s = wire(1, "r128x1s");
        Wire r128x1s1 = wire(1, "r128x1s1");

        new RAM16X1D_logic(this, we, indata.gw(0), clk, wa.gw(0), wa.gw(1), wa.gw(2), 
                           wa.gw(3), ra.gw(0), ra.gw(1), ra.gw(2), ra.gw(3), 
                           r16x1d.gw(0), r16x1d.gw(1), init16[0]);
        new RAM16X1D_1_logic(this, we, indata.gw(0), clk, wa.gw(0), wa.gw(1), wa.gw(2),
                             wa.gw(3),
                             ra.gw(0), ra.gw(1), ra.gw(2), ra.gw(3), r16x1d1.gw(0), 
                             r16x1d1.gw(1), init16[0]);

        new RAM16X1S_logic(this, we, indata.gw(0), clk, ra.gw(0), ra.gw(1), ra.gw(2), 
                           ra.gw(3), r16x1s, init16[0]);
        new RAM16X1S_1_logic(this, we, indata.gw(0), clk, ra.gw(0), ra.gw(1), ra.gw(2),
                             ra.gw(3), r16x1s1, init16[0]);

        new RAM16X2D_logic(this, we, indata.gw(0), indata.gw(1), clk, wa.gw(0), 
                           wa.gw(1), wa.gw(2), wa.gw(3), ra.gw(0), ra.gw(1), ra.gw(2),
                           ra.gw(3), r16x2d.gw(0), r16x2d.gw(1), r16x2d.gw(2), 
                           r16x2d.gw(3), init16[0], init16[1]);
        new RAM16X2S_logic(this, we, indata.gw(0), indata.gw(1), clk, ra.gw(0), 
                           ra.gw(1), ra.gw(2), ra.gw(3), r16x2s.gw(0), r16x2s.gw(1), 
                           init16[0], init16[1]);

        new RAM16X4D_logic(this, we, indata.gw(0), indata.gw(1), indata.gw(2), 
                           indata.gw(3), clk, wa.gw(0), wa.gw(1), wa.gw(2), wa.gw(3), 
                           ra.gw(0), ra.gw(1), ra.gw(2), ra.gw(3), r16x4d.gw(0), 
                           r16x4d.gw(1), r16x4d.gw(2), r16x4d.gw(3), r16x4d.gw(4), 
                           r16x4d.gw(5), r16x4d.gw(6), r16x4d.gw(7),
                           init16[0], init16[1], init16[2], init16[3]);
        new RAM16X4S_logic(this, we, indata.gw(0), indata.gw(1), indata.gw(2), 
                           indata.gw(3), clk, ra.gw(0), ra.gw(1), ra.gw(2), ra.gw(3), 
                           r16x4s.gw(0), r16x4s.gw(1), r16x4s.gw(2), r16x4s.gw(3),
                           init16[0], init16[1], init16[2], init16[3]);

        new RAM16X8D_logic(this, we, indata, clk, wa.gw(0), wa.gw(1), wa.gw(2), 
                           wa.gw(3), ra.gw(0), ra.gw(1), ra.gw(2), ra.gw(3), 
                           r16x8d.range(7, 0), r16x8d.range(15, 8), init16[0], 
                           init16[1], init16[2], init16[3], init16[4], init16[5], 
                           init16[6], init16[7]);
        new RAM16X8S_logic(this, we, indata.range(7, 0), clk, ra.gw(0), ra.gw(1), 
                           ra.gw(2), ra.gw(3), r16x8s,
                           init16[0], init16[1], init16[2], init16[3],
                           init16[4], init16[5], init16[6], init16[7]);

        new RAM32X1D_logic(this, we, indata.gw(0), clk, wa.gw(0), wa.gw(1), wa.gw(2), 
                           wa.gw(3), wa.gw(4), ra.gw(0), ra.gw(1), ra.gw(2), ra.gw(3), 
                           ra.gw(4), r32x1d.gw(0), r32x1d.gw(1), init32[0]);
        new RAM32X1D_1_logic(this, we, indata.gw(0), clk, wa.gw(0), wa.gw(1), wa.gw(2),
                             wa.gw(3), wa.gw(4), ra.gw(0), ra.gw(1), ra.gw(2), ra.gw(3),
                             ra.gw(4), r32x1d1.gw(0), r32x1d1.gw(1), init32[0]);

        new RAM32X1S_logic(this, we, indata.gw(0), clk, ra.gw(0), ra.gw(1), ra.gw(2), 
                           ra.gw(3), ra.gw(4), r32x1s, init32[0]);
        new RAM32X1S_1_logic(this, we, indata.gw(0), clk, ra.gw(0), ra.gw(1), ra.gw(2),
                             ra.gw(3), ra.gw(4), r32x1s1, init32[0]);

        new RAM32X2S_logic(this, we, indata.gw(0), indata.gw(1), clk, ra.gw(0), 
                           ra.gw(1), ra.gw(2), ra.gw(3), ra.gw(4), r32x2s.gw(0), 
                           r32x2s.gw(1), init32[0], init32[1]);
        
        new RAM32X4S_logic(this, we, indata.gw(0), indata.gw(1), indata.gw(2), 
                           indata.gw(3), clk, ra.gw(0), ra.gw(1), ra.gw(2), ra.gw(3), 
                           ra.gw(4), r32x4s.gw(0), r32x4s.gw(1), r32x4s.gw(2), 
                           r32x4s.gw(3), init32[0], init32[1], init32[2], init32[3]);
        new RAM32X8S_logic(this, we, indata.range(7, 0), clk, ra.gw(0), ra.gw(1), 
                           ra.gw(2), ra.gw(3), ra.gw(4), r32x8s,
                           init32[0], init32[1], init32[2], init32[3],
                           init32[4], init32[5], init32[6], init32[7]);

        new RAM64X1D_logic(this, we, indata.gw(0), clk, wa.gw(0), wa.gw(1), wa.gw(2), 
                           wa.gw(3), wa.gw(4), wa.gw(5), ra.gw(0), ra.gw(1), ra.gw(2), 
                           ra.gw(3), ra.gw(4), ra.gw(5), r64x1d.gw(0), r64x1d.gw(1), 
                           init32[0], init32[1]);
        new RAM64X1D_1_logic(this, we, indata.gw(0), clk, wa.gw(0), wa.gw(1), wa.gw(2),
                             wa.gw(3), wa.gw(4), wa.gw(5), ra.gw(0), ra.gw(1), ra.gw(2),
                             ra.gw(3), ra.gw(4), ra.gw(5), r64x1d1.gw(0), r64x1d1.gw(1),
                             init32[0], init32[1]);

        new RAM64X1S_logic(this, we, indata.gw(0), clk, ra.gw(0), ra.gw(1), ra.gw(2), 
                           ra.gw(3), ra.gw(4), ra.gw(5), r64x1s, init32[0], init32[1]);
        new RAM64X1S_1_logic(this, we, indata.gw(0), clk, ra.gw(0), ra.gw(1), ra.gw(2),
                             ra.gw(3), ra.gw(4), ra.gw(5), r64x1s1, init32[0], init32[1]);
        
        new RAM64X2S_logic(this, we, indata.gw(0), indata.gw(1), clk, ra.gw(0), 
                           ra.gw(1), ra.gw(2), ra.gw(3), ra.gw(4), ra.gw(5), 
                           r64x2s.gw(0), r64x2s.gw(1),
                           init32[0], init32[1], init32[2], init32[3]);

        new RAM128X1S_logic(this, we, indata.gw(0), clk, ra.gw(0), ra.gw(1), ra.gw(2), 
                            ra.gw(3), ra.gw(4), ra.gw(5), ra.gw(6), r128x1s,
                            init32[0], init32[1], init32[2], init32[3]);
        new RAM128X1S_1_logic(this, we, indata.gw(0), clk, ra.gw(0), ra.gw(1), ra.gw(2),
                              ra.gw(3), ra.gw(4), ra.gw(5), ra.gw(6), r128x1s1,
                              init32[0], init32[1], init32[2], init32[3]);


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
        buf_o(r16x2d, o16x2d);
        buf_o(r16x2s, o16x2s);
        buf_o(r32x2s, o32x2s);
        buf_o(r64x2s, o64x2s);
        buf_o(r16x4d, o16x4d);
        buf_o(r16x4s, o16x4s);
        buf_o(r32x4s, o32x4s);        
        buf_o(r16x8d, o16x8d);
        buf_o(r16x8s, o16x8s);
        buf_o(r32x8s, o32x8s);

    }
}

