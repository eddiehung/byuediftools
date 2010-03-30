package edu.byu.ece.edif.util.generate;
import java.io.IOException;

import edu.byu.ece.edif.core.EdifPort;


public class AdderGenerator {
	public static void main(String[] args) throws IOException {
		
	    String name = args[0];
	    int length = Integer.parseInt(args[1]);
	    
		CircuitGenerator generator = new CircuitGenerator(name);
		generator.addPort("a", length, EdifPort.IN);
		generator.addPort("b", length, EdifPort.IN);
		generator.addPort("c", length+1, EdifPort.OUT);
		
		Adder adder0 = new Adder(name+"0", length, generator, 0);
		
		
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
