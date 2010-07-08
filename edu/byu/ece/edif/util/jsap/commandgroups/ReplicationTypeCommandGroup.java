package edu.byu.ece.edif.util.jsap.commandgroups;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;

import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationType;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationTypeMapper;
import edu.byu.ece.edif.tools.replicate.nmr.ReplicationTypeMapper.ReplicationTypes;

/**
 * Adds a command for specifying triplication or duplication.
 */
public class ReplicationTypeCommandGroup extends AbstractCommandGroup {

    public static String REPLICATION_TYPE = "replication_type";
    public static String TRIPLICATION = "triplication";
    public static String DUPLICATION = "duplication";
    
    public ReplicationTypeCommandGroup() {
        super();

        // option for duplication/triplication
        
        ReplicationTypes[] validReplicationTypes = ReplicationTypeMapper.ReplicationTypes.values();
        String semiColonString = "";
        String commaString = "";
        for (int i = 0; i < validReplicationTypes.length; i++) {
        	semiColonString += validReplicationTypes[i].name();
        	commaString += validReplicationTypes[i].name().toLowerCase();
        	if (i < (validReplicationTypes.length - 1)) {
        		semiColonString += ";";
        		commaString += ", ";
        	}
        }
        
        _replication_type = new FlaggedOption(REPLICATION_TYPE);
        _replication_type.setStringParser(EnumeratedStringParser.getParser(semiColonString, false));
        _replication_type.setRequired(JSAP.REQUIRED);
        _replication_type.setShortFlag(JSAP.NO_SHORTFLAG);
        _replication_type.setLongFlag(REPLICATION_TYPE);
        _replication_type.setUsageName(REPLICATION_TYPE);
        _replication_type.setHelp("Replication type to use for this run. Must be one of the following: " + commaString);

        addCommand(_replication_type);
    }

    public static ReplicationType getReplicationType(JSAPResult result, NMRArchitecture arch) {
        ReplicationType replicationType = null;
        String repString = result.getString(REPLICATION_TYPE);
        return ReplicationTypeMapper.getReplicationType(repString, arch);
    }
    
    public static final String INPUT_OPTION = "rep_desc";

    protected FlaggedOption _replication_type;

}
