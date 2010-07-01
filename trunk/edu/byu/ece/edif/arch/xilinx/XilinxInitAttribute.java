package edu.byu.ece.edif.arch.xilinx;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class represents the INIT attribute of a Xilinx primitive. It 
 * provides methods to find the minimum number of inputs with the given 
 * init string, determines if any "don't care" inputs exist with the 
 * given init string, and will contain any other methods deemed to be
 * useful.
 * 
 * @author whowes
 *
 */
public class XilinxInitAttribute {
	
	/**
	 * this constructor sets the number of inputs as the minimum
	 * necessary for the given INIT string
	 */
	public XilinxInitAttribute(String initStr) {
		_initString = initStr.toUpperCase();
		_initValue = new BigInteger(_initString, HEX_RADIX);
		//make sure that the number of bits is a power of 2
		_numBits = (int)Math.pow(2, (double)intLog2(_initString.length()*4));
		_numInputs = calcMinNumberOfInputPins();
		initLookupTable();
	}
	
	/**
	 * this constructor allows the user to define the number
	 * of inputs (and thus the number of bits in the lookup table)
	 */
	public XilinxInitAttribute(String initStr, int numInputs) {
		_initString = initStr.toUpperCase();
		_initValue = new BigInteger(_initString, HEX_RADIX);
		_numInputs = numInputs;
		_numBits = (int)Math.pow(2, _numInputs);
		initLookupTable();
	}
	
	/**
	 * This method returns the lookup table value for a given address
	 * 
	 * @param address
	 * @return -1 if bad address given, otherwise a 1 or 0
	 */
	public int getValueFromAddress(int address) {
		int retVal;
		if ((address > _numBits-1) || (address < 0)) {
			retVal = -1;
		}
		else {
			retVal = _lookupTable[address];
		}
		return retVal;
	}
	
	/**
	 * Finds and returns a List<Integer> with the indices of input
	 * pins that do not affect the output.
	 * 
	 * @return List<Integer> containing don't care pin indices
	 */
	public List<Integer> getDontCareInputs() {
		ArrayList<Integer> dontCares = new ArrayList<Integer>();
		int numPairsToCheck = 1;
		int numInGroup = _numBits/2;
		for(int i=_numInputs-1; i>=0; i--) {
			boolean isDontCare = true;
			int idx1 = 0;
			int idx2 = numInGroup;
			pairFor : for (int g=0; g<=numPairsToCheck-1; g++) {
				//check the pair - all corresponding elements of the
				//pair MUST be equal for this input to be a don't care
				for(int j=0; j<numInGroup; j++) {
					if (_lookupTable[idx1+j] != _lookupTable[idx2+j]) {
						isDontCare = false;
						break pairFor;
					}
				}
				idx1 += numInGroup*2;
				idx2 += numInGroup*2;
			}
			if(isDontCare) {
				dontCares.add(i);
			}
			numPairsToCheck *= 2;
			numInGroup /= 2;
			//System.out.println("Pairs to check: " + numPairsToCheck);
			//System.out.println("Number per group: " + numInGroup);
		}
		//this isn't really that necessary...
		Collections.sort(dontCares);
		
		return dontCares;
	}
	
