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
package edu.byu.ece.edif.tools.replicate.nmr.tmr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.tools.replicate.PartialReplicationDescription;
import edu.byu.ece.edif.tools.replicate.ReplicationType;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.NMREdifCell;

public class TMREdifCell extends NMREdifCell {

    /**
     * Create a triplicated EdifCell in the given library with the given name
     * from the given original EdifCell. Specific EdifCellInstance and
     * EdifPortRef objects, along with top-level EdifPort objects, can be
     * explicitly specified to be triplicated.
     * 
     * @param lib The EdifLibrary in which to create the triplicated EdifCell
     * @param name The name of the resulting, triplicated EdifCell
     * @param cell The original, non-triplicated EdifCell
     * @param tmrArchitecture
     * @param origPortsToTriplicate top-level EdifPort objects to be triplicated
     * @param edifCellInstancesToTriplicate A Collection of EdifCellInstance
     * objects to be triplicated
     * @param edifPortRefsToCut A Collection of EdifPortRef objects to be
     * triplicated
     * @throws InvalidEdifNameException
     * @throws EdifNameConflictException
     */
    public TMREdifCell(EdifLibrary lib, String name, EdifCell cell, NMRArchitecture tmrArchitecture,
            Collection<EdifPort> origPortsToTriplicate, Collection<EdifCellInstance> edifCellInstancesToTriplicate,
            Collection<EdifPortRef> edifPortRefsToCut) throws EdifNameConflictException, InvalidEdifNameException {
        super(lib, name, cell, tmrArchitecture, _replicationFactor, _defaultReplicationSuffixes);
        _arch = tmrArchitecture;

        replicateCell(origPortsToTriplicate, edifCellInstancesToTriplicate, edifPortRefsToCut);
    }

    public TMREdifCell(EdifLibrary lib, EdifNameable name, EdifCell cell, NMRArchitecture tmrArchitecture,
            Collection<EdifPort> origPortsToTriplicate, Collection<EdifCellInstance> edifCellInstancesToTriplicate,
            Collection<EdifPortRef> edifPortRefsToCut) throws EdifNameConflictException {
        super(lib, name, cell, tmrArchitecture, _replicationFactor, _defaultReplicationSuffixes);
        _arch = tmrArchitecture;

        replicateCell(origPortsToTriplicate, edifCellInstancesToTriplicate, edifPortRefsToCut);
    }

    public TMREdifCell(EdifLibrary lib, EdifNameable name, NMRArchitecture tmrArchitecture,
            PartialReplicationDescription desc) throws EdifNameConflictException {
        super(lib, name, desc.cellToReplicate, tmrArchitecture, _replicationFactor, _defaultReplicationSuffixes);
        _arch = tmrArchitecture;

        Collection<EdifCellInstance> instancesToTriplicate = desc.getInstances(ReplicationType.TRIPLICATE);
        Collection<EdifPort> portsToTriplicate = desc.getPorts(ReplicationType.TRIPLICATE);
        replicateCell(portsToTriplicate, instancesToTriplicate, desc.portRefsToCut);
    }

    /**
     * Create a triplicated EdifCell in the given library with the given name
     * from the given original EdifCell. Specific EdifCellInstance and
     * EdifPortRef objects, along with top-level EdifPort objects, can be
     * explicitly specified to be triplicated.
     * 
     * @param lib The EdifLibrary in which to create the triplicated EdifCell
     * @param name The name of the resulting, triplicated EdifCell
     * @param cell The original, non-triplicated EdifCell
     * @param tmrArchitecture
     * @param origPortsToTriplicate top-level EdifPort objects to be triplicated
     * @param edifCellInstancesToTriplicate A Collection of EdifCellInstance
     * objects to be triplicated
     * @param edifPortRefsToCut A Collection of EdifPortRef objects to be
     * triplicated
     * @param replicationSuffixes An array of 3 String objects which should be
     * appended to each triplicated element of the TMREdifCell. Each element of
     * the array corresponds to one TMR domain.
     * @throws InvalidEdifNameException
     * @throws EdifNameConflictException
     */
    public TMREdifCell(EdifLibrary lib, String name, EdifCell cell, NMRArchitecture tmrArchitecture,
            Collection<EdifPort> origPortsToTriplicate, Collection<EdifCellInstance> edifCellInstancesToTriplicate,
            Collection<EdifPortRef> edifPortRefsToCut, String[] replicationSuffixes) throws EdifNameConflictException,
            InvalidEdifNameException {
        super(lib, name, cell, tmrArchitecture, _replicationFactor, replicationSuffixes);
        _arch = tmrArchitecture;

        replicateCell(origPortsToTriplicate, edifCellInstancesToTriplicate, edifPortRefsToCut);
    }

