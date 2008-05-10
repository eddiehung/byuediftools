/*
 * TODO: Insert class description here.
 * 
 * Copyright (c) 2008 Brigham Young University
 * 
 * This file is part of the BYU EDIF Tools.
 * 
 * BYU EDIF Tools is free software: you may redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * 
 * BYU EDIF Tools is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * A copy of the GNU General Public License is included with the BYU EDIF Tools.
 * It can be found at /edu/byu/edif/doc/gpl2.txt. You may also get a copy of the
 * license at <http://www.gnu.org/licenses/>.
 * 
 */
package edu.byu.ece.edif.tools.sterilize.halflatch;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.byu.ece.edif.arch.xilinx.XilinxMergeParser;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifPrintWriter;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.tools.flatten.NewFlattenedEdifCell;
import edu.byu.ece.edif.util.merge.DuplicateMergingPolicy;
import edu.byu.ece.edif.util.merge.EdifMergeParser;
import edu.byu.ece.edif.util.merge.EdifMergingPolicy;

/**
 * @since Created on Oct 27, 2005
 */
public abstract class EdifHalfLatchRemover {

	public EdifHalfLatchRemover(HalfLatchArchitecture hlArchitecture, int safeConstantPolarity,
			boolean usePortForConstant) {
		this(hlArchitecture, safeConstantPolarity, usePortForConstant, null);
	}

	/**
     * @param hlArchitecture Object which holds all of the architecture specific
     * information
     * @param safeConstantPolarity Polarity of the main safe-constant signal
     * running around in the design
     * @param usePortForConstant Boolean which determines whether or not the
     * safe-constant network is driven through a port or internal constant
     * generator cells
     * @param safeConstantPortName The name of the port to use for the safe
     * constant.
     */
	public EdifHalfLatchRemover(HalfLatchArchitecture hlArchitecture, int safeConstantPolarity,
			boolean usePortForConstant, String safeConstantPortName) {
		_hlArchitecture = hlArchitecture;
		_safeConstantPolarity = safeConstantPolarity;
		_usePortForConstant = usePortForConstant;
		_topLevelSafePortName = safeConstantPortName;
	}

	public HalfLatchFlattenedEdifCell removeHalfLatches(NewFlattenedEdifCell flatCell) {
		// Create the copy of the flatCell with half-latches removed
		HalfLatchFlattenedEdifCell hlFlatCell = null;
		try {
			// Create new HLFlattenedCell in a new Library so the name doesn't
			// have to change?
			hlFlatCell = new HalfLatchFlattenedEdifCell(flatCell, _hlArchitecture, _safeConstantPolarity);
		} catch (InvalidEdifNameException e1) {
			e1.toRuntime();
		} catch (EdifNameConflictException e2) {
			e2.toRuntime();
		}

		// Add top-level safe constant port, if desired
		// If the user does not want to use a port for the 'safe constant'...
		// - create a safe constant generating element in the top cell
		// Otherwise keep the port on the top-level cell for the 'safe-constant'
		// - buffer the top-level 'safe constant' port
		if (_usePortForConstant == false) {
			hlFlatCell.addSafeConstantGeneratorCell();
		} else {
			if (_topLevelSafePortName != null)
				hlFlatCell.renameSafeConstantPort(_topLevelSafePortName);
			hlFlatCell.bufferSafeConstantPort();
		}

		return hlFlatCell;
	}

