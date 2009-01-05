package edu.byu.ece.edif.tools.sterilize.halflatch;

import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
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

/**
 * Removes half latches in a Xilinx design. The following primitives 
 * are replaced:<br>
 * <ul>
 *   <li> FD
 *   <li> FD_1
 *   <li> FDC
 *   <li> FDC_1
 *   <li> FDCE
 *   <li> FDCE_1
 *   <li> FDCP
 *   <li> FDCP_1
 *   <li> FDE
 *   <li> FDE_1
 *   <li> FDP
 *   <li> FDP_1
 *   <li> FDPE
 *   <li> FDPE_1
 * </ul>
 * 
 * @author Yubo Li
 */
public class HalfLatchRemove {
	
	/** Enumerations of all primitives. These strings are used for matching
	 * the strings in the original EDIF.
	 * @see StringToSRLType 
	 **/
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
	
	/**
	 * An enumerated type that represent each individual primitive. 
	 */
	public enum FFType {FD, FD_1, FDC, FDC_1, FDCE, FDCE_1, FDCP, FDCP_1, FDE, FDE_1, FDP, FDP_1, FDPE, FDPE_1};

	/**
	 * Compares a string against all the primitive strings while ignoring
	 * case.
	 * @return An FFType object representing the primitive that was matched. Returns
	 * a null if no match occurs.
	 */
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
	
	public static void Remove(EdifLibraryManager libManager, String ffType, EdifCell parent,
			boolean lut, String namePrefix, String INIT, EdifNet c, EdifNet d, EdifNet q, EdifNet pre, EdifNet ce, EdifNet clr) {
		FFType type = StringToFFType(ffType);
		if(type == null)
			return;
		Remove(libManager, type, parent, lut, namePrefix, INIT, c, d, q, pre, ce, clr);
	}
	
