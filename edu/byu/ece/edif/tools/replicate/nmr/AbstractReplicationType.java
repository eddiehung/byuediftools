package edu.byu.ece.edif.tools.replicate.nmr;

import java.util.ArrayList;
import java.util.List;

import edu.byu.ece.edif.core.BasicEdifBusNamingPolicy;
import edu.byu.ece.edif.core.BasicEdifBusNetNamingPolicy;
import edu.byu.ece.edif.core.EdifBusNamingPolicy;
import edu.byu.ece.edif.core.EdifBusNetNamingPolicy;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.core.RenamedObject;

/**
 * In this framework, a ReplicationType represents the number of times to
 * replicate things (replication factor), how to restore signals (i.e. voting),
 * and how to name replicated objects. The class also contains code for performing
 * replications.
 */
public abstract class AbstractReplicationType implements ReplicationType {

    /**
     * Initialize a ReplicationType.
     * 
     * @param replicationFactor
     * @param replicationSuffixes
     */
    protected AbstractReplicationType(int replicationFactor, String replicationName) {
        _replicationFactor = replicationFactor;
        _replicationName = replicationName;
    }

    /**
     * Get the replication factor associated with the replication type.
     * 
     */
    public int getReplicationFactor() {
        return _replicationFactor;
    }
    
    /**
     * Create a new String that incorporates the NMR domain number into the
     * name.
     * 
     * @param origName The original name
     * @param domain The domain number
     * @return the new NamedObject with the new name
     */
    public EdifNameable getReplicationInstanceNameable(EdifNameable origName, int domain) {
        String newName = getReplicationString(origName.getName(), domain);
        return NamedObject.createValidEdifNameable(newName);
    }

    /**
     * Create an EdifNameable for replicated EdifNets. Handles three different
     * cases.
     * <ol>
     * <li> Single name (i.e. (net xp_out)): could turn into (net xp_out_TMR_0)
     * <li> Double named, not a bus member (i.e. (net (rename xp_out__ "xp_out_?"))):
     * could turn into (net (rename xp_out___TMR_0 "xp_out_?_TMR_0"))
     * <li> Double name, bus member (i.e. (net (rename xp_out_5 "xp_out(5)"))):
     * could turn into (net (rename xp_out_TMR_0_5 "xp_out_TMR_0(5)"))
     * </ol>
     */
    public EdifNameable getReplicationNetNameable(EdifNameable origName, int domain) {
        EdifNameable result = null;
        String name = origName.getName();
        String oldName = origName.getOldName();
        if (origName instanceof RenamedObject) {
            EdifBusNetNamingPolicy oldNamePolicy = BasicEdifBusNetNamingPolicy.EdifBusNetNamingPolicy(oldName);
            EdifBusNetNamingPolicy namePolicy = BasicEdifBusNetNamingPolicy.EdifBusNetNamingPolicy(name);
            if (oldNamePolicy == null) {
                EdifNameable newName = NamedObject.createValidEdifNameable(getReplicationString(name, domain));
                String newOldName = getReplicationString(oldName, domain);
                result = new RenamedObject(newName, newOldName);
            } else {
                int oldBitNumber = oldNamePolicy.getBusPosition(oldName);
                EdifNameable newName = null;
                if (namePolicy == null) {
                    newName = NamedObject.createValidEdifNameable(getReplicationString(name, domain));
                } else {
                    int bitNumber = namePolicy.getBusPosition(name);
                    String base = namePolicy.getBusBaseName(name);
                    newName = NamedObject.createValidEdifNameable(base + getReplicationSuffix(domain) + "_" + bitNumber);
                }
                String newOldName = oldNamePolicy.getBusBaseName(oldName) + getReplicationSuffix(domain)
                        + oldNamePolicy.generateBitSuffix(oldBitNumber);
                result = new RenamedObject(newName, newOldName);
            }
        } else {
            result = NamedObject.createValidEdifNameable(getReplicationString(name, domain));
        }
        return result;
    }

