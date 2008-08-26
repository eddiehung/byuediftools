package edu.byu.ece.edif.util.export.serialize;

import java.io.Serializable;

import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifSingleBitPort;

/**
 * Represents a name reference to a real EdifSingleBitPort
 */
public class EdifSingleBitPortNameReference extends EdifGenericNameReference implements Serializable {

	private static final long serialVersionUID = 42L;
	
    protected static SerializationNameReferenceCache<EdifSingleBitPort, EdifSingleBitPortNameReference> _referenceCache = new SerializationNameReferenceCache<EdifSingleBitPort, EdifSingleBitPortNameReference>();
    
    protected EdifSingleBitPortNameReference(EdifSingleBitPort esbp) {
        this(esbp, true);
    }
    
    protected EdifSingleBitPortNameReference(EdifSingleBitPort esbp, boolean shouldResolve) {
    	this(esbp, shouldResolve, true);
    }
    
    protected EdifSingleBitPortNameReference(EdifSingleBitPort esbp, boolean shouldResolve, boolean cacheReference) {
        super(shouldResolve);
        EdifPort port = esbp.getParent();
        _portReference = EdifPortNameReference.getReference(port, false);
        _bitPosition = esbp.bitPosition();
        if (cacheReference)
            _referenceCache.cacheReference(esbp, this);
    }
    
    public static EdifSingleBitPortNameReference getReference(EdifSingleBitPort esbp) {
    	return getReference(esbp, true);
    }
    
    public static EdifSingleBitPortNameReference getReference(EdifSingleBitPort esbp, boolean shouldResolve) {
        EdifSingleBitPortNameReference reference = _referenceCache.getReference(esbp, shouldResolve);
        if (reference != null)
            return reference;
        else {
            return new EdifSingleBitPortNameReference(esbp, shouldResolve);
        }
    }
    
    public EdifSingleBitPort getReferencedSingleBitPort(EdifEnvironment referenceEnvironment) throws EdifDeserializationException {
        EdifPort port = _portReference.getReferencedPort(referenceEnvironment);
        EdifSingleBitPort esbp =  port.getSingleBitPort(_bitPosition);
        if (esbp == null)
            throw new EdifDeserializationException("Reference environment does not contain referenced EdifSingleBitPort");
        return esbp;       
    }
    
    protected EdifPortNameReference _portReference;
    protected int _bitPosition;
}
