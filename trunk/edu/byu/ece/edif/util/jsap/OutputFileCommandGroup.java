/*
 * Command line options for specifying an output file.
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
package edu.byu.ece.edif.util.jsap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.tools.replicate.PartialReplicationStringDescription;

/**
 * This class is used represent the command line options for specifying an
 * output file.
 * 
 * @author Derrick Gibelyou TODO
 * <ul>
 * <li> Allow a modification of the help for each option
 * <li> Provide mechanism for parsing the extension and automatically inferring
 * a filename from the parsed extension (i.e. allow a default output filename
 * based on the input filename: input.edf -> input.jedif)
 * </ul>
 */
public class OutputFileCommandGroup extends AbstractCommandGroup {
    protected FlaggedOption _output_file;

    public OutputFileCommandGroup() {
        super();

        // Output filename flag
        _output_file = new FlaggedOption(OUTPUT_OPTION);
        _output_file.setStringParser(JSAP.STRING_PARSER);
        _output_file.setRequired(JSAP.NOT_REQUIRED);
        _output_file.addDefault("bltmr");
        _output_file.setShortFlag('o');
        _output_file.setLongFlag(OUTPUT_OPTION);
        _output_file.setUsageName("output_file");
        _output_file.setHelp("Filename and path to the triplicated EDIF output file created by the BL-TMR tool.");
        this.addCommand(_output_file);

    }

    public void setDefaultFilename(String filename) {
        _output_file.setDefault(filename);
    }

    public static String getOutputFileName(JSAPResult result) {
        return result.getString(OUTPUT_OPTION);
    }

    /**
     * Serializes an object
     * 
     * @param out: PrintStream for errors/info
     * @param result: JSAP commandline results
     * @param obj: Object to serialize
     */
    public static void serializeObject(PrintStream out, JSAPResult result, Object obj) {

        String output_filename = getOutputFileName(result);
        if (!output_filename.contains(".")) {
            if (obj instanceof EdifEnvironment) {
                output_filename = output_filename.concat(".jedif");
            } else if (obj instanceof PartialReplicationStringDescription) {
                output_filename = output_filename.concat(".ptmr");
            } else if (obj instanceof SharedIOBAnalysis) {
                output_filename = output_filename.concat(".iob");
            }
        }

        serializeObject(out, output_filename, obj);

    }

    public static void serializeObject(PrintStream out, String filename, Object obj) {
        out.print("Creating file " + filename + " . . .");
        FileOutputStream fos = null;
        ObjectOutputStream out_object = null;
        try {
            fos = new FileOutputStream(filename);
            out_object = new ObjectOutputStream(fos);
            out_object.writeObject(obj);
            out_object.close();
        } catch (IOException ex) {
            out.println();
            ex.printStackTrace();
        }
        out.println("Done");

    }

    public static final String OUTPUT_OPTION = "output";

    public static final char OUTPUT_OPTION_SHORT = 'o';
}