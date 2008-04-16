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
package edu.byu.ece.edif.tools.replicate.nmr;

import java.io.Serializable;
import java.util.Collection;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;

public class EdifPortRefStringReference implements Serializable {
    public static final long serialVersionUID = 42L;

    public EdifPortRefStringReference(EdifPortRef ref) {
        if (ref.getCellInstance() != null) {
            _instanceName = new EdifNameableStringReference(ref.getCellInstance());
        }
        _netName = new EdifNameableStringReference(ref.getNet());
        _portName = new EdifNameableStringReference(ref.getPort());
        _bitNumber = ref.getBusMember();

    }

    public String toString() {
        return ("\n" + _instanceName + " " + _netName + " " + _portName + "[" + _bitNumber + "] ");
    }

    public EdifPortRefStringReference(EdifNameableStringReference eciref, EdifNameableStringReference netref,
            EdifNameableStringReference portref, int busMember) {
        _instanceName = eciref;
        _netName = netref;
        _portName = portref;
        _bitNumber = busMember;
    }

    public EdifPortRef getEPRFromReference(EdifCell cell) {
        EdifNet net = _netName.getEdifNet(cell);
        if (net == null)
            return null;

        boolean b1 = false, b3 = false, b4 = false;
        Collection<EdifPortRef> eprc = net.getConnectedPortRefs();
        EdifPort ep = null;
        for (EdifPortRef epr : eprc) {
            if (_instanceName == null && epr.getCellInstance() == null) {
                b1 = true;
            } else if (_instanceName != null && epr.getCellInstance() != null) {
                EdifCellInstance eci1 = _instanceName.getEdifCellInstance(cell);
                EdifCellInstance eci2 = epr.getCellInstance();
                b1 = eci1.equals(eci2);
            } else
                b1 = false;

            b3 = epr.getBusMember() == _bitNumber;
            b4 = epr.getNet().equalsProperties(net);

            if (b1 && b3 && b4)
                return epr;
        }
        return null;
    }

    protected EdifNameableStringReference _instanceName;

    protected EdifNameableStringReference _netName;

    protected EdifNameableStringReference _portName;

    protected int _bitNumber;

}
