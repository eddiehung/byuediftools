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

import java.util.HashMap;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifSingleBitPort;

public class SimpleReplacementContext implements ReplacementContext {

    public SimpleReplacementContext(EdifCell matchCell, EdifCell newParent, EdifCellInstance oldInstance) {
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
        _oldSBPortNewNetMap.put(oldSBPort, newNet);
    }

    EdifCell _matchingTemplateCell;

    EdifCell _newParentCell;

    EdifCellInstance _oldInstanceToReplace;

    HashMap<EdifSingleBitPort, EdifNet> _oldSBPortNewNetMap = new HashMap<EdifSingleBitPort, EdifNet>();

}