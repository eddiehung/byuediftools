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
package edu.byu.ece.edif.arch;

import java.util.Iterator;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;

public class Primitives {

    /**
     * Tag all leaf cells of a library as primitive.
     */
    public static void tagLeafCellsAsPrimitives(EdifLibrary lib) {
        for (Iterator i = lib.iterator(); i.hasNext();) {
            EdifCell cell = (EdifCell) i.next();
            if (cell.isLeafCell())
                cell.setPrimitive();
        }
    }

    /**
     * Tag primitives in this library based on whether or not they match cells
     * in the passed in library.
     * 
     * @param primitives The library that contains primitives that will be
     * compared against all cells in this library to match with primitives
     */
    public static void tagPrimitives(EdifLibrary lib, EdifLibrary primitives) {
        for (Iterator i = lib.iterator(); i.hasNext();) {
            EdifCell cell = (EdifCell) i.next();
            cell.tagAsPrimitive(primitives);
        }
    }

    /**
     * Tag all primitive cells by comparing them to the cells contained within
     * the passed-in library.
     * 
     * @param primitives Library containing primitives to be compared to cells.
     * @see EdifCell#tagAsPrimitive
     */
    public static void tagPrimitives(EdifLibraryManager elm, EdifLibrary primitives) {
        for (Iterator i = elm.iterator(); i.hasNext();) {
            EdifLibrary lib = (EdifLibrary) i.next();
            tagPrimitives(lib, primitives);
        }
    }

    /**
     * Tag all leaf cells as primitive.
     */
    public static void tagLeafCellsAsPrimitives(EdifLibraryManager elm) {
        for (Iterator i = elm.iterator(); i.hasNext();) {
            EdifLibrary lib = (EdifLibrary) i.next();
            Primitives.tagLeafCellsAsPrimitives(lib);
        }
    }

}
