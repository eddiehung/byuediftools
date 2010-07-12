package edu.byu.ece.edif.jedif;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import edu.byu.ece.edif.arch.xilinx.XilinxInitAttribute;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifUtils;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.SparseAllPairsShortestPath;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.graph.EdifPortRefGroupGraph;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifAnalyzeCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.MergeParserCommandGroup;
import edu.byu.ece.graph.BasicGraph;
import edu.byu.ece.graph.dfs.BasicDepthFirstSearchTree;
import edu.byu.ece.graph.dfs.DepthFirstTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * A main method class that analyzes the flattened EDIF cell in preparation for future TMR steps.
 * It creates a circuit description file which is a serialized version of a CircuitDescription object.
 * 
 */
public class JEdifBuildAnalyzeV5 extends EDIFMain {
    
	
	public static void main(String[] args) {
		
		// Set default PrintStream objects
		out = System.out;
		err = System.err;
		
		EXECUTABLE_NAME = "JEdifBuildAnalyzeV5";
		TOOL_SUMMARY_STRING = "Experimental circuit build and analyze for V5";
		
		printProgramExecutableString(out);

		// Create new parser
		EdifCommandParser parser = new EdifCommandParser();
		parser.addCommands(new MergeParserCommandGroup());
		parser.addCommand(new SingleCellTypeFlaggedOption());
		parser.addCommand(new LUT6_2FlaggedOption());
		parser.addCommand(new DoAllCellsSwitch());
		
        // Start the parsing
		JSAPResult result = parser.parse(args, System.err);
		if (!result.success())
			System.exit(1);

		//LogFileCommandGroup.CreateLog(result);
        printProgramExecutableString(LogFile.log());
		out = LogFile.out();
		err = LogFile.err();

		
        // Parse EDIF file and generate a EdifEnvironment object
        EdifEnvironment env = MergeParserCommandGroup.getEdifEnvironment(result);

		// Get the cells to operate on
        Collection<EdifCell> cellsToEvaluate = new ArrayList<EdifCell>();
        if (DoAllCellsSwitch.evaluateAllCells(result)) {
        	String singleCell = SingleCellTypeFlaggedOption.getCellToUseName(result);
        	if (singleCell != null) {
        		err.println("Error: cannot specify both a single cell and all cells");
        		System.exit(1);
        	}
        	for (EdifCell cell : env.getLibraryManager().getCells()) {
        		if (!cell.isLeafCell()) {
        			cellsToEvaluate.add(cell);
        		}
        	}
        } else {
            EdifCell workCell = SingleCellTypeFlaggedOption.getCellToUse(result, env);
        	cellsToEvaluate.add(workCell);
        }

 
        boolean flatten = MergeParserCommandGroup.performFlatten(result);

        // Work on all of the cells
        for (EdifCell cell : cellsToEvaluate) {
        	EdifCell workCell = cell;
        	
        	out.println("Operating on Cell Type:"+workCell.getName());
        	
        	// 1. Flatten if necessary
        	if (flatten) {
        		workCell = flatten(cell);        		
        	}
        	cellSummary(workCell,out);
        	
            // 2. Create an instance graph of the cell
    		EdifPortRefGroupGraph graph = new EdifPortRefGroupGraph(workCell);
    		graphSummary(graph, out);
    		
    		// For now, ignore IOB stuff
    		/*
    		boolean packInputRegs = IOBCommandGroup.packInputRegisters(result);
    		boolean packOutputRegs = IOBCommandGroup.packOutputRegisters(result);
    		boolean remove_iob_feedback = JEdifAnalyzeCommandGroup.noIOBFB(result);
    		
    		// TODO: is this hard coded for Virtex? Does this need to be modified for other architectures?
    		IOBAnalyzer iobAnalyzer = new XilinxVirtexIOBAnalyzer(topCell, _instanceGraph, packInputRegs, packOutputRegs);
    		
    		// We don't need source to source edges after the analyer is done with the graph.
    		// Once we have completed IOB analysis, we no longer need the source to source edges (it is needed
    		// for IOB analysis)
    		_instanceGraph.removeSourceToSourceEdges();

    		// Perform IOB feedback analysis
    		List<EdifPortRefEdge> iobFeedbackEdges = iobFeedbackAnalysis(iobAnalyzer, _instanceGraph, use_bad_cut_conn, remove_iob_feedback, arch, topCell);

    		 *
    		 */    		

    		// Perform instance and cell cutting
    		String[] cells_to_cut = JEdifAnalyzeCommandGroup.getCellsToCut(result);
    		String[] instances_to_cut = JEdifAnalyzeCommandGroup.getInstancesToCut(result);
    		instanceConnectivityAnalysis(cells_to_cut, instances_to_cut, graph);

    		// Perform LUT6_2 feedback
    		if (LUT6_2FlaggedOption.performLUT6_2Decomposition(result)) {
    			lut6_2ConnectivityAnalysis(workCell, graph);
    		}
    		
    		SCCDepthFirstSearch sccDFS = new SCCDepthFirstSearch(graph);
    		sccSummary(sccDFS, graph, out);
        }

	}

