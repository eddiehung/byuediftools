/*
 * This class facilitates the replication of a design in which
 * there will be more than one replication factor in the same design (i.e
 * mixing triplication and duplication in the same design). 
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.core.RenamedObject;

/**
 * The purpose of this class is to facilitate replication of a design in which
 * there will be more than one replication factor in the same design (i.e
 * mixing triplication and duplication in the same design). 
 * @author jonjohn
 *
 */
public abstract class IJMREdifCell extends EdifCell {

    /**
     * Construct a replicated EDIF cell with the elements replicated according
     * to the replication factors specified.
     * 
     * @param lib the library to place the cell in
     * @param name the name of the cell
     * @param origCell the cell to replicate
     * @param replicationSuffixes replication suffixes
     * @throws EdifNameConflictException
     * @throws InvalidEdifNameException
     */
    public IJMREdifCell(EdifLibrary lib, String name, EdifCell origCell, Map<Integer, List<String>> replicationSuffixes)
            throws EdifNameConflictException, InvalidEdifNameException {
        super(lib, name);
        _replicationSuffixes = replicationSuffixes;
        _origCell = origCell;
    }

    /**
     * Add the correct number of replicated copies of each original instance to
     * the new cell
     * 
     * @param instancesToReplicate a map indicating the replication factor for
     * each instance to be replicated
     */
    protected void addInstances(Map<Integer, List<EdifCellInstance>> instancesToReplicate) {
        Collection<EdifCellInstance> allInstances = new ArrayList<EdifCellInstance>(_origCell.getSubCellList());
        _instanceReplicationMap = new LinkedHashMap<EdifCellInstance, List<EdifCellInstance>>();
        _instanceDomainMap = new LinkedHashMap<Integer, Map<Integer, List<EdifCellInstance>>>(); // HashMap<replicationFactor, Map<domain, List<instance>>>
        // First add the instances that will be replicated, each according to
        // its replication factor
        for (int factor : instancesToReplicate.keySet()) {
            Map<Integer, List<EdifCellInstance>> factorDomainMap = new LinkedHashMap<Integer, List<EdifCellInstance>>();
            _instanceDomainMap.put(factor, factorDomainMap);
            for (EdifCellInstance origInstance : instancesToReplicate.get(factor)) {
                List<EdifCellInstance> replicationList = new ArrayList<EdifCellInstance>(factor);
                EdifCell cellRef = origInstance.getCellType();
                for (int domain = 0; domain < factor; domain++) {
                    List<EdifCellInstance> domainList = _instanceDomainMap.get(factor).get(domain);
                    if (domainList == null) {
                        domainList = new ArrayList<EdifCellInstance>(factor);
                        factorDomainMap.put(domain, domainList);
                    }
                    EdifNameable newName = replicationNameable(origInstance.getEdifNameable(), factor, domain);
                    newName = getUniqueInstanceNameable(newName);
                    EdifCellInstance newInstance = null;
                    newInstance = new EdifCellInstance(newName, this, cellRef);
                    newInstance.copyProperties(origInstance);
                    addSubCellUniqueName(newInstance);
                    replicationList.add(newInstance);
                    domainList.add(newInstance);
                }
                _instanceReplicationMap.put(origInstance, replicationList);
                allInstances.remove(origInstance);
            }
        }

        // now add the rest of the instances
        Map<Integer, List<EdifCellInstance>> factorDomainMap = new LinkedHashMap<Integer, List<EdifCellInstance>>(1);
        _instanceDomainMap.put(1, factorDomainMap);
        List<EdifCellInstance> domainList = new ArrayList<EdifCellInstance>();
        factorDomainMap.put(0, domainList);
        for (EdifCellInstance origInstance : allInstances) {
            List<EdifCellInstance> replicationList = new ArrayList<EdifCellInstance>(1);
            EdifCell cellRef = origInstance.getCellType();
            EdifNameable newName = getUniqueInstanceNameable(origInstance);
            EdifCellInstance newInstance = new EdifCellInstance(newName, this, cellRef);
            newInstance.copyProperties(origInstance);
            addSubCellUniqueName(newInstance);
            replicationList.add(newInstance);
            domainList.add(newInstance);
            _instanceReplicationMap.put(origInstance, replicationList);
        }
    }

