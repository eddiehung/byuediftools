package edu.byu.ece.edif.util.export.serialize;

import java.io.Serializable;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;

/**
 * Represents a name reference to a real EdifPortRef
 */
public class EdifPortRefNameReference extends EdifGenericNameReference implements Serializable {

	private static final long serialVersionUID = 42L;
	
    protected static SerializationNameReferenceCache<EdifPortRef, EdifPortRefNameReference> _referenceCache = new SerializationNameReferenceCache<EdifPortRef, EdifPortRefNameReference>();
    
    protected EdifPortRefNameReference(EdifPortRef portRef) {
        this(portRef, true);
    }
    
    protected EdifPortRefNameReference(EdifPortRef portRef, boolean shouldResolve) {
    	this(portRef, shouldResolve, true);
    }
    
    protected EdifPortRefNameReference(EdifPortRef portRef, boolean shouldResolve, boolean cacheReference) {
        super(shouldResolve);
        EdifCellInstance instance = portRef.getCellInstance();
        EdifNet net = portRef.getNet();
        EdifPort port = portRef.getPort();
        if (instance != null) {
            _instanceReference = EdifCellInstanceNameReference.getReference(instance, false);
        }
        _netReference = EdifNetNameReference.getReference(net, false);
        _portReference = EdifPortNameReference.getReference(port, false);
        _bitPosition = portRef.getBusMember();
        if (cacheReference)
            _referenceCache.cacheReference(portRef, this);
    }
    
    public static EdifPortRefNameReference getReference(EdifPortRef portRef) {
    	return getReference(portRef, true);
    }
    
    public static EdifPortRefNameReference getReference(EdifPortRef portRef, boolean shouldResolve) {
        EdifPortRefNameReference reference = _referenceCache.getReference(portRef, shouldResolve);
        if (reference != null)
            return reference;
        else {
            return new EdifPortRefNameReference(portRef, shouldResolve);
        }
    }
    
    public EdifPortRef getReferencedPortRef(EdifEnvironment referenceEnvironment) throws EdifDeserializationException {
        EdifCellInstance instance = null;
        if (_instanceReference != null)
            instance = _instanceReference.getReferencedInstance(referenceEnvironment);
        EdifNet net = _netReference.getReferencedNet(referenceEnvironment);
        EdifPort port = _portReference.getReferencedPort(referenceEnvironment);
        EdifPortRef match = null;
        for (EdifPortRef portRef : net.getConnectedPortRefs()) {
            if (portRef.getCellInstance() == instance &&
                    portRef.getNet() == net &&
                    portRef.getPort() == port &&
                    portRef.getBusMember() == _bitPosition) {
                match = portRef;
                break;
            }
        }
        if (match == null)
            throw new EdifDeserializationException("Reference environment does not contain referenced portRef");
        return match;
    }
    
    protected EdifCellInstanceNameReference _instanceReference = null;
    protected EdifNetNameReference _netReference;
    protected EdifPortNameReference _portReference;
    protected int _bitPosition;
}
