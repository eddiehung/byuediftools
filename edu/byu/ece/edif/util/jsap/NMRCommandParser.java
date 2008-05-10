/*
 * A command-line argument parser for options common to all NMR operations.
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
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import com.martiansoftware.jsap.Flagged;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.QualifiedSwitch;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.defaultsources.PropertyDefaultSource;
import com.martiansoftware.jsap.stringparsers.DoubleStringParser;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;

import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.tools.replicate.nmr.NMREdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.NMRUtilities;
import edu.byu.ece.edif.tools.replicate.nmr.NMRUtilities.UtilizationFactor;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.FlattenTMR;

/**
 * A command-line argument parser for FlattenTMR, also known as the BLTmr Tool,
 * based on {@link JSAP} (Java-based Simple Argument Parser). This class handles
 * the parsing of all command-line arguments, ensuring that they are
 * syntactically correct. It also provides automatically-formated usage and help
 * information (<code>--help</code> option).
 * 
 * @see DWCCommandParser
 * @see JSAP
 * @see JSAPCommandParser
 * @see TMRCommandParser
 * @see <a href="http://www.martiansoftware.com/jsap/doc/javadoc/index.html">
 * JSAP API</a>
 * @see <a href="http://www.martiansoftware.com/jsap/">JSAP Homepage</a>
 * @author <a href="mailto:jcarroll@byu.net">James Carroll</a>
 */

public class NMRCommandParser extends JSAPCommandParser {

    /**
     * Create a new NMRCommandParser.
     */
    public NMRCommandParser(int replicationFactor) {

        /*
         * Parent constructor sets up common Parameters such as --help,
         * --version, --writeConfig, --useConfig, etc.
         */
        super();

        // Set NMRDegree first thing
        this.replicationFactor = replicationFactor;

        _NMRParameters = new LinkedHashSet<Parameter>();

        /*
         * File options: input_file, output_file, etc.
         */
        _NMRParameters
                .add(new UnflaggedOption(INPUT_FILE)
                        .setStringParser(JSAP.STRING_PARSER)
                        .setRequired(JSAP.REQUIRED)
                        .setUsageName("input_file")
                        .setHelp(
                                "Filename and path to the EDIF source file containing the top-level cell to be triplicated. Required."));

        _NMRParameters.add(new FlaggedOption(OUTPUT_FILE).setStringParser(JSAP.STRING_PARSER).setDefault("nmr.edf")
                .setRequired(JSAP.NOT_REQUIRED).setShortFlag('o').setLongFlag("output").setUsageName("output_file")
                .setHelp("Filename and path to the triplicated EDIF output file created by the BLTmr tool."));

        _NMRParameters.add(new FlaggedOption(DIR).setStringParser(JSAP.STRING_PARSER).setRequired(JSAP.NOT_REQUIRED)
                .setShortFlag('d').setLongFlag("dir").setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS).setList(
                        JSAP.LIST).setListSeparator(LIST_DELIMITER).setHelp(
                        "Comma-separated list of directories containing external EDIF files to be included."));

