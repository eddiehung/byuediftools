/*
 * An EdifCell object created with triple modular redundancy (TMR) from an
 * existing EdifCell object.
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.byu.ece.edif.core.BasicEdifBusNamingPolicy;
import edu.byu.ece.edif.core.EdifBusNamingPolicy;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.IntegerTypedValue;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.core.RenamedObject;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;

/**
 * This class is an EdifCell object created with triple modular redundancy (TMR)
 * from an existing EdifCell object. This class provides the mechanics for
 * constructing a circuit with TMR at one level of hierarchy. It does not
 * perform any TMR within instanced cells. This class does allow the user to
 * specify what ports and instances to triplicate. It also allows the user to
 * specify where the voters are to be inserted. It is up to the caller to
 * determine exactly what needs to be triplicated. No analysis is performed in
 * this class.
 * 
 * @author Mike Wirthlin, Keith Morgan
 * @since Created on May 20, 2005
 * @deprecated
 */
public class TMREdifCellDeprecated extends EdifCell {

    /**
     * Construct an EdifCell object that has triple modular redundancy (TMR)
     * applied to the original cell.
     * 
     * @param cell The original EdifCell object that will be used as the model
     * for the resulting triplicated circuit.
     * @param portsToTriplicate A Set of top-level ports within the original
     * EdifCell object that are to be triplicated.
     * @param edifCellInstancesToTriplicate A Collection of edif instances to be
     * triplicated. This must be determined by the caller.
     * @param edifPortRefsToCut A Collection of EdifPortRef objects that specify
     * the location of "cuts" or voter insertions.
     * @throws InvalidEdifNameException
     * @throws EdifNameConflictException
     */
    public TMREdifCellDeprecated(EdifCell cell, NMRArchitecture tmrArchitecture, Set portsToTriplicate,
            Collection edifCellInstancesToTriplicate, Collection edifPortRefsToCut) throws EdifNameConflictException,
            InvalidEdifNameException {

        // TODO: insure unique name
        super(cell.getLibrary(), cell.getName() + "TMR");
        _origCell = cell;
        _tmrArchitecture = tmrArchitecture;

        // 0. Add Cell to library
        // the second parameter is needed to insure
        // correct library ordering in the output edif
        //_origCell.getLibrary().addCell(this, false);
        //_origCell.getLibrary().addCell(this);

        // 1. Add ports
        addTopLevelPorts(portsToTriplicate);

        // 2. Triplicate cells
        //triplicateCellInstances(edifCellInstancesToTriplicate, cellTypesNotToTriplicate, 
        //		cellTypesToForceTriplicate, cellTypeInstanceLimitMap, cellInstancesToForceTriplicate);
        triplicateCellInstances(edifCellInstancesToTriplicate);

        // 3. Triplicate nets
        triplicateNets(edifPortRefsToCut);

    }

    ///////////////////////////////////////////////////////////////////
    //// 				public methods							   ////

    public Map getEdifCellInstanceMap() {
        return _edifCellInstanceMap;
    }

    public Map getEdifNetMap() {
        return _edifNetMap;
    }

    public Map getEdifPortTMRMap() {
        return _edifPortTMRMap;
    }

    public Collection getTriplicatedInstances() {
        Collection trip = new ArrayList();
        for (Iterator i = _edifCellInstanceMap.keySet().iterator(); i.hasNext();) {
            EdifCellInstance eci = (EdifCellInstance) i.next();
            EdifCellInstance trips[] = (EdifCellInstance[]) _edifCellInstanceMap.get(eci);
            if (trips.length > 1)
                trip.add(eci);
        }
        return trip;
    }

    public Collection getTriplicatedPorts() {
        Collection trip = new ArrayList();
        for (Iterator i = _edifPortTMRMap.keySet().iterator(); i.hasNext();) {
            EdifPort port = (EdifPort) i.next();
            EdifPort trips[] = (EdifPort[]) _edifPortTMRMap.get(port);
            if (trips != null && trips.length > 1)
                trip.add(port);
            if (trips == null) {
                System.err.println("Warning: no corresponding net for " + port);
            }
        }
        return trip;
    }

    public Collection getTriplicatedNets() {
        Collection trip = new ArrayList();
        for (Iterator i = _edifNetMap.keySet().iterator(); i.hasNext();) {
            EdifNet net = (EdifNet) i.next();
            EdifNet trips[] = (EdifNet[]) _edifNetMap.get(net);
            if (trips != null && trips.length > 1)
                trip.add(net);
            if (trips == null) {
                System.err.println("Warning: no corresponding net for " + net);
            }
        }
        return trip;
    }

    public Collection getVoters() {
        return _voters;
    }

