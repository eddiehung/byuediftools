package edu.byu.ece.edif.util.generate;
import edu.byu.ece.edif.core.EdifCellInstance;


public class SignedMultiplier extends WeightedModule {

	public SignedMultiplier(String name, int width, CircuitGenerator generator, int weight) {
		super(name, generator, weight);
		_width = width;
		_buildModule();
	}

	protected void _buildModule() {

	    // create N - 1 adders of N bits each
	    Module[] adders = new Module[_width-1];
	    for (int j = 0; j < _width-1; j++) {
	        if (j == (_width-2)) {
	            // this is for signed multiplication. This row need as a carry-in the most significant bit
	            // of the multiplier (X)
	            adders[j] = new AdderCIN(_name + "_adder_" + j, _width, _parent, j+1+_weightOffset);
	            connectPort("X", _width-1, adders[j].getPort("CIN"));
	        }
	        else {
	            adders[j] = new Adder(_name + "_adder_" + j, _width, _parent, j+1+_weightOffset);
	        }
	    }

		// first row
		// bit 0
		EdifCellInstance bit0And = createAndOrNand(0, 0);
		addPort("P", 0, bit0And, "O");
		
		// the rest of the bits except the last one
		for (int j = 0; j < _width-1; j++) {
			EdifCellInstance and0 = createAndOrNand(1, j);
			EdifCellInstance and1 = createAndOrNand(0, j+1);
			_parent.connect(and0, "O", adders[0].getPort("X", j));
			_parent.connect(and1, "O", adders[0].getPort("Y", j));
		}
		// the last bit
		EdifCellInstance and = createAndOrNand(1, _width-1);
		_parent.connect(and, "O", adders[0].getPort("X", _width-1));
		_parent.connect(_parent.globalGND(), adders[0].getPort("Y", _width-1));
		
		// the rest of the rows
		for (int i = 1; i < _width-1; i++) {
			// all but the last bit
			for (int j = 0; j < _width-1; j++) {
				EdifCellInstance and1 = createAndOrNand(i+1, j);
				_parent.connect(and1, "O", adders[i].getPort("X",j));
				_parent.connect(adders[i-1].getPort("S", j+1), adders[i].getPort("Y",j));
				
			}
			// last bit
			EdifCellInstance and1 = createAndOrNand(i+1, _width-1);
			_parent.connect(and1, "O", adders[i].getPort("X", _width-1));
			_parent.connect(adders[i-1].getPort("S", _width), adders[i].getPort("Y", _width-1));
		}
		
		// collect the product bits
		for (int j = 1; j < _width-1; j++) {
			connectPort("P", j, adders[j-1].getPort("S", 0));
		}
		
		// add the final ripple stage
		Adder finalAdder = new Adder(_name+"_fAdder", _width+1, _parent, _width-1+_weightOffset);
		for (int i = 0; i < _width+1; i++) {
		    _parent.connect(finalAdder.getPort("X", i), adders[_width-2].getPort("S", i));
		    if (i == 0 || i == _width) {
		        _parent.connect(finalAdder.getPort("Y", i), _parent.globalVCC());
		    }
		    else {
		        _parent.connect(finalAdder.getPort("Y", i), _parent.globalGND());
		    }
		    connectPort("P", _width-1+i, finalAdder.getPort("S", i));
		}
		
	}
	
	protected EdifCellInstance createAndOrNand(int xBit, int yBit) {
		String andType = "AND2";
		String bSuffix = "";
		if ((xBit <= _width-1) && (yBit == _width-1)) {
			andType = "NAND2";
		}
		if (xBit == _width-1) {
		    bSuffix = "B1";
		}
		EdifCellInstance and = _parent.addXilinxInstance(andType+bSuffix, _name+"X"+xBit+"Y"+yBit);
		tagWeight(and, xBit + yBit + _weightOffset);
		addPort("X", xBit, and, "I1");
		addPort("Y", yBit, and, "I0");
		return and;
	}
	
	protected int _width;
}
