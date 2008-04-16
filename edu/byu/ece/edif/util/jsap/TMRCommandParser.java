/*
 * A JSAP parser for the command-line arguments sent to FlattenDWC
 */
/*
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

import java.util.ArrayList;
import java.util.Arrays;


import com.martiansoftware.jsap.Flagged;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.Switch;

import edu.byu.ece.edif.tools.replicate.nmr.tmr.FlattenTMR;

/**
 * A JSAP parser for the command-line arguments sent to FlattenDWC
 * 
 * @author <a href="jcarroll@byu.net">James Carroll</a>
 * @see FlattenTMR
 * @see NMRCommandParser
 * @see JSAP
 * @see <a href="http://www.martiansoftware.com/jsap/doc/javadoc/index.html">
 *      JSAP API</a>
 * @see <a href="http://www.martiansoftware.com/jsap/">JSAP Homepage</a>
 */
public class TMRCommandParser extends NMRCommandParser {

	/**
	 * First let the parent classes register all the Parameters, then unregister
	 * the ones that need to be changed for TMR, change them, and re-register
	 * them.
	 */
	public TMRCommandParser() {

		/*
		 * Parent constructor sets up common NMR Parameters such as input_file,
		 * --output, --nmr_inports, --no_nmr_c, --SCCSortType, etc.
		 */
		super(3); // TMR is a replication factor of 3
		super._MORE_INFO = this._MORE_INFO;
		super._EMAIL_ADDRESS = this._EMAIL_ADDRESS;

		_TMRParameters = new ArrayList<Parameter>();
		// Temp variables to reduce the amount of casting necessary
		Switch s;
		FlaggedOption f;

		/*
		 * File options: input_file, output_file, etc.
		 */
		f = (FlaggedOption) getByID(OUTPUT_FILE);
		unregisterParameter(f);
		_parameters.remove(f);
		f.setDefault(DEFAULT_OUTPUT_FILENAME);
		_TMRParameters.add(f);
		
		f = (FlaggedOption) getByID(NMR_SUFFIX);
		unregisterParameter(f);
		_parameters.remove(f);
		_TMRParameters.add(f.setLongFlag(TMR_SUFFIX_FLAG));

		/*
		 * Partial NMR options
		 */
		s = (Switch) getByID(FULL_NMR);
		unregisterParameter(s);
		_parameters.remove(s);
		_TMRParameters.add(s.setLongFlag(FULL_TMR_FLAG));

		s = (Switch) getByID(NMR_INPORTS);
		unregisterParameter(s);
		_parameters.remove(s);
		_TMRParameters.add(s.setLongFlag(TMR_INPORTS_FLAG));

		s = (Switch) getByID(NMR_OUTPORTS);
		unregisterParameter(s);
		_parameters.remove(s);
		_TMRParameters.add(s.setLongFlag(TMR_OUTPORTS_FLAG));

		f = (FlaggedOption) getByID(NO_NMR_P);
		unregisterParameter(f);
		_parameters.remove(f);
		_TMRParameters.add(f.setLongFlag(NO_TMR_P_FLAG));

		f = (FlaggedOption) getByID(NO_NMR_C);
		unregisterParameter(f);
		_parameters.remove(f);
		_TMRParameters.add(f.setLongFlag(NO_TMR_C_FLAG));
		
		f = (FlaggedOption) getByID(NO_NMR_CLK);
		unregisterParameter(f);
		_parameters.remove(f);
		_TMRParameters.add(f.setLongFlag(NO_TMR_CLK_FLAG));

		f = (FlaggedOption) getByID(NO_NMR_I);
		unregisterParameter(f);
		_parameters.remove(f);
		_TMRParameters.add(f.setLongFlag(NO_TMR_I_FLAG));

		f = (FlaggedOption) getByID(NMR_C);
		unregisterParameter(f);
		_parameters.remove(f);
		_TMRParameters.add(f.setLongFlag(TMR_C_FLAG));

		f = (FlaggedOption) getByID(NMR_CLK);
		unregisterParameter(f);
		_parameters.remove(f);
		_TMRParameters.add(f.setLongFlag(TMR_CLK_FLAG));
		
		f = (FlaggedOption) getByID(NMR_I);
		unregisterParameter(f);
		_parameters.remove(f);
		_TMRParameters.add(f.setLongFlag(TMR_I_FLAG));

		s = (Switch) getByID(NO_NMR_FEED_FORWARD);
		unregisterParameter(s);
		_parameters.remove(s);
		_TMRParameters.add(s.setLongFlag(NO_TMR_FEED_FORWARD_FLAG));

		s = (Switch) getByID(NO_NMR_FEEDBACK);
		unregisterParameter(s);
		_parameters.remove(s);
		_TMRParameters.add(s.setLongFlag(NO_TMR_FEEDBACK_FLAG));

		s = (Switch) getByID(NO_NMR_FEEDBACK_OUTPUT);
		unregisterParameter(s);
		_parameters.remove(s);
		_TMRParameters.add(s.setLongFlag(NO_TMR_FEEDBACK_OUTPUT_FLAG));

		s = (Switch) getByID(NO_NMR_INPUT_TO_FEEDBACK);
		unregisterParameter(s);
		_parameters.remove(s);
		_TMRParameters.add(s.setLongFlag(NO_TMR_INPUT_TO_FEEDBACK_FLAG));

		/*
		 * Log, Report, and Configuration File Options
		 */
		f = (FlaggedOption) getByID(LOG);
		unregisterParameter(f);
		_parameters.remove(f);
		f.setDefault(DEFAULT_LOG_FILENAME);
		_TMRParameters.add(f);

		f = (FlaggedOption) getByID(DOMAIN_REPORT);
		unregisterParameter(f);
		_parameters.remove(f);
		f.setDefault(DEFAULT_DOMAIN_REPORT_FILENAME);
		_TMRParameters.add(f);

		/*
		 * Register the parameters. For more information, see the JSAP API.
		 */
		for (Parameter p : _TMRParameters) {
			try {
				this.registerParameter(p);
				_parameters.add(p);
			} catch (JSAPException e) {
				System.err.println("Error while registering parameter " + p
						+ ":\n" + e);
			}
		}

	}