        _NMRParameters
                .add(new FlaggedOption(FILE)
                        .setStringParser(JSAP.STRING_PARSER)
                        .setRequired(JSAP.NOT_REQUIRED)
                        .setShortFlag('f')
                        .setLongFlag("file")
                        .setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS)
                        .setList(JSAP.LIST)
                        .setListSeparator(LIST_DELIMITER)
                        .setHelp(
                                "Similar to the previous option, but each external EDIF file is named explicitly--including the path to the file."));

        _NMRParameters.add(new FlaggedOption(NMR_SUFFIX).setStringParser(JSAP.STRING_PARSER).setRequired(
                JSAP.NOT_REQUIRED).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("nmrSuffix")
                .setAllowMultipleDeclarations(JSAP.NO_MULTIPLEDECLARATIONS).setList(JSAP.LIST).setListSeparator(
                        LIST_DELIMITER).setUsageName("suffix").setHelp(
                        "Comma-separated list of the " + replicationFactor
                                + " suffixes to use when replicating components."));

        /*
         * Partial NMR options
         */
        _NMRParameters.add(new Switch(FULL_NMR).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("full_nmr").setDefault(
                FALSE).setHelp("Fully triplicate the design, skipping all partial NMR analysis."));

        _NMRParameters.add(new Switch(NMR_INPORTS).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("nmr_inports")
                .setDefault(FALSE).setHelp("Triplicate top-level input ports."));

        _NMRParameters.add(new Switch(NMR_OUTPORTS).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("nmr_outports")
                .setDefault(FALSE).setHelp("Triplicate top-level output ports."));

        _NMRParameters
                .add(new FlaggedOption(NO_NMR_P)
                        .setStringParser(JSAP.STRING_PARSER)
                        .setRequired(JSAP.NOT_REQUIRED)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("no_nmr_p")
                        .setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS)
                        .setList(JSAP.LIST)
                        .setListSeparator(LIST_DELIMITER)
                        .setUsageName("port")
                        .setHelp(
                                "Comma-separated list of top-level ports that should *not* be triplicated. Used with --nmr_inports and --nmr_outports"));

        _NMRParameters.add(new FlaggedOption(NMR_C).setStringParser(JSAP.STRING_PARSER).setRequired(JSAP.NOT_REQUIRED)
                .setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("nmr_c").setAllowMultipleDeclarations(
                        JSAP.MULTIPLEDECLARATIONS).setList(JSAP.LIST).setListSeparator(LIST_DELIMITER).setUsageName(
                        "cell_type").setHelp("Comma-separated list of cell types to be triplicated."));

        _NMRParameters.add(new FlaggedOption(NMR_CLK).setStringParser(JSAP.STRING_PARSER)
                .setRequired(JSAP.NOT_REQUIRED).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("nmr_clk")
                .setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS).setList(JSAP.LIST).setListSeparator(
                        LIST_DELIMITER).setUsageName("clock_domain").setHelp(
                        "Comma-separated list of clock domains to be triplicated."));

        _NMRParameters.add(new FlaggedOption(NMR_I).setStringParser(JSAP.STRING_PARSER).setRequired(JSAP.NOT_REQUIRED)
                .setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("nmr_i").setAllowMultipleDeclarations(
                        JSAP.MULTIPLEDECLARATIONS).setList(JSAP.LIST).setListSeparator(LIST_DELIMITER).setUsageName(
                        "cell_instance").setHelp("Comma-separated list of cell instances to be triplicated."));

        _NMRParameters.add(new FlaggedOption(NO_NMR_C).setStringParser(JSAP.STRING_PARSER).setRequired(
                JSAP.NOT_REQUIRED).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("no_nmr_c")
                .setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS).setList(JSAP.LIST).setListSeparator(
                        LIST_DELIMITER).setUsageName("cell_type").setHelp(
                        "Comma-separated list of cell types that should *not* be triplicated."));
        _NMRParameters.add(new FlaggedOption(NO_NMR_CLK).setStringParser(JSAP.STRING_PARSER).setRequired(
                JSAP.NOT_REQUIRED).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("no_nmr_clk")
                .setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS).setList(JSAP.LIST).setListSeparator(
                        LIST_DELIMITER).setUsageName("clock_domain").setHelp(
                        "Comma-separated list of clock domains that should *not* be triplicated."));

        _NMRParameters.add(new FlaggedOption(NO_NMR_I).setStringParser(JSAP.STRING_PARSER).setRequired(
                JSAP.NOT_REQUIRED).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("no_nmr_i")
                .setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS).setList(JSAP.LIST).setListSeparator(
                        LIST_DELIMITER).setUsageName("cell_instance").setHelp(
                        "Comma-separated list of cell instances that should *not* be triplicated."));

        _NMRParameters.add(new Switch(NO_NMR_FEEDBACK).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("no_nmr_feedback")
                .setDefault(FALSE).setHelp(
                        "Skip triplication of the feedback sections of the input design. *Not* recommended."));

        _NMRParameters
                .add(new Switch(NO_NMR_INPUT_TO_FEEDBACK)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("no_nmr_input_to_feedback")
                        .setDefault(FALSE)
                        .setHelp(
                                "Skip triplication of the portions of the design that \"feed into\" the feedback sections of the design."));

        _NMRParameters
                .add(new Switch(NO_NMR_FEEDBACK_OUTPUT)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("no_nmr_feedback_output")
                        .setDefault(FALSE)
                        .setHelp(
                                "Skip triplication of the portions of the input design which are driven by the feedback sections of the design."));

        _NMRParameters
                .add(new Switch(NO_NMR_FEED_FORWARD)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("no_nmr_feed_forward")
                        .setDefault(FALSE)
                        .setHelp(
                                "Skip triplication of the portions of the input design which are not related to feedback sections of the design."));

        _NMRParameters
                .add(new Switch(NO_IN_OUT_CHECK)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("noInoutCheck")
                        .setDefault(FALSE)
                        .setHelp(
                                "By default, designs with INOUT ports are not allowed by the BLTmr tool, since it currently does not support all aspects of INOUT ports. This option forces the tool to allow designs with INOUT ports, which *may* result in a working NMR'd design, but might not."));

        _NMRParameters
                .add(new Switch(NO_IOB_FB)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("no_iob_feedback")
                        .setDefault(FALSE)
                        .setHelp(
                                "The user may wish to exclude the IOBs (specifically inout ports) from feedback analysis if there is no true feedback (by design). This may greatly reduce the amount of feedback detected."));

        /*
         * SCC Options
         */
        _NMRParameters
                .add(new FlaggedOption(SCC_SORT_TYPE)
                        .setStringParser(_oneTwoThree)
                        .setRequired(JSAP.NOT_REQUIRED)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("SCCSortType")
                        .setUsageName("{1|2|3}")
                        .setDefault("3")
                        .setHelp(
                                "Choose the method the BLTmr tool uses to triplicate logic in the \"feedback\" section of the design. Option 1 chooses the largest SCCs first. Option 2 chooses the smallest first. Option 3 adds the SCCs in topological order."));

        _NMRParameters.add(new Switch(DO_SCC_DECOMPOSITION).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag(
                "doSCCDecomposition").setDefault(FALSE).setHelp("Allow SCCs to be partially triplicated."));

        _NMRParameters.add(new Switch(USE_BAD_CUT_CONN).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("use_bad_cut_conn")
                .setDefault(FALSE).setHelp("Use bad cut connections option."));

        _NMRParameters.add(new Switch(HIGHEST_FANOUT_CUTSET).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag(
                "highest_fanout_cutset").setDefault(FALSE).setHelp("Use highest fanout cutset option."));

        _NMRParameters.add(new Switch(HIGHEST_FF_FANOUT_CUTSET).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag(
                "highest_ff_fanout_cutset").setDefault(FALSE).setHelp("Use highest flip-flop cutset option"));

        _NMRParameters
                .add(new FlaggedOption(INPUT_ADDITION_TYPE)
                        .setStringParser(_oneTwoThree)
                        .setRequired(JSAP.NOT_REQUIRED)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("inputAdditionType")
                        .setUsageName("{1|2|3}")
                        .setDefault("3")
                        .setHelp(
                                "Select between three different algorithms to partially triplicate logic in the \"input to feedback\" section of the design. Option 1 uses a depth-first search starting from the inputs to the feedback section. Option 3 uses a breadth-first search. Option 2 uses a combination of the two."));

        _NMRParameters
                .add(new FlaggedOption(OUTPUT_ADDITION_TYPE)
                        .setStringParser(_oneTwoThree)
                        .setRequired(JSAP.NOT_REQUIRED)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("outputAdditionType")
                        .setUsageName("{1|2|3}")
                        .setDefault("3")
                        .setHelp(
                                "This option is similar to the `--inputAdditionType' option except it applies to the logic driven by the feedback section of the design."));

        /*
         * Merge Factor, Optimization Factor, and Utilization Factors
         */
        _NMRParameters.add(new FlaggedOption(MERGE_FACTOR).setStringParser(_zeroToOneInclusive).setShortFlag(
                JSAP.NO_SHORTFLAG).setLongFlag("mergeFactor").setDefault("0.5").setHelp(
                "The assumed percentage of LUTs and flip-flops that will share the same \"slice\"."));

        _NMRParameters
                .add(new FlaggedOption(OPTIMIZATION_FACTOR)
                        .setStringParser(_zeroToOneInclusive)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("optimizationFactor")
                        .setDefault("0.95")
                        .setHelp(
                                "The assumed percentage of the resulting design size after logic optimization techniques compared with the design size prior to optimization."));

        _NMRParameters.add(new FlaggedOption(FACTOR_TYPE).setStringParser(
                EnumeratedStringParser.getParser(FACTOR_TYPES, false)).setRequired(JSAP.NOT_REQUIRED).setShortFlag(
                JSAP.NO_SHORTFLAG).setLongFlag(FACTOR_TYPE).setDefault(
                NMRUtilities.DEFAULT_UTILIZATION_FACTOR.toString()).setUsageName(
                "{" + FACTOR_TYPES.replace(';', '|') + "}").setHelp(
                "Utilization Factor type. Must be one of the following: " + FACTOR_TYPES.replace(";", ", ")
                        + ". See `factor_value' for more information about each factor type."));

        _NMRParameters
                .add(new FlaggedOption(FACTOR_VALUE)
                        .setStringParser(DoubleStringParser.getParser())
                        .setRequired(JSAP.NOT_REQUIRED)
                        .setLongFlag(FACTOR_VALUE)
                        .setDefault(NMRUtilities.DEFAULT_FACTOR_VALUE)
                        .setHelp(
                                "This value is interpreted differently depending on the `factor_type' chosen. \nFor DUF (Desired Utilization Factor): specifies the maximum percentage of the target chip to be utilized after performing Partial NMR; must be greater than or equal to 0.0. \nFor UEF (Utilization Expansion Factor): specifies the maximum increase in utilization of the target part, expressed as a percentage of the utilization of the original (non-NMR'd) design; must be greater than or equal to 0.0. \nFor ASUF (Available Space Utilization Factor): specifies the maximum utilization of the target part, expressed as a percentage of the unused space on the part after the original (non-NMR'd) design has been considered; must be greater than 0.0 and less than or equal to 1.0.\n"));

        /*
         * Multiple EDIF Creation options
         */
        _NMRParameters.add(new FlaggedOption(LOW).setStringParser(_minZeroInclusive).setRequired(JSAP.NOT_REQUIRED)
                .setLongFlag(LOW).setHelp("Utilization factor lower bound for multiple EDIF file creation."));

        _NMRParameters.add(new FlaggedOption(HIGH).setStringParser(_minZeroInclusive).setRequired(JSAP.NOT_REQUIRED)
                .setLongFlag(HIGH).setHelp("Utilization factor upper bound for multiple EDIF file creation."));

        _NMRParameters.add(new FlaggedOption(INC).setStringParser(_zeroToOneInclusive).setRequired(JSAP.NOT_REQUIRED)
                .setLongFlag(INC).setDefault("0.10").setHelp(
                        "Utilization factor step size for multiple EDIF file creation."));

        /*
         * Half Latch Removal Options
         */
        _NMRParameters
                .add(new Switch(REMOVE_HL)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("removeHL")
                        .setDefault(FALSE)
                        .setHelp(
                                "Remove half-latches in the input design before performing NMR. \nNOTE: Not *all* half-latches can be removed at the EDIF level for all architectures. Some post-processing may be necessary."));

        _NMRParameters.add(new FlaggedOption(HL_CONSTANT).setStringParser(_zeroOne).setShortFlag(JSAP.NO_SHORTFLAG)
                .setLongFlag("hlConst").setDefault("0").setUsageName("{0|1}").setHelp(
                        "The polarity of the half-latch constant to be used. Valid options are '0' and '1'."));

        _NMRParameters
                .add(new FlaggedOption(HL_PORT_NAME)
                        .setStringParser(JSAP.STRING_PARSER)
                        .setRequired(JSAP.NOT_REQUIRED)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("hlUsePort")
                        .setDefault(JSAP.NO_DEFAULT)
                        .setHelp(
                                "Use the specified top-level port as the safe half-latch constant when using half-latch removal."));

        _NMRParameters
                .add(new FlaggedOption(PACK_IOB_REGISTERS)
                        .setStringParser(
                                EnumeratedStringParser.getParser(INPUT + ";" + OUTPUT + ";" + BOTH + ";" + NONE, false))
                        .setShortFlag('r')
                        .setLongFlag("packRegisters")
                        .setDefault(BOTH)
                        .setUsageName("{" + INPUT + "|" + OUTPUT + "|" + BOTH + "|" + NONE + "}")
                        .setHelp(
                                "Half-latch removal by default treat registers that can be packed into the IOB differently. 'i' will pack input registers into IOBs, 'o' will pack output registers, 'b' will pack both. 'n' will pack neither."));

        /*
         * Target Technology and Part Options
         */
        _NMRParameters
                .add(new FlaggedOption(PART)
                        .setStringParser(JSAP.STRING_PARSER)
                        .setRequired(JSAP.NOT_REQUIRED)
                        .setShortFlag('p')
                        .setLongFlag("part")
                        .setDefault("xcv1000fg680")
                        .setHelp(
                                "Target architecture for the triplicated design. Valid parts include all parts from the Virtex, Virtex2, and Virtex4 product lines. Not case-sensitive."));

        /*
         * Log, Report, and Configuration File Options
         */
        _NMRParameters
                .add(new FlaggedOption(LOG)
                        .setStringParser(JSAP.STRING_PARSER)
                        .setRequired(JSAP.NOT_REQUIRED)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("log")
                        .setDefault("nmr.log")
                        .setHelp(
                                "The name of the BLTmr log file, which records the options used along with the runtime details and results."));

        _NMRParameters
                .add(new FlaggedOption(DOMAIN_REPORT)
                        .setStringParser(JSAP.STRING_PARSER)
                        .setRequired(JSAP.NOT_REQUIRED)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("domainReport")
                        .setDefault("nmr_domain_report.txt")
                        .setHelp(
                                "The name of the domain report file. The domain report lists the domain (0, 1, or 2) of each cell instance in the resulting EDIF file."));

        /*
         * Configuration File Options
         */
        QualifiedSwitch q;

        q = (QualifiedSwitch) getByID(WRITE_CONFIG);
        unregisterParameter(q);
        _parameters.remove(q);
        _NMRParameters.add(q.setDefault("BLTmr.conf"));

        /*
         * Help and Version Information: Add short flags -h and -v
         */
        Switch sw; // Temp variable to reduce the amount of casting necessary

        sw = (Switch) getByID(HELP);
        unregisterParameter(sw);
        _parameters.remove(sw);
        sw.setShortFlag('h');
        _NMRParameters.add(sw);

        sw = (Switch) getByID(VERSION);
        unregisterParameter(sw);
        _parameters.remove(sw);
        sw.setShortFlag('v');
        _NMRParameters.add(sw);

        /*
         * Register the parameters. For more information, see the JSAP API.
         */
        for (Parameter p : _NMRParameters) {
            try {
                this.registerParameter(p);
                _parameters.add(p);
            } catch (JSAPException e) {
                System.err.println("Error while registering parameter " + p + ":\n" + e);
            }
        }

    }

    /*
     * For more information about the above parameters, see the documentation
     * (BLTmr.tex or BLTmr.pdf).
     */

    /**
     * Determine what utilization factor type is being used for partial TMR.
     * 
     * @see NMRUtilities.UtilizationFactor
     * @return A UtilizationFactor (enum) object representing the factor type
     * being used.
     */
    public UtilizationFactor getFactorType() {
        /*
         * Attempt to ascertain that the command parser has already parsed the
         * command-line arguments.
         */
        if (_result == null)
            throw new EdifRuntimeException(
                    "No JSAPResult object exists for this command parser.  Make sure that you call NMRCommandParser.parse() after creating the command parser.");

        NMRUtilities.UtilizationFactor type;
        String s = _result.getString(FACTOR_TYPE);

        if (s.compareToIgnoreCase(UtilizationFactor.ASUF.toString()) == 0)
            type = UtilizationFactor.ASUF;
        else if (s.compareToIgnoreCase(UtilizationFactor.UEF.toString()) == 0)
            type = UtilizationFactor.UEF;
        else if (s.compareToIgnoreCase(UtilizationFactor.DUF.toString()) == 0)
            type = UtilizationFactor.DUF;
        else
            // Should never get here.
            type = NMRUtilities.DEFAULT_UTILIZATION_FACTOR;
        return type;
    }

    /**
     * Modified from {@link JSAP#getUsage()} by delimiting parameters with a
     * newline rather than simply a space. Also includes a "concise usage"
     * string.
     * 
     * @return A String with both concise and verbose usage information.
     * @see JSAP#getUsage()
     */
    @Override
    public String getUsage() {
        String conciseUsage = "Usage:\n\n  java " + FlattenTMR.class.getName() + " INPUT_FILE [OPTIONS]\n\n  java "
                + FlattenTMR.class.getName() + " ";
        StringBuffer buf = new StringBuffer();
        for (Parameter param : _NMRParameters) {
            if (buf.length() > 0) {
                buf.append("\n   ");
            }
            buf.append(param.getSyntax());
        }
        String verboseUsage = buf.toString();
        return (conciseUsage + verboseUsage);
    }

    /**
     * For testing purposes only. Parses the command-line arguments, which
     * ensures that they are valid, and then simply prints a list of all the
     * arguments.
     * 
     * @param args Command-line arguments
     */
    public static void main(String[] args) {

        NMRCommandParser cp = new NMRCommandParser(3);
        JSAPResult result = cp.parse(args);

        System.out.println(FlattenTMR.getVersionInfo() + " " + FlattenTMR.REVISION);
        System.out.println("Congratulations! Successfully parsed all parameters:\n");

        // Print all parameters as lists [item1,item2,...,itemN]
        LinkedHashSet<Parameter> allParameters = new LinkedHashSet<Parameter>(_NMRParameters);

        allParameters.addAll(cp._parameters);
        for (Parameter p : allParameters) {
            String id = p.getID();
            Object[] obj = result.getObjectArray(id);
            /*
             * If it's a flagged Parameter, print the long flag rather than the
             * id.
             */
            if (p instanceof Flagged)
                System.out.println(((Flagged) p).getLongFlag() + ":\t\t" + Arrays.asList(obj));
            else
                System.out.println(id + ":\t\t" + Arrays.asList(obj));
        }

    }

    public boolean packInputRegisters() {
        /*
         * Attempt to ascertain that the command parser has already parsed the
         * command-line arguments.
         */
        if (_result == null)
            throw new EdifRuntimeException(
                    "No JSAPResult object exists for this command parser.  Make sure that you call NMRCommandParser.parse() after creating the command parser.");

        String packRegisters = _result.getString(PACK_IOB_REGISTERS);
        if (packRegisters.equalsIgnoreCase(INPUT) || packRegisters.equalsIgnoreCase(BOTH))
            return true;
        else
            return false;
    }

    public boolean packOutputRegisters() {
        /*
         * Attempt to ascertain that the command parser has already parsed the
         * command-line arguments.
         */
        if (_result == null)
            throw new EdifRuntimeException(
                    "No JSAPResult object exists for this command parser.  Make sure that you call NMRCommandParser.parse() after creating the command parser.");

        String packRegisters = _result.getString(PACK_IOB_REGISTERS);
        if (packRegisters.equalsIgnoreCase(OUTPUT) || packRegisters.equalsIgnoreCase(BOTH))
            return true;
        else
            return false;
    }

    /**
     * Overrides {@link JSAP#parse(String[])}
     * 
     * @param args Command-line arguments
     */
    @Override
    public JSAPResult parse(String[] args) {

        // Parse the arguments and store the result.
        _result = super.parse(args);

        // Check for --help switch.
        if (_result.getBoolean(HELP)) {
            System.out.println(getUsage());
            System.out.println();
            System.out.println("Options:");
            System.out.println();
            System.out.println(getHelp());
            System.out.println();
            System.out.println("For more detailed information, see " + _MORE_INFO + ".");
            System.out.println();
            System.out.println("Report bugs to " + _EMAIL_ADDRESS + ".");
            System.exit(0);
            // Check for --version switch.
        } else if (_result.getBoolean(VERSION)) {
            System.out.println(FlattenTMR.getVersionInfo());
            System.out.println();
            System.exit(0);
            // Check for --writeConfig Option.
        } else if (_result.userSpecified(WRITE_CONFIG)) {
            createConfigFile(_result.getString(WRITE_CONFIG));
            System.exit(0);
            // Check for --useConfig Option.
        } else if (_result.userSpecified(USE_CONFIG)) {
            /*
             * Register the user-specified configuration file as the *first*
             * default source. This will take precedence over the default
             * configuration files.
             */
            this.registerDefaultSource(new PropertyDefaultSource(_result.getString(USE_CONFIG), false));
            System.out.println("Using config file: " + _result.getString(USE_CONFIG));
        }

        // Register the default configuration files.
        this.registerDefaultSource(new PropertyDefaultSource(CONF_FILENAME, false));
        this.registerDefaultSource(new PropertyDefaultSource(ETC_FILENAME, false));

        /*
         * Re-parse with defaults from configuration files. `--help',
         * `--version', `--writeConfig', and `--useConfig' parameters will be
         * ignored.
         */
        _result = super.parse(args);

        if (!validateUtlizationFactorValues())
            _result.addException(null, new JSAPException("Failed to validate utilization factors."));

        validateNMRSuffixes();
        //if (!validateNMRSuffixes())
        //	_result.addException(null, new JSAPException(
        //	"Failed to validate NMR suffixes."));

        /*
         * If parsing failed, print specific error message(s) describing the
         * problem(s); then print usage information.
         */
        if (!_result.success()) {
            // If JSAP caught any errors, print them one by one
            for (java.util.Iterator errs = _result.getErrorMessageIterator(); errs.hasNext();) {
                System.err.println("Error: " + errs.next());
            }

            // Print usage information
            System.err.println();
            System.err.println(getUsage());
            System.err.println();
            System.err.println("For detailed usage, try `--help'");
            System.exit(1);
        }

        return _result;
    }

    /**
     * Determine if multiple EDIF creation has been activated. This is
     * determined by whether or not the {@link #LOW low} and {@link #HIGH high}
     * command-line parameters are set. Note that one parameter should not be
     * specified without the other; this is ensured by the
     * {@link NMRCommandParser} when the command-line parameters are parsed.
     * 
     * @return true if the user has activated multiple EDIF creation, false
     * otherwise.
     */
    public boolean usingMultipleEDIF() {
        /*
         * Attempt to ascertain that the command parser has already parsed the
         * command-line arguments.
         */
        if (_result == null)
            throw new EdifRuntimeException(
                    "No JSAPResult object exists for this command parser.  Make sure that you call NMRCommandParser.parse() after creating the command parser and before calling usingMultipleEDIF().");

        return _result.contains(LOW) && _result.contains(HIGH);
    }

    // //////////////////////////////////////////////////////////////////////
    // Protected Methods

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

    /**
     * Determine if the user has entered an invalid combination of a low and
     * high limits for multiple EDIF creation.
     * 
     * @return false if there is a problem (the user has specified a low without
     * a high, or vice versa). Otherwise return true.
     */
    protected boolean validateMultipleEDIF() {
        if (_result == null)
            return false;

        boolean valid;
        if (_result.contains(LOW) ^ _result.contains(HIGH)) {
            _result.addException(null, new JSAPException("In attempting to activate multiple EDIF "
                    + "creation, only one bound detected.  To use " + "multiple EDIF creation, both a low and "
                    + "a high bound must be set."));
            valid = false;
        } else
            valid = true;
        return valid;
    }

    /**
     * Parse the NMR suffixes to ensure they are valid. The rules are: 1. There
     * must be 'N' suffixes specified in the list 2. The 'N' suffixes must be
     * unique
     * 
     * @return true if the suffix list is valid, false if any are invalid.
     */
    protected boolean validateNMRSuffixes() {
        // Perform sanity checks before doing actual work.
        if (_result == null)
            return false;
        if (!_result.contains(NMR_SUFFIX))
            return true;

        /*
         * From this point on, assume success until one of the parameters fail
         * to parse.
         */
        boolean valid = true;

        String[] suffixes = _result.getStringArray(NMR_SUFFIX);

        // 1. There must be 'N' suffixes specified in the list
        if (suffixes.length != replicationFactor) {
            _result.addException(null, new JSAPException("Invalid suffix list. " + "The suffix array must have "
                    + replicationFactor + " elements."));
            valid = false;
        }

        // 2. The 'N' suffixes must be unique
        if (!NMREdifCell.areReplicationSuffixesUnique(suffixes, replicationFactor)) {
            _result.addException(null, new JSAPException("Invalid replicationSuffix list. "
                    + "All replication suffixes must be unique."));
            valid = false;
        }

        return valid;
    }

    /**
     * Parse the utilization factor values to ensure they are within the
     * appropriate range. Note that different factor types have different
     * ranges. For Single EDIF Creation, the value specified with the
     * <code>--factorValue</code> parameter is validated. For Multiple EDIF
     * Creation, both the <code>--low</code> and <code>--high</code> values
     * are validated.
     * 
     * @return true if the values are valid, false if any are invalid.
     */
    protected boolean validateUtlizationFactorValues() {
        // Perform sanity checks before doing actual work.
        if (_result == null)
            return false;
        if (!_result.contains(FACTOR_TYPE))
            return false;
        if (!validateMultipleEDIF()) {
            _result.addException(null, new JSAPException("Failed to validate Multiple EDIF Creation."));
            return false;
        }

        /*
         * From this point on, assume success until one of the parameters fail
         * to parse.
         */
        boolean valid = true;

        /*
         * Get the parameters to be validated. For Single EDIF Creation, this
         * will be the --factorValue parameter. For Multiple EDIF Creation, this
         * will be the --low and --high parameters.
         * 
         * Note that if the user activated Multiple EDIF Creation (by specifying
         * --low and --high, then the value of the --factorValue parameter will
         * be ignored.
         */
        Collection<String> parametersToValidate = new ArrayList<String>();
        if (usingMultipleEDIF()) {
            parametersToValidate.add(LOW);
            parametersToValidate.add(HIGH);
        } else {
            parametersToValidate.add(FACTOR_VALUE);
        }

        // Parse each parameter.
        for (String parameter : parametersToValidate) {
            try {
                _minZeroInclusive.parse(Double.toString(_result.getDouble(parameter)));
            } catch (ParseException e) {
                valid = false;
                _result.addException(null, e);
            }
        }

        return valid;
    }

    /**
     * What degree of NMR this object corresponds to (DWC=2, TMR=3, etc.)
     */
    protected int replicationFactor;

    /**
     * Primary configuration file
     */
    public static final String CONF_FILENAME = "BLTmr.conf";

    public static final String DIR = "dir";

    /**
     * "For more detailed information, see ..."
     * 
     * @see #parse(String[])
     */
    protected String _MORE_INFO = "<http://reliability.ee.byu.edu/>";

    public static final String DOMAIN_REPORT = "domainReport";

    public static final String DO_SCC_DECOMPOSITION = "doSCCDecomposition";

    /**
     * "Report bugs to ..."
     * 
     * @see #parse(String[])
     */
    protected String _EMAIL_ADDRESS = "<jcarroll@byu.net>";

    /**
     * Secondary configuration file
     */
    public static final String ETC_FILENAME = "/etc/BLTmr/BLTmr.conf";

    public static final String FACTOR_TYPE = "factorType";

    public static final String FACTOR_VALUE = "factorValue";

    public static final String FILE = "file";

    public static final String FULL_NMR = "full_nmr";

    public static final String HIGH = "high";

    /**
     * HalfLatch constant
     */
    public static final String HL_CONSTANT = "hlConst";

    /**
     * HalfLatch port name
     */
    public static final String HL_PORT_NAME = "hlPortName";

    /**
     * IOB Register packing option
     */
    public static final String PACK_IOB_REGISTERS = "packRegisters";

    public static final String INPUT = "i";

    public static final String OUTPUT = "o";

    public static final String BOTH = "b";

    public static final String NONE = "n";

    public static final String INC = "inc";

    public static final String INPUT_ADDITION_TYPE = "inputAdditionType";

    public static final String LOG = "logfile";

    public static final String LOW = "low";

    public static final String MERGE_FACTOR = "mergeFactor";

    public static final String NMR_SUFFIX = "nmrSuffix";

    public static final String NO_IN_OUT_CHECK = "noInoutCheck";

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

    public static final String OPTIMIZATION_FACTOR = "optimizationFactor";

    public static final String OUTPUT_ADDITION_TYPE = "outputAdditionType";

    public static final String PART = "part";

    public static final String REMOVE_HL = "removeHL";

    public static final String SCC_SORT_TYPE = "SCCSortType";

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

    /**
     * Remove IOBs from feedback (Don't count as feedback)
     */
    public static final String NO_IOB_FB = "no_iob_feedback";

    public static final String NMR_INPORTS = "nmr_inports";

    public static final String NMR_OUTPORTS = "nmr_outports";

    public static final String VERSION = "version";

    public static final String USE_BAD_CUT_CONN = "use_bad_cut_conn";

    public static final String HIGHEST_FANOUT_CUTSET = "highest_fanout_cutset";

    public static final String HIGHEST_FF_FANOUT_CUTSET = "highest_ff_fanout_cutset";

    public static final String NO_OBUFS = "no_obufs";

    /**
     * Range := [0,oo], where oo = Float.MAX_VALUE (nearly infinity) Use for all
     * of the {@linkplain UtilizationFactor utilization factors}: ASUF, UEF,
     * and DUF.
     */
    protected static BoundedDoubleStringParser _minZeroInclusive = BoundedDoubleStringParser.getParser(0.0, null);

    /**
     * Range := (0,oo] Not currently used
     */
    protected static BoundedDoubleStringParser _minZeroNonInclusive = BoundedDoubleStringParser.getParser(0.0, null,
            false, null);

    /**
     * Set := {1,2,3} Use for
     * {@linkplain #INPUT_ADDITION_TYPE inputAdditionType},
     * {@linkplain #OUTPUT_ADDITION_TYPE outputAdditionType}, and
     * {@linkplain #SCC_SORT_TYPE SCCSortType}.
     */
    protected static BoundedIntegerStringParser _oneTwoThree = BoundedIntegerStringParser.getParser(1, 3);

    /**
     * Ordered List of parameters to easily register them all or print them all
     */
    protected static LinkedHashSet<Parameter> _NMRParameters;

    /**
     * {@link JSAPResult} object used to store the parameters after being
     * parsed.
     */
    // protected JSAPResult _result;
    /**
     * Range := (0,1] Not currently used.
     */
    protected static BoundedDoubleStringParser _zeroToOneIncludeOne = BoundedDoubleStringParser.getParser(0.0, 1.0,
            false, true);

    /**
     * Range := [0,1] Use for {@linkplain #MERGE_FACTOR mergeFactor},
     * {@linkplain #OPTIMIZATION_FACTOR optimizationFactor}, and
     * {@linkplain #INC inc}.
     */
    protected static BoundedDoubleStringParser _zeroToOneInclusive = BoundedDoubleStringParser.getParser(0.0, 1.0);

    /**
     * Set := {0,1} Use for {@linkplain #HL_CONSTANT hlConst}.
     */
    protected static BoundedIntegerStringParser _zeroOne = BoundedIntegerStringParser.getParser(0, 1);
}
