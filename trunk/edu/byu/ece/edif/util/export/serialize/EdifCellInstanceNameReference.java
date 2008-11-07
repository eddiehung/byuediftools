/*
 * Represents a name reference to a real EdifCellInstance
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
