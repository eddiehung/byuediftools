/*
 * Enable command-line options for specifying the input file and output file.
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

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;

/**
 * This class is used represent the command line options for specifying the
 * input file and output file.
 * <p>
 * TODO
 * <ul>
 * <li> Allow the use/specification of STDIN (input option)
 * <li> Allow the use/specification of STDOUT (output option)
 * <li> Allow a modification of the help for each option
 * <li> Provide mechanism for parsing the extension and automatically inferring
 * a filename from the parsed extension (i.e. allow a default output filename
 * based on the input filename: input.edf -> input.jedif)
 * <li> Provide mechanism for handling stdin and stderr messages
 * <li> Should these classes be made static?
 * </ul>
 */
public class InputFileCommandGroup extends AbstractCommandGroup {

    public InputFileCommandGroup() {
        super();

        // Input filename flag
        _input_file_option = new UnflaggedOption(INPUT_OPTION);
        _input_file_option.setStringParser(JSAP.STRING_PARSER);
        _input_file_option.setRequired(JSAP.REQUIRED);
        //input_file.setShortFlag('i');
        _input_file_option.setUsageName("input_file");
        _input_file_option.setHelp("Input Filename. Required.");
        this.addCommand(_input_file_option);

    }

    public void setInputFlagHelp(String str) {
        _input_file_option.setHelp(str);
    }

    public static String getInputFileName(JSAPResult result) {
        return result.getString(INPUT_OPTION);
    }

    public static final String INPUT_OPTION = "input";

    protected UnflaggedOption _input_file_option;

}