	/**
	 * Performs the half latch removal algorithm. This method only operates on a single
	 * EdifCell object. This method assumes that a new EdifCell has been created and
	 * there is a "hole" where a previously used FF primitive was used. It is also assumed
	 * that the design is already flattened before half latch removal begins. All the
	 * surrounding logic and nets are created and passed to this method so the
	 * new FDCPE instance can be hooked up. 
	 * 
	 * It is called by the
	 * edu.byu.ece.edif.tools.sterilize.halflatch.HalfLatchRemoval class.
	 * 
	 * @param libManager The library manager of this new environment
	 * @param ffType The FF type to replace.
	 * @param parent The parent EdifCell that contains the replaced cell
	 * @param lut A flag to indicate which constant value mode is chosen. Set to be true
	 *                   by default.
	 * @param namePrefix This is the String prefix used to create a new name for all of
	 *                   the replacement cells.
	 * @param INIT Init value used when determining which cells to create for initialization.
	 * @param c
	 * @param d
	 * @param q
	 * @param pre
	 * @param ce
	 * @param clr
	 */
	public static void Remove(EdifLibraryManager libManager, FFType ffType, EdifCell parent, 
			boolean lut, String namePrefix, String INIT, EdifNet c, EdifNet d, EdifNet q, EdifNet pre, EdifNet ce, EdifNet clr) {
		
		/***** Step 1. Create/find Xilinx primitive cell needed for the replacement *****/
		EdifCell FDCPE = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FDCPE");
		
		/***** Step 2. Create FDCPE instance *****/
		// Create an FDCPE instance and add it to the design
		String fdcpeInstanceName = namePrefix + "_SAFE_FDCPE";
		EdifNameable fdcpeInstanceNameable = NamedObject.createValidEdifNameable(fdcpeInstanceName);
		fdcpeInstanceNameable = parent.getUniqueInstanceNameable(fdcpeInstanceNameable);
		EdifCellInstance fdcpeInstance = new EdifCellInstance(fdcpeInstanceNameable, parent, FDCPE);
		try {
			parent.addSubCell(fdcpeInstance);
		} catch (EdifNameConflictException e) {
			// Should not get here
		}
		
		/***** Step 3. Create ports and nets for the constant values *****/
		switch(ffType) {
		case FD: {
			if(lut) {
				CreateConstantZeroNet(parent);
				CreateConstantOneNet(parent);
			} else {
				CreateConstantZeroPort(parent);
				CreateConstantOnePort(parent);
				CreateConstantZeroNet(parent);
				CreateConstantOneNet(parent);
			}
			break;
		}
		case FD_1: {
			if(lut) {
				CreateConstantZeroNet(parent);
				CreateConstantOneNet(parent);
			} else {
				CreateConstantZeroPort(parent);
				CreateConstantOnePort(parent);
				CreateConstantZeroNet(parent);
				CreateConstantOneNet(parent);
			}
			break;
		}
		case FDC: {
			if(lut) {
				CreateConstantZeroNet(parent);
				CreateConstantOneNet(parent);
			} else {
				CreateConstantZeroPort(parent);
				CreateConstantOnePort(parent);
				CreateConstantZeroNet(parent);
				CreateConstantOneNet(parent);
			}
			break;
		}
		case FDC_1: {
			if(lut) {
				CreateConstantZeroNet(parent);
				CreateConstantOneNet(parent);
			} else {
				CreateConstantZeroPort(parent);
				CreateConstantOnePort(parent);
				CreateConstantZeroNet(parent);
				CreateConstantOneNet(parent);
			}
			break;
		}
		case FDCE: {
			if(lut) {
				CreateConstantZeroNet(parent);
			} else {
				CreateConstantZeroPort(parent);
				CreateConstantZeroNet(parent);
			}
			break;
		}
		case FDCE_1: {
			if(lut) {
				CreateConstantZeroNet(parent);
			} else {
				CreateConstantZeroPort(parent);
				CreateConstantZeroNet(parent);
			}
			break;
		}
		case FDCP: {
			if(lut) {
				CreateConstantOneNet(parent);
			} else {
				CreateConstantOnePort(parent);
				CreateConstantOneNet(parent);
			}
			break;
		}
		case FDCP_1: {
			if(lut) {
				CreateConstantOneNet(parent);
			} else {
				CreateConstantOnePort(parent);
				CreateConstantOneNet(parent);
			}
			break;
		}
		case FDE: {
			if(lut) {
				CreateConstantZeroNet(parent);
			} else {
				CreateConstantZeroPort(parent);
				CreateConstantZeroNet(parent);
			}
			break;
		}
		case FDE_1: {
			if(lut) {
				CreateConstantZeroNet(parent);
			} else {
				CreateConstantZeroPort(parent);
				CreateConstantZeroNet(parent);
			}
			break;
		}
		case FDP: {
			if(lut) {
				CreateConstantZeroNet(parent);
				CreateConstantOneNet(parent);
			} else {
				CreateConstantZeroPort(parent);
				CreateConstantOnePort(parent);
				CreateConstantZeroNet(parent);
				CreateConstantOneNet(parent);
			}
			break;
		}
		case FDP_1: {
			if(lut) {
				CreateConstantZeroNet(parent);
				CreateConstantOneNet(parent);
			} else {
				CreateConstantZeroPort(parent);
				CreateConstantOnePort(parent);
				CreateConstantZeroNet(parent);
				CreateConstantOneNet(parent);
			}
			break;
		}
		case FDPE: {
			if(lut) {
				CreateConstantZeroNet(parent);
			} else {
				CreateConstantZeroPort(parent);
				CreateConstantZeroNet(parent);
			}
			break;
		}
		case FDPE_1: {
			if(lut) {
				CreateConstantZeroNet(parent);
			} else {
				CreateConstantZeroPort(parent);
				CreateConstantZeroNet(parent);
			}
			break;
		}
		}
		
		/***** Step 4. Connect ports and nets *****/
		// String values specifying the INIT value
		StringTypedValue valueZero = new StringTypedValue("0");
        StringTypedValue valueOne = new StringTypedValue("1");
        // Get interfaces of instances
		EdifCellInterface fdcpeInterface = FDCPE.getInterface();
		EdifCellInterface parentInterface = parent.getInterface();	
		// Get ports of the FDCPE instance
		EdifPort qPort = fdcpeInterface.getPort("Q");
		EdifPort cPort = fdcpeInterface.getPort("C");
		EdifPort cePort = fdcpeInterface.getPort("CE");
		EdifPort clrPort = fdcpeInterface.getPort("CLR");
		EdifPort dPort = fdcpeInterface.getPort("D");
		EdifPort prePort = fdcpeInterface.getPort("PRE");
		// Get ports on parent for constant values
		String constantZeroPortName = parent.getName() + "_CONSTANT_ZERO_PORT";
		String constantOnePortName = parent.getName() + "_CONSTANT_ONE_PORT";
		String constantZeroNetName = parent.getName() + "_CONSTANT_ZERO_NET";
		String constantOneNetName = parent.getName() + "_CONSTANT_ONE_NET";
		EdifNet constantZeroNet = parent.getNet(constantZeroNetName);
		EdifNet constantOneNet = parent.getNet(constantOneNetName);
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
		
		// Get single-bit ports
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
		
		// Connect nets and ports according to the constant value mode
		if(lut) {	// Constant values are provided by LUT
			EdifCell LUT1 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "LUT1");
			EdifCell INV = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "INV");
			
