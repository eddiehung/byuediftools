 




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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.InvalidEdifNameException;


/**
 * An EdifMergingPolicy which will try to reuse only EdifCells that have
 * been added during merging, preserving the libraries in the original
 * file.
 */
public class ReuseNewLeafCellsMergingPolicy implements EdifMergingPolicy {
	
	public ReuseNewLeafCellsMergingPolicy() {
		mergeLibs = new ArrayList<EdifLibrary>();
	}
	
	public EdifLibrary findLibraryForCell(EdifCell cellToCopy,
			EdifLibraryManager newElm) {
		EdifLibrary lib = null;
		
		// deal with leaf cells specially
		
		if (cellToCopy.isLeafCell()) {
			// return a library that already has the right cell if it exists
			Collection matchingNames = newElm.getCells(cellToCopy.getName());
			for (Iterator it = matchingNames.iterator(); it.hasNext();) {
				EdifCell cell = (EdifCell) it.next();
				if (cellToCopy.equalsInterface(cell) && cellToCopy.equalsProperties(cell) && mergeLibs.contains(cell.getLibrary())) {
					lib = cell.getLibrary();
					return lib;
				}
			}
			
			// cell not found so put the cell into a "merge" library
			if (mergeLibs.size() == 0) {
				try {
					lib = new EdifLibrary(newElm, "merge_lib_" + mergeLibs.size());
				} catch (EdifNameConflictException e) {
					e.toRuntime();
				} catch (InvalidEdifNameException e) {
					e.toRuntime();
				}
				mergeLibs.add(lib);
			}
			else {
				for (int i = 0; i < mergeLibs.size(); i++) {
					EdifLibrary tmpLib = mergeLibs.get(i);
					if (!tmpLib.containsCellByName(cellToCopy.getName())) {
						lib = tmpLib;
						break;
					}
				}
				if (lib == null) {
					try {
						lib = new EdifLibrary(newElm, "merge_lib_" + mergeLibs.size());
					} catch (EdifNameConflictException e) {
						e.toRuntime();
					} catch (InvalidEdifNameException e) {
						e.toRuntime();
					}
					mergeLibs.add(lib);
				}
			}
		}
		
		// otherwise, put the cell in the library that corresponds to its file name
		// (this should happen only for non leaf cells, so it won't be very often)
		if (lib == null) {
			String libName = cellToCopy.getLibrary().getLibraryManager().getEdifEnvironment().getTopCell().getName();
			lib = newElm.getLibrary(libName);
			if (lib == null)
				try {
					lib = new EdifLibrary(newElm, libName);
				} catch (EdifNameConflictException e) {
					e.toRuntime();
				} catch (InvalidEdifNameException e) {
					e.toRuntime();
				}
		}
		
		/*// this shouldn't ever happen
		if (lib.containsCellByName(cellToCopy.getName()))
			lib = new EdifLibrary(newElm, AbstractNameSpaceResolver.returnUniqueName(newElm.getLibraries(), lib.getName()));
		*/return lib;
	}
	
	private List<EdifLibrary> mergeLibs;
}
