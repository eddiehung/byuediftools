package edu.byu.ece.edif.arch.xilinx.parts;

public class XilinxDevice {

	public XilinxFamily getFamily() {
		return _family;
	}

	public String getDeviceName() {
		return _deviceName;
	}
	
	public XilinxPackage[] getValidPackages() {
		return _validPackages;
	}
	
	public XilinxSpeedGrade[] getValidSpeedGrades() {
		return _validSpeedGrades;
	}
	
	protected XilinxFamily _family;
	protected String _deviceName;
	protected XilinxPackage[] _validPackages;
	protected XilinxSpeedGrade[] _validSpeedGrades;
	
}
