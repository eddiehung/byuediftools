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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
import edu.byu.ece.edif.arch.xilinx.XilinxMergeParser;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCellInstance;
import edu.byu.ece.edif.tools.flatten.HierarchicalInstance;
import edu.byu.ece.edif.tools.flatten.InstanceNode;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;

public class HalfLatchFlattenedEdifCell extends FlattenedEdifCell {

    /**
     * Construct a new HalfLatchFlattenedEdifCell based on the given
     * FlattenedEdifCell and in the same library as the original. The new
     * Cell will have the suffix "_hl" to prevent name conflicts.
     * 
     * @param flatCell the FlattenedEdifCell to remove half-latches from
     * @throws EdifNameConflictException
     * @throws InvalidEdifNameException
     */
    public HalfLatchFlattenedEdifCell(FlattenedEdifCell flatCell, HalfLatchArchitecture hlArchitecture,
            int safeConstantPolarity) throws EdifNameConflictException, InvalidEdifNameException {
        super(flatCell.getLibrary(), flatCell.getName() + "_hl", flatCell.getInterface());
        _originalCell = flatCell.getOriginalCell();
        _naming = flatCell.getHierarchyNaming();

        _hlArchitecture = hlArchitecture;
        _safeConstantPolarity = safeConstantPolarity;

        copyCellAndRemoveHalfLatches(flatCell);
    }

    /**
     * Construct a new HalfLatchFlattenedEdifCell based on the given
     * FlattenedEdifCell and in the given library
     * 
     * @param lib the library to construct the HalfLatchFlattenedEdifCell in
     * @param flatCell the FlattenedEdifCell to remove half-latches from
     * @throws EdifNameConflictException
     * @throws InvalidEdifNameException
     */
    public HalfLatchFlattenedEdifCell(EdifLibrary lib, FlattenedEdifCell flatCell,
            HalfLatchArchitecture hlArchitecture, int safeConstantPolarity) throws EdifNameConflictException,
            InvalidEdifNameException {
        super(lib, flatCell.getName(), flatCell.getInterface());
        _originalCell = flatCell.getOriginalCell();
        _naming = flatCell.getHierarchyNaming();

        _hlArchitecture = hlArchitecture;
        _safeConstantPolarity = safeConstantPolarity;

        copyCellAndRemoveHalfLatches(flatCell);
    }

    /**
     * Create a safe constant generating element to be added to 'this' cell. The
     * element is architecture specific (for example, in a Xilinx architecture
     * an all 0 LUT will be used). Disconnect the 'safe constant' port and drive
     * everything it was driving with the new 'safe constant' generator. TODO:
     * This should probably automatically call the removeSafeConstantPortBuffer
     * method.
     */
    public void addSafeConstantGeneratorCell() {
        if (_safeConstantGeneratorCell == null) {
            // Add a 'safe constant' cell to the top cell we just found with the given polarity
            _safeConstantGeneratorCell = _hlArchitecture.addConstantCellInstance(this, _safeConstantPolarity);

            // Find and delete the connection between this cell's 'safe constant' port and 'safe constant' net			
            this.removeSafeConstantPort();

            // Now add a connection from the 'safe constant' cell we just created to the 'safe constant' net
            String safeConstantCellOutputPortName = _hlArchitecture.getSafeConstantCellOutputPortName();
            EdifSingleBitPort safeConstantCellOutputPort = _safeConstantGeneratorCell.getCellType().getPort(
                    safeConstantCellOutputPortName).getSingleBitPort(0);
            EdifPortRef new_epr = new EdifPortRef(findOrAddSafeConstantNet(), safeConstantCellOutputPort,
                    _safeConstantGeneratorCell);
            findOrAddSafeConstantNet().addPortConnection(new_epr);
        }
    }

    /**
     * When adding a sub cell to a HalfLatchFlattenedEdifCell, the instance
     * should be added to the instanceNode as well.
     */
    public boolean addSubCell(EdifCellInstance cellInstance) throws EdifNameConflictException {
        // This super call must be to EdifCell's version of addSubCell (not
        //   FlattenedEdifCell's)
        boolean success = super.addSubCell(cellInstance);
        if (success == true) {
            // Must use addChildNoCheck since the original Cell has been modified
            HierarchicalInstance instanceNode = _topInstanceNode.addChildNoCheck(cellInstance);
            // Also add mapping from instanceNode to flat instance
            _nodesToFlatInstances.put(instanceNode, (FlattenedEdifCellInstance) cellInstance);
        }
        return success;
    }

