/*
 * Represents an environment for representing EDIF circuits.
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import edu.byu.ece.edif.test.regression.EdifDifference;

//////////////////////////////////////////////////////////////////////////
////EdifEnvironment
/**
 * <p>
 * Represents an environment for representing EDIF circuits.
 * </p>
 * <p>
 * An EDIF environment represents a list of
 * {@link EdifLibraryManager EDIF libraries} and has a top-level
 * {@link EdifDesign EDIF design}. This object has a name and is not static so
 * multiple EdifEnvironment objects may exist in a single JVM.
 * </p>
 * 
 * @see EdifDesign
 * @see EdifLibraryManager
 * @version $Id:EdifEnvironment.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class EdifEnvironment extends NamedPropertyObject implements EdifOut, Trimable {
    public int serialUID = 42;

    /**
     * Construct an EdifEnvironment Object with the passed-in string as its
     * name.
     * 
     * @param name The name of the constructed EdifEnvironment Object
     * @throws InvalidEdifNameException
     */
    public EdifEnvironment(String name) throws InvalidEdifNameException {
        super(name);
    }

    /**
     * Construct an EdifEnvironment Object with the passed-in EdifNameable as
     * its name Object.
     * 
     * @param name The EdifNameable name of the constructed EdifEnvironment
     * Object
     */
    public EdifEnvironment(EdifNameable name) {
        super(name);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Two EdifEnvironment objects are considered edifEquals if:
     * <ul>
     * <li> The EdifLibraryManager objects in each of the EdifEnvironment
     * objects are edifEqual</li>
     * <li> The EdifDesign objects in each of the EdifEnvironment objects are
     * edifEqual</li>
     * <li> Both EdifEnvironment objects have the same name (as defined by
     * getName().equals()</li>
     * <li> Both EdifEnvironment objects have the same properties (or both have
     * no properties)</li>
     * </ul>
     * 
     * @param o Comparison EdifEnvironment object
     * @return true of the two EdifEnvrionment objects are edifEqual
     */
    public boolean edifEquals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EdifEnvironment))
            return false;

        EdifEnvironment that = (EdifEnvironment) o;

        return (this.getName() == null ? that.getName() == null : this.getName().equals(that.getName()))
                && (this.getTopDesign() == null ? that.getTopDesign() == null : this.getTopDesign().edifEquals(
                        that.getTopDesign()))
                && (this.getPropertyList() == null ? that.getPropertyList() == null : this.getPropertyList().equals(
                        that.getPropertyList()))
                && (this.getLibraryManager() == null ? that.getLibraryManager() == null : this.getLibraryManager()
                        .edifEquals(that.getLibraryManager()));
    }

    /**
     * Calls {@link edifDiff} with verbose output
     * 
     * @param o
     * @return
     */
    public List<EdifDifference> edifDiff(Object o) {
        return edifDiff(o, true);
    }

    public List<EdifDifference> edifDiff(Object o, boolean verbose) {
        List<EdifDifference> differences = new ArrayList<EdifDifference>();
        if (this == o)
            return differences; // No differences!
        if (this.edifEquals(o))
            return differences; // No differences! Yipee!
        if (!(o instanceof EdifEnvironment)) {
            differences.add(new EdifDifference(this, o));
            return differences;
        }

        EdifEnvironment that = (EdifEnvironment) o;

        if (!this.getName().equals(that.getName())) {
            differences.add(new EdifDifference(this.getName(), that.getName()));
            return differences; // if the names are different, we don't want to continue
        }

        if (!(this.getTopDesign() == null ? that.getTopDesign() == null : this.getTopDesign().getName() == null ? that
                .getTopDesign().getName() == null : this.getTopDesign().getName().equals(that.getTopDesign().getName()))) {

            differences.add(new EdifDifference(this.getTopDesign(), that.getTopDesign()));
            //			if (this.getTopDesign() != null)
            //				differences.add(this.getTopDesign().edifDiff(that.getTopDesign()));
        }
        if (!(this.getPropertyList() == null ? that.getPropertyList() == null : this.getPropertyList().equals(
                that.getPropertyList())))
            differences.add(new EdifDifference(this.getPropertyList(), that.getPropertyList()));
        if (!(this.getLibraryManager() == null ? that.getLibraryManager() == null : this.getLibraryManager().equals(
                that.getLibraryManager()))) {
            differences.add(new EdifDifference(this.getLibraryManager(), that.getLibraryManager()));
            if (this.getLibraryManager() != null)
                differences.addAll(this.getLibraryManager().edifDiff(that.getLibraryManager()));
        }

        return differences;
    }

    /**
     * Add the passed-in library to the appropriate position.
     * 
     * @param library The library to add TODO: should this method even exist?
     */
    public void addLibrary(EdifLibrary library) throws EdifNameConflictException {
        _libraries.addLibrary(library);
    }

    /**
     * Return the library that matches the passed in string.
     * 
     * @param lib The String name to match for when fetching the library
     */
    public EdifLibrary getLibrary(String lib) {
        return _libraries.getLibrary(lib);
    }

    /**
     * Return the library manager.
     * 
     * @return This EdifEnvironment's EdifLibraryManager
     */
    public EdifLibraryManager getLibraryManager() {
        return _libraries;
    }

    /**
     * Return the top cell of the design.
     * 
     * @return The top EdifCell of this design
     */
    public EdifCell getTopCell() {
        return getTopCellInstance().getCellType();
    }

    /**
     * Return the top instance of the design.
     * 
     * @return The top EdifCellInstance of this design
     */
    public EdifCellInstance getTopCellInstance() {
        return _topDesign.getTopCellInstance();
    }

    /**
     * Return the top design.
     * 
     * @return The top EdifDesign Object of this EdifEnvironment
     */
    public EdifDesign getTopDesign() {
        return _topDesign;
    }

    /**
     * Set the top design.
     * 
     * @param design The object that will become the new design for this
     * EdifEnvironment
     */
    public void setTopDesign(EdifDesign design) {
        _topDesign = design;
    }

    public void setTopCell(EdifCell cell) {
        EdifCellInstance tmrInstance = null;
        EdifDesign newDesign = null;
        try {
            tmrInstance = new EdifCellInstance(cell.getName(), null, cell);
            newDesign = new EdifDesign(cell.getEdifNameable());
        } catch (InvalidEdifNameException e1) {
            e1.toRuntime();
        }
        newDesign.setTopCellInstance(tmrInstance);
        // copy design properties
        EdifDesign oldDesign = getTopDesign();
        if (oldDesign.getPropertyList() != null) {
            for (Object o : oldDesign.getPropertyList().values()) {
                Property p = (Property) o;
                newDesign.addProperty((Property) p.clone());
            }
        }
        setTopDesign(newDesign);

    }

    /**
     * Convert this Object to EDIF format and prints it to the passed in
     * EdifPrintWriter. defaults to tool "BYU EDIF tools" and version "version
     * 0.3.0"
     * 
     * @param epw The EdifPrintWriter that the EDIF data will be written to
     */
    public void toEdif(EdifPrintWriter epw) {
        toEdif(epw, "BYU EDIF tools", "version 0.3.0");
    }

    /**
     * Convert this Object to EDIF format and prints it to the passed in
     * EdifPrintWriter.
     * 
     * @param epw The EdifPrintWriter that the EDIF data will be
     * @param tool String toolname
     * @param version String version string written to
     */
    public void toEdif(EdifPrintWriter epw, String tool, String version) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH mm ss");
        java.util.Date date = new java.util.Date();
        String timeStamp = (dateFormat.format(date));

        epw.print("(edif ");
        getEdifNameable().toEdif(epw);
        epw.println();

        epw.incrIndent();
        epw.printlnIndent("(edifVersion 2 0 0)");
        epw.printlnIndent("(edifLevel 0)");
        epw.printlnIndent("(keywordMap (keywordLevel 0))");

        epw.printlnIndent("(status");
        epw.incrIndent();
        epw.printlnIndent("(written");
        epw.incrIndent();
        epw.printlnIndent("(timeStamp " + timeStamp + ")");
        epw.printlnIndent("(author \"" + tool + "\")");
        epw.printlnIndent("(program \"" + tool + "\" (version \"" + version + "\"))");
        epw.decrIndent();
        epw.printlnIndent(")");//close written
        epw.decrIndent();
        epw.printlnIndent(")");//close status

        //          (status
        //          (written
        //          (timeStamp 2007 5 2 10 50 53)
        //          (author "Synplicity, Inc.")
        //          (program "Synplify" (version "7.3.5, Build 256R")) )

        _libraries.toEdif(epw);

        //        Iterator it = getLibraryManager().iterator();
        //        while (it.hasNext()) {
        //            EdifLibrary el = (EdifLibrary) it.next();
        //            el.toEdif(epw);
        //        }

        if (_topDesign != null)
            _topDesign.toEdif(epw);

        epw.decrIndent();
        epw.println(")");
        epw.flush();
        epw.close();

    }

    /**
     * Return a String representation of this object.
     * 
     * @return A String representing the name and libraries contained in this
     * EdifEnvironment Object
     */
    @Override
    public String toString() {
        return getEdifNameable().toString();
    }

    /**
     * Trim the size of this object.
     */
    public void trimToSize() {
        _libraries.trimToSize();
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /**
     * The EdifLibraryManager object representing the environment libraries. An
     * empty manager is created during construction.
     */
    private EdifLibraryManager _libraries = new EdifLibraryManager(this);

    /**
     * The top-level design associated with this environment.
     */
    private EdifDesign _topDesign;

}
