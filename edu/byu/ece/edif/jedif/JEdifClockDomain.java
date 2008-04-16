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

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCellInstance;
import edu.byu.ece.edif.tools.flatten.NewFlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxResourceMapper;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.iob.AbstractIOBAnalyzer;
import edu.byu.ece.edif.util.iob.XilinxVirtexIOBAnalyzer;
import edu.byu.ece.edif.util.jsap.EDIFMain;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.graph.AbstractGraphToDotty;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.dfs.DepthFirstTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

public class JEdifClockDomain extends EDIFMain {

    public static void main(String[] args) {
        // Define the print streams for this program
        PrintStream out = System.out;
        PrintStream err = System.out;

        // Print executable heading
        EXECUTABLE_NAME = "JEdifClockDomain";
        TOOL_SUMMARY_STRING = "Reads the contents of a .jedif file and provides clock domain information.";
        printProgramExecutableString(out);

        // Parse command line options
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new JEdifClockDomainCommandGroup());
        JSAPResult result = parser.parse(args, err);
        if (!result.success())
            System.exit(1);

        // Decide whether to print to stdout or an output file
        try {
            if (JEdifClockDomainCommandGroup.outputFile(result))
                out = new PrintStream(JEdifClockDomainCommandGroup.getOutputFilename(result));
        } catch (FileNotFoundException e) {
            err.println("Error creating output file.");
        }

        EdifEnvironment env = JEdifClockDomainCommandGroup.getEdifEnvironment(result, out);
        if (env == null)
            err.println("Invalid .jedif file.");
        JEdifClockDomain jecd = new JEdifClockDomain(env);
        // TODO: add functionality for output file options	
        jecd.classifyNets();
        Set<EdifNet> clocks = getClocksToShow(result, jecd, out);
        boolean showNoDomain = JEdifClockDomainCommandGroup.showNoDomain(result);
        boolean noIOBFeedback = JEdifClockDomainCommandGroup.noIOBFeedback(result);

        // list all clocks used in the design - only default output
        out.println(BAR + "\nDesign clocks\n" + BAR);
        for (EdifNet net : jecd._clkToNetMap.keySet())
            out.println("  " + net);
        out.println();

        if (JEdifClockDomainCommandGroup.showGatedClocks(result))
            showGatedClocks(jecd, out);
        if (JEdifClockDomainCommandGroup.showNets(result))
            showNets(jecd, clocks, showNoDomain, out);

        jecd.classifyECIs();

        if (JEdifClockDomainCommandGroup.showCells(result))
            showCells(jecd, clocks, showNoDomain, out);
        if (JEdifClockDomainCommandGroup.showSynchronous(result))
            showSynchronous(jecd, clocks, showNoDomain, out);
        if (JEdifClockDomainCommandGroup.showAsynchronous(result))
            showAsynchronous(jecd, clocks, showNoDomain, out);
        if (JEdifClockDomainCommandGroup.showAsynchronousResets(result))
            showAsynchronousResets(jecd, out);
        if (JEdifClockDomainCommandGroup.showAsynchronousResetCells(result))
            showAsynchronousResetCells(jecd, out);
        if (JEdifClockDomainCommandGroup.showClockCrossings(result)) {
            String[] params = JEdifClockDomainCommandGroup.getCrossingsList(result);
            showClockCrossings(jecd, params, out);
        }

        if (JEdifClockDomainCommandGroup.doSCCAnalysis(result))
            ;
        {
            SCCDepthFirstSearch scc = jecd.doSCCAnalysis(noIOBFeedback);
            jecd.analyzeSCCs(scc, out);
        }

        if (JEdifClockDomainCommandGroup.createDottyGraph(result)) {
            String[] params = JEdifClockDomainCommandGroup.getDottyGraphList(result);
            createDottyGraph(jecd, params, out);
        }

        if (JEdifClockDomainCommandGroup.outputFile(result))
            System.out.println("Results written to output file: "
                    + JEdifClockDomainCommandGroup.getOutputFilename(result));

