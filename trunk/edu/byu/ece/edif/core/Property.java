/*
 * A String name/value pair to describe an EDIF property.
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
import java.util.Map;

/////////////////////////////////////////////////////////////////////////
////Property
/**
 * Specifies a String name/value pair to describe an EDIF property. The Property
 * name defines the name of a given property and the value defines the actual
 * value assigned to the property.
 * 
 * @version $Id:Property.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class Property implements Nameable, EdifOut, Serializable {

    public Property(EdifNameable name, EdifTypedValue setValue) {
        _name = name;
        _value = setValue;
    }

    /**
     * Construct a Property Object with the specified name and value as a
     * {@link EdifTypedValue}.
     * 
     * @param setName The name of this new Property Object
     * @param setValue The value of this new Property Object
     */
    public Property(String setName, EdifTypedValue setValue) {
        this(RenamedObject.createValidEdifNameable(setName), setValue);
    }

    /**
     * Construct a Property Object with the specified name and value as a
     * {@link StringTypedValue}.
     * 
     * @param setName The name of this new Property Object
     * @param str The String value of this new Property Object
     */
    public Property(String setName, String str) {
        this(setName, new StringTypedValue(str));
    }

    /**
     * Construct a Property Object with the specified name and value as a
     * {@link BooleanTypedValue}.
     * 
     * @param setName The name of this new Property Object
     * @param bool The boolean value of this new Property Object
     */
    public Property(String setName, boolean bool) {
        this(setName, new BooleanTypedValue(bool));
    }

    /**
     * Construct a Property Object with the specified name and value as an
     * {@link IntegerTypedValue}.
     * 
     * @param setName The name of this new Property Object
     * @param i The integer value of this new Property Object
     */
    public Property(String setName, int i) {
        this(setName, new IntegerTypedValue(i));
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Clone this Property Object and return a clone of it.
     * 
     * @return An Object that is a clone of this Property Object
     */
    public Object clone() {
        return new Property(getName(), getValue());
    }

    /**
     * Return True if this Property matches the passed-in one, making sure they
     * are the same type, value, etc.
     * 
     * @param p The Property Object to compare this one to
     * @return True if this Property Object matches the passed-in one
     */
    public boolean equals(Property p) {
        if (!NamedObjectCompare.equals(getName(), p.getName()))
            return false;
        EdifTypedValue cmp1 = getValue();
        EdifTypedValue cmp2 = p.getValue();
        if (!cmp1.getClass().getName().equals(cmp2.getClass().getName()))
            return false;
        return cmp1.equals(cmp2);
    }

    /**
     * Return True if this Property Object matches with one of the Objects in
     * the passed-in Map Object. The Property's name is used as key.
     * 
     * @param properties A list of properties to match against with this one
     * @return True if this Property Object matches with one of the Property
     * Objects contained in the passed-in Map Object
     */
    public boolean existsWithin(Map<String, Property> properties) {
        Property val = properties.get(this.getName());
        if (val == null) // Check lowercase
            val = properties.get(this.getName().toLowerCase());
        if (val == null)
            return false;
        if (!this.equals(val))
            return false;
        return true;
    }

    /**
     * Return the name of this Object.
     * 
     * @return A String Object representing the name of this Property Object.
     */
    public String getName() {
        return _name.getName();
    }

    /**
     * Return the typed value of this object.
     * 
     * @return A {@link EdifTypedValue} Object representing the typed value of
     * this Property Object
     */
    public EdifTypedValue getValue() {
        return _value;
    }

    /**
     * Set the typed value of this object.
     * 
     * @param val The new typed value of this object
     */
    public void setValue(EdifTypedValue val) {
        _value = val;
    }

    /**
     * Convert this Property Object to EDIF format and write it to the passed-in
     * EdifPrintWriter Object.
     * 
     * @param epw The EdifPrintWriter Object that the EDIF will be written to.
     */
    public void toEdif(EdifPrintWriter epw) {
        epw.printIndent("(property " + _name + " ");
        _value.toEdif(epw);
        epw.println(")");
    }

    /**
     * Return a String representation of this object.
     * 
     * @return A String object representing this Property Object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(_name);
        sb.append(" = ");
        sb.append(_value);
        sb.append("]");
        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////
    ////                     private variables                     ////

    /**
     * The actual name of the property
     */
    private EdifNameable _name;

    /**
     * The actual String value of the property
     */
    private EdifTypedValue _value;

}
