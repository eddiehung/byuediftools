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
package edu.byu.ece.edif.util.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Stack;

import edu.byu.ece.edif.arch.xilinx.XilinxMergeParser;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.XilinxTMRArchitecture;
import edu.byu.ece.graph.Edge;

/**
 * @author
 * @since Created on Jan 9, 2006
 */
public class EdifCellBadCutGroupings extends EdifCellInstanceGroupings {

    /**
     * Creates a new instance of EdifCellBadCutGroupings. The groups are formed
     * at object instantiation.
     * 
     * @param cell The EdifCell which will have its instances formed into atomic
     * groups which don't have bad cuts on the edges.
     * @param arch The TMRArchitecture which specifies what a 'bad cut' is.
     * @param ecic The EdifCellInstanceGraph. The caller must be certain that
     * the connectivity is current for the specified EdifCell cell.
     */
    public EdifCellBadCutGroupings(EdifCell cell, NMRArchitecture arch, EdifCellInstanceGraph ecic) {
        super(cell);
        _TMRarch = arch;
        _connectivity = ecic;
        groupCellByBadCuts();
    }

    /**
     * Groups the cell instances inside of the specified _cell according to
     * their bad cuts specified by the specific architecture in _arch.
     */
    protected void groupCellByBadCuts() {
        // 1. For each sub-cell instance inside of 'cell'
        // 2. If a group for this instance does not already exist (a group bigger than 1) 
        //    - Create a group of instances around this instance which do not 
        //      have bad cuts on the peripheral ports of the group
        //    - Add the group to the set of groups in the groupings collection
        for (Iterator i = _cell.getSubCellList().iterator(); i.hasNext();) {
            EdifCellInstance eci = (EdifCellInstance) i.next();
            EdifCellInstanceCollection group = (EdifCellInstanceCollection) _edifCellInstanceGroupsMap.get(eci);
            if (group.size() == 1) {
                EdifCellInstanceCollection new_group = createAtomicGroup(eci);
                if (new_group.size() == 1)
                    continue;
                removeOldSingleInstanceGroups(new_group);
                _edifCellInstanceGroups.add(new_group);
                updateMap(new_group);
            }
        }
    }

    /**
     * Returns a Collection of EdifCellInstances that form an atomic unit,
     * meaning there is no "bad cut" at the edge of the group.
     * <p>
     * TODO: Idea: Use EPRs ?? on the stack rather than instances to speed up
     * the method.
     * <p>
     * Currently all 'bad' connections are checked twice. For example, if cell
     * instance A has a bad cut on output O which drives input I on cell
     * instance B, then B gets put on the stack. But then B turns right around
     * and says that it has a bad cut on input I and tries to add A back onto
     * the stack (the code takes care of not getting into an infinite loop, but
     * if there was a way to avoid the second check, it could speed up the code)
     * KSM
     * 
     * @param startECI The EdifCellInstance to start with
     * @return An atomic set of ECIs forming a "good" cut
     */
    protected EdifCellInstanceCollection createAtomicGroup(EdifCellInstance startECI) {
        Collection ECIset = new LinkedHashSet(1); // Save memory by minimizing size
        Stack ECIstack = new Stack();
        // Initialize the Stack
        EdifCellInstance nextECI = startECI;
        ECIset.add(nextECI);
        ECIstack.push(nextECI);

        while (!ECIstack.isEmpty()) {
            // Grab next ECI off the stack
            nextECI = (EdifCellInstance) ECIstack.pop();
            // Bad cut. Add necessary nodes to complete this atomic set
            //System.out.println("### Bad Cut found:"+nextECI);
            Iterator ecis = null;
            // Grab any predecessors of this ECI which cause bad cuts
            Collection badCutInputs = getBadCutSourceECIs(nextECI, _connectivity, _TMRarch);
            ecis = badCutInputs.iterator();
            while (ecis.hasNext()) {
                // Push "bad cut" predecessors onto Stack and add to ECIset to return
                EdifCellInstance eci = (EdifCellInstance) ecis.next();
                if (ECIset.contains(eci))
                    continue; // Don't bother with duplicate additions
                ECIstack.push(eci);
                ECIset.add(eci);
            }
            // Grab any successors of this ECI which cause bad cuts
            Collection badCutOutputs = getBadCutSinkECIs(nextECI, _connectivity, _TMRarch);
            ecis = badCutOutputs.iterator();
            while (ecis.hasNext()) {
                // Push "bad cut" successors onto Stack and add to ECIset to return
                EdifCellInstance eci = (EdifCellInstance) ecis.next();
                if (ECIset.contains(eci))
                    continue; // Don't bother with duplicate additions
                ECIstack.push(eci);
                ECIset.add(eci);
            }

        }

        if (ECIset.size() == 1)
            return new SingleInstanceCollection(ECIset);
        else
            return new MultipleInstanceCollection(ECIset);
    }

    /**
     * The architecture which defines what are bad cuts to make in the specified
     * cell _cell *
     */
    NMRArchitecture _TMRarch;

    /** Defines the connectivity of all instances within the parent cell _cell * */
    EdifCellInstanceGraph _connectivity;

