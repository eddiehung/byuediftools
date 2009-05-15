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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

/**
 * Encapsulation class for 5 {@link PrintStream} objects: Error, Warning,
 * Normal, Logfile, Debug.
 * <p>
 * Allows the user to specify the verbosity level via the command line.
 * Redirects System.out and System.err to special private class that prints to
 * both the logfile (if any) and the System stream.
 * </p>
 * <p>
 * The streams can be accessed similar to stdout and stderr. Instead of <br>
 * System.out.println();<br>
 * the command is <br>
 * LogFile.out().println();<br>
 * This is because Java doesn't support read-only public variables.
 * 
 * @author Derrick Gibelyou
 */
public class LogFile {
    /**
     * This is a singleton class. However, I need to ensure that the streams are
     * setup to a default, in case somebody tries to print without first setting
     * things up.
     */
    private static LogFile logger = new LogFile();

    public static LogFile getInstance() {
        return logger;
    }

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

    public static final int DEBUG = 5;

    public static final int LOG = 4;

    public static final int STD = 3;

    public static final int WARN = 2;

    public static final int ERR = 1;

    protected int _LogLevel;

    protected static PrintStream stdout = System.out;

    protected static PrintStream stderr = System.err;

    private static void testStreams(PrintStream out, PrintStream err) {
        stdout = out;
        stderr = err;
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
        //logger.setDebug(debug);
        logger.setLogs(debug, log, log_level);

        //cover for legacy code.
        coverLegacyCode();
    }

    public static void coverLegacyCode() {
        System.setOut(logger._out);
        System.setErr(logger._err);
    }

    public static void restoreSystemStreams() {
        System.setOut(stdout);
        System.setErr(stderr);
    }

    /**
     * Default to no log and no debug using a special /dev/nul PrintStream,
     * default to stdout, warning and errors to stderr
     */
    private LogFile() {
        _LogLevel = 3;
        _log = _null;
        _debug = _null;
        _out = new outStream(_log, System.out, STD);
        _warn = new outStream(_log, System.err, WARN);
        _err = new outStream(_log, System.err, ERR);
    }

    private void setLogs(PrintStream debug, PrintStream log, int log_level) {

        _LogLevel = log_level;

        _err = stderr;
        _warn = _null;
        _out = _null;

        /*
         * Right now only Errors will be printed. However, we will Change that
         * based on the user input. By default we will print everything to the
         * screen. If the user wants to mask normal output or warning, we will
         * keep those streams pointed to /dev/nul.
         */
        /*
         * Whether or not to print log and debug to the screen is controlled by
         * the overridden PrintStreams _log and _debug.
         */
        switch (log_level) {
        default:
        case STD:
            _out = new outStream(log, stdout, STD);
        case WARN:
            _warn = new outStream(log, stderr, WARN);
        case ERR:
            _err = new outStream(log, stderr, ERR);
        }
        /*
         * Reassign _debug and _log so that if the verbosity level says "display
         * log and debug info" then it will get printed to stdout, but not to a
         * log file (because the log file was specified to be null). We have to
         * do this after assigning _out, because _out will try to print to _log
         * first, which might cause duplicate printing.
         */
        if (debug == null)
            _debug = new outStream(_null, stdout, DEBUG);
        else
            _debug = new outStream(debug, stdout, DEBUG);

        if (log == null)
            _log = new outStream(_null, stdout, LOG);
        else
            _log = new outStream(log, stdout, LOG);

    }

    protected PrintStream _err, _warn, _out, _log, _debug;

    /**
     * Special anonymous inner class that doesn't write to anything. This is a
     * platform independent /dev/null
     */
    protected PrintStream _null = new java.io.PrintStream(new java.io.OutputStream() {
        public void write(int b) {
        }
    });

    private static boolean test(String teststr, PrintStream sut, BufferedReader readStream, BufferedReader stdStream,
            boolean checkSTD) throws Exception {
        boolean pass = true;
        sut.println(teststr);
        if (readStream != null) {
            if (readStream.readLine().equals(teststr)) {
                if (checkSTD && stdStream.readLine().equals(teststr)) {
                    //good
                    pass = true;
                } else {
                    //fail
                    pass = false;
                    throw new Exception("Wrong Value");
                }
            }
        }
        return pass;
    }


