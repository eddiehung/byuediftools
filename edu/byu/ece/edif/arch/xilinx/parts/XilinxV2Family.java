/*
 * This file was auto-generated on Thu Jun 24 08:47:54 MDT 2010
 * by edu.byu.ece.edif.arch.xilinx.parts.XilinxDeviceClassGenerator.
 * See the source code to make changes.
 *
 * Do not modify this file directly.
 */


package edu.byu.ece.edif.arch.xilinx.parts;

public class XilinxV2Family extends XilinxFamily {

	private XilinxV2Family() {
		super("Virtex2", "XC2V");
	}

	protected void addDevices() {
		if(!_initialized) {
			_initialized = true;
			addDevice(new XC2V40(this));
			addDevice(new XC2V80(this));
			addDevice(new XC2V250(this));
			addDevice(new XC2V500(this));
			addDevice(new XC2V1000(this));
			addDevice(new XC2V1500(this));
			addDevice(new XC2V2000(this));
			addDevice(new XC2V3000(this));
			addDevice(new XC2V4000(this));
			addDevice(new XC2V6000(this));
			addDevice(new XC2V8000(this));
		}
	}

	class XC2V40 extends XilinxDevice {

		public XC2V40(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2V40";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CS144"), new XilinxPackage("FG256"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC2V80 extends XilinxDevice {

		public XC2V80(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2V80";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CS144"), new XilinxPackage("FG256"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC2V250 extends XilinxDevice {

		public XC2V250(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2V250";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CS144"), new XilinxPackage("FG256"), new XilinxPackage("FG456"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC2V500 extends XilinxDevice {

		public XC2V500(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2V500";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG256"), new XilinxPackage("FG456"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC2V1000 extends XilinxDevice {

		public XC2V1000(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2V1000";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG575"), new XilinxPackage("FF896"), new XilinxPackage("FG256"), new XilinxPackage("FG456"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC2V1500 extends XilinxDevice {

		public XC2V1500(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2V1500";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BG575"), new XilinxPackage("FF896"), new XilinxPackage("FG676"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC2V2000 extends XilinxDevice {

		public XC2V2000(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2V2000";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BF957"), new XilinxPackage("BG575"), new XilinxPackage("FF896"), new XilinxPackage("FG676"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC2V3000 extends XilinxDevice {

		public XC2V3000(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2V3000";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BF957"), new XilinxPackage("BG728"), new XilinxPackage("FF1152"), new XilinxPackage("FG676"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC2V4000 extends XilinxDevice {

		public XC2V4000(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2V4000";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BF957"), new XilinxPackage("FF1152"), new XilinxPackage("FF1517"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC2V6000 extends XilinxDevice {

		public XC2V6000(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2V6000";
			_validPackages = new XilinxPackage[] {new XilinxPackage("BF957"), new XilinxPackage("FF1152"), new XilinxPackage("FF1517"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC2V8000 extends XilinxDevice {

		public XC2V8000(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2V8000";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1152"), new XilinxPackage("FF1517"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	public static synchronized XilinxV2Family getSingletonObject() {
		if (!_singletonCreated) {
			_singletonCreated = true;
			_singletonObject = new XilinxV2Family();
		}
		return _singletonObject;
	}

	public static void initializeFamily() {
		getSingletonObject();
	}

	private static boolean _initialized = false;
	private static boolean _singletonCreated = false;
	private static XilinxV2Family _singletonObject;
}
