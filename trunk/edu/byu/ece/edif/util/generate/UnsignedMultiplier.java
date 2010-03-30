package edu.byu.ece.edif.util.generate;
import edu.byu.ece.edif.core.EdifCellInstance;


public class UnsignedMultiplier extends WeightedModule {

	public UnsignedMultiplier(String name, int width, CircuitGenerator generator, int weight) {
		super(name, generator, weight);
		_width = width;
		_buildModule();
	}

	protected void _buildModule() {
		
		// create N - 1 adders of N bits each
		Adder[] adders = new Adder[_width-1];
		for (int j = 0; j < _width-1; j++) {
			adders[j] = new Adder(_name + "_adder_" + j, _width, _parent, j+1+_weightOffset);
		}
		
		// first row
		// bit 0
		EdifCellInstance bit0And = createAnd(0, 0);
		addPort("P", 0, bit0And, "O");
		
		// the rest of the bits except the last one
		for (int j = 0; j < _width-1; j++) {
			EdifCellInstance and0 = createAnd(1, j);
			EdifCellInstance and1 = createAnd(0, j+1);
			_parent.connect(and0, "O", adders[0].getPort("X", j));
			_parent.connect(and1, "O", adders[0].getPort("Y", j));
		}
		// the last bit
		EdifCellInstance and = createAnd(1, _width-1);
		_parent.connect(and, "O", adders[0].getPort("X", _width-1));
		_parent.connect(_parent.globalGND(), adders[0].getPort("Y", _width-1));
		
		
		// the rest of the rows
		for (int i = 1; i < _width-1; i++) {
			// all but the last bit
			for (int j = 0; j < _width-1; j++) {
				EdifCellInstance and1 = createAnd(i+1, j);
				_parent.connect(and1, "O", adders[i].getPort("X",j));
				_parent.connect(adders[i-1].getPort("S", j+1), adders[i].getPort("Y",j));
				
			}
			// last bit
			EdifCellInstance and1 = createAnd(i+1, _width-1);
			_parent.connect(and1, "O", adders[i].getPort("X", _width-1));
			_parent.connect(adders[i-1].getPort("S", _width), adders[i].getPort("Y", _width-1));
		}
		
		// collect the product bits
		for (int j = 1; j < _width; j++) {
			connectPort("P", j, adders[j-1].getPort("S", 0));
		}
		for (int j = _width,i=1; j < (2*_width-1); j++,i++) {
			connectPort("P", j, adders[_width-2].getPort("S", i));
		}
		connectPort("P", 2*_width-1, adders[_width-2].getPort("S", _width));
		
	}
	
	protected EdifCellInstance createAnd(int xBit, int yBit) {
		EdifCellInstance and = _parent.addXilinxInstance("AND2", _name+"X"+xBit+"Y"+yBit);
		tagWeight(and, xBit+yBit+_weightOffset);
		addPort("X", xBit, and, "I0");
		addPort("Y", yBit, and, "I1");
		return and;
	}
	
	protected int _width;

}
