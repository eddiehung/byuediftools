package edu.byu.ece.edif.tools.replicate.nmr;

import edu.byu.ece.edif.tools.replicate.nmr.dwc.DWCReplicationType;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.TMRReplicationType;

/**
 * This class recognizes string representations of replication types and returns
 * singleton instances of the appropriate classes.
 */
public class ReplicationTypeMapper {

	public enum ReplicationTypes {
		DUPLICATION, TRIPLICATION
	}
	
	public static ReplicationType getReplicationType(String name, NMRArchitecture arch) {
		ReplicationType result = null;
		if (name.compareToIgnoreCase(ReplicationTypes.DUPLICATION.name()) == 0) {
			result = DWCReplicationType.getInstance();
		}
		else if (name.compareToIgnoreCase(ReplicationTypes.TRIPLICATION.name()) == 0) {
			result = TMRReplicationType.getInstance(arch);
		}
		return result;
	}
}
