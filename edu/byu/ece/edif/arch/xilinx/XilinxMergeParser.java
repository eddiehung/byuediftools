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

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.util.merge.EdifMergeParser;
import edu.byu.ece.edif.util.parse.ParseException;


/**
 * This is a simple class that contains a static method for parsing and merging
 * edif files using the Xilinx library as the primitive library.
 * 
 * @return The top-level {@link EdifCell} of the parsed EDIF file.
 * @author wirthlin
 * @see edu.byu.ece.edif.util.merge.EdifMergeParser
 */
public class XilinxMergeParser {

    public static EdifCell parseAndMergeXilinx(String args[]) {
        EdifLibrary primitiveLibrary = XilinxGenLib.library;
        EdifEnvironment top = EdifMergeParser.parseAndMerge(args, primitiveLibrary);
        EdifDesign edifDesign = top.getTopDesign();
        EdifCellInstance topCellInstance = edifDesign.getTopCellInstance();
        EdifCell cell = topCellInstance.getCellType();
        return cell;
    }

    public static EdifCell parseAndMergeXilinx(String topFilename, Collection<String> dirs, Collection files,
            PrintStream outstream) {

        EdifLibrary primitiveLibrary = XilinxGenLib.library;
        EdifCell cell = null;

        try {
            Set searchDirs = EdifMergeParser.createDefaultDirs();
            searchDirs.addAll(dirs);
            EdifEnvironment top = EdifMergeParser.parseAndMergeEdif(topFilename, searchDirs, files, primitiveLibrary);
            EdifDesign edifDesign = top.getTopDesign();
            EdifCellInstance topCellInstance = edifDesign.getTopCellInstance();
            cell = topCellInstance.getCellType();
        } catch (FileNotFoundException e) {
            System.err.println(e);
            System.exit(1);
        } catch (ParseException e) {
            System.err.println(e);
            System.exit(1);
        }

        return cell;
    }

}
