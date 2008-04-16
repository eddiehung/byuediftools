/*
 * Represents an Integer value type in an EDIF structure.
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

/////////////////////////////////////////////////////////////////////////
////IntegerTypedValue
/**
 * Signifies that the EDIF file has an Integer value type.
 * 
 * @version $Id:IntegerTypedValue.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class IntegerTypedValue implements EdifTypedValue {

    /**
     * Construct an IntegerTypedValue Object with an initial value as the
     * passed-in value.
     * 
     * @param value The initial value of this IntegerTypedValue Object
     */
    public IntegerTypedValue(int value) {
        _integerValue = value;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    public boolean equals(EdifTypedValue value) {
        if (!(value instanceof IntegerTypedValue))
            return false;
        return _integerValue == ((IntegerTypedValue) value)._integerValue;
    }

    /**
     * Return the Integer value of this Object.
     * 
     * @return An int specifying the value of this IntegerTypedValue Object.
     */
    public int getIntegerValue() {
        return _integerValue;
    }

    /**
     * Convert this object to EDIF format and write it to the passed in
     * EdifPrintWriter Object.
     * 
     * @param epw The EdifPrintWriter Object that the EDIF data will be written
     * to
     */
    public void toEdif(EdifPrintWriter epw) {
        epw.print("(integer " + _integerValue + ")");
    }

    /**
     * Return a String representation of this object.
     * 
     * @return A String representing this IntegerTypedValue Object
     */
    public String toString() {
        return Integer.toString(_integerValue);
    }

    ///////////////////////////////////////////////////////////////////
    ////                     private variables                     ////

    /**
     * Contains the value of this IntegerTypedValue Object
     */
    private int _integerValue;
}
