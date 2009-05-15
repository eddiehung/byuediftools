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
import edu.byu.ece.edif.tools.sterilize.lutreplace.AbstractEdifEnvironmentCopyReplace;
import edu.byu.ece.edif.tools.sterilize.lutreplace.ReplacementContext;

/**
 * Incomplete method to remove all the half latches in a design
 * without flattening it.
 * Called by HalfLatchReplacer.java.
 * 
 * 
 * @author Yubo Li
 *
 */

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
	
	public static void Replace(AbstractEdifEnvironmentCopyReplace ecr, EdifLibraryManager libManager, EdifCell topCell, EdifEnvironment env, 
			String ffType, EdifCell parent, String namePrefix, String INIT, EdifNet c, EdifNet d, EdifNet q, EdifNet pre, EdifNet ce, 
			EdifNet clr) {
		FFType type = StringToFFType(ffType);
		if(type == null)
			return;
		Replace(ecr, libManager, topCell, env, type, parent, namePrefix, INIT, c, d, q, pre, ce, clr);
	}
	
	public static void Replace(AbstractEdifEnvironmentCopyReplace ecr, EdifLibraryManager libManager, EdifCell topCell, EdifEnvironment env, 
			FFType ffType, EdifCell parent, String namePrefix, String INIT, EdifNet c, EdifNet d, EdifNet q, EdifNet pre, EdifNet ce, 
			EdifNet clr) {
		
		/***** Step 1. Create/find Xilinx primitive cell needed for the replacement *****/
		EdifCell FDCPE = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FDCPE");
		
		/***** Step 2. Create FDCPE instance *****/
		// Create an FDCPE instance
		String fdcpeInstanceName = namePrefix + "_FDCPE";
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
			CreatePresetPort(parent, topCell, env, ecr);
			CreateClrPort(parent, topCell, env, ecr);
			CreateCePort(parent, topCell, env, ecr);
			break;
		}
		case FD_1: {
			CreatePresetPort(parent, topCell, env, ecr);
			CreateClrPort(parent, topCell, env, ecr);
			CreateCePort(parent, topCell, env, ecr);
			break;
		}
		case FDC: {
			CreatePresetPort(parent, topCell, env, ecr);
			CreateCePort(parent, topCell, env, ecr);
			break;
		}
		case FDC_1: {
			CreatePresetPort(parent, topCell, env, ecr);
			CreateCePort(parent, topCell, env, ecr);
			break;
		}
		case FDCE: {
			CreatePresetPort(parent, topCell, env, ecr);
			break;
		}
		case FDCE_1: {
			CreatePresetPort(parent, topCell, env, ecr);
			break;
		}
		case FDCP: {
			CreateCePort(parent, topCell, env, ecr);
			break;
		}
		case FDCP_1: {
			CreateCePort(parent, topCell, env, ecr);
			break;
		}
		case FDE: {
			CreatePresetPort(parent, topCell, env, ecr);
			CreateClrPort(parent, topCell, env, ecr);
			break;
		}
		case FDE_1: {
			CreatePresetPort(parent, topCell, env, ecr);
			CreateClrPort(parent, topCell, env, ecr);
			break;
		}
		case FDP: {
			CreateClrPort(parent, topCell, env, ecr);
			CreateCePort(parent, topCell, env, ecr);
			break;
		}
		case FDP_1: {
			CreateClrPort(parent, topCell, env, ecr);
			CreateCePort(parent, topCell, env, ecr);
			break;
		}
		case FDPE: {
			CreateClrPort(parent, topCell, env, ecr);
			break;
		}
		case FDPE_1: {
			CreateClrPort(parent, topCell, env, ecr);
			break;
		}
		}
		
		System.out.println(parent);
		for(EdifPort port: parent.getPortList()) {
			System.out.println(port);
		}
		System.out.println("****************************************");
		
		/***** Step 4. Hook up corresponding nets and ports *****/
		EdifCellInterface fdcpeInterface = FDCPE.getInterface();
		// Add ports to FDCPE interface
		EdifPort qPort = fdcpeInterface.getPort("Q");
		EdifPort cPort = fdcpeInterface.getPort("C");
		EdifPort cePort = fdcpeInterface.getPort("CE");
		EdifPort clrPort = fdcpeInterface.getPort("CLR");
		EdifPort dPort = fdcpeInterface.getPort("D");
		EdifPort prePort = fdcpeInterface.getPort("PRE");
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
	
	public static void CreatePresetPort(EdifCell parent, EdifCell topCell, EdifEnvironment env, AbstractEdifEnvironmentCopyReplace ecr) {
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
			parent = GetParentCell(parent, topCell, env, ecr);
			CreatePresetPort(parent, topCell, env, ecr);
		}
		System.out.println("======================");
	}
	
	public static void CreateClrPort(EdifCell parent, EdifCell topCell, EdifEnvironment env, AbstractEdifEnvironmentCopyReplace ecr) {
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
			parent = GetParentCell(parent, topCell, env, ecr);
			CreateClrPort(parent, topCell, env, ecr);
		}
		System.out.println("======================");
	}
	
	public static void CreateCePort(EdifCell parent, EdifCell topCell, EdifEnvironment env, AbstractEdifEnvironmentCopyReplace ecr) {
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
			parent = GetParentCell(parent, topCell, env, ecr);
			CreateCePort(parent, topCell, env, ecr);
		}
		System.out.println("======================");
	}
	
	public static EdifCell GetParentCell(EdifCell currentCell, EdifCell topCell, EdifEnvironment env, AbstractEdifEnvironmentCopyReplace ecr) {
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
//				for(ReplacementContext tempContext : ecr.getReplacementContexts()) {
//					System.out.println("1: "+tempContext.getNewParentCell().toString());
//					System.out.println("2: "+subCellInstance.getCellType().toString());
//					if(tempContext.getNewParentCell().toString().equals(subCellInstance.getCellType().toString())) {
//						System.out.println("parent found: " + subCellInstance.getParent());
//						return subCellInstance.getParent();
//					}
//				}
				System.out.println("parent found: " + topCell);
				return topCell;
			}
		}
		
		// If no match is found, then call this method recursively to go through all the non-empty sub cells
		for(EdifCellInstance subCellInstance: topCell.getSubCellList()) {
			if(!subCellInstance.getCellType().getSubCellList().isEmpty()) {
				System.out.println("not empty");
				EdifCell parent  = GetParentCell(currentCell, subCellInstance.getCellType(), env, ecr);
				return parent;
			}
		}
		return null;
	}
}