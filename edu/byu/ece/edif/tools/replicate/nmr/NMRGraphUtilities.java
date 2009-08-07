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
package edu.byu.ece.edif.tools.replicate.nmr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import edu.byu.ece.edif.arch.xilinx.XilinxMergeParser;
import edu.byu.ece.edif.arch.xilinx.XilinxTools;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.TMRReplicationType;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxNMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxVirtexDeviceUtilizationTracker;
import edu.byu.ece.edif.util.graph.AbstractEdifGraph;
import edu.byu.ece.edif.util.graph.EdifCellBadCutGroupings;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollection;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollectionGraph;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGroupings;
import edu.byu.ece.edif.util.graph.EdifPortRefEdge;
import edu.byu.ece.graph.AbstractGraphToDotty;
import edu.byu.ece.graph.BasicGraph;
import edu.byu.ece.graph.DirectedGraph;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.dfs.BasicDepthFirstSearchTree;
import edu.byu.ece.graph.dfs.DepthFirstSearchForest;
import edu.byu.ece.graph.dfs.DepthFirstTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

//////////////////////////////////////////////////////////////////////////
////NMRGraphUtilities
/**
 * A set of utilities to work with strongly connected components (SCCs),
 * including methods to break apart large SCCs into smaller, more manageable
 * units. Triplication of SCCs for a given list of EdifEdges is also performed
 * by this class.
 * 
 * @author Michael Wirthlin, Brian Pratt, Keith Morgan
 * @see SCCDepthFirstSearch
 */
public class NMRGraphUtilities {

