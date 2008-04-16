/*
 * Provides a LinkedHashMap of Property objects.
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

import java.util.Iterator;
import java.util.LinkedHashMap;

/////////////////////////////////////////////////////////////////////////
////PropertyList
/**
 * Provides a {@link java.util.LinkedHashMap} of Property objects. The key of
 * this Map is a lower-case String object (EDIF is case insensitive) and the
 * value is a Property object. The key for all objects in this Map is
 * property.getName().toLowerCase().
 * 
 * @see Property
 * @version $Id:PropertyList.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class PropertyList extends LinkedHashMap<String, Property> implements EdifOut {

    /**
     * Construct a PropertyList Object with an initial capacity of 1 and a load
     * factor 0.95.
     */
    public PropertyList() {
        super(1, 1.0f);
    }

    ///////////////////////////////////////////////////////////////////
    ////                      public methods                       ////

    /**
     * Add a Property to the PropertyList; the key is the Property's name.
     * 
     * @param p a <code>Property</code> value representing the property to add
     * to this PropertyList Object
     */
    public void addProperty(Property p) {
        put(p.getName().toLowerCase(), p);
    }

    /**
     * Add a list of properties to the PropertyList
     * 
     * @param list The PropertyList object that contains the list of properties
     * to add to this list.
     */
    public void addPropertyList(PropertyList list) {
        this.putAll(list);
    }

    /**
     * Return an Object that is a clone of this one.
     * 
     * @return an Object that is a clone of this PropertyList Object
     */
    public Object clone() {
        PropertyList list = new PropertyList();
        for (Property p : values()) {
            list.addProperty((Property) p.clone());
        }
        return list;
    }

    /**
     * Return true if this PropertyList equals or matches the passed-in one.
     * 
     * @param cmp The PropertyList Object to compare this one to
     * @return True if this Object matches the passed-in one
     */
    public boolean equals(PropertyList cmp) {
        if (cmp == null || super.values().size() != cmp.values().size())
            return false;

        for (Property p : super.values()) {
            if (!p.existsWithin(cmp))
                return false;
        }
        return true;
    }

    /**
     * Get the Property according to the Property's name
     * 
     * @param str a <code>String</code> value specifying the desired property.
     * This String will be converted to lower case as the key in this Map are
     * Strings in lower case.
     * @return a <code>Property</code> value representing the property
     */
    public Property getProperty(String str) {
        return (Property) get(str.toLowerCase());
    }

    /**
     * Generate the EDIF text for this list of properties.
     * 
     * @param epw The EdifPrintWriter object used for writing the Edif.
     */
    public void toEdif(EdifPrintWriter epw) {
        for (Property p : this.values())
            p.toEdif(epw);
    }

    /**
     * Return a String representation of this Object.
     * 
     * @return A String Object representing this PropertyList Object
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Iterator<Property> it = values().iterator(); it.hasNext();) {
            sb.append(it.next());
            if (it.hasNext())
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
