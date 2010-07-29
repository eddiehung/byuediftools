package edu.byu.ece.edif.util.graph;

import java.util.Comparator;

import edu.byu.ece.edif.core.EdifNet;

/**
 * A comparator that compares the fan out of EdifNet objects
 */
public class NetFanOutComparator implements Comparator<EdifNet> {	
	
	public int compare(EdifNet net1, EdifNet net2) {
		if (net1.getInputPortRefs().size() > net2.getInputPortRefs().size())
			return 1;
		if (net1.getInputPortRefs().size() < net2.getInputPortRefs().size())
			return -1;
		return 0;
	}
	
	public boolean equals(Object o) {
		if (o == this)
			return true;
		return false;
	}
	
}
