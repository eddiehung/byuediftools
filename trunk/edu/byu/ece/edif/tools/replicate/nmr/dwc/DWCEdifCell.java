/*
 * An EdifCell using duplex with detection
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
package edu.byu.ece.edif.tools.replicate.nmr.dwc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.NMREdifCell;

/**
 * Duplex with detection
 * <p>
 * <h2>1. Where to place detectors</h2>
 * <p>
 * There is a trade-off between number of detectors and time to detect a
 * sensitive failure. If you have a lot of detectors, you will detect the
 * failure sooner (closer to the source) than if you have fewer detectors. This,
 * however, takes more logic. A good start to detection would be:
 * <ul>
 * <li> detect in the feedback loop (detect failures in state that may persist
 * before they make it to the outputs).
 * <li> detect at the very output
 * <li> If we put the comparators on the feedback first we are allowing quicker
 * detection of critical errors
 * <li> Another idea would be to evenly space the detectors for quicker MTTD
 * (Mean Time to Detection). Perhaps this could be controlled by a command line
 * parameter
 * </ul>
 * <p>
 * <h2>2. How to string detectors together</h2>
 * <p>
 * This should probably be figured out by a "higher" level algorithm. Need to
 * create some sort of tree node data structure that allows the specification of
 * the topology for the "combining" of the detectors. Each node in the tree
 * represents a detector. The top root node of the tree is the "last" detector
 * or detector output. Its children are also tree nodes and represent other
 * detectors that need to be combined at this level. Logic will be created at
 * this level to "or" the detector signals and create an output signal.
 * <p>
 * DetectorTreeNode
 * <ul>
 * <li> Array of EdifNet objects: Each EdifNet object corresponds to a net in
 * the original circuit that will be compared. This node must compare each of
 * these nets and provide a combined compare output.
 * <li> Array of DetectorTreeNode objects: indicates the previous detector tree
 * nodes that must be combined at this level.
 * </ul>
 * <p>
 * Initial approach:
 * <ul>
 * <li> Outputs (not in topological order). Use some form of log combining
 * approach.
 * <li>Feedback: Create topological order of each SCC (based on cut). Combine
 * topological order of each tree based on component graph.
 * </ul>
 * <p>
 * <h2>3. How to create detector output signal(s)</h2>
 * <p>
 * <h2>4. Use single rail signal or double rail?</h2>
 * <p>
 * <h2>5. Timing of detectors</h2>
 * <p>
 * <ul>
 * <li>Do you register these detectors?
 * <li>Are they one shot or are they latches that remember until a reset is
 * received?
 * <li>Tie up to global reset?
 * </ul>
 * <ol>
 * <li> Decide on where to put the voters (algorithms)
 * <li> Decide on the topology of the comparators
 * <li> Create single rail and double rail versions
 * <li> Algorithm for hooking them up
 * </ol>
 */
public class DWCEdifCell extends NMREdifCell {

    /**
     * Create a duplicated EdifCell in the given library with the given name
     * from the given original EdifCell. Specific EdifCellInstance and
     * EdifPortRef objects, along with top-level EdifPort objects, can be
     * explicitly specified to be duplicated.
     * <p>
     * edifPortRefsToCompare MUST be associated with "driver" ports. Further,
     * there should only be one port associated with each net.
     * 
     * @param lib The EdifLibrary in which to create the triplicated EdifCell
     * @param name The name of the resulting, triplicated EdifCell
     * @param cell The original, non-triplicated EdifCell
     * @param dwcArch
     * @param origPortsToDuplicate top-level EdifPort objects to be triplicated
     * @param edifCellInstancesToDuplicate A Collection of EdifCellInstance
     * objects to be triplicated
     * @param outputEdifPortRefsToCompare The EdifPortRef objects that should be
     * given comparators, from the outputs of the design
     * @param persistentEdifPortRefsToCompare The EdifPortRef objects that
     * should be given comparators, from persistent sections of the design.
     * @param useDRC Indicates to use a dual rail checker, default is a single
     * rail checker
     * @throws InvalidEdifNameException
     * @throws EdifNameConflictException
     */
    public DWCEdifCell(EdifLibrary lib, String name, EdifCell cell, NMRArchitecture dwcArch,
            Collection<EdifPort> origPortsToDuplicate, Collection<EdifCellInstance> edifCellInstancesToDuplicate,
            Collection<EdifPortRef> outputEdifPortRefsToCompare,
            Collection<EdifPortRef> persistentEdifPortRefsToCompare, boolean doPersistence, boolean useDRC)
            throws EdifNameConflictException, InvalidEdifNameException {
        super(lib, name, cell, dwcArch, _replicationFactor, _defaultReplicationSuffixes);

        //_arch = dwcArch;
        _dwcArch = (XilinxDWCArchitecture) dwcArch;
        _useDRC = useDRC;
        _doPersistence = doPersistence;
        //debug = true;
        _outputVoterOutputsRail0 = new ArrayList<EdifNet>();
        _outputVoterOutputsRail1 = new ArrayList<EdifNet>();
        _persistentVoterOutputsRail0 = new ArrayList<EdifNet>();
        _persistentVoterOutputsRail1 = new ArrayList<EdifNet>();
        // stops the voting for downscaling
        // TODO : there should be a better way to do this (i/e less of a hack)
        _useVoterForReplicatedNetToOneNetDownscale = false;
        if (persistentEdifPortRefsToCompare == null)
            persistentEdifPortRefsToCompare = new ArrayList<EdifPortRef>(0);
        replicateCell(origPortsToDuplicate, edifCellInstancesToDuplicate, outputEdifPortRefsToCompare,
                persistentEdifPortRefsToCompare);

    }

