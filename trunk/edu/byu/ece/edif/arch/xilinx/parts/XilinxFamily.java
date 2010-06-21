package edu.byu.ece.edif.arch.xilinx.parts;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a List of Xilinx devices in that family. Each family
 * (Virtex, Virtex-4, etc.) has a singleton instance which
 * extends this class. Right now, a static List containing references
 * to each XilinxFamily is contained in this class; this may not be
 * the best approach.
 */
public abstract class XilinxFamily {
	protected XilinxFamily(String familyName, String partNamePrefix) {
		_familyName = familyName;
		_partNamePrefix = partNamePrefix;
		_devices = new ArrayList<XilinxDevice>();
		_families.add(this);
		addDevices();
	}
	
	public String getFamilyName() {
		return _familyName;
	}
	
	public String getPartNamePrefix() {
		return _partNamePrefix;
	}

	public List<XilinxDevice> getDevices() {
		return new ArrayList<XilinxDevice>(_devices);
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	protected void addDevice(XilinxDevice device) {
		_devices.add(device);
	}
	
	public static List<XilinxFamily> getAllFamilies() {
		return _families;
	}
		
	//has a reference to all instances of XilinxFamily (Virtex, V2Pro, V4...)
	protected static List<XilinxFamily> _families = new ArrayList<XilinxFamily>();
	
	//abstract methods
	protected abstract void addDevices();

	protected ArrayList<XilinxDevice> _devices;
	protected String _familyName;
	protected String _partNamePrefix;
}

