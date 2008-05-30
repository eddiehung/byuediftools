/*
 * Thrown when estimated or known utilization exceeds capacity.
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

/////////////////////////////////////////////////////////////////////////
//// OverutilizationException
/**
 * An overutilization exception. This should be thrown when either estimated or
 * known utilization exceeds device capacity.
 * 
 * @author Keith Morgan
 * @version $Id$
 */
public class OverutilizationException extends Exception {

    public OverutilizationException(String msg) {
        super(msg);
    }
}