    /**
     * Create a duplicated EdifCell in the given library with the given name
     * from the given original EdifCell. Specific EdifCellInstance and
     * EdifPortRef objects, along with top-level EdifPort objects, can be
     * explicitly specified to be duplicated.
     * <p>
     * edifPortRefsToCompare MUST be associated with "driver" ports. Further,
     * there should only be one port associated with each net.
     * 
     * @param lib The EdifLibrary in which to create the triplicated EdifCell
     * @param name The name of the resulting, triplicated EdifCell
     * @param cell The original, non-triplicated EdifCell
     * @param dwcArch
     * @param origPortsToDuplicate top-level EdifPort objects to be triplicated
     * @param edifCellInstancesToDuplicate A Collection of EdifCellInstance
     * objects to be triplicated
     * @param outputEdifPortRefsToCompare The EdifPortRef objects that should be
     * given comparators, from the outputs of the design.
     * @param outputEdifPortRefsToCompare The EdifPortRef objects that should be
     * given comparators, from the persistent sections of the design.
     * @param useDRC Indicates to use a dual rail checker, default is a single
     * rail checker
     * @param replicationSuffixes An array of 2 String objects which should be
     * appended to each triplicated element of the DWCEdifCell Each element of
     * the array corresponds to one DWC domain.
     * @throws InvalidEdifNameException
     * @throws EdifNameConflictException
     */
    public DWCEdifCell(EdifLibrary lib, String name, EdifCell cell, NMRArchitecture dwcArch,
            Collection<EdifPort> origPortsToDuplicate, Collection<EdifCellInstance> edifCellInstancesToDuplicate,
            Collection<EdifPortRef> outputEdifPortRefsToCompare,
            Collection<EdifPortRef> persistentEdifPortRefsToCompare, boolean doPersistence, boolean useDRC,
            String[] replicationSuffixes) throws EdifNameConflictException, InvalidEdifNameException {
        super(lib, name, cell, dwcArch, _replicationFactor, replicationSuffixes);

        _dwcArch = (XilinxDWCArchitecture) dwcArch;
        _useDRC = useDRC;
        _doPersistence = doPersistence;
        //debug = true;
        _outputVoterOutputsRail0 = new ArrayList<EdifNet>();
        _outputVoterOutputsRail1 = new ArrayList<EdifNet>();
        _persistentVoterOutputsRail0 = new ArrayList<EdifNet>();
        _persistentVoterOutputsRail1 = new ArrayList<EdifNet>();
        // stops the voting for downscaling
        // TODO : there should be a better way to do this (i/e less of a hack)
        _useVoterForReplicatedNetToOneNetDownscale = false;
        if (persistentEdifPortRefsToCompare == null)
            persistentEdifPortRefsToCompare = new ArrayList<EdifPortRef>(0);
        replicateCell(origPortsToDuplicate, edifCellInstancesToDuplicate, outputEdifPortRefsToCompare,
                persistentEdifPortRefsToCompare);

    }

    public Collection<EdifCellInstance> getVoters() {
        return _voters;
    }

    public Collection<EdifCellInstance> getOutputVoters() {
        return _outputVoters;
    }

    public Collection<EdifCellInstance> getPersistentVoters() {
        return _persistentVoters;
    }

    /**
     * Overrides {@link NMREdifCell#createVoter(EdifNet[], EdifNet, int)}
     * <p>
     * Note: This method is here in order to extend NMREdifCell. If used, this
     * method will create a comparator linking to the output error line[s].
     * 
     * @param voterInputs An array containing the two EdifNet objects to be
     * compared.
     * @param origNet
     * @param numberOfVoters How many voters to create
     * @return An array of the EdifNet objects created as the output of the new
     * voters.
     */

    protected EdifNet[] createVoter(EdifNet[] voterInputs, EdifNet origNet, int numberOfVoters) {
        return createComparator(voterInputs, origNet, numberOfVoters, false, false);
    }

