package edu.byu.ece.edif.util.generate;
import java.io.IOException;

import edu.byu.ece.edif.core.EdifPort;


public class SignedAdderGenerator {
	
	public static void main(String[] args) throws IOException {

		if (args.length < 2 || args.length > 3) {
			System.out.println("USAGE: java SignedAdderGenerator <Module Name>"
					+ " <Input Signal Width> [Weight of LSB (default=0)]");
			System.exit(-1);
		}
        
        String name = args[0];
        int length = Integer.parseInt(args[1]);
        int weight = 0;
		if (args.length == 3)
			weight = Integer.parseInt(args[2]);
        
        CircuitGenerator generator = new CircuitGenerator(name);
        generator.addPort("a", length, EdifPort.IN);
        generator.addPort("b", length, EdifPort.IN);
        generator.addPort("c", length+1, EdifPort.OUT);
        
        SignedAdder adder0 = new SignedAdder(name + "0", length, generator,
				weight);
        
        for (int i = 0; i < length; i++) {
            generator.connect(generator.getPort("a", i), adder0.getPort("X", i));
            generator.connect(generator.getPort("b", i), adder0.getPort("Y", i));
        }
        
        for (int i = 0; i < length+1; i++) {
            generator.connect(generator.getPort("c", i), adder0.getPort("S", i));
        }
                
        generator.realizeNets();
        
        generator.toEdif(name+".edf");
        
    }
}