    /**
     * This method should be called on the top-level cell if an internal
     * constant cell is not going to be generated to drive the safe constant
     * distribution network. The top-level port needs to be buffered so it will
     * be kept.
     */
    public void bufferSafeConstantPort() {
        if (_safeConstantGeneratorCell != null)
            throw new EdifRuntimeException(
                    "Error: Cannot buffer safe-constant port in cell which already had port removed and internal constant generator source added.");

        if (_inputBufferCellInstance == null) {

            // Add an ibuf cell instance
            EdifLibraryManager elm = this.getLibrary().getLibraryManager();
            EdifCell inputBufferCell = _hlArchitecture.findOrAddPrimitiveInputBufferCell(elm);
            try {
                _inputBufferCellInstance = new FlattenedEdifCellInstance("safeConstantInputBuffer", this,
                        inputBufferCell, null);
                this.addSubCell(_inputBufferCellInstance);
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }

            // Create a net to connect the top-level port to the ibuf
            // Connect the top-level port to the new net.
            // Connect the ibuf input port to the new net.
            String constantPortToBufferNetName = null;
            if (_safeConstantPolarity == 0)
                constantPortToBufferNetName = _safeConstantZeroNetName + "_i";
            else
                constantPortToBufferNetName = _safeConstantOneNetName + "_i";
            // create a net to connect the top-level port to the ibuf
            EdifNet constantPortToBufferNet = null;
            try {
                constantPortToBufferNet = new EdifNet(constantPortToBufferNetName);
                // add the net to 'this' cell
                this.addNet(constantPortToBufferNet);
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }
            // get a reference to the top-level 'safe constant' port of 'this' cell
            EdifSingleBitPort topLevelSafePort = findOrAddSafeConstantPort().getSingleBitPort(0);
            // make a port ref object to connect the port to the net
            EdifPortRef constantPortToBufferNetEPR = new EdifPortRef(constantPortToBufferNet, topLevelSafePort, null);
            // add the port ref to the net
            constantPortToBufferNet.addPortConnection(constantPortToBufferNetEPR);
            // get a reference to the input port of the new input buffer cell
            EdifSingleBitPort inputBufferInputPort = inputBufferCell.getPort(
                    _hlArchitecture.getPrimitiveInputBufferCellInputPortName()).getSingleBitPort(0);
            // make a port ref object to connect the port to the net
            EdifPortRef bufferNetToBufferEPR = new EdifPortRef(constantPortToBufferNet, inputBufferInputPort,
                    _inputBufferCellInstance);
            // add the port ref to the net
            constantPortToBufferNet.addPortConnection(bufferNetToBufferEPR);

            // Connect the ibuf to the internal 'safe constant' net
            // TODO: Should this port ref really be _safeConstantNetSafeConstantPortEPR???
            // get a reference to the output port of the new input buffer cell
            EdifSingleBitPort inputBufferOutputPort = inputBufferCell.getPort(
                    _hlArchitecture.getPrimitiveInputBufferOutputBufferName()).getSingleBitPort(0);
            // make a port ref object to connect the port to the net
            // add the port ref to the net
            this.findOrAddSafeConstantNet().addPortConnection(
                    new EdifPortRef(this.findOrAddSafeConstantNet(), inputBufferOutputPort, _inputBufferCellInstance));

            // Delete the port connection between the safe constant port
            // and the safe constant net
            this.deleteSafeConstantNetSafeConstantPortEPR();
        }
    }

    public EdifNet findOrAddSafeConstantNet() {
        return findOrAddSafeConstantNetNormPolarity();
    }

    public EdifNet findOrAddSafeConstantNet(int polarity) {
        EdifNet result;
        if (polarity == 0 && _safeConstantPolarity == 0 || polarity == 1 && _safeConstantPolarity == 1)
            result = findOrAddSafeConstantNetNormPolarity();
        else
            result = findOrAddSafeConstantNetInvPolarity();
        return result;
    }

    public EdifPortRef findOrAddSafeConstantNetSafeConstantPortEPR() {
        if (_safeConstantNetSafeConstantPortEPR == null) {
            if (_safeConstantNetNorm != null) {
                _safeConstantNetNorm.addPortConnection(new EdifPortRef(this.findOrAddSafeConstantNet(), this
                        .findOrAddSafeConstantPort().getSingleBitPort(0), null));
                System.out.println("Re-adding port ref from port to safe constant net");
            } else
                this.addSafeConstantNetNormPolarity();
        }
        return _safeConstantNetSafeConstantPortEPR;
    }

    public EdifPort findOrAddSafeConstantPort() {
        EdifPort result;
        if (_safeConstantPort == null)
            result = addSafeConstantPort();
        else
            result = _safeConstantPort;
        return result;
    }

    public EdifCellInstance getSafeConstantGeneratorCell() {
        return _safeConstantGeneratorCell;
    }

    public EdifNet getSafeConstantNet(int polarity) {
        EdifNet result;
        if (polarity == 0 && _safeConstantPolarity == 0 || polarity == 1 && _safeConstantPolarity == 1)
            result = _safeConstantNetNorm;
        else
            result = _safeConstantNetInv;
        return result;
    }

    public EdifCellInstance getSafeConstantPortBufferInstance() {
        return _inputBufferCellInstance;
    }