    /**
     * Creates comparators and determines which error line(s) the comparators
     * will ultimately link to.
     * 
     * @param voterInputs An array containing the two EdifNet objects to be
     * compared.
     * @param origNet
     * @param numberOfVoters How many voters to create
     * @param persistentVoter Tells if this comparator will link with the
     * persistent error line(s) or not
     * @param dualVoter Tells if this comparator will link with both persistent
     * and output error lines.
     * @return An array of the EdifNet objects created as the output of the new
     * voters.
     */

    protected EdifNet[] createComparator(EdifNet[] voterInputs, EdifNet origNet, int numberOfVoters,
            boolean persistentVoter, boolean dualVoter) {

        /*
         * Instead of inserting voters, "pass" the duplicated signals through
         * the circuit and use them as the voter "outputs".
         */

        EdifNet voterNets[] = new EdifNet[voterInputs.length];
        if (numberOfVoters != 0) { // insure the voters does not equal zero
            for (int i = 0; i < voterInputs.length; i++) {
                voterNets[i] = voterInputs[i];
                voterNets[i].copyProperties(origNet);
            }
        }

        if (numberOfVoters != 0) { // we only want one voter at each cut
            String voterName = origNet.getName();
            // We create the nets for the Votes and add them to _outputVoterOutputsRail0
            EdifNet voterOutputNetRail0 = null;
            try {
                if (dualVoter)
                    voterOutputNetRail0 = new EdifNet(origNet.getName() + "_VOTE_RAIL0_PERSISTENT_OUTPUT");
                else if (persistentVoter)
                    voterOutputNetRail0 = new EdifNet(origNet.getName() + "_VOTE_RAIL0_PERSISTENT");
                else
                    voterOutputNetRail0 = new EdifNet(origNet.getName() + "_VOTE_RAIL0_OUTPUT");
            } catch (InvalidEdifNameException e1) {
                e1.printStackTrace();
            }
            EdifCellInstance compareInstance0;
            if (dualVoter) {
                compareInstance0 = _dwcArch.createVoter(this, voterName + "_VOTER_RAIL0_PERSISTENT_OUTPUT",
                        voterInputs, voterOutputNetRail0);
                _dualVoters.add(compareInstance0);
                _persistentVoters.add(compareInstance0);
                _outputVoters.add(compareInstance0);
            } else if (persistentVoter) {
                compareInstance0 = _dwcArch.createVoter(this, voterName + "_VOTER_RAIL0_PERSISTENT", voterInputs,
                        voterOutputNetRail0);
                _persistentVoters.add(compareInstance0);
            } else {
                compareInstance0 = _dwcArch.createVoter(this, voterName + "_VOTER_RAIL0_OUTPUT", voterInputs,
                        voterOutputNetRail0);
                _outputVoters.add(compareInstance0);
            }
            _voters.add(compareInstance0);
            if (dualVoter) {
                _persistentVoterOutputsRail0.add(voterOutputNetRail0);
                _outputVoterOutputsRail0.add(voterOutputNetRail0);
            } else if (persistentVoter)
                _persistentVoterOutputsRail0.add(voterOutputNetRail0);
            else
                _outputVoterOutputsRail0.add(voterOutputNetRail0);
            try {
                this.addNet(voterOutputNetRail0); // the net to the parent EdifCell
            } catch (EdifNameConflictException e) {
                // We should never get here the name should be unique
                e.toRuntime();
            }

            if (_useDRC) { // with DRC create a second identical voter
                // We create the nets for the Votes and add them to _outputVoterOutputsRail1 for the second rail
                EdifNet voterOutputNetRail1 = null;
                try {
                    if (dualVoter)
                        voterOutputNetRail1 = new EdifNet(origNet.getName() + "_VOTE_RAIL1_PERSISTENT_OUTPUT");
                    else if (persistentVoter)
                        voterOutputNetRail1 = new EdifNet(origNet.getName() + "_VOTE_RAIL1_PERSISTENT");
                    else
                        voterOutputNetRail1 = new EdifNet(origNet.getName() + "_VOTE_RAIL1_OUTPUT");
                } catch (InvalidEdifNameException e1) {
                    e1.printStackTrace();
                }
                EdifCellInstance compareInstance1;
                if (dualVoter) {
                    compareInstance1 = _dwcArch.createVoter(this, voterName + "_VOTER_RAIL1_PERSISTENT_OUTPUT",
                            voterInputs, voterOutputNetRail1);
                    _dualVoters.add(compareInstance1);
                    _persistentVoters.add(compareInstance1);
                    _outputVoters.add(compareInstance1);
                } else if (persistentVoter) {
                    compareInstance1 = _dwcArch.createVoter(this, voterName + "_VOTER_RAIL1_PERSISTENT", voterInputs,
                            voterOutputNetRail1);
                    _persistentVoters.add(compareInstance1);
                } else {
                    compareInstance1 = _dwcArch.createVoter(this, voterName + "_VOTER_RAIL1_OUTPUT", voterInputs,
                            voterOutputNetRail1);
                    _outputVoters.add(compareInstance1);
                }
                _voters.add(compareInstance1);
                if (dualVoter) {
                    _persistentVoterOutputsRail1.add(voterOutputNetRail1);
                    _outputVoterOutputsRail1.add(voterOutputNetRail1);
                } else if (persistentVoter)
                    _persistentVoterOutputsRail1.add(voterOutputNetRail1);
                else
                    _outputVoterOutputsRail1.add(voterOutputNetRail1);
                try {
                    this.addNet(voterOutputNetRail1); // the net to the parent EdifCell
                } catch (EdifNameConflictException e) {
                    // We should never get here the name should be unique
                    e.toRuntime();
                }
            }
        }// end for

        if (numberOfVoters != 0) {
            CompareNets pair = new CompareNets(voterNets[0], voterNets[1]);
            _origNetPairMap.put(origNet, pair);
        }

        return voterNets;
    }

