/*
 * Implements a name space using an ArrayList.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

//////////////////////////////////////////////////////////////////////////
////ArrayListNameSpaceResolver
/**
 * Implements a name space using an ArrayList.
 * 
 * @see AbstractNameSpaceResolver
 * @see NameSpaceResolver
 * @version $Id: ArrayListNameSpaceResolver.java 130 2008-03-31 16:23:42Z
 * jamesfcarroll $
 */

public class ArrayListNameSpaceResolver extends ArrayList implements NameSpaceResolver {

    /**
     * Construct an ArrayListNameSpaceResolver Object with the default initial
     * capacity of 10.
     */
    public ArrayListNameSpaceResolver() {
        super();
    }

    /**
     * Construct an ArrayListNameSpaceResolver Object with a specified initial
     * capacity.
     * 
     * @param initialCapacity The initial capacity of the ArrayList
     */
    public ArrayListNameSpaceResolver(int initialCapacity) {
        super(initialCapacity);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods

    /**
     * Return true if the name doesn't clash and was added to the name space at
     * the specified position.
     * 
     * @param position Adds the name object to the specified position.
     * @param name Name to add to the name space.
     * @return True if the name was added to the name space, otherwise, false.
     */
    public boolean add(int position, NamedPropertyObject name) {
        if (!nameClash(name.getName())) {
            add(position, (Object) name);
            return true;
        }
        return false;
    }

    /**
     * Return true if the name doesn't clash and was added to the name space.
     * 
     * @param name Name to add to the name space.
     * @return True if the name was added to the name space, otherwise, false.
     */
    public boolean add(NamedPropertyObject name) {
        return add(size(), name);
    }

    /**
     * This method will return the next object in the ordered list. This method
     * will return null if the collection does not contain the object parameter
     * or if the object parameter is the last member of the list.
     * 
     * @param o The object to find the next value of
     * @return The object immediately after the passed-in one in this ArrayList
     * Object
     */
    public Object getNextValue(Object o) {
        if (!contains(o))
            return null;
        for (Iterator i = iterator(); i.hasNext();) {
            Object n = i.next();
            if (n == o) {
                // found the object in the list. See if there is a
                // successor.
                if (i.hasNext())
                    return i.next();
                else
                    // no successor, return null;
                    return null;
            }
        }
        return null;
    }

    /**
     * This method will return a collection of Value objects in the ordered list
     * that occur <em>after</em> the object parameter. This method will return
     * null if the collection does not contain the object parameter. If the
     * parameter is the last object in the list, it will return an empty
     * Collection.
     * 
     * @param o The object to find the next values of
     * @return A Collection of all objects after the passed-in one in this
     * ArrayList Object
     */
    public Collection getNextValues(Object o) {
        if (!contains(o))
            return null;
        ArrayList nextvalues = new ArrayList(size());
        boolean foundobject = false;
        for (Iterator i = iterator(); i.hasNext();) {
            Object n = i.next();
            if (foundobject) {
                nextvalues.add(n);
            } else {
                if (n == o) {
                    foundobject = true;
                }
            }
        }
        return nextvalues;
    }

    /**
     * This method will return the previous Value object in the ordered list.
     * This method will return null if the collection does not contain the
     * object parameter or if the object parameter is the first member of the
     * list.
     * 
     * @param o The object to find the previous value of
     * @return The object immediately before the passed-in one in this ArrayList
     * Object
     */
    public Object getPreviousValue(Object o) {
        Object previous = null;
        if (!contains(o))
            return null;
        for (Iterator i = iterator(); i.hasNext();) {
            Object n = i.next();
            if (n == o) {
                return previous;
            }
            previous = n;
        }
        return null;
    }

    /**
     * This method will return a collection of Value objects in the ordered list
     * that occur <em>before</em> the object parameter. This method will
     * return null if the collection does not contain the object parameter. If
     * the parameter is the first object in the list, it will return an empty
     * Collection.
     * 
     * @param o The object to find the previous values of
     * @return A Collection of all objects before the passed-in one in this
     * ArrayList Object
     */
    public Collection getPreviousValues(Object o) {
        if (!contains(o))
            return null;
        ArrayList previousvalues = new ArrayList(size());

        for (Iterator i = iterator(); i.hasNext();) {
            Object n = i.next();
            if (n == o) {
                return previousvalues;
            }
            previousvalues.add(n);
        }
        // should never get here
        return previousvalues;
    }

    public static void main(String[] args) throws EdifNameConflictException {
        ArrayListNameSpaceResolver nsr = new ArrayListNameSpaceResolver();
        EdifEnvironment env = new EdifEnvironment(NamedObject.createValidEdifNameable("env"));
        EdifLibrary lib = new EdifLibrary(env.getLibraryManager(), NamedObject.createValidEdifNameable("library"));
        String n = "abc";
        EdifCell name = new EdifCell(lib, NamedObject.createValidEdifNameable(n));
        for (int i = 0; i < 6; i++) {
            if (!nsr.add(i, name)) {
                n = AbstractNameSpaceResolver.returnUniqueName(nsr, n);
                name = new EdifCell(lib, NamedObject.createValidEdifNameable(n));
                nsr.add(i, name);
            }
        }

        System.out.println(nsr.toString());
    }

    /**
     * Returns true if name does not clash with name space.
     * 
     * @param name Name to check if it already exists in the name space.
     * @return True if the name clashes, false if it doesn't exist in the name
     * space.
     */
    public boolean nameClash(String name) {
        return AbstractNameSpaceResolver.nameClash(this, name);
    }

    /**
     * If the name is valid, return name. If not, make it valid. It grabs a
     * random element from an array and keeps appending it from the array.
     * 
     * @param name Name to modify, and make unique for this name space.
     * @return The string unique to this name space.
     */
    public String returnUniqueName(String name) {
        return AbstractNameSpaceResolver.returnUniqueName(this, name);
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representing this name space object.
     */
    @Override
    public String toString() {
        return AbstractNameSpaceResolver.toString(this);
    }
}
