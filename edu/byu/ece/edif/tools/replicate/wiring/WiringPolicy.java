package edu.byu.ece.edif.tools.replicate.wiring;

import java.util.List;

import edu.byu.ece.edif.core.EdifNet;

/**
 * An interface to specify the wiring policy between sources/sinks when the replication type differs.
 * 
 * The only WiringPolicy currently available is the ModuloIterationWiringPolicy.
 */
public interface WiringPolicy {
    
    public List<EdifNet> connectSourcesToSinks(List<PortConnection> sources, List<? extends PortConnection> sinks, NetManager netManager);
    
}
