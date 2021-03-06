/*
 * Instances all JHDL SRL16s; used for testing purposes.
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
package edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.SRL.test;

import byucc.jhdl.Logic.Logic;
import byucc.jhdl.Xilinx.Virtex2.srl16;
import byucc.jhdl.Xilinx.Virtex2.srl16_1;
import byucc.jhdl.Xilinx.Virtex2.srl16e;
import byucc.jhdl.Xilinx.Virtex2.srl16e_1;
import byucc.jhdl.Xilinx.Virtex2.srlc16;
import byucc.jhdl.Xilinx.Virtex2.srlc16_1;
import byucc.jhdl.Xilinx.Virtex2.srlc16e;
import byucc.jhdl.Xilinx.Virtex2.srlc16e_1;
import byucc.jhdl.base.CellInterface;
import byucc.jhdl.base.Node;
import byucc.jhdl.base.Wire;

/**
 * Instances all JHDL SRL16s. Used for testing purposes.
 * 
 * @author Nathan Rollins
 * @version $Id:jhdlmultisrltest.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class jhdlmultisrltest extends Logic {
    public static CellInterface cell_interface[] = { in("datain", 1), in("ce", 1), in("clk", 1), in("a", 4),
            out("osrl16", 3), out("osrl161", 3), out("osrl16e", 3), out("osrl16e1", 3), out("osrlc16", 6),
            out("osrlc161", 6), out("osrlc16e", 6), out("osrlc16e1", 6), };

    public jhdlmultisrltest(Node parent, Wire datain, Wire ce, Wire clk, Wire a, Wire osrl16, Wire osrl161,
            Wire osrl16e, Wire osrl16e1, Wire osrlc16, Wire osrlc161, Wire osrlc16e, Wire osrlc16e1) {
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

        Wire srl16 = wire(3, "srl16");
        Wire srl161 = wire(3, "srl161");
        Wire srl16e = wire(3, "srl16e");
        Wire srl16e1 = wire(3, "srl16e1");
        Wire srlc16 = wire(6, "srlc16");
        Wire srlc161 = wire(6, "srlc161");
        Wire srlc16e = wire(6, "srlc16e");
        Wire srlc16e1 = wire(6, "srlc16e1");

        new srl16(this, clk, datain, a, srl16.gw(0), init);
        new srl16(this, clk, datain, a, srl16.gw(1), init);
        new srl16(this, clk, datain, a, srl16.gw(2), init);

        new srl16_1(this, clk, datain, a, srl161.gw(0), init);
        new srl16_1(this, clk, datain, a, srl161.gw(1), init);
        new srl16_1(this, clk, datain, a, srl161.gw(2), init);

        new srl16e(this, clk, datain, ce, a, srl16e.gw(0), init);
        new srl16e(this, clk, datain, ce, a, srl16e.gw(1), init);
        new srl16e(this, clk, datain, ce, a, srl16e.gw(2), init);

        new srl16e_1(this, clk, datain, ce, a, srl16e1.gw(0), init);
        new srl16e_1(this, clk, datain, ce, a, srl16e1.gw(1), init);
        new srl16e_1(this, clk, datain, ce, a, srl16e1.gw(2), init);

        new srlc16(this, clk, datain, a, srlc16.gw(0), srlc16.gw(1), init);
        new srlc16(this, clk, datain, a, srlc16.gw(2), srlc16.gw(3), init);
        new srlc16(this, clk, datain, a, srlc16.gw(4), srlc16.gw(5), init);

        new srlc16_1(this, clk, datain, a, srlc161.gw(0), srlc161.gw(1), init);
        new srlc16_1(this, clk, datain, a, srlc161.gw(2), srlc161.gw(3), init);
        new srlc16_1(this, clk, datain, a, srlc161.gw(4), srlc161.gw(5), init);

        new srlc16e(this, clk, datain, ce, a, srlc16e.gw(0), srlc16e.gw(1), init);
        new srlc16e(this, clk, datain, ce, a, srlc16e.gw(2), srlc16e.gw(3), init);
        new srlc16e(this, clk, datain, ce, a, srlc16e.gw(4), srlc16e.gw(5), init);

        new srlc16e_1(this, clk, datain, ce, a, srlc16e1.gw(0), srlc16e1.gw(1), init);
        new srlc16e_1(this, clk, datain, ce, a, srlc16e1.gw(2), srlc16e1.gw(3), init);
        new srlc16e_1(this, clk, datain, ce, a, srlc16e1.gw(4), srlc16e1.gw(5), init);

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
