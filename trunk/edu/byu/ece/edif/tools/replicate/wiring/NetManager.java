package edu.byu.ece.edif.tools.replicate.wiring;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.NamedObject;

/**
 * The NetManager class wires PortConnections together. It is also responsible for
 * creating new nets when there is no existing net already associated with a source
 * PortConnection. When creating new nets, the name associated with the source
 * PortConnection will be used for the net name. If there is no name associated with
 * a source PortConnection, a uniquified default net name will be used.
 */
public class NetManager {
    
    public NetManager(EdifCell topCell) {
        _topCell = topCell;
        _connectionNets = new LinkedHashMap<PortConnection, EdifNet>();
    }
    
    /**
     * Connect the given source and sink PortConnections, creating a new net
     * only if there isn't one already created for the given source.
     * 
     * @param source
     * @param sink
     */
    public void wirePortConnections(PortConnection source, PortConnection sink) {
        EdifNet net = getNet(source);
        sink.connectToNet(net);
    }
    
    /**
     * Get the net associated with source PortConnection. If there is no associated net,
     * create one and return it.
     * 
     * @param source
     * @return
     */
    public EdifNet getNet(PortConnection source) {
    	EdifNet net = _connectionNets.get(source);
    	if (net == null) {
    		EdifNameable name = source.getName();
    		if (name == null)
    			name = NamedObject.createValidEdifNameable("net");
    		EdifNameable uniqueName = _topCell.getUniqueNetNameable(name);
    		net = new EdifNet(uniqueName);
    		try {
    			_topCell.addNet(net);
    		} catch (EdifNameConflictException e) {
    			// can't get here because of the call to getUniqueNetNameable
    			e.toRuntime();
    		}
    		source.connectToNet(net);
    		_connectionNets.put(source, net);
    	}
    	return net;
    }
    
    public EdifCell getTopCell() {
    	return _topCell;
    }

    /**
     * The EdifCell where the nets are being created
     */
    EdifCell _topCell;    
    
    /**
     * Keep track of which source PortConnections already have nets
     * created for them
     */
    protected Map<PortConnection, EdifNet> _connectionNets;

}