	/**
     * General algorithm as developed by Keith Morgan, Nathan Rollins, Brian
     * Pratt & Dr. Wirthlin
     * <p>
     * Note: Multiple passes are for simplicity in the algorithms and to reduce
     * processing time.
     * <h2>Step 1 / Pass 1</h2>
     * <p>
     * For each cell in design libraries
     * <ol>
     * <li>Create a new HalfLatchEdifCell version of non-primitive cells (This
     * class simply extends EdifCell and is a convenience class to easily create
     * and get the 'safe constant' ports, nets, inverters, etc...)
     * </ol>
     * End For
     * 
     * <h2>Step 2 / Pass 2</h2>
     * <p>
     * For each cell in design libraries
     * <ol>
     * <li>Remove the half-latches and constants in that cell. (This is the
     * abstract method for this class and is currently implemented in a
     * Sequential or Topological fashion. Each has different options.)
     * </ol>
     * end For
     * <h2>Step 3</h2>
     * <p>
     * <ol>
     * <li>If the user does not want to use a port for the 'safe constant'...
     * <ul>
     * <li> create a safe constant generating element in the top cell (like an
     * all-0 LUT)
     * </ul>
     * <li>Otherwise keep the port on the top-level cell for the
     * 'safe-constant'
     * <ul>
     * <li> buffer the top-level 'safe constant' port
     * </ul>
     * </ol>
     * <h2>Step 4</h2>
     * <p>
     * Make sure the edif that will be written will put the cells in correct
     * declaration order.
     * 
     * @param environment Container of objects relating to the design which is
     * being modified
     * @return A new environment (Edif design) which had half-latches removed
     */
	public EdifEnvironment removeHalfLatches(EdifEnvironment environment) {

		// Create a new EdifEnvironment
		EdifEnvironment new_env = null;
		try {
			new_env = new EdifEnvironment(environment.getName());
		} catch (InvalidEdifNameException e) {
			e.toRuntime();
		}

		// Step 1 / Pass 1
		// ------
		// For each cell in design libraries
		// Create a new HalfLatchEdifCell version of non-primitive cells
		// (This class simply extends EdifCell and is a convenience
		// class to easily create and get the 'safe constant' ports,
		// nets, inverters, etc...)
		// End For
		// Set the top cell instance of the new EdifEnvironment
		EdifCell top_hl_cell = convertCelltoHalfLatchCellDeep(environment.getTopCell(), null, new_env
				.getLibraryManager(), _hlArchitecture, _safeConstantPolarity);
		HalfLatchEdifCell hl_top_cell = (HalfLatchEdifCell) top_hl_cell;
		setTopDesignAndInstance(new_env, hl_top_cell, environment);

		// Step 2 / Pass 2
		// ------
		// For each cell in design libraries
		// Remove the half-latches and constants in that cell.
		// (This is the abstract method for this class and is
		// currently implemented in a Sequential or Topological
		// fashion. Each has different options.)
		// end For
		fixPotentialHalfLatchesInEachCell(new_env);

		// Step 3
		// ------
		// If the user does not want to use a port for the 'safe constant'...
		// - create a safe constant generating element in the top cell
		// Otherwise keep the port on the top-level cell for the 'safe-constant'
		// - buffer the top-level 'safe constant' port
		if (_usePortForConstant == false) {
			hl_top_cell.addSafeConstantGeneratorCell();
		} else {
			if (_topLevelSafePortName != null)
				hl_top_cell.renameSafeConstantPort(_topLevelSafePortName);
			hl_top_cell.bufferSafeConstantPort();
		}

		// Step 4
		// ------
		// Make sure the edif that will be written will put the cells
		// in correct declaration order
		// top_cell.getLibrary().getLibraryManager().validateOrder();

		return new_env;

	}

	/**
     * Creates a new EdifDesign in the new environment (copying the properties
     * of the old EdifDesign) and setting the top cell to be the top instance.
     * 
     * @param new_env The new EdifEnvironment created
     * @param hl_top_cell The top-level EdifCell
     * @param environment The old EdifEnvironment to copy properties from
     */
	protected void setTopDesignAndInstance(EdifEnvironment new_env, HalfLatchEdifCell hl_top_cell,
			EdifEnvironment environment) {
		// Set top instance for new EdifEnvironment
		EdifCellInstance top_instance = null;
		EdifDesign new_design = null;
		try {
			top_instance = new EdifCellInstance(hl_top_cell.getName(), null, hl_top_cell);
			new_design = new EdifDesign(hl_top_cell.getEdifNameable());
		} catch (InvalidEdifNameException e1) {
			e1.toRuntime();
		}
		new_design.setTopCellInstance(top_instance);
		// copy design properties
		EdifDesign old_design = environment.getTopDesign();
		if (old_design.getPropertyList() != null) {
			for (Iterator it = old_design.getPropertyList().values().iterator(); it.hasNext();) {
				Property p = (Property) it.next();
				new_design.addProperty((Property) p.clone());
			}
		}
		new_env.setTopDesign(new_design);
	}

