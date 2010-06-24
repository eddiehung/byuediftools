/*
 * This file was auto-generated on Thu Jun 24 08:47:53 MDT 2010
 * by edu.byu.ece.edif.arch.xilinx.parts.XilinxDeviceClassGenerator.
 * See the source code to make changes.
 *
 * Do not modify this file directly.
 */


package edu.byu.ece.edif.arch.xilinx.parts;

public class XilinxVirtexFamily extends XilinxFamily {

	private XilinxVirtexFamily() {
		super("Virtex", "XCV");
	}

	protected void addDevices() {
		if(!_initialized) {
			_initialized = true;
			addDevice(new XCV50(this));
			addDevice(new XCV100(this));
			addDevice(new XCV150(this));
			addDevice(new XCV200(this));
			addDevice(new XCV300(this));
			addDevice(new XCV400(this));
			addDevice(new XCV600(this));
			addDevice(new XCV800(this));
			addDevice(new XCV1000(this));
			addDevice(new XCV50E(this));
			addDevice(new XCV100E(this));
			addDevice(new XCV200E(this));
			addDevice(new XCV300E(this));
			addDevice(new XCV400E(this));
			addDevice(new XCV405E(this));
			addDevice(new XCV600E(this));
			addDevice(new XCV812E(this));
			addDevice(new XCV1000E(this));
			addDevice(new XCV1600E(this));
			addDevice(new XCV2000E(this));
			addDevice(new XCV2600E(this));
			addDevice(new XCV3200E(this));
		}
	}

	class XCV50 extends XilinxDevice {

		public XCV50(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV50";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG256"), new XilinxPackage("CS144"), new XilinxPackage("FG256"), new XilinxPackage("PQ240"), new XilinxPackage("TQ144"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XCV100 extends XilinxDevice {

		public XCV100(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV100";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG256"), new XilinxPackage("CS144"), new XilinxPackage("FG256"), new XilinxPackage("PQ240"), new XilinxPackage("TQ144"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XCV150 extends XilinxDevice {

		public XCV150(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV150";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG256"), new XilinxPackage("BG352"), new XilinxPackage("FG256"), new XilinxPackage("FG456"), new XilinxPackage("PQ240"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XCV200 extends XilinxDevice {

		public XCV200(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV200";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG256"), new XilinxPackage("BG352"), new XilinxPackage("FG256"), new XilinxPackage("FG456"), new XilinxPackage("PQ240"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XCV300 extends XilinxDevice {

		public XCV300(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV300";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG352"), new XilinxPackage("BG432"), new XilinxPackage("FG456"), new XilinxPackage("PQ240"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XCV400 extends XilinxDevice {

		public XCV400(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV400";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG432"), new XilinxPackage("BG560"), new XilinxPackage("FG676"), new XilinxPackage("HQ240"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XCV600 extends XilinxDevice {

		public XCV600(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV600";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG432"), new XilinxPackage("BG560"), new XilinxPackage("FG676"), new XilinxPackage("FG680"), new XilinxPackage("HQ240"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XCV800 extends XilinxDevice {

		public XCV800(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV800";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG432"), new XilinxPackage("BG560"), new XilinxPackage("FG676"), new XilinxPackage("FG680"), new XilinxPackage("HQ240"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XCV1000 extends XilinxDevice {

		public XCV1000(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV1000";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG560"), new XilinxPackage("FG680"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XCV50E extends XilinxDevice {

		public XCV50E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV50E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CS144"), new XilinxPackage("PQ240"), new XilinxPackage("FG256"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-8"), new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), };
		}
	}

	class XCV100E extends XilinxDevice {

		public XCV100E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV100E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG352"), new XilinxPackage("CS144"), new XilinxPackage("FG256"), new XilinxPackage("PQ240"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-8"), new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), };
		}
	}

	class XCV200E extends XilinxDevice {

		public XCV200E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV200E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG352"), new XilinxPackage("CS144"), new XilinxPackage("FG256"), new XilinxPackage("FG456"), new XilinxPackage("PQ240"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-8"), new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), };
		}
	}

	class XCV300E extends XilinxDevice {

		public XCV300E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV300E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG352"), new XilinxPackage("BG432"), new XilinxPackage("FG256"), new XilinxPackage("FG456"), new XilinxPackage("PQ240"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-8"), new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), };
		}
	}

	class XCV400E extends XilinxDevice {

		public XCV400E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV400E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG432"), new XilinxPackage("BG560"), new XilinxPackage("FG676"), new XilinxPackage("PQ240"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-8"), new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), };
		}
	}

	class XCV405E extends XilinxDevice {

		public XCV405E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV405E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG560"), new XilinxPackage("FG676"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-8"), new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), };
		}
	}

	class XCV600E extends XilinxDevice {

		public XCV600E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV600E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG432"), new XilinxPackage("BG560"), new XilinxPackage("FG676"), new XilinxPackage("FG680"), new XilinxPackage("FG900"), new XilinxPackage("HQ240"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-8"), new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), };
		}
	}

	class XCV812E extends XilinxDevice {

		public XCV812E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV812E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG560"), new XilinxPackage("FG900"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-8"), new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), };
		}
	}

	class XCV1000E extends XilinxDevice {

		public XCV1000E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV1000E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG560"), new XilinxPackage("FG680"), new XilinxPackage("FG860"), new XilinxPackage("FG900"), new XilinxPackage("FG1156"), new XilinxPackage("HQ240"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-8"), new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), };
		}
	}

	class XCV1600E extends XilinxDevice {

		public XCV1600E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV1600E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG560"), new XilinxPackage("FG680"), new XilinxPackage("FG860"), new XilinxPackage("FG900"), new XilinxPackage("FG1156"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-8"), new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), };
		}
	}

	class XCV2000E extends XilinxDevice {

		public XCV2000E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV2000E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG560"), new XilinxPackage("FG680"), new XilinxPackage("FG860"), new XilinxPackage("FG1156"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-8"), new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), };
		}
	}

	class XCV2600E extends XilinxDevice {

		public XCV2600E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV2600E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG1156"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-8"), new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), };
		}
	}

	class XCV3200E extends XilinxDevice {

		public XCV3200E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XCV3200E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG1156"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-8"), new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), };
		}
	}

	public static synchronized XilinxVirtexFamily getSingletonObject() {
		if (!_singletonCreated) {
			_singletonCreated = true;
			_singletonObject = new XilinxVirtexFamily();
		}
		return _singletonObject;
	}

	public static void initializeFamily() {
		getSingletonObject();
	}

	private static boolean _initialized = false;
	private static boolean _singletonCreated = false;
	private static XilinxVirtexFamily _singletonObject;
}
