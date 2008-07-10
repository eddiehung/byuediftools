package edu.byu.ece.edif.tools.sterilize.lutreplace.SRL;

import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.EdifTypedValue;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.core.PropertyList;
import edu.byu.ece.edif.core.StringTypedValue;

public class SRL_Replacement {

	/** Enumerate all of the SRL types */	
	public static final String SRL16_STRING 	= 	"SRL16";
	public static final String SRL16_1_STRING 	= 	"SRL16_1";
	public static final String SRL16E_STRING 	= 	"SRL16E";
	public static final String SRL16E_1_STRING 	= 	"SRL16E_1";
	public static final String SRLC16_STRING 	= 	"SRLC16";
	public static final String SRLC16_1_STRING 	= 	"SRLC16_1";
	public static final String SRLC16E_STRING 	= 	"SRLC16E";
	public static final String SRLC16E_1_STRING = 	"SRLC16E_1";
	
	public enum SRLType {SRL16, SRL16_1, SRL16E, SRL16E_1, SRLC16, SRLC16_1, SRLC16E, SRLC16E_1};

	public static SRLType StringToSRLType(String str) {
		if (str.equalsIgnoreCase(SRL16_STRING)) 	return SRLType.SRL16;
		if (str.equalsIgnoreCase(SRL16_1_STRING)) 	return SRLType.SRL16_1;
		if (str.equalsIgnoreCase(SRL16E_STRING)) 	return SRLType.SRL16E;
		if (str.equalsIgnoreCase(SRL16E_1_STRING))	return SRLType.SRL16E_1;
		if (str.equalsIgnoreCase(SRLC16_STRING)) 	return SRLType.SRLC16;
		if (str.equalsIgnoreCase(SRLC16_1_STRING)) 	return SRLType.SRLC16_1;
		if (str.equalsIgnoreCase(SRLC16E_STRING)) 	return SRLType.SRLC16E;
		if (str.equalsIgnoreCase(SRLC16E_1_STRING)) return SRLType.SRLC16E_1;
		return null;
	}
	
	public static void Replace(EdifLibraryManager libManager, String srlType, 
			EdifCell parent, String namePrefix, long INIT,
			EdifNet d,  EdifNet ce, EdifNet clk, EdifNet a0, EdifNet a1, EdifNet a2, EdifNet a3, EdifNet q, EdifNet q15) {
		SRLType type = StringToSRLType(srlType);
		if (type == null)
			return;
		Replace(libManager, type, parent, namePrefix, INIT, d, ce, clk, a0, a1, a2, a3, q, q15);
	}
	