	/**
	 * Testing method - given a don't care index, test a large number
	 * of random addresses, flipping the address bit corresponding to
	 * the supposed don't care input
	 *  
	 * @return boolean verifying whether this is a don't care
	 */
	public boolean randomTestDontCare(int dc) {
		Random generator = new Random();
		for (int i=0; i<NUM_RANDOM_TESTS; i++) {
			int indexOrig = 0;
			int indexFlipped = Integer.MAX_VALUE;
			while(indexFlipped > _numBits-1 || indexFlipped < 0) {
				indexOrig = generator.nextInt(_numBits);
				indexFlipped = indexOrig ^ (1 << dc);
			}

			if (_lookupTable[indexOrig] != _lookupTable[indexFlipped]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns the number of bits in the lookup table for this init value
	 * 
	 * @return number of bits in lookup table
	 */
	public int getNumberOfBits() {
		return _numBits;
	}
	
	/**
	 * Returns the number of inputs. This may be user specified; if not,
	 * it is automatically calculated from the init string (assuming that
	 * all bits are used) 
	 * 
	 * @return number of inputs
	 */
	public int getNumberOfInputPins() {
		return _numInputs;
	}
	
	/**
	 * Returns the user-supplied init string (in upper case)
	 * 
	 * @return initialization String
	 */
	public String getInitString() {
		return _initString;
	}
	
	/**
	 * Returns a BigInteger representing the binary value of the 
	 * initialization value.
	 * 
	 * @return initialization value
	 */
	public BigInteger getInitValue() {
		return _initValue;
	}
	
	/**
	 * Calculates the minimum number of input pins for the number
	 * of bits in the lookup table
	 * 
	 * @return minimum number of bits
	 */
	private int calcMinNumberOfInputPins() {
		return intLog2(_numBits);
	}
		
	/**
	 * Initializes the lookup table from the init value
	 */
	private void initLookupTable() {
		_lookupTable = new int[_numBits];
		byte[] byteArrayOne = new byte[1];
		byteArrayOne[0] = 1;
		BigInteger bigIntOne = new BigInteger(byteArrayOne);
		for(int i=0; i<_numBits; i++) {
			_lookupTable[i] = (byte)(_initValue.shiftRight(i)).and(bigIntOne).intValue();
		}
	}
	
	/**
	 * Returns the integer log base 2 of the input integer. 
	 * 
	 * @param num
	 * @return log2(num)
	 */
	private int intLog2(int num) {
		int cnt;
		if(num<=0) 
			return -1;
		num--;
		for(cnt=0; num>0; cnt++,num>>=1);
			return cnt;
	}
	
	//members
	private String _initString;
	private BigInteger _initValue;
	private int _numInputs;
	private int _numBits;
	private int[] _lookupTable;
	
	//constants
	public static final int HEX_RADIX = 16;
	public static final int NUM_RANDOM_TESTS = 10000000;
	
	public static void main(String[] args) {
		ArrayList<XilinxInitAttribute> vals = new ArrayList<XilinxInitAttribute>();
		
		vals.add(new XilinxInitAttribute("77777777FFF7FFF7"));
		vals.add(new XilinxInitAttribute("FFF0FFF001000100"));
		vals.add(new XilinxInitAttribute("3000300020002000"));
		vals.add(new XilinxInitAttribute("A6AAAAAA"));
		vals.add(new XilinxInitAttribute("2"));
		vals.add(new XilinxInitAttribute("2", 1));
		vals.add(new XilinxInitAttribute("A3"));
		vals.add(new XilinxInitAttribute("6"));
		vals.add(new XilinxInitAttribute("2000"));
		vals.add(new XilinxInitAttribute("AAAA"));
		vals.add(new XilinxInitAttribute("FFFF"));
		vals.add(new XilinxInitAttribute("CC"));
		
		//also do some random inits
		for(int i=0; i<500000; i++) {
			Random gen = new Random();
			vals.add(new XilinxInitAttribute(Integer.toHexString(gen.nextInt(Integer.MAX_VALUE))));
		}
		
		for(XilinxInitAttribute val : vals) {
			List<Integer> dontCares = val.getDontCareInputs();
			if(dontCares.size() > 0) {
				System.out.println("----------------------------------------");			
				System.out.println("- Init String: \"" + val.getInitString() + "\"");
				System.out.println("----------------------------------------");
				System.out.println("Number of bits: " + val.getNumberOfBits());
				System.out.println("Number of inputs: " + val.getNumberOfInputPins());
				System.out.print("Don't Care Inputs: ");
				for(Integer dontCare : dontCares) {
					System.out.print(dontCare + " ");
				}
				System.out.println();
				System.out.print("Randomly Found Don't Care Inputs: ");
				for(int i=0; i<val.getNumberOfInputPins(); i++) {
					if (val.randomTestDontCare(i)) {
						System.out.print(i + " ");
					}
				}
				System.out.println();
				/*for (int i=0; i<val.getNumberOfBits(); i++) {
					System.out.println("value["+i+"]: " + val.getValueFromAddress(i));
				}*/
			}
		}
	}	
}
