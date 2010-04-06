package edu.byu.ece.edif.util.generate;

import edu.byu.ece.edif.core.EdifCellInstance;

public class RegisterCE extends WeightedModule {

    public RegisterCE(String name, int width, CircuitGenerator generator, int weight) {
        super(name, generator, weight);
        _width = width;
        _buildModule();
    }

    protected void _buildModule() {
        EdifCellInstance[] fdArray = new EdifCellInstance[_width];
        for (int i = 0; i < _width; i++) {
            fdArray[i] = _parent.addXilinxInstance("FDCE", _name+"_"+i);
            addPort("en", fdArray[i], "CE");
            addPort("rst", fdArray[i], "CLR");
            addPort("clk", fdArray[i], "C");
            addPort("din", i, fdArray[i], "D");
            addPort("dout", i, fdArray[i], "Q");
            tagWeight(fdArray[i], _weightOffset + i);
        }
    }
    
    protected int _width;
}
