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
package edu.byu.ece.edif.test.regression;

import java.io.PrintStream;
import java.util.List;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.jedif.EDIFMain;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.JEdifOutputCommandGroup;
import edu.byu.ece.edif.util.jsap.MergeParserCommandGroup;
import edu.byu.ece.edif.util.jsap.TechnologyCommandGroup;

/**
 * This class is meant as a playground to try out different regression testing
 * code. Techniques that prove successful will be moved to a different class for
 * production code.
 * 
 * @author james
 */
public class BetaRegression extends EDIFMain {

    /**
     * Main method
     * 
     * @param args
     */
    public static void main(String[] args) {
     
    }

    private static EdifEnvironment getTop(String args[]) {
        // Define the print streams for this program
        PrintStream out = System.out;
        PrintStream err = System.out;

        // Print executable heading
        EXECUTABLE_NAME = "BetaRegression";
        TOOL_SUMMARY_STRING = "Extracts the EdifEnvironment object from a .edf file";
        printProgramExecutableString(out);

        // Parse command line options
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new MergeParserCommandGroup());
        parser.addCommands(new JEdifOutputCommandGroup());
        parser.addCommands(new TechnologyCommandGroup());
        JSAPResult result = parser.parse(args, err);
        if (!result.success())
            System.exit(1);

        // Parse EDIF file and generate a EdifEnvironment object
        return MergeParserCommandGroup.getEdifEnvironment(result);
    }

    private static EdifEnvironment topA;

    private static EdifEnvironment topB;

}
