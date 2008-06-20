/*
 * Organizes EdifCellInstances into logical groups.
 * 
 * Copyright (c) 2008 Brigham Young University
 * 
 * This file is part of the BYU EDIF Tools.
 * 
 * BYU EDIF Tools is free software: you may redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * 
 * BYU EDIF Tools is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * A copy of the GNU General Public License is included with the BYU EDIF Tools.
 * It can be found at /edu/byu/edif/doc/gpl2.txt. You may also get a copy of the
 * license at <http://www.gnu.org/licenses/>.
 * 
 */
package edu.byu.ece.edif.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifRuntimeException;

/**
 * This class organizes EdifCellInstances into logical groups. Each logical
 * group is a EdifCellInstanceCollection object.
 * <p>
 * This class is not atomic and supports the merging and creation of groups.
 * <p>
 * TODO: Let's make an interface for this called EdifCellInstanceGroupings and a
 * simple class that implements the interface.
 */
public class EdifCellInstanceGroupings {

    public EdifCellInstanceGroupings(EdifCell cell) {
        _cell = cell;
        _init();
    }

    // Copy Constructor
    protected EdifCellInstanceGroupings(EdifCellInstanceGroupings ecig) {
        _cell = ecig._cell; // Reference same cell
        // Create copies of internal Collections
        _edifCellInstanceGroups = new ArrayList<EdifCellInstanceCollection>(ecig._edifCellInstanceGroups);
        _edifCellInstanceGroupsMap = new LinkedHashMap<EdifCellInstance, EdifCellInstanceCollection>(
                ecig._edifCellInstanceGroupsMap);
    }

    public Object clone() {
        return new EdifCellInstanceGroupings(this);
    }

    protected void _init() {

        // 1. Initialize the groups collection.
        // 2. Initialize the instance to group map.
        // 3. Create a new SingleInstanceCollection for each instance.
        //    a. Add the new SingleInstanceCollection to the groups array
        //    b. Add the instance - SingleInstanceCollection pair to the map

        _edifCellInstanceGroups = new ArrayList<EdifCellInstanceCollection>(_cell.getSubCellList().size());
        _edifCellInstanceGroupsMap = new LinkedHashMap<EdifCellInstance, EdifCellInstanceCollection>(_cell
                .getSubCellList().size());
        for (EdifCellInstance eci : _cell.getSubCellList()) {
            //for (Iterator i = _cell.getSubCellList().iterator(); i.hasNext();) {
            //EdifCellInstance eci = (EdifCellInstance) i.next();
            EdifCellInstanceCollection ecic = new SingleInstanceCollection(eci);
            _edifCellInstanceGroups.add(ecic);
            _edifCellInstanceGroupsMap.put(eci, ecic);
        }
    }

    /**
     * Create a new EdifCellInstanceCollection group that contains all
     * EdifCellInstance objects from the specified collection ecis.
     * <p>
     * If any instance already belongs to a group, the entire existing group
     * becomes part of the new group, otherwise just the instance itself is
     * added to the group.
     * 
     * @param ecis Collection of EdifCellInstances to group with all the other
     * EdifCellInstances.
     * @return The resulting EdifCellInstanceCollection group containing all
     * instances from ecis.
     */
    public EdifCellInstanceCollection groupInstances(Collection<EdifCellInstance> ecis) {

        // 1. Get the groups (could be size 1) to which each instance belongs.
        //    Add all instances from each group to a new group.
        //    Remove all of the old groups.
        EdifCellInstanceCollection newGroup = new MultipleInstanceCollection();
        for (EdifCellInstance eci : ecis) {
            //for (Iterator i = ecis.iterator(); i.hasNext();) {
            //    EdifCellInstance eci = (EdifCellInstance) i.next();
            EdifCellInstanceCollection oldGroup = _edifCellInstanceGroupsMap.get(eci);
            // The group this Instance belonged to may have been removed already
            if (oldGroup != null) {
                newGroup.addAll(oldGroup);
                _edifCellInstanceGroups.remove(oldGroup);
            }
        }

        // 2. Add the new group to the groups array.
        _edifCellInstanceGroups.add(newGroup);

        // 3. Update the map so that every instance in the new group (key)
        //    points to the new group (value).
        updateMap(newGroup);

        return newGroup;
    }

