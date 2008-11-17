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
package edu.byu.ece.edif.tools.sterilize.lutreplace.RAM;

import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.EdifTypedValue;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.core.PropertyList;
import edu.byu.ece.edif.core.StringTypedValue;

/*
 * TODO: Needs to be modified to keep up with the changes in XilinxLibrary.java
 *       Needs to add ports to each primitive's interface
 *       Needs to use new EdifPort method to get a specific port of a primitive, 
 *       rather than using the .getPort method which is used now.
 */

public class RAM_Replacement {

	/** Enumerate all of the RAM types */
	public static final String RAM16X1D_STRING 		= 	"RAM16X1D";
	public static final String RAM16X1D_1_STRING	= 	"RAM16X1D_1";
	public static final String RAM16X1S_STRING 		= 	"RAM16X1S";
	public static final String RAM16X1S_1_STRING 	= 	"RAM16X1S_1";
	public static final String RAM16X2D_STRING 		= 	"RAM16X2D";
	public static final String RAM16X2S_STRING 		= 	"RAM16X2S";
	public static final String RAM16X4D_STRING 		= 	"RAM16X4D";
	public static final String RAM16X4S_STRING 		= 	"RAM16X4S";
	public static final String RAM16X8D_STRING 		= 	"RAM16X8D";
	public static final String RAM16X8S_STRING 		= 	"RAM16X8S";
	public static final String RAM32X1D_STRING 		= 	"RAM32X1D";
	public static final String RAM32X1D_1_STRING 	= 	"RAM32X1D_1";
	public static final String RAM32X1S_STRING 		= 	"RAM32X1S";
	public static final String RAM32X1S_1_STRING 	= 	"RAM32X1S_1";
	public static final String RAM32X2S_STRING 		= 	"RAM32X2S";
	public static final String RAM32X4S_STRING 		= 	"RAM32X4S";
	public static final String RAM32X8S_STRING 		= 	"RAM32X8S";
	public static final String RAM64X1D_STRING 		= 	"RAM64X1D";
	public static final String RAM64X1D_1_STRING 	= 	"RAM64X1D_1";
	public static final String RAM64X1S_STRING 		= 	"RAM64X1S";
	public static final String RAM64X1S_1_STRING 	= 	"RAM64X1S_1";
	public static final String RAM64X2S_STRING 		= 	"RAM64X2S";
	public static final String RAM128X1S_STRING 	= 	"RAM128X1S";
	public static final String RAM128X1S_1_STRING 	= 	"RAM128X1S_1";
	
	public enum RAMType {RAM16X1D, RAM16X1D_1, RAM16X1S, RAM16X1S_1, RAM16X2D, RAM16X2S, RAM16X4D, RAM16X4S, RAM16X8D, RAM16X8S,
		RAM32X1D, RAM32X1D_1, RAM32X1S, RAM32X1S_1, RAM32X2S, RAM32X4S, RAM32X8S, RAM64X1D, RAM64X1D_1, RAM64X1S, RAM64X1S_1, RAM64X2S,
		RAM128X1S, RAM128X1S_1};

	public static RAMType StringToRAMType(String str) {
		if (str.equalsIgnoreCase(RAM16X1D_STRING)) 		return RAMType.RAM16X1D;
		if (str.equalsIgnoreCase(RAM16X1D_1_STRING)) 	return RAMType.RAM16X1D_1;
		if (str.equalsIgnoreCase(RAM16X1S_STRING)) 		return RAMType.RAM16X1S;
		if (str.equalsIgnoreCase(RAM16X1S_1_STRING))	return RAMType.RAM16X1S_1;
		if (str.equalsIgnoreCase(RAM16X2D_STRING)) 		return RAMType.RAM16X2D;
		if (str.equalsIgnoreCase(RAM16X2S_STRING)) 		return RAMType.RAM16X2S;
		if (str.equalsIgnoreCase(RAM16X4D_STRING)) 		return RAMType.RAM16X4D;
		if (str.equalsIgnoreCase(RAM16X4S_STRING)) 		return RAMType.RAM16X4S;
		if (str.equalsIgnoreCase(RAM16X8D_STRING)) 		return RAMType.RAM16X8D;
		if (str.equalsIgnoreCase(RAM16X8S_STRING)) 		return RAMType.RAM16X8S;
		if (str.equalsIgnoreCase(RAM32X1D_STRING)) 		return RAMType.RAM32X1D;
		if (str.equalsIgnoreCase(RAM32X1D_1_STRING)) 	return RAMType.RAM32X1D_1;
		if (str.equalsIgnoreCase(RAM32X1S_STRING)) 		return RAMType.RAM32X1S;
		if (str.equalsIgnoreCase(RAM32X1S_1_STRING)) 	return RAMType.RAM32X1S_1;
		if (str.equalsIgnoreCase(RAM32X2S_STRING)) 		return RAMType.RAM32X2S;
		if (str.equalsIgnoreCase(RAM32X4S_STRING)) 		return RAMType.RAM32X4S;
		if (str.equalsIgnoreCase(RAM32X8S_STRING)) 		return RAMType.RAM32X8S;
		if (str.equalsIgnoreCase(RAM64X1D_STRING)) 		return RAMType.RAM64X1D;
		if (str.equalsIgnoreCase(RAM64X1D_1_STRING)) 	return RAMType.RAM64X1D_1;
		if (str.equalsIgnoreCase(RAM64X1S_STRING)) 		return RAMType.RAM64X1S;
		if (str.equalsIgnoreCase(RAM64X1S_1_STRING)) 	return RAMType.RAM64X1S_1;
		if (str.equalsIgnoreCase(RAM64X2S_STRING)) 		return RAMType.RAM64X2S;
		if (str.equalsIgnoreCase(RAM128X1S_STRING)) 	return RAMType.RAM128X1S;
		if (str.equalsIgnoreCase(RAM128X1S_1_STRING)) 	return RAMType.RAM128X1S_1;
		return null;
	}
	
