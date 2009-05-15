package edu.byu.ece.edif.tools.replicate.nmr.tmr;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.tools.replicate.nmr.AbstractReplicationType;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.Organ;
import edu.byu.ece.edif.tools.replicate.nmr.OrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationType;
import edu.byu.ece.edif.tools.replicate.nmr.RestoringOrganSpecification;

/**
 * The TMRReplicationType class is used to perform triplication. It also handles
 * signal restoration (voter) issues.
 */
public class TMRReplicationType extends AbstractReplicationType {

    protected static String[] TMR_SUFFIXES = {"_TMR_0", "_TMR_1", "_TMR_2"};
    
    /*
     * This class is kind of like a singleton class, except that there is
     * a single instance of it for each type of voter to be used with it.
     */
    protected static Map<Organ, TMRReplicationType> _instances = new LinkedHashMap<Organ, TMRReplicationType>();
    
    
    protected TMRReplicationType(Organ tmrVoterType) {
        super(3, "triplication");
        _tmrVoterType = tmrVoterType;
    }
    
    public static TMRReplicationType getInstance(Organ tmrVoterType) {
    	TMRReplicationType replicationType = _instances.get(tmrVoterType);
    	if (replicationType == null) {
    		replicationType = new TMRReplicationType(tmrVoterType);
    		_instances.put(tmrVoterType, replicationType);
    	}
    	return replicationType;
    }
    
    public static TMRReplicationType getInstance(NMRArchitecture arch) {
    	Organ voterType = arch.getDefaultRestoringOrganForReplicationType(TMRReplicationType.class);
    	TMRReplicationType replicationType = _instances.get(voterType);
    	if (replicationType == null) {
    		replicationType = new TMRReplicationType(voterType);
    		_instances.put(voterType, replicationType);
    	}
    	return replicationType;
    }
    
    /**
     * This method doesn't do anything for this particular class. It is part of the interface
     * in case any replication types need to know about locations where the user has specified
     * not to converge the domains.
     * 
     * @param net
     * @param desc
     * @return
     */
    public List<OrganSpecification> antiRestore(EdifNet net, ReplicationDescription desc) {
        return null;
    }
    
    /**
     * Force the insertion of voter(s) at this location. The number of voters to insert will be
     * determined by the replication factor(s) of the sink(s). If forceRestoreRefs is <code>null</code>
     * or if it contains of the the drivers, then all sinks will get voter outputs. Otherwise, only
     * sinks contained in forceRestoreRefs will get voter outputs.
     * 
     * @param net
     * @param forceRestoreRefs
     * @param desc
     * @return
     */
    public List<OrganSpecification> forceRestore(EdifNet net, List<EdifPortRef> forceRestoreRefs, ReplicationDescription desc) {
    	return forceRestore(net, forceRestoreRefs, null, desc);
    	
    }

    /**
     * Force the insertion of voter(s) at this location. The number of voters to insert will be
     * determined by the replication factor(s) of the sink(s). If forceRestoreRefs is <code>null</code>
     * or if it contains of the the drivers, then all sinks will get voter outputs. Otherwise, only
     * sinks contained in forceRestoreRefs will get voter outputs.
     * 
     * @param net
     * @param forceRestoreRefs
     * @param desc
     * @return
     */
    public List<OrganSpecification> forceRestore(EdifNet net, List<EdifPortRef> forceRestoreRefs, Collection<OrganSpecification> prevSpecs, ReplicationDescription desc) {
        
    	// first check to see if any of the prevSpecs can be reused (if prevSpecs is not null)
    	RestoringOrganSpecification osToReuse = null;
    	if (prevSpecs != null) {
    		for (OrganSpecification os : prevSpecs) {
    			if ((os instanceof RestoringOrganSpecification) && os.getOrganType() == _tmrVoterType) {
    				osToReuse = (RestoringOrganSpecification) os;
    				break;
    			}
    		}
    	}
    	
        Collection<EdifPortRef> origDrivers = net.getSourcePortRefs(true, true);
        Collection<EdifPortRef> origSinks = net.getSinkPortRefs(false, true);
        
        boolean forceAllSinksVoterOutputs = false;
        if (forceRestoreRefs == null)
            forceAllSinksVoterOutputs = true;
        else {
            for (EdifPortRef driver : origDrivers) {
                if (forceRestoreRefs.contains(driver))
                    forceAllSinksVoterOutputs = true;
            }
        }
        
        int numVoters = 0;
        List<EdifPortRef> sinksGettingVoterOutputs = new ArrayList<EdifPortRef>();
        
        for (EdifPortRef sink : origSinks) {
            ReplicationType sinkRepType = desc.getReplicationType(sink);
            int sinkFactor = sinkRepType.getReplicationFactor();
            if (numVoters < sinkFactor)
                numVoters = sinkFactor;
            if (forceAllSinksVoterOutputs || (forceRestoreRefs != null && forceRestoreRefs.contains(sink)))
                sinksGettingVoterOutputs.add(sink);
        }
        
        List<OrganSpecification> result = null;
        
        if (osToReuse == null) {
        	if (numVoters > 0) {
        		result = new ArrayList<OrganSpecification>();
        		result.add(new RestoringOrganSpecification(_tmrVoterType, numVoters, net, sinksGettingVoterOutputs));
        	}
        }
        else {
        	osToReuse.promoteOrganCountUpTo(numVoters);
        	osToReuse.addSinksGettingVoterOutputs(sinksGettingVoterOutputs);
        }
        return result;
    }
    
