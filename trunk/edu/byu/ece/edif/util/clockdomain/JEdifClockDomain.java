package edu.byu.ece.edif.util.clockdomain;

import java.io.PrintStream;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.jedif.EDIFMain;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.ClockDomainCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.MergeParserCommandGroup;

public class JEdifClockDomain extends EDIFMain {
    public static void main(String args[]) {

        // Define the print streams for this program
        // Print executable heading

        EXECUTABLE_NAME = "JEdifClockDomain";
        TOOL_SUMMARY_STRING = "Print Information about the Clockdomains in the given Edif or JEdif file";
        printProgramExecutableString(System.out);

        // Parse command line options
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new MergeParserCommandGroup());
        parser.addCommands(new ClockDomainCommandGroup());
        parser.addCommands(new LogFileCommandGroup("ClockDomain.log"));

        JSAPResult result = parser.parse(args, System.err);
        if (!result.success())
            System.exit(1);

        LogFileCommandGroup.CreateLog(result);
        PrintStream out = LogFile.out();
        PrintStream err = LogFile.err();

        printProgramExecutableString(LogFile.log());

        EdifEnvironment top;
        //if .jedif file, unserialize it, else
        top = MergeParserCommandGroup.getEdifEnvironment(result);

        // TODO: check to see if top is null
        FlattenedEdifCell ec;
        ClockDomainParser cdp;
        try {
            if (top.getTopCell() instanceof FlattenedEdifCell)
                ec = (FlattenedEdifCell) top.getTopCell();
            else
                ec = new FlattenedEdifCell(top.getTopCell());
            cdp = new ClockDomainParser(ec);
            cdp.setOutput(out);
            cdp.parseCommandlineOptions(result);
            cdp.exec(LogFile.out());
        } catch (InvalidEdifNameException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (EdifNameConflictException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
