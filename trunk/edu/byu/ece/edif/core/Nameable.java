/*
 * Used for objects that can be named with a String name.
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
////Nameable
/**
 * Used for objects that can be named with a String name. Objects that implement
 * this interface can provide a String name.
 * 
 * @version $Id:Nameable.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public interface Nameable {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Return the String name.
     * 
     * @return A String representing the name of the interfacing object
     */
    public String getName();
}
