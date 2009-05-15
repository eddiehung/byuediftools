package edu.byu.ece.edif.util.jsap.commandgroups;

import java.io.PrintStream;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;


public class JEdifDetectionSelectionCommandGroup extends AbstractCommandGroup {

	public static String RAIL_TYPE = "rail_type";
	public static String PORT_NAME = "port_name";
	public static String NO_DOWNSCALE_DETECTION = "no_downscale_detection";
	public static String NO_UPSCALE_DETECTION = "no_upscale_detection";
	public static String NO_OUTPUT_DETECTION = "no_output_detection";
	public static String NO_OBUFS = "no_obufs";
	public static String NO_OREGS = "no_oregs";
	public static String CLOCK_NET = "clock_net";
	
	public static String TRUE = "true";
	public static String FALSE = "false";
	
	public JEdifDetectionSelectionCommandGroup() {
		super();
		// option for dual/single rail
		FlaggedOption rail_type = new FlaggedOption(RAIL_TYPE);
        rail_type.setStringParser(EnumeratedStringParser.getParser("single;dual", false));
        rail_type.setRequired(JSAP.NOT_REQUIRED);
        rail_type.setShortFlag(JSAP.NO_SHORTFLAG);
        rail_type.setLongFlag(RAIL_TYPE);
        rail_type.setDefault("single");
        rail_type.setUsageName(RAIL_TYPE);
        rail_type.setHelp("Rail type. Must be one of the following: single, dual");
        addCommand(rail_type);
        
        // option for port name of detection output
        // the port should have the bus width of the
        // detection type being used
        FlaggedOption port_name = new FlaggedOption(PORT_NAME);
        port_name.setStringParser(JSAP.STRING_PARSER);
        port_name.setRequired(JSAP.REQUIRED);
        port_name.setShortFlag('p');
        port_name.setLongFlag(PORT_NAME);
        port_name.setUsageName(PORT_NAME);
        port_name
                .setHelp("Name of the port that should receive the detection error signals");
        addCommand(port_name);
        
        // option for not using detectors for downscaling
        Switch no_downscale_detection = new Switch(NO_DOWNSCALE_DETECTION);
        no_downscale_detection.setShortFlag(JSAP.NO_SHORTFLAG);
        no_downscale_detection.setLongFlag(NO_DOWNSCALE_DETECTION);
        no_downscale_detection.setDefault(FALSE);
        no_downscale_detection.setHelp("This option disables the default behavior of inserting detectors at locations where the replication factor downscales.");
        addCommand(no_downscale_detection);
        
        // option for not using detectors for upscaling
        Switch no_upscale_detection = new Switch(NO_UPSCALE_DETECTION);
        no_upscale_detection.setShortFlag(JSAP.NO_SHORTFLAG);
        no_upscale_detection.setLongFlag(NO_UPSCALE_DETECTION);
        no_upscale_detection.setDefault(FALSE);
        no_upscale_detection.setHelp("This option disables the default behavior of inserting detectors at locations where the replication factor upscales.");
        addCommand(no_upscale_detection);
        
        // option for not inserting default output detectors
        Switch no_output_detection = new Switch(NO_OUTPUT_DETECTION);
        no_output_detection.setShortFlag(JSAP.NO_SHORTFLAG);
        no_output_detection.setLongFlag(NO_OUTPUT_DETECTION);
        no_output_detection.setDefault(FALSE);
        no_output_detection.setHelp("This option disables the default behavior of inserting detectors at circuit outputs.");
        addCommand(no_output_detection);
        
        // option for disabling the insertion of output buffers on the detection error signals
        Switch no_obufs = new Switch(NO_OBUFS);
        no_obufs.setShortFlag(JSAP.NO_SHORTFLAG);
        no_obufs.setLongFlag(NO_OBUFS);
        no_obufs.setDefault(FALSE);
        no_obufs.setHelp("This option disables the default behavior of inserting output buffers on the detection error signal outputs.");
        addCommand(no_obufs);
        
        // option for disabling the insertion of output registers on the detection error signals
        Switch no_oregs = new Switch(NO_OREGS);
        no_oregs.setShortFlag(JSAP.NO_SHORTFLAG);
        no_oregs.setLongFlag(NO_OREGS);
        no_oregs.setDefault(FALSE);
        no_oregs.setHelp("This option disables the default behavior of inserting output registers on the detection error signal outputs.");
        addCommand(no_oregs);
        
        // option for specifying a clock net to use for output registers
        FlaggedOption clock_net = new FlaggedOption(CLOCK_NET);
        clock_net.setStringParser(JSAP.STRING_PARSER);
        clock_net.setRequired(JSAP.NOT_REQUIRED);
        clock_net.setShortFlag(JSAP.NO_SHORTFLAG);
        clock_net.setLongFlag(CLOCK_NET);
        clock_net.setUsageName(CLOCK_NET);
        clock_net.setHelp("This option specifies a clock net to use for output registers. This option is required unless output register insertion is disabled with the --" + NO_OREGS + " option.");
        addCommand(clock_net);

	}
	
	public static String getRailTypeString(JSAPResult result) {
		return result.getString(RAIL_TYPE);
	}
	
	public static String getDetectionPortName(JSAPResult result) {
		return result.getString(PORT_NAME);
	}
	
	public static boolean noUpscaleDetection(JSAPResult result) {
		return result.getBoolean(NO_UPSCALE_DETECTION);
	}
	
	public static boolean noDownscaleDetection(JSAPResult result) {
		return result.getBoolean(NO_DOWNSCALE_DETECTION);
	}
	
	public static boolean noOutputDetection(JSAPResult result) {
		return result.getBoolean(NO_OUTPUT_DETECTION);
	}
	
	public static boolean noObufs(JSAPResult result) {
		return result.getBoolean(NO_OBUFS);
	}
	
	public static boolean noOregs(JSAPResult result) {
		return result.getBoolean(NO_OREGS);
	}
	
	public static String getClockNetName(JSAPResult result) {
		return result.getString(CLOCK_NET);
	}
	
	public static boolean validateOptions(JSAPResult result, PrintStream err) {
		if (!noOregs(result) && !result.userSpecified(CLOCK_NET)) {
			err.println("Error: a clock net name must be specified unless output register insertion is disabled for detection error signals with the --" + NO_OREGS + " option.");
			return false;
		}
		return true;
	}
	
}
