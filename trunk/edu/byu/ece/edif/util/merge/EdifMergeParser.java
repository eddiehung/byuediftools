/*
 * Provides EDIF parsing and the ability to "merge" multiple EDIF files.
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
package edu.byu.ece.edif.util.merge;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.byu.ece.edif.arch.Primitives;
import edu.byu.ece.edif.arch.xilinx.XilinxGenLib;
import edu.byu.ece.edif.core.BasicEdifBusNamingPolicy;
import edu.byu.ece.edif.core.BasicEdifBusNetNamingPolicy;
import edu.byu.ece.edif.core.EdifBusNamingPolicy;
import edu.byu.ece.edif.core.EdifBusNetNamingPolicy;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifCellInterface;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifPrintWriter;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.util.parse.EdifParser;
import edu.byu.ece.edif.util.parse.ParseException;

/**
 * Provides EDIF parsing capability as well as the ability to "merge" EDIF
 * definitions from more than one EDIF file. This class contains a number of
 * static methods that are used together to provide the merging capability.
 * <p>
 * TODO: Describe the control flow for "matching" (i.e. the order the methods
 * are called).
 * <p>
 * TODO: This class is probably mis-named. It is not a parser but a set of
 * classes that are used to match. A better name might be EdifCellMatching.
 * 
 * @see edu.byu.ece.edif.util.parse.EdifParser
 * @author Mike Wirthlin
 * @version $Id$
 */
public class EdifMergeParser {

    /**
     * Recursively copy an EdifCell from one EdifLibraryManager to another.
     * During the copy, cells (instanced by the cell to be copied) may be added
     * to other libraries within the EdifLibraryManager. This method is not
     * placed in EdifCell because of its need to manage libraries. This method
     * is probably best placed in EdifLibrary or EdifLibraryManager.
     * 
     * @param cellToCopy the EdifCell object to copy
     * @param targetLib the EdifLibrary to copy the cell into. If no EdifLibrary
     * is specified (null), the EdifCell will be placed in an EdifLibrary with
     * the same name (or a new unique name if there is a clash) as it's old
     * EdifLibrary
     * @param elm the EdifLibraryManager to copy the cell into
     * @return a reference to the new copy of the EdifCell object
     */
    public static EdifCell copyCellDeep(EdifCell cellToCopy, EdifLibrary targetLib, EdifLibraryManager elm,
            EdifMergingPolicy mergingPolicy) {

        if (targetLib != null && !elm.containsLibrary(targetLib))
            throw new EdifRuntimeException("Bad library");

        if (targetLib == null) // determine where to put the cell
            targetLib = mergingPolicy.findLibraryForCell(cellToCopy, elm);

        // check to see if the target library already contains a cell with the
        // same name and interface. if so, return it.
        if (targetLib.containsCellByName(cellToCopy.getName())) {
            EdifCell possibleMatch = targetLib.getCell(cellToCopy.getName());
            if (possibleMatch.equalsInterface(cellToCopy))
                return possibleMatch;
        }

        // Create new empty cell with the same name as the original cell
        // and add it to the target library. 
        EdifCell newCell = null;
        try {
            newCell = new EdifCell(targetLib, cellToCopy.getName());
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }

        // Copy primitive flag
        if (cellToCopy.isPrimitive())
            newCell.setPrimitive();

        // copy properties
        newCell.copyProperties(cellToCopy);

        // copy cell instances
        Map<EdifPort, EdifPort> oldToNewPorts = new LinkedHashMap<EdifPort, EdifPort>();
        Map<EdifCellInstance, EdifCellInstance> oldToNewInstances = new LinkedHashMap<EdifCellInstance, EdifCellInstance>();
        Map<EdifCell, EdifCell> oldToNewCells = new LinkedHashMap<EdifCell, EdifCell>();

        if (cellToCopy.getInstancedCellTypes() != null) // there are subcells to
            // copy
            for (Iterator<EdifCell> instanced = cellToCopy.getInstancedCellTypes().iterator(); instanced.hasNext();) {
                EdifCell cell = instanced.next();

                if (oldToNewCells.containsKey(cell))
                    continue;

                // copy the cell over if it is not already here
                EdifCell newCellRef = copyCellDeep(cell, null, elm, mergingPolicy);
                oldToNewCells.put(cell, newCellRef);
            }

        for (Iterator<EdifCellInstance> instanceIterator = cellToCopy.cellInstanceIterator(); instanceIterator
                .hasNext();) {
            EdifCellInstance oldEci = instanceIterator.next();
            EdifCell newCellRef = oldToNewCells.get(oldEci.getCellType());

            for (EdifPort oldPort : oldEci.getCellType().getPortList()) {
                EdifPort newPort = newCellRef.getPort(oldPort.getName());
                if (!oldToNewPorts.containsKey(oldPort)) {
                    oldToNewPorts.put(oldPort, newPort);
                }
            }

            EdifCellInstance newInstance = new EdifCellInstance(oldEci.getEdifNameable(), newCell, newCellRef);

            // copy instance properties
            newInstance.copyProperties(oldEci);
            /*
             * if (oldEci.getPropertyList() != null) { for (Iterator it =
             * oldEci.getPropertyList().values().iterator(); it .hasNext();) {
             * Property p = (Property) it.next();
             * newInstance.addProperty((Property) p.clone()); } }
             */
            oldToNewInstances.put(oldEci, newInstance);
            try {
                newCell.addSubCell(newInstance);
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }
        }

        // copy cell interface
        for (EdifPort oldPort : cellToCopy.getPortList()) {
            EdifPort newPort = null;
            try {
                newPort = newCell.addPort(oldPort.getEdifNameable(), oldPort.getWidth(), oldPort.getDirection());
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }
            oldToNewPorts.put(oldPort, newPort);
            // copy port properties
            newPort.copyProperties(oldPort);
            /*
             * if (oldPort.getPropertyList() != null) { for (Iterator it =
             * oldPort.getPropertyList().values() .iterator(); it.hasNext();) {
             * Property p = (Property) it.next(); newPort.addProperty((Property)
             * p.clone()); } }
             */
        }

        // copy nets
        for (Iterator<EdifNet> netIterator = cellToCopy.netListIterator(); netIterator.hasNext();) {
            EdifNet oldNet = netIterator.next();
            EdifNet newNet = new EdifNet(oldNet.getEdifNameable(), newCell);
            // iterate portRefs
            for (Iterator<EdifPortRef> portRefIterator = oldNet.getPortRefIterator(); portRefIterator.hasNext();) {
                EdifPortRef oldRef = portRefIterator.next();
                EdifSingleBitPort oldSbp = oldRef.getSingleBitPort();
                EdifSingleBitPort newSbp = oldToNewPorts.get(oldSbp.getParent()).getSingleBitPort(oldSbp.bitPosition());

                EdifCellInstance newEci = null;
                if (oldRef.getCellInstance() != null)
                    newEci = oldToNewInstances.get(oldRef.getCellInstance());
                EdifPortRef newEpr = new EdifPortRef(newNet, newSbp, newEci);
                newNet.addPortConnection(newEpr);
            }

            // copy net properties
            newNet.copyProperties(oldNet);
            /*
             * if (oldNet.getPropertyList() != null) { for (Iterator it =
             * oldNet.getPropertyList().values().iterator(); it .hasNext();) {
             * Property p = (Property) it.next(); newNet.addProperty((Property)
             * p.clone()); } }
             */
            try {
                newCell.addNet(newNet);
            } catch (EdifNameConflictException e) {
                e.toRuntime();
            }
        }
        return newCell;
    }

