package edu.byu.ece.edif.jedif;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.util.clockdomain.ClockDomainParser;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.graph.EdifPortRefGroupGraph;
import edu.byu.ece.edif.util.graph.EdifPortRefGroupNode;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifAnalyzeCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.MergeParserCommandGroup;
import edu.byu.ece.graph.BasicGraph;
import edu.byu.ece.graph.Edge;
import edu.byu.ece.graph.algorithms.NearestNeighbors;
import edu.byu.ece.graph.algorithms.TreeMapSparseAllPairsShortestPath;
import edu.byu.ece.graph.dfs.BasicDepthFirstSearchTree;
import edu.byu.ece.graph.dfs.DepthFirstTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * A main method class that analyzes the flattened EDIF cell in preparation for future TMR steps.
 * It creates a circuit description file which is a serialized version of a CircuitDescription object.
 * 
 */
public class JEdifBuildAnalyzeV5 extends EDIFMain {
    
	public static String NEIGHBOR = "nearest_neighbor";
	public static String TREES_TO_TRIPLICATE = "num_trees";
	public static String CLOCK_DOMAIN = "clock_domain";
	
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
		parser.addCommand(new ShortestPathAnalysisOption());
        parser.addCommand(new FlaggedOption( NEIGHBOR,JSAP.INTEGER_PARSER, "0", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, NEIGHBOR, 
        	"Nearest neighbor search" ));
        parser.addCommand(new FlaggedOption( TREES_TO_TRIPLICATE,JSAP.INTEGER_PARSER, "0", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, TREES_TO_TRIPLICATE, 
    	"Number of trees to mitigate" ));
        parser.addCommand(new Switch( CLOCK_DOMAIN).setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag(CLOCK_DOMAIN).setDefault("false").setHelp("Isolate SCC analysis within clock domains"));
		
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
    		//EdifPortRefGroupGraph graph = new EdifPortRefGroupGraph(workCell);
    		EdifCellInstanceGraph graph = new EdifCellInstanceGraph(workCell);
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
    			//lut6_2ConnectivityAnalysis(workCell, graph);
    		}

    		// Perform Clock Domain analysis
    		if (result.contains(CLOCK_DOMAIN)) {
    			out.println("Clock Domain Analysis");

    			Map<EdifNet, Set<EdifNet>> domainNets = ClockDomainParser.classifyNets(workCell);
    			for (EdifNet sourceClock : domainNets.keySet()) {
        	        // Remove all edges associated with clock net
    				Set<EdifCellInstanceEdge> clockEdgesToRemove = graph.getEdges(sourceClock);
        	        graph.removeEdges(clockEdgesToRemove);

        	        Set<EdifNet> nets = domainNets.get(sourceClock);
    				out.println("\tClock domain "+sourceClock+" has "+nets.size()+" nets ("+clockEdgesToRemove.size() + " clock edges removed)");
    			}
    			
    			Map<EdifNet, Map<EdifNet, Set<EdifNet>>> crossings = ClockDomainParser.getClockCrossings(workCell, domainNets);
    			Set<EdifNet> netsToRemove = new HashSet<EdifNet>();
    	        for (EdifNet sourceClock : crossings.keySet()) {
    	        	for (EdifNet sinkClock : crossings.get(sourceClock).keySet()) {
    	        		Set<EdifNet> crossingNets = crossings.get(sourceClock).get(sinkClock);
    	        		out.println("  Crossings from "+sourceClock+" to "+sinkClock+" "+crossingNets.size()+" nets");
    	        		for (EdifNet crossingClock : crossingNets) {
    	        			//out.println("    "+crossingClock);
    	        			netsToRemove.add(crossingClock);
    	        		}
    	        	}
    	        }
    	        Set<EdifCellInstanceEdge> edgesToRemove = graph.getEdges(netsToRemove);
    	        graph.removeEdges(edgesToRemove);
    	        out.println(netsToRemove.size() + " clock crossing nets Removed");
    	        out.println(edgesToRemove.size() + " clock crossing edges Removed");

    	        // Remove actual clocks from the graph
    	        
    	        out.println("Clock Domain Analysis - Done");
    		}

    		// Perform shortest path decomponsition
    		/*
    		int iterations = ShortestPathAnalysisOption.getShortestPath(result);
    		if (iterations > 0) {
    			// perform SCC cutting 
        		SCCDepthFirstSearch sccDFS = new SCCDepthFirstSearch(graph);
    			//shortestPathFeedbackAnalysis(iterations, graph, sccDFS);   			
    		} else {
    		*/
    		SCCDepthFirstSearch sccDFS = null;
    		sccDFS = new SCCDepthFirstSearch(graph);
    		out.println("Regular SCC Decomposition");
    		sccSummary(sccDFS, graph, out, true);
    		
    		int neighborDistance = result.getInt(NEIGHBOR);
    		if (neighborDistance > 0) {
    			out.println("Neigbor SCC Decomposition");
    			sccDFS = NearestNeighbors.nearestNeighborDecomposition(graph, neighborDistance);
    			sccSummary(sccDFS, graph, out, true);		
    		}

    		// Now that the SCC analysis is done, determine the nodes that are to be triplicated
    		Set<Set<EdifCellInstance>> groupsOfECIsToTriplicate = new HashSet<Set<EdifCellInstance>>();
    		int treesToTriplicate = result.getInt(TREES_TO_TRIPLICATE);
    		List<DepthFirstTree> sortedTrees =  sccDFS.getSizeSortedTreeList();
    		if (treesToTriplicate == 0)
    			treesToTriplicate = sccDFS.getTrees().size();
    		for (int i = 0; i < treesToTriplicate; i++) {
    			HashSet<EdifCellInstance> instanceGroup = new HashSet<EdifCellInstance>();
    		}
    			
        }

	}

	public static void graphSummary(BasicGraph graph, PrintStream out) {
		out.println("\tNodes="+graph.getNodes().size()+" edges="+graph.getEdges().size());
	}
	public static void cellSummary(EdifCell cell, PrintStream out) {
		out.println("\tInstances="+cell.getSubCellList().size()+" nets="+cell.getNetList().size());
	}
	public static void sccSummary(SCCDepthFirstSearch dfs, BasicGraph graph, PrintStream out) {
		sccSummary(dfs, graph, out, false);
	}

	public static void sccSummary(SCCDepthFirstSearch dfs, BasicGraph graph, PrintStream out, boolean sortBySize) {

		out.println("\t"+dfs.getSingleNodes().size()+" feed-forward nodes");
		out.println("\t"+dfs.getTopologicallySortedTreeList().size() + " trees");

		if (!sortBySize) {
			for (DepthFirstTree t : dfs.getTopologicallySortedTreeList()) {
				treeSummary(t,graph,out);
	        }			
		} else {
			int currentSize = -1;
			int currentSizeCount = 0;
			for (DepthFirstTree t : dfs.getSizeSortedTreeList()) {
				int treeSize = t.getNodes().size();
				if (treeSize != currentSize) {
			        if (currentSize != -1)
			        	// print old tree
			        	out.println("Tree: " + currentSize + " nodes ("+currentSizeCount+"x)");
			        currentSizeCount = 1;
			        currentSize = treeSize;
				} else {
					currentSizeCount++;
				}
				//treeSummary(t,graph,out);
	        }			
			// print out last tree
			out.println("Tree: " + currentSize + " nodes ("+currentSizeCount+"x)");
		}
	}
	
	public static void treeSummary(DepthFirstTree dfst, BasicGraph graph, PrintStream out) {
        BasicDepthFirstSearchTree tree = (BasicDepthFirstSearchTree) dfst;
        out.println("Tree: " + tree.getNodes().size()+ " nodes ");
        //BasicGraph sccGraph = graph.getSubGraph(tree.getNodes());
        //int allEdges = sccGraph.getEdges().size();
		//out.println(allEdges+ " edges");
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

    private static void dottyInstanceSubgraph(EdifCellInstance eci, EdifPortRefGroupGraph graph, String filename) {
		List<EdifPortRefGroupNode> nodes = graph.getInstanceNodes(eci);
		HashSet<Object> ancestorsPredecessorsAndNodes = new HashSet<Object>();
		ancestorsPredecessorsAndNodes.addAll(nodes);
		for(EdifPortRefGroupNode newnode : nodes) {
			ancestorsPredecessorsAndNodes.addAll(graph.getSuccessors(newnode));
			ancestorsPredecessorsAndNodes.addAll(graph.getPredecessors(newnode));
		}
		graph.getSubGraph(ancestorsPredecessorsAndNodes).toDotty(filename);
    }
		
	private static void shortestPathFeedbackAnalysis(int iterations, EdifPortRefGroupGraph graph, SCCDepthFirstSearch dfs) {

		for (DepthFirstTree t : dfs.getTopologicallySortedTreeList()) {
            BasicDepthFirstSearchTree tree = (BasicDepthFirstSearchTree) t;
            treeSummary(t, graph, out);
            BasicGraph sccGraph = graph.getSubGraph(tree.getNodes());
    		out.println("Calculating all pairs shortest path of "+iterations+" iterations...");
    		TreeMapSparseAllPairsShortestPath apsp = TreeMapSparseAllPairsShortestPath.shortestPath(sccGraph, iterations);
    		out.println("Finding edges to cut from shortest path analysis...");
    		Set<Edge> edgesToCut = getShortestPathEdgesToCut(sccGraph, apsp);
    		if (edgesToCut.size() == 0) {
    			out.println("\tNo Edges to Remove");
    		} else {
    			out.println("Removing " + edgesToCut.size() + " edges.");
    			sccGraph.removeEdges(edgesToCut);
    			out.println("Graph now has " + sccGraph.getNodes().size() + " nodes and "+ sccGraph.getEdges().size()+" edges.");
    			SCCDepthFirstSearch sccDFS = new SCCDepthFirstSearch(sccGraph);
    			out.println("\t New SCC decomposition");
    			if (sccDFS.getTopologicallySortedTreeList().size() == 1) {
    				out.println("\tEdge removal did not decompose SCC (1 SCC)");
    			} else {
    				out.println("\t"+sccDFS.getSingleNodes().size()+" feed-forward nodes");
    				out.println("\t"+sccDFS.getTopologicallySortedTreeList().size() + " trees");
    			}
    		}
		}		
	}	
	
	private static Set<Edge> getShortestPathEdgesToCut(BasicGraph graph, TreeMapSparseAllPairsShortestPath apsp) {
		Set<Edge> toReturn = new HashSet<Edge>();
		for(Edge e : graph.getEdges()) {
			Integer backweight = apsp.getValue(e.getSink(), e.getSource());
			if (backweight == null) { //this means infinite
				toReturn.add(e);
			}
		}	
		return toReturn;
	}
	
	private static void lut6_2ConnectivityAnalysis(EdifCell topCell, EdifPortRefGroupGraph graph) {
		String LUT6_2 = "lut6_2";
		
		ArrayList<EdifCellInstance> luts2cut = new ArrayList<EdifCellInstance>();
		// Iterate over all instances in the graph to see if any instance matches a
		// cut instance name or a cut cell type name
		for (Object node : graph.getNodes() ) {
			if (node instanceof EdifPortRefGroupNode) {
				EdifCellInstance eci = ((EdifPortRefGroupNode)node).getEdifCellInstance();
				if(eci != null) {
				    String cellType = eci.getCellType().getName();
				    if (cellType.compareToIgnoreCase(LUT6_2) == 0)
				        luts2cut.add(eci);
				}
			}
		}

		if (luts2cut.size() > 0) {
			System.out.println(luts2cut.size() + " LUT6_2 instances found");
			//graph.toDotty("/net/fpga1/users/whowes/Desktop/before.dot");
			for (Object node : luts2cut) {
				EdifCellInstance lut6_2 = (EdifCellInstance) node;
				
				//dottyInstanceSubgraph(lut6_2, graph, "/net/fpga1/users/whowes/Desktop/before.dot");
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
					/*we have to also manually add I5 to the O5 don't cares
					(since with a forced input it's like there's only 5 inputs) */
					O5_dont_care.add(new Integer(5));

					
					// Find the single instance associated with the lut6_2 EdifCellInstance
					List<EdifPortRefGroupNode> nodes = graph.getInstanceNodes(lut6_2);
					if(nodes.size() > 1) {
						out.println("Warning: more than 1 EdifPortRefGroupNode for this EdifCellInstance (was it already split?)");
					}
					EdifPortRefGroupNode graphNode = nodes.get(0);
					
					List<EdifPortRef> o5Group = new ArrayList<EdifPortRef>();
					List<EdifPortRef> o6Group = new ArrayList<EdifPortRef>();
					//put O6 and the do care EPRs for O6 in one group
					//put O5 and the do care EPRs for O5 in another
					Collection<EdifPortRef> allEPRs = lut6_2.getAllEPRs();
					for(EdifPortRef epr : allEPRs) {
						String portName = epr.getPort().getName();
						int inputIndex = Integer.parseInt(portName.substring(1));
						if (portName.equalsIgnoreCase("O5")) {
							o5Group.add(epr);
						}
						else if (portName.equalsIgnoreCase("O6")) { 
							o6Group.add(epr);
						}
						else { //must be an input
							if(!O5_dont_care.contains(new Integer(inputIndex))) {
								o5Group.add(epr);
							}
							if(!O6_dont_care.contains(new Integer(inputIndex))) {
								o6Group.add(epr);
							}	
						}
					}
					//actually split the nodes in the graph
					List<List<EdifPortRef>> groupsToSplit = new ArrayList<List<EdifPortRef>>();
					groupsToSplit.add(o5Group);
					groupsToSplit.add(o6Group);
					graph.splitNode(graphNode, groupsToSplit);				
				}
				//dottyInstanceSubgraph(lut6_2, graph, "/net/fpga1/users/whowes/Desktop/after.dot");
			}
			System.out.println("After splitting LUT6_2 nodes:");
			graphSummary(graph, out);
			//graph.toDotty("/net/fpga1/users/whowes/Desktop/after.dot");
						
			
		} else {
			out.println("Warning: No LUT6_2 instances");
		}
	}
}


class ShortestPathAnalysisOption extends FlaggedOption {
	public ShortestPathAnalysisOption() {
		super(SHORTEST_PATH);
		setStringParser(JSAP.INTEGER_PARSER);
		setDefault("0");
		setRequired(false);
		setShortFlag(JSAP.NO_SHORTFLAG);
		setLongFlag(SHORTEST_PATH);
	}

	public static int getShortestPath(JSAPResult result) {
		return result.getInt(SHORTEST_PATH);
	}
	public static String SHORTEST_PATH = "short_path";
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
