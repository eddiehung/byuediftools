/*
 * A parser command group for JEdif binaries.
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

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.util.export.serialize.JEdifFileContents;
import edu.byu.ece.edif.util.export.serialize.JEdifFileManager;

/**
 * This class calls the JEdifFileManager to get JEdifFileContents or EdifEnvironment
 * objects from a .jedif file specified on the command-line.
 */
public class JEdifParserCommandGroup extends InputFileCommandGroup {

    public static EdifEnvironment getEdifEnvironment(JSAPResult result, PrintStream out) {
        JEdifFileContents jEdifFile = JEdifFileManager.getJEdifFileContents(getInputFileName(result), out);
        return jEdifFile.getEdifEnvironment();
    }
    
    public static JEdifFileContents getJEdifFileContents(JSAPResult result, PrintStream out) {
        JEdifFileContents jEdifFile = JEdifFileManager.getJEdifFileContents(getInputFileName(result), out);
        return jEdifFile;
    }
    
}