    @Override
    // TODO : it would seem like this would be the ideal time to tie all the 
    // different voter wires together
    protected void cleanUp() {
        // Clean up rail 0

        // 1. add top level port

        // a. create EdifNet
        try {
            _outputRail0Name = new NamedObject("BLDWC_ERROR_FLAG_OUTPUT_RAIL0");
        } catch (InvalidEdifNameException e) {
            e.toRuntime(); // Invalid name
        }
        _outputRail0Name = this.getUniqueNetNameable(_outputRail0Name);
        EdifNet rail0Net = new EdifNet(_outputRail0Name);
        try {
            this.addNet(rail0Net); // the net to the parent EdifCell
        } catch (EdifNameConflictException e) {
            // We should never get here the name should be unique
            e.toRuntime();
        }

        // b. create EdifPort
        EdifPort rail0_port = null;
        try {
            rail0_port = this.addPort(this.getInterface().getUniquePortNameable(_outputRail0Name), 1, EdifPort.OUT);
        } catch (EdifNameConflictException e) {
            // We should never get here the name should be unique
            e.toRuntime();
        }

        // c. create EdifPortRef
        rail0Net.addPortConnection(null, rail0_port);

        // 2. Merge all the nets to the input of the IOB
        /*
         * This merges the comparator nets in a sort of tree fashion All of the
         * primary comparator nets are merged first Next the secondary (merged)
         * nets are merged And so on until all nets merge down to one
         */
        EdifNet voterNetsRail0[] = new EdifNet[2];
        int j = 0;
        while (_outputVoterOutputsRail0.size() > 1) {
            voterNetsRail0[0] = _outputVoterOutputsRail0.remove(0);
            voterNetsRail0[1] = _outputVoterOutputsRail0.remove(0);
            EdifNameable mergerOutputName = null;
            try {
                mergerOutputName = new NamedObject("BLDWC_MERGER_OUT_" + j + "_OUTPUT_RAIL0");
            } catch (InvalidEdifNameException e) {
                e.toRuntime(); // Invalid name
            }
            _outputRail0Name = this.getUniqueNetNameable(mergerOutputName);
            EdifNet mergerOutput = new EdifNet(mergerOutputName);
            // Create the Merger
            _dwcArch.createMerger(this, "BLDWC_MERGER_" + j + "_OUTPUT_RAIL0", voterNetsRail0, mergerOutput);
            try {
                this.addNet(mergerOutput); // the net to the parent EdifCell
            } catch (EdifNameConflictException e) {
                // We should never get here the name should be unique
                e.toRuntime();
            }
            _outputVoterOutputsRail0.add(mergerOutput);
            j++;
        }

        // 3. create IOB for the net
        EdifNet iobInput0 = _outputVoterOutputsRail0.remove(0);
        _dwcArch.createOBUF(this, "BLDWC_ERROR_FLAG_OUTPUT_RAIL0", iobInput0, rail0Net);

        if (_useDRC) {
            // Clean up rail 1

            // 1. add top level port

            // a. create EdifNet
            try {
                _outputRail1Name = new NamedObject("BLDWC_ERROR_FLAG_OUTPUT_RAIL1");
            } catch (InvalidEdifNameException e) {
                e.toRuntime(); // Invalid name
            }
            _outputRail1Name = this.getUniqueNetNameable(_outputRail1Name);
            EdifNet rail1_net = new EdifNet(_outputRail1Name);
            try {
                this.addNet(rail1_net); // the net to the parent EdifCell
            } catch (EdifNameConflictException e) {
                // We should never get here the name should be unique
                e.toRuntime();
            }

            // b. create EdifPort
            EdifPort rail1_port = null;
            try {
                rail1_port = this.addPort(this.getInterface().getUniquePortNameable(_outputRail1Name), 1, EdifPort.OUT);
            } catch (EdifNameConflictException e) {
                // We should never get here the name should be unique
                e.toRuntime();
            }

            // c. create EdifPortRef
            rail1_net.addPortConnection(null, rail1_port);

            // 2. Merge all the nets to the input of the IOB
            EdifNet voterNetsRail1[] = new EdifNet[2];
            j = 1;
            while (_outputVoterOutputsRail1.size() > 1) {
                voterNetsRail1[0] = _outputVoterOutputsRail1.remove(0);
                voterNetsRail1[1] = _outputVoterOutputsRail1.remove(0);
                EdifNameable mergerOutputName = null;
                try {
                    mergerOutputName = new NamedObject("BLDWC_MERGER_OUT_" + j + "_OUTPUT_RAIL1");
                } catch (InvalidEdifNameException e) {
                    e.toRuntime(); // Invalid name
                }
                _outputRail1Name = this.getUniqueNetNameable(mergerOutputName);
                EdifNet mergerOutput = new EdifNet(mergerOutputName);
                // Create the Merger
                _dwcArch.createMerger(this, "BLDWC_MERGER_" + j + "_OUTPUT_RAIL1", voterNetsRail1, mergerOutput);
                try {
                    this.addNet(mergerOutput); // the net to the parent EdifCell
                } catch (EdifNameConflictException e) {
                    // We should never get here the name should be unique
                    e.toRuntime();
                }
                _outputVoterOutputsRail1.add(mergerOutput);
                j++;
            }

            // 3. create IOB for the net
            EdifNet iobInput = _outputVoterOutputsRail1.remove(0);
            _dwcArch.createOBUF(this, "BLDWC_ERROR_FLAG_OUTPUT_RAIL1", iobInput, rail1_net);

        }// end rail 1

        // do cleanup for the persistent error line(s), if created
        if (_doPersistence) {
            // Clean up rail 0

            // 1. add top level port

            // a. create EdifNet
            try {
                _persistentRail0Name = new NamedObject("BLDWC_ERROR_FLAG_PERSISTENT_RAIL0");
            } catch (InvalidEdifNameException e) {
                e.toRuntime(); // Invalid name
            }
            _persistentRail0Name = this.getUniqueNetNameable(_persistentRail0Name);
            rail0Net = new EdifNet(_persistentRail0Name);
            try {
                this.addNet(rail0Net); // the net to the parent EdifCell
            } catch (EdifNameConflictException e) {
                // We should never get here the name should be unique
                e.toRuntime();
            }

            // b. create EdifPort
            rail0_port = null;
            try {
                rail0_port = this.addPort(this.getInterface().getUniquePortNameable(_persistentRail0Name), 1,
                        EdifPort.OUT);
            } catch (EdifNameConflictException e) {
                // We should never get here the name should be unique
                e.toRuntime();
            }

            // c. create EdifPortRef
            rail0Net.addPortConnection(null, rail0_port);

            // 2. Merge all the nets to the input of the IOB
            /*
             * This merges the comparator nets in a sort of tree fashion All of
             * the primary comparator nets are merged first Next the secondary
             * (merged) nets are merged And so on until all nets merge down to
             * one
             */
            voterNetsRail0 = new EdifNet[2];
            j = 0;
            while (_persistentVoterOutputsRail0.size() > 1) {
                voterNetsRail0[0] = _persistentVoterOutputsRail0.remove(0);
                voterNetsRail0[1] = _persistentVoterOutputsRail0.remove(0);
                EdifNameable mergerOutputName = null;
                try {
                    mergerOutputName = new NamedObject("BLDWC_MERGER_OUT_" + j + "_PERSISTENT_RAIL0");
                } catch (InvalidEdifNameException e) {
                    e.toRuntime(); // Invalid name
                }
                _persistentRail0Name = this.getUniqueNetNameable(mergerOutputName);
                EdifNet mergerOutput = new EdifNet(mergerOutputName);
                // Create the Merger
                _dwcArch.createMerger(this, "BLDWC_MERGER_" + j + "_PERSISTENT_RAIL0", voterNetsRail0, mergerOutput);
                try {
                    this.addNet(mergerOutput); // the net to the parent EdifCell
                } catch (EdifNameConflictException e) {
                    // We should never get here the name should be unique
                    e.toRuntime();
                }
                _persistentVoterOutputsRail0.add(mergerOutput);
                j++;
            }

            // 3. create IOB for the net
            iobInput0 = _persistentVoterOutputsRail0.remove(0);
            _dwcArch.createOBUF(this, "BLDWC_ERROR_FLAG_PERSISTENT_RAIL0", iobInput0, rail0Net);

            if (_useDRC) {
                // Clean up rail 1

                // 1. add top level port

                // a. create EdifNet
                try {
                    _persistentRail1Name = new NamedObject("BLDWC_ERROR_FLAG_PERSISTENT_RAIL1");
                } catch (InvalidEdifNameException e) {
                    e.toRuntime(); // Invalid name
                }
                _persistentRail1Name = this.getUniqueNetNameable(_persistentRail1Name);
                EdifNet rail1_net = new EdifNet(_persistentRail1Name);
                try {
                    this.addNet(rail1_net); // the net to the parent EdifCell
                } catch (EdifNameConflictException e) {
                    // We should never get here the name should be unique
                    e.toRuntime();
                }

                // b. create EdifPort
                EdifPort rail1_port = null;
                try {
                    rail1_port = this.addPort(this.getInterface().getUniquePortNameable(_persistentRail1Name), 1,
                            EdifPort.OUT);
                } catch (EdifNameConflictException e) {
                    // We should never get here the name should be unique
                    e.toRuntime();
                }

                // c. create EdifPortRef
                rail1_net.addPortConnection(null, rail1_port);

                // 2. Merge all the nets to the input of the IOB
                EdifNet voterNetsRail1[] = new EdifNet[2];
                j = 1;
                while (_persistentVoterOutputsRail1.size() > 1) {
                    voterNetsRail1[0] = _persistentVoterOutputsRail1.remove(0);
                    voterNetsRail1[1] = _persistentVoterOutputsRail1.remove(0);
                    EdifNameable mergerOutputName = null;
                    try {
                        mergerOutputName = new NamedObject("BLDWC_MERGER_OUT_" + j + "_PERSISTENT_RAIL1");
                    } catch (InvalidEdifNameException e) {
                        e.toRuntime(); // Invalid name
                    }
                    _persistentRail1Name = this.getUniqueNetNameable(mergerOutputName);
                    EdifNet mergerOutput = new EdifNet(mergerOutputName);
                    // Create the Merger
                    _dwcArch
                            .createMerger(this, "BLDWC_MERGER_" + j + "_PERSISTENT_RAIL1", voterNetsRail1, mergerOutput);
                    try {
                        this.addNet(mergerOutput); // the net to the parent EdifCell
                    } catch (EdifNameConflictException e) {
                        // We should never get here the name should be unique
                        e.toRuntime();
                    }
                    _persistentVoterOutputsRail1.add(mergerOutput);
                    j++;
                }

                // 3. create IOB for the net
                EdifNet iobInput = _persistentVoterOutputsRail1.remove(0);
                _dwcArch.createOBUF(this, "BLDWC_ERROR_FLAG_PERSISTENT_RAIL1", iobInput, rail1_net);

            }// end rail 1

        } //end persistent

    }

