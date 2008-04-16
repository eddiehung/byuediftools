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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import edu.byu.ece.edif.core.EdifCellInstance;

public class MultipleInstanceCollection extends HashSet<EdifCellInstance> implements EdifCellInstanceCollection {

    public MultipleInstanceCollection() {
        super();
        _id = _instantiationCount++; // See hashCode() method
    }

    public MultipleInstanceCollection(Collection c) {
        super(c);
        _id = _instantiationCount++; // See hashCode() method
    }

    public MultipleInstanceCollection(EdifCellInstanceCollection ecic) {
        super(ecic);
        _id = _instantiationCount++; // See hashCode() method
    }

    public MultipleInstanceCollection(EdifCellInstanceCollection c1, EdifCellInstanceCollection c2) {
        super();
        _id = _instantiationCount++; // See hashCode() method
        addAll(c1);
        addAll(c2);
    }

    //	// MultipleInstanceCollection objects may cause problems if they are 
    //	//  modified (their HashCode changes), so these methods ensure that
    //	//  this class is immutable.
    //	public void clear() {
    //		throw new UnsupportedOperationException("Cannot modify MultipleInstanceCollection");
    //	}
    //	public boolean add(Object o) {
    //		throw new UnsupportedOperationException("Cannot modify MultipleInstanceCollection");
    //	}
    //	public boolean remove(Object o) {
    //		throw new UnsupportedOperationException("Cannot modify MultipleInstanceCollection");
    //	}
    //	public boolean addAll(Collection c) {
    //		throw new UnsupportedOperationException("Cannot modify MultipleInstanceCollection");
    //	}
    //	public boolean removeAll(Collection c) {
    //		throw new UnsupportedOperationException("Cannot modify MultipleInstanceCollection");
    //	}
    //	public boolean retainAll(Collection c) {
    //		throw new UnsupportedOperationException("Cannot modify MultipleInstanceCollection");
    //	}

    /**
     * The default hashCode() method does not return the same value if this
     * Collection is changed. This overriding method ensures that each
     * MultipleInstanceCollection object has a unique, unchanging hash code.
     */
    public int hashCode() {
        return _id;
    }

    /**
     * The equals method should work the same as the hashCode method. Make sure
     * the object is of this type as well, just in case the fabricated hashCodes
     * happen to match others.
     */
    public boolean equals(Object obj) {
        return (_id == obj.hashCode() && obj instanceof MultipleInstanceCollection);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = this.iterator(); i.hasNext();) {
            EdifCellInstance eci = (EdifCellInstance) i.next();
            sb.append(eci.toString() + " ");
        }
        sb.append("\n");
        return sb.toString();
    }

    private int _id;

    private static int _instantiationCount = 0;
}