    public boolean isCellInstanceTriplicated(EdifCellInstance eci) {
        EdifCellInstance[] insts = (EdifCellInstance[]) _edifCellInstanceMap.get(eci);
        if (insts.length == 3)
            return true;
        return false;
    }

    /**
     * This method determines if the given EdifPortRef is connected to an
     * instance or top-level port that is triplicated.
     * 
     * @param portRef
     * @return true if the instance or port is triplicated.
     */
    public boolean isPortRefTriplicated(EdifPortRef portRef) {

        if (portRef.isTopLevelPortRef()) {
            if (isTopLevelPortTriplicated(portRef.getPort()))
                return true;
        } else {
            if (isCellInstanceTriplicated(portRef.getCellInstance()))
                return true;
        }
        return false;
    }

    public boolean isTopLevelPortTriplicated(EdifPort port) {
        EdifPort[] ports = (EdifPort[]) _edifPortTMRMap.get(port);
        if (ports.length == 3)
            return true;
        return false;
    }

    /**
     * @param filename
     * @throws FileNotFoundException
     */
    public void printDomainReport(String filename) //, HashMap domainMap)
            throws FileNotFoundException {

        String domainReportFilename;
        if (filename == null || filename.length() == 0)
            domainReportFilename = this.getName() + "_tmr_domain_report.txt";
        else
            domainReportFilename = filename;
        PrintWriter pw = new PrintWriter(new FileOutputStream(domainReportFilename));
        System.out.println("domainreport=" + domainReportFilename);

        for (Iterator keyIterator = _domainMap.keySet().iterator(); keyIterator.hasNext();) {

            List l = (List) keyIterator.next();
            Integer domain = (Integer) _domainMap.get(l);

            for (Iterator hierIterator = l.iterator(); hierIterator.hasNext();) {
                EdifCellInstance eci = (EdifCellInstance) hierIterator.next();
                pw.print(eci.getName());
                if (!hierIterator.hasNext()) {
                    pw.print("\t" + eci.getCellType().getName());
                } else {
                    pw.print("/");
                }
            }
            pw.println("\t" + domain);
        }
        pw.close();

    }

    ///////////////////////////////////////////////////////////////////
    //// 				protected methods						   ////

    /**
     * Add top-level ports to this EdifCell. Some ports may be triplicated as
     * specified by the parameter
     * 
     * @param portsToTriplicate Collection of ports that need to be triplicated.
     * @return A Map between the original EdifPort object in the original
     * EdifCell and an array of EdifPort objects that correspond to the original
     * EdifPort object. If the port is triplicated, this array will contain
     * three ports. If the port is not triplicated, this array will contain one
     * port.
     */
    // "This array" what array?  The set of keys in the map? The Collection of values? -jFc
    protected Map addTopLevelPorts(Collection portsToTriplicate) {

        Collection edifPortList = _origCell.getPortList();

        _edifPortTMRMap = new LinkedHashMap(edifPortList.size());

        for (Iterator portIterator = edifPortList.iterator(); portIterator.hasNext();) {

            EdifPort port = null;
            EdifPort portsTMR[] = null;

            port = (EdifPort) portIterator.next();
            int tmrMax = 3;
            if (portsToTriplicate == null || !portsToTriplicate.contains(port)) {
                tmrMax = 1;
                portsTMR = new EdifPort[1];
            } else {
                tmrMax = 3;
                portsTMR = new EdifPort[3];
            }

            if (debug)
                System.out.println("Port " + port + " will have triplication of " + tmrMax);

            if (tmrMax == 1) {
                try {
                    portsTMR[0] = addPort(port.getEdifNameable(), port.getWidth(), port.getDirection());
                } catch (EdifNameConflictException e) {
                    e.toRuntime();
                }
                portsTMR[0].copyProperties(port);
                _edifPortTMRMap.put(port, portsTMR);
            } else {
                for (int i = 0; i < tmrMax; i++) {
                    String tmrPortName = port.getName() + "_TMR" + i;
                    EdifNameable newPortName = NamedObject.createValidEdifNameable(tmrPortName);
                    if (port.getEdifNameable() instanceof RenamedObject) {
                        RenamedObject ro = (RenamedObject) port.getEdifNameable();
                        String rename = ro.getOldName();
                        EdifBusNamingPolicy policy = BasicEdifBusNamingPolicy.EdifBusNamingPolicy(rename);
                        if (policy != null) {
                            String renameString = policy.getBusBaseName(rename) + "_TMR" + i
                                    + policy.getBusRangeSpecifier(rename);
                            newPortName = new RenamedObject(newPortName, renameString);
                        }
                    }
                    // Create the named object.
                    try {
                        portsTMR[i] =
                        // TODO: make sure the name is unique
                        addPort(newPortName, port.getWidth(), port.getDirection());
                    } catch (EdifNameConflictException e) {
                        e.toRuntime();
                    }
                    portsTMR[i].copyProperties(port);
                }
                _edifPortTMRMap.put(port, portsTMR);
            }
        }

        return _edifPortTMRMap;
    }

