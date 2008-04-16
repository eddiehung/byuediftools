/*
 * TODO: Insert class description here.
 *
 *

 * Copyright (c) 2008 Brigham Young University
 *
 * This file is part of the BYU EDIF Tools.
 * 
 * BYU EDIF Tools is free software: you may redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * BYU EDIF Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License is included with the BYU
 * EDIF Tools. It can be found at /edu/byu/edif/doc/gpl2.txt. You may
 * also get a copy of the license at <http://www.gnu.org/licenses/>.
 *
 */
package edu.byu.ece.edif.util.jsap;

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

/**
 * TODO:
 * - For getEdifEnvironment, how do I make sure that the parser has the
 *   options in this group? 
 * - Allow for different primitive libraries
 */
public class MergeParserCommandGroup extends InputFileCommandGroup {

	public MergeParserCommandGroup() {
		super();

		// Directory flag
		FlaggedOption directory_list = new FlaggedOption(DIR_FLAG);
		directory_list.setStringParser(JSAP.STRING_PARSER);
		directory_list.setRequired(JSAP.NOT_REQUIRED);
		directory_list.setShortFlag('d');
		directory_list.setLongFlag(DIR_FLAG);
		directory_list.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
		directory_list.setList(JSAP.LIST);
		directory_list.setListSeparator(',');
		directory_list.setHelp(
						"Comma-separated list of directories containing external EDIF files to be included."
				);
		this.addCommand(directory_list);

		// Forced file inclusion flag
		FlaggedOption file_option = new FlaggedOption(INCLUDE_FILE_FLAG);
		file_option.setStringParser(JSAP.STRING_PARSER);
		file_option.setRequired(JSAP.NOT_REQUIRED);
		file_option.setShortFlag('f');
		file_option.setLongFlag(INCLUDE_FILE_FLAG);
		file_option.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
		file_option.setList(JSAP.LIST);
		file_option.setListSeparator(',');
		file_option.setHelp(
						"Similar to the previous option, but each external EDIF file is named explicitly--including the path to the file.");
		this.addCommand(file_option);
		
		// Flatten Flag
		Switch no_flatten_option = new Switch(NO_FLATTEN_FLAG);
		no_flatten_option.setShortFlag(JSAP.NO_SHORTFLAG);
		no_flatten_option.setLongFlag(NO_FLATTEN_FLAG);
		no_flatten_option.setDefault("false");
		no_flatten_option.setHelp(
						"Do not flatten design after merging");
		this.addCommand(no_flatten_option);
		
		Switch no_open_pins_option = new Switch(NO_OPEN_PINS_SWITCH);
		no_open_pins_option.setShortFlag(JSAP.NO_SHORTFLAG);
		no_open_pins_option.setLongFlag(NO_OPEN_PINS_SWITCH);
		no_open_pins_option.setDefault("false");
		no_open_pins_option.setHelp(
						"Do not allow parser to infer open pins");
		this.addCommand(no_open_pins_option);
		
		Switch blackboxes_ok_option = new Switch(BLACK_BOX_OK_SWITCH);
		blackboxes_ok_option.setShortFlag(JSAP.NO_SHORTFLAG);
		blackboxes_ok_option.setLongFlag(BLACK_BOX_OK_SWITCH);
		blackboxes_ok_option.setDefault("false");
		blackboxes_ok_option.setHelp(
						"Allow parser to continue if blackbox definitions are not found");
		this.addCommand(blackboxes_ok_option);
		
		_replace_srls_option = new Switch(REPLACE_SRLS);
		_replace_srls_option.setShortFlag(JSAP.NO_SHORTFLAG);
		_replace_srls_option.setLongFlag(REPLACE_SRLS);
		_replace_srls_option.setDefault(FALSE);
		_replace_srls_option.setHelp("Replace LUT RAMs acting as SRLs in the design with actual SRLs." +
				" Normally the tool will not replace these LUT RAMs, but if this option is set it will.");
		this.addCommand(_replace_srls_option);
		
		_remove_rlocs_option = new Switch(REMOVE_RLOCS);
		_remove_rlocs_option.setShortFlag(JSAP.NO_SHORTFLAG);
		_remove_rlocs_option.setLongFlag(REMOVE_RLOCS);
		_remove_rlocs_option.setDefault(FALSE);
		_remove_rlocs_option.setHelp("Remove *all* RLOCs in the design.");
		this.addCommand(_remove_rlocs_option);
		
	}
	
	public static Collection<String> getDirectoryCollection(JSAPResult result) {
		return Arrays.asList(result.getStringArray(DIR_FLAG));		
	}

	public static EdifEnvironment getEdifEnvironment(JSAPResult result) {
		EdifLibrary primitiveLibrary = XilinxLibrary.library;
		EdifEnvironment top = null;
		boolean quitOnError = ! result.getBoolean(BLACK_BOX_OK_SWITCH);
		boolean allow_open_pins = ! result.getBoolean(NO_OPEN_PINS_SWITCH);
		
		LogFile.out().println("Parsing");
		try {
			Set searchDirs = EdifMergeParser.createDefaultDirs();
			searchDirs.addAll(getDirectoryCollection(result));
			top = EdifMergeParser.parseAndMergeEdif(getInputFileName(result), 
					searchDirs, getIncludeFileCollection(result),
					primitiveLibrary, allow_open_pins,quitOnError);
		} catch (FileNotFoundException e) {
			LogFile.err().println(e.toString());
			System.exit(1);
		} catch (ParseException e) {
			LogFile.err().println("\n"+e);
			System.exit(1);
		}
		return top;
	}

	public static Collection<String> getIncludeFileCollection(JSAPResult result) {
		return Arrays.asList(result.getStringArray(TMRCommandParser.FILE));		
	}
	
	public static boolean getReplaceSrls(JSAPResult result)
	{
		return result.getBoolean(REPLACE_SRLS);
	}
	
	public static boolean getRemoveRlocs(JSAPResult result)
	{
		return result.getBoolean(REMOVE_RLOCS);
	}

	public static boolean performFlatten(JSAPResult result) {
		return !result.getBoolean(NO_FLATTEN_FLAG);
	}
	
	public static final String BLACK_BOX_OK_SWITCH = "blackboxes";
	public static final String DIR_FLAG = "dir";
	public static final char DIR_FLAG_SHORT = 'd';
	public static final String FALSE = "false";
	public static final String INCLUDE_FILE_FLAG = "file";
	public static final char INCLUDE_FILE_FLAG_SHORT = 'f';
	public static final String NO_FLATTEN_FLAG = "no_flatten";		
	public static final String NO_OPEN_PINS_SWITCH = "no_open_pins";
	public static final String REPLACE_SRLS = "replace_srls";
	public static final String REMOVE_RLOCS = "remove_rlocs";
	
	protected Switch _replace_srls_option;
	protected Switch _remove_rlocs_option;
	
}