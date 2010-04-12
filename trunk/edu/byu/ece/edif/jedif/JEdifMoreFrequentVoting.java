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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.flatten.PreservedHierarchyByNames;
import edu.byu.ece.edif.tools.replicate.nmr.EdifReplicationPropertyReader;
import edu.byu.ece.edif.tools.replicate.nmr.MoreFrequentVoting;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.NMRUtilities;
import edu.byu.ece.edif.tools.replicate.nmr.OrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;
import edu.byu.ece.edif.tools.replicate.nmr.RestoringOrganSpecification;
import edu.byu.ece.edif.util.export.serialize.JEdifFileContents;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.graph.EdifPortRefEdge;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.InputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifParserCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.MoreFrequentVotingCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ReplicationDescriptionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.TechnologyCommandGroup;
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
        parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));
        parser.addCommands(new TechnologyCommandGroup());
        parser.addCommands(new ReplicationDescriptionCommandGroup());
        parser.addCommands(new MoreFrequentVotingCommandGroup());
        LogFileCommandGroup loggerCG = new LogFileCommandGroup("more_frequent.log");
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
        Collection<String> voterInstanceLocations = new ArrayList<String>();
        Collection<String> voterNetLocations = new ArrayList<String>();
        String[] voterLocationInstanceArray = MoreFrequentVotingCommandGroup.getVoterInstanceLocations(result);
        if (voterLocationInstanceArray != null)
            voterInstanceLocations.addAll(Arrays.asList(voterLocationInstanceArray));
        String[] voterLocationNetArray = MoreFrequentVotingCommandGroup.getVoterNetLocations(result);
        if (voterLocationNetArray != null)
        	voterNetLocations.addAll(Arrays.asList(voterLocationNetArray));
        
        // Create EdifEnvironment data structure and get hierarchy (if present in .jedif)
        
        JEdifFileContents jEdifFile = JEdifParserCommandGroup.getJEdifFileContents(result, out);
        EdifEnvironment top = jEdifFile.getEdifEnvironment();
        if (top == null) {
            // Report error and exit
            err.println("ERROR: Could not read JEdif file.");
            System.exit(1);
        }
        TechnologyCommandGroup.getPartFromEDIF(result, top);

        if (!jEdifFile.hasHierarchy()) {
         // Report error and exit
            err.println("ERROR: The design in the .jedif file has not been flattened.");
            System.exit(1);
        }
        PreservedHierarchyByNames hierarchy = jEdifFile.getHierarchy();
        EdifCell topCell = top.getTopCell();
        
        // Create graph
        EdifCellInstanceGraph graph = new EdifCellInstanceGraph(topCell);

        // Read ReplicationDescription
        ReplicationDescription rDesc = ReplicationDescriptionCommandGroup.getReplicationDescription(result, top, out);

        // Create NMR architecture
        NMRArchitecture nmrArch = TechnologyCommandGroup.getArch(result);

        // Get the voter EPRs from the user-specified voter location instances
        Collection<EdifPortRef> userCutset = getVoterEPRsFromInstanceNames(voterInstanceLocations, topCell, graph, hierarchy);
        userCutset.addAll(getVoterEPRsFromNetNames(voterNetLocations, topCell, hierarchy));

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
        
        // Get the cutset for More Frequent Voting and add it to the ReplicationDescription
        addMoreFrequentVoting(nmrArch, voter_threshold, num_partitions, userCutset, graph, rDesc);

        // Rewrite the ReplicationDescription instead
        ReplicationDescriptionCommandGroup.writeReplicationDescription(result, top, rDesc, out);

        LogFile.out().println("Done");
    }

    /**
     * @param logger
     * @param voterLocations
     * @param topCell
     * @param graph
     * @return
     */
    protected static Collection<EdifPortRef> getVoterEPRsFromInstanceNames(Collection<String> voterLocations,
            EdifCell topCell, EdifCellInstanceGraph graph, PreservedHierarchyByNames hierarchy) {
        // Find voter locations from instance names
        Collection<EdifCellInstance> voterInstances = new ArrayList<EdifCellInstance>();
        // a. Get EdifCellInstances that correspond to these Strings
        for (String instanceName : voterLocations) {
            LogFile.out().println("Adding voter at the output(s) of instance " + instanceName);
            // Get the  corresponding instance
            EdifCellInstance instance = hierarchy.getFlatInstance(instanceName, topCell);
            // Give user warning if no match was found
            if (instance == null)
                LogFile.err().println("\tWARNING: No match for instance " + instanceName);
            else
                voterInstances.add(instance);
        }
        // b. Get output PortRefs
        Collection<EdifPortRef> userCutset = new ArrayList<EdifPortRef>();
        for (EdifCellInstance instance : voterInstances) {
            Collection<EdifPortRefEdge> outputEdges = graph.getOutputEdges(instance);
            for (EdifPortRefEdge outputEdge : outputEdges) {
                userCutset.add(outputEdge.getSourceEPR());
                LogFile.debug().println("Adding voter at EdifPortRef: " + outputEdge.getSourceEPR());
            }
        }
        return userCutset;
    }
    
    protected static Collection<EdifPortRef> getVoterEPRsFromNetNames(Collection<String> netLocations, EdifCell topCell, PreservedHierarchyByNames hierarchy) {
    	Collection<EdifPortRef> cuts = new ArrayList<EdifPortRef>();
    	for (String netName : netLocations) {
    		LogFile.out().println("Adding voter at net: " + netName);
    		EdifNet net = hierarchy.getFlatNet(netName, topCell);
    		if (net == null) {
    			LogFile.err().println("\tWARNING: No match for net: " + netName);
    		}
    		else {
    			Collection<EdifPortRef> sourcePortRefs = net.getSourcePortRefs(true, true);
    			LogFile.debug().println("Adding voter at portRefs: " + sourcePortRefs);
    			cuts.addAll(sourcePortRefs);  			
    		}
    	}
    	return cuts;
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
            Collection<EdifPortRef> voter_locations, EdifCellInstanceGraph graph, ReplicationDescription rDesc) {
        
    	// Get pre-existing cuts (possibly populated by feedback cutset determination -- also includes reduction voters and any other voters)
        Collection<EdifPortRef> EPRCutSet = new LinkedHashSet<EdifPortRef>();
        Map<EdifNet, Set<OrganSpecification>> specMap = rDesc.getOrganSpecifications();
        for (EdifNet net : specMap.keySet()) {
        	Set<OrganSpecification> specs = specMap.get(net);
        	Collection<EdifPortRef> drivers = net.getSourcePortRefs(true, true);
        	for (OrganSpecification spec : specs) {
        		if (spec instanceof RestoringOrganSpecification) {
        			EPRCutSet.addAll(drivers);
        		}
        	}
        }

        int origSize = EPRCutSet.size();
        
        // Add user-specified voter locations
        EPRCutSet.addAll(voter_locations);

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
        //add to rDesc instead using OrganSpecifications as in JEdifVoterSelection
        
        boolean skipClockNets = true;
        for (EdifPortRef cut : newEPRCutSet) {
			EdifNet net = cut.getNet();
			if (EdifReplicationPropertyReader.isDoNotRestoreLocation(net) || (nmrArch.isClockNet(net) && skipClockNets))
			    continue;
			
			// check to see if there is already a voter to be inserted on this net -- use it if there is
			Set<OrganSpecification> prevSpecs = rDesc.getOrganSpecifications(net);
						
			List<EdifPortRef> forceRefs = new ArrayList<EdifPortRef>(1);
			forceRefs.add(cut);
			
			// this is confusing so I'll explain it -- when prevSpecs is not null and contains an OrganSpecification
			// that can be reused, its organ count will be promoted if necessary, and its list of forceRestoreRefs will
			// have forceRefs appended to it. ReplicationType.forceRestore() will return null, so nothing new will be added
			// to the ReplicationDescription -- only the existing OrganSpecification will be modified as necessary. The
			// effect is to create a new OrganSpecification only when necessary.
			rDesc.addOrganSpecifications(net, rDesc.getReplicationType(cut).forceRestore(net, forceRefs, prevSpecs, rDesc));

		}
        
        EPRCutSet.addAll(newEPRCutSet);
        
        // Done
        LogFile.out().println("Done");

        LogFile.out().println("Added " + (EPRCutSet.size() - origSize) + " potential cuts to the cut set.");
    }

}
