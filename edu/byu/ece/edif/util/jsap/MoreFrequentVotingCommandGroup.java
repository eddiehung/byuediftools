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
package edu.byu.ece.edif.util.jsap;

import java.io.PrintStream;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;


public class MoreFrequentVotingCommandGroup extends AbstractCommandGroup {

    protected FlaggedOption _voter_threshold;

    public static final String THRESHOLD_OPTION = "voter_threshold";

    protected FlaggedOption _num_partitions;

    public static final String PARTITION_OPTION = "num_partitions";

    protected FlaggedOption voter_location;

    public static final String VOTER_LOC_OPTION = "voter_location";

    public MoreFrequentVotingCommandGroup() {
        super();

        // Option for using a voter threshold 
        _voter_threshold = new FlaggedOption(THRESHOLD_OPTION);
        _voter_threshold.setStringParser(JSAP.INTEGER_PARSER);
        _voter_threshold.setRequired(JSAP.NOT_REQUIRED);
        _voter_threshold.setLongFlag(THRESHOLD_OPTION);
        _voter_threshold.setUsageName("voter_threshold");
        _voter_threshold.setHelp("Voter insertion threshold.");
        this.addCommand(_voter_threshold);

        // Option for using a number of graph partitions to make
        _num_partitions = new FlaggedOption(PARTITION_OPTION);
        _num_partitions.setStringParser(JSAP.INTEGER_PARSER);
        _num_partitions.setRequired(JSAP.NOT_REQUIRED);
        _num_partitions.setLongFlag(PARTITION_OPTION);
        _num_partitions.setUsageName("num_partitions");
        _num_partitions.setHelp("Number of partitions to create in the circuit.");
        this.addCommand(_num_partitions);

        // Option for user-specified voter locations
        voter_location = new FlaggedOption(VOTER_LOC_OPTION);
        voter_location.setStringParser(JSAP.STRING_PARSER);
        voter_location.setRequired(JSAP.NOT_REQUIRED);
        voter_location.setShortFlag(JSAP.NO_SHORTFLAG);
        voter_location.setLongFlag(VOTER_LOC_OPTION);
        voter_location.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        voter_location.setList(JSAP.LIST);
        voter_location.setListSeparator(',');
        voter_location.setUsageName("voter_location");
        voter_location.setHelp("Comma-separated list of instances to place voters *after* (on the instance's outputs");
        this.addCommand(voter_location);
    }

    public static String[] getVoterLocations(JSAPResult result, PrintStream out) {
        if (result.userSpecified(VOTER_LOC_OPTION))
            return result.getStringArray(VOTER_LOC_OPTION);
        else
            return null;
    }

    public static int getVoterThreshold(JSAPResult result, PrintStream out) {
        if (result.userSpecified(THRESHOLD_OPTION))
            return result.getInt(THRESHOLD_OPTION);
        else
            return -1;
    }

    public static int getNumPartitions(JSAPResult result, PrintStream out) {
        if (result.userSpecified(PARTITION_OPTION))
            return result.getInt(PARTITION_OPTION);
        else
            return -1;
    }
}
