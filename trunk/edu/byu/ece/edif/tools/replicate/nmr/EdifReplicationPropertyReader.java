package edu.byu.ece.edif.tools.replicate.nmr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import edu.byu.ece.edif.core.BooleanTypedValue;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifTypedValue;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.core.StringTypedValue;
import edu.byu.ece.edif.tools.replicate.wiring.PreMitigatedPortGroup;

public class EdifReplicationPropertyReader {

	/* Do not add a restoring organ (i.e. voters) on nets with this property */
	public static String DO_NOT_RESTORE = "do_not_restore";
	
	/* Force the addition of a restoring organ (i.e. a voter) on nets with this property */
	public static String FORCE_RESTORE = "force_restore";
	
	/* Do not add a detecting organ on nets with this property */
	public static String DO_NOT_DETECT = "do_not_detect";
	
	/* Force the addition of a detecting organ on nets with this property */
	public static String FORCE_DETECT = "force_detect";
	
	/* Do not flatten instances of cell types that have this property (the property
	 * goes on the cell, not the instance) */
	public static String DO_NOT_FLATTEN = "do_not_flatten";
	
	/* This property goes on ports and is used to specify pre-mitigated port groups
	 * according to the following format: "<replication type>:<port group name>:<port index>"
	 * where <replication type> is one of "triplication" or "duplication", <port group name> is 
	 * a name used to group ports together, and <port index> is the index of the port in question
	 * within the group.
	 */
	public static String PORT_GROUP = "port_group";
	
	/* This is the delimeter used in port_group property strings */
	public static String PORT_GROUP_DELIMETER = ":";
	
	/* This property is put on half latch constant instances so that they can
	 * be automatically replicated. They will be replicated with the highest replication
	 * factor possible. (i.e. if the user runs JEdifNMRSelection twice, once with duplication
	 * and once with triplication, the half latch constants will be triplicated. If the user
	 * only runs JEdifNMRSelection once and with duplication, the constants will be duplicated.)
	 */
	public static String HALF_LATCH_CONSTANT = "half_latch_constant";

	/* This property goes on instances to indicate that the instances should not be replicated. The
	 * effect is the same as if the user had specified not to replicate the instance on the command line.
	 * The code that checks for this property is in the excludeFromReplication method of the JEdifNMRSelection
	 * class.
	 */
	public static String DO_NOT_REPLICATE = "do_not_replicate";
	
	public static BooleanTypedValue TRUE_VALUE = new BooleanTypedValue(true);
	
	public static boolean isPremitigatedCell(EdifCell cell) {
	    // check the ports of the cell and see if any of them have a port_group property with a valid format
	    // if so, it must be a pre-mitigated cell (this keeps us from needing a specific pre-mitigated cell property)
	    boolean isPreMitigated = false;
	    for (EdifPort port : cell.getPortList()) {
	        if (hasPortGroup(port)) {
	            isPreMitigated = true;
	            break;
	        }
	    }
	    return isPreMitigated;
	}
	
	public static boolean isDoNotReplicateInstance(EdifCellInstance eci) {
		return hasTrueBooleanValue(eci.getProperty(DO_NOT_REPLICATE));
	}
	
	public static boolean hasPortGroup(EdifPort port) {
	    boolean hasPortGroup = false;
	    Property port_group = port.getProperty(PORT_GROUP);
        if (port_group != null) {
            EdifTypedValue value = port_group.getValue();
            if (value instanceof StringTypedValue) {
                StringTypedValue stringValue = (StringTypedValue) value;
                String string = stringValue.getStringValue();
                String[] strings = string.split(PORT_GROUP_DELIMETER);
                if (strings.length == 3) {
                    String replicationTypeString = strings[0];
                    String indexString = strings[2];
                    boolean typeStringValid = false;
                    boolean indexStringValid = true;
                    for (ReplicationTypeMapper.ReplicationTypes validReplicationType : ReplicationTypeMapper.ReplicationTypes.values()) {
                        if (validReplicationType.name().compareToIgnoreCase(replicationTypeString) == 0) {
                            typeStringValid = true;
                            break;
                        }
                    }
                    try {
                        Integer.parseInt(indexString);
                    }
                    catch (NumberFormatException e) {
                        indexStringValid = false;
                    }
                    if (typeStringValid && indexStringValid) {
                        hasPortGroup = true;
                    }
                }
            }
        }
        return hasPortGroup;
	}
	
	public static boolean isHalfLatchConstantInstance(EdifCellInstance instance) {
	    return hasTrueBooleanValue(instance.getProperty(HALF_LATCH_CONSTANT));
	}
	
	public static boolean isPreMitigatedInstance(EdifCellInstance instance) {
		return isPremitigatedCell(instance.getCellType());		
	}
	
	public static boolean isDoNotRestoreLocation(EdifNet net) {
		return hasTrueBooleanValue(net.getProperty(DO_NOT_RESTORE));
	}
	
