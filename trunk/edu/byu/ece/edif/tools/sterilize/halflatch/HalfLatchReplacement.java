package edu.byu.ece.edif.tools.sterilize.halflatch;

import java.util.ArrayList;

import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifCellInterface;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.EdifTypedValue;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.core.PropertyList;
import edu.byu.ece.edif.core.StringTypedValue;
import edu.byu.ece.edif.tools.sterilize.lutreplace.EdifEnvironmentCopyReplace;
import edu.byu.ece.edif.tools.sterilize.lutreplace.ReplacementContext;

public class HalfLatchReplacement {
	
	/** Enumerate all of the FF types */	
	public static final String FD_STRING 	 = 	"FD";
	public static final String FD_1_STRING 	 = 	"FD_1";
	public static final String FDC_STRING 	 = 	"FDC";
	public static final String FDC_1_STRING  = 	"FDC_1";
	public static final String FDCE_STRING 	 = 	"FDCE";
	public static final String FDCE_1_STRING = 	"FDCE_1";
	public static final String FDCP_STRING 	 = 	"FDCP";
	public static final String FDCP_1_STRING = 	"FDCP_1";
	public static final String FDE_STRING 	 = 	"FDE";
	public static final String FDE_1_STRING  = 	"FDE_1";
	public static final String FDP_STRING    = 	"FDP";
	public static final String FDP_1_STRING  = 	"FDP_1";
	public static final String FDPE_STRING   = 	"FDPE";
	public static final String FDPE_1_STRING = 	"FDPE_1";
	
	public enum FFType {FD, FD_1, FDC, FDC_1, FDCE, FDCE_1, FDCP, FDCP_1, FDE, FDE_1, FDP, FDP_1, FDPE, FDPE_1};

	public static FFType StringToFFType(String str) {
		if (str.equalsIgnoreCase(FD_STRING)) 	 return FFType.FD;
		if (str.equalsIgnoreCase(FD_1_STRING)) 	 return FFType.FD_1;
		if (str.equalsIgnoreCase(FDC_STRING)) 	 return FFType.FDC;
		if (str.equalsIgnoreCase(FDC_1_STRING))  return FFType.FDC_1;
		if (str.equalsIgnoreCase(FDCE_STRING)) 	 return FFType.FDCE;
		if (str.equalsIgnoreCase(FDCE_1_STRING)) return FFType.FDCE_1;
		if (str.equalsIgnoreCase(FDCP_STRING)) 	 return FFType.FDCP;
		if (str.equalsIgnoreCase(FDCP_1_STRING)) return FFType.FDCP_1;
		if (str.equalsIgnoreCase(FDE_STRING)) 	 return FFType.FDE;
		if (str.equalsIgnoreCase(FDE_1_STRING))  return FFType.FDE_1;
		if (str.equalsIgnoreCase(FDP_STRING)) 	 return FFType.FDP;
		if (str.equalsIgnoreCase(FDP_1_STRING))  return FFType.FDP_1;
		if (str.equalsIgnoreCase(FDPE_STRING)) 	 return FFType.FDPE;
		if (str.equalsIgnoreCase(FDPE_1_STRING)) return FFType.FDPE_1;
		return null;
	}
	
