/*
 * Identify clock domains and classify nets into one of those domains.
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
package edu.byu.ece.edif.util.clockdomain;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.arch.xilinx.XilinxTools;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifException;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCellInstance;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxResourceMapper;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.iob.AbstractIOBAnalyzer;
import edu.byu.ece.edif.util.iob.XilinxVirtexIOBAnalyzer;
import edu.byu.ece.edif.util.jsap.ClockDomainCommandParser;
import edu.byu.ece.edif.util.parse.ParseException;
import edu.byu.ece.graph.AbstractGraphToDotty;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.dfs.DepthFirstTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * This class is used to identify clock domains in a flattened EDIF design and
 * then classify nets into one of those domains. Once nets are classified,
 * EdifCellInstances can also be classified into those domains.
 * 
 * @author Kevin Lundgreen
 */

public class ClockDomainParser {

    /**
     * A map whose keys are EdifNets that have been classified as clocks. The
     * values in the map are sets of EdifNets that fall into the clock domains.
     */
    protected Map<EdifNet, Set<EdifNet>> _clkToNetMap;

    /**
     * A map whose keys are EdifNets that have been classified as clocks. The
     * values in the map are sets of EdifCellInstances that fall into the clock
     * domains.
     */
    protected Map<EdifNet, Set<EdifCellInstance>> _clkToECIMap;

    /**
     * A set of EdifNets that do not fall into any of the classified domains.
     */
    protected Set<EdifNet> _noClockNets;

    /**
     * A set of EdifCellInstances that do not fall into any of the classified
     * domains.
     */
    protected Set<EdifCellInstance> _noClockECIs;

    /**
     * A graph structure used for efficiently traversing the EDIF design.
     */
    protected EdifCellInstanceGraph _ecic;

    /**
     * An EdifEnvironment used to store the environment of the flattened design.
     */
    protected EdifEnvironment _top;

    /**
     * ClockDomainCommandParser used to parse the command-line arguments of
     * ClockDomainArchitectureImpl.
     */
    protected static ClockDomainCommandParser _parser;

    /**
     * JSAPResult used to access the command-line arguments after being parsed.
     */
    protected static JSAPResult _result;

    protected static final String BAR = "-----------------------------------";

    protected static final String ALL = "all";

    protected static final String RAM_PREFIX = "ramb4";

    private static final Set<String> _resetPortNames;
    static {
        _resetPortNames = new TreeSet<String>();
        _resetPortNames.add("clr");
        _resetPortNames.add("pre");
    }

    /**
     * Construct a ClockDomainArchitectureImpl object and initialize for clock
     * domain analysis.
     * 
     * @param flatCell The FlattenedEdifCell to analyze
     * @throws InvalidEdifNameException
     */
    public ClockDomainParser(FlattenedEdifCell flatCell) throws InvalidEdifNameException {
        init(flatCell);

        System.out.print("Generating EdifCellInstanceGraph Graph...");
        _ecic = new EdifCellInstanceGraph(_top.getTopCell(), true);
        System.out.println("done");
    }

    /**
     * Construct a ClockDomainArchitectureImpl object and initialize for clock
     * domain analysis.
     * 
     * @param flatCell The FlattenedEdifCell to analyze
     * @param ecic The previously generated EdifCellInstanceGraph graph to avoid
     * having to do it again
     * @throws InvalidEdifNameException
     */
    public ClockDomainParser(FlattenedEdifCell flatCell, EdifCellInstanceGraph ecic) throws InvalidEdifNameException {
        init(flatCell);
        _ecic = ecic;
    }

    /**
     * Construct a ClockDomainArchitectureImpl object and parse and flatten the
     * EDIF design to be used for clock domain analysis
     * 
     * @param fileName The name of the EDIF file that will be parsed and
     * flattened.
     * @throws EdifException
     * @throws ParseException
     * @throws FileNotFoundException
     */
    public ClockDomainParser(String fileName, String[] dirs, String[] files) throws EdifException, ParseException,
            FileNotFoundException {
        EdifLibrary xilinxLib = edu.byu.ece.edif.arch.xilinx.XilinxLibrary.library;
        if (dirs == null)
            dirs = new String[0];
        if (files == null)
            files = new String[0];

        EdifEnvironment edif_file = edu.byu.ece.edif.util.merge.EdifMergeParser.parseAndMergeEdif(fileName, Arrays
                .asList(dirs), Arrays.asList(files), xilinxLib);
        init(new FlattenedEdifCell(edif_file.getTopCell()));

        System.out.print("Generating EdifCellInstanceGraph Graph...");
        _ecic = new EdifCellInstanceGraph(_top.getTopCell(), true);
        System.out.println("done");
    }

