/*
 * Create a new replicated EdifCell object from an original object.
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
package edu.byu.ece.edif.tools.replicate.nmr;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.byu.ece.edif.arch.xilinx.InstanceHierarchy;
import edu.byu.ece.edif.core.BasicEdifBusNamingPolicy;
import edu.byu.ece.edif.core.BasicEdifBusNetNamingPolicy;
import edu.byu.ece.edif.core.EdifBusNamingPolicy;
import edu.byu.ece.edif.core.EdifBusNetNamingPolicy;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.core.RenamedObject;

// ////////////////////////////////////////////////////////////////////////////
// // NMREdifCell

/**
 * Create a new replicated EdifCell object from an original object. The type of
 * replication (i.e. detection vs. voter), as well as the amount of replication,
 * are determined by sub-classes. This base class provides the methods for
 * replication of the cells, ports, and nets, but not voters. It is based on
 * partial replication and provides functionality to replicate only those
 * instances and ports specified by the caller. Replication within this class is
 * performed at a single level of the hierarchy. It does not recurse into
 * sub-cells to perform replication. This class does not call the replication
 * code. Sub-classes should be created to control the replication process.
 */
public abstract class NMREdifCell extends EdifCell {

    /**
     * Construct an empty EdifCell from an original, non-replicated cell. This
     * constructor does not perform replication. The replication must be called
     * explicitly or performed through a constructor in a sub class.
     * 
     * @param lib EdifLibrary in which to create this EdifCell
     * @param name The name of the new EdifCell to create
     * @param cell The original EdifCell that is to be replicated
     * @param replicationFactor The number of copies of the various Edif objects
     * to create.
     * @param replicationSuffixes String to be appended to the name of the
     * replicated copies of circuit resources to distinguish them from the
     * original resources. various Edif
     * @throws InvalidEdifNameException
     * @throws EdifNameConflictException
     */
    public NMREdifCell(EdifLibrary lib, String name, EdifCell cell, NMRArchitecture nmrArchitecture,
            int replicationFactor, String[] replicationSuffixes) throws EdifNameConflictException,
            InvalidEdifNameException {
        this(lib, new NamedObject(name), cell, nmrArchitecture, replicationFactor, replicationSuffixes);

        //		super(cell.getLibrary(), name);
        //
        //        // Ensure proper parameters
        //        if (replicationFactor < 1)
        //            throw new EdifRuntimeException("Invalid replicationFactor:" + replicationFactor
        //                    + ". The replicationFactor must be greater than zero.");
        //        validateReplicationSuffixes(replicationSuffixes, replicationFactor);
        //
        //        _origCell = cell;
        //        _replicationFactor = replicationFactor;
        //        _replicationSuffixes = replicationSuffixes;

    }

    public NMREdifCell(EdifLibrary lib, EdifNameable name, EdifCell cell, NMRArchitecture nmrArchitecture,
            int replicationFactor, String[] replicationSuffixes) throws EdifNameConflictException {
        super(cell.getLibrary(), name);

        // Ensure proper parameters
        if (replicationFactor < 1)
            throw new EdifRuntimeException("Invalid replicationFactor:" + replicationFactor
                    + ". The replicationFactor must be greater than zero.");
        validateReplicationSuffixes(replicationSuffixes, replicationFactor);

        _origCell = cell;
        _replicationFactor = replicationFactor;
        _replicationSuffixes = replicationSuffixes;
    }

    /**
     * Checks to make sure that the given String array contains valid (unique)
     * suffixes.
     * 
     * @param replicationSuffixes String array of 'N' suffixes
     * @param replicationFactor 'N'
     */
    public static boolean areReplicationSuffixesUnique(String[] replicationSuffixes, int replicationFactor) {
        // Check for conflicting suffix Strings
        // This should be done at the arguments stage if the user is providing the Strings.
        for (int i = 0; i < replicationFactor; i++) {
            for (int j = i + 1; j < replicationFactor; j++) {
                if (replicationSuffixes[i].equals(replicationSuffixes[j]))
                    return false;
            }
        }
        return true;
    }

    // /////////////////////////////////////////////////////////////////
    // // public methods ////

    /**
     * @return the {@linkplain #_edifCellInstanceMap EDIF cell instance map}.
     */
    public Map getEdifCellInstanceMap() {
        return _edifCellInstanceMap;
    }

