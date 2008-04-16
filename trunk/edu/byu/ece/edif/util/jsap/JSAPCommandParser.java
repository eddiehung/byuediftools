/*
 * TODO: Insert class description here.
 *
 *

 * Copyright (c) 2008 Brigham Young University
 *
 * This file is part of the BYU EDIF Tools.
 * 
 * BYU EDIF Tools is free software: you may redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * BYU EDIF Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License is included with the BYU
 * EDIF Tools. It can be found at /edu/byu/edif/doc/gpl2.txt. You may
 * also get a copy of the license at <http://www.gnu.org/licenses/>.
 *
 */
package edu.byu.ece.edif.util.jsap;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import com.martiansoftware.jsap.Flagged;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.QualifiedSwitch;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;

/**
 * A {@link JSAP} (Java-based Simple Argument Parser) with additional
 * functionality. If the additional functionality provided by this class were
 * added to JSAP itself, this class would be unnecessary. Added functionality
 * includes:
 * <ul>
 * <li>Easily read and write configuration files.</li>
 * <li>the getResult() method</li>
 * <li>Access to the BoundedStringParser classes (Commonly-used
 * BoundedDoubleStringParser objects are declared in this class.)</li>
 * </ul>
 * 
 * @author <a href="jcarroll@byu.net">James Carroll</a>
 * @see <a href="http://www.martiansoftware.com/jsap/doc/javadoc/index.html">
 *      JSAP API</a>
 * @see <a href="http://www.martiansoftware.com/jsap/">JSAP Homepage</a>
 * @see NMRCommandParser
 * @see DWCCommandParser
 * @see JSAP
 */

public class JSAPCommandParser extends JSAP {

	/**
	 * Create a new NMRCommandParser with the given command-line arguments.
	 * 
	 * @param args The command-line arguments
	 */
	public JSAPCommandParser() {

		_parameters = new ArrayList<Parameter>();

		/*
		 * Configuration File Options
		 */
		_parameters
				.add(new FlaggedOption(USE_CONFIG)
						.setLongFlag("useConfig")
						.setUsageName("config_file")
						.setHelp(
								"Load a configuration file to be used as a set of default command-line parameters."));

		_parameters
				.add(new QualifiedSwitch(WRITE_CONFIG)
						.setLongFlag("writeConfig")
						.setUsageName("config_file")
						.setHelp(
								"Create a configuration file from the current command-line options and exit."));

		/*
		 * Help and Version Information
		 */
		_parameters.add(new Switch(HELP).setLongFlag("help").setDefault(FALSE)
				.setHelp("Print this help message to stdout and exit."));

		_parameters.add(new Switch(VERSION).setLongFlag("version").setDefault(
				FALSE).setHelp(
				"Print version and copyright information to stdout and exit."));

		// Register the parameters. For more information, see the JSAP API.
		for (Parameter p : _parameters) {
			try {
				this.registerParameter(p);
			} catch (JSAPException e) {
				System.err.println("Error while registering parameter " + p
						+ ":\n" + e);
			}
		}
	}

	/**
	 * @return The JSAPResult object.
	 */
	public JSAPResult getResult() {
		return this._result;
	}

	/**
	 * For testing purposes only. Parses the command-line arguments, which
	 * ensures that they are valid, and then simply prints a list of all the
	 * arguments.
	 * 
	 * @param args Command-line arguments
	 */
	public static void main(String[] args) {

		JSAPCommandParser cp = new JSAPCommandParser();
		JSAPResult result = cp.parse(args);

		System.out
				.println("Congratulations! Successfully parsed all parameters:\n");

		// Print all parameters as lists [item1,item2,...,itemN]
		for (Parameter p : cp._parameters) {
			String id = p.getID();
			Object[] obj = result.getObjectArray(id);
			/*
			 * If it's a flagged Parameter, print the long flag rather than the
			 * id.
			 */
			if (p instanceof Flagged)
				System.out.println(((Flagged) p).getLongFlag() + ":\t\t"
						+ Arrays.asList(obj));
			else
				System.out.println(id + ":\t\t" + Arrays.asList(obj));
		}
	}

