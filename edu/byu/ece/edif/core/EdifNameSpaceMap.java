/*
 * A map implementation of the EdifNameSpace interface.
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/////////////////////////////////////////////////////////////////////////
//// EdifNameSpaceMap
/**
 * A map implementation of the EdifNameSpace interface.
 * 
 * @version $Id:EdifNameSpaceMap.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
@SuppressWarnings("serial")
public class EdifNameSpaceMap<E extends NamedPropertyObject> extends LinkedHashMap<String, E> implements
        EdifNameSpace<E>, Iterable<E> {

    /**
     * Construct an EdifNameSpaceMap with the given EdifNameClashPolicy.
     * 
     * @param clashPolicy the clash policy to use for determining if two names
     * clash
     */
    public EdifNameSpaceMap(EdifNameClashPolicy clashPolicy) {
        _util = new EdifNameSpaceUtils(this, clashPolicy);
    }

    /**
     * Construct an EdifNameSpaceMap with the given EdifNameClashPolicy and
     * size.
     * 
     * @param clashPolicy the clash policy to use for determining if two names
     * clash
     * @param size the original size of the list
     */
    public EdifNameSpaceMap(EdifNameClashPolicy clashPolicy, int size) {
        super(size);
        _util = new EdifNameSpaceUtils(this, clashPolicy);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    // javadoc inherited from interface
    public void addElement(E element) throws EdifNameConflictException {
        if (!nameClash(element)) {
            EdifNameable en = element.getEdifNameable();
            put(en.getName().toLowerCase(), element);
            if (en instanceof RenamedObject)
                _oldNameToElement.put(en.getOldName(), element);
            _containsValues.add(element);
        } else
            throw new EdifNameConflictException(element);
    }

    // javadoc inherited from interface
    public boolean contains(E element) {
        return _containsValues.contains(element);
//        return containsValue(element);
    }

    // javadoc inherited from interface
    public E get(String str) {
        String convStr = str.toLowerCase();
        if (containsKey(convStr))
            return super.get(convStr);
        return null;
    }

    public E getOld(String str) {
        return _oldNameToElement.get(str); // old names are NOT case insensitive
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
    public Iterator<E> iterator() {
        return values().iterator();
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

    // javadoc inherited from interface
    public boolean containsOld(String str) {
        E element = getOld(str); // getOld is case sensitive
        if (element == null)
            return false;
        return true;
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
     * Remove an object of type E from the name space
     * 
     * @return true if the object was successfully removed from the name spce
     */
    public boolean remove(E element) {
        for (Iterator<String> keyIt = keySet().iterator(); keyIt.hasNext();) {
            String key = keyIt.next();
            if (get(key) == element) {
                keyIt.remove();
                for (Iterator<String> oldKeyIt = _oldNameToElement.keySet().iterator(); oldKeyIt.hasNext();) {
                    String oldKey = oldKeyIt.next();
                    if (_oldNameToElement.get(oldKey) == element)
                        oldKeyIt.remove();
                }
                _containsValues.remove(element);
                return true;
            }
        }
        return false;
    }

    // javadoc inherited from interface
    public void trimToSize() {
    }

    /**
     * Contains methods common to all EdifNameSpace implementations.
     */
    private EdifNameSpaceUtils _util;

    /**
     * Used to map from old names to elements contained in the map for faster
     * lookup.
     */
    private HashMap<String, E> _oldNameToElement = new LinkedHashMap<String, E>();
    
    /**
     * Used to speedup "contains" queries (otherwise they would require using
     * "containsValue" which iterates over every value in the map.
     */
    private LinkedHashSet<E> _containsValues = new LinkedHashSet<E>();
}
