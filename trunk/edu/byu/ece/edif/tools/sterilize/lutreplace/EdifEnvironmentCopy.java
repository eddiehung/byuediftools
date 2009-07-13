/*
 * Copies an EdifEnvironment mapping objects in the old to those in the new.
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
package edu.byu.ece.edif.tools.sterilize.lutreplace;

import java.util.HashMap;
import java.util.LinkedHashMap;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;

/**
 * This class copies an EdifEnvironment and maintains an association between the
 * objects in the old EdifEnvironment and the objects in the new
 * EdifEnvironment.
 * <p>
 * This class was intended to be extended by overriding some of the methods.
 * Custom "copy" classes can be created that actually copy and modify.
 * 
 * @author Mike Wirthlin
 */
public class EdifEnvironmentCopy {

    /**
     * This constructor will create a copy of the EdifEnvironmentCopy object.
     * 
     * @param env Environment to copy
     */
    public EdifEnvironmentCopy(EdifEnvironment env) throws EdifNameConflictException {
        _origEnv = env;
        // createEdifEnvironment();
    }

    /**
     * Initiates the copying of the old environment and the creation of a new
     * environment. This will perform a "deep" copy and copy all elements needed
     * for the top-level design.
     * <p>
     * The name of the new environment will be assigned to the name of the old
     * environment.
     * 
     * @return
     * @throws EdifNameConflictException
     */
    public EdifEnvironment createEdifEnvironment() throws EdifNameConflictException {
        return createEdifEnvironment(_origEnv.getEdifNameable());
    }

    /**
     * Initiates the copying of the old environment and the creation of a new
     * environment. This will perform a "deep" copy and copy all elements needed
     * for the top-level design.
     * <p>
     * The name of the new environment is determined by the name parameter.
     * <p>
     * This method performs three steps: - Create new environment - Copy the
     * properties of the environment - Copy Date, Author, Program, and Version
     * of Environment - Create the top-level design and all EdifCells needed for
     * design (recursive call)
     * 
     * @return
     * @throws EdifNameConflictException
     */
    public EdifEnvironment createEdifEnvironment(EdifNameable name) throws EdifNameConflictException {
        _newEnv = new EdifEnvironment(name);
        // Copy all of the properties
        _newEnv.copyProperties(_origEnv);
        setEdifEnvironmentDateAuthorProgramVersion();
        createTopDesign();
        return _newEnv;
    }
    
    /**
     * 
     * @return The copied EdifEnvironment
     */
    public EdifEnvironment getNewEnvironment() {
    	return _newEnv;
    }

    /**
     * Copy the date, author, program, and version of the old environment into
     * the new environment. These parameters are not immutable so they can be
     * changed at a later time. This method can also be extended to change the
     * default behavior.
     */
    protected void setEdifEnvironmentDateAuthorProgramVersion() {
        // Copy all of the date, author, etc. This can always be changed at a
        // later time.
        _newEnv.setDate(_origEnv.getDate());
        _newEnv.setAuthor(_origEnv.getAuthor());
        _newEnv.setProgram(_origEnv.getProgram());
        _newEnv.setVersion(_origEnv.getVersion());
    }

    /**
     * This method creates the top-level design for the new environment. It
     * performs the following steps:
     * <ol>
     * <li> Copies the top-level EdifCell that represents the top-level design
     * (it does this recursively and will thus add all the libraries, cells,
     * etc. for this top-level Cell)
     * <li> Creates new top-level instance
     * <li>Creates new EdifDesign
     * <li> Copies EdifDesign properties
     * </ol>
     * 
     * @throws EdifNameConflictException
     */
    protected void createTopDesign() throws EdifNameConflictException {
        EdifDesign oldDesign = _origEnv.getTopDesign();
        EdifCellInstance oldTopInstance = oldDesign.getTopCellInstance();
        EdifCell oldTopCell = oldTopInstance.getCellType();
        // Call to generate top-cell. This is the recursive call that will
        // add all Cells and sub-cells, etc.
        createTopCell(oldTopCell);
    }