			// Create a LUT1 instance and add it to parent
			String lutInstanceName = parent.getName() + "_CONSTANT_VALUE_LUT";
			EdifCellInstance lutInstance = null;
			boolean lut_flag = false;
			for(EdifCellInstance subInstance : parent.getCellInstanceList()) {
				if(subInstance.getName().equals(lutInstanceName)) {
					lut_flag = true;
					break;
				}
			}
			if(lut_flag) {
				lutInstance = parent.getInstance(lutInstanceName);
			} else {
				EdifNameable lutInstanceNameable = NamedObject.createValidEdifNameable(lutInstanceName);
				lutInstanceNameable = parent.getUniqueInstanceNameable(lutInstanceNameable);
				lutInstance = new EdifCellInstance(lutInstanceNameable, parent, LUT1);
				try {
					parent.addSubCell(lutInstance);
				} catch (EdifNameConflictException e) {
					System.out.println("EdifNameConflictException caught.");
					System.exit(1);
				}
				lutInstance.addProperty(new Property("INIT", (EdifTypedValue)valueZero));
			}
			
			// Create an INV instance and add it to parent
			String invInstanceName = parent.getName() + "_CONSTANT_VALUE_INV";
			EdifCellInstance invInstance = null;
			boolean inv_flag = false;
			for(EdifCellInstance subInstance : parent.getCellInstanceList()) {
				if(subInstance.getName().equals(invInstanceName)) {
					inv_flag = true;
					break;
				}
			}
			if(inv_flag) {
				invInstance = parent.getInstance(invInstanceName);
			} else {
				EdifNameable invInstanceNameable = NamedObject.createValidEdifNameable(invInstanceName);
				invInstanceNameable = parent.getUniqueInstanceNameable(invInstanceNameable);
				invInstance = new EdifCellInstance(invInstanceNameable, parent, INV);
				try {
					parent.addSubCell(invInstance);
				} catch (EdifNameConflictException e) {
					System.out.println("EdifNameConflictException caught.");
					System.exit(1);
				}
			}
			
