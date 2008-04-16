/*
 * Encapsulation for 5 PrintStreams: Error, Warning, Normal, Logfile, Debug.
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
package edu.byu.ece.edif.tools;

import java.io.PrintStream;

/**
 * Encapsulation class for 5 {@link PrintStream} objects: Error, Warning,
 * Normal, Logfile, Debug.
 * <p>
 * Allows the user to specify the verbosity level via the command line.
 * Redirects System.out and System.err to special private class that prints to
 * both the logfile (if any) and the System stream.
 * 
 * @author dsgib
 */
public class LogFile {

    private static LogFile logger = new LogFile();

    public static PrintStream debug() {
        return logger._debug;
    }

    public static PrintStream log() {
        return logger._log;
    }

    public static PrintStream out() {
        return logger._out;
    }

    public static PrintStream warn() {
        return logger._warn;
    }

    public static PrintStream err() {
        return logger._err;
    }

    /**
     * Initializes a logfile object
     * 
     * @param debug: stream to use for debugging info (can be null)
     * @param log: stream to send logfile info to (can be null)
     * @param log_level: the user specified log level: 1-5. 5 for printing debug
     * to stdout, 1 for only errors to stdout
     */
    public static void InitializeLog(PrintStream debug, PrintStream log, int log_level) {
        //if(logger == null)
        //	logger = new LogFile();
        logger.setDebug(debug);
        logger.setLog(log, log_level);

        //cover for legacy code.
        System.setOut(logger._out);
        System.setErr(logger._err);

        //return logger; 
    }

    private LogFile() {
        _LogLevel = 3;
        _log = _null;
        _debug = _null;
        _out = new outStream(_log, System.out, STD);
        _warn = new outStream(_log, System.err, WARN);
        _err = new outStream(_log, System.err, ERR);
    }

    public static LogFile getInstance() {
        return logger;
    }

    private void setDebug(PrintStream debug) {
        if (debug == null)
            _debug = _null;
        else
            _debug = new outStream(debug, System.out, DEBUG);

    }

    private void setLog(PrintStream log, int log_level) {

        _LogLevel = log_level;

        if (log == null)
            _log = _null;
        else
            _log = new outStream(log, System.out, LOG);

        _err = System.err;
        _warn = _null;
        _out = _null;

        switch (log_level) {
        default:
        case STD:
            _out = new outStream(log, System.out, STD);
        case WARN:
            _warn = new outStream(log, System.err, WARN);
        case ERR:
            _err = new outStream(log, System.err, ERR);
        }
    }

    protected PrintStream _err, _warn, _out, _log, _debug;

    protected PrintStream _null = new java.io.PrintStream(new java.io.OutputStream() {
        public void write(int b) {
        }
    });

    public static final int DEBUG = 5;

    public static final int LOG = 4;

    public static final int STD = 3;

    public static final int WARN = 2;

    public static final int ERR = 1;

    protected int _LogLevel;

    //	public static enum logLevel {Debug,Log,Std,Warn,Err};

    class outStream extends PrintStream {
        private PrintStream _log;

        private PrintStream _out;

        private int _local_level;

        public outStream(PrintStream log, PrintStream out, int local_level) {
            super(System.out);
            _log = log;
            _out = out;
            _local_level = local_level;
            // TODO Auto-generated constructor stub
        }

        @Override
        public void println() {
            _log.println();
            if (_local_level <= _LogLevel)
                _out.println();
        }

        @Override
        public void println(String s) {
            _log.println(s);
            if (_local_level <= _LogLevel)
                _out.println(s);
        }

        @Override
        public void print(String s) {
            _log.println(s);
            if (_local_level <= _LogLevel)
                _out.println(s);
        }

    }
}