    /**
     * Determines the inputs on the specified EdifCellInstance which are bad to
     * cut.
     * 
     * @param eci The EdifCellInstance to examine for bad cut inputs.
     * @param ecic The connectivity of the EdifCell to which the specified
     * EdifCellInstance eci belongs.
     * @param tmrArch The TMRArchitecture which specifies what inputs are bad to
     * cut on the specified EdifCellInstance eci.
     * @return A Collection of EdifCellInstance objects on the source end of a
     * bad cut.
     */
    public static Collection getBadCutSourceECIs(EdifCellInstance eci, EdifCellInstanceGraph ecic,
            NMRArchitecture tmrArch) {
        Collection badCutSourceECIs = new LinkedHashSet();

        if (tmrArch == null) {
            throw new EdifRuntimeException("No TMRArchitecture specified.");
        }

        // Grab the Edges which attach to the input ports of the Instance
        Collection inputEdges = ecic.getInputEdges(eci);
        if (inputEdges == null)
            return badCutSourceECIs;
        Iterator i = inputEdges.iterator();
        while (i.hasNext()) {
            EdifCellInstanceEdge edge = (EdifCellInstanceEdge) i.next();
            if (isBadCutEdge(edge, tmrArch)) {
                // Grab predecessor node - the node on the source end of this edge
                EdifCellInstance bad_cut_predecessor_node = (EdifCellInstance) edge.getSource();
                if (bad_cut_predecessor_node != null)
                    badCutSourceECIs.add(bad_cut_predecessor_node);
            }
        }
        return badCutSourceECIs;
    }

    /**
     * Determines the outputs on the specified EdifCellInstance which are bad to
     * cut.
     * 
     * @param eci The EdifCellInstance to examine for bad cut outputs.
     * @param ecic The connectivity of the EdifCell to which the specified
     * EdifCellInstance eci belongs.
     * @param tmrArch The TMRArchitecture which specifies what outputs are bad
     * to cut on the specified EdifCellInstance eci.
     * @return A Collection of EdifCellInstance objects on the sink end of a bad
     * cut.
     */
    public static Collection getBadCutSinkECIs(EdifCellInstance eci, EdifCellInstanceGraph ecic, NMRArchitecture tmrArch) {
        Collection badCutSinkECIs = new LinkedHashSet();

        if (tmrArch == null) {
            throw new EdifRuntimeException("No TMRArchitecture specified.");
        }

        // Grab the Edges which attach to the output ports of the Instance
        Collection outputEdges = ecic.getOutputEdges(eci);
        if (outputEdges == null)
            return badCutSinkECIs;
        Iterator i = outputEdges.iterator();
        while (i.hasNext()) {
            EdifCellInstanceEdge edge = (EdifCellInstanceEdge) i.next();
            if (isBadCutEdge(edge, tmrArch)) {
                // Grab successor node - the node on the sink end of this edge
                EdifCellInstance bad_cut_successor_node = (EdifCellInstance) edge.getSink();
                if (bad_cut_successor_node != null)
                    badCutSinkECIs.add(bad_cut_successor_node);
            }
        }
        return badCutSinkECIs;
    }

    /**
     * Checks if the given EdifCellInstanceEdge represents a connection that
     * cannot be cut due to architectural restrictions.
     * 
     * @param edge The EdifCellInstanceEdge object to check
     * @param nmrArch The NMR architecture to use as a reference
     * @return true if the Edge should not be cut, false if okay to cut
     */
    public static boolean isBadCutEdge(EdifCellInstanceEdge edge, NMRArchitecture nmrArch) {
        //if (tmrArch.isBadCut(edge.getSourceEPR()) || tmrArch.isBadCut(edge.getSinkEPR()))
        if (nmrArch.isBadCutConnection(edge.getSourceEPR(), edge.getSinkEPR()))
            return true;
        else
            return false;
    }

    /**
     * Checks if the given Edge represents a connection that cannot be cut due
     * to architectural restrictions. Currently supported objects are
     * EdifCellInstanceEdge and EdifCellInstanceCollectionLink.
     * 
     * @param edge The Edge object to check
     * @param nmrArch The NMR architecture to use as a reference
     * @return true if the Edge should not be cut, false if okay to cut
     */
    public static boolean isBadCutEdge(Edge edge, NMRArchitecture nmrArch) {
        if (edge instanceof EdifCellInstanceEdge) {
            // Check single EdifCellInstanceEdge
            return isBadCutEdge((EdifCellInstanceEdge) edge, nmrArch);
        } else if (edge instanceof EdifCellInstanceCollectionLink) {
            // Check all EdifCellInstanceEdge objects. Return false if any 
            //   inner Edge is a Bad Cut Edge
            EdifCellInstanceCollectionLink collectionLink = (EdifCellInstanceCollectionLink) edge;
            for (EdifCellInstanceEdge innerEdge : collectionLink.getLinks()) {
                if (isBadCutEdge(innerEdge, nmrArch))
                    return true;
            }
            return false;
        } else {
            // Unknown Edge type
            System.out.println("Warning (isBadCutEdge): link is not a known type - " + edge.getClass().getName());
            return false;
        }
    }

    public static void main(String[] args) {
        EdifCell top_cell = XilinxMergeParser.parseAndMergeXilinx(args);
        EdifCell flat_top_cell = null;
        try {
            flat_top_cell = new FlattenedEdifCell(top_cell);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }
        EdifCellInstanceGraph connectivity = new EdifCellInstanceGraph(flat_top_cell);
        NMRArchitecture arch = new XilinxTMRArchitecture();
        System.out.println("Begin bad cut grouping...");
        long begin_time = System.currentTimeMillis();
        EdifCellInstanceGroupings badCutGroups = new EdifCellBadCutGroupings(flat_top_cell, arch, connectivity);
        long elapsed_time_sec = (System.currentTimeMillis() - begin_time) / (long) 60.0;
        System.out.println("Grouping finished in " + elapsed_time_sec + " seconds.");
        Collection groups = badCutGroups.getInstanceGroups();
        System.out.println(groups);
        int[] dist = new int[1000];
        for (Iterator i = groups.iterator(); i.hasNext();) {
            int size = ((EdifCellInstanceCollection) i.next()).size();
            dist[size]++;
        }
        for (int i = 0; i < dist.length; i++) {
            //if (dist[i] != 0)
            System.out.println(i + "\t" + dist[i]);
        }
    }
}