    /**
     * Creates a Map object whose key is an EdifNet object and whose value is a
     * Collection of EdifPortRef objects. This Map is created from a Collection
     * of unorganized EdifPortRef objects.
     */
    protected static Map createNetPortRefMap(Collection portRefsToCut) {
        Map netPortRefCutsetMap = new LinkedHashMap(portRefsToCut.size());
        for (Iterator i = portRefsToCut.iterator(); i.hasNext();) {
            EdifPortRef epr = (EdifPortRef) i.next();
            EdifNet net = epr.getNet();
            Collection c = (Collection) netPortRefCutsetMap.get(net);
            if (c == null) {
                c = new ArrayList();
                c.add(epr);
                netPortRefCutsetMap.put(net, c);
            }
            c.add(epr);
        }
        return netPortRefCutsetMap;
    }

    /**
     * @param sinkPortRef
     * @param newNets
     */
    protected void oneNetToOneSink(EdifPortRef sinkPortRef, EdifNet[] newNets) {
        EdifPort port = sinkPortRef.getPort();
        EdifPort sinkPorts[] = (EdifPort[]) _edifPortTMRMap.get(port);
        EdifCellInstance eci = sinkPortRef.getCellInstance();
        EdifCellInstance sinkInstances[] = (EdifCellInstance[]) _edifCellInstanceMap.get(eci);

        if (debug)
            System.out.println("*** pass throgh 1 net, i.e. drive 1 sink with 1 new net");
        if (sinkPortRef.isTopLevelPortRef())
            newNets[0].addPortConnection(null, sinkPorts[0], sinkPortRef.getBusMember());
        else {
            newNets[0].addPortConnection(sinkInstances[0], port, sinkPortRef.getBusMember());
        }

    }

    /**
     * @param sinkPortRef
     * @param newNets
     */
    protected void oneNetToThreeSinks(EdifPortRef sinkPortRef, EdifNet[] newNets) {
        EdifPort port = sinkPortRef.getPort();
        EdifPort sinkPorts[] = (EdifPort[]) _edifPortTMRMap.get(port);
        EdifCellInstance eci = sinkPortRef.getCellInstance();
        EdifCellInstance sinkInstances[] = (EdifCellInstance[]) _edifCellInstanceMap.get(eci);

        if (debug)
            System.out.println("*** Single net and fan out to 3 sinks");
        for (int i = 0; i < 3; i++) {
            if (sinkPortRef.isTopLevelPortRef())
                newNets[0].addPortConnection(null, sinkPorts[i], sinkPortRef.getBusMember());
            else
                newNets[0].addPortConnection(sinkInstances[i], port, sinkPortRef.getBusMember());
        }
    }

    /**
     * @param sinkPortRef
     * @param voterNets
     */
    protected void oneVoterToOneSink(EdifPortRef sinkPortRef, EdifNet[] voterNets) {
        EdifPort port = sinkPortRef.getPort();
        EdifPort sinkPorts[] = (EdifPort[]) _edifPortTMRMap.get(port);
        EdifCellInstance eci = sinkPortRef.getCellInstance();
        EdifCellInstance sinkInstances[] = (EdifCellInstance[]) _edifCellInstanceMap.get(eci);

        if (debug)
            System.out.println("*** get output from voter 0 of 3 and drive 1 sink");
        if (sinkPortRef.isTopLevelPortRef())
            voterNets[0].addPortConnection(null, sinkPorts[0], sinkPortRef.getBusMember());
        else
            voterNets[0].addPortConnection(sinkInstances[0], port, sinkPortRef.getBusMember());

    }

    /**
     * @param sinkPortRef
     * @param newNets
     */
    protected void threeNetsToThreeSinks(EdifPortRef sinkPortRef, EdifNet[] newNets) {
        EdifPort port = sinkPortRef.getPort();
        EdifPort sinkPorts[] = (EdifPort[]) _edifPortTMRMap.get(port);
        EdifCellInstance eci = sinkPortRef.getCellInstance();
        EdifCellInstance sinkInstances[] = (EdifCellInstance[]) _edifCellInstanceMap.get(eci);

        if (debug)
            System.out.println("*** pass three outputs from three new nets to sinks (pass through)");
        for (int i = 0; i < 3; i++) {
            if (sinkPortRef.isTopLevelPortRef())
                newNets[i].addPortConnection(null, sinkPorts[i], sinkPortRef.getBusMember());
            else
                newNets[i].addPortConnection(sinkInstances[i], port, sinkPortRef.getBusMember());
        }
    }

