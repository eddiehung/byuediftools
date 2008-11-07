/* 
 * Maps deserialized EDIF name reference objects to real EDIF objects 
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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import edu.byu.ece.edif.core.EdifEnvironment;

/**
 * This is a customized version of ObjectInputStream used for mapping EDIF name
 * reference objects to the real EDIF objects from the given
 * EdifEnvironment during serialization. The class is intended to be used
 * in conjunction with NameReferenceObjectOutputStream.
 * 
 * The supported EDIF object classes that can be replaced with name references
 * automatically are: EdifCell, EdifCellInstance, EdifLibrary, EdifNet, EdifPort,
 * EdifPortRef, and EdifSingleBitPort.
 */
public class NameReferenceObjectInputStream extends ObjectInputStream {

	/**
	 * Construct a NameReferenceObjectInputStream with the given input stream
	 * and reference EdifEnvironment.
	 * 
	 * @param in
	 * @param referenceEnvironment
	 * @throws IOException
	 */
	public NameReferenceObjectInputStream(InputStream in, EdifEnvironment referenceEnvironment) throws IOException {
		super(in);
		enableResolveObject(true);
		_referenceEnvironment = referenceEnvironment;
	}
    
	/**
	 * Resolve EDIF name reference objects to the corresponding real EDIF
	 * objects in the given reference EdifEnvironment during deserialization.
	 */
    @Override
    protected Object resolveObject(Object obj) throws IOException {
        if (obj instanceof EdifGenericNameReference) {
            EdifGenericNameReference reference = (EdifGenericNameReference) obj;
            if (!reference.shouldResolve())
                return obj;
        }
        if (obj instanceof EdifCellNameReference) {
            EdifCellNameReference reference = (EdifCellNameReference) obj;
            return reference.getReferencedCell(_referenceEnvironment);
        }
        
        else if (obj instanceof EdifCellInstanceNameReference) {
            EdifCellInstanceNameReference reference = (EdifCellInstanceNameReference) obj;
            return reference.getReferencedInstance(_referenceEnvironment);
        }
        
        else if (obj instanceof EdifPortNameReference) {
            EdifPortNameReference reference = (EdifPortNameReference) obj;
            return reference.getReferencedPort(_referenceEnvironment);
        }
        
        else if (obj instanceof EdifPortRefNameReference) {
            EdifPortRefNameReference reference = (EdifPortRefNameReference) obj;
            return reference.getReferencedPortRef(_referenceEnvironment);
        }
        
        else if (obj instanceof EdifNetNameReference) {
            EdifNetNameReference reference = (EdifNetNameReference) obj;
            return reference.getReferencedNet(_referenceEnvironment);
        }
        
        else if (obj instanceof EdifLibraryNameReference) {
            EdifLibraryNameReference reference = (EdifLibraryNameReference) obj;
            return reference.getReferencedLibrary(_referenceEnvironment);
        }
        
        else if (obj instanceof EdifSingleBitPortNameReference) {
            EdifSingleBitPortNameReference reference = (EdifSingleBitPortNameReference) obj;
            return reference.getReferencedSingleBitPort(_referenceEnvironment);
        }
        
        return obj;
    }


    protected EdifEnvironment _referenceEnvironment;
}
