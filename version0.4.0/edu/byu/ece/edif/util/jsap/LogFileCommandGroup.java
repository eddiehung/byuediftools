/*
 * Commandline options for logfile creation and display
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

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Set;

import com.martiansoftware.jsap.Flagged;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.QualifiedSwitch;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;

import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.replicate.nmr.NMRUtilities;

/**
 * Parses options for a Logfile, and provides several levels of output so the
 * user can choose what level of output to see
 * 
 * @author Derrick Gibelyou
 */
public class LogFileCommandGroup extends AbstractCommandGroup {
    public LogFileCommandGroup() {
        this("bltmr.log");
    }

    public LogFileCommandGroup(String logfile) {

        _log_file = new FlaggedOption(LOG_FILE_OPTION);
        _log_file.setStringParser(JSAP.STRING_PARSER);
        _log_file.setRequired(JSAP.NOT_REQUIRED);
        _log_file.setDefault(logfile);
        _log_file.setLongFlag(LOG_FILE_OPTION);
        _log_file.setUsageName("logfile");
        _log_file.setHelp("Specifies a file for logging output");
        this.addCommand(_log_file);

        _debug_file = new QualifiedSwitch(DEBUG_OPTION);
        _debug_file.setStringParser(JSAP.STRING_PARSER);
        _debug_file.setRequired(JSAP.NOT_REQUIRED);
        _debug_file.setLongFlag(DEBUG_OPTION);
        _debug_file.setUsageName("debug_log");
        _debug_file.setHelp("Specifies a file for logging the debuggin output, or "
                + "if no file specified, to send debuging output to the log file.");
        this.addCommand(_debug_file);

        _verbose = new FlaggedOption(VERBOSE_OPTION);
        _verbose.setStringParser(_oneFive);
        _verbose.setShortFlag('V');
        _verbose.setLongFlag(VERBOSE_OPTION);
        _verbose.setDefault("3");
        _verbose.setUsageName("{1|2|3|4|5}");
        _verbose.setAllowMultipleDeclarations(true);
        _verbose.setHelp("sets the verbosity level for standard out."
                + " 1 prints only errors, 2 warnings, 3 normal, 4 prints log"
                + " messages to stdout. 5 prints debug messages to stdout.");
        this.addCommand(_verbose);

        _append_log_file = new Switch(APPEND_LOG_OPTION);
        _append_log_file.setLongFlag(APPEND_LOG_OPTION);

        _append_log_file.setHelp("Use this switch to append to " + "the log file, instead of replacing it");
        this.addCommand(_append_log_file);

    }

    public static boolean getLogging(JSAPResult result) {
        return result.userSpecified(LOG_FILE_OPTION);
    }

    public static void CreateLog(JSAPResult result) {
        //creates a new print stream with an OutputStream that writes nothing.
        int log_level = result.getInt(VERBOSE_OPTION);
        String logfile, debugfile;
        boolean useDebug;
        boolean append = result.getBoolean(APPEND_LOG_OPTION);
        logfile = result.getString(LOG_FILE_OPTION);

        PrintStream log = null, debug = null;
        try {
            log = new PrintStream(new FileOutputStream(logfile, append));
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }

        //debug_file.
        debugfile = result.getQualifiedSwitchValue(DEBUG_OPTION);
        useDebug = result.getBoolean(DEBUG_OPTION);
        if (useDebug || log_level == LogFile.DEBUG) {
            if (debugfile == null) {
                debug = log;
            } else {
                try {
                    debug = new PrintStream(new FileOutputStream(debugfile));
                } catch (Exception e) {
                    System.err.println(e);
                    e.printStackTrace();
                }
            }
        } else {
            debug = null;
        }
        //return 
        LogFile.InitializeLog(debug, log, log_level);
    }

    /**
     * @param startTime
     * @return new start time
     */
    public static long reportTime(long startTime, String description, PrintStream out) {
        long elapsedTime;
        elapsedTime = System.currentTimeMillis() - startTime;
        out.println("TIMESTAMP: " + description + " took " + NMRUtilities.msToString(elapsedTime));
        startTime = System.currentTimeMillis();
        return startTime;
    }

    public static void logOptions(Set<Parameter> params, JSAPResult results) {
        // loop through the options and write them to the log file.
        for (Parameter param : params) {
            String id = param.getID();
            StringBuffer sb = new StringBuffer();
            String key, value;

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
                        sb.append(',');
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
            if (!value.equals(""))
                LogFile.log().println(key + "=" + value);
        }
    }

    protected FlaggedOption _log_file;

    protected QualifiedSwitch _debug_file;

    protected FlaggedOption _verbose;

    protected Switch _append_log_file;

    //protected QualifiedSwitch _verbose,_quiet;

    protected static final String APPEND_LOG_OPTION = "append_log";

    protected static final String LOG_FILE_OPTION = "log";

    protected static final String VERBOSE_OPTION = "verbose";

    protected static final String DEBUG_OPTION = "debug";

    protected static BoundedIntegerStringParser _oneFive = BoundedIntegerStringParser.getParser(1, 5);
}
