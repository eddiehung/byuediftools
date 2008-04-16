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
package edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.RAM;
/*

*/

import byucc.jhdl.base.*;
import byucc.jhdl.Logic.*;
import byucc.jhdl.Xilinx.Virtex.*;

/**
 *
 * Logic implementation of a 2x4 decoder.
 *
 *  @author Nathan Rollins
 *  @version $Id$
 */

public class decode2x4 extends Logic {
    public static CellInterface cell_interface[] = {
        in("addr", 2),
        in("en", 1),
        out("dec", 4),
    };

    protected boolean cellInterfaceDeterminesUniqueNetlistStructure() {
        return true;
    }
    
    public decode2x4(Node parent,
                     Wire addr,
                     Wire en,
                     Wire dec) {
        super(parent);

        connect("addr", addr);
        connect("en", en);
        connect("dec", dec);

        and_o( not(addr.gw(0)),
               not(addr.gw(1)),
               en,
               dec.gw(0));
        
        and_o( addr.gw(0),
               not(addr.gw(1)),
               en,
               dec.gw(1));
        
        and_o( not(addr.gw(0)),
               addr.gw(1),
               en,
               dec.gw(2));

        and_o( addr.gw(0),
               addr.gw(1),
               en,
               dec.gw(3));
    }
}
