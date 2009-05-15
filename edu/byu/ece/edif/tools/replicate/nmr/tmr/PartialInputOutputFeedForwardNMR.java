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
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.DeviceUtilizationTracker;
import edu.byu.ece.edif.tools.replicate.nmr.DuplicateNMRRequestException;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.NMRGraphUtilities;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationEstimatedStopException;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationException;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationHardStopException;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationType;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationUtilizationTracker;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxNMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxVirtexDeviceUtilizationTracker;
import edu.byu.ece.edif.util.graph.EdifCellBadCutGroupings;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollection;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollectionGraph;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.graph.dfs.BasicDepthFirstSearchTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * PartialInputOutputFeedForwardNMR Breaks down the input and output of the
 * given EdifCell by inputs and outputs to SCCs.
 * 
 * @author Brian Pratt, Michael Wirthlin
 */
public class PartialInputOutputFeedForwardNMR {

    /**
     * Default to NMR both input and output, using input and output collapse
     * types of Breadth First (type 3)
     * 
     * @return Status of replication
     */
    public static int[] nmrSCCInputAndOutput(EdifCell cell, EdifCellInstanceGraph ecic,
            ReplicationUtilizationTracker resourceTracker, NMRArchitecture nmrArch, SCCDepthFirstSearch sccs, ReplicationType replicationType, boolean override) {
        return nmrSCCInputAndOutput(cell, ecic, resourceTracker, nmrArch, sccs, true, true, true,
                DEFAULT_COLLAPSE_TYPE, DEFAULT_COLLAPSE_TYPE, replicationType, override);
    }

    /**
     * Select the input and output to be replicated in the given EdifCell.
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
     * the replication together because of architecture-specific limits.
     * 
     * @param cell The given EdifCell
     * @param connectivity
     * @param resourceTracker The DeviceUtilizationTracker being used
     * @param nmrArch The NMRArchitecture
     * @param sccDFS The SCCDepthFirstSearch being used
     * @param nmrInput Whether or not input ports should be replicated
     * @param nmrOutput Whether or not output ports should be replicated
     * @param nmrFeedForward Whether or not the feed forward section should be
     * replicated
     * @param inputAdditionType An integer indicating which of three different
     * algorithms should be used to determine the order of instances added in
     * the Input to Feedback section.
     * @param outputAdditionType An integer indicating which of three different
     * algorithms should be used to determine the order of instances added in
     * the Feedback Output section.
     * @return Status of replication, encoded as an array of int's.
     */
    public static int[] nmrSCCInputAndOutput(EdifCell cell, EdifCellInstanceGraph connectivity,
            ReplicationUtilizationTracker resourceTracker, NMRArchitecture nmrArch, SCCDepthFirstSearch sccDFS,
            boolean nmrInput, boolean nmrOutput, boolean nmrFeedForward, int inputAdditionType, int outputAdditionType, ReplicationType replicationType, boolean override) {

        //////
        // 1. Create Bad Cut Groupings
        if (DEBUG)
            System.out.println("Creating bad cut groupings . . .");
        // Create Connectivity for this EdifCell if not provided
        //   (Don't include top level ports)
        if (connectivity == null)
            connectivity = new EdifCellInstanceGraph(cell, false);
        EdifCellBadCutGroupings groupings = new EdifCellBadCutGroupings(cell, nmrArch, connectivity);

        return nmrSCCInputAndOutput(cell, connectivity, groupings, resourceTracker, nmrArch, sccDFS, nmrInput,
                nmrOutput, nmrFeedForward, inputAdditionType, outputAdditionType, replicationType, override);
    }

