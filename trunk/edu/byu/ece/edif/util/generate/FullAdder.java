package edu.byu.ece.edif.util.generate;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.StringTypedValue;


public class FullAdder extends WeightedModule {

	public FullAdder(String name, CircuitGenerator parent, int weight) {
		super(name, parent, weight);
		_buildModule();
	}
	
	protected void _buildModule() {
		
		EdifCellInstance lut = _parent.addXilinxInstance("LUT2", "lut_" + _name);
		EdifCellInstance and = _parent.addXilinxInstance("MULT_AND", "and_" + _name);
		EdifCellInstance muxcy = _parent.addXilinxInstance("MUXCY", "mux_" + _name);
		EdifCellInstance xorcy = _parent.addXilinxInstance("XORCY", "xor_" + _name);
		
		tagWeight(lut, _weightOffset);
		tagWeight(and, _weightOffset);
		tagWeight(muxcy, _weightOffset);
		tagWeight(xorcy, _weightOffset);		
		
		lut.addProperty("INIT", new StringTypedValue("6"));
		
		addPort("X", lut, "I1");
		addPort("Y", lut, "I0");
		addPort("S", xorcy, "O");
		addPort("CIN", muxcy, "CI");
		addPort("COUT", muxcy, "O");
		
		_parent.connect(lut, "I1", and, "I1");
		_parent.connect(lut, "I0", and, "I0");
		_parent.connect(muxcy, "CI", xorcy, "CI");
		_parent.connect(lut, "O", muxcy, "S");
		_parent.connect(lut, "O", xorcy, "LI");
		_parent.connect(and, "LO", muxcy, "DI");
		
	}
	
	protected int _weight;
}
