/*
 * An interface for describing a name clashing policy
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

import java.io.Serializable;

//////////////////////////////////////////////////////////////////////////
////EdifNameClashPolicy
/**
 * An interface for describing a name clashing policy for elements that will be
 * placed in an EdifNameSpace.
 * 
 * @version $Id:EdifNameClashPolicy.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public interface EdifNameClashPolicy extends Serializable {

    /**
     * @return a boolean representing whether a single name equal to another
     * single name should produce a clash
     */
    public boolean singleClashesSingle();

    /**
     * @return a boolean representing whether a single name equal to another new
     * name should produce a clash
     */
    public boolean singleClashesNew();

    /**
     * @return a boolean representing whether a single name equal to another old
     * name should produce a clash
     */
    public boolean singleClashesOld();

    /**
     * @return a boolean representing whether a new name equal to another new
     * name should produce a clash
     */
    public boolean newClashesNew();

    /**
     * @return a boolean representing whether an old name equal to another old
     * name should produce a clash
     */
    public boolean oldClashesOld();

    /**
     * @return a boolean representing whether an old name equal to another new
     * name should produce a clash
     */
    public boolean oldClashesNew();
}
