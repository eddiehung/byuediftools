package edu.byu.ece.edif.tools.replicate.nmr.xilinx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.byu.ece.edif.arch.xilinx.parts.XilinxDevice;
import edu.byu.ece.edif.arch.xilinx.parts.XilinxFamily;
import edu.byu.ece.edif.arch.xilinx.parts.XilinxPackage;
import edu.byu.ece.edif.arch.xilinx.parts.XilinxSpartan2Family;
import edu.byu.ece.edif.arch.xilinx.parts.XilinxSpartan3Family;
import edu.byu.ece.edif.arch.xilinx.parts.XilinxSpartan6Family;
import edu.byu.ece.edif.arch.xilinx.parts.XilinxV2Family;
import edu.byu.ece.edif.arch.xilinx.parts.XilinxV2ProFamily;
import edu.byu.ece.edif.arch.xilinx.parts.XilinxV4Family;
import edu.byu.ece.edif.arch.xilinx.parts.XilinxV5Family;
import edu.byu.ece.edif.arch.xilinx.parts.XilinxV6Family;
import edu.byu.ece.edif.arch.xilinx.parts.XilinxVirtexFamily;

/**
 * This class allows a DeviceUtilizationTracker class to be created for
 * any XilinxFamily. Resource information for each valid device/package 
 * pair is obtained from the Xilinx tool partgen, using the -v option.
 * 
 * @author whowes
 *
 */
public class XilinxFamilyDeviceUtilizationTrackerGenerator {
	
	/**
	 * Creates a Java file which defines a DeviceUtilizationTracker class
	 * for the specified XilinxFamily 
	 * 
	 * @param fam XilinxFamily for which the class should be created
	 * @param filePath Path to the location in which the file should be created
	 */
	protected void generateFamilyDeviceUtilizationTracker(XilinxFamily fam, String filePath) {
		Map<String, XilinxDeviceResourceCount> nameToResources = new TreeMap<String, XilinxDeviceResourceCount>();
		
		for(XilinxDevice dev : fam.getDevices()) {
			for(XilinxPackage pkg : dev.getValidPackages()) {
				String devPkgName = dev.getDeviceName() + pkg.getPackageName();
				String partgenOutput = generatePartgenOutput(devPkgName); 
				nameToResources.put(devPkgName, parsePartgenOutput(partgenOutput, fam));
			}
		}
		String fileName = filePath + "Xilinx" + fam.getFamilyName() + "DeviceUtilizationTracker.java";
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(fileName));
            createFileHeader(buf);
            buf.write("package edu.byu.ece.edif.tools.replicate.nmr.xilinx;\n\n");
            buf.write("import edu.byu.ece.edif.arch.xilinx.parts.XilinxPartLookup;\n");
            buf.write("import edu.byu.ece.edif.core.EdifCell;\n");
            buf.write("import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationEstimatedStopException;\n");
            buf.write("import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationHardStopException;\n\n");
            buf.write("public class Xilinx" + fam.getFamilyName() + "DeviceUtilizationTracker extends XilinxDeviceUtilizationTracker {\n\n");
          
            //constructors
            buf.write("\tpublic Xilinx" + fam.getFamilyName() + "DeviceUtilizationTracker(EdifCell cell, String part)\n");
            buf.write("\t\tthrows OverutilizationEstimatedStopException, OverutilizationHardStopException, IllegalArgumentException {\n");
            buf.write("\t\t\tthis(cell, part, DEFAULT_MERGE_FACTOR, DEFAULT_OPTIMIZATION_FACTOR, DEFAULT_DESIRED_UTILIZATION_FACTOR);\n");
            buf.write("\t}\n\n");
            buf.write("\tpublic Xilinx" + fam.getFamilyName() + "DeviceUtilizationTracker(EdifCell cell, String part, double mergeFactor,\n");
            buf.write("\t\t\tdouble optimizationFactor, double desiredUtilizationFactor) throws OverutilizationEstimatedStopException,\n");
            buf.write("\t\t\tOverutilizationHardStopException, IllegalArgumentException {\n\n");
            buf.write("\t\tsuper(mergeFactor, optimizationFactor, desiredUtilizationFactor);\n");
            buf.write("\t\tpart = XilinxPartLookup.getPartFromPartName(part).getPartNameNoSpeedGrade();\n\n");
            
