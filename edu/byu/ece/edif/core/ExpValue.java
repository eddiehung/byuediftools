/*
 * Represents a base number with an exponent value.
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
////ExpValue
/**
 * Represents a base number with an exponent value. This object is immutable.
 * 
 * @version $Id:ExpValue.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class ExpValue extends ScaledInteger {

    /**
     * Construct an ExpValue Object with the specified base and exponent.
     * 
     * @param base The base number
     * @param exp The base number is raised to this exponent
     */
    public ExpValue(int base, int exp) {
        super(base);
        this._exp = exp;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    public boolean equals(EdifTypedValue value) {
        if (!(value instanceof ExpValue))
            return false;
        ExpValue e = (ExpValue) value;
        return (_integerValue == e._integerValue) && (_exp == e._exp);
    }

    /**
     * Return the base value of this object.
     * 
     * @return An int representing the base value of this object
     */
    public int getBase() {
        return super.getIntegerValue();
    }

    /**
     * Return the exponent of this object.
     * 
     * @return An int representing what exponent the base number is raised to
     * (in powers of 10)
     */
    public int getExp() {
        return _exp;
    }

    /**
     * Return 10 raised to the exponent multiplied by the base number.
     * 
     * @return An int representing (10^exp)*base
     */
    public double getValue() {
        return java.lang.Math.pow(10, _exp) * getBase();
    }

    /**
     * Convert this object to EDIF format and write it to the passed in
     * EdifPrintWriter Object.
     * 
     * @param epw The EdifPrintWriter Object that the EDIF data will be written
     * to
     */
    public void toEdif(EdifPrintWriter epw) {
        epw.print("(number " + this.toString() + ")");
    }

    /**
     * Return a String representation of this Object.
     * 
     * @return A String representing this object's base and exponent
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append("e ");
        sb.append(getBase());
        //sb.append(" e ");
        sb.append(" ");
        sb.append(_exp);
        sb.append(")");
        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////
    ////                     private variables                     ////

    /**
     * The exponent (in powers of 10) of the base number.
     */
    private int _exp;
}
