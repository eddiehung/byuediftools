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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.TMREdifCell;
import edu.byu.ece.edif.util.jsap.AbstractCommandGroup;

public class JEdifTMRParserCommandGroup extends AbstractCommandGroup {

    public JEdifTMRParserCommandGroup() {
        super();

        // New flags

        // Flag for indicating suffix of cells created during triplication
        FlaggedOption tmr_suffix_option = new FlaggedOption(TMR_SUFFIX);
        tmr_suffix_option.setStringParser(JSAP.STRING_PARSER);
        tmr_suffix_option.setRequired(JSAP.NOT_REQUIRED);
        tmr_suffix_option.setShortFlag(JSAP.NO_SHORTFLAG);
        tmr_suffix_option.setLongFlag(TMR_SUFFIX);
        tmr_suffix_option.setDefault(TMREdifCell._defaultReplicationSuffixes);
        tmr_suffix_option.setAllowMultipleDeclarations(true);
        tmr_suffix_option.setList(JSAP.LIST);
        tmr_suffix_option.setListSeparator(',');
        tmr_suffix_option.setUsageName("suffix");
        tmr_suffix_option.setHelp("Comma-separated list of the three suffixes to use when replicating components.");
        this.addCommand(tmr_suffix_option);

        // Flag for specifying the name of the new flattened cell
        // TODO: This should probably be part of a different command group. I
        // can see other cases when the user should specify the resulting cell.
        FlaggedOption tmr_cell_name = new FlaggedOption(TMR_CELL_NAME);
        tmr_cell_name.setStringParser(JSAP.STRING_PARSER);
        tmr_cell_name.setRequired(JSAP.NOT_REQUIRED);
        tmr_cell_name.setLongFlag(TMR_CELL_NAME);
        tmr_cell_name.setHelp("Specifies the name of the TMR'd cell");
        this.addCommand(tmr_cell_name);

        // Flag for specifying the domain remport
        FlaggedOption domain_report_option = new FlaggedOption(DOMAIN_REPORT);
        domain_report_option.setStringParser(JSAP.STRING_PARSER);
        domain_report_option.setRequired(JSAP.NOT_REQUIRED);
        domain_report_option.setShortFlag(JSAP.NO_SHORTFLAG);
        domain_report_option.setLongFlag(DOMAIN_REPORT);
        domain_report_option.setDefault("domain_report.txt");
        domain_report_option
                .setHelp("The name of the domain report file. The domain report lists the domain (0, 1, or 2) of each cell instance in the resulting EDIF file.");
        this.addCommand(domain_report_option);

        // Flag for deciding whether .edif or .jedif is output
        Switch edif_flag = new Switch(GENERATE_EDIF_FLAG);
        edif_flag.setShortFlag(JSAP.NO_SHORTFLAG);
        edif_flag.setLongFlag(GENERATE_EDIF_FLAG);
        edif_flag.setDefault("false");
        edif_flag.setHelp("Generate EDIF output instead of .jedif");
        this.addCommand(edif_flag);

        FlaggedOption ports_file_option = new FlaggedOption(TMR_PORTS_FILE);
        ports_file_option.setStringParser(JSAP.STRING_PARSER);
        ports_file_option.setRequired(JSAP.NOT_REQUIRED);
        ports_file_option.setShortFlag(JSAP.NO_SHORTFLAG);
        ports_file_option.setLongFlag(TMR_PORTS_FILE);
        ports_file_option.setHelp("The name of the tmr_port file. It consists of a list of ports "
                + "to triplicate followed by three names for the new ports, this format:\n" + "clk:clk0,clk1,clk2\n"
                + "rst:rst0,rst1,rst2\n\n");
        this.addCommand(ports_file_option);

    }

    /**
     * Returns a valid EdifNameable object from the parameter. If the parameter
     * is not set, return null.
     * 
     * @param result
     * @return
     * @throws InvalidEdifNameException
     */
    public static EdifNameable getCellName(JSAPResult result) throws InvalidEdifNameException {
        if (!result.contains(TMR_CELL_NAME))
            return null;
        String name = result.getString(TMR_CELL_NAME);
        return new NamedObject(name);
    }

    public static String getDomainReportFilename(JSAPResult result) {
        return result.getString(DOMAIN_REPORT);
    }

    public static String[] getTMRSuffix(JSAPResult result) {
        return result.getStringArray(TMR_SUFFIX);
    }

    public static Map<String, String[]> getUserTMRPorts(JSAPResult result) {
        Map<String, String[]> prePorts = new LinkedHashMap<String, String[]>();
        if (result.userSpecified(TMR_PORTS_FILE)) {
            String filename = result.getString(TMR_PORTS_FILE);
            try {
                FileReader fileReader = new FileReader(filename);
                BufferedReader file = new BufferedReader(fileReader);
                while (true) {
                    String line = file.readLine();
                    if (line == null)
                        break;
                    StringTokenizer st = new StringTokenizer(line, ":, ");
                    String orig = st.nextToken();
                    String[] trip = new String[3];
                    int i = 0;
                    while (st.hasMoreTokens()) {
                        trip[i++] = st.nextToken();
                    }
                    prePorts.put(orig, trip);
                }

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }

        //		String[] tmp = { "clk1", "clk2", "clk3" };
        //        prePorts.put("clk", tmp);

        return prePorts;
    }

    public static final String DOMAIN_REPORT = "tmr_domain_report";

    public static final String TMR_PORTS_FILE = "tmr_ports_file";

    public static final String TMR_SUFFIX = "tmr_suffix";

    public static final String TMR_CELL_NAME = "tmr_cell_name";

    public static final String GENERATE_EDIF_FLAG = "edif";

}
