/*
 * Graph representing part of an EdifCell based on a specific EdifCellInstance.
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
package edu.byu.ece.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import edu.byu.ece.edif.arch.xilinx.XilinxMergeParser;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.tools.flatten.NewFlattenedEdifCell;
import edu.byu.ece.edif.util.graph.AbstractEdifGraph;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;

/**
 * Allows the user to create a Graph representing a portion of an EdifCell based
 * on a specific EdifCellInstance in that EdifCell.
 * 
 * @author Brian Pratt
 */
public class DebugSubGraph {

    // Recursive helper method
    public static void addNeighbors(Object node, int depth, BasicGraph graph, Collection neighbors, boolean pred,
            boolean succ) {
        neighbors.add(node);

        //if (DEBUG) System.out.println("addNeighbors("+node+", "+depth+", "+pred+", "+succ+"):");

        // Recursive base case
        if (depth <= 0) {
            //if (DEBUG) System.out.println("Ending recursion");
            return;
        }

        // Get predecessors and successors
        Collection predSucc = new LinkedHashSet();
        if (pred)
            predSucc.addAll(graph.getPredecessors(node));
        if (succ)
            predSucc.addAll(graph.getSuccessors(node));
        //if (DEBUG) System.out.println("Found " + predSucc.size() + " predecessors/successors of node "+node);

        // Get neighbors of all these, but with 1 level of depth less
        for (Iterator i = predSucc.iterator(); i.hasNext();) {
            Object neighbor = i.next();
            addNeighbors(neighbor, depth - 1, graph, neighbors, pred, succ);
        }
    }

    public static Collection getNeighbors(EdifCellInstance eci, int depth, BasicGraph graph, boolean bidirectional) {
        Collection neighbors = new LinkedHashSet();
        neighbors.add(eci);

        if (bidirectional) {
            // All neighbors should crawl the graph in both directions
            addNeighbors(eci, depth, graph, neighbors, true, true);
        } else {
            // Predecessors should only crawl to predecessors
            addNeighbors(eci, depth, graph, neighbors, true, false);
            // Successors should only crawl to Successors
            addNeighbors(eci, depth, graph, neighbors, false, true);
        }

        return neighbors;
    }

    public static AbstractEdifGraph getSubGraph(String eciName, int depth, EdifCell cell, boolean bidirectional,
            boolean flatten) throws InvalidEdifNameException {

        // Flatten design
        if (flatten) {
            if (DEBUG)
                System.out.println("Flattening top level EdifCell...");
            EdifCell flat_cell = null;
            try {
                flat_cell = new NewFlattenedEdifCell(cell);
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            }
            cell = flat_cell;
        }

        // Find desired EdifCellInstance
        // TODO: Should I let the user specify the pre-flattened name of the ECI
        //   and auto-generate graphs for each instantiation in the flat cell?
        if (DEBUG)
            System.out.println("Searching for instance " + eciName + "...");
        EdifCellInstance eci = cell.getCellInstance(eciName);
        if (eci == null) {
            if (DEBUG)
                System.out.println("Could not find EdifCellInstance: " + eciName);
            return null;
        }

        // Create the graph representing the EdifCell
        EdifCellInstanceGraph graph = new EdifCellInstanceGraph(cell, true);

        // Create a Collection of this instances neighbors, up to "depth"
        //   in all directions.
        Collection neighbors = getNeighbors(eci, depth, graph, bidirectional);

        // Create subgraph and return
        return graph.getSubGraph(neighbors);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        // Read in design and arguments
        EdifCell cell = XilinxMergeParser.parseAndMergeXilinx(args);

        int depth = DEFAULT_DEPTH;
        boolean bidirectional = false;
        boolean flatten = true;
        String eciName = null;
        String dottyFileName = new String("subgraph.dot");
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-n")) {
                // Depth should be next value
                depth = Integer.parseInt(args[i + 1]);
                // Skip past next value
                i++;
            } else if (args[i].equals("-o")) {
                dottyFileName = args[i + 1];
                i++;
            } else if (args[i].equals("-i")) {
                eciName = args[i + 1];
                i++;
            } else if (args[i].startsWith("-b")) {
                bidirectional = true;
            } else if (args[i].equals("-nf")) {
                flatten = false;
            }
        }
        // Print error, if necessary
        if (eciName == null) {
            System.out.println("USAGE: java DebugSubGraph <inputEdifFile> "
                    + "<-i instanceName> [-n number of surrounding instances] "
                    + "[-o output dotty file] [-bi](bi-directional search) " + "[-nf](don't flatten circuit)");
            System.exit(-1);
        }

        // Create sub graph
        System.out.println("Creating sub graph...");
        AbstractEdifGraph subGraph = null;
        try {
            subGraph = getSubGraph(eciName, depth, cell, bidirectional, flatten);
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }
        if (subGraph == null) {
            System.out.println("Unable to create sub graph.");
            System.exit(-1);
        }

        System.out.println("Writing dotty graph to file: " + dottyFileName);
        // Create dotty file
        subGraph.toDotty(dottyFileName);
    }

    public static final int DEFAULT_DEPTH = 3;

    public static final boolean DEBUG = true;
    //if (DEBUG) System.out.println("");
}
