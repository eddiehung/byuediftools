/*
 * Name reference cache to prevent serialization of duplicate EDIF objects
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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is a name reference cache used when serializing EDIF objects using the
 * custom name reference object input/output classes. The purpose of the cache
 * is so that duplicate copies of the same name reference won't get serialized,
 * taking up more space than is necessary in the output file. Duplicate copies
 * would still get resolved to the correct real EDIF object upon deserialization,
 * but it isn't as efficient. There is a separate cache for objects that should
 * be resolved upon deserialization and objects that shouldn't be resolved upon
 * deserialization. So there may in fact be (at most) two copies of a name
 * reference that refer to the same actual EDIF object. This is necessary so
 * that a reference to an EDIF object can be serialized both as a 'top level'
 * reference and as part of different reference. The only objects that shouldn't
 * be resolved upon deserialization are name reference objects that are memebers
 * of other name reference objects (i.e. an EdifCellNameReference has an
 * EdifLibraryNameReference included in it. If it were resolved to an EdifLibrary
 * upon deserialization, the process wouldn't work right). So all 'top-level'
 * name reference objects should go in the 'do resolve' cache while name references
 * that are part of other name references go in the 'do not resolve' cache.
 *
 * @param <T_OBJ> this is the actual EDIF object class
 * @param <T_REF> this is the name reference class
 */
public class SerializationNameReferenceCache<T_OBJ, T_REF extends EdifGenericNameReference> {

	/**
	 * Add a cache entry for the given object and reference.
	 * 
	 * @param obj
	 * @param ref
	 */
    public void cacheReference(T_OBJ obj, T_REF ref) {
        Map<T_OBJ, T_REF> referenceCache = ref.shouldResolve() ? _resolveCache : _noResolveCache;
        referenceCache.put(obj, ref);
    }
    
    /**
     * Get the reference in the cache for the given object and resolution status.
     * If there is no reference in the cache for the given object, return null
     * 
     * @param obj
     * @param shouldResolve
     * @return the object reference or null
     */
    public T_REF getReference(T_OBJ obj, boolean shouldResolve) {
        Map<T_OBJ, T_REF> referenceCache = shouldResolve ? _resolveCache : _noResolveCache;
        return referenceCache.get(obj);
    }
    
    protected Map<T_OBJ, T_REF> _resolveCache = new LinkedHashMap<T_OBJ, T_REF>();
    protected Map<T_OBJ, T_REF> _noResolveCache = new LinkedHashMap<T_OBJ, T_REF>();
}