    public EdifPort renameSafeConstantPort(String portName) {
        // Remove the old 'safe constant' port
        if (_safeConstantPort != null) {
            removeSafeConstantPort();
        }
        // Create the new 'safe constant' port with the specified name
        addSafeConstantPort(portName);

        return _safeConstantPort;
    }

    protected void copyCellAndRemoveHalfLatches(FlattenedEdifCell flatCell) {
        // Copy all EdifCellInternals as well as the FlattenedEdifCell
        //   structures, removing half-latches along the way
        // 1. Copy the cell properties
        // 2. Copy the Instances
        //    a. If sensitive primitive, create half-latch safe replacement
        //       i. Keep Collection of all half-latch ports to connect
        //    b. If constant primitive, replace all outgoing connections with
        //         connections to the safeConstantNet
        //    c. If not sensitive or constant primitive, copy instance directly
        //    d. Keep mapping between original instance and copy/replacement
        // 3. Copy the Nets
        //    a. Use the instance mapping to wire up the nets correctly
        //    b. All PortRefs must be re-created
        // 4. Copy the FlattenedEdifCell properties
        //    a. Copy _oldToNewNets Map

        // 4a. The top level HierarchicalInstance - Make a copy
        _topInstanceNode = new InstanceNode((InstanceNode) flatCell.getTopInstanceNode());

        // 1. Copy the cell properties
        if (flatCell.getPropertyList() != null) {
            for (Iterator it = flatCell.getPropertyList().values().iterator(); it.hasNext();) {
                Property p = (Property) it.next();
                this.addProperty((Property) p.clone());
            }
        }

        // 2. Copy the Instances
        //    a. If sensitive primitive, create half-latch safe replacement
        //       i. Keep Collection of all half-latch ports to connect
        //    b. If constant primitive, replace all outgoing connections with
        //         connections to the safeConstantNet
        //    c. If not sensitive or constant primitive, copy instance directly
        //    d. Keep mapping between original instance and copy/replacement
        Map<EdifPort, EdifPort> oldToNewPorts = new LinkedHashMap<EdifPort, EdifPort>();
        Map<FlattenedEdifCellInstance, FlattenedEdifCellInstance> oldToNewInstances = new LinkedHashMap<FlattenedEdifCellInstance, FlattenedEdifCellInstance>();
        Map<EdifNet, EdifNet> oldToNewNets = new LinkedHashMap<EdifNet, EdifNet>();

        copyInstances(flatCell, oldToNewPorts, oldToNewInstances);

        // Add top cell to mapping of old instance Ports to new instance Ports
        for (EdifPort oldPort : flatCell.getPortList()) {
            EdifPort newPort = this.getPort(oldPort.getName());
            if (!oldToNewPorts.containsKey(oldPort)) {
                oldToNewPorts.put(oldPort, newPort);
            }
        }

        // 3. Copy the Nets
        //    a. Use the instance mapping to wire up the nets correctly
        //    b. All PortRefs must be re-created
        copyNets(flatCell, oldToNewPorts, oldToNewInstances, oldToNewNets);

        // 4. Copy the FlattenedEdifCell properties
        //    a. Copy _oldToNewNets Map

        // 4a. Make a copy of the _oldToNewNets Map, substituting in the new
        //     flat instances
        // NOTE: We are re-using the old InstanceNode objects since they only
        //  refer to the original cells and instances, not the flattened cell
        Collection<HierarchicalInstance> hierarchicalNodes = flatCell.getAllHierarchicalInstances();
        for (HierarchicalInstance hierarchyNode : hierarchicalNodes) {
            FlattenedEdifCellInstance oldInstance = flatCell.getFlatInstance(hierarchyNode);
            FlattenedEdifCellInstance newInstance = oldToNewInstances.get(oldInstance);
            _nodesToFlatInstances.put(hierarchyNode, newInstance);
        }

    }