			// Get interfaces, ports, single-bit ports, and port references of LUT1 and INV
			EdifCellInterface lutInterface = LUT1.getInterface();
			EdifCellInterface invInterface = INV.getInterface();
			EdifPort lutOPort = lutInterface.getPort("O");
			EdifPort invIPort = invInterface.getPort("I");
			EdifPort invOPort = invInterface.getPort("O");
			EdifSingleBitPort lutOESBP = lutOPort.getSingleBitPort(0);
			EdifSingleBitPort invIESBP = invIPort.getSingleBitPort(0);
			EdifSingleBitPort invOESBP = invOPort.getSingleBitPort(0);
			EdifPortRef lutOEPR = new EdifPortRef(constantZeroNet, lutOESBP, lutInstance);
			EdifPortRef invIEPR = new EdifPortRef(constantZeroNet, invIESBP, invInstance);
			EdifPortRef invOEPR = new EdifPortRef(constantOneNet, invOESBP, invInstance);
			
			// Hook up nets with ports. If the port exists in the 
			// original FF instance, then connect it with the corresponding
			// dangling net. If it is newly created, then connect it to
			// the corresponding constant value (0 or 1).
			if(!lut_flag) {	// LUT is newly created
				constantZeroNet.addPortConnection(lutOEPR);
			}
			if(!inv_flag) {	// INV is newly created
				constantZeroNet.addPortConnection(invIEPR);
				constantOneNet.addPortConnection(invOEPR);
			}
			q.addPortConnection(qEPR);
			c.addPortConnection(cEPR);
			d.addPortConnection(dEPR);
			if(pre != null) {
				pre.addPortConnection(preEPR);
			} else {
				constantZeroNet.addPortConnection(preEPR);
			}
			if(clr != null) {
				clr.addPortConnection(clrEPR);
			} else {
				constantZeroNet.addPortConnection(clrEPR);
			}
			if(ce != null) {
				ce.addPortConnection(ceEPR);
			} else {
				constantOneNet.addPortConnection(ceEPR);
			}

		} else {	// Constant values are provided by input ports on parent's interface
			EdifPort constantZeroPort = parentInterface.getPort(constantZeroPortName);
			EdifPort constantOnePort = parentInterface.getPort(constantOnePortName);
			EdifSingleBitPort constantZeroESBP = constantZeroPort.getSingleBitPort(0);
			EdifSingleBitPort constantOneESBP = constantOnePort.getSingleBitPort(0);
			EdifPortRef constantZeroEPR = new EdifPortRef(constantZeroNet, constantZeroESBP, null);
			EdifPortRef constantOneEPR = new EdifPortRef(constantOneNet, constantOneESBP, null);
			
			// Hook up nets with ports. If the port exists in the 
			// original FF instance, then connect it with the corresponding
			// dangling net. If it is newly created, then connect it to
			// the corresponding constant value (0 or 1).
			q.addPortConnection(qEPR);
			c.addPortConnection(cEPR);
			d.addPortConnection(dEPR);
			if(pre != null) {
				pre.addPortConnection(preEPR);
			} else {
				constantZeroNet.addPortConnection(constantZeroEPR);
				constantZeroNet.addPortConnection(preEPR);
			}
			if(clr != null) {
				clr.addPortConnection(clrEPR);
			} else {
				constantZeroNet.addPortConnection(constantZeroEPR);
				constantZeroNet.addPortConnection(clrEPR);
			}
			if(ce != null) {
				ce.addPortConnection(ceEPR);
			} else {
				constantOneNet.addPortConnection(constantOneEPR);
				constantOneNet.addPortConnection(ceEPR);
			}
		}
		
		/***** Step 5. Set INIT property of FDCPE *****/
		boolean isInit = false;
        PropertyList FDCPE_PropertyList = FDCPE.getPropertyList();
        if(FDCPE_PropertyList != null) {
        	for(Property FDCPE_Property: FDCPE_PropertyList.values()) {
        		if(FDCPE_Property.getName().equals("INIT")) {	// The property "INIT" already exists
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

	/** Create port for PRESET and CLR (constant value '0') **/
	public static void CreateConstantZeroPort(EdifCell parent) {
		// Create a new port on parent's interface for PRESET and CLR signal
		EdifCellInterface parentInterface = parent.getInterface();
		String constantZeroPortName = parent.getName() + "_CONSTANT_ZERO_PORT";
		EdifNameable constantZeroPortNameable = NamedObject.createValidEdifNameable(constantZeroPortName);
		constantZeroPortNameable = parent.getUniqueInstanceNameable(constantZeroPortNameable);
		EdifPort constantZeroPort = new EdifPort(parentInterface, constantZeroPortNameable, 1, 1);
		
		// If the port does not exist, add it to parent's interface
		boolean port_flag = false;
		for(EdifPort port: parent.getPortList()) {
			if(port.equals(constantZeroPort))
				port_flag = true;
		}
		if(port_flag == false) {
			try {
				parentInterface.addPort(constantZeroPortNameable, 1, 1);
			} catch (EdifNameConflictException e) {
				System.out.println("EdifNameConflictException caught");
				System.exit(1);
			}
		}
	}
	
	/** Create net for PRESET and CLR (constant value '0') **/
	public static void CreateConstantZeroNet(EdifCell parent) {
		// Create a new net in the design for the PRESET and CLR port
		String constantZeroNetName = parent.getName() + "_CONSTANT_ZERO_NET";
		EdifNameable constantZeroNetNameable = NamedObject.createValidEdifNameable(constantZeroNetName);
		constantZeroNetNameable = parent.getUniqueInstanceNameable(constantZeroNetNameable);
		EdifNet constantZeroNet = new EdifNet(constantZeroNetNameable, parent);

		// If the net does not exist, add it to the design.
		boolean net_flag = false;
		for(EdifNet net: parent.getNetList()) {
			if(net.equals(constantZeroNet))
				net_flag = true;
		}
		if(net_flag == false) {
			try {
				parent.addNet(constantZeroNet);
			} catch (EdifNameConflictException e) {
				System.out.println("EdifNameConflictException caught");
				System.exit(1);
			}
		}
	}
	
	/** Create port for CE (constant value '1') **/
	public static void CreateConstantOnePort(EdifCell parent) {
		// Create a new port on parent's interface for CE signal
		EdifCellInterface parentInterface = parent.getInterface();
		String constantOnePortName = parent.getName() + "_CONSTANT_ONE_PORT";
		EdifNameable constantOnePortNameable = NamedObject.createValidEdifNameable(constantOnePortName);
		constantOnePortNameable = parent.getUniqueInstanceNameable(constantOnePortNameable);
		EdifPort constantOnePort = new EdifPort(parentInterface, constantOnePortNameable, 1, 1);
		
		// If the port does not exist, add it to parent's interface
		boolean port_flag = false;
		for(EdifPort port: parent.getPortList()) {
			if(port.equals(constantOnePort))
				port_flag = true;
		}
		if(port_flag == false) {
			try {
				parentInterface.addPort(constantOnePortNameable, 1, 1);
			} catch (EdifNameConflictException e) {
				System.out.println("EdifNameConflictException caught");
				System.exit(1);
			}
		}
	}
	
	/** Create net for CE (constant value '1') **/
	public static void CreateConstantOneNet(EdifCell parent) {
		// Create a new net in the design for the CE port
		String constantOneNetName = parent.getName() + "_CONSTANT_ONE_NET";
		EdifNameable constantOneNetNameable = NamedObject.createValidEdifNameable(constantOneNetName);
		constantOneNetNameable = parent.getUniqueInstanceNameable(constantOneNetNameable);
		EdifNet constantOneNet = new EdifNet(constantOneNetNameable, parent);

		// If the net does not exist, add it to the design.
		boolean net_flag = false;
		for(EdifNet net: parent.getNetList()) {
			if(net.equals(constantOneNet))
				net_flag = true;
		}
		if(net_flag == false) {
			try {
				parent.addNet(constantOneNet);
			} catch (EdifNameConflictException e) {
				System.out.println("EdifNameConflictException caught");
				System.exit(1);
			}
		}
	}
}