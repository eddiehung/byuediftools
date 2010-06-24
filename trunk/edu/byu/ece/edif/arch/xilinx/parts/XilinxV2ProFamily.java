/*
 * This file was auto-generated on Thu Jun 24 08:47:54 MDT 2010
 * by edu.byu.ece.edif.arch.xilinx.parts.XilinxDeviceClassGenerator.
 * See the source code to make changes.
 *
 * Do not modify this file directly.
 */


package edu.byu.ece.edif.arch.xilinx.parts;

public class XilinxV2ProFamily extends XilinxFamily {

	private XilinxV2ProFamily() {
		super("Virtex2Pro", "XC2VP");
	}

	protected void addDevices() {
		if(!_initialized) {
			_initialized = true;
			addDevice(new XC2VP2(this));
			addDevice(new XC2VP4(this));
			addDevice(new XC2VP7(this));
			addDevice(new XC2VP20(this));
			addDevice(new XC2VPX20(this));
			addDevice(new XC2VP30(this));
			addDevice(new XC2VP40(this));
			addDevice(new XC2VP50(this));
			addDevice(new XC2VP70(this));
			addDevice(new XC2VPX70(this));
			addDevice(new XC2VP100(this));
		}
	}

	class XC2VP2 extends XilinxDevice {

		public XC2VP2(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2VP2";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG256"), new XilinxPackage("FG456"), new XilinxPackage("FF672"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), };
		}
	}

	class XC2VP4 extends XilinxDevice {

		public XC2VP4(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2VP4";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG256"), new XilinxPackage("FG456"), new XilinxPackage("FF672"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), };
		}
	}

	class XC2VP7 extends XilinxDevice {

		public XC2VP7(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2VP7";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG456"), new XilinxPackage("FF672"), new XilinxPackage("FF896"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), };
		}
	}

	class XC2VP20 extends XilinxDevice {

		public XC2VP20(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2VP20";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG676"), new XilinxPackage("FF896"), new XilinxPackage("FF1152"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), };
		}
	}

	class XC2VPX20 extends XilinxDevice {

		public XC2VPX20(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2VPX20";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF896"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), };
		}
	}

	class XC2VP30 extends XilinxDevice {

		public XC2VP30(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2VP30";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG676"), new XilinxPackage("FF896"), new XilinxPackage("FF1152"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), };
		}
	}

	class XC2VP40 extends XilinxDevice {

		public XC2VP40(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2VP40";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG676"), new XilinxPackage("FF1148"), new XilinxPackage("FF1152"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), };
		}
	}

	class XC2VP50 extends XilinxDevice {

		public XC2VP50(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2VP50";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1148"), new XilinxPackage("FF1152"), new XilinxPackage("FF1517"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), };
		}
	}

	class XC2VP70 extends XilinxDevice {

		public XC2VP70(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2VP70";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1517"), new XilinxPackage("FF1704"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), };
		}
	}

	class XC2VPX70 extends XilinxDevice {

		public XC2VPX70(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2VPX70";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1704"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-7"), new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), };
		}
	}

	class XC2VP100 extends XilinxDevice {

		public XC2VP100(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2VP100";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1696"), new XilinxPackage("FF1704"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), };
		}
	}

	public static synchronized XilinxV2ProFamily getSingletonObject() {
		if (!_singletonCreated) {
			_singletonCreated = true;
			_singletonObject = new XilinxV2ProFamily();
		}
		return _singletonObject;
	}

	public static void initializeFamily() {
		getSingletonObject();
	}

	private static boolean _initialized = false;
	private static boolean _singletonCreated = false;
	private static XilinxV2ProFamily _singletonObject;
}