    /**
     * Create the top-level EdifCell (recursive)
     * 
     * @param oldTopCell
     * @return
     * @throws EdifNameConflictException
     */
    protected EdifCell createTopCell(EdifCell oldTopCell) throws EdifNameConflictException {
        return copyEdifCell(oldTopCell);
    }

    /**
     * Copy the given EdifCell from the original Environment and add it to the
     * new environment. Create all the libraries and children cells necessary to
     * build the EdifCell.
     * <p>
     * This method performs the following steps:
     * <ol>
     * <li> Create new cell and place in map
     * <li> Add ports to cell
     * <li> add instances to cell (including sub-cells through recursion)
     * <li> add nets and wire them up.
     * </ol>
     * 
     * @param origCell
     */
    public EdifCell copyEdifCell(EdifCell origCell, EdifLibrary destLibrary, EdifNameable name)
            throws EdifNameConflictException {
        EdifCell newCell = new EdifCell(destLibrary, name);
        _cellMap.put(origCell, newCell);
        // copy properties
        newCell.copyProperties(origCell);
        // copy primitive status
        if (origCell.isPrimitive())
            newCell.setPrimitive();
        if (origCell == _origEnv.getTopCell()) {
            EdifDesign oldDesign = _origEnv.getTopDesign();
            EdifCellInstance newTopInstance = new EdifCellInstance(newCell.getEdifNameable(), null, newCell);
            EdifDesign newDesign = new EdifDesign(newCell.getEdifNameable());
            newDesign.setTopCellInstance(newTopInstance);
            // copy design properties
            newDesign.copyProperties(oldDesign);
            _newEnv.setTopDesign(newDesign);
        }
                
        addEdifPorts(origCell, newCell);
        addChildEdifCellInstances(origCell, newCell);
        addNets(origCell, newCell);
        return newCell;
    }

    /**
     * Uses name of original EdifCell for name of new cell.
     */
    protected EdifCell copyEdifCell(EdifCell origCell, EdifLibrary destLibrary) throws EdifNameConflictException {
        return copyEdifCell(origCell, destLibrary, origCell.getEdifNameable());
    }

    /**
     * Finds associated library in new environment and uses it as well as the
     * old name.
     */
    protected EdifCell copyEdifCell(EdifCell origCell) throws EdifNameConflictException {
        // Determine destination library
        EdifLibrary origLib = origCell.getLibrary();
        EdifLibrary destLib = _libMap.get(origLib);
        if (destLib == null) {
            destLib = copyEdifLibrary(origLib);
        }
        return copyEdifCell(origCell, destLib);
    }

    /**
     * Copies a new library based on the old library.
     * <p>
     * This is a "shallow" copy that does not copy the cells. Instead, it
     * creates an empty library. EdifCells are added to the library during the
     * copyEdifCell methods.
     * <ol>
     * <li> Create new library
     * <li> Add library to environment
     * <li> copy properties
     * <li> Add to the map
     * </ol>
     * 
     * @param origLib
     * @param libName
     * @return
     * @throws EdifNameConflictException
     */
    protected EdifLibrary copyEdifLibrary(EdifLibrary origLib, EdifNameable libName) throws EdifNameConflictException {

        EdifLibrary newLib = new EdifLibrary(_newEnv.getLibraryManager(), libName, origLib.isExternal());
        _newEnv.addLibrary(newLib);
        newLib.copyProperties(origLib);
        _libMap.put(origLib, newLib);
        return newLib;
    }

    /**
     * Create library using same name as in old library.
     * 
     * @param origLib
     * @return
     * @throws EdifNameConflictException
     */
    protected EdifLibrary copyEdifLibrary(EdifLibrary origLib) throws EdifNameConflictException {
        return copyEdifLibrary(origLib, origLib.getEdifNameable());
    }

