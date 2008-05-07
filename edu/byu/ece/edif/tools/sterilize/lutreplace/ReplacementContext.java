package edu.byu.ece.edif.tools.sterilize.lutreplace;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifSingleBitPort;

public interface ReplacementContext {
	// This method returns the EdifCell object that matches with the old EdifCell
	// object. This is the object that the replacer provided for a match.
	public EdifCell getMatchingTemplateCell();
	// Returns the new EdifCell where the replacement logic should be added.
	// This EdifCell is needed so that new logic can be added.
	public EdifCell getNewParentCell();
	// Returns the EdifCellInstance in the old design that needs to be replaced
	// This provides the replacer with any property information (such as INIT)
	public EdifCellInstance getOldInstanceToReplace();
	// Returns the EdifCell in the old environment that is being replaced.
	public EdifCell getOldCellToReplace();
	// This method will provide the "new" net that was created in the new EdifCell
	// That would have otherwise been hooked up to the given Port if replacement
	// did not occur. The replacer should use this net and hook it up to the new
	// appropriate port. Note that the arguments are the "old" single bit port
	// (i.e. the port in the old cell, not the new cell).
	public EdifNet getNewNetToConnect(EdifSingleBitPort oldPort);
}