	public static void Replace(EdifLibraryManager libManager, EdifCell topCell, EdifEnvironment env, String ffType, EdifCell parent,
			String namePrefix, String INIT, EdifNet c, EdifNet d, EdifNet q, EdifNet pre, EdifNet ce, EdifNet clr, int ffCount) {
		FFType type = StringToFFType(ffType);
		if(type == null)
			return;
		Replace(libManager, topCell, env, type, parent, namePrefix, INIT, c, d, q, pre, ce, clr, ffCount);
	}
	
	
	public static void Replace(EdifLibraryManager libManager, EdifCell topCell, EdifEnvironment env, FFType ffType, EdifCell parent,
			String namePrefix, String INIT, EdifNet c, EdifNet d, EdifNet q, EdifNet pre, EdifNet ce, EdifNet clr, int ffCount) {
		
		/***** Step 1. Create/find Xilinx primitive cell needed for the replacement *****/
		EdifCell FDCPE = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FDCPE");
		
		/***** Step 2. Create FDCPE instance *****/
		// Add ports to the newly created FDCPE cell, but only once
		if(ffCount == 0) {
			try {
				FDCPE.addPort("Q", 1, 2);
				FDCPE.addPort("C", 1, 1);
				FDCPE.addPort("CE", 1, 1);
				FDCPE.addPort("CLR", 1, 1);
				FDCPE.addPort("D", 1, 1);
				FDCPE.addPort("PRE", 1, 1);
			} catch(InvalidEdifNameException e) {
				System.out.println("InvalidEdifNameException caught");
				System.exit(1);
			} catch (EdifNameConflictException e) {
				System.out.println("EdifNameConflictException caught");
				System.exit(1);
			}
		}
		// Create an FDCPE instance
		String fdcpeInstanceName = namePrefix + "_FDCPE_" + Integer.toString(ffCount);
		EdifNameable fdcpeInstanceNameable = NamedObject.createValidEdifNameable(fdcpeInstanceName);
		fdcpeInstanceNameable = parent.getUniqueInstanceNameable(fdcpeInstanceNameable);
		EdifCellInstance fdcpeInstance = new EdifCellInstance(fdcpeInstanceNameable, parent, FDCPE);
		try {
			parent.addSubCell(fdcpeInstance);
		} catch (EdifNameConflictException e) {
			// Should not get here
		}
		
		/***** Step 3. Add extra ports and nets for the constants *****/
		switch(ffType) {
		case FD: {
			CreatePresetPort(parent, topCell, env);
			CreateClrPort(parent, topCell, env);
			CreateCePort(parent, topCell, env);
			break;
		}
		case FD_1: {
			CreatePresetPort(parent, topCell, env);
			CreateClrPort(parent, topCell, env);
			CreateCePort(parent, topCell, env);
			break;
		}
		case FDC: {
			CreatePresetPort(parent, topCell, env);
			CreateCePort(parent, topCell, env);
			break;
		}
		case FDC_1: {
			CreatePresetPort(parent, topCell, env);
			CreateCePort(parent, topCell, env);
			break;
		}
		case FDCE: {
			CreatePresetPort(parent, topCell, env);
			break;
		}
		case FDCE_1: {
			CreatePresetPort(parent, topCell, env);
			break;
		}
		case FDCP: {
			CreateCePort(parent, topCell, env);
			break;
		}
		case FDCP_1: {
			CreateCePort(parent, topCell, env);
			break;
		}
		case FDE: {
			CreatePresetPort(parent, topCell, env);
			CreateClrPort(parent, topCell, env);
			break;
		}
		case FDE_1: {
			CreatePresetPort(parent, topCell, env);
			CreateClrPort(parent, topCell, env);
			break;
		}
		case FDP: {
			CreateClrPort(parent, topCell, env);
			CreateCePort(parent, topCell, env);
			break;
		}
		case FDP_1: {
			CreateClrPort(parent, topCell, env);
			CreateCePort(parent, topCell, env);
			break;
		}
		case FDPE: {
			CreateClrPort(parent, topCell, env);
			break;
		}
		case FDPE_1: {
			CreateClrPort(parent, topCell, env);
			break;
		}
		}
		
		System.out.println(parent);
		for(EdifPort port: parent.getPortList()) {
			System.out.println(port);
		}
		System.out.println("****************************************");
		
		/***** Step 4. Hook up corresponding nets and ports *****/
		EdifCellInterface fdcpeInterface = new EdifCellInterface(FDCPE);
		// Add ports to FDCPE interface
		EdifPort qPort = null;
		EdifPort cPort = null;
		EdifPort cePort = null;
		EdifPort clrPort = null;
		EdifPort dPort = null;
		EdifPort prePort = null;
		try {
			qPort = new EdifPort(fdcpeInterface, "Q", 1, 2);
			cPort = new EdifPort(fdcpeInterface, "C", 1, 1);
			cePort = new EdifPort(fdcpeInterface, "CE", 1, 1);
			clrPort = new EdifPort(fdcpeInterface, "CLR", 1, 1);
			dPort = new EdifPort(fdcpeInterface, "D", 1, 1);
			prePort = new EdifPort(fdcpeInterface, "PRE", 1, 1);
			fdcpeInterface.addPort("Q", 1, 1);
			fdcpeInterface.addPort("C", 1, 1);
			fdcpeInterface.addPort("CE", 1, 1);
			fdcpeInterface.addPort("CLR", 1, 1);
			fdcpeInterface.addPort("D", 1, 1);
			fdcpeInterface.addPort("PRE", 1, 1);
		} catch (EdifNameConflictException e) {
			System.out.println("EdifNameConflictException caught");
			System.exit(1);
		} catch (InvalidEdifNameException e) {
			System.out.println("InvalidEdifNameException caught");
			System.exit(1);
		}
		if (qPort == null) {
			System.err.println("Can't find Q port on cell " + FDCPE);
			System.exit(1);
		}
		if (cPort == null) {
			System.err.println("Can't find C port on cell " + FDCPE);
			System.exit(1);
		}
		if (cePort == null) {
			System.err.println("Can't find CE port on cell " + FDCPE);
			System.exit(1);
		}
		if (clrPort == null) {
			System.err.println("Can't find CLR port on cell " + FDCPE);
			System.exit(1);
		}
		if (dPort == null) {
			System.err.println("Can't find D port on cell " + FDCPE);
			System.exit(1);
		}
		if (prePort == null) {
			System.err.println("Can't find PRE port on cell " + FDCPE);
			System.exit(1);
		}
		// Get single bit ports and hook them up with nets
		EdifSingleBitPort qESBP = qPort.getSingleBitPort(0);
		EdifSingleBitPort cESBP = cPort.getSingleBitPort(0);
		EdifSingleBitPort ceESBP = cePort.getSingleBitPort(0);
		EdifSingleBitPort clrESBP = clrPort.getSingleBitPort(0);
		EdifSingleBitPort dESBP = dPort.getSingleBitPort(0);
		EdifSingleBitPort preESBP = prePort.getSingleBitPort(0);
		EdifPortRef qEPR = new EdifPortRef(q, qESBP, fdcpeInstance);
		EdifPortRef cEPR = new EdifPortRef(c, cESBP, fdcpeInstance);
		EdifPortRef ceEPR = new EdifPortRef(ce, ceESBP, fdcpeInstance);
		EdifPortRef clrEPR = new EdifPortRef(clr, clrESBP, fdcpeInstance);
		EdifPortRef dEPR = new EdifPortRef(d, dESBP, fdcpeInstance);
		EdifPortRef preEPR = new EdifPortRef(pre, preESBP, fdcpeInstance);
		q.addPortConnection(qEPR);
		c.addPortConnection(cEPR);
		//ce.addPortConnection(ceEPR);
		//clr.addPortConnection(clrEPR);
		d.addPortConnection(dEPR);
		//pre.addPortConnection(preEPR);
		

		
		/***** Step ?. Set INIT property *****/
		boolean isInit = false;
		StringTypedValue valueZero = new StringTypedValue("0");
        StringTypedValue valueOne = new StringTypedValue("1");
        PropertyList FDCPE_PropertyList = FDCPE.getPropertyList();
        if(FDCPE_PropertyList != null) {	// The property "INIT" already exists
        	for(Property FDCPE_Property: FDCPE_PropertyList.values()) {
        		if(FDCPE_Property.getName().equals("INIT")) {
        			isInit = true;
        			if(INIT.equals("0"))
        				FDCPE_Property.setValue(valueZero);
        			else
        				FDCPE_Property.setValue(valueOne);
        		}
        	}
        }
        if(!isInit) {
        	if(INIT.equals("0"))
        		fdcpeInstance.addProperty(new Property("INIT", (EdifTypedValue)valueZero));
        	else
        		fdcpeInstance.addProperty(new Property("INIT", (EdifTypedValue)valueOne));
        }
        
		
	}
	
