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

public class SRLC16_replacement{
	public static void main(String args[]){
		
		String outputFileName = "SRLC16_replacement.edf";
		
		try{
			/******Environment and Environment manager******/
			EdifEnvironment topLevel = new EdifEnvironment("SRLC16_replacement");
			EdifLibraryManager manager = new EdifLibraryManager(topLevel);
			try{
				/******Libraries******/
				EdifLibrary work = new EdifLibrary(manager, "work");
				topLevel.addLibrary(work);
				
				/******Cells******/
				EdifCell BUFGP = XilinxLibrary.findOrAddXilinxPrimitive(manager, "BUFGP");
				EdifCell IBUF = XilinxLibrary.findOrAddXilinxPrimitive(manager, "IBUF");
				EdifCell OBUF = XilinxLibrary.findOrAddXilinxPrimitive(manager, "OBUF");
				EdifCell FD = XilinxLibrary.findOrAddXilinxPrimitive(manager, "FD");
				EdifCell LUT3 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "LUT3");
				EdifCell MUXF5 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "MUXF5");
				EdifCell MUXF6 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "MUXF6");		
				EdifCell SRLC16_replacement = new EdifCell(work, "SRLC16_replacement");
				SRLC16_replacement.addPort("clk", 1, 1);
				SRLC16_replacement.addPort("d", 1, 1);
				SRLC16_replacement.addPort("a0", 1, 1);
				SRLC16_replacement.addPort("a1", 1, 1);
				SRLC16_replacement.addPort("a2", 1, 1);
				SRLC16_replacement.addPort("a3", 1, 1);
				SRLC16_replacement.addPort("q", 1, 2);
				SRLC16_replacement.addPort("q15", 1, 2);
				
				/******Design******/
				EdifDesign design = new EdifDesign("SRLC16_replacement");
				topLevel.setTopDesign(design);
				topLevel.setTopCell(SRLC16_replacement);
				
				/******Nets******/
				EdifNet clk_c = new EdifNet("clk_c", SRLC16_replacement);
				EdifNet clk = new EdifNet("clk", SRLC16_replacement);
				EdifNet d_c = new EdifNet("d_c", SRLC16_replacement);
				EdifNet d = new EdifNet("d", SRLC16_replacement);
				EdifNet a0_c = new EdifNet("a0_c", SRLC16_replacement);
				EdifNet a0 = new EdifNet("a0", SRLC16_replacement);
				EdifNet a1_c = new EdifNet("a1_c", SRLC16_replacement);
				EdifNet a1 = new EdifNet("a1", SRLC16_replacement);
				EdifNet a2_c = new EdifNet("a2_c", SRLC16_replacement);
				EdifNet a2 = new EdifNet("a2", SRLC16_replacement);
				EdifNet a3_c = new EdifNet("a3_c", SRLC16_replacement);
				EdifNet a3 = new EdifNet("a3", SRLC16_replacement);
				EdifNet q_c = new EdifNet("q_c", SRLC16_replacement);
				EdifNet q = new EdifNet("q", SRLC16_replacement);
				EdifNet q15 = new EdifNet("q15", SRLC16_replacement);
				EdifNet wire1 = new EdifNet("wire1", SRLC16_replacement);
				EdifNet wire2 = new EdifNet("wire2", SRLC16_replacement);
				EdifNet wire3 = new EdifNet("wire3", SRLC16_replacement);
				EdifNet wire4 = new EdifNet("wire4", SRLC16_replacement);
				EdifNet wire5 = new EdifNet("wire5", SRLC16_replacement);
				EdifNet wire6 = new EdifNet("wire6", SRLC16_replacement);
				EdifNet wire7 = new EdifNet("wire7", SRLC16_replacement);
				EdifNet wire8 = new EdifNet("wire8", SRLC16_replacement);
				EdifNet wire9 = new EdifNet("wire9", SRLC16_replacement);
				EdifNet wire10 = new EdifNet("wire10", SRLC16_replacement);
				EdifNet wire11 = new EdifNet("wire11", SRLC16_replacement);
				EdifNet wire12 = new EdifNet("wire12", SRLC16_replacement);
				EdifNet wire13 = new EdifNet("wire13", SRLC16_replacement);
				EdifNet wire14 = new EdifNet("wire14", SRLC16_replacement);
				EdifNet wire15 = new EdifNet("wire15", SRLC16_replacement);
				EdifNet wire16 = new EdifNet("wire16", SRLC16_replacement);
				EdifNet net1 = new EdifNet("net1", SRLC16_replacement);
				EdifNet net2 = new EdifNet("net2", SRLC16_replacement);
				EdifNet net3 = new EdifNet("net3", SRLC16_replacement);
				EdifNet net4 = new EdifNet("net4", SRLC16_replacement);
				EdifNet net5 = new EdifNet("net5", SRLC16_replacement);
				EdifNet net6 = new EdifNet("net6", SRLC16_replacement);
				EdifNet net7 = new EdifNet("net7", SRLC16_replacement);
				EdifNet net8 = new EdifNet("net8", SRLC16_replacement);
				EdifNet net9 = new EdifNet("net9", SRLC16_replacement);
				EdifNet net10 = new EdifNet("net10", SRLC16_replacement);
				EdifNet net11 = new EdifNet("net11", SRLC16_replacement);
				EdifNet net12 = new EdifNet("net12", SRLC16_replacement);
				EdifNet net13 = new EdifNet("net13", SRLC16_replacement);
				EdifNet net14 = new EdifNet("net14", SRLC16_replacement);
				SRLC16_replacement.addNet(clk_c);
				SRLC16_replacement.addNet(clk);
				SRLC16_replacement.addNet(d_c);
				SRLC16_replacement.addNet(d);
				SRLC16_replacement.addNet(a0_c);
				SRLC16_replacement.addNet(a0);
				SRLC16_replacement.addNet(a1_c);
				SRLC16_replacement.addNet(a1);
				SRLC16_replacement.addNet(a2_c);
				SRLC16_replacement.addNet(a2);
				SRLC16_replacement.addNet(a3_c);
				SRLC16_replacement.addNet(a3);
				SRLC16_replacement.addNet(q_c);
				SRLC16_replacement.addNet(q);
				SRLC16_replacement.addNet(q15);
				SRLC16_replacement.addNet(wire1);
				SRLC16_replacement.addNet(wire2);
				SRLC16_replacement.addNet(wire3);
				SRLC16_replacement.addNet(wire4);
				SRLC16_replacement.addNet(wire5);
				SRLC16_replacement.addNet(wire6);
				SRLC16_replacement.addNet(wire7);
				SRLC16_replacement.addNet(wire8);
				SRLC16_replacement.addNet(wire9);
				SRLC16_replacement.addNet(wire10);
				SRLC16_replacement.addNet(wire11);
				SRLC16_replacement.addNet(wire12);
				SRLC16_replacement.addNet(wire13);
				SRLC16_replacement.addNet(wire14);
				SRLC16_replacement.addNet(wire15);
				SRLC16_replacement.addNet(wire16);
				SRLC16_replacement.addNet(net1);
				SRLC16_replacement.addNet(net2);
				SRLC16_replacement.addNet(net3);
				SRLC16_replacement.addNet(net4);
				SRLC16_replacement.addNet(net5);
				SRLC16_replacement.addNet(net6);
				SRLC16_replacement.addNet(net7);
				SRLC16_replacement.addNet(net8);
				SRLC16_replacement.addNet(net9);
				SRLC16_replacement.addNet(net10);
				SRLC16_replacement.addNet(net11);
				SRLC16_replacement.addNet(net12);
				SRLC16_replacement.addNet(net13);
				SRLC16_replacement.addNet(net14);
				
