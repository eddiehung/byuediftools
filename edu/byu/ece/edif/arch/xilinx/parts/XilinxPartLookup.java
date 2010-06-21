package edu.byu.ece.edif.arch.xilinx.parts;

import java.util.List;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.util.parse.EdifParser;

public class XilinxPartLookup {
	public static XilinxPart getPartFromPartName(String name) {
		XilinxFamily fam;
		XilinxDevice dev;
		XilinxPackage pkg;
		XilinxSpeedGrade sg;
		XilinxPart part;
		
		//first initialize the families
		if (!_initialized) {
			_initialized = true;
			initializeFamilies();
		}		
		List<XilinxFamily> allFamilies = XilinxFamily.getAllFamilies();
		
		name = name.toUpperCase();
        //handle non-commercial parts by changing the q or qr to c.
        name = name.replace("qr", "c");
        name = name.replace("q", "c");
		
        /*System.out.println("There are " + allFamilies.size() + 
        		" device families.");*/
        
        //First, find the family and the device
        fam = findFamily(name, allFamilies);
        dev = findDevice(name, fam);
        //parse out the package and speed grade strings
        XilinxPartNameTokens tokens = 
        	XilinxPartNameTokens.getTokensFromPartNameAndDevice(name, dev);
        //Finally, the package and speed grade
        pkg = findPackage(tokens.getPackageName(), dev);
        sg = findSpeedGrade(tokens.getSpeedGradeName(), dev);
        
		//construct the part with whatever information we have so far
		//if components are null, that's ok for now
		part = new XilinxPart(name, fam, dev, pkg, sg);
		return part;
	}
	
	private static XilinxFamily findFamily(String name, List<XilinxFamily> allFamilies) {
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

	private static XilinxDevice findDevice(String name, XilinxFamily fam) {
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
	
	private static XilinxPackage findPackage(String name, XilinxDevice dev) {
			XilinxPackage pkg = null;
		if (dev != null) {
			for(XilinxPackage p : dev.getValidPackages()) {
				if (name.indexOf(p.getPackageString().toUpperCase()) > -1) {
					//just in case a package name is contained within another
					if (pkg == null || 
							p.getPackageString().length() > pkg.getPackageString().length())
						pkg = p;
				}
			}
		}
		return pkg;
	}
	
	private static XilinxSpeedGrade findSpeedGrade(String name, XilinxDevice dev) {
		XilinxSpeedGrade sg = null;
		if (dev != null) {
			for(XilinxSpeedGrade s : dev.getValidSpeedGrades()) {
				if (name.indexOf(s.getSpeedGradeString().toUpperCase()) > -1) {
					//just in case a speed grade name is contained within another (like -1 and -12)
					if (sg == null || 
							s.getSpeedGradeString().length() > sg.getSpeedGradeString().length())
						sg = s;
				}
			}
		}
		return sg;
	}
	
	private static void initializeFamilies() {
		//TODO: may want a separate class to collect the XilinxFamilies
		//right now, there's a static List in XilinxFamily that holds them
		//all after this step. There is possibly a better approach.
		
		//initialize all device families here:
		XilinxVirtexFamily.initializeFamily();
		XilinxV4Family.initializeFamily();
		XilinxV5Family.initializeFamily();
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
	
	private static boolean _initialized = false;
	
}
