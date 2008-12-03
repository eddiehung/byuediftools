/*
 * This class facilitates applying a mixture of triplication and duplication
 * to a single design.
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
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.XilinxDWCArchitecture;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.iob.AbstractIOB;
import edu.byu.ece.edif.util.iob.AbstractIOBAnalyzer;
import edu.byu.ece.edif.util.iob.XilinxVirtexIOBAnalyzer;

/**
 * This class facilitates applying a mixture of triplication and duplication
 * to a single design.
 */
public class TMRDWCEdifCell extends IJMREdifCell {

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

        DEFAULT_SUFFIXES.put(3, tmrSuffixes);
        DEFAULT_SUFFIXES.put(2, dwcSuffixes);
    }

    public static String VOTER_SUFFIX = "_VOTER";

    public static String COMPARE_SUFFIX = "_COMPARE";

    public static String MERGER_NAME = "DWC_MERGER";

    public static final String PERSISTENT_SUFFIX = "DWC_PERSISTENT";
    public static final String NON_PERSISTENT_SUFFIX = "DWC_NON_PERSISTENT";
    public static final String SINGLE_RAIL_SUFFIX = "";
    public static final String DUAL_RAIL_0_SUFFIX = "_DRC0";
    public static final String DUAL_RAIL_1_SUFFIX = "_DRC1";
    
    /**
     * Create a new TMRDWCEdifCell, used for applying triplication and
     * duplication.
     */
    public TMRDWCEdifCell(EdifLibrary lib, String name, EdifCell origCell,
            Collection<EdifCellInstance> feedbackPlusInput, NMRArchitecture tmrArchitecture,
            NMRArchitecture dwcArchitecture, Map<Integer, List<EdifPort>> portsToReplicate,
            Map<Integer, List<EdifCellInstance>> instancesToReplicate, Collection<EdifPortRef> cutSet,
            boolean useDualRail, boolean packOutputRegs, boolean registerDetection, EdifNet clockNet,
            Map<Integer, List<String>> replicationSuffixes, boolean noObufs) throws EdifNameConflictException,
            InvalidEdifNameException {
        super(lib, name, origCell, replicationSuffixes);
        _tmrArchitecture = tmrArchitecture;
        _dwcArchitecture = (XilinxDWCArchitecture) dwcArchitecture;
        _cutSet = cutSet;
        
        _errorNetManager = new ErrorNetManager();
        _errorNetManager.setComparatorString(DWCComparatorType.PERSISTENT, PERSISTENT_SUFFIX);
        _errorNetManager.setComparatorString(DWCComparatorType.NON_PERSISTENT, NON_PERSISTENT_SUFFIX);
        _errorNetManager.setRailSuffix(DWCRailType.SINGLE_RAIL, SINGLE_RAIL_SUFFIX);
        _errorNetManager.setRailSuffix(DWCRailType.DUAL_RAIL_0, DUAL_RAIL_0_SUFFIX);
        _errorNetManager.setRailSuffix(DWCRailType.DUAL_RAIL_1, DUAL_RAIL_1_SUFFIX);
        
        _usedRailTypes = new ArrayList<DWCRailType>();
        if (useDualRail) {
        	_usedRailTypes.add(DWCRailType.DUAL_RAIL_0);
        	_usedRailTypes.add(DWCRailType.DUAL_RAIL_1);
        }
        else
        	_usedRailTypes.add(DWCRailType.SINGLE_RAIL);
        
        _feedbackPlusInput = feedbackPlusInput;

        _packOutputRegs = packOutputRegs;
        _registerDetection = registerDetection;
        _clockNet = clockNet;

        _voters = new ArrayList<EdifCellInstance>();
        _comparators = new ArrayList<EdifCellInstance>();

        _noObufs = noObufs;
        replicateCell(portsToReplicate, instancesToReplicate);
    }

    /**
     * Create a new TMRDWCEdifCell, used for applying triplication and
     * duplication.
     */
    public TMRDWCEdifCell(EdifLibrary lib, String name, EdifCell origCell,
            Collection<EdifCellInstance> feedbackPlusInput, NMRArchitecture tmrArchitecture,
            NMRArchitecture dwcArchitecture, Map<Integer, List<EdifPort>> portsToReplicate,
            Map<Integer, List<EdifCellInstance>> instancesToReplicate, Collection<EdifPortRef> cutSet,
            boolean useDualRail, boolean packOutputRegs, boolean registerDetection, EdifNet clockNet, boolean noObufs)
            throws EdifNameConflictException, InvalidEdifNameException {
        this(lib, name, origCell, feedbackPlusInput, tmrArchitecture, dwcArchitecture, portsToReplicate,
                instancesToReplicate, cutSet, useDualRail, packOutputRegs, registerDetection, clockNet,
                DEFAULT_SUFFIXES, noObufs);
    }

    /**
     * Merge comparator error signals
     * <pre>
     * There is a tradeoff here between
     *   1. Inserting comparators as late as possible (i.e. after final
     *      register if there is one) but not allowing the final register
     *      to be packed into the IOB.
     *   and
     *   2. Inserting comparators before the very last register (if there
     *      is one) and allowing the output registers to be packed.
     * </pre>
     * It is generally expected that the user will want to allow the output
     * registers to be packed at the expense of not comparing at the very
     * latest location possible, but we still provide the option.
     */
    @Override
    protected void cleanUp() {
        // Add output comparators
        EdifCellInstanceGraph ecic = new EdifCellInstanceGraph(_origCell);
        AbstractIOBAnalyzer iob = new XilinxVirtexIOBAnalyzer((FlattenedEdifCell) _origCell, ecic);
        
        // netsToCompare will hold nets from the original EdifCell (the ones
        // that weren't actually duplicated are filtered out later)
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
        // filter out the nets that weren't actually duplicated
        for (EdifNet netToCompare : netsToCompare) {
            List<EdifNet> nets = _netReplicationMap.get(netToCompare);
            if (nets.size() == 2)
                createComparator(nets, netToCompare, DWCComparatorType.NON_PERSISTENT);
        }


        mergeAndNameErrorSignals();

    }

    /**
     * Merge comparator outputs and add related output circuitry
     */
    protected void mergeAndNameErrorSignals() {

    	for (DWCComparatorType comparatorType : DWCComparatorType.values()) {
    		String comparatorString = _errorNetManager.getComparatorString(comparatorType);
    	  	int railNum = 0;
    		for (DWCRailType railType : _usedRailTypes) {
    			List<EdifNet> netsToMerge = _errorNetManager.getNetList(comparatorType, railType);
    			if (netsToMerge.size() > 0) {
    				EdifNet mergedErrorNet = mergeNets(netsToMerge);
    				EdifNameable outName = NamedObject.createValidEdifNameable(comparatorString + "_" + railNum);
    				EdifNameable portName = getInterface().getUniquePortNameable(outName);
    				EdifNameable netName = getUniqueNetNameable(outName);
    				EdifNet outNet = new EdifNet(netName, this);
    				EdifNet ofdNet = null;
    				String ofdName = null;
    				String obufName = null;
    				if (_registerDetection) {
    					EdifNameable ofdNetName = getUniqueNetNameable(NamedObject.createValidEdifNameable(comparatorString
    							+ "_OFD_" + railNum));
    					ofdNet = new EdifNet(ofdNetName, this);
    					ofdName = comparatorString + "_" + railNum + "_OFD";
    				}
    				obufName = comparatorString + "_" + railNum + "_OBUF";

    				try {
    					addNet(outNet);
    				} catch (EdifNameConflictException e1) {
    					// can't get here
    					e1.toRuntime();
    				}

    				EdifPort portToAdd = addPortUniqueName(portName, 1, EdifPort.OUT);
    				EdifPortRef portRefToAdd = new EdifPortRef(outNet, portToAdd.getSingleBitPort(0), null);
    				outNet.addPortConnection(portRefToAdd);

    				if (!_noObufs) {
    					if (_registerDetection) {
    						try {
    							addNet(ofdNet);
    						} catch (EdifNameConflictException e) {
    							// can't get here
    							e.toRuntime();
    						}
    						createOFD(this, ofdName, mergedErrorNet, ofdNet);
    						_dwcArchitecture.createOBUF(this, obufName, ofdNet, outNet);
    					} else {
    						_dwcArchitecture.createOBUF(this, obufName, mergedErrorNet, outNet);
    					}
    				}
    				else {
    					if (_registerDetection) {
    						createOFD(this, ofdName, mergedErrorNet, ofdNet);
    						for (EdifPortRef epr : ofdNet.getConnectedPortRefs()) {
    							EdifPortRef newEpr = new EdifPortRef(outNet, epr.getSingleBitPort(), epr
    									.getCellInstance());
    							outNet.addPortConnection(newEpr);
    						}
    					} else {
    						deleteNet(mergedErrorNet);
    						for (EdifPortRef epr : mergedErrorNet.getConnectedPortRefs()) {
    							EdifPortRef newEpr = new EdifPortRef(outNet, epr.getSingleBitPort(), epr
    									.getCellInstance());
    							outNet.addPortConnection(newEpr);
    						}
    					}
    				}
    			}
    			railNum++;
    		}
    	}
    }

    /**
     * Create an output register instance
     */
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
        EdifNet newClock = _netReplicationMap.get(_clockNet).iterator().next();
        newClock.addPortConnection(ofdCellInstance, "C");

        return ofdCellInstance;

    }

    /**
     * Get an output register cell (FD). If it is not already in a library of
     * the EdifEnvironment, add it to one of the libraries.
     */
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

    /**
     * This method is responsible for connecting nets to the appropriate sinks.
     * It takes the necessary measures to deal with replication factor
     * boundaries.
     */
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
                createComparator(newNets, origNet, DWCComparatorType.PERSISTENT); // persistent error output
            } else {
                if (hasSingleSink || hasTripledSink)
                    createComparator(newNets, origNet, anySinkInFeedbackPlusInput ? DWCComparatorType.PERSISTENT : DWCComparatorType.NON_PERSISTENT); // mark as persistent if related to feedback
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
     * persistent.
     * 
     * @param inputs comparator inputs (must be size 2)
     * @param origNet the original net before duplication (for getting a name)
     * @param persistent whether or not this is a persistence comparator
     */
    protected void createComparator(List<EdifNet> inputs, EdifNet origNet, DWCComparatorType comparatorType) {
        if (inputs.size() != 2)
            throw new EdifRuntimeException("comparator must have 2 inputs, not " + inputs.size());
        EdifNet[] inputNets = new EdifNet[2];
        for (int i = 0; i < 2; i++) {
            inputNets[i] = inputs.get(i);
        }
        
        for (DWCRailType railType : _usedRailTypes) {
        	EdifNet output = createComparatorOutputNet(origNet, _errorNetManager.getRailSuffix(railType));
        	if (output != null) {
        		_comparators.add(_dwcArchitecture.createVoter(this, output.getName(), inputNets, output));
        		_errorNetManager.addNet(comparatorType, railType, output);
        	}
        }
    }
    
    /**
     * Create, name, and add to EdifCell an output net for a comparator that
     * will be created.
     * 
     * This method with catch an EdifNameConflictException in the case that
     * a persistent comparator and non-persistent comparator are being created
     * in the same location (hopefully this doesn't happen anyway). In this case,
     * this method will return null and the calling method should not continue
     * creating a comparator.
     * 
     * @param origNet EdifNet from the original EdifCell being replicated
     *        (before replication)
     * @param persistent whether or not this is a persistence comparator
     * @param suffix an extra suffix to be added onto the output net name
     *        (typically used to indicate one of the dual-rail comparator lines)
     * @return the created EdifNet that will be an output of the comparator
     *         to be created
     */
    protected EdifNet createComparatorOutputNet(EdifNet origNet, String suffix) {
    	String compareName = origNet.getName() + COMPARE_SUFFIX + suffix;
        EdifNet outputNet = null;
        try {
            outputNet = new EdifNet(compareName, this);
        } catch (InvalidEdifNameException e1) {
            // will be valid, so won't get here
            e1.toRuntime();
        }

        try {
            addNet(outputNet);
        } catch (EdifNameConflictException e1) {
            /*
             * May get here if persistents and nonpersistents both want to
             * create a comparator in the same spot - priority goes to the first
             * one, which should be a persistent comparator.
             */
            return null;
        }
        return outputNet;
    }

    /**
     * Create the specified number of voters and return the voter output nets.
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

    /**
     * Get a Collection of the voters in the design.
     */
    public Collection<EdifCellInstance> getVoters() {
        return _voters;
    }

    /**
     * Get a Collection of the comparators in the design.
     */
    public Collection<EdifCellInstance> getComparators() {
        return _comparators;
    }

    /**
     * Get a Collection of the persistent error nets in the design.
     */
    public Collection<EdifNet> getPersistentErrorNets() {
        Collection<EdifNet> persNets = new ArrayList<EdifNet>();
        for (DWCRailType railType : _usedRailTypes) {
        	persNets.addAll(_errorNetManager.getNetList(DWCComparatorType.PERSISTENT, railType));
        }
        return persNets;
    }

    /**
     * Get a Collection of the non-persistent error nets in the design.
     */
    public Collection<EdifNet> getNonPersistentErrorNets() {
        Collection<EdifNet> nonpersNets = new ArrayList<EdifNet>();
        for (DWCRailType railType : _usedRailTypes) {
        	nonpersNets.addAll(_errorNetManager.getNetList(DWCComparatorType.NON_PERSISTENT, railType));
        }
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
    
    private ErrorNetManager _errorNetManager;

    /**
     * List of persistent comparator error outputs
     */

    /**
     * List of non-persistent comparator error outputs
     */

    private List<DWCRailType> _usedRailTypes;
    
    /**
     * List of voter instances
     */
    private List<EdifCellInstance> _voters;

    /**
     * List of comparator instances
     */
    private List<EdifCellInstance> _comparators;

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

    /**
     * Whether to disable insertion of output buffers after error detection
     * signals
     */
    private boolean _noObufs;
}