            boolean first = true;
            for(String dev : nameToResources.keySet()) {
            	XilinxDeviceResourceCount xdrc = nameToResources.get(dev);
            	if(first) {
            		buf.write("\t\tif (part.compareToIgnoreCase(\"" + dev + "\") == 0) {\n");
            		first = false;
            	}
            	else {
            		buf.write("\t\telse if (part.compareToIgnoreCase(\"" + dev + "\") == 0) {\n");
            	}
            	buf.write("\t\t\t_init(cell, ");
            	buf.write(xdrc._numLUTs + ", ");
            	buf.write(xdrc._numFFs + ", ");
            	buf.write(xdrc._numBRAMs + ", ");
            	buf.write(xdrc._numBUFGs + ", ");
            	buf.write(xdrc._numMults + ", ");
            	buf.write(xdrc._numDCMs + ", ");
            	buf.write(xdrc._numIOs + ", ");
            	buf.write(xdrc._numDSPs + ", ");
            	buf.write(xdrc._numPPCs + ", ");
            	buf.write(xdrc._numEMACs + ", ");
            	buf.write(xdrc._numMGTs + ", ");
            	buf.write(xdrc._numPCIes + ", ");
            	buf.write(xdrc._numICAPs + ", ");
            	buf.write(xdrc._numFrameECCs + ", ");
            	buf.write(xdrc._numPLLs + "");
            	buf.write(");\n\t\t}\n");
            }
            buf.write("\t\telse {\n");
            buf.write("\t\t\tthrow new IllegalArgumentException(\"Part name \" + part\n");
            buf.write("\t\t\t\t+ \" does not match the specified Xilinx " + fam.getFamilyName() + " technology group.\");\n");
            buf.write("\t\t}\n\t}\n\n");
            buf.write("\tprotected void _init(EdifCell cell, int maxLUTs, int maxFFs, int maxBlockRAMs, int maxBUFG, int maxMult,\n");
            buf.write("\t\t\tint maxDCM, int maxIO, int maxDSPs, int maxPPC, int maxEthernet, int maxMGT, int maxPCIe, int maxICAP,\n");
            buf.write("\t\t\tint maxFrameECC, int maxPLL) throws OverutilizationEstimatedStopException,\n");
            buf.write("\t\t\tOverutilizationHardStopException, IllegalArgumentException {\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.LUT, 0.0, maxLUTs);\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.FF, 0.0, maxFFs);\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.BRAM, 0.0, maxBlockRAMs);\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.MULT, 0.0, maxMult);\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.DCM, 0.0, maxDCM);\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.IO, 0.0, maxIO);\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.RES, 0.0, maxIO); // One per IOB\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.BUFG, 0.0, maxBUFG);\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.IBUFG, 0.0, maxBUFG);\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.DSP, 0.0, maxDSPs);\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.ICAP, 0.0, maxICAP);\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.FRAME_ECC, 0.0, maxFrameECC);\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.PPC, 0.0, maxPPC);\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.ETHERNET, 0.0, maxEthernet);\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.TRANSCEIVER, 0.0, maxMGT);\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.PCIE, 0.0, maxPCIe);\n");
            buf.write("\t\taddResourceForTracking(XilinxResourceMapper.PLL, 0.0, maxPLL);\n");
            buf.write("\t\tsuper._init(cell);\n");
            buf.write("\t}\n}\n");
            
