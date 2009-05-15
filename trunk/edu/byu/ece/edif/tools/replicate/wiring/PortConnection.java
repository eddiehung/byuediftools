package edu.byu.ece.edif.tools.replicate.wiring;

import java.io.Serializable;

import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;

public abstract class PortConnection implements Serializable {

    public PortConnection() {
        this(null);
    }
    
    public PortConnection(EdifNameable name) {
        _name = name;
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
    
    /**
     * Associate a name with the PortConnection.
     * 
     * @param name
     */
    public void setName(EdifNameable name) {
        _name = name;
    }
    
    protected EdifNameable _name;
    
}
