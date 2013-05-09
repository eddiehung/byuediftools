/*
 * A Xilinx implementation of NMR to be implemented for different Xilinx parts.
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
package edu.byu.ece.edif.tools.replicate.nmr.xilinx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
import edu.byu.ece.edif.arch.xilinx.XilinxTools;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNameable;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.NamedObject;
import edu.byu.ece.edif.tools.replicate.nmr.AbstractNMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.Organ;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationType;
import edu.byu.ece.edif.tools.replicate.nmr.tmr.TMRReplicationType;
import edu.byu.ece.edif.tools.replicate.wiring.NetManager;
import edu.byu.ece.edif.tools.replicate.wiring.PortConnection;
import edu.byu.ece.edif.tools.replicate.wiring.SinglePortConnection;

/**
 * Provides a Xilinx implementation of NMR. It is intended that this class be
 * implemented by the different families of Xilinx parts.
 * 
 * @since Created on May 23, 2005
 */
public class XilinxNMRArchitecture extends AbstractNMRArchitecture {

    public XilinxNMRArchitecture() {
        // Initialize the set of known Bad Cut Connections for Xilinx parts
        _badCutConnections = new XilinxBadCutConnections();
        _init();
    }

    public boolean isAFlipFlop(EdifCell cellType) {
        return XilinxTools.isAFlipFlop(cellType);
    }

    public boolean isBRAM(EdifCell cellType) {
        return XilinxTools.isBRAM(cellType);
    }

    /**
     * Initialize the set of Bad Cut Connections for this architecture. This
     * list can be less verbose when utilizing the shortcuts enabled in the
     * XilinxBadCutConnections class. E.g. MUXCY, MUXCY_L, and MUXCY_D can all
     * be treated the same with the name MUXCY.
     */
    // TODO: Verify and completely fill in this set
    protected void _init() {
        // These cuts are just plain illegal
        _badCutConnections.addBadCutConnection("MULT_AND", "LO", "MUXCY", "DI");
        _badCutConnections.addBadCutConnection("MUXCY", "LO", "XORCY", "CI");
        _badCutConnections.addBadCutConnection("MUXCY", "O", "XORCY", "CI");
        // These cuts are sometimes legal, but it's a lot easier to just 
        // call all cuts in the carry chain "bad"
        _badCutConnections.addBadCutConnection("MUXCY", "O", "MUXCY", "CI");
        _badCutConnections.addBadCutConnection("MUXCY", "LO", "MUXCY", "CI");
    
        // Not necessary?
        // An XORCY output *can* exit the slice and drive another flip-flop
        //   Maybe the LO output cannot, though...
        //_badCutConnections.addBadCutConnection("XORCY", "LO", "FD", "D");
        //_badCutConnections.addBadCutConnection("XORCY", "O", "FD", "D");
        //_badCutConnections.addBadCutConnection("LUT", "LO", "FD", "D");
        //_badCutConnections.addBadCutConnection("LUT", "O", "FD", "D");

        // Prevent cuts between MUXF?'s
        _badCutConnections.addBadCutConnection("MUXF5", "O", "MUXF6", "I0");
        _badCutConnections.addBadCutConnection("MUXF5", "O", "MUXF6", "I1");
        _badCutConnections.addBadCutConnection("MUXF6", "O", "MUXF7", "I0");
        _badCutConnections.addBadCutConnection("MUXF6", "O", "MUXF7", "I1");
        _badCutConnections.addBadCutConnection("MUXF7", "O", "MUXF8", "I0");
        _badCutConnections.addBadCutConnection("MUXF7", "O", "MUXF8", "I1");

        // Prevent all cuts on inputs to MUXF6's I1 port
        _badCutConnections.addBadCutConnection("*", "*", "MUXF6", "I1");

        // Prevent cuts before input buffers and after output buffers
        // Q: Is this necessary? I can't remember why I did this and in another
        //    design (the 2nd Astrium design) it doesn't seem to be necessary.
        // A: The idea here is to prevent cuts at the pad. In the afore-
        //    mentioned design, the IOBUFs cause a problem because normal
        //    elements can be attached to the "I" and "O" ports
        _badCutConnections.addBadCutConnection("*", "*", "IBUF", "I");
        //_badCutConnections.addBadCutConnection("*", "*", "IOBUF", "I");
        //_badCutConnections.addBadCutConnection("IOBUF", "O", "*", "*");
        _badCutConnections.addBadCutConnection("OBUF", "O", "*", "*");
        //DSP ports in V4 and greater
        _badCutConnections.addBadCutConnection("DSP48", "PCOUT", "DSP48", "PCIN");
        _badCutConnections.addBadCutConnection("DSP48", "BCOUT", "DSP48", "BCIN");
        
        //DSP ports in V7
        _badCutConnections.addBadCutConnection("DSP48E1", "PCOUT", "DSP48E1", "PCIN");
        _badCutConnections.addBadCutConnection("DSP48E1", "BCOUT", "DSP48E1", "BCIN");
        
        _badCutConnections.addBadCutConnection("*", "*", "IDELAYCTL", "REFCLK");
        _badCutConnections.addBadCutConnection("*", "*", "IDELAYCTL", "RST");
        _badCutConnections.addBadCutConnection("BUFG", "O", "IDELAYCTL", "REFCLK");

        _badCutConnections.addBadCutConnection("DCM", "PSEN", "*", "*");

        _badCutConnections.addBadCutConnection("*", "*", "DCM", "CLKFB");
        
        //V7 CARRY4
        _badCutConnections.addBadCutConnection("CARRY4", "CO", "CARRY4", "CI");
        _badCutConnections.addBadCutConnection("CARRY4", "O", "CARRY4", "CI");

    }

