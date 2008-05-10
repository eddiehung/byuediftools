/*
 * Breaks down input and output of an EdifCell by inputs and outputs to SCCs.
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
package edu.byu.ece.edif.tools.replicate.nmr.tmr;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import edu.byu.ece.edif.arch.xilinx.XilinxMergeParser;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifUtils;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.tools.flatten.NewFlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.DeviceUtilizationTracker;
import edu.byu.ece.edif.tools.replicate.nmr.DuplicateNMRRequestException;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.NMRGraphUtilities;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationEstimatedStopException;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationException;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationHardStopException;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxVirtexDeviceUtilizationTracker;
import edu.byu.ece.edif.util.graph.EdifCellBadCutGroupings;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollection;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollectionGraph;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.graph.dfs.BasicDepthFirstSearchTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * PartialInputOutputFeedForwardTMR Breaks down the input and output of the
 * given EdifCell by inputs and outputs to SCCs.
 * 
 * @author Brian Pratt, Michael Wirthlin
 */
public class PartialInputOutputFeedForwardTMR {

    /**
     * Default to TMR both input and output, using input and output collapse
     * types of Breadth First (type 3)
     * 
     * @return Status of triplication
     */
    public static int[] tmrSCCInputAndOutput(EdifCell cell, EdifCellInstanceGraph ecic,
            DeviceUtilizationTracker resourceTracker, NMRArchitecture tmrArch, SCCDepthFirstSearch sccs) {
        return tmrSCCInputAndOutput(cell, ecic, resourceTracker, tmrArch, sccs, true, true, true,
                DEFAULT_COLLAPSE_TYPE, DEFAULT_COLLAPSE_TYPE);
    }

    /**
     * Select the input and output to be triplicated in the given EdifCell.
     * <p>
     * Input and Output logic (to and from the feedback instances) can be added
     * in different ways based on the inputAdditionType and outputAdditionType
     * parameters. Default=3. 1: Depth First, 2: Depth-Breadth, 3: Breadth First
     * <p>
     * Limits on the input and output instances can also be set with the
     * inputLimit and outputLimit parameters.
     * <p>
     * The results of the decomposition can be obtained as two Lists of Sets of
     * EdifCellInstances through the getInputSets() and getOutputSets() methods.
     * Each Set is a group of EdifCellInstances that <i>must</i> be added to
     * the triplication together because of architecture-specific limits.
     * 
     * @param cell The given EdifCell
     * @param connectivity
     * @param resourceTracker The DeviceUtilizationTracker being used
     * @param tmrArch The NMRArchitecture
     * @param sccDFS The SCCDepthFirstSearch being used
     * @param tmrInput Whether or not input ports should be triplicated
     * @param tmrOutput Whether or not output ports should be triplicated
     * @param tmrFeedForward Whether or not the feed forward section should be
     * triplicated
     * @param inputAdditionType An integer indicating which of three different
     * algorithms should be used to determine the order of instances added in
     * the Input to Feedback section.
     * @param outputAdditionType An integer indicating which of three different
     * algorithms should be used to determine the order of instances added in
     * the Feedback Output section.
     * @return Status of triplication, encoded as an array of int's.
     */
    public static int[] tmrSCCInputAndOutput(EdifCell cell, EdifCellInstanceGraph connectivity,
            DeviceUtilizationTracker resourceTracker, NMRArchitecture tmrArch, SCCDepthFirstSearch sccDFS,
            boolean tmrInput, boolean tmrOutput, boolean tmrFeedForward, int inputAdditionType, int outputAdditionType) {

        //////
        // 1. Create Bad Cut Groupings
        if (DEBUG)
            System.out.println("Creating bad cut groupings . . .");
        // Create Connectivity for this EdifCell if not provided
        //   (Don't include top level ports)
        if (connectivity == null)
            connectivity = new EdifCellInstanceGraph(cell, false);
        EdifCellBadCutGroupings groupings = new EdifCellBadCutGroupings(cell, tmrArch, connectivity);

        return tmrSCCInputAndOutput(cell, connectivity, groupings, resourceTracker, tmrArch, sccDFS, tmrInput,
                tmrOutput, tmrFeedForward, inputAdditionType, outputAdditionType);
    }

