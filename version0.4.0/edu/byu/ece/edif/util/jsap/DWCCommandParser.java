/*
 * A JSAP parser for the command-line arguments sent to FlattenDWC
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

import java.util.ArrayList;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.Switch;

import edu.byu.ece.edif.tools.replicate.nmr.dwc.FlattenDWC;

/**
 * A JSAP parser for the command-line arguments sent to FlattenDWC
 * 
 * @author <a href="mailto:jcarroll@byu.net">James Carroll</a>
 * @see FlattenDWC
 * @see NMRCommandParser
 * @see JSAP
 * @see <a href="http://www.martiansoftware.com/jsap/doc/javadoc/index.html">
 * JSAP API</a>
 * @see <a href="http://www.martiansoftware.com/jsap/">JSAP Homepage</a>
 */
public class DWCCommandParser extends NMRCommandParser {

    /**
     * After the parent classes register all the Parameters, unregister the ones
     * that need to be changed for DWC.
     * 
     * @param args Command-line arguments
     */
    public DWCCommandParser() {
        /*
         * Parent constructor sets up common NMR Parameters such as input_file,
         * --output, --nmr_inports, --no_nmr_c, --SCCSortType, etc.
         */
        super(2); // DWC is a replication factor of 2
        super._MORE_INFO = this._MORE_INFO;
        super._EMAIL_ADDRESS = this._EMAIL_ADDRESS;

        _DWCParameters = new ArrayList<Parameter>();
        // Temp variables to reduce the amount of casting necessary
        Switch s;
        FlaggedOption f;

        f = (FlaggedOption) getByID(OUTPUT_FILE);
        unregisterParameter(f);
        f.setDefault("dwc.edf");
        _DWCParameters.add(f);

        s = (Switch) getByID(FULL_NMR);
        unregisterParameter(s);
        _DWCParameters.add(s.setLongFlag(FULL_DWC_FLAG));

        f = (FlaggedOption) getByID(NMR_SUFFIX);
        unregisterParameter(f);
        _parameters.remove(f);
        _DWCParameters.add(f.setLongFlag(DWC_SUFFIX_FLAG));

        s = (Switch) getByID(NMR_INPORTS);
        unregisterParameter(s);
        _DWCParameters.add(s.setLongFlag(DWC_INPORTS_FLAG));

        s = (Switch) getByID(NMR_OUTPORTS);
        unregisterParameter(s);
        _DWCParameters.add(s.setLongFlag(DWC_OUTPORTS_FLAG));

        f = (FlaggedOption) getByID(NO_NMR_P);
        unregisterParameter(f);
        _DWCParameters.add(f.setLongFlag(NO_DWC_P_FLAG));

        f = (FlaggedOption) getByID(NO_NMR_C);
        unregisterParameter(f);
        _DWCParameters.add(f.setLongFlag(NO_DWC_C_FLAG));

        f = (FlaggedOption) getByID(NO_NMR_CLK);
        unregisterParameter(f);
        _DWCParameters.add(f.setLongFlag(NO_DWC_CLK_FLAG));

        f = (FlaggedOption) getByID(NO_NMR_I);
        unregisterParameter(f);
        _DWCParameters.add(f.setLongFlag(NO_DWC_I_FLAG));

        f = (FlaggedOption) getByID(NMR_C);
        unregisterParameter(f);
        _DWCParameters.add(f.setLongFlag(DWC_C_FLAG));

        f = (FlaggedOption) getByID(NMR_CLK);
        unregisterParameter(f);
        _DWCParameters.add(f.setLongFlag(DWC_CLK_FLAG));

        f = (FlaggedOption) getByID(NMR_I);
        unregisterParameter(f);
        _DWCParameters.add(f.setLongFlag(DWC_I_FLAG));

        s = (Switch) getByID(NO_NMR_FEED_FORWARD);
        unregisterParameter(s);
        _DWCParameters.add(s.setLongFlag(NO_DWC_FEED_FORWARD_FLAG));

        s = (Switch) getByID(NO_NMR_FEEDBACK);
        unregisterParameter(s);
        _DWCParameters.add(s.setLongFlag(NO_DWC_FEEDBACK_FLAG));

        s = (Switch) getByID(NO_NMR_FEEDBACK_OUTPUT);
        unregisterParameter(s);
        _DWCParameters.add(s.setLongFlag(NO_DWC_FEEDBACK_OUTPUT_FLAG));

        s = (Switch) getByID(NO_NMR_INPUT_TO_FEEDBACK);
        unregisterParameter(s);
        _DWCParameters.add(s.setLongFlag(NO_DWC_INPUT_TO_FEEDBACK_FLAG));

        _DWCParameters
                .add(new Switch(USE_DRC)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("use_drc")
                        .setDefault(FALSE)
                        .setHelp(
                                "Use a dual rail checker for comparison of the duplicated circuit; Default is a single rail checker."));

        _DWCParameters
                .add(new Switch(PERSISTENCE)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag(PERSISTENCE)
                        .setDefault(FALSE)
                        .setHelp(
                                "Add comparators checking persistent sections of the design. These comparators will output to a separate persistent error line (or a dual rail error line, depending on the --use_drc option.)"));

        _DWCParameters.add(new Switch(REGISTER_DETECTION).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag(
                REGISTER_DETECTION).setDefault(FALSE).setHelp(
                "Register the detection signals before they go to the outputs."));

        _DWCParameters.add(new Switch(NO_OBUFS).setLongFlag(NO_OBUFS).setDefault("false").setHelp(
                "Disable insertion of output buffers on error detection signals."));

        /*
         * Register the parameters. For more information, see the JSAP API.
         */
        for (Parameter p : _DWCParameters) {
            try {
                this.registerParameter(p);
            } catch (JSAPException e) {
                System.err.println("Error while registering parameter " + p + ":\n" + e);
            }
        }

    }

