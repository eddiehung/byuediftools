/*
 * This file was auto-generated on Thu Jun 24 08:52:00 MDT 2010
 * by edu.byu.ece.edif.arch.xilinx.parts.XilinxDeviceClassGenerator.
 * See the source code to make changes.
 *
 * Do not modify this file directly.
 */


package edu.byu.ece.edif.arch.xilinx.parts;

public class XilinxSpartan3Family extends XilinxFamily {

	private XilinxSpartan3Family() {
		super("Spartan3", "XC3S");
	}

	protected void addDevices() {
		if(!_initialized) {
			_initialized = true;
			addDevice(new XC3S50(this));
			addDevice(new XC3S200(this));
			addDevice(new XC3S400(this));
			addDevice(new XC3S1000(this));
			addDevice(new XC3S1000L(this));
			addDevice(new XC3S1500(this));
			addDevice(new XC3S1500L(this));
			addDevice(new XC3S2000(this));
			addDevice(new XC3S4000(this));
			addDevice(new XC3S4000L(this));
			addDevice(new XC3S5000(this));
			addDevice(new XC3S50A(this));
			addDevice(new XC3S50AN(this));
			addDevice(new XC3S200A(this));
			addDevice(new XC3S200AN(this));
			addDevice(new XC3S400A(this));
			addDevice(new XC3S400AN(this));
			addDevice(new XC3S700A(this));
			addDevice(new XC3S700AN(this));
			addDevice(new XC3S1400A(this));
			addDevice(new XC3S1400AN(this));
			addDevice(new XC3SD1800A(this));
			addDevice(new XC3SD3400A(this));
			addDevice(new XC3S100E(this));
			addDevice(new XC3S250E(this));
			addDevice(new XC3S500E(this));
			addDevice(new XC3S1200E(this));
			addDevice(new XC3S1600E(this));
		}
	}

	class XC3S50 extends XilinxDevice {

		public XC3S50(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S50";
			_validPackages = new XilinxPackage[] {new XilinxPackage("PQ208"), new XilinxPackage("TQ144"), new XilinxPackage("VQ100"), new XilinxPackage("CP132"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S200 extends XilinxDevice {

		public XC3S200(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S200";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FT256"), new XilinxPackage("PQ208"), new XilinxPackage("TQ144"), new XilinxPackage("VQ100"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S400 extends XilinxDevice {

		public XC3S400(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S400";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG320"), new XilinxPackage("FG456"), new XilinxPackage("FT256"), new XilinxPackage("PQ208"), new XilinxPackage("TQ144"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S1000 extends XilinxDevice {

		public XC3S1000(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S1000";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG320"), new XilinxPackage("FG456"), new XilinxPackage("FG676"), new XilinxPackage("FT256"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S1000L extends XilinxDevice {

		public XC3S1000L(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S1000L";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FT256"), new XilinxPackage("FG320"), new XilinxPackage("FG456"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S1500 extends XilinxDevice {

		public XC3S1500(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S1500";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG320"), new XilinxPackage("FG456"), new XilinxPackage("FG676"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S1500L extends XilinxDevice {

		public XC3S1500L(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S1500L";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG320"), new XilinxPackage("FG456"), new XilinxPackage("FG676"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S2000 extends XilinxDevice {

		public XC3S2000(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S2000";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG456"), new XilinxPackage("FG676"), new XilinxPackage("FG900"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S4000 extends XilinxDevice {

		public XC3S4000(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S4000";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG676"), new XilinxPackage("FG900"), new XilinxPackage("FG1156"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S4000L extends XilinxDevice {

		public XC3S4000L(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S4000L";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG900"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S5000 extends XilinxDevice {

		public XC3S5000(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S5000";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG900"), new XilinxPackage("FG1156"), new XilinxPackage("FG676"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S50A extends XilinxDevice {

		public XC3S50A(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S50A";
			_validPackages = new XilinxPackage[] {new XilinxPackage("TQ144"), new XilinxPackage("FT256"), new XilinxPackage("VQ100"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S50AN extends XilinxDevice {

		public XC3S50AN(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S50AN";
			_validPackages = new XilinxPackage[] {new XilinxPackage("TQG144"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S200A extends XilinxDevice {

		public XC3S200A(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S200A";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FT256"), new XilinxPackage("FG320"), new XilinxPackage("VQ100"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S200AN extends XilinxDevice {

		public XC3S200AN(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S200AN";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FTG256"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S400A extends XilinxDevice {

		public XC3S400A(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S400A";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FT256"), new XilinxPackage("FG320"), new XilinxPackage("FG400"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S400AN extends XilinxDevice {

		public XC3S400AN(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S400AN";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FGG400"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S700A extends XilinxDevice {

		public XC3S700A(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S700A";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG400"), new XilinxPackage("FG484"), new XilinxPackage("FT256"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S700AN extends XilinxDevice {

		public XC3S700AN(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S700AN";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FGG484"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S1400A extends XilinxDevice {

		public XC3S1400A(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S1400A";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG484"), new XilinxPackage("FG676"), new XilinxPackage("FT256"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S1400AN extends XilinxDevice {

		public XC3S1400AN(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S1400AN";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FGG676"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3SD1800A extends XilinxDevice {

		public XC3SD1800A(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3SD1800A";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CS484"), new XilinxPackage("FG676"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3SD3400A extends XilinxDevice {

		public XC3SD3400A(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3SD3400A";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG676"), new XilinxPackage("CS484"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S100E extends XilinxDevice {

		public XC3S100E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S100E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("VQ100"), new XilinxPackage("CP132"), new XilinxPackage("TQ144"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S250E extends XilinxDevice {

		public XC3S250E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S250E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("VQ100"), new XilinxPackage("CP132"), new XilinxPackage("TQ144"), new XilinxPackage("FT256"), new XilinxPackage("PQ208"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S500E extends XilinxDevice {

		public XC3S500E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S500E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CP132"), new XilinxPackage("FT256"), new XilinxPackage("FG320"), new XilinxPackage("PQ208"), new XilinxPackage("VQ100"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S1200E extends XilinxDevice {

		public XC3S1200E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S1200E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FT256"), new XilinxPackage("FG320"), new XilinxPackage("FG400"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	class XC3S1600E extends XilinxDevice {

		public XC3S1600E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC3S1600E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG320"), new XilinxPackage("FG400"), new XilinxPackage("FG484"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-4"), };
		}
	}

	public static synchronized XilinxSpartan3Family getSingletonObject() {
		if (!_singletonCreated) {
			_singletonCreated = true;
			_singletonObject = new XilinxSpartan3Family();
		}
		return _singletonObject;
	}

	public static void initializeFamily() {
		getSingletonObject();
	}

	private static boolean _initialized = false;
	private static boolean _singletonCreated = false;
	private static XilinxSpartan3Family _singletonObject;
}