	public static void CreatePresetPort(EdifCell parent, EdifCell topCell, EdifEnvironment env) {
		// Create a new port on parent's interface for PRESET signal
		EdifCellInterface parentInterface = parent.getInterface();
		String preConstantPortName = parent.getName() + "_PRE_CONSTANT_PORT";
		EdifNameable preConstantPortNameable = NamedObject.createValidEdifNameable(preConstantPortName);
		preConstantPortNameable = parent.getUniqueInstanceNameable(preConstantPortNameable);
		EdifPort preConstantPort = new EdifPort(parentInterface, preConstantPortNameable, 1, 1);
		
		// If the port does not exist, add it to parent's interface
		boolean port_flag = false;
		for(EdifPort port: parent.getPortList()) {
			if(port.equals(preConstantPort))
				port_flag = true;
		}
		if(port_flag == false) {
			try {
				parentInterface.addPort(preConstantPortNameable, 1, 1);
				System.out.println("port name: "+ preConstantPortNameable.toString());
				System.out.println("Add PRESET port on " + parent.toString());
			} catch (EdifNameConflictException e) {
				System.out.println("EdifNameConflictException caught");
				System.exit(1);
			}
		} else
			System.out.println("PRESET port already exists");
		
		System.out.println("parent: "+parent.toString());
		System.out.println("topCell: "+topCell.toString());
		// Add extra ports to all the higher-level cells
		while(!parent.toString().equals(topCell.toString())) {
			parent = GetParentCell(parent, topCell, env);
			CreatePresetPort(parent, topCell, env);
		}
		System.out.println("======================");
	}
	
