package edu.byu.ece.edif.util.generate;

public class SignedAdder extends WeightedModule {

    public SignedAdder(String name, int width, CircuitGenerator generator, int weight) {
        super(name, generator, weight);
        _width = width;
        _buildModule();
    }
    
    protected void _buildModule() {
        
        Adder unsignedAdder = new Adder(_name+"_u", _width+1, _parent, _weightOffset);
        
        // wiring
        for (int i = 0; i < _width; i++) {
            connectPort("X", i, unsignedAdder.getPort("X", i));
            connectPort("Y", i, unsignedAdder.getPort("Y", i));
            connectPort("S", i, unsignedAdder.getPort("S", i));
        }
        // sign extension by 1 bit
        connectPort("X", _width-1, unsignedAdder.getPort("X", _width));
        connectPort("Y", _width-1, unsignedAdder.getPort("Y", _width));
        connectPort("S", _width, unsignedAdder.getPort("S", _width));
    }
    
    protected int _width;
}
