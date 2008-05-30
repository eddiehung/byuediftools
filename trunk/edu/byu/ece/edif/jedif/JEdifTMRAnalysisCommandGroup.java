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

import java.util.Arrays;
import java.util.Collection;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.stringparsers.DoubleStringParser;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;

import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.tools.replicate.nmr.NMRUtilities;
import edu.byu.ece.edif.tools.replicate.nmr.NMRUtilities.UtilizationFactor;
import edu.byu.ece.edif.util.jsap.AbstractCommandGroup;
import edu.byu.ece.edif.util.jsap.BoundedDoubleStringParser;
import edu.byu.ece.edif.util.jsap.BoundedIntegerStringParser;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

public class JEdifTMRAnalysisCommandGroup extends AbstractCommandGroup {

    /**
     * TODO: Some of these options should be in more general command groups that
     * are usable across multiple tools. We need to break these up and put them
     * in a structure that makes sense.
     */
    public JEdifTMRAnalysisCommandGroup() {
        //super();
        // IOB Output filename flag
        FlaggedOption iob_output_file = new FlaggedOption(IOB_OUTPUT_FILE);
        iob_output_file.setStringParser(JSAP.STRING_PARSER);
        iob_output_file.setRequired(JSAP.NOT_REQUIRED);
        iob_output_file.addDefault("BL-TMR.iob");
        iob_output_file.setShortFlag(JSAP.NO_SHORTFLAG);
        iob_output_file.setLongFlag(IOB_OUTPUT_FILE);
        iob_output_file.setUsageName("output_file");
        iob_output_file.setHelp("Filename and path to the IOB analysis file that will be created. Required.");
        this.addCommand(iob_output_file);

        /*
         * Partial TMR options
         */
        Switch full_tmr = new Switch(FULL_TMR);
        full_tmr.setShortFlag(JSAP.NO_SHORTFLAG);
        full_tmr.setLongFlag(FULL_TMR);
        full_tmr.setDefault(FALSE);
        full_tmr.setHelp("Fully triplicate the design, skipping all partial TMR analysis.");
        this.addCommand(full_tmr);

        FlaggedOption tmr_p = new FlaggedOption(TMR_PORTS);
        tmr_p.setStringParser(JSAP.STRING_PARSER);
        tmr_p.setRequired(JSAP.NOT_REQUIRED);
        tmr_p.setShortFlag(JSAP.NO_SHORTFLAG);
        tmr_p.setLongFlag(TMR_PORTS);
        tmr_p.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        tmr_p.setList(JSAP.LIST);
        tmr_p.setListSeparator(LIST_DELIMITER);
        tmr_p.setUsageName("Port name");
        tmr_p.setHelp("Comma-separated list of ports to be triplicated.");
        this.addCommand(tmr_p);

        Switch tmr_inports = new Switch(TMR_INPORTS);
        tmr_inports.setShortFlag(JSAP.NO_SHORTFLAG);
        tmr_inports.setLongFlag(TMR_INPORTS);
        tmr_inports.setDefault(FALSE);
        tmr_inports.setHelp("Triplicate top-level input ports.");
        this.addCommand(tmr_inports);

        Switch tmr_outports = new Switch(TMR_OUTPORTS);
        tmr_outports.setShortFlag(JSAP.NO_SHORTFLAG);
        tmr_outports.setLongFlag(TMR_OUTPORTS);
        tmr_outports.setDefault(FALSE);
        tmr_outports.setHelp("Triplicate top-level output ports.");
        this.addCommand(tmr_outports);

        FlaggedOption no_tmr_p = new FlaggedOption(NO_TMR_P);
        no_tmr_p.setStringParser(JSAP.STRING_PARSER);
        no_tmr_p.setRequired(JSAP.NOT_REQUIRED);
        no_tmr_p.setShortFlag(JSAP.NO_SHORTFLAG);
        no_tmr_p.setLongFlag(NO_TMR_P);
        no_tmr_p.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        no_tmr_p.setList(JSAP.LIST);
        no_tmr_p.setListSeparator(LIST_DELIMITER);
        no_tmr_p.setUsageName("port");
        no_tmr_p
                .setHelp("Comma-separated list of top-level ports that should *not* be triplicated. Used with --tmr_inports and --tmr_outports");
        this.addCommand(no_tmr_p);

        FlaggedOption tmr_c = new FlaggedOption(TMR_C);
        tmr_c.setStringParser(JSAP.STRING_PARSER);
        tmr_c.setRequired(JSAP.NOT_REQUIRED);
        tmr_c.setShortFlag(JSAP.NO_SHORTFLAG);
        tmr_c.setLongFlag(TMR_C);
        tmr_c.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        tmr_c.setList(JSAP.LIST);
        tmr_c.setListSeparator(LIST_DELIMITER);
        tmr_c.setUsageName("cell_type");
        tmr_c.setHelp("Comma-separated list of cell types to be triplicated.");
        this.addCommand(tmr_c);

        FlaggedOption tmr_clk = new FlaggedOption(TMR_CLK);
        tmr_clk.setStringParser(JSAP.STRING_PARSER);
        tmr_clk.setRequired(JSAP.NOT_REQUIRED);
        tmr_clk.setShortFlag(JSAP.NO_SHORTFLAG);
        tmr_clk.setLongFlag(TMR_CLK);
        tmr_clk.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        tmr_clk.setList(JSAP.LIST);
        tmr_clk.setListSeparator(LIST_DELIMITER);
        tmr_clk.setUsageName("clock_domain");
        tmr_clk.setHelp("Comma-separated list of clock domains to be triplicated.");
        this.addCommand(tmr_clk);

        FlaggedOption tmr_i = new FlaggedOption(TMR_I);
        tmr_i.setStringParser(JSAP.STRING_PARSER);
        tmr_i.setRequired(JSAP.NOT_REQUIRED);
        tmr_i.setShortFlag(JSAP.NO_SHORTFLAG);
        tmr_i.setLongFlag(TMR_I);
        tmr_i.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        tmr_i.setList(JSAP.LIST);
        tmr_i.setListSeparator(LIST_DELIMITER);
        tmr_i.setUsageName("cell_instance");
        tmr_i.setHelp("Comma-separated list of cell instances to be triplicated.");
        this.addCommand(tmr_i);

        FlaggedOption no_tmr_c = new FlaggedOption(NO_TMR_C);
        no_tmr_c.setStringParser(JSAP.STRING_PARSER);
        no_tmr_c.setRequired(JSAP.NOT_REQUIRED);
        no_tmr_c.setShortFlag(JSAP.NO_SHORTFLAG);
        no_tmr_c.setLongFlag(NO_TMR_C);
        no_tmr_c.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        no_tmr_c.setList(JSAP.LIST);
        no_tmr_c.setListSeparator(LIST_DELIMITER);
        no_tmr_c.setUsageName("cell_type");
        no_tmr_c.setHelp("Comma-separated list of cell types that should *not* be triplicated.");
        this.addCommand(no_tmr_c);

        FlaggedOption no_tmr_clk = new FlaggedOption(NO_TMR_CLK);
        no_tmr_clk.setStringParser(JSAP.STRING_PARSER);
        no_tmr_clk.setRequired(JSAP.NOT_REQUIRED);
        no_tmr_clk.setShortFlag(JSAP.NO_SHORTFLAG);
        no_tmr_clk.setLongFlag(NO_TMR_CLK);
        no_tmr_clk.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        no_tmr_clk.setList(JSAP.LIST);
        no_tmr_clk.setListSeparator(LIST_DELIMITER);
        no_tmr_clk.setUsageName("clock_domain");
        no_tmr_clk.setHelp("Comma-separated list of clock domains that should *not* be triplicated.");
        this.addCommand(no_tmr_clk);

        FlaggedOption no_tmr_i = new FlaggedOption(NO_TMR_I);
        no_tmr_i.setStringParser(JSAP.STRING_PARSER);
        no_tmr_i.setRequired(JSAP.NOT_REQUIRED);
        no_tmr_i.setShortFlag(JSAP.NO_SHORTFLAG);
        no_tmr_i.setLongFlag(NO_TMR_I);
        no_tmr_i.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        no_tmr_i.setList(JSAP.LIST);
        no_tmr_i.setListSeparator(LIST_DELIMITER);
        no_tmr_i.setUsageName("cell_instance");
        no_tmr_i.setHelp("Comma-separated list of cell instances that should *not* be triplicated.");
        this.addCommand(no_tmr_i);

        Switch no_tmr_feedback = new Switch(NO_TMR_FEEDBACK);
        no_tmr_feedback.setShortFlag(JSAP.NO_SHORTFLAG);
        no_tmr_feedback.setLongFlag(NO_TMR_FEEDBACK);
        no_tmr_feedback.setDefault(FALSE);
        no_tmr_feedback.setHelp("Skip triplication of the feedback sections of the input design. *Not* recommended.");
        this.addCommand(no_tmr_feedback);

        Switch no_tmr_itf = new Switch(NO_TMR_INPUT_TO_FEEDBACK);
        no_tmr_itf.setShortFlag(JSAP.NO_SHORTFLAG);
        no_tmr_itf.setLongFlag(NO_TMR_INPUT_TO_FEEDBACK);
        no_tmr_itf.setDefault(FALSE);
        no_tmr_itf
                .setHelp("Skip triplication of the portions of the design that \"feed into\" the feedback sections of the design.");
        this.addCommand(no_tmr_itf);

        Switch no_tmr_fo = new Switch(NO_TMR_FEEDBACK_OUTPUT);
        no_tmr_fo.setShortFlag(JSAP.NO_SHORTFLAG);
        no_tmr_fo.setLongFlag(NO_TMR_FEEDBACK_OUTPUT);
        no_tmr_fo.setDefault(FALSE);
        no_tmr_fo
                .setHelp("Skip triplication of the portions of the input design which are driven by the feedback sections of the design.");
        this.addCommand(no_tmr_fo);

        Switch no_tmr_ff = new Switch(NO_TMR_FEED_FORWARD);
        no_tmr_ff.setShortFlag(JSAP.NO_SHORTFLAG);
        no_tmr_ff.setLongFlag(NO_TMR_FEED_FORWARD);
        no_tmr_ff.setDefault(FALSE);
        no_tmr_ff
                .setHelp("Skip triplication of the portions of the input design which are not related to feedback sections of the design.");
        this.addCommand(no_tmr_ff);

        Switch no_iob_fb = new Switch(NO_IOB_FB);
        no_iob_fb.setShortFlag(JSAP.NO_SHORTFLAG);
        no_iob_fb.setLongFlag(NO_IOB_FB);
        no_iob_fb.setDefault(FALSE);
        no_iob_fb
                .setHelp("The user may wish to exclude the IOBs (specifically inout ports) from feedback analysis if there is no true feedback (by design). This may greatly reduce the amount of feedback detected.");
        this.addCommand(no_iob_fb);

        /*
         * SCC Options
         */
        FlaggedOption scc_sort_type = new FlaggedOption(SCC_SORT_TYPE);
        scc_sort_type.setStringParser(_oneTwoThree);
        scc_sort_type.setRequired(JSAP.NOT_REQUIRED);
        scc_sort_type.setShortFlag(JSAP.NO_SHORTFLAG);
        scc_sort_type.setLongFlag(SCC_SORT_TYPE);
        scc_sort_type.setUsageName("{1|2|3}");
        scc_sort_type.setDefault("3");
        scc_sort_type
                .setHelp("Choose the method the BL-TMR tool uses to triplicate logic in the \"feedback\" section of the design. Option 1 chooses the largest SCCs first. Option 2 chooses the smallest first. Option 3 adds the SCCs in topological order.");
        this.addCommand(scc_sort_type);

        Switch doSCCDecomp = new Switch(DO_SCC_DECOMPOSITION);
        doSCCDecomp.setShortFlag(JSAP.NO_SHORTFLAG);
        doSCCDecomp.setLongFlag(DO_SCC_DECOMPOSITION);
        doSCCDecomp.setDefault(FALSE);
        doSCCDecomp.setHelp("Allow SCCs to be partially triplicated.");
        this.addCommand(doSCCDecomp);

        FlaggedOption i_a_t = new FlaggedOption(INPUT_ADDITION_TYPE);
        i_a_t.setStringParser(_oneTwoThree);
        i_a_t.setRequired(JSAP.NOT_REQUIRED);
        i_a_t.setShortFlag(JSAP.NO_SHORTFLAG);
        i_a_t.setLongFlag(INPUT_ADDITION_TYPE);
        i_a_t.setUsageName("{1|2|3}");
        i_a_t.setDefault("3");
        i_a_t
                .setHelp("Select between three different algorithms to partially triplicate logic in the \"input to feedback\" section of the design. Option 1 uses a depth-first search starting from the inputs to the feedback section. Option 3 uses a breadth-first search. Option 2 uses a combination of the two.");
        this.addCommand(i_a_t);

        FlaggedOption o_a_t = new FlaggedOption(OUTPUT_ADDITION_TYPE);
        o_a_t.setStringParser(_oneTwoThree);
        o_a_t.setRequired(JSAP.NOT_REQUIRED);
        o_a_t.setShortFlag(JSAP.NO_SHORTFLAG);
        o_a_t.setLongFlag(OUTPUT_ADDITION_TYPE);
        o_a_t.setUsageName("{1|2|3}");
        o_a_t.setDefault("3");
        o_a_t
                .setHelp("This option is similar to the `--inputAdditionType' option except it applies to the logic driven by the feedback section of the design.");
        this.addCommand(o_a_t);

        /*
         * Merge Factor, Optimization Factor, and Utilization Factors
         */
        FlaggedOption merge_factor = new FlaggedOption(MERGE_FACTOR);
        merge_factor.setStringParser(_zeroToOneInclusive);
        merge_factor.setShortFlag(JSAP.NO_SHORTFLAG);
        merge_factor.setLongFlag(MERGE_FACTOR);
        merge_factor.setDefault("0.5");
        merge_factor.setHelp("The assumed percentage of LUTs and flip-flops that will share the same \"slice\".");
        this.addCommand(merge_factor);

        FlaggedOption o_factor = new FlaggedOption(OPTIMIZATION_FACTOR);
        o_factor.setStringParser(_zeroToOneInclusive);
        o_factor.setShortFlag(JSAP.NO_SHORTFLAG);
        o_factor.setLongFlag(OPTIMIZATION_FACTOR);
        o_factor.setDefault("0.95");
        o_factor
                .setHelp("The assumed percentage of the resulting design size after logic optimization techniques compared with the design size prior to optimization.");
        this.addCommand(o_factor);

        FlaggedOption factor_type = new FlaggedOption(FACTOR_TYPE);
        factor_type.setStringParser(EnumeratedStringParser.getParser(FACTOR_TYPES, false));
        factor_type.setRequired(JSAP.NOT_REQUIRED);
        factor_type.setShortFlag(JSAP.NO_SHORTFLAG);
        factor_type.setLongFlag(FACTOR_TYPE);
        factor_type.setDefault(NMRUtilities.DEFAULT_UTILIZATION_FACTOR.toString());
        factor_type.setUsageName("{" + FACTOR_TYPES.replace(';', '|') + "}");
        factor_type.setHelp("Utilization Factor type. Must be one of the following: " + FACTOR_TYPES.replace(";", ", ")
                + ". See `factor_value' for more information about each factor type.");
        this.addCommand(factor_type);

        FlaggedOption factor_value = new FlaggedOption(FACTOR_VALUE);
        factor_value.setStringParser(DoubleStringParser.getParser());
        factor_value.setRequired(JSAP.NOT_REQUIRED);
        factor_value.setLongFlag(FACTOR_VALUE);
        factor_value.setDefault(NMRUtilities.DEFAULT_FACTOR_VALUE);
        factor_value
                .setHelp("This value is interpreted differently depending on the `factor_type' chosen. \nFor DUF (Desired Utilization Factor): specifies the maximum percentage of the target chip to be utilized after performing Partial TMR; must be greater than or equal to 0.0. \nFor UEF (Utilization Expansion Factor): specifies the maximum increase in utilization of the target part, expressed as a percentage of the utilization of the original (non-TMR'd) design; must be greater than or equal to 0.0. \nFor ASUF (Available Space Utilization Factor): specifies the maximum utilization of the target part, expressed as a percentage of the unused space on the part after the original (non-TMR'd) design has been considered; must be greater than 0.0 and less than or equal to 1.0.\n");
        this.addCommand(factor_value);

        Switch ignore_hard_resource_utilization = new Switch(IGNORE_HARD_UTILIZATION_LIMITS);
        ignore_hard_resource_utilization.setLongFlag(IGNORE_HARD_UTILIZATION_LIMITS);
        ignore_hard_resource_utilization
                .setHelp("This option makes the BL-TMR tool ignore all hard resource utilization limits when "
                        + "triplicating the design. ");
        this.addCommand(ignore_hard_resource_utilization);

        Switch ignore_soft_logic_utilization = new Switch(IGNORE_SOFT_UTILIZATION_LIMIT);
        ignore_soft_logic_utilization.setLongFlag(IGNORE_SOFT_UTILIZATION_LIMIT);
        ignore_soft_logic_utilization.setHelp("This option makes the BL-TMR tool ignore logic block utilization when "
                + "triplicating the design. Hard resources such as BRAMs and CLKDLLs " + "will still be tracked. ");
        this.addCommand(ignore_soft_logic_utilization);

        /*
         * Target Technology and Part Options
         */

        Switch bad_cut_conn = new Switch(USE_BAD_CUT_CONN);
        bad_cut_conn.setShortFlag(JSAP.NO_SHORTFLAG);
        bad_cut_conn.setLongFlag("use_bad_cut_conn");
        bad_cut_conn.setDefault(FALSE);
        bad_cut_conn.setHelp("Use bad cut connections option.");
        this.addCommand(bad_cut_conn);

    }

