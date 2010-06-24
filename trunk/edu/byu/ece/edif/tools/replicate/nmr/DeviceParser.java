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

import java.security.InvalidParameterException;

import edu.byu.ece.edif.arch.xilinx.parts.XilinxPartLookup;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.tools.replicate.nmr.NMRUtilities.UtilizationFactor;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxDeviceUtilizationTracker;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxPartValidator;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxVirtex4DeviceUtilizationTracker;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxVirtexDeviceUtilizationTracker;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxVirtexIIDeviceUtilizationTracker;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxVirtexIIProDeviceUtilizationTracker;

//////////////////////////////////////////////////////////////////////////
//// DeviceParser

/**
 * @author Keith Morgan
 * @version $Id$
 * @since Created on Feb 10, 2006
 */
public class DeviceParser {

    ///////////////////////////////////////////////////////////////////
    ////                         public variables                  ////

    public static final String XILINX = "xilinx";

    public static final String VIRTEX = "virtex";

    public static final String VIRTEX2 = "virtex2";

    public static final String VIRTEX2PRO = "Virtex2Pro";

    public static final String VIRTEX4 = "virtex4";

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * @param cell
     * @param family
     * @param part
     * @return XilinxDeviceUtilizationTracker
     * @throws OverutilizationException
     * @throws OverutilizationEstimatedStopException
     * @throws OverutilizationHardStopException
     */
    public static XilinxDeviceUtilizationTracker createXilinxDeviceUtilizationTracker(EdifCell cell, String family,
            String part) throws OverutilizationException, OverutilizationEstimatedStopException,
            OverutilizationHardStopException {
        double mergeFactor = XilinxDeviceUtilizationTracker.DEFAULT_MERGE_FACTOR;
        double optimizationFactor = XilinxDeviceUtilizationTracker.DEFAULT_OPTIMIZATION_FACTOR;
        double desiredUtilizationFactor = XilinxDeviceUtilizationTracker.DEFAULT_DESIRED_UTILIZATION_FACTOR;
        return createXilinxDeviceUtilizationTracker(cell, part, mergeFactor, optimizationFactor,
                desiredUtilizationFactor);
    }

    /**
     * @param cell
     * @param family
     * @param part
     * @param mergeFactor
     * @param optimizationFactor
     * @param desiredUtilizationFactor
     * @return XilinxDeviceUtilizationTracker
     * @throws OverutilizationException
     * @throws OverutilizationEstimatedStopException
     * @throws OverutilizationHardStopException
     */
    public static XilinxDeviceUtilizationTracker createXilinxDeviceUtilizationTracker(EdifCell cell, String part,
            double mergeFactor, double optimizationFactor, double desiredUtilizationFactor)
            throws OverutilizationException, OverutilizationEstimatedStopException, OverutilizationHardStopException {

        //String xilinxFamily = parseXilinxFamily(XilinxPartValidator.getTechnologyFromPart(part));
        String xilinxFamily = parseXilinxFamily(XilinxPartLookup.getFamilyFromPartName(part).getFamilyName());
    	if (xilinxFamily.equals(VIRTEX))
            return new XilinxVirtexDeviceUtilizationTracker(cell, part, mergeFactor, optimizationFactor,
                    desiredUtilizationFactor);
        else if (xilinxFamily.equals(VIRTEX2))
            return new XilinxVirtexIIDeviceUtilizationTracker(cell, part, mergeFactor, optimizationFactor,
                    desiredUtilizationFactor);
        else if (xilinxFamily.equals(VIRTEX2PRO))
            return new XilinxVirtexIIProDeviceUtilizationTracker(cell, part, mergeFactor, optimizationFactor,
                    desiredUtilizationFactor);
        else if (xilinxFamily.equals(VIRTEX4))
            return new XilinxVirtex4DeviceUtilizationTracker(cell, part, mergeFactor, optimizationFactor,
                    desiredUtilizationFactor);
        else
            throw new EdifRuntimeException("Xilinx family " + xilinxFamily + " not yet supported.");
    }

    public static XilinxDeviceUtilizationTracker createXilinxDeviceUtilizationTracker(EdifCell cell, String part,
            double mergeFactor, double optimizationFactor, double factorValue, NMRUtilities.UtilizationFactor type)
            throws OverutilizationException, OverutilizationEstimatedStopException, OverutilizationHardStopException {
        return createXilinxDeviceUtilizationTracker(cell, part, mergeFactor, optimizationFactor, factorValue, false,
                false, type);
    }

