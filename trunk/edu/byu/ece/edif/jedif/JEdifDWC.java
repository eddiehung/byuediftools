/*
 * Perform the actual duplication and generate a netlist.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifUtils;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.flatten.NewFlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.PartialReplicationDescription;
import edu.byu.ece.edif.tools.replicate.PartialReplicationStringDescription;
import edu.byu.ece.edif.tools.replicate.ReplicationException;
import edu.byu.ece.edif.tools.replicate.ReplicationType;
import edu.byu.ece.edif.tools.replicate.ijmr.TMRDWCEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.XilinxDWCArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.XilinxTMRArchitecture;
import edu.byu.ece.edif.util.clockdomain.ClockDomainParser;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.jsap.EDIFMain;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.InputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.OutputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.PTMRFileCommandGroup;

/**
 * Final stage of DWC process. This executable will perform the actual
 * duplication and generate a netlist.
 */
public class JEdifDWC extends EDIFMain {
    public static void main(String args[]) {

        // Define the print streams for this program
        PrintStream out = System.out;
        PrintStream err = System.out;
        printProgramExecutableString(out);

        // Print executable heading
        EXECUTABLE_NAME = "JEdifDWC";
        TOOL_SUMMARY_STRING = "Duplicates .jedif netlist according to previous duplication policy.";

        // Parse command line options
        EdifCommandParser parser = new EdifCommandParser();
        // TODO: JEdifParserCommand has a simple static method. Better place for it? 
        parser.addCommands(new InputFileCommandGroup());
        parser.addCommands(new OutputFileCommandGroup());
        parser.addCommands(new JEdifDWCParserCommandGroup());
        parser.addCommands(new PTMRFileCommandGroup());
        parser.addCommands(new LogFileCommandGroup("JEdifDWC.log"));
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

        // Parse PartialReplicationDescription
        PartialReplicationStringDescription sdesc = PTMRFileCommandGroup.getPartialReplicationDescription(result, out);
        PartialReplicationDescription desc = null;
        try {
            desc = sdesc.getDescription(top);
        } catch (ReplicationException e) {
            e.toRuntime();
        }
        if (desc == null)
            System.exit(1);

        dwc(result, top, desc);

        // 21. Write output file
        String outputFileName = OutputFileCommandGroup.getOutputFileName(result);
        if (!result.userSpecified(OutputFileCommandGroup.OUTPUT_OPTION)) {
            outputFileName = InputFileCommandGroup.getInputFileName(result);
            outputFileName = outputFileName.substring(0, outputFileName.lastIndexOf('.'));
            outputFileName += "_dwc";
            if (result.getBoolean(JEdifDWCParserCommandGroup.GENERATE_EDIF_FLAG))
                outputFileName += ".edf";
            else
                outputFileName += ".jedif";
        }
        if (result.getBoolean(JEdifDWCParserCommandGroup.GENERATE_EDIF_FLAG)) {
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

    public static void dwc(JSAPResult result, EdifEnvironment top, PartialReplicationDescription desc) {
        // Create DWC architecture
        // TODO: make this more parameterizable. Base this on part name.
        // For now, hard code to Xilinx. Note that this is different from the
        // FlattenTMR. In this case, you need to specify the architecture.
        // See Derrick's command group for feedback
        JEdifDWCParserCommandGroup.getUserTMRPorts(result);

        NMRArchitecture dwcArch = new XilinxDWCArchitecture();
        EdifCell topCell = top.getTopCell();
        // Determine the name of the new cell 
        EdifNameable newCellName = null;
        try {
            newCellName = JEdifDWCParserCommandGroup.getCellName(result);
            if (newCellName == null) {
                newCellName = new NamedObject(topCell.getName() + "_DWC");
            }
        } catch (InvalidEdifNameException edif) {
            LogFile.err().println("Error: " + newCellName + " is an invalid EDIF name");
            System.exit(1);
        }
        // Triplicate design		
        LogFile.out().print("Duplicating design . . .");
        TMRDWCEdifCell dwcCell = null;

        // Get a clock net
        // TODO: put this in a better place (another class to reuse it?)
        EdifCellInstanceGraph graph = new EdifCellInstanceGraph(topCell);
        ClockDomainParser cdp = null;
        try {
            cdp = new ClockDomainParser((NewFlattenedEdifCell) topCell, graph);
        } catch (InvalidEdifNameException e4) {
            e4.toRuntime();
        }
        EdifNet clockNet = cdp.getECIMap().keySet().iterator().next();

        // shuffling data structures in order to use TMRDWCEdifCell class
        Map<Integer, List<EdifPort>> portsToDuplicateMap = new LinkedHashMap<Integer, List<EdifPort>>();
        Map<Integer, List<EdifCellInstance>> instancesToDuplicateMap = new LinkedHashMap<Integer, List<EdifCellInstance>>();
        List<EdifPort> portsToDuplicateList = new ArrayList<EdifPort>();
        portsToDuplicateList.addAll(desc.getPorts(ReplicationType.DUPLICATE));
        List<EdifCellInstance> instancesToDuplicateList = new ArrayList<EdifCellInstance>();
        instancesToDuplicateList.addAll(desc.getInstances(ReplicationType.DUPLICATE));

        // Map will not include any ports or instances to triplicate
        portsToDuplicateMap.put(new Integer(2), portsToDuplicateList);
        portsToDuplicateMap.put(new Integer(3), new ArrayList<EdifPort>());
        instancesToDuplicateMap.put(new Integer(2), instancesToDuplicateList);
        instancesToDuplicateMap.put(new Integer(3), new ArrayList<EdifCellInstance>());
        Collection<EdifPortRef> persistentPortRefsToCompare = desc.portRefsToCut;
        Collection<EdifCellInstance> feedbackPlusInput = desc.getInstances(ReplicationType.FEEDBACK);

        try {
            if (result.contains(JEdifDWCParserCommandGroup.DWC_SUFFIX)) {
                // Use user-supplied suffixes
                // The TMR stuff is so that we can use the TMRDWCEdifCell class
                Map<Integer, List<String>> replicationSuffixMap = new LinkedHashMap<Integer, List<String>>();
                List<String> tmrSuffixes = new ArrayList<String>();
                tmrSuffixes.add("_TMR_0");
                tmrSuffixes.add("_TMR_1");
                tmrSuffixes.add("_TMR_2");
                replicationSuffixMap.put(new Integer(3), tmrSuffixes);
                replicationSuffixMap.put(new Integer(2), Arrays.asList(result
                        .getStringArray(JEdifDWCParserCommandGroup.DWC_SUFFIX)));
                dwcCell = new TMRDWCEdifCell(topCell.getLibrary(), newCellName.toString(), topCell, feedbackPlusInput,
                        new XilinxTMRArchitecture(), dwcArch, portsToDuplicateMap, instancesToDuplicateMap,
                        persistentPortRefsToCompare, result.getBoolean(JEdifDWCParserCommandGroup.USE_DRC), true,
                        result.getBoolean(JEdifDWCParserCommandGroup.REGISTER_DETECTION), clockNet,
                        replicationSuffixMap);
            } else {
                // Use default suffixes
                dwcCell = new TMRDWCEdifCell(topCell.getLibrary(), newCellName.toString(), topCell, feedbackPlusInput,
                        new XilinxTMRArchitecture(), dwcArch, portsToDuplicateMap, instancesToDuplicateMap,
                        persistentPortRefsToCompare, result.getBoolean(JEdifDWCParserCommandGroup.USE_DRC), result
                                .getBoolean(JEdifDWCParserCommandGroup.PACK_DETECTION_REGS), result
                                .getBoolean(JEdifDWCParserCommandGroup.REGISTER_DETECTION), clockNet);
            }

        } catch (EdifNameConflictException e2) {
            e2.toRuntime();
        } catch (InvalidEdifNameException e2) {
            e2.toRuntime();
        }
        LogFile.out().println("Done");
        // Write domain report
        String domain_report_filename = JEdifTMRParserCommandGroup.getDomainReportFilename(result);
        try {
            dwcCell.printDomainReport(domain_report_filename);
        } catch (FileNotFoundException e) {
            LogFile.err().println(e.toString());
            System.exit(1);
        }
        // Compute stats
        printStats(LogFile.out(), topCell, dwcCell);
        // 19. Set DWC instance to top cell
        EdifCellInstance dwcInstance = null;
        EdifDesign newDesign = null;
        try {
            dwcInstance = new EdifCellInstance(dwcCell.getName(), null, dwcCell);
            newDesign = new EdifDesign(dwcCell.getEdifNameable());
        } catch (InvalidEdifNameException e1) {
            e1.toRuntime();
        }
        newDesign.setTopCellInstance(dwcInstance);
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
         * flatCell is no longer needed after dwc, so it deletes itself from the
         * library it belongs to.
         */
        topCell.getLibrary().deleteCell(topCell, true);
    }

    public static void printStats(PrintStream out, EdifCell origCell, TMRDWCEdifCell dwcCell) {

        int numberOfCompares = 0;
        int numberOfOutputCompares = 0;
        int numberOfPersistentCompares = 0;
        int numberOfDuplicatedInstances = 0;
        int numberOfDuplicatedNets = 0;
        int numberOfDuplicatedPorts = 0;

        numberOfCompares = dwcCell.getComparators().size();
        numberOfOutputCompares = dwcCell.getNonPersistentErrorNets().size();
        numberOfPersistentCompares = dwcCell.getPersistentErrorNets().size();
        numberOfDuplicatedInstances = dwcCell.getReplicatedInstances().size();
        numberOfDuplicatedNets = dwcCell.getReplicatedNets().size();
        numberOfDuplicatedPorts = dwcCell.getReplicatedPorts().size();

        int numberOfOriginalInstances = origCell.getSubCellList().size();
        out.print("\tAdded " + numberOfCompares + " compares ");
        out.println("(" + numberOfPersistentCompares + " persistent, " + numberOfOutputCompares + " non-persistent)");

        out.println("\t" + numberOfDuplicatedInstances + " instances out of " + numberOfOriginalInstances
                + " cells duplicated (" + (numberOfDuplicatedInstances * 100 / numberOfOriginalInstances)
                + "% coverage)");
        out.println("\t" + 2 * numberOfDuplicatedInstances + " new instances added to design. ");
        out.println("\t" + numberOfDuplicatedNets + " nets duplicated (" + 2 * numberOfDuplicatedNets
                + " new nets added).");
        out.println("\t" + numberOfDuplicatedPorts + " ports duplicated.");

        // 20. Print Report to stdout if desired; always print to log file
        int dwcPrimitives = EdifUtils.countRecursivePrimitives(dwcCell);
        int flatPrimitives = EdifUtils.countRecursivePrimitives(origCell);
        int dwcNets = EdifUtils.countRecursiveNets(dwcCell);
        int flatNets = EdifUtils.countRecursiveNets(origCell);
        int dwcPortRefs = EdifUtils.countPortRefs(dwcCell, true);
        int flatPortRefs = EdifUtils.countPortRefs(origCell, true);

        out.println("");
        out.println("DWC circuit contains:");
        out.println("\t" + dwcPrimitives + " primitives (" + (100 * (dwcPrimitives - flatPrimitives)) / flatPrimitives
                + "% increase)");
        out.println("\t" + dwcNets + " nets (" + (100 * (dwcNets - flatNets)) / flatNets + "% increase)");
        out.println("\t" + dwcPortRefs + " net connections (" + (100 * (dwcPortRefs - flatPortRefs)) / flatPortRefs
                + "% increase)");

        // TODO: Perform du tracking?
        //out.println("\nPost DWC utilization estimate:\n" + duTracker);

    }
}
