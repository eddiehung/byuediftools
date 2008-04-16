/*
 * TODO: Insert class description here.
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

/**
 * When doing a compare on this object, it has multiple names, so the String
 * passed-in for comparison will be compared against all of its names.
 * 
 * @author Mike Wirthlin, Tyler Anderson
 * @version $Id:MultiNamedObject.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class MultiNamedObject extends NamedObject implements Cloneable {

    /**
     * Constructs a MultiNamedObject with the specified names as its names.
     * 
     * @param names The names of this object
     */
    public MultiNamedObject(String[] names) throws InvalidEdifNameException {
        super(names[0]);
        if (names.length > 1) {
            _additionalNames = new ArrayList<String>(names.length - 1);
            for (String name : names) {
                if (NamedObject.isValidEdifName(name))
                    throw new InvalidEdifNameException(name);
                _additionalNames.add(name);
            }
        }
    }

    //    public Object clone() {
    //        String[] names = new String[_additionalNames.size() + 1];
    //        names[0] = super.getName();
    //        for (int i = 0; i < names.length; i++)
    //            names[i + 1] = (String) _additionalNames.get(i);
    //        return new MultiNamedObject(names);
    //    }

    /**
     * Returns true if the passed-in String matches one of the names in this
     * MultiNamedObject.
     * 
     * @param cmp A String Object to compare against the names in this Object
     * @return True if the passed-in String matches one of the names in this
     * object
     */
    public boolean equals(Object cmp) {
        if (NamedObjectCompare.equals((NamedObject) this, cmp))
            return true;
        if (_additionalNames != null) {
            for (String name : _additionalNames) {
                if (NamedObjectCompare.equals(name, cmp))
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the passed-in String matches one of the names in this
     * MultiNamedObject.
     * 
     * @param cmp A String Object to compare against the names in this Object
     * @return True if the passed-in String matches one of the names in this
     * object
     */
    public boolean startsWith(Object cmp) {
        if (NamedObjectCompare.startsWith((NamedObject) this, cmp))
            return true;
        if (_additionalNames != null) {
            for (String name : _additionalNames) {
                if (NamedObjectCompare.startsWith(name, cmp))
                    return true;
            }
        }
        return false;
    }

    /**
     * A list of additional names for this Object.
     */
    protected ArrayList<String> _additionalNames = null;
}
