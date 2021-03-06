/*
 * Removes SRLs from an EDIF file.
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
package edu.byu.ece.edif.tools.sterilize.lutreplace;

import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import edu.byu.ece.edif.tools.sterilize.lutreplace.SRL.SRL_Replacement;
import edu.byu.ece.edif.tools.sterilize.lutreplace.RAM.RAM_Replacement;
import edu.byu.ece.edif.arch.xilinx.XilinxGenLib;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.core.EdifPrintWriter;
import java.io.*;

/**
 * Removes SRLs and RAMs from an EDIF file. This is used to replace cells in 
 * a netlist that are known to have problems when scrubbing is employed.
 * This class will perform all LUT replacement to generate a safe design.
 * <p>
 * This class relies heavily on 
 * edu.byu.ece.edif.tools.sterilize.lutreplace.SRL.SRL_Replacement
 * and
 * edu.byu.ece.edif.tools.sterilize.lutreplace.RAM.RAM_Replacement
 * <p>
 * This class provides a command-line executable interface (it has a main
 * method). The user specifies the EDIF file, following the conventions of the
 * JEdif tools. The resulting EDIF file carries the same filename as the
 * original, with "_noSRL" appended. The resulting top-level EdifCell has the
 * same name as the original EdifCell.
 * <p>
 * For a precise list of the cells replaced by this class, please see the source
 * code. Examples include RAM16X1D, RAM16X1D_1, SRLC16, SRLC16E_1.
 * <p>
 * @author yuboli
 */
public class LUTReplacer{