    /**
     * @return true if the given EdifNet is a clock net, false otherwise
     */
    public boolean isClockNet(EdifNet net) {
        return XilinxTools.drivesClockPorts(net);
    }

    /** The EdifCell object that implements the majority voting. */
    protected EdifCell _voterCell;

	public Organ getDefaultRestoringOrganForReplicationType(Class<? extends ReplicationType> c) {
		Organ result = null;
		if (c == TMRReplicationType.class)
			result = XilinxTMRVoter.getInstance();
		return result;
	}
	
	public List<PortConnection> prepareForDetectionOutput(List<? extends PortConnection> unpreparedOutput, boolean registerDetection, boolean addObuf, String clockNetName, NetManager netManager) {
		List<PortConnection> result = new ArrayList<PortConnection>();
		for (PortConnection pc : unpreparedOutput) {
			PortConnection output = pc;
			if (registerDetection) {
				EdifNameable name = netManager.getTopCell().getUniqueInstanceNameable(NamedObject.createValidEdifNameable("fd"));
				output = insertFD(name, output, clockNetName, netManager);
			}
			if (addObuf) {
				EdifNameable name = netManager.getTopCell().getUniqueInstanceNameable(NamedObject.createValidEdifNameable("obuf"));
				output = insertOBUF(name, output, netManager);
			}
			result.add(output);			
		}
		return result;
	}
	
	public PortConnection insertOBUF(EdifNameable name, PortConnection input, NetManager netManager) {
		EdifCell parent = netManager.getTopCell();
		
        // 1. Obtain a reference to the OBUF EdifCell
        EdifCell obuf = getOBUFCell(parent);

        // 2. Create a new instance
        EdifCellInstance obufCellInstance = new EdifCellInstance(name, parent, obuf);

        try {
            parent.addSubCell(obufCellInstance);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }

        // Hook up input to I port
        PortConnection I = new SinglePortConnection(obuf.getPort("I").getSingleBitPort(0), obufCellInstance);
        netManager.wirePortConnections(input, I);

        PortConnection O = new SinglePortConnection(obuf.getPort("O").getSingleBitPort(0), obufCellInstance);
        return O;
    }
	