    /**
     * Create a new EdifCellInstanceCollection that contains both
     * EdifCellInstance objects e1 and e2. If either of the instances already
     * belongs to a group, the entire existing group becomes part of the new
     * group.
     * 
     * @param e1 EdifCellInstance to group with other EdifCellInstance.
     * @param e2 EdifCellInstance to group with other EdifCellInstance.
     * @return The resulting EdifCellInstanceCollection group containing e1 and
     * e2.
     */
    public EdifCellInstanceCollection groupInstances(EdifCellInstance e1, EdifCellInstance e2) {

        // 1. Get the groups (could be size 1) to which each instance belongs.
        EdifCellInstanceCollection c1 = _edifCellInstanceGroupsMap.get(e1);
        EdifCellInstanceCollection c2 = _edifCellInstanceGroupsMap.get(e2);

        // 2. Remove the EdifCellInstanceCollection objects from the groups array.
        if (_edifCellInstanceGroups.remove(c1) == false)
            throw new EdifRuntimeException("Groups array and Groups map are not synchronized.");
        if (_edifCellInstanceGroups.remove(c2) == false)
            throw new EdifRuntimeException("Groups array and Groups map are not synchronized.");

        // 3. Merge the two groups
        return mergeCollectionsFast(c1, c2);
    }

    /**
     * Adds the contents of one existing group to annother existing group
     * (EdifCellInstanceCollection object). Deletes the first group while simply
     * adding to the second group.
     * 
     * @param group1 Group to take from and delete
     * @param group2 Group to add the ECIs to
     * @return Expanded group, or EdifCellInstanceCollection, containing all
     * instances from groups c1 and c2.
     * @throws EdifRuntimeException If this object's groups list does not
     * already contain "group".
     */
    public EdifCellInstanceCollection mergeGroupIntoGroup(EdifCellInstanceCollection group1,
            EdifCellInstanceCollection group2) {

        // 1. Throw exception if collections are not part of this groupings.
        if (!_edifCellInstanceGroups.contains(group1))
            throw new EdifRuntimeException("Trying to merge group which does not already exist.");
        if (!_edifCellInstanceGroups.contains(group2))
            throw new EdifRuntimeException("Trying to merge group which does not already exist.");
        // 2. Adds the ecis to the given group
        group2.addAll(group1);

        // 3. Remove old groups from the groups array.
        _edifCellInstanceGroups.remove(group1);

        // 4. Update the map so that every instance in the new group (key)
        //    points to the new group (value).
        updateMap(group2);

        return group2;
    }

    /**
     * Merges two groups, or EdifCellInstanceCollections.
     * 
     * @param c1 Group to merge with other group.
     * @param c2 Group to merge with other group.
     * @return Merged group, or EdifCellInstanceCollection, containing all
     * instances from groups c1 and c2.
     * @throws EdifRuntimeException If this object's groups list does not
     * already contain both c1 and c2.
     */
    public EdifCellInstanceCollection mergeCollections(EdifCellInstanceCollection c1, EdifCellInstanceCollection c2) {

        // 1. Throw exception if collections are not part of this groups.
        if (!_edifCellInstanceGroups.contains(c1))
            throw new EdifRuntimeException("Trying to merge group which does not already exist.");
        if (!_edifCellInstanceGroups.contains(c2))
            throw new EdifRuntimeException("Trying to merge group which does not already exist.");

        return mergeCollectionsFast(c1, c2);
    }

