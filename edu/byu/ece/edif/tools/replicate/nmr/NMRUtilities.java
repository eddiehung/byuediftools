/*
 * Utilities common to all types of replication (NMR)
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

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifPrintWriter;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.EdifUtils;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxResourceMapper;
import edu.byu.ece.edif.util.graph.EdifCellInstanceCollectionLink;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.graph.Edge;

/**
 * Utilities common to all types of replication (NMR)
 * 
 * @author <a href="jcarroll@byu.net">James Carroll</a>
 */
public abstract class NMRUtilities {

    /**
     * Return the Set of BUF instances that are associated with this Collection
     * of EdifPorts. NOTE: This method is currently Xilinx-specific
     * 
     * @param ports The Collection of EdifPort objects
     * @param graph The EdifCellInstanceGraph graph
     * @return the Set of BUF instances that are associated with this Collection
     * of EdifPorts.
     */
    public static Set<EdifCellInstance> getPortBufs(Collection<EdifPort> ports, EdifCellInstanceGraph graph) {

        Set<EdifCellInstance> portBufs = new LinkedHashSet<EdifCellInstance>();

        for (EdifPort port : ports) {
            for (EdifSingleBitPort esbp : port.getSingleBitPortList()) {
                Collection<EdifCellInstance> neighbors = new ArrayList<EdifCellInstance>();

                // Only one of these should work...
                neighbors.addAll(graph.getSuccessors(esbp));
                neighbors.addAll(graph.getPredecessors(esbp));
                // Only add connected instances if they are "IO" instances
                // (only one per SingleBitPort???)
                if (debug)
                    System.out.println("Neighbors of port " + esbp + ": " + neighbors);
                // Ignore any object which are not EdifCellInstances (Top-level
                // ports)
                for (Object obj : neighbors) {
                    if (obj instanceof EdifCellInstance) {
                        EdifCellInstance eci = (EdifCellInstance) obj;
                        String cellType = XilinxResourceMapper.getResourceType(eci);
                        if (cellType != null && cellType.equals(XilinxResourceMapper.IO)) {
                            if (debug)
                                System.out.println("Adding IO instance: " + eci);
                            portBufs.add(eci);
                        }
                    }
                }
            }
        }

        return portBufs;
    }

    /**
     * Determine if the EdifCell contains any in-out ports.
     * 
     * @param cell The EdifCell
     * @return true if any of the EdifPort objects in the EdifCell is an in-out
     * port.
     */
    protected static boolean cellHasInoutPorts(EdifCell cell) {
        Collection<EdifPort> ports = cell.getPortList();
        for (EdifPort port : ports) {
            if (port.isInOut())
                return true;
        }
        return false;
    }

    /**
     * Creates a flattened cell and prints out design flattening information.
     * 
     * @param origCell The original, un-flattened EdifCell.
     * @param out A PrintStream to which a summary of the flattened cell will be
     * printed.
     * @return The flattened EdifCell.
     * @see FlattenedEdifCell
     */
    public static FlattenedEdifCell flattenCell(EdifCell origCell, PrintStream out) {
        // 8. Perform design flattening
        out.println("Flattening");
        FlattenedEdifCell flatCell = null;

        try {
            flatCell = new FlattenedEdifCell(origCell);
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }
        out.println("\tFlattened circuit contains " + EdifUtils.countRecursivePrimitives(flatCell) + " primitives, "
                + EdifUtils.countRecursiveNets(flatCell) + " nets, and " + EdifUtils.countPortRefs(flatCell, true)
                + " net connections");

        return flatCell;
    }

    /**
     * Valid utilization factor types.
     * <dl>
     * <dt>DUF</dt>
     * <dd>Desired Utilization Factor</dd>
     * <dt>UEF</dt>
     * <dd>Utilization Expansion Factor</dd>
     * <dt>ASUF</dt>
     * <dd>Available Space Utilization Factor</dd>
     * </dl>
     * For more information, see the BLTmr documentation (BLTmr.pdf).
     */
    public enum UtilizationFactor {
        DUF, UEF, ASUF, CF
    };

