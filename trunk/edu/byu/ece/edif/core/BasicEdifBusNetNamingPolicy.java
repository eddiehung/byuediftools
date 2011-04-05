/*
 * Basic implementation of the EdifBusNetNamingPolicy using regular expressions.
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
////BasicEdifBusNetNamingPolicy
/**
 * Basic implementation of the EdifBusNetNamingPolicy using regular expressions.
 * This class performs the parsing on given EdifNet names by comparing the names
 * with predefined regular expressions. The regular expression defines the
 * naming policy.
 * 
 * @author Michael J Wirthlin
 */
public class BasicEdifBusNetNamingPolicy implements EdifBusNetNamingPolicy {

    /**
     * The default constructor will create a naming policy based on the
     * {@link #UNDERSCORE_BITPOS_UNDERSCORE_REGEX}.
     */
    public BasicEdifBusNetNamingPolicy() {
        this(UNDERSCORE_BITPOS_UNDERSCORE_REGEX);
    }

    /**
     * Create a new naming policy based on the given regular expression. The
     * regular expression should have two and only two "groups" (i.e. regular
     * expression units separated by parenthesis). The first group will identify
     * the "basename" and the second group will identify the "bit position".
     */
    public BasicEdifBusNetNamingPolicy(String regex) {
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
     * This regular expression will match against a net name that ends in "_<#>"
     * where <#> is a decimal number. Example: test_5
     */
    public static final String UNDERSCORE_BITPOS_REGEX = "^(.+)_(\\d+)$";

    /**
     * This regular expression will match against a net name that ends in "_<#>_"
     * where <#> is a decimal number. Example: test_5_
     */
    public static final String UNDERSCORE_BITPOS_UNDERSCORE_REGEX = "^(.+)_(\\d+)_$";

    /**
     * This regular expression will match against a net name that ends in "(<#>)"
     * where <#> is a decimal number. Note that this is not a valid EDIF name
     * and is used for the "oldname" value. Example: test(5)
     */
    public static final String PARAN_BITPOS_PARAN_REGEX = "^(.+)\\((\\d+)\\)$";

    /**
     * This regular expression will match against a net name that ends in "[<#>]"
     * where <#> is a decimal number. Note that this is not a valid EDIF name.
     */
    public static final String BRACKET_BITPOS_BRACKET_REGEX = "^(.+)\\[(\\d+)\\]$";
    
    /**
     * This regular expression will match against a net name that end in "<<#>>"
     * where <#> is a decimal number. Note that this is not a valid EDIF name
     * and is used for the "oldname" value. Example: test<5>
     */
    public static final String LESS_BITPOS_GREATER_REGEX = "^(.+)<(\\d+)>$";

    /**
     * This regular expression will match against a net name that ends with
     * <n:n> where n is a decimal number.
     */
    public static final String LESS_GREATER_BUS_NET = "^" + BASENAME_GROUP + "<" + UNSIGNED_INTEGER_GROUP + ":"
            + "(\\2)" + ">" + "$";

    /**
     * This regular expression will match against a net name that ends with
     * (n:n) where n and n is a decimal number.
     */
    public static final String PARANTHESIS_BUS_NET = "^" + BASENAME_GROUP + LEFT_PAREN + UNSIGNED_INTEGER_GROUP + ":"
            + "(\\2)" + RIGHT_PAREN + "$";

    /**
     * This regular expression will match against a net name that ends with
     * [n:n] where n and n is a decimal number.
     */
    public static final String BRACKET_BUS_NET = "^" + BASENAME_GROUP + LEFT_BRACKET + UNSIGNED_INTEGER_GROUP + ":"
            + "(\\2)" + RIGHT_BRACKET + "$";

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Parse a string and identifies the bus net naming scheme that matches the
     * given string. If no bus net naming policy matches the string, this method
     * returns a null.
     */
    public static EdifBusNetNamingPolicy EdifBusNetNamingPolicy(String str) {
        EdifBusNetNamingPolicy policy = null;

        String policy_regex[] = { UNDERSCORE_BITPOS_REGEX, UNDERSCORE_BITPOS_UNDERSCORE_REGEX,
                PARAN_BITPOS_PARAN_REGEX, LESS_BITPOS_GREATER_REGEX, LESS_GREATER_BUS_NET, PARANTHESIS_BUS_NET,
                BRACKET_BUS_NET };

        for (int i = 0; i < policy_regex.length; i++) {
            policy = new BasicEdifBusNetNamingPolicy(policy_regex[i]);
            if (policy.isValidBusNetName(str))
                return policy;
        }
        return null;
    }

    /**
     * Parse the given bus net name to determine its basename according to the
     * policy.
     * 
     * @param name the bus net name to parse
     * @return the basename of the given bus net name
     */
    public String getBusBaseName(String name) {
        Matcher m = getMatcher(name);
        if (m == null)
            return null;
        if (m.groupCount() > 0)
            return m.group(1);
        else
            return null;
    }

    /**
     * Get the base name of a bus net name by first determining which policy it
     * conforms to and then calling the getBusBaseName(String) method on that
     * policy.
     * 
     * @param name the bus net name to parse
     * @return the base name of the bus net name
     */
    public static String getBusBaseNameStatic(String str) {
        EdifBusNetNamingPolicy policy = EdifBusNetNamingPolicy(str);
        if (policy == null)
            return str;
        else
            return policy.getBusBaseName(str);
    }

    /**
     * Given a bit number, create a bit suffix that matches the naming policy
     * 
     * @param bitPos bit position
     */
    public String generateBitSuffix(int bitPos) {
        String result;
        String num = (new Integer(bitPos)).toString();
        String regex = getRegexString();

        if (regex.equals(UNDERSCORE_BITPOS_REGEX)) {
            result = "_" + num;
        } else if (regex.equals(UNDERSCORE_BITPOS_UNDERSCORE_REGEX)) {
            result = "_" + num + "_";
        } else if (regex.equals(LESS_BITPOS_GREATER_REGEX)) {
            result = "<" + num + ">";
        } else { // if (regex.equals(PARAN_BITPOS_PARAN_REGEX)) or anything else
            result = "(" + num + ")";
        }
        return result;
    }

    /**
     * Parse the given bus net name to determine its bit position according to
     * the policy.
     * 
     * @param name the bus net name to parse
     * @return the bit position of the bus net name
     */
    public int getBusPosition(String name) {
        String bit_pos;
        Matcher m = getMatcher(name);

        if (m.groupCount() > 0)
            bit_pos = m.group(2);
        else
            return -1;

        return Integer.parseInt(bit_pos);
    }

    /**
     * Get the regular expression string used to parse bus net names for this
     * policy
     */
    public String getRegexString() {
        return _p.toString();
    }

    /**
     * Determine if the given bus net name is valid according to the policy.
     * 
     * @param name the bus net name to parse
     * @return true iff the given bus net name is valid according to the policy.
     */
    public boolean isValidBusNetName(String name) {
        return _p.matcher(name).matches();
    }

    public static void main(String args[]) {
        String test_names[] = { "test_net_0", "test_net_100", "test_2", "test__4", "test_net(0)", "test_net(100)",
                "test(2)", "test(4)", "test[5:5]", "test_net<0:0>", "test(4:4)", "test[6:3]", "test_net<4:8>",
                "bus(4:9)" };

        for (int i = 0; i < test_names.length; i++) {
            String net = test_names[i];
            EdifBusNetNamingPolicy policy = BasicEdifBusNetNamingPolicy.EdifBusNetNamingPolicy(net);
            System.out.println("Test name: " + net);
            if (policy != null) {
                String baseName = policy.getBusBaseName(net);
                int bit = policy.getBusPosition(net);
                System.out.println("Match: base name = " + baseName + ", bit = " + bit);
            } else {
                System.out.println("No Match");
            }
            System.out.println();
        }

    }

    ///////////////////////////////////////////////////////////////////
    ////                         private methods                   ////

    /**
     * @return a Matcher object based on the policy and given bus net name.
     * @param name the bus net name to parse
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

    /**
     * Local copy of the Pattern.
     */
    private Pattern _p;
}
