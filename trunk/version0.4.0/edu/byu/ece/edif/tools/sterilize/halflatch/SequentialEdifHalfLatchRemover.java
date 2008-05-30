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
package edu.byu.ece.edif.tools.sterilize.halflatch;

import java.util.Iterator;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;

/**
 * @since Created on Nov 9, 2005
 */
public class SequentialEdifHalfLatchRemover extends EdifHalfLatchRemover {

    public SequentialEdifHalfLatchRemover(HalfLatchArchitecture hlArchitecture, int safeConstantPolarity,
            boolean usePortForConstant) {
        this(hlArchitecture, safeConstantPolarity, usePortForConstant, null);
    }

    /**
     * @param hlArchitecture Object which holds all of the architecture specific
     * information
     * @param safeConstantPolarity Polarity of the main safe-constant signal
     * running around in the design
     * @param usePortForConstant Boolean which determines whether or not the
     * safe-constant network is driven through a port or internal constant
     * generator cells
     * @param safeConstantPortName The name of the port to use for the safe
     * constant.
     */
    public SequentialEdifHalfLatchRemover(HalfLatchArchitecture hlArchitecture, int safeConstantPolarity,
            boolean usePortForConstant, String safeConstantPortName) {
        super(hlArchitecture, safeConstantPolarity, usePortForConstant, safeConstantPortName);
    }

    /**
     * Overrides
     * {@link EdifHalfLatchRemover#fixPotentialHalfLatchesInEachCell(EdifEnvironment)}
     */
    @Override
    protected void fixPotentialHalfLatchesInEachCell(EdifEnvironment environment) {

        // Step 2 / Pass 2
        // ------
        // For each library in the library manager
        EdifLibraryManager libMan = environment.getLibraryManager();
        for (Iterator libraryIterator = libMan.getLibraries().iterator(); libraryIterator.hasNext();) {
            EdifLibrary lib = (EdifLibrary) libraryIterator.next();
            Object[] cells = lib.getCells().toArray();

            // For each cell in each library
            for (int i = 0; i < cells.length; i++) {
                EdifCell ithCell = (EdifCell) cells[i];
                if (ithCell.isPrimitive() == false && ithCell.isBlackBox() == false) {

                    // Each non-primitive cell in the library *should* already have been recast
                    // as a HalfLatchEdifCell object.
                    HalfLatchEdifCell ithHLCell = (HalfLatchEdifCell) ithCell;

                    // Iterate through the non-primitive sub-cell instances
                    // Modify the current non-primitive instance to reference its HalfLatchEdifCell
                    // counterpart.
                    // Connect the safe-contant port of the sub-cell non-primitive to
                    // the safe-constant net of the current ith cell.
                    for (Iterator k = ithHLCell.getNonPrimitiveSubCellList().iterator(); k.hasNext();) {
                        EdifCellInstance eci = (EdifCellInstance) k.next();
                        // Do nothing for BlackBox cells
                        if (eci.getCellType().isBlackBox() == false)
                            ithHLCell.connectNonPrimitiveToSafeConstant(eci);
                    }

                    // Iterate through the primitive sub-cell instances
                    // 'fix' the half-latch prone cells and replace constant cells
                    for (Iterator j = ithHLCell.getPrimitiveSubCellList().iterator(); j.hasNext();) {
                        EdifCellInstance eci = (EdifCellInstance) j.next();
                        if (_debug)
                            System.out.println(eci);
                        if (_hlArchitecture.cellRequiresReplacement(eci)) {
                            if (_debug)
                                System.out.println("Cell " + eci + " requires replacement");
                            ithHLCell.replaceSensitivePrimitive(eci);
                        } else if (_hlArchitecture.isConstantCell(eci.getType())) {
                            ithHLCell.replaceConstantPrimitive(eci);
                        }
                    }

                }
            }

        } // End For

    }

}
