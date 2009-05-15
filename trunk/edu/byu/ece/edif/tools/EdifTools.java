/*
 * A class to hold generic utilities useful across the EDIF package.
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
package edu.byu.ece.edif.tools;

import java.util.ArrayList;
import java.util.Collection;

import edu.byu.ece.edif.arch.xilinx.XilinxMergeParser;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.NMRUtilities;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxResourceMapper;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.graph.dfs.DepthFirstTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * A class to hold any generic utilities that could be useful across the EDIF
 * package.
 */
public class EdifTools {

    /**
     * Counts the number of Flip-Flops in a collection of EdifCellInstances.
     * Note that this only looks for Xilinx flip-flops.
     * 
     * @param ecis
     * @return The number of flip-flops found
     */
    public static int countXilinxFlipFlops(Collection<EdifCellInstance> ecis) {
        int count = 0;
        for (EdifCellInstance eci : ecis) {
            if (XilinxResourceMapper.getInstance().getResourceType(eci).equals(XilinxResourceMapper.FF))
                count++;
        }
        return count;
    }

    /**
     * Counts the number of flip-flops found in persistent sections of a
     * circuit. The circuit must be flattened to get an accurate count.
     * 
     * @return The number of flip-flops in the persistent sections of the given
     * FlattenedEdifCell
     */
    public static int countXilinxPersistentFlipFlops(FlattenedEdifCell flatCell) {
        int count = 0;

        // Build graph
        EdifCellInstanceGraph graph = new EdifCellInstanceGraph(flatCell);

        // Do SCC analysis
        SCCDepthFirstSearch sccDFS = new SCCDepthFirstSearch(graph);

        // Get SCC instances
        Collection<EdifCellInstance> sccECIs = new ArrayList<EdifCellInstance>();
        for (DepthFirstTree scc : sccDFS.getTrees()) {
            for (Object node : scc.getNodes()) {
                if (node instanceof EdifCellInstance) {
                    sccECIs.add((EdifCellInstance) node);
                }
            }
        }

        // Count SCC flip-flops
        count += countXilinxFlipFlops(sccECIs);

        // Get input-to-SCC instances
        Collection<EdifCellInstance> inputNodes = graph.getAncestors(sccECIs);
        Collection<EdifCellInstance> inputECIs = new ArrayList<EdifCellInstance>();
        for (Object node : inputNodes) {
            if (node instanceof EdifCellInstance) {
                inputECIs.add((EdifCellInstance) node);
            }
        }

        // Count input-to-SCC flip-flops
        count += countXilinxFlipFlops(inputECIs);

        return count;
    }

    public static void main(String[] args) {

        System.out.print("Parsing design...");
        EdifCell cell = XilinxMergeParser.parseAndMergeXilinx(args);
        System.out.println("done.");

        System.out.print("Flattening top cell...");
        FlattenedEdifCell flatCell = NMRUtilities.flattenCell(cell, System.out);

        System.out.print("Counting flip-flops...");
        int ffCount = countXilinxPersistentFlipFlops(flatCell);
        System.out.println("done.");

        System.out.println("Found " + ffCount + " flip-flops in the persistent sections of the given Edif design.");
    }

}
