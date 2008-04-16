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
package edu.byu.ece.edif.tools.replicate.ijmr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.tools.flatten.NewFlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.XilinxDWCArchitecture;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.iob.AbstractIOB;
import edu.byu.ece.edif.util.iob.AbstractIOBAnalyzer;
import edu.byu.ece.edif.util.iob.XilinxVirtexIOBAnalyzer;

public class TMRDWCEdifCell extends MMREdifCell {

    public static Map<Integer, List<String>> DEFAULT_SUFFIXES;
    static {
        DEFAULT_SUFFIXES = new LinkedHashMap<Integer, List<String>>();
        List<String> tmrSuffixes = new ArrayList<String>();
        tmrSuffixes.add("_TMR_0");
        tmrSuffixes.add("_TMR_1");
        tmrSuffixes.add("_TMR_2");

        List<String> dwcSuffixes = new ArrayList<String>();
        dwcSuffixes.add("_DWC_0");
        dwcSuffixes.add("_DWC_1");

        DEFAULT_SUFFIXES.put(new Integer(3), tmrSuffixes);
        DEFAULT_SUFFIXES.put(new Integer(2), dwcSuffixes);
    }

    public static String VOTER_SUFFIX = "_VOTER";

    public static String COMPARE_SUFFIX = "_COMPARE";

    public static String MERGER_NAME = "DWC_MERGER";

    public static String PERSISTENT_OUTPUT = "DWC_PERSISTENT";

    public static String NON_PERSISTENT_OUTPUT = "DWC_NON_PERSISTENT";

    // Array index constants
    public static final int PERSISTENT_0 = 0;

    public static final int PERSISTENT_1 = 1;

    public static final int NON_PERSISTENT_0 = 2;

    public static final int NON_PERSISTENT_1 = 3;

    public TMRDWCEdifCell(EdifLibrary lib, String name, EdifCell origCell,
            Collection<EdifCellInstance> feedbackPlusInput, NMRArchitecture tmrArchitecture,
            NMRArchitecture dwcArchitecture, Map<Integer, List<EdifPort>> portsToReplicate,
            Map<Integer, List<EdifCellInstance>> instancesToReplicate, Collection<EdifPortRef> cutSet,
            boolean useDualRail, boolean packOutputRegs, boolean registerDetection, EdifNet clockNet,
            Map<Integer, List<String>> replicationSuffixes) throws EdifNameConflictException, InvalidEdifNameException {
        super(lib, name, origCell, replicationSuffixes);
        _tmrArchitecture = tmrArchitecture;
        _dwcArchitecture = (XilinxDWCArchitecture) dwcArchitecture;
        _cutSet = cutSet;
        _persistentErrorNets0 = new ArrayList<EdifNet>();
        _persistentErrorNets1 = new ArrayList<EdifNet>();
        _nonPersistentErrorNets0 = new ArrayList<EdifNet>();
        _nonPersistentErrorNets1 = new ArrayList<EdifNet>();

        _feedbackPlusInput = feedbackPlusInput;

        _useDualRail = useDualRail;
        _packOutputRegs = packOutputRegs;
        _registerDetection = registerDetection;
        _clockNet = clockNet;

        _voters = new ArrayList<EdifCellInstance>();
        _comparators = new ArrayList<EdifCellInstance>();
        replicateCell(portsToReplicate, instancesToReplicate);
    }

    public TMRDWCEdifCell(EdifLibrary lib, String name, EdifCell origCell,
            Collection<EdifCellInstance> feedbackPlusInput, NMRArchitecture tmrArchitecture,
            NMRArchitecture dwcArchitecture, Map<Integer, List<EdifPort>> portsToReplicate,
            Map<Integer, List<EdifCellInstance>> instancesToReplicate, Collection<EdifPortRef> cutSet,
            boolean useDualRail, boolean packOutputRegs, boolean registerDetection, EdifNet clockNet)
            throws EdifNameConflictException, InvalidEdifNameException {
        this(lib, name, origCell, feedbackPlusInput, tmrArchitecture, dwcArchitecture, portsToReplicate,
                instancesToReplicate, cutSet, useDualRail, packOutputRegs, registerDetection, clockNet,
                DEFAULT_SUFFIXES);
    }

