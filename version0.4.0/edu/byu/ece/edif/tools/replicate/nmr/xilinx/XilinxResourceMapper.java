/*
 * Maps cells and cell instances to resource types and LUT equivalents.
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

import java.util.LinkedHashMap;
import java.util.Map;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.tools.replicate.nmr.ResourceMapper;

/////////////////////////////////////////////////////////////////////////
//// XilinxResourceMapper
/**
 * Maps cells and cell instances to resource types and LUT equivalents.
 * 
 * @author Keith Morgan
 * @version $Id: XilinxResourceMapper.java 151 2008-04-02 16:27:55Z
 * jamesfcarroll $
 */
public class XilinxResourceMapper implements ResourceMapper {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    public static double getApproxLutUsage(EdifCell cell) {
        if (cell == null)
            return 0;
        return (cellToLUTEquivalentMap.get(cell.getName().toUpperCase())).doubleValue();
    }

    public static double getApproxLUTUsage(EdifCellInstance eci) {
        if (eci == null)
            return 0;
        return getApproxLutUsage(eci.getCellType());
    }

    public static String getResourceType(EdifCell cell) {
        if (cell == null)
            return "";
        return cellToResourceMap.get(cell.getName().toUpperCase());
    }

    public static String getResourceType(EdifCellInstance eci) {
        if (eci == null)
            return "";
        return getResourceType(eci.getCellType());
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public variables                  ////

    /**
     * Block Ram
     */
    public static final String BRAM = "BRAM";

    /**
     * BUFG - Global clock buffer
     * <p>
     * <b>Note:</b>"In Virtex-II/II Pro, BUFG is available for instantiation,
     * but will be implemented with BUFGMUX." (Source: <a
     * href="http://toolbox.xilinx.com/docsan/xilinx4/data/docs/sim/vtex5.html">"Using
     * Global Clock Buffers"</a>)
     * <p>
     * "There are 32 global clock buffers in every Virtex-4 device." (Source: <a
     * href="direct.xilinx.com/bvdocs/userguides/ug070.pdf">UG070 Xilinx
     * Virtex-4 User Guide</a>)
     */
    public static final String BUFG = "BUFG";

    /**
     * Digital Clock Management / DLL
     * <p>
     * BHP: DCMs and DLLs are mutually exlusive as far as availability goes, so
     * they can be combined. Also, in later devices, a DLL in the source EDIF
     * will automatically be interpreted as a DCM, so it makes sense for them to
     * share the same resource type.
     */
    public static final String DCM = "DCM";

    public static final String DLL = "DCM";

    /**
     * Flip-Flop
     */
    public static final String FF = "FF";

    /**
     * Input / Output Pad
     */
    public static final String IO = "IO";

    /**
     * Look-Up Table
     */
    public static final String LUT = "LUT";

    /**
     * MGT
     */
    public static final String MGT = "MGT";

    /**
     * Dedicated Multiplier
     */
    public static final String MULT = "MULT";

    /**
     * Resistor/Keeper element
     */
    public static final String RES = "RES";

    /**
     * Power PC
     */
    public static final String PPC = "PPC";

    /**
     * DSP Element
     */
    public static final String DSP = "DSP";

    /**
     * V4-ICAP
     */
    public static final String ICAP = "ICAP";

    /**
     * V4 Frame ECC
     */
    public static final String FRAME_ECC = "ICAP";

    /**
     * Rocket IO Transeiver
     */
    public static final String TRANSEIVER = "TRANSEIVER";

    /**
     * Ethernet MACs
     */
    public static final String ETHERNET = "ETHERNET";

    ///////////////////////////////////////////////////////////////////
    ////                         protected variables               ////

    protected static final Map<String, String> cellToResourceMap;

    protected static final Map<String, Double> cellToLUTEquivalentMap;

    ///////////////////////////////////////////////////////////////////
    ////                         static initializers               ////

    static {
        cellToResourceMap = new LinkedHashMap<String, String>();
        cellToResourceMap.put("AND2", LUT);
        cellToResourceMap.put("AND2B1", LUT);
        cellToResourceMap.put("AND2B2", LUT);
        cellToResourceMap.put("AND3", LUT);
        cellToResourceMap.put("AND3B1", LUT);
        cellToResourceMap.put("AND3B2", LUT);
        cellToResourceMap.put("AND3B3", LUT);
        cellToResourceMap.put("AND4", LUT);
        cellToResourceMap.put("AND4B1", LUT);
        cellToResourceMap.put("AND4B2", LUT);
        cellToResourceMap.put("AND4B3", LUT);
        cellToResourceMap.put("AND4B4", LUT);
        cellToResourceMap.put("AND5", LUT);
        cellToResourceMap.put("AND5B1", LUT);
        cellToResourceMap.put("AND5B2", LUT);
        cellToResourceMap.put("AND5B3", LUT);
        cellToResourceMap.put("AND5B4", LUT);
        cellToResourceMap.put("AND5B5", LUT);
        cellToResourceMap.put("AND6", LUT);
        cellToResourceMap.put("AND7", LUT);
        cellToResourceMap.put("AND8", LUT);
        cellToResourceMap.put("BSCAN_FPGACORE", "");
        cellToResourceMap.put("BSCAN_SPARTAN2", "");
        cellToResourceMap.put("BSCAN_SPARTAN3", "");
        cellToResourceMap.put("BSCAN_VIRTEX", "");
        cellToResourceMap.put("BSCAN_VIRTEX2", "");
        cellToResourceMap.put("BUF", "");
        cellToResourceMap.put("BUFCF", "");
        cellToResourceMap.put("BUFE", "");
        cellToResourceMap.put("BUFFOE", "");
        cellToResourceMap.put("BUFG", "BUFG");
        cellToResourceMap.put("BUFGCE", "BUFG");
        cellToResourceMap.put("BUFGCE_1", "BUFG");
        cellToResourceMap.put("BUFGDLL", "BUFG");
        cellToResourceMap.put("BUFGMUX", "BUFG");
        cellToResourceMap.put("BUFGMUX_1", "BUFG");
        /*
         * Note: "In Spartan-II, Spartan-IIE, Spartan-3, Virtex, Virtex-E,
         * Virtex-II, Virtex-II Pro, and Virtex-II Pro X, BUFGP is equivalent to
         * an IBUFG driving a BUFG." (Xilinx Library Guide, ISE 8.1i)
         */
        cellToResourceMap.put("BUFGP", IO);
        cellToResourceMap.put("BUFGSR", "BUFG");
        cellToResourceMap.put("BUFGTS", "BUFG");
        cellToResourceMap.put("BUFT", "");
        cellToResourceMap.put("CAPTURE_FPGACORE", "");
        cellToResourceMap.put("CAPTURE_SPARTAN2", "");
        cellToResourceMap.put("CAPTURE_SPARTAN3", "");
        cellToResourceMap.put("CAPTURE_VIRTEX", "");
        cellToResourceMap.put("CAPTURE_VIRTEX2", "");
        cellToResourceMap.put("CLKDLL", DLL);
        cellToResourceMap.put("CLKDLLE", DLL);
        cellToResourceMap.put("CLKDLLHF", DLL);
        cellToResourceMap.put("CONFIG", "");
        cellToResourceMap.put("DCM", DCM);
        cellToResourceMap.put("FD", FF);
        cellToResourceMap.put("FD_1", FF);
        cellToResourceMap.put("FDC", FF);
        cellToResourceMap.put("FDC_1", FF);
        cellToResourceMap.put("FDCE", FF);
        cellToResourceMap.put("FDCE_1", FF);
        cellToResourceMap.put("FDCP", FF);
        cellToResourceMap.put("FDCP_1", FF);
        cellToResourceMap.put("FDCPE", FF);
        cellToResourceMap.put("FDCPE_1", FF);
        cellToResourceMap.put("FDCPX1", FF);
        cellToResourceMap.put("FDDRCPE", FF);
        cellToResourceMap.put("FDDRRSE", FF);
        cellToResourceMap.put("FDE", FF);
        cellToResourceMap.put("FDE_1", FF);
        cellToResourceMap.put("FDP", FF);
        cellToResourceMap.put("FDP_1", FF);
        cellToResourceMap.put("FDPE", FF);
        cellToResourceMap.put("FDPE_1", FF);
        cellToResourceMap.put("FDR", FF);
        cellToResourceMap.put("FDR_1", FF);
        cellToResourceMap.put("FDRE", FF);
        cellToResourceMap.put("FDRE_1", FF);
        cellToResourceMap.put("FDRS", FF);
        cellToResourceMap.put("FDRS_1", FF);
        cellToResourceMap.put("FDRSE", FF);
        cellToResourceMap.put("FDRSE_1", FF);
        cellToResourceMap.put("FDS", FF);
        cellToResourceMap.put("FDS_1", FF);
        cellToResourceMap.put("FDSE", FF);
        cellToResourceMap.put("FDSE_1", FF);
        cellToResourceMap.put("FMAP", LUT);
        cellToResourceMap.put("FTC", FF);
        cellToResourceMap.put("FTCP", FF);
        cellToResourceMap.put("FTP", FF);
        cellToResourceMap.put("GND", "");
        cellToResourceMap.put("IBUF", IO);
        cellToResourceMap.put("IBUF_AGP", IO);
        cellToResourceMap.put("IBUF_CTT", IO);
        cellToResourceMap.put("IBUF_GTL", IO);
        cellToResourceMap.put("IBUF_GTL_DCI", IO);
        cellToResourceMap.put("IBUF_GTLP", IO);
        cellToResourceMap.put("IBUF_GTLP_DCI", IO);
        cellToResourceMap.put("IBUF_HSTL_I", IO);
        cellToResourceMap.put("IBUF_HSTL_I_18", IO);
        cellToResourceMap.put("IBUF_HSTL_I_DCI", IO);
        cellToResourceMap.put("IBUF_HSTL_I_DCI_18", IO);
        cellToResourceMap.put("IBUF_HSTL_II", IO);
        cellToResourceMap.put("IBUF_HSTL_II_18", IO);
        cellToResourceMap.put("IBUF_HSTL_II_DCI", IO);
        cellToResourceMap.put("IBUF_HSTL_II_DCI_18", IO);
        cellToResourceMap.put("IBUF_HSTL_III", IO);
        cellToResourceMap.put("IBUF_HSTL_III_18", IO);
        cellToResourceMap.put("IBUF_HSTL_III_DCI", IO);
        cellToResourceMap.put("IBUF_HSTL_III_DCI_18", IO);
        cellToResourceMap.put("IBUF_HSTL_IV", IO);
        cellToResourceMap.put("IBUF_HSTL_IV_18", IO);
        cellToResourceMap.put("IBUF_HSTL_IV_DCI", IO);
        cellToResourceMap.put("IBUF_HSTL_IV_DCI_18", IO);
        cellToResourceMap.put("IBUF_LVCMOS12", IO);
        cellToResourceMap.put("IBUF_LVCMOS15", IO);
        cellToResourceMap.put("IBUF_LVCMOS18", IO);
        cellToResourceMap.put("IBUF_LVCMOS2", IO);
        cellToResourceMap.put("IBUF_LVCMOS25", IO);
        cellToResourceMap.put("IBUF_LVCMOS33", IO);
        cellToResourceMap.put("IBUF_LVDCI_15", IO);
        cellToResourceMap.put("IBUF_LVDCI_18", IO);
        cellToResourceMap.put("IBUF_LVDCI_25", IO);
        cellToResourceMap.put("IBUF_LVDCI_33", IO);
        cellToResourceMap.put("IBUF_LVDCI_DV2_15", IO);
        cellToResourceMap.put("IBUF_LVDCI_DV2_18", IO);
        cellToResourceMap.put("IBUF_LVDCI_DV2_25", IO);
        cellToResourceMap.put("IBUF_LVDCI_DV2_33", IO);
        cellToResourceMap.put("IBUF_LVDS", IO);
        cellToResourceMap.put("IBUF_LVPECL", IO);
        cellToResourceMap.put("IBUF_LVTTL", IO);
        cellToResourceMap.put("IBUF_PCI33_3", IO);
        cellToResourceMap.put("IBUF_PCI33_5", IO);
        cellToResourceMap.put("IBUF_PCI66_3", IO);
        cellToResourceMap.put("IBUF_PCIX", IO);
        cellToResourceMap.put("IBUF_PCIX66_3", IO);
        cellToResourceMap.put("IBUF_SSTL18_I", IO);
        cellToResourceMap.put("IBUF_SSTL18_I_DCI", IO);
        cellToResourceMap.put("IBUF_SSTL18_II", IO);
        cellToResourceMap.put("IBUF_SSTL18_II_DCI", IO);
        cellToResourceMap.put("IBUF_SSTL2_I", IO);
        cellToResourceMap.put("IBUF_SSTL2_I_DCI", IO);
        cellToResourceMap.put("IBUF_SSTL2_II", IO);
        cellToResourceMap.put("IBUF_SSTL2_II_DCI", IO);
        cellToResourceMap.put("IBUF_SSTL3_I", IO);
        cellToResourceMap.put("IBUF_SSTL3_I_DCI", IO);
        cellToResourceMap.put("IBUF_SSTL3_II", IO);
        cellToResourceMap.put("IBUF_SSTL3_II_DCI", IO);
        cellToResourceMap.put("IBUFDS", IO);
        cellToResourceMap.put("IBUFDS_BLVDS_25", IO);
        cellToResourceMap.put("IBUFDS_DIFF_OUT", IO);
        cellToResourceMap.put("IBUFDS_LDT_25", IO);
        cellToResourceMap.put("IBUFDS_LVDS_25", IO);
        cellToResourceMap.put("IBUFDS_LVDS_25_DCI", IO);
        cellToResourceMap.put("IBUFDS_LVDS_33", IO);
        cellToResourceMap.put("IBUFDS_LVDS_33_DCI", IO);
        cellToResourceMap.put("IBUFDS_LVDSEXT_25", IO);
        cellToResourceMap.put("IBUFDS_LVDSEXT_25_DCI", IO);
        cellToResourceMap.put("IBUFDS_LVDSEXT_33", IO);
        cellToResourceMap.put("IBUFDS_LVDSEXT_33_DCI", IO);
        cellToResourceMap.put("IBUFDS_LVPECL_25", IO);
        cellToResourceMap.put("IBUFDS_LVPECL_33", IO);
        cellToResourceMap.put("IBUFDS_ULVDS_25", IO);
        cellToResourceMap.put("IBUFG", IO);
        cellToResourceMap.put("IBUFG_AGP", IO);
        cellToResourceMap.put("IBUFG_CTT", IO);
        cellToResourceMap.put("IBUFG_GTL", IO);
        cellToResourceMap.put("IBUFG_GTL_DCI", IO);
        cellToResourceMap.put("IBUFG_GTLP", IO);
        cellToResourceMap.put("IBUFG_GTLP_DCI", IO);
        cellToResourceMap.put("IBUFG_HSTL_I", IO);
        cellToResourceMap.put("IBUFG_HSTL_I_18", IO);
        cellToResourceMap.put("IBUFG_HSTL_I_DCI", IO);
        cellToResourceMap.put("IBUFG_HSTL_I_DCI_18", IO);
        cellToResourceMap.put("IBUFG_HSTL_II", IO);
        cellToResourceMap.put("IBUFG_HSTL_II_18", IO);
        cellToResourceMap.put("IBUFG_HSTL_II_DCI", IO);
        cellToResourceMap.put("IBUFG_HSTL_II_DCI_18", IO);
        cellToResourceMap.put("IBUFG_HSTL_III", IO);
        cellToResourceMap.put("IBUFG_HSTL_III_18", IO);
        cellToResourceMap.put("IBUFG_HSTL_III_DCI", IO);
        cellToResourceMap.put("IBUFG_HSTL_III_DCI_18", IO);
        cellToResourceMap.put("IBUFG_HSTL_IV", IO);
        cellToResourceMap.put("IBUFG_HSTL_IV_18", IO);
        cellToResourceMap.put("IBUFG_HSTL_IV_DCI", IO);
        cellToResourceMap.put("IBUFG_HSTL_IV_DCI_18", IO);
        cellToResourceMap.put("IBUFG_LVCMOS12", IO);
        cellToResourceMap.put("IBUFG_LVCMOS15", IO);
        cellToResourceMap.put("IBUFG_LVCMOS18", IO);
        cellToResourceMap.put("IBUFG_LVCMOS2", IO);
        cellToResourceMap.put("IBUFG_LVCMOS25", IO);
        cellToResourceMap.put("IBUFG_LVCMOS33", IO);
        cellToResourceMap.put("IBUFG_LVDCI_15", IO);
        cellToResourceMap.put("IBUFG_LVDCI_18", IO);
        cellToResourceMap.put("IBUFG_LVDCI_25", IO);
        cellToResourceMap.put("IBUFG_LVDCI_33", IO);
        cellToResourceMap.put("IBUFG_LVDCI_DV2_15", IO);
        cellToResourceMap.put("IBUFG_LVDCI_DV2_18", IO);
        cellToResourceMap.put("IBUFG_LVDCI_DV2_25", IO);
        cellToResourceMap.put("IBUFG_LVDCI_DV2_33", IO);
        cellToResourceMap.put("IBUFG_LVDS", IO);
        cellToResourceMap.put("IBUFG_LVPECL", IO);
        cellToResourceMap.put("IBUFG_LVTTL", IO);
        cellToResourceMap.put("IBUFG_PCI33_3", IO);
        cellToResourceMap.put("IBUFG_PCI33_5", IO);
        cellToResourceMap.put("IBUFG_PCI66_3", IO);
        cellToResourceMap.put("IBUFG_PCIX", IO);
        cellToResourceMap.put("IBUFG_PCIX66_3", IO);
        cellToResourceMap.put("IBUFG_SSTL18_I", IO);
        cellToResourceMap.put("IBUFG_SSTL18_I_DCI", IO);
        cellToResourceMap.put("IBUFG_SSTL18_II", IO);
        cellToResourceMap.put("IBUFG_SSTL18_II_DCI", IO);
        cellToResourceMap.put("IBUFG_SSTL2_I", IO);
        cellToResourceMap.put("IBUFG_SSTL2_I_DCI", IO);
        cellToResourceMap.put("IBUFG_SSTL2_II", IO);
        cellToResourceMap.put("IBUFG_SSTL2_II_DCI", IO);
        cellToResourceMap.put("IBUFG_SSTL3_I", IO);
        cellToResourceMap.put("IBUFG_SSTL3_I_DCI", IO);
        cellToResourceMap.put("IBUFG_SSTL3_II", IO);
        cellToResourceMap.put("IBUFG_SSTL3_II_DCI", IO);
        cellToResourceMap.put("IBUFGDS", IO);
        cellToResourceMap.put("IBUFGDS_BLVDS_25", IO);
        cellToResourceMap.put("IBUFGDS_DIFF_OUT", IO);
        cellToResourceMap.put("IBUFGDS_LDT_25", IO);
        cellToResourceMap.put("IBUFGDS_LVDS_25", IO);
        cellToResourceMap.put("IBUFGDS_LVDS_25_DCI", IO);
        cellToResourceMap.put("IBUFGDS_LVDS_33", IO);
        cellToResourceMap.put("IBUFGDS_LVDS_33_DCI", IO);
        cellToResourceMap.put("IBUFGDS_LVDSEXT_25", IO);
        cellToResourceMap.put("IBUFGDS_LVDSEXT_25_DCI", IO);
        cellToResourceMap.put("IBUFGDS_LVDSEXT_33", IO);
        cellToResourceMap.put("IBUFGDS_LVDSEXT_33_DCI", IO);
        cellToResourceMap.put("IBUFGDS_LVPECL_25", IO);
        cellToResourceMap.put("IBUFGDS_LVPECL_33", IO);
        cellToResourceMap.put("IBUFGDS_ULVDS_25", IO);
        cellToResourceMap.put("ICAP_VIRTEX2", "");
        cellToResourceMap.put("IFDDRCPE", "");
        cellToResourceMap.put("IFDDRRSE", "");
        cellToResourceMap.put("ILD", "");
        cellToResourceMap.put("INV", "");
        cellToResourceMap.put("IOBUF", IO);
        cellToResourceMap.put("IOBUF_AGP", IO);
        cellToResourceMap.put("IOBUF_CTT", IO);
        cellToResourceMap.put("IOBUF_F_12", IO);
        cellToResourceMap.put("IOBUF_F_16", IO);
        cellToResourceMap.put("IOBUF_F_2", IO);
        cellToResourceMap.put("IOBUF_F_24", IO);
        cellToResourceMap.put("IOBUF_F_4", IO);
        cellToResourceMap.put("IOBUF_F_6", IO);
        cellToResourceMap.put("IOBUF_F_8", IO);
        cellToResourceMap.put("IOBUF_GTL", IO);
        cellToResourceMap.put("IOBUF_GTL_DCI", IO);
        cellToResourceMap.put("IOBUF_GTLP", IO);
        cellToResourceMap.put("IOBUF_GTLP_DCI", IO);
        cellToResourceMap.put("IOBUF_HSTL_I", IO);
        cellToResourceMap.put("IOBUF_HSTL_I_18", IO);
        cellToResourceMap.put("IOBUF_HSTL_II", IO);
        cellToResourceMap.put("IOBUF_HSTL_II_18", IO);
        cellToResourceMap.put("IOBUF_HSTL_II_DCI", IO);
        cellToResourceMap.put("IOBUF_HSTL_II_DCI_18", IO);
        cellToResourceMap.put("IOBUF_HSTL_III", IO);
        cellToResourceMap.put("IOBUF_HSTL_III_18", IO);
        cellToResourceMap.put("IOBUF_HSTL_IV", IO);
        cellToResourceMap.put("IOBUF_HSTL_IV_18", IO);
        cellToResourceMap.put("IOBUF_HSTL_IV_DCI", IO);
        cellToResourceMap.put("IOBUF_HSTL_IV_DCI_18", IO);
        cellToResourceMap.put("IOBUF_LVCMOS12", IO);
        cellToResourceMap.put("IOBUF_LVCMOS12_F_2", IO);
        cellToResourceMap.put("IOBUF_LVCMOS12_F_4", IO);
        cellToResourceMap.put("IOBUF_LVCMOS12_F_6", IO);
        cellToResourceMap.put("IOBUF_LVCMOS12_F_8", IO);
        cellToResourceMap.put("IOBUF_LVCMOS12_S_2", IO);
        cellToResourceMap.put("IOBUF_LVCMOS12_S_4", IO);
        cellToResourceMap.put("IOBUF_LVCMOS12_S_6", IO);
        cellToResourceMap.put("IOBUF_LVCMOS12_S_8", IO);
        cellToResourceMap.put("IOBUF_LVCMOS15", IO);
        cellToResourceMap.put("IOBUF_LVCMOS15_F_12", IO);
        cellToResourceMap.put("IOBUF_LVCMOS15_F_16", IO);
        cellToResourceMap.put("IOBUF_LVCMOS15_F_2", IO);
        cellToResourceMap.put("IOBUF_LVCMOS15_F_4", IO);
        cellToResourceMap.put("IOBUF_LVCMOS15_F_6", IO);
        cellToResourceMap.put("IOBUF_LVCMOS15_F_8", IO);
        cellToResourceMap.put("IOBUF_LVCMOS15_S_12", IO);
        cellToResourceMap.put("IOBUF_LVCMOS15_S_16", IO);
        cellToResourceMap.put("IOBUF_LVCMOS15_S_2", IO);
        cellToResourceMap.put("IOBUF_LVCMOS15_S_4", IO);
        cellToResourceMap.put("IOBUF_LVCMOS15_S_6", IO);
        cellToResourceMap.put("IOBUF_LVCMOS15_S_8", IO);
        cellToResourceMap.put("IOBUF_LVCMOS18", IO);
        cellToResourceMap.put("IOBUF_LVCMOS18_F_12", IO);
        cellToResourceMap.put("IOBUF_LVCMOS18_F_16", IO);
        cellToResourceMap.put("IOBUF_LVCMOS18_F_2", IO);
        cellToResourceMap.put("IOBUF_LVCMOS18_F_4", IO);
        cellToResourceMap.put("IOBUF_LVCMOS18_F_6", IO);
        cellToResourceMap.put("IOBUF_LVCMOS18_F_8", IO);
        cellToResourceMap.put("IOBUF_LVCMOS18_S_12", IO);
        cellToResourceMap.put("IOBUF_LVCMOS18_S_16", IO);
        cellToResourceMap.put("IOBUF_LVCMOS18_S_2", IO);
        cellToResourceMap.put("IOBUF_LVCMOS18_S_4", IO);
        cellToResourceMap.put("IOBUF_LVCMOS18_S_6", IO);
        cellToResourceMap.put("IOBUF_LVCMOS18_S_8", IO);
        cellToResourceMap.put("IOBUF_LVCMOS2", IO);
        cellToResourceMap.put("IOBUF_LVCMOS25", IO);
        cellToResourceMap.put("IOBUF_LVCMOS25_F_12", IO);
        cellToResourceMap.put("IOBUF_LVCMOS25_F_16", IO);
        cellToResourceMap.put("IOBUF_LVCMOS25_F_2", IO);
        cellToResourceMap.put("IOBUF_LVCMOS25_F_24", IO);
        cellToResourceMap.put("IOBUF_LVCMOS25_F_4", IO);
        cellToResourceMap.put("IOBUF_LVCMOS25_F_6", IO);
        cellToResourceMap.put("IOBUF_LVCMOS25_F_8", IO);
        cellToResourceMap.put("IOBUF_LVCMOS25_S_12", IO);
        cellToResourceMap.put("IOBUF_LVCMOS25_S_16", IO);
        cellToResourceMap.put("IOBUF_LVCMOS25_S_2", IO);
        cellToResourceMap.put("IOBUF_LVCMOS25_S_24", IO);
        cellToResourceMap.put("IOBUF_LVCMOS25_S_4", IO);
        cellToResourceMap.put("IOBUF_LVCMOS25_S_6", IO);
        cellToResourceMap.put("IOBUF_LVCMOS25_S_8", IO);
        cellToResourceMap.put("IOBUF_LVCMOS33", IO);
        cellToResourceMap.put("IOBUF_LVCMOS33_F_12", IO);
        cellToResourceMap.put("IOBUF_LVCMOS33_F_16", IO);
        cellToResourceMap.put("IOBUF_LVCMOS33_F_2", IO);
        cellToResourceMap.put("IOBUF_LVCMOS33_F_24", IO);
        cellToResourceMap.put("IOBUF_LVCMOS33_F_4", IO);
        cellToResourceMap.put("IOBUF_LVCMOS33_F_6", IO);
        cellToResourceMap.put("IOBUF_LVCMOS33_F_8", IO);
        cellToResourceMap.put("IOBUF_LVCMOS33_S_12", IO);
        cellToResourceMap.put("IOBUF_LVCMOS33_S_16", IO);
        cellToResourceMap.put("IOBUF_LVCMOS33_S_2", IO);
        cellToResourceMap.put("IOBUF_LVCMOS33_S_24", IO);
        cellToResourceMap.put("IOBUF_LVCMOS33_S_4", IO);
        cellToResourceMap.put("IOBUF_LVCMOS33_S_6", IO);
        cellToResourceMap.put("IOBUF_LVCMOS33_S_8", IO);
        cellToResourceMap.put("IOBUF_LVDCI_15", IO);
        cellToResourceMap.put("IOBUF_LVDCI_18", IO);
        cellToResourceMap.put("IOBUF_LVDCI_25", IO);
        cellToResourceMap.put("IOBUF_LVDCI_33", IO);
        cellToResourceMap.put("IOBUF_LVDCI_DV2_15", IO);
        cellToResourceMap.put("IOBUF_LVDCI_DV2_18", IO);
        cellToResourceMap.put("IOBUF_LVDCI_DV2_25", IO);
        cellToResourceMap.put("IOBUF_LVDCI_DV2_33", IO);
        cellToResourceMap.put("IOBUF_LVDS", IO);
        cellToResourceMap.put("IOBUF_LVPECL", IO);
        cellToResourceMap.put("IOBUF_LVTTL", IO);
        cellToResourceMap.put("IOBUF_LVTTL_F_12", IO);
        cellToResourceMap.put("IOBUF_LVTTL_F_16", IO);
        cellToResourceMap.put("IOBUF_LVTTL_F_2", IO);
        cellToResourceMap.put("IOBUF_LVTTL_F_24", IO);
        cellToResourceMap.put("IOBUF_LVTTL_F_4", IO);
        cellToResourceMap.put("IOBUF_LVTTL_F_6", IO);
        cellToResourceMap.put("IOBUF_LVTTL_F_8", IO);
        cellToResourceMap.put("IOBUF_LVTTL_S_12", IO);
        cellToResourceMap.put("IOBUF_LVTTL_S_16", IO);
        cellToResourceMap.put("IOBUF_LVTTL_S_2", IO);
        cellToResourceMap.put("IOBUF_LVTTL_S_24", IO);
        cellToResourceMap.put("IOBUF_LVTTL_S_4", IO);
        cellToResourceMap.put("IOBUF_LVTTL_S_6", IO);
        cellToResourceMap.put("IOBUF_LVTTL_S_8", IO);
        cellToResourceMap.put("IOBUF_PCI33_3", IO);
        cellToResourceMap.put("IOBUF_PCI33_5", IO);
        cellToResourceMap.put("IOBUF_PCI66_3", IO);
        cellToResourceMap.put("IOBUF_PCIX", IO);
        cellToResourceMap.put("IOBUF_PCIX66_3", IO);
        cellToResourceMap.put("IOBUF_S_12", IO);
        cellToResourceMap.put("IOBUF_S_16", IO);
        cellToResourceMap.put("IOBUF_S_2", IO);
        cellToResourceMap.put("IOBUF_S_24", IO);
        cellToResourceMap.put("IOBUF_S_4", IO);
        cellToResourceMap.put("IOBUF_S_6", IO);
        cellToResourceMap.put("IOBUF_S_8", IO);
        cellToResourceMap.put("IOBUF_SSTL18_I", IO);
        cellToResourceMap.put("IOBUF_SSTL18_II", IO);
        cellToResourceMap.put("IOBUF_SSTL18_II_DCI", IO);
        cellToResourceMap.put("IOBUF_SSTL2_I", IO);
        cellToResourceMap.put("IOBUF_SSTL2_II", IO);
        cellToResourceMap.put("IOBUF_SSTL2_II_DCI", IO);
        cellToResourceMap.put("IOBUF_SSTL3_I", IO);
        cellToResourceMap.put("IOBUF_SSTL3_II", IO);
        cellToResourceMap.put("IOBUF_SSTL3_II_DCI", IO);
        cellToResourceMap.put("IOBUFDS", IO);
        cellToResourceMap.put("IOBUFDS_BLVDS_25", IO);
        cellToResourceMap.put("IOBUFE", IO);
        cellToResourceMap.put("IOBUFE_F", IO);
        cellToResourceMap.put("IOBUFE_S", IO);
        cellToResourceMap.put("KEEP", RES);
        cellToResourceMap.put("KEEPER", RES);
        cellToResourceMap.put("LD", FF);
        cellToResourceMap.put("LD_1", FF);
        cellToResourceMap.put("LDC", FF);
        cellToResourceMap.put("LDC_1", FF);
        cellToResourceMap.put("LDCE", FF);
        cellToResourceMap.put("LDCE_1", FF);
        cellToResourceMap.put("LDCP", FF);
        cellToResourceMap.put("LDCP_1", FF);
        cellToResourceMap.put("LDCPE", FF);
        cellToResourceMap.put("LDCPE_1", FF);
        cellToResourceMap.put("LDE", FF);
        cellToResourceMap.put("LDE_1", FF);
        cellToResourceMap.put("LDP", FF);
        cellToResourceMap.put("LDP_1", FF);
        cellToResourceMap.put("LDPE", FF);
        cellToResourceMap.put("LDPE_1", FF);
        cellToResourceMap.put("LUT1", LUT);
        cellToResourceMap.put("LUT1_D", LUT);
        cellToResourceMap.put("LUT1_L", LUT);
        cellToResourceMap.put("LUT2", LUT);
        cellToResourceMap.put("LUT2_D", LUT);
        cellToResourceMap.put("LUT2_L", LUT);
        cellToResourceMap.put("LUT3", LUT);
        cellToResourceMap.put("LUT3_D", LUT);
        cellToResourceMap.put("LUT3_L", LUT);
        cellToResourceMap.put("LUT4", LUT);
        cellToResourceMap.put("LUT4_D", LUT);
        cellToResourceMap.put("LUT4_L", LUT);
        cellToResourceMap.put("MERGE", "");
        cellToResourceMap.put("MIN_OFF", "");
        cellToResourceMap.put("MULT18X18", MULT);
        cellToResourceMap.put("MULT18X18S", MULT);
        cellToResourceMap.put("MULT_AND", "");
        cellToResourceMap.put("MUXCY", "");
        cellToResourceMap.put("MUXCY_D", "");
        cellToResourceMap.put("MUXCY_L", "");
        cellToResourceMap.put("MUXF5", "");
        cellToResourceMap.put("MUXF5_D", "");
        cellToResourceMap.put("MUXF5_L", "");
        cellToResourceMap.put("MUXF6", "");
        cellToResourceMap.put("MUXF6_D", "");
        cellToResourceMap.put("MUXF6_L", "");
        cellToResourceMap.put("MUXF7", "");
        cellToResourceMap.put("MUXF7_D", "");
        cellToResourceMap.put("MUXF7_L", "");
        cellToResourceMap.put("MUXF8", "");
        cellToResourceMap.put("MUXF8_D", "");
        cellToResourceMap.put("MUXF8_L", "");
        cellToResourceMap.put("NAND2", LUT);
        cellToResourceMap.put("NAND2B1", LUT);
        cellToResourceMap.put("NAND2B2", LUT);
        cellToResourceMap.put("NAND3", LUT);
        cellToResourceMap.put("NAND3B1", LUT);
        cellToResourceMap.put("NAND3B2", LUT);
        cellToResourceMap.put("NAND3B3", LUT);
        cellToResourceMap.put("NAND4", LUT);
        cellToResourceMap.put("NAND4B1", LUT);
        cellToResourceMap.put("NAND4B2", LUT);
        cellToResourceMap.put("NAND4B3", LUT);
        cellToResourceMap.put("NAND4B4", LUT);
        cellToResourceMap.put("NAND5", LUT);
        cellToResourceMap.put("NAND5B1", LUT);
        cellToResourceMap.put("NAND5B2", LUT);
        cellToResourceMap.put("NAND5B3", LUT);
        cellToResourceMap.put("NAND5B4", LUT);
        cellToResourceMap.put("NAND5B5", LUT);
        cellToResourceMap.put("NOR2", LUT);
        cellToResourceMap.put("NOR2B1", LUT);
        cellToResourceMap.put("NOR2B2", LUT);
        cellToResourceMap.put("NOR3", LUT);
        cellToResourceMap.put("NOR3B1", LUT);
        cellToResourceMap.put("NOR3B2", LUT);
        cellToResourceMap.put("NOR3B3", LUT);
        cellToResourceMap.put("NOR4", LUT);
        cellToResourceMap.put("NOR4B1", LUT);
        cellToResourceMap.put("NOR4B2", LUT);
        cellToResourceMap.put("NOR4B3", LUT);
        cellToResourceMap.put("NOR4B4", LUT);
        cellToResourceMap.put("NOR5", LUT);
        cellToResourceMap.put("NOR5B1", LUT);
        cellToResourceMap.put("NOR5B2", LUT);
        cellToResourceMap.put("NOR5B3", LUT);
        cellToResourceMap.put("NOR5B4", LUT);
        cellToResourceMap.put("NOR5B5", LUT);
        cellToResourceMap.put("OBUF", IO);
        cellToResourceMap.put("OBUF_AGP", IO);
        cellToResourceMap.put("OBUF_CTT", IO);
        cellToResourceMap.put("OBUF_F_12", IO);
        cellToResourceMap.put("OBUF_F_16", IO);
        cellToResourceMap.put("OBUF_F_2", IO);
        cellToResourceMap.put("OBUF_F_24", IO);
        cellToResourceMap.put("OBUF_F_4", IO);
        cellToResourceMap.put("OBUF_F_6", IO);
        cellToResourceMap.put("OBUF_F_8", IO);
        cellToResourceMap.put("OBUF_GTL", IO);
        cellToResourceMap.put("OBUF_GTL_DCI", IO);
        cellToResourceMap.put("OBUF_GTLP", IO);
        cellToResourceMap.put("OBUF_GTLP_DCI", IO);
        cellToResourceMap.put("OBUF_HSTL_I", IO);
        cellToResourceMap.put("OBUF_HSTL_I_18", IO);
        cellToResourceMap.put("OBUF_HSTL_I_DCI", IO);
        cellToResourceMap.put("OBUF_HSTL_I_DCI_18", IO);
        cellToResourceMap.put("OBUF_HSTL_II", IO);
        cellToResourceMap.put("OBUF_HSTL_II_18", IO);
        cellToResourceMap.put("OBUF_HSTL_II_DCI", IO);
        cellToResourceMap.put("OBUF_HSTL_II_DCI_18", IO);
        cellToResourceMap.put("OBUF_HSTL_III", IO);
        cellToResourceMap.put("OBUF_HSTL_III_18", IO);
        cellToResourceMap.put("OBUF_HSTL_III_DCI", IO);
        cellToResourceMap.put("OBUF_HSTL_III_DCI_18", IO);
        cellToResourceMap.put("OBUF_HSTL_IV", IO);
        cellToResourceMap.put("OBUF_HSTL_IV_18", IO);
        cellToResourceMap.put("OBUF_HSTL_IV_DCI", IO);
        cellToResourceMap.put("OBUF_HSTL_IV_DCI_18", IO);
        cellToResourceMap.put("OBUF_LVCMOS12", IO);
        cellToResourceMap.put("OBUF_LVCMOS12_F_2", IO);
        cellToResourceMap.put("OBUF_LVCMOS12_F_4", IO);
        cellToResourceMap.put("OBUF_LVCMOS12_F_6", IO);
        cellToResourceMap.put("OBUF_LVCMOS12_F_8", IO);
        cellToResourceMap.put("OBUF_LVCMOS12_S_2", IO);
        cellToResourceMap.put("OBUF_LVCMOS12_S_4", IO);
        cellToResourceMap.put("OBUF_LVCMOS12_S_6", IO);
        cellToResourceMap.put("OBUF_LVCMOS12_S_8", IO);
        cellToResourceMap.put("OBUF_LVCMOS15", IO);
        cellToResourceMap.put("OBUF_LVCMOS15_F_12", IO);
        cellToResourceMap.put("OBUF_LVCMOS15_F_16", IO);
        cellToResourceMap.put("OBUF_LVCMOS15_F_2", IO);
        cellToResourceMap.put("OBUF_LVCMOS15_F_4", IO);
        cellToResourceMap.put("OBUF_LVCMOS15_F_6", IO);
        cellToResourceMap.put("OBUF_LVCMOS15_F_8", IO);
        cellToResourceMap.put("OBUF_LVCMOS15_S_12", IO);
        cellToResourceMap.put("OBUF_LVCMOS15_S_16", IO);
        cellToResourceMap.put("OBUF_LVCMOS15_S_2", IO);
        cellToResourceMap.put("OBUF_LVCMOS15_S_4", IO);
        cellToResourceMap.put("OBUF_LVCMOS15_S_6", IO);
        cellToResourceMap.put("OBUF_LVCMOS15_S_8", IO);
        cellToResourceMap.put("OBUF_LVCMOS18", IO);
        cellToResourceMap.put("OBUF_LVCMOS18_F_12", IO);
        cellToResourceMap.put("OBUF_LVCMOS18_F_16", IO);
        cellToResourceMap.put("OBUF_LVCMOS18_F_2", IO);
        cellToResourceMap.put("OBUF_LVCMOS18_F_4", IO);
        cellToResourceMap.put("OBUF_LVCMOS18_F_6", IO);
        cellToResourceMap.put("OBUF_LVCMOS18_F_8", IO);
        cellToResourceMap.put("OBUF_LVCMOS18_S_12", IO);
        cellToResourceMap.put("OBUF_LVCMOS18_S_16", IO);
        cellToResourceMap.put("OBUF_LVCMOS18_S_2", IO);
        cellToResourceMap.put("OBUF_LVCMOS18_S_4", IO);
        cellToResourceMap.put("OBUF_LVCMOS18_S_6", IO);
        cellToResourceMap.put("OBUF_LVCMOS18_S_8", IO);
        cellToResourceMap.put("OBUF_LVCMOS2", IO);
        cellToResourceMap.put("OBUF_LVCMOS25", IO);
        cellToResourceMap.put("OBUF_LVCMOS25_F_12", IO);
        cellToResourceMap.put("OBUF_LVCMOS25_F_16", IO);
        cellToResourceMap.put("OBUF_LVCMOS25_F_2", IO);
        cellToResourceMap.put("OBUF_LVCMOS25_F_24", IO);
        cellToResourceMap.put("OBUF_LVCMOS25_F_4", IO);
        cellToResourceMap.put("OBUF_LVCMOS25_F_6", IO);
        cellToResourceMap.put("OBUF_LVCMOS25_F_8", IO);
        cellToResourceMap.put("OBUF_LVCMOS25_S_12", IO);
        cellToResourceMap.put("OBUF_LVCMOS25_S_16", IO);
        cellToResourceMap.put("OBUF_LVCMOS25_S_2", IO);
        cellToResourceMap.put("OBUF_LVCMOS25_S_24", IO);
        cellToResourceMap.put("OBUF_LVCMOS25_S_4", IO);
        cellToResourceMap.put("OBUF_LVCMOS25_S_6", IO);
        cellToResourceMap.put("OBUF_LVCMOS25_S_8", IO);
        cellToResourceMap.put("OBUF_LVCMOS33", IO);
        cellToResourceMap.put("OBUF_LVCMOS33_F_12", IO);
        cellToResourceMap.put("OBUF_LVCMOS33_F_16", IO);
        cellToResourceMap.put("OBUF_LVCMOS33_F_2", IO);
        cellToResourceMap.put("OBUF_LVCMOS33_F_24", IO);
        cellToResourceMap.put("OBUF_LVCMOS33_F_4", IO);
        cellToResourceMap.put("OBUF_LVCMOS33_F_6", IO);
        cellToResourceMap.put("OBUF_LVCMOS33_F_8", IO);
        cellToResourceMap.put("OBUF_LVCMOS33_S_12", IO);
        cellToResourceMap.put("OBUF_LVCMOS33_S_16", IO);
        cellToResourceMap.put("OBUF_LVCMOS33_S_2", IO);
        cellToResourceMap.put("OBUF_LVCMOS33_S_24", IO);
        cellToResourceMap.put("OBUF_LVCMOS33_S_4", IO);
        cellToResourceMap.put("OBUF_LVCMOS33_S_6", IO);
        cellToResourceMap.put("OBUF_LVCMOS33_S_8", IO);
        cellToResourceMap.put("OBUF_LVDCI_15", IO);
        cellToResourceMap.put("OBUF_LVDCI_18", IO);
        cellToResourceMap.put("OBUF_LVDCI_25", IO);
        cellToResourceMap.put("OBUF_LVDCI_33", IO);
        cellToResourceMap.put("OBUF_LVDCI_DV2_15", IO);
        cellToResourceMap.put("OBUF_LVDCI_DV2_18", IO);
        cellToResourceMap.put("OBUF_LVDCI_DV2_25", IO);
        cellToResourceMap.put("OBUF_LVDCI_DV2_33", IO);
        cellToResourceMap.put("OBUF_LVDS", IO);
        cellToResourceMap.put("OBUF_LVPECL", IO);
        cellToResourceMap.put("OBUF_LVTTL", IO);
        cellToResourceMap.put("OBUF_LVTTL_F_12", IO);
        cellToResourceMap.put("OBUF_LVTTL_F_16", IO);
        cellToResourceMap.put("OBUF_LVTTL_F_2", IO);
        cellToResourceMap.put("OBUF_LVTTL_F_24", IO);
        cellToResourceMap.put("OBUF_LVTTL_F_4", IO);
        cellToResourceMap.put("OBUF_LVTTL_F_6", IO);
        cellToResourceMap.put("OBUF_LVTTL_F_8", IO);
        cellToResourceMap.put("OBUF_LVTTL_S_12", IO);
        cellToResourceMap.put("OBUF_LVTTL_S_16", IO);
        cellToResourceMap.put("OBUF_LVTTL_S_2", IO);
        cellToResourceMap.put("OBUF_LVTTL_S_24", IO);
        cellToResourceMap.put("OBUF_LVTTL_S_4", IO);
        cellToResourceMap.put("OBUF_LVTTL_S_6", IO);
        cellToResourceMap.put("OBUF_LVTTL_S_8", IO);
        cellToResourceMap.put("OBUF_PCI33_3", IO);
        cellToResourceMap.put("OBUF_PCI33_5", IO);
        cellToResourceMap.put("OBUF_PCI66_3", IO);
        cellToResourceMap.put("OBUF_PCIX", IO);
        cellToResourceMap.put("OBUF_PCIX66_3", IO);
        cellToResourceMap.put("OBUF_S_12", IO);
        cellToResourceMap.put("OBUF_S_16", IO);
        cellToResourceMap.put("OBUF_S_2", IO);
        cellToResourceMap.put("OBUF_S_24", IO);
        cellToResourceMap.put("OBUF_S_4", IO);
        cellToResourceMap.put("OBUF_S_6", IO);
        cellToResourceMap.put("OBUF_S_8", IO);
        cellToResourceMap.put("OBUF_SSTL18_I", IO);
        cellToResourceMap.put("OBUF_SSTL18_I_DCI", IO);
        cellToResourceMap.put("OBUF_SSTL18_II", IO);
        cellToResourceMap.put("OBUF_SSTL18_II_DCI", IO);
        cellToResourceMap.put("OBUF_SSTL2_I", IO);
        cellToResourceMap.put("OBUF_SSTL2_I_DCI", IO);
        cellToResourceMap.put("OBUF_SSTL2_II", IO);
        cellToResourceMap.put("OBUF_SSTL2_II_DCI", IO);
        cellToResourceMap.put("OBUF_SSTL3_I", IO);
        cellToResourceMap.put("OBUF_SSTL3_I_DCI", IO);
        cellToResourceMap.put("OBUF_SSTL3_II", IO);
        cellToResourceMap.put("OBUF_SSTL3_II_DCI", IO);
        cellToResourceMap.put("OBUFDS", IO);
        cellToResourceMap.put("OBUFDS_BLVDS_25", IO);
        cellToResourceMap.put("OBUFDS_LDT_25", IO);
        cellToResourceMap.put("OBUFDS_LVDS_25", IO);
        cellToResourceMap.put("OBUFDS_LVDS_33", IO);
        cellToResourceMap.put("OBUFDS_LVDSEXT_25", IO);
        cellToResourceMap.put("OBUFDS_LVDSEXT_33", IO);
        cellToResourceMap.put("OBUFDS_LVPECL_25", IO);
        cellToResourceMap.put("OBUFDS_LVPECL_33", IO);
        cellToResourceMap.put("OBUFDS_ULVDS_25", IO);
        cellToResourceMap.put("OBUFE", IO);
        cellToResourceMap.put("OBUFT", IO);
        cellToResourceMap.put("OBUFT_AGP", IO);
        cellToResourceMap.put("OBUFT_CTT", IO);
        cellToResourceMap.put("OBUFT_F_12", IO);
        cellToResourceMap.put("OBUFT_F_16", IO);
        cellToResourceMap.put("OBUFT_F_2", IO);
        cellToResourceMap.put("OBUFT_F_24", IO);
        cellToResourceMap.put("OBUFT_F_4", IO);
        cellToResourceMap.put("OBUFT_F_6", IO);
        cellToResourceMap.put("OBUFT_F_8", IO);
        cellToResourceMap.put("OBUFT_GTL", IO);
        cellToResourceMap.put("OBUFT_GTL_DCI", IO);
        cellToResourceMap.put("OBUFT_GTLP", IO);
        cellToResourceMap.put("OBUFT_GTLP_DCI", IO);
        cellToResourceMap.put("OBUFT_HSTL_I", IO);
        cellToResourceMap.put("OBUFT_HSTL_I_18", IO);
        cellToResourceMap.put("OBUFT_HSTL_I_DCI", IO);
        cellToResourceMap.put("OBUFT_HSTL_I_DCI_18", IO);
        cellToResourceMap.put("OBUFT_HSTL_II", IO);
        cellToResourceMap.put("OBUFT_HSTL_II_18", IO);
        cellToResourceMap.put("OBUFT_HSTL_II_DCI", IO);
        cellToResourceMap.put("OBUFT_HSTL_II_DCI_18", IO);
        cellToResourceMap.put("OBUFT_HSTL_III", IO);
        cellToResourceMap.put("OBUFT_HSTL_III_18", IO);
        cellToResourceMap.put("OBUFT_HSTL_III_DCI", IO);
        cellToResourceMap.put("OBUFT_HSTL_III_DCI_18", IO);
        cellToResourceMap.put("OBUFT_HSTL_IV", IO);
        cellToResourceMap.put("OBUFT_HSTL_IV_18", IO);
        cellToResourceMap.put("OBUFT_HSTL_IV_DCI", IO);
        cellToResourceMap.put("OBUFT_HSTL_IV_DCI_18", IO);
        cellToResourceMap.put("OBUFT_LVCMOS12", IO);
        cellToResourceMap.put("OBUFT_LVCMOS12_F_2", IO);
        cellToResourceMap.put("OBUFT_LVCMOS12_F_4", IO);
        cellToResourceMap.put("OBUFT_LVCMOS12_F_6", IO);
        cellToResourceMap.put("OBUFT_LVCMOS12_F_8", IO);
        cellToResourceMap.put("OBUFT_LVCMOS12_S_2", IO);
        cellToResourceMap.put("OBUFT_LVCMOS12_S_4", IO);
        cellToResourceMap.put("OBUFT_LVCMOS12_S_6", IO);
        cellToResourceMap.put("OBUFT_LVCMOS12_S_8", IO);
        cellToResourceMap.put("OBUFT_LVCMOS15", IO);
        cellToResourceMap.put("OBUFT_LVCMOS15_F_12", IO);
        cellToResourceMap.put("OBUFT_LVCMOS15_F_16", IO);
        cellToResourceMap.put("OBUFT_LVCMOS15_F_2", IO);
        cellToResourceMap.put("OBUFT_LVCMOS15_F_4", IO);
        cellToResourceMap.put("OBUFT_LVCMOS15_F_6", IO);
        cellToResourceMap.put("OBUFT_LVCMOS15_F_8", IO);
        cellToResourceMap.put("OBUFT_LVCMOS15_S_12", IO);
        cellToResourceMap.put("OBUFT_LVCMOS15_S_16", IO);
        cellToResourceMap.put("OBUFT_LVCMOS15_S_2", IO);
        cellToResourceMap.put("OBUFT_LVCMOS15_S_4", IO);
        cellToResourceMap.put("OBUFT_LVCMOS15_S_6", IO);
        cellToResourceMap.put("OBUFT_LVCMOS15_S_8", IO);
        cellToResourceMap.put("OBUFT_LVCMOS18", IO);
        cellToResourceMap.put("OBUFT_LVCMOS18_F_12", IO);
        cellToResourceMap.put("OBUFT_LVCMOS18_F_16", IO);
        cellToResourceMap.put("OBUFT_LVCMOS18_F_2", IO);
        cellToResourceMap.put("OBUFT_LVCMOS18_F_4", IO);
        cellToResourceMap.put("OBUFT_LVCMOS18_F_6", IO);
        cellToResourceMap.put("OBUFT_LVCMOS18_F_8", IO);
        cellToResourceMap.put("OBUFT_LVCMOS18_S_12", IO);
        cellToResourceMap.put("OBUFT_LVCMOS18_S_16", IO);
        cellToResourceMap.put("OBUFT_LVCMOS18_S_2", IO);
        cellToResourceMap.put("OBUFT_LVCMOS18_S_4", IO);
        cellToResourceMap.put("OBUFT_LVCMOS18_S_6", IO);
        cellToResourceMap.put("OBUFT_LVCMOS18_S_8", IO);
        cellToResourceMap.put("OBUFT_LVCMOS2", IO);
        cellToResourceMap.put("OBUFT_LVCMOS25", IO);
        cellToResourceMap.put("OBUFT_LVCMOS25_F_12", IO);
        cellToResourceMap.put("OBUFT_LVCMOS25_F_16", IO);
        cellToResourceMap.put("OBUFT_LVCMOS25_F_2", IO);
        cellToResourceMap.put("OBUFT_LVCMOS25_F_24", IO);
        cellToResourceMap.put("OBUFT_LVCMOS25_F_4", IO);
        cellToResourceMap.put("OBUFT_LVCMOS25_F_6", IO);
        cellToResourceMap.put("OBUFT_LVCMOS25_F_8", IO);
        cellToResourceMap.put("OBUFT_LVCMOS25_S_12", IO);
        cellToResourceMap.put("OBUFT_LVCMOS25_S_16", IO);
        cellToResourceMap.put("OBUFT_LVCMOS25_S_2", IO);
        cellToResourceMap.put("OBUFT_LVCMOS25_S_24", IO);
        cellToResourceMap.put("OBUFT_LVCMOS25_S_4", IO);
        cellToResourceMap.put("OBUFT_LVCMOS25_S_6", IO);
        cellToResourceMap.put("OBUFT_LVCMOS25_S_8", IO);
        cellToResourceMap.put("OBUFT_LVCMOS33", IO);
        cellToResourceMap.put("OBUFT_LVCMOS33_F_12", IO);
        cellToResourceMap.put("OBUFT_LVCMOS33_F_16", IO);
        cellToResourceMap.put("OBUFT_LVCMOS33_F_2", IO);
        cellToResourceMap.put("OBUFT_LVCMOS33_F_24", IO);
        cellToResourceMap.put("OBUFT_LVCMOS33_F_4", IO);
        cellToResourceMap.put("OBUFT_LVCMOS33_F_6", IO);
        cellToResourceMap.put("OBUFT_LVCMOS33_F_8", IO);
        cellToResourceMap.put("OBUFT_LVCMOS33_S_12", IO);
        cellToResourceMap.put("OBUFT_LVCMOS33_S_16", IO);
        cellToResourceMap.put("OBUFT_LVCMOS33_S_2", IO);
        cellToResourceMap.put("OBUFT_LVCMOS33_S_24", IO);
        cellToResourceMap.put("OBUFT_LVCMOS33_S_4", IO);
        cellToResourceMap.put("OBUFT_LVCMOS33_S_6", IO);
        cellToResourceMap.put("OBUFT_LVCMOS33_S_8", IO);
        cellToResourceMap.put("OBUFT_LVDCI_15", IO);
        cellToResourceMap.put("OBUFT_LVDCI_18", IO);
        cellToResourceMap.put("OBUFT_LVDCI_25", IO);
        cellToResourceMap.put("OBUFT_LVDCI_33", IO);
        cellToResourceMap.put("OBUFT_LVDCI_DV2_15", IO);
        cellToResourceMap.put("OBUFT_LVDCI_DV2_18", IO);
        cellToResourceMap.put("OBUFT_LVDCI_DV2_25", IO);
        cellToResourceMap.put("OBUFT_LVDCI_DV2_33", IO);
        cellToResourceMap.put("OBUFT_LVDS", IO);
        cellToResourceMap.put("OBUFT_LVPECL", IO);
        cellToResourceMap.put("OBUFT_LVTTL", IO);
        cellToResourceMap.put("OBUFT_LVTTL_F_12", IO);
        cellToResourceMap.put("OBUFT_LVTTL_F_16", IO);
        cellToResourceMap.put("OBUFT_LVTTL_F_2", IO);
        cellToResourceMap.put("OBUFT_LVTTL_F_24", IO);
        cellToResourceMap.put("OBUFT_LVTTL_F_4", IO);
        cellToResourceMap.put("OBUFT_LVTTL_F_6", IO);
        cellToResourceMap.put("OBUFT_LVTTL_F_8", IO);
        cellToResourceMap.put("OBUFT_LVTTL_S_12", IO);
        cellToResourceMap.put("OBUFT_LVTTL_S_16", IO);
        cellToResourceMap.put("OBUFT_LVTTL_S_2", IO);
        cellToResourceMap.put("OBUFT_LVTTL_S_24", IO);
        cellToResourceMap.put("OBUFT_LVTTL_S_4", IO);
        cellToResourceMap.put("OBUFT_LVTTL_S_6", IO);
        cellToResourceMap.put("OBUFT_LVTTL_S_8", IO);
        cellToResourceMap.put("OBUFT_PCI33_3", IO);
        cellToResourceMap.put("OBUFT_PCI33_5", IO);
        cellToResourceMap.put("OBUFT_PCI66_3", IO);
        cellToResourceMap.put("OBUFT_PCIX", IO);
        cellToResourceMap.put("OBUFT_PCIX66_3", IO);
        cellToResourceMap.put("OBUFT_S_12", IO);
        cellToResourceMap.put("OBUFT_S_16", IO);
        cellToResourceMap.put("OBUFT_S_2", IO);
        cellToResourceMap.put("OBUFT_S_24", IO);
        cellToResourceMap.put("OBUFT_S_4", IO);
        cellToResourceMap.put("OBUFT_S_6", IO);
        cellToResourceMap.put("OBUFT_S_8", IO);
        cellToResourceMap.put("OBUFT_SSTL18_I", IO);
        cellToResourceMap.put("OBUFT_SSTL18_I_DCI", IO);
        cellToResourceMap.put("OBUFT_SSTL18_II", IO);
        cellToResourceMap.put("OBUFT_SSTL18_II_DCI", IO);
        cellToResourceMap.put("OBUFT_SSTL2_I", IO);
        cellToResourceMap.put("OBUFT_SSTL2_I_DCI", IO);
        cellToResourceMap.put("OBUFT_SSTL2_II", IO);
        cellToResourceMap.put("OBUFT_SSTL2_II_DCI", IO);
        cellToResourceMap.put("OBUFT_SSTL3_I", IO);
        cellToResourceMap.put("OBUFT_SSTL3_I_DCI", IO);
        cellToResourceMap.put("OBUFT_SSTL3_II", IO);
        cellToResourceMap.put("OBUFT_SSTL3_II_DCI", IO);
        cellToResourceMap.put("OBUFTDS", IO);
        cellToResourceMap.put("OBUFTDS_BLVDS_25", IO);
        cellToResourceMap.put("OBUFTDS_LDT_25", IO);
        cellToResourceMap.put("OBUFTDS_LVDS_25", IO);
        cellToResourceMap.put("OBUFTDS_LVDS_33", IO);
        cellToResourceMap.put("OBUFTDS_LVDSEXT_25", IO);
        cellToResourceMap.put("OBUFTDS_LVDSEXT_33", IO);
        cellToResourceMap.put("OBUFTDS_LVPECL_25", IO);
        cellToResourceMap.put("OBUFTDS_LVPECL_33", IO);
        cellToResourceMap.put("OBUFTDS_ULVDS_25", IO);

        cellToResourceMap.put("OFDDRCPE", "IO");
        cellToResourceMap.put("OFDDRRSE", "IO");
        cellToResourceMap.put("OFDDRTCPE", "IO");
        cellToResourceMap.put("OFDDRTRSE", "IO");

        cellToResourceMap.put("OPT_OFF", "");
        cellToResourceMap.put("OPT_UIM", "");
        cellToResourceMap.put("OR2", LUT);
        cellToResourceMap.put("OR2B1", LUT);
        cellToResourceMap.put("OR2B2", LUT);
        cellToResourceMap.put("OR3", LUT);
        cellToResourceMap.put("OR3B1", LUT);
        cellToResourceMap.put("OR3B2", LUT);
        cellToResourceMap.put("OR3B3", LUT);
        cellToResourceMap.put("OR4", LUT);
        cellToResourceMap.put("OR4B1", LUT);
        cellToResourceMap.put("OR4B2", LUT);
        cellToResourceMap.put("OR4B3", LUT);
        cellToResourceMap.put("OR4B4", LUT);
        cellToResourceMap.put("OR5", LUT);
        cellToResourceMap.put("OR5B1", LUT);
        cellToResourceMap.put("OR5B2", LUT);
        cellToResourceMap.put("OR5B3", LUT);
        cellToResourceMap.put("OR5B4", LUT);
        cellToResourceMap.put("OR5B5", LUT);
        cellToResourceMap.put("OR6", LUT);
        cellToResourceMap.put("OR7", LUT);
        cellToResourceMap.put("OR8", LUT);
        cellToResourceMap.put("ORCY", "");
        cellToResourceMap.put("PULLDOWN", RES);
        cellToResourceMap.put("PULLUP", RES);
        cellToResourceMap.put("RAM128X1S", LUT);
        cellToResourceMap.put("RAM128X1S_1", LUT);
        cellToResourceMap.put("RAM16X1D", LUT);
        cellToResourceMap.put("RAM16X1D_1", LUT);
        cellToResourceMap.put("RAM16X1S", LUT);
        cellToResourceMap.put("RAM16X1S_1", LUT);
        cellToResourceMap.put("RAM16X2S", LUT);
        cellToResourceMap.put("RAM16X4S", LUT);
        cellToResourceMap.put("RAM16X8S", LUT);
        cellToResourceMap.put("RAM32X1D", LUT);
        cellToResourceMap.put("RAM32X1D_1", LUT);
        cellToResourceMap.put("RAM32X1S", LUT);
        cellToResourceMap.put("RAM32X1S_1", LUT);
        cellToResourceMap.put("RAM32X2S", LUT);
        cellToResourceMap.put("RAM32X4S", LUT);
        cellToResourceMap.put("RAM32X8S", LUT);
        cellToResourceMap.put("RAM64X1D", LUT);
        cellToResourceMap.put("RAM64X1D_1", LUT);
        cellToResourceMap.put("RAM64X1S", LUT);
        cellToResourceMap.put("RAM64X1S_1", LUT);
        cellToResourceMap.put("RAM64X2S", LUT);
        cellToResourceMap.put("RAMB16_S1", BRAM);
        cellToResourceMap.put("RAMB16_S18", BRAM);
        cellToResourceMap.put("RAMB16_S18_S18", BRAM);
        cellToResourceMap.put("RAMB16_S18_S36", BRAM);
        cellToResourceMap.put("RAMB16_S1_S1", BRAM);
        cellToResourceMap.put("RAMB16_S1_S18", BRAM);
        cellToResourceMap.put("RAMB16_S1_S2", BRAM);
        cellToResourceMap.put("RAMB16_S1_S36", BRAM);
        cellToResourceMap.put("RAMB16_S1_S4", BRAM);
        cellToResourceMap.put("RAMB16_S1_S9", BRAM);
        cellToResourceMap.put("RAMB16_S2", BRAM);
        cellToResourceMap.put("RAMB16_S2_S18", BRAM);
        cellToResourceMap.put("RAMB16_S2_S2", BRAM);
        cellToResourceMap.put("RAMB16_S2_S36", BRAM);
        cellToResourceMap.put("RAMB16_S2_S4", BRAM);
        cellToResourceMap.put("RAMB16_S2_S9", BRAM);
        cellToResourceMap.put("RAMB16_S36", BRAM);
        cellToResourceMap.put("RAMB16_S36_S36", BRAM);
        cellToResourceMap.put("RAMB16_S4", BRAM);
        cellToResourceMap.put("RAMB16_S4_S18", BRAM);
        cellToResourceMap.put("RAMB16_S4_S36", BRAM);
        cellToResourceMap.put("RAMB16_S4_S4", BRAM);
        cellToResourceMap.put("RAMB16_S4_S9", BRAM);
        cellToResourceMap.put("RAMB16_S9", BRAM);
        cellToResourceMap.put("RAMB16_S9_S18", BRAM);
        cellToResourceMap.put("RAMB16_S9_S36", BRAM);
        cellToResourceMap.put("RAMB16_S9_S9", BRAM);
        cellToResourceMap.put("RAMB4_S1", BRAM);
        cellToResourceMap.put("RAMB4_S16", BRAM);
        cellToResourceMap.put("RAMB4_S16_S16", BRAM);
        cellToResourceMap.put("RAMB4_S1_S1", BRAM);
        cellToResourceMap.put("RAMB4_S1_S16", BRAM);
        cellToResourceMap.put("RAMB4_S1_S2", BRAM);
        cellToResourceMap.put("RAMB4_S1_S4", BRAM);
        cellToResourceMap.put("RAMB4_S1_S8", BRAM);
        cellToResourceMap.put("RAMB4_S2", BRAM);
        cellToResourceMap.put("RAMB4_S2_S16", BRAM);
        cellToResourceMap.put("RAMB4_S2_S2", BRAM);
        cellToResourceMap.put("RAMB4_S2_S4", BRAM);
        cellToResourceMap.put("RAMB4_S2_S8", BRAM);
        cellToResourceMap.put("RAMB4_S4", BRAM);
        cellToResourceMap.put("RAMB4_S4_S16", BRAM);
        cellToResourceMap.put("RAMB4_S4_S4", BRAM);
        cellToResourceMap.put("RAMB4_S4_S8", BRAM);
        cellToResourceMap.put("RAMB4_S8", BRAM);
        cellToResourceMap.put("RAMB4_S8_S16", BRAM);
        cellToResourceMap.put("RAMB4_S8_S8", BRAM);
        cellToResourceMap.put("ROC", "");
        cellToResourceMap.put("ROCBUF", "");
        cellToResourceMap.put("ROM128X1", LUT);
        cellToResourceMap.put("ROM16X1", LUT);
        cellToResourceMap.put("ROM256X1", LUT);
        cellToResourceMap.put("ROM32X1", LUT);
        cellToResourceMap.put("ROM64X1", LUT);
        cellToResourceMap.put("SRL16", LUT);
        cellToResourceMap.put("SRL16_1", LUT);
        cellToResourceMap.put("SRL16E", LUT);
        cellToResourceMap.put("SRL16E_1", LUT);
        cellToResourceMap.put("SRLC16", LUT);
        cellToResourceMap.put("SRLC16_1", LUT);
        cellToResourceMap.put("SRLC16E", LUT);
        cellToResourceMap.put("SRLC16E_1", LUT);
        cellToResourceMap.put("STARTBUF_FPGACORE", "");
        cellToResourceMap.put("STARTBUF_SPARTAN2", "");
        cellToResourceMap.put("STARTBUF_SPARTAN3", "");
        cellToResourceMap.put("STARTBUF_VIRTEX", "");
        cellToResourceMap.put("STARTBUF_VIRTEX2", "");
        cellToResourceMap.put("STARTUP_FPGACORE", "");
        cellToResourceMap.put("STARTUP_SPARTAN2", "");
        cellToResourceMap.put("STARTUP_SPARTAN3", "");
        cellToResourceMap.put("STARTUP_VIRTEX", "");
        cellToResourceMap.put("STARTUP_VIRTEX2", "");
        cellToResourceMap.put("TBLOCK", "");
        cellToResourceMap.put("TIMEGRP", "");
        cellToResourceMap.put("TIMESPEC", "");
        cellToResourceMap.put("TOC", "");
        cellToResourceMap.put("TOCBUF", "");
        cellToResourceMap.put("VCC", "");
        cellToResourceMap.put("WIREAND", "");
        cellToResourceMap.put("XNOR2", LUT);
        cellToResourceMap.put("XNOR3", LUT);
        cellToResourceMap.put("XNOR4", LUT);
        cellToResourceMap.put("XNOR5", LUT);
        cellToResourceMap.put("XOR2", LUT);
        cellToResourceMap.put("XOR3", LUT);
        cellToResourceMap.put("XOR4", LUT);
        cellToResourceMap.put("XOR5", LUT);
        cellToResourceMap.put("XORCY", "");
        cellToResourceMap.put("XORCY_D", "");
        cellToResourceMap.put("XORCY_L", "");
        cellToResourceMap.put("GT10", "");
        cellToResourceMap.put("GT10_10GE_4", "");
        cellToResourceMap.put("GT10_10GE_8", "");
        cellToResourceMap.put("GT10_10GFC_4", "");
        cellToResourceMap.put("GT10_10GFC_8", "");
        cellToResourceMap.put("GT10_AURORA_1", "");
        cellToResourceMap.put("GT10_AURORA_2", "");
        cellToResourceMap.put("GT10_AURORA_4", "");
        cellToResourceMap.put("GT10_AURORAX_4", "");
        cellToResourceMap.put("GT10_AURORAX_8", "");
        cellToResourceMap.put("GT10_CUSTOM", "");
        cellToResourceMap.put("GT10_INFINIBAND_1", "");
        cellToResourceMap.put("GT10_INFINIBAND_2", "");
        cellToResourceMap.put("GT10_INFINIBAND_4", "");
        cellToResourceMap.put("GT10_OC192_4", "");
        cellToResourceMap.put("GT10_OC192_8", "");
        cellToResourceMap.put("GT10_OC48_1", "");
        cellToResourceMap.put("GT10_OC48_2", "");
        cellToResourceMap.put("GT10_OC48_4", "");
        cellToResourceMap.put("GT10_PCI_EXPRESS_1", "");
        cellToResourceMap.put("GT10_PCI_EXPRESS_2", "");
        cellToResourceMap.put("GT10_PCI_EXPRESS_4", "");
        cellToResourceMap.put("GT10_XAUI_1", "");
        cellToResourceMap.put("GT10_XAUI_2", "");
        cellToResourceMap.put("GT10_XAUI_4", "");
        cellToResourceMap.put("GT", "");
        cellToResourceMap.put("GT_AURORA_1", "");
        cellToResourceMap.put("GT_AURORA_2", "");
        cellToResourceMap.put("GT_AURORA_4", "");
        cellToResourceMap.put("GT_CUSTOM", "");
        cellToResourceMap.put("GT_ETHERNET_1", "");
        cellToResourceMap.put("GT_ETHERNET_2", "");
        cellToResourceMap.put("GT_ETHERNET_4", "");
        cellToResourceMap.put("GT_FIBRE_CHAN_1", "");
        cellToResourceMap.put("GT_FIBRE_CHAN_2", "");
        cellToResourceMap.put("GT_FIBRE_CHAN_4", "");
        cellToResourceMap.put("GT_INFINIBAND_1", "");
        cellToResourceMap.put("GT_INFINIBAND_2", "");
        cellToResourceMap.put("GT_INFINIBAND_4", "");
        cellToResourceMap.put("GT_XAUI_1", "");
        cellToResourceMap.put("GT_XAUI_2", "");
        cellToResourceMap.put("GT_XAUI_4", "");
        cellToResourceMap.put("FPGA_startup", "");
        cellToResourceMap.put("PPC405", PPC);
        cellToResourceMap.put("JTAGPPC", "");
        cellToResourceMap.put("DCC_FPGACORE", "");
        cellToResourceMap.put("GT10_SWIFT_BUS", "");
        cellToResourceMap.put("GT_SWIFT_BUS", "");
        cellToResourceMap.put("PPC405_SWIFT_BUS", "");
        cellToResourceMap.put("DCC_FPGACORE_SWIFT_BUS", "");
        cellToResourceMap.put("FDD", FF);
        cellToResourceMap.put("FDDC", FF);
        cellToResourceMap.put("FDDCE", FF);
        cellToResourceMap.put("FDDCP", FF);
        cellToResourceMap.put("FDDCPE", FF);
        cellToResourceMap.put("FDDP", FF);
        cellToResourceMap.put("FDDPE", FF);
        cellToResourceMap.put("CLK_DIV2", "");
        cellToResourceMap.put("CLK_DIV4", "");
        cellToResourceMap.put("CLK_DIV6", "");
        cellToResourceMap.put("CLK_DIV8", "");
        cellToResourceMap.put("CLK_DIV10", "");
        cellToResourceMap.put("CLK_DIV12", "");
        cellToResourceMap.put("CLK_DIV14", "");
        cellToResourceMap.put("CLK_DIV16", "");
        cellToResourceMap.put("CLK_DIV2R", "");
        cellToResourceMap.put("CLK_DIV4R", "");
        cellToResourceMap.put("CLK_DIV6R", "");
        cellToResourceMap.put("CLK_DIV8R", "");
        cellToResourceMap.put("CLK_DIV10R", "");
        cellToResourceMap.put("CLK_DIV12R", "");
        cellToResourceMap.put("CLK_DIV14R", "");
        cellToResourceMap.put("CLK_DIV16R", "");
        cellToResourceMap.put("CLK_DIV2SD", "");
        cellToResourceMap.put("CLK_DIV4SD", "");
        cellToResourceMap.put("CLK_DIV6SD", "");
        cellToResourceMap.put("CLK_DIV8SD", "");
        cellToResourceMap.put("CLK_DIV10SD", "");
        cellToResourceMap.put("CLK_DIV12SD", "");
        cellToResourceMap.put("CLK_DIV14SD", "");
        cellToResourceMap.put("CLK_DIV16SD", "");
        cellToResourceMap.put("CLK_DIV2RSD", "");
        cellToResourceMap.put("CLK_DIV4RSD", "");
        cellToResourceMap.put("CLK_DIV6RSD", "");
        cellToResourceMap.put("CLK_DIV8RSD", "");
        cellToResourceMap.put("CLK_DIV10RSD", "");
        cellToResourceMap.put("CLK_DIV12RSD", "");
        cellToResourceMap.put("CLK_DIV14RSD", "");
        cellToResourceMap.put("CLK_DIV16RSD", "");
        cellToResourceMap.put("LDG", "");

        //added for the Virtex 4
        cellToResourceMap.put("DSP48", DSP);

        cellToResourceMap.put("BUFGCTRL", BUFG);
        cellToResourceMap.put("BUFGMUX_VIRTEX4", BUFG);
        cellToResourceMap.put("BUFIO", "");//local (not global) clock buffer for IO
        cellToResourceMap.put("BUFR", "");//local clock buffer for IO and CLB
        cellToResourceMap.put("DCM_ADV", DCM);
        cellToResourceMap.put("DCM_BASE", DCM);
        cellToResourceMap.put("DCM_PS", DCM);
        cellToResourceMap.put("PMCD", "");//phase matched clock divider

        cellToResourceMap.put("BSCAN_VIRTEX4", "");
        cellToResourceMap.put("CAPTURE_VIRTEX4", "");
        cellToResourceMap.put("FRAME_ECC_VIRTEX4", FRAME_ECC); //self scrubing component
        cellToResourceMap.put("ICAP_VIRTEX4", ICAP); //self scrubing component
        cellToResourceMap.put("STARTUP_VIRTEX4", "");//?
        cellToResourceMap.put("USR_ACCESS_VIRTEX4", "");//?

        //1-Is RocketIO the same as MGT? YES
        //2-Do the MGTs take up 2 physical locations? See Below:
        //	According to the Xilinx Virtex-4 Libraries Guide for HDL Designs,
        //	it is recommended that the GT11_DUAL is instantiated instead of the GT11_CUSTOM for all usages.
        //  The GT11_DUAL must be used if multiple GT11s are used in a design,
        //	or if the dynamic configuration bus is implemented. See pg. 103
        //	SO BASICALLY FOR ANY DESIGNS A USER WANTS TRIPLICATED THEY HAVE TO USE THE DUAL, CUSTOM CAN'T BE SUPPORTED.
        //	AND YES, SINCE THE DUALS USE TWO CUSTOMS, THE MGTS TAKE UP 2 LOCATIONS (WHICH WILL REQUIRE A DOUBLE).
        cellToResourceMap.put("GT11_CUSTOM", TRANSEIVER);//RocketIO MGT
        cellToResourceMap.put("GT11_DUAL", TRANSEIVER);//RocketIO MGT Tile (contains 2 GT11_CUSTOM)
        cellToResourceMap.put("GT11CLK", "");//A MUX that can select from Differential Package Input Clock, refclk from the fabric, or rxbclk to drive the two vertical reference clock buses for the column of MGTs
        cellToResourceMap.put("GT11CLK_MGT", "");//Allows differential package input to drive the two vertical reference clock buses for the column of MGTs

        cellToResourceMap.put("DCIRESET", "");//Resets the Digitally Controlled Impedance to re-calibrate 
        cellToResourceMap.put("IDDR", FF);//IO
        cellToResourceMap.put("IDELAY", "");//IO
        cellToResourceMap.put("IDELAYCTRL", "");//IO
        cellToResourceMap.put("ISERDES", "");// serial to parallel converter

        cellToResourceMap.put("ODDR", FF);//IO
        cellToResourceMap.put("OSERDES", "");// serial to parallel converter

        cellToResourceMap.put("EMAC", ETHERNET);//Fully integrated Ethernet Media Access Controller! wow!
        cellToResourceMap.put("PPC405_ADV", PPC);

        cellToResourceMap.put("FIFO16", BRAM);
        cellToResourceMap.put("RAMB16", BRAM);
        cellToResourceMap.put("RAMB32_S64_ECC", BRAM);
        //end virtex 4 additions
    }

    static {
        cellToLUTEquivalentMap = new LinkedHashMap<String, Double>();
        cellToLUTEquivalentMap.put("AND2", new Double(1.0));
        cellToLUTEquivalentMap.put("AND2B1", new Double(1.0));
        cellToLUTEquivalentMap.put("AND2B2", new Double(1.0));
        cellToLUTEquivalentMap.put("AND3", new Double(1.0));
        cellToLUTEquivalentMap.put("AND3B1", new Double(1.0));
        cellToLUTEquivalentMap.put("AND3B2", new Double(1.0));
        cellToLUTEquivalentMap.put("AND3B3", new Double(1.0));
        cellToLUTEquivalentMap.put("AND4", new Double(1.0));
        cellToLUTEquivalentMap.put("AND4B1", new Double(1.0));
        cellToLUTEquivalentMap.put("AND4B2", new Double(1.0));
        cellToLUTEquivalentMap.put("AND4B3", new Double(1.0));
        cellToLUTEquivalentMap.put("AND4B4", new Double(1.0));
        cellToLUTEquivalentMap.put("AND5", new Double(2.0));
        cellToLUTEquivalentMap.put("AND5B1", new Double(2.0));
        cellToLUTEquivalentMap.put("AND5B2", new Double(2.0));
        cellToLUTEquivalentMap.put("AND5B3", new Double(2.0));
        cellToLUTEquivalentMap.put("AND5B4", new Double(2.0));
        cellToLUTEquivalentMap.put("AND5B5", new Double(2.0));
        cellToLUTEquivalentMap.put("AND6", new Double(3.0));
        cellToLUTEquivalentMap.put("AND7", new Double(3.0));
        cellToLUTEquivalentMap.put("AND8", new Double(3.0));
        cellToLUTEquivalentMap.put("FMAP", new Double(1.0));
        cellToLUTEquivalentMap.put("LUT1", new Double(1.0));
        cellToLUTEquivalentMap.put("LUT1_D", new Double(1.0));
        cellToLUTEquivalentMap.put("LUT1_L", new Double(1.0));
        cellToLUTEquivalentMap.put("LUT2", new Double(1.0));
        cellToLUTEquivalentMap.put("LUT2_D", new Double(1.0));
        cellToLUTEquivalentMap.put("LUT2_L", new Double(1.0));
        cellToLUTEquivalentMap.put("LUT3", new Double(1.0));
        cellToLUTEquivalentMap.put("LUT3_D", new Double(1.0));
        cellToLUTEquivalentMap.put("LUT3_L", new Double(1.0));
        cellToLUTEquivalentMap.put("LUT4", new Double(1.0));
        cellToLUTEquivalentMap.put("LUT4_D", new Double(1.0));
        cellToLUTEquivalentMap.put("LUT4_L", new Double(1.0));
        cellToLUTEquivalentMap.put("NAND2", new Double(1.0));
        cellToLUTEquivalentMap.put("NAND2B1", new Double(1.0));
        cellToLUTEquivalentMap.put("NAND2B2", new Double(1.0));
        cellToLUTEquivalentMap.put("NAND3", new Double(1.0));
        cellToLUTEquivalentMap.put("NAND3B1", new Double(1.0));
        cellToLUTEquivalentMap.put("NAND3B2", new Double(1.0));
        cellToLUTEquivalentMap.put("NAND3B3", new Double(1.0));
        cellToLUTEquivalentMap.put("NAND4", new Double(1.0));
        cellToLUTEquivalentMap.put("NAND4B1", new Double(1.0));
        cellToLUTEquivalentMap.put("NAND4B2", new Double(1.0));
        cellToLUTEquivalentMap.put("NAND4B3", new Double(1.0));
        cellToLUTEquivalentMap.put("NAND4B4", new Double(1.0));
        cellToLUTEquivalentMap.put("NAND5", new Double(2.0));
        cellToLUTEquivalentMap.put("NAND5B1", new Double(2.0));
        cellToLUTEquivalentMap.put("NAND5B2", new Double(2.0));
        cellToLUTEquivalentMap.put("NAND5B3", new Double(2.0));
        cellToLUTEquivalentMap.put("NAND5B4", new Double(2.0));
        cellToLUTEquivalentMap.put("NAND5B5", new Double(2.0));
        cellToLUTEquivalentMap.put("NOR2", new Double(1.0));
        cellToLUTEquivalentMap.put("NOR2B1", new Double(1.0));
        cellToLUTEquivalentMap.put("NOR2B2", new Double(1.0));
        cellToLUTEquivalentMap.put("NOR3", new Double(1.0));
        cellToLUTEquivalentMap.put("NOR3B1", new Double(1.0));
        cellToLUTEquivalentMap.put("NOR3B2", new Double(1.0));
        cellToLUTEquivalentMap.put("NOR3B3", new Double(1.0));
        cellToLUTEquivalentMap.put("NOR4", new Double(1.0));
        cellToLUTEquivalentMap.put("NOR4B1", new Double(1.0));
        cellToLUTEquivalentMap.put("NOR4B2", new Double(1.0));
        cellToLUTEquivalentMap.put("NOR4B3", new Double(1.0));
        cellToLUTEquivalentMap.put("NOR4B4", new Double(1.0));
        cellToLUTEquivalentMap.put("NOR5", new Double(2.0));
        cellToLUTEquivalentMap.put("NOR5B1", new Double(2.0));
        cellToLUTEquivalentMap.put("NOR5B2", new Double(2.0));
        cellToLUTEquivalentMap.put("NOR5B3", new Double(2.0));
        cellToLUTEquivalentMap.put("NOR5B4", new Double(2.0));
        cellToLUTEquivalentMap.put("NOR5B5", new Double(2.0));
        cellToLUTEquivalentMap.put("OR2", new Double(1.0));
        cellToLUTEquivalentMap.put("OR2B1", new Double(1.0));
        cellToLUTEquivalentMap.put("OR2B2", new Double(1.0));
        cellToLUTEquivalentMap.put("OR3", new Double(1.0));
        cellToLUTEquivalentMap.put("OR3B1", new Double(1.0));
        cellToLUTEquivalentMap.put("OR3B2", new Double(1.0));
        cellToLUTEquivalentMap.put("OR3B3", new Double(1.0));
        cellToLUTEquivalentMap.put("OR4", new Double(1.0));
        cellToLUTEquivalentMap.put("OR4B1", new Double(1.0));
        cellToLUTEquivalentMap.put("OR4B2", new Double(1.0));
        cellToLUTEquivalentMap.put("OR4B3", new Double(1.0));
        cellToLUTEquivalentMap.put("OR4B4", new Double(1.0));
        cellToLUTEquivalentMap.put("OR5", new Double(2.0));
        cellToLUTEquivalentMap.put("OR5B1", new Double(2.0));
        cellToLUTEquivalentMap.put("OR5B2", new Double(2.0));
        cellToLUTEquivalentMap.put("OR5B3", new Double(2.0));
        cellToLUTEquivalentMap.put("OR5B4", new Double(2.0));
        cellToLUTEquivalentMap.put("OR5B5", new Double(2.0));
        cellToLUTEquivalentMap.put("OR6", new Double(3.0));
        cellToLUTEquivalentMap.put("OR7", new Double(3.0));
        cellToLUTEquivalentMap.put("OR8", new Double(3.0));
        cellToLUTEquivalentMap.put("RAM128X1S", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM128X1S_1", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM16X1D", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM16X1D_1", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM16X1S", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM16X1S_1", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM16X2S", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM16X4S", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM16X8S", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM32X1D", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM32X1D_1", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM32X1S", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM32X1S_1", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM32X2S", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM32X4S", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM32X8S", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM64X1D", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM64X1D_1", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM64X1S", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM64X1S_1", new Double(1.0));
        cellToLUTEquivalentMap.put("RAM64X2S", new Double(1.0));
        cellToLUTEquivalentMap.put("ROM128X1", new Double(1.0));
        cellToLUTEquivalentMap.put("ROM16X1", new Double(1.0));
        cellToLUTEquivalentMap.put("ROM256X1", new Double(1.0));
        cellToLUTEquivalentMap.put("ROM32X1", new Double(1.0));
        cellToLUTEquivalentMap.put("ROM64X1", new Double(1.0));
        cellToLUTEquivalentMap.put("SRL16", new Double(1.0));
        cellToLUTEquivalentMap.put("SRL16_1", new Double(1.0));
        cellToLUTEquivalentMap.put("SRL16E", new Double(1.0));
        cellToLUTEquivalentMap.put("SRL16E_1", new Double(1.0));
        cellToLUTEquivalentMap.put("SRLC16", new Double(1.0));
        cellToLUTEquivalentMap.put("SRLC16_1", new Double(1.0));
        cellToLUTEquivalentMap.put("SRLC16E", new Double(1.0));
        cellToLUTEquivalentMap.put("SRLC16E_1", new Double(1.0));
        cellToLUTEquivalentMap.put("XNOR2", new Double(1.0));
        cellToLUTEquivalentMap.put("XNOR3", new Double(1.0));
        cellToLUTEquivalentMap.put("XNOR4", new Double(1.0));
        cellToLUTEquivalentMap.put("XNOR5", new Double(2.0));
        cellToLUTEquivalentMap.put("XOR2", new Double(1.0));
        cellToLUTEquivalentMap.put("XOR3", new Double(1.0));
        cellToLUTEquivalentMap.put("XOR4", new Double(1.0));
        cellToLUTEquivalentMap.put("XOR5", new Double(2.0));
    }
}
