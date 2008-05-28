/*
 * Combines all the JEdif tools into one simple executable.
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

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.replicate.PartialReplicationDescription;
import edu.byu.ece.edif.util.jsap.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.CutFeedbackCommandGroup;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.InputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.MergeParserCommandGroup;
import edu.byu.ece.edif.util.jsap.OutputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.SharedIOBAnalysis;
import edu.byu.ece.edif.util.jsap.TechnologyCommandGroup;

/**
 * Combines the JEdif tools into one simple executable.
 * 
 * @author dsgib
 */
public class EdifTMR extends EDIFMain {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        PrintStream out = System.out;
        PrintStream err = System.out;
        // Print executable heading
        EXECUTABLE_NAME = "EdifTMR";
        TOOL_SUMMARY_STRING = "Runs through the JEdif TMR toolflow in one step";
        // Parse command line options
        printProgramExecutableString(out);

        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new MergeParserCommandGroup());
        parser.addCommands(new OutputFileCommandGroup());
        parser.addCommands(new JEdifSterilizeCommandGroup());
        parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));
        parser.addCommands(new JEdifTMRAnalysisCommandGroup());
        parser.addCommands(new CutFeedbackCommandGroup());
        parser.addCommands(new JEdifTMRParserCommandGroup());

        parser.addCommands(new TechnologyCommandGroup());
        parser.addCommands(new LogFileCommandGroup("BLTmr.log"));

        JSAPResult result = parser.parse(args, err);

        if (!result.success())
            System.exit(1);

        LogFileCommandGroup.CreateLog(result);
        out = LogFile.out();
        err = LogFile.err();

        printProgramExecutableString(LogFile.log());

        EdifEnvironment env;
        if (InputFileCommandGroup.getInputFileName(result).endsWith(".jedif"))
            env = JEdifParserCommandGroup.getEdifEnvironment(result, out);
        else
            env = MergeParserCommandGroup.getEdifEnvironment(result);

        TechnologyCommandGroup.getPartFromEDIF(result, env);

        JEdifSterilize.flatten_sterilize(result, env);
        EdifCell flatCell = env.getTopCell();
        //now we have a flattened and sterilized cell
        //tmr analysis
        PartialReplicationDescription ptmr = JEdifTMRAnalysis.tmr(env, flatCell, result);
        //tmr cutset
        // Get IOB analysis information
        SharedIOBAnalysis iobAnalysis = JEdifTMRAnalysis.getSharedIOB();

        Collection<EdifPortRef> PRGcuts = JEdifCutset.getValidCutset(result, flatCell, iobAnalysis);
        ptmr.portRefsToCut = PRGcuts;
        //tmr
        JEdifTMR.tmr(result, env, ptmr);

        try {
            String filename = OutputFileCommandGroup.getOutputFileName(result);

            if (!filename.contains("."))
                filename = filename.concat(".edf");

            if (filename.endsWith(".jedif")) {
                OutputFileCommandGroup.serializeObject(out, filename, env);
            } else {
                filename = JEdifNetlist.generateEdifNetlist(env, result, "_tmr", EXECUTABLE_NAME, VERSION_STRING);
                LogFile.out().println("Wrote to file: " + filename);

                //                EdifPrintWriter epw;
                //                epw = new EdifPrintWriter(filename);
                //                env.toEdif(epw);

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
