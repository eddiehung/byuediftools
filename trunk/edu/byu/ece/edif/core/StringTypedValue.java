/*
 * Represents a String value type in an EDIF structure.
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
////StringTypedValue
/**
 * Represents a String value type in an EDIF structure.
 * 
 * @version $Id:StringTypedValue.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class StringTypedValue implements EdifTypedValue {

    /**
     * Construct a StringTypedValue Object with the specified value.
     * 
     * @param value The value of the new StringTypedValue Object
     */
    public StringTypedValue(String value) {
        _stringValue = value;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    public boolean equals(EdifTypedValue value) {
        if (!(value instanceof StringTypedValue))
            return false;
        return _stringValue.equals(((StringTypedValue) value)._stringValue);
    }

    /**
     * Return the String value of this object
     * 
     * @return A String representing this Object's StringTypedValue
     */
    public String getStringValue() {
        return _stringValue;
    }

    /**
     * Write this object in EDIF format to the passed-in PrintWriter
     * 
     * @param epw The EdifPrintWriter Object that the EDIF will be written to.
     */
    public void toEdif(EdifPrintWriter epw) {
        epw.print("(string \"" + _stringValue + "\")");
    }

    /**
     * Return a String representation of this object.
     * 
     * @return A String representing this object's value
     */
    public String toString() {
        return _stringValue;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /**
     * The String value of this Object
     */
    private String _stringValue;
}