    /**
     * Select the input and output to be triplicated in the given EdifCell.
     * <p>
     * Input and Output logic (to and from the feedback instances) can be added
     * in different ways based on the inputAdditionType and outputAdditionType
     * parameters. Default=3. 1: Depth First, 2: Depth-Breadth, 3: Breadth First
     * <p>
     * Limits on the input and output instances can also be set with the
     * inputLimit and outputLimit parameters.
     * <p>
     * The results of the decomposition can be obtained as two Lists of Sets of
     * EdifCellInstances through the getInputSets() and getOutputSets() methods.
     * Each Set is a group of EdifCellInstances that <i>must</i> be added to
     * the triplication together because of architecture-specific limits.
     * 
     * @param cell The given EdifCell
     * @param connectivity
     * @param badCutGroupings The EdifCellBadCutGroupings object for this graph
     * @param resourceTracker The DeviceUtilizationTracker being used
     * @param tmrArch The NMRArchitecture
     * @param sccDFS The SCCDepthFirstSearch being used
     * @param tmrInput Whether or not input ports should be triplicated
     * @param tmrOutput Whether or not output ports should be triplicated
     * @param tmrFeedForward Whether or not the feed forward section should be
     * triplicated
     * @param inputAdditionType An integer indicating which of three different
     * algorithms should be used to determine the order of instances added in
     * the Input to Feedback section.
     * @param outputAdditionType An integer indicating which of three different
     * algorithms should be used to determine the order of instances added in
     * the Feedback Output section.
     * @return Status of triplication, encoded as an array of int's.
     */
    public static int[] tmrSCCInputAndOutput(EdifCell cell, EdifCellInstanceGraph connectivity,
            EdifCellBadCutGroupings badCutGroupings, DeviceUtilizationTracker resourceTracker, NMRArchitecture tmrArch,
            SCCDepthFirstSearch sccDFS, boolean tmrInput, boolean tmrOutput, boolean tmrFeedForward,
            int inputAdditionType, int outputAdditionType) {
        // Initialize DEBUG structures
        if (DEBUG) {
            _inputSets = new ArrayList();
            _outputSets = new ArrayList();
            _ECIsRelatedToFB = new LinkedHashSet();
        }
        long startTime, elapsedTime;
        startTime = System.currentTimeMillis();

        boolean allFeedbackInstancesTriplicated = false;
        boolean allInputToFeedbackInstancesTriplicated = false;
        boolean allFeedbackOutputInstancesTriplicated = false;
        boolean allFeedForwardInstancesTriplicated = false;

        int triplicationStatus[] = new int[4];

        /*
         * Steps: 1. Create Bad Cut Groupings 2. Add SCCs to Bad Cut Groupings -
         * Bad Cut instances may be added into groups here. Add them to TMR! 3.
         * Create Group Connectivity 4. Do topological sort of SCCs(?) 5. Add
         * input/output instances to DeviceUtilizationTracker 6. Add left-over
         * instances to DeviceUtilizationTracker
         */

        int initialInstanceCount = resourceTracker.getCurrentNMRInstances().size();

        //////
        // 2. Add SCCs to groupings--Preserve Original Ordering
        if (DEBUG)
            System.out.println("Adding SCCs to bad cut groupings . . .");
        List sccs = sccDFS.getTopologicallySortedTreeList();
        List sccGroups = new ArrayList();
        Set allSCCInstances = new LinkedHashSet();
        Iterator i = sccs.iterator();
        while (i.hasNext()) {
            BasicDepthFirstSearchTree sccTree = (BasicDepthFirstSearchTree) i.next();
            // Each tree could be a Collection of EdifCellInstances or a Collection
            //   of Collections of EdifCellInstances (from a Bad Cut Groupings SCCDFS)
            Collection sccNodes = sccTree.getNodes();
            Set sccInstances = new LinkedHashSet();

            for (Object node : sccNodes) {
                if (node instanceof EdifCellInstance) {
                    sccInstances.add(node);
                } else if (node instanceof EdifCellInstanceCollection) {
                    sccInstances.addAll((EdifCellInstanceCollection) node);
                } else {
                    if (DEBUG)
                        System.out.println("WARNING: " + "PartialInputOutputFeedForwardTMR ignoring " + node
                                + "of type " + node.getClass());
                }
            }

            // Remove non-EdifCellInstances (e.g. top-level ports) from Collection
            //            for (Iterator j = scc.iterator(); j.hasNext();) {
            //                Object next = j.next();
            //                if (!(next instanceof EdifCellInstance)) {
            //                    if (DEBUG)
            //                        System.out.println("WARNING: " + "PartialInputOutputFeedForwardTMR NOT triplicating" + next
            //                                + "of type" + next.getClass());
            //                    j.remove(); // Remove non-instances
            //                }
            //            }

            allSCCInstances.addAll(sccInstances);
            EdifCellInstanceCollection sccGroup = badCutGroupings.groupInstances(sccInstances);
            if (!sccGroups.contains(sccGroup))
                sccGroups.add(sccGroup);
        }
        // Prune Duplicate Groups (sccGroups may be created that include
        //   previous sccGroups, remove the older versions by only keeping
        //   those that were not deleted from the Groupings object)
        sccGroups.retainAll(badCutGroupings.getInstanceGroups());

        // Bad Cut instances may be added into groups here. Add them to TMR!
        allFeedbackInstancesTriplicated = addSCCBadCutInstances(allSCCInstances, sccGroups, resourceTracker);

        int feedbackInstanceCount = resourceTracker.getCurrentNMRInstances().size() - initialInstanceCount;

        //////
        // 3. Create Group Connectivity (Don't include top level ports)
        if (DEBUG)
            System.out.println("Creating GroupConnectivity Graph . . .");
        EdifCellInstanceCollectionGraph badCutGroupConn = new EdifCellInstanceCollectionGraph(connectivity,
                badCutGroupings, false);
        //if (DEBUG) groupConn.toDotty(groupConn.getCell().getName()+"_groupConn.dot");

        //////
        // 4. The SCCs should already be in topological order
        List sortedSCCs = sccGroups;

        // How many Instances are in the SCC Groups?
        if (DEBUG) {
            for (Iterator it = sccGroups.iterator(); it.hasNext();) {
                EdifCellInstanceCollection scc = (EdifCellInstanceCollection) it.next();
                _ECIsRelatedToFB.addAll(scc);
            }
            System.out.println("### Number of Instances in SCC Bad Cut Groups: " + _ECIsRelatedToFB.size());
        }

        //////
        // 5. Collapse input and output using the order calculated above
        // If DEBUG is on, this will fill the _inputSets and _outputSets lists
        if (DEBUG)
            System.out.println("Adding inputs and outputs of SCCs to TMR . . .");
        // These methods do NOT modify the groupConnectivity
        if (DEBUG)
            System.out.println("### Number of Instances to TMR before adding SCC Inputs or Outputs: "
                    + resourceTracker.getCurrentNMRInstances().size());
        // 5.a) Add Logic feeding into the SCCs to TMRResourceTracker
        if (tmrInput) {
            allInputToFeedbackInstancesTriplicated = addInputECIsToTMR(sortedSCCs, badCutGroupConn, resourceTracker,
                    inputAdditionType);
            if (DEBUG)
                System.out.println("### Number of Instances to TMR after adding SCC Inputs: "
                        + resourceTracker.getCurrentNMRInstances().size());
        }
        int inputToFeedbackInstanceCount = resourceTracker.getCurrentNMRInstances().size() - feedbackInstanceCount;
        // 5.b) Add Logic driven by the SCCs to TMRResourceTracker
        if (tmrOutput) {
            allFeedbackOutputInstancesTriplicated = addOutputECIsToTMR(sortedSCCs, badCutGroupConn, resourceTracker,
                    outputAdditionType);
            if (DEBUG)
                System.out.println("### Number of Instances to TMR after adding SCC Outputs: "
                        + resourceTracker.getCurrentNMRInstances().size());
        }
        int feedbackOutputInstanceCount = resourceTracker.getCurrentNMRInstances().size()
                - inputToFeedbackInstanceCount;

        //////
        // 6. Add "Feed-forward" logic (all logic that isn't part of the
        //    other sections)
        if (tmrFeedForward) {
            allFeedForwardInstancesTriplicated = addFeedForwardECIsToTMR(sortedSCCs, badCutGroupConn, resourceTracker,
                    false);
            if (DEBUG)
                System.out.println("### Number of Instances to TMR after adding Feed Forward logic: "
                        + resourceTracker.getCurrentNMRInstances().size());
        }
        int feedForwardInstanceCount = resourceTracker.getCurrentNMRInstances().size() - feedbackOutputInstanceCount;

        elapsedTime = System.currentTimeMillis() - startTime;
        if (DEBUG)
            System.out.println("I/O decomposition took " + msToString(elapsedTime));

        // Return status as array of ints that can be decoded later.
        createTriplicationStatus(triplicationStatus, allFeedbackInstancesTriplicated, feedbackInstanceCount,
                allInputToFeedbackInstancesTriplicated, inputToFeedbackInstanceCount,
                allFeedbackOutputInstancesTriplicated, feedbackOutputInstanceCount, allFeedForwardInstancesTriplicated,
                feedForwardInstanceCount);

        return triplicationStatus;
    }

