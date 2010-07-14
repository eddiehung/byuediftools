package edu.byu.ece.edif.jedif;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.MergeParserCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.TechnologyCommandGroup;

/**
 * A main function that generates the .jedif file used for EDIF post processing. This main function performs a
 * number of optional steps:
 * - Merging of EDIF files
 * - Sterilization (RLOC, FMAP removal)
 * - Half latch removal
 * 
 */
public class JEdifInfo extends EDIFMain {

    public static PrintStream out;
    public static PrintStream err;
    
    public static void main(String[] args) {
        out = System.out;
        err = System.err;
        
        EXECUTABLE_NAME = "JEdifInfo";
        TOOL_SUMMARY_STRING = "Creates merged netlists in a .jedif file format from multiple .edf files";
        
        printProgramExecutableString(out);
        
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new MergeParserCommandGroup());
        parser.addCommands(new LogFileCommandGroup("build.log"));
 
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
      
        EdifCell topCell = env.getTopCell();
        printEdifCell(topCell, out);
        
    }    
    
    protected static void printEdifCell(EdifCell cell, PrintStream out) {
    	printEdifCell(cell, new String(""), out);
    }
    
    protected static void printEdifCell(EdifCell cell, String prefix, PrintStream out) {
    	
    	int totalInstances = getRecursiveInnerCell(cell);
    	out.println(prefix + "Cell: " + cell.getName()+" "+totalInstances + " recursive cells " + 
    			cell.getCellInstanceList().size()+" inner cells "+cell.getNetList().size() + " nets");
    	prefix += "  ";
    	HashMap<EdifCell, List<EdifCellInstance>> children = new HashMap<EdifCell, List<EdifCellInstance>>();
    	for (EdifCellInstance eci : cell.getCellInstanceList()) {
    		EdifCell type = eci.getCellType();
    		List<EdifCellInstance> instances = children.get(type);
    		if (instances == null) {
    			instances = new ArrayList<EdifCellInstance>();
    			children.put(type,instances);
    		}
    		instances.add(eci);
    	}
    	
    	// print non leaf cells and their recursive size
    	for (EdifCell child : children.keySet()) {
    		List<EdifCellInstance> instances = children.get(child);
    		if (!child.isLeafCell()) {
    			int num = instances.size();
    			int childSize = num * getRecursiveInnerCell(child);
        		out.println(prefix+child.getName()+ " "+ childSize+" recursive cells ("+((childSize*100)/totalInstances)+"%) "+
        				instances.size()+" instances");
    		}
    	}

    	// Print black boxes
    	for (EdifCell child : children.keySet()) {
    		List<EdifCellInstance> instances = children.get(child);
    		if (child.isBlackBox()) {
        		out.println(prefix+child.getName()+ " (blackbox) "+instances.size()+" instances");
    		}
    	}

    	// print primitives
    	int primitiveCount = 0;
    	for (EdifCell child : children.keySet()) {
    		List<EdifCellInstance> instances = children.get(child);
    		if (child.isPrimitive()) {
    			primitiveCount+=instances.size();
    		}
    	}
    	out.println(prefix+"Local Primitives - "+primitiveCount+" ("+((primitiveCount*100)/totalInstances)+"%) ");
    	for (EdifCell child : children.keySet()) {
    		List<EdifCellInstance> instances = children.get(child);
    		if (child.isPrimitive()) {
        		out.println(prefix+"  "+child.getName()+ " (primitive) "+instances.size()+" instances");
    		}
    	}
    	for (EdifCell child : children.keySet()) {
    		if (!child.isLeafCell())
    			printEdifCell(child, prefix, out);
    	}
    	
    }
    
    protected static int getRecursiveInnerCell(EdifCell cell) {
    	int cells = 0;
    	if (cell.isLeafCell())
    		return 0;
    	for (EdifCellInstance eci : cell.getCellInstanceList()) {
    		EdifCell type = eci.getCellType();
    		if (type.isLeafCell())
    			cells++;
    		else
    			cells += getRecursiveInnerCell(type);
    	}
    	return cells;
    }
    
}