    /**
     * The default utilization factor to use if omitted by the user. Currently,
     * this is {@link UtilizationFactor#ASUF}, but this could change in the
     * future.
     */
    public static final UtilizationFactor DEFAULT_UTILIZATION_FACTOR = UtilizationFactor.ASUF;

    /**
     * Used to increase precision of step size in Multiple EDIF Creation.
     */
    public static final long PRECISION_FACTOR = 1000000000L;

    /**
     * Set of ports that should not be triplicated, specific to the BYU SLAAC
     * board.
     */
    public static final String SLAAC1V_PORTS_NOT_TO_REPLICATE[] = { "gsr", "capture_clk", "capture" };

    public static final String VIRTEX = "virtex";

    public static final String VIRTEX2 = "virtex2";

    public static final String VIRTEX2PRO = "virtex2pro";

    public static final String VIRTEX4 = "virtex4";

    /**
     * Use to enable or disable debugging print statements
     */
    private static boolean debug = false;

    public static final String DEFAULT_FACTOR_VALUE = "1.0";

    /**
     * Typical suffixes for EDIF files.
     */
    public static final String EDIF_SUFFIXES[] = { ".edf", ".edn", ".edif" };

    /**
     * Remove any of the typical suffixes for EDIF files, if present. The
     * original String object is <i>not</i> modified.
     * 
     * @param s The given String
     * @return The modified String.
     * @see #EDIF_SUFFIXES
     */
    public static String removeEdifSuffixes(String s) {
        String result = new String(s);
        for (String suffix : EDIF_SUFFIXES) {
            if (result.endsWith(suffix)) {
                int index = result.lastIndexOf(suffix);
                result = result.substring(0, index);
                return result; // Return after the first successful find.
            }
        }
        return result;
    }

    /**
     * Write the {@link EdifEnvironment} corresponding to the given
     * {@link EdifCell} to the given filename. If any exceptions are thrown,
     * they are printed and the program exits.
     * 
     * @param filename The filename
     * @param cell The EdifCell
     */
    public static void createOutputFile(String filename, EdifCell cell) {
        try {
            EdifPrintWriter epw = new EdifPrintWriter(new FileOutputStream(filename));
            cell.getLibrary().getLibraryManager().getEdifEnvironment().toEdif(epw);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
            System.exit(1);
        }

    }

    /**
     * Return the verbose equivalent of the given {@link UtilizationFactor}.
     * 
     * @param factor The UtilizationFactor
     * @return the verbose equivalent of the given {@link UtilizationFactor}.
     */
    public static String getVerboseUtilizationFactor(UtilizationFactor factor) {
        String type = null;
        if (factor.compareTo(UtilizationFactor.ASUF) == 0)
            type = "Available Space Utilization Factor";
        else if (factor.compareTo(UtilizationFactor.UEF) == 0)
            type = "Utilization Expansion Factor";
        else if (factor.compareTo(UtilizationFactor.DUF) == 0)
            type = "Desired Utilization Factor";
        else
            // Should never get here.
            throw new EdifRuntimeException("Invalid utilization factor factor: " + factor.toString() + ".");
        return type;
    }

    public static String msToString(long milliseconds) {
        long sec = milliseconds / 1000;
        long min = sec / 60;
        long hour = min / 60;
        milliseconds %= 1000;
        sec %= 60;
        min %= 60;
        DecimalFormat twoDigits = new DecimalFormat("00");

        return new String(twoDigits.format(hour) + ":" + twoDigits.format(min) + ":" + twoDigits.format(sec) + "."
                + milliseconds);
    }

    /**
     * Create a log file If the file cannot be created, for whatever reason, the
     * exception is caught, printed, and the program will terminate.
     * 
     * @param filename The path and filename of the desired log file.
     * @return A PrintStream for the logfile
     */
    public static PrintStream createLogFile(String filename) {
        PrintStream stream = null;
        try {
            stream = new PrintStream(new FileOutputStream(filename));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
            System.exit(1);
        }
        return stream;
    }