    /**
     * @return the
     * {@linkplain #_edifPortReplicationMap EDIF port replication map}.
     */
    public Map getEdifPortReplicationMap() {
        return _edifPortReplicationMap;
    }

    /**
     * @return the {@linklplain #_edifNetMap EDIF net map}.
     */
    public Map getEdifNetMap() {
        return _edifNetMap;
    }

    /**
     * @return a Collection of the EdifCellInstance objects that correspond to a
     * replicated EdifCellInstance object in the replicated circuit.
     */
    public Collection getReplicatedInstances() {
        Collection<EdifCellInstance> trip = new ArrayList<EdifCellInstance>();
        for (EdifCellInstance eci : _edifCellInstanceMap.keySet()) {
            if (isCellInstanceReplicated(eci))
                trip.add(eci);
        }
        return trip;
    }

    /**
     * @return a Collection of the EdifNet objects that correspond to a
     * replicated EdifNet object in the replicated circuit.
     */
    public Collection getReplicatedNets() {
        Collection<EdifNet> trip = new ArrayList<EdifNet>();
        for (EdifNet net : _edifNetMap.keySet()) {
            EdifNet trips[] = _edifNetMap.get(net);
            if (trips != null && trips.length > 1)
                trip.add(net);
            if (trips == null) {
                System.err.println("Warning: no corresponding net for " + net);
            }
        }
        return trip;
    }

    /**
     * @return a Collection of the EdifPort objects that correspond to a
     * replicated EdifPort in the replicated circuit.
     */
    public Collection getReplicatedPorts() {
        Collection<EdifPort> trip = new ArrayList<EdifPort>();
        for (EdifPort port : _edifPortReplicationMap.keySet()) {
            EdifPort trips[] = _edifPortReplicationMap.get(port);
            if (trips != null && trips.length > 1)
                trip.add(port);
            if (trips == null) {
                System.err.println("Warning: no corresponding port for " + port);
            }
        }
        return trip;
    }

    /**
     * Determine if the given EdifPort object corresponds to a replicated port
     * in the replicated circuit
     * 
     * @param origPort the EdifPort
     * @return true if the port corresponds to a replicated port in the
     * replicated circuit.
     */
    public boolean isTopLevelPortReplicated(EdifPort origPort) {
        EdifPort[] ports = _edifPortReplicationMap.get(origPort);
        if (ports.length != 1)
            return true;
        return false;
    }

    /**
     * Determine whether the given EdifCellInstance corresponds to a replicated
     * EdifCellInstance in the replicated circuit
     * 
     * @param eci the EdifCellInstance
     * @return true if the EdifCellInstance corresponds to a replicated
     * EdifCellInstance in this replicated circuit.
     */
    public boolean isCellInstanceReplicated(EdifCellInstance eci) {
        EdifCellInstance[] insts = _edifCellInstanceMap.get(eci);
        if (insts.length != 1)
            return true;
        return false;
    }

    /**
     * Determine if the given EdifPortRef from the original circuit corresponds
     * to a replicated port in the replicated circuit. If the EdifPortRef is
     * connected to a top-level port, determine whether the top-level port is
     * replicated. If the port is connected to an EdifCellInstance, determine if
     * the EdifCellInstance has been replicated.
     * 
     * @param portRef the EdifPortRef from the original circuit
     * @return true if the corresponding EdifCellInstance or EdifPort is
     * replicated.
     */
    public boolean isPortRefReplicated(EdifPortRef portRef) {

        if (portRef.isTopLevelPortRef()) {
            if (isTopLevelPortReplicated(portRef.getPort()))
                return true;
        } else {
            if (isCellInstanceReplicated(portRef.getCellInstance()))
                return true;
        }
        return false;
    }