    /**
     * Select the input and output to be replicated in the given EdifCell.
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
     * the replication together because of architecture-specific limits.
     * 
     * @param cell The given EdifCell
     * @param connectivity
     * @param badCutGroupings The EdifCellBadCutGroupings object for this graph
     * @param resourceTracker The DeviceUtilizationTracker being used
     * @param nmrArch The NMRArchitecture
     * @param sccDFS The SCCDepthFirstSearch being used
     * @param nmrInput Whether or not input ports should be replicated
     * @param nmrOutput Whether or not output ports should be replicated
     * @param nmrFeedForward Whether or not the feed forward section should be
     * replicated
     * @param inputAdditionType An integer indicating which of three different
     * algorithms should be used to determine the order of instances added in
     * the Input to Feedback section.
     * @param outputAdditionType An integer indicating which of three different
     * algorithms should be used to determine the order of instances added in
     * the Feedback Output section.
     * @return Status of replication, encoded as an array of int's.
     */
    public static int[] nmrSCCInputAndOutput(EdifCell cell, EdifCellInstanceGraph connectivity,
            EdifCellBadCutGroupings badCutGroupings, ReplicationUtilizationTracker resourceTracker, NMRArchitecture nmrArch,
            SCCDepthFirstSearch sccDFS, boolean nmrInput, boolean nmrOutput, boolean nmrFeedForward,
            int inputAdditionType, int outputAdditionType, ReplicationType replicationType, boolean override) {
        // Initialize DEBUG structures
        if (DEBUG) {
            _inputSets = new ArrayList();
            _outputSets = new ArrayList();
            _ECIsRelatedToFB = new LinkedHashSet();
        }
        long startTime, elapsedTime;
        startTime = System.currentTimeMillis();

        boolean allFeedbackInstancesReplicated = false;
        boolean allInputToFeedbackInstancesReplicated = false;
        boolean allFeedbackOutputInstancesReplicated = false;
        boolean allFeedForwardInstancesReplicated = false;

        int replicationStatus[] = new int[4];

        /*
         * Steps: 1. Create Bad Cut Groupings 2. Add SCCs to Bad Cut Groupings -
         * Bad Cut instances may be added into groups here. Add them to NMR! 3.
         * Create Group Connectivity 4. Do topological sort of SCCs(?) 5. Add
         * input/output instances to DeviceUtilizationTracker 6. Add left-over
         * instances to DeviceUtilizationTracker
         */

        int initialInstanceCount = resourceTracker.getInstancesWithReplicationType(replicationType).size();
        //int initialInstanceCount = resourceTracker.getCurrentNMRInstances().size();

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
                        System.out.println("WARNING: " + "PartialInputOutputFeedForwardNMR ignoring " + node
                                + "of type " + node.getClass());
                }
            }

            // Remove non-EdifCellInstances (e.g. top-level ports) from Collection
            //            for (Iterator j = scc.iterator(); j.hasNext();) {
            //                Object next = j.next();
            //                if (!(next instanceof EdifCellInstance)) {
            //                    if (DEBUG)
            //                        System.out.println("WARNING: " + "PartialInputOutputFeedForwardNMR NOT replicating" + next
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

        // Bad Cut instances may be added into groups here. Add them to NMR!
        allFeedbackInstancesReplicated = addSCCBadCutInstances(allSCCInstances, sccGroups, resourceTracker, replicationType, override);

        int feedbackInstanceCount = resourceTracker.getInstancesWithReplicationType(replicationType).size() - initialInstanceCount;
