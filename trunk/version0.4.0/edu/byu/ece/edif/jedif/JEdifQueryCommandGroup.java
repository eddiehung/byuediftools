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

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

public class JEdifQueryCommandGroup extends JEdifParserCommandGroup {

    public JEdifQueryCommandGroup() {
        super();

        // Libraries option
        Switch lib_list = new Switch(LIBRARIES);
        lib_list.setShortFlag(JSAP.NO_SHORTFLAG);
        lib_list.setLongFlag(LIBRARIES_FLAG);
        lib_list.setDefault("false");
        lib_list.setHelp("Print a list of all libraries contained in the design.");
        this.addCommand(lib_list);

        // List Ports option
        FlaggedOption ports_list = new FlaggedOption(PORTS);
        ports_list.setStringParser(JSAP.STRING_PARSER);
        ports_list.setRequired(JSAP.NOT_REQUIRED);
        ports_list.setShortFlag(JSAP.NO_SHORTFLAG);
        ports_list.setLongFlag(PORTS);
        ports_list.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        ports_list.setList(JSAP.LIST);
        ports_list.setListSeparator(',');
        ports_list.setUsageName(PORTS_USAGE);
        ports_list.setHelp("Comma-separated list of cell types. All ports of specified types will be printed.");
        this.addCommand(ports_list);

        // Instance List option
        FlaggedOption instance_list = new FlaggedOption(INST_LIST);
        instance_list.setStringParser(JSAP.STRING_PARSER);
        instance_list.setRequired(JSAP.NOT_REQUIRED);
        instance_list.setShortFlag(JSAP.NO_SHORTFLAG);
        instance_list.setLongFlag(INST_LIST);
        instance_list.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        instance_list.setList(JSAP.LIST);
        instance_list.setListSeparator(',');
        instance_list.setUsageName(INST_LIST_USAGE);
        instance_list.setHelp("Comma-separated list of cell types. All instances of specified types will be printed.");
        this.addCommand(instance_list);

        // Cell Library List option
        FlaggedOption lib_cells = new FlaggedOption(LIB_CELLS);
        lib_cells.setStringParser(JSAP.STRING_PARSER);
        lib_cells.setRequired(JSAP.NOT_REQUIRED);
        lib_cells.setShortFlag(JSAP.NO_SHORTFLAG);
        lib_cells.setLongFlag(LIB_CELLS);
        lib_cells.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        lib_cells.setList(JSAP.LIST);
        lib_cells.setListSeparator(',');
        lib_cells.setUsageName(LIB_CELLS_USAGE);
        lib_cells
                .setHelp("Comma-separated list of libraries. Names of all cells contained in the library will be printed.");
        this.addCommand(lib_cells);

        // Subcells List option
        FlaggedOption subcells = new FlaggedOption(SUBCELLS_LIST);
        subcells.setStringParser(JSAP.STRING_PARSER);
        subcells.setRequired(JSAP.NOT_REQUIRED);
        subcells.setShortFlag(JSAP.NO_SHORTFLAG);
        subcells.setLongFlag(SUBCELLS_LIST);
        subcells.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        subcells.setList(JSAP.LIST);
        subcells.setListSeparator(',');
        subcells.setUsageName(SUBCELLS_LIST_USAGE);
        subcells.setHelp("Comma-separated list of cells. Names of all instances within the cell will be printed.");
        this.addCommand(subcells);

        // Cell Nets List option
        FlaggedOption cell_nets = new FlaggedOption(CELL_NETS);
        cell_nets.setStringParser(JSAP.STRING_PARSER);
        cell_nets.setRequired(JSAP.NOT_REQUIRED);
        cell_nets.setShortFlag(JSAP.NO_SHORTFLAG);
        cell_nets.setLongFlag(CELL_NETS);
        cell_nets.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        cell_nets.setList(JSAP.LIST);
        cell_nets.setListSeparator(',');
        cell_nets.setUsageName(CELL_NETS_USAGE);
        cell_nets.setHelp("Comma-separated list of cells. Names of all nets within the cell will be printed.");
        this.addCommand(cell_nets);

        // Primitives List option
        FlaggedOption prim_list = new FlaggedOption(PRIM_LIST);
        prim_list.setStringParser(JSAP.STRING_PARSER);
        prim_list.setRequired(JSAP.NOT_REQUIRED);
        prim_list.setShortFlag(JSAP.NO_SHORTFLAG);
        prim_list.setLongFlag(PRIM_LIST);
        prim_list.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        prim_list.setList(JSAP.LIST);
        prim_list.setListSeparator(',');
        prim_list.setUsageName(PRIM_LIST_USAGE);
        prim_list
                .setHelp("Comma-separated list of cells. Names of all primitives within the cell will be printed (not including "
                        + "primitives within subcells of the specified cell).");
        this.addCommand(prim_list);

        // Print Dangling Nets option
        Switch dangling_nets = new Switch(PRINT_DANGLING_NETS);
        dangling_nets.setShortFlag(JSAP.NO_SHORTFLAG);
        dangling_nets.setLongFlag(PRINT_DANGLING_NETS);
        dangling_nets.setDefault("false");
        dangling_nets.setHelp("Print a list of all dangling nets contained in the design.");
        this.addCommand(dangling_nets);

        // Count Persistent FFs option
        Switch persistentFFs = new Switch(COUNT_PERSISTENT_FFS);
        persistentFFs.setShortFlag(JSAP.NO_SHORTFLAG);
        persistentFFs.setLongFlag(COUNT_PERSISTENT_FFS);
        persistentFFs.setDefault("false");
        persistentFFs.setHelp("Print a list of all Xilinx flip-flops contained in persistent sections of the design."
                + "The JEdif file being queried must be flattened for this option to be valid.");
        this.addCommand(persistentFFs);

    }

