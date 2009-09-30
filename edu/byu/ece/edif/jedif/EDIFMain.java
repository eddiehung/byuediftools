/*
 * Template for "main" routines based on the EDIF infrastructure.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * This class provides a template for "main" routines based on the EDIF
 * infrastructure. TODO:
 * <ul>
 * <li> Include the full class name when printing the command line information
 * </ul>
 */
public class EDIFMain {

    public static String EXECUTABLE_NAME = "edifmain";

    public static String VERSION_STRING = "v0.5.2";

    public static String VERSION_DATE = "30 September 2009";

    public static String COPYRIGHT_STRING = "Copyright (c) 2005-2009 Brigham Young University";

    public static String DEFAULT_AUTHOR_STRING = "BYU Configurable Computing Laboratory";

    public static String TOOL_SUMMARY_STRING = "Generic main function that does not actually do anything";

    public static void printProgramExecutableString(PrintStream out) {
        out.print(EXECUTABLE_NAME + " - " + VERSION_STRING + getSVNversion() + newLine + TOOL_SUMMARY_STRING + newLine
                + COPYRIGHT_STRING + newLine);
    }
    
    public static String getExtendedVersionString() {
    	return VERSION_STRING + getSVNversion();
    }

    public static void main(String args[]) {
        printProgramExecutableString(System.out);
    }

    public static String newLine = System.getProperty("line.separator");

    /**
     * Reads the first line from a file at edu.byu.ece.jarversion and uses that
     * as the version number. If that file does not exist then the local version
     * number will be used, which is very likely to be inaccurate. To create the
     * jar follow these steps:
     * <ol>
     * <li> svnversion > edu/byu/ece/jarversion
     * <li> jar cf ...
     * </ol>
     * <br>
     * Another option is to have the jarversion file in the repository, and add
     * the following steps to creating a jar:
     * <ol>
     * <li> touch jarversion
     * <li> svn commit
     * <li> svn update
     * <li> jar cf ...
     * </ol>
     * 
     * @return Version number as a string
     */
    private static String getSVNversion() {
        String str = "$Revision$";
        str = str.replace("$Revision: ", ".svn");
        str = str.replace(" $", "L");
        //System.out.println (Thread.currentThread ().getContextClassLoader ().getResource ("edu/byu/ece/jarversion"));
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("edu/byu/ece/jarversion");
        try {
            //package edu.byu.ece.edif.util.jsap;
            //BufferedReader in = new BufferedReader(new FileReader(new File(version)));
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            String s = in.readLine();
            if (s != null)
                str = ".svn" + s;
            in.close();
        } catch (IOException e) {
        } catch (NullPointerException e) {
        }

        return str;

    }
}
