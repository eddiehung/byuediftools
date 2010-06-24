/*
 * This file was auto-generated on Thu Jun 24 08:52:02 MDT 2010
 * by edu.byu.ece.edif.arch.xilinx.parts.XilinxDeviceClassGenerator.
 * See the source code to make changes.
 *
 * Do not modify this file directly.
 */


package edu.byu.ece.edif.arch.xilinx.parts;

public class XilinxV5Family extends XilinxFamily {

	private XilinxV5Family() {
		super("Virtex5", "XC5V");
	}

	protected void addDevices() {
		if(!_initialized) {
			_initialized = true;
			addDevice(new XC5VLX20T(this));
			addDevice(new XC5VLX30(this));
			addDevice(new XC5VFX30T(this));
			addDevice(new XC5VLX30T(this));
			addDevice(new XC5VSX35T(this));
			addDevice(new XC5VLX50(this));
			addDevice(new XC5VLX50T(this));
			addDevice(new XC5VSX50T(this));
			addDevice(new XC5VFX70T(this));
			addDevice(new XC5VLX85(this));
			addDevice(new XC5VLX85T(this));
			addDevice(new XC5VSX95T(this));
			addDevice(new XC5VFX100T(this));
			addDevice(new XC5VLX110(this));
			addDevice(new XC5VLX110T(this));
			addDevice(new XC5VFX130T(this));
			addDevice(new XC5VTX150T(this));
			addDevice(new XC5VLX155(this));
			addDevice(new XC5VLX155T(this));
			addDevice(new XC5VFX200T(this));
			addDevice(new XC5VLX220(this));
			addDevice(new XC5VLX220T(this));
			addDevice(new XC5VSX240T(this));
			addDevice(new XC5VTX240T(this));
			addDevice(new XC5VLX330(this));
			addDevice(new XC5VLX330T(this));
		}
	}

	class XC5VLX20T extends XilinxDevice {

		public XC5VLX20T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VLX20T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF323"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VLX30 extends XilinxDevice {

		public XC5VLX30(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VLX30";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF324"), new XilinxPackage("FF676"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VFX30T extends XilinxDevice {

		public XC5VFX30T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VFX30T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF665"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VLX30T extends XilinxDevice {

		public XC5VLX30T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VLX30T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF323"), new XilinxPackage("FF665"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VSX35T extends XilinxDevice {

		public XC5VSX35T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VSX35T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF665"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VLX50 extends XilinxDevice {

		public XC5VLX50(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VLX50";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF324"), new XilinxPackage("FF676"), new XilinxPackage("FF1153"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VLX50T extends XilinxDevice {

		public XC5VLX50T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VLX50T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1136"), new XilinxPackage("FF665"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VSX50T extends XilinxDevice {

		public XC5VSX50T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VSX50T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1136"), new XilinxPackage("FF665"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VFX70T extends XilinxDevice {

		public XC5VFX70T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VFX70T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1136"), new XilinxPackage("FF665"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VLX85 extends XilinxDevice {

		public XC5VLX85(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VLX85";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF676"), new XilinxPackage("FF1153"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VLX85T extends XilinxDevice {

		public XC5VLX85T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VLX85T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1136"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VSX95T extends XilinxDevice {

		public XC5VSX95T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VSX95T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1136"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VFX100T extends XilinxDevice {

		public XC5VFX100T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VFX100T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1738"), new XilinxPackage("FF1136"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VLX110 extends XilinxDevice {

		public XC5VLX110(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VLX110";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF676"), new XilinxPackage("FF1153"), new XilinxPackage("FF1760"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VLX110T extends XilinxDevice {

		public XC5VLX110T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VLX110T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1136"), new XilinxPackage("FF1738"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VFX130T extends XilinxDevice {

		public XC5VFX130T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VFX130T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1738"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VTX150T extends XilinxDevice {

		public XC5VTX150T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VTX150T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1156"), new XilinxPackage("FF1759"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VLX155 extends XilinxDevice {

		public XC5VLX155(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VLX155";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1153"), new XilinxPackage("FF1760"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VLX155T extends XilinxDevice {

		public XC5VLX155T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VLX155T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1136"), new XilinxPackage("FF1738"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-3"), new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VFX200T extends XilinxDevice {

		public XC5VFX200T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VFX200T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1738"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VLX220 extends XilinxDevice {

		public XC5VLX220(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VLX220";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1760"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VLX220T extends XilinxDevice {

		public XC5VLX220T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VLX220T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1738"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VSX240T extends XilinxDevice {

		public XC5VSX240T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VSX240T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1738"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VTX240T extends XilinxDevice {

		public XC5VTX240T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VTX240T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1759"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VLX330 extends XilinxDevice {

		public XC5VLX330(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VLX330";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1760"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	class XC5VLX330T extends XilinxDevice {

		public XC5VLX330T(XilinxFamily family) {
			super();
			_family = family;
			_deviceName = "XC5VLX330T";
			_validPackages = new XilinxPackage[] {new XilinxPackage("FF1738"), };
			_validSpeedGrades = new XilinxSpeedGrade[] {new XilinxSpeedGrade("-2"), new XilinxSpeedGrade("-1"), };
		}
	}

	public static synchronized XilinxV5Family getSingletonObject() {
		if (!_singletonCreated) {
			_singletonCreated = true;
			_singletonObject = new XilinxV5Family();
		}
		return _singletonObject;
	}

	public static void initializeFamily() {
		getSingletonObject();
	}

	private static boolean _initialized = false;
	private static boolean _singletonCreated = false;
	private static XilinxV5Family _singletonObject;
}
