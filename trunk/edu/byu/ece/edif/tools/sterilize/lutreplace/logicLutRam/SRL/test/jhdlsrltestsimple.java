/*
 * Instances a logic and a JHDL SRL16; used for testing purposes
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
import byucc.jhdl.base.CellInterface;
import byucc.jhdl.base.Node;
import byucc.jhdl.base.Wire;
import edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.SRL.SRL16_logic;

/**
 * Instances a logic and a JHDL SRL16. Used for testing purposes
 * 
 * @author Nathan Rollins
 * @version $Id:jhdlsrltestsimple.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class jhdlsrltestsimple extends Logic {
    public static CellInterface cell_interface[] = { in("datain", 1), in("ce", 1), in("clk", 1), in("a", 4),
            out("osrl16jhdl", 1), out("osrl16logic", 1), };

    public jhdlsrltestsimple(Node parent, Wire datain, Wire ce, Wire clk, Wire a, Wire osrl16jhdl, Wire osrl16logic) {
        super(parent);

        connect("datain", datain);
        connect("ce", ce);
        connect("clk", clk);
        connect("a", a);
        connect("osrl16jhdl", osrl16jhdl);
        connect("osrl16logic", osrl16logic);

        String sinit = "abcd";
        int init = 0xabcd;

        Wire srl16jhdl = wire(1, "srl16jhdl");
        Wire srl16logic = wire(1, "srl16logic");

        new srl16(this, clk, datain, a, srl16jhdl, sinit);
        new SRL16_logic(this, datain, clk, a.gw(0), a.gw(1), a.gw(2), a.gw(3), srl16logic, init);

        buf_o(srl16jhdl, osrl16jhdl);
        buf_o(srl16logic, osrl16logic);
    }
}
