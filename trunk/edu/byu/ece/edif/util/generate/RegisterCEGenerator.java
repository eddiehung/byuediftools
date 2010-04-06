package edu.byu.ece.edif.util.generate;

import java.io.IOException;

import edu.byu.ece.edif.core.EdifPort;

public class RegisterCEGenerator {

public static void main(String[] args) throws IOException {
        
        String name = args[0];
        int length = Integer.parseInt(args[1]);
    
        CircuitGenerator generator = new CircuitGenerator(name);
        generator.addPort("clk", EdifPort.IN);
        generator.addPort("rst", EdifPort.IN);
        generator.addPort("en", EdifPort.IN);
        generator.addPort("din", length, EdifPort.IN);
        generator.addPort("dout", length, EdifPort.OUT);
        
        RegisterCE regCE = new RegisterCE(name+"0", length, generator, 0);      

        generator.connect(generator.getPort("clk"), regCE.getPort("clk"));
        generator.connect(generator.getPort("rst"), regCE.getPort("rst"));
        generator.connect(generator.getPort("en"), regCE.getPort("en"));
        for (int i = 0; i < length; i++) {
            generator.connect(generator.getPort("din", i), regCE.getPort("din", i));
            generator.connect(generator.getPort("dout", i), regCE.getPort("dout", i));
        }
                        
        generator.realizeNets();
        
        generator.toEdif(name+".edf");
        
    }
}
