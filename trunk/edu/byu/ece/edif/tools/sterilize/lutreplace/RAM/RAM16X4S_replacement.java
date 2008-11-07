/* Generates the EDIF file for RAM16X4S
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
 * Generates the EDIF file for RAM16X4S
 * Includes: 64 FDEs, 4 16-1 MUXs(9 LUT3s, 4 MUXF5s, and 2 MUXF6s for each), 1 4-16 decoder(2 LUT2s, 16 LUT4s).
 * 
 * @author Yubo Li
 *
 */

public class RAM16X4S_replacement{
	public static void main(String args[]){
		
		String outputFileName = "RAM16X4S_replacement.edf";
		
		try{
			/******Environment and Environment manager******/
			EdifEnvironment topLevel = new EdifEnvironment("RAM16X4S_replacement");
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
				EdifCell RAM16X4S_replacement = new EdifCell(work, "RAM16X4S_replacement");
				RAM16X4S_replacement.addPort("WE", 1, 1);
				RAM16X4S_replacement.addPort("D0", 1, 1);
				RAM16X4S_replacement.addPort("D1", 1, 1);
				RAM16X4S_replacement.addPort("D2", 1, 1);
				RAM16X4S_replacement.addPort("D3", 1, 1);
				RAM16X4S_replacement.addPort("WCLK", 1, 1);
				RAM16X4S_replacement.addPort("A0", 1, 1);
				RAM16X4S_replacement.addPort("A1", 1, 1);
				RAM16X4S_replacement.addPort("A2", 1, 1);
				RAM16X4S_replacement.addPort("A3", 1, 1);
				RAM16X4S_replacement.addPort("O0", 1, 2);
				RAM16X4S_replacement.addPort("O1", 1, 2);
				RAM16X4S_replacement.addPort("O2", 1, 2);
				RAM16X4S_replacement.addPort("O3", 1, 2);
				
				/******Design******/
				EdifDesign design = new EdifDesign("RAM16X4S_replacement");
				topLevel.setTopDesign(design);
				topLevel.setTopCell(RAM16X4S_replacement);
				
				/******Nets******/
				//Constants
				int WIRENUM = 64;
				int NETNUM = 58;
				int ENUM = 16;
				//Declaration of input and output ports
				EdifNet wclk_c = new EdifNet("wclk_c", RAM16X4S_replacement);
				EdifNet wclk = new EdifNet("wclk", RAM16X4S_replacement);
				EdifNet d0_c = new EdifNet("d0_c", RAM16X4S_replacement);
				EdifNet d0 = new EdifNet("d0", RAM16X4S_replacement);
				EdifNet d1_c = new EdifNet("d1_c", RAM16X4S_replacement);
				EdifNet d1 = new EdifNet("d1", RAM16X4S_replacement);
				EdifNet d2_c = new EdifNet("d2_c", RAM16X4S_replacement);
				EdifNet d2 = new EdifNet("d2", RAM16X4S_replacement);
				EdifNet d3_c = new EdifNet("d3_c", RAM16X4S_replacement);
				EdifNet d3 = new EdifNet("d3", RAM16X4S_replacement);
				EdifNet we_c = new EdifNet("we_c", RAM16X4S_replacement);
				EdifNet we = new EdifNet("we", RAM16X4S_replacement);
				EdifNet a0_c = new EdifNet("a0_c", RAM16X4S_replacement);
				EdifNet a0 = new EdifNet("a0", RAM16X4S_replacement);
				EdifNet a1_c = new EdifNet("a1_c", RAM16X4S_replacement);
				EdifNet a1 = new EdifNet("a1", RAM16X4S_replacement);
				EdifNet a2_c = new EdifNet("a2_c", RAM16X4S_replacement);
				EdifNet a2 = new EdifNet("a2", RAM16X4S_replacement);
				EdifNet a3_c = new EdifNet("a3_c", RAM16X4S_replacement);
				EdifNet a3 = new EdifNet("a3", RAM16X4S_replacement);
				EdifNet o0_c = new EdifNet("o0_c", RAM16X4S_replacement);
				EdifNet o0 = new EdifNet("o0", RAM16X4S_replacement);
				EdifNet o1_c = new EdifNet("o1_c", RAM16X4S_replacement);
				EdifNet o1 = new EdifNet("o1", RAM16X4S_replacement);
				EdifNet o2_c = new EdifNet("o2_c", RAM16X4S_replacement);
				EdifNet o2 = new EdifNet("o2", RAM16X4S_replacement);
				EdifNet o3_c = new EdifNet("o3_c", RAM16X4S_replacement);
				EdifNet o3 = new EdifNet("o3", RAM16X4S_replacement);
				//Declaration of wires, which are the outputs of FDEs
				EdifNet[] wire = new EdifNet[WIRENUM];
				for(int i=0; i<WIRENUM; i++)
				{
					String wirename;
					wirename = "wire" + String.valueOf(i);
					wire[i] = new EdifNet(wirename, RAM16X4S_replacement);
				}
				//Declaration of nets, which are used in MUX and decoder
				EdifNet[] net = new EdifNet[NETNUM];
				for(int i=0; i<NETNUM; i++)
				{
					String netname;
					netname = "net" + String.valueOf(i);
					net[i] = new EdifNet(netname, RAM16X4S_replacement);
				}
				//Declaration of enables
				EdifNet[] e = new EdifNet[ENUM];
				for(int i=0; i<ENUM; i++)
				{
					String ename;
					ename = "e" + String.valueOf(i);
					e[i] = new EdifNet(ename, RAM16X4S_replacement);
				}
				//Add all nets to the top level design
				RAM16X4S_replacement.addNet(wclk_c);
				RAM16X4S_replacement.addNet(wclk);
				RAM16X4S_replacement.addNet(d0_c);
				RAM16X4S_replacement.addNet(d0);
				RAM16X4S_replacement.addNet(d1_c);
				RAM16X4S_replacement.addNet(d1);
				RAM16X4S_replacement.addNet(d2_c);
				RAM16X4S_replacement.addNet(d2);
				RAM16X4S_replacement.addNet(d3_c);
				RAM16X4S_replacement.addNet(d3);
				RAM16X4S_replacement.addNet(we_c);
				RAM16X4S_replacement.addNet(we);
				RAM16X4S_replacement.addNet(a0_c);
				RAM16X4S_replacement.addNet(a0);
				RAM16X4S_replacement.addNet(a1_c);
				RAM16X4S_replacement.addNet(a1);
				RAM16X4S_replacement.addNet(a2_c);
				RAM16X4S_replacement.addNet(a2);
				RAM16X4S_replacement.addNet(a3_c);
				RAM16X4S_replacement.addNet(a3);
				RAM16X4S_replacement.addNet(o0_c);
				RAM16X4S_replacement.addNet(o0);
				RAM16X4S_replacement.addNet(o1_c);
				RAM16X4S_replacement.addNet(o1);
				RAM16X4S_replacement.addNet(o2_c);
				RAM16X4S_replacement.addNet(o2);
				RAM16X4S_replacement.addNet(o3_c);
				RAM16X4S_replacement.addNet(o3);
				for(int i=0; i<WIRENUM; i++)
				{
					RAM16X4S_replacement.addNet(wire[i]);
				}
				for(int i=0; i<NETNUM; i++)
				{
					RAM16X4S_replacement.addNet(net[i]);
				}
				for(int i=0; i<ENUM; i++)
				{
					RAM16X4S_replacement.addNet(e[i]);
				}
				
				/******Instances******/
				//Constants
				int FDENUM = 64;
				int LUT2NUM = 2;
				int LUT3NUM = 36;
				int LUT4NUM = 16;
				int MUXF5NUM = 16;
				int MUXF6NUM = 8;
				//Buffers for input and output ports
				EdifCellInstance WE_ibuf = new EdifCellInstance("WE_ibuf", RAM16X4S_replacement, IBUF);
				EdifCellInstance D0_ibuf = new EdifCellInstance("D0_ibuf", RAM16X4S_replacement, IBUF);
				EdifCellInstance D1_ibuf = new EdifCellInstance("D1_ibuf", RAM16X4S_replacement, IBUF);
				EdifCellInstance D2_ibuf = new EdifCellInstance("D2_ibuf", RAM16X4S_replacement, IBUF);
				EdifCellInstance D3_ibuf = new EdifCellInstance("D3_ibuf", RAM16X4S_replacement, IBUF);
				EdifCellInstance WCLK_ibuf = new EdifCellInstance("WCLK_ibuf", RAM16X4S_replacement, BUFGP);
				EdifCellInstance A0_ibuf = new EdifCellInstance("A0_ibuf", RAM16X4S_replacement, IBUF);
				EdifCellInstance A1_ibuf = new EdifCellInstance("A1_ibuf", RAM16X4S_replacement, IBUF);
				EdifCellInstance A2_ibuf = new EdifCellInstance("A2_ibuf", RAM16X4S_replacement, IBUF);
				EdifCellInstance A3_ibuf = new EdifCellInstance("A3_ibuf", RAM16X4S_replacement, IBUF);
				EdifCellInstance O0_obuf = new EdifCellInstance("O0_obuf", RAM16X4S_replacement, OBUF);
				EdifCellInstance O1_obuf = new EdifCellInstance("O1_obuf", RAM16X4S_replacement, OBUF);
				EdifCellInstance O2_obuf = new EdifCellInstance("O2_obuf", RAM16X4S_replacement, OBUF);
				EdifCellInstance O3_obuf = new EdifCellInstance("O3_obuf", RAM16X4S_replacement, OBUF);
				//FDEs
				EdifCellInstance[] FDE_ = new EdifCellInstance[FDENUM];
				for(int i=0; i<FDENUM; i++)
				{
					String fdename;
					fdename = "FDE" + String.valueOf(i);
					FDE_[i] = new EdifCellInstance(fdename, RAM16X4S_replacement, FDE);
				}
				//LUT2s
				EdifCellInstance[] LUT2_ = new EdifCellInstance[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					String lut2name;
					lut2name = "LUT2_" + String.valueOf(i);
					LUT2_[i] = new EdifCellInstance(lut2name, RAM16X4S_replacement, LUT2);
				}
				//LUT3s
				EdifCellInstance[] LUT3_ = new EdifCellInstance[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					String lut3name;
					lut3name = "LUT3_" + String.valueOf(i);
					LUT3_[i] = new EdifCellInstance(lut3name, RAM16X4S_replacement, LUT3);
				}
				//LUT4s
				EdifCellInstance[] LUT4_ = new EdifCellInstance[LUT4NUM];
				for(int i=0; i<LUT4NUM; i++)
				{
					String lut4name;
					lut4name = "LUT4_" + String.valueOf(i);
					LUT4_[i] = new EdifCellInstance(lut4name, RAM16X4S_replacement, LUT4);
				}
				//MUXF5
				EdifCellInstance[] MUXF5_ = new EdifCellInstance[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					String muxf5name;
					muxf5name = "MUXF5_" + String.valueOf(i);
					MUXF5_[i] = new EdifCellInstance(muxf5name, RAM16X4S_replacement, MUXF5);
				}
				//MUXF6
				EdifCellInstance[] MUXF6_ = new EdifCellInstance[MUXF6NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					String muxf6name;
					muxf6name = "MUXF6_" + String.valueOf(i);
					MUXF6_[i] = new EdifCellInstance(muxf6name, RAM16X4S_replacement, MUXF6);
				}
				//Add all instances to the top level design
				RAM16X4S_replacement.addSubCell(WE_ibuf);
				RAM16X4S_replacement.addSubCell(D0_ibuf);
				RAM16X4S_replacement.addSubCell(D1_ibuf);
				RAM16X4S_replacement.addSubCell(D2_ibuf);
				RAM16X4S_replacement.addSubCell(D3_ibuf);
				RAM16X4S_replacement.addSubCell(WCLK_ibuf);
				RAM16X4S_replacement.addSubCell(A0_ibuf);
				RAM16X4S_replacement.addSubCell(A1_ibuf);
				RAM16X4S_replacement.addSubCell(A2_ibuf);
				RAM16X4S_replacement.addSubCell(A3_ibuf);
				RAM16X4S_replacement.addSubCell(O0_obuf);
				RAM16X4S_replacement.addSubCell(O1_obuf);
				RAM16X4S_replacement.addSubCell(O2_obuf);
				RAM16X4S_replacement.addSubCell(O3_obuf);
				for(int i=0; i<FDENUM; i++)
				{
					RAM16X4S_replacement.addSubCell(FDE_[i]);
				}
				for(int i=0; i<LUT2NUM; i++)
				{
					RAM16X4S_replacement.addSubCell(LUT2_[i]);
				}
				for(int i=0; i<LUT3NUM; i++)
				{
					RAM16X4S_replacement.addSubCell(LUT3_[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					RAM16X4S_replacement.addSubCell(LUT4_[i]);
				}
				for(int i=0; i<MUXF5NUM; i++)
				{
					RAM16X4S_replacement.addSubCell(MUXF5_[i]);
				}
				for(int i=0; i<MUXF6NUM; i++)
				{
					RAM16X4S_replacement.addSubCell(MUXF6_[i]);
				}
				
				/******Interface******/
				//Interface of buffers
				EdifCellInterface RAM16X4S_replacement_interface = new EdifCellInterface(RAM16X4S_replacement);
				EdifCellInterface WE_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D0_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D1_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D2_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D3_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface WCLK_ibuf_interface = new EdifCellInterface(BUFGP);
				EdifCellInterface A0_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A1_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A2_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A3_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface O0_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface O1_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface O2_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface O3_obuf_interface = new EdifCellInterface(OBUF);
				//Interface of FDEs
				EdifCellInterface FDEInterface[] = new EdifCellInterface[FDENUM];
				for(int i=0; i<FDENUM; i++)
				{
					FDEInterface[i] = new EdifCellInterface(FDE);
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
				//RAM16X4S_replacement
				EdifPort RAM16X4S_replacement_interface_WE = new EdifPort(RAM16X4S_replacement_interface, "WE", 1, 1);
				EdifPort RAM16X4S_replacement_interface_D0 = new EdifPort(RAM16X4S_replacement_interface, "D0", 1, 1);
				EdifPort RAM16X4S_replacement_interface_D1 = new EdifPort(RAM16X4S_replacement_interface, "D1", 1, 1);
				EdifPort RAM16X4S_replacement_interface_D2 = new EdifPort(RAM16X4S_replacement_interface, "D2", 1, 1);
				EdifPort RAM16X4S_replacement_interface_D3 = new EdifPort(RAM16X4S_replacement_interface, "D3", 1, 1);
				EdifPort RAM16X4S_replacement_interface_WCLK = new EdifPort(RAM16X4S_replacement_interface, "WCLK", 1, 1);
				EdifPort RAM16X4S_replacement_interface_A0 = new EdifPort(RAM16X4S_replacement_interface, "A0", 1, 1);
				EdifPort RAM16X4S_replacement_interface_A1 = new EdifPort(RAM16X4S_replacement_interface, "A1", 1, 1);
				EdifPort RAM16X4S_replacement_interface_A2 = new EdifPort(RAM16X4S_replacement_interface, "A2", 1, 1);
				EdifPort RAM16X4S_replacement_interface_A3 = new EdifPort(RAM16X4S_replacement_interface, "A3", 1, 1);
				EdifPort RAM16X4S_replacement_interface_O0 = new EdifPort(RAM16X4S_replacement_interface, "O0", 1, 2);
				EdifPort RAM16X4S_replacement_interface_O1 = new EdifPort(RAM16X4S_replacement_interface, "O1", 1, 2);
				EdifPort RAM16X4S_replacement_interface_O2 = new EdifPort(RAM16X4S_replacement_interface, "O2", 1, 2);
				EdifPort RAM16X4S_replacement_interface_O3 = new EdifPort(RAM16X4S_replacement_interface, "O3", 1, 2);
				RAM16X4S_replacement_interface.addPort("WE", 1, 1);
				RAM16X4S_replacement_interface.addPort("D0", 1, 1);
				RAM16X4S_replacement_interface.addPort("D1", 1, 1);
				RAM16X4S_replacement_interface.addPort("D2", 1, 1);
				RAM16X4S_replacement_interface.addPort("D3", 1, 1);
				RAM16X4S_replacement_interface.addPort("WCLK", 1, 1);
				RAM16X4S_replacement_interface.addPort("A0", 1, 1);
				RAM16X4S_replacement_interface.addPort("A1", 1, 1);
				RAM16X4S_replacement_interface.addPort("A2", 1, 1);
				RAM16X4S_replacement_interface.addPort("A3", 1, 1);
				RAM16X4S_replacement_interface.addPort("O0", 1, 2);
				RAM16X4S_replacement_interface.addPort("O1", 1, 2);
				RAM16X4S_replacement_interface.addPort("O2", 1, 2);
				RAM16X4S_replacement_interface.addPort("O3", 1, 2);
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
				//D2_ibuf
				EdifPort D2_ibuf_interface_I = new EdifPort(D2_ibuf_interface, "I", 1, 1);
				EdifPort D2_ibuf_interface_O = new EdifPort(D2_ibuf_interface, "O", 1, 2);
				D2_ibuf_interface.addPort("I", 1, 1);
				D2_ibuf_interface.addPort("O", 1, 2);
				//D3_ibuf
				EdifPort D3_ibuf_interface_I = new EdifPort(D3_ibuf_interface, "I", 1, 1);
				EdifPort D3_ibuf_interface_O = new EdifPort(D3_ibuf_interface, "O", 1, 2);
				D3_ibuf_interface.addPort("I", 1, 1);
				D3_ibuf_interface.addPort("O", 1, 2);
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
				//O2_obuf
				EdifPort O2_obuf_interface_I = new EdifPort(O2_obuf_interface, "I", 1, 1);
				EdifPort O2_obuf_interface_O = new EdifPort(O2_obuf_interface, "O", 1, 2);
				O2_obuf_interface.addPort("I", 1, 1);
				O2_obuf_interface.addPort("O", 1, 2);
				//O3_obuf
				EdifPort O3_obuf_interface_I = new EdifPort(O3_obuf_interface, "I", 1, 1);
				EdifPort O3_obuf_interface_O = new EdifPort(O3_obuf_interface, "O", 1, 2);
				O3_obuf_interface.addPort("I", 1, 1);
				O3_obuf_interface.addPort("O", 1, 2);
				//FDEs
				EdifPort[] FDEInterface_C= new EdifPort[FDENUM];
				EdifPort[] FDEInterface_D= new EdifPort[FDENUM];
				EdifPort[] FDEInterface_CE= new EdifPort[FDENUM];
				EdifPort[] FDEInterface_Q= new EdifPort[FDENUM];
				for(int i=0; i<FDENUM; i++)
				{
					FDEInterface_C[i] = new EdifPort(FDEInterface[i], "C", 1, 1);
					FDEInterface_D[i] = new EdifPort(FDEInterface[i], "D", 1, 1);
					FDEInterface_CE[i] = new EdifPort(FDEInterface[i], "CE", 1, 1);
					FDEInterface_Q[i] = new EdifPort(FDEInterface[i], "Q", 1, 2);
					FDEInterface[i].addPort("C", 1, 1);
					FDEInterface[i].addPort("D", 1, 1);
					FDEInterface[i].addPort("CE", 1, 1);
					FDEInterface[i].addPort("Q", 1, 2);
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
				EdifPortRef RAM16X4S_replacement_WE = new EdifPortRef(we, RAM16X4S_replacement_interface_WE.getSingleBitPort(0), null);
				EdifPortRef RAM16X4S_replacement_D0 = new EdifPortRef(d0, RAM16X4S_replacement_interface_D0.getSingleBitPort(0), null);
				EdifPortRef RAM16X4S_replacement_D1 = new EdifPortRef(d1, RAM16X4S_replacement_interface_D1.getSingleBitPort(0), null);
				EdifPortRef RAM16X4S_replacement_D2 = new EdifPortRef(d2, RAM16X4S_replacement_interface_D2.getSingleBitPort(0), null);
				EdifPortRef RAM16X4S_replacement_D3 = new EdifPortRef(d3, RAM16X4S_replacement_interface_D3.getSingleBitPort(0), null);
				EdifPortRef RAM16X4S_replacement_WCLK = new EdifPortRef(wclk, RAM16X4S_replacement_interface_WCLK.getSingleBitPort(0), null);
				EdifPortRef RAM16X4S_replacement_A0 = new EdifPortRef(a0, RAM16X4S_replacement_interface_A0.getSingleBitPort(0), null);
				EdifPortRef RAM16X4S_replacement_A1 = new EdifPortRef(a1, RAM16X4S_replacement_interface_A1.getSingleBitPort(0), null);
				EdifPortRef RAM16X4S_replacement_A2 = new EdifPortRef(a2, RAM16X4S_replacement_interface_A2.getSingleBitPort(0), null);
				EdifPortRef RAM16X4S_replacement_A3 = new EdifPortRef(a3, RAM16X4S_replacement_interface_A3.getSingleBitPort(0), null);
				EdifPortRef RAM16X4S_replacement_O0 = new EdifPortRef(o0, RAM16X4S_replacement_interface_O0.getSingleBitPort(0), null);
				EdifPortRef RAM16X4S_replacement_O1 = new EdifPortRef(o1, RAM16X4S_replacement_interface_O1.getSingleBitPort(0), null);
				EdifPortRef RAM16X4S_replacement_O2 = new EdifPortRef(o2, RAM16X4S_replacement_interface_O2.getSingleBitPort(0), null);
				EdifPortRef RAM16X4S_replacement_O3 = new EdifPortRef(o3, RAM16X4S_replacement_interface_O3.getSingleBitPort(0), null);
				//Port reference of buffers
				EdifPortRef WE_ibuf_I = new EdifPortRef(we, WE_ibuf_interface_I.getSingleBitPort(0), WE_ibuf);
				EdifPortRef WE_ibuf_O = new EdifPortRef(we_c, WE_ibuf_interface_O.getSingleBitPort(0), WE_ibuf);
				EdifPortRef D0_ibuf_I = new EdifPortRef(d0, D0_ibuf_interface_I.getSingleBitPort(0), D0_ibuf);
				EdifPortRef D0_ibuf_O = new EdifPortRef(d0_c, D0_ibuf_interface_O.getSingleBitPort(0), D0_ibuf);
				EdifPortRef D1_ibuf_I = new EdifPortRef(d1, D1_ibuf_interface_I.getSingleBitPort(0), D1_ibuf);
				EdifPortRef D1_ibuf_O = new EdifPortRef(d1_c, D1_ibuf_interface_O.getSingleBitPort(0), D1_ibuf);
				EdifPortRef D2_ibuf_I = new EdifPortRef(d2, D2_ibuf_interface_I.getSingleBitPort(0), D2_ibuf);
				EdifPortRef D2_ibuf_O = new EdifPortRef(d2_c, D2_ibuf_interface_O.getSingleBitPort(0), D2_ibuf);
				EdifPortRef D3_ibuf_I = new EdifPortRef(d3, D3_ibuf_interface_I.getSingleBitPort(0), D3_ibuf);
				EdifPortRef D3_ibuf_O = new EdifPortRef(d3_c, D3_ibuf_interface_O.getSingleBitPort(0), D3_ibuf);
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
				EdifPortRef O2_obuf_I = new EdifPortRef(o2_c, O2_obuf_interface_I.getSingleBitPort(0), O2_obuf);
				EdifPortRef O2_obuf_O = new EdifPortRef(o2, O2_obuf_interface_O.getSingleBitPort(0), O2_obuf);
				EdifPortRef O3_obuf_I = new EdifPortRef(o3_c, O3_obuf_interface_I.getSingleBitPort(0), O3_obuf);
				EdifPortRef O3_obuf_O = new EdifPortRef(o3, O3_obuf_interface_O.getSingleBitPort(0), O3_obuf);
				//Port reference of FDEs
				EdifPortRef[] FDE_C = new EdifPortRef[FDENUM];
				EdifPortRef[] FDE_D = new EdifPortRef[FDENUM];
				EdifPortRef[] FDE_CE = new EdifPortRef[FDENUM];
				EdifPortRef[] FDE_Q = new EdifPortRef[FDENUM];
				for(int i=0; i<FDENUM; i++)
				{
					int d = i/16;
					int q = i%16;
					FDE_C[i] = new EdifPortRef(wclk_c, FDEInterface_C[i].getSingleBitPort(0), FDE_[i]);
					FDE_CE[i] = new EdifPortRef(e[q], FDEInterface_CE[i].getSingleBitPort(0), FDE_[i]);
					FDE_Q[i] = new EdifPortRef(wire[i], FDEInterface_Q[i].getSingleBitPort(0), FDE_[i]);
					if(d==0)
						FDE_D[i] = new EdifPortRef(d0_c, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
					else if(d==1)
						FDE_D[i] = new EdifPortRef(d1_c, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
					else if(d==2)
						FDE_D[i] = new EdifPortRef(d2_c, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
					else
						FDE_D[i] = new EdifPortRef(d3_c, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
				}
				//Port reference of LUT2s
				EdifPortRef[] LUT2_I0 = new EdifPortRef[LUT2NUM];
				EdifPortRef[] LUT2_I1 = new EdifPortRef[LUT2NUM];
				EdifPortRef[] LUT2_O = new EdifPortRef[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					LUT2_I0[i] = new EdifPortRef(a3_c, LUT2Interface_I0[i].getSingleBitPort(0), LUT2_[i]);
					LUT2_I1[i] = new EdifPortRef(we_c, LUT2Interface_I1[i].getSingleBitPort(0), LUT2_[i]);
					LUT2_O[i] = new EdifPortRef(net[14*(LUT3NUM/9)+i], LUT2Interface_O[i].getSingleBitPort(0), LUT2_[i]);
				}
				//Port reference of LUT3s
				EdifPortRef[] LUT3_I0 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_I1 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_I2 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_O = new EdifPortRef[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					int d = i/9;
					int q = i%9;
					if(q!=8)
					{
						LUT3_I0[i] = new EdifPortRef(a3_c, LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_I1[i] = new EdifPortRef(wire[q+16*d], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_I2[i] = new EdifPortRef(wire[q+16*d+8], LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_O[i] = new EdifPortRef(net[q+14*d], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
					}
					else
					{
						LUT3_I0[i] = new EdifPortRef(net[12+14*d], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_I1[i] = new EdifPortRef(net[13+14*d], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_I2[i] = new EdifPortRef(a0_c, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
						if(d==0)
							LUT3_O[i] = new EdifPortRef(o0_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						else if(d==1)
							LUT3_O[i] = new EdifPortRef(o1_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						else if(d==2)
							LUT3_O[i] = new EdifPortRef(o2_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						else
							LUT3_O[i] = new EdifPortRef(o3_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
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
					int d = i/8;
					if(d==0)
						LUT4_I0[i] = new EdifPortRef(net[NETNUM-2], LUT4Interface_I0[i].getSingleBitPort(0), LUT4_[i]);
					else
						LUT4_I0[i] = new EdifPortRef(net[NETNUM-1], LUT4Interface_I0[i].getSingleBitPort(0), LUT4_[i]);
					LUT4_I1[i] = new EdifPortRef(a0_c, LUT4Interface_I1[i].getSingleBitPort(0), LUT4_[i]);
					LUT4_I2[i] = new EdifPortRef(a1_c, LUT4Interface_I2[i].getSingleBitPort(0), LUT4_[i]);
					LUT4_I3[i] = new EdifPortRef(a2_c, LUT4Interface_I3[i].getSingleBitPort(0), LUT4_[i]);
					LUT4_O[i] = new EdifPortRef(e[i], LUT4Interface_O[i].getSingleBitPort(0), LUT4_[i]);
				}
				//Port reference of MUXF5s
				EdifPortRef[] MUXF5_I0 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF5_I1 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF5_S = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF5_O = new EdifPortRef[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					int q = i%4;
					int d = i/4;
					if(q==0)
					{
						MUXF5_I0[i] = new EdifPortRef(net[0+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_I1[i] = new EdifPortRef(net[4+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_S[i] = new EdifPortRef(a2_c, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_O[i] = new EdifPortRef(net[8+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
					}
					else if(q==1)
					{
						MUXF5_I0[i] = new EdifPortRef(net[2+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_I1[i] = new EdifPortRef(net[6+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_S[i] = new EdifPortRef(a2_c, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_O[i] = new EdifPortRef(net[9+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
					}
					else if(q==2)
					{
						MUXF5_I0[i] = new EdifPortRef(net[1+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_I1[i] = new EdifPortRef(net[5+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_S[i] = new EdifPortRef(a2_c, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_O[i] = new EdifPortRef(net[10+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
					}
					else
					{
						MUXF5_I0[i] = new EdifPortRef(net[3+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_I1[i] = new EdifPortRef(net[7+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_S[i] = new EdifPortRef(a2_c, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_O[i] = new EdifPortRef(net[11+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
					}
				}
				//Port reference of MUXF6s
				EdifPortRef[] MUXF6_I0 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_I1 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_S = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_O = new EdifPortRef[MUXF5NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					int d = i/2;
					int q = i%2;
					if(q==0)
					{
						MUXF6_I0[i] = new EdifPortRef(net[8+14*d], MUXF6Interface_I0[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_I1[i] = new EdifPortRef(net[9+14*d], MUXF6Interface_I1[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_S[i] = new EdifPortRef(a1_c, MUXF6Interface_S[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_O[i] = new EdifPortRef(net[12+14*d], MUXF6Interface_O[i].getSingleBitPort(0), MUXF6_[i]);
					}
					else if(q==1)
					{
						MUXF6_I0[i] = new EdifPortRef(net[10+14*d], MUXF6Interface_I0[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_I1[i] = new EdifPortRef(net[11+14*d], MUXF6Interface_I1[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_S[i] = new EdifPortRef(a1_c, MUXF6Interface_S[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_O[i] = new EdifPortRef(net[13+14*d], MUXF6Interface_O[i].getSingleBitPort(0), MUXF6_[i]);
					}
				}
				//we
				we.addPortConnection(RAM16X4S_replacement_WE);
				we.addPortConnection(WE_ibuf_I);
				//we_c
				we_c.addPortConnection(WE_ibuf_O);
				for(int i=0; i<LUT2NUM;i++)
				{
					we_c.addPortConnection(LUT2_I1[i]);
				}
				//d0
				d0.addPortConnection(RAM16X4S_replacement_D0);
				d0.addPortConnection(D0_ibuf_I);
				//d0_c
				d0_c.addPortConnection(D0_ibuf_O);
				for(int i=0; i<16; i++)
				{
					d0_c.addPortConnection(FDE_D[i]);
				}
				//d1
				d1.addPortConnection(RAM16X4S_replacement_D1);
				d1.addPortConnection(D1_ibuf_I);
				//d1_c
				d1_c.addPortConnection(D1_ibuf_O);
				for(int i=16; i<32; i++)
				{
					d1_c.addPortConnection(FDE_D[i]);
				}
				//d2
				d2.addPortConnection(RAM16X4S_replacement_D2);
				d2.addPortConnection(D2_ibuf_I);
				//d2_c
				d2_c.addPortConnection(D2_ibuf_O);
				for(int i=32; i<48; i++)
				{
					d2_c.addPortConnection(FDE_D[i]);
				}
				//d3
				d3.addPortConnection(RAM16X4S_replacement_D3);
				d3.addPortConnection(D3_ibuf_I);
				//d3_c
				d3_c.addPortConnection(D3_ibuf_O);
				for(int i=48; i<64; i++)
				{
					d3_c.addPortConnection(FDE_D[i]);
				}
				//wclk
				wclk.addPortConnection(WCLK_ibuf_I);
				wclk.addPortConnection(RAM16X4S_replacement_WCLK);
				//wclk_c
				wclk_c.addPortConnection(WCLK_ibuf_O);
				for(int i=0; i<FDENUM; i++)
				{
					wclk_c.addPortConnection(FDE_C[i]);
				}
				//a0
				a0.addPortConnection(RAM16X4S_replacement_A0);
				a0.addPortConnection(A0_ibuf_I);
				//a0_c
				a0_c.addPortConnection(A0_ibuf_O);
				for(int i=0; i<LUT3NUM; i++)
				{
					if((i%9)==8)
						a0_c.addPortConnection(LUT3_I2[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a0_c.addPortConnection(LUT4_I1[i]);
				}
				//a1
				a1.addPortConnection(RAM16X4S_replacement_A1);
				a1.addPortConnection(A1_ibuf_I);
				//a1_c
				a1_c.addPortConnection(A1_ibuf_O);
				for(int i=0; i<MUXF6NUM; i++)
				{
					a1_c.addPortConnection(MUXF6_S[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a1_c.addPortConnection(LUT4_I2[i]);
				}
				//a2
				a2.addPortConnection(RAM16X4S_replacement_A2);
				a2.addPortConnection(A2_ibuf_I);
				//a2_c
				a2_c.addPortConnection(A2_ibuf_O);
				for(int i=0; i<MUXF5NUM; i++)
				{
					a2_c.addPortConnection(MUXF5_S[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a2_c.addPortConnection(LUT4_I3[i]);
				}
				//a3
				a3.addPortConnection(RAM16X4S_replacement_A3);
				a3.addPortConnection(A3_ibuf_I);
				//a3_c
				a3_c.addPortConnection(A3_ibuf_O);
				for(int i=0; i<LUT3NUM; i++)
				{
					if((i%9)!=8)
						a3_c.addPortConnection(LUT3_I0[i]);
				}
				for(int i=0; i<LUT2NUM; i++)
				{
					a3_c.addPortConnection(LUT2_I0[i]);
				}
				//o0
				o0.addPortConnection(O0_obuf_O);
				o0.addPortConnection(RAM16X4S_replacement_O0);
				//o0_c
				o0_c.addPortConnection(O0_obuf_I);
				o0_c.addPortConnection(LUT3_O[8]);
				//o1
				o1.addPortConnection(O1_obuf_O);
				o1.addPortConnection(RAM16X4S_replacement_O1);
				//o1_c
				o1_c.addPortConnection(O1_obuf_I);
				o1_c.addPortConnection(LUT3_O[17]);
				//o2
				o2.addPortConnection(O2_obuf_O);
				o2.addPortConnection(RAM16X4S_replacement_O2);
				//o2_c
				o2_c.addPortConnection(O2_obuf_I);
				o2_c.addPortConnection(LUT3_O[26]);
				//o3
				o3.addPortConnection(O3_obuf_O);
				o3.addPortConnection(RAM16X4S_replacement_O3);
				//o3_c
				o3_c.addPortConnection(O3_obuf_I);
				o3_c.addPortConnection(LUT3_O[35]);
				//wires
				for(int i=0; i<FDENUM; i++)
				{
					int d = i/16;
					int q = i%16;
					wire[i].addPortConnection(FDE_Q[i]);
					if(q<8)
						wire[i].addPortConnection(LUT3_I1[d*9+q]);
					else
						wire[i].addPortConnection(LUT3_I2[d*9+q-8]);
				}
				//e
				for(int i=0; i<ENUM; i++)
				{
					e[i].addPortConnection(LUT4_O[i]);
					e[i].addPortConnection(FDE_CE[i]);
					e[i].addPortConnection(FDE_CE[i+16]);
					e[i].addPortConnection(FDE_CE[i+32]);
					e[i].addPortConnection(FDE_CE[i+48]);
				}
				//nets
				for(int i=0; i<NETNUM; i++)
				{
					int d = i/14;
					int q = i%14;
					if(i<NETNUM-2)	//nets in MUX
					{
						if(q==0)
						{
							net[i].addPortConnection(LUT3_O[d*9]);
							net[i].addPortConnection(MUXF5_I0[d*4]);
						}
						else if(q==1)
						{
							net[i].addPortConnection(LUT3_O[d*9+1]);
							net[i].addPortConnection(MUXF5_I0[d*4+2]);
						}
						else if(q==2)
						{
							net[i].addPortConnection(LUT3_O[d*9+2]);
							net[i].addPortConnection(MUXF5_I0[d*4+1]);
						}
						else if(q==3)
						{
							net[i].addPortConnection(LUT3_O[d*9+3]);
							net[i].addPortConnection(MUXF5_I0[d*4+3]);
						}
						else if(q==4)
						{
							net[i].addPortConnection(LUT3_O[d*9+4]);
							net[i].addPortConnection(MUXF5_I1[d*4]);
						}
						else if(q==5)
						{
							net[i].addPortConnection(LUT3_O[d*9+5]);
							net[i].addPortConnection(MUXF5_I1[d*4+2]);
						}
						else if(q==6)
						{
							net[i].addPortConnection(LUT3_O[d*9+6]);
							net[i].addPortConnection(MUXF5_I1[d*4+1]);
						}
						else if(q==7)
						{
							net[i].addPortConnection(LUT3_O[d*9+7]);
							net[i].addPortConnection(MUXF5_I1[d*4+3]);
						}
						else if(q==8)
						{
							net[i].addPortConnection(MUXF5_O[d*4]);
							net[i].addPortConnection(MUXF6_I0[d*2]);
						}
						else if(q==9)
						{
							net[i].addPortConnection(MUXF5_O[d*4+1]);
							net[i].addPortConnection(MUXF6_I1[d*2]);
						}
						else if(q==10)
						{
							net[i].addPortConnection(MUXF5_O[d*4+2]);
							net[i].addPortConnection(MUXF6_I0[d*2+1]);
						}
						else if(q==11)
						{
							net[i].addPortConnection(MUXF5_O[d*4+3]);
							net[i].addPortConnection(MUXF6_I1[d*2+1]);
						}
						else if(q==12)
						{
							net[i].addPortConnection(MUXF6_O[d*2]);
							net[i].addPortConnection(LUT3_I0[d*9+8]);
						}
						else
						{
							net[i].addPortConnection(MUXF6_O[d*2+1]);
							net[i].addPortConnection(LUT3_I1[d*9+8]);
						}
					}
					else			//nets in decoder
					{
						if(i==NETNUM-2)
						{
							net[i].addPortConnection(LUT2_O[0]);
							for(int j=0; j<LUT4NUM/2; j++)
								net[i].addPortConnection(LUT4_I0[j]);
						}
						else
						{
							net[i].addPortConnection(LUT2_O[1]);
							for(int j=LUT4NUM/2; j<LUT4NUM; j++)
								net[i].addPortConnection(LUT4_I0[j]);
						}
					}
				}
				
				/******Set INIT Property******/
				boolean isInit;
				int[] initCount = new int[4];
				long[] INIT = new long[4];
		        initCount[0] = 0;
		        initCount[1] = 0;
		        initCount[2] = 0;
		        initCount[3] = 0;
		        INIT[0] = 63;
		        INIT[1] = 37;
		        INIT[2] = 0;
		        INIT[3] = 0;
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
		        	int q = i%9;
		        	isInit = false;
		        	LUT3_propertylist[i] = LUT3_[i].getPropertyList();
		        	if(LUT3_propertylist[i] != null)
		        	{
		        		for(Property LUT3_property : LUT3_propertylist[i].values())
		        		{
		        			if(LUT3_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if(q==8)
		        					LUT3_property.setValue(valueCA);
		        				else
		        					LUT3_property.setValue(valueE4);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if(q==8)
		        			LUT3_[i].addProperty(new Property("INIT", (EdifTypedValue)valueCA));
		        		else
		        			LUT3_[i].addProperty(new Property("INIT", (EdifTypedValue)valueE4));
		        	}
		        }
		        //Set INIT Property for LUT2s
		        PropertyList[] LUT2_propertylist = new PropertyList[LUT2NUM];
		        for(int i=0; i<LUT2NUM; i++)
		        {
		        	int q = i%2;
		        	isInit = false;
		        	LUT2_propertylist[i] = LUT2_[i].getPropertyList();
		        	if(LUT2_propertylist[i] != null)
		        	{
		        		for(Property LUT2_property : LUT2_propertylist[i].values())
		        		{
		        			if(LUT2_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if(q==0)
		        					LUT2_property.setValue(value4);
		        				else
		        					LUT2_property.setValue(value8);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if(q==0)
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
		        //Set INIT Property for FDEs
		        PropertyList[] FDE_propertylist = new PropertyList[FDENUM];
		        for(int i=0; i<FDENUM; i++)
		        {
		        	int d = i/16;
		        	isInit = false;
		        	FDE_propertylist[i] = FDE_[i].getPropertyList();
		        	if(FDE_propertylist[i] != null)
		        	{
		        		for(Property FDE_property : FDE_propertylist[i].values())
		        		{
		        			if(FDE_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if (((INIT[d] >> initCount[d]++) & 1) == 1)
	                                FDE_property.setValue(valueOne);
	                            else
	                                FDE_property.setValue(valueZero);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if (((INIT[d] >> initCount[d]++) & 1) == 1)
                            FDE_[i].addProperty(new Property("INIT", (EdifTypedValue)valueOne));
                        else
                        	FDE_[i].addProperty(new Property("INIT", (EdifTypedValue)valueZero));
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
	
	public static void Replace(EdifLibraryManager libManager, EdifCell RAM16X4S_replacement, 
			long INIT_00, long INIT_01, long INIT_02, long INIT_03,
			EdifNet we, EdifNet d0, EdifNet d1, EdifNet d2, EdifNet d3, EdifNet wclk, 
			EdifNet a0, EdifNet a1, EdifNet a2, EdifNet a3, EdifNet o0, EdifNet o1, EdifNet o2, EdifNet o3) {
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
				//Constants
				int WIRENUM = 64;
				int NETNUM = 58;
				int ENUM = 16;
				//Declaration of wires, which are the outputs of FDEs
				EdifNet[] wire = new EdifNet[WIRENUM];
				for(int i=0; i<WIRENUM; i++)
				{
					String wirename;
					wirename = "wire" + String.valueOf(i);
					wire[i] = new EdifNet(wirename, RAM16X4S_replacement);
				}
				//Declaration of nets, which are used in MUX and decoder
				EdifNet[] net = new EdifNet[NETNUM];
				for(int i=0; i<NETNUM; i++)
				{
					String netname;
					netname = "net" + String.valueOf(i);
					net[i] = new EdifNet(netname, RAM16X4S_replacement);
				}
				//Declaration of enables
				EdifNet[] e = new EdifNet[ENUM];
				for(int i=0; i<ENUM; i++)
				{
					String ename;
					ename = "e" + String.valueOf(i);
					e[i] = new EdifNet(ename, RAM16X4S_replacement);
				}
				//Add all nets to the top level design
				for(int i=0; i<WIRENUM; i++)
				{
					RAM16X4S_replacement.addNet(wire[i]);
				}
				for(int i=0; i<NETNUM; i++)
				{
					RAM16X4S_replacement.addNet(net[i]);
				}
				for(int i=0; i<ENUM; i++)
				{
					RAM16X4S_replacement.addNet(e[i]);
				}
				
				/******Instances******/
				//Constants
				int FDENUM = 64;
				int LUT2NUM = 2;
				int LUT3NUM = 36;
				int LUT4NUM = 16;
				int MUXF5NUM = 16;
				int MUXF6NUM = 8;
				//FDEs
				EdifCellInstance[] FDE_ = new EdifCellInstance[FDENUM];
				for(int i=0; i<FDENUM; i++)
				{
					String fdename;
					fdename = "FDE" + String.valueOf(i);
					FDE_[i] = new EdifCellInstance(fdename, RAM16X4S_replacement, FDE);
				}
				//LUT2s
				EdifCellInstance[] LUT2_ = new EdifCellInstance[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					String lut2name;
					lut2name = "LUT2_" + String.valueOf(i);
					LUT2_[i] = new EdifCellInstance(lut2name, RAM16X4S_replacement, LUT2);
				}
				//LUT3s
				EdifCellInstance[] LUT3_ = new EdifCellInstance[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					String lut3name;
					lut3name = "LUT3_" + String.valueOf(i);
					LUT3_[i] = new EdifCellInstance(lut3name, RAM16X4S_replacement, LUT3);
				}
				//LUT4s
				EdifCellInstance[] LUT4_ = new EdifCellInstance[LUT4NUM];
				for(int i=0; i<LUT4NUM; i++)
				{
					String lut4name;
					lut4name = "LUT4_" + String.valueOf(i);
					LUT4_[i] = new EdifCellInstance(lut4name, RAM16X4S_replacement, LUT4);
				}
				//MUXF5
				EdifCellInstance[] MUXF5_ = new EdifCellInstance[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					String muxf5name;
					muxf5name = "MUXF5_" + String.valueOf(i);
					MUXF5_[i] = new EdifCellInstance(muxf5name, RAM16X4S_replacement, MUXF5);
				}
				//MUXF6
				EdifCellInstance[] MUXF6_ = new EdifCellInstance[MUXF6NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					String muxf6name;
					muxf6name = "MUXF6_" + String.valueOf(i);
					MUXF6_[i] = new EdifCellInstance(muxf6name, RAM16X4S_replacement, MUXF6);
				}
				//Add all instances to the top level design
				for(int i=0; i<FDENUM; i++)
				{
					RAM16X4S_replacement.addSubCell(FDE_[i]);
				}
				for(int i=0; i<LUT2NUM; i++)
				{
					RAM16X4S_replacement.addSubCell(LUT2_[i]);
				}
				for(int i=0; i<LUT3NUM; i++)
				{
					RAM16X4S_replacement.addSubCell(LUT3_[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					RAM16X4S_replacement.addSubCell(LUT4_[i]);
				}
				for(int i=0; i<MUXF5NUM; i++)
				{
					RAM16X4S_replacement.addSubCell(MUXF5_[i]);
				}
				for(int i=0; i<MUXF6NUM; i++)
				{
					RAM16X4S_replacement.addSubCell(MUXF6_[i]);
				}
				
				/******Interface******/
				//Interface of FDEs
				EdifCellInterface FDEInterface[] = new EdifCellInterface[FDENUM];
				for(int i=0; i<FDENUM; i++)
				{
					FDEInterface[i] = new EdifCellInterface(FDE);
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
				//FDEs
				EdifPort[] FDEInterface_C= new EdifPort[FDENUM];
				EdifPort[] FDEInterface_D= new EdifPort[FDENUM];
				EdifPort[] FDEInterface_CE= new EdifPort[FDENUM];
				EdifPort[] FDEInterface_Q= new EdifPort[FDENUM];
				for(int i=0; i<FDENUM; i++)
				{
					FDEInterface_C[i] = new EdifPort(FDEInterface[i], "C", 1, 1);
					FDEInterface_D[i] = new EdifPort(FDEInterface[i], "D", 1, 1);
					FDEInterface_CE[i] = new EdifPort(FDEInterface[i], "CE", 1, 1);
					FDEInterface_Q[i] = new EdifPort(FDEInterface[i], "Q", 1, 2);
					FDEInterface[i].addPort("C", 1, 1);
					FDEInterface[i].addPort("D", 1, 1);
					FDEInterface[i].addPort("CE", 1, 1);
					FDEInterface[i].addPort("Q", 1, 2);
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
				//Port reference of FDEs
				EdifPortRef[] FDE_C = new EdifPortRef[FDENUM];
				EdifPortRef[] FDE_D = new EdifPortRef[FDENUM];
				EdifPortRef[] FDE_CE = new EdifPortRef[FDENUM];
				EdifPortRef[] FDE_Q = new EdifPortRef[FDENUM];
				for(int i=0; i<FDENUM; i++)
				{
					int d = i/16;
					int q = i%16;
					FDE_C[i] = new EdifPortRef(wclk, FDEInterface_C[i].getSingleBitPort(0), FDE_[i]);
					FDE_CE[i] = new EdifPortRef(e[q], FDEInterface_CE[i].getSingleBitPort(0), FDE_[i]);
					FDE_Q[i] = new EdifPortRef(wire[i], FDEInterface_Q[i].getSingleBitPort(0), FDE_[i]);
					if(d==0)
						FDE_D[i] = new EdifPortRef(d0, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
					else if(d==1)
						FDE_D[i] = new EdifPortRef(d1, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
					else if(d==2)
						FDE_D[i] = new EdifPortRef(d2, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
					else
						FDE_D[i] = new EdifPortRef(d3, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
				}
				//Port reference of LUT2s
				EdifPortRef[] LUT2_I0 = new EdifPortRef[LUT2NUM];
				EdifPortRef[] LUT2_I1 = new EdifPortRef[LUT2NUM];
				EdifPortRef[] LUT2_O = new EdifPortRef[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					LUT2_I0[i] = new EdifPortRef(a3, LUT2Interface_I0[i].getSingleBitPort(0), LUT2_[i]);
					LUT2_I1[i] = new EdifPortRef(we, LUT2Interface_I1[i].getSingleBitPort(0), LUT2_[i]);
					LUT2_O[i] = new EdifPortRef(net[14*(LUT3NUM/9)+i], LUT2Interface_O[i].getSingleBitPort(0), LUT2_[i]);
				}
				//Port reference of LUT3s
				EdifPortRef[] LUT3_I0 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_I1 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_I2 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_O = new EdifPortRef[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					int d = i/9;
					int q = i%9;
					if(q!=8)
					{
						LUT3_I0[i] = new EdifPortRef(a3, LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_I1[i] = new EdifPortRef(wire[q+16*d], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_I2[i] = new EdifPortRef(wire[q+16*d+8], LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_O[i] = new EdifPortRef(net[q+14*d], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
					}
					else
					{
						LUT3_I0[i] = new EdifPortRef(net[12+14*d], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_I1[i] = new EdifPortRef(net[13+14*d], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
						LUT3_I2[i] = new EdifPortRef(a0, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
						if(d==0)
							LUT3_O[i] = new EdifPortRef(o0, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						else if(d==1)
							LUT3_O[i] = new EdifPortRef(o1, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						else if(d==2)
							LUT3_O[i] = new EdifPortRef(o2, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						else
							LUT3_O[i] = new EdifPortRef(o3, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
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
					int d = i/8;
					if(d==0)
						LUT4_I0[i] = new EdifPortRef(net[NETNUM-2], LUT4Interface_I0[i].getSingleBitPort(0), LUT4_[i]);
					else
						LUT4_I0[i] = new EdifPortRef(net[NETNUM-1], LUT4Interface_I0[i].getSingleBitPort(0), LUT4_[i]);
					LUT4_I1[i] = new EdifPortRef(a0, LUT4Interface_I1[i].getSingleBitPort(0), LUT4_[i]);
					LUT4_I2[i] = new EdifPortRef(a1, LUT4Interface_I2[i].getSingleBitPort(0), LUT4_[i]);
					LUT4_I3[i] = new EdifPortRef(a2, LUT4Interface_I3[i].getSingleBitPort(0), LUT4_[i]);
					LUT4_O[i] = new EdifPortRef(e[i], LUT4Interface_O[i].getSingleBitPort(0), LUT4_[i]);
				}
				//Port reference of MUXF5s
				EdifPortRef[] MUXF5_I0 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF5_I1 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF5_S = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF5_O = new EdifPortRef[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					int q = i%4;
					int d = i/4;
					if(q==0)
					{
						MUXF5_I0[i] = new EdifPortRef(net[0+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_I1[i] = new EdifPortRef(net[4+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_S[i] = new EdifPortRef(a2, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_O[i] = new EdifPortRef(net[8+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
					}
					else if(q==1)
					{
						MUXF5_I0[i] = new EdifPortRef(net[2+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_I1[i] = new EdifPortRef(net[6+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_S[i] = new EdifPortRef(a2, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_O[i] = new EdifPortRef(net[9+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
					}
					else if(q==2)
					{
						MUXF5_I0[i] = new EdifPortRef(net[1+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_I1[i] = new EdifPortRef(net[5+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_S[i] = new EdifPortRef(a2, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_O[i] = new EdifPortRef(net[10+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
					}
					else
					{
						MUXF5_I0[i] = new EdifPortRef(net[3+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_I1[i] = new EdifPortRef(net[7+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_S[i] = new EdifPortRef(a2, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_O[i] = new EdifPortRef(net[11+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
					}
				}
				//Port reference of MUXF6s
				EdifPortRef[] MUXF6_I0 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_I1 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_S = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_O = new EdifPortRef[MUXF5NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					int d = i/2;
					int q = i%2;
					if(q==0)
					{
						MUXF6_I0[i] = new EdifPortRef(net[8+14*d], MUXF6Interface_I0[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_I1[i] = new EdifPortRef(net[9+14*d], MUXF6Interface_I1[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_S[i] = new EdifPortRef(a1, MUXF6Interface_S[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_O[i] = new EdifPortRef(net[12+14*d], MUXF6Interface_O[i].getSingleBitPort(0), MUXF6_[i]);
					}
					else if(q==1)
					{
						MUXF6_I0[i] = new EdifPortRef(net[10+14*d], MUXF6Interface_I0[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_I1[i] = new EdifPortRef(net[11+14*d], MUXF6Interface_I1[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_S[i] = new EdifPortRef(a1, MUXF6Interface_S[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_O[i] = new EdifPortRef(net[13+14*d], MUXF6Interface_O[i].getSingleBitPort(0), MUXF6_[i]);
					}
				}
				//we
				for(int i=0; i<LUT2NUM;i++)
				{
					we.addPortConnection(LUT2_I1[i]);
				}
				//d0
				for(int i=0; i<16; i++)
				{
					d0.addPortConnection(FDE_D[i]);
				}
				//d1
				for(int i=16; i<32; i++)
				{
					d1.addPortConnection(FDE_D[i]);
				}
				//d2
				for(int i=32; i<48; i++)
				{
					d2.addPortConnection(FDE_D[i]);
				}
				//d3
				for(int i=48; i<64; i++)
				{
					d3.addPortConnection(FDE_D[i]);
				}
				//wclk
				for(int i=0; i<FDENUM; i++)
				{
					wclk.addPortConnection(FDE_C[i]);
				}
				//a0
				for(int i=0; i<LUT3NUM; i++)
				{
					if((i%9)==8)
						a0.addPortConnection(LUT3_I2[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a0.addPortConnection(LUT4_I1[i]);
				}
				//a1
				for(int i=0; i<MUXF6NUM; i++)
				{
					a1.addPortConnection(MUXF6_S[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a1.addPortConnection(LUT4_I2[i]);
				}
				//a2
				for(int i=0; i<MUXF5NUM; i++)
				{
					a2.addPortConnection(MUXF5_S[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a2.addPortConnection(LUT4_I3[i]);
				}
				//a3
				for(int i=0; i<LUT3NUM; i++)
				{
					if((i%9)!=8)
						a3.addPortConnection(LUT3_I0[i]);
				}
				for(int i=0; i<LUT2NUM; i++)
				{
					a3.addPortConnection(LUT2_I0[i]);
				}
				//o0
				o0.addPortConnection(LUT3_O[8]);
				//o1
				o1.addPortConnection(LUT3_O[17]);
				//o2
				o2.addPortConnection(LUT3_O[26]);
				//o3
				o3.addPortConnection(LUT3_O[35]);
				//wires
				for(int i=0; i<FDENUM; i++)
				{
					int d = i/16;
					int q = i%16;
					wire[i].addPortConnection(FDE_Q[i]);
					if(q<8)
						wire[i].addPortConnection(LUT3_I1[d*9+q]);
					else
						wire[i].addPortConnection(LUT3_I2[d*9+q-8]);
				}
				//e
				for(int i=0; i<ENUM; i++)
				{
					e[i].addPortConnection(LUT4_O[i]);
					e[i].addPortConnection(FDE_CE[i]);
					e[i].addPortConnection(FDE_CE[i+16]);
					e[i].addPortConnection(FDE_CE[i+32]);
					e[i].addPortConnection(FDE_CE[i+48]);
				}
				//nets
				for(int i=0; i<NETNUM; i++)
				{
					int d = i/14;
					int q = i%14;
					if(i<NETNUM-2)	//nets in MUX
					{
						if(q==0)
						{
							net[i].addPortConnection(LUT3_O[d*9]);
							net[i].addPortConnection(MUXF5_I0[d*4]);
						}
						else if(q==1)
						{
							net[i].addPortConnection(LUT3_O[d*9+1]);
							net[i].addPortConnection(MUXF5_I0[d*4+2]);
						}
						else if(q==2)
						{
							net[i].addPortConnection(LUT3_O[d*9+2]);
							net[i].addPortConnection(MUXF5_I0[d*4+1]);
						}
						else if(q==3)
						{
							net[i].addPortConnection(LUT3_O[d*9+3]);
							net[i].addPortConnection(MUXF5_I0[d*4+3]);
						}
						else if(q==4)
						{
							net[i].addPortConnection(LUT3_O[d*9+4]);
							net[i].addPortConnection(MUXF5_I1[d*4]);
						}
						else if(q==5)
						{
							net[i].addPortConnection(LUT3_O[d*9+5]);
							net[i].addPortConnection(MUXF5_I1[d*4+2]);
						}
						else if(q==6)
						{
							net[i].addPortConnection(LUT3_O[d*9+6]);
							net[i].addPortConnection(MUXF5_I1[d*4+1]);
						}
						else if(q==7)
						{
							net[i].addPortConnection(LUT3_O[d*9+7]);
							net[i].addPortConnection(MUXF5_I1[d*4+3]);
						}
						else if(q==8)
						{
							net[i].addPortConnection(MUXF5_O[d*4]);
							net[i].addPortConnection(MUXF6_I0[d*2]);
						}
						else if(q==9)
						{
							net[i].addPortConnection(MUXF5_O[d*4+1]);
							net[i].addPortConnection(MUXF6_I1[d*2]);
						}
						else if(q==10)
						{
							net[i].addPortConnection(MUXF5_O[d*4+2]);
							net[i].addPortConnection(MUXF6_I0[d*2+1]);
						}
						else if(q==11)
						{
							net[i].addPortConnection(MUXF5_O[d*4+3]);
							net[i].addPortConnection(MUXF6_I1[d*2+1]);
						}
						else if(q==12)
						{
							net[i].addPortConnection(MUXF6_O[d*2]);
							net[i].addPortConnection(LUT3_I0[d*9+8]);
						}
						else
						{
							net[i].addPortConnection(MUXF6_O[d*2+1]);
							net[i].addPortConnection(LUT3_I1[d*9+8]);
						}
					}
					else			//nets in decoder
					{
						if(i==NETNUM-2)
						{
							net[i].addPortConnection(LUT2_O[0]);
							for(int j=0; j<LUT4NUM/2; j++)
								net[i].addPortConnection(LUT4_I0[j]);
						}
						else
						{
							net[i].addPortConnection(LUT2_O[1]);
							for(int j=LUT4NUM/2; j<LUT4NUM; j++)
								net[i].addPortConnection(LUT4_I0[j]);
						}
					}
				}
				
				/******Set INIT Property******/
				boolean isInit;
				int[] initCount = new int[4];
				long[] INIT = new long[4];
		        initCount[0] = 0;
		        initCount[1] = 0;
		        initCount[2] = 0;
		        initCount[3] = 0;
		        INIT[0] = INIT_00;
		        INIT[1] = INIT_01;
		        INIT[2] = INIT_02;
		        INIT[3] = INIT_03;
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
		        	int q = i%9;
		        	isInit = false;
		        	LUT3_propertylist[i] = LUT3_[i].getPropertyList();
		        	if(LUT3_propertylist[i] != null)
		        	{
		        		for(Property LUT3_property : LUT3_propertylist[i].values())
		        		{
		        			if(LUT3_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if(q==8)
		        					LUT3_property.setValue(valueCA);
		        				else
		        					LUT3_property.setValue(valueE4);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if(q==8)
		        			LUT3_[i].addProperty(new Property("INIT", (EdifTypedValue)valueCA));
		        		else
		        			LUT3_[i].addProperty(new Property("INIT", (EdifTypedValue)valueE4));
		        	}
		        }
		        //Set INIT Property for LUT2s
		        PropertyList[] LUT2_propertylist = new PropertyList[LUT2NUM];
		        for(int i=0; i<LUT2NUM; i++)
		        {
		        	int q = i%2;
		        	isInit = false;
		        	LUT2_propertylist[i] = LUT2_[i].getPropertyList();
		        	if(LUT2_propertylist[i] != null)
		        	{
		        		for(Property LUT2_property : LUT2_propertylist[i].values())
		        		{
		        			if(LUT2_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if(q==0)
		        					LUT2_property.setValue(value4);
		        				else
		        					LUT2_property.setValue(value8);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if(q==0)
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
		        //Set INIT Property for FDEs
		        PropertyList[] FDE_propertylist = new PropertyList[FDENUM];
		        for(int i=0; i<FDENUM; i++)
		        {
		        	int d = i/16;
		        	isInit = false;
		        	FDE_propertylist[i] = FDE_[i].getPropertyList();
		        	if(FDE_propertylist[i] != null)
		        	{
		        		for(Property FDE_property : FDE_propertylist[i].values())
		        		{
		        			if(FDE_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if (((INIT[d] >> initCount[d]++) & 1) == 1)
	                                FDE_property.setValue(valueOne);
	                            else
	                                FDE_property.setValue(valueZero);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if (((INIT[d] >> initCount[d]++) & 1) == 1)
                            FDE_[i].addProperty(new Property("INIT", (EdifTypedValue)valueOne));
                        else
                        	FDE_[i].addProperty(new Property("INIT", (EdifTypedValue)valueZero));
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
