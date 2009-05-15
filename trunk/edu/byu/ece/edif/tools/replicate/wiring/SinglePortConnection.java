package edu.byu.ece.edif.tools.replicate.wiring;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;

public class SinglePortConnection extends PortConnection {

    public SinglePortConnection(EdifSingleBitPort esbp, EdifCellInstance instance, EdifNameable name) {
        super(name);
        _singleBitPort = esbp;
        _instance = instance;
    }
    
    public SinglePortConnection(EdifSingleBitPort esbp, EdifCellInstance instance) {
        this(esbp, instance, null);
    }
    
    public SinglePortConnection(EdifPortRef epr, EdifNameable name) {
        this(epr.getSingleBitPort(), epr.getCellInstance(), name);
    }
    
    public SinglePortConnection(EdifPortRef epr) {
        this(epr, null);
    }
    
    public void connectToNet(EdifNet net) {
        net.addPortConnection(new EdifPortRef(net, _singleBitPort, _instance));
    }
    
    boolean isTopLevelConnection() {
        return _instance == null;
    }
    
    public EdifSingleBitPort getSingleBitPort() {
        return _singleBitPort;
    }
    
    public EdifCellInstance getInstance() {
        return _instance;
    }
    
    public boolean equals(Object o) {
        if (o instanceof SinglePortConnection) {
            SinglePortConnection other = (SinglePortConnection) o;
            if (other._singleBitPort == _singleBitPort && other._instance == _instance)
                return true;
        }
        return false;
    }
    
    public String toString() {
    	return _instance + ", " + _singleBitPort;
    }
    
    protected EdifSingleBitPort _singleBitPort;
    
    protected EdifCellInstance _instance;
    
}