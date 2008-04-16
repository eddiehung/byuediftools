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
package edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.SRL;
/*

*/

import byucc.jhdl.base.*;
import byucc.jhdl.Logic.*;
import byucc.jhdl.Xilinx.Virtex.*;

/**
 *
 * Logic implementation of an SRL4.  Used as a building block for SRL16s. 
 *
 *  @author Nathan Rollins
 *  @version $Id$
 */

public class srl4_logic extends Logic {
    public static CellInterface cell_interface[] = {
        in("d", 1),
        in("ce", 1),
        in("clk", 1),
        in("a", 2),
        out("q", 1),
        out("q15", 1),
    };

    protected boolean cellInterfaceDeterminesUniqueNetlistStructure() {
        return true;
    }

    public srl4_logic(Node parent,
                      Wire d,
                      Wire ce,
                      Wire clk,
                      Wire a,
                      Wire q,
                      Wire q15,
                      int init) {
        super(parent);

        connect("d", d);
        connect("ce", ce);
        connect("clk", clk);
        connect("a", a);
        connect("q", q);
        connect("q15", q15);

        Wire prop = wire(4, "prop");

        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                if ( (init & 1) == 1 )
                    new fdpe(this, 
                             clk, 
                             d, 
                             ce, 
                             gnd(),                             
                             prop.gw(i));
                
                else 
                    new fdcpe(this, 
                              clk, 
                              d, 
                              ce, 
                              gnd(),
                              gnd(),
                              prop.gw(i));
            }
            else {
                if ( ( (init >> i)  & 1 ) == 1 ) 
                    new fdpe(this, 
                             clk, 
                             prop.gw(i - 1), 
                             ce, 
                             gnd(),
                             prop.gw(i));
                else
                    new fdcpe(this, 
                              clk, 
                              prop.gw(i - 1), 
                              ce, 
                              gnd(),
                              gnd(),
                              prop.gw(i));
            }
        }        
        new mux4(this,
                 prop.gw(0),
                 prop.gw(1),
                 prop.gw(2),
                 prop.gw(3),
                 a,
                 q);
        buf_o(prop.gw(3), q15);
    }
}
