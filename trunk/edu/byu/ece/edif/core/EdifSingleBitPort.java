/*
 * Represents an Edif single bit port, which belongs to a specific EdifPort.
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

/////////////////////////////////////////////////////////////////////////
////EdifSingleBitPort
/**
 * Represents an Edif single bit port, which belongs to a specific
 * {@link EdifPort}. This class specifies a bit position and a reference to its
 * parent EdifPort.
 * 
 * @version $Id:EdifSingleBitPort.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class EdifSingleBitPort implements Serializable {

    /**
     * Constructor requires
     * 
     * @param parent The parent EdifPort of this single-bit port
     * @param position The bit-position of this single-bit port
     */
    public EdifSingleBitPort(EdifPort parent, int position) {
        //super(parent.getName() + "<" + position + ">");
        _parent = parent;
        _bitPosition = position;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    public int bitPosition() {
        return _bitPosition;
    }

    /**
     * Two EdifSingleBitPort objects are considered edifEqual if:
     * <ul>
     * <li> They have the same bit position</li>
     * </ul>
     * 
     * @param o Comparison EdifSingleBitPort object
     * @return true of the two EdifSingleBitPort objects are edifEqual
     */
    public boolean edifEquals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EdifSingleBitPort))
            return false;
        EdifSingleBitPort that = (EdifSingleBitPort) o;

        return this.bitPosition() == that.bitPosition();
    }

    /**
     * @param esp
     * @return true if the Port names, parent EdifPorts, and InnerNets are all
     * the same object. Also checks the bitposition.
     */
    public boolean equals(EdifSingleBitPort esp) {
        return (esp.getPortName() == getPortName() && esp.getParent() == getParent()
                && esp.getInnerNet() == getInnerNet() && esp.bitPosition() == bitPosition());
    }

    /**
     * @override java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EdifSingleBitPort))
            return false;
        EdifSingleBitPort that = (EdifSingleBitPort) o;
        return this.equals(that);
    }

    /**
     * Search through the EdifNet objects owned by the EdifCell that uses this
     * EdifSinglebitPort and find the EdifNet object that connects to this
     * top-level port. Note that this method finds the net connecting to the
     * top-level port -- this method does not look for "outer nets" that connect
     * to EdifCellInstnace objects.
     * 
     * @return The EdifNet object that connects to this single bit port.
     */
    public EdifNet getInnerNet() {
        for (EdifNet net : this.getParent().getEdifCell().getNetList()) {
            if (net.isAttached(null, this))
                return net;
        }
        return null;
    }

    public EdifPort getParent() {
        return _parent;
    }

    public String getPortName() {
        StringBuffer sb = new StringBuffer();

        sb.append(_parent.getName());
        if (_parent.getWidth() > 1) {
            sb.append("<" + _bitPosition + ">");
        }

        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_parent.getEdifNameable());
        sb.append("[");
        sb.append(_bitPosition);
        sb.append("]");
        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////
    ////                    private variables                      ////

    /**
     * Position in multi-bit EdifPort
     */
    private int _bitPosition;

    /**
     * Parent EdifPort
     */
    private EdifPort _parent;
}
