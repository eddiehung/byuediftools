package edu.byu.ece.edif.tools.replicate.wiring;

import java.util.List;

/**
 * An interface to specify the wiring policy between sources/sinks when the replication type differs.
 * 
 * The only WiringPolicy currently available is the ModuloIterationWiringPolicy.
 */
public interface WiringPolicy {
    
    public void connectSourcesToSinks(List<PortConnection> sources, List<? extends PortConnection> sinks, NetManager netManager);
    
}
