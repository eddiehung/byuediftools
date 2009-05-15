package edu.byu.ece.edif.util.jsap.commandgroups;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;
import edu.byu.ece.edif.util.export.serialize.NameReferenceObjectInputStream;
import edu.byu.ece.edif.util.export.serialize.NameReferenceObjectOutputStream;

public class ReplicationDescriptionCommandGroup extends AbstractCommandGroup {
	public ReplicationDescriptionCommandGroup() {
        super();

        // Input filename flag
        _input_file_option = new FlaggedOption(INPUT_OPTION);
        _input_file_option.setStringParser(JSAP.STRING_PARSER);
        _input_file_option.setRequired(JSAP.REQUIRED);
        _input_file_option.setShortFlag('r');
        _input_file_option.setLongFlag(INPUT_OPTION);
        _input_file_option.setUsageName(INPUT_OPTION);
        _input_file_option.setHelp("Replication description filename. Required.");
        this.addCommand(_input_file_option);

    }

    public void setInputFlagHelp(String str) {
        _input_file_option.setHelp(str);
    }

    public static String getInputFileName(JSAPResult result) {
        return result.getString(INPUT_OPTION);
    }

    public static ReplicationDescription getReplicationDescription(JSAPResult result, EdifEnvironment referenceEnv, PrintStream out) {
        FileInputStream fis = null;
        
        NameReferenceObjectInputStream in = null;

        String filename = getInputFileName(result);
        if (filename == null)
        	return null;
        if (!filename.contains("."))
            filename = filename.concat(".rdesc");

        out.print("Loading file " + filename + " . . .");
        try {
            fis = new FileInputStream(filename);
        } catch (FileNotFoundException ex) {
            throw new EdifRuntimeException("File Not Found: Cannot find the specified file. Make"
                    + " sure the file is present and you have access to open it.");
        }
        
        ReplicationDescription rdesc = null;

        try {
            in = new NameReferenceObjectInputStream(fis, referenceEnv);
            rdesc = (ReplicationDescription) in.readObject();
            in.close();
        } catch (ClassCastException ex) {
            out.println("The File you tried to load is the wrong type/class");

        } catch (ClassNotFoundException ex) {
            out.println("Your class file is an old version. Please " + "rerun the program that created this file\n"
                    + ex);
            ex.printStackTrace();
        } catch (IOException ex) {
            out.println("IOException: Unable to parse objects in file. Make "
                    + "sure that the file is a valid replication description file.");
            out.println(ex.getMessage());
        }

        if (rdesc == null)
            throw new EdifRuntimeException("Unknown error while deserializing ReplicationDescription");
        else
            out.println("Done");
        return rdesc;
    }
    
    public static void writeReplicationDescription(JSAPResult result, EdifEnvironment referenceEnv, ReplicationDescription rDesc, PrintStream out) {
        
        String filename = getInputFileName(result);
        writeReplicationDescription(filename, referenceEnv, rDesc, out);

    }
    
    public static void writeReplicationDescription(String filename, EdifEnvironment referenceEnv, ReplicationDescription rDesc, PrintStream out) {
        
        FileOutputStream fos = null;
        
        NameReferenceObjectOutputStream oos = null;

        if (!filename.contains("."))
            filename = filename.concat(".rdesc");

        out.print("Opening file " + filename + " for writing . . .");

        try {
            fos = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            out.println("Unable to open file: " + filename + " for writing.");
        }

        try {
            oos = new NameReferenceObjectOutputStream(fos, referenceEnv);
            oos.writeObject(rDesc);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            out.println("Error writing replication description to file:");
            e.printStackTrace();
        }
        
        out.println("Done");
        
    }
    
    public static final String INPUT_OPTION = "rep_desc";

    protected FlaggedOption _input_file_option;
}
