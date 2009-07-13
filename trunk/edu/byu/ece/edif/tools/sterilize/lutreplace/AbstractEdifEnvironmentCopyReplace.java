package edu.byu.ece.edif.tools.sterilize.lutreplace;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;

/**
 * This is the base class for doing a copy/replace operation on an EdifEnvironment
 * (copying the EdifEnvironment but replacing certain instances of it with others
 * along the way). Subclasses should override the addChildEdifCellInstance method
 * in order to specify which instances should be replaced. Instances to be replaced
 * should have a SimpleReplacementContext created for them and be added into the
 * _oldInstancesToReplace map (see the BasicEdifEnvironmentCopyReplace class for an
 * example).
 */
public abstract class AbstractEdifEnvironmentCopyReplace extends EdifEnvironmentCopy {

    protected AbstractEdifEnvironmentCopyReplace(EdifEnvironment env) throws EdifNameConflictException {
        super(env);
    }

    public Collection<ReplacementContext> getReplacementContexts() {
        return  _oldInstancesToReplace.values();
    }

    /**
     * This method overloads the default parent addEdifPortRef.
     * 
     * This method will examine the EdifPortRef and add it to the
     * net if it attaches to a top-level port or an instance that
     * is not being replaced.
     * 
     * If it is attached to an instance that is being replaced,
     * it will add the information to the ReplacementContext so
     * that the replacement can be hooked up at a later time.
     */
    protected void addEdifPortRef(EdifNet newNet, EdifPortRef oldRef) {
    	// Check to see if port ref is attached to a replaced cell
    	ReplacementContext context = attachesToReplacedCell(oldRef); 
    	if ( context == null)
    		// Doesn't match - call super to hook up port
    		super.addEdifPortRef(newNet, oldRef);
    	else {
    		// Matches. Add information to the replacement context.
    		
    		// Get the EdifSingleBitPort from the oldEPR
    		EdifSingleBitPort oldSBPort = oldRef.getSingleBitPort();
    		context.addOldSBPortNewNetAssociation(oldSBPort,newNet);
    
    		// Later, when the replacement logic needs to be hooked up, this
    		// information is used to find the new net to use. This code will
    		// have a reference to the old instance and iterate over each SBPort.
    		// For each SBPort it will find the "new net" and hook up the new logic
    		// to this new net.
    	}
    }

    /**
     * This method will check an old EdifPortRef and see if it connects to an
     * instance that is being replaced. If it is, it returns the ReplacementContext
     * associated with this instance. If it does not, it returns null.
     * 
     * @param oldRef
     * @return
     */
    protected ReplacementContext attachesToReplacedCell(EdifPortRef oldRef) {
    	EdifCellInstance oldInstance = oldRef.getCellInstance();
    	if (oldInstance == null)
    		// The EdifPortRef connects to a top-level port. No replacement occurs.
    		return null;
    	ReplacementContext context =_oldInstancesToReplace.get(oldInstance); 
    	// if the instance is not being replaced, then a null will be returned.
    	return context;
    }
    
    protected HashMap<EdifCellInstance, ReplacementContext> _oldInstancesToReplace = new LinkedHashMap<EdifCellInstance, ReplacementContext>();

}