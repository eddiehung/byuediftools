/*
 * This class will create a Xilinx primitive library.
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
package edu.byu.ece.edif.arch.xilinx;

import java.util.Iterator;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.InvalidEdifNameException;

/**
 * This class will create a Xilinx primitive library.
 */
public class XilinxLibrary extends XilinxGenLib {

    static {
        //XilinxMacros.insertMacros(library);
    }

    //    public XilinxLibrary() {
    //        super("xilinx", false);
    //        XilinxGenLib.insertPrimitives(this);
    //        XilinxMacros.insertMacros(this);
    //    }

    /**
     * This method will search the libraries found within the library manager
     * and identify the library that contains the xilinx primitives. If there
     * are more than one libraries that match Xilinx primitives, return the
     * largest primitive library.
     */
    public static EdifLibrary findXilinxLibrary(EdifLibraryManager elm) {

        EdifLibrary bestMatch = null;
        int matchCount = 0;
        for (Iterator i = elm.iterator(); i.hasNext();) {
            EdifLibrary lib = (EdifLibrary) i.next();
            int libCount = 0;
            for (Iterator j = lib.iterator(); j.hasNext();) {
                EdifCell cell = (EdifCell) j.next();
                // perform comparison by name only
                if (library.containsCellByName(cell.getName()))
                    libCount++;
            }
            if (libCount > matchCount) {
                bestMatch = lib;
                matchCount = libCount;
            }
        }

        if (bestMatch == null)
            return (EdifLibrary) elm.iterator().next();

        return bestMatch;
    }

    /**
     * Checks to see if the given library contains only xilinx primitives.
     */
    public static boolean containsOnlyXilinxPrimitives(EdifLibrary lib) {
        for (Iterator j = lib.iterator(); j.hasNext();) {
            EdifCell cell = (EdifCell) j;
            // perform comparison by name only
            if (library.containsCellByName(cell.getName()))
                return false;
        }
        return true;
    }

    /**
     * Returns the given Xilinx primitive from the library or adds the primitive
     * to the library if it does not exist. This method will attempt to find the
     * proper Xilinx library and add the Cell to the appropriate library.
     */
    public static EdifCell findOrAddXilinxPrimitive(EdifLibraryManager elm, String primName) {

        EdifCell cell_in_library = elm.getCell(primName);
        EdifCell result = null;
        if (cell_in_library != null)
            return cell_in_library;
        else {
            // cell not in library. Add it.
            EdifCell xilinx_cell = XilinxLibrary.library.getCell(primName);
            EdifLibrary xilinxLibrary = XilinxLibrary.findXilinxLibrary(elm);
            //xilinxLibrary.addCell(xilinx_cell);
            //return xilinx_cell;
            try {
                result = new EdifCell(xilinxLibrary, xilinx_cell);
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            }
            return result;
        }

    }

    public static EdifCell findOrAddXilinxPrimitive(EdifCell cell, String primName) {
        return findOrAddXilinxPrimitive(cell.getLibrary().getLibraryManager(), primName);
    }
}
