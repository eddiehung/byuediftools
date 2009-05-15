package edu.byu.ece.edif.jedif;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.flatten.PreservedHierarchyByNames;
import edu.byu.ece.edif.tools.replicate.nmr.CircuitDescription;
import edu.byu.ece.edif.tools.replicate.nmr.DeviceParser;
import edu.byu.ece.edif.tools.replicate.nmr.DeviceUtilizationTracker;
import edu.byu.ece.edif.tools.replicate.nmr.DuplicateNMRRequestException;
import edu.byu.ece.edif.tools.replicate.nmr.EdifReplicationPropertyReader;
import edu.byu.ece.edif.tools.replicate.nmr.FullNMRSelectionStrategy;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.NMRSelectionStrategy;
import edu.byu.ece.edif.tools.replicate.nmr.NMRUtilities;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationEstimatedStopException;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationException;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationHardStopException;
import edu.byu.ece.edif.tools.replicate.nmr.PartialNMRSelectionStrategy;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationType;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationUtilizationTracker;
import edu.byu.ece.edif.tools.replicate.nmr.UnsupportedResourceTypeException;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxNMRArchitecture;
import edu.byu.ece.edif.tools.replicate.wiring.PreMitigatedDummyTrimmer;
import edu.byu.ece.edif.tools.replicate.wiring.PreMitigatedPortGroup;
import edu.byu.ece.edif.util.graph.EdifCellBadCutGroupings;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollection;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.graph.EdifPortRefEdge;
import edu.byu.ece.edif.util.iob.IOBAnalyzer;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.CircuitDescriptionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifNMRSelectionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifParserCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ReplicationDescriptionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ReplicationTypeCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.TechnologyCommandGroup;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

public class JEdifNMRSelection extends EDIFMain {

    protected static PrintStream out;
    protected static PrintStream err;
    protected static JSAPResult result;
    protected static ReplicationType replicationType;
    
    public static String TRUE = "true";
    public static String FALSE = "false";
    
