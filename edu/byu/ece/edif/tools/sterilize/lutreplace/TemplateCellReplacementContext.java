/* A simple implementation of the ReplacementContext interface.
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


import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;

/**
 * A simple implementation of the ReplacementContext interface.
 * 
 */
public class TemplateCellReplacementContext extends BasicReplacementContext implements ReplacementContext {

    public TemplateCellReplacementContext(EdifCell matchCell, EdifCell newParent, EdifCellInstance oldInstance) {
        super(newParent, oldInstance);
        _matchingTemplateCell = matchCell;
    }

    public EdifCell getMatchingTemplateCell() {
        return _matchingTemplateCell;
    }

    EdifCell _matchingTemplateCell;
}
