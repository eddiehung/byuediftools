/*
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
package edu.byu.ece.edif.util.parse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import edu.byu.ece.edif.arch.Primitives;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifPrintWriter;


/**
 * Provides a stand alone EDIF parser that interacts with the {@link byucc.edif}
 * package. This class extends the {@link EdifParserCore} class which is auto
 * generated from the edif.jj file. Classes that want to integrate an Edif
 * parser should use this object rather than the EdifParserCore. It is much
 * easier to work with the parser in a new class rather than editing the class
 * in a .jj file.
 * 
 * @version $Id:EdifParser.java 144 2008-04-02 01:40:38Z jamesfcarroll $
 */
public class EdifParser extends EdifParserCore {

    /**
     * This method will create an {@link EdifEnvironment} object from an
     * InputStream object.
     * 
     * @param is The input stream that contains the file
     * @param filename The filename that was used to get the file
     * @param prims The Collection of primitive libraries
     * @return An EdifEnvironment that represents the file
     */
    public static EdifEnvironment translate(InputStream is, String filename, Collection prims) throws ParseException {

        EdifParserCore parser = null;
        parser = new EdifParserCore(is);
        parser.edif(); // throws ParseException

        EdifEnvironment edifFile = parser.getEdifEnvironment();
        //edifFile.renameTopCellToMatchName(filename);

        if (prims != null && prims.size() != 0)
            for (Iterator i = prims.iterator(); i.hasNext();) {
                EdifLibrary lib = (EdifLibrary) i.next();
                if (lib != null)
                    Primitives.tagPrimitives(edifFile.getLibraryManager(), lib);
            }

        return edifFile;
    }

    /**
     * This method will create an EdifEnvironment object from a given edif file.
     * This will call the javacc parser and create the data structure from the
     * contents of the given file.
     * 
     * @param filename The filename that was used to get the file
     * @param prims An EdifLibrary of primitives
     * @return An EdifEnvironment that represents the file
     */
    public static EdifEnvironment translate(String filename, EdifLibrary prims) throws ParseException,
            FileNotFoundException {
        InputStream is = new FileInputStream(filename);
        ArrayList primitives = null;
        if (prims != null) {
            primitives = new ArrayList();
            primitives.add(prims);
        }
        return translate(is, filename, primitives);
    }

    /**
     * This method will create an EdifEnvironment object from a given edif file.
     * This will call the javacc parser and create the data structure from the
     * contents of the given file.
     * 
     * @param filename The filename that was used to get the file
     * @param prims The Collection of primitive libraries
     * @return An EdifEnvironment that represents the file TODO: Explain the use
     * of the prims argument
     */
    public static EdifEnvironment translate(String filename, Collection prims) throws ParseException,
            FileNotFoundException {
        InputStream is = new FileInputStream(filename);
        return translate(is, filename, prims);
    }

    /**
     * This method will create an EdifEnvironment object from a given edif file.
     * This will call the javacc parser and create the data structure from the
     * contents of the given file.
     * 
     * @param filename The filename that was used to get the file
     * @return An EdifEnvironment that represents the file
     */
    public static EdifEnvironment translate(String filename) throws ParseException, FileNotFoundException {
        InputStream is = new FileInputStream(filename);
        return translate(is, filename, null);
    }

    public static EdifEnvironment translate(InputStream is) throws ParseException, FileNotFoundException {
        return translate(is, "", null);
    }

    public static void main(String args[]) {
        String usage = "Usage: java EdifParser EDIF_File [-o outputfile]";
        if (args.length < 1) {
            System.out.println(usage);
            System.exit(1);
        } else {
            EdifEnvironment file = null;
            try {
                file = translate(args[0]);
            } catch (ParseException e) {
                System.out.println("Parsing error!\n" + e);
                System.exit(1);
            } catch (FileNotFoundException e) {
                System.out.println("File " + args[0] + " not found");
                System.exit(1);
            }
            if (args.length > 2 && args[1].equals("-o")) {
                String outputFile = args[2];

                try {
                    EdifPrintWriter epw = new EdifPrintWriter(new FileOutputStream(outputFile));
                    System.out.println("Printing to file " + outputFile);
                    file.toEdif(epw);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e);
                    System.exit(1);
                }

            }

            System.out.println("Parser finished successfully.");
        }
    }
}