    /**
     * @param filename the output file name for the domain report.
     * @throws FileNotFoundException "if the file exists but is a directory
     * rather than a regular file, does not exist but cannot be created, or
     * cannot be opened for any other reason" (See
     * {@link FileOutputStream#FileOutputStream(String)})
     */
    public void printDomainReport(String filename) // , HashMap domainMap)
            throws FileNotFoundException {

        String domainReportFilename;
        if (filename == null || filename.length() == 0)
            domainReportFilename = this.getName() + "_tmr_domain_report.txt";
        else
            domainReportFilename = filename;
        PrintWriter pw = new PrintWriter(new FileOutputStream(domainReportFilename));
        System.out.println("domainreport=" + domainReportFilename);

        for (int i = 0; i < _instanceDomainArray.length; i++) {
            ArrayList<EdifCellInstance> domainCells = _instanceDomainArray[i];
            for (EdifCellInstance eci : domainCells) {
                InstanceHierarchy hier = new InstanceHierarchy(eci);
                // Print hierarchical instance name
                pw.print(hier.getInstanceName(true));
                // Print cell type
                pw.print("\t" + eci.getCellType().getName());
                // Print Domain
                pw.println("\t" + i);
            }
        }
        pw.close();
    }

    // /////////////////////////////////////////////////////////////////
    // // protected methods ////

    /**
     * Create top-level ports in the replicated EdifCell. At the same time,
     * replicate the top-level ports specified by the parameter.
     * 
     * @param portsToReplicate Collection of EdifPort objects to be replicated.
     * @return the equivalent of the
     * {@linkplain #_edifPortReplicationMap EDIF port replication map}.
     */
    protected Map<EdifPort, EdifPort[]> addTopLevelPorts(Collection<EdifPort> portsToReplicate,
            Map<String, String[]> prePorts) {

        //		Map<String, String[]> prePorts = new LinkedHashMap<String, String[]>();
        //        String[] tmp = { "clk1", "clk2", "clk3" };
        //        prePorts.put("clk", tmp);

        // If there aren't any, set to 0 size array
        if (portsToReplicate == null)
            portsToReplicate = new ArrayList<EdifPort>(0);

        // Get the ports from the original cell
        Collection<EdifPort> origPortList = _origCell.getPortList();

        /*
         * Create a mapping of the current EdifPort objects to an EdifPort[]
         * array that is as large as the number of ports in the original design
         */
        _edifPortReplicationMap = new LinkedHashMap<EdifPort, EdifPort[]>(origPortList.size());

        // Loop through each port in the original cell's ports
        for (EdifPort origPort : origPortList) {
            // Initialize data
            boolean preTrip = false;
            EdifPort newPorts[] = null;
            int numberOfPorts = 1;

            /*
             * Get the names of ports from the user's created list of names that
             * are for the current original port.
             */
            String[] userNames = prePorts.get(origPort.getName());

            /*
             * Flag that we actually got something from the user specification
             * file i.e. that the module has been pretriplicated
             */
            if (userNames != null) {
                preTrip = true;
            }

            /*
             * If the current port is in the list of ports that need to be
             * replicated, or if it has already been hand-triplicated by the
             * user, set the number of ports to make. (probably 3, if we're
             * doing triplication)
             */if (portsToReplicate.contains(origPort) || preTrip) {
                numberOfPorts = _replicationFactor;
            }
            newPorts = new EdifPort[numberOfPorts];

            if (debug)
                System.out.println("Port " + origPort + " will correspond to " + numberOfPorts + " new ports");

            /*
             * Add the port to the array, whether the original port (same name)
             * or multiple (name modified with _replicationSuffix and domain
             * number)
             */
            if (numberOfPorts == 1) {
                /*
                 * Try to create a new port that goes in the same direction with
                 * the same width, and the same name as the original. If it
                 * already exists, then just get the existing one.
                 */
                try {
                    newPorts[0] = addPort(origPort.getEdifNameable(), origPort.getWidth(), origPort.getDirection());
                } catch (EdifNameConflictException e) {
                    newPorts[0] = getPort(origPort.getName());
                    //e.toRuntime();
                }
                // Copy the properties from the original to the new one.
                newPorts[0].copyProperties(origPort);
            } else {
                // Iterate through the ports to be added
                for (int i = 0; i < numberOfPorts; i++) {
                    EdifNameable newPortName;
                    /*
                     * If the design was pretriplicated, then create a valid
                     * name from the one the user provided. Otherwise, create a
                     * valid name with a suffix.
                     */
                    if (preTrip)
                        newPortName = NamedObject.createValidEdifNameable(userNames[i]);
                    else
                        newPortName = replicationNameable(origPort.getEdifNameable(), i);
                    // Add the new port to the design.
                    // TODO: make sure the name is unique
                    try {
                        newPorts[i] = addPort(newPortName, origPort.getWidth(), origPort.getDirection());
                    } catch (EdifNameConflictException e) {
                        e.toRuntime();
                    }
                    // Copy the original port's properties to the new port.
                    newPorts[i].copyProperties(origPort);
                }
            }
            // Add this original port and its new destination ports to the map.
            _edifPortReplicationMap.put(origPort, newPorts);
        }

        return _edifPortReplicationMap;
    }

