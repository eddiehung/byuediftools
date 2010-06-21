package edu.byu.ece.edif.arch.xilinx.parts;

public class XilinxPartNameTokens {

	public XilinxPartNameTokens(String familyName, String devName, String pkgName,
			String sgName) {
		_deviceName = devName;
		_packageName = pkgName;
		_speedGradeName = sgName;
		_familyName = familyName;
	}
	
	public String getDeviceName() {
		return _deviceName;
	}
	
	public String getPackageName() {
		return _packageName;
	}
	
	public String getSpeedGradeName() {
		return _speedGradeName;
	}

	public String getFamilyName() {
		return _familyName;
	}
	
	public static XilinxPartNameTokens getTokensFromPartNameAndDevice(String name, XilinxDevice dev) {
		String famString = dev.getFamily().getFamilyName();
		String nameMinusPrefix = name.substring(
				name.indexOf(dev.getFamily().getPartNamePrefix())
				+ dev.getFamily().getPartNamePrefix().length());
		String speedGradeString = 
			nameMinusPrefix.substring(nameMinusPrefix.indexOf('-'));
		String devicePlusPackage =
			nameMinusPrefix.substring(0,nameMinusPrefix.indexOf('-'));
		boolean foundLetter = false;
		boolean foundNumber = false;
		int curIdx = devicePlusPackage.length()-1;
		while(!foundNumber && curIdx > -1) {
			if(!foundLetter && Character.isLetter(devicePlusPackage.charAt(curIdx)))
				foundLetter = true;
			curIdx--;
			if(foundLetter && Character.isDigit(devicePlusPackage.charAt(curIdx))) {
				foundNumber = true;
			}
		}
		String devString = name.substring(0, curIdx+dev.getFamily().getPartNamePrefix().length()+1);
		String pkgString = devicePlusPackage.substring(curIdx+1);
		return new XilinxPartNameTokens(famString, devString, pkgString, speedGradeString);
	}

	private String _familyName;
	private String _deviceName;
	private String _packageName;
	private String _speedGradeName;

}
