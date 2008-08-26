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