    /**
     * Adds the input EdifCellInstances of the given sorted List of SCCs to the
     * DeviceUtilizationResouceTracker until that object throws an "Estimated
     * Stop" Exception. This method will skip over "Hard Stop" Exceptions if
     * encountered. (i.e. It will add all Instances before and after the
     * non-triplicatable Instance(s)).
     * <p>
     * The type of collapse (breadth-first, depth-first) is dependent on the
     * inputAdditionType parameter.
     * 
     * @param sortedSCCs A List of EdifCellInstanceCollection objects
     * representing the SCCs. These should be in topological order.
     * @param groupConn The EdifCellInstanceCollectionGraph object associated
     * with this EdifCell that contains the sortedSCC groups
     * @param resourceTracker The DeviceUtilizationTracker object to add the
     * triplicated Instances to
     * @param inputAdditionType Which type of input collapse to perform
     * @return Status of triplication: true if ALL instance were triplicated,
     * false if some were skipped or wouldn't fit.
     */
    public static boolean addInputECIsToTMR(List sortedSCCs, EdifCellInstanceCollectionGraph groupConn,
            DeviceUtilizationTracker resourceTracker, int inputAdditionType) {
        return addInputOrOutputECIsToTMR(sortedSCCs, groupConn, resourceTracker, inputAdditionType, true);
    }

    /**
     * Adds the output EdifCellInstances of the given sorted List of SCCs to the
     * DeviceUtilizationResouceTracker until that object throws an "Estimated
     * Stop" Exception. This method will skip over "Hard Stop" Exceptions if
     * encountered. (i.e. It will add all Instances before and after the
     * non-triplicatable Instance(s)).
     * <p>
     * The type of collapse (breadth-first, depth-first) is dependent on the
     * outputAdditionType parameter.
     * 
     * @param sortedSCCs A List of EdifCellInstanceCollection objects
     * representing the SCCs. These should be in topological order.
     * @param groupConn The EdifCellInstanceCollectionGraph object associated
     * with this EdifCell that contains the sortedSCC groups
     * @param resourceTracker The DeviceUtilizationTracker object to add the
     * triplicated Instances to
     * @param outputAdditionType Which type of output collapse to perform
     * @return Status of triplication: true if ALL instance were triplicated,
     * false if some were skipped or wouldn't fit.
     */
    public static boolean addOutputECIsToTMR(List sortedSCCs, EdifCellInstanceCollectionGraph groupConn,
            DeviceUtilizationTracker resourceTracker, int outputAdditionType) {
        return addInputOrOutputECIsToTMR(sortedSCCs, groupConn, resourceTracker, outputAdditionType, false);
    }

    // ACCESSOR METHODS
    public static Collection getCellInstancesRelatedToFeedback() {
        return _ECIsRelatedToFB;
    }

    public static List getInputSets() {
        return _inputSets;
    }

    public static List getOutputSets() {
        return _outputSets;
    }

    /**
     * Adds the feed-forward EdifCellInstances of the given group connectivity
     * to the DeviceUtilizationResouceTracker until that object throws an
     * "Estimated Stop" Exception. This method will skip over "Hard Stop"
     * Exceptions if encountered. (i.e. It will add all Instances before and
     * after the non-triplicatable Instance(s)).
     * 
     * @param sccGroups A Collection of EdifCellInstanceCollection objects
     * representing the SCCs. These are needed to avoid feedback loops.
     * @param groupConn The EdifCellInstanceCollectionGraph object associated
     * with this EdifCell
     * @param resourceTracker The DeviceUtilizationTracker object to add the
     * triplicated Instances to
     * @param forward Indicates whether to start at the inputs (forward) or
     * outputs (!forward)
     * @return Status of triplication: true if ALL instance were triplicated,
     * false if some were skipped or wouldn't fit.
     */
    protected static boolean addFeedForwardECIsToTMR(Collection sccGroups, EdifCellInstanceCollectionGraph groupConn,
            DeviceUtilizationTracker resourceTracker, boolean forward) {

        boolean allInstancesTriplicated = true;
        List groupsToTMR = new ArrayList();

        // Keep track of the nodes that have already been visited, initialized
        //   with the SCCs (we don't want to add them)
        // Exclude SCCs by default
        HashSet visitedGroups = new LinkedHashSet(sccGroups);

        // Initialize with input/output groups based on "doInput" parameter
        // Input Nodes are those with zero input edges
        // Output Nodes are those with zero output edges
        for (Iterator j = groupConn.getNodes().iterator(); j.hasNext();) {
            Object node = j.next();
            if (forward && (groupConn.getInputEdges(node).size() == 0) || !forward
                    && (groupConn.getOutputEdges(node).size() == 0))
                groupsToTMR.add(node);
        }

        // Iterate over the "groupsToTMR" List, following Graph and adding
        //   instances to TMR
        // Repeat until EstimatedStopException is received
        // (forward = true means we should follow the graph forward, just like
        //    the "doOutput" case for SCCs)
        try {
            tmrInputOrOutputGroups(groupConn, resourceTracker, DEFAULT_COLLAPSE_TYPE, !forward, visitedGroups,
                    groupsToTMR);
        } catch (OverutilizationEstimatedStopException e) {
            allInstancesTriplicated = false;
        }

        return allInstancesTriplicated;
    }