	/**
	 * This static method used to perform LUT replacement. It returns a new EdifEnvironment
	 * that is a copy of the old with LUTs replaced.
	 * 
	 * @param env EdifEnvironment containing SRLs or RAMs 
	 * @param out output PrintStream object 
	 * @return EdifEnvironment in which all SRLs have been replaced with LUTs
	 */
	public static EdifEnvironment replaceLUTs(EdifEnvironment env, PrintStream log, PrintStream out) {
		ArrayList<EdifCell> cellsToReplace = new ArrayList<EdifCell>();
		EdifLibrary primitiveLibrary = XilinxGenLib.library;

		// List of cells that need to be replaced.
		String[] cellNamesToReplace = {
				RAM_Replacement.RAM16X1D_STRING,
				RAM_Replacement.RAM16X1D_1_STRING,
				RAM_Replacement.RAM16X1S_STRING,
				RAM_Replacement.RAM16X1S_1_STRING,
				RAM_Replacement.RAM16X2S_STRING,
				RAM_Replacement.RAM16X4S_STRING,
				RAM_Replacement.RAM16X8S_STRING,
				RAM_Replacement.RAM32X1D_STRING,
				RAM_Replacement.RAM32X1D_1_STRING,
				RAM_Replacement.RAM32X1S_STRING,
				RAM_Replacement.RAM32X1S_1_STRING,
				RAM_Replacement.RAM32X2S_STRING,
				RAM_Replacement.RAM32X4S_STRING,
				RAM_Replacement.RAM32X8S_STRING,
				RAM_Replacement.RAM64X1D_STRING,
				RAM_Replacement.RAM64X1D_1_STRING,
				RAM_Replacement.RAM64X1S_STRING,
				RAM_Replacement.RAM64X1S_1_STRING,
				RAM_Replacement.RAM64X2S_STRING,
				RAM_Replacement.RAM128X1S_STRING,
				RAM_Replacement.RAM128X1S_1_STRING,
				/*
				"RAM16X1D", "RAM16X1D_1", "RAM16X1S", "RAM16X1S_1", "RAM16X2S", "RAM16X4S", "RAM16X8S", 
				"RAM32X1D", "RAM32X1D_1", "RAM32X1S", "RAM32X1S_1", "RAM32X2S", "RAM32X4S", "RAM32X8S",	"RAM64X1D", "RAM64X1D_1", 
				"RAM64X1S", "RAM64X1S_1", "RAM64X2S", "RAM128X1S", "RAM128X1S_1",
				*/ 
				SRL_Replacement.SRL16_STRING,
				SRL_Replacement.SRL16_1_STRING,
				SRL_Replacement.SRL16E_STRING,
				SRL_Replacement.SRL16E_1_STRING,
				SRL_Replacement.SRLC16_STRING,
				SRL_Replacement.SRLC16_1_STRING,
				SRL_Replacement.SRLC16E_STRING,
				SRL_Replacement.SRLC16E_1_STRING,
				//"SRL16", "SRL16_1", "SRL16E", "SRL16E_1", "SRLC16", "SRLC16_1",	"SRLC16E", "SRLC16E_1" 
				};

		// Create an array of the cells to replace.
		for ( String s : cellNamesToReplace ) {
			cellsToReplace.add(primitiveLibrary.getCell(s));        	
		}

		// Create copy replace object
		AbstractEdifEnvironmentCopyReplace ecr = null;
		try {
			ecr = new BasicEdifEnvironmentCopyReplace(env, cellsToReplace);
		} catch (EdifNameConflictException e) {
			System.err.println(e);
			System.exit(1);
		}

		// Get new environment and new library manager
		EdifEnvironment newEnv = ecr.getNewEnvironment();
		EdifLibraryManager newLibManager = newEnv.getLibraryManager();

		int numReplaced = 0;
		// Iterate over all of the things that need replacement
		for (ReplacementContext context : ecr.getReplacementContexts()) {
		    log.println("****************************************");
			log.println("Need to replace instance " + 
					context.getOldInstanceToReplace().getParent().getName() + "." + 
					context.getOldInstanceToReplace().getName() +" (" +
					context.getOldCellToReplace().getName()+")");
			log.println("****************************************");

			// Get original cell that we're replacing to know which code to call
			EdifCell oldCell = context.getOldCellToReplace();
			log.println("Old Cell: " + oldCell.getName());

			// Get info needed to replace cell: parent cell , library manager,
			// dangling nets that need to be wired up, and INIT value.
			EdifCell newParent = context.getNewParentCell();
			EdifCellInstance oldInstance = context.getOldInstanceToReplace();
			
			// 1. This next section of code handles the INIT values. The first step is to parse
			// the init strings and store them as long variables.
			
			
			// This is the LONG representation of the init string
			long INIT = 0;
			long INIT_00 = 0;
			long INIT_01 = 0;
			long INIT_02 = 0;
			long INIT_03 = 0;
			long INIT_04 = 0;
			long INIT_05 = 0;
			long INIT_06 = 0;
			long INIT_07 = 0;
			long INIT_HIGH = 0;
			long INIT_LOW = 0;
			if(oldInstance.getProperty("INIT")!=null){
				// Handle the INIT value two ways. The first way is for 128 bits and the second is for 
				// 64 bits or less
				if(oldCell.getName().equals("RAM128X1S") || oldCell.getName().equals("RAM128X1S_1")){
					// Break into two parts
					// long type has only 64 bits, so for RAM128X1S and RAM128X1S_1, the INIT value needs to be divided into two parts
					INIT_HIGH = Long.parseLong(oldInstance.getProperty("INIT").getValue().toString().substring(1, 16), 16);
					INIT_LOW = Long.parseLong(oldInstance.getProperty("INIT").getValue().toString().substring(17, 32), 16);
				}
				else
					INIT = Long.parseLong(oldInstance.getProperty("INIT").getValue().toString(), 16);
			}
			if(oldInstance.getProperty("INIT_00")!=null)
				INIT_00 = Long.parseLong(oldInstance.getProperty("INIT_00").getValue().toString(), 16);
			if(oldInstance.getProperty("INIT_01")!=null)
				INIT_01 = Long.parseLong(oldInstance.getProperty("INIT_01").getValue().toString(), 16);
			if(oldInstance.getProperty("INIT_02")!=null)
				INIT_02 = Long.parseLong(oldInstance.getProperty("INIT_02").getValue().toString(), 16);
			if(oldInstance.getProperty("INIT_03")!=null)
				INIT_03 = Long.parseLong(oldInstance.getProperty("INIT_03").getValue().toString(), 16);
			if(oldInstance.getProperty("INIT_04")!=null)
				INIT_04 = Long.parseLong(oldInstance.getProperty("INIT_04").getValue().toString(), 16);
			if(oldInstance.getProperty("INIT_05")!=null)
				INIT_05 = Long.parseLong(oldInstance.getProperty("INIT_05").getValue().toString(), 16);
			if(oldInstance.getProperty("INIT_06")!=null)
				INIT_06 = Long.parseLong(oldInstance.getProperty("INIT_06").getValue().toString(), 16);
			if(oldInstance.getProperty("INIT_07")!=null)
				INIT_07 = Long.parseLong(oldInstance.getProperty("INIT_07").getValue().toString(), 16);
			
			
			// 2. This next section will get all of the nets needed to hook up the primitive.
			//    We try to get all of the nets even though we konw that only a subset of these nets 
			//    will actually be used. These nets represent the superset of all nets that could be
			//    used in all of the primitives that we are replacing.
			EdifNet D = context.getNewNetToConnect("D");
			EdifNet CE = context.getNewNetToConnect("CE");
			EdifNet CLK = context.getNewNetToConnect("CLK");
			EdifNet A0 = context.getNewNetToConnect("A0");
			EdifNet A1 = context.getNewNetToConnect("A1");
			EdifNet A2 = context.getNewNetToConnect("A2");
			EdifNet A3 = context.getNewNetToConnect("A3");
			EdifNet A4 = context.getNewNetToConnect("A4");
			EdifNet A5 = context.getNewNetToConnect("A5");
			EdifNet A6 = context.getNewNetToConnect("A6");
			EdifNet Q = context.getNewNetToConnect("Q");
			EdifNet Q15 = context.getNewNetToConnect("Q15");
			EdifNet WE = context.getNewNetToConnect("WE");
			EdifNet D0 = context.getNewNetToConnect("D0");
			EdifNet D1 = context.getNewNetToConnect("D1");
			EdifNet D2 = context.getNewNetToConnect("D2");
			EdifNet D3 = context.getNewNetToConnect("D3");
			EdifNet D_0 = context.getNewNetToConnect("D", 0);
			EdifNet D_1 = context.getNewNetToConnect("D", 1);
			EdifNet D_2 = context.getNewNetToConnect("D", 2);
			EdifNet D_3 = context.getNewNetToConnect("D", 3);
			EdifNet D_4 = context.getNewNetToConnect("D", 4);
			EdifNet D_5 = context.getNewNetToConnect("D", 5);
			EdifNet D_6 = context.getNewNetToConnect("D", 6);
			EdifNet D_7 = context.getNewNetToConnect("D", 7);
			EdifNet WCLK = context.getNewNetToConnect("WCLK");
			EdifNet DPRA0 = context.getNewNetToConnect("DPRA0");
			EdifNet DPRA1 = context.getNewNetToConnect("DPRA1");
			EdifNet DPRA2 = context.getNewNetToConnect("DPRA2");
			EdifNet DPRA3 = context.getNewNetToConnect("DPRA3");
			EdifNet DPRA4 = context.getNewNetToConnect("DPRA4");
			EdifNet DPRA5 = context.getNewNetToConnect("DPRA5");
			EdifNet SPO = context.getNewNetToConnect("SPO");
			EdifNet SPO_0 = context.getNewNetToConnect("SPO", 0);
			EdifNet SPO_1 = context.getNewNetToConnect("SPO", 1);
			EdifNet SPO_2 = context.getNewNetToConnect("SPO", 2);
			EdifNet SPO_3 = context.getNewNetToConnect("SPO", 3);
			EdifNet SPO_4 = context.getNewNetToConnect("SPO", 4);
			EdifNet SPO_5 = context.getNewNetToConnect("SPO", 5);
			EdifNet SPO_6 = context.getNewNetToConnect("SPO", 6);
			EdifNet SPO_7 = context.getNewNetToConnect("SPO", 7);
			EdifNet SPO0 = context.getNewNetToConnect("SPO0");
			EdifNet SPO1 = context.getNewNetToConnect("SPO1");
			EdifNet SPO2 = context.getNewNetToConnect("SPO2");
			EdifNet SPO3 = context.getNewNetToConnect("SPO3");
			EdifNet DPO = context.getNewNetToConnect("DPO");
			EdifNet DPO_0 = context.getNewNetToConnect("DPO", 0);
			EdifNet DPO_1 = context.getNewNetToConnect("DPO", 1);
			EdifNet DPO_2 = context.getNewNetToConnect("DPO", 2);
			EdifNet DPO_3 = context.getNewNetToConnect("DPO", 3);
			EdifNet DPO_4 = context.getNewNetToConnect("DPO", 4);
			EdifNet DPO_5 = context.getNewNetToConnect("DPO", 5);
			EdifNet DPO_6 = context.getNewNetToConnect("DPO", 6);
			EdifNet DPO_7 = context.getNewNetToConnect("DPO", 7);
			EdifNet DPO0 = context.getNewNetToConnect("DPO0");
			EdifNet DPO1 = context.getNewNetToConnect("DPO1");
			EdifNet DPO2 = context.getNewNetToConnect("DPO2");
			EdifNet DPO3 = context.getNewNetToConnect("DPO3");
			EdifNet O = context.getNewNetToConnect("O");
			EdifNet O_0 = context.getNewNetToConnect("O", 0);
			EdifNet O_1 = context.getNewNetToConnect("O", 1);
			EdifNet O_2 = context.getNewNetToConnect("O", 2);
			EdifNet O_3 = context.getNewNetToConnect("O", 3);
			EdifNet O_4 = context.getNewNetToConnect("O", 4);
			EdifNet O_5 = context.getNewNetToConnect("O", 5);
			EdifNet O_6 = context.getNewNetToConnect("O", 6);
			EdifNet O_7 = context.getNewNetToConnect("O", 7);
			EdifNet O0 = context.getNewNetToConnect("O0");
			EdifNet O1 = context.getNewNetToConnect("O1");
			EdifNet O2 = context.getNewNetToConnect("O2");
			EdifNet O3 = context.getNewNetToConnect("O3");

			// 3. Call code to create replacement cell internals
			if(oldCell.getName().equals("SRL16") || oldCell.getName().equals("SRL16_1") || 
					oldCell.getName().equals("SRL16E") || oldCell.getName().equals("SRL16E_1") || 
					oldCell.getName().equals("SRLC16") || oldCell.getName().equals("SRLC16_1") || 
					oldCell.getName().equals("SRLC16E") || oldCell.getName().equals("SRLC16E_1")) {
				edu.byu.ece.edif.tools.sterilize.lutreplace.SRL.SRL_Replacement.Replace(newLibManager, oldCell.getName(), newParent,
						oldInstance.getName(), INIT, D, CE, CLK, A0, A1, A2, A3, Q, Q15);
			} else {
				edu.byu.ece.edif.tools.sterilize.lutreplace.RAM.RAM_Replacement.Replace(newLibManager, oldCell.getName(), newParent, 
						oldInstance.getName(), INIT, INIT_00, INIT_01, INIT_02, INIT_03, INIT_04, INIT_05, INIT_06, INIT_07, INIT_HIGH, 
						INIT_LOW, WE, WCLK, D, D0, D1, D2, D3, D_0, D_1, D_2, D_3, D_4, D_5, D_6, D_7, A0, A1, A2, A3, A4, A5, A6, 
						DPRA0, DPRA1, DPRA2, DPRA3, DPRA4, DPRA5, SPO, SPO0, SPO1, SPO2, SPO3, SPO_0, SPO_1, SPO_2, SPO_3, SPO_4, SPO_5, 
						SPO_6, SPO_7, DPO, DPO0, DPO1, DPO2, DPO3, DPO_0, DPO_1, DPO_2, DPO_3, DPO_4, DPO_5, DPO_6, DPO_7, 
						O, O0, O1, O2, O3, O_0, O_1, O_2, O_3, O_4, O_5, O_6, O_7);
			}
			numReplaced++;
		}
		out.println("Replaced " + numReplaced + " instances.");
		return newEnv;
	}