    /**
     * Returns a Map between matching ports in the two different EdifCell
     * objects. In order for these ports to match, it is first checked if they
     * return true on a call to the method EdifPort.equals(EdifPort). This is a
     * strict match between the two ports. If false is returned, there may still
     * be a match if the names of the two different EdifPorts differ slightly.
     * For example, a port named "port0" will match a port named "port_0_". The
     * key of the Map is a Port from c1 and the value is a Port from c2.
     */
    public static Map<EdifPort, EdifPort> getOneToOnePortMatches(EdifCell c1, EdifCell c2) {
        Map<EdifPort, EdifPort> portMap = new LinkedHashMap<EdifPort, EdifPort>(c1.getPortList().size());

        for (Iterator<EdifPort> i = c1.getPortList().iterator(); i.hasNext();) {

            EdifPort port1 = i.next();

            boolean loop = true;
            for (Iterator<EdifPort> j = c2.getPortList().iterator(); j.hasNext() && loop;) {
                EdifPort port2 = j.next();
                if (port1.equals(port2)) {
                    portMap.put(port1, port2);
                    loop = false;
                }
            }
            // KWL 01/19/06 This Block of code added to match some ports
            // that were not matching because the port names did not
            // match exactly
            if (loop) // So far no match. Now check for matches with expanded
            // name matching. eg. port0 matches port_0_
            {
                for (Iterator<EdifPort> j = c2.getPortList().iterator(); j.hasNext() && loop;) {

                    EdifPort port2 = j.next();

                    if (port1.getWidth() == port2.getWidth() && // Widths and
                            // directions
                            // MUST be the
                            // same in a
                            // 1to1 match
                            port1.getDirection() == port2.getDirection()) {
                        String name1 = "", name2 = "", p1name = port1.getName(), p2name = port2.getName();
                        int length1 = p1name.length(), length2 = p2name.length();
                        for (int c = 0; c < length1; c++) {
                            if (p1name.charAt(c) != '_') // ignore '_'s in
                                // the name
                                name1 += p1name.charAt(c);
                        }
                        for (int c = 0; c < length2; c++) {
                            if (p2name.charAt(c) != '_') // ignore '_'s in
                                // the name
                                name2 += p2name.charAt(c);
                        }
                        if (name1.toLowerCase().equals(name2.toLowerCase())) {
                            portMap.put(port1, port2);
                            loop = false;
                        }
                    }
                }
            }
            // End KWL

            // I added the following to match rd_count(0) with rd_count_0_
            // (a single bit port array that was getting matched in
            // one to many)
            if (loop) {
                // still no match - now check for matches using
                // EdifBusNetNamingPolicy
                for (Iterator<EdifPort> j = c2.getPortList().iterator(); j.hasNext() && loop;) {
                    EdifPort port2 = j.next();
                    if (port1.getWidth() == port2.getWidth() && port1.getDirection() == port2.getDirection()) {
                        EdifBusNetNamingPolicy policy1 = BasicEdifBusNetNamingPolicy.EdifBusNetNamingPolicy(port1
                                .getOldName());
                        EdifBusNetNamingPolicy policy2 = BasicEdifBusNetNamingPolicy.EdifBusNetNamingPolicy(port2
                                .getOldName());

                        if (policy1 != null && policy2 != null) {
                            if (policy1.getBusBaseName(port1.getOldName()).toLowerCase().equals(
                                    policy2.getBusBaseName(port2.getOldName()).toLowerCase())
                                    && policy1.getBusPosition(port1.getOldName()) == policy2.getBusPosition(port2
                                            .getOldName())) {
                                portMap.put(port1, port2);
                                loop = false;
                            }
                        }
                    }
                }
            }
        }
        return portMap;
    }

    /**
     * This method will compare the cell interfaces of c1 and c2 to see if there
     * are any multi-bit ports in C1 that match a group of single-bit ports in
     * C2.
     * 
     * @return A map whose key is an EdifPort in c1 and whose value is a
     * Collection of EdifPort objects in c2 that match.
     */
    public static Map<EdifPort, Collection<EdifPort>> getOneToManyPortMatches(EdifCell c1, EdifCell c2) {
        Map<EdifPort, Collection<EdifPort>> portMap = new LinkedHashMap<EdifPort, Collection<EdifPort>>(c1
                .getPortList().size());

        // Iterate over all ports in c1
        for (EdifPort port1 : c1.getPortList()) {
            Collection<EdifPort> matches = getMatchingBusPorts(c2.getInterface(), port1);
            if (matches != null && matches.size() > 1) // changed this to 1 to
                // avoid matching single
                // bit port arrays
                portMap.put(port1, matches);
        }
        return portMap;
    }

    /**
     * Returns an unordered Collection of EdifPorts in the cellInt interface
     * that match the multi-bit bus port "port". The goal is to try and find a
     * set of single-bit ports in this interface that can be combined to match a
     * reference multi-bit port. In order for a match to occur, there must be N
     * single-bit ports whose basenames match the multi-bit port. The multi-bit
     * port width must be N.
     * <p>
     * If no match can be found, a null will be returned.
     * 
     * @param port The multi-bit edif port used for the reference
     * @param policy The naming policy used to perform the match
     * @return A collection of EdifPort objects that when combined will match
     * the given EdifPort object. Returns a null if no match is found
     */
    public static Collection<EdifPort> getMatchingBusPorts(EdifCellInterface cellInt, EdifPort port) {

        // 1. Search for all single-bit ports whose basename
        // matches the name of the given EdifPort
        List<EdifPort> c = new ArrayList<EdifPort>(port.getWidth());
        for (Iterator<EdifPort> i = cellInt.getPortList().iterator(); i.hasNext();) {
            EdifPort p = i.next();
            if (p.getWidth() > 1)
                continue; // skip over multi-bit ports
            String basename = BasicEdifBusNetNamingPolicy.getBusBaseNameStatic(p.getName());
            if (basename.equals(p.getName()))
                basename = null;
            if (basename != null)
                basename = basename.toLowerCase();
            if (basename == null || !basename.equals(port.getName().toLowerCase()))
                continue;
            c.add(p);
        }

        // 2. See if the number of matches equals the size of the port
        if (c.size() == 0)
            return null;
        if (c.size() != port.getWidth())
            return null;
        return c;
    }

    /**
     * The key of the Map is a multi-bit EdifPort in "ref" and the value is a
     * Collection of single-bit EdifPort objects in "compare" that match the
     * "ref" multi-bit EdifPort. Note: This method is not currently used by any
     * other method in this class.
     */
    public static Map<EdifPort, Collection<EdifPort>> getMatchingMultiBitPorts(EdifCellInterface ref,
            EdifCellInterface compare) {
        Map<EdifPort, Collection<EdifPort>> m = new LinkedHashMap<EdifPort, Collection<EdifPort>>(ref.getPortList()
                .size());
        for (Iterator<EdifPort> i = ref.getPortList().iterator(); i.hasNext();) {
            EdifPort p = i.next();
            if (p.getWidth() <= 1)
                continue; // look for multi-bit ports
            Collection<EdifPort> mports = getMatchingBusPorts(compare, p);
            if (mports == null || mports.size() == 0)
                continue; // no matches
            m.put(p, mports);
        }
        if (m.size() == 0)
            return null;
        return m;
    }

