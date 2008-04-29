/*
 * Perform the actual triplication and generate a netlist.
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

import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifUtils;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.replicate.PartialReplicationDescription;
import edu.byu.ece.edif.tools.replicate.PartialReplicationStringDescription;
import edu.byu.ece.edif.tools.replicate.ReplicationException;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.TMREdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.XilinxTMRArchitecture;
import edu.byu.ece.edif.util.jsap.EDIFMain;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.InputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.OutputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.PTMRFileCommandGroup;

/**
 * Final stage of TMR process. This executable will perform the actual
 * triplication and generate a netlist.
 */
public class JEdifTMR extends EDIFMain {

    /**
     * Issues:
     * <ul>
     * <li>Need to import the entire device utilization tracking. It would be
     * nice to print out the estimated device utilization if the user wants it
     * (wait until this is done).</li>
     * <li>Allow *any* cell to be triplicated, not just the top cell. Provide
     * an option for this.</li>
     * <li> Add an option that allows user to specify new cell name, or to
     * specify that the new cell name is just the old cell name with an
     * extension. These two options would be mutually exclusive.</li>
     * </ul>
     */
    public static void main(String args[]) {

        // Define the print streams for this program
        PrintStream out = System.out;
        PrintStream err = System.out;

        // Print executable heading
        EXECUTABLE_NAME = "JEdifTMR";
        TOOL_SUMMARY_STRING = "Triplicates .jedif netlist according to previous triplication policy.";
        printProgramExecutableString(out);

        // Parse command line options
        EdifCommandParser parser = new EdifCommandParser();
        // TODO: JEdifParserCommand has a simple static method. Better place for it? 
        parser.addCommands(new InputFileCommandGroup());
        parser.addCommands(new OutputFileCommandGroup());
        parser.addCommands(new JEdifTMRParserCommandGroup());
        parser.addCommands(new PTMRFileCommandGroup());
        parser.addCommands(new LogFileCommandGroup("JEdifTMR.log"));
        JSAPResult result = parser.parse(args, err);
        if (!result.success())
            System.exit(1);

        LogFileCommandGroup.CreateLog(result);
        out = LogFile.out();
        err = LogFile.err();

        printProgramExecutableString(LogFile.log());

        // Obtain the .jedif file and obtain references to various objects
        EdifEnvironment top = JEdifParserCommandGroup.getEdifEnvironment(result, out);

        // TODO: output file options
        // - An output file is required (require -o flag)
        // - Provide a -edif flag that will generate EDIF instead of .jedif

        // Parse PartialTMRDescription
        PartialReplicationStringDescription sdesc = PTMRFileCommandGroup.getPartialReplicationDescription(result, out);
        PartialReplicationDescription desc = null;
        try {
            desc = sdesc.getDescription(top);
        } catch (ReplicationException e) {
            e.toRuntime();
        }
        if (desc == null)
            System.exit(1);

        tmr(result, top, desc);
        // 21. Write output file
        String outputFileName = OutputFileCommandGroup.getOutputFileName(result);
        if (!result.userSpecified(OutputFileCommandGroup.OUTPUT_OPTION)) {
            outputFileName = InputFileCommandGroup.getInputFileName(result);
            outputFileName = outputFileName.substring(0, outputFileName.lastIndexOf('.'));
            outputFileName += "_tmr";
            if (result.getBoolean(JEdifTMRParserCommandGroup.GENERATE_EDIF_FLAG))
                outputFileName += ".edf";
            else
                outputFileName += ".jedif";
        }
        if (result.getBoolean(JEdifTMRParserCommandGroup.GENERATE_EDIF_FLAG)) {
            // Generate EDIF output
            LogFile.out().println("Generating .edf file " + outputFileName);
            try {
                JEdifNetlist.generateEdifNetlist(top, result, EXECUTABLE_NAME, VERSION_STRING);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            // Generate .jedif output
            LogFile.out().println("Generating .jedif file " + outputFileName);
            // Serialize output
            OutputFileCommandGroup.serializeObject(LogFile.out(), outputFileName, top);
        }

    }

    public static void tmr(JSAPResult result, EdifEnvironment top, PartialReplicationDescription desc) {
        // Create TMR architecture
        // TODO: make this more parameterizable. Base this on part name.
        // For now, hard code to Xilinx. Note that this is different from the
        // FlattenTMR. In this case, you need to specify the architecture.
        // See Derrick's command group for feedback

        NMRArchitecture tmrArch = new XilinxTMRArchitecture();
        EdifCell topCell = top.getTopCell();
        EdifLibrary topLib = topCell.getLibrary();
        // Determine the name of the new cell 
        EdifNameable newCellName = null;
        try {
            newCellName = JEdifTMRParserCommandGroup.getCellName(result);
            if (newCellName == null) {
                newCellName = new NamedObject(topCell.getName() + "_TMR");
            }
        } catch (InvalidEdifNameException edif) {
            LogFile.err().println("Error: " + newCellName + " is an invalid EDIF name");
            System.exit(1);
        }
        // Triplicate design		
        LogFile.out().print("Triplicating design . . .");
        TMREdifCell tmrCell = null;
        try {
            //			if (result.contains(TMRCommandParser.TMR_SUFFIX))
            // Use user-supplied suffixes
            tmrCell = new TMREdifCell(topLib, newCellName, tmrArch, desc, JEdifTMRParserCommandGroup
                    .getUserTMRPorts(result), JEdifTMRParserCommandGroup.getTMRSuffix(result));
            //			else
            //				// Use default suffixes
            //				tmrCell = new TMREdifCell(topLib, newCellName,
            //						tmrArch, JEdifTMRParserCommandGroup.getUserTMRPorts(result), desc);

        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
        LogFile.out().println("Done");
        // Write domain report
        String domain_report_filename = JEdifTMRParserCommandGroup.getDomainReportFilename(result);
        try {
            tmrCell.printDomainReport(domain_report_filename);
        } catch (FileNotFoundException e) {
            LogFile.err().println(e.toString());
            System.exit(1);
        }
        // Compute statistics
        printStats(LogFile.out(), topCell, tmrCell);
        // 19. Set TMR instance to top cell
        EdifCellInstance tmrInstance = null;
        EdifDesign newDesign = null;
        try {
            tmrInstance = new EdifCellInstance(tmrCell.getName(), null, tmrCell);
            newDesign = new EdifDesign(tmrCell.getEdifNameable());
        } catch (InvalidEdifNameException e1) {
            e1.toRuntime();
        }
        newDesign.setTopCellInstance(tmrInstance);
        // copy design properties
        // TODO: is there not an automated way of doing this?
        EdifDesign oldDesign = topCell.getLibrary().getLibraryManager().getEdifEnvironment().getTopDesign();
        if (oldDesign.getPropertyList() != null) {
            for (Object o : oldDesign.getPropertyList().values()) {
                Property p = (Property) o;
                newDesign.addProperty((Property) p.clone());
            }
        }
        top.setTopDesign(newDesign);
        /*
         * flatCell is no longer needed after TMR, so it deletes itself from the
         * library it belongs to.
         */
        topCell.getLibrary().deleteCell(topCell, true);
    }

    public static void printStats(PrintStream out, EdifCell origCell, TMREdifCell tmrCell) {

        int numberOfVoters = 0;
        int numberOfTriplicatedInstances = 0;
        int numberOfTriplicatedNets = 0;
        int numberOfTriplicatedPorts = 0;

        numberOfVoters = tmrCell.getVoters().size();
        numberOfTriplicatedInstances = tmrCell.getReplicatedInstances().size();
        numberOfTriplicatedNets = tmrCell.getReplicatedNets().size();
        numberOfTriplicatedPorts = tmrCell.getReplicatedPorts().size();

        int numberOfOriginalInstances = origCell.getSubCellList().size();
        out.println("\tAdded " + numberOfVoters + " voters.");
        out.println("\t" + numberOfTriplicatedInstances + " instances out of " + numberOfOriginalInstances
                + " cells triplicated (" + (numberOfTriplicatedInstances * 100 / numberOfOriginalInstances)
                + "% coverage)");
        out.println("\t" + 2 * numberOfTriplicatedInstances + " new instances added to design. ");
        out.println("\t" + numberOfTriplicatedNets + " nets triplicated (" + 2 * numberOfTriplicatedNets
                + " new nets added).");
        out.println("\t" + numberOfTriplicatedPorts + " ports triplicated.");

        // 20. Print Report to stdout if desired; always print to log file
        int tmrPrimitives = EdifUtils.countRecursivePrimitives(tmrCell);
        int flatPrimitives = EdifUtils.countRecursivePrimitives(origCell);
        int tmrNets = EdifUtils.countRecursiveNets(tmrCell);
        int flatNets = EdifUtils.countRecursiveNets(origCell);
        int tmrPortRefs = EdifUtils.countPortRefs(tmrCell, true);
        int flatPortRefs = EdifUtils.countPortRefs(origCell, true);

        out.println("");
        out.println("TMR circuit contains:");
        out.println("\t" + tmrPrimitives + " primitives (" + (100 * (tmrPrimitives - flatPrimitives)) / flatPrimitives
                + "% increase)");
        out.println("\t" + tmrNets + " nets (" + (100 * (tmrNets - flatNets)) / flatNets + "% increase)");
        out.println("\t" + tmrPortRefs + " net connections (" + (100 * (tmrPortRefs - flatPortRefs)) / flatPortRefs
                + "% increase)");

        // TODO: Perform du tracking?
        //out.println("\nPost TMR utilization estimate:\n" + duTracker);

    }
}
