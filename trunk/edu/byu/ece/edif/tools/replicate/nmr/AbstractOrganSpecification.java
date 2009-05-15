package edu.byu.ece.edif.tools.replicate.nmr;

import java.util.List;

import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;

/**
 * An organ specification is an indication of where to put a particular kind of
 * organ during replication, how many to place, and what to do with the outputs.
 * OrganSpecifications come in two main types: RestoringOrganSpecifications and
 * DetectorOrganSpecifications.
 */
public abstract class AbstractOrganSpecification implements OrganSpecification {

	/**
     * Create a new OrganSpecification with the given organ type, number of organs, and net.
     * 
     * @param organType
     * @param organCount
     * @param net
     */
    public AbstractOrganSpecification(Organ organType, int organCount, EdifNet net) {
        _organType = organType;
        _organCount = organCount;
        _net = net;
    }
    
    /**
     * Get the type of organ specified.
     * 
     * @return
     */
    public Organ getOrganType() {
        return _organType;
    }
    
    /**
     * Get the number of organs specified.
     * 
     * @return
     */
    public int getOrganCount() {
        return _organCount;
    }
    
    /**
     * Get the list of sinks that need to get organ outputs.
     * 
     * @return
     */
    public abstract List<EdifPortRef> getSinksGettingOrganOutputs();
    
    /**
     * Get the orginal EdifNet associated with the organ specification
     */
    public EdifNet getEdifNet() {
    	return _net;
    }
    
    public void promoteOrganCountUpTo(int organCount) {
    	if (_organCount < organCount)
    		_organCount = organCount;
    }
    
    protected EdifNet _net;
    protected Organ _organType;
    protected int _organCount;
	
}