    /**
     * Searches for an EdifCell object in a given library whose interface
     * matches the interface of the EdifCell parameter. In order for this method
     * to return a matching EdifCell object, the following conditions must hold:
     * <ol>
     * <li> The name of the EdifCell in the library must match the name of the
     * EdifCell parameter
     * <li> The port interface of the EdifCell must match the port interface of
     * the parameter. Note that two different methods will be used to check this
     * interface. First, {@link EdifCell#equalsInterface(EdifCell)} will be
     * called to perform an exact match on the ports. If this fails,
     * {@link #matchingCellInterfaceBusExpansion(EdifCell, EdifCell)} will be
     * called to see if the interfaces match when busses are expanded in either
     * of the cells.
     * </ol>
     * 
     * @param lib EdifLibrary in which to search for a match
     * @param cell EdifCell to match
     * @param open Whether to allow open pins on matching cell
     * @return The matching EdifCell object. This method will return a null if
     * no match is found.
     */
    public static EdifCell findMatchingEdifCellInterface(EdifLibrary lib, EdifCell cell, boolean open) {

        // See if a name matches by name
        EdifCell matchingCell = lib.getCell(cell.getName());
        if (matchingCell == null) {
            error_code = NO_MATCHING_INSTANCE_NAME;
            //name didn't match
            return null;
        }
        // See if cell interface matches
        if (cell.equalsInterface(matchingCell)) {
            error_code = MATCH_FOUND;
            return matchingCell;
        }
        // If the cell interface does not match, see if the
        // cell matches with "bus expansion"
        if (matchingCellInterfaceBusExpansion(cell, matchingCell, open)) {
            //this function sets different error codes, if it had to infer pins.
            //error_code=MATCH_FOUND;
            return matchingCell;
        }
        //name matched, no suitable interfaces found
        //error_code=NO_MATCHING_INTERFACE;
        return null;
    }

    /**
     * Searches the EdifLibraryManager parameter for an EdifCell that matches
     * the EdifCell parameter. This method calls
     * {@link #findMatchingEdifCellInterface(EdifLibrary, EdifCell, Boolean)} to
     * perform this search.
     * 
     * @param elm EdifLibraryManager to search.
     * @param cell EdifCell to match
     * @param open Whether to allow unmatched pins on matching cell
     * @return The matching EdifCell object. This method will return a null if
     * no match is found.
     */
    public static EdifCell findMatchingEdifCellInterface(EdifLibraryManager elm, EdifCell cell, Boolean open) {

        //EdifCell tmp=null;
        for (Iterator<EdifLibrary> i = elm.getLibraries().iterator(); i.hasNext();) {
            EdifLibrary lib = i.next();
            EdifCell c = findMatchingEdifCellInterface(lib, cell, open);
            if (c != null) {
                return c;
            }
        }
        return null;
    }

    /**
     * Searches for a matching EdifCell definition in an existing, unrelated
     * EdifEnvironment object. This method will call
     * {@link #findMatchingEdifCellInterface(EdifLibraryManager, EdifCell)} to
     * perform this search.
     * 
     * @param blackBox
     * @param env
     * @return The matching EdifCell object. This method will return a null if
     * no match is found.
     */
    public static EdifCell findMatchingCell(EdifCell blackBox, EdifEnvironment env) {
        EdifLibraryManager elm = env.getLibraryManager();
        return findMatchingEdifCellInterface(elm, blackBox, false);
    }

    /**
     * Searches for a matching EdifCell, but allows unconnected pins on lower
     * level definition, This method will call
     * {@link #findMatchingEdifCellInterface(EdifLibraryManager, EdifCell)} to
     * perform this search.
     * 
     * @param blackBox
     * @param env
     * @return The matching EdifCell object. This method will return a null if
     * no match is found.
     */
    public static EdifCell findLooseMatchingCell(EdifCell blackBox, EdifEnvironment env) {
        EdifLibraryManager elm = env.getLibraryManager();
        return findMatchingEdifCellInterface(elm, blackBox, true);
    }

    /**
     * Searches for a matching EdifCell definition in an external file. This
     * method will call {@link #findMatchingCell(EdifCell,EdifEnvironment)} to
     * perform this search.
     * 
     * @param blackBox EdifCell object that this method is trying to match
     * @param filename The filename to open and search for a matching EdifCell.
     * @return The EdifCell object found within this file that matches the
     * blackBox EdifCell parameter. This method returns a null if no match is
     * found.
     */
    public static EdifCell findMatchingCellInFile(EdifCell blackBox, String filename, PrintStream outstream) {
        // 1. See if file exists
        if (!new File(filename).exists()) {
            return null;
        }
        // 2. Try to create an EdifEnvironment from the file
        EdifEnvironment top = null;
        try {
            top = EdifParser.translate(filename);
        } catch (ParseException e) {
            outstream.println("Error when parsing file: " + filename);
            return null;
        } catch (FileNotFoundException e) {
            outstream.println("File " + filename + " not found.");
            return null;
        }
        return findMatchingCell(blackBox, top);
    }

    /**
     * Searches a given directory for an Edif file with a name that is the same
     * as the Cell name. Multiple extensions will be searched if necessary.
     */
    public static EdifCell findMatchingCellInDir(EdifCell blackBox, String dir, PrintStream outstream) {
        String filename = null;
        EdifCell ecell = null;
        // Create a filename to look for based on the directory name
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        for (int i = 0; i < EDIF_EXTENSIONS.length; i++) {
            filename = dir + blackBox.getName() + "." + EDIF_EXTENSIONS[i];
            ecell = findMatchingCellInFile(blackBox, filename, outstream);
            if (ecell != null) {
                return ecell;
            }
        }
        return null;
    }

    /**
     * This method will investigate all EdifCell objects found within each
     * library in the group and compare it with the passed in library (the
     * passed in library is not necessarily a member of the manager). This
     * method will return the library within the manager that contains the most
     * number of matches with the library that was passed in.
     * 
     * @return The matching library
     */
    public static EdifLibrary findMatchingLibrary(EdifLibrary inputLibrary, EdifLibraryManager elm) {

        int maxMatches = 0;
        EdifLibrary matchingLib = null;
        Collection<EdifLibrary> libraries = elm.getLibraries();
        if (inputLibrary != null) {
            for (Iterator<EdifLibrary> i = libraries.iterator(); i.hasNext();) {
                EdifLibrary lib = i.next();
                int localMatch = 0;
                for (Iterator<EdifCell> j = inputLibrary.getCells().iterator(); j.hasNext();) {
                    EdifCell c = j.next();

                    // NHR2 - MAR 2006
                    // - We should only count cells that are the same in name
                    // AS WELL as interface
                    if (lib.containsCellByName(c.getName())) {
                        // if (lib.containsClashingCell(c)) {
                        // LogFile.out().println("\tMATCH CELL: "+c);
                        localMatch++;
                    }
                }
                if (localMatch > maxMatches) {
                    matchingLib = lib;
                    maxMatches = localMatch;
                }
            }
        }

        return matchingLib;
    }