	/** A simple main class that can be used to perform this LUT replacement.
	 *
	 */
	public static void main(String[] args) {
		/***********************************************************************
		 * The String printed when there is a problem with the argument string.
		 **********************************************************************/
		String usageString = "Usage: java LUTreplacer <top file> [-L <search directory>]* [-f <filename>]* [-o <outputfilename>]";

		if (args.length < 1) {
			LogFile.out().println(usageString);
			System.exit(1);
		}

		EdifEnvironment top = edu.byu.ece.edif.util.merge.EdifMergeParser.getMergedEdifEnvironment(args[0], args);
		PrintStream out = null;
		EdifEnvironment newEnv = replaceLUTs(top, out, out);

		// Write to EDIF File
		String inputFileName = args[0];
		String outputFileName;
		int pos = inputFileName.indexOf('.');
		outputFileName = inputFileName.substring(0, pos) + "_noSRL.edf";
		File f = new File(outputFileName);
		System.out.println("Output File: " + outputFileName);
		if (f.exists())
			System.out.println("Note: existing file will be overwritten");

		try{
			FileOutputStream outputStream = new FileOutputStream(outputFileName);
			EdifPrintWriter epw = new EdifPrintWriter(outputStream);
			newEnv.toEdif(epw, true);
			epw.close();
		} catch (FileNotFoundException e){
			System.out.println("FileNotFoundException caught");
		} 
		System.out.println("Replacement done");

		// DONE!
	}
	
	protected int _numReplaced;
}