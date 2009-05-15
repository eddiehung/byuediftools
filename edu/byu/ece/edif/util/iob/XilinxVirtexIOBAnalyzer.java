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
package edu.byu.ece.edif.util.iob;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;

/**
 * This class is currently written for Virtex devices only, though other
 * architectures will probably work to some degree.
 */
public class XilinxVirtexIOBAnalyzer extends XilinxIOBAnalyzer {

    public XilinxVirtexIOBAnalyzer(EdifCell topCell) {
        this(topCell, null);
    }

    // Pack all registers by default
    public XilinxVirtexIOBAnalyzer(EdifCell topCell, EdifCellInstanceGraph graph) {
        this(topCell, graph, true, true);
    }

    // TODO: Check for same clock on all FF Regs
    public XilinxVirtexIOBAnalyzer(EdifCell topCell, EdifCellInstanceGraph graph,
            boolean packInputRegisters, boolean packOutputRegisters) {
        _cell = topCell;

        // Create a Connectivity Graph if none exists
        if (graph == null)
            _graph = new EdifCellInstanceGraph(topCell, true, true);
        else
            _graph = graph;

        // Initialize Collections
        _iobMap = new LinkedHashMap<EdifSingleBitPort, AbstractIOB>();
        _iobFeedbackEdges = new ArrayList<EdifCellInstanceEdge>();
        _feedbackIOBMap = new LinkedHashMap<EdifSingleBitPort, Collection<EdifCellInstanceEdge>>();
        _packInputRegs = packInputRegisters;
        _packOutputRegs = packOutputRegisters;

        // Analyze the EdifCell and extract the IOBs
        analyze();
    }

    /**
     * Creates a new XilinxIOB object and fills in the appropriate information
     * using the given EdifCellInstanceGraph graph.
     * 
     * @param esbp The EdifSingleBitPort object to generate the XilinxIOB from
     * @param graph The EdifCellInstanceGraph graph to use
     * @return a newly-created XilinxIOB object with all IOB information
     */
    public static AbstractIOB createXilinxIOBFromPort(EdifSingleBitPort esbp, EdifCellInstanceGraph graph) {
        return createXilinxIOBFromPort(esbp, graph, true, true);
    }
}
