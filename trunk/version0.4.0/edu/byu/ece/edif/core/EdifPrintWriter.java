/*
 * Used for generating valid Edif text output.
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import edu.byu.ece.edif.util.merge.DuplicateMergingPolicy;
import edu.byu.ece.edif.util.merge.EdifMergeParser;

/////////////////////////////////////////////////////////////////////////
////EdifPrintWriter
/**
 * Used for generating valid Edif text output. This class extends PrintWriter
 * but also contains a number of convenience methods for properly formatting
 * Edif output.
 * <p>
 * This class keeps track of the indentation during formatted printing. An
 * integer value ({@link EdifPrintWriter#_indenture}) keeps track of the
 * indentation level and the {@link EdifPrintWriter#_indentString} is used as
 * the String printed for each level of indentation.
 * <p>
 * <b>To Do:</b>
 * <p>
 * <ul>
 * <li> Provide a static method that will create a new EdifEnvironment from a
 * EdifCellInstance. See Eric's TMR code for this.
 * <li> Create a method in EdifEnvironment that creates a minimal
 * EdifEnvironment object for the given cell.
 * <p>
 * Algorithm for determining the minimum set of library cells required for a
 * given top-level cell:
 * <ul>
 * <li> Can ignore any libraries that follow the library in which the given cell
 * is defined (higher up in the hierarchy)
 * <li> Can ignore any cells that follow the top-level cell. (these are defined
 * *after* the given cell and thus they are not needed).
 * <li> Can ignore any cells that occur *before* the given cell that are not
 * needed by the cell or any of its children.
 * </ul>
 * </ul>
 * 
 * @version $Id:EdifPrintWriter.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class EdifPrintWriter extends PrintWriter {

    /**
     * Construct an EdifPrintWriter Object using the passed-in OutputStream as
     * its output.
     * 
     * @param out The OutputStream Object that this EdifPrintWriter Object will
     * write to
     */
    public EdifPrintWriter(OutputStream out) {
        super(out);
    }

    /**
     * Construct an EdifPrintWriter Object that will write to the passed in
     * String filename.
     * 
     * @param outputFilename The file that this EdifPrintWriter Object will
     * write to
     */
    public EdifPrintWriter(String outputFilename) throws IOException {
        super(new FileOutputStream(outputFilename));
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Decrement the amount of indenture.
     */
    public void decrIndent() {
        _indenture--;
    }

    /**
     * Increment the amount of indenture.
     */
    public void incrIndent() {
        _indenture++;
    }

    /**
     * This method will create a new Edif file for a given EdifCell object, and
     * print it to the passed-in filename.
     * 
     * @param filename The name of the file to generate
     * @param cell The EdifCell object that is to be put into the top-level of
     * the generated Edif file.
     * @param subFile The EdifEnvironment object that owns the cell.
     */
    public static void printEdifEnvironment(String filename, EdifCell cell) throws IOException {
        // 1. Create a new empty EdifEnvironment object
        int end = filename.lastIndexOf('.');
        if (end < 0)
            end = filename.length();
        String base_name = filename.substring(0, end);
        EdifEnvironment newEnv = new EdifEnvironment(NamedObject.createValidEdifNameable(base_name));

        // 3. Copy cell and all referenced cells to new EdifEnvironment
        EdifCell newCell = EdifMergeParser.copyCellDeep(cell, null, newEnv.getLibraryManager(),
                new DuplicateMergingPolicy());

        // 4. Create top level design and set in environment
        EdifCellInstance top = new EdifCellInstance(NamedObject.createValidEdifNameable("ROOT"), null, newCell);
        EdifDesign oldDesign = cell.getLibrary().getLibraryManager().getEdifEnvironment().getTopDesign();
        EdifDesign design = new EdifDesign(NamedObject.createValidEdifNameable(oldDesign.getName()));
        design.setTopCellInstance(top);
        newEnv.setTopDesign(design);
        // copy design properties
        if (oldDesign.getPropertyList() != null) {
            for (Iterator it = oldDesign.getPropertyList().values().iterator(); it.hasNext();) {
                Property p = (Property) it.next();
                design.addProperty((Property) p.clone());
            }
        }

        // 5. Output the edif file
        EdifPrintWriter epw = new EdifPrintWriter(new FileOutputStream(filename));
        newEnv.toEdif(epw);
        epw.flush();
        epw.close();
    }

    /**
     * Print the passed-in String object with the right amount of preceding
     * indentation.
     * 
     * @param str The String object that will be printed preceded by indentation
     */
    public void printIndent(String str) {
        for (int i = 0; i < _indenture; i++)
            print(_indentString);
        print(str);
    }

    /**
     * Print the end of line character to the PrintWriter.
     */
    public void println() {
        print(_endOfLineChar);
    }

    /**
     * Print out the passed-in string followed by the end of line character.
     * 
     * @param str The String that will be written to the PrintWriter followed by
     * the end of line character
     */
    public void println(String str) {
        print(str + _endOfLineChar);
    }

    /**
     * Print the passed-in String object with the right amount of preceding
     * indentation, followed by the newline character.
     * 
     * @param str The String object that will be printed preceded by
     * indentation, and followed by the end of line character
     */
    public void printlnIndent(String str) {
        printIndent(str);
        println();
    }

    /**
     * Print quotes around the passed in String object.
     * 
     * @param str The String object that will have quotes printed around it
     */
    public void printQuote(String str) {
        print("\"" + str + "\"");
    }

    ///////////////////////////////////////////////////////////////////
    ////                     private variables                     ////

    /**
     * Contains the endOfLineChar.
     */
    private static final String _endOfLineChar = "\n";

    /**
     * Contains the indent String.
     */
    private static final String _indentString = "  ";

    /**
     * Contains the amount of indenture.
     */
    private int _indenture = 0;
}
