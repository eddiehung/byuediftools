package edu.byu.ece.edif.jedif;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.tools.replicate.nmr.Organ;
import edu.byu.ece.edif.tools.replicate.nmr.OrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationType;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DetectionDomain;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DetectionOutputSpecification;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifParserCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ReplicationDescriptionCommandGroup;

public class JEdifReplicationQuery extends EDIFMain {

	public static void main(String[] args) {
		PrintStream out = System.out;
		PrintStream err = System.err;
		
		String TAB = "  ";

		EXECUTABLE_NAME = "JEdifReplicationQuery";
		TOOL_SUMMARY_STRING = "Print out information about the replication to be performed";

		printProgramExecutableString(out);

		EdifCommandParser parser = new EdifCommandParser();

		parser.addCommands(new JEdifParserCommandGroup());
		
		// option for input ReplicationDescription file
		parser.addCommands(new ReplicationDescriptionCommandGroup());

		JSAPResult result = parser.parse(args, err);
		if (!result.success())
			System.exit(1);
		
		EdifEnvironment referenceEnv = JEdifParserCommandGroup.getEdifEnvironment(result, out);
		
		ReplicationDescription rdesc = ReplicationDescriptionCommandGroup.getReplicationDescription(result, referenceEnv, out);
		
		// Prepare to display information about replication types and which instances/ports end up in each
		Set<ReplicationType> allReplicationTypes = new LinkedHashSet<ReplicationType>();
		Map<ReplicationType, List<EdifPort>> replicationPorts = new LinkedHashMap<ReplicationType, List<EdifPort>>();
		Map<ReplicationType, List<EdifCellInstance>> replicationInstances = new LinkedHashMap<ReplicationType, List<EdifCellInstance>>();
		Map<EdifPort, ReplicationType> portReplicationMap = rdesc.getPortReplicationMap();
		for (EdifPort port : portReplicationMap.keySet()) {
			ReplicationType rType = portReplicationMap.get(port);
			allReplicationTypes.add(rType);
			List<EdifPort> portList = replicationPorts.get(rType);
			if (portList == null) {
				portList = new ArrayList<EdifPort>();
				replicationPorts.put(rType, portList);
			}
			portList.add(port);
		}
		
		Map<EdifCellInstance, ReplicationType> instanceReplicationMap = rdesc.getInstanceReplicationMap();
		for (EdifCellInstance instance : instanceReplicationMap.keySet()) {
			ReplicationType rType = instanceReplicationMap.get(instance);
			allReplicationTypes.add(rType);
			List<EdifCellInstance> instanceList = replicationInstances.get(rType);
			if (instanceList == null) {
				instanceList = new ArrayList<EdifCellInstance>();
				replicationInstances.put(rType, instanceList);
			}
			instanceList.add(instance);
		}
		
		// Display replication type information
		System.out.println();
		System.out.println("Replication Types:");
		for (ReplicationType rType : allReplicationTypes) {
			System.out.println(TAB + capFirstLetter(rType));
			List<EdifPort> portList = replicationPorts.get(rType);
			List<EdifCellInstance> instanceList = replicationInstances.get(rType);
			if (portList != null) {
				System.out.println(TAB + TAB +"Ports:");
				for (EdifPort port : portList) {
					System.out.println(TAB + TAB + TAB +port);
				}
			}
			if (instanceList != null) {
				System.out.println(TAB + TAB + "Instances:");
				for (EdifCellInstance instance : instanceList) {
					System.out.println(TAB + TAB + TAB + instance);
				}
			}
		}
		
		// Display organ specification information
		Map<EdifNet, Set<OrganSpecification>> organMap = rdesc.getOrganSpecifications();
		Map<Organ, Integer> numKindOrgan = new LinkedHashMap<Organ, Integer>();
		if (organMap.size() > 0) {
			System.out.println();
			System.out.println("Restoring/Detecting Organs (by net):");
			for (EdifNet net : organMap.keySet()) {
				System.out.println(TAB + "Net: " + net);
				Set<OrganSpecification> specs = organMap.get(net);
				for (OrganSpecification os : specs) {
					System.out.println(TAB + TAB + os);
					Integer currentCount = numKindOrgan.get(os.getOrganType());
					if (currentCount == null) {
					    currentCount = 0;
					}
					numKindOrgan.put(os.getOrganType(), currentCount + os.getOrganCount());
				}
			}
			System.out.println();
			System.out.println("Organ Summary:");
			for (Organ organType : numKindOrgan.keySet()) {
			    System.out.println("# of " + organType + ": " + numKindOrgan.get(organType));			    
			}
		}
		

		
		
		// Display detection output information
		Collection<DetectionOutputSpecification> dspecs = rdesc.getDetectionOutputSpecifications();
		if (dspecs != null && dspecs.size() > 0) {
			System.out.println();
			System.out.println("Detection Outputs:");
			for (DetectionOutputSpecification dos : dspecs) {
				String portName = "";
				if (dos.needsPortCreated())
					portName = dos.getPortNameToCreate();
				else
					portName = dos.getOutputConnection().get(0).getSingleBitPort().getParent().getName();
				System.out.println(TAB + "Port Name: " + portName);
				if (dos.needsPortCreated())
					System.out.println(TAB + TAB + "Port will be created");
				else
					System.out.println(TAB + TAB + "Port already exists");
				System.out.println(TAB + TAB + "Detection Type: " + dos.getDetectionType());
				if (dos.shouldInsertOreg())
					System.out.println(TAB + TAB + "Will use an output register clocked with net: " + dos.getClockNetName());
				if (dos.shouldInsertObuf())
					System.out.println(TAB + TAB + "Will use an output buffer");
				Collection<DetectionDomain> domains = dos.getDetectionDomains();
				if (domains != null && domains.size() > 0) {
					System.out.println(TAB + TAB + "Detects on these nets:");
					for (DetectionDomain domain : domains) {
						Collection<OrganSpecification> ospecs = domain.getDetectorSpecifications();
						for (OrganSpecification os : ospecs) {
							System.out.println(TAB + TAB + TAB + os.getEdifNet());
						}
					}
				}
				
			}
		}
		
		out.println();
	}
	
	public static String capFirstLetter(Object obj) {
		String string = obj.toString();
		if (string == null)
			return null;
		if (string.length() == 0)
			return string.toUpperCase();
		return string.substring(0, 1).toUpperCase() + string.substring(1);
			
	}
}
