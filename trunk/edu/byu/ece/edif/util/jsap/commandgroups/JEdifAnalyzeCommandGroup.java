package edu.byu.ece.edif.util.jsap.commandgroups;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;


public class JEdifAnalyzeCommandGroup extends AbstractCommandGroup {

	public JEdifAnalyzeCommandGroup() {
		
        Switch bad_cut_conn = new Switch(USE_BAD_CUT_CONN);
        bad_cut_conn.setShortFlag(JSAP.NO_SHORTFLAG);
        bad_cut_conn.setLongFlag("use_bad_cut_conn");
        bad_cut_conn.setDefault(FALSE);
        bad_cut_conn.setHelp("Use bad cut connections option.");
        this.addCommand(bad_cut_conn);
        
        Switch no_iob_fb = new Switch(NO_IOB_FB);
        no_iob_fb.setShortFlag(JSAP.NO_SHORTFLAG);
        no_iob_fb.setLongFlag(NO_IOB_FB);
        no_iob_fb.setDefault(FALSE);
        no_iob_fb.setHelp("The user may wish to exclude the IOBs (specifically inout ports) from feedback analysis if there is no true feedback (by design). This may greatly reduce the amount of feedback detected.");
        this.addCommand(no_iob_fb);
        
		// Output filename flag
		FlaggedOption output_file_option = new FlaggedOption(OUTPUT_OPTION);
		output_file_option.setStringParser(JSAP.STRING_PARSER);
		output_file_option.setRequired(JSAP.REQUIRED);
		output_file_option.setShortFlag('o');
		output_file_option.setLongFlag(OUTPUT_OPTION);
		output_file_option.setUsageName(OUTPUT_OPTION);
		output_file_option.setHelp("Circuit description output filename. Required.");
		this.addCommand(output_file_option);

		// Additional command for indicating component cutting
		char LIST_DELIMITER = ',';
		FlaggedOption cut_component_option = new FlaggedOption(CUT_CELL_STRING);
        cut_component_option.setStringParser(JSAP.STRING_PARSER);
        cut_component_option.setRequired(JSAP.NOT_REQUIRED);
        cut_component_option.setShortFlag(JSAP.NO_SHORTFLAG);
        cut_component_option.setLongFlag(CUT_CELL_STRING);
        cut_component_option.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        cut_component_option.setList(JSAP.LIST);
        cut_component_option.setListSeparator(LIST_DELIMITER);
        cut_component_option.setUsageName("cell_type");
        cut_component_option.setHelp("Comma-separated list of cell types that will be cut in circuit graph analysis");
        this.addCommand(cut_component_option);

		// Additional command for indicating instance cutting
        FlaggedOption cut_instance_option = new FlaggedOption(CUT_INSTANCE_STRING);
        cut_instance_option.setStringParser(JSAP.STRING_PARSER);
        cut_instance_option.setRequired(JSAP.NOT_REQUIRED);
        cut_instance_option.setShortFlag(JSAP.NO_SHORTFLAG);
        cut_instance_option.setLongFlag(CUT_INSTANCE_STRING);
        cut_instance_option.setAllowMultipleDeclarations(JSAP.MULTIPLEDECLARATIONS);
        cut_instance_option.setList(JSAP.LIST);
        cut_instance_option.setListSeparator(LIST_DELIMITER);
        cut_instance_option.setUsageName("cell_instance");
        cut_instance_option.setHelp("Comma-separated list of cell instances that will be cut in circuit graph analysis");
        this.addCommand(cut_instance_option);
	
	
	}
	
    public static boolean badCutConn(JSAPResult result) {
        return result.getBoolean(USE_BAD_CUT_CONN);
    }
    
    public static boolean noIOBFB(JSAPResult result) {
        return result.getBoolean(NO_IOB_FB);
    }
    
    public static String getOutputFilename(JSAPResult result) {
    	return result.getString(OUTPUT_OPTION);
    }

    public static String[] getCellsToCut(JSAPResult result) {
    	return result.getStringArray(CUT_CELL_STRING);    	
    }
    
    public static String[] getInstancesToCut(JSAPResult result) {
    	return result.getStringArray(CUT_INSTANCE_STRING);    	
    }
    
    /**
     * Remove IOBs from feedback (Don't count as feedback)
     */
    public static final String OUTPUT_OPTION = "output";
    
    public static final String NO_IOB_FB = "no_iob_feedback";
    
    public static final String USE_BAD_CUT_CONN = "use_bad_cut_conn";

    public static final String CUT_CELL_STRING = "cut_cell";
    
    public static final String CUT_INSTANCE_STRING = "cut_instance";


}
