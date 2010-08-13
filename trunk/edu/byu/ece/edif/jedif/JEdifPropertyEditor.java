package edu.byu.ece.edif.jedif;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import edu.byu.ece.edif.arch.xilinx.XilinxTools;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifTypedValue;
import edu.byu.ece.edif.core.StringTypedValue;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.InputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.MergeParserCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.OutputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.TechnologyCommandGroup;

public class JEdifPropertyEditor extends EDIFMain {

	public static String GENERATE_EDIF_FLAG = "edif";
	public static String SIGNAL_FILE = "signals";
	public static String PROPERTY_NAME = "property";
	public static String VALUE_NAME = "value";
	
	public static boolean DEBUG = true;
	
    public static void main(String[] args) {

    	out = System.out;
        err = System.err;
        
        EXECUTABLE_NAME = "JEdifPropertyEditor";
        TOOL_SUMMARY_STRING = "Edit EDIF Properies";
        
        printProgramExecutableString(out);
        
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new MergeParserCommandGroup());
        parser.addCommands(new LogFileCommandGroup("build.log"));
        parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));
		parser.addCommands(new OutputFileCommandGroup());
        parser.addCommand(new Switch(GENERATE_EDIF_FLAG).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag(GENERATE_EDIF_FLAG).setDefault("false").setHelp("Generate EDIF output instead of .jedif"));
        parser.addCommand(new FlaggedOption( SIGNAL_FILE,JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NO_SHORTFLAG, SIGNAL_FILE, 
        	"Filename of signal file" ));
        parser.addCommand(new FlaggedOption( PROPERTY_NAME,JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NO_SHORTFLAG, PROPERTY_NAME, 
    		"Name of property to add" ));
        parser.addCommand(new FlaggedOption( VALUE_NAME,JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NO_SHORTFLAG, VALUE_NAME, 
    	 	"Name of value to add" ));

        JSAPResult result = parser.parse(args, err);
        if (!result.success())
            System.exit(1);

        LogFileCommandGroup.CreateLog(result);
        out = LogFile.out();
        err = LogFile.err();

        printProgramExecutableString(LogFile.log());

        // Parse EDIF file and generate a EdifEnvironment object
        EdifEnvironment env = MergeParserCommandGroup.getEdifEnvironment(result);
        try {
            TechnologyCommandGroup.getPartFromEDIF(result, env);
        } catch (IllegalArgumentException ex) {
            //don't care right now, but other tools might later
        }
        
        // Open and parse the signal file
        String signalFilename = result.getString(SIGNAL_FILE);
        FileReader signalFile = null;
        try {
        	signalFile = new FileReader(signalFilename);
        } catch (FileNotFoundException e) {
        	System.err.println("File "+signalFile + " not found");
        	System.exit(1);
        }
        LineNumberReader lnr = new LineNumberReader(signalFile);
        
        // Parse properties
        String propertyName = result.getString(PROPERTY_NAME);
        String valueName = result.getString(VALUE_NAME);
        
        // PROPERTY EDITING GOES HERE
        Map<String, List<String>> cellRegexMap = parseSignalFile(lnr);
        processProperties(env, cellRegexMap, propertyName, valueName);
        
        
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
				JEdifNetlist.generateEdifNetlist(env, result, EXECUTABLE_NAME, VERSION_STRING);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// Generate .jedif output
			LogFile.out().println("Generating .jedif file " + outputFileName);
			// Serialize output
			OutputFileCommandGroup.serializeObject(LogFile.out(), outputFileName, env);
		}

        out.println();
    }
    
    
    protected static void processProperties(EdifEnvironment env, Map<String, List<String>> cellRegexMap, String property, String value) {
		EdifTypedValue v = new StringTypedValue(value);
    	
    	for (String cellType : cellRegexMap.keySet()) {
    		EdifCell cell = env.getLibraryManager().getCell(cellType);
    		if (cell == null) {
    			System.err.println("Warning: No cell found - "+cellType);
    			continue;
    		}
    		if (DEBUG) System.out.println("Working on cell "+cellType);
    		List<String> regexes = cellRegexMap.get(cellType);
    		for (String regex : regexes) {
    			if (DEBUG) System.out.println("\t"+regex);
    			Collection<EdifCellInstance> instances = getMatchingInstance(cell, regex);
       			if (instances.size() == 0) {
    				System.err.println("Warning: no instances of "+regex+" in "+cell.getName());
    			}
 
    			for (EdifCellInstance eci : instances) {
    				if (DEBUG) System.out.println("\tFound instance "+eci.getName()+" of type "+eci.getCellType());
    				eci.addProperty(property, v);
    			}
    		}
    	}
    	
    }
    
    protected static Collection<EdifCellInstance> getMatchingInstance(EdifCell cell, String regex) {
    	Collection<EdifCellInstance> instances = cell.getCellInstanceList();
    	List<EdifCellInstance> matchingInstances = new ArrayList<EdifCellInstance>();
    	regex = regex.toLowerCase();
    	Pattern p = Pattern.compile(regex);
    	
    	for (EdifCellInstance eci : instances) {
    		String oldName = eci.getOldName().toLowerCase();
    		String newName = eci.getName().toLowerCase();
    		//if (DEBUG) System.out.println(regex +": "+oldName+" "+newName);
    		if ( 
    				p.matcher(oldName).matches() ||
    				p.matcher(newName).matches() ) {
    			// Matches a string. Is it a sequential element?
    			if (XilinxTools.isSequential(eci.getCellType()))
    				matchingInstances.add(eci);
    			//if (DEBUG) System.out.println("\t\tMatch!");
    		} else {
    			//if (DEBUG) System.out.println("\t\tNo Match!");

    		}
    	}
    	return matchingInstances;
    }
    
    /**
     * Format of file:
     * # indciates a comment
     * <name of cell>:<name regular expression>
     */
    protected static Map<String, List<String>> parseSignalFile(LineNumberReader lnr) {
    	HashMap<String, List<String>> stringMap = new HashMap<String, List<String>>();
    	String readLine = null;
    	do {
    		try {
    			readLine = lnr.readLine();
    		} catch (IOException e) {
    			System.err.println(e);
    			System.exit(1);
    		}
    		if (readLine != null) {
    			// parse line
    			readLine = readLine.trim(); // trim white space    			
    			if (readLine.startsWith("#")) {
    				if (DEBUG) System.out.println("Comment line: "+readLine);
    				continue; // skip comments
    			}
    			// parse line
    			String splitStrings[] = null;
    			try {
    				splitStrings = readLine.split(":", 2);
    			} catch (PatternSyntaxException e) {
        			System.err.println(e);
        			System.exit(1);
    			}
    			if (splitStrings.length != 2) {
    				System.err.println("Warning: bad line: "+readLine);
    				continue;
    			}
    			String cellType = splitStrings[0];
    			String regex = splitStrings[1];
    			List<String> strings = stringMap.get(cellType);
    			if (strings == null) {
    				strings = new ArrayList<String>();
    				stringMap.put(cellType,strings);
    			}
				strings.add(regex);
				if (DEBUG) System.out.println("Adding "+cellType+":"+regex);
    		}
    	} while (readLine != null);

    	return stringMap;
    }
    
}
