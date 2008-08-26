package edu.byu.ece.edif.util.export.serialize;

import java.io.Serializable;

/**
 * Represents a name reference to a real EDIF object.
 */
public abstract class EdifGenericNameReference implements Serializable {

    public EdifGenericNameReference(boolean shouldResolve) {
        _shouldResolve = shouldResolve;
    }
    
    public boolean shouldResolve() {
        return _shouldResolve;
    }
    
    protected boolean _shouldResolve = true;
}
