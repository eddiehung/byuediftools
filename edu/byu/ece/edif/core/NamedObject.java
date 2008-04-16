/*
 * Provides a String name and the ability to create Edif using EdifNameable.
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
////NamedObject
/**
 * A named String object with a valid EDIF name. These names are assumed to be
 * valid EDIF 2.0 String names. This object is immutable.
 * <p>
 * This class has a number of helper functions for evaluating and manipulating
 * Strings according to the EDIF specification.
 * <p>
 * <b>Edif Identifier Specification</b>:
 * <p>
 * <em>Identifier</em> is a basic token type; it is used for name definition,
 * name reference, keywords, and symbolic constants. It contains <b>alphanumeric</b>
 * or <b>underscore</b> characters and must be preceded with an ampersand if
 * the first character is not a letter. This will normally be recognized by a
 * lexical scanner.
 * <p>
 * Case is not significant in identifiers. There are no reserved identifiers,
 * except within <em>keywordNameDef</em>. The length of an identifier must be
 * between 1 and 256 characters, excluding the optional ampersand character.
 * <em>Identifiers</em> are terminated by <em>whiteSpace</em> or by a left
 * or right parenthesis.
 * <p>
 * <em>Rename</em> can be used to express external names which do not conform
 * to the <em>identifier</em> syntax.
 * <p>
 * <b>Example</b>:
 * <p>
 * a12 <br>
 * &a12 <br>
 * A12 <br>
 * abc <br>
 * &12s <br>
 * &120 <br>
 * <p>
 * The first three identifiers in this example are identical. The last two must
 * include the ampersand.
 * 
 * @version $Id:NamedObject.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class NamedObject implements EdifNameable, Cloneable {

    protected NamedObject() {
        _name = null;
    }

    /**
     * Construct a NamedObject with the name string initialized.
     * 
     * @param name String name of NamedObject.
     */
    public NamedObject(String name) throws InvalidEdifNameException {
        if (!isValidEdifName(name))
            throw new InvalidEdifNameException(name);
        _name = name;
    }

    //	public NamedObject(String name) {
    //        _name = createValidEdifString(name);
    //    }

    public NamedObject(EdifNameable n) {
        _name = n.getName();
    }

    ///////////////////////////////////////////////////////////////////
    ////                       public methods                      ////

    /**
     * Perform a "deep" clone of the object. Specifically, create a new String
     * with the same name as the name in this object.
     * 
     * @return An Object that is a copy of clone of this NamedObject
     */
    public Object clone() {
        NamedObject obj = new NamedObject(this);
        return obj;
    }

    /**
     * This method will create a new EdifNameable object from the given string.
     * The object will represent a valid Edif name. In this implementation, a
     * NamedObject will be returned. If an invalid string is passed in, the old
     * name will not be remembered.
     * 
     * @param string The Name of this Object that will be used to create a valid
     * EDIF name if it isn't one already
     * @return An {@link EdifNameable} Object that contains name information
     * about this NamedObject Object
     */
    public static EdifNameable createValidEdifNameable(String string) {
        String validString = createValidEdifString(string);
        EdifNameable newName = null;
        try {
            newName = new NamedObject(validString);
        } catch (InvalidEdifNameException e) {
            // Should never get here
            throw new RuntimeException("Failed to create valid string " + e);
        }
        return newName;
        //return (new NamedObject(createValidEdifString(string)));
    }

    /**
     * This method will check the string to see if it is a valid Edif string. If
     * it is valid, it will return the original string. If it is not valid, it
     * will create a new string that is a valid "variation" of the original
     * string.
     * 
     * @param string The String representation for a Name, which will be used as
     * a base to make the name valid, unless it already is
     * @return A String Object representing a valid EDIF name
     */
    public static String createValidEdifString(String string) {

        if (string == null || string.equals(""))
            return null;

        if (isValidEdifName(string))
            return string;

        //if (string == null)
        //    return "new_edif_name";

        String newValidString = "";
        //1. Check if the first letter is valid, if valid, copy it to the new
        //   string, if not, add an ampersand at the beginning of the new
        //   string.
        //   If the first letter is valid, step 2 will check the remaining 
        //   letters; if the first letter is not valid, step 2 will check all 
        //   the letters.
        char c = string.charAt(0);
        int startLetter = 1;

        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '&'))
            newValidString += c;
        else {
            newValidString += "&";
            startLetter = 0;
        }

        //2. Check every letter, if it is valid( alphanumeric or underscore ),
        //   append it to the new string; if not valid, we replace it with an 
        //   underscore. And we make sure the new string length is between 1
        //   and 256 (excluding the starting ampersand if there is one)
        for (int i = startLetter; i < string.length(); i++) {
            c = string.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || (c == '_'))
                newValidString += c;
            else
                newValidString += "_";

            if (i == 255)
                break;
        }

        return newValidString;
    }

    /**
     * Compare this Object's name to the given Object.
     * 
     * @param cmp The Object to compare to this one
     * @return True if the name of this Object equals the name of the passed in
     * one
     */
    public boolean equals(Object cmp) {
        return NamedObjectCompare.equals(this, cmp);
    }

    /**
     * Return the String name.
     * 
     * @return A String Object representing the name of this NamedObject
     */
    public String getName() {
        return _name;
    }

    public String getOldName() {
        return _name;
    }

    /**
     * Test if the input string is a valid Edif name
     * 
     * @param string the input string
     * @return true if it is a valid Edif name, false if it is invalid
     */
    public static boolean isValidEdifName(String string) {

        //1. Check if the string is null
        if (string == null || string.equals(""))
            return false;

        //2. Check if the first letter is a letter or an ampersand
        char c = string.charAt(0);
        if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '&')))
            return false;

        //3. Check the string length according to the first letter
        int validLength = 256;
        if (c == '&')
            validLength = 257;
        if (string.length() > validLength)
            return false;

        //4. Check if the remaining letters are alphanumerics or underscores
        for (int i = 1; i < string.length(); i++) {
            c = string.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || (c == '_')))
                return false;
        }

        return true;
    }

    /**
     * Convert this object to EDIF format, and write the data to the passed-in
     * EdifPrintWriter Object.
     * 
     * @param epw The EdifPrintWriter that the EDIF data will be written to
     */
    public void toEdif(EdifPrintWriter epw) {
        epw.print(getName());
    }

    /**
     * Return the name of the object (same as the {@link NamedObject#getName}
     * method).
     * 
     * @return a String representing this object
     */
    public String toString() {
        return getName();
    }

    ///////////////////////////////////////////////////////////////////
    ////                    private variables                      ////

    /**
     * The String name of this object.
     */
    protected String _name;
}
