/*
 * Created on Jun 30, 2005
 * 
 */
/*
 * 
 * 
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
package edu.byu.ece.edif.arch.xilinx;

import edu.byu.ece.edif.core.EdifNet;

/**
 * @author wirthlin TODO To change the template for this generated type comment
 * go to Window - Preferences - Java - Code Style - Code Templates
 */
public class NetHierarchy {

    public NetHierarchy(InstanceHierarchy parent, EdifNet net) {
        _net = net;
        _instance = parent;
    }

    /**
     * This method will return all of the instanced nets in the design. The
     * method will return a Collection of List objects. Each member of the List
     * object is a NetHierarchy object (i.e. instanced Net) that are connected
     * within the hierarchy. The List is ordered by hierarchy (i.e. top-level
     * net is first followed by second level, etc.).
     * 
     * @param instanceHierarchy
     * @return
     */
    /*
     * public static Collection createNetHierarchy(List instanceHierarchy) {
     * List c = new ArrayList(); // key: instanceHierarchy, value: List of nets
     * Map instanceNetMap = new LinkedHashMap(instanceHierarchy.size());
     * 
     * for (Iterator i = instanceHierarchy.iterator(); i.hasNext(); ) {
     * InstanceHierarchy ih = (InstanceHierarchy) i.next(); EdifCell cell =
     * ih.getInstanceCellType();
     *  // Don't process leaf cells if (cell.isLeafCell()) continue;
     * 
     * ArrayList instanceNets = new ArrayList(cell.getNetList().size());
     * instanceNetMap.put(ih,instanceNets);
     * 
     * for (Iterator j = cell.getNetList().iterator();j.hasNext();) { EdifNet
     * net = (EdifNet) j.next(); NetHierarchy nh = new NetHierarchy(ih,net);
     * instanceNets.add(nh);
     * 
     * if (net.hasTopLevelPortConnection() && !ih.isTop()) { // Connects to a
     * top-level port & not TOP cell
     *  // find the
     *  } else { // Does not connect to a top-level port or TOP cell
     *  // Create a new List of nets List connectedNets = new ArrayList();
     * connectedNets.add(nh); }
     *  } } return c; } <<<<<<< NetHierarchy.java
     */

    public EdifNet getEdifNet() {
        return _net;
    }

    public InstanceHierarchy getInstanceHierarchy() {
        return _instance;
    }

    public String getFullNetName(boolean includeTop) {
        StringBuffer sb = new StringBuffer();
        InstanceHierarchy hier = this.getInstanceHierarchy();
        String slash = "";
        if (hier != null) {
            sb.append(hier.getInstanceName(includeTop));
            slash = (getInstanceHierarchy().isTop() && !includeTop) ? "" : "/";
        }
        sb.append(slash + this.getEdifNet().getOldName());

        return sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(getFullNetName(true));

        return sb.toString();
    }

    protected EdifNet _net;

    /**
     * Collection of NetHierachy objects that connect to this net at the higher
     * level.
     */
    //protected NetHierarchy[] _parentNets;
    protected InstanceHierarchy _instance;

}