    /*
     * TODO: The replicateCell, replicateNets, and replicateNet methods are
     * copied from NMREdifCell, changed slightly to be able to add voters on
     * both persistent and non-persistent error lines. Is there a way to do this
     * without duplicating so much code?
     */

    /**
     * Perform the replication process for the DWC cell.
     * 
     * @param origPortsToReplicate top-level EdifPort objects from the original
     * EdifCell object to be replicated. This Collection may be null if no port
     * replication is desired.
     * @param edifCellInstancesToReplicate The EdifCellInstance objects to be
     * replicated from the original EdifCell object. This Collection may be null
     * if no instance replication is desired.
     * @param outputEdifPortRefsToCut The EdifPortRef objects from the original
     * design that specify the location of the voter/detection circuits for
     * output errors. This Collection may be null if no application-specific
     * voter/detection circuits are desired.
     * @param persistentEdifPortRefsToCut The EdifPortRef objects from the
     * original design that specify the location of the voter/detection circuits
     * for persistent errors. This Collection may be null if no
     * application-specific voter/detection circuits are desired for persistent
     * errors.
     */
    protected void replicateCell(Collection<EdifPort> origPortsToReplicate,
            Collection<EdifCellInstance> edifCellInstancesToReplicate, Collection<EdifPortRef> outputEdifPortRefsToCut,
            Collection<EdifPortRef> persistentEdifPortRefsToCut) {

        // empty the design.

        // 1. Add ports
        addTopLevelPorts(origPortsToReplicate, null);

        // 2. Replicate cells
        replicateCellInstances(edifCellInstancesToReplicate);

        // 3. replicate nets
        replicateNets(outputEdifPortRefsToCut, persistentEdifPortRefsToCut);

        // 4. Clean up
        cleanUp();
    }

