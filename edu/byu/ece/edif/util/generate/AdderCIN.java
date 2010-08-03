package edu.byu.ece.edif.util.generate;

public class AdderCIN extends WeightedModule {

	public AdderCIN(String name, int width, CircuitGenerator generator, int weight) {
		super(name, generator, weight);
		_width = width;
		_buildModule();
	}
	
	protected void _buildModule() {
		_buildModule(Integer.MAX_VALUE);
	}
	
	/*
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
	*/

	/**
	 * Includes an alternate way of tagging weights. This is used by the SignedMutliplier
	 * when tagging weights according to a diagonal-weighted multiplier.
	 * 
	 * If not interested in the diagonal multiplier-style adder weights,
	 * just call with Integer.MAX_VALUE to nullify the effect.
	 */
	public AdderCIN(String name, int width, CircuitGenerator generator, int weight, int row) {
		super(name, generator, weight);
		_width = width;
		_buildModule(row);
	}

	/**
	 * Includes an alternate way of tagging weights. This is used by the SignedMutliplier
	 * when tagging weights according to a diagonal-weighted multiplier.
	 * 
	 * If not interested in the diagonal multiplier-style adder weights,
	 * just call with Integer.MAX_VALUE to nullify the effect.
	 */
	protected void _buildModule(int row) {
		
		// create full adders
		FullAdder[] fullAdders = new FullAdder[_width];
		for (int i = 0; i < _width; i++) {
			// Don't let the module weight exceed it's row value (for diagonal-weighted multipliers)
			int weight = (row < i) ? row : i;
			weight += _weightOffset;
			fullAdders[i] = new FullAdder(_name+"_fa_"+i, _parent, weight);
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
