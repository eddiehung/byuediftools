package edu.byu.ece.edif.arch.xilinx;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.byu.ece.edif.arch.ClockingArchitecture;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;

// TODO: evaluate division of code between this class and XilinxTools;
// right now, all that was in ClockDomainParser is in here, all that was
// in XilinxTools is still there.

// TODO: some of the code here or in XilinxTools might be fairly
// portable (XilinxTools.hasClockPort, XilinxClockingArchitecture.getClockNets
// has a little bit of special casing). Would it be worthwhile to create
// an abstract class between the interface and this class (and future
// ClockingArchitectures for different vendors?)
public class XilinxClockingArchitecture implements ClockingArchitecture {
    /**
     * Determines whether this EdifCell is a register cell.
     * In this case, register means a cell storing state
     * (flip-flops, latches, RAMs)
     * 
     * TODO: This may not be the best name for this method,
     * but this is the name given to the corresponding method
     * for Xilinx (edif.arch.xilinx.XilinxTools)
     * 
     * @param cell
     * @return
     */
    public boolean isRegisterCell(EdifCell cell) {
        return XilinxTools.isRegisterCell(cell);
    }
    
    /**
     * Determines whether this EdifCell is a sequential cell, 
     * which essentially means that it is either a register cell
     * or a different cell with a clock input (such as a DCM).
     * 
     * TODO: as with isRegisterCell, a name change may be appropriate
     * 
     * @param cell
     * @return
     */
    public boolean isSequential(EdifCell cell) {
        return XilinxTools.isSequential(cell);
    }
    
    /**
     * Determines if this EdifSingleBitPort is a clock port.
     * 
     * @param port
     * @return
     */
    public boolean isClockPort(EdifSingleBitPort port) {
        return XilinxTools.isClockPort(port);
    }
    
    /**
     * Determines whether this EdifCell has any clock ports.
     * 
     * @param cell
     * @return
     */
    public boolean hasClockPort(EdifCell cell) {
        return XilinxTools.hasClockPort(cell);
    }
    
    /**
     * Determines whether this EdifNet is a clock net. This is the case
     * if any sinks of the net are connected to clock ports of 
     * sequential cells - this method does NOT check whether the driver
     * of the net is driven by a clock driver primitive. 
     * 
     * @param net
     * @return
     */
    public boolean isClockNet(EdifNet net) {
        return XilinxTools.drivesClockPorts(net);
    }
    
    /** 
     * Returns all of the EdifNets within this cell which are clock nets,
     * meaning that sinks of the net are connected to clock ports of 
     * sequential cells. If the passed-in cell is not flattened, this 
     * method may not find the clock nets in the cell. It will only find
     * nets that drive clock ports of sequential leaf cells contained in
     * this cell (not in leaf cells in lower levels of hierarchy.)
     * 
     * It is not necessary for any clock drivers (BUFG, etc.) to be present
     * in the cell to find clock nets using this method; any EdifCell with
     * sequential leaf cells should find the nets that drive the clock ports
     * of all of the leaf cells.
     * 
     * @param cell
     * @return a Set containing all clock nets internal to this EdifCell.
     */
    public Set<EdifNet> getClockNets(EdifCell cell) {        

        // Identify the clock clock nets. Iterate over all instances of the cell and select
        // those that are tagged as "sequential". For those instances tagged as sequential,
        // find all the input EdifPortRef objects that are attached to it.Of these, find the
        // EdifPortRef objects that are attached to a clock input. Tag such nets as "clock nets"
        // (with exception of GND).
        Set<EdifNet> clockNets = new LinkedHashSet<EdifNet>();        
        EdifCellInstanceGraph graph = new EdifCellInstanceGraph(cell);
        for (EdifCellInstance eci : cell.getSubCellList()) {
            if (isSequential(eci.getCellType())) {
                // TODO: Do we really need a graph to compute this? It doesn't seem
                // necessary (i.e., just look at the netlist topology)
                for (EdifPortRef epr : graph.getEPRsWhichReferenceInputPortsOfECI(eci)) {
                    if (XilinxTools.isClockPort(epr.getSingleBitPort())) {
                        // The following is for a special case which
                        // doesn't allow GNDs to drive a clock net
                        Iterator<EdifPortRef> it = epr.getNet().getOutputPortRefs().iterator();
                        if (!it.hasNext() || !it.next().getCellInstance().getType().toLowerCase().equals("gnd"))
                            clockNets.add(epr.getNet());
                    }
                }
            }
        }
        return clockNets;
    }
    
    /**
     * Return true if the EdifCell ec has an asynchronous reset/preset.
     * 
     * @param cell The name of the cell to check
     * @return True if this cell has an asynchronous reset/preset
     */
    public boolean hasAsynchronousReset(EdifCell cell) {
        String str = cell.getName().toLowerCase();

        if (str.startsWith("fdc"))
            return true;
        if (str.startsWith("fddrcpe"))
            return true;
        if (str.startsWith("fdp"))
            return true;
        return false;
    }

    /**
     * Return true if the EdifCell ec is BUFG, DLL, or DCM
     * 
     * @param cell The name of the cell to check
     * @return True if this cell is BUFG, DLL, or DCM
     */
    public boolean isClockDriver(EdifCell cell) {
        String str = cell.getName().toLowerCase();
        if (str.startsWith("bufg"))
            return true;
        if (str.startsWith("dcm"))
            return true;
        if (str.contains("dll"))
            return true;
        return false;
    }  
}