    /**
     * Attach the nets to the appropriate sink ports.
     * <ul>
     * <li>Single Net and non replicated port ref: single connection made to
     * single net.
     * <li>Single Net and replicated port ref: multiple connections made to
     * single net.
     * <li>Multiple Nets and non replicated port ref: single connection made to
     * first net.
     * <li>Multiple Nets and replicated port ref: single connection made to
     * each net.
     * </ul>
     * 
     * @param origSinkPortRef The sink EdifPortRef associated with the original
     * net that corresponds to the sinks that need to be attached on the new
     * nets.
     * @param newNets The array of new nets that need to be connected to the
     * appropriate sink ports.
     */
    protected void connectSinkPortsToSourceNets(EdifPortRef origSinkPortRef, EdifNet[] newNets) {

        EdifPort origSinkPort = origSinkPortRef.getPort();
        EdifPort newSinkPorts[] = _edifPortReplicationMap.get(origSinkPort);
        EdifCellInstance origSinkInstance = origSinkPortRef.getCellInstance();
        EdifCellInstance newSinkInstances[] = _edifCellInstanceMap.get(origSinkInstance);

        // Determine how many sinks to create
        int numSinks = (isPortRefReplicated(origSinkPortRef)) ? _replicationFactor : 1;

        for (int i = 0; i < numSinks; i++) {
            int sourceNetIndex = 0; // single source
            if (newNets.length > 1)
                sourceNetIndex = i; // multiple sources
            if (origSinkPortRef.isTopLevelPortRef())
                newNets[sourceNetIndex].addPortConnection(null, newSinkPorts[i], origSinkPortRef.getBusMember());
            else {
                newNets[sourceNetIndex].addPortConnection(newSinkInstances[i], origSinkPort, origSinkPortRef
                        .getBusMember());
            }
        }
    }

    /**
     * Create an array of new nets and attach the appropriate drivers to the
     * nets. The number of new nets to create will be either 1 or equal to
     * _replicationFactor.
     */
    protected EdifNet[] createNewNetsWithDrivers(EdifNet net, Collection<EdifPortRef> originalNetDrivers,
            int numberOfNetsToCreate) {

        EdifNet newNets[] = new EdifNet[numberOfNetsToCreate];
        for (int i = 0; i < numberOfNetsToCreate; i++) {
            EdifNet newNet = null;
            EdifNameable newNetName = null;
            if (numberOfNetsToCreate == 1)
                newNetName = net.getEdifNameable();
            else
                newNetName = replicationNetNameable(net.getEdifNameable(), i);
            newNet = new EdifNet(newNetName);

            newNets[i] = newNet;
            newNet.copyProperties(net);
            try {
                addNet(newNet);
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }

            if (debug)
                System.out.println("Creating net " + i + " called " + newNetName);

            /*
             * Connect to the driver port refs. If the original driver is a top
             * level port then we connect to an input port of the top level
             * cell. If the original driver was a cell, then we connect to an
             * output port of the cell instance.
             */
            for (EdifPortRef origDriver : originalNetDrivers) {
                EdifCellInstance[] newInstances = _edifCellInstanceMap.get(origDriver.getCellInstance());

                //				  System.out.print("jth original net driver: driver Ports: " + newDriverPorts + " cell Instances: "
                //                        + newInstances);

                if (origDriver.isTopLevelPortRef()) {
                    EdifPort[] newDriverPorts = _edifPortReplicationMap.get(origDriver.getPort());
                    newNet.addPortConnection(null, newDriverPorts[i], origDriver.getBusMember());
                } else {
                    EdifCellInstance inst = newInstances[i];
                    EdifCell type = inst.getCellType();
                    EdifPort port = type.getMatchingPort(origDriver.getPort());
                    if (port == null)
                        throw new EdifRuntimeException("Can't find port");
                    newNet.addPortConnection(inst, port, origDriver.getBusMember());
                }
            }
        }
        return newNets;
    }

