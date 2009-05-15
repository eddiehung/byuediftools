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
package edu.byu.ece.edif.tools.sterilize.halflatch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
import edu.byu.ece.edif.core.BasicEdifBusNetNamingPolicy;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.core.StringTypedValue;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCellInstance;
import edu.byu.ece.edif.util.iob.IOBAnalyzer;

/**
 * @since Created on Oct 29, 2005
 */
public class XilinxHalfLatchArchitecture implements HalfLatchArchitecture, Serializable {

    public XilinxHalfLatchArchitecture() {
        this(null);
    }

    public XilinxHalfLatchArchitecture(IOBAnalyzer iobAnalyzer) {
        _problemPrimitiveMap = new XilinxProblemPrimitiveMap();
        if (iobAnalyzer != null)
            ((XilinxProblemPrimitiveMap) _problemPrimitiveMap).addIOBRegisters(iobAnalyzer.getAllIOBRegisters());
        initializeBadCutPins();
    }

    private void initializeBadCutPins() {
        _badCutPins.add("CASCADEINA");
        _badCutPins.add("CASCADEINB");
        _badCutPins.add("pcin");
        _badCutPins.add("bcin");
    }

    public boolean isBadCutPin(EdifPort port) {
        String str = port.getEdifCell().getName();

        if (str.equals("RAMB16")) {
            if (port.getName().contains("CASCADEIN"))
                return true;
        }
        if (str.contains("DSP48")) {
            if (port.getName().equals("pcin") || port.getName().equals("bcin"))
                return true;
        }

        return false;
        //return _badCutPins.contains(port.getName());
    }

    /*
     * Adds a 'safe constant' cell to the parameter cell, (the 'safe constant'
     * cell will not be turned into a half-latch)
     */
    public EdifCellInstance addConstantCellInstance(EdifCell cell, int safeConstantPolarity) {

        // To add a cell, we first need the library manager this cell belongs to
        EdifLibrary el = cell.getLibrary();
        if (el == null)
            throw new EdifRuntimeException(("Cell " + cell + " does not belong to a library."));
        EdifLibraryManager elm = el.getLibraryManager();
        if (elm == null)
            throw new EdifRuntimeException(("Library " + el + " for cell " + cell + " does not belong to a library manager."));

        // Create a new constant cell
        EdifCell rom16x1 = XilinxLibrary.findOrAddXilinxPrimitive(elm, "ROM16X1");

        // Create an instance of the new constant cell
        String safeConstantCellName = null;
        if (safeConstantPolarity == 0)
            safeConstantCellName = "safeConstantCell_zero";
        else
            safeConstantCellName = "safeConstantCell_one";
        EdifCellInstance safeConstantCell = null;
        try {
            safeConstantCell = new FlattenedEdifCellInstance(safeConstantCellName, cell, rom16x1, null);
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }

        /*
         * We are going to use a LUT (ROM16X1) as the driver cell, so to get a
         * constant value we initialize the cell with either all 0's or all 1's.
         */
        StringTypedValue init = null;
        if (safeConstantPolarity == 0)
            init = new StringTypedValue("0000");
        else
            init = new StringTypedValue("1111");
        safeConstantCell.addProperty(new Property("INIT", init));

        // Create ground driver for address lines of the 16x1 ROM.
        EdifCell gnd = XilinxLibrary.findOrAddXilinxPrimitive(elm, "GND");
        EdifCellInstance const_addr_gnd = null;
        try {
            const_addr_gnd = new FlattenedEdifCellInstance("const_addr_gnd", cell, gnd, null);
            cell.addSubCell(const_addr_gnd);
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }

        // attach nets to address lines of LUT
        EdifNet const_addr = null;
        try {
            const_addr = new EdifNet("const_addr");
            cell.addNet(const_addr);
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }

        // Get the output port (there should only be one)
        ArrayList gnd_outs = new ArrayList(gnd.getOutputPorts());
        if (gnd_outs.size() != 1)
            throw new EdifRuntimeException("Bad ground:" + gnd.getInterface());
        // Get the first (and only) element and add a connection
        EdifPort gnd_out = (EdifPort) gnd_outs.get(0);
        const_addr.addPortConnection(const_addr_gnd, BasicEdifBusNetNamingPolicy
                .getBusBaseNameStatic(gnd_out.getName()));

        // Attach zero constant to address lines of LUT
        const_addr.addPortConnection(safeConstantCell, "A0");
        const_addr.addPortConnection(safeConstantCell, "A1");
        const_addr.addPortConnection(safeConstantCell, "A2");
        const_addr.addPortConnection(safeConstantCell, "A3");

        /*
         * Now that we have the constant cell initialized, we will actually add
         * the instance to the passed in cell
         */
        try {
            cell.addSubCell(safeConstantCell);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }

        return safeConstantCell;
    }

