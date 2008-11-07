/*
 * Represents a name reference to a real EDIF object.
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

import java.io.Serializable;

/**
 * Represents a name reference to a real EDIF object.
 */
public abstract class EdifGenericNameReference implements Serializable {

    public EdifGenericNameReference(boolean shouldResolve) {
        _shouldResolve = shouldResolve;
    }
    
    public boolean shouldResolve() {
        return _shouldResolve;
    }
    
    protected boolean _shouldResolve = true;
}
