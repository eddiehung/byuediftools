/*
 * Implements an unordered name space using a HashMap.
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

import java.util.LinkedHashMap;

/////////////////////////////////////////////////////////////////////////
////HashMapNameSpaceResolver
/**
 * Implements an unordered name space using a HashMap.
 * 
 * @see AbstractNameSpaceResolver
 * @see NameSpaceResolver
 * @version $Id: HashMapNameSpaceResolver.java 130 2008-03-31 16:23:42Z
 * jamesfcarroll $
 */
public class HashMapNameSpaceResolver extends LinkedHashMap implements NameSpaceResolver {

    /**
     * Construct an empty HashMapNameSpaceResolver Object.
     */
    public HashMapNameSpaceResolver() {
        super();
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Add a name if it doesn't clash with an existing name and return true. If
     * it does clash, return false and don't add.
     * 
     * @param name Name to add to the name space.
     * @return True if the name was added to the name space, otherwise, false.
     */
    public boolean add(NamedPropertyObject name) {
        if (!nameClash(name.getName())) {
            put(name.getName().toLowerCase(), name);
            return true;
        }
        return false;
    }

    /**
     * Return the Object associated with the given String.
     * 
     * @param name The key of the value to retrieve
     * @return An Object mapped at the given String
     */
    public Object get(String name) {
        return super.get(name.toLowerCase());
    }

    public static void main(String[] args) throws EdifNameConflictException {
        HashMapNameSpaceResolver nsr = new HashMapNameSpaceResolver();

        String n = "abc";
        EdifEnvironment env = new EdifEnvironment(NamedObject.createValidEdifNameable("env"));
        EdifLibrary lib = new EdifLibrary(env.getLibraryManager(), NamedObject.createValidEdifNameable("env"));
        EdifCell name = new EdifCell(lib, NamedObject.createValidEdifNameable(n));

        for (int i = 0; i < 6; i++) {
            if (!nsr.add(name)) {
                n = nsr.returnUniqueName(n);
                name = new EdifCell(lib, NamedObject.createValidEdifNameable(n));
                nsr.add(name);
            }
        }

        System.out.println(nsr.toString());
    }

    /**
     * Return true if name does not clash with name space.
     * 
     * @param name Name to check if it already exists in the name space.
     * @return True if the name clashes, false if it doesn't exist in the name
     * space.
     */
    public boolean nameClash(String name) {
        return AbstractNameSpaceResolver.nameClash(this, name.toLowerCase());
    }

    /**
     * Remove the value mapped at the given String key.
     * 
     * @param name The key of the value to remove
     * @return The Object mapped at the given String
     */
    public Object remove(String name) {
        return super.remove(name.toLowerCase());
    }

    /**
     * If the name is valid, return name. If not, make it valid. It grabs a
     * random element from an array and keeps appending it from the array.
     * 
     * @param name Name to modify, and make unique for this name space.
     * @return The string unique to this name space.
     */
    public String returnUniqueName(String name) {
        return AbstractNameSpaceResolver.returnUniqueName(this, name.toLowerCase());
    }

    /**
     * Return a string representation of this object.
     * 
     * @return String representing this name space object.
     */
    public String toString() {
        return AbstractNameSpaceResolver.toString(this.values());
    }
}
