package edu.byu.ece.edif.arch.xilinx.parts;

import java.util.Collection;

public class XilinxDevice {

	public XilinxDevice(XilinxFamily family, String name, XilinxPackage packages[], XilinxSpeedGrade grades[]) {
		_family = family;
		_deviceName = name;
		// _validPackages =
		// _validSpeedGrades =
	}

	public XilinxFamily getFamily() {
		return _family;
	}

	public String getDeviceName() {
		return _deviceName;
	}
	
	public Collection<XilinxPackage> getValidPackages() {
		return _validPackages;
	}
	
	public Collection<XilinxSpeedGrade> getValidSpeedGrades() {
		return _validSpeedGrades;
	}
	
	XilinxFamily _family;
	String _deviceName;
	Collection<XilinxPackage> _validPackages;
	Collection<XilinxSpeedGrade> _validSpeedGrades;
	
}