	public static void CreateClrPort(EdifCell parent, EdifCell topCell, EdifEnvironment env) {
		// Create a new port on parent's interface for CLR signal
		EdifCellInterface parentInterface = parent.getInterface();
		String clrConstantPortName = parent.getName() + "_CLR_CONSTANT_PORT";
		EdifNameable clrConstantPortNameable = NamedObject.createValidEdifNameable(clrConstantPortName);
		clrConstantPortNameable = parent.getUniqueInstanceNameable(clrConstantPortNameable);
		EdifPort clrConstantPort = new EdifPort(parentInterface, clrConstantPortNameable, 1, 1);
		// If the port does not exist, add it to parent's interface
		boolean port_flag = false;
		for(EdifPort port: parent.getPortList()) {
			if(port.equals(clrConstantPort))
				port_flag = true;
		}
		if(port_flag == false) {
			try {
				parentInterface.addPort(clrConstantPortNameable, 1, 1);
				System.out.println("Add CLR port on " + parent.toString());
			} catch (EdifNameConflictException e) {
				System.out.println("EdifNameConflictException caught");
				System.exit(1);
			}
		} else
			System.out.println("CLRport already exists");

		System.out.println("parent: "+parent.toString());
		System.out.println("topCell: "+topCell.toString());
		// Add extra ports to all the higher-level cells
		while(!parent.toString().equals(topCell.toString())) {
			parent = GetParentCell(parent, topCell, env);
			CreateClrPort(parent, topCell, env);
		}
		System.out.println("======================");
	}
	
	public static void CreateCePort(EdifCell parent, EdifCell topCell, EdifEnvironment env) {
		// Create a new port on parent's interface for CE signal
		EdifCellInterface parentInterface = parent.getInterface();
		String ceConstantPortName = parent.getName() + "_CE_CONSTANT_PORT";
		EdifNameable ceConstantPortNameable = NamedObject.createValidEdifNameable(ceConstantPortName);
		ceConstantPortNameable = parent.getUniqueInstanceNameable(ceConstantPortNameable);
		EdifPort ceConstantPort = new EdifPort(parentInterface, ceConstantPortNameable, 1, 1);
		// If the port does not exist, add it to parent's interface
		boolean port_flag = false;
		for(EdifPort port: parent.getPortList()) {
			if(port.equals(ceConstantPort))
				port_flag = true;
		}
		if(port_flag == false) {
			try {
				parentInterface.addPort(ceConstantPortNameable, 1, 1);
				System.out.println("Add CE port on " + parent.toString());
			} catch (EdifNameConflictException e) {
				System.out.println("EdifNameConflictException caught");
				System.exit(1);
			}
		} else
			System.out.println("CE port already exists");

		System.out.println("parent: "+parent.toString());
		System.out.println("topCell: "+topCell.toString());
		// Add extra ports to all the higher-level cells
		while(!parent.toString().equals(topCell.toString())) {
			parent = GetParentCell(parent, topCell, env);
			CreateCePort(parent, topCell, env);
		}
		System.out.println("======================");
	}
	
	public static EdifCell GetParentCell(EdifCell currentCell, EdifCell topCell, EdifEnvironment env) {
		System.out.println("parent cell is not top level cell, finding parent's parents...");
//		ArrayList <EdifCell> currentCellArray = new ArrayList<EdifCell>();
//		currentCellArray.add(currentCell);
//		EdifEnvironmentCopyReplace ecr = null;
//		try {
//			ecr = new EdifEnvironmentCopyReplace(env, currentCellArray);
//		} catch (EdifNameConflictException e) {
//			System.err.println(e);
//			System.exit(1);
//		}
//		EdifCell parent = null;
//		System.out.println("replacement context: "+ecr.getReplacementContexts());
//		for(ReplacementContext context : ecr.getReplacementContexts()) {
//			parent = context.getNewParentCell();
//			System.out.println("parent found: "+parent.toString());
//		}
//		return parent;
		
		for(EdifCellInstance subCellInstance: topCell.getSubCellList()) {
			if(subCellInstance.getCellType().toString().equals(currentCell.toString())) {	// Found a match
				System.out.println("parent found: " + subCellInstance.getParent());
				return subCellInstance.getParent();
			}
		}
		
		// If no match is found, then call this method recursively to go through all the non-empty sub cells
		for(EdifCellInstance subCellInstance: topCell.getSubCellList()) {
			if(!subCellInstance.getCellType().getSubCellList().isEmpty()) {
				System.out.println("not empty");
				EdifCell parent  = GetParentCell(currentCell, subCellInstance.getCellType(), env);
				return parent;
			}
		}
		return null;
	}
}