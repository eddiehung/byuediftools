package edu.byu.ece.edif.tools.replicate.nmr;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;

/**
 * An organ specification is an indication of where to put a particular kind of
 * organ during replication, how many to place, and what to do with the outputs.
 * OrganSpecifications come in two main types: RestoringOrganSpecifications and
 * DetectorOrganSpecifications.
 */
public interface OrganSpecification extends Serializable {

    /**
     * @return the type of organ this specification refers to 
     */
	public Organ getOrganType();
	
	/**
	 * @return the number of organs to place on the net indicated by
	 * this specification
	 */
    public int getOrganCount();
    
    /**
     * @return a list of portRefs indicating net sinks that should
     * get the organ's outputs instead of the pre-organ drivers
     */
    public List<EdifPortRef> getSinksGettingOrganOutputs();
    
    /**
     * Increase the number of organs in the specification up to
     * organCount
     */
    public void promoteOrganCountUpTo(int organCount);
    
    /**
     * @return the net which this specification refers to
     */
    public EdifNet getEdifNet();
    
}