    /**
     * Create (and return) a XilinxDeviceUtilizationTracker for the given cell,
     * device part & family, using the given merge factor, optimization factor,
     * and utilization factor.
     * <p>
     * This method was created as a wrapper for
     * {@link #createXilinxDeviceUtilizationTracker(EdifCell, String, String, double, double, double)}
     * such that the caller need not recalculate the utilization factor for
     * different utilization factor types. That is, {@linkplain FlattenTMR} need
     * not know about the internal working of DeviceParser nor
     * XilinxDeviceUtilizationTracker. Rather, FlattenTMR should be able to get
     * a utilization tracker by requesting one with the utilization factor type
     * and value.
     * 
     * @param cell The EdifCell object
     * @param part The target part name (String object)
     * @param mergeFactor The merge factor
     * @param optimizationFactor The optimization factor
     * @param factorValue The utilization factor
     * @param type The utilization factor type
     * @return a XilinxDeviceUtilizationTracker
     * @throws OverutilizationException if the specified utilization factor is
     * smaller than the utilization of the design with no TMR.
     * @throws OverutilizationEstimatedStopException if the specified
     * utilization factor is smaller than the estimated utilization of the
     * design with no TMR.
     * @throws OverutilizationHardStopException if the design cannot fit on the
     * part due to hard constraints (e.g. too few RAMs, too few global buffers,
     * etc.).
     * @author <a href="mailto:jcarroll@byu.net">James Carroll</a>
     */
    public static XilinxDeviceUtilizationTracker createXilinxDeviceUtilizationTracker(EdifCell cell, String part,
            double mergeFactor, double optimizationFactor, double factorValue, boolean ignore_hard_limits,
            boolean ignore_soft_limits, NMRUtilities.UtilizationFactor type) throws OverutilizationException,
            OverutilizationEstimatedStopException, OverutilizationHardStopException {

        /*
         * First, create a utilization tracker with an "infinite" utilization
         * limit, to guarantee that it will finish with the initial estimation.
         */
        XilinxDeviceUtilizationTracker tracker = createXilinxDeviceUtilizationTracker(cell, part, mergeFactor,
                optimizationFactor, Double.MAX_VALUE);

        tracker.setDesiredUtilizationFactor(Double.valueOf(NMRUtilities.DEFAULT_FACTOR_VALUE));
        /*
         * If the user has requested an "available space factor," use the
         * current device utilization to update the "desiredUtilizationFactor"
         */
        if (type == UtilizationFactor.ASUF) {
            double currentUtilization = tracker.getEstimatedLogicBlockUtilizationRatio();
            double newUtilizationFactor = currentUtilization + (1 - currentUtilization) * factorValue;
            tracker.setDesiredUtilizationFactor(newUtilizationFactor);
        }

        /*
         * If the user has requested an "expansion factor," use the current
         * device utilization to update the "desiredUtilizationFactor"
         */
        /*
         * TODO: Put each call to {@link setDesiredUtilizationFactor(double) in 
         * a try-catch block to catch a OverUtilizationException
         * 
         * @see XilinxDeviceUtilizationTracker#setDesiredUtilizationFactor(double)
         */
        else if (type == UtilizationFactor.UEF) {
            double currentUtilization = tracker.getEstimatedLogicBlockUtilizationRatio();
            double newUtilizationFactor = currentUtilization * (1 + factorValue);
            tracker.setDesiredUtilizationFactor(newUtilizationFactor);
        } else if (type == UtilizationFactor.DUF) {
            tracker.setDesiredUtilizationFactor(factorValue);
        } else if (type == UtilizationFactor.CF) {
            tracker.setDesiredCoverageFactor(factorValue);
        } else {
            throw new InvalidParameterException("Invalid factor type.  Use one of the following: ASUF, UEF, DUF.");
        }

        if (ignore_soft_limits) {
            //tracker.setDesiredUtilizationFactor(Double.MAX_VALUE);
            tracker.ignoreSoftLogicUtilizationLimit();
        }

        if (ignore_hard_limits) {
            tracker.ignoreHardResourceUtilizationLimits();
        }

        return tracker;

    }

    /**
     * @param vendor
     * @param family
     * @return String
     */
    public static String parseFamily(String vendor, String family) {
        if (parseVendor(vendor).equals(XILINX))
            return parseXilinxFamily(family);
        else
            throw new EdifRuntimeException("Device vendor " + vendor + " not yet supported.");
    }

    /**
     * @param vendor
     * @return String
     */
    public static String parseVendor(String vendor) {
        if (vendor.compareToIgnoreCase(XILINX) == 0)
            return XILINX;
        else
            throw new EdifRuntimeException("Device vendor " + vendor + " not yet supported.");
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private methods                   ////

    /**
     * @param family
     * @return String
     */
    private static String parseXilinxFamily(String family) {
        if (family.compareToIgnoreCase(VIRTEX) == 0)
            return VIRTEX;
        else if (family.compareToIgnoreCase(VIRTEX2) == 0)
            return VIRTEX2;
        else if (family.compareToIgnoreCase(VIRTEX2PRO) == 0)
            return VIRTEX2PRO;
        else if (family.compareToIgnoreCase(VIRTEX4) == 0)
            return VIRTEX4;
        else
            throw new EdifRuntimeException("Xilinx family " + family + " not yet supported.");
    }
}
