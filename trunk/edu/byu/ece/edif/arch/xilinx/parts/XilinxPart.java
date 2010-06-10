package edu.byu.ece.edif.arch.xilinx.parts;

/**
 * Represents a specific part available from Xilinx. Is a member of a XilinxFamily,
 * has a specific XilixDevice, XilinxPackage, and XilinxSpeedGrade.
 */
public interface XilinxPart {

	public XilinxFamily getFamily();
	public XilinxDevice getDevice();
	public XilinxPackage getPackate();
	public XilinxSpeedGrade getSpeedGrade();
	
	public String getPartName();
	
}