	public static void Replace(EdifLibraryManager libManager, String ramType, 
			EdifCell parent, String namePrefix, long INIT, long INIT_00, long INIT_01, long INIT_02, long INIT_03, 
			long INIT_04, long INIT_05, long INIT_06, long INIT_07, long INIT_HIGH, long INIT_LOW,
			EdifNet we, EdifNet wclk, EdifNet d, EdifNet d0, EdifNet d1, EdifNet d2, EdifNet d3, 
			EdifNet d_0, EdifNet d_1, EdifNet d_2, EdifNet d_3, EdifNet d_4, EdifNet d_5, EdifNet d_6, EdifNet d_7,
			EdifNet a0, EdifNet a1, EdifNet a2, EdifNet a3, EdifNet a4, EdifNet a5, EdifNet a6,
			EdifNet dpra0, EdifNet dpra1, EdifNet dpra2, EdifNet dpra3, EdifNet dpra4, EdifNet dpra5,
			EdifNet spo, EdifNet spo0, EdifNet spo1, EdifNet spo2, EdifNet spo3, EdifNet spo_0, EdifNet spo_1, EdifNet spo_2,
			EdifNet spo_3, EdifNet spo_4, EdifNet spo_5, EdifNet spo_6, EdifNet spo_7, EdifNet dpo, EdifNet dpo0, EdifNet dpo1, 
			EdifNet dpo2, EdifNet dpo3, EdifNet dpo_0, EdifNet dpo_1, EdifNet dpo_2, EdifNet dpo_3, EdifNet dpo_4, EdifNet dpo_5, 
			EdifNet dpo_6, EdifNet dpo_7, EdifNet o, EdifNet o0, EdifNet o1, EdifNet o2, EdifNet o3, EdifNet o_0, EdifNet o_1,
			EdifNet o_2, EdifNet o_3, EdifNet o_4, EdifNet o_5, EdifNet o_6, EdifNet o_7) {
		RAMType type = StringToRAMType(ramType);
		if (type == null)
			return;
		Replace(libManager, type, parent, namePrefix, INIT, INIT_00, INIT_01, INIT_02, INIT_03, INIT_04, INIT_05, INIT_06, INIT_07, 
				INIT_HIGH, INIT_LOW, we, wclk, d, d0, d1, d2, d3, d_0, d_1, d_2, d_3, d_4, d_5, d_6, d_7, a0, a1, a2, a3, a4, a5, a6,
				dpra0, dpra1, dpra2, dpra3, dpra4, dpra5, spo, spo0, spo1, spo2, spo3, spo_0, spo_1, spo_2, spo_3, spo_4, spo_5, spo_6, spo_7, 
				dpo, dpo0, dpo1, dpo2, dpo3, dpo_0, dpo_1, dpo_2, dpo_3, dpo_4, dpo_5, dpo_6, dpo_7, o, o0, o1, o2, o3, o_0, o_1, o_2, o_3, 
				o_4, o_5, o_6, o_7);
	}
	
