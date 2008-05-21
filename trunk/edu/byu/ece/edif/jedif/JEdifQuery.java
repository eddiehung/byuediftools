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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.tools.EdifTools;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.util.jsap.EDIFMain;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;

/**
 * TODO: provide a large set of options for querying and describing information
 * about the internal structure of the netlist.
 */
public class JEdifQuery extends EDIFMain {

    public static void main(String args[]) {

        // Define the print streams for this program
        PrintStream out = System.out;
        PrintStream err = System.out;

        // Print executable heading
        EXECUTABLE_NAME = "JEdifQuery";
        TOOL_SUMMARY_STRING = "Queries the contents of a .jedif file and provides summary information.";
        printProgramExecutableString(out);

        // Parse command line options
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new JEdifQueryCommandGroup());
        JSAPResult result = parser.parse(args, err);
        if (!result.success())
            System.exit(1);

        //TODO: add option to print to an output file like in JEdifClockDomain
        EdifEnvironment top = JEdifQueryCommandGroup.getEdifEnvironment(result, out);
        if (top == null)
            err.println("Invalid .jedif file");

        // default output - top cell, top-level ports, number of nets, and number of instances
        printEdifStats(top, out);
        printPorts(top, out);
        if (JEdifQueryCommandGroup.listLibs(result))
            printLibraries(top, out);
        if (JEdifQueryCommandGroup.listPorts(result)) {
            List<String> cellList = Arrays.asList(JEdifQueryCommandGroup.getPortsList(result));
            printCellPorts(top, cellList, out);
        }
        if (JEdifQueryCommandGroup.instanceList(result)) {
            List<String> instList = Arrays.asList(JEdifQueryCommandGroup.getInstanceList(result));
            printInstanceLists(top, instList, out);
        }
        if (JEdifQueryCommandGroup.libraryCells(result)) {
            List<String> libList = Arrays.asList(JEdifQueryCommandGroup.getLibList(result));
            printLibraryCells(top, libList, out);
        }
        if (JEdifQueryCommandGroup.subcells(result)) {
            List<String> cellList = Arrays.asList(JEdifQueryCommandGroup.getCellsList(result));
            printSubcells(top, cellList, out);
        }
        if (JEdifQueryCommandGroup.cellNets(result)) {
            List<String> cellList = Arrays.asList(JEdifQueryCommandGroup.getCellNetsList(result));
            printCellNets(top, cellList, out);
        }
        if (JEdifQueryCommandGroup.printPrimitives(result)) {
            List<String> cellList = Arrays.asList(JEdifQueryCommandGroup.getPrimitivesList(result));
            printPrimitives(top, cellList, out);
        }
        if (JEdifQueryCommandGroup.printDanglingNets(result))
            printDanglingNets(top, out);
        if (JEdifQueryCommandGroup.countPersistentFFs(result))
            countPersistentFFs(top, out);
        out.println();
    }

    /**
     * Prints out the default EDIF Statistics: top cell name, number of nets,
     * and number of instances.
     * 
     * @param env The EDIFEnvironment read in by the .jedif file
     * @param out The PrintStream to which output will be printed
     */
    public static void printEdifStats(EdifEnvironment env, PrintStream out) {
        out.println();
        out.println(BAR + "\nEDIF Statistics\n" + BAR);
        EdifCell cell = env.getTopCell();
        out.println("Top Cell = " + cell.getName());
        out.println(cell.getNetList().size() + " nets");
        out.println(cell.getSubCellList().size() + " instances");
    }

    /**
     * Prints out a list of all EdifLibraries contained in the EDIFEnvironment
     * read from the .jedif file
     * 
     * @param env The EDIF environment read in by the JEdif file
     * @param out The PrintStream to which output will be printed
     */
    public static void printLibraries(EdifEnvironment env, PrintStream out) {
        out.println();
        out.println(BAR + "\nLibraries\n" + BAR);
        List<EdifLibrary> libList = env.getLibraryManager().getLibraries();
        for (EdifLibrary lib : libList)
            out.println(lib);
    }

    /**
     * Prints out a list of input and output ports in the top-level cell.
     * 
     * @param env The EDIF environment read in by the JEdif file
     * @param out The PrintStream to which output will be printed
     */
    public static void printPorts(EdifEnvironment env, PrintStream out) {
        EdifCell topCell = env.getTopCell();
        Collection<EdifPort> inports = topCell.getInputPorts();
        Collection<EdifPort> outports = topCell.getOutputPorts();
        out.println("\nPorts of top-level cell (" + topCell.getName() + ")");
        out.println("Input (" + inports.size() + "):");
        for (EdifPort port : inports)
            out.println("\t" + port.getName());
        out.println("Output (" + outports.size() + "):");
        for (EdifPort port : outports)
            out.println("\t" + port.getName());
    }

    /**
     * Prints a list of all instances of user-specified cell types.
     * 
     * @param env The EDIF environment read in by the JEdif file
     * @param instList The list of cell types provided by the user
     * @param out The PrintStream to which output will be printed
     */
    public static void printInstanceLists(EdifEnvironment env, List<String> instList, PrintStream out) {
        EdifCell top = env.getTopCell();
        EdifLibraryManager libman = env.getLibraryManager();
        out.println();
        out.println(BAR + "\nInstances Listed by Cell Type\n" + BAR);
        for (String cellType : instList) {
            EdifCell cell = libman.getCell(cellType);
            Collection<EdifCellInstance> instances = top.findCellInstancesOf(cell);
            if (instances == null)
                out.println("No cell instance found matching cell type " + cellType + ".");
            else {
                out.println(instances.size() + " instances found matching cell type " + cellType + ":");
                for (EdifCellInstance inst : instances)
                    out.println("\t" + inst.getName());
            }
        }
    }

    /**
     * Prints a list of all cells contained in EDIF libraries provided by the
     * user.
     * 
     * @param env The EDIF environment read in by the JEdif file
     * @param libList The list of libraries provided by the user
     * @param out The PrintStream to which output will be printed
     */
    public static void printLibraryCells(EdifEnvironment env, List<String> libList, PrintStream out) {
        EdifLibraryManager libman = env.getLibraryManager();
        out.println();
        out.println(BAR + "\nCells Listed by Library\n" + BAR);
        for (String libName : libList) {
            EdifLibrary lib = libman.getLibrary(libName);
            if (lib == null)
                out.println("No library found matching name " + libName + ".");
            else {
                Collection<EdifCell> cells = lib.getCells();
                out.println("Cells in library " + lib.getName() + ":");
                for (EdifCell cell : cells)
                    out.println("\t" + cell.getName());
            }
        }
    }

    /**
     * Prints a list of all nets contained in user-specified cells.
     * 
     * @param env The EDIF environment read in by the JEdif file
     * @param cellList The list of cells provided by the user
     * @param out The PrintStream to which output will be printed
     */
    public static void printCellNets(EdifEnvironment env, List<String> cellList, PrintStream out) {
        EdifLibraryManager libman = env.getLibraryManager();
        List<EdifLibrary> libList = libman.getLibraries();
        out.println();
        out.println(BAR + "\nNets Contained in Specified Cells\n" + BAR);
        for (String cellName : cellList) {
            EdifCell cell = null;
            for (EdifLibrary lib : libList) {
                cell = lib.getCell(cellName);
                if (cell != null)
                    break;
            }
            if (cell == null)
                out.println("No cell found matching name " + cellName);
            else {
                Collection<EdifNet> netList = cell.getNetList();
                out.println("Nets in Cell " + cellName);
                if (netList == null)
                    out.println("\tNone");
                else {
                    for (EdifNet net : netList)
                        out.println("\t" + net.getName());
                }
            }
        }
    }

    /**
     * Prints a list of all ports contained in user-specified cell types.
     * 
     * @param env The EDIF environment read in by the JEdif file
     * @param cellList The list of cell types provided by the user
     * @param out The PrintStream to which output will be printed
     */
    public static void printCellPorts(EdifEnvironment env, List<String> cellList, PrintStream out) {
        EdifLibraryManager libman = env.getLibraryManager();
        List<EdifLibrary> libList = libman.getLibraries();
        out.println();
        out.println(BAR + "\nPorts Contained in Specified Cells\n" + BAR);
        for (String cellName : cellList) {
            EdifCell cell = null;
            for (EdifLibrary lib : libList) {
                cell = lib.getCell(cellName);
                if (cell != null)
                    break;
            }
            if (cell == null)
                out.println("No cell found matching name " + cellName);
            else {
                Collection<EdifPort> inports = cell.getInputPorts();
                Collection<EdifPort> outports = cell.getOutputPorts();
                out.println("Ports in Cell " + cellName);
                out.println("Input (" + inports.size() + "):");
                for (EdifPort port : inports)
                    out.println("\t" + port.getName());
                out.println("Output (" + outports.size() + "):");
                for (EdifPort port : outports)
                    out.println("\t" + port.getName());
            }
        }
    }

    /**
     * Prints a list of all dangling nets contained in the design.
     * 
     * @param env The EDIF environment read in by the JEdif file
     * @param out The PrintStream to which output will be printed
     */
    public static void printDanglingNets(EdifEnvironment env, PrintStream out) {
        boolean any = false;
        EdifLibraryManager libman = env.getLibraryManager();
        out.println();
        out.println(BAR + "\nDangling Nets in Design\n" + BAR);
        Collection<EdifLibrary> libs = libman.getLibraries();
        for (EdifLibrary lib : libs) {
            Collection<EdifCell> cellList = lib.getCells();
            for (EdifCell cell : cellList) {
                Collection<EdifNet> dangling = cell.getDanglingNets();
                if (dangling != null) {
                    any = true;
                    for (EdifNet net : dangling) {
                        out.println(net.getName() + " in cell " + cell.getName() + " in library " + lib.getName());

                    }
                }
            }
        }
        if (!any)
            out.println("None");
    }

    /**
     * Prints a list of all instances contained in user-specified cell types.
     * 
     * @param env The EDIF environment read in by the JEdif file
     * @param instList The list of cell types provided by the user
     * @param out The PrintStream to which output will be printed
     */
    public static void printSubcells(EdifEnvironment env, List<String> cellList, PrintStream out) {
        EdifLibraryManager libman = env.getLibraryManager();
        List<EdifLibrary> libList = libman.getLibraries();
        out.println();
        out.println(BAR + "\nSubcells of Specified Cells\n" + BAR);
        for (String cellName : cellList) {
            EdifCell cell = null;
            for (EdifLibrary lib : libList) {
                cell = lib.getCell(cellName);
                if (cell != null)
                    break;
            }
            if (cell == null)
                out.println("No cell found matching name " + cellName);
            else {
                Collection<EdifCellInstance> instList = cell.getSubCellList();
                out.println("Subcells in Cell " + cellName);
                if (instList == null || instList.size() == 0)
                    out.println("\tNone");
                else {
                    for (EdifCellInstance inst : instList)
                        out.println("\t" + inst.getName() + " (" + inst.getCellType() + ")");
                }
            }
        }
    }

    /**
     * Prints a list of all primitive cells contained in user-specified cell
     * types.
     * 
     * @param env The EDIF environment read in by the JEdif file
     * @param instList The list of cell types provided by the user
     * @param out The PrintStream to which output will be printed
     */
    public static void printPrimitives(EdifEnvironment env, List<String> cellList, PrintStream out) {
        EdifLibraryManager libman = env.getLibraryManager();
        List<EdifLibrary> libList = libman.getLibraries();
        out.println();
        out.println(BAR + "\nPrimitives within Specified Cells\n" + BAR);
        for (String cellName : cellList) {
            EdifCell cell = null;
            for (EdifLibrary lib : libList) {
                cell = lib.getCell(cellName);
                if (cell != null)
                    break;
            }
            if (cell == null)
                out.println("No cell found matching name " + cellName);
            else if (cell.isPrimitive())
                out.println("Cell " + cellName + " is a primitive.");
            else {
                Collection<EdifCellInstance> instList = cell.getSubCellList();
                out.println("Primitives Within Cell " + cellName);
                if (instList == null || instList.size() == 0)
                    out.println("\tNone");
                else {
                    for (EdifCellInstance inst : instList) {
                        if (inst.getCellType().isPrimitive())
                            out.println("\t" + inst.getName() + " (" + inst.getCellType() + ")");
                    }
                }
            }
        }
    }

    /**
     * Counts the number of persistent Xilinx flip-flops in the design. The
     * design must be flattened in order for this option to be valid.
     * 
     * @param env The EDIF environment read in by the JEdif file
     * @param out The PrintStream to which output will be printed
     */
    private static void countPersistentFFs(EdifEnvironment env, PrintStream out) {
        out.println();
        out.println(BAR + "\nPersistent Flip-Flops\n" + BAR);
        EdifCell cell = env.getTopCell();
        if (cell instanceof FlattenedEdifCell) {
            int persFF = EdifTools.countXilinxPersistentFlipFlops((FlattenedEdifCell) cell);
            out.println("Design contains " + persFF + " flip-flops in persistent sections.");
        } else {
            out.println(cell.getName() + " has not been flattened.");
        }
    }

    protected static final String BAR = "-----------------------------------";

}
