package edu.byu.ece.edif.jedif;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.QualifiedSwitch;

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

    static String PRINT_PRIMITIVES = new String("no_prims");
    
    public static void main(String[] args) {
        out = System.out;
        err = System.err;
        
        EXECUTABLE_NAME = "JEdifInfo";
        TOOL_SUMMARY_STRING = "Creates merged netlists in a .jedif file format from multiple .edf files";
        
        printProgramExecutableString(out);
        
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new MergeParserCommandGroup());
        parser.addCommands(new LogFileCommandGroup("build.log"));
        parser.addCommand(new QualifiedSwitch( PRINT_PRIMITIVES, JSAP.STRING_PARSER, JSAP.NO_DEFAULT, 
        		JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, PRINT_PRIMITIVES, "Print primitives." ));
 		
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
        printEdifCell(topCell, result.getBoolean(PRINT_PRIMITIVES),out);
        
    }    
    
    protected static void printEdifCell(EdifCell cell, boolean printPrimitives, PrintStream out) {
    	printEdifCell(cell, printPrimitives, new String(""), out);
    }
    
    protected static void printEdifCell(EdifCell cell, boolean printPrimitives, String prefix, PrintStream out) {
    	
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
    	TreeSet<ComparableEdifCell> set = new TreeSet<ComparableEdifCell>(); 
    	for (EdifCell child : children.keySet()) {
    		List<EdifCellInstance> instances = children.get(child);
    		if (!child.isLeafCell()) {
    			int num = instances.size();
    			int childSize = num * getRecursiveInnerCell(child);
    			ComparableEdifCell cec = new ComparableEdifCell(child,childSize);
    			set.add(cec);
    		}
    	}
    	for (ComparableEdifCell ccell : set) {
    		EdifCell child = ccell._cell;
    		int childSize = ccell.size;
    		List<EdifCellInstance> instances = children.get(child);
    		out.println(prefix+child.getName()+ " "+ childSize+" recursive cells ("+((childSize*100)/totalInstances)+"%) "+
    				instances.size()+" instances");    		
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
    	if (printPrimitives) {
    		for (EdifCell child : children.keySet()) {
    			List<EdifCellInstance> instances = children.get(child);
    			if (child.isPrimitive()) {
    				out.println(prefix+"  "+child.getName()+ " (primitive) "+instances.size()+" instances");
    			}
    		}
    	}
    	for (EdifCell child : children.keySet()) {
    		if (!child.isLeafCell())
    			printEdifCell(child, printPrimitives, prefix, out);
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

class ComparableEdifCell implements Comparable {
	public ComparableEdifCell(EdifCell cell, int recursiveSubCells) {
		_cell = cell;
		size = recursiveSubCells;
	}
	public int compareTo(Object o) {
		ComparableEdifCell c = (ComparableEdifCell) o;
		if (c.getSize() > getSize())
			return 1;
		if (c.getSize() == getSize())
			return 0;
		return -1;
	}
	public EdifCell getCell() { return _cell; }
	public int getSize() { return size; }
	EdifCell _cell;
	int size;
}