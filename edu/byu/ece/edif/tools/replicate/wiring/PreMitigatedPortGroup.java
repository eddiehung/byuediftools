package edu.byu.ece.edif.tools.replicate.wiring;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationType;

public class PreMitigatedPortGroup implements Serializable {

    public PreMitigatedPortGroup(List<EdifPort> ports, ReplicationType replicationType) {
        _ports = ports;
        _replicationType = replicationType;
    }

    public List<EdifPort> getPorts() {
        return _ports;
    }

    public ReplicationType getReplicationType() {
        return _replicationType;
    }
    
    public EdifPort getFirstPort() {
        return _ports.iterator().next();
    }

    /**
     * Get the ports other than the first one.
     */
    public Collection<EdifPort> getOtherPorts() {
        List<EdifPort> result = new ArrayList<EdifPort>(2);
        Iterator<EdifPort> it = _ports.iterator();
        EdifPort port = null;
        if (it.hasNext())
            port = it.next();
        while (it.hasNext()) {
            port = it.next();
            result.add(port);
        }
        return result;
    }
    
    public String toString() {
        return "Port Group: " + _replicationType + ", " + _ports;
    }
    
    protected List<EdifPort> _ports;
    protected ReplicationType _replicationType;

}

