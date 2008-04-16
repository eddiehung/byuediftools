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
package edu.byu.ece.edif.arch.xilinx;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.MultiNamedObject;
import edu.byu.ece.edif.core.NamedObject;

/**
 * Inserts EdifCell primitives into the Xilinx Library that aren't already in
 * the unisim_VCOMP.vhd file. note: This class is currently unused.
 */
public class XilinxMacros {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    public static void insertMacros(EdifLibrary lib) {
        _initIFD(lib);
        _initVCC(lib);
        _initGND(lib);
    }

    public static EdifCell IFD() {
        return _IFD;
    }

    public static EdifCell VCC() {
        return _VCC;
    }

    public static EdifCell GND() {
        return _GND;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private methods                   ////

    private static void _initIFD(EdifLibrary lib) {
        try {
            _IFD = new EdifCell(lib, "IFD");
            _IFD.addPort(new NamedObject("Q"), 1, EdifPort.OUT);
            _IFD.addPort(new NamedObject("D"), 1, EdifPort.IN);
            _IFD.addPort(new NamedObject("C"), 1, EdifPort.IN);
            _IFD.addPort(new NamedObject("GSR"), 1, EdifPort.IN);
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
    }

    private static void _initVCC(EdifLibrary lib) {
        try {
            _VCC = new EdifCell(lib, "VCC");
            String outputNames[] = { "P", "VCC" };
            EdifNameable portname = new MultiNamedObject(outputNames);
            _VCC.addPort(portname, 1, EdifPort.OUT);
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
    }

    private static void _initGND(EdifLibrary lib) {
        try {
            _GND = new EdifCell(lib, "GND");
            String outputNames[] = { "G", "GROUND" };
            EdifNameable portname = new MultiNamedObject(outputNames);
            _GND.addPort(portname, 1, EdifPort.OUT);
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    private static EdifCell _IFD;

    private static EdifCell _VCC;

    private static EdifCell _GND;
}
