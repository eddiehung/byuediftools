/*
 * Defines an interface for different naming policies for multi-bit bus names.
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

/**
 * Defines an interface for different naming policies for multi-bit bus names.
 * These names are used for ports as ports are the only object that support
 * multiple bits. This interface defines a set of methods required for
 * implementing a "policy" for naming buses.
 * 
 * @author Michael J Wirthlin
 * @since Created on May 13, 2005
 */
public interface EdifBusNamingPolicy {

    /**
     * Indicates whether the given String is a valid Bus name using the
     * implemented bus naming policy.
     */
    public boolean isValidBusName(String name);

    /**
     * Returns the integer value of the "left" bus limit within the bus string
     * name (i.e. will return a "4" for the following naming example "(4:8)").
     * Will return a non-negative value if the string is valid. Otherwise, it
     * returns -1.
     */
    public int getLeftBusLimit(String name);

    /**
     * Returns the integer value of the "right" bus limit within the bus string
     * name (i.e. will return a "8" for the following naming example "(4:8)").
     * Will return a non-negative value if the string is valid. Otherwise, it
     * returns -1.
     */
    public int getRightBusLimit(String name);

    /**
     * Determine the bit ordering of the given EDIF bus name. Little endian
     * implies that the "little" end of the bus (i.e. bit 0) comes first. Big
     * endian implies that the "big" end of the bus comes first (i.e. bit 31).
     * LittleEndian:(31:0) BigEndian: (0:31)
     */
    public boolean isLittleEndian(String name);

    /**
     * Return the portion of the bus string that specifies the range of the bus.
     * This String does not include the basename.
     */
    public String getBusRangeSpecifier(String name);

    /**
     * Return the basename of the bus name without all of the bus range
     * information. This method will should return a null if the name does not
     * match the bus naming policy.
     */
    public String getBusBaseName(String name);

    /**
     * Get the regular expression string used to parse bus names for this policy
     */
    public String getRegexString();
}
