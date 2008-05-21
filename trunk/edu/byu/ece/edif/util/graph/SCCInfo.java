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
package edu.byu.ece.edif.util.graph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCellInstance;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.graph.dfs.DepthFirstTree;
import edu.byu.ece.graph.dfs.SCCDepthFirstSearch;

/**
 * This class holds the information about the Strongly Connected Components in
 * an EdifCell in String format. The SCCs are in an ordered List in topological
 * order. Each SCC is a Collection of Strings which is unordered. This class is
 * meant to be serialized compactly and thus contains no backward reference to
 * the associated EdifCell or SCC objects.
 */
public class SCCInfo implements Serializable {

    public SCCInfo(SCCDepthFirstSearch sccDFS) {
        _SCCs = new ArrayList(sccDFS.getTrees().size());

        for (DepthFirstTree scc : sccDFS.getTopologicallySortedTreeList()) {
            Collection instanceStrings = new LinkedHashSet();
            for (Object node : scc.getNodes()) {
                if (node instanceof EdifCellInstance) {
                    String instanceString = ((EdifCellInstance) node).getOldName();
                    instanceStrings.add(instanceString);
                } else if (node instanceof EdifCellInstanceCollection) {
                    for (EdifCellInstance eci : ((EdifCellInstanceCollection) node)) {
                        String instanceString = ((EdifCellInstance) eci).getOldName();
                        instanceStrings.add(instanceString);
                    }
                } else if (node instanceof EdifPortRef) {
                    String instanceString = ((EdifPortRef) node).getCellInstance().getOldName();
                    instanceStrings.add(instanceString);
                } else if (node instanceof EdifSingleBitPort) {
                    // Top-level port. Do nothing
                } else {
                    // Unknown node type
                    throw new EdifRuntimeException("Unknown node type in graph");
                }
            }
            _SCCs.add(instanceStrings);
        }
    }

    /**
     * Converts this SCCInfo object from a List of Collections of Strings to a
     * List of Collections of EdifCellInstances. Each Collection in the List is
     * the set of Instances in a single SCC.
     * 
     * @param The FlattenedEdifCell this SCCInfo object is based on
     * @return
     */
    public List<Collection<EdifCellInstance>> convertToInstances(FlattenedEdifCell flatCell) {
        List instanceSCCs = new ArrayList<Collection<EdifCellInstance>>(_SCCs.size());

        for (Collection<String> scc : _SCCs) {
            Collection instanceSCC = new LinkedHashSet();
            for (String eciString : scc) {
                FlattenedEdifCellInstance flatECI = flatCell.getFlatInstance(eciString);
                if (flatECI == null)
                    System.out.println("ERROR: corresponding cell not found for: " + eciString);
                else
                    instanceSCC.add(flatECI);
            }
            instanceSCCs.add(instanceSCC);
        }

        return instanceSCCs;
    }

    public void print(PrintStream out) {
        out.println(_SCCs.size() + " trees");
        int j = 1;
        for (Collection<String> scc : _SCCs) {
            out.println("Tree " + j++);
            for (String eci : scc) {
                out.println("\t" + eci);
            }
        }
    }

    public void toFile(String filename) {
        // Serialize SCC Info object
        FileOutputStream fos = null;
        ObjectOutputStream out_object = null;
        try {
            fos = new FileOutputStream(filename);
            out_object = new ObjectOutputStream(fos);
            out_object.writeObject(this);
            out_object.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // The name of the EdifCell object this SCCInfo object corresponds to
    //protected String _cell;
    // An ordered List of SCCs, each SCC being an unordered Collection of Strings
    protected List<Collection<String>> _SCCs;
}
