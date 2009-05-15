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

import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import com.martiansoftware.jsap.Flagged;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;

import edu.byu.ece.edif.util.jsap.EdifCommandParser;

/**
 * @author Derrick Gibelyou
 */
public class ConfigFileCommandGroup extends AbstractCommandGroup {

    protected FlaggedOption write_config, use_config;

    protected static String _tool = "unknown";

    public ConfigFileCommandGroup(String tool) {
        _tool = tool;
        /*
         * Configuration File Options
         */
        write_config = new FlaggedOption(WRITE_CONFIG);
        write_config.setStringParser(JSAP.STRING_PARSER);
        write_config.setRequired(JSAP.NOT_REQUIRED);
        write_config.setLongFlag(WRITE_CONFIG);
        write_config.setUsageName("config_file");
        write_config.setHelp("Create a configuration file from the " + "current command-line options and exit.");
        this.addCommand(write_config);

        use_config = new FlaggedOption(USE_CONFIG);
        use_config.setStringParser(JSAP.STRING_PARSER);
        use_config.setRequired(JSAP.NOT_REQUIRED);
        use_config.setLongFlag(USE_CONFIG);
        use_config.setUsageName("config_file");
        use_config.setHelp("Load a configuration file to be used " + "as a set of default command-line parameters.");
        this.addCommand(use_config);

    }

    /**
     * Takes all the user set parameters and writes them to a config file, which
     * can be used later as a default set of parameters.
     * 
     * @param filename name of the file to write the configuration to
     * @param results the JSAPResults from parsing the command line
     * @return 0 on success
     */
    public static int createConfigFile(JSAPResult results, EdifCommandParser parser) {

        String filename = results.getString(ConfigFileCommandGroup.WRITE_CONFIG);
        Properties p = new Properties(); // properties object

        int ret = 0;//return value. 0 is success

        /*
         * Iterate through each parameter. Create a new property with the
         * parameter's id as the key and the parameter's value as the value. If
         * the parameter can be represented as an array of String objects,
         * iterate through them and create a list from the elements in the
         * array.
         */
        for (Parameter param : parser.getCommands()) {
            String id = param.getID();
            StringBuffer sb = new StringBuffer();
            String key, value;

            // If parameter isn't set by the user, skip it and move on.

            if (!results.userSpecified(id)) {
                continue;
            }

            // Is it an UnflaggedOption?
            if (param instanceof UnflaggedOption) {
                value = results.getString(id);
                key = id;
            }
            // Is it a Switch?
            else if (param instanceof Switch) {
                key = ((Flagged) param).getLongFlag();
                value = Boolean.toString(results.getBoolean(id));
            }
            // Must be a FlaggedOption or a QualifiedSwitch.
            else {
                key = ((Flagged) param).getLongFlag();
                Object[] array = results.getObjectArray(id);
                for (Object o : array) {
                    if (sb.length() > 0)
                        sb.append(LIST_DELIMITER);
                    // Double?
                    if (o instanceof Double)
                        sb.append(((Double) o).toString());
                    // Float?
                    else if (o instanceof Float)
                        sb.append(((Float) o).toString());
                    // Integer?
                    else if (o instanceof Integer)
                        sb.append(((Integer) o).toString());
                    // Must be a string of some kind...
                    else
                        sb.append(o);
                }
                value = sb.toString();
            }
            // Store the property for this parameter.
            p.setProperty(key, value);
        }

        try {

            p.store(new PrintStream(filename), filename + ", created by " + _tool);
        } catch (IOException e) {
            System.err.println(e);
            ret = 1;
        }
        return ret;
    }

    public static final String CONF_FILENAME = "config.conf";

    public static final char LIST_DELIMITER = ',';

    public static final String USE_CONFIG = "use_config";

    public static final String WRITE_CONFIG = "write_config";

}
