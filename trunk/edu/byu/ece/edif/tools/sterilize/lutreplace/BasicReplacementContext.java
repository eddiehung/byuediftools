package edu.byu.ece.edif.tools.sterilize.lutreplace;

import java.util.Collection;
import java.util.HashMap;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifSingleBitPort;

public class BasicReplacementContext implements ReplacementContext {

    public BasicReplacementContext(EdifCell newParent, EdifCellInstance oldInstance) {
        _newParentCell = newParent;
        _oldInstanceToReplace = oldInstance;
    }
    
    public EdifCell getNewParentCell() {
        return _newParentCell;
    }

    public EdifNet getNewNetToConnect(EdifSingleBitPort oldPort) {
    	return _oldSBPortNewNetMap.get(oldPort);
    }

    public void addOldSBPortNewNetAssociation(EdifSingleBitPort oldSBPort, EdifNet newNet) {
    	_oldSBPortNewNetMap.put(oldSBPort,newNet);
    }

    public EdifNet getNewNetToConnect(String portName, int bitNum) {
    	EdifSingleBitPort esbp = getOldSingleBitPort(portName, bitNum);
    	if (esbp != null)
    		return getNewNetToConnect(esbp);
    	return null;
    }

    public EdifCell getOldCellToReplace() {
    	return _oldInstanceToReplace.getCellType();
    }

    public EdifCellInstance getOldInstanceToReplace() {
    	return _oldInstanceToReplace;
    }

    public EdifNet getNewNetToConnect(String portName) {
    	return getNewNetToConnect(portName, 0);
    }

    protected EdifSingleBitPort getOldSingleBitPort(String portName, int bitNum) {
    	EdifCell oldCellType = _oldInstanceToReplace.getCellType();
    	EdifPort oldPort = oldCellType.getPort(portName);
    	if (oldPort == null)
    		return null;
    	return oldPort.getSingleBitPort(bitNum);
    }

    public Collection<EdifSingleBitPort> getOldSBPorts() {
        return _oldSBPortNewNetMap.keySet();
    }
    
    protected EdifCellInstance _oldInstanceToReplace;
    protected HashMap<EdifSingleBitPort,EdifNet> _oldSBPortNewNetMap = new HashMap<EdifSingleBitPort,EdifNet>();
    protected EdifCell _newParentCell;
}