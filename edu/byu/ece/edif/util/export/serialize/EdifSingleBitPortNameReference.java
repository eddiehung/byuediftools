/*
 * Represents a name reference to a real EdifSingleBitPort
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
