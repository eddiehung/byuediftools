package edu.byu.ece.edif.util.generate;
import java.io.IOException;

import edu.byu.ece.edif.core.EdifPort;


public class FullAdderTest {

	public static void main(String[] args) throws IOException {
	
		CircuitGenerator generator = new CircuitGenerator("fullAdder");
		generator.addPort("X", EdifPort.IN);
		generator.addPort("Y", EdifPort.IN);
		generator.addPort("CIN", EdifPort.IN);
		generator.addPort("COUT", EdifPort.OUT);
		generator.addPort("S", EdifPort.OUT);
		
		FullAdder adder0 = new FullAdder("fa0", generator, 0);
		
		generator.connect(generator.getPort("X"), adder0.getPort("X"));
		generator.connect(generator.getPort("Y"), adder0.getPort("Y"));
		generator.connect(generator.getPort("CIN"), adder0.getPort("CIN"));
		generator.connect(generator.getPort("COUT"), adder0.getPort("COUT"));
		generator.connect(generator.getPort("S"), adder0.getPort("S"));
		
		generator.realizeNets();
		
		generator.toEdif("fullAdder.edf");
		
	}
	
}
