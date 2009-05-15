/* 
 * Converts EDIF objects to name reference objects during serialization
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
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;

/**
 * This is a customized version of ObjectOutputStream used for converting EDIF
 * objects to corresponding name reference objects during serialization. The
 * intended use is to serialize EDIF objects that are part of a reference
 * EdifEnvironment with this class and then deserialize them with
 * NameReferenceObjectInputStream which resolves the name references to the
 * real EDIF objects in the reference EdifEnvironment. An example application
 * would be to serialize an entire EDIF design using regular serialization
 * (i.e. a .jedif file) and then serialize references to particular objects in
 * the design using this class. When the references are deserialized they can
 * be matched up to the original objects automatically.
 * 
 * The supported EDIF object classes that can be replaced with name references
 * automatically are: EdifCell, EdifCellInstance, EdifLibrary, EdifNet, EdifPort,
 * EdifPortRef, and EdifSingleBitPort.
 */
public class NameReferenceObjectOutputStream extends ObjectOutputStream {

	/**
	 * Construct a NameReferenceObjectOutputStream with the given OutputStream
	 * and reference EdifEnvironment
	 * 
	 * @param out
	 * @param referenceEnvironment
	 * @throws IOException
	 */
    public NameReferenceObjectOutputStream(OutputStream out, EdifEnvironment referenceEnvironment) throws IOException {
        super(out);
        enableReplaceObject(true);
        _referenceEnvironment = referenceEnvironment;
    }
    
    /**
     * Replace supported EDIF objects as they go into the stream with appropriate
     * reference objects.
     */
    @Override
    protected Object replaceObject(Object obj) throws IOException {
        
        if (obj instanceof EdifCell) {
            EdifCell cell = (EdifCell) obj;
            EdifLibrary lib = cell.getLibrary();
            String libName = lib.getName();
            if (_referenceEnvironment.getLibrary(libName) == lib) {
                return EdifCellNameReference.getReference(cell);
            }
            else
                throw new EdifSerializationException("Attempted serialization of cell not in reference environment");
        }
        
        else if (obj instanceof EdifCellInstance) {
            EdifCellInstance instance = (EdifCellInstance) obj;
            EdifCell cell = instance.getParent();
            EdifLibrary lib = null;
            if (cell != null)
            	lib = cell.getLibrary();
            try {
            	if (lib != null) {
            		EdifLibrary rLib = _referenceEnvironment.getLibrary(lib.getName());
            		EdifCell rCell = rLib.getCell(cell.getName());
            		EdifCellInstance rInstance = rCell.getCellInstance(instance.getName());
            		if (instance == rInstance) {
            			return EdifCellInstanceNameReference.getReference(instance);
            		}
            		else throw new NullPointerException();
            	}
            	else { // in this case, we assume the instance is the design's top instance so we check that instead
            		if (instance == _referenceEnvironment.getTopCellInstance()) {
            			return EdifCellInstanceNameReference.getReference(instance);
            		}
            		else throw new EdifSerializationException("Error: EdifCellInstance has no parent cell but is not top instance of reference environment.");
            	}
            }
            catch (NullPointerException e) {
                throw new EdifSerializationException("Attempted serializaiton of cell instance not in reference environment");
            }
        }
        
        else if (obj instanceof EdifPort) {
            EdifPort port = (EdifPort) obj;
            EdifCell cell = port.getEdifCell();
            EdifLibrary lib = cell.getLibrary();
            try {
                EdifLibrary rLib = _referenceEnvironment.getLibrary(lib.getName());
                EdifCell rCell = rLib.getCell(cell.getName());
                EdifPort rPort = rCell.getPort(port.getName());
                if (port == rPort) {
                    return EdifPortNameReference.getReference(port);
                }
                else
                    throw new NullPointerException();
            }
            catch (NullPointerException e) {
                throw new EdifSerializationException("Attempted serialization of port not in reference environment");
            }
            
        }
        
        else if (obj instanceof EdifPortRef) {
            EdifPortRef portRef = (EdifPortRef) obj;
            EdifNet net = portRef.getNet();
            EdifCell cell = net.getParent();
            EdifLibrary lib = cell.getLibrary();
            try {
                EdifLibrary rLib = _referenceEnvironment.getLibrary(lib.getName());
                EdifCell rCell = rLib.getCell(cell.getName());
                EdifNet rNet = rCell.getNet(net.getName());
                boolean match = false;
                for (EdifPortRef rPortRef : rNet.getConnectedPortRefs()) {
                    if (portRef == rPortRef) {
                        match = true;                        
                        break;
                    }
                }
                if (match) {
                    return EdifPortRefNameReference.getReference(portRef);
                }
                else throw new NullPointerException();
            }
            catch (NullPointerException e) {
                throw new EdifSerializationException("Attempted serialization of portRef not in reference environment");
            }
        }
        
        else if (obj instanceof EdifNet) {
            EdifNet net = (EdifNet) obj;
            EdifCell cell = net.getParent();
            EdifLibrary lib = cell.getLibrary();
            try {
                EdifLibrary rLib = _referenceEnvironment.getLibrary(lib.getName());
                EdifCell rCell = rLib.getCell(cell.getName());
                EdifNet rNet = rCell.getNet(net.getName());
                if (net == rNet) {
                    return EdifNetNameReference.getReference(net);
                }
                else
                    throw new NullPointerException();
            }
            catch (NullPointerException e) {
                throw new EdifSerializationException("Attempted serialization of net not in reference environment");
            }
        }
        
        else if (obj instanceof EdifLibrary) {
            EdifLibrary lib = (EdifLibrary) obj;
            EdifLibrary rLib = _referenceEnvironment.getLibrary(lib.getName());
            if (lib == rLib)
                return EdifLibraryNameReference.getReference(lib);
            else
                throw new EdifSerializationException("Attempted serialization of library not in reference environment");
        }
        
        else if (obj instanceof EdifSingleBitPort) {
            EdifSingleBitPort esbp = (EdifSingleBitPort) obj;
            EdifPort port = esbp.getParent();
            EdifCell cell = port.getEdifCell();
            EdifLibrary lib = cell.getLibrary();
            try {
                EdifLibrary rLib = _referenceEnvironment.getLibrary(lib.getName());
                EdifCell rCell = rLib.getCell(cell.getName());
                EdifPort rPort = rCell.getPort(port.getName());
                EdifSingleBitPort rEsbp = rPort.getSingleBitPort(esbp.bitPosition());
                if (esbp == rEsbp) {
                    return EdifSingleBitPortNameReference.getReference(esbp);
                }
                else throw new NullPointerException();
            }
            catch (NullPointerException e) {
                throw new EdifSerializationException("Attempted serialization of single bit port not in reference environment");
            }
        }
                
        return obj;
    }
    
    protected EdifEnvironment _referenceEnvironment;
}
