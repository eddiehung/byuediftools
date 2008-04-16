/*
 * Represents Edif names that are renamed using the RENAME keyword.
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
//// RenamedObject
/**
 * Represents Edif names that are renamed using the RENAME keyword. This class
 * actually contains two names: The first name is the "Edif" name that will be
 * used by the EDIF infrastructure. This name is accessed/changed using the
 * NamedObject super class methods. The second name is the "old name" or the
 * original name that was renamed. This is often an invalid EDIF name that
 * required renaming for internal use. This object is immutable.
 * <p>
 * Sample Edif:
 * 
 * <pre>
 *  (rename diff_0 &quot;diff(0)&quot;)
 * </pre>
 * 
 * @version $Id:RenamedObject.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class RenamedObject extends NamedObject implements Cloneable {

    public RenamedObject(String name) {
        super();
        _oldname = name;
        if (isValidEdifName(name)) {
            _name = name;
        } else {
            _name = createValidEdifString(name);
        }
    }

    /**
     * Construct an initialized RenamedObject.
     * 
     * @param name The Edif name (new name)
     * @param oldname The old original name that needed renaming
     */
    public RenamedObject(String name, String oldname) throws InvalidEdifNameException {
        super(name);
        _oldname = oldname;
    }

    public RenamedObject(EdifNameable name, String oldname) {
        super(name);
        _oldname = oldname;
    }

    public RenamedObject(RenamedObject obj) {
        _name = obj._name;
        _oldname = obj._oldname;
    }

    ///////////////////////////////////////////////////////////////////
    ////                      public methods                       ////

    /**
     * Provide a deep clone of this object. New strings are created for the
     * cloned object.
     */
    public Object clone() {
        RenamedObject obj = new RenamedObject(this);
        return obj;
    }

    /**
     * This method will create a new EdifNameable object from the given string.
     * The object will represent a valid Edif name. In this implementation, a
     * NamedObject will be returned if the string is a valid Edif string. If an
     * invalid string is passed in, a RenamedObject will be returned with the
     * old invalid name stored in the old name field of the RenamedObject.
     * 
     * @param string The string that will be used to make a valid and unique
     * EDIF name
     * @return And EdifNameable Object, containing the name, or new name and old
     * name
     */
    public static EdifNameable createValidEdifNameable(String string) {
        EdifNameable en;

        String newString = NamedObject.createValidEdifString(string);

        try {
            if (newString.equals(string))
                en = new NamedObject(string);
            else
                en = new RenamedObject(newString, string);
        } catch (InvalidEdifNameException e) {
            // should never get here
            throw new RuntimeException(e);
        }

        return en;
    }

    public boolean equals(Object cmp) {
        return NamedObjectCompare.equals(this, cmp);
    }

    /**
     * Return the old name.
     * 
     * @return A String object containing the old name of this object before it
     * was renamed
     */
    public String getOldName() {
        return _oldname;
    }

    /**
     * Convert this object to EDIF format, and write it to the passed in
     * EdifPrintWriter
     * 
     * @param epw The EdifPrintWriter Object that the EDIF data will be written
     * to
     */
    public void toEdif(EdifPrintWriter epw) {
        // If this is really only a single name, call
        // super.toEdif
        if (_oldname == _name || _oldname == null || _oldname.equals(_name))
            super.toEdif(epw);
        else
            // Else a valid renamed object
            epw.print("(rename " + getName() + " \"" + getOldName() + "\")");
    }

    /**
     * Return a String representation of this object.
     * 
     * @return a String object that represents this renamed object
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(rename ");
        sb.append(getName());
        sb.append(" \"");
        sb.append(getOldName());
        sb.append("\")");
        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////
    ////                    private variables                      ////

    /**
     * The old name that had to be renamed.
     */
    private String _oldname;
}