        System.out.println();
    }

    //TODO: add more constructors to better interact with other tools (feedback)
    //	public JEdifClockDomain (EdifCell) {
    //		classifyNets();
    //		classifyECIs();
    //		//or something like that. then make methods to access useful information
    //	}

    // this is the constructor used right now in main - convenient for JEdif
    public JEdifClockDomain(EdifEnvironment e) {
        _top = e;
        _ecic = new EdifCellInstanceGraph(_top.getTopCell(), true);
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
     * EdifNets are not neccessarily mutually exclusive.
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
                        if (!epr.getNet().getOutputPortRefs().iterator().next().getCellInstance().getType()
                                .toLowerCase().equals("gnd"))
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
                    if (eci.getType().toLowerCase().contains(RAM_PREFIX)) {
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
     * of EdifCellInstances are not neccessarily mutually exclusive.
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
     * Find all clock crossing in the design. This is found by iterating over
     * all EdifCellInstances in the design. Clock crossings occur when a
     * sequential cell's inputs are in a different domain than its own.
     * <p>
     * TODO: do we still want to use a map here? Maybe we should return a clock
     * domain crossing object to make it easier to interact with the outside.
     * Or, we could do that in another method - this one works great for
     * printing.
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
     * Returns a Set of EdifNets corresponding to the clock domains that will be
     * printed out and analyzed.
     * 
     * @param result The results of the command line parser
     * @param jecd The JEdifClockDomain instance created for this design
     * @param out The specified output PrintStream
     * @return Set of EdifNets corresponding to user-specified clock domains.
     */
    private static Set<EdifNet> getClocksToShow(JSAPResult result, JEdifClockDomain jecd, PrintStream out) {
        String[] clocksToShow = JEdifClockDomainCommandGroup.getClocksToShow(result);
        Set<EdifNet> clocks = null;
        if (clocksToShow[0].toLowerCase().equals(ALL))
            clocks = new LinkedHashSet<EdifNet>(jecd._clkToNetMap.keySet());
        else {
            clocks = new LinkedHashSet<EdifNet>();
            for (int i = 0; i < clocksToShow.length; i++) {
                boolean match = false;
                for (EdifNet net : jecd._clkToNetMap.keySet()) {
                    if (net.getName().toLowerCase().equals(clocksToShow[i].toLowerCase())) {
                        match = true;
                        clocks.add(net);
                        break;
                    }
                }
                if (!match)
                    out.println("\n*** No match for " + clocksToShow[i]);
            }
        }

        return clocks;
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
            AbstractIOBAnalyzer iobAnalyzer = new XilinxVirtexIOBAnalyzer((NewFlattenedEdifCell) _top.getTopCell(),
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
     * Prints a list of all gated clocks in the design.
     * 
     * @param jecd The JEdifClockDomain instance created for this design
     * @param out The specified output PrintStream
     */
    private static void showGatedClocks(JEdifClockDomain jecd, PrintStream out) {
        boolean found = false;
        out.println(BAR + "\nGated clocks\n" + BAR);
        for (EdifNet net : jecd._clkToNetMap.keySet()) {
            String str = "";
            for (EdifPortRef epr : net.getOutputPortRefs()) {
                if (!jecd.isClockDriver(epr.getCellInstance().getCellType())) {
                    str += ((FlattenedEdifCellInstance) epr.getCellInstance()).getHierarchicalEdifName() + " ("
                            + epr.getCellInstance().getCellType() + ")";
                    found = true;
                }
            }
            if (!str.equals(""))
                out.println("  " + net + ": driven by " + str);
        }
        if (!found)
            out.println("  No gated clocks");
        out.println();
    }

    /**
     * Prints out a list of EdifNets belonging to the specified clock domains.
     * 
     * @param jecd The JEdifClockDomain instance created for this design
     * @param clocks The set of EdifNets corresponding to user-specified clock
     * domains
     * @param showNoDomain Boolean specifying whether to print EdifNets
     * belonging to no clock domain
     * @param out The specified output PrintStream
     */
    private static void showNets(JEdifClockDomain jecd, Set<EdifNet> clocks, boolean showNoDomain, PrintStream out) {
        out.println(BAR + "\nClassified Nets\n" + BAR);
        for (EdifNet net : clocks) {
            out.println("NET: " + net + " (" + jecd._clkToNetMap.get(net).size() + " nets in domain)");
            Set<String> strSet = new TreeSet<String>();
            for (EdifNet n : jecd._clkToNetMap.get(net)) {
                Set<String> driverSet = new TreeSet<String>();
                for (EdifPortRef epr : n.getOutputPortRefs()) {
                    if (epr.getCellInstance() != null)
                        driverSet.add(((FlattenedEdifCellInstance) epr.getCellInstance()).getHierarchicalEdifName()
                                + " (" + epr.getCellInstance().getType() + ")");
                }
                strSet.add(net + ": " + n.getName() + " driven by " + driverSet);
            }
            for (String s : strSet) {
                out.println("  " + s);
            }
            out.println();
        }
        if (showNoDomain) {
            out.println("No Domain (" + jecd._noClockNets.size() + " nets)");
            Set<String> strSet = new TreeSet<String>();
            for (EdifNet n : jecd._noClockNets) {
                Set<String> driverSet = new TreeSet<String>();
                for (EdifPortRef epr : n.getOutputPortRefs()) {
                    if (epr.getCellInstance() != null)
                        driverSet.add(((FlattenedEdifCellInstance) epr.getCellInstance()).getHierarchicalEdifName()
                                + " (" + epr.getCellInstance().getType() + ")");
                }
                strSet.add("No domain: " + n.getName() + " driven by " + driverSet);
            }
            for (String s : strSet) {
                out.println("  " + s);
            }
            out.println();
        }
    }

    /**
     * Prints out a list of EdifCellInstances belonging to the specified clock
     * domains.
     * 
     * @param jecd The JEdifClockDomain instance created for this design
     * @param clocks The set of EdifNets corresponding to user-specified clock
     * domains
     * @param showNoDomain Boolean specifying whether to print EdifNets
     * belonging to no clock domain
     * @param out The specified output PrintStream
     */
    private static void showCells(JEdifClockDomain jecd, Set<EdifNet> clocks, boolean showNoDomain, PrintStream out) {
        out.println(BAR + "\nClassified Edif Cells\n" + BAR);
        for (EdifNet net : clocks) {
            out.println("NET: " + net + " (" + jecd._clkToECIMap.get(net).size() + " cells in domain)");
            Set<String> strSet = new TreeSet<String>();
            for (EdifCellInstance eci : jecd._clkToECIMap.get(net)) {
                strSet.add(net + ": " + ((FlattenedEdifCellInstance) eci).getHierarchicalEdifName() + " ("
                        + eci.getCellType().getName() + ")");
            }
            for (String s : strSet) {
                out.println("  " + s);
            }
            out.println();
        }
        if (showNoDomain) {
            out.println("No Domain (" + jecd._noClockECIs.size() + " cells)");
            Set<String> strSet = new TreeSet<String>();
            for (EdifCellInstance eci : jecd._noClockECIs) {
                strSet.add("No domain: " + ((FlattenedEdifCellInstance) eci).getHierarchicalEdifName() + " ("
                        + eci.getCellType().getName() + ")");
            }
            for (String s : strSet) {
                out.println("  " + s);
            }
            out.println();
        }

    }

    /**
     * Prints out a list of synchronous EdifCellInstances belonging to the
     * specified clock domains.
     * 
     * @param jecd The JEdifClockDomain instance created for this design
     * @param clocks The set of EdifNets corresponding to user-specified clock
     * domains
     * @param showNoDomain Boolean specifying whether to print EdifNets
     * belonging to no clock domain
     * @param out The specified output PrintStream
     */
    private static void showSynchronous(JEdifClockDomain jecd, Set<EdifNet> clocks, boolean showNoDomain,
            PrintStream out) {
        out.println(BAR + "\nClassified Synchronous Edif Cells\n" + BAR);
        for (EdifNet net : clocks) {
            out.println("NET: " + net);
            Set<String> strSet = new TreeSet<String>();
            for (EdifCellInstance eci : jecd._clkToECIMap.get(net)) {
                if (jecd.isSequential(eci.getCellType()))
                    strSet.add(net + ": " + ((FlattenedEdifCellInstance) eci).getHierarchicalEdifName() + " ("
                            + eci.getCellType().getName() + ")");
            }
            for (String s : strSet) {
                out.println("  " + s);
            }
            out.println();
        }
        if (showNoDomain) {
            out.println("No Domain");
            Set<String> strSet = new TreeSet<String>();
            for (EdifCellInstance eci : jecd._noClockECIs) {
                if (jecd.isSequential(eci.getCellType()))
                    strSet.add("No domain: " + ((FlattenedEdifCellInstance) eci).getHierarchicalEdifName() + " ("
                            + eci.getCellType().getName() + ")");
            }
            for (String s : strSet) {
                out.println("  " + s);
            }
            out.println();
        }
    }

    /**
     * Prints out a list of asynchronous EdifCellInstances belonging to the
     * specified clock domains.
     * 
     * @param jecd The JEdifClockDomain instance created for this design
     * @param clocks The set of EdifNets corresponding to user-specified clock
     * domains
     * @param showNoDomain Boolean specifying whether to print EdifNets
     * belonging to no clock domain
     * @param out The specified output PrintStream
     */
    private static void showAsynchronous(JEdifClockDomain jecd, Set<EdifNet> clocks, boolean showNoDomain,
            PrintStream out) {
        out.println(BAR + "\nClassified Asynchronous Edif Cells\n" + BAR);
        for (EdifNet net : clocks) {
            out.println("NET: " + net);
            Set<String> strSet = new TreeSet<String>();
            for (EdifCellInstance eci : jecd._clkToECIMap.get(net)) {
                if (!jecd.isSequential(eci.getCellType()))
                    strSet.add(net + ": " + ((FlattenedEdifCellInstance) eci).getHierarchicalEdifName() + " ("
                            + eci.getCellType().getName() + ")");
            }
            for (String s : strSet) {
                out.println("  " + s);
            }
            out.println();
        }
        if (showNoDomain) {
            out.println("No Domain");
            Set<String> strSet = new TreeSet<String>();
            for (EdifCellInstance eci : jecd._noClockECIs) {
                if (!jecd.isSequential(eci.getCellType()))
                    strSet.add("No domain: " + ((FlattenedEdifCellInstance) eci).getHierarchicalEdifName() + " ("
                            + eci.getCellType().getName() + ")");
            }
            for (String s : strSet) {
                out.println("  " + s);
            }
            out.println();
        }

    }

    /**
     * Prints out a list of asynchronous reset cells in the design.
     * 
     * @param jecd The JEdifClockDomain instance created for this design
     * @param out The specified output PrintStream
     */
    private static void showAsynchronousResetCells(JEdifClockDomain jecd, PrintStream out) {
        out.println(BAR + "\nAsynchronous Reset Cells\n" + BAR);
        Map<EdifNet, Set<EdifCellInstance>> asynchResetMap = jecd.getAsynchronousResets();
        if (asynchResetMap.size() > 0) {
            for (EdifNet net : asynchResetMap.keySet()) {
                Set<String> strSet = new TreeSet<String>();
                for (EdifCellInstance eci : asynchResetMap.get(net)) {
                    strSet.add(net + ": " + ((FlattenedEdifCellInstance) eci).getHierarchicalEdifName() + " ("
                            + eci.getCellType().getName() + ")");
                }
                out.println("NET: " + net + " (" + strSet.size() + " cells)");
                for (String s : strSet) {
                    out.println("  " + s);
                }
            }
        } else
            out.println("None");
        out.println();

    }

    /**
     * Prints out a list of synchronous resets in the design.
     * 
     * @param jecd The JEdifClockDomain instance created for this design
     * @param out The specified output PrintStream
     */
    private static void showAsynchronousResets(JEdifClockDomain jecd, PrintStream out) {
        out.println(BAR + "\nAsynchronous Resets\n" + BAR);
        Map<EdifNet, Set<EdifCellInstance>> asynchResetMap = jecd.getAsynchronousResets();
        if (asynchResetMap.size() > 0) {
            for (EdifNet net : asynchResetMap.keySet()) {
                out.println("  " + net);
            }
        } else
            out.println("None");
        out.println();
    }

    /**
     * Prints information about all clock crossings belonging to the specified
     * clock domain pairs.
     * 
     * @param jecd The JEdifClockDomain instance created for this design
     * @param params The array of clock domain names specified by the user
     * @param out The specified output PrintStream
     */
    private static void showClockCrossings(JEdifClockDomain jecd, String[] params, PrintStream out) {
        out.println(BAR + "\nClock Domain Crossings\n" + BAR);
        Set<String> crossingsToShow = new TreeSet<String>();
        int i = 0;
        // print out any names that don't match clocks
        while (i < params.length) {
            params[i] = params[i].toLowerCase();
            if (!params[i].equals(ALL)) {
                boolean valid = false;
                for (EdifNet n : jecd._clkToNetMap.keySet()) {
                    if (n.getName().toLowerCase().equals(params[i]))
                        valid = true;
                }
                if (!valid)
                    out.println("*** " + params[i] + " not a clock");
            }
            i++;
        }
        i = 0;
        while (i < params.length) {
            Set<String> tempSet = new TreeSet<String>();
            if (params[i].equals(ALL)) {
                for (EdifNet net : jecd._clkToNetMap.keySet()) {
                    tempSet.add(net + " to ");
                }
            } else {
                tempSet.add(params[i] + " to ");
            }
            if (params[i + 1].equals(ALL)) {
                for (String from : tempSet) {
                    for (EdifNet net : jecd._clkToNetMap.keySet()) {
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
        Map<String, Set<String>> strMap = jecd.getClockCrossings();
        boolean nonePrinted = true;
        for (String key : crossingsToShow) {
            key = key.toLowerCase();
            if (strMap.keySet().contains(key)) {
                nonePrinted = false;
                out.println(key + " (" + strMap.get(key).size() + " crossings)");
                for (String s : strMap.get(key)) {
                    out.println("  " + s);
                }
                out.println();
            }
        }
        if (strMap.keySet().size() == 0)
            out.println("None\n");
        if (nonePrinted)
            out.println("None between specified domain pairs\n");
    }

    /**
     * Output summary of SCC to a printstream
     * 
     * @param scc The SCC to analyze
     * @param output The PrintStream to write the summary to
     */
    protected void analyzeSCCs(SCCDepthFirstSearch scc, PrintStream out) {
        out.println(BAR + "\nSCC Analysis\n" + BAR);
        out.println("Total sccs in design: " + scc.getTopologicallySortedTreeList().size());

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
            out.println("  SCC(" + tree.getNodes().size() + ") " + clkSet);
            out.println();
        }
    }

    /**
     * Creates a dotty graph representation of the specified cell and its
     * predecessors through the specified port. Outputs a file with filename in
     * the form of: [name of EdifCellInstance][size of EdifCellInstance].dot
     * 
     * @param jecd The JEdifClockDomain instance created for this design
     * @param params The array of cell/port names provided by the user
     * @param out The specified output PrintStream
     */
    private static void createDottyGraph(JEdifClockDomain jecd, String[] params, PrintStream out) {
        int i = 0;
        while (i < params.length) {
            boolean found = false;
            for (EdifCellInstance eci : JEdifClockDomain._top.getTopCell().getSubCellList()) {
                if (((FlattenedEdifCellInstance) eci).getHierarchicalEdifName().toLowerCase().equals(
                        params[i].toLowerCase())) {
                    Stack<EdifCellInstance> myStack = new Stack<EdifCellInstance>();
                    Set<EdifCellInstance> eciSet = new LinkedHashSet<EdifCellInstance>();
                    eciSet.add(eci);
                    for (EdifPortRef epr : eci.getInputEPRs()) {
                        if (epr.getPort().getName().toLowerCase().equals(params[i + 1].toLowerCase())) {
                            myStack.addAll(JEdifClockDomain._ecic.getPredecessors(eci, epr));
                        }
                    }
                    while (!myStack.isEmpty()) {
                        try {
                            EdifCellInstance e = myStack.pop();
                            if (!eciSet.contains(e) && !jecd.isSequential(e.getCellType())) {
                                myStack.addAll(JEdifClockDomain._ecic.getPredecessors(e));
                            }
                            eciSet.add(e);
                        } catch (ClassCastException e) {
                        }
                    }
                    String name = eci.getName() + "_" + eciSet.size() + ".dot";
                    AbstractGraphToDotty agtd = new AbstractGraphToDotty();

                    String data = agtd.createDottyBody(JEdifClockDomain._ecic.getSubGraph(eciSet), jecd._clkToECIMap);
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
                out.println(params[i] + " not found");
            }
            i += 2;
        }

    }

    /**
     * Public method to return _clkToECIMap after classifying the nets and ECIs.
     * 
     * @return A map whose key is the EdifNet of the clock domain and whose
     * value is a set of EdifCellInstances that belong to that domain. The sets
     * of EdifCellInstances are not neccessarily mutually exclusive.
     */
    public Map<EdifNet, Set<EdifCellInstance>> getECIMap() {
        classifyNets();
        classifyECIs();
        return _clkToECIMap;
    }

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
    protected static EdifCellInstanceGraph _ecic;

    /**
     * An EdifEnvironment used to store the environment of the flattened design.
     */
    protected static EdifEnvironment _top;

    protected static final String BAR = "-----------------------------------";

    protected static final String ALL = "all";

    protected static final String RAM_PREFIX = "ramb4";

    private static final Set<String> _resetPortNames;
    static {
        _resetPortNames = new TreeSet<String>();
        _resetPortNames.add("clr");
        _resetPortNames.add("pre");
    }

}
