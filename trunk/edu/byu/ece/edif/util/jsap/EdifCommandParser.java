/*
 * A standard command line parser for the EDIF infrastructure.
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

import java.io.PrintStream;
import java.util.LinkedHashSet;
import java.util.Set;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.defaultsources.PropertyDefaultSource;

import edu.byu.ece.edif.jedif.EDIFMain;
import edu.byu.ece.edif.util.jsap.commandgroups.CommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ConfigFileCommandGroup;

/**
 * A standard command line parser for the EDIF infrastructure, using the
 * {@link JSAP} (Java-based Simple Argument Parser) with additional
 * functionality.
 * <p>
 * TODO:
 * <ul>
 * <li> Provide way to specify the command line name as a part of the usage
 * (i.e. somehow tie back to the executable program to get the full java
 * pathname and put this name into the usage)
 * <li> Add a default --help command that prints all of the program switches
 * (allow this --help to be disabled?)
 * <li> getting several errors for a bad flag like "blah"
 * <li> Add James' config file parser (see NMRCommandParser)
 * </ul>
 * 
 * @see <a href="http://www.martiansoftware.com/jsap/doc/javadoc/index.html">
 * JSAP API</a>
 * @see <a href="http://www.martiansoftware.com/jsap/">JSAP Homepage</a>
 * @see JSAP
 */
public class EdifCommandParser extends JSAP {

    public EdifCommandParser() {
        super();
        _commands = new LinkedHashSet<Parameter>();

        try {
            this.registerParameter(new Switch(HELP).setLongFlag("help").setDefault("false").setShortFlag('h').setHelp(
                    "Print this help message to stdout and exit."));
            this.registerParameter(new Switch(VERSION).setLongFlag("version").setDefault("false").setShortFlag('v')
                    .setHelp("Print version and copyright information to stdout and exit."));
        } catch (JSAPException e) {
            System.err.println("Error while registering parameter help and version:\n" + e);
        }

    }

    /**
     * Parses the command line, and looks for options that will kill the program
     * (help, version, and write_config_file)
     * 
     * @param args
     * @param err
     * @return
     */
    public JSAPResult parse(String[] args, PrintStream err) {
        JSAPResult result = super.parse(args);
        if (result.getBoolean("help")) {
            err.println(getUsage() + "\n");
            err.println("Options:\n" + getHelp() + "\n");
            //out.println("For more detailed information, see "+ _MORE_INFO + ".");
            //throw help argument exception?
            System.exit(0);
            // Check for --version switch.
        } else if (result.getBoolean("version")) {
            err.println(getVersionInfo() + "\n");
            //throw help argument exception?
            System.exit(0);
        }
        // Check for --writeConfig Option.
        else if (result.userSpecified(ConfigFileCommandGroup.WRITE_CONFIG)) {
            ConfigFileCommandGroup.createConfigFile(result, this);
            //throw exception? or exit()?
            System.exit(0);
            // Check for --useConfig Option.
        } else if (result.userSpecified(ConfigFileCommandGroup.USE_CONFIG)) {

            //Register the user-specified configuration file as the *first*
            //default source. This will take precedence over the default
            //configuration files.
            this.registerDefaultSource(new PropertyDefaultSource(result.getString(ConfigFileCommandGroup.USE_CONFIG),
                    false));
            err.println("Using Config File: " + result.getString(ConfigFileCommandGroup.USE_CONFIG));
        }
        // Register the default configuration files.

        //Re-parse with defaults from configuration files. `--help',`--version',
        // `--writeConfig', and `--useConfig' parameters will be ignored.
        result = this.parse(args);
        if (!result.success()) {
            // If JSAP caught any errors, print them one by one
            for (java.util.Iterator<String> errs = result.getErrorMessageIterator(); errs.hasNext();) {
                err.println("Error: " + errs.next());
            }

            // Print usage information
            err.println("Usage:" + getUsage());
        }

        return result;
    }

    public static String getVersionInfo() {
        return "BYU EDIF Tools version " + EDIFMain.getExtendedVersionString() + ", " + EDIFMain.VERSION_DATE;
    }

    public void addCommands(CommandGroup group) {

        for (Parameter p : group.getCommands()) {
            try {
                this.registerParameter(p);
                _commands.add(p);
            } catch (JSAPException e) {
                System.err.println("Error while registering parameter " + p + ":\n" + e);
            }
        }
    }

    public Set<Parameter> getCommands() {
        return _commands;
    }

    private LinkedHashSet<Parameter> _commands;

    static final String HELP = "help";

    static final String VERSION = "version";

}
