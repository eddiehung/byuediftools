package edu.byu.ece.edif.tools.sterilize.halflatch;

import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import edu.byu.ece.edif.arch.xilinx.XilinxGenLib;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifCellInterface;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.sterilize.lutreplace.EdifEnvironmentCopyReplace;
import edu.byu.ece.edif.tools.sterilize.lutreplace.ReplacementContext;
import edu.byu.ece.edif.core.EdifPrintWriter;
import java.io.*;
import edu.byu.ece.edif.tools.sterilize.halflatch.HalfLatchRemove;

public class HalfLatchRemoval {
	public static EdifEnvironment removeHalfLatches(EdifEnvironment env, boolean lut) {
		System.out.println("Removing half latches . . .");
		
		// Create a list of instances to replace
		ArrayList <EdifCell> cellsToReplace = new ArrayList<EdifCell>();
		EdifLibrary primitiveLibrary = XilinxGenLib.library;
		String[] cellNamesToReplace = {
				HalfLatchRemove.FD_STRING,
				HalfLatchRemove.FD_1_STRING,
				HalfLatchRemove.FDC_STRING,
				HalfLatchRemove.FDC_1_STRING,
				HalfLatchRemove.FDCE_STRING,
				HalfLatchRemove.FDCE_1_STRING,
				HalfLatchRemove.FDCP_STRING,
				HalfLatchRemove.FDCP_1_STRING,
				HalfLatchRemove.FDE_STRING,
				HalfLatchRemove.FDE_1_STRING,
				HalfLatchRemove.FDP_STRING,
				HalfLatchRemove.FDP_1_STRING,
				HalfLatchRemove.FDPE_STRING,
				HalfLatchRemove.FDPE_1_STRING,
				/*"FD", "FD_1", "FDC", "FDC_1", "FDCE", "FDCE_1", "FDCP", "FDCP_1", "FDE", "FDE_1", 
				"FDP", "FDP_1", "FDPE", "FDPE_1"*/
		};
		for(String s: cellNamesToReplace) {
			cellsToReplace.add(primitiveLibrary.getCell(s));
		}

		// Create an environment copy used for replacement
		EdifEnvironmentCopyReplace ecr = null;
		try {
			ecr = new EdifEnvironmentCopyReplace(env, cellsToReplace);
		} catch (EdifNameConflictException e) {
			System.err.println(e);
			System.exit(1);
		}
		
		// Get new environment and library manager
		EdifEnvironment newEnv = ecr.getNewEnvironment();
		EdifLibraryManager newLibManager = newEnv.getLibraryManager();
				
		// Replace each problem primitive FF
		System.out.println("****************************************");
		for (ReplacementContext context : ecr.getReplacementContexts()) {
			// Print out the names of all the instances that need to be replaced
			System.out.println("Need to replace instance " + 
					context.getOldInstanceToReplace().getParent().getName() + "." + 
					context.getOldInstanceToReplace().getName() + " (" +
					context.getOldCellToReplace().getName() + ")");

			// Get information needed for replacement: parent cell, INIT value,
			// old cell type, and dangling nets that need to be wired up
			EdifCell oldCell = context.getOldCellToReplace();
			EdifCell newParent = context.getNewParentCell();
			EdifCellInstance oldInstance = context.getOldInstanceToReplace();
			String INIT = oldInstance.getProperty("INIT").getValue().toString();
			EdifNet C = context.getNewNetToConnect("C");
			EdifNet D = context.getNewNetToConnect("D");
			EdifNet Q = context.getNewNetToConnect("Q");
			EdifNet PRE = context.getNewNetToConnect("PRE");
			EdifNet CE = context.getNewNetToConnect("CE");
			EdifNet CLR = context.getNewNetToConnect("CLR");
			
			//Replace the current FF with an FDCPE primitive
			edu.byu.ece.edif.tools.sterilize.halflatch.HalfLatchRemove.Remove(newLibManager, oldCell.getName(), newParent, 
					lut, oldInstance.getName(), INIT, C, D, Q, PRE, CE, CLR);

		}
		System.out.println("****************************************");
		
		return newEnv;
	}
	
	public static void main(String[] args) {
		// The String printed when there is a problem with the argument string.
		String usageString = "Usage: java HalfLatchReplacer <top file> [-L <search directory>]* [-f <filename>]* [-o <outputfilename>] [-c <constant value mode>]";
		String lutOption = "Constant value mode options: lut, nolut";
		if (args.length < 1) {
			LogFile.out().println(usageString);
			System.exit(1);
		}
		
		// Process the command line arguments
		// Argument specifying whether or not the constant values are provided by LUT
		boolean lut = true;
		for(int i = 0; i < args.length; i++) {
			if(args[i].equals("-c")) {
				i++;
				if(args[i].equals("lut"))
					lut = true;
				else if(args[i].equals("nolut"))
					lut = false;
				else {
					LogFile.out().println(lutOption);
					System.exit(1);
				}
				break;
			}
		}
		
		// Extract EdifEnvironment from input EDIF file, and replace all the half latches.
		EdifEnvironment top = edu.byu.ece.edif.util.merge.EdifMergeParser.getMergedEdifEnvironment(args[0], args);
		EdifEnvironment newEnv = removeHalfLatches(top, lut);
		
		// Write to EDIF File
		String inputFileName = args[0];
		String outputFileName;
		int pos = inputFileName.indexOf('.');
		outputFileName = inputFileName.substring(0, pos) + "_noHalfLatch.edf";
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
		System.out.println("Half Latch Removal done");

		// DONE!
	}
}