    /**
     * This method will replace the "blackBox" EdifCell object with the
     * "definition" EdifCell object. Port interfaces will be equalized if
     * necessary.
     * 
     * @param blackBox The "blackBox" EdifCell object that will be replaced.
     * This EdifCell should contain NO contents.
     * @param definition The EdifCell object that provides a "definition" for
     * the black Box EdifCell. This EdifCell MUST contain contents.
     */
    public static void replaceBlackBox(EdifCell blackBox, EdifCell definition, PrintStream outstream,
            EdifMergingPolicy mergingPolicy) {

        // Step 1. Delete the black box from the original library
        // We need to delete first because we are going to add
        // a EdifCell to the library with the same name and need
        // to avoid name clashes
        EdifLibrary blackBoxLibrary = blackBox.getLibrary();
        // There are still references to the black box in the library
        blackBoxLibrary.deleteCell(blackBox, true);

        // Step 2. Move the definition EdifCell to library containing the black
        // box
        EdifLibraryManager elm = blackBoxLibrary.getLibraryManager();
        EdifCell newDef = copyCellDeep(definition, blackBoxLibrary, elm, mergingPolicy);
        // Step 3. Determine if equalization is needed
        // boolean equalize = false;
        if (!blackBox.equalsInterface(newDef) && matchingCellInterfaceBusExpansion(blackBox, newDef, false)) {
            outstream.println("Equalization required for " + newDef.getName());
        }

        // Step 3. Update cell references to black box. Equalize each
        // reference if necessary

        Map<EdifPort, Collection<EdifPort>> definitionExpandedPorts = getOneToManyPortMatches(blackBox, newDef);
        Map<EdifPort, Collection<EdifPort>> blackBoxExpandedPorts = getOneToManyPortMatches(newDef, blackBox);
        Map<EdifPort, EdifPort> oneToOnePorts = getOneToOnePortMatches(blackBox, newDef);

        Collection<EdifCellInstance> blackInstances = elm.findCellInstancesOf(blackBox);
        for (Iterator<EdifCellInstance> i = blackInstances.iterator(); i.hasNext();) {
            EdifCellInstance eci = i.next();
            eci.modifyCellRef(newDef, false);
            modifyPortRefsEqualize(eci, definitionExpandedPorts, blackBoxExpandedPorts, oneToOnePorts, outstream);
        }
    }

    /**
     * This method will modify all of the port refs to the black box to properly
     * reflect the change in the interface of the definition.
     * 
     * @param eci EdifCellInstance
     * @param blackBox EdifCell to be modified
     * @param definition
     */
    public static void modifyPortRefsEqualize(EdifCellInstance eci,
            Map<EdifPort, Collection<EdifPort>> definitionExpandedPorts,
            Map<EdifPort, Collection<EdifPort>> blackBoxExpandedPorts, Map<EdifPort, EdifPort> oneToOnePorts,
            PrintStream outstream) {
        // outstream.println("Equalizing eci "+eci);

        // Step 1. Obtain all port refs to this instance
        Collection<EdifPortRef> instancePortRefs = eci.getAllEPRs();
        EdifBusNetNamingPolicy policy = new BasicEdifBusNetNamingPolicy();
        ;

        Map<EdifPortRef, EdifPortRef> oldToNewPortRefs = new LinkedHashMap<EdifPortRef, EdifPortRef>();

        Set<EdifNet> netsToModify = new LinkedHashSet<EdifNet>();

        // Step 3. Identify those ports in the definition that are
        // "expanded" (i.e. single-bit ports in the definition ->
        // multi-bit port in the black box).

        // Change each port reference to each expanded port
        for (Iterator<EdifPort> i = definitionExpandedPorts.keySet().iterator(); i.hasNext();) {
            EdifPort multiBitBlackBoxPort = i.next();
            Collection<EdifPort> singleBitDefinitionPorts = definitionExpandedPorts.get(multiBitBlackBoxPort);
            EdifBusNamingPolicy busPolicy = BasicEdifBusNamingPolicy.EdifBusNamingPolicy(multiBitBlackBoxPort
                    .getOldName());
            if (busPolicy == null)
                throw new EdifRuntimeException("unsupported bus name format: " + multiBitBlackBoxPort.getOldName());
            boolean littleEndian = busPolicy.isLittleEndian(multiBitBlackBoxPort.getOldName());
            int rightLimit = busPolicy.getRightBusLimit(multiBitBlackBoxPort.getOldName());
            int leftLimit = busPolicy.getLeftBusLimit(multiBitBlackBoxPort.getOldName());

            // Search port refs for those connected to port
            for (EdifPortRef epr : instancePortRefs) {
                if (epr.getPort() == multiBitBlackBoxPort) {
                    // 1. Find the corresonding single-bit port
                    int busMember = epr.getBusMember();

                    // KEY LINE HERE:
                    // Determine the "port number" to look for
                    int portNumber;
                    if (littleEndian)
                        portNumber = multiBitBlackBoxPort.getWidth() - 1 - busMember + rightLimit;
                    else
                        portNumber = busMember + leftLimit;

                    EdifNet net = epr.getNet();
                    boolean find = false;
                    for (Iterator<EdifPort> k = singleBitDefinitionPorts.iterator(); k.hasNext();) {
                        EdifPort singleBitPort = k.next();
                        //
                        policy = BasicEdifBusNetNamingPolicy.EdifBusNetNamingPolicy(singleBitPort.getName());
                        //
                        int singleBitPortNumber = policy.getBusPosition(singleBitPort.getName());
                        if (singleBitPortNumber == portNumber) {
                            if (DEBUG)
                                outstream.println("Replace port " + multiBitBlackBoxPort.getName() + " bit "
                                        + busMember + " with " + singleBitPort.getName() + " and rightLimit = "
                                        + rightLimit);

                            oldToNewPortRefs.put(epr, new EdifPortRef(net, singleBitPort.getSingleBitPort(0), eci));
                            netsToModify.add(net);
                            find = true;
                        }
                    }
                    if (find == false)
                        outstream.println("Can't find matching port for port " + epr);
                }
            }
        }

        // Step 3. Identify those ports in the definition that are
        // multi-bits and single-bit in the black box.
        for (Iterator<EdifPort> i = blackBoxExpandedPorts.keySet().iterator(); i.hasNext();) {
            EdifPort multiBitDefinitionPort = i.next();
            Collection<EdifPort> singleBitBlackBoxPorts = blackBoxExpandedPorts.get(multiBitDefinitionPort);
            EdifBusNamingPolicy busPolicy = BasicEdifBusNamingPolicy.EdifBusNamingPolicy(multiBitDefinitionPort
                    .getOldName());
            boolean littleEndian = true;
            littleEndian = busPolicy.isLittleEndian(multiBitDefinitionPort.getOldName());
            int rightLimit = busPolicy.getRightBusLimit(multiBitDefinitionPort.getOldName());
            int leftLimit = busPolicy.getLeftBusLimit(multiBitDefinitionPort.getOldName());

            // Search port refs for those connected to port
            for (EdifPortRef epr : instancePortRefs) {
                // Loop over the single bit ports and see if they match
                for (Iterator<EdifPort> k = singleBitBlackBoxPorts.iterator(); k.hasNext();) {
                    EdifPort singleBitBlackBoxPort = k.next();
                    if (epr.getPort() == singleBitBlackBoxPort) {
                        EdifNet net = epr.getNet();
                        int busMember;
                        if (littleEndian)
                            busMember = multiBitDefinitionPort.getWidth()
                                    - policy.getBusPosition(singleBitBlackBoxPort.getName()) - 1 + rightLimit;
                        else
                            busMember = policy.getBusPosition(singleBitBlackBoxPort.getName()) - leftLimit;
                        oldToNewPortRefs.put(epr, new EdifPortRef(net, multiBitDefinitionPort
                                .getSingleBitPort(busMember), eci));
                        netsToModify.add(net);
                    }
                }

            }
        }

        // Step 4. Identify those ports in the definition that are
        // the same as those in the black box
        for (Iterator<EdifPort> i = oneToOnePorts.keySet().iterator(); i.hasNext();) {
            EdifPort blackBoxPort = i.next();
            EdifPort definitionPort = oneToOnePorts.get(blackBoxPort);

            // Search port refs for those connected to port
            for (EdifPortRef epr : instancePortRefs) {
                // Loop over the single bit ports and see if they match
                if (epr.getPort() == blackBoxPort) {
                    EdifNet net = epr.getNet();

                    oldToNewPortRefs.put(epr, new EdifPortRef(net, definitionPort.getSingleBitPort(epr.getBusMember()),
                            eci));
                    netsToModify.add(net);
                }
            }
        }

        // Step 5. Now that the modification map has been made, make the changes
        // while preserving portRef order
        for (EdifNet net : netsToModify) {
            ArrayList<EdifPortRef> oldPortRefs = new ArrayList<EdifPortRef>(net.getConnectedPortRefs());
            for (EdifPortRef ref : oldPortRefs) {
                net.deletePortConnection(ref);
            }
            for (EdifPortRef ref : oldPortRefs) {
                if (oldToNewPortRefs.containsKey(ref))
                    net.addPortConnection(oldToNewPortRefs.get(ref));
                else
                    net.addPortConnection(ref);
            }
        }
    }

