package edu.byu.ece.edif.tools.replicate.nmr.dwc;

import java.io.Serializable;
import java.util.List;

import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.tools.replicate.nmr.OrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;

/**
 * A DetectionType indicates how to perform detection on specified signals
 * and what the detection signal width is.
 */
public interface DetectionType extends Serializable {

    /**
     * @return the detection type's signal width
     */
    public int getSignalWidth();
    
    /**
     * Indicate to the detection type that a detector should not be placed on the given net.
     * 
     * @param net
     * @param desc
     * @return
     */
    public List<OrganSpecification> antiDetect(EdifNet net, ReplicationDescription desc);

    /**
     * Force detection on the given net
     * @param net
     * @param desc
     * @return
     */
    public List<OrganSpecification> forceDetect(EdifNet net, ReplicationDescription desc);

    /**
     * Provide default detection on the given net. Default detection is detection that
     * occurs as a result of upscaling/downscaling.
     * 
     * @param net
     * @param desc
     * @return
     */
    public List<OrganSpecification> defaultDetect(EdifNet net, ReplicationDescription desc);    
 
    /**
     * Indicate whether default detections should include downscaling.
     * @param useComparatorForDownscale
     */
	public void setUseComparatorForDownscale(boolean useComparatorForDownscale);
	
	/**
	 * Indicate whether default detections should include upscaling.
	 * @param useComparatorForUpscale
	 */
	public void setUseComparatorForUpscale(boolean useComparatorForUpscale);
	
}
