/*
 * Basic implementation of the EdifBusNamingPolicy using regular expressions.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//////////////////////////////////////////////////////////////////////////
////BasicEdifBusNamingPolicy
/**
 * Basic implementation of the EdifBusNamingPolicy using regular expressions.
 * This class performs the parsing on given EdifNet names by comparing the names
 * with predefined regular expressions. The regular expressions define the
 * naming policy.
 * 
 * @author Michael J Wirthlin
 */
public class BasicEdifBusNamingPolicy implements EdifBusNamingPolicy {

    /**
     * Create a new naming policy based on the given regular expression. The
     * regular expression should have three and only three "groups" (i.e.
     * regular expression units separated by parenthesis). The first group will
     * identify the "basename", the second group will identify the "left
     * position", and the third will identify the "right position".
     */
    public BasicEdifBusNamingPolicy(String regex) {
        _p = Pattern.compile(regex);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public variables                  ////

    public static final String LEFT_PAREN = "\\(";

    public static final String RIGHT_PAREN = "\\)";

    public static final String LEFT_BRACKET = "\\[";

    public static final String RIGHT_BRACKET = "\\]";

    public static final String UNSIGNED_INTEGER_GROUP = "(\\d+)";

    public static final String BASENAME_GROUP = "(.+)";

    /**
     * This regular expression will match against a bus name that ends with
     * <n:m> where n and m are decimal numbers.
     */
    public static final String LESS_GREATER_BUS = "^" + BASENAME_GROUP + "<" + UNSIGNED_INTEGER_GROUP + ":"
            + UNSIGNED_INTEGER_GROUP + ">" + "$";

    /**
     * This regular expression will match against a bus name that ends with
     * (n:m) where n and m are decimal numbers.
     */
    public static final String PARANTHESIS_BUS = "^" + BASENAME_GROUP + LEFT_PAREN + UNSIGNED_INTEGER_GROUP + ":"
            + UNSIGNED_INTEGER_GROUP + RIGHT_PAREN + "$";

    /**
     * This regular expression will match against a bus name that ends with
     * [n:m] where n and m are decimal numbers.
     */
    public static final String BRACKET_BUS = "^" + BASENAME_GROUP + LEFT_BRACKET + UNSIGNED_INTEGER_GROUP + ":"
            + UNSIGNED_INTEGER_GROUP + RIGHT_BRACKET + "$";

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Parses a string and identifies the bus naming scheme that matches the
     * given string. If no bus naming policy matches the string, this method
     * returns a null.
     */
    public static EdifBusNamingPolicy EdifBusNamingPolicy(String str) {
        EdifBusNamingPolicy policy = null;

        String policy_regex[] = { PARANTHESIS_BUS, BRACKET_BUS, LESS_GREATER_BUS };

        for (int i = 0; i < policy_regex.length; i++) {
            policy = new BasicEdifBusNamingPolicy(policy_regex[i]);
            if (policy.isValidBusName(str))
                return policy;
        }
        return null;
    }

    /**
     * Parse the given bus name to determine its basename according to the
     * policy.
     * 
     * @param name the bus name to parse
     * @return the basename of the given bus name
     */
    public String getBusBaseName(String name) {
        Matcher m = getMatcher(name);

        if (m.groupCount() > 0)
            return m.group(1);
        else
            return null;
    }

    /**
     * Get the base name of a bus name by first determining which policy it
     * conforms to and then calling the getBusBaseName(String) method on that
     * policy.
     * 
     * @param name the bus name to parse
     * @return the base name of the bus name
     */
    public static String getBusBaseNameStatic(String name) {
        EdifBusNamingPolicy policy = EdifBusNamingPolicy(name);
        if (policy == null)
            return name;
        else
            return policy.getBusBaseName(name);
    }

    /**
     * @return the portion of the bus string that specifies the range of the
     * bus. This String does not include the basename.
     */
    public String getBusRangeSpecifier(String name) {
        String baseName = getBusBaseName(name);
        if (baseName == null)
            return null;
        return name.substring(baseName.length());
    }

    /**
     * @return the integer value of the "left" bus limit within the bus string
     * name (i.e. will return a "4" for the following naming example "(4:8)").
     * Will return a non-negative value if the string is valid. Otherwise, it
     * returns -1.
     */
    public int getLeftBusLimit(String name) {
        String bit_pos;
        Matcher m = getMatcher(name);

        if (m.groupCount() > 0)
            bit_pos = m.group(2); // left limit
        else
            return -1;

        return Integer.parseInt(bit_pos);
    }

    /**
     * Get the regular expression String from which this policy was created
     */
    public String getRegexString() {
        return _p.toString();
    }

    /**
     * @return the integer value of the "right" bus limit within the bus string
     * name (i.e. will return a "8" for the following naming example "(4:8)").
     * Will return a non-negative value if the string is valid. Otherwise, it
     * returns -1.
     */
    public int getRightBusLimit(String name) {
        String bit_pos;
        Matcher m = getMatcher(name);

        if (m.groupCount() > 0)
            bit_pos = m.group(3); // right limit
        else
            return -1;

        return Integer.parseInt(bit_pos);
    }

    /**
     * Determine the bit ordering of the given EDIF bus name. Little endian
     * implies that the "little" end of the bus (i.e. bit 0) comes first. Big
     * endian implies that the "big" end of the bus comes first (i.e. bit 31).
     * LittleEndian:(31:0) BigEndian: (0:31)
     */
    public boolean isLittleEndian(String name) {
        if (isValidBusName(name)) {
            if (getLeftBusLimit(name) > getRightBusLimit(name))
                return true;
        }
        return false;
    }

    /**
     * Determine whether the given String is a valid Bus name using the
     * implemented bus naming policy.
     */
    public boolean isValidBusName(String name) {
        // JMJ 3/12/08
        // added && (getLeftBusLimit(name) != getRightBusLimit(name)) so that
        // single bit slices won't be valid bus names. They should be recognized
        // as net names, not bus names
        return _p.matcher(name).matches() && (getLeftBusLimit(name) != getRightBusLimit(name));
    }

    public static void main(String args[]) {
        String test_names[] = { "test_net(0:50)", "test_net_(100:2)", "test_2", "test__4", "test_net[0:50]",
                "test_net_[100:2]", "test_net<0:50>", "test_net_<100:2>", };

        for (int i = 0; i < test_names.length; i++) {
            String str = test_names[i];
            System.out.print(str + ":");
            EdifBusNamingPolicy policy = BasicEdifBusNamingPolicy.EdifBusNamingPolicy(str);
            if (policy == null) {
                System.out.println("No Match");
            } else {
                System.out.print(" Basename=\"" + policy.getBusBaseName(str) + "\"");
                System.out.print(" Range=\"" + policy.getBusRangeSpecifier(str) + "\"");
                System.out.print(" left=" + policy.getLeftBusLimit(str) + " right=" + policy.getRightBusLimit(str));
                if (policy.isLittleEndian(str))
                    System.out.print(" Little Endian");
                else
                    System.out.print(" Big Endian");
                System.out.println();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private methods                   ////

    /**
     * @return a Matcher object based on the policy and given bus name.
     * @param name the bus name to parse
     */
    private Matcher getMatcher(String name) {
        Matcher m = _p.matcher(name);
        if (m.matches())
            return m;
        else
            return null;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    private Pattern _p;
}