    private void copyNets(FlattenedEdifCell flatCell, Map<EdifPort, EdifPort> oldToNewPorts,
            Map<FlattenedEdifCellInstance, FlattenedEdifCellInstance> oldToNewInstances,
            Map<EdifNet, EdifNet> oldToNewNets) {
        List<EdifPortRef> badCutPorts = new ArrayList<EdifPortRef>();
        for (Iterator<EdifNet> netIterator = (Iterator<EdifNet>) flatCell.netListIterator(); netIterator.hasNext();) {
            EdifNet oldNet = netIterator.next();
            EdifNet newNet = null;
            // Check for Constant Primitive output Net
            List<EdifPortRef> constPrims = getConstantPrimitiveEPRs(oldNet, _hlArchitecture);
            boolean isConstantNet = !constPrims.isEmpty();
            if (isConstantNet) {
                // Get Polarity of Constant driving the Net. Assume same polarity
                //   from all drivers.
                int constPolarity = _hlArchitecture.getConstantCellValue(constPrims.get(0).getCellInstance().getType());
                // All sinks of this Net should now be driven by the Safe Constant Net
                if (constPolarity == _safeConstantPolarity)
                    newNet = findOrAddSafeConstantNetNormPolarity();
                else
                    newNet = findOrAddSafeConstantNetInvPolarity();
            } else
                // Create a new Net for the new HLFEdifCell
                newNet = new EdifNet(oldNet.getEdifNameable(), this);

            // Must keep track of which old net corresponds to this new net
            oldToNewNets.put(oldNet, newNet);
            // iterate portRefs
            for (Iterator<EdifPortRef> portRefIterator = (Iterator<EdifPortRef>) oldNet.getPortRefIterator(); portRefIterator
                    .hasNext();) {
                EdifPortRef oldRef = portRefIterator.next();

                // Check for Constant Primitive. Skip these.
                // BHP: Maybe better to check for any drivers? Any net drivers
                //   should NOT be hooked up to the safeConstantNet.
                if (constPrims.contains(oldRef))
                    continue;

                EdifSingleBitPort oldSbp = oldRef.getSingleBitPort();
                EdifSingleBitPort newSbp = oldToNewPorts.get(oldSbp.getParent()).getSingleBitPort(oldSbp.bitPosition());

                EdifCellInstance newEci = null;
                if (oldRef.getCellInstance() != null)
                    newEci = oldToNewInstances.get(oldRef.getCellInstance());
                EdifPortRef newEpr = new EdifPortRef(newNet, newSbp, newEci);
                if (this._hlArchitecture.isBadCutPin(newEpr.getPort())) {
                    badCutPorts.add(newEpr);
                } else
                    newNet.addPortConnection(newEpr);

                //				if (newEpr.toString().contains("CASCADE")) {
                //                    System.out.println(newEpr.getPort().getName() + " net: " + oldNet + " newNet: " + newNet);
                //                }

            }

            // If this was a constant net, stop here. We don't need to copy 
            //   properties (they could conflict) or re-add the safe constant
            //   net to the new Cell.
            if (isConstantNet)
                continue;

            // copy net properties
            if (oldNet.getPropertyList() != null) {
                for (Iterator it = oldNet.getPropertyList().values().iterator(); it.hasNext();) {
                    Property p = (Property) it.next();
                    newNet.addProperty((Property) p.clone());
                }
            }
            try {
                this.addNet(newNet);
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }
        }
        if (!badCutPorts.isEmpty()) {
            try {
                EdifNet newNet = new EdifNet("GNDZ0");
                EdifLibraryManager elm = this.getLibrary().getLibraryManager();
                EdifCell gnd = XilinxLibrary.findOrAddXilinxPrimitive(elm, "GND");
                EdifCellInstance new_gnd = null;
                new_gnd = new FlattenedEdifCellInstance("new_gnd", this, gnd, null);

                //				ArrayList gnd_outs = new ArrayList(gnd.getOutputPorts());
                //                if (gnd_outs.size() != 1)
                //                    throw new EdifRuntimeException("Bad ground:" + gnd.getInterface());
                //                EdifPort gnd_out = (EdifPort) gnd_outs.get(0);
                //                newNet.addPortConnection(new_gnd, BasicEdifBusNetNamingPolicy.getBusBaseNameStatic(gnd_out.getName()));     

                newNet.addPortConnection(new_gnd, "G");

                for (EdifPortRef epr : badCutPorts) {
                    newNet.addPortConnection(epr);
                }
                this.addNet(newNet);
                this.addSubCell(new_gnd);
            } catch (InvalidEdifNameException e) {
                e.printStackTrace();
            } catch (EdifNameConflictException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyInstances(FlattenedEdifCell flatCell, Map<EdifPort, EdifPort> oldToNewPorts,
            Map<FlattenedEdifCellInstance, FlattenedEdifCellInstance> oldToNewInstances) {
        for (Iterator<EdifCellInstance> instanceIterator = (Iterator<EdifCellInstance>) flatCell.cellInstanceIterator(); instanceIterator
                .hasNext();) {
            FlattenedEdifCellInstance oldEci = (FlattenedEdifCellInstance) instanceIterator.next();

            // Choose new EdifCell type
            // - Converted for sensitive primitives
            // - Same as original for non-sensitive
            EdifCell newCellRef = null;
            FlattenedEdifCellInstance newInstance = null;
            // Determine if the instanced Cell is a sensitive primitive or not
            if (oldEci.getCellType().isPrimitive() && _hlArchitecture.cellRequiresReplacement(oldEci)) {
                // Find out what the 'safe primitive' is for this sensitive primitive
                String safePrimitiveType = _hlArchitecture.getPrimitiveReplacementType(oldEci);
                EdifLibraryManager elm = getLibrary().getLibraryManager();
                newCellRef = _hlArchitecture.findOrAddPrimitiveReplacementCell(elm, safePrimitiveType);
                // Create new instance in the new HalfLatchFlattenedEdifCell
                // Use the same name as the original (flat) instance.
                newInstance = new FlattenedEdifCellInstance(oldEci.getEdifNameable(), this, newCellRef, oldEci
                        .getHierarchicalInstance());

                // Attach all the floating ports on this new Instance to the
                //   half-latch constants
                String[] unconnectedPorts = _hlArchitecture.getPrimitiveReplacementFloatingPorts(oldEci);
                // Place each port in the correct Collection
                for (int i = 0; i < unconnectedPorts.length; i++) {
                    // Get the actual port represented by this String
                    EdifPort ep = newCellRef.getPort(unconnectedPorts[i]);
                    EdifSingleBitPort esbp = ep.getSingleBitPort(0);
                    // Get the driving value for this port
                    int defaultPortValue = _hlArchitecture.getPrimitiveReplacementFloatingPortDefaultValue(oldEci,
                            unconnectedPorts[i]);
                    // Create a new port ref connecting the 'safe constant' net to the unconnected port of the new 'safe' primitive 
                    EdifPortRef epr = new EdifPortRef(findOrAddSafeConstantNet(defaultPortValue), esbp, newInstance);
                    findOrAddSafeConstantNet(defaultPortValue).addPortConnection(epr);
                }

            } else if (oldEci.getCellType().isPrimitive()
                    && _hlArchitecture.isConstantCell(oldEci.getCellType().getName())) {
                // Constant Primitive.
                // Don't create a new Instance for this, just replace all Net
                //   connections in the original cell with connections to the 
                //   safe constant net.
                newCellRef = null;
                newInstance = null;
                continue;
            } else {
                // Non-primitive or non-sensitive primitive.
                newCellRef = oldEci.getCellType();
                // Create new instance in the new HalfLatchFlattenedEdifCell
                // Use the same name as the original (flat) instance.
                newInstance = new FlattenedEdifCellInstance(oldEci.getEdifNameable(), this, newCellRef, oldEci
                        .getHierarchicalInstance());
            }

            // Populate mapping of old instance Ports to new instance Ports
            for (EdifPort oldPort : oldEci.getCellType().getPortList()) {
                EdifPort newPort = newCellRef.getPort(oldPort.getName());
                if (!oldToNewPorts.containsKey(oldPort)) {
                    oldToNewPorts.put(oldPort, newPort);
                }
            }

            // copy instance properties
            if (oldEci.getPropertyList() != null) {
                for (Iterator it = oldEci.getPropertyList().values().iterator(); it.hasNext();) {
                    Property p = (Property) it.next();
                    newInstance.addProperty((Property) p.clone());
                }
            }

            // Add this ECI to the mapping
            oldToNewInstances.put(oldEci, newInstance);

            // Add this ECI to the new Cell
            try {
                // Use the super method (from EdifCell) so an InstanceNode is
                //   not created--we don't need new ones.
                super.addSubCell(newInstance);
            } catch (EdifNameConflictException e) {
                // This shouldn't happen since the instance name was valid
                //   in the original cell
                e.toRuntime();
            }
        }
    }

    /**
     * Checks the drivers of the given EdifNet and returns a Collection of the
     * EdifPortRef objects of any constant primitives that drive this Net.
     * 
     * @param net The EdifNet object to examine
     * @param hlArch The HalfLatchArchitecture object which determines which
     * cells are constant cells
     * @return A Collection of EPRs of constant primitives driving this Net, or
     * an empty Collection if there are none.
     */
    protected static List<EdifPortRef> getConstantPrimitiveEPRs(EdifNet net, HalfLatchArchitecture hlArch) {
        List<EdifPortRef> constPrimEPRs = new ArrayList(1); // Usually only a single driver, if any
        for (EdifPortRef sourceEPR : net.getSourcePortRefs(false, false)) {
            if (hlArch.isConstantCell(sourceEPR.getCellInstance().getType()))
                constPrimEPRs.add(sourceEPR);
        }
        return constPrimEPRs;
    }

    /**
     * Checks the drivers of the given EdifNet and returns true if one of these
     * is a constant primitive.
     * 
     * @param net The EdifNet object to examine
     * @param hlArch The HalfLatchArchitecture object which determines which
     * cells are constant cells
     * @return true if the given EdifNet is driven by a constant, false
     * otherwise
     */
    protected static boolean isConstantNet(EdifNet net, HalfLatchArchitecture hlArch) {
        for (EdifPortRef sourceEPR : net.getSourcePortRefs(false, false)) {
            if (hlArch.isConstantCell(sourceEPR.getCellInstance().getType()))
                return true;
        }
        return false;
    }

    /**
     * If the safe constant net with *normal* polarity for 'this' cell has not
     * already been created and added, then this method call will first create
     * and add the net to 'this' cell. By default, this method will create a net
     * which is attached to the output of the inverter this method is supposed
     * to create.
     */
    private void addSafeConstantInverter() {
        if (_safeConstantInverterInstance == null) {
            // create an inverter
            EdifCell inverterCell = _hlArchitecture.findOrAddPrimitiveInverterCell(this.getLibrary()
                    .getLibraryManager());
            try {
                _safeConstantInverterInstance = new FlattenedEdifCellInstance("HL_INV", this, inverterCell, null);
                this.addSubCell(_safeConstantInverterInstance);
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }

            // get a copy of the original safe constant net
            EdifNet thisSafeConstantNet = this.findOrAddSafeConstantNet();
            // create a new net (hereafter referred to as 'safe constant' net inv
            EdifNet thisSafeConstantNetInv = this.addSafeConstantNetInvPolarity();
            // connect the normal polarity safe constant net to the input of the inverter		
            EdifSingleBitPort inverterInputPort = inverterCell.getPort(
                    _hlArchitecture.getPrimitiveInverterCellInputPortName()).getSingleBitPort(0);
            EdifPortRef constantNetInverterInputEPR = new EdifPortRef(thisSafeConstantNet, inverterInputPort,
                    _safeConstantInverterInstance);
            thisSafeConstantNet.addPortConnection(constantNetInverterInputEPR);
            // connect the inverse polarity safe constant net to the output of the inverter
            EdifSingleBitPort inverterOutputPort = inverterCell.getPort(
                    _hlArchitecture.getPrimitiveInverterCellOutputPortName()).getSingleBitPort(0);
            EdifPortRef constantInvNetInverterOutputEPR = new EdifPortRef(thisSafeConstantNetInv, inverterOutputPort,
                    _safeConstantInverterInstance);
            thisSafeConstantNetInv.addPortConnection(constantInvNetInverterOutputEPR);
        }
    }

    /**
     * This method should NOT be directly called. It is meant as a utility
     * function for the function findOrAddSafeConstantNetInvPolarity()
     */
    private EdifNet addSafeConstantNetInvPolarity() {
        if (_safeConstantNetInv == null) {
            String netName = null;
            if (_safeConstantPolarity == 0)
                netName = _safeConstantOneNetName;
            else
                netName = _safeConstantZeroNetName;
            try {
                _safeConstantNetInv = new EdifNet(netName);
                addNet(_safeConstantNetInv);
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }

        }
        return _safeConstantNetInv;
    }

    /**
     * This method should NOT be directly called. It is meant as a utility
     * function for the function findOrAddSafeConstantNetNormPolarity()
     */
    private EdifNet addSafeConstantNetNormPolarity() {
        if (_safeConstantNetNorm == null) {
            String netName = null;
            if (_safeConstantPolarity == 0)
                netName = _safeConstantZeroNetName;
            else
                netName = _safeConstantOneNetName;
            try {
                _safeConstantNetNorm = new EdifNet(netName);
                addNet(_safeConstantNetNorm);
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }

        }
        return _safeConstantNetNorm;
    }

    /**
     * This method should NOT be called directly. It is meant only as a utility
     * function for findOrAddSafeConstantPort(). This method pre-determines the
     * safeConstantPort's name. If the safe constant port for 'this' cell has
     * not already been created and added, then this method call will first
     * create and add the port to 'this' cell before returning a reference to
     * it.
     */
    private EdifPort addSafeConstantPort() {
        EdifPort result;
        if (_safeConstantPort == null) {
            String portName = null;
            if (_debug)
                System.out.println("Adding safe constant port to " + this + " with polarity " + _safeConstantPolarity);
            if (_safeConstantPolarity == 0)
                portName = _safeConstantZeroPortName;
            else
                portName = _safeConstantOnePortName;
            result = addSafeConstantPort(portName);
        } else
            result = _safeConstantPort;
        return result;
    }

    /**
     * This method should NOT be called directly. It is meant only as a utility
     * function for addSafeConstantPort(). If the safe constant port for 'this'
     * cell has not already been created and added, then this method call will
     * first create and add the port to 'this' cell before returning a reference
     * to it.
     */
    private EdifPort addSafeConstantPort(String portName) {
        if (_safeConstantPort == null) {
            if (portName == null)
                throw new EdifRuntimeException("String for new safe constant port is null");
            try {
                _safeConstantPort = addPort(portName, 1, EdifPort.IN);
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }
            // connect the new 'safe constant' port to the 'safe constant' net of this cell
            EdifSingleBitPort thisSafeConstantPort = _safeConstantPort.getSingleBitPort(0);
            _safeConstantNetSafeConstantPortEPR = new EdifPortRef(this.findOrAddSafeConstantNet(),
                    thisSafeConstantPort, null);
            _safeConstantNetNorm.addPortConnection(_safeConstantNetSafeConstantPortEPR);
        }
        return _safeConstantPort;
    }

    private void deleteSafeConstantNetSafeConstantPortEPR() {
        if (_safeConstantNetSafeConstantPortEPR != null) {
            _safeConstantNetSafeConstantPortEPR.getNet().deletePortConnection(_safeConstantNetSafeConstantPortEPR);
            _safeConstantNetSafeConstantPortEPR = null;
        }
    }

    /**
     * If the safe constant inverter and or the safe constant net with inverted
     * polarity for 'this' cell have not already been created and added, then
     * this method call will first create and add the inverter and net (attached
     * to the output of the inverter) to 'this' cell before returning a
     * reference to the inverted polarity net.
     */
    private EdifNet findOrAddSafeConstantNetInvPolarity() {
        EdifNet result;
        if (_safeConstantNetInv == null) {
            addSafeConstantInverter();
            result = addSafeConstantNetInvPolarity();
        } else
            result = _safeConstantNetInv;
        return result;
    }

    /**
     * If the safe constant net with normal polarity for 'this' cell has not
     * already been created and added, then this method call will first create
     * and add the net to 'this' cell before returning a reference to it.
     */
    private EdifNet findOrAddSafeConstantNetNormPolarity() {
        EdifNet result;
        if (_safeConstantNetNorm == null)
            result = addSafeConstantNetNormPolarity();
        else
            result = _safeConstantNetNorm;
        return result;
    }

    private void removeSafeConstantPort() {
        if (_safeConstantPort != null) {

            // TODO: Wouldn't this be easier if we just iterated through
            // all of the port's portRefs and deleted all of them???
            Object[] portRefs = this.getPortRefs().toArray();
            EdifPort safeConstantPort = _safeConstantPort;
            for (int i = 0; i < portRefs.length; i++) {
                EdifPortRef epr = (EdifPortRef) portRefs[i];
                if (epr.getPort().equals(safeConstantPort))
                    epr.getNet().deletePortConnection(epr);
            }
            //    		EdifNet connectedNet = this.getSafeConstantPort().getInnerNet();
            //            EdifPortRef safeConstantPortSafeConstantNetEPR = connectedNet.getEdifPortRef(null, _safeConstantPort
            //                    .getSingleBitPort(0));
            //            if (this.getSafeConstantNetSafeConstantPortEPR().equals(safeConstantPortSafeConstantNetEPR)) {
            //                System.out.println("Deleting safe constant net - safe constant port EPR");
            //                _safeConstantNetSafeConstantPortEPR = null;
            //            }
            //            safeConstantPortSafeConstantNetEPR.getNet().deletePortConnection(safeConstantPortSafeConstantNetEPR);

            // Delete the actual port
            this.deletePort(_safeConstantPort);
            _safeConstantNetSafeConstantPortEPR = null;
            _safeConstantPort = null;
        }
    }

    public static final String _safeConstantZeroNetName = "safeConstantNet_zero";

    public static final String _safeConstantOneNetName = "safeConstantNet_one";

    public static final String _safeConstantZeroPortName = "safeConstantPort_zero";

    public static final String _safeConstantOnePortName = "safeConstantPort_one";

    public static final boolean _debug = true;

    protected EdifPort _safeConstantPort = null;

    protected EdifNet _safeConstantNetNorm = null;

    protected EdifNet _safeConstantNetInv = null;

    protected EdifCellInstance _safeConstantInverterInstance = null;

    protected EdifPortRef _safeConstantNetSafeConstantPortEPR = null;

    protected EdifCellInstance _safeConstantGeneratorCell = null;

    protected EdifCellInstance _inputBufferCellInstance = null;

    protected HalfLatchArchitecture _hlArchitecture = null;

    protected int _safeConstantPolarity = 0;

    protected boolean _fixed = false;

    protected int _numConstantPortsToDriveWithSafeConstant = 0;

    public static int test(String inputEdifFilename, String outputEdifFilename) {
        String error_report = "";
        int error_count = 0;

        int safeConstantPolarity = 0;
        String safeConstantPortName = null, safeConstantNetName = null;
        if (safeConstantPolarity == 1) {
            safeConstantPortName = _safeConstantOnePortName;
            safeConstantNetName = _safeConstantOneNetName;
        } else {
            safeConstantPortName = _safeConstantZeroPortName;
            safeConstantNetName = _safeConstantZeroNetName;
        }

        // Load EDIF
        String[] args = new String[1];
        args[0] = inputEdifFilename;
        EdifCell top_cell = XilinxMergeParser.parseAndMergeXilinx(args);

        FlattenedEdifCell flatCell = null;
        try {
            flatCell = new FlattenedEdifCell(top_cell);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }

        XilinxHalfLatchArchitecture hlArchitecture = new XilinxHalfLatchArchitecture(flatCell);

        // This should do the actual half latch removal
        //SequentialEdifHalfLatchRemover sequentialEdifHalfLatchRemover = new SequentialEdifHalfLatchRemover(new XilinxHalfLatchArchitecture(), safeConstantPolarity, true);
        SequentialEdifHalfLatchRemover sequentialEdifHalfLatchRemover = new SequentialEdifHalfLatchRemover(
                hlArchitecture, safeConstantPolarity, false);
        FlattenedEdifCell hlFlatCell = sequentialEdifHalfLatchRemover.removeHalfLatches(flatCell);

        // Now we make all of our checks

        // All non-primitive cells should be of type HalfLatchEdifCell and
        // consequently should also have a safe constant net with the same name 
        // as the variable safeConstantNetName.
        // In addition, the safeConstantNet should connect to a port in the 
        // current cell with the same name as safeConstantPortName AND to a
        // like-named port in ALL non-primitive sub-cell instances of the
        // current cell.
        //        EdifLibraryManager elm = top_cell.getLibrary().getLibraryManager();
        //        for (Iterator i = elm.iterator(); i.hasNext();) {
        //            EdifLibrary lib = (EdifLibrary) i.next();
        //            for (Iterator j = lib.iterator(); j.hasNext();) {
        //                EdifCell cell = (EdifCell) j.next();
        //                if (cell.isPrimitive() == false) {
        //                    if (testHasSafeNet(cell, safeConstantNetName) == true) {
        //                        if (testHasSafePort(cell, safeConstantPortName) == false)
        //                            error_report += ("\n Error: HalfLatchEdifCell: Cell " + cell.getName()
        //                                    + " has no safe constant port, but it should have one named " + safeConstantPortName);
        //                        Collection<EdifCellInstance> nonConnectedNonPrimitiveSubCells = new ArrayList<EdifCellInstance>();
        //                        for (Iterator m = cell.getSubCellList().iterator(); m.hasNext();) {
        //                            EdifCellInstance subcell_eci = (EdifCellInstance) m.next();
        //                            if ((subcell_eci.getCellType().isPrimitive() == false)
        //                                    && (testHasSafePort(subcell_eci.getCellType(), safeConstantPortName) == false))
        //                                nonConnectedNonPrimitiveSubCells.add(subcell_eci);
        //                        }
        //                        if (nonConnectedNonPrimitiveSubCells.size() > 0)
        //                            error_report += ("\n Error: HalfLatchEdifCell: Cell "
        //                                    + cell.getName()
        //                                    + " has the following non-primitive sub-cell instances with no safe constant port... " + nonConnectedNonPrimitiveSubCells);
        //                    } else {
        //                        error_report += ("\n Error: HalfLatchEdifCell: Cell " + cell.getName()
        //                                + " has no safe constant net, but it should have one named " + safeConstantNetName);
        //                    }
        //                }
        //            }
        //        }

        // All primitive cells should not be sensitive primitives
        // && should not be constant primitives
        for (Iterator i = hlFlatCell.cellInstanceIterator(); i.hasNext();) {
            EdifCellInstance eci = (EdifCellInstance) i.next();
            if (hlArchitecture.isConstantCell(eci.getType()) == true) {
                error_report += ("\n Error: HalfLatchEdifCell: Design has remaining constant cell instance " + eci
                        .getName());
                error_count += 1;
            } else if (hlArchitecture.cellRequiresReplacement(eci) == true) {
                error_report += ("\n Error: HalfLatchEdifCell: Design has remaining half-latch prone primitive cell instance " + eci
                        .getName());
                error_count += 1;
            }
        }

        //elm.validateOrder();

        // If the user specified a non-null filename for the output edif
        // then write the modified edif to file.
        //        if (outputEdifFilename != "") {
        //            try {
        //                EdifPrintWriter epw = new EdifPrintWriter(new FileOutputStream(outputEdifFilename));
        //                top_cell.getLibrary().getLibraryManager().getEdifEnvironment().toEdif(epw);
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //                System.err.println(e);
        //                System.exit(1);
        //            }
        //        }

        // Print the error report
        int result;
        if (error_report != "") {
            System.out.println(error_report);
            System.out.println("Error Count: " + error_count);
            result = -1;
        } else {
            System.out.println("Success: HalfLatchEdifCell: All tests passed");
            result = 0;
        }
        return result;
    }

    private static boolean testHasSafeNet(EdifCell cell, String safeConstantNetName) {
        boolean safeNetFound = false;
        for (Iterator k = cell.getNetList().iterator(); k.hasNext();) {
            EdifNet net = (EdifNet) k.next();
            if (net.getName().compareToIgnoreCase(safeConstantNetName) == 0) {
                safeNetFound = true;
            }
        }
        return safeNetFound;
    }

    private static boolean testHasSafePort(EdifCell cell, String safeConstantPortName) {
        boolean safePortFound = false;
        for (Iterator l = cell.getPortList().iterator(); l.hasNext();) {
            EdifPort port = (EdifPort) l.next();
            if (port.getName().compareToIgnoreCase(safeConstantPortName) == 0) {
                safePortFound = true;
            }
        }
        return safePortFound;
    }

    public static void main(String[] args) {
        HalfLatchFlattenedEdifCell.test(args[0], args[1]);
    }

}
