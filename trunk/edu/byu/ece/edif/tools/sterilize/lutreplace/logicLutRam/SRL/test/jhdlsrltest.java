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
package edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.SRL.test;
/*

*/

import byucc.jhdl.base.*;
import byucc.jhdl.Logic.*;
import byucc.jhdl.Xilinx.Virtex2.*;

/**
 *
 * Instances all JHDL SRL16s.  Used for testing purposes.
 *
 *  @author Nathan Rollins
 *  @version $Id:jhdlsrltest.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class jhdlsrltest extends Logic {
    public static CellInterface cell_interface[] = {
        in("datain", 1),
        in("ce", 1),
        in("clk", 1),
        in("a", 4),
        out("osrl16", 1),
        out("osrl161", 1),
        out("osrl16e", 1),
        out("osrl16e1", 1),
        out("osrlc16", 2),
        out("osrlc161", 2),
        out("osrlc16e", 2),
        out("osrlc16e1", 2),
    };
    
    public jhdlsrltest(Node parent,
                       Wire datain,
                       Wire ce,
                       Wire clk,
                       Wire a,
                       Wire osrl16,
                       Wire osrl161,
                       Wire osrl16e,
                       Wire osrl16e1,
                       Wire osrlc16,
                       Wire osrlc161,
                       Wire osrlc16e,
                       Wire osrlc16e1) {
        super(parent);
        
        connect("datain", datain);
        connect("ce", ce);
        connect("clk", clk);
        connect("a", a);
        connect("osrl16", osrl16);
        connect("osrl161", osrl161);
        connect("osrl16e", osrl16e);
        connect("osrl16e1", osrl16e1);
        connect("osrlc16", osrlc16);
        connect("osrlc161", osrlc161);
        connect("osrlc16e", osrlc16e);
        connect("osrlc16e1", osrlc16e1);

        String init = "abcd";

        Wire srl16 = wire(1, "srl16");
        Wire srl161 = wire(1, "srl161");
        Wire srl16e = wire(1, "srl16e");
        Wire srl16e1 = wire(1, "srl16e1");
        Wire srlc16 = wire(2, "srlc16");
        Wire srlc161 = wire(2, "srlc161");
        Wire srlc16e = wire(2, "srlc16e");
        Wire srlc16e1 = wire(2, "srlc16e1");

        new srl16(this, clk, datain, a, srl16, init); 
        new srl16_1(this, clk, datain, a, srl161, init);
        new srl16e(this, clk, datain, ce, a, srl16e, init); 
        new srl16e_1(this, clk, datain, ce, a, srl16e1, init);
        new srlc16(this, clk, datain, a, srlc16.gw(0), 
                   srlc16.gw(1), init); 
        new srlc16_1(this, clk, datain, a, srlc161.gw(0), 
                     srlc161.gw(1), init); 
        new srlc16e(this, clk, datain, ce, a, srlc16e.gw(0), 
                    srlc16e.gw(1), init); 
        new srlc16e_1(this, clk, datain, ce, a, srlc16e1.gw(0), 
                      srlc16e1.gw(1), init); 

        buf_o(srl16, osrl16);
        buf_o(srl161, osrl161);
        buf_o(srl16e, osrl16e);
        buf_o(srl16e1, osrl16e1);
        buf_o(srlc16, osrlc16);
        buf_o(srlc161, osrlc161);
        buf_o(srlc16e, osrlc16e);
        buf_o(srlc16e1, osrlc16e1);
    }
}