    /**
     * Add the correct number of replicated copies of each original net to the
     * new cell.
     */
    protected void addNets() {
        _netReplicationMap = new LinkedHashMap<EdifNet, List<EdifNet>>();
        for (EdifNet net : _origCell.getNetList()) {
            _netReplicationMap.put(net, replicateNet(net));
        }
    }

    /**
     * Add the correct number of replicated copies of each original top level
     * port to the new cell
     * 
     * @param portsToReplicate a map indicating the replication factor for each
     * port to be replicated
     */
    protected void addTopLevelPorts(Map<Integer, List<EdifPort>> portsToReplicate) {
        Collection<EdifPort> allPorts = _origCell.getPortList();
        _portReplicationMap = new LinkedHashMap<EdifPort, List<EdifPort>>(allPorts.size());

        // First add the ports that will be replicated, each according to its
        // replication factor
        for (int factor : portsToReplicate.keySet()) {
            for (EdifPort origPort : portsToReplicate.get(factor)) {
                List<EdifPort> replicationList = new ArrayList<EdifPort>(factor);
                for (int domain = 0; domain < factor; domain++) {
                    EdifNameable newName = replicationNameable(origPort.getEdifNameable(), factor, domain);
                    EdifPort newPort = null;
                    try {
                        newPort = addPort(newName, origPort.getWidth(), origPort.getDirection());
                    } catch (EdifNameConflictException e) {
                        e.toRuntime();
                    }
                    newPort.copyProperties(origPort);
                    replicationList.add(newPort);
                }
                _portReplicationMap.put(origPort, replicationList);
                allPorts.remove(origPort);
            }
        }

        // now add whatever ports are left (non replicated ports)
        for (EdifPort origPort : allPorts) {
            List<EdifPort> replicationList = new ArrayList<EdifPort>(1);
            EdifPort newPort = null;
            try {
                newPort = addPort(origPort.getEdifNameable(), origPort.getWidth(), origPort.getDirection());
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }
            newPort.copyProperties(origPort);
            replicationList.add(newPort);
            _portReplicationMap.put(origPort, replicationList);
        }
    }

    /**
     * This method is to be implemented in the child class and should do
     * whatever final procedure is necessary to finalize the replication.
     */
    protected abstract void cleanUp();

    /**
     * This method is to be implemented in the child class and should create
     * whatever voters/comparators necessary and connect the nets wherever they
     * need to be connected.
     */
    protected abstract void connectNetSinks(EdifNet origNet, List<EdifNet> newNets,
            Collection<EdifPortRef> origDrivers, Collection<EdifPortRef> origSinks);

    /**
     * Connect a replicated net to it's corresponding replicated sources
     * 
     * @param newNet the replicated new
     * @param origNet the original net
     * @param domain the replication domain
     */
    protected void connectNetSources(EdifNet newNet, EdifNet origNet, int domain,
            Collection<EdifPortRef> originalDrivers) {
        for (EdifPortRef origDriver : originalDrivers) {
            EdifSingleBitPort origESB = origDriver.getSingleBitPort();
            int busIndex = origESB.bitPosition();
            EdifPort origPort = origESB.getParent();
            EdifCellInstance origInstance = origDriver.getCellInstance();
            EdifPort newPort = null;
            EdifCellInstance newInstance = null;
            if (origInstance == null) { // connected to a top level port
                List<EdifPort> originalPorts = _portReplicationMap.get(origPort);
                newPort = originalPorts.get(domain);
            } else { // connected to a port on an instance
                List<EdifCellInstance> originalInstances = _instanceReplicationMap.get(origInstance);
                newInstance = originalInstances.get(domain);
                newPort = newInstance.getCellType().getMatchingPort(origPort);
            }
            EdifSingleBitPort newESB = newPort.getSingleBitPort(busIndex);
            EdifPortRef newPortRef = new EdifPortRef(newNet, newESB, newInstance);
            newNet.addPortConnection(newPortRef);
        }
    }

