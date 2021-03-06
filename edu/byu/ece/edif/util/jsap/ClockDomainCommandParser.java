/*
 * An argument parser for ClockDomainArchitectureImpl, based on JSAP.
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

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.martiansoftware.jsap.Flagged;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;

import edu.byu.ece.edif.util.clockdomain.ClockDomainParser;

/**
 * A command-line argument parser for ClockDomainArchitectureImpl, based on
 * {@link JSAP} (Java-based Simple Argument Parser). This class handles the
 * parsing of all command-line arguments, ensuring that they are syntactically
 * correct. It also provides automatically-formated usage and help information (<code>--help</code>
 * option).
 * 
 * @see DWCCommandParser
 * @see JSAP
 * @see JSAPCommandParser
 * @see <a href="http://www.martiansoftware.com/jsap/doc/javadoc/index.html">
 * JSAP API</a>
 * @see <a href="http://www.martiansoftware.com/jsap/">JSAP Homepage</a>
 * @author Kevin Lundgreen
 */

public class ClockDomainCommandParser extends JSAP {

    /**
     * Create a new ClockDomainCommandParser.
     */
    public ClockDomainCommandParser() {
        /*
         * Parent constructor sets up common Parameters such as --help,
         * --version, --writeConfig, --useConfig, etc.
         */
        super();
        _ClockAnalyzerParameters = new LinkedHashSet<Parameter>();

        /*
         * File options: input_file, output_file, etc.
         */
        _ClockAnalyzerParameters.add(new UnflaggedOption(INPUT_FILE).setStringParser(JSAP.STRING_PARSER).setRequired(
                JSAP.REQUIRED).setUsageName("input_file").setHelp(
                "Filename and path to the EDIF source file containing the top-level cell to be analyzed. Required."));

        _ClockAnalyzerParameters
                .add(new FlaggedOption(OUTPUT_FILE)
                        .setStringParser(JSAP.STRING_PARSER)
                        .setRequired(JSAP.NOT_REQUIRED)
                        .setShortFlag('o')
                        .setLongFlag("output")
                        .setUsageName("output_file")
                        .setHelp(
                                "Filename and path to the summary generated by the clock domain analayzer. If no file is specified, summary is output to stdout."));

        _ClockAnalyzerParameters.add(new FlaggedOption(DIR).setStringParser(JSAP.STRING_PARSER).setRequired(
                JSAP.NOT_REQUIRED).setShortFlag('d').setLongFlag("dir").setAllowMultipleDeclarations(
                JSAP.MULTIPLEDECLARATIONS).setList(JSAP.LIST).setListSeparator(LIST_DELIMITER).setHelp(
                "Comma-separated list of directories containing external EDIF files to be included."));

        _ClockAnalyzerParameters
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

        /*
         * Domain options
         */
        _ClockAnalyzerParameters.add(new FlaggedOption(DOMAIN).setStringParser(JSAP.STRING_PARSER).setRequired(
                JSAP.NOT_REQUIRED).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("domain").setDefault("all")
                .setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS).setList(JSAP.LIST).setListSeparator(
                        LIST_DELIMITER).setHelp("A comma-separated list of domains to report."));

        _ClockAnalyzerParameters.add(new Switch(SHOW_NO_DOMAIN).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag(
                "show_no_domain").setDefault(FALSE).setHelp("Add cells and nets not in a domain to the report."));

        _ClockAnalyzerParameters.add(new Switch(SHOW_CLOCK_CROSSINGS).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag(
        "show_clock_crossings").setDefault(FALSE).setHelp(
        "Show all clock domain crossings"));
        /*
        _ClockAnalyzerParameters
                .add(new FlaggedOption(SHOW_CLOCK_CROSSINGS)
                        .setStringParser(JSAP.STRING_PARSER)
                        .setRequired(JSAP.NOT_REQUIRED)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("show_clock_crossings")
                        .setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS)
                        .setList(JSAP.LIST)
                        .setListSeparator(LIST_DELIMITER)
                        .setHelp(
                                "The domain crossing to report. This option must be followed by 2 comma-separated domains.  e.g. --show_clock_crossings clk1,clk2 or --show_clock_crossings clk1,all."));
	*/
        
