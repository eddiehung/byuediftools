/*
 * Replaces all LUTRAMs with a logic implementation to allow readback.
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
package edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.Replace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import edu.byu.ece.edif.arch.xilinx.XilinxGenLib;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifPrintWriter;
import edu.byu.ece.edif.core.EdifTypedValue;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.core.PropertyList;
import edu.byu.ece.edif.core.StringTypedValue;
import edu.byu.ece.edif.tools.sterilize.lutreplace.RLOCRemove;
import edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.TargetPart;
import edu.byu.ece.edif.util.export.jhdl.BuildWrapper;
import edu.byu.ece.edif.util.parse.EdifParser;

/**
 * Takes an EDIF design and replaces all LUTRAMs with a logic implementation.
 * Replacing LUTRAMs allows readback to be used on a design. Note that logic
 * implementations of LUTRAMs are much more costly in terms of size.
 * 
 * @author Nathan Rollins
 * @version $Id:replaceLutRam.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class replaceLutRam {

    /**
     * Default constructor
     * 
     * @param filename The name of the EDIF design file to modify
     */
    public replaceLutRam(String filename) {

        _designname = filename;
        _fixFile(_designname);
    }

    public replaceLutRam(EdifEnvironment eedesign) {
        _eedesign = eedesign;
        _externalCall = true;
        _replaceRLOCs = true;
    }

    /**
     * This function creates a JHDL wrapper file based on the _eedesign
     * EdifEnvironment variable
     */
    public void createJHDLWrapper() {
        ArrayList args = new ArrayList();
        args.add(_newedif);
        BuildWrapper.createJHDLdotJavaFile(_wrapper, _target, _eedesign.getTopCell(), _newedif, args);
    }

    /**
     * The main replacement function.
     */
    public void replaceLutRams() {

        EdifCell topcell = null;
        EdifLibrary ellogiclib = null;
        EdifLibraryManager elmdesign;

        // Add the library for the replacement parts we're going to add
        // to this edif design.
        _createEnvironments();

        if (_replaceRLOCs) {
            _remover = new RLOCRemove(_eelogiclib, _newedif);
            //_eelogiclib;
        }
        topcell = _eelogiclib.getTopCell();
        ellogiclib = topcell.getLibrary();
        elmdesign = _eedesign.getLibraryManager();
        try {
            if (!elmdesign.containsLibrary(ellogiclib.getName()))
                elmdesign.addLibrary(ellogiclib);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }

        // grab the logic implemented LUTRAM cells from the design 
        EdifCell ram16x1d_logic = ellogiclib.getCell("RAM16X1D_logic");
        EdifCell ram16x1d_1_logic = ellogiclib.getCell("RAM16X1D_1_logic");
        EdifCell ram16x1s_logic = ellogiclib.getCell("RAM16X1S_logic");
        EdifCell ram16x1s_1_logic = ellogiclib.getCell("RAM16X1S_1_logic");
        EdifCell ram16x2d_logic = ellogiclib.getCell("RAM16X2D_logic");
        EdifCell ram16x2s_logic = ellogiclib.getCell("RAM16X2S_logic");
        EdifCell ram16x4d_logic = ellogiclib.getCell("RAM16X4D_logic");
        EdifCell ram16x4s_logic = ellogiclib.getCell("RAM16X4S_logic");
        EdifCell ram16x8d_logic = ellogiclib.getCell("RAM16X8D_logic");
        EdifCell ram16x8s_logic = ellogiclib.getCell("RAM16X8S_logic");

        EdifCell ram32x1d_logic = ellogiclib.getCell("RAM32X1D_logic");
        EdifCell ram32x1d_1_logic = ellogiclib.getCell("RAM32X1D_1_logic");
        EdifCell ram32x1s_logic = ellogiclib.getCell("RAM32X1S_logic");
        EdifCell ram32x1s_1_logic = ellogiclib.getCell("RAM32X1S_1_logic");
        EdifCell ram32x2d_logic = ellogiclib.getCell("RAM32X2D_logic");
        EdifCell ram32x2s_logic = ellogiclib.getCell("RAM32X2S_logic");
        EdifCell ram32x4d_logic = ellogiclib.getCell("RAM32X4D_logic");
        EdifCell ram32x4s_logic = ellogiclib.getCell("RAM32X4S_logic");
        EdifCell ram32x8d_logic = ellogiclib.getCell("RAM32X8D_logic");
        EdifCell ram32x8s_logic = ellogiclib.getCell("RAM32X8S_logic");

        EdifCell ram64x1d_logic = ellogiclib.getCell("RAM64X1D_logic");
        EdifCell ram64x1d_1_logic = ellogiclib.getCell("RAM64X1D_1_logic");
        EdifCell ram64x1s_logic = ellogiclib.getCell("RAM64X1S_logic");
        EdifCell ram64x1s_1_logic = ellogiclib.getCell("RAM64X1S_1_logic");
        EdifCell ram64x2s_logic = ellogiclib.getCell("RAM64X2S_logic");

        EdifCell ram128x1s_logic = ellogiclib.getCell("RAM128X1S_logic");
        EdifCell ram128x1s_1_logic = ellogiclib.getCell("RAM128X1S_1_logic");

        EdifCell srl16_logic = ellogiclib.getCell("SRL16_logic");
        EdifCell srl16_1_logic = ellogiclib.getCell("SRL16_1_logic");
        EdifCell srl16e_logic = ellogiclib.getCell("SRL16E_logic");
        EdifCell srl16e_1_logic = ellogiclib.getCell("SRL16E_1_logic");
        EdifCell srlc16_logic = ellogiclib.getCell("SRLC16_logic");
        EdifCell srlc16_1_logic = ellogiclib.getCell("SRLC16_1_logic");
        EdifCell srlc16e_logic = ellogiclib.getCell("SRLC16E_logic");
        EdifCell srlc16e_1_logic = ellogiclib.getCell("SRLC16E_1_logic");

        // grab the LUTRAM cells from the Xilinx library
        EdifCell ram16x1d = elmdesign.getCell("RAM16X1D");
        EdifCell ram16x1d_1 = elmdesign.getCell("RAM16X1D_1");
        EdifCell ram16x1s = elmdesign.getCell("RAM16X1S");
        EdifCell ram16x1s_1 = elmdesign.getCell("RAM16X1S_1");
        // EdifCell ram16x2d = elmdesign.getCell(RAM16X2D();
        EdifCell ram16x2s = elmdesign.getCell("RAM16X2S");
        //EdifCell ram16x4d = elmdesign.getCell("RAM16X4D");
        EdifCell ram16x4s = elmdesign.getCell("RAM16X4S");
        //EdifCell ram16x8d = elmdesign.getCell("RAM16X8D");
        EdifCell ram16x8s = elmdesign.getCell("RAM16X8S");

        EdifCell ram32x1d = elmdesign.getCell("RAM32X1D");
        EdifCell ram32x1d_1 = elmdesign.getCell("RAM32X1D_1");
        EdifCell ram32x1s = elmdesign.getCell("RAM32X1S");
        EdifCell ram32x1s_1 = elmdesign.getCell("RAM32X1S_1");
        //EdifCell ram32x2d = elmdesign.getCell("RAM32X2D");
        EdifCell ram32x2s = elmdesign.getCell("RAM32X2S");
        //EdifCell ram32x4d = elmdesign.getCell("RAM32X4D");
        EdifCell ram32x4s = elmdesign.getCell("RAM32X4S");
        //EdifCell ram32x8d = elmdesign.getCell("RAM32X8D");
        EdifCell ram32x8s = elmdesign.getCell("RAM32X8S");

        EdifCell ram64x1d = elmdesign.getCell("RAM64X1D");
        EdifCell ram64x1d_1 = elmdesign.getCell("RAM64X1D_1");
        EdifCell ram64x1s = elmdesign.getCell("RAM64X1S");
        EdifCell ram64x1s_1 = elmdesign.getCell("RAM64X1S_1");
        EdifCell ram64x2s = elmdesign.getCell("RAM64X2S");

        EdifCell ram128x1s = elmdesign.getCell("RAM128X1S");
        EdifCell ram128x1s_1 = elmdesign.getCell("RAM128X1S_1");

        EdifCell srl16 = elmdesign.getCell("SRL16");
        EdifCell srl16_1 = elmdesign.getCell("SRL16_1");
        EdifCell srl16e = elmdesign.getCell("SRL16E");
        EdifCell srl16e_1 = elmdesign.getCell("SRL16E_1");
        EdifCell srlc16 = elmdesign.getCell("SRLC16");
        EdifCell srlc16_1 = elmdesign.getCell("SRLC16_1");
        EdifCell srlc16e = elmdesign.getCell("SRLC16E");
        EdifCell srlc16e_1 = elmdesign.getCell("SRLC16E_1");

        // iterate through the design cells
        for (Iterator i = elmdesign.getCells().iterator(); i.hasNext();) {
            EdifCell cell = (EdifCell) i.next();

            for (Iterator j = cell.cellInstanceIterator(); j.hasNext();) {
                EdifCellInstance cellinst = (EdifCellInstance) j.next();

                //System.out.println("cell instance: " + cellinst);

                _modifyLutRam(ram16x1d, ram16x1d_logic, cellinst);
                _modifyLutRam(ram16x1d_1, ram16x1d_1_logic, cellinst);
                _modifyLutRam(ram16x1s, ram16x1s_logic, cellinst);
                _modifyLutRam(ram16x1s_1, ram16x1s_1_logic, cellinst);
                //_modifyLutRam(ram16x2d, ram16x2d_logic, cellinst);
                _modifyLutRam(ram16x2s, ram16x2s_logic, cellinst);
                //_modifyLutRam(ram16x4d, ram16x4d_logic, cellinst);
                _modifyLutRam(ram16x4s, ram16x4s_logic, cellinst);
                //_modifyLutRam(ram16x8d, ram16x8d_logic, cellinst);
                _modifyLutRam(ram16x8s, ram16x8s_logic, cellinst);

                _modifyLutRam(ram32x1d, ram32x1d_logic, cellinst);
                _modifyLutRam(ram32x1d_1, ram32x1d_1_logic, cellinst);
                _modifyLutRam(ram32x1s, ram32x1s_logic, cellinst);
                _modifyLutRam(ram32x1s_1, ram32x1s_1_logic, cellinst);
                //_modifyLutRam(ram32x2d, ram32x2d_logic, cellinst);
                _modifyLutRam(ram32x2s, ram32x2s_logic, cellinst);
                //_modifyLutRam(ram32x4d, ram32x4d_logic, cellinst);
                _modifyLutRam(ram32x4s, ram32x4s_logic, cellinst);
                //_modifyLutRam(ram32x8d, ram32x8d_logic, cellinst);
                _modifyLutRam(ram32x8s, ram32x8s_logic, cellinst);

                _modifyLutRam(ram64x1d, ram64x1d_logic, cellinst);
                _modifyLutRam(ram64x1d_1, ram64x1d_1_logic, cellinst);
                _modifyLutRam(ram64x1s, ram64x1s_logic, cellinst);
                _modifyLutRam(ram64x1s_1, ram64x1s_1_logic, cellinst);
                _modifyLutRam(ram64x2s, ram64x2s_logic, cellinst);

                _modifyLutRam(ram128x1s, ram128x1s_logic, cellinst);
                _modifyLutRam(ram128x1s_1, ram128x1s_1_logic, cellinst);

                _modifyLutRam(srl16, srl16_logic, cellinst);
                _modifyLutRam(srl16_1, srl16_1_logic, cellinst);
                _modifyLutRam(srl16e, srl16e_logic, cellinst);
                _modifyLutRam(srl16e_1, srl16e_1_logic, cellinst);
                _modifyLutRam(srlc16, srlc16_logic, cellinst);
                _modifyLutRam(srlc16_1, srlc16_1_logic, cellinst);
                _modifyLutRam(srlc16e, srlc16e_logic, cellinst);
                _modifyLutRam(srlc16e_1, srlc16e_1_logic, cellinst);

            }
        }
        elmdesign.pruneNonReferencedCells();
    }

    /**
     * This function sets the name of the EDIF output file.
     * 
     * @param filename the name of the output EDIF file
     */
    public void setOutputEDIFFile(String filename) {
        _setOutputEDIFFile(filename);
    }

    /**
     * This function sets the name of the JHDL wrapper output file.
     * 
     * @param filename the name of the output wrapper file
     */
    public void setJHDLWrapperFile(String filename) {
        _setJHDLWrapperFile(filename);
    }

    /**
     * This function sets the name of the EDIF LUTRAM replacement library file
     * 
     * @param filename the name of the EDIF logic LUTRAM library file
     */
    public void setReplaceLibraryFile(String filename) {
        _setReplaceLibraryFile(filename);
    }

    /**
     * This function sets the FPGA target architecture
     * 
     * @param filename the name of the architecture
     */
    public void setTarget(String target) {
        _setTarget(target);
    }

    /**
     * This function writes an EDIF file corresponding to the _eedesign
     * EdifEnvironment variable
     */
    public void writeEdif() {
        try {
            _eedesign.toEdif(new EdifPrintWriter(_newedif));
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
    }

    /**
     * The main function which can be used to execute the replacement operation
     * from the command-line.
     * 
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        _parseArgs(args);
        if (!_justwrapper) {
            System.out.println("\nLOGIC LIBRARY EDIF FILE: " + _logiclib);
            System.out.println("REPLACING LUTRAMS IN EDIF FILE: " + _designname);
            System.out.println("CREATING NEW EDIF FILE: " + _newedif);
            System.out.println("CREATING JHDL WRAPPER FILE: " + _wrapper + "\n");

            // Make changes here depending on the value of _targetPart

            if (_targetPart != TargetPart.VIRTEX && _targetPart != TargetPart.VIRTEX2)
                _replaceRLOCs = true;

            replaceLutRam rlr = new replaceLutRam(_designname);
            rlr.replaceLutRams();
            rlr.writeEdif();
            if (_targetPart == TargetPart.VIRTEX || _targetPart == TargetPart.VIRTEX2) {
                //rlr.writeEdif();
                // It would be better to get rid of _target and simply use
                // _targetPart in the function call that createJHDLWrapper() makes.
                _target = _targetPart.toString();
                rlr.createJHDLWrapper();
            }
            //            else {
            //                RLOCRemove remover = new RLOCRemove(_eedesign, _newedif);
            //                remover.writeEdif();
            //            }
        } else {
            System.out.println("\nJUST CREATING A JHDL WRAPPER.");
            System.out.println("DESIGN EDIF FILE: " + _designname);
            System.out.println("CREATING JHDL WRAPPER FILE: " + _wrapper + "\n");
            replaceLutRam rlr = new replaceLutRam(_designname);
            rlr._createEnvironments();
            _newedif = _designname;
            rlr.writeEdif();
            rlr.createJHDLWrapper();
        }
    }

    /**
     * This function creates the EdifEnvironments for the library and design
     * files.
     */
    private void _createEnvironments() {
        // parse the logic LUTRAM implementation EDIF file
        try {

            //_eelogiclib = EdifParser.translate(_logiclib);
            if (_externalCall) {
                _eelogiclib = EdifParser.translate(this.getClass().getResourceAsStream(_logiclib));
            } else
                _eelogiclib = EdifParser.translate(_logiclib);

            if (_eelogiclib == null)
                throw new IOException("Could not find the specified library.");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }

        // parse the EDIF of the design to modify
        if (!_externalCall) {
            try {
                _eedesign = EdifParser.translate(_designname);
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
            }
        }

    }

    /**
     * This function ensures that the file has an EDIF extension
     * 
     * @param filename file to fix
     */
    private static void _fixFile(String filename) {
        if (filename.indexOf(".") == -1)
            filename = _designname + _ext;
    }

    /**
     * This function returns the integer value of a hexidecimal string
     * 
     * @param initstring the hexidecimal string
     */
    private static int _initString2int(String initstring) {
        int radix = 16;
        int retval = 0;

        for (int i = 0; i < initstring.length(); i++) {
            retval = retval | (Character.digit(initstring.charAt(initstring.length() - i - 1), radix) << i * 4);
        }
        return retval;
    }

    /**
     * This function returns the integer value of a hexidecimal string
     * 
     * @param initstring the hexidecimal string
     */
    private static long _initString2long(String initstring) {
        int radix = 16;
        long retval = 0;

        for (int i = 0; i < initstring.length(); i++) {
            retval = retval | (Character.digit(initstring.charAt(initstring.length() - i - 1), radix) << i * 4);
        }
        return retval;
    }

    /**
     * Modify LUTRAM instances and uniquify them so that each instance can be
     * initialized according to the Xilinx LUTRAM primitive init string.
     * 
     * @param oldCell the Xilinx primitive
     * @param newCell the logic version of the Xilinx primitive
     * @param cellInstance the instance of the LUTRAM to be replaced and
     * initialized
     */
    private static void _modifyLutRam(EdifCell oldCell, EdifCell newCell, EdifCellInstance cellInstance) {
        boolean isInit = false;

        int initCount = 0;
        long initInt = 0;

        StringTypedValue valueR = new StringTypedValue("R");
        StringTypedValue valueS = new StringTypedValue("S");
        StringTypedValue initString = new StringTypedValue("0");

        if (oldCell != null && cellInstance.getCellType().equalsName(oldCell)) {
            PropertyList cellInstancePropertyList = cellInstance.getPropertyList();
            Property toRemove = null;
            if (cellInstancePropertyList != null) {
                for (Property cellInstanceProperty : cellInstancePropertyList.values()) {
                    if (cellInstanceProperty.getName().equals("INIT")) {
                        initString = (StringTypedValue) cellInstanceProperty.getValue();
                        toRemove = cellInstanceProperty;
                        isInit = true;
                    }
                }
                if (toRemove != null)
                    cellInstancePropertyList.remove(toRemove.getName());
            }
            initInt = (isInit) ? _initString2int(initString.getStringValue()) : 0;

            EdifCell copy = null;
            try {
                copy = new EdifCell(newCell.getLibrary(), newCell, newCell.getLibrary().getUniqueEdifCellNameable(
                        newCell));
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            }
            //EdifCell cpy = newcell.copy();
            //EdifLibrary elcell = newcell.getLibrary();
            //elcell.addCell(cpy);
            cellInstance.modifyCellRef(copy);

            //System.out.println("Here!");

            // For some reason the NetList reference inside oldCell is null.  Not sure
            // what's going on with that.  The problem may have occurred farther up.
            //			try {
            //                EdifCellInstance newInstance = new EdifCellInstance(cellInst.getName() + "_replace", cellInst
            //                        .getParent(), copy);
            //                oldCell.getNetList();
            //                EdifTools.replaceCellInstance(oldCell, cellInst, newInstance);
            //            } catch (InvalidEdifNameException e) {
            //                e.toRuntime();
            //            }
            for (EdifCellInstance logicInstance : copy.getSafeCellInstanceList()) {
                if (logicInstance.getCellType().equals(XilinxGenLib.FDCPE())
                        || logicInstance.getCellType().equals(XilinxGenLib.FDPE())
                        || logicInstance.getCellType().equals(XilinxGenLib.FDCPE_1())
                        || logicInstance.getCellType().equals(XilinxGenLib.FDPE_1())) {
                    //System.out.println("Replacing logicInstance: " + logicInstance.getName());
                    isInit = false;
                    PropertyList logicInstancePropertyList = logicInstance.getPropertyList();
                    if (logicInstancePropertyList != null) {
                        for (Property logicInstanceProperty : logicInstancePropertyList.values()) {
                            if (logicInstanceProperty.getName().equals("INIT")) {
                                isInit = true;
                                if (((initInt >> initCount++) & 1) == 1)
                                    logicInstanceProperty.setValue(valueS);
                                else
                                    logicInstanceProperty.setValue(valueR);
                            }
                        }
                    }
                    if (!isInit) {
                        if (((initInt >> initCount++) & 1) == 1)
                            logicInstance.addProperty(new Property("INIT", (EdifTypedValue) valueS));
                        else
                            logicInstance.addProperty(new Property("INIT", (EdifTypedValue) valueR));
                    }

                }
            }
        }
    }

    /**
     * This function parses the arguments passed in from the command-line when
     * executing the main function.
     * 
     * @param args the command-line arguments
     */
    private static void _parseArgs(String[] args) {
        int idx = 0;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-design")) {
                i++;
                _designname = args[i];
                _fixFile(_designname);
            } else if (args[i].equals("-lib")) {
                i++;
                _setReplaceLibraryFile(args[i]);
            } else if (args[i].equals("-edifout")) {
                i++;
                _setOutputEDIFFile(args[i]);
            } else if (args[i].equals("-justwrapper")) {
                _justwrapper = true;
            } else if (args[i].equals("-target")) {
                i++;
                _setTarget(args[i]);
            } else if (args[i].equals("-wrapper")) {
                i++;
                _setJHDLWrapperFile(args[i]);
            } else {
                _usage();
                System.exit(-1);
            }
        }
    }

    /**
     * This function sets the name of the EDIF output file.
     * 
     * @param filename the name of the output EDIF file
     */
    private static void _setOutputEDIFFile(String filename) {
        if (filename != null) {
            _newedif = filename;
            _fixFile(_newedif);
        }
    }

    /**
     * This function sets the name of the JHDL wrapper output file.
     * 
     * @param filename the name of the output wrapper file
     */
    private static void _setJHDLWrapperFile(String filename) {
        if (filename != null) {
            _wrapper = filename;
            if (_wrapper.indexOf(".java") == -1)
                _wrapper = _wrapper + ".java";
        }
    }

    /**
     * This function sets the name of the EDIF LUTRAM replacement library file
     * 
     * @param filename the name of the EDIF logic LUTRAM library file
     */
    private static void _setReplaceLibraryFile(String filename) {
        if (filename != null) {
            _logiclib = filename;
            _fixFile(_logiclib);
        }
    }

    /**
     * This function sets the FPGA target architecture
     * 
     * @param filename the name of the architecture
     */
    private static void _setTarget(String target) {
        try {
            _targetPart = TargetPart.valueOf(target.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Not a valid target: " + target);
            System.err.print("Valid target parts: ");
            for (TargetPart part : TargetPart.values()) {
                System.err.print(part);
            }
            System.exit(-1);
        }

        //    	if (target.equalsIgnoreCase("Virtex"))
        //            _target = "Virtex";
        //        else if (target.equalsIgnoreCase("Virtex2"))
        //            _target = "Virtex2";
        //        // Modified this part to accept the Virtex 3-5.
        //        else if (target.equalsIgnoreCase("Virtex3"))
        //            _target = "Virtex3";
        //        else if (target.equalsIgnoreCase("Virtex4"))
        //            _target = "Virtex4";
        //        else if (target.equalsIgnoreCase("Virtex5"))
        //            _target = "Virtex5";
        //        else
        //            _target = "Virtex";
    }

    /**
     * This function prints out the command-line options
     */
    private static void _usage() {

        System.err.println();
        System.err.println("replaceLutRam Usage: java replaceLutRam [-opts]");
        System.err.println("\t-design <design_name>\t\t EDIF file to replace lutrams ");
        System.err.println("\t\t\t\t\t (file name with or without extension)");
        System.err.println("\t-edifout <output_EDIF_filename>\t name of EDIF file with replacements");
        System.err.println("\t-help\t\t\t\t print this message");
        System.err.println("\t-justwrapper \t\t\t Just create wrapper for <design>");
        System.err.println("\t-lib <EDIF_libaray>\t\t EDIF library to use for replacement");
        System.err.println("\t-target <architecture>\t\t either: Virtex or Virtex2");
        System.err.println("\t-wrapper <wrapper_name>\t\t file name of JHDL wrapper created");
        System.err.println("\t\t\t\t\t (file name with or without extension)");
        System.err.println();
    }

    private static boolean _externalCall = false;

    private static boolean _justwrapper = false;

    private static boolean _replaceRLOCs = false;

    private static RLOCRemove _remover = null;

    private static EdifEnvironment _eedesign;

    private static EdifEnvironment _eelogiclib;

    private static TargetPart _targetPart = TargetPart.VIRTEX;

    private static String _ext = ".edn";

    private static String _designname = "alljhdlmultitest" + _ext;

    private static String _logiclib = "alllutramtest" + _ext;

    private static String _newedif = "newfile" + _ext;

    private static String _target = "Virtex";

    private static String _wrapper = "lutramwrapper.java";
}
