package edu.byu.ece.edif.util.generate;
import java.io.IOException;

import edu.byu.ece.edif.core.EdifPort;


public class SignedMultiplierGenerator {

	public static void main(String[] args) throws IOException {
		
		if (args.length < 2 || args.length > 4) {
			System.out.println("USAGE: java SignedMultiplierGenerator "
					+ "<Module Name> <Input Signal Width> "
					+ "[Weight of LSB (default=0)] "
					+ "[Diagonal-style weights (default=false)]");
			System.exit(-1);
		}
		
        String name = args[0];
        int length = Integer.parseInt(args[1]);
        int weight = 0;
        boolean diagonal = false;
		if (args.length == 3)
			weight = Integer.parseInt(args[2]);
		if (args.length == 4)
			diagonal = Boolean.parseBoolean(args[3]);
    
		CircuitGenerator generator = new CircuitGenerator(name);
		generator.addPort("a", length, EdifPort.IN);
		generator.addPort("b", length, EdifPort.IN);
		generator.addPort("c", 2*length, EdifPort.OUT);
		
		SignedMultiplier mult0 = new SignedMultiplier(name + "0", length,
				generator, weight, diagonal);		
		
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
