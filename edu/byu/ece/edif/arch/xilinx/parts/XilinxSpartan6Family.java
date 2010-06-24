/*
 * This file was auto-generated on Thu Jun 24 08:52:01 MDT 2010
 * by edu.byu.ece.edif.arch.xilinx.parts.XilinxDeviceClassGenerator.
 * See the source code to make changes.
 *
 * Do not modify this file directly.
 */


package edu.byu.ece.edif.arch.xilinx.parts;

public class XilinxSpartan6Family extends XilinxFamily {

	private XilinxSpartan6Family() {
		super("Spartan6", "XC6S");
	}

	protected void addDevices() {
		if(!_initialized) {
			_initialized = true;
			addDevice(new XC6SLX4(this));
			addDevice(new XC6SLX9(this));
			addDevice(new XC6SLX16(this));
			addDevice(new XC6SLX25(this));
			addDevice(new XC6SLX25T(this));
			addDevice(new XC6SLX45(this));
			addDevice(new XC6SLX45T(this));
			addDevice(new XC6SLX75(this));
			addDevice(new XC6SLX75T(this));
			addDevice(new XC6SLX100(this));
			addDevice(new XC6SLX100T(this));
			addDevice(new XC6SLX150(this));
			addDevice(new XC6SLX150T(this));
			addDevice(new XC6SLX4L(this));
			addDevice(new XC6SLX9L(this));
			addDevice(new XC6SLX16L(this));
			addDevice(new XC6SLX25L(this));
			addDevice(new XC6SLX45L(this));
			addDevice(new XC6SLX75L(this));
			addDevice(new XC6SLX100L(this));
			addDevice(new XC6SLX150L(this));
		}
	}

	class XC6SLX4 extends XilinxDevice {

		public XC6SLX4(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX4";
			_validPackages = new XilinxPackage[] {new XilinxPackage("TQG144"), new XilinxPackage("CPG196"), new XilinxPackage("CSG225"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), };
		}
	}

	class XC6SLX9 extends XilinxDevice {

		public XC6SLX9(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX9";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CSG225"), new XilinxPackage("CSG324"), new XilinxPackage("TQG144"), new XilinxPackage("FTG256"), new XilinxPackage("CPG196"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), };
		}
	}

	class XC6SLX16 extends XilinxDevice {

		public XC6SLX16(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX16";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CSG324"), new XilinxPackage("FTG256"), new XilinxPackage("CSG225"), new XilinxPackage("CPG196"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), };
		}
	}

	class XC6SLX25 extends XilinxDevice {

		public XC6SLX25(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX25";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CSG324"), new XilinxPackage("FGG484"), new XilinxPackage("FTG256"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), };
		}
	}

	class XC6SLX25T extends XilinxDevice {

		public XC6SLX25T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX25T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CSG324"), new XilinxPackage("FGG484"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-4"), new XilinxSpeedGrade("-2"), };
		}
	}

	class XC6SLX45 extends XilinxDevice {

		public XC6SLX45(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX45";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FGG484"), new XilinxPackage("CSG324"), new XilinxPackage("FGG676"), new XilinxPackage("CSG484"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), };
		}
	}

	class XC6SLX45T extends XilinxDevice {

		public XC6SLX45T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX45T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FGG484"), new XilinxPackage("CSG324"), new XilinxPackage("CSG484"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-4"), new XilinxSpeedGrade("-2"), };
		}
	}

	class XC6SLX75 extends XilinxDevice {

		public XC6SLX75(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX75";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CSG484"), new XilinxPackage("FGG484"), new XilinxPackage("FGG676"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), };
		}
	}

	class XC6SLX75T extends XilinxDevice {

		public XC6SLX75T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX75T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FGG676"), new XilinxPackage("CSG484"), new XilinxPackage("FGG484"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-4"), new XilinxSpeedGrade("-2"), };
		}
	}

	class XC6SLX100 extends XilinxDevice {

		public XC6SLX100(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX100";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FGG484"), new XilinxPackage("FGG676"), new XilinxPackage("CSG484"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), };
		}
	}

	class XC6SLX100T extends XilinxDevice {

		public XC6SLX100T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX100T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FGG484"), new XilinxPackage("FGG676"), new XilinxPackage("FGG900"), new XilinxPackage("CSG484"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-4"), new XilinxSpeedGrade("-2"), };
		}
	}

	class XC6SLX150 extends XilinxDevice {

		public XC6SLX150(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX150";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FGG484"), new XilinxPackage("FGG676"), new XilinxPackage("CSG484"), new XilinxPackage("FGG900"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), };
		}
	}

	class XC6SLX150T extends XilinxDevice {

		public XC6SLX150T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX150T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FGG676"), new XilinxPackage("FGG484"), new XilinxPackage("FGG900"), new XilinxPackage("CSG484"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-4"), new XilinxSpeedGrade("-2"), };
		}
	}

	class XC6SLX4L extends XilinxDevice {

		public XC6SLX4L(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX4L";
			_validPackages = new XilinxPackage[] {new XilinxPackage("TQG144"), new XilinxPackage("CPG196"), new XilinxPackage("CSG225"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	class XC6SLX9L extends XilinxDevice {

		public XC6SLX9L(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX9L";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CSG225"), new XilinxPackage("CSG324"), new XilinxPackage("TQG144"), new XilinxPackage("FTG256"), new XilinxPackage("CPG196"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	class XC6SLX16L extends XilinxDevice {

		public XC6SLX16L(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX16L";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CSG324"), new XilinxPackage("FTG256"), new XilinxPackage("CSG225"), new XilinxPackage("CPG196"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	class XC6SLX25L extends XilinxDevice {

		public XC6SLX25L(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX25L";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CSG324"), new XilinxPackage("FGG484"), new XilinxPackage("FTG256"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	class XC6SLX45L extends XilinxDevice {

		public XC6SLX45L(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX45L";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FGG484"), new XilinxPackage("CSG324"), new XilinxPackage("FGG676"), new XilinxPackage("CSG484"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	class XC6SLX75L extends XilinxDevice {

		public XC6SLX75L(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX75L";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CSG484"), new XilinxPackage("FGG484"), new XilinxPackage("FGG676"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	class XC6SLX100L extends XilinxDevice {

		public XC6SLX100L(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX100L";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FGG484"), new XilinxPackage("FGG676"), new XilinxPackage("CSG484"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	class XC6SLX150L extends XilinxDevice {

		public XC6SLX150L(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC6SLX150L";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FGG484"), new XilinxPackage("FGG676"), new XilinxPackage("CSG484"), new XilinxPackage("FGG900"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-1L"), };
		}
	}

	public static synchronized XilinxSpartan6Family getSingletonObject() {
		if (!_singletonCreated) {
			_singletonCreated = true;
			_singletonObject = new XilinxSpartan6Family();
		}
		return _singletonObject;
	}

	public static void initializeFamily() {
		getSingletonObject();
	}

	private static boolean _initialized = false;
	private static boolean _singletonCreated = false;
	private static XilinxSpartan6Family _singletonObject;
}
