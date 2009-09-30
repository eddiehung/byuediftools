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

import byucc.jhdl.parsers.edif.syntaxtree.after;

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

        after_ff_cutset = new Switch(AFTER_FF_CUTSET);
        after_ff_cutset.setShortFlag(JSAP.NO_SHORTFLAG);
        after_ff_cutset.setLongFlag(AFTER_FF_CUTSET);
        after_ff_cutset.setDefault(FALSE).setHelp("This cutset algorithm inserts a voter after every flip-flop.");
        this.addCommand(after_ff_cutset);
        
        before_ff_cutset = new Switch(BEFORE_FF_CUTSET);
        before_ff_cutset.setShortFlag(JSAP.NO_SHORTFLAG);
        before_ff_cutset.setLongFlag(BEFORE_FF_CUTSET);
        before_ff_cutset.setDefault(FALSE).setHelp("This cutset algorithm inserts a voter before every flip-flop.");
        this.addCommand(before_ff_cutset);
        
        connectivity = new Switch(CONNECTIVITY_CUTSET);
        connectivity.setShortFlag(JSAP.NO_SHORTFLAG);
        connectivity.setLongFlag(CONNECTIVITY_CUTSET);
        connectivity.setDefault(FALSE).setHelp("Use older connectivity graph for cutset");
        this.addCommand(connectivity);

        basic_decomposition = new Switch(BASIC_DECOMPOSITION);
        basic_decomposition.setShortFlag(JSAP.NO_SHORTFLAG);
        basic_decomposition.setLongFlag(BASIC_DECOMPOSITION);
        basic_decomposition.setDefault(FALSE).setHelp("Use basic SCC decomposition cutset option");
        this.addCommand(basic_decomposition);
        
        fanout = new Switch(HIGHEST_FANOUT_CUTSET);
        fanout.setShortFlag(JSAP.NO_SHORTFLAG);
        fanout.setLongFlag(HIGHEST_FANOUT_CUTSET);
        fanout.setDefault(FALSE).setHelp("Use highest fanout cutset algorithm.");
        this.addCommand(fanout);

        ff_fanout = new Switch(HIGHEST_FF_FANOUT_CUTSET);
        ff_fanout.setShortFlag(JSAP.NO_SHORTFLAG);
        ff_fanout.setLongFlag(HIGHEST_FF_FANOUT_CUTSET);
        ff_fanout.setDefault(TRUE).setHelp("Use highest flip-flop fanout cutset option (default)");
        this.addCommand(ff_fanout);
        
        ff_fanin = new Switch(HIGHEST_FF_FANIN_CUTSET);
        ff_fanin.setShortFlag(JSAP.NO_SHORTFLAG);
        ff_fanin.setLongFlag(HIGHEST_FF_FANIN_CUTSET);
        ff_fanin.setDefault(FALSE).setHelp("Use highest flip-flop fanin input cutset option");
        this.addCommand(ff_fanin);
        
        ff_fanin_output = new Switch(HIGHEST_FF_FANIN_OUTPUT_CUTSET);
        ff_fanin_output.setShortFlag(JSAP.NO_SHORTFLAG);
        ff_fanin_output.setLongFlag(HIGHEST_FF_FANIN_OUTPUT_CUTSET);
        ff_fanin_output.setDefault(FALSE).setHelp("Use highest flip-flop fanin output cutset option");
        this.addCommand(ff_fanin_output);
        
    }

    static public boolean getFanout(JSAPResult result) {
        return result.getBoolean(HIGHEST_FANOUT_CUTSET);
    }

    static public boolean getConnectivity(JSAPResult result) {
        return result.getBoolean(CONNECTIVITY_CUTSET);
    }

    static public boolean getFFFanin(JSAPResult result) {
        return result.getBoolean(HIGHEST_FF_FANIN_CUTSET);
    }
    
    static public boolean getFFFaninOuput(JSAPResult result) {
        return result.getBoolean(HIGHEST_FF_FANIN_OUTPUT_CUTSET);
    }
    
    static public boolean getFFFanout(JSAPResult result) {
        // the extra checks are because this one defaults to true
        return (result.getBoolean(HIGHEST_FF_FANOUT_CUTSET) && !getFanout(result) && !getConnectivity(result) && !getBasicDecomposition(result) &&!getFFFanin(result) && !getAfterFFCutset(result) && !getBeforeFFCutset(result) && !getFFFaninOuput(result));
    }
    
    static public boolean getBeforeFFCutset(JSAPResult result) {
        return (result.getBoolean(BEFORE_FF_CUTSET));
    }
    
    static public boolean getAfterFFCutset(JSAPResult result) {
        return (result.getBoolean(AFTER_FF_CUTSET));
    }
    
    static public boolean getBasicDecomposition(JSAPResult result) {
        return result.getBoolean(BASIC_DECOMPOSITION);
    }

    protected Switch fanout, ff_fanout, connectivity, basic_decomposition, ff_fanin, ff_fanin_output, after_ff_cutset, before_ff_cutset;

    public static final String HIGHEST_FANOUT_CUTSET = "highest_fanout_cutset";

    public static final String HIGHEST_FF_FANOUT_CUTSET = "highest_ff_fanout_cutset";

    public static final String HIGHEST_FF_FANIN_CUTSET = "highest_ff_fanin_input_cutset";
    
    public static final String HIGHEST_FF_FANIN_OUTPUT_CUTSET = "highest_ff_fanin_output_cutset";
    
    public static final String CONNECTIVITY_CUTSET = "connectivity_cutset";
    
    public static final String BASIC_DECOMPOSITION = "basic_decomposition";
    
    public static final String AFTER_FF_CUTSET = "after_ff_cutset";
    
    public static final String BEFORE_FF_CUTSET = "before_ff_cutset";

    public static final String FALSE = "false";

    public static final String TRUE = "true";

}
