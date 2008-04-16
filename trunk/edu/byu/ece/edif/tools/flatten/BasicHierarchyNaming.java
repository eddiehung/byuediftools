/*
 * A basic implementation of the HierarchyNaming interface
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
package edu.byu.ece.edif.tools.flatten;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;

//////////////////////////////////////////////////////////////////////////
//// BasicHierarchyNaming
/**
 * A basic implementation of the HierarchyNaming interface
 */
public class BasicHierarchyNaming implements HierarchyNaming {

    public BasicHierarchyNaming(String separator, boolean includeRootName) {
        _separator = separator;
        _includeRootName = includeRootName;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public variables                  ////

    public static final HierarchyNaming DEFAULT_BACKSLASH_NAMING = new BasicHierarchyNaming("/", false);

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    // javadoc inherited from interface
    public HierarchicalInstance getHierarchicalInstance(HierarchicalInstance head, String name) {

        Collection<String> hierarchyNames = Arrays.asList(name.split(Pattern.quote(_separator)));
        Iterator<String> it = hierarchyNames.iterator();
        if (!it.hasNext())
            return null;

        HierarchicalInstance currentNode = head;
        // check to see that the top instance matches, if required
        if (_includeRootName) {
            String currentName = it.next();
            if (!currentName.equals(currentNode.getInstance().getOldName()))
                return null;
            if (!it.hasNext())
                return currentNode;
        }

        while (it.hasNext()) {
            String currentName = it.next();
            HierarchicalInstance nextNode = null;
            for (HierarchicalInstance child : currentNode.getChildren()) {
                if (currentName.equals(child.getInstance().getOldName())) {
                    nextNode = child;
                    break;
                }
            }
            if (nextNode == null)
                return null;
            currentNode = nextNode;
        }

        return currentNode;
    }

    // javadoc inherited from interface
    public String getHierarchicalInstanceName(HierarchicalInstance node) {
        LinkedList<String> instanceNames = new LinkedList<String>();
        while (node != null) {
            HierarchicalInstance nextHigherNode = node.getParent();
            if (!(!_includeRootName && nextHigherNode == null))
                instanceNames.add(node.getInstance().getOldName());
            node = nextHigherNode;
        }

        StringBuilder sb = new StringBuilder();
        while (!instanceNames.isEmpty()) {
            sb.append(instanceNames.removeLast());
            if (!instanceNames.isEmpty())
                sb.append(_separator);
        }

        return sb.toString();
    }

    // javadoc inherited from interface
    public HierarchicalNet getHierarchicalNet(HierarchicalInstance head, String name) {
        HierarchicalInstance parentNode = null;
        String netName = null;
        if (name.indexOf(_separator) < 0 && !_includeRootName) {
            // this net is in the top instance
            parentNode = head;
            netName = name;
        } else {
            int splitPoint = name.lastIndexOf(_separator);
            String instanceName = name.substring(0, splitPoint);
            netName = name.substring(splitPoint + 1, name.length());
            parentNode = getHierarchicalInstance(head, instanceName);
        }
        if (parentNode == null)
            return null;
        HierarchicalNet matchingNet = null;
        for (HierarchicalNet hierarchicalNet : parentNode.getHierarchicalNets()) {
            if (netName.equals(hierarchicalNet.getOriginalNet().getOldName())) {
                matchingNet = hierarchicalNet;
                break;
            }
        }
        return matchingNet;
    }

    // javadoc inherited from interface
    public String getHierarchicalNetName(HierarchicalNet hierarchicalNet) {
        StringBuilder sb = new StringBuilder();
        sb.append(getHierarchicalInstanceName(hierarchicalNet.getParent()));
        if (sb.length() != 0)
            sb.append(_separator);
        sb.append(hierarchicalNet.getOriginalNet().getOldName());
        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /**
     * A boolean indicating whether the top level instance name should be
     * included in complete hierarchical names
     */
    private boolean _includeRootName;

    /**
     * A string to be used as a hierarchical separator (i.e. "/")
     */
    private String _separator;
}
