/* Generates the EDIF file for RAM16X2S
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
package edu.byu.ece.edif.tools.sterilize.lutreplace.RAM;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifCellInterface;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifPrintWriter;
import edu.byu.ece.edif.core.EdifTypedValue;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.core.PropertyList;
import edu.byu.ece.edif.core.StringTypedValue;

/**
 * Generates the EDIF file for RAM16X2S
 * Includes: 32 FDEs, 2 16-1 MUXs(9 LUT3s, 4 MUXF5s, and 2 MUXF6s for each), 1 4-16 decoder(2 LUT2s, 16 LUT4s).
 * 
 * @author Yubo Li
 *
 */

public class RAM16X2S_replacement{
	public static void main(String args[]){
		
		String outputFileName = "RAM16X2S_replacement.edf";
		
		try{
			/******Environment and Environment manager******/
			EdifEnvironment topLevel = new EdifEnvironment("RAM16X2S_replacement");
			EdifLibraryManager manager = new EdifLibraryManager(topLevel);
			try{
				/******Libraries******/
				EdifLibrary work = new EdifLibrary(manager, "work");
				topLevel.addLibrary(work);
				
				/******Cells******/
				EdifCell BUFGP = XilinxLibrary.findOrAddXilinxPrimitive(manager, "BUFGP");
				EdifCell IBUF = XilinxLibrary.findOrAddXilinxPrimitive(manager, "IBUF");
				EdifCell OBUF = XilinxLibrary.findOrAddXilinxPrimitive(manager, "OBUF");
				EdifCell FDE = XilinxLibrary.findOrAddXilinxPrimitive(manager, "FDE");
				EdifCell LUT2 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "LUT2");
				EdifCell LUT3 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "LUT3");
				EdifCell LUT4 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "LUT4");
				EdifCell MUXF5 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "MUXF5");
				EdifCell MUXF6 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "MUXF6");		
				EdifCell RAM16X2S_replacement = new EdifCell(work, "RAM16X2S_replacement");
				RAM16X2S_replacement.addPort("WE", 1, 1);
				RAM16X2S_replacement.addPort("D0", 1, 1);
				RAM16X2S_replacement.addPort("D1", 1, 1);
				RAM16X2S_replacement.addPort("WCLK", 1, 1);
				RAM16X2S_replacement.addPort("A0", 1, 1);
				RAM16X2S_replacement.addPort("A1", 1, 1);
				RAM16X2S_replacement.addPort("A2", 1, 1);
				RAM16X2S_replacement.addPort("A3", 1, 1);
				RAM16X2S_replacement.addPort("O0", 1, 2);
				RAM16X2S_replacement.addPort("O1", 1, 2);
				
				/******Design******/
				EdifDesign design = new EdifDesign("RAM16X2S_replacement");
				topLevel.setTopDesign(design);
				topLevel.setTopCell(RAM16X2S_replacement);
				
				/******Nets******/
				EdifNet wclk_c = new EdifNet("wclk_c", RAM16X2S_replacement);
				EdifNet wclk = new EdifNet("wclk", RAM16X2S_replacement);
				EdifNet d0_c = new EdifNet("d0_c", RAM16X2S_replacement);
				EdifNet d0 = new EdifNet("d0", RAM16X2S_replacement);
				EdifNet d1_c = new EdifNet("d1_c", RAM16X2S_replacement);
				EdifNet d1 = new EdifNet("d1", RAM16X2S_replacement);
				EdifNet we_c = new EdifNet("we_c", RAM16X2S_replacement);
				EdifNet we = new EdifNet("we", RAM16X2S_replacement);
				EdifNet a0_c = new EdifNet("a0_c", RAM16X2S_replacement);
				EdifNet a0 = new EdifNet("a0", RAM16X2S_replacement);
				EdifNet a1_c = new EdifNet("a1_c", RAM16X2S_replacement);
				EdifNet a1 = new EdifNet("a1", RAM16X2S_replacement);
				EdifNet a2_c = new EdifNet("a2_c", RAM16X2S_replacement);
				EdifNet a2 = new EdifNet("a2", RAM16X2S_replacement);
				EdifNet a3_c = new EdifNet("a3_c", RAM16X2S_replacement);
				EdifNet a3 = new EdifNet("a3", RAM16X2S_replacement);
				EdifNet o0_c = new EdifNet("o0_c", RAM16X2S_replacement);
				EdifNet o0 = new EdifNet("o0", RAM16X2S_replacement);
				EdifNet o1_c = new EdifNet("o1_c", RAM16X2S_replacement);
				EdifNet o1 = new EdifNet("o1", RAM16X2S_replacement);
				EdifNet wire1 = new EdifNet("wire1", RAM16X2S_replacement);
				EdifNet wire2 = new EdifNet("wire2", RAM16X2S_replacement);
				EdifNet wire3 = new EdifNet("wire3", RAM16X2S_replacement);
				EdifNet wire4 = new EdifNet("wire4", RAM16X2S_replacement);
				EdifNet wire5 = new EdifNet("wire5", RAM16X2S_replacement);
				EdifNet wire6 = new EdifNet("wire6", RAM16X2S_replacement);
				EdifNet wire7 = new EdifNet("wire7", RAM16X2S_replacement);
				EdifNet wire8 = new EdifNet("wire8", RAM16X2S_replacement);
				EdifNet wire9 = new EdifNet("wire9", RAM16X2S_replacement);
				EdifNet wire10 = new EdifNet("wire10", RAM16X2S_replacement);
				EdifNet wire11 = new EdifNet("wire11", RAM16X2S_replacement);
				EdifNet wire12 = new EdifNet("wire12", RAM16X2S_replacement);
				EdifNet wire13 = new EdifNet("wire13", RAM16X2S_replacement);
				EdifNet wire14 = new EdifNet("wire14", RAM16X2S_replacement);
				EdifNet wire15 = new EdifNet("wire15", RAM16X2S_replacement);
				EdifNet wire16 = new EdifNet("wire16", RAM16X2S_replacement);
				EdifNet wire17 = new EdifNet("wire17", RAM16X2S_replacement);
				EdifNet wire18 = new EdifNet("wire18", RAM16X2S_replacement);
				EdifNet wire19 = new EdifNet("wire19", RAM16X2S_replacement);
				EdifNet wire20 = new EdifNet("wire20", RAM16X2S_replacement);
				EdifNet wire21 = new EdifNet("wire21", RAM16X2S_replacement);
				EdifNet wire22 = new EdifNet("wire22", RAM16X2S_replacement);
				EdifNet wire23 = new EdifNet("wire23", RAM16X2S_replacement);
				EdifNet wire24 = new EdifNet("wire24", RAM16X2S_replacement);
				EdifNet wire25 = new EdifNet("wire25", RAM16X2S_replacement);
				EdifNet wire26 = new EdifNet("wire26", RAM16X2S_replacement);
				EdifNet wire27 = new EdifNet("wire27", RAM16X2S_replacement);
				EdifNet wire28 = new EdifNet("wire28", RAM16X2S_replacement);
				EdifNet wire29 = new EdifNet("wire29", RAM16X2S_replacement);
				EdifNet wire30 = new EdifNet("wire30", RAM16X2S_replacement);
				EdifNet wire31 = new EdifNet("wire31", RAM16X2S_replacement);
				EdifNet wire32 = new EdifNet("wire32", RAM16X2S_replacement);
				EdifNet net1 = new EdifNet("net1", RAM16X2S_replacement);
				EdifNet net2 = new EdifNet("net2", RAM16X2S_replacement);
				EdifNet net3 = new EdifNet("net3", RAM16X2S_replacement);
				EdifNet net4 = new EdifNet("net4", RAM16X2S_replacement);
				EdifNet net5 = new EdifNet("net5", RAM16X2S_replacement);
				EdifNet net6 = new EdifNet("net6", RAM16X2S_replacement);
				EdifNet net7 = new EdifNet("net7", RAM16X2S_replacement);
				EdifNet net8 = new EdifNet("net8", RAM16X2S_replacement);
				EdifNet net9 = new EdifNet("net9", RAM16X2S_replacement);
				EdifNet net10 = new EdifNet("net10", RAM16X2S_replacement);
				EdifNet net11 = new EdifNet("net11", RAM16X2S_replacement);
				EdifNet net12 = new EdifNet("net12", RAM16X2S_replacement);
				EdifNet net13 = new EdifNet("net13", RAM16X2S_replacement);
				EdifNet net14 = new EdifNet("net14", RAM16X2S_replacement);
				EdifNet net15 = new EdifNet("net15", RAM16X2S_replacement);
				EdifNet net16 = new EdifNet("net16", RAM16X2S_replacement);
				EdifNet net17 = new EdifNet("net17", RAM16X2S_replacement);
				EdifNet net18 = new EdifNet("net18", RAM16X2S_replacement);
				EdifNet net19 = new EdifNet("net19", RAM16X2S_replacement);
				EdifNet net20 = new EdifNet("net20", RAM16X2S_replacement);
				EdifNet net21 = new EdifNet("net21", RAM16X2S_replacement);
				EdifNet net22 = new EdifNet("net22", RAM16X2S_replacement);
				EdifNet net23 = new EdifNet("net23", RAM16X2S_replacement);
				EdifNet net24 = new EdifNet("net24", RAM16X2S_replacement);
				EdifNet net25 = new EdifNet("net25", RAM16X2S_replacement);
				EdifNet net26 = new EdifNet("net26", RAM16X2S_replacement);
				EdifNet net27 = new EdifNet("net27", RAM16X2S_replacement);
				EdifNet net28 = new EdifNet("net28", RAM16X2S_replacement);
				EdifNet net29 = new EdifNet("net29", RAM16X2S_replacement);
				EdifNet net30 = new EdifNet("net30", RAM16X2S_replacement);
				EdifNet e1 = new EdifNet("e1", RAM16X2S_replacement);
				EdifNet e2 = new EdifNet("e2", RAM16X2S_replacement);
				EdifNet e3 = new EdifNet("e3", RAM16X2S_replacement);
				EdifNet e4 = new EdifNet("e4", RAM16X2S_replacement);
				EdifNet e5 = new EdifNet("e5", RAM16X2S_replacement);
				EdifNet e6 = new EdifNet("e6", RAM16X2S_replacement);
				EdifNet e7 = new EdifNet("e7", RAM16X2S_replacement);
				EdifNet e8 = new EdifNet("e8", RAM16X2S_replacement);
				EdifNet e9 = new EdifNet("e9", RAM16X2S_replacement);
				EdifNet e10 = new EdifNet("e10", RAM16X2S_replacement);
				EdifNet e11 = new EdifNet("e11", RAM16X2S_replacement);
				EdifNet e12 = new EdifNet("e12", RAM16X2S_replacement);
				EdifNet e13 = new EdifNet("e13", RAM16X2S_replacement);
				EdifNet e14 = new EdifNet("e14", RAM16X2S_replacement);
				EdifNet e15 = new EdifNet("e15", RAM16X2S_replacement);
				EdifNet e16 = new EdifNet("e16", RAM16X2S_replacement);
				RAM16X2S_replacement.addNet(wclk_c);
				RAM16X2S_replacement.addNet(wclk);
				RAM16X2S_replacement.addNet(d0_c);
				RAM16X2S_replacement.addNet(d0);
				RAM16X2S_replacement.addNet(d1_c);
				RAM16X2S_replacement.addNet(d1);
				RAM16X2S_replacement.addNet(we_c);
				RAM16X2S_replacement.addNet(we);
				RAM16X2S_replacement.addNet(a0_c);
				RAM16X2S_replacement.addNet(a0);
				RAM16X2S_replacement.addNet(a1_c);
				RAM16X2S_replacement.addNet(a1);
				RAM16X2S_replacement.addNet(a2_c);
				RAM16X2S_replacement.addNet(a2);
				RAM16X2S_replacement.addNet(a3_c);
				RAM16X2S_replacement.addNet(a3);
				RAM16X2S_replacement.addNet(o0_c);
				RAM16X2S_replacement.addNet(o0);
				RAM16X2S_replacement.addNet(o1_c);
				RAM16X2S_replacement.addNet(o1);
				RAM16X2S_replacement.addNet(wire1);
				RAM16X2S_replacement.addNet(wire2);
				RAM16X2S_replacement.addNet(wire3);
				RAM16X2S_replacement.addNet(wire4);
				RAM16X2S_replacement.addNet(wire5);
				RAM16X2S_replacement.addNet(wire6);
				RAM16X2S_replacement.addNet(wire7);
				RAM16X2S_replacement.addNet(wire8);
				RAM16X2S_replacement.addNet(wire9);
				RAM16X2S_replacement.addNet(wire10);
				RAM16X2S_replacement.addNet(wire11);
				RAM16X2S_replacement.addNet(wire12);
				RAM16X2S_replacement.addNet(wire13);
				RAM16X2S_replacement.addNet(wire14);
				RAM16X2S_replacement.addNet(wire15);
				RAM16X2S_replacement.addNet(wire16);
				RAM16X2S_replacement.addNet(wire17);
				RAM16X2S_replacement.addNet(wire18);
				RAM16X2S_replacement.addNet(wire19);
				RAM16X2S_replacement.addNet(wire20);
				RAM16X2S_replacement.addNet(wire21);
				RAM16X2S_replacement.addNet(wire22);
				RAM16X2S_replacement.addNet(wire23);
				RAM16X2S_replacement.addNet(wire24);
				RAM16X2S_replacement.addNet(wire25);
				RAM16X2S_replacement.addNet(wire26);
				RAM16X2S_replacement.addNet(wire27);
				RAM16X2S_replacement.addNet(wire28);
				RAM16X2S_replacement.addNet(wire29);
				RAM16X2S_replacement.addNet(wire30);
				RAM16X2S_replacement.addNet(wire31);
				RAM16X2S_replacement.addNet(wire32);
				RAM16X2S_replacement.addNet(net1);
				RAM16X2S_replacement.addNet(net2);
				RAM16X2S_replacement.addNet(net3);
				RAM16X2S_replacement.addNet(net4);
				RAM16X2S_replacement.addNet(net5);
				RAM16X2S_replacement.addNet(net6);
				RAM16X2S_replacement.addNet(net7);
				RAM16X2S_replacement.addNet(net8);
				RAM16X2S_replacement.addNet(net9);
				RAM16X2S_replacement.addNet(net10);
				RAM16X2S_replacement.addNet(net11);
				RAM16X2S_replacement.addNet(net12);
				RAM16X2S_replacement.addNet(net13);
				RAM16X2S_replacement.addNet(net14);
				RAM16X2S_replacement.addNet(net15);
				RAM16X2S_replacement.addNet(net16);
				RAM16X2S_replacement.addNet(net17);
				RAM16X2S_replacement.addNet(net18);
				RAM16X2S_replacement.addNet(net19);
				RAM16X2S_replacement.addNet(net20);
				RAM16X2S_replacement.addNet(net21);
				RAM16X2S_replacement.addNet(net22);
				RAM16X2S_replacement.addNet(net23);
				RAM16X2S_replacement.addNet(net24);
				RAM16X2S_replacement.addNet(net25);
				RAM16X2S_replacement.addNet(net26);
				RAM16X2S_replacement.addNet(net27);
				RAM16X2S_replacement.addNet(net28);
				RAM16X2S_replacement.addNet(net29);
				RAM16X2S_replacement.addNet(net30);
				RAM16X2S_replacement.addNet(e1);
				RAM16X2S_replacement.addNet(e2);
				RAM16X2S_replacement.addNet(e3);
				RAM16X2S_replacement.addNet(e4);
				RAM16X2S_replacement.addNet(e5);
				RAM16X2S_replacement.addNet(e6);
				RAM16X2S_replacement.addNet(e7);
				RAM16X2S_replacement.addNet(e8);
				RAM16X2S_replacement.addNet(e9);
				RAM16X2S_replacement.addNet(e10);
				RAM16X2S_replacement.addNet(e11);
				RAM16X2S_replacement.addNet(e12);
				RAM16X2S_replacement.addNet(e13);
				RAM16X2S_replacement.addNet(e14);
				RAM16X2S_replacement.addNet(e15);
				RAM16X2S_replacement.addNet(e16);
				
				/******Instances******/
				EdifCellInstance WE_ibuf = new EdifCellInstance("WE_ibuf", RAM16X2S_replacement, IBUF);
				EdifCellInstance D0_ibuf = new EdifCellInstance("D0_ibuf", RAM16X2S_replacement, IBUF);
				EdifCellInstance D1_ibuf = new EdifCellInstance("D1_ibuf", RAM16X2S_replacement, IBUF);
				EdifCellInstance WCLK_ibuf = new EdifCellInstance("WCLK_ibuf", RAM16X2S_replacement, BUFGP);
				EdifCellInstance A0_ibuf = new EdifCellInstance("A0_ibuf", RAM16X2S_replacement, IBUF);
				EdifCellInstance A1_ibuf = new EdifCellInstance("A1_ibuf", RAM16X2S_replacement, IBUF);
				EdifCellInstance A2_ibuf = new EdifCellInstance("A2_ibuf", RAM16X2S_replacement, IBUF);
				EdifCellInstance A3_ibuf = new EdifCellInstance("A3_ibuf", RAM16X2S_replacement, IBUF);
				EdifCellInstance O0_obuf = new EdifCellInstance("O0_obuf", RAM16X2S_replacement, OBUF);
				EdifCellInstance O1_obuf = new EdifCellInstance("O1_obuf", RAM16X2S_replacement, OBUF);
				EdifCellInstance FDE1 = new EdifCellInstance("FDE1", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE2 = new EdifCellInstance("FDE2", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE3 = new EdifCellInstance("FDE3", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE4 = new EdifCellInstance("FDE4", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE5 = new EdifCellInstance("FDE5", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE6 = new EdifCellInstance("FDE6", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE7 = new EdifCellInstance("FDE7", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE8 = new EdifCellInstance("FDE8", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE9 = new EdifCellInstance("FDE9", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE10 = new EdifCellInstance("FDE10", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE11 = new EdifCellInstance("FDE11", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE12 = new EdifCellInstance("FDE12", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE13 = new EdifCellInstance("FDE13", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE14 = new EdifCellInstance("FDE14", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE15 = new EdifCellInstance("FDE15", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE16 = new EdifCellInstance("FDE16", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE17 = new EdifCellInstance("FDE17", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE18 = new EdifCellInstance("FDE18", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE19 = new EdifCellInstance("FDE19", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE20 = new EdifCellInstance("FDE20", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE21 = new EdifCellInstance("FDE21", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE22 = new EdifCellInstance("FDE22", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE23 = new EdifCellInstance("FDE23", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE24 = new EdifCellInstance("FDE24", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE25 = new EdifCellInstance("FDE25", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE26 = new EdifCellInstance("FDE26", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE27 = new EdifCellInstance("FDE27", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE28 = new EdifCellInstance("FDE28", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE29 = new EdifCellInstance("FDE29", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE30 = new EdifCellInstance("FDE30", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE31 = new EdifCellInstance("FDE31", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE32 = new EdifCellInstance("FDE32", RAM16X2S_replacement, FDE);
				EdifCellInstance LUT2_1 = new EdifCellInstance("LUT2_1", RAM16X2S_replacement, LUT2);
				EdifCellInstance LUT2_2 = new EdifCellInstance("LUT2_2", RAM16X2S_replacement, LUT2);
				EdifCellInstance LUT3_1 = new EdifCellInstance("LUT3_1", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_2 = new EdifCellInstance("LUT3_2", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_3 = new EdifCellInstance("LUT3_3", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_4 = new EdifCellInstance("LUT3_4", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_5 = new EdifCellInstance("LUT3_5", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_6 = new EdifCellInstance("LUT3_6", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_7 = new EdifCellInstance("LUT3_7", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_8 = new EdifCellInstance("LUT3_8", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_9 = new EdifCellInstance("LUT3_9", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_10 = new EdifCellInstance("LUT3_10", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_11 = new EdifCellInstance("LUT3_11", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_12 = new EdifCellInstance("LUT3_12", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_13 = new EdifCellInstance("LUT3_13", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_14 = new EdifCellInstance("LUT3_14", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_15 = new EdifCellInstance("LUT3_15", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_16 = new EdifCellInstance("LUT3_16", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_17 = new EdifCellInstance("LUT3_17", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_18 = new EdifCellInstance("LUT3_18", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT4_1 = new EdifCellInstance("LUT4_1", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_2 = new EdifCellInstance("LUT4_2", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_3 = new EdifCellInstance("LUT4_3", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_4 = new EdifCellInstance("LUT4_4", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_5 = new EdifCellInstance("LUT4_5", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_6 = new EdifCellInstance("LUT4_6", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_7 = new EdifCellInstance("LUT4_7", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_8 = new EdifCellInstance("LUT4_8", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_9 = new EdifCellInstance("LUT4_9", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_10 = new EdifCellInstance("LUT4_10", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_11 = new EdifCellInstance("LUT4_11", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_12 = new EdifCellInstance("LUT4_12", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_13 = new EdifCellInstance("LUT4_13", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_14 = new EdifCellInstance("LUT4_14", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_15 = new EdifCellInstance("LUT4_15", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_16 = new EdifCellInstance("LUT4_16", RAM16X2S_replacement, LUT4);
				EdifCellInstance MUXF5_1 = new EdifCellInstance("MUXF5_1", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF5_2 = new EdifCellInstance("MUXF5_2", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF5_3 = new EdifCellInstance("MUXF5_3", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF5_4 = new EdifCellInstance("MUXF5_4", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF5_5 = new EdifCellInstance("MUXF5_5", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF5_6 = new EdifCellInstance("MUXF5_6", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF5_7 = new EdifCellInstance("MUXF5_7", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF5_8 = new EdifCellInstance("MUXF5_8", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF6_1 = new EdifCellInstance("MUXF6_1", RAM16X2S_replacement, MUXF6);
				EdifCellInstance MUXF6_2 = new EdifCellInstance("MUXF6_2", RAM16X2S_replacement, MUXF6);
				EdifCellInstance MUXF6_3 = new EdifCellInstance("MUXF6_3", RAM16X2S_replacement, MUXF6);
				EdifCellInstance MUXF6_4 = new EdifCellInstance("MUXF6_4", RAM16X2S_replacement, MUXF6);
				RAM16X2S_replacement.addSubCell(WE_ibuf);
				RAM16X2S_replacement.addSubCell(D0_ibuf);
				RAM16X2S_replacement.addSubCell(D1_ibuf);
				RAM16X2S_replacement.addSubCell(WCLK_ibuf);
				RAM16X2S_replacement.addSubCell(A0_ibuf);
				RAM16X2S_replacement.addSubCell(A1_ibuf);
				RAM16X2S_replacement.addSubCell(A2_ibuf);
				RAM16X2S_replacement.addSubCell(A3_ibuf);
				RAM16X2S_replacement.addSubCell(O0_obuf);
				RAM16X2S_replacement.addSubCell(O1_obuf);
				RAM16X2S_replacement.addSubCell(FDE1);
				RAM16X2S_replacement.addSubCell(FDE2);
				RAM16X2S_replacement.addSubCell(FDE3);
				RAM16X2S_replacement.addSubCell(FDE4);
				RAM16X2S_replacement.addSubCell(FDE5);
				RAM16X2S_replacement.addSubCell(FDE6);
				RAM16X2S_replacement.addSubCell(FDE7);
				RAM16X2S_replacement.addSubCell(FDE8);
				RAM16X2S_replacement.addSubCell(FDE9);
				RAM16X2S_replacement.addSubCell(FDE10);
				RAM16X2S_replacement.addSubCell(FDE11);
				RAM16X2S_replacement.addSubCell(FDE12);
				RAM16X2S_replacement.addSubCell(FDE13);
				RAM16X2S_replacement.addSubCell(FDE14);
				RAM16X2S_replacement.addSubCell(FDE15);
				RAM16X2S_replacement.addSubCell(FDE16);
				RAM16X2S_replacement.addSubCell(FDE17);
				RAM16X2S_replacement.addSubCell(FDE18);
				RAM16X2S_replacement.addSubCell(FDE19);
				RAM16X2S_replacement.addSubCell(FDE20);
				RAM16X2S_replacement.addSubCell(FDE21);
				RAM16X2S_replacement.addSubCell(FDE22);
				RAM16X2S_replacement.addSubCell(FDE23);
				RAM16X2S_replacement.addSubCell(FDE24);
				RAM16X2S_replacement.addSubCell(FDE25);
				RAM16X2S_replacement.addSubCell(FDE26);
				RAM16X2S_replacement.addSubCell(FDE27);
				RAM16X2S_replacement.addSubCell(FDE28);
				RAM16X2S_replacement.addSubCell(FDE29);
				RAM16X2S_replacement.addSubCell(FDE30);
				RAM16X2S_replacement.addSubCell(FDE31);
				RAM16X2S_replacement.addSubCell(FDE32);
				RAM16X2S_replacement.addSubCell(LUT2_1);
				RAM16X2S_replacement.addSubCell(LUT2_2);
				RAM16X2S_replacement.addSubCell(LUT3_1);
				RAM16X2S_replacement.addSubCell(LUT3_2);
				RAM16X2S_replacement.addSubCell(LUT3_3);
				RAM16X2S_replacement.addSubCell(LUT3_4);
				RAM16X2S_replacement.addSubCell(LUT3_5);
				RAM16X2S_replacement.addSubCell(LUT3_6);
				RAM16X2S_replacement.addSubCell(LUT3_7);
				RAM16X2S_replacement.addSubCell(LUT3_8);
				RAM16X2S_replacement.addSubCell(LUT3_9);
				RAM16X2S_replacement.addSubCell(LUT3_10);
				RAM16X2S_replacement.addSubCell(LUT3_11);
				RAM16X2S_replacement.addSubCell(LUT3_12);
				RAM16X2S_replacement.addSubCell(LUT3_13);
				RAM16X2S_replacement.addSubCell(LUT3_14);
				RAM16X2S_replacement.addSubCell(LUT3_15);
				RAM16X2S_replacement.addSubCell(LUT3_16);
				RAM16X2S_replacement.addSubCell(LUT3_17);
				RAM16X2S_replacement.addSubCell(LUT3_18);
				RAM16X2S_replacement.addSubCell(LUT4_1);
				RAM16X2S_replacement.addSubCell(LUT4_2);
				RAM16X2S_replacement.addSubCell(LUT4_3);
				RAM16X2S_replacement.addSubCell(LUT4_4);
				RAM16X2S_replacement.addSubCell(LUT4_5);
				RAM16X2S_replacement.addSubCell(LUT4_6);
				RAM16X2S_replacement.addSubCell(LUT4_7);
				RAM16X2S_replacement.addSubCell(LUT4_8);
				RAM16X2S_replacement.addSubCell(LUT4_9);
				RAM16X2S_replacement.addSubCell(LUT4_10);
				RAM16X2S_replacement.addSubCell(LUT4_11);
				RAM16X2S_replacement.addSubCell(LUT4_12);
				RAM16X2S_replacement.addSubCell(LUT4_13);
				RAM16X2S_replacement.addSubCell(LUT4_14);
				RAM16X2S_replacement.addSubCell(LUT4_15);
				RAM16X2S_replacement.addSubCell(LUT4_16);
				RAM16X2S_replacement.addSubCell(MUXF5_1);
				RAM16X2S_replacement.addSubCell(MUXF5_2);
				RAM16X2S_replacement.addSubCell(MUXF5_3);
				RAM16X2S_replacement.addSubCell(MUXF5_4);
				RAM16X2S_replacement.addSubCell(MUXF5_5);
				RAM16X2S_replacement.addSubCell(MUXF5_6);
				RAM16X2S_replacement.addSubCell(MUXF5_7);
				RAM16X2S_replacement.addSubCell(MUXF5_8);
				RAM16X2S_replacement.addSubCell(MUXF6_1);
				RAM16X2S_replacement.addSubCell(MUXF6_2);
				RAM16X2S_replacement.addSubCell(MUXF6_3);
				RAM16X2S_replacement.addSubCell(MUXF6_4);
				
				/******Interface******/
				EdifCellInterface RAM16X2S_replacement_interface = new EdifCellInterface(RAM16X2S_replacement);
				EdifCellInterface WE_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D0_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D1_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface WCLK_ibuf_interface = new EdifCellInterface(BUFGP);
				EdifCellInterface A0_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A1_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A2_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A3_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface O0_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface O1_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface FDE1_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE2_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE3_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE4_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE5_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE6_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE7_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE8_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE9_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE10_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE11_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE12_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE13_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE14_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE15_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE16_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE17_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE18_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE19_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE20_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE21_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE22_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE23_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE24_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE25_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE26_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE27_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE28_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE29_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE30_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE31_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE32_interface = new EdifCellInterface(FDE);
				EdifCellInterface LUT2_1_interface = new EdifCellInterface(LUT2);
				EdifCellInterface LUT2_2_interface = new EdifCellInterface(LUT2);
				EdifCellInterface LUT3_1_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_2_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_3_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_4_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_5_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_6_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_7_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_8_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_9_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_10_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_11_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_12_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_13_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_14_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_15_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_16_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_17_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_18_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT4_1_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_2_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_3_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_4_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_5_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_6_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_7_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_8_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_9_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_10_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_11_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_12_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_13_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_14_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_15_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_16_interface = new EdifCellInterface(LUT4);
				EdifCellInterface MUXF5_1_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_2_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_3_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_4_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_5_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_6_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_7_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_8_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF6_1_interface = new EdifCellInterface(MUXF6);
				EdifCellInterface MUXF6_2_interface = new EdifCellInterface(MUXF6);
				EdifCellInterface MUXF6_3_interface = new EdifCellInterface(MUXF6);
				EdifCellInterface MUXF6_4_interface = new EdifCellInterface(MUXF6);
				
				/******Ports******/
				//RAM16X2S_replacement
				EdifPort RAM16X2S_replacement_interface_WE = new EdifPort(RAM16X2S_replacement_interface, "WE", 1, 1);
				EdifPort RAM16X2S_replacement_interface_D0 = new EdifPort(RAM16X2S_replacement_interface, "D0", 1, 1);
				EdifPort RAM16X2S_replacement_interface_D1 = new EdifPort(RAM16X2S_replacement_interface, "D1", 1, 1);
				EdifPort RAM16X2S_replacement_interface_WCLK = new EdifPort(RAM16X2S_replacement_interface, "WCLK", 1, 1);
				EdifPort RAM16X2S_replacement_interface_A0 = new EdifPort(RAM16X2S_replacement_interface, "A0", 1, 1);
				EdifPort RAM16X2S_replacement_interface_A1 = new EdifPort(RAM16X2S_replacement_interface, "A1", 1, 1);
				EdifPort RAM16X2S_replacement_interface_A2 = new EdifPort(RAM16X2S_replacement_interface, "A2", 1, 1);
				EdifPort RAM16X2S_replacement_interface_A3 = new EdifPort(RAM16X2S_replacement_interface, "A3", 1, 1);
				EdifPort RAM16X2S_replacement_interface_O0 = new EdifPort(RAM16X2S_replacement_interface, "O0", 1, 2);
				EdifPort RAM16X2S_replacement_interface_O1 = new EdifPort(RAM16X2S_replacement_interface, "O1", 1, 2);
				RAM16X2S_replacement_interface.addPort("WE", 1, 1);
				RAM16X2S_replacement_interface.addPort("D0", 1, 1);
				RAM16X2S_replacement_interface.addPort("D1", 1, 1);
				RAM16X2S_replacement_interface.addPort("WCLK", 1, 1);
				RAM16X2S_replacement_interface.addPort("A0", 1, 1);
				RAM16X2S_replacement_interface.addPort("A1", 1, 1);
				RAM16X2S_replacement_interface.addPort("A2", 1, 1);
				RAM16X2S_replacement_interface.addPort("A3", 1, 1);
				RAM16X2S_replacement_interface.addPort("O0", 1, 2);
				RAM16X2S_replacement_interface.addPort("O1", 1, 2);
				//WE_ibuf
				EdifPort WE_ibuf_interface_I = new EdifPort(WE_ibuf_interface, "I", 1, 1);
				EdifPort WE_ibuf_interface_O = new EdifPort(WE_ibuf_interface, "O", 1, 2);
				WE_ibuf_interface.addPort("I", 1, 1);
				WE_ibuf_interface.addPort("O", 1, 2);
				//D0_ibuf
				EdifPort D0_ibuf_interface_I = new EdifPort(D0_ibuf_interface, "I", 1, 1);
				EdifPort D0_ibuf_interface_O = new EdifPort(D0_ibuf_interface, "O", 1, 2);
				D0_ibuf_interface.addPort("I", 1, 1);
				D0_ibuf_interface.addPort("O", 1, 2);
				//D1_ibuf
				EdifPort D1_ibuf_interface_I = new EdifPort(D1_ibuf_interface, "I", 1, 1);
				EdifPort D1_ibuf_interface_O = new EdifPort(D1_ibuf_interface, "O", 1, 2);
				D1_ibuf_interface.addPort("I", 1, 1);
				D1_ibuf_interface.addPort("O", 1, 2);
				//WCLK_ibuf
				EdifPort WCLK_ibuf_interface_I = new EdifPort(WCLK_ibuf_interface, "I", 1, 1);
				EdifPort WCLK_ibuf_interface_O = new EdifPort(WCLK_ibuf_interface, "O", 1, 2);
				WCLK_ibuf_interface.addPort("I", 1, 1);
				WCLK_ibuf_interface.addPort("O", 1, 2);
				//A0_ibuf
				EdifPort A0_ibuf_interface_I = new EdifPort(A0_ibuf_interface, "I", 1, 1);
				EdifPort A0_ibuf_interface_O = new EdifPort(A0_ibuf_interface, "O", 1, 2);
				A0_ibuf_interface.addPort("I", 1, 1);
				A0_ibuf_interface.addPort("O", 1, 2);
				//A1_ibuf
				EdifPort A1_ibuf_interface_I = new EdifPort(A1_ibuf_interface, "I", 1, 1);
				EdifPort A1_ibuf_interface_O = new EdifPort(A1_ibuf_interface, "O", 1, 2);
				A1_ibuf_interface.addPort("I", 1, 1);
				A1_ibuf_interface.addPort("O", 1, 2);
				//A2_ibuf
				EdifPort A2_ibuf_interface_I = new EdifPort(A2_ibuf_interface, "I", 1, 1);
				EdifPort A2_ibuf_interface_O = new EdifPort(A2_ibuf_interface, "O", 1, 2);
				A2_ibuf_interface.addPort("I", 1, 1);
				A2_ibuf_interface.addPort("O", 1, 2);
				//A3_ibuf
				EdifPort A3_ibuf_interface_I = new EdifPort(A3_ibuf_interface, "I", 1, 1);
				EdifPort A3_ibuf_interface_O = new EdifPort(A3_ibuf_interface, "O", 1, 2);
				A3_ibuf_interface.addPort("I", 1, 1);
				A3_ibuf_interface.addPort("O", 1, 2);
				//O0_obuf
				EdifPort O0_obuf_interface_I = new EdifPort(O0_obuf_interface, "I", 1, 1);
				EdifPort O0_obuf_interface_O = new EdifPort(O0_obuf_interface, "O", 1, 2);
				O0_obuf_interface.addPort("I", 1, 1);
				O0_obuf_interface.addPort("O", 1, 2);
				//O1_obuf
				EdifPort O1_obuf_interface_I = new EdifPort(O1_obuf_interface, "I", 1, 1);
				EdifPort O1_obuf_interface_O = new EdifPort(O1_obuf_interface, "O", 1, 2);
				O1_obuf_interface.addPort("I", 1, 1);
				O1_obuf_interface.addPort("O", 1, 2);
				//FDE1
				EdifPort FDE1_interface_C = new EdifPort(FDE1_interface, "C", 1, 1);
				EdifPort FDE1_interface_D = new EdifPort(FDE1_interface, "D", 1, 1);
				EdifPort FDE1_interface_CE = new EdifPort(FDE1_interface, "CE", 1, 1);
				EdifPort FDE1_interface_Q = new EdifPort(FDE1_interface, "Q", 1, 2);
				FDE1_interface.addPort("C", 1, 1);
				FDE1_interface.addPort("D", 1, 1);
				FDE1_interface.addPort("CE", 1, 1);
				FDE1_interface.addPort("Q", 1, 2);
				//FDE2
				EdifPort FDE2_interface_C = new EdifPort(FDE2_interface, "C", 1, 1);
				EdifPort FDE2_interface_D = new EdifPort(FDE2_interface, "D", 1, 1);
				EdifPort FDE2_interface_CE = new EdifPort(FDE2_interface, "CE", 1, 1);
				EdifPort FDE2_interface_Q = new EdifPort(FDE2_interface, "Q", 1, 2);
				FDE2_interface.addPort("C", 1, 1);
				FDE2_interface.addPort("D", 1, 1);
				FDE2_interface.addPort("CE", 1, 1);
				FDE2_interface.addPort("Q", 1, 2);
				//FDE3
				EdifPort FDE3_interface_C = new EdifPort(FDE3_interface, "C", 1, 1);
				EdifPort FDE3_interface_D = new EdifPort(FDE3_interface, "D", 1, 1);
				EdifPort FDE3_interface_CE = new EdifPort(FDE3_interface, "CE", 1, 1);
				EdifPort FDE3_interface_Q = new EdifPort(FDE3_interface, "Q", 1, 2);
				FDE3_interface.addPort("C", 1, 1);
				FDE3_interface.addPort("D", 1, 1);
				FDE3_interface.addPort("CE", 1, 1);
				FDE3_interface.addPort("Q", 1, 2);
				//FDE4
				EdifPort FDE4_interface_C = new EdifPort(FDE4_interface, "C", 1, 1);
				EdifPort FDE4_interface_D = new EdifPort(FDE4_interface, "D", 1, 1);
				EdifPort FDE4_interface_CE = new EdifPort(FDE4_interface, "CE", 1, 1);
				EdifPort FDE4_interface_Q = new EdifPort(FDE4_interface, "Q", 1, 2);
				FDE4_interface.addPort("C", 1, 1);
				FDE4_interface.addPort("D", 1, 1);
				FDE4_interface.addPort("CE", 1, 1);
				FDE4_interface.addPort("Q", 1, 2);
				//FDE5
				EdifPort FDE5_interface_C = new EdifPort(FDE5_interface, "C", 1, 1);
				EdifPort FDE5_interface_D = new EdifPort(FDE5_interface, "D", 1, 1);
				EdifPort FDE5_interface_CE = new EdifPort(FDE5_interface, "CE", 1, 1);
				EdifPort FDE5_interface_Q = new EdifPort(FDE5_interface, "Q", 1, 2);
				FDE5_interface.addPort("C", 1, 1);
				FDE5_interface.addPort("D", 1, 1);
				FDE5_interface.addPort("CE", 1, 1);
				FDE5_interface.addPort("Q", 1, 2);
				//FDE6
				EdifPort FDE6_interface_C = new EdifPort(FDE6_interface, "C", 1, 1);
				EdifPort FDE6_interface_D = new EdifPort(FDE6_interface, "D", 1, 1);
				EdifPort FDE6_interface_CE = new EdifPort(FDE6_interface, "CE", 1, 1);
				EdifPort FDE6_interface_Q = new EdifPort(FDE6_interface, "Q", 1, 2);
				FDE6_interface.addPort("C", 1, 1);
				FDE6_interface.addPort("D", 1, 1);
				FDE6_interface.addPort("CE", 1, 1);
				FDE6_interface.addPort("Q", 1, 2);
				//FDE7
				EdifPort FDE7_interface_C = new EdifPort(FDE7_interface, "C", 1, 1);
				EdifPort FDE7_interface_D = new EdifPort(FDE7_interface, "D", 1, 1);
				EdifPort FDE7_interface_CE = new EdifPort(FDE7_interface, "CE", 1, 1);
				EdifPort FDE7_interface_Q = new EdifPort(FDE7_interface, "Q", 1, 2);
				FDE7_interface.addPort("C", 1, 1);
				FDE7_interface.addPort("D", 1, 1);
				FDE7_interface.addPort("CE", 1, 1);
				FDE7_interface.addPort("Q", 1, 2);
				//FDE8
				EdifPort FDE8_interface_C = new EdifPort(FDE8_interface, "C", 1, 1);
				EdifPort FDE8_interface_D = new EdifPort(FDE8_interface, "D", 1, 1);
				EdifPort FDE8_interface_CE = new EdifPort(FDE8_interface, "CE", 1, 1);
				EdifPort FDE8_interface_Q = new EdifPort(FDE8_interface, "Q", 1, 2);
				FDE8_interface.addPort("C", 1, 1);
				FDE8_interface.addPort("D", 1, 1);
				FDE8_interface.addPort("CE", 1, 1);
				FDE8_interface.addPort("Q", 1, 2);
				//FDE9
				EdifPort FDE9_interface_C = new EdifPort(FDE9_interface, "C", 1, 1);
				EdifPort FDE9_interface_D = new EdifPort(FDE9_interface, "D", 1, 1);
				EdifPort FDE9_interface_CE = new EdifPort(FDE9_interface, "CE", 1, 1);
				EdifPort FDE9_interface_Q = new EdifPort(FDE9_interface, "Q", 1, 2);
				FDE9_interface.addPort("C", 1, 1);
				FDE9_interface.addPort("D", 1, 1);
				FDE9_interface.addPort("CE", 1, 1);
				FDE9_interface.addPort("Q", 1, 2);
				//FDE10
				EdifPort FDE10_interface_C = new EdifPort(FDE10_interface, "C", 1, 1);
				EdifPort FDE10_interface_D = new EdifPort(FDE10_interface, "D", 1, 1);
				EdifPort FDE10_interface_CE = new EdifPort(FDE10_interface, "CE", 1, 1);
				EdifPort FDE10_interface_Q = new EdifPort(FDE10_interface, "Q", 1, 2);
				FDE10_interface.addPort("C", 1, 1);
				FDE10_interface.addPort("D", 1, 1);
				FDE10_interface.addPort("CE", 1, 1);
				FDE10_interface.addPort("Q", 1, 2);
				//FDE11
				EdifPort FDE11_interface_C = new EdifPort(FDE11_interface, "C", 1, 1);
				EdifPort FDE11_interface_D = new EdifPort(FDE11_interface, "D", 1, 1);
				EdifPort FDE11_interface_CE = new EdifPort(FDE11_interface, "CE", 1, 1);
				EdifPort FDE11_interface_Q = new EdifPort(FDE11_interface, "Q", 1, 2);
				FDE11_interface.addPort("C", 1, 1);
				FDE11_interface.addPort("D", 1, 1);
				FDE11_interface.addPort("CE", 1, 1);
				FDE11_interface.addPort("Q", 1, 2);
				//FDE12
				EdifPort FDE12_interface_C = new EdifPort(FDE12_interface, "C", 1, 1);
				EdifPort FDE12_interface_D = new EdifPort(FDE12_interface, "D", 1, 1);
				EdifPort FDE12_interface_CE = new EdifPort(FDE13_interface, "CE", 1, 1);
				EdifPort FDE12_interface_Q = new EdifPort(FDE12_interface, "Q", 1, 2);
				FDE12_interface.addPort("C", 1, 1);
				FDE12_interface.addPort("D", 1, 1);
				FDE12_interface.addPort("CE", 1, 1);
				FDE12_interface.addPort("Q", 1, 2);
				//FDE13
				EdifPort FDE13_interface_C = new EdifPort(FDE13_interface, "C", 1, 1);
				EdifPort FDE13_interface_D = new EdifPort(FDE13_interface, "D", 1, 1);
				EdifPort FDE13_interface_CE = new EdifPort(FDE13_interface, "CE", 1, 1);
				EdifPort FDE13_interface_Q = new EdifPort(FDE13_interface, "Q", 1, 2);
				FDE13_interface.addPort("C", 1, 1);
				FDE13_interface.addPort("D", 1, 1);
				FDE13_interface.addPort("CE", 1, 1);
				FDE13_interface.addPort("Q", 1, 2);
				//FDE14
				EdifPort FDE14_interface_C = new EdifPort(FDE14_interface, "C", 1, 1);
				EdifPort FDE14_interface_D = new EdifPort(FDE14_interface, "D", 1, 1);
				EdifPort FDE14_interface_CE = new EdifPort(FDE15_interface, "CE", 1, 1);
				EdifPort FDE14_interface_Q = new EdifPort(FDE14_interface, "Q", 1, 2);
				FDE14_interface.addPort("C", 1, 1);
				FDE14_interface.addPort("D", 1, 1);
				FDE14_interface.addPort("CE", 1, 1);
				FDE14_interface.addPort("Q", 1, 2);
				//FDE15
				EdifPort FDE15_interface_C = new EdifPort(FDE15_interface, "C", 1, 1);
				EdifPort FDE15_interface_D = new EdifPort(FDE15_interface, "D", 1, 1);
				EdifPort FDE15_interface_CE = new EdifPort(FDE15_interface, "CE", 1, 1);
				EdifPort FDE15_interface_Q = new EdifPort(FDE15_interface, "Q", 1, 2);
				FDE15_interface.addPort("C", 1, 1);
				FDE15_interface.addPort("D", 1, 1);
				FDE15_interface.addPort("CE", 1, 1);
				FDE15_interface.addPort("Q", 1, 2);
				//FDE16
				EdifPort FDE16_interface_C = new EdifPort(FDE16_interface, "C", 1, 1);
				EdifPort FDE16_interface_D = new EdifPort(FDE16_interface, "D", 1, 1);
				EdifPort FDE16_interface_CE = new EdifPort(FDE16_interface, "CE", 1, 1);
				EdifPort FDE16_interface_Q = new EdifPort(FDE16_interface, "Q", 1, 2);
				FDE16_interface.addPort("C", 1, 1);
				FDE16_interface.addPort("D", 1, 1);
				FDE16_interface.addPort("CE", 1, 1);
				FDE16_interface.addPort("Q", 1, 2);
				//FDE17
				EdifPort FDE17_interface_C = new EdifPort(FDE17_interface, "C", 1, 1);
				EdifPort FDE17_interface_D = new EdifPort(FDE17_interface, "D", 1, 1);
				EdifPort FDE17_interface_CE = new EdifPort(FDE17_interface, "CE", 1, 1);
				EdifPort FDE17_interface_Q = new EdifPort(FDE17_interface, "Q", 1, 2);
				FDE17_interface.addPort("C", 1, 1);
				FDE17_interface.addPort("D", 1, 1);
				FDE17_interface.addPort("CE", 1, 1);
				FDE17_interface.addPort("Q", 1, 2);
				//FDE18
				EdifPort FDE18_interface_C = new EdifPort(FDE18_interface, "C", 1, 1);
				EdifPort FDE18_interface_D = new EdifPort(FDE18_interface, "D", 1, 1);
				EdifPort FDE18_interface_CE = new EdifPort(FDE18_interface, "CE", 1, 1);
				EdifPort FDE18_interface_Q = new EdifPort(FDE18_interface, "Q", 1, 2);
				FDE18_interface.addPort("C", 1, 1);
				FDE18_interface.addPort("D", 1, 1);
				FDE18_interface.addPort("CE", 1, 1);
				FDE18_interface.addPort("Q", 1, 2);
				//FDE19
				EdifPort FDE19_interface_C = new EdifPort(FDE19_interface, "C", 1, 1);
				EdifPort FDE19_interface_D = new EdifPort(FDE19_interface, "D", 1, 1);
				EdifPort FDE19_interface_CE = new EdifPort(FDE19_interface, "CE", 1, 1);
				EdifPort FDE19_interface_Q = new EdifPort(FDE19_interface, "Q", 1, 2);
				FDE19_interface.addPort("C", 1, 1);
				FDE19_interface.addPort("D", 1, 1);
				FDE19_interface.addPort("CE", 1, 1);
				FDE19_interface.addPort("Q", 1, 2);
				//FDE20
				EdifPort FDE20_interface_C = new EdifPort(FDE20_interface, "C", 1, 1);
				EdifPort FDE20_interface_D = new EdifPort(FDE20_interface, "D", 1, 1);
				EdifPort FDE20_interface_CE = new EdifPort(FDE20_interface, "CE", 1, 1);
				EdifPort FDE20_interface_Q = new EdifPort(FDE20_interface, "Q", 1, 2);
				FDE20_interface.addPort("C", 1, 1);
				FDE20_interface.addPort("D", 1, 1);
				FDE20_interface.addPort("CE", 1, 1);
				FDE20_interface.addPort("Q", 1, 2);
				//FDE21
				EdifPort FDE21_interface_C = new EdifPort(FDE21_interface, "C", 1, 1);
				EdifPort FDE21_interface_D = new EdifPort(FDE21_interface, "D", 1, 1);
				EdifPort FDE21_interface_CE = new EdifPort(FDE21_interface, "CE", 1, 1);
				EdifPort FDE21_interface_Q = new EdifPort(FDE21_interface, "Q", 1, 2);
				FDE21_interface.addPort("C", 1, 1);
				FDE21_interface.addPort("D", 1, 1);
				FDE21_interface.addPort("CE", 1, 1);
				FDE21_interface.addPort("Q", 1, 2);
				//FDE22
				EdifPort FDE22_interface_C = new EdifPort(FDE22_interface, "C", 1, 1);
				EdifPort FDE22_interface_D = new EdifPort(FDE22_interface, "D", 1, 1);
				EdifPort FDE22_interface_CE = new EdifPort(FDE22_interface, "CE", 1, 1);
				EdifPort FDE22_interface_Q = new EdifPort(FDE22_interface, "Q", 1, 2);
				FDE22_interface.addPort("C", 1, 1);
				FDE22_interface.addPort("D", 1, 1);
				FDE22_interface.addPort("CE", 1, 1);
				FDE22_interface.addPort("Q", 1, 2);
				//FDE23
				EdifPort FDE23_interface_C = new EdifPort(FDE23_interface, "C", 1, 1);
				EdifPort FDE23_interface_D = new EdifPort(FDE23_interface, "D", 1, 1);
				EdifPort FDE23_interface_CE = new EdifPort(FDE23_interface, "CE", 1, 1);
				EdifPort FDE23_interface_Q = new EdifPort(FDE23_interface, "Q", 1, 2);
				FDE23_interface.addPort("C", 1, 1);
				FDE23_interface.addPort("D", 1, 1);
				FDE23_interface.addPort("CE", 1, 1);
				FDE23_interface.addPort("Q", 1, 2);
				//FDE24
				EdifPort FDE24_interface_C = new EdifPort(FDE24_interface, "C", 1, 1);
				EdifPort FDE24_interface_D = new EdifPort(FDE24_interface, "D", 1, 1);
				EdifPort FDE24_interface_CE = new EdifPort(FDE24_interface, "CE", 1, 1);
				EdifPort FDE24_interface_Q = new EdifPort(FDE24_interface, "Q", 1, 2);
				FDE24_interface.addPort("C", 1, 1);
				FDE24_interface.addPort("D", 1, 1);
				FDE24_interface.addPort("CE", 1, 1);
				FDE24_interface.addPort("Q", 1, 2);
				//FDE25
				EdifPort FDE25_interface_C = new EdifPort(FDE25_interface, "C", 1, 1);
				EdifPort FDE25_interface_D = new EdifPort(FDE25_interface, "D", 1, 1);
				EdifPort FDE25_interface_CE = new EdifPort(FDE25_interface, "CE", 1, 1);
				EdifPort FDE25_interface_Q = new EdifPort(FDE25_interface, "Q", 1, 2);
				FDE25_interface.addPort("C", 1, 1);
				FDE25_interface.addPort("D", 1, 1);
				FDE25_interface.addPort("CE", 1, 1);
				FDE25_interface.addPort("Q", 1, 2);
				//FDE26
				EdifPort FDE26_interface_C = new EdifPort(FDE26_interface, "C", 1, 1);
				EdifPort FDE26_interface_D = new EdifPort(FDE26_interface, "D", 1, 1);
				EdifPort FDE26_interface_CE = new EdifPort(FDE26_interface, "CE", 1, 1);
				EdifPort FDE26_interface_Q = new EdifPort(FDE26_interface, "Q", 1, 2);
				FDE26_interface.addPort("C", 1, 1);
				FDE26_interface.addPort("D", 1, 1);
				FDE26_interface.addPort("CE", 1, 1);
				FDE26_interface.addPort("Q", 1, 2);
				//FDE27
				EdifPort FDE27_interface_C = new EdifPort(FDE27_interface, "C", 1, 1);
				EdifPort FDE27_interface_D = new EdifPort(FDE27_interface, "D", 1, 1);
				EdifPort FDE27_interface_CE = new EdifPort(FDE27_interface, "CE", 1, 1);
				EdifPort FDE27_interface_Q = new EdifPort(FDE27_interface, "Q", 1, 2);
				FDE27_interface.addPort("C", 1, 1);
				FDE27_interface.addPort("D", 1, 1);
				FDE27_interface.addPort("CE", 1, 1);
				FDE27_interface.addPort("Q", 1, 2);
				//FDE28
				EdifPort FDE28_interface_C = new EdifPort(FDE28_interface, "C", 1, 1);
				EdifPort FDE28_interface_D = new EdifPort(FDE28_interface, "D", 1, 1);
				EdifPort FDE28_interface_CE = new EdifPort(FDE28_interface, "CE", 1, 1);
				EdifPort FDE28_interface_Q = new EdifPort(FDE28_interface, "Q", 1, 2);
				FDE28_interface.addPort("C", 1, 1);
				FDE28_interface.addPort("D", 1, 1);
				FDE28_interface.addPort("CE", 1, 1);
				FDE28_interface.addPort("Q", 1, 2);
				//FDE29
				EdifPort FDE29_interface_C = new EdifPort(FDE29_interface, "C", 1, 1);
				EdifPort FDE29_interface_D = new EdifPort(FDE29_interface, "D", 1, 1);
				EdifPort FDE29_interface_CE = new EdifPort(FDE29_interface, "CE", 1, 1);
				EdifPort FDE29_interface_Q = new EdifPort(FDE29_interface, "Q", 1, 2);
				FDE29_interface.addPort("C", 1, 1);
				FDE29_interface.addPort("D", 1, 1);
				FDE29_interface.addPort("CE", 1, 1);
				FDE29_interface.addPort("Q", 1, 2);
				//FDE30
				EdifPort FDE30_interface_C = new EdifPort(FDE30_interface, "C", 1, 1);
				EdifPort FDE30_interface_D = new EdifPort(FDE30_interface, "D", 1, 1);
				EdifPort FDE30_interface_CE = new EdifPort(FDE30_interface, "CE", 1, 1);
				EdifPort FDE30_interface_Q = new EdifPort(FDE30_interface, "Q", 1, 2);
				FDE30_interface.addPort("C", 1, 1);
				FDE30_interface.addPort("D", 1, 1);
				FDE30_interface.addPort("CE", 1, 1);
				FDE30_interface.addPort("Q", 1, 2);
				//FDE31
				EdifPort FDE31_interface_C = new EdifPort(FDE31_interface, "C", 1, 1);
				EdifPort FDE31_interface_D = new EdifPort(FDE31_interface, "D", 1, 1);
				EdifPort FDE31_interface_CE = new EdifPort(FDE31_interface, "CE", 1, 1);
				EdifPort FDE31_interface_Q = new EdifPort(FDE31_interface, "Q", 1, 2);
				FDE31_interface.addPort("C", 1, 1);
				FDE31_interface.addPort("D", 1, 1);
				FDE31_interface.addPort("CE", 1, 1);
				FDE31_interface.addPort("Q", 1, 2);
				//FDE32
				EdifPort FDE32_interface_C = new EdifPort(FDE32_interface, "C", 1, 1);
				EdifPort FDE32_interface_D = new EdifPort(FDE32_interface, "D", 1, 1);
				EdifPort FDE32_interface_CE = new EdifPort(FDE32_interface, "CE", 1, 1);
				EdifPort FDE32_interface_Q = new EdifPort(FDE32_interface, "Q", 1, 2);
				FDE32_interface.addPort("C", 1, 1);
				FDE32_interface.addPort("D", 1, 1);
				FDE32_interface.addPort("CE", 1, 1);
				FDE32_interface.addPort("Q", 1, 2);
				//LUT2_1
				EdifPort LUT2_1_interface_I0 = new EdifPort(LUT2_1_interface, "I0", 1, 1);
				EdifPort LUT2_1_interface_I1 = new EdifPort(LUT2_1_interface, "I1", 1, 1);
				EdifPort LUT2_1_interface_O = new EdifPort(LUT2_1_interface, "O", 1, 2);
				LUT2_1_interface.addPort("I0", 1, 1);
				LUT2_1_interface.addPort("I1", 1, 1);
				LUT2_1_interface.addPort("O", 1, 2);
				//LUT2_2
				EdifPort LUT2_2_interface_I0 = new EdifPort(LUT2_2_interface, "I0", 1, 1);
				EdifPort LUT2_2_interface_I1 = new EdifPort(LUT2_2_interface, "I1", 1, 1);
				EdifPort LUT2_2_interface_O = new EdifPort(LUT2_2_interface, "O", 1, 2);
				LUT2_2_interface.addPort("I0", 1, 1);
				LUT2_2_interface.addPort("I1", 1, 1);
				LUT2_2_interface.addPort("O", 1, 2);
				//LUT3_1
				EdifPort LUT3_1_interface_I0 = new EdifPort(LUT3_1_interface, "I0", 1, 1);
				EdifPort LUT3_1_interface_I1 = new EdifPort(LUT3_1_interface, "I1", 1, 1);
				EdifPort LUT3_1_interface_I2 = new EdifPort(LUT3_1_interface, "I2", 1, 1);
				EdifPort LUT3_1_interface_O = new EdifPort(LUT3_1_interface, "O", 1, 2);
				LUT3_1_interface.addPort("I0", 1, 1);
				LUT3_1_interface.addPort("I1", 1, 1);
				LUT3_1_interface.addPort("I2", 1, 1);
				LUT3_1_interface.addPort("O", 1, 2);
				//LUT3_2
				EdifPort LUT3_2_interface_I0 = new EdifPort(LUT3_2_interface, "I0", 1, 1);
				EdifPort LUT3_2_interface_I1 = new EdifPort(LUT3_2_interface, "I1", 1, 1);
				EdifPort LUT3_2_interface_I2 = new EdifPort(LUT3_2_interface, "I2", 1, 1);
				EdifPort LUT3_2_interface_O = new EdifPort(LUT3_2_interface, "O", 1, 2);
				LUT3_2_interface.addPort("I0", 1, 1);
				LUT3_2_interface.addPort("I1", 1, 1);
				LUT3_2_interface.addPort("I2", 1, 1);
				LUT3_2_interface.addPort("O", 1, 2);
				//LUT3_3
				EdifPort LUT3_3_interface_I0 = new EdifPort(LUT3_3_interface, "I0", 1, 1);
				EdifPort LUT3_3_interface_I1 = new EdifPort(LUT3_3_interface, "I1", 1, 1);
				EdifPort LUT3_3_interface_I2 = new EdifPort(LUT3_3_interface, "I2", 1, 1);
				EdifPort LUT3_3_interface_O = new EdifPort(LUT3_3_interface, "O", 1, 2);
				LUT3_3_interface.addPort("I0", 1, 1);
				LUT3_3_interface.addPort("I1", 1, 1);
				LUT3_3_interface.addPort("I2", 1, 1);
				LUT3_3_interface.addPort("O", 1, 2);
				//LUT3_4
				EdifPort LUT3_4_interface_I0 = new EdifPort(LUT3_4_interface, "I0", 1, 1);
				EdifPort LUT3_4_interface_I1 = new EdifPort(LUT3_4_interface, "I1", 1, 1);
				EdifPort LUT3_4_interface_I2 = new EdifPort(LUT3_4_interface, "I2", 1, 1);
				EdifPort LUT3_4_interface_O = new EdifPort(LUT3_4_interface, "O", 1, 2);
				LUT3_4_interface.addPort("I0", 1, 1);
				LUT3_4_interface.addPort("I1", 1, 1);
				LUT3_4_interface.addPort("I2", 1, 1);
				LUT3_4_interface.addPort("O", 1, 2);
				//LUT3_5
				EdifPort LUT3_5_interface_I0 = new EdifPort(LUT3_5_interface, "I0", 1, 1);
				EdifPort LUT3_5_interface_I1 = new EdifPort(LUT3_5_interface, "I1", 1, 1);
				EdifPort LUT3_5_interface_I2 = new EdifPort(LUT3_5_interface, "I2", 1, 1);
				EdifPort LUT3_5_interface_O = new EdifPort(LUT3_5_interface, "O", 1, 2);
				LUT3_5_interface.addPort("I0", 1, 1);
				LUT3_5_interface.addPort("I1", 1, 1);
				LUT3_5_interface.addPort("I2", 1, 1);
				LUT3_5_interface.addPort("O", 1, 2);
				//LUT3_6
				EdifPort LUT3_6_interface_I0 = new EdifPort(LUT3_6_interface, "I0", 1, 1);
				EdifPort LUT3_6_interface_I1 = new EdifPort(LUT3_6_interface, "I1", 1, 1);
				EdifPort LUT3_6_interface_I2 = new EdifPort(LUT3_6_interface, "I2", 1, 1);
				EdifPort LUT3_6_interface_O = new EdifPort(LUT3_6_interface, "O", 1, 2);
				LUT3_6_interface.addPort("I0", 1, 1);
				LUT3_6_interface.addPort("I1", 1, 1);
				LUT3_6_interface.addPort("I2", 1, 1);
				LUT3_6_interface.addPort("O", 1, 2);
				//LUT3_7
				EdifPort LUT3_7_interface_I0 = new EdifPort(LUT3_7_interface, "I0", 1, 1);
				EdifPort LUT3_7_interface_I1 = new EdifPort(LUT3_7_interface, "I1", 1, 1);
				EdifPort LUT3_7_interface_I2 = new EdifPort(LUT3_7_interface, "I2", 1, 1);
				EdifPort LUT3_7_interface_O = new EdifPort(LUT3_7_interface, "O", 1, 2);
				LUT3_7_interface.addPort("I0", 1, 1);
				LUT3_7_interface.addPort("I1", 1, 1);
				LUT3_7_interface.addPort("I2", 1, 1);
				LUT3_7_interface.addPort("O", 1, 2);
				//LUT3_8
				EdifPort LUT3_8_interface_I0 = new EdifPort(LUT3_8_interface, "I0", 1, 1);
				EdifPort LUT3_8_interface_I1 = new EdifPort(LUT3_8_interface, "I1", 1, 1);
				EdifPort LUT3_8_interface_I2 = new EdifPort(LUT3_8_interface, "I2", 1, 1);
				EdifPort LUT3_8_interface_O = new EdifPort(LUT3_8_interface, "O", 1, 2);
				LUT3_8_interface.addPort("I0", 1, 1);
				LUT3_8_interface.addPort("I1", 1, 1);
				LUT3_8_interface.addPort("I2", 1, 1);
				LUT3_8_interface.addPort("O", 1, 2);
				//LUT3_9
				EdifPort LUT3_9_interface_I0 = new EdifPort(LUT3_9_interface, "I0", 1, 1);
				EdifPort LUT3_9_interface_I1 = new EdifPort(LUT3_9_interface, "I1", 1, 1);
				EdifPort LUT3_9_interface_I2 = new EdifPort(LUT3_9_interface, "I2", 1, 1);
				EdifPort LUT3_9_interface_O = new EdifPort(LUT3_9_interface, "O", 1, 2);
				LUT3_9_interface.addPort("I0", 1, 1);
				LUT3_9_interface.addPort("I1", 1, 1);
				LUT3_9_interface.addPort("I2", 1, 1);
				LUT3_9_interface.addPort("O", 1, 2);
				//LUT3_10
				EdifPort LUT3_10_interface_I0 = new EdifPort(LUT3_10_interface, "I0", 1, 1);
				EdifPort LUT3_10_interface_I1 = new EdifPort(LUT3_10_interface, "I1", 1, 1);
				EdifPort LUT3_10_interface_I2 = new EdifPort(LUT3_10_interface, "I2", 1, 1);
				EdifPort LUT3_10_interface_O = new EdifPort(LUT3_10_interface, "O", 1, 2);
				LUT3_10_interface.addPort("I0", 1, 1);
				LUT3_10_interface.addPort("I1", 1, 1);
				LUT3_10_interface.addPort("I2", 1, 1);
				LUT3_10_interface.addPort("O", 1, 2);
				//LUT3_11
				EdifPort LUT3_11_interface_I0 = new EdifPort(LUT3_11_interface, "I0", 1, 1);
				EdifPort LUT3_11_interface_I1 = new EdifPort(LUT3_11_interface, "I1", 1, 1);
				EdifPort LUT3_11_interface_I2 = new EdifPort(LUT3_11_interface, "I2", 1, 1);
				EdifPort LUT3_11_interface_O = new EdifPort(LUT3_11_interface, "O", 1, 2);
				LUT3_11_interface.addPort("I0", 1, 1);
				LUT3_11_interface.addPort("I1", 1, 1);
				LUT3_11_interface.addPort("I2", 1, 1);
				LUT3_11_interface.addPort("O", 1, 2);
				//LUT3_12
				EdifPort LUT3_12_interface_I0 = new EdifPort(LUT3_12_interface, "I0", 1, 1);
				EdifPort LUT3_12_interface_I1 = new EdifPort(LUT3_12_interface, "I1", 1, 1);
				EdifPort LUT3_12_interface_I2 = new EdifPort(LUT3_12_interface, "I2", 1, 1);
				EdifPort LUT3_12_interface_O = new EdifPort(LUT3_12_interface, "O", 1, 2);
				LUT3_12_interface.addPort("I0", 1, 1);
				LUT3_12_interface.addPort("I1", 1, 1);
				LUT3_12_interface.addPort("I2", 1, 1);
				LUT3_12_interface.addPort("O", 1, 2);
				//LUT3_13
				EdifPort LUT3_13_interface_I0 = new EdifPort(LUT3_13_interface, "I0", 1, 1);
				EdifPort LUT3_13_interface_I1 = new EdifPort(LUT3_13_interface, "I1", 1, 1);
				EdifPort LUT3_13_interface_I2 = new EdifPort(LUT3_13_interface, "I2", 1, 1);
				EdifPort LUT3_13_interface_O = new EdifPort(LUT3_13_interface, "O", 1, 2);
				LUT3_13_interface.addPort("I0", 1, 1);
				LUT3_13_interface.addPort("I1", 1, 1);
				LUT3_13_interface.addPort("I2", 1, 1);
				LUT3_13_interface.addPort("O", 1, 2);
				//LUT3_14
				EdifPort LUT3_14_interface_I0 = new EdifPort(LUT3_14_interface, "I0", 1, 1);
				EdifPort LUT3_14_interface_I1 = new EdifPort(LUT3_14_interface, "I1", 1, 1);
				EdifPort LUT3_14_interface_I2 = new EdifPort(LUT3_14_interface, "I2", 1, 1);
				EdifPort LUT3_14_interface_O = new EdifPort(LUT3_14_interface, "O", 1, 2);
				LUT3_14_interface.addPort("I0", 1, 1);
				LUT3_14_interface.addPort("I1", 1, 1);
				LUT3_14_interface.addPort("I2", 1, 1);
				LUT3_14_interface.addPort("O", 1, 2);
				//LUT3_15
				EdifPort LUT3_15_interface_I0 = new EdifPort(LUT3_15_interface, "I0", 1, 1);
				EdifPort LUT3_15_interface_I1 = new EdifPort(LUT3_15_interface, "I1", 1, 1);
				EdifPort LUT3_15_interface_I2 = new EdifPort(LUT3_15_interface, "I2", 1, 1);
				EdifPort LUT3_15_interface_O = new EdifPort(LUT3_15_interface, "O", 1, 2);
				LUT3_15_interface.addPort("I0", 1, 1);
				LUT3_15_interface.addPort("I1", 1, 1);
				LUT3_15_interface.addPort("I2", 1, 1);
				LUT3_15_interface.addPort("O", 1, 2);
				//LUT3_16
				EdifPort LUT3_16_interface_I0 = new EdifPort(LUT3_16_interface, "I0", 1, 1);
				EdifPort LUT3_16_interface_I1 = new EdifPort(LUT3_16_interface, "I1", 1, 1);
				EdifPort LUT3_16_interface_I2 = new EdifPort(LUT3_16_interface, "I2", 1, 1);
				EdifPort LUT3_16_interface_O = new EdifPort(LUT3_16_interface, "O", 1, 2);
				LUT3_16_interface.addPort("I0", 1, 1);
				LUT3_16_interface.addPort("I1", 1, 1);
				LUT3_16_interface.addPort("I2", 1, 1);
				LUT3_16_interface.addPort("O", 1, 2);
				//LUT3_17
				EdifPort LUT3_17_interface_I0 = new EdifPort(LUT3_17_interface, "I0", 1, 1);
				EdifPort LUT3_17_interface_I1 = new EdifPort(LUT3_17_interface, "I1", 1, 1);
				EdifPort LUT3_17_interface_I2 = new EdifPort(LUT3_17_interface, "I2", 1, 1);
				EdifPort LUT3_17_interface_O = new EdifPort(LUT3_17_interface, "O", 1, 2);
				LUT3_17_interface.addPort("I0", 1, 1);
				LUT3_17_interface.addPort("I1", 1, 1);
				LUT3_17_interface.addPort("I2", 1, 1);
				LUT3_17_interface.addPort("O", 1, 2);
				//LUT3_18
				EdifPort LUT3_18_interface_I0 = new EdifPort(LUT3_18_interface, "I0", 1, 1);
				EdifPort LUT3_18_interface_I1 = new EdifPort(LUT3_18_interface, "I1", 1, 1);
				EdifPort LUT3_18_interface_I2 = new EdifPort(LUT3_18_interface, "I2", 1, 1);
				EdifPort LUT3_18_interface_O = new EdifPort(LUT3_18_interface, "O", 1, 2);
				LUT3_18_interface.addPort("I0", 1, 1);
				LUT3_18_interface.addPort("I1", 1, 1);
				LUT3_18_interface.addPort("I2", 1, 1);
				LUT3_18_interface.addPort("O", 1, 2);
				//LUT4_1
				EdifPort LUT4_1_interface_I0 = new EdifPort(LUT4_1_interface, "I0", 1, 1);
				EdifPort LUT4_1_interface_I1 = new EdifPort(LUT4_1_interface, "I1", 1, 1);
				EdifPort LUT4_1_interface_I2 = new EdifPort(LUT4_1_interface, "I2", 1, 1);
				EdifPort LUT4_1_interface_I3 = new EdifPort(LUT4_1_interface, "I3", 1, 1);
				EdifPort LUT4_1_interface_O = new EdifPort(LUT4_1_interface, "O", 1, 2);
				LUT4_1_interface.addPort("I0", 1, 1);
				LUT4_1_interface.addPort("I1", 1, 1);
				LUT4_1_interface.addPort("I2", 1, 1);
				LUT4_1_interface.addPort("I3", 1, 1);
				LUT4_1_interface.addPort("O", 1, 2);
				//LUT4_2
				EdifPort LUT4_2_interface_I0 = new EdifPort(LUT4_2_interface, "I0", 1, 1);
				EdifPort LUT4_2_interface_I1 = new EdifPort(LUT4_2_interface, "I1", 1, 1);
				EdifPort LUT4_2_interface_I2 = new EdifPort(LUT4_2_interface, "I2", 1, 1);
				EdifPort LUT4_2_interface_I3 = new EdifPort(LUT4_2_interface, "I3", 1, 1);
				EdifPort LUT4_2_interface_O = new EdifPort(LUT4_2_interface, "O", 1, 2);
				LUT4_2_interface.addPort("I0", 1, 1);
				LUT4_2_interface.addPort("I1", 1, 1);
				LUT4_2_interface.addPort("I2", 1, 1);
				LUT4_2_interface.addPort("I3", 1, 1);
				LUT4_2_interface.addPort("O", 1, 2);
				//LUT4_3
				EdifPort LUT4_3_interface_I0 = new EdifPort(LUT4_3_interface, "I0", 1, 1);
				EdifPort LUT4_3_interface_I1 = new EdifPort(LUT4_3_interface, "I1", 1, 1);
				EdifPort LUT4_3_interface_I2 = new EdifPort(LUT4_3_interface, "I2", 1, 1);
				EdifPort LUT4_3_interface_I3 = new EdifPort(LUT4_3_interface, "I3", 1, 1);
				EdifPort LUT4_3_interface_O = new EdifPort(LUT4_3_interface, "O", 1, 2);
				LUT4_3_interface.addPort("I0", 1, 1);
				LUT4_3_interface.addPort("I1", 1, 1);
				LUT4_3_interface.addPort("I2", 1, 1);
				LUT4_3_interface.addPort("I3", 1, 1);
				LUT4_3_interface.addPort("O", 1, 2);
				//LUT4_4
				EdifPort LUT4_4_interface_I0 = new EdifPort(LUT4_4_interface, "I0", 1, 1);
				EdifPort LUT4_4_interface_I1 = new EdifPort(LUT4_4_interface, "I1", 1, 1);
				EdifPort LUT4_4_interface_I2 = new EdifPort(LUT4_4_interface, "I2", 1, 1);
				EdifPort LUT4_4_interface_I3 = new EdifPort(LUT4_4_interface, "I3", 1, 1);
				EdifPort LUT4_4_interface_O = new EdifPort(LUT4_4_interface, "O", 1, 2);
				LUT4_4_interface.addPort("I0", 1, 1);
				LUT4_4_interface.addPort("I1", 1, 1);
				LUT4_4_interface.addPort("I2", 1, 1);
				LUT4_4_interface.addPort("I3", 1, 1);
				LUT4_4_interface.addPort("O", 1, 2);
				//LUT4_5
				EdifPort LUT4_5_interface_I0 = new EdifPort(LUT4_5_interface, "I0", 1, 1);
				EdifPort LUT4_5_interface_I1 = new EdifPort(LUT4_5_interface, "I1", 1, 1);
				EdifPort LUT4_5_interface_I2 = new EdifPort(LUT4_5_interface, "I2", 1, 1);
				EdifPort LUT4_5_interface_I3 = new EdifPort(LUT4_5_interface, "I3", 1, 1);
				EdifPort LUT4_5_interface_O = new EdifPort(LUT4_5_interface, "O", 1, 2);
				LUT4_5_interface.addPort("I0", 1, 1);
				LUT4_5_interface.addPort("I1", 1, 1);
				LUT4_5_interface.addPort("I2", 1, 1);
				LUT4_5_interface.addPort("I3", 1, 1);
				LUT4_5_interface.addPort("O", 1, 2);
				//LUT4_6
				EdifPort LUT4_6_interface_I0 = new EdifPort(LUT4_6_interface, "I0", 1, 1);
				EdifPort LUT4_6_interface_I1 = new EdifPort(LUT4_6_interface, "I1", 1, 1);
				EdifPort LUT4_6_interface_I2 = new EdifPort(LUT4_6_interface, "I2", 1, 1);
				EdifPort LUT4_6_interface_I3 = new EdifPort(LUT4_6_interface, "I3", 1, 1);
				EdifPort LUT4_6_interface_O = new EdifPort(LUT4_6_interface, "O", 1, 2);
				LUT4_6_interface.addPort("I0", 1, 1);
				LUT4_6_interface.addPort("I1", 1, 1);
				LUT4_6_interface.addPort("I2", 1, 1);
				LUT4_6_interface.addPort("I3", 1, 1);
				LUT4_6_interface.addPort("O", 1, 2);
				//LUT4_7
				EdifPort LUT4_7_interface_I0 = new EdifPort(LUT4_7_interface, "I0", 1, 1);
				EdifPort LUT4_7_interface_I1 = new EdifPort(LUT4_7_interface, "I1", 1, 1);
				EdifPort LUT4_7_interface_I2 = new EdifPort(LUT4_7_interface, "I2", 1, 1);
				EdifPort LUT4_7_interface_I3 = new EdifPort(LUT4_7_interface, "I3", 1, 1);
				EdifPort LUT4_7_interface_O = new EdifPort(LUT4_7_interface, "O", 1, 2);
				LUT4_7_interface.addPort("I0", 1, 1);
				LUT4_7_interface.addPort("I1", 1, 1);
				LUT4_7_interface.addPort("I2", 1, 1);
				LUT4_7_interface.addPort("I3", 1, 1);
				LUT4_7_interface.addPort("O", 1, 2);
				//LUT4_8
				EdifPort LUT4_8_interface_I0 = new EdifPort(LUT4_8_interface, "I0", 1, 1);
				EdifPort LUT4_8_interface_I1 = new EdifPort(LUT4_8_interface, "I1", 1, 1);
				EdifPort LUT4_8_interface_I2 = new EdifPort(LUT4_8_interface, "I2", 1, 1);
				EdifPort LUT4_8_interface_I3 = new EdifPort(LUT4_8_interface, "I3", 1, 1);
				EdifPort LUT4_8_interface_O = new EdifPort(LUT4_8_interface, "O", 1, 2);
				LUT4_8_interface.addPort("I0", 1, 1);
				LUT4_8_interface.addPort("I1", 1, 1);
				LUT4_8_interface.addPort("I2", 1, 1);
				LUT4_8_interface.addPort("I3", 1, 1);
				LUT4_8_interface.addPort("O", 1, 2);
				//LUT4_9
				EdifPort LUT4_9_interface_I0 = new EdifPort(LUT4_9_interface, "I0", 1, 1);
				EdifPort LUT4_9_interface_I1 = new EdifPort(LUT4_9_interface, "I1", 1, 1);
				EdifPort LUT4_9_interface_I2 = new EdifPort(LUT4_9_interface, "I2", 1, 1);
				EdifPort LUT4_9_interface_I3 = new EdifPort(LUT4_9_interface, "I3", 1, 1);
				EdifPort LUT4_9_interface_O = new EdifPort(LUT4_9_interface, "O", 1, 2);
				LUT4_9_interface.addPort("I0", 1, 1);
				LUT4_9_interface.addPort("I1", 1, 1);
				LUT4_9_interface.addPort("I2", 1, 1);
				LUT4_9_interface.addPort("I3", 1, 1);
				LUT4_9_interface.addPort("O", 1, 2);
				//LUT4_10
				EdifPort LUT4_10_interface_I0 = new EdifPort(LUT4_10_interface, "I0", 1, 1);
				EdifPort LUT4_10_interface_I1 = new EdifPort(LUT4_10_interface, "I1", 1, 1);
				EdifPort LUT4_10_interface_I2 = new EdifPort(LUT4_10_interface, "I2", 1, 1);
				EdifPort LUT4_10_interface_I3 = new EdifPort(LUT4_10_interface, "I3", 1, 1);
				EdifPort LUT4_10_interface_O = new EdifPort(LUT4_10_interface, "O", 1, 2);
				LUT4_10_interface.addPort("I0", 1, 1);
				LUT4_10_interface.addPort("I1", 1, 1);
				LUT4_10_interface.addPort("I2", 1, 1);
				LUT4_10_interface.addPort("I3", 1, 1);
				LUT4_10_interface.addPort("O", 1, 2);
				//LUT4_11
				EdifPort LUT4_11_interface_I0 = new EdifPort(LUT4_11_interface, "I0", 1, 1);
				EdifPort LUT4_11_interface_I1 = new EdifPort(LUT4_11_interface, "I1", 1, 1);
				EdifPort LUT4_11_interface_I2 = new EdifPort(LUT4_11_interface, "I2", 1, 1);
				EdifPort LUT4_11_interface_I3 = new EdifPort(LUT4_11_interface, "I3", 1, 1);
				EdifPort LUT4_11_interface_O = new EdifPort(LUT4_11_interface, "O", 1, 2);
				LUT4_11_interface.addPort("I0", 1, 1);
				LUT4_11_interface.addPort("I1", 1, 1);
				LUT4_11_interface.addPort("I2", 1, 1);
				LUT4_11_interface.addPort("I3", 1, 1);
				LUT4_11_interface.addPort("O", 1, 2);
				//LUT4_12
				EdifPort LUT4_12_interface_I0 = new EdifPort(LUT4_12_interface, "I0", 1, 1);
				EdifPort LUT4_12_interface_I1 = new EdifPort(LUT4_12_interface, "I1", 1, 1);
				EdifPort LUT4_12_interface_I2 = new EdifPort(LUT4_12_interface, "I2", 1, 1);
				EdifPort LUT4_12_interface_I3 = new EdifPort(LUT4_12_interface, "I3", 1, 1);
				EdifPort LUT4_12_interface_O = new EdifPort(LUT4_12_interface, "O", 1, 2);
				LUT4_12_interface.addPort("I0", 1, 1);
				LUT4_12_interface.addPort("I1", 1, 1);
				LUT4_12_interface.addPort("I2", 1, 1);
				LUT4_12_interface.addPort("I3", 1, 1);
				LUT4_12_interface.addPort("O", 1, 2);
				//LUT4_13
				EdifPort LUT4_13_interface_I0 = new EdifPort(LUT4_13_interface, "I0", 1, 1);
				EdifPort LUT4_13_interface_I1 = new EdifPort(LUT4_13_interface, "I1", 1, 1);
				EdifPort LUT4_13_interface_I2 = new EdifPort(LUT4_13_interface, "I2", 1, 1);
				EdifPort LUT4_13_interface_I3 = new EdifPort(LUT4_13_interface, "I3", 1, 1);
				EdifPort LUT4_13_interface_O = new EdifPort(LUT4_13_interface, "O", 1, 2);
				LUT4_13_interface.addPort("I0", 1, 1);
				LUT4_13_interface.addPort("I1", 1, 1);
				LUT4_13_interface.addPort("I2", 1, 1);
				LUT4_13_interface.addPort("I3", 1, 1);
				LUT4_13_interface.addPort("O", 1, 2);
				//LUT4_14
				EdifPort LUT4_14_interface_I0 = new EdifPort(LUT4_14_interface, "I0", 1, 1);
				EdifPort LUT4_14_interface_I1 = new EdifPort(LUT4_14_interface, "I1", 1, 1);
				EdifPort LUT4_14_interface_I2 = new EdifPort(LUT4_14_interface, "I2", 1, 1);
				EdifPort LUT4_14_interface_I3 = new EdifPort(LUT4_14_interface, "I3", 1, 1);
				EdifPort LUT4_14_interface_O = new EdifPort(LUT4_14_interface, "O", 1, 2);
				LUT4_14_interface.addPort("I0", 1, 1);
				LUT4_14_interface.addPort("I1", 1, 1);
				LUT4_14_interface.addPort("I2", 1, 1);
				LUT4_14_interface.addPort("I3", 1, 1);
				LUT4_14_interface.addPort("O", 1, 2);
				//LUT4_15
				EdifPort LUT4_15_interface_I0 = new EdifPort(LUT4_15_interface, "I0", 1, 1);
				EdifPort LUT4_15_interface_I1 = new EdifPort(LUT4_15_interface, "I1", 1, 1);
				EdifPort LUT4_15_interface_I2 = new EdifPort(LUT4_15_interface, "I2", 1, 1);
				EdifPort LUT4_15_interface_I3 = new EdifPort(LUT4_15_interface, "I3", 1, 1);
				EdifPort LUT4_15_interface_O = new EdifPort(LUT4_15_interface, "O", 1, 2);
				LUT4_15_interface.addPort("I0", 1, 1);
				LUT4_15_interface.addPort("I1", 1, 1);
				LUT4_15_interface.addPort("I2", 1, 1);
				LUT4_15_interface.addPort("I3", 1, 1);
				LUT4_15_interface.addPort("O", 1, 2);
				//LUT4_16
				EdifPort LUT4_16_interface_I0 = new EdifPort(LUT4_16_interface, "I0", 1, 1);
				EdifPort LUT4_16_interface_I1 = new EdifPort(LUT4_16_interface, "I1", 1, 1);
				EdifPort LUT4_16_interface_I2 = new EdifPort(LUT4_16_interface, "I2", 1, 1);
				EdifPort LUT4_16_interface_I3 = new EdifPort(LUT4_16_interface, "I3", 1, 1);
				EdifPort LUT4_16_interface_O = new EdifPort(LUT4_16_interface, "O", 1, 2);
				LUT4_16_interface.addPort("I0", 1, 1);
				LUT4_16_interface.addPort("I1", 1, 1);
				LUT4_16_interface.addPort("I2", 1, 1);
				LUT4_16_interface.addPort("I3", 1, 1);
				LUT4_16_interface.addPort("O", 1, 2);
				//MUXF5_1
				EdifPort MUXF5_1_interface_I0 = new EdifPort(MUXF5_1_interface, "I0", 1, 1);
				EdifPort MUXF5_1_interface_I1 = new EdifPort(MUXF5_1_interface, "I1", 1, 1);
				EdifPort MUXF5_1_interface_S = new EdifPort(MUXF5_1_interface, "S", 1, 1);
				EdifPort MUXF5_1_interface_O = new EdifPort(MUXF5_1_interface, "O", 1, 2);
				MUXF5_1_interface.addPort("I0", 1, 1);
				MUXF5_1_interface.addPort("I1", 1, 1);
				MUXF5_1_interface.addPort("S", 1, 1);
				MUXF5_1_interface.addPort("O", 1, 2);
				//MUXF5_2
				EdifPort MUXF5_2_interface_I0 = new EdifPort(MUXF5_2_interface, "I0", 1, 1);
				EdifPort MUXF5_2_interface_I1 = new EdifPort(MUXF5_2_interface, "I1", 1, 1);
				EdifPort MUXF5_2_interface_S = new EdifPort(MUXF5_2_interface, "S", 1, 1);
				EdifPort MUXF5_2_interface_O = new EdifPort(MUXF5_2_interface, "O", 1, 2);
				MUXF5_2_interface.addPort("I0", 1, 1);
				MUXF5_2_interface.addPort("I1", 1, 1);
				MUXF5_2_interface.addPort("S", 1, 1);
				MUXF5_2_interface.addPort("O", 1, 2);
				//MUXF5_3
				EdifPort MUXF5_3_interface_I0 = new EdifPort(MUXF5_3_interface, "I0", 1, 1);
				EdifPort MUXF5_3_interface_I1 = new EdifPort(MUXF5_3_interface, "I1", 1, 1);
				EdifPort MUXF5_3_interface_S = new EdifPort(MUXF5_3_interface, "S", 1, 1);
				EdifPort MUXF5_3_interface_O = new EdifPort(MUXF5_3_interface, "O", 1, 2);
				MUXF5_3_interface.addPort("I0", 1, 1);
				MUXF5_3_interface.addPort("I1", 1, 1);
				MUXF5_3_interface.addPort("S", 1, 1);
				MUXF5_3_interface.addPort("O", 1, 2);
				//MUXF5_4
				EdifPort MUXF5_4_interface_I0 = new EdifPort(MUXF5_4_interface, "I0", 1, 1);
				EdifPort MUXF5_4_interface_I1 = new EdifPort(MUXF5_4_interface, "I1", 1, 1);
				EdifPort MUXF5_4_interface_S = new EdifPort(MUXF5_4_interface, "S", 1, 1);
				EdifPort MUXF5_4_interface_O = new EdifPort(MUXF5_4_interface, "O", 1, 2);
				MUXF5_4_interface.addPort("I0", 1, 1);
				MUXF5_4_interface.addPort("I1", 1, 1);
				MUXF5_4_interface.addPort("S", 1, 1);
				MUXF5_4_interface.addPort("O", 1, 2);
				//MUXF5_5
				EdifPort MUXF5_5_interface_I0 = new EdifPort(MUXF5_5_interface, "I0", 1, 1);
				EdifPort MUXF5_5_interface_I1 = new EdifPort(MUXF5_5_interface, "I1", 1, 1);
				EdifPort MUXF5_5_interface_S = new EdifPort(MUXF5_5_interface, "S", 1, 1);
				EdifPort MUXF5_5_interface_O = new EdifPort(MUXF5_5_interface, "O", 1, 2);
				MUXF5_5_interface.addPort("I0", 1, 1);
				MUXF5_5_interface.addPort("I1", 1, 1);
				MUXF5_5_interface.addPort("S", 1, 1);
				MUXF5_5_interface.addPort("O", 1, 2);
				//MUXF5_6
				EdifPort MUXF5_6_interface_I0 = new EdifPort(MUXF5_6_interface, "I0", 1, 1);
				EdifPort MUXF5_6_interface_I1 = new EdifPort(MUXF5_6_interface, "I1", 1, 1);
				EdifPort MUXF5_6_interface_S = new EdifPort(MUXF5_6_interface, "S", 1, 1);
				EdifPort MUXF5_6_interface_O = new EdifPort(MUXF5_6_interface, "O", 1, 2);
				MUXF5_6_interface.addPort("I0", 1, 1);
				MUXF5_6_interface.addPort("I1", 1, 1);
				MUXF5_6_interface.addPort("S", 1, 1);
				MUXF5_6_interface.addPort("O", 1, 2);
				//MUXF5_7
				EdifPort MUXF5_7_interface_I0 = new EdifPort(MUXF5_7_interface, "I0", 1, 1);
				EdifPort MUXF5_7_interface_I1 = new EdifPort(MUXF5_7_interface, "I1", 1, 1);
				EdifPort MUXF5_7_interface_S = new EdifPort(MUXF5_7_interface, "S", 1, 1);
				EdifPort MUXF5_7_interface_O = new EdifPort(MUXF5_7_interface, "O", 1, 2);
				MUXF5_7_interface.addPort("I0", 1, 1);
				MUXF5_7_interface.addPort("I1", 1, 1);
				MUXF5_7_interface.addPort("S", 1, 1);
				MUXF5_7_interface.addPort("O", 1, 2);
				//MUXF5_8
				EdifPort MUXF5_8_interface_I0 = new EdifPort(MUXF5_8_interface, "I0", 1, 1);
				EdifPort MUXF5_8_interface_I1 = new EdifPort(MUXF5_8_interface, "I1", 1, 1);
				EdifPort MUXF5_8_interface_S = new EdifPort(MUXF5_8_interface, "S", 1, 1);
				EdifPort MUXF5_8_interface_O = new EdifPort(MUXF5_8_interface, "O", 1, 2);
				MUXF5_8_interface.addPort("I0", 1, 1);
				MUXF5_8_interface.addPort("I1", 1, 1);
				MUXF5_8_interface.addPort("S", 1, 1);
				MUXF5_8_interface.addPort("O", 1, 2);
				//MUXF6_1
				EdifPort MUXF6_1_interface_I0 = new EdifPort(MUXF6_1_interface, "I0", 1, 1);
				EdifPort MUXF6_1_interface_I1 = new EdifPort(MUXF6_1_interface, "I1", 1, 1);
				EdifPort MUXF6_1_interface_S = new EdifPort(MUXF6_1_interface, "S", 1, 1);
				EdifPort MUXF6_1_interface_O = new EdifPort(MUXF6_1_interface, "O", 1, 2);
				MUXF6_1_interface.addPort("I0", 1, 1);
				MUXF6_1_interface.addPort("I1", 1, 1);
				MUXF6_1_interface.addPort("S", 1, 1);
				MUXF6_1_interface.addPort("O", 1, 2);
				//MUXF6_2
				EdifPort MUXF6_2_interface_I0 = new EdifPort(MUXF6_2_interface, "I0", 1, 1);
				EdifPort MUXF6_2_interface_I1 = new EdifPort(MUXF6_2_interface, "I1", 1, 1);
				EdifPort MUXF6_2_interface_S = new EdifPort(MUXF6_2_interface, "S", 1, 1);
				EdifPort MUXF6_2_interface_O = new EdifPort(MUXF6_2_interface, "O", 1, 2);
				MUXF6_2_interface.addPort("I0", 1, 1);
				MUXF6_2_interface.addPort("I1", 1, 1);
				MUXF6_2_interface.addPort("S", 1, 1);
				MUXF6_2_interface.addPort("O", 1, 2);
				//MUXF6_3
				EdifPort MUXF6_3_interface_I0 = new EdifPort(MUXF6_3_interface, "I0", 1, 1);
				EdifPort MUXF6_3_interface_I1 = new EdifPort(MUXF6_3_interface, "I1", 1, 1);
				EdifPort MUXF6_3_interface_S = new EdifPort(MUXF6_3_interface, "S", 1, 1);
				EdifPort MUXF6_3_interface_O = new EdifPort(MUXF6_3_interface, "O", 1, 2);
				MUXF6_3_interface.addPort("I0", 1, 1);
				MUXF6_3_interface.addPort("I1", 1, 1);
				MUXF6_3_interface.addPort("S", 1, 1);
				MUXF6_3_interface.addPort("O", 1, 2);
				//MUXF6_4
				EdifPort MUXF6_4_interface_I0 = new EdifPort(MUXF6_4_interface, "I0", 1, 1);
				EdifPort MUXF6_4_interface_I1 = new EdifPort(MUXF6_4_interface, "I1", 1, 1);
				EdifPort MUXF6_4_interface_S = new EdifPort(MUXF6_4_interface, "S", 1, 1);
				EdifPort MUXF6_4_interface_O = new EdifPort(MUXF6_4_interface, "O", 1, 2);
				MUXF6_4_interface.addPort("I0", 1, 1);
				MUXF6_4_interface.addPort("I1", 1, 1);
				MUXF6_4_interface.addPort("S", 1, 1);
				MUXF6_4_interface.addPort("O", 1, 2);
				
				/******PortRefs******/
				EdifPortRef RAM16X2S_replacement_WE = new EdifPortRef(we, RAM16X2S_replacement_interface_WE.getSingleBitPort(0), null);
				EdifPortRef RAM16X2S_replacement_D0 = new EdifPortRef(d0, RAM16X2S_replacement_interface_D0.getSingleBitPort(0), null);
				EdifPortRef RAM16X2S_replacement_D1 = new EdifPortRef(d1, RAM16X2S_replacement_interface_D1.getSingleBitPort(0), null);
				EdifPortRef RAM16X2S_replacement_WCLK = new EdifPortRef(wclk, RAM16X2S_replacement_interface_WCLK.getSingleBitPort(0), null);
				EdifPortRef RAM16X2S_replacement_A0 = new EdifPortRef(a0, RAM16X2S_replacement_interface_A0.getSingleBitPort(0), null);
				EdifPortRef RAM16X2S_replacement_A1 = new EdifPortRef(a1, RAM16X2S_replacement_interface_A1.getSingleBitPort(0), null);
				EdifPortRef RAM16X2S_replacement_A2 = new EdifPortRef(a2, RAM16X2S_replacement_interface_A2.getSingleBitPort(0), null);
				EdifPortRef RAM16X2S_replacement_A3 = new EdifPortRef(a3, RAM16X2S_replacement_interface_A3.getSingleBitPort(0), null);
				EdifPortRef RAM16X2S_replacement_O0 = new EdifPortRef(o0, RAM16X2S_replacement_interface_O0.getSingleBitPort(0), null);
				EdifPortRef RAM16X2S_replacement_O1 = new EdifPortRef(o1, RAM16X2S_replacement_interface_O1.getSingleBitPort(0), null);
				EdifPortRef WE_ibuf_I = new EdifPortRef(we, WE_ibuf_interface_I.getSingleBitPort(0), WE_ibuf);
				EdifPortRef WE_ibuf_O = new EdifPortRef(we_c, WE_ibuf_interface_O.getSingleBitPort(0), WE_ibuf);
				EdifPortRef D0_ibuf_I = new EdifPortRef(d0, D0_ibuf_interface_I.getSingleBitPort(0), D0_ibuf);
				EdifPortRef D0_ibuf_O = new EdifPortRef(d0_c, D0_ibuf_interface_O.getSingleBitPort(0), D0_ibuf);
				EdifPortRef D1_ibuf_I = new EdifPortRef(d1, D1_ibuf_interface_I.getSingleBitPort(0), D1_ibuf);
				EdifPortRef D1_ibuf_O = new EdifPortRef(d1_c, D1_ibuf_interface_O.getSingleBitPort(0), D1_ibuf);
				EdifPortRef WCLK_ibuf_I = new EdifPortRef(wclk, WCLK_ibuf_interface_I.getSingleBitPort(0), WCLK_ibuf);
				EdifPortRef WCLK_ibuf_O = new EdifPortRef(wclk_c, WCLK_ibuf_interface_O.getSingleBitPort(0), WCLK_ibuf);
				EdifPortRef A0_ibuf_I = new EdifPortRef(a0, A0_ibuf_interface_I.getSingleBitPort(0), A0_ibuf);
				EdifPortRef A0_ibuf_O = new EdifPortRef(a0_c, A0_ibuf_interface_O.getSingleBitPort(0), A0_ibuf);
				EdifPortRef A1_ibuf_I = new EdifPortRef(a1, A1_ibuf_interface_I.getSingleBitPort(0), A1_ibuf);
				EdifPortRef A1_ibuf_O = new EdifPortRef(a1_c, A1_ibuf_interface_O.getSingleBitPort(0), A1_ibuf);
				EdifPortRef A2_ibuf_I = new EdifPortRef(a2, A2_ibuf_interface_I.getSingleBitPort(0), A2_ibuf);
				EdifPortRef A2_ibuf_O = new EdifPortRef(a2_c, A2_ibuf_interface_O.getSingleBitPort(0), A2_ibuf);
				EdifPortRef A3_ibuf_I = new EdifPortRef(a3, A3_ibuf_interface_I.getSingleBitPort(0), A3_ibuf);
				EdifPortRef A3_ibuf_O = new EdifPortRef(a3_c, A3_ibuf_interface_O.getSingleBitPort(0), A3_ibuf);
				EdifPortRef O0_obuf_I = new EdifPortRef(o0_c, O0_obuf_interface_I.getSingleBitPort(0), O0_obuf);
				EdifPortRef O0_obuf_O = new EdifPortRef(o0, O0_obuf_interface_O.getSingleBitPort(0), O0_obuf);
				EdifPortRef O1_obuf_I = new EdifPortRef(o1_c, O1_obuf_interface_I.getSingleBitPort(0), O1_obuf);
				EdifPortRef O1_obuf_O = new EdifPortRef(o1, O1_obuf_interface_O.getSingleBitPort(0), O1_obuf);
				EdifPortRef FDE1_C = new EdifPortRef(wclk_c, FDE1_interface_C.getSingleBitPort(0), FDE1);
				EdifPortRef FDE1_D = new EdifPortRef(d0_c, FDE1_interface_D.getSingleBitPort(0), FDE1);
				EdifPortRef FDE1_CE = new EdifPortRef(e1, FDE1_interface_CE.getSingleBitPort(0), FDE1);
				EdifPortRef FDE1_Q = new EdifPortRef(wire1, FDE1_interface_Q.getSingleBitPort(0), FDE1);
				EdifPortRef FDE2_C = new EdifPortRef(wclk_c, FDE2_interface_C.getSingleBitPort(0), FDE2);
				EdifPortRef FDE2_D = new EdifPortRef(d0_c, FDE2_interface_D.getSingleBitPort(0), FDE2);
				EdifPortRef FDE2_CE = new EdifPortRef(e2, FDE2_interface_CE.getSingleBitPort(0), FDE2);
				EdifPortRef FDE2_Q = new EdifPortRef(wire2, FDE2_interface_Q.getSingleBitPort(0), FDE2);
				EdifPortRef FDE3_C = new EdifPortRef(wclk_c, FDE3_interface_C.getSingleBitPort(0), FDE3);
				EdifPortRef FDE3_D = new EdifPortRef(d0_c, FDE3_interface_D.getSingleBitPort(0), FDE3);
				EdifPortRef FDE3_CE = new EdifPortRef(e3, FDE3_interface_CE.getSingleBitPort(0), FDE3);
				EdifPortRef FDE3_Q = new EdifPortRef(wire3, FDE3_interface_Q.getSingleBitPort(0), FDE3);
				EdifPortRef FDE4_C = new EdifPortRef(wclk_c, FDE4_interface_C.getSingleBitPort(0), FDE4);
				EdifPortRef FDE4_D = new EdifPortRef(d0_c, FDE4_interface_D.getSingleBitPort(0), FDE4);
				EdifPortRef FDE4_CE = new EdifPortRef(e4, FDE4_interface_CE.getSingleBitPort(0), FDE4);
				EdifPortRef FDE4_Q = new EdifPortRef(wire4, FDE4_interface_Q.getSingleBitPort(0), FDE4);
				EdifPortRef FDE5_C = new EdifPortRef(wclk_c, FDE5_interface_C.getSingleBitPort(0), FDE5);
				EdifPortRef FDE5_D = new EdifPortRef(d0_c, FDE5_interface_D.getSingleBitPort(0), FDE5);
				EdifPortRef FDE5_CE = new EdifPortRef(e5, FDE5_interface_CE.getSingleBitPort(0), FDE5);
				EdifPortRef FDE5_Q = new EdifPortRef(wire5, FDE5_interface_Q.getSingleBitPort(0), FDE5);
				EdifPortRef FDE6_C = new EdifPortRef(wclk_c, FDE6_interface_C.getSingleBitPort(0), FDE6);
				EdifPortRef FDE6_D = new EdifPortRef(d0_c, FDE6_interface_D.getSingleBitPort(0), FDE6);
				EdifPortRef FDE6_CE = new EdifPortRef(e6, FDE6_interface_CE.getSingleBitPort(0), FDE6);
				EdifPortRef FDE6_Q = new EdifPortRef(wire6, FDE6_interface_Q.getSingleBitPort(0), FDE6);
				EdifPortRef FDE7_C = new EdifPortRef(wclk_c, FDE7_interface_C.getSingleBitPort(0), FDE7);
				EdifPortRef FDE7_D = new EdifPortRef(d0_c, FDE7_interface_D.getSingleBitPort(0), FDE7);
				EdifPortRef FDE7_CE = new EdifPortRef(e7, FDE7_interface_CE.getSingleBitPort(0), FDE7);
				EdifPortRef FDE7_Q = new EdifPortRef(wire7, FDE7_interface_Q.getSingleBitPort(0), FDE7);
				EdifPortRef FDE8_C = new EdifPortRef(wclk_c, FDE8_interface_C.getSingleBitPort(0), FDE8);
				EdifPortRef FDE8_D = new EdifPortRef(d0_c, FDE8_interface_D.getSingleBitPort(0), FDE8);
				EdifPortRef FDE8_CE = new EdifPortRef(e8, FDE8_interface_CE.getSingleBitPort(0), FDE8);
				EdifPortRef FDE8_Q = new EdifPortRef(wire8, FDE8_interface_Q.getSingleBitPort(0), FDE8);
				EdifPortRef FDE9_C = new EdifPortRef(wclk_c, FDE9_interface_C.getSingleBitPort(0), FDE9);
				EdifPortRef FDE9_D = new EdifPortRef(d0_c, FDE9_interface_D.getSingleBitPort(0), FDE9);
				EdifPortRef FDE9_CE = new EdifPortRef(e9, FDE9_interface_CE.getSingleBitPort(0), FDE9);
				EdifPortRef FDE9_Q = new EdifPortRef(wire9, FDE9_interface_Q.getSingleBitPort(0), FDE9);
				EdifPortRef FDE10_C = new EdifPortRef(wclk_c, FDE10_interface_C.getSingleBitPort(0), FDE10);
				EdifPortRef FDE10_D = new EdifPortRef(d0_c, FDE10_interface_D.getSingleBitPort(0), FDE10);
				EdifPortRef FDE10_CE = new EdifPortRef(e10, FDE10_interface_CE.getSingleBitPort(0), FDE10);
				EdifPortRef FDE10_Q = new EdifPortRef(wire10, FDE10_interface_Q.getSingleBitPort(0), FDE10);
				EdifPortRef FDE11_C = new EdifPortRef(wclk_c, FDE11_interface_C.getSingleBitPort(0), FDE11);
				EdifPortRef FDE11_D = new EdifPortRef(d0_c, FDE11_interface_D.getSingleBitPort(0), FDE11);
				EdifPortRef FDE11_CE = new EdifPortRef(e11, FDE11_interface_CE.getSingleBitPort(0), FDE11);
				EdifPortRef FDE11_Q = new EdifPortRef(wire11, FDE11_interface_Q.getSingleBitPort(0), FDE11);
				EdifPortRef FDE12_C = new EdifPortRef(wclk_c, FDE12_interface_C.getSingleBitPort(0), FDE12);
				EdifPortRef FDE12_D = new EdifPortRef(d0_c, FDE12_interface_D.getSingleBitPort(0), FDE12);
				EdifPortRef FDE12_CE = new EdifPortRef(e12, FDE12_interface_CE.getSingleBitPort(0), FDE12);
				EdifPortRef FDE12_Q = new EdifPortRef(wire12, FDE12_interface_Q.getSingleBitPort(0), FDE12);
				EdifPortRef FDE13_C = new EdifPortRef(wclk_c, FDE13_interface_C.getSingleBitPort(0), FDE13);
				EdifPortRef FDE13_D = new EdifPortRef(d0_c, FDE13_interface_D.getSingleBitPort(0), FDE13);
				EdifPortRef FDE13_CE = new EdifPortRef(e13, FDE13_interface_CE.getSingleBitPort(0), FDE13);
				EdifPortRef FDE13_Q = new EdifPortRef(wire13, FDE13_interface_Q.getSingleBitPort(0), FDE13);
				EdifPortRef FDE14_C = new EdifPortRef(wclk_c, FDE14_interface_C.getSingleBitPort(0), FDE14);
				EdifPortRef FDE14_D = new EdifPortRef(d0_c, FDE14_interface_D.getSingleBitPort(0), FDE14);
				EdifPortRef FDE14_CE = new EdifPortRef(e14, FDE14_interface_CE.getSingleBitPort(0), FDE14);
				EdifPortRef FDE14_Q = new EdifPortRef(wire14, FDE14_interface_Q.getSingleBitPort(0), FDE14);
				EdifPortRef FDE15_C = new EdifPortRef(wclk_c, FDE15_interface_C.getSingleBitPort(0), FDE15);
				EdifPortRef FDE15_D = new EdifPortRef(d0_c, FDE15_interface_D.getSingleBitPort(0), FDE15);
				EdifPortRef FDE15_CE = new EdifPortRef(e15, FDE15_interface_CE.getSingleBitPort(0), FDE15);
				EdifPortRef FDE15_Q = new EdifPortRef(wire15, FDE15_interface_Q.getSingleBitPort(0), FDE15);
				EdifPortRef FDE16_C = new EdifPortRef(wclk_c, FDE16_interface_C.getSingleBitPort(0), FDE16);
				EdifPortRef FDE16_D = new EdifPortRef(d0_c, FDE16_interface_D.getSingleBitPort(0), FDE16);
				EdifPortRef FDE16_CE = new EdifPortRef(e16, FDE16_interface_CE.getSingleBitPort(0), FDE16);
				EdifPortRef FDE16_Q = new EdifPortRef(wire16, FDE16_interface_Q.getSingleBitPort(0), FDE16);
				EdifPortRef FDE17_C = new EdifPortRef(wclk_c, FDE17_interface_C.getSingleBitPort(0), FDE17);
				EdifPortRef FDE17_D = new EdifPortRef(d1_c, FDE17_interface_D.getSingleBitPort(0), FDE17);
				EdifPortRef FDE17_CE = new EdifPortRef(e1, FDE17_interface_CE.getSingleBitPort(0), FDE17);
				EdifPortRef FDE17_Q = new EdifPortRef(wire17, FDE17_interface_Q.getSingleBitPort(0), FDE17);
				EdifPortRef FDE18_C = new EdifPortRef(wclk_c, FDE18_interface_C.getSingleBitPort(0), FDE18);
				EdifPortRef FDE18_D = new EdifPortRef(d1_c, FDE18_interface_D.getSingleBitPort(0), FDE18);
				EdifPortRef FDE18_CE = new EdifPortRef(e2, FDE18_interface_CE.getSingleBitPort(0), FDE18);
				EdifPortRef FDE18_Q = new EdifPortRef(wire18, FDE18_interface_Q.getSingleBitPort(0), FDE18);
				EdifPortRef FDE19_C = new EdifPortRef(wclk_c, FDE19_interface_C.getSingleBitPort(0), FDE19);
				EdifPortRef FDE19_D = new EdifPortRef(d1_c, FDE19_interface_D.getSingleBitPort(0), FDE19);
				EdifPortRef FDE19_CE = new EdifPortRef(e3, FDE19_interface_CE.getSingleBitPort(0), FDE19);
				EdifPortRef FDE19_Q = new EdifPortRef(wire19, FDE19_interface_Q.getSingleBitPort(0), FDE19);
				EdifPortRef FDE20_C = new EdifPortRef(wclk_c, FDE20_interface_C.getSingleBitPort(0), FDE20);
				EdifPortRef FDE20_D = new EdifPortRef(d1_c, FDE20_interface_D.getSingleBitPort(0), FDE20);
				EdifPortRef FDE20_CE = new EdifPortRef(e4, FDE20_interface_CE.getSingleBitPort(0), FDE20);
				EdifPortRef FDE20_Q = new EdifPortRef(wire20, FDE20_interface_Q.getSingleBitPort(0), FDE20);
				EdifPortRef FDE21_C = new EdifPortRef(wclk_c, FDE21_interface_C.getSingleBitPort(0), FDE21);
				EdifPortRef FDE21_D = new EdifPortRef(d1_c, FDE21_interface_D.getSingleBitPort(0), FDE21);
				EdifPortRef FDE21_CE = new EdifPortRef(e5, FDE21_interface_CE.getSingleBitPort(0), FDE21);
				EdifPortRef FDE21_Q = new EdifPortRef(wire21, FDE21_interface_Q.getSingleBitPort(0), FDE21);
				EdifPortRef FDE22_C = new EdifPortRef(wclk_c, FDE22_interface_C.getSingleBitPort(0), FDE22);
				EdifPortRef FDE22_D = new EdifPortRef(d1_c, FDE22_interface_D.getSingleBitPort(0), FDE22);
				EdifPortRef FDE22_CE = new EdifPortRef(e6, FDE22_interface_CE.getSingleBitPort(0), FDE22);
				EdifPortRef FDE22_Q = new EdifPortRef(wire22, FDE22_interface_Q.getSingleBitPort(0), FDE22);
				EdifPortRef FDE23_C = new EdifPortRef(wclk_c, FDE23_interface_C.getSingleBitPort(0), FDE23);
				EdifPortRef FDE23_D = new EdifPortRef(d1_c, FDE23_interface_D.getSingleBitPort(0), FDE23);
				EdifPortRef FDE23_CE = new EdifPortRef(e7, FDE23_interface_CE.getSingleBitPort(0), FDE23);
				EdifPortRef FDE23_Q = new EdifPortRef(wire23, FDE23_interface_Q.getSingleBitPort(0), FDE23);
				EdifPortRef FDE24_C = new EdifPortRef(wclk_c, FDE24_interface_C.getSingleBitPort(0), FDE24);
				EdifPortRef FDE24_D = new EdifPortRef(d1_c, FDE24_interface_D.getSingleBitPort(0), FDE24);
				EdifPortRef FDE24_CE = new EdifPortRef(e8, FDE24_interface_CE.getSingleBitPort(0), FDE24);
				EdifPortRef FDE24_Q = new EdifPortRef(wire24, FDE24_interface_Q.getSingleBitPort(0), FDE24);
				EdifPortRef FDE25_C = new EdifPortRef(wclk_c, FDE25_interface_C.getSingleBitPort(0), FDE25);
				EdifPortRef FDE25_D = new EdifPortRef(d1_c, FDE25_interface_D.getSingleBitPort(0), FDE25);
				EdifPortRef FDE25_CE = new EdifPortRef(e9, FDE25_interface_CE.getSingleBitPort(0), FDE25);
				EdifPortRef FDE25_Q = new EdifPortRef(wire25, FDE25_interface_Q.getSingleBitPort(0), FDE25);
				EdifPortRef FDE26_C = new EdifPortRef(wclk_c, FDE26_interface_C.getSingleBitPort(0), FDE26);
				EdifPortRef FDE26_D = new EdifPortRef(d1_c, FDE26_interface_D.getSingleBitPort(0), FDE26);
				EdifPortRef FDE26_CE = new EdifPortRef(e10, FDE26_interface_CE.getSingleBitPort(0), FDE26);
				EdifPortRef FDE26_Q = new EdifPortRef(wire26, FDE26_interface_Q.getSingleBitPort(0), FDE26);
				EdifPortRef FDE27_C = new EdifPortRef(wclk_c, FDE27_interface_C.getSingleBitPort(0), FDE27);
				EdifPortRef FDE27_D = new EdifPortRef(d1_c, FDE27_interface_D.getSingleBitPort(0), FDE27);
				EdifPortRef FDE27_CE = new EdifPortRef(e11, FDE27_interface_CE.getSingleBitPort(0), FDE27);
				EdifPortRef FDE27_Q = new EdifPortRef(wire27, FDE27_interface_Q.getSingleBitPort(0), FDE27);
				EdifPortRef FDE28_C = new EdifPortRef(wclk_c, FDE28_interface_C.getSingleBitPort(0), FDE28);
				EdifPortRef FDE28_D = new EdifPortRef(d1_c, FDE28_interface_D.getSingleBitPort(0), FDE28);
				EdifPortRef FDE28_CE = new EdifPortRef(e12, FDE28_interface_CE.getSingleBitPort(0), FDE28);
				EdifPortRef FDE28_Q = new EdifPortRef(wire28, FDE28_interface_Q.getSingleBitPort(0), FDE28);
				EdifPortRef FDE29_C = new EdifPortRef(wclk_c, FDE29_interface_C.getSingleBitPort(0), FDE29);
				EdifPortRef FDE29_D = new EdifPortRef(d1_c, FDE29_interface_D.getSingleBitPort(0), FDE29);
				EdifPortRef FDE29_CE = new EdifPortRef(e13, FDE29_interface_CE.getSingleBitPort(0), FDE29);
				EdifPortRef FDE29_Q = new EdifPortRef(wire29, FDE29_interface_Q.getSingleBitPort(0), FDE29);
				EdifPortRef FDE30_C = new EdifPortRef(wclk_c, FDE30_interface_C.getSingleBitPort(0), FDE30);
				EdifPortRef FDE30_D = new EdifPortRef(d1_c, FDE30_interface_D.getSingleBitPort(0), FDE30);
				EdifPortRef FDE30_CE = new EdifPortRef(e14, FDE30_interface_CE.getSingleBitPort(0), FDE30);
				EdifPortRef FDE30_Q = new EdifPortRef(wire30, FDE30_interface_Q.getSingleBitPort(0), FDE30);
				EdifPortRef FDE31_C = new EdifPortRef(wclk_c, FDE31_interface_C.getSingleBitPort(0), FDE31);
				EdifPortRef FDE31_D = new EdifPortRef(d1_c, FDE31_interface_D.getSingleBitPort(0), FDE31);
				EdifPortRef FDE31_CE = new EdifPortRef(e15, FDE31_interface_CE.getSingleBitPort(0), FDE31);
				EdifPortRef FDE31_Q = new EdifPortRef(wire31, FDE31_interface_Q.getSingleBitPort(0), FDE31);
				EdifPortRef FDE32_C = new EdifPortRef(wclk_c, FDE32_interface_C.getSingleBitPort(0), FDE32);
				EdifPortRef FDE32_D = new EdifPortRef(d1_c, FDE32_interface_D.getSingleBitPort(0), FDE32);
				EdifPortRef FDE32_CE = new EdifPortRef(e16, FDE32_interface_CE.getSingleBitPort(0), FDE32);
				EdifPortRef FDE32_Q = new EdifPortRef(wire32, FDE32_interface_Q.getSingleBitPort(0), FDE32);
				EdifPortRef LUT2_1_I0 = new EdifPortRef(a3_c, LUT2_1_interface_I0.getSingleBitPort(0), LUT2_1);
				EdifPortRef LUT2_1_I1 = new EdifPortRef(we_c, LUT2_1_interface_I1.getSingleBitPort(0), LUT2_1);
				EdifPortRef LUT2_1_O = new EdifPortRef(net29, LUT2_1_interface_O.getSingleBitPort(0), LUT2_1);
				EdifPortRef LUT2_2_I0 = new EdifPortRef(a3_c, LUT2_2_interface_I0.getSingleBitPort(0), LUT2_2);
				EdifPortRef LUT2_2_I1 = new EdifPortRef(we_c, LUT2_2_interface_I1.getSingleBitPort(0), LUT2_2);
				EdifPortRef LUT2_2_O = new EdifPortRef(net30, LUT2_2_interface_O.getSingleBitPort(0), LUT2_2);
				EdifPortRef LUT3_1_I0 = new EdifPortRef(a3_c, LUT3_1_interface_I0.getSingleBitPort(0), LUT3_1);
				EdifPortRef LUT3_1_I1 = new EdifPortRef(wire1, LUT3_1_interface_I1.getSingleBitPort(0), LUT3_1);
				EdifPortRef LUT3_1_I2 = new EdifPortRef(wire9, LUT3_1_interface_I2.getSingleBitPort(0), LUT3_1);
				EdifPortRef LUT3_1_O = new EdifPortRef(net1, LUT3_1_interface_O.getSingleBitPort(0), LUT3_1);
				EdifPortRef LUT3_2_I0 = new EdifPortRef(a3_c, LUT3_2_interface_I0.getSingleBitPort(0), LUT3_2);
				EdifPortRef LUT3_2_I1 = new EdifPortRef(wire5, LUT3_2_interface_I1.getSingleBitPort(0), LUT3_2);
				EdifPortRef LUT3_2_I2 = new EdifPortRef(wire13, LUT3_2_interface_I2.getSingleBitPort(0), LUT3_2);
				EdifPortRef LUT3_2_O = new EdifPortRef(net2, LUT3_2_interface_O.getSingleBitPort(0), LUT3_2);
				EdifPortRef LUT3_3_I0 = new EdifPortRef(a3_c, LUT3_3_interface_I0.getSingleBitPort(0), LUT3_3);
				EdifPortRef LUT3_3_I1 = new EdifPortRef(wire3, LUT3_3_interface_I1.getSingleBitPort(0), LUT3_3);
				EdifPortRef LUT3_3_I2 = new EdifPortRef(wire11, LUT3_3_interface_I2.getSingleBitPort(0), LUT3_3);
				EdifPortRef LUT3_3_O = new EdifPortRef(net3, LUT3_3_interface_O.getSingleBitPort(0), LUT3_3);
				EdifPortRef LUT3_4_I0 = new EdifPortRef(a3_c, LUT3_4_interface_I0.getSingleBitPort(0), LUT3_4);
				EdifPortRef LUT3_4_I1 = new EdifPortRef(wire7, LUT3_4_interface_I1.getSingleBitPort(0), LUT3_4);
				EdifPortRef LUT3_4_I2 = new EdifPortRef(wire15, LUT3_4_interface_I2.getSingleBitPort(0), LUT3_4);
				EdifPortRef LUT3_4_O = new EdifPortRef(net4, LUT3_4_interface_O.getSingleBitPort(0), LUT3_4);
				EdifPortRef LUT3_5_I0 = new EdifPortRef(a3_c, LUT3_5_interface_I0.getSingleBitPort(0), LUT3_5);
				EdifPortRef LUT3_5_I1 = new EdifPortRef(wire2, LUT3_5_interface_I1.getSingleBitPort(0), LUT3_5);
				EdifPortRef LUT3_5_I2 = new EdifPortRef(wire10, LUT3_5_interface_I2.getSingleBitPort(0), LUT3_5);
				EdifPortRef LUT3_5_O = new EdifPortRef(net7, LUT3_5_interface_O.getSingleBitPort(0), LUT3_5);
				EdifPortRef LUT3_6_I0 = new EdifPortRef(a3_c, LUT3_6_interface_I0.getSingleBitPort(0), LUT3_6);
				EdifPortRef LUT3_6_I1 = new EdifPortRef(wire6, LUT3_6_interface_I1.getSingleBitPort(0), LUT3_6);
				EdifPortRef LUT3_6_I2 = new EdifPortRef(wire14, LUT3_6_interface_I2.getSingleBitPort(0), LUT3_6);
				EdifPortRef LUT3_6_O = new EdifPortRef(net8, LUT3_6_interface_O.getSingleBitPort(0), LUT3_6);
				EdifPortRef LUT3_7_I0 = new EdifPortRef(a3_c, LUT3_7_interface_I0.getSingleBitPort(0), LUT3_7);
				EdifPortRef LUT3_7_I1 = new EdifPortRef(wire4, LUT3_7_interface_I1.getSingleBitPort(0), LUT3_7);
				EdifPortRef LUT3_7_I2 = new EdifPortRef(wire12, LUT3_7_interface_I2.getSingleBitPort(0), LUT3_7);
				EdifPortRef LUT3_7_O = new EdifPortRef(net9, LUT3_7_interface_O.getSingleBitPort(0), LUT3_7);
				EdifPortRef LUT3_8_I0 = new EdifPortRef(a3_c, LUT3_8_interface_I0.getSingleBitPort(0), LUT3_8);
				EdifPortRef LUT3_8_I1 = new EdifPortRef(wire8, LUT3_8_interface_I1.getSingleBitPort(0), LUT3_8);
				EdifPortRef LUT3_8_I2 = new EdifPortRef(wire16, LUT3_8_interface_I2.getSingleBitPort(0), LUT3_8);
				EdifPortRef LUT3_8_O = new EdifPortRef(net10, LUT3_8_interface_O.getSingleBitPort(0), LUT3_8);
				EdifPortRef LUT3_9_I0 = new EdifPortRef(net13, LUT3_9_interface_I0.getSingleBitPort(0), LUT3_9);
				EdifPortRef LUT3_9_I1 = new EdifPortRef(net14, LUT3_9_interface_I1.getSingleBitPort(0), LUT3_9);
				EdifPortRef LUT3_9_I2 = new EdifPortRef(a0_c, LUT3_9_interface_I2.getSingleBitPort(0), LUT3_9);
				EdifPortRef LUT3_9_O = new EdifPortRef(o0_c, LUT3_9_interface_O.getSingleBitPort(0), LUT3_9);
				EdifPortRef LUT3_10_I0 = new EdifPortRef(a3_c, LUT3_10_interface_I0.getSingleBitPort(0), LUT3_10);
				EdifPortRef LUT3_10_I1 = new EdifPortRef(wire17, LUT3_10_interface_I1.getSingleBitPort(0), LUT3_10);
				EdifPortRef LUT3_10_I2 = new EdifPortRef(wire25, LUT3_10_interface_I2.getSingleBitPort(0), LUT3_10);
				EdifPortRef LUT3_10_O = new EdifPortRef(net15, LUT3_10_interface_O.getSingleBitPort(0), LUT3_10);
				EdifPortRef LUT3_11_I0 = new EdifPortRef(a3_c, LUT3_11_interface_I0.getSingleBitPort(0), LUT3_11);
				EdifPortRef LUT3_11_I1 = new EdifPortRef(wire21, LUT3_11_interface_I1.getSingleBitPort(0), LUT3_11);
				EdifPortRef LUT3_11_I2 = new EdifPortRef(wire29, LUT3_11_interface_I2.getSingleBitPort(0), LUT3_11);
				EdifPortRef LUT3_11_O = new EdifPortRef(net16, LUT3_11_interface_O.getSingleBitPort(0), LUT3_11);
				EdifPortRef LUT3_12_I0 = new EdifPortRef(a3_c, LUT3_12_interface_I0.getSingleBitPort(0), LUT3_12);
				EdifPortRef LUT3_12_I1 = new EdifPortRef(wire19, LUT3_12_interface_I1.getSingleBitPort(0), LUT3_12);
				EdifPortRef LUT3_12_I2 = new EdifPortRef(wire27, LUT3_12_interface_I2.getSingleBitPort(0), LUT3_12);
				EdifPortRef LUT3_12_O = new EdifPortRef(net17, LUT3_12_interface_O.getSingleBitPort(0), LUT3_12);
				EdifPortRef LUT3_13_I0 = new EdifPortRef(a3_c, LUT3_13_interface_I0.getSingleBitPort(0), LUT3_13);
				EdifPortRef LUT3_13_I1 = new EdifPortRef(wire23, LUT3_13_interface_I1.getSingleBitPort(0), LUT3_13);
				EdifPortRef LUT3_13_I2 = new EdifPortRef(wire31, LUT3_13_interface_I2.getSingleBitPort(0), LUT3_13);
				EdifPortRef LUT3_13_O = new EdifPortRef(net18, LUT3_13_interface_O.getSingleBitPort(0), LUT3_13);
				EdifPortRef LUT3_14_I0 = new EdifPortRef(a3_c, LUT3_14_interface_I0.getSingleBitPort(0), LUT3_14);
				EdifPortRef LUT3_14_I1 = new EdifPortRef(wire18, LUT3_14_interface_I1.getSingleBitPort(0), LUT3_14);
				EdifPortRef LUT3_14_I2 = new EdifPortRef(wire26, LUT3_14_interface_I2.getSingleBitPort(0), LUT3_14);
				EdifPortRef LUT3_14_O = new EdifPortRef(net21, LUT3_14_interface_O.getSingleBitPort(0), LUT3_14);
				EdifPortRef LUT3_15_I0 = new EdifPortRef(a3_c, LUT3_15_interface_I0.getSingleBitPort(0), LUT3_15);
				EdifPortRef LUT3_15_I1 = new EdifPortRef(wire22, LUT3_15_interface_I1.getSingleBitPort(0), LUT3_15);
				EdifPortRef LUT3_15_I2 = new EdifPortRef(wire30, LUT3_15_interface_I2.getSingleBitPort(0), LUT3_15);
				EdifPortRef LUT3_15_O = new EdifPortRef(net22, LUT3_15_interface_O.getSingleBitPort(0), LUT3_15);
				EdifPortRef LUT3_16_I0 = new EdifPortRef(a3_c, LUT3_16_interface_I0.getSingleBitPort(0), LUT3_16);
				EdifPortRef LUT3_16_I1 = new EdifPortRef(wire20, LUT3_16_interface_I1.getSingleBitPort(0), LUT3_16);
				EdifPortRef LUT3_16_I2 = new EdifPortRef(wire28, LUT3_16_interface_I2.getSingleBitPort(0), LUT3_16);
				EdifPortRef LUT3_16_O = new EdifPortRef(net23, LUT3_16_interface_O.getSingleBitPort(0), LUT3_16);
				EdifPortRef LUT3_17_I0 = new EdifPortRef(a3_c, LUT3_17_interface_I0.getSingleBitPort(0), LUT3_17);
				EdifPortRef LUT3_17_I1 = new EdifPortRef(wire24, LUT3_17_interface_I1.getSingleBitPort(0), LUT3_17);
				EdifPortRef LUT3_17_I2 = new EdifPortRef(wire32, LUT3_17_interface_I2.getSingleBitPort(0), LUT3_17);
				EdifPortRef LUT3_17_O = new EdifPortRef(net24, LUT3_17_interface_O.getSingleBitPort(0), LUT3_17);
				EdifPortRef LUT3_18_I0 = new EdifPortRef(net27, LUT3_18_interface_I0.getSingleBitPort(0), LUT3_18);
				EdifPortRef LUT3_18_I1 = new EdifPortRef(net28, LUT3_18_interface_I1.getSingleBitPort(0), LUT3_18);
				EdifPortRef LUT3_18_I2 = new EdifPortRef(a0_c, LUT3_18_interface_I2.getSingleBitPort(0), LUT3_18);
				EdifPortRef LUT3_18_O = new EdifPortRef(o1_c, LUT3_18_interface_O.getSingleBitPort(0), LUT3_18);
				EdifPortRef LUT4_1_I0 = new EdifPortRef(net29, LUT4_1_interface_I0.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_I1 = new EdifPortRef(a0_c, LUT4_1_interface_I1.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_I2 = new EdifPortRef(a1_c, LUT4_1_interface_I2.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_I3 = new EdifPortRef(a2_c, LUT4_1_interface_I3.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_O = new EdifPortRef(e1, LUT4_1_interface_O.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_2_I0 = new EdifPortRef(net29, LUT4_2_interface_I0.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_I1 = new EdifPortRef(a0_c, LUT4_2_interface_I1.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_I2 = new EdifPortRef(a1_c, LUT4_2_interface_I2.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_I3 = new EdifPortRef(a2_c, LUT4_2_interface_I3.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_O = new EdifPortRef(e2, LUT4_2_interface_O.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_3_I0 = new EdifPortRef(net29, LUT4_3_interface_I0.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_I1 = new EdifPortRef(a0_c, LUT4_3_interface_I1.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_I2 = new EdifPortRef(a1_c, LUT4_3_interface_I2.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_I3 = new EdifPortRef(a2_c, LUT4_3_interface_I3.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_O = new EdifPortRef(e3, LUT4_3_interface_O.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_4_I0 = new EdifPortRef(net29, LUT4_4_interface_I0.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_I1 = new EdifPortRef(a0_c, LUT4_4_interface_I1.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_I2 = new EdifPortRef(a1_c, LUT4_4_interface_I2.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_I3 = new EdifPortRef(a2_c, LUT4_4_interface_I3.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_O = new EdifPortRef(e4, LUT4_4_interface_O.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_5_I0 = new EdifPortRef(net29, LUT4_5_interface_I0.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_I1 = new EdifPortRef(a0_c, LUT4_5_interface_I1.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_I2 = new EdifPortRef(a1_c, LUT4_5_interface_I2.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_I3 = new EdifPortRef(a2_c, LUT4_5_interface_I3.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_O = new EdifPortRef(e5, LUT4_5_interface_O.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_6_I0 = new EdifPortRef(net29, LUT4_6_interface_I0.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_I1 = new EdifPortRef(a0_c, LUT4_6_interface_I1.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_I2 = new EdifPortRef(a1_c, LUT4_6_interface_I2.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_I3 = new EdifPortRef(a2_c, LUT4_6_interface_I3.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_O = new EdifPortRef(e6, LUT4_6_interface_O.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_7_I0 = new EdifPortRef(net29, LUT4_7_interface_I0.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_I1 = new EdifPortRef(a0_c, LUT4_7_interface_I1.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_I2 = new EdifPortRef(a1_c, LUT4_7_interface_I2.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_I3 = new EdifPortRef(a2_c, LUT4_7_interface_I3.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_O = new EdifPortRef(e7, LUT4_7_interface_O.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_8_I0 = new EdifPortRef(net29, LUT4_8_interface_I0.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_I1 = new EdifPortRef(a0_c, LUT4_8_interface_I1.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_I2 = new EdifPortRef(a1_c, LUT4_8_interface_I2.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_I3 = new EdifPortRef(a2_c, LUT4_8_interface_I3.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_O = new EdifPortRef(e8, LUT4_8_interface_O.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_9_I0 = new EdifPortRef(net30, LUT4_9_interface_I0.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_I1 = new EdifPortRef(a0_c, LUT4_9_interface_I1.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_I2 = new EdifPortRef(a1_c, LUT4_9_interface_I2.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_I3 = new EdifPortRef(a2_c, LUT4_9_interface_I3.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_O = new EdifPortRef(e9, LUT4_9_interface_O.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_10_I0 = new EdifPortRef(net30, LUT4_10_interface_I0.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_I1 = new EdifPortRef(a0_c, LUT4_10_interface_I1.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_I2 = new EdifPortRef(a1_c, LUT4_10_interface_I2.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_I3 = new EdifPortRef(a2_c, LUT4_10_interface_I3.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_O = new EdifPortRef(e10, LUT4_10_interface_O.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_11_I0 = new EdifPortRef(net30, LUT4_11_interface_I0.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_I1 = new EdifPortRef(a0_c, LUT4_11_interface_I1.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_I2 = new EdifPortRef(a1_c, LUT4_11_interface_I2.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_I3 = new EdifPortRef(a2_c, LUT4_11_interface_I3.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_O = new EdifPortRef(e11, LUT4_11_interface_O.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_12_I0 = new EdifPortRef(net30, LUT4_12_interface_I0.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_I1 = new EdifPortRef(a0_c, LUT4_12_interface_I1.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_I2 = new EdifPortRef(a1_c, LUT4_12_interface_I2.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_I3 = new EdifPortRef(a2_c, LUT4_12_interface_I3.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_O = new EdifPortRef(e12, LUT4_12_interface_O.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_13_I0 = new EdifPortRef(net30, LUT4_13_interface_I0.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_I1 = new EdifPortRef(a0_c, LUT4_13_interface_I1.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_I2 = new EdifPortRef(a1_c, LUT4_13_interface_I2.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_I3 = new EdifPortRef(a2_c, LUT4_13_interface_I3.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_O = new EdifPortRef(e13, LUT4_13_interface_O.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_14_I0 = new EdifPortRef(net30, LUT4_14_interface_I0.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_I1 = new EdifPortRef(a0_c, LUT4_14_interface_I1.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_I2 = new EdifPortRef(a1_c, LUT4_14_interface_I2.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_I3 = new EdifPortRef(a2_c, LUT4_14_interface_I3.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_O = new EdifPortRef(e14, LUT4_14_interface_O.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_15_I0 = new EdifPortRef(net30, LUT4_15_interface_I0.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_I1 = new EdifPortRef(a0_c, LUT4_15_interface_I1.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_I2 = new EdifPortRef(a1_c, LUT4_15_interface_I2.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_I3 = new EdifPortRef(a2_c, LUT4_15_interface_I3.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_O = new EdifPortRef(e15, LUT4_15_interface_O.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_16_I0 = new EdifPortRef(net30, LUT4_16_interface_I0.getSingleBitPort(0), LUT4_16);
				EdifPortRef LUT4_16_I1 = new EdifPortRef(a0_c, LUT4_16_interface_I1.getSingleBitPort(0), LUT4_16);
				EdifPortRef LUT4_16_I2 = new EdifPortRef(a1_c, LUT4_16_interface_I2.getSingleBitPort(0), LUT4_16);
				EdifPortRef LUT4_16_I3 = new EdifPortRef(a2_c, LUT4_16_interface_I3.getSingleBitPort(0), LUT4_16);
				EdifPortRef LUT4_16_O = new EdifPortRef(e16, LUT4_16_interface_O.getSingleBitPort(0), LUT4_16);
				EdifPortRef MUXF5_1_I0 = new EdifPortRef(net1, MUXF5_1_interface_I0.getSingleBitPort(0), MUXF5_1);
				EdifPortRef MUXF5_1_I1 = new EdifPortRef(net2, MUXF5_1_interface_I1.getSingleBitPort(0), MUXF5_1);
				EdifPortRef MUXF5_1_S = new EdifPortRef(a2_c, MUXF5_1_interface_S.getSingleBitPort(0), MUXF5_1);
				EdifPortRef MUXF5_1_O = new EdifPortRef(net5, MUXF5_1_interface_O.getSingleBitPort(0), MUXF5_1);
				EdifPortRef MUXF5_2_I0 = new EdifPortRef(net3, MUXF5_2_interface_I0.getSingleBitPort(0), MUXF5_2);
				EdifPortRef MUXF5_2_I1 = new EdifPortRef(net4, MUXF5_2_interface_I1.getSingleBitPort(0), MUXF5_2);
				EdifPortRef MUXF5_2_S = new EdifPortRef(a2_c, MUXF5_2_interface_S.getSingleBitPort(0), MUXF5_2);
				EdifPortRef MUXF5_2_O = new EdifPortRef(net6, MUXF5_2_interface_O.getSingleBitPort(0), MUXF5_2);
				EdifPortRef MUXF5_3_I0 = new EdifPortRef(net7, MUXF5_3_interface_I0.getSingleBitPort(0), MUXF5_3);
				EdifPortRef MUXF5_3_I1 = new EdifPortRef(net8, MUXF5_3_interface_I1.getSingleBitPort(0), MUXF5_3);
				EdifPortRef MUXF5_3_S = new EdifPortRef(a2_c, MUXF5_3_interface_S.getSingleBitPort(0), MUXF5_3);
				EdifPortRef MUXF5_3_O = new EdifPortRef(net11, MUXF5_3_interface_O.getSingleBitPort(0), MUXF5_3);
				EdifPortRef MUXF5_4_I0 = new EdifPortRef(net9, MUXF5_4_interface_I0.getSingleBitPort(0), MUXF5_4);
				EdifPortRef MUXF5_4_I1 = new EdifPortRef(net10, MUXF5_4_interface_I1.getSingleBitPort(0), MUXF5_4);
				EdifPortRef MUXF5_4_S = new EdifPortRef(a2_c, MUXF5_4_interface_S.getSingleBitPort(0), MUXF5_4);
				EdifPortRef MUXF5_4_O = new EdifPortRef(net12, MUXF5_4_interface_O.getSingleBitPort(0), MUXF5_4);
				EdifPortRef MUXF5_5_I0 = new EdifPortRef(net15, MUXF5_5_interface_I0.getSingleBitPort(0), MUXF5_5);
				EdifPortRef MUXF5_5_I1 = new EdifPortRef(net16, MUXF5_5_interface_I1.getSingleBitPort(0), MUXF5_5);
				EdifPortRef MUXF5_5_S = new EdifPortRef(a2_c, MUXF5_5_interface_S.getSingleBitPort(0), MUXF5_5);
				EdifPortRef MUXF5_5_O = new EdifPortRef(net19, MUXF5_5_interface_O.getSingleBitPort(0), MUXF5_5);
				EdifPortRef MUXF5_6_I0 = new EdifPortRef(net17, MUXF5_6_interface_I0.getSingleBitPort(0), MUXF5_6);
				EdifPortRef MUXF5_6_I1 = new EdifPortRef(net18, MUXF5_6_interface_I1.getSingleBitPort(0), MUXF5_6);
				EdifPortRef MUXF5_6_S = new EdifPortRef(a2_c, MUXF5_6_interface_S.getSingleBitPort(0), MUXF5_6);
				EdifPortRef MUXF5_6_O = new EdifPortRef(net20, MUXF5_6_interface_O.getSingleBitPort(0), MUXF5_6);
				EdifPortRef MUXF5_7_I0 = new EdifPortRef(net21, MUXF5_7_interface_I0.getSingleBitPort(0), MUXF5_7);
				EdifPortRef MUXF5_7_I1 = new EdifPortRef(net22, MUXF5_7_interface_I1.getSingleBitPort(0), MUXF5_7);
				EdifPortRef MUXF5_7_S = new EdifPortRef(a2_c, MUXF5_7_interface_S.getSingleBitPort(0), MUXF5_7);
				EdifPortRef MUXF5_7_O = new EdifPortRef(net25, MUXF5_7_interface_O.getSingleBitPort(0), MUXF5_7);
				EdifPortRef MUXF5_8_I0 = new EdifPortRef(net23, MUXF5_8_interface_I0.getSingleBitPort(0), MUXF5_8);
				EdifPortRef MUXF5_8_I1 = new EdifPortRef(net24, MUXF5_8_interface_I1.getSingleBitPort(0), MUXF5_8);
				EdifPortRef MUXF5_8_S = new EdifPortRef(a2_c, MUXF5_8_interface_S.getSingleBitPort(0), MUXF5_8);
				EdifPortRef MUXF5_8_O = new EdifPortRef(net26, MUXF5_8_interface_O.getSingleBitPort(0), MUXF5_8);
				EdifPortRef MUXF6_1_I0 = new EdifPortRef(net5, MUXF6_1_interface_I0.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_I1 = new EdifPortRef(net6, MUXF6_1_interface_I1.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_S = new EdifPortRef(a1_c, MUXF6_1_interface_S.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_O = new EdifPortRef(net13, MUXF6_1_interface_O.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_2_I0 = new EdifPortRef(net11, MUXF6_2_interface_I0.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_I1 = new EdifPortRef(net12, MUXF6_2_interface_I1.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_S = new EdifPortRef(a1_c, MUXF6_2_interface_S.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_O = new EdifPortRef(net14, MUXF6_2_interface_O.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_3_I0 = new EdifPortRef(net19, MUXF6_3_interface_I0.getSingleBitPort(0), MUXF6_3);
				EdifPortRef MUXF6_3_I1 = new EdifPortRef(net20, MUXF6_3_interface_I1.getSingleBitPort(0), MUXF6_3);
				EdifPortRef MUXF6_3_S = new EdifPortRef(a1_c, MUXF6_3_interface_S.getSingleBitPort(0), MUXF6_3);
				EdifPortRef MUXF6_3_O = new EdifPortRef(net27, MUXF6_3_interface_O.getSingleBitPort(0), MUXF6_3);
				EdifPortRef MUXF6_4_I0 = new EdifPortRef(net25, MUXF6_4_interface_I0.getSingleBitPort(0), MUXF6_4);
				EdifPortRef MUXF6_4_I1 = new EdifPortRef(net26, MUXF6_4_interface_I1.getSingleBitPort(0), MUXF6_4);
				EdifPortRef MUXF6_4_S = new EdifPortRef(a1_c, MUXF6_4_interface_S.getSingleBitPort(0), MUXF6_4);
				EdifPortRef MUXF6_4_O = new EdifPortRef(net28, MUXF6_4_interface_O.getSingleBitPort(0), MUXF6_4);
				//we
				we.addPortConnection(RAM16X2S_replacement_WE);
				we.addPortConnection(WE_ibuf_I);
				//we_c
				we_c.addPortConnection(WE_ibuf_O);
				we_c.addPortConnection(LUT2_1_I1);
				we_c.addPortConnection(LUT2_2_I1);
				//d0
				d0.addPortConnection(RAM16X2S_replacement_D0);
				d0.addPortConnection(D0_ibuf_I);
				//d0_c
				d0_c.addPortConnection(D0_ibuf_O);
				d0_c.addPortConnection(FDE1_D);
				d0_c.addPortConnection(FDE2_D);
				d0_c.addPortConnection(FDE3_D);
				d0_c.addPortConnection(FDE4_D);
				d0_c.addPortConnection(FDE5_D);
				d0_c.addPortConnection(FDE6_D);
				d0_c.addPortConnection(FDE7_D);
				d0_c.addPortConnection(FDE8_D);
				d0_c.addPortConnection(FDE9_D);
				d0_c.addPortConnection(FDE10_D);
				d0_c.addPortConnection(FDE11_D);
				d0_c.addPortConnection(FDE12_D);
				d0_c.addPortConnection(FDE13_D);
				d0_c.addPortConnection(FDE14_D);
				d0_c.addPortConnection(FDE15_D);
				d0_c.addPortConnection(FDE16_D);
				//d1
				d1.addPortConnection(RAM16X2S_replacement_D1);
				d1.addPortConnection(D1_ibuf_I);
				//d1_c
				d1_c.addPortConnection(D1_ibuf_O);
				d1_c.addPortConnection(FDE17_D);
				d1_c.addPortConnection(FDE18_D);
				d1_c.addPortConnection(FDE19_D);
				d1_c.addPortConnection(FDE20_D);
				d1_c.addPortConnection(FDE21_D);
				d1_c.addPortConnection(FDE22_D);
				d1_c.addPortConnection(FDE23_D);
				d1_c.addPortConnection(FDE24_D);
				d1_c.addPortConnection(FDE25_D);
				d1_c.addPortConnection(FDE26_D);
				d1_c.addPortConnection(FDE27_D);
				d1_c.addPortConnection(FDE28_D);
				d1_c.addPortConnection(FDE29_D);
				d1_c.addPortConnection(FDE30_D);
				d1_c.addPortConnection(FDE31_D);
				d1_c.addPortConnection(FDE32_D);
				//wclk
				wclk.addPortConnection(WCLK_ibuf_I);
				wclk.addPortConnection(RAM16X2S_replacement_WCLK);
				//wclk_c
				wclk_c.addPortConnection(WCLK_ibuf_O);
				wclk_c.addPortConnection(FDE1_C);
				wclk_c.addPortConnection(FDE2_C);
				wclk_c.addPortConnection(FDE3_C);
				wclk_c.addPortConnection(FDE4_C);
				wclk_c.addPortConnection(FDE5_C);
				wclk_c.addPortConnection(FDE6_C);
				wclk_c.addPortConnection(FDE7_C);
				wclk_c.addPortConnection(FDE8_C);
				wclk_c.addPortConnection(FDE9_C);
				wclk_c.addPortConnection(FDE10_C);
				wclk_c.addPortConnection(FDE11_C);
				wclk_c.addPortConnection(FDE12_C);
				wclk_c.addPortConnection(FDE13_C);
				wclk_c.addPortConnection(FDE14_C);
				wclk_c.addPortConnection(FDE15_C);
				wclk_c.addPortConnection(FDE16_C);
				wclk_c.addPortConnection(FDE17_C);
				wclk_c.addPortConnection(FDE18_C);
				wclk_c.addPortConnection(FDE19_C);
				wclk_c.addPortConnection(FDE20_C);
				wclk_c.addPortConnection(FDE21_C);
				wclk_c.addPortConnection(FDE22_C);
				wclk_c.addPortConnection(FDE23_C);
				wclk_c.addPortConnection(FDE24_C);
				wclk_c.addPortConnection(FDE25_C);
				wclk_c.addPortConnection(FDE26_C);
				wclk_c.addPortConnection(FDE27_C);
				wclk_c.addPortConnection(FDE28_C);
				wclk_c.addPortConnection(FDE29_C);
				wclk_c.addPortConnection(FDE30_C);
				wclk_c.addPortConnection(FDE31_C);
				wclk_c.addPortConnection(FDE32_C);
				//a0
				a0.addPortConnection(RAM16X2S_replacement_A0);
				a0.addPortConnection(A0_ibuf_I);
				//a0_c
				a0_c.addPortConnection(A0_ibuf_O);
				a0_c.addPortConnection(LUT4_1_I1);
				a0_c.addPortConnection(LUT4_2_I1);
				a0_c.addPortConnection(LUT4_3_I1);
				a0_c.addPortConnection(LUT4_4_I1);
				a0_c.addPortConnection(LUT4_5_I1);
				a0_c.addPortConnection(LUT4_6_I1);
				a0_c.addPortConnection(LUT4_7_I1);
				a0_c.addPortConnection(LUT4_8_I1);
				a0_c.addPortConnection(LUT4_9_I1);
				a0_c.addPortConnection(LUT4_10_I1);
				a0_c.addPortConnection(LUT4_11_I1);
				a0_c.addPortConnection(LUT4_12_I1);
				a0_c.addPortConnection(LUT4_13_I1);
				a0_c.addPortConnection(LUT4_14_I1);
				a0_c.addPortConnection(LUT4_15_I1);
				a0_c.addPortConnection(LUT4_16_I1);
				a0_c.addPortConnection(LUT3_9_I2);
				a0_c.addPortConnection(LUT3_18_I2);
				//a1
				a1.addPortConnection(RAM16X2S_replacement_A1);
				a1.addPortConnection(A1_ibuf_I);
				//a1_c
				a1_c.addPortConnection(A1_ibuf_O);
				a1_c.addPortConnection(LUT4_1_I2);
				a1_c.addPortConnection(LUT4_2_I2);
				a1_c.addPortConnection(LUT4_3_I2);
				a1_c.addPortConnection(LUT4_4_I2);
				a1_c.addPortConnection(LUT4_5_I2);
				a1_c.addPortConnection(LUT4_6_I2);
				a1_c.addPortConnection(LUT4_7_I2);
				a1_c.addPortConnection(LUT4_8_I2);
				a1_c.addPortConnection(LUT4_9_I2);
				a1_c.addPortConnection(LUT4_10_I2);
				a1_c.addPortConnection(LUT4_11_I2);
				a1_c.addPortConnection(LUT4_12_I2);
				a1_c.addPortConnection(LUT4_13_I2);
				a1_c.addPortConnection(LUT4_14_I2);
				a1_c.addPortConnection(LUT4_15_I2);
				a1_c.addPortConnection(LUT4_16_I2);
				a1_c.addPortConnection(MUXF6_1_S);
				a1_c.addPortConnection(MUXF6_2_S);
				a1_c.addPortConnection(MUXF6_3_S);
				a1_c.addPortConnection(MUXF6_4_S);
				//a2
				a2.addPortConnection(RAM16X2S_replacement_A2);
				a2.addPortConnection(A2_ibuf_I);
				//a2_c
				a2_c.addPortConnection(A2_ibuf_O);
				a2_c.addPortConnection(LUT4_1_I3);
				a2_c.addPortConnection(LUT4_2_I3);
				a2_c.addPortConnection(LUT4_3_I3);
				a2_c.addPortConnection(LUT4_4_I3);
				a2_c.addPortConnection(LUT4_5_I3);
				a2_c.addPortConnection(LUT4_6_I3);
				a2_c.addPortConnection(LUT4_7_I3);
				a2_c.addPortConnection(LUT4_8_I3);
				a2_c.addPortConnection(LUT4_9_I3);
				a2_c.addPortConnection(LUT4_10_I3);
				a2_c.addPortConnection(LUT4_11_I3);
				a2_c.addPortConnection(LUT4_12_I3);
				a2_c.addPortConnection(LUT4_13_I3);
				a2_c.addPortConnection(LUT4_14_I3);
				a2_c.addPortConnection(LUT4_15_I3);
				a2_c.addPortConnection(LUT4_16_I3);
				a2_c.addPortConnection(MUXF5_1_S);
				a2_c.addPortConnection(MUXF5_2_S);
				a2_c.addPortConnection(MUXF5_3_S);
				a2_c.addPortConnection(MUXF5_4_S);
				a2_c.addPortConnection(MUXF5_5_S);
				a2_c.addPortConnection(MUXF5_6_S);
				a2_c.addPortConnection(MUXF5_7_S);
				a2_c.addPortConnection(MUXF5_8_S);
				//a3
				a3.addPortConnection(RAM16X2S_replacement_A3);
				a3.addPortConnection(A3_ibuf_I);
				//a3_c
				a3_c.addPortConnection(A3_ibuf_O);
				a3_c.addPortConnection(LUT2_1_I0);
				a3_c.addPortConnection(LUT2_2_I0);
				a3_c.addPortConnection(LUT3_1_I0);
				a3_c.addPortConnection(LUT3_2_I0);
				a3_c.addPortConnection(LUT3_3_I0);
				a3_c.addPortConnection(LUT3_4_I0);
				a3_c.addPortConnection(LUT3_5_I0);
				a3_c.addPortConnection(LUT3_6_I0);
				a3_c.addPortConnection(LUT3_7_I0);
				a3_c.addPortConnection(LUT3_8_I0);
				a3_c.addPortConnection(LUT3_10_I0);
				a3_c.addPortConnection(LUT3_11_I0);
				a3_c.addPortConnection(LUT3_12_I0);
				a3_c.addPortConnection(LUT3_13_I0);
				a3_c.addPortConnection(LUT3_14_I0);
				a3_c.addPortConnection(LUT3_15_I0);
				a3_c.addPortConnection(LUT3_16_I0);
				a3_c.addPortConnection(LUT3_17_I0);
				//o0
				o0.addPortConnection(O0_obuf_O);
				o0.addPortConnection(RAM16X2S_replacement_O0);
				//o0_c
				o0_c.addPortConnection(O0_obuf_I);
				o0_c.addPortConnection(LUT3_9_O);
				//o1
				o1.addPortConnection(O1_obuf_O);
				o1.addPortConnection(RAM16X2S_replacement_O1);
				//o1_c
				o1_c.addPortConnection(O1_obuf_I);
				o1_c.addPortConnection(LUT3_18_O);
				//wire1
				wire1.addPortConnection(LUT3_1_I1);
				wire1.addPortConnection(FDE1_Q);
				//wire2
				wire2.addPortConnection(LUT3_5_I1);
				wire2.addPortConnection(FDE2_Q);
				//wire3
				wire3.addPortConnection(LUT3_3_I1);
				wire3.addPortConnection(FDE3_Q);
				//wire4
				wire4.addPortConnection(LUT3_7_I1);
				wire4.addPortConnection(FDE4_Q);
				//wire5
				wire5.addPortConnection(LUT3_2_I1);
				wire5.addPortConnection(FDE5_Q);
				//wire6
				wire6.addPortConnection(LUT3_6_I1);
				wire6.addPortConnection(FDE6_Q);
				//wire7
				wire7.addPortConnection(LUT3_4_I1);
				wire7.addPortConnection(FDE7_Q);
				//wire8
				wire8.addPortConnection(LUT3_8_I1);
				wire8.addPortConnection(FDE8_Q);
				//wire9
				wire9.addPortConnection(LUT3_1_I2);
				wire9.addPortConnection(FDE9_Q);
				//wire10
				wire10.addPortConnection(LUT3_5_I2);
				wire10.addPortConnection(FDE10_Q);
				//wire11
				wire11.addPortConnection(LUT3_3_I2);
				wire11.addPortConnection(FDE11_Q);
				//wire12
				wire12.addPortConnection(LUT3_7_I2);
				wire12.addPortConnection(FDE12_Q);
				//wire13
				wire13.addPortConnection(LUT3_2_I2);
				wire13.addPortConnection(FDE13_Q);
				//wire14
				wire14.addPortConnection(LUT3_6_I2);
				wire14.addPortConnection(FDE14_Q);
				//wire15
				wire15.addPortConnection(LUT3_4_I2);
				wire15.addPortConnection(FDE15_Q);
				//wire16
				wire16.addPortConnection(LUT3_8_I2);
				wire16.addPortConnection(FDE16_Q);
				//wire17
				wire17.addPortConnection(LUT3_10_I1);
				wire17.addPortConnection(FDE17_Q);
				//wire18
				wire18.addPortConnection(LUT3_14_I1);
				wire18.addPortConnection(FDE18_Q);
				//wire19
				wire19.addPortConnection(LUT3_12_I1);
				wire19.addPortConnection(FDE19_Q);
				//wire20
				wire20.addPortConnection(LUT3_16_I1);
				wire20.addPortConnection(FDE20_Q);
				//wire21
				wire21.addPortConnection(LUT3_11_I1);
				wire21.addPortConnection(FDE21_Q);
				//wire22
				wire22.addPortConnection(LUT3_15_I1);
				wire22.addPortConnection(FDE22_Q);
				//wire23
				wire23.addPortConnection(LUT3_13_I1);
				wire23.addPortConnection(FDE23_Q);
				//wire24
				wire24.addPortConnection(LUT3_17_I1);
				wire24.addPortConnection(FDE24_Q);
				//wire25
				wire25.addPortConnection(LUT3_10_I2);
				wire25.addPortConnection(FDE25_Q);
				//wire26
				wire26.addPortConnection(LUT3_14_I2);
				wire26.addPortConnection(FDE26_Q);
				//wire27
				wire27.addPortConnection(LUT3_12_I2);
				wire27.addPortConnection(FDE27_Q);
				//wire28
				wire28.addPortConnection(LUT3_16_I2);
				wire28.addPortConnection(FDE28_Q);
				//wire29
				wire29.addPortConnection(LUT3_11_I2);
				wire29.addPortConnection(FDE29_Q);
				//wire30
				wire30.addPortConnection(LUT3_15_I2);
				wire30.addPortConnection(FDE30_Q);
				//wire31
				wire31.addPortConnection(LUT3_13_I2);
				wire31.addPortConnection(FDE31_Q);
				//wire32
				wire32.addPortConnection(LUT3_17_I2);
				wire32.addPortConnection(FDE32_Q);
				//net1
				net1.addPortConnection(LUT3_1_O);
				net1.addPortConnection(MUXF5_1_I0);
				//net2
				net2.addPortConnection(LUT3_2_O);
				net2.addPortConnection(MUXF5_1_I1);
				//net3
				net3.addPortConnection(LUT3_3_O);
				net3.addPortConnection(MUXF5_2_I0);
				//net4
				net4.addPortConnection(LUT3_4_O);
				net4.addPortConnection(MUXF5_2_I1);
				//net5
				net5.addPortConnection(MUXF5_1_O);
				net5.addPortConnection(MUXF6_1_I0);
				//net6
				net6.addPortConnection(MUXF5_2_O);
				net6.addPortConnection(MUXF6_1_I1);
				//net7
				net7.addPortConnection(LUT3_5_O);
				net7.addPortConnection(MUXF5_3_I0);
				//net8
				net8.addPortConnection(LUT3_6_O);
				net8.addPortConnection(MUXF5_3_I1);
				//net9
				net9.addPortConnection(LUT3_7_O);
				net9.addPortConnection(MUXF5_4_I0);
				//net10
				net10.addPortConnection(LUT3_8_O);
				net10.addPortConnection(MUXF5_4_I1);
				//net11
				net11.addPortConnection(MUXF5_3_O);
				net11.addPortConnection(MUXF6_2_I0);
				//net12
				net12.addPortConnection(MUXF5_4_O);
				net12.addPortConnection(MUXF6_2_I1);
				//net13
				net13.addPortConnection(MUXF6_1_O);
				net13.addPortConnection(LUT3_9_I0);
				//net14
				net14.addPortConnection(MUXF6_2_O);
				net14.addPortConnection(LUT3_9_I1);
				//net15
				net15.addPortConnection(LUT3_10_O);
				net15.addPortConnection(MUXF5_5_I0);
				//net16
				net16.addPortConnection(LUT3_11_O);
				net16.addPortConnection(MUXF5_5_I1);
				//net17
				net17.addPortConnection(LUT3_12_O);
				net17.addPortConnection(MUXF5_6_I0);
				//net18
				net18.addPortConnection(LUT3_13_O);
				net18.addPortConnection(MUXF5_6_I1);
				//net19
				net19.addPortConnection(MUXF5_5_O);
				net19.addPortConnection(MUXF6_3_I0);
				//net20
				net20.addPortConnection(MUXF5_6_O);
				net20.addPortConnection(MUXF6_3_I1);
				//net21
				net21.addPortConnection(LUT3_14_O);
				net21.addPortConnection(MUXF5_7_I0);
				//net22
				net22.addPortConnection(LUT3_15_O);
				net22.addPortConnection(MUXF5_7_I1);
				//net23
				net23.addPortConnection(LUT3_16_O);
				net23.addPortConnection(MUXF5_8_I0);
				//net24
				net24.addPortConnection(LUT3_17_O);
				net24.addPortConnection(MUXF5_8_I1);
				//net25
				net25.addPortConnection(MUXF5_7_O);
				net25.addPortConnection(MUXF6_4_I0);
				//net26
				net26.addPortConnection(MUXF5_8_O);
				net26.addPortConnection(MUXF6_4_I1);
				//net27
				net27.addPortConnection(MUXF6_3_O);
				net27.addPortConnection(LUT3_18_I0);
				//net28
				net28.addPortConnection(MUXF6_4_O);
				net28.addPortConnection(LUT3_18_I1);
				//net29
				net29.addPortConnection(LUT2_1_O);
				net29.addPortConnection(LUT4_1_I0);
				net29.addPortConnection(LUT4_2_I0);
				net29.addPortConnection(LUT4_3_I0);
				net29.addPortConnection(LUT4_4_I0);
				net29.addPortConnection(LUT4_5_I0);
				net29.addPortConnection(LUT4_6_I0);
				net29.addPortConnection(LUT4_7_I0);
				net29.addPortConnection(LUT4_8_I0);
				//net30
				net30.addPortConnection(LUT2_2_O);
				net30.addPortConnection(LUT4_9_I0);
				net30.addPortConnection(LUT4_10_I0);
				net30.addPortConnection(LUT4_11_I0);
				net30.addPortConnection(LUT4_12_I0);
				net30.addPortConnection(LUT4_13_I0);
				net30.addPortConnection(LUT4_14_I0);
				net30.addPortConnection(LUT4_15_I0);
				net30.addPortConnection(LUT4_16_I0);
				//e1
				e1.addPortConnection(LUT4_1_O);
				e1.addPortConnection(FDE1_CE);
				e1.addPortConnection(FDE17_CE);
				//e2
				e2.addPortConnection(LUT4_2_O);
				e2.addPortConnection(FDE2_CE);
				e2.addPortConnection(FDE18_CE);
				//e3
				e3.addPortConnection(LUT4_3_O);
				e3.addPortConnection(FDE3_CE);
				e3.addPortConnection(FDE19_CE);
				//e4
				e4.addPortConnection(LUT4_4_O);
				e4.addPortConnection(FDE4_CE);
				e4.addPortConnection(FDE20_CE);
				//e5
				e5.addPortConnection(LUT4_5_O);
				e5.addPortConnection(FDE5_CE);
				e5.addPortConnection(FDE21_CE);
				//e6
				e6.addPortConnection(LUT4_6_O);
				e6.addPortConnection(FDE6_CE);
				e6.addPortConnection(FDE22_CE);
				//e7
				e7.addPortConnection(LUT4_7_O);
				e7.addPortConnection(FDE7_CE);
				e7.addPortConnection(FDE23_CE);
				//e8
				e8.addPortConnection(LUT4_8_O);
				e8.addPortConnection(FDE8_CE);
				e8.addPortConnection(FDE24_CE);
				//e9
				e9.addPortConnection(LUT4_9_O);
				e9.addPortConnection(FDE9_CE);
				e9.addPortConnection(FDE25_CE);
				//e10
				e10.addPortConnection(LUT4_10_O);
				e10.addPortConnection(FDE10_CE);
				e10.addPortConnection(FDE26_CE);
				//e11
				e11.addPortConnection(LUT4_11_O);
				e11.addPortConnection(FDE11_CE);
				e11.addPortConnection(FDE27_CE);
				//e12
				e12.addPortConnection(LUT4_12_O);
				e12.addPortConnection(FDE12_CE);
				e12.addPortConnection(FDE28_CE);
				//e13
				e13.addPortConnection(LUT4_13_O);
				e13.addPortConnection(FDE13_CE);
				e13.addPortConnection(FDE29_CE);
				//e14
				e14.addPortConnection(LUT4_14_O);
				e14.addPortConnection(FDE14_CE);
				e14.addPortConnection(FDE30_CE);
				//e15
				e15.addPortConnection(LUT4_15_O);
				e15.addPortConnection(FDE15_CE);
				e15.addPortConnection(FDE31_CE);
				//e16
				e16.addPortConnection(LUT4_16_O);
				e16.addPortConnection(FDE16_CE);
				e16.addPortConnection(FDE32_CE);
				
				/******Set INIT Property******/
				boolean isInit;
				int initCount_00 = 0;
				int initCount_01 = 0;
		        long INIT_00 = 127;
		        long INIT_01 = 31;
		        StringTypedValue valueZero = new StringTypedValue("0");
		        StringTypedValue valueOne = new StringTypedValue("1");
		        StringTypedValue valueE4 = new StringTypedValue("E4");
		        StringTypedValue valueCA = new StringTypedValue("CA");
		        StringTypedValue value4 = new StringTypedValue("4");
		        StringTypedValue value8 = new StringTypedValue("8");
		        StringTypedValue value0002 = new StringTypedValue("0002");
		        StringTypedValue value0008 = new StringTypedValue("0008");
		        StringTypedValue value0020 = new StringTypedValue("0020");
		        StringTypedValue value0080 = new StringTypedValue("0080");
		        StringTypedValue value0200 = new StringTypedValue("0200");
		        StringTypedValue value0800 = new StringTypedValue("0800");
		        StringTypedValue value2000 = new StringTypedValue("2000");
		        StringTypedValue value8000 = new StringTypedValue("8000");
		        //Set INIT Property for FDE1
		        isInit = false;
				PropertyList FDE1_propertylist = FDE1.getPropertyList();
				if (FDE1_propertylist != null) {
                    for (Property FDE1_property : FDE1_propertylist.values()) {
                        if (FDE1_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE1_property.setValue(valueOne);
                            else
                                FDE1_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE1.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE1.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE2
                isInit = false;
				PropertyList FDE2_propertylist = FDE2.getPropertyList();
				if (FDE2_propertylist != null) {
                    for (Property FDE2_property : FDE2_propertylist.values()) {
                        if (FDE2_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE2_property.setValue(valueOne);
                            else
                                FDE2_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE2.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE2.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE3
                isInit = false;
				PropertyList FDE3_propertylist = FDE3.getPropertyList();
				if (FDE3_propertylist != null) {
                    for (Property FDE3_property : FDE3_propertylist.values()) {
                        if (FDE3_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE3_property.setValue(valueOne);
                            else
                                FDE3_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE3.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE3.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE4
                isInit = false;
				PropertyList FDE4_propertylist = FDE4.getPropertyList();
				if (FDE4_propertylist != null) {
                    for (Property FDE4_property : FDE4_propertylist.values()) {
                        if (FDE4_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE4_property.setValue(valueOne);
                            else
                                FDE4_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE4.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE4.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE5
                isInit = false;
				PropertyList FDE5_propertylist = FDE5.getPropertyList();
				if (FDE5_propertylist != null) {
                    for (Property FDE5_property : FDE5_propertylist.values()) {
                        if (FDE5_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE5_property.setValue(valueOne);
                            else
                                FDE5_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE5.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE5.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE6
                isInit = false;
				PropertyList FDE6_propertylist = FDE6.getPropertyList();
				if (FDE6_propertylist != null) {
                    for (Property FDE6_property : FDE6_propertylist.values()) {
                        if (FDE6_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE6_property.setValue(valueOne);
                            else
                                FDE6_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE6.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE6.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE7
                isInit = false;
				PropertyList FDE7_propertylist = FDE7.getPropertyList();
				if (FDE7_propertylist != null) {
                    for (Property FDE7_property : FDE7_propertylist.values()) {
                        if (FDE7_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE7_property.setValue(valueOne);
                            else
                                FDE7_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE7.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE7.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE8
                isInit = false;
				PropertyList FDE8_propertylist = FDE8.getPropertyList();
				if (FDE8_propertylist != null) {
                    for (Property FDE8_property : FDE8_propertylist.values()) {
                        if (FDE8_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE8_property.setValue(valueOne);
                            else
                                FDE8_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE8.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE8.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE9
                isInit = false;
				PropertyList FDE9_propertylist = FDE9.getPropertyList();
				if (FDE9_propertylist != null) {
                    for (Property FDE9_property : FDE9_propertylist.values()) {
                        if (FDE9_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE9_property.setValue(valueOne);
                            else
                                FDE9_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE9.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE9.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE10
                isInit = false;
				PropertyList FDE10_propertylist = FDE10.getPropertyList();
				if (FDE10_propertylist != null) {
                    for (Property FDE10_property : FDE10_propertylist.values()) {
                        if (FDE10_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE10_property.setValue(valueOne);
                            else
                                FDE10_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE10.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE10.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE11
                isInit = false;
				PropertyList FDE11_propertylist = FDE11.getPropertyList();
				if (FDE11_propertylist != null) {
                    for (Property FDE11_property : FDE11_propertylist.values()) {
                        if (FDE11_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE11_property.setValue(valueOne);
                            else
                                FDE11_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE11.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE11.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE12
                isInit = false;
				PropertyList FDE12_propertylist = FDE12.getPropertyList();
				if (FDE12_propertylist != null) {
                    for (Property FDE12_property : FDE12_propertylist.values()) {
                        if (FDE12_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE12_property.setValue(valueOne);
                            else
                                FDE12_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE12.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE12.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE13
                isInit = false;
				PropertyList FDE13_propertylist = FDE13.getPropertyList();
				if (FDE13_propertylist != null) {
                    for (Property FDE13_property : FDE13_propertylist.values()) {
                        if (FDE13_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE13_property.setValue(valueOne);
                            else
                                FDE13_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE13.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE13.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE14
                isInit = false;
				PropertyList FDE14_propertylist = FDE14.getPropertyList();
				if (FDE14_propertylist != null) {
                    for (Property FDE14_property : FDE14_propertylist.values()) {
                        if (FDE14_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE14_property.setValue(valueOne);
                            else
                                FDE14_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE14.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE14.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE15
                isInit = false;
				PropertyList FDE15_propertylist = FDE15.getPropertyList();
				if (FDE15_propertylist != null) {
                    for (Property FDE15_property : FDE15_propertylist.values()) {
                        if (FDE15_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE15_property.setValue(valueOne);
                            else
                                FDE15_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE15.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE15.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE16
                isInit = false;
				PropertyList FDE16_propertylist = FDE16.getPropertyList();
				if (FDE16_propertylist != null) {
                    for (Property FDE16_property : FDE16_propertylist.values()) {
                        if (FDE16_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE16_property.setValue(valueOne);
                            else
                                FDE16_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE16.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE16.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE17
		        isInit = false;
				PropertyList FDE17_propertylist = FDE17.getPropertyList();
				if (FDE17_propertylist != null) {
                    for (Property FDE17_property : FDE17_propertylist.values()) {
                        if (FDE17_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE17_property.setValue(valueOne);
                            else
                                FDE17_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE17.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE17.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE18
		        isInit = false;
				PropertyList FDE18_propertylist = FDE18.getPropertyList();
				if (FDE18_propertylist != null) {
                    for (Property FDE18_property : FDE18_propertylist.values()) {
                        if (FDE18_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE18_property.setValue(valueOne);
                            else
                                FDE18_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE18.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE18.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE19
		        isInit = false;
				PropertyList FDE19_propertylist = FDE19.getPropertyList();
				if (FDE19_propertylist != null) {
                    for (Property FDE19_property : FDE19_propertylist.values()) {
                        if (FDE19_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE19_property.setValue(valueOne);
                            else
                                FDE19_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE19.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE19.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE20
		        isInit = false;
				PropertyList FDE20_propertylist = FDE20.getPropertyList();
				if (FDE20_propertylist != null) {
                    for (Property FDE20_property : FDE20_propertylist.values()) {
                        if (FDE20_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE20_property.setValue(valueOne);
                            else
                                FDE20_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE20.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE20.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE21
		        isInit = false;
				PropertyList FDE21_propertylist = FDE21.getPropertyList();
				if (FDE21_propertylist != null) {
                    for (Property FDE21_property : FDE21_propertylist.values()) {
                        if (FDE21_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE21_property.setValue(valueOne);
                            else
                                FDE21_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE21.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE21.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE22
		        isInit = false;
				PropertyList FDE22_propertylist = FDE22.getPropertyList();
				if (FDE22_propertylist != null) {
                    for (Property FDE22_property : FDE22_propertylist.values()) {
                        if (FDE22_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE22_property.setValue(valueOne);
                            else
                                FDE22_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE22.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE22.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE23
		        isInit = false;
				PropertyList FDE23_propertylist = FDE23.getPropertyList();
				if (FDE23_propertylist != null) {
                    for (Property FDE23_property : FDE23_propertylist.values()) {
                        if (FDE23_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE23_property.setValue(valueOne);
                            else
                                FDE23_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE23.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE23.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE24
		        isInit = false;
				PropertyList FDE24_propertylist = FDE24.getPropertyList();
				if (FDE24_propertylist != null) {
                    for (Property FDE24_property : FDE24_propertylist.values()) {
                        if (FDE24_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE24_property.setValue(valueOne);
                            else
                                FDE24_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE24.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE24.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE25
		        isInit = false;
				PropertyList FDE25_propertylist = FDE25.getPropertyList();
				if (FDE25_propertylist != null) {
                    for (Property FDE25_property : FDE25_propertylist.values()) {
                        if (FDE25_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE25_property.setValue(valueOne);
                            else
                                FDE25_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE25.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE25.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE26
		        isInit = false;
				PropertyList FDE26_propertylist = FDE26.getPropertyList();
				if (FDE26_propertylist != null) {
                    for (Property FDE26_property : FDE26_propertylist.values()) {
                        if (FDE26_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE26_property.setValue(valueOne);
                            else
                                FDE26_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE26.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE26.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE27
		        isInit = false;
				PropertyList FDE27_propertylist = FDE27.getPropertyList();
				if (FDE27_propertylist != null) {
                    for (Property FDE27_property : FDE27_propertylist.values()) {
                        if (FDE27_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE27_property.setValue(valueOne);
                            else
                                FDE27_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE27.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE27.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE28
		        isInit = false;
				PropertyList FDE28_propertylist = FDE28.getPropertyList();
				if (FDE28_propertylist != null) {
                    for (Property FDE28_property : FDE28_propertylist.values()) {
                        if (FDE28_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE28_property.setValue(valueOne);
                            else
                                FDE28_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE28.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE28.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE29
		        isInit = false;
				PropertyList FDE29_propertylist = FDE29.getPropertyList();
				if (FDE29_propertylist != null) {
                    for (Property FDE29_property : FDE29_propertylist.values()) {
                        if (FDE29_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE29_property.setValue(valueOne);
                            else
                                FDE29_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE29.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE29.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE30
		        isInit = false;
				PropertyList FDE30_propertylist = FDE30.getPropertyList();
				if (FDE30_propertylist != null) {
                    for (Property FDE30_property : FDE30_propertylist.values()) {
                        if (FDE30_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE30_property.setValue(valueOne);
                            else
                                FDE30_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE30.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE30.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE31
		        isInit = false;
				PropertyList FDE31_propertylist = FDE31.getPropertyList();
				if (FDE31_propertylist != null) {
                    for (Property FDE31_property : FDE31_propertylist.values()) {
                        if (FDE31_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE31_property.setValue(valueOne);
                            else
                                FDE31_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE31.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE31.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE32
		        isInit = false;
				PropertyList FDE32_propertylist = FDE32.getPropertyList();
				if (FDE32_propertylist != null) {
                    for (Property FDE32_property : FDE32_propertylist.values()) {
                        if (FDE32_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE32_property.setValue(valueOne);
                            else
                                FDE32_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE32.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE32.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for LUT3_1
                isInit = false;
				PropertyList LUT3_1_propertylist = LUT3_1.getPropertyList();
				if (LUT3_1_propertylist != null) {
                    for (Property LUT3_1_property : LUT3_1_propertylist.values()) {
                        if (LUT3_1_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_1_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_1.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_2
                isInit = false;
				PropertyList LUT3_2_propertylist = LUT3_2.getPropertyList();
				if (LUT3_2_propertylist != null) {
                    for (Property LUT3_2_property : LUT3_2_propertylist.values()) {
                        if (LUT3_2_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_2_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_2.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_3
                isInit = false;
				PropertyList LUT3_3_propertylist = LUT3_3.getPropertyList();
				if (LUT3_3_propertylist != null) {
                    for (Property LUT3_3_property : LUT3_3_propertylist.values()) {
                        if (LUT3_3_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_3_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_3.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_4
                isInit = false;
				PropertyList LUT3_4_propertylist = LUT3_4.getPropertyList();
				if (LUT3_4_propertylist != null) {
                    for (Property LUT3_4_property : LUT3_4_propertylist.values()) {
                        if (LUT3_4_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_4_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_4.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_5
                isInit = false;
				PropertyList LUT3_5_propertylist = LUT3_5.getPropertyList();
				if (LUT3_5_propertylist != null) {
                    for (Property LUT3_5_property : LUT3_5_propertylist.values()) {
                        if (LUT3_5_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_5_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_5.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_6
                isInit = false;
				PropertyList LUT3_6_propertylist = LUT3_6.getPropertyList();
				if (LUT3_6_propertylist != null) {
                    for (Property LUT3_6_property : LUT3_6_propertylist.values()) {
                        if (LUT3_6_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_6_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_6.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_7
                isInit = false;
				PropertyList LUT3_7_propertylist = LUT3_7.getPropertyList();
				if (LUT3_7_propertylist != null) {
                    for (Property LUT3_7_property : LUT3_7_propertylist.values()) {
                        if (LUT3_7_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_7_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_7.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_8
                isInit = false;
				PropertyList LUT3_8_propertylist = LUT3_8.getPropertyList();
				if (LUT3_8_propertylist != null) {
                    for (Property LUT3_8_property : LUT3_8_propertylist.values()) {
                        if (LUT3_8_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_8_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_8.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_9
                isInit = false;
				PropertyList LUT3_9_propertylist = LUT3_9.getPropertyList();
				if (LUT3_9_propertylist != null) {
                    for (Property LUT3_9_property : LUT3_9_propertylist.values()) {
                        if (LUT3_9_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_9_property.setValue(valueCA);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_9.addProperty(new Property("INIT", (EdifTypedValue) valueCA));
                }
              //Set INIT Property for LUT3_10
                isInit = false;
				PropertyList LUT3_10_propertylist = LUT3_10.getPropertyList();
				if (LUT3_10_propertylist != null) {
                    for (Property LUT3_10_property : LUT3_10_propertylist.values()) {
                        if (LUT3_10_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_10_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_10.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_11
                isInit = false;
				PropertyList LUT3_11_propertylist = LUT3_11.getPropertyList();
				if (LUT3_11_propertylist != null) {
                    for (Property LUT3_11_property : LUT3_11_propertylist.values()) {
                        if (LUT3_11_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_11_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_11.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_12
                isInit = false;
				PropertyList LUT3_12_propertylist = LUT3_12.getPropertyList();
				if (LUT3_12_propertylist != null) {
                    for (Property LUT3_12_property : LUT3_12_propertylist.values()) {
                        if (LUT3_12_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_12_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_12.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_13
                isInit = false;
				PropertyList LUT3_13_propertylist = LUT3_13.getPropertyList();
				if (LUT3_13_propertylist != null) {
                    for (Property LUT3_13_property : LUT3_13_propertylist.values()) {
                        if (LUT3_13_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_13_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_13.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_14
                isInit = false;
				PropertyList LUT3_14_propertylist = LUT3_14.getPropertyList();
				if (LUT3_14_propertylist != null) {
                    for (Property LUT3_14_property : LUT3_14_propertylist.values()) {
                        if (LUT3_14_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_14_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_14.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_15
                isInit = false;
				PropertyList LUT3_15_propertylist = LUT3_15.getPropertyList();
				if (LUT3_15_propertylist != null) {
                    for (Property LUT3_15_property : LUT3_15_propertylist.values()) {
                        if (LUT3_15_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_15_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_15.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_16
                isInit = false;
				PropertyList LUT3_16_propertylist = LUT3_16.getPropertyList();
				if (LUT3_16_propertylist != null) {
                    for (Property LUT3_16_property : LUT3_16_propertylist.values()) {
                        if (LUT3_16_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_16_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_16.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_17
                isInit = false;
				PropertyList LUT3_17_propertylist = LUT3_17.getPropertyList();
				if (LUT3_17_propertylist != null) {
                    for (Property LUT3_17_property : LUT3_17_propertylist.values()) {
                        if (LUT3_17_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_17_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_17.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_18
                isInit = false;
				PropertyList LUT3_18_propertylist = LUT3_18.getPropertyList();
				if (LUT3_18_propertylist != null) {
                    for (Property LUT3_18_property : LUT3_18_propertylist.values()) {
                        if (LUT3_18_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_18_property.setValue(valueCA);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_18.addProperty(new Property("INIT", (EdifTypedValue) valueCA));
                }
              //Set INIT Property for LUT2_1
                isInit = false;
				PropertyList LUT2_1_propertylist = LUT2_1.getPropertyList();
				if (LUT2_1_propertylist != null) {
                    for (Property LUT2_1_property : LUT2_1_propertylist.values()) {
                        if (LUT2_1_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT2_1_property.setValue(value4);
                        }
                    }
                }
                if (!isInit) {
                    LUT2_1.addProperty(new Property("INIT", (EdifTypedValue) value4));
                }
              //Set INIT Property for LUT2_2
                isInit = false;
				PropertyList LUT2_2_propertylist = LUT2_2.getPropertyList();
				if (LUT2_2_propertylist != null) {
                    for (Property LUT2_2_property : LUT2_2_propertylist.values()) {
                        if (LUT2_2_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT2_2_property.setValue(value8);
                        }
                    }
                }
                if (!isInit) {
                    LUT2_2.addProperty(new Property("INIT", (EdifTypedValue) value8));
                }
              //Set INIT Property for LUT4_1
                isInit = false;
				PropertyList LUT4_1_propertylist = LUT4_1.getPropertyList();
				if (LUT4_1_propertylist != null) {
                    for (Property LUT4_1_property : LUT4_1_propertylist.values()) {
                        if (LUT4_1_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_1_property.setValue(value0002);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_1.addProperty(new Property("INIT", (EdifTypedValue) value0002));
                }
              //Set INIT Property for LUT4_2
                isInit = false;
				PropertyList LUT4_2_propertylist = LUT4_2.getPropertyList();
				if (LUT4_2_propertylist != null) {
                    for (Property LUT4_2_property : LUT4_2_propertylist.values()) {
                        if (LUT4_2_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_2_property.setValue(value0008);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_2.addProperty(new Property("INIT", (EdifTypedValue) value0008));
                }
              //Set INIT Property for LUT4_3
                isInit = false;
				PropertyList LUT4_3_propertylist = LUT4_3.getPropertyList();
				if (LUT4_3_propertylist != null) {
                    for (Property LUT4_3_property : LUT4_3_propertylist.values()) {
                        if (LUT4_3_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_3_property.setValue(value0020);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_3.addProperty(new Property("INIT", (EdifTypedValue) value0020));
                }
              //Set INIT Property for LUT4_4
                isInit = false;
				PropertyList LUT4_4_propertylist = LUT4_4.getPropertyList();
				if (LUT4_4_propertylist != null) {
                    for (Property LUT4_4_property : LUT4_4_propertylist.values()) {
                        if (LUT4_4_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_4_property.setValue(value0080);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_4.addProperty(new Property("INIT", (EdifTypedValue) value0080));
                }
              //Set INIT Property for LUT4_5
                isInit = false;
				PropertyList LUT4_5_propertylist = LUT4_5.getPropertyList();
				if (LUT4_5_propertylist != null) {
                    for (Property LUT4_5_property : LUT4_5_propertylist.values()) {
                        if (LUT4_5_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_5_property.setValue(value0200);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_5.addProperty(new Property("INIT", (EdifTypedValue) value0200));
                }
              //Set INIT Property for LUT4_6
                isInit = false;
				PropertyList LUT4_6_propertylist = LUT4_6.getPropertyList();
				if (LUT4_6_propertylist != null) {
                    for (Property LUT4_6_property : LUT4_6_propertylist.values()) {
                        if (LUT4_6_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_6_property.setValue(value0800);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_6.addProperty(new Property("INIT", (EdifTypedValue) value0800));
                }
              //Set INIT Property for LUT4_7
                isInit = false;
				PropertyList LUT4_7_propertylist = LUT4_7.getPropertyList();
				if (LUT4_7_propertylist != null) {
                    for (Property LUT4_7_property : LUT4_7_propertylist.values()) {
                        if (LUT4_7_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_7_property.setValue(value2000);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_7.addProperty(new Property("INIT", (EdifTypedValue) value2000));
                }
              //Set INIT Property for LUT4_8
                isInit = false;
				PropertyList LUT4_8_propertylist = LUT4_8.getPropertyList();
				if (LUT4_8_propertylist != null) {
                    for (Property LUT4_8_property : LUT4_8_propertylist.values()) {
                        if (LUT4_8_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_8_property.setValue(value8000);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_8.addProperty(new Property("INIT", (EdifTypedValue) value8000));
                }
              //Set INIT Property for LUT4_9
                isInit = false;
				PropertyList LUT4_9_propertylist = LUT4_9.getPropertyList();
				if (LUT4_9_propertylist != null) {
                    for (Property LUT4_9_property : LUT4_9_propertylist.values()) {
                        if (LUT4_9_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_9_property.setValue(value0002);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_9.addProperty(new Property("INIT", (EdifTypedValue) value0002));
                }
              //Set INIT Property for LUT4_10
                isInit = false;
				PropertyList LUT4_10_propertylist = LUT4_10.getPropertyList();
				if (LUT4_10_propertylist != null) {
                    for (Property LUT4_10_property : LUT4_10_propertylist.values()) {
                        if (LUT4_10_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_10_property.setValue(value0008);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_10.addProperty(new Property("INIT", (EdifTypedValue) value0008));
                }
              //Set INIT Property for LUT4_11
                isInit = false;
				PropertyList LUT4_11_propertylist = LUT4_11.getPropertyList();
				if (LUT4_11_propertylist != null) {
                    for (Property LUT4_11_property : LUT4_11_propertylist.values()) {
                        if (LUT4_11_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_11_property.setValue(value0020);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_11.addProperty(new Property("INIT", (EdifTypedValue) value0020));
                }
              //Set INIT Property for LUT4_12
                isInit = false;
				PropertyList LUT4_12_propertylist = LUT4_12.getPropertyList();
				if (LUT4_12_propertylist != null) {
                    for (Property LUT4_12_property : LUT4_12_propertylist.values()) {
                        if (LUT4_12_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_12_property.setValue(value0080);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_12.addProperty(new Property("INIT", (EdifTypedValue) value0080));
                }
              //Set INIT Property for LUT4_13
                isInit = false;
				PropertyList LUT4_13_propertylist = LUT4_13.getPropertyList();
				if (LUT4_13_propertylist != null) {
                    for (Property LUT4_13_property : LUT4_13_propertylist.values()) {
                        if (LUT4_13_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_13_property.setValue(value0200);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_13.addProperty(new Property("INIT", (EdifTypedValue) value0200));
                }
              //Set INIT Property for LUT4_14
                isInit = false;
				PropertyList LUT4_14_propertylist = LUT4_14.getPropertyList();
				if (LUT4_14_propertylist != null) {
                    for (Property LUT4_14_property : LUT4_14_propertylist.values()) {
                        if (LUT4_14_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_14_property.setValue(value0800);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_14.addProperty(new Property("INIT", (EdifTypedValue) value0800));
                }
              //Set INIT Property for LUT4_15
                isInit = false;
				PropertyList LUT4_15_propertylist = LUT4_15.getPropertyList();
				if (LUT4_15_propertylist != null) {
                    for (Property LUT4_15_property : LUT4_15_propertylist.values()) {
                        if (LUT4_15_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_15_property.setValue(value2000);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_15.addProperty(new Property("INIT", (EdifTypedValue) value2000));
                }
              //Set INIT Property for LUT4_16
                isInit = false;
				PropertyList LUT4_16_propertylist = LUT4_16.getPropertyList();
				if (LUT4_16_propertylist != null) {
                    for (Property LUT4_16_property : LUT4_16_propertylist.values()) {
                        if (LUT4_16_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_16_property.setValue(value8000);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_16.addProperty(new Property("INIT", (EdifTypedValue) value8000));
                }
			} catch (EdifNameConflictException e){
				System.out.println("EdifNameConflictException caught"+e);
				//Should not happen
			}
			
			/******Write to EDIF File******/
			try{
				FileOutputStream outputStream = new FileOutputStream(outputFileName);
				EdifPrintWriter epw = new EdifPrintWriter(outputStream);
		        topLevel.toEdif(epw, true);
		        epw.close();
			} catch (FileNotFoundException e){
				System.out.println("FileNotFoundException caught"+e);
			}
			
		} catch (InvalidEdifNameException e){
			System.out.println("InvalidEdifNameException caught"+e);
			//Should not happen
		}
	}
	
	public static void Replace(EdifLibraryManager libManager, EdifCell RAM16X2S_replacement, long INIT_00, long INIT_01,
			EdifNet we, EdifNet d0, EdifNet d1, EdifNet wclk, EdifNet a0, EdifNet a1, EdifNet a2, EdifNet a3,
			EdifNet o0, EdifNet o1) {
		try{
			try{
				/******Cells******/
				EdifCell FDE = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FDE");
				EdifCell LUT2 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "LUT2");
				EdifCell LUT3 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "LUT3");
				EdifCell LUT4 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "LUT4");
				EdifCell MUXF5 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "MUXF5");
				EdifCell MUXF6 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "MUXF6");		

				/******Nets******/
				EdifNet wire1 = new EdifNet("wire1", RAM16X2S_replacement);
				EdifNet wire2 = new EdifNet("wire2", RAM16X2S_replacement);
				EdifNet wire3 = new EdifNet("wire3", RAM16X2S_replacement);
				EdifNet wire4 = new EdifNet("wire4", RAM16X2S_replacement);
				EdifNet wire5 = new EdifNet("wire5", RAM16X2S_replacement);
				EdifNet wire6 = new EdifNet("wire6", RAM16X2S_replacement);
				EdifNet wire7 = new EdifNet("wire7", RAM16X2S_replacement);
				EdifNet wire8 = new EdifNet("wire8", RAM16X2S_replacement);
				EdifNet wire9 = new EdifNet("wire9", RAM16X2S_replacement);
				EdifNet wire10 = new EdifNet("wire10", RAM16X2S_replacement);
				EdifNet wire11 = new EdifNet("wire11", RAM16X2S_replacement);
				EdifNet wire12 = new EdifNet("wire12", RAM16X2S_replacement);
				EdifNet wire13 = new EdifNet("wire13", RAM16X2S_replacement);
				EdifNet wire14 = new EdifNet("wire14", RAM16X2S_replacement);
				EdifNet wire15 = new EdifNet("wire15", RAM16X2S_replacement);
				EdifNet wire16 = new EdifNet("wire16", RAM16X2S_replacement);
				EdifNet wire17 = new EdifNet("wire17", RAM16X2S_replacement);
				EdifNet wire18 = new EdifNet("wire18", RAM16X2S_replacement);
				EdifNet wire19 = new EdifNet("wire19", RAM16X2S_replacement);
				EdifNet wire20 = new EdifNet("wire20", RAM16X2S_replacement);
				EdifNet wire21 = new EdifNet("wire21", RAM16X2S_replacement);
				EdifNet wire22 = new EdifNet("wire22", RAM16X2S_replacement);
				EdifNet wire23 = new EdifNet("wire23", RAM16X2S_replacement);
				EdifNet wire24 = new EdifNet("wire24", RAM16X2S_replacement);
				EdifNet wire25 = new EdifNet("wire25", RAM16X2S_replacement);
				EdifNet wire26 = new EdifNet("wire26", RAM16X2S_replacement);
				EdifNet wire27 = new EdifNet("wire27", RAM16X2S_replacement);
				EdifNet wire28 = new EdifNet("wire28", RAM16X2S_replacement);
				EdifNet wire29 = new EdifNet("wire29", RAM16X2S_replacement);
				EdifNet wire30 = new EdifNet("wire30", RAM16X2S_replacement);
				EdifNet wire31 = new EdifNet("wire31", RAM16X2S_replacement);
				EdifNet wire32 = new EdifNet("wire32", RAM16X2S_replacement);
				EdifNet net1 = new EdifNet("net1", RAM16X2S_replacement);
				EdifNet net2 = new EdifNet("net2", RAM16X2S_replacement);
				EdifNet net3 = new EdifNet("net3", RAM16X2S_replacement);
				EdifNet net4 = new EdifNet("net4", RAM16X2S_replacement);
				EdifNet net5 = new EdifNet("net5", RAM16X2S_replacement);
				EdifNet net6 = new EdifNet("net6", RAM16X2S_replacement);
				EdifNet net7 = new EdifNet("net7", RAM16X2S_replacement);
				EdifNet net8 = new EdifNet("net8", RAM16X2S_replacement);
				EdifNet net9 = new EdifNet("net9", RAM16X2S_replacement);
				EdifNet net10 = new EdifNet("net10", RAM16X2S_replacement);
				EdifNet net11 = new EdifNet("net11", RAM16X2S_replacement);
				EdifNet net12 = new EdifNet("net12", RAM16X2S_replacement);
				EdifNet net13 = new EdifNet("net13", RAM16X2S_replacement);
				EdifNet net14 = new EdifNet("net14", RAM16X2S_replacement);
				EdifNet net15 = new EdifNet("net15", RAM16X2S_replacement);
				EdifNet net16 = new EdifNet("net16", RAM16X2S_replacement);
				EdifNet net17 = new EdifNet("net17", RAM16X2S_replacement);
				EdifNet net18 = new EdifNet("net18", RAM16X2S_replacement);
				EdifNet net19 = new EdifNet("net19", RAM16X2S_replacement);
				EdifNet net20 = new EdifNet("net20", RAM16X2S_replacement);
				EdifNet net21 = new EdifNet("net21", RAM16X2S_replacement);
				EdifNet net22 = new EdifNet("net22", RAM16X2S_replacement);
				EdifNet net23 = new EdifNet("net23", RAM16X2S_replacement);
				EdifNet net24 = new EdifNet("net24", RAM16X2S_replacement);
				EdifNet net25 = new EdifNet("net25", RAM16X2S_replacement);
				EdifNet net26 = new EdifNet("net26", RAM16X2S_replacement);
				EdifNet net27 = new EdifNet("net27", RAM16X2S_replacement);
				EdifNet net28 = new EdifNet("net28", RAM16X2S_replacement);
				EdifNet net29 = new EdifNet("net29", RAM16X2S_replacement);
				EdifNet net30 = new EdifNet("net30", RAM16X2S_replacement);
				EdifNet e1 = new EdifNet("e1", RAM16X2S_replacement);
				EdifNet e2 = new EdifNet("e2", RAM16X2S_replacement);
				EdifNet e3 = new EdifNet("e3", RAM16X2S_replacement);
				EdifNet e4 = new EdifNet("e4", RAM16X2S_replacement);
				EdifNet e5 = new EdifNet("e5", RAM16X2S_replacement);
				EdifNet e6 = new EdifNet("e6", RAM16X2S_replacement);
				EdifNet e7 = new EdifNet("e7", RAM16X2S_replacement);
				EdifNet e8 = new EdifNet("e8", RAM16X2S_replacement);
				EdifNet e9 = new EdifNet("e9", RAM16X2S_replacement);
				EdifNet e10 = new EdifNet("e10", RAM16X2S_replacement);
				EdifNet e11 = new EdifNet("e11", RAM16X2S_replacement);
				EdifNet e12 = new EdifNet("e12", RAM16X2S_replacement);
				EdifNet e13 = new EdifNet("e13", RAM16X2S_replacement);
				EdifNet e14 = new EdifNet("e14", RAM16X2S_replacement);
				EdifNet e15 = new EdifNet("e15", RAM16X2S_replacement);
				EdifNet e16 = new EdifNet("e16", RAM16X2S_replacement);
				RAM16X2S_replacement.addNet(wire1);
				RAM16X2S_replacement.addNet(wire2);
				RAM16X2S_replacement.addNet(wire3);
				RAM16X2S_replacement.addNet(wire4);
				RAM16X2S_replacement.addNet(wire5);
				RAM16X2S_replacement.addNet(wire6);
				RAM16X2S_replacement.addNet(wire7);
				RAM16X2S_replacement.addNet(wire8);
				RAM16X2S_replacement.addNet(wire9);
				RAM16X2S_replacement.addNet(wire10);
				RAM16X2S_replacement.addNet(wire11);
				RAM16X2S_replacement.addNet(wire12);
				RAM16X2S_replacement.addNet(wire13);
				RAM16X2S_replacement.addNet(wire14);
				RAM16X2S_replacement.addNet(wire15);
				RAM16X2S_replacement.addNet(wire16);
				RAM16X2S_replacement.addNet(wire17);
				RAM16X2S_replacement.addNet(wire18);
				RAM16X2S_replacement.addNet(wire19);
				RAM16X2S_replacement.addNet(wire20);
				RAM16X2S_replacement.addNet(wire21);
				RAM16X2S_replacement.addNet(wire22);
				RAM16X2S_replacement.addNet(wire23);
				RAM16X2S_replacement.addNet(wire24);
				RAM16X2S_replacement.addNet(wire25);
				RAM16X2S_replacement.addNet(wire26);
				RAM16X2S_replacement.addNet(wire27);
				RAM16X2S_replacement.addNet(wire28);
				RAM16X2S_replacement.addNet(wire29);
				RAM16X2S_replacement.addNet(wire30);
				RAM16X2S_replacement.addNet(wire31);
				RAM16X2S_replacement.addNet(wire32);
				RAM16X2S_replacement.addNet(net1);
				RAM16X2S_replacement.addNet(net2);
				RAM16X2S_replacement.addNet(net3);
				RAM16X2S_replacement.addNet(net4);
				RAM16X2S_replacement.addNet(net5);
				RAM16X2S_replacement.addNet(net6);
				RAM16X2S_replacement.addNet(net7);
				RAM16X2S_replacement.addNet(net8);
				RAM16X2S_replacement.addNet(net9);
				RAM16X2S_replacement.addNet(net10);
				RAM16X2S_replacement.addNet(net11);
				RAM16X2S_replacement.addNet(net12);
				RAM16X2S_replacement.addNet(net13);
				RAM16X2S_replacement.addNet(net14);
				RAM16X2S_replacement.addNet(net15);
				RAM16X2S_replacement.addNet(net16);
				RAM16X2S_replacement.addNet(net17);
				RAM16X2S_replacement.addNet(net18);
				RAM16X2S_replacement.addNet(net19);
				RAM16X2S_replacement.addNet(net20);
				RAM16X2S_replacement.addNet(net21);
				RAM16X2S_replacement.addNet(net22);
				RAM16X2S_replacement.addNet(net23);
				RAM16X2S_replacement.addNet(net24);
				RAM16X2S_replacement.addNet(net25);
				RAM16X2S_replacement.addNet(net26);
				RAM16X2S_replacement.addNet(net27);
				RAM16X2S_replacement.addNet(net28);
				RAM16X2S_replacement.addNet(net29);
				RAM16X2S_replacement.addNet(net30);
				RAM16X2S_replacement.addNet(e1);
				RAM16X2S_replacement.addNet(e2);
				RAM16X2S_replacement.addNet(e3);
				RAM16X2S_replacement.addNet(e4);
				RAM16X2S_replacement.addNet(e5);
				RAM16X2S_replacement.addNet(e6);
				RAM16X2S_replacement.addNet(e7);
				RAM16X2S_replacement.addNet(e8);
				RAM16X2S_replacement.addNet(e9);
				RAM16X2S_replacement.addNet(e10);
				RAM16X2S_replacement.addNet(e11);
				RAM16X2S_replacement.addNet(e12);
				RAM16X2S_replacement.addNet(e13);
				RAM16X2S_replacement.addNet(e14);
				RAM16X2S_replacement.addNet(e15);
				RAM16X2S_replacement.addNet(e16);
				
				/******Instances******/
				EdifCellInstance FDE1 = new EdifCellInstance("FDE1", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE2 = new EdifCellInstance("FDE2", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE3 = new EdifCellInstance("FDE3", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE4 = new EdifCellInstance("FDE4", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE5 = new EdifCellInstance("FDE5", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE6 = new EdifCellInstance("FDE6", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE7 = new EdifCellInstance("FDE7", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE8 = new EdifCellInstance("FDE8", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE9 = new EdifCellInstance("FDE9", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE10 = new EdifCellInstance("FDE10", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE11 = new EdifCellInstance("FDE11", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE12 = new EdifCellInstance("FDE12", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE13 = new EdifCellInstance("FDE13", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE14 = new EdifCellInstance("FDE14", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE15 = new EdifCellInstance("FDE15", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE16 = new EdifCellInstance("FDE16", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE17 = new EdifCellInstance("FDE17", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE18 = new EdifCellInstance("FDE18", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE19 = new EdifCellInstance("FDE19", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE20 = new EdifCellInstance("FDE20", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE21 = new EdifCellInstance("FDE21", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE22 = new EdifCellInstance("FDE22", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE23 = new EdifCellInstance("FDE23", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE24 = new EdifCellInstance("FDE24", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE25 = new EdifCellInstance("FDE25", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE26 = new EdifCellInstance("FDE26", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE27 = new EdifCellInstance("FDE27", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE28 = new EdifCellInstance("FDE28", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE29 = new EdifCellInstance("FDE29", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE30 = new EdifCellInstance("FDE30", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE31 = new EdifCellInstance("FDE31", RAM16X2S_replacement, FDE);
				EdifCellInstance FDE32 = new EdifCellInstance("FDE32", RAM16X2S_replacement, FDE);
				EdifCellInstance LUT2_1 = new EdifCellInstance("LUT2_1", RAM16X2S_replacement, LUT2);
				EdifCellInstance LUT2_2 = new EdifCellInstance("LUT2_2", RAM16X2S_replacement, LUT2);
				EdifCellInstance LUT3_1 = new EdifCellInstance("LUT3_1", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_2 = new EdifCellInstance("LUT3_2", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_3 = new EdifCellInstance("LUT3_3", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_4 = new EdifCellInstance("LUT3_4", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_5 = new EdifCellInstance("LUT3_5", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_6 = new EdifCellInstance("LUT3_6", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_7 = new EdifCellInstance("LUT3_7", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_8 = new EdifCellInstance("LUT3_8", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_9 = new EdifCellInstance("LUT3_9", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_10 = new EdifCellInstance("LUT3_10", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_11 = new EdifCellInstance("LUT3_11", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_12 = new EdifCellInstance("LUT3_12", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_13 = new EdifCellInstance("LUT3_13", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_14 = new EdifCellInstance("LUT3_14", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_15 = new EdifCellInstance("LUT3_15", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_16 = new EdifCellInstance("LUT3_16", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_17 = new EdifCellInstance("LUT3_17", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT3_18 = new EdifCellInstance("LUT3_18", RAM16X2S_replacement, LUT3);
				EdifCellInstance LUT4_1 = new EdifCellInstance("LUT4_1", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_2 = new EdifCellInstance("LUT4_2", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_3 = new EdifCellInstance("LUT4_3", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_4 = new EdifCellInstance("LUT4_4", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_5 = new EdifCellInstance("LUT4_5", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_6 = new EdifCellInstance("LUT4_6", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_7 = new EdifCellInstance("LUT4_7", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_8 = new EdifCellInstance("LUT4_8", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_9 = new EdifCellInstance("LUT4_9", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_10 = new EdifCellInstance("LUT4_10", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_11 = new EdifCellInstance("LUT4_11", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_12 = new EdifCellInstance("LUT4_12", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_13 = new EdifCellInstance("LUT4_13", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_14 = new EdifCellInstance("LUT4_14", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_15 = new EdifCellInstance("LUT4_15", RAM16X2S_replacement, LUT4);
				EdifCellInstance LUT4_16 = new EdifCellInstance("LUT4_16", RAM16X2S_replacement, LUT4);
				EdifCellInstance MUXF5_1 = new EdifCellInstance("MUXF5_1", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF5_2 = new EdifCellInstance("MUXF5_2", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF5_3 = new EdifCellInstance("MUXF5_3", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF5_4 = new EdifCellInstance("MUXF5_4", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF5_5 = new EdifCellInstance("MUXF5_5", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF5_6 = new EdifCellInstance("MUXF5_6", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF5_7 = new EdifCellInstance("MUXF5_7", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF5_8 = new EdifCellInstance("MUXF5_8", RAM16X2S_replacement, MUXF5);
				EdifCellInstance MUXF6_1 = new EdifCellInstance("MUXF6_1", RAM16X2S_replacement, MUXF6);
				EdifCellInstance MUXF6_2 = new EdifCellInstance("MUXF6_2", RAM16X2S_replacement, MUXF6);
				EdifCellInstance MUXF6_3 = new EdifCellInstance("MUXF6_3", RAM16X2S_replacement, MUXF6);
				EdifCellInstance MUXF6_4 = new EdifCellInstance("MUXF6_4", RAM16X2S_replacement, MUXF6);
				RAM16X2S_replacement.addSubCell(FDE1);
				RAM16X2S_replacement.addSubCell(FDE2);
				RAM16X2S_replacement.addSubCell(FDE3);
				RAM16X2S_replacement.addSubCell(FDE4);
				RAM16X2S_replacement.addSubCell(FDE5);
				RAM16X2S_replacement.addSubCell(FDE6);
				RAM16X2S_replacement.addSubCell(FDE7);
				RAM16X2S_replacement.addSubCell(FDE8);
				RAM16X2S_replacement.addSubCell(FDE9);
				RAM16X2S_replacement.addSubCell(FDE10);
				RAM16X2S_replacement.addSubCell(FDE11);
				RAM16X2S_replacement.addSubCell(FDE12);
				RAM16X2S_replacement.addSubCell(FDE13);
				RAM16X2S_replacement.addSubCell(FDE14);
				RAM16X2S_replacement.addSubCell(FDE15);
				RAM16X2S_replacement.addSubCell(FDE16);
				RAM16X2S_replacement.addSubCell(FDE17);
				RAM16X2S_replacement.addSubCell(FDE18);
				RAM16X2S_replacement.addSubCell(FDE19);
				RAM16X2S_replacement.addSubCell(FDE20);
				RAM16X2S_replacement.addSubCell(FDE21);
				RAM16X2S_replacement.addSubCell(FDE22);
				RAM16X2S_replacement.addSubCell(FDE23);
				RAM16X2S_replacement.addSubCell(FDE24);
				RAM16X2S_replacement.addSubCell(FDE25);
				RAM16X2S_replacement.addSubCell(FDE26);
				RAM16X2S_replacement.addSubCell(FDE27);
				RAM16X2S_replacement.addSubCell(FDE28);
				RAM16X2S_replacement.addSubCell(FDE29);
				RAM16X2S_replacement.addSubCell(FDE30);
				RAM16X2S_replacement.addSubCell(FDE31);
				RAM16X2S_replacement.addSubCell(FDE32);
				RAM16X2S_replacement.addSubCell(LUT2_1);
				RAM16X2S_replacement.addSubCell(LUT2_2);
				RAM16X2S_replacement.addSubCell(LUT3_1);
				RAM16X2S_replacement.addSubCell(LUT3_2);
				RAM16X2S_replacement.addSubCell(LUT3_3);
				RAM16X2S_replacement.addSubCell(LUT3_4);
				RAM16X2S_replacement.addSubCell(LUT3_5);
				RAM16X2S_replacement.addSubCell(LUT3_6);
				RAM16X2S_replacement.addSubCell(LUT3_7);
				RAM16X2S_replacement.addSubCell(LUT3_8);
				RAM16X2S_replacement.addSubCell(LUT3_9);
				RAM16X2S_replacement.addSubCell(LUT3_10);
				RAM16X2S_replacement.addSubCell(LUT3_11);
				RAM16X2S_replacement.addSubCell(LUT3_12);
				RAM16X2S_replacement.addSubCell(LUT3_13);
				RAM16X2S_replacement.addSubCell(LUT3_14);
				RAM16X2S_replacement.addSubCell(LUT3_15);
				RAM16X2S_replacement.addSubCell(LUT3_16);
				RAM16X2S_replacement.addSubCell(LUT3_17);
				RAM16X2S_replacement.addSubCell(LUT3_18);
				RAM16X2S_replacement.addSubCell(LUT4_1);
				RAM16X2S_replacement.addSubCell(LUT4_2);
				RAM16X2S_replacement.addSubCell(LUT4_3);
				RAM16X2S_replacement.addSubCell(LUT4_4);
				RAM16X2S_replacement.addSubCell(LUT4_5);
				RAM16X2S_replacement.addSubCell(LUT4_6);
				RAM16X2S_replacement.addSubCell(LUT4_7);
				RAM16X2S_replacement.addSubCell(LUT4_8);
				RAM16X2S_replacement.addSubCell(LUT4_9);
				RAM16X2S_replacement.addSubCell(LUT4_10);
				RAM16X2S_replacement.addSubCell(LUT4_11);
				RAM16X2S_replacement.addSubCell(LUT4_12);
				RAM16X2S_replacement.addSubCell(LUT4_13);
				RAM16X2S_replacement.addSubCell(LUT4_14);
				RAM16X2S_replacement.addSubCell(LUT4_15);
				RAM16X2S_replacement.addSubCell(LUT4_16);
				RAM16X2S_replacement.addSubCell(MUXF5_1);
				RAM16X2S_replacement.addSubCell(MUXF5_2);
				RAM16X2S_replacement.addSubCell(MUXF5_3);
				RAM16X2S_replacement.addSubCell(MUXF5_4);
				RAM16X2S_replacement.addSubCell(MUXF5_5);
				RAM16X2S_replacement.addSubCell(MUXF5_6);
				RAM16X2S_replacement.addSubCell(MUXF5_7);
				RAM16X2S_replacement.addSubCell(MUXF5_8);
				RAM16X2S_replacement.addSubCell(MUXF6_1);
				RAM16X2S_replacement.addSubCell(MUXF6_2);
				RAM16X2S_replacement.addSubCell(MUXF6_3);
				RAM16X2S_replacement.addSubCell(MUXF6_4);
				
				/******Interface******/
				EdifCellInterface FDE1_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE2_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE3_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE4_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE5_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE6_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE7_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE8_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE9_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE10_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE11_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE12_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE13_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE14_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE15_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE16_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE17_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE18_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE19_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE20_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE21_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE22_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE23_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE24_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE25_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE26_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE27_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE28_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE29_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE30_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE31_interface = new EdifCellInterface(FDE);
				EdifCellInterface FDE32_interface = new EdifCellInterface(FDE);
				EdifCellInterface LUT2_1_interface = new EdifCellInterface(LUT2);
				EdifCellInterface LUT2_2_interface = new EdifCellInterface(LUT2);
				EdifCellInterface LUT3_1_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_2_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_3_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_4_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_5_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_6_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_7_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_8_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_9_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_10_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_11_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_12_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_13_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_14_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_15_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_16_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_17_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_18_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT4_1_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_2_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_3_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_4_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_5_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_6_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_7_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_8_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_9_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_10_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_11_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_12_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_13_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_14_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_15_interface = new EdifCellInterface(LUT4);
				EdifCellInterface LUT4_16_interface = new EdifCellInterface(LUT4);
				EdifCellInterface MUXF5_1_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_2_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_3_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_4_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_5_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_6_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_7_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_8_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF6_1_interface = new EdifCellInterface(MUXF6);
				EdifCellInterface MUXF6_2_interface = new EdifCellInterface(MUXF6);
				EdifCellInterface MUXF6_3_interface = new EdifCellInterface(MUXF6);
				EdifCellInterface MUXF6_4_interface = new EdifCellInterface(MUXF6);
				
				/******Ports******/
				//FDE1
				EdifPort FDE1_interface_C = new EdifPort(FDE1_interface, "C", 1, 1);
				EdifPort FDE1_interface_D = new EdifPort(FDE1_interface, "D", 1, 1);
				EdifPort FDE1_interface_CE = new EdifPort(FDE1_interface, "CE", 1, 1);
				EdifPort FDE1_interface_Q = new EdifPort(FDE1_interface, "Q", 1, 2);
				FDE1_interface.addPort("C", 1, 1);
				FDE1_interface.addPort("D", 1, 1);
				FDE1_interface.addPort("CE", 1, 1);
				FDE1_interface.addPort("Q", 1, 2);
				//FDE2
				EdifPort FDE2_interface_C = new EdifPort(FDE2_interface, "C", 1, 1);
				EdifPort FDE2_interface_D = new EdifPort(FDE2_interface, "D", 1, 1);
				EdifPort FDE2_interface_CE = new EdifPort(FDE2_interface, "CE", 1, 1);
				EdifPort FDE2_interface_Q = new EdifPort(FDE2_interface, "Q", 1, 2);
				FDE2_interface.addPort("C", 1, 1);
				FDE2_interface.addPort("D", 1, 1);
				FDE2_interface.addPort("CE", 1, 1);
				FDE2_interface.addPort("Q", 1, 2);
				//FDE3
				EdifPort FDE3_interface_C = new EdifPort(FDE3_interface, "C", 1, 1);
				EdifPort FDE3_interface_D = new EdifPort(FDE3_interface, "D", 1, 1);
				EdifPort FDE3_interface_CE = new EdifPort(FDE3_interface, "CE", 1, 1);
				EdifPort FDE3_interface_Q = new EdifPort(FDE3_interface, "Q", 1, 2);
				FDE3_interface.addPort("C", 1, 1);
				FDE3_interface.addPort("D", 1, 1);
				FDE3_interface.addPort("CE", 1, 1);
				FDE3_interface.addPort("Q", 1, 2);
				//FDE4
				EdifPort FDE4_interface_C = new EdifPort(FDE4_interface, "C", 1, 1);
				EdifPort FDE4_interface_D = new EdifPort(FDE4_interface, "D", 1, 1);
				EdifPort FDE4_interface_CE = new EdifPort(FDE4_interface, "CE", 1, 1);
				EdifPort FDE4_interface_Q = new EdifPort(FDE4_interface, "Q", 1, 2);
				FDE4_interface.addPort("C", 1, 1);
				FDE4_interface.addPort("D", 1, 1);
				FDE4_interface.addPort("CE", 1, 1);
				FDE4_interface.addPort("Q", 1, 2);
				//FDE5
				EdifPort FDE5_interface_C = new EdifPort(FDE5_interface, "C", 1, 1);
				EdifPort FDE5_interface_D = new EdifPort(FDE5_interface, "D", 1, 1);
				EdifPort FDE5_interface_CE = new EdifPort(FDE5_interface, "CE", 1, 1);
				EdifPort FDE5_interface_Q = new EdifPort(FDE5_interface, "Q", 1, 2);
				FDE5_interface.addPort("C", 1, 1);
				FDE5_interface.addPort("D", 1, 1);
				FDE5_interface.addPort("CE", 1, 1);
				FDE5_interface.addPort("Q", 1, 2);
				//FDE6
				EdifPort FDE6_interface_C = new EdifPort(FDE6_interface, "C", 1, 1);
				EdifPort FDE6_interface_D = new EdifPort(FDE6_interface, "D", 1, 1);
				EdifPort FDE6_interface_CE = new EdifPort(FDE6_interface, "CE", 1, 1);
				EdifPort FDE6_interface_Q = new EdifPort(FDE6_interface, "Q", 1, 2);
				FDE6_interface.addPort("C", 1, 1);
				FDE6_interface.addPort("D", 1, 1);
				FDE6_interface.addPort("CE", 1, 1);
				FDE6_interface.addPort("Q", 1, 2);
				//FDE7
				EdifPort FDE7_interface_C = new EdifPort(FDE7_interface, "C", 1, 1);
				EdifPort FDE7_interface_D = new EdifPort(FDE7_interface, "D", 1, 1);
				EdifPort FDE7_interface_CE = new EdifPort(FDE7_interface, "CE", 1, 1);
				EdifPort FDE7_interface_Q = new EdifPort(FDE7_interface, "Q", 1, 2);
				FDE7_interface.addPort("C", 1, 1);
				FDE7_interface.addPort("D", 1, 1);
				FDE7_interface.addPort("CE", 1, 1);
				FDE7_interface.addPort("Q", 1, 2);
				//FDE8
				EdifPort FDE8_interface_C = new EdifPort(FDE8_interface, "C", 1, 1);
				EdifPort FDE8_interface_D = new EdifPort(FDE8_interface, "D", 1, 1);
				EdifPort FDE8_interface_CE = new EdifPort(FDE8_interface, "CE", 1, 1);
				EdifPort FDE8_interface_Q = new EdifPort(FDE8_interface, "Q", 1, 2);
				FDE8_interface.addPort("C", 1, 1);
				FDE8_interface.addPort("D", 1, 1);
				FDE8_interface.addPort("CE", 1, 1);
				FDE8_interface.addPort("Q", 1, 2);
				//FDE9
				EdifPort FDE9_interface_C = new EdifPort(FDE9_interface, "C", 1, 1);
				EdifPort FDE9_interface_D = new EdifPort(FDE9_interface, "D", 1, 1);
				EdifPort FDE9_interface_CE = new EdifPort(FDE9_interface, "CE", 1, 1);
				EdifPort FDE9_interface_Q = new EdifPort(FDE9_interface, "Q", 1, 2);
				FDE9_interface.addPort("C", 1, 1);
				FDE9_interface.addPort("D", 1, 1);
				FDE9_interface.addPort("CE", 1, 1);
				FDE9_interface.addPort("Q", 1, 2);
				//FDE10
				EdifPort FDE10_interface_C = new EdifPort(FDE10_interface, "C", 1, 1);
				EdifPort FDE10_interface_D = new EdifPort(FDE10_interface, "D", 1, 1);
				EdifPort FDE10_interface_CE = new EdifPort(FDE10_interface, "CE", 1, 1);
				EdifPort FDE10_interface_Q = new EdifPort(FDE10_interface, "Q", 1, 2);
				FDE10_interface.addPort("C", 1, 1);
				FDE10_interface.addPort("D", 1, 1);
				FDE10_interface.addPort("CE", 1, 1);
				FDE10_interface.addPort("Q", 1, 2);
				//FDE11
				EdifPort FDE11_interface_C = new EdifPort(FDE11_interface, "C", 1, 1);
				EdifPort FDE11_interface_D = new EdifPort(FDE11_interface, "D", 1, 1);
				EdifPort FDE11_interface_CE = new EdifPort(FDE11_interface, "CE", 1, 1);
				EdifPort FDE11_interface_Q = new EdifPort(FDE11_interface, "Q", 1, 2);
				FDE11_interface.addPort("C", 1, 1);
				FDE11_interface.addPort("D", 1, 1);
				FDE11_interface.addPort("CE", 1, 1);
				FDE11_interface.addPort("Q", 1, 2);
				//FDE12
				EdifPort FDE12_interface_C = new EdifPort(FDE12_interface, "C", 1, 1);
				EdifPort FDE12_interface_D = new EdifPort(FDE12_interface, "D", 1, 1);
				EdifPort FDE12_interface_CE = new EdifPort(FDE13_interface, "CE", 1, 1);
				EdifPort FDE12_interface_Q = new EdifPort(FDE12_interface, "Q", 1, 2);
				FDE12_interface.addPort("C", 1, 1);
				FDE12_interface.addPort("D", 1, 1);
				FDE12_interface.addPort("CE", 1, 1);
				FDE12_interface.addPort("Q", 1, 2);
				//FDE13
				EdifPort FDE13_interface_C = new EdifPort(FDE13_interface, "C", 1, 1);
				EdifPort FDE13_interface_D = new EdifPort(FDE13_interface, "D", 1, 1);
				EdifPort FDE13_interface_CE = new EdifPort(FDE13_interface, "CE", 1, 1);
				EdifPort FDE13_interface_Q = new EdifPort(FDE13_interface, "Q", 1, 2);
				FDE13_interface.addPort("C", 1, 1);
				FDE13_interface.addPort("D", 1, 1);
				FDE13_interface.addPort("CE", 1, 1);
				FDE13_interface.addPort("Q", 1, 2);
				//FDE14
				EdifPort FDE14_interface_C = new EdifPort(FDE14_interface, "C", 1, 1);
				EdifPort FDE14_interface_D = new EdifPort(FDE14_interface, "D", 1, 1);
				EdifPort FDE14_interface_CE = new EdifPort(FDE15_interface, "CE", 1, 1);
				EdifPort FDE14_interface_Q = new EdifPort(FDE14_interface, "Q", 1, 2);
				FDE14_interface.addPort("C", 1, 1);
				FDE14_interface.addPort("D", 1, 1);
				FDE14_interface.addPort("CE", 1, 1);
				FDE14_interface.addPort("Q", 1, 2);
				//FDE15
				EdifPort FDE15_interface_C = new EdifPort(FDE15_interface, "C", 1, 1);
				EdifPort FDE15_interface_D = new EdifPort(FDE15_interface, "D", 1, 1);
				EdifPort FDE15_interface_CE = new EdifPort(FDE15_interface, "CE", 1, 1);
				EdifPort FDE15_interface_Q = new EdifPort(FDE15_interface, "Q", 1, 2);
				FDE15_interface.addPort("C", 1, 1);
				FDE15_interface.addPort("D", 1, 1);
				FDE15_interface.addPort("CE", 1, 1);
				FDE15_interface.addPort("Q", 1, 2);
				//FDE16
				EdifPort FDE16_interface_C = new EdifPort(FDE16_interface, "C", 1, 1);
				EdifPort FDE16_interface_D = new EdifPort(FDE16_interface, "D", 1, 1);
				EdifPort FDE16_interface_CE = new EdifPort(FDE16_interface, "CE", 1, 1);
				EdifPort FDE16_interface_Q = new EdifPort(FDE16_interface, "Q", 1, 2);
				FDE16_interface.addPort("C", 1, 1);
				FDE16_interface.addPort("D", 1, 1);
				FDE16_interface.addPort("CE", 1, 1);
				FDE16_interface.addPort("Q", 1, 2);
				//FDE17
				EdifPort FDE17_interface_C = new EdifPort(FDE17_interface, "C", 1, 1);
				EdifPort FDE17_interface_D = new EdifPort(FDE17_interface, "D", 1, 1);
				EdifPort FDE17_interface_CE = new EdifPort(FDE17_interface, "CE", 1, 1);
				EdifPort FDE17_interface_Q = new EdifPort(FDE17_interface, "Q", 1, 2);
				FDE17_interface.addPort("C", 1, 1);
				FDE17_interface.addPort("D", 1, 1);
				FDE17_interface.addPort("CE", 1, 1);
				FDE17_interface.addPort("Q", 1, 2);
				//FDE18
				EdifPort FDE18_interface_C = new EdifPort(FDE18_interface, "C", 1, 1);
				EdifPort FDE18_interface_D = new EdifPort(FDE18_interface, "D", 1, 1);
				EdifPort FDE18_interface_CE = new EdifPort(FDE18_interface, "CE", 1, 1);
				EdifPort FDE18_interface_Q = new EdifPort(FDE18_interface, "Q", 1, 2);
				FDE18_interface.addPort("C", 1, 1);
				FDE18_interface.addPort("D", 1, 1);
				FDE18_interface.addPort("CE", 1, 1);
				FDE18_interface.addPort("Q", 1, 2);
				//FDE19
				EdifPort FDE19_interface_C = new EdifPort(FDE19_interface, "C", 1, 1);
				EdifPort FDE19_interface_D = new EdifPort(FDE19_interface, "D", 1, 1);
				EdifPort FDE19_interface_CE = new EdifPort(FDE19_interface, "CE", 1, 1);
				EdifPort FDE19_interface_Q = new EdifPort(FDE19_interface, "Q", 1, 2);
				FDE19_interface.addPort("C", 1, 1);
				FDE19_interface.addPort("D", 1, 1);
				FDE19_interface.addPort("CE", 1, 1);
				FDE19_interface.addPort("Q", 1, 2);
				//FDE20
				EdifPort FDE20_interface_C = new EdifPort(FDE20_interface, "C", 1, 1);
				EdifPort FDE20_interface_D = new EdifPort(FDE20_interface, "D", 1, 1);
				EdifPort FDE20_interface_CE = new EdifPort(FDE20_interface, "CE", 1, 1);
				EdifPort FDE20_interface_Q = new EdifPort(FDE20_interface, "Q", 1, 2);
				FDE20_interface.addPort("C", 1, 1);
				FDE20_interface.addPort("D", 1, 1);
				FDE20_interface.addPort("CE", 1, 1);
				FDE20_interface.addPort("Q", 1, 2);
				//FDE21
				EdifPort FDE21_interface_C = new EdifPort(FDE21_interface, "C", 1, 1);
				EdifPort FDE21_interface_D = new EdifPort(FDE21_interface, "D", 1, 1);
				EdifPort FDE21_interface_CE = new EdifPort(FDE21_interface, "CE", 1, 1);
				EdifPort FDE21_interface_Q = new EdifPort(FDE21_interface, "Q", 1, 2);
				FDE21_interface.addPort("C", 1, 1);
				FDE21_interface.addPort("D", 1, 1);
				FDE21_interface.addPort("CE", 1, 1);
				FDE21_interface.addPort("Q", 1, 2);
				//FDE22
				EdifPort FDE22_interface_C = new EdifPort(FDE22_interface, "C", 1, 1);
				EdifPort FDE22_interface_D = new EdifPort(FDE22_interface, "D", 1, 1);
				EdifPort FDE22_interface_CE = new EdifPort(FDE22_interface, "CE", 1, 1);
				EdifPort FDE22_interface_Q = new EdifPort(FDE22_interface, "Q", 1, 2);
				FDE22_interface.addPort("C", 1, 1);
				FDE22_interface.addPort("D", 1, 1);
				FDE22_interface.addPort("CE", 1, 1);
				FDE22_interface.addPort("Q", 1, 2);
				//FDE23
				EdifPort FDE23_interface_C = new EdifPort(FDE23_interface, "C", 1, 1);
				EdifPort FDE23_interface_D = new EdifPort(FDE23_interface, "D", 1, 1);
				EdifPort FDE23_interface_CE = new EdifPort(FDE23_interface, "CE", 1, 1);
				EdifPort FDE23_interface_Q = new EdifPort(FDE23_interface, "Q", 1, 2);
				FDE23_interface.addPort("C", 1, 1);
				FDE23_interface.addPort("D", 1, 1);
				FDE23_interface.addPort("CE", 1, 1);
				FDE23_interface.addPort("Q", 1, 2);
				//FDE24
				EdifPort FDE24_interface_C = new EdifPort(FDE24_interface, "C", 1, 1);
				EdifPort FDE24_interface_D = new EdifPort(FDE24_interface, "D", 1, 1);
				EdifPort FDE24_interface_CE = new EdifPort(FDE24_interface, "CE", 1, 1);
				EdifPort FDE24_interface_Q = new EdifPort(FDE24_interface, "Q", 1, 2);
				FDE24_interface.addPort("C", 1, 1);
				FDE24_interface.addPort("D", 1, 1);
				FDE24_interface.addPort("CE", 1, 1);
				FDE24_interface.addPort("Q", 1, 2);
				//FDE25
				EdifPort FDE25_interface_C = new EdifPort(FDE25_interface, "C", 1, 1);
				EdifPort FDE25_interface_D = new EdifPort(FDE25_interface, "D", 1, 1);
				EdifPort FDE25_interface_CE = new EdifPort(FDE25_interface, "CE", 1, 1);
				EdifPort FDE25_interface_Q = new EdifPort(FDE25_interface, "Q", 1, 2);
				FDE25_interface.addPort("C", 1, 1);
				FDE25_interface.addPort("D", 1, 1);
				FDE25_interface.addPort("CE", 1, 1);
				FDE25_interface.addPort("Q", 1, 2);
				//FDE26
				EdifPort FDE26_interface_C = new EdifPort(FDE26_interface, "C", 1, 1);
				EdifPort FDE26_interface_D = new EdifPort(FDE26_interface, "D", 1, 1);
				EdifPort FDE26_interface_CE = new EdifPort(FDE26_interface, "CE", 1, 1);
				EdifPort FDE26_interface_Q = new EdifPort(FDE26_interface, "Q", 1, 2);
				FDE26_interface.addPort("C", 1, 1);
				FDE26_interface.addPort("D", 1, 1);
				FDE26_interface.addPort("CE", 1, 1);
				FDE26_interface.addPort("Q", 1, 2);
				//FDE27
				EdifPort FDE27_interface_C = new EdifPort(FDE27_interface, "C", 1, 1);
				EdifPort FDE27_interface_D = new EdifPort(FDE27_interface, "D", 1, 1);
				EdifPort FDE27_interface_CE = new EdifPort(FDE27_interface, "CE", 1, 1);
				EdifPort FDE27_interface_Q = new EdifPort(FDE27_interface, "Q", 1, 2);
				FDE27_interface.addPort("C", 1, 1);
				FDE27_interface.addPort("D", 1, 1);
				FDE27_interface.addPort("CE", 1, 1);
				FDE27_interface.addPort("Q", 1, 2);
				//FDE28
				EdifPort FDE28_interface_C = new EdifPort(FDE28_interface, "C", 1, 1);
				EdifPort FDE28_interface_D = new EdifPort(FDE28_interface, "D", 1, 1);
				EdifPort FDE28_interface_CE = new EdifPort(FDE28_interface, "CE", 1, 1);
				EdifPort FDE28_interface_Q = new EdifPort(FDE28_interface, "Q", 1, 2);
				FDE28_interface.addPort("C", 1, 1);
				FDE28_interface.addPort("D", 1, 1);
				FDE28_interface.addPort("CE", 1, 1);
				FDE28_interface.addPort("Q", 1, 2);
				//FDE29
				EdifPort FDE29_interface_C = new EdifPort(FDE29_interface, "C", 1, 1);
				EdifPort FDE29_interface_D = new EdifPort(FDE29_interface, "D", 1, 1);
				EdifPort FDE29_interface_CE = new EdifPort(FDE29_interface, "CE", 1, 1);
				EdifPort FDE29_interface_Q = new EdifPort(FDE29_interface, "Q", 1, 2);
				FDE29_interface.addPort("C", 1, 1);
				FDE29_interface.addPort("D", 1, 1);
				FDE29_interface.addPort("CE", 1, 1);
				FDE29_interface.addPort("Q", 1, 2);
				//FDE30
				EdifPort FDE30_interface_C = new EdifPort(FDE30_interface, "C", 1, 1);
				EdifPort FDE30_interface_D = new EdifPort(FDE30_interface, "D", 1, 1);
				EdifPort FDE30_interface_CE = new EdifPort(FDE30_interface, "CE", 1, 1);
				EdifPort FDE30_interface_Q = new EdifPort(FDE30_interface, "Q", 1, 2);
				FDE30_interface.addPort("C", 1, 1);
				FDE30_interface.addPort("D", 1, 1);
				FDE30_interface.addPort("CE", 1, 1);
				FDE30_interface.addPort("Q", 1, 2);
				//FDE31
				EdifPort FDE31_interface_C = new EdifPort(FDE31_interface, "C", 1, 1);
				EdifPort FDE31_interface_D = new EdifPort(FDE31_interface, "D", 1, 1);
				EdifPort FDE31_interface_CE = new EdifPort(FDE31_interface, "CE", 1, 1);
				EdifPort FDE31_interface_Q = new EdifPort(FDE31_interface, "Q", 1, 2);
				FDE31_interface.addPort("C", 1, 1);
				FDE31_interface.addPort("D", 1, 1);
				FDE31_interface.addPort("CE", 1, 1);
				FDE31_interface.addPort("Q", 1, 2);
				//FDE32
				EdifPort FDE32_interface_C = new EdifPort(FDE32_interface, "C", 1, 1);
				EdifPort FDE32_interface_D = new EdifPort(FDE32_interface, "D", 1, 1);
				EdifPort FDE32_interface_CE = new EdifPort(FDE32_interface, "CE", 1, 1);
				EdifPort FDE32_interface_Q = new EdifPort(FDE32_interface, "Q", 1, 2);
				FDE32_interface.addPort("C", 1, 1);
				FDE32_interface.addPort("D", 1, 1);
				FDE32_interface.addPort("CE", 1, 1);
				FDE32_interface.addPort("Q", 1, 2);
				//LUT2_1
				EdifPort LUT2_1_interface_I0 = new EdifPort(LUT2_1_interface, "I0", 1, 1);
				EdifPort LUT2_1_interface_I1 = new EdifPort(LUT2_1_interface, "I1", 1, 1);
				EdifPort LUT2_1_interface_O = new EdifPort(LUT2_1_interface, "O", 1, 2);
				LUT2_1_interface.addPort("I0", 1, 1);
				LUT2_1_interface.addPort("I1", 1, 1);
				LUT2_1_interface.addPort("O", 1, 2);
				//LUT2_2
				EdifPort LUT2_2_interface_I0 = new EdifPort(LUT2_2_interface, "I0", 1, 1);
				EdifPort LUT2_2_interface_I1 = new EdifPort(LUT2_2_interface, "I1", 1, 1);
				EdifPort LUT2_2_interface_O = new EdifPort(LUT2_2_interface, "O", 1, 2);
				LUT2_2_interface.addPort("I0", 1, 1);
				LUT2_2_interface.addPort("I1", 1, 1);
				LUT2_2_interface.addPort("O", 1, 2);
				//LUT3_1
				EdifPort LUT3_1_interface_I0 = new EdifPort(LUT3_1_interface, "I0", 1, 1);
				EdifPort LUT3_1_interface_I1 = new EdifPort(LUT3_1_interface, "I1", 1, 1);
				EdifPort LUT3_1_interface_I2 = new EdifPort(LUT3_1_interface, "I2", 1, 1);
				EdifPort LUT3_1_interface_O = new EdifPort(LUT3_1_interface, "O", 1, 2);
				LUT3_1_interface.addPort("I0", 1, 1);
				LUT3_1_interface.addPort("I1", 1, 1);
				LUT3_1_interface.addPort("I2", 1, 1);
				LUT3_1_interface.addPort("O", 1, 2);
				//LUT3_2
				EdifPort LUT3_2_interface_I0 = new EdifPort(LUT3_2_interface, "I0", 1, 1);
				EdifPort LUT3_2_interface_I1 = new EdifPort(LUT3_2_interface, "I1", 1, 1);
				EdifPort LUT3_2_interface_I2 = new EdifPort(LUT3_2_interface, "I2", 1, 1);
				EdifPort LUT3_2_interface_O = new EdifPort(LUT3_2_interface, "O", 1, 2);
				LUT3_2_interface.addPort("I0", 1, 1);
				LUT3_2_interface.addPort("I1", 1, 1);
				LUT3_2_interface.addPort("I2", 1, 1);
				LUT3_2_interface.addPort("O", 1, 2);
				//LUT3_3
				EdifPort LUT3_3_interface_I0 = new EdifPort(LUT3_3_interface, "I0", 1, 1);
				EdifPort LUT3_3_interface_I1 = new EdifPort(LUT3_3_interface, "I1", 1, 1);
				EdifPort LUT3_3_interface_I2 = new EdifPort(LUT3_3_interface, "I2", 1, 1);
				EdifPort LUT3_3_interface_O = new EdifPort(LUT3_3_interface, "O", 1, 2);
				LUT3_3_interface.addPort("I0", 1, 1);
				LUT3_3_interface.addPort("I1", 1, 1);
				LUT3_3_interface.addPort("I2", 1, 1);
				LUT3_3_interface.addPort("O", 1, 2);
				//LUT3_4
				EdifPort LUT3_4_interface_I0 = new EdifPort(LUT3_4_interface, "I0", 1, 1);
				EdifPort LUT3_4_interface_I1 = new EdifPort(LUT3_4_interface, "I1", 1, 1);
				EdifPort LUT3_4_interface_I2 = new EdifPort(LUT3_4_interface, "I2", 1, 1);
				EdifPort LUT3_4_interface_O = new EdifPort(LUT3_4_interface, "O", 1, 2);
				LUT3_4_interface.addPort("I0", 1, 1);
				LUT3_4_interface.addPort("I1", 1, 1);
				LUT3_4_interface.addPort("I2", 1, 1);
				LUT3_4_interface.addPort("O", 1, 2);
				//LUT3_5
				EdifPort LUT3_5_interface_I0 = new EdifPort(LUT3_5_interface, "I0", 1, 1);
				EdifPort LUT3_5_interface_I1 = new EdifPort(LUT3_5_interface, "I1", 1, 1);
				EdifPort LUT3_5_interface_I2 = new EdifPort(LUT3_5_interface, "I2", 1, 1);
				EdifPort LUT3_5_interface_O = new EdifPort(LUT3_5_interface, "O", 1, 2);
				LUT3_5_interface.addPort("I0", 1, 1);
				LUT3_5_interface.addPort("I1", 1, 1);
				LUT3_5_interface.addPort("I2", 1, 1);
				LUT3_5_interface.addPort("O", 1, 2);
				//LUT3_6
				EdifPort LUT3_6_interface_I0 = new EdifPort(LUT3_6_interface, "I0", 1, 1);
				EdifPort LUT3_6_interface_I1 = new EdifPort(LUT3_6_interface, "I1", 1, 1);
				EdifPort LUT3_6_interface_I2 = new EdifPort(LUT3_6_interface, "I2", 1, 1);
				EdifPort LUT3_6_interface_O = new EdifPort(LUT3_6_interface, "O", 1, 2);
				LUT3_6_interface.addPort("I0", 1, 1);
				LUT3_6_interface.addPort("I1", 1, 1);
				LUT3_6_interface.addPort("I2", 1, 1);
				LUT3_6_interface.addPort("O", 1, 2);
				//LUT3_7
				EdifPort LUT3_7_interface_I0 = new EdifPort(LUT3_7_interface, "I0", 1, 1);
				EdifPort LUT3_7_interface_I1 = new EdifPort(LUT3_7_interface, "I1", 1, 1);
				EdifPort LUT3_7_interface_I2 = new EdifPort(LUT3_7_interface, "I2", 1, 1);
				EdifPort LUT3_7_interface_O = new EdifPort(LUT3_7_interface, "O", 1, 2);
				LUT3_7_interface.addPort("I0", 1, 1);
				LUT3_7_interface.addPort("I1", 1, 1);
				LUT3_7_interface.addPort("I2", 1, 1);
				LUT3_7_interface.addPort("O", 1, 2);
				//LUT3_8
				EdifPort LUT3_8_interface_I0 = new EdifPort(LUT3_8_interface, "I0", 1, 1);
				EdifPort LUT3_8_interface_I1 = new EdifPort(LUT3_8_interface, "I1", 1, 1);
				EdifPort LUT3_8_interface_I2 = new EdifPort(LUT3_8_interface, "I2", 1, 1);
				EdifPort LUT3_8_interface_O = new EdifPort(LUT3_8_interface, "O", 1, 2);
				LUT3_8_interface.addPort("I0", 1, 1);
				LUT3_8_interface.addPort("I1", 1, 1);
				LUT3_8_interface.addPort("I2", 1, 1);
				LUT3_8_interface.addPort("O", 1, 2);
				//LUT3_9
				EdifPort LUT3_9_interface_I0 = new EdifPort(LUT3_9_interface, "I0", 1, 1);
				EdifPort LUT3_9_interface_I1 = new EdifPort(LUT3_9_interface, "I1", 1, 1);
				EdifPort LUT3_9_interface_I2 = new EdifPort(LUT3_9_interface, "I2", 1, 1);
				EdifPort LUT3_9_interface_O = new EdifPort(LUT3_9_interface, "O", 1, 2);
				LUT3_9_interface.addPort("I0", 1, 1);
				LUT3_9_interface.addPort("I1", 1, 1);
				LUT3_9_interface.addPort("I2", 1, 1);
				LUT3_9_interface.addPort("O", 1, 2);
				//LUT3_10
				EdifPort LUT3_10_interface_I0 = new EdifPort(LUT3_10_interface, "I0", 1, 1);
				EdifPort LUT3_10_interface_I1 = new EdifPort(LUT3_10_interface, "I1", 1, 1);
				EdifPort LUT3_10_interface_I2 = new EdifPort(LUT3_10_interface, "I2", 1, 1);
				EdifPort LUT3_10_interface_O = new EdifPort(LUT3_10_interface, "O", 1, 2);
				LUT3_10_interface.addPort("I0", 1, 1);
				LUT3_10_interface.addPort("I1", 1, 1);
				LUT3_10_interface.addPort("I2", 1, 1);
				LUT3_10_interface.addPort("O", 1, 2);
				//LUT3_11
				EdifPort LUT3_11_interface_I0 = new EdifPort(LUT3_11_interface, "I0", 1, 1);
				EdifPort LUT3_11_interface_I1 = new EdifPort(LUT3_11_interface, "I1", 1, 1);
				EdifPort LUT3_11_interface_I2 = new EdifPort(LUT3_11_interface, "I2", 1, 1);
				EdifPort LUT3_11_interface_O = new EdifPort(LUT3_11_interface, "O", 1, 2);
				LUT3_11_interface.addPort("I0", 1, 1);
				LUT3_11_interface.addPort("I1", 1, 1);
				LUT3_11_interface.addPort("I2", 1, 1);
				LUT3_11_interface.addPort("O", 1, 2);
				//LUT3_12
				EdifPort LUT3_12_interface_I0 = new EdifPort(LUT3_12_interface, "I0", 1, 1);
				EdifPort LUT3_12_interface_I1 = new EdifPort(LUT3_12_interface, "I1", 1, 1);
				EdifPort LUT3_12_interface_I2 = new EdifPort(LUT3_12_interface, "I2", 1, 1);
				EdifPort LUT3_12_interface_O = new EdifPort(LUT3_12_interface, "O", 1, 2);
				LUT3_12_interface.addPort("I0", 1, 1);
				LUT3_12_interface.addPort("I1", 1, 1);
				LUT3_12_interface.addPort("I2", 1, 1);
				LUT3_12_interface.addPort("O", 1, 2);
				//LUT3_13
				EdifPort LUT3_13_interface_I0 = new EdifPort(LUT3_13_interface, "I0", 1, 1);
				EdifPort LUT3_13_interface_I1 = new EdifPort(LUT3_13_interface, "I1", 1, 1);
				EdifPort LUT3_13_interface_I2 = new EdifPort(LUT3_13_interface, "I2", 1, 1);
				EdifPort LUT3_13_interface_O = new EdifPort(LUT3_13_interface, "O", 1, 2);
				LUT3_13_interface.addPort("I0", 1, 1);
				LUT3_13_interface.addPort("I1", 1, 1);
				LUT3_13_interface.addPort("I2", 1, 1);
				LUT3_13_interface.addPort("O", 1, 2);
				//LUT3_14
				EdifPort LUT3_14_interface_I0 = new EdifPort(LUT3_14_interface, "I0", 1, 1);
				EdifPort LUT3_14_interface_I1 = new EdifPort(LUT3_14_interface, "I1", 1, 1);
				EdifPort LUT3_14_interface_I2 = new EdifPort(LUT3_14_interface, "I2", 1, 1);
				EdifPort LUT3_14_interface_O = new EdifPort(LUT3_14_interface, "O", 1, 2);
				LUT3_14_interface.addPort("I0", 1, 1);
				LUT3_14_interface.addPort("I1", 1, 1);
				LUT3_14_interface.addPort("I2", 1, 1);
				LUT3_14_interface.addPort("O", 1, 2);
				//LUT3_15
				EdifPort LUT3_15_interface_I0 = new EdifPort(LUT3_15_interface, "I0", 1, 1);
				EdifPort LUT3_15_interface_I1 = new EdifPort(LUT3_15_interface, "I1", 1, 1);
				EdifPort LUT3_15_interface_I2 = new EdifPort(LUT3_15_interface, "I2", 1, 1);
				EdifPort LUT3_15_interface_O = new EdifPort(LUT3_15_interface, "O", 1, 2);
				LUT3_15_interface.addPort("I0", 1, 1);
				LUT3_15_interface.addPort("I1", 1, 1);
				LUT3_15_interface.addPort("I2", 1, 1);
				LUT3_15_interface.addPort("O", 1, 2);
				//LUT3_16
				EdifPort LUT3_16_interface_I0 = new EdifPort(LUT3_16_interface, "I0", 1, 1);
				EdifPort LUT3_16_interface_I1 = new EdifPort(LUT3_16_interface, "I1", 1, 1);
				EdifPort LUT3_16_interface_I2 = new EdifPort(LUT3_16_interface, "I2", 1, 1);
				EdifPort LUT3_16_interface_O = new EdifPort(LUT3_16_interface, "O", 1, 2);
				LUT3_16_interface.addPort("I0", 1, 1);
				LUT3_16_interface.addPort("I1", 1, 1);
				LUT3_16_interface.addPort("I2", 1, 1);
				LUT3_16_interface.addPort("O", 1, 2);
				//LUT3_17
				EdifPort LUT3_17_interface_I0 = new EdifPort(LUT3_17_interface, "I0", 1, 1);
				EdifPort LUT3_17_interface_I1 = new EdifPort(LUT3_17_interface, "I1", 1, 1);
				EdifPort LUT3_17_interface_I2 = new EdifPort(LUT3_17_interface, "I2", 1, 1);
				EdifPort LUT3_17_interface_O = new EdifPort(LUT3_17_interface, "O", 1, 2);
				LUT3_17_interface.addPort("I0", 1, 1);
				LUT3_17_interface.addPort("I1", 1, 1);
				LUT3_17_interface.addPort("I2", 1, 1);
				LUT3_17_interface.addPort("O", 1, 2);
				//LUT3_18
				EdifPort LUT3_18_interface_I0 = new EdifPort(LUT3_18_interface, "I0", 1, 1);
				EdifPort LUT3_18_interface_I1 = new EdifPort(LUT3_18_interface, "I1", 1, 1);
				EdifPort LUT3_18_interface_I2 = new EdifPort(LUT3_18_interface, "I2", 1, 1);
				EdifPort LUT3_18_interface_O = new EdifPort(LUT3_18_interface, "O", 1, 2);
				LUT3_18_interface.addPort("I0", 1, 1);
				LUT3_18_interface.addPort("I1", 1, 1);
				LUT3_18_interface.addPort("I2", 1, 1);
				LUT3_18_interface.addPort("O", 1, 2);
				//LUT4_1
				EdifPort LUT4_1_interface_I0 = new EdifPort(LUT4_1_interface, "I0", 1, 1);
				EdifPort LUT4_1_interface_I1 = new EdifPort(LUT4_1_interface, "I1", 1, 1);
				EdifPort LUT4_1_interface_I2 = new EdifPort(LUT4_1_interface, "I2", 1, 1);
				EdifPort LUT4_1_interface_I3 = new EdifPort(LUT4_1_interface, "I3", 1, 1);
				EdifPort LUT4_1_interface_O = new EdifPort(LUT4_1_interface, "O", 1, 2);
				LUT4_1_interface.addPort("I0", 1, 1);
				LUT4_1_interface.addPort("I1", 1, 1);
				LUT4_1_interface.addPort("I2", 1, 1);
				LUT4_1_interface.addPort("I3", 1, 1);
				LUT4_1_interface.addPort("O", 1, 2);
				//LUT4_2
				EdifPort LUT4_2_interface_I0 = new EdifPort(LUT4_2_interface, "I0", 1, 1);
				EdifPort LUT4_2_interface_I1 = new EdifPort(LUT4_2_interface, "I1", 1, 1);
				EdifPort LUT4_2_interface_I2 = new EdifPort(LUT4_2_interface, "I2", 1, 1);
				EdifPort LUT4_2_interface_I3 = new EdifPort(LUT4_2_interface, "I3", 1, 1);
				EdifPort LUT4_2_interface_O = new EdifPort(LUT4_2_interface, "O", 1, 2);
				LUT4_2_interface.addPort("I0", 1, 1);
				LUT4_2_interface.addPort("I1", 1, 1);
				LUT4_2_interface.addPort("I2", 1, 1);
				LUT4_2_interface.addPort("I3", 1, 1);
				LUT4_2_interface.addPort("O", 1, 2);
				//LUT4_3
				EdifPort LUT4_3_interface_I0 = new EdifPort(LUT4_3_interface, "I0", 1, 1);
				EdifPort LUT4_3_interface_I1 = new EdifPort(LUT4_3_interface, "I1", 1, 1);
				EdifPort LUT4_3_interface_I2 = new EdifPort(LUT4_3_interface, "I2", 1, 1);
				EdifPort LUT4_3_interface_I3 = new EdifPort(LUT4_3_interface, "I3", 1, 1);
				EdifPort LUT4_3_interface_O = new EdifPort(LUT4_3_interface, "O", 1, 2);
				LUT4_3_interface.addPort("I0", 1, 1);
				LUT4_3_interface.addPort("I1", 1, 1);
				LUT4_3_interface.addPort("I2", 1, 1);
				LUT4_3_interface.addPort("I3", 1, 1);
				LUT4_3_interface.addPort("O", 1, 2);
				//LUT4_4
				EdifPort LUT4_4_interface_I0 = new EdifPort(LUT4_4_interface, "I0", 1, 1);
				EdifPort LUT4_4_interface_I1 = new EdifPort(LUT4_4_interface, "I1", 1, 1);
				EdifPort LUT4_4_interface_I2 = new EdifPort(LUT4_4_interface, "I2", 1, 1);
				EdifPort LUT4_4_interface_I3 = new EdifPort(LUT4_4_interface, "I3", 1, 1);
				EdifPort LUT4_4_interface_O = new EdifPort(LUT4_4_interface, "O", 1, 2);
				LUT4_4_interface.addPort("I0", 1, 1);
				LUT4_4_interface.addPort("I1", 1, 1);
				LUT4_4_interface.addPort("I2", 1, 1);
				LUT4_4_interface.addPort("I3", 1, 1);
				LUT4_4_interface.addPort("O", 1, 2);
				//LUT4_5
				EdifPort LUT4_5_interface_I0 = new EdifPort(LUT4_5_interface, "I0", 1, 1);
				EdifPort LUT4_5_interface_I1 = new EdifPort(LUT4_5_interface, "I1", 1, 1);
				EdifPort LUT4_5_interface_I2 = new EdifPort(LUT4_5_interface, "I2", 1, 1);
				EdifPort LUT4_5_interface_I3 = new EdifPort(LUT4_5_interface, "I3", 1, 1);
				EdifPort LUT4_5_interface_O = new EdifPort(LUT4_5_interface, "O", 1, 2);
				LUT4_5_interface.addPort("I0", 1, 1);
				LUT4_5_interface.addPort("I1", 1, 1);
				LUT4_5_interface.addPort("I2", 1, 1);
				LUT4_5_interface.addPort("I3", 1, 1);
				LUT4_5_interface.addPort("O", 1, 2);
				//LUT4_6
				EdifPort LUT4_6_interface_I0 = new EdifPort(LUT4_6_interface, "I0", 1, 1);
				EdifPort LUT4_6_interface_I1 = new EdifPort(LUT4_6_interface, "I1", 1, 1);
				EdifPort LUT4_6_interface_I2 = new EdifPort(LUT4_6_interface, "I2", 1, 1);
				EdifPort LUT4_6_interface_I3 = new EdifPort(LUT4_6_interface, "I3", 1, 1);
				EdifPort LUT4_6_interface_O = new EdifPort(LUT4_6_interface, "O", 1, 2);
				LUT4_6_interface.addPort("I0", 1, 1);
				LUT4_6_interface.addPort("I1", 1, 1);
				LUT4_6_interface.addPort("I2", 1, 1);
				LUT4_6_interface.addPort("I3", 1, 1);
				LUT4_6_interface.addPort("O", 1, 2);
				//LUT4_7
				EdifPort LUT4_7_interface_I0 = new EdifPort(LUT4_7_interface, "I0", 1, 1);
				EdifPort LUT4_7_interface_I1 = new EdifPort(LUT4_7_interface, "I1", 1, 1);
				EdifPort LUT4_7_interface_I2 = new EdifPort(LUT4_7_interface, "I2", 1, 1);
				EdifPort LUT4_7_interface_I3 = new EdifPort(LUT4_7_interface, "I3", 1, 1);
				EdifPort LUT4_7_interface_O = new EdifPort(LUT4_7_interface, "O", 1, 2);
				LUT4_7_interface.addPort("I0", 1, 1);
				LUT4_7_interface.addPort("I1", 1, 1);
				LUT4_7_interface.addPort("I2", 1, 1);
				LUT4_7_interface.addPort("I3", 1, 1);
				LUT4_7_interface.addPort("O", 1, 2);
				//LUT4_8
				EdifPort LUT4_8_interface_I0 = new EdifPort(LUT4_8_interface, "I0", 1, 1);
				EdifPort LUT4_8_interface_I1 = new EdifPort(LUT4_8_interface, "I1", 1, 1);
				EdifPort LUT4_8_interface_I2 = new EdifPort(LUT4_8_interface, "I2", 1, 1);
				EdifPort LUT4_8_interface_I3 = new EdifPort(LUT4_8_interface, "I3", 1, 1);
				EdifPort LUT4_8_interface_O = new EdifPort(LUT4_8_interface, "O", 1, 2);
				LUT4_8_interface.addPort("I0", 1, 1);
				LUT4_8_interface.addPort("I1", 1, 1);
				LUT4_8_interface.addPort("I2", 1, 1);
				LUT4_8_interface.addPort("I3", 1, 1);
				LUT4_8_interface.addPort("O", 1, 2);
				//LUT4_9
				EdifPort LUT4_9_interface_I0 = new EdifPort(LUT4_9_interface, "I0", 1, 1);
				EdifPort LUT4_9_interface_I1 = new EdifPort(LUT4_9_interface, "I1", 1, 1);
				EdifPort LUT4_9_interface_I2 = new EdifPort(LUT4_9_interface, "I2", 1, 1);
				EdifPort LUT4_9_interface_I3 = new EdifPort(LUT4_9_interface, "I3", 1, 1);
				EdifPort LUT4_9_interface_O = new EdifPort(LUT4_9_interface, "O", 1, 2);
				LUT4_9_interface.addPort("I0", 1, 1);
				LUT4_9_interface.addPort("I1", 1, 1);
				LUT4_9_interface.addPort("I2", 1, 1);
				LUT4_9_interface.addPort("I3", 1, 1);
				LUT4_9_interface.addPort("O", 1, 2);
				//LUT4_10
				EdifPort LUT4_10_interface_I0 = new EdifPort(LUT4_10_interface, "I0", 1, 1);
				EdifPort LUT4_10_interface_I1 = new EdifPort(LUT4_10_interface, "I1", 1, 1);
				EdifPort LUT4_10_interface_I2 = new EdifPort(LUT4_10_interface, "I2", 1, 1);
				EdifPort LUT4_10_interface_I3 = new EdifPort(LUT4_10_interface, "I3", 1, 1);
				EdifPort LUT4_10_interface_O = new EdifPort(LUT4_10_interface, "O", 1, 2);
				LUT4_10_interface.addPort("I0", 1, 1);
				LUT4_10_interface.addPort("I1", 1, 1);
				LUT4_10_interface.addPort("I2", 1, 1);
				LUT4_10_interface.addPort("I3", 1, 1);
				LUT4_10_interface.addPort("O", 1, 2);
				//LUT4_11
				EdifPort LUT4_11_interface_I0 = new EdifPort(LUT4_11_interface, "I0", 1, 1);
				EdifPort LUT4_11_interface_I1 = new EdifPort(LUT4_11_interface, "I1", 1, 1);
				EdifPort LUT4_11_interface_I2 = new EdifPort(LUT4_11_interface, "I2", 1, 1);
				EdifPort LUT4_11_interface_I3 = new EdifPort(LUT4_11_interface, "I3", 1, 1);
				EdifPort LUT4_11_interface_O = new EdifPort(LUT4_11_interface, "O", 1, 2);
				LUT4_11_interface.addPort("I0", 1, 1);
				LUT4_11_interface.addPort("I1", 1, 1);
				LUT4_11_interface.addPort("I2", 1, 1);
				LUT4_11_interface.addPort("I3", 1, 1);
				LUT4_11_interface.addPort("O", 1, 2);
				//LUT4_12
				EdifPort LUT4_12_interface_I0 = new EdifPort(LUT4_12_interface, "I0", 1, 1);
				EdifPort LUT4_12_interface_I1 = new EdifPort(LUT4_12_interface, "I1", 1, 1);
				EdifPort LUT4_12_interface_I2 = new EdifPort(LUT4_12_interface, "I2", 1, 1);
				EdifPort LUT4_12_interface_I3 = new EdifPort(LUT4_12_interface, "I3", 1, 1);
				EdifPort LUT4_12_interface_O = new EdifPort(LUT4_12_interface, "O", 1, 2);
				LUT4_12_interface.addPort("I0", 1, 1);
				LUT4_12_interface.addPort("I1", 1, 1);
				LUT4_12_interface.addPort("I2", 1, 1);
				LUT4_12_interface.addPort("I3", 1, 1);
				LUT4_12_interface.addPort("O", 1, 2);
				//LUT4_13
				EdifPort LUT4_13_interface_I0 = new EdifPort(LUT4_13_interface, "I0", 1, 1);
				EdifPort LUT4_13_interface_I1 = new EdifPort(LUT4_13_interface, "I1", 1, 1);
				EdifPort LUT4_13_interface_I2 = new EdifPort(LUT4_13_interface, "I2", 1, 1);
				EdifPort LUT4_13_interface_I3 = new EdifPort(LUT4_13_interface, "I3", 1, 1);
				EdifPort LUT4_13_interface_O = new EdifPort(LUT4_13_interface, "O", 1, 2);
				LUT4_13_interface.addPort("I0", 1, 1);
				LUT4_13_interface.addPort("I1", 1, 1);
				LUT4_13_interface.addPort("I2", 1, 1);
				LUT4_13_interface.addPort("I3", 1, 1);
				LUT4_13_interface.addPort("O", 1, 2);
				//LUT4_14
				EdifPort LUT4_14_interface_I0 = new EdifPort(LUT4_14_interface, "I0", 1, 1);
				EdifPort LUT4_14_interface_I1 = new EdifPort(LUT4_14_interface, "I1", 1, 1);
				EdifPort LUT4_14_interface_I2 = new EdifPort(LUT4_14_interface, "I2", 1, 1);
				EdifPort LUT4_14_interface_I3 = new EdifPort(LUT4_14_interface, "I3", 1, 1);
				EdifPort LUT4_14_interface_O = new EdifPort(LUT4_14_interface, "O", 1, 2);
				LUT4_14_interface.addPort("I0", 1, 1);
				LUT4_14_interface.addPort("I1", 1, 1);
				LUT4_14_interface.addPort("I2", 1, 1);
				LUT4_14_interface.addPort("I3", 1, 1);
				LUT4_14_interface.addPort("O", 1, 2);
				//LUT4_15
				EdifPort LUT4_15_interface_I0 = new EdifPort(LUT4_15_interface, "I0", 1, 1);
				EdifPort LUT4_15_interface_I1 = new EdifPort(LUT4_15_interface, "I1", 1, 1);
				EdifPort LUT4_15_interface_I2 = new EdifPort(LUT4_15_interface, "I2", 1, 1);
				EdifPort LUT4_15_interface_I3 = new EdifPort(LUT4_15_interface, "I3", 1, 1);
				EdifPort LUT4_15_interface_O = new EdifPort(LUT4_15_interface, "O", 1, 2);
				LUT4_15_interface.addPort("I0", 1, 1);
				LUT4_15_interface.addPort("I1", 1, 1);
				LUT4_15_interface.addPort("I2", 1, 1);
				LUT4_15_interface.addPort("I3", 1, 1);
				LUT4_15_interface.addPort("O", 1, 2);
				//LUT4_16
				EdifPort LUT4_16_interface_I0 = new EdifPort(LUT4_16_interface, "I0", 1, 1);
				EdifPort LUT4_16_interface_I1 = new EdifPort(LUT4_16_interface, "I1", 1, 1);
				EdifPort LUT4_16_interface_I2 = new EdifPort(LUT4_16_interface, "I2", 1, 1);
				EdifPort LUT4_16_interface_I3 = new EdifPort(LUT4_16_interface, "I3", 1, 1);
				EdifPort LUT4_16_interface_O = new EdifPort(LUT4_16_interface, "O", 1, 2);
				LUT4_16_interface.addPort("I0", 1, 1);
				LUT4_16_interface.addPort("I1", 1, 1);
				LUT4_16_interface.addPort("I2", 1, 1);
				LUT4_16_interface.addPort("I3", 1, 1);
				LUT4_16_interface.addPort("O", 1, 2);
				//MUXF5_1
				EdifPort MUXF5_1_interface_I0 = new EdifPort(MUXF5_1_interface, "I0", 1, 1);
				EdifPort MUXF5_1_interface_I1 = new EdifPort(MUXF5_1_interface, "I1", 1, 1);
				EdifPort MUXF5_1_interface_S = new EdifPort(MUXF5_1_interface, "S", 1, 1);
				EdifPort MUXF5_1_interface_O = new EdifPort(MUXF5_1_interface, "O", 1, 2);
				MUXF5_1_interface.addPort("I0", 1, 1);
				MUXF5_1_interface.addPort("I1", 1, 1);
				MUXF5_1_interface.addPort("S", 1, 1);
				MUXF5_1_interface.addPort("O", 1, 2);
				//MUXF5_2
				EdifPort MUXF5_2_interface_I0 = new EdifPort(MUXF5_2_interface, "I0", 1, 1);
				EdifPort MUXF5_2_interface_I1 = new EdifPort(MUXF5_2_interface, "I1", 1, 1);
				EdifPort MUXF5_2_interface_S = new EdifPort(MUXF5_2_interface, "S", 1, 1);
				EdifPort MUXF5_2_interface_O = new EdifPort(MUXF5_2_interface, "O", 1, 2);
				MUXF5_2_interface.addPort("I0", 1, 1);
				MUXF5_2_interface.addPort("I1", 1, 1);
				MUXF5_2_interface.addPort("S", 1, 1);
				MUXF5_2_interface.addPort("O", 1, 2);
				//MUXF5_3
				EdifPort MUXF5_3_interface_I0 = new EdifPort(MUXF5_3_interface, "I0", 1, 1);
				EdifPort MUXF5_3_interface_I1 = new EdifPort(MUXF5_3_interface, "I1", 1, 1);
				EdifPort MUXF5_3_interface_S = new EdifPort(MUXF5_3_interface, "S", 1, 1);
				EdifPort MUXF5_3_interface_O = new EdifPort(MUXF5_3_interface, "O", 1, 2);
				MUXF5_3_interface.addPort("I0", 1, 1);
				MUXF5_3_interface.addPort("I1", 1, 1);
				MUXF5_3_interface.addPort("S", 1, 1);
				MUXF5_3_interface.addPort("O", 1, 2);
				//MUXF5_4
				EdifPort MUXF5_4_interface_I0 = new EdifPort(MUXF5_4_interface, "I0", 1, 1);
				EdifPort MUXF5_4_interface_I1 = new EdifPort(MUXF5_4_interface, "I1", 1, 1);
				EdifPort MUXF5_4_interface_S = new EdifPort(MUXF5_4_interface, "S", 1, 1);
				EdifPort MUXF5_4_interface_O = new EdifPort(MUXF5_4_interface, "O", 1, 2);
				MUXF5_4_interface.addPort("I0", 1, 1);
				MUXF5_4_interface.addPort("I1", 1, 1);
				MUXF5_4_interface.addPort("S", 1, 1);
				MUXF5_4_interface.addPort("O", 1, 2);
				//MUXF5_5
				EdifPort MUXF5_5_interface_I0 = new EdifPort(MUXF5_5_interface, "I0", 1, 1);
				EdifPort MUXF5_5_interface_I1 = new EdifPort(MUXF5_5_interface, "I1", 1, 1);
				EdifPort MUXF5_5_interface_S = new EdifPort(MUXF5_5_interface, "S", 1, 1);
				EdifPort MUXF5_5_interface_O = new EdifPort(MUXF5_5_interface, "O", 1, 2);
				MUXF5_5_interface.addPort("I0", 1, 1);
				MUXF5_5_interface.addPort("I1", 1, 1);
				MUXF5_5_interface.addPort("S", 1, 1);
				MUXF5_5_interface.addPort("O", 1, 2);
				//MUXF5_6
				EdifPort MUXF5_6_interface_I0 = new EdifPort(MUXF5_6_interface, "I0", 1, 1);
				EdifPort MUXF5_6_interface_I1 = new EdifPort(MUXF5_6_interface, "I1", 1, 1);
				EdifPort MUXF5_6_interface_S = new EdifPort(MUXF5_6_interface, "S", 1, 1);
				EdifPort MUXF5_6_interface_O = new EdifPort(MUXF5_6_interface, "O", 1, 2);
				MUXF5_6_interface.addPort("I0", 1, 1);
				MUXF5_6_interface.addPort("I1", 1, 1);
				MUXF5_6_interface.addPort("S", 1, 1);
				MUXF5_6_interface.addPort("O", 1, 2);
				//MUXF5_7
				EdifPort MUXF5_7_interface_I0 = new EdifPort(MUXF5_7_interface, "I0", 1, 1);
				EdifPort MUXF5_7_interface_I1 = new EdifPort(MUXF5_7_interface, "I1", 1, 1);
				EdifPort MUXF5_7_interface_S = new EdifPort(MUXF5_7_interface, "S", 1, 1);
				EdifPort MUXF5_7_interface_O = new EdifPort(MUXF5_7_interface, "O", 1, 2);
				MUXF5_7_interface.addPort("I0", 1, 1);
				MUXF5_7_interface.addPort("I1", 1, 1);
				MUXF5_7_interface.addPort("S", 1, 1);
				MUXF5_7_interface.addPort("O", 1, 2);
				//MUXF5_8
				EdifPort MUXF5_8_interface_I0 = new EdifPort(MUXF5_8_interface, "I0", 1, 1);
				EdifPort MUXF5_8_interface_I1 = new EdifPort(MUXF5_8_interface, "I1", 1, 1);
				EdifPort MUXF5_8_interface_S = new EdifPort(MUXF5_8_interface, "S", 1, 1);
				EdifPort MUXF5_8_interface_O = new EdifPort(MUXF5_8_interface, "O", 1, 2);
				MUXF5_8_interface.addPort("I0", 1, 1);
				MUXF5_8_interface.addPort("I1", 1, 1);
				MUXF5_8_interface.addPort("S", 1, 1);
				MUXF5_8_interface.addPort("O", 1, 2);
				//MUXF6_1
				EdifPort MUXF6_1_interface_I0 = new EdifPort(MUXF6_1_interface, "I0", 1, 1);
				EdifPort MUXF6_1_interface_I1 = new EdifPort(MUXF6_1_interface, "I1", 1, 1);
				EdifPort MUXF6_1_interface_S = new EdifPort(MUXF6_1_interface, "S", 1, 1);
				EdifPort MUXF6_1_interface_O = new EdifPort(MUXF6_1_interface, "O", 1, 2);
				MUXF6_1_interface.addPort("I0", 1, 1);
				MUXF6_1_interface.addPort("I1", 1, 1);
				MUXF6_1_interface.addPort("S", 1, 1);
				MUXF6_1_interface.addPort("O", 1, 2);
				//MUXF6_2
				EdifPort MUXF6_2_interface_I0 = new EdifPort(MUXF6_2_interface, "I0", 1, 1);
				EdifPort MUXF6_2_interface_I1 = new EdifPort(MUXF6_2_interface, "I1", 1, 1);
				EdifPort MUXF6_2_interface_S = new EdifPort(MUXF6_2_interface, "S", 1, 1);
				EdifPort MUXF6_2_interface_O = new EdifPort(MUXF6_2_interface, "O", 1, 2);
				MUXF6_2_interface.addPort("I0", 1, 1);
				MUXF6_2_interface.addPort("I1", 1, 1);
				MUXF6_2_interface.addPort("S", 1, 1);
				MUXF6_2_interface.addPort("O", 1, 2);
				//MUXF6_3
				EdifPort MUXF6_3_interface_I0 = new EdifPort(MUXF6_3_interface, "I0", 1, 1);
				EdifPort MUXF6_3_interface_I1 = new EdifPort(MUXF6_3_interface, "I1", 1, 1);
				EdifPort MUXF6_3_interface_S = new EdifPort(MUXF6_3_interface, "S", 1, 1);
				EdifPort MUXF6_3_interface_O = new EdifPort(MUXF6_3_interface, "O", 1, 2);
				MUXF6_3_interface.addPort("I0", 1, 1);
				MUXF6_3_interface.addPort("I1", 1, 1);
				MUXF6_3_interface.addPort("S", 1, 1);
				MUXF6_3_interface.addPort("O", 1, 2);
				//MUXF6_4
				EdifPort MUXF6_4_interface_I0 = new EdifPort(MUXF6_4_interface, "I0", 1, 1);
				EdifPort MUXF6_4_interface_I1 = new EdifPort(MUXF6_4_interface, "I1", 1, 1);
				EdifPort MUXF6_4_interface_S = new EdifPort(MUXF6_4_interface, "S", 1, 1);
				EdifPort MUXF6_4_interface_O = new EdifPort(MUXF6_4_interface, "O", 1, 2);
				MUXF6_4_interface.addPort("I0", 1, 1);
				MUXF6_4_interface.addPort("I1", 1, 1);
				MUXF6_4_interface.addPort("S", 1, 1);
				MUXF6_4_interface.addPort("O", 1, 2);
				
				/******PortRefs******/
				EdifPortRef FDE1_C = new EdifPortRef(wclk, FDE1_interface_C.getSingleBitPort(0), FDE1);
				EdifPortRef FDE1_D = new EdifPortRef(d0, FDE1_interface_D.getSingleBitPort(0), FDE1);
				EdifPortRef FDE1_CE = new EdifPortRef(e1, FDE1_interface_CE.getSingleBitPort(0), FDE1);
				EdifPortRef FDE1_Q = new EdifPortRef(wire1, FDE1_interface_Q.getSingleBitPort(0), FDE1);
				EdifPortRef FDE2_C = new EdifPortRef(wclk, FDE2_interface_C.getSingleBitPort(0), FDE2);
				EdifPortRef FDE2_D = new EdifPortRef(d0, FDE2_interface_D.getSingleBitPort(0), FDE2);
				EdifPortRef FDE2_CE = new EdifPortRef(e2, FDE2_interface_CE.getSingleBitPort(0), FDE2);
				EdifPortRef FDE2_Q = new EdifPortRef(wire2, FDE2_interface_Q.getSingleBitPort(0), FDE2);
				EdifPortRef FDE3_C = new EdifPortRef(wclk, FDE3_interface_C.getSingleBitPort(0), FDE3);
				EdifPortRef FDE3_D = new EdifPortRef(d0, FDE3_interface_D.getSingleBitPort(0), FDE3);
				EdifPortRef FDE3_CE = new EdifPortRef(e3, FDE3_interface_CE.getSingleBitPort(0), FDE3);
				EdifPortRef FDE3_Q = new EdifPortRef(wire3, FDE3_interface_Q.getSingleBitPort(0), FDE3);
				EdifPortRef FDE4_C = new EdifPortRef(wclk, FDE4_interface_C.getSingleBitPort(0), FDE4);
				EdifPortRef FDE4_D = new EdifPortRef(d0, FDE4_interface_D.getSingleBitPort(0), FDE4);
				EdifPortRef FDE4_CE = new EdifPortRef(e4, FDE4_interface_CE.getSingleBitPort(0), FDE4);
				EdifPortRef FDE4_Q = new EdifPortRef(wire4, FDE4_interface_Q.getSingleBitPort(0), FDE4);
				EdifPortRef FDE5_C = new EdifPortRef(wclk, FDE5_interface_C.getSingleBitPort(0), FDE5);
				EdifPortRef FDE5_D = new EdifPortRef(d0, FDE5_interface_D.getSingleBitPort(0), FDE5);
				EdifPortRef FDE5_CE = new EdifPortRef(e5, FDE5_interface_CE.getSingleBitPort(0), FDE5);
				EdifPortRef FDE5_Q = new EdifPortRef(wire5, FDE5_interface_Q.getSingleBitPort(0), FDE5);
				EdifPortRef FDE6_C = new EdifPortRef(wclk, FDE6_interface_C.getSingleBitPort(0), FDE6);
				EdifPortRef FDE6_D = new EdifPortRef(d0, FDE6_interface_D.getSingleBitPort(0), FDE6);
				EdifPortRef FDE6_CE = new EdifPortRef(e6, FDE6_interface_CE.getSingleBitPort(0), FDE6);
				EdifPortRef FDE6_Q = new EdifPortRef(wire6, FDE6_interface_Q.getSingleBitPort(0), FDE6);
				EdifPortRef FDE7_C = new EdifPortRef(wclk, FDE7_interface_C.getSingleBitPort(0), FDE7);
				EdifPortRef FDE7_D = new EdifPortRef(d0, FDE7_interface_D.getSingleBitPort(0), FDE7);
				EdifPortRef FDE7_CE = new EdifPortRef(e7, FDE7_interface_CE.getSingleBitPort(0), FDE7);
				EdifPortRef FDE7_Q = new EdifPortRef(wire7, FDE7_interface_Q.getSingleBitPort(0), FDE7);
				EdifPortRef FDE8_C = new EdifPortRef(wclk, FDE8_interface_C.getSingleBitPort(0), FDE8);
				EdifPortRef FDE8_D = new EdifPortRef(d0, FDE8_interface_D.getSingleBitPort(0), FDE8);
				EdifPortRef FDE8_CE = new EdifPortRef(e8, FDE8_interface_CE.getSingleBitPort(0), FDE8);
				EdifPortRef FDE8_Q = new EdifPortRef(wire8, FDE8_interface_Q.getSingleBitPort(0), FDE8);
				EdifPortRef FDE9_C = new EdifPortRef(wclk, FDE9_interface_C.getSingleBitPort(0), FDE9);
				EdifPortRef FDE9_D = new EdifPortRef(d0, FDE9_interface_D.getSingleBitPort(0), FDE9);
				EdifPortRef FDE9_CE = new EdifPortRef(e9, FDE9_interface_CE.getSingleBitPort(0), FDE9);
				EdifPortRef FDE9_Q = new EdifPortRef(wire9, FDE9_interface_Q.getSingleBitPort(0), FDE9);
				EdifPortRef FDE10_C = new EdifPortRef(wclk, FDE10_interface_C.getSingleBitPort(0), FDE10);
				EdifPortRef FDE10_D = new EdifPortRef(d0, FDE10_interface_D.getSingleBitPort(0), FDE10);
				EdifPortRef FDE10_CE = new EdifPortRef(e10, FDE10_interface_CE.getSingleBitPort(0), FDE10);
				EdifPortRef FDE10_Q = new EdifPortRef(wire10, FDE10_interface_Q.getSingleBitPort(0), FDE10);
				EdifPortRef FDE11_C = new EdifPortRef(wclk, FDE11_interface_C.getSingleBitPort(0), FDE11);
				EdifPortRef FDE11_D = new EdifPortRef(d0, FDE11_interface_D.getSingleBitPort(0), FDE11);
				EdifPortRef FDE11_CE = new EdifPortRef(e11, FDE11_interface_CE.getSingleBitPort(0), FDE11);
				EdifPortRef FDE11_Q = new EdifPortRef(wire11, FDE11_interface_Q.getSingleBitPort(0), FDE11);
				EdifPortRef FDE12_C = new EdifPortRef(wclk, FDE12_interface_C.getSingleBitPort(0), FDE12);
				EdifPortRef FDE12_D = new EdifPortRef(d0, FDE12_interface_D.getSingleBitPort(0), FDE12);
				EdifPortRef FDE12_CE = new EdifPortRef(e12, FDE12_interface_CE.getSingleBitPort(0), FDE12);
				EdifPortRef FDE12_Q = new EdifPortRef(wire12, FDE12_interface_Q.getSingleBitPort(0), FDE12);
				EdifPortRef FDE13_C = new EdifPortRef(wclk, FDE13_interface_C.getSingleBitPort(0), FDE13);
				EdifPortRef FDE13_D = new EdifPortRef(d0, FDE13_interface_D.getSingleBitPort(0), FDE13);
				EdifPortRef FDE13_CE = new EdifPortRef(e13, FDE13_interface_CE.getSingleBitPort(0), FDE13);
				EdifPortRef FDE13_Q = new EdifPortRef(wire13, FDE13_interface_Q.getSingleBitPort(0), FDE13);
				EdifPortRef FDE14_C = new EdifPortRef(wclk, FDE14_interface_C.getSingleBitPort(0), FDE14);
				EdifPortRef FDE14_D = new EdifPortRef(d0, FDE14_interface_D.getSingleBitPort(0), FDE14);
				EdifPortRef FDE14_CE = new EdifPortRef(e14, FDE14_interface_CE.getSingleBitPort(0), FDE14);
				EdifPortRef FDE14_Q = new EdifPortRef(wire14, FDE14_interface_Q.getSingleBitPort(0), FDE14);
				EdifPortRef FDE15_C = new EdifPortRef(wclk, FDE15_interface_C.getSingleBitPort(0), FDE15);
				EdifPortRef FDE15_D = new EdifPortRef(d0, FDE15_interface_D.getSingleBitPort(0), FDE15);
				EdifPortRef FDE15_CE = new EdifPortRef(e15, FDE15_interface_CE.getSingleBitPort(0), FDE15);
				EdifPortRef FDE15_Q = new EdifPortRef(wire15, FDE15_interface_Q.getSingleBitPort(0), FDE15);
				EdifPortRef FDE16_C = new EdifPortRef(wclk, FDE16_interface_C.getSingleBitPort(0), FDE16);
				EdifPortRef FDE16_D = new EdifPortRef(d0, FDE16_interface_D.getSingleBitPort(0), FDE16);
				EdifPortRef FDE16_CE = new EdifPortRef(e16, FDE16_interface_CE.getSingleBitPort(0), FDE16);
				EdifPortRef FDE16_Q = new EdifPortRef(wire16, FDE16_interface_Q.getSingleBitPort(0), FDE16);
				EdifPortRef FDE17_C = new EdifPortRef(wclk, FDE17_interface_C.getSingleBitPort(0), FDE17);
				EdifPortRef FDE17_D = new EdifPortRef(d1, FDE17_interface_D.getSingleBitPort(0), FDE17);
				EdifPortRef FDE17_CE = new EdifPortRef(e1, FDE17_interface_CE.getSingleBitPort(0), FDE17);
				EdifPortRef FDE17_Q = new EdifPortRef(wire17, FDE17_interface_Q.getSingleBitPort(0), FDE17);
				EdifPortRef FDE18_C = new EdifPortRef(wclk, FDE18_interface_C.getSingleBitPort(0), FDE18);
				EdifPortRef FDE18_D = new EdifPortRef(d1, FDE18_interface_D.getSingleBitPort(0), FDE18);
				EdifPortRef FDE18_CE = new EdifPortRef(e2, FDE18_interface_CE.getSingleBitPort(0), FDE18);
				EdifPortRef FDE18_Q = new EdifPortRef(wire18, FDE18_interface_Q.getSingleBitPort(0), FDE18);
				EdifPortRef FDE19_C = new EdifPortRef(wclk, FDE19_interface_C.getSingleBitPort(0), FDE19);
				EdifPortRef FDE19_D = new EdifPortRef(d1, FDE19_interface_D.getSingleBitPort(0), FDE19);
				EdifPortRef FDE19_CE = new EdifPortRef(e3, FDE19_interface_CE.getSingleBitPort(0), FDE19);
				EdifPortRef FDE19_Q = new EdifPortRef(wire19, FDE19_interface_Q.getSingleBitPort(0), FDE19);
				EdifPortRef FDE20_C = new EdifPortRef(wclk, FDE20_interface_C.getSingleBitPort(0), FDE20);
				EdifPortRef FDE20_D = new EdifPortRef(d1, FDE20_interface_D.getSingleBitPort(0), FDE20);
				EdifPortRef FDE20_CE = new EdifPortRef(e4, FDE20_interface_CE.getSingleBitPort(0), FDE20);
				EdifPortRef FDE20_Q = new EdifPortRef(wire20, FDE20_interface_Q.getSingleBitPort(0), FDE20);
				EdifPortRef FDE21_C = new EdifPortRef(wclk, FDE21_interface_C.getSingleBitPort(0), FDE21);
				EdifPortRef FDE21_D = new EdifPortRef(d1, FDE21_interface_D.getSingleBitPort(0), FDE21);
				EdifPortRef FDE21_CE = new EdifPortRef(e5, FDE21_interface_CE.getSingleBitPort(0), FDE21);
				EdifPortRef FDE21_Q = new EdifPortRef(wire21, FDE21_interface_Q.getSingleBitPort(0), FDE21);
				EdifPortRef FDE22_C = new EdifPortRef(wclk, FDE22_interface_C.getSingleBitPort(0), FDE22);
				EdifPortRef FDE22_D = new EdifPortRef(d1, FDE22_interface_D.getSingleBitPort(0), FDE22);
				EdifPortRef FDE22_CE = new EdifPortRef(e6, FDE22_interface_CE.getSingleBitPort(0), FDE22);
				EdifPortRef FDE22_Q = new EdifPortRef(wire22, FDE22_interface_Q.getSingleBitPort(0), FDE22);
				EdifPortRef FDE23_C = new EdifPortRef(wclk, FDE23_interface_C.getSingleBitPort(0), FDE23);
				EdifPortRef FDE23_D = new EdifPortRef(d1, FDE23_interface_D.getSingleBitPort(0), FDE23);
				EdifPortRef FDE23_CE = new EdifPortRef(e7, FDE23_interface_CE.getSingleBitPort(0), FDE23);
				EdifPortRef FDE23_Q = new EdifPortRef(wire23, FDE23_interface_Q.getSingleBitPort(0), FDE23);
				EdifPortRef FDE24_C = new EdifPortRef(wclk, FDE24_interface_C.getSingleBitPort(0), FDE24);
				EdifPortRef FDE24_D = new EdifPortRef(d1, FDE24_interface_D.getSingleBitPort(0), FDE24);
				EdifPortRef FDE24_CE = new EdifPortRef(e8, FDE24_interface_CE.getSingleBitPort(0), FDE24);
				EdifPortRef FDE24_Q = new EdifPortRef(wire24, FDE24_interface_Q.getSingleBitPort(0), FDE24);
				EdifPortRef FDE25_C = new EdifPortRef(wclk, FDE25_interface_C.getSingleBitPort(0), FDE25);
				EdifPortRef FDE25_D = new EdifPortRef(d1, FDE25_interface_D.getSingleBitPort(0), FDE25);
				EdifPortRef FDE25_CE = new EdifPortRef(e9, FDE25_interface_CE.getSingleBitPort(0), FDE25);
				EdifPortRef FDE25_Q = new EdifPortRef(wire25, FDE25_interface_Q.getSingleBitPort(0), FDE25);
				EdifPortRef FDE26_C = new EdifPortRef(wclk, FDE26_interface_C.getSingleBitPort(0), FDE26);
				EdifPortRef FDE26_D = new EdifPortRef(d1, FDE26_interface_D.getSingleBitPort(0), FDE26);
				EdifPortRef FDE26_CE = new EdifPortRef(e10, FDE26_interface_CE.getSingleBitPort(0), FDE26);
				EdifPortRef FDE26_Q = new EdifPortRef(wire26, FDE26_interface_Q.getSingleBitPort(0), FDE26);
				EdifPortRef FDE27_C = new EdifPortRef(wclk, FDE27_interface_C.getSingleBitPort(0), FDE27);
				EdifPortRef FDE27_D = new EdifPortRef(d1, FDE27_interface_D.getSingleBitPort(0), FDE27);
				EdifPortRef FDE27_CE = new EdifPortRef(e11, FDE27_interface_CE.getSingleBitPort(0), FDE27);
				EdifPortRef FDE27_Q = new EdifPortRef(wire27, FDE27_interface_Q.getSingleBitPort(0), FDE27);
				EdifPortRef FDE28_C = new EdifPortRef(wclk, FDE28_interface_C.getSingleBitPort(0), FDE28);
				EdifPortRef FDE28_D = new EdifPortRef(d1, FDE28_interface_D.getSingleBitPort(0), FDE28);
				EdifPortRef FDE28_CE = new EdifPortRef(e12, FDE28_interface_CE.getSingleBitPort(0), FDE28);
				EdifPortRef FDE28_Q = new EdifPortRef(wire28, FDE28_interface_Q.getSingleBitPort(0), FDE28);
				EdifPortRef FDE29_C = new EdifPortRef(wclk, FDE29_interface_C.getSingleBitPort(0), FDE29);
				EdifPortRef FDE29_D = new EdifPortRef(d1, FDE29_interface_D.getSingleBitPort(0), FDE29);
				EdifPortRef FDE29_CE = new EdifPortRef(e13, FDE29_interface_CE.getSingleBitPort(0), FDE29);
				EdifPortRef FDE29_Q = new EdifPortRef(wire29, FDE29_interface_Q.getSingleBitPort(0), FDE29);
				EdifPortRef FDE30_C = new EdifPortRef(wclk, FDE30_interface_C.getSingleBitPort(0), FDE30);
				EdifPortRef FDE30_D = new EdifPortRef(d1, FDE30_interface_D.getSingleBitPort(0), FDE30);
				EdifPortRef FDE30_CE = new EdifPortRef(e14, FDE30_interface_CE.getSingleBitPort(0), FDE30);
				EdifPortRef FDE30_Q = new EdifPortRef(wire30, FDE30_interface_Q.getSingleBitPort(0), FDE30);
				EdifPortRef FDE31_C = new EdifPortRef(wclk, FDE31_interface_C.getSingleBitPort(0), FDE31);
				EdifPortRef FDE31_D = new EdifPortRef(d1, FDE31_interface_D.getSingleBitPort(0), FDE31);
				EdifPortRef FDE31_CE = new EdifPortRef(e15, FDE31_interface_CE.getSingleBitPort(0), FDE31);
				EdifPortRef FDE31_Q = new EdifPortRef(wire31, FDE31_interface_Q.getSingleBitPort(0), FDE31);
				EdifPortRef FDE32_C = new EdifPortRef(wclk, FDE32_interface_C.getSingleBitPort(0), FDE32);
				EdifPortRef FDE32_D = new EdifPortRef(d1, FDE32_interface_D.getSingleBitPort(0), FDE32);
				EdifPortRef FDE32_CE = new EdifPortRef(e16, FDE32_interface_CE.getSingleBitPort(0), FDE32);
				EdifPortRef FDE32_Q = new EdifPortRef(wire32, FDE32_interface_Q.getSingleBitPort(0), FDE32);
				EdifPortRef LUT2_1_I0 = new EdifPortRef(a3, LUT2_1_interface_I0.getSingleBitPort(0), LUT2_1);
				EdifPortRef LUT2_1_I1 = new EdifPortRef(we, LUT2_1_interface_I1.getSingleBitPort(0), LUT2_1);
				EdifPortRef LUT2_1_O = new EdifPortRef(net29, LUT2_1_interface_O.getSingleBitPort(0), LUT2_1);
				EdifPortRef LUT2_2_I0 = new EdifPortRef(a3, LUT2_2_interface_I0.getSingleBitPort(0), LUT2_2);
				EdifPortRef LUT2_2_I1 = new EdifPortRef(we, LUT2_2_interface_I1.getSingleBitPort(0), LUT2_2);
				EdifPortRef LUT2_2_O = new EdifPortRef(net30, LUT2_2_interface_O.getSingleBitPort(0), LUT2_2);
				EdifPortRef LUT3_1_I0 = new EdifPortRef(a3, LUT3_1_interface_I0.getSingleBitPort(0), LUT3_1);
				EdifPortRef LUT3_1_I1 = new EdifPortRef(wire1, LUT3_1_interface_I1.getSingleBitPort(0), LUT3_1);
				EdifPortRef LUT3_1_I2 = new EdifPortRef(wire9, LUT3_1_interface_I2.getSingleBitPort(0), LUT3_1);
				EdifPortRef LUT3_1_O = new EdifPortRef(net1, LUT3_1_interface_O.getSingleBitPort(0), LUT3_1);
				EdifPortRef LUT3_2_I0 = new EdifPortRef(a3, LUT3_2_interface_I0.getSingleBitPort(0), LUT3_2);
				EdifPortRef LUT3_2_I1 = new EdifPortRef(wire5, LUT3_2_interface_I1.getSingleBitPort(0), LUT3_2);
				EdifPortRef LUT3_2_I2 = new EdifPortRef(wire13, LUT3_2_interface_I2.getSingleBitPort(0), LUT3_2);
				EdifPortRef LUT3_2_O = new EdifPortRef(net2, LUT3_2_interface_O.getSingleBitPort(0), LUT3_2);
				EdifPortRef LUT3_3_I0 = new EdifPortRef(a3, LUT3_3_interface_I0.getSingleBitPort(0), LUT3_3);
				EdifPortRef LUT3_3_I1 = new EdifPortRef(wire3, LUT3_3_interface_I1.getSingleBitPort(0), LUT3_3);
				EdifPortRef LUT3_3_I2 = new EdifPortRef(wire11, LUT3_3_interface_I2.getSingleBitPort(0), LUT3_3);
				EdifPortRef LUT3_3_O = new EdifPortRef(net3, LUT3_3_interface_O.getSingleBitPort(0), LUT3_3);
				EdifPortRef LUT3_4_I0 = new EdifPortRef(a3, LUT3_4_interface_I0.getSingleBitPort(0), LUT3_4);
				EdifPortRef LUT3_4_I1 = new EdifPortRef(wire7, LUT3_4_interface_I1.getSingleBitPort(0), LUT3_4);
				EdifPortRef LUT3_4_I2 = new EdifPortRef(wire15, LUT3_4_interface_I2.getSingleBitPort(0), LUT3_4);
				EdifPortRef LUT3_4_O = new EdifPortRef(net4, LUT3_4_interface_O.getSingleBitPort(0), LUT3_4);
				EdifPortRef LUT3_5_I0 = new EdifPortRef(a3, LUT3_5_interface_I0.getSingleBitPort(0), LUT3_5);
				EdifPortRef LUT3_5_I1 = new EdifPortRef(wire2, LUT3_5_interface_I1.getSingleBitPort(0), LUT3_5);
				EdifPortRef LUT3_5_I2 = new EdifPortRef(wire10, LUT3_5_interface_I2.getSingleBitPort(0), LUT3_5);
				EdifPortRef LUT3_5_O = new EdifPortRef(net7, LUT3_5_interface_O.getSingleBitPort(0), LUT3_5);
				EdifPortRef LUT3_6_I0 = new EdifPortRef(a3, LUT3_6_interface_I0.getSingleBitPort(0), LUT3_6);
				EdifPortRef LUT3_6_I1 = new EdifPortRef(wire6, LUT3_6_interface_I1.getSingleBitPort(0), LUT3_6);
				EdifPortRef LUT3_6_I2 = new EdifPortRef(wire14, LUT3_6_interface_I2.getSingleBitPort(0), LUT3_6);
				EdifPortRef LUT3_6_O = new EdifPortRef(net8, LUT3_6_interface_O.getSingleBitPort(0), LUT3_6);
				EdifPortRef LUT3_7_I0 = new EdifPortRef(a3, LUT3_7_interface_I0.getSingleBitPort(0), LUT3_7);
				EdifPortRef LUT3_7_I1 = new EdifPortRef(wire4, LUT3_7_interface_I1.getSingleBitPort(0), LUT3_7);
				EdifPortRef LUT3_7_I2 = new EdifPortRef(wire12, LUT3_7_interface_I2.getSingleBitPort(0), LUT3_7);
				EdifPortRef LUT3_7_O = new EdifPortRef(net9, LUT3_7_interface_O.getSingleBitPort(0), LUT3_7);
				EdifPortRef LUT3_8_I0 = new EdifPortRef(a3, LUT3_8_interface_I0.getSingleBitPort(0), LUT3_8);
				EdifPortRef LUT3_8_I1 = new EdifPortRef(wire8, LUT3_8_interface_I1.getSingleBitPort(0), LUT3_8);
				EdifPortRef LUT3_8_I2 = new EdifPortRef(wire16, LUT3_8_interface_I2.getSingleBitPort(0), LUT3_8);
				EdifPortRef LUT3_8_O = new EdifPortRef(net10, LUT3_8_interface_O.getSingleBitPort(0), LUT3_8);
				EdifPortRef LUT3_9_I0 = new EdifPortRef(net13, LUT3_9_interface_I0.getSingleBitPort(0), LUT3_9);
				EdifPortRef LUT3_9_I1 = new EdifPortRef(net14, LUT3_9_interface_I1.getSingleBitPort(0), LUT3_9);
				EdifPortRef LUT3_9_I2 = new EdifPortRef(a0, LUT3_9_interface_I2.getSingleBitPort(0), LUT3_9);
				EdifPortRef LUT3_9_O = new EdifPortRef(o0, LUT3_9_interface_O.getSingleBitPort(0), LUT3_9);
				EdifPortRef LUT3_10_I0 = new EdifPortRef(a3, LUT3_10_interface_I0.getSingleBitPort(0), LUT3_10);
				EdifPortRef LUT3_10_I1 = new EdifPortRef(wire17, LUT3_10_interface_I1.getSingleBitPort(0), LUT3_10);
				EdifPortRef LUT3_10_I2 = new EdifPortRef(wire25, LUT3_10_interface_I2.getSingleBitPort(0), LUT3_10);
				EdifPortRef LUT3_10_O = new EdifPortRef(net15, LUT3_10_interface_O.getSingleBitPort(0), LUT3_10);
				EdifPortRef LUT3_11_I0 = new EdifPortRef(a3, LUT3_11_interface_I0.getSingleBitPort(0), LUT3_11);
				EdifPortRef LUT3_11_I1 = new EdifPortRef(wire21, LUT3_11_interface_I1.getSingleBitPort(0), LUT3_11);
				EdifPortRef LUT3_11_I2 = new EdifPortRef(wire29, LUT3_11_interface_I2.getSingleBitPort(0), LUT3_11);
				EdifPortRef LUT3_11_O = new EdifPortRef(net16, LUT3_11_interface_O.getSingleBitPort(0), LUT3_11);
				EdifPortRef LUT3_12_I0 = new EdifPortRef(a3, LUT3_12_interface_I0.getSingleBitPort(0), LUT3_12);
				EdifPortRef LUT3_12_I1 = new EdifPortRef(wire19, LUT3_12_interface_I1.getSingleBitPort(0), LUT3_12);
				EdifPortRef LUT3_12_I2 = new EdifPortRef(wire27, LUT3_12_interface_I2.getSingleBitPort(0), LUT3_12);
				EdifPortRef LUT3_12_O = new EdifPortRef(net17, LUT3_12_interface_O.getSingleBitPort(0), LUT3_12);
				EdifPortRef LUT3_13_I0 = new EdifPortRef(a3, LUT3_13_interface_I0.getSingleBitPort(0), LUT3_13);
				EdifPortRef LUT3_13_I1 = new EdifPortRef(wire23, LUT3_13_interface_I1.getSingleBitPort(0), LUT3_13);
				EdifPortRef LUT3_13_I2 = new EdifPortRef(wire31, LUT3_13_interface_I2.getSingleBitPort(0), LUT3_13);
				EdifPortRef LUT3_13_O = new EdifPortRef(net18, LUT3_13_interface_O.getSingleBitPort(0), LUT3_13);
				EdifPortRef LUT3_14_I0 = new EdifPortRef(a3, LUT3_14_interface_I0.getSingleBitPort(0), LUT3_14);
				EdifPortRef LUT3_14_I1 = new EdifPortRef(wire18, LUT3_14_interface_I1.getSingleBitPort(0), LUT3_14);
				EdifPortRef LUT3_14_I2 = new EdifPortRef(wire26, LUT3_14_interface_I2.getSingleBitPort(0), LUT3_14);
				EdifPortRef LUT3_14_O = new EdifPortRef(net21, LUT3_14_interface_O.getSingleBitPort(0), LUT3_14);
				EdifPortRef LUT3_15_I0 = new EdifPortRef(a3, LUT3_15_interface_I0.getSingleBitPort(0), LUT3_15);
				EdifPortRef LUT3_15_I1 = new EdifPortRef(wire22, LUT3_15_interface_I1.getSingleBitPort(0), LUT3_15);
				EdifPortRef LUT3_15_I2 = new EdifPortRef(wire30, LUT3_15_interface_I2.getSingleBitPort(0), LUT3_15);
				EdifPortRef LUT3_15_O = new EdifPortRef(net22, LUT3_15_interface_O.getSingleBitPort(0), LUT3_15);
				EdifPortRef LUT3_16_I0 = new EdifPortRef(a3, LUT3_16_interface_I0.getSingleBitPort(0), LUT3_16);
				EdifPortRef LUT3_16_I1 = new EdifPortRef(wire20, LUT3_16_interface_I1.getSingleBitPort(0), LUT3_16);
				EdifPortRef LUT3_16_I2 = new EdifPortRef(wire28, LUT3_16_interface_I2.getSingleBitPort(0), LUT3_16);
				EdifPortRef LUT3_16_O = new EdifPortRef(net23, LUT3_16_interface_O.getSingleBitPort(0), LUT3_16);
				EdifPortRef LUT3_17_I0 = new EdifPortRef(a3, LUT3_17_interface_I0.getSingleBitPort(0), LUT3_17);
				EdifPortRef LUT3_17_I1 = new EdifPortRef(wire24, LUT3_17_interface_I1.getSingleBitPort(0), LUT3_17);
				EdifPortRef LUT3_17_I2 = new EdifPortRef(wire32, LUT3_17_interface_I2.getSingleBitPort(0), LUT3_17);
				EdifPortRef LUT3_17_O = new EdifPortRef(net24, LUT3_17_interface_O.getSingleBitPort(0), LUT3_17);
				EdifPortRef LUT3_18_I0 = new EdifPortRef(net27, LUT3_18_interface_I0.getSingleBitPort(0), LUT3_18);
				EdifPortRef LUT3_18_I1 = new EdifPortRef(net28, LUT3_18_interface_I1.getSingleBitPort(0), LUT3_18);
				EdifPortRef LUT3_18_I2 = new EdifPortRef(a0, LUT3_18_interface_I2.getSingleBitPort(0), LUT3_18);
				EdifPortRef LUT3_18_O = new EdifPortRef(o1, LUT3_18_interface_O.getSingleBitPort(0), LUT3_18);
				EdifPortRef LUT4_1_I0 = new EdifPortRef(net29, LUT4_1_interface_I0.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_I1 = new EdifPortRef(a0, LUT4_1_interface_I1.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_I2 = new EdifPortRef(a1, LUT4_1_interface_I2.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_I3 = new EdifPortRef(a2, LUT4_1_interface_I3.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_O = new EdifPortRef(e1, LUT4_1_interface_O.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_2_I0 = new EdifPortRef(net29, LUT4_2_interface_I0.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_I1 = new EdifPortRef(a0, LUT4_2_interface_I1.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_I2 = new EdifPortRef(a1, LUT4_2_interface_I2.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_I3 = new EdifPortRef(a2, LUT4_2_interface_I3.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_O = new EdifPortRef(e2, LUT4_2_interface_O.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_3_I0 = new EdifPortRef(net29, LUT4_3_interface_I0.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_I1 = new EdifPortRef(a0, LUT4_3_interface_I1.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_I2 = new EdifPortRef(a1, LUT4_3_interface_I2.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_I3 = new EdifPortRef(a2, LUT4_3_interface_I3.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_O = new EdifPortRef(e3, LUT4_3_interface_O.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_4_I0 = new EdifPortRef(net29, LUT4_4_interface_I0.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_I1 = new EdifPortRef(a0, LUT4_4_interface_I1.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_I2 = new EdifPortRef(a1, LUT4_4_interface_I2.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_I3 = new EdifPortRef(a2, LUT4_4_interface_I3.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_O = new EdifPortRef(e4, LUT4_4_interface_O.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_5_I0 = new EdifPortRef(net29, LUT4_5_interface_I0.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_I1 = new EdifPortRef(a0, LUT4_5_interface_I1.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_I2 = new EdifPortRef(a1, LUT4_5_interface_I2.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_I3 = new EdifPortRef(a2, LUT4_5_interface_I3.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_O = new EdifPortRef(e5, LUT4_5_interface_O.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_6_I0 = new EdifPortRef(net29, LUT4_6_interface_I0.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_I1 = new EdifPortRef(a0, LUT4_6_interface_I1.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_I2 = new EdifPortRef(a1, LUT4_6_interface_I2.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_I3 = new EdifPortRef(a2, LUT4_6_interface_I3.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_O = new EdifPortRef(e6, LUT4_6_interface_O.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_7_I0 = new EdifPortRef(net29, LUT4_7_interface_I0.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_I1 = new EdifPortRef(a0, LUT4_7_interface_I1.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_I2 = new EdifPortRef(a1, LUT4_7_interface_I2.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_I3 = new EdifPortRef(a2, LUT4_7_interface_I3.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_O = new EdifPortRef(e7, LUT4_7_interface_O.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_8_I0 = new EdifPortRef(net29, LUT4_8_interface_I0.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_I1 = new EdifPortRef(a0, LUT4_8_interface_I1.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_I2 = new EdifPortRef(a1, LUT4_8_interface_I2.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_I3 = new EdifPortRef(a2, LUT4_8_interface_I3.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_O = new EdifPortRef(e8, LUT4_8_interface_O.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_9_I0 = new EdifPortRef(net30, LUT4_9_interface_I0.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_I1 = new EdifPortRef(a0, LUT4_9_interface_I1.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_I2 = new EdifPortRef(a1, LUT4_9_interface_I2.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_I3 = new EdifPortRef(a2, LUT4_9_interface_I3.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_O = new EdifPortRef(e9, LUT4_9_interface_O.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_10_I0 = new EdifPortRef(net30, LUT4_10_interface_I0.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_I1 = new EdifPortRef(a0, LUT4_10_interface_I1.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_I2 = new EdifPortRef(a1, LUT4_10_interface_I2.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_I3 = new EdifPortRef(a2, LUT4_10_interface_I3.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_O = new EdifPortRef(e10, LUT4_10_interface_O.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_11_I0 = new EdifPortRef(net30, LUT4_11_interface_I0.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_I1 = new EdifPortRef(a0, LUT4_11_interface_I1.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_I2 = new EdifPortRef(a1, LUT4_11_interface_I2.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_I3 = new EdifPortRef(a2, LUT4_11_interface_I3.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_O = new EdifPortRef(e11, LUT4_11_interface_O.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_12_I0 = new EdifPortRef(net30, LUT4_12_interface_I0.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_I1 = new EdifPortRef(a0, LUT4_12_interface_I1.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_I2 = new EdifPortRef(a1, LUT4_12_interface_I2.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_I3 = new EdifPortRef(a2, LUT4_12_interface_I3.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_O = new EdifPortRef(e12, LUT4_12_interface_O.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_13_I0 = new EdifPortRef(net30, LUT4_13_interface_I0.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_I1 = new EdifPortRef(a0, LUT4_13_interface_I1.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_I2 = new EdifPortRef(a1, LUT4_13_interface_I2.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_I3 = new EdifPortRef(a2, LUT4_13_interface_I3.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_O = new EdifPortRef(e13, LUT4_13_interface_O.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_14_I0 = new EdifPortRef(net30, LUT4_14_interface_I0.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_I1 = new EdifPortRef(a0, LUT4_14_interface_I1.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_I2 = new EdifPortRef(a1, LUT4_14_interface_I2.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_I3 = new EdifPortRef(a2, LUT4_14_interface_I3.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_O = new EdifPortRef(e14, LUT4_14_interface_O.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_15_I0 = new EdifPortRef(net30, LUT4_15_interface_I0.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_I1 = new EdifPortRef(a0, LUT4_15_interface_I1.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_I2 = new EdifPortRef(a1, LUT4_15_interface_I2.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_I3 = new EdifPortRef(a2, LUT4_15_interface_I3.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_O = new EdifPortRef(e15, LUT4_15_interface_O.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_16_I0 = new EdifPortRef(net30, LUT4_16_interface_I0.getSingleBitPort(0), LUT4_16);
				EdifPortRef LUT4_16_I1 = new EdifPortRef(a0, LUT4_16_interface_I1.getSingleBitPort(0), LUT4_16);
				EdifPortRef LUT4_16_I2 = new EdifPortRef(a1, LUT4_16_interface_I2.getSingleBitPort(0), LUT4_16);
				EdifPortRef LUT4_16_I3 = new EdifPortRef(a2, LUT4_16_interface_I3.getSingleBitPort(0), LUT4_16);
				EdifPortRef LUT4_16_O = new EdifPortRef(e16, LUT4_16_interface_O.getSingleBitPort(0), LUT4_16);
				EdifPortRef MUXF5_1_I0 = new EdifPortRef(net1, MUXF5_1_interface_I0.getSingleBitPort(0), MUXF5_1);
				EdifPortRef MUXF5_1_I1 = new EdifPortRef(net2, MUXF5_1_interface_I1.getSingleBitPort(0), MUXF5_1);
				EdifPortRef MUXF5_1_S = new EdifPortRef(a2, MUXF5_1_interface_S.getSingleBitPort(0), MUXF5_1);
				EdifPortRef MUXF5_1_O = new EdifPortRef(net5, MUXF5_1_interface_O.getSingleBitPort(0), MUXF5_1);
				EdifPortRef MUXF5_2_I0 = new EdifPortRef(net3, MUXF5_2_interface_I0.getSingleBitPort(0), MUXF5_2);
				EdifPortRef MUXF5_2_I1 = new EdifPortRef(net4, MUXF5_2_interface_I1.getSingleBitPort(0), MUXF5_2);
				EdifPortRef MUXF5_2_S = new EdifPortRef(a2, MUXF5_2_interface_S.getSingleBitPort(0), MUXF5_2);
				EdifPortRef MUXF5_2_O = new EdifPortRef(net6, MUXF5_2_interface_O.getSingleBitPort(0), MUXF5_2);
				EdifPortRef MUXF5_3_I0 = new EdifPortRef(net7, MUXF5_3_interface_I0.getSingleBitPort(0), MUXF5_3);
				EdifPortRef MUXF5_3_I1 = new EdifPortRef(net8, MUXF5_3_interface_I1.getSingleBitPort(0), MUXF5_3);
				EdifPortRef MUXF5_3_S = new EdifPortRef(a2, MUXF5_3_interface_S.getSingleBitPort(0), MUXF5_3);
				EdifPortRef MUXF5_3_O = new EdifPortRef(net11, MUXF5_3_interface_O.getSingleBitPort(0), MUXF5_3);
				EdifPortRef MUXF5_4_I0 = new EdifPortRef(net9, MUXF5_4_interface_I0.getSingleBitPort(0), MUXF5_4);
				EdifPortRef MUXF5_4_I1 = new EdifPortRef(net10, MUXF5_4_interface_I1.getSingleBitPort(0), MUXF5_4);
				EdifPortRef MUXF5_4_S = new EdifPortRef(a2, MUXF5_4_interface_S.getSingleBitPort(0), MUXF5_4);
				EdifPortRef MUXF5_4_O = new EdifPortRef(net12, MUXF5_4_interface_O.getSingleBitPort(0), MUXF5_4);
				EdifPortRef MUXF5_5_I0 = new EdifPortRef(net15, MUXF5_5_interface_I0.getSingleBitPort(0), MUXF5_5);
				EdifPortRef MUXF5_5_I1 = new EdifPortRef(net16, MUXF5_5_interface_I1.getSingleBitPort(0), MUXF5_5);
				EdifPortRef MUXF5_5_S = new EdifPortRef(a2, MUXF5_5_interface_S.getSingleBitPort(0), MUXF5_5);
				EdifPortRef MUXF5_5_O = new EdifPortRef(net19, MUXF5_5_interface_O.getSingleBitPort(0), MUXF5_5);
				EdifPortRef MUXF5_6_I0 = new EdifPortRef(net17, MUXF5_6_interface_I0.getSingleBitPort(0), MUXF5_6);
				EdifPortRef MUXF5_6_I1 = new EdifPortRef(net18, MUXF5_6_interface_I1.getSingleBitPort(0), MUXF5_6);
				EdifPortRef MUXF5_6_S = new EdifPortRef(a2, MUXF5_6_interface_S.getSingleBitPort(0), MUXF5_6);
				EdifPortRef MUXF5_6_O = new EdifPortRef(net20, MUXF5_6_interface_O.getSingleBitPort(0), MUXF5_6);
				EdifPortRef MUXF5_7_I0 = new EdifPortRef(net21, MUXF5_7_interface_I0.getSingleBitPort(0), MUXF5_7);
				EdifPortRef MUXF5_7_I1 = new EdifPortRef(net22, MUXF5_7_interface_I1.getSingleBitPort(0), MUXF5_7);
				EdifPortRef MUXF5_7_S = new EdifPortRef(a2, MUXF5_7_interface_S.getSingleBitPort(0), MUXF5_7);
				EdifPortRef MUXF5_7_O = new EdifPortRef(net25, MUXF5_7_interface_O.getSingleBitPort(0), MUXF5_7);
				EdifPortRef MUXF5_8_I0 = new EdifPortRef(net23, MUXF5_8_interface_I0.getSingleBitPort(0), MUXF5_8);
				EdifPortRef MUXF5_8_I1 = new EdifPortRef(net24, MUXF5_8_interface_I1.getSingleBitPort(0), MUXF5_8);
				EdifPortRef MUXF5_8_S = new EdifPortRef(a2, MUXF5_8_interface_S.getSingleBitPort(0), MUXF5_8);
				EdifPortRef MUXF5_8_O = new EdifPortRef(net26, MUXF5_8_interface_O.getSingleBitPort(0), MUXF5_8);
				EdifPortRef MUXF6_1_I0 = new EdifPortRef(net5, MUXF6_1_interface_I0.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_I1 = new EdifPortRef(net6, MUXF6_1_interface_I1.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_S = new EdifPortRef(a1, MUXF6_1_interface_S.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_O = new EdifPortRef(net13, MUXF6_1_interface_O.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_2_I0 = new EdifPortRef(net11, MUXF6_2_interface_I0.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_I1 = new EdifPortRef(net12, MUXF6_2_interface_I1.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_S = new EdifPortRef(a1, MUXF6_2_interface_S.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_O = new EdifPortRef(net14, MUXF6_2_interface_O.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_3_I0 = new EdifPortRef(net19, MUXF6_3_interface_I0.getSingleBitPort(0), MUXF6_3);
				EdifPortRef MUXF6_3_I1 = new EdifPortRef(net20, MUXF6_3_interface_I1.getSingleBitPort(0), MUXF6_3);
				EdifPortRef MUXF6_3_S = new EdifPortRef(a1, MUXF6_3_interface_S.getSingleBitPort(0), MUXF6_3);
				EdifPortRef MUXF6_3_O = new EdifPortRef(net27, MUXF6_3_interface_O.getSingleBitPort(0), MUXF6_3);
				EdifPortRef MUXF6_4_I0 = new EdifPortRef(net25, MUXF6_4_interface_I0.getSingleBitPort(0), MUXF6_4);
				EdifPortRef MUXF6_4_I1 = new EdifPortRef(net26, MUXF6_4_interface_I1.getSingleBitPort(0), MUXF6_4);
				EdifPortRef MUXF6_4_S = new EdifPortRef(a1, MUXF6_4_interface_S.getSingleBitPort(0), MUXF6_4);
				EdifPortRef MUXF6_4_O = new EdifPortRef(net28, MUXF6_4_interface_O.getSingleBitPort(0), MUXF6_4);
				//we
				we.addPortConnection(LUT2_1_I1);
				we.addPortConnection(LUT2_2_I1);
				//d0
				d0.addPortConnection(FDE1_D);
				d0.addPortConnection(FDE2_D);
				d0.addPortConnection(FDE3_D);
				d0.addPortConnection(FDE4_D);
				d0.addPortConnection(FDE5_D);
				d0.addPortConnection(FDE6_D);
				d0.addPortConnection(FDE7_D);
				d0.addPortConnection(FDE8_D);
				d0.addPortConnection(FDE9_D);
				d0.addPortConnection(FDE10_D);
				d0.addPortConnection(FDE11_D);
				d0.addPortConnection(FDE12_D);
				d0.addPortConnection(FDE13_D);
				d0.addPortConnection(FDE14_D);
				d0.addPortConnection(FDE15_D);
				d0.addPortConnection(FDE16_D);
				//d1
				d1.addPortConnection(FDE17_D);
				d1.addPortConnection(FDE18_D);
				d1.addPortConnection(FDE19_D);
				d1.addPortConnection(FDE20_D);
				d1.addPortConnection(FDE21_D);
				d1.addPortConnection(FDE22_D);
				d1.addPortConnection(FDE23_D);
				d1.addPortConnection(FDE24_D);
				d1.addPortConnection(FDE25_D);
				d1.addPortConnection(FDE26_D);
				d1.addPortConnection(FDE27_D);
				d1.addPortConnection(FDE28_D);
				d1.addPortConnection(FDE29_D);
				d1.addPortConnection(FDE30_D);
				d1.addPortConnection(FDE31_D);
				d1.addPortConnection(FDE32_D);
				//wclk
				wclk.addPortConnection(FDE1_C);
				wclk.addPortConnection(FDE2_C);
				wclk.addPortConnection(FDE3_C);
				wclk.addPortConnection(FDE4_C);
				wclk.addPortConnection(FDE5_C);
				wclk.addPortConnection(FDE6_C);
				wclk.addPortConnection(FDE7_C);
				wclk.addPortConnection(FDE8_C);
				wclk.addPortConnection(FDE9_C);
				wclk.addPortConnection(FDE10_C);
				wclk.addPortConnection(FDE11_C);
				wclk.addPortConnection(FDE12_C);
				wclk.addPortConnection(FDE13_C);
				wclk.addPortConnection(FDE14_C);
				wclk.addPortConnection(FDE15_C);
				wclk.addPortConnection(FDE16_C);
				wclk.addPortConnection(FDE17_C);
				wclk.addPortConnection(FDE18_C);
				wclk.addPortConnection(FDE19_C);
				wclk.addPortConnection(FDE20_C);
				wclk.addPortConnection(FDE21_C);
				wclk.addPortConnection(FDE22_C);
				wclk.addPortConnection(FDE23_C);
				wclk.addPortConnection(FDE24_C);
				wclk.addPortConnection(FDE25_C);
				wclk.addPortConnection(FDE26_C);
				wclk.addPortConnection(FDE27_C);
				wclk.addPortConnection(FDE28_C);
				wclk.addPortConnection(FDE29_C);
				wclk.addPortConnection(FDE30_C);
				wclk.addPortConnection(FDE31_C);
				wclk.addPortConnection(FDE32_C);
				//a0
				a0.addPortConnection(LUT4_1_I1);
				a0.addPortConnection(LUT4_2_I1);
				a0.addPortConnection(LUT4_3_I1);
				a0.addPortConnection(LUT4_4_I1);
				a0.addPortConnection(LUT4_5_I1);
				a0.addPortConnection(LUT4_6_I1);
				a0.addPortConnection(LUT4_7_I1);
				a0.addPortConnection(LUT4_8_I1);
				a0.addPortConnection(LUT4_9_I1);
				a0.addPortConnection(LUT4_10_I1);
				a0.addPortConnection(LUT4_11_I1);
				a0.addPortConnection(LUT4_12_I1);
				a0.addPortConnection(LUT4_13_I1);
				a0.addPortConnection(LUT4_14_I1);
				a0.addPortConnection(LUT4_15_I1);
				a0.addPortConnection(LUT4_16_I1);
				a0.addPortConnection(LUT3_9_I2);
				a0.addPortConnection(LUT3_18_I2);
				//a1
				a1.addPortConnection(LUT4_1_I2);
				a1.addPortConnection(LUT4_2_I2);
				a1.addPortConnection(LUT4_3_I2);
				a1.addPortConnection(LUT4_4_I2);
				a1.addPortConnection(LUT4_5_I2);
				a1.addPortConnection(LUT4_6_I2);
				a1.addPortConnection(LUT4_7_I2);
				a1.addPortConnection(LUT4_8_I2);
				a1.addPortConnection(LUT4_9_I2);
				a1.addPortConnection(LUT4_10_I2);
				a1.addPortConnection(LUT4_11_I2);
				a1.addPortConnection(LUT4_12_I2);
				a1.addPortConnection(LUT4_13_I2);
				a1.addPortConnection(LUT4_14_I2);
				a1.addPortConnection(LUT4_15_I2);
				a1.addPortConnection(LUT4_16_I2);
				a1.addPortConnection(MUXF6_1_S);
				a1.addPortConnection(MUXF6_2_S);
				a1.addPortConnection(MUXF6_3_S);
				a1.addPortConnection(MUXF6_4_S);
				//a2
				a2.addPortConnection(LUT4_1_I3);
				a2.addPortConnection(LUT4_2_I3);
				a2.addPortConnection(LUT4_3_I3);
				a2.addPortConnection(LUT4_4_I3);
				a2.addPortConnection(LUT4_5_I3);
				a2.addPortConnection(LUT4_6_I3);
				a2.addPortConnection(LUT4_7_I3);
				a2.addPortConnection(LUT4_8_I3);
				a2.addPortConnection(LUT4_9_I3);
				a2.addPortConnection(LUT4_10_I3);
				a2.addPortConnection(LUT4_11_I3);
				a2.addPortConnection(LUT4_12_I3);
				a2.addPortConnection(LUT4_13_I3);
				a2.addPortConnection(LUT4_14_I3);
				a2.addPortConnection(LUT4_15_I3);
				a2.addPortConnection(LUT4_16_I3);
				a2.addPortConnection(MUXF5_1_S);
				a2.addPortConnection(MUXF5_2_S);
				a2.addPortConnection(MUXF5_3_S);
				a2.addPortConnection(MUXF5_4_S);
				a2.addPortConnection(MUXF5_5_S);
				a2.addPortConnection(MUXF5_6_S);
				a2.addPortConnection(MUXF5_7_S);
				a2.addPortConnection(MUXF5_8_S);
				//a3
				a3.addPortConnection(LUT2_1_I0);
				a3.addPortConnection(LUT2_2_I0);
				a3.addPortConnection(LUT3_1_I0);
				a3.addPortConnection(LUT3_2_I0);
				a3.addPortConnection(LUT3_3_I0);
				a3.addPortConnection(LUT3_4_I0);
				a3.addPortConnection(LUT3_5_I0);
				a3.addPortConnection(LUT3_6_I0);
				a3.addPortConnection(LUT3_7_I0);
				a3.addPortConnection(LUT3_8_I0);
				a3.addPortConnection(LUT3_10_I0);
				a3.addPortConnection(LUT3_11_I0);
				a3.addPortConnection(LUT3_12_I0);
				a3.addPortConnection(LUT3_13_I0);
				a3.addPortConnection(LUT3_14_I0);
				a3.addPortConnection(LUT3_15_I0);
				a3.addPortConnection(LUT3_16_I0);
				a3.addPortConnection(LUT3_17_I0);
				//o0
				o0.addPortConnection(LUT3_9_O);
				//o1
				o1.addPortConnection(LUT3_18_O);
				//wire1
				wire1.addPortConnection(LUT3_1_I1);
				wire1.addPortConnection(FDE1_Q);
				//wire2
				wire2.addPortConnection(LUT3_5_I1);
				wire2.addPortConnection(FDE2_Q);
				//wire3
				wire3.addPortConnection(LUT3_3_I1);
				wire3.addPortConnection(FDE3_Q);
				//wire4
				wire4.addPortConnection(LUT3_7_I1);
				wire4.addPortConnection(FDE4_Q);
				//wire5
				wire5.addPortConnection(LUT3_2_I1);
				wire5.addPortConnection(FDE5_Q);
				//wire6
				wire6.addPortConnection(LUT3_6_I1);
				wire6.addPortConnection(FDE6_Q);
				//wire7
				wire7.addPortConnection(LUT3_4_I1);
				wire7.addPortConnection(FDE7_Q);
				//wire8
				wire8.addPortConnection(LUT3_8_I1);
				wire8.addPortConnection(FDE8_Q);
				//wire9
				wire9.addPortConnection(LUT3_1_I2);
				wire9.addPortConnection(FDE9_Q);
				//wire10
				wire10.addPortConnection(LUT3_5_I2);
				wire10.addPortConnection(FDE10_Q);
				//wire11
				wire11.addPortConnection(LUT3_3_I2);
				wire11.addPortConnection(FDE11_Q);
				//wire12
				wire12.addPortConnection(LUT3_7_I2);
				wire12.addPortConnection(FDE12_Q);
				//wire13
				wire13.addPortConnection(LUT3_2_I2);
				wire13.addPortConnection(FDE13_Q);
				//wire14
				wire14.addPortConnection(LUT3_6_I2);
				wire14.addPortConnection(FDE14_Q);
				//wire15
				wire15.addPortConnection(LUT3_4_I2);
				wire15.addPortConnection(FDE15_Q);
				//wire16
				wire16.addPortConnection(LUT3_8_I2);
				wire16.addPortConnection(FDE16_Q);
				//wire17
				wire17.addPortConnection(LUT3_10_I1);
				wire17.addPortConnection(FDE17_Q);
				//wire18
				wire18.addPortConnection(LUT3_14_I1);
				wire18.addPortConnection(FDE18_Q);
				//wire19
				wire19.addPortConnection(LUT3_12_I1);
				wire19.addPortConnection(FDE19_Q);
				//wire20
				wire20.addPortConnection(LUT3_16_I1);
				wire20.addPortConnection(FDE20_Q);
				//wire21
				wire21.addPortConnection(LUT3_11_I1);
				wire21.addPortConnection(FDE21_Q);
				//wire22
				wire22.addPortConnection(LUT3_15_I1);
				wire22.addPortConnection(FDE22_Q);
				//wire23
				wire23.addPortConnection(LUT3_13_I1);
				wire23.addPortConnection(FDE23_Q);
				//wire24
				wire24.addPortConnection(LUT3_17_I1);
				wire24.addPortConnection(FDE24_Q);
				//wire25
				wire25.addPortConnection(LUT3_10_I2);
				wire25.addPortConnection(FDE25_Q);
				//wire26
				wire26.addPortConnection(LUT3_14_I2);
				wire26.addPortConnection(FDE26_Q);
				//wire27
				wire27.addPortConnection(LUT3_12_I2);
				wire27.addPortConnection(FDE27_Q);
				//wire28
				wire28.addPortConnection(LUT3_16_I2);
				wire28.addPortConnection(FDE28_Q);
				//wire29
				wire29.addPortConnection(LUT3_11_I2);
				wire29.addPortConnection(FDE29_Q);
				//wire30
				wire30.addPortConnection(LUT3_15_I2);
				wire30.addPortConnection(FDE30_Q);
				//wire31
				wire31.addPortConnection(LUT3_13_I2);
				wire31.addPortConnection(FDE31_Q);
				//wire32
				wire32.addPortConnection(LUT3_17_I2);
				wire32.addPortConnection(FDE32_Q);
				//net1
				net1.addPortConnection(LUT3_1_O);
				net1.addPortConnection(MUXF5_1_I0);
				//net2
				net2.addPortConnection(LUT3_2_O);
				net2.addPortConnection(MUXF5_1_I1);
				//net3
				net3.addPortConnection(LUT3_3_O);
				net3.addPortConnection(MUXF5_2_I0);
				//net4
				net4.addPortConnection(LUT3_4_O);
				net4.addPortConnection(MUXF5_2_I1);
				//net5
				net5.addPortConnection(MUXF5_1_O);
				net5.addPortConnection(MUXF6_1_I0);
				//net6
				net6.addPortConnection(MUXF5_2_O);
				net6.addPortConnection(MUXF6_1_I1);
				//net7
				net7.addPortConnection(LUT3_5_O);
				net7.addPortConnection(MUXF5_3_I0);
				//net8
				net8.addPortConnection(LUT3_6_O);
				net8.addPortConnection(MUXF5_3_I1);
				//net9
				net9.addPortConnection(LUT3_7_O);
				net9.addPortConnection(MUXF5_4_I0);
				//net10
				net10.addPortConnection(LUT3_8_O);
				net10.addPortConnection(MUXF5_4_I1);
				//net11
				net11.addPortConnection(MUXF5_3_O);
				net11.addPortConnection(MUXF6_2_I0);
				//net12
				net12.addPortConnection(MUXF5_4_O);
				net12.addPortConnection(MUXF6_2_I1);
				//net13
				net13.addPortConnection(MUXF6_1_O);
				net13.addPortConnection(LUT3_9_I0);
				//net14
				net14.addPortConnection(MUXF6_2_O);
				net14.addPortConnection(LUT3_9_I1);
				//net15
				net15.addPortConnection(LUT3_10_O);
				net15.addPortConnection(MUXF5_5_I0);
				//net16
				net16.addPortConnection(LUT3_11_O);
				net16.addPortConnection(MUXF5_5_I1);
				//net17
				net17.addPortConnection(LUT3_12_O);
				net17.addPortConnection(MUXF5_6_I0);
				//net18
				net18.addPortConnection(LUT3_13_O);
				net18.addPortConnection(MUXF5_6_I1);
				//net19
				net19.addPortConnection(MUXF5_5_O);
				net19.addPortConnection(MUXF6_3_I0);
				//net20
				net20.addPortConnection(MUXF5_6_O);
				net20.addPortConnection(MUXF6_3_I1);
				//net21
				net21.addPortConnection(LUT3_14_O);
				net21.addPortConnection(MUXF5_7_I0);
				//net22
				net22.addPortConnection(LUT3_15_O);
				net22.addPortConnection(MUXF5_7_I1);
				//net23
				net23.addPortConnection(LUT3_16_O);
				net23.addPortConnection(MUXF5_8_I0);
				//net24
				net24.addPortConnection(LUT3_17_O);
				net24.addPortConnection(MUXF5_8_I1);
				//net25
				net25.addPortConnection(MUXF5_7_O);
				net25.addPortConnection(MUXF6_4_I0);
				//net26
				net26.addPortConnection(MUXF5_8_O);
				net26.addPortConnection(MUXF6_4_I1);
				//net27
				net27.addPortConnection(MUXF6_3_O);
				net27.addPortConnection(LUT3_18_I0);
				//net28
				net28.addPortConnection(MUXF6_4_O);
				net28.addPortConnection(LUT3_18_I1);
				//net29
				net29.addPortConnection(LUT2_1_O);
				net29.addPortConnection(LUT4_1_I0);
				net29.addPortConnection(LUT4_2_I0);
				net29.addPortConnection(LUT4_3_I0);
				net29.addPortConnection(LUT4_4_I0);
				net29.addPortConnection(LUT4_5_I0);
				net29.addPortConnection(LUT4_6_I0);
				net29.addPortConnection(LUT4_7_I0);
				net29.addPortConnection(LUT4_8_I0);
				//net30
				net30.addPortConnection(LUT2_2_O);
				net30.addPortConnection(LUT4_9_I0);
				net30.addPortConnection(LUT4_10_I0);
				net30.addPortConnection(LUT4_11_I0);
				net30.addPortConnection(LUT4_12_I0);
				net30.addPortConnection(LUT4_13_I0);
				net30.addPortConnection(LUT4_14_I0);
				net30.addPortConnection(LUT4_15_I0);
				net30.addPortConnection(LUT4_16_I0);
				//e1
				e1.addPortConnection(LUT4_1_O);
				e1.addPortConnection(FDE1_CE);
				e1.addPortConnection(FDE17_CE);
				//e2
				e2.addPortConnection(LUT4_2_O);
				e2.addPortConnection(FDE2_CE);
				e2.addPortConnection(FDE18_CE);
				//e3
				e3.addPortConnection(LUT4_3_O);
				e3.addPortConnection(FDE3_CE);
				e3.addPortConnection(FDE19_CE);
				//e4
				e4.addPortConnection(LUT4_4_O);
				e4.addPortConnection(FDE4_CE);
				e4.addPortConnection(FDE20_CE);
				//e5
				e5.addPortConnection(LUT4_5_O);
				e5.addPortConnection(FDE5_CE);
				e5.addPortConnection(FDE21_CE);
				//e6
				e6.addPortConnection(LUT4_6_O);
				e6.addPortConnection(FDE6_CE);
				e6.addPortConnection(FDE22_CE);
				//e7
				e7.addPortConnection(LUT4_7_O);
				e7.addPortConnection(FDE7_CE);
				e7.addPortConnection(FDE23_CE);
				//e8
				e8.addPortConnection(LUT4_8_O);
				e8.addPortConnection(FDE8_CE);
				e8.addPortConnection(FDE24_CE);
				//e9
				e9.addPortConnection(LUT4_9_O);
				e9.addPortConnection(FDE9_CE);
				e9.addPortConnection(FDE25_CE);
				//e10
				e10.addPortConnection(LUT4_10_O);
				e10.addPortConnection(FDE10_CE);
				e10.addPortConnection(FDE26_CE);
				//e11
				e11.addPortConnection(LUT4_11_O);
				e11.addPortConnection(FDE11_CE);
				e11.addPortConnection(FDE27_CE);
				//e12
				e12.addPortConnection(LUT4_12_O);
				e12.addPortConnection(FDE12_CE);
				e12.addPortConnection(FDE28_CE);
				//e13
				e13.addPortConnection(LUT4_13_O);
				e13.addPortConnection(FDE13_CE);
				e13.addPortConnection(FDE29_CE);
				//e14
				e14.addPortConnection(LUT4_14_O);
				e14.addPortConnection(FDE14_CE);
				e14.addPortConnection(FDE30_CE);
				//e15
				e15.addPortConnection(LUT4_15_O);
				e15.addPortConnection(FDE15_CE);
				e15.addPortConnection(FDE31_CE);
				//e16
				e16.addPortConnection(LUT4_16_O);
				e16.addPortConnection(FDE16_CE);
				e16.addPortConnection(FDE32_CE);
				
				/******Set INIT Property******/
				boolean isInit;
				int initCount_00 = 0;
				int initCount_01 = 0;
		        StringTypedValue valueZero = new StringTypedValue("0");
		        StringTypedValue valueOne = new StringTypedValue("1");
		        StringTypedValue valueE4 = new StringTypedValue("E4");
		        StringTypedValue valueCA = new StringTypedValue("CA");
		        StringTypedValue value4 = new StringTypedValue("4");
		        StringTypedValue value8 = new StringTypedValue("8");
		        StringTypedValue value0002 = new StringTypedValue("0002");
		        StringTypedValue value0008 = new StringTypedValue("0008");
		        StringTypedValue value0020 = new StringTypedValue("0020");
		        StringTypedValue value0080 = new StringTypedValue("0080");
		        StringTypedValue value0200 = new StringTypedValue("0200");
		        StringTypedValue value0800 = new StringTypedValue("0800");
		        StringTypedValue value2000 = new StringTypedValue("2000");
		        StringTypedValue value8000 = new StringTypedValue("8000");
		        //Set INIT Property for FDE1
		        isInit = false;
				PropertyList FDE1_propertylist = FDE1.getPropertyList();
				if (FDE1_propertylist != null) {
                    for (Property FDE1_property : FDE1_propertylist.values()) {
                        if (FDE1_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE1_property.setValue(valueOne);
                            else
                                FDE1_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE1.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE1.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE2
                isInit = false;
				PropertyList FDE2_propertylist = FDE2.getPropertyList();
				if (FDE2_propertylist != null) {
                    for (Property FDE2_property : FDE2_propertylist.values()) {
                        if (FDE2_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE2_property.setValue(valueOne);
                            else
                                FDE2_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE2.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE2.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE3
                isInit = false;
				PropertyList FDE3_propertylist = FDE3.getPropertyList();
				if (FDE3_propertylist != null) {
                    for (Property FDE3_property : FDE3_propertylist.values()) {
                        if (FDE3_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE3_property.setValue(valueOne);
                            else
                                FDE3_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE3.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE3.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE4
                isInit = false;
				PropertyList FDE4_propertylist = FDE4.getPropertyList();
				if (FDE4_propertylist != null) {
                    for (Property FDE4_property : FDE4_propertylist.values()) {
                        if (FDE4_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE4_property.setValue(valueOne);
                            else
                                FDE4_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE4.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE4.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE5
                isInit = false;
				PropertyList FDE5_propertylist = FDE5.getPropertyList();
				if (FDE5_propertylist != null) {
                    for (Property FDE5_property : FDE5_propertylist.values()) {
                        if (FDE5_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE5_property.setValue(valueOne);
                            else
                                FDE5_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE5.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE5.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE6
                isInit = false;
				PropertyList FDE6_propertylist = FDE6.getPropertyList();
				if (FDE6_propertylist != null) {
                    for (Property FDE6_property : FDE6_propertylist.values()) {
                        if (FDE6_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE6_property.setValue(valueOne);
                            else
                                FDE6_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE6.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE6.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE7
                isInit = false;
				PropertyList FDE7_propertylist = FDE7.getPropertyList();
				if (FDE7_propertylist != null) {
                    for (Property FDE7_property : FDE7_propertylist.values()) {
                        if (FDE7_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE7_property.setValue(valueOne);
                            else
                                FDE7_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE7.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE7.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE8
                isInit = false;
				PropertyList FDE8_propertylist = FDE8.getPropertyList();
				if (FDE8_propertylist != null) {
                    for (Property FDE8_property : FDE8_propertylist.values()) {
                        if (FDE8_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE8_property.setValue(valueOne);
                            else
                                FDE8_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE8.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE8.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE9
                isInit = false;
				PropertyList FDE9_propertylist = FDE9.getPropertyList();
				if (FDE9_propertylist != null) {
                    for (Property FDE9_property : FDE9_propertylist.values()) {
                        if (FDE9_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE9_property.setValue(valueOne);
                            else
                                FDE9_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE9.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE9.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE10
                isInit = false;
				PropertyList FDE10_propertylist = FDE10.getPropertyList();
				if (FDE10_propertylist != null) {
                    for (Property FDE10_property : FDE10_propertylist.values()) {
                        if (FDE10_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE10_property.setValue(valueOne);
                            else
                                FDE10_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE10.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE10.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE11
                isInit = false;
				PropertyList FDE11_propertylist = FDE11.getPropertyList();
				if (FDE11_propertylist != null) {
                    for (Property FDE11_property : FDE11_propertylist.values()) {
                        if (FDE11_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE11_property.setValue(valueOne);
                            else
                                FDE11_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE11.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE11.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE12
                isInit = false;
				PropertyList FDE12_propertylist = FDE12.getPropertyList();
				if (FDE12_propertylist != null) {
                    for (Property FDE12_property : FDE12_propertylist.values()) {
                        if (FDE12_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE12_property.setValue(valueOne);
                            else
                                FDE12_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE12.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE12.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE13
                isInit = false;
				PropertyList FDE13_propertylist = FDE13.getPropertyList();
				if (FDE13_propertylist != null) {
                    for (Property FDE13_property : FDE13_propertylist.values()) {
                        if (FDE13_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE13_property.setValue(valueOne);
                            else
                                FDE13_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE13.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE13.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE14
                isInit = false;
				PropertyList FDE14_propertylist = FDE14.getPropertyList();
				if (FDE14_propertylist != null) {
                    for (Property FDE14_property : FDE14_propertylist.values()) {
                        if (FDE14_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE14_property.setValue(valueOne);
                            else
                                FDE14_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE14.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE14.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE15
                isInit = false;
				PropertyList FDE15_propertylist = FDE15.getPropertyList();
				if (FDE15_propertylist != null) {
                    for (Property FDE15_property : FDE15_propertylist.values()) {
                        if (FDE15_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE15_property.setValue(valueOne);
                            else
                                FDE15_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE15.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE15.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE16
                isInit = false;
				PropertyList FDE16_propertylist = FDE16.getPropertyList();
				if (FDE16_propertylist != null) {
                    for (Property FDE16_property : FDE16_propertylist.values()) {
                        if (FDE16_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_00 >> initCount_00++) & 1) == 1)
                                FDE16_property.setValue(valueOne);
                            else
                                FDE16_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_00 >> initCount_00++) & 1) == 1)
                        FDE16.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE16.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE17
		        isInit = false;
				PropertyList FDE17_propertylist = FDE17.getPropertyList();
				if (FDE17_propertylist != null) {
                    for (Property FDE17_property : FDE17_propertylist.values()) {
                        if (FDE17_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE17_property.setValue(valueOne);
                            else
                                FDE17_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE17.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE17.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE18
		        isInit = false;
				PropertyList FDE18_propertylist = FDE18.getPropertyList();
				if (FDE18_propertylist != null) {
                    for (Property FDE18_property : FDE18_propertylist.values()) {
                        if (FDE18_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE18_property.setValue(valueOne);
                            else
                                FDE18_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE18.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE18.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE19
		        isInit = false;
				PropertyList FDE19_propertylist = FDE19.getPropertyList();
				if (FDE19_propertylist != null) {
                    for (Property FDE19_property : FDE19_propertylist.values()) {
                        if (FDE19_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE19_property.setValue(valueOne);
                            else
                                FDE19_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE19.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE19.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE20
		        isInit = false;
				PropertyList FDE20_propertylist = FDE20.getPropertyList();
				if (FDE20_propertylist != null) {
                    for (Property FDE20_property : FDE20_propertylist.values()) {
                        if (FDE20_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE20_property.setValue(valueOne);
                            else
                                FDE20_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE20.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE20.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE21
		        isInit = false;
				PropertyList FDE21_propertylist = FDE21.getPropertyList();
				if (FDE21_propertylist != null) {
                    for (Property FDE21_property : FDE21_propertylist.values()) {
                        if (FDE21_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE21_property.setValue(valueOne);
                            else
                                FDE21_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE21.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE21.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE22
		        isInit = false;
				PropertyList FDE22_propertylist = FDE22.getPropertyList();
				if (FDE22_propertylist != null) {
                    for (Property FDE22_property : FDE22_propertylist.values()) {
                        if (FDE22_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE22_property.setValue(valueOne);
                            else
                                FDE22_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE22.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE22.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE23
		        isInit = false;
				PropertyList FDE23_propertylist = FDE23.getPropertyList();
				if (FDE23_propertylist != null) {
                    for (Property FDE23_property : FDE23_propertylist.values()) {
                        if (FDE23_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE23_property.setValue(valueOne);
                            else
                                FDE23_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE23.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE23.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE24
		        isInit = false;
				PropertyList FDE24_propertylist = FDE24.getPropertyList();
				if (FDE24_propertylist != null) {
                    for (Property FDE24_property : FDE24_propertylist.values()) {
                        if (FDE24_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE24_property.setValue(valueOne);
                            else
                                FDE24_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE24.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE24.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE25
		        isInit = false;
				PropertyList FDE25_propertylist = FDE25.getPropertyList();
				if (FDE25_propertylist != null) {
                    for (Property FDE25_property : FDE25_propertylist.values()) {
                        if (FDE25_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE25_property.setValue(valueOne);
                            else
                                FDE25_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE25.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE25.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE26
		        isInit = false;
				PropertyList FDE26_propertylist = FDE26.getPropertyList();
				if (FDE26_propertylist != null) {
                    for (Property FDE26_property : FDE26_propertylist.values()) {
                        if (FDE26_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE26_property.setValue(valueOne);
                            else
                                FDE26_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE26.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE26.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE27
		        isInit = false;
				PropertyList FDE27_propertylist = FDE27.getPropertyList();
				if (FDE27_propertylist != null) {
                    for (Property FDE27_property : FDE27_propertylist.values()) {
                        if (FDE27_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE27_property.setValue(valueOne);
                            else
                                FDE27_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE27.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE27.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE28
		        isInit = false;
				PropertyList FDE28_propertylist = FDE28.getPropertyList();
				if (FDE28_propertylist != null) {
                    for (Property FDE28_property : FDE28_propertylist.values()) {
                        if (FDE28_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE28_property.setValue(valueOne);
                            else
                                FDE28_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE28.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE28.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE29
		        isInit = false;
				PropertyList FDE29_propertylist = FDE29.getPropertyList();
				if (FDE29_propertylist != null) {
                    for (Property FDE29_property : FDE29_propertylist.values()) {
                        if (FDE29_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE29_property.setValue(valueOne);
                            else
                                FDE29_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE29.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE29.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE30
		        isInit = false;
				PropertyList FDE30_propertylist = FDE30.getPropertyList();
				if (FDE30_propertylist != null) {
                    for (Property FDE30_property : FDE30_propertylist.values()) {
                        if (FDE30_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE30_property.setValue(valueOne);
                            else
                                FDE30_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE30.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE30.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE31
		        isInit = false;
				PropertyList FDE31_propertylist = FDE31.getPropertyList();
				if (FDE31_propertylist != null) {
                    for (Property FDE31_property : FDE31_propertylist.values()) {
                        if (FDE31_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE31_property.setValue(valueOne);
                            else
                                FDE31_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE31.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE31.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE32
		        isInit = false;
				PropertyList FDE32_propertylist = FDE32.getPropertyList();
				if (FDE32_propertylist != null) {
                    for (Property FDE32_property : FDE32_propertylist.values()) {
                        if (FDE32_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT_01 >> initCount_01++) & 1) == 1)
                                FDE32_property.setValue(valueOne);
                            else
                                FDE32_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT_01 >> initCount_01++) & 1) == 1)
                        FDE32.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE32.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for LUT3_1
                isInit = false;
				PropertyList LUT3_1_propertylist = LUT3_1.getPropertyList();
				if (LUT3_1_propertylist != null) {
                    for (Property LUT3_1_property : LUT3_1_propertylist.values()) {
                        if (LUT3_1_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_1_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_1.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_2
                isInit = false;
				PropertyList LUT3_2_propertylist = LUT3_2.getPropertyList();
				if (LUT3_2_propertylist != null) {
                    for (Property LUT3_2_property : LUT3_2_propertylist.values()) {
                        if (LUT3_2_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_2_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_2.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_3
                isInit = false;
				PropertyList LUT3_3_propertylist = LUT3_3.getPropertyList();
				if (LUT3_3_propertylist != null) {
                    for (Property LUT3_3_property : LUT3_3_propertylist.values()) {
                        if (LUT3_3_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_3_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_3.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_4
                isInit = false;
				PropertyList LUT3_4_propertylist = LUT3_4.getPropertyList();
				if (LUT3_4_propertylist != null) {
                    for (Property LUT3_4_property : LUT3_4_propertylist.values()) {
                        if (LUT3_4_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_4_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_4.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_5
                isInit = false;
				PropertyList LUT3_5_propertylist = LUT3_5.getPropertyList();
				if (LUT3_5_propertylist != null) {
                    for (Property LUT3_5_property : LUT3_5_propertylist.values()) {
                        if (LUT3_5_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_5_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_5.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_6
                isInit = false;
				PropertyList LUT3_6_propertylist = LUT3_6.getPropertyList();
				if (LUT3_6_propertylist != null) {
                    for (Property LUT3_6_property : LUT3_6_propertylist.values()) {
                        if (LUT3_6_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_6_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_6.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_7
                isInit = false;
				PropertyList LUT3_7_propertylist = LUT3_7.getPropertyList();
				if (LUT3_7_propertylist != null) {
                    for (Property LUT3_7_property : LUT3_7_propertylist.values()) {
                        if (LUT3_7_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_7_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_7.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_8
                isInit = false;
				PropertyList LUT3_8_propertylist = LUT3_8.getPropertyList();
				if (LUT3_8_propertylist != null) {
                    for (Property LUT3_8_property : LUT3_8_propertylist.values()) {
                        if (LUT3_8_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_8_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_8.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_9
                isInit = false;
				PropertyList LUT3_9_propertylist = LUT3_9.getPropertyList();
				if (LUT3_9_propertylist != null) {
                    for (Property LUT3_9_property : LUT3_9_propertylist.values()) {
                        if (LUT3_9_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_9_property.setValue(valueCA);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_9.addProperty(new Property("INIT", (EdifTypedValue) valueCA));
                }
              //Set INIT Property for LUT3_10
                isInit = false;
				PropertyList LUT3_10_propertylist = LUT3_10.getPropertyList();
				if (LUT3_10_propertylist != null) {
                    for (Property LUT3_10_property : LUT3_10_propertylist.values()) {
                        if (LUT3_10_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_10_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_10.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_11
                isInit = false;
				PropertyList LUT3_11_propertylist = LUT3_11.getPropertyList();
				if (LUT3_11_propertylist != null) {
                    for (Property LUT3_11_property : LUT3_11_propertylist.values()) {
                        if (LUT3_11_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_11_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_11.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_12
                isInit = false;
				PropertyList LUT3_12_propertylist = LUT3_12.getPropertyList();
				if (LUT3_12_propertylist != null) {
                    for (Property LUT3_12_property : LUT3_12_propertylist.values()) {
                        if (LUT3_12_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_12_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_12.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_13
                isInit = false;
				PropertyList LUT3_13_propertylist = LUT3_13.getPropertyList();
				if (LUT3_13_propertylist != null) {
                    for (Property LUT3_13_property : LUT3_13_propertylist.values()) {
                        if (LUT3_13_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_13_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_13.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_14
                isInit = false;
				PropertyList LUT3_14_propertylist = LUT3_14.getPropertyList();
				if (LUT3_14_propertylist != null) {
                    for (Property LUT3_14_property : LUT3_14_propertylist.values()) {
                        if (LUT3_14_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_14_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_14.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_15
                isInit = false;
				PropertyList LUT3_15_propertylist = LUT3_15.getPropertyList();
				if (LUT3_15_propertylist != null) {
                    for (Property LUT3_15_property : LUT3_15_propertylist.values()) {
                        if (LUT3_15_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_15_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_15.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_16
                isInit = false;
				PropertyList LUT3_16_propertylist = LUT3_16.getPropertyList();
				if (LUT3_16_propertylist != null) {
                    for (Property LUT3_16_property : LUT3_16_propertylist.values()) {
                        if (LUT3_16_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_16_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_16.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_17
                isInit = false;
				PropertyList LUT3_17_propertylist = LUT3_17.getPropertyList();
				if (LUT3_17_propertylist != null) {
                    for (Property LUT3_17_property : LUT3_17_propertylist.values()) {
                        if (LUT3_17_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_17_property.setValue(valueE4);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_17.addProperty(new Property("INIT", (EdifTypedValue) valueE4));
                }
              //Set INIT Property for LUT3_18
                isInit = false;
				PropertyList LUT3_18_propertylist = LUT3_18.getPropertyList();
				if (LUT3_18_propertylist != null) {
                    for (Property LUT3_18_property : LUT3_18_propertylist.values()) {
                        if (LUT3_18_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT3_18_property.setValue(valueCA);
                        }
                    }
                }
                if (!isInit) {
                    LUT3_18.addProperty(new Property("INIT", (EdifTypedValue) valueCA));
                }
              //Set INIT Property for LUT2_1
                isInit = false;
				PropertyList LUT2_1_propertylist = LUT2_1.getPropertyList();
				if (LUT2_1_propertylist != null) {
                    for (Property LUT2_1_property : LUT2_1_propertylist.values()) {
                        if (LUT2_1_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT2_1_property.setValue(value4);
                        }
                    }
                }
                if (!isInit) {
                    LUT2_1.addProperty(new Property("INIT", (EdifTypedValue) value4));
                }
              //Set INIT Property for LUT2_2
                isInit = false;
				PropertyList LUT2_2_propertylist = LUT2_2.getPropertyList();
				if (LUT2_2_propertylist != null) {
                    for (Property LUT2_2_property : LUT2_2_propertylist.values()) {
                        if (LUT2_2_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT2_2_property.setValue(value8);
                        }
                    }
                }
                if (!isInit) {
                    LUT2_2.addProperty(new Property("INIT", (EdifTypedValue) value8));
                }
              //Set INIT Property for LUT4_1
                isInit = false;
				PropertyList LUT4_1_propertylist = LUT4_1.getPropertyList();
				if (LUT4_1_propertylist != null) {
                    for (Property LUT4_1_property : LUT4_1_propertylist.values()) {
                        if (LUT4_1_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_1_property.setValue(value0002);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_1.addProperty(new Property("INIT", (EdifTypedValue) value0002));
                }
              //Set INIT Property for LUT4_2
                isInit = false;
				PropertyList LUT4_2_propertylist = LUT4_2.getPropertyList();
				if (LUT4_2_propertylist != null) {
                    for (Property LUT4_2_property : LUT4_2_propertylist.values()) {
                        if (LUT4_2_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_2_property.setValue(value0008);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_2.addProperty(new Property("INIT", (EdifTypedValue) value0008));
                }
              //Set INIT Property for LUT4_3
                isInit = false;
				PropertyList LUT4_3_propertylist = LUT4_3.getPropertyList();
				if (LUT4_3_propertylist != null) {
                    for (Property LUT4_3_property : LUT4_3_propertylist.values()) {
                        if (LUT4_3_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_3_property.setValue(value0020);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_3.addProperty(new Property("INIT", (EdifTypedValue) value0020));
                }
              //Set INIT Property for LUT4_4
                isInit = false;
				PropertyList LUT4_4_propertylist = LUT4_4.getPropertyList();
				if (LUT4_4_propertylist != null) {
                    for (Property LUT4_4_property : LUT4_4_propertylist.values()) {
                        if (LUT4_4_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_4_property.setValue(value0080);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_4.addProperty(new Property("INIT", (EdifTypedValue) value0080));
                }
              //Set INIT Property for LUT4_5
                isInit = false;
				PropertyList LUT4_5_propertylist = LUT4_5.getPropertyList();
				if (LUT4_5_propertylist != null) {
                    for (Property LUT4_5_property : LUT4_5_propertylist.values()) {
                        if (LUT4_5_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_5_property.setValue(value0200);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_5.addProperty(new Property("INIT", (EdifTypedValue) value0200));
                }
              //Set INIT Property for LUT4_6
                isInit = false;
				PropertyList LUT4_6_propertylist = LUT4_6.getPropertyList();
				if (LUT4_6_propertylist != null) {
                    for (Property LUT4_6_property : LUT4_6_propertylist.values()) {
                        if (LUT4_6_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_6_property.setValue(value0800);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_6.addProperty(new Property("INIT", (EdifTypedValue) value0800));
                }
              //Set INIT Property for LUT4_7
                isInit = false;
				PropertyList LUT4_7_propertylist = LUT4_7.getPropertyList();
				if (LUT4_7_propertylist != null) {
                    for (Property LUT4_7_property : LUT4_7_propertylist.values()) {
                        if (LUT4_7_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_7_property.setValue(value2000);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_7.addProperty(new Property("INIT", (EdifTypedValue) value2000));
                }
              //Set INIT Property for LUT4_8
                isInit = false;
				PropertyList LUT4_8_propertylist = LUT4_8.getPropertyList();
				if (LUT4_8_propertylist != null) {
                    for (Property LUT4_8_property : LUT4_8_propertylist.values()) {
                        if (LUT4_8_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_8_property.setValue(value8000);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_8.addProperty(new Property("INIT", (EdifTypedValue) value8000));
                }
              //Set INIT Property for LUT4_9
                isInit = false;
				PropertyList LUT4_9_propertylist = LUT4_9.getPropertyList();
				if (LUT4_9_propertylist != null) {
                    for (Property LUT4_9_property : LUT4_9_propertylist.values()) {
                        if (LUT4_9_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_9_property.setValue(value0002);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_9.addProperty(new Property("INIT", (EdifTypedValue) value0002));
                }
              //Set INIT Property for LUT4_10
                isInit = false;
				PropertyList LUT4_10_propertylist = LUT4_10.getPropertyList();
				if (LUT4_10_propertylist != null) {
                    for (Property LUT4_10_property : LUT4_10_propertylist.values()) {
                        if (LUT4_10_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_10_property.setValue(value0008);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_10.addProperty(new Property("INIT", (EdifTypedValue) value0008));
                }
              //Set INIT Property for LUT4_11
                isInit = false;
				PropertyList LUT4_11_propertylist = LUT4_11.getPropertyList();
				if (LUT4_11_propertylist != null) {
                    for (Property LUT4_11_property : LUT4_11_propertylist.values()) {
                        if (LUT4_11_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_11_property.setValue(value0020);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_11.addProperty(new Property("INIT", (EdifTypedValue) value0020));
                }
              //Set INIT Property for LUT4_12
                isInit = false;
				PropertyList LUT4_12_propertylist = LUT4_12.getPropertyList();
				if (LUT4_12_propertylist != null) {
                    for (Property LUT4_12_property : LUT4_12_propertylist.values()) {
                        if (LUT4_12_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_12_property.setValue(value0080);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_12.addProperty(new Property("INIT", (EdifTypedValue) value0080));
                }
              //Set INIT Property for LUT4_13
                isInit = false;
				PropertyList LUT4_13_propertylist = LUT4_13.getPropertyList();
				if (LUT4_13_propertylist != null) {
                    for (Property LUT4_13_property : LUT4_13_propertylist.values()) {
                        if (LUT4_13_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_13_property.setValue(value0200);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_13.addProperty(new Property("INIT", (EdifTypedValue) value0200));
                }
              //Set INIT Property for LUT4_14
                isInit = false;
				PropertyList LUT4_14_propertylist = LUT4_14.getPropertyList();
				if (LUT4_14_propertylist != null) {
                    for (Property LUT4_14_property : LUT4_14_propertylist.values()) {
                        if (LUT4_14_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_14_property.setValue(value0800);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_14.addProperty(new Property("INIT", (EdifTypedValue) value0800));
                }
              //Set INIT Property for LUT4_15
                isInit = false;
				PropertyList LUT4_15_propertylist = LUT4_15.getPropertyList();
				if (LUT4_15_propertylist != null) {
                    for (Property LUT4_15_property : LUT4_15_propertylist.values()) {
                        if (LUT4_15_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_15_property.setValue(value2000);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_15.addProperty(new Property("INIT", (EdifTypedValue) value2000));
                }
              //Set INIT Property for LUT4_16
                isInit = false;
				PropertyList LUT4_16_propertylist = LUT4_16.getPropertyList();
				if (LUT4_16_propertylist != null) {
                    for (Property LUT4_16_property : LUT4_16_propertylist.values()) {
                        if (LUT4_16_property.getName().equals("INIT")) {
                            isInit = true;
                            LUT4_16_property.setValue(value8000);
                        }
                    }
                }
                if (!isInit) {
                    LUT4_16.addProperty(new Property("INIT", (EdifTypedValue) value8000));
                }
			} catch (EdifNameConflictException e){
				System.out.println("EdifNameConflictException caught"+e);
				//Should not happen
			}
		} catch (InvalidEdifNameException e){
			System.out.println("InvalidEdifNameException caught"+e);
			//Should not happen
		}
	}
}
