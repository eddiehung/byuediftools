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
 * Generates the EDIF file for RAM128X1S_1
 * Includes: 128 FDE_1s, 1 128-1 MUXs(79 LUT3s, 32 MUXF5s, and 16 MUXF6s for each), 1 7-128 decoder(2 LUT2s, 144 LUT4s).
 * 
 * @author Yubo Li
 *
 */

public class RAM128X1S_1_replacement{
	public static void main(String args[]){
		
		String outputFileName = "RAM128X1S_1_replacement.edf";
		
		try{
			/******Environment and Environment manager******/
			EdifEnvironment topLevel = new EdifEnvironment("RAM128X1S_1_replacement");
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
				EdifCell RAM128X1S_1_replacement = new EdifCell(work, "RAM128X1S_1_replacement");
				RAM128X1S_1_replacement.addPort("WE", 1, 1);
				RAM128X1S_1_replacement.addPort("D", 1, 1);
				RAM128X1S_1_replacement.addPort("WCLK", 1, 1);
				RAM128X1S_1_replacement.addPort("A0", 1, 1);
				RAM128X1S_1_replacement.addPort("A1", 1, 1);
				RAM128X1S_1_replacement.addPort("A2", 1, 1);
				RAM128X1S_1_replacement.addPort("A3", 1, 1);
				RAM128X1S_1_replacement.addPort("A4", 1, 1);
				RAM128X1S_1_replacement.addPort("A5", 1, 1);
				RAM128X1S_1_replacement.addPort("A6", 1, 1);
				RAM128X1S_1_replacement.addPort("O", 1, 2);
				
				/******Design******/
				EdifDesign design = new EdifDesign("RAM128X1S_1_replacement");
				topLevel.setTopDesign(design);
				topLevel.setTopCell(RAM128X1S_1_replacement);
				
				/******Nets******/
				//Constants
				int WIRENUM = 128;
				int NETNUM = 144;
				int ENUM = 128;
				//Declaration of input and output ports
				EdifNet wclk_c = new EdifNet("wclk_c", RAM128X1S_1_replacement);
				EdifNet wclk = new EdifNet("wclk", RAM128X1S_1_replacement);
				EdifNet d_c = new EdifNet("d_c", RAM128X1S_1_replacement);
				EdifNet d = new EdifNet("d", RAM128X1S_1_replacement);
				EdifNet we_c = new EdifNet("we_c", RAM128X1S_1_replacement);
				EdifNet we = new EdifNet("we", RAM128X1S_1_replacement);
				EdifNet a0_c = new EdifNet("a0_c", RAM128X1S_1_replacement);
				EdifNet a0 = new EdifNet("a0", RAM128X1S_1_replacement);
				EdifNet a1_c = new EdifNet("a1_c", RAM128X1S_1_replacement);
				EdifNet a1 = new EdifNet("a1", RAM128X1S_1_replacement);
				EdifNet a2_c = new EdifNet("a2_c", RAM128X1S_1_replacement);
				EdifNet a2 = new EdifNet("a2", RAM128X1S_1_replacement);
				EdifNet a3_c = new EdifNet("a3_c", RAM128X1S_1_replacement);
				EdifNet a3 = new EdifNet("a3", RAM128X1S_1_replacement);
				EdifNet a4_c = new EdifNet("a4_c", RAM128X1S_1_replacement);
				EdifNet a4 = new EdifNet("a4", RAM128X1S_1_replacement);
				EdifNet a5_c = new EdifNet("a5_c", RAM128X1S_1_replacement);
				EdifNet a5 = new EdifNet("a5", RAM128X1S_1_replacement);
				EdifNet a6_c = new EdifNet("a6_c", RAM128X1S_1_replacement);
				EdifNet a6 = new EdifNet("a6", RAM128X1S_1_replacement);
				EdifNet o_c = new EdifNet("o_c", RAM128X1S_1_replacement);
				EdifNet o = new EdifNet("o", RAM128X1S_1_replacement);
				//Declaration of wires, which are the outputs of FDE_1s
				EdifNet[] wire = new EdifNet[WIRENUM];
				for(int i=0; i<WIRENUM; i++)
				{
					String wirename;
					wirename = "wire" + String.valueOf(i);
					wire[i] = new EdifNet(wirename, RAM128X1S_1_replacement);
				}
				//Declaration of nets, which are used in MUX and decoder
				EdifNet[] net = new EdifNet[NETNUM];
				for(int i=0; i<NETNUM; i++)
				{
					String netname;
					netname = "net" + String.valueOf(i);
					net[i] = new EdifNet(netname, RAM128X1S_1_replacement);
				}
				//Declaration of enables
				EdifNet[] e = new EdifNet[ENUM];
				for(int i=0; i<ENUM; i++)
				{
					String ename;
					ename = "e" + String.valueOf(i);
					e[i] = new EdifNet(ename, RAM128X1S_1_replacement);
				}
				//Add all nets to the top level design
				RAM128X1S_1_replacement.addNet(wclk_c);
				RAM128X1S_1_replacement.addNet(wclk);
				RAM128X1S_1_replacement.addNet(d_c);
				RAM128X1S_1_replacement.addNet(d);
				RAM128X1S_1_replacement.addNet(we_c);
				RAM128X1S_1_replacement.addNet(we);
				RAM128X1S_1_replacement.addNet(a0_c);
				RAM128X1S_1_replacement.addNet(a0);
				RAM128X1S_1_replacement.addNet(a1_c);
				RAM128X1S_1_replacement.addNet(a1);
				RAM128X1S_1_replacement.addNet(a2_c);
				RAM128X1S_1_replacement.addNet(a2);
				RAM128X1S_1_replacement.addNet(a3_c);
				RAM128X1S_1_replacement.addNet(a3);
				RAM128X1S_1_replacement.addNet(a4_c);
				RAM128X1S_1_replacement.addNet(a4);
				RAM128X1S_1_replacement.addNet(a5_c);
				RAM128X1S_1_replacement.addNet(a5);
				RAM128X1S_1_replacement.addNet(a6_c);
				RAM128X1S_1_replacement.addNet(a6);
				RAM128X1S_1_replacement.addNet(o_c);
				RAM128X1S_1_replacement.addNet(o);
				for(int i=0; i<WIRENUM; i++)
				{
					RAM128X1S_1_replacement.addNet(wire[i]);
				}
				for(int i=0; i<NETNUM; i++)
				{
					RAM128X1S_1_replacement.addNet(net[i]);
				}
				for(int i=0; i<ENUM; i++)
				{
					RAM128X1S_1_replacement.addNet(e[i]);
				}
				
				/******Instances******/
				//Constants
				int FDE_1NUM = 128;
				int LUT2NUM = 2;
				int LUT3NUM = 79;
				int LUT4NUM = 144;
				int MUXF5NUM = 32;
				int MUXF6NUM = 16;
				//Buffers for input and output ports
				EdifCellInstance WE_ibuf = new EdifCellInstance("WE_ibuf", RAM128X1S_1_replacement, IBUF);
				EdifCellInstance D_ibuf = new EdifCellInstance("D_ibuf", RAM128X1S_1_replacement, IBUF);
				EdifCellInstance WCLK_ibuf = new EdifCellInstance("WCLK_ibuf", RAM128X1S_1_replacement, BUFGP);
				EdifCellInstance A0_ibuf = new EdifCellInstance("A0_ibuf", RAM128X1S_1_replacement, IBUF);
				EdifCellInstance A1_ibuf = new EdifCellInstance("A1_ibuf", RAM128X1S_1_replacement, IBUF);
				EdifCellInstance A2_ibuf = new EdifCellInstance("A2_ibuf", RAM128X1S_1_replacement, IBUF);
				EdifCellInstance A3_ibuf = new EdifCellInstance("A3_ibuf", RAM128X1S_1_replacement, IBUF);
				EdifCellInstance A4_ibuf = new EdifCellInstance("A4_ibuf", RAM128X1S_1_replacement, IBUF);
				EdifCellInstance A5_ibuf = new EdifCellInstance("A5_ibuf", RAM128X1S_1_replacement, IBUF);
				EdifCellInstance A6_ibuf = new EdifCellInstance("A6_ibuf", RAM128X1S_1_replacement, IBUF);
				EdifCellInstance O_obuf = new EdifCellInstance("O_obuf", RAM128X1S_1_replacement, OBUF);
				//FDE_1s
				EdifCellInstance[] FDE_1_ = new EdifCellInstance[FDE_1NUM];
				for(int i=0; i<FDE_1NUM; i++)
				{
					String fdename;
					fdename = "FDE_1_" + String.valueOf(i);
					FDE_1_[i] = new EdifCellInstance(fdename, RAM128X1S_1_replacement, FDE_1);
				}
				//LUT2s
				EdifCellInstance[] LUT2_ = new EdifCellInstance[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					String lut2name;
					lut2name = "LUT2_" + String.valueOf(i);
					LUT2_[i] = new EdifCellInstance(lut2name, RAM128X1S_1_replacement, LUT2);
				}
				//LUT3s
				EdifCellInstance[] LUT3_ = new EdifCellInstance[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					String lut3name;
					lut3name = "LUT3_" + String.valueOf(i);
					LUT3_[i] = new EdifCellInstance(lut3name, RAM128X1S_1_replacement, LUT3);
				}
				//LUT4s
				EdifCellInstance[] LUT4_ = new EdifCellInstance[LUT4NUM];
				for(int i=0; i<LUT4NUM; i++)
				{
					String lut4name;
					lut4name = "LUT4_" + String.valueOf(i);
					LUT4_[i] = new EdifCellInstance(lut4name, RAM128X1S_1_replacement, LUT4);
				}
				//MUXF5
				EdifCellInstance[] MUXF5_ = new EdifCellInstance[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					String muxf5name;
					muxf5name = "MUXF5_" + String.valueOf(i);
					MUXF5_[i] = new EdifCellInstance(muxf5name, RAM128X1S_1_replacement, MUXF5);
				}
				//MUXF6
				EdifCellInstance[] MUXF6_ = new EdifCellInstance[MUXF6NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					String muxf6name;
					muxf6name = "MUXF6_" + String.valueOf(i);
					MUXF6_[i] = new EdifCellInstance(muxf6name, RAM128X1S_1_replacement, MUXF6);
				}
				//Add all instances to the top level design
				RAM128X1S_1_replacement.addSubCell(WE_ibuf);
				RAM128X1S_1_replacement.addSubCell(D_ibuf);
				RAM128X1S_1_replacement.addSubCell(WCLK_ibuf);
				RAM128X1S_1_replacement.addSubCell(A0_ibuf);
				RAM128X1S_1_replacement.addSubCell(A1_ibuf);
				RAM128X1S_1_replacement.addSubCell(A2_ibuf);
				RAM128X1S_1_replacement.addSubCell(A3_ibuf);
				RAM128X1S_1_replacement.addSubCell(A4_ibuf);
				RAM128X1S_1_replacement.addSubCell(A5_ibuf);
				RAM128X1S_1_replacement.addSubCell(A6_ibuf);
				RAM128X1S_1_replacement.addSubCell(O_obuf);
				for(int i=0; i<FDE_1NUM; i++)
				{
					RAM128X1S_1_replacement.addSubCell(FDE_1_[i]);
				}
				for(int i=0; i<LUT2NUM; i++)
				{
					RAM128X1S_1_replacement.addSubCell(LUT2_[i]);
				}
				for(int i=0; i<LUT3NUM; i++)
				{
					RAM128X1S_1_replacement.addSubCell(LUT3_[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					RAM128X1S_1_replacement.addSubCell(LUT4_[i]);
				}
				for(int i=0; i<MUXF5NUM; i++)
				{
					RAM128X1S_1_replacement.addSubCell(MUXF5_[i]);
				}
				for(int i=0; i<MUXF6NUM; i++)
				{
					RAM128X1S_1_replacement.addSubCell(MUXF6_[i]);
				}
				
				/******Interface******/
				//Interface of buffers
				EdifCellInterface RAM128X1S_1_replacement_interface = new EdifCellInterface(RAM128X1S_1_replacement);
				EdifCellInterface WE_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface WCLK_ibuf_interface = new EdifCellInterface(BUFGP);
				EdifCellInterface A0_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A1_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A2_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A3_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A4_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A5_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A6_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface O_obuf_interface = new EdifCellInterface(OBUF);
				//Interface of FDE_1s
				EdifCellInterface FDE_1Interface[] = new EdifCellInterface[FDE_1NUM];
				for(int i=0; i<FDE_1NUM; i++)
				{
					FDE_1Interface[i] = new EdifCellInterface(FDE_1);
				}
				//Interface of LUT2s
				EdifCellInterface LUT2Interface[] = new EdifCellInterface[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					LUT2Interface[i] = new EdifCellInterface(LUT2);
				}
				//Interface of LUT3s
				EdifCellInterface LUT3Interface[] = new EdifCellInterface[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					LUT3Interface[i] = new EdifCellInterface(LUT3);
				}
				//Interface of LUT4s
				EdifCellInterface LUT4Interface[] = new EdifCellInterface[LUT4NUM];
				for(int i=0; i<LUT4NUM; i++)
				{
					LUT4Interface[i] = new EdifCellInterface(LUT4);
				}
				//Interface of MUXF5s
				EdifCellInterface MUXF5Interface[] = new EdifCellInterface[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					MUXF5Interface[i] = new EdifCellInterface(MUXF5);
				}
				//Interface of MUXF6s
				EdifCellInterface MUXF6Interface[] = new EdifCellInterface[MUXF6NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					MUXF6Interface[i] = new EdifCellInterface(MUXF6);
				}
				
				/******Ports******/
				//RAM128X1S_1_replacement
				EdifPort RAM128X1S_1_replacement_interface_WE = new EdifPort(RAM128X1S_1_replacement_interface, "WE", 1, 1);
				EdifPort RAM128X1S_1_replacement_interface_D = new EdifPort(RAM128X1S_1_replacement_interface, "D", 1, 1);
				EdifPort RAM128X1S_1_replacement_interface_WCLK = new EdifPort(RAM128X1S_1_replacement_interface, "WCLK", 1, 1);
				EdifPort RAM128X1S_1_replacement_interface_A0 = new EdifPort(RAM128X1S_1_replacement_interface, "A0", 1, 1);
				EdifPort RAM128X1S_1_replacement_interface_A1 = new EdifPort(RAM128X1S_1_replacement_interface, "A1", 1, 1);
				EdifPort RAM128X1S_1_replacement_interface_A2 = new EdifPort(RAM128X1S_1_replacement_interface, "A2", 1, 1);
				EdifPort RAM128X1S_1_replacement_interface_A3 = new EdifPort(RAM128X1S_1_replacement_interface, "A3", 1, 1);
				EdifPort RAM128X1S_1_replacement_interface_A4 = new EdifPort(RAM128X1S_1_replacement_interface, "A4", 1, 1);
				EdifPort RAM128X1S_1_replacement_interface_A5 = new EdifPort(RAM128X1S_1_replacement_interface, "A5", 1, 1);
				EdifPort RAM128X1S_1_replacement_interface_A6 = new EdifPort(RAM128X1S_1_replacement_interface, "A6", 1, 1);
				EdifPort RAM128X1S_1_replacement_interface_O = new EdifPort(RAM128X1S_1_replacement_interface, "O", 1, 2);
				RAM128X1S_1_replacement_interface.addPort("WE", 1, 1);
				RAM128X1S_1_replacement_interface.addPort("D", 1, 1);
				RAM128X1S_1_replacement_interface.addPort("WCLK", 1, 1);
				RAM128X1S_1_replacement_interface.addPort("A0", 1, 1);
				RAM128X1S_1_replacement_interface.addPort("A1", 1, 1);
				RAM128X1S_1_replacement_interface.addPort("A2", 1, 1);
				RAM128X1S_1_replacement_interface.addPort("A3", 1, 1);
				RAM128X1S_1_replacement_interface.addPort("A4", 1, 1);
				RAM128X1S_1_replacement_interface.addPort("A5", 1, 1);
				RAM128X1S_1_replacement_interface.addPort("A6", 1, 1);
				RAM128X1S_1_replacement_interface.addPort("O", 1, 2);
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
				//A4_ibuf
				EdifPort A4_ibuf_interface_I = new EdifPort(A4_ibuf_interface, "I", 1, 1);
				EdifPort A4_ibuf_interface_O = new EdifPort(A4_ibuf_interface, "O", 1, 2);
				A4_ibuf_interface.addPort("I", 1, 1);
				A4_ibuf_interface.addPort("O", 1, 2);
				//A5_ibuf
				EdifPort A5_ibuf_interface_I = new EdifPort(A5_ibuf_interface, "I", 1, 1);
				EdifPort A5_ibuf_interface_O = new EdifPort(A5_ibuf_interface, "O", 1, 2);
				A5_ibuf_interface.addPort("I", 1, 1);
				A5_ibuf_interface.addPort("O", 1, 2);
				//A6_ibuf
				EdifPort A6_ibuf_interface_I = new EdifPort(A6_ibuf_interface, "I", 1, 1);
				EdifPort A6_ibuf_interface_O = new EdifPort(A6_ibuf_interface, "O", 1, 2);
				A6_ibuf_interface.addPort("I", 1, 1);
				A6_ibuf_interface.addPort("O", 1, 2);
				//O_obuf
				EdifPort O_obuf_interface_I = new EdifPort(O_obuf_interface, "I", 1, 1);
				EdifPort O_obuf_interface_O = new EdifPort(O_obuf_interface, "O", 1, 2);
				O_obuf_interface.addPort("I", 1, 1);
				O_obuf_interface.addPort("O", 1, 2);
				//FDE_1s
				EdifPort[] FDE_1Interface_C= new EdifPort[FDE_1NUM];
				EdifPort[] FDE_1Interface_D= new EdifPort[FDE_1NUM];
				EdifPort[] FDE_1Interface_CE= new EdifPort[FDE_1NUM];
				EdifPort[] FDE_1Interface_Q= new EdifPort[FDE_1NUM];
				for(int i=0; i<FDE_1NUM; i++)
				{
					FDE_1Interface_C[i] = new EdifPort(FDE_1Interface[i], "C", 1, 1);
					FDE_1Interface_D[i] = new EdifPort(FDE_1Interface[i], "D", 1, 1);
					FDE_1Interface_CE[i] = new EdifPort(FDE_1Interface[i], "CE", 1, 1);
					FDE_1Interface_Q[i] = new EdifPort(FDE_1Interface[i], "Q", 1, 2);
					FDE_1Interface[i].addPort("C", 1, 1);
					FDE_1Interface[i].addPort("D", 1, 1);
					FDE_1Interface[i].addPort("CE", 1, 1);
					FDE_1Interface[i].addPort("Q", 1, 2);
				}
				//LUT2s
				EdifPort[] LUT2Interface_I0 = new EdifPort[LUT2NUM];
				EdifPort[] LUT2Interface_I1 = new EdifPort[LUT2NUM];
				EdifPort[] LUT2Interface_O = new EdifPort[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					LUT2Interface_I0[i] = new EdifPort(LUT2Interface[i], "I0", 1, 1);
					LUT2Interface_I1[i] = new EdifPort(LUT2Interface[i], "I1", 1, 1);
					LUT2Interface_O[i] = new EdifPort(LUT2Interface[i], "O", 1, 2);
					LUT2Interface[i].addPort("I0", 1, 1);
					LUT2Interface[i].addPort("I1", 1, 1);
					LUT2Interface[i].addPort("O", 1, 2);
				}
				//LUT3s
				EdifPort[] LUT3Interface_I0 = new EdifPort[LUT3NUM];
				EdifPort[] LUT3Interface_I1 = new EdifPort[LUT3NUM];
				EdifPort[] LUT3Interface_I2 = new EdifPort[LUT3NUM];
				EdifPort[] LUT3Interface_O = new EdifPort[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					LUT3Interface_I0[i] = new EdifPort(LUT3Interface[i], "I0", 1, 1);
					LUT3Interface_I1[i] = new EdifPort(LUT3Interface[i], "I1", 1, 1);
					LUT3Interface_I2[i] = new EdifPort(LUT3Interface[i], "I2", 1, 1);
					LUT3Interface_O[i] = new EdifPort(LUT3Interface[i], "O", 1, 2);
					LUT3Interface[i].addPort("I0", 1, 1);
					LUT3Interface[i].addPort("I1", 1, 1);
					LUT3Interface[i].addPort("I2", 1, 1);
					LUT3Interface[i].addPort("O", 1, 2);
				}
				//LUT4s
				EdifPort[] LUT4Interface_I0 = new EdifPort[LUT4NUM];
				EdifPort[] LUT4Interface_I1 = new EdifPort[LUT4NUM];
				EdifPort[] LUT4Interface_I2 = new EdifPort[LUT4NUM];
				EdifPort[] LUT4Interface_I3 = new EdifPort[LUT4NUM];
				EdifPort[] LUT4Interface_O = new EdifPort[LUT4NUM];
				for(int i=0; i<LUT4NUM; i++)
				{
					LUT4Interface_I0[i] = new EdifPort(LUT4Interface[i], "I0", 1, 1);
					LUT4Interface_I1[i] = new EdifPort(LUT4Interface[i], "I1", 1, 1);
					LUT4Interface_I2[i] = new EdifPort(LUT4Interface[i], "I2", 1, 1);
					LUT4Interface_I3[i] = new EdifPort(LUT4Interface[i], "I3", 1, 1);
					LUT4Interface_O[i] = new EdifPort(LUT4Interface[i], "O", 1, 2);
					LUT4Interface[i].addPort("I0", 1, 1);
					LUT4Interface[i].addPort("I1", 1, 1);
					LUT4Interface[i].addPort("I2", 1, 1);
					LUT4Interface[i].addPort("I3", 1, 1);
					LUT4Interface[i].addPort("O", 1, 2);
				}
				//MUXF5s
				EdifPort[] MUXF5Interface_I0 = new EdifPort[MUXF5NUM];
				EdifPort[] MUXF5Interface_I1 = new EdifPort[MUXF5NUM];
				EdifPort[] MUXF5Interface_S = new EdifPort[MUXF5NUM];
				EdifPort[] MUXF5Interface_O = new EdifPort[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					MUXF5Interface_I0[i] = new EdifPort(MUXF5Interface[i], "I0", 1, 1);
					MUXF5Interface_I1[i] = new EdifPort(MUXF5Interface[i], "I1", 1, 1);
					MUXF5Interface_S[i] = new EdifPort(MUXF5Interface[i], "S", 1, 1);
					MUXF5Interface_O[i] = new EdifPort(MUXF5Interface[i], "O", 1, 2);
					MUXF5Interface[i].addPort("I0", 1, 1);
					MUXF5Interface[i].addPort("I1", 1, 1);
					MUXF5Interface[i].addPort("S", 1, 1);
					MUXF5Interface[i].addPort("O", 1, 2);
				}
				//MUXF6s
				EdifPort[] MUXF6Interface_I0 = new EdifPort[MUXF6NUM];
				EdifPort[] MUXF6Interface_I1 = new EdifPort[MUXF6NUM];
				EdifPort[] MUXF6Interface_S = new EdifPort[MUXF6NUM];
				EdifPort[] MUXF6Interface_O = new EdifPort[MUXF6NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					MUXF6Interface_I0[i] = new EdifPort(MUXF6Interface[i], "I0", 1, 1);
					MUXF6Interface_I1[i] = new EdifPort(MUXF6Interface[i], "I1", 1, 1);
					MUXF6Interface_S[i] = new EdifPort(MUXF6Interface[i], "S", 1, 1);
					MUXF6Interface_O[i] = new EdifPort(MUXF6Interface[i], "O", 1, 2);
					MUXF6Interface[i].addPort("I0", 1, 1);
					MUXF6Interface[i].addPort("I1", 1, 1);
					MUXF6Interface[i].addPort("S", 1, 1);
					MUXF6Interface[i].addPort("O", 1, 2);
				}
				
				/******PortRefs******/
				//Port reference of input and output ports
				EdifPortRef RAM128X1S_1_replacement_WE = new EdifPortRef(we, RAM128X1S_1_replacement_interface_WE.getSingleBitPort(0), null);
				EdifPortRef RAM128X1S_1_replacement_D = new EdifPortRef(d, RAM128X1S_1_replacement_interface_D.getSingleBitPort(0), null);
				EdifPortRef RAM128X1S_1_replacement_WCLK = new EdifPortRef(wclk, RAM128X1S_1_replacement_interface_WCLK.getSingleBitPort(0), null);
				EdifPortRef RAM128X1S_1_replacement_A0 = new EdifPortRef(a0, RAM128X1S_1_replacement_interface_A0.getSingleBitPort(0), null);
				EdifPortRef RAM128X1S_1_replacement_A1 = new EdifPortRef(a1, RAM128X1S_1_replacement_interface_A1.getSingleBitPort(0), null);
				EdifPortRef RAM128X1S_1_replacement_A2 = new EdifPortRef(a2, RAM128X1S_1_replacement_interface_A2.getSingleBitPort(0), null);
				EdifPortRef RAM128X1S_1_replacement_A3 = new EdifPortRef(a3, RAM128X1S_1_replacement_interface_A3.getSingleBitPort(0), null);
				EdifPortRef RAM128X1S_1_replacement_A4 = new EdifPortRef(a4, RAM128X1S_1_replacement_interface_A4.getSingleBitPort(0), null);
				EdifPortRef RAM128X1S_1_replacement_A5 = new EdifPortRef(a5, RAM128X1S_1_replacement_interface_A5.getSingleBitPort(0), null);
				EdifPortRef RAM128X1S_1_replacement_A6 = new EdifPortRef(a6, RAM128X1S_1_replacement_interface_A6.getSingleBitPort(0), null);
				EdifPortRef RAM128X1S_1_replacement_O = new EdifPortRef(o, RAM128X1S_1_replacement_interface_O.getSingleBitPort(0), null);
				//Port reference of buffers
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
				EdifPortRef A4_ibuf_I = new EdifPortRef(a4, A4_ibuf_interface_I.getSingleBitPort(0), A4_ibuf);
				EdifPortRef A4_ibuf_O = new EdifPortRef(a4_c, A4_ibuf_interface_O.getSingleBitPort(0), A4_ibuf);
				EdifPortRef A5_ibuf_I = new EdifPortRef(a5, A5_ibuf_interface_I.getSingleBitPort(0), A5_ibuf);
				EdifPortRef A5_ibuf_O = new EdifPortRef(a5_c, A5_ibuf_interface_O.getSingleBitPort(0), A5_ibuf);
				EdifPortRef A6_ibuf_I = new EdifPortRef(a6, A6_ibuf_interface_I.getSingleBitPort(0), A6_ibuf);
				EdifPortRef A6_ibuf_O = new EdifPortRef(a6_c, A6_ibuf_interface_O.getSingleBitPort(0), A6_ibuf);
				EdifPortRef O_obuf_I = new EdifPortRef(o_c, O_obuf_interface_I.getSingleBitPort(0), O_obuf);
				EdifPortRef O_obuf_O = new EdifPortRef(o, O_obuf_interface_O.getSingleBitPort(0), O_obuf);
				//Port reference of FDE_1s
				EdifPortRef[] FDE_1_C = new EdifPortRef[FDE_1NUM];
				EdifPortRef[] FDE_1_D = new EdifPortRef[FDE_1NUM];
				EdifPortRef[] FDE_1_CE = new EdifPortRef[FDE_1NUM];
				EdifPortRef[] FDE_1_Q = new EdifPortRef[FDE_1NUM];
				for(int i=0; i<FDE_1NUM; i++)
				{
					int q = i%128;
					FDE_1_C[i] = new EdifPortRef(wclk_c, FDE_1Interface_C[i].getSingleBitPort(0), FDE_1_[i]);
					FDE_1_CE[i] = new EdifPortRef(e[q], FDE_1Interface_CE[i].getSingleBitPort(0), FDE_1_[i]);
					FDE_1_Q[i] = new EdifPortRef(wire[i], FDE_1Interface_Q[i].getSingleBitPort(0), FDE_1_[i]);
					FDE_1_D[i] = new EdifPortRef(d_c, FDE_1Interface_D[i].getSingleBitPort(0), FDE_1_[i]);
				}
				//Port reference of LUT2s
				EdifPortRef[] LUT2_I0 = new EdifPortRef[LUT2NUM];
				EdifPortRef[] LUT2_I1 = new EdifPortRef[LUT2NUM];
				EdifPortRef[] LUT2_O = new EdifPortRef[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
						LUT2_I0[i] = new EdifPortRef(a6_c, LUT2Interface_I0[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_I1[i] = new EdifPortRef(we_c, LUT2Interface_I1[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_O[i] = new EdifPortRef(net[i+126], LUT2Interface_O[i].getSingleBitPort(0), LUT2_[i]);
				}
				//Port reference of LUT3s
				EdifPortRef[] LUT3_I0 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_I1 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_I2 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_O = new EdifPortRef[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					int a = i/79;
					int q = i%79;
					if(q<64)
					{
						int wireNumber = 0;
						int lutNumber = i%64;
						int divider = 32;
						int addition = 1;
						for(int j=0; j<6; j++)
						{
							if(lutNumber/divider==1)
							{
								wireNumber += addition;
								lutNumber -= divider;
							}
							divider /= 2;
							addition *= 2;
						}
						LUT3_I0[i] = new EdifPortRef(a6_c, LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_I1[i] = new EdifPortRef(wire[wireNumber+128*a], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_I2[i] = new EdifPortRef(wire[wireNumber+64+128*a], LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_O[i] = new EdifPortRef(net[q+126*a], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
					}
					else
					{
						LUT3_I0[i] = new EdifPortRef(net[126*a+2*q-32], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_I1[i] = new EdifPortRef(net[126*a+2*q-31], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
						if(q>=64 && q<=71)
						{
							LUT3_I2[i] = new EdifPortRef(a3_c, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[126*a+48], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else if(q>=72 && q<=75)
						{
							LUT3_I2[i] = new EdifPortRef(a2_c, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[126*a+48], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else if(q==76 || q==77)
						{
							LUT3_I2[i] = new EdifPortRef(a1_c, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[126*a+48], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else
						{
							LUT3_I2[i] = new EdifPortRef(a0_c, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(o_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
					}
				}
				//Port reference of LUT4s
				EdifPortRef[] LUT4_I0 = new EdifPortRef[LUT4NUM];
				EdifPortRef[] LUT4_I1 = new EdifPortRef[LUT4NUM];
				EdifPortRef[] LUT4_I2 = new EdifPortRef[LUT4NUM];
				EdifPortRef[] LUT4_I3 = new EdifPortRef[LUT4NUM];
				EdifPortRef[] LUT4_O = new EdifPortRef[LUT4NUM];
				for(int i=0; i<LUT4NUM; i++)
				{
					if(i<16)
					{
						if(i<8)
							LUT4_I0[i] = new EdifPortRef(net[NETNUM-18], LUT4Interface_I0[i].getSingleBitPort(0), LUT4_[i]);
						else
							LUT4_I0[i] = new EdifPortRef(net[NETNUM-17], LUT4Interface_I0[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_I1[i] = new EdifPortRef(a3_c, LUT4Interface_I1[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_I2[i] = new EdifPortRef(a4_c, LUT4Interface_I2[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_I3[i] = new EdifPortRef(a5_c, LUT4Interface_I3[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_O[i] = new EdifPortRef(net[i+128], LUT4Interface_O[i].getSingleBitPort(0), LUT4_[i]);
					}
					else
					{
						int a = (i-16)/8;
						LUT4_I0[i] = new EdifPortRef(net[a+128], LUT4Interface_I0[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_I1[i] = new EdifPortRef(a0_c, LUT4Interface_I1[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_I2[i] = new EdifPortRef(a1_c, LUT4Interface_I2[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_I3[i] = new EdifPortRef(a2_c, LUT4Interface_I3[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_O[i] = new EdifPortRef(e[i-16], LUT4Interface_O[i].getSingleBitPort(0), LUT4_[i]);
					}
				}
				//Port reference of MUXF5s
				EdifPortRef[] MUXF5_I0 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF5_I1 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF5_S = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF5_O = new EdifPortRef[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					int q = i%32;
					int a = i/32;
					MUXF5_I0[i] = new EdifPortRef(net[2*q+126*a], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
					MUXF5_I1[i] = new EdifPortRef(net[2*q+1+126*a], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
					MUXF5_S[i] = new EdifPortRef(a5_c, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
					MUXF5_O[i] = new EdifPortRef(net[64+126*a], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
				}
				//Port reference of MUXF6s
				EdifPortRef[] MUXF6_I0 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_I1 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_S = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_O = new EdifPortRef[MUXF5NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					int a = i/16;
					int q = i%16;
					MUXF6_I0[i] = new EdifPortRef(net[2*q+64+126*a], MUXF6Interface_I0[i].getSingleBitPort(0), MUXF6_[i]);
					MUXF6_I1[i] = new EdifPortRef(net[2*q+64+126*a], MUXF6Interface_I1[i].getSingleBitPort(0), MUXF6_[i]);
					MUXF6_S[i] = new EdifPortRef(a4_c, MUXF6Interface_S[i].getSingleBitPort(0), MUXF6_[i]);
					MUXF6_O[i] = new EdifPortRef(net[96+126*a], MUXF6Interface_O[i].getSingleBitPort(0), MUXF6_[i]);
				}
				//we
				we.addPortConnection(RAM128X1S_1_replacement_WE);
				we.addPortConnection(WE_ibuf_I);
				//we_c
				we_c.addPortConnection(WE_ibuf_O);
				for(int i=0; i<2;i++)
				{
					we_c.addPortConnection(LUT2_I1[i]);
				}
				//d
				d.addPortConnection(RAM128X1S_1_replacement_D);
				d.addPortConnection(D_ibuf_I);
				//d_c
				d_c.addPortConnection(D_ibuf_O);
				for(int i=0; i<128; i++)
				{
					d_c.addPortConnection(FDE_1_D[i]);
				}
				//wclk
				wclk.addPortConnection(WCLK_ibuf_I);
				wclk.addPortConnection(RAM128X1S_1_replacement_WCLK);
				//wclk_c
				wclk_c.addPortConnection(WCLK_ibuf_O);
				for(int i=0; i<FDE_1NUM; i++)
				{
					wclk_c.addPortConnection(FDE_1_C[i]);
				}
				//a0
				a0.addPortConnection(RAM128X1S_1_replacement_A0);
				a0.addPortConnection(A0_ibuf_I);
				//a0_c
				a0_c.addPortConnection(A0_ibuf_O);
				for(int i=0; i<LUT3NUM; i++)
				{
					if((i%79)==78)
						a0_c.addPortConnection(LUT3_I2[i]);
				}
				for(int i=16; i<LUT4NUM; i++)
				{
					a0_c.addPortConnection(LUT4_I1[i]);
				}
				//a1
				a1.addPortConnection(RAM128X1S_1_replacement_A1);
				a1.addPortConnection(A1_ibuf_I);
				//a1_c
				a1_c.addPortConnection(A1_ibuf_O);
				for(int i=0; i<LUT3NUM; i++)
				{
					if((i%79)==76 || (i%79)==77)
						a1_c.addPortConnection(LUT3_I2[i]);
				}
				for(int i=16; i<LUT4NUM; i++)
				{
					a1_c.addPortConnection(LUT4_I2[i]);
				}
				//a2
				a2.addPortConnection(RAM128X1S_1_replacement_A2);
				a2.addPortConnection(A2_ibuf_I);
				//a2_c
				a2_c.addPortConnection(A2_ibuf_O);
				for(int i=0; i<LUT3NUM; i++)
				{
					if((i%79)>=72 && (i%79)<=75)
						a2_c.addPortConnection(LUT3_I2[i]);
				}
				for(int i=16; i<LUT4NUM; i++)
				{
					a2_c.addPortConnection(LUT4_I3[i]);
				}
				//a3
				a3.addPortConnection(RAM128X1S_1_replacement_A3);
				a3.addPortConnection(A3_ibuf_I);
				//a3_c
				a3_c.addPortConnection(A3_ibuf_O);
				for(int i=0; i<LUT3NUM; i++)
				{
					if((i%79)>=64 && (i%79)<=71)
						a3_c.addPortConnection(LUT3_I2[i]);
				}
				for(int i=0; i<16; i++)
				{
					a3_c.addPortConnection(LUT4_I1[i]);
				}
				//a4
				a4.addPortConnection(RAM128X1S_1_replacement_A4);
				a4.addPortConnection(A4_ibuf_I);
				//a4_c
				a4_c.addPortConnection(A4_ibuf_O);
				for(int i=0; i<MUXF6NUM; i++)
				{
					a4_c.addPortConnection(MUXF6_S[i]);
				}
				for(int i=0; i<16; i++)
				{
					a4_c.addPortConnection(LUT4_I2[i]);
				}
				//a5
				a5.addPortConnection(RAM128X1S_1_replacement_A5);
				a5.addPortConnection(A5_ibuf_I);
				//a5_c
				a5_c.addPortConnection(A5_ibuf_O);
				for(int i=0; i<MUXF5NUM; i++)
				{
					a5_c.addPortConnection(MUXF5_S[i]);
				}
				for(int i=0; i<16; i++)
				{
					a5_c.addPortConnection(LUT4_I3[i]);
				}
				//a6
				a6.addPortConnection(RAM128X1S_1_replacement_A6);
				a6.addPortConnection(A6_ibuf_I);
				//a6_c
				a6_c.addPortConnection(A6_ibuf_O);
				for(int i=0; i<LUT3NUM; i++)
				{
					if((i%79)<64)
						a6_c.addPortConnection(LUT3_I0[i]);
				}
				for(int i=0; i<LUT2NUM; i++)
				{
					a6_c.addPortConnection(LUT2_I0[i]);
				}
				//o
				o.addPortConnection(O_obuf_O);
				o.addPortConnection(RAM128X1S_1_replacement_O);
				//o_c
				o_c.addPortConnection(O_obuf_I);
				o_c.addPortConnection(LUT3_O[78]);
				//wires
				for(int i=0; i<WIRENUM; i++)
				{
					int lutNumber = 0;
					int value = 32;
					int addition = 1;
					int wireNumber = i%64;
					for(int j=0; j<6; j++)
					{
						if(wireNumber==value)
						{
							lutNumber += addition;
							break;
						}
						else if(wireNumber>value)
						{
							wireNumber -= value;
							lutNumber += addition;
						}
						addition *= 2;
						value /= 2;
					}				
					int a = i/128;
					int q = i%128;
					wire[i].addPortConnection(FDE_1_Q[i]);
					if(q<64)
					{
						wire[i].addPortConnection(LUT3_I1[a*79+lutNumber]);
					}
					else
					{
						wire[i].addPortConnection(LUT3_I2[a*79+lutNumber]);
					}
				}
				//e
				for(int i=0; i<ENUM; i++)
				{
					e[i].addPortConnection(LUT4_O[i+16]);
					e[i].addPortConnection(FDE_1_CE[i]);
				}
				//nets
				for(int i=0; i<NETNUM; i++)
				{
					int a = i/126;
					int q = i%126;
					if(i<NETNUM-18)	//nets in MUX
					{
						if(q<64)
						{
							net[i].addPortConnection(LUT3_O[q+79*a]);
							if((q%2)==0)
								net[i].addPortConnection(MUXF5_I0[q/2+32*a]);
							else
								net[i].addPortConnection(MUXF5_I1[q/2+32*a]);
						}
						else if(q<96)
						{
							net[i].addPortConnection(MUXF5_O[q-64+32*a]);
							if((q%2)==0)
								net[i].addPortConnection(MUXF6_I0[q/2-32+16*a]);
							else
								net[i].addPortConnection(MUXF6_I1[q/2-32+16*a]);
						}
						else if(q<112)
						{
							net[i].addPortConnection(MUXF6_O[q-96+16*a]);
							if((q%2)==0)
								net[i].addPortConnection(LUT3_I0[q/2+16+79*a]);
							else
								net[i].addPortConnection(LUT3_I1[q/2+16+79*a]);
						}
						else
						{
							net[i].addPortConnection(LUT3_O[q-48+79*a]);
							if((q%2)==0)
								net[i].addPortConnection(LUT3_I0[q/2+16+79*a]);
							else
								net[i].addPortConnection(LUT3_I1[q/2+16+79*a]);
						}
					}
					else			//nets in decoder
					{
						if(q<2)
						{
							net[i].addPortConnection(LUT2_O[q]);
							for(int j=0; j<8; j++)
								net[i].addPortConnection(LUT4_I0[q*8+j]);
						}
						else
						{
							net[i].addPortConnection(LUT4_O[q-2]);
							for(int j=0; j<8; j++)
								net[i].addPortConnection(LUT4_I0[q*8+j]);
						}
					}
				}
				
				/******Set INIT Property******/
				boolean isInit;
				int[] initCount = new int[1];
				long[] INIT = new long[1];
				initCount[0] = 0;
				INIT[0] = 12;
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
		        //Set INIT Property for LUT3s
		        PropertyList[] LUT3_propertylist = new PropertyList[LUT3NUM];
		        for(int i=0; i<LUT3NUM; i++)
		        {
		        	int q = i%79;
		        	isInit = false;
		        	LUT3_propertylist[i] = LUT3_[i].getPropertyList();
		        	if(LUT3_propertylist[i] != null)
		        	{
		        		for(Property LUT3_property : LUT3_propertylist[i].values())
		        		{
		        			if(LUT3_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if(q<64)
		        					LUT3_property.setValue(valueE4);
		        				else
		        					LUT3_property.setValue(valueCA);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if(q<64)
		        			LUT3_[i].addProperty(new Property("INIT", (EdifTypedValue)valueE4));
		        		else
		        			LUT3_[i].addProperty(new Property("INIT", (EdifTypedValue)valueCA));
		        	}
		        }
		        //Set INIT Property for LUT2s
		        PropertyList[] LUT2_propertylist = new PropertyList[LUT2NUM];
		        for(int i=0; i<LUT2NUM; i++)
		        {
		        	isInit = false;
		        	LUT2_propertylist[i] = LUT2_[i].getPropertyList();
		        	if(LUT2_propertylist[i] != null)
		        	{
		        		for(Property LUT2_property : LUT2_propertylist[i].values())
		        		{
		        			if(LUT2_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if(i==0)
		        					LUT2_property.setValue(value4);
		        				else
		        					LUT2_property.setValue(value8);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if(i==0)
		        			LUT2_[i].addProperty(new Property("INIT", (EdifTypedValue)value4));
		        		else
		        			LUT2_[i].addProperty(new Property("INIT", (EdifTypedValue)value8));
		        	}
		        }
		        //Set INIT Property for LUT4s
		        PropertyList[] LUT4_propertylist = new PropertyList[LUT4NUM];
		        for(int i=0; i<LUT4NUM; i++)
		        {
		        	int q = i%8;
		        	isInit = false;
		        	LUT4_propertylist[i] = LUT4_[i].getPropertyList();
		        	if(LUT4_propertylist[i] != null)
		        	{
		        		for(Property LUT4_property : LUT4_propertylist[i].values())
		        		{
		        			if(LUT4_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if(q==0)
		        					LUT4_property.setValue(value0002);
		        				else if(q==1)
		        					LUT4_property.setValue(value0008);
		        				else if(q==2)
		        					LUT4_property.setValue(value0020);
		        				else if(q==3)
		        					LUT4_property.setValue(value0080);
		        				else if(q==4)
		        					LUT4_property.setValue(value0200);
		        				else if(q==5)
		        					LUT4_property.setValue(value0800);
		        				else if(q==6)
		        					LUT4_property.setValue(value2000);
		        				else
		        					LUT4_property.setValue(value8000);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if(q==0)
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value0002));
		        		else if(q==1)
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value0008));
		        		else if(q==2)
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value0020));
		        		else if(q==3)
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value0080));
		        		else if(q==4)
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value0200));
		        		else if(q==5)
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value0800));
		        		else if(q==6)
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value2000));
		        		else
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value8000));
		        	}
		        }
		        //Set INIT Property for FDE_1s
		        PropertyList[] FDE_1_propertylist = new PropertyList[FDE_1NUM];
		        for(int i=0; i<FDE_1NUM; i++)
		        {
		        	int a = i/128;
		        	isInit = false;
		        	FDE_1_propertylist[i] = FDE_1_[i].getPropertyList();
		        	if(FDE_1_propertylist[i] != null)
		        	{
		        		for(Property FDE_1_property : FDE_1_propertylist[i].values())
		        		{
		        			if(FDE_1_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if (((INIT[a] >> initCount[a]++) & 1) == 1)
	                                FDE_1_property.setValue(valueOne);
	                            else
	                                FDE_1_property.setValue(valueZero);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if (((INIT[a] >> initCount[a]++) & 1) == 1)
                            FDE_1_[i].addProperty(new Property("INIT", (EdifTypedValue)valueOne));
                        else
                        	FDE_1_[i].addProperty(new Property("INIT", (EdifTypedValue)valueZero));
		        	}
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
	
	public static void Replace(EdifLibraryManager libManager, EdifCell RAM128X1S_1_replacement, long INIT_HIGH, long INIT_LOW,
			EdifNet we, EdifNet d, EdifNet wclk, EdifNet a0, EdifNet a1, EdifNet a2, EdifNet a3, EdifNet a4, EdifNet a5, EdifNet a6,
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
				//Constants
				int WIRENUM = 128;
				int NETNUM = 144;
				int ENUM = 128;
				//Declaration of wires, which are the outputs of FDE_1s
				EdifNet[] wire = new EdifNet[WIRENUM];
				for(int i=0; i<WIRENUM; i++)
				{
					String wirename;
					wirename = "wire" + String.valueOf(i);
					wire[i] = new EdifNet(wirename, RAM128X1S_1_replacement);
				}
				//Declaration of nets, which are used in MUX and decoder
				EdifNet[] net = new EdifNet[NETNUM];
				for(int i=0; i<NETNUM; i++)
				{
					String netname;
					netname = "net" + String.valueOf(i);
					net[i] = new EdifNet(netname, RAM128X1S_1_replacement);
				}
				//Declaration of enables
				EdifNet[] e = new EdifNet[ENUM];
				for(int i=0; i<ENUM; i++)
				{
					String ename;
					ename = "e" + String.valueOf(i);
					e[i] = new EdifNet(ename, RAM128X1S_1_replacement);
				}
				//Add all nets to the top level design
				for(int i=0; i<WIRENUM; i++)
				{
					RAM128X1S_1_replacement.addNet(wire[i]);
				}
				for(int i=0; i<NETNUM; i++)
				{
					RAM128X1S_1_replacement.addNet(net[i]);
				}
				for(int i=0; i<ENUM; i++)
				{
					RAM128X1S_1_replacement.addNet(e[i]);
				}
				
				/******Instances******/
				//Constants
				int FDE_1NUM = 128;
				int LUT2NUM = 2;
				int LUT3NUM = 79;
				int LUT4NUM = 144;
				int MUXF5NUM = 32;
				int MUXF6NUM = 16;
				//Buffers for input and output ports
				//FDE_1s
				EdifCellInstance[] FDE_1_ = new EdifCellInstance[FDE_1NUM];
				for(int i=0; i<FDE_1NUM; i++)
				{
					String fdename;
					fdename = "FDE_1_" + String.valueOf(i);
					FDE_1_[i] = new EdifCellInstance(fdename, RAM128X1S_1_replacement, FDE_1);
				}
				//LUT2s
				EdifCellInstance[] LUT2_ = new EdifCellInstance[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					String lut2name;
					lut2name = "LUT2_" + String.valueOf(i);
					LUT2_[i] = new EdifCellInstance(lut2name, RAM128X1S_1_replacement, LUT2);
				}
				//LUT3s
				EdifCellInstance[] LUT3_ = new EdifCellInstance[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					String lut3name;
					lut3name = "LUT3_" + String.valueOf(i);
					LUT3_[i] = new EdifCellInstance(lut3name, RAM128X1S_1_replacement, LUT3);
				}
				//LUT4s
				EdifCellInstance[] LUT4_ = new EdifCellInstance[LUT4NUM];
				for(int i=0; i<LUT4NUM; i++)
				{
					String lut4name;
					lut4name = "LUT4_" + String.valueOf(i);
					LUT4_[i] = new EdifCellInstance(lut4name, RAM128X1S_1_replacement, LUT4);
				}
				//MUXF5
				EdifCellInstance[] MUXF5_ = new EdifCellInstance[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					String muxf5name;
					muxf5name = "MUXF5_" + String.valueOf(i);
					MUXF5_[i] = new EdifCellInstance(muxf5name, RAM128X1S_1_replacement, MUXF5);
				}
				//MUXF6
				EdifCellInstance[] MUXF6_ = new EdifCellInstance[MUXF6NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					String muxf6name;
					muxf6name = "MUXF6_" + String.valueOf(i);
					MUXF6_[i] = new EdifCellInstance(muxf6name, RAM128X1S_1_replacement, MUXF6);
				}
				//Add all instances to the top level design
				for(int i=0; i<FDE_1NUM; i++)
				{
					RAM128X1S_1_replacement.addSubCell(FDE_1_[i]);
				}
				for(int i=0; i<LUT2NUM; i++)
				{
					RAM128X1S_1_replacement.addSubCell(LUT2_[i]);
				}
				for(int i=0; i<LUT3NUM; i++)
				{
					RAM128X1S_1_replacement.addSubCell(LUT3_[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					RAM128X1S_1_replacement.addSubCell(LUT4_[i]);
				}
				for(int i=0; i<MUXF5NUM; i++)
				{
					RAM128X1S_1_replacement.addSubCell(MUXF5_[i]);
				}
				for(int i=0; i<MUXF6NUM; i++)
				{
					RAM128X1S_1_replacement.addSubCell(MUXF6_[i]);
				}
				
				/******Interface******/
				//Interface of FDE_1s
				EdifCellInterface FDE_1Interface[] = new EdifCellInterface[FDE_1NUM];
				for(int i=0; i<FDE_1NUM; i++)
				{
					FDE_1Interface[i] = new EdifCellInterface(FDE_1);
				}
				//Interface of LUT2s
				EdifCellInterface LUT2Interface[] = new EdifCellInterface[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					LUT2Interface[i] = new EdifCellInterface(LUT2);
				}
				//Interface of LUT3s
				EdifCellInterface LUT3Interface[] = new EdifCellInterface[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					LUT3Interface[i] = new EdifCellInterface(LUT3);
				}
				//Interface of LUT4s
				EdifCellInterface LUT4Interface[] = new EdifCellInterface[LUT4NUM];
				for(int i=0; i<LUT4NUM; i++)
				{
					LUT4Interface[i] = new EdifCellInterface(LUT4);
				}
				//Interface of MUXF5s
				EdifCellInterface MUXF5Interface[] = new EdifCellInterface[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					MUXF5Interface[i] = new EdifCellInterface(MUXF5);
				}
				//Interface of MUXF6s
				EdifCellInterface MUXF6Interface[] = new EdifCellInterface[MUXF6NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					MUXF6Interface[i] = new EdifCellInterface(MUXF6);
				}
				
				/******Ports******/
				//FDE_1s
				EdifPort[] FDE_1Interface_C= new EdifPort[FDE_1NUM];
				EdifPort[] FDE_1Interface_D= new EdifPort[FDE_1NUM];
				EdifPort[] FDE_1Interface_CE= new EdifPort[FDE_1NUM];
				EdifPort[] FDE_1Interface_Q= new EdifPort[FDE_1NUM];
				for(int i=0; i<FDE_1NUM; i++)
				{
					FDE_1Interface_C[i] = new EdifPort(FDE_1Interface[i], "C", 1, 1);
					FDE_1Interface_D[i] = new EdifPort(FDE_1Interface[i], "D", 1, 1);
					FDE_1Interface_CE[i] = new EdifPort(FDE_1Interface[i], "CE", 1, 1);
					FDE_1Interface_Q[i] = new EdifPort(FDE_1Interface[i], "Q", 1, 2);
					FDE_1Interface[i].addPort("C", 1, 1);
					FDE_1Interface[i].addPort("D", 1, 1);
					FDE_1Interface[i].addPort("CE", 1, 1);
					FDE_1Interface[i].addPort("Q", 1, 2);
				}
				//LUT2s
				EdifPort[] LUT2Interface_I0 = new EdifPort[LUT2NUM];
				EdifPort[] LUT2Interface_I1 = new EdifPort[LUT2NUM];
				EdifPort[] LUT2Interface_O = new EdifPort[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					LUT2Interface_I0[i] = new EdifPort(LUT2Interface[i], "I0", 1, 1);
					LUT2Interface_I1[i] = new EdifPort(LUT2Interface[i], "I1", 1, 1);
					LUT2Interface_O[i] = new EdifPort(LUT2Interface[i], "O", 1, 2);
					LUT2Interface[i].addPort("I0", 1, 1);
					LUT2Interface[i].addPort("I1", 1, 1);
					LUT2Interface[i].addPort("O", 1, 2);
				}
				//LUT3s
				EdifPort[] LUT3Interface_I0 = new EdifPort[LUT3NUM];
				EdifPort[] LUT3Interface_I1 = new EdifPort[LUT3NUM];
				EdifPort[] LUT3Interface_I2 = new EdifPort[LUT3NUM];
				EdifPort[] LUT3Interface_O = new EdifPort[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					LUT3Interface_I0[i] = new EdifPort(LUT3Interface[i], "I0", 1, 1);
					LUT3Interface_I1[i] = new EdifPort(LUT3Interface[i], "I1", 1, 1);
					LUT3Interface_I2[i] = new EdifPort(LUT3Interface[i], "I2", 1, 1);
					LUT3Interface_O[i] = new EdifPort(LUT3Interface[i], "O", 1, 2);
					LUT3Interface[i].addPort("I0", 1, 1);
					LUT3Interface[i].addPort("I1", 1, 1);
					LUT3Interface[i].addPort("I2", 1, 1);
					LUT3Interface[i].addPort("O", 1, 2);
				}
				//LUT4s
				EdifPort[] LUT4Interface_I0 = new EdifPort[LUT4NUM];
				EdifPort[] LUT4Interface_I1 = new EdifPort[LUT4NUM];
				EdifPort[] LUT4Interface_I2 = new EdifPort[LUT4NUM];
				EdifPort[] LUT4Interface_I3 = new EdifPort[LUT4NUM];
				EdifPort[] LUT4Interface_O = new EdifPort[LUT4NUM];
				for(int i=0; i<LUT4NUM; i++)
				{
					LUT4Interface_I0[i] = new EdifPort(LUT4Interface[i], "I0", 1, 1);
					LUT4Interface_I1[i] = new EdifPort(LUT4Interface[i], "I1", 1, 1);
					LUT4Interface_I2[i] = new EdifPort(LUT4Interface[i], "I2", 1, 1);
					LUT4Interface_I3[i] = new EdifPort(LUT4Interface[i], "I3", 1, 1);
					LUT4Interface_O[i] = new EdifPort(LUT4Interface[i], "O", 1, 2);
					LUT4Interface[i].addPort("I0", 1, 1);
					LUT4Interface[i].addPort("I1", 1, 1);
					LUT4Interface[i].addPort("I2", 1, 1);
					LUT4Interface[i].addPort("I3", 1, 1);
					LUT4Interface[i].addPort("O", 1, 2);
				}
				//MUXF5s
				EdifPort[] MUXF5Interface_I0 = new EdifPort[MUXF5NUM];
				EdifPort[] MUXF5Interface_I1 = new EdifPort[MUXF5NUM];
				EdifPort[] MUXF5Interface_S = new EdifPort[MUXF5NUM];
				EdifPort[] MUXF5Interface_O = new EdifPort[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					MUXF5Interface_I0[i] = new EdifPort(MUXF5Interface[i], "I0", 1, 1);
					MUXF5Interface_I1[i] = new EdifPort(MUXF5Interface[i], "I1", 1, 1);
					MUXF5Interface_S[i] = new EdifPort(MUXF5Interface[i], "S", 1, 1);
					MUXF5Interface_O[i] = new EdifPort(MUXF5Interface[i], "O", 1, 2);
					MUXF5Interface[i].addPort("I0", 1, 1);
					MUXF5Interface[i].addPort("I1", 1, 1);
					MUXF5Interface[i].addPort("S", 1, 1);
					MUXF5Interface[i].addPort("O", 1, 2);
				}
				//MUXF6s
				EdifPort[] MUXF6Interface_I0 = new EdifPort[MUXF6NUM];
				EdifPort[] MUXF6Interface_I1 = new EdifPort[MUXF6NUM];
				EdifPort[] MUXF6Interface_S = new EdifPort[MUXF6NUM];
				EdifPort[] MUXF6Interface_O = new EdifPort[MUXF6NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					MUXF6Interface_I0[i] = new EdifPort(MUXF6Interface[i], "I0", 1, 1);
					MUXF6Interface_I1[i] = new EdifPort(MUXF6Interface[i], "I1", 1, 1);
					MUXF6Interface_S[i] = new EdifPort(MUXF6Interface[i], "S", 1, 1);
					MUXF6Interface_O[i] = new EdifPort(MUXF6Interface[i], "O", 1, 2);
					MUXF6Interface[i].addPort("I0", 1, 1);
					MUXF6Interface[i].addPort("I1", 1, 1);
					MUXF6Interface[i].addPort("S", 1, 1);
					MUXF6Interface[i].addPort("O", 1, 2);
				}
				
				/******PortRefs******/
				//Port reference of FDE_1s
				EdifPortRef[] FDE_1_C = new EdifPortRef[FDE_1NUM];
				EdifPortRef[] FDE_1_D = new EdifPortRef[FDE_1NUM];
				EdifPortRef[] FDE_1_CE = new EdifPortRef[FDE_1NUM];
				EdifPortRef[] FDE_1_Q = new EdifPortRef[FDE_1NUM];
				for(int i=0; i<FDE_1NUM; i++)
				{
					int q = i%128;
					FDE_1_C[i] = new EdifPortRef(wclk, FDE_1Interface_C[i].getSingleBitPort(0), FDE_1_[i]);
					FDE_1_CE[i] = new EdifPortRef(e[q], FDE_1Interface_CE[i].getSingleBitPort(0), FDE_1_[i]);
					FDE_1_Q[i] = new EdifPortRef(wire[i], FDE_1Interface_Q[i].getSingleBitPort(0), FDE_1_[i]);
					FDE_1_D[i] = new EdifPortRef(d, FDE_1Interface_D[i].getSingleBitPort(0), FDE_1_[i]);
				}
				//Port reference of LUT2s
				EdifPortRef[] LUT2_I0 = new EdifPortRef[LUT2NUM];
				EdifPortRef[] LUT2_I1 = new EdifPortRef[LUT2NUM];
				EdifPortRef[] LUT2_O = new EdifPortRef[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
						LUT2_I0[i] = new EdifPortRef(a6, LUT2Interface_I0[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_I1[i] = new EdifPortRef(we, LUT2Interface_I1[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_O[i] = new EdifPortRef(net[i+126], LUT2Interface_O[i].getSingleBitPort(0), LUT2_[i]);
				}
				//Port reference of LUT3s
				EdifPortRef[] LUT3_I0 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_I1 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_I2 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_O = new EdifPortRef[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					int a = i/79;
					int q = i%79;
					if(q<64)
					{
						int wireNumber = 0;
						int lutNumber = i%64;
						int divider = 32;
						int addition = 1;
						for(int j=0; j<6; j++)
						{
							if(lutNumber/divider==1)
							{
								wireNumber += addition;
								lutNumber -= divider;
							}
							divider /= 2;
							addition *= 2;
						}
						LUT3_I0[i] = new EdifPortRef(a6, LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_I1[i] = new EdifPortRef(wire[wireNumber+128*a], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_I2[i] = new EdifPortRef(wire[wireNumber+64+128*a], LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_O[i] = new EdifPortRef(net[q+126*a], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
					}
					else
					{
						LUT3_I0[i] = new EdifPortRef(net[126*a+2*q-32], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_I1[i] = new EdifPortRef(net[126*a+2*q-31], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
						if(q>=64 && q<=71)
						{
							LUT3_I2[i] = new EdifPortRef(a3, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[126*a+48], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else if(q>=72 && q<=75)
						{
							LUT3_I2[i] = new EdifPortRef(a2, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[126*a+48], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else if(q==76 || q==77)
						{
							LUT3_I2[i] = new EdifPortRef(a1, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[126*a+48], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else
						{
							LUT3_I2[i] = new EdifPortRef(a0, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(o, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
					}
				}
				//Port reference of LUT4s
				EdifPortRef[] LUT4_I0 = new EdifPortRef[LUT4NUM];
				EdifPortRef[] LUT4_I1 = new EdifPortRef[LUT4NUM];
				EdifPortRef[] LUT4_I2 = new EdifPortRef[LUT4NUM];
				EdifPortRef[] LUT4_I3 = new EdifPortRef[LUT4NUM];
				EdifPortRef[] LUT4_O = new EdifPortRef[LUT4NUM];
				for(int i=0; i<LUT4NUM; i++)
				{
					if(i<16)
					{
						if(i<8)
							LUT4_I0[i] = new EdifPortRef(net[NETNUM-18], LUT4Interface_I0[i].getSingleBitPort(0), LUT4_[i]);
						else
							LUT4_I0[i] = new EdifPortRef(net[NETNUM-17], LUT4Interface_I0[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_I1[i] = new EdifPortRef(a3, LUT4Interface_I1[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_I2[i] = new EdifPortRef(a4, LUT4Interface_I2[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_I3[i] = new EdifPortRef(a5, LUT4Interface_I3[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_O[i] = new EdifPortRef(net[i+128], LUT4Interface_O[i].getSingleBitPort(0), LUT4_[i]);
					}
					else
					{
						int a = (i-16)/8;
						LUT4_I0[i] = new EdifPortRef(net[a+128], LUT4Interface_I0[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_I1[i] = new EdifPortRef(a0, LUT4Interface_I1[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_I2[i] = new EdifPortRef(a1, LUT4Interface_I2[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_I3[i] = new EdifPortRef(a2, LUT4Interface_I3[i].getSingleBitPort(0), LUT4_[i]);
						LUT4_O[i] = new EdifPortRef(e[i-16], LUT4Interface_O[i].getSingleBitPort(0), LUT4_[i]);
					}
				}
				//Port reference of MUXF5s
				EdifPortRef[] MUXF5_I0 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF5_I1 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF5_S = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF5_O = new EdifPortRef[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					int q = i%32;
					int a = i/32;
					MUXF5_I0[i] = new EdifPortRef(net[2*q+126*a], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
					MUXF5_I1[i] = new EdifPortRef(net[2*q+1+126*a], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
					MUXF5_S[i] = new EdifPortRef(a5, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
					MUXF5_O[i] = new EdifPortRef(net[64+126*a], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
				}
				//Port reference of MUXF6s
				EdifPortRef[] MUXF6_I0 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_I1 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_S = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_O = new EdifPortRef[MUXF5NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					int a = i/16;
					int q = i%16;
					MUXF6_I0[i] = new EdifPortRef(net[2*q+64+126*a], MUXF6Interface_I0[i].getSingleBitPort(0), MUXF6_[i]);
					MUXF6_I1[i] = new EdifPortRef(net[2*q+64+126*a], MUXF6Interface_I1[i].getSingleBitPort(0), MUXF6_[i]);
					MUXF6_S[i] = new EdifPortRef(a4, MUXF6Interface_S[i].getSingleBitPort(0), MUXF6_[i]);
					MUXF6_O[i] = new EdifPortRef(net[96+126*a], MUXF6Interface_O[i].getSingleBitPort(0), MUXF6_[i]);
				}
				//we
				for(int i=0; i<2;i++)
				{
					we.addPortConnection(LUT2_I1[i]);
				}
				//d
				for(int i=0; i<128; i++)
				{
					d.addPortConnection(FDE_1_D[i]);
				}
				//wclk
				for(int i=0; i<FDE_1NUM; i++)
				{
					wclk.addPortConnection(FDE_1_C[i]);
				}
				//a0
				for(int i=0; i<LUT3NUM; i++)
				{
					if((i%79)==78)
						a0.addPortConnection(LUT3_I2[i]);
				}
				for(int i=16; i<LUT4NUM; i++)
				{
					a0.addPortConnection(LUT4_I1[i]);
				}
				//a1
				for(int i=0; i<LUT3NUM; i++)
				{
					if((i%79)==76 || (i%79)==77)
						a1.addPortConnection(LUT3_I2[i]);
				}
				for(int i=16; i<LUT4NUM; i++)
				{
					a1.addPortConnection(LUT4_I2[i]);
				}
				//a2
				for(int i=0; i<LUT3NUM; i++)
				{
					if((i%79)>=72 && (i%79)<=75)
						a2.addPortConnection(LUT3_I2[i]);
				}
				for(int i=16; i<LUT4NUM; i++)
				{
					a2.addPortConnection(LUT4_I3[i]);
				}
				//a3
				for(int i=0; i<LUT3NUM; i++)
				{
					if((i%79)>=64 && (i%79)<=71)
						a3.addPortConnection(LUT3_I2[i]);
				}
				for(int i=0; i<16; i++)
				{
					a3.addPortConnection(LUT4_I1[i]);
				}
				//a4
				for(int i=0; i<MUXF6NUM; i++)
				{
					a4.addPortConnection(MUXF6_S[i]);
				}
				for(int i=0; i<16; i++)
				{
					a4.addPortConnection(LUT4_I2[i]);
				}
				//a5
				for(int i=0; i<MUXF5NUM; i++)
				{
					a5.addPortConnection(MUXF5_S[i]);
				}
				for(int i=0; i<16; i++)
				{
					a5.addPortConnection(LUT4_I3[i]);
				}
				//a6
				for(int i=0; i<LUT3NUM; i++)
				{
					if((i%79)<64)
						a6.addPortConnection(LUT3_I0[i]);
				}
				for(int i=0; i<LUT2NUM; i++)
				{
					a6.addPortConnection(LUT2_I0[i]);
				}
				//o
				o.addPortConnection(LUT3_O[78]);
				//wires
				for(int i=0; i<WIRENUM; i++)
				{
					int lutNumber = 0;
					int value = 32;
					int addition = 1;
					int wireNumber = i%64;
					for(int j=0; j<6; j++)
					{
						if(wireNumber==value)
						{
							lutNumber += addition;
							break;
						}
						else if(wireNumber>value)
						{
							wireNumber -= value;
							lutNumber += addition;
						}
						addition *= 2;
						value /= 2;
					}				
					int a = i/128;
					int q = i%128;
					wire[i].addPortConnection(FDE_1_Q[i]);
					if(q<64)
					{
						wire[i].addPortConnection(LUT3_I1[a*79+lutNumber]);
					}
					else
					{
						wire[i].addPortConnection(LUT3_I2[a*79+lutNumber]);
					}
				}
				//e
				for(int i=0; i<ENUM; i++)
				{
					e[i].addPortConnection(LUT4_O[i+16]);
					e[i].addPortConnection(FDE_1_CE[i]);
				}
				//nets
				for(int i=0; i<NETNUM; i++)
				{
					int a = i/126;
					int q = i%126;
					if(i<NETNUM-18)	//nets in MUX
					{
						if(q<64)
						{
							net[i].addPortConnection(LUT3_O[q+79*a]);
							if((q%2)==0)
								net[i].addPortConnection(MUXF5_I0[q/2+32*a]);
							else
								net[i].addPortConnection(MUXF5_I1[q/2+32*a]);
						}
						else if(q<96)
						{
							net[i].addPortConnection(MUXF5_O[q-64+32*a]);
							if((q%2)==0)
								net[i].addPortConnection(MUXF6_I0[q/2-32+16*a]);
							else
								net[i].addPortConnection(MUXF6_I1[q/2-32+16*a]);
						}
						else if(q<112)
						{
							net[i].addPortConnection(MUXF6_O[q-96+16*a]);
							if((q%2)==0)
								net[i].addPortConnection(LUT3_I0[q/2+16+79*a]);
							else
								net[i].addPortConnection(LUT3_I1[q/2+16+79*a]);
						}
						else
						{
							net[i].addPortConnection(LUT3_O[q-48+79*a]);
							if((q%2)==0)
								net[i].addPortConnection(LUT3_I0[q/2+16+79*a]);
							else
								net[i].addPortConnection(LUT3_I1[q/2+16+79*a]);
						}
					}
					else			//nets in decoder
					{
						if(q<2)
						{
							net[i].addPortConnection(LUT2_O[q]);
							for(int j=0; j<8; j++)
								net[i].addPortConnection(LUT4_I0[q*8+j]);
						}
						else
						{
							net[i].addPortConnection(LUT4_O[q-2]);
							for(int j=0; j<8; j++)
								net[i].addPortConnection(LUT4_I0[q*8+j]);
						}
					}
				}
				
				/******Set INIT Property******/
				boolean isInit;
				int initCount_high = 0;
				int initCount_low = 0;
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
		        //Set INIT Property for LUT3s
		        PropertyList[] LUT3_propertylist = new PropertyList[LUT3NUM];
		        for(int i=0; i<LUT3NUM; i++)
		        {
		        	int q = i%79;
		        	isInit = false;
		        	LUT3_propertylist[i] = LUT3_[i].getPropertyList();
		        	if(LUT3_propertylist[i] != null)
		        	{
		        		for(Property LUT3_property : LUT3_propertylist[i].values())
		        		{
		        			if(LUT3_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if(q<64)
		        					LUT3_property.setValue(valueE4);
		        				else
		        					LUT3_property.setValue(valueCA);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if(q<64)
		        			LUT3_[i].addProperty(new Property("INIT", (EdifTypedValue)valueE4));
		        		else
		        			LUT3_[i].addProperty(new Property("INIT", (EdifTypedValue)valueCA));
		        	}
		        }
		        //Set INIT Property for LUT2s
		        PropertyList[] LUT2_propertylist = new PropertyList[LUT2NUM];
		        for(int i=0; i<LUT2NUM; i++)
		        {
		        	isInit = false;
		        	LUT2_propertylist[i] = LUT2_[i].getPropertyList();
		        	if(LUT2_propertylist[i] != null)
		        	{
		        		for(Property LUT2_property : LUT2_propertylist[i].values())
		        		{
		        			if(LUT2_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if(i==0)
		        					LUT2_property.setValue(value4);
		        				else
		        					LUT2_property.setValue(value8);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if(i==0)
		        			LUT2_[i].addProperty(new Property("INIT", (EdifTypedValue)value4));
		        		else
		        			LUT2_[i].addProperty(new Property("INIT", (EdifTypedValue)value8));
		        	}
		        }
		        //Set INIT Property for LUT4s
		        PropertyList[] LUT4_propertylist = new PropertyList[LUT4NUM];
		        for(int i=0; i<LUT4NUM; i++)
		        {
		        	int q = i%8;
		        	isInit = false;
		        	LUT4_propertylist[i] = LUT4_[i].getPropertyList();
		        	if(LUT4_propertylist[i] != null)
		        	{
		        		for(Property LUT4_property : LUT4_propertylist[i].values())
		        		{
		        			if(LUT4_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if(q==0)
		        					LUT4_property.setValue(value0002);
		        				else if(q==1)
		        					LUT4_property.setValue(value0008);
		        				else if(q==2)
		        					LUT4_property.setValue(value0020);
		        				else if(q==3)
		        					LUT4_property.setValue(value0080);
		        				else if(q==4)
		        					LUT4_property.setValue(value0200);
		        				else if(q==5)
		        					LUT4_property.setValue(value0800);
		        				else if(q==6)
		        					LUT4_property.setValue(value2000);
		        				else
		        					LUT4_property.setValue(value8000);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if(q==0)
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value0002));
		        		else if(q==1)
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value0008));
		        		else if(q==2)
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value0020));
		        		else if(q==3)
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value0080));
		        		else if(q==4)
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value0200));
		        		else if(q==5)
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value0800));
		        		else if(q==6)
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value2000));
		        		else
		        			LUT4_[i].addProperty(new Property("INIT", (EdifTypedValue)value8000));
		        	}
		        }
		        //Set INIT Property for FDE_1s
		        PropertyList[] FDE_1_propertylist = new PropertyList[FDE_1NUM];
		        for(int i=0; i<FDE_1NUM; i++)
		        {
		        	isInit = false;
		        	FDE_1_propertylist[i] = FDE_1_[i].getPropertyList();
		        	if(FDE_1_propertylist[i] != null)
		        	{
		        		for(Property FDE_1_property : FDE_1_propertylist[i].values())
		        		{
		        			if(FDE_1_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if(i<64) {
		        					if (((INIT_LOW >> initCount_low++) & 1) == 1)
		        						FDE_1_property.setValue(valueOne);
		        					else
		        						FDE_1_property.setValue(valueZero);
		        				}
		        				else {
		        					if (((INIT_HIGH >> initCount_high++) & 1) == 1)
		        						FDE_1_property.setValue(valueOne);
		        					else
		        						FDE_1_property.setValue(valueZero);
		        				}
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{if(i<64) {
	        			if (((INIT_LOW >> initCount_low++) & 1) == 1)
	        				FDE_1_[i].addProperty(new Property("INIT", (EdifTypedValue)valueOne));
	        			else
	        				FDE_1_[i].addProperty(new Property("INIT", (EdifTypedValue)valueZero));
	        		}
	        		else {
	        			if (((INIT_HIGH >> initCount_high++) & 1) == 1)
	        				FDE_1_[i].addProperty(new Property("INIT", (EdifTypedValue)valueOne));
	        			else
	        				FDE_1_[i].addProperty(new Property("INIT", (EdifTypedValue)valueZero));
	        		}
		        	}
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