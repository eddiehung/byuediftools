package edu.byu.ece.edif.util.bitblast;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.core.RenamedObject;
import edu.byu.ece.edif.tools.sterilize.lutreplace.EdifEnvironmentCopy;

public class EdifEnvironmentBitBlast extends EdifEnvironmentCopy {

	public EdifEnvironmentBitBlast(EdifEnvironment env, Set<EdifPort> bitBlastPorts) throws EdifNameConflictException {
		super(env);
		_bitBlastPorts = Collections.unmodifiableSet(bitBlastPorts);
		_esbpMap = new LinkedHashMap<EdifSingleBitPort, EdifSingleBitPort>();
	}

    protected void addEdifPorts(EdifCell origCell, EdifCell newCell) throws EdifNameConflictException {
        // copy cell interface
        for (EdifPort oldPort : origCell.getPortList()) {
        	int width = oldPort.getWidth();
        	if (_bitBlastPorts.contains(oldPort) && width > 1) {
        		for (int i = 0; i < width; i++) {
        			String nameString = oldPort.getName() + "_" + i;
        			EdifNameable singleName = NamedObject.createValidEdifNameable(nameString);
        			EdifNameable name = new RenamedObject(singleName, oldPort.getName() + "(" + i + ")");        			
        			EdifPort newPort = newCell.addPort(name, 1, oldPort.getDirection());
        			newPort.copyProperties(oldPort);
        			_esbpMap.put(oldPort.getSingleBitPort(i), newPort.getSingleBitPort(0));
        		}
        	}
        	else {
        		EdifPort newPort = newCell.addPort(oldPort.getEdifNameable(), width, oldPort.getDirection());
        		newPort.copyProperties(oldPort);
        		for (int i = 0; i < width; i++) {
        			_esbpMap.put(oldPort.getSingleBitPort(i), newPort.getSingleBitPort(i));
        		}
        	}
        }
    }

    protected void addEdifPortRef(EdifNet newNet, EdifPortRef oldRef) {
        EdifSingleBitPort oldSbp = oldRef.getSingleBitPort();
        EdifSingleBitPort newSbp = _esbpMap.get(oldSbp);
        EdifCellInstance newEci = null;
        if (oldRef.getCellInstance() != null)
            newEci = _instanceMap.get(oldRef.getCellInstance());
        EdifPortRef newEpr = new EdifPortRef(newNet, newSbp, newEci);
        newNet.addPortConnection(newEpr);
    }

	Set<EdifPort> _bitBlastPorts;
	Map<EdifSingleBitPort, EdifSingleBitPort> _esbpMap;
}