    // TODO: update this class so that the usage information prints out correctly

    /**
     * An ArrayList of Parameters unique to DWC
     */
    protected static ArrayList<Parameter> _DWCParameters;

    protected String _MORE_INFO = "[documentation pending...]";

    protected String _EMAIL_ADDRESS = "<dlm44@nm.byu.edu> or <jcarroll@byu.net>";

    /*
     * The following exist so that FlattenDWC can refer to these strings as
     * "fooDWC" instead of "fooNMR".
     */

    public static final String DWC_C = NMR_C;

    public static final String DWC_C_FLAG = "dwc_c";

    public static final String DWC_CLK = NMR_CLK;

    public static final String DWC_CLK_FLAG = "dwc_clk";

    public static final String DWC_I = NMR_I;

    public static final String DWC_INPORTS = NMR_INPORTS;

    public static final String DWC_INPORTS_FLAG = "dwc_inports";

    public static final String DWC_I_FLAG = "dwc_i";

    public static final String DWC_OUTPORTS = NMR_OUTPORTS;

    public static final String DWC_OUTPORTS_FLAG = "dwc_outports";

    public static final String FULL_DWC = FULL_NMR;

    public static final String FULL_DWC_FLAG = "full_dwc";

    public static final String DWC_SUFFIX = NMR_SUFFIX;

    public static final String DWC_SUFFIX_FLAG = "dwcSuffix";

    public static final String NO_DWC_C = NO_NMR_C;

    public static final String NO_DWC_C_FLAG = "no_dwc_c";

    public static final String NO_DWC_CLK = NO_NMR_CLK;

    public static final String NO_DWC_CLK_FLAG = "no_dwc_clk";

    public static final String NO_DWC_FEEDBACK = NO_NMR_FEEDBACK;

    public static final String NO_DWC_FEEDBACK_FLAG = "nodwcFeedback";

    public static final String NO_DWC_FEEDBACK_OUTPUT = NO_NMR_FEEDBACK_OUTPUT;

    public static final String NO_DWC_FEEDBACK_OUTPUT_FLAG = "nodwcFeedbackOutput";

    public static final String NO_DWC_FEED_FORWARD = NO_NMR_FEED_FORWARD;

    public static final String NO_DWC_FEED_FORWARD_FLAG = "nodwcFeedForward";

    public static final String NO_DWC_I = NO_NMR_I;

    public static final String NO_DWC_INPUT_TO_FEEDBACK = NO_NMR_INPUT_TO_FEEDBACK;

    public static final String NO_DWC_INPUT_TO_FEEDBACK_FLAG = "nodwcInputToFeedback";

    public static final String NO_DWC_I_FLAG = "no_dwc_i";

    public static final String NO_DWC_P = NO_NMR_P;

    public static final String NO_DWC_P_FLAG = "no_dwc_p";

    public static final String USE_DRC = "use_drc";

    public static final String PERSISTENCE = "persistent_comparators";

    public static final String REGISTER_DETECTION = "register_detection";

}