    /**
     * Initialize the ClockDomainArchitectureImpl object by removing the
     * original cell from the flattened cell's library. Also initialize the
     * necessary data structures.
     * 
     * @param flatCell The FlattenedEdifCell to analyze.
     * @throws InvalidEdifNameException
     */
    private void init(FlattenedEdifCell flatCell) throws InvalidEdifNameException {
        EdifCell cell = flatCell.getOriginalCell();
        _top = flatCell.getLibrary().getLibraryManager().getEdifEnvironment();
        EdifDesign new_design = new EdifDesign("ROOT");
        new_design.setTopCellInstance(new EdifCellInstance("flat_" + cell.getName(), null, flatCell));
        _top.setTopDesign(new_design);
        cell.getLibrary().deleteCell(cell, true);
        _top.getLibraryManager().pruneNonReferencedCells();

        //try{top.toEdif(new EdifPrintWriter(new FileOutputStream("flat.edf")));}
        //catch(Exception e){}
    }

    /**
     * Return true if the EdifCell ec is sequential (has a clock port).
     * 
     * @param cell The name of the cell to check
     * @return True if this cell is sequential
     */
    private boolean isSequential(EdifCell cell) {
        String str = cell.getName().toLowerCase();
        if (str.startsWith("fd"))
            return true;
        if (str.startsWith("ram"))
            return true;
        if (str.startsWith("clkdll"))
            return true;
        if (str.startsWith("dcm"))
            return true;
        if (str.startsWith("sr"))
            return true;
        return false;
    }

    /**
     * Return true if the EdifCell ec has an asynchronous reset/preset.
     * 
     * @param cell The name of the cell to check
     * @return True if this cell has an asynchronous reset/preset
     */
    private boolean hasAsynchronousReset(EdifCell cell) {
        String str = cell.getName().toLowerCase();

        if (str.startsWith("fdc"))
            return true;
        if (str.startsWith("fddrcpe"))
            return true;
        if (str.startsWith("fdp"))
            return true;
        return false;
    }

    /**
     * Return true if the EdifCell ec is BUFG, DLL, or DCM
     * 
     * @param cell The name of the cell to check
     * @return True if this cell is BUFG, DLL, or DCM
     */
    private boolean isClockDriver(EdifCell cell) {
        String str = cell.getName().toLowerCase();
        if (str.startsWith("bufg"))
            return true;
        if (str.startsWith("dcm"))
            return true;
        if (str.contains("dll"))
            return true;
        return false;
    }

