package edu.byu.ece.edif.tools.replicate.nmr.xilinx;

import java.io.ObjectStreamException;
import java.util.List;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.tools.replicate.nmr.AbstractOrgan;
import edu.byu.ece.edif.tools.replicate.nmr.OrganSpecification;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationType;
import edu.byu.ece.edif.tools.replicate.wiring.NetManager;
import edu.byu.ece.edif.tools.replicate.wiring.PortConnection;

/**
 * This class implements a single XOR2 comparator. Dual-rail comparison can be
 * accomplished by creating an OrganSpecification with an organCount of 2 instead
 * of 1.
 */
public class XilinxDWCComparator extends AbstractOrgan {

	protected static XilinxDWCComparator _instance;
	protected static String[] COMPARATOR_INPUT_NAMES = {"I0", "I1"};
	
	protected XilinxDWCComparator() {
		super("DWC_COMPARATOR");
	}
	
	public static XilinxDWCComparator getInstance() {
		if (_instance == null) {
			_instance = new XilinxDWCComparator();
		}
		return _instance;
	}
	
	public void createOrgan(OrganSpecification organSpec, EdifNet origNet, EdifCell newCell) {
		EdifCell comparator = getXOR2ComparatorCell(newCell);
		int numOrgans = organSpec.getOrganCount();
		createOrgan(organSpec, comparator, numOrgans, origNet, newCell, "O", null);		
	}

	public void wireInputs(OrganSpecification organSpecification, EdifNet origNet, List<PortConnection> driverConnections, NetManager netManager, ReplicationType replicationType) {
		
		// error checking
        if (_organCell == null)
            throw new EdifRuntimeException("Error: trying to wire voter inputs before creating a voter");

        wireInputs(organSpecification, origNet, driverConnections, netManager, COMPARATOR_INPUT_NAMES);		
	}
	
	protected EdifCell getXOR2ComparatorCell(EdifCell parent) {
		return getOrganCell(parent, "XOR2");
	}
	
	/**
	 * This method ensures that during deserialization, the _instance variable will be referred
	 * to and possibly modified so that extra instances (more than necessary) don't get created.
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		XilinxDWCComparator instance = _instance;
		if (instance == null) {
			instance = getInstance();
		}
		return instance;
	}
	
}
