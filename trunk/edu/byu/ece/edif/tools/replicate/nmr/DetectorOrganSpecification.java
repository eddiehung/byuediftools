package edu.byu.ece.edif.tools.replicate.nmr;

import java.util.List;

import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;

/**
 * A DetectorOrganSpecification holds information about where to put a detector organ
 * (i.e. a comparator).
 */
public class DetectorOrganSpecification extends AbstractOrganSpecification {

	public DetectorOrganSpecification(Organ organType, int organCount, EdifNet net) {
		super(organType, organCount, net);
	}

	public List<EdifPortRef> getSinksGettingOrganOutputs() {
		return null;
	}
	
	public String toString() {
		return "Organ type: " + _organType + ", Organ count: " + _organCount;
	}

}