    /**
     * Merge comparator error signals
     */
    @Override
    protected void cleanUp() {
        // Add output comparators
        EdifCellInstanceGraph ecic = new EdifCellInstanceGraph(_origCell);
        AbstractIOBAnalyzer iob = new XilinxVirtexIOBAnalyzer((NewFlattenedEdifCell) _origCell, ecic);
        List<EdifNet> netsToCompare = new ArrayList<EdifNet>();
        for (EdifPort port : _portReplicationMap.keySet()) {
            if (port.getDirection() == EdifPort.OUT || port.getDirection() == EdifPort.INOUT) {
                for (EdifSingleBitPort esbp : port.getSingleBitPortList()) {
                    AbstractIOB xiob = XilinxVirtexIOBAnalyzer.createXilinxIOBFromPort(esbp, ecic);
                    Object bufObject = xiob.getOBUF();
                    Object regObject = xiob.getOutputReg();
                    if (bufObject != null && (bufObject instanceof EdifCellInstance)) {
                        EdifCellInstance buf = (EdifCellInstance) bufObject;
                        if (regObject != null && (regObject instanceof EdifCellInstance)) {
                            EdifCellInstance reg = (EdifCellInstance) regObject;
                            if (_packOutputRegs) {
                                Collection<EdifCellInstanceEdge> edges = ecic.getInputEdges(reg, "D");
                                if (edges.size() == 1) {
                                    netsToCompare.add(edges.iterator().next().getNet());
                                }
                            } else {
                                netsToCompare.add(((EdifCellInstanceEdge) ecic.getEdge(reg, buf)).getNet());
                            }

                        } else {
                            Collection<EdifCellInstanceEdge> edges = ecic.getInputEdges(buf, "I");
                            if (edges.size() == 1) {
                                netsToCompare.add(edges.iterator().next().getNet());
                            }
                        }
                    }
                }
            }
        }

        // now add the comparators in the determined locations
        for (EdifNet netToCompare : netsToCompare) {
            List<EdifNet> nets = _netReplicationMap.get(netToCompare);
            if (nets.size() == 2)
                createComparator(nets, netToCompare, false);
        }

        // Merge error signal nets and add outputs for applicable error signals
        EdifNameable[] outName = new EdifNameable[4];
        EdifNameable[] portName = new EdifNameable[4];
        EdifNameable[] netName = new EdifNameable[4];
        EdifNameable[] ofdNetName = new EdifNameable[4];
        EdifNet[] ofdNet = new EdifNet[4];
        EdifNet[] mergedErrorNet = new EdifNet[4];
        EdifNet[] outNet = new EdifNet[4];
        String[] ofdName = new String[4];
        String[] obufName = new String[4];

        mergeAndNameErrorSignals(outName, portName, netName, ofdNetName, ofdNet, mergedErrorNet, outNet, ofdName,
                obufName);

        // Add new nets, ports, obufs, and detection registers (if desired)
        for (int i = 0; i < mergedErrorNet.length; i++) {
            if (mergedErrorNet[i] != null) {
                try {
                    addNet(outNet[i]);
                } catch (EdifNameConflictException e1) {
                    // can't get here
                    e1.toRuntime();
                }

                EdifPort portToAdd = addPortUniqueName(portName[i], 1, EdifPort.OUT);
                EdifPortRef portRefToAdd = new EdifPortRef(outNet[i], portToAdd.getSingleBitPort(0), null);
                outNet[i].addPortConnection(portRefToAdd);

                if (_registerDetection) {
                    try {
                        addNet(ofdNet[i]);
                    } catch (EdifNameConflictException e) {
                        // can't get here
                        e.toRuntime();
                    }
                    createOFD(this, ofdName[i], mergedErrorNet[i], ofdNet[i]);
                    _dwcArchitecture.createOBUF(this, obufName[i], ofdNet[i], outNet[i]);
                } else {
                    _dwcArchitecture.createOBUF(this, obufName[i], mergedErrorNet[i], outNet[i]);
                }
            }
        }
    }

