/*
 * Removes half latches and/or fmaps from a JEdif file.
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
package edu.byu.ece.edif.jedif;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.NMRUtilities;
import edu.byu.ece.edif.tools.sterilize.fmap.FmapRemover;
import edu.byu.ece.edif.tools.sterilize.halflatch.EdifHalfLatchRemover;
import edu.byu.ece.edif.tools.sterilize.halflatch.HalfLatchArchitecture;
import edu.byu.ece.edif.tools.sterilize.halflatch.HalfLatchFlattenedEdifCell;
import edu.byu.ece.edif.tools.sterilize.halflatch.SequentialEdifHalfLatchRemover;
import edu.byu.ece.edif.tools.sterilize.halflatch.XilinxHalfLatchArchitecture;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.iob.AbstractIOBAnalyzer;
import edu.byu.ece.edif.util.iob.XilinxVirtexIOBAnalyzer;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.JEdifOutputCommandGroup;
import edu.byu.ece.edif.util.jsap.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.MergeParserCommandGroup;
import edu.byu.ece.edif.util.jsap.OutputFileCommandGroup;
import edu.byu.ece.edif.tools.sterilize.lutreplace.LUTReplacer;

public class JEdifSterilize extends EDIFMain {

    /**
     * JEdifSterilize takes a JEdif file and, depending on user options,
     * 1-removes half latches from the file 2-removes F maps from the file
     * 
     * @param args
     */
    public static void main(String[] args) {
        // Define the print streams for this program
        PrintStream out = System.out;
        PrintStream err = System.out;
        // Print executable heading
        EXECUTABLE_NAME = "JEdifSterilize";
        TOOL_SUMMARY_STRING = "Sterilizes a JEdif file by removing half-latches and fmaps.";
        // Parse command line options
        printProgramExecutableString(out);

        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new JEdifSterilizeCommandGroup());
        parser.addCommands(new IOBCommandGroup());
        parser.addCommands(new JEdifParserCommandGroup());
        parser.addCommands(new JEdifOutputCommandGroup());
        parser.addCommands(new LogFileCommandGroup("sterilize.log"));
        JSAPResult result = parser.parse(args, err);

        if (!result.success())
            System.exit(1);

        LogFileCommandGroup.CreateLog(result);
        out = LogFile.out();
        err = LogFile.err();

        printProgramExecutableString(LogFile.log());

        EdifEnvironment myEnv = JEdifParserCommandGroup.getEdifEnvironment(result, out);

        if (myEnv == null) {
            LogFile.err().println("Invalid JEdif file");
            System.exit(1);
        }

        // This function has been moved to JEdifBuild.
        //replace_srls_rlocs(result, logger, myEnv);

        flatten_sterilize(result, myEnv);

        //Serializing to a JEdif
        if (!result.userSpecified(OutputFileCommandGroup.OUTPUT_OPTION)) {
            String name = MergeParserCommandGroup.getInputFileName(result);
            name = name.substring(0, name.lastIndexOf('.'));
            JEdifOutputCommandGroup.serializeObject(out, name + "_clean.jedif", myEnv);
        } else {
            JEdifOutputCommandGroup.serializeObject(out, result, myEnv);
        }

        //JEdifOutputCommandGroup.serializeObject(out,result,	flatCell.getLibrary().getLibraryManager().getEdifEnvironment());

        LogFile.out().println("Finished Sterilization");
        //All Finished!
        //System.out.println("\nThe JEdif file '"+JEdifParserCommandGroup.getInputFileName(result)+"' has been"+
        //		" sterilized into the JEdif file '"+JEdifOutputCommandGroup.getOutputFileName(result)+"'");
    }

    public static void flatten_sterilize(JSAPResult result, EdifEnvironment myEnv) {
        boolean debug = false;
        EdifCell myCell = null;
        FlattenedEdifCell flatCell = null;
        try {
            myCell = myEnv.getTopCell();

            if (myCell instanceof FlattenedEdifCell) {
                flatCell = (FlattenedEdifCell) myCell;
            } else {
                LogFile.out().print("Flattening... ");
                flatCell = new FlattenedEdifCell(myCell, "_flat");
                LogFile.out().println("Done");
                flatCell.getLibrary().getLibraryManager().deleteCell(myCell, true);
            }
        } catch (EdifNameConflictException e1) {
            LogFile.err().println("ERROR: EdifNameConflictException");
            System.exit(1);
        } catch (InvalidEdifNameException e2) {
            LogFile.err().println("ERROR: InvalidEdifNameException");
            System.exit(1);
        }

        EdifCellInstanceGraph eciConnectivityGraph = new EdifCellInstanceGraph(flatCell);
        if (!result.success())
            System.exit(1);
        EdifCell sterilizeCell = sterilize(flatCell, //FlattenedEdifCell, 
                eciConnectivityGraph, //EdifCellInstanceGraph,
                false, //boolean reportTiming,
                debug, //boolean _debug,
                result); //JSAPResult
        // Set as top cell

        myEnv.setTopCell(sterilizeCell);

        //        EdifCellInstance tmrInstance = null;
        //        EdifDesign newDesign = null;
        //        try {
        //            tmrInstance = new EdifCellInstance(sterilizeCell.getName(), null, sterilizeCell);
        //            newDesign = new EdifDesign(sterilizeCell.getEdifNameable());
        //        } catch (InvalidEdifNameException e1) {
        //            e1.toRuntime();
        //        }
        //        newDesign.setTopCellInstance(tmrInstance);
        //        // copy design properties
        //        EdifDesign oldDesign = myEnv.getTopDesign();
        //        if (oldDesign.getPropertyList() != null) {
        //            for (Object o : oldDesign.getPropertyList().values()) {
        //                Property p = (Property) o;
        //                newDesign.addProperty((Property) p.clone());
        //            }
        //        }
        //        myEnv.setTopDesign(newDesign);
    }

    /**
     * sterilize does the work of removing half latches and fmaps.
     * 
     * @param _parser The _parser contains which options the user has selected.
     * @param flatCell is the cell that has been flattened.
     * @param eciConnectivityGraph is the graph.
     * @param reportTiming is an option for turning on timing information.
     * @param _debug is an option for debugging.
     * @param args is args.
     */
    public static FlattenedEdifCell sterilize(FlattenedEdifCell flatCell, EdifCellInstanceGraph eciConnectivityGraph,
            boolean reportTiming, boolean debug, JSAPResult result) {
        /*
         * 5. Remove fmaps.
         */
        FmapRemover.removeFmaps(flatCell.getLibrary().getLibraryManager().getEdifEnvironment());
        
        LUTReplacer.replaceLUTs(flatCell.getLibrary().getLibraryManager().getEdifEnvironment());

        /*
         * 8. Analyze IOBs of the flattened EdifCell TODO: Add IOBAnalyzer
         * objects for other architectures/technologies
         */
        // Pack IOBs or not?
        boolean packInputRegs = false, packOutputRegs = false;
        packInputRegs = IOBCommandGroup.packInputRegisters(result);
        packOutputRegs = IOBCommandGroup.packOutputRegisters(result);

        AbstractIOBAnalyzer iobAnalyzer = new XilinxVirtexIOBAnalyzer(flatCell, eciConnectivityGraph, packInputRegs,
                packOutputRegs);
        // Delete the source-to-source Edges. We don't need them after the
        //   IOB analysis
        // BHP: These were never created. Don't need to remove them
        //eciConnectivityGraph.removeSourceToSourceEdges();
        long startTime;
        startTime = System.currentTimeMillis();
        if (reportTiming)
            startTime = LogFileCommandGroup.reportTime(startTime, "IOB Analysis", LogFile.out());

        /*
         * 8. Remove half-latches, if desired
         */
        /**
         * JSAPResult used to access the command-line arguments after being
         * parsed.
         */

        Collection<String> forceInstanceStrings = new ArrayList<String>();
        if (JEdifSterilizeCommandGroup.getRemoveHL(result)) {
            //long startTime, elapsedTime;
            //startTime = System.currentTimeMillis();
            LogFile.out().println("Removing half-latches...");
            // TODO: Ensure we are using Xilinx (the only supported architecture)
            HalfLatchArchitecture hlArchitecture = null;
            if (packInputRegs || packOutputRegs) {
                // Send the (possibly large) list to the log file only
                LogFile.log().println(
                        "\tThe following flip-flops were treated as IOB " + "registers during half-latch removal: "
                                + iobAnalyzer.getAllIOBRegisters());
                hlArchitecture = new XilinxHalfLatchArchitecture(flatCell, iobAnalyzer);
            } else {
                hlArchitecture = new XilinxHalfLatchArchitecture(flatCell);
            }

            String hlPortName = JEdifSterilizeCommandGroup.getHLPortName(result);
            boolean hlUsePort = false;
            if (hlPortName != null)
                hlUsePort = true;

            // Set up a SequentialEdifHalfLatchRemover (BHP: I'm not sure the other
            //   alternative--Topological...--works anymore)
            EdifHalfLatchRemover edifHalfLatchRemover = new SequentialEdifHalfLatchRemover(hlArchitecture,
                    JEdifSterilizeCommandGroup.getHLConstant(result), hlUsePort, hlPortName);
            // Remove Half-latches
            HalfLatchFlattenedEdifCell hlflatCell = edifHalfLatchRemover.removeHalfLatches(flatCell);

            // Force triplication of internal half-latch constant or port ibuf
            EdifCellInstance safeConstantInstance;
            if (hlUsePort)
                safeConstantInstance = hlflatCell.getSafeConstantPortBufferInstance();
            else
                safeConstantInstance = hlflatCell.getSafeConstantGeneratorCell();

            // Set up this instance for triplication.
            // Add only the instance name because this instance is at the top
            // level and does not need any hierarchy information.
            forceInstanceStrings.add(safeConstantInstance.getName());

            hlflatCell.getLibrary().getLibraryManager().deleteCell(flatCell, true);
            if (debug)
                NMRUtilities.createOutputFile("s_" + JEdifParserCommandGroup.getInputFileName(result) + ".edf",
                        flatCell);
            if (reportTiming)
                startTime = LogFileCommandGroup.reportTime(startTime, "Half-latch removal", LogFile.out());
            flatCell = hlflatCell;

        }
        return flatCell;
    }

}