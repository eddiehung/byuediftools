package edu.byu.ece.edif.util.jsap.commandgroups;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

public class JEdifRPRSelectionCommandGroup extends AbstractCommandGroup {
    
    public JEdifRPRSelectionCommandGroup() {

        FlaggedOption output_port = new FlaggedOption(BIT_THRESHOLD);
        output_port.setStringParser(JSAP.INTEGER_PARSER);
        output_port.setRequired(JSAP.REQUIRED);
        output_port.setShortFlag(JSAP.NO_SHORTFLAG);
        output_port.setLongFlag(BIT_THRESHOLD);
        output_port.setUsageName("Bit weight threshold.");
        output_port.setHelp("Any instances with bit weight greater than or equal to the threshold will be replicated.");
        this.addCommand(output_port);

    }

    public static int getBitWeightThreshold(JSAPResult result) {
        return result.getInt(BIT_THRESHOLD);
    }

    public static final String BIT_THRESHOLD = "bit_threshold";
}
