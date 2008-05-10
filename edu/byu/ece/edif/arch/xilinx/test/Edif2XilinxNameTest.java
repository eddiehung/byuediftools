/*
 * TODO: Insert class description here.
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
package edu.byu.ece.edif.arch.xilinx.test;

import edu.byu.ece.edif.arch.xilinx.EdifXPowerCompare;
import edu.byu.ece.edif.arch.xilinx.EdifXPowerParser;

/**
 * @author nhr2
 * @since Created on Jun 13, 2005
 */
public class Edif2XilinxNameTest {

    /**
     * Must pass the EDIF file name and the XPower file name as parameters.
     * 
     * @param args
     */
    public static void main(String args[]) {
        String usage = "Usage: <edifFilename> <xpowername>\n";
        String[] sections = { "Signals" };

        if (args.length < 2) {
            System.err.println(usage);
            System.exit(1);
        }
        for (int i = 2; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-h") || args[i].equalsIgnoreCase("-help")) {
                System.out.println(usage);
                System.exit(0);
            } else {
                System.out.println(usage);
                System.exit(-1);
            }
        }

        String edifFile = args[0];
        String xpowerFile = args[1];

        EdifXPowerCompare expc = new EdifXPowerCompare(edifFile, xpowerFile);
        EdifXPowerParser.printXPowerFileSection(xpowerFile, sections[0], 6);
    }
}
