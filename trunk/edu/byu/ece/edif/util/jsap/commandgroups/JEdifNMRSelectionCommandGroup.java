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
package edu.byu.ece.edif.util.jsap.commandgroups;

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
import edu.byu.ece.edif.util.jsap.BoundedDoubleStringParser;
import edu.byu.ece.edif.util.jsap.BoundedIntegerStringParser;

public class JEdifNMRSelectionCommandGroup extends AbstractCommandGroup {

    /**
     * TODO: Some of these options should be in more general command groups that
     * are usable across multiple tools. We need to break these up and put them
     * in a structure that makes sense.
     */
    public JEdifNMRSelectionCommandGroup() {

        Switch continue_selection = new Switch(CONTINUE_SELECTION);
        continue_selection.setShortFlag(JSAP.NO_SHORTFLAG);
        continue_selection.setLongFlag(CONTINUE_SELECTION);
        continue_selection.setDefault(FALSE);
        continue_selection.setHelp("Select this option to build selection results on top of results from previous runs. If not selected, the replication description file (.rdesc) will be overwritten completely instead of just modified. Normally, when continuing NMR selection with this flag, only instances that have not yet been selected for a replication type will be considered. Overriding replication types for instances can be accomplished by using the --" + OVERRIDE_SELECTION + " flag in conjunction with this flag.");
    	this.addCommand(continue_selection);
    	
    	Switch override = new Switch(OVERRIDE_SELECTION);
    	override.setShortFlag(JSAP.NO_SHORTFLAG);
    	override.setLongFlag(OVERRIDE_SELECTION);
    	override.setDefault(FALSE);
    	override.setHelp("This flag may be used in conjunction with the --" + CONTINUE_SELECTION + " flag in order to override the replication type selections for instances that have already been selected");
    	this.addCommand(override);
        
        /*
         * Partial NMR options
         */
        Switch full_nmr = new Switch(FULL_NMR);
        full_nmr.setShortFlag(JSAP.NO_SHORTFLAG);
        full_nmr.setLongFlag(FULL_NMR);
        full_nmr.setDefault(FALSE);
        full_nmr.setHelp("Fully replicate the design, skipping all partial NMR analysis.");
        this.addCommand(full_nmr);

        Switch no_partial_nmr = new Switch(NO_PARTIAL_NMR);
        no_partial_nmr.setShortFlag(JSAP.NO_SHORTFLAG);
        no_partial_nmr.setLongFlag(NO_PARTIAL_NMR);
        no_partial_nmr.setDefault(FALSE);
        no_partial_nmr.setHelp("This option will disable the use of partial NMR analysis to determine which parts of the circuit to replicate. " +
        		"Use this option in conjuction with the --nmr_i and --nmr_c options for explicit control of replicated instances. This option need " +
        		"not be used when the --full_nmr option is used.");
        this.addCommand(no_partial_nmr);
        
        FlaggedOption nmr_p = new FlaggedOption(NMR_PORTS);
        nmr_p.setStringParser(JSAP.STRING_PARSER);
        nmr_p.setRequired(JSAP.NOT_REQUIRED);
        nmr_p.setShortFlag(JSAP.NO_SHORTFLAG);
        nmr_p.setLongFlag(NMR_PORTS);
        nmr_p.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        nmr_p.setList(JSAP.LIST);
        nmr_p.setListSeparator(LIST_DELIMITER);
        nmr_p.setUsageName("Port name");
        nmr_p.setHelp("Comma-separated list of ports to be replicated.");
        this.addCommand(nmr_p);

        Switch nmr_inports = new Switch(NMR_INPORTS);
        nmr_inports.setShortFlag(JSAP.NO_SHORTFLAG);
        nmr_inports.setLongFlag(NMR_INPORTS);
        nmr_inports.setDefault(FALSE);
        nmr_inports.setHelp("Replicate top-level input ports.");
        this.addCommand(nmr_inports);

        Switch nmr_outports = new Switch(NMR_OUTPORTS);
        nmr_outports.setShortFlag(JSAP.NO_SHORTFLAG);
        nmr_outports.setLongFlag(NMR_OUTPORTS);
        nmr_outports.setDefault(FALSE);
        nmr_outports.setHelp("Replicate top-level output ports.");
        this.addCommand(nmr_outports);

        FlaggedOption no_nmr_p = new FlaggedOption(NO_NMR_P);
        no_nmr_p.setStringParser(JSAP.STRING_PARSER);
        no_nmr_p.setRequired(JSAP.NOT_REQUIRED);
        no_nmr_p.setShortFlag(JSAP.NO_SHORTFLAG);
        no_nmr_p.setLongFlag(NO_NMR_P);
        no_nmr_p.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        no_nmr_p.setList(JSAP.LIST);
        no_nmr_p.setListSeparator(LIST_DELIMITER);
        no_nmr_p.setUsageName("port");
        no_nmr_p
                .setHelp("Comma-separated list of top-level ports that should *not* be replicated. Used with --nmr_inports and --nmr_outports");
        this.addCommand(no_nmr_p);

        FlaggedOption nmr_c = new FlaggedOption(NMR_C);
        nmr_c.setStringParser(JSAP.STRING_PARSER);
        nmr_c.setRequired(JSAP.NOT_REQUIRED);
        nmr_c.setShortFlag(JSAP.NO_SHORTFLAG);
        nmr_c.setLongFlag(NMR_C);
        nmr_c.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        nmr_c.setList(JSAP.LIST);
        nmr_c.setListSeparator(LIST_DELIMITER);
        nmr_c.setUsageName("cell_type");
        nmr_c.setHelp("Comma-separated list of cell types to be replicated.");
        this.addCommand(nmr_c);

        FlaggedOption nmr_clk = new FlaggedOption(NMR_CLK);
        nmr_clk.setStringParser(JSAP.STRING_PARSER);
        nmr_clk.setRequired(JSAP.NOT_REQUIRED);
        nmr_clk.setShortFlag(JSAP.NO_SHORTFLAG);
        nmr_clk.setLongFlag(NMR_CLK);
        nmr_clk.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        nmr_clk.setList(JSAP.LIST);
        nmr_clk.setListSeparator(LIST_DELIMITER);
        nmr_clk.setUsageName("clock_domain");
        nmr_clk.setHelp("Comma-separated list of clock domains to be replicated.");
        this.addCommand(nmr_clk);

        FlaggedOption nmr_i = new FlaggedOption(NMR_I);
        nmr_i.setStringParser(JSAP.STRING_PARSER);
        nmr_i.setRequired(JSAP.NOT_REQUIRED);
        nmr_i.setShortFlag(JSAP.NO_SHORTFLAG);
        nmr_i.setLongFlag(NMR_I);
        nmr_i.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        nmr_i.setList(JSAP.LIST);
        nmr_i.setListSeparator(LIST_DELIMITER);
        nmr_i.setUsageName("cell_instance");
        nmr_i.setHelp("Comma-separated list of cell instances to be replicated.");
        this.addCommand(nmr_i);

        FlaggedOption no_nmr_c = new FlaggedOption(NO_NMR_C);
        no_nmr_c.setStringParser(JSAP.STRING_PARSER);
        no_nmr_c.setRequired(JSAP.NOT_REQUIRED);
        no_nmr_c.setShortFlag(JSAP.NO_SHORTFLAG);
        no_nmr_c.setLongFlag(NO_NMR_C);
        no_nmr_c.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        no_nmr_c.setList(JSAP.LIST);
        no_nmr_c.setListSeparator(LIST_DELIMITER);
        no_nmr_c.setUsageName("cell_type");
        no_nmr_c.setHelp("Comma-separated list of cell types that should *not* be replicated.");
        this.addCommand(no_nmr_c);

        FlaggedOption no_nmr_clk = new FlaggedOption(NO_NMR_CLK);
        no_nmr_clk.setStringParser(JSAP.STRING_PARSER);
        no_nmr_clk.setRequired(JSAP.NOT_REQUIRED);
        no_nmr_clk.setShortFlag(JSAP.NO_SHORTFLAG);
        no_nmr_clk.setLongFlag(NO_NMR_CLK);
        no_nmr_clk.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        no_nmr_clk.setList(JSAP.LIST);
        no_nmr_clk.setListSeparator(LIST_DELIMITER);
        no_nmr_clk.setUsageName("clock_domain");
        no_nmr_clk.setHelp("Comma-separated list of clock domains that should *not* be replicated.");
        this.addCommand(no_nmr_clk);

        FlaggedOption no_nmr_i = new FlaggedOption(NO_NMR_I);
        no_nmr_i.setStringParser(JSAP.STRING_PARSER);
        no_nmr_i.setRequired(JSAP.NOT_REQUIRED);
        no_nmr_i.setShortFlag(JSAP.NO_SHORTFLAG);
        no_nmr_i.setLongFlag(NO_NMR_I);
        no_nmr_i.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        no_nmr_i.setList(JSAP.LIST);
        no_nmr_i.setListSeparator(LIST_DELIMITER);
        no_nmr_i.setUsageName("cell_instance");
        no_nmr_i.setHelp("Comma-separated list of cell instances that should *not* be replicated.");
        this.addCommand(no_nmr_i);

        Switch no_nmr_feedback = new Switch(NO_NMR_FEEDBACK);
        no_nmr_feedback.setShortFlag(JSAP.NO_SHORTFLAG);
        no_nmr_feedback.setLongFlag(NO_NMR_FEEDBACK);
        no_nmr_feedback.setDefault(FALSE);
        no_nmr_feedback.setHelp("Skip replication of the feedback sections of the input design. *Not* recommended.");
        this.addCommand(no_nmr_feedback);

        Switch no_nmr_itf = new Switch(NO_NMR_INPUT_TO_FEEDBACK);
        no_nmr_itf.setShortFlag(JSAP.NO_SHORTFLAG);
        no_nmr_itf.setLongFlag(NO_NMR_INPUT_TO_FEEDBACK);
        no_nmr_itf.setDefault(FALSE);
        no_nmr_itf
                .setHelp("Skip replication of the portions of the design that \"feed into\" the feedback sections of the design.");
        this.addCommand(no_nmr_itf);

        Switch no_nmr_fo = new Switch(NO_NMR_FEEDBACK_OUTPUT);
        no_nmr_fo.setShortFlag(JSAP.NO_SHORTFLAG);
        no_nmr_fo.setLongFlag(NO_NMR_FEEDBACK_OUTPUT);
        no_nmr_fo.setDefault(FALSE);
        no_nmr_fo.setHelp("Skip replication of the portions of the input design which are driven by the feedback sections of the design.");
        this.addCommand(no_nmr_fo);

        Switch no_nmr_ff = new Switch(NO_NMR_FEED_FORWARD);
        no_nmr_ff.setShortFlag(JSAP.NO_SHORTFLAG);
        no_nmr_ff.setLongFlag(NO_NMR_FEED_FORWARD);
        no_nmr_ff.setDefault(FALSE);
        no_nmr_ff.setHelp("Skip replication of the portions of the input design which are not related to feedback sections of the design.");
        this.addCommand(no_nmr_ff);

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
                .setHelp("Choose the method used to replicate logic in the \"feedback\" section of the design. Option 1 chooses the largest SCCs first. Option 2 chooses the smallest first. Option 3 adds the SCCs in topological order.");
        this.addCommand(scc_sort_type);

        Switch doSCCDecomp = new Switch(DO_SCC_DECOMPOSITION);
        doSCCDecomp.setShortFlag(JSAP.NO_SHORTFLAG);
        doSCCDecomp.setLongFlag(DO_SCC_DECOMPOSITION);
        doSCCDecomp.setDefault(FALSE);
        doSCCDecomp.setHelp("Allow SCCs to be partially replicated.");
        this.addCommand(doSCCDecomp);

        FlaggedOption i_a_t = new FlaggedOption(INPUT_ADDITION_TYPE);
        i_a_t.setStringParser(_oneTwoThree);
        i_a_t.setRequired(JSAP.NOT_REQUIRED);
        i_a_t.setShortFlag(JSAP.NO_SHORTFLAG);
        i_a_t.setLongFlag(INPUT_ADDITION_TYPE);
        i_a_t.setUsageName("{1|2|3}");
        i_a_t.setDefault("3");
        i_a_t
                .setHelp("Select between three different algorithms to partially replicate logic in the \"input to feedback\" section of the design. Option 1 uses a depth-first search starting from the inputs to the feedback section. Option 3 uses a breadth-first search. Option 2 uses a combination of the two.");
        this.addCommand(i_a_t);

        FlaggedOption o_a_t = new FlaggedOption(OUTPUT_ADDITION_TYPE);
        o_a_t.setStringParser(_oneTwoThree);
        o_a_t.setRequired(JSAP.NOT_REQUIRED);
        o_a_t.setShortFlag(JSAP.NO_SHORTFLAG);
        o_a_t.setLongFlag(OUTPUT_ADDITION_TYPE);
        o_a_t.setUsageName("{1|2|3}");
        o_a_t.setDefault("3");
        o_a_t.setHelp("This option is similar to the `--inputAdditionType' option except it applies to the logic driven by the feedback section of the design.");
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
                .setHelp("This value is interpreted differently depending on the `factor_type' chosen. \nFor DUF (Desired Utilization Factor): specifies the maximum percentage of the target chip to be utilized after performing Partial NMR; must be greater than or equal to 0.0. \nFor UEF (Utilization Expansion Factor): specifies the maximum increase in utilization of the target part, expressed as a percentage of the utilization of the original (non-NMR'd) design; must be greater than or equal to 0.0. \nFor ASUF (Available Space Utilization Factor): specifies the maximum utilization of the target part, expressed as a percentage of the unused space on the part after the original (non-NMR'd) design has been considered; must be greater than 0.0 and less than or equal to 1.0.\n");
        this.addCommand(factor_value);

        Switch ignore_hard_resource_utilization = new Switch(IGNORE_HARD_UTILIZATION_LIMITS);
        ignore_hard_resource_utilization.setLongFlag(IGNORE_HARD_UTILIZATION_LIMITS);
        ignore_hard_resource_utilization
                .setHelp("This option causes all hard resource utilization limits to be ignored when replicating the design.");
        this.addCommand(ignore_hard_resource_utilization);

        Switch ignore_soft_logic_utilization = new Switch(IGNORE_SOFT_UTILIZATION_LIMIT);
        ignore_soft_logic_utilization.setLongFlag(IGNORE_SOFT_UTILIZATION_LIMIT);
        ignore_soft_logic_utilization.setHelp("This option causes logic block utilization to be ignored when "
                + "replicating the design. Hard resources such as BRAMs and CLKDLLs will still be tracked. ");
        this.addCommand(ignore_soft_logic_utilization);

        /*
         * Target Technology and Part Options
         */



    }

//    public static String getIOBName(JSAPResult result) {
//        String filename = result.getString(IOB_OUTPUT_FILE);
//        if (!filename.contains(".")) {
//            filename = filename.concat(".iob");
//        }
//        return filename;
//    }

