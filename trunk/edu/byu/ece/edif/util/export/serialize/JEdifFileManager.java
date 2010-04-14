package edu.byu.ece.edif.util.export.serialize;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.tools.flatten.PreservedHierarchyByNames;

/**
 * Manages the reading from and writing to .jedif files. All the code for managing this file format
 * is contained here.
 */
public class JEdifFileManager {

	/**
	 * Performs the deserialization of a .jedif file and creates a JEdifFileContents object.
	 * 
	 */
    public static JEdifFileContents getJEdifFileContents(String filename, PrintStream out) {
        FileInputStream fis = null;
        ObjectInputStream in = null;
        EdifEnvironment env = null;
        PreservedHierarchyByNames hierarchy = null;
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
            env = (EdifEnvironment) in.readObject();
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

        try {
            in.close();
        } catch (IOException e) {
            out.println("IOException: error while trying to close input stream.");
            out.println(e.getMessage());
        }
        
        if (env == null)
            throw new NullPointerException("Returning null top-level cell!");
        else
            out.println("Done");
        
        return new JEdifFileContents(env, hierarchy);
    }
    
    /**
     * Serialization method (without the PreservedHierarchyByNames
     */
    public static void writeJEdifFile(PrintStream out, String filename, EdifEnvironment env) {
        writeJEdifFile(out, filename, env, null);
    }
    
    /**
     * Serialization method.
     */
    public static void writeJEdifFile(PrintStream out, String filename, EdifEnvironment env, PreservedHierarchyByNames hierarchy) {
        out.println("Creating file " + filename + " . . .");
        FileOutputStream fos = null;
        ObjectOutputStream out_object = null;
        try {
            fos = new FileOutputStream(filename);
            out_object = new ObjectOutputStream(fos);
            out_object.writeObject(env);
            if (hierarchy != null) {
                out_object.writeObject(hierarchy);
            }
            out_object.close();
        } catch (IOException ex) {
            out.println();
            ex.printStackTrace();
        }
        out.println("Done");
    }
    
}