    public String getSafeConstantCellOutputPortName() {
        return "O";
    }

    public boolean cellRequiresReplacement(EdifCellInstance edifCellInstance) {
        if (_problemPrimitiveMap.getPrimitiveReplacementType(edifCellInstance) != null)
            return true;
        else
            return false;
    }

    public boolean isConstantCell(String edifCellInstanceType) {
        if (getConstantCellArrayIndex(edifCellInstanceType) != -1)
            return true;
        else
            return false;
    }

    public int getConstantCellValue(String edifCellInstanceType) {
        if (isConstantCell(edifCellInstanceType) == false)
            throw new EdifRuntimeException(
                    "Runtime Error: XilinxHalfLatchArchitecture: Cannot return constant value for non-constant cell type "
                            + edifCellInstanceType + ".");
        int constantCellArrayIndex = getConstantCellArrayIndex(edifCellInstanceType);
        if (constantCellArrayIndex < 0 || constantCellArrayIndex >= _constantPrimitiveValues.length)
            throw new EdifRuntimeException(
                    "Runtime Error: XilinxHalfLatchArchitecture: Cannot find constant value for cell type "
                            + edifCellInstanceType + ".");
        return _constantPrimitiveValues[constantCellArrayIndex];
    }

    private int getConstantCellArrayIndex(String edifCellInstanceType) {
        for (int i = 0; i < _constantPrimitives.length; i++) {
            if (_constantPrimitives[i].compareToIgnoreCase(edifCellInstanceType) == 0)
                return i;
        }
        return -1;
    }

    public String getPrimitiveReplacementType(EdifCellInstance edifCellInstance) {
        return _problemPrimitiveMap.getPrimitiveReplacementType(edifCellInstance);
    }

    public String[] getPrimitiveReplacementFloatingPorts(EdifCellInstance edifCellInstance) {
        return _problemPrimitiveMap.getPrimitiveReplacementFloatingPorts(edifCellInstance);
    }

    public int getPrimitiveReplacementFloatingPortDefaultValue(EdifCellInstance edifCellInstance, String floatingPort) {
        return _problemPrimitiveMap.getPrimitiveReplacementFloatingPortDefaultValue(edifCellInstance, floatingPort);
    }

    public EdifCell findOrAddPrimitiveReplacementCell(EdifLibraryManager elm, String safePrimitiveType) {
        return XilinxLibrary.findOrAddXilinxPrimitive(elm, safePrimitiveType);
    }

    public EdifCell findOrAddPrimitiveInverterCell(EdifLibraryManager elm) {
        if (_primitiveInverterCell == null) {
            _primitiveInverterCell = XilinxLibrary.findOrAddXilinxPrimitive(elm, "INV");
        }
        return _primitiveInverterCell;
    }

    public String getPrimitiveInverterCellInputPortName() {
        return "I";
    }

    public String getPrimitiveInverterCellOutputPortName() {
        return "O";
    }

    public EdifCell findOrAddPrimitiveInputBufferCell(EdifLibraryManager elm) {
        if (_primitiveInputBufferCell == null) {
            _primitiveInputBufferCell = XilinxLibrary.findOrAddXilinxPrimitive(elm, "IBUF");
        }
        return _primitiveInputBufferCell;
    }

    public String getPrimitiveInputBufferCellInputPortName() {
        return "I";
    }

    public String getPrimitiveInputBufferOutputBufferName() {
        return "O";
    }

    public void tagIOBRegisters(Collection iobRegisters) {
        ((XilinxProblemPrimitiveMap) _problemPrimitiveMap).addIOBRegisters(iobRegisters);
    }

    private EdifCell _primitiveInverterCell = null;

    private EdifCell _primitiveInputBufferCell = null;

    // *** This set of arrays must stay in this order because
    //     the class depends on the ordering to match!!!
    private String[] _constantPrimitives = { "gnd", "vcc" };

    private int[] _constantPrimitiveValues = { 0, 1 };

    private ProblemPrimitiveMap _problemPrimitiveMap;

    private Set<String> _badCutPins = new LinkedHashSet<String>();

