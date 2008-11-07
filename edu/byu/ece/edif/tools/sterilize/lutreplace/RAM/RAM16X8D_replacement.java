/*
 * Generates the EDIF file for RAM16X8D
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
 * Generates the EDIF file for RAM16X8D
 * Includes: 128 FDEs, 16 16-1 MUXs(9 LUT3s, 4 MUXF5s, and 2 MUXF6s for each), 1 4-16 decoder(2 LUT2s, 16 LUT4s).
 * 
 * @author Yubo Li
 *
 */

public class RAM16X8D_replacement{
	public static void main(String args[]){
		
		String outputFileName = "RAM16X8D_replacement.edf";
		
		try{
			/******Environment and Environment manager******/
			EdifEnvironment topLevel = new EdifEnvironment("RAM16X8D_replacement");
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
				EdifCell RAM16X8D_replacement = new EdifCell(work, "RAM16X8D_replacement");
				RAM16X8D_replacement.addPort("WE", 1, 1);
				RAM16X8D_replacement.addPort("D", 8, 1);
				RAM16X8D_replacement.addPort("WCLK", 1, 1);
				RAM16X8D_replacement.addPort("A0", 1, 1);
				RAM16X8D_replacement.addPort("A1", 1, 1);
				RAM16X8D_replacement.addPort("A2", 1, 1);
				RAM16X8D_replacement.addPort("A3", 1, 1);
				RAM16X8D_replacement.addPort("DPRA0", 1, 1);
				RAM16X8D_replacement.addPort("DPRA1", 1, 1);
				RAM16X8D_replacement.addPort("DPRA2", 1, 1);
				RAM16X8D_replacement.addPort("DPRA3", 1, 1);
				RAM16X8D_replacement.addPort("SPO", 8, 2);
				RAM16X8D_replacement.addPort("DPO", 8, 2);
				
				/******Design******/
				EdifDesign design = new EdifDesign("RAM16X8D_replacement");
				topLevel.setTopDesign(design);
				topLevel.setTopCell(RAM16X8D_replacement);
				
				/******Nets******/
				//Constants
				int WIRENUM = 128;
				int NETNUM = 226;
				int ENUM = 16;
				//Declaration of input and output ports
				EdifNet wclk_c = new EdifNet("wclk_c", RAM16X8D_replacement);
				EdifNet wclk = new EdifNet("wclk", RAM16X8D_replacement);
				EdifNet d0_c = new EdifNet("d0_c", RAM16X8D_replacement);
				EdifNet d0 = new EdifNet("d0", RAM16X8D_replacement);
				EdifNet d1_c = new EdifNet("d1_c", RAM16X8D_replacement);
				EdifNet d1 = new EdifNet("d1", RAM16X8D_replacement);
				EdifNet d2_c = new EdifNet("d2_c", RAM16X8D_replacement);
				EdifNet d2 = new EdifNet("d2", RAM16X8D_replacement);
				EdifNet d3_c = new EdifNet("d3_c", RAM16X8D_replacement);
				EdifNet d3 = new EdifNet("d3", RAM16X8D_replacement);
				EdifNet d4_c = new EdifNet("d4_c", RAM16X8D_replacement);
				EdifNet d4 = new EdifNet("d4", RAM16X8D_replacement);
				EdifNet d5_c = new EdifNet("d5_c", RAM16X8D_replacement);
				EdifNet d5 = new EdifNet("d5", RAM16X8D_replacement);
				EdifNet d6_c = new EdifNet("d6_c", RAM16X8D_replacement);
				EdifNet d6 = new EdifNet("d6", RAM16X8D_replacement);
				EdifNet d7_c = new EdifNet("d7_c", RAM16X8D_replacement);
				EdifNet d7 = new EdifNet("d7", RAM16X8D_replacement);
				EdifNet we_c = new EdifNet("we_c", RAM16X8D_replacement);
				EdifNet we = new EdifNet("we", RAM16X8D_replacement);
				EdifNet a0_c = new EdifNet("a0_c", RAM16X8D_replacement);
				EdifNet a0 = new EdifNet("a0", RAM16X8D_replacement);
				EdifNet a1_c = new EdifNet("a1_c", RAM16X8D_replacement);
				EdifNet a1 = new EdifNet("a1", RAM16X8D_replacement);
				EdifNet a2_c = new EdifNet("a2_c", RAM16X8D_replacement);
				EdifNet a2 = new EdifNet("a2", RAM16X8D_replacement);
				EdifNet a3_c = new EdifNet("a3_c", RAM16X8D_replacement);
				EdifNet a3 = new EdifNet("a3", RAM16X8D_replacement);
				EdifNet dpra0_c = new EdifNet("dpra0_c", RAM16X8D_replacement);
				EdifNet dpra0 = new EdifNet("dpra0", RAM16X8D_replacement);
				EdifNet dpra1_c = new EdifNet("dpra1_c", RAM16X8D_replacement);
				EdifNet dpra1 = new EdifNet("dpra1", RAM16X8D_replacement);
				EdifNet dpra2_c = new EdifNet("dpra2_c", RAM16X8D_replacement);
				EdifNet dpra2 = new EdifNet("dpra2", RAM16X8D_replacement);
				EdifNet dpra3_c = new EdifNet("dpra3_c", RAM16X8D_replacement);
				EdifNet dpra3 = new EdifNet("dpra3", RAM16X8D_replacement);
				EdifNet spo0_c = new EdifNet("spo0_c", RAM16X8D_replacement);
				EdifNet spo0 = new EdifNet("spo0", RAM16X8D_replacement);
				EdifNet spo1_c = new EdifNet("spo1_c", RAM16X8D_replacement);
				EdifNet spo1 = new EdifNet("spo1", RAM16X8D_replacement);
				EdifNet spo2_c = new EdifNet("spo2_c", RAM16X8D_replacement);
				EdifNet spo2 = new EdifNet("spo2", RAM16X8D_replacement);
				EdifNet spo3_c = new EdifNet("spo3_c", RAM16X8D_replacement);
				EdifNet spo3 = new EdifNet("spo3", RAM16X8D_replacement);
				EdifNet spo4_c = new EdifNet("spo4_c", RAM16X8D_replacement);
				EdifNet spo4 = new EdifNet("spo4", RAM16X8D_replacement);
				EdifNet spo5_c = new EdifNet("spo5_c", RAM16X8D_replacement);
				EdifNet spo5 = new EdifNet("spo5", RAM16X8D_replacement);
				EdifNet spo6_c = new EdifNet("spo6_c", RAM16X8D_replacement);
				EdifNet spo6 = new EdifNet("spo6", RAM16X8D_replacement);
				EdifNet spo7_c = new EdifNet("spo7_c", RAM16X8D_replacement);
				EdifNet spo7 = new EdifNet("spo7", RAM16X8D_replacement);
				EdifNet dpo0_c = new EdifNet("dpo0_c", RAM16X8D_replacement);
				EdifNet dpo0 = new EdifNet("dpo0", RAM16X8D_replacement);
				EdifNet dpo1_c = new EdifNet("dpo1_c", RAM16X8D_replacement);
				EdifNet dpo1 = new EdifNet("dpo1", RAM16X8D_replacement);
				EdifNet dpo2_c = new EdifNet("dpr2_c", RAM16X8D_replacement);
				EdifNet dpo2 = new EdifNet("dpo2", RAM16X8D_replacement);
				EdifNet dpo3_c = new EdifNet("dpo3_c", RAM16X8D_replacement);
				EdifNet dpo3 = new EdifNet("dpo3", RAM16X8D_replacement);
				EdifNet dpo4_c = new EdifNet("dpo4_c", RAM16X8D_replacement);
				EdifNet dpo4 = new EdifNet("dpo4", RAM16X8D_replacement);
				EdifNet dpo5_c = new EdifNet("dpo5_c", RAM16X8D_replacement);
				EdifNet dpo5 = new EdifNet("dpo5", RAM16X8D_replacement);
				EdifNet dpo6_c = new EdifNet("dpr6_c", RAM16X8D_replacement);
				EdifNet dpo6 = new EdifNet("dpo6", RAM16X8D_replacement);
				EdifNet dpo7_c = new EdifNet("dpo7_c", RAM16X8D_replacement);
				EdifNet dpo7 = new EdifNet("dpo7", RAM16X8D_replacement);
				//Declaration of wires, which are the outputs of FDEs
				EdifNet[] wire = new EdifNet[WIRENUM];
				for(int i=0; i<WIRENUM; i++)
				{
					String wirename;
					wirename = "wire" + String.valueOf(i);
					wire[i] = new EdifNet(wirename, RAM16X8D_replacement);
				}
				//Declaration of nets, which are used in MUX and decoder
				EdifNet[] net = new EdifNet[NETNUM];
				for(int i=0; i<NETNUM; i++)
				{
					String netname;
					netname = "net" + String.valueOf(i);
					net[i] = new EdifNet(netname, RAM16X8D_replacement);
				}
				//Declaration of enables
				EdifNet[] e = new EdifNet[ENUM];
				for(int i=0; i<ENUM; i++)
				{
					String ename;
					ename = "e" + String.valueOf(i);
					e[i] = new EdifNet(ename, RAM16X8D_replacement);
				}
				//Add all nets to the top level design
				RAM16X8D_replacement.addNet(wclk_c);
				RAM16X8D_replacement.addNet(wclk);
				RAM16X8D_replacement.addNet(d0_c);
				RAM16X8D_replacement.addNet(d0);
				RAM16X8D_replacement.addNet(d1_c);
				RAM16X8D_replacement.addNet(d1);
				RAM16X8D_replacement.addNet(d2_c);
				RAM16X8D_replacement.addNet(d2);
				RAM16X8D_replacement.addNet(d3_c);
				RAM16X8D_replacement.addNet(d3);
				RAM16X8D_replacement.addNet(d4_c);
				RAM16X8D_replacement.addNet(d4);
				RAM16X8D_replacement.addNet(d5_c);
				RAM16X8D_replacement.addNet(d5);
				RAM16X8D_replacement.addNet(d6_c);
				RAM16X8D_replacement.addNet(d6);
				RAM16X8D_replacement.addNet(d7_c);
				RAM16X8D_replacement.addNet(d7);
				RAM16X8D_replacement.addNet(we_c);
				RAM16X8D_replacement.addNet(we);
				RAM16X8D_replacement.addNet(a0_c);
				RAM16X8D_replacement.addNet(a0);
				RAM16X8D_replacement.addNet(a1_c);
				RAM16X8D_replacement.addNet(a1);
				RAM16X8D_replacement.addNet(a2_c);
				RAM16X8D_replacement.addNet(a2);
				RAM16X8D_replacement.addNet(a3_c);
				RAM16X8D_replacement.addNet(a3);
				RAM16X8D_replacement.addNet(dpra0_c);
				RAM16X8D_replacement.addNet(dpra0);
				RAM16X8D_replacement.addNet(dpra1_c);
				RAM16X8D_replacement.addNet(dpra1);
				RAM16X8D_replacement.addNet(dpra2_c);
				RAM16X8D_replacement.addNet(dpra2);
				RAM16X8D_replacement.addNet(dpra3_c);
				RAM16X8D_replacement.addNet(dpra3);
				RAM16X8D_replacement.addNet(spo0_c);
				RAM16X8D_replacement.addNet(spo0);
				RAM16X8D_replacement.addNet(spo1_c);
				RAM16X8D_replacement.addNet(spo1);
				RAM16X8D_replacement.addNet(spo2_c);
				RAM16X8D_replacement.addNet(spo2);
				RAM16X8D_replacement.addNet(spo3_c);
				RAM16X8D_replacement.addNet(spo3);
				RAM16X8D_replacement.addNet(spo4_c);
				RAM16X8D_replacement.addNet(spo4);
				RAM16X8D_replacement.addNet(spo5_c);
				RAM16X8D_replacement.addNet(spo5);
				RAM16X8D_replacement.addNet(spo6_c);
				RAM16X8D_replacement.addNet(spo6);
				RAM16X8D_replacement.addNet(spo7_c);
				RAM16X8D_replacement.addNet(spo7);
				RAM16X8D_replacement.addNet(dpo0_c);
				RAM16X8D_replacement.addNet(dpo0);
				RAM16X8D_replacement.addNet(dpo1_c);
				RAM16X8D_replacement.addNet(dpo1);
				RAM16X8D_replacement.addNet(dpo2_c);
				RAM16X8D_replacement.addNet(dpo2);
				RAM16X8D_replacement.addNet(dpo3_c);
				RAM16X8D_replacement.addNet(dpo3);
				RAM16X8D_replacement.addNet(dpo4_c);
				RAM16X8D_replacement.addNet(dpo4);
				RAM16X8D_replacement.addNet(dpo5_c);
				RAM16X8D_replacement.addNet(dpo5);
				RAM16X8D_replacement.addNet(dpo6_c);
				RAM16X8D_replacement.addNet(dpo6);
				RAM16X8D_replacement.addNet(dpo7_c);
				RAM16X8D_replacement.addNet(dpo7);
				for(int i=0; i<WIRENUM; i++)
				{
					RAM16X8D_replacement.addNet(wire[i]);
				}
				for(int i=0; i<NETNUM; i++)
				{
					RAM16X8D_replacement.addNet(net[i]);
				}
				for(int i=0; i<ENUM; i++)
				{
					RAM16X8D_replacement.addNet(e[i]);
				}
				
				/******Instances******/
				//Constants
				int FDENUM = 128;
				int LUT2NUM = 2;
				int LUT3NUM = 144;
				int LUT4NUM = 16;
				int MUXF5NUM = 64;
				int MUXF6NUM = 32;
				//Buffers for input and output ports
				EdifCellInstance WE_ibuf = new EdifCellInstance("WE_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance D0_ibuf = new EdifCellInstance("D0_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance D1_ibuf = new EdifCellInstance("D1_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance D2_ibuf = new EdifCellInstance("D2_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance D3_ibuf = new EdifCellInstance("D3_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance D4_ibuf = new EdifCellInstance("D4_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance D5_ibuf = new EdifCellInstance("D5_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance D6_ibuf = new EdifCellInstance("D6_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance D7_ibuf = new EdifCellInstance("D7_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance WCLK_ibuf = new EdifCellInstance("WCLK_ibuf", RAM16X8D_replacement, BUFGP);
				EdifCellInstance A0_ibuf = new EdifCellInstance("A0_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance A1_ibuf = new EdifCellInstance("A1_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance A2_ibuf = new EdifCellInstance("A2_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance A3_ibuf = new EdifCellInstance("A3_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance DPRA0_ibuf = new EdifCellInstance("DPRA0_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance DPRA1_ibuf = new EdifCellInstance("DPRA1_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance DPRA2_ibuf = new EdifCellInstance("DPRA2_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance DPRA3_ibuf = new EdifCellInstance("DPRA3_ibuf", RAM16X8D_replacement, IBUF);
				EdifCellInstance SPO0_obuf = new EdifCellInstance("SPO0_obuf", RAM16X8D_replacement, OBUF);
				EdifCellInstance SPO1_obuf = new EdifCellInstance("SPO1_obuf", RAM16X8D_replacement, OBUF);
				EdifCellInstance SPO2_obuf = new EdifCellInstance("SPO2_obuf", RAM16X8D_replacement, OBUF);
				EdifCellInstance SPO3_obuf = new EdifCellInstance("SPO3_obuf", RAM16X8D_replacement, OBUF);
				EdifCellInstance SPO4_obuf = new EdifCellInstance("SPO4_obuf", RAM16X8D_replacement, OBUF);
				EdifCellInstance SPO5_obuf = new EdifCellInstance("SPO5_obuf", RAM16X8D_replacement, OBUF);
				EdifCellInstance SPO6_obuf = new EdifCellInstance("SPO6_obuf", RAM16X8D_replacement, OBUF);
				EdifCellInstance SPO7_obuf = new EdifCellInstance("SPO7_obuf", RAM16X8D_replacement, OBUF);
				EdifCellInstance DPO0_obuf = new EdifCellInstance("DPO0_obuf", RAM16X8D_replacement, OBUF);
				EdifCellInstance DPO1_obuf = new EdifCellInstance("DPO1_obuf", RAM16X8D_replacement, OBUF);
				EdifCellInstance DPO2_obuf = new EdifCellInstance("DPO2_obuf", RAM16X8D_replacement, OBUF);
				EdifCellInstance DPO3_obuf = new EdifCellInstance("DPO3_obuf", RAM16X8D_replacement, OBUF);
				EdifCellInstance DPO4_obuf = new EdifCellInstance("DPO4_obuf", RAM16X8D_replacement, OBUF);
				EdifCellInstance DPO5_obuf = new EdifCellInstance("DPO5_obuf", RAM16X8D_replacement, OBUF);
				EdifCellInstance DPO6_obuf = new EdifCellInstance("DPO6_obuf", RAM16X8D_replacement, OBUF);
				EdifCellInstance DPO7_obuf = new EdifCellInstance("DPO7_obuf", RAM16X8D_replacement, OBUF);
				//FDEs
				EdifCellInstance[] FDE_ = new EdifCellInstance[FDENUM];
				for(int i=0; i<FDENUM; i++)
				{
					String fdename;
					fdename = "FDE" + String.valueOf(i);
					FDE_[i] = new EdifCellInstance(fdename, RAM16X8D_replacement, FDE);
				}
				//LUT2s
				EdifCellInstance[] LUT2_ = new EdifCellInstance[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					String lut2name;
					lut2name = "LUT2_" + String.valueOf(i);
					LUT2_[i] = new EdifCellInstance(lut2name, RAM16X8D_replacement, LUT2);
				}
				//LUT3s
				EdifCellInstance[] LUT3_ = new EdifCellInstance[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					String lut3name;
					lut3name = "LUT3_" + String.valueOf(i);
					LUT3_[i] = new EdifCellInstance(lut3name, RAM16X8D_replacement, LUT3);
				}
				//LUT4s
				EdifCellInstance[] LUT4_ = new EdifCellInstance[LUT4NUM];
				for(int i=0; i<LUT4NUM; i++)
				{
					String lut4name;
					lut4name = "LUT4_" + String.valueOf(i);
					LUT4_[i] = new EdifCellInstance(lut4name, RAM16X8D_replacement, LUT4);
				}
				//MUXF5
				EdifCellInstance[] MUXF5_ = new EdifCellInstance[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					String muxf5name;
					muxf5name = "MUXF5_" + String.valueOf(i);
					MUXF5_[i] = new EdifCellInstance(muxf5name, RAM16X8D_replacement, MUXF5);
				}
				//MUXF6
				EdifCellInstance[] MUXF6_ = new EdifCellInstance[MUXF6NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					String muxf6name;
					muxf6name = "MUXF6_" + String.valueOf(i);
					MUXF6_[i] = new EdifCellInstance(muxf6name, RAM16X8D_replacement, MUXF6);
				}
				//Add all instances to the top level design
				RAM16X8D_replacement.addSubCell(WE_ibuf);
				RAM16X8D_replacement.addSubCell(D0_ibuf);
				RAM16X8D_replacement.addSubCell(D1_ibuf);
				RAM16X8D_replacement.addSubCell(D2_ibuf);
				RAM16X8D_replacement.addSubCell(D3_ibuf);
				RAM16X8D_replacement.addSubCell(D4_ibuf);
				RAM16X8D_replacement.addSubCell(D5_ibuf);
				RAM16X8D_replacement.addSubCell(D6_ibuf);
				RAM16X8D_replacement.addSubCell(D7_ibuf);
				RAM16X8D_replacement.addSubCell(WCLK_ibuf);
				RAM16X8D_replacement.addSubCell(A0_ibuf);
				RAM16X8D_replacement.addSubCell(A1_ibuf);
				RAM16X8D_replacement.addSubCell(A2_ibuf);
				RAM16X8D_replacement.addSubCell(A3_ibuf);
				RAM16X8D_replacement.addSubCell(DPRA0_ibuf);
				RAM16X8D_replacement.addSubCell(DPRA1_ibuf);
				RAM16X8D_replacement.addSubCell(DPRA2_ibuf);
				RAM16X8D_replacement.addSubCell(DPRA3_ibuf);
				RAM16X8D_replacement.addSubCell(SPO0_obuf);
				RAM16X8D_replacement.addSubCell(SPO1_obuf);
				RAM16X8D_replacement.addSubCell(SPO2_obuf);
				RAM16X8D_replacement.addSubCell(SPO3_obuf);
				RAM16X8D_replacement.addSubCell(SPO4_obuf);
				RAM16X8D_replacement.addSubCell(SPO5_obuf);
				RAM16X8D_replacement.addSubCell(SPO6_obuf);
				RAM16X8D_replacement.addSubCell(SPO7_obuf);
				RAM16X8D_replacement.addSubCell(DPO0_obuf);
				RAM16X8D_replacement.addSubCell(DPO1_obuf);
				RAM16X8D_replacement.addSubCell(DPO2_obuf);
				RAM16X8D_replacement.addSubCell(DPO3_obuf);
				RAM16X8D_replacement.addSubCell(DPO4_obuf);
				RAM16X8D_replacement.addSubCell(DPO5_obuf);
				RAM16X8D_replacement.addSubCell(DPO6_obuf);
				RAM16X8D_replacement.addSubCell(DPO7_obuf);
				for(int i=0; i<FDENUM; i++)
				{
					RAM16X8D_replacement.addSubCell(FDE_[i]);
				}
				for(int i=0; i<LUT2NUM; i++)
				{
					RAM16X8D_replacement.addSubCell(LUT2_[i]);
				}
				for(int i=0; i<LUT3NUM; i++)
				{
					RAM16X8D_replacement.addSubCell(LUT3_[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					RAM16X8D_replacement.addSubCell(LUT4_[i]);
				}
				for(int i=0; i<MUXF5NUM; i++)
				{
					RAM16X8D_replacement.addSubCell(MUXF5_[i]);
				}
				for(int i=0; i<MUXF6NUM; i++)
				{
					RAM16X8D_replacement.addSubCell(MUXF6_[i]);
				}
				
				/******Interface******/
				//Interface of buffers
				EdifCellInterface RAM16X8D_replacement_interface = new EdifCellInterface(RAM16X8D_replacement);
				EdifCellInterface WE_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D0_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D1_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D2_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D3_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D4_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D5_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D6_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface D7_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface WCLK_ibuf_interface = new EdifCellInterface(BUFGP);
				EdifCellInterface A0_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A1_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A2_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface A3_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface DPRA0_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface DPRA1_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface DPRA2_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface DPRA3_ibuf_interface = new EdifCellInterface(IBUF);
				EdifCellInterface SPO0_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface SPO1_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface SPO2_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface SPO3_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface SPO4_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface SPO5_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface SPO6_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface SPO7_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface DPO0_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface DPO1_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface DPO2_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface DPO3_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface DPO4_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface DPO5_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface DPO6_obuf_interface = new EdifCellInterface(OBUF);
				EdifCellInterface DPO7_obuf_interface = new EdifCellInterface(OBUF);
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
				//RAM16X8D_replacement
				EdifPort RAM16X8D_replacement_interface_WE = new EdifPort(RAM16X8D_replacement_interface, "WE", 1, 1);
				EdifPort RAM16X8D_replacement_interface_D = new EdifPort(RAM16X8D_replacement_interface, "D", 8, 1);
				EdifPort RAM16X8D_replacement_interface_WCLK = new EdifPort(RAM16X8D_replacement_interface, "WCLK", 1, 1);
				EdifPort RAM16X8D_replacement_interface_A0 = new EdifPort(RAM16X8D_replacement_interface, "A0", 1, 1);
				EdifPort RAM16X8D_replacement_interface_A1 = new EdifPort(RAM16X8D_replacement_interface, "A1", 1, 1);
				EdifPort RAM16X8D_replacement_interface_A2 = new EdifPort(RAM16X8D_replacement_interface, "A2", 1, 1);
				EdifPort RAM16X8D_replacement_interface_A3 = new EdifPort(RAM16X8D_replacement_interface, "A3", 1, 1);
				EdifPort RAM16X8D_replacement_interface_DPRA0 = new EdifPort(RAM16X8D_replacement_interface, "DPRA0", 1, 1);
				EdifPort RAM16X8D_replacement_interface_DPRA1 = new EdifPort(RAM16X8D_replacement_interface, "DPRA1", 1, 1);
				EdifPort RAM16X8D_replacement_interface_DPRA2 = new EdifPort(RAM16X8D_replacement_interface, "DPRA2", 1, 1);
				EdifPort RAM16X8D_replacement_interface_DPRA3 = new EdifPort(RAM16X8D_replacement_interface, "DPRA3", 1, 1);
				EdifPort RAM16X8D_replacement_interface_SPO = new EdifPort(RAM16X8D_replacement_interface, "SPO", 8, 2);
				EdifPort RAM16X8D_replacement_interface_DPO = new EdifPort(RAM16X8D_replacement_interface, "DPO", 8, 2);
				RAM16X8D_replacement_interface.addPort("WE", 1, 1);
				RAM16X8D_replacement_interface.addPort("D", 8, 1);
				RAM16X8D_replacement_interface.addPort("WCLK", 1, 1);
				RAM16X8D_replacement_interface.addPort("A0", 1, 1);
				RAM16X8D_replacement_interface.addPort("A1", 1, 1);
				RAM16X8D_replacement_interface.addPort("A2", 1, 1);
				RAM16X8D_replacement_interface.addPort("A3", 1, 1);
				RAM16X8D_replacement_interface.addPort("DPRA0", 1, 1);
				RAM16X8D_replacement_interface.addPort("DPRA1", 1, 1);
				RAM16X8D_replacement_interface.addPort("DPRA2", 1, 1);
				RAM16X8D_replacement_interface.addPort("DPRA3", 1, 1);
				RAM16X8D_replacement_interface.addPort("SPO", 8, 2);
				RAM16X8D_replacement_interface.addPort("DPO", 8, 2);
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
				//D4_ibuf
				EdifPort D4_ibuf_interface_I = new EdifPort(D4_ibuf_interface, "I", 1, 1);
				EdifPort D4_ibuf_interface_O = new EdifPort(D4_ibuf_interface, "O", 1, 2);
				D4_ibuf_interface.addPort("I", 1, 1);
				D4_ibuf_interface.addPort("O", 1, 2);
				//D5_ibuf
				EdifPort D5_ibuf_interface_I = new EdifPort(D5_ibuf_interface, "I", 1, 1);
				EdifPort D5_ibuf_interface_O = new EdifPort(D5_ibuf_interface, "O", 1, 2);
				D5_ibuf_interface.addPort("I", 1, 1);
				D5_ibuf_interface.addPort("O", 1, 2);
				//D6_ibuf
				EdifPort D6_ibuf_interface_I = new EdifPort(D6_ibuf_interface, "I", 1, 1);
				EdifPort D6_ibuf_interface_O = new EdifPort(D6_ibuf_interface, "O", 1, 2);
				D6_ibuf_interface.addPort("I", 1, 1);
				D6_ibuf_interface.addPort("O", 1, 2);
				//D7_ibuf
				EdifPort D7_ibuf_interface_I = new EdifPort(D7_ibuf_interface, "I", 1, 1);
				EdifPort D7_ibuf_interface_O = new EdifPort(D7_ibuf_interface, "O", 1, 2);
				D7_ibuf_interface.addPort("I", 1, 1);
				D7_ibuf_interface.addPort("O", 1, 2);
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
				//SPO0_obuf
				EdifPort SPO0_obuf_interface_I = new EdifPort(SPO0_obuf_interface, "I", 1, 1);
				EdifPort SPO0_obuf_interface_O = new EdifPort(SPO0_obuf_interface, "O", 1, 2);
				SPO0_obuf_interface.addPort("I", 1, 1);
				SPO0_obuf_interface.addPort("O", 1, 2);
				//SPO1_obuf
				EdifPort SPO1_obuf_interface_I = new EdifPort(SPO1_obuf_interface, "I", 1, 1);
				EdifPort SPO1_obuf_interface_O = new EdifPort(SPO1_obuf_interface, "O", 1, 2);
				SPO1_obuf_interface.addPort("I", 1, 1);
				SPO1_obuf_interface.addPort("O", 1, 2);
				//SPO2_obuf
				EdifPort SPO2_obuf_interface_I = new EdifPort(SPO2_obuf_interface, "I", 1, 1);
				EdifPort SPO2_obuf_interface_O = new EdifPort(SPO2_obuf_interface, "O", 1, 2);
				SPO2_obuf_interface.addPort("I", 1, 1);
				SPO2_obuf_interface.addPort("O", 1, 2);
				//SPO3_obuf
				EdifPort SPO3_obuf_interface_I = new EdifPort(SPO3_obuf_interface, "I", 1, 1);
				EdifPort SPO3_obuf_interface_O = new EdifPort(SPO3_obuf_interface, "O", 1, 2);
				SPO3_obuf_interface.addPort("I", 1, 1);
				SPO3_obuf_interface.addPort("O", 1, 2);
				//SPO4_obuf
				EdifPort SPO4_obuf_interface_I = new EdifPort(SPO4_obuf_interface, "I", 1, 1);
				EdifPort SPO4_obuf_interface_O = new EdifPort(SPO4_obuf_interface, "O", 1, 2);
				SPO4_obuf_interface.addPort("I", 1, 1);
				SPO4_obuf_interface.addPort("O", 1, 2);
				//SPO5_obuf
				EdifPort SPO5_obuf_interface_I = new EdifPort(SPO5_obuf_interface, "I", 1, 1);
				EdifPort SPO5_obuf_interface_O = new EdifPort(SPO5_obuf_interface, "O", 1, 2);
				SPO5_obuf_interface.addPort("I", 1, 1);
				SPO5_obuf_interface.addPort("O", 1, 2);
				//SPO6_obuf
				EdifPort SPO6_obuf_interface_I = new EdifPort(SPO6_obuf_interface, "I", 1, 1);
				EdifPort SPO6_obuf_interface_O = new EdifPort(SPO6_obuf_interface, "O", 1, 2);
				SPO6_obuf_interface.addPort("I", 1, 1);
				SPO6_obuf_interface.addPort("O", 1, 2);
				//SPO7_obuf
				EdifPort SPO7_obuf_interface_I = new EdifPort(SPO7_obuf_interface, "I", 1, 1);
				EdifPort SPO7_obuf_interface_O = new EdifPort(SPO7_obuf_interface, "O", 1, 2);
				SPO7_obuf_interface.addPort("I", 1, 1);
				SPO7_obuf_interface.addPort("O", 1, 2);
				//DPO0_obuf
				EdifPort DPO0_obuf_interface_I = new EdifPort(DPO0_obuf_interface, "I", 1, 1);
				EdifPort DPO0_obuf_interface_O = new EdifPort(DPO0_obuf_interface, "O", 1, 2);
				DPO0_obuf_interface.addPort("I", 1, 1);
				DPO0_obuf_interface.addPort("O", 1, 2);
				//DPO1_obuf
				EdifPort DPO1_obuf_interface_I = new EdifPort(DPO1_obuf_interface, "I", 1, 1);
				EdifPort DPO1_obuf_interface_O = new EdifPort(DPO1_obuf_interface, "O", 1, 2);
				DPO1_obuf_interface.addPort("I", 1, 1);
				DPO1_obuf_interface.addPort("O", 1, 2);
				//DPO2_obuf
				EdifPort DPO2_obuf_interface_I = new EdifPort(DPO2_obuf_interface, "I", 1, 1);
				EdifPort DPO2_obuf_interface_O = new EdifPort(DPO2_obuf_interface, "O", 1, 2);
				DPO2_obuf_interface.addPort("I", 1, 1);
				DPO2_obuf_interface.addPort("O", 1, 2);
				//DPO3_obuf
				EdifPort DPO3_obuf_interface_I = new EdifPort(DPO3_obuf_interface, "I", 1, 1);
				EdifPort DPO3_obuf_interface_O = new EdifPort(DPO3_obuf_interface, "O", 1, 2);
				DPO3_obuf_interface.addPort("I", 1, 1);
				DPO3_obuf_interface.addPort("O", 1, 2);
				//DPO4_obuf
				EdifPort DPO4_obuf_interface_I = new EdifPort(DPO4_obuf_interface, "I", 1, 1);
				EdifPort DPO4_obuf_interface_O = new EdifPort(DPO4_obuf_interface, "O", 1, 2);
				DPO4_obuf_interface.addPort("I", 1, 1);
				DPO4_obuf_interface.addPort("O", 1, 2);
				//DPO5_obuf
				EdifPort DPO5_obuf_interface_I = new EdifPort(DPO5_obuf_interface, "I", 1, 1);
				EdifPort DPO5_obuf_interface_O = new EdifPort(DPO5_obuf_interface, "O", 1, 2);
				DPO5_obuf_interface.addPort("I", 1, 1);
				DPO5_obuf_interface.addPort("O", 1, 2);
				//DPO6_obuf
				EdifPort DPO6_obuf_interface_I = new EdifPort(DPO6_obuf_interface, "I", 1, 1);
				EdifPort DPO6_obuf_interface_O = new EdifPort(DPO6_obuf_interface, "O", 1, 2);
				DPO6_obuf_interface.addPort("I", 1, 1);
				DPO6_obuf_interface.addPort("O", 1, 2);
				//DPO7_obuf
				EdifPort DPO7_obuf_interface_I = new EdifPort(DPO7_obuf_interface, "I", 1, 1);
				EdifPort DPO7_obuf_interface_O = new EdifPort(DPO7_obuf_interface, "O", 1, 2);
				DPO7_obuf_interface.addPort("I", 1, 1);
				DPO7_obuf_interface.addPort("O", 1, 2);
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
				EdifPortRef RAM16X8D_replacement_WE = new EdifPortRef(we, RAM16X8D_replacement_interface_WE.getSingleBitPort(0), null);
				EdifPortRef RAM16X8D_replacement_D0 = new EdifPortRef(d0, RAM16X8D_replacement_interface_D.getSingleBitPort(0), null);
				EdifPortRef RAM16X8D_replacement_D1 = new EdifPortRef(d0, RAM16X8D_replacement_interface_D.getSingleBitPort(1), null);
				EdifPortRef RAM16X8D_replacement_D2 = new EdifPortRef(d0, RAM16X8D_replacement_interface_D.getSingleBitPort(2), null);
				EdifPortRef RAM16X8D_replacement_D3 = new EdifPortRef(d0, RAM16X8D_replacement_interface_D.getSingleBitPort(3), null);
				EdifPortRef RAM16X8D_replacement_D4 = new EdifPortRef(d0, RAM16X8D_replacement_interface_D.getSingleBitPort(4), null);
				EdifPortRef RAM16X8D_replacement_D5 = new EdifPortRef(d0, RAM16X8D_replacement_interface_D.getSingleBitPort(5), null);
				EdifPortRef RAM16X8D_replacement_D6 = new EdifPortRef(d0, RAM16X8D_replacement_interface_D.getSingleBitPort(6), null);
				EdifPortRef RAM16X8D_replacement_D7 = new EdifPortRef(d0, RAM16X8D_replacement_interface_D.getSingleBitPort(7), null);
				EdifPortRef RAM16X8D_replacement_WCLK = new EdifPortRef(wclk, RAM16X8D_replacement_interface_WCLK.getSingleBitPort(0), null);
				EdifPortRef RAM16X8D_replacement_A0 = new EdifPortRef(a0, RAM16X8D_replacement_interface_A0.getSingleBitPort(0), null);
				EdifPortRef RAM16X8D_replacement_A1 = new EdifPortRef(a1, RAM16X8D_replacement_interface_A1.getSingleBitPort(0), null);
				EdifPortRef RAM16X8D_replacement_A2 = new EdifPortRef(a2, RAM16X8D_replacement_interface_A2.getSingleBitPort(0), null);
				EdifPortRef RAM16X8D_replacement_A3 = new EdifPortRef(a3, RAM16X8D_replacement_interface_A3.getSingleBitPort(0), null);
				EdifPortRef RAM16X8D_replacement_DPRA0 = new EdifPortRef(dpra0, RAM16X8D_replacement_interface_DPRA0.getSingleBitPort(0), null);
				EdifPortRef RAM16X8D_replacement_DPRA1 = new EdifPortRef(dpra1, RAM16X8D_replacement_interface_DPRA1.getSingleBitPort(0), null);
				EdifPortRef RAM16X8D_replacement_DPRA2 = new EdifPortRef(dpra2, RAM16X8D_replacement_interface_DPRA2.getSingleBitPort(0), null);
				EdifPortRef RAM16X8D_replacement_DPRA3 = new EdifPortRef(dpra3, RAM16X8D_replacement_interface_DPRA3.getSingleBitPort(0), null);
				EdifPortRef RAM16X8D_replacement_SPO0 = new EdifPortRef(spo0, RAM16X8D_replacement_interface_SPO.getSingleBitPort(0), null);
				EdifPortRef RAM16X8D_replacement_SPO1 = new EdifPortRef(spo1, RAM16X8D_replacement_interface_SPO.getSingleBitPort(1), null);
				EdifPortRef RAM16X8D_replacement_SPO2 = new EdifPortRef(spo2, RAM16X8D_replacement_interface_SPO.getSingleBitPort(2), null);
				EdifPortRef RAM16X8D_replacement_SPO3 = new EdifPortRef(spo3, RAM16X8D_replacement_interface_SPO.getSingleBitPort(3), null);
				EdifPortRef RAM16X8D_replacement_SPO4 = new EdifPortRef(spo4, RAM16X8D_replacement_interface_SPO.getSingleBitPort(4), null);
				EdifPortRef RAM16X8D_replacement_SPO5 = new EdifPortRef(spo5, RAM16X8D_replacement_interface_SPO.getSingleBitPort(5), null);
				EdifPortRef RAM16X8D_replacement_SPO6 = new EdifPortRef(spo6, RAM16X8D_replacement_interface_SPO.getSingleBitPort(6), null);
				EdifPortRef RAM16X8D_replacement_SPO7 = new EdifPortRef(spo7, RAM16X8D_replacement_interface_SPO.getSingleBitPort(7), null);
				EdifPortRef RAM16X8D_replacement_DPO0 = new EdifPortRef(dpo0, RAM16X8D_replacement_interface_DPO.getSingleBitPort(0), null);
				EdifPortRef RAM16X8D_replacement_DPO1 = new EdifPortRef(dpo1, RAM16X8D_replacement_interface_DPO.getSingleBitPort(1), null);
				EdifPortRef RAM16X8D_replacement_DPO2 = new EdifPortRef(dpo2, RAM16X8D_replacement_interface_DPO.getSingleBitPort(2), null);
				EdifPortRef RAM16X8D_replacement_DPO3 = new EdifPortRef(dpo3, RAM16X8D_replacement_interface_DPO.getSingleBitPort(3), null);
				EdifPortRef RAM16X8D_replacement_DPO4 = new EdifPortRef(dpo4, RAM16X8D_replacement_interface_DPO.getSingleBitPort(4), null);
				EdifPortRef RAM16X8D_replacement_DPO5 = new EdifPortRef(dpo5, RAM16X8D_replacement_interface_DPO.getSingleBitPort(5), null);
				EdifPortRef RAM16X8D_replacement_DPO6 = new EdifPortRef(dpo6, RAM16X8D_replacement_interface_DPO.getSingleBitPort(6), null);
				EdifPortRef RAM16X8D_replacement_DPO7 = new EdifPortRef(dpo7, RAM16X8D_replacement_interface_DPO.getSingleBitPort(7), null);
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
				EdifPortRef D4_ibuf_I = new EdifPortRef(d4, D4_ibuf_interface_I.getSingleBitPort(0), D4_ibuf);
				EdifPortRef D4_ibuf_O = new EdifPortRef(d4_c, D4_ibuf_interface_O.getSingleBitPort(0), D4_ibuf);
				EdifPortRef D5_ibuf_I = new EdifPortRef(d5, D5_ibuf_interface_I.getSingleBitPort(0), D5_ibuf);
				EdifPortRef D5_ibuf_O = new EdifPortRef(d5_c, D5_ibuf_interface_O.getSingleBitPort(0), D5_ibuf);
				EdifPortRef D6_ibuf_I = new EdifPortRef(d6, D6_ibuf_interface_I.getSingleBitPort(0), D6_ibuf);
				EdifPortRef D6_ibuf_O = new EdifPortRef(d6_c, D6_ibuf_interface_O.getSingleBitPort(0), D6_ibuf);
				EdifPortRef D7_ibuf_I = new EdifPortRef(d7, D7_ibuf_interface_I.getSingleBitPort(0), D7_ibuf);
				EdifPortRef D7_ibuf_O = new EdifPortRef(d7_c, D7_ibuf_interface_O.getSingleBitPort(0), D7_ibuf);
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
				EdifPortRef DPRA0_ibuf_I = new EdifPortRef(dpra0, DPRA0_ibuf_interface_I.getSingleBitPort(0), DPRA0_ibuf);
				EdifPortRef DPRA0_ibuf_O = new EdifPortRef(dpra0_c, DPRA0_ibuf_interface_O.getSingleBitPort(0), DPRA0_ibuf);
				EdifPortRef DPRA1_ibuf_I = new EdifPortRef(dpra1, DPRA1_ibuf_interface_I.getSingleBitPort(0), DPRA1_ibuf);
				EdifPortRef DPRA1_ibuf_O = new EdifPortRef(dpra1_c, DPRA1_ibuf_interface_O.getSingleBitPort(0), DPRA1_ibuf);
				EdifPortRef DPRA2_ibuf_I = new EdifPortRef(dpra2, DPRA2_ibuf_interface_I.getSingleBitPort(0), DPRA2_ibuf);
				EdifPortRef DPRA2_ibuf_O = new EdifPortRef(dpra2_c, DPRA2_ibuf_interface_O.getSingleBitPort(0), DPRA2_ibuf);
				EdifPortRef DPRA3_ibuf_I = new EdifPortRef(dpra3, DPRA3_ibuf_interface_I.getSingleBitPort(0), DPRA3_ibuf);
				EdifPortRef DPRA3_ibuf_O = new EdifPortRef(dpra3_c, DPRA3_ibuf_interface_O.getSingleBitPort(0), DPRA3_ibuf);
				EdifPortRef SPO0_obuf_I = new EdifPortRef(spo0_c, SPO0_obuf_interface_I.getSingleBitPort(0), SPO0_obuf);
				EdifPortRef SPO0_obuf_O = new EdifPortRef(spo0, SPO0_obuf_interface_O.getSingleBitPort(0), SPO0_obuf);
				EdifPortRef SPO1_obuf_I = new EdifPortRef(spo1_c, SPO1_obuf_interface_I.getSingleBitPort(0), SPO1_obuf);
				EdifPortRef SPO1_obuf_O = new EdifPortRef(spo1, SPO1_obuf_interface_O.getSingleBitPort(0), SPO1_obuf);
				EdifPortRef SPO2_obuf_I = new EdifPortRef(spo2_c, SPO2_obuf_interface_I.getSingleBitPort(0), SPO2_obuf);
				EdifPortRef SPO2_obuf_O = new EdifPortRef(spo2, SPO2_obuf_interface_O.getSingleBitPort(0), SPO2_obuf);
				EdifPortRef SPO3_obuf_I = new EdifPortRef(spo3_c, SPO3_obuf_interface_I.getSingleBitPort(0), SPO3_obuf);
				EdifPortRef SPO3_obuf_O = new EdifPortRef(spo3, SPO3_obuf_interface_O.getSingleBitPort(0), SPO3_obuf);
				EdifPortRef SPO4_obuf_I = new EdifPortRef(spo4_c, SPO4_obuf_interface_I.getSingleBitPort(0), SPO4_obuf);
				EdifPortRef SPO4_obuf_O = new EdifPortRef(spo4, SPO4_obuf_interface_O.getSingleBitPort(0), SPO4_obuf);
				EdifPortRef SPO5_obuf_I = new EdifPortRef(spo5_c, SPO5_obuf_interface_I.getSingleBitPort(0), SPO5_obuf);
				EdifPortRef SPO5_obuf_O = new EdifPortRef(spo5, SPO5_obuf_interface_O.getSingleBitPort(0), SPO5_obuf);
				EdifPortRef SPO6_obuf_I = new EdifPortRef(spo6_c, SPO6_obuf_interface_I.getSingleBitPort(0), SPO6_obuf);
				EdifPortRef SPO6_obuf_O = new EdifPortRef(spo6, SPO6_obuf_interface_O.getSingleBitPort(0), SPO6_obuf);
				EdifPortRef SPO7_obuf_I = new EdifPortRef(spo7_c, SPO7_obuf_interface_I.getSingleBitPort(0), SPO7_obuf);
				EdifPortRef SPO7_obuf_O = new EdifPortRef(spo7, SPO7_obuf_interface_O.getSingleBitPort(0), SPO7_obuf);
				EdifPortRef DPO0_obuf_I = new EdifPortRef(dpo0_c, DPO0_obuf_interface_I.getSingleBitPort(0), DPO0_obuf);
				EdifPortRef DPO0_obuf_O = new EdifPortRef(dpo0, DPO0_obuf_interface_O.getSingleBitPort(0), DPO0_obuf);
				EdifPortRef DPO1_obuf_I = new EdifPortRef(dpo1_c, DPO1_obuf_interface_I.getSingleBitPort(0), DPO1_obuf);
				EdifPortRef DPO1_obuf_O = new EdifPortRef(dpo1, DPO1_obuf_interface_O.getSingleBitPort(0), DPO1_obuf);
				EdifPortRef DPO2_obuf_I = new EdifPortRef(dpo2_c, DPO2_obuf_interface_I.getSingleBitPort(0), DPO2_obuf);
				EdifPortRef DPO2_obuf_O = new EdifPortRef(dpo2, DPO2_obuf_interface_O.getSingleBitPort(0), DPO2_obuf);
				EdifPortRef DPO3_obuf_I = new EdifPortRef(dpo3_c, DPO3_obuf_interface_I.getSingleBitPort(0), DPO3_obuf);
				EdifPortRef DPO3_obuf_O = new EdifPortRef(dpo3, DPO3_obuf_interface_O.getSingleBitPort(0), DPO3_obuf);
				EdifPortRef DPO4_obuf_I = new EdifPortRef(dpo4_c, DPO4_obuf_interface_I.getSingleBitPort(0), DPO4_obuf);
				EdifPortRef DPO4_obuf_O = new EdifPortRef(dpo4, DPO4_obuf_interface_O.getSingleBitPort(0), DPO4_obuf);
				EdifPortRef DPO5_obuf_I = new EdifPortRef(dpo5_c, DPO5_obuf_interface_I.getSingleBitPort(0), DPO5_obuf);
				EdifPortRef DPO5_obuf_O = new EdifPortRef(dpo5, DPO5_obuf_interface_O.getSingleBitPort(0), DPO5_obuf);
				EdifPortRef DPO6_obuf_I = new EdifPortRef(dpo6_c, DPO6_obuf_interface_I.getSingleBitPort(0), DPO6_obuf);
				EdifPortRef DPO6_obuf_O = new EdifPortRef(dpo6, DPO6_obuf_interface_O.getSingleBitPort(0), DPO6_obuf);
				EdifPortRef DPO7_obuf_I = new EdifPortRef(dpo7_c, DPO7_obuf_interface_I.getSingleBitPort(0), DPO7_obuf);
				EdifPortRef DPO7_obuf_O = new EdifPortRef(dpo7, DPO7_obuf_interface_O.getSingleBitPort(0), DPO7_obuf);
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
					else if(d==3)
						FDE_D[i] = new EdifPortRef(d3_c, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
					else if(d==4)
						FDE_D[i] = new EdifPortRef(d4_c, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
					else if(d==5)
						FDE_D[i] = new EdifPortRef(d5_c, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
					else if(d==6)
						FDE_D[i] = new EdifPortRef(d6_c, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
					else
						FDE_D[i] = new EdifPortRef(d7_c, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
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
					if(d<8)		//SPO
					{
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
								LUT3_O[i] = new EdifPortRef(spo0_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==1)
								LUT3_O[i] = new EdifPortRef(spo1_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==2)
								LUT3_O[i] = new EdifPortRef(spo2_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==3)
								LUT3_O[i] = new EdifPortRef(spo3_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==4)
								LUT3_O[i] = new EdifPortRef(spo4_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==5)
								LUT3_O[i] = new EdifPortRef(spo5_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==6)
								LUT3_O[i] = new EdifPortRef(spo6_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else
								LUT3_O[i] = new EdifPortRef(spo7_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
					}
					else	//DPO
					{
						if(q!=8)
						{
							LUT3_I0[i] = new EdifPortRef(dpra3_c, LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(wire[q+16*(d-8)], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(wire[q+16*(d-8)+8], LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[q+14*d], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else
						{
							LUT3_I0[i] = new EdifPortRef(net[12+14*d], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(net[13+14*d], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(dpra0_c, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							if(d==8)
								LUT3_O[i] = new EdifPortRef(dpo0_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==9)
								LUT3_O[i] = new EdifPortRef(dpo1_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==10)
								LUT3_O[i] = new EdifPortRef(dpo2_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==11)
								LUT3_O[i] = new EdifPortRef(dpo3_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==12)
								LUT3_O[i] = new EdifPortRef(dpo4_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==13)
								LUT3_O[i] = new EdifPortRef(dpo5_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==14)
								LUT3_O[i] = new EdifPortRef(dpo6_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else
								LUT3_O[i] = new EdifPortRef(dpo7_c, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
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
					if(d<8)		//SPO
					{
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
					else	//DPO
					{
						if(q==0)
						{
							MUXF5_I0[i] = new EdifPortRef(net[0+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_I1[i] = new EdifPortRef(net[4+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_S[i] = new EdifPortRef(dpra2_c, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_O[i] = new EdifPortRef(net[8+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
						}
						else if(q==1)
						{
							MUXF5_I0[i] = new EdifPortRef(net[2+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_I1[i] = new EdifPortRef(net[6+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_S[i] = new EdifPortRef(dpra2_c, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_O[i] = new EdifPortRef(net[9+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
						}
						else if(q==2)
						{
							MUXF5_I0[i] = new EdifPortRef(net[1+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_I1[i] = new EdifPortRef(net[5+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_S[i] = new EdifPortRef(dpra2_c, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_O[i] = new EdifPortRef(net[10+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
						}
						else
						{
							MUXF5_I0[i] = new EdifPortRef(net[3+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_I1[i] = new EdifPortRef(net[7+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_S[i] = new EdifPortRef(dpra2_c, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_O[i] = new EdifPortRef(net[11+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
						}
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
					if(d<8)		//SPO
					{
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
					else		//DPO
					{
						if(q==0)
						{
							MUXF6_I0[i] = new EdifPortRef(net[8+14*d], MUXF6Interface_I0[i].getSingleBitPort(0), MUXF6_[i]);
							MUXF6_I1[i] = new EdifPortRef(net[9+14*d], MUXF6Interface_I1[i].getSingleBitPort(0), MUXF6_[i]);
							MUXF6_S[i] = new EdifPortRef(dpra1_c, MUXF6Interface_S[i].getSingleBitPort(0), MUXF6_[i]);
							MUXF6_O[i] = new EdifPortRef(net[12+14*d], MUXF6Interface_O[i].getSingleBitPort(0), MUXF6_[i]);
						}
						else if(q==1)
						{
							MUXF6_I0[i] = new EdifPortRef(net[10+14*d], MUXF6Interface_I0[i].getSingleBitPort(0), MUXF6_[i]);
							MUXF6_I1[i] = new EdifPortRef(net[11+14*d], MUXF6Interface_I1[i].getSingleBitPort(0), MUXF6_[i]);
							MUXF6_S[i] = new EdifPortRef(dpra1_c, MUXF6Interface_S[i].getSingleBitPort(0), MUXF6_[i]);
							MUXF6_O[i] = new EdifPortRef(net[13+14*d], MUXF6Interface_O[i].getSingleBitPort(0), MUXF6_[i]);
						}
					}
				}
				//we
				we.addPortConnection(RAM16X8D_replacement_WE);
				we.addPortConnection(WE_ibuf_I);
				//we_c
				we_c.addPortConnection(WE_ibuf_O);
				for(int i=0; i<LUT2NUM;i++)
				{
					we_c.addPortConnection(LUT2_I1[i]);
				}
				//d0
				d0.addPortConnection(RAM16X8D_replacement_D0);
				d0.addPortConnection(D0_ibuf_I);
				//d0_c
				d0_c.addPortConnection(D0_ibuf_O);
				for(int i=0; i<16; i++)
				{
					d0_c.addPortConnection(FDE_D[i]);
				}
				//d1
				d1.addPortConnection(RAM16X8D_replacement_D1);
				d1.addPortConnection(D1_ibuf_I);
				//d1_c
				d1_c.addPortConnection(D1_ibuf_O);
				for(int i=16; i<32; i++)
				{
					d1_c.addPortConnection(FDE_D[i]);
				}
				//d2
				d2.addPortConnection(RAM16X8D_replacement_D2);
				d2.addPortConnection(D2_ibuf_I);
				//d2_c
				d2_c.addPortConnection(D2_ibuf_O);
				for(int i=32; i<48; i++)
				{
					d2_c.addPortConnection(FDE_D[i]);
				}
				//d3
				d3.addPortConnection(RAM16X8D_replacement_D3);
				d3.addPortConnection(D3_ibuf_I);
				//d3_c
				d3_c.addPortConnection(D3_ibuf_O);
				for(int i=48; i<64; i++)
				{
					d3_c.addPortConnection(FDE_D[i]);
				}
				//d4
				d4.addPortConnection(RAM16X8D_replacement_D4);
				d4.addPortConnection(D4_ibuf_I);
				//d4_c
				d4_c.addPortConnection(D4_ibuf_O);
				for(int i=64; i<79; i++)
				{
					d4_c.addPortConnection(FDE_D[i]);
				}
				//d5
				d5.addPortConnection(RAM16X8D_replacement_D5);
				d5.addPortConnection(D5_ibuf_I);
				//d5_c
				d5_c.addPortConnection(D5_ibuf_O);
				for(int i=80; i<95; i++)
				{
					d5_c.addPortConnection(FDE_D[i]);
				}
				//d6
				d6.addPortConnection(RAM16X8D_replacement_D6);
				d6.addPortConnection(D6_ibuf_I);
				//d6_c
				d6_c.addPortConnection(D6_ibuf_O);
				for(int i=96; i<111; i++)
				{
					d6_c.addPortConnection(FDE_D[i]);
				}
				//d7
				d7.addPortConnection(RAM16X8D_replacement_D7);
				d7.addPortConnection(D7_ibuf_I);
				//d7_c
				d7_c.addPortConnection(D7_ibuf_O);
				for(int i=112; i<127; i++)
				{
					d7_c.addPortConnection(FDE_D[i]);
				}
				//wclk
				wclk.addPortConnection(WCLK_ibuf_I);
				wclk.addPortConnection(RAM16X8D_replacement_WCLK);
				//wclk_c
				wclk_c.addPortConnection(WCLK_ibuf_O);
				for(int i=0; i<FDENUM; i++)
				{
					wclk_c.addPortConnection(FDE_C[i]);
				}
				//a0
				a0.addPortConnection(RAM16X8D_replacement_A0);
				a0.addPortConnection(A0_ibuf_I);
				//a0_c
				a0_c.addPortConnection(A0_ibuf_O);
				for(int i=0; i<LUT3NUM/2; i++)
				{
					if((i%9)==8)
						a0_c.addPortConnection(LUT3_I2[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a0_c.addPortConnection(LUT4_I1[i]);
				}
				//a1
				a1.addPortConnection(RAM16X8D_replacement_A1);
				a1.addPortConnection(A1_ibuf_I);
				//a1_c
				a1_c.addPortConnection(A1_ibuf_O);
				for(int i=0; i<MUXF6NUM/2; i++)
				{
					a1_c.addPortConnection(MUXF6_S[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a1_c.addPortConnection(LUT4_I2[i]);
				}
				//a2
				a2.addPortConnection(RAM16X8D_replacement_A2);
				a2.addPortConnection(A2_ibuf_I);
				//a2_c
				a2_c.addPortConnection(A2_ibuf_O);
				for(int i=0; i<MUXF5NUM/2; i++)
				{
					a2_c.addPortConnection(MUXF5_S[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a2_c.addPortConnection(LUT4_I3[i]);
				}
				//a3
				a3.addPortConnection(RAM16X8D_replacement_A3);
				a3.addPortConnection(A3_ibuf_I);
				//a3_c
				a3_c.addPortConnection(A3_ibuf_O);
				for(int i=0; i<LUT3NUM/2; i++)
				{
					if((i%9)!=8)
						a3_c.addPortConnection(LUT3_I0[i]);
				}
				for(int i=0; i<LUT2NUM; i++)
				{
					a3_c.addPortConnection(LUT2_I0[i]);
				}
				//dpra0
				dpra0.addPortConnection(RAM16X8D_replacement_DPRA0);
				dpra0.addPortConnection(DPRA0_ibuf_I);
				//dpra0_c
				dpra0_c.addPortConnection(DPRA0_ibuf_O);
				for(int i=LUT3NUM/2; i<LUT3NUM; i++)
				{
					if((i%9)==8)
						dpra0_c.addPortConnection(LUT3_I2[i]);
				}
				//dpra1
				dpra1.addPortConnection(RAM16X8D_replacement_DPRA1);
				dpra1.addPortConnection(DPRA1_ibuf_I);
				//dpra1_c
				dpra1_c.addPortConnection(DPRA1_ibuf_O);
				for(int i=MUXF6NUM/2; i<MUXF6NUM; i++)
				{
					dpra1_c.addPortConnection(MUXF6_S[i]);
				}
				//dpra2
				dpra2.addPortConnection(RAM16X8D_replacement_DPRA2);
				dpra2.addPortConnection(DPRA2_ibuf_I);
				//dpra2_c
				dpra2_c.addPortConnection(DPRA2_ibuf_O);
				for(int i=MUXF5NUM/2; i<MUXF5NUM; i++)
				{
					dpra2_c.addPortConnection(MUXF5_S[i]);
				}
				//dpra3
				dpra3.addPortConnection(RAM16X8D_replacement_DPRA3);
				dpra3.addPortConnection(DPRA3_ibuf_I);
				//dpra3_c
				dpra3_c.addPortConnection(DPRA3_ibuf_O);
				for(int i=LUT3NUM/2; i<LUT3NUM; i++)
				{
					if((i%9)!=8)
						dpra3_c.addPortConnection(LUT3_I0[i]);
				}
				//spo0
				spo0.addPortConnection(SPO0_obuf_O);
				spo0.addPortConnection(RAM16X8D_replacement_SPO0);
				//spo0_c
				spo0_c.addPortConnection(SPO0_obuf_I);
				spo0_c.addPortConnection(LUT3_O[8]);
				//spo1
				spo1.addPortConnection(SPO1_obuf_O);
				spo1.addPortConnection(RAM16X8D_replacement_SPO1);
				//spo1_c
				spo1_c.addPortConnection(SPO1_obuf_I);
				spo1_c.addPortConnection(LUT3_O[17]);
				//spo2
				spo2.addPortConnection(SPO2_obuf_O);
				spo2.addPortConnection(RAM16X8D_replacement_SPO2);
				//spo2_c
				spo2_c.addPortConnection(SPO2_obuf_I);
				spo2_c.addPortConnection(LUT3_O[26]);
				//spo3
				spo3.addPortConnection(SPO3_obuf_O);
				spo3.addPortConnection(RAM16X8D_replacement_SPO3);
				//spo3_c
				spo3_c.addPortConnection(SPO3_obuf_I);
				spo3_c.addPortConnection(LUT3_O[35]);
				//spo4
				spo4.addPortConnection(SPO4_obuf_O);
				spo4.addPortConnection(RAM16X8D_replacement_SPO4);
				//spo4_c
				spo4_c.addPortConnection(SPO4_obuf_I);
				spo4_c.addPortConnection(LUT3_O[44]);
				//spo5
				spo5.addPortConnection(SPO5_obuf_O);
				spo5.addPortConnection(RAM16X8D_replacement_SPO5);
				//spo5_c
				spo5_c.addPortConnection(SPO5_obuf_I);
				spo5_c.addPortConnection(LUT3_O[53]);
				//spo6
				spo6.addPortConnection(SPO6_obuf_O);
				spo6.addPortConnection(RAM16X8D_replacement_SPO6);
				//spo6_c
				spo6_c.addPortConnection(SPO6_obuf_I);
				spo6_c.addPortConnection(LUT3_O[62]);
				//spo7
				spo7.addPortConnection(SPO7_obuf_O);
				spo7.addPortConnection(RAM16X8D_replacement_SPO7);
				//spo7_c
				spo7_c.addPortConnection(SPO7_obuf_I);
				spo7_c.addPortConnection(LUT3_O[71]);
				//dpo0
				dpo0.addPortConnection(DPO0_obuf_O);
				dpo0.addPortConnection(RAM16X8D_replacement_DPO0);
				//dpo0_c
				dpo0_c.addPortConnection(DPO0_obuf_I);
				dpo0_c.addPortConnection(LUT3_O[80]);
				//dpo1
				dpo1.addPortConnection(DPO1_obuf_O);
				dpo1.addPortConnection(RAM16X8D_replacement_DPO1);
				//dpo1_c
				dpo1_c.addPortConnection(DPO1_obuf_I);
				dpo1_c.addPortConnection(LUT3_O[89]);
				//dpo2
				dpo2.addPortConnection(DPO2_obuf_O);
				dpo2.addPortConnection(RAM16X8D_replacement_DPO2);
				//dpo2_c
				dpo2_c.addPortConnection(DPO2_obuf_I);
				dpo2_c.addPortConnection(LUT3_O[98]);
				//dpo3
				dpo3.addPortConnection(DPO3_obuf_O);
				dpo3.addPortConnection(RAM16X8D_replacement_DPO3);
				//dpo3_c
				dpo3_c.addPortConnection(DPO3_obuf_I);
				dpo3_c.addPortConnection(LUT3_O[107]);
				//dpo4
				dpo4.addPortConnection(DPO4_obuf_O);
				dpo4.addPortConnection(RAM16X8D_replacement_DPO4);
				//dpo4_c
				dpo4_c.addPortConnection(DPO4_obuf_I);
				dpo4_c.addPortConnection(LUT3_O[116]);
				//dpo5
				dpo5.addPortConnection(DPO5_obuf_O);
				dpo5.addPortConnection(RAM16X8D_replacement_DPO5);
				//dpo5_c
				dpo5_c.addPortConnection(DPO5_obuf_I);
				dpo5_c.addPortConnection(LUT3_O[125]);
				//dpo6
				dpo6.addPortConnection(DPO6_obuf_O);
				dpo6.addPortConnection(RAM16X8D_replacement_DPO6);
				//dpo6_c
				dpo6_c.addPortConnection(DPO6_obuf_I);
				dpo6_c.addPortConnection(LUT3_O[134]);
				//dpo7
				dpo7.addPortConnection(DPO7_obuf_O);
				dpo7.addPortConnection(RAM16X8D_replacement_DPO7);
				//dpo7_c
				dpo7_c.addPortConnection(DPO7_obuf_I);
				dpo7_c.addPortConnection(LUT3_O[143]);
				//wires
				for(int i=0; i<FDENUM; i++)
				{
					int d = i/16;
					int q = i%16;
					wire[i].addPortConnection(FDE_Q[i]);
					if(q<8)
					{
						wire[i].addPortConnection(LUT3_I1[d*9+q]);
						wire[i].addPortConnection(LUT3_I1[d*9+q+LUT3NUM/2]);
					}
					else
					{
						wire[i].addPortConnection(LUT3_I2[d*9+q-8]);
						wire[i].addPortConnection(LUT3_I2[d*9+q-8+LUT3NUM/2]);
					}
				}
				//e
				for(int i=0; i<ENUM; i++)
				{
					e[i].addPortConnection(LUT4_O[i]);
					e[i].addPortConnection(FDE_CE[i]);
					e[i].addPortConnection(FDE_CE[i+16]);
					e[i].addPortConnection(FDE_CE[i+32]);
					e[i].addPortConnection(FDE_CE[i+48]);
					e[i].addPortConnection(FDE_CE[i+64]);
					e[i].addPortConnection(FDE_CE[i+80]);
					e[i].addPortConnection(FDE_CE[i+96]);
					e[i].addPortConnection(FDE_CE[i+112]);
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
	
	public static void Replace(EdifLibraryManager libManager, EdifCell RAM16X8D_replacement,
			EdifNet we, EdifNet d0, EdifNet d1, EdifNet d2, EdifNet d3, EdifNet d4, EdifNet d5, EdifNet d6, EdifNet d7, EdifNet wclk, 
			EdifNet a0, EdifNet a1, EdifNet a2, EdifNet a3, EdifNet dpra0, EdifNet dpra1, EdifNet dpra2, EdifNet dpra3, 
			EdifNet spo0, EdifNet spo1, EdifNet spo2, EdifNet spo3, EdifNet spo4, EdifNet spo5, EdifNet spo6, EdifNet spo7, 
			EdifNet dpo0, EdifNet dpo1, EdifNet dpo2, EdifNet dpo3, EdifNet dpo4, EdifNet dpo5, EdifNet dpo6, EdifNet dpo7) {
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
				int WIRENUM = 128;
				int NETNUM = 226;
				int ENUM = 16;
				//Declaration of wires, which are the outputs of FDEs
				EdifNet[] wire = new EdifNet[WIRENUM];
				for(int i=0; i<WIRENUM; i++)
				{
					String wirename;
					wirename = "wire" + String.valueOf(i);
					wire[i] = new EdifNet(wirename, RAM16X8D_replacement);
				}
				//Declaration of nets, which are used in MUX and decoder
				EdifNet[] net = new EdifNet[NETNUM];
				for(int i=0; i<NETNUM; i++)
				{
					String netname;
					netname = "net" + String.valueOf(i);
					net[i] = new EdifNet(netname, RAM16X8D_replacement);
				}
				//Declaration of enables
				EdifNet[] e = new EdifNet[ENUM];
				for(int i=0; i<ENUM; i++)
				{
					String ename;
					ename = "e" + String.valueOf(i);
					e[i] = new EdifNet(ename, RAM16X8D_replacement);
				}
				//Add all nets to the top level design
				for(int i=0; i<WIRENUM; i++)
				{
					RAM16X8D_replacement.addNet(wire[i]);
				}
				for(int i=0; i<NETNUM; i++)
				{
					RAM16X8D_replacement.addNet(net[i]);
				}
				for(int i=0; i<ENUM; i++)
				{
					RAM16X8D_replacement.addNet(e[i]);
				}
				
				/******Instances******/
				//Constants
				int FDENUM = 128;
				int LUT2NUM = 2;
				int LUT3NUM = 144;
				int LUT4NUM = 16;
				int MUXF5NUM = 64;
				int MUXF6NUM = 32;
				//FDEs
				EdifCellInstance[] FDE_ = new EdifCellInstance[FDENUM];
				for(int i=0; i<FDENUM; i++)
				{
					String fdename;
					fdename = "FDE" + String.valueOf(i);
					FDE_[i] = new EdifCellInstance(fdename, RAM16X8D_replacement, FDE);
				}
				//LUT2s
				EdifCellInstance[] LUT2_ = new EdifCellInstance[LUT2NUM];
				for(int i=0; i<LUT2NUM; i++)
				{
					String lut2name;
					lut2name = "LUT2_" + String.valueOf(i);
					LUT2_[i] = new EdifCellInstance(lut2name, RAM16X8D_replacement, LUT2);
				}
				//LUT3s
				EdifCellInstance[] LUT3_ = new EdifCellInstance[LUT3NUM];
				for(int i=0; i<LUT3NUM; i++)
				{
					String lut3name;
					lut3name = "LUT3_" + String.valueOf(i);
					LUT3_[i] = new EdifCellInstance(lut3name, RAM16X8D_replacement, LUT3);
				}
				//LUT4s
				EdifCellInstance[] LUT4_ = new EdifCellInstance[LUT4NUM];
				for(int i=0; i<LUT4NUM; i++)
				{
					String lut4name;
					lut4name = "LUT4_" + String.valueOf(i);
					LUT4_[i] = new EdifCellInstance(lut4name, RAM16X8D_replacement, LUT4);
				}
				//MUXF5
				EdifCellInstance[] MUXF5_ = new EdifCellInstance[MUXF5NUM];
				for(int i=0; i<MUXF5NUM; i++)
				{
					String muxf5name;
					muxf5name = "MUXF5_" + String.valueOf(i);
					MUXF5_[i] = new EdifCellInstance(muxf5name, RAM16X8D_replacement, MUXF5);
				}
				//MUXF6
				EdifCellInstance[] MUXF6_ = new EdifCellInstance[MUXF6NUM];
				for(int i=0; i<MUXF6NUM; i++)
				{
					String muxf6name;
					muxf6name = "MUXF6_" + String.valueOf(i);
					MUXF6_[i] = new EdifCellInstance(muxf6name, RAM16X8D_replacement, MUXF6);
				}
				//Add all instances to the top level design
				for(int i=0; i<FDENUM; i++)
				{
					RAM16X8D_replacement.addSubCell(FDE_[i]);
				}
				for(int i=0; i<LUT2NUM; i++)
				{
					RAM16X8D_replacement.addSubCell(LUT2_[i]);
				}
				for(int i=0; i<LUT3NUM; i++)
				{
					RAM16X8D_replacement.addSubCell(LUT3_[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					RAM16X8D_replacement.addSubCell(LUT4_[i]);
				}
				for(int i=0; i<MUXF5NUM; i++)
				{
					RAM16X8D_replacement.addSubCell(MUXF5_[i]);
				}
				for(int i=0; i<MUXF6NUM; i++)
				{
					RAM16X8D_replacement.addSubCell(MUXF6_[i]);
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
					else if(d==3)
						FDE_D[i] = new EdifPortRef(d3, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
					else if(d==4)
						FDE_D[i] = new EdifPortRef(d4, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
					else if(d==5)
						FDE_D[i] = new EdifPortRef(d5, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
					else if(d==6)
						FDE_D[i] = new EdifPortRef(d6, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
					else
						FDE_D[i] = new EdifPortRef(d7, FDEInterface_D[i].getSingleBitPort(0), FDE_[i]);
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
					if(d<8)		//SPO
					{
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
								LUT3_O[i] = new EdifPortRef(spo0, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==1)
								LUT3_O[i] = new EdifPortRef(spo1, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==2)
								LUT3_O[i] = new EdifPortRef(spo2, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==3)
								LUT3_O[i] = new EdifPortRef(spo3, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==4)
								LUT3_O[i] = new EdifPortRef(spo4, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==5)
								LUT3_O[i] = new EdifPortRef(spo5, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==6)
								LUT3_O[i] = new EdifPortRef(spo6, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else
								LUT3_O[i] = new EdifPortRef(spo7, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
					}
					else	//DPO
					{
						if(q!=8)
						{
							LUT3_I0[i] = new EdifPortRef(dpra3, LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(wire[q+16*(d-8)], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(wire[q+16*(d-8)+8], LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_O[i] = new EdifPortRef(net[q+14*d], LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
						}
						else
						{
							LUT3_I0[i] = new EdifPortRef(net[12+14*d], LUT3Interface_I0[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I1[i] = new EdifPortRef(net[13+14*d], LUT3Interface_I1[i].getSingleBitPort(0), LUT3_[i]);
							LUT3_I2[i] = new EdifPortRef(dpra0, LUT3Interface_I2[i].getSingleBitPort(0), LUT3_[i]);
							if(d==8)
								LUT3_O[i] = new EdifPortRef(dpo0, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==9)
								LUT3_O[i] = new EdifPortRef(dpo1, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==10)
								LUT3_O[i] = new EdifPortRef(dpo2, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==11)
								LUT3_O[i] = new EdifPortRef(dpo3, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==12)
								LUT3_O[i] = new EdifPortRef(dpo4, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==13)
								LUT3_O[i] = new EdifPortRef(dpo5, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else if(d==14)
								LUT3_O[i] = new EdifPortRef(dpo6, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
							else
								LUT3_O[i] = new EdifPortRef(dpo7, LUT3Interface_O[i].getSingleBitPort(0), LUT3_[i]);
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
					if(d<8)		//SPO
					{
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
					else	//DPO
					{
						if(q==0)
						{
							MUXF5_I0[i] = new EdifPortRef(net[0+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_I1[i] = new EdifPortRef(net[4+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_S[i] = new EdifPortRef(dpra2, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_O[i] = new EdifPortRef(net[8+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
						}
						else if(q==1)
						{
							MUXF5_I0[i] = new EdifPortRef(net[2+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_I1[i] = new EdifPortRef(net[6+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_S[i] = new EdifPortRef(dpra2, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_O[i] = new EdifPortRef(net[9+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
						}
						else if(q==2)
						{
							MUXF5_I0[i] = new EdifPortRef(net[1+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_I1[i] = new EdifPortRef(net[5+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_S[i] = new EdifPortRef(dpra2, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_O[i] = new EdifPortRef(net[10+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
						}
						else
						{
							MUXF5_I0[i] = new EdifPortRef(net[3+14*d], MUXF5Interface_I0[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_I1[i] = new EdifPortRef(net[7+14*d], MUXF5Interface_I1[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_S[i] = new EdifPortRef(dpra2, MUXF5Interface_S[i].getSingleBitPort(0), MUXF5_[i]);
							MUXF5_O[i] = new EdifPortRef(net[11+14*d], MUXF5Interface_O[i].getSingleBitPort(0), MUXF5_[i]);
						}
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
					if(d<8)		//SPO
					{
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
					else		//DPO
					{
						if(q==0)
						{
							MUXF6_I0[i] = new EdifPortRef(net[8+14*d], MUXF6Interface_I0[i].getSingleBitPort(0), MUXF6_[i]);
							MUXF6_I1[i] = new EdifPortRef(net[9+14*d], MUXF6Interface_I1[i].getSingleBitPort(0), MUXF6_[i]);
							MUXF6_S[i] = new EdifPortRef(dpra1, MUXF6Interface_S[i].getSingleBitPort(0), MUXF6_[i]);
							MUXF6_O[i] = new EdifPortRef(net[12+14*d], MUXF6Interface_O[i].getSingleBitPort(0), MUXF6_[i]);
						}
						else if(q==1)
						{
							MUXF6_I0[i] = new EdifPortRef(net[10+14*d], MUXF6Interface_I0[i].getSingleBitPort(0), MUXF6_[i]);
							MUXF6_I1[i] = new EdifPortRef(net[11+14*d], MUXF6Interface_I1[i].getSingleBitPort(0), MUXF6_[i]);
							MUXF6_S[i] = new EdifPortRef(dpra1, MUXF6Interface_S[i].getSingleBitPort(0), MUXF6_[i]);
							MUXF6_O[i] = new EdifPortRef(net[13+14*d], MUXF6Interface_O[i].getSingleBitPort(0), MUXF6_[i]);
						}
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
				//d4
				for(int i=64; i<80; i++)
				{
					d4.addPortConnection(FDE_D[i]);
				}
				//d5
				for(int i=80; i<96; i++)
				{
					d5.addPortConnection(FDE_D[i]);
				}
				//d6
				for(int i=96; i<112; i++)
				{
					d6.addPortConnection(FDE_D[i]);
				}
				//d7
				for(int i=112; i<128; i++)
				{
					d7.addPortConnection(FDE_D[i]);
				}
				//wclk
				for(int i=0; i<FDENUM; i++)
				{
					wclk.addPortConnection(FDE_C[i]);
				}
				//a0
				for(int i=0; i<LUT3NUM/2; i++)
				{
					if((i%9)==8)
						a0.addPortConnection(LUT3_I2[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a0.addPortConnection(LUT4_I1[i]);
				}
				//a1
				for(int i=0; i<MUXF6NUM/2; i++)
				{
					a1.addPortConnection(MUXF6_S[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a1.addPortConnection(LUT4_I2[i]);
				}
				//a2
				for(int i=0; i<MUXF5NUM/2; i++)
				{
					a2.addPortConnection(MUXF5_S[i]);
				}
				for(int i=0; i<LUT4NUM; i++)
				{
					a2.addPortConnection(LUT4_I3[i]);
				}
				//a3
				for(int i=0; i<LUT3NUM/2; i++)
				{
					if((i%9)!=8)
						a3.addPortConnection(LUT3_I0[i]);
				}
				for(int i=0; i<LUT2NUM; i++)
				{
					a3.addPortConnection(LUT2_I0[i]);
				}
				//dpra0
				for(int i=LUT3NUM/2; i<LUT3NUM; i++)
				{
					if((i%9)==8)
						dpra0.addPortConnection(LUT3_I2[i]);
				}
				//dpra1
				for(int i=MUXF6NUM/2; i<MUXF6NUM; i++)
				{
					dpra1.addPortConnection(MUXF6_S[i]);
				}
				//dpra2
				for(int i=MUXF5NUM/2; i<MUXF5NUM; i++)
				{
					dpra2.addPortConnection(MUXF5_S[i]);
				}
				//dpra3
				for(int i=LUT3NUM/2; i<LUT3NUM; i++)
				{
					if((i%9)!=8)
						dpra3.addPortConnection(LUT3_I0[i]);
				}
				//spo0
				spo0.addPortConnection(LUT3_O[8]);
				//spo1
				spo1.addPortConnection(LUT3_O[17]);
				//spo2
				spo2.addPortConnection(LUT3_O[26]);
				//spo3
				spo3.addPortConnection(LUT3_O[35]);
				//spo4
				spo4.addPortConnection(LUT3_O[44]);
				//spo5
				spo5.addPortConnection(LUT3_O[53]);
				//spo6
				spo6.addPortConnection(LUT3_O[62]);
				//spo7
				spo7.addPortConnection(LUT3_O[71]);
				//dpo0
				dpo0.addPortConnection(LUT3_O[80]);
				//dpo1
				dpo1.addPortConnection(LUT3_O[89]);
				//dpo2
				dpo2.addPortConnection(LUT3_O[98]);
				//dpo3
				dpo3.addPortConnection(LUT3_O[107]);
				//dpo4
				dpo4.addPortConnection(LUT3_O[116]);
				//dpo5
				dpo5.addPortConnection(LUT3_O[125]);
				//dpo6
				dpo6.addPortConnection(LUT3_O[134]);
				//dpo7
				dpo7.addPortConnection(LUT3_O[143]);
				//wires
				for(int i=0; i<FDENUM; i++)
				{
					int d = i/16;
					int q = i%16;
					wire[i].addPortConnection(FDE_Q[i]);
					if(q<8)
					{
						wire[i].addPortConnection(LUT3_I1[d*9+q]);
						wire[i].addPortConnection(LUT3_I1[d*9+q+LUT3NUM/2]);
					}
					else
					{
						wire[i].addPortConnection(LUT3_I2[d*9+q-8]);
						wire[i].addPortConnection(LUT3_I2[d*9+q-8+LUT3NUM/2]);
					}
				}
				//e
				for(int i=0; i<ENUM; i++)
				{
					e[i].addPortConnection(LUT4_O[i]);
					e[i].addPortConnection(FDE_CE[i]);
					e[i].addPortConnection(FDE_CE[i+16]);
					e[i].addPortConnection(FDE_CE[i+32]);
					e[i].addPortConnection(FDE_CE[i+48]);
					e[i].addPortConnection(FDE_CE[i+64]);
					e[i].addPortConnection(FDE_CE[i+80]);
					e[i].addPortConnection(FDE_CE[i+96]);
					e[i].addPortConnection(FDE_CE[i+112]);
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
