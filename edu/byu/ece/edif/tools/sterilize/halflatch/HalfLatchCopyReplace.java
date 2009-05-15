package edu.byu.ece.edif.tools.sterilize.halflatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.tools.sterilize.lutreplace.AbstractEdifEnvironmentCopyReplace;
import edu.byu.ece.edif.tools.sterilize.lutreplace.ReplacementContext;
import edu.byu.ece.edif.util.iob.IOBAnalyzer;

public class HalfLatchCopyReplace extends AbstractEdifEnvironmentCopyReplace {

    public HalfLatchCopyReplace(EdifEnvironment env, HalfLatchArchitecture hlArchitecture, IOBAnalyzer iobAnalyzer, int safeConstantPolarity, boolean usePortForConstant) throws EdifNameConflictException {
        this(env, hlArchitecture, iobAnalyzer, safeConstantPolarity, usePortForConstant, null);
    }
    
    public HalfLatchCopyReplace(EdifEnvironment env, HalfLatchArchitecture hlArchitecture, IOBAnalyzer iobAnalyzer, int safeConstantPolarity, boolean usePortForConstant, String safeConstantPortName) throws EdifNameConflictException {
        super(env);
        _hlArchitecture = hlArchitecture;
        _badCutPins = new ArrayList<EdifPortRef>();
        if (safeConstantPortName != null) {
            _safeConstantOnePortName = safeConstantPortName;
            _safeConstantZeroPortName = safeConstantPortName;
        }
        _safeConstantPolarity = safeConstantPolarity;
        
        createEdifEnvironment();
        replaceSensitiveInstances();
        
        if (usePortForConstant) {
            bufferSafeConstantPort();
        }
        else {
            addSafeConstantGeneratorCell();
        }
    }
    
    /**
     * Overrides the parent addChildEdifCellInstance method. This method
     * will check to see if the instance is of a type that needs to be 
     * replaced. If so, it will not add the instance and create a
     * ReplacementContext for this instance. If it doesn't match, the
     * instance will be added as usual.
     */
    protected void addChildEdifCellInstance(EdifCell origCell, EdifCell newCell, EdifCellInstance oldChildInstance) throws EdifNameConflictException {
        // Check to see if the instance needs to be replaced
        boolean isPrimitive = oldChildInstance.getCellType().isPrimitive();
        boolean isSensitive = _hlArchitecture.cellRequiresReplacement(oldChildInstance);
        boolean isConstant = _hlArchitecture.isConstantCell(oldChildInstance.getType());
        if (isSensitive || isConstant) {
            HalfLatchReplacementContext rc = new HalfLatchReplacementContext(newCell, oldChildInstance, isSensitive);
            _oldInstancesToReplace.put(oldChildInstance, rc);
        }
        else { 
            // Doesn't match: allow the parent "copy" method to continue
            super.addChildEdifCellInstance(origCell, newCell, oldChildInstance);
        }
    }
    
    protected void addNets(EdifCell origCell, EdifCell newCell) throws EdifNameConflictException {
        super.addNets(origCell, newCell);
        
        // some of the inputs to DSP48 and RAMB16 cells use special routing *must* be connected
        // to a GND primitive if not otherwise used. They won't really connect to a GND primitive,
        // this is just how the Xilinx tools mark them as unused. Connecting these pins to a safe
        // constant port or instance wouldn't be right, so a special GND instance is created just
        // for them since all the others will have been replaced by a safe constant generator.
        if (_badCutPins.size() > 0) {
            EdifCell newTopCell = _newEnv.getTopCell();
            EdifNameable gndNetName = newTopCell.getUniqueNetNameable(NamedObject.createValidEdifNameable("GNDZ0"));
            EdifNet gndNet = new EdifNet(gndNetName, newTopCell);
            EdifLibraryManager elm = _newEnv.getLibraryManager();
            EdifCell gnd = XilinxLibrary.findOrAddXilinxPrimitive(elm, "GND");
            EdifCellInstance gndInstance = null;
            EdifNameable gndName = newTopCell.getUniqueInstanceNameable(NamedObject.createValidEdifNameable("new_gnd"));
            gndInstance = new EdifCellInstance(gndName, newTopCell, gnd);
            EdifPortRef gndEpr = new EdifPortRef(gndNet, gnd.getPort("G").getSingleBitPort(0), gndInstance);
            gndNet.addPortConnection(gndEpr);

            for (EdifPortRef epr : _badCutPins) {
                EdifCellInstance newEci = _instanceMap.get(epr.getCellInstance());
                EdifSingleBitPort newEsbp = newEci.getCellType().getPort(epr.getPort().getName()).getSingleBitPort(epr.getBusMember());
                EdifPortRef newEpr = new EdifPortRef(gndNet, newEsbp, newEci);
                gndNet.addPortConnection(newEpr);
            }
            newTopCell.addNet(gndNet);
            newTopCell.addSubCell(gndInstance);            
        }
    }

