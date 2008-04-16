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
package edu.byu.ece.edif.jedif;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.util.graph.EdifOutputPortRefGraph;
import edu.byu.ece.edif.util.jsap.EDIFMain;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

public class JEdifFeedback extends EDIFMain {

    public static void main(String args[]) {

        // Define the print streams for this program
        PrintStream out = System.out;
        PrintStream err = System.out;

        // Print executable heading
        EXECUTABLE_NAME = "JEdifFeedback";
        TOOL_SUMMARY_STRING = "Identifies circuit feedback.";
        printProgramExecutableString(out);

        // Parse command line options
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new JEdifParserCommandGroup());
        JSAPResult result = parser.parse(args);
        if (!result.success())
            System.exit(1);

        // Create EdifEnvironment data structure
        EdifEnvironment top = JEdifParserCommandGroup.getEdifEnvironment(result, out);
        EdifCell flatCell = top.getTopCell();

        // Create graph
        EdifOutputPortRefGraph graph = new EdifOutputPortRefGraph(flatCell);

        // Create SCC
        out.print("SCC Decomposition. . .");
        SCCDepthFirstSearch PRGsccDFS = new SCCDepthFirstSearch(graph);
        printSccInfo(PRGsccDFS, out);
        // Done
        out.println("Done");

        // Serialize SCC
        String output_filename = JEdifParserCommandGroup.getInputFileName(result) + ".feedback";
        out.print("Creating file " + output_filename + " . . .");
        FileOutputStream fos = null;
        ObjectOutputStream out_object = null;
        try {
            fos = new FileOutputStream(output_filename);
            out_object = new ObjectOutputStream(fos);
            out_object.writeObject(PRGsccDFS);
            out_object.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        out.println("Done");
    }

    public static void printSccInfo(SCCDepthFirstSearch dfs, PrintStream out) {
        dfs.printSCCs();
    }
}