    public static void main(String[] args) {

        out = System.out;
        err = System.err;
        
        EXECUTABLE_NAME = "JEdifNMRSelection";
        TOOL_SUMMARY_STRING = "Determines which parts of a design will be replicated";

        printProgramExecutableString(out);

        EdifCommandParser parser = new EdifCommandParser();
        
        parser.addCommands(new JEdifParserCommandGroup());
        parser.addCommands(new CircuitDescriptionCommandGroup());
        parser.addCommands(new JEdifNMRSelectionCommandGroup());
        parser.addCommands(new TechnologyCommandGroup());
        parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));
        parser.addCommands(new LogFileCommandGroup("nmr_selection.log"));
        
        // option for replication type (triplication/duplication)
        parser.addCommands(new ReplicationTypeCommandGroup());
        
        // option for ReplicationDescription
        // selection will be continued from a previous pass if this is specified, otherwise a new
        // ReplicationDescription will be created
        parser.addCommands(new ReplicationDescriptionCommandGroup());
        
        result = parser.parse(args, err);
        if (!result.success())
            System.exit(1);
        

        
        LogFileCommandGroup.CreateLog(result);
        out = LogFile.out();
        err = LogFile.err();

        printProgramExecutableString(LogFile.log());
        
        // Step 1: Get EdifEnvironment and hierarchy information from JEdif File
        List<PreservedHierarchyByNames> hierarchyReturn = new ArrayList<PreservedHierarchyByNames>();
        EdifEnvironment env = JEdifParserCommandGroup.getEdifEnvironment(result, out, hierarchyReturn);
        if (env == null) {
            err.println("Invalid .jedif file.");
            System.exit(1);
        }
        if (hierarchyReturn.size() < 1) {
            // Report error and exit
            err.println("ERROR: The design in the .jedif file has not been flattened.");
            System.exit(1);
        }
        PreservedHierarchyByNames hierarchy = hierarchyReturn.iterator().next();
        
        JEdifQuery.printEdifStats(env, out);
        
        out.println("Processing: " + JEdifNMRSelectionCommandGroup.getFactorType(result) + " "
                + JEdifNMRSelectionCommandGroup.getFactorValue(result));
        EdifCell topCell = env.getTopCell();
        
        TechnologyCommandGroup.getPartFromEDIF(result, env);
        NMRArchitecture arch = TechnologyCommandGroup.getArch(result);
        replicationType = ReplicationTypeCommandGroup.getReplicationType(result, arch);
        _override = JEdifNMRSelectionCommandGroup.overrideSelection(result);
        CircuitDescription cDesc = CircuitDescriptionCommandGroup.getCircuitDescription(result, env, out);
        
        ReplicationDescription rDesc = null;
        if (JEdifNMRSelectionCommandGroup.continueSelection(result)) {
        	rDesc = ReplicationDescriptionCommandGroup.getReplicationDescription(result, env, out);
        }
        
        if (rDesc == null)
            rDesc = new ReplicationDescription();

        
        // Make sure port group information has been added to the ReplicationDescription
        // This will be needed for JEdifVoterSelectoin, JEdifDetectionSelection, and JEdifNMR
        if (!rDesc.alreadySetPortGroups()) {
            Collection<PreMitigatedPortGroup> portGroups = EdifReplicationPropertyReader.getPreMitigatedPortGroups(topCell, arch);
            
            // this is necessary because of synthesis tool limitations
            // (you can't generate a circuit that has a pre-mitigated instance with
            //  some ports left completely open--they always get wired to ground. This
            //  bit of code marks these connections so they can be ignored later).
            PreMitigatedDummyTrimmer.markDummyConnectionsToIgnore(topCell, portGroups, cDesc, rDesc);            
            rDesc.setPortGroups(portGroups);
        }
            
        ReplicationDescription newRDesc = nmr(env, topCell, result, cDesc, rDesc, arch, hierarchy);
        
        ReplicationDescriptionCommandGroup.writeReplicationDescription(result, env, newRDesc, out);
        
        out.println();
        
    }
    
    public static ReplicationDescription nmr(EdifEnvironment env, EdifCell topCell, JSAPResult result, CircuitDescription cDesc, ReplicationDescription rDesc, NMRArchitecture arch, PreservedHierarchyByNames hierarchy) {
  
        out.println("Using part " + env.getTopDesign().getProperty("PART").getValue());

        //Step 2: Make resource tracker & add the flattened top cell to it

        DeviceUtilizationTracker duTracker = null;
        
        try {
            duTracker = DeviceParser.createXilinxDeviceUtilizationTracker(topCell, TechnologyCommandGroup
                    .getPart(result), JEdifNMRSelectionCommandGroup.getMergeFactor(result), JEdifNMRSelectionCommandGroup
                    .getOptimizationFactor(result), JEdifNMRSelectionCommandGroup.getFactorValue(result),
                    JEdifNMRSelectionCommandGroup.ignoreHardResourceUtilizationLimits(result),
                    JEdifNMRSelectionCommandGroup.ignoreSoftLogicUtilizationLimit(result), JEdifNMRSelectionCommandGroup.getFactorType(result));
        } catch (OverutilizationException e) {
            String errmsg = new String("ERROR: Original cell " + topCell + " could not fit into specified part "
                    + TechnologyCommandGroup.getPart(result) + "\n." + e);
            err.println(errmsg);
            System.exit(1);
        }
        
        ReplicationUtilizationTracker rTracker = new ReplicationUtilizationTracker(duTracker);
        
        // if there was a previous replication description, add the ReplicationType instance assignments to the tracker
        Map<EdifCellInstance, ReplicationType> instanceReplicationMap = rDesc.getInstanceReplicationMap();
        for (EdifCellInstance eci : instanceReplicationMap.keySet()) {
            ReplicationType repType = instanceReplicationMap.get(eci);
            try {
                rTracker.addToTracker(eci, repType, true); // <-- it shouldn't matter if this boolean is true or false at this point
            } catch (OverutilizationEstimatedStopException e) {
                throw new EdifRuntimeException("Error: previous replication assignments don't fit in current device.");
            } catch (OverutilizationHardStopException e) {
                throw new EdifRuntimeException("Error: previous replication assignments don't fit in current device.");
            } catch (UnsupportedResourceTypeException e) {
                throw new EdifRuntimeException("Error: previous replication assignment has invalid resource type for instance: " + eci);
            } catch (DuplicateNMRRequestException e) {
                throw new EdifRuntimeException("Error: previous replication assignment has duplicate assignment for instance: " + eci);
            } 
        }


        out.println("\nUtilization before Replication: \n" + duTracker);
        
        IOBAnalyzer iobAnalyzer = cDesc.getIOBAnalyzer();
        EdifCellBadCutGroupings badCutGroupings = cDesc.getBadCutGroupings();
        EdifCellInstanceGraph eciConnectivityGraph = cDesc.getInstanceGraph();
        SCCDepthFirstSearch sccDFS = cDesc.getDepthFirstSearch();
        
        // Replicate ports and force replicate/exclude from replication
        _portsToReplicate = replicatePorts(topCell, rTracker, iobAnalyzer, rDesc);
        excludeFromReplication(topCell, rTracker, env, hierarchy);
        forceReplication(topCell, rTracker, env, hierarchy);
        
        // Replicate any half latch constant instances
        // If they are already marked for replication, override them
        // with the new replication type iff it has a higher replication factor
        // (we want the most protection possible for the half-latch constants)
        for (EdifCellInstance eci : topCell.getSubCellList()) {
        	if (EdifReplicationPropertyReader.isHalfLatchConstantInstance(eci)) {
        		ReplicationType rType = rTracker.getInstanceReplicationMap().get(eci);
        		if (rType == null || rType.getReplicationFactor() < replicationType.getReplicationFactor())
					try {
						rTracker.addToTracker(eci, replicationType, true);
					} catch (OverutilizationEstimatedStopException e) {
						out.println("Warning: half-latch constant can't be replicated because there is not enough estimated room in the device.");
					} catch (OverutilizationHardStopException e) {
						out.println("Warning: half-latch constant can't be replicated because there is not enough room in the device.");
					} catch (UnsupportedResourceTypeException e) {
						out.println("Warning: half-latch constant is an unsupported type for replication and will not be replicated.");
						e.printStackTrace();
					} catch (DuplicateNMRRequestException e) {
						// ignore this one
					}
        	}
        }

        // Unify the bad cut groupings
        try {
            unifyBadCutGroups(badCutGroupings, rTracker);
        } catch (IllegalArgumentException e4) {
            out.println(e4.getMessage());
            System.exit(1);
        }

        NMRSelectionStrategy selectionStrategy = null;

        // (if fullNMR): Determine full NMR  
        if (JEdifNMRSelectionCommandGroup.fullNMR(result))
            selectionStrategy = FullNMRSelectionStrategy.getInstance();
        else if (!JEdifNMRSelectionCommandGroup.noPartialNMR(result))
            // (if !fullNMR && !noPartialNMR): determine partial NMR
        	selectionStrategy = PartialNMRSelectionStrategy.getInstance();
        
        if (selectionStrategy != null) {
        	boolean success = selectionStrategy.selectNMR(rTracker, topCell, eciConnectivityGraph, badCutGroupings, sccDFS, arch, out, err, result, replicationType, _override);
        	if (!success)
        		System.exit(1);
        }

        // Print out NMR statistics from the Resource Tracker
        printNMRstats(duTracker, topCell);       
        
        // Add lists of instances/ports to NMR to ReplicationDescription
        rDesc.setInstanceReplicationMap(rTracker.getInstanceReplicationMap());
        rDesc.addPorts(_portsToReplicate, replicationType);
        
        return rDesc;
    }
    
    
    
    /**
	 * @param topCell
	 * @param duTracker
	 */
	protected static void excludeFromReplication(EdifCell topCell, ReplicationUtilizationTracker rTracker, EdifEnvironment env, PreservedHierarchyByNames hierarchy) {
	    Collection<EdifCellInstance> excludeInstances = new ArrayList<EdifCellInstance>();
	    
	    // exlcude all instances of cell types with pre_mitigated property
	    for (EdifCellInstance eci : topCell.getSubCellList()) {
	        if (EdifReplicationPropertyReader.isPreMitigatedInstance(eci)) {
	            excludeInstances.add(eci);
	            LogFile.log().println("Excluding instance " + eci + " from NMR because it is an instance" +
	        		" of a pre-mitigated cell type.");
	        }
	    }
	    
	    if (JEdifNMRSelectionCommandGroup.noNMRi(result)) {
	        for (String instanceName : JEdifNMRSelectionCommandGroup.getNoNMRi(result)) {
	            LogFile.log().println("Excluding instance " + instanceName + " from NMR");
	            // Get Collection of instances
	            Collection<EdifCellInstance> instances = hierarchy.getInstancesWithin(instanceName, topCell);
	            // Give user warning if no match was found
	            if (instances == null || instances.isEmpty())
	                LogFile.log().println("\tWARNING: No match for instance " + instanceName);
	            else
	                excludeInstances.addAll(instances);
	        }
	    }
	    // Get Collection of instances based on cell types to exclude
	    if (JEdifNMRSelectionCommandGroup.noNMRc(result)) {
	        Collection<String> excludeCellTypes = Arrays.asList(JEdifNMRSelectionCommandGroup.getNoNMRc(result));
	
	        for (String excludeCellType : excludeCellTypes) {
	            LogFile.log().println("Excluding cell type " + excludeCellType + " from NMR");
	            // Need to package the excludeCellType String in a Collection
	            Collection<String> excludeCellTypeColl = new ArrayList<String>(1);
	            excludeCellTypeColl.add(excludeCellType);
	            Collection<EdifCellInstance> instances = hierarchy.getInstancesWithinCellTypes(excludeCellTypeColl, topCell);
	            // Give user warning if no match was found
	            if (instances == null || instances.isEmpty())
	                LogFile.log().println("\tWARNING: No match for cell type " + excludeCellType);
	            else
	                excludeInstances.addAll(instances);
	        }
	    }
	
	    // Add all instances from specified clock domains to exclusion list
	    if (JEdifNMRSelectionCommandGroup.noNMRclk(result)) {
	        Collection<String> excludeClockDomains = Arrays.asList(JEdifNMRSelectionCommandGroup
	                .getNoNMRclk(result));
	
	        JEdifClockDomain domainParser = new JEdifClockDomain(env);
	        Map<EdifNet, Set<EdifCellInstance>> clockDomainMap = domainParser.getECIMap();
	        Set<EdifNet> clocks = clockDomainMap.keySet();
	
	        for (String netName : excludeClockDomains) {
	            Collection<EdifCellInstance> instances = new ArrayList<EdifCellInstance>();
	            for (EdifNet net : clocks) {
	                if (net.getName().equals(netName)) {
	                    out.println("Excluding clock domain " + netName + " from NMR.");
	                    instances.addAll(clockDomainMap.get(net));
	                }
	            }
	            if (instances.isEmpty())
	                out.println("\tWARNING: No match for clock domain " + netName);
	            else
	                excludeInstances.addAll(instances);
	        }
	
	    }
	
	    // Add each instance to the exclude list
	    for (EdifCellInstance excludeInstance : excludeInstances) {
	        rTracker.excludeInstance(excludeInstance);
	    }
	}

	/**
	 * Start with all the top-level ports of the original cell, remove ports
	 * marked to not be replicated, and return the remaining set of ports,
	 * which is the set of ports to be replicated.
	 * 
	 * @param cell The original, un-replicated EdifCell
	 * @param repInputs Should inputs be replicated?
	 * @param repOutputs Should outputs be replicated?
	 * @param noReplicate A Collection of String objects of port names that
	 * will not be replicated.
	 * @return a Set of EdifPort objects to be replicated
	 */
	protected static Set<EdifPort> filterPortsToReplicate(EdifCell cell, boolean repInputs, boolean repOutputs,
	        Collection<String> noReplicate, Collection<String> replicate, ReplicationDescription rDesc) {
	
	    Set<EdifPort> ports = new LinkedHashSet<EdifPort>();
	
	    if (!repInputs && !repOutputs && (replicate.size() == 0)) {
	        LogFile.out().println("No Port Replication");
	        return ports; // Return empty Set
	    }
	
	    LogFile.out().println("");
	    LogFile.out().println("Port Replication: see Log for details.");
	    for (EdifPort port : cell.getPortList()) {
	        LogFile.log().print("\t" + port);
	        if (noReplicate.contains(port.getName())) {
	            LogFile.log().println(" : in list to not replicate.");
	            continue;
	        }
	        if (rDesc.isPreMitigatedPort(port)) {
	            LogFile.log().println(" : pre-mitigated -- not replicating.");
	            continue;
	        }
	        if (replicate.contains(port.getName())) {
	            String dir;
	            if (port.isInput())
	                dir = "in";
	            else
	                dir = "out";
	
	            LogFile.log().println(" : replicate " + dir + "put port as requested");
	            ports.add(port);
	            continue;
	        }
	        if (repInputs && port.isInput()) {
	            LogFile.log().println(" : replicate input port");
	            ports.add(port);
	        } else if (repOutputs && port.isOutput()) {
	            LogFile.log().println(" : replicate output port");
	            ports.add(port);
	        } else
	            LogFile.log().println(" : nothing being done");
	    }
	    return ports;
	}

	/**
	 * Forces certain EdifCellInstances and cell types to be replicated based
	 * on the command line arguments provided by the user. Instance names are
	 * case sensitive, cell types are not.
	 * 
	 * @param forceInstanceStrings
	 * @param topCell
	 * @param duTracker
	 */
	protected static void forceReplication(EdifCell topCell, ReplicationUtilizationTracker rTracker, EdifEnvironment env, PreservedHierarchyByNames hierarchy) {
	    Collection<String> forceInstanceStrings = new ArrayList<String>();
	    Collection<EdifCellInstance> forceInstances = new ArrayList<EdifCellInstance>();
	    // Add Strings from command line to forceInstance Collection
	    if (JEdifNMRSelectionCommandGroup.nmrI(result))
	        forceInstanceStrings.addAll(Arrays.asList(JEdifNMRSelectionCommandGroup.getNMRi(result)));
	    // Get EdifCellInstances that correspond to these Strings
	    for (String instanceName : forceInstanceStrings) {
	        out.println("Forcing replication of instance " + instanceName);
	        // Get Collection of instances
	        Collection<EdifCellInstance> instances = hierarchy.getInstancesWithin(instanceName, topCell);
	        // Give user warning if no match was found
	        if (instances == null || instances.isEmpty())
	            out.println("\tWARNING: No match for instance " + instanceName);
	        else
	            forceInstances.addAll(instances);
	    }
	    // Get Collection of instances based on cell types to exclude
	    if (JEdifNMRSelectionCommandGroup.nmrC(result)) {
	        Collection<String> forceCellTypes = Arrays.asList(JEdifNMRSelectionCommandGroup.getNMRc(result));
	        for (String forceCellType : forceCellTypes) {
	            out.println("Forcing replication of cell type " + forceCellType);
	            // Need to package the forceCellType String in a Collection
	            Collection<String> forceCellTypeColl = new ArrayList<String>(1);
	            forceCellTypeColl.add(forceCellType);
	
	            Collection<EdifCellInstance> instances = hierarchy.getInstancesWithinCellTypes(forceCellTypes, topCell);
	            // Give user warning if no match was found
	            if (instances == null || instances.isEmpty())
	                out.println("\tWARNING: No match for cell type " + forceCellType);
	            else
	                forceInstances.addAll(instances);
	        }
	    }
	
	    // Add all instances from specified clock domains to forced replication list
	    if (JEdifNMRSelectionCommandGroup.nmrClk(result)) {
	        Collection<String> forceClockDomains = Arrays.asList(JEdifNMRSelectionCommandGroup.getNMRclk(result));
	
	        JEdifClockDomain domainParser = new JEdifClockDomain(env);
	
	        Map<EdifNet, Set<EdifCellInstance>> clockDomainMap = domainParser.getECIMap();
	        Set<EdifNet> clocks = clockDomainMap.keySet();
	
	        for (String netName : forceClockDomains) {
	            Collection<EdifCellInstance> instances = new ArrayList<EdifCellInstance>();
	            for (EdifNet net : clocks) {
	                if (net.getName().equals(netName)) {
	                    out.println("Forcing replication of clock domain " + netName);
	                    instances.addAll(clockDomainMap.get(net));
	                }
	            }
	            if (instances.isEmpty())
	                out.println("\tWARNING: No match for clock domain " + netName);
	            else
	                forceInstances.addAll(instances);
	        }
	
	    }
	
	    // Replicate each of these EdifCellInstances
	    for (EdifCellInstance forceInstance : forceInstances) {
	        try {
	        	rTracker.addToTracker(forceInstance, replicationType, _override);
	        } catch (DuplicateNMRRequestException e1) {
	            // Already NMR'd
	        } catch (OverutilizationEstimatedStopException e2) {
	            /*
	             * This instance will not fit in the device. (Device is full)
	             */
	            out.println("WARNING: Could not add instance " + forceInstance + ". Device full.");
	        } catch (OverutilizationHardStopException e3) {
	            // There are no more resources available for this instance.
	            out.println("WARNING: Could not add instance " + forceInstance + ". No resources of type "
	                    + forceInstance.getType() + " available.");
	        }
	    }
	}

	/**
     * Return a NMRArchitecture object for the specified technology.
     * 
     * @param technologyString The specified technology
     * @return A NMRArchtecture object
     */
    protected static XilinxNMRArchitecture getArchitecture(String technologyString) {
        if (technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX)
                || technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX2PRO)
                || technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX2)
                || technologyString.equalsIgnoreCase(NMRUtilities.VIRTEX4))
            return new XilinxNMRArchitecture();

        throw new EdifRuntimeException("Invalid Technology: " + technologyString + ". Valid technologies include "
                + NMRUtilities.VIRTEX + ", " + NMRUtilities.VIRTEX2 + ", and " + NMRUtilities.VIRTEX2PRO + ", and "
                + NMRUtilities.VIRTEX4 + ".");
    }
    
    /**
	 * Prints out useful statistics about the projected NMR to be done
	 */
	protected static void printNMRstats(DeviceUtilizationTracker duTracker, EdifCell flatCell) {
	    //TODO: we can probably print more useful stats than this
	    //how to best do it with only the resource tracker and flat cell?
	    out.println();
	    out.println("Estimated utilization (after replication, before adding voters):");
	    out.println(duTracker.toString());
	}

	/**
     * Marks specified ports for replication. IOB instances will be force
     * replicated or force not replicated based on the replication status of
     * their associated ports.
     * 
     * @param duTracker The device utilization tracker
     * @param flatCell The flattened EDIFCell object
     * @return Set of all EdifPorts in the design to be replicated.
     */
    protected static Set<EdifPort> replicatePorts(EdifCell flatCell, ReplicationUtilizationTracker rTracker,
            IOBAnalyzer iobAnalyzer, ReplicationDescription rDesc) {
        // Determine which ports to replicate
        // Identify BOARD specific ports to replicate
        _noReplicatePorts = new LinkedHashSet<String>();
        //for (String port : NMRUtilities.SLAAC1V_PORTS_NOT_TO_REPLICATE)
        //    _noReplicatePorts.add(port);

        _noReplicatePorts.addAll(Arrays.asList(JEdifNMRSelectionCommandGroup.getNoNMRp(result)));
        // if there are any previously marked ports and there is no --override parameter,
        // we can't override them
        if (!_override) {
            for (EdifPort port : rDesc.getPortReplicationMap().keySet()) {
                _noReplicatePorts.add(port.getName());
            }
        }

        // Filter the set of ports to replicate
        LogFile.out().println("Replicating Ports:");
        _portsToReplicate = filterPortsToReplicate(flatCell, JEdifNMRSelectionCommandGroup.nmrInports(result),
                JEdifNMRSelectionCommandGroup.nmrOutports(result), _noReplicatePorts,
                JEdifNMRSelectionCommandGroup.nmrPorts(result), rDesc);
        // Create list of ports NOT to replicate
        _portsNotToReplicate = flatCell.getPortList();
        _portsNotToReplicate.removeAll(_portsToReplicate);

        // Add ports for replication (with associated IBUF/OBUF instances)
        // Use the IOB Analyzer to find the BUFs and other IOB instances
        Set<EdifCellInstance> _iobInstancesToReplicate = new LinkedHashSet<EdifCellInstance>();
        for (EdifPort port : _portsToReplicate) {
            _iobInstancesToReplicate.addAll(iobAnalyzer.getIOBInstances(port.getSingleBitPortList()));
        }
        
        // Mark IO instances associated with port group ports for replication with appropriate replication type
        for (EdifPort port : flatCell.getPortList()) {
            PreMitigatedPortGroup portGroup = rDesc.getPortGroup(port);
            if (portGroup != null) {
                if (portGroup.getFirstPort() != port)
                    continue;
                Collection<EdifCellInstance> iobInstances = iobAnalyzer.getIOBInstances(port.getSingleBitPortList());
                if (iobInstances != null) {
                    try {
                        rTracker.addToTrackerAtomic(iobInstances, portGroup.getReplicationType(), false);
                    } catch (OverutilizationEstimatedStopException e) {
                        // DeviceUtilizationTracker says to stop adding instances for nmr
                        out.println("WARNING: Device full when adding Ports. Should not get here. " + e);
                    } catch (OverutilizationHardStopException e) {
                        out.println("ERROR: Could not replicate IO instances associated with port: " + port + " due to resource constraints.");
                        System.exit(1);
                    } catch (DuplicateNMRRequestException e) {
                        // Ignore -- already NMR'd
                    }
                }
            }
        }
        

        try {
        	rTracker.addToTrackerAtomic(_iobInstancesToReplicate, replicationType, _override);
        } catch (DuplicateNMRRequestException e1) {
            // Already NMR'd
            out.println("WARNING: Duplicate NMR Port request. Should not get here: " + e1);
        } catch (OverutilizationEstimatedStopException e2) {
            // DeviceUtilizationTracker says to stop adding instances for nmr
            out.println("WARNING: Device full when adding Ports. Should not get here. " + e2);
        } catch (OverutilizationHardStopException e3) {
            // Hit some hard limit.
            out.println("ERROR: Could not replicate one or more of " + _portsToReplicate.size() + " top-level ports"
                    + " due to IOB resource constraints: " + _portsToReplicate);
            System.exit(1);
        }

        // Register the port IOB instances to skip with the DeviceUtilizationTracker
        Set<EdifCellInstance> _iobInstancesNotToReplicate = new LinkedHashSet<EdifCellInstance>();
        for (EdifPort port : _portsNotToReplicate) {
            LogFile.log().println("Not replicating port:" + port);
            _iobInstancesNotToReplicate.addAll(iobAnalyzer.getIOBInstances(port.getSingleBitPortList()));
        }
        for (EdifCellInstance eci : _iobInstancesNotToReplicate) {
        	rTracker.excludeInstance(eci);
            LogFile.log().println("Excluding instance from iob register packing: " + eci);
        }
        for (EdifCellInstance eci : _iobInstancesToReplicate) {
            LogFile.log().println(" Including instance for iob register packing: " + eci);
        }
        return _portsToReplicate;
    }
    
    
	/**
     * Makes sure that the ECIs within a BadCutGroup all have the same
     * replication status.
     * 
     * @author Derrick Gibelyou
     * @param badCutGroupings
     * @param duTracker
     * @throws DuplicateNMRRequestException
     */
    protected static void unifyBadCutGroups(EdifCellBadCutGroupings badCutGroupings, ReplicationUtilizationTracker rTracker)
            throws IllegalArgumentException {
        /*
         * make sure that the badcut groups all have the same replication
         * status
         */
        Iterator<EdifCellInstanceCollection> bc_it = badCutGroupings.getInstanceGroups().iterator();
        EdifCellInstanceCollection badCuteci;
        Collection<EdifCellInstance> rep_eci = rTracker.getInstancesWithReplicationType(replicationType);
        // walk through all the collections.
        // if there is more than one cell in the collection make sure they all have the 
        // same replication status.
        while (bc_it.hasNext()) {
            badCuteci = bc_it.next();
            int i = badCuteci.size();
            if (i > 1) {
                Collection<EdifCellInstance> excluded = new ArrayList<EdifCellInstance>();
                Collection<EdifCellInstance> included = new ArrayList<EdifCellInstance>();
                Iterator<EdifCellInstance> eci_it = badCuteci.iterator();
                while (eci_it.hasNext()) {
                    EdifCellInstance eci = eci_it.next();
                    if (rTracker.isExcluded(eci)) {
                    	excluded.add(eci);
                    }
                    if (rep_eci.contains(eci)) {
                        included.add(eci);
                    }
                }
                //user asked for conflicting replication status in same bad cut group
                if (!excluded.isEmpty() && !included.isEmpty()) {
                    Iterator<EdifCellInstance> it = excluded.iterator();
                    String error = "\nExcluded Cell(s): \n";
                    while (it.hasNext()) {
                        error = error + "\t" + it.next().toString() + "\n";
                    }

                    it = included.iterator();
                    error += "\nIncluded Cell(s): \n";
                    while (it.hasNext()) {
                        error = error + "\t" + it.next().toString() + "\n";
                    }

                    throw new IllegalArgumentException("\n\nCan't force replication and "
                            + "force exclusion in same slice:" + error);
                }
                //exclude the whole group
                if (!excluded.isEmpty()) {
                    LogFile.out().println("Excluding all cells in bad cut group. See log for details");
                    // Previously-excluded
                    LogFile.log().println("User requested exclusion of instances: " + excluded);
                    // Newly-added:
                    Collection<EdifCellInstance> newExclusions = new ArrayList<EdifCellInstance>(badCuteci);
                    newExclusions.removeAll(excluded);
                    LogFile.log().println("The following instances will also be excluded: " + newExclusions);
                    eci_it = badCuteci.iterator();
                    while (eci_it.hasNext()) {
                        EdifCellInstance eci = eci_it.next();
                        rTracker.excludeInstance(eci);
                    }
                }
                //include the whole group
                if (!included.isEmpty()) {
                    LogFile.out().println("Including all cells in bad cut " + "group. See log for details");
                    LogFile.log().println(included.toString());
                    eci_it = badCuteci.iterator();
                    EdifCellInstance forceInstance;
                    while (eci_it.hasNext()) {
                        forceInstance = eci_it.next();
                        try {
                        	rTracker.addToTracker(forceInstance, replicationType, _override);
                        } catch (DuplicateNMRRequestException e1) {
                            // Already NMR'd
                        } catch (OverutilizationEstimatedStopException e2) {
                            /*
                             * This instance will not fit in the device. (Device
                             * is full)
                             */
                            LogFile.out()
                                    .println("WARNING: Could not add instance " + forceInstance + ". Device full.");
                        } catch (OverutilizationHardStopException e3) {
                            // There are no more resources available for this instance.
                            LogFile.out().println(
                                    "WARNING: Could not add instance " + forceInstance + ". No resources of type "
                                            + forceInstance.getType() + " available.");
                        }
                    }// end iteration loop
                }// end include whole group

            }//end if( more than one in group)
        }//end top while loop
    }
    
    /**
     * Set of String names of ports that should <i>not</i> be replicated
     */
    protected static Set<String> _noReplicatePorts;

    /**
     * Set of EdifPort objects to be replicated.
     */
    protected static Set<EdifPort> _portsToReplicate;

    protected static boolean _override;
    
    /**
     * Collection of EdifPort objects to <i>not</i> be replicated
     */
    protected static Collection<EdifPort> _portsNotToReplicate;

    protected static PrintStream debug;

    protected static List<EdifPortRefEdge> iobFeedbackEdges;
    
}