	/**
	 * For testing purposes only. Parses the command-line arguments, which
	 * ensures that they are valid, and then simply prints a list of all the
	 * arguments.
	 * 
	 * @param args Command-line arguments
	 */
	public static void main(String[] args) {

		TMRCommandParser cp = new TMRCommandParser();
		JSAPResult result = cp.parse(args);

		System.out.println(FlattenTMR.getVersionInfo() + " "
				+ FlattenTMR.REVISION);
		System.out
				.println("Congratulations! Successfully parsed all parameters:\n");

		// Print all parameters as lists [item1,item2,...,itemN]
		ArrayList<Parameter> allParameters = new ArrayList<Parameter>(
				_NMRParameters);
		allParameters.addAll(cp._parameters);
		for (Parameter p : allParameters) {
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
	 * An ArrayList of Parameters unique to TMR
	 */
	protected static ArrayList<Parameter> _TMRParameters;

	protected String _MORE_INFO = "BLTmr.pdf";

	protected String _EMAIL_ADDRESS = "<brianpratt@byu.edu> or <jcarroll@byu.net>";

	protected String DEFAULT_DOMAIN_REPORT_FILENAME = "BLTmr_domain_report.txt";

	protected String DEFAULT_LOG_FILENAME = "BLTmr.log";

	protected String DEFAULT_OUTPUT_FILENAME = "BLTmr.edf";

	/*
	 * The following exist so that FlattenTMR can refer to these strings as
	 * "FOO_TMR" instead of "FOO_NMR".
	 */
	public static final String FULL_TMR = FULL_NMR;

	public static final String FULL_TMR_FLAG = "full_tmr";
	
	public static final String TMR_SUFFIX = NMR_SUFFIX;

	public static final String TMR_SUFFIX_FLAG = "tmr_suffix";

	public static final String NO_TMR_C = NO_NMR_C;

	public static final String NO_TMR_C_FLAG = "no_tmr_c";
	
	public static final String NO_TMR_CLK = NO_NMR_CLK;
	
	public static final String NO_TMR_CLK_FLAG = "no_tmr_clk";

	public static final String NO_TMR_FEEDBACK = NO_NMR_FEEDBACK;

	public static final String NO_TMR_FEEDBACK_FLAG = "no_tmr_feedback";

	public static final String NO_TMR_FEEDBACK_OUTPUT = NO_NMR_FEEDBACK_OUTPUT;

	public static final String NO_TMR_FEEDBACK_OUTPUT_FLAG = "no_tmr_feedback_output";

	public static final String NO_TMR_FEED_FORWARD = NO_NMR_FEED_FORWARD;

	public static final String NO_TMR_FEED_FORWARD_FLAG = "no_tmr_feed_forward";

	public static final String NO_TMR_I = NO_NMR_I;

	public static final String NO_TMR_INPUT_TO_FEEDBACK = NO_NMR_INPUT_TO_FEEDBACK;

	public static final String NO_TMR_INPUT_TO_FEEDBACK_FLAG = "no_tmr_input_to_feedback";

	public static final String NO_TMR_I_FLAG = "no_tmr_i";

	public static final String NO_TMR_P = NO_NMR_P;

	public static final String NO_TMR_P_FLAG = "no_tmr_p";

	public static final String TMR_C = NMR_C;

	public static final String TMR_C_FLAG = "tmr_c";
	
	public static final String TMR_CLK = NMR_CLK;

	public static final String TMR_CLK_FLAG = "tmr_clk";

	public static final String TMR_I = NMR_I;

	public static final String TMR_INPORTS = NMR_INPORTS;

	public static final String TMR_INPORTS_FLAG = "tmr_inports";

	public static final String TMR_I_FLAG = "tmr_i";

	public static final String TMR_OUTPORTS = NMR_OUTPORTS;

	public static final String TMR_OUTPORTS_FLAG = "tmr_outports";
}
