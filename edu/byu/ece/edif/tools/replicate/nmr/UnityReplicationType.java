package edu.byu.ece.edif.tools.replicate.nmr;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.List;

import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;

/**
 * This class represents a one to one copy (not really a replication). This is
 * used when partial replication is used and some design portions don't need to
 * get replicated.
 */
public class UnityReplicationType extends AbstractReplicationType {

	private static UnityReplicationType _instance = null;
	
    /**
     * Make sure the constructor is private so the only way to get an instance is
     * to get the singleton instance from the getInstance() method.
     */
    private UnityReplicationType() {
        super(1, "unity");
    }

    
    /**
     * Get the singleton instance of the UnityReplicationType class.
     * 
     * @return
     */
    public static UnityReplicationType getInstance() {
        if (_instance == null)
            _instance = new UnityReplicationType();
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
        return "";
    }

	public List<OrganSpecification> antiRestore(EdifNet net,
			ReplicationDescription desc) {

		// unity replication does not use restoring organs
		return null;
	}


	public List<OrganSpecification> defaultRestore(EdifNet net,
			ReplicationDescription desc) {
		
		// unity replication does not use restoring organs
		return null;
	}


	public List<OrganSpecification> forceRestore(EdifNet net,
			List<EdifPortRef> forceRestoreRefs, ReplicationDescription desc) {
		
		// unity replication does not use restoring organs
		return null;
	}


	public List<OrganSpecification> forceRestore(EdifNet net,
			List<EdifPortRef> forceRestoreRefs,
			Collection<OrganSpecification> prevSpecs, ReplicationDescription desc) {

		// unity replication does not use restoring organs
		return null;
	}
	
	/**
	 * This method ensures that during deserialization, the _instance variable will be referred
	 * to and possibly modified so that extra instances (more than necessary) don't get created.
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		UnityReplicationType instance = _instance;
		if (instance == null) {
			instance = getInstance();
		}
		return instance;
	}
	

}
