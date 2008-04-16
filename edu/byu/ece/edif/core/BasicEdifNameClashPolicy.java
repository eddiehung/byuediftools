/*
 * Defines standard name clashing policies for EDIF name spaces.
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

//////////////////////////////////////////////////////////////////////////
//// BasicEdifNameClashPolicy
/**
 * Defines standard name clashing policies for EDIF name spaces. Clashing rules
 * (according to ngdbuild) for each EDIF name space:
 * <p>
 * EdifCellInstances in an EdifCell
 * <ul>
 * <li> single-single - error
 * <li> single-new - error
 * <li> single-old - warning, auto-rename
 * <li> new-new - error
 * <li> old-old - warning, auto-rename
 * <li> old-new - none
 * </ul>
 * <p>
 * EdifNets in an EdifCell
 * <li> single-single - warning, auto-rename
 * <li> single-new - none
 * <li> single-old - warning, auto-rename
 * <li> new-new - none
 * <li> old-old - warning, auto-rename
 * <li> old-new - none
 * </ul>
 * <p>
 * EdifLibraries in an EdifLibraryManager
 * <ul>
 * <li> single-single - error
 * <li> single-new - error
 * <li> single-old - none
 * <li> new-new - error
 * <li> old-old - none
 * <li> old-new - none
 * </ul>
 * <p>
 * EdifCells in an EdifLibrary
 * <ul>
 * <li> single-single - error
 * <li> single-new - error
 * <li> single-old - none
 * <li> new-new - error
 * <li> old-old - none
 * <li> old-new - none
 * </ul>
 * <p>
 * EdifPorts in an EdifCellInterface
 * <ul>
 * <li> single-single - error
 * <li> single-new - error
 * <li> single-old - error
 * <li> new-new - error
 * <li> old-old - error
 * <li> old-new - none
 * </ul>
 * <p>
 * This class is designed to provide a set of policies that can be used for each
 * EDIF namespace. Because of the map implementation of cell instance and net
 * collections, new-new, new-single, and single-single combinations should never
 * be allowed even though ngdbuild allows new-new and single-new duplications
 * for nets. With this taken into account, there are really only two basic
 * policies that need to be used by the EDIF name spaces. They are the
 * anyButOldWithNewClashPolicy (to be used by EdifNets in an EdifCell,
 * EdifCellInstances in an EdifCell, EdifPorts in an EdifCellInterface) and the
 * singleAndNewClashPolicy (to be used by: EdifLibraries in an
 * EdifLibraryManager, EdifCells in an EdifLibrary)
 */
public class BasicEdifNameClashPolicy implements EdifNameClashPolicy {

    public BasicEdifNameClashPolicy(boolean singleClashesSingle, boolean singleClashesNew, boolean singleClashesOld,
            boolean newClashesNew, boolean oldClashesOld, boolean oldClashesNew) {
        _singleClashesSingle = singleClashesSingle;
        _singleClashesNew = singleClashesNew;
        _singleClashesOld = singleClashesOld;
        _newClashesNew = newClashesNew;
        _oldClashesOld = oldClashesOld;
        _oldClashesNew = oldClashesNew;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public variables                  ////

    /**
     * A name clashing policy which causes a clash between any combination of
     * equal names except for a new name with an old name.
     */
    public static final EdifNameClashPolicy anyButOldWithNewClashPolicy = new BasicEdifNameClashPolicy(true, true,
            true, true, true, false);

    /**
     * A name clashing policy which causes a clash between any combination of
     * single and new names but not with any combination that includes an old
     * name.
     */
    public static final EdifNameClashPolicy singleAndNewClashPolicy = new BasicEdifNameClashPolicy(true, true, false,
            true, false, false);

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * @return a boolean representing whether a single name equal to another
     * single name should produce a clash
     */
    public boolean singleClashesSingle() {
        return _singleClashesSingle;
    }

    /**
     * @return a boolean representing whether a single name equal to another new
     * name should produce a clash
     */
    public boolean singleClashesNew() {
        return _singleClashesNew;
    }

    /**
     * @return a boolean representing whether a single name equal to another old
     * name should produce a clash
     */
    public boolean singleClashesOld() {
        return _singleClashesOld;
    }

    /**
     * @return a boolean representing whether a new name equal to another new
     * name should produce a clash
     */
    public boolean newClashesNew() {
        return _newClashesNew;
    }

    /**
     * @return a boolean representing whether an old name equal to another old
     * name should produce a clash
     */
    public boolean oldClashesOld() {
        return _oldClashesOld;
    }

    /**
     * @return a boolean representing whether an old name equal to another new
     * name should produce a clash
     */
    public boolean oldClashesNew() {
        return _oldClashesNew;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    private boolean _singleClashesSingle;

    private boolean _singleClashesNew;

    private boolean _singleClashesOld;

    private boolean _newClashesNew;

    private boolean _oldClashesOld;

    private boolean _oldClashesNew;

}
