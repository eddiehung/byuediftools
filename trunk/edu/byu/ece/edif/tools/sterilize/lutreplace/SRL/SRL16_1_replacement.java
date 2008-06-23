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

public class SRL16_1_replacement{
	public static void main(String args[]){
		
		String outputFileName = "SRL16_1_replacement.edf";
		
		try{
			/******Environment and Environment manager******/
			EdifEnvironment topLevel = new EdifEnvironment("srl16_1_replacement");
			EdifLibraryManager manager = new EdifLibraryManager(topLevel);
			try{
				/******Libraries******/
				EdifLibrary work = new EdifLibrary(manager, "work");
				topLevel.addLibrary(work);
				
				/******Cells******/
				EdifCell BUFGP = XilinxLibrary.findOrAddXilinxPrimitive(manager, "BUFGP");
				EdifCell IBUF = XilinxLibrary.findOrAddXilinxPrimitive(manager, "IBUF");
				EdifCell OBUF = XilinxLibrary.findOrAddXilinxPrimitive(manager, "OBUF");
				EdifCell FD_1 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "FD_1");
				EdifCell LUT3 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "LUT3");
				EdifCell MUXF5 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "MUXF5");
				EdifCell MUXF6 = XilinxLibrary.findOrAddXilinxPrimitive(manager, "MUXF6");		
				EdifCell srl16_1_replacement = new EdifCell(work, "srl16_1_replacement");
				srl16_1_replacement.addPort("clk", 1, 1);
				srl16_1_replacement.addPort("d", 1, 1);
				srl16_1_replacement.addPort("a0", 1, 1);
				srl16_1_replacement.addPort("a1", 1, 1);
				srl16_1_replacement.addPort("a2", 1, 1);
				srl16_1_replacement.addPort("a3", 1, 1);
				srl16_1_replacement.addPort("q", 1, 2);
				
				/******Design******/
				EdifDesign design = new EdifDesign("srl16_1_replacement");
				topLevel.setTopDesign(design);
				topLevel.setTopCell(srl16_1_replacement);
				
				/******Nets******/
				EdifNet clk_c = new EdifNet("clk_c", srl16_1_replacement);
				EdifNet clk = new EdifNet("clk", srl16_1_replacement);
				EdifNet d_c = new EdifNet("d_c", srl16_1_replacement);
				EdifNet d = new EdifNet("d", srl16_1_replacement);
				EdifNet a0_c = new EdifNet("a0_c", srl16_1_replacement);
				EdifNet a0 = new EdifNet("a0", srl16_1_replacement);
				EdifNet a1_c = new EdifNet("a1_c", srl16_1_replacement);
				EdifNet a1 = new EdifNet("a1", srl16_1_replacement);
				EdifNet a2_c = new EdifNet("a2_c", srl16_1_replacement);
				EdifNet a2 = new EdifNet("a2", srl16_1_replacement);
				EdifNet a3_c = new EdifNet("a3_c", srl16_1_replacement);
				EdifNet a3 = new EdifNet("a3", srl16_1_replacement);
				EdifNet q_c = new EdifNet("q_c", srl16_1_replacement);
				EdifNet q = new EdifNet("q", srl16_1_replacement);
				EdifNet wire1 = new EdifNet("wire1", srl16_1_replacement);
				EdifNet wire2 = new EdifNet("wire2", srl16_1_replacement);
				EdifNet wire3 = new EdifNet("wire3", srl16_1_replacement);
				EdifNet wire4 = new EdifNet("wire4", srl16_1_replacement);
				EdifNet wire5 = new EdifNet("wire5", srl16_1_replacement);
				EdifNet wire6 = new EdifNet("wire6", srl16_1_replacement);
				EdifNet wire7 = new EdifNet("wire7", srl16_1_replacement);
				EdifNet wire8 = new EdifNet("wire8", srl16_1_replacement);
				EdifNet wire9 = new EdifNet("wire9", srl16_1_replacement);
				EdifNet wire10 = new EdifNet("wire10", srl16_1_replacement);
				EdifNet wire11 = new EdifNet("wire11", srl16_1_replacement);
				EdifNet wire12 = new EdifNet("wire12", srl16_1_replacement);
				EdifNet wire13 = new EdifNet("wire13", srl16_1_replacement);
				EdifNet wire14 = new EdifNet("wire14", srl16_1_replacement);
				EdifNet wire15 = new EdifNet("wire15", srl16_1_replacement);
				EdifNet wire16 = new EdifNet("wire16", srl16_1_replacement);
				EdifNet net1 = new EdifNet("net1", srl16_1_replacement);
				EdifNet net2 = new EdifNet("net2", srl16_1_replacement);
				EdifNet net3 = new EdifNet("net3", srl16_1_replacement);
				EdifNet net4 = new EdifNet("net4", srl16_1_replacement);
				EdifNet net5 = new EdifNet("net5", srl16_1_replacement);
				EdifNet net6 = new EdifNet("net6", srl16_1_replacement);
				EdifNet net7 = new EdifNet("net7", srl16_1_replacement);
				EdifNet net8 = new EdifNet("net8", srl16_1_replacement);
				EdifNet net9 = new EdifNet("net9", srl16_1_replacement);
				EdifNet net10 = new EdifNet("net10", srl16_1_replacement);
				EdifNet net11 = new EdifNet("net11", srl16_1_replacement);
				EdifNet net12 = new EdifNet("net12", srl16_1_replacement);
				EdifNet net13 = new EdifNet("net13", srl16_1_replacement);
				EdifNet net14 = new EdifNet("net14", srl16_1_replacement);
				srl16_1_replacement.addNet(clk_c);
				srl16_1_replacement.addNet(clk);
				srl16_1_replacement.addNet(d_c);
				srl16_1_replacement.addNet(d);
				srl16_1_replacement.addNet(a0_c);
				srl16_1_replacement.addNet(a0);
				srl16_1_replacement.addNet(a1_c);
				srl16_1_replacement.addNet(a1);
				srl16_1_replacement.addNet(a2_c);
				srl16_1_replacement.addNet(a2);
				srl16_1_replacement.addNet(a3_c);
				srl16_1_replacement.addNet(a3);
				srl16_1_replacement.addNet(q_c);
				srl16_1_replacement.addNet(q);
				srl16_1_replacement.addNet(wire1);
				srl16_1_replacement.addNet(wire2);
				srl16_1_replacement.addNet(wire3);
				srl16_1_replacement.addNet(wire4);
				srl16_1_replacement.addNet(wire5);
				srl16_1_replacement.addNet(wire6);
				srl16_1_replacement.addNet(wire7);
				srl16_1_replacement.addNet(wire8);
				srl16_1_replacement.addNet(wire9);
				srl16_1_replacement.addNet(wire10);
				srl16_1_replacement.addNet(wire11);
				srl16_1_replacement.addNet(wire12);
				srl16_1_replacement.addNet(wire13);
				srl16_1_replacement.addNet(wire14);
				srl16_1_replacement.addNet(wire15);
				srl16_1_replacement.addNet(wire16);
				srl16_1_replacement.addNet(net1);
				srl16_1_replacement.addNet(net2);
				srl16_1_replacement.addNet(net3);
				srl16_1_replacement.addNet(net4);
				srl16_1_replacement.addNet(net5);
				srl16_1_replacement.addNet(net6);
				srl16_1_replacement.addNet(net7);
				srl16_1_replacement.addNet(net8);
				srl16_1_replacement.addNet(net9);
				srl16_1_replacement.addNet(net10);
				srl16_1_replacement.addNet(net11);
				srl16_1_replacement.addNet(net12);
				srl16_1_replacement.addNet(net13);
				srl16_1_replacement.addNet(net14);
				
