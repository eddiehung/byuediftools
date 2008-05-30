/*
 * A group of Nameable objects each with a unique String name.
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

/////////////////////////////////////////////////////////////////////////
//// EdifNameSpace
/**
 * A group of Nameable objects each with a unique String name. The group
 * specifies a name space of unique names for EdifNameable objects. Each name in
 * the space is unique and methods are provided for creating and checking for
 * uniqueness. Each unique name is associated with a EdifNameable object.
 * 
 * @version $Id:EdifNameSpace.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public interface EdifNameSpace<E extends NamedPropertyObject> extends Trimable {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Add an EdifNameable object to the name space. This method will check to
     * see if there is a conflict and throw an exception if there is a conflict.
     * 
     * @param e Add the named object to the name space. The name of the object
     * is associated with name of the NamedPropertyObject.
     * @throws EdifNameConflictException This exception is thrown when a name
     * conflict occurs during the add.
     */
    public void addElement(E element) throws EdifNameConflictException;

    /**
     * Indicate whether the given EdifNameable object is a member of the name
     * space. The search is performed using the name of the object.
     * 
     * @param e The NamedPropertyObject to search for in the name space.
     * @return true if the object is a member of the space. false if it is not a
     * member of the space.
     */
    public boolean contains(E element);

    /**
     * Return the Nameable object associated with this name space that matches
     * the given String name.
     * 
     * @param name
     * @return The Nameable object within this name space. This will return a
     * null if an object of the given name does not exist.
     */
    public E get(String name);

    /**
     * Determine a name based on the name of the given element that will not
     * clash with any names already in the name space.
     * 
     * @param element the element whose name will be used as a basis for a
     * unique name
     * @return a unique name
     */
    public EdifNameable getUniqueNameable(E element);

    /**
     * Determine a name based on the given name that will not clash with any
     * names already in the name space.
     * 
     * @param en the name to use as a basis for a unique name.
     * @return a unique name
     */
    public EdifNameable getUniqueNameable(EdifNameable en);

    /**
     * Return an Iterator for all Nameable objects in the name space
     * 
     * @return Iterator over all Nameable objects in the name space.
     */
    public Iterator<E> iterator();

    /**
     * Determine if the given elements name will clash with the names already in
     * the name space.
     * 
     * @param element the element whose name will be compared with the name
     * space
     * @return a boolean indicating whether there will be a clash
     */
    public boolean nameClash(E element);

    /**
     * Determine if the given name will clash with the names in this given
     * EdifNameSpace.
     * 
     * @param en the name that will be compared with the name space
     * @return a boolean indicating whether there will be a clash
     */
    public boolean nameClash(EdifNameable en);

    /**
     * Indicate whether the name space contains a new name (part of a
     * RenamedObject) that matches the given string.
     * 
     * @param name the string to match against
     * @return a boolean indicating whether a match was found
     */
    public boolean containsNew(String name);

    /**
     * Indicate whether the name space contains an old name (part of a
     * RenamedObject) that matches the given string.
     * 
     * @param name the string to match against
     * @return a boolean indicating whether a match was found
     */
    public boolean containsOld(String name);

    /**
     * Indicate whether the name space contains a single name (as a NamedObject)
     * that matches the given string.
     * 
     * @param name the string to match against
     * @return a boolean indicating whether a match was found
     */
    public boolean containsSingle(String name);

    /**
     * Indicate the number of members of the name space
     * 
     * @return The number of elements in the name space.
     */
    public int size();
}
