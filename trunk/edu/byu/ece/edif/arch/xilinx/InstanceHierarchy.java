/*
 * Created on Jun 30, 2005
 * 
 */
/*
 * 
 * 
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
package edu.byu.ece.edif.arch.xilinx;

import java.util.ArrayList;
import java.util.Iterator;


import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;

/**
 * This class represents an EdifCellInstance object within a hierarchy
 * framework. This is a tree data structure with a reference to the parent
 * branch (if not the root) and an array of the children representing each child
 * branch. The top node in this tree is the top-level of the hierarchy. An
 * instance in the hierarchy includes the following: - A reference to a given
 * EdifCellInstance (self) - A reference to a parent instance (null if it is the
 * root) - An array of NetHierarchy objects representing nets at this level of
 * hierarchy - An array of InstanceHierarchy children representing children in
 * the hierarchy. TODO: Move this to byucc.edif.hierarchy Rename to
 * EDIFInstanceHierarchy
 */
public class InstanceHierarchy {

    /**
     * Create a top-level hierarchical instance.
     * 
     * @param self
     */
    public InstanceHierarchy(EdifCellInstance self) {
        this(null, self);
    }

    /**
     * Creates a new hierarchical element.
     * 
     * @param parent Parent EdifCellInstance object in the hierarchy
     * @param self The EdifCellInstance represented by this hierarchical object.
     * TODO: provide option for ignoring nets (if interested only in instance
     * hierarchy).
     */
    public InstanceHierarchy(InstanceHierarchy parent, EdifCellInstance self) {
        this(parent, self, true);
    }

    public InstanceHierarchy(InstanceHierarchy parent, EdifCellInstance self, boolean record_nets) {
        _parent = parent;
        _self = self;

        EdifCell cell = this.getInstanceCellType();

        // Record nets
        if (cell.getNetList() != null && record_nets) {
            _nets = new NetHierarchy[cell.getNetList().size()];
            int j = 0;
            for (Iterator i = cell.netListIterator(); i.hasNext();) {
                EdifNet net = (EdifNet) i.next();
                NetHierarchy nh = new NetHierarchy(this, net);
                _nets[j++] = nh;
            }
        }

        // Create children
        if (cell.getSubCellList().size() != 0) {
            _children = new InstanceHierarchy[cell.getSubCellList().size()];
            int j = 0;
            if (cell.getSubCellList() != null)
                for (Iterator i = cell.getSubCellList().iterator(); i.hasNext();) {
                    EdifCellInstance eci = (EdifCellInstance) i.next();
                    InstanceHierarchy ih = new InstanceHierarchy(this, eci);
                    _children[j++] = ih;
                }
        }
    }

    // ? not used
    public static InstanceHierarchy findInstance(InstanceHierarchy hier, EdifCellInstance eci) {
        InstanceHierarchy retval = null;

        InstanceHierarchy[] children = hier.getChildren();
        if (hier.getSelf() == eci) {
            retval = hier;
        } else {
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    if (children[i].getSelf() == eci) {
                        retval = children[i];
                        break;
                    }
                    retval = findInstance(children[i], eci);
                    if (retval != null)
                        break;
                }
            }
        }

        return retval;
    }

    public InstanceHierarchy getHierarchyNode(ArrayList cells) {
        InstanceHierarchy retval = null;
        InstanceHierarchy self = this;
        boolean found = false;

        for (Iterator it = cells.iterator(); it.hasNext();) {
            EdifCellInstance cell = (EdifCellInstance) it.next();
            InstanceHierarchy[] childs = self.getChildren();
            found = false;
            if (self.getSelf() == cell) {
                found = true;
                continue;
            }
            for (int i = 0; i < childs.length; i++) {
                if (childs[i].getSelf() == cell) {
                    self = childs[i];
                    found = true;
                    break;
                }
            }
        }
        if (found) {
            retval = self;
        }

        return retval;
    }

    public InstanceHierarchy[] getChildren() {
        return _children;
    }

    /**
     * Determine EdifCell type of the EdifCellInstance at this level in the
     * hierarchy.
     * 
     * @return
     */
    public EdifCell getInstanceCellType() {
        return _self.getCellType();
    }

    public NetHierarchy[] getNets() {
        return _nets;
    }

    /**
     * @return The parent InstanceHierarchy of the object.
     */
    public InstanceHierarchy getParent() {
        return _parent;
    }

    public EdifCellInstance getSelf() {
        return _self;
    }

    /**
     * Returns an iterator of InstanceHierachy objects going "up" the hierarchy
     * through parents.
     * 
     * @return
     */
    public Iterator getUpIterator() {
        return new InstanceHierarchyIterator(this);
    }

    public boolean isTop() {
        return (_parent == null);
    }

    public boolean isTop(boolean includeTop) {
        boolean retval = isTop();

        if (!includeTop && !retval) {
            retval = getParent().isTop();
        }
        return retval;
    }

    /**
     * Create a unique name for this instance that includes the hierarchy
     * information. This method uses the "oldname" (if it exists) and separates
     * the levels of hierarchy by the "/" character.
     */
    public String getInstanceName() {
        return getInstanceName(true, "/");
    }

    public String getInstanceName(boolean printTop) {
        return getInstanceName(printTop, "/");
    }

    public String getInstanceName(boolean printTop, String hierarchy_separation_string) {
        StringBuffer sb = new StringBuffer();

        for (InstanceHierarchy hier = this; hier != null; hier = hier.getParent()) {
            String insert = hier.getSelf().getOldName();
            if (hier != this)
                insert += hierarchy_separation_string;
            if (hier.isTop() && printTop)
                sb.insert(0, insert);
            else if (!hier.isTop())
                sb.insert(0, insert);
        }
        return sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(getInstanceName(true));

        return sb.toString();
    }

    /**
     * The parent instance of this instance. This will be null if the instance
     * is the top level.
     */
    protected InstanceHierarchy _parent;

    /**
     * The corresponding EdifCellInstance object of this instance.
     */
    protected EdifCellInstance _self;

    /**
     * A collection of NetHierarchy objects that correspond to this instance.
     */
    protected NetHierarchy[] _nets;

    protected InstanceHierarchy[] _children;

}

/**
 * This is an iterator class for the InstanceHierarchy object. This iterator
 * will start at the bottom of the hierarchy and move up towards the top.
 */
class InstanceHierarchyIterator implements Iterator {

    public InstanceHierarchyIterator(InstanceHierarchy bottom) {
        _current = bottom;
    }

    public boolean hasNext() {
        if (_current == null)
            return false;
        return true;
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove not supported");
    }

    public Object next() {
        InstanceHierarchy next = _current;
        _current = _current.getParent();
        return next;
    }

    InstanceHierarchy _current;
}
