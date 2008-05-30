/*
 * An enumerated type for describing the status of a test method.
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
package edu.byu.ece.edif.test;

//////////////////////////////////////////////////////////////////////////
//// TestStatus
/**
 * An enumerated type for describing the status of a test method.
 * <ul>
 * <li>UNTESTED indicates that the test method did not call any assert methods
 * <li>PASSED indicates that all assert methods called by the test method
 * passed
 * <li>FAILED indicates that one or more assert methods called by the test
 * method did not pass
 * <li>EXCEPTION indicates that the test method threw an exception
 * </ul>
 * 
 * @author Jonathan Johnson
 * @version $Id$
 */
public enum TestStatus {
    UNTESTED, PASSED, FAILED, EXCEPTION
}