    /**
     * Create the voter circuit for the voting operation. The implementing class
     * must do the following:
     * <ul>
     * <li>Create the voter instance(s) (if any). This voter must have the same
     * number of inputs as specified in the voterInputs array.
     * <li>Create as many voters as specified by the numberOfVoters parameter.
     * This will be either 1 or voterInputs.length
     * <li>Create a valid, unique name for each voter
     * <li>Connect the voterInput nets to each voter that was created.
     * <li>Create a unique new net (with unique net name) for each voter
     * created
     * <li>Attach each new net to the output of each voter - Return these nets
     * at the end of the function
     * </ol>
     * 
     * @param voterInputs The nets that should form the inputs to the voter(s)
     * @param origNet
     * @param numberOfVoters
     * @return An the array of EdifNet objects that are the output of all the
     * voters
     */
    protected abstract EdifNet[] createVoter(EdifNet[] voterInputs, EdifNet origNet, int numberOfVoters);

    /**
     * Determine how many nets will be created from a given set of drivers. This
     * method will throw an exception if there is more than one driver of a net
     * and the replication factor of the different drivers is not the same.
     * <p>
     * Checks the actual CellInstances driving the Net, not the status of the
     * PortRef itself (if it is in the portCutSet). Thus a PortRef that is
     * tagged to be cut will not be if the Net drivers are not triplicated.
     * 
     * @param originalNetDrivers A Collection of EdifPortRef objects that
     * correspond to the drivers of the current net. A driver is either a
     * CellInstance whose output is connected to this Net, or a top-level input
     * port connected to this Net.
     * @return the number of nets that should be created. This will be either 1
     * or equal to _replicationFactor.
     */
    protected int determineNumberOfNets(Collection<EdifPortRef> originalNetDrivers) {
        int numberOfNetsToCreate = 0;
        for (EdifPortRef driver : originalNetDrivers) {
            if (isPortRefReplicated(driver)) {
                if (numberOfNetsToCreate == 0)
                    numberOfNetsToCreate = _replicationFactor;
                else if (numberOfNetsToCreate == 1) // previous driver was not
                    // replicated
                    throw new EdifRuntimeException(
                            "More than one driver with different replication status for driver: " + driver
                                    + " \nin set of drivers: " + originalNetDrivers + " on net: " + driver.getNet());
            } else {
                if (numberOfNetsToCreate == 0)
                    numberOfNetsToCreate = 1;
                else if (numberOfNetsToCreate != 1) // previous driver was
                    // replicated
                    throw new EdifRuntimeException("More than one driver with different replication status");
            }
        }
        return numberOfNetsToCreate;
    }

    /**
     * Perform the replication process for the cell.
     * 
     * @param origPortsToReplicate top-level EdifPort objects from the original
     * EdifCell object to be replicated. This Collection may be null if no port
     * replication is desired.
     * @param edifCellInstancesToReplicate The EdifCellInstance objects to be
     * replicated from the original EdifCell object. This Collection may be null
     * if no instance replication is desired.
     * @param edifPortRefsToCut The EdifPortRef objects from the original design
     * that specify the location of the voter/detection circuits. This
     * Collection may be null if no application-specific voter/detection
     * circuits are desired.
     */
    protected void replicateCell(Collection<EdifPort> origPortsToReplicate,
            Collection<EdifCellInstance> edifCellInstancesToReplicate, Collection<EdifPortRef> edifPortRefsToCut,
            Map<String, String[]> preTripPorts) {

        // empty the design.

        // 1. Add ports
        addTopLevelPorts(origPortsToReplicate, preTripPorts);

        // 2. Replicate cells
        replicateCellInstances(edifCellInstancesToReplicate);

        // 3. replicate nets
        replicateNets(edifPortRefsToCut);

        // 4. Clean up
        cleanUp();
    }

    protected void replicateCell(Collection<EdifPort> origPortsToReplicate,
            Collection<EdifCellInstance> edifCellInstancesToReplicate, Collection<EdifPortRef> edifPortRefsToCut) {
        replicateCell(origPortsToReplicate, edifCellInstancesToReplicate, edifPortRefsToCut, null);
    }

