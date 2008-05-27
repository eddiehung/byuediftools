/*
 * TODO: Insert class description here.
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
import java.util.Arrays;
import java.util.Collection;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCellInstance;
import edu.byu.ece.edif.tools.replicate.PartialReplicationDescription;
import edu.byu.ece.edif.tools.replicate.PartialReplicationStringDescription;
import edu.byu.ece.edif.tools.replicate.ReplicationException;
import edu.byu.ece.edif.tools.replicate.nmr.MoreFrequentVoting;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.NMRUtilities;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.graph.EdifPortRefEdge;
import edu.byu.ece.edif.util.jsap.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.EDIFMain;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.InputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.MergeParserCommandGroup;
import edu.byu.ece.edif.util.jsap.MoreFrequentVotingCommandGroup;
import edu.byu.ece.edif.util.jsap.OutputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.PTMRFileCommandGroup;
import edu.byu.ece.edif.util.jsap.TechnologyCommandGroup;
import edu.byu.ece.graph.Edge;

public class JEdifMoreFrequentVoting extends EDIFMain {

    /**
     * @param args
     */
    public static void main(String[] args) {

        // Define the print streams for this program
        PrintStream out = System.out;
        PrintStream err = System.err;

        // Print executable heading
        EXECUTABLE_NAME = "JEdifMoreFrequentVoting";
        TOOL_SUMMARY_STRING = "Identifies PortRefs to cut for possible voter insertion for NMR with frequent voting";
        printProgramExecutableString(out);

        // Parse command line options
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new InputFileCommandGroup());
        OutputFileCommandGroup of = new OutputFileCommandGroup();
        of.setDefaultFilename("<inputfile>.ptmr");
        parser.addCommands(of);
        parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));
        parser.addCommands(new TechnologyCommandGroup());
        parser.addCommands(new PTMRFileCommandGroup());
        parser.addCommands(new MoreFrequentVotingCommandGroup());
        LogFileCommandGroup loggerCG = new LogFileCommandGroup();
        parser.addCommands(loggerCG);

        JSAPResult result = parser.parse(args, err);
        if (!result.success()) {
            // Report error and exit
            err.println("ERROR: Could not parse arguments.");
            System.exit(1);
        }

        LogFileCommandGroup.CreateLog(result);
        out = LogFile.out();
        err = LogFile.err();

        printProgramExecutableString(LogFile.log());

        // can be used to log currently used options, as well as to create help files
        LogFile.log().println("These command-line options were used:" + Arrays.asList(args) + "\n");

        // One of the two options must have been specified
        int voter_threshold = MoreFrequentVotingCommandGroup.getVoterThreshold(result, out);
        int num_partitions = MoreFrequentVotingCommandGroup.getNumPartitions(result, out);
        if (voter_threshold == -1 && num_partitions == -1) {
            // Report error and exit
            err.println("ERROR: A valid voter insertion parameter must be specified.");
            System.exit(1);
        } else if (voter_threshold != -1 && num_partitions != -1) {
            // Report error and exit
            err.println("ERROR: Only one voter insertion parameter should be specified.");
            System.exit(1);
        }

        // Get Strings from command line for user-specified voter locations
        Collection<String> voterLocations = new ArrayList<String>();
        String[] voterLocationArray = MoreFrequentVotingCommandGroup.getVoterLocations(result, out);
        if (voterLocationArray != null)
            voterLocations.addAll(Arrays.asList(MoreFrequentVotingCommandGroup.getVoterLocations(result, out)));

        // Create EdifEnvironment data structure
        EdifEnvironment top = JEdifParserCommandGroup.getEdifEnvironment(result, out);
        if (top == null) {
            // Report error and exit
            err.println("ERROR: Could not read JEdif file.");
            System.exit(1);
        }
        TechnologyCommandGroup.getPartFromEDIF(result, top);

        EdifCell cell = top.getTopCell();
        if (!(cell instanceof FlattenedEdifCell)) {
            // Report error and exit
            err.println("ERROR: JEdif file does not contain a Flattened EdifCell as the top cell.");
            System.exit(1);
        }
        FlattenedEdifCell flatCell = (FlattenedEdifCell) cell;

        // Create graph
        EdifCellInstanceGraph graph = new EdifCellInstanceGraph(flatCell);

        // Parse PartialTMRDescription
        PartialReplicationStringDescription sdesc = PTMRFileCommandGroup.getPartialReplicationDescription(result, out);
        PartialReplicationDescription desc = null;
        try {
            desc = sdesc.getDescription(top);
        } catch (ReplicationException e) {
            e.toRuntime();
        }
        if (desc == null) {
            // Report error and exit
            LogFile.err().println("ERROR: Could not read PTMRDescription file.");
            System.exit(1);
        }

        // Create TMR architecture
        NMRArchitecture nmrArch = TechnologyCommandGroup.getArch(result);

        // Get the voter EPRs from the user-specified voter location instances
        Collection<EdifPortRef> userCutset = getVoterEPRsFromInstanceNames(voterLocations, flatCell, graph);

		//////////////
		//// BHP: HACK to remove edges from the graph that throw off my partitioning code
		//Collection<EdifPortRefEdge> edgesToRemove = new ArrayList<EdifPortRefEdge>();
		//for (EdifPortRefEdge edge : graph.getEdges()) {
		//    EdifPortRef sourceEPR = edge.getSourceEPR();
		//    Object source = edge.getSource();
		//    EdifPortRef sinkEPR = edge.getSinkEPR();
		//    //if (source instanceof EdifCellInstance && ((EdifCellInstance)source).getType().equalsIgnoreCase("BUFG"))
		//    if (source instanceof EdifCellInstance && ((EdifCellInstance) source).getName().startsWith("clk")
		//    		|| source instanceof EdifCellInstance && ((EdifCellInstance) source).getName().startsWith("safeConstant")
		//    		|| source instanceof EdifCellInstance && ((EdifCellInstance) source).getName().startsWith("HL_INV")
		//    		|| source instanceof EdifCellInstance && ((EdifCellInstance) source).getName().startsWith("reset")
		//            || source instanceof EdifSingleBitPort && sourceEPR.getPort().getName().startsWith("xp_in")
		//            || source instanceof EdifSingleBitPort && sourceEPR.getPort().getName().startsWith("clk")
		//            || source instanceof EdifSingleBitPort && sourceEPR.getPort().getName().startsWith("reset")
		//            || source instanceof EdifSingleBitPort && sourceEPR.getPort().getName().startsWith("rst")) {
		//        //System.out.println("Removing edge "+edge);
		//        edgesToRemove.add(edge);
		//    }
		//    // Check if bus member is "2", this is the one that jumps
		//    // Or if this edge drives LUT input "I0"
		//    if (sinkEPR.getSingleBitPort().getPortName().equals("I0")) {
		//        //System.out.println("Removing edge "+edge);
		//        edgesToRemove.add(edge);
		//    }
		//
		//}
		//graph.removeEdges(edgesToRemove);
		///////////////

        // Get the cutset for More Frequent Voting and add it to the PartialTMRDescription
        addMoreFrequentVoting(nmrArch, voter_threshold, num_partitions, userCutset, graph, desc);

        // Write out PTMR Description
        try {
            sdesc = new PartialReplicationStringDescription(desc);
        } catch (ReplicationException e) {
            e.toRuntime();
        }

        if (!result.userSpecified(OutputFileCommandGroup.OUTPUT_OPTION)) {
            String name = MergeParserCommandGroup.getInputFileName(result);
            name = name.substring(0, name.lastIndexOf('.'));
            OutputFileCommandGroup.serializeObject(out, name + ".ptmr", sdesc);
        } else {
            OutputFileCommandGroup.serializeObject(out, result, sdesc);
        }

        LogFile.out().println("Done");
    }

    /**
     * @param logger
     * @param voterLocations
     * @param flatCell
     * @param graph
     * @return
     */
    protected static Collection<EdifPortRef> getVoterEPRsFromInstanceNames(Collection<String> voterLocations,
            FlattenedEdifCell flatCell, EdifCellInstanceGraph graph) {
        // Find voter locations from instance names
        Collection<FlattenedEdifCellInstance> voterInstances = new ArrayList<FlattenedEdifCellInstance>();
        // a. Get EdifCellInstances that correspond to these Strings
        for (String instanceName : voterLocations) {
            LogFile.out().println("Adding voter at the output(s) of instance " + instanceName);
            // Get the  corresponding instance
            FlattenedEdifCellInstance instance = ((FlattenedEdifCell) flatCell).getFlatInstance(instanceName);
            // Give user warning if no match was found
            if (instance == null)
                LogFile.err().println("\tWARNING: No match for instance " + instanceName);
            else
                voterInstances.add(instance);
        }
        // b. Get output PortRefs
        Collection<EdifPortRef> userCutset = new ArrayList<EdifPortRef>();
        for (FlattenedEdifCellInstance instance : voterInstances) {
            Collection<EdifPortRefEdge> outputEdges = graph.getOutputEdges(instance);
            for (EdifPortRefEdge outputEdge : outputEdges) {
                userCutset.add(outputEdge.getSourceEPR());
                LogFile.debug().println("Adding voter at EdifPortRef: " + outputEdge.getSourceEPR());
            }
        }
        return userCutset;
    }

    /**
     * @param result
     * @param logger
     * @param voter_threshold
     * @param num_partitions
     * @param graph
     * @param desc
     */
    public static void addMoreFrequentVoting(NMRArchitecture nmrArch, int voter_threshold, int num_partitions,
            Collection<EdifPortRef> voter_locations, EdifCellInstanceGraph graph, PartialReplicationDescription desc) {
        // Get cutset (possibly populated by feedback cutset determination
        Collection<EdifPortRef> EPRCutSet = desc.portRefsToCut;

        // Add user-specified voter locations
        EPRCutSet.addAll(voter_locations);

        int origSize = EPRCutSet.size();
        // Get Edge cutset from EPRs
        Collection<Edge> cutSet = MoreFrequentVoting.getEdifEdgesFromPortRefs(graph, EPRCutSet);

        // Determine Voter Locations
        Collection<Edge> newCutSet = null;
        if (voter_threshold != -1) {
            LogFile.out().print(
                    "Determining voter insertion locations using voter threshold of " + voter_threshold + " . . .");
            newCutSet = MoreFrequentVoting.insertVotersByLogicLevels(graph, nmrArch, cutSet, voter_threshold);
        } else {
            LogFile.out().print("Determining voter insertion locations using " + num_partitions + " partitions . . .");
            newCutSet = MoreFrequentVoting.partitionGraphWithVoters(graph, nmrArch, cutSet, num_partitions);
        }

        // Get PortRefs to cut from edges, replace old cutset
        Collection<EdifPortRef> newEPRCutSet = NMRUtilities.getPortRefsToCutFromEdges(newCutSet, graph, nmrArch);
        //desc.portRefsToCut.addAll(newEPRCutSet);
        EPRCutSet.addAll(newEPRCutSet);
        // Done
        LogFile.out().println("Done");

        LogFile.out().println("Added " + (EPRCutSet.size() - origSize) + " potential cuts to the cut set.");
    }

}
