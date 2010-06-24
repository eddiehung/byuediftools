package edu.byu.ece.edif.arch.xilinx.parts;

/**
 * Represents a specific part available from Xilinx. 
 * Contains several Strings for the part name, and also has
 * a specific XilinxFamily, XilixDevice, XilinxPackage,
 * and XilinxSpeedGrade. However, any of these fields can
 * be null. 
 */
public class XilinxPart {

	public XilinxPart(String name, XilinxFamily family, XilinxDevice device,
			XilinxPackage pkg, XilinxSpeedGrade speedGrade) {
		_suppliedPartName = name;
		_family = family;
		_device = device;
		_package = pkg;
		_speedGrade = speedGrade;
		_partName = createPartName();
		_partNameNoSpeedGrade = createPartNameNoSpeedGrade();
	}
	
	public XilinxDevice getDevice() {
		return _device;
	}
	
	public XilinxPackage getPackage() {
		return _package;
	}
	
	public XilinxSpeedGrade getSpeedGrade() {
		return _speedGrade;
	}
	
	public String getPartName() {
		return _partName;
	}
	
	public String getSuppliedPartName() {
		return _suppliedPartName;
	}

	/**
	 * This method exists to support the current DeviceUtilizationTrackers.
	 * It may no longer be needed once they are rewritten.
	 */	
	public String getPartNameNoSpeedGrade() {
		return _partNameNoSpeedGrade;
	}
	
	private String createPartNameNoSpeedGrade() {
		String retVal;
		if (_family == null || _device == null || _package == null) {
			retVal = null;
		}
		else {
			retVal = _family.getPartNamePrefix() + (_device.getDeviceName()).replace(_family.getPartNamePrefix(), "") + _package.getPackageName();
		}
		return retVal;
	}
	
	private String createPartName() {
		String retVal;
		if (_family == null || _device == null || _package == null) {
			retVal = null;
		}
		else {
			retVal = _family.getPartNamePrefix() + (_device.getDeviceName()).replace(_family.getPartNamePrefix(), "") + _package.getPackageName() + _speedGrade.getSpeedGradeName();
		}
		return retVal;
	}
	
	public String toString() {
		String retVal = "";
		if (_partName != null) {
			retVal += "Part Name: " + _partName + "\n";
		}
		else {
			retVal += "Part Name: None or Invalid\n";
		}
		if (_partNameNoSpeedGrade != null) {
			retVal += "Part Name (without speed grade): " + _partNameNoSpeedGrade + "\n";
		}
		else {
			retVal += "Part Name (without speed grade):: None or Invalid\n";
		}
		if (_suppliedPartName != null) {
			retVal += "Supplied Part Name: " + _suppliedPartName + "\n";
		}
		else {
			retVal += "Supplied Part Name: None or Invalid\n";
		}
		if (_family != null) {
			retVal += "Family: " + _family.getFamilyName() + "\n";
		}
		else {
			retVal += "Family: none or invalid\n";
		}
		if (_device != null) {
			retVal += "Device: " + _device.getDeviceName() + "\n";
		}
		else {
			retVal += "Device/Family: none or invalid\n";
		}
		if (_package != null) {
			retVal += "Package: " + _package.getPackageName() + "\n";
		}
		else {
			retVal += "Package: none or invalid\n";
		}
		if (_speedGrade != null) {
			retVal += "Speed Grade: " + _speedGrade.getSpeedGradeName() + "\n";
		}
		else {
			retVal += "Speed Grade: none or invalid\n";
		}
		return retVal;
	}
	
	private String _partName;
	private String _partNameNoSpeedGrade;
	private String _suppliedPartName;
	private XilinxDevice _device;
	private XilinxFamily _family;
	private XilinxPackage _package;
	private XilinxSpeedGrade _speedGrade;
}
