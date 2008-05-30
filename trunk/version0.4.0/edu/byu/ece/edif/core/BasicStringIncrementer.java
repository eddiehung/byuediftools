/*
 * Provides methods for "incrementing" a String value.
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

//////////////////////////////////////////////////////////////////////////
////BasicStringIncrementer
/**
 * Provides methods for "incrementing" a String value. Incrementing is a
 * convenient way of creating unique String names in a deterministic manner.
 */

public class BasicStringIncrementer {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * This method will perform an "increment" on a character. This method will
     * sequence from 'a' to 'z' and then from '0' to '9'. After reaching '9',
     * this method will return back to 'a'. This method will return 0 if the
     * character is invalid.
     * <p>
     * This method is case insensitive and will return a lower case incremented
     * character.
     * 
     * @param c The character to increment.
     * @return The incremented character
     */
    public static char incrementDigit(char c) {
        if (c >= 'A' && c <= 'Z')
            // convert to lower case
            c = (char) (c - ('A' - 'a'));
        if (c >= 'a' && c < 'z')
            return (char) (c + 1);
        if (c == 'z')
            return '0';
        if (c >= '0' && c < '9')
            return (char) (c + 1);
        else if (c == '9')// roll over
            return 'a';
        else
            return 'a';
    }

    /**
     * Provide a new String
     * 
     * @return An incremented version of the passed in String object
     */
    public static String incrementString(String str) {
        char[] charStr = str.toCharArray();

        //System.out.println("orig = "+str);

        boolean rollOver = true;
        for (int i = charStr.length - 1; i >= 0 && rollOver; i--) {
            char newChar = incrementDigit(charStr[i]);
            charStr[i] = newChar;
            if (newChar != 'a') { // no need to rollover
                rollOver = false;
            }
            //System.out.println("i="+i+"-"+newChar+"="+new String(charStr)+" "+rollOver);
        }
        String newString;
        //if (rollOver)
        //	newString =  "blah" + new String(charStr);
        newString = new String(charStr);
        if (rollOver)
            newString = "a" + newString;
        //System.out.println("new="+newString);
        return newString;
    }

    /**
     * Increment a string by appending a "_" + num to the end of it. If there is
     * already an "_" at the end, the number is simply incremented. The original
     * length of the string should be passed in so that the method will be able
     * to determine if the "_" has already been added by this method or not.
     * 
     * @param str the String to increment
     * @param origLength the original length of the string to increment
     * @return an incremented version of the string
     */
    public static String incrementString(String str, int origLength) {
        int length = str.length();
        if (length < origLength)
            throw new EdifRuntimeException("invalid use of incrementString(String, int)");
        if (length > origLength && !str.substring(origLength, origLength + 1).equals("_"))
            throw new EdifRuntimeException("invalid use of incrementString(String, int)");

        char[] charStr = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        sb.append(str.substring(0, origLength));

        if (length == origLength)
            sb.append("_0");
        else {
            sb.append(str.substring(origLength, origLength + 1));
            int num = Integer.parseInt(str.substring(origLength + 1, length));
            num++;
            sb.append(num);
        }
        return sb.toString();
    }
}
