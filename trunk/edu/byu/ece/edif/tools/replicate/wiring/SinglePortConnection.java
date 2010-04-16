package edu.byu.ece.edif.tools.replicate.wiring;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;

/**
 * A container for an EdifCellInstance and a EdifPortRef. The EdifCellInstance can be null in which
 * case this is a top-level port connection. This is a lot like the EdifPortRef. This class is created
 * to help manage the connections before the net has been created. 
 * 
 */
@SuppressWarnings("serial")
public class SinglePortConnection extends PortConnection {

	/**
	 * Atomic class. All elements set at construction.
	 */
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
    
    /**
     * Uses the information in this class to actually make the connection. This is done
     * after the container elements have been populated.
     */
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