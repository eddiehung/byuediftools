/*
 * Resolves String name conflicts.
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

/////////////////////////////////////////////////////////////////////////
////NameSpaceResolver
/**
 * Resolves String name conflicts. This class will contain methods that all
 * interfaces need to implement. There are four classes that need to be wrapped
 * in a name space; these classes are: EdifLibrary, EdifCell, EdifNet, and
 * EdifCellInstance.
 * <p>
 * Memory Consumption Tests on Name Space (in bytes): Note: The first # is
 * memory usage at program termination, GC is memory that was garbage collected,
 * and the total memory consumption is memory consumption at termination plus
 * the memory that was garbage collected.
 * <ul>
 * <li>signalgen.edf (90 nets)
 * <li>winograd16_fft.edf (7642 nets)
 * </ul>
 * <p>
 * Time for HashMap on EdifNets fpga_a.edf: 113 s <br>
 * Time for HashSet on EdifNets fpga_a.edf: 156 s <br>
 * Time for ArrayList on fpga_a.edf: 150 s <br>
 * Time for HashMap on EdifNets and EdifCellInstances on fpga_a.edf: 35 s!
 * 
 * @version $Id:NameSpaceResolver.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public interface NameSpaceResolver {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Returns true if name does not clash with name space. Currently converts
     * everything to upper case.
     * 
     * @param newName Name to check if it already exists in the name space.
     * @return True if the name clashes, false if it doesn't exist in the name
     * space.
     */
    public boolean nameClash(String newName);

    /**
     * If the name is valid, return name. If not, make it valid. It grabs a
     * random element from an array and keeps appending it from the array.
     * 
     * @param name Name to modify, and make unique for this name space.
     * @return The string unique to this name space.
     */
    public String returnUniqueName(String name);

    /**
     * Return a string representation of this object.
     * 
     * @return String representing this name space object.
     */
    public String toString();
}
