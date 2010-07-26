/*
 * This file was auto-generated on Wed Jul 21 15:42:59 MDT 2010
 * by edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxFamilyDeviceUtilizationTrackerGenerator.
 * See the source code to make changes.
 *
 * Do not modify this file directly.
 */


package edu.byu.ece.edif.tools.replicate.nmr.xilinx;

import edu.byu.ece.edif.arch.xilinx.parts.XilinxPartLookup;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationEstimatedStopException;
import edu.byu.ece.edif.tools.replicate.nmr.OverutilizationHardStopException;

public class XilinxVirtex5DeviceUtilizationTracker extends XilinxDeviceUtilizationTracker {

	public XilinxVirtex5DeviceUtilizationTracker(EdifCell cell, String part)
		throws OverutilizationEstimatedStopException, OverutilizationHardStopException, IllegalArgumentException {
			this(cell, part, DEFAULT_MERGE_FACTOR, DEFAULT_OPTIMIZATION_FACTOR, DEFAULT_DESIRED_UTILIZATION_FACTOR);
	}

	public XilinxVirtex5DeviceUtilizationTracker(EdifCell cell, String part, double mergeFactor,
			double optimizationFactor, double desiredUtilizationFactor) throws OverutilizationEstimatedStopException,
			OverutilizationHardStopException, IllegalArgumentException {

		super(mergeFactor, optimizationFactor, desiredUtilizationFactor);
		part = XilinxPartLookup.getPartFromPartName(part).getPartNameNoSpeedGrade();

		if (part.compareToIgnoreCase("XC5VFX100TFF1136") == 0) {
			_init(cell, 64000, 64000, 228, 32, 0, 12, 640, 256, 2, 2, 8, 3, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VFX100TFF1738") == 0) {
			_init(cell, 64000, 64000, 228, 32, 0, 12, 680, 256, 2, 2, 8, 3, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VFX130TFF1738") == 0) {
			_init(cell, 81920, 81920, 298, 32, 0, 12, 840, 320, 2, 3, 10, 3, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VFX200TFF1738") == 0) {
			_init(cell, 122880, 122880, 456, 32, 0, 12, 960, 384, 2, 4, 12, 4, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VFX30TFF665") == 0) {
			_init(cell, 20480, 20480, 68, 32, 0, 4, 360, 64, 1, 2, 4, 1, 1, 1, 2);
		}
		else if (part.compareToIgnoreCase("XC5VFX70TFF1136") == 0) {
			_init(cell, 44800, 44800, 148, 32, 0, 12, 640, 128, 1, 2, 8, 3, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VFX70TFF665") == 0) {
			_init(cell, 44800, 44800, 148, 32, 0, 12, 360, 128, 1, 2, 4, 3, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX110FF1153") == 0) {
			_init(cell, 69120, 69120, 128, 32, 0, 12, 800, 64, 0, 0, 0, 0, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX110FF1760") == 0) {
			_init(cell, 69120, 69120, 128, 32, 0, 12, 800, 64, 0, 0, 0, 0, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX110FF676") == 0) {
			_init(cell, 69120, 69120, 128, 32, 0, 12, 440, 64, 0, 0, 0, 0, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX110TFF1136") == 0) {
			_init(cell, 69120, 69120, 148, 32, 0, 12, 640, 64, 0, 2, 8, 1, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX110TFF1738") == 0) {
			_init(cell, 69120, 69120, 148, 32, 0, 12, 680, 64, 0, 2, 8, 1, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX155FF1153") == 0) {
			_init(cell, 97280, 97280, 192, 32, 0, 12, 800, 128, 0, 0, 0, 0, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX155FF1760") == 0) {
			_init(cell, 97280, 97280, 192, 32, 0, 12, 800, 128, 0, 0, 0, 0, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX155TFF1136") == 0) {
			_init(cell, 97280, 97280, 212, 32, 0, 12, 640, 128, 0, 2, 8, 1, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX155TFF1738") == 0) {
			_init(cell, 97280, 97280, 212, 32, 0, 12, 680, 128, 0, 2, 8, 1, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX20TFF323") == 0) {
			_init(cell, 12480, 12480, 26, 32, 0, 2, 172, 24, 0, 1, 2, 1, 1, 1, 1);
		}
		else if (part.compareToIgnoreCase("XC5VLX220FF1760") == 0) {
			_init(cell, 138240, 138240, 192, 32, 0, 12, 800, 128, 0, 0, 0, 0, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX220TFF1738") == 0) {
			_init(cell, 138240, 138240, 212, 32, 0, 12, 680, 128, 0, 2, 8, 1, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX30FF324") == 0) {
			_init(cell, 19200, 19200, 32, 32, 0, 4, 220, 32, 0, 0, 0, 0, 1, 1, 2);
		}
		else if (part.compareToIgnoreCase("XC5VLX30FF676") == 0) {
			_init(cell, 19200, 19200, 32, 32, 0, 4, 400, 32, 0, 0, 0, 0, 1, 1, 2);
		}
		else if (part.compareToIgnoreCase("XC5VLX30TFF323") == 0) {
			_init(cell, 19200, 19200, 36, 32, 0, 4, 172, 32, 0, 2, 2, 1, 1, 1, 2);
		}
		else if (part.compareToIgnoreCase("XC5VLX30TFF665") == 0) {
			_init(cell, 19200, 19200, 36, 32, 0, 4, 360, 32, 0, 2, 4, 1, 1, 1, 2);
		}
		else if (part.compareToIgnoreCase("XC5VLX330FF1760") == 0) {
			_init(cell, 207360, 207360, 288, 32, 0, 12, 1200, 192, 0, 0, 0, 0, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX330TFF1738") == 0) {
			_init(cell, 207360, 207360, 324, 32, 0, 12, 960, 192, 0, 2, 12, 1, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX50FF1153") == 0) {
			_init(cell, 28800, 28800, 48, 32, 0, 12, 560, 48, 0, 0, 0, 0, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX50FF324") == 0) {
			_init(cell, 28800, 28800, 48, 32, 0, 12, 220, 48, 0, 0, 0, 0, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX50FF676") == 0) {
			_init(cell, 28800, 28800, 48, 32, 0, 12, 440, 48, 0, 0, 0, 0, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX50TFF1136") == 0) {
			_init(cell, 28800, 28800, 60, 32, 0, 12, 480, 48, 0, 2, 6, 1, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX50TFF665") == 0) {
			_init(cell, 28800, 28800, 60, 32, 0, 12, 360, 48, 0, 2, 4, 1, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX85FF1153") == 0) {
			_init(cell, 51840, 51840, 96, 32, 0, 12, 560, 48, 0, 0, 0, 0, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX85FF676") == 0) {
			_init(cell, 51840, 51840, 96, 32, 0, 12, 440, 48, 0, 0, 0, 0, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VLX85TFF1136") == 0) {
			_init(cell, 51840, 51840, 108, 32, 0, 12, 480, 48, 0, 2, 6, 1, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VSX240TFF1738") == 0) {
			_init(cell, 149760, 149760, 516, 32, 0, 12, 960, 1056, 0, 2, 12, 1, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VSX35TFF665") == 0) {
			_init(cell, 21760, 21760, 84, 32, 0, 4, 360, 192, 0, 2, 4, 1, 1, 1, 2);
		}
		else if (part.compareToIgnoreCase("XC5VSX50TFF1136") == 0) {
			_init(cell, 32640, 32640, 132, 32, 0, 12, 480, 288, 0, 2, 6, 1, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VSX50TFF665") == 0) {
			_init(cell, 32640, 32640, 132, 32, 0, 12, 360, 288, 0, 2, 4, 1, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VSX95TFF1136") == 0) {
			_init(cell, 58880, 58880, 244, 32, 0, 12, 640, 640, 0, 2, 8, 1, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VTX150TFF1156") == 0) {
			_init(cell, 92800, 92800, 228, 32, 0, 12, 360, 80, 0, 2, 0, 1, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VTX150TFF1759") == 0) {
			_init(cell, 92800, 92800, 228, 32, 0, 12, 680, 80, 0, 2, 0, 1, 1, 1, 6);
		}
		else if (part.compareToIgnoreCase("XC5VTX240TFF1759") == 0) {
			_init(cell, 149760, 149760, 324, 32, 0, 12, 680, 96, 0, 2, 0, 1, 1, 1, 6);
		}
		else {
			throw new IllegalArgumentException("Part name " + part
				+ " does not match the specified Xilinx Virtex5 technology group.");
		}
	}

	protected void _init(EdifCell cell, int maxLUTs, int maxFFs, int maxBlockRAMs, int maxBUFG, int maxMult,
			int maxDCM, int maxIO, int maxDSPs, int maxPPC, int maxEthernet, int maxMGT, int maxPCIe, int maxICAP,
			int maxFrameECC, int maxPLL) throws OverutilizationEstimatedStopException,
			OverutilizationHardStopException, IllegalArgumentException {
		addResourceForTracking(XilinxResourceMapper.LUT, 0.0, maxLUTs);
		addResourceForTracking(XilinxResourceMapper.FF, 0.0, maxFFs);
		addResourceForTracking(XilinxResourceMapper.BRAM, 0.0, maxBlockRAMs);
		addResourceForTracking(XilinxResourceMapper.MULT, 0.0, maxMult);
		addResourceForTracking(XilinxResourceMapper.DCM, 0.0, maxDCM);
		addResourceForTracking(XilinxResourceMapper.IO, 0.0, maxIO);
		addResourceForTracking(XilinxResourceMapper.RES, 0.0, maxIO); // One per IOB
		addResourceForTracking(XilinxResourceMapper.BUFG, 0.0, maxBUFG);
		addResourceForTracking(XilinxResourceMapper.IBUFG, 0.0, maxBUFG);
		addResourceForTracking(XilinxResourceMapper.DSP, 0.0, maxDSPs);
		addResourceForTracking(XilinxResourceMapper.ICAP, 0.0, maxICAP);
		addResourceForTracking(XilinxResourceMapper.FRAME_ECC, 0.0, maxFrameECC);
		addResourceForTracking(XilinxResourceMapper.PPC, 0.0, maxPPC);
		addResourceForTracking(XilinxResourceMapper.ETHERNET, 0.0, maxEthernet);
		addResourceForTracking(XilinxResourceMapper.TRANSCEIVER, 0.0, maxMGT);
		addResourceForTracking(XilinxResourceMapper.PCIE, 0.0, maxPCIe);
		addResourceForTracking(XilinxResourceMapper.PLL, 0.0, maxPLL);
		super._init(cell);
	}
}