    /**
     * Performs a comparison between the port interfaces of two EdifCell
     * objects. Unlike the strict {@link EdifCell#equalsInterface(EdifCell)}
     * method, this method will perform a match while allowing busses in one
     * EdifCell interface to be expanded into individual bits and remain as a
     * complete bus in the original EdifCell.
     * <p>
     * Xilinx tools allow an upper level cell to match even though the upper
     * level cell may not reference all the ports in the lower level. It is
     * infered that Xilix then ties these ports "open"
     * 
     * @param cell1-upper level
     * @param cell2-lower level
     * @return true if the cell interfaces of the two cells match. False if
     * there is no match.
     */
    public static boolean matchingCellInterfaceBusExpansion(EdifCell cell1, EdifCell cell2) {
        return matchingCellInterfaceBusExpansion(cell1, cell2, false);
    }

    /**
     * Performs a comparison between the port interfaces of two EdifCell
     * objects. Unlike the strict {@link EdifCell#equalsInterface(EdifCell)}
     * method, this method will perform a match while allowing busses in one
     * EdifCell interface to be expanded into individual bits and remain as a
     * complete bus in the original EdifCell.
     * <p>
     * Xilinx tools allow an upper level cell to match even though the upper
     * level cell may not reference all the ports in the lower level. It is
     * infered that Xilix then ties these ports "open"
     * 
     * @param cell1-upper level- allowed to have less ports
     * @param cell2-lower level
     * @param open- whether to allow cell1 to have less ports than cell2
     * @return true if the cell interfaces of the two cells match. False if
     * there is no match.
     */
    public static boolean matchingCellInterfaceBusExpansion(EdifCell cell1, EdifCell cell2, boolean open) {

        // Find all cases when one port in cell1 matches one and only one
        // port in cell2
        // This is accomplished by iterating over all ports in cell1 and
        // cell2, and placing ports that return true when passed to the
        // EdifPort "equals" method in the Map oneToOneMatches.

        Map<EdifPort, EdifPort> oneToOneMatches = getOneToOnePortMatches(cell1, cell2);

        // Find all cases when one port in cell1 matches more than
        // one port in cell2
        // This is accomplished by iterating over all ports in cell1 and
        // comparing each port with cell2's interface. The method
        // getMatchingBusPorts() in this class accomplishes this task.

        Map<EdifPort, Collection<EdifPort>> c1TOc2Matches = getOneToManyPortMatches(cell1, cell2);

        // Find all cases when one port in cell2 matches more than
        // one port in cell1
        // This is accomplished by iterating over all ports in cell1 and
        // comparing each port with cell2's interface. The method
        // getMatchingBusPorts() in this class accomplishes this task.

        Map<EdifPort, Collection<EdifPort>> c2TOc1Matches = getOneToManyPortMatches(cell2, cell1);

        // See if all the ports in both cells are matched

        Collection<EdifPort> portList1 = cell1.getPortList();
        Collection<EdifPort> portList2 = cell2.getPortList();

        // Start checking off ports from list1 (check off by removing from
        // port list)

        portList1.removeAll(oneToOneMatches.keySet());
        portList1.removeAll(c1TOc2Matches.keySet());
        for (Iterator<Collection<EdifPort>> i = c2TOc1Matches.values().iterator(); i.hasNext();) {
            Collection<EdifPort> c = i.next();
            portList1.removeAll(c);
        }

        // If there are any ports remaining in this list, not all of the
        // ports in cell1 were found and the interfaces do not match.

        if (portList1.size() > 0) {
            LogFile.debug().println("\nportlist1 to big\n" + portList1 + "\n\n");
            error_code = NO_MATCHING_INTERFACE;
            return false;
        }

        // Start checking off ports from list2

        portList2.removeAll(oneToOneMatches.values());
        portList2.removeAll(c2TOc1Matches.keySet());
        for (Iterator<Collection<EdifPort>> i = c1TOc2Matches.values().iterator(); i.hasNext();) {
            Collection<EdifPort> c = i.next();
            portList2.removeAll(c);
        }
        if (portList2.size() > 0) {
            if (open) {
                //check if unmatched ports are inputs or outputs.
                EdifPort tmp = null;
                for (Iterator<EdifPort> i = portList2.iterator(); i.hasNext();) {
                    tmp = i.next();
                    if (tmp.isInput()) {
                        error_code = INFER_OPEN_INPUT_PINS;
                        LogFile.debug().println("\nWarning. pin " + tmp + " is undriven \n");
                        return false;
                    }
                }
                error_code = INFER_OPEN_OUTPUT_PINS;
                LogFile.debug().println("\nportlist2 too big, okay..\n" + portList2 + "\n\n");
                return true;
            } else {
                error_code = NO_MATCHING_INTERFACE;
                LogFile.debug().println("\nportlist2 too big, not okay..\n" + portList2 + "\n\n");
                return false;
            }

        }
        error_code = MATCH_FOUND;
        return true;
    }

