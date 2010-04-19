package edu.byu.ece.edif.jedif;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.replicate.nmr.CircuitDescription;
import edu.byu.ece.edif.tools.replicate.nmr.EdifReplicationPropertyReader;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.Organ;
import edu.byu.ece.edif.tools.replicate.nmr.OrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationType;
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
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.iob.AbstractIOB;
import edu.byu.ece.edif.util.iob.IOBAnalyzer;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.CircuitDescriptionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifDetectionSelectionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifParserCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ReplicationDescriptionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ReplicationTypeCommandGroup;

/**
 * This static executable class is used to select locations for detectors. The inputs
 * to this executable include a .jedif file, a replication description file, a circuit
 * description file, a replication type, and a top-level portname for the detection
 * output.
 */
public class JEdifDetectionSelection extends EDIFMain {
    
	public static void main(String[] args) {

		PrintStream out = System.out;
		PrintStream err = System.err;

		EXECUTABLE_NAME = "JEdifDetectionSelection";
		TOOL_SUMMARY_STRING = "Determines detector locations based on user-specified options";

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
        
        // other options
        parser.addCommands(new JEdifDetectionSelectionCommandGroup());
		
		// config file options
		parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));
		
		// log file command group
		parser.addCommands(new LogFileCommandGroup("detection_selection.log"));
		
		JSAPResult result = parser.parse(args, err);
		if (!result.success() || !JEdifDetectionSelectionCommandGroup.validateOptions(result, err))
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
		
		// Determine from command line whether you want to insert obufs or oregs for the outputs
		boolean insertObufs = !JEdifDetectionSelectionCommandGroup.noObufs(result);
		boolean insertOregs = !JEdifDetectionSelectionCommandGroup.noOregs(result);
		String clockNetName = null;
		if (result.userSpecified(JEdifDetectionSelectionCommandGroup.CLOCK_NET))
			clockNetName = JEdifDetectionSelectionCommandGroup.getClockNetName(result);
		
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
		String railTypeString = JEdifDetectionSelectionCommandGroup.getRailTypeString(result);
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
		String portName = NamedObject.createValidEdifString(JEdifDetectionSelectionCommandGroup.getDetectionPortName(result));
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
		
		// create detection domain
		DetectionDomain detectionDomain = new DetectionDomain(detectionType);

		int numSpecs = 0;
		
		// first find out where the circuit outputs are 
		// (we should detect at the output of the circuit but beforethe output buffer and
		// any output registers)
		IOBAnalyzer iobAnalyzer = cDesc.getIOBAnalyzer();
		EdifCellInstanceGraph instanceGraph = cDesc.getInstanceGraph();
		// This Set includes the nets that we will detect on based on the following analysis
		Set<EdifNet> outputNets = new LinkedHashSet<EdifNet>();
		if (!JEdifDetectionSelectionCommandGroup.noOutputDetection(result)) {
			// We want to detect at the outputs. Iterate over all top-level ports
			for (EdifPort outPort : topCell.getPortList()) {
				int direction = outPort.getDirection();
				if (!(direction == EdifPort.OUT || direction == EdifPort.INOUT))
					// ignore input ports
					continue;

				for (EdifSingleBitPort esbp : outPort.getSingleBitPortList()) {
					// iterate over each bit of the port
					AbstractIOB iob = iobAnalyzer.getIOB(esbp);
					if (iob == null)
						continue;
					// find OBUFs and output registers
					Object bufObject = iob.getOBUF();
					Object regObject = iob.getOutputReg();
					// check to see if there is an obuf object
					if (bufObject != null && (bufObject instanceof EdifCellInstance)) {
						EdifCellInstance buf = (EdifCellInstance) bufObject;
						// if there is an obuf, check to see if there is an output register
						if (regObject != null && (regObject instanceof EdifCellInstance)) {
							EdifCellInstance reg = (EdifCellInstance) regObject;
							if (iobAnalyzer.packOutputRegs()) {
								// There is a register, provide the input of the register as the detect location.
								// The pack IOB option must be set for this to occur.
								Collection<EdifCellInstanceEdge> edges = instanceGraph.getInputEdges(reg, "D");
								if (edges.size() == 1) {
									outputNets.add(edges.iterator().next().getNet());
								}
							} else {
								// There is a register but we are not packing so get the output of the register for the detection
								outputNets.add(((EdifCellInstanceEdge) instanceGraph.getEdge(reg, buf)).getNet());
							}

						} else {
							// There is no ouput register but there is a buffer. Select the input to the buffer
							Collection<EdifCellInstanceEdge> edges = instanceGraph.getInputEdges(buf, "I");
							if (edges.size() == 1) {
								outputNets.add(edges.iterator().next().getNet());
							}
						}
					}
				}
			}		
		}
		
		// At this point, the output detection locations have been determined
		
		detectionType.setUseComparatorForDownscale(!JEdifDetectionSelectionCommandGroup.noDownscaleDetection(result));
		detectionType.setUseComparatorForUpscale(!JEdifDetectionSelectionCommandGroup.noUpscaleDetection(result));
		
		boolean skipClockNets = true;
		// Iterate through all nets and look for various detection location posibilites
		for (EdifNet net : topCell.getNetList()) {

			// Ignore the following nets for detection locations:
			// - ignore nets specified during the premitigated block tagging
			// - ignore clock nets
			if (rDesc.shouldIgnoreNet(net) || (arch.isClockNet(net) && skipClockNets))
		        continue;

			// Determine the replication type of the driver
			Collection<EdifPortRef> drivers = net.getSourcePortRefs(true, true);
			ReplicationType replicationType = null;
            for (EdifPortRef driver : drivers) {
                ReplicationType driverType = rDesc.getReplicationType(driver);
                if (replicationType == null)
                    replicationType = driverType;
                else {
                    if (replicationType != driverType) {
                        throw new EdifRuntimeException("Unexpected: Net drivers have different ReplicationTypes");
                    }
                }
            }
            
            // skip nets that aren't the replication type we're working on. This only works on
            // one replication type at a time (i.e., per pass)
            if (replicationType.getClass() != userReplicationTypeClass)
            	continue;
            
            
			if (EdifReplicationPropertyReader.isDoNotDetectLocation(net)) {
				// If there is an edif property that says don't detect here, add an antiDetector to the net (usually nothing)
				List<OrganSpecification> detectors = detectionType.antiDetect(net, rDesc);
				if (detectors != null) {
					numSpecs += detectors.size();
					rDesc.addDetectionOrganSpecifications(detectionDomain, net, detectors);
				}
			}
			else if (EdifReplicationPropertyReader.isForceDetectLocation(net) || outputNets.contains(net)) {
				// If there is a force detection properly on the net or if it is one of the output nets,
				// add a forcced detector at this location
				List<OrganSpecification> detectors = detectionType.forceDetect(net, rDesc);
				if (detectors != null) {
					numSpecs += detectors.size();
					rDesc.addDetectionOrganSpecifications(detectionDomain, net, detectors);
				}
			}
			else {
				// Apply default detection at this net. It depends on the detection type. This usually
				// determines whether you want to detect at an upscale or downscale location
				List<OrganSpecification> detectors = detectionType.defaultDetect(net, rDesc);
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
