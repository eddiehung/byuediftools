/* Copy an EdifEnvironment and replace instances in the environment 
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
package edu.byu.ece.edif.tools.sterilize.lutreplace;

import java.util.ArrayList;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNameConflictException;

/**
 * Provides a mechanism to copy an EdifEnvironment and replace instances
 * in the environment with different instances.
 * 
 * This class overrides several of the parent class methods to operate
 * differently during the copy process.
 * 
 * This class does not actually do the replacement. Instead, it builds a
 * copy and saves all the information necessary for adding replacement
 * circuitry at a later time by a different class.
 */
public class BasicEdifEnvironmentCopyReplace extends AbstractEdifEnvironmentCopyReplace {
    
	/**
	 * Perform the copy. Note that several of the methods in this class
	 * will be called by the parent methods.
	 * 
	 * @param env
	 * @param replacements A list of EdifCell objects that should be replaced.
	 * @throws EdifNameConflictException
	 */
	public BasicEdifEnvironmentCopyReplace(EdifEnvironment env, ArrayList<EdifCell> replacements)
		throws EdifNameConflictException {
		super(env);
		_cellTypesToReplace  = replacements;
		createEdifEnvironment();
	}

	/**
	 * Overrides the parent addChildEdifCellInstance method. This method
	 * will check to see if the instance is of a type that needs to be 
	 * replaced. If so, it will not add the instance and create a
	 * ReplacementContext for this instance. If it doesn't match, the
	 * instance will be added as usual.
	 */
	protected void addChildEdifCellInstance(EdifCell origCell, EdifCell newCell, 
			EdifCellInstance oldChildInstance) throws EdifNameConflictException {
		// Check to see if the instance matches one of the replacements
		EdifCell replaceCell = matchingReplacementCell(oldChildInstance); 
		//System.out.println("Instance "+oldChildInstance.getName()+" "+
		//		oldChildInstance.getCellType().getName()+" matches " + replaceCell);
		if (replaceCell != null) {
			// Add instance to list for replacement. Do not copy instance
			TemplateCellReplacementContext rc = 
				new TemplateCellReplacementContext(replaceCell, newCell, oldChildInstance);
			_oldInstancesToReplace.put(oldChildInstance, rc);
			
		} else
			// Doesn't match: allow the parent "copy" method to continue
			super.addChildEdifCellInstance(origCell, newCell, oldChildInstance);
	}
	
	// This method will examine the instance to see if it matches one of the
	// EdifCells that need to be replaced. If a match occurs, the corresponding
	// matching EdifCell is returned. If no match occurs, a null is returned.
	protected EdifCell matchingReplacementCell(EdifCellInstance oldChildInstance) {
		EdifCell oldChildType = oldChildInstance.getCellType();
		// Check the cache of previously matched cells to avoid matching every
		// cell.
		if (_oldCellsNOTToReplace.contains(oldChildType))
			return null;
		if (_oldCellsToReplace.contains(oldChildType))
			return oldChildType;
		// At this point, the Cell has not been matched yet (i.e. not in a cache).
		// Perform the match and
		// add to the appropriate cache.
		for (EdifCell replaceCell : _cellTypesToReplace)  {
			//System.out.println("Checking "+replaceCell+" against " + oldChildInstance);

			if (oldChildType.equalsName(replaceCell) && oldChildType.equalsInterface(replaceCell)) {
				_oldCellsToReplace.add(oldChildType);
				//System.out.println("cell "+oldChildType.getName() + " matches against " + replaceCell.getName());
				return replaceCell;
			}			
		}
		// No match found
		//System.out.println("cell "+oldChildType + " doesn't match anything");
		_oldCellsNOTToReplace.add(oldChildType);
		return null;
	}
	
	// The following two lists are "caches" of cells to replace and not to replace.
	// These are for speed so we dont' have to match every cell every time (match once only)
	protected ArrayList<EdifCell> _oldCellsToReplace = new ArrayList<EdifCell>();
	protected ArrayList<EdifCell> _oldCellsNOTToReplace = new ArrayList<EdifCell>();
	// Cell types from some new library that we want to replace
	protected ArrayList<EdifCell> _cellTypesToReplace;
	
	/* Create a simple class that will replace cells of one type with cells
	 * of another type. The cell interfaces must match.
	 * - Find all cells in original file that must be replaced
	 * - During building, create the new replacement cell and put it
	 *   into the Map.
	 * - During hookup and instancing, it will find the new map and hook
	 *   up the nets to the new instance.
	 */
	
	/* Flattened replace: A method that will replace cells of one type
	 * with the flattened guts of another type.
	 * - Find the cells in the original file that must be replaced
	 * - Create a new, secondary map between cells that must be replaced
	 *   with specific cells.
	 * - When a suspect cell is found,   
	 */
}