    /**
     * This method will parse a given Edif filename and merge any appropriate
     * EDIF files as specified by arguments in the args parameter. This method
     * can be used by main methods to parse arguments and provide an
     * EdifEnvironment in return.
     * <p>
     * The following invocation of this method demonstrates the use of
     * appropriate arguments:
     * 
     * <pre>
     *        java &lt;main class&gt; &lt;EDIF filename&gt; -L &lt;search dir #1&gt; -L &lt;search dir #2&gt; \
     *                      -f &lt;external EDIF filename&gt;
     * </pre>
     * 
     * The arguments are interpreted as follows:
     * <ul>
     * <li> args[0] - original EDIF filename.
     * <li> Search directories - this method needs to search multiple
     * directories in an attempt to find matching edif files. To add a search
     * directories, use the "-L" flag (one external directory for each
     * invocation of "-L").
     * <li> A specific edif filename can be specified for merging using the "-f"
     * flag. Only one external filename should be specified for each "-f" flag.
     * </ul>
     * Once the arguments have been parsed, this method will call
     * {@link EdifMergeParser#parseAndMergeEdif(String, Collection, Collection, EdifLibrary, PrintStream)}.
     * If there are any errors, this method will exit rather than throwing an
     * exception.
     * 
     * @param args String arguments that contain the filename, libraries, and
     * external files needed for parsing.
     * @param primLib Primitive library to use for primitive tagging. If the
     * library is null, do not tag library elements for primitives
     * @return The merged EdifEnvironment
     */
    public static EdifEnvironment parseAndMerge(String args[], EdifLibrary primLib) {

        if (args.length <= 0) {
            throw new IllegalArgumentException("No filename specified");
        }

        // Filename
        String topFilename = args[0];

        // External directories
        Set<String> newDirs = EdifMergeParser.parseArguments(args, "-L");
        Set<String> defaultDirs = EdifMergeParser.createDefaultDirs();
        if (newDirs != null && newDirs.size() > 0)
            defaultDirs.addAll(newDirs);

        // External files
        Set<String> externalFiles = EdifMergeParser.parseArguments(args, "-f");

        try {
            return EdifMergeParser.parseAndMergeEdif(topFilename, defaultDirs, externalFiles, primLib);
        } catch (FileNotFoundException e) {
            System.err.println(e);
            System.exit(1);
        } catch (ParseException e) {
            System.err.println(e);
            System.exit(1);
        }
        return null;
    }

    /**
     * This method will parse a given EDIF filename and merge any appropriate
     * EDIF files if necessary. If successful, the result is returned as an
     * EdifEnvironment object. The files to be merged are found in the
     * directories contained by the dirs parameter, as well as the files
     * contained in the files parameter. This task is accomplished by:
     * <ul>
     * <li>Parse the top level EDIF design</li>
     * <li>Parse all EDIF files in the files collection. This generates an
     * EdifEnvironment for each edif file.</li>
     * <li>Blackboxes are identified by a call to
     * {@link EdifLibraryManager#findBlackBoxes()}</li>
     * <li>The blackBoxes are iterated over, and each is compared to the
     * EdifEnvironments generated from parsing the files on the command line.
     * Matches are identified by a call to
     * {@link #findMatchingCell(EdifCell, EdifEnvironment)}.</li>
     * <li>If a match is not found, a call to
     * {@link #findMatchingCellInDir(EdifCell, String, PrintStream)} will search
     * the directory specified by the string parameter for an edif file that
     * matches the blackbox.</li>
     * <li>After searching for blackbox matches, all successful matches are
     * merged by a call to
     * {@link #replaceBlackBox(EdifCell, EdifCell, PrintStream, EdifMergingPolicy)}.</li>
     * </ul>
     * This method can be used by main methods to parse arguments and provide an
     * EdifEnvironment in return. TODO: - Use Javadoc "link" and "see" tags to
     * highlight the key methods called from this method.
     * 
     * @param topFilename Filename of "top" edif environment
     * @param dirs A Collection of String directory names used to search for
     * black box definitions. If this is null, no directories will be searched.
     * @param files A Collection of String filenames that contain EdifCell
     * definitions. These files will be parsed and searched when black box
     * definitions are found.
     * @param primLib Primitive library to use for primitive tagging. If the
     * library is null, do not tag library elements for primitives
     * @param outstream PrintStream to which output during the merge process
     * should be directed.
     * @return The merged EdifEnvironment
     * @throws ParseException
     * @throws FileNotFoundException
     */
    public static EdifEnvironment parseAndMergeEdif(String topFilename, Collection<String> dirs,
            Collection<String> files, EdifLibrary primLib, boolean allowOpenPins, boolean quitOnError)
            throws ParseException, FileNotFoundException {

        if (files != null && files.size() > 0)
            LogFile.out().println("Include these EDIF files: " + files);
        if (dirs != null && dirs.size() > 0)
            LogFile.out().println("Search for EDIF files in these directories: " + dirs);

        if (files == null)
            files = new ArrayList<String>();

        return getMergedEdifEnvironment(topFilename, dirs, files.iterator(), primLib, null, allowOpenPins, quitOnError);
    }

    public static EdifEnvironment parseAndMergeEdif(String topFilename, Collection<String> dirs,
            Collection<String> files, EdifLibrary primLib) throws ParseException, FileNotFoundException {
        return parseAndMergeEdif(topFilename, dirs, files, primLib, true, false);
    }

    /**
     * Create a Collection of "default" directories that should be searched for
     * black box definitions. This method will include the directory "."
     * 
     * @return
     */
    public static Set<String> createDefaultDirs() {
        // A TreeSet is used so that multiple copies of the same String
        // will not be added to the Collection.
        Set<String> dirs = new TreeSet<String>();
        dirs.add(new String("."));
        return dirs;
    }

