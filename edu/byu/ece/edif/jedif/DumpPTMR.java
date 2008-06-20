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
