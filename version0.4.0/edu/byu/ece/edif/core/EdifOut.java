/*
 * Used for objects that can generate valid EDIF output.
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
////EdifOut
/**
 * Used for objects that can generate valid EDIF output. A class that implements
 * this interface must 'print' the Edif source using the provided
 * {@link EdifPrintWriter} object.
 * 
 * @version $Id:EdifOut.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 * @see EdifPrintWriter
 */
public interface EdifOut {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * This method will convert the interfacing object into EDIF format and
     * write it to the passed-in EdifPrintWriter Object.
     * 
     * @param epw The EdifPrintWriter Object that the interfacing Object will
     * write EDIF data to
     */
    public void toEdif(EdifPrintWriter epw);
}
