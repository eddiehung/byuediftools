/*
 * Container class with an EdifNameable object and a PropertyList object.
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

/////////////////////////////////////////////////////////////////////////
////NamedPropertyObject
/**
 * Represents a container class that contains an {@link EdifNameable} object (to
 * define the name of this object) as well as a {@link PropertyList} object to
 * store all properties of the object. This class is used as a base class for
 * many of the Edif objects used in this package that require a name as well as
 * properties.
 * <p>
 * To save memory, this class will not create any PropertyList object unless
 * properties are added to the object.
 * 
 * @version $Id:NamedPropertyObject.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public abstract class NamedPropertyObject implements Properties, Serializable {

    /**
     * Construct a NamedPropertyObject with the specified name
     * 
     * @param name The name of this new NamedPropertyObject
     * @throws InvalidEdifNameException
     */
    public NamedPropertyObject(String name) throws InvalidEdifNameException {
        //_name = new NamedObject(name);

        //        String tmp = NamedObject.createValidEdifString(name);
        //        if (tmp != name)
        //            _name = new RenamedObject(tmp, name);
        //        else
        //            _name = new NamedObject(name);

        // TODO: we need to throw an invalid name exception here
        //_name = RenamedObject.createValidEdifNameable(name);

        _name = new NamedObject(name);

    }

    /**
     * Construct a NamedPropertyObject with the specified name object as its
     * value
     * 
     * @param name The object containing name information for this new
     * NamedPropertyObject
     */
    public NamedPropertyObject(EdifNameable name) {
        _name = name;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Add a single property to this object.
     * 
     * @param name The name of the property object
     * @param value The instance specific value of this property
     */
    public void addProperty(String name, EdifTypedValue value) {
        addProperty(new Property(name, value));
    }

    /**
     * Add a single property to this object.
     * 
     * @param p The Property Object to add to this objects list of properties.
     */
    public void addProperty(Property p) {
        if (_pl == null)
            _pl = new PropertyList();
        _pl.addProperty(p);
    }

    /**
     * Add a list of properties to the object. This will append the current list
     * of properties with the list of properties that are passed in as an
     * argument.
     * 
     * @param pList The list of properties to add to this object.
     */
    public void addPropertyList(PropertyList pList) {
        if (_pl == null)
            _pl = new PropertyList();
        _pl.addPropertyList(pList);
    }

    /**
     * Copy properties of given properties object into this object.
     */
    public void copyProperties(Properties props) {
        // Copy properties of NamedPropertyObject
        PropertyList pl = props.getPropertyList();
        if (pl != null) {
            for (Property p : pl.values()) {
                addProperty(p);
            }
        }
    }

    public boolean equals(NamedPropertyObject cmp) {
        if (cmp == null)
            return false;
        return (_name.equals(cmp.getEdifNameable()) && equalsProperties(cmp));
    }

    /**
     * Return True if the passed-in NamedPropertyObject's PropertyList matches
     * the PropertyList Object contained in this Object.
     * 
     * @param cmp The Object whose PropertyList Object will be matched up with
     * the one in this Object
     * @return True if this Object's PropertyList matches with the passed-in
     * one's PropertyList
     */
    public boolean equalsProperties(NamedPropertyObject cmp) {
        PropertyList pl1 = getPropertyList();
        PropertyList pl2 = cmp.getPropertyList();
        if (pl1 == null && pl2 == null)
            return true;
        if ((pl1 == null && pl2 != null) || (pl1 != null && pl2 == null) || pl1.size() != pl2.size())
            return false;
        return pl1.equals(pl2);
    }

    /**
     * Get the EdifNameable object that names this object.
     * 
     * @return An {@link EdifNameable} Object that contains the name information
     * of this object
     */
    public EdifNameable getEdifNameable() {
        return _name;
    }

    /**
     * Get the Edif name of this object.
     * 
     * @return A String Object specifying the name of this object
     */
    public String getName() {
        return _name.getName();
    }

    /**
     * Return the "old name" associated with this Named object. If the object
     * does not have an old name (i.e. it is not of type RenamedObject), it will
     * return the Edif name.
     * 
     * @return EDIF old name
     */
    public String getOldName() {
        return _name.getOldName();
    }

    /**
     * Get a property from this object.
     * 
     * @param str The 'key' of the property
     * @return A Property Object that corresponds to the passed-in String object
     */
    public Property getProperty(String str) {
        if (_pl == null)
            return null;
        return _pl.getProperty(str);
    }

    /**
     * Return the complete PropertyList of this object.
     * <p>
     * TODO: Should we return the list or a copy of the list? Returning the
     * actual list seems dangerous as the list can be easily modified.
     * 
     * @return A {@link PropertyList} Object containing all the properties of
     * this object.
     */
    public PropertyList getPropertyList() {
        return _pl;
    }

    //	public void addComment(EdifComment comment) {
    //        if (_commentList == null)
    //            _commentList = new ArrayList(1);
    //        _commentList.add(comment);
    //    }
    //
    //    public void addComment(String comment) {
    //        EdifComment c = new EdifComment(comment);
    //        addComment(c);
    //    }
    //
    //    public List getComments() {
    //        return _commentList;
    //    }

    ///////////////////////////////////////////////////////////////////
    ////                 package private methods                   ////

    /**
     * Rename this object.
     * 
     * @param newName The new name for this object
     * @param oldName The new name for this object
     */
    void rename(String newName, String oldName) {
        try {
            _name = new RenamedObject(newName, oldName);
        } catch (InvalidEdifNameException e) {
            // Shouldn't get here - renames create a valid name first
            throw new RuntimeException(e);
        }
    }

    /**
     * Rename this object.
     * 
     * @param newName The new name for this object
     */
    void rename(String newName) {
        try {
            _name = new NamedObject(newName);
        } catch (InvalidEdifNameException e) {
            // Shouldn't get here - renames create a valid name first
            throw new RuntimeException(e);
        }
    }

    /**
     * Rename this object
     * 
     * @param newName A NamedObject representing the new name for this object
     */
    void rename(EdifNameable newName) {
        _name = newName;
    }

    ///////////////////////////////////////////////////////////////////
    ////                    private variables                      ////

    /**
     * The EdifNameable object that names this object.
     */
    private EdifNameable _name;

    /**
     * The PropertyList object. This is kept null unless at least one property
     * is added to the object.
     */
    private PropertyList _pl = null;

    //private List _commentList = null;

}
