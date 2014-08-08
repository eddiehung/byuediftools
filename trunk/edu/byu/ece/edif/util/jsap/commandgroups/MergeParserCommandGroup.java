/*
 * TODO: Insert class description here.
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

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.util.merge.EdifMergeParser;
import edu.byu.ece.edif.util.parse.ParseException;

public class MergeParserCommandGroup extends JEdifParserCommandGroup {

	public static final MultipleDirectoryFlaggedOption DIRECTORY_FLAG = new MultipleDirectoryFlaggedOption();

	public static final MultipleIncludeFileFlaggedOption INCLUDE_FLAG = new MultipleIncludeFileFlaggedOption();
	
	public static final BlackBoxSwitch BLACK_BOX_SWITCH = new BlackBoxSwitch();
	
	public MergeParserCommandGroup() {
        super();

        // Directory flag
        this.addCommand(DIRECTORY_FLAG);
        // Forced file inclusion flag
        this.addCommand(INCLUDE_FLAG);
        // Black boxes (allow black boxes - ie., don't quit on error if an unmatched black box is found)
        this.addCommand(BLACK_BOX_SWITCH);
        
        // Flatten Flag
        Switch no_flatten_option = new Switch(NO_FLATTEN_FLAG);
        no_flatten_option.setShortFlag(JSAP.NO_SHORTFLAG);
        no_flatten_option.setLongFlag(NO_FLATTEN_FLAG);
        no_flatten_option.setDefault("false");
        no_flatten_option.setHelp("Do not flatten design after merging");
        this.addCommand(no_flatten_option);

        Switch no_open_pins_option = new Switch(NO_OPEN_PINS_SWITCH);
        no_open_pins_option.setShortFlag(JSAP.NO_SHORTFLAG);
        no_open_pins_option.setLongFlag(NO_OPEN_PINS_SWITCH);
        no_open_pins_option.setDefault("false");
        no_open_pins_option.setHelp("Do not allow parser to infer open pins");
        this.addCommand(no_open_pins_option);


    }

    public static EdifEnvironment getEdifEnvironment(JSAPResult result) {
        EdifLibrary primitiveLibrary = XilinxLibrary.library;
        EdifEnvironment top = null;

        //Allows MergeParser to open jedif files.
        if (getInputFileName(result).contains(".jedif")) {
            top = JEdifParserCommandGroup.getEdifEnvironment(result, LogFile.out());
        }
        //parse the edif files if nessisary
        else {
            boolean quitOnError = !result.getBoolean(BlackBoxSwitch.BLACK_BOX_OK_SWITCH);
            boolean allow_open_pins = !result.getBoolean(NO_OPEN_PINS_SWITCH);

            LogFile.out().println("Parsing");
            try {
                Set<String> searchDirs = EdifMergeParser.createDefaultDirs();
                searchDirs.addAll(MultipleDirectoryFlaggedOption.getDirectoryCollection(result));
                top = EdifMergeParser.parseAndMergeEdif(getInputFileName(result), searchDirs,
                		MultipleIncludeFileFlaggedOption.getIncludeFileCollection(result), primitiveLibrary, allow_open_pins, quitOnError);
            } catch (FileNotFoundException e) {
                LogFile.err().println(e.toString());
                System.exit(1);
            } catch (ParseException e) {
                LogFile.err().println("\n" + e);
                System.exit(1);
            }
        }
        return top;
    }

    public static boolean performFlatten(JSAPResult result) {
        return !result.getBoolean(NO_FLATTEN_FLAG);
    }

    public static final String NO_FLATTEN_FLAG = "no_flatten";

    public static final String NO_OPEN_PINS_SWITCH = "no_open_pins";

}

class BlackBoxSwitch extends Switch {

	// Additional command for processing LUT6_2
    public BlackBoxSwitch() {
        super(BLACK_BOX_OK_SWITCH);
        setShortFlag(JSAP.NO_SHORTFLAG);
        setLongFlag(BLACK_BOX_OK_SWITCH);
        setDefault("false");
        setHelp("Allow parser to continue if blackbox definitions are not found");
    }

    public static boolean backBoxSwitch(JSAPResult result) {
    	return result.getBoolean(BLACK_BOX_OK_SWITCH);
    }

    public static final String BLACK_BOX_OK_SWITCH = "blackboxes";
}


/**
 * A flagged option for listing the "include" files for an Edif merging operation.
 */
class MultipleIncludeFileFlaggedOption extends FlaggedOption {

    public MultipleIncludeFileFlaggedOption() {
        super(INCLUDE_FILE_FLAG);
        setStringParser(JSAP.STRING_PARSER);
        setRequired(JSAP.NOT_REQUIRED);
        setShortFlag(INCLUDE_FILE_FLAG_SHORT);
        setLongFlag(INCLUDE_FILE_FLAG);
        setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        setList(JSAP.LIST);
        setListSeparator(',');
        setHelp("Explicitly name edif files to include -- including the path to the file.");
    }

    public static Collection<String> getIncludeFileCollection(JSAPResult result) {
        return Arrays.asList(result.getStringArray(INCLUDE_FILE_FLAG));
    }

    public static final String INCLUDE_FILE_FLAG = "file";
    public static final char INCLUDE_FILE_FLAG_SHORT = 'f';

}

/**
 * A flagged option for listing the directories to search when parsing and
 * merging an edif file.
 */
class MultipleDirectoryFlaggedOption extends FlaggedOption {

    public MultipleDirectoryFlaggedOption() {
        super(DIR_FLAG);
        setStringParser(JSAP.STRING_PARSER);
        setRequired(JSAP.NOT_REQUIRED);
        setShortFlag('d');
        setLongFlag(DIR_FLAG);
        setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        setList(JSAP.LIST);
        setListSeparator(',');
        setHelp("Comma-separated list of directories containing external EDIF files to be included.");
    }

    public static Collection<String> getDirectoryCollection(JSAPResult result) {
        return Arrays.asList(result.getStringArray(DIR_FLAG));
    }

    public static final String DIR_FLAG = "dir";
    public static final char DIR_FLAG_SHORT = 'd';

    public static final String INCLUDE_FILE_FLAG = "file";

}