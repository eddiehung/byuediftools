/*
 * Represents a flattened EdifCell object
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
package edu.byu.ece.edif.tools.flatten;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifCellInterface;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifPrintWriter;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.core.RenamedObject;
import edu.byu.ece.edif.util.merge.EdifMergeParser;

//////////////////////////////////////////////////////////////////////////
//// FlattenedEdifCell
/**
 * Represents a flattened EdifCell object; that is, an EdifCell with no
 * subcells, composed entirely of primitives. This class will create a new
 * FlattenedEdifCell object during construction that represents a flattened
 * version of the original EdifCell passed into the constructor. This class
 * maintains associations between instances in the flattened cell to the
 * corresponding instances in the original cell by using
 * FlattenedEdifCellInstances which contain references to HierarchicalInstance
 * objects which contain information about the hierarchy of the original cell. A
 * map is also maintained from original EdifNets to their corresponding new
 * EdifNets (more than one original net will map to the same new net).
 */
public class NewFlattenedEdifCell extends EdifCell {

    /**
     * Construct a new FlattenedEdifCell based on the given EdifCell and in the
     * same library, with a default hierarchy naming scheme.
     * 
     * @throws InvalidEdifNameException
     * @throws EdifNameConflictException
     */
    public NewFlattenedEdifCell(EdifCell cell) throws EdifNameConflictException, InvalidEdifNameException {
        this(cell.getLibrary(), cell, BasicHierarchyNaming.DEFAULT_BACKSLASH_NAMING);
    }

    /**
     * Construct a new FlattenedEdifCell based on the given EdifCell and in the
     * same library, with a default hierarchy naming scheme, but it is already
     * flattened, so don't flatten it again.
     * 
     * @throws InvalidEdifNameException
     * @throws EdifNameConflictException
     */
    public NewFlattenedEdifCell(EdifCell cell, String suffix) throws EdifNameConflictException,
            InvalidEdifNameException {
        this(cell.getLibrary(), cell, suffix, BasicHierarchyNaming.DEFAULT_BACKSLASH_NAMING);

    }

    /**
     * Construct a new FlattenedEdifCell based on the given EdifCell and in the
     * same library, with the given hierarchy naming scheme
     * 
     * @param cell the EdifCell to flatten
     * @param naming the HierarchyNaming scheme to use
     * @throws EdifNameConflictException
     * @throws InvalidEdifNameException
     */
    public NewFlattenedEdifCell(EdifCell cell, HierarchyNaming naming) throws EdifNameConflictException,
            InvalidEdifNameException {
        this(cell.getLibrary(), cell, naming);
    }

    /**
     * Construct a new FlattenedEdifCell based on the given EdifCell and in the
     * given library, with the given hierarchy naming scheme
     * 
     * @param lib the library to construct the FlattenedEdifCell in
     * @param cell the EdifCell to flatten
     * @param naming the HierarchyNaming scheme to use
     * @throws EdifNameConflictException
     * @throws InvalidEdifNameException
     */
    public NewFlattenedEdifCell(EdifLibrary lib, EdifCell cell, HierarchyNaming naming)
            throws EdifNameConflictException, InvalidEdifNameException {
        super(lib, lib.getUniqueEdifCellNameable(new NamedObject(cell.getName() + "_flat")).getName(), cell
                .getInterface());
        _originalCell = cell;
        _naming = naming;
        flatten();
    }

    /**
     * Construct a new FlattenedEdifCell based on the given EdifCell and in the
     * given library, with the given hierarchy naming scheme
     * 
     * @param lib the library to construct the FlattenedEdifCell in
     * @param cell the EdifCell to flatten
     * @param naming the HierarchyNaming scheme to use
     * @throws EdifNameConflictException
     * @throws InvalidEdifNameException
     */
    public NewFlattenedEdifCell(EdifLibrary lib, EdifCell cell, String suffix, HierarchyNaming naming)
            throws EdifNameConflictException, InvalidEdifNameException {
        super(lib, lib.getUniqueEdifCellNameable(new NamedObject(cell.getName() + suffix)).getName(), cell
                .getInterface());
        _originalCell = cell;
        _naming = naming;
        flatten();
    }