    /**
     * Merges two groups, or EdifCellInstanceCollections. (This version does not
     * check to see if the specified groups already exist. It is up to the
     * caller to guarantee this when calling this version of the method.)
     * 
     * @param c1 Group to merge with other group.
     * @param c2 Group to merge with other group.
     * @return Merged group, or EdifCellInstanceCollection, containing all
     * instances from groups c1 and c2.
     */
    private EdifCellInstanceCollection mergeCollectionsFast(EdifCellInstanceCollection c1, EdifCellInstanceCollection c2) {

        // 1. Merge the two groups to create a new group.
        EdifCellInstanceCollection newGroup = new MultipleInstanceCollection(c1, c2);

        // 2. Put the new EdifCellInstanceCollection group into the groups array.
        _edifCellInstanceGroups.add(newGroup);

        // 3. Remove old groups from the groups array.
        _edifCellInstanceGroups.remove(c1);
        _edifCellInstanceGroups.remove(c2);

        // 4. Update the map so that every instance in the new group (key)
        //    points to the new group (value).
        updateMap(newGroup);

        return newGroup;
    }

    /**
     * This method updates the Map of instances to groups. All instances
     * contained within the specified EdifCellInstanceCollection group ecic,
     * will now have ecic as the value to which they map.
     * 
     * @param ecic New group to update in the map.
     */
    protected void updateMap(EdifCellInstanceCollection ecic) {
        for (EdifCellInstance eci : ecic)
            _edifCellInstanceGroupsMap.put(eci, ecic);
    }

    /**
     * This method removes a collection of EdifCellInstances (held within and
     * EdifCellInstanceCollection object) from the groups array. The method
     * assumes that each EdifCellInstance has a corresponding
     * SingleInstanceCollection object in the groups array.
     * 
     * @param new_group The collection of EdifCellInstances which will have
     * their corresponding SingleInstanceCollection removed from the groups
     * array.
     */
    protected void removeOldSingleInstanceGroups(EdifCellInstanceCollection new_group) {
        for (EdifCellInstance eci : new_group) {
            //for (Iterator i = new_group.iterator(); i.hasNext();) {
            //    EdifCellInstance eci = (EdifCellInstance) i.next();
            EdifCellInstanceCollection singleInstanceGroup = _edifCellInstanceGroupsMap.get(eci);
            _edifCellInstanceGroups.remove(singleInstanceGroup);
        }
    }

    /**
     * Get a copy of the group to which the specified cell instance belongs.
     * 
     * @param eci EdifCellInstance for which its group is requested.
     * @return Copy of the EdifCellInstanceCollection group to which the
     * specified EdifCellInstance belongs.
     */
    public EdifCellInstanceCollection getGroup(Object eci) {
        return _edifCellInstanceGroupsMap.get(eci);
    }

    /**
     * Get a copy of the list of all the groups. Each item in the Collection is
     * of type EdifCellInstanceCollection.
     * 
     * @return Collection of the EdifCellInstanceCollection groups.
     */
    public Collection<EdifCellInstanceCollection> getInstanceGroups() {
        // create a new copy of the collections. Don't allow them
        // to mess it up.
        return new ArrayList<EdifCellInstanceCollection>(_edifCellInstanceGroups);
    }

    public int getNumberGroups() {
        return _edifCellInstanceGroups.size();
    }

    /**
     * Retain all groups in the passed-in Collection of
     * EdifCellInstanceCollection objects. Removes all references to the rest of
     * the ECIs and their corresponding groups.
     * 
     * @param ecis Collection of EdifCellInstanceCollection to retain
     */
    public void retainGroups(Collection<EdifCellInstanceCollection> ecics) {
        _edifCellInstanceGroups.retainAll(ecics);
        _edifCellInstanceGroupsMap.values().retainAll(ecics);
    }

    /** Reference to the cell to which the groupings in this class belong. * */
    EdifCell _cell;

    /** Collection of the groupings created for the EdifCell object _cell * */
    Collection<EdifCellInstanceCollection> _edifCellInstanceGroups;

    /**
     * Map between each EdifCellInstance in the EdifCell object _cell to an
     * EdifCellInstanceCollection grouping of EdifCellInstances. Note that a
     * group can be a single instance.
     */
    Map<EdifCellInstance, EdifCellInstanceCollection> _edifCellInstanceGroupsMap;
}
