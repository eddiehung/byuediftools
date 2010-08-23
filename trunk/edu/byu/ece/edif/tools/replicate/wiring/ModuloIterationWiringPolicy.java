package edu.byu.ece.edif.tools.replicate.wiring;

import java.io.ObjectStreamException;
import java.util.Iterator;
import java.util.List;

import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifRuntimeException;

/**
 * The modulo-iteration wiring policy wires sinks and sources together by iterating
 * sinks and sources simultaneously but recycling through the sources if there are less
 * sources than sinks.
 */
public class ModuloIterationWiringPolicy implements WiringPolicy {

    /**
     * Ensure that the only way to get an instance of the class is through
     * the static method getInstance().
     */
    private ModuloIterationWiringPolicy() {
        
    }
    
    /**
     * Connect the given sources and sinks with a modulo-iteration wiring policy. The source and sinks are
     * iterated simultaneously. If the sources run out before the sinks, the iteration of sources cycles back
     * to the beginning of the list.
     */
    public List<EdifNet> connectSourcesToSinks(List<PortConnection> sources, List<? extends PortConnection> sinks, NetManager netManager) {
    	if (sources.size() < 1 || sinks.size() < 1)
            throw new EdifRuntimeException("Unexpected while in ModuloIterationWiringPolicy.connectSourcesToSinks: sources.size() = " + sources.size() + ", sinks.size() = " + sinks.size() + ".");
        
        Iterator<PortConnection> sourceIt = sources.iterator();
        Iterator<? extends PortConnection> sinkIt = sinks.iterator();
        List<EdifNet> nets = null;
        while (sinkIt.hasNext()) {
            PortConnection source = null;
            if (!sourceIt.hasNext())
                sourceIt = sources.iterator();
            source = sourceIt.next();
            PortConnection sink = sinkIt.next();
            EdifNet n = netManager.wirePortConnections(source, sink);            
            nets.add(n);
        }
        return nets;
    }
    
    /**
     * Get (create if needed) the singleton instance of the ModuloIterationWiringPolicy
     * class.
     * 
     * @return
     */
    public static ModuloIterationWiringPolicy getInstance() {
        if (_instance == null)
            _instance = new ModuloIterationWiringPolicy();
        return _instance;
    }
    
	/**
	 * This method ensures that during deserialization, the _instance variable will be referred
	 * to and possibly modified so that extra instances (more than necessary) don't get created.
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		ModuloIterationWiringPolicy instance = _instance;
		if (instance == null) {
			instance = getInstance();
		}
		return instance;
	}
    
    /**
     * Singleton instance of the class
     */
    protected static ModuloIterationWiringPolicy _instance = null;

}
