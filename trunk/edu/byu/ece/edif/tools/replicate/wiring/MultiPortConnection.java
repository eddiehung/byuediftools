package edu.byu.ece.edif.tools.replicate.wiring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
 * A list of net connections without the net along with a method to perform the net connections once the
 * net has been created.
 */
@SuppressWarnings("serial")
public class MultiPortConnection extends PortConnection {

    public MultiPortConnection() {
        this((EdifNameable) null);
    }
    
    public MultiPortConnection(EdifNameable name) {
        super(name);
        _singleBitPorts = new ArrayList<EdifSingleBitPort>();
        _instances = new ArrayList<EdifCellInstance>();
    }
    
    public MultiPortConnection(Collection<SinglePortConnection> connections, EdifNameable name) {
        this(name);
        for (SinglePortConnection spc : connections) {
            _singleBitPorts.add(spc.getSingleBitPort());
            _instances.add(spc.getInstance());
        }
    }
    
    public MultiPortConnection(Collection<SinglePortConnection> connections) {
        this(connections, null);
    }
    
    public void addConnection(EdifCellInstance instance, EdifSingleBitPort singleBitPort) {
        _singleBitPorts.add(singleBitPort);
        _instances.add(instance);
    }
    
    public void connectToNet(EdifNet net) {
        Iterator<EdifSingleBitPort> esbpIt = _singleBitPorts.iterator();
        Iterator<EdifCellInstance> instanceIt = _instances.iterator();
        while (esbpIt.hasNext() && instanceIt.hasNext()) {
            net.addPortConnection(new EdifPortRef(net, esbpIt.next(), instanceIt.next()));
        }
    }

    protected List<EdifSingleBitPort> _singleBitPorts;
    protected List<EdifCellInstance> _instances;
    
}