    public static String getIOBName(JSAPResult result) {
        String filename = result.getString(IOB_OUTPUT_FILE);
        if (!filename.contains(".")) {
            filename = filename.concat(".iob");
        }
        return filename;
    }

    public static double getMergeFactor(JSAPResult result) {
        return result.getDouble(MERGE_FACTOR);
    }

    public static double getOptimizationFactor(JSAPResult result) {
        return result.getDouble(OPTIMIZATION_FACTOR);
    }

    public static double getFactorValue(JSAPResult result) {
        return result.getDouble(FACTOR_VALUE);
    }

    public static String[] getNoTMRp(JSAPResult result) {
        return result.getStringArray(NO_TMR_P);
    }

    public static boolean tmrInports(JSAPResult result) {
        return result.getBoolean(TMR_INPORTS);
    }

    public static boolean tmrOutports(JSAPResult result) {
        return result.getBoolean(TMR_OUTPORTS);
    }

    public static Collection<String> tmrPorts(JSAPResult result) {
        return Arrays.asList(result.getStringArray(TMR_PORTS));
    }

    public static boolean noTMRi(JSAPResult result) {
        return result.contains(NO_TMR_I);
    }

    public static String[] getNoTMRi(JSAPResult result) {
        return result.getStringArray(NO_TMR_I);
    }

