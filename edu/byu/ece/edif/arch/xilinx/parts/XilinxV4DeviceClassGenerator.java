package edu.byu.ece.edif.arch.xilinx.parts;

/**
 * This class generates Java source files that describe Xilinx device specifications
 * for the Virtex4 architecture.
 */
public class XilinxV4DeviceClassGenerator extends XilinxDeviceClassGenerator {
    
    /**
     * Construct a new class generator and collect all of the data needed
     * to write Java source code for a part library. Data is collected
     * using the Xilinx partgen tool.
     * @param architecture
     */
    public XilinxV4DeviceClassGenerator(){
    	super("Virtex4", "XC4V");
    }
    
    /**
     * Create the Java source code for a part library of the architecture associated
     * with the class generator instance.
     */
    public void createJavaSourceFile() {
    	super.createJavaSourceFile("V4");
    }
    
   
    /**
     * Allows this class to run stand alone from the rest of the project
     * @param args The architectures for which to generate the java files (ie. virtex4, virtex5 ...)
     */
    public static void main(String args[]){
        XilinxV4DeviceClassGenerator gen = new XilinxV4DeviceClassGenerator();
        gen.createJavaSourceFile();
    }
}

