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
package edu.byu.ece.edif.util.graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import edu.byu.ece.edif.core.EdifCellInstance;

public class SingleInstanceCollection implements EdifCellInstanceCollection, Serializable {

    public SingleInstanceCollection(EdifCellInstance eci) {
        _instance = eci;
    }

    public SingleInstanceCollection(Collection c) {
        if (c == null || c.size() == 0)
            _instance = null;
        else
            _instance = (EdifCellInstance) c.iterator().next();
    }

    public int size() {
        return 1;
    }

    public void clear() {
        throw new UnsupportedOperationException("Cannot clear single instance");
    }

    public boolean isEmpty() {
        return false;
    }

    public Object[] toArray() {
        EdifCellInstance e[] = new EdifCellInstance[1];
        e[0] = _instance;
        return e;
    }

    public boolean add(EdifCellInstance o) {
        throw new UnsupportedOperationException("Cannot add to single instance");
    }

    public boolean contains(Object o) {
        if (o == _instance)
            return true;
        return false;
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Cannot remove single instance");
    }

    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException("Cannot add to single instance");
    }

    public boolean containsAll(Collection c) {
        for (Iterator i = c.iterator(); i.hasNext();)
            if (!contains(i.next()))
                return false;
        return true;
    }

    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException("Cannot remove single instance");
    }

    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException("Cannot remove single instance");
    }

    public Iterator iterator() {
        return new SingleInstanceIterator(this);
    }

    public Object[] toArray(Object[] a) {
        // Throw null if the specified array is null
        if (a == null)
            throw new NullPointerException("");

        if (a.length >= 1) {
            a[0] = _instance;
            return a;
        } else {
            a = new EdifCellInstance[1];
            a[0] = _instance;
            return a;
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(_instance.toString() + "\n");
        return sb.toString();
    }

    protected EdifCellInstance _instance;

}

class SingleInstanceIterator implements Iterator {

    public SingleInstanceIterator(SingleInstanceCollection sic) {
        _instance = sic._instance;
    }

    public boolean hasNext() {
        if (_instance != null)
            return true;
        return false;
    }

    public Object next() {
        Object rval = _instance;
        _instance = null;
        return rval;
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove from single instance");
    }

    protected EdifCellInstance _instance;

}