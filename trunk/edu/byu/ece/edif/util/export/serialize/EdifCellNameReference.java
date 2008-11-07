/*
 * Represents a name reference to a real EdifCell object
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
package edu.byu.ece.edif.util.export.serialize;

import java.io.Serializable;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;

/**
 * Represents a name reference to a real EdifCell object
 */
public class EdifCellNameReference extends EdifGenericNameReference implements Serializable {

	private static final long serialVersionUID = 42L;
	
    protected static SerializationNameReferenceCache<EdifCell, EdifCellNameReference> _referenceCache = new SerializationNameReferenceCache<EdifCell, EdifCellNameReference>();
    
    protected EdifCellNameReference(EdifCell cell) {
        this(cell, true, true);
    }
    
    protected EdifCellNameReference(EdifCell cell, boolean shouldResolve) {
        this(cell, shouldResolve, true);
    }
    
    protected EdifCellNameReference(EdifCell cell, boolean shouldResolve, boolean cacheReference) {
        super(shouldResolve);
        _libraryReference = EdifLibraryNameReference.getReference(cell.getLibrary(), false);
        _cellName = cell.getName();
        if (cacheReference)
            _referenceCache.cacheReference(cell, this);
    }
    
    public static EdifCellNameReference getReference(EdifCell cell) {
    	return getReference(cell, true);
    }
    
    public static EdifCellNameReference getReference(EdifCell cell, boolean shouldResolve) {
        EdifCellNameReference reference = _referenceCache.getReference(cell, shouldResolve);
        if (reference != null)
            return reference;
        else {
            return new EdifCellNameReference(cell, shouldResolve);
        }
    }
    
    public EdifCell getReferencedCell(EdifEnvironment referenceEnvironment) throws EdifDeserializationException {
        EdifLibrary library = _libraryReference.getReferencedLibrary(referenceEnvironment);
        EdifCell cell = library.getCell(_cellName);
        if (cell == null)
            throw new EdifDeserializationException("Reference environment does not contain referenced cell");
        return cell;
    }
    
    protected EdifLibraryNameReference _libraryReference;
    protected String _cellName;
}
