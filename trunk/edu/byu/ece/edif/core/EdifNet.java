/*
 * Represents an EDIF signal net.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/////////////////////////////////////////////////////////////////////////
//// EdifNet
/**
 * Represents an EDIF signal net. A net within EDIF is defined by a collection
 * of edif port references. Specifically, the net is connected to each edif port
 * reference in the collection. This implementation of EdifNet contains contains
 * a reference to the {@link EdifCell} that contains this net. This class also
 * contains an array of {@link EdifPortRef} objects that attach to the net.
 * <p>
 * Sample EDIF code for an EdifNet:
 * 
 * <pre>
 *  (net sgn_exp 
 *  (joined
 *  (portRef (member sgn_exp 0))
 *  (portRef DI (instanceRef L_3_U2))
 *  (portRef I1 (instanceRef L_3_l_4))
 *  )
 *  )
 * </pre>
 * 
 * <p>
 * <b>To Do:</b>
 * <ul>
 * <li> Provide some form of a clone method
 * <li> We need a better way of handling the toString method for all classes.
 * What is the purpose of this method?
 * <li> Why have EdifNet's toEdif method write EdifPortRef's EDIF data? Have:
 * EdifPortRef do the toEdif method.
 * <li> <b>TMR</b> - is there a way that the EdifNet can keep track of which
 * EdifPortRef objects refer to a parent, and which connect only to
 * EdifCellInstances? a method such as <code>connectedToEdifCell()</code>,
 * which returns a boolean
 * <li> <b>TMR</b> - Could we add 2 methods -
 * <code>connectedToTopLevelInput()</code> and
 * <code>connectedToTopLevelOutput()</code> - this would add storage of only 2
 * booleans per EdifNet, and should be relatively easy to keep track of when a
 * net is created and connections are added. It would most likely result in a
 * great speed-up to the TMR stuff.
 * </ul>
 * 
 * @see EdifPortRef
 * @version $Id:EdifNet.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class EdifNet extends NamedPropertyObject implements EdifOut, Trimable {

    /**
     * Construct an empty, parentless, named EdifNet Object.
     * 
     * @param name The name of this EdifNet Object
     * @throws InvalidEdifNameException
     */
    public EdifNet(String name) throws InvalidEdifNameException {
        super(name);
    }

    /**
     * Construct an empty, parentless, named EdifNet Object.
     * 
     * @param name The Object that contains name information about this object
     */
    public EdifNet(EdifNameable name) {
        super(name);
    }

    /**
     * Construct an empty, named EdifNet Object with the specified parent.
     * 
     * @param name The name of this EdifNet Object
     * @param parent The parent EdifCell Object of this EdifNet
     * @throws InvalidEdifNameException
     */
    public EdifNet(String name, EdifCell parent) throws InvalidEdifNameException {
        this(name);
        setParent(parent);
    }

    /**
     * Construct an empty, named EdifNet Object with the specified parent.
     * 
     * @param name The Object that contains name information about this object
     * @param parent The parent EdifCell Object of this EdifNet
     */
    public EdifNet(EdifNameable name, EdifCell parent) {
        this(name);
        setParent(parent);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Add a pre-defined EdifPortRef port connection to the net. The EdifPortRef
     * object must be created before calling this method. This method makes the
     * following assumptions (these assumptions are not checked):
     * <ul>
     * <li> The EdifCellInstance referred by the port ref is owned by the same
     * EdifCell that owns this EdifNet.
     * <li> The EdifNet referred by the port ref is "this".
     * </ul>
     * If any of these assumptions are violated, an incoherent data structure
     * will result.
     * <p>
     * TODO: Should we check these assumptions?
     * 
     * @param portref The EdifPortRef to connect to the net.
     */
    public EdifPortRef addPortConnection(EdifPortRef portref) {
        _attachedPortRefs.add(portref);
        return portref;
    }

    /**
     * Add a port connection to the given port on the given instance onto this
     * net.
     * <p>
     * TODO: We should remove this method. We should force the caller to find
     * the Port rather than doing this here. There is too much opportunity for
     * run-time errors with this method.
     * <p>
     * This is only referenced in halflatch and TMR -B
     * 
     * @param eci The EdifCellInstance whose parent contains the port referred
     * to by the passed-in portName
     * @param portName The name of the port contained by the parent to the
     * passed-in EdifCellInstance Object
     */
    public EdifPortRef addPortConnection(EdifCellInstance eci, String portName) {
        EdifCell cellType = eci.getCellType();
        EdifPort p = cellType.getPort(portName);
        if (p == null)
            throw new EdifRuntimeException("No Port named " + portName + " on instance " + eci
                    + ". Available ports include " + eci.getCellType().getInterface());
        return addPortConnection(new EdifPortRef(this, p.getSingleBitPort(0), eci));
    }

    /**
     * TODO: Remove this method and force the caller to create a new
     * EdifPortRef() and then add that using
     * <code>addPortConnection(EdifPortRef epr)</code> -B
     * 
     * @param eci
     * @param p
     */
    public EdifPortRef addPortConnection(EdifCellInstance eci, EdifPort p) {
        EdifPortRef epr = new EdifPortRef(this, p.getSingleBitPort(0), eci);
        addPortConnection(epr);
        return epr;
    }

    /**
     * Add a port connection to the net.
     * <p>
     * TODO: Remove this method. What happens if the string parameter for the
     * port name fails to match in the method getPort?
     * <p>
     * This method is never used -B
     * 
     * @param eci The EdifCellInstance whose parent contains the port referred
     * to by the passed-in portName
     * @param portName The name of the port contained by the parent to the
     * passed-in EdifCellInstance Object
     * @deprecated This method should be removed.
     */
    public EdifPortRef addPortConnection(EdifCellInstance eci, String portName, int busMember) {
        EdifPort p = eci.getParent().getPort(portName);
        return addPortConnection(new EdifPortRef(this, p.getSingleBitPort(busMember), eci));
    }

    /**
     * Add a port connection to the net.
     * <p>
     * TODO: Remove this method. Force the caller to create the EdifPortRef
     * first and use the method addPortConnection(EdifPortRef epr)
     * 
     * @param eci The EdifCellInstance whose parent contains the port referred
     * to by the passed-in portName
     * @param p The port contained by the parent to the passed-in
     * EdifCellInstance Object
     * @deprecated Remove this method. Force the caller to create the
     * EdifPortRef first and use the method addPortConnection(EdifPortRef epr)
     */
    public EdifPortRef addPortConnection(EdifCellInstance eci, EdifPort p, int busMember) {
        EdifPortRef epr = new EdifPortRef(this, p.getSingleBitPort(busMember), eci);
        addPortConnection(epr);
        return epr;
    }

    /**
     * TODO: We should remove this method. We should force the caller to find
     * the Port rather than doing this here. There is too much opportunity for
     * run-time errors with this method.
     * <p>
     * This is only referenced in halflatch -B
     * 
     * @deprecated We should remove this method. We should force the caller to
     * find the Port rather than doing this here. There is too much opportunity
     * for run-time errors with this method.
     */
    public EdifPortRef addTopLevelPortConnection(String portName) {
        EdifPort port = _parent.getPort(portName);
        if (port == null)
            throw new EdifRuntimeException("No Port named " + portName + " in cell " + _parent);
        return addPortConnection(new EdifPortRef(this, port.getSingleBitPort(0), null));
    }

    /**
     * TODO: Remove this method. The caller should create a new EdifPortRef and
     * use the constructor addPortConnection(EdifPortRef epr)
     * <p>
     * This is never used -B
     * 
     * @param port
     * @deprecated Remove this method. The caller should create a new
     * EdifPortRef and use the constructor addPortConnection(EdifPortRef epr)
     */
    public EdifPortRef addTopLevelPortConnection(EdifPort port) {
        return addPortConnection(new EdifPortRef(this, port.getSingleBitPort(0), null));
    }

    /**
     * Return a deep copy of this Object. No Object, except String, is copied
     * over using '='. All are done using the 'new' statement, copying all the
     * insides verbatim.
     * 
     * @param parent The EdifCell Object that will become the parent of the
     * copied EdifNet
     * @return An EdifNet 'copy' of this Object, having the passed-in EdifCell
     * as its parent
     */
    public EdifNet copy(EdifCell parent) {
        EdifNameable newName = getEdifNameable();

        //        if (getEdifNameable() instanceof NamedObject)
        //            newName = new NamedObject(getName());
        //        else if (getEdifNameable() instanceof RenamedObject) {
        //            RenamedObject renamed = (RenamedObject) getEdifNameable();
        //            newName = new RenamedObject(renamed.getName(), renamed.getOldName());
        //        }
        //        // Only NamedObject and RenamedObject can be  of type EdifNameable (at the present time) 
        //        else {
        //            System.err.println("Error copying net (EdifNameable)");
        //            return null;
        //        }

        EdifNet net = new EdifNet(newName, parent);

        for (Iterator i = _attachedPortRefs.iterator(); i.hasNext();) {
            EdifPortRef port = (EdifPortRef) i.next();

            EdifCellInstance addInstance = null;
            if (port.getCellInstance() != null) {
                addInstance = parent.getInstance(port.getCellInstance().getName());
                if (addInstance == null) {
                    System.err.println("Error copying net (instance)");
                    return null;
                }
            }

            EdifPort actualPort = null;
            if (addInstance != null)
                actualPort = addInstance.getCellType().getPort(port.getPort().getName());
            else
                actualPort = parent.getPort(port.getPort().getName());
            if (actualPort == null) {
                System.err.println("Error copying net (port)");
                return null;
            }

            EdifPortRef newPort = new EdifPortRef(net, actualPort, addInstance, port.getBusMember());
            net.addPortConnection(newPort);
        }
        if (getPropertyList() != null)
            for (Iterator vals = getPropertyList().values().iterator(); vals.hasNext();)
                net.addProperty((Property) ((Property) vals.next()).clone());

        return net;
    }

    /**
     * Detach an EdifPortRef from this EdifNet.
     * 
     * @param delete The EdifPortRef to remove from this EdifNet Objects list of
     * EdifPortRefs
     * @return True if the EdifPortRef was found and deleted
     */
    public boolean deletePortConnection(EdifPortRef delete) {
        if (_attachedPortRefs == null)
            return false;
        return _attachedPortRefs.remove(delete);
    }

    /**
     * Return the EdifPortRef on this EdifNet that references the given
     * EdifPort/EdifCellInstance combination or null if no EdifPortRef on this
     * EdifNet references the given combination.
     * 
     * @param cell An EdifCellInstance Object to check if one of the
     * EdifPortRefs in this EdifNet Object connect to a port of that instance
     * @param port An EdifPort Object to check if one of the EdifPortRefs in
     * this EdifNet Object refer to that port
     * @return an EdifPortRef if this EdifNet is attached to the passed-in
     * EdifPort/EdifCellInstance combination
     */
    public EdifPortRef getAttachedPortRef(EdifCellInstance cell, EdifPort port) {
        Iterator i = getConnectedPortRefs().iterator();
        while (i.hasNext()) {
            EdifPortRef epr = (EdifPortRef) i.next();
            if (epr.getCellInstance() == cell && epr.getPort() == port)
                return epr;
        }
        return null;
    }

    /**
     * This method will return a list of all EdifPortRef objects attached to the
     * given EdifCellInstance.
     * <p>
     * This method is only used by EdifCellInstance. Shouldn't EdifCellInstance
     * then be responsible for the work of this method? EdifCellInstance could
     * do the work by first getting the iterator using the getPortRefIterator()
     * method. -B
     * <p>
     * TODO: better name for method
     * <p>
     * TODO: remove this method
     * 
     * @param cell The instance to find port refs of this EdifNet that attach to
     * this instance
     * @return A Collection of EdifPortRef Objects that attach to the passed-in
     * EdifCellInstance
     */
    public Collection<EdifPortRef> getAttachedPortRefs(EdifCellInstance cell) {
        ArrayList<EdifPortRef> list = new ArrayList<EdifPortRef>(3);
        for (EdifPortRef epr : getConnectedPortRefs()) {
            if (epr.getCellInstance() == cell)
                list.add(epr);
        }
        return list;
    }

    /**
     * Return a list of EdifPortRef objects which are attached to this net.
     * 
     * @return A safe copy of the Collection of EdifPortRef objects attached to
     * this net.
     */
    public Collection<EdifPortRef> getConnectedPortRefs() {
        if (_attachedPortRefs == null)
            return null;
        return new ArrayList<EdifPortRef>(_attachedPortRefs);
    }

    /**
     * Return the EdifPortRef object attached to the given instance and single
     * bit port.
     * 
     * @param inst The EdifCellInstance reference attached to this EdifNet
     * @param singlePort The single-bit port within the instance type attached
     * to the net.
     * @return The EdifPortRef object that connects the net to the port of the
     * instance.
     */
    public EdifPortRef getEdifPortRef(EdifCellInstance inst, EdifSingleBitPort singlePort) {
        Iterator i = getConnectedPortRefs().iterator();
        while (i.hasNext()) {
            EdifPortRef epr = (EdifPortRef) i.next();
            if (epr.getCellInstance() == inst && // instance must match
                    epr.getPort() == singlePort.getParent()) {
                if (epr.getBusMember() == singlePort.bitPosition())
                    return epr;
            }
        }
        return null;
    }

    /**
     * This method will return all EdifPortRef in this EdifNet object that are
     * connected to ports of type "inout". This method does not distinguish
     * between EdifPortRefs that are connected to top-level ports versus those
     * connected to instances.
     * 
     * @return A Collection of EdifPortRef Objects that are connected to inout
     * ports.
     */
    public Collection<EdifPortRef> getInOutPortRefs() {
        ArrayList<EdifPortRef> a = new ArrayList<EdifPortRef>();
        for (Iterator i = getConnectedPortRefs().iterator(); i.hasNext();) {
            EdifPortRef portref = (EdifPortRef) i.next();
            if (portref.getPort().isInOut())
                a.add(portref);
        }
        return a;
    }

    /**
     * Provide a Colletion of EdifPortRef objects that are attached to this
     * EdifNet and connected to tri-state ports (inout).
     * 
     * @return A Collection of EdifPort Objects that are connected to this
     * EdifNet Object
     */
    public Collection<EdifPort> getInOutPorts() {
        ArrayList<EdifPort> a = new ArrayList<EdifPort>();
        for (Iterator i = getConnectedPortRefs().iterator(); i.hasNext();) {
            EdifPortRef portref = (EdifPortRef) i.next();
            if (portref.getPort().isInOut())
                a.add(portref.getPort());
        }
        return a;
    }

    /**
     * This method will return all EdifPortRef in this EdifNet object that are
     * connected to ports of type "input".
     * <p>
     * Note that this does NOT provide a list of "sink" EdifPortRefs as it
     * returns the top-level input port refs that are actually "sources". Use
     * {@link #getSinkPortRefs(boolean, boolean)} to obtain all "sink"
     * EdifPortRefs.
     * 
     * @return A Collection of EdifPortRef objects that are connected to this
     * EdifNet Object and attached to a port that is of type Input.
     */
    public Collection<EdifPortRef> getInputPortRefs() {
        ArrayList<EdifPortRef> a = new ArrayList<EdifPortRef>();
        for (Iterator i = getConnectedPortRefs().iterator(); i.hasNext();) {
            EdifPortRef portref = (EdifPortRef) i.next();
            if (portref.getPort().isInput())
                a.add(portref);
        }
        return a;
    }

    /**
     * This method will return all portrefs that can be driven on the current
     * net. This will return all EdifPortRef objects that are associated with
     * EdifCellInstances (i.e. EdifPortRef.getCellInstance() returns a non-null)
     * and are inputs. This method will also return EdifPortRef objects that are
     * associated with an EdifCell (i.e. EdifPortRef.getCellInstance() returns
     * null) and is an output. These EdifPortRef objects refer to top-level
     * output ports that can be driven by the given net.
     * 
     * @return A Collection of EdifPortRefs that are driven by this EdifNet
     * Object
     * @deprecated Use getSinkPortRefs(boolean, boolean) in place of this
     * method.
     */
    @Deprecated
    public Collection getNetDriven() {
        ArrayList<EdifPortRef> a = new ArrayList<EdifPortRef>();
        for (Iterator i = getConnectedPortRefs().iterator(); i.hasNext();) {
            EdifPortRef portref = (EdifPortRef) i.next();
            if (portref.getPort().isInput() && // This if statement could be rearanged to 
                    portref.getCellInstance() != null) // reduce the possible number of comparisons
                a.add(portref); // by putting the cellinstance comparison first
            else if (portref.getPort().isOutput() && // and the i/o inside it
                    portref.getCellInstance() == null)
                a.add(portref);
        }
        return a;
    }

    /**
     * This method will return all portrefs that can drive on the current net.
     * 
     * @return A Collection of EdifPortRef Objects that can drive this EdifNet
     * Object
     * @deprecated Use getSourcePortRefs(boolean, boolean) in place of this
     * method.
     */
    @Deprecated
    public Collection getNetDrivers() {
        ArrayList<EdifPortRef> a = new ArrayList<EdifPortRef>();
        for (Iterator i = getConnectedPortRefs().iterator(); i.hasNext();) {
            EdifPortRef portref = (EdifPortRef) i.next();
            if (portref.getPort().isOutput() && portref.getCellInstance() != null)
                a.add(portref);
            else if (portref.getPort().isInput() && portref.getCellInstance() == null)
                a.add(portref);
        }
        return a;
    }

    /**
     * This method will return all EdifPortRef in this EdifNet object that are
     * connected to ports of type "output".
     * <p>
     * Note that this does NOT provide a list of "Source" EdifPortRefs as it
     * returns the top-level output ports that are actually "sinks". Use
     * {@link #getSourcePortRefs(boolean, boolean)} to obtain the edif port refs
     * that are sources.
     * 
     * @return A Collection of EdifPortRef objects that are connected to this
     * EdifNet Object and attached to a port that is of type OUTPUT.
     */
    public Collection<EdifPortRef> getOutputPortRefs() {
        ArrayList<EdifPortRef> a = new ArrayList<EdifPortRef>();
        for (Iterator i = getConnectedPortRefs().iterator(); i.hasNext();) {
            EdifPortRef portref = (EdifPortRef) i.next();
            if (portref.getPort().isOutput())
                a.add(portref);
        }
        return a;
    }

    /**
     * Provide a Colletion of EdifPortRef objects that are attached to this
     * EdifNet and connected to true output ports.
     * 
     * @return A Collection of Output EdifPort Objects that are connected to
     * this EdifNet Object
     */
    public Collection<EdifPort> getOutputPorts() {
        ArrayList<EdifPort> a = new ArrayList<EdifPort>();
        for (Iterator i = getConnectedPortRefs().iterator(); i.hasNext();) {
            EdifPortRef portref = (EdifPortRef) i.next();
            if (portref.getPort().isOutput())
                a.add(portref.getPort());
        }
        return a;
    }

    /**
     * Return a reference to the parent EdifCell (owner of net).
     * 
     * @return The parent {@link EdifCell} object
     */
    public EdifCell getParent() {
        return _parent;
    }

    /**
     * Create an iterator for the EdifPortRef objects owned by this net. An safe
     * empty Iterator will be returned if there are no attached port refs.
     */
    public Iterator getPortRefIterator() {
        if (_attachedPortRefs != null)
            return _attachedPortRefs.iterator();
        return (new ArrayList(0)).iterator();
    }

    /**
     * @return An ArrayList of all the EdifPortRef objects associated with this
     * EdifNet.
     */
    public List<EdifPortRef> getPortRefList() {
        int length = _attachedPortRefs.size();
        ArrayList<EdifPortRef> ebr = new ArrayList<EdifPortRef>(length);
        for (int i = 0; i < length; i++)
            ebr.add(_attachedPortRefs.get(i));
        return ebr;
    }

    /**
     * Return all EdifPortRef objects in this EdifNet object that are "sinks".
     * This includes those EdifPortRef objects connected to input ports of
     * EdifCellInstances and EdifPortRef objects connected to top-level output
     * ports.
     * <p>
     * This method is different than the getInputPortRefs() method in that this
     * method distinguishes between input ports that are sinks and those that
     * are sources (top-level input ports are sources).
     * 
     * @param tristate Indicates whether tristate ports should be included (true
     * indicates that tri-state ports should not be included; false indicates
     * that tristate ports should be ignored).
     * @param includeTopPorts Indicates whether connections to top-level ports
     * should be included (true indicates that port refs to top-level ports
     * should be included, false indicates that these port refs should be
     * ignored).
     * @see EdifPort#isOutput(boolean).
     * @return A Collection of EdifPortRef objects that are sinks on the net. If
     * there are no sink ports, this method will return an empty but valid
     * Collection.
     */
    public Collection<EdifPortRef> getSinkPortRefs(boolean tristate, boolean includeTopPorts) {
        ArrayList<EdifPortRef> a = new ArrayList<EdifPortRef>();
        for (EdifPortRef portref : getConnectedPortRefs()) {
            EdifPort port = portref.getPort();
            if ((includeTopPorts && portref.isTopLevelPortRef() && port.isOutput(tristate))
                    || (portref.isTopLevelPortRef() == false && port.isInput(tristate)))
                a.add(portref);
        }
        return a;
    }

    /**
     * This method will return all EdifPortRef objects in this EdifNet object
     * that are "sources". This includes those EdifPortRef objects connected to
     * output ports of EdifCellInstances and EdifPortRef objects connected to
     * top-level input ports.
     * <p>
     * This method is different than the getOutputPortRefs() method in that this
     * method distinguishes between output ports that are sources and those that
     * are sinks (top-level output ports are sinks).
     * 
     * @param tristate Indicates whether tristate ports should be included or
     * not (true indicates that tri-state ports should be considered as
     * "sources" and thus include corresponding port refs in the collection.
     * False indicates that tri-state ports should not be considered as sources
     * and thus ignore port refs to these connections).
     * @param includeTopPorts Indicates whether connections to top-level ports
     * should be included (true indicates that port refs to top-level ports
     * should be included, false indicates that these port refs should be
     * ignored).
     * @see EdifPort#isOutput(boolean)
     * @return A Collection of EdifPortRef objects that are sources on the net.
     * If there are no source ports, this method will return an empty but valid
     * Collection.
     */
    public Collection<EdifPortRef> getSourcePortRefs(boolean tristate, boolean includeTopPorts) {
        ArrayList<EdifPortRef> a = new ArrayList<EdifPortRef>();
        for (EdifPortRef portref : _attachedPortRefs) {
            EdifPort port = portref.getPort();
            if ((includeTopPorts && portref.isTopLevelPortRef() && port.isInput(tristate))
                    || (portref.isTopLevelPortRef() == false && port.isOutput(tristate)))

                a.add(portref);
        }
        return a;
    }

    /**
     * This method will determine whether or not the given net connects to at
     * least one top-level port. This occurs when any owned EdifPortRef object
     * has a "null" for its EdifCellInstance object.
     * 
     * @return true if the current EdifNet connects to at least one top-level
     * port, false if the current EdifNet has no connections to a top-level
     * port.
     */
    public boolean hasTopLevelPortConnection() {
        for (EdifPortRef epr : getConnectedPortRefs()) {
            if (epr.isTopLevelPortRef())
                return true;
        }
        return false;
    }

    /**
     * This method will examine the EdifPortRef objects connected to this net
     * and determine whether the given EdifCellInstance is connected or not to
     * the given EdifNet.
     * 
     * @param cell The EdifCellInstance Object that will be tested to see if it
     * connects to this EdifNet Object
     * @return True if this EdifNet Object is attached to the passed-in
     * EdifCellInstance Object
     */
    public boolean isAttached(EdifCellInstance cell) {
        Iterator i = getConnectedPortRefs().iterator();
        while (i.hasNext()) {
            EdifPortRef epr = (EdifPortRef) i.next();
            if (epr.getCellInstance() == cell)
                return true;
        }
        return false;
    }

    /**
     * This method will return true if the given EdifPort/EdifCellInstance
     * combination are attached to the given EdifNet.
     * 
     * @param cell An EdifCellInstance Object to check if one of the
     * EdifPortRefs in this EdifNet Object connect to a port of that instance
     * @param port An EdifPort Object to check if one of the EdifPortRefs in
     * this EdifNet Object refer to that port
     * @return True if this EdifNet is attached to the passed-in
     * EdifPort/EdifCellInstance combination
     */
    public boolean isAttached(EdifCellInstance cell, EdifPort port) {
        return (getAttachedPortRef(cell, port) != null);
    }

    /**
     * Determine if this EdifNet object is connected to the given instance and
     * single-bit port.
     * 
     * @param cell Instance object to search for
     * @param singlePort The single-bit port to search for
     * @see #getEdifPortRef
     */
    public boolean isAttached(EdifCellInstance cell, EdifSingleBitPort singlePort) {
        if (getEdifPortRef(cell, singlePort) == null)
            return false;
        return true;
    }

    /**
     * This method does a deep compare on the current net with the passed-in
     * net. The two nets must have the same name, same number of connected
     * ports, and a port connected to one net must match the name of a port
     * connected to the other net.
     * 
     * @param net The net to compare to this net
     * @return True if both nets have the same structure, false otherwise.
     */
    public boolean samePorts(EdifNet net) {
        // make sure the two nets have the same name
        if (!NamedObjectCompare.equals(this, net))
            return false;

        Collection aListPorts = getConnectedPortRefs();
        Collection bListPorts = net.getConnectedPortRefs();

        // check to see if both have no connected ports
        if (aListPorts == null && bListPorts == null)
            return true;
        // check to see if either has no ports
        else if (aListPorts == null && bListPorts != null || aListPorts != null && bListPorts == null ||
        // check to see if the two lists don't have the same
                // number of ports in each of their lists of ports
                aListPorts.size() != bListPorts.size())
            return false;

        // See if each port in list a exists in list b (check
        // equality by name only, for now)
        for (Iterator itAPorts = aListPorts.iterator(); itAPorts.hasNext();) {
            EdifPortRef portA = (EdifPortRef) itAPorts.next();

            // if a port doesn't exist in list b then return false
            if (!portA.matchesWithin(bListPorts))
                return false;
        }

        return true;
    }

    /**
     * Convert this object to EDIF format and write it to the passed-in
     * EdifPrintWriter Object.
     * 
     * @param epw The EdifPrintWriter Object that the EDIF data will be written
     * to
     */
    public void toEdif(EdifPrintWriter epw) {

        epw.printIndent("(net ");

        // have the name implement the toEdif function
        getEdifNameable().toEdif(epw);
        epw.println();

        epw.incrIndent();
        epw.printlnIndent("(joined");
        if (!_attachedPortRefs.isEmpty()) {
            epw.incrIndent();
            Iterator it = _attachedPortRefs.iterator();
            while (it.hasNext()) {
                epw.flush();
                EdifPortRef portRef = (EdifPortRef) it.next();
                EdifPort port = portRef.getPort();
                epw.printIndent("(portRef ");
                String portName = port.getName();
                if (!port.isArray()) {
                    epw.print(portName);
                } else {
                    int busMember = portRef.getBusMember();
                    epw.print("(member " + portName + " " + busMember + ")");
                }

                if (portRef.getCellInstance() != null)
                    epw.print(" (instanceRef " + portRef.getCellInstance().getName() + ")");

                epw.println(")");
            }
            epw.decrIndent();
        }
        epw.printlnIndent(")");
        epw.decrIndent();
        epw.flush();

        Map l = getPropertyList();
        if (l != null) {
            epw.incrIndent();
            Iterator it = l.values().iterator();
            while (it.hasNext()) {
                Property p = (Property) it.next();
                p.toEdif(epw);
            }
            epw.decrIndent();
        }

        epw.printlnIndent(")");

    }

    /**
     * String representation of the EdifNet with detailed information.
     * 
     * @return a <code>String</code> value representing this Object and its
     * EdifPortRefs
     */
    public String toString() {
        return getEdifNameable().toString();
    }

    /**
     * Trim to size this object and all contained Trimmable Objects.
     * <p>
     * TODO: Why do we frist test to see if the ArrayList is Trimmable?
     * shoulden't this always be true? -B
     */
    public void trimToSize() {
        _attachedPortRefs.trimToSize();
    }

    ///////////////////////////////////////////////////////////////////
    ////                 package private methods                   ////

    /**
     * Set the reference to the parent EdifCell. Note: Adding this net to a cell
     * will set the parent of this net to that cell.
     * 
     * @param cell an <code>EdifCell</code> value that will become the new
     * parent cell of this EdifNet
     */
    void setParent(EdifCell cell) {
        _parent = cell;
    }

    ///////////////////////////////////////////////////////////////////
    // Private methods

    /**
     * List of EdifPortRef objects that this net is connected to. Limit the size
     * initial List so that we don't waste memory on unused list elements.
     */
    private ArrayList<EdifPortRef> _attachedPortRefs = new ArrayList<EdifPortRef>(4);

    /**
     * The reference to the parent EdifCell
     */
    private EdifCell _parent;
}
