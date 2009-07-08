package edu.byu.ece.edif.jedif;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.arch.xilinx.XilinxTools;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.replicate.nmr.CircuitDescription;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.OrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;
import edu.byu.ece.edif.tools.replicate.nmr.RestoringOrganSpecification;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.CircuitDescriptionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifParserCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ReplicationDescriptionCommandGroup;

public class JEdifRecoveryDistance extends EDIFMain{

public static void main(String[] args) {
        
        PrintStream out = System.out;
        PrintStream err = System.err;
        
        EXECUTABLE_NAME = "JEdifRecoveryDistance";
        TOOL_SUMMARY_STRING = "Determines recovery distance (in FFs) between voters";
        
        printProgramExecutableString(out);
        
        EdifCommandParser parser = new EdifCommandParser();
        
        // option for input .jedif file
        parser.addCommands(new JEdifParserCommandGroup());
        
        // option for input ReplicationDescription file
        parser.addCommands(new ReplicationDescriptionCommandGroup());
        
        // option for input CircuitAnalysisDescription file
        parser.addCommands(new CircuitDescriptionCommandGroup());
        
        // config file options
        parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));
        
        // log file command group
        parser.addCommands(new LogFileCommandGroup("recovery_distance.log"));
        
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
        
        EdifCellInstanceGraph graph = cDesc.getInstanceGraph();
        
        JEdifRecoveryDistance rt = new JEdifRecoveryDistance(env, rDesc, cDesc);
        
        System.out.println();
        System.out.println(rt.reportPaths());
    }
    
    public JEdifRecoveryDistance(EdifEnvironment env, ReplicationDescription rDesc, CircuitDescription cDesc) {
        _env = env;
        _rDesc = rDesc;
        _cDesc = cDesc;
        findPaths();
    }
    
    protected void findPaths() {
        EdifCell topCell = _env.getTopCell();

        NMRArchitecture arch = _cDesc.getNMRArchitecture();

        EdifCellInstanceGraph graph = _cDesc.getInstanceGraph();

        HashSet<EdifNet> voterNets = new HashSet<EdifNet>();
        for (EdifNet possibleVoterNet : _rDesc.getOrganSpecifications().keySet()) {
            for (OrganSpecification spec : _rDesc.getOrganSpecifications(possibleVoterNet)) {
                if (spec instanceof RestoringOrganSpecification) {
                    voterNets.add(possibleVoterNet);
                    break;
                }
            }
        }

        _voterEdges = new LinkedHashSet<EdifCellInstanceEdge>();
        for (Object edge : graph.getEdges()) {
            if (edge instanceof EdifCellInstanceEdge) {
                EdifCellInstanceEdge ecic = (EdifCellInstanceEdge) edge;
                if (voterNets.contains(ecic.getNet())) {
                    _voterEdges.add(ecic);
                }
            }
        }
        
        _histogram = new HashMap<Integer, Integer>();

        for (EdifCellInstanceEdge voterEdge: _voterEdges) {
            Object sink = voterEdge.getSink();
            if (sink instanceof EdifCellInstance) {
                EdifCellInstance eci = (EdifCellInstance) sink;
                _alreadyVisited = new HashSet<EdifCellInstance>();
                dfs(eci, graph, 0, new Stack<EdifCellInstance>());
            }
        }        
    }
    
    protected void dfs(EdifCellInstance eci, EdifCellInstanceGraph graph, int numFFs, Stack<EdifCellInstance> path) {
        boolean isFF = false;
        if (XilinxTools.isRegisterCell(eci.getCellType())) {
            numFFs += 1;
            isFF = true;
        }
        path.push(eci);
        HashSet<EdifNet> usedNets = new HashSet<EdifNet>(1);
        for (Object edge : graph.getOutputEdges(eci)) {
            if (edge instanceof EdifCellInstanceEdge) {
                EdifCellInstanceEdge ecic = (EdifCellInstanceEdge) edge;
                EdifNet net = ecic.getNet();
                boolean hasVoter = _voterEdges.contains(ecic);
                if (hasVoter) {
                    if (!usedNets.contains(net)) {
                        usedNets.add(net);
                        foundPath3(numFFs, path);
                    }
                }
                else {
                    Object sink = ecic.getSink();
                    if (sink instanceof EdifCellInstance) {
                        EdifCellInstance sinkEci = (EdifCellInstance) sink;
                        if (!_alreadyVisited.contains(sinkEci)) {
                            _alreadyVisited.add(sinkEci);
                            dfs(sinkEci, graph, numFFs, path);
                        }
                    }
                    else if (sink instanceof EdifSingleBitPort) {
                        foundPath3(numFFs, path);
                    }
                }
            }
        }
        if (isFF) {
            numFFs -= 1;
        }
        path.pop();
    }
    
    protected void foundPath3(int numFFs, Stack<EdifCellInstance> path) {
        Integer value = _histogram.get(numFFs);
        if (value == null) {
            _histogram.put(numFFs, 1);
        }
        else {
            _histogram.put(numFFs, value.intValue() + 1);
        }
    }
    
    public String reportPaths() {
        StringBuilder sb = new StringBuilder();
        sb.append("Histogram:\nPath Length\t#\n");
        TreeMap<Integer, Integer> sortedHistogram = new TreeMap(_histogram); // (this makes the output sorted instead of in some random hash order)
        for (Integer numFFs : sortedHistogram.keySet()) {
            Integer value = sortedHistogram.get(numFFs);
            sb.append(numFFs + "\t \t" + value + "\n");            
        }
        return sb.toString();
    }
    
    protected EdifEnvironment _env;
    protected ReplicationDescription _rDesc;
    protected CircuitDescription _cDesc;
    protected Set<EdifCellInstanceEdge> _voterEdges;
    protected Set<EdifCellInstance> _alreadyVisited;
    protected Map<Integer, Integer> _histogram;
}
