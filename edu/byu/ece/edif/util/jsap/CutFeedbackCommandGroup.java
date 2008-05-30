/*
 * Adds options to select Feedback cutting algorithm
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
import com.martiansoftware.jsap.Switch;

import edu.byu.ece.edif.jedif.JEdifParserCommandGroup;

/**
 * Adds options to select Feedback cutting algorithm
 * 
 * @author Derrick Gibelyou
 */
public class CutFeedbackCommandGroup extends AbstractCommandGroup {

    public CutFeedbackCommandGroup() {
        super();

        fanout = new Switch(HIGHEST_FANOUT_CUTSET);
        fanout.setShortFlag(JSAP.NO_SHORTFLAG);
        fanout.setLongFlag(HIGHEST_FANOUT_CUTSET);
        fanout.setDefault(FALSE).setHelp("Use highest fanout cutset algorithm.");
        this.addCommand(fanout);

        ff_fanout = new Switch(HIGHEST_FF_FANOUT_CUTSET);
        ff_fanout.setShortFlag(JSAP.NO_SHORTFLAG);
        ff_fanout.setLongFlag(HIGHEST_FF_FANOUT_CUTSET);
        ff_fanout.setDefault(FALSE).setHelp("Use highest flip-flop cutset option");
        this.addCommand(ff_fanout);

        //TODO: remove since this option will now only be used in JEdifTMRAnalysis
        //		iob_feedback = new Switch(NO_IOB_FB);
        //        iob_feedback.setShortFlag(JSAP.NO_SHORTFLAG);
        //        iob_feedback.setLongFlag(NO_IOB_FB);
        //        iob_feedback.setDefault(FALSE);
        //        iob_feedback.setHelp("The user may wish to exclude the IOBs (specifically "
        //                + "inout ports) from feedback analysis if there is no true feedback "
        //                + "(by design). This may greatly reduce the amount of feedback detected.");
        //        this.addCommand(iob_feedback);

        // IOB Output filename flag
        iob_input_file = new FlaggedOption(IOB_INPUT_FILE);
        iob_input_file.setStringParser(JSAP.STRING_PARSER);
        iob_input_file.setRequired(JSAP.NOT_REQUIRED);
        iob_input_file.addDefault("bltmr");
        iob_input_file.setShortFlag(JSAP.NO_SHORTFLAG);
        iob_input_file.setLongFlag(IOB_INPUT_FILE);
        iob_input_file.setUsageName("input_file");
        iob_input_file.setHelp("Filename and path to the IOB analysis file that will be used. Required.");
        this.addCommand(iob_input_file);

    }

    static public boolean getFanout(JSAPResult result) {
        return result.getBoolean(HIGHEST_FANOUT_CUTSET);
    }

    //	static public boolean getCutIOB(JSAPResult result) {
    //        return result.getBoolean(NO_IOB_FB);
    //    }

    static public boolean getFFFanout(JSAPResult result) {
        return result.getBoolean(HIGHEST_FF_FANOUT_CUTSET);
    }

    public static SharedIOBAnalysis getIOBAnalysis(JSAPResult result, PrintStream out) {
        FileInputStream fis = null;
        ObjectInputStream in = null;
        SharedIOBAnalysis iobAnal = null;

        String filename = result.getString(IOB_INPUT_FILE);

        if (!result.userSpecified(IOB_INPUT_FILE)) {
            filename = JEdifParserCommandGroup.getInputFileName(result);
            filename = filename.substring(0, filename.lastIndexOf('.'));
            filename += ".iob";
        }

        if (!filename.contains("."))
            filename = filename.concat(".iob");

        out.print("Loading file " + filename + " . . .");
        try {
            fis = new FileInputStream(filename);
            in = new ObjectInputStream(fis);
            iobAnal = (SharedIOBAnalysis) in.readObject();
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
        return iobAnal;
    }

    protected Switch fanout, ff_fanout, iob_feedback;

    protected FlaggedOption iob_input_file;

    public static final String NO_IOB_FB = "no_iob_feedback";

    public static final String HIGHEST_FANOUT_CUTSET = "highest_fanout_cutset";

    public static final String HIGHEST_FF_FANOUT_CUTSET = "highest_ff_fanout_cutset";

    public static final String FALSE = "false";

    public static final String IOB_INPUT_FILE = "iob_input";

}
