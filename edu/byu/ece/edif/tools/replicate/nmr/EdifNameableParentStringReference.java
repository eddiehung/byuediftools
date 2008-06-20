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
package edu.byu.ece.edif.tools.replicate.nmr;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;

/**
 * Represents the String reference of an EdifCell object including a reference
 * to any parent owning object. This provides more information than
 * EdifNameableStringReference and can be used to reference objects from larger
 * aggregate objects by dereferencing the parent first.
 */
public class EdifNameableParentStringReference extends EdifNameableStringReference {
    public static final long serialVersionUID = 42L;

    /**
     * Constructor for a EdifCell object. Store a reference to the parent
     * library.
     */
    public EdifNameableParentStringReference(EdifCell cell) {
        super(cell);
        _parent = new EdifNameableStringReference(cell.getLibrary());
    }

    public EdifCell getEdifCell(EdifEnvironment env) {
        EdifLibrary lib = _parent.getEdifLibrary(env);
        if (lib == null)
            return null;
        return lib.getCell(_edifName);
    }

    /**
     * Returns the name of the EdifCell
     */
    public String toString() {
        return _parent.toString();
    }

    protected EdifNameableStringReference _parent;
}