    /**
     * @param sinkPortRef
     * @param voterNets
     */
    protected void threeVotersToThreeSinks(EdifPortRef sinkPortRef, EdifNet[] voterNets) {
        EdifPort port = sinkPortRef.getPort();
        EdifPort sinkPorts[] = (EdifPort[]) _edifPortTMRMap.get(port);
        EdifCellInstance eci = sinkPortRef.getCellInstance();
        EdifCellInstance sinkInstances[] = (EdifCellInstance[]) _edifCellInstanceMap.get(eci);

        if (debug)
            System.out.println("*** get 3 outputs from voters and drive 3 sinks");
        for (int i = 0; i < 3; i++) {
            if (sinkPortRef.isTopLevelPortRef())
                voterNets[i].addPortConnection(null, sinkPorts[i], sinkPortRef.getBusMember());
            else
                voterNets[i].addPortConnection(sinkInstances[i], port, sinkPortRef.getBusMember());
        }

    }

    /**
     * This method will triplicate the cells specified while avoiding those Cell
     * types that are not allowed. This method will not do anything if no cells
     * are specified for triplication.
     * 
     * @param edifCellInstancesToTriplicate Instances within the EdifCell to
     * triplicate.
     * @return the _edifCellInstanceMap associated with this EdifCell
     */
    protected Map triplicateCellInstances(Collection edifCellInstancesToTriplicate) {

        // I'm removing this block of code, because this method does more
        // than triplication... it is also adding a single cell instance
        // to *this* cell if it isn't supposed to be triplicated. (KSM)
        //		if (edifCellInstancesToTriplicate == null)
        //			return null;

        if (debug)
            System.out.println("size of edifCellInstancesToTriplicate is: " + edifCellInstancesToTriplicate.size());
        //if (debug) System.out.println ("cell types not to triplicate: " + cellTypesNotToTriplicate);
        Collection edifCellInstanceList = _origCell.getSubCellList();
        _edifCellInstanceMap = new LinkedHashMap(edifCellInstanceList.size());
        _domainMap = new LinkedHashMap(edifCellInstanceList.size());
        //Map cellTypeTMRInstanceLimitMap = createCellTypeTMRInstanceLimitMap(cellTypeInstanceLimitList);		

        for (Iterator cellInstanceIterator = edifCellInstanceList.iterator(); cellInstanceIterator.hasNext();) {

            EdifCellInstance cellInstance = (EdifCellInstance) cellInstanceIterator.next();
            EdifCell cellRef = cellInstance.getCellType();
            EdifCellInstance[] cellInstancesTMR = null;

            // Determine whether or not this CellInstance should be triplicated
            int tmrMax = 1;
            if (edifCellInstancesToTriplicate != null && edifCellInstancesToTriplicate.contains(cellInstance)) {
                tmrMax = 3;
            }
            cellInstancesTMR = new EdifCellInstance[tmrMax];

            if (debug)
                System.out.println("Determined a triplication of " + tmrMax + " for cell: " + cellInstance);

            // Create three copies of instance if the cell is to be triplicated,
            // otherwise just add one instance.
            for (int i = 0; i < tmrMax; i++) {
                // TODO: insure unique name
                // TODO: don't use TMR name if cell is not being triplicated // This has been done, right? -jFc
                try {
                    if (tmrMax == 1)
                        cellInstancesTMR[i] = new EdifCellInstance(cellInstance.getName(), this, cellRef);
                    else
                        cellInstancesTMR[i] = new EdifCellInstance(cellInstance.getName() + "_instanceTMR_" + i, this,
                                cellRef);
                } catch (InvalidEdifNameException e) {
                    e.toRuntime();
                }
                // copy properties
                cellInstancesTMR[i].copyProperties(cellInstance);
                // add cell
                try {
                    addSubCell(cellInstancesTMR[i]);
                } catch (EdifNameConflictException e) {
                    e.toRuntime();
                }
            }

            // ??? What does this section of code do? (KSM)
            /*
             * It looks like it adds a property to a cell instance (if it is
             * triplicated) which says which TMR_DOMAIN the cell instance
             * resides in.
             * 
             * 
             * It looks like it also is adding each cell instance to the domain
             * map which Eric creates.
             */
            for (int i = 0; i < tmrMax; i++) {
                // Flag? for adding property to Edif?
                if (tmrMax == 3) {
                    IntegerTypedValue domain = new IntegerTypedValue(i);
                    cellInstancesTMR[i].addProperty(new Property("TMR_DOMAIN", domain));
                }
                // Is this necessary for a flattened design?
                /*
                 * No this probably isn't necessary, but it works because
                 * getHierarchicalPrimitiveList returns the instance itself as
                 * part of the list.
                 */
                Collection prims = cellInstancesTMR[i].getHierarchicalPrimitiveList();
                for (Iterator j = prims.iterator(); j.hasNext();) {
                    List l = (List) j.next();
                    _domainMap.put(l, new Integer(i));
                }
            }

            _edifCellInstanceMap.put(cellInstance, cellInstancesTMR);
        }

        return _edifCellInstanceMap;
    }

