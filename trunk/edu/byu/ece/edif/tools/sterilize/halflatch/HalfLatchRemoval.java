package edu.byu.ece.edif.tools.sterilize.halflatch;

import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import edu.byu.ece.edif.arch.xilinx.XilinxGenLib;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
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
import edu.byu.ece.edif.tools.sterilize.halflatch.HalfLatchRemove;

/**
 * Removes half latches from an EDIF file. Replaces all types of FFs with FDCPEs 
 * and connect the unused ports (PRE, CLR, CE) to constant values (0 or 1). The
 * constant values are provided in two ways, depending on the user's choice:
 * 1) A LUT is added into the EDIF file and provides the constant values;
 * 2) New input ports are added on the top-level cell's interface, and the constant
 * 	  values are provided by external inputs.
 * 
 * This class provides a command-line executable interface (it has a main
 * method). The user specifies the EDIF file, following the conventions of the
 * JEdif tools. An argument [-c <constant value mode>] is used to specify the 
 * constant value mode. There are two optional modes: "lut" and "nolut". "lut" 
 * is set as default. The resulting EDIF file carries the same filename as the
 * original, with "_noHalfLatch" appended. The resulting top-level EdifCell has the
 * same name as the original EdifCell.
 * 
 * For a precise list of the cells replaced by this class, please see the source
 * code.
 * 
 * The half latch removal is assumed to be performed on a flattened cell.
 * 
 * @author Yubo Li
 */
public class HalfLatchRemoval {
	
	/**
	 * This static method removes half latches. It returns a new EdifEnvironment
	 * that is a copy of the old with half latches removed.
	 * 
	 * @param env EdifEnvironment containing half latches.
	 * @param lut a flag indicating which mode is chosen.
	 * @return EdifEnvironment in which all half latches have been removed.
	 */
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
			String INIT;
			if(oldInstance.getProperty("INIT")!=null)	// get INIT value if it exists
				INIT = oldInstance.getProperty("INIT").getValue().toString();
			else
				INIT = "0";								// set INIT to be 0 as default
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
	
	/** A simple main class that can be used to perform this half latch removal.
	 *
	 */
	public static void main(String[] args) {
		/***********************************************************************
		 * The String printed when there is a problem with the argument string.
		 **********************************************************************/
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