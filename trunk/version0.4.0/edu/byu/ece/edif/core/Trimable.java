/*
 * Interface to enable "trimming" the data structures of a class.
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
package edu.byu.ece.edif.core;

/////////////////////////////////////////////////////////////////////////
////Trimable
/**
 * This interface is used for classes that can "trim" the data structures
 * allocated by the class. These classes will trim the data structures to
 * preserve memory and reduce any unneeded memory overhead.
 * <p>
 * Classes that should probably implement this interface: EdifNet, (anything
 * with a collection or ArrayList).
 * 
 * @version $Id:Trimable.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public interface Trimable {

    /**
     * This method is used to trim the size of all "trimable" data structures
     * within the Object. This method often involves the "trimToSize" method
     * provided by many of the standard Java Collection data structures.
     */
    public void trimToSize();
}