    /**
     * Iterate through the design and classify nets into a clock domain. First,
     * clock domains are identified as those nets that drive clock ports of
     * sequential cells. Once clock domains have been identified, sequential
     * cells and their children (up to another sequential cell) are added to
     * that domain.
     * 
     * @return A map whose key is the EdifNet of the clock domain and whose
     * value is a set of EdifNets that belong to that domain. The sets of
     * EdifNets are not necessarily mutually exclusive.
     */
    protected Map<EdifNet, Set<EdifNet>> classifyNets() {
        _noClockNets = new LinkedHashSet<EdifNet>(_top.getTopCell().getNetList());
        _clkToNetMap = new LinkedHashMap<EdifNet, Set<EdifNet>>();
        Set<EdifNet> clockNets = new LinkedHashSet<EdifNet>();
        for (EdifCellInstance eci : _top.getTopCell().getSubCellList()) {
            if (isSequential(eci.getCellType())) {
                for (EdifPortRef epr : _ecic.getEPRsWhichReferenceInputPortsOfECI(eci)) {
                    if (XilinxTools.isClockPort(epr.getSingleBitPort())) {
                        // The following is for a special case which
                        // doesn't allow GNDs to drive a clock net
                        Iterator<EdifPortRef> it = epr.getNet().getOutputPortRefs().iterator();
                        if (!it.hasNext() || !it.next().getCellInstance().getType().toLowerCase().equals("gnd"))
                            clockNets.add(epr.getNet());
                    }
                }
            }
        }
        for (EdifNet n : clockNets) {
            _clkToNetMap.put(n, new LinkedHashSet<EdifNet>());
            for (EdifPortRef epr : n.getInputPortRefs()) {
                // Only continue if this epr corresponds to a clock port
                if (XilinxTools.isClockPort(epr.getSingleBitPort())) {
                    EdifCellInstance eci = epr.getCellInstance();
                    Queue<EdifNet> q = new ConcurrentLinkedQueue<EdifNet>();
                    Set<EdifNet> classifiedNets = new LinkedHashSet<EdifNet>();
                    // Special Handling for dual-port BRAMs
                    if (eci != null && eci.getType().toLowerCase().contains(RAM_PREFIX)) {
                        for (EdifPortRef myEpr : _ecic.getEPRsWhichReferenceInputPortsOfECI(eci)) {
                            if (myEpr.getPort().getName().toLowerCase().contains("clk")) {
                                if (myEpr.getNet().equals(n)) {
                                    String portName = "a";
                                    if (myEpr.getPort().getName().toLowerCase().contains("b"))
                                        portName = "b";
                                    for (EdifPortRef outRefs : _ecic.getEPRsWhichReferenceOutputPortsOfECI(eci)) {
                                        if (outRefs.getPort().getName().toLowerCase().contains(portName)
                                                && !classifiedNets.contains(outRefs.getNet())) {
                                            q.add(outRefs.getNet());
                                            classifiedNets.add(outRefs.getNet());
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        for (EdifPortRef myEpr : _ecic.getEPRsWhichReferenceOutputPortsOfECI(eci)) {
                            if (!classifiedNets.contains(myEpr.getNet())) {
                                q.add(myEpr.getNet());
                                classifiedNets.add(myEpr.getNet());
                            }
                        }
                    }
                    while (!q.isEmpty()) {
                        EdifNet myNet = q.poll();
                        classifiedNets.add(myNet);
                        for (EdifPortRef myEpr : myNet.getInputPortRefs()) {
                            for (EdifPortRef myEpr2 : _ecic.getEPRsWhichReferenceOutputPortsOfECI(myEpr
                                    .getCellInstance())) {
                                if (!classifiedNets.contains(myEpr2.getNet())
                                        && !isSequential(myEpr2.getCellInstance().getCellType())) {
                                    q.add(myEpr2.getNet());
                                    classifiedNets.add(myEpr2.getNet());
                                }
                            }
                        }
                    }
                    _clkToNetMap.get(n).addAll(classifiedNets);
                    _noClockNets.removeAll(classifiedNets);
                }
            }
        }
        return new LinkedHashMap<EdifNet, Set<EdifNet>>(_clkToNetMap);
    }

    /**
     * Iterate through the design and classify EdifCellInstances into a clock
     * domain. Based on the net domains which should have been previously found,
     * non-sequential cells, like LUTs, belong to the domains of their input
     * nets, and sequential cells belong to the domain of the net driving their
     * clock port(s).
     * 
     * @return A map whose key is the EdifNet of the clock domain and whose
     * value is a set of EdifCellInstances that belong to that domain. The sets
     * of EdifCellInstances are not necessarily mutually exclusive.
     */
    protected Map<EdifNet, Set<EdifCellInstance>> classifyECIs() {
        _noClockECIs = new LinkedHashSet<EdifCellInstance>(_top.getTopCell().getSubCellList());
        _clkToECIMap = new LinkedHashMap<EdifNet, Set<EdifCellInstance>>();
        for (EdifNet n : _clkToNetMap.keySet())
            _clkToECIMap.put(n, new LinkedHashSet<EdifCellInstance>());
        for (EdifCellInstance eci : _top.getTopCell().getSubCellList()) {
            for (EdifPortRef epr : _ecic.getEPRsWhichReferenceInputPortsOfECI(eci)) {
                // if a cell is sequential, it falls into the domain(s)
                // of the net(s) driving its clock port(s)
                if (isSequential(eci.getCellType())) {
                    if (XilinxTools.isClockPort(epr.getSingleBitPort())) {
                        if (_clkToECIMap.get(epr.getNet()) != null) {
                            _clkToECIMap.get(epr.getNet()).add(eci);
                            _noClockECIs.remove(eci);
                        }
                    }
                } else {
                    for (EdifNet net : _clkToNetMap.keySet()) {
                        if (_clkToNetMap.get(net).contains(epr.getNet())) {
                            _clkToECIMap.get(net).add(eci);
                            _noClockECIs.remove(eci);
                        }
                    }
                }
            }
        }
        return _clkToECIMap;
    }

    /**
     * Perform SCC decomposition on the EdifCellInstanceGraph graph.
     * 
     * @param noIOBFB Boolean value indicating whether or not to exclude IOBs as
     * feedback paths.
     * @return SCCDepthFirstSearch data structure
     */
    protected SCCDepthFirstSearch doSCCAnalysis(boolean noIOBFB) {
        SCCDepthFirstSearch scc = new SCCDepthFirstSearch(_ecic);

        if (noIOBFB) {
            AbstractIOBAnalyzer iobAnalyzer = new XilinxVirtexIOBAnalyzer((FlattenedEdifCell) _top.getTopCell(),
                    _ecic);
            Collection<EdifCellInstanceEdge> possibleIOBFeedbackEdges = iobAnalyzer.getIOBFeedbackEdges();

            // Find the possible feedback edges that are contained in the SCCs
            //   (i.e. those that are contained in feedback)
            Collection<Edge> iobFeedbackEdges = new ArrayList<Edge>();
            Collection<DepthFirstTree> sccs = scc.getTrees();
            // Collect all feedback edges into a single Collection.
            //   Use a HashSet because we are doing multiple look-ups in a large Set
            Collection<Edge> edgesInFeedback = new LinkedHashSet<Edge>();
            for (DepthFirstTree scc1 : sccs) {
                edgesInFeedback.addAll(scc1.getEdges());
            }
            // Collect all matching "possible" IOB feedback edges
            for (EdifCellInstanceEdge possibleIOBFeedbackEdge : possibleIOBFeedbackEdges) {
                if (edgesInFeedback.contains(possibleIOBFeedbackEdge))
                    iobFeedbackEdges.add(possibleIOBFeedbackEdge);
            }
            // If there are any IOB feedback edges:
            // 1. Report to user (warning)
            // 2. Remove these edges from the graph and recompute the SCCs
            //    (If the user has chosen this option)
            if (iobFeedbackEdges.size() > 0) {
                System.out.println("");
                System.out.println("The following IOBs were found in feedback structures: "
                        + iobAnalyzer.getFeedbackIOBs());
                System.out.println("\tThese IOBs will be excluded from feedback analysis.");

                _ecic.removeEdges(iobFeedbackEdges);
                scc = new SCCDepthFirstSearch(_ecic);
            }
        }
        return scc;
    }

    /**
     * Find all clock crossing in the design. This is found by iterating over
     * all EdifCellInstances in the design. Clock crossings occur when a
     * sequential cell's inputs are in a different domain than its own.
     * 
     * @return A map whose keys are Strings representing the domain crossing
     * (e.g. "domainA to domainB", and whose values are Sets of Strings
     * describing the clock crossing.
     */
    protected Map<String, Set<String>> getClockCrossings() {
        Map<String, Set<String>> domainCrossMap = new TreeMap<String, Set<String>>();
        // Iterate through all EdifCellInstances.  If any of the nets driving
        // the ECIs are in a different domain than the ECI, we have a domain crossing
        for (EdifCellInstance eci : _top.getTopCell().getSubCellList()) {
            EdifNet clock = null, clockA = null, clockB = null;
            if (isSequential(eci.getCellType())) {
                for (EdifPortRef epr : _ecic.getEPRsWhichReferenceInputPortsOfECI(eci)) {
                    if (XilinxTools.isClockPort(epr.getSingleBitPort())) {
                        if (XilinxResourceMapper.getResourceType(eci).equals("BRAM")) {
                            if (epr.getPort().getName().toLowerCase().contains("clka"))
                                clockA = epr.getNet();
                            else if (epr.getPort().getName().toLowerCase().contains("clkb"))
                                clockB = epr.getNet();
                            else
                                clock = epr.getNet();
                        } else
                            clock = epr.getNet();
                    }
                }
                for (EdifPortRef epr : _ecic.getEPRsWhichReferenceInputPortsOfECI(eci)) {
                    if (!XilinxTools.isClockPort(epr.getSingleBitPort())) {
                        String portName = epr.getPort().getName().toLowerCase();
                        int last = portName.indexOf("_");
                        if (last > 0)
                            portName = portName.substring(0, last);
                        Set<String> driverSet = new TreeSet<String>();
                        for (Object o : _ecic.getPredecessors(eci, epr)) {
                            driverSet.add(((FlattenedEdifCellInstance) o).getHierarchicalEdifName() + " ("
                                    + ((FlattenedEdifCellInstance) o).getType() + ")");
                        }
                        String value = ((FlattenedEdifCellInstance) eci).getHierarchicalEdifName() + "("
                                + eci.getCellType() + ") [" + portName + "] Net:" + epr.getNet().getName() + " Driver:"
                                + driverSet;
                        String key = "";
                        if (XilinxResourceMapper.getResourceType(eci).equals("BRAM")) {
                            EdifNet clk = null;
                            if (portName.endsWith("a"))
                                clk = clockA;
                            else if (portName.endsWith("b"))
                                clk = clockB;
                            else
                                clk = clock;
                            for (EdifNet clockNet : _clkToNetMap.keySet()) {
                                if (_clkToNetMap.get(clockNet).contains(epr.getNet()) && clockNet != clk) {
                                    key = clockNet + " to " + clk;
                                }
                            }
                        } else {
                            for (EdifNet clockNet : _clkToNetMap.keySet()) {
                                if (_clkToNetMap.get(clockNet).contains(epr.getNet()) && clockNet != clock) {
                                    key = clockNet + " to " + clock;
                                }
                            }
                        }
                        if (key != "") {
                            key = key.toLowerCase();
                            if (!domainCrossMap.containsKey(key))
                                domainCrossMap.put(key, new TreeSet<String>());
                            domainCrossMap.get(key).add(value);
                        }
                    }
                }
            }
        }
        return domainCrossMap;
    }

    /**
     * Find all asynchronous resets/presets in the design.
     * 
     * @return A map whose keys are nets that drive asynchronous reset ports and
     * whose values are Sets of EdifCellInstances that have asynchronous
     * resets/presets.
     */
    protected Map<EdifNet, Set<EdifCellInstance>> getAsynchronousResets() {
        Map<EdifNet, Set<EdifCellInstance>> retVal = new LinkedHashMap<EdifNet, Set<EdifCellInstance>>();

        for (EdifCellInstance eci : _top.getTopCell().getSubCellList()) {
            if (hasAsynchronousReset(eci.getCellType())) {
                for (EdifPortRef epr : _ecic.getEPRsWhichReferenceInputPortsOfECI(eci)) {// eci.getInputEPRs()) {
                    String name = epr.getPort().getName().toLowerCase();
                    if (_resetPortNames.contains(name)) {
                        EdifNet net = epr.getNet();
                        if (!retVal.containsKey(net))
                            retVal.put(net, new LinkedHashSet<EdifCellInstance>());
                        retVal.get(net).add(eci);
                    }
                }
            }
        }

        return retVal;
    }

    /**
     * Output summary of SCC to a printstream
     * 
     * @param scc The SCC to analyze
     * @param output The PrintStream to write the summary to
     */
    protected void analyzeSCCs(SCCDepthFirstSearch scc, PrintStream output) {
        output.println("Total sccs in design: " + scc.getTopologicallySortedTreeList().size());

        List<DepthFirstTree> list = scc.getTopologicallySortedTreeList();

        for (DepthFirstTree tree : list) {
            // Keep track of all clocks in SCC
            HashSet<EdifNet> clkSet = new LinkedHashSet<EdifNet>();
            for (Object o : tree.getNodes()) {
                EdifCellInstance eci = null;
                try {
                    eci = (EdifCellInstance) o;
                }
                // Some objects are EdifPorts and will be ignored
                catch (ClassCastException e) {
                    continue;
                }
                for (EdifNet n : _clkToECIMap.keySet()) {
                    if (_clkToECIMap.get(n).contains(eci))
                        clkSet.add(n);
                }
            }
            output.println("  SCC(" + tree.getNodes().size() + ") " + clkSet);
        }
    }

    /**
     * @return a String of the version of this class
     */
    public static String getVersionInfo() {
        return "ClockDomainArchitectureImpl version 0.1";
    }

    /**
     * Public method to return _clkToECIMap after classifying the nets and ECIs.
     * 
     * @return A map whose key is the EdifNet of the clock domain and whose
     * value is a set of EdifCellInstances that belong to that domain. The sets
     * of EdifCellInstances are not necessarily mutually exclusive.
     */
    public Map<EdifNet, Set<EdifCellInstance>> getECIMap() {
        classifyNets();
        classifyECIs();
        return _clkToECIMap;
    }

    public static void main(String[] args) throws ParseException, FileNotFoundException, EdifException {
        _parser = new ClockDomainCommandParser();
        _result = _parser.parse(args);

        ClockDomainParser cda = new ClockDomainParser(_result.getString(ClockDomainCommandParser.INPUT_FILE), _result
                .getStringArray(ClockDomainCommandParser.DIR), _result.getStringArray(ClockDomainCommandParser.FILE));
        PrintStream output = System.out;
        if (_result.contains(ClockDomainCommandParser.OUTPUT_FILE))
            output = new PrintStream(_result.getString(ClockDomainCommandParser.OUTPUT_FILE));
        String[] clocksToShow = _result.getStringArray(ClockDomainCommandParser.DOMAIN);
        Set<EdifNet> clocks = null;

        cda.classifyNets();
        // This array is guaranteed to have at least one element due to
        // the command parser, so the following won't be out of bounds
        if (clocksToShow[0].toLowerCase().equals(ALL))
            clocks = new LinkedHashSet<EdifNet>(cda._clkToNetMap.keySet());
        else {
            clocks = new LinkedHashSet<EdifNet>();
            for (int i = 0; i < clocksToShow.length; i++) {
                boolean match = false;
                for (EdifNet net : cda._clkToNetMap.keySet()) {
                    if (net.getName().toLowerCase().equals(clocksToShow[i].toLowerCase())) {
                        match = true;
                        clocks.add(net);
                        break;
                    }
                }
                if (!match)
                    output.println("\n*** No match for " + clocksToShow[i]);
            }
        }

        output.println(BAR + "\nDesign clocks\n" + BAR);
        for (EdifNet net : cda._clkToNetMap.keySet())
            output.println("  " + net);
        output.println();

        if (_result.getBoolean(ClockDomainCommandParser.SHOW_GATED_CLOCKS)) {
            boolean found = false;
            output.println(BAR + "\nGated clocks\n" + BAR);
            for (EdifNet net : cda._clkToNetMap.keySet()) {
                String str = "";
                for (EdifPortRef epr : net.getOutputPortRefs()) {
                    if (!cda.isClockDriver(epr.getCellInstance().getCellType())) {
                        str += ((FlattenedEdifCellInstance) epr.getCellInstance()).getHierarchicalEdifName() + " ("
                                + epr.getCellInstance().getCellType() + ")";
                        found = true;
                    }
                }
                if (!str.equals(""))
                    output.println("  " + net + ": driven by " + str);
            }
            if (!found)
                output.println("  No gated clocks");
            output.println();
        }

        if (_result.getBoolean(ClockDomainCommandParser.SHOW_NETS)) {
            output.println(BAR + "\nClassified Nets\n" + BAR);
            for (EdifNet net : clocks) {
                output.println("NET: " + net + " (" + cda._clkToNetMap.get(net).size() + " nets in domain)");
                Set<String> strSet = new TreeSet<String>();
                for (EdifNet n : cda._clkToNetMap.get(net)) {
                    Set<String> driverSet = new TreeSet<String>();
                    for (EdifPortRef epr : n.getOutputPortRefs()) {
                        if (epr.getCellInstance() != null)
                            driverSet.add(((FlattenedEdifCellInstance) epr.getCellInstance()).getHierarchicalEdifName()
                                    + " (" + epr.getCellInstance().getType() + ")");
                    }
                    strSet.add(net + ": " + n.getName() + " driven by " + driverSet);
                }
                for (String s : strSet) {
                    output.println("  " + s);
                }
                output.println();
            }
            if (_result.getBoolean(ClockDomainCommandParser.SHOW_NO_DOMAIN)) {
                output.println("No Domain (" + cda._noClockNets.size() + " nets)");
                Set<String> strSet = new TreeSet<String>();
                for (EdifNet n : cda._noClockNets) {
                    Set<String> driverSet = new TreeSet<String>();
                    for (EdifPortRef epr : n.getOutputPortRefs()) {
                        if (epr.getCellInstance() != null)
                            driverSet.add(((FlattenedEdifCellInstance) epr.getCellInstance()).getHierarchicalEdifName()
                                    + " (" + epr.getCellInstance().getType() + ")");
                    }
                    strSet.add("No domain: " + n.getName() + " driven by " + driverSet);
                }
                for (String s : strSet) {
                    output.println("  " + s);
                }
                output.println();
            }
        }

        cda.classifyECIs();

        if (_result.getBoolean(ClockDomainCommandParser.SHOW_CELLS)) {
            output.println(BAR + "\nClassified Edif Cells\n" + BAR);
            for (EdifNet net : clocks) {
                output.println("NET: " + net + " (" + cda._clkToECIMap.get(net).size() + " cells in domain)");
                Set<String> strSet = new TreeSet<String>();
                for (EdifCellInstance eci : cda._clkToECIMap.get(net)) {
                    strSet.add(net + ": " + ((FlattenedEdifCellInstance) eci).getHierarchicalEdifName() + " ("
                            + eci.getCellType().getName() + ")");
                }
                for (String s : strSet) {
                    output.println("  " + s);
                }
                output.println();
            }
            if (_result.getBoolean(ClockDomainCommandParser.SHOW_NO_DOMAIN)) {
                output.println("No Domain (" + cda._noClockECIs.size() + " cells)");
                Set<String> strSet = new TreeSet<String>();
                for (EdifCellInstance eci : cda._noClockECIs) {
                    strSet.add("No domain: " + ((FlattenedEdifCellInstance) eci).getHierarchicalEdifName() + " ("
                            + eci.getCellType().getName() + ")");
                }
                for (String s : strSet) {
                    output.println("  " + s);
                }
                output.println();
            }
        }

        if (_result.getBoolean(ClockDomainCommandParser.SHOW_SYNCHRONOUS)) {
            output.println(BAR + "\nClassified Synchronous Edif Cells\n" + BAR);
            for (EdifNet net : clocks) {
                output.println("NET: " + net);
                Set<String> strSet = new TreeSet<String>();
                for (EdifCellInstance eci : cda._clkToECIMap.get(net)) {
                    if (cda.isSequential(eci.getCellType()))
                        strSet.add(net + ": " + ((FlattenedEdifCellInstance) eci).getHierarchicalEdifName() + " ("
                                + eci.getCellType().getName() + ")");
                }
                for (String s : strSet) {
                    output.println("  " + s);
                }
                output.println();
            }
            if (_result.getBoolean(ClockDomainCommandParser.SHOW_NO_DOMAIN)) {
                output.println("No Domain");
                Set<String> strSet = new TreeSet<String>();
                for (EdifCellInstance eci : cda._noClockECIs) {
                    if (cda.isSequential(eci.getCellType()))
                        strSet.add("No domain: " + ((FlattenedEdifCellInstance) eci).getHierarchicalEdifName() + " ("
                                + eci.getCellType().getName() + ")");
                }
                for (String s : strSet) {
                    output.println("  " + s);
                }
                output.println();
            }
        }

        if (_result.getBoolean(ClockDomainCommandParser.SHOW_ASYNCHRONOUS)) {
            output.println(BAR + "\nClassified Asynchronous Edif Cells\n" + BAR);
            for (EdifNet net : clocks) {
                output.println("NET: " + net);
                Set<String> strSet = new TreeSet<String>();
                for (EdifCellInstance eci : cda._clkToECIMap.get(net)) {
                    if (!cda.isSequential(eci.getCellType()))
                        strSet.add(net + ": " + ((FlattenedEdifCellInstance) eci).getHierarchicalEdifName() + " ("
                                + eci.getCellType().getName() + ")");
                }
                for (String s : strSet) {
                    output.println("  " + s);
                }
                output.println();
            }
            if (_result.getBoolean(ClockDomainCommandParser.SHOW_NO_DOMAIN)) {
                output.println("No Domain");
                Set<String> strSet = new TreeSet<String>();
                for (EdifCellInstance eci : cda._noClockECIs) {
                    if (!cda.isSequential(eci.getCellType()))
                        strSet.add("No domain: " + ((FlattenedEdifCellInstance) eci).getHierarchicalEdifName() + " ("
                                + eci.getCellType().getName() + ")");
                }
                for (String s : strSet) {
                    output.println("  " + s);
                }
                output.println();
            }
        }

        if (_result.getBoolean(ClockDomainCommandParser.SHOW_ASYNCHRONOUS_RESETS)) {
            output.println(BAR + "\nAsynchronous Resets\n" + BAR);
            Map<EdifNet, Set<EdifCellInstance>> asynchResetMap = cda.getAsynchronousResets();
            if (asynchResetMap.size() > 0) {
                for (EdifNet net : asynchResetMap.keySet()) {
                    output.println("  " + net);
                }
            } else
                output.println("None");
            output.println();
        }

        if (_result.getBoolean(ClockDomainCommandParser.SHOW_ASYNCHRONOUS_RESET_CELLS)) {
            output.println(BAR + "\nAsynchronous Reset Cells\n" + BAR);
            Map<EdifNet, Set<EdifCellInstance>> asynchResetMap = cda.getAsynchronousResets();
            if (asynchResetMap.size() > 0) {
                for (EdifNet net : asynchResetMap.keySet()) {
                    Set<String> strSet = new TreeSet<String>();
                    for (EdifCellInstance eci : asynchResetMap.get(net)) {
                        strSet.add(net + ": " + ((FlattenedEdifCellInstance) eci).getHierarchicalEdifName() + " ("
                                + eci.getCellType().getName() + ")");
                    }
                    output.println("NET: " + net + " (" + strSet.size() + " cells)");
                    for (String s : strSet) {
                        output.println("  " + s);
                    }
                }
            } else
                output.println("None");
            output.println();
        }

        if (_result.contains(ClockDomainCommandParser.SHOW_CLOCK_CROSSINGS)) {
            output.println(BAR + "\nClock Domain Crossings\n" + BAR);

            Set<String> crossingsToShow = new TreeSet<String>();
            String[] params = _result.getStringArray(ClockDomainCommandParser.SHOW_CLOCK_CROSSINGS);
            int i = 0;
            // print out any names that don't match clocks
            while (i < params.length) {
                params[i] = params[i].toLowerCase();
                if (!params[i].equals(ALL)) {
                    boolean valid = false;
                    for (EdifNet n : cda._clkToNetMap.keySet()) {
                        if (n.getName().toLowerCase().equals(params[i]))
                            valid = true;
                    }
                    if (!valid)
                        output.println("*** " + params[i] + " not a clock");
                }
                i++;
            }
            i = 0;
            while (i < params.length) {
                Set<String> tempSet = new TreeSet<String>();
                if (params[i].equals(ALL)) {
                    for (EdifNet net : cda._clkToNetMap.keySet()) {
                        tempSet.add(net + " to ");
                    }
                } else {
                    tempSet.add(params[i] + " to ");
                }
                if (params[i + 1].equals(ALL)) {
                    for (String from : tempSet) {
                        for (EdifNet net : cda._clkToNetMap.keySet()) {
                            if (!from.contains(net.toString())) // don't include "clka to clka" as a crossing
                                crossingsToShow.add(from + net);
                        }
                    }
                } else {
                    for (String from : tempSet) {
                        if (!from.contains(params[i + 1])) // don't include "clka to clka" as a crossing
                            crossingsToShow.add(from + params[i + 1]);
                    }
                }
                i += 2;
            }
            Map<String, Set<String>> strMap = cda.getClockCrossings();
            for (String key : crossingsToShow) {
                key = key.toLowerCase();
                if (strMap.keySet().contains(key)) {
                    output.println(key + " (" + strMap.get(key).size() + " crossings)");
                    for (String s : strMap.get(key)) {
                        output.println("  " + s);
                    }
                    output.println();
                }
            }
            if (strMap.keySet().size() == 0)
                output.println("None\n");
        }

        if (_result.getBoolean(ClockDomainCommandParser.DO_SCC_ANALYSIS)) {
            SCCDepthFirstSearch scc = cda.doSCCAnalysis(_result.getBoolean(ClockDomainCommandParser.NO_IOB_FEEDBACK));
            output.println(BAR + "\nSCC Analysis\n" + BAR);
            cda.analyzeSCCs(scc, output);
            output.println();
        }

        if (_result.contains(ClockDomainCommandParser.CREATE_DOTTY_GRAPH)) {
            String[] params = _result.getStringArray(ClockDomainCommandParser.CREATE_DOTTY_GRAPH);
            int i = 0;
            while (i < params.length) {
                boolean found = false;
                for (EdifCellInstance eci : cda._top.getTopCell().getSubCellList()) {
                    if (((FlattenedEdifCellInstance) eci).getHierarchicalEdifName().toLowerCase().equals(
                            params[i].toLowerCase())) {
                        Stack<EdifCellInstance> myStack = new Stack<EdifCellInstance>();
                        Set<EdifCellInstance> eciSet = new LinkedHashSet<EdifCellInstance>();
                        eciSet.add(eci);
                        for (EdifPortRef epr : eci.getInputEPRs()) {
                            if (epr.getPort().getName().toLowerCase().equals(params[i + 1].toLowerCase())) {
                                myStack.addAll(cda._ecic.getPredecessors(eci, epr));
                            }
                        }
                        while (!myStack.isEmpty()) {
                            try {
                                EdifCellInstance e = myStack.pop();
                                if (!eciSet.contains(e) && !cda.isSequential(e.getCellType())) {
                                    myStack.addAll(cda._ecic.getPredecessors(e));
                                }
                                eciSet.add(e);
                            } catch (ClassCastException e) {
                            }
                        }
                        String name = eci.getName() + "_" + eciSet.size() + ".dot";
                        AbstractGraphToDotty agtd = new AbstractGraphToDotty();

                        String data = agtd.createDottyBody(cda._ecic.getSubGraph(eciSet), cda._clkToECIMap);
                        try {
                            FileWriter fw = new FileWriter(name);
                            fw.write(data);
                            fw.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.exit(-1);
                        }
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    output.println(params[i] + " not found");
                }
                i += 2;
            }
        }
    }
}