	public static boolean isForceRestoreLocation(EdifNet net) {
		return hasTrueBooleanValue(net.getProperty(FORCE_RESTORE));
	}
	
	public static boolean isDoNotDetectLocation(EdifNet net) {
		return hasTrueBooleanValue(net.getProperty(DO_NOT_DETECT));
	}
	
	public static boolean isForceDetectLocation(EdifNet net) {
		return hasTrueBooleanValue(net.getProperty(FORCE_DETECT));
	}
	
	public static boolean isDoNotRestoreOrDoNotDetectLocation(EdifNet net) {
	    return (isDoNotRestoreLocation(net) || isDoNotDetectLocation(net));
	}
	
	public static boolean isDoNotFlattenCell(EdifCell cell) {
	    return hasTrueBooleanValue(cell.getProperty(DO_NOT_FLATTEN));
	}
	
	public static Collection<PreMitigatedPortGroup> getPreMitigatedPortGroups(EdifCell cell, NMRArchitecture arch) {
		
		List<PreMitigatedPortGroup> portGroups = new ArrayList<PreMitigatedPortGroup>();
		Queue<EdifCell> cells = new LinkedList<EdifCell>();
		List<EdifCell> alreadyTraversed = new ArrayList<EdifCell>();
		cells.offer(cell);
		while (!cells.isEmpty()) {
			EdifCell currentCell = cells.poll();
			for (EdifCellInstance subCellInstance : currentCell.getSubCellList()) {
				EdifCell subCell = subCellInstance.getCellType();
				if (!alreadyTraversed.contains(subCell)) {
					cells.offer(subCell);
					alreadyTraversed.add(subCell);
				}
			}
			
			Map<String, Map<Integer, EdifPort>> portGroupMap = new LinkedHashMap<String, Map<Integer, EdifPort>>();
			Map<String, ReplicationType> replicationTypeMap = new LinkedHashMap<String, ReplicationType>();
			for (EdifPort port : currentCell.getPortList()) {
				Property port_group = port.getProperty(PORT_GROUP);
				if (port_group != null) {
					EdifTypedValue value = port_group.getValue();
					if (value instanceof StringTypedValue) {
						StringTypedValue stringValue = (StringTypedValue) value;
						String string = stringValue.getStringValue();
						String[] strings = string.split(PORT_GROUP_DELIMETER);
						if (strings.length == 3) {
							String replicationTypeString = strings[0];
                            String groupName = strings[1];
							String groupIndexString = strings[2];
							ReplicationType replicationType = ReplicationTypeMapper.getReplicationType(replicationTypeString, arch);
							ReplicationType prevReplicationType = replicationTypeMap.get(groupName);
							if (prevReplicationType != null) {
								if (prevReplicationType != replicationType)
									throw new EdifRuntimeException("Error: Conflicting replication types in a single port group");
							}
							else {
								replicationTypeMap.put(groupName, replicationType);
							}
							int groupIndex = -1;
							try {
								groupIndex = Integer.parseInt(groupIndexString);
							}
							catch (NumberFormatException e) {
								throw new EdifRuntimeException("Error: Incorrectly formatted port group property string: '" + string + "'");
							}
							Map<Integer, EdifPort> indexMap = portGroupMap.get(groupName);
							if (indexMap == null) {
								indexMap = new LinkedHashMap<Integer, EdifPort>(3);
								portGroupMap.put(groupName, indexMap);
							}
							if (indexMap.get(groupIndex) != null)
								throw new EdifRuntimeException("Error: Duplicate port index in a single port group");
							indexMap.put(groupIndex, port);
						}
						else {
						    throw new EdifRuntimeException("Error: incorrectly formatted port group property string: " + string);
						}
					}
				}
			}
			
			
			for (String groupName : portGroupMap.keySet()) {
			    List<EdifPort> orderedPortList = new ArrayList<EdifPort>(3);
			    ReplicationType replicationType = replicationTypeMap.get(groupName);
				int replicationFactor = replicationType.getReplicationFactor();
				Map<Integer, EdifPort> indexMap = portGroupMap.get(groupName);
				if (indexMap.size() > replicationFactor)
					throw new EdifRuntimeException("Error: Port group '" + groupName + "' should have " + replicationFactor + " ports but has " + indexMap.size() + " ports.");
				for (int i = 0; i < replicationFactor; i++) {
					EdifPort port = indexMap.get(i);
					if (port == null)
						throw new EdifRuntimeException("Error: Port group '" + groupName + "' is missing a port with index " + i);
					orderedPortList.add(port);
				}
				PreMitigatedPortGroup portGroup = new PreMitigatedPortGroup(orderedPortList, replicationType);
				portGroups.add(portGroup);
			}	
		}
		return portGroups;
	}

	public static boolean hasTrueBooleanValue(Property property) {
		boolean result = false;
		if (property != null && property.getValue().equals(TRUE_VALUE))
			result = true;
		return result;
	}
	
}
