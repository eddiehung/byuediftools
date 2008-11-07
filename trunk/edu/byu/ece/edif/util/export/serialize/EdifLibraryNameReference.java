/*
 * Represents a name reference to a real EdifLibrary
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

import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;

/**
 * Represents a name reference to a real EdifLibrary
 */
public class EdifLibraryNameReference extends EdifGenericNameReference implements Serializable {
    
protected static SerializationNameReferenceCache<EdifLibrary, EdifLibraryNameReference> _referenceCache = new SerializationNameReferenceCache<EdifLibrary, EdifLibraryNameReference>();
    
	private static final long serialVersionUID = 42L;

    protected EdifLibraryNameReference(EdifLibrary lib) {
        this(lib, true);
    }
    
    protected EdifLibraryNameReference(EdifLibrary lib, boolean shouldResolve) {
        this(lib, shouldResolve, true);
    }
    
    protected EdifLibraryNameReference(EdifLibrary lib, boolean shouldResolve, boolean cacheReference) {
        super(shouldResolve);
        _libraryName = lib.getName();
        if (cacheReference)
            _referenceCache.cacheReference(lib, this);
    }
    
    public static EdifLibraryNameReference getReference(EdifLibrary lib) {
    	return getReference(lib, true);
    }
    
    public static EdifLibraryNameReference getReference(EdifLibrary lib, boolean shouldResolve) {
        EdifLibraryNameReference reference = _referenceCache.getReference(lib, shouldResolve);
        if (reference != null)
            return reference;
        else {
            return new EdifLibraryNameReference(lib, shouldResolve);
        }
    }
    
    public EdifLibrary getReferencedLibrary(EdifEnvironment referenceEnvironment) throws EdifDeserializationException {
        EdifLibrary library = referenceEnvironment.getLibrary(_libraryName);
        if (library == null)
            throw new EdifDeserializationException("Reference environment does not contain referenced library");
        return library;
    }
    
    protected String _libraryName;
    
}
