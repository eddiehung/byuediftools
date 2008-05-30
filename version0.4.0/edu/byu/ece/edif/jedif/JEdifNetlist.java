/*
 * Create an EDIF file from the JEdif representation of the netlist.
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
package edu.byu.ece.edif.jedif;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifPrintWriter;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.InputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.OutputFileCommandGroup;

/**
 * Create an EDIF file from the JEdif representation of the netlist.
 * <p>
 * TODO: Allow output to be sent to stdout
 * 
 * @author Unknown (dsgib?)
 */
public class JEdifNetlist extends EDIFMain {

    /**
     * @param args
     */
    public static void main(String args[]) {

        // Define the print streams for this program
        PrintStream out = System.out;
        PrintStream err = System.out;

        // Print executable heading
        EXECUTABLE_NAME = "JEdifNetlist";
        TOOL_SUMMARY_STRING = "Creates a single EDIF netlist from the .jedif file.";
        printProgramExecutableString(out);

        // Parse command line options
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new InputFileCommandGroup());
        OutputFileCommandGroup of = new OutputFileCommandGroup();
        of.setDefaultFilename("<input_filename>.edf");
        parser.addCommands(of);
        JSAPResult result = parser.parse(args, err);

        if (!result.success())
            System.exit(1);

        // Parse .jedif file and place into data structure
        EdifEnvironment top = JEdifParserCommandGroup.getEdifEnvironment(result, out);

        // Write out EDIF file
        try {
            String outputFileName = generateEdifNetlist(top, result);
            out.println("Wrote output EDIF file to " + outputFileName);
        } catch (FileNotFoundException e) {
            err.println("Error:" + e);
            System.exit(1);
        }
    }

    /**
     * @param top
     * @param result
     * @return
     * @throws FileNotFoundException
     */
    public static String generateEdifNetlist(EdifEnvironment top, JSAPResult result) throws FileNotFoundException {
        return generateEdifNetlist(top, result, EXECUTABLE_NAME, VERSION_STRING);

    }

    /**
     * @param top
     * @param result
     * @param stuff
     * @param tool
     * @param version
     * @return
     * @throws FileNotFoundException
     */
    public static String generateEdifNetlist(EdifEnvironment top, JSAPResult result, String stuff, String tool,
            String version) throws FileNotFoundException {
        String outputFileName;
        if (!result.userSpecified(OutputFileCommandGroup.OUTPUT_OPTION)) {
            outputFileName = InputFileCommandGroup.getInputFileName(result);
            outputFileName = outputFileName.substring(0, outputFileName.lastIndexOf('.'));
            outputFileName += stuff;
            outputFileName += ".edf";
        } else {
            outputFileName = OutputFileCommandGroup.getOutputFileName(result);
            if (!outputFileName.contains("."))
                outputFileName += ".edf";
        }

        EdifPrintWriter epw = new EdifPrintWriter(new FileOutputStream(outputFileName));
    	top.setVersion(version);
    	top.setProgram(tool);
    	top.setAuthor(DEFAULT_AUTHOR_STRING);
        //top.toEdif(epw, tool, version);
        top.toEdif(epw);
        epw.close();
        return outputFileName;
    }

    /**
     * @param top
     * @param result
     * @param executable_name
     * @param version_string
     * @return
     * @throws FileNotFoundException
     */
    public static String generateEdifNetlist(EdifEnvironment top, JSAPResult result, String executable_name,
            String version_string) throws FileNotFoundException {
        return generateEdifNetlist(top, result, "", EXECUTABLE_NAME, VERSION_STRING);
    }

}
