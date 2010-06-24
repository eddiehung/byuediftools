/*
 * This file was auto-generated on Thu Jun 24 08:47:53 MDT 2010
 * by edu.byu.ece.edif.arch.xilinx.parts.XilinxDeviceClassGenerator.
 * See the source code to make changes.
 *
 * Do not modify this file directly.
 */


package edu.byu.ece.edif.arch.xilinx.parts;

public class XilinxSpartan2Family extends XilinxFamily {

	private XilinxSpartan2Family() {
		super("Spartan2", "XC2S");
	}

	protected void addDevices() {
		if(!_initialized) {
			_initialized = true;
			addDevice(new XC2S15(this));
			addDevice(new XC2S30(this));
			addDevice(new XC2S50(this));
			addDevice(new XC2S100(this));
			addDevice(new XC2S150(this));
			addDevice(new XC2S200(this));
			addDevice(new XA2S300E(this));
		}
	}

	class XC2S15 extends XilinxDevice {

		public XC2S15(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2S15";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CS144"), new XilinxPackage("TQ144"), new XilinxPackage("VQ100"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-5Q"), };
		}
	}

	class XC2S30 extends XilinxDevice {

		public XC2S30(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2S30";
			_validPackages = new XilinxPackage[] {new XilinxPackage("CS144"), new XilinxPackage("TQ144"), new XilinxPackage("PQ208"), new XilinxPackage("VQ100"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-5Q"), };
		}
	}

	class XC2S50 extends XilinxDevice {

		public XC2S50(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2S50";
			_validPackages = new XilinxPackage[] {new XilinxPackage("TQ144"), new XilinxPackage("FG256"), new XilinxPackage("PQ208"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-5Q"), };
		}
	}

	class XC2S100 extends XilinxDevice {

		public XC2S100(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2S100";
			_validPackages = new XilinxPackage[] {new XilinxPackage("TQ144"), new XilinxPackage("FG256"), new XilinxPackage("FG456"), new XilinxPackage("PQ208"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-5Q"), };
		}
	}

	class XC2S150 extends XilinxDevice {

		public XC2S150(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2S150";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG456"), new XilinxPackage("FG256"), new XilinxPackage("PQ208"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-5Q"), };
		}
	}

	class XC2S200 extends XilinxDevice {

		public XC2S200(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC2S200";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FG256"), new XilinxPackage("FG456"), new XilinxPackage("PQ208"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-5"), new XilinxSpeedGrade("-5Q"), };
		}
	}

	class XA2S300E extends XilinxDevice {

		public XA2S300E(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XA2S300E";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FT256"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-6"), new XilinxSpeedGrade("-6Q"), };
		}
	}

	public static synchronized XilinxSpartan2Family getSingletonObject() {
		if (!_singletonCreated) {
			_singletonCreated = true;
			_singletonObject = new XilinxSpartan2Family();
		}
		return _singletonObject;
	}

	public static void initializeFamily() {
		getSingletonObject();
	}

	private static boolean _initialized = false;
	private static boolean _singletonCreated = false;
	private static XilinxSpartan2Family _singletonObject;
}