	/**
     * Recursively copy an EdifCell from one EdifLibraryManager to another.
     * During the copy, cells (instanced by the cell to be copied) may be added
     * to other libraries within the EdifLibraryManager.
     * 
     * @param cellToConvert the EdifCell object to copy
     * @param targetLib the EdifLibrary to copy the cell into. If no EdifLibrary
     * is specified (null), the EdifCell will be placed in an EdifLibrary with
     * the same name (or a new unique name if there is a clash) as it's old
     * EdifLibrary
     * @param elm the EdifLibraryManager to copy the cell into
     * @return a reference to the new copy of the EdifCell object
     */
	public static EdifCell convertCelltoHalfLatchCellDeep(EdifCell cellToConvert, EdifLibrary targetLib,
			EdifLibraryManager elm, HalfLatchArchitecture hlArchitecture, int safeConstantPolarity) {
		if (targetLib != null && !elm.containsLibrary(targetLib))
			throw new EdifRuntimeException("Bad library");

		// Use the DuplicateMergingPolicy to create libraries with identical
		// names in the new Environment
		EdifMergingPolicy mergingPolicy = new DuplicateMergingPolicy();

		if (targetLib == null) // determine where to put the cell
			targetLib = mergingPolicy.findLibraryForCell(cellToConvert, elm);

		// check to see if the target library already contains a cell with the
		// same name and interface. if so, return it.
		if (targetLib.containsCellByName(cellToConvert.getName())) {
			EdifCell possibleMatch = targetLib.getCell(cellToConvert.getName());
			// Interface probably won't match for half-latch cells!
			// if (possibleMatch.equalsInterface(cellToCopy))
			return possibleMatch;
		}

		// Check for primitive or black box (empty cells)
		if (cellToConvert.isPrimitive() || cellToConvert.isBlackBox()) {
			// Do a simple copy
			try {
				return new EdifCell(targetLib, cellToConvert);
			} catch (InvalidEdifNameException e) {
				// This should never happen
				e.toRuntime();
			} catch (EdifNameConflictException e) {
				// This should never happen
				e.toRuntime();
			}
		}

		EdifCell hlCell = null;
		try {
			hlCell = new HalfLatchEdifCell(targetLib, cellToConvert.getEdifNameable(), hlArchitecture,
					safeConstantPolarity);
		} catch (EdifNameConflictException e) {
			e.toRuntime();
		}

		// copy properties
		if (cellToConvert.getPropertyList() != null) {
			for (Iterator it = cellToConvert.getPropertyList().values().iterator(); it.hasNext();) {
				Property p = (Property) it.next();
				hlCell.addProperty((Property) p.clone());
			}
		}

		// copy cell instances
		Map<EdifPort, EdifPort> oldToNewPorts = new LinkedHashMap<EdifPort, EdifPort>();
		Map<EdifCellInstance, EdifCellInstance> oldToNewInstances = new LinkedHashMap<EdifCellInstance, EdifCellInstance>();
		Map<EdifCell, EdifCell> oldToNewCells = new LinkedHashMap<EdifCell, EdifCell>();

		if (cellToConvert.getInstancedCellTypes() != null) // there are
			// subcells to
			// copy
			for (Iterator instanced = cellToConvert.getInstancedCellTypes().iterator(); instanced.hasNext();) {
				EdifCell cell = (EdifCell) instanced.next();

				if (oldToNewCells.containsKey(cell))
					continue;

				// copy the cell over if it is not already here
				EdifCell newCellRef = convertCelltoHalfLatchCellDeep(cell, null, elm, hlArchitecture,
						safeConstantPolarity);
				oldToNewCells.put(cell, newCellRef);
			}

		for (Iterator<EdifCellInstance> instanceIterator = (Iterator<EdifCellInstance>) cellToConvert
				.cellInstanceIterator(); instanceIterator.hasNext();) {
			EdifCellInstance oldEci = instanceIterator.next();
			EdifCell newCellRef = oldToNewCells.get(oldEci.getCellType());

			for (EdifPort oldPort : oldEci.getCellType().getPortList()) {
				EdifPort newPort = newCellRef.getPort(oldPort.getName());
				if (!oldToNewPorts.containsKey(oldPort)) {
					oldToNewPorts.put(oldPort, newPort);
				}
			}

			// Create half-latch instance in the new HalfLatchEdifCell
			EdifCellInstance newInstance = null;
			// Determine if the instanced Cell is a primitive or not
			if (oldEci.getCellType().isPrimitive() && hlArchitecture.cellRequiresReplacement(oldEci)) {
				// If the instance is a "sensitive" primitive, add SENSITIVE
				// suffix to the name. This will later be removed when
				// the instance is replaced.
				try {
					newInstance = new EdifCellInstance(oldEci.getName() + HalfLatchEdifCell.SENSITIVE_SUFFIX, hlCell,
							newCellRef);
				} catch (InvalidEdifNameException e) {
					// This shouldn't happen, but if it does, just use the
					// original name.
					if (_debug)
						System.out.println("WARNING: Sensitive primitive " + oldEci + "could not be renamed to "
								+ oldEci.getName() + HalfLatchEdifCell.SENSITIVE_SUFFIX);
					newInstance = new EdifCellInstance(oldEci.getEdifNameable(), hlCell, newCellRef);
				}
			} else {
				// Non-primitive or non-sensitive primitive. Use the same
				// name as the original instance.
				newInstance = new EdifCellInstance(oldEci.getEdifNameable(), hlCell, newCellRef);
			}

			// copy instance properties
			if (oldEci.getPropertyList() != null) {
				for (Iterator it = oldEci.getPropertyList().values().iterator(); it.hasNext();) {
					Property p = (Property) it.next();
					newInstance.addProperty((Property) p.clone());
				}
			}
			oldToNewInstances.put(oldEci, newInstance);
			try {
				hlCell.addSubCell(newInstance);
			} catch (EdifNameConflictException e) {
				e.toRuntime();
			}
		}

		// copy cell interface
		for (EdifPort oldPort : cellToConvert.getPortList()) {
			EdifPort newPort = null;
			try {
				newPort = hlCell.addPort(oldPort.getEdifNameable(), oldPort.getWidth(), oldPort.getDirection());
			} catch (EdifNameConflictException e) {
				e.toRuntime();
			}
			oldToNewPorts.put(oldPort, newPort);
			// copy port properties
			if (oldPort.getPropertyList() != null) {
				for (Iterator it = oldPort.getPropertyList().values().iterator(); it.hasNext();) {
					Property p = (Property) it.next();
					newPort.addProperty((Property) p.clone());
				}
			}
		}

		// copy nets
		for (Iterator<EdifNet> netIterator = (Iterator<EdifNet>) cellToConvert.netListIterator(); netIterator.hasNext();) {
			EdifNet oldNet = netIterator.next();
			EdifNet newNet = new EdifNet(oldNet.getEdifNameable(), hlCell);
			// iterate portRefs
			for (Iterator<EdifPortRef> portRefIterator = (Iterator<EdifPortRef>) oldNet.getPortRefIterator(); portRefIterator
					.hasNext();) {
				EdifPortRef oldRef = portRefIterator.next();
				EdifSingleBitPort oldSbp = oldRef.getSingleBitPort();
				EdifSingleBitPort newSbp = oldToNewPorts.get(oldSbp.getParent()).getSingleBitPort(oldSbp.bitPosition());

				EdifCellInstance newEci = null;
				if (oldRef.getCellInstance() != null)
					newEci = oldToNewInstances.get(oldRef.getCellInstance());
				EdifPortRef newEpr = new EdifPortRef(newNet, newSbp, newEci);
				newNet.addPortConnection(newEpr);
			}

			// copy net properties
			if (oldNet.getPropertyList() != null) {
				for (Iterator it = oldNet.getPropertyList().values().iterator(); it.hasNext();) {
					Property p = (Property) it.next();
					newNet.addProperty((Property) p.clone());
				}
			}
			try {
				hlCell.addNet(newNet);
			} catch (EdifNameConflictException e) {
				e.toRuntime();
			}
		}
		return hlCell;
	}

