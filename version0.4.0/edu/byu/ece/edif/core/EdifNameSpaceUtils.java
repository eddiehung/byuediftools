/*
 * Shared methods among EdifNameSpace implementations
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

import java.io.Serializable;

/////////////////////////////////////////////////////////////////////////
//// EdifNameSpaceUtils
/**
 * An abstract implementation of the EdifNameSpace. This class does not have any
 * implementation specific code (i.e. storage of the name space) but does
 * provide a number of helper functions that simplify the process of creating an
 * EdifNameSpace.
 * 
 * @version $Id:EdifNameSpaceUtils.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class EdifNameSpaceUtils implements Serializable {

    public EdifNameSpaceUtils(EdifNameSpace nameSpace, EdifNameClashPolicy clashPolicy) {
        _nameSpace = nameSpace;
        _clashPolicy = clashPolicy;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Determine whether the given EdifNameable will cause a name clash with the
     * current name space.
     * 
     * @param en the EdifNameable to compare with the name space
     * @return a boolean indicating whether there is a clash
     */
    public boolean nameClash(EdifNameable en) {
        if (en instanceof RenamedObject) {
            RenamedObject ro = (RenamedObject) en;
            return (nameClashOld(ro) || nameClashNew(ro));
        } else {
            NamedObject no = (NamedObject) en;
            return nameClashSingle(no);
        }
    }

    /**
     * Determine whether the given RenamedObject will cause a name clash with an
     * exiting new name in the current name space.
     * 
     * @param en the RenamedObject to compare with the name space
     * @return a boolean indicating whether there is a clash
     */
    public boolean nameClashNew(RenamedObject en) {
        return nameClashNew(en.getName());
    }

    /**
     * Determine whether the given String will cause a name clash with an
     * existing new name in the current name space.
     * 
     * @param newName the String to compare with the name space
     * @return a boolean indicating whether there is a clash
     */
    public boolean nameClashNew(String newName) {
        if (_clashPolicy.oldClashesNew() && _nameSpace.containsOld(newName))
            return true;
        if (_clashPolicy.newClashesNew() && _nameSpace.containsNew(newName))
            return true;
        if (_clashPolicy.singleClashesNew() && _nameSpace.containsSingle(newName))
            return true;
        return false;
    }

    /**
     * Determine whether the given RenamedObject will cause a name clash with an
     * existing old name in the current name space.
     * 
     * @param en the RenamedObject to compare with the name space
     * @return a boolean indicating whether there is a clash
     */
    public boolean nameClashOld(RenamedObject en) {
        return nameClashOld(en.getOldName());
    }

    /**
     * Determine whether the given String will cause a name clash with an
     * existing old name in the current name space.
     * 
     * @param oldName the String to compare with the name space
     * @return a boolean indicating whether there is a clash
     */
    public boolean nameClashOld(String oldName) {
        if (_clashPolicy.oldClashesOld() && _nameSpace.containsOld(oldName))
            return true;
        if (_clashPolicy.oldClashesNew() && _nameSpace.containsNew(oldName))
            return true;
        if (_clashPolicy.singleClashesOld() && _nameSpace.containsSingle(oldName))
            return true;
        return false;
    }

    /**
     * Determine whether the given NamedObject will cause a name clash with an
     * existing single name in the current name space.
     * 
     * @param en the NamedObject to compare with the name space
     * @return a boolean indicating whether there is a clash
     */
    public boolean nameClashSingle(NamedObject en) {
        return nameClashSingle(en.getName());
    }

    /**
     * Determine whether the given String will cause a name clash with an
     * existing single name in the current name space.
     * 
     * @param single the String to compare with the name space
     * @return a boolean indicating whether there is a clash
     */
    public boolean nameClashSingle(String single) {
        if (_clashPolicy.singleClashesOld() && _nameSpace.containsOld(single))
            return true;
        if (_clashPolicy.singleClashesNew() && _nameSpace.containsNew(single))
            return true;
        if (_clashPolicy.singleClashesSingle() && _nameSpace.containsSingle(single))
            return true;
        return false;
    }

    /**
     * Create an EdifNameable object based on the given one that will not clash
     * with any of the existing names in the current name space.
     * 
     * @param en the EdifNameable object to use as a basis for a unique name
     * @return a new unique EdifNameable object
     */
    public EdifNameable returnUniqueNameable(EdifNameable en) {
        if (!nameClash(en))
            return en;
        if (en instanceof RenamedObject) {
            String newName = en.getName();
            String oldName = en.getOldName();
            int origLength = 0;
            if (nameClashOld(oldName)) {
                origLength = newName.length();
                while (nameClashSingle(newName))
                    newName = BasicStringIncrementer.incrementString(newName, origLength);
                try {
                    return new NamedObject(newName);
                } catch (InvalidEdifNameException e) {
                    e.toRuntime();
                }
            } else {
                origLength = newName.length();
                while (nameClashNew(newName))
                    newName = BasicStringIncrementer.incrementString(newName, origLength);
                try {
                    return new RenamedObject(newName, oldName);
                } catch (InvalidEdifNameException e) {
                    e.toRuntime();
                }
            }
        }

        // en must be a single name
        else {
            String name = en.getName();
            int origLength = name.length();
            while (nameClashSingle(name))
                name = BasicStringIncrementer.incrementString(name, origLength);
            try {
                return new NamedObject(name);
            } catch (InvalidEdifNameException e) {
                e.printStackTrace();
            }
        }
        // never get here
        return null;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /**
     * The EdifNameClashPolicy to use for the collection associated with this
     * instance of EdifNameSpaceUtils
     */
    private EdifNameClashPolicy _clashPolicy;

    /**
     * The EdifNameSpace associated with this instance of EdifNameSpaceUtils
     */
    private EdifNameSpace _nameSpace;
}