	public PortConnection insertFD(EdifNameable name, PortConnection input, String clockNetName, NetManager netManager) {
		EdifCell parent = netManager.getTopCell();
		
		// 1. Obtain a reference to the FD EdifCell
		EdifCell fd = getFDCell(parent);
		
		// 2. Create a new instance
		EdifCellInstance fdCellInstance = new EdifCellInstance(name, parent, fd);
		
		try {
			parent.addSubCell(fdCellInstance);
		} catch (EdifNameConflictException e) {
			e.toRuntime();
		}
		
		// find clock net and hook to FD clock input
		EdifNet clockNet = parent.getNet(clockNetName);
		if (clockNet == null)
			throw new EdifRuntimeException("Error: Cannot find specified clock net (" + clockNetName + ") for error detection signal output registers.");
		EdifPortRef c = new EdifPortRef(clockNet, fd.getPort("C").getSingleBitPort(0), fdCellInstance);
		clockNet.addPortConnection(c);
				
		// Hook up input to D port
		PortConnection d = new SinglePortConnection(fd.getPort("D").getSingleBitPort(0), fdCellInstance);
		netManager.wirePortConnections(input, d);
		
		// return output (Q) PortConnection
		PortConnection q = new SinglePortConnection(fd.getPort("Q").getSingleBitPort(0), fdCellInstance);
		return q;
	}
	
	protected EdifCell getOBUFCell(EdifCell parent) {

        /*
         * If the obuf cell has already been defined, return the associated Edif
         * Cell.
         */
		EdifCell obufCell = _cellToObufMap.get(parent);
		if (obufCell != null)
			return obufCell;
		
        String obufCellName = "OBUF";

        // Step #1 - Get Xilinx primitive
        EdifLibrary xilinxLibrary = XilinxLibrary.library;

        EdifCell buf = xilinxLibrary.getCell(obufCellName);

        // Step #2 - Search for the Xilinx primitive in library manager
        Collection<EdifCell> matchingOBUFCells = parent.getLibrary().getLibraryManager().getCells(obufCellName);

        // Iterate over all cells to see if the primitive exists
        if (matchingOBUFCells != null) {
            // Iterate over the matching cells and see if it exists
            for (EdifCell ce : matchingOBUFCells) {
                if (ce.equalsInterface(buf)) {
                    // The AND exists - tag it as the merger cell
                    obufCell = ce;
                    _cellToObufMap.put(parent, obufCell);
                    return obufCell;
                }
            }
        }

        // Step #3 - The primitive does not exist in our library. Add it.
        EdifLibrary lib = parent.getLibrary().getLibraryManager().getFirstPrimitiveLibrary();
        if (lib == null)
            lib = parent.getLibrary();

        try {
            lib.addCell(buf);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
        _cellToObufMap.put(parent, buf);

        return buf;
    }
	
	protected EdifCell getFDCell(EdifCell parent) {

        /*
         * If the fd cell has already been defined, return the associated Edif
         * Cell.
         */
		EdifCell fdCell = _cellToFDMap.get(parent);
		if (fdCell != null)
			return fdCell;
		
        String fdCellName = "FD";

        // Step #1 - Get Xilinx primitive
        EdifLibrary xilinxLibrary = XilinxLibrary.library;

        EdifCell fd = xilinxLibrary.getCell(fdCellName);

        // Step #2 - Search for the Xilinx primitive in library manager
        Collection<EdifCell> matchingFDCells = parent.getLibrary().getLibraryManager().getCells(fdCellName);

        // Iterate over all cells to see if the primitive exists
        if (matchingFDCells != null) {
            // Iterate over the matching cells and see if it exists
            for (EdifCell ce : matchingFDCells) {
                if (ce.equalsInterface(fd)) {
                    // The AND exists - tag it as the merger cell
                    fdCell = ce;
                    _cellToFDMap.put(parent, fdCell);
                    return fdCell;
                }
            }
        }

        // Step #3 - The primitive does not exist in our library. Add it.
        EdifLibrary lib = parent.getLibrary().getLibraryManager().getFirstPrimitiveLibrary();
        if (lib == null)
            lib = parent.getLibrary();

        try {
            lib.addCell(fd);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
        _cellToFDMap.put(parent, fd);

        return fd;
    }
	
	Map<EdifCell, EdifCell> _cellToObufMap = new LinkedHashMap<EdifCell, EdifCell>();
	Map<EdifCell, EdifCell> _cellToFDMap = new LinkedHashMap<EdifCell, EdifCell>();

}
