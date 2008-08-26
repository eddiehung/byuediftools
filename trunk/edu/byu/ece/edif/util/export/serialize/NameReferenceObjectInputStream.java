package edu.byu.ece.edif.util.export.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import edu.byu.ece.edif.core.EdifEnvironment;

/**
 * This is a customized version of ObjectInputStream used for EDIF name
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