    protected void mergeAndNameErrorSignals(EdifNameable[] outName, EdifNameable[] portName, EdifNameable[] netName,
            EdifNameable[] ofdNetName, EdifNet[] ofdNet, EdifNet[] mergedErrorNet, EdifNet[] outNet, String[] ofdName,
            String[] obufName) {

        if (!_persistentErrorNets0.isEmpty()) {
            mergedErrorNet[PERSISTENT_0] = mergeNets(_persistentErrorNets0);
            outName[PERSISTENT_0] = NamedObject.createValidEdifNameable(PERSISTENT_OUTPUT + "_0");
            portName[PERSISTENT_0] = getInterface().getUniquePortNameable(outName[PERSISTENT_0]);
            netName[PERSISTENT_0] = getUniqueNetNameable(outName[PERSISTENT_0]);
            outNet[PERSISTENT_0] = new EdifNet(netName[PERSISTENT_0], this);
            if (_registerDetection) {
                ofdNetName[PERSISTENT_0] = getUniqueNetNameable(NamedObject.createValidEdifNameable(PERSISTENT_OUTPUT
                        + "_OFD_0"));
                ofdNet[PERSISTENT_0] = new EdifNet(ofdNetName[PERSISTENT_0], this);
                ofdName[PERSISTENT_0] = PERSISTENT_OUTPUT + "_0_OFD";
                obufName[PERSISTENT_0] = PERSISTENT_OUTPUT + "_0_OBUF";
            }
        } else
            mergedErrorNet[PERSISTENT_0] = null;

        if (!_persistentErrorNets1.isEmpty()) {
            mergedErrorNet[PERSISTENT_1] = mergeNets(_persistentErrorNets1);
            outName[PERSISTENT_1] = NamedObject.createValidEdifNameable(PERSISTENT_OUTPUT + "_1");
            portName[PERSISTENT_1] = getInterface().getUniquePortNameable(outName[PERSISTENT_1]);
            netName[PERSISTENT_1] = getUniqueNetNameable(outName[PERSISTENT_1]);
            outNet[PERSISTENT_1] = new EdifNet(netName[PERSISTENT_1], this);
            if (_registerDetection) {
                ofdNetName[PERSISTENT_1] = getUniqueNetNameable(NamedObject.createValidEdifNameable(PERSISTENT_OUTPUT
                        + "_OFD_1"));
                ofdNet[PERSISTENT_1] = new EdifNet(ofdNetName[PERSISTENT_1], this);
                ofdName[PERSISTENT_1] = PERSISTENT_OUTPUT + "_1_OFD";
                obufName[PERSISTENT_1] = PERSISTENT_OUTPUT + "_1_OBUF";
            }
        } else
            mergedErrorNet[PERSISTENT_1] = null;

        if (!_nonPersistentErrorNets0.isEmpty()) {
            mergedErrorNet[NON_PERSISTENT_0] = mergeNets(_nonPersistentErrorNets0);
            outName[NON_PERSISTENT_0] = NamedObject.createValidEdifNameable(NON_PERSISTENT_OUTPUT + "_0");
            portName[NON_PERSISTENT_0] = getInterface().getUniquePortNameable(outName[NON_PERSISTENT_0]);
            netName[NON_PERSISTENT_0] = getUniqueNetNameable(outName[NON_PERSISTENT_0]);
            outNet[NON_PERSISTENT_0] = new EdifNet(netName[NON_PERSISTENT_0], this);
            if (_registerDetection) {
                ofdNetName[NON_PERSISTENT_0] = getUniqueNetNameable(NamedObject
                        .createValidEdifNameable(NON_PERSISTENT_OUTPUT + "_OFD_0"));
                ofdNet[NON_PERSISTENT_0] = new EdifNet(ofdNetName[NON_PERSISTENT_0], this);
                ofdName[NON_PERSISTENT_0] = NON_PERSISTENT_OUTPUT + "_0_OFD";
                obufName[NON_PERSISTENT_0] = NON_PERSISTENT_OUTPUT + "_0_OBUF";
            }
        } else
            mergedErrorNet[NON_PERSISTENT_0] = null;

        if (!_nonPersistentErrorNets1.isEmpty()) {
            mergedErrorNet[NON_PERSISTENT_1] = mergeNets(_nonPersistentErrorNets1);
            outName[NON_PERSISTENT_1] = NamedObject.createValidEdifNameable(NON_PERSISTENT_OUTPUT + "_1");
            portName[NON_PERSISTENT_1] = getInterface().getUniquePortNameable(outName[NON_PERSISTENT_1]);
            netName[NON_PERSISTENT_1] = getUniqueNetNameable(outName[NON_PERSISTENT_1]);
            outNet[NON_PERSISTENT_1] = new EdifNet(netName[NON_PERSISTENT_1], this);
            if (_registerDetection) {
                ofdNetName[NON_PERSISTENT_1] = getUniqueNetNameable(NamedObject
                        .createValidEdifNameable(NON_PERSISTENT_OUTPUT + "_OFD_1"));
                ofdNet[NON_PERSISTENT_1] = new EdifNet(ofdNetName[NON_PERSISTENT_1], this);
                ofdName[NON_PERSISTENT_1] = NON_PERSISTENT_OUTPUT + "_1_OFD";
                obufName[NON_PERSISTENT_1] = NON_PERSISTENT_OUTPUT + "_1_OBUF";
            }
        } else
            mergedErrorNet[NON_PERSISTENT_1] = null;
    }

