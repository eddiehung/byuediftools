package edu.byu.ece.edif.tools.replicate.nmr.dwc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.tools.replicate.nmr.DetectorOrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.Organ;
import edu.byu.ece.edif.tools.replicate.nmr.OrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationType;

/**
 * Parent class for single- and dual-rail detection types.
 */
public abstract class RailedDetectionType extends AbstractDetectionType {

	protected RailedDetectionType(Organ detectorOrgan, int railFactor) {
		super(railFactor);
		_detectorOrgan = detectorOrgan;
		_railFactor = railFactor;
		_useComparatorForDownscale = true;
		_useComparatorForUpscale = true;
	}

	public List<OrganSpecification> antiDetect(EdifNet net,
			ReplicationDescription desc) {

		// this method doesn't need to do anything for this class.
		// It's only here in case a DetectionType needs to
		// know where comparators are NOT being inserted
		return null;
	}

	public List<OrganSpecification> defaultDetect(EdifNet net, ReplicationDescription desc) {

		Collection<EdifPortRef> origDrivers = net.getSourcePortRefs(true, true);
		Collection<EdifPortRef> origSinks = net.getSinkPortRefs(false, true);
		
		ReplicationType replicationType = null;
        for (EdifPortRef driver : origDrivers) {
            ReplicationType driverType = desc.getReplicationType(driver);
            if (replicationType == null)
                replicationType = driverType;
            else {
                if (replicationType != driverType) {
                    throw new EdifRuntimeException("Unexpected: Net drivers have different ReplicationTypes");
                }
            }
        }
        int driverFactor = replicationType.getReplicationFactor();
		
        boolean needsComparator = false;
        
        for (EdifPortRef sink : origSinks) {
            ReplicationType sinkRepType = desc.getReplicationType(sink);
            int sinkFactor = sinkRepType.getReplicationFactor();

            if (sinkFactor < driverFactor) { // downscale
                if (_useComparatorForDownscale) {
                    needsComparator = true;
                }
            }

            else if (sinkFactor > driverFactor) { // upscale
                if (_useComparatorForUpscale) {
                    needsComparator = true;
                }
            }
        }
        
        List<OrganSpecification> result = null;
		if (needsComparator) {
			result = new ArrayList<OrganSpecification>(1);
			result.add(new DetectorOrganSpecification(_detectorOrgan, _railFactor, net));
		}
		return result;
		
	}

	public List<OrganSpecification> forceDetect(EdifNet net, ReplicationDescription desc) {
		List<OrganSpecification> result = new ArrayList<OrganSpecification>(1);
		result.add(new DetectorOrganSpecification(_detectorOrgan, _railFactor, net));
		return result;
	}

	protected Organ _detectorOrgan;

	protected int _railFactor;

}