    public static boolean noTMRc(JSAPResult result) {
        return result.contains(NO_TMR_C);
    }

    public static String[] getNoTMRc(JSAPResult result) {
        return result.getStringArray(NO_TMR_C);
    }

    public static boolean noTMRclk(JSAPResult result) {
        return result.contains(NO_TMR_CLK);
    }

    public static String[] getNoTMRclk(JSAPResult result) {
        return result.getStringArray(NO_TMR_CLK);
    }

    public static boolean tmrI(JSAPResult result) {
        return result.contains(TMR_I);
    }

    public static String[] getTMRi(JSAPResult result) {
        return result.getStringArray(TMR_I);
    }

    public static boolean tmrC(JSAPResult result) {
        return result.contains(TMR_C);
    }

    public static String[] getTMRc(JSAPResult result) {
        return result.getStringArray(TMR_C);
    }

    public static boolean tmrClk(JSAPResult result) {
        return result.contains(TMR_CLK);
    }

    public static String[] getTMRclk(JSAPResult result) {
        return result.getStringArray(TMR_CLK);
    }

    public static boolean badCutConn(JSAPResult result) {
        return result.getBoolean(USE_BAD_CUT_CONN);
    }

    public static boolean fullTMR(JSAPResult result) {
        return result.getBoolean(FULL_TMR);
    }

