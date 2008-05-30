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

public class JEdifClockDomainCommandGroup extends JEdifParserCommandGroup {

    public JEdifClockDomainCommandGroup() {
        super();
        /*
         * TODO: the next three ought to be taken from another command group -
         * it's redundant here
         * 
         * The MergeParserCommandGroup contains these types of options but it
         * makes the output parameter required. Maybe use that command group and
         * remove that stipulation here? How to go about doing that?
         * 
         */

        FlaggedOption outfile = new FlaggedOption(OUTPUT_FILE);
        outfile.setStringParser(JSAP.STRING_PARSER);
        outfile.setRequired(JSAP.NOT_REQUIRED);
        outfile.setShortFlag('o');
        outfile.setLongFlag(OUTPUT_FILE);
        outfile.setUsageName("output_file");
        outfile
                .setHelp("Filename and path to the summary generated by the clock domain analayzer. If no file is specified, summary is output to stdout.");
        this.addCommand(outfile);

        // TODO: implement these options in JEdifClockDomain

        //		FlaggedOption dir = new FlaggedOption(DIR);
        //		dir.setStringParser(JSAP.STRING_PARSER);
        //		dir.setRequired(JSAP.NOT_REQUIRED);
        //		dir.setShortFlag('d');
        //		dir.setLongFlag("dir");
        //		dir.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        //		dir.setList(JSAP.LIST);
        //		dir.setListSeparator(LIST_DELIMITER);
        //		dir.setHelp("Comma-separated list of directories containing external EDIF files to be included.");
        //		this.addCommand(dir);
        //		
        //		FlaggedOption file = new FlaggedOption(FILE);
        //		file.setStringParser(JSAP.STRING_PARSER);
        //		file.setRequired(JSAP.NOT_REQUIRED);
        //		file.setShortFlag('f');
        //		file.setLongFlag("file");
        //		file.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        //		file.setList(JSAP.LIST);
        //		file.setListSeparator(LIST_DELIMITER);
        //		file.setHelp("Similar to the previous option, but each external EDIF file is named explicitly--including the path to the file.");
        //		this.addCommand(file);

        FlaggedOption domain = new FlaggedOption(DOMAIN);
        domain.setStringParser(JSAP.STRING_PARSER);
        domain.setRequired(JSAP.NOT_REQUIRED);
        domain.setShortFlag(JSAP.NO_SHORTFLAG);
        domain.setLongFlag(DOMAIN);
        domain.setDefault("all");
        domain.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        domain.setList(JSAP.LIST);
        domain.setListSeparator(LIST_DELIMITER);
        domain.setHelp("A comma-separated list of domains to report.");
        this.addCommand(domain);

        Switch show_no_domain = new Switch(SHOW_NO_DOMAIN);
        show_no_domain.setShortFlag(JSAP.NO_SHORTFLAG);
        show_no_domain.setLongFlag(SHOW_NO_DOMAIN);
        show_no_domain.setDefault(FALSE);
        show_no_domain.setHelp("Add cells and nets not in a domain to the report.");
        this.addCommand(show_no_domain);

        FlaggedOption clk_cross = new FlaggedOption(SHOW_CLOCK_CROSSINGS);
        clk_cross.setStringParser(JSAP.STRING_PARSER);
        clk_cross.setRequired(JSAP.NOT_REQUIRED);
        clk_cross.setShortFlag(JSAP.NO_SHORTFLAG);
        clk_cross.setLongFlag(SHOW_CLOCK_CROSSINGS);
        clk_cross.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        clk_cross.setList(JSAP.LIST);
        clk_cross.setListSeparator(LIST_DELIMITER);
        clk_cross
                .setHelp("The domain crossing to report. This option must be followed by 2 comma-separated domains.  e.g. --show_clock_crossings clk1,clk2 or --show_clock_crossings clk1,all.");
        this.addCommand(clk_cross);

        FlaggedOption dotty_graph = new FlaggedOption(CREATE_DOTTY_GRAPH);
        dotty_graph.setStringParser(JSAP.STRING_PARSER);
        dotty_graph.setRequired(JSAP.NOT_REQUIRED);
        dotty_graph.setShortFlag(JSAP.NO_SHORTFLAG);
        dotty_graph.setLongFlag(CREATE_DOTTY_GRAPH);
        dotty_graph.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        dotty_graph.setList(JSAP.LIST);
        dotty_graph.setListSeparator(LIST_DELIMITER);
        dotty_graph
                .setHelp("The name of an EdifCellInstance to display in a graph.  Also required is the name of the port to show.  e.g.  --create_dotty_graph fd_inst,d");
        this.addCommand(dotty_graph);

        Switch show_cells = new Switch(SHOW_CELLS);
        show_cells.setShortFlag(JSAP.NO_SHORTFLAG);
        show_cells.setLongFlag(SHOW_CELLS);
        show_cells.setDefault(FALSE);
        show_cells.setHelp("Show listing of all EdifCellInstances in the domain(s) specified by --domain.");
        this.addCommand(show_cells);

        Switch show_nets = new Switch(SHOW_NETS);
        show_nets.setShortFlag(JSAP.NO_SHORTFLAG);
        show_nets.setLongFlag(SHOW_NETS);
        show_nets.setDefault(FALSE);
        show_nets.setHelp("Show listing of all EdifNets in the domain(s) specified by --domain.");
        this.addCommand(show_nets);

        Switch show_sync = new Switch(SHOW_SYNCHRONOUS);
        show_sync.setShortFlag(JSAP.NO_SHORTFLAG);
        show_sync.setLongFlag(SHOW_SYNCHRONOUS);
        show_sync.setDefault(FALSE);
        show_sync.setHelp("Show listing of all synchronous EdifCellInstances in the domain(s) specified by --domain.");
        this.addCommand(show_sync);

        Switch show_async = new Switch(SHOW_ASYNCHRONOUS);
        show_async.setShortFlag(JSAP.NO_SHORTFLAG);
        show_async.setLongFlag(SHOW_ASYNCHRONOUS);
        show_async.setDefault(FALSE);
        show_async
                .setHelp("Show listing of all asynchronous EdifCellInstances in the domain(s) specified by --domain.");
        this.addCommand(show_async);

        Switch show_gated_clocks = new Switch(SHOW_GATED_CLOCKS);
        show_gated_clocks.setShortFlag(JSAP.NO_SHORTFLAG);
        show_gated_clocks.setLongFlag(SHOW_GATED_CLOCKS);
        show_gated_clocks.setDefault(FALSE);
        show_gated_clocks.setHelp("Show listing of all clock domains not driven by a BUFG, DCM, or DLL.");
        this.addCommand(show_gated_clocks);

        Switch show_async_resets = new Switch(SHOW_ASYNCHRONOUS_RESETS);
        show_async_resets.setShortFlag(JSAP.NO_SHORTFLAG);
        show_async_resets.setLongFlag(SHOW_ASYNCHRONOUS_RESETS);
        show_async_resets.setDefault(FALSE);
        show_async_resets.setHelp("Show listing of all nets driving asynchronous reset ports.");
        this.addCommand(show_async_resets);

        Switch show_async_rst_cells = new Switch(SHOW_ASYNCHRONOUS_RESET_CELLS);
        show_async_rst_cells.setShortFlag(JSAP.NO_SHORTFLAG);
        show_async_rst_cells.setLongFlag(SHOW_ASYNCHRONOUS_RESET_CELLS);
        show_async_rst_cells.setDefault(FALSE);
        show_async_rst_cells
                .setHelp("Show listing of all nets driving asynchronous reset ports, along with the cells they drive.");
        this.addCommand(show_async_rst_cells);

        Switch do_scc_analysis = new Switch(DO_SCC_ANALYSIS);
        do_scc_analysis.setShortFlag(JSAP.NO_SHORTFLAG);
        do_scc_analysis.setLongFlag(DO_SCC_ANALYSIS);
        do_scc_analysis.setDefault(FALSE);
        do_scc_analysis.setHelp("Show report of all of the SCCs contained in the design.");
        this.addCommand(do_scc_analysis);

        Switch no_iob_feedback = new Switch(NO_IOB_FEEDBACK);
        no_iob_feedback.setShortFlag(JSAP.NO_SHORTFLAG);
        no_iob_feedback.setLongFlag(NO_IOB_FEEDBACK);
        no_iob_feedback.setDefault(FALSE);
        no_iob_feedback.setHelp("Exclude IOBs from feedback analaysis.");
        this.addCommand(no_iob_feedback);

    }

