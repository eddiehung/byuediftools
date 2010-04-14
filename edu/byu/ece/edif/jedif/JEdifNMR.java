package edu.byu.ece.edif.jedif;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.replicate.nmr.EdifEnvironmentReplicate;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.InputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifParserCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.OutputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.ReplicationDescriptionCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.TechnologyCommandGroup;

/**
 * This static executable will perform the NMR operation on an Edif file. This executable needs an EdifEnvironment
 * and a replication description object.
 */
public class JEdifNMR extends EDIFMain {

	public static String GENERATE_EDIF_FLAG = "edif";
	public static String DOMAIN_REPORT = "write_domain_report";
	public static String RENAME_TOP_CELL = "rename_top_cell";
	
	public static void main(String[] args) {
		
		PrintStream out = System.out;
		PrintStream err = System.err;
		
		EXECUTABLE_NAME = "JEdifNMR";
		TOOL_SUMMARY_STRING = "Performs the replication selected by previously run tools (i.e. JEdifAnalyze, JEdifSelection, JEdifVoterSelection, JEdifDetectionSelection, etc.)";
		
		printProgramExecutableString(out);
		
		EdifCommandParser parser = new EdifCommandParser();
		
		// option for input .jedif file
		parser.addCommands(new JEdifParserCommandGroup());
		
		// option for input ReplicationDescription file
		parser.addCommands(new ReplicationDescriptionCommandGroup());
		
		// option for the output filename
		parser.addCommands(new OutputFileCommandGroup());
		
		// part options
		parser.addCommands(new TechnologyCommandGroup());
		
        // Flag for deciding whether .edif or .jedif is output
        Switch edif_flag = new Switch(GENERATE_EDIF_FLAG);
        edif_flag.setShortFlag(JSAP.NO_SHORTFLAG);
        edif_flag.setLongFlag(GENERATE_EDIF_FLAG);
        edif_flag.setDefault("false");
        edif_flag.setHelp("Generate EDIF output instead of .jedif");
        
        // Flag for specifying that a domain report should be written
        FlaggedOption domain_report = new FlaggedOption(DOMAIN_REPORT);
        domain_report.setStringParser(JSAP.STRING_PARSER);
        domain_report.setRequired(JSAP.NOT_REQUIRED);
        domain_report.setShortFlag(JSAP.NO_SHORTFLAG);
        domain_report.setLongFlag(DOMAIN_REPORT);
        domain_report.setHelp("When this option is specified, a domain report is written to the given file name. " +
        		"The domain report lists the domain (i.e. 0, 1, or 2 for TMR) of each cell instance in the replicated design.");
        
        // Option for renaming the top cell
        FlaggedOption rename_top_cell = new FlaggedOption(RENAME_TOP_CELL);
        rename_top_cell.setStringParser(JSAP.STRING_PARSER);
        rename_top_cell.setRequired(JSAP.NOT_REQUIRED);
        rename_top_cell.setShortFlag(JSAP.NO_SHORTFLAG);
        rename_top_cell.setLongFlag(RENAME_TOP_CELL);
        rename_top_cell.setUsageName(RENAME_TOP_CELL);
        rename_top_cell.setHelp("Use this option to specify a name for the design's top cell.");
                
        try {
			parser.registerParameter(edif_flag);
			parser.registerParameter(domain_report);
			parser.registerParameter(rename_top_cell);
		} catch (JSAPException e1) {
			// won't get here
		}
		
		parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));
		
		// log file command group
		parser.addCommands(new LogFileCommandGroup("nmr.log"));
		
		JSAPResult result = parser.parse(args, err);
		if (!result.success())
			System.exit(1);
		LogFileCommandGroup.CreateLog(result);
		printProgramExecutableString(LogFile.log());
		out = LogFile.out();
		err = LogFile.err();
		
		// Get the Edif Environment
		EdifEnvironment env = JEdifParserCommandGroup.getEdifEnvironment(result, out);
		// Get the replication description
		ReplicationDescription rdesc = ReplicationDescriptionCommandGroup.getReplicationDescription(result, env, out);

		TechnologyCommandGroup.getPartFromEDIF(result, env);
		NMRArchitecture arch = TechnologyCommandGroup.getArch(result);
		
		// Create a new name for the top level cell and call replicate
		EdifEnvironmentReplicate replicator = null;
		EdifEnvironment newEnv = null;
		try {
		    if (result.userSpecified(RENAME_TOP_CELL)) {
		        EdifNameable topCellName = NamedObject.createValidEdifNameable(result.getString(RENAME_TOP_CELL));
		        replicator = new EdifEnvironmentReplicate(env, rdesc, arch, topCellName);
		    }
		    else {
		        replicator = new EdifEnvironmentReplicate(env, rdesc, arch);
		    }
			newEnv = replicator.replicate();
		} catch (EdifNameConflictException e) {
			e.printStackTrace();
			throw new EdifRuntimeException("Unexpected EDIF name conflict");
		}

		// Determine output filename
		String outputFileName = OutputFileCommandGroup.getOutputFileName(result);
		boolean generateEdif = result.getBoolean(GENERATE_EDIF_FLAG);
		if (!result.userSpecified(OutputFileCommandGroup.OUTPUT_OPTION)) {
			outputFileName = InputFileCommandGroup.getInputFileName(result);
			outputFileName = outputFileName.substring(0, outputFileName.lastIndexOf('.'));
			outputFileName += "_tmr";
			if (result.getBoolean(GENERATE_EDIF_FLAG))
				outputFileName += ".edf";
			else
				outputFileName += ".jedif";
		}
		else {
			if (outputFileName.endsWith(".edf"))
				generateEdif = true;			
		}

		// Generate EDIF or .jedif
		if (generateEdif) {
			// Generate EDIF output
			LogFile.out().println("Generating .edf file " + outputFileName);
			try {
				JEdifNetlist.generateEdifNetlist(newEnv, result, EXECUTABLE_NAME, VERSION_STRING);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// Generate .jedif output
			LogFile.out().println("Generating .jedif file " + outputFileName);
			// Serialize output
			OutputFileCommandGroup.serializeObject(LogFile.out(), outputFileName, newEnv);
		}
		
		// print domain report if requested by the user
		if (result.userSpecified(DOMAIN_REPORT)) {
		    String fileName = result.getString(DOMAIN_REPORT);
		    System.out.println("Printing domain report to: " + fileName);
		    try {
                replicator.printDomainReport(fileName);
            } catch (FileNotFoundException e) {
                System.out.println("Error writing domain report: unable to open file");
            }
		}
		
		out.println();
		
	}
	
}