    public static boolean noTMRFeedback(JSAPResult result) {
        return result.getBoolean(NO_TMR_FEEDBACK);
    }

    public static boolean doSCCDecomposition(JSAPResult result) {
        return result.getBoolean(DO_SCC_DECOMPOSITION);
    }

    public static int getSCCSortType(JSAPResult result) {
        return result.getInt(SCC_SORT_TYPE);
    }

    public static boolean noTMRinputToFeedback(JSAPResult result) {
        return result.getBoolean(NO_TMR_INPUT_TO_FEEDBACK);
    }

    public static boolean noTMRfeedForward(JSAPResult result) {
        return result.getBoolean(NO_TMR_FEED_FORWARD);
    }

    public static boolean noIOBFB(JSAPResult result) {
        return result.getBoolean(NO_IOB_FB);
    }

    public static boolean noTMRfeedbackOutput(JSAPResult result) {
        return result.getBoolean(NO_TMR_FEEDBACK_OUTPUT);
    }

    public static boolean ignoreHardResourceUtilizationLimits(JSAPResult result) {
        return result.getBoolean(IGNORE_HARD_UTILIZATION_LIMITS);
    }

    public static boolean ignoreSoftLogicUtilizationLimit(JSAPResult result) {
        return result.getBoolean(IGNORE_SOFT_UTILIZATION_LIMIT);
    }

