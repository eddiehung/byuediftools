package edu.byu.ece.edif.tools.replicate.nmr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.tools.replicate.wiring.MultiPortConnection;
import edu.byu.ece.edif.tools.replicate.wiring.NetManager;
import edu.byu.ece.edif.tools.replicate.wiring.PortConnection;
import edu.byu.ece.edif.tools.replicate.wiring.SinglePortConnection;

/**
 * In the BYU EDIF Replication framework, there are two main types of organs:
 * restoring organs and detection organs. Voters are an example of restoring
 * organs. Restoring organs take a signal from some number of domains and
 * produce and output that feeds forward in the circuit. Detecting organs take
 * a signal from some number of domains and produce an auxiliary output that
 * goes somewhere else in the circuit (i.e. detection merging circuitry).
 */
public abstract class AbstractOrgan implements Organ {

    /**
     * Initialize the data structures needed for keeping track of
     * created organs and their output port connections.
     */
    protected AbstractOrgan(String organSuffix) {
        _organOutputs = new LinkedHashMap<EdifCellInstance, PortConnection>();
        _createdOrgans = new LinkedHashMap<OrganSpecification, List<EdifCellInstance>>();
        _organSuffix = organSuffix;
    }

    /**
     * Create <code>numOrgans</code> organs of type <code>organCell</code> in <code>newCell</code> . The organs will
     * be associated with <code>origNet</code>. If an <code>organOutputName</code> is given (not <code>null</code>, it
     * the port with the given name will be inserted into the <code>_organOutputs</code> map. If an
     * <code>instanceProperty</code> is given (not null), the property will be associated with each instance of the organ
     * created. All of the created organs will be inserted into the <code>_createdOrgans</code> map.
     * 
     * @param organCell
     * @param numOrgans
     * @param origNet
     * @param newCell
     * @param organOutputName
     * @param instanceProperty
     */
    protected void createOrgan(OrganSpecification os, EdifCell organCell, int numOrgans, EdifNet origNet, EdifCell newCell, String organOutputName, Property instanceProperty) {
        List<EdifCellInstance> organList = new ArrayList<EdifCellInstance>(numOrgans);
        _createdOrgans.put(os, organList);
        for (int i = 0; i < numOrgans; i++) {
            EdifNameable organName = NamedObject.createValidEdifNameable(origNet.getName() + "_" + getOrganSuffix() + "_" + i);
            EdifNameable uniqueVoterName = newCell.getUniqueInstanceNameable(organName);
            EdifCellInstance organ = new EdifCellInstance(uniqueVoterName, newCell, organCell);
            if (instanceProperty != null)
                organ.addProperty(instanceProperty);
            if (organOutputName != null) {
                SinglePortConnection organOutput = new SinglePortConnection(organCell.getPort(organOutputName).getSingleBitPort(0), organ);
                organOutput.setName(organName);
                _organOutputs.put(organ, organOutput);
            }
            organList.add(organ);
            try {
                newCell.addSubCell(organ);
            } catch (EdifNameConflictException e) {
                // won't get here because we created a unique name
                e.toRuntime();
            }
        }
    }

    /**
     * Wire the organ inputs. <code>origNet</code> is used to lookup the organ(s) associated with the net.
     * The organ input names are given by <code>organInputNames</code>. The connections in
     * <code>driverConnections</code> are wired to the organs using <code>netManager</code>.
     * 
     * @param origNet
     * @param driverConnections
     * @param netManager
     * @param organInputNames
     */
    protected List<EdifNet> wireInputs(OrganSpecification os, EdifNet origNet, List<PortConnection> driverConnections, NetManager netManager, String[] organInputNames) {
        int numOrganInputs = organInputNames.length;
        List<EdifNet> nets = new ArrayList<EdifNet>();
        
        // initialize voter input MultiPortConnections
        List<PortConnection> organInputs = new ArrayList<PortConnection>(numOrganInputs);
        for (int i = 0; i < numOrganInputs; i++) {
            organInputs.add(new MultiPortConnection());
        }

        for (EdifCellInstance organInstance : _createdOrgans.get(os)) {
            Iterator<PortConnection> organInputIt = organInputs.iterator();
            int i = 0;
            while (organInputIt.hasNext()) {
                MultiPortConnection organInput = (MultiPortConnection) organInputIt.next();
                String organInputName = organInputNames[i];
                organInput.addConnection(organInstance, _organCell.getPort(organInputName).getSingleBitPort(0));
                i++;
            }
        }

        // wire connections
        Iterator<PortConnection> driverIt = driverConnections.iterator();
        Iterator<PortConnection> organInputIt = organInputs.iterator();
        while (driverIt.hasNext() && organInputIt.hasNext()) {
            PortConnection driver = driverIt.next();
            PortConnection organInput = organInputIt.next();
            EdifNet wc = netManager.wirePortConnections(driver, organInput);
            nets.add(wc);
        }
        return nets;
    }

    /**
     * Get the organ cell in the design's library (insert it into the library if needed). The primitive
     * cell type (i.e. LUT3 for a 3-input voter) is given by <code>primitiveName</code>. The cell
     * will be inserted into the library associated with <code>parent</code> if not in one of the
     * design's libraries already.
     * 
     * @param parent
     * @param primitiveName
     * @return
     */
    protected EdifCell getOrganCell(EdifCell parent, String primitiveName) {
        if (_organCell != null)
            return _organCell;

        // Step #1 - Get Xilinx primitive
        EdifLibrary xilinxLibrary = edu.byu.ece.edif.arch.xilinx.XilinxLibrary.library;
        EdifCell voter = xilinxLibrary.getCell(primitiveName);

        // Step #2 - Search for the Xilinx primitive in library manager
        Collection<EdifCell> matchingVoterCells = parent.getLibrary().getLibraryManager().getCells(primitiveName);

        // Iterate over all cells to see if the primitive exists
        if (matchingVoterCells != null) {
            // Iterate over the matching cells and see if it exists
            for (EdifCell cell : matchingVoterCells) {
                if (cell.equalsInterface(voter)) { // The LUT exists - tag it as the votercell
                    _organCell = cell;
                    return _organCell;
                }
            }
        }

        // Step #3 - The primitive does not exist in our library. Add it.
        EdifLibrary lib = parent.getLibrary().getLibraryManager().getFirstPrimitiveLibrary();
        if (lib == null)
            lib = parent.getLibrary();

        try {
            lib.addCell(voter);
        } catch (EdifNameConflictException e) {
            // shouldn't get here -- we are only adding the cell because we couldn't find it
            // (unless there is another LUT3 cell with the wrong interface...)
            e.toRuntime();
        }
        _organCell = voter;

        return _organCell;
    }

    /**
     * Get the organ outputs associated with the organs created for the given
     * net (<code>origNet</code>).
     */
    public List<PortConnection> getOutputs(OrganSpecification os) {
        List<PortConnection> outputs = new ArrayList<PortConnection>();
        List<EdifCellInstance> organs = _createdOrgans.get(os);
        for (EdifCellInstance organ : organs) {
            outputs.add(_organOutputs.get(organ));
        }
        return outputs;
    }

    
    public String getOrganSuffix() {
        return _organSuffix;
    }
    
	public String toString() {
		return getClass().getSimpleName();
	}
    
    protected EdifCell _organCell;
    protected String _organSuffix;
    protected Map<EdifCellInstance, PortConnection> _organOutputs;
    protected Map<OrganSpecification, List<EdifCellInstance>> _createdOrgans;

}