    /**
     * Create a new EdifNameable object that incorporates the NMR domain number
     * into the name. This method will identify renamed objects and attempt to
     * create a unique name that incorporates the domain number. Specifically,
     * this will attempt to preserve any bus numbering that is found in the
     * renamed object.
     * 
     * @param name The original name
     * @param domain The domain number
     * @return The new NamedObject or the RenamedObject, as appropriate.
     */
    public EdifNameable getReplicationPortNameable(EdifNameable name, int domain) {
        EdifNameable newName = getReplicationInstanceNameable(name, domain);
        if (name instanceof RenamedObject) {
            String rename = ((RenamedObject) name).getOldName();

            // See if the old name is a "bus" name. If so,
            // create a new valid "bus" name.
            EdifBusNamingPolicy policy = BasicEdifBusNamingPolicy.EdifBusNamingPolicy(rename);
            if (policy != null) {
                String renameString = getReplicationString(policy.getBusBaseName(rename), domain);
                renameString += policy.getBusRangeSpecifier(rename);
                return new RenamedObject(newName, renameString);
            }
        }
        return new NamedObject(newName);
    }
    
    /**
     * Create a new String that incorporates the NMR domain number into the
     * name. Note that this is NOT necessarily a valid EDIF name. The origName
     * can be invalid and thus the resulting concatenated String could be
     * invalid.
     * 
     * @param origName The original name
     * @param domain The domain number
     * @return the String with the replication suffix and domain number.
     */
    protected String getReplicationString(String origName, int domain) {
        return origName + getReplicationSuffix(domain);
    }

    /**
     * Create a new String to be added to the end of a triplicated element that
     * incorporates the NMR domain number.
     * 
     * @param domain The domain number
     * @return the String with the replication suffix and domain number.
     */
    protected String getReplicationSuffix(int domain) {
        return "_" + domain;
    }
    
    
    /**
     * Create the <code>_replicationFactor</code> copies of the given instance using
     * the naming scheme provided by the replication type.
     */
    public List<EdifCellInstance> replicate(EdifCellInstance oldInstance, EdifCell newCellDefinition, EdifCell newTopCell) throws EdifNameConflictException {
        
        List<EdifCellInstance> newInstances = new ArrayList<EdifCellInstance>(_replicationFactor);
        
        // Create instances, copy properties, add to top cell
        for (int i = 0; i < _replicationFactor; i++) {
            EdifNameable newInstanceName = null;
            if (_replicationFactor > 1)
                newInstanceName = getReplicationInstanceNameable(oldInstance.getEdifNameable(), i);
            else
                newInstanceName = oldInstance.getEdifNameable();
            
            EdifCellInstance newInstance = new EdifCellInstance(newInstanceName, newTopCell, newCellDefinition);
            
            newInstance.copyProperties(oldInstance);
            
            newTopCell.addSubCell(newInstance);
            
            newInstances.add(newInstance);            
        }
        
        return newInstances;      
    }

    /**
     * Create the <code>_replicationFactor</code> copies of the given port using
     * the naming scheme provided by the replication type.
     */
    public List<EdifPort> replicate(EdifPort oldPort, EdifCell newTopCell) throws EdifNameConflictException {
        List<EdifPort> newPorts = new ArrayList<EdifPort>(_replicationFactor);
        // Create pots, copy properties, add to top cell
        for (int i = 0; i < _replicationFactor; i++) {
            EdifNameable newPortName = null;
            if (_replicationFactor > 1)
                newPortName = getReplicationPortNameable(oldPort.getEdifNameable(), i);
            else
                newPortName = oldPort.getEdifNameable();
            
            EdifPort newPort =  newTopCell.addPort(newPortName, oldPort.getWidth(), oldPort.getDirection());
            
            newPort.copyProperties(oldPort);
            
            newPorts.add(newPort);
        }
        
        return newPorts;
    }
    
    public String toString() {
    	return _replicationName;
    }
    
    protected int _replicationFactor;
    
    protected String _replicationName;
}
