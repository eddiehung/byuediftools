package edu.byu.ece.edif.tools.replicate.nmr.dwc;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.List;

import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.tools.replicate.nmr.AbstractReplicationType;
import edu.byu.ece.edif.tools.replicate.nmr.OrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;

/**
 * The DWC replication type performs duplication. It doesn't of itself
 * insert detectors. That must be accomplished using an appropriate
 * DetectionType. This class only takes care of the actual replication.
 */
public class DWCReplicationType extends AbstractReplicationType {

    protected static String[] DWC_SUFFIXES = {"_DWC_0", "_DWC_1"};
    
    protected static DWCReplicationType _instance;
    
    private DWCReplicationType() {
        super(2, "duplication");
    }
    
    public static DWCReplicationType getInstance() {
    	if (_instance == null) {
    		_instance = new DWCReplicationType();
    	}
    	return _instance;
    }
    
    /**
     * Create a new String to be added to the end of a triplicated element that
     * incorporates the NMR domain number.
     * 
     * @param domain The domain number
     * @return the String with the replication suffix and domain number.
     */
    protected String getReplicationSuffix(int domain) {
        return DWC_SUFFIXES[domain];
    }

	public List<OrganSpecification> antiRestore(EdifNet net,
			ReplicationDescription desc) {

		// duplication doesn't use restoring organs 
		return null;
	}

	public List<OrganSpecification> defaultRestore(EdifNet net,
			ReplicationDescription desc) {

		// duplication doesn't use restoring organs 
		return null;
	}

	public List<OrganSpecification> forceRestore(EdifNet net,
			List<EdifPortRef> forceRestoreRefs, ReplicationDescription desc) {

		// duplication doesn't use restoring organs 
		return null;
	}
	
	public List<OrganSpecification> forceRestore(EdifNet net, List<EdifPortRef> forceRestoreRefs, Collection<OrganSpecification> prevSpecs, ReplicationDescription desc) {
		
		// duplication doesn't use restoring organs
		return null;
	}
	
	/**
	 * This method ensures that during deserialization, the _instance variable will be referred
	 * to and possibly modified so that extra instances (more than necessary) don't get created.
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		DWCReplicationType type = _instance;
		if (type == null) {
			type = getInstance();
		}
		return type;
	}
}
