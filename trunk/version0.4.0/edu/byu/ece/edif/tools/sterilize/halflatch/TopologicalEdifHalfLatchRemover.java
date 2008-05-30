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

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;

/**
 * @since Created on Nov 9, 2005
 */
public class TopologicalEdifHalfLatchRemover extends EdifHalfLatchRemover {

    public TopologicalEdifHalfLatchRemover(HalfLatchArchitecture hlArchitecture, int safeConstantPolarity,
            boolean usePortForConstant, int constantSinkThreshold) {
        this(hlArchitecture, safeConstantPolarity, usePortForConstant, null, constantSinkThreshold);
    }

    /**
     * @param hlArchitecture Object which holds all of the architecture specific
     * information
     * @param safeConstantPolarity Polarity of the main safe-constant signal
     * running around in the design
     * @param usePortForConstant Boolean which determines whether or not the
     * safe-constant network is driven through a port or internal constant
     * generator cells
     * @param constantSinkThreshold Threshold to determine when a cell should
     * insert it's own safe-constant generator (rather than connect to the
     * network provided by the next higher up level of hierarchy)
     */
    public TopologicalEdifHalfLatchRemover(HalfLatchArchitecture hlArchitecture, int safeConstantPolarity,
            boolean usePortForConstant, String safeConstantPortName, int constantSinkThreshold) {
        super(hlArchitecture, safeConstantPolarity, usePortForConstant, safeConstantPortName);
        _constantSinkThreshold = constantSinkThreshold;
    }

    /**
     * Step 2 (pass 2) of the remove half-latch algorithm. For each cell in
     * design libraries Remove the half-latches and constants in that cell.
     * (This is the abstract method for this class and is currently implemented
     * in a Sequential or Topological fashion. Each has different options.) end
     * For
     * 
     * @param environment Container of objects relating to the design which is
     * being modified
     * @param origCellToHalfLatchCellMap Map from each original cell in design
     * library to HalfLatchEdifCell version of the same cell. Overrides
     * {@link EdifHalfLatchRemover#fixPotentialHalfLatchesInEachCell(EdifEnvironment)}
     */
    //protected void fixPotentialHalfLatchesInEachCell(EdifEnvironment environment, Map origCellToHalfLatchCellMap) {
    protected void fixPotentialHalfLatchesInEachCell(EdifEnvironment environment) {
        // Call recursive algorithm method on top cell.
        //EdifCell top_cell = environment.getTopCell();
        //HalfLatchEdifCell top_hlCell = (HalfLatchEdifCell) origCellToHalfLatchCellMap.get(top_cell.getName());
        //removeHalfLatches(top_hlCell, origCellToHalfLatchCellMap);
        HalfLatchEdifCell top_cell = (HalfLatchEdifCell) environment.getTopCell();
        removeHalfLatches(top_cell);
    }

