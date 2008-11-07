/*
 * Removes LOCs or RLOCs from an EDIF file.
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
package edu.byu.ece.edif.tools.sterilize.lutreplace;

import java.util.ArrayList;
import java.util.Iterator;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPrintWriter;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.core.PropertyList;
import edu.byu.ece.edif.util.parse.EdifParser;

/**
 * This tool simply removes LOCs or RLOCs from EDIF.
 * 
 * @author Nathan Rollins
 * @version $Id$
 */

public class RLOCRemove {

    public RLOCRemove(String filename) {
        try {
            _eedesign = EdifParser.translate(filename);
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
        EdifLibraryManager elmdesign = _eedesign.getLibraryManager();

        removeRLOC(elmdesign);
    }

    public RLOCRemove(EdifEnvironment eeDesign, String newFileName) {
        _eedesign = eeDesign;
        _newedif = newFileName;
        _type = "RLOC";

        EdifLibraryManager elmdesign = _eedesign.getLibraryManager();

        removeRLOC(elmdesign);
    }

    public RLOCRemove(EdifEnvironment eeDesign) {
        _eedesign = eeDesign;
        _type = "RLOC";

        EdifLibraryManager elmdesign = _eedesign.getLibraryManager();

        removeRLOC(elmdesign);
    }

    private void removeRLOC(EdifLibraryManager elmdesign) {
        for (Iterator i = elmdesign.getCells().iterator(); i.hasNext();) {
            EdifCell cell = (EdifCell) i.next();

            for (Iterator j = cell.cellInstanceIterator(); j.hasNext();) {
                EdifCellInstance inst = (EdifCellInstance) j.next();

                PropertyList instpl = inst.getPropertyList();
                Property toremove = null;
                if (instpl != null) {
                    for (Iterator k = instpl.values().iterator(); k.hasNext();) {
                        Object key = k.next();
                        Property instprop = (Property) key;
                        if (instprop.getName().equalsIgnoreCase(_type)) {
                            toremove = instprop;
                            Object v = instpl.remove(instprop);
                        }
                    }
                    if (toremove != null) {
                        // instpl.remove(toremove.getName());
                        Object val = instpl.remove(toremove.getName().toLowerCase());
                    }
                }
            }
            for (Iterator j = cell.netListIterator(); j.hasNext();) {
                EdifNet net = (EdifNet) j.next();

                PropertyList netpl = net.getPropertyList();
                Property toremove = null;
                if (netpl != null) {
                    for (Iterator k = netpl.values().iterator(); k.hasNext();) {
                        Property netprop = (Property) k.next();
                        if (netprop.getName().equals(_type)) {
                            toremove = netprop;
                        }
                    }
                    if (toremove != null) {
                        netpl.remove(toremove.getName().toLowerCase());
                    }
                }
            }
        }
    }

    /**
     * This function sets the name of the EDIF output file.
     * 
     * @param filename the name of the output EDIF file
     */
    public void setOutputEdifFile(String filename) {
        _newedif = filename;
        if (_newedif.indexOf(".") == -1)
            _newedif = _newedif + _ext;
    }

    /**
     * This function writes an EDIF file corresponding to the _eedesign
     * EdifEnvironment variable
     */
    public void writeEdif() {

        ArrayList args = new ArrayList();
        args.add(_newedif);
        if (_eedesign != null) {
            try {
                _eedesign.toEdif(new EdifPrintWriter((String) args.get(0)));
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
            }
        }
    }

    /**
     * A main function
     * 
     * @param args the command-line input arguments
     */
    public static void main(String[] args) {

        _parseArgs(args);
        RLOCRemove rlr = new RLOCRemove(_designname);
        rlr.writeEdif();
    }

    /**
     * This function parses the arguments passed in from the command-line when
     * executing the main function.
     * 
     * @param args the command-line arguments
     */
    private static void _parseArgs(String[] args) {

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-design")) {
                i++;
                _designname = args[i];
                if (_designname.indexOf(".") == -1)
                    _designname = _designname + _ext;
            } else if (args[i].equals("-edifout")) {
                i++;
                _newedif = args[i];
            } else if (args[i].equals("-type")) {
                i++;
                if (args[i].equals("-l"))
                    _type = "LOC";
                else
                    _type = "RLOC";
            } else {
                _usage();
                System.exit(-1);
            }
        }
    }

    /**
     * This function prints out the command-line options
     */
    private static void _usage() {

        System.err.println();
        System.err.println("RLOCRemove Usage: java RLOCRemove [-opts]");
        System.err.println("\t-design <design_name>\t\t EDIF file to replace lutrams ");
        System.err.println("\t\t\t\t\t (file name with or without extension)");
        System.err.println("\t-edifout <output_EDIF_filename>\t name of EDIF file with replacements");
        System.err.println("\t-help\t\t\t\t print this message");
        System.err.println("\t-type {-l | -r} \t\t either: LOC or RLOC");
        System.err.println();
    }

    private static EdifEnvironment _eedesign;

    private static String _ext = ".edn";

    private static String _designname = "newfile" + _ext;

    private static String _newedif = "newfile2" + _ext;

    private static String _type = "RLOC";
}
