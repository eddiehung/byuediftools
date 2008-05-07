package edu.byu.ece.edif.tools.sterilize.lutreplace;

import java.util.HashMap;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifSingleBitPort;


public class SimpleReplacementContext implements ReplacementContext {

	public SimpleReplacementContext (EdifCell matchCell, EdifCell newParent, EdifCellInstance oldInstance) {
		_matchingTemplateCell = matchCell;
		_newParentCell = newParent;
		_oldInstanceToReplace = oldInstance;		
	}
	
	public EdifCell getMatchingTemplateCell() {
		return _matchingTemplateCell; 
	}
	
	public EdifCell getNewParentCell() {
		return _newParentCell;
	}

	public EdifCellInstance getOldInstanceToReplace() {
		return _oldInstanceToReplace;
	}

	public EdifCell getOldCellToReplace() {
		return _oldInstanceToReplace.getCellType();
	}

	public EdifNet getNewNetToConnect(EdifSingleBitPort oldPort) {
		return _oldSBPortNewNetMap.get(oldPort);
	}
	
	public void addOldSPBortNewNetAssociation(EdifSingleBitPort oldSBPort, EdifNet newNet) {
		_oldSBPortNewNetMap.put(oldSBPort,newNet);		
	}
	
	EdifCell _matchingTemplateCell;
	EdifCell _newParentCell;
	EdifCellInstance _oldInstanceToReplace;
	HashMap<EdifSingleBitPort,EdifNet> _oldSBPortNewNetMap = new HashMap<EdifSingleBitPort,EdifNet>(); 
	
}