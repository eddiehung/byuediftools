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
package edu.byu.ece.edif.jedif;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import edu.byu.ece.edif.util.jsap.AbstractCommandGroup;
import edu.byu.ece.edif.util.jsap.BoundedIntegerStringParser;

public class JEdifSterilizeCommandGroup extends AbstractCommandGroup {

    public JEdifSterilizeCommandGroup() {

        _remove_hl_option = new Switch(REMOVE_HL);
        _remove_hl_option.setShortFlag(JSAP.NO_SHORTFLAG);
        _remove_hl_option.setLongFlag(REMOVE_HL);
        _remove_hl_option.setDefault(FALSE);
        _remove_hl_option.setHelp("Remove half-latches in the input design before performing NMR. \nNOTE: Not"
                + " *all* half-latches can be removed at the EDIF level for all architectures. Some"
                + " post-processing may be necessary.");
        this.addCommand(_remove_hl_option);

        _hl_constant_option = new FlaggedOption(HL_CONSTANT);
        _hl_constant_option.setStringParser(_zeroOne);
        _hl_constant_option.setShortFlag(JSAP.NO_SHORTFLAG);
        _hl_constant_option.setLongFlag(HL_CONSTANT);
        _hl_constant_option.setDefault("0");
        _hl_constant_option.setUsageName("{0|1}");
        _hl_constant_option.setHelp("The polarity of the half-latch constant to be used. Valid options are"
                + " '0' and '1'.");
        this.addCommand(_hl_constant_option);

        _hl_port_name_option = new FlaggedOption(HL_PORT_NAME);
        _hl_port_name_option.setStringParser(JSAP.STRING_PARSER);
        _hl_port_name_option.setRequired(JSAP.NOT_REQUIRED);
        _hl_port_name_option.setShortFlag(JSAP.NO_SHORTFLAG);
        _hl_port_name_option.setLongFlag(HL_USE_PORT);
        _hl_port_name_option.setDefault(JSAP.NO_DEFAULT);
        _hl_port_name_option.setHelp("Use the specified top-level port as the safe half-latch constant"
                + " when using half-latch removal.");
        this.addCommand(_hl_port_name_option);

        _remove_fmaps_option = new Switch(REMOVE_FMAPS);
        _remove_fmaps_option.setShortFlag(JSAP.NO_SHORTFLAG);
        _remove_fmaps_option.setLongFlag(REMOVE_FMAPS);
        _remove_fmaps_option.setDefault(FALSE);
        _remove_fmaps_option.setHelp("Remove FMAPS in the input design before performing NMR. \nNOTE: Not"
                + " *all* FMAPS can be removed at the EDIF level for all architectures. Some"
                + " post-processing may be necessary.");
        this.addCommand(_remove_fmaps_option);
        
        _replace_lut_option = new Switch(REPLACE_LUTS);
        _replace_lut_option.setShortFlag(JSAP.NO_SHORTFLAG);
        _replace_lut_option.setLongFlag(REPLACE_LUTS);
        _replace_lut_option.setDefault(FALSE);
        _replace_lut_option.setHelp("Replace all the SRLs and RAMs instantiatied by LUTs with actual"
                + " flip-flops");
        this.addCommand(_replace_lut_option);
    }
    
    /**
     * Set := {0,1} Use for {@linkplain #HL_CONSTANT hlConst}.
     */
    protected static BoundedIntegerStringParser _zeroOne = BoundedIntegerStringParser.getParser(0, 1);

    public static boolean getRemoveHL(JSAPResult result) {
        return result.getBoolean(REMOVE_HL);
    }

    public static boolean getRemoveFMaps(JSAPResult result) {
        return result.getBoolean(REMOVE_FMAPS);
    }
    
    public static boolean getReplaceLuts(JSAPResult result) {
        return result.getBoolean(REPLACE_LUTS);
    }

    public static int getHLConstant(JSAPResult result) {
        return result.getInt(HL_CONSTANT);
    }

    public static String getHLPortName(JSAPResult result) {
        return result.getString(JEdifSterilizeCommandGroup.HL_PORT_NAME);
    }

    protected Switch _remove_hl_option;

    protected FlaggedOption _hl_constant_option;

    protected FlaggedOption _hl_port_name_option;

    protected Switch _remove_fmaps_option;
    
    protected Switch _replace_lut_option;

    public static final String REMOVE_HL = "remove_hl";

    public static final String HL_CONSTANT = "hl_constant";

    public static final String HL_PORT_NAME = "hl_port_name";

    public static final String HL_USE_PORT = "hl_use_port";

    public static final String REMOVE_FMAPS = "remove_fmaps";
    
    public static final String REPLACE_LUTS = "replace_luts";

    public static final String FALSE = "false";

    public static final String TRUE = "true";

}