/*
 * A List implementation of the EdifNameSpace interface.
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

/////////////////////////////////////////////////////////////////////////
//// EdifNameSpaceList
/**
 * A List implementation of the EdifNameSpace interface.
 * 
 * @version $Id:EdifNameSpaceList.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
@SuppressWarnings("serial")
public class EdifNameSpaceList<E extends NamedPropertyObject> extends ArrayList<E> implements EdifNameSpace<E> {

    /**
     * Construct an EdifNameSpaceList with the given EdifNameClashPolicy.
     * 
     * @param clashPolicy the clash policy to use for determining if two names
     * clash
     */
    public EdifNameSpaceList(EdifNameClashPolicy clashPolicy) {
        _util = new EdifNameSpaceUtils(this, clashPolicy);
    }

    /**
     * Construct an EdifNameSpaceList with the given EdifNameClashPolicy and
     * size.
     * 
     * @param clashPolicy the clash policy to use for determining if two names
     * clash
     * @param size the original size of the list
     */
    public EdifNameSpaceList(EdifNameClashPolicy clashPolicy, int size) {
        super(size);
        _util = new EdifNameSpaceUtils(this, clashPolicy);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    // javadoc inherited from interface
    public void addElement(E element) throws EdifNameConflictException {
        if (!nameClash(element))
            super.add(element);
        else
            throw new EdifNameConflictException(element);
    }

    // javadoc inherited from interface
    public boolean contains(E element) {
        return super.contains(element);
    }

    public boolean containsName(String name) {
        for (E namedObject : this) {
            if (namedObject.getName().equals(name))
                return true;
        }

        return false;
    }

    // javadoc inherited from interface
    public E get(String str) {
        for (E element : this) {
            EdifNameable en = element.getEdifNameable();
            if (en.getName().equalsIgnoreCase(str))
                return element;
        }
        return null;
    }

    // javadoc inherited from interface
    public EdifNameable getUniqueNameable(E element) {
        return _util.returnUniqueNameable(element.getEdifNameable());
    }

    // javadoc inherited from interface
    public EdifNameable getUniqueNameable(EdifNameable en) {
        return _util.returnUniqueNameable(en);
    }

    // javadoc inherited from interface
    public boolean nameClash(E element) {
        return _util.nameClash(element.getEdifNameable());
    }

    // javadoc inherited from interface
    public boolean nameClash(EdifNameable en) {
        return _util.nameClash(en);
    }

    // javadoc inherited from interface
    public boolean containsNew(String str) {
        E element = get(str); // get is case insensitive
        if (element == null)
            return false;
        EdifNameable en = element.getEdifNameable();
        if (!(en instanceof RenamedObject))
            return false;
        return true;
    }

    public boolean containsOld(String str) {
        for (E element : this) {
            EdifNameable en = element.getEdifNameable();
            if ((en instanceof RenamedObject) && en.getOldName().equals(str))
                return true;
        }
        return false;
    }

    // javadoc inherited from interface
    public boolean containsSingle(String str) {
        E element = get(str); // get is case insensitive
        if (element == null)
            return false;
        EdifNameable en = element.getEdifNameable();
        if (en == null || !(en instanceof NamedObject))
            return false;
        if (en instanceof RenamedObject)
            return false;
        if (en instanceof MultiNamedObject)
            return false;
        return true;
    }

    /**
     * Contains methods common to all EdifNameSpace implementations.
     */
    private EdifNameSpaceUtils _util;
}