    /**
     * Adds the input/output EdifCellInstances of the given sorted List of SCCs
     * to the DeviceUtilizationResouceTracker until that object throws an
     * "Estimated Stop" Exception. This method will skip over "Hard Stop"
     * Exceptions if encountered. (i.e. It will add all Instances before and
     * after the non-triplicatable Instance(s)).
     * <p>
     * The type of collapse (breadth-first, depth-first) is dependent on the
     * _inputAdditionType/_outputAdditionType variables.
     * 
     * @param sortedSCCs A List of EdifCellInstanceCollection objects
     * representing the SCCs. These should be in topological order.
     * @param groupConn The EdifCellInstanceCollectionGraph object associated
     * with this EdifCell that contains the sortedSCC groups
     * @param resourceTracker The DeviceUtilizationTracker object to add the
     * triplicated Instances to
     * @param additionType Which type of input/output collapse to perform
     * @param doInput Indicates whether to add the inputs to the SCCs or the
     * outputs from the SCCs
     * @return Status of triplication: true if ALL instance were triplicated,
     * false if some were skipped or wouldn't fit.
     */
    protected static boolean addInputOrOutputECIsToTMR(List sortedSCCs, EdifCellInstanceCollectionGraph groupConn,
            DeviceUtilizationTracker resourceTracker, int additionType, boolean doInput) {

        boolean allInstancesTriplicated = true;

        //////
        // STEPS:
        // 1. Iterate over sorted list of SCC Groups (sorted in topological order)
        //     - Iterate over the list forward when adding SCC inputs and 
        //       backwards when adding SCC outputs
        //   a) Initialize the "nextWave" List to contain the current SCC Group
        //   b) Get the next set of Groups ("groupsToTMR") to add to TMR 
        //      (the predecessors or successors of the "nextWave" List of Groups)
        //   c) Iterate over the "groupsToTMR" List
        //      i.   Skip current Group if it has already been visited (This 
        //           prevents us from following paths that have already been
        //           examined/added)
        //      ii.  Add this Group to the visited Set as well as the
        //           "nextWave" Set to move past it in the next iteration
        //      iii. Attempt to TMR this Group with the device tracker
        //      iv.  Catch exceptions:
        //             - Quit on receipt of EstimatedStopException (Device Full)
        //             - Skip instances that cause HardStopExceptions
        //             - Skip instances that have already been added
        //                 (DuplicateNMRRequestException)
        //   d) Repeat from step b) until EstimatedStopException is received
        //////

        /*
         * Keep track of the nodes that have already been visited, initialized
         * with the SCCs (we don't want to add them)
         */// Exclude SCCs by default
        HashSet visitedGroups = new LinkedHashSet(sortedSCCs);

        //////
        // 1. Iterate over sorted list of SCC Groups (sorted in topological order)
        //     - Iterate over the list forward when adding SCC inputs and 
        //       backwards when adding SCC outputs
        ListIterator sccIt;
        if (doInput)
            sccIt = sortedSCCs.listIterator();
        else
            sccIt = sortedSCCs.listIterator(sortedSCCs.size());
        // Iterate over all SCC groups
        int sccCount = 0;
        EdifCellInstanceCollection sccGroup = null;
        while ((doInput && sccIt.hasNext()) || (!doInput && sccIt.hasPrevious())) {
            if (doInput)
                sccGroup = (EdifCellInstanceCollection) sccIt.next(); // Forwards
            else
                sccGroup = (EdifCellInstanceCollection) sccIt.previous(); // Backwards
            if (DEBUG)
                System.out.println("SCC #" + (++sccCount) + " has " + sccGroup.size() + " instances.");
            if (DEBUG)
                _ECIsRelatedToFB.addAll(sccGroup);

            //////
            //   a) Initialize the "nextWave" List to contain the current SCC Group
            // Keep track of the next wave of input/output to add
            List firstWave = new ArrayList();
            firstWave.add(sccGroup);

            //////
            //   b) Get the next set of Groups ("groupsToTMR") to add to TMR 
            //      (the predecessors or successors of the "nextWave" List of Groups)
            List groupsToTMR;
            initGetGroups();
            if (doInput)
                groupsToTMR = getInputGroups(firstWave, visitedGroups, groupConn, additionType);
            else
                groupsToTMR = getOutputGroups(firstWave, visitedGroups, groupConn, additionType);

            //////
            //   c) Iterate over the "groupsToTMR" List
            //   d) Repeat until EstimatedStopException is received
            try {
                // Record if any instances were missed (due to HardStopExceptions)
                allInstancesTriplicated = tmrInputOrOutputGroups(groupConn, resourceTracker, additionType, doInput,
                        visitedGroups, groupsToTMR);
            } catch (OverutilizationEstimatedStopException e) {
                // Record if any instances were missed (due to no space left on chip)
                allInstancesTriplicated = false;
                break;
            }
        }

        return allInstancesTriplicated;
    }

