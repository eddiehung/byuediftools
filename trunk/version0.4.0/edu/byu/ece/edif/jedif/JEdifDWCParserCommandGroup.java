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
import edu.byu.ece.edif.tools.replicate.ijmr.TMRDWCEdifCell;
import edu.byu.ece.edif.util.jsap.AbstractCommandGroup;

public class JEdifDWCParserCommandGroup extends AbstractCommandGroup {

    public JEdifDWCParserCommandGroup() {
        super();

        // New flags
        String[] defaultSuffixes = new String[2];
        TMRDWCEdifCell.DEFAULT_SUFFIXES.get(2).toArray(defaultSuffixes);

        // Flag for indicating suffix of cells created during duplication
        FlaggedOption tmr_suffix_option = new FlaggedOption(DWC_SUFFIX);
        tmr_suffix_option.setStringParser(JSAP.STRING_PARSER);
        tmr_suffix_option.setRequired(JSAP.NOT_REQUIRED);
        tmr_suffix_option.setShortFlag(JSAP.NO_SHORTFLAG);
        tmr_suffix_option.setLongFlag(DWC_SUFFIX);
        tmr_suffix_option.setDefault(defaultSuffixes);
        tmr_suffix_option.setAllowMultipleDeclarations(true);
        tmr_suffix_option.setList(JSAP.LIST);
        tmr_suffix_option.setListSeparator(',');
        tmr_suffix_option.setUsageName("suffix");
        tmr_suffix_option.setHelp("Comma-separated list of the two suffixes to use when replicating components.");
        this.addCommand(tmr_suffix_option);

        // Flag for specifying the name of the new flattened cell
        // TODO: This should probably be part of a different command group. I
        // can see other cases when the user should specify the resulting cell.
        FlaggedOption tmr_cell_name = new FlaggedOption(DWC_CELL_NAME);
        tmr_cell_name.setStringParser(JSAP.STRING_PARSER);
        tmr_cell_name.setRequired(JSAP.NOT_REQUIRED);
        tmr_cell_name.setLongFlag(DWC_CELL_NAME);
        tmr_cell_name.setHelp("Specifies the name of the duplicated cell");
        this.addCommand(tmr_cell_name);

        // Flag for specifying the domain remport
        FlaggedOption domain_report_option = new FlaggedOption(DOMAIN_REPORT);
        domain_report_option.setStringParser(JSAP.STRING_PARSER);
        domain_report_option.setRequired(JSAP.NOT_REQUIRED);
        domain_report_option.setShortFlag(JSAP.NO_SHORTFLAG);
        domain_report_option.setLongFlag(DOMAIN_REPORT);
        domain_report_option.setDefault("domain_report.txt");
        domain_report_option
                .setHelp("The name of the domain report file. The domain report lists the domain (0 or 1) of each cell instance in the resulting EDIF file.");
        this.addCommand(domain_report_option);

        // Flag for deciding whether .edif or .jedif is output
        Switch edif_flag = new Switch(GENERATE_EDIF_FLAG);
        edif_flag.setShortFlag(JSAP.NO_SHORTFLAG);
        edif_flag.setLongFlag(GENERATE_EDIF_FLAG);
        edif_flag.setDefault("false");
        edif_flag.setHelp("Generate EDIF output instead of .jedif");
        this.addCommand(edif_flag);

        FlaggedOption ports_file_option = new FlaggedOption(DWC_PORTS_FILE);
        ports_file_option.setStringParser(JSAP.STRING_PARSER);
        ports_file_option.setRequired(JSAP.NOT_REQUIRED);
        ports_file_option.setShortFlag(JSAP.NO_SHORTFLAG);
        ports_file_option.setLongFlag(DWC_PORTS_FILE);
        ports_file_option.setHelp("The name of the dwc_port file. It consists of a list of ports "
                + "to duplicate followed by two names for the new ports, this format:\n" + "clk:clk0,clk1\n"
                + "rst:rst0,rst1\n\n");
        this.addCommand(ports_file_option);

        /*
         * DWC Comparator options
         */
        Switch use_drc = new Switch(USE_DRC);
        use_drc.setShortFlag(JSAP.NO_SHORTFLAG);
        use_drc.setLongFlag("use_drc");
        use_drc.setDefault(FALSE);
        use_drc
                .setHelp("Use a dual rail checker for comparison of the duplicated circuit; Default is a single rail checker.");
        this.addCommand(use_drc);

        Switch pers_comp = new Switch(PERS_COMP);
        pers_comp.setShortFlag(JSAP.NO_SHORTFLAG);
        pers_comp.setLongFlag(PERS_COMP);
        pers_comp.setDefault(FALSE);
        pers_comp
                .setHelp("Add comparators checking persistent sections of the design. These comparators will output to a separate persistent error line (or a dual rail error line, depending on the --use_drc option.)");
        this.addCommand(pers_comp);

        Switch reg_dect = new Switch(REGISTER_DETECTION);
        reg_dect.setShortFlag(JSAP.NO_SHORTFLAG);
        reg_dect.setLongFlag(REGISTER_DETECTION);
        reg_dect.setDefault(FALSE);
        reg_dect.setHelp("Register the detection signals before they go to the outputs.");
        this.addCommand(reg_dect);

        Switch pack_dect_regs = new Switch(PACK_DETECTION_REGS);
        pack_dect_regs.setShortFlag(JSAP.NO_SHORTFLAG);
        pack_dect_regs.setLongFlag(PACK_DETECTION_REGS);
        pack_dect_regs.setDefault(FALSE);
        pack_dect_regs.setHelp("Pack the detection registers into IOBs (if detection signals are registered.)");
        this.addCommand(pack_dect_regs);

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
        if (!result.contains(DWC_CELL_NAME))
            return null;
        String name = result.getString(DWC_CELL_NAME);
        return new NamedObject(name);
    }

    public static String getDomainReportFilename(JSAPResult result) {
        return result.getString(DOMAIN_REPORT);
    }

    public static String[] getTMRSuffix(JSAPResult result) {
        return result.getStringArray(DWC_SUFFIX);
    }

    public static Map<String, String[]> getUserTMRPorts(JSAPResult result) {
        Map<String, String[]> prePorts = new LinkedHashMap<String, String[]>();
        if (result.userSpecified(DWC_PORTS_FILE)) {
            String filename = result.getString(DWC_PORTS_FILE);
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

    public static final String DOMAIN_REPORT = "dwc_domain_report";

    public static final String DWC_PORTS_FILE = "dwc_ports_file";

    public static final String DWC_SUFFIX = "dwc_suffix";

    public static final String DWC_CELL_NAME = "dwc_cell_name";

    public static final String GENERATE_EDIF_FLAG = "edif";

    public static final String USE_DRC = "use_drc";

    public static final String PERS_COMP = "persistent_comparators";

    public static final String REGISTER_DETECTION = "register_detection";

    public static final String PACK_DETECTION_REGS = "pack_detection_registers";

    public static final String TRUE = "true";

    public static final String FALSE = "false";

}
