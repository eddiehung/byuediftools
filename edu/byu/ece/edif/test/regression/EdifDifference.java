/*
 * This object represents a single difference between two EDIF Objects.
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
package edu.byu.ece.edif.test.regression;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifLibrary;

/**
 * This object represents a single difference between two EDIF Objects.
 * <p>
 * Presently, differences are stored simply as two String objects: the first is
 * unique to the item on the left of the comparison. The second is unique to the
 * item on the right.
 * <p>
 * There are 11 classes in the BYU EDIF Hierarchy. This object can represent any
 * set of differences between any two objects in that hierarchy. For example,
 * two {@link EdifLibrary} objects may have five {@link EdifCell} objects that
 * are equal, and two EdifCell objects that aren't equal. In this case, the set
 * of differences between the two EdifLibrary objects is the two EdifCell
 * objects. If two EDIF objects are equal, then the set of differences between
 * them is the empty set.
 * <p>
 * Note that the set of differences between two EDIF objects of different types
 * (different classes) is nonsensical. Such sets would generally consist of the
 * union of the two EDIF objects; that is, every member of both objects would be
 * in the set of differences. Such difference sets are highly discouraged.
 * <p>
 * TODO: We would like to define this Set to only contain "EDIF Hierarchy"
 * objects; however, there is no class nor interface that encompasses all of the
 * 11 classes; rather, 9 of the 11 implement the EdifOut interface, and the
 * other two don't. So, the todo item is to resolve or remove this oddity from
 * the BYU EDIF Tools.
 * 
 * @author james
 */
public class EdifDifference {

    public EdifDifference(String A, String B) {
        this.A = A;
        this.B = B;
    }

    public EdifDifference(Object A, Object B) {
        this.A = (A == null) ? "NULL" : A.toString();
        this.B = (B == null) ? "NULL" : B.toString();
    }

    /**
     * Add the same string to both; this is for differences such as "Door object
     * differs" or "See documentation"
     * 
     * @param both
     * @deprecated
     */
    public void addDifference(String both) {
        this.A = both;
        this.B = both;
    }

    public String toString() {
        return "< " + A + newline + "> " + B + newline; // diff-like output
    }

    private String A;

    private String B;

    private static final String newline = System.getProperty("line.separator");
}
