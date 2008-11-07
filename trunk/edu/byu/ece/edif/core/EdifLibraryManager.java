/*
 * Represents a List of related EdifLibraries.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


//////////////////////////////////////////////////////////////////////////
////EdifLibraryManager
/**
 * <p>
 * Represents and manages a list of related EDIF libraries ({@link EdifLibrary}).
 * This object has a parent {@link EdifEnvironment} object and a list of
 * EdifLibrary objects. Each library must have a unique name and the uniqueness
 * of the names is managed by a {@link ArrayListNameSpaceResolver} object.
 * <p>
 * The libraries within this object are related and must be managed according to
 * the "define before use" rules of EDIF. This means that any EdifCell object
 * instanced within a parent EdifCell object must be defined <em>before</em>
 * the parent EdifCell is defined.
 * <p>
 * This object manages the ordering of EdifCell objects within libraries and the
 * ordering of the libraries. Major issues: <br>
 * <h2>addLibrary</h2>
 * <ul>
 * <li> The purpose of this method is to add a library that is currently not a
 * part of this manager and link it into this manager correctly. </li>
 * </ul>
 * <ol>
 * <li> throws NameDuplicationException if there is a name conflict </li>
 * <li> do not rename! A valid name must be given. </li>
 * <li> Do we need to go through and validate the references of cells </li>
 * </ol>
 * <ul>
 * <li> in the added library to those libraries that have already been added? It
 * seems like we should go through all of the previously defined libraries and
 * match by name those library elements that are referenced by the cells in the
 * library we are adding. </li>
 * </ul>
 * <br>
 * <h2>EdifLibrary.findInstancedLibraryCells(EdifCell)</h2>
 * <ul>
 * <li> Should add a parameter that says whether to find instances by name or
 * find instances by reference. </li>
 * </ul>
 * <br>
 * <h2>addLeafCell</h2>
 * <ul>
 * <li> Used by only addCellDeep </li>
 * <li> This method doesn't seem to make sense here. This class can be used to
 * help find the appropriate library, but most of the method should be performed
 * somewhere else. It would probably best fit in the EdifMergeParser class.
 * </li>
 * </ul>
 * <br>
 * <h2>addCellDeep</h2>
 * <ul>
 * <li> used by EdifMergeParser. Move to this class </li>
 * </ul>
 * <br>
 * <h2>EdifLibrary containsCell(EdifCell)</h2>
 * <ul>
 * <li> change to “getLibraryContainingCell”. </li>
 * <li> Return null if there is no library that contains the cell. This method
 * performs a search based on reference, not name. Note that the “boolean
 * EdifLibrary.containsCell(EdifCell)” method called by this method does not
 * need a name change. </li>
 * </ul>
 * <br>
 * <h2>deleteCell</h2>
 * <ul>
 * <li> Get rid of this method. Call the EdifLibrary version as needed. </li>
 * <li> We need a better way of managing the deletion of cells in the libraries.
 * It is possible that a cell is deleted but there are still references to the
 * cell. We would want to generate an exception when this occurs
 * (“ReferencedCell deletion exception”). </li>
 * <li> The delete code would need to check all successor libraries to see if
 * the cell was referenced and if it was, generate an exception. </li>
 * </ul>
 * <br>
 * <h2>findBlackBoxes</h2>
 * <ul>
 * <li> keep. </li>
 * <li> Methods in EdifLibraryManager may look much the same as EdifLibrary
 * </li>
 * </ul>
 * <br>
 * <h2>findCellInstancesOf</h2>
 * <ul>
 * <li> OK. </li>
 * <li> Bad name (should be findInstancesOf(EdifCell)) however, there are
 * similar methods: EdifLibrary.findCellInstancesOf(EdifCell)
 * EdifCell.findCellInstancesOf(EdifCell) (This is worth doing at some point)
 * </li>
 * <li> Only findEarliestPositionToAdd is used by any method (addLibrary). All
 * of these methods can be removed so long as the “library ordering” problem is
 * solved. This ordering is needed by “addLibrary” to make sure a library is
 * added at the proper time (this goes under the addLibrary issue). </li>
 * </ul>
 * <br>
 * <h2>findEarliestLibraryToAdd</h2>
 * <br>
 * <h2>findEarliestPositionToAdd</h2>
 * <br>
 * <h2>findLatestLibraryToAdd</h2>
 * <br>
 * <h2>findLastestPositionToAdd</h2>
 * <br>
 * <h2>findEarliestPositionToAdd</h2>
 * <ol>
 * <li> calls EdifLibrary.findInstancedLibraryCells(EdifLibrary) </li>
 * <li> calls EdifLibrary.findInstancedLibraryCells(EdifCell)
 * <ul>
 * <li> Uses reference matching to see if the cell uses the library. </li>
 * <li> It should use name matching. </li>
 * </ul>
 * </li>
 * </ol>
 * <br>
 * <h2>findNonReferencedCells()</h2>
 * <ul>
 * <li> keep this method. </li>
 * </ul>
 * <br>
 * <h2>getCell(String)</h2>
 * <ul>
 * <li> this method has problems as it is possible to have more than one
 * EdifCell with the same name but in different libraries. Need to use
 * “getCells” method and fix all references to this method. </li>
 * </ul>
 * <br>
 * <h2>getCells()</h2>
 * <ul>
 * <li> ok </li>
 * </ul>
 * <br>
 * <h2>getCells(String)</h2>
 * <ul>
 * <li> ok </li>
 * </ul>
 * <br>
 * <h2>getFirstPrimitiveLibrary</h2>
 * <ul>
 * <li> this is an awkward method. Perhaps there should be a different class
 * that manages primitive libraries. I suppose it is ok here. </li>
 * </ul>
 * <br>
 * <h2>getLibraries()</h2>
 * <ul>
 * <li> ok </li>
 * </ul>
 * <br>
 * <h2>getLibrary(String)</h2>
 * <ul>
 * <li> need to fix for lower case name compare </li>
 * </ul>
 * <br>
 * <h2>nameClash()</h2>
 * <ul>
 * <li> keep this method but modify it so that it conforms to some interface.
 * Give a EdifNameable as a parameter. </li>
 * </ul>
 * <br>
 * <h2>pruneNonReferencedCells()</h2>
 * <ul>
 * <li> keep method (useful). Update as necessary. </li>
 * <li> Don't call delete method as a part of this class. Instead, do the
 * following for each EdifCell object that you get from the
 * findNonReferencedCells:
 * <ul>
 * <li> call deleteCell on the library in which the cell resides. </li>
 * </ul>
 * </li>
 * </ul>
 * <br>
 * <h2>tagLeafCellsAsPrimitives()</h2>
 * <ul>
 * <li> awkward. Need to have a special package that handles all of this
 * primitive handling. I think the best place for this would be
 * byucc.edif.libraries (although primitives would be a better name). Why would
 * you want to tag all libraries as primitives? This makes no sense (used by
 * EdifRandomCircuit). </li>
 * </ul>
 * <br>
 * <h2>tagPrimitives()</h2>
 * <ul>
 * <li> move to the new Primitive handling classes. </li>
 * </ul>
 * <br>
 * <h2>toEdif</h2>
 * <br>
 * <h2>toString</h2>
 * <br>
 * <h2>trimToSize</h2>
 * <br>
 * <h2>validateOrder</h2>
 * <ul>
 * <li> this is awkward. The data structure should always be valid. Track down
 * when and how this is used and remove this method. </li>
 * </ul>
 * <br>
 * <h2>Primitive handling:</h2>
 * <ol>
 * <li> create new class byucc.edif.libraries.PrimitiveHandling </li>
 * <li> tagLeafCellsAsPrimitives method for tagging </li>
 * </ol>
 * 
 * @see EdifLibrary
 * @version $Id:EdifLibraryManager.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class EdifLibraryManager implements Trimable, EdifOut, Serializable {

    /**
     * Construct an EdifLibraryManager Object with the specified
     * EdifEnvironment.
     * 
     * @param edifFile The EdifEnvironment that contains this EdifLibraryManager
     */
    public EdifLibraryManager(EdifEnvironment edifFile) {
        _edifEnvironment = edifFile;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Add a library to this library manager, and sets its library manager to
     * 'this'
     * <p>
     * TODO: should this method even exist?
     * 
     * @param library The library to add into this manager, defaulted to add at
     * the earliest position
     * @return True if the library was added
     * @see EdifLibraryManager#findLatestPositionToAdd(EdifLibrary)
     */
    public boolean addLibrary(EdifLibrary library) throws EdifNameConflictException {
        if (_libraries.contains(library))
            return false;
        _libraries.addElement(library);
        library.setLibraryManager(this);
        return true;

        //          if (nameClash(library))
        //          library.rename(_libraries.returnUniqueName(library.getName())); 
        //        //  insert at latest position 
        //          int position = findLatestPositionToAdd(library);
        //          boolean ret = _libraries.add(position, library);
        //          library.setLibraryManager(this); return ret;

    }

    public void addLibraries(Collection<EdifLibrary> libraries) throws EdifNameConflictException {

        for (EdifLibrary library : libraries) {
            if (!_libraries.contains(library)) {
                try {
                    _libraries.addElement(library);
                    library.setLibraryManager(this);
                } catch (EdifNameConflictException e) {
                    library.rename(getUniqueLibraryNameable(library));
                    _libraries.addElement(library);
                    library.setLibraryManager(this);
                }
            }
        }
    }

    /**
     * Search for the given cell among all libraries. If the EdifCell exists in
     * a library, it returns the given library. Otherwise, it returns null.
     */
    public EdifLibrary containsCell(EdifCell cell) {
        for (EdifLibrary lib : getLibraries()) {
            if (lib.containsCell(cell))
                return lib;
        }
        return null;
    }

    /**
     * This function only checks the library using the Object.equals() method.
     * 
     * @param lib
     * @return
     */
    public boolean containsLibrary(EdifLibrary lib) {
        return _libraries.contains(lib);
    }

    /**
     * This version of the function will check the library's name against
     * library names of those it already contains.
     * 
     * @param libraryName name of the library to be checked
     * @return true if the library name is already contained in this Library
     * Manager, false if not.
     */
    public boolean containsLibrary(String libraryName) {
        return _libraries.containsName(libraryName);
    }

    /**
     * Delete a cell from the library manager, and return whether or not the
     * delete was successful.
     * 
     * @param cell The cell to be deleted.
     * @param force Forces the cell to be deleted even if it's still referenced
     * within the library manager.
     * @return True if the cell was deleted.
     * @see EdifLibrary#deleteCell(EdifCell,boolean)
     */
    public boolean deleteCell(EdifCell cell, boolean force) {
        for (EdifLibrary lib : this.getLibraries()) {
            if (force || findCellInstancesOf(cell) == null) {
                if (lib.deleteCell(cell, force))
                    return true;
            } else {
                System.err.println("Cell not deleted! The cell is still " + "referenced within the "
                        + "EdifLibraryManager.");
                return false;
            }
        }
        return false;
    }

    /**
     * This method will search through all EdifCells in the current Manager and
     * determine which cells are not defined.
     * <p>
     * The purpose of this method is to identify those EdifCell objects that are
     * "undefined". This may be used to search for other Edif files that
     * describe the given black box.
     * 
     * @return A collection of EdifCells that are Black boxes, or undefined, and
     * non-primitive
     * @see EdifLibrary#findBlackBoxes
     */
    public Collection<EdifCell> findBlackBoxes() {

        Collection<EdifCell> blackBoxes = new ArrayList<EdifCell>();

        // Iterate over every EdifCell in the current file
        for (EdifLibrary lib : this.getLibraries()) {
            blackBoxes.addAll(lib.findBlackBoxes());
        }
        return blackBoxes;
    }

    /**
     * This method will return a list of EdifCellInstances in the entire
     * libraryManager that reference the passed in cell. This method will return
     * null if no EdifCellInstances are found.
     * 
     * @param cell The cell to check references for
     * @return A collection of EdifCellInstance Objects in this library manager
     * that refer to the passed-in cell
     * @see EdifLibrary#findCellInstancesOf(EdifCell)
     */
    public Collection<EdifCellInstance> findCellInstancesOf(EdifCell cell) {
        Collection<EdifCellInstance> instances = new ArrayList<EdifCellInstance>();
        for (EdifLibrary lib : this.getLibraries()) {
            Collection<EdifCellInstance> subCellInstances = lib.findCellInstancesOf(cell);
            if (subCellInstances != null)
                instances.addAll(subCellInstances);
        }
        // check the topCell
        EdifCellInstance topCellInstance = _edifEnvironment.getTopDesign().getTopCellInstance();
        if (topCellInstance != null && topCellInstance.getCellType() == cell)
            instances.add(topCellInstance);

        if (instances.size() == 0)
            return null;
        return instances;
    }

    /**
     * This method returns the earliest library that this cell can be added to.
     * All of the passed in EdifCell's EdifCellInstances must instance cells
     * declared inside or after the library it will get added to.
     * 
     * @param cell The cell to be added into which library.
     * @param addToPrimitiveLibraryOK Specifies whether or not it is ok to add
     * the cell to a primitive library.
     * @return The earliest library to add the cell in.
     * @see EdifLibraryManager#findEarliestPositionToAdd(EdifLibrary)
     */
    public EdifLibrary findEarliestLibraryToAdd(EdifCell cell, boolean addToPrimitiveLibraryOK) {
        EdifLibrary lib = null;
        int pos = -1;
        for (int i = _libraries.size() - 1; i >= 0; i--) {
            lib = _libraries.get(i);

            if ((addToPrimitiveLibraryOK || !lib.isPrimitiveLibrary()) && !lib.nameClash(cell)) {
                if (lib.findInstancedLibraryCells(cell) != null)
                    return lib;
                pos = i;
            }
        }
        // pos should never be -1
        if (pos == -1) {
            System.err.println("Bad library, pos == -1, most likely a cell by the name: " + cell.getName()
                    + " already exists in the file.");
            return null;
        }
        return _libraries.get(pos);
    }

    /**
     * Return the earliest position to add the library within the collection of
     * libraries, so as to avoid violating the define before use rule.
     * 
     * @param add The library to add to the earliest position
     * @return The exact position to add this library to
     * @see EdifLibraryManager#findEarliestPositionToAdd(EdifLibrary)
     */
    public int findEarliestPositionToAdd(EdifLibrary add) {
        for (int i = _libraries.size() - 1; i >= 0; i--) {
            EdifLibrary lib = _libraries.get(i);

            Collection<EdifCell> instances = lib.findInstancedLibraryCells(add);
            if (instances != null && instances.size() > 0) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * This method returns the latest library that this cell can be added to. If
     * the passed in EdifCell is referenced in a library then it has to be added
     * to the position at or before the referring library.
     * 
     * @param cell The cell to be added into which library.
     * @param addToPrimitiveLibraryOK Specifies whether or not it is ok to add
     * the cell to a primitive library.
     * @return The latest library to add the cell in.
     * @see EdifLibraryManager#findLatestPositionToAdd(EdifLibrary)
     */
    public EdifLibrary findLatestLibraryToAdd(EdifCell cell, boolean addToPrimitiveLibraryOK) {
        EdifLibrary lib = null;
        int pos = -1;
        for (int i = 0; i < _libraries.size(); i++) {
            lib = _libraries.get(i);
            if ((addToPrimitiveLibraryOK || !lib.isPrimitiveLibrary()) && !lib.nameClash(cell)) {
                if (lib.findCellInstancesOf(cell) != null)
                    return lib;
                pos = i;
            }
        }
        // pos should never be -1
        if (pos == -1) {
            System.err.println("Bad library, pos == -1, most likely a cell by the name: " + cell.getName()
                    + " already exists in the file.");
            return null;
        }
        return _libraries.get(pos);
    }

    /**
     * Return the latest position to add the library within the collection of
     * libraries, so as to avoid violating the define before use rule.
     * 
     * @param add The library to add to the latest position
     * @return The exact position to add this library to
     * @see EdifLibraryManager#findPositionToAdd
     */
    public int findLatestPositionToAdd(EdifLibrary add) {
        for (int i = 0; i < _libraries.size(); i++) {
            EdifLibrary lib = _libraries.get(i);

            if (lib.findCellInstancesOf(add) != null)
                return i;
        }
        return _libraries.size();
    }

    //      public Collection findNonReferencedCells(EdifCell topCell) {
    //        Set<EdifCell> nonReferenced = new LinkedHashSet<EdifCell>(getCells());
    //        Set<EdifCell> referenced = new LinkedHashSet<EdifCell>();
    //        visitCell(topCell, referenced);
    //        nonReferenced.removeAll(referenced);
    //        return nonReferenced;
    //    }

    /**
     * Starting at the top cell, find all cells referenced by it either directly
     * or indirectly through recursion and return a Collection of EdifCells that
     * are not referenced.
     * 
     * @return a Collection<EdifCell> not referenced (recursively) - this means
     * that a cell returned in the collection could be referenced by another
     * cell, but it is part of a different tree than the main tree
     */
    public Collection<EdifCell> findNonReferencedCells(EdifCell topCell) {
        Set<EdifCell> nonReferenced = new LinkedHashSet<EdifCell>(getCells());
        Queue<EdifCell> referenced = new LinkedList<EdifCell>();
        referenced.offer(topCell);
        while (referenced.size() > 0) {
            EdifCell c = referenced.remove();
            nonReferenced.remove(c);
            Collection<EdifCell> instanced = c.getInstancedCellTypes();
            if (instanced != null)
                for (EdifCell referencedCell : instanced)
                    referenced.offer(referencedCell);
        }
        return nonReferenced;
    }

    /**
     * Return the first cell whose name matches the give String.
     * <p>
     * This method is awkward and likely to cause errors. There may be many
     * cells within a set of libraries of a given name and it doesn't seem safe
     * to have a method that only returns the first element of a given name.
     * Other approachecs should be used and this method should be removed. The
     * user of this method includes: - XilinxLibrary.findOrAddXilinxPrimitive
     * (used to find a Xilinx primitive in the manager) (This method is used by
     * many other methods) - LUTReplace.logicLutRam.Replace (this method should
     * call the xilinx verion).
     * 
     * @param name The name of the cell to return
     * @return An EdifCell whose name matches the given String
     * @see EdifLibrary#getCell(String)
     */
    public EdifCell getCell(String name) {
        for (EdifLibrary lib : this.getLibraries()) {
            EdifCell ret = lib.getCell(name);
            if (ret != null)
                return ret;
        }
        return null;
    }

    /**
     * This method will return a List of all cells in the library manager. The
     * cells will be returned in proper order (from lowest to highest).
     * 
     * @return A List Object of all the cells in the entire library manager.
     */
    public List<EdifCell> getCells() {
        ArrayList<EdifCell> list = new ArrayList<EdifCell>();
        for (EdifLibrary lib : this.getLibraries()) {
            list.addAll(lib.getCells());
        }
        return list;
    }

    /**
     * Return all the EdifCell objects whose name matches the give String.
     * 
     * @param name The name of the cell to return
     * @return A Collection of EdifCell objects
     * @see EdifLibrary#getCell(String)
     */
    public Collection<EdifCell> getCells(String name) {
        ArrayList<EdifCell> cells = new ArrayList<EdifCell>();
        for (EdifLibrary lib : this.getLibraries()) {
            EdifCell ret = lib.getCell(name);
            if (ret != null)
                cells.add(ret);
        }
        return cells;
    }

    /**
     * @return a List of EdifLibrary objects in a valid order according to the
     * define before use rule. The order is computed using a topological sort.
     */
    public List<List<EdifLibrary>> getDFSForest() {

        List<List<EdifLibrary>> forest = new ArrayList<List<EdifLibrary>>();
        List<EdifLibrary> finishedLibs = new ArrayList<EdifLibrary>();
        List<EdifLibrary> unvisitedLibs = new ArrayList<EdifLibrary>(_libraries);

        while (!unvisitedLibs.isEmpty()) {
            EdifLibrary lib = unvisitedLibs.get(0);
            List<EdifLibrary> currentList = new ArrayList<EdifLibrary>();
            visitLib(lib, currentList, unvisitedLibs, finishedLibs);
            forest.add(currentList);
        }
        return forest;
    }

    /**
     * Return the file pointing to this library manager.
     * 
     * @return The EdifEnvironment that contains this library manager.
     */
    public EdifEnvironment getEdifEnvironment() {
        return _edifEnvironment;
    }

    /**
     * Return the first primitive library in the collection of libraries. Useful
     * when a primitive must be added to a primitive library.
     * 
     * @return The first Primitive Library
     */
    public EdifLibrary getFirstPrimitiveLibrary() {
        for (EdifLibrary lib : this.getLibraries()) {

            if (lib.isPrimitiveLibrary())
                return lib;
        }
        return null;
    }

    /**
     * Return the libraries contained by this library manager.
     * 
     * @return A List of the EdifLibrary objects within this library manager.
     */
    public List<EdifLibrary> getLibraries() {
        return new ArrayList<EdifLibrary>(_libraries);
    }

    /**
     * Return the library based on the string lib.
     * <p>
     * TODO: lower case compare?
     * 
     * @param lib The String that will be used to find the library
     * @return The EdifLibrary whose name matches lib
     */
    public EdifLibrary getLibrary(String lib) {
        //        String lc_lib = lib.toLowerCase();
        for (EdifLibrary l : this.getLibraries()) {
            if (NamedObjectCompare.equals(l, lib))
                return l;
        }
        return null;
    }

    /**
     * @return a List of EdifLibrary objects in a valid order according to the
     * define before use rule. The order is computed using a topological sort.
     */
    public List<EdifLibrary> getValidLibraryOrder() {

        List<EdifLibrary> finishedLibs = new ArrayList<EdifLibrary>();
        List<EdifLibrary> unvisitedLibs = new ArrayList<EdifLibrary>(_libraries);

        while (!unvisitedLibs.isEmpty()) {
            EdifLibrary lib = unvisitedLibs.get(0);
            visitLib(lib, finishedLibs, unvisitedLibs, finishedLibs);
        }

        return finishedLibs;
    }

    /**
     * Return iterator of the EdifLibrary objects contained within this object.
     * 
     * @return An Iterator Object of the libraries within this library manager.
     */
    public Iterator<EdifLibrary> iterator() {
        return _libraries.iterator();
    }

    /**
     * Return true if the library can be added to the library name space,
     * otherwise false.
     * 
     * @param lib The library to chech to see if there is a name clash with this
     * libraries of this library manager
     * @return True if there is a clash, false if not
     */
    public boolean nameClash(EdifLibrary lib) {
        return _libraries.nameClash(lib);
    }

    /**
     * Prune cells that are not referenced (recursively) by the top cell.
     * 
     * @param topCell the design's top cell
     */
    public void pruneNonReferencedCells(EdifCell topCell) {
        boolean ret = true;
        while (ret) {
            ret = false;
            Collection<EdifCell> toPrune = findNonReferencedCells(topCell);
            if (toPrune != null)
                for (EdifCell pruneMe : toPrune) {
                    deleteCell(pruneMe, true);
                    ret = true;
                }
        }
    }

    /**
     * Prune cells that are not referenced (recursively) by the top cell.
     */
    public void pruneNonReferencedCells() {
        EdifCell topCell = getEdifEnvironment().getTopCell();
        pruneNonReferencedCells(topCell);
    }

    public EdifNameable getUniqueLibraryNameable(EdifNameable en) {
        return _libraries.getUniqueNameable(en);
    }

    public EdifNameable getUniqueLibraryNameable(EdifLibrary lib) {
        return _libraries.getUniqueNameable(lib);
    }

    /**
     * Print the Edif equivalent of this EdifLibraryManager.
     */
    public void toEdif(EdifPrintWriter epw) {
        epw.flush();
        for (EdifLibrary el : this.getValidLibraryOrder()) {
            el.toEdif(epw);
        }
    }

    public String toString() {
        return getValidLibraryOrder().toString();
        //        The following code simply implements List.toString(), so I removed it and simply called List.toString(). -James
        //        StringBuilder sb = new StringBuilder();
        //        sb.append("[");
        //        for (Iterator<EdifLibrary> it = getValidLibraryOrder().iterator(); it.hasNext();) {
        //            sb.append(it.next());
        //            if (it.hasNext())
        //                sb.append(", ");
        //        }
        //        sb.append("]");
        //        return sb.toString();
    }

    /**
     * Trim to size all Trimmable objects in this library manager.
     */
    public void trimToSize() {
        for (EdifLibrary l : _libraries)
            l.trimToSize();
    }

    /**
     * Visit a library during the depth first search topological sort.
     * 
     * @param lib The EdifLibrary to visit
     * @param currentLib A List of libraries to add libraries to
     * @param unvisitedLibs A List of unvisited libraries
     * @param finishedLibs A List of finished libraries
     */
    private void visitLib(EdifLibrary lib, List<EdifLibrary> currentTree, List<EdifLibrary> unvisitedLibs,
            List<EdifLibrary> finishedLibs) {
        unvisitedLibs.remove(lib);
        Collection<EdifLibrary> instancedLibs = lib.getExternalReferencedLibraries();

        for (EdifLibrary l : instancedLibs) {
            if (!unvisitedLibs.contains(l) && !finishedLibs.contains(l) && !lib.equals(l))
                throw new EdifRuntimeException("circular library dependency");
            if (unvisitedLibs.contains(l))
                visitLib(l, currentTree, unvisitedLibs, finishedLibs);
        }
        currentTree.add(lib);
    }

    private void visitCell(EdifCell cell, Set<EdifCell> visited) {
        visited.add(cell);
        Collection<EdifCell> referenced = cell.getInstancedCellTypes();
        if (referenced != null)
            for (EdifCell c : referenced)
                if (!visited.contains(c))
                    visitCell(c, visited);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /**
     * The EdifEnvironment that points to this library manager.
     */
    private EdifEnvironment _edifEnvironment;

    /**
     * The EdifLibrary objects of this file. Note that the order of libraries
     * within this list matters - there is a partial order between libraries
     * (i.e. sub-cells must be defined previous to the instance of the
     * sub-cell).
     */
    private EdifNameSpaceList<EdifLibrary> _libraries = new EdifNameSpaceList<EdifLibrary>(
            BasicEdifNameClashPolicy.singleAndNewClashPolicy);

}