    /**
     * Triplicate the nets as needed.
     * 
     * @param edifPortRefsToCut A Collection of EdifPortRef objects which are to
     * be "cut." That is, a voter will be placed between the EdifNet and the
     * EdifPortRef.
     */
    protected void triplicateNets(
    //Map netPortRefCutsetMap, //key=net, value=Collection of EdifPortRefs needing voting 
            Collection edifPortRefsToCut) {

        if (edifPortRefsToCut == null)
            edifPortRefsToCut = new ArrayList(0);
        _voters = new ArrayList();
        _edifNetMap = new LinkedHashMap(_origCell.getNetList().size());

        Map netPortRefCutsetMap = createNetPortRefMap(edifPortRefsToCut);

        /*
         * Iterate over every net in the cell. For each net, create either a
         * single net or a triplicated version of the net.
         */
        for (Iterator netIterator = _origCell.netListIterator(); netIterator.hasNext();) {

            //////////////////////////////////////////////////////////////////////
            //
            // Here are the possible cases for each net. These rules will
            // govern how we hook up the sinks to the drivers.
            //
            // Case 1: if Driver(s) is not triplicated
            //       No voters created in this case. Hook up to single driver wire.
            //   A. Triple sinks: one driver to 3 sinks
            //   B. Single sink: pass driver to sink
            // Case 2: Driver is triplicated (3 wires)
            //   A. Driver is cut (force voter on all sinks)
            //      a. For non-triplicated sinks, use 1 voter output
            //      b. for triplicated sinks, use all three voters
            //   B. Sink connection cut (Driver NOT cut)
            //      a. For non-triplicated sinks, use a 1 voter output
            //      b. for triplicated sinks, use all three voters
            //   C. Sink connection NOT cut and driver NOT cut (default fall through)
            //      a. different triple sink cut && option true
            //         i. triple sink: use voters for inputs
            //         ii. single sink: choose 1 voter
            //      b. different single sink cut && option true
            //         i. triple sink: pass through
            //         ii. single sink: choose 1 voter
            //      c. left over - (options off or no other sinks)
            //         i. triple sink: pass through
            //         ii. single sink & downscale: voter signal
            //         iii. single sink & !downscale : pass one wire
            //
            //////////////////////////////////////////////////////////////////////

            EdifNet net = (EdifNet) netIterator.next();
            if (debug) {
                System.out.println("----------");
                System.out.println("Net " + net.getName() + " (" + net.getOldName() + ")");
            }

            //////////////////////////////////////////////////////////////////////
            //
            // Step 1. Analyze the net to determine how to recreate it in 
            //         the TMR circuit. Determine the "Drivers" of the net
            // 
            //
            //////////////////////////////////////////////////////////////////////
            //			Collection originalNetDrivers = net.getNetDrivers();
            Collection originalNetDrivers = net.getSourcePortRefs(true, true);
            boolean anySinksTriplicated = false;
            boolean allSinksTriplicated = true;
            boolean aTripleSinkIsCut = false;
            boolean aSingleSinkIsCut = false;
            boolean driverIsCut = false;

            for (EdifPortRef portRef : net.getConnectedPortRefs()) {

                //EdifPort port = portRef.getPort();
                //EdifCellInstance eci = portRef.getCellInstance();

                if (portRef.isDriverPortRef() && edifPortRefsToCut.contains(portRef)) {
                    driverIsCut = true;
                    if (debug)
                        System.out.println("Driver of net is cut");
                }

                // Check to see if ANY of the sinks are triplicated
                if (!portRef.isDriverPortRef()) {
                    if (isPortRefTriplicated(portRef))
                        anySinksTriplicated = true;
                    if (!isPortRefTriplicated(portRef))
                        allSinksTriplicated = false;

                    // Check for voter needs
                    if (edifPortRefsToCut.contains(portRef)) {
                        if (isPortRefTriplicated(portRef))
                            aTripleSinkIsCut = true;
                        else
                            aSingleSinkIsCut = true;
                    }
                }

            }

            //////////////////////////////////////////////////////////////////////
            //
            // Determine how many Nets to create and make sure that all drivers
            // have the same triplication status.
            // Checks the actual CellInstances driving the Net, not the status
            // of the PortRef itself (if it is in the portCutSet). Thus a PortRef
            // that is tagged to be cut will not be if the Net drivers are not
            // triplicated.
            //
            //////////////////////////////////////////////////////////////////////
            int numberOfNetsToCreate = 0;
            for (Iterator i = originalNetDrivers.iterator(); i.hasNext();) {
                EdifPortRef driver = (EdifPortRef) i.next();
                if (isPortRefTriplicated(driver)) {
                    if (numberOfNetsToCreate == 0)
                        numberOfNetsToCreate = 3;
                    else if (numberOfNetsToCreate == 1) // previous driver was not triplicated
                        throw new EdifRuntimeException("More than one driver with different triplication status: "
                                + "\n\tNet: " + net + "\n\tDrivers: " + originalNetDrivers);
                } else {
                    if (numberOfNetsToCreate == 0)
                        numberOfNetsToCreate = 1;
                    else if (numberOfNetsToCreate == 3) // previous driver was triplicated
                        throw new EdifRuntimeException("More than one driver with different triplication status: "
                                + "\n\tNet: " + net + "\n\tDrivers: " + originalNetDrivers);
                }
            }

            if (debug) {
                System.out.println("Cuts (driver,triple,single)=(" + driverIsCut + "," + aTripleSinkIsCut + ","
                        + aSingleSinkIsCut + ")");
                System.out.println("all sinks TMR=" + allSinksTriplicated + " some sinks TMR=" + anySinksTriplicated);
                System.out.println("Number of net copies=" + numberOfNetsToCreate);
                System.out.println("This net has these eprs " + net.getConnectedPortRefs());
            }

            //////////////////////////////////////////////////////////////////////
            //
            // Create the new Net(s) and connect the new Net(s) to 
            // all "drivers" of the net
            //
            //////////////////////////////////////////////////////////////////////			
            EdifNet newNets[] = new EdifNet[numberOfNetsToCreate];
            for (int i = 0; i < numberOfNetsToCreate; i++) {
                EdifNet newNet = null;
                if (numberOfNetsToCreate == 1)
                    newNet = new EdifNet(net.getEdifNameable());
                else {
                    EdifNameable oldNameable = net.getEdifNameable();
                    if (oldNameable instanceof RenamedObject) {
                        EdifNameable newNameable = null;
                        try {
                            newNameable = new RenamedObject(net.getName() + "_TMR" + i, net.getOldName() + "_TMR" + i);
                        } catch (InvalidEdifNameException e) {
                            e.printStackTrace();
                        }
                        newNet = new EdifNet(newNameable);
                    } else
                        try {
                            newNet = new EdifNet(net.getName() + "_TMR" + i);
                        } catch (InvalidEdifNameException e) {
                            e.toRuntime();
                        }
                }
                newNets[i] = newNet;
                newNet.copyProperties(net);
                try {
                    addNet(newNet);
                } catch (EdifNameConflictException e) {
                    e.toRuntime();
                }
                _edifNetMap.put(net, newNets);

                if (debug)
                    System.out.println("Creating net " + i + " called " + newNet.getName());

                /*
                 * connect to the driver port refs If the original driver is a
                 * top level port then we connect to an input port of the top
                 * level cell If the original driver was a cell, then we connect
                 * to an output port of the cell instance.
                 */
                for (Iterator j = originalNetDrivers.iterator(); j.hasNext();) {
                    EdifPortRef origDriver = (EdifPortRef) j.next();
                    EdifCellInstance[] newInstances = (EdifCellInstance[]) _edifCellInstanceMap.get(origDriver
                            .getCellInstance());
                    //System.out.print("jth original net driver: driver Ports: " + newDriverPorts + " cell Instances: " + newInstances);
                    if (origDriver.isTopLevelPortRef()) {
                        EdifPort[] newDriverPorts = (EdifPort[]) _edifPortTMRMap.get(origDriver.getPort());
                        newNet.addPortConnection(null, newDriverPorts[i], origDriver.getBusMember());
                    } else {
                        EdifCellInstance inst = newInstances[i];
                        EdifCell type = inst.getCellType();
                        EdifPort port = type.getMatchingPort(origDriver.getPort());
                        if (port == null)
                            throw new EdifRuntimeException("Can't find port");
                        newNet.addPortConnection(inst/* ,origDriver.getPort() */, port, origDriver.getBusMember());
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////
            //
            // Create the voters for the net. 
            //
            // The rules for creating voters are as follows:
            // 1. There must be 3 nets (i.e. 3 drivers), and
            // 2. a. The driver is cut, or
            //    b. There is a sink that is cut, or
            //      i. Sink is not triplicated: use 1 voter
            //      ii. Sink is triplicated: use 3 voters
            //    c. None of the sinks are cut and not all the sinks are triplicated
            //////////////////////////////////////////////////////////////////////			
            int numberOfVoters = 0;
            if (numberOfNetsToCreate == 3)
                if (driverIsCut)
                    if (anySinksTriplicated)
                        numberOfVoters = 3;
                    else
                        numberOfVoters = 1;
                else if (aTripleSinkIsCut)
                    numberOfVoters = 3;
                else if (aSingleSinkIsCut)
                    numberOfVoters = 1;
                else if (!allSinksTriplicated && _useVoterForThreeWireToOneWireDownscale)
                    numberOfVoters = 1;
                else
                    numberOfVoters = 0; // just to make sure

            if (debug)
                System.out.println("Creating " + numberOfVoters + " voters");
            EdifNet voterNets[] = new EdifNet[numberOfVoters];
            for (int i = 0; i < numberOfVoters; i++) {
                EdifNet voterOutputNet = null;
                try {
                    voterOutputNet = new EdifNet(net.getName() + "_VOTER" + i);
                    voterNets[i] = voterOutputNet;
                    voterOutputNet.copyProperties(net); // TODO: does this make sense? 
                    addNet(voterOutputNet);
                } catch (InvalidEdifNameException e) {
                    e.toRuntime();
                } catch (EdifNameConflictException e1) {
                    e1.toRuntime();
                }

                String voterName = voterOutputNet.getName();
                EdifCellInstance voterInstance = _tmrArchitecture.createVoter(this, voterName, newNets, voterOutputNet);
                /*
                 * createVoter(newNets, voterOutputNet);
                 */
                _voters.add(voterInstance);
            }

            //////////////////////////////////////////////////////////////////////
            //
            // Hook up the "sinks" to the new net(s) including connections
            // to the voters
            //
            //////////////////////////////////////////////////////////////////////			

            // sink triplicated
            //    One driver
            //      needs cut: Won't happen
            //      no cut: fan out
            //    3 drivers
            //      needs cut: get from voters
            //      no cut: 3 net pass through
            // sink not triplicated
            //    One driver
            //      needs cut: Won't happen
            //      no cut: 1 net pass through
            //    3 drivers
            //      needs cut: get from voter 0
            //      no cut: get from voter 0
            // Iterate over all non-drivers (sinks)
            Collection portRefsToCut = (Collection) netPortRefCutsetMap.get(net);
            if (debug) {
                System.out.println("portRefsToCut " + portRefsToCut);
                if (portRefsToCut != null) {
                    for (Iterator portRefsToCutIterator = portRefsToCut.iterator(); portRefsToCutIterator.hasNext();) {
                        EdifPortRef cutPortRef = (EdifPortRef) portRefsToCutIterator.next();
                        if (debug)
                            System.out.println("\t cut=" + cutPortRef);
                    }
                }
            }
            for (Iterator portRefIterator = net.getConnectedPortRefs().iterator(); portRefIterator.hasNext();) {
                EdifPortRef sinkPortRef = (EdifPortRef) portRefIterator.next();
                if (sinkPortRef.isDriverPortRef())
                    continue; // we already hooked up the drivers. Skip them here

                //EdifPort port = sinkPortRef.getPort();
                //EdifPort sinkPorts[] = (EdifPort[]) _edifPortTMRMap.get(port);
                //EdifCellInstance eci = sinkPortRef.getCellInstance();
                //EdifCellInstance sinkInstances[] = 
                //	(EdifCellInstance[]) _edifCellInstanceMap.get(eci);

                if (debug)
                    System.out.println("Examining portRef " + sinkPortRef);

                if (numberOfNetsToCreate == 1) { // CASE 1: single driver
                    if (isPortRefTriplicated(sinkPortRef)) {
                        // Case 1.A: single driver, 3 sinks
                        oneNetToThreeSinks(sinkPortRef, newNets);
                    } else {
                        // Case 1.B: single driver, 1 sinks
                        // pass through 1 net, i.e. drive 1 sink with 1 new net
                        oneNetToOneSink(sinkPortRef, newNets);
                    }
                } else { // CASE 2: 3 drivers
                    if (driverIsCut || // Case 2.A 3 drivers, driver is cut
                            (portRefsToCut != null && portRefsToCut.contains(sinkPortRef))) { // Case 2.B 3 drivers, sink is cut
                        if (isPortRefTriplicated(sinkPortRef)) { // Case 2.A|B.b - 3 drivers, driver is cut, triplicated sink voter
                            threeVotersToThreeSinks(sinkPortRef, voterNets);
                        } else { // Case 2.A|B.a - 3 drivers, driver is cut, single sink voter
                            oneVoterToOneSink(sinkPortRef, voterNets);
                        }
                    } else {
                        // Case 2.C - 3 drivers, neither driver or sink is cut
                        if (aTripleSinkIsCut && _useVoterOutputsForNonCutInputs) {
                            // Case 2.C.a - 3 drivers, neither driver or sink is cut, other triple is cut
                            if (isPortRefTriplicated(sinkPortRef)) {
                                // Case 2.C.a.i - 3 drivers, neither driver or sink is cut, other triple is cut, triple sink
                                threeVotersToThreeSinks(sinkPortRef, voterNets);
                            } else {
                                // Case 2.C.a.ii - 3 drivers, neither driver or sink is cut, other triple is cut, single sink
                                oneVoterToOneSink(sinkPortRef, voterNets);
                            }
                        } else if (aSingleSinkIsCut && _useVoterOutputsForNonCutInputs) {
                            // Case 2.C.b - 3 drivers, neither driver or sink is cut, other single is cut
                            if (isPortRefTriplicated(sinkPortRef)) {
                                // Case 2.C.b.i - 3 drivers, neither driver or sink is cut, other single is cut, triple sink
                                // pass three outputs from three new nets to sinks (pass through)
                                threeNetsToThreeSinks(sinkPortRef, newNets);
                            } else {
                                // Case 2.C.b.ii - 3 drivers, neither driver or sink is cut, other single is cut, single sink
                                oneVoterToOneSink(sinkPortRef, voterNets);
                            }
                        } else {
                            // Case 2.C.c - 3 drivers, neither driver or sink is cut, no other sinks are cut
                            if (isPortRefTriplicated(sinkPortRef)) {
                                // Case 2.C.c.i - 3 drivers, neither driver or sink is cut, no other sinks are cut, 3 sinks
                                threeNetsToThreeSinks(sinkPortRef, newNets);
                            } else if (_useVoterForThreeWireToOneWireDownscale) {
                                // Case 2.C.c.ii - 3 drivers, neither driver or sink is cut, no other sinks are cut, 1 sinks
                                //                  && option for voting on downscale
                                oneVoterToOneSink(sinkPortRef, voterNets);
                            } else {
                                // Case 2.C.c.ii - 3 drivers, neither driver or sink is cut, no other sinks are cut, 1 sinks
                                //                  && NOT option for voting on downscale
                                oneNetToOneSink(sinkPortRef, newNets);
                            }
                        }
                    }
                }
            }

            // debug to check to see which of the newly created nets got hooked up
            if (debug) {
                for (int i = 0; i < newNets.length; i++) {
                    System.out.println("newNet " + i + " has the following eprs " + newNets[i].getConnectedPortRefs());
                }
                // debug check to see which of the voter nets got hooked up
                for (int i = 0; i < voterNets.length; i++) {
                    System.out.println("voter " + i + " has the following eprs " + voterNets[i].getConnectedPortRefs());
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////
    //// 				protected variables						   ////

    /**
     * If set to "true", debugging print statements will be activated.
     */
    protected boolean debug = false;

    /**
     * A Map between each EdifCellInstance in this EdifCell and an Integer
     * object indicating the domain of the instance.
     */
    protected Map _domainMap;

    /**
     * A Map between the original EdifCellInstance and an array of
     * EdifCellInstace objects in the new cell. If the instance is triplicated,
     * this array will be of length three. If the instance is not triplicated,
     * this array will be of length one.
     */
    protected Map _edifCellInstanceMap;

    /**
     * A Map between the original EdifPort and an array of EdifPort objects in
     * the new cell. If the instance is triplicated, this array will be of
     * length three. If the instance is not triplicated, this array will be of
     * length one.
     */
    protected Map _edifPortTMRMap;

    protected Map _edifNetMap;

    /**
     * A reference to the original non-TMRed cell.
     */
    protected EdifCell _origCell;

    /**
     * The TMR architecture style to use when applying TMR.
     */
    protected NMRArchitecture _tmrArchitecture;

    /**
     * This flag specifies which signal is attached to "non cut inputs". The
     * condition of interest occurs when there is a triplicated net and the net
     * is "cut" by a voter. In this case, a voter will be inserted on the
     * signal. However, some inputs of this net will not be "cut" (i.e. they do
     * not necessarily need the voted input). This flag specifies which signal
     * is passed to these inputs. If this flag is true, non-cut inputs will
     * receive the voted signal. If this flag is false, these inputs will
     * receive the non-voted signal.
     */
    protected boolean _useVoterOutputsForNonCutInputs = true;

    /**
     * All voters added to this cell.
     */
    protected Collection _voters;

    /**
     * This is a reference to the "voter cell". This reference is cached since
     * we will be adding lots of voters.
     */
    protected EdifCell _voterCell = null;

    /**
     * This flag specifies the wire object to use when a triplicated wire is
     * downscaled to a single wire. If this flag is true, the single wire that
     * is used by the rest of the circuit will be a vote of the three TMR
     * signals. If this flag is false, one of the 3 wires will be used.
     */
    protected boolean _useVoterForThreeWireToOneWireDownscale = true;
}
