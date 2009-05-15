package edu.byu.ece.edif.tools.replicate.nmr.xilinx;

import java.io.ObjectStreamException;
import java.util.List;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.core.StringTypedValue;
import edu.byu.ece.edif.tools.replicate.nmr.AbstractOrgan;
import edu.byu.ece.edif.tools.replicate.nmr.OrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationType;
import edu.byu.ece.edif.tools.replicate.wiring.NetManager;
import edu.byu.ece.edif.tools.replicate.wiring.PortConnection;

/**
 * This class represents a majority voter represented as a LUT3 with
 * init string "E8".
 */
public class XilinxTMRVoter extends AbstractOrgan {

    protected static String[] VOTER_INPUT_NAMES = {"I0", "I1", "I2"};
    
    /**
     * Make sure the only way to get an instance of the class
     * is to use the static getInstance() method. (This is a 
     * singleton class).
     */
    protected XilinxTMRVoter() {
       super("TMR_VOTER");
    }
    
    /**
     * Create the voter's specified by the given OrganSpecification for the given net in the
     * given EdifCell.
     */
    public void createOrgan(OrganSpecification organSpec, EdifNet origNet, EdifCell newCell) {
        EdifCell voterCell = getLUT3VoterCell(newCell);
        int numOrgans = organSpec.getOrganCount();     
        createOrgan(organSpec, voterCell, numOrgans, origNet, newCell, "O", new Property("init", new StringTypedValue("E8")));
    }
    
    /**
     * Create a LUT3 implementation of a voter cell if it has not been defined
     * yet. Add it to the library to which the parent cell belongs. If the
     * voter cell has already been built, a reference to that cell is simply
     * returned.
     * 
     * @param parent The parent cell from which all libraries will be extracted.
     * @return A voter cell implemented as a LUT3.
     */
    protected EdifCell getLUT3VoterCell(EdifCell parent) {

        return getOrganCell(parent, "LUT3");
    }
    
    /**
     * Wire the voter's inputs to the given driver connections using the given net manager. Also,
     * verify that this kind of voter can be used with the given replication type.
     */
    public void wireInputs(OrganSpecification organSpecification, EdifNet origNet, List<PortConnection> driverConnections, NetManager netManager, ReplicationType replicationType) {
        
        // error checking
        if (_organCell == null)
            throw new EdifRuntimeException("Error: trying to wire voter inputs before creating a voter");

        wireInputs(organSpecification, origNet, driverConnections, netManager, VOTER_INPUT_NAMES);
    }
    
    /**
     * Get the singleton instance of the XilinxTMRVoter class.
     * 
     * @return
     */
    public static XilinxTMRVoter getInstance() {
        if (_instance == null)
            _instance = new XilinxTMRVoter();
        return _instance;
    }
    
	/**
	 * This method ensures that during deserialization, the _instance variable will be referred
	 * to and possibly modified so that extra instances (more than necessary) don't get created.
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		XilinxTMRVoter instance = _instance;
		if (instance == null) {
			instance = getInstance();
		}
		return instance;
	}
    
    protected static XilinxTMRVoter _instance = null;

}