    public static boolean outputFile(JSAPResult result) {
        return result.contains(OUTPUT_FILE);
    }

    public static String getOutputFilename(JSAPResult result) {
        return result.getString(OUTPUT_FILE);
    }

    public static boolean clocksToShow(JSAPResult result) {
        return result.contains(DOMAIN);
    }

    public static String[] getClocksToShow(JSAPResult result) {
        return result.getStringArray(DOMAIN);
    }

    public static boolean showClockCrossings(JSAPResult result) {
        return result.contains(SHOW_CLOCK_CROSSINGS);
    }

    public static String[] getCrossingsList(JSAPResult result) {
        return result.getStringArray(SHOW_CLOCK_CROSSINGS);
    }

    public static boolean createDottyGraph(JSAPResult result) {
        return result.contains(CREATE_DOTTY_GRAPH);
    }

    public static String[] getDottyGraphList(JSAPResult result) {
        return result.getStringArray(CREATE_DOTTY_GRAPH);
    }

    public static boolean showGatedClocks(JSAPResult result) {
        return result.getBoolean(SHOW_GATED_CLOCKS);
    }

    public static boolean showNets(JSAPResult result) {
        return result.getBoolean(SHOW_NETS);
    }

    public static boolean showNoDomain(JSAPResult result) {
        return result.getBoolean(SHOW_NO_DOMAIN);
    }

    public static boolean showCells(JSAPResult result) {
        return result.getBoolean(SHOW_CELLS);
    }

    public static boolean showSynchronous(JSAPResult result) {
        return result.getBoolean(SHOW_SYNCHRONOUS);
    }

    public static boolean showAsynchronous(JSAPResult result) {
        return result.getBoolean(SHOW_ASYNCHRONOUS);
    }

    public static boolean showAsynchronousResets(JSAPResult result) {
        return result.getBoolean(SHOW_ASYNCHRONOUS_RESETS);
    }

    public static boolean showAsynchronousResetCells(JSAPResult result) {
        return result.getBoolean(SHOW_ASYNCHRONOUS_RESET_CELLS);
    }

    public static boolean doSCCAnalysis(JSAPResult result) {
        return result.getBoolean(DO_SCC_ANALYSIS);
    }

    public static boolean noIOBFeedback(JSAPResult result) {
        return result.getBoolean(NO_IOB_FEEDBACK);
    }

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

}
