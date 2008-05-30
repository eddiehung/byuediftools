/*
 * Contains a collection of static utility methods.
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
package edu.byu.ece.edif.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/////////////////////////////////////////////////////////////////////////
////EdifUtils
/**
 * Contains a collection of static utility methods. These methods will likely be
 * moved to other classes and packages as their usefulness is better understood.
 * 
 * @version $Id:EdifUtils.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class EdifUtils {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Count the number of EdifPortRefs in the cell. This method operates
     * recursively and will call all hierarchical cells found within the
     * top-level cell and add the count to the top-level.
     * 
     * @param cell
     * @param primsOnly If true, count only port refs that are connected to
     * leaf-cells. If false, count all port refs.
     * @return The number of recursive nets in the cell
     */
    public static int countPortRefs(EdifCell cell, boolean primsOnly) {

        int primitivePortRefs = 0;

        for (Iterator netIterator = cell.getNetList().iterator(); netIterator.hasNext();) {
            EdifNet net = (EdifNet) netIterator.next();

            for (EdifPortRef portRef : net.getConnectedPortRefs()) {
                EdifCellInstance eci = portRef.getCellInstance();
                if (eci != null) {
                    if (primsOnly) {
                        if (eci.getCellType().isLeafCell())
                            primitivePortRefs++;
                    } else {
                        primitivePortRefs++;
                    }
                }
            }
        }
        for (Iterator i = cell.cellInstanceIterator(); i.hasNext();) {
            EdifCellInstance origCellInst = (EdifCellInstance) i.next();
            EdifCell origSubCellType = origCellInst.getCellType();
            if (!origSubCellType.isLeafCell())
                primitivePortRefs += countPortRefs(origSubCellType, primsOnly);
        }
        return primitivePortRefs;
    }

    /**
     * Count the number of nets in the EdifCell. This method operates
     * recursively and will call all hierarchical cells found within the
     * top-level cell and add the count to the top-level.
     * 
     * @param cell
     * @return The number of recursive nets in the cell
     */
    public static int countRecursiveNets(EdifCell cell) {
        int nets = cell.getNetList().size();

        for (Iterator i = cell.cellInstanceIterator(); i.hasNext();) {
            EdifCellInstance origCellInst = (EdifCellInstance) i.next();
            EdifCell origSubCellType = origCellInst.getCellType();
            if (!origSubCellType.isLeafCell())
                nets += countRecursiveNets(origSubCellType);
        }
        return nets;

    }

    /**
     * Count the number of leaf cells in the EdifCell. This method operates
     * recursively and will call all hierarchical cells found within the
     * top-level cell and add the count to the top-level.
     * 
     * @param cell
     * @return The number of recursive leaf-cells
     */
    public static int countRecursivePrimitives(EdifCell cell) {
        int prims = 0;
        for (Iterator i = cell.cellInstanceIterator(); i.hasNext();) {
            EdifCellInstance origCellInst = (EdifCellInstance) i.next();
            EdifCell origSubCellType = origCellInst.getCellType();
            if (!origSubCellType.isLeafCell())
                prims += countRecursivePrimitives(origSubCellType);
            else
                prims++;
        }
        return prims;
    }

    /**
     * Return a Map of all unique instance types found within the given EdifCell
     * object. The key of this Map is an EdifCell representing a "type" of an
     * instance within the given top-level cell. The value of this Map is a
     * Collection of EdifCellInstance objects that instance this type in the
     * cell.
     * 
     * @param cell The EdifCell to investigate
     * @param map A Map object to add the entries to. If this is null, a new Map
     * will be created. This parameter is used to allow recursive calling of
     * this method.
     */
    public static Map getUniqueInstanceTypes(EdifCell cell, Map map) {
        if (map == null)
            map = new LinkedHashMap();
        for (Iterator i = cell.cellInstanceIterator(); i.hasNext();) {
            EdifCellInstance eci = (EdifCellInstance) i.next();
            EdifCell type = eci.getCellType();
            if (type.isLeafCell()) {
                Collection c = (Collection) map.get(type);
                if (c == null) {
                    c = new ArrayList();
                    map.put(type, c);
                }
                c.add(eci);
                map.put(type, c);
            } else {
                getUniqueInstanceTypes(type, map);
            }
        }
        return map;
    }

    //     public static HashMap primitiveSummary(EdifCell cell, HashMap map) {
    //
    //        for (Iterator i = cell.cellInstanceIterator(); i.hasNext();) {
    //            EdifCellInstance origCellInst = (EdifCellInstance) i.next();
    //            EdifCell origSubCellType = origCellInst.getCellType();
    //            if (!origSubCellType.isLeafCell())
    //                primitiveSummary(origSubCellType, map);
    //            else {
    //                List items = (List) map.get(origSubCellType);
    //                if (items == null) {
    //                    ArrayList al = new ArrayList();
    //                    map.put(origSubCellType, al);
    //                }
    //                items.add(origCellInst);
    //            }
    //        }
    //        return map;
    //
    //    }

    //    public static int countInstanceProperties(EdifCell cell) {
    //        int properties = 0;
    //
    //        for (Iterator i = cell.cellInstanceIterator(); i.hasNext();) {
    //            EdifCellInstance origCellInst = (EdifCellInstance) i.next();
    //            EdifCell origSubCellType = origCellInst.getCellType();
    //            PropertyList props = origCellInst.getPropertyList();
    //            if (props != null && props.size() > 0)
    //                properties++;
    //            if (!origSubCellType.isLeafCell())
    //                properties += countInstanceProperties(origSubCellType);
    //        }
    //        return properties;
    //
    //    }

    //    public static int countNetProperties(EdifCell cell) {
    //        int properties = 0;
    //
    //        for (Iterator netIterator = cell.getNetList().iterator(); netIterator.hasNext();) {
    //            EdifNet net = (EdifNet) netIterator.next();
    //            PropertyList props = net.getPropertyList();
    //            if (props != null && props.size() > 0)
    //                properties++;
    //        }
    //
    //        for (Iterator i = cell.cellInstanceIterator(); i.hasNext();) {
    //            EdifCellInstance origCellInst = (EdifCellInstance) i.next();
    //            EdifCell origSubCellType = origCellInst.getCellType();
    //            if (!origSubCellType.isLeafCell())
    //                properties += countNetProperties(origSubCellType);
    //        }
    //        return properties;
    //
    //    }

}
