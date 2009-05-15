package edu.byu.ece.edif.tools.sterilize.halflatch;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.tools.sterilize.lutreplace.BasicReplacementContext;

public class HalfLatchReplacementContext extends BasicReplacementContext {

    /**
     * There are two types of instances to be replaced: sensitive instances and constant instances.
     * 
     * @param newParent
     * @param oldInstance
     * @param isSensitive true if this is a sensitive instance, false if it is a constant instance
     */
    public HalfLatchReplacementContext(EdifCell newParent, EdifCellInstance oldInstance, boolean isSensitive) {
        super(newParent, oldInstance);
        _isSensitive = isSensitive;
    }
    
    public boolean isSensitive() {
        return _isSensitive;
    }
    
    public boolean isConstant() {
        return !_isSensitive;
    }
    
    protected boolean _isSensitive;
    
}
