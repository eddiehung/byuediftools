package edu.byu.ece.edif.arch.xilinx.parts;

/**
 * This class generates Java source files that describe Xilinx device specifications
 * for the virtex5 architecture.
 */
public class XilinxV5DeviceClassGenerator extends XilinxDeviceClassGenerator {
    
    /**
     * Construct a new class generator and collect all of the data needed
     * to write Java source code for a part library. Data is collected
     * using the Xilinx partgen tool.
     * @param architecture
     */
    public XilinxV5DeviceClassGenerator(){
    	super("Virtex5", "XC5V");
    }
    
    /**
     * Create the Java source code for a part library of the architecture associated
     * with the class generator instance.
     */
    public void createJavaSourceFile() {
    	super.createJavaSourceFile("V5");
    }
    
   
    /**
     * Allows this class to run stand alone from the rest of the project
     * @param args The architectures for which to generate the java files (ie. virtex4, virtex5 ...)
     */
    public static void main(String args[]){
        XilinxV5DeviceClassGenerator gen = new XilinxV5DeviceClassGenerator();
        gen.createJavaSourceFile();
    }
}

