/*
 * Command line options for specifying a list of input files
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

import java.util.Arrays;
import java.util.Collection;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

/**
 * This class is used represent the command line options for specifying a list
 * of input files.
 * 
 * @author Derrick Gibelyou
 * 
 * TODO
 * <ul>
 * <li> Allow a modification of the help for each option
 * <li> Provide mechanism for parsing the extension and automatically inferring
 * a filename from the parsed extension (i.e. allow a default output filename
 * based on the input filename: input.edf -> input.jedif)
 * <li> Provide mechanism for handling stdin and stderr messages
 * </ul>
 */
public class MultipleInputFileCommandGroup extends AbstractCommandGroup {

    public MultipleInputFileCommandGroup() {
        super();

        // Input filename flag
        _input_files_option = new FlaggedOption(INPUTS_OPTION);
        _input_files_option.setStringParser(JSAP.STRING_PARSER);
        _input_files_option.setRequired(JSAP.REQUIRED);
        _input_files_option.setLongFlag(INPUTS_OPTION);
        _input_files_option.setAllowMultipleDeclarations(true);
        _input_files_option.setList(true);
        _input_files_option.setListSeparator(',');
        _input_files_option.setUsageName("input_file");
        _input_files_option.setHelp("Input Filename. Required.");
        this.addCommand(_input_files_option);

    }

    public void setInputFlagHelp(String str) {
        _input_files_option.setHelp(str);
    }

    public static Collection<String> getInputFileNames(JSAPResult result) {
        return (Arrays.asList(result.getStringArray(INPUTS_OPTION)));
    }

    public static final String INPUTS_OPTION = "input";

    protected FlaggedOption _input_files_option;

}