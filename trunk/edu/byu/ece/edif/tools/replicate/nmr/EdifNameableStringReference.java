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

import java.io.Serializable;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.NamedPropertyObject;

/**
 * Represents the String reference of an EdifNameable object without storing the
 * reference to the object. Only the valid EdifNameable String is stored in this
 * class. This class is used to store the name for later referencing from a
 * parent Edif class object.
 * <p>
 * This is a fairly generic object and does not store the context of the
 * original EdifObject (i.e. store any parent relationships). A variety of
 * methods are provided for finding actual Edif objects within a given context
 * from the saved name reference.
 */
public class EdifNameableStringReference implements Serializable {
    public static final long serialVersionUID = 42L;

    public EdifNameableStringReference(NamedPropertyObject obj) {
        _edifName = obj.getName();
    }

    public EdifLibrary getEdifLibrary(EdifEnvironment env) {
        return env.getLibrary(_edifName);
    }

    public EdifCell getEdifCell(EdifLibrary lib) {
        return lib.getCell(_edifName);
    }

    public EdifNet getEdifNet(EdifCell cell) {
        return cell.getNet(_edifName);
    }

    public EdifCellInstance getEdifCellInstance(EdifCell cell) {
        return cell.getInstance(_edifName);
    }

    public EdifPort getEdifPort(EdifCell cell) {
        return cell.getPort(_edifName);
    }

    public String toString() {
        return _edifName;
    }

    protected String _edifName;
}