    /**
     * Adds instances that are part of the SCC bad cut groupings, but not part
     * of the original SCCs to TMR.
     * 
     * @param sccInstances Instances in the original SCC groups
     * @param sccGroups SCC bad cut groupings
     * @param resourceTracker DeviceUtilizationTracker to TMR instances
     * @return Status of triplication: true if ALL instance were triplicated,
     * false if some were skipped or wouldn't fit.
     */
    protected static boolean addSCCBadCutInstances(Collection sccInstances, Collection sccGroups,
            DeviceUtilizationTracker resourceTracker) {

        boolean allInstancesTriplicated = true;

        // Iterate over SCC bad cut groups
        for (Iterator i = sccGroups.iterator(); i.hasNext();) {
            EdifCellInstanceCollection sccGroup = (EdifCellInstanceCollection) i.next();
            // Iterate over SCC bad cut instances
            for (Iterator j = sccGroup.iterator(); j.hasNext();) {
                EdifCellInstance eci = (EdifCellInstance) j.next();
                // If this instance is NOT part of the original set of SCC instances,
                //   add it to TMR
                if (!sccInstances.contains(eci)) {
                    // All the instances from a particular bad cut group must be added as a whold set.
                    // If one of them doesn't fit, we must not add any of them.
                    // Build up a list of instances to add and attempt to add them as a group.
                    // TODO: implement this!
                    // BHP: These "sccGroup" groups are bad cut groups based on the original
                    //   groups found by the SCC algorithms. But the original groups have 
                    //   already been added to TMR, so is it too late to back out of adding 
                    //   the entire group now? Do we have to go back and add the bad cut 
                    //   grouping code to the SCC code instead?
                    // BHP: The SCC code handles bad cut edges internally. The only problem
                    //   is bad cut edges that actually leave the SCC. These edges are NOT
                    //   checked in the SCC code and could potentially cause a problem for
                    //   us here. I'm not sure of the best way (or place) to handle this.
                    try {
                        if (DEBUG)
                            System.out.println("Adding SCC bad cut instance: " + eci);
                        resourceTracker.nmrInstance(eci, _replicationFactor);
                    } catch (DuplicateNMRRequestException e4) {
                        // Skip
                        continue;
                    } catch (OverutilizationEstimatedStopException e5) {
                        // DeviceUtilizationTracker says to stop adding instances
                        //   for triplication
                        if (DEBUG)
                            System.out.println("Received EstimatedStopException "
                                    + "in addSCCBadCutInstances. Halting triplication.");
                        allInstancesTriplicated = false;
                        break;
                    } catch (OverutilizationHardStopException e6) {
                        // Skip Hard Stops--can't add this instance
                        if (DEBUG)
                            System.out.println("\tCan't add instance. No " + eci.getType() + "'s left.");
                        allInstancesTriplicated = false;
                        continue;
                    }
                }
            }
        }

        return allInstancesTriplicated;
    }

    /**
     * Collapses one "string" of inputs (ECIs) to this group into the group.
     * Only pulls in the ECIs that feed into the startECI. The order of how the
     * ECIs are brought in is partially dependent on the the type1 parameter.
     * <p>
     * Type 1 (Stack-based) does a depth-first search
     * <p>
     * Type 2 (Queue-based) does a depth/breadth-first - does a breadth-first
     * search doing, but only considers the predecessors of the given ECI
     * 
     * @param startECI The ECI to create the string of ECIs from
     * @param visitedGroups
     * @param GroupConn
     * @param doInput
     * @param type1 Determines whether to use a Stack or a Queue
     * @return A List of "atomic" Sets of EdifCellInstances
     */
    protected static List collapseInputOrOutputGroupsOneString(EdifCellInstanceCollection startGroup,
            Collection visitedGroups, EdifCellInstanceCollectionGraph groupConn, boolean doInput, boolean type1) {
        // Search through all the Instances with EPRs in the input list and
        //   pull in those ECIs which only have sinks in the group.
        // 	 - This is true iff all of the ECIs output EPRs exist in the _inputEPR
        //     Set.
        // Repeat this until no additions are made to the group in one pass.
        List neighborGroups = new ArrayList();
        LinkedList groupString = new LinkedList(); // Either a Stack or a Queue
        int additionCount = 0;

        if (DEBUG)
            System.out.println("collapseInputOrOutputGroupsOneString from group: " + startGroup);
        if (DEBUG && doInput)
            System.out.print(" Collapsing one string of input logic...");
        if (DEBUG && !doInput)
            System.out.print(" Collapsing one string of output logic...");

        // Initialize the stack/queue
        groupString.add(startGroup);

        // Keep adding instances until the stack runs out
        while (!groupString.isEmpty()) {
            EdifCellInstanceCollection group;
            if (type1) // Use a Stack
                group = (EdifCellInstanceCollection) groupString.removeLast();
            else
                // Use a Queue
                group = (EdifCellInstanceCollection) groupString.removeFirst();

            // Add this group				
            if (DEBUG)
                additionCount += group.size();
            neighborGroups.add(group);
            //System.out.println("\n$$$ Adding group: "+group);

            // Add all predecessors/successors to the stack/queue
            Collection neighbors;
            if (doInput)
                neighbors = groupConn.getPredecessors(group);
            else
                neighbors = groupConn.getSuccessors(group);
            for (Iterator i = neighbors.iterator(); i.hasNext();) {
                Object neighbor = i.next();
                //////
                // i.   Skip current Group if it has already been visited (This 
                //      prevents us from following paths that have already been
                //      examined/added)
                if (visitedGroups.contains(neighbor))
                    continue;
                else {
                    //////
                    // ii.  Add this Group to the visited Set as well as the
                    //      groupString to move past it in the next iteration
                    groupString.add(neighbor);
                    visitedGroups.add(neighbor);
                    //System.out.println("\n$$$ Adding neighbor: "+neighbor);
                }
            }
        }

        if (DEBUG && doInput)
            System.out.println("One string of input collapsed. " + additionCount + " Instances added.");
        if (DEBUG && !doInput)
            System.out.println("One string of output collapsed. " + additionCount + " Instances added.");

        return neighborGroups;
    }