	public static void graphSummary(BasicGraph graph, PrintStream out) {
		out.println("\tNodes="+graph.getNodes().size()+" edges="+graph.getEdges().size());
	}
	public static void cellSummary(EdifCell cell, PrintStream out) {
		out.println("\tInstances="+cell.getSubCellList().size()+" nets="+cell.getNetList().size());
	}
	public static void sccSummary(SCCDepthFirstSearch dfs, BasicGraph graph, PrintStream out) {

		int j=1;
		out.println("\t"+dfs.getSingleNodes().size()+" feed-forward nodes");
		out.println("\t"+dfs.getTopologicallySortedTreeList().size() + " trees");

		for (DepthFirstTree t : dfs.getTopologicallySortedTreeList()) {
            BasicDepthFirstSearchTree tree = (BasicDepthFirstSearchTree) t;
            out.print("Tree " + j++ + ": " + tree.getNodes().size()+ " nodes ");
            BasicGraph sccGraph = graph.getSubGraph(tree.getNodes());
            int allEdges = sccGraph.getEdges().size();
    		out.println(allEdges+ " edges");
        }
	}
	
	/**
	 * This method is used to modify the connectivity graph of the circuit based on user parameters
	 * regarding instance connectivity. To help manage the connectivity of the circuit graph, some
	 * instances may be tagged as not fully connected. This method will take these parameters to modify
	 * the connectivity graph. Specifically, this method will remove edges based on the user input.
	 */
	private static void instanceConnectivityAnalysis(String[] cells_to_cut, String[] instances_to_cut, 
			BasicGraph eciConnectivityGraph) {
		
		if (cells_to_cut == null && instances_to_cut == null)
			return;
		
		ArrayList<EdifCellInstance> instancesToCut = new ArrayList<EdifCellInstance>();
		// Iterate over all instances in the graph to see if any instance matches a
		// cut instance name or a cut cell type name
		for (Object node : eciConnectivityGraph.getNodes() ) {
			if (node instanceof EdifCellInstance) {
				EdifCellInstance eci = (EdifCellInstance) node;
				String cellType = eci.getCellType().getName();
				String cellName = eci.getName();
				// Check the cell type of the instance against the list of 
				// cell types that should be cut
				if (cells_to_cut != null)
					for (String s : cells_to_cut) {
						if (s.compareToIgnoreCase(cellType) == 0)
							instancesToCut.add(eci);
					}
				// Check the cell instance name against the list of instances to cut
				if (instances_to_cut != null)
					for (String s : instances_to_cut) {
						if (s.compareToIgnoreCase(cellName) == 0)
							instancesToCut.add(eci);
					}					
			}
		}

		if (instancesToCut.size() > 0) {
			int edgeCutCount = 0;
			int initialEdgeCout = eciConnectivityGraph.getEdges().size();
			for (EdifCellInstance eci : instancesToCut) {
				Collection<?> edgesToCut = eciConnectivityGraph.getOutputEdges(eci);
				eciConnectivityGraph.removeEdges(edgesToCut);
				out.println("Cutting output "+edgesToCut.size() + " edges from instance "+eci+"  from graph");
				edgeCutCount += edgesToCut.size();
			}
			out.println(edgeCutCount + " Edges cut from graph. "+initialEdgeCout + " Edges in original graph. "
					+ eciConnectivityGraph.getEdges().size() + " edges in final graph");
		}		
	}

	
    private static FlattenedEdifCell flatten(EdifCell cellToFlatten) {
        FlattenedEdifCell flatCell = null;

        try {
            if (cellToFlatten instanceof FlattenedEdifCell) {
                flatCell = (FlattenedEdifCell) cellToFlatten;
            } else {                
                flatCell = new FlattenedEdifCell(cellToFlatten, "_flat");
            }
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }

        /*
        out.println("\tFlattened circuit contains " + EdifUtils.countRecursivePrimitives(flatCell) + " primitives, "
                + EdifUtils.countRecursiveNets(flatCell) + " nets, and " + EdifUtils.countPortRefs(flatCell, true)
                + " net connections");
	*/
        return flatCell;
    }

		
	private static void lut6_2ConnectivityAnalysis(EdifCell topCell, EdifPortRefGroupGraph graph) {
		String LUT6_2 = "lut6_2";
		
		ArrayList<EdifCellInstance> luts2cut = new ArrayList<EdifCellInstance>();
		// Iterate over all instances in the graph to see if any instance matches a
		// cut instance name or a cut cell type name
		for (Object node : graph.getNodes() ) {
			if (node instanceof EdifCellInstance) {
				EdifCellInstance eci = (EdifCellInstance) node;
				String cellType = eci.getCellType().getName();
				if (cellType.compareToIgnoreCase(LUT6_2) == 0)
					luts2cut.add(eci);
			}
		}

		if (luts2cut.size() > 0) {
			for (Object node : luts2cut) {
				EdifCellInstance lut6_2 = (EdifCellInstance) node;
				// get properties
				Property init_string = lut6_2.getProperty("INIT");
				if (init_string == null) {
					out.println("Warning: LUT"+lut6_2+" has no INIT string");
				} else {

					// Figure out which pins are don't care
					String init = init_string.getValue().toString();
					XilinxInitAttribute attr_O6 = new XilinxInitAttribute(init);

					List<Integer> inputConstants = new ArrayList<Integer>(1);
					inputConstants.add(new Integer(5));
					List<Integer> values = new ArrayList<Integer>(1);
					values.add(new Integer(0));					
					XilinxInitAttribute attr_O5 = attr_O6.getInitAttributeWithForcedInputs(inputConstants, values);
					
					List<Integer> O6_dont_care = attr_O6.getDontCareInputs();
					List<Integer> O5_dont_care = attr_O5.getDontCareInputs();
					
					// Find the single instance associated with the lut6_2 EdifCellInstance
					//
					// Returns all group nodes associated with a single instance. At this point,
					// there should be only one.
					//
					// Collection<?> graph.getInstanceNodes(EdifCellInstance eci);
					
					// 1. Create a new node for O6
					// 2. Move the EPR assocaited with the O6 output within the origional node to the new O6 node
					// 3. Remove the I5 input on the O5 node (I5 is not used for O5)
					// 4. Compute don't cares for O5 and remove from the O5 node
					// 5. Compute don't cares for O6 and remove them from the O6 node

					
				}
			}
		} else {
			out.println("Warning: No LUT6_2 instances");
		}
	}
}

