package edu.byu.ece.edif.arch.xilinx.parts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Shared code for all the XilinxDeviceClass generators. This is based on some code
 * Chris Lavin wrote.
 */
public class XilinxDeviceClassGenerator {

    /**
     * Construct a new class generator and collect all of the data needed
     * to write Java source code for a part library. Data is collected
     * using the Xilinx partgen tool.
     * @param architecture
     */
    protected XilinxDeviceClassGenerator(String family, String partNamePrefix) {
    	_partNamePrefix = partNamePrefix;
    	_family = family;
        _partGenOutput = new ArrayList<String>();
        _partNames = new ArrayList<String>();
        _validPackages = new ArrayList<List<String>>();
        _validSpeedGrades = new ArrayList<List<String>>();

        collectData();
    }

    protected void createJavaSourceFile(String familyPrefix) {
        String fileName = "edu/byu/ece/edif/arch/xilinx/parts/Xilinx" + familyPrefix + "Family.java";
            
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(fileName));
            createFileHeader(buf);
            buf.write("package edu.byu.ece.edif.arch.xilinx.parts;\n\n");            
            buf.write("public class Xilinx" + familyPrefix + "Family extends XilinxFamily {\n\n");
            
            // constructor
            buf.write("\tprivate Xilinx" + familyPrefix + "Family() {\n");
            buf.write("\t\tsuper(\"" + _family + "\", \"" + _partNamePrefix + "\");\n");
            buf.write("\t}\n\n");
            
            // add parts method
            buf.write("\tprotected void addDevices() {\n");
            buf.write("\t\tif(!_initialized) {\n");
            buf.write("\t\t\t_initialized = true;\n");
            for (String currPartName : _partNames) {
                buf.write("\t\t\taddDevice(new " + currPartName.toUpperCase() + "(this));\n");
            }
            buf.write("\t\t}\n\t}\n\n");
            
            // define parts
            int i = 0;
            for (String currPartName : _partNames) {
    
                buf.write("\tclass " + currPartName.toUpperCase() +" extends XilinxDevice {\n\n" );
                buf.write("\t\tpublic " +currPartName.toUpperCase() + "(XilinxFamily family) {\n");
                buf.write("\t\t\tsuper();\n");
                buf.write("\t\t\t_family = family;\n");
                buf.write("\t\t\t_deviceName = \""+currPartName.toUpperCase()+"\";\n");
    
                // Write out Packages
                buf.write("\t\t\t_validPackages = new XilinxPackage[] {");
                for(String pkg : _validPackages.get(i)){
                    buf.write("new XilinxPackage(\""+ pkg.toUpperCase() + "\"), ");
                }
                buf.write("};\n");
    
                // Write out Speed Grades
                buf.write("\t\t\t_validSpeedGrades = new XilinxSpeedGrade[] {");
                for(String speeds : _validSpeedGrades.get(i)){
                    buf.write("new XilinxSpeedGrade(\""+ speeds + "\"), ");
                }
                buf.write("};\n");
                buf.write("\t\t}\n");
                buf.write("\t}\n\n");
                i++;
            }
            
            // singleton instance method
            buf.write("\tpublic static synchronized Xilinx" + familyPrefix + "Family getSingletonObject() {\n");
            buf.write("\t\tif (!_singletonCreated) {\n");
            buf.write("\t\t\t_singletonCreated = true;\n");
            buf.write("\t\t\t_singletonObject = new Xilinx" + familyPrefix + "Family();\n\t\t}\n");
            buf.write("\t\treturn _singletonObject;\n");
            buf.write("\t}\n\n");
            
            buf.write("\tpublic static void initializeFamily() {\n");
            buf.write("\t\tgetSingletonObject();\n\t}\n\n");
            	
        	buf.write("\tprivate static boolean _initialized = false;\n");
        	buf.write("\tprivate static boolean _singletonCreated = false;\n");
        	buf.write("\tprivate static Xilinx" + familyPrefix + "Family _singletonObject;\n");
            buf.write("}\n");
            buf.flush();
    
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error writing file: " + fileName);
            System.exit(1);
        }
    
    }

    public void createFileHeader(Writer writer) throws IOException {
        writer.write("/*\n");
        writer.write(" * This file was auto-generated on " + (new Date()).toString() + "\n");
        writer.write(" * by " + this.getClass().getCanonicalName() + ".\n");
        writer.write(" * See the source code to make changes.\n *\n");
        writer.write(" * Do not modify this file directly.\n");
        writer.write(" */\n\n\n");
    }

    /**
     * Method that starts all of the work.
     */
    protected void collectData() {
        List<String> partGenOutput = generatePartgenOutput(_family);
        parseParts(partGenOutput);
    }

    /**
     * This method parses the List of Strings that was found in the pargen output.
     * It creates a list of part names (_partNames), the valid packages for each
     * partname (_validPackages), and the valid speed grades for each part (_validSpeedGrades)
     * 
     * 
     */
    protected void parseParts(List<String> partgenOutput) {
        int currentLine = 0;

        while (currentLine < partgenOutput.size() -1 ){

            if(currentLine >= 0){

                //Advance to the next line where the part is named
                String[] tokens = partgenOutput.get(currentLine).split("\\s+");
                while(currentLine+1 < partgenOutput.size() -1 && !tokens[0].startsWith("xc")){
                    currentLine++;
                    tokens = partgenOutput.get(currentLine).split("\\s+");
                }

                // Now we should be on the line with the part name
                _partNames.add(tokens[0]);
                List<String> currValidPackages = new ArrayList<String>();
                List<String> currValidSpeedGrades = new ArrayList<String>();

                // Add the speed grades
                for(String token : tokens){
                    if(token.startsWith("-")){
                        currValidSpeedGrades.add(token);
                    }
                }
                // Add packages
                do {
                    currentLine++;
                    tokens = partgenOutput.get(currentLine).split("\\s+");
                    currValidPackages.add(tokens[1]);                   
                }
                while((currentLine+1 < partgenOutput.size()) && !(partgenOutput.get(currentLine+1).startsWith("xc")));
                _validPackages.add(currValidPackages);
                _validSpeedGrades.add(currValidSpeedGrades);
            }
        }       
    }

    /**
     * This function runs Xilinx partgen and stores all output in an ArrayList of Strings
     * where each string is a line from the partgen output. This output is stored in the
     * class member named "output". It is parsed by the "parseNextPart" method.
     * 
     * You give it an architecture and it will tell you what the valid parts are for the
     * architecture.
     * 
     * @param arch The parameter to pass to partgen -arch
     */
    protected static List<String> generatePartgenOutput(String arch){
        BufferedReader input;
        Process p;
        String line;
        ArrayList<String> output = new ArrayList<String>();
        try {
            p = Runtime.getRuntime().exec("partgen -arch " + arch.toLowerCase());
            input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while((line = input.readLine()) != null){
                output.add(line);
            }
            input.close();
            p.waitFor();
            p.destroy();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("There was an error running partgen with arch:"+arch);
            System.err.println("Check that Xilinx tools are on your path.");
            System.err.println("Also, make sure " + arch + " is a valid Xilinx architecture.");
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return output;
    }

    
    protected String _partNamePrefix;
    protected String _family;
    protected List<String> _partGenOutput;
    protected List<String> _partNames;
    protected List<List<String>> _validPackages;
    protected List<List<String>> _validSpeedGrades;

}