    /**
     * Construct a new, empty FlattenedEdifCell with the given EdifCell as the
     * "original cell" but without any properties, instances, etc. copied and in
     * the given library, and without any of the internal. This constructor
     * should only be used in conjunction with a subclass which fills in the
     * missing information.
     * 
     * @param lib the library to construct the FlattenedEdifCell in
     * @param cellName the name of the new EdifCell to create
     * @param cellInterface The Interface of the cell to be created
     * @throws EdifNameConflictException
     * @throws InvalidEdifNameException
     */
    protected NewFlattenedEdifCell(EdifLibrary lib, String cellName, EdifCellInterface cellInterface)
            throws EdifNameConflictException, InvalidEdifNameException {
        super(lib, lib.getUniqueEdifCellNameable(new NamedObject(cellName)).getName(), cellInterface);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * @return the Set of all HierarchicalInstance nodes in the flat cell
     */
    public Set<HierarchicalInstance> getAllHierarchicalInstances() {
        return _nodesToFlatInstances.keySet();
    }

    /**
     * Given an array of command-line argument strings, return a
     * FlattenedEdifCell base on the top cell of the specified file.
     * 
     * @return a FlattenedEdifCell based on the top cell of the specified file
     */
    public static NewFlattenedEdifCell getFlatCell(String[] args) {
        // The value of the string printed out when there is a problem
        // with the argument string.
        String usageString = "Usage: java FlattenedEdifCell <top file> [-L <search directory>]* [-f <filename>]* [-o <outputfilename>]";

        if (args.length < 1) {
            System.out.println(usageString);
            System.exit(1);
        }

        System.out.println("Parsing File " + args[0] + ". . .");
        EdifEnvironment top = EdifMergeParser.getMergedEdifEnvironment(args[0], args);

        System.out.println("Flattening . . .");
        EdifCell cell = top.getTopCell();
        NewFlattenedEdifCell flat_cell = null;
        try {
            flat_cell = new NewFlattenedEdifCell(cell);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }

        return flat_cell;
    }

    /**
     * @return the FlattenedEdifCellInstance which corresponds to the given
     * HierarchicalInstance
     */
    public FlattenedEdifCellInstance getFlatInstance(HierarchicalInstance hierarchyNode) {
        return _nodesToFlatInstances.get(hierarchyNode);
    }

    /**
     * Given a string representing the full hierarchical name of an original,
     * unflattened instance, get the FlattenedEdifCellInstance that corresponds
     * to it.
     * 
     * @return the FlattenedEdifCellInstance which corresponds to the given full
     * hierarchical name
     */
    public FlattenedEdifCellInstance getFlatInstance(String hierarchicalName) {
        HierarchicalInstance hierarchyNode = getHierarchicalInstance(hierarchicalName);
        return getFlatInstance(hierarchyNode);
    }

    /**
     * Given a string representing the full hierarchical name of an original,
     * unflattened instance, get the HierarchicalInstance node that corresponds
     * to it.
     * 
     * @param hierarchicalName the full hierarchical name of the original
     * instance
     * @return the HierarchicalInstance node that corresponds to the given name
     */
    public HierarchicalInstance getHierarchicalInstance(String hierarchicalName) {
        return _naming.getHierarchicalInstance(_topInstanceNode, hierarchicalName);
    }

    /**
     * @return the HierarchyNaming scheme used by this FlattenedEdifCell
     */
    public HierarchyNaming getHierarchyNaming() {
        return _naming;
    }

    /**
     * Given a string representing the full hierarchical name of an instance
     * from the original EdifCell, get a Collection of
     * FlattenedEdifCellInstances that are leaf instances that would be "within"
     * the given original instance.
     * 
     * @param hierarchicalName a String representing the full hierarchical name
     * of the original instance
     * @return a Collection of FlattenedEdifCellInstances that correspond to
     * original instances "within" the given instance
     */
    public Collection<FlattenedEdifCellInstance> getInstancesWithin(String hierarchicalName) {

        HierarchicalInstance instanceNode = getHierarchicalInstance(hierarchicalName);
        return getInstancesWithinNode(instanceNode);
    }

    /**
     * Given a Collection of Strings representing cell types, get a Collection
     * of all instances that would have fallen within any instance of the given
     * types.
     * 
     * @param cellTypes a Collection of String representing cell types in the
     * original cell
     * @return a Collection of FlattenedEdifCellInstances that would have fallen
     * within any instance of the given types.
     */
    public Collection<FlattenedEdifCellInstance> getInstancesWithinCellTypes(Collection<String> cellTypes) {
        LinkedList<HierarchicalInstance> bfsTraversalList = new LinkedList<HierarchicalInstance>();
        Collection<FlattenedEdifCellInstance> result = new LinkedHashSet<FlattenedEdifCellInstance>();

        bfsTraversalList.add(_topInstanceNode);
        while (!bfsTraversalList.isEmpty()) {
            HierarchicalInstance currentNode = bfsTraversalList.poll();
            for (String cellType : cellTypes) {
                if (currentNode.getCellType().getName().equalsIgnoreCase(cellType)) {
                    result.addAll(getInstancesWithinNode(currentNode));
                }
                bfsTraversalList.addAll(currentNode.getChildren());
            }
        }
        return result;
    }

    /**
     * @return a reference to the original, unflattened EdifCell
     */
    public EdifCell getOriginalCell() {
        return _originalCell;
    }

    /**
     * @return a reference to the top node of the HierarchicalInstance node
     * hierarchy
     */
    public HierarchicalInstance getTopInstanceNode() {
        return _topInstanceNode;
    }

    /**
     * @param args command-line arguments -- first specify the input file; then,
     * use -o for output file, -L to include a search directory, and -f to
     * explicity include an edif file (for black box merging).
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        NewFlattenedEdifCell flat_cell = getFlatCell(args);
        EdifCell cell = flat_cell.getOriginalCell();
        EdifEnvironment top = cell.getLibrary().getLibraryManager().getEdifEnvironment();
        top.getLibraryManager().pruneNonReferencedCells(flat_cell);
        EdifCellInstance flat_instance = null;
        EdifDesign new_design = null;
        try {
            flat_instance = new EdifCellInstance("flat_" + cell.getName(), null, flat_cell);
            new_design = new EdifDesign("ROOT");
        } catch (InvalidEdifNameException e1) {
            e1.toRuntime();
        }

        new_design.setTopCellInstance(flat_instance);
        top.setTopDesign(new_design);
        cell.getLibrary().deleteCell(cell, true);

        System.out.println("Writing new file . . .");
        Set outputFile = EdifMergeParser.parseArguments(args, "-o");

        String OutputFileName = null;
        if (outputFile == null || outputFile.size() < 1)
            OutputFileName = new String("flat.edf");
        else
            OutputFileName = (String) outputFile.iterator().next();

        try {
            EdifPrintWriter epw = new EdifPrintWriter(new FileOutputStream(OutputFileName));
            top.toEdif(epw);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
            System.exit(1);
        }

        System.out.println("Done");
        System.out.println("Instances: orig=" + edu.byu.ece.edif.core.EdifUtils.countRecursivePrimitives(cell) + " flat="
                + edu.byu.ece.edif.core.EdifUtils.countRecursivePrimitives(flat_cell));
        System.out.println("Port Refs: orig=" + edu.byu.ece.edif.core.EdifUtils.countPortRefs(cell, true) + " flat="
                + edu.byu.ece.edif.core.EdifUtils.countPortRefs(flat_cell, true));

        System.out.println();
    }

    /**
     * Execute the flattening process. This method fills in this cell with the
     * flattened contents of the original cell.
     */
    protected void flatten() {
        // Check to make sure that it is the top cell that is being flattened,
        // otherwise, the top HierarchicalInstance won't match up right to the
        // instance of the cell being flattened.
        EdifCellInstance topInstance = _originalCell.getLibrary().getLibraryManager().getEdifEnvironment()
                .getTopCellInstance();
        if (topInstance.getCellType() != _originalCell) {
            throw new EdifRuntimeException("Error: attempting to flatten a cell that is not the top cell");
        }

        // This manages the connectivity of the nets
        NetConnections connections = new NetConnections();

        // This list is for breadth first search traversal of the instance
        // hierarchy
        LinkedList<InstanceNode> bfsTraversalList = new LinkedList<InstanceNode>();

        // The top level HierarchicalInstance
        _topInstanceNode = new InstanceNode(topInstance);

        // This is a list of nets that will be created in the flattened cell.
        // They are PseudoNets because some of them may be merged together into
        // one new EdifNet.
        LinkedList<PseudoNet> newPseudoNets = new LinkedList<PseudoNet>();

        // handle top level separately before starting breadth first search traversal
        {
            // The following two maps are inside this block because they are
            // only valid for the contents of one instance at a time because
            // one EdifCellInstance may correspond to multiple nodes in the
            // InstanceNode hierarchy, but it will correspond to only one node
            // within a particular node at a time. They are populated during
            // instance traversal and used during net processing. For clarity,
            // new versions of these maps are created for each subcell that is
            // traversed.
            Map<EdifCellInstance, InstanceNode> oldInstanceToInstanceNode = new LinkedHashMap<EdifCellInstance, InstanceNode>();
            Map<EdifCellInstance, EdifCellInstance> oldToNewInstances = new LinkedHashMap<EdifCellInstance, EdifCellInstance>();

            // Iterate subCells; create flattened versions of leaf cells and
            // add non-leaf cells to bfsTraversalList
            oldInstanceToInstanceNode.put(topInstance, _topInstanceNode);
            bfsTraversalList.addAll(processSubCells(oldInstanceToInstanceNode, oldToNewInstances, _topInstanceNode));

            // Process the nets at the top level
            // Create a new PseudoNet for each; add top level connections
            // and any connections to leaf cells; for non-leaf cells, add
            // map entries in the connection map corresponding to the
            // correct InstanceNode for connections to EdifSingleBitPorts.
            if (_originalCell.getNetList() != null) {
                for (EdifNet net : _originalCell.getNetList()) {
                    PseudoNet newPseudoNet = new PseudoNet(_originalCell, net);
                    newPseudoNets.add(newPseudoNet);
                    for (EdifPortRef epr : net.getConnectedPortRefs()) {
                        if (epr.isTopLevelPortRef()) {
                            int bitPosition = epr.getSingleBitPort().bitPosition();
                            EdifPort matchingPort = getMatchingPort(epr.getSingleBitPort().getParent());
                            EdifSingleBitPort matchingEsbp = matchingPort.getSingleBitPort(bitPosition);
                            newPseudoNet.addTopLevelConnection(matchingEsbp);
                        } else {
                            if (epr.getCellInstance().getCellType().isLeafCell())
                                newPseudoNet.addConnection(oldToNewInstances.get(epr.getCellInstance()), epr
                                        .getSingleBitPort());
                            else
                                connections.addConnection(newPseudoNet, oldInstanceToInstanceNode.get(epr
                                        .getCellInstance()), epr.getSingleBitPort());
                        }
                    }
                }
            }
        }

        // Commence breadth first search traversal of instance hierarchy
        while (!bfsTraversalList.isEmpty()) {
            // The following two maps are valid only for the contents of one
            // instance at a time because one EdifCellInstance may correspond
            // to multiple nodes in the InstanceNode hierarchy, but it will
            // correspond to only one node within a particular node at a time.
            Map<EdifCellInstance, InstanceNode> oldInstanceToInstanceNode = new LinkedHashMap<EdifCellInstance, InstanceNode>();
            Map<EdifCellInstance, EdifCellInstance> oldToNewInstances = new LinkedHashMap<EdifCellInstance, EdifCellInstance>();

            InstanceNode currentNode = bfsTraversalList.poll();
            EdifCellInstance currentInstance = currentNode.getInstance();
            EdifCell currentCell = currentInstance.getCellType();

            // Iterate subCells; create flattened versions of leaf cells and
            // add non-leaf cells to bfsTraversalList
            bfsTraversalList.addAll(processSubCells(oldInstanceToInstanceNode, oldToNewInstances, currentNode));

            // Process the nets in this cell
            // Create a new PseudoNet for each that does not connect to a
            // net at a higher level; add top level connections
            // and any connections to leaf cells; for non-leaf cells, add
            // map entries in the connection map corresponding to the
            // correct InstanceNode for connections to EdifSingleBitPorts.
            if (currentCell.getNetList() != null) {
                for (EdifNet net : currentCell.getNetList()) {
                    Collection<EdifPortRef> nonTopLevelConnections = new ArrayList<EdifPortRef>();
                    Collection<PseudoNet> higherLevelPseudoNets = new LinkedHashSet<PseudoNet>();
                    for (EdifPortRef epr : net.getConnectedPortRefs()) {
                        PseudoNet higherLevelPseudoNet = null;
                        if (epr.isTopLevelPortRef()) {
                            higherLevelPseudoNet = connections.query(currentNode, epr.getSingleBitPort());
                        } else {
                            nonTopLevelConnections.add(epr);
                        }
                        if (higherLevelPseudoNet != null)
                            higherLevelPseudoNets.add(higherLevelPseudoNet);
                    }

                    // There are three cases for how many higher level nets
                    // each might be attached to - 0 means a new net must be
                    // created, 1 is a normal case, and >1 means that the higher
                    // level nets should be joined -- in any case, the current
                    // level net's connections should be added to the net at
                    // the higher level or the newly created net (in the case
                    // where there is more than one, they only need to be added
                    // to one) and the current level net should be added to the
                    // higher level or newly created net's list of original
                    // nets; also map entries should be added for connectivity
                    // to lower levels from the matching higher level net(s).
                    PseudoNet matchingPseudoNet = null;
                    switch (higherLevelPseudoNets.size()) {
                    case 0:
                        matchingPseudoNet = new PseudoNet(_originalCell, net);
                        newPseudoNets.add(matchingPseudoNet);
                        break;
                    case 1:
                        matchingPseudoNet = higherLevelPseudoNets.iterator().next();
                        matchingPseudoNet.addOriginalNet(net);
                        break;
                    default:
                        matchingPseudoNet = higherLevelPseudoNets.iterator().next();
                        matchingPseudoNet.addOriginalNet(net);
                        connections.markToJoin(higherLevelPseudoNets);
                        break;
                    }
                    for (EdifPortRef epr : nonTopLevelConnections) {
                        if (epr.getCellInstance().getCellType().isLeafCell())
                            matchingPseudoNet.addConnection(oldToNewInstances.get(epr.getCellInstance()), epr
                                    .getSingleBitPort());
                        else
                            connections.addConnection(matchingPseudoNet, oldInstanceToInstanceNode.get(epr
                                    .getCellInstance()), epr.getSingleBitPort());
                    }
                }
            }
        }

        // Create new EdifNets based on the created PseudoNets and add them
        // to the FlattenedEdifCell (this) -- only create one EdifNet for
        // each set of joined PseudoNets -- add map entries from old nets
        // to new nets
        while (!newPseudoNets.isEmpty()) {
            PseudoNet pseudoNet = newPseudoNets.poll();
            EdifNet newNet = new EdifNet(getUniqueNetNameable(pseudoNet.getEdifNameable()), this);
            pseudoNet.insertPortRefs(newNet);
            Collection<PseudoNet> possibleJoinedNets = connections.getJoinedNetList(pseudoNet);
            if (possibleJoinedNets != null) {
                for (PseudoNet joinedPseudoNet : possibleJoinedNets) {
                    joinedPseudoNet.insertPortRefs(newNet);
                    newPseudoNets.remove(joinedPseudoNet);
                }
            }
            try {
                addNet(newNet);
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }
        }
    }

    /**
     * Given a HierarchicalInstance node, get a Collection of
     * FlattenedEdifCellInstances that are leaf instances that would be "within"
     * the original instance associated with the node.
     * 
     * @param instanceNode the HierarchicalInstance node corresponding to an
     * original instance
     * @return a Collection of FlattenedEdifCellInstances that correspond to
     * original instances "within" the instance associated with the given
     * HierarchicalInstance node
     */
    protected Collection<FlattenedEdifCellInstance> getInstancesWithinNode(HierarchicalInstance instanceNode) {
        Collection<FlattenedEdifCellInstance> result = new ArrayList<FlattenedEdifCellInstance>();
        if (instanceNode == null)
            return result;
        LinkedList<HierarchicalInstance> bfsTraversalList = new LinkedList<HierarchicalInstance>();
        bfsTraversalList.add(instanceNode);
        while (!bfsTraversalList.isEmpty()) {
            HierarchicalInstance currentNode = bfsTraversalList.poll();
            FlattenedEdifCellInstance matchingInstance = _nodesToFlatInstances.get(currentNode);
            if (matchingInstance != null)
                result.add(matchingInstance);
            for (HierarchicalInstance childNode : currentNode.getChildren())
                bfsTraversalList.add(childNode);
        }
        return result;
    }

    /**
     * Process the subCells in the current cell being traversed. Clone any leaf
     * cells encountered and return other cells in a collection to be added to
     * the traversal list. Add map entries from old instances to InstanceNodes
     * and from old instances to new instances.
     * 
     * @param oldInstanceToInstanceNode map for entries from old instances to
     * InstanceNodes
     * @param oldToNewInstances map for entries from old instances to new
     * instances
     * @param currentNode the node whose contents are being processed
     * @return a Collection of InstanceNodes to be added to the traversal list
     */
    protected Collection<InstanceNode> processSubCells(Map<EdifCellInstance, InstanceNode> oldInstanceToInstanceNode,
            Map<EdifCellInstance, EdifCellInstance> oldToNewInstances, InstanceNode currentNode) {
        EdifCell currentCell = currentNode.getCellType();
        Collection<InstanceNode> bfsAdditions = new ArrayList<InstanceNode>();
        if (currentCell.getSubCellList() != null) {
            for (EdifCellInstance child : currentCell.getSubCellList()) {
                InstanceNode childNode = currentNode.addChild(child);
                oldInstanceToInstanceNode.put(child, childNode);
                if (child.getCellType().isLeafCell()) {
                    EdifNameable nonUniqueName = null;
                    try {
                        nonUniqueName = new RenamedObject(childNode.getInstance().getName(), _naming
                                .getHierarchicalInstanceName(childNode));
                    } catch (InvalidEdifNameException e1) {
                        e1.toRuntime();
                    }
                    EdifNameable newName = getUniqueInstanceNameable(nonUniqueName);
                    FlattenedEdifCellInstance newInstance = new FlattenedEdifCellInstance(newName, this, child
                            .getCellType(), childNode);
                    newInstance.copyProperties(child);
                    _nodesToFlatInstances.put(childNode, newInstance);
                    try {
                        addSubCell(newInstance);
                    } catch (EdifNameConflictException e) {
                        e.toRuntime();
                    }
                    oldToNewInstances.put(child, newInstance);
                } else {
                    bfsAdditions.add(childNode);
                }
            }
        }
        return bfsAdditions;
    }

    /**
     * A mapping from HierarchicalInstance nodes to their counterpart
     * FlattenedEdifCellInstance. Not all nodes will be mapped because not all
     * instances in the original cell are instances of leaf cells
     */
    protected Map<HierarchicalInstance, FlattenedEdifCellInstance> _nodesToFlatInstances = new LinkedHashMap<HierarchicalInstance, FlattenedEdifCellInstance>();

    /**
     * The naming scheme to be used by this flattened cell
     */
    protected HierarchyNaming _naming;

    /**
     * A reference to the original EdifCell from which the flattened version was
     * created
     */
    protected EdifCell _originalCell;

    /**
     * A reference to the top InstanceNode
     */
    protected InstanceNode _topInstanceNode;
}