    /**
     * Replicate the nets of the circuit. Replication of nets is determined by
     * the replication of the instances.
     * 
     * @param edifPortRefsToCut A Collection of EdifPortRef objects which are to
     * be "cut." That is, a voter, or other restoring organ, will be placed
     * between the EdifNet and the EdifPortRef.
     * @see #replicateNet(EdifNet,Collection)
     */
    protected void replicateNets(Collection<EdifPortRef> outputEdifPortRefsToCut,
            Collection<EdifPortRef> persistentEdifPortRefsToCut) {

        int mapSize = _origCell.getNetList().size();
        _edifNetMap = new LinkedHashMap<EdifNet, EdifNet[]>(mapSize);

        /*
         * Iterate over every net in the cell. For each net, create either a
         * single net or a replicated version of the net.
         */
        for (Iterator netIterator = _origCell.netListIterator(); netIterator.hasNext();) {

            EdifNet net = (EdifNet) netIterator.next();
            if (debug) {
                System.out.println("----------");
                System.out.println("Net " + net.getName() + " (" + net.getOldName() + ")");
            }

            replicateNet(net, outputEdifPortRefsToCut, persistentEdifPortRefsToCut);

        }
    }

    /**
     * Replicate the given EdifNet object. Replication occurs in the following
     * manner:
     * <ol>
     * <li>Determine which nets to replicate by identifying all the drivers of
     * each net and determining if the drivers have been replicated. If the
     * drivers have been replicated, that net should be replicated.
     * <li>A set of new nets is created based on the net replication number
     * identified in the previous step. The drivers of these nets are attached.
     * <li>The sinks are attached to the replicated nets.
     * </ol>
     * 
     * @param net The EdifNet object to be replicated
     * @param outputPortRefsToCut A Collection of EdifPortRef objects to be cut
     * @return An array of the EdifNet objects created by replicating this
     * EdifNet.
     */
    protected EdifNet[] replicateNet(EdifNet net, Collection<EdifPortRef> outputPortRefsToCut,
            Collection<EdifPortRef> persistentPortRefsToCut) {
        //debug = true;
        // ////////////////////////////////////////////////////////////////////
        //
        // Step 1
        //
        // Determine how many nets to create and make sure that all drivers
        // have the same replication status.
        //
        // ////////////////////////////////////////////////////////////////////
        // net.getNetDrivers();

        /*
         * The drivers of the non-replicated EdifNet, including EdifPortRef
         * objects connected to tri-state ports. A driver is either a
         * CellInstance whose output is connected to this Net, or a top-level
         * input port connected to this Net.
         * 
         * The net drivers are the regular drivers (including top-level ports -
         * first true argument) plus any EPRs connected to tri-state ports
         * (second true argument).
         */
        Collection<EdifPortRef> originalNetDrivers = net.getSourcePortRefs(true, true);

        /*
         * The sinks of the non-replicated EdifNet. The net sinks do not include
         * EdifPortRef objects connected to tri-state ports. A sink is either a
         * CellInstance whose input is connected to this Net, or a top-level
         * output port connected to this Net.
         */
        Collection<EdifPortRef> originalNetSinks = net.getSinkPortRefs(false, true);

        /*
         * The number of Nets to be created. This will be either 1 or equal to
         * _replicationFactor.
         */
        int numberOfNetsToCreate = determineNumberOfNets(originalNetDrivers);

        // determines whether voters created will connect to the persistent or
        // non-persistent error lines
        boolean persistentVoter = false;
        boolean dualVoter = false;

        // ////////////////////////////////////////////////////////////////////
        //
        // Step 2
        //
        // Create the new Net(s) and connect the new Net(s) to
        // all "drivers" of the net. Add a Map between the original
        // EdifNet object and the resulting EdifNet[] array.
        //
        // ////////////////////////////////////////////////////////////////////
        EdifNet newNets[] = createNewNetsWithDrivers(net, originalNetDrivers, numberOfNetsToCreate);
        _edifNetMap.put(net, newNets);

        // ////////////////////////////////////////////////////////////////////
        //
        // Step 3
        //
        // Create the voters associated with the given net.
        //
        // A voter is created for the net under the following conditions:
        //
        // 1. The net is replicated (i.e. there is more than one new net (i.e.
        //    multiple drivers)), and
        // 2. a. The driver is cut, or
        //    b. There is a sink that is cut, or
        //       i. Sink is not replicated: use 1 voter
        //       ii. Sink is replicated: use N voters
        //    c. None of the sinks are cut and not all the sinks are replicated
        // ////////////////////////////////////////////////////////////////////

        // Determine if the driver is cut
        // The driver's cut status determines which error lines the comparator
        // will feed to. If it belongs to both cut groups, it will feed to both
        // sets of error lines.
        boolean driverIsCut = false;
        for (EdifPortRef driver : originalNetDrivers) {
            if (outputPortRefsToCut.contains(driver) || persistentPortRefsToCut.contains(driver)) {
                driverIsCut = true;
                if (persistentPortRefsToCut.contains(driver)) {
                    persistentVoter = true;
                    if (outputPortRefsToCut.contains(driver))
                        dualVoter = true;
                }
                if (debug)
                    System.out.println("At least one driver of net " + net.getName() + " is cut");
            }
        }

        // Determine state of net sinks

        boolean anySinksReplicated = false;
        boolean allSinksReplicated = true;
        boolean aReplicatedSinkIsCut = false;
        boolean aSingleSinkIsCut = false;
        for (EdifPortRef sink : originalNetSinks) {
            if (isPortRefReplicated(sink)) {
                // replicated portrefs
                anySinksReplicated = true;
                if (outputPortRefsToCut.contains(sink) || persistentPortRefsToCut.contains(sink)) {
                    aReplicatedSinkIsCut = true;
                    if (persistentPortRefsToCut.contains(sink)) {
                        persistentVoter = true;
                        if (outputPortRefsToCut.contains(sink))
                            dualVoter = true;
                    }
                }
            } else {
                // non replicated port refs
                allSinksReplicated = false;
                if (outputPortRefsToCut.contains(sink) || persistentPortRefsToCut.contains(sink)) {
                    aSingleSinkIsCut = true;
                    if (persistentPortRefsToCut.contains(sink)) {
                        persistentVoter = true;
                        if (outputPortRefsToCut.contains(sink))
                            dualVoter = true;
                    }
                }
            }
        }

        /*
         * Determine how many voters to create. (See rules above.)
         * 
         * TODO: Give a warning if the user provides an EdifPortRef on an driver
         * port that is not replicated (i.e. warning: no voter placed blah blah
         * blah).
         */
        int numberOfVoters = 0;
        if (numberOfNetsToCreate == _replicationFactor) {
            if (driverIsCut) {
                if (anySinksReplicated)
                    numberOfVoters = _replicationFactor;
                else
                    numberOfVoters = 1;
            } else if (aReplicatedSinkIsCut)
                numberOfVoters = _replicationFactor;
            else if (aSingleSinkIsCut)
                numberOfVoters = 1;
            else if (!allSinksReplicated && _useVoterForReplicatedNetToOneNetDownscale)
                numberOfVoters = 1;
            else
                numberOfVoters = 0; // just to make sure
        }

        if (debug)
            System.out.println("Creating " + numberOfVoters + " voters");
        EdifNet voterNets[] = createComparator(newNets, net, numberOfVoters, persistentVoter, dualVoter);

        // ////////////////////////////////////////////////////////////////////
        //
        // Hook up the "sinks" to the new net(s)
        //
        // ////////////////////////////////////////////////////////////////////
        for (EdifPortRef sinkPortRef : originalNetSinks) {

            boolean sinkReplicated = isPortRefReplicated(sinkPortRef);
            boolean sinkCut = outputPortRefsToCut.contains(sinkPortRef)
                    || persistentPortRefsToCut.contains(sinkPortRef);

            // Decide whether to connect the sink to voter outputs or
            // new net outputs.
            if (numberOfVoters > 0 // there are voters on these nets
                    && (sinkCut // sink needs voters
                            || (sinkReplicated && (numberOfVoters == _replicationFactor) && _useVoterOutputsForNonCutInputs) || (!sinkReplicated && _useVoterForReplicatedNetToOneNetDownscale)))
                connectSinkPortsToSourceNets(sinkPortRef, voterNets);
            else
                connectSinkPortsToSourceNets(sinkPortRef, newNets);

        }

        // debug to check to see which of the newly created nets got hooked up
        if (debug) {
            for (int i = 0; i < newNets.length; i++) {
                System.out.println("newNet " + i + " has the following eprs " + newNets[i].getConnectedPortRefs());
            }
        }
        return newNets;
    }

