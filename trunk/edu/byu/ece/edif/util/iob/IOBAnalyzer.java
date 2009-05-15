package edu.byu.ece.edif.util.iob;

import java.io.Serializable;
import java.util.Collection;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;

public interface IOBAnalyzer extends Serializable {

    /**
     * @return A Collection of EdifCellInstances corresponding to all of the IOB
     * instances in this EdifCell
     */
    public Collection<EdifCellInstance> getAllIOBInstances();

    /**
     * @return A Collection of EdifCellInstances corresponding to all of the IOB
     * registers in this EdifCell
     */
    public Collection<EdifCellInstance> getAllIOBRegisters();

    /**
     * @return A Collection of IOBs that may be in feedback (inout IOBs)
     */
    public Collection<EdifSingleBitPort> getFeedbackIOBs();

    /**
     * Returns a Collection of graph Edges corresponding to possible IOB
     * feedback in the design.
     */
    public Collection<EdifCellInstanceEdge> getIOBFeedbackEdges();

    /**
     * Returns a Set of EPRs corresponding to the IOB feedback edges in the
     * graph. The decision of which EPR is not smart currently. Please use
     * getIOBFeedbackEdges() instead.
     * 
     * @return
     */
    public Collection<EdifPortRef> getIOBFeedbackEPRs();

    /**
     * @return A Collection of EdifCellInstances corresponding to all of the IOB
     * instances associated with this EdifSingleBitPort
     */
    public Collection<EdifCellInstance> getIOBInstances(EdifSingleBitPort esbp);

    /**
     * @return A Collection of EdifCellInstances corresponding to all of the IOB
     * instances associated with these EdifSingleBitPorts
     */
    public Collection<EdifCellInstance> getIOBInstances(Collection<EdifSingleBitPort> esbps);

    /**
     * @return A Collection of EdifCellInstances corresponding to all of the IOB
     * registers in associated with this EdifSingleBitPort (including input
     * register, output register, and tristate register)
     */
    public Collection<EdifCellInstance> getIOBRegisters(EdifSingleBitPort esbp);
    
    /**
     * @return the AbstractIOB associated with this EdifSingleBitPort
     * @param esbp
     */
    public AbstractIOB getIOB(EdifSingleBitPort esbp);
    
    /**
     * @return a boolean indicating whether input register packing was selected
     */
    public boolean packInputRegs();
    
    /**
     * @return a boolean indicating whether output register packing was selected
     */
    public boolean packOutputRegs();

}
