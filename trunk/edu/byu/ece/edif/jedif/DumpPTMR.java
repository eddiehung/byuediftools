/* TODO: Insert class description here.
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

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.replicate.PartialReplicationStringDescription;
import edu.byu.ece.edif.tools.replicate.Replication;
import edu.byu.ece.edif.tools.replicate.nmr.EdifNameableParentStringReference;
import edu.byu.ece.edif.tools.replicate.nmr.EdifNameableStringReference;
import edu.byu.ece.edif.tools.replicate.nmr.EdifPortRefStringReference;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.OutputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.PTMRFileCommandGroup;

public class DumpPTMR extends EDIFMain {

    public static void main(String[] args) {

        // Define the print streams for this program
        PrintStream out = System.out;
        PrintStream err = System.err;

        // Print executable heading
        EXECUTABLE_NAME = "JEdifCutset";
        TOOL_SUMMARY_STRING = "Identifies PortRefs to cut feedback for possible voter insertion";
        printProgramExecutableString(out);

        // Parse command line options
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new OutputFileCommandGroup());
        parser.addCommands(new PTMRFileCommandGroup());
        LogFileCommandGroup loggerCG = new LogFileCommandGroup("JEdifCutset.log");
        parser.addCommands(loggerCG);

        JSAPResult result = parser.parse(args, err);
        if (!result.success())
            System.exit(1);

        LogFileCommandGroup.CreateLog(result);
        out = LogFile.out();
        err = LogFile.err();

        printProgramExecutableString(LogFile.log());

        // can be used to log currently used options, as well as to create help files
        //loggerCG.logOptions(parser.getCommands(),logger);
        LogFile.log().println("These command-line options were used:" + Arrays.asList(args) + "\n");

        PartialReplicationStringDescription prsd = PTMRFileCommandGroup.getPartialReplicationDescription(result, out);

        EdifNameableParentStringReference enpsr = prsd.get_cellToReplicate();
        Collection<EdifNameableStringReference> feedback = prsd.get_feedbackPlusInput();
        Collection<Replication> instances = prsd.get_instancesToReplicate();
        Collection<EdifPortRefStringReference> portRefs = prsd.get_portRefsToCut();
        Collection<Replication> ports = prsd.get_portsToReplicate();

        PrintStream file = OutputFileCommandGroup.getOutputStream(result);

        file.println(enpsr.toString());

        file.println("\n\n----------------------------------");
        file.println("--Feedback");
        file.println("----------------------------------\n\n");
        if (feedback != null)
            for (EdifNameableStringReference ensr : feedback) {
                file.println(ensr);
            }

        file.println("\n\n----------------------------------");
        file.println("--Instances to Triplication");
        file.println("----------------------------------\n\n");
        file.println();
        if (instances != null)
            for (Replication ensr : instances) {
                file.println(ensr.getStringRef());
            }

        file.println("\n\n----------------------------------");
        file.println("--portRefs to Cut");
        file.println("----------------------------------\n\n");
        file.println();
        if (portRefs != null)
            for (EdifPortRefStringReference ensr : portRefs) {
                file.println(ensr);
            }

        file.println("\n\n----------------------------------");
        file.println("--Ports to Triplicate");
        file.println("----------------------------------\n\n");
        file.println();
        if (portRefs != null)
            for (Replication port : ports) {
                file.println(port);
            }

    }
}
