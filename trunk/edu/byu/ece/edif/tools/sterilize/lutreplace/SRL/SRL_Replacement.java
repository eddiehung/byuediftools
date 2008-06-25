package edu.byu.ece.edif.tools.sterilize.lutreplace.SRL;

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

public class SRL_Replacement {

	/** Enumerate all of the SRL types */	
	public static final String SRL16_STRING = 		"SRL16";
	public static final String SRL16_1_STRING = 	"SRL16_1";
	public static final String SRL16E_STRING = 	"SRL16E";
	public static final String SRL16E_1_STRING = 	"SRL16E_1";
	public static final String SRLC16_STRING = 	"SRLC16";
	public static final String SRLC16_1_STRING = 	"SRLC16_1";
	public static final String SRLC16E_STRING = 	"SRLC16E";
	public static final String SRLC16E_1_STRING = 	"SRLC16E_1";
	
	public enum SRLType {SRL16, SRL16_1, SRL16E, SRL16E_1, SRLC16, SRLC16_1, SRLC16E, SRLC16E_1};

	public static SRLType StringToSRLType(String str) {
		if (str.equalsIgnoreCase(SRL16_STRING)) return SRLType.SRL16;
		if (str.equalsIgnoreCase(SRL16_1_STRING)) return SRLType.SRL16_1;
		if (str.equalsIgnoreCase(SRL16E_STRING)) return SRLType.SRL16E;
		if (str.equalsIgnoreCase(SRL16E_1_STRING)) return SRLType.SRL16E_1;
		if (str.equalsIgnoreCase(SRLC16_STRING)) return SRLType.SRLC16;
		if (str.equalsIgnoreCase(SRLC16_1_STRING)) return SRLType.SRLC16_1;
		if (str.equalsIgnoreCase(SRLC16E_STRING)) return SRLType.SRLC16E;
		if (str.equalsIgnoreCase(SRLC16E_1_STRING)) return SRLType.SRLC16E_1;
		return null;
	}
	
	public static void Replace(EdifLibraryManager libManager, String srlType, 
			EdifCell parent, String namePrefix, long INIT,
			EdifNet d, EdifNet clk, EdifNet a0, EdifNet a1, EdifNet a2, EdifNet a3, EdifNet q, EdifNet q15, EdifNet e_input) {
		SRLType type = StringToSRLType(srlType);
		if (type == null)
			return;
		Replace(libManager, type, parent, namePrefix, INIT, d, clk, a0, a1, a2, a3, q, q15, e_input);
	}
	
