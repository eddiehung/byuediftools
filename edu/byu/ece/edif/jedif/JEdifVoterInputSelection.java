package edu.byu.ece.edif.jedif;

import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.replicate.nmr.DetectorOrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.Organ;
import edu.byu.ece.edif.tools.replicate.nmr.OrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.PassThroughOrgan;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;
import edu.byu.ece.edif.tools.replicate.nmr.RestoringOrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DetectionDomain;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DetectionType;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.PassThroughDetectionMerger;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.TriPassThroughDetectionType;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifParserCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ReplicationDescriptionCommandGroup;

/**
 * JEdifVoterInputSelection is designed to find the locations of the voters
 * inserted in a previous step. The inputs to these voters can then be tapped
 * to give visibility in the form of top-level ports to the inputs of these
 * voters. This is helpful when trying to see into the triplicated design
 * at a point before the voters have masked any errors.
 * 
 * @author Brian Pratt
 */
public class JEdifVoterInputSelection extends EDIFMain {
	
	public static void main(String[] args) {
		
		PrintStream out = System.out;
		PrintStream err = System.err;
		
		EXECUTABLE_NAME = "JEdifVoterInputSelection";
		TOOL_SUMMARY_STRING = "Determines location of voter inputs (for inserting top-level ports there) by finding previously-inserted voters";
		
		printProgramExecutableString(out);
		
		EdifCommandParser parser = new EdifCommandParser();
		
		// option for input .jedif file
		parser.addCommands(new JEdifParserCommandGroup());
		
		// option for input ReplicationDescription file
		parser.addCommands(new ReplicationDescriptionCommandGroup());
		
		// config file options
		parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));
		
		// log file command group
		parser.addCommands(new LogFileCommandGroup("voter_input_selection.log"));
		
		JSAPResult result = parser.parse(args, err);
		if (!result.success())
			System.exit(1);
		
		LogFileCommandGroup.CreateLog(result);
		printProgramExecutableString(LogFile.log());
		out = LogFile.out();
		err = LogFile.err();

		
		EdifEnvironment env = JEdifParserCommandGroup.getEdifEnvironment(result, out);
		
		ReplicationDescription rDesc = ReplicationDescriptionCommandGroup.getReplicationDescription(result, env, out);
		
		// Create a port/pass-through organ
		Organ passthroughOrgan = new PassThroughOrgan();
		
		// Create a DetectionDomain
		DetectionType detectionType = TriPassThroughDetectionType.getInstance(passthroughOrgan);
		
		// Iterate over all OrganSpecifications
		// Find the restoring organs
		// Insert new OrganSpecifications at the same Nets
		Map<EdifNet,Set<OrganSpecification>> organSpecSets = rDesc.getOrganSpecifications();
		for (EdifNet net : organSpecSets.keySet()) {
			// Is there a restoring organ on this Net?
			boolean selectNet = false;
			for (OrganSpecification organSpec : organSpecSets.get(net)) {
				if (organSpec instanceof RestoringOrganSpecification) {
					selectNet = true;
					break;
				}
			}
			// Add a new OrganSpecification on this Net
			if (selectNet) {
				DetectionDomain detectionDomain = new DetectionDomain(detectionType);
				OrganSpecification passthroughOrganSpec = new DetectorOrganSpecification(passthroughOrgan, 1, net);
				rDesc.addDetectionOrganSpecification(detectionDomain, net, passthroughOrganSpec);

				// associate the detection domain just created with a DetectionOutputSpecification (pre-existing or not) in the ReplicationDescription
				rDesc.associateDetectionOutputWithDomain(detectionType, net.getName(), detectionDomain, PassThroughDetectionMerger.getInstance(), false, false, null);
			}
		}
		
		// rewrite replication description file
		ReplicationDescriptionCommandGroup.writeReplicationDescription(result, env, rDesc, out);
		
		out.println();
	}
	
	
    

}