    public static int getInputAdditionType(JSAPResult result) {
        return result.getInt(INPUT_ADDITION_TYPE);
    }

    public static int getOutputAdditionType(JSAPResult result) {
        return result.getInt(OUTPUT_ADDITION_TYPE);
    }

    public static String getIOBOutputFilename(JSAPResult result) {
        return result.getString(IOB_OUTPUT_FILE);
    }

    /**
     * Determine what utilization factor type is being used for partial TMR.
     * 
     * @see NMRUtilities.UtilizationFactor
     * @return A UtilizationFactor (enum) object representing the factor type
     * being used.
     */
    public static UtilizationFactor getFactorType(JSAPResult result) {
        /*
         * Attempt to ascertain that the command parser has already parsed the
         * command-line arguments.
         */
        if (result == null)
            throw new EdifRuntimeException(
                    "No JSAPResult object exists for this command parser.  Make sure that you call NMRCommandParser.parse() after creating the command parser.");

        NMRUtilities.UtilizationFactor type;
        String s = result.getString(FACTOR_TYPE);

        if (s.compareToIgnoreCase(UtilizationFactor.ASUF.toString()) == 0)
            type = UtilizationFactor.ASUF;
        else if (s.compareToIgnoreCase(UtilizationFactor.UEF.toString()) == 0)
            type = UtilizationFactor.UEF;
        else if (s.compareToIgnoreCase(UtilizationFactor.DUF.toString()) == 0)
            type = UtilizationFactor.DUF;
        else if (s.compareToIgnoreCase(UtilizationFactor.CF.toString()) == 0)
            type = UtilizationFactor.CF;
        else
            // Should never get here.
            type = NMRUtilities.DEFAULT_UTILIZATION_FACTOR;
        return type;
    }

