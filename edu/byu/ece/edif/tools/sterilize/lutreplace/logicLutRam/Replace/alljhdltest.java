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
package edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.Replace;
/*

*/

import edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.RAM.test.*;
import edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.SRL.test.*;
import byucc.jhdl.base.*;
import byucc.jhdl.Logic.*;

/**
 *
 * Instances all JHDL RAMs and SRLs.
 *
 *  @author Nathan Rollins
 *  @version $Id:alljhdltest.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class alljhdltest extends Logic {
    public static CellInterface cell_interface[] = {
        in("indata", 8),
        in("ce", 1),
        in("we", 1),
        in("clk", 1),
        in("wa", 7),
        in("ra", 7),
        out("o16x1d", 2),
        out("o16x1d1", 2),
        out("o16x1s", 1),
        out("o16x1s1", 1),
        // out("o16x2d", 4),
        //out("o16x2s", 2),
        //out("o16x4d", 8),
        //out("o16x4s", 4),
        //out("o16x8d", 16),
        //out("o16x8s", 8),
        out("o32x1d", 2),
        out("o32x1d1", 2),
        out("o32x1s", 1),
        out("o32x1s1", 1),
        //out("o32x2s", 2),
        //out("o32x4s", 4),
        //out("o32x8s", 8),
        out("o64x1d", 2),
        out("o64x1d1", 2),
        out("o64x1s", 1),
        out("o64x1s1", 1),
        out("o64x2s", 2),
        out("o128x1s", 1),
        out("o128x1s1", 1),
        
        out("osrl16", 1),
        out("osrl161", 1),
        out("osrl16e", 1),
        out("osrl16e1", 1),
        out("osrlc16", 2),
        out("osrlc161", 2),
        out("osrlc16e", 2),
        out("osrlc16e1", 2),
    };
    
    public alljhdltest(Node parent,
                       Wire indata,
                       Wire ce,
                       Wire we,
                       Wire clk,
                       Wire wa,
                       Wire ra,
                       Wire o16x1d,
                       Wire o16x1d1,
                       Wire o16x1s,
                       Wire o16x1s1,
                       Wire o16x2d,
                       //Wire o16x2s,
                       //Wire o16x4d,
                       //Wire o16x4s,
                       //Wire o16x8d,
                       //Wire o16x8s,                      
                       Wire o32x1d,
                       Wire o32x1d1,
                       Wire o32x1s,
                       Wire o32x1s1,
                       //Wire o32x2s,
                       //Wire o32x4s,
                       //Wire o32x8s,                    
                       Wire o64x1d,
                       Wire o64x1d1,
                       Wire o64x1s,
                       Wire o64x1s1,
                       Wire o64x2s,                      
                       Wire o128x1s,
                       Wire o128x1s1,
                       Wire osrl16,
                       Wire osrl161,
                       Wire osrl16e,
                       Wire osrl16e1,
                       Wire osrlc16,
                       Wire osrlc161,
                       Wire osrlc16e,
                       Wire osrlc16e1) {
        
        super(parent);

        connect("indata", indata);
        connect("ce", ce);
        connect("we", we);
        connect("clk", clk);
        connect("wa", wa);
        connect("ra", ra);
        connect("o16x1d", o16x1d);
        connect("o16x1d1", o16x1d1);
        connect("o16x1s", o16x1s);
        connect("o16x1s1", o16x1s1);
        //connect("o16x2d", o16x2d);
        //connect("o16x2s", o16x2s);
        //connect("o16x4d", o16x4d);
        //connect("o16x4s", o16x4s);
        //connect("o16x8d", o16x8d);
        //connect("o16x8s", o16x8s);
        connect("o32x1d", o32x1d);
        connect("o32x1d1", o32x1d1);
        connect("o32x1s", o32x1s);
        connect("o32x1s1", o32x1s1);
        //connect("o32x2s", o32x2s);
        //connect("o32x4s", o32x4s);
        //connect("o32x8s", o32x8s);
        connect("o64x1d", o64x1d);
        connect("o64x1d1", o64x1d1);
        connect("o64x1s", o64x1s);
        connect("o64x1s1", o64x1s1);
        connect("o64x2s", o64x2s);
        connect("o128x1s", o128x1s);
        connect("o128x1s1", o128x1s1);
        connect("osrl16", osrl16);
        connect("osrl161", osrl161);
        connect("osrl16e", osrl16e);
        connect("osrl16e1", osrl16e1);
        connect("osrlc16", osrlc16);
        connect("osrlc161", osrlc161);
        connect("osrlc16e", osrlc16e);
        connect("osrlc16e1", osrlc16e1);
    
        // instance all JHDL RAMs
        new jhdlramtest(this, indata, we, clk, wa, ra,
                        o16x1d,
                        o16x1d1,
                        o16x1s,
                        o16x1s1,
                        //o16x2d,
                        //o16x2s,
                        //o16x4d,
                        //o16x4s,
                        //o16x8d,
                        //o16x8s,
                        o32x1d,
                        o32x1d1,
                        o32x1s,
                        o32x1s1,
                        //o32x2s,
                        //o32x4s,
                        //o32x8s,
                        o64x1d,
                        o64x1d1,
                        o64x1s,
                        o64x1s1,
                        o64x2s,
                        o128x1s,
                        o128x1s1);

        // instance all JHDL SRLs
        new jhdlsrltest(this, indata.gw(0), ce, clk, ra.range(3, 0),
                        osrl16,
                        osrl161,
                        osrl16e,
                        osrl16e1,
                        osrlc16,
                        osrlc161,
                        osrlc16e,
                        osrlc16e1);                      
    }
}

