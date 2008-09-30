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

    public String getAuthor() {
    	return _author;
    }

    public void setAuthor(String author) {
		_author = author;
	}

    public java.util.Date getDate() {
    	return _date;
    }
    
    public void setDate(java.util.Date date) {
    	_date = date;
    }

    /**
     * Set the date tag with the current time.
     */
    public void setDateWithCurrentTime() {
    	_date = new java.util.Date();
    }
    
    public String getProgram() {
    	return _program;
    }
    
	public void setProgram(String program) {
		_program = program;
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

    public String getVersion() {
    	return _version;
    }
    
    public void setVersion(String version) {
		_version = version;
	}

	/**
     * Convert this Object to EDIF format and prints it to the passed in
     * EdifPrintWriter. defaults to tool "BYU EDIF tools" and version "version
     * 0.3.0"
     * 
     * @param epw The EdifPrintWriter that the EDIF data will be written to
     */
    public void toEdif(EdifPrintWriter epw) {
        toEdif(epw, true);
    }

    public void toEdif(EdifPrintWriter epw, boolean useCurrDate) {
    	
    	DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH mm ss");
        
        if (useCurrDate)
        	setDateWithCurrentTime();
        String timeStamp = (dateFormat.format(_date));

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
        epw.printlnIndent("(author \"" + _author + "\")");
        epw.printlnIndent("(program \"" + _program + "\" (version \"" + _version + "\"))");
        epw.decrIndent();
        epw.printlnIndent(")");//close written
        epw.decrIndent();
        epw.printlnIndent(")");//close status

        _libraries.toEdif(epw);

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

    /**
     * The EdifLibraryManager object representing the environment libraries. An
     * empty manager is created during construction.
     */
    protected EdifLibraryManager _libraries = new EdifLibraryManager(this);

    /**
     * The top-level design associated with this environment.
     */
    protected EdifDesign _topDesign;

    /**
     * The date timestamp associated with this environment. 
     */
    protected java.util.Date _date;
    
    /**
     * Author string to be printed in final EDIF output. 
     */
    protected String _author = "BYU CCL (AUTHOR NOT SET)";
    
    /**
     * Program string to be printed in final EDIF output.
     */
    protected String _program = "PROGRAM NOT SET";

    /**
     * Version string to be printed in final EDIF output.
     */
    protected String _version = "VERSION NOT SET";
    
}