    /**
     * A simple method for parsing arguments. This method will search for
     * arguments in the form <code><flag> <value></code>. Each time the flag
     * is used, the successor <code><value></code> is added to a Collection of
     * Strings. A null will be returned if no parsed arguments are found. This
     * method uses a TreeSet to make sure that copies of the argument are not
     * included in the set.
     */
    public static Set<String> parseArguments(String args[], String flag) {

        Set<String> list = new TreeSet<String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(flag)) {
                i++;
                if (args.length > i) {
                    list.add(args[i]);
                }
            }
        }
        return list;
    }

    /**
     * @param args An array of String objects, the arguments to be parsed
     * @param flag
     * @param numValues
     * @return A Set of List values
     */
    public static Set<String> parseArguments(String args[], String flag, int numValues) {
        Set<String> list = new TreeSet<String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(flag)) {
                i++;
                ArrayList<String> values = new ArrayList<String>();
                int firstValIndex = i;
                for (; i < firstValIndex + numValues; i++) {
                    if (args.length > i) {
                        values.add(args[i]);
                    }
                }
                list.addAll(values); // Will this properly add each of the
                // elements of the ArrayList "values" to the
                // Set "list"?
            }
        }
        if (list.size() > 0)
            return list;
        else
            return null;
    }

    public static boolean containsArgument(String args[], String arg) {
        for (int i = 0; i < args.length; i++)
            if (args[i].equals(arg))
                return true;
        return false;
    }

    /**
     * This static method will parse the top-level EDIF file and merge the
     * appropriate EdifFiles into this object. This method will support a search
     * path as well as the parsing of specific auxilliary EDIF files.
     * <p>
     * This particular method is currently hard coded to expect the
     * XilinxLibrary.
     * 
     * @param filename Filename of the top-level EDIF file
     * @param args The command line arguments (looking for -L directory searches
     * and -f file searches
     * @return The merged EdifEnvironment object.
     */
    public static EdifEnvironment getMergedEdifEnvironment(String filename, String[] args) {
        // Identify any search directories and default files
        Collection<String> dirs = createDefaultDirs();
        Set<String> newDirs = parseArguments(args, "-L");

        if (newDirs != null && newDirs.size() > 0)
            dirs.addAll(newDirs);
        Set<String> files = parseArguments(args, "-f");

        if (files != null && files.size() > 0)
            LogFile.out().println("Include these EDIF files: " + files);
        if (dirs != null && dirs.size() > 0)
            LogFile.out().println("Search for EDIF files in these directories: " + dirs);

        EdifEnvironment top = null;
        try {
            //top = parseAndMergeEdif(args[0], dirs, files, primLib, System.out);
            top = getMergedEdifEnvironment(filename, dirs, files, XilinxGenLib.library, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return top;
    }

    /**
     * Parse an EDIF file and recursively merge in all black boxes encountered.
     * 
     * @param filename Filename of the EDIF file
     * @param dirs A collection of directories to search for black boxes
     * @param files A collection of files to search for black boxes
     * @param primLib A primitive library to use for tagging all primitives in
     * the design. Leave null for no primitive tagging.
     * @param outstream A PrintStream to direct output to.
     * @param fileNameToEnv A map of filenames to parsed EdifEnvironments used
     * for recursive processing. Leave null for the top-level file.
     * @return The merged EdifEnvironment object.
     * @throws FileNotFoundException
     * @throws ParseException
     */
    public static EdifEnvironment getMergedEdifEnvironment(String filename, Collection<String> dirs,
            Collection<String> files, EdifLibrary primLib, Map<String, EdifEnvironment> fileNameToEnv)
            throws FileNotFoundException, ParseException {
        return getMergedEdifEnvironment(filename, dirs, files.iterator(), primLib, fileNameToEnv, true, true);
    }

    /**
     * Parse an EDIF file and recursively merge in all black boxes encountered.
     * 
     * @param filename Filename of the EDIF file
     * @param dirs A collection of directories to search for black boxes
     * @param files A collection of files to search for black boxes
     * @param primLib A primitive library to use for tagging all primitives in
     * the design. Leave null for no primitive tagging.
     * @param outstream A PrintStream to direct output to.
     * @param fileNameToEnv A map of filenames to parsed EdifEnvironments used
     * for recursive processing. Leave null for the top-level file.
     * @param openPins whether or not to allow matching on open pins.
     * @return The merged EdifEnvironment object.
     * @throws FileNotFoundException
     * @throws ParseException
     */
    public static EdifEnvironment getMergedEdifEnvironment(String filename, Collection<String> dirs,
            Iterator<String> files_i, EdifLibrary primLib, Map<String, EdifEnvironment> fileNameToEnv_param,
            boolean openPins, boolean quitOnError) throws FileNotFoundException, ParseException {

        PrintStream outstream = LogFile.out();
        if (outstream != null)
            outstream.println("Parsing file " + filename);

        EdifEnvironment top = EdifParser.translate(filename);
        Map<String, EdifEnvironment> fileNameToEnv;
        if (fileNameToEnv_param == null)
            fileNameToEnv = new LinkedHashMap<String, EdifEnvironment>();
        else
            fileNameToEnv = fileNameToEnv_param;

        // tag primitives by comparing against primitive library
        if (primLib != null)
            Primitives.tagPrimitives(top.getLibraryManager(), primLib);

        // find all black boxes
        Collection<EdifCell> blackBoxes = top.getLibraryManager().findBlackBoxes();

        if (blackBoxes.size() > 0) {

            // parse files in Collection files
            while (files_i.hasNext()) {
                String fName = files_i.next();
                if (!fileNameToEnv.containsKey(fName))
                    fileNameToEnv.put(fName, getMergedEdifEnvironment(fName, dirs, files_i, primLib, fileNameToEnv,
                            openPins, quitOnError));
            }

            // parse possible matching files in Collection dirs
            if (dirs != null)
                for (String dirName : dirs) {
                    if (!dirName.endsWith(File.separator))
                        dirName += File.separator;
                    for (EdifCell blackBox : blackBoxes) {
                        for (int i = 0; i < EDIF_EXTENSIONS.length; i++) {
                            String fullPath = dirName + blackBox.getName() + "." + EDIF_EXTENSIONS[i];
                            String fName = blackBox.getName() + "."
									+ EDIF_EXTENSIONS[i];
							if (filename_exists(dirName, fName)
									&& !fileNameToEnv.containsKey(fullPath)
                                    && (!fullPath.equals(filename))) {
                                // does this file exist? and did we already parse it?
                                fileNameToEnv.put(fullPath, getMergedEdifEnvironment(fullPath, dirs, files_i, primLib,
                                        fileNameToEnv, openPins, quitOnError));
                            }
                        }
                    }
                }

            // make a map of black box cells to definition cells
            Map<EdifCell, EdifCell> blackBoxToDefinition = new LinkedHashMap<EdifCell, EdifCell>();
            for (EdifCell blackBox : blackBoxes) {
                EdifCell definition = null;
                if (outstream != null)
                    outstream.print("Searching for definition for black box " + blackBox + " . ");
                if (fileNameToEnv.isEmpty())
                    error_code = NO_MATCHING_INSTANCE_NAME;
                for (EdifEnvironment env : fileNameToEnv.values()) {
                    definition = findMatchingCell(blackBox, env);
                    if (definition != null) {
                        blackBoxToDefinition.put(blackBox, definition);
                        break;
                    }
                }

                /*
                 * No exact match could be found. Now we start looking for
                 * alternatives. First look for EDIF files that are a partial
                 * match, according to inferred xilinx rules
                 */
                if (blackBoxToDefinition.get(blackBox) == null)
                    definition = findOpenPinBlackBoxes(fileNameToEnv, openPins, blackBoxToDefinition, blackBox,
                            definition);

                //Next we look for binary definitions (ngo, ngo)
                findBinaryBlackBoxes(dirs, blackBox, definition);

                //Next we look for a utilization guide file.
                findBlackBoxUtilization(dirs, blackBox, definition);

                if (outstream != null)
                    outstream.println(getErrorString(quitOnError));

            }
            //end looping through blackboxes

            // merge black boxes
            EdifMergingPolicy mergingPolicy = new ReuseNewLeafCellsMergingPolicy();
            for (Iterator<EdifCell> i = blackBoxToDefinition.keySet().iterator(); i.hasNext();) {
                EdifCell blackBox = i.next();
                EdifCell blackBoxDefinition = blackBoxToDefinition.get(blackBox);
                if (blackBoxDefinition != null)
                    replaceBlackBox(blackBox, blackBoxDefinition, outstream, mergingPolicy);
            }
        }
        return top;
    }

    private static boolean filename_exists(String dName, String file) {

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File d, String name) {
            	boolean result = false;
            	for (String extension : EDIF_EXTENSIONS) {
            		if (name.toLowerCase().endsWith(extension)) {
            			result = true;
            			break;
            		}
            	}
                return result;
            }
        };
        File dir = new File(dName);
        ArrayList<String> children = new ArrayList<String>(Arrays.asList(dir.list(filter)));
        for (String name : children) {
            if (file.equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    /**
     * Find Xilinx Binary file, and tell the user that they have a possible
     * BlackBox match
     * 
     * @param dirs
     * @param blackBox
     * @param definition
     */
    private static void findBinaryBlackBoxes(Collection<String> dirs, EdifCell blackBox, EdifCell definition) {
        if (dirs != null && definition == null) {
            for (String dirName : dirs) {
                if (!dirName.endsWith(File.separator))
                    dirName += File.separator;
                for (int i = 0; i < EDIF_BINARY_EXTENSIONS.length; i++) {
                    String fName = dirName + blackBox.getName() + "." + EDIF_BINARY_EXTENSIONS[i];
                    if (new File(fName).exists()) {
                        // does this file exist?
                        error_code = MATCHING_BINARY_FILE;
                    }
                }
            }
        }
    }

    /**
     * Find Utilization file and add it to the Utilization list on the EdifCell
     * 
     * @param dirs
     * @param blackBox
     * @param definition
     */
    private static void findBlackBoxUtilization(Collection<String> dirs, EdifCell blackBox, EdifCell definition) {
        if (dirs != null && definition == null) {
            for (String dirName : dirs) {
                if (!dirName.endsWith(File.separator))
                    dirName += File.separator;
                for (int i = 0; i < EDIF_BB_UTILIZE_EXTENSIONS.length; i++) {
                    String fName = dirName + blackBox.getName() + "." + EDIF_BB_UTILIZE_EXTENSIONS[i];
                    if (new File(fName).exists()) {
                        error_code = MATCHING_UTILIZATION_FILE;
                        //Parse the file...
                        parseBlackBoxInfoFile(blackBox, fName);

                        //                        blackBox.addBlackBoxResource("BRAM");
                        //                        for (int k = 0; k < 100; k++)
                        //                            blackBox.addBlackBoxResource("LUT");

                    }
                }
            }
        }
    }

    /**
     * Parses a file named EdifCell.bb to find out information about the
     * resources for a blackbox, that we don't have the source for. The file
     * format is: PRIMATIVE_NAME:num_of_primatives For example: BRAM:1 LUT:100
     * FF:50
     * 
     * @param blackBox
     * @param filename
     */
    private static void parseBlackBoxInfoFile(EdifCell blackBox, String filename) {
        try {
            // Open the file that is the first 
            // command line parameter
            FileInputStream fstream = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                String[] parts = strLine.split(":");
                for (int i = 0; i < Integer.parseInt(parts[1]); i++) {
                    blackBox.addBlackBoxResource(parts[0]);
                }
            }
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            LogFile.err().println("Error opening black box utilization file: " + e.getMessage());
        }
    }

    /**
     * Find open pins and allow them to match as blackboxes, per Xilinx rules.
     * i.e. the blackbox can have open outputs, but not open inputs.
     * 
     * @param fileNameToEnv
     * @param openPins
     * @param blackBoxToDefinition
     * @param blackBox
     * @param definition
     * @return
     */
    private static EdifCell findOpenPinBlackBoxes(Map<String, EdifEnvironment> fileNameToEnv, boolean openPins,
            Map<EdifCell, EdifCell> blackBoxToDefinition, EdifCell blackBox, EdifCell definition) {

        if (openPins) {
            //EdifCell definition=null;
            for (EdifEnvironment env : fileNameToEnv.values()) {
                definition = findLooseMatchingCell(blackBox, env);
                if (definition != null) {
                    blackBoxToDefinition.put(blackBox, definition);
                    break;
                }
            }
        }
        return definition;
    }

    private static String getErrorString(boolean quitOnFailure) throws ParseException {
        String ret = "";
        boolean failed = false;
        switch (error_code) {
        case MATCH_FOUND:
            ret = "Found";
            break;
        case NO_MATCHING_INSTANCE_NAME:
            ret = "ERROR: No matching instance name found";
            failed = true;
            break;
        case NO_MATCHING_INTERFACE:
            ret = "ERROR: Instance found, but had wrong interface";
            failed = true;
            break;
        case MATCHING_BINARY_FILE:
            ret = "Found: Possible binary match found";
            break;
        case INFER_OPEN_OUTPUT_PINS:
            ret = "Found: Infering open output ports on Blackbox";
            LogFile.log().println(ret + "\nUse --debug to see the mismatched list of ports in the log file");
            break;
        case INFER_OPEN_INPUT_PINS:
            //ret="ERROR: Can't infer open inputs to Blackbox";
            //failed=true;
            ret = "Found: Infering open input ports on Blackbox";
            LogFile.log().println(ret + "\nUse --debug to see the mismatched list of ports in the log file");
            break;
        case MATCHING_UTILIZATION_FILE:
            ret = "Found utilization file, trying to infer utilization...";
            break;
        default:
            ret = "ERROR: Unknown error";
            failed = true;
        }
        if (quitOnFailure && failed)
            throw new ParseException(ret);

        return ret;
    }

    /**
     * Sample "main" to demonstrate the merging of edif files.
     */
    public static void main(String args[]) {

        /** The value of the minimum # of args for this class. * */
        int MIN_ARGS = 1;

        /*
         * The value of the string printed out when there is a problem with the
         * argument string.
         */
        String usageString = "Usage: java EdifMergeParser <top file> [-L <search directory>]* [-f <filename>]* [-o <outputfilename>]";

        if (args.length < MIN_ARGS) {
            LogFile.out().println(usageString);
            System.exit(1);
        }

        EdifEnvironment top = getMergedEdifEnvironment(args[0], args);

        LogFile.out().println("Done");

        Set<String> outputFile = parseArguments(args, "-o");
        String OutputFileName = null;
        if (outputFile == null || outputFile.size() < 1)
            OutputFileName = new String("merge.edf");
        else
            OutputFileName = outputFile.iterator().next();

        try {
            EdifPrintWriter epw = new EdifPrintWriter(new FileOutputStream(OutputFileName));
            top.toEdif(epw);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
            System.exit(1);
        }

        LogFile.out().println();
    }

    /**
     * The types of acceptable extensions for EDIF files.
     * <p>
     * Note that the current extensions do not include upper-case versions for
     * windows compatibility. This should be augmented to check for the OS and
     * add upper case extensions if necessary
     */
    public static final String[] EDIF_EXTENSIONS = { "edn", "edf", "ndf" };

    public static final String[] EDIF_BINARY_EXTENSIONS = { "ngc", "ngo" };

    public static final String[] EDIF_BB_UTILIZE_EXTENSIONS = { "bb" };

    private static int error_code = 0;

    private static final int MATCH_FOUND = 0;

    private static final int NO_MATCHING_INSTANCE_NAME = 1;

    private static final int NO_MATCHING_INTERFACE = 2;

    private static final int MATCHING_BINARY_FILE = 3;

    private static final int MATCHING_UTILIZATION_FILE = 6;

    private static final int INFER_OPEN_OUTPUT_PINS = 4;

    private static final int INFER_OPEN_INPUT_PINS = 5;

    public static final boolean DEBUG = false;

}