    protected EdifNet addNet(EdifCell origCell, EdifCell newCell, EdifNet oldNet) throws EdifNameConflictException {
        
        boolean hasConstantConnection = false;
        int constPolarity = 0;
        for (EdifPortRef epr : oldNet.getConnectedPortRefs()) {
            EdifCellInstance instance = epr.getCellInstance();
            if (instance != null) {
                if (_hlArchitecture.isConstantCell(instance.getType())) {
                    hasConstantConnection = true;
                    constPolarity = _hlArchitecture.getConstantCellValue(instance.getType());
                    break;
                }
            }
        }
        
        if (hasConstantConnection) {
            EdifNet newNet = null;
            // All sinks of this Net should now be driven by the Safe Constant Net
            if (constPolarity == _safeConstantPolarity)
                newNet = findOrAddSafeConstantNetNormPolarity();
            else
                newNet = findOrAddSafeConstantNetInvPolarity();
            _netMap.put(oldNet, newNet);

            // iterate portRefs
            for (EdifPortRef oldRef : oldNet.getConnectedPortRefs()) {
                if (_hlArchitecture.isBadCutPin(oldRef.getPort())) {
                    _badCutPins.add(oldRef);
                }
                else {
                    // skip original constant connections
                    if (!_hlArchitecture.isConstantCell(oldRef.getCellInstance().getType()))
                        addEdifPortRef(newNet, oldRef);
                }
            }

            return newNet;
        }
        else
            return super.addNet(origCell, newCell, oldNet);
    }
    
    protected void replaceSensitiveInstances() {
        for (ReplacementContext rc : getReplacementContexts()) {
            HalfLatchReplacementContext hlrc = (HalfLatchReplacementContext) rc;
            if (hlrc.isSensitive()) {
                replaceSensitiveInstance(hlrc);
            }
        }
    }
    
    protected void replaceSensitiveInstance(HalfLatchReplacementContext hlrc) {
        EdifLibraryManager elm = _newEnv.getLibraryManager();
        EdifCellInstance oldInstance = hlrc.getOldInstanceToReplace();
        EdifCell newParent = hlrc.getNewParentCell();
        String replacementType = _hlArchitecture.getPrimitiveReplacementType(oldInstance);
        EdifCell newCellType = _hlArchitecture.findOrAddPrimitiveReplacementCell(elm, replacementType);
        EdifNameable newName = newParent.getUniqueInstanceNameable(oldInstance.getEdifNameable());
        EdifCellInstance newInstance = new EdifCellInstance(newName, newParent, newCellType);
        
        // connect original nets
        Collection<EdifSingleBitPort> oldSBPorts = hlrc.getOldSBPorts();
        for (EdifSingleBitPort esbp : oldSBPorts) {
            EdifNet newNet = hlrc.getNewNetToConnect(esbp);
            EdifPort newPort = newCellType.getPort(esbp.getPortName());
            EdifSingleBitPort newEsbp = newPort.getSingleBitPort(esbp.bitPosition());
            EdifPortRef epr = new EdifPortRef(newNet, newEsbp, newInstance);
            newNet.addPortConnection(epr);
        }
        
        // connect floating nets
        String[] unconnectedPorts = _hlArchitecture.getPrimitiveReplacementFloatingPorts(oldInstance);
        for (int i = 0; i < unconnectedPorts.length; i++) {
            // Get the actual port represented by this String
            EdifPort newPort = newCellType.getPort(unconnectedPorts[i]);
            EdifSingleBitPort esbp = newPort.getSingleBitPort(0);
            // Get the driving value for this port
            int defaultPortValue = _hlArchitecture.getPrimitiveReplacementFloatingPortDefaultValue(oldInstance, unconnectedPorts[i]);
            // Create a new port ref connecting the 'safe constant' net to the unconnected port of the new 'safe' primitive 
            EdifNet safeConstantNet = findOrAddSafeConstantNet(defaultPortValue);
            EdifPortRef epr = new EdifPortRef(safeConstantNet, esbp, newInstance);
            safeConstantNet.addPortConnection(epr);
        }
        
        // add instance to top cell
        newParent.addSubCellUniqueName(newInstance);        
    }
        
