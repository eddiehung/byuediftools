/*
 * Abstract class that contains methods for name space resolvers.
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

//////////////////////////////////////////////////////////////////////////
////AbstractNameSpaceResolver
/**
 * Abstract class that contains methods for name space resolvers. Other name
 * space classes will call the methods of this one. If another data type is
 * desired for the name space then in the constructor of the other name space
 * object _names is instantiated. This class is abstract and doesn't and doesn't
 * implement an interface because interface methods can't be declared abstract.
 * <p>
 * TODO: this class supports both maps and collections. This should probably be
 * split up.
 * 
 * @version $Id: AbstractNameSpaceResolver.java 130 2008-03-31 16:23:42Z
 * jamesfcarroll $
 */

public abstract class AbstractNameSpaceResolver {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods

    public static void main(String args[]) {
        String testStrings[] = { "test", "test_a", "test_z", "test_0", "test_A", "test_Z", "test_9", "test_a9",
                "test_aa", "test_af", "test_99", "test_c9", "test_d99", "test_999" };

        for (int i = 0; i < testStrings.length; i++)
            test_string(testStrings[i]);
    }

    /**
     * Return true if name clashes with the name space.
     * 
     * @param newName Name to check if it already exists in the name space.
     * @param list The name space passed to this method.
     * @return True if the name clashes, false if it doesn't exist in the name
     * space.
     */
    public static boolean nameClash(Collection<NamedPropertyObject> list, String newName) {
        for (NamedPropertyObject name : list) {
            if (name.getName().toLowerCase().equals(newName.toLowerCase()))
                //if(NamedObjectCompare.equals(name, newName))
                return true;
        }
        return false;
    }

    /**
     * Return true if name clashes with name space.
     * 
     * @param newName Name to check if it already exists in the name space.
     * @param map The name space passed to this method.
     * @return True if the name clashes, false if it doesn't exist in the name
     * space.
     */
    public static boolean nameClash(Map map, String newName) {
        return (map.get(newName) != null);
    }

    /**
     * The algorithm for creating a new unique name is as follows: - If the
     * conflicting name does not have a "_", add one to the end as well as "a" -
     * Change the last character and "increment" by one (i.e. "a->b") -
     */
    public static String returnNextString(String orig) {
        int underscore_index = orig.lastIndexOf('_');

        if (underscore_index == -1) {
            // If no _<value>, start off with _a
            return orig + "_a";
        }

        String uniquePart = orig.substring(underscore_index + 1, orig.length());
        String nextUnique = BasicStringIncrementer.incrementString(uniquePart);

        return new String(orig.substring(0, underscore_index) + "_" + nextUnique);
    }

    /**
     * If the name is valid, return name. If not, make it valid. It returns the
     * next available name by using the returnNextString method.
     * 
     * @param name Name to modify, and make unique for this name space.
     * @param list The name space passed to this method.
     * @return The string unique to this name space.
     */
    public static String returnUniqueName(Collection list, String name) {
        while (nameClash(list, name)) {
            name = returnNextString(name);
        }
        return name;
    }

    /**
     * If the name is valid, return name. If not, make it valid. It grabs a
     * random element from an array and keeps appending it from the array.
     * 
     * @param name Name to modify, and make unique for this name space.
     * @param map The name space passed to this method.
     * @return The string unique to this name space.
     */
    public static String returnUniqueName(Map map, String name) {
        while (nameClash(map, name)) {
            name = returnNextString(name);
        }
        return name;

        //        if (nameClash(map, name)) {
        //            name += "_";
        //            name += _random[(int) (Math.random() * (_random.length - 1))];
        //        }
        //        while (nameClash(map, name))
        //            name += _random[(int) (Math.random() * (_random.length - 1))];
        //        return name;

    }

    public static void test_string(String str) {
        String newString = returnNextString(str);
        System.out.println("Orig=" + str + " New=" + newString);
    }

    /**
     * Return a string representation of this object.
     * 
     * @return String representing this name space object.
     */
    public static String toString(Collection list) {
        StringBuffer sb = new StringBuffer();
        EdifNameable name;
        sb.append("Names: [ ");
        for (Iterator nameIterator = list.iterator(); nameIterator.hasNext();) {
            name = ((NamedPropertyObject) nameIterator.next()).getEdifNameable();
            sb.append(name);
            if (nameIterator.hasNext())
                sb.append(", ");
        }
        sb.append(" ]");
        return sb.toString();
    }
}
