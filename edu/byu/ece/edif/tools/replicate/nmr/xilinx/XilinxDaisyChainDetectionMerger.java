package edu.byu.ece.edif.tools.replicate.nmr.xilinx;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.tools.replicate.nmr.dwc.DetectionOutputMerger;
import edu.byu.ece.edif.tools.replicate.wiring.NetManager;
import edu.byu.ece.edif.tools.replicate.wiring.PortConnection;
import edu.byu.ece.edif.tools.replicate.wiring.SinglePortConnection;

public class XilinxDaisyChainDetectionMerger extends DetectionOutputMerger {


	protected XilinxDaisyChainDetectionMerger() {

	}

	public static XilinxDaisyChainDetectionMerger getInstance() {
		if (_instance == null) {
			_instance = new XilinxDaisyChainDetectionMerger();
		}
		return _instance;
	}

	@Override
	public List<PortConnection> mergeOutputs(List<List<PortConnection>> outputs, EdifCell topCell, NetManager netManager) {
		if (outputs.size() < 1)
			return null;
		// "uncollate" the outputs into a set of Lists, one for each detection rail
		List<List<PortConnection>> uncollatedOutputs = new ArrayList<List<PortConnection>>();
		int connectionSize = -1;
		for (List<PortConnection> output : outputs) {
			int size = output.size();
			if (connectionSize == -1)
				connectionSize = size;
			else {
				if (connectionSize != size)
					throw new EdifRuntimeException("Error: trying to merge detection outputs of different sizes");
			}
			int i = 0;
			for (PortConnection conn : output) {
				if (uncollatedOutputs.size() < (i + 1))
					uncollatedOutputs.add(new ArrayList<PortConnection>());
				List<PortConnection> subUncollatedList = uncollatedOutputs.get(i);
				subUncollatedList.add(conn);				
				i++;
			}
		}
		
		// Merge the outputs of each "rail" into a single output
		List<PortConnection> railOutputs = new ArrayList<PortConnection>();
		for (int i = 0; i < connectionSize; i++) {
			List<PortConnection> subUncollatedList = uncollatedOutputs.get(i);
			railOutputs.add(mergeConnections(subUncollatedList, topCell, netManager));
		}
		return railOutputs;
	}
	
	protected PortConnection mergeConnections(List<PortConnection> connections, EdifCell topCell, NetManager netManager) {
		
		Stack<PortConnection> connectionStack = new Stack<PortConnection>();
		connectionStack.addAll(connections);
		while (connectionStack.size() > 1) {
			int i = 0;
			List<PortConnection> mergedConnections = new ArrayList<PortConnection>();
			while (connectionStack.size() > 1) {
				EdifNameable mergerName = topCell.getUniqueInstanceNameable(NamedObject.createValidEdifNameable(MERGER_NAME + "_" + i));
				EdifCellInstance mergerInstance = addMergerInstance(mergerName, topCell);
				PortConnection I0 = new SinglePortConnection(mergerInstance.getCellType().getPort("I0").getSingleBitPort(0), mergerInstance);
				PortConnection I1 = new SinglePortConnection(mergerInstance.getCellType().getPort("I1").getSingleBitPort(0), mergerInstance);
				PortConnection O = new SinglePortConnection(mergerInstance.getCellType().getPort("O").getSingleBitPort(0), mergerInstance);
				netManager.wirePortConnections(connectionStack.pop(), I0);
				netManager.wirePortConnections(connectionStack.pop(), I1);
				mergedConnections.add(O);
				i++;
			}
			connectionStack.addAll(mergedConnections);
		}
		return connectionStack.pop();
	}

	protected EdifCellInstance addMergerInstance(EdifNameable mergerName, EdifCell topCell) {
        // 1. Obtain a reference to the EdifCell voter object
        EdifCell merger = getORMergerCell(topCell);

        // 2. Create a new instance
        EdifCellInstance mergerCellInstance = null;
        
        mergerCellInstance = new EdifCellInstance(mergerName, topCell, merger);
        
        try {
            topCell.addSubCell(mergerCellInstance);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }

        return mergerCellInstance;
    }
	
	/**
	 * Creates a OR2 implementation of a merger cell if it has not been defined
	 * yet. Adds it to the library to which the parent cell belongs. If the
	 * merger cell has already been built, a reference to that cell is simply
	 * returned.
	 * 
	 * @param parent The parent cell from which all libraries will be extracted.
	 * @return A merger cell implemented as a OR2.
	 */
	protected EdifCell getORMergerCell(EdifCell parent) {

		/*
		 * If the merger cell has already been defined, return the associated
		 * Edif Cell.
		 */
		if (_mergerCell != null)
			return _mergerCell;

		//String voterCellName = "LUT" + ports; 
		String voterCellName = "OR2";

		// Step #1 - Get Xilinx primitive
		EdifLibrary xilinxLibrary = XilinxLibrary.library;

		EdifCell compare = xilinxLibrary.getCell(voterCellName);

		// Step #2 - Search for the Xilinx primitive in library manager
		Collection<EdifCell> matchingVoterCells = parent.getLibrary().getLibraryManager().getCells(voterCellName);

		// Iterate over all cells to see if the primitive exists
		if (matchingVoterCells != null) {
			// Iterate over the matching cells and see if it exists
			for (EdifCell ce : matchingVoterCells) {
				if (ce.equalsInterface(compare)) {
					// The AND exists - tag it as the merger cell
					_mergerCell = ce;
					return _mergerCell;
				}
			}
		}

		// Step #3 - The primitive does not exist in our library. Add it.
		EdifLibrary lib = parent.getLibrary().getLibraryManager().getFirstPrimitiveLibrary();
		if (lib == null)
			lib = parent.getLibrary();
		try {
			lib.addCell(compare);
		} catch (EdifNameConflictException e) {
			e.toRuntime();
		}
		_mergerCell = compare;

		return _mergerCell;
	}

	/**
	 * This method ensures that during deserialization, the _instance variable will be referred
	 * to and possibly modified so that extra instances (more than necessary) don't get created.
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		XilinxDaisyChainDetectionMerger instance = _instance;
		if (instance == null) {
			instance = getInstance();
		}
		return instance;
	}
	
	protected EdifCell _mergerCell;
	protected static XilinxDaisyChainDetectionMerger _instance;
	protected static String MERGER_NAME = "merger";
}
