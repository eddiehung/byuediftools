package edu.byu.ece.edif.util.export.serialize;

import java.io.Serializable;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;

/**
 * Represents a name reference to a real EdifCellInstance
 */
public class EdifCellInstanceNameReference extends EdifGenericNameReference implements Serializable {

	private static final long serialVersionUID = 42L;
	
	protected static SerializationNameReferenceCache<EdifCellInstance, EdifCellInstanceNameReference> _referenceCache = new SerializationNameReferenceCache<EdifCellInstance, EdifCellInstanceNameReference>();
    
    protected EdifCellInstanceNameReference(EdifCellInstance instance) {
        this(instance, true);
    }
    
    protected EdifCellInstanceNameReference(EdifCellInstance instance, boolean shouldResolve) {
    	this(instance, shouldResolve, true);
    }
    
    protected EdifCellInstanceNameReference(EdifCellInstance instance, boolean shouldResolve, boolean cacheReference) {
        super(shouldResolve);
    	_instanceName = instance.getName();
        EdifCell parent = instance.getParent();
        _cellReference = EdifCellNameReference.getReference(parent, false);
        if (cacheReference)
            _referenceCache.cacheReference(instance, this);
    }
    
    public static EdifCellInstanceNameReference getReference(EdifCellInstance instance) {
    	return getReference(instance, true);
    }
    
    public static EdifCellInstanceNameReference getReference(EdifCellInstance instance, boolean shouldResolve) {
        EdifCellInstanceNameReference reference = _referenceCache.getReference(instance, shouldResolve);
        if (reference != null)
            return reference;
        else {
            return new EdifCellInstanceNameReference(instance, shouldResolve);
        }
    }
    
    public EdifCellInstance getReferencedInstance(EdifEnvironment referenceEnvironment) throws EdifDeserializationException {
        EdifCell parent = _cellReference.getReferencedCell(referenceEnvironment);
        if (parent == null)
            throw new EdifDeserializationException("Reference environment does not contain referenced instance's parent cell");
        EdifCellInstance instance = parent.getCellInstance(_instanceName);
        if (instance == null)
            throw new EdifDeserializationException("Reference environment does not contain referenced instance");
        return instance;
    }
    
    protected String _instanceName;
    protected EdifCellNameReference _cellReference;
    
}