        _ClockAnalyzerParameters
                .add(new FlaggedOption(CREATE_DOTTY_GRAPH)
                        .setStringParser(JSAP.STRING_PARSER)
                        .setRequired(JSAP.NOT_REQUIRED)
                        .setShortFlag(JSAP.NO_SHORTFLAG)
                        .setLongFlag("create_dotty_graph")
                        .setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS)
                        .setList(JSAP.LIST)
                        .setListSeparator(LIST_DELIMITER)
                        .setHelp(
                                "The name of an EdifCellInstance to display in a graph.  Also required is the name of the port to show.  e.g.  --create_dotty_graph fd_inst,d"));

        _ClockAnalyzerParameters.add(new Switch(SHOW_CELLS).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("show_cells")
                .setDefault(FALSE).setHelp(
                        "Show listing of all EdifCellInstances in the domain(s) specified by --domain."));

        _ClockAnalyzerParameters.add(new Switch(SHOW_NETS).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("show_nets")
                .setDefault(FALSE).setHelp("Show listing of all EdifNets in the domain(s) specified by --domain."));

        _ClockAnalyzerParameters.add(new Switch(SHOW_SYNCHRONOUS).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag(
                "show_synchronous").setDefault(FALSE).setHelp(
                "Show listing of all synchronous EdifCellInstances in the domain(s) specified by --domain."));

        _ClockAnalyzerParameters.add(new Switch(SHOW_ASYNCHRONOUS).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag(
                "show_asynchronous").setDefault(FALSE).setHelp(
                "Show listing of all asynchronous EdifCellInstances in the domain(s) specified by --domain."));

        _ClockAnalyzerParameters.add(new Switch(SHOW_GATED_CLOCKS).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag(
                "show_gated_clocks").setDefault(FALSE).setHelp(
                "Show listing of all clock domains not driven by a BUFG, DCM, or DLL."));

        _ClockAnalyzerParameters.add(new Switch(SHOW_ASYNCHRONOUS_RESETS).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag(
                "show_asynchronous_resets").setDefault(FALSE).setHelp(
                "Show listing of all nets driving asynchronous reset ports."));

        _ClockAnalyzerParameters.add(new Switch(SHOW_ASYNCHRONOUS_RESET_CELLS).setShortFlag(JSAP.NO_SHORTFLAG)
                .setLongFlag("show_asynchronous_reset_cells").setDefault(FALSE).setHelp(
                        "Show listing of all nets driving asynchronous reset ports, along with the cells they drive."));

        _ClockAnalyzerParameters.add(new Switch(DO_SCC_ANALYSIS).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag(
                "do_scc_analysis").setDefault(FALSE).setHelp(
                "Show listing of all asynchronous EdifCellInstances in the domain(s) specified by --domain."));

        _ClockAnalyzerParameters.add(new Switch(NO_IOB_FEEDBACK).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag(
                "no_iob_feedback").setDefault(FALSE).setHelp("Exclude IOBs from feedback analaysis."));

        _ClockAnalyzerParameters.add(new Switch(HELP).setLongFlag("help").setShortFlag('h').setDefault(FALSE).setHelp(
                "Print this help message to stdout and exit."));

        _ClockAnalyzerParameters.add(new Switch(VERSION).setLongFlag("version").setShortFlag('v').setDefault(FALSE)
                .setHelp("Print version and copyright information to stdout and exit."));
        /*
         * Register the parameters. For more information, see the JSAP API.
         */
        for (Parameter p : _ClockAnalyzerParameters) {
            try {
                this.registerParameter(p);
                _ClockAnalyzerParameters.add(p);
            } catch (JSAPException e) {
                System.err.println("Error while registering parameter " + p + ":\n" + e);
            }
        }
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
        String conciseUsage = "Usage:\n\n  java " + ClockDomainParser.class.getName()
                + " INPUT_FILE [OPTIONS]\n\n  java " + ClockDomainParser.class.getName() + " ";
        StringBuffer buf = new StringBuffer();
        for (Parameter param : _ClockAnalyzerParameters) {
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

        ClockDomainCommandParser cp = new ClockDomainCommandParser();
        JSAPResult result = cp.parse(args);

        // Print all parameters as lists [item1,item2,...,itemN]
        LinkedHashSet<Parameter> allParameters = new LinkedHashSet<Parameter>(cp._ClockAnalyzerParameters);

        allParameters.addAll(cp._ClockAnalyzerParameters);
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

    /**
     * Overrides {@link JSAP#parse(String[])}
     * 
     * @param args Command-line arguments
     */
    @Override
    public JSAPResult parse(String[] args) {

        // Parse the arguments and store the result.
        _result = super.parse(args);

        // Ensure that all clock crossings have exactly 2 values (from, to)
        /*
        if (_result.contains(SHOW_CLOCK_CROSSINGS) && _result.getStringArray(SHOW_CLOCK_CROSSINGS).length % 2 != 0) {
            System.out.println("usage for --" + SHOW_CLOCK_CROSSINGS + " is:");
            System.out.println("--" + SHOW_CLOCK_CROSSINGS + " clock1,clock2");
            System.out.println("clock1 and clock2 are both required.  \"all\" can be used to choose all clocks.");
            System.exit(-1);
        }
        */
        if (_result.contains(CREATE_DOTTY_GRAPH) && _result.getStringArray(CREATE_DOTTY_GRAPH).length % 2 != 0) {
            System.out.println("usage for --" + CREATE_DOTTY_GRAPH + " is:");
            System.out.println("--" + CREATE_DOTTY_GRAPH + " cell,port_name");
            System.out.println("cell and port_name are both required.");
            System.exit(-1);
        }
        // Check for --help switch.
        if (_result.getBoolean(HELP)) {
            System.out.println(getUsage());
            System.out.println();
            System.out.println("Options:");
            System.out.println();
            System.out.println(getHelp());
            System.out.println();
            System.exit(0);
            // Check for --version switch.
        } else if (_result.getBoolean(VERSION)) {
            System.out.println(ClockDomainParser.getVersionInfo());
            System.out.println();
            System.exit(0);
            // Check for --writeConfig Option.
        }

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

    // //////////////////////////////////////////////////////////////////////
    // Protected Methods

    public static final String FALSE = "false";

    public static final String INPUT_FILE = "input";

    public static final String OUTPUT_FILE = "output";

    public static final char LIST_DELIMITER = ',';

    public static final String DIR = "dir";

    public static final String FILE = "file";

    public static final String DOMAIN = "domain";

    public static final String SHOW_NO_DOMAIN = "show_no_domain";

    public static final String SHOW_CLOCK_CROSSINGS = "show_clock_crossings";

    public static final String CREATE_DOTTY_GRAPH = "create_dotty_graph";

    public static final String SHOW_CELLS = "show_cells";

    public static final String SHOW_NETS = "show_nets";

    public static final String SHOW_SYNCHRONOUS = "show_synchronous";

    public static final String SHOW_ASYNCHRONOUS = "show_asynchronous";

    public static final String DO_SCC_ANALYSIS = "do_scc_analysis";

    public static final String NO_IOB_FEEDBACK = "no_iob_feedback";

    public static final String SHOW_GATED_CLOCKS = "show_gated_clocks";

    public static final String SHOW_ASYNCHRONOUS_RESETS = "show_asynchronous_resets";

    public static final String SHOW_ASYNCHRONOUS_RESET_CELLS = "show_asynchronous_reset_cells";

    public static final String HELP = "help";

    public static final String VERSION = "version";

    /**
     * Ordered List of parameters to easily register them all or print them all
     */
    protected LinkedHashSet<Parameter> _ClockAnalyzerParameters;

    /**
     * {@link JSAPResult} object used to store the parameters after being
     * parsed.
     */
    protected JSAPResult _result;
}
