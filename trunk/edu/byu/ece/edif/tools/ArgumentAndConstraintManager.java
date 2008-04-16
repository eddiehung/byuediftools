/*
 * TODO: Insert class description here.
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
package edu.byu.ece.edif.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * @author ksm4
 * @since Created on Jun 29, 2005
 */
public class ArgumentAndConstraintManager {

    public ArgumentAndConstraintManager(String args[]) {
        init(); // must come first
        parseArguments(args);
    }

    private void init() {
        _mandatoryArgs = new Vector();
        _argMap = new LinkedHashMap();
    }

    public boolean containsArgument(String flag_) {
        String flag = convertToDashlessFlag(flag_);
        // If the argument map is null, or this
        // flag (key) wasn't mapped return null
        if (_argMap == null || !_argMap.containsKey(flag))
            return false;
        else
            return true;
    }

    protected String convertToDashlessFlag(String flag) {
        // If the String flag doesn't already start with a
        // dash ("-"), then do nothing.  Just return the flag.
        // Otherwise, return a substring of flag starting
        // at index 1 (0 based numbering, 1 is included).
        // This just gets rid of the dash.
        if (!flag.startsWith("-"))
            return flag;
        else
            return flag.substring(1);
    }

    public Set getComplexOptionalArgument(String flag_) {
        String flag = convertToDashlessFlag(flag_);

        // If the argument map is null, or this
        // flag (key) wasn't mapped return null
        if (!containsArgument(flag))
            return null;

        // Get the collection to which this flag, or key, is mapped.
        // This should be a collection of collections.
        return (HashSet) _argMap.get(flag);
    }

    public String getMandatoryArgument(int index) {
        // Make sure that the mandatory args variable isn't null
        // and that they aren't trying to access a variable index
        // out of bounds.
        if (_mandatoryArgs != null && index < _mandatoryArgs.size())
            return (String) _mandatoryArgs.get(index);
        else
            return null;
    }

    public Set getSimpleOptionalArgument(String flag_) {
        String flag = convertToDashlessFlag(flag_);

        // If the argument map is null, or this
        // flag (key) wasn't mapped return null
        if (!containsArgument(flag))
            return null;

        // Get the collection to which this flag, or key, is mapped.
        // The key is mapped to a collection of collections,
        // but since they asked for a simple optional arg,
        // we are going to assume that the 'collections' all have
        // one value (a String).  So we will just convert what we
        // get into a single collection of Strings for them.
        Set mappedCollection = (HashSet) _argMap.get(flag);
        Set singleStringCollection = new LinkedHashSet();

        // This is the conversion to a single collection of strings.
        // For each collection in the collection of collections,
        // we get the first (.next()) object from that collection
        // assume it is a String and add it to our new holding
        // set called singleStringCollection.
        for (Iterator j = mappedCollection.iterator(); j.hasNext();) {
            ArrayList nextValuesCollection = (ArrayList) j.next();
            singleStringCollection.add((String) (nextValuesCollection.iterator().next()));
        }

        return singleStringCollection;
    }

    public void parseArguments(String args[]) {

        // Go through and extract the mandatory arguments
        // (Mandatory args are those which come first in the
        // arg array.  Once the first optional arg (the first
        // arg which has a -) is seen, the mandatory arguments
        // end.)
        int k = 0;
        while (k < args.length) {
            if (args[k].startsWith("-")) // optional arg found
                break;
            else
                _mandatoryArgs.add(k, args[k]); // add mandatory arg
            k++;
        }

        // Go through and extract the optional arguments
        // and put them in the argMap.
        // Start at k, or where the mandatory arg loop left off.
        for (int i = k; i < args.length; i++) {

            String key = args[i];

            // First check to see if we are looking at a 'flag'
            // argument (begins with a -), i.e. we are looking
            // at a key for the map.
            // If not, continue on to the next argument.
            if (!key.startsWith("-"))
                continue;

            // First check to see if this key has a previous
            // mapping.  If so, get the collection of collections (previousMapCollections)
            // to which it was previously mapped.
            Set previousMapCollections = null;
            if (_argMap.containsKey(key))
                previousMapCollections = (HashSet) _argMap.get(key);

            // Next, create a new collection to hold the old (where applicable)
            // and new collections associated with this key. (These collections hold 0 or more values)
            // If there was a previous mapping, add the previous collections
            // to which it was mapped to newMapCollections.
            Set newMapCollections = new LinkedHashSet();
            if (previousMapCollections != null && previousMapCollections.size() > 0)
                newMapCollections.addAll(previousMapCollections);

            // Now, grab the collection of strings with which this argument
            // is associated.
            ArrayList newValuesCollection = new ArrayList();
            int j = i + 1;
            while (j < args.length && !args[j].startsWith("-")) {
                newValuesCollection.add(args[j]);
                j++;
            }

            // Now add the newValuesCollection to the newMapCollections
            newMapCollections.add(newValuesCollection);

            // Now put the newMapCollections into the map
            _argMap.put(convertToDashlessFlag(key), newMapCollections);
        }
    }

    protected Vector _mandatoryArgs;

    protected Map _argMap;
}