    /**
     * Creates an array representing the triplication status of the partial TMR
     * selection based on the different TMR sections.
     * 
     * @param triplicationStatus
     * @param allFeedbackInstancesTriplicated
     * @param feedbackInstanceCount
     * @param allInputToFeedbackInstancesTriplicated
     * @param inputToFeedbackInstanceCount
     * @param allFeedbackOutputInstancesTriplicated
     * @param feedbackOutputInstanceCount
     * @param allFeedForwardInstancesTriplicated
     * @param feedForwardInstanceCount
     */
    protected static void createTriplicationStatus(int[] triplicationStatus, boolean allFeedbackInstancesTriplicated,
            int feedbackInstanceCount, boolean allInputToFeedbackInstancesTriplicated,
            int inputToFeedbackInstanceCount, boolean allFeedbackOutputInstancesTriplicated,
            int feedbackOutputInstanceCount, boolean allFeedForwardInstancesTriplicated, int feedForwardInstanceCount) {
        // Feedback
        if (allFeedbackInstancesTriplicated == true)
            triplicationStatus[FEEDBACK] = ALL;
        else if (feedbackInstanceCount == 0)
            triplicationStatus[FEEDBACK] = NONE;
        else
            triplicationStatus[FEEDBACK] = SOME;
        // Input to Feedback
        if (allInputToFeedbackInstancesTriplicated == true)
            triplicationStatus[INPUT_TO_FEEDBACK] = ALL;
        else if (inputToFeedbackInstanceCount == 0)
            triplicationStatus[INPUT_TO_FEEDBACK] = NONE;
        else
            triplicationStatus[INPUT_TO_FEEDBACK] = SOME;
        // Feedback Output
        if (allFeedbackOutputInstancesTriplicated == true)
            triplicationStatus[FEEDBACK_OUTPUT] = ALL;
        else if (feedbackOutputInstanceCount == 0)
            triplicationStatus[FEEDBACK_OUTPUT] = NONE;
        else
            triplicationStatus[FEEDBACK_OUTPUT] = SOME;
        // Feedforward
        if (allFeedForwardInstancesTriplicated == true)
            triplicationStatus[FEED_FORWARD] = ALL;
        else if (feedForwardInstanceCount == 0)
            triplicationStatus[FEED_FORWARD] = NONE;
        else
            triplicationStatus[FEED_FORWARD] = SOME;
    }

    /**
     * Collapse the input of the given InstanceGroup objects using the collapse
     * type specified.
     * 
     * @return A List of "atomic" Sets of EdifCellInstances
     */
    protected static List getInputGroups(Collection groups, Collection visitedGroups,
            EdifCellInstanceCollectionGraph groupConn, int type) {
        List inputSets;

        switch (type) {
        case 1:
            inputSets = getInputGroupsDepthFirstOneString(groups, visitedGroups, groupConn);
            break;
        case 2:
            inputSets = getInputGroupsBreadthDepthFirstOneLevel(groups, visitedGroups, groupConn);
            break;
        default:
            inputSets = getInputGroupsBreadthFirstOneLevel(groups, visitedGroups, groupConn);
            break;
        }

        return inputSets;
    }

    /**
     * When no more input can be added, this method will return null.
     * 
     * @param group
     * @param groupConn
     * @return An ordered List of EdifCellInstanceCollection objects, which
     * contain EdifCellInstance objects
     */
    protected static List getInputGroupsDepthFirstOneString(Collection groups, Collection visitedGroups,
            EdifCellInstanceCollectionGraph groupConn) {
        return getInputOrOutputGroupsOneString(groups, visitedGroups, groupConn, true, true);
    }

    /**
     * When no more input can be added, this method will return null.
     * 
     * @param group
     * @param groupConn
     * @return An ordered List of EdifCellInstanceCollection objects, which
     * contain EdifCellInstance objects
     */
    protected static List getInputGroupsBreadthDepthFirstOneLevel(Collection groups, Collection visitedGroups,
            EdifCellInstanceCollectionGraph groupConn) {
        return getInputOrOutputGroupsOneString(groups, visitedGroups, groupConn, true, false);
    }

    /**
     * This method grabs the predecessor groups of the given Collection of
     * groups. It returns a List of groups that should be the next "groups"
     * parameter passed to this method, if the breadth-first search is to
     * continue.
     * <p>
     * When no more input can be added, this method will return null.
     * 
     * @param group
     * @param groupConn
     * @return An ordered List of EdifCellInstanceCollection objects, which
     * contain EdifCellInstance objects. The order of the List, in this case, is
     * random.
     */
    protected static List getInputGroupsBreadthFirstOneLevel(Collection groups, Collection visitedGroups,
            EdifCellInstanceCollectionGraph groupConn) {
        // Grab predecessor groups
        List inputWave = new ArrayList();
        for (Iterator i = groups.iterator(); i.hasNext();) {
            Object group = i.next();
            Collection predecessors = groupConn.getPredecessors(group);
            for (Iterator j = predecessors.iterator(); j.hasNext();) {
                Object predecessor = j.next();
                //////
                // i.   Skip current Group if it has already been visited (This 
                //      prevents us from following paths that have already been
                //      examined/added)
                if (visitedGroups.contains(predecessor))
                    continue;
                else {
                    //////
                    // ii.  Add this Group to the visited Set as well as the
                    //      "inputWave" Set to move past it in the next iteration
                    inputWave.add(predecessor);
                    visitedGroups.add(predecessor);
                }
            }
        }
        if (inputWave.isEmpty())
            return null; // No more input can be collapsed
        else
            return inputWave;
    }

    /**
     * This is a helper method for the depth-first collapse. It keeps track of
     * the EdifCellInstances to use as "startECI" parameters to the real method,
     * so the caller doesn't have to keep track of them.
     * <p>
     * The startGroups parameter is only used the first time this method is
     * called. It is ignored until the DFS has finished or _startGroupsIter has
     * been reset to null.
     * 
     * @param type1 The type of collapse to perform
     * @return A List of "atomic" Sets of EdifCellInstances
     */
    protected static List getInputOrOutputGroupsOneString(Collection startGroups, Collection visitedGroups,
            EdifCellInstanceCollectionGraph groupConn, boolean doInput, boolean type) {
        // Keep track of current group to use as start group
        EdifCellInstanceCollection group;

        if (_startGroupsIter == null) {
            /*
             * Make a copy of the startGroups Set (to freeze it at this point)
             * This Set doesn't need to updated between calls to collapse one
             * string, it will include all the groups to start with.
             */
            Collection neighbors = new ArrayList();
            if (doInput)
                for (Iterator i = startGroups.iterator(); i.hasNext();)
                    neighbors.addAll(groupConn.getPredecessors(i.next()));
            else
                for (Iterator i = startGroups.iterator(); i.hasNext();)
                    neighbors.addAll(groupConn.getSuccessors(i.next()));
            _startGroupsIter = neighbors.iterator();
            if (DEBUG)
                System.out.println("### Filling _startGroupsIter...");
        }

        if (_startGroupsIter.hasNext()) {
            group = (EdifCellInstanceCollection) _startGroupsIter.next();
        } else {
            // Reset _startGroupsIter
            _startGroupsIter = null;
            return null; // Input has been fully collapsed
        }

        // Grab the next depth-first string of logic
        return collapseInputOrOutputGroupsOneString(group, visitedGroups, groupConn, doInput, type);
    }

