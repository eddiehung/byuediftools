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
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.replicate.nmr.CircuitDescription;
import edu.byu.ece.edif.tools.replicate.nmr.CutsetComputation;
import edu.byu.ece.edif.tools.replicate.nmr.DetectorOrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.Organ;
import edu.byu.ece.edif.tools.replicate.nmr.OrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DWCReplicationType;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DetectionDomain;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DetectionType;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DualRailDetectionType;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.SingleRailDetectionType;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.TMRReplicationType;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxDWCComparator;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxDaisyChainDetectionMerger;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxTMRComparator;
import edu.byu.ece.edif.tools.replicate.wiring.SinglePortConnection;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.CircuitDescriptionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.CutFeedbackCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifParserCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifPersistenceDetectionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ReplicationDescriptionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ReplicationTypeCommandGroup;


public class JEdifPersistenceDetection extends EDIFMain {


	
	public static void main(String[] args) {

		PrintStream out = System.out;
		PrintStream err = System.err;

		EXECUTABLE_NAME = "JEdifPersistenceDetection";
		TOOL_SUMMARY_STRING = "Determines persistence detector locations based on user-specified options. " +
				"Note: if a cutset has already been computed in a previous executable (i.e. JEdifVoterSelection" +
				" or JEdifMoreFrequentVoting), cutset commandline options will be ignored and the previously computed" +
				" cutset will be used.";

		printProgramExecutableString(out);

		EdifCommandParser parser = new EdifCommandParser();

		// option for input .jedif file
		parser.addCommands(new JEdifParserCommandGroup());

		// option for input ReplicationDescription file
		parser.addCommands(new ReplicationDescriptionCommandGroup());

		// option for input CircuitDescription file
		parser.addCommands(new CircuitDescriptionCommandGroup());
        
        // option for duplication/triplication
        parser.addCommands(new ReplicationTypeCommandGroup());
        
        // cutset options
        parser.addCommands(new CutFeedbackCommandGroup());
        
        // other options
        parser.addCommands(new JEdifPersistenceDetectionCommandGroup());
		
		// config file options
		parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));
		
		// log file command group
		parser.addCommands(new LogFileCommandGroup("detection_selection.log"));
		
				
		JSAPResult result = parser.parse(args, err);
		if (!result.success() || !JEdifPersistenceDetectionCommandGroup.validateOptions(result, err))
			System.exit(1);
		LogFileCommandGroup.CreateLog(result);
		printProgramExecutableString(LogFile.log());
		out = LogFile.out();
		err = LogFile.err();		
		
		EdifEnvironment env = JEdifParserCommandGroup.getEdifEnvironment(result, out);

		ReplicationDescription rDesc = ReplicationDescriptionCommandGroup.getReplicationDescription(result, env, out);

		CircuitDescription cDesc = CircuitDescriptionCommandGroup.getCircuitDescription(result, env, out);
		NMRArchitecture arch = cDesc.getNMRArchitecture();
		
		EdifCell topCell = env.getTopCell();
		
		boolean insertObufs = !JEdifPersistenceDetectionCommandGroup.noObufs(result);
		boolean insertOregs = !JEdifPersistenceDetectionCommandGroup.noOregs(result);
		String clockNetName = null;
		if (result.userSpecified(JEdifPersistenceDetectionCommandGroup.CLOCK_NET))
			clockNetName = JEdifPersistenceDetectionCommandGroup.getClockNetName(result);
		
		// set replication type class and comparator organ
		String replicationTypeString = result.getString(ReplicationTypeCommandGroup.REPLICATION_TYPE);
		Class userReplicationTypeClass = null;
		Organ comparatorOrgan = null;
		if (replicationTypeString.compareToIgnoreCase(ReplicationTypeCommandGroup.TRIPLICATION) == 0) {
			userReplicationTypeClass = TMRReplicationType.class;
			comparatorOrgan = XilinxTMRComparator.getInstance();
		}
		else if (replicationTypeString.compareToIgnoreCase(ReplicationTypeCommandGroup.DUPLICATION) == 0) {
			userReplicationTypeClass = DWCReplicationType.class;
			comparatorOrgan = XilinxDWCComparator.getInstance();
		}
		
		// set detection type, signal width
		DetectionType detectionType = null;
		String railTypeString = JEdifPersistenceDetectionCommandGroup.getRailTypeString(result);
		int signalWidth = 0;
		if (railTypeString.compareToIgnoreCase("single") == 0) {
			detectionType = SingleRailDetectionType.getInstance(comparatorOrgan);
			signalWidth = 1;
		}
		else if (railTypeString.compareToIgnoreCase("dual") == 0) {
			detectionType = DualRailDetectionType.getInstance(comparatorOrgan);
			signalWidth = 2;
		}
		
		// find port for getting detection outputs
		String portName = NamedObject.createValidEdifString(JEdifPersistenceDetectionCommandGroup.getDetectionPortName(result));
		boolean needsPortCreated = false;
		List<SinglePortConnection> detectionOutput = null;
		EdifPort port = topCell.getPort(portName);
		if (port == null) {
			out.println("Detection output port: " + portName + " not found in top cell; It will be created in the JEdifNMR step.");
			needsPortCreated = true;
		}
		else {
			if (port.getWidth() != signalWidth) {
				err.println("Error: specified port (" + portName + ") has a signal width (" + port.getWidth() + ") that doesn't match the chosen detection type.");
				System.exit(3);
			}

			detectionOutput = new ArrayList<SinglePortConnection>(signalWidth);
			for (int i = 0; i < signalWidth; i++) {
				SinglePortConnection connection = new SinglePortConnection(port.getSingleBitPort(i), null);
				detectionOutput.add(connection);
			}
		}
		
		// get a cutset: use a previously computed one or generate a new one
		Collection<EdifPortRef> cutset = rDesc.getCutsetReference();
		if (cutset == null) {// need to generate a cutset 
			cutset = CutsetComputation.getValidCutset(result, topCell, cDesc, out);
		}
		
		// create detection domain
		DetectionDomain detectionDomain = new DetectionDomain(detectionType);

		int numSpecs = 0;
		
		// add organ specifications for cutset
		boolean skipClockNets = true;
		for (EdifPortRef cut : cutset) {
			EdifNet net = cut.getNet();
			if (arch.isClockNet(net) && skipClockNets)
			    continue;
			// check to see if there is already a detector to be inserted on this net -- use it if there is
			Set<OrganSpecification> prevSpecs = rDesc.getOrganSpecifications(net);
			DetectorOrganSpecification osToReuse = null;
			if (prevSpecs != null) {
				for (OrganSpecification os : prevSpecs) {
					if (os instanceof DetectorOrganSpecification) {
						DetectorOrganSpecification dos = (DetectorOrganSpecification) os;
						if (dos.getOrganType() == comparatorOrgan) {
							osToReuse = dos;
							if (dos.getOrganCount() < detectionType.getSignalWidth())
								dos.promoteOrganCountUpTo(detectionType.getSignalWidth());
							break;
						}
					}
				}
			}
			
			if (osToReuse != null) {
				numSpecs += 1;
				rDesc.addDetectionOrganSpecification(detectionDomain, net, osToReuse);
			}
			else {
				List<OrganSpecification> detectors = detectionType.forceDetect(net, rDesc);
				if (detectors != null) {
					numSpecs += detectors.size();
					rDesc.addDetectionOrganSpecifications(detectionDomain, net, detectors);
				}
			}
		}
		
		// associate the detection domain just created with a DetectionOutputSpecification (pre-existing or not) in the ReplicationDescription
		if (numSpecs > 0) {
			if (needsPortCreated)
				rDesc.associateDetectionOutputWithDomain(detectionType, portName, detectionDomain, XilinxDaisyChainDetectionMerger.getInstance(), insertOregs, insertObufs, clockNetName);
			else
				rDesc.associateDetectionOutputWithDomain(detectionType, detectionOutput, detectionDomain, XilinxDaisyChainDetectionMerger.getInstance(), insertOregs, insertObufs, clockNetName);

			// rewrite replication description file
			ReplicationDescriptionCommandGroup.writeReplicationDescription(result, env, rDesc, out);
		}
		else {
			out.println("Warning: no detectors were added. ReplicationDescription unchanged.");
		}
	
		out.println();
	}

}