    protected HashMap<EdifNet, CompareNets> _origNetPairMap = new LinkedHashMap<EdifNet, CompareNets>();

    /**
     * Passed to parent (NMREdifCell) to indicate duplication (not triplication
     * nor quintuplication).
     */
    private static final int _replicationFactor = 2;

    /**
     * Passed to parent (NMREdifCell) as the strings to append to the end of
     * resource names when duplicated.
     */
    //private static final String _replicationSuffix = "DWC";
    private static final String[] _defaultReplicationSuffixes = { "_DWC_0", "_DWC_1" };

    protected Collection<EdifCellInstance> _voters = new ArrayList<EdifCellInstance>();

    protected Collection<EdifCellInstance> _outputVoters = new ArrayList<EdifCellInstance>();

    protected Collection<EdifCellInstance> _persistentVoters = new ArrayList<EdifCellInstance>();

    protected Collection<EdifCellInstance> _dualVoters = new ArrayList<EdifCellInstance>();

    protected XilinxDWCArchitecture _dwcArch;

    protected boolean _useDRC;

    protected boolean _doPersistence;

    // Contain voter nets
    private ArrayList<EdifNet> _outputVoterOutputsRail0;

    private ArrayList<EdifNet> _outputVoterOutputsRail1;

    private ArrayList<EdifNet> _persistentVoterOutputsRail0;

    private ArrayList<EdifNet> _persistentVoterOutputsRail1;

    private EdifNameable _outputRail0Name;

    private EdifNameable _outputRail1Name;

    private EdifNameable _persistentRail0Name;

    private EdifNameable _persistentRail1Name;
}

class CompareNets {

    public CompareNets(EdifNet net0, EdifNet net1) {
        _net0 = net0;
        _net1 = net1;
    }

    EdifNet _net0;

    EdifNet _net1;
}