	public static void Replace(EdifLibraryManager libManager, RAMType ramType, 
			EdifCell parent, String namePrefix, long INIT, long INIT_00, long INIT_01, long INIT_02, long INIT_03, 
			long INIT_04, long INIT_05, long INIT_06, long INIT_07, long INIT_HIGH, long INIT_LOW,
			EdifNet we, EdifNet wclk, EdifNet d, EdifNet d0, EdifNet d1, EdifNet d2, EdifNet d3, 
			EdifNet d_0, EdifNet d_1, EdifNet d_2, EdifNet d_3, EdifNet d_4, EdifNet d_5, EdifNet d_6, EdifNet d_7,
			EdifNet a0, EdifNet a1, EdifNet a2, EdifNet a3, EdifNet a4, EdifNet a5, EdifNet a6,
			EdifNet dpra0, EdifNet dpra1, EdifNet dpra2, EdifNet dpra3, EdifNet dpra4, EdifNet dpra5,
			EdifNet spo, EdifNet spo0, EdifNet spo1, EdifNet spo2, EdifNet spo3, EdifNet spo_0, EdifNet spo_1, EdifNet spo_2,
			EdifNet spo_3, EdifNet spo_4, EdifNet spo_5, EdifNet spo_6, EdifNet spo_7, EdifNet dpo, EdifNet dpo0, EdifNet dpo1, 
			EdifNet dpo2, EdifNet dpo3, EdifNet dpo_0, EdifNet dpo_1, EdifNet dpo_2, EdifNet dpo_3, EdifNet dpo_4, EdifNet dpo_5, 
			EdifNet dpo_6, EdifNet dpo_7, EdifNet o, EdifNet o0, EdifNet o1, EdifNet o2, EdifNet o3, EdifNet o_0, EdifNet o_1,
			EdifNet o_2, EdifNet o_3, EdifNet o_4, EdifNet o_5, EdifNet o_6, EdifNet o_7) {
		
		/****** Step 1. Create/Find Xilinx primitive Cells needed for this replacement ******/
		// Add all of the cells that are needed for all of the different cases.	
		EdifCell FDE = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FDE");
		EdifCell FDE_1 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FDE_1");
		EdifCell MUXF5 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "MUXF5");
		EdifCell LUT4 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "LUT4");
		EdifCell GND = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "GND");
		
		/****** Step 2. Get information for the specific RAM ******/
		EdifCell ff_type = null;
		int depth = 0;
		int width = 0;
		int ffInstanceNumber = 0;
		int muxInstanceNumber = 0;
		int lut4InstanceNumber = 0;
		int ffNetNumber = 0;
		int muxNetNumber = 0;
		int decoderNetNumber = 0;
		int eNetNumber = 0;
		int outputPortNumber = 0;
		switch(ramType) {
		   case RAM16X1D: {
			   ff_type = FDE;
			   depth = 16;
			   width = 1;
			   outputPortNumber = 2;
			   lut4InstanceNumber = 18;
			   decoderNetNumber = 2;
			   break;
		   }
		   case RAM16X1D_1: {
			   ff_type = FDE_1;
			   depth = 16;
			   width = 1;
			   outputPortNumber = 2;
			   lut4InstanceNumber = 18;
			   decoderNetNumber = 2;
			   break;
		   }
		   case RAM16X1S: {
			   ff_type = FDE;
			   depth = 16;
			   width = 1;
			   outputPortNumber = 1;
			   lut4InstanceNumber = 18;
			   decoderNetNumber = 2;
			   break;
		   }
		   case RAM16X1S_1: {
			   ff_type = FDE_1;
			   depth = 16;
			   width = 1;
			   outputPortNumber = 1;
			   lut4InstanceNumber = 18;
			   decoderNetNumber = 2;
			   break;
		   }
		   case RAM16X2D: {
			   ff_type = FDE;
			   depth = 16;
			   width = 2;
			   outputPortNumber = 2;
			   lut4InstanceNumber = 18;
			   decoderNetNumber = 2;
			   break;
		   }
		   case RAM16X2S: {
			   ff_type = FDE;
			   depth = 16;
			   width = 2;
			   outputPortNumber = 1;
			   lut4InstanceNumber = 18;
			   decoderNetNumber = 2;
			   break;
		   }
		   case RAM16X4D: {
			   ff_type = FDE;
			   depth = 16;
			   width = 4;
			   outputPortNumber = 2;
			   lut4InstanceNumber = 18;
			   decoderNetNumber = 2;
			   break;
		   }
		   case RAM16X4S: {
			   ff_type = FDE;
			   depth = 16;
			   width = 4;
			   outputPortNumber = 1;
			   lut4InstanceNumber = 18;
			   decoderNetNumber = 2;
			   break;
		   }
		   case RAM16X8D: {
			   ff_type = FDE;
			   depth = 16;
			   width = 8;
			   outputPortNumber = 2;
			   lut4InstanceNumber = 18;
			   decoderNetNumber = 2;
			   break;
		   }
		   case RAM16X8S: {
			   ff_type = FDE;
			   depth = 16;
			   width = 8;
			   outputPortNumber = 1;
			   lut4InstanceNumber = 18;
			   decoderNetNumber = 2;
			   break;
		   }
		   case RAM32X1D: {
			   ff_type = FDE;
			   depth = 32;
			   width = 1;
			   outputPortNumber = 2;
			   lut4InstanceNumber = 36;
			   decoderNetNumber = 4;
			   break;
		   }
		   case RAM32X1D_1:	{
			   ff_type = FDE_1;
			   depth = 32;
			   width = 1;
			   outputPortNumber = 2;
			   lut4InstanceNumber = 36;
			   decoderNetNumber = 4;
			   break;
		   }
		   case RAM32X1S: {
			   ff_type = FDE;
			   depth = 32;
			   width = 1;
			   outputPortNumber = 1;
			   lut4InstanceNumber = 36;
			   decoderNetNumber = 4;
			   break;
		   }
		   case RAM32X1S_1:	{
			   ff_type = FDE_1;
			   depth = 32;
			   width = 1;
			   outputPortNumber = 1;
			   lut4InstanceNumber = 36;
			   decoderNetNumber = 4;
			   break;
		   }
		   case RAM32X2S: {
			   ff_type = FDE;
			   depth = 32;
			   width = 2;
			   outputPortNumber = 1;
			   lut4InstanceNumber = 36;
			   decoderNetNumber = 4;
			   break;
		   }
		   case RAM32X4S: {
			   ff_type = FDE;
			   depth = 32;
			   width = 4;
			   outputPortNumber = 1;
			   lut4InstanceNumber = 36;
			   decoderNetNumber = 4;
			   break;
		   }
		   case RAM32X8S: {
			   ff_type = FDE;
			   depth = 32;
			   width = 8;
			   outputPortNumber = 1;
			   lut4InstanceNumber = 36;
			   decoderNetNumber = 4;
			   break;
		   }
		   case RAM64X1D: {
			   ff_type = FDE;
			   depth = 64;
			   width = 1;
			   outputPortNumber = 2;
			   lut4InstanceNumber = 72;
			   decoderNetNumber = 8;
			   break;
		   }
		   case RAM64X1D_1: {
			   ff_type = FDE_1;
			   depth = 64;
			   width = 1;
			   outputPortNumber = 2;
			   lut4InstanceNumber = 72;
			   decoderNetNumber = 8;
			   break;
		   }
		   case RAM64X1S: {
			   ff_type = FDE;
			   depth = 64;
			   width = 1;
			   outputPortNumber = 1;
			   lut4InstanceNumber = 72;
			   decoderNetNumber = 8;
			   break;
		   }
		   case RAM64X1S_1: {
			   depth = 64;
			   width = 1;
			   outputPortNumber = 1;
			   lut4InstanceNumber = 72;
			   decoderNetNumber = 8;
			   break;
		   }
		   case RAM64X2S: {
			   ff_type = FDE;
			   depth = 64;
			   width = 2;
			   outputPortNumber = 1;
			   lut4InstanceNumber = 72;
			   decoderNetNumber = 8;
			   break;
		   }
		   case RAM128X1S: {
			   ff_type = FDE;
			   depth = 128;
			   width = 1;
			   outputPortNumber = 1;
			   lut4InstanceNumber = 146;
			   decoderNetNumber = 18;
			   break;
		   }
		   case RAM128X1S_1: {
			   ff_type = FDE_1;
			   depth = 128;
			   width = 1;
			   outputPortNumber = 1;
			   lut4InstanceNumber = 146;
			   decoderNetNumber = 18;
		   }
		}
		ffInstanceNumber = depth * width;
		muxInstanceNumber = (depth-1) * width * outputPortNumber;
		ffNetNumber = ffInstanceNumber;
		muxNetNumber = (depth-2) * width * outputPortNumber;
		eNetNumber = depth;
		
		/****** Step 3. Create the FF instances ******/
		EdifCellInstance ffInstances[] = new EdifCellInstance[ffInstanceNumber];		
		for (int i = 0; i < ffInstanceNumber; i++) {
			String ffInstanceName = namePrefix + "_FF_" + Integer.toString(i);
			EdifNameable ffInstanceNameable = NamedObject.createValidEdifNameable(ffInstanceName);
			ffInstanceNameable = parent.getUniqueInstanceNameable(ffInstanceNameable);
			ffInstances[i] = new EdifCellInstance(ffInstanceNameable, parent, ff_type);
			try {
				parent.addSubCell(ffInstances[i]);
			} catch (EdifNameConflictException e) {
				// Should not get here
			}
		}
		
		/****** Step 4. Create the Mux instances ******/
		EdifCellInstance muxInstances[] = new EdifCellInstance[muxInstanceNumber];
		for (int i = 0; i < muxInstanceNumber; i++) {
			String muxInstanceName = namePrefix + "_MUX_" + Integer.toString(i);
			EdifNameable muxInstanceNameable = NamedObject.createValidEdifNameable(muxInstanceName);
			muxInstanceNameable = parent.getUniqueInstanceNameable(muxInstanceNameable);
			muxInstances[i] = new EdifCellInstance(muxInstanceNameable, parent, MUXF5);
			try {
				parent.addSubCell(muxInstances[i]);
			} catch (EdifNameConflictException e) {
				//Should not get here
			}
		}
		
		/****** Step 5. Create the LUT4 instances ******/
		EdifCellInstance lut4Instances[] = new EdifCellInstance[lut4InstanceNumber];
		for (int i = 0; i < lut4InstanceNumber; i++) {
			String lut4InstanceName = namePrefix + "_LUT4_" + Integer.toString(i);
			EdifNameable lut4InstanceNameable = NamedObject.createValidEdifNameable(lut4InstanceName);
			lut4InstanceNameable = parent.getUniqueInstanceNameable(lut4InstanceNameable);
			lut4Instances[i] = new EdifCellInstance(lut4InstanceNameable, parent, LUT4);
			try {
				parent.addSubCell(lut4Instances[i]);
			} catch (EdifNameConflictException e) {
				//Should not get here
			}
		}
		
		/****** Step 6. Create the GND instance ******/
		String gndInstanceName = namePrefix + "_GND";
		EdifNameable gndInstanceNameable = NamedObject.createValidEdifNameable(gndInstanceName);
		gndInstanceNameable = parent.getUniqueInstanceNameable(gndInstanceNameable);
		EdifCellInstance gndInstance = new EdifCellInstance(gndInstanceNameable, parent, GND);
		try {
			parent.addSubCell(gndInstance);
		} catch (EdifNameConflictException e) {
			//Should not get here
		}
		
		/****** Step 7. Create the FF nets ******/
		EdifNet ffOutputNets[] = new EdifNet[ffNetNumber];
		for (int i = 0; i < ffNetNumber; i++) {
			String netName = namePrefix + "_o_" + Integer.toString(i);
			EdifNameable netNameable = NamedObject.createValidEdifNameable(netName);
			netNameable = parent.getUniqueNetNameable(netNameable);
			ffOutputNets[i] = new EdifNet(netNameable, parent);
			try {
				parent.addNet(ffOutputNets[i]);
			} catch (EdifNameConflictException e) {
				// Should not get here
			}
		}
		
		/****** Step 8. Create the mux nets ******/
		EdifNet muxOutputNets[] = new EdifNet[muxNetNumber];
		for (int i = 0; i < muxNetNumber; i++) {
			String netName = namePrefix + "_m_" + Integer.toString(i);
			EdifNameable netNameable = NamedObject.createValidEdifNameable(netName);
			netNameable = parent.getUniqueNetNameable(netNameable);
			muxOutputNets[i] = new EdifNet(netNameable, parent);
			try {
				parent.addNet(muxOutputNets[i]);
			} catch (EdifNameConflictException e) {
				// Should not get here
			}
		}
		
		/****** Step 9. Create the decoder nets ******/
		EdifNet decoderConnectionNets[] = new EdifNet[decoderNetNumber];
		for (int i = 0; i < decoderNetNumber; i++) {
			String netName = namePrefix + "_d_" + Integer.toString(i);
			EdifNameable netNameable = NamedObject.createValidEdifNameable(netName);
			netNameable = parent.getUniqueNetNameable(netNameable);
			decoderConnectionNets[i] = new EdifNet(netNameable, parent);
			try {
				parent.addNet(decoderConnectionNets[i]);
			} catch (EdifNameConflictException e) {
				// Should not get here
			}
		}
		
		/****** Step 10. Create enable nets ******/
		EdifNet eNets[] = new EdifNet[eNetNumber];
		for (int i = 0; i < eNetNumber; i++) {
			String netName = namePrefix + "_e_" + Integer.toString(i);
			EdifNameable netNameable = NamedObject.createValidEdifNameable(netName);
			netNameable = parent.getUniqueNetNameable(netNameable);
			eNets[i] = new EdifNet(netNameable, parent);
			try {
				parent.addNet(eNets[i]);
			} catch (EdifNameConflictException e) {
				// Should not get here
			}
		}
		
		/****** Step 11. Create GND nets and connect it to the GND instance ******/
		String gnetName = namePrefix + "_g";
		EdifNameable gnetNameable = NamedObject.createValidEdifNameable(gnetName);
		gnetNameable = parent.getUniqueNetNameable(gnetNameable);
		EdifNet gNet = new EdifNet(gnetNameable, parent);
		try {
			parent.addNet(gNet);
		} catch (EdifNameConflictException e) {
			// Should not get here
		}
		EdifPort gPort = GND.getPort("G");
		if (gPort == null) {
			System.err.println("Can't find G port on cell " + GND);
			System.exit(1);
		}
		EdifSingleBitPort gESBP = gPort.getSingleBitPort(0);
		EdifPortRef gEPR = new EdifPortRef(gNet, gESBP, gndInstance);
		gNet.addPortConnection(gEPR);
		
		/****** Step 12. Wire up clocks ******/
		EdifPort clkPort = ff_type.getPort("C");
		if (clkPort == null) {
			System.err.println("Can't find C port on cell " + ff_type);
			System.exit(1);
		}
		EdifSingleBitPort clkESBP = clkPort.getSingleBitPort(0);
		for (int i = 0; i < ffInstanceNumber; i++) {
			EdifPortRef wclkEPR = new EdifPortRef(wclk, clkESBP, ffInstances[i]);
			wclk.addPortConnection(wclkEPR);
		}
		
		/****** Step 13. Wire up enable signals ******/
		EdifPort cePort = ff_type.getPort("CE");
		if (cePort == null) {
			System.err.println("Can't find CE port on cell " + ff_type);
			System.exit(1);
		}
		EdifSingleBitPort ceESBP = cePort.getSingleBitPort(0);
		for(int i = 0; i < ffInstanceNumber; i++) {
			EdifPortRef ceEPR = new EdifPortRef(eNets[i%depth], ceESBP, ffInstances[i]);
			eNets[i%depth].addPortConnection(ceEPR);
		}

		/****** Step 14. Hook up D ports ******/
		// Get D port information of the specific RAM and wire it up
		switch (width) {
		case 1: {
			EdifPort dPort = ff_type.getPort("D");
			if (dPort == null) {
				System.err.println("Can't find D port on cell " + ff_type);
				System.exit(1);
			}
			EdifSingleBitPort dESBP = dPort.getSingleBitPort(0);
			for(int i = 0; i < depth; i++) {
				EdifPortRef dEPR = new EdifPortRef(d, dESBP, ffInstances[i]);
				d.addPortConnection(dEPR);
			}
			break;
		}
		case 2: 
		case 4: {
			for (int i = 0; i < width; i++) {
				EdifPort dPort = ff_type.getPort("D");
				if (dPort == null) {
					System.err.println("Can't find D port on cell " + ff_type);
					System.exit(1);
				}
				EdifSingleBitPort dESBP = dPort.getSingleBitPort(0);
				EdifNet inputNet = null;
				switch (i) {
				case 0: inputNet = d0; break;
				case 1: inputNet = d1; break;
				case 2: inputNet = d2; break;
				case 3: inputNet = d3;
				}
				for (int j = i*depth; j < (i+1)*depth; j++) {
					EdifPortRef dEPR = new EdifPortRef(inputNet, dESBP, ffInstances[j]);
					inputNet.addPortConnection(dEPR);
				}
			}
			break;
		}
		case 8: {
			for (int i = 0; i < 8; i++) {
				EdifPort dPort = ff_type.getPort("D");
				if (dPort == null) {
					System.err.println("Can't find D port on cell " + ff_type);
					System.exit(1);
				}
				EdifSingleBitPort dESBP = dPort.getSingleBitPort(0);
				EdifNet inputNet = null;
				switch (i) {
				case 0: inputNet = d_0; break;
				case 1: inputNet = d_1; break;
				case 2: inputNet = d_2; break;
				case 3: inputNet = d_3; break;
				case 4: inputNet = d_4; break;
				case 5: inputNet = d_5; break;
				case 6: inputNet = d_6; break;
				case 7: inputNet = d_7;
				}
				for (int j = i*depth; j < (i+1)*depth; j++) {
					EdifPortRef dEPR = new EdifPortRef(inputNet, dESBP, ffInstances[j]);
					inputNet.addPortConnection(dEPR);
				}
			}
		}
		}
		
		/****** Step 15. Hook up Q ports ******/
		EdifPort qPort = ff_type.getPort("Q");
		if (qPort == null) {
			System.err.println("Can't find Q port on cell " + ff_type);
			System.exit(1);
		}
		EdifSingleBitPort qESBP = qPort.getSingleBitPort(0);
		for (int i = 0; i < ffNetNumber; i++) {
			EdifPortRef qEPR = new EdifPortRef(ffOutputNets[i], qESBP, ffInstances[i]);
			ffOutputNets[i].addPortConnection(qEPR);
		}
		
		/****** Step 16. Hook up the muxes ******/
		// Get port information
		EdifPort mI0Port = MUXF5.getPort("I0");
		if (mI0Port == null) {
			System.err.println("Can't find I0 port on cell " + MUXF5);
			System.exit(1);
		}
		EdifPort mI1Port = MUXF5.getPort("I1");
		if (mI1Port == null) {
			System.err.println("Can't find I1 port on cell " + MUXF5);
			System.exit(1);
		}
		EdifPort mSPort = MUXF5.getPort("S");
		if (mSPort == null) {
			System.err.println("Can't find S port on cell " + MUXF5);
			System.exit(1);
		}
		EdifPort mOPort = MUXF5.getPort("O");
		if (mOPort == null) {
			System.err.println("Can't find O port on cell " + MUXF5);
			System.exit(1);
		}
		EdifSingleBitPort mI0ESBP = mI0Port.getSingleBitPort(0);
		EdifSingleBitPort mI1ESBP = mI1Port.getSingleBitPort(0);
		EdifSingleBitPort mSESBP = mSPort.getSingleBitPort(0);
		EdifSingleBitPort mOESBP = mOPort.getSingleBitPort(0);
		// Hook up 2-1 muxes to get large mux
		int muxf5Number = depth - 1;	// the number of muxf5 instances needed for a single XX to 1 MUX
		int netNumber = depth - 2;		// the number of wires needed for muxf5 connection in a single XX to 1 MUX
		for (int i = 0; i < muxNetNumber; i++) {
			int muxNumber;
			int r = i % netNumber;
			int q = i / netNumber;
			if (r%2 == 0) {
				muxNumber = (r+depth) / 2 + muxf5Number*q;
				EdifPortRef mI0EPR = new EdifPortRef(muxOutputNets[i], mI0ESBP, muxInstances[muxNumber]);
				muxOutputNets[i].addPortConnection(mI0EPR);
			} else {
				muxNumber = (r+depth-1) / 2 + muxf5Number*q;
				EdifPortRef mI1EPR = new EdifPortRef(muxOutputNets[i], mI1ESBP, muxInstances[muxNumber]);
				muxOutputNets[i].addPortConnection(mI1EPR);
			}
			muxNumber = r + muxf5Number * q;
			EdifPortRef mOEPR = new EdifPortRef(muxOutputNets[i], mOESBP, muxInstances[muxNumber]);
			muxOutputNets[i].addPortConnection(mOEPR);
		}
		// Hook up FFs and the mux
		for (int i = 0; i < ffNetNumber; i++) {
			int muxNumber;
			int r = i % depth;
			int q = i / depth;
			if(r%2 == 0) {
				muxNumber = r/2 + muxf5Number*q;
				EdifPortRef mI0EPR = new EdifPortRef(ffOutputNets[i], mI0ESBP, muxInstances[muxNumber]);
				ffOutputNets[i].addPortConnection(mI0EPR);
				if(outputPortNumber == 2) {	// dual output ports
					muxNumber = r/2 + muxf5Number*q + muxf5Number*width;
					mI0EPR = new EdifPortRef(ffOutputNets[i], mI0ESBP, muxInstances[muxNumber]);
					ffOutputNets[i].addPortConnection(mI0EPR);
				}
			} else {
				muxNumber = r/2 + muxf5Number*q;
				EdifPortRef mI1EPR = new EdifPortRef(ffOutputNets[i], mI1ESBP, muxInstances[muxNumber]);
				ffOutputNets[i].addPortConnection(mI1EPR);
				if(outputPortNumber == 2) {	// dual output ports
					muxNumber = r/2 + muxf5Number*q + muxf5Number*width;
					mI1EPR = new EdifPortRef(ffOutputNets[i], mI1ESBP, muxInstances[muxNumber]);
					ffOutputNets[i].addPortConnection(mI1EPR);
				}
			}
		}
		// Hoop up select signals (a0 ~ a6, dpra0 ~ dpra5)
		for (int i = 0; i < muxf5Number*width; i++) {
			EdifNet aNet = null;
			int r = i % muxf5Number;
			if (r < depth/2) {
				aNet = a0;
			} else if (r < depth/2 + depth/4) {
				aNet = a1;
			} else if (r < depth/2 + depth/4 + depth/8) {
				aNet = a2;
			} else if (r < depth/2 + depth/4 + depth/8 + depth/16) {
				aNet = a3;
			} else if (r < depth/2 + depth/4 + depth/8 + depth/16 + depth/32) {
				aNet = a4;
			} else if (r < depth/2 + depth/4 + depth/8 + depth/16 + depth/32 + depth/64) {
				aNet = a5;
			} else if (r < depth/2 + depth/4 + depth/8 + depth/16 + depth/32 + depth/64 + depth/128) {
				aNet = a6;
			}
			EdifPortRef aEPR = new EdifPortRef(aNet, mSESBP, muxInstances[i]);
			aNet.addPortConnection(aEPR);
		}
		for (int i = muxf5Number*width; i < muxf5Number*width*outputPortNumber; i++) {
			EdifNet dpraNet = null;
			int r = i % muxf5Number;
			if (r < depth/2) {
				dpraNet = dpra0;
			} else if (r < depth/2 + depth/4) {
				dpraNet = dpra1;
			} else if (r < depth/2 + depth/4 + depth/8) {
				dpraNet = dpra2;
			} else if (r < depth/2 + depth/4 + depth/8 + depth/16) {
				dpraNet = dpra3;
			} else if (r < depth/2 + depth/4 + depth/8 + depth/16 + depth/32) {
				dpraNet = dpra4;
			} else if (r < depth/2 + depth/4 + depth/8 + depth/16 + depth/32 + depth/64) {
				dpraNet = dpra5;
			}
			EdifPortRef dpraEPR = new EdifPortRef(dpraNet, mSESBP, muxInstances[i]);
			dpraNet.addPortConnection(dpraEPR);
		}
		// Hook up mux output to RAM output
		if (outputPortNumber == 1) {		// single output port
			if (width == 1) {								// word width = 1
				EdifNet outputNet = o;
				EdifPortRef outputEPR = new EdifPortRef(outputNet, mOESBP, muxInstances[muxInstanceNumber-1]);
				outputNet.addPortConnection(outputEPR);
			} else if (width == 2 || width == 4) {			// word width = 2 or 4
				for (int i = 0; i < muxInstanceNumber; i++) {
					EdifNet outputNet = null;
					if((i%muxf5Number) == (muxf5Number-1)) {
						switch (i/muxf5Number) {
						case 0: outputNet = o0; break;
						case 1: outputNet = o1; break;
						case 2: outputNet = o2; break;
						case 3: outputNet = o3;
						}
						EdifPortRef outputEPR = new EdifPortRef(outputNet, mOESBP, muxInstances[i]);
						outputNet.addPortConnection(outputEPR);
					}
				}
			} else {										// word width = 8
				for (int i = 0; i < muxInstanceNumber; i++) {
					EdifNet outputNet = null;
					if((i%muxf5Number) == (muxf5Number-1)) {
						switch (i/muxf5Number) {
						case 0: outputNet = o_0; break;
						case 1: outputNet = o_1; break;
						case 2: outputNet = o_2; break;
						case 3: outputNet = o_3; break;
						case 4: outputNet = o_4; break;
						case 5: outputNet = o_5; break;
						case 6: outputNet = o_6; break;
						case 7: outputNet = o_7;
						}
						EdifPortRef outputEPR = new EdifPortRef(outputNet, mOESBP, muxInstances[i]);
						outputNet.addPortConnection(outputEPR);
					}
				}
			}
		} else {							 // dual output ports
			if (width == 1) {								// word width = 1
				EdifNet dpoNet = dpo;
				EdifNet spoNet = spo;
				EdifPortRef dpoEPR = new EdifPortRef(dpoNet, mOESBP, muxInstances[muxInstanceNumber/2-1]);
				EdifPortRef spoEPR = new EdifPortRef(spoNet, mOESBP, muxInstances[muxInstanceNumber-1]);
				dpoNet.addPortConnection(dpoEPR);
				spoNet.addPortConnection(spoEPR);
			} else if (width == 2 || width == 4) {			// word width = 2 or 4
				for (int i = 0; i < muxInstanceNumber/2; i++) {
					EdifNet dpoNet = null;
					EdifNet spoNet = null;
					if((i%muxf5Number) == (muxf5Number-1)) {
						switch (i/muxf5Number) {
						case 0: dpoNet = dpo0; spoNet = spo0; break;
						case 1: dpoNet = dpo1; spoNet = spo1; break;
						case 2: dpoNet = dpo2; spoNet = spo2; break;
						case 3: dpoNet = dpo3; spoNet = spo3;
						}
					}
					EdifPortRef dpoEPR = new EdifPortRef(dpoNet, mOESBP, muxInstances[i]);
					EdifPortRef spoEPR = new EdifPortRef(spoNet, mOESBP, muxInstances[i + muxInstanceNumber/2]);
					dpoNet.addPortConnection(dpoEPR);
					spoNet.addPortConnection(spoEPR);
				}
			} else {										// word width = 8
				for (int i = 0; i < muxInstanceNumber/2; i++) {
					EdifNet dpoNet = null;
					EdifNet spoNet = null;
					if((i%muxf5Number) == (muxf5Number-1)) {
						switch (i/muxf5Number) {
						case 0: dpoNet = dpo_0; spoNet = spo_0; break;
						case 1: dpoNet = dpo_1; spoNet = spo_1; break;
						case 2: dpoNet = dpo_2; spoNet = spo_2; break;
						case 3: dpoNet = dpo_3; spoNet = spo_3; break;
						case 4: dpoNet = dpo_4; spoNet = spo_4; break;
						case 5: dpoNet = dpo_5; spoNet = spo_5; break;
						case 6: dpoNet = dpo_6; spoNet = spo_6; break;
						case 7: dpoNet = dpo_7; spoNet = spo_7;
						}
					}
					EdifPortRef dpoEPR = new EdifPortRef(dpoNet, mOESBP, muxInstances[i]);
					EdifPortRef spoEPR = new EdifPortRef(spoNet, mOESBP, muxInstances[i + muxInstanceNumber/2]);
					dpoNet.addPortConnection(dpoEPR);
					spoNet.addPortConnection(spoEPR);
				}
			}
		}
		
		/****** Step 17. Hook up the decoder  ******/
		// Get port information
		EdifPort lI0Port = LUT4.getPort("I0");
		if (lI0Port == null) {
			System.err.println("Can't find I0 port on cell " + LUT4);
			System.exit(1);
		}
		EdifPort lI1Port = LUT4.getPort("I1");
		if (lI1Port == null) {
			System.err.println("Can't find I1 port on cell " + LUT4);
			System.exit(1);
		}
		EdifPort lI2Port = LUT4.getPort("I2");
		if (lI2Port == null) {
			System.err.println("Can't find I2 port on cell " + LUT4);
			System.exit(1);
		}
		EdifPort lI3Port = LUT4.getPort("I3");
		if (lI3Port == null) {
			System.err.println("Can't find I3 port on cell " + LUT4);
			System.exit(1);
		}
		EdifPort lOPort = LUT4.getPort("O");
		if (lOPort == null) {
			System.err.println("Can't find O port on cell " + LUT4);
			System.exit(1);
		}
		EdifSingleBitPort lI0ESBP = lI0Port.getSingleBitPort(0);
		EdifSingleBitPort lI1ESBP = lI1Port.getSingleBitPort(0);
		EdifSingleBitPort lI2ESBP = lI2Port.getSingleBitPort(0);
		EdifSingleBitPort lI3ESBP = lI3Port.getSingleBitPort(0);
		EdifSingleBitPort lOESBP = lOPort.getSingleBitPort(0);
		int levelOneNumber = 0;
		int levelTwoNumber = 0;
		int levelThreeNumber = 0;
		EdifNet lI0Net = null;
		EdifNet lI1Net = null;
		EdifNet lI2Net = null;
		EdifNet lI3Net = null;
		EdifNet lONet = null;
		switch (depth) {
		case 16: levelOneNumber = 0; levelTwoNumber = 2; levelThreeNumber = 16;	break;
		case 32: levelOneNumber = 0; levelTwoNumber = 4; levelThreeNumber = 32; break;
		case 64: levelOneNumber = 0; levelTwoNumber = 8; levelThreeNumber = 64;	break;
		case 128:levelOneNumber = 2; levelTwoNumber = 16;levelThreeNumber = 128;
		}
		// Hook up different levels of LUT4
		for (int i = 0; i < levelOneNumber; i++) {	// level 1
			// connect I2 and I3 to GND
			EdifPortRef lI2EPR = new EdifPortRef(gNet, lI2ESBP, lut4Instances[i]);
			EdifPortRef lI3EPR = new EdifPortRef(gNet, lI3ESBP, lut4Instances[i]);
			gNet.addPortConnection(lI2EPR);
			gNet.addPortConnection(lI3EPR);
			// connect I1 to we
			EdifPortRef lI1EPR = new EdifPortRef(we, lI1ESBP, lut4Instances[i]);
			we.addPortConnection(lI1EPR);
			// connect I0 to a6
			EdifPortRef lI0EPR = new EdifPortRef(a6, lI0ESBP, lut4Instances[i]);
			a6.addPortConnection(lI0EPR);
			// connect O to the next level
			EdifPortRef lOEPR = new EdifPortRef(decoderConnectionNets[i], lOESBP, lut4Instances[i]);
			decoderConnectionNets[i].addPortConnection(lOEPR);
		}
		for (int i = levelOneNumber; i < levelOneNumber + levelTwoNumber; i++) {	// level 2
			switch (depth) {
			case 16: {
				lI0Net = a3;
				lI1Net = we;
				lI2Net = gNet;
				lI3Net = gNet;
				lONet = decoderConnectionNets[i];
				break;
			}
			case 32: {
				lI0Net = a3;
				lI1Net = a4;
				lI2Net = we;
				lI3Net = gNet;
				lONet = decoderConnectionNets[i];
				break;
			}
			case 64: {
				lI0Net = a3;
				lI1Net = a4;
				lI2Net = a5;
				lI3Net = we;
				lONet = decoderConnectionNets[i];
				break;
			}
			case 128: {
				lI0Net = decoderConnectionNets[(i-levelOneNumber)/(levelTwoNumber/2)];
				lI1Net = a3;
				lI2Net = a4;
				lI3Net = a5;
				lONet = decoderConnectionNets[i];
			}
			}
			EdifPortRef lI0EPR = new EdifPortRef(lI0Net, lI0ESBP, lut4Instances[i]);
			EdifPortRef lI1EPR = new EdifPortRef(lI1Net, lI1ESBP, lut4Instances[i]);
			EdifPortRef lI2EPR = new EdifPortRef(lI2Net, lI2ESBP, lut4Instances[i]);
			EdifPortRef lI3EPR = new EdifPortRef(lI3Net, lI3ESBP, lut4Instances[i]);
			EdifPortRef lOEPR = new EdifPortRef(lONet, lOESBP, lut4Instances[i]);
			lI0Net.addPortConnection(lI0EPR);
			lI1Net.addPortConnection(lI1EPR);
			lI2Net.addPortConnection(lI2EPR);
			lI3Net.addPortConnection(lI3EPR);
			lONet.addPortConnection(lOEPR);
		}
		for (int i = levelOneNumber + levelTwoNumber; i < lut4InstanceNumber; i++) {	// level 3
			lI0Net = decoderConnectionNets[(i - levelOneNumber - levelTwoNumber)/8 + levelOneNumber];
			lI1Net = a0;
			lI2Net = a1;
			lI3Net = a2;
			lONet = eNets[i - levelOneNumber - levelTwoNumber];
			EdifPortRef lI0EPR = new EdifPortRef(lI0Net, lI0ESBP, lut4Instances[i]);
			EdifPortRef lI1EPR = new EdifPortRef(lI1Net, lI1ESBP, lut4Instances[i]);
			EdifPortRef lI2EPR = new EdifPortRef(lI2Net, lI2ESBP, lut4Instances[i]);
			EdifPortRef lI3EPR = new EdifPortRef(lI3Net, lI3ESBP, lut4Instances[i]);
			EdifPortRef lOEPR = new EdifPortRef(lONet, lOESBP, lut4Instances[i]);
			lI0Net.addPortConnection(lI0EPR);
			lI1Net.addPortConnection(lI1EPR);
			lI2Net.addPortConnection(lI2EPR);
			lI3Net.addPortConnection(lI3EPR);
			lONet.addPortConnection(lOEPR);
		}
			
		/****** Step 18. Set INIT string  ******/
		boolean isInit;
		StringTypedValue valueZero = new StringTypedValue("0");
        StringTypedValue valueOne = new StringTypedValue("1");
        StringTypedValue value0002 = new StringTypedValue("0002");
        StringTypedValue value0008 = new StringTypedValue("0008");
        StringTypedValue value0020 = new StringTypedValue("0020");
        StringTypedValue value0080 = new StringTypedValue("0080");
        StringTypedValue value0200 = new StringTypedValue("0200");
        StringTypedValue value0800 = new StringTypedValue("0800");
        StringTypedValue value2000 = new StringTypedValue("2000");
        StringTypedValue value8000 = new StringTypedValue("8000");
        StringTypedValue value0004 = new StringTypedValue("0004");
        StringTypedValue value0010 = new StringTypedValue("0010");
        StringTypedValue value0040 = new StringTypedValue("0040");
        StringTypedValue value0100 = new StringTypedValue("0100");
        StringTypedValue value0400 = new StringTypedValue("0400");
        StringTypedValue value1000 = new StringTypedValue("1000");
        StringTypedValue value4000 = new StringTypedValue("4000");
        
		// Set INIT Property for FDEs
        long[] INITValue = new long[8];
        int[] initCount = new int[8];
        INITValue[0] = INIT_00;
        INITValue[1] = INIT_01;
        INITValue[2] = INIT_02;
        INITValue[3] = INIT_03;
        INITValue[4] = INIT_04;
        INITValue[5] = INIT_05;
        INITValue[6] = INIT_06;
        INITValue[7] = INIT_07;
        initCount[0] = 0;
        initCount[1] = 0;
        initCount[2] = 0;
        initCount[3] = 0;
        initCount[4] = 0;
        initCount[5] = 0;
        initCount[6] = 0;
        initCount[7] = 0;
        int initCount_high = 0;
        int initCount_low = 0;
        PropertyList[] FDE_propertylist = new PropertyList[ffInstanceNumber];
        if (depth == 128) {			// when depth = 128, INIT value needs to be divided into two parts
        	for(int i=0; i<ffInstanceNumber; i++)
	        {
	        	isInit = false;
	        	FDE_propertylist[i] = ffInstances[i].getPropertyList();      	
	        	if(FDE_propertylist[i] != null)
	        	{
	        		for(Property FDE_property : FDE_propertylist[i].values())
	        		{
	        			if(FDE_property.getName().equals("INIT"))
	        			{
	        				isInit = true;
	        				if(i<64) {
	        					if (((INIT_LOW >> initCount_low++) & 1) == 1)
	        						FDE_property.setValue(valueOne);
	        					else
	        						FDE_property.setValue(valueZero);
	        				}
	        				else {
	        					if (((INIT_HIGH >> initCount_high++) & 1) == 1)
	        						FDE_property.setValue(valueOne);
	        					else
	        						FDE_property.setValue(valueZero);
	        				}
	        			}
	        		}
	        	}
	        	if(!isInit)
	        	{
	        		if(i<64) {
	        			if (((INIT_LOW >> initCount_low++) & 1) == 1)
	        				ffInstances[i].addProperty(new Property("INIT", (EdifTypedValue)valueOne));
	        			else
	        				ffInstances[i].addProperty(new Property("INIT", (EdifTypedValue)valueZero));
	        		}
	        		else {
	        			if (((INIT_HIGH >> initCount_high++) & 1) == 1)
	        				ffInstances[i].addProperty(new Property("INIT", (EdifTypedValue)valueOne));
	        			else
	        				ffInstances[i].addProperty(new Property("INIT", (EdifTypedValue)valueZero));
	        		}
	        	}
	        }
        } else if (width == 1) {		// when depth != 128 and width = 1, apply INIT
        	for (int i = 0; i < ffInstanceNumber; i++) {
        		isInit = false;
        		FDE_propertylist[i] = ffInstances[i].getPropertyList();
        		if(FDE_propertylist[i] != null)	{		// The property "INIT" already exists
        			for(Property FDE_property : FDE_propertylist[i].values()) {
        				if(FDE_property.getName().equals("INIT")) {
        					isInit = true;
        					if (((INIT >> initCount[i/depth]++) & 1) == 1)
        						FDE_property.setValue(valueOne);
        					else
        						FDE_property.setValue(valueZero);
        				}
        			}
        		}
        		if(!isInit)	{							// The property "INIT" does not exist
        			if (((INIT >> initCount[i/depth]++) & 1) == 1)
        				ffInstances[i].addProperty(new Property("INIT", (EdifTypedValue)valueOne));
        			else
        				ffInstances[i].addProperty(new Property("INIT", (EdifTypedValue)valueZero));
        		}
        	}
        } else {						// when width > 1, apply the INIT value array
        	for (int i = 0; i < ffInstanceNumber; i++) {
        		isInit = false;
        		FDE_propertylist[i] = ffInstances[i].getPropertyList();
        		if(FDE_propertylist[i] != null)	{		// The property "INIT" already exists
        			for(Property FDE_property : FDE_propertylist[i].values()) {
        				if(FDE_property.getName().equals("INIT")) {
        					isInit = true;
        					if (((INITValue[i/depth] >> initCount[i/depth]++) & 1) == 1)
        						FDE_property.setValue(valueOne);
        					else
        						FDE_property.setValue(valueZero);
        				}
        			}
        		}
        		if(!isInit)	{							// The property "INIT" does not exist
        			if (((INITValue[i/depth] >> initCount[i/depth]++) & 1) == 1)
        				ffInstances[i].addProperty(new Property("INIT", (EdifTypedValue)valueOne));
        			else
        				ffInstances[i].addProperty(new Property("INIT", (EdifTypedValue)valueZero));
        		}
        	}
        }
        
        // Set INIT Property for LUT4s
        PropertyList[] LUT4_propertylist = new PropertyList[lut4InstanceNumber];
        // level 1
        for (int i = 0; i < levelOneNumber; i++) {
        	isInit = false;
        	LUT4_propertylist[i] = lut4Instances[i].getPropertyList();
        	if(LUT4_propertylist[i] != null) {		// The property "INIT" already exists
        		for (Property LUT4_property : LUT4_propertylist[i].values()) {
        			if (LUT4_property.getName().equals("INIT")) {
        				isInit = true;
        				if (i==0)
        					LUT4_property.setValue(value0004);
        				else
        					LUT4_property.setValue(value0008);
        			}
        		}
        	}
        	if(!isInit) {							// The property "INIT" does not exist
        		if (i==0)
        			lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0004));
        		else
        			lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0008));
        	}
        }
        // level 2
        for (int i = levelOneNumber; i < levelOneNumber + levelTwoNumber; i++) {
        	isInit = false;
        	LUT4_propertylist[i] = lut4Instances[i].getPropertyList();
        	if(LUT4_propertylist[i] != null) {		// The property "INIT" already exists
        		for (Property LUT4_property : LUT4_propertylist[i].values()) {
        			if (LUT4_property.getName().equals("INIT")) {
        				isInit = true;
        				switch (depth) {
        				case 16: {
        					switch (i-levelOneNumber) {
        					case 0: LUT4_property.setValue(value0004); break;
        					case 1: LUT4_property.setValue(value0008);
        					}
        					break;
        				}
        				case 32: {
        					switch (i-levelOneNumber) {
        					case 0: LUT4_property.setValue(value0010); break;
        					case 1: LUT4_property.setValue(value0020); break;
        					case 2: LUT4_property.setValue(value0040); break;
        					case 3: LUT4_property.setValue(value0080);
        					}
        					break;
        				}
        				case 64: {
        					switch (i-levelOneNumber) {
        					case 0: LUT4_property.setValue(value0100); break;
        					case 1: LUT4_property.setValue(value0200); break;
        					case 2: LUT4_property.setValue(value0400); break;
        					case 3: LUT4_property.setValue(value0800); break;
        					case 4: LUT4_property.setValue(value1000); break;
        					case 5: LUT4_property.setValue(value2000); break;
        					case 6: LUT4_property.setValue(value4000); break;
        					case 7: LUT4_property.setValue(value8000);
        					}
        					break;
        				}
        				case 128: {
        					switch ((i-levelOneNumber)%(levelTwoNumber/2)) {
        					case 0: LUT4_property.setValue(value0002); break;
            				case 1: LUT4_property.setValue(value0008); break;
            				case 2: LUT4_property.setValue(value0020); break;
            				case 3: LUT4_property.setValue(value0080); break;
            				case 4: LUT4_property.setValue(value0200); break;
            				case 5: LUT4_property.setValue(value0800); break;
            				case 6: LUT4_property.setValue(value2000); break;
            				case 7: LUT4_property.setValue(value8000);
        					}
        				}
        				}
        			}
        		}
        	}
        	if(!isInit) {							// The property "INIT" does not exist
        		switch(depth) {
        		case 16: {
        			switch (i-levelOneNumber) {
        			case 0: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0004)); break;
        			case 1: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0008));
        			}
        			break;
        		}
        		case 32: {
        			switch (i-levelOneNumber) {
        			case 0: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0010)); break;
        			case 1: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0020)); break;
        			case 2: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0040)); break;
        			case 3: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0080));
        			}
        			break;
        		}
        		case 64: {
        			switch (i-levelOneNumber) {
        			case 0: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0100)); break;
        			case 1: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0200)); break;
        			case 2: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0400)); break;
        			case 3: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0800)); break;
        			case 4: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value1000)); break;
        			case 5: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value2000)); break;
        			case 6: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value4000)); break;
        			case 7: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value8000));
        			}
        			break;
        		}
        		case 128: {
        			switch ((i-levelOneNumber)%(levelTwoNumber/2)) {
        			case 0: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0002)); break;
            		case 1: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0008)); break;
            		case 2: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0020)); break;
            		case 3: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0080)); break;
            		case 4: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0200)); break;
            		case 5: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0800)); break;
            		case 6: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value2000)); break;
            		case 7: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value8000));
        			}
        		}
        		}	
        	}
        }
        // level 3
        for (int i = levelOneNumber + levelTwoNumber; i < lut4InstanceNumber; i++) {
        	isInit = false;
        	LUT4_propertylist[i] = lut4Instances[i].getPropertyList();
        	if(LUT4_propertylist[i] != null) {		// The property "INIT" already exists
        		for (Property LUT4_property : LUT4_propertylist[i].values()) {
        			if (LUT4_property.getName().equals("INIT")) {
        				isInit = true;
        				switch ((i-levelOneNumber-levelTwoNumber)%8) {
        				case 0: LUT4_property.setValue(value0002); break;
        				case 1: LUT4_property.setValue(value0008); break;
        				case 2: LUT4_property.setValue(value0020); break;
        				case 3: LUT4_property.setValue(value0080); break;
        				case 4: LUT4_property.setValue(value0200); break;
        				case 5: LUT4_property.setValue(value0800); break;
        				case 6: LUT4_property.setValue(value2000); break;
        				case 7: LUT4_property.setValue(value8000);
        				}
        			}
        		}
        	}
        	if(!isInit) {							// The property "INIT" does not exist
        		switch ((i-levelOneNumber-levelTwoNumber)%8) {
        		case 0: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0002)); break;
        		case 1: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0008)); break;
        		case 2: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0020)); break;
        		case 3: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0080)); break;
        		case 4: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0200)); break;
        		case 5: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value0800)); break;
        		case 6: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value2000)); break;
        		case 7: lut4Instances[i].addProperty(new Property("INIT", (EdifTypedValue)value8000));
        		}
        	}
        }
        // End of method Replacement
	}
}