    protected EdifCellInstance createOFD(EdifCell parent, String name, EdifNet input, EdifNet output) {

        // 1. Obtain a reference to the EdifCell voter object
        EdifCell ofd = getOFDCell(parent);

        // 2. Create a new instance
        EdifCellInstance ofdCellInstance = null;
        try {
            ofdCellInstance = new EdifCellInstance(name, parent, ofd);
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }

        try {
            parent.addSubCell(ofdCellInstance);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }

        // Hook up input to I port
        EdifNet inputNet = input;
        EdifPortRef epr = inputNet.addPortConnection(ofdCellInstance, "D");
        // Hook up outputs to O port
        output.addPortConnection(ofdCellInstance, "Q");

        // Hook up clock net to C port
        System.out.println("_clockNet = " + _clockNet);
        System.out.println("_netReplicationMap.get(_clockNet) = " + _netReplicationMap.get(_clockNet));
        EdifNet newClock = _netReplicationMap.get(_clockNet).iterator().next();
        newClock.addPortConnection(ofdCellInstance, "C");

        return ofdCellInstance;

    }

    protected EdifCell getOFDCell(EdifCell parent) {

        /*
         * If the obuf cell has already been defined, return the associated Edif
         * Cell.
         */
        if (_ofdCell != null)
            return _ofdCell;

        String ofdCellName = "FD";

        // Step #1 - Get Xilinx primitive
        EdifLibrary xilinxLibrary = XilinxLibrary.library;

        EdifCell ofd = xilinxLibrary.getCell(ofdCellName);

        // Step #2 - Search for the Xilinx primitive in library manager
        Collection<EdifCell> matchingOFDCells = parent.getLibrary().getLibraryManager().getCells(ofdCellName);

        // Iterate over all cells to see if the primitive exists
        if (matchingOFDCells != null) {
            // Iterate over the matching cells and see if it exists
            for (EdifCell cell : matchingOFDCells) {
                if (cell.equalsInterface(ofd)) {
                    // The AND exists - tag it as the OFD cell
                    _ofdCell = cell;
                    return _ofdCell;
                }
            }
        }

        // Step #3 - The primitive does not exist in our library. Add it.
        EdifLibrary lib = parent.getLibrary().getLibraryManager().getFirstPrimitiveLibrary();
        if (lib == null)
            lib = parent.getLibrary();
        //System.out.println("Destination library "+lib.getName()); // debug

        try {
            lib.addCell(ofd);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
        _ofdCell = ofd;

        return _ofdCell;
    }

    @Override
    protected void connectNetSinks(EdifNet origNet, List<EdifNet> newNets, Collection<EdifPortRef> origDrivers,
            Collection<EdifPortRef> origSinks) {

        // Whether to create voter(s), comparator(s), or just connect the nets
        // directly depends on the replication factor of the driver and the sinks
        // and also depends on whether any drivers or sinks are cut.
        // 
        // Non-replicated driver (single new net)
        //   -Connect the single new net to every replication of every sink
        //   
        // Duplicated driver (two new nets)
        //   -any driver or any sink is cut
        //     -add comparator
        //     -for each sink
        //       -single sink
        //         -connect first domain to single sink
        //       -duplicated sink
        //         -connect each domain to respective sink
        //       -triplicated sink
        //         -connect first domain to each sink
        //   -no cuts
        //     -if there is any single or triplicated sink, add comparator
        //     -for each sink
        //       -single sink
        //         -connect first domain to single sink
        //       -duplicated sink
        //         -connect each domain to respective sink
        //       -triplicated sink
        //         -connect first domain to each sink
        //   
        // Triplicated driver (three new nets)
        //   -any driver or any sink is cut
        //     -if there is a triplicated sink, add three voters
        //     -else if there is a duplicated sink, add two voters
        //     -else add one voter
        //     -for each sink
        //       -single sink
        //         -use first (possible only) voter to downscale to single sink
        //       -duplicated sink
        //         -use first two (possibly only two) voters to downscale to both sinks
        //       -triplicated sink
        //         -use each voter for each respective sink domain
        //   -no cuts
        //     -if there is a duplicated sink, add two voters
        //     -else if there is a single sink, add one voter
        //     -else don't add any voters
        //     -for each sink
        //       -single sink
        //         -use first (possibly only) voter to downscale to single sink
        //       -duplicated sink
        //         -use both voters to downscale to both sinks
        //       -triplicated sink
        //         -connect each new net to respective sink

        // gather driver/sink information
        int driverFactor = newNets.size();
        boolean driverOrSinkCut = false;
        boolean hasTripledSink = false;
        boolean hasDoubledSink = false;
        boolean hasSingleSink = false;
        boolean anySinkInFeedbackPlusInput = false;

        for (EdifPortRef origDriver : origDrivers) {
            if (isCut(origDriver))
                driverOrSinkCut = true;
        }
        for (EdifPortRef origSink : origSinks) {
            if (isCut(origSink))
                driverOrSinkCut = true;
            int sinkFactor = replicationFactor(origSink);
            if (sinkFactor == 1)
                hasSingleSink = true;
            else if (sinkFactor == 2)
                hasDoubledSink = true;
            else if (sinkFactor == 3)
                hasTripledSink = true;
            EdifCellInstance sinkInstance = origSink.getCellInstance();
            if (sinkInstance != null && _feedbackPlusInput.contains(sinkInstance))
                anySinkInFeedbackPlusInput = true;
        }

        // implement above rules
        if (driverFactor == 1) {
            for (EdifPortRef origSink : origSinks) {
                connectNetsToSinks(origSink, newNets);
            }
        } else if (driverFactor == 2) {
            if (driverOrSinkCut) {
                createComparator(newNets, origNet, true); // persistent error output
            } else {
                if (hasSingleSink || hasTripledSink)
                    createComparator(newNets, origNet, anySinkInFeedbackPlusInput); // mark as persistent if related to feedback
            }
            for (EdifPortRef origSink : origSinks) {
                List<EdifNet> netsToConnect = null;
                if (replicationFactor(origSink) == 2) {
                    netsToConnect = newNets;
                } else {
                    netsToConnect = new ArrayList<EdifNet>(1);
                    netsToConnect.add(newNets.get(0));
                }
                connectNetsToSinks(origSink, netsToConnect);
            }
        } else if (driverFactor == 3) {
            if (driverOrSinkCut) {
                int numberOfVoters = 0;
                if (hasTripledSink)
                    numberOfVoters = 3;
                else if (hasDoubledSink)
                    numberOfVoters = 2;
                else
                    numberOfVoters = 1;
                List<EdifNet> voterOutputs = createVoters(newNets, origNet, numberOfVoters);
                for (EdifPortRef origSink : origSinks) {
                    List<EdifNet> netsToConnect = null;
                    if (replicationFactor(origSink) == 1) {
                        netsToConnect = new ArrayList<EdifNet>(1);
                        netsToConnect.add(voterOutputs.get(0));
                    } else if (replicationFactor(origSink) == 2) {
                        netsToConnect = new ArrayList<EdifNet>(2);
                        netsToConnect.add(voterOutputs.get(0));
                        netsToConnect.add(voterOutputs.get(1));
                    } else if (replicationFactor(origSink) == 3) {
                        netsToConnect = voterOutputs;
                    }
                    connectNetsToSinks(origSink, netsToConnect);
                }
            } else {
                int numberOfVoters = 0;
                if (hasDoubledSink)
                    numberOfVoters = 2;
                else if (hasSingleSink)
                    numberOfVoters = 1;
                List<EdifNet> voterOutputs = null;
                if (numberOfVoters != 0)
                    voterOutputs = createVoters(newNets, origNet, numberOfVoters);
                for (EdifPortRef origSink : origSinks) {
                    List<EdifNet> netsToConnect = null;
                    if (replicationFactor(origSink) == 1) {
                        netsToConnect = new ArrayList<EdifNet>(1);
                        netsToConnect.add(voterOutputs.get(0));
                    } else if (replicationFactor(origSink) == 2) {
                        netsToConnect = new ArrayList<EdifNet>(2);
                        netsToConnect.add(voterOutputs.get(0));
                        netsToConnect.add(voterOutputs.get(1));
                    } else if (replicationFactor(origSink) == 3) {
                        netsToConnect = newNets;
                    }
                    connectNetsToSinks(origSink, netsToConnect);
                }
            }
        } else
            throw new EdifRuntimeException("unexpected net replication factor: " + driverFactor);
    }

    /**
     * Connect the given nets to the sinks that correspond to the given original
     * sink
     * 
     * @param origSink original sink
     * @param newNets nets to connect
     */
    protected void connectNetsToSinks(EdifPortRef origSink, List<EdifNet> newNets) {
        // cases:
        // single net - connect to each sink
        //   - this happens when the source is not replicated or is duplicated
        //     (only one domain of the duplication can feed forward) or when there
        //     is a single voter for down scaling
        //
        // two nets - connect each to respective sink (there must also be 2 sinks)
        //   - this happens when going from a triplicated portion to a duplicated
        //     portion - 2 voters scale down to 2 sinks
        //
        // three nets - connect each to respective sink (there must also be 3 sinks)
        //   - this happens when cutting within a triplicated portion

        EdifCellInstance origInstance = origSink.getCellInstance();
        List<EdifSingleBitPort> newESBs = new ArrayList<EdifSingleBitPort>();
        List<EdifCellInstance> newEPRInstances = new ArrayList<EdifCellInstance>();
        EdifSingleBitPort origESB = origSink.getSingleBitPort();
        EdifPort origPort = origESB.getParent();
        int busIndex = origESB.bitPosition();
        if (origInstance == null) { // sink is a top level port
            List<EdifPort> newPorts = _portReplicationMap.get(origPort);
            for (EdifPort newPort : newPorts) {
                EdifSingleBitPort newESB = newPort.getSingleBitPort(busIndex);
                newESBs.add(newESB);
                newEPRInstances.add(null);
            }
        } else { // sink is a port on an instance
            List<EdifCellInstance> newInstances = _instanceReplicationMap.get(origInstance);
            EdifPort newPort = origInstance.getCellType().getMatchingPort(origPort);
            for (EdifCellInstance newInstance : newInstances) {
                EdifSingleBitPort newESB = newPort.getSingleBitPort(busIndex);
                newESBs.add(newESB);
                newEPRInstances.add(newInstance);
            }
        }

        int numberOfNets = newNets.size();

        Iterator<EdifSingleBitPort> esbIt = newESBs.iterator();
        Iterator<EdifCellInstance> instanceIt = newEPRInstances.iterator();
        if (numberOfNets == 1) { // connect single net to multiple sinks
            EdifNet newNet = newNets.get(0);
            while (esbIt.hasNext() && instanceIt.hasNext()) {
                EdifSingleBitPort eprEsb = esbIt.next();
                EdifCellInstance eprInstance = instanceIt.next();
                EdifPortRef newEpr = new EdifPortRef(newNet, eprEsb, eprInstance);
                newNet.addPortConnection(newEpr);
            }
        } else { // connect each net to respective sink -- there better be the same number
            if (numberOfNets != newESBs.size())
                throw new EdifRuntimeException("number of nets doesn't match number of sinks");
            Iterator<EdifNet> netIt = newNets.iterator();
            while (esbIt.hasNext() && instanceIt.hasNext() && netIt.hasNext()) {
                EdifSingleBitPort eprEsb = esbIt.next();
                EdifCellInstance eprInstance = instanceIt.next();
                EdifNet newNet = netIt.next();
                EdifPortRef newEpr = new EdifPortRef(newNet, eprEsb, eprInstance);
                newNet.addPortConnection(newEpr);
            }
        }
    }

    /**
     * Create a comparator and add the output to the list of either persistent
     * or non persistent error signals, depending on the boolean parameter
     * persistentCut.
     * 
     * @param inputs comparator inputs (must be size 2)
     * @param origNet the original net before duplication (for getting a name)
     * @param persistent whether or not this is a persistence comparator
     */
    protected void createComparator(List<EdifNet> inputs, EdifNet origNet, boolean persistent) {
        if (inputs.size() != 2)
            throw new EdifRuntimeException("comparator must have 2 inputs, not " + inputs.size());
        EdifNet[] inputNets = new EdifNet[2];
        for (int i = 0; i < 2; i++) {
            inputNets[i] = inputs.get(i);
        }
        String compareName0 = origNet.getName() + COMPARE_SUFFIX + "_DRC0";
        EdifNet output0 = null;
        try {
            output0 = new EdifNet(compareName0, this);
        } catch (InvalidEdifNameException e1) {
            // will be valid, so won't get here
            e1.toRuntime();
        }
        boolean create = true;
        try {
            addNet(output0);
        } catch (EdifNameConflictException e1) {
            /*
             * May get here if persistents and nonpersistents both want to
             * create a comparator in the same spot - priority goes to the first
             * one, which should be a persistent comparator.
             */
            // e1.toRuntime();
            create = false;
        }
        if (create) {
            _comparators.add(_dwcArchitecture.createVoter(this, compareName0, inputNets, output0));
            if (persistent) {
                _persistentErrorNets0.add(output0);
            } else {
                _nonPersistentErrorNets0.add(output0);
            }
        }

        if (_useDualRail) {
            String compareName1 = origNet.getName() + COMPARE_SUFFIX + "_DRC1";
            EdifNet output1 = null;
            create = true;
            try {
                output1 = new EdifNet(compareName1, this);
            } catch (InvalidEdifNameException e) {
                // will be valid, so won't get here
                e.toRuntime();
            }
            try {
                addNet(output1);
            } catch (EdifNameConflictException e) {
                // May get here if persistents and nonpersistents both
                // want to create a comparator - priority goes to the first one,
                // which should be a persistent comparator.
                create = false;
            }
            if (create) {
                _comparators.add(_dwcArchitecture.createVoter(this, compareName1, inputNets, output1));
                if (persistent) {
                    _persistentErrorNets1.add(output1);
                } else {
                    _nonPersistentErrorNets1.add(output1);
                }
            }
        }
    }

    /**
     * Create the specified number of voters and return the voter ouput nets.
     * 
     * @param inputs voter inputs
     * @param origNet the original net before triplication (for getting a name)
     * @param numberOfVoters the number of voters to create
     * @return the list of voter output nets
     */
    protected List<EdifNet> createVoters(List<EdifNet> inputs, EdifNet origNet, int numberOfVoters) {
        if (inputs.size() != 3)
            throw new EdifRuntimeException("voter must have 3 inputs, not " + inputs.size());
        EdifNet[] inputNets = new EdifNet[3];
        for (int i = 0; i < 3; i++) {
            inputNets[i] = inputs.get(i);
        }
        List<EdifNet> voterOutputs = new ArrayList<EdifNet>(numberOfVoters);
        for (int voterNum = 0; voterNum < numberOfVoters; voterNum++) {
            String voterName = origNet.getName() + VOTER_SUFFIX + "_" + voterNum;
            EdifNet output = null;
            try {
                output = new EdifNet(voterName, this);
            } catch (InvalidEdifNameException e) {
                // will be a valid name
                e.toRuntime();
            }
            try {
                addNet(output);
            } catch (EdifNameConflictException e) {
                // there will be no conflict
                e.toRuntime();
            }
            _voters.add(_tmrArchitecture.createVoter(this, voterName, inputNets, output));
            voterOutputs.add(output);
        }
        return voterOutputs;
    }

    public Collection<EdifCellInstance> getVoters() {
        return _voters;
    }

    public Collection<EdifCellInstance> getComparators() {
        return _comparators;
    }

    public Collection<EdifNet> getPersistentErrorNets() {
        Collection<EdifNet> persNets = new ArrayList<EdifNet>();
        persNets.addAll(_persistentErrorNets0);
        persNets.addAll(_persistentErrorNets1);
        return persNets;
    }

    public Collection<EdifNet> getNonPersistentErrorNets() {
        Collection<EdifNet> nonpersNets = new ArrayList<EdifNet>();
        nonpersNets.addAll(_nonPersistentErrorNets0);
        nonpersNets.addAll(_nonPersistentErrorNets1);
        return nonpersNets;
    }

    /**
     * Determine whether the given EdifPortRef is cut (either a persistent or
     * non-persistent cut)
     */
    protected boolean isCut(EdifPortRef portRef) {
        return _cutSet.contains(portRef);
    }

    /**
     * Merge the given nets into one error signal using the merger provided by
     * the dwc architecture (_dwcArchitecture)
     */
    protected EdifNet mergeNets(List<EdifNet> nets) {
        Stack<EdifNet> netStack = new Stack<EdifNet>();
        netStack.addAll(nets);
        while (netStack.size() > 1) {
            int i = 0;
            List<EdifNet> mergedNets = new ArrayList<EdifNet>();
            while (netStack.size() > 1) {
                EdifNet[] inputs = new EdifNet[2];
                inputs[0] = netStack.pop();
                inputs[1] = netStack.pop();
                EdifNameable mergerName = getUniqueNetNameable(NamedObject.createValidEdifNameable(MERGER_NAME + "_"
                        + i));
                EdifNet merged = new EdifNet(mergerName, this);
                try {
                    addNet(merged);
                } catch (EdifNameConflictException e) {
                    // the name is already unique
                    e.toRuntime();
                }
                _dwcArchitecture.createMerger(this, mergerName.getName(), inputs, merged);
                mergedNets.add(merged);
                i++;
            }
            netStack.addAll(mergedNets);
        }
        return netStack.pop();
    }

    /**
     * Feedback cutset
     */
    private Collection<EdifPortRef> _cutSet;

    /**
     * The architecture for providing tmr (i.e. voters)
     */
    private NMRArchitecture _tmrArchitecture;

    /**
     * The architecture for providing dwc (i.e. comparators)
     */
    private XilinxDWCArchitecture _dwcArchitecture;

    /**
     * List of persistent comparator error outputs
     */
    private List<EdifNet> _persistentErrorNets0;

    private List<EdifNet> _persistentErrorNets1;

    /**
     * List of non-persistent comparator error outputs
     */
    private List<EdifNet> _nonPersistentErrorNets0;

    private List<EdifNet> _nonPersistentErrorNets1;

    /**
     * List of voter instances
     */
    private List<EdifCellInstance> _voters;

    /**
     * List of comparator instances
     */
    private List<EdifCellInstance> _comparators;

    /**
     * Whether or not to use dual rail checkers
     */
    private boolean _useDualRail;

    /**
     * Whether or not to take special care that insertion of output comparators
     * does not interfere with output register packing
     */
    private boolean _packOutputRegs;

    /**
     * Collection of instances that are part of feedback or input to feedback
     */
    private Collection<EdifCellInstance> _feedbackPlusInput;

    /**
     * Reference to OFD EdifCell used for buffering detection signal outputs
     */
    private EdifCell _ofdCell = null;

    /**
     * The clock net of the design
     */
    private EdifNet _clockNet = null;

    /**
     * Whether to register the detection signals
     */
    private boolean _registerDetection;
}