    /**
     * Prints one thing to each stream, and tests to see if it arrived and
     * didn't arrive appropriately Has some messy code at the moment, but I will
     * clean it up very soon.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws IOException {
        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (!assertsEnabled)
            throw new RuntimeException("Asserts must be enabled!!!");

        //assert false : "print stuff";
        //String logfile = args[0];
        //String debugfile = args[1];
        PrintStream stdout = System.out;

        PipedOutputStream debug_o = new PipedOutputStream();
        PipedOutputStream log_o = new PipedOutputStream();
        PipedOutputStream std_o = new PipedOutputStream();
        PipedOutputStream err_o = new PipedOutputStream();

        PrintStream debug = new PrintStream(debug_o);
        PrintStream log = new PrintStream(log_o);
        PrintStream std = new PrintStream(std_o);
        PrintStream err = new PrintStream(err_o);

        BufferedReader debug_i = new BufferedReader(new InputStreamReader(new PipedInputStream(debug_o)));
        BufferedReader log_i = new BufferedReader(new InputStreamReader(new PipedInputStream(log_o)));
        BufferedReader std_i = new BufferedReader(new InputStreamReader(new PipedInputStream(std_o)));
        BufferedReader err_i = new BufferedReader(new InputStreamReader(new PipedInputStream(err_o)));

        testStreams(std, err);
        String test = "testing this string";
        InitializeLog(debug, log, 5);
        try {
            test(test, LogFile.debug(), debug_i, std_i, true);
            test(test, LogFile.log(), log_i, std_i, true);
            test(test, LogFile.out(), std_i, std_i, false);
            test(test, LogFile.warn(), err_i, std_i, false);
            test(test, LogFile.err(), err_i, std_i, false);

            InitializeLog(debug, log, 4);
            test(test, LogFile.debug(), debug_i, std_i, false);
            test(test, LogFile.log(), log_i, std_i, true);
            test(test, LogFile.out(), std_i, std_i, false);
            test(test, LogFile.warn(), err_i, std_i, false);
            test(test, LogFile.err(), err_i, std_i, false);

            InitializeLog(debug, log, 3);
            test(test, LogFile.debug(), debug_i, std_i, false);
            test(test, LogFile.log(), log_i, std_i, false);
            test(test, LogFile.out(), std_i, std_i, false);
            test(test, LogFile.warn(), err_i, std_i, false);
            test(test, LogFile.err(), err_i, std_i, false);

            InitializeLog(debug, log, 2);
            test(test, LogFile.debug(), debug_i, std_i, false);
            test(test, LogFile.log(), log_i, std_i, false);
            test(test, LogFile.out(), null, std_i, false);
            test(test, LogFile.warn(), err_i, std_i, false);
            test(test, LogFile.err(), err_i, std_i, false);

            InitializeLog(debug, log, 1);
            test(test, LogFile.debug(), debug_i, std_i, false);
            test(test, LogFile.log(), log_i, std_i, false);
            test(test, LogFile.out(), null, std_i, false);
            test(test, LogFile.warn(), null, std_i, false);
            test(test, LogFile.err(), err_i, std_i, false);
            assert false : "print stuff";
        } catch (Exception e) {
            stdout.println("Caught an exception" + e.getStackTrace());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        InitializeLog(debug, log, 5);
        String str = "debug file and stdout (but not the log file)(level 5)";
        String str2;
        LogFile.debug().println(str);
        str2 = debug_i.readLine();
        boolean b = str2.equals(str);
        assert b : "Debug didn't make it.";
        str2 = log_i.readLine();
        b = str2.equals(str);
        //assert false : "print stuff";
        assert b : "str=" + str + " str2=" + str2;
        LogFile.log().println("log file and stdout (level 5)");
        assert log_i.readLine().equals(str) : "Log didn't make it.";
        assert std_i.readLine().equals(str) : "Std didn't make it";
        LogFile.out().println("log file and stdout");
        assert log_i.readLine().equals(str) : "Log didn't make it.";
        assert std_i.readLine().equals(str) : "Std didn't make it";
        LogFile.warn().println("log file and stderr");
        assert log_i.readLine().equals(str) : "Log didn't make it.";
        assert err_i.readLine().equals(str) : "Err didn't make it";
        LogFile.err().println("log file and stderr");
        assert log_i.readLine().equals("") : "Log didn't make it.";
        assert err_i.readLine().equals("") : "Err didn't make it";

        InitializeLog(debug, log, 4);
        LogFile.debug().println("debug file only");
        LogFile.log().println("log file and stdout (level 4)");
        LogFile.out().println("log file and stdout");
        LogFile.warn().println("log file and stderr");
        LogFile.err().println("log file and stderr");

        InitializeLog(debug, log, 3);
        LogFile.debug().println("debug file only");
        LogFile.log().println("log file only");
        LogFile.out().println("log file and on stdout");
        LogFile.warn().println("log file and stderr (warn level 3)");
        LogFile.err().println("log file and stderr (err level 3)");

        InitializeLog(debug, log, 2);
        LogFile.debug().println("debug file only");
        LogFile.log().println("log file only");
        LogFile.out().println("log file only (level 2)");
        LogFile.warn().println("log file and stderr (warn level 2)");
        LogFile.err().println("log file and stderr (err level 2)");

        InitializeLog(debug, log, 1);
        LogFile.debug().println("debug file only");
        LogFile.log().println("log file only");
        LogFile.out().println("log file only (out level 1)");
        LogFile.warn().println("log file only (err level 1)");
        LogFile.err().println("log file and stderr (err level 1)");

        System.out.println("Right now system.out = _null, and this will not be seen");
        restoreSystemStreams();
        System.out.println("testing nulls");

        InitializeLog(null, null, 5);
        LogFile.debug().println("5![debug file and] stdout (but not the log file)(level 5)");
        LogFile.log().println("4!{log file} and stdout (level 5)");
        LogFile.out().println("3!{log file} and stdout");
        LogFile.warn().println("2!{log file} and stderr");
        LogFile.err().println("1!{log file} and stderr");


        InitializeLog(null, null, 4);
        LogFile.debug().println("5![debug file and stdout] (but not the log file)(level 4)");
        LogFile.log().println("4!{log file} and stdout (level 4)");
        LogFile.out().println("3!{log file} and stdout");
        LogFile.warn().println("2!{log file} and stderr");
        LogFile.err().println("1!{log file} and stderr");

        InitializeLog(debug, log, 2);
        LogFile.debug().println("debug file only");
        LogFile.log().println("log file only");
        LogFile.out().println("log file only (level 2)");
        LogFile.warn().println("log file and stderr (warn level 2)");
        LogFile.err().println("log file and stderr (err level 2)");

        InitializeLog(debug, log, 1);
        LogFile.debug().println("debug file only");
        LogFile.log().println("log file only");
        LogFile.out().println("log file only (out level 1)");
        LogFile.warn().println("log file only (err level 1)");
        LogFile.err().println("log file and stderr (err level 1)");

        System.out.println("Right now system.out = _null, and this will not be seen");
        restoreSystemStreams();
        System.out.println("testing nulls");

        InitializeLog(null, null, 5);
        LogFile.debug().println("5![debug file and] stdout (but not the log file)(level 5)");
        LogFile.log().println("4!{log file} and stdout (level 5)");
        LogFile.out().println("3!{log file} and stdout");
        LogFile.warn().println("2!{log file} and stderr");
        LogFile.err().println("1!{log file} and stderr");

        InitializeLog(null, null, 4);
        LogFile.debug().println("5![debug file and stdout] (but not the log file)(level 4)");
        LogFile.log().println("4!{log file} and stdout (level 4)");
        LogFile.out().println("3!{log file} and stdout");
        LogFile.warn().println("2!{log file} and stderr");
        LogFile.err().println("1!{log file} and stderr");
    /**
     * Special Private class that inherit from PrintStream, and overwrites the
     * print methods. The overridden print methods print to both the logfile,
     * then checks to see if the "log_level" or "verbosity_level" is high enough
     * to print to the usual output. <br>
     * <br>
     * For example: if the user wants to print debugging info to the screen,
     * then it will print to both the file and the screen. <br>
     * <br>
     * If the user wants to run quiet, and only see warnings and errors, then
     * everything will be printed to the file, but only warning and errors will
     * print to the usual output, probably stderr.
     */
    }

    //	public static enum logLevel {Debug,Log,Std,Warn,Err};
    /**
     * Special Private class that inherit from PrintStream, and overwrites the
     * print methods. The overridden print methods print to both the logfile,
     * then checks to see if the "log_level" or "verbosity_level" is high enough
     * to print to the usual output. <br>
     * <br>
     * For example: if the user wants to print debugging info to the screen,
     * then it will print to both the file and the screen. <br>
     * <br>
     * If the user wants to run quiet, and only see warnings and errors, then
     * everything will be printed to the file, but only warning and errors will
     * print to the usual output, probably stderr.
     */
    class outStream extends PrintStream {



        private PrintStream _file;

        private PrintStream _local_out;

        private int _local_level;

        public outStream(PrintStream file, PrintStream out1, int local_level) {
            super(System.out);

            if (file == null)
                _file = _null;
            else
                _file = file;

            _local_out = out1;
            _local_level = local_level;
            // TODO Auto-generated constructor stub
        }

        @Override
        public void println() {
            _file.println();
            if (_local_level <= _LogLevel)
                _local_out.println();
        }

        @Override
        public void println(String s) {
            _file.println(s);
            if (_local_level <= _LogLevel)
                _local_out.println(s);
        }

        @Override
        public void print(String s) {
            _file.print(s);
            if (_local_level <= _LogLevel)
                _local_out.print(s);
        }

    }

    class tester {
        PrintStream stdout = System.out;

        PipedOutputStream debug_o = new PipedOutputStream();

        PipedOutputStream log_o = new PipedOutputStream();

        PipedOutputStream std_o = new PipedOutputStream();

        PipedOutputStream err_o = new PipedOutputStream();

        PrintStream debug = new PrintStream(debug_o);

        PrintStream log = new PrintStream(log_o);

        PrintStream std = new PrintStream(std_o);

        PrintStream err = new PrintStream(err_o);

        PipedInputStream debug_p = new PipedInputStream(debug_o);

        PipedInputStream log_p = new PipedInputStream(log_o);

        PipedInputStream std_p = new PipedInputStream(std_o);

        PipedInputStream err_p = new PipedInputStream(err_o);

        BufferedReader debug_i;

        BufferedReader log_i;

        BufferedReader std_i;

        BufferedReader err_i;

        BufferedReader[] bufferedReaders = { null, debug_i, log_i, std_i, err_i };

        public tester() throws IOException {
            debug_i = new BufferedReader(new InputStreamReader(debug_p));
            log_i = new BufferedReader(new InputStreamReader(debug_p));
            std_i = new BufferedReader(new InputStreamReader(debug_p));
            err_i = new BufferedReader(new InputStreamReader(debug_p));
            bufferedReaders[0] = null;
            bufferedReaders[5] = debug_i;
            bufferedReaders[4] = log_i;
            bufferedReaders[3] = std_i;
            bufferedReaders[2] = err_i;
            bufferedReaders[1] = err_i;

            InitializeLog(debug, log, 4);
            String str = "debug file only";
            LogFile.debug().println("debug file only");
            test(str, 5, 0);
            LogFile.log().println("log file and stdout (level 4)");
            test(str, 4, 3);
            LogFile.out().println("log file and stdout");
            test(str, 4, 3);
            LogFile.warn().println("log file and stderr");
            test(str, 4, 2);
            LogFile.err().println("log file and stderr");
            test(str, 4, 2);
        }

        public void test(String str, int one, int two) throws IOException {

            if (!bufferedReaders[one].readLine().equals(str)) {
                stdout.println("Failed Failed Failed!!!");
                new Exception().printStackTrace();
            }
            if (two != 0) {
                if (!bufferedReaders[two].readLine().equals(str)) {
                    stdout.println("Failed Failed Failed!!!");
                    new Exception().printStackTrace();
                }
            }
        }
    }
}