	protected abstract void fixPotentialHalfLatchesInEachCell(EdifEnvironment environment);

	public static final int DEFAULT_SAFE_CONSTANT_POLARITY = 0;

	public static final boolean DEFAULT_USE_PORT_FOR_CONSTANT_BOOL = false;

	protected static final boolean _debug = false;

	protected HalfLatchArchitecture _hlArchitecture = null;

	protected int _safeConstantPolarity = DEFAULT_SAFE_CONSTANT_POLARITY;

	protected boolean _usePortForConstant = DEFAULT_USE_PORT_FOR_CONSTANT_BOOL;

	protected String _topLevelSafePortName = null;

	protected int _constantSinkThreshold = 0;

	/**
     * @param args
     */
	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println(getUsage());
			return;
		}

		// Determine the technology to use
		// Default technology is Xilinx
		Set technologies = EdifMergeParser.parseArguments(args, "-t");
		String technology = "Xilinx";
		if (technologies != null && technologies.size() >= 1)
			technology = (String) technologies.iterator().next();
		HalfLatchArchitecture hlArchitecture = null;
		// we currently only support xilinx
		if (technology.compareToIgnoreCase("Xilinx") != 0)
			throw new EdifRuntimeException("Invalid technology " + technology + " specified.");
		System.out.println("Using technology " + technology);

		// Determine the polarity of the internal safe constant
		// The default value is 0.
		int safeConstantPolarity = EdifHalfLatchRemover.DEFAULT_SAFE_CONSTANT_POLARITY;
		Set constantPolarities = EdifMergeParser.parseArguments(args, "-constantpolarity");
		if (constantPolarities != null && constantPolarities.size() >= 1)
			safeConstantPolarity = Integer.parseInt((String) constantPolarities.iterator().next());
		System.out.println("Safe constant will have polarity " + safeConstantPolarity);

		// Determine if a port should be used to drive the safe constant or
		// an internal source.
		boolean usePortForConstant = EdifMergeParser.containsArgument(args, "-useportforconstant");
		if (usePortForConstant == true)
			System.out.println("Using top-level port for safe constant source");
		else
			System.out.println("Using internal cell(s) for safe constant source");

		// Decide what type of half-latch removal to do... sequential or
		// recursive
		int constantSinkThreshold = -1;
		Set constantSinkThresholds = EdifMergeParser.parseArguments(args, "-constantsinkthreshold");
		if (constantSinkThresholds != null && constantSinkThresholds.size() > 0) {
			constantSinkThreshold = Integer.parseInt((String) constantSinkThresholds.iterator().next());
			System.out.println("Setting constant sink threshold to " + constantSinkThreshold + ".");
		}
		if (constantSinkThreshold != -1 && usePortForConstant == true)
			throw new EdifRuntimeException(
					"Conflicting arguments.  Cannot use top-level port to drive safe constant network AND have internal constant generator cells.  Conflicting argument flags are -useportforconstant and -constantsinkthreshold.");

		// Load EDIF
		EdifCell top_cell = XilinxMergeParser.parseAndMergeXilinx(args);
		EdifEnvironment environment = top_cell.getLibrary().getLibraryManager().getEdifEnvironment();

		// Create HalfLatchArchitecture
		hlArchitecture = new XilinxHalfLatchArchitecture(top_cell);

		// Now do the actual half latch removal
		System.out.println("Removing half-latches...");
		EdifHalfLatchRemover edifHalfLatchRemover = null;
		/*
         * If the user wants to drive the safe constant network through a port,
         * or if the user only wants *one* internal safe constant generator
         * source, then use more simple Sequential half-latch removal algorithm.
         * Otherwise (the user wants to have multiple internal constant
         * generator sources) use the Topological half-latch removal algorithm.
         */
		if (constantSinkThreshold == -1 || usePortForConstant == true) {
			System.out.println("Using Sequential algorithm...");
			edifHalfLatchRemover = new SequentialEdifHalfLatchRemover(hlArchitecture, safeConstantPolarity,
					usePortForConstant);
		} else {
			System.out.println("Using Topological algorithm...");
			edifHalfLatchRemover = new TopologicalEdifHalfLatchRemover(hlArchitecture, safeConstantPolarity,
					usePortForConstant, constantSinkThreshold);
		}
		EdifEnvironment halflatchSafeEdifEnvironment = edifHalfLatchRemover.removeHalfLatches(environment);

		/*
         * If the user specified a non-null filename for the output edif then
         * write the modified edif to file. Default filename is hl.edf
         */
		Set outputFilenames = EdifMergeParser.parseArguments(args, "-o");
		String outputEdifFilename = null;
		if (outputFilenames == null || outputFilenames.size() < 1)
			outputEdifFilename = new String("hl.edf");
		else
			outputEdifFilename = (String) outputFilenames.iterator().next();
		System.out.println("Writing half-latch safe edif to file " + outputEdifFilename);
		try {
			EdifPrintWriter epw = new EdifPrintWriter(new FileOutputStream(outputEdifFilename));
			halflatchSafeEdifEnvironment.toEdif(epw);
			// top_cell.getLibrary().getLibraryManager().getEdifEnvironment().toEdif(epw);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			System.exit(1);
		}

		return;
	}

	private static String getUsage() {
		StringBuffer sb = new StringBuffer();
		sb.append("Usage: EdifHalfLatchRemover <top edif filename> [-o <output edif filename>]\n");
		sb.append("\t[-L <directory search path>]* [-f <aux edif files>]*\n");
		sb.append("\t[-t <technology>] (Default is Xilinx)\n");
		sb.append("\t[-constantsinkthreshold <num>] (Directs the tool to insert a safe\n");
		sb.append("\tconstant generator cell at each level of the hierarchy which drives\n");
		sb.append("\tmore than <num> sinks.  By default, there is no threshold and only one\n");
		sb.append("\tconstant generator cell is placed at the top level of the hierarchy.)\n");
		sb.append("\t[-useportforconstant] (Lets user drive safe constant with a port.\n");
		sb.append("\tThe port will be named safeConstantPort_zero or safeConstantPort_one\n");
		sb.append("\tdepending on the polarity of the safe constant; see below.\n");
		sb.append("\tDefault is to not use a port, but an internally generated constant.)\n");
		sb.append("\t[-constantpolarity <polarity>] (Sets polarity of safe constant net,\n");
		sb.append("\t0=zero, !0=one; Default polarity is 0.)\n");
		return sb.toString();
	}

}