    public EdifNet findOrAddSafeConstantNet(int polarity) {
        EdifNet result;
        if (polarity == 0 && _safeConstantPolarity == 0 || polarity == 1 && _safeConstantPolarity == 1)
            result = findOrAddSafeConstantNetNormPolarity();
        else
            result = findOrAddSafeConstantNetInvPolarity();
        return result;
    }
    
    /**
     * If the safe constant inverter and or the safe constant net with inverted
     * polarity for 'this' cell have not already been created and added, then
     * this method call will first create and add the inverter and net (attached
     * to the output of the inverter) to 'this' cell before returning a
     * reference to the inverted polarity net.
     */
    protected EdifNet findOrAddSafeConstantNetInvPolarity() {
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
    protected EdifNet findOrAddSafeConstantNetNormPolarity() {
        EdifNet result;
        if (_safeConstantNetNorm == null)
            result = addSafeConstantNetNormPolarity();
        else
            result = _safeConstantNetNorm;
        return result;
    }
    
    /**
     * If the safe constant net with *normal* polarity for 'this' cell has not
     * already been created and added, then this method call will first create
     * and add the net to 'this' cell. By default, this method will create a net
     * which is attached to the output of the inverter this method is supposed
     * to create.
     */
    protected void addSafeConstantInverter() {
        if (_safeConstantInverterInstance == null) {
            EdifLibraryManager elm = _newEnv.getLibraryManager();
            EdifCell newTopCell = _newEnv.getTopCell();
            // create an inverter
            EdifCell inverterCell = _hlArchitecture.findOrAddPrimitiveInverterCell(elm);
            try {
                _safeConstantInverterInstance = new EdifCellInstance("HL_INV", newTopCell, inverterCell);
                newTopCell.addSubCell(_safeConstantInverterInstance);
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
    protected EdifNet addSafeConstantNetInvPolarity() {
        if (_safeConstantNetInv == null) {
            EdifCell newTopCell = _newEnv.getTopCell();
            String netName = null;
            if (_safeConstantPolarity == 0)
                netName = _safeConstantOneNetName;
            else
                netName = _safeConstantZeroNetName;
            try {
                _safeConstantNetInv = new EdifNet(netName);
                newTopCell.addNet(_safeConstantNetInv);
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
    protected EdifNet addSafeConstantNetNormPolarity() {
        if (_safeConstantNetNorm == null) {
            EdifCell newTopCell = _newEnv.getTopCell();
            String netName = null;
            if (_safeConstantPolarity == 0)
                netName = _safeConstantZeroNetName;
            else
                netName = _safeConstantOneNetName;
            try {
                _safeConstantNetNorm = new EdifNet(netName);
                newTopCell.addNet(_safeConstantNetNorm);
            } catch (InvalidEdifNameException e) {
                e.toRuntime();
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }

        }
        return _safeConstantNetNorm;
    }
    
    protected EdifNet findOrAddSafeConstantNet() {
        return findOrAddSafeConstantNetNormPolarity();
    }

    protected void bufferSafeConstantPort() {
        
        if (_inputBufferCellInstance == null) {

            // Add an ibuf cell instance
            EdifLibraryManager elm = _newEnv.getLibraryManager();
            EdifCell newTopCell = _newEnv.getTopCell();
            EdifCell inputBufferCell = _hlArchitecture.findOrAddPrimitiveInputBufferCell(elm);
            EdifNameable bufferName = newTopCell.getUniqueInstanceNameable(NamedObject.createValidEdifNameable("safeConstantInputBuffer"));
            _inputBufferCellInstance = new EdifCellInstance(bufferName, newTopCell, inputBufferCell);
            try {
                newTopCell.addSubCell(_inputBufferCellInstance);
            } catch (EdifNameConflictException e) {
                // can't get here, the name is unique already
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
            EdifNameable netName_i = newTopCell.getUniqueNetNameable(NamedObject.createValidEdifNameable(constantPortToBufferNetName));
            EdifNet constantPortToBufferNet = new EdifNet(netName_i, newTopCell);
            try {
                newTopCell.addNet(constantPortToBufferNet);
            } catch (EdifNameConflictException e) {
                // can't get here, the name is unique already
                e.toRuntime();
            }

            // get a reference to the top-level 'safe constant' port of 'this' cell
            EdifSingleBitPort topLevelSafePort = findOrAddSafeConstantPort().getSingleBitPort(0);
            // make a port ref object to connect the port to the net
            EdifPortRef constantPortToBufferNetEPR = new EdifPortRef(constantPortToBufferNet, topLevelSafePort, null);
            // add the port ref to the net
            constantPortToBufferNet.addPortConnection(constantPortToBufferNetEPR);
            // get a reference to the input port of the new input buffer cell
            EdifSingleBitPort inputBufferInputPort = inputBufferCell.getPort(_hlArchitecture.getPrimitiveInputBufferCellInputPortName()).getSingleBitPort(0);
            // make a port ref object to connect the port to the net
            EdifPortRef bufferNetToBufferEPR = new EdifPortRef(constantPortToBufferNet, inputBufferInputPort, _inputBufferCellInstance);
            // add the port ref to the net
            constantPortToBufferNet.addPortConnection(bufferNetToBufferEPR);

            // Connect the ibuf to the internal 'safe constant' net
            // get a reference to the output port of the new input buffer cell
            EdifSingleBitPort inputBufferOutputPort = inputBufferCell.getPort(_hlArchitecture.getPrimitiveInputBufferOutputBufferName()).getSingleBitPort(0);
            EdifNet safeConstantNet = findOrAddSafeConstantNet();
            // make a port ref object to connect the port to the net
            
            EdifPortRef safeConstantEPR = new EdifPortRef(safeConstantNet, inputBufferOutputPort, _inputBufferCellInstance);
            // add the port ref to the net
            safeConstantNet.addPortConnection(safeConstantEPR);
        }
    }
    
    protected EdifPort findOrAddSafeConstantPort() {
        EdifPort result;
        if (_safeConstantPort == null)
            result = addSafeConstantPort();
        else
            result = _safeConstantPort;
        return result;
    }
    
    protected EdifPort addSafeConstantPort() {
        EdifCell newTopCell = _newEnv.getTopCell();
        String portName = null;
        if (_safeConstantPolarity == 0)
            portName = _safeConstantZeroPortName;
        else
            portName = _safeConstantOnePortName;
        EdifPort existingPort = newTopCell.getPort(portName);
        if (existingPort != null) {
            _safeConstantPort = existingPort;
        }
        else {
            EdifNameable portNameable = NamedObject.createValidEdifNameable(portName);
            try {
                _safeConstantPort = newTopCell.addPort(portNameable, 1, EdifPort.IN);
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }
        }
        return _safeConstantPort;
    }
    
    protected void addSafeConstantGeneratorCell() {
        EdifCell newTopCell = _newEnv.getTopCell();
        if (_safeConstantGeneratorCell == null) {
            // Add a 'safe constant' cell to the top cell we just found with the given polarity
            _safeConstantGeneratorCell = _hlArchitecture.addConstantCellInstance(newTopCell, _safeConstantPolarity);

            // Now add a connection from the 'safe constant' cell we just created to the 'safe constant' net
            String safeConstantCellOutputPortName = _hlArchitecture.getSafeConstantCellOutputPortName();
            EdifSingleBitPort safeConstantCellOutputPort = _safeConstantGeneratorCell.getCellType().getPort(safeConstantCellOutputPortName).getSingleBitPort(0);
            EdifPortRef new_epr = new EdifPortRef(findOrAddSafeConstantNet(), safeConstantCellOutputPort, _safeConstantGeneratorCell);
            findOrAddSafeConstantNet().addPortConnection(new_epr);
        }
    }
    
    public EdifCellInstance getSafeConstantPortBufferInstance() {
        return _inputBufferCellInstance;
    }
    
    public EdifCellInstance getSafeConstantGeneratorCell() {
        return _safeConstantGeneratorCell;
    }
    
    protected HalfLatchArchitecture _hlArchitecture;
    protected IOBAnalyzer _iobAnalyzer;
    protected int safeConstantPolarity;
    
    protected List<EdifPortRef> _badCutPins;
    
    public static String _safeConstantZeroNetName = "safeConstantNet_zero";
    public static String _safeConstantOneNetName = "safeConstantNet_one";
    public static String _safeConstantZeroPortName = "safeConstantPort_zero";
    public static String _safeConstantOnePortName = "safeConstantPort_one";
    protected int _safeConstantPolarity;
    protected EdifNet _safeConstantNetNorm = null;
    protected EdifNet _safeConstantNetInv = null;
    protected EdifCellInstance _safeConstantInverterInstance;
    protected EdifCellInstance _safeConstantGeneratorCell;
    protected EdifCellInstance _inputBufferCellInstance;
    protected EdifPort _safeConstantPort;
}