class SingleCellTypeFlaggedOption extends FlaggedOption {

    public SingleCellTypeFlaggedOption() {
    	super(CELL_TO_USE);
		setStringParser(JSAP.STRING_PARSER);
		setLongFlag(CELL_TO_USE);
		setUsageName(CELL_TO_USE);
		setHelp("The cell type to operate on. Default cell is top-level.");
    }

    public static EdifCell getCellToUse(JSAPResult result, EdifEnvironment env) {
    	
    	String cellName = getCellToUseName(result);
    	EdifCell workCell = null;
        if (cellName != null) {
        	EdifLibraryManager mang = env.getLibraryManager();
        	workCell = mang.getCell(cellName);
        	if (workCell == null) {
        		throw new EdifRuntimeException("No cell by name '"+cellName+"' found in Edif environment.");
        	}
        } else {
        	workCell = env.getTopCell();
        }
        return workCell;
    }

    public static String getCellToUseName(JSAPResult result) {
        return result.getString(CELL_TO_USE);    	
    }

    public static final String CELL_TO_USE = new String("cell");
}

/**
 * A flagged option for listing the "include" files for an Edif merging operation.
 */
class LUT6_2FlaggedOption extends Switch {

	// Additional command for processing LUT6_2
    public LUT6_2FlaggedOption() {
        super(LUT6_2);
        setLongFlag(LUT6_2);
        setHelp("Process LUT6_2 feedback");
    }

    public static boolean performLUT6_2Decomposition(JSAPResult result) {
    	return result.getBoolean(LUT6_2);
    }

    public static final String LUT6_2 = "lut6_2";
}

/**
 * A flagged option for listing the "include" files for an Edif merging operation.
 */
class DoAllCellsSwitch extends Switch {

	// Additional command for processing LUT6_2
    public DoAllCellsSwitch() {
        super(DO_ALL);
        setLongFlag(DO_ALL);
        setHelp("Process ALL cells");
    }

    public static boolean evaluateAllCells(JSAPResult result) {
    	return result.getBoolean(DO_ALL);
    }

    public static final String DO_ALL = "all_cells";

}
