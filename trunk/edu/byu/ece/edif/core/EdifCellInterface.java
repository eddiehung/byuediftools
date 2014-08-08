/*
 * Represents the port interface of an EdifCell object.
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
package edu.byu.ece.edif.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

//////////////////////////////////////////////////////////////////////////
////EdifCellInterface
/**
 * Represents the port interface of an {@link EdifCell} object. The port
 * interface is a list of EdifPort objects.
 * 
 * @version $Id:EdifCellInterface.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class EdifCellInterface implements EdifOut, Trimable, Serializable {

    /**
     * Construct an Empty EdifCellInterface Object with the passed-in EdifCell
     * object as its parent.
     * 
     * @param parent The parent EdifCell Object, or owner if this
     * EdifCellInterface
     */
    public EdifCellInterface(EdifCell parent) {
        _parent = parent;
    }

    /**
     * Add a new port to the EdifCellInterface. Return null if the port wasn't
     * added (most likely due to a name clash).
     * 
     * @param name The name of the new port to add
     * @param width The width of the new port to add
     * @param direction The direction of the new port to add
     * @return The port that was added, if it wasn't, null
     * @throws EdifNameConflictException
     */
    public EdifPort addPort(EdifNameable name, int width, int direction) throws EdifNameConflictException {
        EdifPort port = addPort(name,width,direction,false);
        return port;
    }

    public EdifPort addPort(EdifNameable name, int width, int direction, boolean isArray) throws EdifNameConflictException {
        EdifPort port = new EdifPort(this, name, width, direction, isArray);
        _portList.addElement(port);
        return port;
    }

    /**
     * Add a new port to the EdifCellInterface. Return null if the port wasn't
     * added (most likely due to a name clash).
     * 
     * @param name The name of the new port to add
     * @param width The width of the new port to add
     * @param direction The direction of the new port to add
     * @return The port that was added, if it wasn't, null
     * @throws InvalidEdifNameException
     * @throws EdifNameConflictException
     */
    public EdifPort addPort(String name, int width, int direction) throws InvalidEdifNameException,
            EdifNameConflictException {
        return addPort(new NamedObject(name), width, direction);
    }

    public EdifPort addPortUniqueName(EdifNameable name, int width, int direction) {
        EdifPort port = new EdifPort(this, _portList.getUniqueNameable(name), width, direction);
        try {
            _portList.addElement(port);
        } catch (EdifNameConflictException e) {
            // will never get here because
            // a unique name has already been
            // created
        }
        return port;
    }

    public EdifPort addPortUniqueName(String name, int width, int direction) throws InvalidEdifNameException {
        return addPortUniqueName(new NamedObject(name), width, direction);
    }

    /**
     * Check to see if the passed-in EdifPort is already contained within this
     * EdifCellInterface. Equality is done using <code>==</code>.
     * 
     * @param contain The EdifPort to compare against the EdifPort Objects
     * contained within this EdifCellInterface Object to check to see if it
     * already exists in this EdifCellInterface
     * @return True if the passed-in EdifPort is already contained within this
     * EdifCellInterface Object
     */
    public boolean contains(EdifPort contain) {
        for (EdifPort port : _portList) {
            if (port == contain)
                return true;
        }
        return false;
    }

    /**
     * Check to see if the EdifSingleBitPort is contained within the interface.
     * This method operates by calling EdifCellInterface.contains on the parent
     * of the single-bit port.
     * 
     * @param esbp
     * @return true if the passed-in EdifSingleBitPort is owned by a EdifPort
     * object contained in this interface.
     */
    public boolean contains(EdifSingleBitPort esbp) {
        return contains(esbp.getParent());
    }

    /**
     * Return a deep copy of this EdifCellInterface Object. The parent cell of
     * the interface is specified by the EdifCell parent parameter. Note that
     * the parent cell will explicitly need to set the interface as the active
     * interface after the copy has occurred. All copies are done using the
     * 'new' statement, except String objects.
     * 
     * @param parent The EdifCell that will be the parent to this copy
     * @return The copied EdifCellInterface Object
     */
    public EdifCellInterface copy(EdifCell parent) {
        EdifCellInterface eci = new EdifCellInterface(parent);

        for (EdifPort port : _portList) {
            EdifPort newPort = null;
            try {
                newPort = eci.addPort(port.getEdifNameable(), port.getWidth(), port.getDirection());
            } catch (EdifNameConflictException e) {
                // will never get here
            }
            newPort.copyProperties(port);
        }
        return eci;
    }

    /**
     * Remove a port from this EdifCellInterface. TODO: is this what we want to
     * do?
     * 
     * @param port The port to remove
     */
    //package scope
    public void deletePort(EdifPort port) {
        _portList.remove(port);
    }

    /**
     * Determine whether the given EdifCell object matches the interface of the
     * current EdifCell object. To make this test, each port is compared for
     * equality. Two EdifCell port interfaces are the same if each of the
     * following conditions are met:
     * <ul>
     * <li> The number of ports in each interface is the same.
     * <li> A matching port in the passed in EdifCell is found for each port in
     * this EdifCell (see {@link EdifPort#equals(EdifCellInterface)}).
     * </ul>
     * <br>
     * The internal structure of the two EdifCell objects are not checked for
     * equality.
     * 
     * @param cell EdifCell object to compare against the current EdifCell
     * object
     * @return boolean indicating whether the two EdifCell objects have
     * equivalent port interfaces
     */
    public boolean equals(EdifCellInterface cell) {

        Collection<EdifPort> cellPorts = cell.getPortList();
        Collection<EdifPort> thisPorts = getPortList();

        // Several simple checks to report false early 
        if (cellPorts == null && thisPorts != null)
            return false;
        else if (cellPorts != null && thisPorts == null)
            return false;
        else if ((cellPorts.size() != thisPorts.size() || !portCountEquals(cell)))
            return false;

        // Iterate over each port in the given cell. See if there is a matching
        // port in this cell. If not, return false.
        for (EdifPort port : cellPorts) {
            if (getMatchingPort(port) == null)
                return false;
        }
        return true;
    }

    /**
     * This method will search the passed in EdifCellInterface object for
     * single-bit EdifPort objects that have a name that begins with the name
     * provided by the passed in busPort. If there is a match, a collection of
     * the matching single bit EdifPort objects will be returned. A match will
     * only occur when the following are true:
     * <ol>
     * <li> The prefix of each single bit port is the same as the multi-bit port
     * <li> Each single bit port can be identified as a valid "bus signal"
     * <li> The number of single-bit ports is the same as the number of bits in
     * the multi-bit port
     * </ol>
     * 
     * @param busPort A EdifPort object that is multiple bits (i.e. a bus). The
     * full name of this Port will be used to find matching single-bit EdifPort
     * objects.
     * @param cellIf The EdifCellInterface object that will be searched.
     * @return A Collection of single-bit EdifPort objects that correspond to
     * the given multi-bit EdifPort. A null will be returned if a complete match
     * did not occur.
     */
    public static Collection<EdifPort> findMatchingSingleBitPorts(EdifPort busPort, EdifCellInterface cellIf) {

        Collection<EdifPort> matchingPorts = new ArrayList<EdifPort>(busPort.getWidth());
        String busName = busPort.getName();

        // iterate over all single-bit ports and check for name matches
        for (EdifPort port : cellIf.getPortList()) {
            // skip over multiple-bit ports
            if (port.getWidth() > 1)
                continue;
            String portName = port.getName();
            if (portName.startsWith(busName))
                matchingPorts.add(port);
        }

        if (matchingPorts.size() < busPort.getWidth())
            return null;

        // Check each EdifPort object and remove those that do not follow a
        // known naming pattern for a bit in a bus 

        if (matchingPorts.size() == busPort.getWidth())
            return matchingPorts;

        return null;
    }

    /**
     * Return the parent EdifCell that owns this object.
     * 
     * @return An EdifCell Object that is the parent to this EdifCellInterface
     */
    public EdifCell getEdifCell() {
        return _parent;
    }

    /**
     * Return a Collection of only inout ports.
     * 
     * @return A Collection of InOut ports
     */
    public Collection<EdifPort> getInOutPorts() {
        ArrayList<EdifPort> list = new ArrayList<EdifPort>(getPortList().size());
        for (EdifPort port : getPortList()) {
            if (port.isInOut())
                list.add(port);
        }
        return list;
    }

    /**
     * Return a Collection of only input ports.
     * 
     * @return A Collection of Input ports only
     */
    public Collection<EdifPort> getInputOnlyPorts() {
        ArrayList<EdifPort> list = new ArrayList<EdifPort>(getPortList().size());
        for (EdifPort port : getPortList()) {
            if (port.isInputOnly())
                list.add(port);
        }
        return list;
    }

    /**
     * Return a Collection of input and inout ports.
     * 
     * @return A Collection of Input and InOut ports
     */
    public Collection<EdifPort> getInputPorts() {
        ArrayList<EdifPort> list = new ArrayList<EdifPort>(getPortList().size());
        for (EdifPort port : getPortList()) {
            if (port.isInput())
                list.add(port);
        }
        return list;
    }

    /**
     * Return a port contained within this EdifCellInterface that matches the
     * passed-in port by calling {@link EdifPort#equals(EdifPort)}.
     * 
     * @param port The EdifPort Object to match within this interface
     */
    public EdifPort getMatchingPort(EdifPort port) {
        for (EdifPort p : _portList) {
            if (p.equals(port))
                return p;
        }
        return null;
    }

    /**
     * Return a Collection of only output ports.
     * 
     * @return A Collection of Output ports only.
     */
    public Collection<EdifPort> getOutputOnlyPorts() {
        ArrayList<EdifPort> list = new ArrayList<EdifPort>(getPortList().size());
        for (EdifPort port : getPortList()) {
            if (port.isOutputOnly())
                list.add(port);
        }
        return list;
    }

    /**
     * Return a Collection of output and inout ports.
     * 
     * @return A Collection of Output and InOut ports.
     */
    public Collection<EdifPort> getOutputPorts() {
        Collection<EdifPort> topLevelPorts = getPortList();
        ArrayList<EdifPort> list = new ArrayList<EdifPort>(topLevelPorts.size());
        for (EdifPort port : topLevelPorts) {
            if (port.isOutput())
                list.add(port);
        }
        return list;
    }

    /**
     * Return the EdifPort object which defines a port on the current EdifCell
     * object and corresponds to the given name. If no such EdifPort is found, a
     * null object is returned.
     * 
     * @param name A string indicating the name of the EdifPort object desired
     * to be found
     * @return The EdifPort corresponding to the passed-in port
     */
    public EdifPort getPort(String name) {
        for (EdifPort p : _portList) {
            if (NamedObjectCompare.equals(p, name))
                return p;
        }
        return null;
    }

    /**
     * Return a Collection containing all of the EdifPort objects currently
     * associated with this EdifCell object.
     * 
     * @return Collection containing the EdifPort objects associated with the
     * current EdifCell object
     */
    public List<EdifPort> getPortList() {
        if (_portList == null)
            return null;
        return new ArrayList<EdifPort>(_portList);
    }

    public Collection<EdifSingleBitPort> getSingleBitPortCollection() {
        Collection<EdifSingleBitPort> sbps = new ArrayList<EdifSingleBitPort>(this.getTotalInterfaceBits());
        for (EdifPort p : getPortList()) {
            sbps.addAll(p.getSingleBitPortList());
        }
        return sbps;
    }

    /**
     * Return a sorted Collection containing all of the EdifPort objects
     * currently associated with this EdifCell object. The objects in the
     * Collection are sorted by their names.
     * 
     * @return sorted Collection containing the EdifPort objects associated with
     * the current EdifCell object
     */
    public Collection<EdifPort> getSortedPortList() {
        ArrayList<EdifPort> sortedPortList = new ArrayList<EdifPort>(_portList);
        Collections.sort(sortedPortList, new NamedObjectCompare());
        return sortedPortList;
    }

    public int getTotalInoutBits() {
        int width = 0;
        for (EdifPort port : getInOutPorts()) {
            width += port.getWidth();
        }
        return width;
    }

    public int getTotalInputOnlyBits() {
        int width = 0;
        for (EdifPort port : getInputOnlyPorts()) {
            width += port.getWidth();
        }
        return width;
    }

    /**
     * @return The total number of bits in this interface. This is the sum of
     * the port width of each port in the interface.
     */
    public int getTotalInterfaceBits() {
        int width = 0;
        for (EdifPort port : _portList) {
            width += port.getWidth();
        }
        return width;
    }

    public int getTotalOutputOnlyBits() {
        int width = 0;
        for (EdifPort port : getOutputOnlyPorts()) {
            width += port.getWidth();
        }
        return width;
    }

    public EdifNameable getUniquePortNameable(EdifNameable en) {
        return _portList.getUniqueNameable(en);
    }

    public EdifNameable getUniquePortNameable(EdifPort port) {
        return _portList.getUniqueNameable(port);
    }

    /**
     * Returns true if the # of output, input, and inout ports match for the
     * current and passed-in cell interfaces.
     * 
     * @param cell The interface used for comparison
     * @return True if the # of each type of port that exists in each cell is
     * the same
     */
    public boolean portCountEquals(EdifCellInterface cell) {

        int output_c1 = 0;
        int output_c2 = 0;
        int input_c1 = 0;
        int input_c2 = 0;
        int inout_c1 = 0;
        int inout_c2 = 0;

        // Check the output port counts
        for (EdifPort port : _portList) {
            if (port.isOutputOnly())
                output_c1 += port.getWidth();
            else if (port.isInputOnly())
                input_c1 += port.getWidth();
            else if (port.isInOut())
                inout_c1 += port.getWidth();
        }

        for (EdifPort port : cell.getPortList()) {
            if (port.isOutputOnly())
                output_c2 += port.getWidth();
            else if (port.isInputOnly())
                input_c2 += port.getWidth();
            else if (port.isInOut())
                inout_c2 += port.getWidth();
        }
        if (output_c1 != output_c2 || input_c1 != input_c2 || inout_c1 != inout_c2)
            return false;

        return true;
    }

    /**
     * Write the EDIF representation of this EdifCell object to the
     * {@link EdifPrintWriter} passed as a parameter.
     * 
     * @param epw EdifPrintWriter to which EDIF will be written
     */
    public void toEdif(EdifPrintWriter epw) {

        epw.printlnIndent("(interface");

        epw.incrIndent(); // indent 3
        for (EdifPort p : _portList) {
            if (p.getWidth() > 0)
                p.toEdif(epw);
        }
        epw.decrIndent(); // indent 3
        epw.printlnIndent(")"); // End of interface

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Iterator<EdifPort> it = _portList.iterator(); it.hasNext();) {
            EdifPort port = it.next();
            sb.append(port);
            if (it.hasNext())
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Trim this object and all other contained Trimmable Objects down to size.
     */
    public void trimToSize() {
        _portList.trimToSize();
    }

    /**
     * Parent EdifCell that owns this interface
     */
    private EdifCell _parent;

    /**
     * This List is used to hold all the EdifPort objects of the interface
     */
    private EdifNameSpaceList<EdifPort> _portList = new EdifNameSpaceList<EdifPort>(
            BasicEdifNameClashPolicy.anyButOldWithNewClashPolicy, 4);
}
