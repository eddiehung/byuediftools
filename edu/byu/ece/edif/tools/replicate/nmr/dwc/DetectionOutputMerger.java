package edu.byu.ece.edif.tools.replicate.nmr.dwc;

import java.io.Serializable;
import java.util.List;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.tools.replicate.wiring.NetManager;
import edu.byu.ece.edif.tools.replicate.wiring.PortConnection;

/**
 * A DetectionOutputMerger provides a method for merging the outputs from
 * a group of detectors.
 */
public abstract class DetectionOutputMerger implements Serializable {
    
    /**
     * Merge the outputs in the given list. (Each List of PortConnections is a single output).
     * @param outputs
     * @param topCell
     * @param netManager
     * @return
     */
	public abstract List<PortConnection> mergeOutputs(List<List<PortConnection>> outputs, EdifCell topCell, NetManager netManager);
	
    protected DetectionType _detectionType;
    
    public boolean equals(Object obj) {
    	return (this.getClass() == obj.getClass());
    }
    
}
