/*
 * Represents a library which is an ordered list of EdifCell objects.
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
import java.util.List;

import edu.byu.ece.edif.test.regression.EdifDifference;

//////////////////////////////////////////////////////////////////////////
////EdifLibrary
/**
 * Represents a library which is an ordered list of {@link EdifCell} objects.
 * <p>
 * The EdifCell objects within the library are available for use within the
 * structure of other EdifCell objects. Each EdifCell contained within the
 * library must have a unique name.
 * <p>
 * The order of EdifCell objects within the library must conform to the EDIF
 * "define before use" policy. This policy requires that all EdifCell objects
 * instantiated within a higher EdifCell object as {@link EdifCellInstance} must
 * be defined earlier in the library or earlier within a previously defined
 * library. No EdifCell object can be instanced until it has been defined. Many
 * of the methods associated with this object are used for managing the name
 * space and ordering of EdifCell objects. One of the main purposes of this
 * class is to make sure that this object ordering is preserved and maintained.
 * <p>
 * It is not necessary for all EdifCell objects to be used by the library are
 * defined in the library. EDIF supports multiple libraries and it is possible
 * that EdifCells used in a given library are actually defined in a "previous"
 * library. The {@link EdifLibraryManager} class manages these dependencies
 * between libraries. TODO: addCell changes - only have one addCell() method.
 * This method will add the cell to the end of the library. Check to make sure
 * that all of its internal cells (within current library) have been defined
 * (make sure it is valid by construction) - remove addCell(cell, boolean)
 * method. Naming issues: - throw NameDuplicationException - create a library
 * without a manager? - Generate exceptions if there is a name clash when
 * adding. - case matching for get cell - return copy of collection for get
 * cells.
 * 
 * @see EdifLibraryManager
 * @see EdifCell
 * @version $Id:EdifLibrary.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class EdifLibrary extends NamedPropertyObject implements EdifOut, Trimable {

    /**
     * Construct an empty EdifLibrary Object with the specified
     * EdifLibraryManager and name.
     * 
     * @param elm The EdifLibraryManager to construct the library into
     * @param name The name of the new EdifLibrary Object
     * @throws InvalidEdifNameException
     */
    public EdifLibrary(EdifLibraryManager elm, String name) throws EdifNameConflictException, InvalidEdifNameException {
        this(elm, name, false);
    }

    /**
     * Construct an empty EdifLibrary object with the specified
     * EdifLibraryManager and EdifNameable object as its name.
     * 
     * @param elm The EdifLibraryManager to construct the library into
     * @param name The name of the new EdifLibrary object
     */
    public EdifLibrary(EdifLibraryManager elm, EdifNameable name) throws EdifNameConflictException {
        this(elm, name, false);
    }

    /**
     * Construct an empty EdifLibrary Object with the specified
     * EdifLibraryManager, name, and value for external.
     * 
     * @param name The name of the new EdifLibrary Object
     * @param external True if this is an external library
     * @throws InvalidEdifNameException
     */
    public EdifLibrary(EdifLibraryManager elm, String name, boolean external) throws EdifNameConflictException,
            InvalidEdifNameException {
        super(name);
        elm.addLibrary(this);
        _external = external;
    }

    /**
     * Construct an empty EdifLibrary Object with the specified
     * EdifLibraryManager, EdifNameable Object as its name, and value for
     * external.
     * 
     * @param name The name-holding information Object of the new EdifLibrary
     * Object
     * @param external True if this is an External library
     */
    public EdifLibrary(EdifLibraryManager elm, EdifNameable name, boolean external) throws EdifNameConflictException {
        super(name);
        elm.addLibrary(this);
        _external = external;
    }

    /**
     * Add an EdifCell into the ArrayList at the earliest possible position in
     * the library. TODO: should this method even exist?
     * 
     * @param cell The EdifCell to add to this EdifLibrary
     * @return True if the cell was actually added to the library
     * @see EdifLibrary#deleteCell(EdifCell,boolean)
     */
    public boolean addCell(EdifCell cell) throws EdifNameConflictException {
        if (_edifCells.contains(cell))
            return false; // can't add the same cell twice
        try {
            _edifCells.addElement(cell);
            cell.setLibrary(this);
        } catch (EdifNameConflictException e) {
            //cell.rename(getUniqueEdifCellNameable(cell));
            //_edifCells.addElement(cell);
            cell.setLibrary(this);
            return false;
        }
        return true;
    }

    public void addCells(Collection<EdifCell> cells) throws EdifNameConflictException {

        for (EdifCell cell : cells) {
            if (!_edifCells.contains(cell)) {
                try {
                    _edifCells.addElement(cell);
                    cell.setLibrary(this);
                } catch (EdifNameConflictException e) {
                    //cell.rename(getUniqueEdifCellNameable(cell));
                    //_edifCells.addElement(cell);
                    cell.setLibrary(this);
                }
            }
        }

    }

    /**
     * Return true if the collection of cells contains the passed-in EdifCell.
     * 
     * @param cell The cell to check whether or not it exists in this
     * EdifLibrary
     * @return True if the passed-in EdifCell Object is contained within this
     * EdifLibrary
     */
    public boolean containsCell(EdifCell cell) {
        boolean retval = false;
        retval = _edifCells.contains(cell);
        return retval;
    }

    /**
     * Return true if the collection of cells contains a cell by the passed-in
     * name.
     * 
     * @param name The name of the cell to see if it exists by name within this
     * EdifLibrary
     * @return True if this EdifLibrary contains a cell by the name of the
     * passed-in String
     */
    public boolean containsCellByName(String name) {
        if (getCell(name) != null)
            return true;
        return false;
    }

    /**
     * Indicate whether there is a Cell in the library that clashes with the
     * passed-in cell. A clash occurs when there is a cell in the library with
     * the same name <em>and</em> the cell interfaces of the two cells is
     * different.
     * 
     * @param cell The cell to match up with among the cells in this library to
     * see if a clashing cell exists
     * @return True if this EdifLibrary contains an EdifCell that clashes with
     * the passed-in EdifCell
     */
    public boolean containsClashingCell(EdifCell cell) {
        EdifCell matchingCell = getCell(cell.getName());
        if (matchingCell != null)
            // We know the names match since matchingCell
            // is not null. Now check the interface
            if (matchingCell.equalsInterface(cell))
                return true;

        return false;
    }

    /**
     * Delete a cell from the library and return whether or not the delete was
     * successful.
     * 
     * @param cell The cell to be deleted.
     * @param force Forces the cell to be deleted even if it's still referenced
     * within the library.
     * @return True if the cell was deleted.
     */
    public boolean deleteCell(EdifCell cell, boolean force) {
        if (force || findCellInstancesOf(cell) == null)
            return deleteCellAux(cell);
        else
            return false;
    }

    /**
     * Return all leafCell, non primitive EdifCell objects within the library.
     * These leafCells are called black boxes.
     * 
     * @return A Collection of EdifCells contained within this library that are
     * leaf cells and aren't primitives, or black boxes
     * @see EdifLibraryManager#findBlackBoxes
     */
    public Collection<EdifCell> findBlackBoxes() {
        Collection<EdifCell> blackBoxes = new ArrayList<EdifCell>();
        for (EdifCell cell : _edifCells) {
            if (!cell.isLeafCell() || cell.isPrimitive()) {
                continue;
            }

            // Cell is a leaf. Add to list of black boxes
            blackBoxes.add(cell);
        }
        return blackBoxes;
    }

    /**
     * This function returns all EdifCellInstances that reference the given in
     * cell in this library. This will only search the <em>current</em>
     * library. If no CellCellInstances are found, this method will return null.
     * This method finds all instanced EdifCell objects (i.e. EdifCellInstances)
     * in the current library.
     * 
     * @param cell The cell to find cell instances of
     * @return A Collection of EdifCellInstance Objects that refer to the passed
     * in EdifCell
     * @see EdifCell#findCellInstancesOf
     */
    public Collection<EdifCellInstance> findCellInstancesOf(EdifCell cell) {
        Collection<EdifCellInstance> cellInstances = new ArrayList<EdifCellInstance>();
        if (cell != null && _edifCells != null)
            for (EdifCell currentCell : _edifCells) {

                Collection<EdifCellInstance> currentCellInstances = currentCell.findCellInstancesOf(cell);
                if (currentCellInstances != null)
                    cellInstances.addAll(currentCellInstances);
            }
        if (cellInstances.size() == 0)
            return null;
        return cellInstances;
    }

    /**
     * This function returns all EdifCellInstances that reference cells from the
     * passed-in library. This will only search the <em>current</em> library.
     * If no CellInstances are found, this method will return null. This method
     * finds all instanced EdifCell objects (i.e. EdifCellInstances) in the
     * current library.
     * 
     * @param lib The library that contains EdifCells that this method will find
     * the EdifCellInstances of
     * @return A Collection of EdifCellInstaces that refer to EdifCells in the
     * passed-in library
     * @see EdifCell#findCellInstancesOf
     */
    public Collection<EdifCellInstance> findCellInstancesOf(EdifLibrary lib) {
        Collection<EdifCellInstance> cellInstances = new ArrayList<EdifCellInstance>();
        for (EdifCell cell : lib.getCells()) {

            Collection<EdifCellInstance> libraryCellInstanceList = findCellInstancesOf(cell);
            if (libraryCellInstanceList != null)
                for (EdifCellInstance libInstance : libraryCellInstanceList) {
                    if (!cellInstances.contains(libInstance))
                        cellInstances.add(libInstance);
                }
        }
        if (cellInstances.size() == 0)
            return null;
        return cellInstances;
    }

    /**
     * This function returns a list of EdifCell objects instanced by the given
     * EdifCell object that are contained in the current library. This is very
     * similar to the {@link EdifCell#getSubCellList} method with the exception
     * that the returned subcells are limited to those that lie within this
     * EdifLibrary.
     * 
     * @param cell The cell that contains EdifCellInstances that refer to
     * EdifCells this method returns a Collection of
     * @return A Collection of EdifCells that are referenced by
     * EdifCellInstances contained within the passed in EdifCell
     */
    public Collection<EdifCell> findInstancedLibraryCells(EdifCell cell) {
        Collection<EdifCell> cellList = new ArrayList<EdifCell>();
        if (cell != null)
            for (EdifCellInstance eci : cell.getSubCellList()) {

                if (eci != null && _edifCells != null)

                    for (EdifCell currentCell : _edifCells) {

                        if (eci.getCellType() != null) {
                            if (eci.getCellType() == currentCell) {

                                cellList.add(eci.getCellType());
                                break;
                            }
                        }
                    }
            }
        if (cellList.size() == 0)
            return null;
        return cellList;
    }

    /**
     * This function returns a list of EdifCell objects instanced by EdifCells
     * of the passed-in EdifLibrary Object that are contained in the current
     * library. This is very similar to the {@link EdifCell#getSubCellList}
     * method with the exception that the returned subcells are limited to those
     * that lie within this EdifLibrary.
     * 
     * @param lib The library that contains EdifCells that contains
     * EdifCellInstances that refer to EdifCells this method returns a
     * Collection of
     * @return A Collection of EdifCells that are referenced by
     * EdifCellInstances contained within EdifCells contained within the passed
     * in EdifLibrary
     */
    public Collection<EdifCell> findInstancedLibraryCells(EdifLibrary lib) {
        Collection<EdifCell> cellList = new ArrayList<EdifCell>();
        for (EdifCell cell : lib.getCells()) {

            Collection<EdifCell> libraryCellList = findInstancedLibraryCells(cell);
            if (libraryCellList != null)
                for (EdifCell libCell : libraryCellList) {

                    if (!cellList.contains(libCell))
                        cellList.add(libCell);
                }
        }
        if (cellList.size() == 0)
            return null;
        return cellList;
    }

    /**
     * This function will return a list of EdifCell objects that are not
     * referenced anywhere within the current Library. These EdifCell objects
     * may be referenced from a different library.
     * 
     * @return A Collection of EdifCells that exist in this library and that
     * aren't referenced from within this library
     * @see EdifLibraryManager#findNonReferencedCells
     */
    public Collection<EdifCell> findNonReferencedCells() {
        Collection<EdifCell> nonRefCells = new ArrayList<EdifCell>();
        for (EdifCell cell : _edifCells) {
            if (findCellInstancesOf(cell) == null)
                nonRefCells.add(cell);
        }
        if (nonRefCells.size() == 0)
            return null;
        return nonRefCells;
    }

    /**
     * Return the EdifCell object in this library that is associated with the
     * given String name. If no cell with the given name exists in the library,
     * return null.
     * 
     * @param name The name of the cell to fetch from this library
     * @return An EdifCell object whose name equals the passed in String
     */
    public EdifCell getCell(String name) {
        for (int i = 0; i < _edifCells.size(); i++) {
            EdifCell c = _edifCells.get(i);
            if (NamedObjectCompare.equals(c, name))
                return c;
        }
        return null;
    }

    /**
     * Return the EdifCell Objects in this library.
     * 
     * @return A Collection object of the EdifCell Objects in this EdifLibrary
     */
    public Collection<EdifCell> getCells() {
        return _edifCells;
    }

    /**
     * @return a Collection of EdifCell objects that are instanced in this
     * library but defined in a different library.
     */
    public Collection<EdifCell> getExternalReferencedCells() {
        List<EdifCell> result = new ArrayList<EdifCell>();
        for (EdifCell cell : _edifCells) {
            Collection<EdifCell> cells = cell.getInstancedCellTypes();
            if (cells != null) {
                for (EdifCell c : cells)
                    if (!containsCell(c))
                        result.add(c);
            }
        }
        return result;
    }

    /**
     * @return a Collection of EdifLibrary objects that contain cells that are
     * instanced by this library.
     */
    public Collection<EdifLibrary> getExternalReferencedLibraries() {
        List<EdifLibrary> result = new ArrayList<EdifLibrary>();
        for (EdifCell c : getExternalReferencedCells()) {
            if (!result.contains(c.getLibrary()))
                result.add(c.getLibrary());
        }
        return result;
    }

    /**
     * Return this library's library manager.
     * 
     * @return The EdifLibraryManager Object of this EdifLibrary
     */
    public EdifLibraryManager getLibraryManager() {
        return _edifLibraryManager;
    }

    /**
     * @return a List of EdifCell objects in valid order according to the define
     * before use rule. The order is computed using a depth first search
     * topological sort.
     */
    public List<EdifCell> getValidCellOrder() {

        List<EdifCell> finishedCells = new ArrayList<EdifCell>();
        List<EdifCell> unvisitedCells = new ArrayList<EdifCell>(_edifCells);

        while (!unvisitedCells.isEmpty()) {
            EdifCell cell = unvisitedCells.get(0);
            visitCell(cell, finishedCells, unvisitedCells);
        }

        return finishedCells;
    }

    /**
     * Indicate whether this library was tagged with the
     * 
     * <pre>
     * external
     * </pre>
     * 
     * tag.
     * 
     * @return True if this is an external library
     */
    public boolean isExternal() {
        return _external;
    }

    /**
     * This method will examine each of the EdifCell objects in the library and
     * determine if <em>every</em> EdifCell in the library is a primitive (see
     * {@link EdifCell#isPrimitive}). If every EdifCell is a primitive, then
     * this method will return true. Otherwise, it will return false.
     * <p>
     * Note that this method does not look for leaf cells. Instead, it searches
     * for cells that are tagged as primitives. If all of the cells are leaf
     * cells but not all of the cells have been tagged as primitive, this method
     * will return false.
     * 
     * @return true if this library contains only primitive cells
     */
    public boolean isPrimitiveLibrary() {
        for (EdifCell cell : _edifCells) {
            if (!cell.isPrimitive())
                return false;
        }
        return true;
    }

    /**
     * Return a Collection of EdifCell objects contained by this library.
     * 
     * @return An Iterator Object of the EdifCells in this library
     */
    public Iterator<EdifCell> iterator() {
        return _edifCells.iterator();
    }

    /**
     * Return whether or not the given EdifCell's name will clash with another
     * cell's name in this library.
     * 
     * @param cell The EdifCell whose name will be checked against the names
     * contained within the name space of EdifCell Objects
     * @return true if the name of this EdifCell will clash with the name of a
     * cell already existing within the library
     */
    public boolean nameClash(EdifCell cell) {
        return _edifCells.nameClash(cell);
    }

    /**
     * Determine a unique name based on the given EdifNameable that will not
     * clash with any of the library's existing cells. If there is no clash, the
     * unique name will be the same as the original.
     * 
     * @param en the name to use as a basis for a unique name
     * @return an EdifNameable representing a unique name
     */
    public EdifNameable getUniqueEdifCellNameable(EdifNameable en) {
        return _edifCells.getUniqueNameable(en);
    }

    /**
     * Determine a unique name based on the given EdifCell's name that will not
     * clash with any of the library's existing cells. If there is no clash, the
     * unique name will be the same as the given cell's original name.
     * 
     * @param cell this cell's name will be used as a basis for the unique name
     * @return an EdifNameable representing a unique name
     */
    public EdifNameable getUniqueEdifCellNameable(EdifCell cell) {
        return _edifCells.getUniqueNameable(cell);
    }

    /**
     * Convert this object to EDIF format and write it to the passed-in
     * EdifPrintWriter Object.
     * 
     * @param epw The EdifPrinterWriter Object that the EDIF data will be
     * written to
     */
    public void toEdif(EdifPrintWriter epw) {

        if (isExternal())
            epw.printIndent("(external ");
        else
            epw.printIndent("(library ");
        getEdifNameable().toEdif(epw);
        epw.println();

        epw.incrIndent();

        epw.printlnIndent("(edifLevel 0)");
        epw.printlnIndent("(technology (numberDefinition ))");

        List<EdifCell> cell_order = getValidCellOrder();
        for (EdifCell c : cell_order) {
            epw.flush();
            c.toEdif(epw);
        }

        epw.decrIndent();
        epw.printlnIndent(")");

    }

    /**
     * Returns a String representation of this Object.
     * 
     * @return A String representing this EdifLibrary Objects name, and
     * contained EdifCell Objects
     */
    @Override
    public String toString() {
        return getEdifNameable().toString();
    }

    /**
     * Trim this EdifLibrary Object and contained Trimmable Objects down to
     * size.
     */
    public void trimToSize() {
        _edifCells.trimToSize();
        for (int i = 0; i < _edifCells.size(); i++)
            _edifCells.get(i).trimToSize();
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private methods                   ////

    /**
     * Delete a cell from the library, and return whether or not the delete was
     * successful.
     * 
     * @param cell The cell to be deleted.
     * @return true if the cell was deleted. false if the cell is not in the
     * library or cell is null
     * @see EdifLibrary#deleteCell(EdifCell,boolean)
     */
    protected boolean deleteCellAux(EdifCell cell) {
        if (cell == null)
            return false;
        if (!_edifCells.contains(cell))
            return false;

        _edifCells.remove(cell);
        return true;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         package private methods           ////

    /**
     * Set this library's library manager to libMan.
     * 
     * @param libMan The library manager of this library
     */
    void setLibraryManager(EdifLibraryManager libMan) {
        _edifLibraryManager = libMan;
    }

    ///////////////////////////////////////////////////////////////////
    // Private methods

    /**
     * Visit a cell during the topological sort for computing valid cell order.
     * 
     * @param cell The EdifCell to visit
     * @param finishedCells a List of finished cells
     * @param unvisitedCells a List of unvisited cells
     */
    private void visitCell(EdifCell cell, List<EdifCell> finishedCells, List<EdifCell> unvisitedCells) {
        unvisitedCells.remove(cell);
        Collection<EdifCell> instancedTypes = cell.getInstancedCellTypes();
        if (instancedTypes != null) {
            for (EdifCell c : instancedTypes) {
                if (containsCell(c)) {
                    if (!unvisitedCells.contains(c) && !finishedCells.contains(c))
                        throw new EdifRuntimeException("circular cell dependency");
                    if (unvisitedCells.contains(c))
                        visitCell(c, finishedCells, unvisitedCells);
                }
            }
        }
        finishedCells.add(cell);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /**
     * The list of cells defined within the library.
     */
    private EdifNameSpaceList<EdifCell> _edifCells = new EdifNameSpaceList<EdifCell>(
            BasicEdifNameClashPolicy.singleAndNewClashPolicy);

    /**
     * This is a reference to the parent EdifLibraryManager object. This manager
     * manages other related libraries in the library structure.
     */
    private EdifLibraryManager _edifLibraryManager = null;

    /**
     * <b><em>External</em> Keyword</b>
     * <p>
     * The external construct declares a library to which reference is made, but
     * which is not actually present within the current EDIF file. This may
     * include any library which has been exchanged by means outside the current
     * EDIF file.
     * <p>
     * The structure and semantics of external are parallel to those of library
     * except that external is known to be incomplete; external libraries that
     * are declared should only contain minimal information. Any information
     * present, such as status, must agree with the information available in the
     * read's library of the same name. External represents one way in which
     * reference can be made to external information from within an EDIF file.
     * <p>
     * External should occur within a file only with prior consent of the
     * intended receiving party, since it must be assumed that the missing
     * library has already been transmitted. This statement provides an explicit
     * means of declaring libraries, providing a simple check for data
     * completeness. It also provides a mechanism for renaming an external
     * library, since the rename construct may be used here. Cells in external
     * may not have contents sections. Any object referenced later should be
     * defined here, including all names and interface declarations.
     * <p>
     * 
     * <pre>
     *      edif ::=
     *      '(''edif' edifFileNameDef
     *      edifVersion
     *      edifLevel
     *      keywordMap
     *      { &lt;status &gt; | external | library | design |
     *      comment | userdata }
     *      ')'
     *      external ::=
     *      '(''external' libraryNameDef
     *      edifLevel
     *      technology
     *      {&lt; status &gt; | cell | comment | userData }
     *      ')'a
     * </pre>
     * 
     * So, this means that external is parallel to library, all the cells in the
     * external definition have only name and ports, the exact definition of
     * these cells should be in other EDIF file, which has a library defined
     * with the same name of this external name, or the accepting system has
     * already had a library with the same name of this external name.
     */
    private boolean _external = false;

}
