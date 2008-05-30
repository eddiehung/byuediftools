/*
 * An interface for objects that can be annotated with Properties.
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
////Properties
/**
 * Defines an interface for objects that can be annotated with Properties.
 * 
 * @version $Id:Properties.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public interface Properties {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Add a property to the object.
     * 
     * @param p The property to add
     */
    public void addProperty(Property p);

    /**
     * Copy all the properties associated with the passed in Properties object
     * into this Properties object.
     * 
     * @param p
     */
    public void copyProperties(Properties p);

    /**
     * Obtain a property from the object.
     * 
     * @param str The property name that is wanted
     */
    public Property getProperty(String str);

    /**
     * Return a list of the object properties.
     * 
     * @return A {@link PropertyList} Object containing all the properties for
     * the interfacing object
     */
    public PropertyList getPropertyList();
}
