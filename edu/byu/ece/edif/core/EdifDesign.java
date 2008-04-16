/*
 * Represents an EdifDesign within an EdifEnvironment object.
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
////EdifDesign
/**
 * Represents an EdifDesign within an EdifEnvironment object. An EdifDesign
 * identifies the cell instance at the top level of the hierarchy of an Edif
 * file. A design has a name (renamed if necessary), properties, and an
 * EdifCellInstance. Note that the name of the design may be different than the
 * name of the top EdifCellInstance.
 * 
 * @see EdifEnvironment
 * @version $Id:EdifDesign.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class EdifDesign extends NamedPropertyObject implements EdifOut {

    /**
     * Construct an empty, named EdifDesign Object
     * 
     * @param name The name of this EdifDesign Object
     * @throws InvalidEdifNameException
     */
    public EdifDesign(String name) throws InvalidEdifNameException {
        super(name);
    }

    /**
     * Construct an empty, named EdifDesign Object
     * 
     * @param name The Object that holds name information for this EdifDesign
     * Object
     */
    public EdifDesign(EdifNameable name) {
        super(name);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Two EdifDsign objects are considered edifEquals if:
     * <ul>
     * <li> The EdifCellInstance objects in each EdifDesign object are edifEqual</li>
     * <li> Both EdifDesign objects have the same name (as defined by
     * getName().equals()</li>
     * <li> Both EdifDesign objects have the same properties (or both have no
     * properties)</li>
     * </ul>
     * 
     * @param o Comparison EdifDesign object
     * @return true of the two EdifDesign objects are edifEqual
     */
    public boolean edifEquals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EdifDesign))
            return false;

        EdifDesign that = (EdifDesign) o;

        return (this.getName() == null ? that.getName() == null : this.getName().equals(that.getName()))
                && (this.getPropertyList() == null ? that.getPropertyList() == null : this.getPropertyList().equals(
                        that.getPropertyList()))
                && (this.getTopCellInstance() == null ? that.getTopCellInstance() == null : this.getTopCellInstance()
                        .edifEquals(that.getTopCellInstance()));
    }

    /**
     * Return the top cell instance of this design.
     * 
     * @return an EdifCellInstance Object that refers to the top EdifCell of
     * this design
     */
    public EdifCellInstance getTopCellInstance() {
        return _topCellInstance;
    }

    /**
     * Set the top EdifCellInstance of this design.
     * 
     * @param cellInstance The EdifCellInstance Object that this EdifDesign
     * Object will refer to
     */
    public void setTopCellInstance(EdifCellInstance cellInstance) {
        _topCellInstance = cellInstance;
    }

    /**
     * Generate the EDIF text for this object.
     * 
     * @param epw The EdifPrintWriter Object that will write this Object's EDIF
     * data to
     */
    public void toEdif(EdifPrintWriter epw) {

        epw.flush();
        epw.printIndent("(design ");
        getEdifNameable().toEdif(epw);
        epw.println();
        epw.incrIndent();
        epw.printIndent("(cellRef " + _topCellInstance.getCellType().getName());
        epw.println(" (libraryRef " + _topCellInstance.getCellType().getLibrary().getName() + "))");
        epw.flush();

        PropertyList pl = getPropertyList();
        if (pl != null)
            pl.toEdif(epw);

        //        Map l = getPropertyList();
        //        if (l != null) {
        //            Iterator it = l.values().iterator();
        //            while (it.hasNext()) {
        //                Property p = (Property) it.next();
        //                p.toEdif(epw);
        //            }
        //        }

        epw.decrIndent();
        epw.printlnIndent(")");
    }

    /**
     * Return a String representation of this Object.
     * 
     * @return A String representing this object, and the top EdifCellInstance
     */
    @Override
    public String toString() {
        return getEdifNameable().toString();
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /**
     * The top EdifCellInstance of this design.
     */
    private EdifCellInstance _topCellInstance;

}