	public static void Replace(EdifLibraryManager libManager, SRLType srlType, 
			EdifCell parent, String namePrefix, long INIT,
			EdifNet d, EdifNet ce, EdifNet clk, EdifNet a0, EdifNet a1, EdifNet a2, EdifNet a3, EdifNet q, EdifNet q15) {

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
		EdifCell FD = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FD");
		EdifCell FD_1 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FD_1");
		EdifCell FDE = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FDE");
		EdifCell FDE_1 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "FDE_1");
		EdifCell MUXF5 = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "MUXF5");
		EdifCell BUF = XilinxLibrary.findOrAddXilinxPrimitive(libManager, "BUF");
		
		/****** Step 2. Create the FF instances ******/
		EdifCellInstance ffInstances[] = new EdifCellInstance[16];		
		EdifCell ff_type = null;
		// Determine which flip-flop type to use
		switch(srlType) {
		   case SRL16: 		ff_type = FD; 	 	break;
		   case SRL16_1: 	ff_type = FD_1;  	break;
		   case SRL16E: 	ff_type = FDE; 	 	break;
		   case SRL16E_1: 	ff_type = FDE_1; 	break;
		   case SRLC16: 	ff_type = FD; 		break;
		   case SRLC16_1: 	ff_type = FD_1; 	break;
		   case SRLC16E: 	ff_type = FDE; 		break;
		   case SRLC16E_1: 	ff_type = FDE_1;
		}
		for (int i = 0; i < 16; i++) {
			String ffInstanceName = namePrefix + "_FF_" + Integer.toString(i);
			EdifNameable ffInstanceNameable = NamedObject.createValidEdifNameable(ffInstanceName);
			ffInstanceNameable = parent.getUniqueInstanceNameable(ffInstanceNameable);
			ffInstances[i] = new EdifCellInstance(ffInstanceNameable, parent, ff_type);
			try {
				parent.addSubCell(ffInstances[i]);
			} catch (EdifNameConflictException e) {
				// Should not get here
			}
		}
		
		/****** Step 3. Create the Mux instances ******/
		EdifCellInstance muxInstances[] = new EdifCellInstance[15];
		for (int i = 0; i < 15; i++) {
			String muxInstanceName = namePrefix + "_MUX_" + Integer.toString(i);
			EdifNameable muxInstanceNameable = NamedObject.createValidEdifNameable(muxInstanceName);
			muxInstanceNameable = parent.getUniqueInstanceNameable(muxInstanceNameable);
			muxInstances[i] = new EdifCellInstance(muxInstanceNameable, parent, MUXF5);
			try {
				parent.addSubCell(muxInstances[i]);
			} catch (EdifNameConflictException e) {
				//Should not get here
			}
		}
		
		/****** Step 4. Create the nets for FF output ******/
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
		
		/****** Step 6. Wire up clocks ******/
		EdifPort clkPort = ff_type.getPort("C");
		if (clkPort == null) {
			System.err.println("Can't find C port on cell " + ff_type);
			System.exit(1);
		}
		EdifSingleBitPort clkESBP = clkPort.getSingleBitPort(0);
		for (int i = 0; i < 16; i++) {
			EdifPortRef clkEPR = new EdifPortRef(clk, clkESBP, ffInstances[i]);
			clk.addPortConnection(clkEPR);
		}
		
		/****** Step 7. Wire up enable signals, if the cell is an "E" type cell******/
		if (srlType == SRLType.SRL16E || srlType == SRLType.SRL16E_1 || 
				srlType == SRLType.SRLC16E || srlType ==SRLType.SRLC16E_1) {
			EdifPort cePort = ff_type.getPort("CE");
			if (cePort == null) {
				System.err.println("Can't find CE port on cell " + ff_type);
				System.exit(1);
			}
			EdifSingleBitPort ceESBP = cePort.getSingleBitPort(0);
			for(int i = 0; i < 16; i++) {
				EdifPortRef ceEPR = new EdifPortRef(ce, ceESBP, ffInstances[i]);
				ce.addPortConnection(ceEPR);
			}
		}

		/****** Step 8. Wire up nets between flip-flops ******/
		// Hook up the FF net to the Q port
		EdifPort qPort = ff_type.getPort("Q");
		if (qPort == null) {
			System.err.println("Can't find Q port on cell " + ff_type);
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
			inputNet.addPortConnection(dEPR);			
		}
		
		/****** Step 10. Add optional Q15 output ******/
		// If the SRL is a "C" type, add to the q15 output
		if (srlType == SRLType.SRLC16 || srlType == SRLType.SRLC16_1 || 
				srlType == SRLType.SRLC16E || srlType == SRLType.SRLC16E_1) {
			// Create a "buf" edifcellinstance
			String bufName = namePrefix + "_BUF";
			EdifNameable bufNameable = NamedObject.createValidEdifNameable(bufName);
			bufNameable = parent.getUniqueNetNameable(bufNameable);
			EdifCellInstance q15_buf = new EdifCellInstance(bufNameable, parent, BUF);
			try {
				parent.addSubCell(q15_buf);
			} catch (EdifNameConflictException e) {
				// Should not get here
			}
			
			// connect ffOutputNets[15] (i.e. output of FF[15] to the "I" input of the buf
			EdifPort iPort = BUF.getPort("I");
			if (iPort == null) {
				System.err.println("Can't find I port on cell " + BUF);
				System.exit(1);
			}
			EdifSingleBitPort iESBP = iPort.getSingleBitPort(0);
			EdifPortRef iEPR = new EdifPortRef(ffOutputNets[15], iESBP, q15_buf);
			ffOutputNets[15].addPortConnection(iEPR);
			
			// connect the q15 net (passed in above) to the "O" output of the buf
			EdifPort oPort = BUF.getPort("O");
			if (oPort == null) {
				System.err.println("Can't find O port on cell " + BUF);
				System.exit(1);
			}
			EdifSingleBitPort oESBP = oPort.getSingleBitPort(0);
			EdifPortRef oEPR = new EdifPortRef(q15, oESBP, q15_buf);
			q15.addPortConnection(oEPR);
		}

		/****** Step 11. Hook up the muxes ******/
		// Get port information
		EdifPort i0Port = MUXF5.getPort("I0");
		if (i0Port == null) {
			System.err.println("Can't find I0 port on cell " + MUXF5);
			System.exit(1);
		}
		EdifPort i1Port = MUXF5.getPort("I1");
		if (i1Port == null) {
			System.err.println("Can't find I1 port on cell " + MUXF5);
			System.exit(1);
		}
		EdifPort sPort = MUXF5.getPort("S");
		if (sPort == null) {
			System.err.println("Can't find S port on cell " + MUXF5);
			System.exit(1);
		}
		EdifPort oPort = MUXF5.getPort("O");
		if (oPort == null) {
			System.err.println("Can't find O port on cell " + MUXF5);
			System.exit(1);
		}
		EdifSingleBitPort i0ESBP = i0Port.getSingleBitPort(0);
		EdifSingleBitPort i1ESBP = i1Port.getSingleBitPort(0);
		EdifSingleBitPort sESBP = sPort.getSingleBitPort(0);
		EdifSingleBitPort oESBP = oPort.getSingleBitPort(0);
		
		// Hook up 2-1 muxes to get a 16-1 mux
		for (int i = 0; i < 14; i++) {
			int muxNumber;
			if(i%2 == 0) {
				muxNumber = (i+16) / 2;
				EdifPortRef i0EPR = new EdifPortRef(muxOutputNets[i], i0ESBP, muxInstances[muxNumber]);
				muxOutputNets[i].addPortConnection(i0EPR);
			} else {
				muxNumber = (i+15) / 2;
				EdifPortRef i1EPR = new EdifPortRef(muxOutputNets[i], i1ESBP, muxInstances[muxNumber]);
				muxOutputNets[i].addPortConnection(i1EPR);
			}
			EdifPortRef oEPR = new EdifPortRef(muxOutputNets[i], oESBP, muxInstances[i]);
			muxOutputNets[i].addPortConnection(oEPR);
		}
		
		// Hook up FFs and the 16-1 mux
		for (int i = 0; i < 16; i++) {
			if(i%2 ==0) {
				EdifPortRef i0EPR = new EdifPortRef(ffOutputNets[i], i0ESBP, muxInstances[i/2]);
				ffOutputNets[i].addPortConnection(i0EPR);
			} else {
				EdifPortRef i1EPR = new EdifPortRef(ffOutputNets[i], i1ESBP, muxInstances[i/2]);
				ffOutputNets[i].addPortConnection(i1EPR);
			}
		}
		
		// Hook up select signals a0, a1, a2, a3
		for (int i = 0; i < 15; i++) {
			EdifNet aNet = null;
			if (i >= 0 && i <= 7) {
				aNet = a0;
			} else if (i >= 8 && i <= 11) {
				aNet = a1;
			} else if (i == 12 || i == 13) {
				aNet = a2;
			} else {
				aNet = a3;
			}
			EdifPortRef sEPR = new EdifPortRef(aNet, sESBP, muxInstances[i]);
			aNet.addPortConnection(sEPR);
		}
		
		// Hook up mux output to SRL output (q)
		EdifPortRef oEPR = new EdifPortRef(q, oESBP, muxInstances[14]);
		q.addPortConnection(oEPR);
		
		/****** Step 12. set INIT string  ******/
		boolean isInit;
		int initCount = 0;
		StringTypedValue valueZero = new StringTypedValue("0");
        StringTypedValue valueOne = new StringTypedValue("1");
		//Set INIT Property for FDEs
        PropertyList[] FDE_propertylist = new PropertyList[16];
        for (int i = 0; i < 16; i++) {
        	isInit = false;
        	FDE_propertylist[i] = ffInstances[i].getPropertyList();
        	if(FDE_propertylist[i] != null)		// The property "INIT" already exists
        	{
        		for(Property FDE_property : FDE_propertylist[i].values())
        		{
        			if(FDE_property.getName().equals("INIT"))
        			{
        				isInit = true;
        				if (((INIT >> initCount++) & 1) == 1)
                            FDE_property.setValue(valueOne);
                        else
                            FDE_property.setValue(valueZero);
        			}
        		}
        	}
        	if(!isInit)							// The property "INIT" does not exist
        	{
        		if (((INIT >> initCount++) & 1) == 1)
                    ffInstances[i].addProperty(new Property("INIT", (EdifTypedValue)valueOne));
                else
                	ffInstances[i].addProperty(new Property("INIT", (EdifTypedValue)valueZero));
        	}
        }
	}
}