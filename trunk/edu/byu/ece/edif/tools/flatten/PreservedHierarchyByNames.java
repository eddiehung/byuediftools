package edu.byu.ece.edif.tools.flatten;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;

/**
 * The purpose of this class is to keep track of the hierarchy that used to
 * exist in a design before flattening. It is designed to do so without the
 * need for references to actual EdifCell, EdifCellInstance, EdifNet, etc.
 * objects. Instead it keeps track of the names of the objects in the
 * flattened design and their mapping from the original design hierarchy.
 * 
 * The class has three main object references: the top InstanceNode from the
 * FlattenedEdifCell (this keeps track of all the hierarchy in terms of
 * original names of cells/instances/nets in a tree made up of HierarchicalInstances
 * and HierarchicalNets), a Map<HierarchicalInstance, String> (this
 * keeps a mapping from InstanceNodes to names of instances in the new,
 * flattened design), and a Map<HierarchicalNet, String> (this keeps a
 * mapping from HierarchicalNets to the name of the net in the new, flattened
 * design to which each HierarchicalNet corresponds).
 * 
 * By using only name references, this class makes it possible for hierarchy
 * information to be preserved even after an EdifEnvironment goes through a
 * transformation such as SRL replacement or half-latch removal (in these
 * cases, the EdifEnvironment is copied so object references don't match up
 * which is why name references are needed)
 * 
 * @author jonjohn
 *
 */
public class PreservedHierarchyByNames implements Serializable {
    
    public PreservedHierarchyByNames(FlattenedEdifCell flatCell) {
        _topNode = flatCell.getTopInstanceNode();
        _instanceNameMap = new LinkedHashMap<HierarchicalInstance, String>();
        _netNameMap = new LinkedHashMap<HierarchicalNet, String>();
        
        Map<HierarchicalInstance, FlattenedEdifCellInstance> instanceHierarchyMap = flatCell.getInstanceHierarchyMap();
        Map<HierarchicalNet, EdifNet> netHierarchyMap = flatCell.getNetHierarchyMap();
        
        for (HierarchicalInstance hi : instanceHierarchyMap.keySet()) {
            FlattenedEdifCellInstance instance = instanceHierarchyMap.get(hi);
            _instanceNameMap.put(hi, instance.getName());
        }
        
        for (HierarchicalNet hn : netHierarchyMap.keySet()) {
            EdifNet net = netHierarchyMap.get(hn);
            _netNameMap.put(hn, net.getName());
        }
        
        _hierarchyNaming = BasicHierarchyNaming.DEFAULT_BACKSLASH_NAMING;
    }
    
    public String getFlatInstanceName(HierarchicalInstance hInstance) {
        return _instanceNameMap.get(hInstance);
    }
    
    public EdifCellInstance getFlatInstance(HierarchicalInstance hInstance, EdifCell flatCell) {
        return flatCell.getCellInstance(getFlatInstanceName(hInstance));
    }
    
    public String getFlatInstanceName(String hierarchicalName) {
        return getFlatInstanceName(getHierarchicalInstance(hierarchicalName));
    }
    
    public EdifCellInstance getFlatInstance(String hierarchicalName, EdifCell flatCell) {
        return getFlatInstance(getHierarchicalInstance(hierarchicalName), flatCell);
    }
    
    public HierarchicalInstance getHierarchicalInstance(String hierarchicalName) {
        return _hierarchyNaming.getHierarchicalInstance(_topNode, hierarchicalName);
    }
    
    public String getFlatNetName(HierarchicalNet hNet) {
        return _netNameMap.get(hNet);
    }
    
    public EdifNet getFlatNet(HierarchicalNet hNet, EdifCell flatCell) {
        return flatCell.getNet(getFlatNetName(hNet));
    }
    
    public String getFlatNetName(String hierarchicalName) {
        return getFlatNetName(getHierarchicalNet(hierarchicalName));
    }
    
    public EdifNet getFlatNet(String hierarchicalName, EdifCell flatCell) {
        return flatCell.getNet(getFlatNetName(hierarchicalName));
    }
    
    public HierarchicalNet getHierarchicalNet(String hierarchicalName) {
        return _hierarchyNaming.getHierarchicalNet(_topNode, hierarchicalName);
    }
    
    public Collection<EdifCellInstance> getInstancesWithin(String hierarchicalName, EdifCell flatCell) {
        Collection<EdifCellInstance> result = new ArrayList<EdifCellInstance>();
        HierarchicalInstance hInstance = _hierarchyNaming.getHierarchicalInstance(_topNode, hierarchicalName);        
        if (hInstance == null)
            return result;
        LinkedList<HierarchicalInstance> bfsTraversalList = new LinkedList<HierarchicalInstance>();
        bfsTraversalList.add(hInstance);
        while (!bfsTraversalList.isEmpty()) {
            HierarchicalInstance currentNode = bfsTraversalList.poll();
            EdifCellInstance matchingInstance = flatCell.getCellInstance(_instanceNameMap.get(currentNode));
            if (matchingInstance != null)
                result.add(matchingInstance);
            for (HierarchicalInstance childNode : currentNode.getChildren())
                bfsTraversalList.add(childNode);
        }
        return result;
    }
    
    /**
     * Given a HierarchicalInstance node, get a Collection of
     * FlattenedEdifCellInstances that are leaf instances that would be "within"
     * the original instance associated with the node.
     * 
     * @param instanceNode the HierarchicalInstance node corresponding to an
     * original instance
     * @return a Collection of FlattenedEdifCellInstances that correspond to
     * original instances "within" the instance associated with the given
     * HierarchicalInstance node
     */
    protected Collection<EdifCellInstance> getInstancesWithinNode(HierarchicalInstance instanceNode, EdifCell topCell) {
        Collection<EdifCellInstance> result = new ArrayList<EdifCellInstance>();
        if (instanceNode == null)
            return result;
        LinkedList<HierarchicalInstance> bfsTraversalList = new LinkedList<HierarchicalInstance>();
        bfsTraversalList.add(instanceNode);
        while (!bfsTraversalList.isEmpty()) {
            HierarchicalInstance currentNode = bfsTraversalList.poll();
            EdifCellInstance matchingInstance = getFlatInstance(currentNode, topCell);
            if (matchingInstance != null)
                result.add(matchingInstance);
            for (HierarchicalInstance childNode : currentNode.getChildren())
                bfsTraversalList.add(childNode);
        }
        return result;
    }
    
    public Collection<EdifCellInstance> getInstancesWithinCellTypes(Collection<String> cellTypes, EdifCell topCell) {
        LinkedList<HierarchicalInstance> bfsTraversalList = new LinkedList<HierarchicalInstance>();
        Collection<EdifCellInstance> result = new LinkedHashSet<EdifCellInstance>();
        bfsTraversalList.add(_topNode);
        while (!bfsTraversalList.isEmpty()) {
            HierarchicalInstance currentNode = bfsTraversalList.poll();
            for (String cellType : cellTypes) {
                if (currentNode.getCellTypeName().equalsIgnoreCase(cellType)) {
                    result.addAll(getInstancesWithinNode(currentNode, topCell));
                }
                bfsTraversalList.addAll(currentNode.getChildren());
            }
        }
        return result;
    }
    
    protected HierarchicalInstance _topNode;
    protected Map<HierarchicalInstance, String> _instanceNameMap;
    protected Map<HierarchicalNet, String> _netNameMap;
    
    protected HierarchyNaming _hierarchyNaming;
}
