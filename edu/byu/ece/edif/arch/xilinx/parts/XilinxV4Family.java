package edu.byu.ece.edif.arch.xilinx.parts;

// Auto generate this for all parts
public class XilinxV4Family implements XilinxFamily {

	// XCVLX15
	public static String XC4VLX15_NAME = "LX15";
	public static XilinxPackage XC4VLX15_PACKAGES[] = new XilinxPackage[] {
		XilinxValidPackages.SF363, XilinxValidPackages.FF668
	};
	public static XilinxSpeedGrade XC4VLX15_SPEEDGRADES[] = new XilinxSpeedGrade[] {
		XilinxValidSpeedGrades.SPEED_1, XilinxValidSpeedGrades.SPEED_2
	};
	public static XilinxDevice XC4VLX15 = new XilinxDevice(this, XC4VLX15_NAME, XC4VLX15_PACKAGES, XC4VLX15_SPEEDGRADES);
	
	// From old code:
    String archName = "xc4v";
    String[] devNames = { "lx15", "lx25", "lx40", "lx60", "lx80", "lx100", "lx160", "lx200", "sx25", "sx35",
            "sx55", "fx12", "fx20", "fx40", "fx60", "fx100", "fx140" };

    String[] xc4vlx15Package = { "SF363", "FF668" };
    String[] xc4vlx25Package = { "SF363", "FF668" };
    String[] xc4vlx40Package = { "FF668", "FF1148" };
    String[] xc4vlx60Package = { "FF668", "FF1148" };
    String[] xc4vlx80Package = { "FF1148" };
    String[] xc4vlx100Package = { "FF1148", "FF1513" };
    String[] xc4vlx160Package = { "FF1148", "FF1513" };
    String[] xc4vlx200Package = { "FF1513" };

    String[] xc4vsx25Package = { "FF668" };
    String[] xc4vsx35Package = { "FF668" };
    String[] xc4vsx55Package = { "FF1148" };

    String[] xc4vfx12Package = { "SF363", "FF668" };
    String[] xc4vfx20Package = { "FF672" };
    String[] xc4vfx40Package = { "FF672", "FF1152" };
    String[] xc4vfx60Package = { "FF672", "FF1152" };
    String[] xc4vfx100Package = { "FF1152", "FF1517" };
    String[] xc4vfx140Package = { "FF1517", "FF1760" };

	
}