    /**
     * Collapse the output of the given InstanceGroup using the type specified.
     * 
     * @return A List of "atomic" Sets of EdifCellInstances
     */
    protected static List getOutputGroups(Collection groups, Collection visitedGroups,
            EdifCellInstanceCollectionGraph groupConn, int type) {
        List outputSets = null;

        switch (type) {
        case 1:
            outputSets = getOutputGroupsDepthFirstOneString(groups, visitedGroups, groupConn);
            break;
        case 2:
            outputSets = getOutputGroupsBreadthDepthFirstOneLevel(groups, visitedGroups, groupConn);
            break;
        default:
            outputSets = getOutputGroupsBreadthFirstOneLevel(groups, visitedGroups, groupConn);
            break;
        }

        return outputSets;
    }

    /**
     * When no more output can be added, this method will return null.
     * 
     * @param group
     * @param groupConn
     * @return An ordered List of EdifCellInstanceCollection objects, which
     * contain EdifCellInstance objects
     */
    protected static List getOutputGroupsDepthFirstOneString(Collection groups, Collection visitedGroups,
            EdifCellInstanceCollectionGraph groupConn) {
        return getInputOrOutputGroupsOneString(groups, visitedGroups, groupConn, false, true);
    }

    /**
     * When no more output can be added, this method will return null.
     * 
     * @param group
     * @param groupConn
     * @return An ordered List of EdifCellInstanceCollection objects, which
     * contain EdifCellInstance objects
     */
    protected static List getOutputGroupsBreadthDepthFirstOneLevel(Collection groups, Collection visitedGroups,
            EdifCellInstanceCollectionGraph groupConn) {
        return getInputOrOutputGroupsOneString(groups, visitedGroups, groupConn, false, false);
    }

    /**
     * This method grabs the successor groups of the given Collection of groups.
     * It returns a List of groups that should be the next "groups" parameter
     * passed to this method, if the breadth-first search is to continue.
     * <p>
     * When no more output can be added, this method will return null.
     * 
     * @param group
     * @param groupConn
     * @return An ordered List of EdifCellInstanceCollection objects, which
     * contain EdifCellInstance objects. The order of the List, in this case, is
     * random.
     */
    protected static List getOutputGroupsBreadthFirstOneLevel(Collection groups, Collection visitedGroups,
            EdifCellInstanceCollectionGraph groupConn) {
        // Grab successor groups
        List outputWave = new ArrayList();
        for (Iterator i = groups.iterator(); i.hasNext();) {
            Object group = i.next();
            //outputWave.addAll(groupConn.getSuccessors(group));
            Collection successors = groupConn.getSuccessors(group);
            for (Iterator j = successors.iterator(); j.hasNext();) {
                Object successor = j.next();
                //////
                // i.   Skip current Group if it has already been visited (This 
                //      prevents us from following paths that have already been
                //      examined/added)
                if (visitedGroups.contains(successor))
                    continue;
                else {
                    //////
                    // ii.  Add this Group to the visited Set as well as the
                    //      "outputWave" Set to move past it in the next iteration
                    outputWave.add(successor);
                    visitedGroups.add(successor);
                }
            }
        }
        if (outputWave.isEmpty())
            return null; // No more input can be collapsed
        else
            return outputWave;
    }

    protected static void initGetGroups() {
        _startGroupsIter = null;
    }

    /**
     * @param groupConn
     * @param resourceTracker
     * @param additionType
     * @param doInput
     * @param visitedGroups
     * @param groupsToTMR
     * @return Status of triplication: true if ALL instance were triplicated,
     * false if some were skipped or wouldn't fit.
     */
    protected static boolean tmrInputOrOutputGroups(EdifCellInstanceCollectionGraph groupConn,
            DeviceUtilizationTracker resourceTracker, int additionType, boolean doInput, HashSet visitedGroups,
            List groupsToTMR) throws OverutilizationEstimatedStopException {

        boolean allInstancesTriplicated = true;
        //boolean stopTriplication = false;
        int instancesAdded = 0;

        //////
        //   c) Iterate over the "groupsToTMR" List
        while (groupsToTMR != null) {
            Iterator tmrGroupsIt = groupsToTMR.iterator();
            // Add groups one at a time
            while (tmrGroupsIt.hasNext()) {
                Collection atomicSet = (Collection) tmrGroupsIt.next();

                //////
                //      iii. Attempt to TMR this Group with the device tracker
                //      iv.  Catch exceptions:
                //             - Quit on receipt of EstimatedStopException (Device Full)
                //             - Skip instances that cause HardStopExceptions
                //             - Skip instances that have already been added
                //                 (DuplicateNMRRequestException)
                try {
                    resourceTracker.nmrInstances(atomicSet, _replicationFactor);
                    // Add atomic sets of ECIs to the input/outputList if there is room 
                    if (DEBUG) {
                        if (doInput) {
                            _inputSets.add(atomicSet);
                            //System.out.println("Input Group added: "+atomicSet);
                        } else {
                            _outputSets.add(atomicSet);
                            //System.out.println("Output Group added: "+atomicSet);
                        }
                        instancesAdded += atomicSet.size();
                        _ECIsRelatedToFB.addAll(atomicSet);
                    }
                } catch (DuplicateNMRRequestException e1) {
                    // Don't want to follow this branch anymore???
                    /*
                     * TODO: Choose what to do here. We could use this instead
                     * of the "exclude list" to figure out which nodes we have
                     * already visited, but this may cause problems when running
                     * this method the second time (e.g. doing outputs after
                     * having done inputs) because we want to not follow the
                     * direction that has already been done, but we *do* want to
                     * continue along the direction that has *not* been explored
                     * yet.
                     */
                    //nextWave.remove(atomicSet);
                    continue;
                } catch (OverutilizationEstimatedStopException e2) {
                    // Ran out of logic while trying to triplicate this 
                    //   atomic set. Thus, none of the set was triplicated.
                    // DeviceUtilizationTracker says to stop adding instances
                    //   for triplication
                    if (DEBUG)
                        System.out.println("Received EstimatedStopException. Halting triplication.");
                    allInstancesTriplicated = false;
                    throw e2;
                } catch (OverutilizationHardStopException e3) {
                    // Ran out of resources for (at least) one of the instances
                    //   included in this atomic set. Thus, none of the set
                    //   was triplicated.
                    if (DEBUG)
                        System.out.println("Received EstimatedStopException. Halting triplication.");
                    // Can't triplicate all instances. Record this for the
                    //   return value
                    allInstancesTriplicated = false;
                    continue;
                }
            }

            //////
            //   d) Repeat from step b) until EstimatedStopException is received
            // Next wave/string of output ECIs
            if (doInput)
                groupsToTMR = getInputGroups(groupsToTMR, visitedGroups, groupConn, additionType);
            else
                groupsToTMR = getOutputGroups(groupsToTMR, visitedGroups, groupConn, additionType);
        }

        // How many ECIs were added?
        if (DEBUG)
            System.out.println("Successfully added " + instancesAdded + " ECIs for TMR.");

        //return stopTriplication;
        return allInstancesTriplicated;
    }