	/**
	 * Takes all the user set parameters and writes them to a config file, which
	 * can be used later as a default set of parameters.
	 * 
	 * @param filename The output filename to be written.
	 */
	protected void createConfigFile(String filename) {
		Properties p = new Properties(); // properties object

		/*
		 * Iterate through each parameter. Create a new property with the
		 * parameter's id as the key and the parameter's value as the value. If
		 * the parameter can be represented as an array of String objects,
		 * iterate through them and create a list from the elements in the
		 * array.
		 */
		for (Parameter param : _parameters) {
			String id = param.getID();
			StringBuffer sb = new StringBuffer();
			String key, value;

			// If parameter isn't set by the user, skip it and move on.
			if (!_result.userSpecified(id)) {
				continue;
			}

			// Is it an UnflaggedOption?
			if (param instanceof UnflaggedOption) {
				value = _result.getString(id);
				key = id;
			}
			// Is it a Switch?
			else if (param instanceof Switch) {
				key = ((Flagged) param).getLongFlag();
				value = Boolean.toString(_result.getBoolean(id));
			}
			// Must be a FlaggedOption or a QualifiedSwitch.
			else {
				key = ((Flagged) param).getLongFlag();
				Object[] array = _result.getObjectArray(id);
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

			p.store(new PrintStream(filename), filename + ", created by "
					+ NMRCommandParser.class.getName());
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	/**
	 * Primary configuration file
	 */
	public static final String CONF_FILENAME = "config.conf";

	/**
	 * "Report bugs to ..."
	 */
	protected String _EMAIL_ADDRESS = "<jcarroll@byu.net>";

	/**
	 * Secondary configuration file
	 */
	public static final String ETC_FILENAME = "/etc/BYU/config.conf";

	/**
	 * Character used to separate lists
	 */
	public static final char LIST_DELIMITER = ',';

	public static final String FALSE = "false";

	public static final String HELP = "help";

	public static final String INPUT_FILE = "input";

	public static final String OUTPUT_FILE = "output";

	public static final String TRUE = "true";

	public static final String USE_CONFIG = "useConfig";

	public static final String VERSION = "version";

	public static final String WRITE_CONFIG = "writeConfig";

	// ///////////////////////////////////////////////////////////////
	// Protected fields

	/**
	 * Range := [0,oo], where oo = Double.MAX_VALUE (nearly infinity)<br>
	 * 
	 * Known uses: desiredUtilizationFactor
	 */
	protected static BoundedDoubleStringParser _minZeroInclusive = BoundedDoubleStringParser
			.getParser(0.0, null);

	/**
	 * Range := (0,oo]<br>
	 * 
	 * Known uses: ultilzationExpansionFactor
	 */
	protected static BoundedDoubleStringParser _minZeroNonInclusive = BoundedDoubleStringParser
			.getParser(0.0, null, false, null);

	/**
	 * Set := {1,2,3}<br>
	 * 
	 * Known uses: inputAdditionType, outputAdditionType, SCCSortType
	 */
	protected static BoundedIntegerStringParser _oneTwoThree = BoundedIntegerStringParser
			.getParser(1, 3);

	/**
	 * Ordered List of parameters to easily register them all or print them all
	 */
	protected ArrayList<Parameter> _parameters;

	/**
	 * {@link JSAPResult} object used to store the parameters after being
	 * parsed.
	 */
	protected JSAPResult _result;

	/**
	 * Range := (0,1]<br>
	 * 
	 * Known uses: availableSpaceUtilizationFactor
	 */
	protected static BoundedDoubleStringParser _zeroToOneIncludeOne = BoundedDoubleStringParser
			.getParser(0.0, 1.0, false, true);

	/**
	 * Range := [0,1)<br>
	 * 
	 * Known uses: none
	 */
	protected static BoundedDoubleStringParser _zeroToOneIncludeZero = BoundedDoubleStringParser
			.getParser(0.0, 1.0, true, false);

	/**
	 * Range := [0,1]<br>
	 * 
	 * Known uses: mergeFactor, optimizationFactor.
	 */
	protected static BoundedDoubleStringParser _zeroToOneInclusive = BoundedDoubleStringParser
			.getParser(0.0, 1.0);

	/**
	 * Set := {0,1} Use for hlConst
	 */
	protected static BoundedIntegerStringParser _zeroOne = BoundedIntegerStringParser
			.getParser(0, 1);
}
