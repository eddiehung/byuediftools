package edu.byu.ece.edif.util.clockdomain;

import java.util.Map;
import java.util.Set;

import edu.byu.ece.edif.core.EdifNet;

public class ClockDomainEdifNetClassification {
    public ClockDomainEdifNetClassification(Set<EdifNet> noClockNets,
            Set<EdifNet> clockNets, Map<EdifNet, Set<EdifNet>> clkToNetMap) {
        _noClockNets = noClockNets;
        _clockNets = clockNets;
        _clkToNetMap = clkToNetMap;
    }
    
    public Set<EdifNet> getNoClockNets() {
        return _noClockNets;
    }
    
    public Set<EdifNet> getClockNets() {
        return _clockNets;
    }
    
    public Map<EdifNet, Set<EdifNet>> getClkToNetMap() {
        return _clkToNetMap;
    }
    
    private Set<EdifNet> _noClockNets;
    private Set<EdifNet> _clockNets;
    private Map<EdifNet, Set<EdifNet>> _clkToNetMap;
}
