/*
 * Represents a simple test bench.
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
package edu.byu.ece.edif.util.export.jhdl;

import byucc.jhdl.Logic.Logic;
import byucc.jhdl.base.HWSystem;
import byucc.jhdl.base.TestBench;
import byucc.jhdl.base.Wire;

/**
 * Represents a simple test bench.
 * 
 * @version $Id$
 */

public class SimpleTestBench extends Logic implements TestBench {

    /**
     * Constructs a SimpleTestBench Object with a default clock, specified
     * parent and name.
     * 
     * @param parent The parent HWSystem to this test bench
     * @param name The name of this SimpleTestBench
     */
    public SimpleTestBench(HWSystem parent, String name) {
        super(parent, name);

        // Create the clock driver.
        Wire _clockWire = clockDriver(wire(1, "c"), "01", "c");
        setDefaultClock(_clockWire);
    }

}
