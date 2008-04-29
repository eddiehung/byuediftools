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

import java.io.PrintStream;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifUtils;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.flatten.NewFlattenedEdifCell;
import edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.Replace.replaceLutRam;
import edu.byu.ece.edif.util.jsap.EDIFMain;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.JEdifOutputCommandGroup;
import edu.byu.ece.edif.util.jsap.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.MergeParserCommandGroup;
import edu.byu.ece.edif.util.jsap.OutputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.TechnologyCommandGroup;

/**
 * TODO:
 * <ul>
 * <li> Allow a variety of parsing options (as parser matures)
 * <li> Provide options for measuring time
 * <li> Need to clean-up the output of the merge parser (send it through out)
 * <li> Should we cast the Flatten into a EdifCell so the file is smaller?
 * Delete old cell?
 * </ul>
 */
public class JEdifBuild extends EDIFMain {

    public static void main(String args[]) {

        // Define the print streams for this program
        // Print executable heading

        EXECUTABLE_NAME = "JEdifBuild";
        TOOL_SUMMARY_STRING = "Creates merged netlists in a .jedif file format from multiple .edif files";
        printProgramExecutableString(System.out);

        // Parse command line options
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new MergeParserCommandGroup());
        parser.addCommands(new LogFileCommandGroup("BLTmr.log"));
        parser.addCommands(new JEdifOutputCommandGroup());
        parser.addCommands(new TechnologyCommandGroup());
        JSAPResult result = parser.parse(args, System.err);
        if (!result.success())
            System.exit(1);

        LogFileCommandGroup.CreateLog(result);
        PrintStream out = LogFile.out();
        PrintStream err = LogFile.err();

        printProgramExecutableString(LogFile.log());

        // Parse EDIF file and generate a EdifEnvironment object
        EdifEnvironment top = MergeParserCommandGroup.getEdifEnvironment(result);
        try {
            TechnologyCommandGroup.getPartFromEDIF(result, top);
        } catch (IllegalArgumentException ex) {
            //don't care right now, but other tools might later
        }
        // TODO: check to see if top is null

        replace_srls_rlocs(result, out, top);

        // Flatten netlist if necessary
        if (MergeParserCommandGroup.performFlatten(result)) {
            out.print("Flattening . . .");
            NewFlattenedEdifCell flatCell = null;
            EdifCell oldCell = top.getTopCell();
            try {
                flatCell = new NewFlattenedEdifCell(oldCell);
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            }

            // Set flat cell at top (there should be an automated way for this)
            EdifCellInstance flatInstance = null;
            EdifDesign newDesign = null;
            try {
                flatInstance = new EdifCellInstance(flatCell.getName(), null, flatCell);
                newDesign = new EdifDesign(flatCell.getEdifNameable());
            } catch (InvalidEdifNameException e1) {
                e1.toRuntime();
            }
            newDesign.setTopCellInstance(flatInstance);
            // copy design properties
            EdifDesign oldDesign = top.getTopDesign();
            if (oldDesign.getPropertyList() != null) {
                for (Object o : oldDesign.getPropertyList().values()) {
                    Property p = (Property) o;
                    newDesign.addProperty((Property) p.clone());
                }
            }
            top.setTopDesign(newDesign);

            // Delete old cell(s)- done in serialization.

            if (!JEdifOutputCommandGroup.getNoDelete(result)) {
                flatCell.getLibrary().deleteCell(oldCell, true);
            }

            out.println("Done");

            out.println("\tFlattened circuit contains " + EdifUtils.countRecursivePrimitives(flatCell)
                    + " primitives, " + EdifUtils.countRecursiveNets(flatCell) + " nets, and "
                    + EdifUtils.countPortRefs(flatCell, true) + " net connections");

        }

        // Serialize output		
        if (!result.userSpecified(OutputFileCommandGroup.OUTPUT_OPTION)) {
            String name = MergeParserCommandGroup.getInputFileName(result);
            name = name.substring(0, name.lastIndexOf('.'));
            JEdifOutputCommandGroup.serializeObject(out, name + ".jedif", top);
        } else {
            JEdifOutputCommandGroup.serializeObject(out, result, top);
        }

        //		String output_filename = MergeParserCommandGroup.getOutputFileName(result);
        //        out.print("Creating file " + output_filename + " . . .");
        //        FileOutputStream fos = null;
        //        ObjectOutputStream out_object = null;
        //        try {
        //            fos = new FileOutputStream(output_filename);
        //            out_object = new ObjectOutputStream(fos);
        //            out_object.writeObject(top);
        //            out_object.close();
        //        } catch (IOException ex) {
        //            ex.printStackTrace();
        //        }
        //        out.println("Done");

    }

    public static void replace_srls_rlocs(JSAPResult result, PrintStream out, EdifEnvironment myEnv) {
        if (MergeParserCommandGroup.getReplaceSrls(result)) {
            out.println("Replacing SRL LUT RAMs...");
            replaceLutRam rlr = new replaceLutRam(myEnv);
            rlr.replaceLutRams();
            out.println("Finished replacing SRL LUT RAMs.");
        }

        if (MergeParserCommandGroup.getRemoveRlocs(result)) {
            out.println("Removing RLOCs...");
            out.println("Finished removing RLOCs.");
        }
    }

}
