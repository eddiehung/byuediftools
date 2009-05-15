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
package edu.byu.ece.edif.util.jsap.commandgroups;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.PrintStream;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;


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
        ff_fanout.setDefault(TRUE).setHelp("Use highest flip-flop cutset option (default)");
        this.addCommand(ff_fanout);

        connectivity = new Switch(CONNECTIVITY_CUTSET);
        connectivity.setShortFlag(JSAP.NO_SHORTFLAG);
        connectivity.setLongFlag(CONNECTIVITY_CUTSET);
        connectivity.setDefault(FALSE).setHelp("Use older connectivity graph for cutset");
        this.addCommand(connectivity);

    }

    static public boolean getFanout(JSAPResult result) {
        return result.getBoolean(HIGHEST_FANOUT_CUTSET);
    }

    static public boolean getConnectivity(JSAPResult result) {
        return result.getBoolean(CONNECTIVITY_CUTSET);
    }

    static public boolean getFFFanout(JSAPResult result) {
        return result.getBoolean(HIGHEST_FF_FANOUT_CUTSET);
    }

    protected Switch fanout, ff_fanout, connectivity;

    public static final String HIGHEST_FANOUT_CUTSET = "highest_fanout_cutset";

    public static final String HIGHEST_FF_FANOUT_CUTSET = "highest_ff_fanout_cutset";

    public static final String CONNECTIVITY_CUTSET = "connectivity_cutset";

    public static final String FALSE = "false";

    public static final String TRUE = "true";

}
