package edu.byu.ece.edif.jedif;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifUtils;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.EdifReplicationPropertyReader;
import edu.byu.ece.edif.util.graph.CustomGraphToDotty;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.MergeParserCommandGroup;

public class JEdifDotty extends EDIFMain {
    
    public static String OUTPUT_FILE = "output";
    public static String IGNORE_NETS = "ignore_nets";
    public static char LIST_DELIMITER = ',';
    
    public static void main(String args[]) {

        // Define the print streams for this program
        // Print executable heading

        EXECUTABLE_NAME = "JEdifDotty";
        TOOL_SUMMARY_STRING = "Creates a dotty graph from an EDIF file";
        printProgramExecutableString(System.out);

        // Parse command line options
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new MergeParserCommandGroup());
        
        FlaggedOption output_file = new FlaggedOption(OUTPUT_FILE);
        output_file.setStringParser(JSAP.STRING_PARSER);
        output_file.setRequired(JSAP.REQUIRED);
        output_file.setShortFlag('o');
        output_file.setLongFlag(OUTPUT_FILE);
        output_file.setUsageName(OUTPUT_FILE);
        output_file.setHelp("Filename and path to the .dot file that will be created. Required.");
        
        FlaggedOption ignore_nets = new FlaggedOption(IGNORE_NETS);
        ignore_nets.setStringParser(JSAP.STRING_PARSER);
        ignore_nets.setRequired(JSAP.NOT_REQUIRED);
        ignore_nets.setShortFlag(JSAP.NO_SHORTFLAG);
        ignore_nets.setLongFlag(IGNORE_NETS);
        ignore_nets.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        ignore_nets.setList(JSAP.LIST);
        ignore_nets.setListSeparator(LIST_DELIMITER);
        ignore_nets.setUsageName(IGNORE_NETS);
        ignore_nets
                .setHelp("Comma-separated list of net names to ignore from the circuit. This " +
                		"can be useful for removing clock nets from the graph since these make it" +
                		" more difficult for dotty to produce a display.");

        
        try {
            parser.registerParameter(output_file);
            parser.registerParameter(ignore_nets);
        } catch (JSAPException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        
        JSAPResult result = parser.parse(args, System.err);
        if (!result.success())
            System.exit(1);

        printProgramExecutableString(LogFile.log());

        Set<String> ignoreNets = new LinkedHashSet<String>();
        if (result.userSpecified(IGNORE_NETS)) {
            for (String ignoreNet : result.getStringArray(IGNORE_NETS)) {
                ignoreNets.add(ignoreNet.toLowerCase());
            }
        }
        
        // Parse EDIF file and generate a EdifEnvironment object
        EdifEnvironment top = MergeParserCommandGroup.getEdifEnvironment(result);
       
        // Make a Set of cells not to flatten
        Set<EdifCell> noFlattenCells = new LinkedHashSet<EdifCell>();
        for (EdifCell cell : top.getLibraryManager().getCells()) {
            if (EdifReplicationPropertyReader.isDoNotFlattenCell(cell) || EdifReplicationPropertyReader.isPremitigatedCell(cell))
                noFlattenCells.add(cell);
        }
        
        // Flatten netlist if necessary
        if (MergeParserCommandGroup.performFlatten(result)) {
            System.out.print("Flattening . . .");
            FlattenedEdifCell flatCell = null;
            EdifCell oldCell = top.getTopCell();
            try {
                flatCell = new FlattenedEdifCell(oldCell, noFlattenCells);
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            }

            // Set flat cell at top (there should be an automated way for this)
            EdifCellInstance flatInstance = null;
            EdifDesign newDesign = null;
            try {
                flatInstance = new EdifCellInstance(flatCell.getName(), null, flatCell);
                newDesign = new EdifDesign(flatCell.getEdifNameable());
            } catch (InvalidEdifNameException e1) {
                e1.toRuntime();
            }
            newDesign.setTopCellInstance(flatInstance);
            // copy design properties
            EdifDesign oldDesign = top.getTopDesign();
            if (oldDesign.getPropertyList() != null) {
                for (Object o : oldDesign.getPropertyList().values()) {
                    Property p = (Property) o;
                    newDesign.addProperty((Property) p.clone());
                }
            }
            top.setTopDesign(newDesign);


            System.out.println("Done");

            System.out.println("\tFlattened circuit contains " + EdifUtils.countRecursivePrimitives(flatCell)
                    + " primitives, " + EdifUtils.countRecursiveNets(flatCell) + " nets, and "
                    + EdifUtils.countPortRefs(flatCell, true) + " net connections");
        }
        
        
        EdifCellInstanceGraph graph = new EdifCellInstanceGraph(top.getTopCell());
        Collection<EdifCellInstanceEdge> edgesToRemove = new ArrayList<EdifCellInstanceEdge>();
        for (EdifCellInstanceEdge edge : graph.getEdges()) {
            if (ignoreNets.contains(edge.getNet().getName().toLowerCase())) {
                edgesToRemove.add(edge);
            }
        }
        graph.removeEdges(edgesToRemove);
        
        CustomGraphToDotty cgtd = new CustomGraphToDotty();
        
        String dotty = cgtd.createDottyBody(graph);
        
        CustomGraphToDotty.printFile(result.getString(OUTPUT_FILE), dotty);
        
        System.out.println();
        
    }    

}
