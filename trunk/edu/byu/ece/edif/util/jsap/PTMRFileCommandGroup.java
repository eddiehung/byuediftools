/*
 * Command group to specify and retrieve a PartialReplicationStringDescription.
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
package edu.byu.ece.edif.util.jsap;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.PrintStream;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.jedif.JEdifParserCommandGroup;
import edu.byu.ece.edif.tools.replicate.PartialReplicationStringDescription;

/**
 * Command group to specify and retrieve a PartialReplicationStringDescription
 * 
 * @author Derrick Gibelyou
 */
public class PTMRFileCommandGroup extends AbstractCommandGroup {

    protected FlaggedOption _input_files_option;

    public static final String INPUT2_OPTION = "ptmr";

    public PTMRFileCommandGroup() {
        super();
        _input_files_option = new FlaggedOption(INPUT2_OPTION);
        _input_files_option.setStringParser(JSAP.STRING_PARSER);
        _input_files_option.setRequired(JSAP.NOT_REQUIRED);
        _input_files_option.addDefault("BLTmr.ptmr");
        _input_files_option.setLongFlag(INPUT2_OPTION);
        _input_files_option.setUsageName("ptmr_file");
        _input_files_option.setHelp("Partial tmr Data Filename.");
        this.addCommand(_input_files_option);

    }

    public static PartialReplicationStringDescription getPartialReplicationDescription(JSAPResult result,
            PrintStream out) {
        FileInputStream fis = null;
        ObjectInputStream in = null;
        PartialReplicationStringDescription ptmrs = null;

        String filename = result.getString(INPUT2_OPTION);
        if (!result.userSpecified(INPUT2_OPTION)) {
            filename = JEdifParserCommandGroup.getInputFileName(result);
            filename = filename.substring(0, filename.lastIndexOf('.'));
            filename += ".ptmr";
        }

        if (!filename.contains("."))
            filename = filename.concat(".ptmr");

        out.print("Loading file " + filename + " . . .");
        try {
            fis = new FileInputStream(filename);
            in = new ObjectInputStream(fis);
            ptmrs = (PartialReplicationStringDescription) in.readObject();
            in.close();
        } catch (InvalidClassException ex) {
            out.println("Your class file is an old version.\n Please " + "rerun the program that created this file");
            ex.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException ex) {
            out.println("Your class file is an old version. Please " + "rerun the program that created this file");
            ex.printStackTrace();
        } catch (IOException ex) {
            out.println("IOException: Cannot find (or open) the specified file. Make"
                    + "sure the file is present and you have access to open it.");
            ex.printStackTrace();
        }
        out.println("Done");
        return ptmrs;
    }

}