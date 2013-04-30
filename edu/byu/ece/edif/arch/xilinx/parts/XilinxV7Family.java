/*
 * This file was auto-generated on Tue Mar 19 19:58:06 MDT 2013
 * by edu.byu.ece.edif.arch.xilinx.parts.XilinxDeviceClassGenerator.
 * See the source code to make changes.
 *
 * Do not modify this file directly.
 */


package edu.byu.ece.edif.arch.xilinx.parts;

public class XilinxV7Family extends XilinxFamily {

	private XilinxV7Family() {
		super("Virtex7", "XC7V");
	}

	protected void addDevices() {
		if(!_initialized) {
			_initialized = true;
			addDevice(new XC7VX330T(this));
			addDevice(new XC7VX415T(this));
			addDevice(new XC7VX485T(this));
			addDevice(new XC7VX550T(this));
			addDevice(new XC7VH580T(this));
			addDevice(new XC7V585T(this));
			addDevice(new XC7VX690T(this));
			addDevice(new XC7VH870T(this));
			addDevice(new XC7VX980T(this));
			addDevice(new XC7VX1140T(this));
			addDevice(new XC7V2000T(this));
			addDevice(new XC7VX330TL(this));
			addDevice(new XC7VX415TL(this));
			addDevice(new XC7VX485TL(this));
			addDevice(new XC7VX550TL(this));
			addDevice(new XC7V585TL(this));
			addDevice(new XC7VX690TL(this));
			addDevice(new XC7VX980TL(this));
			addDevice(new XC7VX1140TL(this));
			addDevice(new XC7V2000TL(this));
		}
	}

	class XC7VX330T extends XilinxDevice {

		public XC7VX330T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VX330T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FFG1157"), new XilinxPackage("FFG1761"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7VX415T extends XilinxDevice {

		public XC7VX415T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VX415T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FFG1157"), new XilinxPackage("FFG1158"), new XilinxPackage("FFG1927"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7VX485T extends XilinxDevice {

		public XC7VX485T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VX485T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FFG1157"), new XilinxPackage("FFG1158"), new XilinxPackage("FFG1761"), new XilinxPackage("FFG1927"), new XilinxPackage("FFG1930"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7VX550T extends XilinxDevice {

		public XC7VX550T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VX550T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FFG1158"), new XilinxPackage("FFG1927"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7VH580T extends XilinxDevice {

		public XC7VH580T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VH580T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("HCG1155"), new XilinxPackage("HCG1931"), new XilinxPackage("HCG1932"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), new XilinxSpeedGrade("-2G"), };
		}
	}

	class XC7V585T extends XilinxDevice {

		public XC7V585T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7V585T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FFG1157"), new XilinxPackage("FFG1761"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7VX690T extends XilinxDevice {

		public XC7VX690T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VX690T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FFG1157"), new XilinxPackage("FFG1158"), new XilinxPackage("FFG1761"), new XilinxPackage("FFG1926"), new XilinxPackage("FFG1927"), new XilinxPackage("FFG1930"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7VH870T extends XilinxDevice {

		public XC7VH870T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VH870T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("HCG1931"), new XilinxPackage("HCG1932"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), new XilinxSpeedGrade("-2G"), };
		}
	}

	class XC7VX980T extends XilinxDevice {

		public XC7VX980T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VX980T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FFG1926"), new XilinxPackage("FFG1928"), new XilinxPackage("FFG1930"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7VX1140T extends XilinxDevice {

		public XC7VX1140T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VX1140T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FLG1930"), new XilinxPackage("FLG1928"), new XilinxPackage("FLG1926"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), new XilinxSpeedGrade("-2G"), new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7V2000T extends XilinxDevice {

		public XC7V2000T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7V2000T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FLG1925"), new XilinxPackage("FHG1761"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), new XilinxSpeedGrade("-2G"), new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7VX330TL extends XilinxDevice {

		public XC7VX330TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VX330TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FFG1157"), new XilinxPackage("FFG1761"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7VX415TL extends XilinxDevice {

		public XC7VX415TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VX415TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FFG1157"), new XilinxPackage("FFG1158"), new XilinxPackage("FFG1927"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7VX485TL extends XilinxDevice {

		public XC7VX485TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VX485TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FFG1157"), new XilinxPackage("FFG1158"), new XilinxPackage("FFG1761"), new XilinxPackage("FFG1927"), new XilinxPackage("FFG1930"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7VX550TL extends XilinxDevice {

		public XC7VX550TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VX550TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FFG1158"), new XilinxPackage("FFG1927"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7V585TL extends XilinxDevice {

		public XC7V585TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7V585TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FFG1157"), new XilinxPackage("FFG1761"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7VX690TL extends XilinxDevice {

		public XC7VX690TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VX690TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FFG1157"), new XilinxPackage("FFG1158"), new XilinxPackage("FFG1761"), new XilinxPackage("FFG1926"), new XilinxPackage("FFG1927"), new XilinxPackage("FFG1930"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7VX980TL extends XilinxDevice {

		public XC7VX980TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VX980TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FFG1926"), new XilinxPackage("FFG1928"), new XilinxPackage("FFG1930"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7VX1140TL extends XilinxDevice {

		public XC7VX1140TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7VX1140TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FLG1930"), new XilinxPackage("FLG1928"), new XilinxPackage("FLG1926"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2L"), };
		}
	}

	class XC7V2000TL extends XilinxDevice {

		public XC7V2000TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC7V2000TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FLG1925"), new XilinxPackage("FHG1761"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2L"), };
		}
	}

	public static synchronized XilinxV7Family getSingletonObject() {
		if (!_singletonCreated) {
			_singletonCreated = true;
			_singletonObject = new XilinxV7Family();
		}
		return _singletonObject;
	}

	public static void initializeFamily() {
		getSingletonObject();
	}

	private static boolean _initialized = false;
	private static boolean _singletonCreated = false;
	private static XilinxV7Family _singletonObject;
}