    public static boolean continueSelection(JSAPResult result) {
    	return result.getBoolean(CONTINUE_SELECTION);
    }
    
    public static boolean overrideSelection(JSAPResult result) {
    	return result.getBoolean(OVERRIDE_SELECTION);
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

    public static String[] getNoNMRp(JSAPResult result) {
        return result.getStringArray(NO_NMR_P);
    }

    public static boolean nmrInports(JSAPResult result) {
        return result.getBoolean(NMR_INPORTS);
    }

    public static boolean nmrOutports(JSAPResult result) {
        return result.getBoolean(NMR_OUTPORTS);
    }

    public static Collection<String> nmrPorts(JSAPResult result) {
        return Arrays.asList(result.getStringArray(NMR_PORTS));
    }

    public static boolean noNMRi(JSAPResult result) {
        return result.contains(NO_NMR_I);
    }

    public static String[] getNoNMRi(JSAPResult result) {
        return result.getStringArray(NO_NMR_I);
    }

    public static boolean noNMRc(JSAPResult result) {
        return result.contains(NO_NMR_C);
    }

    public static String[] getNoNMRc(JSAPResult result) {
        return result.getStringArray(NO_NMR_C);
    }

    public static boolean noNMRclk(JSAPResult result) {
        return result.contains(NO_NMR_CLK);
    }

    public static String[] getNoNMRclk(JSAPResult result) {
        return result.getStringArray(NO_NMR_CLK);
    }

    public static boolean nmrI(JSAPResult result) {
        return result.contains(NMR_I);
    }

    public static String[] getNMRi(JSAPResult result) {
        return result.getStringArray(NMR_I);
    }

    public static boolean nmrC(JSAPResult result) {
        return result.contains(NMR_C);
    }

    public static String[] getNMRc(JSAPResult result) {
        return result.getStringArray(NMR_C);
    }

    public static boolean nmrClk(JSAPResult result) {
        return result.contains(NMR_CLK);
    }

    public static String[] getNMRclk(JSAPResult result) {
        return result.getStringArray(NMR_CLK);
    }

    public static boolean fullNMR(JSAPResult result) {
        return result.getBoolean(FULL_NMR);
    }
    
    public static boolean noPartialNMR(JSAPResult result) {
    	return result.getBoolean(NO_PARTIAL_NMR);
    }

    public static boolean noNMRFeedback(JSAPResult result) {
        return result.getBoolean(NO_NMR_FEEDBACK);
    }

    public static boolean doSCCDecomposition(JSAPResult result) {
        return result.getBoolean(DO_SCC_DECOMPOSITION);
    }

    public static int getSCCSortType(JSAPResult result) {
        return result.getInt(SCC_SORT_TYPE);
    }

    public static boolean noNMRinputToFeedback(JSAPResult result) {
        return result.getBoolean(NO_NMR_INPUT_TO_FEEDBACK);
    }

    public static boolean noNMRfeedForward(JSAPResult result) {
        return result.getBoolean(NO_NMR_FEED_FORWARD);
    }

    public static boolean noNMRfeedbackOutput(JSAPResult result) {
        return result.getBoolean(NO_NMR_FEEDBACK_OUTPUT);
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

//    public static String getIOBOutputFilename(JSAPResult result) {
//        return result.getString(IOB_OUTPUT_FILE);
//    }

    /**
     * Determine what utilization factor type is being used for partial NMR.
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

    public static final String CONTINUE_SELECTION = "continue";
    
    public static final String OVERRIDE_SELECTION = "override";
    
    public static final String DO_SCC_DECOMPOSITION = "do_scc_decomposition";

    public static final String FACTOR_TYPE = "factor_type";

    public static final String FACTOR_VALUE = "factor_value";

    public static final String IGNORE_HARD_UTILIZATION_LIMITS = "ignore_hard_resource_utilization_limits";

    public static final String IGNORE_SOFT_UTILIZATION_LIMIT = "ignore_soft_logic_utilization_limit";

    public static final String FILE = "file";

    public static final String FULL_NMR = "full_nmr";

    public static final String HIGH = "high";

    public static final String INC = "inc";

    public static final String INPUT_ADDITION_TYPE = "input_addition_type";

//    public static final String IOB_OUTPUT_FILE = "iob_output";

    public static final String LOG = "logfile";

    public static final String MERGE_FACTOR = "merge_factor";

    /**
     * No NMR Cell type
     */
    public static final String NO_NMR_C = "no_nmr_c";

    public static final String NO_NMR_FEEDBACK = "no_nmr_feedback";

    public static final String NO_NMR_FEEDBACK_OUTPUT = "no_nmr_feedback_output";

    public static final String NO_NMR_FEED_FORWARD = "no_nmr_feed_forward";

    /**
     * No NMR cell instance
     */
    public static final String NO_NMR_I = "no_nmr_i";

    /**
     * No NMR clock domain
     */
    public static final String NO_NMR_CLK = "no_nmr_clk";

    public static final String NO_NMR_INPUT_TO_FEEDBACK = "no_nmr_input_to_feedback";

    /**
     * No NMR port
     */
    public static final String NO_NMR_P = "no_nmr_p";

    public static final String OPTIMIZATION_FACTOR = "optimization_factor";

    public static final String OUTPUT_ADDITION_TYPE = "output_addition_type";

    public static final String SCC_SORT_TYPE = "scc_sort_type";

    /**
     * Force NMR of cell type
     */
    public static final String NMR_C = "nmr_c";

    /**
     * Force NMR of cell instance
     */
    public static final String NMR_I = "nmr_i";

    /**
     * Force NMR of clock domain
     */
    public static final String NMR_CLK = "nmr_clk";

    public static final String NMR_INPORTS = "nmr_inports";

    public static final String NMR_OUTPORTS = "nmr_outports";

    public static final String NMR_PORTS = "nmr_p";
    
    public static final String NO_PARTIAL_NMR = "no_partial_nmr";

    public static final String TRUE = "true";

    public static final String FALSE = "false";

    public static final char LIST_DELIMITER = ',';

}