    /**
     * Determine the voters that need to be inserted based on upscaling/downscaling.
     * 
     * @param net
     * @param desc
     * @return
     */
    public List<OrganSpecification> defaultRestore(EdifNet net, ReplicationDescription desc) {
    	Collection<EdifPortRef> origSinks = net.getSinkPortRefs(false, true);

        List <EdifPortRef> sinksGettingVoterOutputs = new ArrayList<EdifPortRef>();

        int numVoters = 0;
        for (EdifPortRef sink : origSinks) {
            ReplicationType sinkRepType = desc.getReplicationType(sink);
            int sinkFactor = sinkRepType.getReplicationFactor();
            
            /*
             * If we are downscaling, we only need voters if the option to use voters
             * for downscaling is specified
             */
            if (sinkFactor < 3) { // downscale
                if (_useVoterForDownscale) {
                    if (numVoters < sinkFactor)
                        numVoters = sinkFactor;
                    sinksGettingVoterOutputs.add(sink);
                }
            }

            /*
             * If we are upscaling, we only need voters if the option to use voters
             * for upscaling is specified
             */
            else if (sinkFactor > 3) { // upscale
                if (_useVoterForUpscale) {
                    if (numVoters < sinkFactor)
                        numVoters = sinkFactor;
                    sinksGettingVoterOutputs.add(sink);
                }
            }
        }
        
        List<OrganSpecification> result = null;
        if (numVoters > 0) {
            result = new ArrayList<OrganSpecification>();
            result.add(new RestoringOrganSpecification(_tmrVoterType, numVoters, net, sinksGettingVoterOutputs));
        }
        return result;
    }
        
    /**
     * Create a new String to be added to the end of a triplicated element that
     * incorporates the NMR domain number.
     * 
     * @param domain The domain number
     * @return the String with the replication suffix and domain number.
     */
    protected String getReplicationSuffix(int domain) {
        return TMR_SUFFIXES[domain];
    }

    /**
     * Set whether or not to use voter outputs for non-cut sinks when there are enough
     * voters being created anyways for other sinks.
     * 
     * @param useVoterOutputsForNonCutSinks
     */
    public void setUseVoterOutputsForNonCutSinks(boolean useVoterOutputsForNonCutSinks) {
        _useVoterOutputsForNonCutSinks = useVoterOutputsForNonCutSinks;
    }
    
    /**
     * Set whether to use voters for downscaling regardless of whether there is a
     * feedback cut.
     * 
     * @param useVoterForDownscale
     */
    public void setUseVoterForDownScale(boolean useVoterForDownscale) {
        _useVoterForDownscale = useVoterForDownscale;
    }
    
    /**
     * Set whether to use voters for upscaling regardless of whether there is a
     * feedback cut.
     * 
     * @param useVoterForUpscale
     */
    public void setUseVoterForUpscale(boolean useVoterForUpscale) {
        _useVoterForUpscale = useVoterForUpscale;
    }
    
	/**
	 * This method ensures that during deserialization, the _instances map will be referred
	 * to and possibly modified so that extra instances (more than necessary) don't get created.
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		TMRReplicationType instance = _instances.get(_tmrVoterType);
		if (instance == null) {
			instance = getInstance(_tmrVoterType);
		}
		return instance;
	}
    
    protected boolean _useVoterOutputsForNonCutSinks = true;
    protected boolean _useVoterForDownscale = true;
    protected boolean _useVoterForUpscale = false;
    
    protected Organ _tmrVoterType;
}
