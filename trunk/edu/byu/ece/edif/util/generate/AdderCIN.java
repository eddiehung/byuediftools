package edu.byu.ece.edif.util.generate;

public class AdderCIN extends WeightedModule {

	public AdderCIN(String name, int width, CircuitGenerator generator, int weight) {
		super(name, generator, weight);
		_width = width;
		_buildModule();
	}
	
	protected void _buildModule() {
		
		// create full adders
		FullAdder[] fullAdders = new FullAdder[_width];
		for (int i = 0; i < _width; i++) {
			fullAdders[i] = new FullAdder("fa_"+i, _parent, i+_weightOffset);
		}
		
		// wiring
		addPort("CIN", fullAdders[0].getPort("CIN"));
		for (int i = 0; i < _width; i++) {
			connectPort("X", i, fullAdders[i].getPort("X"));
			connectPort("Y", i, fullAdders[i].getPort("Y"));
			connectPort("S", i, fullAdders[i].getPort("S"));
			if (i < (_width-1)) {
				_parent.connect(fullAdders[i].getPort("COUT"), fullAdders[i+1].getPort("CIN"));
			}
		}
		connectPort("S", _width, fullAdders[_width-1].getPort("COUT"));
		
	}
	
	public int getWidth() {
		return _width;
	}
	
	protected int _width;
}
