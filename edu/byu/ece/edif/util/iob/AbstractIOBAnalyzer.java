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
import java.util.LinkedHashSet;
import java.util.Map;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.core.StringTypedValue;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;

public abstract class AbstractIOBAnalyzer {

    /**
     * Checks for the IOB property being FALSE or not
     * 
     * @param eci The EdifCellInstance
     * @return true if the EdifCellInstance has an IOB property set to FALSE
     */
    public static boolean hasIOBFalseProperty(EdifCellInstance eci) {
        Property property = eci.getProperty("IOB");
        if (property == null)
            return false;

        String iobProperty = ((StringTypedValue) property.getValue()).getStringValue();
        if (iobProperty != null && iobProperty.equals("FALSE"))
            return true;
        //else
        return false;
    }

    /**
     * @return A Collection of EdifCellInstances corresponding to all of the IOB
     * instances in this EdifCell
     */
    public Collection<EdifCellInstance> getAllIOBInstances() {
        Collection<EdifCellInstance> ecis = new LinkedHashSet<EdifCellInstance>(); // No duplicates

        for (AbstractIOB xiob : _iobMap.values()) {
            // Get input, output, and tristate regs in each IOB
            if (xiob != null)
                ecis.addAll(xiob.getAllInstances());
        }

        return ecis;
    }

    /**
     * @return A Collection of EdifCellInstances corresponding to all of the IOB
     * registers in this EdifCell
     */
    public Collection<EdifCellInstance> getAllIOBRegisters() {
        Collection<EdifCellInstance> iobRegs = new LinkedHashSet<EdifCellInstance>(); // No duplicates

        for (AbstractIOB xiob : _iobMap.values()) {
            // Get input, output, and tristate regs in each IOB
            if (xiob != null)
                iobRegs.addAll(xiob.getRegisters());
        }

        return iobRegs;
    }

    /**
     * @return A Collection of IOBs that may be in feedback (inout IOBs)
     */
    public Collection<EdifSingleBitPort> getFeedbackIOBs() {
        return new ArrayList<EdifSingleBitPort>(_feedbackIOBMap.keySet());
    }

    /**
     * Returns a Collection of graph Edges corresponding to possible IOB
     * feedback in the design.
     */
    public Collection<EdifCellInstanceEdge> getIOBFeedbackEdges() {
        return new ArrayList<EdifCellInstanceEdge>(_iobFeedbackEdges);
    }

    /**
     * Returns a Set of EPRs corresponding to the IOB feedback edges in the
     * graph. The decision of which EPR is not smart currently. Please use
     * getIOBFeedbackEdges() instead.
     * 
     * @return
     */
    public Collection<EdifPortRef> getIOBFeedbackEPRs() {
        Collection<EdifPortRef> feedbackEPRs = new LinkedHashSet<EdifPortRef>(_iobFeedbackEdges.size());
        /*
         * Add sink EPRs due to the way we define the cuts. Since all of these
         * Edges correspond to outputs of a top-level port (perhaps buffered),
         * the sink EPRs will be attached to EdifCellInstances (except for the
         * case in which a top-level port directly drives another top-level
         * port).
         * 
         * TODO: Perhaps be smart about which EPR to get (source vs. sink) based
         * on the type of inout IOB (with buffers/wihout/etc.)
         */
        for (EdifCellInstanceEdge edge : _iobFeedbackEdges) {
            feedbackEPRs.add(edge.getSinkEPR());
        }

        return feedbackEPRs;
    }

    /**
     * @return A Collection of EdifCellInstances corresponding to all of the IOB
     * instances associated with this EdifSingleBitPort
     */
    public Collection<EdifCellInstance> getIOBInstances(EdifSingleBitPort esbp) {
        // Get all instances
        AbstractIOB xiob = _iobMap.get(esbp);
        if (xiob != null)
            return xiob.getAllInstances();
        //else
        return null;
    }

    /**
     * @return A Collection of EdifCellInstances corresponding to all of the IOB
     * instances associated with these EdifSingleBitPorts
     */
    public Collection<EdifCellInstance> getIOBInstances(Collection<EdifSingleBitPort> esbps) {
        Collection<EdifCellInstance> ecis = new LinkedHashSet<EdifCellInstance>(); // No duplicates

        for (EdifSingleBitPort esbp : esbps) {
            AbstractIOB xiob = _iobMap.get(esbp);
            // Get all instances in each IOB
            if (xiob != null)
                ecis.addAll(xiob.getAllInstances());
        }

        return ecis;
    }

    /**
     * @return A Collection of EdifCellInstances corresponding to all of the IOB
     * registers in associated with this EdifSingleBitPort (including input
     * register, output register, and tristate register)
     */
    public Collection<EdifCellInstance> getIOBRegisters(EdifSingleBitPort esbp) {
        // Get input, output, and tristate registers
        AbstractIOB xiob = _iobMap.get(esbp);
        if (xiob != null)
            return xiob.getRegisters();
        //else
        return null;
    }

    public AbstractIOBAnalyzer() {

    }

    protected EdifCell _cell;

    protected EdifCellInstanceGraph _graph;

    protected Map<EdifSingleBitPort, XilinxVirtexIOB> _iobMap;

    protected ArrayList<EdifCellInstanceEdge> _iobFeedbackEdges;

    protected Map<EdifSingleBitPort, Collection<EdifCellInstanceEdge>> _feedbackIOBMap;

    protected boolean _packInputRegs;

    protected boolean _packOutputRegs;

    public static final boolean _debug = false;

}