            buf.flush();
		}
		catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error writing file: " + fileName);
            System.exit(1);
		}
	}
	
	/**
	 * Creates the header to notify users that the DeviceUtilizationTracker
	 * class is auto-generated
	 * 
	 * @param writer
	 * @throws IOException
	 */
    public void createFileHeader(Writer writer) throws IOException {
        writer.write("/*\n");
        writer.write(" * This file was auto-generated on " + (new Date()).toString() + "\n");
        writer.write(" * by " + this.getClass().getCanonicalName() + ".\n");
        writer.write(" * See the source code to make changes.\n *\n");
        writer.write(" * Do not modify this file directly.\n");
        writer.write(" */\n\n\n");
    }
	
    
    /**
     * Given the textual output from partgen -v for a device/package pair,
     * return a XilinxDeviceResourceCount object that summarizes the 
     * resources available in that device/package.
     * 
     * Some family-specific interpretation of the partgen output is 
     * necessary due to the use of 6-input LUTs in newer Xilinx families.
     * 
     * @param partgenOutput output from partgen as a String
     * @param fam XilinxFamily to which the device/package pair belongs
     * @return a filled-in XilinxDeviceResourceCount object
     */
	public static XilinxDeviceResourceCount parsePartgenOutput(String partgenOutput, XilinxFamily fam) {
		int numCLB = getValueFromPartgen(partgenOutput, PARTGEN_NUM_CLB);
		int numSlices = numCLB * getValueFromPartgen(partgenOutput, PARTGEN_NUM_SLICES_PER_CLB);
		int numLUTsPerSlice = getValueFromPartgen(partgenOutput, PARTGEN_NUM_LUT_PER_SLICE);
		if(fam.getFamilyName().equalsIgnoreCase("Virtex5") 
				|| fam.getFamilyName().equalsIgnoreCase("Virtex6")
				|| fam.getFamilyName().equalsIgnoreCase("Spartan6")) {
			//partgen output here is misleading, as the number of LUTs/slice
			//is actually double the number of 6-input LUTs/slice (since a
			//6-input LUT is basically 2 5-input LUTs, see libraries guide)
			numLUTsPerSlice /= 2;
		}
		
		int numLUTs = numSlices * numLUTsPerSlice;	
		int numFFs = getValueFromPartgen(partgenOutput, PARTGEN_NUM_FF_PER_SLICE) * numSlices;
		int numBRAMs = getValueFromPartgen(partgenOutput, PARTGEN_NUM_BRAM);
		int numMults = getValueFromPartgen(partgenOutput, PARTGEN_NUM_MULT);
		int numDSPs = getValueFromPartgen(partgenOutput, PARTGEN_NUM_DSP);
		int numPPCs = getValueFromPartgen(partgenOutput, PARTGEN_NUM_PPC);
		int numEMACs = getValueFromPartgen(partgenOutput, PARTGEN_NUM_EMAC);
		if(numEMACs == 0) { //V4/V5 have a different entry
			numEMACs = getValueFromPartgen(partgenOutput, PARTGEN_NUM_EMAC_NEW);
		}
		int numPCIes = getValueFromPartgen(partgenOutput, PARTGEN_NUM_PCIE);
		int numMGTs = getValueFromPartgen(partgenOutput, PARTGEN_NUM_MGT);
		int numDCMs = getValueFromPartgen(partgenOutput, PARTGEN_NUM_DCM);
		int numIOs = getValueFromPartgen(partgenOutput, PARTGEN_NUM_IO);
		int numBUFGs = getValueFromPartgen(partgenOutput, PARTGEN_NUM_BUFG);
		int numPLLs = getValueFromPartgen(partgenOutput, PARTGEN_NUM_PLL);    
		
		//TODO: hard-coded right now, is this accurate?
		int numICAPs = 1;
		int numFrameECCs = 1;
		
		return new XilinxDeviceResourceCount(numLUTs, numFFs, numBRAMs, numMults,
				numDSPs, numPPCs, numEMACs, numPCIes, numMGTs, numDCMs, numIOs, numBUFGs,
				numICAPs, numFrameECCs, numPLLs);
	}
	
	/**
	 * Helper method to find a certain value from the partgen output.
	 * 
	 * @param partgen String containing input from partgen
	 * @param target String to find within the partgen output
	 * @return the int value requested
	 */
	public static int getValueFromPartgen(String partgen, String target) {
		int retVal;
		int index = partgen.indexOf(target);
		if(index>=0) {
			String valString = "";
			index = index + target.length() + 1; //start right after '='
			while(partgen.charAt(index) != ' ') {
				valString += partgen.charAt(index++);
			}
			retVal = Integer.parseInt(valString);
		}
		else
			retVal = 0;
		return retVal;
	}
	
	/**
	 * Runs Xilinx partgen -v to obtain resource information for the
	 * specified device/package pair.
	 * 
	 * Note that partgen creates a number of different files (.pkg,
	 * partlist.xct, partlist.xct) which this method DOES NOT clean
	 * up.
	 * 
	 * @param partname A String formatted as device+package, passed into
	 * partgen as the -v argument.
	 * 
	 * @return A String containing the text of partlist.xct, 
	 * which is created by partgen -v and contains resource information
	 * for the device/package pair specified by the -v argument.
	 */
    protected static String generatePartgenOutput(String partname){
        BufferedReader input;
        Process p;
        String line;
        String output = "";
        try {
            p = Runtime.getRuntime().exec("partgen -v " + partname.toLowerCase());
            p.waitFor();
            p.destroy();
            input = new BufferedReader(new FileReader("partlist.xct"));
            while((line = input.readLine()) != null){
                output = output + "\n" + line;
            }
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("There was an error running partgen with partname:"+partname);
            System.err.println("Check that Xilinx tools are on your path and that the version supports the target family.");
            System.err.println("Also, make sure " + partname + " is a valid Xilinx device/package.");
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return output;
    }
    
	public static void main(String[] args) {
    	//TODO: determine the user's version of the tools automatically
		List<XilinxFamily> families = new ArrayList<XilinxFamily>();
		XilinxFamilyDeviceUtilizationTrackerGenerator gen = new XilinxFamilyDeviceUtilizationTrackerGenerator();
		
    	if (args.length < 1 || args[0] == null) {
    		System.out.println("Usage: XilinxDeviceUtilizationTrackerGenerator toolMajorVersion");
    		System.exit(0);
    	}
    	String toolMajorVersion = args[0];
    	if (toolMajorVersion.equalsIgnoreCase("10")) {
    		families.add(XilinxVirtexFamily.getSingletonObject());
    		families.add(XilinxV2Family.getSingletonObject());
    		families.add(XilinxSpartan2Family.getSingletonObject());
    		families.add(XilinxV2ProFamily.getSingletonObject());
    	}
    	if(toolMajorVersion.equalsIgnoreCase("12")) {
    		families.add(XilinxV4Family.getSingletonObject());
    		families.add(XilinxV5Family.getSingletonObject());
    		families.add(XilinxV6Family.getSingletonObject());
    		families.add(XilinxSpartan3Family.getSingletonObject());
    		families.add(XilinxSpartan6Family.getSingletonObject());
    	}
    	for(XilinxFamily fam : families) {
    		//put in current working directory
    		gen.generateFamilyDeviceUtilizationTracker(fam, "");
    		System.out.println("Done for " + fam.getFamilyName());
    	}
	}
    
    public static final String PARTGEN_NUM_LUT_PER_SLICE = "NUM_LUTS_PER_SLICE";
    public static final String PARTGEN_NUM_FF_PER_SLICE = "FFS_PER_SLICE";
    public static final String PARTGEN_NUM_SLICES_PER_CLB = "SLICES_PER_CLB";
    public static final String PARTGEN_NUM_CLB = "NUM_CLB";
    public static final String PARTGEN_NUM_BRAM = "NUM_BLK_RAMS";
    public static final String PARTGEN_NUM_DSP = "NUM_DSP";
    public static final String PARTGEN_NUM_MULT = "NUM_MULT";
    public static final String PARTGEN_NUM_PPC = "NUM_PPC";
    public static final String PARTGEN_NUM_EMAC = "NUM_EMAC";
    public static final String PARTGEN_NUM_EMAC_NEW = "NUM_TEMAC";
    public static final String PARTGEN_NUM_PCIE = "NUM_PCIE";
    public static final String PARTGEN_NUM_MGT = "NUM_GT";
    public static final String PARTGEN_NUM_DCM = "NUM_DCM_OR_DLL";
    public static final String PARTGEN_NUM_IO = "NBIOBS";
    public static final String PARTGEN_NUM_BUFG = "NUM_GLOBAL_BUFFERS";
    public static final String PARTGEN_NUM_PLL = "NUM_PLL";
}