    public TMREdifCell(EdifLibrary lib, EdifNameable name, EdifCell cell, NMRArchitecture tmrArchitecture,
            Collection<EdifPort> origPortsToTriplicate, Collection<EdifCellInstance> edifCellInstancesToTriplicate,
            Collection<EdifPortRef> edifPortRefsToCut, String[] replicationSuffixes) throws EdifNameConflictException {
        super(lib, name, cell, tmrArchitecture, _replicationFactor, replicationSuffixes);
        _arch = tmrArchitecture;

        replicateCell(origPortsToTriplicate, edifCellInstancesToTriplicate, edifPortRefsToCut);
    }

    public TMREdifCell(EdifLibrary lib, EdifNameable name, NMRArchitecture tmrArchitecture,
            PartialReplicationDescription tmrDescription, String[] replicationSuffixes)
            throws EdifNameConflictException {
        super(lib, name, tmrDescription.cellToReplicate, tmrArchitecture, _replicationFactor, replicationSuffixes);
        _arch = tmrArchitecture;

        Collection<EdifCellInstance> instancesToTriplicate = tmrDescription.getInstances(ReplicationType.TRIPLICATE);
        Collection<EdifPort> portsToTriplicate = tmrDescription.getPorts(ReplicationType.TRIPLICATE);

        replicateCell(portsToTriplicate, instancesToTriplicate, tmrDescription.portRefsToCut);
    }

    public TMREdifCell(EdifLibrary lib, EdifNameable name, NMRArchitecture tmrArchitecture,
            PartialReplicationDescription tmrDescription, Map<String, String[]> userTMRPorts,
            String[] replicationSuffixes) throws EdifNameConflictException {

        super(lib, name, tmrDescription.cellToReplicate, tmrArchitecture, _replicationFactor, replicationSuffixes);
        _arch = tmrArchitecture;

        Collection<EdifCellInstance> instancesToTriplicate = tmrDescription.getInstances(ReplicationType.TRIPLICATE);
        Collection<EdifPort> portsToTriplicate = tmrDescription.getPorts(ReplicationType.TRIPLICATE);

        replicateCell(portsToTriplicate, instancesToTriplicate, tmrDescription.portRefsToCut, userTMRPorts);

    }

    /**
     * Overrides {@link NMREdifCell#createVoter(EdifNet[], EdifNet, int)}
     */
    @Override
    protected EdifNet[] createVoter(EdifNet[] voterInputs, EdifNet origNet, int numberOfVoters) {

        if (debug)
            System.out.println("Creating " + numberOfVoters + " voters");
        EdifNet voterNets[] = new EdifNet[numberOfVoters];
        for (int i = 0; i < numberOfVoters; i++) {
            EdifNet voterOutputNet = null;
            try {
                voterOutputNet = new EdifNet(origNet.getName() + "_VOTER" + i);
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            }
            voterNets[i] = voterOutputNet;
            voterOutputNet.copyProperties(origNet); // TODO: does this make
            // sense?
            try {
                addNet(voterOutputNet);
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }

            String voterName = voterOutputNet.getName();
            EdifCellInstance voterInstance = _arch.createVoter(this, voterName, voterInputs, voterOutputNet);
            _voters.add(voterInstance);
        }

        return voterNets;
    }

    public Collection<EdifCellInstance> getVoters() {
        return _voters;
    }

    protected Collection<EdifCellInstance> _voters = new ArrayList<EdifCellInstance>();

    /**
     * Passed to parent ({@link NMREdifCell}) to indicate triplication (not
     * duplication nor quintuplication).
     */
    private static final int _replicationFactor = 3;

    /**
     * Passed to parent (@{link NMREdifCell}) as the strings to append to the
     * end of resource names when triplicated.
     */
    //private static final String[] _replicationSuffix = "TMR";
    public static final String[] _defaultReplicationSuffixes = { "_TMR_0", "_TMR_1", "_TMR_2" };

}
