/*
 * Signifies that the EDIF file has a Boolean value type.
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

//////////////////////////////////////////////////////////////////////////
////BooleanTypedValue
/**
 * Signifies that the EDIF file has a Boolean value type.
 * 
 * @version $Id:BooleanTypedValue.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class BooleanTypedValue implements EdifTypedValue {

    /**
     * Construct a BooleanTypedValue Object with the specified value.
     * 
     * @param value The value of this BooleanTypeValue Object
     */
    public BooleanTypedValue(boolean value) {
        _booleanValue = value;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    public boolean equals(EdifTypedValue value) {
        if (!(value instanceof BooleanTypedValue))
            return false;
        return _booleanValue == ((BooleanTypedValue) value)._booleanValue;
    }

    /**
     * Return the boolean value of this object.
     * 
     * @return A boolean representing the value of this BooleanTypeValue Object
     */
    public boolean getBooleanValue() {
        return _booleanValue;
    }

    /**
     * Convert this object to EDIF format and write it to the passed-in
     * EdifPrintWriter.
     * 
     * @param epw The EdifPrintWriter Object that the EDIF data will be written
     * to
     */
    public void toEdif(EdifPrintWriter epw) {
        epw.print("(boolean (" + _booleanValue + "))");
    }

    /**
     * Return a String representation of this object.
     * 
     * @return A String representing this BooleanTypedValue Object
     */
    @Override
    public String toString() {
        return Boolean.toString(_booleanValue);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /**
     * The boolean value of this object
     */
    private boolean _booleanValue;
}
