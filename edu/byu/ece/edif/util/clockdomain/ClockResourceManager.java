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
package edu.byu.ece.edif.util.clockdomain;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.byu.ece.edif.arch.xilinx.XilinxClockingArchitecture;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifException;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.jsap.ClockDomainCommandParser;
import edu.byu.ece.edif.util.parse.ParseException;

public class ClockResourceManager {

    ClockDomainParser _cdp;
    FlattenedEdifCell _ec;
    EdifCellInstanceGraph _graph;
    
    Set<EdifNet> _clockSet;

    Set<ClockResource> _clockResourceSet;

    public ClockResourceManager(String fileName, String[] dirs, String[] files) throws EdifException, ParseException,
            FileNotFoundException {
        String[] d = new String[1];
        d[0] = "..";
        EdifEnvironment edif_file = edu.byu.ece.edif.util.merge.EdifMergeParser.parseAndMergeEdif(
               fileName,
               Arrays.asList(d),
               Arrays.asList(files),
               edu.byu.ece.edif.arch.xilinx.XilinxLibrary.library);
        _ec = new FlattenedEdifCell(edif_file.getTopCell());
        _graph = new EdifCellInstanceGraph(_ec, true);
        _cdp = new ClockDomainParser(new XilinxClockingArchitecture());
        _clockResourceSet = new LinkedHashSet<ClockResource>();
        identifyClocks();
    }

    public ClockResourceManager(FlattenedEdifCell flatCell, EdifCellInstanceGraph ecic) throws EdifException,
            ParseException, FileNotFoundException {
        _ec = flatCell;
        _graph = ecic;
        _cdp = new ClockDomainParser(new XilinxClockingArchitecture());
        _clockResourceSet = new LinkedHashSet<ClockResource>();
        identifyClocks();
    }

    protected void identifyClocks() {
        _clockSet = _cdp.classifyNets(_ec, _graph).getClkToNetMap().keySet();
    }

    protected Set<ClockResource> getClockResources() {
        Set<EdifCellInstance> drivers = new LinkedHashSet<EdifCellInstance>();
        for (EdifNet net : _clockSet) {
            for (EdifPortRef epr : net.getOutputPortRefs()) {
                drivers.add(epr.getCellInstance());
            }
        }
        for (EdifCellInstance eci : drivers) {
            _clockResourceSet.add(new ClockResource(eci));
        }
        return new LinkedHashSet<ClockResource>(_clockResourceSet);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws ParseException, FileNotFoundException, EdifException {
        String fileName = "/net/fpga1/users/kwl6/tmp/dll.edf";
        if (args.length > 0)
            fileName = args[0];
        ClockResourceManager crm = new ClockResourceManager(fileName, null, null);
        for (ClockResource cr : crm.getClockResources()) {
            System.out.println(cr);
        }
    }
}