    // For the depth-first and breadth-depth-first searches
    protected static Iterator _startGroupsIter = null;

    // Constants
    public static final int DEFAULT_COLLAPSE_TYPE = 3;

    // TMR Sections
    public static final int FEEDBACK = 0;

    public static final int INPUT_TO_FEEDBACK = 1;

    public static final int FEEDBACK_OUTPUT = 2;

    public static final int FEED_FORWARD = 3;

    // Triplication Status
    public static final int NONE = 0;

    public static final int SOME = 1;

    public static final int ALL = 2;

    // These are only used for debugging purposes
    protected static List _inputSets;

    protected static List _outputSets;

    protected static Set _ECIsRelatedToFB;

    public static boolean DEBUG = false;

    public static void main(String[] args) throws OverutilizationException {
        if (args.length < 1) {
            System.out.println("USAGE: java InputOuputDecomposition <filename> [merge options]");
            System.exit(-1);
        }

        // Turn on DEBUG when running this main method
        DEBUG = true;

        // Parse and merge
        EdifCell cell = XilinxMergeParser.parseAndMergeXilinx(args);

        // Flatten Cell
        System.out.println("Flattening Cell...");
        EdifCell flatCell = null;
        try {
            flatCell = new NewFlattenedEdifCell(cell);
        } catch (EdifNameConflictException e1) {
            e1.toRuntime();
        } catch (InvalidEdifNameException e1) {
            e1.toRuntime();
        }
        System.out.println("\tFlattened circuit contains " + EdifUtils.countRecursivePrimitives(flatCell)
                + " primitives, " + EdifUtils.countRecursiveNets(flatCell) + " nets, and "
                + EdifUtils.countPortRefs(flatCell, true) + " net connections");

        // Create Device Utilization Tracker
        DeviceUtilizationTracker duTracker = null;
        try {
            duTracker = new XilinxVirtexDeviceUtilizationTracker(flatCell, "XCV1000FG680");
            // DEBUG with unlimited Virtex Device
            //duTracker = new XilinxVirtexDeviceUtilizationTracker(flatCell, 
            //		"UNLIMITED");
        } catch (OverutilizationException e) {
            throw new EdifRuntimeException("ERROR: Initial contents of cell " + flatCell + " do not fit into part.");
        }
        // Need a TMRArchitecture object
        NMRArchitecture tmrArch = new XilinxTMRArchitecture();

        // Take care of SCCs
        System.out.println("Finding SCCs...");
        EdifCellInstanceGraph connectivity = new EdifCellInstanceGraph(flatCell, false);
        SCCDepthFirstSearch sccDFS = new SCCDepthFirstSearch(connectivity);
        System.out.println("Found " + sccDFS.getTrees().size() + " SCCs in this design.");

        // Add SCC instances to TMR
        // 7. Perform feedback analysis and determine cut set
        List cutSet = new ArrayList();
        NMRGraphUtilities.tmrSCCsUsingSCCDecomposition(sccDFS, tmrArch, duTracker, true, cutSet);

        // Add Inputs and Outputs
        System.out.println("Performing I/O Decomposition...");
        tmrSCCInputAndOutput(flatCell, connectivity, duTracker, tmrArch, sccDFS);
        Collection ECIsRelatedToFeedback = getCellInstancesRelatedToFeedback();
        System.out.println("I/O Decomposition resulted in " + ECIsRelatedToFeedback.size()
                + " ECIs related to feedback found.");

        // REPORT ON RESULTS

        // Create Collection of feed-forward ECIs for comparison
        Collection feedForwardECIs = new LinkedHashSet(flatCell.getSubCellList());
        feedForwardECIs.removeAll(ECIsRelatedToFeedback);

        // How many input cone instances?
        Set inputInstances = new LinkedHashSet();
        int inputCount = 0;
        Iterator i = getInputSets().iterator();
        while (i.hasNext()) {
            Collection inECIs = (Collection) i.next();
            inputCount += inECIs.size();
            inputInstances.addAll(inECIs);
        }
        System.out.println("  " + inputCount + " of these are part of the input cone.");
        System.out.println("  " + inputInstances.size() + " of these are part of the input cone.");

        // How many in the feedback output logic?
        Set outputInstances = new LinkedHashSet();
        int outputCount = 0;
        i = getOutputSets().iterator();
        while (i.hasNext()) {
            Collection outECIs = (Collection) i.next();
            outputCount += outECIs.size();
            outputInstances.addAll(outECIs);
        }
        System.out.println("  " + outputCount + " of these are part of the FB output logic.");
        System.out.println("  " + outputInstances.size() + " of these are part of the FB output logic.");

        System.out.println("\nPost IODecomp utilization estimate:\n" + duTracker);
    }

    public static String msToString(long milliseconds) {
        long sec = milliseconds / 1000;
        long min = sec / 60;
        long hour = min / 60;
        milliseconds %= 1000;
        sec %= 60;
        min %= 60;
        DecimalFormat twoDigits = new DecimalFormat("00");

        return new String(twoDigits.format(hour) + ":" + twoDigits.format(min) + ":" + twoDigits.format(sec) + "."
                + milliseconds);
    }

    /**
     * Use to indicate triplication
     */
    private static final int _replicationFactor = 3;
}
