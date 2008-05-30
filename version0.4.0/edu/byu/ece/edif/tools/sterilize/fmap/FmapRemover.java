/*
 * Remove fmap EDIF directives.
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
package edu.byu.ece.edif.tools.sterilize.fmap;

import java.util.ArrayList;
import java.util.Collection;

import edu.byu.ece.edif.arch.xilinx.XilinxGenLib;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;

/**
 * <p>
 * Remove fmap EDIF directives.
 * </p>
 * <p>
 * An fmap is not a actual EdifCell nor EdifCellInstance. Rather, it is a
 * directive in the EDIF language that specifies that a small group of logic
 * gates must be placed in the same LUT. In previous years, fmaps were more
 * common in EDIF designs; but as mapping programs have become more
 * sophisticated, the need for fmaps has gone. In the BYU EDIF tools, an fmap is
 * represented with an EdifCell object. For this reason, fmaps can cause
 * problems in circuits; for example, when a design is partially triplicated
 * (see {@link edu.byu.ece.edif.tools.replicate.nmr.tmr}), and the fmap is
 * marked to be triplicated, but not all of its contents are triplicated, the
 * result is an error being thrown by the triplication tool. Thus, fmaps are
 * completely removed from designs before performing triplication.
 * </p>
 * <p>
 * The following might help to clarify the definition of an fmap. It comes from
 * the JHDL javadocs:
 * </p>
 * <blockquote
 * cite="http://www.jhdl.org/documentation/latestdocs/api/byucc/jhdl/Xilinx/Virtex2/fmap.html">The
 * FMAP symbol is used to control logic partitioning into XC4000 family 4-input
 * function generators. The place and route software chooses an F or a G
 * function generator as a default, unless specified in the RLOC property. The
 * FMAP sits 'around' a set of existing gates, indicating the boundary of
 * partitioning for a given function. (Source: XACT Libraries Guide, pg. 3-280,
 * Xilinx Corporation, 1994.)</blockquote>
 * <p class="citation">
 * <cite
 * cite="http://www.jhdl.org/documentation/latestdocs/api/byucc/jhdl/Xilinx/Virtex2/fmap.html"><a
 * href="http://www.jhdl.org/documentation/latestdocs/api/byucc/jhdl/Xilinx/Virtex2/fmap.html">fmap
 * (JHDL API)</a></cite>
 * </p>
 * 
 * @author <a href="jcarroll@byu.net">James Carroll</a>
 * @link http://www.jhdl.org/documentation/latestdocs/api/byucc/jhdl/Xilinx/Virtex2/fmap.html
 */
public abstract class FmapRemover {

    /**
     * Remove fmaps from the given {@link EdifEnvironment}. Note that the
     * EdifEnvironment passed to this method is modified; no copy is made.
     * 
     * @param environment The EdifEnvironment object
     * @return true if the EdifEnvironment was modified.
     */
    public static boolean removeFmaps(EdifEnvironment environment) {
        boolean result = false;
        /* Iterate over all the EdifCells in all the EdifLibraries. */
        for (EdifLibrary library : environment.getLibraryManager().getLibraries()) {

            Collection<EdifCell> cellsToRemove = new ArrayList<EdifCell>();
            for (EdifCell cell : library.getCells()) {

                /*
                 * Iterate over all the EdifCellInstances in the EdifCell and
                 * mark any fmap instances to be removed.
                 */
                Collection<EdifCellInstance> instancesToRemove = new ArrayList<EdifCellInstance>();
                for (EdifCellInstance instance : cell.getSubCellList()) {
                    if (instance.getCellType().equalsName(XilinxGenLib.FMAP())) {
                        // Remove the instance and all associated portRefs.
                        instancesToRemove.add(instance);
                    }
                }
                /*
                 * Actually remove any fmap EdifCellInstances previously found.
                 * 
                 * (This must be done after we are done iterating over the
                 * instances to avoid a ConcurrentModificationException.)
                 */
                for (EdifCellInstance instance : instancesToRemove) {
                    if (cell.deleteSubCell(instance, true))
                        result = true;
                }

                /*
                 * Mark any fmap EdifCell objects to be removed from the
                 * library.
                 */
                if (cell.equalsName(XilinxGenLib.FMAP())) {
                    cellsToRemove.add(cell);
                }
            }
            /*
             * Actually remove any fmap EdifCells previously found.
             * 
             * (This must be done after we are done iterating over the cells to
             * avoid a ConcurrentModificationException.)
             */
            for (EdifCell cell : cellsToRemove) {
                if (library.deleteCell(cell, true))
                    result = true;
            }
        }
        return result;
    }
}