    public static void main(String args[]) {

        // 1. Parse the EDIF file and merge any associated black boxes
        System.out.println("Parsing . . .");
        EdifCell cell = XilinxMergeParser.parseAndMergeXilinx(args);

        // 2. Flatten the design into a single EDIF cell
        System.out.println("Starting Flattening . . .");
        EdifCell flat_cell = null;
        try {
            flat_cell = new FlattenedEdifCell(cell);
        } catch (EdifNameConflictException e1) {
            e1.toRuntime();
        } catch (InvalidEdifNameException e1) {
            e1.toRuntime();
        }

        // 3. Create the cell connectivity data structure         
        EdifCellInstanceGraph graph = new EdifCellInstanceGraph(flat_cell);

        //		 for (Iterator i = graph.getEdges().iterator(); i.hasNext();) {
        //            Edge e = (Edge) i.next();
        //            graph.removeEdge(e);
        //        }

        // 4. Create architecture specific data structures
        final String part = "XCV1000FG680";

        NMRArchitecture nmrArch = new XilinxNMRArchitecture();
        System.out.println("Calculating normal resource utilization of cell " + flat_cell);
        DeviceUtilizationTracker du_tracker = null;
        try {
            du_tracker = new XilinxVirtexDeviceUtilizationTracker(flat_cell, part);
        } catch (OverutilizationException e) {
            throw new EdifRuntimeException("ERROR: Initial contents of cell " + flat_cell + " do not fit into part "
                    + part);

        }
        ReplicationUtilizationTracker rTracker = new ReplicationUtilizationTracker(du_tracker);
        
        System.out.println("Normal utilization for cell " + flat_cell);
        System.out.println(du_tracker);

        /*
         * Create BadCutGroupConnectivity to be used with NMRGraphUtilities
         */
        // Create BadCutGroupings
        EdifCellInstanceGroupings groupings = new EdifCellBadCutGroupings(flat_cell, nmrArch, graph);
        // Create Group Connectivity (include top level ports)
        EdifCellInstanceCollectionGraph badCutGroupConn = new EdifCellInstanceCollectionGraph(graph, groupings, true);

        // 6. Create SCC (this can be reused by Brian)
        SCCDepthFirstSearch sccDFS = new SCCDepthFirstSearch(badCutGroupConn);
        //SCCDepthFirstSearch sccDFS = new SCCDepthFirstSearch(graph);

        // DEBUG: Print out Dotty Graph with colored SCCs
        Collection sccs = new ArrayList(sccDFS.getTrees().size());
        for (DepthFirstTree tree : sccDFS.getTrees()) {
            sccs.add(tree.getNodes());
        }
        String data = new AbstractGraphToDotty().createColoredDottyBody(badCutGroupConn, sccs);
        //String data = new AbstractGraphToDotty().createColoredDottyBody(graph, sccs);
        AbstractGraphToDotty.printFile("sccGraph.dot", data);

        // 7. Perform feedback analysis
       
        ReplicationType replicationType = TMRReplicationType.getInstance(nmrArch);
        boolean override = false;
        nmrSCCsUsingSCCDecomposition(sccDFS, nmrArch, rTracker, true, DEFAULT_SCC_SORT_TYPE, replicationType, override);
        //nmrSCCsUsingSCCDecomposition(sccDFS, tmrArch, du_tracker, true, cutSet);

        System.out.println("Post TMR estimate:\n" + du_tracker);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public variables                  ////

    public static final int DEFAULT_SCC_SORT_TYPE = 3;

    
    public static List<Edge> createBasicDecompositionCutset(AbstractEdifGraph graph, SCCDepthFirstSearch sccs, NMRArchitecture arch) {

        List<Edge> cuts = new ArrayList<Edge>();
        Stack<DepthFirstTree> s = new Stack<DepthFirstTree>();

        for (DepthFirstTree scc : sccs.getTrees()) {
            s.push(scc);
        }

        SCCDepthFirstSearch lastSearch = sccs;
        
        // Iterate until a valid cutset has been found.
        while (!s.empty()) {

            // Pop off DFS search tree (SCC)
            DepthFirstTree subSCCDFSTree = s.pop();


            Collection backEdges = subSCCDFSTree.getBackEdges();
            Collection badEdges = findBadEdges(backEdges, arch);
            backEdges.removeAll(badEdges);
            
            if (badEdges.size() == 0) { // if all cuts are good
                // remove all back edges, done with this SCC
                graph.removeEdges(backEdges);
                cuts.addAll(backEdges);
            }
            else {
                if (backEdges.size() != 0) { // if some are good, some are bad
                    // remove good back eges, put subSCCs on stack
                    graph.removeEdges(backEdges);
                    cuts.addAll(backEdges);
                    BasicGraph sccSubGraph = graph.getSubGraph(subSCCDFSTree.getNodes());
                    SCCDepthFirstSearch sccDFS = new SCCDepthFirstSearch(sccSubGraph);
                    for (DepthFirstTree scc : sccDFS.getTopologicallySortedTreeList()) {
                        s.push(scc);
                    }
                }
                else { // else all the cuts are bad
                    // redo the SCC decomposition of the current SCC with a different search order,
                    // putting the resulting SCC on the stack
                    LinkedList visitOrder  = new LinkedList(subSCCDFSTree.getNodes());
                    visitOrder.addFirst(visitOrder.removeLast());
                    BasicGraph sccSubGraph = graph.getSubGraph(visitOrder);
                    SCCDepthFirstSearch sccDFS = new SCCDepthFirstSearch(sccSubGraph, visitOrder);
                    for (DepthFirstTree scc : sccDFS.getTopologicallySortedTreeList()) {
                        s.push(scc);
                    }
                }   
            }
        }
        return cuts;
    }
    
    public static Collection<EdifPortRef> createAfterFFsCutset(AbstractEdifGraph graph, NMRArchitecture nmrArch) {
        Collection<EdifPortRef> cuts = new LinkedHashSet<EdifPortRef>();
        for (Object node : graph.getNodes()) {
            // make sure it's an instance, not a single bit port
            if (node instanceof EdifCellInstance) {
                EdifCellInstance eci = (EdifCellInstance) node;
                // make sure it's a register node
                if (XilinxTools.isRegisterCell(eci.getCellType())) {
                    // make sure it has a Q output and find it
                    EdifPortRef eprQ = null;
                    EdifCellInstanceEdge edgeQ = null;
                    for (Object edge : graph.getOutputEdges(node)) {
                        if (edge instanceof EdifCellInstanceEdge) {
                            EdifCellInstanceEdge eciEdge = (EdifCellInstanceEdge) edge;
                            EdifPortRef epr = eciEdge.getSourceEPR();
                            eprQ = epr;
                            edgeQ = eciEdge;
                            // make sure this is not a bad cut
                            if (!nmrArch.isBadCutConnection(eciEdge.getSourceEPR(), eciEdge.getSinkEPR())) {
                                // remove edge from graph (for verification purposes--later we'll see if there are any SCCs left)
                                graph.removeEdge(edgeQ);
                                cuts.add(eciEdge.getSinkEPR()); // add the sinks not the source (in case any of the sinks are a bad cut)
                            }
                        }
                    }
                }
            }
        }
        
        return cuts;
    }
    
    public static Collection<EdifPortRef> createBeforeFFsCutset(AbstractEdifGraph graph, NMRArchitecture nmrArch) {
        Collection<EdifPortRef> cuts = new LinkedHashSet<EdifPortRef>();
        for (Object node : graph.getNodes()) {
            // make sure it's an instance, not a single bit port
            if (node instanceof EdifCellInstance) {
                EdifCellInstance eci = (EdifCellInstance) node;
                // make sure it's a register node
                if (XilinxTools.isRegisterCell(eci.getCellType())) {
                    // make sure it has a D input and find it
                    EdifPortRef eprD = null;
                    EdifCellInstanceEdge edgeD = null;
                    for (Object edge : graph.getInputEdges(node)) {
                        if (edge instanceof EdifCellInstanceEdge) {
                            EdifCellInstanceEdge eciEdge = (EdifCellInstanceEdge) edge;
                            EdifPortRef epr = eciEdge.getSinkEPR();
                            if (epr.getPort().getName().equalsIgnoreCase("D")) {
                                eprD = epr;
                                edgeD = eciEdge;
                                // make sure this is not a bad cut
                                if (!nmrArch.isBadCutConnection(eciEdge.getSourceEPR(), eciEdge.getSinkEPR())) {
                                    // remove edge from graph (for verification purposes--later we'll see if there are any SCCs left)
                                    graph.removeEdge(edgeD);
                                    cuts.add(eprD);
                                }
                                break; // there's only one D
                            }
                        }
                    }
                }
            }
        }

        return cuts;
    }
    
    /**
     * Create a valid set of cuts which break all feedback in the given SCC.
     * This method breaks up the SCC little by little instead of generating the
     * entire cutset at once. Thus, cuts are determined in sets, breaking the
     * problem up when a full cutset is not immediately found.
     * 
     * @param graph The top-level graph
     * @param scc A BasicDepthFirstSearchTree object representing an SCC in the
     * top-level graph
     * @param arch The TMRArchitecture, needed to determine good/bad cuts
     * @return A Collection of EdifEdges making up a valid set of cuts to break
     * all feedback in the given SCC
     */
    public static Collection<Edge> createDecomposeValidCutSet(AbstractEdifGraph graph, // This should be the top-level, non-inverted graph
            BasicDepthFirstSearchTree scc, // This is a tree from a previous sccDecomp call
            NMRArchitecture arch) {
        // Algorithm for creating a valid cutset by decomposing an SCC:
        // 1. Create a sub graph containing only the SCC in question
        // 2. Initialize stack by pushing the full SCC
        // 3. While the stack is not empty, (the stack is expected to hold SCC subgraphs)
        //    a) Pop one SCC off of the stack
        //    b) Find the set of back edges in this stack
        //    c) Separate the set into "good" and "bad" back edges
        //    d) Add all good back edges to the cutset
        //    e) Switch on types of edges found:
        //       i.   Only good back edges found
        //            - Done with this SCC. Continue stack loop.
        //       ii.  Only bad back edges found
        //            - Re-search (DFS) the *top SCC* with a different visit 
        //              list to get a new set of back edges
        //              ** Why do we ust the top SCC, but use the visit list from the stack SCC?
        //            - Since this should be an SCC, only one tree should fall 
        //              out. Push this SCC onto the stack. 
        //       iii. Both good and bad back edges found
        //            - Remove all good edges *from the top SCC* (?)
        //            - Re-perform SCC decomp on top SCC (This should produce
        //              a forest of smaller SCCs)
        //            - Push all smaller SCCs onto the stack. Continue stack loop.
        //            ** Will this re-add SCCs that have already been added??
        //            ** Shouldn't we be doing this on the stack SCC rather than the top SCC?
        // 4. Once the stack is empty, a valid cutset should have been found,
        //    return that set of Edges

        // Create a sub graph of the original graph that includes only
        // those nets/nodes from the SCC.
        AbstractEdifGraph sccGraph = (AbstractEdifGraph) graph.getSubGraph(scc.getNodes());

        if (DEBUG)
            System.out.println("createDecomposeValidCutSet() called on SCC: " + sccGraph);

        Collection<Edge> cuts = new LinkedHashSet<Edge>();
        Stack<BasicDepthFirstSearchTree> s = new Stack<BasicDepthFirstSearchTree>();

        // Initialize stack with the SCC
        s.push(scc);
        // Iterate until a valid cutset has been found.
        // This will only be one iteration if a good cutset is found the first time
        while (!s.empty()) {
            BasicDepthFirstSearchTree subSCCDFSTree = s.pop();
            if (DEBUG)
                System.out.println("POP: stack SCC: " + subSCCDFSTree);

            // Grab *all* back edges in this tree
            Collection<Edge> goodBackEdges = subSCCDFSTree.getBackEdges();

            // Extract the "bad" edges from the good set and store them in a new set
            Collection<Edge> badBackEdges = null;
            badBackEdges = findBadEdges(goodBackEdges, arch);
            goodBackEdges.removeAll(badBackEdges);

            // Add all of the "good" edges to the Collection of edges to cut
            cuts.addAll(goodBackEdges);

            /*
             * If there were any "bad" edges, break up the SCC by removing all
             * of the "good" edges from the SCC graph and recomputing the SCC
             * (it should be broken up into smaller, more manageable SCCs)
             */
            if (badBackEdges.size() > 0) {
                if (DEBUG)
                    System.out.println("Bad back edges found.");
                if (DEBUG)
                    System.out.println("Bad edges: " + badBackEdges);

                if (goodBackEdges.size() == 0) { // Only Bad Back Edges found
                    if (DEBUG)
                        System.out.print("Zero good back edges found. Shifting DFS to another node: ");
                    // 1. Get the visit list of dfs
                    List visitList = (List) subSCCDFSTree.getNodes();
                    // 2. Take top item off and put to bottom
                    Object formerRootNode = visitList.remove(0); // take from head
                    visitList.add(formerRootNode); // add to tail
                    if (DEBUG)
                        System.out.println(visitList.get(0));
                    // 3. Call visit again with different list
                    // This will re-search the given SCC starting with the first node in the visitList
                    // To do the new search, we must first create a standard 
                    //   subgraph of this SCC. We cannot use the BasicDepthFirstSearchTree
                    //   object because the "back edges" are implicitly hidden.
                    // Must use "sccGraph" since this is the graph we've been 
                    //   removing edges from. (Don't use passed-in top-level graph)
                    AbstractEdifGraph subSCCGraph = (AbstractEdifGraph) sccGraph.getSubGraph(subSCCDFSTree.getNodes());
                    DepthFirstSearchForest newSearch = new DepthFirstSearchForest(subSCCGraph, visitList);
                    // 4. Put this on the stack
                    if (newSearch.getTrees().size() != 1) {
                        System.out.println("Warning (createDecomposeValidCutSet): new SCC forest is not size 1. (size="
                                + newSearch.getTrees().size() + ")");
                        //System.out.println("\tGood head: "+formerRootNode);
                        //System.out.println("\tBad head: "+visitList.get(0));
                        //System.out.println(newSearch.getTrees());
                        //sccGraph.toDotty("graph"+(NUMBER++)+".dot");
                    }
                    //throw new RuntimeException("createValidCutSet: Illegal SCC");    	    		
                    // Get the back edges of this SCC
                    BasicDepthFirstSearchTree new_tree = (BasicDepthFirstSearchTree) newSearch.getTrees().iterator()
                            .next();
                    s.push(new_tree);
                    if (DEBUG)
                        System.out.println("\tPushing subtree with " + new_tree.getNodes().size() + " nodes and "
                                + new_tree.getEdges().size() + " edges.");
                } else { // Both Good and Back Edges found
                    if (DEBUG)
                        System.out.println("Removing " + goodBackEdges.size() + " edges from graph with "
                                + sccGraph.getEdges().size() + " edges...");
                    sccGraph.removeEdges(goodBackEdges);
                    SCCDepthFirstSearch newSearch = new SCCDepthFirstSearch(sccGraph);
                    /*
                     * Since we are re-searching the entire top-level SCC, we
                     * should clear out the Stack before adding the sub-SCCs.
                     */
                    if (DEBUG)
                        System.out.println("Clearing stack");
                    s.clear();
                    // Add all subtrees to the Stack
                    for (Iterator i = newSearch.getTrees().iterator(); i.hasNext();) {
                        BasicDepthFirstSearchTree smallScc = (BasicDepthFirstSearchTree) i.next();
                        s.push(smallScc);
                        if (DEBUG)
                            System.out.println("\tPushing subtree with " + smallScc.getNodes().size() + " nodes and "
                                    + smallScc.getEdges().size() + " edges.");
                    }
                    if (DEBUG)
                        System.out.println("Found " + newSearch.getSingleNodes().size() + " SingleNodeSCCs: "
                                + newSearch.getSingleNodes());
                }
            } else { // Only Good Back Edges found
                if (DEBUG)
                    System.out.println("No bad back edges found!");
            }

        }

        return cuts;
    }

    /**
     * The highest fanin flip-flop is the flip-flop with the most nets leading into the D
     * input going five levels of instances backwards.
     * 
     * @param graph
     * @param sccs
     * @param arch
     * @return
     */
    public static Collection<EdifPortRef> createHighestFFFaninCutset(AbstractEdifGraph graph, SCCDepthFirstSearch sccs, NMRArchitecture arch) {
        Collection<EdifPortRef> cuts = new LinkedHashSet<EdifPortRef>();
        Stack<DepthFirstTree> s = new Stack<DepthFirstTree>();
        
        for (DepthFirstTree scc : sccs.getTrees()) {
            s.push(scc);
        }

        while (!s.empty()) {
            DepthFirstTree subSCCDFSTree = s.pop();

            // Find the FF node with the highest fan-in
            EdifCellInstance highFaninNode = null;
            EdifPortRef highFaninDInput = null;
            EdifCellInstanceEdge highFaninDEdge = null;
            int highFaninCount = 0;
            
            for (Iterator i = subSCCDFSTree.getNodes().iterator(); i.hasNext();) {
                Object node = i.next();
                
                if (node instanceof EdifCellInstance) {
                    EdifCellInstance eci = (EdifCellInstance) node;
                    // make sure this is a register
                    if (!XilinxTools.isRegisterCell(eci.getCellType()))
                        continue;
                    
                    // make sure it has a D input and find it
                    EdifPortRef eprD = null;
                    EdifCellInstanceEdge edgeD = null;
                    for (Object edge : graph.getInputEdges(node)) {
                        if (edge instanceof EdifCellInstanceEdge) {
                            EdifCellInstanceEdge eciEdge = (EdifCellInstanceEdge) edge;
                            EdifPortRef epr = eciEdge.getSinkEPR();
                            if (eciEdge.getSinkEPR().getPort().getName().equalsIgnoreCase("D")) {
                                eprD = epr;
                                edgeD = eciEdge;
                            }
                        }
                    }
                    if (eprD == null)
                        continue;
                    
                    int numPredecessors = computeFanin(node, graph, 5);
                    
                    // check to see if predecessors may be greater
                    if (numPredecessors > highFaninCount) {

                        // Find all bad cuts for this input
                        Collection inputEdges = new ArrayList();
                        inputEdges.add(edgeD);
                        Collection<Edge> badEdges = findBadEdges(inputEdges, arch);
                        if (badEdges.size() == 0) {
                            highFaninCount = numPredecessors;
                            highFaninDInput = eprD;
                            highFaninDEdge = edgeD;
                            highFaninNode = eci;
                        }
                    }
                }
            }
            if (highFaninNode != null) {
                // cut high fan-in node
                graph.removeEdge(highFaninDEdge);
                BasicGraph sccSubGraph = graph.getSubGraph(subSCCDFSTree.getNodes());
                cuts.add(highFaninDInput);
                
                // Recompute the SCCs from broken graph
                SCCDepthFirstSearch newSearch = new SCCDepthFirstSearch(sccSubGraph);
                for (DepthFirstTree scc : newSearch.getTrees()) {
                    s.push(scc);
                }
            }
        }
        return cuts;
    }
    
    /**
     * The highest fanin flip-flop is the flip-flop with the most nets leading into the D
     * input going five levels of instances backwards.
     * 
     * @param graph
     * @param sccs
     * @param arch
     * @return
     */
    public static Collection<EdifPortRef> createHighestFFFaninOutputCutset(AbstractEdifGraph graph, SCCDepthFirstSearch sccs, NMRArchitecture arch) {
        Collection<EdifPortRef> cuts = new LinkedHashSet<EdifPortRef>();
        Stack<DepthFirstTree> s = new Stack<DepthFirstTree>();

        for (DepthFirstTree scc : sccs.getTrees()) {
            s.push(scc);
        }

        while (!s.empty()) {
            DepthFirstTree subSCCDFSTree = s.pop();

            // Find the FF node with the highest fan-in
            EdifCellInstance highFaninNode = null;
            List<Edge> highFaninEdgesQ = null;
            Set<EdifPortRef> highFaninQEPRs = null;
            int highFaninCount = 0;
            
            for (Iterator i = subSCCDFSTree.getNodes().iterator(); i.hasNext();) {
                Object node = i.next();
                
                if (node instanceof EdifCellInstance) {
                    EdifCellInstance eci = (EdifCellInstance) node;
                    // make sure this is a register
                    if (!XilinxTools.isRegisterCell(eci.getCellType()))
                        continue;
                    
                    // Find the outputs
                    Set<EdifPortRef> eprsQ = new LinkedHashSet<EdifPortRef>();
                    List<Edge> edgesQ = new ArrayList<Edge>();
                    for (Object edge : graph.getOutputEdges(node)) {
                        if (edge instanceof EdifCellInstanceEdge) {
                            EdifCellInstanceEdge eciEdge = (EdifCellInstanceEdge) edge;
                            EdifPortRef epr = eciEdge.getSourceEPR();
                            eprsQ.add(epr);
                            edgesQ.add(eciEdge);
                        }
                    }
                    
                    if (eprsQ.size() != 0) {
                        int numPredecessors = computeFanin(node, graph, 5);
                        // check to see if predecessors may be greater
                        if (numPredecessors > highFaninCount) {
                            // Find bad cuts for the output of the flip flop
                            Collection<Edge> badEdgesQ = findBadEdges(edgesQ, arch);
                            if (badEdgesQ.size() == 0) {
                                highFaninCount = numPredecessors;
                                highFaninNode = eci;
                                highFaninEdgesQ = edgesQ;
                                highFaninQEPRs = eprsQ;
                            }
                        }
                    }
                }
            }
            if (highFaninNode == null) {
                
                List<Edge> highFaninEdgesQ2 = null;
                Set<EdifPortRef> highFaninQEPRs2 = null;
                int highFaninCount2 = 0;
                
                // do partial cuts here
                for (Iterator i = subSCCDFSTree.getNodes().iterator(); i.hasNext();) {
                    Object node = i.next();
                    
                    if (node instanceof EdifCellInstance) {
                        EdifCellInstance eci = (EdifCellInstance) node;
                        // make sure this is a register
                        if (!XilinxTools.isRegisterCell(eci.getCellType()))
                            continue;
                        
                        // Find the outputs
                        //Set<EdifPortRef> eprsQ = new LinkedHashSet<EdifPortRef>();
                        List<Edge> edgesQ = new ArrayList<Edge>();
                        for (Object edge : graph.getOutputEdges(node)) {
                            if (edge instanceof EdifCellInstanceEdge) {
                                EdifCellInstanceEdge eciEdge = (EdifCellInstanceEdge) edge;
                                EdifPortRef epr = eciEdge.getSourceEPR();
                                //eprsQ.add(epr);
                                edgesQ.add(eciEdge);
                            }
                        }
                        if (edgesQ.size() != 0) {
                            int numPredecessors = computeFanin(node, graph, 5);
                            // check to see if predecessors may be greater
                            if (numPredecessors > highFaninCount2) {
                                // Find bad cuts for the outputs of the register
                                Collection<Edge> badEdgesQ = findBadEdges(edgesQ, arch);
                                edgesQ.removeAll(badEdgesQ);
                                if (edgesQ.size() > 0) {
                                    highFaninCount2 = numPredecessors;
                                    highFaninEdgesQ2 = edgesQ;
                                    highFaninQEPRs2 = new LinkedHashSet<EdifPortRef>();
                                    for (Edge edge : edgesQ) {
                                        EdifCellInstanceEdge eciEdge = (EdifCellInstanceEdge) edge;
                                        highFaninQEPRs2.add(eciEdge.getSinkEPR());
                                    }
                                }
                            }
                        }
                    }   
                }
                // cut good cuts coming from high fanin node
                if (highFaninQEPRs != null) {
                    graph.removeEdges(highFaninEdgesQ2);
                    cuts.addAll(highFaninQEPRs2);
                }
                else {
                    throw new EdifRuntimeException("Error: no flip-flop D input with good cuts on the Q output found in the SCC. This cutset algorithm only works with feedback created with registers using a D input and Q output.");
                }
            }
            else {
                // cut high fan-in node
                graph.removeEdges(highFaninEdgesQ);
                cuts.addAll(highFaninQEPRs);
            }
            // Recompute the SCCs from broken graph
            BasicGraph sccSubGraph = graph.getSubGraph(subSCCDFSTree.getNodes());
            SCCDepthFirstSearch newSearch = new SCCDepthFirstSearch(sccSubGraph);
            for (DepthFirstTree scc : newSearch.getTrees()) {
                s.push(scc);
            }
        }
        return cuts;
    }

    public static int computeFanin(Object node, AbstractEdifGraph graph, int levels) {
        Stack nodes = new Stack();
        Set visited = new HashSet();
        Set<EdifNet> foundNets = new HashSet<EdifNet>();
        nodes.add(node);
        while (!nodes.isEmpty()) {
            Object currentNode = nodes.pop();
            visited.add(currentNode);
            if (currentNode instanceof EdifCellInstance) {
                EdifCellInstance currentEci = (EdifCellInstance) currentNode;
                for (Object edge: graph.getInputEdges(currentNode)) {
                    if (edge instanceof EdifCellInstanceEdge) {
                        EdifCellInstanceEdge eciEdge = (EdifCellInstanceEdge) edge;
                        foundNets.add(eciEdge.getNet());
                    }
                }
                if (nodes.size() < levels) {
                    for (Object predecessor : graph.getPredecessors(currentEci)) {
                        if (!visited.contains(predecessor))
                            nodes.push(predecessor);
                    }
                }
            }
        }
        return foundNets.size();
    }
    
    /**
     * Create a valid set of cuts which break all feedback in the given SCC.
     * This method breaks up the SCC little by little instead of generating the
     * entire cutset at once. Thus, cuts are determined in sets, breaking the
     * problem up when a full cutset is not immediately found. This algorithm
     * will cut the edges with the highest fanout first.
     * 
     * @param graph The top-level graph
     * @param scc A BasicDepthFirstSearchTree object representing an SCC in the
     * top-level graph
     * @param arch The TMRArchitecture, needed to determine good/bad cuts
     * @return A Collection of EdifEdges making up a valid set of cuts to break
     * all feedback in the given SCC
     */
    public static Collection<EdifPortRef> createDecomposeValidCutSetFanout(AbstractEdifGraph graph, // This should be the top-level, non-inverted graph
            SCCDepthFirstSearch sccs, NMRArchitecture arch) {

        Collection<EdifPortRef> globalCutsToMake = new LinkedHashSet<EdifPortRef>();
        Stack<DepthFirstTree> s = new Stack<DepthFirstTree>();

        //if (DEBUG) System.out.println("Performing initial SCC Decomposition");
        //SCCDepthFirstSearch sccs = new SCCDepthFirstSearch(graph);
        //if (DEBUG) System.out.println(sccs.getTrees().size()+" SCCs in original graph. All placed on stack");
        for (DepthFirstTree scc : sccs.getTrees()) {
            s.push(scc);
        }

        // Iterate until a valid cutset has been found.
        while (!s.empty()) {

            // Pop off DFS search tree (SCC)
            DepthFirstTree subSCCDFSTree = s.pop();
            if (DEBUG)
                System.out.println("Popping off a graph with " + subSCCDFSTree.getNodes().size()
                        + " nodes from a stack with " + (s.size() + 1) + " graphs");
            // Create a sub-graph of this SCC from the original graph
            BasicGraph sccSubGraph = graph.getSubGraph(subSCCDFSTree.getNodes());

            // Find the node that has the greatest fan out. Also find all of the fanout edges
            Object highFanoutNode = null;
            int highFanoutCount = 0;
            boolean hasBadCut = false;
            Collection outputEdges = null;

            for (Iterator i = sccSubGraph.getNodes().iterator(); i.hasNext();) {
                Object node = i.next();
                Collection nodeSuccessors = sccSubGraph.getSuccessors(node);
                int numSuccessors = nodeSuccessors.size();
                // check to see if successors may be greater
                if (numSuccessors > highFanoutCount) {

                    // Get out edges
                    outputEdges = sccSubGraph.getOutputEdges(node);

                    // Find all bad cuts for this output
                    Collection<EdifPortRefEdge> badEdges = getBadEdges(outputEdges, arch);
                    if (numSuccessors - badEdges.size() > highFanoutCount) {
                        if (badEdges.size() > 0) {
                            // Cut contains some bad edges. Cut non-bad inputs
                            outputEdges.removeAll(badEdges);
                            hasBadCut = true;
                        }
                        else {
                            hasBadCut = false;
                        }
                        highFanoutCount = outputEdges.size();
                        highFanoutNode = node;
                    }
                }
            }
            if (DEBUG)
                System.out.println("\tNode " + highFanoutNode + " has fanout of " + highFanoutCount
                        + " chosen for cutting");
            
            // Break up the SCC by removing all of the "good" edges
            outputEdges = sccSubGraph.getOutputEdges(highFanoutNode);
            sccSubGraph.removeEdges(outputEdges);
            if (hasBadCut) {
                if (DEBUG)
                    System.out.println("This OUTPUT has a bad cut");
                // add all sink EPRs
                for (Iterator j = outputEdges.iterator(); j.hasNext();) {
                    // add sink
                    EdifPortRefEdge edge = (EdifPortRefEdge) j.next();
                    globalCutsToMake.add(edge.getSinkEPR());
                }
            } else {
                // add single output EPR
                globalCutsToMake.add((EdifPortRef) highFanoutNode);
            }

            // Recompute the SCCs from broken graph
            SCCDepthFirstSearch newSearch = new SCCDepthFirstSearch(sccSubGraph);

            if (newSearch.getTrees().size() == 0) {
                if (DEBUG)
                    System.out.println("\tCutting output removes all feedback in SCC. No graph pushed on stack.");
            } else {
                if (DEBUG)
                    System.out.println("\tCutting output breaks SCC into " + newSearch.getTrees().size() + " sccs");
                // Add all subtrees with size > 1 to the Stack
                for (DepthFirstTree scc : newSearch.getTrees()) {
                    s.push(scc);
                    if (DEBUG)
                        System.out.println("\tPushing subtree with " + scc.getNodes().size() + " nodes and "
                                + scc.getEdges().size() + " edges.");
                }
            }
        }
        return globalCutsToMake;
    }

    /**
     * Performs a cut by choosing outputs of Flip-flops first.
     */
    public static Collection<EdifPortRef> createDecomposeValidCutSetFFFanout(AbstractEdifGraph graph, // This should be the top-level, non-inverted graph
            SCCDepthFirstSearch sccs, NMRArchitecture arch) {

        /*
         * Collection of the cuts to make. This collection will grow through
         * each iteration.
         */
        Collection<EdifPortRef> globalCutsToMake = new LinkedHashSet<EdifPortRef>();
        /*
         * A stack of DFS trees. As cuts are made, SCCs are broken up into
         * smaller SCCs. This stack contains the remaining SCCs that resulting
         * from a cut. The algorithm will continue until all SCCs are cut.
         */
        Stack<DepthFirstTree> s = new Stack<DepthFirstTree>();

        //if (DEBUG) System.out.println("Performing initial SCC Decomposition");
        //SCCDepthFirstSearch sccs = new SCCDepthFirstSearch(graph);
        //if (DEBUG) System.out.println(sccs.getTrees().size()+" SCCs in original graph. All placed on stack");

        // Push all initial SCCs onto the stack. 
        for (DepthFirstTree scc : sccs.getTrees()) {
            s.push(scc);
        }

        /*
         * Iterate until a valid cutset has been found (i.e. no SCCs left in the
         * graph)
         */
        while (!s.empty()) {

            // Pop off top DFS search tree (SCC)
            DepthFirstTree subSCCDFSTree = s.pop();
            if (DEBUG)
                System.out.println("Popping off a graph with " + subSCCDFSTree.getNodes().size()
                        + " nodes from a stack with " + (s.size() + 1) + " graphs");
            // Create a sub-graph of this SCC from the original graph
            BasicGraph sccSubGraph = graph.getSubGraph2(subSCCDFSTree.getNodes());
            //BasicGraph  sccSubGraph2= graph.getSubGraph2(subSCCDFSTree.getNodes());

            // Find the node that has the greatest fan out. Also find all of the fanout edges
            Object highFanoutNode = null;
            int highFanoutCount = 0;
            boolean hasBadCut = false;
            Collection outputEdges = null;

            /*
             * Pass #1: Iterate over all nodes in the graph and find those nodes
             * that correspond to FFs. Find the FF that has the greatest fan out
             * and choose this net for cutting.
             */
            for (Iterator i = sccSubGraph.getNodes().iterator(); i.hasNext();) {
                EdifPortRef epr = (EdifPortRef) i.next();
                EdifCellInstance driving_cell = epr.getCellInstance();
                // Check to see if cell is a FF. If not, skip
                // TODO: do not hard code to Xilinx here
                if (driving_cell == null
                        || !edu.byu.ece.edif.arch.xilinx.XilinxTools.isRegisterCell(driving_cell.getCellType()))
                    continue;

                Collection nodeSuccessors = sccSubGraph.getSuccessors(epr);
                int numSuccessors = nodeSuccessors.size();
                // check to see if successors may be greater
                if (numSuccessors > highFanoutCount) {

                    // Get out edges
                    outputEdges = sccSubGraph.getOutputEdges(epr);

                    // Find all bad cuts for this output
                    Collection<EdifPortRefEdge> badEdges = getBadEdges(outputEdges, arch);
                    if (numSuccessors - badEdges.size() > highFanoutCount) {
                        if (badEdges.size() > 0) {
                            // Cut contains some bad edges. Cut non-bad inputs
                            outputEdges.removeAll(badEdges);
                            hasBadCut = true;
                        }
                        else {
                            hasBadCut = false;
                        }
                        highFanoutCount = outputEdges.size();
                        highFanoutNode = epr;
                    }
                }
            }
            // See if a high fanout node is found. If not, no FFs were found.
            if (highFanoutNode == null) {
                for (Iterator i = sccSubGraph.getNodes().iterator(); i.hasNext();) {
                    Object node = i.next();
                    Collection nodeSuccessors = sccSubGraph.getSuccessors(node);
                    int numSuccessors = nodeSuccessors.size();
                    // check to see if succesors may be greater
                    if (numSuccessors > highFanoutCount) {

                        // Get out edges
                        outputEdges = sccSubGraph.getOutputEdges(node);

                        // Find all bad cuts for this output
                        Collection<EdifPortRefEdge> badEdges = getBadEdges(outputEdges, arch);
                        if (numSuccessors - badEdges.size() > highFanoutCount) {
                            if (badEdges.size() > 0) {
                                // Cut contains some bad edges. Cut non-bad inputs
                                outputEdges.removeAll(badEdges);
                                hasBadCut = true;
                            }
                            else {
                                hasBadCut = false;
                            }
                            highFanoutCount = outputEdges.size();
                            highFanoutNode = node;
                        }
                    }
                }
            }

            if (DEBUG)
                System.out.println("\tNode " + highFanoutNode + " has fanout of " + highFanoutCount
                        + " chosen for cutting");

            // Break up the SCC by removing all of the "good" edges
            outputEdges = sccSubGraph.getOutputEdges(highFanoutNode);
            sccSubGraph.removeEdges(outputEdges);
            if (hasBadCut) {
                if (DEBUG)
                    System.out.println("This OUTPUT has a bad cut");
                // add all sink EPRs
                for (Iterator j = outputEdges.iterator(); j.hasNext();) {
                    // add sink
                    EdifPortRefEdge edge = (EdifPortRefEdge) j.next();
                    globalCutsToMake.add(edge.getSinkEPR());
                }
            } else {
                // add single output EPR
                globalCutsToMake.add((EdifPortRef) highFanoutNode);
            }

            // Recompute the SCCs from broken graph
            SCCDepthFirstSearch newSearch = new SCCDepthFirstSearch(sccSubGraph);

            if (newSearch.getTrees().size() == 0) {
                if (DEBUG)
                    System.out.println("\tCutting output removes all feedback in SCC. No graph pushed on stack.");
            } else {
                if (DEBUG)
                    System.out.println("\tCutting output breaks SCC into " + newSearch.getTrees().size() + " sccs");
                // Add all subtrees with size > 1 to the Stack
                for (DepthFirstTree scc : newSearch.getTrees()) {
                    s.push(scc);
                    if (DEBUG)
                        System.out.println("\tPushing subtree with " + scc.getNodes().size() + " nodes and "
                                + scc.getEdges().size() + " edges.");
                }
            }
        }
        return globalCutsToMake;
    }

    protected static Collection<EdifPortRefEdge> getBadEdges(Collection<EdifPortRefEdge> edges, NMRArchitecture arch) {
        Collection<EdifPortRefEdge> badEdges = new ArrayList<EdifPortRefEdge>();
        for (Iterator j = edges.iterator(); j.hasNext();) {
            EdifPortRefEdge edge = (EdifPortRefEdge) j.next();
            EdifNet net = edge.getNet();
            if (arch.isBadCutConnection(edge.getSourceEPR(), edge.getSinkEPR()) || EdifReplicationPropertyReader.isDoNotRestoreOrDoNotDetectLocation(net)) {
                //if (DEBUG) System.out.println("Bad Edge Found:"+edge);
                badEdges.add(edge);
            }
        }
        return badEdges;
    }

    /**
     * Create a valid cut set from scratch. Assuming that there is a valid cut
     * set then there exists a depth-first search tree in which all back edges
     * are not tagged as "bad". There are N different depth search trees for an
     * SCC where N is the number of nodes in the tree. Each different tree is
     * created by using a different node as the "start" node.
     * 
     * @deprecated This method does NOT examine all cutset possibilities; use
     * {@link #createDecomposeValidCutSet(AbstractEdifGraph, BasicDepthFirstSearchTree, NMRArchitecture)}
     * instead.
     */
    @Deprecated
    public static Collection createValidCutSet(DirectedGraph graph, BasicDepthFirstSearchTree tree, NMRArchitecture arch) {

        // 1. Create a connectivity sub-set
        DirectedGraph newGraph = graph.getSubGraph(tree.getNodes());
        //System.out.println(newGraph);

        // 2. Iterate over each element in the tree
        for (Iterator i = tree.getNodes().iterator(); i.hasNext();) {

            // For each iteration, create a new depth-first
            // search using the given element as the "start".
            Object root = i.next();
            if (DEBUG)
                System.out.print("\tFind new cutset with root " + root);

            /*
             * Set the visit order of the depth first search such that the root
             * is first.
             */
            ArrayList visitOrder = new ArrayList(tree.getNodes());
            visitOrder.remove(root);
            visitOrder.add(0, root);

            // Perform new depth first search
            DepthFirstSearchForest dfs = new DepthFirstSearchForest(newGraph, visitOrder);

            /*
             * The dfs must have only one tree. If not, it is not an SCC
             */
            if (dfs.getTrees().size() != 1)
                System.out.println("Warning (createValidCutSet): new SCC forest is not size 1: " + dfs.getTrees());
            //throw new RuntimeException("createValidCutSet: Illegal SCC");

            // Get the back edges of this SCC
            BasicDepthFirstSearchTree new_tree = (BasicDepthFirstSearchTree) dfs.getTrees().iterator().next();
            Collection backEdges = new_tree.getBackEdges();
            if (!hasBadEdge(backEdges, arch)) {
                if (DEBUG)
                    System.out.println(" - found");
                return backEdges;
            }
            if (DEBUG) {
                System.out.println(" - failed: Back edges:");
                for (Iterator j = backEdges.iterator(); j.hasNext();) {
                    Edge edge = (Edge) j.next();
                    System.out.print("\t" + edge);
                    if (EdifCellBadCutGroupings.isBadCutEdge(edge, arch))
                        System.out.println(" - Bad");
                    else
                        System.out.println();
                }
            }

        }

        System.out.println("No set of back edges that provides a valid cut");
        System.out.println(tree);

        /*
         * All depth first trees have been explored and none could be found in
         * which there isn't a bad cut. Throw an exception.
         *///throw new RuntimeException("No set of back edges that provides a valid cut");
        return new ArrayList(1);
    }

    /**
     * Given a Strongly-Connected Component (SCC), identify a set of edges in
     * the graph that, when cut, will "break" or decompose the SCC into one or
     * more trees, each of which is <i>not</i> an SCC.
     * 
     * @param graph The given SCC
     * @param sccTree A valid topological order of the SCC
     * @return An SCCDepthFirstSearch of the resulting {@link AbstractEdifGraph}.
     * Why does this method require the entire graph? Why isn't the sccTree
     * enough? -James C.
     */
    public static SCCDepthFirstSearch decomposeSCC(AbstractEdifGraph graph, BasicDepthFirstSearchTree sccTree) {

        // 1. Create a smaller graph based on just the SCC.
        AbstractEdifGraph smallGraph = (AbstractEdifGraph) graph.getSubGraph(sccTree.getNodes());

        // 2. Perform SCC decomposition on sub graph
        if (DEBUG)
            System.out.println("Performing SCC decomposition for SCC");
        // EdifCellDFS2 smallSCC = sccDecomp(smallGraph);
        DepthFirstSearchForest smallSCC = new SCCDepthFirstSearch(smallGraph);

        if (smallSCC.getTrees().size() != 1)
            throw new RuntimeException("SCC sub graph produces more than one SCC");
        BasicDepthFirstSearchTree smallTree = (BasicDepthFirstSearchTree) smallSCC.getTrees().iterator().next();

        // 3. Determine cut link
        AbstractEdifGraph newGraph = (AbstractEdifGraph) smallSCC.getGraph();
        Edge cutLink = smallTree.getLongestBackEdge();
        System.out.println("Cut edge = " + cutLink);

        // 4. Cut the back link from the graph.
        if (DEBUG)
            System.out.println("Edges before cut = " + newGraph.getEdges().size());
        boolean removed = newGraph.removeEdge(cutLink);
        if (DEBUG) {
            System.out.println(" Edges after cut = " + newGraph.getEdges().size());
            System.out.println("Cut graph\r\n" + newGraph);
        }
        if (!removed)
            throw new RuntimeException("Edge NOT removed");

        // 5. Perform new SCC decomposition
        SCCDepthFirstSearch cutdfs = new SCCDepthFirstSearch(newGraph);

        return cutdfs;
    }

    /**
     * Finds and returns any "bad" edges in the collection of edges. Uses the
     * TMRArchitecture for determining a bad cut edge.
     * 
     * @param edges
     * @param arch
     * @return A Collection of "bad" Edge objects in the given Collection Edge
     * objects.
     */
    public static Collection<Edge> findBadEdges(Collection<Edge> edges, NMRArchitecture arch) {
        Collection<Edge> badCutEdges = new ArrayList<Edge>();
        for (Edge edge : edges) {
            EdifNet net = null;
            if (edge instanceof EdifCellInstanceEdge) {
                net = ((EdifCellInstanceEdge) edge).getNet();
            }
            if (EdifCellBadCutGroupings.isBadCutEdge(edge, arch) || (net != null && EdifReplicationPropertyReader.isDoNotRestoreOrDoNotDetectLocation(net)))
                badCutEdges.add(edge);
        }
        return badCutEdges;
    }

    /**
     * Determines whether there are any "bad" edges in the collection of edges.
     * Uses the TMRArchitecture for determining a bad cut.
     * 
     * @param edges
     * @param arch
     * @return
     */
    public static boolean hasBadEdge(Collection edges, NMRArchitecture arch) {
        for (Iterator i = edges.iterator(); i.hasNext();) {
            Edge edge = (Edge) i.next();
            if (EdifCellBadCutGroupings.isBadCutEdge(edge, arch))
                return true;
        }
        return false;
    }

    /**
     * Checks whether the given graph is a strongly-connected component or not.
     * Warning: This method is terribly slow and inefficient. The object of
     * creating the method was to have a sure way of judging a graph as an SCC
     * or not. Thus it was designed to be accurate rather than efficient.
     * 
     * @param graph The potential SCC (a DirectedGraph)
     * @return true if the graph is an SCC, false otherwise
     */
    public static boolean isSCC(DirectedGraph graph) {
        // Check to make sure that every node is reachable from every other node
        // - A DFS rooted at each node should discover every other node
        for (Object node : graph.getNodes()) {
            BasicDepthFirstSearchTree dfstree = new BasicDepthFirstSearchTree(null, graph, node);
            System.out.println("DFS for node " + node + ": " + dfstree);
            for (Object checkNode : graph.getNodes())
                if (!dfstree.containsNode(checkNode)) {
                    if (DEBUG)
                        System.out.println("Warning: Graph is not an SCC. Could not reach node " + checkNode
                                + " from node " + node);
                    return false;
                }
        }
        // If not false, must be true!
        return true;
    }

    public static Collection smallestCutSet(DirectedGraph graph, BasicDepthFirstSearchTree tree, NMRArchitecture arch) {

        //int fewestCuts = -1;
        //Collection smallestCutSet = null;

        // 1. Create a connectivity sub-set
        DirectedGraph newGraph = graph.getSubGraph(tree.getNodes());
        //System.out.println(newGraph);

        // 2. Iterate over each element in the tree
        for (Iterator i = tree.getNodes().iterator(); i.hasNext();) {

            /*
             * For each iteration, create a new depth-first search using the
             * given element as the "start".
             */
            Object root = i.next();
            if (DEBUG)
                System.out.print("\tFind new cutset with root " + root);

            /*
             * Set the visit order of the depth first search such that the root
             * is first.
             */
            ArrayList visitOrder = new ArrayList(tree.getNodes());
            visitOrder.remove(root);
            visitOrder.add(0, root);

            // Perform new depth first search
            DepthFirstSearchForest dfs = new DepthFirstSearchForest(newGraph, visitOrder);

            /*
             * The dfs must have only one tree. If not, it is not an SCC
             */
            if (dfs.getTrees().size() != 1)
                System.out.println("Warning (smallestCutSet): new SCC forest is not size 1: " + dfs.getTrees());
            //throw new RuntimeException("createValidCutSet: Illegal SCC");

            // Get the back edges of this SCC
            BasicDepthFirstSearchTree new_tree = (BasicDepthFirstSearchTree) dfs.getTrees().iterator().next();
            Collection backEdges = new_tree.getBackEdges();
            if (hasBadEdge(backEdges, arch))
                continue;

            if (!hasBadEdge(backEdges, arch)) {
                if (DEBUG)
                    System.out.println(" - found");
                return backEdges;
            }
            if (DEBUG) {
                System.out.println(" - failed: Back edges:");
                for (Iterator j = backEdges.iterator(); j.hasNext();) {
                    Edge edge = (Edge) j.next();
                    System.out.print("\t" + edge);
                    if (EdifCellBadCutGroupings.isBadCutEdge(edge, arch))
                        System.out.println(" - Bad");
                    else
                        System.out.println();
                }
            }

        }

        System.out.println("No set of back edges that provides a valid cut");
        System.out.println(tree);

        /*
         * All depth first trees have been explored and none could be found in
         * which there isn't a bad cut. Throw an exception.
         *///throw new RuntimeException("No set of back edges that provides a valid cut");
        return new ArrayList(1);
    }

//    /**
//     * Determines how many SCCs can be triplicated using the given capacity
//     * object. This method will explore SCC decomposition to include as many
//     * SCCs as possible.
//     * 
//     * @param sccDFS The SCCDepthFirstSearch structure of the edif circuit
//     * @param arch The NMRArchitecture
//     * @param capacity The capacity object to use for testing SCC inclusion.
//     * @param allowSCCDecomposition This flag indicates that SCCs will be
//     * decomposed if they don't fit. If false, any SCCs that don't fit are
//     * skipped.
//     * @param sortType The method of SCC sorting used (3 = topological sorting)
//     * @param edgeCutSet An empty List for returning Edge objects that must be
//     * cut.
//     * @return true if ALL instances were triplicated, false otherwise
//     */
//    public static boolean tmrSCCsUsingSCCDecomposition(SCCDepthFirstSearch sccDFS, NMRArchitecture arch,
//            ReplicationUtilizationTracker capacity, boolean allowSCCDecomposition, int sortType) {
//        return nmrSCCsUsingSCCDecomposition(sccDFS, arch, capacity, allowSCCDecomposition, sortType, 3);
//    }

    /**
     * Determines how many SCCs can be triplicated using the given capacity
     * object. This method will explore SCC decomposition to include as many
     * SCCs as possible.
     * 
     * @param sccDFS The SCCDepthFirstSearch structure of the edif circuit
     * @param arch The NMRArchitecture
     * @param capacity The capacity object to use for testing SCC inclusion.
     * @param allowSCCDecomposition This flag indicates that SCCs will be
     * decomposed if they don't fit. If false, any SCCs that don't fit are
     * skipped.
     * @param sortType The method of SCC sorting used (3 = topological sorting)
     * @param edgeCutSet An empty List for returning Edge objects that must be
     * cut.
     * @param replicationFacotor The replication factor to use (3=tmr, 2=dwc,
     * etc.)
     * @return true if ALL instances were triplicated, false otherwise
     */
    public static boolean nmrSCCsUsingSCCDecomposition(SCCDepthFirstSearch sccDFS, NMRArchitecture arch,
            ReplicationUtilizationTracker capacity, boolean allowSCCDecomposition, int sortType,
            ReplicationType replicationType, boolean override) {

        AbstractEdifGraph graph = (AbstractEdifGraph) sccDFS.getGraph();
        boolean allSCCInstancesTriplicated = true;

        /*
         * This is an ordered list of SCCs that need to be evaluated. Each item
         * in this Collection is of type EdifSCCInstanceCollection. It is
         * initialized by the first SCC decomposition step.
         */
        List sccList = new ArrayList();

        /*
         * When allowing SCCs to be decomposed, some single-node SCCs may
         * result. These will be stored in this List and handled after all SCCs.
         */
        // TODO: Should these be added at the end or during?
        List singleNodeSCCs = new ArrayList();

        // Get list of SCC trees from sccDFS
        List sortedSCCList;
        if (sortType == 1)
            sortedSCCList = sccDFS.getDescendingTreeList();
        else if (sortType == 2)
            sortedSCCList = sccDFS.getAscendingTreeList();
        else
            sortedSCCList = sccDFS.getTopologicallySortedTreeList();

        sccList.addAll(sortedSCCList);

        /*
         * Iterate until either the queue of SCCs is empty or you run out of
         * room.
         */
        while (sccList.size() > 0) {

            if (DEBUG)
                System.out.print("(" + sccList.size() + ") ");

            // Get the first SCC in the list and remove it
            BasicDepthFirstSearchTree scc = (BasicDepthFirstSearchTree) sccList.get(0);
            sccList.remove(scc);

            Collection sccNodes = scc.getNodes();

            if (DEBUG) {
                System.out.print("Evaluating SCC of size:" + sccNodes.size() + " . . .");
                System.out.println();
                for (Iterator i = sccNodes.iterator(); i.hasNext();) {
                    System.out.println("\t" + i.next());
                }
            }

            /*
             * Do not include non-EdifCellInstances (e.g. top-level ports) in
             * the Collection. Extract EdifCellInstances from
             * EdifCellInstanceCollection objects.
             */
            Collection sccInstances = new ArrayList();
            for (Object node : sccNodes) {
                if (node instanceof EdifCellInstance) {
                    sccInstances.add(node);
                } else if (node instanceof EdifCellInstanceCollection) {
                    sccInstances.addAll((EdifCellInstanceCollection) node);
                } else {
                    if (DEBUG)
                        System.out.println("WARNING: " + "tmrSCCsUsingSCCDecomposition NOT triplicating" + node
                                + "of type " + node.getClass());
                }
            }

            // Try to add the SCC.
            try {
            	capacity.addToTrackerAtomic(sccInstances, replicationType, override);
                //capacity.nmrInstancesAtomic(sccInstances, replicationFactor);
                if (DEBUG) {
                    System.out.println(" SCC fits");
                    System.out.println(capacity.toString());
                }

                // SCC fits - determine the location of the voter cuts
                /*Collection<Edge> cuts = createDecomposeValidCutSet(graph, scc, arch);
                edgeCutSet.addAll(cuts);
                for (Edge cut : cuts) {
                    // Each cut EdifNet will insert a voter
                    capacity.incrementVoterCount();
                }*/
            }
            // Request for same instance twice. This shouldn't happen.
            catch (DuplicateNMRRequestException e1) {
                //throw new EdifRuntimeException(e1.toString());
            	// TODO: What if this DOES happen? We should somehow force the
            	//   triplication of this SCC since we don't care if another
            	//   part of the program has asked for a piece of our SCC to be
            	//   duplicated. -BHP
                continue;
            }
            /*
             * Can't fit the entire SCC. Decompose this SCC and add subtrees to
             * the end of the SCC list.
             */
            catch (OverutilizationEstimatedStopException e2) {
                allSCCInstancesTriplicated = false;
                if (DEBUG)
                    System.out.println("SCC does not fit");

                if (allowSCCDecomposition) {
                    if (DEBUG)
                        System.out.println("\tDecomposing SCC...");
                    _decomposeSCC(graph, sccList, singleNodeSCCs, scc);
                }
            }
            /*
             * One or more of the instances in this SCC will not fit. Add all
             * the instances that DO fit and skip those that don't.
             */
            // TODO: Is there a difference between soft logic and hard logic
            //   (limits) here? The new "nmrInstancesAsManyAsPossible" method
            //   skips both soft and hard logic exceptions and replicates as
            //   much as possible. The following code must change to reflect
            //   this. -BHP
            catch (OverutilizationHardStopException e3) {
                allSCCInstancesTriplicated = false;
                if (DEBUG)
                    System.out.println("Received HardStopException. Skipping problem instances.");
                /*
                 * TODO: Should we print out this message? It could be helpful
                 * to the user to know what instances have been skipped!
                 */
                //System.out.println(e3);
                try {
                    // Skip Hard Stops
                	capacity.addToTrackerAsManyAsPossible(sccInstances, replicationType, override);
                    //capacity.nmrInstancesAsManyAsPossible(sccInstances, replicationFactor);
                    // SCC fits - determine the location of the voter cuts    			

//                    Collection<Edge> cuts = createDecomposeValidCutSet(graph, scc, arch);
//                    edgeCutSet.addAll(cuts);
//                    for (Edge cut : cuts) {
//                        // Each cut EdifNet will insert a voter
//                        capacity.incrementVoterCount();
//                    }
                } catch (DuplicateNMRRequestException e4) {
                    continue;
                } catch (OverutilizationEstimatedStopException e5) {
                    allSCCInstancesTriplicated = false;
                    /*
                     * DeviceUtilizationTracker says to stop adding instances
                     * for triplication.
                     */
                    if (DEBUG)
                        System.out.println("SCC does not fit");

                    if (allowSCCDecomposition) {
                        if (DEBUG)
                            System.out.println("\tDecomposing SCC...");
                        _decomposeSCC(graph, sccList, singleNodeSCCs, scc);
                    }

                } catch (OverutilizationHardStopException e6) {
                    /*
                     * !!! Shouldn't get here because we called tmrInstances
                     * with the flag set to skip hard stops
                     */
                    throw new EdifRuntimeException("ERROR: Shouldn't get here! " + e6);
                }
            }

        }

        // Attempt to triplicate leftover single-node "SCCs".
        for (Iterator i = singleNodeSCCs.iterator(); i.hasNext();) {
            EdifCellInstance instance = (EdifCellInstance) i.next();
            try {
                if (DEBUG)
                    System.out.println("Adding instance: " + instance);
                capacity.addToTracker(instance, replicationType, override);
                //capacity.nmrInstance(instance, replicationFactor);
            } catch (DuplicateNMRRequestException e4) {
                // Skip
                continue;
            } catch (OverutilizationEstimatedStopException e5) {
                allSCCInstancesTriplicated = false;
                /*
                 * DeviceUtilizationTracker says to stop adding instances for
                 * triplication.
                 */
                if (DEBUG)
                    System.out.println("Received EstimatedStopException "
                            + "in tmrSCCsUsingSCCDecomposition. Halting triplication.");
                break;
            } catch (OverutilizationHardStopException e6) {
                allSCCInstancesTriplicated = false;
                // Skip Hard Stops--can't add this instance
                if (DEBUG)
                    System.out.println("\tCan't add instance. No " + instance.getType() + "'s left.");
                continue;
            }
        }

        return allSCCInstancesTriplicated;
    }

//    /**
//     * Determines how many SCCs can be triplicated using the given capacity
//     * object. This method will explore SCC decomposition to include as many
//     * SCCs as possible. Uses the
//     * {@linkplain #DEFAULT_SCC_SORT_TYPE default SCC sort type}, 3.
//     * 
//     * @param sccDFS The SCCDepthFirstSearch structure of the edif circuit
//     * @param arch The NMRArchitecture
//     * @param capacity The capacity object to use for testing SCC inclusion.
//     * @param allowSCCDecomposition This flag indicates that SCCs will be
//     * decomposed if they don't fit. If false, any SCCs that don't fit are
//     * skipped.
//     * @param edgeCutSet An empty List for returning Edge objects that must be
//     * cut.
//     * @return A List of Edge objects that must be cut.
//     */
//    public static boolean tmrSCCsUsingSCCDecomposition(SCCDepthFirstSearch sccDFS, NMRArchitecture arch,
//            ReplicationUtilizationTracker capacity, boolean allowSCCDecomposition) {
//        return tmrSCCsUsingSCCDecomposition(sccDFS, arch, capacity, allowSCCDecomposition, DEFAULT_SCC_SORT_TYPE,
//                edgeCutSet);
//    }

    /**
     * Identify a valid set of edges that completely removes feedback in the
     * given DFSTree. Assume that the tree is a SCC.
     * 
     * @param graph
     * @param tree
     * @param arch
     * @return A Collection of
     */
    public static Collection validCutSet(DirectedGraph graph, BasicDepthFirstSearchTree tree, NMRArchitecture arch) {

        /*
         * See if any of the edges in the back-edge set are bad. If not, return
         * this set.
         */
        Collection backEdges = tree.getBackEdges();
        if (!hasBadEdge(backEdges, arch))
            return backEdges;

        return createValidCutSet(graph, tree, arch);
    }

    public static Collection validCutSet(SCCDepthFirstSearch sccs, NMRArchitecture arch) {

        ArrayList cuts = new ArrayList();
        for (Iterator i = sccs.getTrees().iterator(); i.hasNext();) {
            BasicDepthFirstSearchTree scc = (BasicDepthFirstSearchTree) i.next();
            cuts.addAll(validCutSet(sccs.getGraph(), scc, arch));
        }
        return cuts;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         protected variables               ////

    protected static boolean DEBUG = false;

    ///////////////////////////////////////////////////////////////////
    ////                         private methods                   ////

    /**
     * Internal helper method for tmrSCCsUsingSCCDecomposition method. This
     * method breaks up an SCC into smaller SCCs and single nodes. These are
     * added to the internal structures used by the parent method.
     * 
     * @param graph The top-level graph which contains this SCC
     * @param sccList The List of SCCs to triplicate
     * @param singleNodeSCCs The List of single nodes to triplicate later
     * @param scc The SCC to decompose
     */
    private static void _decomposeSCC(AbstractEdifGraph graph, List sccList, List singleNodeSCCs,
            BasicDepthFirstSearchTree scc) {
        List descendingSCCList;
        SCCDepthFirstSearch smallSCCs = decomposeSCC(graph, scc);

        // Add all Sub-SCCs to SCC List
        descendingSCCList = smallSCCs.getDescendingTreeList();
        if (DEBUG) {
            for (Iterator j = descendingSCCList.iterator(); j.hasNext();) {
                BasicDepthFirstSearchTree smallScc = (BasicDepthFirstSearchTree) j.next();
                System.out.println("\tAdding subtree with " + smallScc.getNodes().size() + " nodes and "
                        + smallScc.getEdges().size() + " edges.");
            }
        }
        // Add sub SCCs to the end of the list (give priority to full SCCs).
        // TODO: Make this a parameter?
        sccList.addAll(descendingSCCList);

        /*
         * Add any Single-Node SCCs as well. These will be added to triplication
         * later.
         */
        if (DEBUG) {
            for (Iterator j = smallSCCs.getSingleNodes().iterator(); j.hasNext();) {
                Object node = j.next();
                System.out.println("\tFound single-node subtree: " + node);
            }
        }
        singleNodeSCCs.addAll(smallSCCs.getSingleNodes());
    }

}
