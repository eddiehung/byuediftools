/*
 * This file was auto-generated on Mon Jun 14 09:50:12 MDT 2010
 * by edu.byu.ece.edif.arch.xilinx.parts.XilinxV4DeviceClassGenerator.
 * See the source code to make changes.
 *
 * Do not modify this file directly.
 */


package edu.byu.ece.edif.arch.xilinx.parts;

public class XilinxV4Family extends XilinxFamily {

	private XilinxV4Family() {
		super("Virtex4", "XC4V");
	}

	protected void addDevices() {
		if(!_initialized) {
			_initialized = true;
			addDevice(new XC4VFX12(this));
			addDevice(new XC4VLX15(this));
			addDevice(new XC4VFX20(this));
			addDevice(new XC4VLX25(this));
			addDevice(new XC4VSX25(this));
			addDevice(new XC4VSX35(this));
			addDevice(new XC4VFX40(this));
			addDevice(new XC4VLX40(this));
			addDevice(new XC4VSX55(this));
			addDevice(new XC4VFX60(this));
			addDevice(new XC4VLX60(this));
			addDevice(new XC4VLX80(this));
			addDevice(new XC4VFX100(this));
			addDevice(new XC4VLX100(this));
			addDevice(new XC4VFX140(this));
			addDevice(new XC4VLX160(this));
			addDevice(new XC4VLX200(this));
		}
	}

	class XC4VFX12 extends XilinxDevice {

		public XC4VFX12(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VFX12";
			_validPackages = new XilinxPackage[] {new XilinxPackage("SF363"), new XilinxPackage("FF668"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-12"), new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VLX15 extends XilinxDevice {

		public XC4VLX15(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VLX15";
			_validPackages = new XilinxPackage[] {new XilinxPackage("SF363"), new XilinxPackage("FF668"), new XilinxPackage("FF676"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-12"), new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VFX20 extends XilinxDevice {

		public XC4VFX20(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VFX20";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF672"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-12"), new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VLX25 extends XilinxDevice {

		public XC4VLX25(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VLX25";
			_validPackages = new XilinxPackage[] {new XilinxPackage("SF363"), new XilinxPackage("FF668"), new XilinxPackage("FF676"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-12"), new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VSX25 extends XilinxDevice {

		public XC4VSX25(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VSX25";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF668"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-12"), new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VSX35 extends XilinxDevice {

		public XC4VSX35(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VSX35";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF668"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-12"), new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VFX40 extends XilinxDevice {

		public XC4VFX40(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VFX40";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF672"), new XilinxPackage("FF1152"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-12"), new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VLX40 extends XilinxDevice {

		public XC4VLX40(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VLX40";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF668"), new XilinxPackage("FF1148"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-12"), new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VSX55 extends XilinxDevice {

		public XC4VSX55(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VSX55";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1148"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-12"), new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VFX60 extends XilinxDevice {

		public XC4VFX60(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VFX60";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF672"), new XilinxPackage("FF1152"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-12"), new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VLX60 extends XilinxDevice {

		public XC4VLX60(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VLX60";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF668"), new XilinxPackage("FF1148"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-12"), new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VLX80 extends XilinxDevice {

		public XC4VLX80(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VLX80";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1148"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-12"), new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VFX100 extends XilinxDevice {

		public XC4VFX100(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VFX100";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1152"), new XilinxPackage("FF1517"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-12"), new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VLX100 extends XilinxDevice {

		public XC4VLX100(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VLX100";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1148"), new XilinxPackage("FF1513"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-12"), new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VFX140 extends XilinxDevice {

		public XC4VFX140(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VFX140";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1517"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VLX160 extends XilinxDevice {

		public XC4VLX160(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VLX160";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1148"), new XilinxPackage("FF1513"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-12"), new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	class XC4VLX200 extends XilinxDevice {

		public XC4VLX200(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC4VLX200";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1513"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-11"), new XilinxSpeedGrade("-10"), };
		}
	}

	public static synchronized XilinxV4Family getSingletonObject() {
		if (!_singletonCreated) {
			_singletonCreated = true;
			_singletonObject = new XilinxV4Family();
		}
		return _singletonObject;
	}

	public static void initializeFamily() {
		getSingletonObject();
	}

	private static boolean _initialized = false;
	private static boolean _singletonCreated = false;
	private static XilinxV4Family _singletonObject;
}
