/*
 * Wrapper to share IOB Feedback analysis information among JEdif executables.
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
package edu.byu.ece.edif.util.jsap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.tools.replicate.nmr.EdifPortRefStringReference;
import edu.byu.ece.edif.util.graph.EdifPortRefEdge;

/*
 * Provides a serializable wrapper object to share necessary information from
 * IOB Feedback analysis among the JEdif executables
 */
public class SharedIOBAnalysis implements Serializable {

    public SharedIOBAnalysis(Collection<EdifPortRefEdge> edgesToRemove, boolean remIOB) {
        _eprStringRef = new ArrayList<EdifPortRefStringReference[]>();
        for (EdifPortRefEdge edge : edgesToRemove) {
            EdifPortRefStringReference[] pair = new EdifPortRefStringReference[2];
            pair[SOURCE] = new EdifPortRefStringReference((EdifPortRef) edge.getSourceEPR());
            pair[SINK] = new EdifPortRefStringReference((EdifPortRef) edge.getSinkEPR());
            _eprStringRef.add(pair);
        }
        _removeIOBfeedback = remIOB;
    }

    public boolean removeIOBfeedback() {
        return _removeIOBfeedback;
    }

    public Collection<EdifPortRef[]> getIOBFeedbackPortRefs(EdifCell cell) {
        List<EdifPortRef[]> eprs = new ArrayList<EdifPortRef[]>();
        for (EdifPortRefStringReference[] eprString : _eprStringRef) {
            EdifPortRef[] pair = new EdifPortRef[2];
            pair[SOURCE] = eprString[SOURCE].getEPRFromReference(cell);
            pair[SINK] = eprString[SINK].getEPRFromReference(cell);
            eprs.add(pair);
        }
        return eprs;
    }

    public static final long serialVersionUID = 2L;

    private List<EdifPortRefStringReference[]> _eprStringRef;

    private boolean _removeIOBfeedback;

    // constants
    public final int SOURCE = 0;

    public final int SINK = 1;

}
