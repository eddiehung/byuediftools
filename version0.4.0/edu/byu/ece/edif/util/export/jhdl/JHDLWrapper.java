/*
 * Builds a JHDL Wrapper circuit from an EDIF file with the given technology.
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
package edu.byu.ece.edif.util.export.jhdl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

import byucc.jhdl.apps.Stimulator.Stimulator;
import byucc.jhdl.apps.Viewers.Schematic.SchematicViewerFrame;
import byucc.jhdl.base.Cell;
import byucc.jhdl.base.HWSystem;
import byucc.jhdl.base.PortRecord;
import byucc.jhdl.base.PortRecordList;
import byucc.jhdl.base.Wire;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.util.parse.EdifParser;
import edu.byu.ece.edif.util.parse.ParseException;

/**
 * Builds a JHDL Wrapper circuit from an EDIF file with a passed technology and
 * file name as arguments to main.
 * 
 * @author Welson Sun, Tyler Anderson
 * @version $Id$
 */

public class JHDLWrapper {

    /**
     * This method will check the validity of a given technology string. This
     * method return a false if the technology is not valid and true if it is
     * valid.
     * 
     * @param technology The technology name to check against the valid ones
     * @return True if the passed-in name matches a valid technology
     */
    public static boolean checkTechnologyValidity(String technology) {
        for (int i = 0; i < VALID_TECHNOLOGY.length; i++) {
            if (technology.equalsIgnoreCase(VALID_TECHNOLOGY[i]))
                return true;
        }
        return false;
    }

    /**
     * Returns a String representing the valid/supported technologies.
     * 
     * @return a String representing valid/supported technologies
     */
    public static String listValidTechnologies() {
        StringBuffer technologies = new StringBuffer("Valid Technologies:");
        for (int i = 0; i < VALID_TECHNOLOGY.length; i++) {
            technologies.append(VALID_TECHNOLOGY[i] + " ");
        }
        return technologies.toString();
    }

    /**
     * Returns a JHDL-valid Identifier.
     * 
     * @param id The String to make JHDL-valid
     * @return A JHDL-valid String
     */
    public static String JHDL_ID(String id) {
        return Edi2JHDL.JHDL_ID(id);
    }

    public static Cell buildWrapper(HWSystem parent, EdifCellInstance instance, String technology) {
        return buildWrapper(parent, instance, technology, false);
    }

    /**
     * <ol>
     * <li> Dynamically create a top-level "test-bench" cell
     * <ul>
     * <li> is a "Logic" object
     * <li> Create the cell interface (see BuildWrapper code for figuring out
     * how to decide)
     * <li> Create all the wires owned by this new cell (see BuildWrapper)
     * (connect them to the ports using the connect call).
     * </ul>
     * <li> Call Parser
     * <ul>
     * <li> Create a new Edi2JHDL object with proper technology
     * <li> Generate sub-cell using toJHDLCircuit method of Edi2JHDL
     * <li> You can now call the CLI thing with the new top-level cell
     * </ul>
     * </ol>
     * 
     * @param parent The parent HWSystem for the new cell.
     * @param instance EdifCellInstance of the top EDIF cell to build the JHDL
     * cell off of.
     * @param technology The technology to base the circuit off of.
     * @return The JHDL version of the EDIF Cell.
     */
    public static Cell buildWrapper(HWSystem parent, EdifCellInstance instance, String technology,
            boolean addEdifCellInstanceAsProperty) {

        // Step 0. Check technology
        if (!checkTechnologyValidity(technology)) {
            System.err.println("Bad technology " + technology);
            System.err.println(listValidTechnologies());
            System.exit(1);
        }

        // Step 1. Create a testbench
        SimpleTestBench stb = new SimpleTestBench(parent, "Edif_wrapper");

        Wire portWires[] = createWiresFromPorts(stb, instance, addEdifCellInstanceAsProperty);

        Edi2JHDL jhdl_gen = new Edi2JHDL(technology);
        if (portWires.length == 0)
            System.err.println("\n\nWARNING: Top cells with NO ports are not simulateable in JHDL!\n\n");
        Cell cell = jhdl_gen.toJHDLCircuit(stb, instance, portWires, addEdifCellInstanceAsProperty);

        // Put all the ports into the Stimulator.
        PortRecordList ports = cell.getPortRecords();
        Stimulator _stimulator = new Stimulator(stb);
        // The starting put value for the Stimulator:
        String putValue = "0";
        for (ports.init(); !ports.atEnd(); ports.next()) {
            PortRecord portRecord = ports.getPortRecord();
            if (portRecord.isInPort()) {
                Wire portWire = portRecord.getAttachedWire();
                if (portWire == null) {
                    System.out.println("Error building circuit; " + "unconnected port:\n" + portRecord.toString());
                    System.exit(1);
                }
                // Connect each port to a wire of the stimulator
                // with a putValue.
                _stimulator.addWire(portWire, portRecord.isInOutPort() ? Stimulator.HIGH_IMPEDANCE : putValue);
            }
        }
        return stb;
    }

    /**
     * Takes the EdifPorts, and make wires for the CellInterface.
     * 
     * @param testbench The testbench to add wires to for each top-level port
     * @param edifCell The EdifCell to get the ports from, in order to make the
     * wires
     * @return A Wire Array of top-level Wires Objects for the CellInterface
     */
    public static Wire[] createWiresFromPorts(SimpleTestBench testbench, EdifCellInstance eci,
            boolean addEdifCellInstanceAsProperty) {

        Collection sortedPortsCol = eci.getCellType().getSortedPortList();
        ArrayList sortedPorts = new ArrayList(sortedPortsCol);
        Wire[] portWires = new Wire[sortedPorts.size()];
        int wireIndex = 0;
        for (int i = 0; i < sortedPorts.size(); i++) {
            EdifPort port = (EdifPort) sortedPorts.get(i);
            if (portWires[wireIndex] == null) {
                String portName = JHDL_ID(port.getName());
                int width = port.getWidth();

                Wire stbWire = testbench.wire(width, portName);
                portWires[wireIndex] = stbWire;
            }
            wireIndex++;
        }
        return portWires;
    }

    /**
     * This public array of Strings contains all of the valid technologies
     * supported by the JHDL translator.
     */
    public static final String[] VALID_TECHNOLOGY = { "XC4000", "Virtex", "Virtex2", };

    public static void main(String args[]) {
        if (args.length < 2) {
            System.out.println("<Technology> <EdifEnvironment>");
            System.exit(1);
        }
        String technology = args[0];
        String filename = args[1];

        EdifEnvironment subFile = null;
        try {
            subFile = EdifParser.translate(filename);
        } catch (ParseException e) {
            System.out.println("Parse error!");
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
        EdifDesign edif_design = subFile.getTopDesign();
        EdifCellInstance topCellInstance = edif_design.getTopCellInstance();

        HWSystem hw = new HWSystem();
        Cell c = buildWrapper(hw, topCellInstance, technology);
        new SchematicViewerFrame(c);

    }

}
