package edu.byu.ece.edif.arch.xilinx.parts;

import java.util.List;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.util.parse.EdifParser;

/**
 * Performs Xilinx partname string lookup. The primary method of this class is a static method
 * that returns a XilinxPart object from a String.
 */
public class XilinxPartLookup {

	/**
	 * Parses a String that contains a valid Xilinx partname and returns a XilinxPart object.
	 */
	public static XilinxPart getPartFromPartName(String name) {
		XilinxFamily fam;
		XilinxDevice dev;
		XilinxPackage pkg;
		XilinxSpeedGrade sg;
		XilinxPart part;
				        
        //get all pieces necessary to construct the part
        dev = getDeviceFromPartName(name);
        fam = dev.getFamily();
        pkg = getPackageFromPartName(name);
        sg = getSpeedGradeFromPartName(name);
        
		//construct the part with whatever information we have so far
		//if components are null, that's ok for now
		part = new XilinxPart(name, fam, dev, pkg, sg);
		return part;
	}
	
	public static XilinxFamily getFamilyFromPartName(String name) {
		name = formatPartName(name);
		List<XilinxFamily> allFamilies = XilinxFamily.getAllFamilies();
		XilinxFamily fam = null;
		for(XilinxFamily f : allFamilies) {
			if (name.indexOf(f.getPartNamePrefix()) > -1) {
				//just in case a part name prefix is contained within another (like XCV or XCV4)
				//it wouldn't cause a problem with the prefixes out now though
				if (fam == null || 
						f.getPartNamePrefix().length() > fam.getPartNamePrefix().length())
					fam = f;
			}
		}
		return fam;
	}

	public static XilinxDevice getDeviceFromPartName(String name) {
		name = formatPartName(name);
		XilinxFamily fam = getFamilyFromPartName(name);
		XilinxDevice dev = null;
		if (fam != null) {
			for(XilinxDevice d : fam.getDevices()) {
				//System.out.println("Trying device: " + d.getDeviceName());
				if (name.indexOf(d.getDeviceName()) > -1) {
					//just in case a device name is contained within another (like XCV100 and XCV1000)
					if (dev == null || 
							d.getDeviceName().length() > dev.getDeviceName().length())
						dev = d;
				}
			}
		}
		return dev;
	}
	
	public static XilinxPackage getPackageFromPartName(String name) {
		name = formatPartName(name);
		XilinxDevice dev = getDeviceFromPartName(name);
		XilinxPackage pkg = null;
		if (dev != null) {
			for(XilinxPackage p : dev.getValidPackages()) {
				if (name.indexOf(p.getPackageName().toUpperCase()) > -1) {
					//just in case a package name is contained within another
					if (pkg == null || 
							p.getPackageName().length() > pkg.getPackageName().length())
						pkg = p;
				}
			}
		}
		return pkg;
	}
	
	public static XilinxSpeedGrade getSpeedGradeFromPartName(String name) {
		name = formatPartName(name);
		XilinxDevice dev = getDeviceFromPartName(name);
		XilinxSpeedGrade sg = null;
		if (dev != null) {
			for(XilinxSpeedGrade s : dev.getValidSpeedGrades()) {
				if (name.indexOf(s.getSpeedGradeName().toUpperCase()) > -1) {
					//just in case a speed grade name is contained within another (like -1 and -12)
					if (sg == null || 
							s.getSpeedGradeName().length() > sg.getSpeedGradeName().length())
						sg = s;
				}
			}
		}
		return sg;
	}
	
	private static String formatPartName(String name) {
		name = name.toUpperCase();
        //handle non-commercial parts by changing the q or qr to c.
        name = name.replace("qr", "c");
        name = name.replace("q", "c");
        return name;
	}
	
	static {
		//TODO: may want a separate class to collect the XilinxFamilies
		//right now, there's a static List in XilinxFamily that holds them
		//all after this step. There is possibly a better approach.
		
		//initialize all device families here:
		//is there a way to determine which ones need to be done at runtime?
		XilinxSpartan2Family.initializeFamily();
		XilinxSpartan3Family.initializeFamily();
		XilinxSpartan6Family.initializeFamily();
		XilinxVirtexFamily.initializeFamily();
		XilinxV2Family.initializeFamily();
		XilinxV2ProFamily.initializeFamily();		
		XilinxV4Family.initializeFamily();
		XilinxV5Family.initializeFamily();
		XilinxV6Family.initializeFamily();		
	}
	
	public static void main(String[] args) {
		EdifEnvironment testEnv = null;
        String part_str = "";
        String[] files = {"/net/fpga1/users/whowes/cisco/bench_design/18stage_replicated_v5.edf",
        		"/net/fpga1/users/whowes/cisco/bench_design/18stage_replicated_v4.edf",
        		"/net/fpga1/users/whowes/cisco/bench_design/18stage_replicated_virtex.edf"};
        XilinxPart part;
        
        for(String file : files) {
	        //get an input file
	        try {
				testEnv = EdifParser.translate(file);
			} 
	        catch (Exception e) {
				e.printStackTrace();
			} 
			Property part_prop = testEnv.getTopDesign().getProperty("PART");
	        part_str = part_prop.getValue().toString();
	        System.out.println("Looking up part from part name: " + part_str);
	        part = XilinxPartLookup.getPartFromPartName(part_str);
	        System.out.println("Part found:\n----------------------");
	        System.out.println(part);
        }
	}	
}