    protected void addEdifPorts(EdifCell origCell, EdifCell newCell) throws EdifNameConflictException {
        // copy cell interface
        for (EdifPort oldPort : origCell.getPortList()) {
            EdifPort newPort = newCell.addPort(oldPort.getEdifNameable(), oldPort.getWidth(), oldPort.getDirection());
            _portMap.put(oldPort, newPort);
            // copy port properties
            newPort.copyProperties(oldPort);
        }
    }

    protected void addChildEdifCellInstances(EdifCell origCell, EdifCell newCell) throws EdifNameConflictException {

        // iterate over all children
        if (origCell.getInstancedCellTypes() != null)
            for (EdifCellInstance oldInstance : origCell.getCellInstanceList()) {
                addChildEdifCellInstance(origCell, newCell, oldInstance);
            }
    }

    protected void addChildEdifCellInstance(EdifCell origCell, EdifCell newCell, EdifCellInstance oldChildInstance)
            throws EdifNameConflictException {

        EdifCell oldChildType = oldChildInstance.getCellType();

        // See if the EdifCell of the instance is in the library. Add if
        // necessary.
        if (!_cellMap.containsKey(oldChildType))
            // recursive call to add EdifCell of child
            copyEdifCell(oldChildType);

        // get the new copy of the type (it should be in there now)
        EdifCell newChildType = _cellMap.get(oldChildType);

        // Create instance
        EdifCellInstance newInstance = new EdifCellInstance(oldChildInstance.getEdifNameable(), newCell, newChildType);

        // copy instance properties
        newInstance.copyProperties(oldChildInstance);
        _instanceMap.put(oldChildInstance, newInstance);

        // TODO: This is odd: I don't think you should have to construct the
        // instance
        // and then add it - it should be added automatically.
        newCell.addSubCell(newInstance);

    }

    protected void addNets(EdifCell origCell, EdifCell newCell) throws EdifNameConflictException {
        // copy nets
        if (origCell.getNetList() != null)
            for (EdifNet oldNet : origCell.getNetList()) {
                addNet(origCell, newCell, oldNet);
            }
    }

    protected EdifNet addNet(EdifCell origCell, EdifCell newCell, EdifNet oldNet) throws EdifNameConflictException {
        // TODO: again, this is odd that you create a net and don't
        // automatically add it.
        EdifNet newNet = new EdifNet(oldNet.getEdifNameable(), newCell);
        _netMap.put(oldNet, newNet);

        // iterate portRefs
        for (EdifPortRef oldRef : oldNet.getConnectedPortRefs()) {
            addEdifPortRef(newNet, oldRef);
        }

        // copy net properties
        newNet.copyProperties(oldNet);
        newCell.addNet(newNet);
        return newNet;
    }

    protected void addEdifPortRef(EdifNet newNet, EdifPortRef oldRef) {
        EdifSingleBitPort oldSbp = oldRef.getSingleBitPort();
        EdifPort oldPort = oldSbp.getParent();
        EdifPort newPort = _portMap.get(oldPort);        
        EdifSingleBitPort newSbp = newPort.getSingleBitPort(oldSbp.bitPosition());
        EdifCellInstance newEci = null;
        if (oldRef.getCellInstance() != null)
            newEci = _instanceMap.get(oldRef.getCellInstance());
        EdifPortRef newEpr = new EdifPortRef(newNet, newSbp, newEci);
        newNet.addPortConnection(newEpr);
    }

    protected HashMap<EdifCellInstance, EdifCellInstance> _instanceMap = new LinkedHashMap<EdifCellInstance, EdifCellInstance>();

    protected HashMap<EdifPort, EdifPort> _portMap = new LinkedHashMap<EdifPort, EdifPort>();

    protected HashMap<EdifNet, EdifNet> _netMap = new LinkedHashMap<EdifNet, EdifNet>();

    protected HashMap<EdifCell, EdifCell> _cellMap = new LinkedHashMap<EdifCell, EdifCell>();

    protected HashMap<EdifLibrary, EdifLibrary> _libMap = new LinkedHashMap<EdifLibrary, EdifLibrary>();

    protected EdifEnvironment _newEnv;

    protected EdifEnvironment _origEnv;

}
