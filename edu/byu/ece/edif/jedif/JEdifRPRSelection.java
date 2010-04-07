package edu.byu.ece.edif.jedif;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.EdifTypedValue;
import edu.byu.ece.edif.core.IntegerTypedValue;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.flatten.PreservedHierarchyByNames;
import edu.byu.ece.edif.tools.replicate.nmr.CircuitDescription;
import edu.byu.ece.edif.tools.replicate.nmr.EdifReplicationPropertyReader;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationType;
import edu.byu.ece.edif.tools.replicate.wiring.PreMitigatedDummyTrimmer;
import edu.byu.ece.edif.tools.replicate.wiring.PreMitigatedPortGroup;
import edu.byu.ece.edif.util.generate.WeightedModule;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.iob.AbstractIOB;
import edu.byu.ece.edif.util.iob.IOBAnalyzer;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.CircuitDescriptionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifParserCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifRPRSelectionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ReplicationDescriptionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ReplicationTypeCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.TechnologyCommandGroup;

public class JEdifRPRSelection extends EDIFMain {

    protected static PrintStream out;
    protected static PrintStream err;
    protected static JSAPResult result;
    
    public static String TRUE = "true";
    public static String FALSE = "false";
    
    public static void main(String[] args) {

        out = System.out;
        err = System.err;
        
        EXECUTABLE_NAME = "JEdifRPRSelection";
        TOOL_SUMMARY_STRING = "Select instances to replicate based on bit weight properties.";

        printProgramExecutableString(out);

        EdifCommandParser parser = new EdifCommandParser();
        
        parser.addCommands(new JEdifParserCommandGroup());
        parser.addCommands(new JEdifRPRSelectionCommandGroup());
        parser.addCommands(new CircuitDescriptionCommandGroup());
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
        
        EdifCell topCell = env.getTopCell();
        
        TechnologyCommandGroup.getPartFromEDIF(result, env);
        NMRArchitecture arch = TechnologyCommandGroup.getArch(result);
        ReplicationType replicationType = ReplicationTypeCommandGroup.getReplicationType(result, arch);
        CircuitDescription cDesc = CircuitDescriptionCommandGroup.getCircuitDescription(result, env, out);
        
        ReplicationDescription rDesc = new ReplicationDescription();
        
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
            
        
 
        
        int bitWeightThreshold = JEdifRPRSelectionCommandGroup.getBitWeightThreshold(result);
 
        EdifCellInstanceGraph graph = cDesc.getInstanceGraph();
        
        for (Object o : graph.getNodes()) {
            if (o instanceof EdifCellInstance) {
                EdifCellInstance eci = (EdifCellInstance) o;
                Property prop = eci.getProperty(WeightedModule.WEIGHT_PROPERTY);
                if (prop != null) {
                    EdifTypedValue value = prop.getValue();
                    if (value instanceof IntegerTypedValue) {
                        IntegerTypedValue intValue = (IntegerTypedValue) value;
                        int bitWeight = intValue.getIntegerValue();
                        if (bitWeight >= bitWeightThreshold) {
                            rDesc.addInstance(eci, replicationType);
                        }
                    }
                }
                String cellName = eci.getCellType().getName();
                if (cellName.equalsIgnoreCase("VCC") || cellName.equalsIgnoreCase("GND")) {
                    rDesc.addInstance(eci, replicationType);
                }
            }
        }
        
        // look for inputs connected to replicated instances and tag them for replication
        IOBAnalyzer iobAnalyzer = cDesc.getIOBAnalyzer();
        for (EdifPort port : topCell.getInputPorts()) {
            // skip multi-bit ports -- for now we only support bit blasted ports
            if (port.getWidth() > 1) {
                continue;
            }
            EdifSingleBitPort esbp = port.getSingleBitPort(0);
            AbstractIOB iob = iobAnalyzer.getIOB(esbp);
            Collection<EdifCellInstance> iobInstances = iob.getAllInstances();
            List<Object> iobObjects = new ArrayList<Object>();
            iobObjects.add(esbp);
            iobObjects.addAll(iobInstances);
            List<Object> iobSuccessors = new ArrayList<Object>();
            for (Object iobObject : iobObjects) {
                iobSuccessors.addAll(graph.getSuccessors(iobObject));                                
            }
            for (Object iobSuccessor : iobSuccessors) {
                if (iobSuccessor instanceof EdifCellInstance) {
                    EdifCellInstance successorInstance = (EdifCellInstance) iobSuccessor;
                    ReplicationType successorType = rDesc.getReplicationType(successorInstance);
                    if (successorType == replicationType) {
                        rDesc.addPort(port, replicationType);
                        rDesc.addInstances(iobInstances, replicationType);
                    }
                }
            }            
        }
        
        // look for outputs connected to replicated instances and tag them for replication
        for (EdifPort port : topCell.getOutputPorts()) {
            // skip multi-bit ports -- for now we only support bit blasted ports
            if (port.getWidth() > 1) {
                continue;
            }
            EdifSingleBitPort esbp = port.getSingleBitPort(0);
            AbstractIOB iob = iobAnalyzer.getIOB(esbp);
            Collection<EdifCellInstance> iobInstances = iob.getAllInstances();
            List<Object> iobObjects = new ArrayList<Object>();
            iobObjects.add(esbp);
            iobObjects.addAll(iobInstances);
            List<Object> iobPredecessors = new ArrayList<Object>();
            for (Object iobObject : iobObjects) {
                iobPredecessors.addAll(graph.getPredecessors(iobObject));                                
            }
            for (Object iobPredecessor : iobPredecessors) {
                if (iobPredecessor instanceof EdifCellInstance) {
                    EdifCellInstance predecessorInstance = (EdifCellInstance) iobPredecessor;
                    ReplicationType predecessorType = rDesc.getReplicationType(predecessorInstance);
                    if (predecessorType == replicationType) {
                        rDesc.addPort(port, replicationType);
                        rDesc.addInstances(iobInstances, replicationType);
                    }
                }
            }
        }
        
        ReplicationDescriptionCommandGroup.writeReplicationDescription(result, env, rDesc, out);
    }
}
