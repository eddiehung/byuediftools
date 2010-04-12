package edu.byu.ece.edif.util.export.serialize;

import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.tools.flatten.PreservedHierarchyByNames;

/**
 * This class represents the contents of a .jedif file. The contents of a
 * .jedif file can be either (1) a single EdifEnvironment object or (2) an
 * EdifEnvironment object and a PrerservedHierarchyByNames object which
 * represents the original hierarchy of a flattened design.
 * 
 * A JEdifFileContents object must have an EdifEnvironment object but may or
 * may not include a PreservedHierarchyByNames object.
 */
public class JEdifFileContents {

    public JEdifFileContents(EdifEnvironment env) {
        this(env, null);
    }
    
    public JEdifFileContents(EdifEnvironment env, PreservedHierarchyByNames hierarchy) {
        _env = env;
        _hierarchy = hierarchy;
    }
    
    public EdifEnvironment getEdifEnvironment() {
        return _env;
    }
    
    public PreservedHierarchyByNames getHierarchy() {
        return _hierarchy;
    }
    
    public boolean hasHierarchy() {
        return (_hierarchy != null);
    }
    
    protected EdifEnvironment _env;
    protected PreservedHierarchyByNames _hierarchy;
}
