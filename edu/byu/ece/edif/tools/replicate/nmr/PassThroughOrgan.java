package edu.byu.ece.edif.tools.replicate.nmr;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.tools.replicate.wiring.NetManager;
import edu.byu.ece.edif.tools.replicate.wiring.PortConnection;

/**
 * This class implements a simple pass-through detection organ
 * When used properly, this will allow visibility into a specific net
 * via a top-level port
 */
public class PassThroughOrgan implements Organ {
	
	static protected Map<OrganSpecification,List<PortConnection>> _driverConnectionsMap = new LinkedHashMap<OrganSpecification,List<PortConnection>>();
    
    public void createOrgan(OrganSpecification organSpec, EdifNet origNet, EdifCell newCell) {
    	// No need to do anything here for this class
    }
    
    public void wireInputs(OrganSpecification organSpecification, EdifNet origNet, List<PortConnection> driverConnections, NetManager netManager, ReplicationType replicationType) {
    	// Save out relevant items
    	_driverConnectionsMap.put(organSpecification, new ArrayList<PortConnection>(driverConnections));
    }
    
    //List<PortConnection> getOutputs(EdifNet origNet);
    
    public List<PortConnection> getOutputs(OrganSpecification os) {
    	// Just return the list of driver connections since this organ is just
    	// for pass-through
    	return _driverConnectionsMap.get(os);
    }
    
    public String getOrganSuffix() {
    	return "PASSTHROUGH";
    }
    
	public String toString() {
		return getClass().getSimpleName();
	}
    
    /**
     * Get the singleton instance of the PassThroughOrgan class.
     * 
     * @return
     */
    public static PassThroughOrgan getInstance() {
        if (_instance == null)
            _instance = new PassThroughOrgan();
        return _instance;
    }
    
	/**
	 * This method ensures that during deserialization, the _instance variable will be referred
	 * to and possibly modified so that extra instances (more than necessary) don't get created.
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		PassThroughOrgan instance = _instance;
		if (instance == null) {
			instance = getInstance();
		}
		return instance;
	}
    
    protected static PassThroughOrgan _instance = null;

}
