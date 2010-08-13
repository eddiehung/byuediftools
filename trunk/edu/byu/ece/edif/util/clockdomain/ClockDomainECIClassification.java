package edu.byu.ece.edif.util.clockdomain;

import java.util.Map;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;

public class ClockDomainECIClassification { 
    public ClockDomainECIClassification(Set<EdifCellInstance> noClockECIs, 
            Map<EdifNet, Set<EdifCellInstance>> clkToECIMap) {
        _noClockECIs = noClockECIs;
        _clkToECIMap = clkToECIMap;
    }
    
    public Set<EdifCellInstance> getNoClockECIs() {
        return _noClockECIs;
    }
 
    public Map<EdifNet, Set<EdifCellInstance>> getClkToECIMap() {
        return _clkToECIMap;
    }
    
    private Set<EdifCellInstance> _noClockECIs;
    private Map<EdifNet, Set<EdifCellInstance>> _clkToECIMap;

}
