package edu.byu.ece.edif.arch;

import java.util.Set;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifSingleBitPort;

public interface ClockingArchitecture {
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
    public boolean isRegisterCell(EdifCell cell);
    
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
    public boolean isSequential(EdifCell cell);
    
    /**
     * Determines if this EdifSingleBitPort is a clock port.
     * 
     * @param port
     * @return
     */
    public boolean isClockPort(EdifSingleBitPort port);
    
    /**
     * Determines whether this EdifCell has any clock ports.
     * 
     * @param cell
     * @return
     */
    public boolean hasClockPort(EdifCell cell);
    
    /**
     * Determines whether this EdifNet is a clock net. This is the case
     * if any sinks of the net are connected to clock ports of 
     * sequential cells - this method does NOT check whether the driver
     * of the net is driven by a clock driver primitive. 
     * 
     * @param net
     * @return
     */
    public boolean isClockNet(EdifNet net);
    
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
    public Set<EdifNet> getClockNets(EdifCell cell); 
    
    /**
     * Determines if this EdifCell has any asynchronous resets.
     * 
     * @param cell
     * @return
     */
    public boolean hasAsynchronousReset(EdifCell cell);
    
    /**
     * Determines if this EdifCell is a clock driver (such as BUFG or
     * similar/related primitives).
     * 
     * @param cell
     * @return
     */
    public boolean isClockDriver(EdifCell cell);

}