//        int feedbackInstanceCount = resourceTracker.getCurrentNMRInstances().size() - initialInstanceCount;

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
            System.out.println("Adding inputs and outputs of SCCs to NMR . . .");
        // These methods do NOT modify the groupConnectivity
        if (DEBUG)
            System.out.println("### Number of Instances to NMR before adding SCC Inputs or Outputs: "
                    + resourceTracker.getInstancesWithReplicationType(replicationType).size());
        // 5.a) Add Logic feeding into the SCCs to the resource tracker
        if (nmrInput) {
            allInputToFeedbackInstancesReplicated = addInputECIsToNMR(sortedSCCs, badCutGroupConn, resourceTracker,
                    inputAdditionType, replicationType, override);
            if (DEBUG)
                System.out.println("### Number of Instances to NMR after adding SCC Inputs: "
                        + resourceTracker.getInstancesWithReplicationType(replicationType).size());
        }
        int inputToFeedbackInstanceCount = resourceTracker.getInstancesWithReplicationType(replicationType).size() - feedbackInstanceCount;
        // 5.b) Add Logic driven by the SCCs to the resource tracker
        if (nmrOutput) {
            allFeedbackOutputInstancesReplicated = addOutputECIsToNMR(sortedSCCs, badCutGroupConn, resourceTracker,
                    outputAdditionType, replicationType, override);
            if (DEBUG)
                System.out.println("### Number of Instances to NMR after adding SCC Outputs: "
                        + resourceTracker.getInstancesWithReplicationType(replicationType).size());
        }
        int feedbackOutputInstanceCount = resourceTracker.getInstancesWithReplicationType(replicationType).size()
                - inputToFeedbackInstanceCount;

        //////
        // 6. Add "Feed-forward" logic (all logic that isn't part of the
        //    other sections)
        if (nmrFeedForward) {
            allFeedForwardInstancesReplicated = addFeedForwardECIsToNMR(sortedSCCs, badCutGroupConn, resourceTracker,
                    false, replicationType, override);
            if (DEBUG)
                System.out.println("### Number of Instances to NMR after adding Feed Forward logic: "
                        + resourceTracker.getInstancesWithReplicationType(replicationType).size());
        }
        int feedForwardInstanceCount = resourceTracker.getInstancesWithReplicationType(replicationType).size() - feedbackOutputInstanceCount;

        elapsedTime = System.currentTimeMillis() - startTime;
        if (DEBUG)
            System.out.println("I/O decomposition took " + msToString(elapsedTime));

        // Return status as array of ints that can be decoded later.
        createReplicationStatus(replicationStatus, allFeedbackInstancesReplicated, feedbackInstanceCount,
                allInputToFeedbackInstancesReplicated, inputToFeedbackInstanceCount,
                allFeedbackOutputInstancesReplicated, feedbackOutputInstanceCount, allFeedForwardInstancesReplicated,
                feedForwardInstanceCount);

        return replicationStatus;
    }

    /**
     * Adds the input EdifCellInstances of the given sorted List of SCCs to the
     * DeviceUtilizationResouceTracker until that object throws an "Estimated
     * Stop" Exception. This method will skip over "Hard Stop" Exceptions if
     * encountered. (i.e. It will add all Instances before and after the
     * non-replicatable Instance(s)).
     * <p>
     * The type of collapse (breadth-first, depth-first) is dependent on the
     * inputAdditionType parameter.
     * 
     * @param sortedSCCs A List of EdifCellInstanceCollection objects
     * representing the SCCs. These should be in topological order.
     * @param groupConn The EdifCellInstanceCollectionGraph object associated
     * with this EdifCell that contains the sortedSCC groups
     * @param resourceTracker The DeviceUtilizationTracker object to add the
     * replicated Instances to
     * @param inputAdditionType Which type of input collapse to perform
     * @return Status of replication: true if ALL instance were replicated,
     * false if some were skipped or wouldn't fit.
     */
    public static boolean addInputECIsToNMR(List sortedSCCs, EdifCellInstanceCollectionGraph groupConn,
            ReplicationUtilizationTracker resourceTracker, int inputAdditionType, ReplicationType replicationType, boolean override) {
        return addInputOrOutputECIsToNMR(sortedSCCs, groupConn, resourceTracker, inputAdditionType, true, replicationType, override);
    }

    /**
     * Adds the output EdifCellInstances of the given sorted List of SCCs to the
     * DeviceUtilizationResouceTracker until that object throws an "Estimated
     * Stop" Exception. This method will skip over "Hard Stop" Exceptions if
     * encountered. (i.e. It will add all Instances before and after the
     * non-replicatable Instance(s)).
     * <p>
     * The type of collapse (breadth-first, depth-first) is dependent on the
     * outputAdditionType parameter.
     * 
     * @param sortedSCCs A List of EdifCellInstanceCollection objects
     * representing the SCCs. These should be in topological order.
     * @param groupConn The EdifCellInstanceCollectionGraph object associated
     * with this EdifCell that contains the sortedSCC groups
     * @param resourceTracker The DeviceUtilizationTracker object to add the
     * replicated Instances to
     * @param outputAdditionType Which type of output collapse to perform
     * @return Status of replication: true if ALL instance were replicated,
     * false if some were skipped or wouldn't fit.
     */
    public static boolean addOutputECIsToNMR(List sortedSCCs, EdifCellInstanceCollectionGraph groupConn,
            ReplicationUtilizationTracker resourceTracker, int outputAdditionType, ReplicationType replicationType, boolean override) {
        return addInputOrOutputECIsToNMR(sortedSCCs, groupConn, resourceTracker, outputAdditionType, false, replicationType, override);
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
     * after the non-replicatable Instance(s)).
     * 
     * @param sccGroups A Collection of EdifCellInstanceCollection objects
     * representing the SCCs. These are needed to avoid feedback loops.
     * @param groupConn The EdifCellInstanceCollectionGraph object associated
     * with this EdifCell
     * @param resourceTracker The DeviceUtilizationTracker object to add the
     * replicated Instances to
     * @param forward Indicates whether to start at the inputs (forward) or
     * outputs (!forward)
     * @return Status of replication: true if ALL instance were replicated,
     * false if some were skipped or wouldn't fit.
     */
    protected static boolean addFeedForwardECIsToNMR(Collection sccGroups, EdifCellInstanceCollectionGraph groupConn,
            ReplicationUtilizationTracker resourceTracker, boolean forward, ReplicationType replicationType, boolean override) {

        boolean allInstancesReplicated = true;
        List groupsToNMR = new ArrayList();

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
                groupsToNMR.add(node);
        }

        // Iterate over the "groupsToNMR" List, following Graph and adding
        //   instances to NMR
        // Repeat until EstimatedStopException is received
        // (forward = true means we should follow the graph forward, just like
        //    the "doOutput" case for SCCs)
        try {
            nmrInputOrOutputGroups(groupConn, resourceTracker, DEFAULT_COLLAPSE_TYPE, !forward, visitedGroups,
                    groupsToNMR, replicationType, override);
        } catch (OverutilizationEstimatedStopException e) {
            allInstancesReplicated = false;
        }

        return allInstancesReplicated;
    }

    /**
     * Adds the input/output EdifCellInstances of the given sorted List of SCCs
     * to the DeviceUtilizationResouceTracker until that object throws an
     * "Estimated Stop" Exception. This method will skip over "Hard Stop"
     * Exceptions if encountered. (i.e. It will add all Instances before and
     * after the non-replicatable Instance(s)).
     * <p>
     * The type of collapse (breadth-first, depth-first) is dependent on the
     * _inputAdditionType/_outputAdditionType variables.
     * 
     * @param sortedSCCs A List of EdifCellInstanceCollection objects
     * representing the SCCs. These should be in topological order.
     * @param groupConn The EdifCellInstanceCollectionGraph object associated
     * with this EdifCell that contains the sortedSCC groups
     * @param resourceTracker The DeviceUtilizationTracker object to add the
     * replicated Instances to
     * @param additionType Which type of input/output collapse to perform
     * @param doInput Indicates whether to add the inputs to the SCCs or the
     * outputs from the SCCs
     * @return Status of replication: true if ALL instance were replicated,
     * false if some were skipped or wouldn't fit.
     */
    protected static boolean addInputOrOutputECIsToNMR(List sortedSCCs, EdifCellInstanceCollectionGraph groupConn,
            ReplicationUtilizationTracker resourceTracker, int additionType, boolean doInput, ReplicationType replicationType, boolean override) {

        boolean allInstancesReplicated = true;

        //////
        // STEPS:
        // 1. Iterate over sorted list of SCC Groups (sorted in topological order)
        //     - Iterate over the list forward when adding SCC inputs and 
        //       backwards when adding SCC outputs
        //   a) Initialize the "nextWave" List to contain the current SCC Group
        //   b) Get the next set of Groups ("groupsToNMR") to add to NMR 
        //      (the predecessors or successors of the "nextWave" List of Groups)
        //   c) Iterate over the "groupsToNMR" List
        //      i.   Skip current Group if it has already been visited (This 
        //           prevents us from following paths that have already been
        //           examined/added)
        //      ii.  Add this Group to the visited Set as well as the
        //           "nextWave" Set to move past it in the next iteration
        //      iii. Attempt to NMR this Group with the device tracker
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
            //   b) Get the next set of Groups ("groupsToNMR") to add to NMR 
            //      (the predecessors or successors of the "nextWave" List of Groups)
            List groupsToNMR;
            initGetGroups();
            if (doInput)
                groupsToNMR = getInputGroups(firstWave, visitedGroups, groupConn, additionType);
            else
                groupsToNMR = getOutputGroups(firstWave, visitedGroups, groupConn, additionType);

            //////
            //   c) Iterate over the "groupsToNMR" List
            //   d) Repeat until EstimatedStopException is received
            try {
                // Record if any instances were missed (due to HardStopExceptions)
                allInstancesReplicated = nmrInputOrOutputGroups(groupConn, resourceTracker, additionType, doInput,
                        visitedGroups, groupsToNMR, replicationType, override);
            } catch (OverutilizationEstimatedStopException e) {
                // Record if any instances were missed (due to no space left on chip)
                allInstancesReplicated = false;
                break;
            }
        }

        return allInstancesReplicated;
    }

    /**
     * Adds instances that are part of the SCC bad cut groupings, but not part
     * of the original SCCs to NMR.
     * 
     * @param sccInstances Instances in the original SCC groups
     * @param sccGroups SCC bad cut groupings
     * @param resourceTracker DeviceUtilizationTracker to NMR instances
     * @return Status of replication: true if ALL instance were replicated,
     * false if some were skipped or wouldn't fit.
     */
    protected static boolean addSCCBadCutInstances(Collection sccInstances, Collection sccGroups,
            ReplicationUtilizationTracker resourceTracker, ReplicationType replicationType, boolean override) {

        boolean allInstancesReplicated = true;

        // Iterate over SCC bad cut groups
        for (Iterator i = sccGroups.iterator(); i.hasNext();) {
            EdifCellInstanceCollection sccGroup = (EdifCellInstanceCollection) i.next();
            // Iterate over SCC bad cut instances
            for (Iterator j = sccGroup.iterator(); j.hasNext();) {
                EdifCellInstance eci = (EdifCellInstance) j.next();
                // If this instance is NOT part of the original set of SCC instances,
                //   add it to NMR
                if (!sccInstances.contains(eci)) {
                    // All the instances from a particular bad cut group must be added as a whold set.
                    // If one of them doesn't fit, we must not add any of them.
                    // Build up a list of instances to add and attempt to add them as a group.
                    // TODO: implement this!
                    // BHP: These "sccGroup" groups are bad cut groups based on the original
                    //   groups found by the SCC algorithms. But the original groups have 
                    //   already been added to NMR, so is it too late to back out of adding 
                    //   the entire group now? Do we have to go back and add the bad cut 
                    //   grouping code to the SCC code instead?
                    // BHP: The SCC code handles bad cut edges internally. The only problem
                    //   is bad cut edges that actually leave the SCC. These edges are NOT
                    //   checked in the SCC code and could potentially cause a problem for
                    //   us here. I'm not sure of the best way (or place) to handle this.
                    try {
                        if (DEBUG)
                            System.out.println("Adding SCC bad cut instance: " + eci);
                        resourceTracker.addToTracker(eci, replicationType, override);
//                        resourceTracker.nmrInstance(eci, _replicationFactor);
                    } catch (DuplicateNMRRequestException e4) {
                        // Skip
                        continue;
                    } catch (OverutilizationEstimatedStopException e5) {
                        // DeviceUtilizationTracker says to stop adding instances
                        //   for replication
                        if (DEBUG)
                            System.out.println("Received EstimatedStopException "
                                    + "in addSCCBadCutInstances. Halting replication.");
                        allInstancesReplicated = false;
                        break;
                    } catch (OverutilizationHardStopException e6) {
                        // Skip Hard Stops--can't add this instance
                        if (DEBUG)
                            System.out.println("\tCan't add instance. No " + eci.getType() + "'s left.");
                        allInstancesReplicated = false;
                        continue;
                    }
                }
            }
        }

        return allInstancesReplicated;
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
     * Creates an array representing the replication status of the partial NMR
     * selection based on the different NMR sections.
     * 
     * @param replicationStatus
     * @param allFeedbackInstancesReplicated
     * @param feedbackInstanceCount
     * @param allInputToFeedbackInstancesReplicated
     * @param inputToFeedbackInstanceCount
     * @param allFeedbackOutputInstancesReplicated
     * @param feedbackOutputInstanceCount
     * @param allFeedForwardInstancesReplicated
     * @param feedForwardInstanceCount
     */
    protected static void createReplicationStatus(int[] replicationStatus, boolean allFeedbackInstancesReplicated,
            int feedbackInstanceCount, boolean allInputToFeedbackInstancesReplicated,
            int inputToFeedbackInstanceCount, boolean allFeedbackOutputInstancesReplicated,
            int feedbackOutputInstanceCount, boolean allFeedForwardInstancesReplicated, int feedForwardInstanceCount) {
        // Feedback
        if (allFeedbackInstancesReplicated == true)
            replicationStatus[FEEDBACK] = ALL;
        else if (feedbackInstanceCount == 0)
            replicationStatus[FEEDBACK] = NONE;
        else
            replicationStatus[FEEDBACK] = SOME;
        // Input to Feedback
        if (allInputToFeedbackInstancesReplicated == true)
            replicationStatus[INPUT_TO_FEEDBACK] = ALL;
        else if (inputToFeedbackInstanceCount == 0)
            replicationStatus[INPUT_TO_FEEDBACK] = NONE;
        else
            replicationStatus[INPUT_TO_FEEDBACK] = SOME;
        // Feedback Output
        if (allFeedbackOutputInstancesReplicated == true)
            replicationStatus[FEEDBACK_OUTPUT] = ALL;
        else if (feedbackOutputInstanceCount == 0)
            replicationStatus[FEEDBACK_OUTPUT] = NONE;
        else
            replicationStatus[FEEDBACK_OUTPUT] = SOME;
        // Feedforward
        if (allFeedForwardInstancesReplicated == true)
            replicationStatus[FEED_FORWARD] = ALL;
        else if (feedForwardInstanceCount == 0)
            replicationStatus[FEED_FORWARD] = NONE;
        else
            replicationStatus[FEED_FORWARD] = SOME;
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
     * @param groupsToNMR
     * @return Status of replication: true if ALL instance were replicated,
     * false if some were skipped or wouldn't fit.
     */
    protected static boolean nmrInputOrOutputGroups(EdifCellInstanceCollectionGraph groupConn,
            ReplicationUtilizationTracker resourceTracker, int additionType, boolean doInput, HashSet visitedGroups,
            List groupsToNMR, ReplicationType replicationType, boolean override) throws OverutilizationEstimatedStopException {

        boolean allInstancesReplicated = true;
        //boolean stopReplication = false;
        int instancesAdded = 0;

        //////
        //   c) Iterate over the "groupsToNMR" List
        while (groupsToNMR != null) {
            Iterator nmrGroupsIt = groupsToNMR.iterator();
            // Add groups one at a time
            while (nmrGroupsIt.hasNext()) {
                Collection atomicSet = (Collection) nmrGroupsIt.next();

                //////
                //      iii. Attempt to NMR this Group with the device tracker
                //      iv.  Catch exceptions:
                //             - Quit on receipt of EstimatedStopException (Device Full)
                //             - Skip instances that cause HardStopExceptions
                //             - Skip instances that have already been added
                //                 (DuplicateNMRRequestException)
                try {
                	resourceTracker.addToTrackerAtomic(atomicSet, replicationType, override);
//                    resourceTracker.nmrInstancesAtomic(atomicSet, _replicationFactor);
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
                    // Ran out of logic while trying to replicate this 
                    //   atomic set. Thus, none of the set was replicated.
                    // DeviceUtilizationTracker says to stop adding instances
                    //   for replication
                    if (DEBUG)
                        System.out.println("Received EstimatedStopException. Halting replication.");
                    allInstancesReplicated = false;
                    throw e2;
                } catch (OverutilizationHardStopException e3) {
                    // Ran out of resources for (at least) one of the instances
                    //   included in this atomic set. Thus, none of the set
                    //   was replicated.
                    if (DEBUG)
                        System.out.println("Received EstimatedStopException. Halting replication.");
                    // Can't replicate all instances. Record this for the
                    //   return value
                    allInstancesReplicated = false;
                    continue;
                }
            }

            //////
            //   d) Repeat from step b) until EstimatedStopException is received
            // Next wave/string of output ECIs
            if (doInput)
                groupsToNMR = getInputGroups(groupsToNMR, visitedGroups, groupConn, additionType);
            else
                groupsToNMR = getOutputGroups(groupsToNMR, visitedGroups, groupConn, additionType);
        }

        // How many ECIs were added?
        if (DEBUG)
            System.out.println("Successfully added " + instancesAdded + " ECIs for NMR.");

        //return stopReplication;
        return allInstancesReplicated;
    }

    // For the depth-first and breadth-depth-first searches
    protected static Iterator _startGroupsIter = null;

    // Constants
    public static final int DEFAULT_COLLAPSE_TYPE = 3;

    // NMR Sections
    public static final int FEEDBACK = 0;

    public static final int INPUT_TO_FEEDBACK = 1;

    public static final int FEEDBACK_OUTPUT = 2;

    public static final int FEED_FORWARD = 3;

    // Replication Status
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
            flatCell = new FlattenedEdifCell(cell);
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
        ReplicationUtilizationTracker rTracker = new ReplicationUtilizationTracker(duTracker);
        
        // Need a NMRArchitecture object
        NMRArchitecture nmrArch = new XilinxNMRArchitecture();
        
        ReplicationType replicationType = TMRReplicationType.getInstance(nmrArch);
        boolean override = true;
        
        // Take care of SCCs
        System.out.println("Finding SCCs...");
        EdifCellInstanceGraph connectivity = new EdifCellInstanceGraph(flatCell, false);
        SCCDepthFirstSearch sccDFS = new SCCDepthFirstSearch(connectivity);
        System.out.println("Found " + sccDFS.getTrees().size() + " SCCs in this design.");

        // Add SCC instances to NMR
        // 7. Perform feedback analysis and determine cut set
        List cutSet = new ArrayList();
        NMRGraphUtilities.nmrSCCsUsingSCCDecomposition(sccDFS, nmrArch, rTracker, true, NMRGraphUtilities.DEFAULT_SCC_SORT_TYPE, replicationType, override);       
        
        // Add Inputs and Outputs
        System.out.println("Performing I/O Decomposition...");
        nmrSCCInputAndOutput(flatCell, connectivity, rTracker, nmrArch, sccDFS, replicationType, override);
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
     * Use to indicate replication
     */
    private static final int _replicationFactor = 3;
}
