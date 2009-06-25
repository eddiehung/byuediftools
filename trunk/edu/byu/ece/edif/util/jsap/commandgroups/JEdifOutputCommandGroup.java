/*
 * Commandline options to output JEdif files.
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

import java.io.PrintStream;
import java.util.Collection;

import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import edu.byu.ece.edif.core.EdifEnvironment;

/**
 * This class is used output JEdif files. It can optionally prune unused cells
 * and defaults to .jedif if no extension is specified.
 * 
 * @author Derrick Gibelyou
 */
public class JEdifOutputCommandGroup extends OutputFileCommandGroup {

    public JEdifOutputCommandGroup() {
        super();
        _deleteOldCells = new Switch(NO_DELETE_CELLS);
        _deleteOldCells.setLongFlag(NO_DELETE_CELLS);
        _deleteOldCells.setDefault("false");
        _deleteOldCells.setHelp("don't delete old cells from the edif environment");
        this.addCommand(_deleteOldCells);

        _output_file.setDefault("bltmr.jedif");
        // New flags
    }

    public static boolean getNoDelete(JSAPResult result) {
        return result.getBoolean(NO_DELETE_CELLS);
    }

    /**
     * Serializes an object
     * 
     * @param out: PrintStream for errors/info
     * @param result: JSAP commandline results
     * @param obj: Object to serialize
     */
    public static void serializeObject(PrintStream out, JSAPResult result, EdifEnvironment environ) {
       
        String output_filename = getOutputFileName(result);
        if (!output_filename.contains("."))
            output_filename = output_filename.concat(".jedif");

        serializeObject(out, output_filename,  environ);
    }
    
    public static void serializeObjects(PrintStream out, JSAPResult result, Collection objects) {
       
        String output_filename = getOutputFileName(result);
        if (!output_filename.contains("."))
            output_filename = output_filename.concat(".jedif");

        serializeObjects(out, output_filename, objects);
    }

    Switch _deleteOldCells;

    protected static final String NO_DELETE_CELLS = "no_delete_cells";

}