    /**
     * Replicate the EdifCellInstance objects included in the cell replication
     * list.
     * 
     * @param edifCellInstancesToReplicate Indicates which of the instances in
     * the original design to replicate within this new design.
     */
    protected Map replicateCellInstances(Collection edifCellInstancesToReplicate) {

        if (debug)
            System.out.println("Number of instances to replicate: " + edifCellInstancesToReplicate.size());

        // TODO: How do I create an array of ArrayList<EdifCellInstance>?
        _instanceDomainArray = new ArrayList[_replicationFactor];
        int numberOfInstancesToReplicate = edifCellInstancesToReplicate.size();
        for (int i = 0; i < _replicationFactor; i++)
            _instanceDomainArray[i] = new ArrayList<EdifCellInstance>(numberOfInstancesToReplicate);

        if (edifCellInstancesToReplicate == null)
            edifCellInstancesToReplicate = new ArrayList(0);

        Collection<EdifCellInstance> edifCellInstanceList = _origCell.getSubCellList();
        _edifCellInstanceMap = new LinkedHashMap<EdifCellInstance, EdifCellInstance[]>(edifCellInstanceList.size());

        for (EdifCellInstance origInstance : edifCellInstanceList) {

            EdifCell cellRef = origInstance.getCellType();
            EdifCellInstance[] newInstances = null;

            int numberOfCells = 1;
            if (edifCellInstancesToReplicate.contains(origInstance)) {
                numberOfCells = _replicationFactor;
            }
            newInstances = new EdifCellInstance[numberOfCells];

            if (debug)
                System.out.println("Instance: " + origInstance + " replicated " + numberOfCells + " times.");

            /*
             * Create copies of this EdifCellInstance. If the cell isn't to be
             * replicated, just add one instance and keep the name the same. If
             * the cell is to be replicated, add _replicationFactor number of
             * instances, getting the appropriate name for each.
             */
            for (int i = 0; i < numberOfCells; i++) {
                // TODO: insure unique name
                EdifNameable newName = null;
                if (numberOfCells == 1)
                    newName = origInstance.getEdifNameable();
                else
                    newName = replicationEdifNameable(origInstance.getEdifNameable(), i);

                newInstances[i] = new EdifCellInstance(newName, this, cellRef);

                // copy properties
                newInstances[i].copyProperties(origInstance);
                // add cell
                try {
                    addSubCell(newInstances[i]);
                } catch (EdifNameConflictException e) {
                    e.toRuntime();
                }

                if (edifCellInstancesToReplicate.contains(origInstance))
                    _instanceDomainArray[i].add(newInstances[i]);
            }
            _edifCellInstanceMap.put(origInstance, newInstances);
        }

        return _edifCellInstanceMap;
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
    protected void replicateNets(Collection<EdifPortRef> edifPortRefsToCut) {

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
            replicateNet(net, edifPortRefsToCut);

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
     * @param portRefsToCut A Collection of EdifPortRef objects to be cut
     * @return An array of the EdifNet objects created by replicating this
     * EdifNet.
     */
    protected EdifNet[] replicateNet(EdifNet net, Collection<EdifPortRef> portRefsToCut) {

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
        boolean driverIsCut = false;
        for (EdifPortRef driver : originalNetDrivers) {
            if (portRefsToCut.contains(driver)) {
                driverIsCut = true;
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
                if (portRefsToCut.contains(sink))
                    aReplicatedSinkIsCut = true;
            } else {
                // non replicated port refs
                allSinksReplicated = false;
                if (portRefsToCut.contains(sink))
                    aSingleSinkIsCut = true;
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
            // Do not insert a voter in the case of a clock. It is never
            //   a good idea to vote on a clock net.
            else if (!allSinksReplicated && _useVoterForReplicatedNetToOneNetDownscale && !_arch.isClockNet(net))
                numberOfVoters = 1;
            else
                numberOfVoters = 0; // just to make sure
        }

        if (debug)
            System.out.println("Creating " + numberOfVoters + " voters");
        EdifNet voterNets[] = createVoter(newNets, net, numberOfVoters);

        // ////////////////////////////////////////////////////////////////////
        //
        // Hook up the "sinks" to the new net(s)
        //
        // ////////////////////////////////////////////////////////////////////
        for (EdifPortRef sinkPortRef : originalNetSinks) {

            boolean sinkReplicated = isPortRefReplicated(sinkPortRef);
            boolean sinkCut = portRefsToCut.contains(sinkPortRef);

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

    protected void cleanUp() {

    }

    /**
     * Create a new EdifNameable object that incorporates the NMR domain number
     * into the name. This method will identify renamed objects and attempt to
     * create a unique name that incorporates the domain number. Specifically,
     * this will attempt to preserve any bus numbering that is found in the
     * renamed object.
     * 
     * @param name The original name
     * @param domain The domain number
     * @return The new NamedObject or the RenamedObject, as appropriate.
     */
    protected EdifNameable replicationNameable(EdifNameable name, int domain) {
        EdifNameable newName = replicationEdifNameable(name, domain);
        if (name instanceof RenamedObject) {
            String rename = ((RenamedObject) name).getOldName();

            // See if the old name is a "bus" name. If so,
            // create a new valid "bus" name.
            EdifBusNamingPolicy policy = BasicEdifBusNamingPolicy.EdifBusNamingPolicy(rename);
            if (policy != null) {
                String renameString = replicationString(policy.getBusBaseName(rename), domain);
                renameString += policy.getBusRangeSpecifier(rename);
                return new RenamedObject(newName, renameString);
            }
            // TODO: handle single bit names as well as busses?
        }
        return new NamedObject(newName);
    }

    /**
     * Create a new String that incorporates the NMR domain number into the
     * name.
     * 
     * @param origName The original name
     * @param domain The domain number
     * @return the new NamedObject with the new name
     */
    protected EdifNameable replicationEdifNameable(EdifNameable origName, int domain) {
        String newName = replicationString(origName.getName(), domain);
        return NamedObject.createValidEdifNameable(newName);
    }

    /**
     * Create an EdifNameable for replicated EdifNets. Handles three different
     * cases.
     * <ol>
     * <li> Single name (i.e. (net xp_out)): could turn into (net xp_out_TMR_0)
     * <li> Double named, not a bus member (i.e. (net (rename xp_out__
     * "xp_out_?"))): could turn into (net (rename xp_out___TMR_0
     * "xp_out_?_TMR_0"))
     * <li> Double name, bus member (i.e. (net (rename xp_out_5 "xp_out(5)"))):
     * could turn into (net (rename xp_out_TMR_0_5 "xp_out_TMR_0(5)"))
     * </ol>
     */
    protected EdifNameable replicationNetNameable(EdifNameable origName, int domain) {
        EdifNameable result = null;
        String name = origName.getName();
        String oldName = origName.getOldName();
        if (origName instanceof RenamedObject) {
            EdifBusNetNamingPolicy oldNamePolicy = BasicEdifBusNetNamingPolicy.EdifBusNetNamingPolicy(oldName);
            EdifBusNetNamingPolicy namePolicy = BasicEdifBusNetNamingPolicy.EdifBusNetNamingPolicy(name);
            if (oldNamePolicy == null) {
                EdifNameable newName = NamedObject.createValidEdifNameable(replicationString(name, domain));
                String newOldName = replicationString(oldName, domain);
                result = new RenamedObject(newName, newOldName);
            } else {
                int oldBitNumber = oldNamePolicy.getBusPosition(oldName);
                EdifNameable newName = null;
                if (namePolicy == null) {
                    newName = NamedObject.createValidEdifNameable(replicationString(name, domain));
                } else {
                    int bitNumber = namePolicy.getBusPosition(name);
                    String base = namePolicy.getBusBaseName(name);
                    newName = NamedObject.createValidEdifNameable(base + replicationSuffix(domain) + "_" + bitNumber);
                }
                String newOldName = oldNamePolicy.getBusBaseName(oldName) + replicationSuffix(domain)
                        + oldNamePolicy.generateBitSuffix(oldBitNumber);
                result = new RenamedObject(newName, newOldName);
            }
        } else {
            result = NamedObject.createValidEdifNameable(replicationString(name, domain));
        }
        return result;
    }

    /**
     * Create a new String that incorporates the NMR domain number into the
     * name. Note that this is NOT necessarily a valid EDIF name. The origName
     * can be invalid and thus the resulting concatenated String could be
     * invalid.
     * 
     * @param origName The original name
     * @param domain The domain number
     * @return the String with the replication suffix and domain number.
     */
    protected String replicationString(String origName, int domain) {
        return origName + replicationSuffix(domain);
    }

    /**
     * Create a new String to be added to the end of a triplicated element that
     * incorporates the NMR domain number.
     * 
     * @param domain The domain number
     * @return the String with the replication suffix and domain number.
     */
    protected String replicationSuffix(int domain) {
        //return "_" + _replicationSuffix + "_" + domain;
        return _replicationSuffixes[domain];
    }

    protected void validateReplicationSuffixes(String[] replicationSuffixes, int replicationFactor) {
        if (replicationSuffixes == null)
            throw new EdifRuntimeException("Invalid replicationSuffixes. The replicationSuffix cannot be null.");
        if (replicationSuffixes.length != replicationFactor)
            throw new EdifRuntimeException("Invalid replicationSuffixes. The replicationSuffix array must have "
                    + replicationFactor + " elements.");
        if (!areReplicationSuffixesUnique(replicationSuffixes, replicationFactor))
            throw new EdifRuntimeException("ERROR: All replication suffixes must be unique.");
    }

    protected boolean debug = false;

    /**
     * A Map between each of the original EdifPort objects of the top-level cell
     * and an array of corresponding EdifPort objects in the replicated cell. If
     * a given EdifPort object is replicated, the corresponding array will have
     * a length of _replicationFactor. If the EdifPort is not replicated, the
     * array will be of length one.
     * <p>
     * Note that this does not map EdifPort objects of EdifCellInstances within
     * the top-level cell.
     */
    protected Map<EdifPort, EdifPort[]> _edifPortReplicationMap;

    /**
     * A Map between each of the original EdifCellInstance objects and an array
     * of corresponding EdifCellInstace objects in the replicated cell. If a
     * given instance is replicated, the corresponding array will be of length
     * _replicationFactor. If the instance is not replicated, the array will be
     * of length one.
     */
    protected Map<EdifCellInstance, EdifCellInstance[]> _edifCellInstanceMap;

    /**
     * A Map between each of the original EdifNet objects and an array of
     * corresponding EdifNet objects to be created in the replicated cell. If a
     * given Net is replicated, the corresponding array will have a length of
     * _replicationFactor. If the Net is not replicated, the array will have a
     * length of one.
     */
    protected Map<EdifNet, EdifNet[]> _edifNetMap;

    /**
     * The original, non-replicated EdifCell.
     */
    protected EdifCell _origCell;

    /**
     * This flag specifies which signal is attached to "non cut inputs". The
     * condition of interest occurs when there is a replicated net and the net
     * is "cut" by a voter. In this case, a voter will be inserted on the
     * signal. However, some inputs of this net will not be "cut" (i.e. they do
     * not necessarily need the voted input). This flag specifies which signal
     * is passed to these inputs. If this flag is true, non-cut inputs will
     * receive the voted signal. If this flag is false, these inputs will
     * receive the non-voted signal.
     */
    protected boolean _useVoterOutputsForNonCutInputs = true;

    /**
     * This flag specifies the wire object to use when a replicated wire is
     * downscaled to a single wire. If this flag is true, the single wire that
     * is used by the rest of the circuit will be a vote of the three TMR
     * signals. If this flag is false, one of the 3 wires will be used.
     */
    protected boolean _useVoterForReplicatedNetToOneNetDownscale = true;

    // /////////////////////////////////////////////////////////////////
    // // private variables ////

    /**
     * String to be appended to the name of the replicated copies of circuit
     * resources to distinguish them from the original resources. various Edif
     * objects. For example, if the replicationSuffix is "TMR", the
     * replicationFactor is 3, and the original EdifCell contains an
     * EdifCellInstance named "MyAdder", then the resulting NMREdifCell will
     * contain EdifCellInstances with the names "MyAdder_TMR_0",
     * "MyAdder_TMR_1", and "MyAdder_TMR_2". Similarly, an EdifNet that was
     * previously called "addOut" would, after replication, become
     * "addOut_TMR0", "addOut_TMR1", and "addOut_TMR2"
     */
    private String[] _replicationSuffixes;

    /**
     * The number of copies of the original resources to create. A value of 1
     * indicates no replication, 2 duplication, 3 replication, 5
     * quintuplication, etc. Must be greater than zero.
     */
    private int _replicationFactor;

    protected ArrayList<EdifCellInstance>[] _instanceDomainArray;

    protected NMRArchitecture _arch;

}

// DuplexWithDetect
// TMRWithDetect
// Quad
// Quint