/**
 * This class is a container for available resources in a a Xilinx 
 * device/package pair. It is primarily used for convenience reasons
 * when generating DeviceUtilizationTracker Java files.
 * 
 * @author whowes
 *
 */
class XilinxDeviceResourceCount {
	public XilinxDeviceResourceCount(int numLUTs, int numFFs, int numBRAMs, int numMults,
			int numDSPs, int numPPCs, int numEMACs, int numPCIes, int numMGTs,
			int numDCMs, int numIOs, int numBUFGs, int numICAPs, int numFrameECCs,
			int numPLLs) {		
		_numLUTs = numLUTs;
		_numFFs = numFFs;
		_numBRAMs = numBRAMs;
		_numMults = numMults;
		_numDSPs = numDSPs;
		_numPPCs = numPPCs;
		_numEMACs = numEMACs;
		_numPCIes = numPCIes;
		_numMGTs = numMGTs;
		_numDCMs = numDCMs;
		_numIOs = numIOs;
		_numBUFGs = numBUFGs;	
		_numICAPs = numICAPs;
		_numFrameECCs = numFrameECCs;
		_numPLLs = numPLLs;
	}
	
	public int _numLUTs;
	public int _numFFs;
	public int _numBRAMs;
	public int _numMults;
	public int _numDSPs;
	public int _numPPCs;
	public int _numEMACs;
	public int _numPCIes;
	public int _numMGTs;
	public int _numDCMs;
	public int _numIOs;
	public int _numBUFGs;
	public int _numICAPs;
	public int _numFrameECCs;
	public int _numPLLs;
	
	public String toString() {
		String retVal = "";
		retVal += "LUTs: " + _numLUTs;
		retVal += "\nFFs: " + _numFFs;
		retVal += "\nBRAMs: " + _numBRAMs;
		retVal += "\nMultipliers: " + _numMults;
		retVal += "\nDSPs: " + _numDSPs;
		retVal += "\nPPCs: " + _numPPCs;
		retVal += "\nEthernet: " + _numEMACs;
		retVal += "\nPCI Express: " + _numPCIes;
		retVal += "\nMGTs: " + _numMGTs;
		retVal += "\nDCM/DLL: " + _numDCMs;
		retVal += "\nIO: " + _numIOs;
		retVal += "\nBUFG: " + _numBUFGs;
		retVal += "\n";
		return retVal;
	}
}
