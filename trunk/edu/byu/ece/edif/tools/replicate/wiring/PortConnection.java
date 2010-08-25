package edu.byu.ece.edif.tools.replicate.wiring;

import java.io.Serializable;

import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;

/**
 * A container for a single port connection or multiple port connections.
 */
@SuppressWarnings("serial")
public abstract class PortConnection implements Serializable {

    public PortConnection(EdifNameable name, EdifNet origNet) {
        _name = name;
        _oldNet = origNet;
    }
    
    /**
     * Wire the connection to the given net.
     * 
     * @param net
     */
    public abstract void connectToNet(EdifNet net);
    
    /**
     * Get the name associated with the PortConnection or <code>null</code> if there is no name
     * 
     * @return
     */
    public EdifNameable getName() {
        return _name;
    }
    
    public EdifNet getOldNet() {
    	return _oldNet;
    }
    
    /**
     * Associate a name with the PortConnection.
     * 
     * @param name
     */
    public void setName(EdifNameable name) {
        _name = name;
    }
    
    protected EdifNameable _name = null;
    
    protected EdifNet _oldNet = null;
    
}
