/*
 * A Collection of EdifCellInstances.
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
package edu.byu.ece.edif.util.graph;

import java.util.Collection;

import edu.byu.ece.edif.core.EdifCellInstance;

/**
 * A Collection of EdifCellInstances. All Collection methods deal with
 * EdifCellInstance objects.
 * <p>
 * Idea?:
 * <ul>
 * <li> Do not extend Collection
 * <ul>
 * <li> Figure out which "Collection" methods these classes actually use
 * <li> Come up with -
 * </ul>
 * <li> Provide method: Iterator instanceIterator
 * </ul>
 */
public interface EdifCellInstanceCollection extends Collection<EdifCellInstance> {

}