				/******Instances******/
				EdifCellInstance clk_ibuf = new EdifCellInstance("clk_ibuf", SRLC16_replacement, BUFGP);
				EdifCellInstance q_obuf = new EdifCellInstance("q_obuf", SRLC16_replacement, OBUF);
				EdifCellInstance q15_obuf = new EdifCellInstance("q15_obuf", SRLC16_replacement, OBUF);
				EdifCellInstance d_ibuf = new EdifCellInstance("d_ibuf", SRLC16_replacement, IBUF);
				EdifCellInstance a0_ibuf = new EdifCellInstance("a0_ibuf", SRLC16_replacement, IBUF);
				EdifCellInstance a1_ibuf = new EdifCellInstance("a1_ibuf", SRLC16_replacement, IBUF);
				EdifCellInstance a2_ibuf = new EdifCellInstance("a2_ibuf", SRLC16_replacement, IBUF);
				EdifCellInstance a3_ibuf = new EdifCellInstance("a3_ibuf", SRLC16_replacement, IBUF);
				EdifCellInstance FD1 = new EdifCellInstance("FD1", SRLC16_replacement, FD);
				EdifCellInstance FD2 = new EdifCellInstance("FD2", SRLC16_replacement, FD);
				EdifCellInstance FD3 = new EdifCellInstance("FD3", SRLC16_replacement, FD);
				EdifCellInstance FD4 = new EdifCellInstance("FD4", SRLC16_replacement, FD);
				EdifCellInstance FD5 = new EdifCellInstance("FD5", SRLC16_replacement, FD);
				EdifCellInstance FD6 = new EdifCellInstance("FD6", SRLC16_replacement, FD);
				EdifCellInstance FD7 = new EdifCellInstance("FD7", SRLC16_replacement, FD);
				EdifCellInstance FD8 = new EdifCellInstance("FD8", SRLC16_replacement, FD);
				EdifCellInstance FD9 = new EdifCellInstance("FD9", SRLC16_replacement, FD);
				EdifCellInstance FD10 = new EdifCellInstance("FD10", SRLC16_replacement, FD);
				EdifCellInstance FD11 = new EdifCellInstance("FD11", SRLC16_replacement, FD);
				EdifCellInstance FD12 = new EdifCellInstance("FD12", SRLC16_replacement, FD);
				EdifCellInstance FD13 = new EdifCellInstance("FD13", SRLC16_replacement, FD);
				EdifCellInstance FD14 = new EdifCellInstance("FD14", SRLC16_replacement, FD);
				EdifCellInstance FD15 = new EdifCellInstance("FD15", SRLC16_replacement, FD);
				EdifCellInstance FD16 = new EdifCellInstance("FD16", SRLC16_replacement, FD);
				EdifCellInstance LUT3_1 = new EdifCellInstance("LUT3_1", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_2 = new EdifCellInstance("LUT3_2", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_3 = new EdifCellInstance("LUT3_3", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_4 = new EdifCellInstance("LUT3_4", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_5 = new EdifCellInstance("LUT3_5", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_6 = new EdifCellInstance("LUT3_6", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_7 = new EdifCellInstance("LUT3_7", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_8 = new EdifCellInstance("LUT3_8", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_9 = new EdifCellInstance("LUT3_9", SRLC16_replacement, LUT3);
				EdifCellInstance MUXF5_1 = new EdifCellInstance("MUXF5_1", SRLC16_replacement, MUXF5);
				EdifCellInstance MUXF5_2 = new EdifCellInstance("MUXF5_2", SRLC16_replacement, MUXF5);
				EdifCellInstance MUXF5_3 = new EdifCellInstance("MUXF5_3", SRLC16_replacement, MUXF5);
				EdifCellInstance MUXF5_4 = new EdifCellInstance("MUXF5_4", SRLC16_replacement, MUXF5);
				EdifCellInstance MUXF6_1 = new EdifCellInstance("MUXF6_1", SRLC16_replacement, MUXF6);
				EdifCellInstance MUXF6_2 = new EdifCellInstance("MUXF6_2", SRLC16_replacement, MUXF6);
				SRLC16_replacement.addSubCell(clk_ibuf);
				SRLC16_replacement.addSubCell(q_obuf);
				SRLC16_replacement.addSubCell(q15_obuf);
				SRLC16_replacement.addSubCell(d_ibuf);
				SRLC16_replacement.addSubCell(a0_ibuf);
				SRLC16_replacement.addSubCell(a1_ibuf);
				SRLC16_replacement.addSubCell(a2_ibuf);
				SRLC16_replacement.addSubCell(a3_ibuf);
				SRLC16_replacement.addSubCell(FD1);
				SRLC16_replacement.addSubCell(FD2);
				SRLC16_replacement.addSubCell(FD3);
				SRLC16_replacement.addSubCell(FD4);
				SRLC16_replacement.addSubCell(FD5);
				SRLC16_replacement.addSubCell(FD6);
				SRLC16_replacement.addSubCell(FD7);
				SRLC16_replacement.addSubCell(FD8);
				SRLC16_replacement.addSubCell(FD9);
				SRLC16_replacement.addSubCell(FD10);
				SRLC16_replacement.addSubCell(FD11);
				SRLC16_replacement.addSubCell(FD12);
				SRLC16_replacement.addSubCell(FD13);
				SRLC16_replacement.addSubCell(FD14);
				SRLC16_replacement.addSubCell(FD15);
				SRLC16_replacement.addSubCell(FD16);
				SRLC16_replacement.addSubCell(LUT3_1);
				SRLC16_replacement.addSubCell(LUT3_2);
				SRLC16_replacement.addSubCell(LUT3_3);
				SRLC16_replacement.addSubCell(LUT3_4);
				SRLC16_replacement.addSubCell(LUT3_5);
				SRLC16_replacement.addSubCell(LUT3_6);
				SRLC16_replacement.addSubCell(LUT3_7);
				SRLC16_replacement.addSubCell(LUT3_8);
				SRLC16_replacement.addSubCell(LUT3_9);
				SRLC16_replacement.addSubCell(MUXF5_1);
				SRLC16_replacement.addSubCell(MUXF5_2);
				SRLC16_replacement.addSubCell(MUXF5_3);
				SRLC16_replacement.addSubCell(MUXF5_4);
				SRLC16_replacement.addSubCell(MUXF6_1);
				SRLC16_replacement.addSubCell(MUXF6_2);
				
				/******Interface******/
				EdifCellInterface SRLC16_replacement_interface = new EdifCellInterface(SRLC16_replacement);
				EdifCellInterface clk_ibuf_interface = new EdifCellInterface(BUFGP);
				EdifCellInterface d_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface a0_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface a1_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface a2_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface a3_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface q_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface q15_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface FD1_interface = new EdifCellInterface(FD);
				EdifCellInterface FD2_interface = new EdifCellInterface(FD);
				EdifCellInterface FD3_interface = new EdifCellInterface(FD);
				EdifCellInterface FD4_interface = new EdifCellInterface(FD);
				EdifCellInterface FD5_interface = new EdifCellInterface(FD);
				EdifCellInterface FD6_interface = new EdifCellInterface(FD);
				EdifCellInterface FD7_interface = new EdifCellInterface(FD);
				EdifCellInterface FD8_interface = new EdifCellInterface(FD);
				EdifCellInterface FD9_interface = new EdifCellInterface(FD);
				EdifCellInterface FD10_interface = new EdifCellInterface(FD);
				EdifCellInterface FD11_interface = new EdifCellInterface(FD);
				EdifCellInterface FD12_interface = new EdifCellInterface(FD);
				EdifCellInterface FD13_interface = new EdifCellInterface(FD);
				EdifCellInterface FD14_interface = new EdifCellInterface(FD);
				EdifCellInterface FD15_interface = new EdifCellInterface(FD);
				EdifCellInterface FD16_interface = new EdifCellInterface(FD);
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
				//SRLC16_replacement
				EdifPort SRLC16_replacement_interface_clk = new EdifPort(SRLC16_replacement_interface, "clk", 1, 1);
				EdifPort SRLC16_replacement_interface_d = new EdifPort(SRLC16_replacement_interface, "d", 1, 1);
				EdifPort SRLC16_replacement_interface_a0 = new EdifPort(SRLC16_replacement_interface, "a0", 1 ,1);
				EdifPort SRLC16_replacement_interface_a1 = new EdifPort(SRLC16_replacement_interface, "a1", 1 ,1);
				EdifPort SRLC16_replacement_interface_a2 = new EdifPort(SRLC16_replacement_interface, "a2", 1 ,1);
				EdifPort SRLC16_replacement_interface_a3 = new EdifPort(SRLC16_replacement_interface, "a3", 1 ,1);
				EdifPort SRLC16_replacement_interface_q = new EdifPort(SRLC16_replacement_interface, "q", 1, 2);
				EdifPort SRLC16_replacement_interface_q15 = new EdifPort(SRLC16_replacement_interface, "q15", 1, 2);
				SRLC16_replacement_interface.addPort("clk", 1, 1);
				SRLC16_replacement_interface.addPort("d", 1, 1);
				SRLC16_replacement_interface.addPort("a0", 1, 1);
				SRLC16_replacement_interface.addPort("a1", 1, 1);
				SRLC16_replacement_interface.addPort("a2", 1, 1);
				SRLC16_replacement_interface.addPort("a3", 1, 1);
				SRLC16_replacement_interface.addPort("q", 1, 2);
				SRLC16_replacement_interface.addPort("q15", 1, 2);
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
				//q15_obuf
				EdifPort q15_obuf_interface_I = new EdifPort(q15_obuf_interface, "I", 1, 1);
				EdifPort q15_obuf_interface_O = new EdifPort(q15_obuf_interface, "O", 1, 2);
				q15_obuf_interface.addPort("I", 1, 1);
				q15_obuf_interface.addPort("O", 1, 2);
				//FD1
				EdifPort FD1_interface_C = new EdifPort(FD1_interface, "C", 1, 1);
				EdifPort FD1_interface_D = new EdifPort(FD1_interface, "D", 1, 1);
				EdifPort FD1_interface_Q = new EdifPort(FD1_interface, "Q", 1, 2);
				FD1_interface.addPort("C", 1, 1);
				FD1_interface.addPort("D", 1, 1);
				FD1_interface.addPort("Q", 1, 2);
				//FD2
				EdifPort FD2_interface_C = new EdifPort(FD2_interface, "C", 1, 1);
				EdifPort FD2_interface_D = new EdifPort(FD2_interface, "D", 1, 1);
				EdifPort FD2_interface_Q = new EdifPort(FD2_interface, "Q", 1, 2);
				FD2_interface.addPort("C", 1, 1);
				FD2_interface.addPort("D", 1, 1);
				FD2_interface.addPort("Q", 1, 2);
				//FD3
				EdifPort FD3_interface_C = new EdifPort(FD3_interface, "C", 1, 1);
				EdifPort FD3_interface_D = new EdifPort(FD3_interface, "D", 1, 1);
				EdifPort FD3_interface_Q = new EdifPort(FD3_interface, "Q", 1, 2);
				FD3_interface.addPort("C", 1, 1);
				FD3_interface.addPort("D", 1, 1);
				FD3_interface.addPort("Q", 1, 2);
				//FD4
				EdifPort FD4_interface_C = new EdifPort(FD4_interface, "C", 1, 1);
				EdifPort FD4_interface_D = new EdifPort(FD4_interface, "D", 1, 1);
				EdifPort FD4_interface_Q = new EdifPort(FD4_interface, "Q", 1, 2);
				FD4_interface.addPort("C", 1, 1);
				FD4_interface.addPort("D", 1, 1);
				FD4_interface.addPort("Q", 1, 2);
				//FD5
				EdifPort FD5_interface_C = new EdifPort(FD5_interface, "C", 1, 1);
				EdifPort FD5_interface_D = new EdifPort(FD5_interface, "D", 1, 1);
				EdifPort FD5_interface_Q = new EdifPort(FD5_interface, "Q", 1, 2);
				FD5_interface.addPort("C", 1, 1);
				FD5_interface.addPort("D", 1, 1);
				FD5_interface.addPort("Q", 1, 2);
				//FD6
				EdifPort FD6_interface_C = new EdifPort(FD6_interface, "C", 1, 1);
				EdifPort FD6_interface_D = new EdifPort(FD6_interface, "D", 1, 1);
				EdifPort FD6_interface_Q = new EdifPort(FD6_interface, "Q", 1, 2);
				FD6_interface.addPort("C", 1, 1);
				FD6_interface.addPort("D", 1, 1);
				FD6_interface.addPort("Q", 1, 2);
				//FD7
				EdifPort FD7_interface_C = new EdifPort(FD7_interface, "C", 1, 1);
				EdifPort FD7_interface_D = new EdifPort(FD7_interface, "D", 1, 1);
				EdifPort FD7_interface_Q = new EdifPort(FD7_interface, "Q", 1, 2);
				FD7_interface.addPort("C", 1, 1);
				FD7_interface.addPort("D", 1, 1);
				FD7_interface.addPort("Q", 1, 2);
				//FD8
				EdifPort FD8_interface_C = new EdifPort(FD8_interface, "C", 1, 1);
				EdifPort FD8_interface_D = new EdifPort(FD8_interface, "D", 1, 1);
				EdifPort FD8_interface_Q = new EdifPort(FD8_interface, "Q", 1, 2);
				FD8_interface.addPort("C", 1, 1);
				FD8_interface.addPort("D", 1, 1);
				FD8_interface.addPort("Q", 1, 2);
				//FD9
				EdifPort FD9_interface_C = new EdifPort(FD9_interface, "C", 1, 1);
				EdifPort FD9_interface_D = new EdifPort(FD9_interface, "D", 1, 1);
				EdifPort FD9_interface_Q = new EdifPort(FD9_interface, "Q", 1, 2);
				FD9_interface.addPort("C", 1, 1);
				FD9_interface.addPort("D", 1, 1);
				FD9_interface.addPort("Q", 1, 2);
				//FD10
				EdifPort FD10_interface_C = new EdifPort(FD10_interface, "C", 1, 1);
				EdifPort FD10_interface_D = new EdifPort(FD10_interface, "D", 1, 1);
				EdifPort FD10_interface_Q = new EdifPort(FD10_interface, "Q", 1, 2);
				FD10_interface.addPort("C", 1, 1);
				FD10_interface.addPort("D", 1, 1);
				FD10_interface.addPort("Q", 1, 2);
				//FD11
				EdifPort FD11_interface_C = new EdifPort(FD11_interface, "C", 1, 1);
				EdifPort FD11_interface_D = new EdifPort(FD11_interface, "D", 1, 1);
				EdifPort FD11_interface_Q = new EdifPort(FD11_interface, "Q", 1, 2);
				FD11_interface.addPort("C", 1, 1);
				FD11_interface.addPort("D", 1, 1);
				FD11_interface.addPort("Q", 1, 2);
				//FD12
				EdifPort FD12_interface_C = new EdifPort(FD12_interface, "C", 1, 1);
				EdifPort FD12_interface_D = new EdifPort(FD12_interface, "D", 1, 1);
				EdifPort FD12_interface_Q = new EdifPort(FD12_interface, "Q", 1, 2);
				FD12_interface.addPort("C", 1, 1);
				FD12_interface.addPort("D", 1, 1);
				FD12_interface.addPort("Q", 1, 2);
				//FD13
				EdifPort FD13_interface_C = new EdifPort(FD13_interface, "C", 1, 1);
				EdifPort FD13_interface_D = new EdifPort(FD13_interface, "D", 1, 1);
				EdifPort FD13_interface_Q = new EdifPort(FD13_interface, "Q", 1, 2);
				FD13_interface.addPort("C", 1, 1);
				FD13_interface.addPort("D", 1, 1);
				FD13_interface.addPort("Q", 1, 2);
				//FD14
				EdifPort FD14_interface_C = new EdifPort(FD14_interface, "C", 1, 1);
				EdifPort FD14_interface_D = new EdifPort(FD14_interface, "D", 1, 1);
				EdifPort FD14_interface_Q = new EdifPort(FD14_interface, "Q", 1, 2);
				FD14_interface.addPort("C", 1, 1);
				FD14_interface.addPort("D", 1, 1);
				FD14_interface.addPort("Q", 1, 2);
				//FD15
				EdifPort FD15_interface_C = new EdifPort(FD15_interface, "C", 1, 1);
				EdifPort FD15_interface_D = new EdifPort(FD15_interface, "D", 1, 1);
				EdifPort FD15_interface_Q = new EdifPort(FD15_interface, "Q", 1, 2);
				FD15_interface.addPort("C", 1, 1);
				FD15_interface.addPort("D", 1, 1);
				FD15_interface.addPort("Q", 1, 2);
				//FD16
				EdifPort FD16_interface_C = new EdifPort(FD16_interface, "C", 1, 1);
				EdifPort FD16_interface_D = new EdifPort(FD16_interface, "D", 1, 1);
				EdifPort FD16_interface_Q = new EdifPort(FD16_interface, "Q", 1, 2);
				FD16_interface.addPort("C", 1, 1);
				FD16_interface.addPort("D", 1, 1);
				FD16_interface.addPort("Q", 1, 2);
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
				EdifPortRef SRLC16_replacement_clk = new EdifPortRef(clk, SRLC16_replacement_interface_clk.getSingleBitPort(0), null);
				EdifPortRef SRLC16_replacement_d = new EdifPortRef(d, SRLC16_replacement_interface_d.getSingleBitPort(0), null);
				EdifPortRef SRLC16_replacement_a0 = new EdifPortRef(a0, SRLC16_replacement_interface_a0.getSingleBitPort(0), null);
				EdifPortRef SRLC16_replacement_a1 = new EdifPortRef(a1, SRLC16_replacement_interface_a1.getSingleBitPort(0), null);
				EdifPortRef SRLC16_replacement_a2 = new EdifPortRef(a2, SRLC16_replacement_interface_a2.getSingleBitPort(0), null);
				EdifPortRef SRLC16_replacement_a3 = new EdifPortRef(a3, SRLC16_replacement_interface_a3.getSingleBitPort(0), null);
				EdifPortRef SRLC16_replacement_q = new EdifPortRef(q, SRLC16_replacement_interface_q.getSingleBitPort(0), null);
				EdifPortRef SRLC16_replacement_q15 = new EdifPortRef(q15, SRLC16_replacement_interface_q15.getSingleBitPort(0), null);
				EdifPortRef clk_ibuf_I = new EdifPortRef(clk, clk_ibuf_interface_I.getSingleBitPort(0), clk_ibuf);
				EdifPortRef clk_ibuf_O = new EdifPortRef(clk_c, clk_ibuf_interface_O.getSingleBitPort(0), clk_ibuf);
				EdifPortRef d_ibuf_I = new EdifPortRef(d, d_ibuf_interface_I.getSingleBitPort(0), d_ibuf);
				EdifPortRef d_ibuf_O = new EdifPortRef(d_c, d_ibuf_interface_O.getSingleBitPort(0), d_ibuf);
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
				EdifPortRef q15_obuf_I = new EdifPortRef(wire16, q15_obuf_interface_I.getSingleBitPort(0), q15_obuf);
				EdifPortRef q15_obuf_O = new EdifPortRef(q15, q15_obuf_interface_O.getSingleBitPort(0), q15_obuf);
				EdifPortRef FD1_C = new EdifPortRef(clk_c, FD1_interface_C.getSingleBitPort(0), FD1);
				EdifPortRef FD1_D = new EdifPortRef(d_c, FD1_interface_D.getSingleBitPort(0), FD1);
				EdifPortRef FD1_Q = new EdifPortRef(wire1, FD1_interface_Q.getSingleBitPort(0), FD1);
				EdifPortRef FD2_C = new EdifPortRef(clk_c, FD2_interface_C.getSingleBitPort(0), FD2);
				EdifPortRef FD2_D = new EdifPortRef(wire1, FD2_interface_D.getSingleBitPort(0), FD2);
				EdifPortRef FD2_Q = new EdifPortRef(wire2, FD2_interface_Q.getSingleBitPort(0), FD2);
				EdifPortRef FD3_C = new EdifPortRef(clk_c, FD3_interface_C.getSingleBitPort(0), FD3);
				EdifPortRef FD3_D = new EdifPortRef(wire2, FD3_interface_D.getSingleBitPort(0), FD3);
				EdifPortRef FD3_Q = new EdifPortRef(wire3, FD3_interface_Q.getSingleBitPort(0), FD3);
				EdifPortRef FD4_C = new EdifPortRef(clk_c, FD4_interface_C.getSingleBitPort(0), FD4);
				EdifPortRef FD4_D = new EdifPortRef(wire3, FD4_interface_D.getSingleBitPort(0), FD4);
				EdifPortRef FD4_Q = new EdifPortRef(wire4, FD4_interface_Q.getSingleBitPort(0), FD4);
				EdifPortRef FD5_C = new EdifPortRef(clk_c, FD5_interface_C.getSingleBitPort(0), FD5);
				EdifPortRef FD5_D = new EdifPortRef(wire4, FD5_interface_D.getSingleBitPort(0), FD5);
				EdifPortRef FD5_Q = new EdifPortRef(wire5, FD5_interface_Q.getSingleBitPort(0), FD5);
				EdifPortRef FD6_C = new EdifPortRef(clk_c, FD6_interface_C.getSingleBitPort(0), FD6);
				EdifPortRef FD6_D = new EdifPortRef(wire5, FD6_interface_D.getSingleBitPort(0), FD6);
				EdifPortRef FD6_Q = new EdifPortRef(wire6, FD6_interface_Q.getSingleBitPort(0), FD6);
				EdifPortRef FD7_C = new EdifPortRef(clk_c, FD7_interface_C.getSingleBitPort(0), FD7);
				EdifPortRef FD7_D = new EdifPortRef(wire6, FD7_interface_D.getSingleBitPort(0), FD7);
				EdifPortRef FD7_Q = new EdifPortRef(wire7, FD7_interface_Q.getSingleBitPort(0), FD7);
				EdifPortRef FD8_C = new EdifPortRef(clk_c, FD8_interface_C.getSingleBitPort(0), FD8);
				EdifPortRef FD8_D = new EdifPortRef(wire7, FD8_interface_D.getSingleBitPort(0), FD8);
				EdifPortRef FD8_Q = new EdifPortRef(wire8, FD8_interface_Q.getSingleBitPort(0), FD8);
				EdifPortRef FD9_C = new EdifPortRef(clk_c, FD9_interface_C.getSingleBitPort(0), FD9);
				EdifPortRef FD9_D = new EdifPortRef(wire8, FD9_interface_D.getSingleBitPort(0), FD9);
				EdifPortRef FD9_Q = new EdifPortRef(wire9, FD9_interface_Q.getSingleBitPort(0), FD9);
				EdifPortRef FD10_C = new EdifPortRef(clk_c, FD10_interface_C.getSingleBitPort(0), FD10);
				EdifPortRef FD10_D = new EdifPortRef(wire9, FD10_interface_D.getSingleBitPort(0), FD10);
				EdifPortRef FD10_Q = new EdifPortRef(wire10, FD10_interface_Q.getSingleBitPort(0), FD10);
				EdifPortRef FD11_C = new EdifPortRef(clk_c, FD11_interface_C.getSingleBitPort(0), FD11);
				EdifPortRef FD11_D = new EdifPortRef(wire10, FD11_interface_D.getSingleBitPort(0), FD11);
				EdifPortRef FD11_Q = new EdifPortRef(wire11, FD11_interface_Q.getSingleBitPort(0), FD11);
				EdifPortRef FD12_C = new EdifPortRef(clk_c, FD12_interface_C.getSingleBitPort(0), FD12);
				EdifPortRef FD12_D = new EdifPortRef(wire11, FD12_interface_D.getSingleBitPort(0), FD12);
				EdifPortRef FD12_Q = new EdifPortRef(wire12, FD12_interface_Q.getSingleBitPort(0), FD12);
				EdifPortRef FD13_C = new EdifPortRef(clk_c, FD13_interface_C.getSingleBitPort(0), FD13);
				EdifPortRef FD13_D = new EdifPortRef(wire12, FD13_interface_D.getSingleBitPort(0), FD13);
				EdifPortRef FD13_Q = new EdifPortRef(wire13, FD13_interface_Q.getSingleBitPort(0), FD13);
				EdifPortRef FD14_C = new EdifPortRef(clk_c, FD14_interface_C.getSingleBitPort(0), FD14);
				EdifPortRef FD14_D = new EdifPortRef(wire13, FD14_interface_D.getSingleBitPort(0), FD14);
				EdifPortRef FD14_Q = new EdifPortRef(wire14, FD14_interface_Q.getSingleBitPort(0), FD14);
				EdifPortRef FD15_C = new EdifPortRef(clk_c, FD15_interface_C.getSingleBitPort(0), FD15);
				EdifPortRef FD15_D = new EdifPortRef(wire14, FD15_interface_D.getSingleBitPort(0), FD15);
				EdifPortRef FD15_Q = new EdifPortRef(wire15, FD15_interface_Q.getSingleBitPort(0), FD15);
				EdifPortRef FD16_C = new EdifPortRef(clk_c, FD16_interface_C.getSingleBitPort(0), FD16);
				EdifPortRef FD16_D = new EdifPortRef(wire15, FD16_interface_D.getSingleBitPort(0), FD16);
				EdifPortRef FD16_Q = new EdifPortRef(wire16, FD16_interface_Q.getSingleBitPort(0), FD16);
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
				clk.addPortConnection(SRLC16_replacement_clk);
				//clk_c
				clk_c.addPortConnection(clk_ibuf_O);
				clk_c.addPortConnection(FD1_C);
				clk_c.addPortConnection(FD2_C);
				clk_c.addPortConnection(FD3_C);
				clk_c.addPortConnection(FD4_C);
				clk_c.addPortConnection(FD5_C);
				clk_c.addPortConnection(FD6_C);
				clk_c.addPortConnection(FD7_C);
				clk_c.addPortConnection(FD8_C);
				clk_c.addPortConnection(FD9_C);
				clk_c.addPortConnection(FD10_C);
				clk_c.addPortConnection(FD11_C);
				clk_c.addPortConnection(FD12_C);
				clk_c.addPortConnection(FD13_C);
				clk_c.addPortConnection(FD14_C);
				clk_c.addPortConnection(FD15_C);
				clk_c.addPortConnection(FD16_C);
				//d
				d.addPortConnection(SRLC16_replacement_d);
				d.addPortConnection(d_ibuf_I);
				//d_c
				d_c.addPortConnection(d_ibuf_O);
				d_c.addPortConnection(FD1_D);
				//a0
				a0.addPortConnection(SRLC16_replacement_a0);
				a0.addPortConnection(a0_ibuf_I);
				//a0_c
				a0_c.addPortConnection(a0_ibuf_O);
				a0_c.addPortConnection(LUT3_9_I2);
				//a1
				a1.addPortConnection(SRLC16_replacement_a1);
				a1.addPortConnection(a1_ibuf_I);
				//a1_c
				a1_c.addPortConnection(a1_ibuf_O);
				a1_c.addPortConnection(MUXF6_1_S);
				a1_c.addPortConnection(MUXF6_2_S);
				//a2
				a2.addPortConnection(SRLC16_replacement_a2);
				a2.addPortConnection(a2_ibuf_I);
				//a2_c
				a2_c.addPortConnection(a2_ibuf_O);
				a2_c.addPortConnection(MUXF5_1_S);
				a2_c.addPortConnection(MUXF5_2_S);
				a2_c.addPortConnection(MUXF5_3_S);
				a2_c.addPortConnection(MUXF5_4_S);
				//a3
				a3.addPortConnection(SRLC16_replacement_a3);
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
				//wire1
				wire1.addPortConnection(LUT3_1_I1);
				wire1.addPortConnection(FD1_Q);
				wire1.addPortConnection(FD2_D);
				//wire2
				wire2.addPortConnection(LUT3_5_I1);
				wire2.addPortConnection(FD2_Q);
				wire2.addPortConnection(FD3_D);
				//wire3
				wire3.addPortConnection(LUT3_3_I1);
				wire3.addPortConnection(FD3_Q);
				wire3.addPortConnection(FD4_D);
				//wire4
				wire4.addPortConnection(LUT3_7_I1);
				wire4.addPortConnection(FD4_Q);
				wire4.addPortConnection(FD5_D);
				//wire5
				wire5.addPortConnection(LUT3_2_I1);
				wire5.addPortConnection(FD5_Q);
				wire5.addPortConnection(FD6_D);
				//wire6
				wire6.addPortConnection(LUT3_6_I1);
				wire6.addPortConnection(FD6_Q);
				wire6.addPortConnection(FD7_D);
				//wire7
				wire7.addPortConnection(LUT3_4_I1);
				wire7.addPortConnection(FD7_Q);
				wire7.addPortConnection(FD8_D);
				//wire8
				wire8.addPortConnection(LUT3_8_I1);
				wire8.addPortConnection(FD8_Q);
				wire8.addPortConnection(FD9_D);
				//wire9
				wire9.addPortConnection(LUT3_1_I2);
				wire9.addPortConnection(FD9_Q);
				wire9.addPortConnection(FD10_D);
				//wire10
				wire10.addPortConnection(LUT3_5_I2);
				wire10.addPortConnection(FD10_Q);
				wire10.addPortConnection(FD11_D);
				//wire11
				wire11.addPortConnection(LUT3_3_I2);
				wire11.addPortConnection(FD11_Q);
				wire11.addPortConnection(FD12_D);
				//wire12
				wire12.addPortConnection(LUT3_7_I2);
				wire12.addPortConnection(FD12_Q);
				wire12.addPortConnection(FD13_D);
				//wire13
				wire13.addPortConnection(LUT3_2_I2);
				wire13.addPortConnection(FD13_Q);
				wire13.addPortConnection(FD14_D);
				//wire14
				wire14.addPortConnection(LUT3_6_I2);
				wire14.addPortConnection(FD14_Q);
				wire14.addPortConnection(FD15_D);
				//wire15
				wire15.addPortConnection(LUT3_4_I2);
				wire15.addPortConnection(FD15_Q);
				wire15.addPortConnection(FD16_D);
				//wire16
				wire16.addPortConnection(LUT3_8_I2);
				wire16.addPortConnection(FD16_Q);
				wire16.addPortConnection(q15_obuf_I);
				//q
				q.addPortConnection(SRLC16_replacement_q);
				q.addPortConnection(q_obuf_O);
				//q15
				q15.addPortConnection(SRLC16_replacement_q15);
				q15.addPortConnection(q15_obuf_O);
				//q_c
				q_c.addPortConnection(q_obuf_I);
				q_c.addPortConnection(LUT3_9_O);
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
		        long initInt = 0;
		        StringTypedValue valueZero = new StringTypedValue("0");
		        StringTypedValue valueOne = new StringTypedValue("1");
		        StringTypedValue valueE4 = new StringTypedValue("E4");
		        StringTypedValue valueCA = new StringTypedValue("CA");
		        //Set INIT Property for FD1
				PropertyList FD1_propertylist = FD1.getPropertyList();
				if (FD1_propertylist != null) {
                    for (Property FD1_property : FD1_propertylist.values()) {
                        if (FD1_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD1_property.setValue(valueOne);
                            else
                                FD1_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD1.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD1.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD2
				PropertyList FD2_propertylist = FD2.getPropertyList();
				if (FD2_propertylist != null) {
                    for (Property FD2_property : FD2_propertylist.values()) {
                        if (FD2_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD2_property.setValue(valueOne);
                            else
                                FD2_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD2.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD2.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD3
				PropertyList FD3_propertylist = FD3.getPropertyList();
				if (FD3_propertylist != null) {
                    for (Property FD3_property : FD3_propertylist.values()) {
                        if (FD3_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD3_property.setValue(valueOne);
                            else
                                FD3_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD3.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD3.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD4
				PropertyList FD4_propertylist = FD4.getPropertyList();
				if (FD4_propertylist != null) {
                    for (Property FD4_property : FD4_propertylist.values()) {
                        if (FD4_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD4_property.setValue(valueOne);
                            else
                                FD4_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD4.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD4.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD5
				PropertyList FD5_propertylist = FD5.getPropertyList();
				if (FD5_propertylist != null) {
                    for (Property FD5_property : FD5_propertylist.values()) {
                        if (FD5_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD5_property.setValue(valueOne);
                            else
                                FD5_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD5.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD5.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD6
				PropertyList FD6_propertylist = FD6.getPropertyList();
				if (FD6_propertylist != null) {
                    for (Property FD6_property : FD6_propertylist.values()) {
                        if (FD6_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD6_property.setValue(valueOne);
                            else
                                FD6_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD6.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD6.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD7
				PropertyList FD7_propertylist = FD7.getPropertyList();
				if (FD7_propertylist != null) {
                    for (Property FD7_property : FD7_propertylist.values()) {
                        if (FD7_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD7_property.setValue(valueOne);
                            else
                                FD7_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD7.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD7.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD8
				PropertyList FD8_propertylist = FD8.getPropertyList();
				if (FD8_propertylist != null) {
                    for (Property FD8_property : FD8_propertylist.values()) {
                        if (FD8_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD8_property.setValue(valueOne);
                            else
                                FD8_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD8.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD8.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD9
				PropertyList FD9_propertylist = FD9.getPropertyList();
				if (FD9_propertylist != null) {
                    for (Property FD9_property : FD9_propertylist.values()) {
                        if (FD9_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD9_property.setValue(valueOne);
                            else
                                FD9_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD9.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD9.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD10
				PropertyList FD10_propertylist = FD10.getPropertyList();
				if (FD10_propertylist != null) {
                    for (Property FD10_property : FD10_propertylist.values()) {
                        if (FD10_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD10_property.setValue(valueOne);
                            else
                                FD10_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD10.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD10.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD11
				PropertyList FD11_propertylist = FD11.getPropertyList();
				if (FD11_propertylist != null) {
                    for (Property FD11_property : FD11_propertylist.values()) {
                        if (FD11_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD11_property.setValue(valueOne);
                            else
                                FD11_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD11.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD11.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD12
				PropertyList FD12_propertylist = FD12.getPropertyList();
				if (FD12_propertylist != null) {
                    for (Property FD12_property : FD12_propertylist.values()) {
                        if (FD12_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD12_property.setValue(valueOne);
                            else
                                FD12_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD12.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD12.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD13
				PropertyList FD13_propertylist = FD13.getPropertyList();
				if (FD13_propertylist != null) {
                    for (Property FD13_property : FD13_propertylist.values()) {
                        if (FD13_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD13_property.setValue(valueOne);
                            else
                                FD13_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD13.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD13.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD14
				PropertyList FD14_propertylist = FD14.getPropertyList();
				if (FD14_propertylist != null) {
                    for (Property FD14_property : FD14_propertylist.values()) {
                        if (FD14_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD14_property.setValue(valueOne);
                            else
                                FD14_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD14.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD14.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD15
				PropertyList FD15_propertylist = FD15.getPropertyList();
				if (FD15_propertylist != null) {
                    for (Property FD15_property : FD15_propertylist.values()) {
                        if (FD15_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD15_property.setValue(valueOne);
                            else
                                FD15_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD15.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD15.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD16
				PropertyList FD16_propertylist = FD16.getPropertyList();
				if (FD16_propertylist != null) {
                    for (Property FD16_property : FD16_propertylist.values()) {
                        if (FD16_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD16_property.setValue(valueOne);
                            else
                                FD16_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD16.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD16.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
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
	
	public static void Replace(EdifLibraryManager libManager, EdifCell SRLC16_replacement, long INIT,
			EdifNet d, EdifNet clk, EdifNet a0, EdifNet a1, EdifNet a2, EdifNet a3, EdifNet q, EdifNet q15){
		try{
			try{
				/******Cells******/
				EdifCell FD = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FD");
				EdifCell LUT3 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "LUT3");
				EdifCell MUXF5 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "MUXF5");
				EdifCell MUXF6 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "MUXF6");		
				
				/******Nets******/
				EdifNet wire1 = new EdifNet("wire1", SRLC16_replacement);
				EdifNet wire2 = new EdifNet("wire2", SRLC16_replacement);
				EdifNet wire3 = new EdifNet("wire3", SRLC16_replacement);
				EdifNet wire4 = new EdifNet("wire4", SRLC16_replacement);
				EdifNet wire5 = new EdifNet("wire5", SRLC16_replacement);
				EdifNet wire6 = new EdifNet("wire6", SRLC16_replacement);
				EdifNet wire7 = new EdifNet("wire7", SRLC16_replacement);
				EdifNet wire8 = new EdifNet("wire8", SRLC16_replacement);
				EdifNet wire9 = new EdifNet("wire9", SRLC16_replacement);
				EdifNet wire10 = new EdifNet("wire10", SRLC16_replacement);
				EdifNet wire11 = new EdifNet("wire11", SRLC16_replacement);
				EdifNet wire12 = new EdifNet("wire12", SRLC16_replacement);
				EdifNet wire13 = new EdifNet("wire13", SRLC16_replacement);
				EdifNet wire14 = new EdifNet("wire14", SRLC16_replacement);
				EdifNet wire15 = new EdifNet("wire15", SRLC16_replacement);
				EdifNet net1 = new EdifNet("net1", SRLC16_replacement);
				EdifNet net2 = new EdifNet("net2", SRLC16_replacement);
				EdifNet net3 = new EdifNet("net3", SRLC16_replacement);
				EdifNet net4 = new EdifNet("net4", SRLC16_replacement);
				EdifNet net5 = new EdifNet("net5", SRLC16_replacement);
				EdifNet net6 = new EdifNet("net6", SRLC16_replacement);
				EdifNet net7 = new EdifNet("net7", SRLC16_replacement);
				EdifNet net8 = new EdifNet("net8", SRLC16_replacement);
				EdifNet net9 = new EdifNet("net9", SRLC16_replacement);
				EdifNet net10 = new EdifNet("net10", SRLC16_replacement);
				EdifNet net11 = new EdifNet("net11", SRLC16_replacement);
				EdifNet net12 = new EdifNet("net12", SRLC16_replacement);
				EdifNet net13 = new EdifNet("net13", SRLC16_replacement);
				EdifNet net14 = new EdifNet("net14", SRLC16_replacement);
				SRLC16_replacement.addNet(wire1);
				SRLC16_replacement.addNet(wire2);
				SRLC16_replacement.addNet(wire3);
				SRLC16_replacement.addNet(wire4);
				SRLC16_replacement.addNet(wire5);
				SRLC16_replacement.addNet(wire6);
				SRLC16_replacement.addNet(wire7);
				SRLC16_replacement.addNet(wire8);
				SRLC16_replacement.addNet(wire9);
				SRLC16_replacement.addNet(wire10);
				SRLC16_replacement.addNet(wire11);
				SRLC16_replacement.addNet(wire12);
				SRLC16_replacement.addNet(wire13);
				SRLC16_replacement.addNet(wire14);
				SRLC16_replacement.addNet(wire15);
				SRLC16_replacement.addNet(net1);
				SRLC16_replacement.addNet(net2);
				SRLC16_replacement.addNet(net3);
				SRLC16_replacement.addNet(net4);
				SRLC16_replacement.addNet(net5);
				SRLC16_replacement.addNet(net6);
				SRLC16_replacement.addNet(net7);
				SRLC16_replacement.addNet(net8);
				SRLC16_replacement.addNet(net9);
				SRLC16_replacement.addNet(net10);
				SRLC16_replacement.addNet(net11);
				SRLC16_replacement.addNet(net12);
				SRLC16_replacement.addNet(net13);
				SRLC16_replacement.addNet(net14);
				
				/******Instances******/
				EdifCellInstance FD1 = new EdifCellInstance("FD1", SRLC16_replacement, FD);
				EdifCellInstance FD2 = new EdifCellInstance("FD2", SRLC16_replacement, FD);
				EdifCellInstance FD3 = new EdifCellInstance("FD3", SRLC16_replacement, FD);
				EdifCellInstance FD4 = new EdifCellInstance("FD4", SRLC16_replacement, FD);
				EdifCellInstance FD5 = new EdifCellInstance("FD5", SRLC16_replacement, FD);
				EdifCellInstance FD6 = new EdifCellInstance("FD6", SRLC16_replacement, FD);
				EdifCellInstance FD7 = new EdifCellInstance("FD7", SRLC16_replacement, FD);
				EdifCellInstance FD8 = new EdifCellInstance("FD8", SRLC16_replacement, FD);
				EdifCellInstance FD9 = new EdifCellInstance("FD9", SRLC16_replacement, FD);
				EdifCellInstance FD10 = new EdifCellInstance("FD10", SRLC16_replacement, FD);
				EdifCellInstance FD11 = new EdifCellInstance("FD11", SRLC16_replacement, FD);
				EdifCellInstance FD12 = new EdifCellInstance("FD12", SRLC16_replacement, FD);
				EdifCellInstance FD13 = new EdifCellInstance("FD13", SRLC16_replacement, FD);
				EdifCellInstance FD14 = new EdifCellInstance("FD14", SRLC16_replacement, FD);
				EdifCellInstance FD15 = new EdifCellInstance("FD15", SRLC16_replacement, FD);
				EdifCellInstance FD16 = new EdifCellInstance("FD16", SRLC16_replacement, FD);
				EdifCellInstance LUT3_1 = new EdifCellInstance("LUT3_1", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_2 = new EdifCellInstance("LUT3_2", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_3 = new EdifCellInstance("LUT3_3", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_4 = new EdifCellInstance("LUT3_4", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_5 = new EdifCellInstance("LUT3_5", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_6 = new EdifCellInstance("LUT3_6", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_7 = new EdifCellInstance("LUT3_7", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_8 = new EdifCellInstance("LUT3_8", SRLC16_replacement, LUT3);
				EdifCellInstance LUT3_9 = new EdifCellInstance("LUT3_9", SRLC16_replacement, LUT3);
				EdifCellInstance MUXF5_1 = new EdifCellInstance("MUXF5_1", SRLC16_replacement, MUXF5);
				EdifCellInstance MUXF5_2 = new EdifCellInstance("MUXF5_2", SRLC16_replacement, MUXF5);
				EdifCellInstance MUXF5_3 = new EdifCellInstance("MUXF5_3", SRLC16_replacement, MUXF5);
				EdifCellInstance MUXF5_4 = new EdifCellInstance("MUXF5_4", SRLC16_replacement, MUXF5);
				EdifCellInstance MUXF6_1 = new EdifCellInstance("MUXF6_1", SRLC16_replacement, MUXF6);
				EdifCellInstance MUXF6_2 = new EdifCellInstance("MUXF6_2", SRLC16_replacement, MUXF6);
				SRLC16_replacement.addSubCell(FD1);
				SRLC16_replacement.addSubCell(FD2);
				SRLC16_replacement.addSubCell(FD3);
				SRLC16_replacement.addSubCell(FD4);
				SRLC16_replacement.addSubCell(FD5);
				SRLC16_replacement.addSubCell(FD6);
				SRLC16_replacement.addSubCell(FD7);
				SRLC16_replacement.addSubCell(FD8);
				SRLC16_replacement.addSubCell(FD9);
				SRLC16_replacement.addSubCell(FD10);
				SRLC16_replacement.addSubCell(FD11);
				SRLC16_replacement.addSubCell(FD12);
				SRLC16_replacement.addSubCell(FD13);
				SRLC16_replacement.addSubCell(FD14);
				SRLC16_replacement.addSubCell(FD15);
				SRLC16_replacement.addSubCell(FD16);
				SRLC16_replacement.addSubCell(LUT3_1);
				SRLC16_replacement.addSubCell(LUT3_2);
				SRLC16_replacement.addSubCell(LUT3_3);
				SRLC16_replacement.addSubCell(LUT3_4);
				SRLC16_replacement.addSubCell(LUT3_5);
				SRLC16_replacement.addSubCell(LUT3_6);
				SRLC16_replacement.addSubCell(LUT3_7);
				SRLC16_replacement.addSubCell(LUT3_8);
				SRLC16_replacement.addSubCell(LUT3_9);
				SRLC16_replacement.addSubCell(MUXF5_1);
				SRLC16_replacement.addSubCell(MUXF5_2);
				SRLC16_replacement.addSubCell(MUXF5_3);
				SRLC16_replacement.addSubCell(MUXF5_4);
				SRLC16_replacement.addSubCell(MUXF6_1);
				SRLC16_replacement.addSubCell(MUXF6_2);
				
				/******Interface******/
				EdifCellInterface FD1_interface = new EdifCellInterface(FD);
				EdifCellInterface FD2_interface = new EdifCellInterface(FD);
				EdifCellInterface FD3_interface = new EdifCellInterface(FD);
				EdifCellInterface FD4_interface = new EdifCellInterface(FD);
				EdifCellInterface FD5_interface = new EdifCellInterface(FD);
				EdifCellInterface FD6_interface = new EdifCellInterface(FD);
				EdifCellInterface FD7_interface = new EdifCellInterface(FD);
				EdifCellInterface FD8_interface = new EdifCellInterface(FD);
				EdifCellInterface FD9_interface = new EdifCellInterface(FD);
				EdifCellInterface FD10_interface = new EdifCellInterface(FD);
				EdifCellInterface FD11_interface = new EdifCellInterface(FD);
				EdifCellInterface FD12_interface = new EdifCellInterface(FD);
				EdifCellInterface FD13_interface = new EdifCellInterface(FD);
				EdifCellInterface FD14_interface = new EdifCellInterface(FD);
				EdifCellInterface FD15_interface = new EdifCellInterface(FD);
				EdifCellInterface FD16_interface = new EdifCellInterface(FD);
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
				//FD1
				EdifPort FD1_interface_C = new EdifPort(FD1_interface, "C", 1, 1);
				EdifPort FD1_interface_D = new EdifPort(FD1_interface, "D", 1, 1);
				EdifPort FD1_interface_Q = new EdifPort(FD1_interface, "Q", 1, 2);
				FD1_interface.addPort("C", 1, 1);
				FD1_interface.addPort("D", 1, 1);
				FD1_interface.addPort("Q", 1, 2);
				//FD2
				EdifPort FD2_interface_C = new EdifPort(FD2_interface, "C", 1, 1);
				EdifPort FD2_interface_D = new EdifPort(FD2_interface, "D", 1, 1);
				EdifPort FD2_interface_Q = new EdifPort(FD2_interface, "Q", 1, 2);
				FD2_interface.addPort("C", 1, 1);
				FD2_interface.addPort("D", 1, 1);
				FD2_interface.addPort("Q", 1, 2);
				//FD3
				EdifPort FD3_interface_C = new EdifPort(FD3_interface, "C", 1, 1);
				EdifPort FD3_interface_D = new EdifPort(FD3_interface, "D", 1, 1);
				EdifPort FD3_interface_Q = new EdifPort(FD3_interface, "Q", 1, 2);
				FD3_interface.addPort("C", 1, 1);
				FD3_interface.addPort("D", 1, 1);
				FD3_interface.addPort("Q", 1, 2);
				//FD4
				EdifPort FD4_interface_C = new EdifPort(FD4_interface, "C", 1, 1);
				EdifPort FD4_interface_D = new EdifPort(FD4_interface, "D", 1, 1);
				EdifPort FD4_interface_Q = new EdifPort(FD4_interface, "Q", 1, 2);
				FD4_interface.addPort("C", 1, 1);
				FD4_interface.addPort("D", 1, 1);
				FD4_interface.addPort("Q", 1, 2);
				//FD5
				EdifPort FD5_interface_C = new EdifPort(FD5_interface, "C", 1, 1);
				EdifPort FD5_interface_D = new EdifPort(FD5_interface, "D", 1, 1);
				EdifPort FD5_interface_Q = new EdifPort(FD5_interface, "Q", 1, 2);
				FD5_interface.addPort("C", 1, 1);
				FD5_interface.addPort("D", 1, 1);
				FD5_interface.addPort("Q", 1, 2);
				//FD6
				EdifPort FD6_interface_C = new EdifPort(FD6_interface, "C", 1, 1);
				EdifPort FD6_interface_D = new EdifPort(FD6_interface, "D", 1, 1);
				EdifPort FD6_interface_Q = new EdifPort(FD6_interface, "Q", 1, 2);
				FD6_interface.addPort("C", 1, 1);
				FD6_interface.addPort("D", 1, 1);
				FD6_interface.addPort("Q", 1, 2);
				//FD7
				EdifPort FD7_interface_C = new EdifPort(FD7_interface, "C", 1, 1);
				EdifPort FD7_interface_D = new EdifPort(FD7_interface, "D", 1, 1);
				EdifPort FD7_interface_Q = new EdifPort(FD7_interface, "Q", 1, 2);
				FD7_interface.addPort("C", 1, 1);
				FD7_interface.addPort("D", 1, 1);
				FD7_interface.addPort("Q", 1, 2);
				//FD8
				EdifPort FD8_interface_C = new EdifPort(FD8_interface, "C", 1, 1);
				EdifPort FD8_interface_D = new EdifPort(FD8_interface, "D", 1, 1);
				EdifPort FD8_interface_Q = new EdifPort(FD8_interface, "Q", 1, 2);
				FD8_interface.addPort("C", 1, 1);
				FD8_interface.addPort("D", 1, 1);
				FD8_interface.addPort("Q", 1, 2);
				//FD9
				EdifPort FD9_interface_C = new EdifPort(FD9_interface, "C", 1, 1);
				EdifPort FD9_interface_D = new EdifPort(FD9_interface, "D", 1, 1);
				EdifPort FD9_interface_Q = new EdifPort(FD9_interface, "Q", 1, 2);
				FD9_interface.addPort("C", 1, 1);
				FD9_interface.addPort("D", 1, 1);
				FD9_interface.addPort("Q", 1, 2);
				//FD10
				EdifPort FD10_interface_C = new EdifPort(FD10_interface, "C", 1, 1);
				EdifPort FD10_interface_D = new EdifPort(FD10_interface, "D", 1, 1);
				EdifPort FD10_interface_Q = new EdifPort(FD10_interface, "Q", 1, 2);
				FD10_interface.addPort("C", 1, 1);
				FD10_interface.addPort("D", 1, 1);
				FD10_interface.addPort("Q", 1, 2);
				//FD11
				EdifPort FD11_interface_C = new EdifPort(FD11_interface, "C", 1, 1);
				EdifPort FD11_interface_D = new EdifPort(FD11_interface, "D", 1, 1);
				EdifPort FD11_interface_Q = new EdifPort(FD11_interface, "Q", 1, 2);
				FD11_interface.addPort("C", 1, 1);
				FD11_interface.addPort("D", 1, 1);
				FD11_interface.addPort("Q", 1, 2);
				//FD12
				EdifPort FD12_interface_C = new EdifPort(FD12_interface, "C", 1, 1);
				EdifPort FD12_interface_D = new EdifPort(FD12_interface, "D", 1, 1);
				EdifPort FD12_interface_Q = new EdifPort(FD12_interface, "Q", 1, 2);
				FD12_interface.addPort("C", 1, 1);
				FD12_interface.addPort("D", 1, 1);
				FD12_interface.addPort("Q", 1, 2);
				//FD13
				EdifPort FD13_interface_C = new EdifPort(FD13_interface, "C", 1, 1);
				EdifPort FD13_interface_D = new EdifPort(FD13_interface, "D", 1, 1);
				EdifPort FD13_interface_Q = new EdifPort(FD13_interface, "Q", 1, 2);
				FD13_interface.addPort("C", 1, 1);
				FD13_interface.addPort("D", 1, 1);
				FD13_interface.addPort("Q", 1, 2);
				//FD14
				EdifPort FD14_interface_C = new EdifPort(FD14_interface, "C", 1, 1);
				EdifPort FD14_interface_D = new EdifPort(FD14_interface, "D", 1, 1);
				EdifPort FD14_interface_Q = new EdifPort(FD14_interface, "Q", 1, 2);
				FD14_interface.addPort("C", 1, 1);
				FD14_interface.addPort("D", 1, 1);
				FD14_interface.addPort("Q", 1, 2);
				//FD15
				EdifPort FD15_interface_C = new EdifPort(FD15_interface, "C", 1, 1);
				EdifPort FD15_interface_D = new EdifPort(FD15_interface, "D", 1, 1);
				EdifPort FD15_interface_Q = new EdifPort(FD15_interface, "Q", 1, 2);
				FD15_interface.addPort("C", 1, 1);
				FD15_interface.addPort("D", 1, 1);
				FD15_interface.addPort("Q", 1, 2);
				//FD16
				EdifPort FD16_interface_C = new EdifPort(FD16_interface, "C", 1, 1);
				EdifPort FD16_interface_D = new EdifPort(FD16_interface, "D", 1, 1);
				EdifPort FD16_interface_Q = new EdifPort(FD16_interface, "Q", 1, 2);
				FD16_interface.addPort("C", 1, 1);
				FD16_interface.addPort("D", 1, 1);
				FD16_interface.addPort("Q", 1, 2);
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
				EdifPortRef FD1_C = new EdifPortRef(clk, FD1_interface_C.getSingleBitPort(0), FD1);
				EdifPortRef FD1_D = new EdifPortRef(d, FD1_interface_D.getSingleBitPort(0), FD1);
				EdifPortRef FD1_Q = new EdifPortRef(wire1, FD1_interface_Q.getSingleBitPort(0), FD1);
				EdifPortRef FD2_C = new EdifPortRef(clk, FD2_interface_C.getSingleBitPort(0), FD2);
				EdifPortRef FD2_D = new EdifPortRef(wire1, FD2_interface_D.getSingleBitPort(0), FD2);
				EdifPortRef FD2_Q = new EdifPortRef(wire2, FD2_interface_Q.getSingleBitPort(0), FD2);
				EdifPortRef FD3_C = new EdifPortRef(clk, FD3_interface_C.getSingleBitPort(0), FD3);
				EdifPortRef FD3_D = new EdifPortRef(wire2, FD3_interface_D.getSingleBitPort(0), FD3);
				EdifPortRef FD3_Q = new EdifPortRef(wire3, FD3_interface_Q.getSingleBitPort(0), FD3);
				EdifPortRef FD4_C = new EdifPortRef(clk, FD4_interface_C.getSingleBitPort(0), FD4);
				EdifPortRef FD4_D = new EdifPortRef(wire3, FD4_interface_D.getSingleBitPort(0), FD4);
				EdifPortRef FD4_Q = new EdifPortRef(wire4, FD4_interface_Q.getSingleBitPort(0), FD4);
				EdifPortRef FD5_C = new EdifPortRef(clk, FD5_interface_C.getSingleBitPort(0), FD5);
				EdifPortRef FD5_D = new EdifPortRef(wire4, FD5_interface_D.getSingleBitPort(0), FD5);
				EdifPortRef FD5_Q = new EdifPortRef(wire5, FD5_interface_Q.getSingleBitPort(0), FD5);
				EdifPortRef FD6_C = new EdifPortRef(clk, FD6_interface_C.getSingleBitPort(0), FD6);
				EdifPortRef FD6_D = new EdifPortRef(wire5, FD6_interface_D.getSingleBitPort(0), FD6);
				EdifPortRef FD6_Q = new EdifPortRef(wire6, FD6_interface_Q.getSingleBitPort(0), FD6);
				EdifPortRef FD7_C = new EdifPortRef(clk, FD7_interface_C.getSingleBitPort(0), FD7);
				EdifPortRef FD7_D = new EdifPortRef(wire6, FD7_interface_D.getSingleBitPort(0), FD7);
				EdifPortRef FD7_Q = new EdifPortRef(wire7, FD7_interface_Q.getSingleBitPort(0), FD7);
				EdifPortRef FD8_C = new EdifPortRef(clk, FD8_interface_C.getSingleBitPort(0), FD8);
				EdifPortRef FD8_D = new EdifPortRef(wire7, FD8_interface_D.getSingleBitPort(0), FD8);
				EdifPortRef FD8_Q = new EdifPortRef(wire8, FD8_interface_Q.getSingleBitPort(0), FD8);
				EdifPortRef FD9_C = new EdifPortRef(clk, FD9_interface_C.getSingleBitPort(0), FD9);
				EdifPortRef FD9_D = new EdifPortRef(wire8, FD9_interface_D.getSingleBitPort(0), FD9);
				EdifPortRef FD9_Q = new EdifPortRef(wire9, FD9_interface_Q.getSingleBitPort(0), FD9);
				EdifPortRef FD10_C = new EdifPortRef(clk, FD10_interface_C.getSingleBitPort(0), FD10);
				EdifPortRef FD10_D = new EdifPortRef(wire9, FD10_interface_D.getSingleBitPort(0), FD10);
				EdifPortRef FD10_Q = new EdifPortRef(wire10, FD10_interface_Q.getSingleBitPort(0), FD10);
				EdifPortRef FD11_C = new EdifPortRef(clk, FD11_interface_C.getSingleBitPort(0), FD11);
				EdifPortRef FD11_D = new EdifPortRef(wire10, FD11_interface_D.getSingleBitPort(0), FD11);
				EdifPortRef FD11_Q = new EdifPortRef(wire11, FD11_interface_Q.getSingleBitPort(0), FD11);
				EdifPortRef FD12_C = new EdifPortRef(clk, FD12_interface_C.getSingleBitPort(0), FD12);
				EdifPortRef FD12_D = new EdifPortRef(wire11, FD12_interface_D.getSingleBitPort(0), FD12);
				EdifPortRef FD12_Q = new EdifPortRef(wire12, FD12_interface_Q.getSingleBitPort(0), FD12);
				EdifPortRef FD13_C = new EdifPortRef(clk, FD13_interface_C.getSingleBitPort(0), FD13);
				EdifPortRef FD13_D = new EdifPortRef(wire12, FD13_interface_D.getSingleBitPort(0), FD13);
				EdifPortRef FD13_Q = new EdifPortRef(wire13, FD13_interface_Q.getSingleBitPort(0), FD13);
				EdifPortRef FD14_C = new EdifPortRef(clk, FD14_interface_C.getSingleBitPort(0), FD14);
				EdifPortRef FD14_D = new EdifPortRef(wire13, FD14_interface_D.getSingleBitPort(0), FD14);
				EdifPortRef FD14_Q = new EdifPortRef(wire14, FD14_interface_Q.getSingleBitPort(0), FD14);
				EdifPortRef FD15_C = new EdifPortRef(clk, FD15_interface_C.getSingleBitPort(0), FD15);
				EdifPortRef FD15_D = new EdifPortRef(wire14, FD15_interface_D.getSingleBitPort(0), FD15);
				EdifPortRef FD15_Q = new EdifPortRef(wire15, FD15_interface_Q.getSingleBitPort(0), FD15);
				EdifPortRef FD16_C = new EdifPortRef(clk, FD16_interface_C.getSingleBitPort(0), FD16);
				EdifPortRef FD16_D = new EdifPortRef(wire15, FD16_interface_D.getSingleBitPort(0), FD16);
				EdifPortRef FD16_Q = new EdifPortRef(q15, FD16_interface_Q.getSingleBitPort(0), FD16);
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
				EdifPortRef LUT3_8_I2 = new EdifPortRef(q15, LUT3_8_interface_I2.getSingleBitPort(0), LUT3_8);
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
				clk.addPortConnection(FD1_C);
				clk.addPortConnection(FD2_C);
				clk.addPortConnection(FD3_C);
				clk.addPortConnection(FD4_C);
				clk.addPortConnection(FD5_C);
				clk.addPortConnection(FD6_C);
				clk.addPortConnection(FD7_C);
				clk.addPortConnection(FD8_C);
				clk.addPortConnection(FD9_C);
				clk.addPortConnection(FD10_C);
				clk.addPortConnection(FD11_C);
				clk.addPortConnection(FD12_C);
				clk.addPortConnection(FD13_C);
				clk.addPortConnection(FD14_C);
				clk.addPortConnection(FD15_C);
				clk.addPortConnection(FD16_C);
				//d
				d.addPortConnection(FD1_D);
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
				//wire1
				wire1.addPortConnection(LUT3_1_I1);
				wire1.addPortConnection(FD1_Q);
				wire1.addPortConnection(FD2_D);
				//wire2
				wire2.addPortConnection(LUT3_5_I1);
				wire2.addPortConnection(FD2_Q);
				wire2.addPortConnection(FD3_D);
				//wire3
				wire3.addPortConnection(LUT3_3_I1);
				wire3.addPortConnection(FD3_Q);
				wire3.addPortConnection(FD4_D);
				//wire4
				wire4.addPortConnection(LUT3_7_I1);
				wire4.addPortConnection(FD4_Q);
				wire4.addPortConnection(FD5_D);
				//wire5
				wire5.addPortConnection(LUT3_2_I1);
				wire5.addPortConnection(FD5_Q);
				wire5.addPortConnection(FD6_D);
				//wire6
				wire6.addPortConnection(LUT3_6_I1);
				wire6.addPortConnection(FD6_Q);
				wire6.addPortConnection(FD7_D);
				//wire7
				wire7.addPortConnection(LUT3_4_I1);
				wire7.addPortConnection(FD7_Q);
				wire7.addPortConnection(FD8_D);
				//wire8
				wire8.addPortConnection(LUT3_8_I1);
				wire8.addPortConnection(FD8_Q);
				wire8.addPortConnection(FD9_D);
				//wire9
				wire9.addPortConnection(LUT3_1_I2);
				wire9.addPortConnection(FD9_Q);
				wire9.addPortConnection(FD10_D);
				//wire10
				wire10.addPortConnection(LUT3_5_I2);
				wire10.addPortConnection(FD10_Q);
				wire10.addPortConnection(FD11_D);
				//wire11
				wire11.addPortConnection(LUT3_3_I2);
				wire11.addPortConnection(FD11_Q);
				wire11.addPortConnection(FD12_D);
				//wire12
				wire12.addPortConnection(LUT3_7_I2);
				wire12.addPortConnection(FD12_Q);
				wire12.addPortConnection(FD13_D);
				//wire13
				wire13.addPortConnection(LUT3_2_I2);
				wire13.addPortConnection(FD13_Q);
				wire13.addPortConnection(FD14_D);
				//wire14
				wire14.addPortConnection(LUT3_6_I2);
				wire14.addPortConnection(FD14_Q);
				wire14.addPortConnection(FD15_D);
				//wire15
				wire15.addPortConnection(LUT3_4_I2);
				wire15.addPortConnection(FD15_Q);
				wire15.addPortConnection(FD16_D);
				//q15
				q15.addPortConnection(LUT3_8_I2);
				q15.addPortConnection(FD16_Q);
				//q
				q.addPortConnection(LUT3_9_O);
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
		        //Set INIT Property for FD1
				PropertyList FD1_propertylist = FD1.getPropertyList();
				if (FD1_propertylist != null) {
                    for (Property FD1_property : FD1_propertylist.values()) {
                        if (FD1_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD1_property.setValue(valueOne);
                            else
                                FD1_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD1.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD1.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD2
				PropertyList FD2_propertylist = FD2.getPropertyList();
				if (FD2_propertylist != null) {
                    for (Property FD2_property : FD2_propertylist.values()) {
                        if (FD2_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD2_property.setValue(valueOne);
                            else
                                FD2_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD2.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD2.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD3
				PropertyList FD3_propertylist = FD3.getPropertyList();
				if (FD3_propertylist != null) {
                    for (Property FD3_property : FD3_propertylist.values()) {
                        if (FD3_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD3_property.setValue(valueOne);
                            else
                                FD3_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD3.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD3.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD4
				PropertyList FD4_propertylist = FD4.getPropertyList();
				if (FD4_propertylist != null) {
                    for (Property FD4_property : FD4_propertylist.values()) {
                        if (FD4_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD4_property.setValue(valueOne);
                            else
                                FD4_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD4.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD4.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD5
				PropertyList FD5_propertylist = FD5.getPropertyList();
				if (FD5_propertylist != null) {
                    for (Property FD5_property : FD5_propertylist.values()) {
                        if (FD5_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD5_property.setValue(valueOne);
                            else
                                FD5_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD5.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD5.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD6
				PropertyList FD6_propertylist = FD6.getPropertyList();
				if (FD6_propertylist != null) {
                    for (Property FD6_property : FD6_propertylist.values()) {
                        if (FD6_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD6_property.setValue(valueOne);
                            else
                                FD6_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD6.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD6.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD7
				PropertyList FD7_propertylist = FD7.getPropertyList();
				if (FD7_propertylist != null) {
                    for (Property FD7_property : FD7_propertylist.values()) {
                        if (FD7_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD7_property.setValue(valueOne);
                            else
                                FD7_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD7.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD7.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD8
				PropertyList FD8_propertylist = FD8.getPropertyList();
				if (FD8_propertylist != null) {
                    for (Property FD8_property : FD8_propertylist.values()) {
                        if (FD8_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD8_property.setValue(valueOne);
                            else
                                FD8_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD8.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD8.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD9
				PropertyList FD9_propertylist = FD9.getPropertyList();
				if (FD9_propertylist != null) {
                    for (Property FD9_property : FD9_propertylist.values()) {
                        if (FD9_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD9_property.setValue(valueOne);
                            else
                                FD9_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD9.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD9.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD10
				PropertyList FD10_propertylist = FD10.getPropertyList();
				if (FD10_propertylist != null) {
                    for (Property FD10_property : FD10_propertylist.values()) {
                        if (FD10_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD10_property.setValue(valueOne);
                            else
                                FD10_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD10.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD10.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD11
				PropertyList FD11_propertylist = FD11.getPropertyList();
				if (FD11_propertylist != null) {
                    for (Property FD11_property : FD11_propertylist.values()) {
                        if (FD11_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD11_property.setValue(valueOne);
                            else
                                FD11_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD11.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD11.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD12
				PropertyList FD12_propertylist = FD12.getPropertyList();
				if (FD12_propertylist != null) {
                    for (Property FD12_property : FD12_propertylist.values()) {
                        if (FD12_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD12_property.setValue(valueOne);
                            else
                                FD12_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD12.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD12.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD13
				PropertyList FD13_propertylist = FD13.getPropertyList();
				if (FD13_propertylist != null) {
                    for (Property FD13_property : FD13_propertylist.values()) {
                        if (FD13_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD13_property.setValue(valueOne);
                            else
                                FD13_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD13.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD13.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD14
				PropertyList FD14_propertylist = FD14.getPropertyList();
				if (FD14_propertylist != null) {
                    for (Property FD14_property : FD14_propertylist.values()) {
                        if (FD14_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD14_property.setValue(valueOne);
                            else
                                FD14_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD14.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD14.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD15
				PropertyList FD15_propertylist = FD15.getPropertyList();
				if (FD15_propertylist != null) {
                    for (Property FD15_property : FD15_propertylist.values()) {
                        if (FD15_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD15_property.setValue(valueOne);
                            else
                                FD15_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD15.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD15.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD16
				PropertyList FD16_propertylist = FD16.getPropertyList();
				if (FD16_propertylist != null) {
                    for (Property FD16_property : FD16_propertylist.values()) {
                        if (FD16_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD16_property.setValue(valueOne);
                            else
                                FD16_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD16.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD16.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
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
