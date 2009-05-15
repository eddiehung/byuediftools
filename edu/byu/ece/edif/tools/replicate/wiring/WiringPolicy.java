package edu.byu.ece.edif.tools.replicate.wiring;

import java.util.List;

public interface WiringPolicy {
    
    public void connectSourcesToSinks(List<PortConnection> sources, List<? extends PortConnection> sinks, NetManager netManager);
    
}
