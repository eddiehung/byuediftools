package edu.byu.ece.edif.util.generate;
import java.io.IOException;

import edu.byu.ece.edif.core.EdifPort;


public class SignedMultiplierGenerator {

	public static void main(String[] args) throws IOException {
		
        String name = args[0];
        int length = Integer.parseInt(args[1]);
    
		CircuitGenerator generator = new CircuitGenerator(name);
		generator.addPort("a", length, EdifPort.IN);
		generator.addPort("b", length, EdifPort.IN);
		generator.addPort("c", 2*length, EdifPort.OUT);
		
		SignedMultiplier mult0 = new SignedMultiplier(name+"0", length, generator, 0);		
		
		for (int i = 0; i < length; i++) {
			generator.connect(generator.getPort("a", i), mult0.getPort("X", i));
			generator.connect(generator.getPort("b", i), mult0.getPort("Y", i));
		}
		
		for (int i = 0; i < 2*length; i++) {
			generator.connect(generator.getPort("c", i), mult0.getPort("P", i));
		}
				
		generator.realizeNets();
		
		generator.toEdif(name+".edf");
		
	}
	
}