    public static int test() {

        //		XilinxHalfLatchArchitecture hlArchitecture = new XilinxHalfLatchArchitecture();
        //        String error_report = "";
        //
        //        // Misc. variables
        //
        //        EdifEnvironment ee = null;
        //        EdifLibrary el = null;
        //        EdifCell cell = null;
        //        try {
        //            ee = new EdifEnvironment("environment");
        //            el = new EdifLibrary(ee.getLibraryManager(), "library");
        //            cell = new EdifCell(el, "test_cell_1");
        //        } catch (InvalidEdifNameException e) {
        //            e.toRuntime();
        //        } catch (EdifNameConflictException e) {
        //            e.toRuntime();
        //        }
        //
        //        int safeConstantPolarity = 1;
        //        String prim = "fds";
        //        // Test 1: Add a constant generating cell instance to 'cell'
        //        EdifCellInstance constantCellInstance = hlArchitecture.addConstantCellInstance(cell, safeConstantPolarity);
        //        boolean instanceFoundInCell = false;
        //        EdifCellInstance foundConstantCell = null;
        //        for (Iterator i = cell.getSortedSubCellList().iterator(); i.hasNext();) {
        //            EdifCellInstance eci = (EdifCellInstance) i.next();
        //            if (eci.getName().compareToIgnoreCase(constantCellInstance.getName()) == 0) {
        //                instanceFoundInCell = true;
        //                foundConstantCell = eci;
        //                break;
        //            }
        //        }
        //        if (instanceFoundInCell == false)
        //            error_report += ("\nError: XilinxHalfLatchArchitecture: Constant cell " + constantCellInstance.getName()
        //                    + " added to top cell " + cell.getName() + " not actually found.");
        //
        //        // Test 2: Check to see if the found constant cell has the correct property for its INIT value compared to the polarity selected
        //        PropertyList foundConstantCellPropertyList = foundConstantCell.getPropertyList();
        //        Property init_property = foundConstantCellPropertyList.getProperty("INIT");
        //        String init_property_value = init_property.getValue().toString();
        //        if (safeConstantPolarity == 0 && init_property_value.compareToIgnoreCase("0000") != 0) {
        //            error_report += ("\nError: XilinxHalfLatchArchitecture: Constant cell " + constantCellInstance.getName()
        //                    + " has INIT value " + init_property_value + " when 0000 was expected.");
        //        } else if (safeConstantPolarity != 0 && init_property_value.compareToIgnoreCase("1111") != 0) {
        //            error_report += ("\nError: XilinxHalfLatchArchitecture: Constant cell " + constantCellInstance.getName()
        //                    + " has INIT value " + init_property_value + " when 1111 was expected.");
        //        }
        //
        //        // Test 3: Check to see if the safe constant cell has an output port matching what the class says it should be
        //        String safeConstantCellOutputPortName = hlArchitecture.getSafeConstantCellOutputPortName();
        //        boolean foundMatchingOutputPort = false;
        //        for (Iterator i = foundConstantCell.getCellType().getOutputPorts().iterator(); i.hasNext();) {
        //            String ithOutputPortName = ((EdifPort) i.next()).getName();
        //            if (safeConstantCellOutputPortName.compareToIgnoreCase(ithOutputPortName) == 0)
        //                foundMatchingOutputPort = true;
        //        }
        //        if (foundMatchingOutputPort == false)
        //            error_report += ("\nError: XilinxHalfLatchArchitecture: Expected constant cell "
        //                    + constantCellInstance.getName() + " to have output port named " + safeConstantCellOutputPortName + ", but port not found.");
        //
        //        // Test 4: Test primitive replacement requirement function
        //        if (hlArchitecture.cellRequiresReplacement(prim) == false)
        //            error_report += ("\nError: XilinxHalfLatchArchitecture: Expected primitive " + prim + " to require replacement, but false was returned.");
        //
        //        // Test 5: Make sure that the findOrAddPrimitiveReplacement method works
        //        EdifLibraryManager elm = ee.getLibraryManager();
        //        String prim_replacement_cell_name = hlArchitecture.getPrimitiveReplacementType(prim);
        //        hlArchitecture.findOrAddPrimitiveReplacementCell(elm, prim_replacement_cell_name);
        //        boolean foundReplacementCellInLibrary = false;
        //        for (Iterator i = elm.iterator(); i.hasNext();) {
        //            EdifLibrary lib = (EdifLibrary) i.next();
        //            if (lib.containsCellByName(prim_replacement_cell_name) == true)
        //                foundReplacementCellInLibrary = true;
        //        }
        //        if (foundReplacementCellInLibrary == false)
        //            error_report += ("\nError: XilinxHalfLatchArchitecture: Added replacement cell "
        //                    + prim_replacement_cell_name + " for cell " + prim + " but could not find "
        //                    + prim_replacement_cell_name + " in library manager.");
        //
        //        if (error_report != "") {
        //            System.out.println(error_report);
        //            return -1;
        //        } else {
        //            System.out.println("Success: XilinxHalfLatchArchitecture: All tests passed");
        //            return 0;
        //        }

        return 0;
    }

    public static void main(String[] args) {
        XilinxHalfLatchArchitecture.test();
    }
}