    /**
     * @return a Collection of the EdifCellInstance objects that correspond to a
     * replicated EdifCellInstance object in the replicated circuit.
     */
    public Collection getReplicatedInstances() {
        Collection<EdifCellInstance> rep = new ArrayList<EdifCellInstance>();
        for (EdifCellInstance eci : _instanceReplicationMap.keySet()) {
            if (replicationFactor(eci) > 1)
                rep.add(eci);
        }
        return rep;
    }

    public Collection getReplicatedInstances(int replicationFactor) {
        Collection<EdifCellInstance> rep = new ArrayList<EdifCellInstance>();
        for (EdifCellInstance eci : _instanceReplicationMap.keySet()) {
            if (replicationFactor(eci) == replicationFactor)
                rep.add(eci);
        }
        return rep;
    }

    /**
     * @return a Collection of the EdifNet objects that correspond to a
     * replicated EdifNet object in the replicated circuit.
     */
    public Collection getReplicatedNets() {
        Collection<EdifNet> rep = new ArrayList<EdifNet>();
        for (EdifNet net : _netReplicationMap.keySet()) {
            if (replicationFactor(net) > 1)
                rep.add(net);
        }
        return rep;
    }

    /**
     * @return a Collection of the EdifPort objects that correspond to a
     * replicated EdifPort in the replicated circuit.
     */
    public Collection getReplicatedPorts() {
        Collection<EdifPort> rep = new ArrayList<EdifPort>();
        for (EdifPort port : _portReplicationMap.keySet()) {
            if (replicationFactor(port) > 1)
                rep.add(port);
        }
        return rep;
    }

    /**
     * @param filename the output file name for the domain report.
     * @throws FileNotFoundException "if the file exists but is a directory
     * rather than a regular file, does not exist but cannot be created, or
     * cannot be opened for any other reason" (See
     * {@link FileOutputStream#FileOutputStream(String)})
     */
    public void printDomainReport(String filename) throws FileNotFoundException {

        String domainReportFilename;
        if (filename == null || filename.length() == 0)
            domainReportFilename = this.getName() + "_mmr_domain_report.txt";
        else
            domainReportFilename = filename;
        PrintWriter pw = new PrintWriter(new FileOutputStream(domainReportFilename));
        System.out.println("domainreport=" + domainReportFilename);

        for (int replicationFactor : _instanceDomainMap.keySet()) {
            pw.println("Replication Factor: " + replicationFactor);
            for (int domain : _instanceDomainMap.get(replicationFactor).keySet()) {
                pw.println("\tDomain: " + domain);
                for (EdifCellInstance eci : _instanceDomainMap.get(replicationFactor).get(domain)) {
                    pw.println("\t\t" + eci.getName() + "\t" + eci.getCellType().getName());
                }
            }
        }
        pw.flush();
        pw.close();
    }

    /**
     * Create a replicated copy of the original cell with the ports, instances,
     * and nets replicated the number of times specified in the maps.
     * 
     * @param portsToReplicate map of replication factor to ports
     * @param instancesToReplicate map of replication factor to instances
     */
    protected void replicateCell(Map<Integer, List<EdifPort>> portsToReplicate,
            Map<Integer, List<EdifCellInstance>> instancesToReplicate) {

        // 1. Add ports
        addTopLevelPorts(portsToReplicate);

        // 2. Add instances
        addInstances(instancesToReplicate);

        // 3. Add nets and connect to sources and sinks
        addNets();

        // 5. Clean up
        cleanUp();

    }