				/******Instances******/
				EdifCellInstance clk_ibuf = new EdifCellInstance("clk_ibuf", srl16_1_replacement, BUFGP);
				EdifCellInstance q_obuf = new EdifCellInstance("q_obuf", srl16_1_replacement, OBUF);
				EdifCellInstance d_ibuf = new EdifCellInstance("d_ibuf", srl16_1_replacement, IBUF);
				EdifCellInstance a0_ibuf = new EdifCellInstance("a0_ibuf", srl16_1_replacement, IBUF);
				EdifCellInstance a1_ibuf = new EdifCellInstance("a1_ibuf", srl16_1_replacement, IBUF);
				EdifCellInstance a2_ibuf = new EdifCellInstance("a2_ibuf", srl16_1_replacement, IBUF);
				EdifCellInstance a3_ibuf = new EdifCellInstance("a3_ibuf", srl16_1_replacement, IBUF);
				EdifCellInstance FD_1_1 = new EdifCellInstance("FD_1_1", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_2 = new EdifCellInstance("FD_1_2", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_3 = new EdifCellInstance("FD_1_3", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_4 = new EdifCellInstance("FD_1_4", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_5 = new EdifCellInstance("FD_1_5", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_6 = new EdifCellInstance("FD_1_6", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_7 = new EdifCellInstance("FD_1_7", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_8 = new EdifCellInstance("FD_1_8", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_9 = new EdifCellInstance("FD_1_9", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_10 = new EdifCellInstance("FD_1_10", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_11 = new EdifCellInstance("FD_1_11", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_12 = new EdifCellInstance("FD_1_12", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_13 = new EdifCellInstance("FD_1_13", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_14 = new EdifCellInstance("FD_1_14", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_15 = new EdifCellInstance("FD_1_15", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_16 = new EdifCellInstance("FD_1_16", srl16_1_replacement, FD_1);
				EdifCellInstance LUT3_1 = new EdifCellInstance("LUT3_1", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_2 = new EdifCellInstance("LUT3_2", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_3 = new EdifCellInstance("LUT3_3", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_4 = new EdifCellInstance("LUT3_4", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_5 = new EdifCellInstance("LUT3_5", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_6 = new EdifCellInstance("LUT3_6", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_7 = new EdifCellInstance("LUT3_7", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_8 = new EdifCellInstance("LUT3_8", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_9 = new EdifCellInstance("LUT3_9", srl16_1_replacement, LUT3);
				EdifCellInstance MUXF5_1 = new EdifCellInstance("MUXF5_1", srl16_1_replacement, MUXF5);
				EdifCellInstance MUXF5_2 = new EdifCellInstance("MUXF5_2", srl16_1_replacement, MUXF5);
				EdifCellInstance MUXF5_3 = new EdifCellInstance("MUXF5_3", srl16_1_replacement, MUXF5);
				EdifCellInstance MUXF5_4 = new EdifCellInstance("MUXF5_4", srl16_1_replacement, MUXF5);
				EdifCellInstance MUXF6_1 = new EdifCellInstance("MUXF6_1", srl16_1_replacement, MUXF6);
				EdifCellInstance MUXF6_2 = new EdifCellInstance("MUXF6_2", srl16_1_replacement, MUXF6);
				srl16_1_replacement.addSubCell(clk_ibuf);
				srl16_1_replacement.addSubCell(q_obuf);
				srl16_1_replacement.addSubCell(d_ibuf);
				srl16_1_replacement.addSubCell(a0_ibuf);
				srl16_1_replacement.addSubCell(a1_ibuf);
				srl16_1_replacement.addSubCell(a2_ibuf);
				srl16_1_replacement.addSubCell(a3_ibuf);
				srl16_1_replacement.addSubCell(FD_1_1);
				srl16_1_replacement.addSubCell(FD_1_2);
				srl16_1_replacement.addSubCell(FD_1_3);
				srl16_1_replacement.addSubCell(FD_1_4);
				srl16_1_replacement.addSubCell(FD_1_5);
				srl16_1_replacement.addSubCell(FD_1_6);
				srl16_1_replacement.addSubCell(FD_1_7);
				srl16_1_replacement.addSubCell(FD_1_8);
				srl16_1_replacement.addSubCell(FD_1_9);
				srl16_1_replacement.addSubCell(FD_1_10);
				srl16_1_replacement.addSubCell(FD_1_11);
				srl16_1_replacement.addSubCell(FD_1_12);
				srl16_1_replacement.addSubCell(FD_1_13);
				srl16_1_replacement.addSubCell(FD_1_14);
				srl16_1_replacement.addSubCell(FD_1_15);
				srl16_1_replacement.addSubCell(FD_1_16);
				srl16_1_replacement.addSubCell(LUT3_1);
				srl16_1_replacement.addSubCell(LUT3_2);
				srl16_1_replacement.addSubCell(LUT3_3);
				srl16_1_replacement.addSubCell(LUT3_4);
				srl16_1_replacement.addSubCell(LUT3_5);
				srl16_1_replacement.addSubCell(LUT3_6);
				srl16_1_replacement.addSubCell(LUT3_7);
				srl16_1_replacement.addSubCell(LUT3_8);
				srl16_1_replacement.addSubCell(LUT3_9);
				srl16_1_replacement.addSubCell(MUXF5_1);
				srl16_1_replacement.addSubCell(MUXF5_2);
				srl16_1_replacement.addSubCell(MUXF5_3);
				srl16_1_replacement.addSubCell(MUXF5_4);
				srl16_1_replacement.addSubCell(MUXF6_1);
				srl16_1_replacement.addSubCell(MUXF6_2);
				
				/******Interface******/
				EdifCellInterface srl16_1_replacement_interface = new EdifCellInterface(srl16_1_replacement);
				EdifCellInterface clk_ibuf_interface = new EdifCellInterface(BUFGP);
				EdifCellInterface d_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface a0_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface a1_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface a2_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface a3_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface q_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface FD_1_1_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_2_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_3_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_4_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_5_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_6_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_7_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_8_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_9_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_10_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_11_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_12_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_13_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_14_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_15_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_16_interface = new EdifCellInterface(FD_1);
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
				//srl16_1_replacement
				EdifPort srl16_1_replacement_interface_clk = new EdifPort(srl16_1_replacement_interface, "clk", 1, 1);
				EdifPort srl16_1_replacement_interface_d = new EdifPort(srl16_1_replacement_interface, "d", 1, 1);
				EdifPort srl16_1_replacement_interface_a0 = new EdifPort(srl16_1_replacement_interface, "a0", 1 ,1);
				EdifPort srl16_1_replacement_interface_a1 = new EdifPort(srl16_1_replacement_interface, "a1", 1 ,1);
				EdifPort srl16_1_replacement_interface_a2 = new EdifPort(srl16_1_replacement_interface, "a2", 1 ,1);
				EdifPort srl16_1_replacement_interface_a3 = new EdifPort(srl16_1_replacement_interface, "a3", 1 ,1);
				EdifPort srl16_1_replacement_interface_q = new EdifPort(srl16_1_replacement_interface, "q", 1, 2);
				srl16_1_replacement_interface.addPort("clk", 1, 1);
				srl16_1_replacement_interface.addPort("d", 1, 1);
				srl16_1_replacement_interface.addPort("a0", 1, 1);
				srl16_1_replacement_interface.addPort("a1", 1, 1);
				srl16_1_replacement_interface.addPort("a2", 1, 1);
				srl16_1_replacement_interface.addPort("a3", 1, 1);
				srl16_1_replacement_interface.addPort("q", 1, 2);
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
				//FD_1_1
				EdifPort FD_1_1_interface_C = new EdifPort(FD_1_1_interface, "C", 1, 1);
				EdifPort FD_1_1_interface_D = new EdifPort(FD_1_1_interface, "D", 1, 1);
				EdifPort FD_1_1_interface_Q = new EdifPort(FD_1_1_interface, "Q", 1, 2);
				FD_1_1_interface.addPort("C", 1, 1);
				FD_1_1_interface.addPort("D", 1, 1);
				FD_1_1_interface.addPort("Q", 1, 2);
				//FD_1_2
				EdifPort FD_1_2_interface_C = new EdifPort(FD_1_2_interface, "C", 1, 1);
				EdifPort FD_1_2_interface_D = new EdifPort(FD_1_2_interface, "D", 1, 1);
				EdifPort FD_1_2_interface_Q = new EdifPort(FD_1_2_interface, "Q", 1, 2);
				FD_1_2_interface.addPort("C", 1, 1);
				FD_1_2_interface.addPort("D", 1, 1);
				FD_1_2_interface.addPort("Q", 1, 2);
				//FD_1_3
				EdifPort FD_1_3_interface_C = new EdifPort(FD_1_3_interface, "C", 1, 1);
				EdifPort FD_1_3_interface_D = new EdifPort(FD_1_3_interface, "D", 1, 1);
				EdifPort FD_1_3_interface_Q = new EdifPort(FD_1_3_interface, "Q", 1, 2);
				FD_1_3_interface.addPort("C", 1, 1);
				FD_1_3_interface.addPort("D", 1, 1);
				FD_1_3_interface.addPort("Q", 1, 2);
				//FD_1_4
				EdifPort FD_1_4_interface_C = new EdifPort(FD_1_4_interface, "C", 1, 1);
				EdifPort FD_1_4_interface_D = new EdifPort(FD_1_4_interface, "D", 1, 1);
				EdifPort FD_1_4_interface_Q = new EdifPort(FD_1_4_interface, "Q", 1, 2);
				FD_1_4_interface.addPort("C", 1, 1);
				FD_1_4_interface.addPort("D", 1, 1);
				FD_1_4_interface.addPort("Q", 1, 2);
				//FD_1_5
				EdifPort FD_1_5_interface_C = new EdifPort(FD_1_5_interface, "C", 1, 1);
				EdifPort FD_1_5_interface_D = new EdifPort(FD_1_5_interface, "D", 1, 1);
				EdifPort FD_1_5_interface_Q = new EdifPort(FD_1_5_interface, "Q", 1, 2);
				FD_1_5_interface.addPort("C", 1, 1);
				FD_1_5_interface.addPort("D", 1, 1);
				FD_1_5_interface.addPort("Q", 1, 2);
				//FD_1_6
				EdifPort FD_1_6_interface_C = new EdifPort(FD_1_6_interface, "C", 1, 1);
				EdifPort FD_1_6_interface_D = new EdifPort(FD_1_6_interface, "D", 1, 1);
				EdifPort FD_1_6_interface_Q = new EdifPort(FD_1_6_interface, "Q", 1, 2);
				FD_1_6_interface.addPort("C", 1, 1);
				FD_1_6_interface.addPort("D", 1, 1);
				FD_1_6_interface.addPort("Q", 1, 2);
				//FD_1_7
				EdifPort FD_1_7_interface_C = new EdifPort(FD_1_7_interface, "C", 1, 1);
				EdifPort FD_1_7_interface_D = new EdifPort(FD_1_7_interface, "D", 1, 1);
				EdifPort FD_1_7_interface_Q = new EdifPort(FD_1_7_interface, "Q", 1, 2);
				FD_1_7_interface.addPort("C", 1, 1);
				FD_1_7_interface.addPort("D", 1, 1);
				FD_1_7_interface.addPort("Q", 1, 2);
				//FD_1_8
				EdifPort FD_1_8_interface_C = new EdifPort(FD_1_8_interface, "C", 1, 1);
				EdifPort FD_1_8_interface_D = new EdifPort(FD_1_8_interface, "D", 1, 1);
				EdifPort FD_1_8_interface_Q = new EdifPort(FD_1_8_interface, "Q", 1, 2);
				FD_1_8_interface.addPort("C", 1, 1);
				FD_1_8_interface.addPort("D", 1, 1);
				FD_1_8_interface.addPort("Q", 1, 2);
				//FD_1_9
				EdifPort FD_1_9_interface_C = new EdifPort(FD_1_9_interface, "C", 1, 1);
				EdifPort FD_1_9_interface_D = new EdifPort(FD_1_9_interface, "D", 1, 1);
				EdifPort FD_1_9_interface_Q = new EdifPort(FD_1_9_interface, "Q", 1, 2);
				FD_1_9_interface.addPort("C", 1, 1);
				FD_1_9_interface.addPort("D", 1, 1);
				FD_1_9_interface.addPort("Q", 1, 2);
				//FD_1_10
				EdifPort FD_1_10_interface_C = new EdifPort(FD_1_10_interface, "C", 1, 1);
				EdifPort FD_1_10_interface_D = new EdifPort(FD_1_10_interface, "D", 1, 1);
				EdifPort FD_1_10_interface_Q = new EdifPort(FD_1_10_interface, "Q", 1, 2);
				FD_1_10_interface.addPort("C", 1, 1);
				FD_1_10_interface.addPort("D", 1, 1);
				FD_1_10_interface.addPort("Q", 1, 2);
				//FD_1_11
				EdifPort FD_1_11_interface_C = new EdifPort(FD_1_11_interface, "C", 1, 1);
				EdifPort FD_1_11_interface_D = new EdifPort(FD_1_11_interface, "D", 1, 1);
				EdifPort FD_1_11_interface_Q = new EdifPort(FD_1_11_interface, "Q", 1, 2);
				FD_1_11_interface.addPort("C", 1, 1);
				FD_1_11_interface.addPort("D", 1, 1);
				FD_1_11_interface.addPort("Q", 1, 2);
				//FD_1_12
				EdifPort FD_1_12_interface_C = new EdifPort(FD_1_12_interface, "C", 1, 1);
				EdifPort FD_1_12_interface_D = new EdifPort(FD_1_12_interface, "D", 1, 1);
				EdifPort FD_1_12_interface_Q = new EdifPort(FD_1_12_interface, "Q", 1, 2);
				FD_1_12_interface.addPort("C", 1, 1);
				FD_1_12_interface.addPort("D", 1, 1);
				FD_1_12_interface.addPort("Q", 1, 2);
				//FD_1_13
				EdifPort FD_1_13_interface_C = new EdifPort(FD_1_13_interface, "C", 1, 1);
				EdifPort FD_1_13_interface_D = new EdifPort(FD_1_13_interface, "D", 1, 1);
				EdifPort FD_1_13_interface_Q = new EdifPort(FD_1_13_interface, "Q", 1, 2);
				FD_1_13_interface.addPort("C", 1, 1);
				FD_1_13_interface.addPort("D", 1, 1);
				FD_1_13_interface.addPort("Q", 1, 2);
				//FD_1_14
				EdifPort FD_1_14_interface_C = new EdifPort(FD_1_14_interface, "C", 1, 1);
				EdifPort FD_1_14_interface_D = new EdifPort(FD_1_14_interface, "D", 1, 1);
				EdifPort FD_1_14_interface_Q = new EdifPort(FD_1_14_interface, "Q", 1, 2);
				FD_1_14_interface.addPort("C", 1, 1);
				FD_1_14_interface.addPort("D", 1, 1);
				FD_1_14_interface.addPort("Q", 1, 2);
				//FD_1_15
				EdifPort FD_1_15_interface_C = new EdifPort(FD_1_15_interface, "C", 1, 1);
				EdifPort FD_1_15_interface_D = new EdifPort(FD_1_15_interface, "D", 1, 1);
				EdifPort FD_1_15_interface_Q = new EdifPort(FD_1_15_interface, "Q", 1, 2);
				FD_1_15_interface.addPort("C", 1, 1);
				FD_1_15_interface.addPort("D", 1, 1);
				FD_1_15_interface.addPort("Q", 1, 2);
				//FD_1_16
				EdifPort FD_1_16_interface_C = new EdifPort(FD_1_16_interface, "C", 1, 1);
				EdifPort FD_1_16_interface_D = new EdifPort(FD_1_16_interface, "D", 1, 1);
				EdifPort FD_1_16_interface_Q = new EdifPort(FD_1_16_interface, "Q", 1, 2);
				FD_1_16_interface.addPort("C", 1, 1);
				FD_1_16_interface.addPort("D", 1, 1);
				FD_1_16_interface.addPort("Q", 1, 2);
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
				EdifPortRef srl16_1_replacement_clk = new EdifPortRef(clk, srl16_1_replacement_interface_clk.getSingleBitPort(0), null);
				EdifPortRef srl16_1_replacement_d = new EdifPortRef(d, srl16_1_replacement_interface_d.getSingleBitPort(0), null);
				EdifPortRef srl16_1_replacement_a0 = new EdifPortRef(a0, srl16_1_replacement_interface_a0.getSingleBitPort(0), null);
				EdifPortRef srl16_1_replacement_a1 = new EdifPortRef(a1, srl16_1_replacement_interface_a1.getSingleBitPort(0), null);
				EdifPortRef srl16_1_replacement_a2 = new EdifPortRef(a2, srl16_1_replacement_interface_a2.getSingleBitPort(0), null);
				EdifPortRef srl16_1_replacement_a3 = new EdifPortRef(a3, srl16_1_replacement_interface_a3.getSingleBitPort(0), null);
				EdifPortRef srl16_1_replacement_q = new EdifPortRef(q, srl16_1_replacement_interface_q.getSingleBitPort(0), null);
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
				EdifPortRef FD_1_1_C = new EdifPortRef(clk_c, FD_1_1_interface_C.getSingleBitPort(0), FD_1_1);
				EdifPortRef FD_1_1_D = new EdifPortRef(d_c, FD_1_1_interface_D.getSingleBitPort(0), FD_1_1);
				EdifPortRef FD_1_1_Q = new EdifPortRef(wire1, FD_1_1_interface_Q.getSingleBitPort(0), FD_1_1);
				EdifPortRef FD_1_2_C = new EdifPortRef(clk_c, FD_1_2_interface_C.getSingleBitPort(0), FD_1_2);
				EdifPortRef FD_1_2_D = new EdifPortRef(wire1, FD_1_2_interface_D.getSingleBitPort(0), FD_1_2);
				EdifPortRef FD_1_2_Q = new EdifPortRef(wire2, FD_1_2_interface_Q.getSingleBitPort(0), FD_1_2);
				EdifPortRef FD_1_3_C = new EdifPortRef(clk_c, FD_1_3_interface_C.getSingleBitPort(0), FD_1_3);
				EdifPortRef FD_1_3_D = new EdifPortRef(wire2, FD_1_3_interface_D.getSingleBitPort(0), FD_1_3);
				EdifPortRef FD_1_3_Q = new EdifPortRef(wire3, FD_1_3_interface_Q.getSingleBitPort(0), FD_1_3);
				EdifPortRef FD_1_4_C = new EdifPortRef(clk_c, FD_1_4_interface_C.getSingleBitPort(0), FD_1_4);
				EdifPortRef FD_1_4_D = new EdifPortRef(wire3, FD_1_4_interface_D.getSingleBitPort(0), FD_1_4);
				EdifPortRef FD_1_4_Q = new EdifPortRef(wire4, FD_1_4_interface_Q.getSingleBitPort(0), FD_1_4);
				EdifPortRef FD_1_5_C = new EdifPortRef(clk_c, FD_1_5_interface_C.getSingleBitPort(0), FD_1_5);
				EdifPortRef FD_1_5_D = new EdifPortRef(wire4, FD_1_5_interface_D.getSingleBitPort(0), FD_1_5);
				EdifPortRef FD_1_5_Q = new EdifPortRef(wire5, FD_1_5_interface_Q.getSingleBitPort(0), FD_1_5);
				EdifPortRef FD_1_6_C = new EdifPortRef(clk_c, FD_1_6_interface_C.getSingleBitPort(0), FD_1_6);
				EdifPortRef FD_1_6_D = new EdifPortRef(wire5, FD_1_6_interface_D.getSingleBitPort(0), FD_1_6);
				EdifPortRef FD_1_6_Q = new EdifPortRef(wire6, FD_1_6_interface_Q.getSingleBitPort(0), FD_1_6);
				EdifPortRef FD_1_7_C = new EdifPortRef(clk_c, FD_1_7_interface_C.getSingleBitPort(0), FD_1_7);
				EdifPortRef FD_1_7_D = new EdifPortRef(wire6, FD_1_7_interface_D.getSingleBitPort(0), FD_1_7);
				EdifPortRef FD_1_7_Q = new EdifPortRef(wire7, FD_1_7_interface_Q.getSingleBitPort(0), FD_1_7);
				EdifPortRef FD_1_8_C = new EdifPortRef(clk_c, FD_1_8_interface_C.getSingleBitPort(0), FD_1_8);
				EdifPortRef FD_1_8_D = new EdifPortRef(wire7, FD_1_8_interface_D.getSingleBitPort(0), FD_1_8);
				EdifPortRef FD_1_8_Q = new EdifPortRef(wire8, FD_1_8_interface_Q.getSingleBitPort(0), FD_1_8);
				EdifPortRef FD_1_9_C = new EdifPortRef(clk_c, FD_1_9_interface_C.getSingleBitPort(0), FD_1_9);
				EdifPortRef FD_1_9_D = new EdifPortRef(wire8, FD_1_9_interface_D.getSingleBitPort(0), FD_1_9);
				EdifPortRef FD_1_9_Q = new EdifPortRef(wire9, FD_1_9_interface_Q.getSingleBitPort(0), FD_1_9);
				EdifPortRef FD_1_10_C = new EdifPortRef(clk_c, FD_1_10_interface_C.getSingleBitPort(0), FD_1_10);
				EdifPortRef FD_1_10_D = new EdifPortRef(wire9, FD_1_10_interface_D.getSingleBitPort(0), FD_1_10);
				EdifPortRef FD_1_10_Q = new EdifPortRef(wire10, FD_1_10_interface_Q.getSingleBitPort(0), FD_1_10);
				EdifPortRef FD_1_11_C = new EdifPortRef(clk_c, FD_1_11_interface_C.getSingleBitPort(0), FD_1_11);
				EdifPortRef FD_1_11_D = new EdifPortRef(wire10, FD_1_11_interface_D.getSingleBitPort(0), FD_1_11);
				EdifPortRef FD_1_11_Q = new EdifPortRef(wire11, FD_1_11_interface_Q.getSingleBitPort(0), FD_1_11);
				EdifPortRef FD_1_12_C = new EdifPortRef(clk_c, FD_1_12_interface_C.getSingleBitPort(0), FD_1_12);
				EdifPortRef FD_1_12_D = new EdifPortRef(wire11, FD_1_12_interface_D.getSingleBitPort(0), FD_1_12);
				EdifPortRef FD_1_12_Q = new EdifPortRef(wire12, FD_1_12_interface_Q.getSingleBitPort(0), FD_1_12);
				EdifPortRef FD_1_13_C = new EdifPortRef(clk_c, FD_1_13_interface_C.getSingleBitPort(0), FD_1_13);
				EdifPortRef FD_1_13_D = new EdifPortRef(wire12, FD_1_13_interface_D.getSingleBitPort(0), FD_1_13);
				EdifPortRef FD_1_13_Q = new EdifPortRef(wire13, FD_1_13_interface_Q.getSingleBitPort(0), FD_1_13);
				EdifPortRef FD_1_14_C = new EdifPortRef(clk_c, FD_1_14_interface_C.getSingleBitPort(0), FD_1_14);
				EdifPortRef FD_1_14_D = new EdifPortRef(wire13, FD_1_14_interface_D.getSingleBitPort(0), FD_1_14);
				EdifPortRef FD_1_14_Q = new EdifPortRef(wire14, FD_1_14_interface_Q.getSingleBitPort(0), FD_1_14);
				EdifPortRef FD_1_15_C = new EdifPortRef(clk_c, FD_1_15_interface_C.getSingleBitPort(0), FD_1_15);
				EdifPortRef FD_1_15_D = new EdifPortRef(wire14, FD_1_15_interface_D.getSingleBitPort(0), FD_1_15);
				EdifPortRef FD_1_15_Q = new EdifPortRef(wire15, FD_1_15_interface_Q.getSingleBitPort(0), FD_1_15);
				EdifPortRef FD_1_16_C = new EdifPortRef(clk_c, FD_1_16_interface_C.getSingleBitPort(0), FD_1_16);
				EdifPortRef FD_1_16_D = new EdifPortRef(wire15, FD_1_16_interface_D.getSingleBitPort(0), FD_1_16);
				EdifPortRef FD_1_16_Q = new EdifPortRef(wire16, FD_1_16_interface_Q.getSingleBitPort(0), FD_1_16);
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
				clk.addPortConnection(srl16_1_replacement_clk);
				//clk_c
				clk_c.addPortConnection(clk_ibuf_O);
				clk_c.addPortConnection(FD_1_1_C);
				clk_c.addPortConnection(FD_1_2_C);
				clk_c.addPortConnection(FD_1_3_C);
				clk_c.addPortConnection(FD_1_4_C);
				clk_c.addPortConnection(FD_1_5_C);
				clk_c.addPortConnection(FD_1_6_C);
				clk_c.addPortConnection(FD_1_7_C);
				clk_c.addPortConnection(FD_1_8_C);
				clk_c.addPortConnection(FD_1_9_C);
				clk_c.addPortConnection(FD_1_10_C);
				clk_c.addPortConnection(FD_1_11_C);
				clk_c.addPortConnection(FD_1_12_C);
				clk_c.addPortConnection(FD_1_13_C);
				clk_c.addPortConnection(FD_1_14_C);
				clk_c.addPortConnection(FD_1_15_C);
				clk_c.addPortConnection(FD_1_16_C);
				//d
				d.addPortConnection(srl16_1_replacement_d);
				d.addPortConnection(d_ibuf_I);
				//d_c
				d_c.addPortConnection(d_ibuf_O);
				d_c.addPortConnection(FD_1_1_D);
				//a0
				a0.addPortConnection(srl16_1_replacement_a0);
				a0.addPortConnection(a0_ibuf_I);
				//a0_c
				a0_c.addPortConnection(a0_ibuf_O);
				a0_c.addPortConnection(LUT3_9_I2);
				//a1
				a1.addPortConnection(srl16_1_replacement_a1);
				a1.addPortConnection(a1_ibuf_I);
				//a1_c
				a1_c.addPortConnection(a1_ibuf_O);
				a1_c.addPortConnection(MUXF6_1_S);
				a1_c.addPortConnection(MUXF6_2_S);
				//a2
				a2.addPortConnection(srl16_1_replacement_a2);
				a2.addPortConnection(a2_ibuf_I);
				//a2_c
				a2_c.addPortConnection(a2_ibuf_O);
				a2_c.addPortConnection(MUXF5_1_S);
				a2_c.addPortConnection(MUXF5_2_S);
				a2_c.addPortConnection(MUXF5_3_S);
				a2_c.addPortConnection(MUXF5_4_S);
				//a3
				a3.addPortConnection(srl16_1_replacement_a3);
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
				wire1.addPortConnection(FD_1_1_Q);
				wire1.addPortConnection(FD_1_2_D);
				//wire2
				wire2.addPortConnection(LUT3_5_I1);
				wire2.addPortConnection(FD_1_2_Q);
				wire2.addPortConnection(FD_1_3_D);
				//wire3
				wire3.addPortConnection(LUT3_3_I1);
				wire3.addPortConnection(FD_1_3_Q);
				wire3.addPortConnection(FD_1_4_D);
				//wire4
				wire4.addPortConnection(LUT3_7_I1);
				wire4.addPortConnection(FD_1_4_Q);
				wire4.addPortConnection(FD_1_5_D);
				//wire5
				wire5.addPortConnection(LUT3_2_I1);
				wire5.addPortConnection(FD_1_5_Q);
				wire5.addPortConnection(FD_1_6_D);
				//wire6
				wire6.addPortConnection(LUT3_6_I1);
				wire6.addPortConnection(FD_1_6_Q);
				wire6.addPortConnection(FD_1_7_D);
				//wire7
				wire7.addPortConnection(LUT3_4_I1);
				wire7.addPortConnection(FD_1_7_Q);
				wire7.addPortConnection(FD_1_8_D);
				//wire8
				wire8.addPortConnection(LUT3_8_I1);
				wire8.addPortConnection(FD_1_8_Q);
				wire8.addPortConnection(FD_1_9_D);
				//wire9
				wire9.addPortConnection(LUT3_1_I2);
				wire9.addPortConnection(FD_1_9_Q);
				wire9.addPortConnection(FD_1_10_D);
				//wire10
				wire10.addPortConnection(LUT3_5_I2);
				wire10.addPortConnection(FD_1_10_Q);
				wire10.addPortConnection(FD_1_11_D);
				//wire11
				wire11.addPortConnection(LUT3_3_I2);
				wire11.addPortConnection(FD_1_11_Q);
				wire11.addPortConnection(FD_1_12_D);
				//wire12
				wire12.addPortConnection(LUT3_7_I2);
				wire12.addPortConnection(FD_1_12_Q);
				wire12.addPortConnection(FD_1_13_D);
				//wire13
				wire13.addPortConnection(LUT3_2_I2);
				wire13.addPortConnection(FD_1_13_Q);
				wire13.addPortConnection(FD_1_14_D);
				//wire14
				wire14.addPortConnection(LUT3_6_I2);
				wire14.addPortConnection(FD_1_14_Q);
				wire14.addPortConnection(FD_1_15_D);
				//wire15
				wire15.addPortConnection(LUT3_4_I2);
				wire15.addPortConnection(FD_1_15_Q);
				wire15.addPortConnection(FD_1_16_D);
				//wire16
				wire16.addPortConnection(LUT3_8_I2);
				wire16.addPortConnection(FD_1_16_Q);
				//q
				q.addPortConnection(srl16_1_replacement_q);
				q.addPortConnection(q_obuf_O);
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
		        long initInt = 15;
		        StringTypedValue valueZero = new StringTypedValue("0");
		        StringTypedValue valueOne = new StringTypedValue("1");
		        StringTypedValue valueE4 = new StringTypedValue("E4");
		        StringTypedValue valueCA = new StringTypedValue("CA");
		        //Set INIT Property for FD_1_1
				PropertyList FD_1_1_propertylist = FD_1_1.getPropertyList();
				if (FD_1_1_propertylist != null) {
                    for (Property FD_1_1_property : FD_1_1_propertylist.values()) {
                        if (FD_1_1_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_1_property.setValue(valueOne);
                            else
                                FD_1_1_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_1.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_1.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_2
				PropertyList FD_1_2_propertylist = FD_1_2.getPropertyList();
				if (FD_1_2_propertylist != null) {
                    for (Property FD_1_2_property : FD_1_2_propertylist.values()) {
                        if (FD_1_2_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_2_property.setValue(valueOne);
                            else
                                FD_1_2_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_2.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_2.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_3
				PropertyList FD_1_3_propertylist = FD_1_3.getPropertyList();
				if (FD_1_3_propertylist != null) {
                    for (Property FD_1_3_property : FD_1_3_propertylist.values()) {
                        if (FD_1_3_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_3_property.setValue(valueOne);
                            else
                                FD_1_3_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_3.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_3.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_4
				PropertyList FD_1_4_propertylist = FD_1_4.getPropertyList();
				if (FD_1_4_propertylist != null) {
                    for (Property FD_1_4_property : FD_1_4_propertylist.values()) {
                        if (FD_1_4_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_4_property.setValue(valueOne);
                            else
                                FD_1_4_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_4.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_4.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_5
				PropertyList FD_1_5_propertylist = FD_1_5.getPropertyList();
				if (FD_1_5_propertylist != null) {
                    for (Property FD_1_5_property : FD_1_5_propertylist.values()) {
                        if (FD_1_5_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_5_property.setValue(valueOne);
                            else
                                FD_1_5_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_5.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_5.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_6
				PropertyList FD_1_6_propertylist = FD_1_6.getPropertyList();
				if (FD_1_6_propertylist != null) {
                    for (Property FD_1_6_property : FD_1_6_propertylist.values()) {
                        if (FD_1_6_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_6_property.setValue(valueOne);
                            else
                                FD_1_6_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_6.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_6.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_7
				PropertyList FD_1_7_propertylist = FD_1_7.getPropertyList();
				if (FD_1_7_propertylist != null) {
                    for (Property FD_1_7_property : FD_1_7_propertylist.values()) {
                        if (FD_1_7_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_7_property.setValue(valueOne);
                            else
                                FD_1_7_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_7.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_7.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_8
				PropertyList FD_1_8_propertylist = FD_1_8.getPropertyList();
				if (FD_1_8_propertylist != null) {
                    for (Property FD_1_8_property : FD_1_8_propertylist.values()) {
                        if (FD_1_8_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_8_property.setValue(valueOne);
                            else
                                FD_1_8_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_8.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_8.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_9
				PropertyList FD_1_9_propertylist = FD_1_9.getPropertyList();
				if (FD_1_9_propertylist != null) {
                    for (Property FD_1_9_property : FD_1_9_propertylist.values()) {
                        if (FD_1_9_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_9_property.setValue(valueOne);
                            else
                                FD_1_9_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_9.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_9.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_10
				PropertyList FD_1_10_propertylist = FD_1_10.getPropertyList();
				if (FD_1_10_propertylist != null) {
                    for (Property FD_1_10_property : FD_1_10_propertylist.values()) {
                        if (FD_1_10_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_10_property.setValue(valueOne);
                            else
                                FD_1_10_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_10.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_10.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_11
				PropertyList FD_1_11_propertylist = FD_1_11.getPropertyList();
				if (FD_1_11_propertylist != null) {
                    for (Property FD_1_11_property : FD_1_11_propertylist.values()) {
                        if (FD_1_11_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_11_property.setValue(valueOne);
                            else
                                FD_1_11_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_11.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_11.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_12
				PropertyList FD_1_12_propertylist = FD_1_12.getPropertyList();
				if (FD_1_12_propertylist != null) {
                    for (Property FD_1_12_property : FD_1_12_propertylist.values()) {
                        if (FD_1_12_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_12_property.setValue(valueOne);
                            else
                                FD_1_12_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_12.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_12.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_13
				PropertyList FD_1_13_propertylist = FD_1_13.getPropertyList();
				if (FD_1_13_propertylist != null) {
                    for (Property FD_1_13_property : FD_1_13_propertylist.values()) {
                        if (FD_1_13_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_13_property.setValue(valueOne);
                            else
                                FD_1_13_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_13.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_13.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_14
				PropertyList FD_1_14_propertylist = FD_1_14.getPropertyList();
				if (FD_1_14_propertylist != null) {
                    for (Property FD_1_14_property : FD_1_14_propertylist.values()) {
                        if (FD_1_14_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_14_property.setValue(valueOne);
                            else
                                FD_1_14_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_14.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_14.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_15
				PropertyList FD_1_15_propertylist = FD_1_15.getPropertyList();
				if (FD_1_15_propertylist != null) {
                    for (Property FD_1_15_property : FD_1_15_propertylist.values()) {
                        if (FD_1_15_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_15_property.setValue(valueOne);
                            else
                                FD_1_15_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_15.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_15.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_16
				PropertyList FD_1_16_propertylist = FD_1_16.getPropertyList();
				if (FD_1_16_propertylist != null) {
                    for (Property FD_1_16_property : FD_1_16_propertylist.values()) {
                        if (FD_1_16_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((initInt >> initCount++) & 1) == 1)
                                FD_1_16_property.setValue(valueOne);
                            else
                                FD_1_16_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((initInt >> initCount++) & 1) == 1)
                        FD_1_16.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_16.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
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
	
	public static void Replace(EdifLibraryManager libManager, EdifCell srl16_1_replacement, long INIT,
			EdifNet d, EdifNet clk, EdifNet a0, EdifNet a1, EdifNet a2, EdifNet a3, EdifNet q){
		try{
			try{
				/******Cells******/
				EdifCell FD_1 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FD_1");
				EdifCell LUT3 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "LUT3");
				EdifCell MUXF5 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "MUXF5");
				EdifCell MUXF6 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "MUXF6");		
				
				/******Nets******/
				EdifNet wire1 = new EdifNet("wire1", srl16_1_replacement);
				EdifNet wire2 = new EdifNet("wire2", srl16_1_replacement);
				EdifNet wire3 = new EdifNet("wire3", srl16_1_replacement);
				EdifNet wire4 = new EdifNet("wire4", srl16_1_replacement);
				EdifNet wire5 = new EdifNet("wire5", srl16_1_replacement);
				EdifNet wire6 = new EdifNet("wire6", srl16_1_replacement);
				EdifNet wire7 = new EdifNet("wire7", srl16_1_replacement);
				EdifNet wire8 = new EdifNet("wire8", srl16_1_replacement);
				EdifNet wire9 = new EdifNet("wire9", srl16_1_replacement);
				EdifNet wire10 = new EdifNet("wire10", srl16_1_replacement);
				EdifNet wire11 = new EdifNet("wire11", srl16_1_replacement);
				EdifNet wire12 = new EdifNet("wire12", srl16_1_replacement);
				EdifNet wire13 = new EdifNet("wire13", srl16_1_replacement);
				EdifNet wire14 = new EdifNet("wire14", srl16_1_replacement);
				EdifNet wire15 = new EdifNet("wire15", srl16_1_replacement);
				EdifNet wire16 = new EdifNet("wire16", srl16_1_replacement);
				EdifNet net1 = new EdifNet("net1", srl16_1_replacement);
				EdifNet net2 = new EdifNet("net2", srl16_1_replacement);
				EdifNet net3 = new EdifNet("net3", srl16_1_replacement);
				EdifNet net4 = new EdifNet("net4", srl16_1_replacement);
				EdifNet net5 = new EdifNet("net5", srl16_1_replacement);
				EdifNet net6 = new EdifNet("net6", srl16_1_replacement);
				EdifNet net7 = new EdifNet("net7", srl16_1_replacement);
				EdifNet net8 = new EdifNet("net8", srl16_1_replacement);
				EdifNet net9 = new EdifNet("net9", srl16_1_replacement);
				EdifNet net10 = new EdifNet("net10", srl16_1_replacement);
				EdifNet net11 = new EdifNet("net11", srl16_1_replacement);
				EdifNet net12 = new EdifNet("net12", srl16_1_replacement);
				EdifNet net13 = new EdifNet("net13", srl16_1_replacement);
				EdifNet net14 = new EdifNet("net14", srl16_1_replacement);
				srl16_1_replacement.addNet(wire1);
				srl16_1_replacement.addNet(wire2);
				srl16_1_replacement.addNet(wire3);
				srl16_1_replacement.addNet(wire4);
				srl16_1_replacement.addNet(wire5);
				srl16_1_replacement.addNet(wire6);
				srl16_1_replacement.addNet(wire7);
				srl16_1_replacement.addNet(wire8);
				srl16_1_replacement.addNet(wire9);
				srl16_1_replacement.addNet(wire10);
				srl16_1_replacement.addNet(wire11);
				srl16_1_replacement.addNet(wire12);
				srl16_1_replacement.addNet(wire13);
				srl16_1_replacement.addNet(wire14);
				srl16_1_replacement.addNet(wire15);
				srl16_1_replacement.addNet(wire16);
				srl16_1_replacement.addNet(net1);
				srl16_1_replacement.addNet(net2);
				srl16_1_replacement.addNet(net3);
				srl16_1_replacement.addNet(net4);
				srl16_1_replacement.addNet(net5);
				srl16_1_replacement.addNet(net6);
				srl16_1_replacement.addNet(net7);
				srl16_1_replacement.addNet(net8);
				srl16_1_replacement.addNet(net9);
				srl16_1_replacement.addNet(net10);
				srl16_1_replacement.addNet(net11);
				srl16_1_replacement.addNet(net12);
				srl16_1_replacement.addNet(net13);
				srl16_1_replacement.addNet(net14);
				
				/******Instances******/
				EdifCellInstance FD_1_1 = new EdifCellInstance("FD_1_1", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_2 = new EdifCellInstance("FD_1_2", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_3 = new EdifCellInstance("FD_1_3", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_4 = new EdifCellInstance("FD_1_4", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_5 = new EdifCellInstance("FD_1_5", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_6 = new EdifCellInstance("FD_1_6", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_7 = new EdifCellInstance("FD_1_7", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_8 = new EdifCellInstance("FD_1_8", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_9 = new EdifCellInstance("FD_1_9", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_10 = new EdifCellInstance("FD_1_10", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_11 = new EdifCellInstance("FD_1_11", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_12 = new EdifCellInstance("FD_1_12", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_13 = new EdifCellInstance("FD_1_13", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_14 = new EdifCellInstance("FD_1_14", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_15 = new EdifCellInstance("FD_1_15", srl16_1_replacement, FD_1);
				EdifCellInstance FD_1_16 = new EdifCellInstance("FD_1_16", srl16_1_replacement, FD_1);
				EdifCellInstance LUT3_1 = new EdifCellInstance("LUT3_1", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_2 = new EdifCellInstance("LUT3_2", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_3 = new EdifCellInstance("LUT3_3", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_4 = new EdifCellInstance("LUT3_4", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_5 = new EdifCellInstance("LUT3_5", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_6 = new EdifCellInstance("LUT3_6", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_7 = new EdifCellInstance("LUT3_7", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_8 = new EdifCellInstance("LUT3_8", srl16_1_replacement, LUT3);
				EdifCellInstance LUT3_9 = new EdifCellInstance("LUT3_9", srl16_1_replacement, LUT3);
				EdifCellInstance MUXF5_1 = new EdifCellInstance("MUXF5_1", srl16_1_replacement, MUXF5);
				EdifCellInstance MUXF5_2 = new EdifCellInstance("MUXF5_2", srl16_1_replacement, MUXF5);
				EdifCellInstance MUXF5_3 = new EdifCellInstance("MUXF5_3", srl16_1_replacement, MUXF5);
				EdifCellInstance MUXF5_4 = new EdifCellInstance("MUXF5_4", srl16_1_replacement, MUXF5);
				EdifCellInstance MUXF6_1 = new EdifCellInstance("MUXF6_1", srl16_1_replacement, MUXF6);
				EdifCellInstance MUXF6_2 = new EdifCellInstance("MUXF6_2", srl16_1_replacement, MUXF6);
				srl16_1_replacement.addSubCell(FD_1_1);
				srl16_1_replacement.addSubCell(FD_1_2);
				srl16_1_replacement.addSubCell(FD_1_3);
				srl16_1_replacement.addSubCell(FD_1_4);
				srl16_1_replacement.addSubCell(FD_1_5);
				srl16_1_replacement.addSubCell(FD_1_6);
				srl16_1_replacement.addSubCell(FD_1_7);
				srl16_1_replacement.addSubCell(FD_1_8);
				srl16_1_replacement.addSubCell(FD_1_9);
				srl16_1_replacement.addSubCell(FD_1_10);
				srl16_1_replacement.addSubCell(FD_1_11);
				srl16_1_replacement.addSubCell(FD_1_12);
				srl16_1_replacement.addSubCell(FD_1_13);
				srl16_1_replacement.addSubCell(FD_1_14);
				srl16_1_replacement.addSubCell(FD_1_15);
				srl16_1_replacement.addSubCell(FD_1_16);
				srl16_1_replacement.addSubCell(LUT3_1);
				srl16_1_replacement.addSubCell(LUT3_2);
				srl16_1_replacement.addSubCell(LUT3_3);
				srl16_1_replacement.addSubCell(LUT3_4);
				srl16_1_replacement.addSubCell(LUT3_5);
				srl16_1_replacement.addSubCell(LUT3_6);
				srl16_1_replacement.addSubCell(LUT3_7);
				srl16_1_replacement.addSubCell(LUT3_8);
				srl16_1_replacement.addSubCell(LUT3_9);
				srl16_1_replacement.addSubCell(MUXF5_1);
				srl16_1_replacement.addSubCell(MUXF5_2);
				srl16_1_replacement.addSubCell(MUXF5_3);
				srl16_1_replacement.addSubCell(MUXF5_4);
				srl16_1_replacement.addSubCell(MUXF6_1);
				srl16_1_replacement.addSubCell(MUXF6_2);
				
				/******Interface******/
				EdifCellInterface FD_1_1_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_2_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_3_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_4_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_5_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_6_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_7_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_8_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_9_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_10_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_11_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_12_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_13_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_14_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_15_interface = new EdifCellInterface(FD_1);
				EdifCellInterface FD_1_16_interface = new EdifCellInterface(FD_1);
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
				//FD_1_1
				EdifPort FD_1_1_interface_C = new EdifPort(FD_1_1_interface, "C", 1, 1);
				EdifPort FD_1_1_interface_D = new EdifPort(FD_1_1_interface, "D", 1, 1);
				EdifPort FD_1_1_interface_Q = new EdifPort(FD_1_1_interface, "Q", 1, 2);
				FD_1_1_interface.addPort("C", 1, 1);
				FD_1_1_interface.addPort("D", 1, 1);
				FD_1_1_interface.addPort("Q", 1, 2);
				//FD_1_2
				EdifPort FD_1_2_interface_C = new EdifPort(FD_1_2_interface, "C", 1, 1);
				EdifPort FD_1_2_interface_D = new EdifPort(FD_1_2_interface, "D", 1, 1);
				EdifPort FD_1_2_interface_Q = new EdifPort(FD_1_2_interface, "Q", 1, 2);
				FD_1_2_interface.addPort("C", 1, 1);
				FD_1_2_interface.addPort("D", 1, 1);
				FD_1_2_interface.addPort("Q", 1, 2);
				//FD_1_3
				EdifPort FD_1_3_interface_C = new EdifPort(FD_1_3_interface, "C", 1, 1);
				EdifPort FD_1_3_interface_D = new EdifPort(FD_1_3_interface, "D", 1, 1);
				EdifPort FD_1_3_interface_Q = new EdifPort(FD_1_3_interface, "Q", 1, 2);
				FD_1_3_interface.addPort("C", 1, 1);
				FD_1_3_interface.addPort("D", 1, 1);
				FD_1_3_interface.addPort("Q", 1, 2);
				//FD_1_4
				EdifPort FD_1_4_interface_C = new EdifPort(FD_1_4_interface, "C", 1, 1);
				EdifPort FD_1_4_interface_D = new EdifPort(FD_1_4_interface, "D", 1, 1);
				EdifPort FD_1_4_interface_Q = new EdifPort(FD_1_4_interface, "Q", 1, 2);
				FD_1_4_interface.addPort("C", 1, 1);
				FD_1_4_interface.addPort("D", 1, 1);
				FD_1_4_interface.addPort("Q", 1, 2);
				//FD_1_5
				EdifPort FD_1_5_interface_C = new EdifPort(FD_1_5_interface, "C", 1, 1);
				EdifPort FD_1_5_interface_D = new EdifPort(FD_1_5_interface, "D", 1, 1);
				EdifPort FD_1_5_interface_Q = new EdifPort(FD_1_5_interface, "Q", 1, 2);
				FD_1_5_interface.addPort("C", 1, 1);
				FD_1_5_interface.addPort("D", 1, 1);
				FD_1_5_interface.addPort("Q", 1, 2);
				//FD_1_6
				EdifPort FD_1_6_interface_C = new EdifPort(FD_1_6_interface, "C", 1, 1);
				EdifPort FD_1_6_interface_D = new EdifPort(FD_1_6_interface, "D", 1, 1);
				EdifPort FD_1_6_interface_Q = new EdifPort(FD_1_6_interface, "Q", 1, 2);
				FD_1_6_interface.addPort("C", 1, 1);
				FD_1_6_interface.addPort("D", 1, 1);
				FD_1_6_interface.addPort("Q", 1, 2);
				//FD_1_7
				EdifPort FD_1_7_interface_C = new EdifPort(FD_1_7_interface, "C", 1, 1);
				EdifPort FD_1_7_interface_D = new EdifPort(FD_1_7_interface, "D", 1, 1);
				EdifPort FD_1_7_interface_Q = new EdifPort(FD_1_7_interface, "Q", 1, 2);
				FD_1_7_interface.addPort("C", 1, 1);
				FD_1_7_interface.addPort("D", 1, 1);
				FD_1_7_interface.addPort("Q", 1, 2);
				//FD_1_8
				EdifPort FD_1_8_interface_C = new EdifPort(FD_1_8_interface, "C", 1, 1);
				EdifPort FD_1_8_interface_D = new EdifPort(FD_1_8_interface, "D", 1, 1);
				EdifPort FD_1_8_interface_Q = new EdifPort(FD_1_8_interface, "Q", 1, 2);
				FD_1_8_interface.addPort("C", 1, 1);
				FD_1_8_interface.addPort("D", 1, 1);
				FD_1_8_interface.addPort("Q", 1, 2);
				//FD_1_9
				EdifPort FD_1_9_interface_C = new EdifPort(FD_1_9_interface, "C", 1, 1);
				EdifPort FD_1_9_interface_D = new EdifPort(FD_1_9_interface, "D", 1, 1);
				EdifPort FD_1_9_interface_Q = new EdifPort(FD_1_9_interface, "Q", 1, 2);
				FD_1_9_interface.addPort("C", 1, 1);
				FD_1_9_interface.addPort("D", 1, 1);
				FD_1_9_interface.addPort("Q", 1, 2);
				//FD_1_10
				EdifPort FD_1_10_interface_C = new EdifPort(FD_1_10_interface, "C", 1, 1);
				EdifPort FD_1_10_interface_D = new EdifPort(FD_1_10_interface, "D", 1, 1);
				EdifPort FD_1_10_interface_Q = new EdifPort(FD_1_10_interface, "Q", 1, 2);
				FD_1_10_interface.addPort("C", 1, 1);
				FD_1_10_interface.addPort("D", 1, 1);
				FD_1_10_interface.addPort("Q", 1, 2);
				//FD_1_11
				EdifPort FD_1_11_interface_C = new EdifPort(FD_1_11_interface, "C", 1, 1);
				EdifPort FD_1_11_interface_D = new EdifPort(FD_1_11_interface, "D", 1, 1);
				EdifPort FD_1_11_interface_Q = new EdifPort(FD_1_11_interface, "Q", 1, 2);
				FD_1_11_interface.addPort("C", 1, 1);
				FD_1_11_interface.addPort("D", 1, 1);
				FD_1_11_interface.addPort("Q", 1, 2);
				//FD_1_12
				EdifPort FD_1_12_interface_C = new EdifPort(FD_1_12_interface, "C", 1, 1);
				EdifPort FD_1_12_interface_D = new EdifPort(FD_1_12_interface, "D", 1, 1);
				EdifPort FD_1_12_interface_Q = new EdifPort(FD_1_12_interface, "Q", 1, 2);
				FD_1_12_interface.addPort("C", 1, 1);
				FD_1_12_interface.addPort("D", 1, 1);
				FD_1_12_interface.addPort("Q", 1, 2);
				//FD_1_13
				EdifPort FD_1_13_interface_C = new EdifPort(FD_1_13_interface, "C", 1, 1);
				EdifPort FD_1_13_interface_D = new EdifPort(FD_1_13_interface, "D", 1, 1);
				EdifPort FD_1_13_interface_Q = new EdifPort(FD_1_13_interface, "Q", 1, 2);
				FD_1_13_interface.addPort("C", 1, 1);
				FD_1_13_interface.addPort("D", 1, 1);
				FD_1_13_interface.addPort("Q", 1, 2);
				//FD_1_14
				EdifPort FD_1_14_interface_C = new EdifPort(FD_1_14_interface, "C", 1, 1);
				EdifPort FD_1_14_interface_D = new EdifPort(FD_1_14_interface, "D", 1, 1);
				EdifPort FD_1_14_interface_Q = new EdifPort(FD_1_14_interface, "Q", 1, 2);
				FD_1_14_interface.addPort("C", 1, 1);
				FD_1_14_interface.addPort("D", 1, 1);
				FD_1_14_interface.addPort("Q", 1, 2);
				//FD_1_15
				EdifPort FD_1_15_interface_C = new EdifPort(FD_1_15_interface, "C", 1, 1);
				EdifPort FD_1_15_interface_D = new EdifPort(FD_1_15_interface, "D", 1, 1);
				EdifPort FD_1_15_interface_Q = new EdifPort(FD_1_15_interface, "Q", 1, 2);
				FD_1_15_interface.addPort("C", 1, 1);
				FD_1_15_interface.addPort("D", 1, 1);
				FD_1_15_interface.addPort("Q", 1, 2);
				//FD_1_16
				EdifPort FD_1_16_interface_C = new EdifPort(FD_1_16_interface, "C", 1, 1);
				EdifPort FD_1_16_interface_D = new EdifPort(FD_1_16_interface, "D", 1, 1);
				EdifPort FD_1_16_interface_Q = new EdifPort(FD_1_16_interface, "Q", 1, 2);
				FD_1_16_interface.addPort("C", 1, 1);
				FD_1_16_interface.addPort("D", 1, 1);
				FD_1_16_interface.addPort("Q", 1, 2);
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
				EdifPortRef FD_1_1_C = new EdifPortRef(clk, FD_1_1_interface_C.getSingleBitPort(0), FD_1_1);
				EdifPortRef FD_1_1_D = new EdifPortRef(d, FD_1_1_interface_D.getSingleBitPort(0), FD_1_1);
				EdifPortRef FD_1_1_Q = new EdifPortRef(wire1, FD_1_1_interface_Q.getSingleBitPort(0), FD_1_1);
				EdifPortRef FD_1_2_C = new EdifPortRef(clk, FD_1_2_interface_C.getSingleBitPort(0), FD_1_2);
				EdifPortRef FD_1_2_D = new EdifPortRef(wire1, FD_1_2_interface_D.getSingleBitPort(0), FD_1_2);
				EdifPortRef FD_1_2_Q = new EdifPortRef(wire2, FD_1_2_interface_Q.getSingleBitPort(0), FD_1_2);
				EdifPortRef FD_1_3_C = new EdifPortRef(clk, FD_1_3_interface_C.getSingleBitPort(0), FD_1_3);
				EdifPortRef FD_1_3_D = new EdifPortRef(wire2, FD_1_3_interface_D.getSingleBitPort(0), FD_1_3);
				EdifPortRef FD_1_3_Q = new EdifPortRef(wire3, FD_1_3_interface_Q.getSingleBitPort(0), FD_1_3);
				EdifPortRef FD_1_4_C = new EdifPortRef(clk, FD_1_4_interface_C.getSingleBitPort(0), FD_1_4);
				EdifPortRef FD_1_4_D = new EdifPortRef(wire3, FD_1_4_interface_D.getSingleBitPort(0), FD_1_4);
				EdifPortRef FD_1_4_Q = new EdifPortRef(wire4, FD_1_4_interface_Q.getSingleBitPort(0), FD_1_4);
				EdifPortRef FD_1_5_C = new EdifPortRef(clk, FD_1_5_interface_C.getSingleBitPort(0), FD_1_5);
				EdifPortRef FD_1_5_D = new EdifPortRef(wire4, FD_1_5_interface_D.getSingleBitPort(0), FD_1_5);
				EdifPortRef FD_1_5_Q = new EdifPortRef(wire5, FD_1_5_interface_Q.getSingleBitPort(0), FD_1_5);
				EdifPortRef FD_1_6_C = new EdifPortRef(clk, FD_1_6_interface_C.getSingleBitPort(0), FD_1_6);
				EdifPortRef FD_1_6_D = new EdifPortRef(wire5, FD_1_6_interface_D.getSingleBitPort(0), FD_1_6);
				EdifPortRef FD_1_6_Q = new EdifPortRef(wire6, FD_1_6_interface_Q.getSingleBitPort(0), FD_1_6);
				EdifPortRef FD_1_7_C = new EdifPortRef(clk, FD_1_7_interface_C.getSingleBitPort(0), FD_1_7);
				EdifPortRef FD_1_7_D = new EdifPortRef(wire6, FD_1_7_interface_D.getSingleBitPort(0), FD_1_7);
				EdifPortRef FD_1_7_Q = new EdifPortRef(wire7, FD_1_7_interface_Q.getSingleBitPort(0), FD_1_7);
				EdifPortRef FD_1_8_C = new EdifPortRef(clk, FD_1_8_interface_C.getSingleBitPort(0), FD_1_8);
				EdifPortRef FD_1_8_D = new EdifPortRef(wire7, FD_1_8_interface_D.getSingleBitPort(0), FD_1_8);
				EdifPortRef FD_1_8_Q = new EdifPortRef(wire8, FD_1_8_interface_Q.getSingleBitPort(0), FD_1_8);
				EdifPortRef FD_1_9_C = new EdifPortRef(clk, FD_1_9_interface_C.getSingleBitPort(0), FD_1_9);
				EdifPortRef FD_1_9_D = new EdifPortRef(wire8, FD_1_9_interface_D.getSingleBitPort(0), FD_1_9);
				EdifPortRef FD_1_9_Q = new EdifPortRef(wire9, FD_1_9_interface_Q.getSingleBitPort(0), FD_1_9);
				EdifPortRef FD_1_10_C = new EdifPortRef(clk, FD_1_10_interface_C.getSingleBitPort(0), FD_1_10);
				EdifPortRef FD_1_10_D = new EdifPortRef(wire9, FD_1_10_interface_D.getSingleBitPort(0), FD_1_10);
				EdifPortRef FD_1_10_Q = new EdifPortRef(wire10, FD_1_10_interface_Q.getSingleBitPort(0), FD_1_10);
				EdifPortRef FD_1_11_C = new EdifPortRef(clk, FD_1_11_interface_C.getSingleBitPort(0), FD_1_11);
				EdifPortRef FD_1_11_D = new EdifPortRef(wire10, FD_1_11_interface_D.getSingleBitPort(0), FD_1_11);
				EdifPortRef FD_1_11_Q = new EdifPortRef(wire11, FD_1_11_interface_Q.getSingleBitPort(0), FD_1_11);
				EdifPortRef FD_1_12_C = new EdifPortRef(clk, FD_1_12_interface_C.getSingleBitPort(0), FD_1_12);
				EdifPortRef FD_1_12_D = new EdifPortRef(wire11, FD_1_12_interface_D.getSingleBitPort(0), FD_1_12);
				EdifPortRef FD_1_12_Q = new EdifPortRef(wire12, FD_1_12_interface_Q.getSingleBitPort(0), FD_1_12);
				EdifPortRef FD_1_13_C = new EdifPortRef(clk, FD_1_13_interface_C.getSingleBitPort(0), FD_1_13);
				EdifPortRef FD_1_13_D = new EdifPortRef(wire12, FD_1_13_interface_D.getSingleBitPort(0), FD_1_13);
				EdifPortRef FD_1_13_Q = new EdifPortRef(wire13, FD_1_13_interface_Q.getSingleBitPort(0), FD_1_13);
				EdifPortRef FD_1_14_C = new EdifPortRef(clk, FD_1_14_interface_C.getSingleBitPort(0), FD_1_14);
				EdifPortRef FD_1_14_D = new EdifPortRef(wire13, FD_1_14_interface_D.getSingleBitPort(0), FD_1_14);
				EdifPortRef FD_1_14_Q = new EdifPortRef(wire14, FD_1_14_interface_Q.getSingleBitPort(0), FD_1_14);
				EdifPortRef FD_1_15_C = new EdifPortRef(clk, FD_1_15_interface_C.getSingleBitPort(0), FD_1_15);
				EdifPortRef FD_1_15_D = new EdifPortRef(wire14, FD_1_15_interface_D.getSingleBitPort(0), FD_1_15);
				EdifPortRef FD_1_15_Q = new EdifPortRef(wire15, FD_1_15_interface_Q.getSingleBitPort(0), FD_1_15);
				EdifPortRef FD_1_16_C = new EdifPortRef(clk, FD_1_16_interface_C.getSingleBitPort(0), FD_1_16);
				EdifPortRef FD_1_16_D = new EdifPortRef(wire15, FD_1_16_interface_D.getSingleBitPort(0), FD_1_16);
				EdifPortRef FD_1_16_Q = new EdifPortRef(wire16, FD_1_16_interface_Q.getSingleBitPort(0), FD_1_16);
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
				clk.addPortConnection(FD_1_1_C);
				clk.addPortConnection(FD_1_2_C);
				clk.addPortConnection(FD_1_3_C);
				clk.addPortConnection(FD_1_4_C);
				clk.addPortConnection(FD_1_5_C);
				clk.addPortConnection(FD_1_6_C);
				clk.addPortConnection(FD_1_7_C);
				clk.addPortConnection(FD_1_8_C);
				clk.addPortConnection(FD_1_9_C);
				clk.addPortConnection(FD_1_10_C);
				clk.addPortConnection(FD_1_11_C);
				clk.addPortConnection(FD_1_12_C);
				clk.addPortConnection(FD_1_13_C);
				clk.addPortConnection(FD_1_14_C);
				clk.addPortConnection(FD_1_15_C);
				clk.addPortConnection(FD_1_16_C);
				//d
				d.addPortConnection(FD_1_1_D);
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
				wire1.addPortConnection(FD_1_1_Q);
				wire1.addPortConnection(FD_1_2_D);
				//wire2
				wire2.addPortConnection(LUT3_5_I1);
				wire2.addPortConnection(FD_1_2_Q);
				wire2.addPortConnection(FD_1_3_D);
				//wire3
				wire3.addPortConnection(LUT3_3_I1);
				wire3.addPortConnection(FD_1_3_Q);
				wire3.addPortConnection(FD_1_4_D);
				//wire4
				wire4.addPortConnection(LUT3_7_I1);
				wire4.addPortConnection(FD_1_4_Q);
				wire4.addPortConnection(FD_1_5_D);
				//wire5
				wire5.addPortConnection(LUT3_2_I1);
				wire5.addPortConnection(FD_1_5_Q);
				wire5.addPortConnection(FD_1_6_D);
				//wire6
				wire6.addPortConnection(LUT3_6_I1);
				wire6.addPortConnection(FD_1_6_Q);
				wire6.addPortConnection(FD_1_7_D);
				//wire7
				wire7.addPortConnection(LUT3_4_I1);
				wire7.addPortConnection(FD_1_7_Q);
				wire7.addPortConnection(FD_1_8_D);
				//wire8
				wire8.addPortConnection(LUT3_8_I1);
				wire8.addPortConnection(FD_1_8_Q);
				wire8.addPortConnection(FD_1_9_D);
				//wire9
				wire9.addPortConnection(LUT3_1_I2);
				wire9.addPortConnection(FD_1_9_Q);
				wire9.addPortConnection(FD_1_10_D);
				//wire10
				wire10.addPortConnection(LUT3_5_I2);
				wire10.addPortConnection(FD_1_10_Q);
				wire10.addPortConnection(FD_1_11_D);
				//wire11
				wire11.addPortConnection(LUT3_3_I2);
				wire11.addPortConnection(FD_1_11_Q);
				wire11.addPortConnection(FD_1_12_D);
				//wire12
				wire12.addPortConnection(LUT3_7_I2);
				wire12.addPortConnection(FD_1_12_Q);
				wire12.addPortConnection(FD_1_13_D);
				//wire13
				wire13.addPortConnection(LUT3_2_I2);
				wire13.addPortConnection(FD_1_13_Q);
				wire13.addPortConnection(FD_1_14_D);
				//wire14
				wire14.addPortConnection(LUT3_6_I2);
				wire14.addPortConnection(FD_1_14_Q);
				wire14.addPortConnection(FD_1_15_D);
				//wire15
				wire15.addPortConnection(LUT3_4_I2);
				wire15.addPortConnection(FD_1_15_Q);
				wire15.addPortConnection(FD_1_16_D);
				//wire16
				wire16.addPortConnection(LUT3_8_I2);
				wire16.addPortConnection(FD_1_16_Q);
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
		        //Set INIT Property for FD_1_1
				PropertyList FD_1_1_propertylist = FD_1_1.getPropertyList();
				if (FD_1_1_propertylist != null) {
                    for (Property FD_1_1_property : FD_1_1_propertylist.values()) {
                        if (FD_1_1_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_1_property.setValue(valueOne);
                            else
                                FD_1_1_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_1.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_1.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_2
				PropertyList FD_1_2_propertylist = FD_1_2.getPropertyList();
				if (FD_1_2_propertylist != null) {
                    for (Property FD_1_2_property : FD_1_2_propertylist.values()) {
                        if (FD_1_2_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_2_property.setValue(valueOne);
                            else
                                FD_1_2_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_2.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_2.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_3
				PropertyList FD_1_3_propertylist = FD_1_3.getPropertyList();
				if (FD_1_3_propertylist != null) {
                    for (Property FD_1_3_property : FD_1_3_propertylist.values()) {
                        if (FD_1_3_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_3_property.setValue(valueOne);
                            else
                                FD_1_3_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_3.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_3.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_4
				PropertyList FD_1_4_propertylist = FD_1_4.getPropertyList();
				if (FD_1_4_propertylist != null) {
                    for (Property FD_1_4_property : FD_1_4_propertylist.values()) {
                        if (FD_1_4_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_4_property.setValue(valueOne);
                            else
                                FD_1_4_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_4.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_4.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_5
				PropertyList FD_1_5_propertylist = FD_1_5.getPropertyList();
				if (FD_1_5_propertylist != null) {
                    for (Property FD_1_5_property : FD_1_5_propertylist.values()) {
                        if (FD_1_5_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_5_property.setValue(valueOne);
                            else
                                FD_1_5_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_5.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_5.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_6
				PropertyList FD_1_6_propertylist = FD_1_6.getPropertyList();
				if (FD_1_6_propertylist != null) {
                    for (Property FD_1_6_property : FD_1_6_propertylist.values()) {
                        if (FD_1_6_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_6_property.setValue(valueOne);
                            else
                                FD_1_6_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_6.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_6.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_7
				PropertyList FD_1_7_propertylist = FD_1_7.getPropertyList();
				if (FD_1_7_propertylist != null) {
                    for (Property FD_1_7_property : FD_1_7_propertylist.values()) {
                        if (FD_1_7_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_7_property.setValue(valueOne);
                            else
                                FD_1_7_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_7.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_7.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_8
				PropertyList FD_1_8_propertylist = FD_1_8.getPropertyList();
				if (FD_1_8_propertylist != null) {
                    for (Property FD_1_8_property : FD_1_8_propertylist.values()) {
                        if (FD_1_8_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_8_property.setValue(valueOne);
                            else
                                FD_1_8_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_8.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_8.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_9
				PropertyList FD_1_9_propertylist = FD_1_9.getPropertyList();
				if (FD_1_9_propertylist != null) {
                    for (Property FD_1_9_property : FD_1_9_propertylist.values()) {
                        if (FD_1_9_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_9_property.setValue(valueOne);
                            else
                                FD_1_9_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_9.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_9.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_10
				PropertyList FD_1_10_propertylist = FD_1_10.getPropertyList();
				if (FD_1_10_propertylist != null) {
                    for (Property FD_1_10_property : FD_1_10_propertylist.values()) {
                        if (FD_1_10_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_10_property.setValue(valueOne);
                            else
                                FD_1_10_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_10.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_10.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_11
				PropertyList FD_1_11_propertylist = FD_1_11.getPropertyList();
				if (FD_1_11_propertylist != null) {
                    for (Property FD_1_11_property : FD_1_11_propertylist.values()) {
                        if (FD_1_11_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_11_property.setValue(valueOne);
                            else
                                FD_1_11_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_11.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_11.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_12
				PropertyList FD_1_12_propertylist = FD_1_12.getPropertyList();
				if (FD_1_12_propertylist != null) {
                    for (Property FD_1_12_property : FD_1_12_propertylist.values()) {
                        if (FD_1_12_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_12_property.setValue(valueOne);
                            else
                                FD_1_12_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_12.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_12.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_13
				PropertyList FD_1_13_propertylist = FD_1_13.getPropertyList();
				if (FD_1_13_propertylist != null) {
                    for (Property FD_1_13_property : FD_1_13_propertylist.values()) {
                        if (FD_1_13_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_13_property.setValue(valueOne);
                            else
                                FD_1_13_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_13.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_13.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_14
				PropertyList FD_1_14_propertylist = FD_1_14.getPropertyList();
				if (FD_1_14_propertylist != null) {
                    for (Property FD_1_14_property : FD_1_14_propertylist.values()) {
                        if (FD_1_14_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_14_property.setValue(valueOne);
                            else
                                FD_1_14_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_14.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_14.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_15
				PropertyList FD_1_15_propertylist = FD_1_15.getPropertyList();
				if (FD_1_15_propertylist != null) {
                    for (Property FD_1_15_property : FD_1_15_propertylist.values()) {
                        if (FD_1_15_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_15_property.setValue(valueOne);
                            else
                                FD_1_15_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_15.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_15.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
                }
              //Set INIT Property for FD_1_16
				PropertyList FD_1_16_propertylist = FD_1_16.getPropertyList();
				if (FD_1_16_propertylist != null) {
                    for (Property FD_1_16_property : FD_1_16_propertylist.values()) {
                        if (FD_1_16_property.getName().equals("INIT")) {
                            isInit = true;
                            if (((INIT >> initCount++) & 1) == 1)
                                FD_1_16_property.setValue(valueOne);
                            else
                                FD_1_16_property.setValue(valueZero);
                        }
                    }
                }
                if (!isInit) {
                    if (((INIT >> initCount++) & 1) == 1)
                        FD_1_16.addProperty(new Property("INIT", (EdifTypedValue) valueOne));
                    else
                        FD_1_16.addProperty(new Property("INIT", (EdifTypedValue) valueZero));
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