    /**
     * @param cutSet
     * @param portRefsToCut
     */
    public static Collection<EdifPortRef> getPortRefsToCutFromEdges(Collection<Edge> cutSet,
            EdifCellInstanceGraph graph, NMRArchitecture nmrArch) {
        Collection<EdifPortRef> portRefsToCut = new LinkedHashSet<EdifPortRef>();
        Collection<EdifCellInstanceEdge> esslCutSet = new ArrayList<EdifCellInstanceEdge>(cutSet.size());

        // alwaysChooseSinks = true uses the old, brain-dead method
        // alwaysChooseSinks = false uses a smarter method
        boolean alwaysChooseSinks = false;
        boolean debug = false;

        // Convert CollectionLinks to SourceSinkLinks, if necessary
        for (Edge edge : cutSet) {
            if (edge instanceof EdifCellInstanceCollectionLink) {
                esslCutSet.addAll(((EdifCellInstanceCollectionLink) edge).getLinks());
            } else if (edge instanceof EdifCellInstanceEdge)
                esslCutSet.add((EdifCellInstanceEdge) edge);
            else
                throw new EdifRuntimeException("Unknown Edge type: " + edge.getClass());
        }

        // Always choose the Sink EPR
        if (alwaysChooseSinks) {
            // System.out.println("Getting cutset port refs...");
            for (EdifCellInstanceEdge edge : esslCutSet) {
                EdifPortRef cutEPR = edge.getSinkEPR();
                // Double-check that the Sink is an Input EPR (driven, not drives)
                if (cutEPR.isDriverPortRef()) {
                    // System.out.println("WARNING: PortRef chosen for TMR is a
                    // signal driver: " + cutEPR);
                    // TODO: Do this more gracefully!
                    cutEPR = edge.getSourceEPR();
                }
                portRefsToCut.add(cutEPR);
            }
        }
        // Try and be smart, choosing the Driver EPR unless not possible
        //   because of bad cuts
        else {
            // Iterate over all cut edges. For each edge:
            // - Check if the source portRef has been cut (or checked?)
            //   - If cut, skip this one
            //   - else if checked (and not cut), cut sink
            //   - else Check all Edges coming from this portRef
            //     - If *any* are bad cuts, cut sink and mark this EPR as checked
            Collection<EdifPortRef> checkedPortRefs = new LinkedHashSet<EdifPortRef>();
            for (EdifCellInstanceEdge edge : esslCutSet) {
                // Get Edge driver
                EdifPortRef cutEPR = edge.getSourceEPR();
                EdifPortRef noCutEPR = edge.getSinkEPR();
                // Double-check that the Source is an Output EPR (driver, not driven)
                if (!cutEPR.isDriverPortRef()) {
                    throw new EdifRuntimeException("ERROR " + "(getPortRefsToCutFromEdges): Edge source is "
                            + "not a signal driver: " + cutEPR);
                    //cutEPR = edge.getSinkEPR();
                    //noCutEPR = edge.getSourceEPR();
                }

                // - Check if the source portRef has been cut (or checked?)
                //   - If cut, skip this one
                if (portRefsToCut.contains(cutEPR)) {
                    if (debug)
                        System.out.println("### SKIP THIS ONE (SAVE TIME!!!)");
                    continue;
                }
                //   - else if checked (and not cut), cut sink
                else if (checkedPortRefs.contains(cutEPR)) {
                    if (debug)
                        System.out.println("### HAD TO CUT A SINK!!!");
                    cutEPR = noCutEPR;
                }
                //   - else Check all Edges coming from this portRef
                else {
                    // First visit of this EPR. Mark this EPR as checked.
                    checkedPortRefs.add(cutEPR);
                    // If *any* are bad cuts, cut sink (and move on)
                    for (EdifCellInstanceEdge otherEdge : graph.getOutputEdges(edge.getSource(), cutEPR)) {
                        if (nmrArch.isBadCutConnection(otherEdge.getSourceEPR(), otherEdge.getSinkEPR())) {
                            if (debug)
                                System.out.println("### ONE OF THE EDGES IS A BAD CUT!!!");
                            cutEPR = noCutEPR;
                            break;
                        }
                    }
                }

                portRefsToCut.add(cutEPR);
            }

        }

        if (debug)
            System.out.println("Cutting " + portRefsToCut.size() + " PortRefs");

        return portRefsToCut;
    }

}
