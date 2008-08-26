package edu.byu.ece.edif.util.export.serialize;

import java.io.Serializable;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifPort;

/**
 * Represents a name reference to a real EdifPort
 */
public class EdifPortNameReference extends EdifGenericNameReference implements Serializable {

	private static final long serialVersionUID = 42L;
	
    protected static SerializationNameReferenceCache<EdifPort, EdifPortNameReference> _referenceCache = new SerializationNameReferenceCache<EdifPort, EdifPortNameReference>();
    
    protected EdifPortNameReference(EdifPort port) {
        this(port, true);
    }
    
    protected EdifPortNameReference(EdifPort port, boolean shouldResolve) {
    	this(port, shouldResolve, true);
    }
    
    protected EdifPortNameReference(EdifPort port, boolean shouldResolve, boolean cacheReference) {
        super(shouldResolve);
    	_portName = port.getName();
        EdifCell parent = port.getEdifCell();
        _cellReference = EdifCellNameReference.getReference(parent, false);
        if (cacheReference)
            _referenceCache.cacheReference(port, this);
    }
    
    public static EdifPortNameReference getReference(EdifPort port) {
    	return getReference(port, true);
    }
    
    public static EdifPortNameReference getReference(EdifPort port, boolean shouldResolve) {
        EdifPortNameReference reference = _referenceCache.getReference(port, shouldResolve);
        if (reference != null)
            return reference;
        else {
            return new EdifPortNameReference(port, shouldResolve);
        }
    }
    
    public EdifPort getReferencedPort(EdifEnvironment referenceEnvironment) throws EdifDeserializationException {
        EdifCell parent = _cellReference.getReferencedCell(referenceEnvironment);
        if (parent == null)
            throw new EdifDeserializationException("Reference environment does not contain referenced port's parent cell");
        EdifPort port = parent.getPort(_portName);
        if (port == null)
            throw new EdifDeserializationException("Reference environment does not contain referenced port");
        return port;
    }
    
    protected String _portName;
    protected EdifCellNameReference _cellReference;
}
