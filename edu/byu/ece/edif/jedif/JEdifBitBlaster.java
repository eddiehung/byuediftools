package edu.byu.ece.edif.jedif;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPrintWriter;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.util.bitblast.EdifEnvironmentBitBlast;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.InputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.OutputFileCommandGroup;
import edu.byu.ece.edif.util.parse.EdifParser;
import edu.byu.ece.edif.util.parse.ParseException;

public class JEdifBitBlaster extends EDIFMain {
	
	public static PrintStream out;
    public static PrintStream err;
    
    public static void main(String[] args) {
        out = System.out;
        err = System.err;
        
        EXECUTABLE_NAME = "JEdifBitBlaster";
        TOOL_SUMMARY_STRING = "Bit blast the top-level ports of an .edf file.";
        
        printProgramExecutableString(out);
        
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new InputFileCommandGroup());
        parser.addCommands(new OutputFileCommandGroup());
        parser.addCommands(new LogFileCommandGroup("build.log"));
        parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));

        JSAPResult result = parser.parse(args, err);
        if (!result.success())
            System.exit(1);

        LogFileCommandGroup.CreateLog(result);
        out = LogFile.out();
        err = LogFile.err();

        printProgramExecutableString(LogFile.log());

        // Parse EDIF file and generate a EdifEnvironment object
        String inputFile = InputFileCommandGroup.getInputFileName(result);
        
        EdifEnvironment env = null;
        try {
            env = EdifParser.translate(inputFile);
        } catch (FileNotFoundException e) {
            err.println("File not found: " + inputFile);
            System.exit(2);
        } catch (ParseException e) {
            err.println("Error parsing file: " + inputFile);
            System.exit(2);
        }
        
        EdifCell topCell = env.getTopCell();
        Set<EdifPort> bitBlastPorts = new HashSet<EdifPort>();
        bitBlastPorts.addAll(topCell.getPortList());
        
        EdifEnvironmentBitBlast bitBlastEnv = null;
        EdifEnvironment outputEnv = null;
		try {
			bitBlastEnv = new EdifEnvironmentBitBlast(env, bitBlastPorts);
			outputEnv = bitBlastEnv.createEdifEnvironment();
		} catch (EdifNameConflictException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        OutputStream outputStream = OutputFileCommandGroup.getOutputStream(result);
    	EdifPrintWriter epw = new EdifPrintWriter(outputStream);
    	outputEnv.toEdif(epw);
    	epw.flush();
    	epw.close();
    	
    	out.println("Output written to: " + OutputFileCommandGroup.getOutputFileName(result));
    	
    }
	
}
