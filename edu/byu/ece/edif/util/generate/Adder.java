package edu.byu.ece.edif.util.generate;


public class Adder extends WeightedModule {
	
	public Adder(String name, int width, CircuitGenerator generator, int weight) {
		super(name, generator, weight);
		_width = width;
		_buildModule();
	}
	
	protected void _buildModule() {
		
		// create adder with CIN
		AdderCIN adderCIN = new AdderCIN(_name + "_cin", _width, _parent, _weightOffset);
		
		// wiring
		_parent.connect(_parent.globalGND(), adderCIN.getPort("CIN"));
		for (int i = 0; i < _width; i++) {
			connectPort("X", i, adderCIN.getPort("X", i));
			connectPort("Y", i, adderCIN.getPort("Y", i));
			connectPort("S", i, adderCIN.getPort("S", i));
		}
		connectPort("S", _width, adderCIN.getPort("S", _width));
		
	}
	
	public int getWidth() {
		return _width;
	}
	
	protected int _width;
	
}
