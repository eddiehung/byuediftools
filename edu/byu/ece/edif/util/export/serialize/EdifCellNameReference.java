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
