/*
 * Represents an Edif port, which belongs to a specific EdifCell.
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/////////////////////////////////////////////////////////////////////////
////EdifPort
/**
 * Represents an Edif port, which belongs to a specific {@link EdifCell}. This
 * class specifies a port direction ({@link EdifPort#IN}, {@link EdifPort#OUT}
 * or {@link EdifPort#INOUT}), and a width for support of multi-bit ports.
 * <p>
 * Note that this object is immutable. Once this object has been created, it
 * cannot be changed.
 * <p>
 * The purpose of an EdifPort object is to connect a net from the outer level of
 * hierarchy to the inner level of hierarchy.
 * <p>
 * Because ports in EDIF can represent both single and multiple bits, this class
 * represents the port as a collection of EdifSingleBitPort objects. A distinct
 * object is created for each bit of the port.
 * <p>
 * Sample EDIF code for an EdifPort:
 * 
 * <pre>
 *  (cell RAMB4_S2 (cellType GENERIC)
 *  (view PRIM (viewType NETLIST)
 *  (interface
 *  (port (array (rename do &quot;DO[1:0]&quot;) 2) (direction OUTPUT))
 *  (port (array (rename addr &quot;ADDR[10:0]&quot;) 11) (direction INPUT))
 *  (port (array (rename di &quot;DI[1:0]&quot;) 2) (direction INPUT))
 *  (port EN (direction INPUT))
 *  (port CLK (direction INPUT))
 *  (port WE (direction INPUT))
 *  (port RST (direction INPUT))
 *  )
 *  )
 *  )
 * </pre>
 * 
 * <p>
 * 
 * @version $Id:EdifPort.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class EdifPort extends NamedPropertyObject implements EdifOut {

    /**
     * Construct an EdifPort according to the passed in name, width and
     * direction.
     * 
     * @param parent The parent of this new EdifPort
     * @param name The name of this Object
     * @param width The width of this EdifPort
     * @param direction The direction of this EdifPort
     * @throws InvalidEdifNameException
     */
    public EdifPort(EdifCellInterface parent, String name, int width, int direction) throws InvalidEdifNameException {
        super(name);
        _init(parent, width, direction);
    }

    /**
     * Construct an EdifPort according to the passed in name, width and
     * direction.
     * 
     * @param parent The parent of this new EdifPort
     * @param name The Object holding name information for this EdifPort Object
     * @param width The width of this EdifPort
     * @param direction The direction of this EdifPort
     */
    public EdifPort(EdifCellInterface parent, EdifNameable name, int width, int direction) {
        super(name);
        _init(parent, width, direction);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public variables                  ////

    /**
     * Constant value representing that the EdifPort direction is IN.
     */
    public static final int IN = 1;

    /**
     * Constant value representing that the EdifPort direction is OUT.
     */
    public static final int OUT = 2;

    /**
     * Constant value representing that the EdifPort direction is INOUT.
     */
    public static final int INOUT = 3;

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * This method determines whether the EdifPort object passed in is the same
     * as this EdifPort object.
     * 
     * @param port The EdifPort object to compare for equality with this
     * EdifPort Object
     * @return True if the passed-in EdifPort Object matches with this one
     */
    public boolean equals(EdifPort port) {
        return equals(this, port);
    }

    /**
     * Two EdifPort objects are considered edifEqual if:
     * <ul>
     * <li> They have the same name (as defined by {@link EdifNet#getName()}.equals())</li>
     * <li> They have the same direction (IN or OUT)</li>
     * <li> The property lists are equal (as defined by
     * {@link PropertyList#equals(PropertyList)}) </li>
     * <li> Each of the SingleBitPort objects are edifEqual when taken in order</li>
     * </ul>
     * 
     * @param o Comparison EdifPort object
     * @return true of the two EdifPort objects are edifEqual
     */
    public boolean edifEquals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EdifPort))
            return false;

        EdifPort that = (EdifPort) o;

        return this._direction == that.getDirection()
                && (this.getName() == null ? that.getName() == null : this.getName().equals(that.getName()))
                && (this.getPropertyList() == null ? that.getPropertyList() == null : this.getPropertyList().equals(
                        that.getPropertyList())) && edifEqualsSingleBitPorts(that);
    }

    /**
     * Return the EdifCell which this EdifPort belongs to.
     * 
     * @return an <code>EdifCell</code> value representing the
     * EdifCellInterface of this EdifPort Object
     */
    public EdifCellInterface getCellInterface() {
        return _parentInterface;
    }

    /**
     * Return the EdifPort's direction There are three kinds of directions in
     * this package: {@link EdifPort#IN} {@link EdifPort#OUT}
     * {@link EdifPort#INOUT}
     * 
     * @return The direction of the port
     */
    public int getDirection() {
        return _direction;
    }

    public String getDirectionString() {

        switch (_direction) {
        case IN:
            return "Input";
        case OUT:
            return "Output";
        case INOUT:
            return "Input/Output";
        default:
            return "Unknown";
        }
    }

    /**
     * Return the EdifCell that contains this port.
     * 
     * @return An EdifCell that contains this EdifPort in its EdifCellInterface
     */
    public EdifCell getEdifCell() {
        return _parentInterface.getEdifCell();
    }

    /**
     * Returns the EdifSingleBitPort object associated with bit i of this port.
     * If i < 0 or greater than the maximum number of ports, this method will
     * return null.
     * 
     * @param i Bit index of port.
     * @return The EdifSingleBitPort object associated with bit i of this port.
     */
    public EdifSingleBitPort getSingleBitPort(int i) {
        if (i < 0 || i >= _singleBitPorts.length)
            return null;
        return _singleBitPorts[i];
    }

    /**
     * @return A List of all the EdifSingleBitPort objects associated with this
     * EdifPort.
     */
    public List<EdifSingleBitPort> getSingleBitPortList() {
        ArrayList<EdifSingleBitPort> sbp = new ArrayList<EdifSingleBitPort>(_singleBitPorts.length);
        for (int i = 0; i < _singleBitPorts.length; i++)
            sbp.add(_singleBitPorts[i]);
        return sbp;
    }

    /**
     * Return the width of this EdifPort.
     * 
     * @return an <code>int</code> representing the width of this EdifPort
     */
    public int getWidth() {
        return _singleBitPorts.length;
    }

    /**
     * Return true if this port is a bus-port, false otherwise. Equality is done
     * by seeing if singleBitPorts.length > 1.
     * 
     * @return True if this EdifPort Object has a width > 1, meaning that its a
     * bus
     */
    public boolean isBus() {
        if (_singleBitPorts.length > 1)
            return true;
        return false;
    }

    /**
     * Check if this port's direction is bidirectional
     * 
     * @return True if this EdifPort Object is InOut
     */
    public boolean isInOut() {
        return (_direction == INOUT);
    }

    /**
     * Check if this port's direction is input, meaning IN or INOUT
     * 
     * @return True if this EdifPort Object is Input or InOut
     */
    public boolean isInput() {
        return (_direction == IN) || (_direction == INOUT);
    }

    /**
     * Determine whether this port is an input. This method uses a tristate flag
     * to indicate how to handle the tristate or INOUT condition.
     * 
     * @param tristate Indicates that a tri-state port (INOUT) should be
     * considered as an input. If false, this method will return false for ports
     * that are tri-state (INOUT).
     * @return true if the port is considered an input
     */
    public boolean isInput(boolean tristate) {
        return (_direction == IN || (tristate && _direction == INOUT));
    }

    /**
     * Check if this port's direction is input only, meaning IN but not INOUT
     * 
     * @return True if this EdifPort Object is Input only
     */
    public boolean isInputOnly() {
        return (_direction == IN);
    }

    /**
     * Check if this port's direction is output, meaning OUT or INOUT
     * 
     * @return True if this EdifPort Object is Output or InOut
     */
    public boolean isOutput() {
        return (_direction == OUT) || (_direction == INOUT);
    }

    /**
     * Determine whether this port is an output. This method uses a tristate
     * flag to indicate how to handle the tristate or INOUT condition.
     * 
     * @param tristate Indicates that a tri-state port (INOUT) should be
     * considered as an output. If false, this method will return false for
     * ports that are tri-state (INOUT).
     * @return true if the above
     */
    public boolean isOutput(boolean tristate) {
        return (_direction == OUT || (tristate && _direction == INOUT));
    }

    /**
     * Check if this port's direction is output only, meaning OUT but not INOUT
     * 
     * @return True if this EdifPort Object is Output only
     */
    public boolean isOutputOnly() {
        return (_direction == OUT);
    }

    /**
     * Write the EDIF representation of this EdifPort object to the
     * {@link EdifPrintWriter} passed as a parameter.
     * 
     * @param epw an <code>EdifPrintWriter</code> Object where the EDIF data
     * will be written to
     */
    public void toEdif(EdifPrintWriter epw) {

        if (_singleBitPorts.length > 1)
            epw.printIndent("(port (array ");
        else
            epw.printIndent("(port ");

        getEdifNameable().toEdif(epw);

        if (_singleBitPorts.length > 1)
            epw.print(" " + _singleBitPorts.length + ")");

        epw.print(" (direction ");
        switch (_direction) {
        case IN:
            epw.print("INPUT)");
            break;
        case OUT:
            epw.print("OUTPUT)");
            break;
        case INOUT:
            epw.print("INOUT)");
            break;
        }

        Map l = getPropertyList();
        if (l != null) {
            epw.println();
            epw.incrIndent();
            Iterator it = l.values().iterator();
            while (it.hasNext()) {
                Property p = (Property) it.next();
                p.toEdif(epw);
            }
            epw.decrIndent();
            epw.printlnIndent(")");
        } else
            epw.println(")");
    }

    /**
     * Return the string representation of the EdifPort
     * 
     * @return a <code>String</code> value representing this Object, its
     * direction and inner nets
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getEdifNameable());
        if (getWidth() > 1) {
            sb.append("[");
            sb.append(getWidth());
            sb.append("]");
        }
        sb.append(":");
        switch (_direction) {
        case IN:
            sb.append("IN");
            break;
        case OUT:
            sb.append("OUT");
            break;
        case INOUT:
            sb.append("INOUT");
            break;
        default:
            sb.append("UNKNOWN DIRECTION");
        }
        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////
    // Private methods

    /**
     * For each bit position, in order, check to make sure that both EdifPort
     * objects have the same SingleBitPort object in that position, or that both
     * are null.
     * 
     * @param that Comparison EdifPort object
     * @return true if the EdifSingleBitPorts are edifEqual
     */
    private boolean edifEqualsSingleBitPorts(EdifPort that) {
        for (int i = 0; i < this._singleBitPorts.length; i++)
            if (!(this.getSingleBitPort(i) == null ? that.getSingleBitPort(i) == null : this.getSingleBitPort(i)
                    .edifEquals(that.getSingleBitPort(i))))
                return false;

        return true;
    }

    /**
     * This method determines whether the EdifPort object passed in is the same
     * as the other passed-in EdifPort object.
     * <p>
     * This method determines whether the two EdifPort objects are the same. The
     * following criteria are used to make this comparison:
     * <ul>
     * <li> Neither of the two ports are null.
     * <li> The parent cells of both ports match each other.
     * <li> The base name of both ports are the same.
     * <li> The direction of the two ports are the same
     * <li> The width of the two ports are the same
     * </ul>
     * 
     * @param port1 The first port used for comparison
     * @param port2 The second port used for comparison
     * @param ignoreDirection Specifies whether the port directions during a
     * compare will be ignored or not
     * @return True if the two ports match each other
     * @see EdifPort#equals(EdifPort)
     */
    private static boolean equals(EdifPort port1, EdifPort port2, boolean ignoreDirection) {
        if (port1 == null || port2 == null)
            return false;
        if (!NamedObjectCompare.equals(port1.getName(), port2.getName()))
            return false;
        //      if (port1.getBitPosition() != port2.getBitPosition())
        //            return false;
        if (!ignoreDirection && port1.getDirection() != port2.getDirection())
            return false;
        if (ignoreDirection && port1.getDirection() != port2.getDirection())
            System.err.println("Ignoring port compare direction and they differ");
        if (port1.getWidth() != port2.getWidth())
            return false;
        return true;
    }

    /**
     * This method determines whether the EdifPort object passed in is the same
     * as the other passed-in EdifPort object.
     * <p>
     * This method determines whether the two EdifPort objects are the same. The
     * following criteria are used to make this comparison:
     * <ul>
     * <li> Neither of the two ports are null.
     * <li> The parent cells of both ports match each other.
     * <li> The base name of both ports are the same.
     * <li> The direction of the two ports are the same
     * <li> The width of the two ports are the same
     * </ul>
     * 
     * @param port1 The first port used for comparison
     * @param port2 The second port used for comparison
     * @return True if the two ports match each other
     * @see EdifPort#equals(EdifPort,EdifPort,boolean)
     * @see EdifPort#equals(EdifPort)
     */
    private static boolean equals(EdifPort port1, EdifPort port2) {
        return equals(port1, port2, false);
    }

    /**
     * Helper function for the constructor.
     * 
     * @param parent The parent of this new EdifPort
     * @param width The width of this EdifPort
     * @param direction The direction of this EdifPort
     */
    private void _init(EdifCellInterface parent, int width, int direction) {
        _parentInterface = parent;
        _direction = direction;
        _singleBitPorts = new EdifSingleBitPort[width];
        for (int i = 0; i < width; i++)
            _singleBitPorts[i] = new EdifSingleBitPort(this, i);
    }

    ///////////////////////////////////////////////////////////////////
    ////                     private variables                     ////

    /**
     * The direction of this EdifPort
     */
    private int _direction = 0;

    /**
     * The EdifCellInterface to which the EdifPort belongs
     */
    private EdifCellInterface _parentInterface;

    /**
     * The array of single bit ports that make up this port object.
     */
    private EdifSingleBitPort[] _singleBitPorts;

    ///////////////////////////////////////////////////////////////////
    ////                 package private methods                   ////

    /**
     * This method is needed to use a TreeSet of EdifPort objects. In order to
     * use a TreeSet, the Object must be "consistent with equals" meaning ... a
     * class C is said to be "consistent with equals" if and only if
     * (e1.compareTo((Object)e2) == 0) has the same boolean value as
     * e1.equals((Object)e2) for every e1 and e2 of class C The comparison, or
     * sorting for this object for now is going to just be based on the string
     * representation of its name. To enforce "consistent with equals", the
     * method will return 0 if the two objects are equal according to the equals
     * method.
     * 
     * @param port
     * @return the output generated by compareTo(String)
     */
    int compareTo(EdifPort port) {
        if (this.equals(port))
            return 0;
        int compareVal = this.getName().compareTo(port.getName());
        if (compareVal == 0)
            throw new EdifRuntimeException("Port " + this + " and port " + port
                    + " have the same name, but are not equal");
        else
            return compareVal;
    }
}