    // eventually will be implemented to get an SCCDFS from a file
    public static SCCDepthFirstSearch getSCCDFS(JSAPResult result) {
        return null;
    }

    /**
     * Set := {1,2,3} Use for
     * {@linkplain #INPUT_ADDITION_TYPE inputAdditionType},
     * {@linkplain #OUTPUT_ADDITION_TYPE outputAdditionType}, and
     * {@linkplain #SCC_SORT_TYPE SCCSortType}.
     */
    protected static BoundedIntegerStringParser _oneTwoThree = BoundedIntegerStringParser.getParser(1, 3);

    /**
     * Range := [0,1] Use for {@linkplain #MERGE_FACTOR mergeFactor},
     * {@linkplain #OPTIMIZATION_FACTOR optimizationFactor}, and
     * {@linkplain #INC inc}.
     */
    protected static BoundedDoubleStringParser _zeroToOneInclusive = BoundedDoubleStringParser.getParser(0.0, 1.0);

    /**
     * A String of the various Factor Types for an EnumeratedStringParser;
     * initialized with a static initialization block.
     */
    protected static String FACTOR_TYPES = null;
    static {
        StringBuffer sb = new StringBuffer();
        for (UtilizationFactor type : NMRUtilities.UtilizationFactor.values()) {
            if (sb.length() > 0) {
                sb.append(";");
            }
            sb.append(type.toString());
        }
        FACTOR_TYPES = sb.toString();
    }