    /**
     * This method is the meat of the Topological half-latch removal algorithm.
     * 
     * @param hlCell The cell from which to remove half-latches and constants.
     * @param origCellToHalfLatchCellMap Map from each original cell in design
     * library to HalfLatchEdifCell version of the same cell.
     * @return An integer indicating the number of ports the cell passed in
     * needs driven through it's safe-constant port (0 will disconnect the cell
     * from the higher levels of hierarchy and no safe-constant port will be
     * created)
     */
    // If the passed in cell hlCell has already been fixed, then
    // return the number of safe constant ports that cell needs driven
    // by an external source.
    //
    // keep counter of number of constant ports to drive within this
    // cell and within this cell's sub-cell instances
    //
    // Iterate through each of the sub-cell instances
    //    if non-primitive
    //       1. Fix the instances cell reference to reference the half-latch safe version of its cell type    
    //       2. Call this method on the sub-cell instance
    //       3. If sub-cell instance needs it's safe constant port driven (i.e. the number of constant ports it needs driven > 0)
    //          then connect the current cell's net to that port
    //       4. Increment counter by num of constant ports sub-cell needs driven 
    //    if primitive
    //       if half-latch prone
    //          1. replace primitive
    //          2. increment counter
    //       if constant cell
    //          1. replace constant cell
    //          2. increment counter
    // end iteration
    //
    // if the number of non-primitive sub-cell instances which need a "safe" constant
    // PLUS the number of prims which need a "safe" constant > constantSinkThreshold
    //    then generate an internal safe constant generator
    //    return 0
    // else
    //    return (the sum of the non-prims + prims which need a safe constant)
    //private int removeHalfLatches(HalfLatchEdifCell hlCell, Map origCellToHalfLatchCellMap) {
    private int removeHalfLatches(HalfLatchEdifCell hlCell) {

        if (hlCell.isFixed() == true)
            return hlCell.getNumConstantPortsToDriveWithSafeConstant();

        // Iterate through each of the non-primitive sub-cell instances
        int numConstantPortsToDriveWithSafeConstant = 0;
        Object[] nonPrimitiveSubCellList = hlCell.getNonPrimitiveSubCellList().toArray();
        for (int i = 0; i < nonPrimitiveSubCellList.length; i++) {
            EdifCellInstance sub_eci = (EdifCellInstance) nonPrimitiveSubCellList[i];

            // Ignore Black Box cells
            if (sub_eci.getCellType().isBlackBox())
                continue;

            // This should be taken care of by the previous iteration (Deep Convert)
            //HalfLatchEdifCell sub_eci_type_hl = (HalfLatchEdifCell) origCellToHalfLatchCellMap.get(sub_eci.getType());
            //hlCell.modifyInstanceCellReference(sub_eci, origCellToHalfLatchCellMap);
            //int numConstantPortsToDriveInSubCell = removeHalfLatches(sub_eci_type_hl, origCellToHalfLatchCellMap);

            HalfLatchEdifCell sub_eci_type_hl = (HalfLatchEdifCell) sub_eci.getCellType();
            int numConstantPortsToDriveInSubCell = removeHalfLatches(sub_eci_type_hl);
            numConstantPortsToDriveWithSafeConstant += numConstantPortsToDriveInSubCell;
            //System.out.println("Sub Cell " + sub_eci + " has " + numConstantPortsToDriveInSubCell + " constant ports to drive");
            if (numConstantPortsToDriveInSubCell > 0)
                hlCell.connectNonPrimitiveToSafeConstant(sub_eci);
        }

        Object[] primitiveSubCellList = hlCell.getPrimitiveSubCellList().toArray();
        for (int j = 0; j < primitiveSubCellList.length; j++) {
            EdifCellInstance sub_eci = (EdifCellInstance) primitiveSubCellList[j];
            if (_hlArchitecture.cellRequiresReplacement(sub_eci)) {
                numConstantPortsToDriveWithSafeConstant += hlCell.replaceSensitivePrimitive(sub_eci);
            } else if (_hlArchitecture.isConstantCell(sub_eci.getType())) {
                //numConstantPortsToDriveWithSafeConstant += hlCell.replaceConstantPrimitive(sub_eci, origCellToHalfLatchCellMap);
                numConstantPortsToDriveWithSafeConstant += hlCell.replaceConstantPrimitive(sub_eci);
            } else {
                // do nothing!
            }
        }

        int result;
        if (numConstantPortsToDriveWithSafeConstant > _constantSinkThreshold) {
            hlCell.addSafeConstantGeneratorCell();
            // Mark the current cell as "fixed".
            // In addition, we'll keep track that this cell has it's own internal
            // constant generator, so it doesn't "need" to have any internal
            // ports driven with an external safe constant.
            hlCell.setNumConstantPortsToDriveWithSafeConstant(0);
            result = 0;
        } else {
            // Mark the current cell as "fixed".
            // In addition, we'll keep track of the number of constant ports this
            // cell has to drive for future instances of this cell we come across
            hlCell.setNumConstantPortsToDriveWithSafeConstant(numConstantPortsToDriveWithSafeConstant);
            result = numConstantPortsToDriveWithSafeConstant;
        }
        return result;

    }

    private int _constantSinkThreshold = 0;

}