    /**
     * Replicate an individual original net and add the correct number of copies
     * to the new cell. Also, connect the newly created nets to the correct
     * sources and sinks.
     * 
     * @param origNet the net to replicate
     * @return a list of replicated nets that correspond to the original net
     */
    protected List<EdifNet> replicateNet(EdifNet origNet) {
        Collection<EdifPortRef> originalDrivers = origNet.getSourcePortRefs(true, true);
        Collection<EdifPortRef> originalSinks = origNet.getSinkPortRefs(false, true);

        int factor = replicationFactor(origNet);
        List<EdifNet> replicationList = new ArrayList<EdifNet>(factor);
        if (factor == 1) {
            EdifNameable newName = getUniqueNetNameable(origNet);
            EdifNet newNet = new EdifNet(newName, this);
            try {
                addNet(newNet);
            } catch (EdifNameConflictException e) {
                // can't get here because the name is unique
                e.toRuntime();
            }
            replicationList.add(newNet);
        } else {
            for (int domain = 0; domain < factor; domain++) {
                EdifNameable newName = replicationNetNameable(origNet.getEdifNameable(), factor, domain);
                newName = getUniqueNetNameable(newName);
                EdifNet newNet = new EdifNet(newName, this);
                try {
                    addNet(newNet);
                } catch (EdifNameConflictException e) {
                    // can't get here because the name is unique
                    e.toRuntime();
                }
                replicationList.add(newNet);
            }
        }

        int i = 0;
        for (EdifNet newNet : replicationList) {
            connectNetSources(newNet, origNet, i, originalDrivers);
            i++;
        }

        // create voting/comparing and connect nets to sinks
        connectNetSinks(origNet, replicationList, originalDrivers, originalSinks);

        return replicationList;
    }

    /**
     * Get the replication factor for the (original) instance
     * 
     * @param instance
     * @return
     */
    protected int replicationFactor(EdifCellInstance instance) {
        List<EdifCellInstance> replicationList = _instanceReplicationMap.get(instance);
        if (replicationList == null)
            return 0;
        else
            return replicationList.size();
    }

    /**
     * Get the needed replication factor for the given (original) net based on
     * the replication factor(s) of its driver(s)
     */
    protected int replicationFactor(EdifNet net) {
        Collection<EdifPortRef> drivers = net.getSourcePortRefs(true, true);
        int factor = -1;
        for (EdifPortRef epr : drivers) {
            if (factor == -1)
                factor = replicationFactor(epr);
            else if (factor != replicationFactor(epr)) {
                throw new EdifRuntimeException("More than one driver with different replication status for net: " + net);
            }
        }
        if (factor == -1)
            throw new EdifRuntimeException("Net: " + net + " has no driver");
        return factor;
    }

    /**
     * Get the replication factor for the given (original) top level port
     * 
     * @param port
     * @return
     */
    protected int replicationFactor(EdifPort port) {
        List<EdifPort> replicationList = _portReplicationMap.get(port);
        if (replicationList == null)
            return 0;
        else
            return replicationList.size();
    }

    /**
     * Get the replication factor for the given EdifPortRef
     * 
     * @param epr the EdifPortRef
     * @return the replication factor
     */
    protected int replicationFactor(EdifPortRef epr) {
        EdifCellInstance instance = epr.getCellInstance();
        if (instance == null) // connected to top level port
            return replicationFactor(epr.getPort());
        else
            // connected to an instance
            return replicationFactor(instance);
    }