    public static final String DO_SCC_DECOMPOSITION = "do_scc_decomposition";

    public static final String FACTOR_TYPE = "factor_type";

    public static final String FACTOR_VALUE = "factor_value";

    public static final String IGNORE_HARD_UTILIZATION_LIMITS = "ignore_hard_resource_utilization_limits";

    public static final String IGNORE_SOFT_UTILIZATION_LIMIT = "ignore_soft_logic_utilization_limit";

    public static final String FILE = "file";

    public static final String FULL_TMR = "full_tmr";

    public static final String HIGH = "high";

    public static final String INC = "inc";

    public static final String INPUT_ADDITION_TYPE = "input_addition_type";

    public static final String IOB_OUTPUT_FILE = "iob_output";

    public static final String LOG = "logfile";

    public static final String MERGE_FACTOR = "merge_factor";

    /**
     * No TMR Cell type
     */
    public static final String NO_TMR_C = "no_tmr_c";

    public static final String NO_TMR_FEEDBACK = "no_tmr_feedback";

    public static final String NO_TMR_FEEDBACK_OUTPUT = "no_tmr_feedback_output";

    public static final String NO_TMR_FEED_FORWARD = "no_tmr_feed_forward";

    /**
     * No TMR cell instance
     */
    public static final String NO_TMR_I = "no_tmr_i";

    /**
     * No TMR clock domain
     */
    public static final String NO_TMR_CLK = "no_tmr_clk";

    public static final String NO_TMR_INPUT_TO_FEEDBACK = "no_tmr_input_to_feedback";

    /**
     * No TMR port
     */
    public static final String NO_TMR_P = "no_tmr_p";

    public static final String OPTIMIZATION_FACTOR = "optimization_factor";

    public static final String OUTPUT_ADDITION_TYPE = "output_addition_type";

    public static final String SCC_SORT_TYPE = "scc_sort_type";

    /**
     * Force TMR of cell type
     */
    public static final String TMR_C = "tmr_c";

    /**
     * Force TMR of cell instance
     */
    public static final String TMR_I = "tmr_i";

    /**
     * Force TMR of clock domain
     */
    public static final String TMR_CLK = "tmr_clk";

    /**
     * Remove IOBs from feedback (Don't count as feedback)
     */
    public static final String NO_IOB_FB = "no_iob_feedback";

    public static final String TMR_INPORTS = "tmr_inports";

    public static final String TMR_OUTPORTS = "tmr_outports";

    public static final String TMR_PORTS = "tmr_p";

    public static final String USE_BAD_CUT_CONN = "use_bad_cut_conn";

    public static final String TRUE = "true";

    public static final String FALSE = "false";

    public static final char LIST_DELIMITER = ',';

}