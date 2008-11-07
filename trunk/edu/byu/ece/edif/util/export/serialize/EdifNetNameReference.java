/*
 * Represents a name reference to a real EdifNet
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
import edu.byu.ece.edif.core.EdifNet;

/**
 * Represents a name reference to a real EdifNet
 */
public class EdifNetNameReference extends EdifGenericNameReference implements Serializable {

	private static final long serialVersionUID = 42L;
	
    protected static SerializationNameReferenceCache<EdifNet, EdifNetNameReference> _referenceCache = new SerializationNameReferenceCache<EdifNet, EdifNetNameReference>();
    
    protected EdifNetNameReference(EdifNet net) {
        this(net, true);
    }
    
    protected EdifNetNameReference(EdifNet net, boolean shouldResolve) {
    	this(net, shouldResolve, true);
    }
    
    protected EdifNetNameReference(EdifNet net, boolean shouldResolve, boolean cacheReference) {
        super(shouldResolve);
    	_netName = net.getName();
        EdifCell parent = net.getParent();
        _cellReference = EdifCellNameReference.getReference(parent, false);
        if (cacheReference)
            _referenceCache.cacheReference(net, this);
    }
    
    public static EdifNetNameReference getReference(EdifNet net) {
    	return getReference(net);
    }
    
    public static EdifNetNameReference getReference(EdifNet net, boolean shouldResolve) {
        EdifNetNameReference reference = _referenceCache.getReference(net, shouldResolve);
        if (reference != null)
            return reference;
        else {
            return new EdifNetNameReference(net, shouldResolve);
        }
    }
    
    public EdifNet getReferencedNet(EdifEnvironment referenceEnvironment) throws EdifDeserializationException {
        EdifCell parent = _cellReference.getReferencedCell(referenceEnvironment);
        if (parent == null)
            throw new EdifDeserializationException("Reference environment does not contain referenced net's parent cell");
        EdifNet net = parent.getNet(_netName);
        if (net == null)
            throw new EdifDeserializationException("Reference environment does not contain referenced net");
        return net;
    }
    
    protected String _netName;
    protected EdifCellNameReference _cellReference;
}
