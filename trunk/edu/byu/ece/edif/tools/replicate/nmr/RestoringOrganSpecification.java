package edu.byu.ece.edif.tools.replicate.nmr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;

/**
 * A RestoringOrganSpecification represents where to insert a restoring organ
 * (i.e. a voter), how many to insert, and which net sinks should get the
 * organs' outputs instead of the pre-organ outputs.
 */
public class RestoringOrganSpecification extends AbstractOrganSpecification {

	public RestoringOrganSpecification(Organ organType, int organCount, EdifNet net, List<EdifPortRef> sinksGettingOrganOutputs) {
        super(organType, organCount, net);
        if (sinksGettingOrganOutputs == null)
            _sinksGettingOrganOutputs = new ArrayList<EdifPortRef>(0);
        else
            _sinksGettingOrganOutputs = sinksGettingOrganOutputs;
    }
	
    /**
     * Get the list of sinks that need to get organ outputs.
     * 
     * @return
     */
    public List<EdifPortRef> getSinksGettingOrganOutputs() {
        return _sinksGettingOrganOutputs;
    }
	
	public String toString() {
		String result = "Organ type: " + _organType + ", Organ count: " + _organCount + ", sinks getting organ outputs: ";
		
		Iterator<EdifPortRef> it = _sinksGettingOrganOutputs.iterator();
		while (it.hasNext()) {
			EdifPortRef ref = it.next();
			result += ref;
			if (it.hasNext())
				result += ", ";
		}
		
		return result;
	}
	
	public void addSinksGettingVoterOutputs(Collection<EdifPortRef> sinksGettingVoterOutputs) {
		for (EdifPortRef ref : sinksGettingVoterOutputs) {
			if (!_sinksGettingOrganOutputs.contains(ref))
				_sinksGettingOrganOutputs.add(ref);
		}
	}
    
    protected List<EdifPortRef> _sinksGettingOrganOutputs;
	
}
