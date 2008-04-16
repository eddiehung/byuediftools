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

public class JEdifDWCAnalysisCommandGroup extends AbstractCommandGroup {

    public JEdifDWCAnalysisCommandGroup() {
        //super();
        // IOB Output filename flag
        FlaggedOption iob_output_file = new FlaggedOption(IOB_OUTPUT_FILE);
        iob_output_file.setStringParser(JSAP.STRING_PARSER);
        iob_output_file.setRequired(JSAP.NOT_REQUIRED);
        iob_output_file.addDefault("BLDwc.iob");
        iob_output_file.setShortFlag(JSAP.NO_SHORTFLAG);
        iob_output_file.setLongFlag(IOB_OUTPUT_FILE);
        iob_output_file.setUsageName("output_file");
        iob_output_file.setHelp("Filename and path to the IOB analysis file that will be created. Required.");
        this.addCommand(iob_output_file);

        /*
         * Partial DWC options
         */
        Switch full_dwc = new Switch(FULL_DWC);
        full_dwc.setShortFlag(JSAP.NO_SHORTFLAG);
        full_dwc.setLongFlag(FULL_DWC);
        full_dwc.setDefault(FALSE);
        full_dwc.setHelp("Fully duplicate the design, skipping all partial DWC analysis.");
        this.addCommand(full_dwc);

        FlaggedOption dwc_p = new FlaggedOption(DWC_PORTS);
        dwc_p.setStringParser(JSAP.STRING_PARSER);
        dwc_p.setRequired(JSAP.NOT_REQUIRED);
        dwc_p.setShortFlag(JSAP.NO_SHORTFLAG);
        dwc_p.setLongFlag(DWC_PORTS);
        dwc_p.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        dwc_p.setList(JSAP.LIST);
        dwc_p.setListSeparator(LIST_DELIMITER);
        dwc_p.setUsageName("Port name");
        dwc_p.setHelp("Comma-separated list of ports to be duplicated.");
        this.addCommand(dwc_p);

        Switch dwc_inports = new Switch(DWC_INPORTS);
        dwc_inports.setShortFlag(JSAP.NO_SHORTFLAG);
        dwc_inports.setLongFlag(DWC_INPORTS);
        dwc_inports.setDefault(FALSE);
        dwc_inports.setHelp("Duplicate top-level input ports.");
        this.addCommand(dwc_inports);

        Switch dwc_outports = new Switch(DWC_OUTPORTS);
        dwc_outports.setShortFlag(JSAP.NO_SHORTFLAG);
        dwc_outports.setLongFlag(DWC_OUTPORTS);
        dwc_outports.setDefault(FALSE);
        dwc_outports.setHelp("Duplicate top-level output ports.");
        this.addCommand(dwc_outports);

        FlaggedOption no_dwc_p = new FlaggedOption(NO_DWC_P);
        no_dwc_p.setStringParser(JSAP.STRING_PARSER);
        no_dwc_p.setRequired(JSAP.NOT_REQUIRED);
        no_dwc_p.setShortFlag(JSAP.NO_SHORTFLAG);
        no_dwc_p.setLongFlag(NO_DWC_P);
        no_dwc_p.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        no_dwc_p.setList(JSAP.LIST);
        no_dwc_p.setListSeparator(LIST_DELIMITER);
        no_dwc_p.setUsageName("port");
        no_dwc_p
                .setHelp("Comma-separated list of top-level ports that should *not* be duplicated. Used with --dwc_inports and --dwc_outports");
        this.addCommand(no_dwc_p);

        FlaggedOption dwc_c = new FlaggedOption(DWC_C);
        dwc_c.setStringParser(JSAP.STRING_PARSER);
        dwc_c.setRequired(JSAP.NOT_REQUIRED);
        dwc_c.setShortFlag(JSAP.NO_SHORTFLAG);
        dwc_c.setLongFlag(DWC_C);
        dwc_c.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        dwc_c.setList(JSAP.LIST);
        dwc_c.setListSeparator(LIST_DELIMITER);
        dwc_c.setUsageName("cell_type");
        dwc_c.setHelp("Comma-separated list of cell types to be duplicated.");
        this.addCommand(dwc_c);

        FlaggedOption dwc_clk = new FlaggedOption(DWC_CLK);
        dwc_clk.setStringParser(JSAP.STRING_PARSER);
        dwc_clk.setRequired(JSAP.NOT_REQUIRED);
        dwc_clk.setShortFlag(JSAP.NO_SHORTFLAG);
        dwc_clk.setLongFlag(DWC_CLK);
        dwc_clk.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        dwc_clk.setList(JSAP.LIST);
        dwc_clk.setListSeparator(LIST_DELIMITER);
        dwc_clk.setUsageName("clock_domain");
        dwc_clk.setHelp("Comma-separated list of clock domains to be duplicated.");
        this.addCommand(dwc_clk);

        FlaggedOption dwc_i = new FlaggedOption(DWC_I);
        dwc_i.setStringParser(JSAP.STRING_PARSER);
        dwc_i.setRequired(JSAP.NOT_REQUIRED);
        dwc_i.setShortFlag(JSAP.NO_SHORTFLAG);
        dwc_i.setLongFlag(DWC_I);
        dwc_i.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        dwc_i.setList(JSAP.LIST);
        dwc_i.setListSeparator(LIST_DELIMITER);
        dwc_i.setUsageName("cell_instance");
        dwc_i.setHelp("Comma-separated list of cell instances to be duplicated.");
        this.addCommand(dwc_i);

        FlaggedOption no_dwc_c = new FlaggedOption(NO_DWC_C);
        no_dwc_c.setStringParser(JSAP.STRING_PARSER);
        no_dwc_c.setRequired(JSAP.NOT_REQUIRED);
        no_dwc_c.setShortFlag(JSAP.NO_SHORTFLAG);
        no_dwc_c.setLongFlag(NO_DWC_C);
        no_dwc_c.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        no_dwc_c.setList(JSAP.LIST);
        no_dwc_c.setListSeparator(LIST_DELIMITER);
        no_dwc_c.setUsageName("cell_type");
        no_dwc_c.setHelp("Comma-separated list of cell types that should *not* be duplicated.");
        this.addCommand(no_dwc_c);

        FlaggedOption no_dwc_clk = new FlaggedOption(NO_DWC_CLK);
        no_dwc_clk.setStringParser(JSAP.STRING_PARSER);
        no_dwc_clk.setRequired(JSAP.NOT_REQUIRED);
        no_dwc_clk.setShortFlag(JSAP.NO_SHORTFLAG);
        no_dwc_clk.setLongFlag(NO_DWC_CLK);
        no_dwc_clk.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        no_dwc_clk.setList(JSAP.LIST);
        no_dwc_clk.setListSeparator(LIST_DELIMITER);
        no_dwc_clk.setUsageName("clock_domain");
        no_dwc_clk.setHelp("Comma-separated list of clock domains that should *not* be duplicated.");
        this.addCommand(no_dwc_clk);

        FlaggedOption no_dwc_i = new FlaggedOption(NO_DWC_I);
        no_dwc_i.setStringParser(JSAP.STRING_PARSER);
        no_dwc_i.setRequired(JSAP.NOT_REQUIRED);
        no_dwc_i.setShortFlag(JSAP.NO_SHORTFLAG);
        no_dwc_i.setLongFlag(NO_DWC_I);
        no_dwc_i.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        no_dwc_i.setList(JSAP.LIST);
        no_dwc_i.setListSeparator(LIST_DELIMITER);
        no_dwc_i.setUsageName("cell_instance");
        no_dwc_i.setHelp("Comma-separated list of cell instances that should *not* be duplicated.");
        this.addCommand(no_dwc_i);

        Switch no_dwc_feedback = new Switch(NO_DWC_FEEDBACK);
        no_dwc_feedback.setShortFlag(JSAP.NO_SHORTFLAG);
        no_dwc_feedback.setLongFlag(NO_DWC_FEEDBACK);
        no_dwc_feedback.setDefault(FALSE);
        no_dwc_feedback.setHelp("Skip duplication of the feedback sections of the input design. *Not* recommended.");
        this.addCommand(no_dwc_feedback);

        Switch no_dwc_itf = new Switch(NO_DWC_INPUT_TO_FEEDBACK);
        no_dwc_itf.setShortFlag(JSAP.NO_SHORTFLAG);
        no_dwc_itf.setLongFlag(NO_DWC_INPUT_TO_FEEDBACK);
        no_dwc_itf.setDefault(FALSE);
        no_dwc_itf
                .setHelp("Skip duplication of the portions of the design that \"feed into\" the feedback sections of the design.");
        this.addCommand(no_dwc_itf);

        Switch no_dwc_fo = new Switch(NO_DWC_FEEDBACK_OUTPUT);
        no_dwc_fo.setShortFlag(JSAP.NO_SHORTFLAG);
        no_dwc_fo.setLongFlag(NO_DWC_FEEDBACK_OUTPUT);
        no_dwc_fo.setDefault(FALSE);
        no_dwc_fo
                .setHelp("Skip duplication of the portions of the input design which are driven by the feedback sections of the design.");
        this.addCommand(no_dwc_fo);

        Switch no_dwc_ff = new Switch(NO_DWC_FEED_FORWARD);
        no_dwc_ff.setShortFlag(JSAP.NO_SHORTFLAG);
        no_dwc_ff.setLongFlag(NO_DWC_FEED_FORWARD);
        no_dwc_ff.setDefault(FALSE);
        no_dwc_ff
                .setHelp("Skip duplication of the portions of the input design which are not related to feedback sections of the design.");
        this.addCommand(no_dwc_ff);

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
                .setHelp("Choose the method the BLDwc tool uses to duplicate logic in the \"feedback\" section of the design. Option 1 chooses the largest SCCs first. Option 2 chooses the smallest first. Option 3 adds the SCCs in topological order.");
        this.addCommand(scc_sort_type);

        Switch doSCCDecomp = new Switch(DO_SCC_DECOMPOSITION);
        doSCCDecomp.setShortFlag(JSAP.NO_SHORTFLAG);
        doSCCDecomp.setLongFlag(DO_SCC_DECOMPOSITION);
        doSCCDecomp.setDefault(FALSE);
        doSCCDecomp.setHelp("Allow SCCs to be partially duplicated.");
        this.addCommand(doSCCDecomp);

        FlaggedOption i_a_t = new FlaggedOption(INPUT_ADDITION_TYPE);
        i_a_t.setStringParser(_oneTwoThree);
        i_a_t.setRequired(JSAP.NOT_REQUIRED);
        i_a_t.setShortFlag(JSAP.NO_SHORTFLAG);
        i_a_t.setLongFlag(INPUT_ADDITION_TYPE);
        i_a_t.setUsageName("{1|2|3}");
        i_a_t.setDefault("3");
        i_a_t
                .setHelp("Select between three different algorithms to partially duplicate logic in the \"input to feedback\" section of the design. Option 1 uses a depth-first search starting from the inputs to the feedback section. Option 3 uses a breadth-first search. Option 2 uses a combination of the two.");
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
                .setHelp("This value is interpreted differently depending on the `factor_type' chosen. \nFor DUF (Desired Utilization Factor): specifies the maximum percentage of the target chip to be utilized after performing Partial DWC; must be greater than or equal to 0.0. \nFor UEF (Utilization Expansion Factor): specifies the maximum increase in utilization of the target part, expressed as a percentage of the utilization of the original (non-DWC'd) design; must be greater than or equal to 0.0. \nFor ASUF (Available Space Utilization Factor): specifies the maximum utilization of the target part, expressed as a percentage of the unused space on the part after the original (non-DWC'd) design has been considered; must be greater than 0.0 and less than or equal to 1.0.\n");
        this.addCommand(factor_value);

        Switch ignore_logic_utilization = new Switch(IGNORE_LOGIC_UTILIZATION);
        ignore_logic_utilization.setLongFlag(IGNORE_LOGIC_UTILIZATION);
        ignore_logic_utilization.setHelp("This option makes the BLDwc tool ignore logic utilization when "
                + "triplicating the design. Hard resources such as BRAMs and CLKDLLs " + "will still be tracked. ");
        this.addCommand(ignore_logic_utilization);

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

    public static String[] getNoDWCp(JSAPResult result) {
        return result.getStringArray(NO_DWC_P);
    }

    public static boolean dwcInports(JSAPResult result) {
        return result.getBoolean(DWC_INPORTS);
    }

    public static boolean dwcOutports(JSAPResult result) {
        return result.getBoolean(DWC_OUTPORTS);
    }

    public static Collection<String> dwcPorts(JSAPResult result) {
        return Arrays.asList(result.getStringArray(DWC_PORTS));
    }

    public static boolean noDWCi(JSAPResult result) {
        return result.contains(NO_DWC_I);
    }

    public static String[] getNoDWCi(JSAPResult result) {
        return result.getStringArray(NO_DWC_I);
    }

    public static boolean noDWCc(JSAPResult result) {
        return result.contains(NO_DWC_C);
    }

    public static String[] getNoDWCc(JSAPResult result) {
        return result.getStringArray(NO_DWC_C);
    }

    public static boolean noDWCclk(JSAPResult result) {
        return result.contains(NO_DWC_CLK);
    }

    public static String[] getNoDWCclk(JSAPResult result) {
        return result.getStringArray(NO_DWC_CLK);
    }

    public static boolean dwcI(JSAPResult result) {
        return result.contains(DWC_I);
    }

    public static String[] getDWCi(JSAPResult result) {
        return result.getStringArray(DWC_I);
    }

    public static boolean dwcC(JSAPResult result) {
        return result.contains(DWC_C);
    }

    public static String[] getDWCc(JSAPResult result) {
        return result.getStringArray(DWC_C);
    }

    public static boolean dwcClk(JSAPResult result) {
        return result.contains(DWC_CLK);
    }

    public static String[] getDWCclk(JSAPResult result) {
        return result.getStringArray(DWC_CLK);
    }

    public static boolean badCutConn(JSAPResult result) {
        return result.getBoolean(USE_BAD_CUT_CONN);
    }

    public static boolean fullDWC(JSAPResult result) {
        return result.getBoolean(FULL_DWC);
    }

    public static boolean noDWCFeedback(JSAPResult result) {
        return result.getBoolean(NO_DWC_FEEDBACK);
    }

    public static boolean doSCCDecomposition(JSAPResult result) {
        return result.getBoolean(DO_SCC_DECOMPOSITION);
    }

    public static int getSCCSortType(JSAPResult result) {
        return result.getInt(SCC_SORT_TYPE);
    }

    public static boolean noDWCinputToFeedback(JSAPResult result) {
        return result.getBoolean(NO_DWC_INPUT_TO_FEEDBACK);
    }

    public static boolean noDWCfeedForward(JSAPResult result) {
        return result.getBoolean(NO_DWC_FEED_FORWARD);
    }

    public static boolean noIOBFB(JSAPResult result) {
        return result.getBoolean(NO_IOB_FB);
    }

    public static boolean noDWCfeedbackOutput(JSAPResult result) {
        return result.getBoolean(NO_DWC_FEEDBACK_OUTPUT);
    }

    public static boolean use_drc(JSAPResult result) {
        return result.getBoolean(USE_DRC);
    }

    public static boolean persComp(JSAPResult result) {
        return result.getBoolean(PERS_COMP);
    }

    public static boolean register_detection(JSAPResult result) {
        return result.getBoolean(REGISTER_DETECTION);
    }

    public static boolean ignoreLogicUtilization(JSAPResult result) {
        return result.getBoolean(IGNORE_LOGIC_UTILIZATION);
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
     * Determine what utilization factor type is being used for partial DWC.
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

    public static final String IGNORE_LOGIC_UTILIZATION = "ignore_logic_utilization";

    public static final String FILE = "file";

    public static final String FULL_DWC = "full_dwc";

    public static final String HIGH = "high";

    public static final String INC = "inc";

    public static final String INPUT_ADDITION_TYPE = "input_addition_type";

    public static final String IOB_OUTPUT_FILE = "iob_output";

    public static final String LOG = "logfile";

    public static final String MERGE_FACTOR = "merge_factor";

    /**
     * No DWC Cell type
     */
    public static final String NO_DWC_C = "no_dwc_c";

    public static final String NO_DWC_FEEDBACK = "no_dwc_feedback";

    public static final String NO_DWC_FEEDBACK_OUTPUT = "no_dwc_feedback_output";

    public static final String NO_DWC_FEED_FORWARD = "no_dwc_feed_forward";

    /**
     * No DWC cell instance
     */
    public static final String NO_DWC_I = "no_dwc_i";

    /**
     * No DWC clock domain
     */
    public static final String NO_DWC_CLK = "no_dwc_clk";

    public static final String NO_DWC_INPUT_TO_FEEDBACK = "no_dwc_input_to_feedback";

    /**
     * No DWC port
     */
    public static final String NO_DWC_P = "no_dwc_p";

    public static final String OPTIMIZATION_FACTOR = "optimization_factor";

    public static final String OUTPUT_ADDITION_TYPE = "output_addition_type";

    public static final String SCC_SORT_TYPE = "scc_sort_type";

    /**
     * Force DWC of cell type
     */
    public static final String DWC_C = "dwc_c";

    /**
     * Force DWC of cell instance
     */
    public static final String DWC_I = "dwc_i";

    /**
     * Force DWC of clock domain
     */
    public static final String DWC_CLK = "dwc_clk";

    /**
     * Remove IOBs from feedback (Don't count as feedback)
     */
    public static final String NO_IOB_FB = "no_iob_feedback";

    public static final String DWC_INPORTS = "dwc_inports";

    public static final String DWC_OUTPORTS = "dwc_outports";

    public static final String DWC_PORTS = "dwc_p";

    public static final String USE_BAD_CUT_CONN = "use_bad_cut_conn";

    public static final String USE_DRC = "use_drc";

    public static final String PERS_COMP = "persistent_comparators";

    public static final String REGISTER_DETECTION = "register_detection";

    public static final String TRUE = "true";

    public static final String FALSE = "false";

    public static final char LIST_DELIMITER = ',';

}