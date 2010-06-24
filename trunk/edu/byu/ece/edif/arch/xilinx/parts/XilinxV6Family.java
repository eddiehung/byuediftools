/*
 * This file was auto-generated on Thu Jun 24 08:52:03 MDT 2010
 * by edu.byu.ece.edif.arch.xilinx.parts.XilinxDeviceClassGenerator.
 * See the source code to make changes.
 *
 * Do not modify this file directly.
 */


package edu.byu.ece.edif.arch.xilinx.parts;

public class XilinxV6Family extends XilinxFamily {

	private XilinxV6Family() {
		super("Virtex6", "XC6V");
	}

	protected void addDevices() {
		if(!_initialized) {
			_initialized = true;
			addDevice(new XC6VCX75T(this));
			addDevice(new XC6VLX75T(this));
			addDevice(new XC6VCX130T(this));
			addDevice(new XC6VLX130T(this));
			addDevice(new XC6VCX195T(this));
			addDevice(new XC6VLX195T(this));
			addDevice(new XC6VCX240T(this));
			addDevice(new XC6VLX240T(this));
			addDevice(new XC6VHX250T(this));
			addDevice(new XC6VHX255T(this));
			addDevice(new XC6VSX315T(this));
			addDevice(new XC6VLX365T(this));
			addDevice(new XC6VHX380T(this));
			addDevice(new XC6VSX475T(this));
			addDevice(new XC6VLX550T(this));
			addDevice(new XC6VHX565T(this));
			addDevice(new XC6VLX760(this));
			addDevice(new XC6VLX75TL(this));
			addDevice(new XC6VLX130TL(this));
			addDevice(new XC6VLX195TL(this));
			addDevice(new XC6VLX240TL(this));
			addDevice(new XC6VSX315TL(this));
			addDevice(new XC6VLX365TL(this));
			addDevice(new XC6VSX475TL(this));
			addDevice(new XC6VLX550TL(this));
			addDevice(new XC6VLX760L(this));
		}
	}

	class XC6VCX75T extends XilinxDevice {

		public XC6VCX75T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VCX75T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF484"), new XilinxPackage("FF784"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VLX75T extends XilinxDevice {

		public XC6VLX75T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VLX75T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF484"), new XilinxPackage("FF784"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VCX130T extends XilinxDevice {

		public XC6VCX130T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VCX130T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF484"), new XilinxPackage("FF784"), new XilinxPackage("FF1156"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VLX130T extends XilinxDevice {

		public XC6VLX130T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VLX130T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF484"), new XilinxPackage("FF784"), new XilinxPackage("FF1156"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VCX195T extends XilinxDevice {

		public XC6VCX195T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VCX195T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF784"), new XilinxPackage("FF1156"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VLX195T extends XilinxDevice {

		public XC6VLX195T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VLX195T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF784"), new XilinxPackage("FF1156"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VCX240T extends XilinxDevice {

		public XC6VCX240T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VCX240T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF784"), new XilinxPackage("FF1156"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VLX240T extends XilinxDevice {

		public XC6VLX240T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VLX240T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF784"), new XilinxPackage("FF1156"), new XilinxPackage("FF1759"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VHX250T extends XilinxDevice {

		public XC6VHX250T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VHX250T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1154"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VHX255T extends XilinxDevice {

		public XC6VHX255T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VHX255T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1155"), new XilinxPackage("FF1923"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VSX315T extends XilinxDevice {

		public XC6VSX315T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VSX315T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1156"), new XilinxPackage("FF1759"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VLX365T extends XilinxDevice {

		public XC6VLX365T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VLX365T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1156"), new XilinxPackage("FF1759"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VHX380T extends XilinxDevice {

		public XC6VHX380T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VHX380T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1155"), new XilinxPackage("FF1154"), new XilinxPackage("FF1923"), new XilinxPackage("FF1924"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VSX475T extends XilinxDevice {

		public XC6VSX475T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VSX475T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1156"), new XilinxPackage("FF1759"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VLX550T extends XilinxDevice {

		public XC6VLX550T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VLX550T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1759"), new XilinxPackage("FF1760"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VHX565T extends XilinxDevice {

		public XC6VHX565T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VHX565T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1923"), new XilinxPackage("FF1924"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VLX760 extends XilinxDevice {

		public XC6VLX760(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VLX760";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1760"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC6VLX75TL extends XilinxDevice {

		public XC6VLX75TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VLX75TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF484"), new XilinxPackage("FF784"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	class XC6VLX130TL extends XilinxDevice {

		public XC6VLX130TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VLX130TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF484"), new XilinxPackage("FF784"), new XilinxPackage("FF1156"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	class XC6VLX195TL extends XilinxDevice {

		public XC6VLX195TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VLX195TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF784"), new XilinxPackage("FF1156"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	class XC6VLX240TL extends XilinxDevice {

		public XC6VLX240TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VLX240TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF784"), new XilinxPackage("FF1156"), new XilinxPackage("FF1759"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	class XC6VSX315TL extends XilinxDevice {

		public XC6VSX315TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VSX315TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1156"), new XilinxPackage("FF1759"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	class XC6VLX365TL extends XilinxDevice {

		public XC6VLX365TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VLX365TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1156"), new XilinxPackage("FF1759"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	class XC6VSX475TL extends XilinxDevice {

		public XC6VSX475TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VSX475TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1156"), new XilinxPackage("FF1759"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	class XC6VLX550TL extends XilinxDevice {

		public XC6VLX550TL(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VLX550TL";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1759"), new XilinxPackage("FF1760"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	class XC6VLX760L extends XilinxDevice {

		public XC6VLX760L(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6VLX760L";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1760"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	public static synchronized XilinxV6Family getSingletonObject() {
		if (!_singletonCreated) {
			_singletonCreated = true;
			_singletonObject = new XilinxV6Family();
		}
		return _singletonObject;
	}

	public static void initializeFamily() {
		getSingletonObject();
	}

	private static boolean _initialized = false;
	private static boolean _singletonCreated = false;
	private static XilinxV6Family _singletonObject;
}
