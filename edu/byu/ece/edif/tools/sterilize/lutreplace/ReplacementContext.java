/*
 * TODO: Insert class description here.
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
package edu.byu.ece.edif.tools.sterilize.lutreplace;

import java.util.Collection;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifSingleBitPort;

public interface ReplacementContext {

    public void addOldSBPortNewNetAssociation(EdifSingleBitPort oldSBPort, EdifNet newNet);
    
    /**
     * @return the new EdifCell where the replacement logic should be added.
     * This EdifCell is needed so that new logic can be added.
     */
    public EdifCell getNewParentCell();

    /**
     * Returns the EdifCellInstance in the old design that needs to be replaced
     * This provides the replacer with any property information (such as INIT)
     */
    public EdifCellInstance getOldInstanceToReplace();

    /**
     * Returns the EdifCell in the old environment that is being replaced.
     */
    public EdifCell getOldCellToReplace();

    /**
     * This method will provide the "new" net that was created in the new
     * EdifCell That would have otherwise been hooked up to the given Port if
     * replacement did not occur. The replacer should use this net and hook it
     * up to the new appropriate port. Note that the arguments are the "old"
     * single bit port (i.e. the port in the old cell, not the new cell).
     */
    public EdifNet getNewNetToConnect(EdifSingleBitPort oldPort);
    
	/** This method will provide the "new" net that was created in the new EdifCell
	 * that would have otherwise been hooked up to the given port. The port list
	 * of the old cell type will be searched using the given port name. The bit
	 * number of the port will be determined by the bitNum parameter. If a
	 * port does not exist with the given name or there is no single bit port
	 * with the given bit number then method will return a null.
	 **/
	public EdifNet getNewNetToConnect(String portName, int bitNum);
	
	/** Return the new net. Assumes a "0" bit number. Will return a null
	 * if the string does not exist.
	 **/
	public EdifNet getNewNetToConnect(String portName);
    
	public Collection<EdifSingleBitPort> getOldSBPorts();	
	
}