    public static boolean listLibs(JSAPResult result) {
        return result.getBoolean(LIBRARIES);
    }

    public static String[] getLibList(JSAPResult result) {
        return result.getStringArray(LIB_CELLS);
    }

    public static String[] getInstanceList(JSAPResult result) {
        return result.getStringArray(INST_LIST);
    }

    public static String[] getCellsList(JSAPResult result) {
        return result.getStringArray(SUBCELLS_LIST);
    }

    public static String[] getCellNetsList(JSAPResult result) {
        return result.getStringArray(CELL_NETS);
    }

    public static boolean listPorts(JSAPResult result) {
        return result.contains(PORTS);
    }

    public static String[] getPortsList(JSAPResult result) {
        return result.getStringArray(PORTS);
    }

    public static boolean instanceList(JSAPResult result) {
        return result.contains(INST_LIST);
    }

    public static boolean printPrimitives(JSAPResult result) {
        return result.contains(PRIM_LIST);
    }

    public static String[] getPrimitivesList(JSAPResult result) {
        return result.getStringArray(PRIM_LIST);
    }

    public static boolean libraryCells(JSAPResult result) {
        return result.contains(LIB_CELLS);
    }

    public static boolean cellNets(JSAPResult result) {
        return result.contains(CELL_NETS);
    }

    public static boolean printDanglingNets(JSAPResult result) {
        return result.getBoolean(PRINT_DANGLING_NETS);
    }

    public static boolean subcells(JSAPResult result) {
        return result.contains(SUBCELLS_LIST);
    }

    public static boolean countPersistentFFs(JSAPResult result) {
        return result.contains(COUNT_PERSISTENT_FFS);
    }

    public static final String LIBRARIES = "libraries";

    public static final String LIBRARIES_FLAG = "libraries";

    public static final String PORTS = "ports";

    public static final String PORTS_USAGE = "cell";

    public static final String PRIM_LIST = "primitive_list";

    public static final String PRIM_LIST_USAGE = "cell";

    public static final String INST_LIST = "instance_list";

    public static final String INST_LIST_USAGE = "cell_type";

    public static final String LIB_CELLS = "cells_in_library";

    public static final String LIB_CELLS_USAGE = "library";

    public static final String CELL_NETS = "cell_nets";

    public static final String CELL_NETS_USAGE = "cell";

    public static final String PRINT_DANGLING_NETS = "dangling_nets";

    public static final String SUBCELLS_LIST = "subcells";

    public static final String SUBCELLS_LIST_USAGE = "cell";

    public static final String COUNT_PERSISTENT_FFS = "count_persistent_FFs";

}