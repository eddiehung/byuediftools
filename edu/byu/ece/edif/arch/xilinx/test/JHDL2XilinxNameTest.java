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

import de.uni_frankfurt.grimm.vhdl.Edif2XilinxName;
import de.uni_frankfurt.grimm.vhdl.EdifXPowerParser;
import de.uni_frankfurt.grimm.vhdl.JHDL2XilinxNameMap;
import byucc.jhdl.base.Cell;
import byucc.jhdl.base.HWSystem;
import byucc.jhdl.base.Wire;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.util.export.jhdl.Edi2JHDL;
import edu.byu.ece.edif.util.export.jhdl.JHDLWrapper;
import edu.byu.ece.edif.util.export.jhdl.SimpleTestBench;

/**
 * @author nhr2
 * @since Created on Jun 27, 2005
 */
public class JHDL2XilinxNameTest {

    public JHDL2XilinxNameTest() {
        super();
    }

    public static void main(String[] args) {
        String usage = "Usage: <edifFilename> [-t <technology>] [-p]\n";
        String tech = "Virtex2";
        String xpfile = "";
        boolean print = false;
        boolean doxp = false;

        if (args.length < 1) {
            System.err.println(usage);
            System.exit(1);
        }
        for (int i = 1; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-h") || args[i].equalsIgnoreCase("-help")) {
                System.out.println(usage);
                System.exit(0);
            } else if (args[i].equalsIgnoreCase("-p")) {
                print = true;
            } else if (args[i].equalsIgnoreCase("-t")) {
                i++;
                tech = args[i];
            } else if (args[i].equalsIgnoreCase("-xp")) {
                i++;
                xpfile = args[i];
                doxp = true;
            } else {
                System.out.println(usage);
                System.exit(-1);
            }
        }
        String edifFile = args[0];
        SimpleTestBench root = new SimpleTestBench(new HWSystem(), "Edif_wrapper");
        EdifCellInstance topInstance = Edif2XilinxName.parseCell(edifFile);
        Edi2JHDL e2j = new Edi2JHDL(tech);
        Wire portWires[] = JHDLWrapper.createWiresFromPorts(root, topInstance, true);
        //BuildWrapper.openCircuitInCVT("Virtex", topInstance, true);
        Cell mainRoot = e2j.toJHDLCircuit(root, topInstance, portWires, true);

        JHDL2XilinxNameMap j2xn = new JHDL2XilinxNameMap(mainRoot, topInstance);
        if (print)
            j2xn.printEdifNetMap();
        if (doxp) {
            EdifXPowerParser exp = new EdifXPowerParser(xpfile, "WIRES");
            JHDL2XilinxNameMap.checkXPowerNameMatch(j2xn, exp.getXPowerNames());
        }

    }
}
