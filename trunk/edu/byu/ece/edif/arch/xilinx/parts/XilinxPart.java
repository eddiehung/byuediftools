package edu.byu.ece.edif.arch.xilinx.parts;

/**
 * Represents a specific part available from Xilinx. 
 * Contains a string for the PartName, and also has
 * a specific XilinxFamily, XilixDevice, XilinxPackage,
 * and XilinxSpeedGrade. However, any of these fields can
 * be null. 
 */
public class XilinxPart {

	public XilinxPart(String name, XilinxFamily family, XilinxDevice device,
			XilinxPackage pkg, XilinxSpeedGrade speedGrade) {
		_partName = name;
		_family = family;
		_device = device;
		_package = pkg;
		_speedGrade = speedGrade;
	}
	
	public XilinxDevice getDevice() {
		return _device;
	}
	
	public XilinxPackage getPackate() {
		return _package;
	}
	
	public XilinxSpeedGrade getSpeedGrade() {
		return _speedGrade;
	}
	
	public String getPartName() {
		return _partName;
	}
	
	public String toString() {
		String retVal = "";
		if (_partName != null) {
			retVal += "Part Name: " + _partName + "\n";
		}
		else {
			retVal += "Part Name: None or Invalid\n";
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
			retVal += "Package: " + _package.getPackageString() + "\n";
		}
		else {
			retVal += "Package: none or invalid\n";
		}
		if (_speedGrade != null) {
			retVal += "Speed Grade: " + _speedGrade.getSpeedGradeString() + "\n";
		}
		else {
			retVal += "Speed Grade: none or invalid\n";
		}
		return retVal;
	}
	
	private String _partName;
	private XilinxDevice _device;
	private XilinxFamily _family;
	private XilinxPackage _package;
	private XilinxSpeedGrade _speedGrade;
}
