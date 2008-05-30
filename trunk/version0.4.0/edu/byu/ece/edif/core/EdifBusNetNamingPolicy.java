/*
 * Interface for different naming policies for individual nets within a bus.
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
 * Defines an interface for different naming policies for individual nets within
 * a bus. Since EDIF does not have a standard for naming individual wires of a
 * bus, different tools implement such naming in an ad-hoc way. This interface
 * defines a set of methods required for implementing a "policy" for naming
 * individual nets that are part of a bus. Any class that implements this
 * interface will be able to identify nets that are part of a bus and identify
 * their bit position within the bus.
 * 
 * @author Michael J Wirthlin
 * @since Created on May 13, 2005
 */
public interface EdifBusNetNamingPolicy {

    /**
     * @return true if the given name is valid under the given naming policy.
     * This method should only return true if the naming policy can return both
     * the basename and bit position of the net.
     */
    public boolean isValidBusNetName(String name);

    /**
     * Given a bit number, create a bit suffix that matches the naming policy
     * 
     * @param bitPos bit position
     */
    public String generateBitSuffix(int bitNum);

    /**
     * Returns the bit position of the Net within the bus. Will return a
     * non-negative value if the string is valid. Otherwise, it returns -1.
     */
    public int getBusPosition(String name);

    /**
     * Return the basename of the single bit net without the bit number. This
     * method will should return a null if the name does not match the bus
     * naming policy.
     */
    public String getBusBaseName(String name);

    /**
     * Get the regular expression string used to parse bus net names for this
     * policy
     */
    public String getRegexString();
}
