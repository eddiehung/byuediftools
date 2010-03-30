package edu.byu.ece.edif.util.generate;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.IntegerTypedValue;


public abstract class WeightedModule extends Module {

    public static final String WEIGHT_PROPERTY = "BIT_WEIGHT";
    
    public WeightedModule(String name, CircuitGenerator parent, int weightOffset) {
        super(name, parent);
        _weightOffset = weightOffset;
    }
    
    protected void tagWeight(EdifCellInstance instance, int weight) {
        instance.addProperty(WEIGHT_PROPERTY, new IntegerTypedValue(weight));
    }

    protected int _weightOffset;
}
