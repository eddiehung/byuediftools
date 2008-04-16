/*
 * Represents a port reference within an EdifEnvironment.
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
import java.util.Collection;

/////////////////////////////////////////////////////////////////////////
////EdifPortRef
/**
 * Represents a port reference within an EdifEnvironment. This object contains a
 * reference to an EdifPort, an EdifNet, and an EdifCellInstance. The EdifPort
 * object is a port that is contained by an EdifCell object. The EdifNet object
 * is the parent or "owner" of this object (i.e. this object is contained in the
 * EdifNet list of EdifPortRef objects). The EdifCellInstance object refers to
 * the cell instance that owns the port. If this reference is null, the
 * referenced port is a top-level port found in the EdifCell that owns the
 * EdifNet object.
 * 
 * @see EdifPortRef
 * @see EdifNet
 * @version $Id:EdifPortRef.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class EdifPortRef implements Serializable {

    /**
     * Construct an atomic EdifPortRef object based on a given net, port, and
     * instance with a bus member = -1 (i.e. hooked up to a single-bit port).
     * 
     * @param net The EdifNet Object this EdifPortRef Object is connected to
     * @param port The EdifPort Object that this EdifPortRef refers to
     * @param instance The EdifCellInstance Object this EdifPortRef connects to
     * @deprecated Please use
     * EdifPortRef(EdifNet,EdifSingleBitPort,EdifCellInstance) instead
     */
    @Deprecated
    public EdifPortRef(EdifNet net, EdifPort port, EdifCellInstance instance) {
        init(net, port.getSingleBitPort(0), instance);
    }

    /**
     * Construct an atomic EdifPortRef object based on a given net, port, and
     * instance with a bus member.
     * 
     * @param net The EdifNet Object this EdifPortRef Object is connected to
     * @param port The EdifPort Object that this EdifPortRef refers to
     * @param instance The EdifCellInstance Object this EdifPortRef connects to
     * @param busMember The position within the EdifPort object to retrieve the
     * EdifSingleBitPort
     * @deprecated Please use
     * EdifPortRef(EdifNet,EdifSingleBitPort,EdifCellInstance) instead
     */
    @Deprecated
    public EdifPortRef(EdifNet net, EdifPort port, EdifCellInstance instance, int busMember) {
        if (busMember == -1)
            busMember = 0;
        init(net, port.getSingleBitPort(busMember), instance);
    }

    /**
     * A constructor for the new EdifSingleBitPort.
     * 
     * @param net The EdifNet Object this EdifPortRef Object is connected to
     * @param port The EdifSingleBitPort Object that this EdifPortRef refers to
     * @param instance The EdifCellInstance Object this EdifPortRef connects to.
     */
    public EdifPortRef(EdifNet net, EdifSingleBitPort port, EdifCellInstance instance) {
        init(net, port, instance);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Copy reference of refPort (DOES NOT COPY PORT!) Copy reference of
     * EdifCellInstance (DOES NOT COPY INSTANCE) Copy reference to EdifNet (DOES
     * NOT COPY NET)
     * 
     * @return an Object that is a copy or clone of this EdifPortRef Object
     */
    public Object clone() {
        EdifPortRef epr = new EdifPortRef(_net, _refSingleBitPort, _cellInstance);
        return epr;
    }

    /**
     * Two EdifPortRef objects are considered edifEqual if:
     * <ul>
     * <li> If the referenced EdifCellInstance objects in each EdifPortRef
     * object have the same name</li>
     * </ul>
     * 
     * @param o Comparison EdifPortRef object
     * @return true of the two EdifPortRef objects are edifEqual
     */
    public boolean edifEquals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EdifPortRef))
            return false;

        EdifPortRef that = (EdifPortRef) o;

        return edifEqualCellInstanceName(that);

    }

    /**
     * Return true if the current port ref's port equals within one of the
     * passed-in collection of EdifPortRef Object's port. Equality done by ==.
     * 
     * @param portRefs A Collection of EdifPortRef Objects that the port of this
     * EdifPortRef Object will be compared to in order to determine if an
     * EdifPort exists between this EdifPortRef Object, and an EdifPortRef
     * Object within the Collection
     * @return True if a port exists between this EdifPortRef Object and an
     * EdifPortRef Object within the passed-in Collection of EdifPortRef Objects
     */
    public boolean equalsPortRefWithin(Collection<EdifPortRef> portRefs) {
        if (portRefs == null)
            return false;
        for (EdifPortRef port : portRefs) {
            // equality checking done by ==
            if (getPort() == port.getPort())
                return true;
        }
        return false;
    }

    /**
     * Return true if the current port ref equals within one of the passed-in
     * collection of EdifPort Objects. Equality done by ==.
     * 
     * @param ports A Collection of EdifPort Objects that this EdifPortRef
     * Object will be compared to in order to determine if an EdifPort exists
     * between this EdifPortRef Object, and an EdifPort Object within the
     * Collection
     * @return True if a port exists between this EdifPortRef Object and an
     * EdifPort Object within the passed-in Collection of EdifPort Objects
     */
    public boolean equalsPortWithin(Collection<EdifPort> ports) {
        if (ports == null)
            return false;
        for (EdifPort port : ports) {

            // equality checking done by ==
            if (getPort() == port)
                return true;
        }
        return false;
    }

    /**
     * Return the index this EdifPortRef Object refers to within a bus.
     * 
     * @return An int specifying the index this EdifPortRef Object refers to
     * within a bus
     */
    public int getBusMember() {
        return _refSingleBitPort.bitPosition();
    }

    /**
     * Return the EdifCellInstance Object that this EdifPortRef Object connects
     * to.
     * 
     * @return An EdifCellInstance Object that this EdifPortRef Object connects
     * to
     */
    public EdifCellInstance getCellInstance() {
        return _cellInstance;
    }

    /**
     * Return the EdifNet Object that this EdifPortRef Object is connected to.
     * 
     * @return An EdifNet Object that this EdifPortRef Object is connected to
     */
    public EdifNet getNet() {
        return _net;
    }

    /**
     * Return the EdifPort Object that this EdifPortRef Object refers to.
     * 
     * @return The EdifPort Object referred to by this EdifPortRef Object
     */
    public EdifPort getPort() {
        return _refSingleBitPort.getParent();
    }

    /**
     * @deprecated This method has been deprecated in favor of the
     * EdifPort.getCellInstance() method that contains a more intuitive method
     * name.
     */
    @Deprecated
    public EdifCellInstance getRefCell() {
        return _cellInstance;
    }

    /**
     * Return the EdifPort Object that this EdifPortRef Object refers to.
     * 
     * @return The EdifPort Object referred to by this EdifPortRef Object
     * @deprecated This method has been deprecated in favor of the
     * EdifPort.getPort() method that contains a more intuitive method name.
     */
    @Deprecated
    public EdifPort getRefPort() {
        return getPort();
    }

    /**
     * Return the EdifSingleBitPort Object that this EdifPortRef Object refers
     * to.
     * 
     * @return The EdifSingleBitPort Object referred to by this EdifPortRef
     * Object
     */
    public EdifSingleBitPort getSingleBitPort() {
        return _refSingleBitPort;
    }

    /**
     * Indicate whether the port ref is a "driver" connection rather than a sink
     * connection. If the associated port is a top-level port and the port is a
     * top-level input, this method will return true. If the assocaited port is
     * an internal port and the internal port is an output, this method will
     * return true.
     * 
     * @return If the associated port is a top-level port and the port is a
     * top-level input, this method will return true. If the assocaited port is
     * an internal port and the internal port is an output, this method will
     * return true.
     */
    public boolean isDriverPortRef() {
        if (isTopLevelPortRef() && _refSingleBitPort.getParent().isInput())
            return true;
        if (!isTopLevelPortRef() && _refSingleBitPort.getParent().isOutput())
            return true;
        return false;
    }

    /**
     * Return true if port ref corresponds to a single-bit port (i.e. bus member =
     * -1).
     */
    public boolean isSingleBitPortRef() {
        return getPort().getWidth() == 1;
    }

    /**
     * Indicate whether the EdifPortRef is connected to a top level port.
     * 
     * @return true when associated Port is a top-level port, false when
     * associated Port is an internal port.
     */
    public boolean isTopLevelPortRef() {
        if (_cellInstance == null)
            return true;
        return false;
    }

    /**
     * Return true if the current port ref matches within the passed-in
     * collection of EdifPortRef Objects. Equality done by doing a deep compare
     * on the ports.
     * 
     * @param portRefs A Collection of EdifPortRef Objects that this EdifPortRef
     * Object will be compared to in order to determine if a match exists
     * between this EdifPortRef Object, and an EdifPortRef Object within the
     * Collection
     * @return True if a match exists between this EdifPortRef Object and an
     * EdifPortRef Object within the passed-in Collection of EdifPortRef Objects
     */
    public boolean matchesWithin(Collection<EdifPortRef> portRefs) {
        if (portRefs == null)
            return false;
        for (EdifPortRef port : portRefs) {

            // equality checking done by name only
            if (getPort().equals(port.getPort()) && getBusMember() == port.getBusMember())
                return true;
        }
        return false;
    }

    /**
     * Return a String representation of this Object. The output is as follows:
     * 
     * <pre>
     *  
     *      Top-level port: TOP-&lt;CellName&gt;.&lt;port&gt; 
     *      Internal port:  &lt;InstanceName&gt;.&lt;port&gt;
     * </pre>
     * 
     * @return A String representing this object
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (_cellInstance == null) {
            sb.append("top(");
            sb.append(_net.getParent());
            sb.append(")");
        } else
            sb.append(_cellInstance);
        sb.append(", ");
        sb.append(_refSingleBitPort);
        sb.append("]");
        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////
    ////                      private methods                      ////

    private boolean edifEqualCellInstanceName(EdifPortRef that) {
        boolean result;

        if (this.getCellInstance() == null)
            result = (that.getCellInstance() == null);
        else if (that.getCellInstance() == null)
            result = false;
        else if (this.getCellInstance().getName() == null)
            result = (that.getCellInstance().getName() == null);
        else
            result = this.getCellInstance().getName().equals(that.getCellInstance().getName());
        return result;
    }

    private void init(EdifNet net, EdifSingleBitPort port, EdifCellInstance i) {
        _net = net;
        _cellInstance = i;
        _refSingleBitPort = port;
    }

    ///////////////////////////////////////////////////////////////////
    ////                     private variables                     ////

    /**
     * The EdifPort Object this EdifPortRef Object refers to.
     */
    private EdifSingleBitPort _refSingleBitPort = null;

    /**
     * The EdifCellInstance Object that this EdifPortRef Object connects to.
     */
    private EdifCellInstance _cellInstance = null;

    /**
     * The EdifNet Object that connects to this EdifPortRef Object.
     */
    private EdifNet _net = null;
}
