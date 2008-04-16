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
package edu.byu.ece.edif.tools.sterilize.halflatch;

import java.util.Map;

import edu.byu.ece.edif.core.EdifCellInstance;

public abstract class AbstractProblemPrimitiveMap implements ProblemPrimitiveMap {

    public String getPrimitiveReplacementType(EdifCellInstance primitiveECI) {
        String primitiveType = primitiveECI.getType().toUpperCase();
        String primitiveReplacementType = null;
        primitiveReplacementType = (String) _safePrimitiveMap.get(primitiveType);
        return primitiveReplacementType;
    }

    public String[] getPrimitiveReplacementFloatingPorts(EdifCellInstance primitiveECI) {
        String primitiveType = primitiveECI.getType().toUpperCase();
        String[] primitiveReplacementFloatingPorts = null;
        primitiveReplacementFloatingPorts = (String[]) _floatingPortMap.get(primitiveType);
        return primitiveReplacementFloatingPorts;
    }

    public int getPrimitiveReplacementFloatingPortDefaultValue(EdifCellInstance primitiveECI, String floatingPort) {
        if (primitiveECI == null || floatingPort == null)
            return -1;
        int primitiveReplacementFloatingPortDefaultValue = -1;
        String primitiveType = primitiveECI.getType().toUpperCase();
        String[] primitiveReplacementFloatingPorts = getPrimitiveReplacementFloatingPorts(primitiveECI);
        int[] primitiveReplacementFloatingPortsDefaultValues = (int[]) _floatingPortDefaultValueMap.get(primitiveType);
        if (primitiveReplacementFloatingPortsDefaultValues != null) {
            for (int i = 0; i < primitiveReplacementFloatingPorts.length; i++) {
                if (primitiveReplacementFloatingPorts[i].compareToIgnoreCase(floatingPort) == 0) {
                    primitiveReplacementFloatingPortDefaultValue = primitiveReplacementFloatingPortsDefaultValues[i];
                }
            }
        }
        return primitiveReplacementFloatingPortDefaultValue;
    }

    // Map 1 maps primitive names to the corresponding 'safe primitive' name
    // Map 2 maps primitive names to the will-be floating port names once it is replaced by its corresponding 'safe primitive'
    // Map 3 maps primitive names to the will-be floating port default values (once it is replaced by its corresponding 'safe primitive')
    protected Map<String, String[]> _floatingPortMap;

    protected Map<String, int[]> _floatingPortDefaultValueMap;

    protected Map<String, String> _safePrimitiveMap;

}