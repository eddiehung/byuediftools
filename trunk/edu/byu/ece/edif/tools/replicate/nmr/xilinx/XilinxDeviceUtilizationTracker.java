/*
 * An abstract implementation of a Xilinx device utilization tracker.
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

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.tools.replicate.nmr.AbstractDeviceUtilizationTracker;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationEstimatedStopException;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationHardStopException;
import edu.byu.ece.edif.tools.replicate.nmr.UnsupportedResourceTypeException;

/////////////////////////////////////////////////////////////////////////
//// XilinxDeviceUtilizationTracker
/**
 * An abstract implementation of a Xilinx device utilization tracker.
 * 
 * @author Keith Morgan
 * @version $Id: XilinxDeviceUtilizationTracker.java 151 2008-04-02 16:27:55Z
 * jamesfcarroll $
 */
public abstract class XilinxDeviceUtilizationTracker extends AbstractDeviceUtilizationTracker {

    public XilinxDeviceUtilizationTracker(double mergeFactor, double optimizationFactor, double desiredUtilizationFactor) {
        super();
        _mergeFactor = mergeFactor;
        _optimizationFactor = optimizationFactor;
        _desiredUtilizationFactor = desiredUtilizationFactor;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Decrements the utilization of the cell-type of the parameter eci.
     */
    public void decrementResourceCount(EdifCellInstance eci) throws UnsupportedResourceTypeException {
        String eciResourceType = getResourceType(eci);
        if (eciResourceType == null) {
            System.out
                    .println("WARNING: Instance "
                            + eci
                            + " does not map to a Xilinx primitive.  If it is a black box, it's internal utilization will not be tracked.");
            return;
        }
        decrementResourceCount(eciResourceType);
    }

    /**
     * Decrements the utilization of the cell-type of the parameter eci.
     */
    public void decrementResourceCount(String resourceType) throws UnsupportedResourceTypeException {

        //TODO: What should happen if the resource type "" is returned??
        //      Previously this was just ignored
        if (resourceType.compareToIgnoreCase("") == 0)
            return;

        Double currentUtilizationD = _currentUtilizationMap.get(resourceType);
        Integer maxUtilizationI = _maxUtilizationMap.get(resourceType);
        if (currentUtilizationD == null || maxUtilizationI == null)
            throw new UnsupportedResourceTypeException("Resource type " + resourceType
                    + " which is not supported in the specified part.");
        double currentUtilization = currentUtilizationD.doubleValue();
        int maxUtilization = maxUtilizationI.intValue();

        // Check to not go below 0, otherwise decrement utilization
        if (currentUtilization - 1.0 < 0.0)
            _currentUtilizationMap.put(resourceType, new Double(0.0));
        else
            _currentUtilizationMap.put(resourceType, new Double(currentUtilization - 1.0));
    }

    public void decrementVoterCount() throws UnsupportedResourceTypeException {
        _numVoters--;
        if (_numVoters < 0)
            _numVoters = 0;
        decrementResourceCount(XilinxResourceMapper.LUT);
    }

    public double getDesiredUtilizationFactor() {
        return _desiredUtilizationFactor;
    }

    /**
     * Estimates the logic block utilization of the current design. <br>
     * The equation is as follows:<br>
     * <blockquote><code>Estimate = optimizationFactor*(utilizedFFs 
     * + utilizedLUTs - mergeFactor * utilizedFFs);</code></blockquote>
     * Derivation: <blockquote>Let A represent the total number of FF's, B
     * represent the number of logic blocks in which FF's and LUTs are combined,
     * and C represent the total number of LUTs. Then <blockquote><code>total FF's = (A + B)<br>total LUTs = (B + C)<br>total logic blocks = (A + B + C)</code></blockquote> We define
     * mergeFactor to be <blockquote><code>mergeFactor = (mergeable flipflops) / (total FF's)</code></blockquote>
     * so<blockquote><code>mergeFactor = B / (A + B)</code></blockquote>and<blockquote><code>B = mergeFactor * (A + B)</code></blockquote>
     * Note that <blockquote><code>(A + B + C) = (A + B) + (B + C) - B</code></blockquote>Substitute
     * B to get<blockquote><code>(A + B + C) = (A + B) + (B + C) - mergeFactor * (A + B)</code></blockquote>
     * Thus the total number of logic blocks (without taking into account
     * optimization) is given by the following equation:<blockquote><code>total logic blocks = FF's + LUTs - mergeFactor * FF's</code></blockquote>
     * We define optimizationFactor to be the number of logic blocks after
     * optimization divided by the number of logic blocks before optimization.
     * So the final equation for the total number of logic blocks is as follows:
     * <blockquote><code>Estimate = optimizationFactor * (FF's + LUTs - mergeFactor * FF's)</code></blockquote></blockquote>
     * If you need to calculate a custom mergeFactor for a specific design, use
     * the following equation: <blockquote><code>mergeFactor = (FF's + LUTs - 2 * slices) / FF's</code></blockquote>
     * 
     * @return Logic block utilization estimate.
     * @see #getMergeFactor()
     * @see #getOptimizationFactor()
     */
    public double getEstimatedLogicBlockUtilization() {
        double estimatedLogicBlockUtilization = 0.0;
        double currentLutUtilization = _currentUtilizationMap.get(XilinxResourceMapper.LUT).doubleValue();
        double currentFFUtilization = _currentUtilizationMap.get(XilinxResourceMapper.FF).doubleValue();
        estimatedLogicBlockUtilization = _optimizationFactor
                * (currentFFUtilization + currentLutUtilization - _mergeFactor * currentFFUtilization);
        return estimatedLogicBlockUtilization;
    }

    public double getEstimatedLogicBlockUtilizationRatio() {
        return (getEstimatedLogicBlockUtilization()) / getMaxLogicBlocks();
    }

    /**
     * The merge factor is a scaler which tries to estimate the percentage of
     * Flip-Flops and LUTs which can share a logic block.
     * 
     * @return The merge scale factor.
     */
    public double getMergeFactor() {
        return _mergeFactor;
    }

    /**
     * The optimization factor is a scaler which tries to estimate the
     * percentage of logic blocks which will be optimized away.
     * 
     * @return The optimization scale factor.
     */
    public double getOptimizationFactor() {
        return _optimizationFactor;
    }

    public String getResourceType(EdifCellInstance eci) {
        return XilinxResourceMapper.getResourceType(eci);
    }

    public int getVoterCount() {
        return _numVoters;
    }

    /**
     * Increments the utilization of the cell-type parameter resourceType.
     */
    public void incrementResourceCount(String resourceType) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, UnsupportedResourceTypeException {

        //TODO: What should happen if the resource type "" is returned??
        //      Previously this was just ignored
        if (resourceType == null || resourceType.compareToIgnoreCase("") == 0) {
            return;
        }
        if (_currentNMRInstances.size() / total_instances > coverage_factor)
            throw new OverutilizationEstimatedStopException("Reached desired Triplication coverage");

        Double currentUtilizationD = _currentUtilizationMap.get(resourceType);
        Integer maxUtilizationI = _maxUtilizationMap.get(resourceType);
        if (currentUtilizationD == null || maxUtilizationI == null)
            throw new UnsupportedResourceTypeException("Resource type " + resourceType
                    + " is not supported in the specified part.");
        double currentUtilization = currentUtilizationD.doubleValue();
        int maxUtilization = maxUtilizationI.intValue();

        // Check for hard resource exception, otherwise increment utilization
        if (currentUtilization + 1.0 > maxUtilization)
            throw new OverutilizationHardStopException("Adding instance of resource type" + resourceType + " exceeds "
                    + maxUtilization + ", the maximum number of available resources for type " + resourceType + ".");
        else
            _currentUtilizationMap.put(resourceType, new Double(currentUtilization + 1.0));

        // Check to see if the current utilization exceeds the calculated limit.
        // If the limit is exceeded, throw an estimated overutilization exception
        int estimatedLogicBlockUtilization = (int) getEstimatedLogicBlockUtilization();
        double maxLogicBlocks = getMaxLogicBlocks();
        double maxDesiredLogicBlocks = _desiredUtilizationFactor * maxLogicBlocks;
        if (estimatedLogicBlockUtilization > maxDesiredLogicBlocks) {
            double currentLUTUtilization = _currentUtilizationMap.get(XilinxResourceMapper.LUT).doubleValue();
            double currentFFUtilization = _currentUtilizationMap.get(XilinxResourceMapper.FF).doubleValue();
            throw new OverutilizationEstimatedStopException("The current device has " + maxLogicBlocks
                    + " available logic blocks.  The user requested a maximum of " + maxDesiredLogicBlocks
                    + " be utilized. The estimated current logic block utilization is "
                    + estimatedLogicBlockUtilization + ", based on the current LUT utilization "
                    + currentLUTUtilization + " and FF utilization " + currentFFUtilization + ".");
        }
    }

    /**
     * Increments the utilization of the cell-type of the parameter eci.
     */
    public void incrementResourceCount(EdifCellInstance eci) throws OverutilizationEstimatedStopException,
            OverutilizationHardStopException, UnsupportedResourceTypeException {
        String eciResourceType = getResourceType(eci);

        if (eciResourceType == null) {
            if (eci.getCellType().getBlackBoxResources() == null) {
                System.out.println("WARNING: Instance " + eci
                        + " does not map to a Xilinx primitve.  If it is a black box,"
                        + " it's internal utilization will not be tracked.");
            } else {
                for (String type : eci.getCellType().getBlackBoxResources())
                    incrementResourceCount(type);
                System.out.println("WARNING:\n\t Instance " + eci
                        + " does not map to a Xilinx primitve, and is assumed to be a black box "
                        + "There is utilization information about this blackbox, and that will be "
                        + "used to estimate the total utilization. \n");
            }
        } else
            incrementResourceCount(eciResourceType);
        return;
    }

    public void incrementVoterCount() throws OverutilizationHardStopException, OverutilizationEstimatedStopException {
        _numVoters++;
        incrementResourceCount(XilinxResourceMapper.LUT);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append("Logic Blocks (estimated): " + (int) getEstimatedLogicBlockUtilization() + " out of "
                + getMaxLogicBlocks() + " (" + (int) (100.0 * getEstimatedLogicBlockUtilizationRatio()) + "%).\n");
        sb.append("Specified Merge Factor: " + _mergeFactor + "\n");
        sb.append("Specified Optimization Factor: " + _optimizationFactor + "\n");
        sb.append("Desired Utilization Factor: " + _desiredUtilizationFactor + "\n");
        return sb.toString();
    }

    /**
     * TODO: Check to make sure we aren't setting the utilization factor too
     * low.
     * 
     * @param desiredUtilizationFactor
     */
    public void setDesiredUtilizationFactor(double desiredUtilizationFactor) {
        _desiredUtilizationFactor = desiredUtilizationFactor;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         protected methods                 ////

    @Override
    protected void _init(EdifCell cell) throws OverutilizationEstimatedStopException, OverutilizationHardStopException {
        // !!! This ordering matters, the super call to _init should be last !!!
        super._init(cell);
    }

    /**
     * This assumes that the number of logic blocks (LUT-FF pairs) is equal to
     * the number of LUTs.
     */
    protected int getMaxLogicBlocks() {
        return getResourceLimit(XilinxResourceMapper.LUT);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         protected variables               ////

    /**
     * This factor scales the total max utilization ratio. For example if a user
     * wants to only utilize 75% of the chip, they would specify this factor to
     * be 0.75
     */
    protected double _desiredUtilizationFactor;

    /**
     * This factor scales the number of LUTs and FFs which will share a logic
     * block
     */
    protected double _mergeFactor;

    /**
     * This factor scales the number of logic blocks used by LUTs and FFs (to
     * account for logic minimization)
     */
    protected double _optimizationFactor;

    /* Virtex, Virtex II, and Virtex II-Pro Package names */
    protected static final String BF957_PACKAGE = "BF957";

    protected static final String BG256_PACKAGE = "BG256";

    protected static final String BG352_PACKAGE = "BG352";

    protected static final String BG432_PACKAGE = "BG432";

    protected static final String BG560_PACKAGE = "BG560";

    protected static final String BG575_PACKAGE = "BG575";

    protected static final String BG728_PACKAGE = "BG728";

    protected static final String CS144_PACKAGE = "CS144";

    protected static final String FF672_PACKAGE = "FF672";

    protected static final String FF896_PACKAGE = "FF896";

    protected static final String FF1148_PACKAGE = "FF1148";

    protected static final String FF1152_PACKAGE = "FF1152";

    protected static final String FF1517_PACKAGE = "FF1517";

    protected static final String FF1696_PACKAGE = "FF1696";

    protected static final String FF1704_PACKAGE = "FF1704";

    protected static final String FG256_PACKAGE = "FG256";

    protected static final String FG456_PACKAGE = "FG456";

    protected static final String FG676_PACKAGE = "FG676";

    protected static final String FG680_PACKAGE = "FG680";

    protected static final String HQ240_PACKAGE = "HQ240";

    protected static final String PQ240_PACKAGE = "PQ240";

    protected static final String TQ144_PACKAGE = "TQ144";

    /* Virtex 4 Package Strings */
    protected static final String SF363_PACKAGE = "SF363";

    protected static final String FF668_PACKAGE = "FF668";

    protected static final String FF676_PACKAGE = "FF676";

    protected static final String FF1513_PACKAGE = "FF1513";

    protected static final String SFG363_PACKAGE = "SFG363";

    protected static final String FFG668_PACKAGE = "FFG668";

    protected static final String FFG672_PACKAGE = "FFG672";

    protected static final String FFG676_PACKAGE = "FFG676";

    protected static final String FFG1148_PACKAGE = "FFG1148";

    protected static final String FFG1152_PACKAGE = "FFG1152";

    protected static final String FFG1513_PACKAGE = "FFG1513";

    protected static final String FFG1517_PACKAGE = "FFG1517";

    /*
     * The following packages are found in both Virtex4 and previous Virtex
     * architectures; they are included here only for completeness. -jFc
     */
    // protected static final String FF1517_PACKAGE = "FF1517";
    // protected static final String FF672_PACKAGE = "FF672"; 
    // protected static final String FF1148_PACKAGE = "FF1148";
    // protected static final String FF1152_PACKAGE = "FF1152";
    // /////////////////////////////////////////////////////////////////
    ////                         private variables                 ////
    /**
     * Keep track of the number of voters added.
     */
    private int _numVoters = 0;

    ///////////////////////////////////////////////////////////////////
    ////                         package private variables         ////

    /**
     * The default merge factor
     */
    public static final double DEFAULT_MERGE_FACTOR = 0.5;

    /**
     * The default optimization factor
     */
    public static final double DEFAULT_OPTIMIZATION_FACTOR = 0.95;

    /**
     * The default desired utilization factor
     */
    public static final double DEFAULT_DESIRED_UTILIZATION_FACTOR = 1.0;
}
