package edu.byu.ece.edif.jedif;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.replicate.nmr.CircuitDescription;
import edu.byu.ece.edif.tools.replicate.nmr.CutsetComputation;
import edu.byu.ece.edif.tools.replicate.nmr.EdifReplicationPropertyReader;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.OrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationType;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.CircuitDescriptionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.CutFeedbackCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifParserCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ReplicationDescriptionCommandGroup;

public class JEdifVoterSelection extends EDIFMain {
	
	/**
	 * A static executable that determines where the voters are inserted. This executable will update
	 * an existing ReplicationDescription object.
	 */
	public static void main(String[] args) {
		
		PrintStream out = System.out;
		PrintStream err = System.err;
		
		EXECUTABLE_NAME = "JEdifVoterSelection";
		TOOL_SUMMARY_STRING = "Determines voter locations using rules and a cutset algorithm";
		
		printProgramExecutableString(out);
		
		EdifCommandParser parser = new EdifCommandParser();
		
		// option for input .jedif file
		parser.addCommands(new JEdifParserCommandGroup());
		
		// option for input ReplicationDescription file
		parser.addCommands(new ReplicationDescriptionCommandGroup());
		
		// option for input CircuitAnalysisDescription file
		parser.addCommands(new CircuitDescriptionCommandGroup());
		
		// cutset algorithm options (connectivity_cutset, highest_fanout_cutset, highest_ff_fanout_cutset)
		parser.addCommands(new CutFeedbackCommandGroup());
		
		// config file options
		parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));
		
		// log file command group
		parser.addCommands(new LogFileCommandGroup("voter_selection.log"));
		
		JSAPResult result = parser.parse(args, err);
		if (!result.success())
			System.exit(1);
		
		LogFileCommandGroup.CreateLog(result);
		printProgramExecutableString(LogFile.log());
		out = LogFile.out();
		err = LogFile.err();

		
		EdifEnvironment env = JEdifParserCommandGroup.getEdifEnvironment(result, out);
		
		ReplicationDescription rDesc = ReplicationDescriptionCommandGroup.getReplicationDescription(result, env, out);
		
		CircuitDescription cDesc = CircuitDescriptionCommandGroup.getCircuitDescription(result, env, out);
		
		EdifCell topCell = env.getTopCell();
		
		NMRArchitecture arch = cDesc.getNMRArchitecture();
		
		// TODO: We need to make this an option and provide some clock handling.
		boolean skipClockNets = true;
		
		/*
		 * Iterates through every net in the design. It determines what the replication type
		 * of the driver is. It checks to see if there are any properties (Do not restore : ie do
		 * not stick a voter here). Then it checks for a force restore property. 
		 * 
		 * This is setting initial, forced conditions.
		 */
		for (EdifNet net : topCell.getNetList()) {
		    if (rDesc.shouldIgnoreNet(net) || (arch.isClockNet(net) && skipClockNets))
		        continue;
			Collection<EdifPortRef> drivers = net.getSourcePortRefs(true, true);
			if (drivers.size() == 0) {
				System.out.println("Warning: skipping net with no drivers: " + net);
				continue;
			}
			ReplicationType replicationType = null;
            for (EdifPortRef driver : drivers) {
                ReplicationType driverType = rDesc.getReplicationType(driver);
                if (replicationType == null)
                    replicationType = driverType;
                else {
                    if (replicationType != driverType) {
                        System.out.println(drivers);
                        throw new EdifRuntimeException("Unexpected: Net drivers have different ReplicationTypes");
                    }
                }
            }
            
            // Check to see if there is a property to prevent restore. It adds a 
            // special replication type for preventing this restore. In most cases, nothing
            // is returned.
			if (EdifReplicationPropertyReader.isDoNotRestoreLocation(net)) {
				rDesc.addOrganSpecifications(net, replicationType.antiRestore(net, rDesc));
			}
			else if (EdifReplicationPropertyReader.isForceRestoreLocation(net)) {
				rDesc.addOrganSpecifications(net, replicationType.forceRestore(net, null, rDesc));
			}
			else {
				// Adds voters for reduction, etc. Depends on the repcliation tpye of the driver.
				rDesc.addOrganSpecifications(net, replicationType.defaultRestore(net, rDesc));
			}
		}

		// Compute cutset. The actual algorithm used is determined by the command line arguement.
		// TODO: later, the cutset algorithm should take advantage of the voters already added up to this point
		Collection<EdifPortRef> cuts = CutsetComputation.getValidCutset(result, topCell, cDesc, out);

		// save the cutset so it doesn't need to be recomputed later if using JEdifDetectionSelection with persistence detection or JEdifMoreFrequentVoting
		rDesc.setCutsetReference(cuts);
		
		// add organ specifications for cutset (use forceRestore)
		for (EdifPortRef cut : cuts) {
		    EdifNet net = cut.getNet();
		    Collection<EdifPortRef> drivers = net.getSourcePortRefs(true, true);
		    ReplicationType replicationType = null;
		    for (EdifPortRef driver : drivers) {
		        ReplicationType driverType = rDesc.getReplicationType(driver);
		        if (replicationType == null)
		            replicationType = driverType;
		        else {
		            if (replicationType != driverType) {
		                System.out.println(drivers);
		                throw new EdifRuntimeException("Unexpected: Net drivers have different ReplicationTypes");
		            }
		        }
		    }

		    // check to see if there is already a voter to be inserted on this net -- use it if there is
		    Set<OrganSpecification> prevSpecs = rDesc.getOrganSpecifications(net);

		    List<EdifPortRef> forceRefs = new ArrayList<EdifPortRef>(1);
			forceRefs.add(cut);
			
			// this is confusing so I'll explain it -- when prevSpecs is not null and contains an OrganSpecification
			// that can be reused, its organ count will be promoted if necessary, and its list of forceRestoreRefs will
			// have forceRefs appended to it. ReplicationType.forceRestore() will return null, so nothing new will be added
			// to the ReplicationDescription -- only the existing OrganSpecification will be modified as necessary. The
			// effect is to create a new OrganSpecification only when necessary.
			rDesc.addOrganSpecifications(net, replicationType.forceRestore(net, forceRefs, prevSpecs, rDesc));
		}
		
		// rewrite replication description file
		ReplicationDescriptionCommandGroup.writeReplicationDescription(result, env, rDesc, out);
		
		out.println();
	}
	
	
    

}