	public static void Replace(EdifLibraryManager libManager, SRLType srlType, 
			EdifCell parent, String namePrefix, long INIT,
			EdifNet d, EdifNet clk, EdifNet a0, EdifNet a1, EdifNet a2, EdifNet a3, EdifNet q, EdifNet q15, EdifNet e_input){

		/** Issues to consider when coding:
		 *  - Don't provide all of your try/catch clauses around the entire program. Use them
		 *    internall to allow better error handling
		 *  - Use more loops to reduce the code (there is a lot of code without loops
		 */
		/** Things to do to clean up code:
		 * - Make a single file that works for all replacement types
		 */
		
		/****** Step 1. Create/Find Xilinx primitive Cells needed for this replacement ******/
		// I think you should just add all of the cells that are needed for all of the different
		// cases. Even if you do not need the cell it is no harm to put it in the library.
		// TODO: add all of the different types.
		EdifCell MUXF5 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "MUXF5");
		EdifCell MUXF6 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "MUXF6");		
		EdifCell FD = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FD");
		EdifCell FD_1 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FD_1");
		EdifCell LUT3 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "LUT3");
		EdifCell FDE_1 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FDE_1");

		/****** Step 2. Create the FF instances ******/
		EdifCellInstance ffInstances[] = new EdifCellInstance[16];		
		EdifCell ff_type = null;

		// Determine which flip-flop type to use
		switch(srlType) {
		   case SRL16: ff_type = FD; break;
		   case SRL16_1: ff_type = FD_1; break;
		   // TODO: finish this list
		}
		for (int i = 0; i < 16; i++) {
			String instanceName = namePrefix + "_FF_" + Integer.toString(i);
			EdifNameable instanceNameable = NamedObject.createValidEdifNameable(instanceName);
			instanceNameable = parent.getUniqueInstanceNameable(instanceNameable);
			ffInstances[i] = new EdifCellInstance(instanceNameable, parent, ff_type);
			try {
				parent.addSubCell(ffInstances[i]);
			} catch (EdifNameConflictException e) {
				// Should not get here
			}
		}
		
		/****** Step 3. Create the Mux instances ******/
		EdifNameable muxf5_1_name = NamedObject.createValidEdifNameable(namePrefix + "MUXF5_1");
		EdifNameable muxf5_2_name = NamedObject.createValidEdifNameable(namePrefix + "MUXF5_2");
		EdifNameable muxf5_3_name = NamedObject.createValidEdifNameable(namePrefix + "MUXF5_3");
		EdifNameable muxf5_4_name = NamedObject.createValidEdifNameable(namePrefix + "MUXF5_4");
		EdifNameable muxf6_1_name = NamedObject.createValidEdifNameable(namePrefix + "MUXF6_1");
		EdifNameable muxf6_2_name = NamedObject.createValidEdifNameable(namePrefix + "MUXF6_2");
		EdifCellInstance MUXF5_1 = new EdifCellInstance(muxf5_1_name, parent, MUXF5);
		EdifCellInstance MUXF5_2 = new EdifCellInstance(muxf5_2_name, parent, MUXF5);
		EdifCellInstance MUXF5_3 = new EdifCellInstance(muxf5_3_name, parent, MUXF5);
		EdifCellInstance MUXF5_4 = new EdifCellInstance(muxf5_4_name, parent, MUXF5);
		EdifCellInstance MUXF6_1 = new EdifCellInstance(muxf6_1_name, parent, MUXF6);
		EdifCellInstance MUXF6_2 = new EdifCellInstance(muxf6_2_name, parent, MUXF6);
		parent.addSubCellUniqueName(MUXF5_1);
		parent.addSubCellUniqueName(MUXF5_2);
		parent.addSubCellUniqueName(MUXF5_3);
		parent.addSubCellUniqueName(MUXF5_4);
		parent.addSubCellUniqueName(MUXF6_1);
		parent.addSubCellUniqueName(MUXF6_2);
		
		/****** Step 4. Create the output nets ******/
		// create 16 nets for FF outputs
		EdifNet ffOutputNets[] = new EdifNet[16];
		for (int i = 0; i < 16; i++) {
			String netName = namePrefix + "_o_" + Integer.toString(i);
			EdifNameable netNameable = NamedObject.createValidEdifNameable(netName);
			netNameable = parent.getUniqueNetNameable(netNameable);
			ffOutputNets[i] = new EdifNet(netNameable, parent);
			try {
				parent.addNet(ffOutputNets[i]);
			} catch (EdifNameConflictException e) {
				// Should not get here
			}
		}
		
		/****** Step 5. Create the mux nets ******/
		// create 14 nets for mux outputs
		EdifNet muxOutputNets[] = new EdifNet[14];
		for (int i = 0; i < 14; i++) {
			String netName = namePrefix + "_m_" + Integer.toString(i);
			EdifNameable netNameable = NamedObject.createValidEdifNameable(netName);
			netNameable = parent.getUniqueNetNameable(netNameable);
			muxOutputNets[i] = new EdifNet(netNameable, parent);
			try {
				parent.addNet(muxOutputNets[i]);
			} catch (EdifNameConflictException e) {
				// Should not get here
			}
		}
		

		/****** Step 6. Wire up clocks******/
		for (int i = 0; i < 16; i++) {
			EdifPort clkPort = ff_type.getPort("C");
			if (clkPort == null) {
				System.err.println("Can't find C port on cell "+ff_type);
				System.exit(1);
			}
			EdifSingleBitPort clkESBP = clkPort.getSingleBitPort(0);
			EdifPortRef clkEPR = new EdifPortRef(clk, clkESBP, ffInstances[i]);
			clk.addPortConnection(clkEPR);
			
			// TODO:
			// - If the cell has an enable signal, hook up the enable here
		}

		/****** Step 7. Wire up nets between flip-flops ******/
		// Create 16 new nets for the flip-flop outputs
		// Hook up the FF net to the Q port
		EdifPort qPort = ff_type.getPort("Q");
		if (qPort == null) {
			System.err.println("Can't find Q port on cell "+ff_type);
			System.exit(1);
		}
		EdifSingleBitPort qESBP = qPort.getSingleBitPort(0);
		for (int i = 0; i < 16; i++) {

			EdifPortRef qEPR = new EdifPortRef(ffOutputNets[i], qESBP, ffInstances[i]);
			ffOutputNets[i].addPortConnection(qEPR);

			// Hook up the inputs to the FF
			EdifPort dPort = ff_type.getPort("D");
			EdifNet inputNet = null;
			if (dPort == null) {
				System.err.println("Can't find D port on cell "+ff_type);
				System.exit(1);
			}
			EdifSingleBitPort dESBP = dPort.getSingleBitPort(0);

			if (i == 0) {
				// input to the first FF is the d input to the SRL
				inputNet = d;
			} else {
				// input to the other FFs is the output net of the previous FF
				inputNet = ffOutputNets[i-1];
			}
			EdifPortRef dEPR = new EdifPortRef(inputNet, dESBP, ffInstances[i]);
			clk.addPortConnection(dEPR);			
		}
		
		/****** Step 8. Add optional Q15 output ******/
		// If the SRL is a "C" type, add to the q15 output
		if (srlType == SRLType.SRLC16 || srlType == SRLType.SRLC16_1 || 
				srlType == SRLType.SRLC16E || srlType == SRLType.SRLC16E_1) {
			// TODO
			// Create a "buf" edifcellinstance
			// connect ffOutputNets[15] (i.e. output of FF[15] to the "I" input of the buf
			// connect the q15 net (passed in above) to the "O" output of the buf
			
		}

		/****** Step 9. Hook up the muxes ******/
		// TODO
		
		/****** Step 10. set INIT string - use a loop ******/
		// TODO
		
	}
}