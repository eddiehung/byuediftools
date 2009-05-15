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
import edu.byu.ece.edif.tools.sterilize.lutreplace.AbstractEdifEnvironmentCopyReplace;
import edu.byu.ece.edif.tools.sterilize.lutreplace.BasicEdifEnvironmentCopyReplace;
import edu.byu.ece.edif.tools.sterilize.lutreplace.ReplacementContext;
import edu.byu.ece.edif.core.EdifPrintWriter;
import java.io.*;

/**
 * This is an incomplete Half Latch replacer. 
 * Attempts to remove half latches in a design without flattening it.
 * Calls HalflatchReplacement.java.
 * 
 * 
 * @author Yubo Li
 *
 */
public class HalfLatchReplacer {
	
	public static EdifEnvironment replaceHalfLatches(EdifEnvironment env) {
		System.out.println("Replacing half latches . . .");
		// Create a list of instances to replace
		ArrayList <EdifCell> cellsToReplace = new ArrayList<EdifCell>();
		EdifLibrary primitiveLibrary = XilinxGenLib.library;
		String[] cellNamesToReplace = {"FD", "FD_1", "FDC", "FDC_1", "FDCE", "FDCE_1", "FDCP", "FDCP_1", "FDE", "FDE_1", 
				"FDP", "FDP_1", "FDPE", "FDPE_1"
		};
		for(String s: cellNamesToReplace) {
			cellsToReplace.add(primitiveLibrary.getCell(s));
		}

		// Create an environment copy used for replacement
		AbstractEdifEnvironmentCopyReplace ecr = null;
		try {
			ecr = new BasicEdifEnvironmentCopyReplace(env, cellsToReplace);
		} catch (EdifNameConflictException e) {
			System.err.println(e);
			System.exit(1);
		}
		
		// Get new environment and library manager
		EdifEnvironment newEnv = ecr.getNewEnvironment();
		EdifLibraryManager newLibManager = newEnv.getLibraryManager();
		
		// topCell is used to indicate the top level cell
		// If there exist certain flip-flops that need to be replaced in the top level cell,
		// then the parent returned from method context.getNewParentCell() is different from 
		// topCell, although they have the same name. So here I let topCell equal to the returned
		// parent cell. If no instance needs to be replaced in the top level cell, then it is
		// OK to set topCell as env.getTopCell().
		EdifCell topCell = env.getTopCell();
		for(ReplacementContext tempContext : ecr.getReplacementContexts()) {
			if(tempContext.getNewParentCell().toString().equals(topCell.toString())) {
				topCell = tempContext.getNewParentCell();
				break;
			}
		}
		
		
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
			edu.byu.ece.edif.tools.sterilize.halflatch.HalfLatchReplacement.Replace(ecr, newLibManager, topCell, newEnv,
					oldCell.getName(), newParent, oldInstance.getName(), INIT, C, D, Q, PRE, CE, CLR);

		}
		System.out.println("****************************************");
		
		return newEnv;
	}
	
	public static void main(String[] args) {
		// The String printed when there is a problem with the argument string.
		String usageString = "Usage: java HalfLatchReplacer <top file> [-L <search directory>]* [-f <filename>]* [-o <outputfilename>]";
		if (args.length < 1) {
			LogFile.out().println(usageString);
			System.exit(1);
		}
		
		// Extract EdifEnvironment from input EDIF file, and replace all the half latches.
		EdifEnvironment top = edu.byu.ece.edif.util.merge.EdifMergeParser.getMergedEdifEnvironment(args[0], args);
		EdifEnvironment newEnv = replaceHalfLatches(top);
		
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
		System.out.println("Replacement done");

		// DONE!
	}
}