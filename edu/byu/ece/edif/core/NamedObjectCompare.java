/*
 * Compares NamedObject objects.
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

import java.util.Comparator;

/////////////////////////////////////////////////////////////////////////
////NamedObjectCompare
/**
 * Compares NamedObject objects.
 * 
 * @see NamedObject
 * @version $Id:NamedObjectCompare.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class NamedObjectCompare implements Comparator {

    ///////////////////////////////////////////////////////////////////
    ////                         public variables                  ////

    /**
     * Variable specifying whether string compares should be case sensitive or
     * not.
     */
    public static boolean CASE_SENSITIVE = false;

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Compare two Objects by their String name.
     * 
     * @param o1 The first Object for comparison
     * @param o2 The second Object for comparison
     * @return An int value which specifies the result of the String comparison
     */
    public int compare(Object o1, Object o2) {
        getStrings(o1, o2);
        return s1.compareTo(s2);
    }

    /**
     * Compare two Objects by their String name.
     * 
     * @param o1 The first Object for comparison
     * @param o2 The second Object for comparison
     * @return True if o1 equals o2
     */
    public static boolean equals(Object o1, Object o2) {
        if (o1 instanceof MultiNamedObject)
            return ((MultiNamedObject) o1).equals(o2);
        if (o2 instanceof MultiNamedObject)
            return ((MultiNamedObject) o2).equals(o1);
        getStrings(o1, o2);
        return s1.equals(s2);
    }

    /**
     * Compare string o2 with the beginning of o1.
     * 
     * @param o1 The first Object for comparison
     * @param o2 The second Object for comparison
     * @return True if o1 starts with o2
     */
    public static boolean startsWith(Object o1, Object o2) {
        if (o1 instanceof MultiNamedObject)
            return ((MultiNamedObject) o1).startsWith(o2);
        if (o2 instanceof MultiNamedObject)
            return ((MultiNamedObject) o2).startsWith(o1);

        getStrings(o1, o2);
        return s1.startsWith(s2);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private methods                   ////

    /**
     * Initialize the two strings used for comparison by the methods in this
     * class.
     * 
     * @param o1 The first Object for comparison
     * @param o2 The second Object for comparison
     */
    private static void getStrings(Object o1, Object o2) {
        if (o1 instanceof NamedPropertyObject)
            s1 = ((NamedPropertyObject) o1).getName();
        else if (o1 instanceof String)
            s1 = (String) o1;
        else if (o1 instanceof NamedObject)
            s1 = ((NamedObject) o1).getName();
        else if (o1 instanceof RenamedObject)
            s1 = ((RenamedObject) o1).getName();
        else
            s1 = o1.toString();

        if (o2 instanceof NamedPropertyObject)
            s2 = ((NamedPropertyObject) o2).getName();
        else if (o2 instanceof String)
            s2 = (String) o2;
        else if (o2 instanceof NamedObject)
            s2 = ((NamedObject) o2).getName();
        else if (o2 instanceof RenamedObject)
            s2 = ((RenamedObject) o2).getName();
        else
            s2 = o2.toString();

        if (!CASE_SENSITIVE) {
            s1 = s1.toLowerCase();
            s2 = s2.toLowerCase();
        }
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /**
     * The two strings initialized, and then used for comparison by methods in
     * this class.
     */
    private static String s1, s2;
}
