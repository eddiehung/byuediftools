package edu.byu.ece.edif.tools.replicate.wiring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.tools.replicate.nmr.CircuitDescription;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationDescription;
import edu.byu.ece.edif.util.graph.EdifCellInstanceEdge;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;

/**
 * When pre-mitigated cells are used, it is necessary to leave some of the
 * ports unconnected in input design to the replication tool flow. However,
 * most synthesis tools will not allow a dangling top-level output port or a
 * dangling non top-level input port. Instead, they usually wire these ports
 * maked as 'open' in VHDL to a GND instance. The job of this class is to find
 * all of these extra dummy connections and mark them so they can be ignored
 * during the actual replication process (in JEdifNMR). The information about
 * which isntances, nets, and portRefs to ignore is saved in the
 * ReplicationDescription.
 */
public class PreMitigatedDummyTrimmer {

    public static void markDummyConnectionsToIgnore(EdifCell topCell,
            Collection<PreMitigatedPortGroup> portGroups,
            CircuitDescription cDesc, ReplicationDescription rDesc) {
        
        // this map maps from pre-mitigated cell types to a list of non-domain0 ports associated with the cell type
        Map<EdifCell, Collection<EdifPort>> preMitigatedCellTypes = new LinkedHashMap<EdifCell, Collection<EdifPort>>();
        Collection<EdifCellInstance> possibilityNodes = new LinkedHashSet<EdifCellInstance>();
        for (PreMitigatedPortGroup portGroup : portGroups) {
            EdifCell cell = portGroup.getFirstPort().getEdifCell();
            Collection<EdifPort> portSet = preMitigatedCellTypes.get(cell);
            if (portSet == null) {
                portSet = new LinkedHashSet<EdifPort>();
                preMitigatedCellTypes.put(cell, portSet);
            }
            portSet.addAll(portGroup.getOtherPorts());
        }

        // The first step is to initialize the algorithm with the edges leading to the nodes we are concerned with
        //   there are both top-level output ports and subCell input ports to worry about (only the non-domain0 ports (getOtherPorts) matter)
        EdifCellInstanceGraph ecic = cDesc.getInstanceGraph();
        
        // this will keep track of all edges leading TO things that shouldn't have connections leading to them
        Collection<EdifCellInstanceEdge> incomingEdges = new LinkedHashSet<EdifCellInstanceEdge>();

        // add incoming edges from top-level ports
        for (PreMitigatedPortGroup portGroup : portGroups) {
            if (portGroup.getFirstPort().getEdifCell() == topCell) { // only looking at top-level
                for (EdifPort port : portGroup.getOtherPorts()) {
                    for (EdifSingleBitPort esbp : port.getSingleBitPortList()) {
                        incomingEdges.addAll(ecic.getInputEdges(esbp));
                    }
                }
            }
        }

        // add incoming edges from non top-level ports
        for (EdifCellInstance eci : topCell.getSubCellList()) {
            if (preMitigatedCellTypes.containsKey(eci.getCellType())) {
                Collection<EdifCellInstanceEdge> maybeIncomingEdges = ecic
                        .getInputEdges(eci);
                for (EdifCellInstanceEdge edge : maybeIncomingEdges) {
                    // make sure only the edges going to the non-domain0 ports of the instance get counted as incomingEdges
                    if (preMitigatedCellTypes.get(eci.getCellType()).contains(
                            edge.getSinkEPR().getPort())) {
                        incomingEdges.add(edge);
                    }
                }
            }
        }

        // now add all nodes that are sources of incoming edges as possibility nodes
        // (possibility nodes are nodes that *might* need to be deleted. A possibility
        // node will be marked for deletion when all of the edges leading out of it are
        // in the incomingEdges collection (edges that lead TO things that shouldn't have
        // connections leading to them)
        for (EdifCellInstanceEdge incomingEdge : incomingEdges) {
            Object node = incomingEdge.getSource();
            if (node instanceof EdifCellInstance)
                possibilityNodes.add((EdifCellInstance) node);
        }

        // keep track of which instances we are going to be deleting
        Collection<EdifCellInstance> nodesToDelete = new ArrayList<EdifCellInstance>();
        
        // this queue keeps track of nodes we haven't looked at yet
        Queue<EdifCellInstance> nodes = new LinkedList<EdifCellInstance>();
        boolean somethingChanged = true;
        while (somethingChanged) { // the algorithm only need continue while progress is being made
            somethingChanged = false;
            while (!nodes.isEmpty()) {
                EdifCellInstance node = nodes.poll();
                Collection<EdifCellInstanceEdge> inputEdges = ecic.getInputEdges(node);
                for (EdifCellInstanceEdge inputEdge : inputEdges) {
                    somethingChanged = true;
                    incomingEdges.add(inputEdge);
                    Object source = inputEdge.getSource();
                    if (source instanceof EdifCellInstance) {
                        possibilityNodes.add((EdifCellInstance) source);
                    }
                }
                incomingEdges.addAll(ecic.getInputEdges(node));
            }
            List<EdifCellInstance> nodesToMove = new ArrayList<EdifCellInstance>();
            for (EdifCellInstance eci : possibilityNodes) {
                if (incomingEdges.containsAll(ecic.getOutputEdges(eci))) {
                    nodesToMove.add(eci);
                }
            }
            for (EdifCellInstance nodeToMove : nodesToMove) {
                possibilityNodes.remove(nodeToMove);
                nodes.offer(nodeToMove);
                nodesToDelete.add(nodeToMove);
                somethingChanged = true;
            }
        }

        // now, find out which nets/portRefs to delete
        // Nets that have all sinks included in incomingEdges should be
        // deleted. The rest of the incomingEdges should have their sink
        // portRefs deleted while leaving the rest of the net
        Collection<EdifNet> netsToDelete = new ArrayList<EdifNet>();
        Map<EdifNet, Set<EdifPortRef>> eprsToDelete = new LinkedHashMap<EdifNet, Set<EdifPortRef>>();
        Map<EdifNet, Set<EdifPortRef>> netEdgeMap = new LinkedHashMap<EdifNet, Set<EdifPortRef>>();
        for (EdifCellInstanceEdge edge : incomingEdges) {
            EdifNet net = edge.getSourceEPR().getNet();
            Set<EdifPortRef> eprs = netEdgeMap.get(net);
            if (eprs == null) {
                eprs = new LinkedHashSet<EdifPortRef>();
                netEdgeMap.put(net, eprs);
            }
            eprs.add(edge.getSinkEPR());
        }
        for (EdifNet net : netEdgeMap.keySet()) {
            Set<EdifPortRef> eprs = netEdgeMap.get(net);
            if (eprs.containsAll(net.getSinkPortRefs(false, false)))
                netsToDelete.add(net);
            else {
                eprsToDelete.put(net, eprs);
            }
        }
        
        rDesc.markInstancesToIgnore(nodesToDelete);
        rDesc.markNetsToIgnore(netsToDelete);
        rDesc.markPortRefsToIgnore(eprsToDelete);
        
    }

}
