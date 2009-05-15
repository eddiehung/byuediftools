/*
 * A parser command group for JEdif binaries.
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
package edu.byu.ece.edif.util.jsap.commandgroups;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.List;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.tools.flatten.PreservedHierarchyByNames;

/**
 * A parser command group for JEdif binaries. TODO:
 * <ul>
 * <li> Change output of inherited flags
 * <li> Add options to change summary
 * <li> Should this class be moved to edu.byu.ece.edif.util.jsap? --jcarroll
 * </ul>
 * 
 * @author unknown (dsgib?)
 */
public class JEdifParserCommandGroup extends InputFileCommandGroup {

    /**
     * @param result
     * @param out
     * @return
     */
    public static EdifEnvironment getEdifEnvironment(JSAPResult result, PrintStream out) {
        return getEdifEnvironment(result, out, null);
    }
    
    public static EdifEnvironment getEdifEnvironment(JSAPResult result, PrintStream out, List<PreservedHierarchyByNames> hierarchyReturn) {
        FileInputStream fis = null;
        ObjectInputStream in = null;
        EdifEnvironment new_top = null;
        PreservedHierarchyByNames hierarchy = null;
        String filename = getInputFileName(result);
        if (!filename.contains("."))
            filename = filename.concat(".jedif");

        out.print("Loading file " + filename + " . . .");
        try {
            fis = new FileInputStream(filename);
        } catch (FileNotFoundException ex) {
            out.println("File Not Found: Cannot find the specified file. Make"
                    + " sure the file is present and you have access to open it.");
            out.println(ex.getMessage());
        }

        try {
            in = new ObjectInputStream(fis);
            new_top = (EdifEnvironment) in.readObject();
        } catch (ClassCastException ex) {
            out.println("The File you tried to load is the wrong type/class");

        } catch (ClassNotFoundException ex) {
            out.println("Your class file is an old version. Please " + "rerun the program that created this file\n"
                    + ex);
            ex.printStackTrace();
        } catch (IOException ex) {
            out.println("IOException: Unable to parse objects in file. Make "
                    + "sure that the file is a valid JEDIF file.");
            out.println(ex.getMessage());
            //ex.printStackTrace();
        }
        
        if (hierarchyReturn != null) {
            try {
                hierarchy = (PreservedHierarchyByNames) in.readObject();
            } catch (EOFException e) {
                // ignore this -- it just means there was no PreservedHierarchyByNames object
                // saved in the .jedif file. It's optional.
            } catch (ClassCastException ex) {
                out.println("The File you tried to load is the wrong type/class");

            } catch (ClassNotFoundException ex) {
                out.println("Your class file is an old version. Please " + "rerun the program that created this file\n"
                        + ex);
                ex.printStackTrace();
            } catch (IOException ex) {
                out.println("IOException: Unable to parse objects in file. Make "
                        + "sure that the file is a valid JEDIF file.");
                out.println(ex.getMessage());
                //ex.printStackTrace();
            }
            if (hierarchy != null)
                hierarchyReturn.add(hierarchy);
        }

        try {
            in.close();
        } catch (IOException e) {
            out.println("IOException: error while trying to close input stream.");
            out.println(e.getMessage());
        }
        
        if (new_top == null)
            throw new NullPointerException("Returning null top-level cell!");
        else
            out.println("Done");
        return new_top;
    }
}