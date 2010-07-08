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
import edu.byu.ece.edif.tools.replicate.nmr.CircuitDescription;
import edu.byu.ece.edif.util.export.serialize.NameReferenceObjectInputStream;
import edu.byu.ece.edif.util.export.serialize.NameReferenceObjectOutputStream;

public class CircuitDescriptionCommandGroup extends AbstractCommandGroup {

	public CircuitDescriptionCommandGroup() {
		super();

		// Input filename flag
		input_file_option = new FlaggedOption(INPUT_OPTION);
		input_file_option.setStringParser(JSAP.STRING_PARSER);
		input_file_option.setRequired(JSAP.REQUIRED);
		input_file_option.setShortFlag('c');
		input_file_option.setLongFlag(INPUT_OPTION);
		input_file_option.setUsageName(INPUT_OPTION);
		input_file_option.setHelp("Circuit description input filename. Required.");
		this.addCommand(input_file_option);
	}

	public static String getInputFileName(JSAPResult result) {
		return result.getString(INPUT_OPTION);
	}

	public static CircuitDescription getCircuitDescription(JSAPResult result, EdifEnvironment referenceEnv, PrintStream out) {
		FileInputStream fis = null;

		NameReferenceObjectInputStream in = null;

		String filename = getInputFileName(result);
		if (!filename.contains("."))
			filename = filename.concat(".cdesc");

		out.print("Loading file " + filename + " . . .");
		try {
			fis = new FileInputStream(filename);
		} catch (FileNotFoundException ex) {
			out.println("File Not Found: Cannot find the specified file. Make"
					+ " sure the file is present and you have access to open it.");
			out.println(ex.getMessage());

		}

		CircuitDescription cDesc = null;

		try {
			in = new NameReferenceObjectInputStream(fis, referenceEnv);
			cDesc = (CircuitDescription) in.readObject();
			in.close();
		} catch (ClassCastException ex) {
			out.println("The File you tried to load is the wrong type/class");

		} catch (ClassNotFoundException ex) {
			out.println("Your class file is an old version. Please " + "rerun the program that created this file\n"
					+ ex);
			ex.printStackTrace();
		} catch (IOException ex) {
			out.println("IOException: Unable to parse objects in file. Make "
					+ "sure that the file is a valid circuit analysis file.");
			out.println(ex.getMessage());
		}

		if (cDesc == null)
			throw new EdifRuntimeException("Unknown error while deserializing CircuitAnalysisDescription");
		else
			out.println("Done");
		return cDesc;
	}

	public static void writeCircuitDescription(JSAPResult result, EdifEnvironment referenceEnv, CircuitDescription cDesc, PrintStream out) {

		String filename = getInputFileName(result);
		writeCircuitDescription(filename, referenceEnv, cDesc, out);

	}

	public static void writeCircuitDescription(String filename, EdifEnvironment referenceEnv, CircuitDescription cDesc, PrintStream out) {

		FileOutputStream fos = null;

		NameReferenceObjectOutputStream oos = null;

		if (!filename.contains("."))
			filename = filename.concat(".cdesc");

		out.print("Opening file " + filename + " for writing . . .");

		try {
			fos = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			out.println("Unable to open file: " + filename + " for writing.");
		}

		try {
			oos = new NameReferenceObjectOutputStream(fos, referenceEnv);
			oos.writeObject(cDesc);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			out.println("Error writing circuit description to file:");
			e.printStackTrace();			
		}

		out.println("Done");

	}

	public static final String INPUT_OPTION = "c_desc";
	public FlaggedOption input_file_option;
}
