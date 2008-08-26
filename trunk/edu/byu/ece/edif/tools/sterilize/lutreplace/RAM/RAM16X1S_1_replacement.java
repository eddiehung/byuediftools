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
 * Generates the EDIF file for RAM16X1S_1
 * Includes: 16 FDE_1s, 1 16-1 MUXs(9 LUT3s, 4 MUXF5s, and 2 MUXF6s), 1 4-16 decoder(2 LUT2s, 16 LUT4s).
 * 
 * @author Yubo Li
 *
 */

public class RAM16X1S_1_replacement{
	public static void main(String args[]){
		
		String outputFileName = "RAM16X1S_1_replacement.edf";
		
		try{
			/******Environment and Environment manager******/
			EdifEnvironment topLevel = new EdifEnvironment("RAM16X1S_1_replacement");
			EdifLibraryManager manager = new EdifLibraryManager(topLevel);
			try{
				/******Libraries******/
				EdifLibrary work = new EdifLibrary(manager, "work");
				topLevel.addLibrary(work);
				
				/******Cells******/
				EdifCell BUFGP = XilinxLibrary.findOrAddXilinxPrimitive(manager, "BUFGP");
				EdifCell IBUF = XilinxLibrary.findOrAddXilinxPrimitive(manager, "IBUF");
				EdifCell OBUF = XilinxLibrary.findOrAddXilinxPrimitive(manager, "OBUF");
				EdifCell FDE_1 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "FDE_1");
				EdifCell LUT2 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "LUT2");
				EdifCell LUT3 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "LUT3");
				EdifCell LUT4 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "LUT4");
				EdifCell MUXF5 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "MUXF5");
				EdifCell MUXF6 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "MUXF6");		
				EdifCell RAM16X1S_1_replacement = new EdifCell(work, "RAM16X1S_1_replacement");
				RAM16X1S_1_replacement.addPort("WE", 1, 1);
				RAM16X1S_1_replacement.addPort("D", 1, 1);
				RAM16X1S_1_replacement.addPort("WCLK", 1, 1);
				RAM16X1S_1_replacement.addPort("A0", 1, 1);
				RAM16X1S_1_replacement.addPort("A1", 1, 1);
				RAM16X1S_1_replacement.addPort("A2", 1, 1);
				RAM16X1S_1_replacement.addPort("A3", 1, 1);
				RAM16X1S_1_replacement.addPort("O", 1, 2);
				
				/******Design******/
				EdifDesign design = new EdifDesign("RAM16X1S_1_replacement");
				topLevel.setTopDesign(design);
				topLevel.setTopCell(RAM16X1S_1_replacement);
				
				/******Nets******/
				EdifNet wclk_c = new EdifNet("wclk_c", RAM16X1S_1_replacement);
				EdifNet wclk = new EdifNet("wclk", RAM16X1S_1_replacement);
				EdifNet d_c = new EdifNet("d_c", RAM16X1S_1_replacement);
				EdifNet d = new EdifNet("d", RAM16X1S_1_replacement);
				EdifNet we_c = new EdifNet("we_c", RAM16X1S_1_replacement);
				EdifNet we = new EdifNet("we", RAM16X1S_1_replacement);
				EdifNet a0_c = new EdifNet("a0_c", RAM16X1S_1_replacement);
				EdifNet a0 = new EdifNet("a0", RAM16X1S_1_replacement);
				EdifNet a1_c = new EdifNet("a1_c", RAM16X1S_1_replacement);
				EdifNet a1 = new EdifNet("a1", RAM16X1S_1_replacement);
				EdifNet a2_c = new EdifNet("a2_c", RAM16X1S_1_replacement);
				EdifNet a2 = new EdifNet("a2", RAM16X1S_1_replacement);
				EdifNet a3_c = new EdifNet("a3_c", RAM16X1S_1_replacement);
				EdifNet a3 = new EdifNet("a3", RAM16X1S_1_replacement);
				EdifNet o_c = new EdifNet("o_c", RAM16X1S_1_replacement);
				EdifNet o = new EdifNet("o", RAM16X1S_1_replacement);
				EdifNet wire1 = new EdifNet("wire1", RAM16X1S_1_replacement);
				EdifNet wire2 = new EdifNet("wire2", RAM16X1S_1_replacement);
				EdifNet wire3 = new EdifNet("wire3", RAM16X1S_1_replacement);
				EdifNet wire4 = new EdifNet("wire4", RAM16X1S_1_replacement);
				EdifNet wire5 = new EdifNet("wire5", RAM16X1S_1_replacement);
				EdifNet wire6 = new EdifNet("wire6", RAM16X1S_1_replacement);
				EdifNet wire7 = new EdifNet("wire7", RAM16X1S_1_replacement);
				EdifNet wire8 = new EdifNet("wire8", RAM16X1S_1_replacement);
				EdifNet wire9 = new EdifNet("wire9", RAM16X1S_1_replacement);
				EdifNet wire10 = new EdifNet("wire10", RAM16X1S_1_replacement);
				EdifNet wire11 = new EdifNet("wire11", RAM16X1S_1_replacement);
				EdifNet wire12 = new EdifNet("wire12", RAM16X1S_1_replacement);
				EdifNet wire13 = new EdifNet("wire13", RAM16X1S_1_replacement);
				EdifNet wire14 = new EdifNet("wire14", RAM16X1S_1_replacement);
				EdifNet wire15 = new EdifNet("wire15", RAM16X1S_1_replacement);
				EdifNet wire16 = new EdifNet("wire16", RAM16X1S_1_replacement);
				EdifNet net1 = new EdifNet("net1", RAM16X1S_1_replacement);
				EdifNet net2 = new EdifNet("net2", RAM16X1S_1_replacement);
				EdifNet net3 = new EdifNet("net3", RAM16X1S_1_replacement);
				EdifNet net4 = new EdifNet("net4", RAM16X1S_1_replacement);
				EdifNet net5 = new EdifNet("net5", RAM16X1S_1_replacement);
				EdifNet net6 = new EdifNet("net6", RAM16X1S_1_replacement);
				EdifNet net7 = new EdifNet("net7", RAM16X1S_1_replacement);
				EdifNet net8 = new EdifNet("net8", RAM16X1S_1_replacement);
				EdifNet net9 = new EdifNet("net9", RAM16X1S_1_replacement);
				EdifNet net10 = new EdifNet("net10", RAM16X1S_1_replacement);
				EdifNet net11 = new EdifNet("net11", RAM16X1S_1_replacement);
				EdifNet net12 = new EdifNet("net12", RAM16X1S_1_replacement);
				EdifNet net13 = new EdifNet("net13", RAM16X1S_1_replacement);
				EdifNet net14 = new EdifNet("net14", RAM16X1S_1_replacement);
				EdifNet net15 = new EdifNet("net15", RAM16X1S_1_replacement);
				EdifNet net16 = new EdifNet("net16", RAM16X1S_1_replacement);
				EdifNet e1 = new EdifNet("e1", RAM16X1S_1_replacement);
				EdifNet e2 = new EdifNet("e2", RAM16X1S_1_replacement);
				EdifNet e3 = new EdifNet("e3", RAM16X1S_1_replacement);
				EdifNet e4 = new EdifNet("e4", RAM16X1S_1_replacement);
				EdifNet e5 = new EdifNet("e5", RAM16X1S_1_replacement);
				EdifNet e6 = new EdifNet("e6", RAM16X1S_1_replacement);
				EdifNet e7 = new EdifNet("e7", RAM16X1S_1_replacement);
				EdifNet e8 = new EdifNet("e8", RAM16X1S_1_replacement);
				EdifNet e9 = new EdifNet("e9", RAM16X1S_1_replacement);
				EdifNet e10 = new EdifNet("e10", RAM16X1S_1_replacement);
				EdifNet e11 = new EdifNet("e11", RAM16X1S_1_replacement);
				EdifNet e12 = new EdifNet("e12", RAM16X1S_1_replacement);
				EdifNet e13 = new EdifNet("e13", RAM16X1S_1_replacement);
				EdifNet e14 = new EdifNet("e14", RAM16X1S_1_replacement);
				EdifNet e15 = new EdifNet("e15", RAM16X1S_1_replacement);
				EdifNet e16 = new EdifNet("e16", RAM16X1S_1_replacement);
				RAM16X1S_1_replacement.addNet(wclk_c);
				RAM16X1S_1_replacement.addNet(wclk);
				RAM16X1S_1_replacement.addNet(d_c);
				RAM16X1S_1_replacement.addNet(d);
				RAM16X1S_1_replacement.addNet(we_c);
				RAM16X1S_1_replacement.addNet(we);
				RAM16X1S_1_replacement.addNet(a0_c);
				RAM16X1S_1_replacement.addNet(a0);
				RAM16X1S_1_replacement.addNet(a1_c);
				RAM16X1S_1_replacement.addNet(a1);
				RAM16X1S_1_replacement.addNet(a2_c);
				RAM16X1S_1_replacement.addNet(a2);
				RAM16X1S_1_replacement.addNet(a3_c);
				RAM16X1S_1_replacement.addNet(a3);
				RAM16X1S_1_replacement.addNet(o_c);
				RAM16X1S_1_replacement.addNet(o);
				RAM16X1S_1_replacement.addNet(wire1);
				RAM16X1S_1_replacement.addNet(wire2);
				RAM16X1S_1_replacement.addNet(wire3);
				RAM16X1S_1_replacement.addNet(wire4);
				RAM16X1S_1_replacement.addNet(wire5);
				RAM16X1S_1_replacement.addNet(wire6);
				RAM16X1S_1_replacement.addNet(wire7);
				RAM16X1S_1_replacement.addNet(wire8);
				RAM16X1S_1_replacement.addNet(wire9);
				RAM16X1S_1_replacement.addNet(wire10);
				RAM16X1S_1_replacement.addNet(wire11);
				RAM16X1S_1_replacement.addNet(wire12);
				RAM16X1S_1_replacement.addNet(wire13);
				RAM16X1S_1_replacement.addNet(wire14);
				RAM16X1S_1_replacement.addNet(wire15);
				RAM16X1S_1_replacement.addNet(wire16);
				RAM16X1S_1_replacement.addNet(net1);
				RAM16X1S_1_replacement.addNet(net2);
				RAM16X1S_1_replacement.addNet(net3);
				RAM16X1S_1_replacement.addNet(net4);
				RAM16X1S_1_replacement.addNet(net5);
				RAM16X1S_1_replacement.addNet(net6);
				RAM16X1S_1_replacement.addNet(net7);
				RAM16X1S_1_replacement.addNet(net8);
				RAM16X1S_1_replacement.addNet(net9);
				RAM16X1S_1_replacement.addNet(net10);
				RAM16X1S_1_replacement.addNet(net11);
				RAM16X1S_1_replacement.addNet(net12);
				RAM16X1S_1_replacement.addNet(net13);
				RAM16X1S_1_replacement.addNet(net14);
				RAM16X1S_1_replacement.addNet(net15);
				RAM16X1S_1_replacement.addNet(net16);
				RAM16X1S_1_replacement.addNet(e1);
				RAM16X1S_1_replacement.addNet(e2);
				RAM16X1S_1_replacement.addNet(e3);
				RAM16X1S_1_replacement.addNet(e4);
				RAM16X1S_1_replacement.addNet(e5);
				RAM16X1S_1_replacement.addNet(e6);
				RAM16X1S_1_replacement.addNet(e7);
				RAM16X1S_1_replacement.addNet(e8);
				RAM16X1S_1_replacement.addNet(e9);
				RAM16X1S_1_replacement.addNet(e10);
				RAM16X1S_1_replacement.addNet(e11);
				RAM16X1S_1_replacement.addNet(e12);
				RAM16X1S_1_replacement.addNet(e13);
				RAM16X1S_1_replacement.addNet(e14);
				RAM16X1S_1_replacement.addNet(e15);
				RAM16X1S_1_replacement.addNet(e16);
				
				/******Instances******/
				EdifCellInstance WE_ibuf = new EdifCellInstance("WE_ibuf", RAM16X1S_1_replacement, IBUF);
				EdifCellInstance D_ibuf = new EdifCellInstance("D_ibuf", RAM16X1S_1_replacement, IBUF);
				EdifCellInstance WCLK_ibuf = new EdifCellInstance("WCLK_ibuf", RAM16X1S_1_replacement, BUFGP);
				EdifCellInstance A0_ibuf = new EdifCellInstance("A0_ibuf", RAM16X1S_1_replacement, IBUF);
				EdifCellInstance A1_ibuf = new EdifCellInstance("A1_ibuf", RAM16X1S_1_replacement, IBUF);
				EdifCellInstance A2_ibuf = new EdifCellInstance("A2_ibuf", RAM16X1S_1_replacement, IBUF);
				EdifCellInstance A3_ibuf = new EdifCellInstance("A3_ibuf", RAM16X1S_1_replacement, IBUF);
				EdifCellInstance O_obuf = new EdifCellInstance("O_obuf", RAM16X1S_1_replacement, OBUF);
				EdifCellInstance FDE_1_1 = new EdifCellInstance("FDE_1_1", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_2 = new EdifCellInstance("FDE_1_2", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_3 = new EdifCellInstance("FDE_1_3", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_4 = new EdifCellInstance("FDE_1_4", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_5 = new EdifCellInstance("FDE_1_5", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_6 = new EdifCellInstance("FDE_1_6", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_7 = new EdifCellInstance("FDE_1_7", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_8 = new EdifCellInstance("FDE_1_8", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_9 = new EdifCellInstance("FDE_1_9", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_10 = new EdifCellInstance("FDE_1_10", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_11 = new EdifCellInstance("FDE_1_11", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_12 = new EdifCellInstance("FDE_1_12", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_13 = new EdifCellInstance("FDE_1_13", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_14 = new EdifCellInstance("FDE_1_14", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_15 = new EdifCellInstance("FDE_1_15", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_16 = new EdifCellInstance("FDE_1_16", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance LUT2_1 = new EdifCellInstance("LUT2_1", RAM16X1S_1_replacement, LUT2);
				EdifCellInstance LUT2_2 = new EdifCellInstance("LUT2_2", RAM16X1S_1_replacement, LUT2);
				EdifCellInstance LUT3_1 = new EdifCellInstance("LUT3_1", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_2 = new EdifCellInstance("LUT3_2", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_3 = new EdifCellInstance("LUT3_3", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_4 = new EdifCellInstance("LUT3_4", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_5 = new EdifCellInstance("LUT3_5", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_6 = new EdifCellInstance("LUT3_6", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_7 = new EdifCellInstance("LUT3_7", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_8 = new EdifCellInstance("LUT3_8", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_9 = new EdifCellInstance("LUT3_9", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT4_1 = new EdifCellInstance("LUT4_1", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_2 = new EdifCellInstance("LUT4_2", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_3 = new EdifCellInstance("LUT4_3", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_4 = new EdifCellInstance("LUT4_4", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_5 = new EdifCellInstance("LUT4_5", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_6 = new EdifCellInstance("LUT4_6", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_7 = new EdifCellInstance("LUT4_7", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_8 = new EdifCellInstance("LUT4_8", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_9 = new EdifCellInstance("LUT4_9", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_10 = new EdifCellInstance("LUT4_10", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_11 = new EdifCellInstance("LUT4_11", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_12 = new EdifCellInstance("LUT4_12", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_13 = new EdifCellInstance("LUT4_13", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_14 = new EdifCellInstance("LUT4_14", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_15 = new EdifCellInstance("LUT4_15", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_16 = new EdifCellInstance("LUT4_16", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance MUXF5_1 = new EdifCellInstance("MUXF5_1", RAM16X1S_1_replacement, MUXF5);
				EdifCellInstance MUXF5_2 = new EdifCellInstance("MUXF5_2", RAM16X1S_1_replacement, MUXF5);
				EdifCellInstance MUXF5_3 = new EdifCellInstance("MUXF5_3", RAM16X1S_1_replacement, MUXF5);
				EdifCellInstance MUXF5_4 = new EdifCellInstance("MUXF5_4", RAM16X1S_1_replacement, MUXF5);
				EdifCellInstance MUXF6_1 = new EdifCellInstance("MUXF6_1", RAM16X1S_1_replacement, MUXF6);
				EdifCellInstance MUXF6_2 = new EdifCellInstance("MUXF6_2", RAM16X1S_1_replacement, MUXF6);
				RAM16X1S_1_replacement.addSubCell(WE_ibuf);
				RAM16X1S_1_replacement.addSubCell(D_ibuf);
				RAM16X1S_1_replacement.addSubCell(WCLK_ibuf);
				RAM16X1S_1_replacement.addSubCell(A0_ibuf);
				RAM16X1S_1_replacement.addSubCell(A1_ibuf);
				RAM16X1S_1_replacement.addSubCell(A2_ibuf);
				RAM16X1S_1_replacement.addSubCell(A3_ibuf);
				RAM16X1S_1_replacement.addSubCell(O_obuf);
				RAM16X1S_1_replacement.addSubCell(FDE_1_1);
				RAM16X1S_1_replacement.addSubCell(FDE_1_2);
				RAM16X1S_1_replacement.addSubCell(FDE_1_3);
				RAM16X1S_1_replacement.addSubCell(FDE_1_4);
				RAM16X1S_1_replacement.addSubCell(FDE_1_5);
				RAM16X1S_1_replacement.addSubCell(FDE_1_6);
				RAM16X1S_1_replacement.addSubCell(FDE_1_7);
				RAM16X1S_1_replacement.addSubCell(FDE_1_8);
				RAM16X1S_1_replacement.addSubCell(FDE_1_9);
				RAM16X1S_1_replacement.addSubCell(FDE_1_10);
				RAM16X1S_1_replacement.addSubCell(FDE_1_11);
				RAM16X1S_1_replacement.addSubCell(FDE_1_12);
				RAM16X1S_1_replacement.addSubCell(FDE_1_13);
				RAM16X1S_1_replacement.addSubCell(FDE_1_14);
				RAM16X1S_1_replacement.addSubCell(FDE_1_15);
				RAM16X1S_1_replacement.addSubCell(FDE_1_16);
				RAM16X1S_1_replacement.addSubCell(LUT2_1);
				RAM16X1S_1_replacement.addSubCell(LUT2_2);
				RAM16X1S_1_replacement.addSubCell(LUT3_1);
				RAM16X1S_1_replacement.addSubCell(LUT3_2);
				RAM16X1S_1_replacement.addSubCell(LUT3_3);
				RAM16X1S_1_replacement.addSubCell(LUT3_4);
				RAM16X1S_1_replacement.addSubCell(LUT3_5);
				RAM16X1S_1_replacement.addSubCell(LUT3_6);
				RAM16X1S_1_replacement.addSubCell(LUT3_7);
				RAM16X1S_1_replacement.addSubCell(LUT3_8);
				RAM16X1S_1_replacement.addSubCell(LUT3_9);
				RAM16X1S_1_replacement.addSubCell(LUT4_1);
				RAM16X1S_1_replacement.addSubCell(LUT4_2);
				RAM16X1S_1_replacement.addSubCell(LUT4_3);
				RAM16X1S_1_replacement.addSubCell(LUT4_4);
				RAM16X1S_1_replacement.addSubCell(LUT4_5);
				RAM16X1S_1_replacement.addSubCell(LUT4_6);
				RAM16X1S_1_replacement.addSubCell(LUT4_7);
				RAM16X1S_1_replacement.addSubCell(LUT4_8);
				RAM16X1S_1_replacement.addSubCell(LUT4_9);
				RAM16X1S_1_replacement.addSubCell(LUT4_10);
				RAM16X1S_1_replacement.addSubCell(LUT4_11);
				RAM16X1S_1_replacement.addSubCell(LUT4_12);
				RAM16X1S_1_replacement.addSubCell(LUT4_13);
				RAM16X1S_1_replacement.addSubCell(LUT4_14);
				RAM16X1S_1_replacement.addSubCell(LUT4_15);
				RAM16X1S_1_replacement.addSubCell(LUT4_16);
				RAM16X1S_1_replacement.addSubCell(MUXF5_1);
				RAM16X1S_1_replacement.addSubCell(MUXF5_2);
				RAM16X1S_1_replacement.addSubCell(MUXF5_3);
				RAM16X1S_1_replacement.addSubCell(MUXF5_4);
				RAM16X1S_1_replacement.addSubCell(MUXF6_1);
				RAM16X1S_1_replacement.addSubCell(MUXF6_2);
				
				/******Interface******/
				EdifCellInterface RAM16X1S_1_replacement_interface = new EdifCellInterface(RAM16X1S_1_replacement);
				EdifCellInterface WE_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface WCLK_ibuf_interface = new EdifCellInterface(BUFGP);
				EdifCellInterface A0_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A1_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A2_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A3_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface O_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface FDE_1_1_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_2_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_3_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_4_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_5_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_6_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_7_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_8_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_9_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_10_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_11_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_12_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_13_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_14_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_15_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_16_interface = new EdifCellInterface(FDE_1);
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
				EdifCellInterface MUXF6_1_interface = new EdifCellInterface(MUXF6);
				EdifCellInterface MUXF6_2_interface = new EdifCellInterface(MUXF6);
				
				/******Ports******/
				//RAM16X1S_1_replacement
				EdifPort RAM16X1S_1_replacement_interface_WE = new EdifPort(RAM16X1S_1_replacement_interface, "WE", 1, 1);
				EdifPort RAM16X1S_1_replacement_interface_D = new EdifPort(RAM16X1S_1_replacement_interface, "D", 1, 1);
				EdifPort RAM16X1S_1_replacement_interface_WCLK = new EdifPort(RAM16X1S_1_replacement_interface, "WCLK", 1, 1);
				EdifPort RAM16X1S_1_replacement_interface_A0 = new EdifPort(RAM16X1S_1_replacement_interface, "A0", 1, 1);
				EdifPort RAM16X1S_1_replacement_interface_A1 = new EdifPort(RAM16X1S_1_replacement_interface, "A1", 1, 1);
				EdifPort RAM16X1S_1_replacement_interface_A2 = new EdifPort(RAM16X1S_1_replacement_interface, "A2", 1, 1);
				EdifPort RAM16X1S_1_replacement_interface_A3 = new EdifPort(RAM16X1S_1_replacement_interface, "A3", 1, 1);
				EdifPort RAM16X1S_1_replacement_interface_O = new EdifPort(RAM16X1S_1_replacement_interface, "O", 1, 2);
				RAM16X1S_1_replacement_interface.addPort("WE", 1, 1);
				RAM16X1S_1_replacement_interface.addPort("D", 1, 1);
				RAM16X1S_1_replacement_interface.addPort("WCLK", 1, 1);
				RAM16X1S_1_replacement_interface.addPort("A0", 1, 1);
				RAM16X1S_1_replacement_interface.addPort("A1", 1, 1);
				RAM16X1S_1_replacement_interface.addPort("A2", 1, 1);
				RAM16X1S_1_replacement_interface.addPort("A3", 1, 1);
				RAM16X1S_1_replacement_interface.addPort("O", 1, 2);
				//WE_ibuf
				EdifPort WE_ibuf_interface_I = new EdifPort(WE_ibuf_interface, "I", 1, 1);
				EdifPort WE_ibuf_interface_O = new EdifPort(WE_ibuf_interface, "O", 1, 2);
				WE_ibuf_interface.addPort("I", 1, 1);
				WE_ibuf_interface.addPort("O", 1, 2);
				//D_ibuf
				EdifPort D_ibuf_interface_I = new EdifPort(D_ibuf_interface, "I", 1, 1);
				EdifPort D_ibuf_interface_O = new EdifPort(D_ibuf_interface, "O", 1, 2);
				D_ibuf_interface.addPort("I", 1, 1);
				D_ibuf_interface.addPort("O", 1, 2);
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
				//O_obuf
				EdifPort O_obuf_interface_I = new EdifPort(O_obuf_interface, "I", 1, 1);
				EdifPort O_obuf_interface_O = new EdifPort(O_obuf_interface, "O", 1, 2);
				O_obuf_interface.addPort("I", 1, 1);
				O_obuf_interface.addPort("O", 1, 2);
				//FDE_1_1
				EdifPort FDE_1_1_interface_C = new EdifPort(FDE_1_1_interface, "C", 1, 1);
				EdifPort FDE_1_1_interface_D = new EdifPort(FDE_1_1_interface, "D", 1, 1);
				EdifPort FDE_1_1_interface_CE = new EdifPort(FDE_1_1_interface, "CE", 1, 1);
				EdifPort FDE_1_1_interface_Q = new EdifPort(FDE_1_1_interface, "Q", 1, 2);
				FDE_1_1_interface.addPort("C", 1, 1);
				FDE_1_1_interface.addPort("D", 1, 1);
				FDE_1_1_interface.addPort("CE", 1, 1);
				FDE_1_1_interface.addPort("Q", 1, 2);
				//FDE_1_2
				EdifPort FDE_1_2_interface_C = new EdifPort(FDE_1_2_interface, "C", 1, 1);
				EdifPort FDE_1_2_interface_D = new EdifPort(FDE_1_2_interface, "D", 1, 1);
				EdifPort FDE_1_2_interface_CE = new EdifPort(FDE_1_2_interface, "CE", 1, 1);
				EdifPort FDE_1_2_interface_Q = new EdifPort(FDE_1_2_interface, "Q", 1, 2);
				FDE_1_2_interface.addPort("C", 1, 1);
				FDE_1_2_interface.addPort("D", 1, 1);
				FDE_1_2_interface.addPort("CE", 1, 1);
				FDE_1_2_interface.addPort("Q", 1, 2);
				//FDE_1_3
				EdifPort FDE_1_3_interface_C = new EdifPort(FDE_1_3_interface, "C", 1, 1);
				EdifPort FDE_1_3_interface_D = new EdifPort(FDE_1_3_interface, "D", 1, 1);
				EdifPort FDE_1_3_interface_CE = new EdifPort(FDE_1_3_interface, "CE", 1, 1);
				EdifPort FDE_1_3_interface_Q = new EdifPort(FDE_1_3_interface, "Q", 1, 2);
				FDE_1_3_interface.addPort("C", 1, 1);
				FDE_1_3_interface.addPort("D", 1, 1);
				FDE_1_3_interface.addPort("CE", 1, 1);
				FDE_1_3_interface.addPort("Q", 1, 2);
				//FDE_1_4
				EdifPort FDE_1_4_interface_C = new EdifPort(FDE_1_4_interface, "C", 1, 1);
				EdifPort FDE_1_4_interface_D = new EdifPort(FDE_1_4_interface, "D", 1, 1);
				EdifPort FDE_1_4_interface_CE = new EdifPort(FDE_1_4_interface, "CE", 1, 1);
				EdifPort FDE_1_4_interface_Q = new EdifPort(FDE_1_4_interface, "Q", 1, 2);
				FDE_1_4_interface.addPort("C", 1, 1);
				FDE_1_4_interface.addPort("D", 1, 1);
				FDE_1_4_interface.addPort("CE", 1, 1);
				FDE_1_4_interface.addPort("Q", 1, 2);
				//FDE_1_5
				EdifPort FDE_1_5_interface_C = new EdifPort(FDE_1_5_interface, "C", 1, 1);
				EdifPort FDE_1_5_interface_D = new EdifPort(FDE_1_5_interface, "D", 1, 1);
				EdifPort FDE_1_5_interface_CE = new EdifPort(FDE_1_5_interface, "CE", 1, 1);
				EdifPort FDE_1_5_interface_Q = new EdifPort(FDE_1_5_interface, "Q", 1, 2);
				FDE_1_5_interface.addPort("C", 1, 1);
				FDE_1_5_interface.addPort("D", 1, 1);
				FDE_1_5_interface.addPort("CE", 1, 1);
				FDE_1_5_interface.addPort("Q", 1, 2);
				//FDE_1_6
				EdifPort FDE_1_6_interface_C = new EdifPort(FDE_1_6_interface, "C", 1, 1);
				EdifPort FDE_1_6_interface_D = new EdifPort(FDE_1_6_interface, "D", 1, 1);
				EdifPort FDE_1_6_interface_CE = new EdifPort(FDE_1_6_interface, "CE", 1, 1);
				EdifPort FDE_1_6_interface_Q = new EdifPort(FDE_1_6_interface, "Q", 1, 2);
				FDE_1_6_interface.addPort("C", 1, 1);
				FDE_1_6_interface.addPort("D", 1, 1);
				FDE_1_6_interface.addPort("CE", 1, 1);
				FDE_1_6_interface.addPort("Q", 1, 2);
				//FDE_1_7
				EdifPort FDE_1_7_interface_C = new EdifPort(FDE_1_7_interface, "C", 1, 1);
				EdifPort FDE_1_7_interface_D = new EdifPort(FDE_1_7_interface, "D", 1, 1);
				EdifPort FDE_1_7_interface_CE = new EdifPort(FDE_1_7_interface, "CE", 1, 1);
				EdifPort FDE_1_7_interface_Q = new EdifPort(FDE_1_7_interface, "Q", 1, 2);
				FDE_1_7_interface.addPort("C", 1, 1);
				FDE_1_7_interface.addPort("D", 1, 1);
				FDE_1_7_interface.addPort("CE", 1, 1);
				FDE_1_7_interface.addPort("Q", 1, 2);
				//FDE_1_8
				EdifPort FDE_1_8_interface_C = new EdifPort(FDE_1_8_interface, "C", 1, 1);
				EdifPort FDE_1_8_interface_D = new EdifPort(FDE_1_8_interface, "D", 1, 1);
				EdifPort FDE_1_8_interface_CE = new EdifPort(FDE_1_8_interface, "CE", 1, 1);
				EdifPort FDE_1_8_interface_Q = new EdifPort(FDE_1_8_interface, "Q", 1, 2);
				FDE_1_8_interface.addPort("C", 1, 1);
				FDE_1_8_interface.addPort("D", 1, 1);
				FDE_1_8_interface.addPort("CE", 1, 1);
				FDE_1_8_interface.addPort("Q", 1, 2);
				//FDE_1_9
				EdifPort FDE_1_9_interface_C = new EdifPort(FDE_1_9_interface, "C", 1, 1);
				EdifPort FDE_1_9_interface_D = new EdifPort(FDE_1_9_interface, "D", 1, 1);
				EdifPort FDE_1_9_interface_CE = new EdifPort(FDE_1_9_interface, "CE", 1, 1);
				EdifPort FDE_1_9_interface_Q = new EdifPort(FDE_1_9_interface, "Q", 1, 2);
				FDE_1_9_interface.addPort("C", 1, 1);
				FDE_1_9_interface.addPort("D", 1, 1);
				FDE_1_9_interface.addPort("CE", 1, 1);
				FDE_1_9_interface.addPort("Q", 1, 2);
				//FDE_1_10
				EdifPort FDE_1_10_interface_C = new EdifPort(FDE_1_10_interface, "C", 1, 1);
				EdifPort FDE_1_10_interface_D = new EdifPort(FDE_1_10_interface, "D", 1, 1);
				EdifPort FDE_1_10_interface_CE = new EdifPort(FDE_1_10_interface, "CE", 1, 1);
				EdifPort FDE_1_10_interface_Q = new EdifPort(FDE_1_10_interface, "Q", 1, 2);
				FDE_1_10_interface.addPort("C", 1, 1);
				FDE_1_10_interface.addPort("D", 1, 1);
				FDE_1_10_interface.addPort("CE", 1, 1);
				FDE_1_10_interface.addPort("Q", 1, 2);
				//FDE_1_11
				EdifPort FDE_1_11_interface_C = new EdifPort(FDE_1_11_interface, "C", 1, 1);
				EdifPort FDE_1_11_interface_D = new EdifPort(FDE_1_11_interface, "D", 1, 1);
				EdifPort FDE_1_11_interface_CE = new EdifPort(FDE_1_11_interface, "CE", 1, 1);
				EdifPort FDE_1_11_interface_Q = new EdifPort(FDE_1_11_interface, "Q", 1, 2);
				FDE_1_11_interface.addPort("C", 1, 1);
				FDE_1_11_interface.addPort("D", 1, 1);
				FDE_1_11_interface.addPort("CE", 1, 1);
				FDE_1_11_interface.addPort("Q", 1, 2);
				//FDE_1_12
				EdifPort FDE_1_12_interface_C = new EdifPort(FDE_1_12_interface, "C", 1, 1);
				EdifPort FDE_1_12_interface_D = new EdifPort(FDE_1_12_interface, "D", 1, 1);
				EdifPort FDE_1_12_interface_CE = new EdifPort(FDE_1_13_interface, "CE", 1, 1);
				EdifPort FDE_1_12_interface_Q = new EdifPort(FDE_1_12_interface, "Q", 1, 2);
				FDE_1_12_interface.addPort("C", 1, 1);
				FDE_1_12_interface.addPort("D", 1, 1);
				FDE_1_12_interface.addPort("CE", 1, 1);
				FDE_1_12_interface.addPort("Q", 1, 2);
				//FDE_1_13
				EdifPort FDE_1_13_interface_C = new EdifPort(FDE_1_13_interface, "C", 1, 1);
				EdifPort FDE_1_13_interface_D = new EdifPort(FDE_1_13_interface, "D", 1, 1);
				EdifPort FDE_1_13_interface_CE = new EdifPort(FDE_1_13_interface, "CE", 1, 1);
				EdifPort FDE_1_13_interface_Q = new EdifPort(FDE_1_13_interface, "Q", 1, 2);
				FDE_1_13_interface.addPort("C", 1, 1);
				FDE_1_13_interface.addPort("D", 1, 1);
				FDE_1_13_interface.addPort("CE", 1, 1);
				FDE_1_13_interface.addPort("Q", 1, 2);
				//FDE_1_14
				EdifPort FDE_1_14_interface_C = new EdifPort(FDE_1_14_interface, "C", 1, 1);
				EdifPort FDE_1_14_interface_D = new EdifPort(FDE_1_14_interface, "D", 1, 1);
				EdifPort FDE_1_14_interface_CE = new EdifPort(FDE_1_15_interface, "CE", 1, 1);
				EdifPort FDE_1_14_interface_Q = new EdifPort(FDE_1_14_interface, "Q", 1, 2);
				FDE_1_14_interface.addPort("C", 1, 1);
				FDE_1_14_interface.addPort("D", 1, 1);
				FDE_1_14_interface.addPort("CE", 1, 1);
				FDE_1_14_interface.addPort("Q", 1, 2);
				//FDE_1_15
				EdifPort FDE_1_15_interface_C = new EdifPort(FDE_1_15_interface, "C", 1, 1);
				EdifPort FDE_1_15_interface_D = new EdifPort(FDE_1_15_interface, "D", 1, 1);
				EdifPort FDE_1_15_interface_CE = new EdifPort(FDE_1_15_interface, "CE", 1, 1);
				EdifPort FDE_1_15_interface_Q = new EdifPort(FDE_1_15_interface, "Q", 1, 2);
				FDE_1_15_interface.addPort("C", 1, 1);
				FDE_1_15_interface.addPort("D", 1, 1);
				FDE_1_15_interface.addPort("CE", 1, 1);
				FDE_1_15_interface.addPort("Q", 1, 2);
				//FDE_1_16
				EdifPort FDE_1_16_interface_C = new EdifPort(FDE_1_16_interface, "C", 1, 1);
				EdifPort FDE_1_16_interface_D = new EdifPort(FDE_1_16_interface, "D", 1, 1);
				EdifPort FDE_1_16_interface_CE = new EdifPort(FDE_1_16_interface, "CE", 1, 1);
				EdifPort FDE_1_16_interface_Q = new EdifPort(FDE_1_16_interface, "Q", 1, 2);
				FDE_1_16_interface.addPort("C", 1, 1);
				FDE_1_16_interface.addPort("D", 1, 1);
				FDE_1_16_interface.addPort("CE", 1, 1);
				FDE_1_16_interface.addPort("Q", 1, 2);
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
				EdifPortRef RAM16X1S_1_replacement_WE = new EdifPortRef(we, RAM16X1S_1_replacement_interface_WE.getSingleBitPort(0), null);
				EdifPortRef RAM16X1S_1_replacement_D = new EdifPortRef(d, RAM16X1S_1_replacement_interface_D.getSingleBitPort(0), null);
				EdifPortRef RAM16X1S_1_replacement_WCLK = new EdifPortRef(wclk, RAM16X1S_1_replacement_interface_WCLK.getSingleBitPort(0), null);
				EdifPortRef RAM16X1S_1_replacement_A0 = new EdifPortRef(a0, RAM16X1S_1_replacement_interface_A0.getSingleBitPort(0), null);
				EdifPortRef RAM16X1S_1_replacement_A1 = new EdifPortRef(a1, RAM16X1S_1_replacement_interface_A1.getSingleBitPort(0), null);
				EdifPortRef RAM16X1S_1_replacement_A2 = new EdifPortRef(a2, RAM16X1S_1_replacement_interface_A2.getSingleBitPort(0), null);
				EdifPortRef RAM16X1S_1_replacement_A3 = new EdifPortRef(a3, RAM16X1S_1_replacement_interface_A3.getSingleBitPort(0), null);
				EdifPortRef RAM16X1S_1_replacement_O = new EdifPortRef(o, RAM16X1S_1_replacement_interface_O.getSingleBitPort(0), null);
				EdifPortRef WE_ibuf_I = new EdifPortRef(we, WE_ibuf_interface_I.getSingleBitPort(0), WE_ibuf);
				EdifPortRef WE_ibuf_O = new EdifPortRef(we_c, WE_ibuf_interface_O.getSingleBitPort(0), WE_ibuf);
				EdifPortRef D_ibuf_I = new EdifPortRef(d, D_ibuf_interface_I.getSingleBitPort(0), D_ibuf);
				EdifPortRef D_ibuf_O = new EdifPortRef(d_c, D_ibuf_interface_O.getSingleBitPort(0), D_ibuf);
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
				EdifPortRef O_obuf_I = new EdifPortRef(o_c, O_obuf_interface_I.getSingleBitPort(0), O_obuf);
				EdifPortRef O_obuf_O = new EdifPortRef(o, O_obuf_interface_O.getSingleBitPort(0), O_obuf);
				EdifPortRef FDE_1_1_C = new EdifPortRef(wclk_c, FDE_1_1_interface_C.getSingleBitPort(0), FDE_1_1);
				EdifPortRef FDE_1_1_D = new EdifPortRef(d_c, FDE_1_1_interface_D.getSingleBitPort(0), FDE_1_1);
				EdifPortRef FDE_1_1_CE = new EdifPortRef(e1, FDE_1_1_interface_CE.getSingleBitPort(0), FDE_1_1);
				EdifPortRef FDE_1_1_Q = new EdifPortRef(wire1, FDE_1_1_interface_Q.getSingleBitPort(0), FDE_1_1);
				EdifPortRef FDE_1_2_C = new EdifPortRef(wclk_c, FDE_1_2_interface_C.getSingleBitPort(0), FDE_1_2);
				EdifPortRef FDE_1_2_D = new EdifPortRef(d_c, FDE_1_2_interface_D.getSingleBitPort(0), FDE_1_2);
				EdifPortRef FDE_1_2_CE = new EdifPortRef(e2, FDE_1_2_interface_CE.getSingleBitPort(0), FDE_1_2);
				EdifPortRef FDE_1_2_Q = new EdifPortRef(wire2, FDE_1_2_interface_Q.getSingleBitPort(0), FDE_1_2);
				EdifPortRef FDE_1_3_C = new EdifPortRef(wclk_c, FDE_1_3_interface_C.getSingleBitPort(0), FDE_1_3);
				EdifPortRef FDE_1_3_D = new EdifPortRef(d_c, FDE_1_3_interface_D.getSingleBitPort(0), FDE_1_3);
				EdifPortRef FDE_1_3_CE = new EdifPortRef(e3, FDE_1_3_interface_CE.getSingleBitPort(0), FDE_1_3);
				EdifPortRef FDE_1_3_Q = new EdifPortRef(wire3, FDE_1_3_interface_Q.getSingleBitPort(0), FDE_1_3);
				EdifPortRef FDE_1_4_C = new EdifPortRef(wclk_c, FDE_1_4_interface_C.getSingleBitPort(0), FDE_1_4);
				EdifPortRef FDE_1_4_D = new EdifPortRef(d_c, FDE_1_4_interface_D.getSingleBitPort(0), FDE_1_4);
				EdifPortRef FDE_1_4_CE = new EdifPortRef(e4, FDE_1_4_interface_CE.getSingleBitPort(0), FDE_1_4);
				EdifPortRef FDE_1_4_Q = new EdifPortRef(wire4, FDE_1_4_interface_Q.getSingleBitPort(0), FDE_1_4);
				EdifPortRef FDE_1_5_C = new EdifPortRef(wclk_c, FDE_1_5_interface_C.getSingleBitPort(0), FDE_1_5);
				EdifPortRef FDE_1_5_D = new EdifPortRef(d_c, FDE_1_5_interface_D.getSingleBitPort(0), FDE_1_5);
				EdifPortRef FDE_1_5_CE = new EdifPortRef(e5, FDE_1_5_interface_CE.getSingleBitPort(0), FDE_1_5);
				EdifPortRef FDE_1_5_Q = new EdifPortRef(wire5, FDE_1_5_interface_Q.getSingleBitPort(0), FDE_1_5);
				EdifPortRef FDE_1_6_C = new EdifPortRef(wclk_c, FDE_1_6_interface_C.getSingleBitPort(0), FDE_1_6);
				EdifPortRef FDE_1_6_D = new EdifPortRef(d_c, FDE_1_6_interface_D.getSingleBitPort(0), FDE_1_6);
				EdifPortRef FDE_1_6_CE = new EdifPortRef(e6, FDE_1_6_interface_CE.getSingleBitPort(0), FDE_1_6);
				EdifPortRef FDE_1_6_Q = new EdifPortRef(wire6, FDE_1_6_interface_Q.getSingleBitPort(0), FDE_1_6);
				EdifPortRef FDE_1_7_C = new EdifPortRef(wclk_c, FDE_1_7_interface_C.getSingleBitPort(0), FDE_1_7);
				EdifPortRef FDE_1_7_D = new EdifPortRef(d_c, FDE_1_7_interface_D.getSingleBitPort(0), FDE_1_7);
				EdifPortRef FDE_1_7_CE = new EdifPortRef(e7, FDE_1_7_interface_CE.getSingleBitPort(0), FDE_1_7);
				EdifPortRef FDE_1_7_Q = new EdifPortRef(wire7, FDE_1_7_interface_Q.getSingleBitPort(0), FDE_1_7);
				EdifPortRef FDE_1_8_C = new EdifPortRef(wclk_c, FDE_1_8_interface_C.getSingleBitPort(0), FDE_1_8);
				EdifPortRef FDE_1_8_D = new EdifPortRef(d_c, FDE_1_8_interface_D.getSingleBitPort(0), FDE_1_8);
				EdifPortRef FDE_1_8_CE = new EdifPortRef(e8, FDE_1_8_interface_CE.getSingleBitPort(0), FDE_1_8);
				EdifPortRef FDE_1_8_Q = new EdifPortRef(wire8, FDE_1_8_interface_Q.getSingleBitPort(0), FDE_1_8);
				EdifPortRef FDE_1_9_C = new EdifPortRef(wclk_c, FDE_1_9_interface_C.getSingleBitPort(0), FDE_1_9);
				EdifPortRef FDE_1_9_D = new EdifPortRef(d_c, FDE_1_9_interface_D.getSingleBitPort(0), FDE_1_9);
				EdifPortRef FDE_1_9_CE = new EdifPortRef(e9, FDE_1_9_interface_CE.getSingleBitPort(0), FDE_1_9);
				EdifPortRef FDE_1_9_Q = new EdifPortRef(wire9, FDE_1_9_interface_Q.getSingleBitPort(0), FDE_1_9);
				EdifPortRef FDE_1_10_C = new EdifPortRef(wclk_c, FDE_1_10_interface_C.getSingleBitPort(0), FDE_1_10);
				EdifPortRef FDE_1_10_D = new EdifPortRef(d_c, FDE_1_10_interface_D.getSingleBitPort(0), FDE_1_10);
				EdifPortRef FDE_1_10_CE = new EdifPortRef(e10, FDE_1_10_interface_CE.getSingleBitPort(0), FDE_1_10);
				EdifPortRef FDE_1_10_Q = new EdifPortRef(wire10, FDE_1_10_interface_Q.getSingleBitPort(0), FDE_1_10);
				EdifPortRef FDE_1_11_C = new EdifPortRef(wclk_c, FDE_1_11_interface_C.getSingleBitPort(0), FDE_1_11);
				EdifPortRef FDE_1_11_D = new EdifPortRef(d_c, FDE_1_11_interface_D.getSingleBitPort(0), FDE_1_11);
				EdifPortRef FDE_1_11_CE = new EdifPortRef(e11, FDE_1_11_interface_CE.getSingleBitPort(0), FDE_1_11);
				EdifPortRef FDE_1_11_Q = new EdifPortRef(wire11, FDE_1_11_interface_Q.getSingleBitPort(0), FDE_1_11);
				EdifPortRef FDE_1_12_C = new EdifPortRef(wclk_c, FDE_1_12_interface_C.getSingleBitPort(0), FDE_1_12);
				EdifPortRef FDE_1_12_D = new EdifPortRef(d_c, FDE_1_12_interface_D.getSingleBitPort(0), FDE_1_12);
				EdifPortRef FDE_1_12_CE = new EdifPortRef(e12, FDE_1_12_interface_CE.getSingleBitPort(0), FDE_1_12);
				EdifPortRef FDE_1_12_Q = new EdifPortRef(wire12, FDE_1_12_interface_Q.getSingleBitPort(0), FDE_1_12);
				EdifPortRef FDE_1_13_C = new EdifPortRef(wclk_c, FDE_1_13_interface_C.getSingleBitPort(0), FDE_1_13);
				EdifPortRef FDE_1_13_D = new EdifPortRef(d_c, FDE_1_13_interface_D.getSingleBitPort(0), FDE_1_13);
				EdifPortRef FDE_1_13_CE = new EdifPortRef(e13, FDE_1_13_interface_CE.getSingleBitPort(0), FDE_1_13);
				EdifPortRef FDE_1_13_Q = new EdifPortRef(wire13, FDE_1_13_interface_Q.getSingleBitPort(0), FDE_1_13);
				EdifPortRef FDE_1_14_C = new EdifPortRef(wclk_c, FDE_1_14_interface_C.getSingleBitPort(0), FDE_1_14);
				EdifPortRef FDE_1_14_D = new EdifPortRef(d_c, FDE_1_14_interface_D.getSingleBitPort(0), FDE_1_14);
				EdifPortRef FDE_1_14_CE = new EdifPortRef(e14, FDE_1_14_interface_CE.getSingleBitPort(0), FDE_1_14);
				EdifPortRef FDE_1_14_Q = new EdifPortRef(wire14, FDE_1_14_interface_Q.getSingleBitPort(0), FDE_1_14);
				EdifPortRef FDE_1_15_C = new EdifPortRef(wclk_c, FDE_1_15_interface_C.getSingleBitPort(0), FDE_1_15);
				EdifPortRef FDE_1_15_D = new EdifPortRef(d_c, FDE_1_15_interface_D.getSingleBitPort(0), FDE_1_15);
				EdifPortRef FDE_1_15_CE = new EdifPortRef(e15, FDE_1_15_interface_CE.getSingleBitPort(0), FDE_1_15);
				EdifPortRef FDE_1_15_Q = new EdifPortRef(wire15, FDE_1_15_interface_Q.getSingleBitPort(0), FDE_1_15);
				EdifPortRef FDE_1_16_C = new EdifPortRef(wclk_c, FDE_1_16_interface_C.getSingleBitPort(0), FDE_1_16);
				EdifPortRef FDE_1_16_D = new EdifPortRef(d_c, FDE_1_16_interface_D.getSingleBitPort(0), FDE_1_16);
				EdifPortRef FDE_1_16_CE = new EdifPortRef(e16, FDE_1_16_interface_CE.getSingleBitPort(0), FDE_1_16);
				EdifPortRef FDE_1_16_Q = new EdifPortRef(wire16, FDE_1_16_interface_Q.getSingleBitPort(0), FDE_1_16);
				EdifPortRef LUT2_1_I0 = new EdifPortRef(a3_c, LUT2_1_interface_I0.getSingleBitPort(0), LUT2_1);
				EdifPortRef LUT2_1_I1 = new EdifPortRef(we_c, LUT2_1_interface_I1.getSingleBitPort(0), LUT2_1);
				EdifPortRef LUT2_1_O = new EdifPortRef(net15, LUT2_1_interface_O.getSingleBitPort(0), LUT2_1);
				EdifPortRef LUT2_2_I0 = new EdifPortRef(a3_c, LUT2_2_interface_I0.getSingleBitPort(0), LUT2_2);
				EdifPortRef LUT2_2_I1 = new EdifPortRef(we_c, LUT2_2_interface_I1.getSingleBitPort(0), LUT2_2);
				EdifPortRef LUT2_2_O = new EdifPortRef(net16, LUT2_2_interface_O.getSingleBitPort(0), LUT2_2);
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
				EdifPortRef LUT3_9_O = new EdifPortRef(o_c, LUT3_9_interface_O.getSingleBitPort(0), LUT3_9);
				EdifPortRef LUT4_1_I0 = new EdifPortRef(net15, LUT4_1_interface_I0.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_I1 = new EdifPortRef(a0_c, LUT4_1_interface_I1.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_I2 = new EdifPortRef(a1_c, LUT4_1_interface_I2.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_I3 = new EdifPortRef(a2_c, LUT4_1_interface_I3.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_O = new EdifPortRef(e1, LUT4_1_interface_O.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_2_I0 = new EdifPortRef(net15, LUT4_2_interface_I0.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_I1 = new EdifPortRef(a0_c, LUT4_2_interface_I1.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_I2 = new EdifPortRef(a1_c, LUT4_2_interface_I2.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_I3 = new EdifPortRef(a2_c, LUT4_2_interface_I3.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_O = new EdifPortRef(e2, LUT4_2_interface_O.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_3_I0 = new EdifPortRef(net15, LUT4_3_interface_I0.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_I1 = new EdifPortRef(a0_c, LUT4_3_interface_I1.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_I2 = new EdifPortRef(a1_c, LUT4_3_interface_I2.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_I3 = new EdifPortRef(a2_c, LUT4_3_interface_I3.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_O = new EdifPortRef(e3, LUT4_3_interface_O.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_4_I0 = new EdifPortRef(net15, LUT4_4_interface_I0.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_I1 = new EdifPortRef(a0_c, LUT4_4_interface_I1.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_I2 = new EdifPortRef(a1_c, LUT4_4_interface_I2.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_I3 = new EdifPortRef(a2_c, LUT4_4_interface_I3.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_O = new EdifPortRef(e4, LUT4_4_interface_O.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_5_I0 = new EdifPortRef(net15, LUT4_5_interface_I0.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_I1 = new EdifPortRef(a0_c, LUT4_5_interface_I1.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_I2 = new EdifPortRef(a1_c, LUT4_5_interface_I2.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_I3 = new EdifPortRef(a2_c, LUT4_5_interface_I3.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_O = new EdifPortRef(e5, LUT4_5_interface_O.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_6_I0 = new EdifPortRef(net15, LUT4_6_interface_I0.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_I1 = new EdifPortRef(a0_c, LUT4_6_interface_I1.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_I2 = new EdifPortRef(a1_c, LUT4_6_interface_I2.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_I3 = new EdifPortRef(a2_c, LUT4_6_interface_I3.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_O = new EdifPortRef(e6, LUT4_6_interface_O.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_7_I0 = new EdifPortRef(net15, LUT4_7_interface_I0.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_I1 = new EdifPortRef(a0_c, LUT4_7_interface_I1.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_I2 = new EdifPortRef(a1_c, LUT4_7_interface_I2.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_I3 = new EdifPortRef(a2_c, LUT4_7_interface_I3.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_O = new EdifPortRef(e7, LUT4_7_interface_O.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_8_I0 = new EdifPortRef(net15, LUT4_8_interface_I0.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_I1 = new EdifPortRef(a0_c, LUT4_8_interface_I1.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_I2 = new EdifPortRef(a1_c, LUT4_8_interface_I2.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_I3 = new EdifPortRef(a2_c, LUT4_8_interface_I3.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_O = new EdifPortRef(e8, LUT4_8_interface_O.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_9_I0 = new EdifPortRef(net16, LUT4_9_interface_I0.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_I1 = new EdifPortRef(a0_c, LUT4_9_interface_I1.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_I2 = new EdifPortRef(a1_c, LUT4_9_interface_I2.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_I3 = new EdifPortRef(a2_c, LUT4_9_interface_I3.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_O = new EdifPortRef(e9, LUT4_9_interface_O.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_10_I0 = new EdifPortRef(net16, LUT4_10_interface_I0.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_I1 = new EdifPortRef(a0_c, LUT4_10_interface_I1.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_I2 = new EdifPortRef(a1_c, LUT4_10_interface_I2.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_I3 = new EdifPortRef(a2_c, LUT4_10_interface_I3.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_O = new EdifPortRef(e10, LUT4_10_interface_O.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_11_I0 = new EdifPortRef(net16, LUT4_11_interface_I0.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_I1 = new EdifPortRef(a0_c, LUT4_11_interface_I1.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_I2 = new EdifPortRef(a1_c, LUT4_11_interface_I2.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_I3 = new EdifPortRef(a2_c, LUT4_11_interface_I3.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_O = new EdifPortRef(e11, LUT4_11_interface_O.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_12_I0 = new EdifPortRef(net16, LUT4_12_interface_I0.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_I1 = new EdifPortRef(a0_c, LUT4_12_interface_I1.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_I2 = new EdifPortRef(a1_c, LUT4_12_interface_I2.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_I3 = new EdifPortRef(a2_c, LUT4_12_interface_I3.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_O = new EdifPortRef(e12, LUT4_12_interface_O.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_13_I0 = new EdifPortRef(net16, LUT4_13_interface_I0.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_I1 = new EdifPortRef(a0_c, LUT4_13_interface_I1.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_I2 = new EdifPortRef(a1_c, LUT4_13_interface_I2.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_I3 = new EdifPortRef(a2_c, LUT4_13_interface_I3.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_O = new EdifPortRef(e13, LUT4_13_interface_O.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_14_I0 = new EdifPortRef(net16, LUT4_14_interface_I0.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_I1 = new EdifPortRef(a0_c, LUT4_14_interface_I1.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_I2 = new EdifPortRef(a1_c, LUT4_14_interface_I2.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_I3 = new EdifPortRef(a2_c, LUT4_14_interface_I3.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_O = new EdifPortRef(e14, LUT4_14_interface_O.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_15_I0 = new EdifPortRef(net16, LUT4_15_interface_I0.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_I1 = new EdifPortRef(a0_c, LUT4_15_interface_I1.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_I2 = new EdifPortRef(a1_c, LUT4_15_interface_I2.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_I3 = new EdifPortRef(a2_c, LUT4_15_interface_I3.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_O = new EdifPortRef(e15, LUT4_15_interface_O.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_16_I0 = new EdifPortRef(net16, LUT4_16_interface_I0.getSingleBitPort(0), LUT4_16);
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
				EdifPortRef MUXF6_1_I0 = new EdifPortRef(net5, MUXF6_1_interface_I0.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_I1 = new EdifPortRef(net6, MUXF6_1_interface_I1.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_S = new EdifPortRef(a1_c, MUXF6_1_interface_S.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_O = new EdifPortRef(net13, MUXF6_1_interface_O.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_2_I0 = new EdifPortRef(net11, MUXF6_2_interface_I0.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_I1 = new EdifPortRef(net12, MUXF6_2_interface_I1.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_S = new EdifPortRef(a1_c, MUXF6_2_interface_S.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_O = new EdifPortRef(net14, MUXF6_2_interface_O.getSingleBitPort(0), MUXF6_2);
				//we
				we.addPortConnection(RAM16X1S_1_replacement_WE);
				we.addPortConnection(WE_ibuf_I);
				//we_c
				we_c.addPortConnection(WE_ibuf_O);
				we_c.addPortConnection(LUT2_1_I1);
				we_c.addPortConnection(LUT2_2_I1);
				//d
				d.addPortConnection(RAM16X1S_1_replacement_D);
				d.addPortConnection(D_ibuf_I);
				//d_c
				d_c.addPortConnection(D_ibuf_O);
				d_c.addPortConnection(FDE_1_1_D);
				d_c.addPortConnection(FDE_1_2_D);
				d_c.addPortConnection(FDE_1_3_D);
				d_c.addPortConnection(FDE_1_4_D);
				d_c.addPortConnection(FDE_1_5_D);
				d_c.addPortConnection(FDE_1_6_D);
				d_c.addPortConnection(FDE_1_7_D);
				d_c.addPortConnection(FDE_1_8_D);
				d_c.addPortConnection(FDE_1_9_D);
				d_c.addPortConnection(FDE_1_10_D);
				d_c.addPortConnection(FDE_1_11_D);
				d_c.addPortConnection(FDE_1_12_D);
				d_c.addPortConnection(FDE_1_13_D);
				d_c.addPortConnection(FDE_1_14_D);
				d_c.addPortConnection(FDE_1_15_D);
				d_c.addPortConnection(FDE_1_16_D);
				//wclk
				wclk.addPortConnection(WCLK_ibuf_I);
				wclk.addPortConnection(RAM16X1S_1_replacement_WCLK);
				//wclk_c
				wclk_c.addPortConnection(WCLK_ibuf_O);
				wclk_c.addPortConnection(FDE_1_1_C);
				wclk_c.addPortConnection(FDE_1_2_C);
				wclk_c.addPortConnection(FDE_1_3_C);
				wclk_c.addPortConnection(FDE_1_4_C);
				wclk_c.addPortConnection(FDE_1_5_C);
				wclk_c.addPortConnection(FDE_1_6_C);
				wclk_c.addPortConnection(FDE_1_7_C);
				wclk_c.addPortConnection(FDE_1_8_C);
				wclk_c.addPortConnection(FDE_1_9_C);
				wclk_c.addPortConnection(FDE_1_10_C);
				wclk_c.addPortConnection(FDE_1_11_C);
				wclk_c.addPortConnection(FDE_1_12_C);
				wclk_c.addPortConnection(FDE_1_13_C);
				wclk_c.addPortConnection(FDE_1_14_C);
				wclk_c.addPortConnection(FDE_1_15_C);
				wclk_c.addPortConnection(FDE_1_16_C);
				//a0
				a0.addPortConnection(RAM16X1S_1_replacement_A0);
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
				//a1
				a1.addPortConnection(RAM16X1S_1_replacement_A1);
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
				//a2
				a2.addPortConnection(RAM16X1S_1_replacement_A2);
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
				//a3
				a3.addPortConnection(RAM16X1S_1_replacement_A3);
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
				//o
				o.addPortConnection(O_obuf_O);
				o.addPortConnection(RAM16X1S_1_replacement_O);
				//o_c
				o_c.addPortConnection(O_obuf_I);
				o_c.addPortConnection(LUT3_9_O);
				//wire1
				wire1.addPortConnection(LUT3_1_I1);
				wire1.addPortConnection(FDE_1_1_Q);
				//wire2
				wire2.addPortConnection(LUT3_5_I1);
				wire2.addPortConnection(FDE_1_2_Q);
				//wire3
				wire3.addPortConnection(LUT3_3_I1);
				wire3.addPortConnection(FDE_1_3_Q);
				//wire4
				wire4.addPortConnection(LUT3_7_I1);
				wire4.addPortConnection(FDE_1_4_Q);
				//wire5
				wire5.addPortConnection(LUT3_2_I1);
				wire5.addPortConnection(FDE_1_5_Q);
				//wire6
				wire6.addPortConnection(LUT3_6_I1);
				wire6.addPortConnection(FDE_1_6_Q);
				//wire7
				wire7.addPortConnection(LUT3_4_I1);
				wire7.addPortConnection(FDE_1_7_Q);
				//wire8
				wire8.addPortConnection(LUT3_8_I1);
				wire8.addPortConnection(FDE_1_8_Q);
				//wire9
				wire9.addPortConnection(LUT3_1_I2);
				wire9.addPortConnection(FDE_1_9_Q);
				//wire10
				wire10.addPortConnection(LUT3_5_I2);
				wire10.addPortConnection(FDE_1_10_Q);
				//wire11
				wire11.addPortConnection(LUT3_3_I2);
				wire11.addPortConnection(FDE_1_11_Q);
				//wire12
				wire12.addPortConnection(LUT3_7_I2);
				wire12.addPortConnection(FDE_1_12_Q);
				//wire13
				wire13.addPortConnection(LUT3_2_I2);
				wire13.addPortConnection(FDE_1_13_Q);
				//wire14
				wire14.addPortConnection(LUT3_6_I2);
				wire14.addPortConnection(FDE_1_14_Q);
				//wire15
				wire15.addPortConnection(LUT3_4_I2);
				wire15.addPortConnection(FDE_1_15_Q);
				//wire16
				wire16.addPortConnection(LUT3_8_I2);
				wire16.addPortConnection(FDE_1_16_Q);
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
				net15.addPortConnection(LUT2_1_O);
				net15.addPortConnection(LUT4_1_I0);
				net15.addPortConnection(LUT4_2_I0);
				net15.addPortConnection(LUT4_3_I0);
				net15.addPortConnection(LUT4_4_I0);
				net15.addPortConnection(LUT4_5_I0);
				net15.addPortConnection(LUT4_6_I0);
				net15.addPortConnection(LUT4_7_I0);
				net15.addPortConnection(LUT4_8_I0);
				//net16
				net16.addPortConnection(LUT2_2_O);
				net16.addPortConnection(LUT4_9_I0);
				net16.addPortConnection(LUT4_10_I0);
				net16.addPortConnection(LUT4_11_I0);
				net16.addPortConnection(LUT4_12_I0);
				net16.addPortConnection(LUT4_13_I0);
				net16.addPortConnection(LUT4_14_I0);
				net16.addPortConnection(LUT4_15_I0);
				net16.addPortConnection(LUT4_16_I0);
				//e1
				e1.addPortConnection(LUT4_1_O);
				e1.addPortConnection(FDE_1_1_CE);
				//e2
				e2.addPortConnection(LUT4_2_O);
				e2.addPortConnection(FDE_1_2_CE);
				//e3
				e3.addPortConnection(LUT4_3_O);
				e3.addPortConnection(FDE_1_3_CE);
				//e4
				e4.addPortConnection(LUT4_4_O);
				e4.addPortConnection(FDE_1_4_CE);
				//e5
				e5.addPortConnection(LUT4_5_O);
				e5.addPortConnection(FDE_1_5_CE);
				//e6
				e6.addPortConnection(LUT4_6_O);
				e6.addPortConnection(FDE_1_6_CE);
				//e7
				e7.addPortConnection(LUT4_7_O);
				e7.addPortConnection(FDE_1_7_CE);
				//e8
				e8.addPortConnection(LUT4_8_O);
				e8.addPortConnection(FDE_1_8_CE);
				//e9
				e9.addPortConnection(LUT4_9_O);
				e9.addPortConnection(FDE_1_9_CE);
				//e10
				e10.addPortConnection(LUT4_10_O);
				e10.addPortConnection(FDE_1_10_CE);
				//e11
				e11.addPortConnection(LUT4_11_O);
				e11.addPortConnection(FDE_1_11_CE);
				//e12
				e12.addPortConnection(LUT4_12_O);
				e12.addPortConnection(FDE_1_12_CE);
				//e13
				e13.addPortConnection(LUT4_13_O);
				e13.addPortConnection(FDE_1_13_CE);
				//e14
				e14.addPortConnection(LUT4_14_O);
				e14.addPortConnection(FDE_1_14_CE);
				//e15
				e15.addPortConnection(LUT4_15_O);
				e15.addPortConnection(FDE_1_15_CE);
				//e16
				e16.addPortConnection(LUT4_16_O);
				e16.addPortConnection(FDE_1_16_CE);
				
				/******Set INIT Property******/
				boolean isInit;
				int initCount = 0;
		        long initInt = 127;
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
		        //Set INIT Property for FDE_1_1
		        isInit = false;
				PropertyList FDE_1_1_propertylist = FDE_1_1.getPropertyList();
				if (FDE_1_1_propertylist != null) {
                    for (Property FDE_1_1_property : FDE_1_1_propertylist.values()) {
                        if (FDE_1_1_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_1_property.setValue(valueOne);
                            else
                                FDE_1_1_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_1.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_1.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_2
                isInit = false;
				PropertyList FDE_1_2_propertylist = FDE_1_2.getPropertyList();
				if (FDE_1_2_propertylist != null) {
                    for (Property FDE_1_2_property : FDE_1_2_propertylist.values()) {
                        if (FDE_1_2_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_2_property.setValue(valueOne);
                            else
                                FDE_1_2_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_2.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_2.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_3
                isInit = false;
				PropertyList FDE_1_3_propertylist = FDE_1_3.getPropertyList();
				if (FDE_1_3_propertylist != null) {
                    for (Property FDE_1_3_property : FDE_1_3_propertylist.values()) {
                        if (FDE_1_3_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_3_property.setValue(valueOne);
                            else
                                FDE_1_3_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_3.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_3.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_4
                isInit = false;
				PropertyList FDE_1_4_propertylist = FDE_1_4.getPropertyList();
				if (FDE_1_4_propertylist != null) {
                    for (Property FDE_1_4_property : FDE_1_4_propertylist.values()) {
                        if (FDE_1_4_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_4_property.setValue(valueOne);
                            else
                                FDE_1_4_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_4.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_4.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_5
                isInit = false;
				PropertyList FDE_1_5_propertylist = FDE_1_5.getPropertyList();
				if (FDE_1_5_propertylist != null) {
                    for (Property FDE_1_5_property : FDE_1_5_propertylist.values()) {
                        if (FDE_1_5_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_5_property.setValue(valueOne);
                            else
                                FDE_1_5_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_5.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_5.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_6
                isInit = false;
				PropertyList FDE_1_6_propertylist = FDE_1_6.getPropertyList();
				if (FDE_1_6_propertylist != null) {
                    for (Property FDE_1_6_property : FDE_1_6_propertylist.values()) {
                        if (FDE_1_6_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_6_property.setValue(valueOne);
                            else
                                FDE_1_6_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_6.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_6.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_7
                isInit = false;
				PropertyList FDE_1_7_propertylist = FDE_1_7.getPropertyList();
				if (FDE_1_7_propertylist != null) {
                    for (Property FDE_1_7_property : FDE_1_7_propertylist.values()) {
                        if (FDE_1_7_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_7_property.setValue(valueOne);
                            else
                                FDE_1_7_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_7.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_7.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_8
                isInit = false;
				PropertyList FDE_1_8_propertylist = FDE_1_8.getPropertyList();
				if (FDE_1_8_propertylist != null) {
                    for (Property FDE_1_8_property : FDE_1_8_propertylist.values()) {
                        if (FDE_1_8_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_8_property.setValue(valueOne);
                            else
                                FDE_1_8_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_8.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_8.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_9
                isInit = false;
				PropertyList FDE_1_9_propertylist = FDE_1_9.getPropertyList();
				if (FDE_1_9_propertylist != null) {
                    for (Property FDE_1_9_property : FDE_1_9_propertylist.values()) {
                        if (FDE_1_9_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_9_property.setValue(valueOne);
                            else
                                FDE_1_9_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_9.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_9.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_10
                isInit = false;
				PropertyList FDE_1_10_propertylist = FDE_1_10.getPropertyList();
				if (FDE_1_10_propertylist != null) {
                    for (Property FDE_1_10_property : FDE_1_10_propertylist.values()) {
                        if (FDE_1_10_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_10_property.setValue(valueOne);
                            else
                                FDE_1_10_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_10.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_10.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_11
                isInit = false;
				PropertyList FDE_1_11_propertylist = FDE_1_11.getPropertyList();
				if (FDE_1_11_propertylist != null) {
                    for (Property FDE_1_11_property : FDE_1_11_propertylist.values()) {
                        if (FDE_1_11_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_11_property.setValue(valueOne);
                            else
                                FDE_1_11_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_11.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_11.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_12
                isInit = false;
				PropertyList FDE_1_12_propertylist = FDE_1_12.getPropertyList();
				if (FDE_1_12_propertylist != null) {
                    for (Property FDE_1_12_property : FDE_1_12_propertylist.values()) {
                        if (FDE_1_12_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_12_property.setValue(valueOne);
                            else
                                FDE_1_12_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_12.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_12.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_13
                isInit = false;
				PropertyList FDE_1_13_propertylist = FDE_1_13.getPropertyList();
				if (FDE_1_13_propertylist != null) {
                    for (Property FDE_1_13_property : FDE_1_13_propertylist.values()) {
                        if (FDE_1_13_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_13_property.setValue(valueOne);
                            else
                                FDE_1_13_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_13.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_13.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_14
                isInit = false;
				PropertyList FDE_1_14_propertylist = FDE_1_14.getPropertyList();
				if (FDE_1_14_propertylist != null) {
                    for (Property FDE_1_14_property : FDE_1_14_propertylist.values()) {
                        if (FDE_1_14_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_14_property.setValue(valueOne);
                            else
                                FDE_1_14_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_14.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_14.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_15
                isInit = false;
				PropertyList FDE_1_15_propertylist = FDE_1_15.getPropertyList();
				if (FDE_1_15_propertylist != null) {
                    for (Property FDE_1_15_property : FDE_1_15_propertylist.values()) {
                        if (FDE_1_15_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_15_property.setValue(valueOne);
                            else
                                FDE_1_15_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_15.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_15.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_16
                isInit = false;
				PropertyList FDE_1_16_propertylist = FDE_1_16.getPropertyList();
				if (FDE_1_16_propertylist != null) {
                    for (Property FDE_1_16_property : FDE_1_16_propertylist.values()) {
                        if (FDE_1_16_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FDE_1_16_property.setValue(valueOne);
                            else
                                FDE_1_16_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FDE_1_16.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_16.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
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
	
	public static void Replace(EdifLibraryManager libManager, EdifCell RAM16X1S_1_replacement, long INIT,
			EdifNet we, EdifNet d, EdifNet wclk, EdifNet a0, EdifNet a1, EdifNet a2, EdifNet a3,
			EdifNet o) {
		try{
			try{
				/******Cells******/
				EdifCell FDE_1 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FDE_1");
				EdifCell LUT2 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "LUT2");
				EdifCell LUT3 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "LUT3");
				EdifCell LUT4 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "LUT4");
				EdifCell MUXF5 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "MUXF5");
				EdifCell MUXF6 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "MUXF6");		

				/******Nets******/
				EdifNet wire1 = new EdifNet("wire1", RAM16X1S_1_replacement);
				EdifNet wire2 = new EdifNet("wire2", RAM16X1S_1_replacement);
				EdifNet wire3 = new EdifNet("wire3", RAM16X1S_1_replacement);
				EdifNet wire4 = new EdifNet("wire4", RAM16X1S_1_replacement);
				EdifNet wire5 = new EdifNet("wire5", RAM16X1S_1_replacement);
				EdifNet wire6 = new EdifNet("wire6", RAM16X1S_1_replacement);
				EdifNet wire7 = new EdifNet("wire7", RAM16X1S_1_replacement);
				EdifNet wire8 = new EdifNet("wire8", RAM16X1S_1_replacement);
				EdifNet wire9 = new EdifNet("wire9", RAM16X1S_1_replacement);
				EdifNet wire10 = new EdifNet("wire10", RAM16X1S_1_replacement);
				EdifNet wire11 = new EdifNet("wire11", RAM16X1S_1_replacement);
				EdifNet wire12 = new EdifNet("wire12", RAM16X1S_1_replacement);
				EdifNet wire13 = new EdifNet("wire13", RAM16X1S_1_replacement);
				EdifNet wire14 = new EdifNet("wire14", RAM16X1S_1_replacement);
				EdifNet wire15 = new EdifNet("wire15", RAM16X1S_1_replacement);
				EdifNet wire16 = new EdifNet("wire16", RAM16X1S_1_replacement);
				EdifNet net1 = new EdifNet("net1", RAM16X1S_1_replacement);
				EdifNet net2 = new EdifNet("net2", RAM16X1S_1_replacement);
				EdifNet net3 = new EdifNet("net3", RAM16X1S_1_replacement);
				EdifNet net4 = new EdifNet("net4", RAM16X1S_1_replacement);
				EdifNet net5 = new EdifNet("net5", RAM16X1S_1_replacement);
				EdifNet net6 = new EdifNet("net6", RAM16X1S_1_replacement);
				EdifNet net7 = new EdifNet("net7", RAM16X1S_1_replacement);
				EdifNet net8 = new EdifNet("net8", RAM16X1S_1_replacement);
				EdifNet net9 = new EdifNet("net9", RAM16X1S_1_replacement);
				EdifNet net10 = new EdifNet("net10", RAM16X1S_1_replacement);
				EdifNet net11 = new EdifNet("net11", RAM16X1S_1_replacement);
				EdifNet net12 = new EdifNet("net12", RAM16X1S_1_replacement);
				EdifNet net13 = new EdifNet("net13", RAM16X1S_1_replacement);
				EdifNet net14 = new EdifNet("net14", RAM16X1S_1_replacement);
				EdifNet net15 = new EdifNet("net15", RAM16X1S_1_replacement);
				EdifNet net16 = new EdifNet("net16", RAM16X1S_1_replacement);
				EdifNet e1 = new EdifNet("e1", RAM16X1S_1_replacement);
				EdifNet e2 = new EdifNet("e2", RAM16X1S_1_replacement);
				EdifNet e3 = new EdifNet("e3", RAM16X1S_1_replacement);
				EdifNet e4 = new EdifNet("e4", RAM16X1S_1_replacement);
				EdifNet e5 = new EdifNet("e5", RAM16X1S_1_replacement);
				EdifNet e6 = new EdifNet("e6", RAM16X1S_1_replacement);
				EdifNet e7 = new EdifNet("e7", RAM16X1S_1_replacement);
				EdifNet e8 = new EdifNet("e8", RAM16X1S_1_replacement);
				EdifNet e9 = new EdifNet("e9", RAM16X1S_1_replacement);
				EdifNet e10 = new EdifNet("e10", RAM16X1S_1_replacement);
				EdifNet e11 = new EdifNet("e11", RAM16X1S_1_replacement);
				EdifNet e12 = new EdifNet("e12", RAM16X1S_1_replacement);
				EdifNet e13 = new EdifNet("e13", RAM16X1S_1_replacement);
				EdifNet e14 = new EdifNet("e14", RAM16X1S_1_replacement);
				EdifNet e15 = new EdifNet("e15", RAM16X1S_1_replacement);
				EdifNet e16 = new EdifNet("e16", RAM16X1S_1_replacement);
				RAM16X1S_1_replacement.addNet(wire1);
				RAM16X1S_1_replacement.addNet(wire2);
				RAM16X1S_1_replacement.addNet(wire3);
				RAM16X1S_1_replacement.addNet(wire4);
				RAM16X1S_1_replacement.addNet(wire5);
				RAM16X1S_1_replacement.addNet(wire6);
				RAM16X1S_1_replacement.addNet(wire7);
				RAM16X1S_1_replacement.addNet(wire8);
				RAM16X1S_1_replacement.addNet(wire9);
				RAM16X1S_1_replacement.addNet(wire10);
				RAM16X1S_1_replacement.addNet(wire11);
				RAM16X1S_1_replacement.addNet(wire12);
				RAM16X1S_1_replacement.addNet(wire13);
				RAM16X1S_1_replacement.addNet(wire14);
				RAM16X1S_1_replacement.addNet(wire15);
				RAM16X1S_1_replacement.addNet(wire16);
				RAM16X1S_1_replacement.addNet(net1);
				RAM16X1S_1_replacement.addNet(net2);
				RAM16X1S_1_replacement.addNet(net3);
				RAM16X1S_1_replacement.addNet(net4);
				RAM16X1S_1_replacement.addNet(net5);
				RAM16X1S_1_replacement.addNet(net6);
				RAM16X1S_1_replacement.addNet(net7);
				RAM16X1S_1_replacement.addNet(net8);
				RAM16X1S_1_replacement.addNet(net9);
				RAM16X1S_1_replacement.addNet(net10);
				RAM16X1S_1_replacement.addNet(net11);
				RAM16X1S_1_replacement.addNet(net12);
				RAM16X1S_1_replacement.addNet(net13);
				RAM16X1S_1_replacement.addNet(net14);
				RAM16X1S_1_replacement.addNet(net15);
				RAM16X1S_1_replacement.addNet(net16);
				RAM16X1S_1_replacement.addNet(e1);
				RAM16X1S_1_replacement.addNet(e2);
				RAM16X1S_1_replacement.addNet(e3);
				RAM16X1S_1_replacement.addNet(e4);
				RAM16X1S_1_replacement.addNet(e5);
				RAM16X1S_1_replacement.addNet(e6);
				RAM16X1S_1_replacement.addNet(e7);
				RAM16X1S_1_replacement.addNet(e8);
				RAM16X1S_1_replacement.addNet(e9);
				RAM16X1S_1_replacement.addNet(e10);
				RAM16X1S_1_replacement.addNet(e11);
				RAM16X1S_1_replacement.addNet(e12);
				RAM16X1S_1_replacement.addNet(e13);
				RAM16X1S_1_replacement.addNet(e14);
				RAM16X1S_1_replacement.addNet(e15);
				RAM16X1S_1_replacement.addNet(e16);
				
				/******Instances******/
				EdifCellInstance FDE_1_1 = new EdifCellInstance("FDE_1_1", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_2 = new EdifCellInstance("FDE_1_2", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_3 = new EdifCellInstance("FDE_1_3", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_4 = new EdifCellInstance("FDE_1_4", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_5 = new EdifCellInstance("FDE_1_5", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_6 = new EdifCellInstance("FDE_1_6", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_7 = new EdifCellInstance("FDE_1_7", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_8 = new EdifCellInstance("FDE_1_8", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_9 = new EdifCellInstance("FDE_1_9", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_10 = new EdifCellInstance("FDE_1_10", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_11 = new EdifCellInstance("FDE_1_11", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_12 = new EdifCellInstance("FDE_1_12", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_13 = new EdifCellInstance("FDE_1_13", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_14 = new EdifCellInstance("FDE_1_14", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_15 = new EdifCellInstance("FDE_1_15", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance FDE_1_16 = new EdifCellInstance("FDE_1_16", RAM16X1S_1_replacement, FDE_1);
				EdifCellInstance LUT2_1 = new EdifCellInstance("LUT2_1", RAM16X1S_1_replacement, LUT2);
				EdifCellInstance LUT2_2 = new EdifCellInstance("LUT2_2", RAM16X1S_1_replacement, LUT2);
				EdifCellInstance LUT3_1 = new EdifCellInstance("LUT3_1", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_2 = new EdifCellInstance("LUT3_2", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_3 = new EdifCellInstance("LUT3_3", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_4 = new EdifCellInstance("LUT3_4", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_5 = new EdifCellInstance("LUT3_5", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_6 = new EdifCellInstance("LUT3_6", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_7 = new EdifCellInstance("LUT3_7", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_8 = new EdifCellInstance("LUT3_8", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT3_9 = new EdifCellInstance("LUT3_9", RAM16X1S_1_replacement, LUT3);
				EdifCellInstance LUT4_1 = new EdifCellInstance("LUT4_1", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_2 = new EdifCellInstance("LUT4_2", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_3 = new EdifCellInstance("LUT4_3", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_4 = new EdifCellInstance("LUT4_4", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_5 = new EdifCellInstance("LUT4_5", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_6 = new EdifCellInstance("LUT4_6", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_7 = new EdifCellInstance("LUT4_7", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_8 = new EdifCellInstance("LUT4_8", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_9 = new EdifCellInstance("LUT4_9", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_10 = new EdifCellInstance("LUT4_10", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_11 = new EdifCellInstance("LUT4_11", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_12 = new EdifCellInstance("LUT4_12", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_13 = new EdifCellInstance("LUT4_13", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_14 = new EdifCellInstance("LUT4_14", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_15 = new EdifCellInstance("LUT4_15", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance LUT4_16 = new EdifCellInstance("LUT4_16", RAM16X1S_1_replacement, LUT4);
				EdifCellInstance MUXF5_1 = new EdifCellInstance("MUXF5_1", RAM16X1S_1_replacement, MUXF5);
				EdifCellInstance MUXF5_2 = new EdifCellInstance("MUXF5_2", RAM16X1S_1_replacement, MUXF5);
				EdifCellInstance MUXF5_3 = new EdifCellInstance("MUXF5_3", RAM16X1S_1_replacement, MUXF5);
				EdifCellInstance MUXF5_4 = new EdifCellInstance("MUXF5_4", RAM16X1S_1_replacement, MUXF5);
				EdifCellInstance MUXF6_1 = new EdifCellInstance("MUXF6_1", RAM16X1S_1_replacement, MUXF6);
				EdifCellInstance MUXF6_2 = new EdifCellInstance("MUXF6_2", RAM16X1S_1_replacement, MUXF6);
				RAM16X1S_1_replacement.addSubCell(FDE_1_1);
				RAM16X1S_1_replacement.addSubCell(FDE_1_2);
				RAM16X1S_1_replacement.addSubCell(FDE_1_3);
				RAM16X1S_1_replacement.addSubCell(FDE_1_4);
				RAM16X1S_1_replacement.addSubCell(FDE_1_5);
				RAM16X1S_1_replacement.addSubCell(FDE_1_6);
				RAM16X1S_1_replacement.addSubCell(FDE_1_7);
				RAM16X1S_1_replacement.addSubCell(FDE_1_8);
				RAM16X1S_1_replacement.addSubCell(FDE_1_9);
				RAM16X1S_1_replacement.addSubCell(FDE_1_10);
				RAM16X1S_1_replacement.addSubCell(FDE_1_11);
				RAM16X1S_1_replacement.addSubCell(FDE_1_12);
				RAM16X1S_1_replacement.addSubCell(FDE_1_13);
				RAM16X1S_1_replacement.addSubCell(FDE_1_14);
				RAM16X1S_1_replacement.addSubCell(FDE_1_15);
				RAM16X1S_1_replacement.addSubCell(FDE_1_16);
				RAM16X1S_1_replacement.addSubCell(LUT2_1);
				RAM16X1S_1_replacement.addSubCell(LUT2_2);
				RAM16X1S_1_replacement.addSubCell(LUT3_1);
				RAM16X1S_1_replacement.addSubCell(LUT3_2);
				RAM16X1S_1_replacement.addSubCell(LUT3_3);
				RAM16X1S_1_replacement.addSubCell(LUT3_4);
				RAM16X1S_1_replacement.addSubCell(LUT3_5);
				RAM16X1S_1_replacement.addSubCell(LUT3_6);
				RAM16X1S_1_replacement.addSubCell(LUT3_7);
				RAM16X1S_1_replacement.addSubCell(LUT3_8);
				RAM16X1S_1_replacement.addSubCell(LUT3_9);
				RAM16X1S_1_replacement.addSubCell(LUT4_1);
				RAM16X1S_1_replacement.addSubCell(LUT4_2);
				RAM16X1S_1_replacement.addSubCell(LUT4_3);
				RAM16X1S_1_replacement.addSubCell(LUT4_4);
				RAM16X1S_1_replacement.addSubCell(LUT4_5);
				RAM16X1S_1_replacement.addSubCell(LUT4_6);
				RAM16X1S_1_replacement.addSubCell(LUT4_7);
				RAM16X1S_1_replacement.addSubCell(LUT4_8);
				RAM16X1S_1_replacement.addSubCell(LUT4_9);
				RAM16X1S_1_replacement.addSubCell(LUT4_10);
				RAM16X1S_1_replacement.addSubCell(LUT4_11);
				RAM16X1S_1_replacement.addSubCell(LUT4_12);
				RAM16X1S_1_replacement.addSubCell(LUT4_13);
				RAM16X1S_1_replacement.addSubCell(LUT4_14);
				RAM16X1S_1_replacement.addSubCell(LUT4_15);
				RAM16X1S_1_replacement.addSubCell(LUT4_16);
				RAM16X1S_1_replacement.addSubCell(MUXF5_1);
				RAM16X1S_1_replacement.addSubCell(MUXF5_2);
				RAM16X1S_1_replacement.addSubCell(MUXF5_3);
				RAM16X1S_1_replacement.addSubCell(MUXF5_4);
				RAM16X1S_1_replacement.addSubCell(MUXF6_1);
				RAM16X1S_1_replacement.addSubCell(MUXF6_2);
				
				/******Interface******/
				EdifCellInterface FDE_1_1_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_2_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_3_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_4_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_5_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_6_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_7_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_8_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_9_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_10_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_11_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_12_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_13_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_14_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_15_interface = new EdifCellInterface(FDE_1);
				EdifCellInterface FDE_1_16_interface = new EdifCellInterface(FDE_1);
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
				EdifCellInterface MUXF6_1_interface = new EdifCellInterface(MUXF6);
				EdifCellInterface MUXF6_2_interface = new EdifCellInterface(MUXF6);
				
				/******Ports******/
				//FDE_1_1
				EdifPort FDE_1_1_interface_C = new EdifPort(FDE_1_1_interface, "C", 1, 1);
				EdifPort FDE_1_1_interface_D = new EdifPort(FDE_1_1_interface, "D", 1, 1);
				EdifPort FDE_1_1_interface_CE = new EdifPort(FDE_1_1_interface, "CE", 1, 1);
				EdifPort FDE_1_1_interface_Q = new EdifPort(FDE_1_1_interface, "Q", 1, 2);
				FDE_1_1_interface.addPort("C", 1, 1);
				FDE_1_1_interface.addPort("D", 1, 1);
				FDE_1_1_interface.addPort("CE", 1, 1);
				FDE_1_1_interface.addPort("Q", 1, 2);
				//FDE_1_2
				EdifPort FDE_1_2_interface_C = new EdifPort(FDE_1_2_interface, "C", 1, 1);
				EdifPort FDE_1_2_interface_D = new EdifPort(FDE_1_2_interface, "D", 1, 1);
				EdifPort FDE_1_2_interface_CE = new EdifPort(FDE_1_2_interface, "CE", 1, 1);
				EdifPort FDE_1_2_interface_Q = new EdifPort(FDE_1_2_interface, "Q", 1, 2);
				FDE_1_2_interface.addPort("C", 1, 1);
				FDE_1_2_interface.addPort("D", 1, 1);
				FDE_1_2_interface.addPort("CE", 1, 1);
				FDE_1_2_interface.addPort("Q", 1, 2);
				//FDE_1_3
				EdifPort FDE_1_3_interface_C = new EdifPort(FDE_1_3_interface, "C", 1, 1);
				EdifPort FDE_1_3_interface_D = new EdifPort(FDE_1_3_interface, "D", 1, 1);
				EdifPort FDE_1_3_interface_CE = new EdifPort(FDE_1_3_interface, "CE", 1, 1);
				EdifPort FDE_1_3_interface_Q = new EdifPort(FDE_1_3_interface, "Q", 1, 2);
				FDE_1_3_interface.addPort("C", 1, 1);
				FDE_1_3_interface.addPort("D", 1, 1);
				FDE_1_3_interface.addPort("CE", 1, 1);
				FDE_1_3_interface.addPort("Q", 1, 2);
				//FDE_1_4
				EdifPort FDE_1_4_interface_C = new EdifPort(FDE_1_4_interface, "C", 1, 1);
				EdifPort FDE_1_4_interface_D = new EdifPort(FDE_1_4_interface, "D", 1, 1);
				EdifPort FDE_1_4_interface_CE = new EdifPort(FDE_1_4_interface, "CE", 1, 1);
				EdifPort FDE_1_4_interface_Q = new EdifPort(FDE_1_4_interface, "Q", 1, 2);
				FDE_1_4_interface.addPort("C", 1, 1);
				FDE_1_4_interface.addPort("D", 1, 1);
				FDE_1_4_interface.addPort("CE", 1, 1);
				FDE_1_4_interface.addPort("Q", 1, 2);
				//FDE_1_5
				EdifPort FDE_1_5_interface_C = new EdifPort(FDE_1_5_interface, "C", 1, 1);
				EdifPort FDE_1_5_interface_D = new EdifPort(FDE_1_5_interface, "D", 1, 1);
				EdifPort FDE_1_5_interface_CE = new EdifPort(FDE_1_5_interface, "CE", 1, 1);
				EdifPort FDE_1_5_interface_Q = new EdifPort(FDE_1_5_interface, "Q", 1, 2);
				FDE_1_5_interface.addPort("C", 1, 1);
				FDE_1_5_interface.addPort("D", 1, 1);
				FDE_1_5_interface.addPort("CE", 1, 1);
				FDE_1_5_interface.addPort("Q", 1, 2);
				//FDE_1_6
				EdifPort FDE_1_6_interface_C = new EdifPort(FDE_1_6_interface, "C", 1, 1);
				EdifPort FDE_1_6_interface_D = new EdifPort(FDE_1_6_interface, "D", 1, 1);
				EdifPort FDE_1_6_interface_CE = new EdifPort(FDE_1_6_interface, "CE", 1, 1);
				EdifPort FDE_1_6_interface_Q = new EdifPort(FDE_1_6_interface, "Q", 1, 2);
				FDE_1_6_interface.addPort("C", 1, 1);
				FDE_1_6_interface.addPort("D", 1, 1);
				FDE_1_6_interface.addPort("CE", 1, 1);
				FDE_1_6_interface.addPort("Q", 1, 2);
				//FDE_1_7
				EdifPort FDE_1_7_interface_C = new EdifPort(FDE_1_7_interface, "C", 1, 1);
				EdifPort FDE_1_7_interface_D = new EdifPort(FDE_1_7_interface, "D", 1, 1);
				EdifPort FDE_1_7_interface_CE = new EdifPort(FDE_1_7_interface, "CE", 1, 1);
				EdifPort FDE_1_7_interface_Q = new EdifPort(FDE_1_7_interface, "Q", 1, 2);
				FDE_1_7_interface.addPort("C", 1, 1);
				FDE_1_7_interface.addPort("D", 1, 1);
				FDE_1_7_interface.addPort("CE", 1, 1);
				FDE_1_7_interface.addPort("Q", 1, 2);
				//FDE_1_8
				EdifPort FDE_1_8_interface_C = new EdifPort(FDE_1_8_interface, "C", 1, 1);
				EdifPort FDE_1_8_interface_D = new EdifPort(FDE_1_8_interface, "D", 1, 1);
				EdifPort FDE_1_8_interface_CE = new EdifPort(FDE_1_8_interface, "CE", 1, 1);
				EdifPort FDE_1_8_interface_Q = new EdifPort(FDE_1_8_interface, "Q", 1, 2);
				FDE_1_8_interface.addPort("C", 1, 1);
				FDE_1_8_interface.addPort("D", 1, 1);
				FDE_1_8_interface.addPort("CE", 1, 1);
				FDE_1_8_interface.addPort("Q", 1, 2);
				//FDE_1_9
				EdifPort FDE_1_9_interface_C = new EdifPort(FDE_1_9_interface, "C", 1, 1);
				EdifPort FDE_1_9_interface_D = new EdifPort(FDE_1_9_interface, "D", 1, 1);
				EdifPort FDE_1_9_interface_CE = new EdifPort(FDE_1_9_interface, "CE", 1, 1);
				EdifPort FDE_1_9_interface_Q = new EdifPort(FDE_1_9_interface, "Q", 1, 2);
				FDE_1_9_interface.addPort("C", 1, 1);
				FDE_1_9_interface.addPort("D", 1, 1);
				FDE_1_9_interface.addPort("CE", 1, 1);
				FDE_1_9_interface.addPort("Q", 1, 2);
				//FDE_1_10
				EdifPort FDE_1_10_interface_C = new EdifPort(FDE_1_10_interface, "C", 1, 1);
				EdifPort FDE_1_10_interface_D = new EdifPort(FDE_1_10_interface, "D", 1, 1);
				EdifPort FDE_1_10_interface_CE = new EdifPort(FDE_1_10_interface, "CE", 1, 1);
				EdifPort FDE_1_10_interface_Q = new EdifPort(FDE_1_10_interface, "Q", 1, 2);
				FDE_1_10_interface.addPort("C", 1, 1);
				FDE_1_10_interface.addPort("D", 1, 1);
				FDE_1_10_interface.addPort("CE", 1, 1);
				FDE_1_10_interface.addPort("Q", 1, 2);
				//FDE_1_11
				EdifPort FDE_1_11_interface_C = new EdifPort(FDE_1_11_interface, "C", 1, 1);
				EdifPort FDE_1_11_interface_D = new EdifPort(FDE_1_11_interface, "D", 1, 1);
				EdifPort FDE_1_11_interface_CE = new EdifPort(FDE_1_11_interface, "CE", 1, 1);
				EdifPort FDE_1_11_interface_Q = new EdifPort(FDE_1_11_interface, "Q", 1, 2);
				FDE_1_11_interface.addPort("C", 1, 1);
				FDE_1_11_interface.addPort("D", 1, 1);
				FDE_1_11_interface.addPort("CE", 1, 1);
				FDE_1_11_interface.addPort("Q", 1, 2);
				//FDE_1_12
				EdifPort FDE_1_12_interface_C = new EdifPort(FDE_1_12_interface, "C", 1, 1);
				EdifPort FDE_1_12_interface_D = new EdifPort(FDE_1_12_interface, "D", 1, 1);
				EdifPort FDE_1_12_interface_CE = new EdifPort(FDE_1_13_interface, "CE", 1, 1);
				EdifPort FDE_1_12_interface_Q = new EdifPort(FDE_1_12_interface, "Q", 1, 2);
				FDE_1_12_interface.addPort("C", 1, 1);
				FDE_1_12_interface.addPort("D", 1, 1);
				FDE_1_12_interface.addPort("CE", 1, 1);
				FDE_1_12_interface.addPort("Q", 1, 2);
				//FDE_1_13
				EdifPort FDE_1_13_interface_C = new EdifPort(FDE_1_13_interface, "C", 1, 1);
				EdifPort FDE_1_13_interface_D = new EdifPort(FDE_1_13_interface, "D", 1, 1);
				EdifPort FDE_1_13_interface_CE = new EdifPort(FDE_1_13_interface, "CE", 1, 1);
				EdifPort FDE_1_13_interface_Q = new EdifPort(FDE_1_13_interface, "Q", 1, 2);
				FDE_1_13_interface.addPort("C", 1, 1);
				FDE_1_13_interface.addPort("D", 1, 1);
				FDE_1_13_interface.addPort("CE", 1, 1);
				FDE_1_13_interface.addPort("Q", 1, 2);
				//FDE_1_14
				EdifPort FDE_1_14_interface_C = new EdifPort(FDE_1_14_interface, "C", 1, 1);
				EdifPort FDE_1_14_interface_D = new EdifPort(FDE_1_14_interface, "D", 1, 1);
				EdifPort FDE_1_14_interface_CE = new EdifPort(FDE_1_15_interface, "CE", 1, 1);
				EdifPort FDE_1_14_interface_Q = new EdifPort(FDE_1_14_interface, "Q", 1, 2);
				FDE_1_14_interface.addPort("C", 1, 1);
				FDE_1_14_interface.addPort("D", 1, 1);
				FDE_1_14_interface.addPort("CE", 1, 1);
				FDE_1_14_interface.addPort("Q", 1, 2);
				//FDE_1_15
				EdifPort FDE_1_15_interface_C = new EdifPort(FDE_1_15_interface, "C", 1, 1);
				EdifPort FDE_1_15_interface_D = new EdifPort(FDE_1_15_interface, "D", 1, 1);
				EdifPort FDE_1_15_interface_CE = new EdifPort(FDE_1_15_interface, "CE", 1, 1);
				EdifPort FDE_1_15_interface_Q = new EdifPort(FDE_1_15_interface, "Q", 1, 2);
				FDE_1_15_interface.addPort("C", 1, 1);
				FDE_1_15_interface.addPort("D", 1, 1);
				FDE_1_15_interface.addPort("CE", 1, 1);
				FDE_1_15_interface.addPort("Q", 1, 2);
				//FDE_1_16
				EdifPort FDE_1_16_interface_C = new EdifPort(FDE_1_16_interface, "C", 1, 1);
				EdifPort FDE_1_16_interface_D = new EdifPort(FDE_1_16_interface, "D", 1, 1);
				EdifPort FDE_1_16_interface_CE = new EdifPort(FDE_1_16_interface, "CE", 1, 1);
				EdifPort FDE_1_16_interface_Q = new EdifPort(FDE_1_16_interface, "Q", 1, 2);
				FDE_1_16_interface.addPort("C", 1, 1);
				FDE_1_16_interface.addPort("D", 1, 1);
				FDE_1_16_interface.addPort("CE", 1, 1);
				FDE_1_16_interface.addPort("Q", 1, 2);
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
				EdifPortRef FDE_1_1_C = new EdifPortRef(wclk, FDE_1_1_interface_C.getSingleBitPort(0), FDE_1_1);
				EdifPortRef FDE_1_1_D = new EdifPortRef(d, FDE_1_1_interface_D.getSingleBitPort(0), FDE_1_1);
				EdifPortRef FDE_1_1_CE = new EdifPortRef(e1, FDE_1_1_interface_CE.getSingleBitPort(0), FDE_1_1);
				EdifPortRef FDE_1_1_Q = new EdifPortRef(wire1, FDE_1_1_interface_Q.getSingleBitPort(0), FDE_1_1);
				EdifPortRef FDE_1_2_C = new EdifPortRef(wclk, FDE_1_2_interface_C.getSingleBitPort(0), FDE_1_2);
				EdifPortRef FDE_1_2_D = new EdifPortRef(d, FDE_1_2_interface_D.getSingleBitPort(0), FDE_1_2);
				EdifPortRef FDE_1_2_CE = new EdifPortRef(e2, FDE_1_2_interface_CE.getSingleBitPort(0), FDE_1_2);
				EdifPortRef FDE_1_2_Q = new EdifPortRef(wire2, FDE_1_2_interface_Q.getSingleBitPort(0), FDE_1_2);
				EdifPortRef FDE_1_3_C = new EdifPortRef(wclk, FDE_1_3_interface_C.getSingleBitPort(0), FDE_1_3);
				EdifPortRef FDE_1_3_D = new EdifPortRef(d, FDE_1_3_interface_D.getSingleBitPort(0), FDE_1_3);
				EdifPortRef FDE_1_3_CE = new EdifPortRef(e3, FDE_1_3_interface_CE.getSingleBitPort(0), FDE_1_3);
				EdifPortRef FDE_1_3_Q = new EdifPortRef(wire3, FDE_1_3_interface_Q.getSingleBitPort(0), FDE_1_3);
				EdifPortRef FDE_1_4_C = new EdifPortRef(wclk, FDE_1_4_interface_C.getSingleBitPort(0), FDE_1_4);
				EdifPortRef FDE_1_4_D = new EdifPortRef(d, FDE_1_4_interface_D.getSingleBitPort(0), FDE_1_4);
				EdifPortRef FDE_1_4_CE = new EdifPortRef(e4, FDE_1_4_interface_CE.getSingleBitPort(0), FDE_1_4);
				EdifPortRef FDE_1_4_Q = new EdifPortRef(wire4, FDE_1_4_interface_Q.getSingleBitPort(0), FDE_1_4);
				EdifPortRef FDE_1_5_C = new EdifPortRef(wclk, FDE_1_5_interface_C.getSingleBitPort(0), FDE_1_5);
				EdifPortRef FDE_1_5_D = new EdifPortRef(d, FDE_1_5_interface_D.getSingleBitPort(0), FDE_1_5);
				EdifPortRef FDE_1_5_CE = new EdifPortRef(e5, FDE_1_5_interface_CE.getSingleBitPort(0), FDE_1_5);
				EdifPortRef FDE_1_5_Q = new EdifPortRef(wire5, FDE_1_5_interface_Q.getSingleBitPort(0), FDE_1_5);
				EdifPortRef FDE_1_6_C = new EdifPortRef(wclk, FDE_1_6_interface_C.getSingleBitPort(0), FDE_1_6);
				EdifPortRef FDE_1_6_D = new EdifPortRef(d, FDE_1_6_interface_D.getSingleBitPort(0), FDE_1_6);
				EdifPortRef FDE_1_6_CE = new EdifPortRef(e6, FDE_1_6_interface_CE.getSingleBitPort(0), FDE_1_6);
				EdifPortRef FDE_1_6_Q = new EdifPortRef(wire6, FDE_1_6_interface_Q.getSingleBitPort(0), FDE_1_6);
				EdifPortRef FDE_1_7_C = new EdifPortRef(wclk, FDE_1_7_interface_C.getSingleBitPort(0), FDE_1_7);
				EdifPortRef FDE_1_7_D = new EdifPortRef(d, FDE_1_7_interface_D.getSingleBitPort(0), FDE_1_7);
				EdifPortRef FDE_1_7_CE = new EdifPortRef(e7, FDE_1_7_interface_CE.getSingleBitPort(0), FDE_1_7);
				EdifPortRef FDE_1_7_Q = new EdifPortRef(wire7, FDE_1_7_interface_Q.getSingleBitPort(0), FDE_1_7);
				EdifPortRef FDE_1_8_C = new EdifPortRef(wclk, FDE_1_8_interface_C.getSingleBitPort(0), FDE_1_8);
				EdifPortRef FDE_1_8_D = new EdifPortRef(d, FDE_1_8_interface_D.getSingleBitPort(0), FDE_1_8);
				EdifPortRef FDE_1_8_CE = new EdifPortRef(e8, FDE_1_8_interface_CE.getSingleBitPort(0), FDE_1_8);
				EdifPortRef FDE_1_8_Q = new EdifPortRef(wire8, FDE_1_8_interface_Q.getSingleBitPort(0), FDE_1_8);
				EdifPortRef FDE_1_9_C = new EdifPortRef(wclk, FDE_1_9_interface_C.getSingleBitPort(0), FDE_1_9);
				EdifPortRef FDE_1_9_D = new EdifPortRef(d, FDE_1_9_interface_D.getSingleBitPort(0), FDE_1_9);
				EdifPortRef FDE_1_9_CE = new EdifPortRef(e9, FDE_1_9_interface_CE.getSingleBitPort(0), FDE_1_9);
				EdifPortRef FDE_1_9_Q = new EdifPortRef(wire9, FDE_1_9_interface_Q.getSingleBitPort(0), FDE_1_9);
				EdifPortRef FDE_1_10_C = new EdifPortRef(wclk, FDE_1_10_interface_C.getSingleBitPort(0), FDE_1_10);
				EdifPortRef FDE_1_10_D = new EdifPortRef(d, FDE_1_10_interface_D.getSingleBitPort(0), FDE_1_10);
				EdifPortRef FDE_1_10_CE = new EdifPortRef(e10, FDE_1_10_interface_CE.getSingleBitPort(0), FDE_1_10);
				EdifPortRef FDE_1_10_Q = new EdifPortRef(wire10, FDE_1_10_interface_Q.getSingleBitPort(0), FDE_1_10);
				EdifPortRef FDE_1_11_C = new EdifPortRef(wclk, FDE_1_11_interface_C.getSingleBitPort(0), FDE_1_11);
				EdifPortRef FDE_1_11_D = new EdifPortRef(d, FDE_1_11_interface_D.getSingleBitPort(0), FDE_1_11);
				EdifPortRef FDE_1_11_CE = new EdifPortRef(e11, FDE_1_11_interface_CE.getSingleBitPort(0), FDE_1_11);
				EdifPortRef FDE_1_11_Q = new EdifPortRef(wire11, FDE_1_11_interface_Q.getSingleBitPort(0), FDE_1_11);
				EdifPortRef FDE_1_12_C = new EdifPortRef(wclk, FDE_1_12_interface_C.getSingleBitPort(0), FDE_1_12);
				EdifPortRef FDE_1_12_D = new EdifPortRef(d, FDE_1_12_interface_D.getSingleBitPort(0), FDE_1_12);
				EdifPortRef FDE_1_12_CE = new EdifPortRef(e12, FDE_1_12_interface_CE.getSingleBitPort(0), FDE_1_12);
				EdifPortRef FDE_1_12_Q = new EdifPortRef(wire12, FDE_1_12_interface_Q.getSingleBitPort(0), FDE_1_12);
				EdifPortRef FDE_1_13_C = new EdifPortRef(wclk, FDE_1_13_interface_C.getSingleBitPort(0), FDE_1_13);
				EdifPortRef FDE_1_13_D = new EdifPortRef(d, FDE_1_13_interface_D.getSingleBitPort(0), FDE_1_13);
				EdifPortRef FDE_1_13_CE = new EdifPortRef(e13, FDE_1_13_interface_CE.getSingleBitPort(0), FDE_1_13);
				EdifPortRef FDE_1_13_Q = new EdifPortRef(wire13, FDE_1_13_interface_Q.getSingleBitPort(0), FDE_1_13);
				EdifPortRef FDE_1_14_C = new EdifPortRef(wclk, FDE_1_14_interface_C.getSingleBitPort(0), FDE_1_14);
				EdifPortRef FDE_1_14_D = new EdifPortRef(d, FDE_1_14_interface_D.getSingleBitPort(0), FDE_1_14);
				EdifPortRef FDE_1_14_CE = new EdifPortRef(e14, FDE_1_14_interface_CE.getSingleBitPort(0), FDE_1_14);
				EdifPortRef FDE_1_14_Q = new EdifPortRef(wire14, FDE_1_14_interface_Q.getSingleBitPort(0), FDE_1_14);
				EdifPortRef FDE_1_15_C = new EdifPortRef(wclk, FDE_1_15_interface_C.getSingleBitPort(0), FDE_1_15);
				EdifPortRef FDE_1_15_D = new EdifPortRef(d, FDE_1_15_interface_D.getSingleBitPort(0), FDE_1_15);
				EdifPortRef FDE_1_15_CE = new EdifPortRef(e15, FDE_1_15_interface_CE.getSingleBitPort(0), FDE_1_15);
				EdifPortRef FDE_1_15_Q = new EdifPortRef(wire15, FDE_1_15_interface_Q.getSingleBitPort(0), FDE_1_15);
				EdifPortRef FDE_1_16_C = new EdifPortRef(wclk, FDE_1_16_interface_C.getSingleBitPort(0), FDE_1_16);
				EdifPortRef FDE_1_16_D = new EdifPortRef(d, FDE_1_16_interface_D.getSingleBitPort(0), FDE_1_16);
				EdifPortRef FDE_1_16_CE = new EdifPortRef(e16, FDE_1_16_interface_CE.getSingleBitPort(0), FDE_1_16);
				EdifPortRef FDE_1_16_Q = new EdifPortRef(wire16, FDE_1_16_interface_Q.getSingleBitPort(0), FDE_1_16);
				EdifPortRef LUT2_1_I0 = new EdifPortRef(a3, LUT2_1_interface_I0.getSingleBitPort(0), LUT2_1);
				EdifPortRef LUT2_1_I1 = new EdifPortRef(we, LUT2_1_interface_I1.getSingleBitPort(0), LUT2_1);
				EdifPortRef LUT2_1_O = new EdifPortRef(net15, LUT2_1_interface_O.getSingleBitPort(0), LUT2_1);
				EdifPortRef LUT2_2_I0 = new EdifPortRef(a3, LUT2_2_interface_I0.getSingleBitPort(0), LUT2_2);
				EdifPortRef LUT2_2_I1 = new EdifPortRef(we, LUT2_2_interface_I1.getSingleBitPort(0), LUT2_2);
				EdifPortRef LUT2_2_O = new EdifPortRef(net16, LUT2_2_interface_O.getSingleBitPort(0), LUT2_2);
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
				EdifPortRef LUT3_9_O = new EdifPortRef(o, LUT3_9_interface_O.getSingleBitPort(0), LUT3_9);
				EdifPortRef LUT4_1_I0 = new EdifPortRef(net15, LUT4_1_interface_I0.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_I1 = new EdifPortRef(a0, LUT4_1_interface_I1.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_I2 = new EdifPortRef(a1, LUT4_1_interface_I2.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_I3 = new EdifPortRef(a2, LUT4_1_interface_I3.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_1_O = new EdifPortRef(e1, LUT4_1_interface_O.getSingleBitPort(0), LUT4_1);
				EdifPortRef LUT4_2_I0 = new EdifPortRef(net15, LUT4_2_interface_I0.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_I1 = new EdifPortRef(a0, LUT4_2_interface_I1.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_I2 = new EdifPortRef(a1, LUT4_2_interface_I2.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_I3 = new EdifPortRef(a2, LUT4_2_interface_I3.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_2_O = new EdifPortRef(e2, LUT4_2_interface_O.getSingleBitPort(0), LUT4_2);
				EdifPortRef LUT4_3_I0 = new EdifPortRef(net15, LUT4_3_interface_I0.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_I1 = new EdifPortRef(a0, LUT4_3_interface_I1.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_I2 = new EdifPortRef(a1, LUT4_3_interface_I2.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_I3 = new EdifPortRef(a2, LUT4_3_interface_I3.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_3_O = new EdifPortRef(e3, LUT4_3_interface_O.getSingleBitPort(0), LUT4_3);
				EdifPortRef LUT4_4_I0 = new EdifPortRef(net15, LUT4_4_interface_I0.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_I1 = new EdifPortRef(a0, LUT4_4_interface_I1.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_I2 = new EdifPortRef(a1, LUT4_4_interface_I2.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_I3 = new EdifPortRef(a2, LUT4_4_interface_I3.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_4_O = new EdifPortRef(e4, LUT4_4_interface_O.getSingleBitPort(0), LUT4_4);
				EdifPortRef LUT4_5_I0 = new EdifPortRef(net15, LUT4_5_interface_I0.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_I1 = new EdifPortRef(a0, LUT4_5_interface_I1.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_I2 = new EdifPortRef(a1, LUT4_5_interface_I2.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_I3 = new EdifPortRef(a2, LUT4_5_interface_I3.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_5_O = new EdifPortRef(e5, LUT4_5_interface_O.getSingleBitPort(0), LUT4_5);
				EdifPortRef LUT4_6_I0 = new EdifPortRef(net15, LUT4_6_interface_I0.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_I1 = new EdifPortRef(a0, LUT4_6_interface_I1.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_I2 = new EdifPortRef(a1, LUT4_6_interface_I2.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_I3 = new EdifPortRef(a2, LUT4_6_interface_I3.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_6_O = new EdifPortRef(e6, LUT4_6_interface_O.getSingleBitPort(0), LUT4_6);
				EdifPortRef LUT4_7_I0 = new EdifPortRef(net15, LUT4_7_interface_I0.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_I1 = new EdifPortRef(a0, LUT4_7_interface_I1.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_I2 = new EdifPortRef(a1, LUT4_7_interface_I2.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_I3 = new EdifPortRef(a2, LUT4_7_interface_I3.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_7_O = new EdifPortRef(e7, LUT4_7_interface_O.getSingleBitPort(0), LUT4_7);
				EdifPortRef LUT4_8_I0 = new EdifPortRef(net15, LUT4_8_interface_I0.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_I1 = new EdifPortRef(a0, LUT4_8_interface_I1.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_I2 = new EdifPortRef(a1, LUT4_8_interface_I2.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_I3 = new EdifPortRef(a2, LUT4_8_interface_I3.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_8_O = new EdifPortRef(e8, LUT4_8_interface_O.getSingleBitPort(0), LUT4_8);
				EdifPortRef LUT4_9_I0 = new EdifPortRef(net16, LUT4_9_interface_I0.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_I1 = new EdifPortRef(a0, LUT4_9_interface_I1.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_I2 = new EdifPortRef(a1, LUT4_9_interface_I2.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_I3 = new EdifPortRef(a2, LUT4_9_interface_I3.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_9_O = new EdifPortRef(e9, LUT4_9_interface_O.getSingleBitPort(0), LUT4_9);
				EdifPortRef LUT4_10_I0 = new EdifPortRef(net16, LUT4_10_interface_I0.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_I1 = new EdifPortRef(a0, LUT4_10_interface_I1.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_I2 = new EdifPortRef(a1, LUT4_10_interface_I2.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_I3 = new EdifPortRef(a2, LUT4_10_interface_I3.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_10_O = new EdifPortRef(e10, LUT4_10_interface_O.getSingleBitPort(0), LUT4_10);
				EdifPortRef LUT4_11_I0 = new EdifPortRef(net16, LUT4_11_interface_I0.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_I1 = new EdifPortRef(a0, LUT4_11_interface_I1.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_I2 = new EdifPortRef(a1, LUT4_11_interface_I2.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_I3 = new EdifPortRef(a2, LUT4_11_interface_I3.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_11_O = new EdifPortRef(e11, LUT4_11_interface_O.getSingleBitPort(0), LUT4_11);
				EdifPortRef LUT4_12_I0 = new EdifPortRef(net16, LUT4_12_interface_I0.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_I1 = new EdifPortRef(a0, LUT4_12_interface_I1.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_I2 = new EdifPortRef(a1, LUT4_12_interface_I2.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_I3 = new EdifPortRef(a2, LUT4_12_interface_I3.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_12_O = new EdifPortRef(e12, LUT4_12_interface_O.getSingleBitPort(0), LUT4_12);
				EdifPortRef LUT4_13_I0 = new EdifPortRef(net16, LUT4_13_interface_I0.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_I1 = new EdifPortRef(a0, LUT4_13_interface_I1.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_I2 = new EdifPortRef(a1, LUT4_13_interface_I2.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_I3 = new EdifPortRef(a2, LUT4_13_interface_I3.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_13_O = new EdifPortRef(e13, LUT4_13_interface_O.getSingleBitPort(0), LUT4_13);
				EdifPortRef LUT4_14_I0 = new EdifPortRef(net16, LUT4_14_interface_I0.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_I1 = new EdifPortRef(a0, LUT4_14_interface_I1.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_I2 = new EdifPortRef(a1, LUT4_14_interface_I2.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_I3 = new EdifPortRef(a2, LUT4_14_interface_I3.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_14_O = new EdifPortRef(e14, LUT4_14_interface_O.getSingleBitPort(0), LUT4_14);
				EdifPortRef LUT4_15_I0 = new EdifPortRef(net16, LUT4_15_interface_I0.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_I1 = new EdifPortRef(a0, LUT4_15_interface_I1.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_I2 = new EdifPortRef(a1, LUT4_15_interface_I2.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_I3 = new EdifPortRef(a2, LUT4_15_interface_I3.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_15_O = new EdifPortRef(e15, LUT4_15_interface_O.getSingleBitPort(0), LUT4_15);
				EdifPortRef LUT4_16_I0 = new EdifPortRef(net16, LUT4_16_interface_I0.getSingleBitPort(0), LUT4_16);
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
				EdifPortRef MUXF6_1_I0 = new EdifPortRef(net5, MUXF6_1_interface_I0.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_I1 = new EdifPortRef(net6, MUXF6_1_interface_I1.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_S = new EdifPortRef(a1, MUXF6_1_interface_S.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_1_O = new EdifPortRef(net13, MUXF6_1_interface_O.getSingleBitPort(0), MUXF6_1);
				EdifPortRef MUXF6_2_I0 = new EdifPortRef(net11, MUXF6_2_interface_I0.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_I1 = new EdifPortRef(net12, MUXF6_2_interface_I1.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_S = new EdifPortRef(a1, MUXF6_2_interface_S.getSingleBitPort(0), MUXF6_2);
				EdifPortRef MUXF6_2_O = new EdifPortRef(net14, MUXF6_2_interface_O.getSingleBitPort(0), MUXF6_2);
				//we
				we.addPortConnection(LUT2_1_I1);
				we.addPortConnection(LUT2_2_I1);
				//d
				d.addPortConnection(FDE_1_1_D);
				d.addPortConnection(FDE_1_2_D);
				d.addPortConnection(FDE_1_3_D);
				d.addPortConnection(FDE_1_4_D);
				d.addPortConnection(FDE_1_5_D);
				d.addPortConnection(FDE_1_6_D);
				d.addPortConnection(FDE_1_7_D);
				d.addPortConnection(FDE_1_8_D);
				d.addPortConnection(FDE_1_9_D);
				d.addPortConnection(FDE_1_10_D);
				d.addPortConnection(FDE_1_11_D);
				d.addPortConnection(FDE_1_12_D);
				d.addPortConnection(FDE_1_13_D);
				d.addPortConnection(FDE_1_14_D);
				d.addPortConnection(FDE_1_15_D);
				d.addPortConnection(FDE_1_16_D);
				//wclk
				wclk.addPortConnection(FDE_1_1_C);
				wclk.addPortConnection(FDE_1_2_C);
				wclk.addPortConnection(FDE_1_3_C);
				wclk.addPortConnection(FDE_1_4_C);
				wclk.addPortConnection(FDE_1_5_C);
				wclk.addPortConnection(FDE_1_6_C);
				wclk.addPortConnection(FDE_1_7_C);
				wclk.addPortConnection(FDE_1_8_C);
				wclk.addPortConnection(FDE_1_9_C);
				wclk.addPortConnection(FDE_1_10_C);
				wclk.addPortConnection(FDE_1_11_C);
				wclk.addPortConnection(FDE_1_12_C);
				wclk.addPortConnection(FDE_1_13_C);
				wclk.addPortConnection(FDE_1_14_C);
				wclk.addPortConnection(FDE_1_15_C);
				wclk.addPortConnection(FDE_1_16_C);
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
				//o
				o.addPortConnection(LUT3_9_O);
				//wire1
				wire1.addPortConnection(LUT3_1_I1);
				wire1.addPortConnection(FDE_1_1_Q);
				//wire2
				wire2.addPortConnection(LUT3_5_I1);
				wire2.addPortConnection(FDE_1_2_Q);
				//wire3
				wire3.addPortConnection(LUT3_3_I1);
				wire3.addPortConnection(FDE_1_3_Q);
				//wire4
				wire4.addPortConnection(LUT3_7_I1);
				wire4.addPortConnection(FDE_1_4_Q);
				//wire5
				wire5.addPortConnection(LUT3_2_I1);
				wire5.addPortConnection(FDE_1_5_Q);
				//wire6
				wire6.addPortConnection(LUT3_6_I1);
				wire6.addPortConnection(FDE_1_6_Q);
				//wire7
				wire7.addPortConnection(LUT3_4_I1);
				wire7.addPortConnection(FDE_1_7_Q);
				//wire8
				wire8.addPortConnection(LUT3_8_I1);
				wire8.addPortConnection(FDE_1_8_Q);
				//wire9
				wire9.addPortConnection(LUT3_1_I2);
				wire9.addPortConnection(FDE_1_9_Q);
				//wire10
				wire10.addPortConnection(LUT3_5_I2);
				wire10.addPortConnection(FDE_1_10_Q);
				//wire11
				wire11.addPortConnection(LUT3_3_I2);
				wire11.addPortConnection(FDE_1_11_Q);
				//wire12
				wire12.addPortConnection(LUT3_7_I2);
				wire12.addPortConnection(FDE_1_12_Q);
				//wire13
				wire13.addPortConnection(LUT3_2_I2);
				wire13.addPortConnection(FDE_1_13_Q);
				//wire14
				wire14.addPortConnection(LUT3_6_I2);
				wire14.addPortConnection(FDE_1_14_Q);
				//wire15
				wire15.addPortConnection(LUT3_4_I2);
				wire15.addPortConnection(FDE_1_15_Q);
				//wire16
				wire16.addPortConnection(LUT3_8_I2);
				wire16.addPortConnection(FDE_1_16_Q);
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
				net15.addPortConnection(LUT2_1_O);
				net15.addPortConnection(LUT4_1_I0);
				net15.addPortConnection(LUT4_2_I0);
				net15.addPortConnection(LUT4_3_I0);
				net15.addPortConnection(LUT4_4_I0);
				net15.addPortConnection(LUT4_5_I0);
				net15.addPortConnection(LUT4_6_I0);
				net15.addPortConnection(LUT4_7_I0);
				net15.addPortConnection(LUT4_8_I0);
				//net16
				net16.addPortConnection(LUT2_2_O);
				net16.addPortConnection(LUT4_9_I0);
				net16.addPortConnection(LUT4_10_I0);
				net16.addPortConnection(LUT4_11_I0);
				net16.addPortConnection(LUT4_12_I0);
				net16.addPortConnection(LUT4_13_I0);
				net16.addPortConnection(LUT4_14_I0);
				net16.addPortConnection(LUT4_15_I0);
				net16.addPortConnection(LUT4_16_I0);
				//e1
				e1.addPortConnection(LUT4_1_O);
				e1.addPortConnection(FDE_1_1_CE);
				//e2
				e2.addPortConnection(LUT4_2_O);
				e2.addPortConnection(FDE_1_2_CE);
				//e3
				e3.addPortConnection(LUT4_3_O);
				e3.addPortConnection(FDE_1_3_CE);
				//e4
				e4.addPortConnection(LUT4_4_O);
				e4.addPortConnection(FDE_1_4_CE);
				//e5
				e5.addPortConnection(LUT4_5_O);
				e5.addPortConnection(FDE_1_5_CE);
				//e6
				e6.addPortConnection(LUT4_6_O);
				e6.addPortConnection(FDE_1_6_CE);
				//e7
				e7.addPortConnection(LUT4_7_O);
				e7.addPortConnection(FDE_1_7_CE);
				//e8
				e8.addPortConnection(LUT4_8_O);
				e8.addPortConnection(FDE_1_8_CE);
				//e9
				e9.addPortConnection(LUT4_9_O);
				e9.addPortConnection(FDE_1_9_CE);
				//e10
				e10.addPortConnection(LUT4_10_O);
				e10.addPortConnection(FDE_1_10_CE);
				//e11
				e11.addPortConnection(LUT4_11_O);
				e11.addPortConnection(FDE_1_11_CE);
				//e12
				e12.addPortConnection(LUT4_12_O);
				e12.addPortConnection(FDE_1_12_CE);
				//e13
				e13.addPortConnection(LUT4_13_O);
				e13.addPortConnection(FDE_1_13_CE);
				//e14
				e14.addPortConnection(LUT4_14_O);
				e14.addPortConnection(FDE_1_14_CE);
				//e15
				e15.addPortConnection(LUT4_15_O);
				e15.addPortConnection(FDE_1_15_CE);
				//e16
				e16.addPortConnection(LUT4_16_O);
				e16.addPortConnection(FDE_1_16_CE);
				
				/******Set INIT Property******/
				boolean isInit;
				int initCount = 0;
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
		        //Set INIT Property for FDE_1_1
		        isInit = false;
				PropertyList FDE_1_1_propertylist = FDE_1_1.getPropertyList();
				if (FDE_1_1_propertylist != null) {
                    for (Property FDE_1_1_property : FDE_1_1_propertylist.values()) {
                        if (FDE_1_1_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_1_property.setValue(valueOne);
                            else
                                FDE_1_1_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_1.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_1.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_2
                isInit = false;
				PropertyList FDE_1_2_propertylist = FDE_1_2.getPropertyList();
				if (FDE_1_2_propertylist != null) {
                    for (Property FDE_1_2_property : FDE_1_2_propertylist.values()) {
                        if (FDE_1_2_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_2_property.setValue(valueOne);
                            else
                                FDE_1_2_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_2.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_2.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_3
                isInit = false;
				PropertyList FDE_1_3_propertylist = FDE_1_3.getPropertyList();
				if (FDE_1_3_propertylist != null) {
                    for (Property FDE_1_3_property : FDE_1_3_propertylist.values()) {
                        if (FDE_1_3_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_3_property.setValue(valueOne);
                            else
                                FDE_1_3_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_3.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_3.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_4
                isInit = false;
				PropertyList FDE_1_4_propertylist = FDE_1_4.getPropertyList();
				if (FDE_1_4_propertylist != null) {
                    for (Property FDE_1_4_property : FDE_1_4_propertylist.values()) {
                        if (FDE_1_4_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_4_property.setValue(valueOne);
                            else
                                FDE_1_4_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_4.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_4.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_5
                isInit = false;
				PropertyList FDE_1_5_propertylist = FDE_1_5.getPropertyList();
				if (FDE_1_5_propertylist != null) {
                    for (Property FDE_1_5_property : FDE_1_5_propertylist.values()) {
                        if (FDE_1_5_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_5_property.setValue(valueOne);
                            else
                                FDE_1_5_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_5.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_5.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_6
                isInit = false;
				PropertyList FDE_1_6_propertylist = FDE_1_6.getPropertyList();
				if (FDE_1_6_propertylist != null) {
                    for (Property FDE_1_6_property : FDE_1_6_propertylist.values()) {
                        if (FDE_1_6_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_6_property.setValue(valueOne);
                            else
                                FDE_1_6_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_6.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_6.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_7
                isInit = false;
				PropertyList FDE_1_7_propertylist = FDE_1_7.getPropertyList();
				if (FDE_1_7_propertylist != null) {
                    for (Property FDE_1_7_property : FDE_1_7_propertylist.values()) {
                        if (FDE_1_7_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_7_property.setValue(valueOne);
                            else
                                FDE_1_7_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_7.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_7.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_8
                isInit = false;
				PropertyList FDE_1_8_propertylist = FDE_1_8.getPropertyList();
				if (FDE_1_8_propertylist != null) {
                    for (Property FDE_1_8_property : FDE_1_8_propertylist.values()) {
                        if (FDE_1_8_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_8_property.setValue(valueOne);
                            else
                                FDE_1_8_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_8.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_8.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_9
                isInit = false;
				PropertyList FDE_1_9_propertylist = FDE_1_9.getPropertyList();
				if (FDE_1_9_propertylist != null) {
                    for (Property FDE_1_9_property : FDE_1_9_propertylist.values()) {
                        if (FDE_1_9_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_9_property.setValue(valueOne);
                            else
                                FDE_1_9_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_9.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_9.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_10
                isInit = false;
				PropertyList FDE_1_10_propertylist = FDE_1_10.getPropertyList();
				if (FDE_1_10_propertylist != null) {
                    for (Property FDE_1_10_property : FDE_1_10_propertylist.values()) {
                        if (FDE_1_10_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_10_property.setValue(valueOne);
                            else
                                FDE_1_10_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_10.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_10.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_11
                isInit = false;
				PropertyList FDE_1_11_propertylist = FDE_1_11.getPropertyList();
				if (FDE_1_11_propertylist != null) {
                    for (Property FDE_1_11_property : FDE_1_11_propertylist.values()) {
                        if (FDE_1_11_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_11_property.setValue(valueOne);
                            else
                                FDE_1_11_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_11.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_11.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_12
                isInit = false;
				PropertyList FDE_1_12_propertylist = FDE_1_12.getPropertyList();
				if (FDE_1_12_propertylist != null) {
                    for (Property FDE_1_12_property : FDE_1_12_propertylist.values()) {
                        if (FDE_1_12_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_12_property.setValue(valueOne);
                            else
                                FDE_1_12_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_12.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_12.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_13
                isInit = false;
				PropertyList FDE_1_13_propertylist = FDE_1_13.getPropertyList();
				if (FDE_1_13_propertylist != null) {
                    for (Property FDE_1_13_property : FDE_1_13_propertylist.values()) {
                        if (FDE_1_13_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_13_property.setValue(valueOne);
                            else
                                FDE_1_13_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_13.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_13.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_14
                isInit = false;
				PropertyList FDE_1_14_propertylist = FDE_1_14.getPropertyList();
				if (FDE_1_14_propertylist != null) {
                    for (Property FDE_1_14_property : FDE_1_14_propertylist.values()) {
                        if (FDE_1_14_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_14_property.setValue(valueOne);
                            else
                                FDE_1_14_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_14.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_14.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_15
                isInit = false;
				PropertyList FDE_1_15_propertylist = FDE_1_15.getPropertyList();
				if (FDE_1_15_propertylist != null) {
                    for (Property FDE_1_15_property : FDE_1_15_propertylist.values()) {
                        if (FDE_1_15_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_15_property.setValue(valueOne);
                            else
                                FDE_1_15_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_15.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_15.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FDE_1_16
                isInit = false;
				PropertyList FDE_1_16_propertylist = FDE_1_16.getPropertyList();
				if (FDE_1_16_propertylist != null) {
                    for (Property FDE_1_16_property : FDE_1_16_propertylist.values()) {
                        if (FDE_1_16_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FDE_1_16_property.setValue(valueOne);
                            else
                                FDE_1_16_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FDE_1_16.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FDE_1_16.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
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