    /**
     * Create a new EdifNameable object that incorporates the NMR domain number
     * into the name. This method will identify renamed objects and attempt to
     * create a unique name that incorporates the domain number. Specifically,
     * this will attempt to preserve any bus numbering that is found in the
     * renamed object.
     * 
     * @param name The original name
     * @param factor the replication factor for this name
     * @param domain The domain number
     * @return The new NamedObject or the RenamedObject, as appropriate.
     */
    protected EdifNameable replicationNameable(EdifNameable name, int factor, int domain) {
        EdifNameable newName = NamedObject.createValidEdifNameable(replicationString(name.getName(), factor, domain));
        if (name instanceof RenamedObject) {
            String rename = ((RenamedObject) name).getOldName();

            // See if the old name is a "bus" name. If so,
            // create a new valid "bus" name.
            EdifBusNamingPolicy policy = BasicEdifBusNamingPolicy.EdifBusNamingPolicy(rename);
            if (policy != null) {
                String renameString = replicationString(policy.getBusBaseName(rename), factor, domain);
                renameString += policy.getBusRangeSpecifier(rename);
                return new RenamedObject(newName, renameString);
            }
            // TODO: handle single bit names as well as busses?
        }
        return new NamedObject(newName);
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
    protected EdifNameable replicationNetNameable(EdifNameable origName, int factor, int domain) {
        EdifNameable result = null;
        String name = origName.getName();
        String oldName = origName.getOldName();
        if (origName instanceof RenamedObject) {
            EdifBusNetNamingPolicy oldNamePolicy = BasicEdifBusNetNamingPolicy.EdifBusNetNamingPolicy(oldName);
            EdifBusNetNamingPolicy namePolicy = BasicEdifBusNetNamingPolicy.EdifBusNetNamingPolicy(name);
            if (oldNamePolicy == null) {
                EdifNameable newName = NamedObject.createValidEdifNameable(replicationString(name, factor, domain));
                String newOldName = replicationString(oldName, factor, domain);
                result = new RenamedObject(newName, newOldName);
            } else {
                int oldBitNumber = oldNamePolicy.getBusPosition(oldName);
                EdifNameable newName = null;
                if (namePolicy == null) {
                    newName = NamedObject.createValidEdifNameable(replicationString(name, factor, domain));
                } else {
                    int bitNumber = namePolicy.getBusPosition(name);
                    String base = namePolicy.getBusBaseName(name);
                    newName = NamedObject.createValidEdifNameable(base + replicationSuffix(factor, domain) + "_"
                            + bitNumber);
                }
                String newOldName = oldNamePolicy.getBusBaseName(oldName) + replicationSuffix(factor, domain)
                        + oldNamePolicy.generateBitSuffix(oldBitNumber);
                result = new RenamedObject(newName, newOldName);
            }
        } else {
            result = NamedObject.createValidEdifNameable(replicationString(name, factor, domain));
        }
        return result;
    }

    /**
     * Modify the given string to include the appropriate replication suffix for
     * the given replication factor and domain.
     * 
     * @param name the name to modify
     * @param factor replication factor
     * @param domain replication domain
     * @return the modified name
     */
    protected String replicationString(String name, int factor, int domain) {
        String replicationName = name;
        replicationName += replicationSuffix(factor, domain);
        return replicationName;
    }

    /**
     * Get the replication suffix for the specified factor and domain.
     * 
     * @param factor replication factor
     * @param domain replication domain
     * @return the replication suffix
     */
    protected String replicationSuffix(int factor, int domain) {
        return _replicationSuffixes.get(factor).get(domain);
    }

    /**
     * Map of original instances to replicated instances
     */
    protected Map<EdifCellInstance, List<EdifCellInstance>> _instanceReplicationMap;

    /**
     * Map of original nets to replicated nets
     */
    protected Map<EdifNet, List<EdifNet>> _netReplicationMap;

    /**
     * The original EdifCell (unreplicated)
     */
    protected EdifCell _origCell;

    /**
     * Map of original ports to replicated ports
     */
    protected Map<EdifPort, List<EdifPort>> _portReplicationMap;

    /**
     * A map from replication factors to replication suffix lists
     */
    protected Map<Integer, List<String>> _replicationSuffixes;

    /**
     * A map for keeping track of which instances (post replication) belong to
     * which domains
     */
    protected Map<Integer, Map<Integer, List<EdifCellInstance>>> _instanceDomainMap;
}
