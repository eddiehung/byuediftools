package edu.byu.ece.edif.arch.xilinx.parts;

import java.util.Collection;

public interface XilinxFamily {

	/** Return name of family. **/
	public String getFamilyName();
	
	public Collection<XilinxDevice> getFamilyParts();
	
}
