/*
 * Generates the EDIF file for RAM64X1D
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
 * Generates the EDIF file for RAM64X1D
 * Includes: 64 FDEs, 2 64-1 MUXs(39 LUT3s, 16 MUXF5s, and 8 MUXF6s for each), 1 6-64  decoder(14 LUT2s, 64 LUT4s).
 * 
 * @author Yubo Li
 *
 */

public class RAM64X1D_replacement{
	public static void main(String args[]){
		
		String outputFileName = "RAM64X1D_replacement.edf";
		
		try{
			/******Environment and Environment manager******/
			EdifEnvironment topLevel = new EdifEnvironment("RAM64X1D_replacement");
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
				EdifCell RAM64X1D_replacement = new EdifCell(work, "RAM64X1D_replacement");
				RAM64X1D_replacement.addPort("WE", 1, 1);
				RAM64X1D_replacement.addPort("D", 1, 1);
				RAM64X1D_replacement.addPort("WCLK", 1, 1);
				RAM64X1D_replacement.addPort("A0", 1, 1);
				RAM64X1D_replacement.addPort("A1", 1, 1);
				RAM64X1D_replacement.addPort("A2", 1, 1);
				RAM64X1D_replacement.addPort("A3", 1, 1);
				RAM64X1D_replacement.addPort("A4", 1, 1);
				RAM64X1D_replacement.addPort("A5", 1, 1);
				RAM64X1D_replacement.addPort("DPRA0", 1, 1);
				RAM64X1D_replacement.addPort("DPRA1", 1, 1);
				RAM64X1D_replacement.addPort("DPRA2", 1, 1);
				RAM64X1D_replacement.addPort("DPRA3", 1, 1);
				RAM64X1D_replacement.addPort("DPRA4", 1, 1);
				RAM64X1D_replacement.addPort("DPRA5", 1, 1);
				RAM64X1D_replacement.addPort("SPO", 1, 2);
				RAM64X1D_replacement.addPort("DPO", 1, 2);
				
				/******Design******/
				EdifDesign design = new EdifDesign("RAM64X1D_replacement");
				topLevel.setTopDesign(design);
				topLevel.setTopCell(RAM64X1D_replacement);
				
				/******Nets******/
				//Constants
				int WIRENUM = 64;
				int NETNUM = 138;
				int ENUM = 64;
				//Declaration of input and output ports
				EdifNet wclk_c = new EdifNet("wclk_c", RAM64X1D_replacement);
				EdifNet wclk = new EdifNet("wclk", RAM64X1D_replacement);
				EdifNet d_c = new EdifNet("d_c", RAM64X1D_replacement);
				EdifNet d = new EdifNet("d", RAM64X1D_replacement);
				EdifNet we_c = new EdifNet("we_c", RAM64X1D_replacement);
				EdifNet we = new EdifNet("we", RAM64X1D_replacement);
				EdifNet a0_c = new EdifNet("a0_c", RAM64X1D_replacement);
				EdifNet a0 = new EdifNet("a0", RAM64X1D_replacement);
				EdifNet a1_c = new EdifNet("a1_c", RAM64X1D_replacement);
				EdifNet a1 = new EdifNet("a1", RAM64X1D_replacement);
				EdifNet a2_c = new EdifNet("a2_c", RAM64X1D_replacement);
				EdifNet a2 = new EdifNet("a2", RAM64X1D_replacement);
				EdifNet a3_c = new EdifNet("a3_c", RAM64X1D_replacement);
				EdifNet a3 = new EdifNet("a3", RAM64X1D_replacement);
				EdifNet a4_c = new EdifNet("a4_c", RAM64X1D_replacement);
				EdifNet a4 = new EdifNet("a4", RAM64X1D_replacement);
				EdifNet a5_c = new EdifNet("a5_c", RAM64X1D_replacement);
				EdifNet a5 = new EdifNet("a5", RAM64X1D_replacement);
				EdifNet dpra0_c = new EdifNet("dpra0_c", RAM64X1D_replacement);
				EdifNet dpra0 = new EdifNet("dpra0", RAM64X1D_replacement);
				EdifNet dpra1_c = new EdifNet("dpra1_c", RAM64X1D_replacement);
				EdifNet dpra1 = new EdifNet("dpra1", RAM64X1D_replacement);
				EdifNet dpra2_c = new EdifNet("dpra2_c", RAM64X1D_replacement);
				EdifNet dpra2 = new EdifNet("dpra2", RAM64X1D_replacement);
				EdifNet dpra3_c = new EdifNet("dpra3_c", RAM64X1D_replacement);
				EdifNet dpra3 = new EdifNet("dpra3", RAM64X1D_replacement);
				EdifNet dpra4_c = new EdifNet("dpra4_c", RAM64X1D_replacement);
				EdifNet dpra4 = new EdifNet("dpra4", RAM64X1D_replacement);
				EdifNet dpra5_c = new EdifNet("dpra5_c", RAM64X1D_replacement);
				EdifNet dpra5 = new EdifNet("dpra5", RAM64X1D_replacement);
				EdifNet spo_c = new EdifNet("spo_c", RAM64X1D_replacement);
				EdifNet spo = new EdifNet("spo", RAM64X1D_replacement);
				EdifNet dpo_c = new EdifNet("dpo_c", RAM64X1D_replacement);
				EdifNet dpo = new EdifNet("dpo", RAM64X1D_replacement);
				//Declaration of wires, which are the outputs of FDEs
				EdifNet[] wire = new EdifNet[WIRENUM];
				for(int i=0; i<WIRENUM; i++)
				{
					String wirename;
					wirename = "wire" + String.valueOf(i);
					wire[i] = new EdifNet(wirename, RAM64X1D_replacement);
				}
				//Declaration of nets, which are used in MUX and decoder
				EdifNet[] net = new EdifNet[NETNUM];
				for(int i=0; i<NETNUM; i++)
				{
					String netname;
					netname = "net" + String.valueOf(i);
					net[i] = new EdifNet(netname, RAM64X1D_replacement);
				}
				//Declaration of enables
				EdifNet[] e = new EdifNet[ENUM];
				for(int i=0; i<ENUM; i++)
				{
					String ename;
					ename = "e" + String.valueOf(i);
					e[i] = new EdifNet(ename, RAM64X1D_replacement);
				}
				//Add all nets to the top level design
				RAM64X1D_replacement.addNet(wclk_c);
				RAM64X1D_replacement.addNet(wclk);
				RAM64X1D_replacement.addNet(d_c);
				RAM64X1D_replacement.addNet(d);
				RAM64X1D_replacement.addNet(we_c);
				RAM64X1D_replacement.addNet(we);
				RAM64X1D_replacement.addNet(a0_c);
				RAM64X1D_replacement.addNet(a0);
				RAM64X1D_replacement.addNet(a1_c);
				RAM64X1D_replacement.addNet(a1);
				RAM64X1D_replacement.addNet(a2_c);
				RAM64X1D_replacement.addNet(a2);
				RAM64X1D_replacement.addNet(a3_c);
				RAM64X1D_replacement.addNet(a3);
				RAM64X1D_replacement.addNet(a4_c);
				RAM64X1D_replacement.addNet(a4);
				RAM64X1D_replacement.addNet(a5_c);
				RAM64X1D_replacement.addNet(a5);
				RAM64X1D_replacement.addNet(dpra0_c);
				RAM64X1D_replacement.addNet(dpra0);
				RAM64X1D_replacement.addNet(dpra1_c);
				RAM64X1D_replacement.addNet(dpra1);
				RAM64X1D_replacement.addNet(dpra2_c);
				RAM64X1D_replacement.addNet(dpra2);
				RAM64X1D_replacement.addNet(dpra3_c);
				RAM64X1D_replacement.addNet(dpra3);
				RAM64X1D_replacement.addNet(dpra4_c);
				RAM64X1D_replacement.addNet(dpra4);
				RAM64X1D_replacement.addNet(dpra5_c);
				RAM64X1D_replacement.addNet(dpra5);
				RAM64X1D_replacement.addNet(spo_c);
				RAM64X1D_replacement.addNet(spo);
				RAM64X1D_replacement.addNet(dpo_c);
				RAM64X1D_replacement.addNet(dpo);
				for(int i=0; i<WIRENUM; i++)
				{
					RAM64X1D_replacement.addNet(wire[i]);
				}
				for(int i=0; i<NETNUM; i++)
				{
					RAM64X1D_replacement.addNet(net[i]);
				}
				for(int i=0; i<ENUM; i++)
				{
					RAM64X1D_replacement.addNet(e[i]);
				}
				
				/******Instances******/
				//Constants
				int FDENUM = 64;
				int LUT2NUM = 14;
				int LUT3NUM = 78;
				int LUT4NUM = 64;
				int MUXF5NUM = 32;
				int MUXF6NUM = 16;
				//Buffers for input and output ports
				EdifCellInstance WE_ibuf = new EdifCellInstance("WE_ibuf", RAM64X1D_replacement, IBUF);
				EdifCellInstance D_ibuf = new EdifCellInstance("D_ibuf", RAM64X1D_replacement, IBUF);
				EdifCellInstance WCLK_ibuf = new EdifCellInstance("WCLK_ibuf", RAM64X1D_replacement, BUFGP);
				EdifCellInstance A0_ibuf = new EdifCellInstance("A0_ibuf", RAM64X1D_replacement, IBUF);
				EdifCellInstance A1_ibuf = new EdifCellInstance("A1_ibuf", RAM64X1D_replacement, IBUF);
				EdifCellInstance A2_ibuf = new EdifCellInstance("A2_ibuf", RAM64X1D_replacement, IBUF);
				EdifCellInstance A3_ibuf = new EdifCellInstance("A3_ibuf", RAM64X1D_replacement, IBUF);
				EdifCellInstance A4_ibuf = new EdifCellInstance("A4_ibuf", RAM64X1D_replacement, IBUF);
				EdifCellInstance A5_ibuf = new EdifCellInstance("A5_ibuf", RAM64X1D_replacement, IBUF);
				EdifCellInstance DPRA0_ibuf = new EdifCellInstance("DPRA0_ibuf", RAM64X1D_replacement, IBUF);
				EdifCellInstance DPRA1_ibuf = new EdifCellInstance("DPRA1_ibuf", RAM64X1D_replacement, IBUF);
				EdifCellInstance DPRA2_ibuf = new EdifCellInstance("DPRA2_ibuf", RAM64X1D_replacement, IBUF);
				EdifCellInstance DPRA3_ibuf = new EdifCellInstance("DPRA3_ibuf", RAM64X1D_replacement, IBUF);
				EdifCellInstance DPRA4_ibuf = new EdifCellInstance("DPRA4_ibuf", RAM64X1D_replacement, IBUF);
				EdifCellInstance DPRA5_ibuf = new EdifCellInstance("DPRA5_ibuf", RAM64X1D_replacement, IBUF);
				EdifCellInstance SPO_obuf = new EdifCellInstance("SPO_obuf", RAM64X1D_replacement, OBUF);
				EdifCellInstance DPO_obuf = new EdifCellInstance("DPO_obuf", RAM64X1D_replacement, OBUF);
				//FDEs
				EdifCellInstance[] FDE_ = new EdifCellInstance[FDENUM];
				for(int i=0; i<FDENUM; i++)
				{
					String fdename;
					fdename = "FDE" + String.valueOf(i);
					FDE_[i] = new EdifCellInstance(fdename, RAM64X1D_replacement, FDE);
				}
				//LUT2s
				EdifCellInstance[] LUT2_ = new EdifCellInstance[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					String lut2name;
					lut2name = "LUT2_" + String.valueOf(i);
					LUT2_[i] = new EdifCellInstance(lut2name, RAM64X1D_replacement, LUT2);
				}
				//LUT3s
				EdifCellInstance[] LUT3_ = new EdifCellInstance[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					String lut3name;
					lut3name = "LUT3_" + String.valueOf(i);
					LUT3_[i] = new EdifCellInstance(lut3name, RAM64X1D_replacement, LUT3);
				}
				//LUT4s
				EdifCellInstance[] LUT4_ = new EdifCellInstance[LUT4NUM];
				for(int i=0; i<LUT4NUM; i++)
				{
					String lut4name;
					lut4name = "LUT4_" + String.valueOf(i);
					LUT4_[i] = new EdifCellInstance(lut4name, RAM64X1D_replacement, LUT4);
				}
				//MUXF5
				EdifCellInstance[] MUXF5_ = new EdifCellInstance[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					String muxf5name;
					muxf5name = "MUXF5_" + String.valueOf(i);
					MUXF5_[i] = new EdifCellInstance(muxf5name, RAM64X1D_replacement, MUXF5);
				}
				//MUXF6
				EdifCellInstance[] MUXF6_ = new EdifCellInstance[MUXF6NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					String muxf6name;
					muxf6name = "MUXF6_" + String.valueOf(i);
					MUXF6_[i] = new EdifCellInstance(muxf6name, RAM64X1D_replacement, MUXF6);
				}
				//Add all instances to the top level design
				RAM64X1D_replacement.addSubCell(WE_ibuf);
				RAM64X1D_replacement.addSubCell(D_ibuf);
				RAM64X1D_replacement.addSubCell(WCLK_ibuf);
				RAM64X1D_replacement.addSubCell(A0_ibuf);
				RAM64X1D_replacement.addSubCell(A1_ibuf);
				RAM64X1D_replacement.addSubCell(A2_ibuf);
				RAM64X1D_replacement.addSubCell(A3_ibuf);
				RAM64X1D_replacement.addSubCell(A4_ibuf);
				RAM64X1D_replacement.addSubCell(A5_ibuf);
				RAM64X1D_replacement.addSubCell(DPRA0_ibuf);
				RAM64X1D_replacement.addSubCell(DPRA1_ibuf);
				RAM64X1D_replacement.addSubCell(DPRA2_ibuf);
				RAM64X1D_replacement.addSubCell(DPRA3_ibuf);
				RAM64X1D_replacement.addSubCell(DPRA4_ibuf);
				RAM64X1D_replacement.addSubCell(DPRA5_ibuf);
				RAM64X1D_replacement.addSubCell(SPO_obuf);
				RAM64X1D_replacement.addSubCell(DPO_obuf);
				for(int i=0; i<FDENUM; i++)
				{
					RAM64X1D_replacement.addSubCell(FDE_[i]);
				}
				for(int i=0; i<LUT2NUM; i++)
				{
					RAM64X1D_replacement.addSubCell(LUT2_[i]);
				}
				for(int i=0; i<LUT3NUM; i++)
				{
					RAM64X1D_replacement.addSubCell(LUT3_[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					RAM64X1D_replacement.addSubCell(LUT4_[i]);
				}
				for(int i=0; i<MUXF5NUM; i++)
				{
					RAM64X1D_replacement.addSubCell(MUXF5_[i]);
				}
				for(int i=0; i<MUXF6NUM; i++)
				{
					RAM64X1D_replacement.addSubCell(MUXF6_[i]);
				}
				
				/******Interface******/
				//Interface of buffers
				EdifCellInterface RAM64X1D_replacement_interface = new EdifCellInterface(RAM64X1D_replacement);
				EdifCellInterface WE_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface WCLK_ibuf_interface = new EdifCellInterface(BUFGP);
				EdifCellInterface A0_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A1_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A2_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A3_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A4_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A5_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface DPRA0_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface DPRA1_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface DPRA2_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface DPRA3_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface DPRA4_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface DPRA5_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface SPO_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface DPO_obuf_interface = new EdifCellInterface(OBUF);
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
				//RAM64X1D_replacement
				EdifPort RAM64X1D_replacement_interface_WE = new EdifPort(RAM64X1D_replacement_interface, "WE", 1, 1);
				EdifPort RAM64X1D_replacement_interface_D = new EdifPort(RAM64X1D_replacement_interface, "D", 1, 1);
				EdifPort RAM64X1D_replacement_interface_WCLK = new EdifPort(RAM64X1D_replacement_interface, "WCLK", 1, 1);
				EdifPort RAM64X1D_replacement_interface_A0 = new EdifPort(RAM64X1D_replacement_interface, "A0", 1, 1);
				EdifPort RAM64X1D_replacement_interface_A1 = new EdifPort(RAM64X1D_replacement_interface, "A1", 1, 1);
				EdifPort RAM64X1D_replacement_interface_A2 = new EdifPort(RAM64X1D_replacement_interface, "A2", 1, 1);
				EdifPort RAM64X1D_replacement_interface_A3 = new EdifPort(RAM64X1D_replacement_interface, "A3", 1, 1);
				EdifPort RAM64X1D_replacement_interface_A4 = new EdifPort(RAM64X1D_replacement_interface, "A4", 1, 1);
				EdifPort RAM64X1D_replacement_interface_A5 = new EdifPort(RAM64X1D_replacement_interface, "A5", 1, 1);
				EdifPort RAM64X1D_replacement_interface_DPRA0 = new EdifPort(RAM64X1D_replacement_interface, "DPRA0", 1, 1);
				EdifPort RAM64X1D_replacement_interface_DPRA1 = new EdifPort(RAM64X1D_replacement_interface, "DPRA1", 1, 1);
				EdifPort RAM64X1D_replacement_interface_DPRA2 = new EdifPort(RAM64X1D_replacement_interface, "DPRA2", 1, 1);
				EdifPort RAM64X1D_replacement_interface_DPRA3 = new EdifPort(RAM64X1D_replacement_interface, "DPRA3", 1, 1);
				EdifPort RAM64X1D_replacement_interface_DPRA4 = new EdifPort(RAM64X1D_replacement_interface, "DPRA4", 1, 1);
				EdifPort RAM64X1D_replacement_interface_DPRA5 = new EdifPort(RAM64X1D_replacement_interface, "DPRA5", 1, 1);
				EdifPort RAM64X1D_replacement_interface_SPO = new EdifPort(RAM64X1D_replacement_interface, "SPO", 1, 2);
				EdifPort RAM64X1D_replacement_interface_DPO = new EdifPort(RAM64X1D_replacement_interface, "DPO", 1, 2);
				RAM64X1D_replacement_interface.addPort("WE", 1, 1);
				RAM64X1D_replacement_interface.addPort("D", 1, 1);
				RAM64X1D_replacement_interface.addPort("WCLK", 1, 1);
				RAM64X1D_replacement_interface.addPort("A0", 1, 1);
				RAM64X1D_replacement_interface.addPort("A1", 1, 1);
				RAM64X1D_replacement_interface.addPort("A2", 1, 1);
				RAM64X1D_replacement_interface.addPort("A3", 1, 1);
				RAM64X1D_replacement_interface.addPort("A4", 1, 1);
				RAM64X1D_replacement_interface.addPort("A5", 1, 1);
				RAM64X1D_replacement_interface.addPort("DPRA0", 1, 1);
				RAM64X1D_replacement_interface.addPort("DPRA1", 1, 1);
				RAM64X1D_replacement_interface.addPort("DPRA2", 1, 1);
				RAM64X1D_replacement_interface.addPort("DPRA3", 1, 1);
				RAM64X1D_replacement_interface.addPort("DPRA4", 1, 1);
				RAM64X1D_replacement_interface.addPort("DPRA5", 1, 1);
				RAM64X1D_replacement_interface.addPort("SPO", 1, 2);
				RAM64X1D_replacement_interface.addPort("DPO", 1, 2);
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
				//DPRA0_ibuf
				EdifPort DPRA0_ibuf_interface_I = new EdifPort(DPRA0_ibuf_interface, "I", 1, 1);
				EdifPort DPRA0_ibuf_interface_O = new EdifPort(DPRA0_ibuf_interface, "O", 1, 2);
				DPRA0_ibuf_interface.addPort("I", 1, 1);
				DPRA0_ibuf_interface.addPort("O", 1, 2);
				//DPRA1_ibuf
				EdifPort DPRA1_ibuf_interface_I = new EdifPort(DPRA1_ibuf_interface, "I", 1, 1);
				EdifPort DPRA1_ibuf_interface_O = new EdifPort(DPRA1_ibuf_interface, "O", 1, 2);
				DPRA1_ibuf_interface.addPort("I", 1, 1);
				DPRA1_ibuf_interface.addPort("O", 1, 2);
				//DPRA2_ibuf
				EdifPort DPRA2_ibuf_interface_I = new EdifPort(DPRA2_ibuf_interface, "I", 1, 1);
				EdifPort DPRA2_ibuf_interface_O = new EdifPort(DPRA2_ibuf_interface, "O", 1, 2);
				DPRA2_ibuf_interface.addPort("I", 1, 1);
				DPRA2_ibuf_interface.addPort("O", 1, 2);
				//DPRA3_ibuf
				EdifPort DPRA3_ibuf_interface_I = new EdifPort(DPRA3_ibuf_interface, "I", 1, 1);
				EdifPort DPRA3_ibuf_interface_O = new EdifPort(DPRA3_ibuf_interface, "O", 1, 2);
				DPRA3_ibuf_interface.addPort("I", 1, 1);
				DPRA3_ibuf_interface.addPort("O", 1, 2);
				//DPRA4_ibuf
				EdifPort DPRA4_ibuf_interface_I = new EdifPort(DPRA4_ibuf_interface, "I", 1, 1);
				EdifPort DPRA4_ibuf_interface_O = new EdifPort(DPRA4_ibuf_interface, "O", 1, 2);
				DPRA4_ibuf_interface.addPort("I", 1, 1);
				DPRA4_ibuf_interface.addPort("O", 1, 2);
				//DPRA5_ibuf
				EdifPort DPRA5_ibuf_interface_I = new EdifPort(DPRA5_ibuf_interface, "I", 1, 1);
				EdifPort DPRA5_ibuf_interface_O = new EdifPort(DPRA5_ibuf_interface, "O", 1, 2);
				DPRA5_ibuf_interface.addPort("I", 1, 1);
				DPRA5_ibuf_interface.addPort("O", 1, 2);
				//SPO_obuf
				EdifPort SPO_obuf_interface_I = new EdifPort(SPO_obuf_interface, "I", 1, 1);
				EdifPort SPO_obuf_interface_O = new EdifPort(SPO_obuf_interface, "O", 1, 2);
				SPO_obuf_interface.addPort("I", 1, 1);
				SPO_obuf_interface.addPort("O", 1, 2);
				//DPO_obuf
				EdifPort DPO_obuf_interface_I = new EdifPort(DPO_obuf_interface, "I", 1, 1);
				EdifPort DPO_obuf_interface_O = new EdifPort(DPO_obuf_interface, "O", 1, 2);
				DPO_obuf_interface.addPort("I", 1, 1);
				DPO_obuf_interface.addPort("O", 1, 2);
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
				EdifPortRef RAM64X1D_replacement_WE = new EdifPortRef(we, RAM64X1D_replacement_interface_WE.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_D = new EdifPortRef(d, RAM64X1D_replacement_interface_D.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_WCLK = new EdifPortRef(wclk, RAM64X1D_replacement_interface_WCLK.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_A0 = new EdifPortRef(a0, RAM64X1D_replacement_interface_A0.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_A1 = new EdifPortRef(a1, RAM64X1D_replacement_interface_A1.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_A2 = new EdifPortRef(a2, RAM64X1D_replacement_interface_A2.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_A3 = new EdifPortRef(a3, RAM64X1D_replacement_interface_A3.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_A4 = new EdifPortRef(a4, RAM64X1D_replacement_interface_A4.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_A5 = new EdifPortRef(a5, RAM64X1D_replacement_interface_A5.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_DPRA0 = new EdifPortRef(dpra0, RAM64X1D_replacement_interface_DPRA0.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_DPRA1 = new EdifPortRef(dpra1, RAM64X1D_replacement_interface_DPRA1.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_DPRA2 = new EdifPortRef(dpra2, RAM64X1D_replacement_interface_DPRA2.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_DPRA3 = new EdifPortRef(dpra3, RAM64X1D_replacement_interface_DPRA3.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_DPRA4 = new EdifPortRef(dpra4, RAM64X1D_replacement_interface_DPRA4.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_DPRA5 = new EdifPortRef(dpra5, RAM64X1D_replacement_interface_DPRA5.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_SPO = new EdifPortRef(spo, RAM64X1D_replacement_interface_SPO.getSingleBitPort(0), null);
				EdifPortRef RAM64X1D_replacement_DPO = new EdifPortRef(dpo, RAM64X1D_replacement_interface_DPO.getSingleBitPort(0), null);
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
				EdifPortRef DPRA0_ibuf_I = new EdifPortRef(dpra0, DPRA0_ibuf_interface_I.getSingleBitPort(0), DPRA0_ibuf);
				EdifPortRef DPRA0_ibuf_O = new EdifPortRef(dpra0_c, DPRA0_ibuf_interface_O.getSingleBitPort(0), DPRA0_ibuf);
				EdifPortRef DPRA1_ibuf_I = new EdifPortRef(dpra1, DPRA1_ibuf_interface_I.getSingleBitPort(0), DPRA1_ibuf);
				EdifPortRef DPRA1_ibuf_O = new EdifPortRef(dpra1_c, DPRA1_ibuf_interface_O.getSingleBitPort(0), DPRA1_ibuf);
				EdifPortRef DPRA2_ibuf_I = new EdifPortRef(dpra2, DPRA2_ibuf_interface_I.getSingleBitPort(0), DPRA2_ibuf);
				EdifPortRef DPRA2_ibuf_O = new EdifPortRef(dpra2_c, DPRA2_ibuf_interface_O.getSingleBitPort(0), DPRA2_ibuf);
				EdifPortRef DPRA3_ibuf_I = new EdifPortRef(dpra3, DPRA3_ibuf_interface_I.getSingleBitPort(0), DPRA3_ibuf);
				EdifPortRef DPRA3_ibuf_O = new EdifPortRef(dpra3_c, DPRA3_ibuf_interface_O.getSingleBitPort(0), DPRA3_ibuf);
				EdifPortRef DPRA4_ibuf_I = new EdifPortRef(dpra4, DPRA4_ibuf_interface_I.getSingleBitPort(0), DPRA4_ibuf);
				EdifPortRef DPRA4_ibuf_O = new EdifPortRef(dpra4_c, DPRA4_ibuf_interface_O.getSingleBitPort(0), DPRA4_ibuf);
				EdifPortRef DPRA5_ibuf_I = new EdifPortRef(dpra5, DPRA5_ibuf_interface_I.getSingleBitPort(0), DPRA5_ibuf);
				EdifPortRef DPRA5_ibuf_O = new EdifPortRef(dpra5_c, DPRA5_ibuf_interface_O.getSingleBitPort(0), DPRA5_ibuf);
				EdifPortRef SPO_obuf_I = new EdifPortRef(spo_c, SPO_obuf_interface_I.getSingleBitPort(0), SPO_obuf);
				EdifPortRef SPO_obuf_O = new EdifPortRef(spo, SPO_obuf_interface_O.getSingleBitPort(0), SPO_obuf);
				EdifPortRef DPO_obuf_I = new EdifPortRef(dpo_c, DPO_obuf_interface_I.getSingleBitPort(0), DPO_obuf);
				EdifPortRef DPO_obuf_O = new EdifPortRef(dpo, DPO_obuf_interface_O.getSingleBitPort(0), DPO_obuf);
				//Port reference of FDEs
				EdifPortRef[] FDE_C = new EdifPortRef[FDENUM];
				EdifPortRef[] FDE_D = new EdifPortRef[FDENUM];
				EdifPortRef[] FDE_CE = new EdifPortRef[FDENUM];
				EdifPortRef[] FDE_Q = new EdifPortRef[FDENUM];
				for(int i=0; i<FDENUM; i++)
				{
					int q = i%64;
					FDE_C[i] = new EdifPortRef(wclk_c, FDEInterface_C[i].getSingleBitPort(0), FDE_[i]);
					FDE_CE[i] = new EdifPortRef(e[q], FDEInterface_CE[i].getSingleBitPort(0), FDE_[i]);
					FDE_Q[i] = new EdifPortRef(wire[i], FDEInterface_Q[i].getSingleBitPort(0), FDE_[i]);
					FDE_D[i] = new EdifPortRef(d_c, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
				}
				//Port reference of LUT2s
				EdifPortRef[] LUT2_I0 = new EdifPortRef[LUT2NUM];
				EdifPortRef[] LUT2_I1 = new EdifPortRef[LUT2NUM];
				EdifPortRef[] LUT2_O = new EdifPortRef[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					if(i==0 || i==1)
					{
						LUT2_I0[i] = new EdifPortRef(a5_c, LUT2Interface_I0[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_I1[i] = new EdifPortRef(we_c, LUT2Interface_I1[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_O[i] = new EdifPortRef(net[62*(LUT3NUM/39)+i], LUT2Interface_O[i].getSingleBitPort(0), LUT2_[i]);
					}
					else if(i==2 || i==3 || i==4 || i==5)
					{
						LUT2_I0[i] = new EdifPortRef(net[62*(LUT3NUM/39)+(i/2)-1], LUT2Interface_I0[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_I1[i] = new EdifPortRef(a4_c, LUT2Interface_I1[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_O[i] = new EdifPortRef(net[62*(LUT3NUM/39)+i], LUT2Interface_O[i].getSingleBitPort(0), LUT2_[i]);
					}
					else
					{
						LUT2_I0[i] = new EdifPortRef(net[62*(LUT3NUM/39)+(i/2)-1], LUT2Interface_I0[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_I1[i] = new EdifPortRef(a3_c, LUT2Interface_I1[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_O[i] = new EdifPortRef(net[62*(LUT3NUM/39)+i], LUT2Interface_O[i].getSingleBitPort(0), LUT2_[i]);
					}
				}
				//Port reference of LUT3s
				EdifPortRef[] LUT3_I0 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_I1 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_I2 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_O = new EdifPortRef[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					int a = i/39;
					int q = i%39;
					if(a<(LUT3NUM/78))		//SPO
					{
						if(q<32)
						{
							int wireNumber = 0;
							int lutNumber = i%32;
							int divider = 16;
							int addition = 1;
							for(int j=0; j<5; j++)
							{
								if(lutNumber/divider==1)
								{
									wireNumber += addition;
									lutNumber -= divider;
								}
								divider /= 2;
								addition *= 2;
							}
							LUT3_I0[i] = new EdifPortRef(a5_c, LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(wire[wireNumber+64*a], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(wire[wireNumber+32+64*a], LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[q+62*a], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else if(q>=32 && q<=35)
						{
							LUT3_I0[i] = new EdifPortRef(net[62*a+2*q-16], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(net[62*a+2*q-15], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(a2_c, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[62*a+24], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else if(q==36 || q==37)
						{
							LUT3_I0[i] = new EdifPortRef(net[62*a+2*q-16], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(net[62*a+2*q-15], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(a1_c, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[62*a+24], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else
						{
							LUT3_I0[i] = new EdifPortRef(net[62*a+2*q-16], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(net[62*a+2*q-15], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(a0_c, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(spo_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
					}
					else					//DPO
					{
						if(q<32)
						{
							int wireNumber = 0;
							int lutNumber = (i-LUT3NUM/2)%32;
							int divider = 16;
							int addition = 1;
							for(int j=0; j<5; j++)
							{
								if(lutNumber/divider==1)
								{
									wireNumber += addition;
									lutNumber -= divider;
								}
								divider /= 2;
								addition *= 2;
							}
							LUT3_I0[i] = new EdifPortRef(dpra5_c, LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(wire[wireNumber+64*(a-1)], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(wire[wireNumber+32+64*(a-1)], LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[q+62*a], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else if(q>=32 && q<=35)
						{
							LUT3_I0[i] = new EdifPortRef(net[62*a+2*q-16], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(net[62*a+2*q-15], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(dpra2_c, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[62*a+24], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else if(q==36 || q==37)
						{
							LUT3_I0[i] = new EdifPortRef(net[62*a+2*q-16], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(net[62*a+2*q-15], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(dpra1_c, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[62*a+24], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else
						{
							LUT3_I0[i] = new EdifPortRef(net[62*a+2*q-16], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(net[62*a+2*q-15], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(dpra0_c, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(spo_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
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
					int a = i/8;
					LUT4_I0[i] = new EdifPortRef(net[NETNUM-(8-a)], LUT4Interface_I0[i].getSingleBitPort(0), LUT4_[i]);
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
					int q = i%16;
					int a = i/16;
					if(a<(LUT3NUM/78))		//SPO
					{
						MUXF5_I0[i] = new EdifPortRef(net[2*q+62*a], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_I1[i] = new EdifPortRef(net[2*q+1+62*a], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_S[i] = new EdifPortRef(a4_c, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_O[i] = new EdifPortRef(net[32+62*a], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
					}
					else					//DPO
					{
						MUXF5_I0[i] = new EdifPortRef(net[2*q+62*a], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_I1[i] = new EdifPortRef(net[2*q+1+62*a], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_S[i] = new EdifPortRef(dpra4_c, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_O[i] = new EdifPortRef(net[32+62*a], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
					}
				}
				//Port reference of MUXF6s
				EdifPortRef[] MUXF6_I0 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_I1 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_S = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_O = new EdifPortRef[MUXF5NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					int a = i/8;
					int q = i%8;
					if(a<(LUT3NUM/78))		//SPO
					{
						MUXF6_I0[i] = new EdifPortRef(net[2*q+32+62*a], MUXF6Interface_I0[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_I1[i] = new EdifPortRef(net[2*q+33+62*a], MUXF6Interface_I1[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_S[i] = new EdifPortRef(a3_c, MUXF6Interface_S[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_O[i] = new EdifPortRef(net[48+62*a], MUXF6Interface_O[i].getSingleBitPort(0), MUXF6_[i]);
					}
					else				//DPO
					{
						MUXF6_I0[i] = new EdifPortRef(net[2*q+32+62*a], MUXF6Interface_I0[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_I1[i] = new EdifPortRef(net[2*q+33+62*a], MUXF6Interface_I1[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_S[i] = new EdifPortRef(dpra3_c, MUXF6Interface_S[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_O[i] = new EdifPortRef(net[48+62*a], MUXF6Interface_O[i].getSingleBitPort(0), MUXF6_[i]);
					}
				}
				//we
				we.addPortConnection(RAM64X1D_replacement_WE);
				we.addPortConnection(WE_ibuf_I);
				//we_c
				we_c.addPortConnection(WE_ibuf_O);
				for(int i=0; i<2;i++)
				{
					we_c.addPortConnection(LUT2_I1[i]);
				}
				//d
				d.addPortConnection(RAM64X1D_replacement_D);
				d.addPortConnection(D_ibuf_I);
				//d_c
				d_c.addPortConnection(D_ibuf_O);
				for(int i=0; i<64; i++)
				{
					d_c.addPortConnection(FDE_D[i]);
				}
				//wclk
				wclk.addPortConnection(WCLK_ibuf_I);
				wclk.addPortConnection(RAM64X1D_replacement_WCLK);
				//wclk_c
				wclk_c.addPortConnection(WCLK_ibuf_O);
				for(int i=0; i<FDENUM; i++)
				{
					wclk_c.addPortConnection(FDE_C[i]);
				}
				//a0
				a0.addPortConnection(RAM64X1D_replacement_A0);
				a0.addPortConnection(A0_ibuf_I);
				//a0_c
				a0_c.addPortConnection(A0_ibuf_O);
				for(int i=0; i<LUT3NUM/2; i++)
				{
					if((i%39)==38)
						a0_c.addPortConnection(LUT3_I2[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a0_c.addPortConnection(LUT4_I1[i]);
				}
				//a1
				a1.addPortConnection(RAM64X1D_replacement_A1);
				a1.addPortConnection(A1_ibuf_I);
				//a1_c
				a1_c.addPortConnection(A1_ibuf_O);
				for(int i=0; i<LUT3NUM/2; i++)
				{
					if((i%39)==36 || (i%39)==37)
						a1_c.addPortConnection(LUT3_I2[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a1_c.addPortConnection(LUT4_I2[i]);
				}
				//a2
				a2.addPortConnection(RAM64X1D_replacement_A2);
				a2.addPortConnection(A2_ibuf_I);
				//a2_c
				a2_c.addPortConnection(A2_ibuf_O);
				for(int i=0; i<LUT3NUM/2; i++)
				{
					if((i%39)>=32 && (i%39)<=35)
						a2_c.addPortConnection(LUT3_I2[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a2_c.addPortConnection(LUT4_I3[i]);
				}
				//a3
				a3.addPortConnection(RAM64X1D_replacement_A3);
				a3.addPortConnection(A3_ibuf_I);
				//a3_c
				a3_c.addPortConnection(A3_ibuf_O);
				for(int i=0; i<MUXF6NUM/2; i++)
				{
					a3_c.addPortConnection(MUXF6_S[i]);
				}
				for(int i=6; i<LUT2NUM; i++)
				{
					a3_c.addPortConnection(LUT2_I1[i]);
				}
				//a4
				a4.addPortConnection(RAM64X1D_replacement_A4);
				a4.addPortConnection(A4_ibuf_I);
				//a4_c
				a4_c.addPortConnection(A4_ibuf_O);
				for(int i=0; i<MUXF5NUM/2; i++)
				{
					a4_c.addPortConnection(MUXF5_S[i]);
				}
				for(int i=2; i<6; i++)
				{
					a4_c.addPortConnection(LUT2_I1[i]);
				}
				//a5
				a5.addPortConnection(RAM64X1D_replacement_A5);
				a5.addPortConnection(A5_ibuf_I);
				//a5_c
				a5_c.addPortConnection(A5_ibuf_O);
				for(int i=0; i<LUT3NUM/2; i++)
				{
					if((i%39)<32)
						a5_c.addPortConnection(LUT3_I0[i]);
				}
				for(int i=0; i<2; i++)
				{
					a5_c.addPortConnection(LUT2_I0[i]);
				}
				//dpra0
				dpra0.addPortConnection(RAM64X1D_replacement_DPRA0);
				dpra0.addPortConnection(DPRA0_ibuf_I);
				//dpra0_c
				dpra0_c.addPortConnection(DPRA0_ibuf_O);
				for(int i=LUT3NUM/2; i<LUT3NUM; i++)
				{
					if((i%39)==38)
						dpra0_c.addPortConnection(LUT3_I2[i]);
				}
				//dpra1
				dpra1.addPortConnection(RAM64X1D_replacement_DPRA1);
				dpra1.addPortConnection(DPRA1_ibuf_I);
				//dpra1_c
				dpra1_c.addPortConnection(DPRA1_ibuf_O);
				for(int i=LUT3NUM/2; i<LUT3NUM; i++)
				{
					if((i%39)==36 || (i%39)==37)
						dpra1_c.addPortConnection(LUT3_I2[i]);
				}
				//dpra2
				dpra2.addPortConnection(RAM64X1D_replacement_DPRA2);
				dpra2.addPortConnection(DPRA2_ibuf_I);
				//dpra2_c
				dpra2_c.addPortConnection(DPRA2_ibuf_O);
				for(int i=LUT3NUM/2; i<LUT3NUM; i++)
				{
					if((i%39)>=32 && (i%39)<=35)
						dpra2_c.addPortConnection(LUT3_I2[i]);
				}
				//dpra3
				dpra3.addPortConnection(RAM64X1D_replacement_DPRA3);
				dpra3.addPortConnection(DPRA3_ibuf_I);
				//dpra3_c
				dpra3_c.addPortConnection(DPRA3_ibuf_O);
				for(int i=MUXF6NUM/2; i<MUXF6NUM; i++)
				{
					dpra3_c.addPortConnection(MUXF6_S[i]);
				}
				//dpra4
				dpra4.addPortConnection(RAM64X1D_replacement_DPRA4);
				dpra4.addPortConnection(DPRA4_ibuf_I);
				//dpra4_c
				dpra4_c.addPortConnection(DPRA4_ibuf_O);
				for(int i=MUXF5NUM/2; i<MUXF5NUM; i++)
				{
					dpra4_c.addPortConnection(MUXF5_S[i]);
				}
				//dpra5
				dpra5.addPortConnection(RAM64X1D_replacement_DPRA5);
				dpra5.addPortConnection(DPRA5_ibuf_I);
				//dpra5_c
				dpra5_c.addPortConnection(DPRA5_ibuf_O);
				for(int i=LUT3NUM/2; i<LUT3NUM; i++)
				{
					if((i%39)<32)
						dpra5_c.addPortConnection(LUT3_I0[i]);
				}
				//spo
				spo.addPortConnection(SPO_obuf_O);
				spo.addPortConnection(RAM64X1D_replacement_SPO);
				//spo_c
				spo_c.addPortConnection(SPO_obuf_I);
				spo_c.addPortConnection(LUT3_O[38]);
				//dpo
				dpo.addPortConnection(DPO_obuf_O);
				dpo.addPortConnection(RAM64X1D_replacement_DPO);
				//dpo_c
				dpo_c.addPortConnection(DPO_obuf_I);
				dpo_c.addPortConnection(LUT3_O[77]);
				//wires
				for(int i=0; i<WIRENUM; i++)
				{
					int lutNumber = 0;
					int value = 16;
					int addition = 1;
					int wireNumber = i%32;
					for(int j=0; j<5; j++)
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
					int a = i/64;
					int q = i%64;
					wire[i].addPortConnection(FDE_Q[i]);
					if(q<32)
					{
						wire[i].addPortConnection(LUT3_I1[a*39+lutNumber]);
						wire[i].addPortConnection(LUT3_I1[a*39+lutNumber+LUT3NUM/2]);
					}
					else
					{
						wire[i].addPortConnection(LUT3_I2[a*39+lutNumber]);
						wire[i].addPortConnection(LUT3_I2[a*39+lutNumber+LUT3NUM/2]);
					}
				}
				//e
				for(int i=0; i<ENUM; i++)
				{
					e[i].addPortConnection(LUT4_O[i]);
					e[i].addPortConnection(FDE_CE[i]);
				}
				//nets
				for(int i=0; i<NETNUM; i++)
				{
					int a = i/62;
					int q = i%62;
					if(i<NETNUM-14)	//nets in MUX
					{
						if(q<32)
						{
							net[i].addPortConnection(LUT3_O[q+39*a]);
							if((q%2)==0)
								net[i].addPortConnection(MUXF5_I0[q/2+16*a]);
							else
								net[i].addPortConnection(MUXF5_I1[q/2+16*a]);
						}
						else if(q<48)
						{
							net[i].addPortConnection(MUXF5_O[q-32+16*a]);
							if((q%2)==0)
								net[i].addPortConnection(MUXF6_I0[q/2-16+8*a]);
							else
								net[i].addPortConnection(MUXF6_I1[q/2-16+8*a]);
						}
						else if(q<56)
						{
							net[i].addPortConnection(MUXF6_O[q-48+8*a]);
							if((q%2)==0)
								net[i].addPortConnection(LUT3_I0[q/2+8+39*a]);
							else
								net[i].addPortConnection(LUT3_I1[q/2+8+39*a]);
						}
						else
						{
							net[i].addPortConnection(LUT3_O[q-24+39*a]);
							if((q%2)==0)
								net[i].addPortConnection(LUT3_I0[q/2+8+39*a]);
							else
								net[i].addPortConnection(LUT3_I1[q/2+8+39*a]);
						}
					}
					else			//nets in decoder
					{
						if(q<6)
						{
							net[i].addPortConnection(LUT2_O[q]);
							net[i].addPortConnection(LUT2_I0[q*2+2]);
							net[i].addPortConnection(LUT2_I0[q*2+3]);
						}
						else
						{
							net[i].addPortConnection(LUT2_O[q]);
							for(int j=0; j<8; j++)
								net[i].addPortConnection(LUT4_I0[(q-6)*8+j]);
						}
					}
				}
				
				/******Set INIT Property******/
				boolean isInit;
				int[] initCount = new int[1];
				long[] INIT = new long[1];
				initCount[0] = 0;
				INIT[0] = 127;
				StringTypedValue valueZero = new StringTypedValue("0");
		        StringTypedValue valueOne = new StringTypedValue("1");
		        StringTypedValue valueE4 = new StringTypedValue("E4");
		        StringTypedValue valueCA = new StringTypedValue("CA");
		        StringTypedValue value2 = new StringTypedValue("2");
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
		        	int q = i%39;
		        	isInit = false;
		        	LUT3_propertylist[i] = LUT3_[i].getPropertyList();
		        	if(LUT3_propertylist[i] != null)
		        	{
		        		for(Property LUT3_property : LUT3_propertylist[i].values())
		        		{
		        			if(LUT3_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if(q<32)
		        					LUT3_property.setValue(valueE4);
		        				else
		        					LUT3_property.setValue(valueCA);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if(q<32)
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
		        				else if((i%2)==0)
		        					LUT2_property.setValue(value2);
		        				else
		        					LUT2_property.setValue(value8);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if(i==0)
		        			LUT2_[i].addProperty(new Property("INIT", (EdifTypedValue)value4));
		        		else if((i%2)==0)
		        			LUT2_[i].addProperty(new Property("INIT", (EdifTypedValue)value2));
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
		        	int a = i/64;
		        	isInit = false;
		        	FDE_propertylist[i] = FDE_[i].getPropertyList();
		        	if(FDE_propertylist[i] != null)
		        	{
		        		for(Property FDE_property : FDE_propertylist[i].values())
		        		{
		        			if(FDE_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if (((INIT[a] >> initCount[a]++) & 1) == 1)
	                                FDE_property.setValue(valueOne);
	                            else
	                                FDE_property.setValue(valueZero);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if (((INIT[a] >> initCount[a]++) & 1) == 1)
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
	
	public static void Replace(EdifLibraryManager libManager, EdifCell RAM64X1D_replacement, long INIT,
			EdifNet we, EdifNet d, EdifNet wclk, EdifNet a0, EdifNet a1, EdifNet a2, EdifNet a3, EdifNet a4, EdifNet a5,
			EdifNet dpra0, EdifNet dpra1, EdifNet dpra2, EdifNet dpra3, EdifNet dpra4, EdifNet dpra5, EdifNet spo, EdifNet dpo) {
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
				int NETNUM = 138;
				int ENUM = 64;
				//Declaration of wires, which are the outputs of FDEs
				EdifNet[] wire = new EdifNet[WIRENUM];
				for(int i=0; i<WIRENUM; i++)
				{
					String wirename;
					wirename = "wire" + String.valueOf(i);
					wire[i] = new EdifNet(wirename, RAM64X1D_replacement);
				}
				//Declaration of nets, which are used in MUX and decoder
				EdifNet[] net = new EdifNet[NETNUM];
				for(int i=0; i<NETNUM; i++)
				{
					String netname;
					netname = "net" + String.valueOf(i);
					net[i] = new EdifNet(netname, RAM64X1D_replacement);
				}
				//Declaration of enables
				EdifNet[] e = new EdifNet[ENUM];
				for(int i=0; i<ENUM; i++)
				{
					String ename;
					ename = "e" + String.valueOf(i);
					e[i] = new EdifNet(ename, RAM64X1D_replacement);
				}
				//Add all nets to the top level design
				for(int i=0; i<WIRENUM; i++)
				{
					RAM64X1D_replacement.addNet(wire[i]);
				}
				for(int i=0; i<NETNUM; i++)
				{
					RAM64X1D_replacement.addNet(net[i]);
				}
				for(int i=0; i<ENUM; i++)
				{
					RAM64X1D_replacement.addNet(e[i]);
				}
				
				/******Instances******/
				//Constants
				int FDENUM = 64;
				int LUT2NUM = 14;
				int LUT3NUM = 78;
				int LUT4NUM = 64;
				int MUXF5NUM = 32;
				int MUXF6NUM = 16;
				//FDEs
				EdifCellInstance[] FDE_ = new EdifCellInstance[FDENUM];
				for(int i=0; i<FDENUM; i++)
				{
					String fdename;
					fdename = "FDE" + String.valueOf(i);
					FDE_[i] = new EdifCellInstance(fdename, RAM64X1D_replacement, FDE);
				}
				//LUT2s
				EdifCellInstance[] LUT2_ = new EdifCellInstance[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					String lut2name;
					lut2name = "LUT2_" + String.valueOf(i);
					LUT2_[i] = new EdifCellInstance(lut2name, RAM64X1D_replacement, LUT2);
				}
				//LUT3s
				EdifCellInstance[] LUT3_ = new EdifCellInstance[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					String lut3name;
					lut3name = "LUT3_" + String.valueOf(i);
					LUT3_[i] = new EdifCellInstance(lut3name, RAM64X1D_replacement, LUT3);
				}
				//LUT4s
				EdifCellInstance[] LUT4_ = new EdifCellInstance[LUT4NUM];
				for(int i=0; i<LUT4NUM; i++)
				{
					String lut4name;
					lut4name = "LUT4_" + String.valueOf(i);
					LUT4_[i] = new EdifCellInstance(lut4name, RAM64X1D_replacement, LUT4);
				}
				//MUXF5
				EdifCellInstance[] MUXF5_ = new EdifCellInstance[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					String muxf5name;
					muxf5name = "MUXF5_" + String.valueOf(i);
					MUXF5_[i] = new EdifCellInstance(muxf5name, RAM64X1D_replacement, MUXF5);
				}
				//MUXF6
				EdifCellInstance[] MUXF6_ = new EdifCellInstance[MUXF6NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					String muxf6name;
					muxf6name = "MUXF6_" + String.valueOf(i);
					MUXF6_[i] = new EdifCellInstance(muxf6name, RAM64X1D_replacement, MUXF6);
				}
				//Add all instances to the top level design
				for(int i=0; i<FDENUM; i++)
				{
					RAM64X1D_replacement.addSubCell(FDE_[i]);
				}
				for(int i=0; i<LUT2NUM; i++)
				{
					RAM64X1D_replacement.addSubCell(LUT2_[i]);
				}
				for(int i=0; i<LUT3NUM; i++)
				{
					RAM64X1D_replacement.addSubCell(LUT3_[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					RAM64X1D_replacement.addSubCell(LUT4_[i]);
				}
				for(int i=0; i<MUXF5NUM; i++)
				{
					RAM64X1D_replacement.addSubCell(MUXF5_[i]);
				}
				for(int i=0; i<MUXF6NUM; i++)
				{
					RAM64X1D_replacement.addSubCell(MUXF6_[i]);
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
					int q = i%64;
					FDE_C[i] = new EdifPortRef(wclk, FDEInterface_C[i].getSingleBitPort(0), FDE_[i]);
					FDE_CE[i] = new EdifPortRef(e[q], FDEInterface_CE[i].getSingleBitPort(0), FDE_[i]);
					FDE_Q[i] = new EdifPortRef(wire[i], FDEInterface_Q[i].getSingleBitPort(0), FDE_[i]);
					FDE_D[i] = new EdifPortRef(d, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
				}
				//Port reference of LUT2s
				EdifPortRef[] LUT2_I0 = new EdifPortRef[LUT2NUM];
				EdifPortRef[] LUT2_I1 = new EdifPortRef[LUT2NUM];
				EdifPortRef[] LUT2_O = new EdifPortRef[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					if(i==0 || i==1)
					{
						LUT2_I0[i] = new EdifPortRef(a5, LUT2Interface_I0[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_I1[i] = new EdifPortRef(we, LUT2Interface_I1[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_O[i] = new EdifPortRef(net[62*(LUT3NUM/39)+i], LUT2Interface_O[i].getSingleBitPort(0), LUT2_[i]);
					}
					else if(i==2 || i==3 || i==4 || i==5)
					{
						LUT2_I0[i] = new EdifPortRef(net[62*(LUT3NUM/39)+(i/2)-1], LUT2Interface_I0[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_I1[i] = new EdifPortRef(a4, LUT2Interface_I1[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_O[i] = new EdifPortRef(net[62*(LUT3NUM/39)+i], LUT2Interface_O[i].getSingleBitPort(0), LUT2_[i]);
					}
					else
					{
						LUT2_I0[i] = new EdifPortRef(net[62*(LUT3NUM/39)+(i/2)-1], LUT2Interface_I0[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_I1[i] = new EdifPortRef(a3, LUT2Interface_I1[i].getSingleBitPort(0), LUT2_[i]);
						LUT2_O[i] = new EdifPortRef(net[62*(LUT3NUM/39)+i], LUT2Interface_O[i].getSingleBitPort(0), LUT2_[i]);
					}
				}
				//Port reference of LUT3s
				EdifPortRef[] LUT3_I0 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_I1 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_I2 = new EdifPortRef[LUT3NUM];
				EdifPortRef[] LUT3_O = new EdifPortRef[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					int a = i/39;
					int q = i%39;
					if(a<(LUT3NUM/78))		//SPO
					{
						if(q<32)
						{
							int wireNumber = 0;
							int lutNumber = i%32;
							int divider = 16;
							int addition = 1;
							for(int j=0; j<5; j++)
							{
								if(lutNumber/divider==1)
								{
									wireNumber += addition;
									lutNumber -= divider;
								}
								divider /= 2;
								addition *= 2;
							}
							LUT3_I0[i] = new EdifPortRef(a5, LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(wire[wireNumber+64*a], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(wire[wireNumber+32+64*a], LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[q+62*a], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else if(q>=32 && q<=35)
						{
							LUT3_I0[i] = new EdifPortRef(net[62*a+2*q-16], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(net[62*a+2*q-15], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(a2, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[62*a+24], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else if(q==36 || q==37)
						{
							LUT3_I0[i] = new EdifPortRef(net[62*a+2*q-16], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(net[62*a+2*q-15], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(a1, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[62*a+24], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else
						{
							LUT3_I0[i] = new EdifPortRef(net[62*a+2*q-16], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(net[62*a+2*q-15], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(a0, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(spo, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
					}
					else					//DPO
					{
						if(q<32)
						{
							int wireNumber = 0;
							int lutNumber = (i-LUT3NUM/2)%32;
							int divider = 16;
							int addition = 1;
							for(int j=0; j<5; j++)
							{
								if(lutNumber/divider==1)
								{
									wireNumber += addition;
									lutNumber -= divider;
								}
								divider /= 2;
								addition *= 2;
							}
							LUT3_I0[i] = new EdifPortRef(dpra5, LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(wire[wireNumber+64*(a-1)], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(wire[wireNumber+32+64*(a-1)], LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[q+62*a], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else if(q>=32 && q<=35)
						{
							LUT3_I0[i] = new EdifPortRef(net[62*a+2*q-16], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(net[62*a+2*q-15], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(dpra2, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[62*a+24], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else if(q==36 || q==37)
						{
							LUT3_I0[i] = new EdifPortRef(net[62*a+2*q-16], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(net[62*a+2*q-15], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(dpra1, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[62*a+24], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else
						{
							LUT3_I0[i] = new EdifPortRef(net[62*a+2*q-16], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(net[62*a+2*q-15], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(dpra0, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(spo, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
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
					int a = i/8;
					LUT4_I0[i] = new EdifPortRef(net[NETNUM-(8-a)], LUT4Interface_I0[i].getSingleBitPort(0), LUT4_[i]);
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
					int q = i%16;
					int a = i/16;
					if(a<(LUT3NUM/78))		//SPO
					{
						MUXF5_I0[i] = new EdifPortRef(net[2*q+62*a], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_I1[i] = new EdifPortRef(net[2*q+1+62*a], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_S[i] = new EdifPortRef(a4, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_O[i] = new EdifPortRef(net[32+62*a], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
					}
					else					//DPO
					{
						MUXF5_I0[i] = new EdifPortRef(net[2*q+62*a], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_I1[i] = new EdifPortRef(net[2*q+1+62*a], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_S[i] = new EdifPortRef(dpra4, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
						MUXF5_O[i] = new EdifPortRef(net[32+62*a], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
					}
				}
				//Port reference of MUXF6s
				EdifPortRef[] MUXF6_I0 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_I1 = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_S = new EdifPortRef[MUXF5NUM];
				EdifPortRef[] MUXF6_O = new EdifPortRef[MUXF5NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					int a = i/8;
					int q = i%8;
					if(a<(LUT3NUM/78))		//SPO
					{
						MUXF6_I0[i] = new EdifPortRef(net[2*q+32+62*a], MUXF6Interface_I0[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_I1[i] = new EdifPortRef(net[2*q+33+62*a], MUXF6Interface_I1[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_S[i] = new EdifPortRef(a3, MUXF6Interface_S[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_O[i] = new EdifPortRef(net[48+62*a], MUXF6Interface_O[i].getSingleBitPort(0), MUXF6_[i]);
					}
					else				//DPO
					{
						MUXF6_I0[i] = new EdifPortRef(net[2*q+32+62*a], MUXF6Interface_I0[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_I1[i] = new EdifPortRef(net[2*q+33+62*a], MUXF6Interface_I1[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_S[i] = new EdifPortRef(dpra3, MUXF6Interface_S[i].getSingleBitPort(0), MUXF6_[i]);
						MUXF6_O[i] = new EdifPortRef(net[48+62*a], MUXF6Interface_O[i].getSingleBitPort(0), MUXF6_[i]);
					}
				}
				//we
				for(int i=0; i<2;i++)
				{
					we.addPortConnection(LUT2_I1[i]);
				}
				//d
				for(int i=0; i<64; i++)
				{
					d.addPortConnection(FDE_D[i]);
				}
				//wclk
				for(int i=0; i<FDENUM; i++)
				{
					wclk.addPortConnection(FDE_C[i]);
				}
				//a0
				for(int i=0; i<LUT3NUM/2; i++)
				{
					if((i%39)==38)
						a0.addPortConnection(LUT3_I2[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a0.addPortConnection(LUT4_I1[i]);
				}
				//a1
				for(int i=0; i<LUT3NUM/2; i++)
				{
					if((i%39)==36 || (i%39)==37)
						a1.addPortConnection(LUT3_I2[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a1.addPortConnection(LUT4_I2[i]);
				}
				//a2
				for(int i=0; i<LUT3NUM/2; i++)
				{
					if((i%39)>=32 && (i%39)<=35)
						a2.addPortConnection(LUT3_I2[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a2.addPortConnection(LUT4_I3[i]);
				}
				//a3
				for(int i=0; i<MUXF6NUM/2; i++)
				{
					a3.addPortConnection(MUXF6_S[i]);
				}
				for(int i=6; i<LUT2NUM; i++)
				{
					a3.addPortConnection(LUT2_I1[i]);
				}
				//a4
				for(int i=0; i<MUXF5NUM/2; i++)
				{
					a4.addPortConnection(MUXF5_S[i]);
				}
				for(int i=2; i<6; i++)
				{
					a4.addPortConnection(LUT2_I1[i]);
				}
				//a5
				for(int i=0; i<LUT3NUM/2; i++)
				{
					if((i%39)<32)
						a5.addPortConnection(LUT3_I0[i]);
				}
				for(int i=0; i<2; i++)
				{
					a5.addPortConnection(LUT2_I0[i]);
				}
				//dpra0
				for(int i=LUT3NUM/2; i<LUT3NUM; i++)
				{
					if((i%39)==38)
						dpra0.addPortConnection(LUT3_I2[i]);
				}
				//dpra1
				for(int i=LUT3NUM/2; i<LUT3NUM; i++)
				{
					if((i%39)==36 || (i%39)==37)
						dpra1.addPortConnection(LUT3_I2[i]);
				}
				//dpra2
				for(int i=LUT3NUM/2; i<LUT3NUM; i++)
				{
					if((i%39)>=32 && (i%39)<=35)
						dpra2.addPortConnection(LUT3_I2[i]);
				}
				//dpra3
				for(int i=MUXF6NUM/2; i<MUXF6NUM; i++)
				{
					dpra3.addPortConnection(MUXF6_S[i]);
				}
				//dpra4
				for(int i=MUXF5NUM/2; i<MUXF5NUM; i++)
				{
					dpra4.addPortConnection(MUXF5_S[i]);
				}
				//dpra5
				for(int i=LUT3NUM/2; i<LUT3NUM; i++)
				{
					if((i%39)<32)
						dpra5.addPortConnection(LUT3_I0[i]);
				}
				//spo
				spo.addPortConnection(LUT3_O[38]);
				//dpo
				dpo.addPortConnection(LUT3_O[77]);
				//wires
				for(int i=0; i<WIRENUM; i++)
				{
					int lutNumber = 0;
					int value = 16;
					int addition = 1;
					int wireNumber = i%32;
					for(int j=0; j<5; j++)
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
					int a = i/64;
					int q = i%64;
					wire[i].addPortConnection(FDE_Q[i]);
					if(q<32)
					{
						wire[i].addPortConnection(LUT3_I1[a*39+lutNumber]);
						wire[i].addPortConnection(LUT3_I1[a*39+lutNumber+LUT3NUM/2]);
					}
					else
					{
						wire[i].addPortConnection(LUT3_I2[a*39+lutNumber]);
						wire[i].addPortConnection(LUT3_I2[a*39+lutNumber+LUT3NUM/2]);
					}
				}
				//e
				for(int i=0; i<ENUM; i++)
				{
					e[i].addPortConnection(LUT4_O[i]);
					e[i].addPortConnection(FDE_CE[i]);
				}
				//nets
				for(int i=0; i<NETNUM; i++)
				{
					int a = i/62;
					int q = i%62;
					if(i<NETNUM-14)	//nets in MUX
					{
						if(q<32)
						{
							net[i].addPortConnection(LUT3_O[q+39*a]);
							if((q%2)==0)
								net[i].addPortConnection(MUXF5_I0[q/2+16*a]);
							else
								net[i].addPortConnection(MUXF5_I1[q/2+16*a]);
						}
						else if(q<48)
						{
							net[i].addPortConnection(MUXF5_O[q-32+16*a]);
							if((q%2)==0)
								net[i].addPortConnection(MUXF6_I0[q/2-16+8*a]);
							else
								net[i].addPortConnection(MUXF6_I1[q/2-16+8*a]);
						}
						else if(q<56)
						{
							net[i].addPortConnection(MUXF6_O[q-48+8*a]);
							if((q%2)==0)
								net[i].addPortConnection(LUT3_I0[q/2+8+39*a]);
							else
								net[i].addPortConnection(LUT3_I1[q/2+8+39*a]);
						}
						else
						{
							net[i].addPortConnection(LUT3_O[q-24+39*a]);
							if((q%2)==0)
								net[i].addPortConnection(LUT3_I0[q/2+8+39*a]);
							else
								net[i].addPortConnection(LUT3_I1[q/2+8+39*a]);
						}
					}
					else			//nets in decoder
					{
						if(q<6)
						{
							net[i].addPortConnection(LUT2_O[q]);
							net[i].addPortConnection(LUT2_I0[q*2+2]);
							net[i].addPortConnection(LUT2_I0[q*2+3]);
						}
						else
						{
							net[i].addPortConnection(LUT2_O[q]);
							for(int j=0; j<8; j++)
								net[i].addPortConnection(LUT4_I0[(q-6)*8+j]);
						}
					}
				}
				
				/******Set INIT Property******/
				boolean isInit;
				int[] initCount = new int[1];
				long[] INITValue = new long[1];
				initCount[0] = 0;
				INITValue[0] = INIT;
				StringTypedValue valueZero = new StringTypedValue("0");
		        StringTypedValue valueOne = new StringTypedValue("1");
		        StringTypedValue valueE4 = new StringTypedValue("E4");
		        StringTypedValue valueCA = new StringTypedValue("CA");
		        StringTypedValue value2 = new StringTypedValue("2");
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
		        	int q = i%39;
		        	isInit = false;
		        	LUT3_propertylist[i] = LUT3_[i].getPropertyList();
		        	if(LUT3_propertylist[i] != null)
		        	{
		        		for(Property LUT3_property : LUT3_propertylist[i].values())
		        		{
		        			if(LUT3_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if(q<32)
		        					LUT3_property.setValue(valueE4);
		        				else
		        					LUT3_property.setValue(valueCA);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if(q<32)
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
		        				else if((i%2)==0)
		        					LUT2_property.setValue(value2);
		        				else
		        					LUT2_property.setValue(value8);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if(i==0)
		        			LUT2_[i].addProperty(new Property("INIT", (EdifTypedValue)value4));
		        		else if((i%2)==0)
		        			LUT2_[i].addProperty(new Property("INIT", (EdifTypedValue)value2));
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
		        	int a = i/64;
		        	isInit = false;
		        	FDE_propertylist[i] = FDE_[i].getPropertyList();
		        	if(FDE_propertylist[i] != null)
		        	{
		        		for(Property FDE_property : FDE_propertylist[i].values())
		        		{
		        			if(FDE_property.getName().equals("INIT"))
		        			{
		        				isInit = true;
		        				if (((INITValue[a] >> initCount[a]++) & 1) == 1)
	                                FDE_property.setValue(valueOne);
	                            else
	                                FDE_property.setValue(valueZero);
		        			}
		        		}
		        	}
		        	if(!isInit)
		        	{
		        		if (((INITValue[a] >> initCount[a]++) & 1) == 1)
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