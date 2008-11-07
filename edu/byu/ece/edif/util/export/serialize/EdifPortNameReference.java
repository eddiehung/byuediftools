/*
 * Represents a name reference to a real EdifPort
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
