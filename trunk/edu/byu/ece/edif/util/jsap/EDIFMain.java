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

import java.io.PrintStream;

/**
 * This class provides a template for "main" routines based on the EDIF
 * infrastructure.
 *
 * TODO:
 * - Include the full class name when printing the command line information
 */
public class EDIFMain {

	public static String EXECUTABLE_NAME = "edifmain";
	
	public static String VERSION_STRING = "v0.3.4rc1.$Revision$";
	
	public static String VERSION_DATE = "17 Apr 2008";
	
	public static String COPYRIGHT_STRING = "Copyright (c) 2005-2007 Brigham Young University, All rights reserved.";

	public static String TOOL_SUMMARY_STRING = "Generic main function that does not actually do anything";
	
	public static void printProgramExecutableString(PrintStream out) {
		out.print(EXECUTABLE_NAME + " - " + VERSION_STRING + newLine
		+ TOOL_SUMMARY_STRING + newLine
		+ COPYRIGHT_STRING + newLine);
	}
		
	public static void main(String args[]) {		
		printProgramExecutableString(System.out);
	}

	public static String newLine = System.getProperty("line.separator");
	
}
