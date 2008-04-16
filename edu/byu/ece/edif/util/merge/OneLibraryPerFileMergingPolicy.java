 




/*
 * 
 *

 * Copyright (c) 2008 Brigham Young University
 *
 * This file is part of the BYU EDIF Tools.
 * 
 * BYU EDIF Tools is free software: you may redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * BYU EDIF Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License is included with the BYU
 * EDIF Tools. It can be found at /edu/byu/edif/doc/gpl2.txt. You may
 * also get a copy of the license at <http://www.gnu.org/licenses/>.
 *
 */
package edu.byu.ece.edif.util.merge;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.InvalidEdifNameException;

/**
 * An EdifMergingPolicy policy which will create a library for each black box
 * file and place all elements from each black box in its respective library.
 */
public class OneLibraryPerFileMergingPolicy implements EdifMergingPolicy {

	public EdifLibrary findLibraryForCell(EdifCell cellToCopy,
			EdifLibraryManager newElm) {
		String libName = cellToCopy.getLibrary().getLibraryManager().getEdifEnvironment().getTopCell().getName();
		EdifLibrary lib = newElm.getLibrary(libName);
		if (lib == null)
			try {
				lib = new EdifLibrary(newElm, libName);
			} catch (EdifNameConflictException e) {
				e.toRuntime();
			} catch (InvalidEdifNameException e) {
				e.toRuntime();
			}
		
		/*// this shouldn't ever happen
		if (lib.containsCellByName(cellToCopy.getName()))
			lib = new EdifLibrary(newElm, AbstractNameSpaceResolver.returnUniqueName(newElm.getLibraries(), lib.getName()));
		*/return lib;
	}
}
