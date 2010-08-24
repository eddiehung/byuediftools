package edu.byu.ece.edif.tools.replicate.nmr.xilinx;

import java.io.ObjectStreamException;
import java.util.List;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.core.StringTypedValue;
import edu.byu.ece.edif.tools.replicate.nmr.AbstractOrgan;
import edu.byu.ece.edif.tools.replicate.nmr.OrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationType;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.TMRReplicationType;
import edu.byu.ece.edif.tools.replicate.wiring.NetManager;
import edu.byu.ece.edif.tools.replicate.wiring.PortConnection;

/**
 * This class represents a dual-rail TMR comparator implemented as a LUT3 with
 * init string "7E".
 */
public class XilinxTMRComparator extends AbstractOrgan {

    protected static String[] COMPARATOR_INPUT_NAMES = {"I0", "I1", "I2"};
    protected static XilinxTMRComparator _instance = null;
    
    protected XilinxTMRComparator() {
        super("TMR_COMPARATOR");
    }

    /**
     * Create a dual-rail TMR comparator.
     * 
     */
    public void createOrgan(OrganSpecification organSpec, EdifNet origNet, EdifCell newCell) {
        EdifCell voterCell = getLUT3ComparatorCell(newCell);
        int numOrgans = organSpec.getOrganCount();     
        createOrgan(organSpec, voterCell, numOrgans, origNet, newCell, "O", new Property("init", new StringTypedValue("7E")));
    }

    /**
     * Wire the inputs of a previously created dual-rail TMR comparator.
     * 
     */
    public List<EdifNet> wireInputs(OrganSpecification organSpecification, EdifNet origNet, List<PortConnection> driverConnections, NetManager netManager, ReplicationType replicationType) {
        // error checking
        if (_organCell == null)
            throw new EdifRuntimeException("Error: trying to wire comparator inputs before creating a comparator");
        if (!(replicationType instanceof TMRReplicationType))
            throw new EdifRuntimeException("Error: trying to use a TMR comparator with an incompatible replication type");

        return wireInputs(organSpecification, origNet, driverConnections, netManager, COMPARATOR_INPUT_NAMES);
    }

    /**
     * Create a LUT3 implementation of a comparator cell if it has not been defined
     * yet. Add it to the library to which the parent cell belongs. If the
     * comparator cell has already been added, a reference to that cell is simply
     * returned.
     * 
     * @param parent The parent cell from which all libraries will be extracted.
     * @return A comparator cell implemented as a LUT3.
     */
    protected EdifCell getLUT3ComparatorCell(EdifCell parent) {
        return getOrganCell(parent, "LUT3");
    }
    
    /**
     * Get the singleton instance of the XilinxTMRComparator class.
     * 
     * @return
     */
    public static XilinxTMRComparator getInstance() {
        if (_instance == null)
            _instance = new XilinxTMRComparator();
        return _instance;
    }
    
	/**
	 * This method ensures that during deserialization, the _instance variable will be referred
	 * to and possibly modified so that extra instances (more than necessary) don't get created.
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		XilinxTMRComparator instance = _instance;
		if (instance == null) {
			instance = getInstance();
		}
		return instance;
	}
}
