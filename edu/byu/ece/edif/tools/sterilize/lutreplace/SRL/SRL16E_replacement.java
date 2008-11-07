/* TODO: Insert class description here.
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
package edu.byu.ece.edif.tools.sterilize.lutreplace.SRL;

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

public class SRL16E_replacement{
	public static void main(String args[]){
		
		String outputFileName = "SRL16E_replacement.edf";
		
		try{
			/******Environment and Environment manager******/
			EdifEnvironment topLevel = new EdifEnvironment("SRL16E_replacement");
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
				EdifCell LUT3 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "LUT3");
				EdifCell MUXF5 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "MUXF5");
				EdifCell MUXF6 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "MUXF6");	
				EdifCell SRL16E_replacement = new EdifCell(work, "SRL16E_replacement");
				SRL16E_replacement.addPort("clk", 1, 1);
				SRL16E_replacement.addPort("ce", 1, 1);
				SRL16E_replacement.addPort("d", 1, 1);
				SRL16E_replacement.addPort("a0", 1, 1);
				SRL16E_replacement.addPort("a1", 1, 1);
				SRL16E_replacement.addPort("a2", 1, 1);
				SRL16E_replacement.addPort("a3", 1, 1);
				SRL16E_replacement.addPort("q", 1, 2);
				
				/******Design******/
				EdifDesign design = new EdifDesign("SRL16E_replacement");
				topLevel.setTopDesign(design);
				topLevel.setTopCell(SRL16E_replacement);
				
				/******Nets******/
				EdifNet clk_c = new EdifNet("clk_c", SRL16E_replacement);
				EdifNet clk = new EdifNet("clk", SRL16E_replacement);
				EdifNet ce_c = new EdifNet("ce_c", SRL16E_replacement);
				EdifNet ce = new EdifNet("ce", SRL16E_replacement);
				EdifNet d_c = new EdifNet("d_c", SRL16E_replacement);
				EdifNet d = new EdifNet("d", SRL16E_replacement);
				EdifNet a0_c = new EdifNet("a0_c", SRL16E_replacement);
				EdifNet a0 = new EdifNet("a0", SRL16E_replacement);
				EdifNet a1_c = new EdifNet("a1_c", SRL16E_replacement);
				EdifNet a1 = new EdifNet("a1", SRL16E_replacement);
				EdifNet a2_c = new EdifNet("a2_c", SRL16E_replacement);
				EdifNet a2 = new EdifNet("a2", SRL16E_replacement);
				EdifNet a3_c = new EdifNet("a3_c", SRL16E_replacement);
				EdifNet a3 = new EdifNet("a3", SRL16E_replacement);
				EdifNet q_c = new EdifNet("q_c", SRL16E_replacement);
				EdifNet q = new EdifNet("q", SRL16E_replacement);
				EdifNet wire1 = new EdifNet("wire1", SRL16E_replacement);
				EdifNet wire2 = new EdifNet("wire2", SRL16E_replacement);
				EdifNet wire3 = new EdifNet("wire3", SRL16E_replacement);
				EdifNet wire4 = new EdifNet("wire4", SRL16E_replacement);
				EdifNet wire5 = new EdifNet("wire5", SRL16E_replacement);
				EdifNet wire6 = new EdifNet("wire6", SRL16E_replacement);
				EdifNet wire7 = new EdifNet("wire7", SRL16E_replacement);
				EdifNet wire8 = new EdifNet("wire8", SRL16E_replacement);
				EdifNet wire9 = new EdifNet("wire9", SRL16E_replacement);
				EdifNet wire10 = new EdifNet("wire10", SRL16E_replacement);
				EdifNet wire11 = new EdifNet("wire11", SRL16E_replacement);
				EdifNet wire12 = new EdifNet("wire12", SRL16E_replacement);
				EdifNet wire13 = new EdifNet("wire13", SRL16E_replacement);
				EdifNet wire14 = new EdifNet("wire14", SRL16E_replacement);
				EdifNet wire15 = new EdifNet("wire15", SRL16E_replacement);
				EdifNet wire16 = new EdifNet("wire16", SRL16E_replacement);
				EdifNet net1 = new EdifNet("net1", SRL16E_replacement);
				EdifNet net2 = new EdifNet("net2", SRL16E_replacement);
				EdifNet net3 = new EdifNet("net3", SRL16E_replacement);
				EdifNet net4 = new EdifNet("net4", SRL16E_replacement);
				EdifNet net5 = new EdifNet("net5", SRL16E_replacement);
				EdifNet net6 = new EdifNet("net6", SRL16E_replacement);
				EdifNet net7 = new EdifNet("net7", SRL16E_replacement);
				EdifNet net8 = new EdifNet("net8", SRL16E_replacement);
				EdifNet net9 = new EdifNet("net9", SRL16E_replacement);
				EdifNet net10 = new EdifNet("net10", SRL16E_replacement);
				EdifNet net11 = new EdifNet("net11", SRL16E_replacement);
				EdifNet net12 = new EdifNet("net12", SRL16E_replacement);
				EdifNet net13 = new EdifNet("net13", SRL16E_replacement);
				EdifNet net14 = new EdifNet("net14", SRL16E_replacement);
				SRL16E_replacement.addNet(clk_c);
				SRL16E_replacement.addNet(clk);
				SRL16E_replacement.addNet(ce_c);
				SRL16E_replacement.addNet(ce);
				SRL16E_replacement.addNet(d_c);
				SRL16E_replacement.addNet(d);
				SRL16E_replacement.addNet(a0_c);
				SRL16E_replacement.addNet(a0);
				SRL16E_replacement.addNet(a1_c);
				SRL16E_replacement.addNet(a1);
				SRL16E_replacement.addNet(a2_c);
				SRL16E_replacement.addNet(a2);
				SRL16E_replacement.addNet(a3_c);
				SRL16E_replacement.addNet(a3);
				SRL16E_replacement.addNet(q_c);
				SRL16E_replacement.addNet(q);
				SRL16E_replacement.addNet(wire1);
				SRL16E_replacement.addNet(wire2);
				SRL16E_replacement.addNet(wire3);
				SRL16E_replacement.addNet(wire4);
				SRL16E_replacement.addNet(wire5);
				SRL16E_replacement.addNet(wire6);
				SRL16E_replacement.addNet(wire7);
				SRL16E_replacement.addNet(wire8);
				SRL16E_replacement.addNet(wire9);
				SRL16E_replacement.addNet(wire10);
				SRL16E_replacement.addNet(wire11);
				SRL16E_replacement.addNet(wire12);
				SRL16E_replacement.addNet(wire13);
				SRL16E_replacement.addNet(wire14);
				SRL16E_replacement.addNet(wire15);
				SRL16E_replacement.addNet(wire16);
				SRL16E_replacement.addNet(net1);
				SRL16E_replacement.addNet(net2);
				SRL16E_replacement.addNet(net3);
				SRL16E_replacement.addNet(net4);
				SRL16E_replacement.addNet(net5);
				SRL16E_replacement.addNet(net6);
				SRL16E_replacement.addNet(net7);
				SRL16E_replacement.addNet(net8);
				SRL16E_replacement.addNet(net9);
				SRL16E_replacement.addNet(net10);
				SRL16E_replacement.addNet(net11);
				SRL16E_replacement.addNet(net12);
				SRL16E_replacement.addNet(net13);
				SRL16E_replacement.addNet(net14);
				
				/******Instances******/
				EdifCellInstance clk_ibuf = new EdifCellInstance("clk_ibuf", SRL16E_replacement, BUFGP);
				EdifCellInstance q_obuf = new EdifCellInstance("q_obuf", SRL16E_replacement, OBUF);
				EdifCellInstance d_ibuf = new EdifCellInstance("d_ibuf", SRL16E_replacement, IBUF);
				EdifCellInstance ce_ibuf = new EdifCellInstance("ce_ibuf", SRL16E_replacement, IBUF);
				EdifCellInstance a0_ibuf = new EdifCellInstance("a0_ibuf", SRL16E_replacement, IBUF);
				EdifCellInstance a1_ibuf = new EdifCellInstance("a1_ibuf", SRL16E_replacement, IBUF);
				EdifCellInstance a2_ibuf = new EdifCellInstance("a2_ibuf", SRL16E_replacement, IBUF);
				EdifCellInstance a3_ibuf = new EdifCellInstance("a3_ibuf", SRL16E_replacement, IBUF);
				EdifCellInstance FDE1 = new EdifCellInstance("FDE1", SRL16E_replacement, FDE);
				EdifCellInstance FDE2 = new EdifCellInstance("FDE2", SRL16E_replacement, FDE);
				EdifCellInstance FDE3 = new EdifCellInstance("FDE3", SRL16E_replacement, FDE);
				EdifCellInstance FDE4 = new EdifCellInstance("FDE4", SRL16E_replacement, FDE);
				EdifCellInstance FDE5 = new EdifCellInstance("FDE5", SRL16E_replacement, FDE);
				EdifCellInstance FDE6 = new EdifCellInstance("FDE6", SRL16E_replacement, FDE);
				EdifCellInstance FDE7 = new EdifCellInstance("FDE7", SRL16E_replacement, FDE);
				EdifCellInstance FDE8 = new EdifCellInstance("FDE8", SRL16E_replacement, FDE);
				EdifCellInstance FDE9 = new EdifCellInstance("FDE9", SRL16E_replacement, FDE);
				EdifCellInstance FDE10 = new EdifCellInstance("FDE10", SRL16E_replacement, FDE);
				EdifCellInstance FDE11 = new EdifCellInstance("FDE11", SRL16E_replacement, FDE);
				EdifCellInstance FDE12 = new EdifCellInstance("FDE12", SRL16E_replacement, FDE);
				EdifCellInstance FDE13 = new EdifCellInstance("FDE13", SRL16E_replacement, FDE);
				EdifCellInstance FDE14 = new EdifCellInstance("FDE14", SRL16E_replacement, FDE);
				EdifCellInstance FDE15 = new EdifCellInstance("FDE15", SRL16E_replacement, FDE);
				EdifCellInstance FDE16 = new EdifCellInstance("FDE16", SRL16E_replacement, FDE);
				EdifCellInstance LUT3_1 = new EdifCellInstance("LUT3_1", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_2 = new EdifCellInstance("LUT3_2", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_3 = new EdifCellInstance("LUT3_3", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_4 = new EdifCellInstance("LUT3_4", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_5 = new EdifCellInstance("LUT3_5", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_6 = new EdifCellInstance("LUT3_6", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_7 = new EdifCellInstance("LUT3_7", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_8 = new EdifCellInstance("LUT3_8", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_9 = new EdifCellInstance("LUT3_9", SRL16E_replacement, LUT3);
				EdifCellInstance MUXF5_1 = new EdifCellInstance("MUXF5_1", SRL16E_replacement, MUXF5);
				EdifCellInstance MUXF5_2 = new EdifCellInstance("MUXF5_2", SRL16E_replacement, MUXF5);
				EdifCellInstance MUXF5_3 = new EdifCellInstance("MUXF5_3", SRL16E_replacement, MUXF5);
				EdifCellInstance MUXF5_4 = new EdifCellInstance("MUXF5_4", SRL16E_replacement, MUXF5);
				EdifCellInstance MUXF6_1 = new EdifCellInstance("MUXF6_1", SRL16E_replacement, MUXF6);
				EdifCellInstance MUXF6_2 = new EdifCellInstance("MUXF6_2", SRL16E_replacement, MUXF6);
				SRL16E_replacement.addSubCell(clk_ibuf);
				SRL16E_replacement.addSubCell(q_obuf);
				SRL16E_replacement.addSubCell(d_ibuf);
				SRL16E_replacement.addSubCell(ce_ibuf);
				SRL16E_replacement.addSubCell(a0_ibuf);
				SRL16E_replacement.addSubCell(a1_ibuf);
				SRL16E_replacement.addSubCell(a2_ibuf);
				SRL16E_replacement.addSubCell(a3_ibuf);
				SRL16E_replacement.addSubCell(FDE1);
				SRL16E_replacement.addSubCell(FDE2);
				SRL16E_replacement.addSubCell(FDE3);
				SRL16E_replacement.addSubCell(FDE4);
				SRL16E_replacement.addSubCell(FDE5);
				SRL16E_replacement.addSubCell(FDE6);
				SRL16E_replacement.addSubCell(FDE7);
				SRL16E_replacement.addSubCell(FDE8);
				SRL16E_replacement.addSubCell(FDE9);
				SRL16E_replacement.addSubCell(FDE10);
				SRL16E_replacement.addSubCell(FDE11);
				SRL16E_replacement.addSubCell(FDE12);
				SRL16E_replacement.addSubCell(FDE13);
				SRL16E_replacement.addSubCell(FDE14);
				SRL16E_replacement.addSubCell(FDE15);
				SRL16E_replacement.addSubCell(FDE16);
				SRL16E_replacement.addSubCell(LUT3_1);
				SRL16E_replacement.addSubCell(LUT3_2);
				SRL16E_replacement.addSubCell(LUT3_3);
				SRL16E_replacement.addSubCell(LUT3_4);
				SRL16E_replacement.addSubCell(LUT3_5);
				SRL16E_replacement.addSubCell(LUT3_6);
				SRL16E_replacement.addSubCell(LUT3_7);
				SRL16E_replacement.addSubCell(LUT3_8);
				SRL16E_replacement.addSubCell(LUT3_9);
				SRL16E_replacement.addSubCell(MUXF5_1);
				SRL16E_replacement.addSubCell(MUXF5_2);
				SRL16E_replacement.addSubCell(MUXF5_3);
				SRL16E_replacement.addSubCell(MUXF5_4);
				SRL16E_replacement.addSubCell(MUXF6_1);
				SRL16E_replacement.addSubCell(MUXF6_2);
				
				/******Interface******/
				EdifCellInterface SRL16E_replacement_interface = new EdifCellInterface(SRL16E_replacement);
				EdifCellInterface clk_ibuf_interface = new EdifCellInterface(BUFGP);
				EdifCellInterface d_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface ce_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface a0_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface a1_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface a2_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface a3_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface q_obuf_interface = new EdifCellInterface(OBUF);
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
				EdifCellInterface LUT3_1_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_2_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_3_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_4_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_5_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_6_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_7_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_8_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_9_interface = new EdifCellInterface(LUT3);
				EdifCellInterface MUXF5_1_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_2_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_3_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_4_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF6_1_interface = new EdifCellInterface(MUXF6);
				EdifCellInterface MUXF6_2_interface = new EdifCellInterface(MUXF6);
				
				/******Ports******/
				//SRL16E_replacement
				EdifPort SRL16E_replacement_interface_clk = new EdifPort(SRL16E_replacement_interface, "clk", 1, 1);
				EdifPort SRL16E_replacement_interface_d = new EdifPort(SRL16E_replacement_interface, "d", 1, 1);
				EdifPort SRL16E_replacement_interface_ce = new EdifPort(SRL16E_replacement_interface, "ce", 1, 1);
				EdifPort SRL16E_replacement_interface_a0 = new EdifPort(SRL16E_replacement_interface, "a0", 1 ,1);
				EdifPort SRL16E_replacement_interface_a1 = new EdifPort(SRL16E_replacement_interface, "a1", 1 ,1);
				EdifPort SRL16E_replacement_interface_a2 = new EdifPort(SRL16E_replacement_interface, "a2", 1 ,1);
				EdifPort SRL16E_replacement_interface_a3 = new EdifPort(SRL16E_replacement_interface, "a3", 1 ,1);
				EdifPort SRL16E_replacement_interface_q = new EdifPort(SRL16E_replacement_interface, "q", 1, 2);
				SRL16E_replacement_interface.addPort("clk", 1, 1);
				SRL16E_replacement_interface.addPort("d", 1, 1);
				SRL16E_replacement_interface.addPort("ce", 1, 1);
				SRL16E_replacement_interface.addPort("a0", 1, 1);
				SRL16E_replacement_interface.addPort("a1", 1, 1);
				SRL16E_replacement_interface.addPort("a2", 1, 1);
				SRL16E_replacement_interface.addPort("a3", 1, 1);
				SRL16E_replacement_interface.addPort("q", 1, 2);
				//clk_ibuf
				EdifPort clk_ibuf_interface_I = new EdifPort(clk_ibuf_interface, "I", 1, 1);
				EdifPort clk_ibuf_interface_O = new EdifPort(clk_ibuf_interface, "O", 1, 2);
				clk_ibuf_interface.addPort("I", 1, 1);
				clk_ibuf_interface.addPort("O", 1, 2);
				//d_ibuf
				EdifPort d_ibuf_interface_I = new EdifPort(d_ibuf_interface, "I", 1, 1);
				EdifPort d_ibuf_interface_O = new EdifPort(d_ibuf_interface, "O", 1, 2);
				d_ibuf_interface.addPort("I", 1, 1);
				d_ibuf_interface.addPort("O", 1, 2);
				//ce_ibuf
				EdifPort ce_ibuf_interface_I = new EdifPort(ce_ibuf_interface, "I", 1, 1);
				EdifPort ce_ibuf_interface_O = new EdifPort(ce_ibuf_interface, "O", 1, 2);
				ce_ibuf_interface.addPort("I", 1, 1);
				ce_ibuf_interface.addPort("O", 1, 2);
				//a0_ibuf
				EdifPort a0_ibuf_interface_I = new EdifPort(a0_ibuf_interface, "I", 1, 1);
				EdifPort a0_ibuf_interface_O = new EdifPort(a0_ibuf_interface, "O", 1, 2);
				a0_ibuf_interface.addPort("I", 1, 1);
				a0_ibuf_interface.addPort("O", 1, 2);
				//a1_ibuf
				EdifPort a1_ibuf_interface_I = new EdifPort(a1_ibuf_interface, "I", 1, 1);
				EdifPort a1_ibuf_interface_O = new EdifPort(a1_ibuf_interface, "O", 1, 2);
				a1_ibuf_interface.addPort("I", 1, 1);
				a1_ibuf_interface.addPort("O", 1, 2);
				//a2_ibuf
				EdifPort a2_ibuf_interface_I = new EdifPort(a2_ibuf_interface, "I", 1, 1);
				EdifPort a2_ibuf_interface_O = new EdifPort(a2_ibuf_interface, "O", 1, 2);
				a2_ibuf_interface.addPort("I", 1, 1);
				a2_ibuf_interface.addPort("O", 1, 2);
				//a3_ibuf
				EdifPort a3_ibuf_interface_I = new EdifPort(a3_ibuf_interface, "I", 1, 1);
				EdifPort a3_ibuf_interface_O = new EdifPort(a3_ibuf_interface, "O", 1, 2);
				a3_ibuf_interface.addPort("I", 1, 1);
				a3_ibuf_interface.addPort("O", 1, 2);
				//q_obuf
				EdifPort q_obuf_interface_I = new EdifPort(q_obuf_interface, "I", 1, 1);
				EdifPort q_obuf_interface_O = new EdifPort(q_obuf_interface, "O", 1, 2);
				q_obuf_interface.addPort("I", 1, 1);
				q_obuf_interface.addPort("O", 1, 2);
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
				
				/******PortRefs******/
				EdifPortRef SRL16E_replacement_clk = new EdifPortRef(clk, SRL16E_replacement_interface_clk.getSingleBitPort(0), null);
				EdifPortRef SRL16E_replacement_d = new EdifPortRef(d, SRL16E_replacement_interface_d.getSingleBitPort(0), null);
				EdifPortRef SRL16E_replacement_ce = new EdifPortRef(ce, SRL16E_replacement_interface_ce.getSingleBitPort(0), null);
				EdifPortRef SRL16E_replacement_a0 = new EdifPortRef(a0, SRL16E_replacement_interface_a0.getSingleBitPort(0), null);
				EdifPortRef SRL16E_replacement_a1 = new EdifPortRef(a1, SRL16E_replacement_interface_a1.getSingleBitPort(0), null);
				EdifPortRef SRL16E_replacement_a2 = new EdifPortRef(a2, SRL16E_replacement_interface_a2.getSingleBitPort(0), null);
				EdifPortRef SRL16E_replacement_a3 = new EdifPortRef(a3, SRL16E_replacement_interface_a3.getSingleBitPort(0), null);
				EdifPortRef SRL16E_replacement_q = new EdifPortRef(q, SRL16E_replacement_interface_q.getSingleBitPort(0), null);
				EdifPortRef clk_ibuf_I = new EdifPortRef(clk, clk_ibuf_interface_I.getSingleBitPort(0), clk_ibuf);
				EdifPortRef clk_ibuf_O = new EdifPortRef(clk_c, clk_ibuf_interface_O.getSingleBitPort(0), clk_ibuf);
				EdifPortRef d_ibuf_I = new EdifPortRef(d, d_ibuf_interface_I.getSingleBitPort(0), d_ibuf);
				EdifPortRef d_ibuf_O = new EdifPortRef(d_c, d_ibuf_interface_O.getSingleBitPort(0), d_ibuf);
				EdifPortRef ce_ibuf_I = new EdifPortRef(ce, ce_ibuf_interface_I.getSingleBitPort(0), ce_ibuf);
				EdifPortRef ce_ibuf_O = new EdifPortRef(ce_c, ce_ibuf_interface_O.getSingleBitPort(0), ce_ibuf);
				EdifPortRef a0_ibuf_I = new EdifPortRef(a0, a0_ibuf_interface_I.getSingleBitPort(0), a0_ibuf);
				EdifPortRef a0_ibuf_O = new EdifPortRef(a0_c, a0_ibuf_interface_O.getSingleBitPort(0), a0_ibuf);
				EdifPortRef a1_ibuf_I = new EdifPortRef(a1, a1_ibuf_interface_I.getSingleBitPort(0), a1_ibuf);
				EdifPortRef a1_ibuf_O = new EdifPortRef(a1_c, a1_ibuf_interface_O.getSingleBitPort(0), a1_ibuf);
				EdifPortRef a2_ibuf_I = new EdifPortRef(a2, a2_ibuf_interface_I.getSingleBitPort(0), a2_ibuf);
				EdifPortRef a2_ibuf_O = new EdifPortRef(a2_c, a2_ibuf_interface_O.getSingleBitPort(0), a2_ibuf);
				EdifPortRef a3_ibuf_I = new EdifPortRef(a3, a3_ibuf_interface_I.getSingleBitPort(0), a3_ibuf);
				EdifPortRef a3_ibuf_O = new EdifPortRef(a3_c, a3_ibuf_interface_O.getSingleBitPort(0), a3_ibuf);
				EdifPortRef q_obuf_I = new EdifPortRef(q_c, q_obuf_interface_I.getSingleBitPort(0), q_obuf);
				EdifPortRef q_obuf_O = new EdifPortRef(q, q_obuf_interface_O.getSingleBitPort(0), q_obuf);
				EdifPortRef FDE1_C = new EdifPortRef(clk_c, FDE1_interface_C.getSingleBitPort(0), FDE1);
				EdifPortRef FDE1_D = new EdifPortRef(d_c, FDE1_interface_D.getSingleBitPort(0), FDE1);
				EdifPortRef FDE1_CE = new EdifPortRef(ce_c, FDE1_interface_CE.getSingleBitPort(0), FDE1);
				EdifPortRef FDE1_Q = new EdifPortRef(wire1, FDE1_interface_Q.getSingleBitPort(0), FDE1);
				EdifPortRef FDE2_C = new EdifPortRef(clk_c, FDE2_interface_C.getSingleBitPort(0), FDE2);
				EdifPortRef FDE2_D = new EdifPortRef(wire1, FDE2_interface_D.getSingleBitPort(0), FDE2);
				EdifPortRef FDE2_CE = new EdifPortRef(ce_c, FDE2_interface_CE.getSingleBitPort(0), FDE2);
				EdifPortRef FDE2_Q = new EdifPortRef(wire2, FDE2_interface_Q.getSingleBitPort(0), FDE2);
				EdifPortRef FDE3_C = new EdifPortRef(clk_c, FDE3_interface_C.getSingleBitPort(0), FDE3);
				EdifPortRef FDE3_D = new EdifPortRef(wire2, FDE3_interface_D.getSingleBitPort(0), FDE3);
				EdifPortRef FDE3_CE = new EdifPortRef(ce_c, FDE3_interface_CE.getSingleBitPort(0), FDE3);
				EdifPortRef FDE3_Q = new EdifPortRef(wire3, FDE3_interface_Q.getSingleBitPort(0), FDE3);
				EdifPortRef FDE4_C = new EdifPortRef(clk_c, FDE4_interface_C.getSingleBitPort(0), FDE4);
				EdifPortRef FDE4_D = new EdifPortRef(wire3, FDE4_interface_D.getSingleBitPort(0), FDE4);
				EdifPortRef FDE4_CE = new EdifPortRef(ce_c, FDE4_interface_CE.getSingleBitPort(0), FDE4);
				EdifPortRef FDE4_Q = new EdifPortRef(wire4, FDE4_interface_Q.getSingleBitPort(0), FDE4);
				EdifPortRef FDE5_C = new EdifPortRef(clk_c, FDE5_interface_C.getSingleBitPort(0), FDE5);
				EdifPortRef FDE5_D = new EdifPortRef(wire4, FDE5_interface_D.getSingleBitPort(0), FDE5);
				EdifPortRef FDE5_CE = new EdifPortRef(ce_c, FDE5_interface_CE.getSingleBitPort(0), FDE5);
				EdifPortRef FDE5_Q = new EdifPortRef(wire5, FDE5_interface_Q.getSingleBitPort(0), FDE5);
				EdifPortRef FDE6_C = new EdifPortRef(clk_c, FDE6_interface_C.getSingleBitPort(0), FDE6);
				EdifPortRef FDE6_D = new EdifPortRef(wire5, FDE6_interface_D.getSingleBitPort(0), FDE6);
				EdifPortRef FDE6_CE = new EdifPortRef(ce_c, FDE6_interface_CE.getSingleBitPort(0), FDE6);
				EdifPortRef FDE6_Q = new EdifPortRef(wire6, FDE6_interface_Q.getSingleBitPort(0), FDE6);
				EdifPortRef FDE7_C = new EdifPortRef(clk_c, FDE7_interface_C.getSingleBitPort(0), FDE7);
				EdifPortRef FDE7_D = new EdifPortRef(wire6, FDE7_interface_D.getSingleBitPort(0), FDE7);
				EdifPortRef FDE7_CE = new EdifPortRef(ce_c, FDE7_interface_CE.getSingleBitPort(0), FDE7);
				EdifPortRef FDE7_Q = new EdifPortRef(wire7, FDE7_interface_Q.getSingleBitPort(0), FDE7);
				EdifPortRef FDE8_C = new EdifPortRef(clk_c, FDE8_interface_C.getSingleBitPort(0), FDE8);
				EdifPortRef FDE8_D = new EdifPortRef(wire7, FDE8_interface_D.getSingleBitPort(0), FDE8);
				EdifPortRef FDE8_CE = new EdifPortRef(ce_c, FDE8_interface_CE.getSingleBitPort(0), FDE8);
				EdifPortRef FDE8_Q = new EdifPortRef(wire8, FDE8_interface_Q.getSingleBitPort(0), FDE8);
				EdifPortRef FDE9_C = new EdifPortRef(clk_c, FDE9_interface_C.getSingleBitPort(0), FDE9);
				EdifPortRef FDE9_D = new EdifPortRef(wire8, FDE9_interface_D.getSingleBitPort(0), FDE9);
				EdifPortRef FDE9_CE = new EdifPortRef(ce_c, FDE9_interface_CE.getSingleBitPort(0), FDE9);
				EdifPortRef FDE9_Q = new EdifPortRef(wire9, FDE9_interface_Q.getSingleBitPort(0), FDE9);
				EdifPortRef FDE10_C = new EdifPortRef(clk_c, FDE10_interface_C.getSingleBitPort(0), FDE10);
				EdifPortRef FDE10_D = new EdifPortRef(wire9, FDE10_interface_D.getSingleBitPort(0), FDE10);
				EdifPortRef FDE10_CE = new EdifPortRef(ce_c, FDE10_interface_CE.getSingleBitPort(0), FDE10);
				EdifPortRef FDE10_Q = new EdifPortRef(wire10, FDE10_interface_Q.getSingleBitPort(0), FDE10);
				EdifPortRef FDE11_C = new EdifPortRef(clk_c, FDE11_interface_C.getSingleBitPort(0), FDE11);
				EdifPortRef FDE11_D = new EdifPortRef(wire10, FDE11_interface_D.getSingleBitPort(0), FDE11);
				EdifPortRef FDE11_CE = new EdifPortRef(ce_c, FDE11_interface_CE.getSingleBitPort(0), FDE11);
				EdifPortRef FDE11_Q = new EdifPortRef(wire11, FDE11_interface_Q.getSingleBitPort(0), FDE11);
				EdifPortRef FDE12_C = new EdifPortRef(clk_c, FDE12_interface_C.getSingleBitPort(0), FDE12);
				EdifPortRef FDE12_D = new EdifPortRef(wire11, FDE12_interface_D.getSingleBitPort(0), FDE12);
				EdifPortRef FDE12_CE = new EdifPortRef(ce_c, FDE12_interface_CE.getSingleBitPort(0), FDE12);
				EdifPortRef FDE12_Q = new EdifPortRef(wire12, FDE12_interface_Q.getSingleBitPort(0), FDE12);
				EdifPortRef FDE13_C = new EdifPortRef(clk_c, FDE13_interface_C.getSingleBitPort(0), FDE13);
				EdifPortRef FDE13_D = new EdifPortRef(wire12, FDE13_interface_D.getSingleBitPort(0), FDE13);
				EdifPortRef FDE13_CE = new EdifPortRef(ce_c, FDE13_interface_CE.getSingleBitPort(0), FDE13);
				EdifPortRef FDE13_Q = new EdifPortRef(wire13, FDE13_interface_Q.getSingleBitPort(0), FDE13);
				EdifPortRef FDE14_C = new EdifPortRef(clk_c, FDE14_interface_C.getSingleBitPort(0), FDE14);
				EdifPortRef FDE14_D = new EdifPortRef(wire13, FDE14_interface_D.getSingleBitPort(0), FDE14);
				EdifPortRef FDE14_CE = new EdifPortRef(ce_c, FDE14_interface_CE.getSingleBitPort(0), FDE14);
				EdifPortRef FDE14_Q = new EdifPortRef(wire14, FDE14_interface_Q.getSingleBitPort(0), FDE14);
				EdifPortRef FDE15_C = new EdifPortRef(clk_c, FDE15_interface_C.getSingleBitPort(0), FDE15);
				EdifPortRef FDE15_D = new EdifPortRef(wire14, FDE15_interface_D.getSingleBitPort(0), FDE15);
				EdifPortRef FDE15_CE = new EdifPortRef(ce_c, FDE15_interface_CE.getSingleBitPort(0), FDE15);
				EdifPortRef FDE15_Q = new EdifPortRef(wire15, FDE15_interface_Q.getSingleBitPort(0), FDE15);
				EdifPortRef FDE16_C = new EdifPortRef(clk_c, FDE16_interface_C.getSingleBitPort(0), FDE16);
				EdifPortRef FDE16_D = new EdifPortRef(wire15, FDE16_interface_D.getSingleBitPort(0), FDE16);
				EdifPortRef FDE16_CE = new EdifPortRef(ce_c, FDE16_interface_CE.getSingleBitPort(0), FDE16);
				EdifPortRef FDE16_Q = new EdifPortRef(wire16, FDE16_interface_Q.getSingleBitPort(0), FDE16);
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
				EdifPortRef LUT3_9_O = new EdifPortRef(q_c, LUT3_9_interface_O.getSingleBitPort(0), LUT3_9);
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
				EdifPortRef MUXF6_1_I0 = new EdifPortRef(net5, MUXF6_1_interface_I0.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_I1 = new EdifPortRef(net6, MUXF6_1_interface_I1.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_S = new EdifPortRef(a1_c, MUXF6_1_interface_S.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_O = new EdifPortRef(net13, MUXF6_1_interface_O.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_2_I0 = new EdifPortRef(net11, MUXF6_2_interface_I0.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_I1 = new EdifPortRef(net12, MUXF6_2_interface_I1.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_S = new EdifPortRef(a1_c, MUXF6_2_interface_S.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_O = new EdifPortRef(net14, MUXF6_2_interface_O.getSingleBitPort(0), MUXF6_2);
				//clk
				clk.addPortConnection(clk_ibuf_I);
				clk.addPortConnection(SRL16E_replacement_clk);
				//clk_c
				clk_c.addPortConnection(clk_ibuf_O);
				clk_c.addPortConnection(FDE1_C);
				clk_c.addPortConnection(FDE2_C);
				clk_c.addPortConnection(FDE3_C);
				clk_c.addPortConnection(FDE4_C);
				clk_c.addPortConnection(FDE5_C);
				clk_c.addPortConnection(FDE6_C);
				clk_c.addPortConnection(FDE7_C);
				clk_c.addPortConnection(FDE8_C);
				clk_c.addPortConnection(FDE9_C);
				clk_c.addPortConnection(FDE10_C);
				clk_c.addPortConnection(FDE11_C);
				clk_c.addPortConnection(FDE12_C);
				clk_c.addPortConnection(FDE13_C);
				clk_c.addPortConnection(FDE14_C);
				clk_c.addPortConnection(FDE15_C);
				clk_c.addPortConnection(FDE16_C);
				//ce
				ce.addPortConnection(ce_ibuf_I);
				ce.addPortConnection(SRL16E_replacement_ce);
				//ce_c
				ce_c.addPortConnection(ce_ibuf_O);
				ce_c.addPortConnection(FDE1_CE);
				ce_c.addPortConnection(FDE2_CE);
				ce_c.addPortConnection(FDE3_CE);
				ce_c.addPortConnection(FDE4_CE);
				ce_c.addPortConnection(FDE5_CE);
				ce_c.addPortConnection(FDE6_CE);
				ce_c.addPortConnection(FDE7_CE);
				ce_c.addPortConnection(FDE8_CE);
				ce_c.addPortConnection(FDE9_CE);
				ce_c.addPortConnection(FDE10_CE);
				ce_c.addPortConnection(FDE11_CE);
				ce_c.addPortConnection(FDE12_CE);
				ce_c.addPortConnection(FDE13_CE);
				ce_c.addPortConnection(FDE14_CE);
				ce_c.addPortConnection(FDE15_CE);
				ce_c.addPortConnection(FDE16_CE);
				//d
				d.addPortConnection(SRL16E_replacement_d);
				d.addPortConnection(d_ibuf_I);
				//d_c
				d_c.addPortConnection(d_ibuf_O);
				d_c.addPortConnection(FDE1_D);
				//a0
				a0.addPortConnection(SRL16E_replacement_a0);
				a0.addPortConnection(a0_ibuf_I);
				//a0_c
				a0_c.addPortConnection(a0_ibuf_O);
				a0_c.addPortConnection(LUT3_9_I2);
				//a1
				a1.addPortConnection(SRL16E_replacement_a1);
				a1.addPortConnection(a1_ibuf_I);
				//a1_c
				a1_c.addPortConnection(a1_ibuf_O);
				a1_c.addPortConnection(MUXF6_1_S);
				a1_c.addPortConnection(MUXF6_2_S);
				//a2
				a2.addPortConnection(SRL16E_replacement_a2);
				a2.addPortConnection(a2_ibuf_I);
				//a2_c
				a2_c.addPortConnection(a2_ibuf_O);
				a2_c.addPortConnection(MUXF5_1_S);
				a2_c.addPortConnection(MUXF5_2_S);
				a2_c.addPortConnection(MUXF5_3_S);
				a2_c.addPortConnection(MUXF5_4_S);
				//a3
				a3.addPortConnection(SRL16E_replacement_a3);
				a3.addPortConnection(a3_ibuf_I);
				//a3_c
				a3_c.addPortConnection(a3_ibuf_O);
				a3_c.addPortConnection(LUT3_1_I0);
				a3_c.addPortConnection(LUT3_2_I0);
				a3_c.addPortConnection(LUT3_3_I0);
				a3_c.addPortConnection(LUT3_4_I0);
				a3_c.addPortConnection(LUT3_5_I0);
				a3_c.addPortConnection(LUT3_6_I0);
				a3_c.addPortConnection(LUT3_7_I0);
				a3_c.addPortConnection(LUT3_8_I0);
				//q
				q.addPortConnection(SRL16E_replacement_q);
				q.addPortConnection(q_obuf_O);
				//q_c
				q_c.addPortConnection(q_obuf_I);
				q_c.addPortConnection(LUT3_9_O);
				//wire1
				wire1.addPortConnection(LUT3_1_I1);
				wire1.addPortConnection(FDE1_Q);
				wire1.addPortConnection(FDE2_D);
				//wire2
				wire2.addPortConnection(LUT3_5_I1);
				wire2.addPortConnection(FDE2_Q);
				wire2.addPortConnection(FDE3_D);
				//wire3
				wire3.addPortConnection(LUT3_3_I1);
				wire3.addPortConnection(FDE3_Q);
				wire3.addPortConnection(FDE4_D);
				//wire4
				wire4.addPortConnection(LUT3_7_I1);
				wire4.addPortConnection(FDE4_Q);
				wire4.addPortConnection(FDE5_D);
				//wire5
				wire5.addPortConnection(LUT3_2_I1);
				wire5.addPortConnection(FDE5_Q);
				wire5.addPortConnection(FDE6_D);
				//wire6
				wire6.addPortConnection(LUT3_6_I1);
				wire6.addPortConnection(FDE6_Q);
				wire6.addPortConnection(FDE7_D);
				//wire7
				wire7.addPortConnection(LUT3_4_I1);
				wire7.addPortConnection(FDE7_Q);
				wire7.addPortConnection(FDE8_D);
				//wire8
				wire8.addPortConnection(LUT3_8_I1);
				wire8.addPortConnection(FDE8_Q);
				wire8.addPortConnection(FDE9_D);
				//wire9
				wire9.addPortConnection(LUT3_1_I2);
				wire9.addPortConnection(FDE9_Q);
				wire9.addPortConnection(FDE10_D);
				//wire10
				wire10.addPortConnection(LUT3_5_I2);
				wire10.addPortConnection(FDE10_Q);
				wire10.addPortConnection(FDE11_D);
				//wire11
				wire11.addPortConnection(LUT3_3_I2);
				wire11.addPortConnection(FDE11_Q);
				wire11.addPortConnection(FDE12_D);
				//wire12
				wire12.addPortConnection(LUT3_7_I2);
				wire12.addPortConnection(FDE12_Q);
				wire12.addPortConnection(FDE13_D);
				//wire13
				wire13.addPortConnection(LUT3_2_I2);
				wire13.addPortConnection(FDE13_Q);
				wire13.addPortConnection(FDE14_D);
				//wire14
				wire14.addPortConnection(LUT3_6_I2);
				wire14.addPortConnection(FDE14_Q);
				wire14.addPortConnection(FDE15_D);
				//wire15
				wire15.addPortConnection(LUT3_4_I2);
				wire15.addPortConnection(FDE15_Q);
				wire15.addPortConnection(FDE16_D);
				//wire16
				wire16.addPortConnection(LUT3_8_I2);
				wire16.addPortConnection(FDE16_Q);
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
				
				/******Set INIT Property******/
				boolean isInit = false;
				int initCount = 0;
		        long initInt = 15;
		        StringTypedValue valueZero = new StringTypedValue("0");
		        StringTypedValue valueOne = new StringTypedValue("1");
		        StringTypedValue valueE4 = new StringTypedValue("E4");
		        StringTypedValue valueCA = new StringTypedValue("CA");
		        //Set INIT Property for FDE1
				PropertyList FDE1_propertylist = FDE1.getPropertyList();
				if (FDE1_propertylist != null) {
                    for (Property FDE1_property : FDE1_propertylist.values()) {
                        if (FDE1_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE1_property.setValue(valueOne);
                            else
                                FDE1_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE1.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE1.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE2
				PropertyList FDE2_propertylist = FDE2.getPropertyList();
				if (FDE2_propertylist != null) {
                    for (Property FDE2_property : FDE2_propertylist.values()) {
                        if (FDE2_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE2_property.setValue(valueOne);
                            else
                                FDE2_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE2.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE2.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE3
				PropertyList FDE3_propertylist = FDE3.getPropertyList();
				if (FDE3_propertylist != null) {
                    for (Property FDE3_property : FDE3_propertylist.values()) {
                        if (FDE3_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE3_property.setValue(valueOne);
                            else
                                FDE3_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE3.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE3.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE4
				PropertyList FDE4_propertylist = FDE4.getPropertyList();
				if (FDE4_propertylist != null) {
                    for (Property FDE4_property : FDE4_propertylist.values()) {
                        if (FDE4_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE4_property.setValue(valueOne);
                            else
                                FDE4_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE4.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE4.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE5
				PropertyList FDE5_propertylist = FDE5.getPropertyList();
				if (FDE5_propertylist != null) {
                    for (Property FDE5_property : FDE5_propertylist.values()) {
                        if (FDE5_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE5_property.setValue(valueOne);
                            else
                                FDE5_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE5.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE5.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE6
				PropertyList FDE6_propertylist = FDE6.getPropertyList();
				if (FDE6_propertylist != null) {
                    for (Property FDE6_property : FDE6_propertylist.values()) {
                        if (FDE6_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE6_property.setValue(valueOne);
                            else
                                FDE6_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE6.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE6.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE7
				PropertyList FDE7_propertylist = FDE7.getPropertyList();
				if (FDE7_propertylist != null) {
                    for (Property FDE7_property : FDE7_propertylist.values()) {
                        if (FDE7_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE7_property.setValue(valueOne);
                            else
                                FDE7_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE7.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE7.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE8
				PropertyList FDE8_propertylist = FDE8.getPropertyList();
				if (FDE8_propertylist != null) {
                    for (Property FDE8_property : FDE8_propertylist.values()) {
                        if (FDE8_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE8_property.setValue(valueOne);
                            else
                                FDE8_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE8.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE8.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE9
				PropertyList FDE9_propertylist = FDE9.getPropertyList();
				if (FDE9_propertylist != null) {
                    for (Property FDE9_property : FDE9_propertylist.values()) {
                        if (FDE9_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE9_property.setValue(valueOne);
                            else
                                FDE9_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE9.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE9.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE10
				PropertyList FDE10_propertylist = FDE10.getPropertyList();
				if (FDE10_propertylist != null) {
                    for (Property FDE10_property : FDE10_propertylist.values()) {
                        if (FDE10_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE10_property.setValue(valueOne);
                            else
                                FDE10_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE10.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE10.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE11
				PropertyList FDE11_propertylist = FDE11.getPropertyList();
				if (FDE11_propertylist != null) {
                    for (Property FDE11_property : FDE11_propertylist.values()) {
                        if (FDE11_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE11_property.setValue(valueOne);
                            else
                                FDE11_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE11.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE11.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE12
				PropertyList FDE12_propertylist = FDE12.getPropertyList();
				if (FDE12_propertylist != null) {
                    for (Property FDE12_property : FDE12_propertylist.values()) {
                        if (FDE12_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE12_property.setValue(valueOne);
                            else
                                FDE12_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE12.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE12.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE13
				PropertyList FDE13_propertylist = FDE13.getPropertyList();
				if (FDE13_propertylist != null) {
                    for (Property FDE13_property : FDE13_propertylist.values()) {
                        if (FDE13_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE13_property.setValue(valueOne);
                            else
                                FDE13_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE13.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE13.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE14
				PropertyList FDE14_propertylist = FDE14.getPropertyList();
				if (FDE14_propertylist != null) {
                    for (Property FDE14_property : FDE14_propertylist.values()) {
                        if (FDE14_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE14_property.setValue(valueOne);
                            else
                                FDE14_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE14.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE14.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE15
				PropertyList FDE15_propertylist = FDE15.getPropertyList();
				if (FDE15_propertylist != null) {
                    for (Property FDE15_property : FDE15_propertylist.values()) {
                        if (FDE15_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE15_property.setValue(valueOne);
                            else
                                FDE15_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE15.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE15.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE16
				PropertyList FDE16_propertylist = FDE16.getPropertyList();
				if (FDE16_propertylist != null) {
                    for (Property FDE16_property : FDE16_propertylist.values()) {
                        if (FDE16_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE16_property.setValue(valueOne);
                            else
                                FDE16_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE16.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE16.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for LUT3_1
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
				System.out.println("FileNotFoundException caught");
			}
			
		} catch (InvalidEdifNameException e){
			System.out.println("InvalidEdifNameException caught");
			//Should not happen
		}
	}
	
	public static void Replace(EdifLibraryManager libManager, EdifCell SRL16E_replacement, long INIT,
			EdifNet d, EdifNet ce, EdifNet clk, EdifNet a0, EdifNet a1, EdifNet a2, EdifNet a3, EdifNet q){
		try{
			try{
				/******Cells******/
				EdifCell FDE = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FDE");
				EdifCell LUT3 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "LUT3");
				EdifCell MUXF5 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "MUXF5");
				EdifCell MUXF6 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "MUXF6");	
				
				/******Nets******/
				EdifNet wire1 = new EdifNet("wire1", SRL16E_replacement);
				EdifNet wire2 = new EdifNet("wire2", SRL16E_replacement);
				EdifNet wire3 = new EdifNet("wire3", SRL16E_replacement);
				EdifNet wire4 = new EdifNet("wire4", SRL16E_replacement);
				EdifNet wire5 = new EdifNet("wire5", SRL16E_replacement);
				EdifNet wire6 = new EdifNet("wire6", SRL16E_replacement);
				EdifNet wire7 = new EdifNet("wire7", SRL16E_replacement);
				EdifNet wire8 = new EdifNet("wire8", SRL16E_replacement);
				EdifNet wire9 = new EdifNet("wire9", SRL16E_replacement);
				EdifNet wire10 = new EdifNet("wire10", SRL16E_replacement);
				EdifNet wire11 = new EdifNet("wire11", SRL16E_replacement);
				EdifNet wire12 = new EdifNet("wire12", SRL16E_replacement);
				EdifNet wire13 = new EdifNet("wire13", SRL16E_replacement);
				EdifNet wire14 = new EdifNet("wire14", SRL16E_replacement);
				EdifNet wire15 = new EdifNet("wire15", SRL16E_replacement);
				EdifNet wire16 = new EdifNet("wire16", SRL16E_replacement);
				EdifNet net1 = new EdifNet("net1", SRL16E_replacement);
				EdifNet net2 = new EdifNet("net2", SRL16E_replacement);
				EdifNet net3 = new EdifNet("net3", SRL16E_replacement);
				EdifNet net4 = new EdifNet("net4", SRL16E_replacement);
				EdifNet net5 = new EdifNet("net5", SRL16E_replacement);
				EdifNet net6 = new EdifNet("net6", SRL16E_replacement);
				EdifNet net7 = new EdifNet("net7", SRL16E_replacement);
				EdifNet net8 = new EdifNet("net8", SRL16E_replacement);
				EdifNet net9 = new EdifNet("net9", SRL16E_replacement);
				EdifNet net10 = new EdifNet("net10", SRL16E_replacement);
				EdifNet net11 = new EdifNet("net11", SRL16E_replacement);
				EdifNet net12 = new EdifNet("net12", SRL16E_replacement);
				EdifNet net13 = new EdifNet("net13", SRL16E_replacement);
				EdifNet net14 = new EdifNet("net14", SRL16E_replacement);
				SRL16E_replacement.addNet(wire1);
				SRL16E_replacement.addNet(wire2);
				SRL16E_replacement.addNet(wire3);
				SRL16E_replacement.addNet(wire4);
				SRL16E_replacement.addNet(wire5);
				SRL16E_replacement.addNet(wire6);
				SRL16E_replacement.addNet(wire7);
				SRL16E_replacement.addNet(wire8);
				SRL16E_replacement.addNet(wire9);
				SRL16E_replacement.addNet(wire10);
				SRL16E_replacement.addNet(wire11);
				SRL16E_replacement.addNet(wire12);
				SRL16E_replacement.addNet(wire13);
				SRL16E_replacement.addNet(wire14);
				SRL16E_replacement.addNet(wire15);
				SRL16E_replacement.addNet(wire16);
				SRL16E_replacement.addNet(net1);
				SRL16E_replacement.addNet(net2);
				SRL16E_replacement.addNet(net3);
				SRL16E_replacement.addNet(net4);
				SRL16E_replacement.addNet(net5);
				SRL16E_replacement.addNet(net6);
				SRL16E_replacement.addNet(net7);
				SRL16E_replacement.addNet(net8);
				SRL16E_replacement.addNet(net9);
				SRL16E_replacement.addNet(net10);
				SRL16E_replacement.addNet(net11);
				SRL16E_replacement.addNet(net12);
				SRL16E_replacement.addNet(net13);
				SRL16E_replacement.addNet(net14);
				
				/******Instances******/
				EdifCellInstance FDE1 = new EdifCellInstance("FDE1", SRL16E_replacement, FDE);
				EdifCellInstance FDE2 = new EdifCellInstance("FDE2", SRL16E_replacement, FDE);
				EdifCellInstance FDE3 = new EdifCellInstance("FDE3", SRL16E_replacement, FDE);
				EdifCellInstance FDE4 = new EdifCellInstance("FDE4", SRL16E_replacement, FDE);
				EdifCellInstance FDE5 = new EdifCellInstance("FDE5", SRL16E_replacement, FDE);
				EdifCellInstance FDE6 = new EdifCellInstance("FDE6", SRL16E_replacement, FDE);
				EdifCellInstance FDE7 = new EdifCellInstance("FDE7", SRL16E_replacement, FDE);
				EdifCellInstance FDE8 = new EdifCellInstance("FDE8", SRL16E_replacement, FDE);
				EdifCellInstance FDE9 = new EdifCellInstance("FDE9", SRL16E_replacement, FDE);
				EdifCellInstance FDE10 = new EdifCellInstance("FDE10", SRL16E_replacement, FDE);
				EdifCellInstance FDE11 = new EdifCellInstance("FDE11", SRL16E_replacement, FDE);
				EdifCellInstance FDE12 = new EdifCellInstance("FDE12", SRL16E_replacement, FDE);
				EdifCellInstance FDE13 = new EdifCellInstance("FDE13", SRL16E_replacement, FDE);
				EdifCellInstance FDE14 = new EdifCellInstance("FDE14", SRL16E_replacement, FDE);
				EdifCellInstance FDE15 = new EdifCellInstance("FDE15", SRL16E_replacement, FDE);
				EdifCellInstance FDE16 = new EdifCellInstance("FDE16", SRL16E_replacement, FDE);
				EdifCellInstance LUT3_1 = new EdifCellInstance("LUT3_1", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_2 = new EdifCellInstance("LUT3_2", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_3 = new EdifCellInstance("LUT3_3", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_4 = new EdifCellInstance("LUT3_4", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_5 = new EdifCellInstance("LUT3_5", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_6 = new EdifCellInstance("LUT3_6", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_7 = new EdifCellInstance("LUT3_7", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_8 = new EdifCellInstance("LUT3_8", SRL16E_replacement, LUT3);
				EdifCellInstance LUT3_9 = new EdifCellInstance("LUT3_9", SRL16E_replacement, LUT3);
				EdifCellInstance MUXF5_1 = new EdifCellInstance("MUXF5_1", SRL16E_replacement, MUXF5);
				EdifCellInstance MUXF5_2 = new EdifCellInstance("MUXF5_2", SRL16E_replacement, MUXF5);
				EdifCellInstance MUXF5_3 = new EdifCellInstance("MUXF5_3", SRL16E_replacement, MUXF5);
				EdifCellInstance MUXF5_4 = new EdifCellInstance("MUXF5_4", SRL16E_replacement, MUXF5);
				EdifCellInstance MUXF6_1 = new EdifCellInstance("MUXF6_1", SRL16E_replacement, MUXF6);
				EdifCellInstance MUXF6_2 = new EdifCellInstance("MUXF6_2", SRL16E_replacement, MUXF6);
				SRL16E_replacement.addSubCell(FDE1);
				SRL16E_replacement.addSubCell(FDE2);
				SRL16E_replacement.addSubCell(FDE3);
				SRL16E_replacement.addSubCell(FDE4);
				SRL16E_replacement.addSubCell(FDE5);
				SRL16E_replacement.addSubCell(FDE6);
				SRL16E_replacement.addSubCell(FDE7);
				SRL16E_replacement.addSubCell(FDE8);
				SRL16E_replacement.addSubCell(FDE9);
				SRL16E_replacement.addSubCell(FDE10);
				SRL16E_replacement.addSubCell(FDE11);
				SRL16E_replacement.addSubCell(FDE12);
				SRL16E_replacement.addSubCell(FDE13);
				SRL16E_replacement.addSubCell(FDE14);
				SRL16E_replacement.addSubCell(FDE15);
				SRL16E_replacement.addSubCell(FDE16);
				SRL16E_replacement.addSubCell(LUT3_1);
				SRL16E_replacement.addSubCell(LUT3_2);
				SRL16E_replacement.addSubCell(LUT3_3);
				SRL16E_replacement.addSubCell(LUT3_4);
				SRL16E_replacement.addSubCell(LUT3_5);
				SRL16E_replacement.addSubCell(LUT3_6);
				SRL16E_replacement.addSubCell(LUT3_7);
				SRL16E_replacement.addSubCell(LUT3_8);
				SRL16E_replacement.addSubCell(LUT3_9);
				SRL16E_replacement.addSubCell(MUXF5_1);
				SRL16E_replacement.addSubCell(MUXF5_2);
				SRL16E_replacement.addSubCell(MUXF5_3);
				SRL16E_replacement.addSubCell(MUXF5_4);
				SRL16E_replacement.addSubCell(MUXF6_1);
				SRL16E_replacement.addSubCell(MUXF6_2);
				
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
				EdifCellInterface LUT3_1_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_2_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_3_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_4_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_5_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_6_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_7_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_8_interface = new EdifCellInterface(LUT3);
				EdifCellInterface LUT3_9_interface = new EdifCellInterface(LUT3);
				EdifCellInterface MUXF5_1_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_2_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_3_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF5_4_interface = new EdifCellInterface(MUXF5);
				EdifCellInterface MUXF6_1_interface = new EdifCellInterface(MUXF6);
				EdifCellInterface MUXF6_2_interface = new EdifCellInterface(MUXF6);
				
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
				
				/******PortRefs******/
				EdifPortRef FDE1_C = new EdifPortRef(clk, FDE1_interface_C.getSingleBitPort(0), FDE1);
				EdifPortRef FDE1_D = new EdifPortRef(d, FDE1_interface_D.getSingleBitPort(0), FDE1);
				EdifPortRef FDE1_CE = new EdifPortRef(ce, FDE1_interface_CE.getSingleBitPort(0), FDE1);
				EdifPortRef FDE1_Q = new EdifPortRef(wire1, FDE1_interface_Q.getSingleBitPort(0), FDE1);
				EdifPortRef FDE2_C = new EdifPortRef(clk, FDE2_interface_C.getSingleBitPort(0), FDE2);
				EdifPortRef FDE2_D = new EdifPortRef(wire1, FDE2_interface_D.getSingleBitPort(0), FDE2);
				EdifPortRef FDE2_CE = new EdifPortRef(ce, FDE2_interface_CE.getSingleBitPort(0), FDE2);
				EdifPortRef FDE2_Q = new EdifPortRef(wire2, FDE2_interface_Q.getSingleBitPort(0), FDE2);
				EdifPortRef FDE3_C = new EdifPortRef(clk, FDE3_interface_C.getSingleBitPort(0), FDE3);
				EdifPortRef FDE3_D = new EdifPortRef(wire2, FDE3_interface_D.getSingleBitPort(0), FDE3);
				EdifPortRef FDE3_CE = new EdifPortRef(ce, FDE3_interface_CE.getSingleBitPort(0), FDE3);
				EdifPortRef FDE3_Q = new EdifPortRef(wire3, FDE3_interface_Q.getSingleBitPort(0), FDE3);
				EdifPortRef FDE4_C = new EdifPortRef(clk, FDE4_interface_C.getSingleBitPort(0), FDE4);
				EdifPortRef FDE4_D = new EdifPortRef(wire3, FDE4_interface_D.getSingleBitPort(0), FDE4);
				EdifPortRef FDE4_CE = new EdifPortRef(ce, FDE4_interface_CE.getSingleBitPort(0), FDE4);
				EdifPortRef FDE4_Q = new EdifPortRef(wire4, FDE4_interface_Q.getSingleBitPort(0), FDE4);
				EdifPortRef FDE5_C = new EdifPortRef(clk, FDE5_interface_C.getSingleBitPort(0), FDE5);
				EdifPortRef FDE5_D = new EdifPortRef(wire4, FDE5_interface_D.getSingleBitPort(0), FDE5);
				EdifPortRef FDE5_CE = new EdifPortRef(ce, FDE5_interface_CE.getSingleBitPort(0), FDE5);
				EdifPortRef FDE5_Q = new EdifPortRef(wire5, FDE5_interface_Q.getSingleBitPort(0), FDE5);
				EdifPortRef FDE6_C = new EdifPortRef(clk, FDE6_interface_C.getSingleBitPort(0), FDE6);
				EdifPortRef FDE6_D = new EdifPortRef(wire5, FDE6_interface_D.getSingleBitPort(0), FDE6);
				EdifPortRef FDE6_CE = new EdifPortRef(ce, FDE6_interface_CE.getSingleBitPort(0), FDE6);
				EdifPortRef FDE6_Q = new EdifPortRef(wire6, FDE6_interface_Q.getSingleBitPort(0), FDE6);
				EdifPortRef FDE7_C = new EdifPortRef(clk, FDE7_interface_C.getSingleBitPort(0), FDE7);
				EdifPortRef FDE7_D = new EdifPortRef(wire6, FDE7_interface_D.getSingleBitPort(0), FDE7);
				EdifPortRef FDE7_CE = new EdifPortRef(ce, FDE7_interface_CE.getSingleBitPort(0), FDE7);
				EdifPortRef FDE7_Q = new EdifPortRef(wire7, FDE7_interface_Q.getSingleBitPort(0), FDE7);
				EdifPortRef FDE8_C = new EdifPortRef(clk, FDE8_interface_C.getSingleBitPort(0), FDE8);
				EdifPortRef FDE8_D = new EdifPortRef(wire7, FDE8_interface_D.getSingleBitPort(0), FDE8);
				EdifPortRef FDE8_CE = new EdifPortRef(ce, FDE8_interface_CE.getSingleBitPort(0), FDE8);
				EdifPortRef FDE8_Q = new EdifPortRef(wire8, FDE8_interface_Q.getSingleBitPort(0), FDE8);
				EdifPortRef FDE9_C = new EdifPortRef(clk, FDE9_interface_C.getSingleBitPort(0), FDE9);
				EdifPortRef FDE9_D = new EdifPortRef(wire8, FDE9_interface_D.getSingleBitPort(0), FDE9);
				EdifPortRef FDE9_CE = new EdifPortRef(ce, FDE9_interface_CE.getSingleBitPort(0), FDE9);
				EdifPortRef FDE9_Q = new EdifPortRef(wire9, FDE9_interface_Q.getSingleBitPort(0), FDE9);
				EdifPortRef FDE10_C = new EdifPortRef(clk, FDE10_interface_C.getSingleBitPort(0), FDE10);
				EdifPortRef FDE10_D = new EdifPortRef(wire9, FDE10_interface_D.getSingleBitPort(0), FDE10);
				EdifPortRef FDE10_CE = new EdifPortRef(ce, FDE10_interface_CE.getSingleBitPort(0), FDE10);
				EdifPortRef FDE10_Q = new EdifPortRef(wire10, FDE10_interface_Q.getSingleBitPort(0), FDE10);
				EdifPortRef FDE11_C = new EdifPortRef(clk, FDE11_interface_C.getSingleBitPort(0), FDE11);
				EdifPortRef FDE11_D = new EdifPortRef(wire10, FDE11_interface_D.getSingleBitPort(0), FDE11);
				EdifPortRef FDE11_CE = new EdifPortRef(ce, FDE11_interface_CE.getSingleBitPort(0), FDE11);
				EdifPortRef FDE11_Q = new EdifPortRef(wire11, FDE11_interface_Q.getSingleBitPort(0), FDE11);
				EdifPortRef FDE12_C = new EdifPortRef(clk, FDE12_interface_C.getSingleBitPort(0), FDE12);
				EdifPortRef FDE12_D = new EdifPortRef(wire11, FDE12_interface_D.getSingleBitPort(0), FDE12);
				EdifPortRef FDE12_CE = new EdifPortRef(ce, FDE12_interface_CE.getSingleBitPort(0), FDE12);
				EdifPortRef FDE12_Q = new EdifPortRef(wire12, FDE12_interface_Q.getSingleBitPort(0), FDE12);
				EdifPortRef FDE13_C = new EdifPortRef(clk, FDE13_interface_C.getSingleBitPort(0), FDE13);
				EdifPortRef FDE13_D = new EdifPortRef(wire12, FDE13_interface_D.getSingleBitPort(0), FDE13);
				EdifPortRef FDE13_CE = new EdifPortRef(ce, FDE13_interface_CE.getSingleBitPort(0), FDE13);
				EdifPortRef FDE13_Q = new EdifPortRef(wire13, FDE13_interface_Q.getSingleBitPort(0), FDE13);
				EdifPortRef FDE14_C = new EdifPortRef(clk, FDE14_interface_C.getSingleBitPort(0), FDE14);
				EdifPortRef FDE14_D = new EdifPortRef(wire13, FDE14_interface_D.getSingleBitPort(0), FDE14);
				EdifPortRef FDE14_CE = new EdifPortRef(ce, FDE14_interface_CE.getSingleBitPort(0), FDE14);
				EdifPortRef FDE14_Q = new EdifPortRef(wire14, FDE14_interface_Q.getSingleBitPort(0), FDE14);
				EdifPortRef FDE15_C = new EdifPortRef(clk, FDE15_interface_C.getSingleBitPort(0), FDE15);
				EdifPortRef FDE15_D = new EdifPortRef(wire14, FDE15_interface_D.getSingleBitPort(0), FDE15);
				EdifPortRef FDE15_CE = new EdifPortRef(ce, FDE15_interface_CE.getSingleBitPort(0), FDE15);
				EdifPortRef FDE15_Q = new EdifPortRef(wire15, FDE15_interface_Q.getSingleBitPort(0), FDE15);
				EdifPortRef FDE16_C = new EdifPortRef(clk, FDE16_interface_C.getSingleBitPort(0), FDE16);
				EdifPortRef FDE16_D = new EdifPortRef(wire15, FDE16_interface_D.getSingleBitPort(0), FDE16);
				EdifPortRef FDE16_CE = new EdifPortRef(ce, FDE16_interface_CE.getSingleBitPort(0), FDE16);
				EdifPortRef FDE16_Q = new EdifPortRef(wire16, FDE16_interface_Q.getSingleBitPort(0), FDE16);
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
				EdifPortRef LUT3_9_O = new EdifPortRef(q, LUT3_9_interface_O.getSingleBitPort(0), LUT3_9);
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
				EdifPortRef MUXF6_1_I0 = new EdifPortRef(net5, MUXF6_1_interface_I0.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_I1 = new EdifPortRef(net6, MUXF6_1_interface_I1.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_S = new EdifPortRef(a1, MUXF6_1_interface_S.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_O = new EdifPortRef(net13, MUXF6_1_interface_O.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_2_I0 = new EdifPortRef(net11, MUXF6_2_interface_I0.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_I1 = new EdifPortRef(net12, MUXF6_2_interface_I1.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_S = new EdifPortRef(a1, MUXF6_2_interface_S.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_O = new EdifPortRef(net14, MUXF6_2_interface_O.getSingleBitPort(0), MUXF6_2);
				//clk
				clk.addPortConnection(FDE1_C);
				clk.addPortConnection(FDE2_C);
				clk.addPortConnection(FDE3_C);
				clk.addPortConnection(FDE4_C);
				clk.addPortConnection(FDE5_C);
				clk.addPortConnection(FDE6_C);
				clk.addPortConnection(FDE7_C);
				clk.addPortConnection(FDE8_C);
				clk.addPortConnection(FDE9_C);
				clk.addPortConnection(FDE10_C);
				clk.addPortConnection(FDE11_C);
				clk.addPortConnection(FDE12_C);
				clk.addPortConnection(FDE13_C);
				clk.addPortConnection(FDE14_C);
				clk.addPortConnection(FDE15_C);
				clk.addPortConnection(FDE16_C);
				//ce
				ce.addPortConnection(FDE1_CE);
				ce.addPortConnection(FDE2_CE);
				ce.addPortConnection(FDE3_CE);
				ce.addPortConnection(FDE4_CE);
				ce.addPortConnection(FDE5_CE);
				ce.addPortConnection(FDE6_CE);
				ce.addPortConnection(FDE7_CE);
				ce.addPortConnection(FDE8_CE);
				ce.addPortConnection(FDE9_CE);
				ce.addPortConnection(FDE10_CE);
				ce.addPortConnection(FDE11_CE);
				ce.addPortConnection(FDE12_CE);
				ce.addPortConnection(FDE13_CE);
				ce.addPortConnection(FDE14_CE);
				ce.addPortConnection(FDE15_CE);
				ce.addPortConnection(FDE16_CE);
				//d
				d.addPortConnection(FDE1_D);
				//a0
				a0.addPortConnection(LUT3_9_I2);
				//a1
				a1.addPortConnection(MUXF6_1_S);
				a1.addPortConnection(MUXF6_2_S);
				//a2
				a2.addPortConnection(MUXF5_1_S);
				a2.addPortConnection(MUXF5_2_S);
				a2.addPortConnection(MUXF5_3_S);
				a2.addPortConnection(MUXF5_4_S);
				//a3
				a3.addPortConnection(LUT3_1_I0);
				a3.addPortConnection(LUT3_2_I0);
				a3.addPortConnection(LUT3_3_I0);
				a3.addPortConnection(LUT3_4_I0);
				a3.addPortConnection(LUT3_5_I0);
				a3.addPortConnection(LUT3_6_I0);
				a3.addPortConnection(LUT3_7_I0);
				a3.addPortConnection(LUT3_8_I0);
				//q
				q.addPortConnection(LUT3_9_O);
				//wire1
				wire1.addPortConnection(LUT3_1_I1);
				wire1.addPortConnection(FDE1_Q);
				wire1.addPortConnection(FDE2_D);
				//wire2
				wire2.addPortConnection(LUT3_5_I1);
				wire2.addPortConnection(FDE2_Q);
				wire2.addPortConnection(FDE3_D);
				//wire3
				wire3.addPortConnection(LUT3_3_I1);
				wire3.addPortConnection(FDE3_Q);
				wire3.addPortConnection(FDE4_D);
				//wire4
				wire4.addPortConnection(LUT3_7_I1);
				wire4.addPortConnection(FDE4_Q);
				wire4.addPortConnection(FDE5_D);
				//wire5
				wire5.addPortConnection(LUT3_2_I1);
				wire5.addPortConnection(FDE5_Q);
				wire5.addPortConnection(FDE6_D);
				//wire6
				wire6.addPortConnection(LUT3_6_I1);
				wire6.addPortConnection(FDE6_Q);
				wire6.addPortConnection(FDE7_D);
				//wire7
				wire7.addPortConnection(LUT3_4_I1);
				wire7.addPortConnection(FDE7_Q);
				wire7.addPortConnection(FDE8_D);
				//wire8
				wire8.addPortConnection(LUT3_8_I1);
				wire8.addPortConnection(FDE8_Q);
				wire8.addPortConnection(FDE9_D);
				//wire9
				wire9.addPortConnection(LUT3_1_I2);
				wire9.addPortConnection(FDE9_Q);
				wire9.addPortConnection(FDE10_D);
				//wire10
				wire10.addPortConnection(LUT3_5_I2);
				wire10.addPortConnection(FDE10_Q);
				wire10.addPortConnection(FDE11_D);
				//wire11
				wire11.addPortConnection(LUT3_3_I2);
				wire11.addPortConnection(FDE11_Q);
				wire11.addPortConnection(FDE12_D);
				//wire12
				wire12.addPortConnection(LUT3_7_I2);
				wire12.addPortConnection(FDE12_Q);
				wire12.addPortConnection(FDE13_D);
				//wire13
				wire13.addPortConnection(LUT3_2_I2);
				wire13.addPortConnection(FDE13_Q);
				wire13.addPortConnection(FDE14_D);
				//wire14
				wire14.addPortConnection(LUT3_6_I2);
				wire14.addPortConnection(FDE14_Q);
				wire14.addPortConnection(FDE15_D);
				//wire15
				wire15.addPortConnection(LUT3_4_I2);
				wire15.addPortConnection(FDE15_Q);
				wire15.addPortConnection(FDE16_D);
				//wire16
				wire16.addPortConnection(LUT3_8_I2);
				wire16.addPortConnection(FDE16_Q);
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
				
				/******Set INIT Property******/
				boolean isInit = false;
				int initCount = 0;
		        StringTypedValue valueZero = new StringTypedValue("0");
		        StringTypedValue valueOne = new StringTypedValue("1");
		        StringTypedValue valueE4 = new StringTypedValue("E4");
		        StringTypedValue valueCA = new StringTypedValue("CA");
		        //Set INIT Property for FDE1
				PropertyList FDE1_propertylist = FDE1.getPropertyList();
				if (FDE1_propertylist != null) {
                    for (Property FDE1_property : FDE1_propertylist.values()) {
                        if (FDE1_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE1_property.setValue(valueOne);
                            else
                                FDE1_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE1.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE1.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE2
				PropertyList FDE2_propertylist = FDE2.getPropertyList();
				if (FDE2_propertylist != null) {
                    for (Property FDE2_property : FDE2_propertylist.values()) {
                        if (FDE2_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE2_property.setValue(valueOne);
                            else
                                FDE2_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE2.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE2.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE3
				PropertyList FDE3_propertylist = FDE3.getPropertyList();
				if (FDE3_propertylist != null) {
                    for (Property FDE3_property : FDE3_propertylist.values()) {
                        if (FDE3_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE3_property.setValue(valueOne);
                            else
                                FDE3_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE3.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE3.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE4
				PropertyList FDE4_propertylist = FDE4.getPropertyList();
				if (FDE4_propertylist != null) {
                    for (Property FDE4_property : FDE4_propertylist.values()) {
                        if (FDE4_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE4_property.setValue(valueOne);
                            else
                                FDE4_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE4.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE4.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE5
				PropertyList FDE5_propertylist = FDE5.getPropertyList();
				if (FDE5_propertylist != null) {
                    for (Property FDE5_property : FDE5_propertylist.values()) {
                        if (FDE5_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE5_property.setValue(valueOne);
                            else
                                FDE5_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE5.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE5.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE6
				PropertyList FDE6_propertylist = FDE6.getPropertyList();
				if (FDE6_propertylist != null) {
                    for (Property FDE6_property : FDE6_propertylist.values()) {
                        if (FDE6_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE6_property.setValue(valueOne);
                            else
                                FDE6_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE6.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE6.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE7
				PropertyList FDE7_propertylist = FDE7.getPropertyList();
				if (FDE7_propertylist != null) {
                    for (Property FDE7_property : FDE7_propertylist.values()) {
                        if (FDE7_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE7_property.setValue(valueOne);
                            else
                                FDE7_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE7.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE7.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE8
				PropertyList FDE8_propertylist = FDE8.getPropertyList();
				if (FDE8_propertylist != null) {
                    for (Property FDE8_property : FDE8_propertylist.values()) {
                        if (FDE8_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE8_property.setValue(valueOne);
                            else
                                FDE8_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE8.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE8.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE9
				PropertyList FDE9_propertylist = FDE9.getPropertyList();
				if (FDE9_propertylist != null) {
                    for (Property FDE9_property : FDE9_propertylist.values()) {
                        if (FDE9_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE9_property.setValue(valueOne);
                            else
                                FDE9_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE9.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE9.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE10
				PropertyList FDE10_propertylist = FDE10.getPropertyList();
				if (FDE10_propertylist != null) {
                    for (Property FDE10_property : FDE10_propertylist.values()) {
                        if (FDE10_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE10_property.setValue(valueOne);
                            else
                                FDE10_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE10.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE10.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE11
				PropertyList FDE11_propertylist = FDE11.getPropertyList();
				if (FDE11_propertylist != null) {
                    for (Property FDE11_property : FDE11_propertylist.values()) {
                        if (FDE11_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE11_property.setValue(valueOne);
                            else
                                FDE11_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE11.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE11.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE12
				PropertyList FDE12_propertylist = FDE12.getPropertyList();
				if (FDE12_propertylist != null) {
                    for (Property FDE12_property : FDE12_propertylist.values()) {
                        if (FDE12_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE12_property.setValue(valueOne);
                            else
                                FDE12_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE12.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE12.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE13
				PropertyList FDE13_propertylist = FDE13.getPropertyList();
				if (FDE13_propertylist != null) {
                    for (Property FDE13_property : FDE13_propertylist.values()) {
                        if (FDE13_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE13_property.setValue(valueOne);
                            else
                                FDE13_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE13.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE13.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE14
				PropertyList FDE14_propertylist = FDE14.getPropertyList();
				if (FDE14_propertylist != null) {
                    for (Property FDE14_property : FDE14_propertylist.values()) {
                        if (FDE14_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE14_property.setValue(valueOne);
                            else
                                FDE14_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE14.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE14.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE15
				PropertyList FDE15_propertylist = FDE15.getPropertyList();
				if (FDE15_propertylist != null) {
                    for (Property FDE15_property : FDE15_propertylist.values()) {
                        if (FDE15_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE15_property.setValue(valueOne);
                            else
                                FDE15_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE15.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE15.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE16
				PropertyList FDE16_propertylist = FDE16.getPropertyList();
				if (FDE16_propertylist != null) {
                    for (Property FDE16_property : FDE16_propertylist.values()) {
                        if (FDE16_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE16_property.setValue(valueOne);
                            else
                                FDE16_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE16.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE16.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for LUT3_1
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
			} catch (EdifNameConflictException e){
				System.out.println("EdifNameConflictException caught"+e);
				//Should not happen
			}	
		} catch (InvalidEdifNameException e){
			System.out.println("InvalidEdifNameException caught");
			//Should not happen
		}
	}
}
