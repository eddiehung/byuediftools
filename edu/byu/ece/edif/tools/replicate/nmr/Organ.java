package edu.byu.ece.edif.tools.replicate.nmr;

import java.io.Serializable;
import java.util.List;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.tools.replicate.wiring.NetManager;
import edu.byu.ece.edif.tools.replicate.wiring.PortConnection;

/**
 * In the BYU EDIF Replication framework, there are two main types of organs:
 * restoring organs and detection organs. Voters are an example of restoring
 * organs. Restoring organs take a signal from some number of domains and
 * produce and output that feeds forward in the circuit. Detecting organs take
 * a signal from some number of domains and produce an auxiliary output that
 * goes somewhere else in the circuit (i.e. detection merging circuitry).
 */
public interface Organ extends Serializable {
    
    void createOrgan(OrganSpecification organSpec, EdifNet origNet, EdifCell newCell);
    
    List<EdifNet> wireInputs(OrganSpecification organSpecification, EdifNet origNet, List<PortConnection> driverConnections, NetManager netManager, ReplicationType replicationType);
    
    //List<PortConnection> getOutputs(EdifNet origNet);
    
    List<PortConnection> getOutputs(OrganSpecification os);
    
    String getOrganSuffix();
    
}
