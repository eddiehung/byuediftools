package edu.byu.ece.edif.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifSingleBitPort;

/**
 * A comparator that compares the fan out of EdifNet objects
 */
public class EdifCellInstanceGraphFanOutComparator implements Comparator {	
	
	public int compare(Object eci1, Object eci2) {
		int net1FanOut = getEdifCellInstanceFanout(eci1);
		int net2FanOut = getEdifCellInstanceFanout(eci2);
		
		if (net1FanOut > net2FanOut)
			return -1;
		if (net1FanOut < net2FanOut)
			return 1;
		return 0;
	}
	
	public boolean equals(Object o) {
		if (o == this)
			return true;
		return false;
	}
	
	public static int getEdifCellInstanceFanout(Object eci) {
		int fanOut = 0;
		Collection<EdifNet> outputNets = null;
		if (eci instanceof EdifCellInstance) {
			outputNets = ((EdifCellInstance) eci).getOuterNets().values();
		} else {
			outputNets = new ArrayList<EdifNet>(1);
			outputNets.add (((EdifSingleBitPort) eci).getInnerNet());
		}
		for (EdifNet n : outputNets) {
			fanOut += n.getConnectedPortRefs().size();
		}
		return fanOut;
	}
	
}
