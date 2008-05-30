/*
 * This testbench tests the JHDL support within the EDIF tool.
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
package edu.byu.ece.edif.util.export.jhdl.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.util.export.jhdl.BuildWrapper;
import edu.byu.ece.edif.util.merge.EdifMergeParser;
import edu.byu.ece.edif.util.parse.EdifParser;

/**
 * This testbench tests the JHDL support within the EDIF tool.
 * 
 * @author Nathan Rollins
 * @version $Id:JHDLTests.java 144 2008-04-02 01:40:38Z jamesfcarroll $
 */

public class JHDLTests {

    /**
     * Default and only Constructor
     */
    public JHDLTests() {

        try {
            _runSpecificTests();
        } catch (IOException e) {
            System.err.println("ERROR: " + e);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        JHDLTests jhdlt = new JHDLTests();

        System.out.println("Files Tested: " + _parseNum);
        System.out.println("\tParsing ERRORS: " + _parseErrors);
        System.out.println("\tWrapper ERRORS: " + _wrapperErrors);
    }

    /**
     * This function parses and builds a JHDL wrapper for the given EDIF file.
     * 
     * @param edifFileName
     * @param technologyName
     * @param dirs
     * @param subFiles
     */
    protected void _parseEdifFile(String edifFileName, String technologyName, Collection dirs, Set subFiles) {

        System.out.print("PARSING: " + edifFileName + "  TECHNOLOGY: " + technologyName);
        if (dirs == null)
            System.out.print("  NO DIRS");
        else
            System.out.print("  DIRS: " + dirs);
        if (subFiles == null)
            System.out.println("  NO SUBFILES");
        else
            System.out.println("  SUBFILES: " + subFiles);
        // variable declarations
        String baseEdifEnvironmentName = edifFileName.substring(edifFileName.lastIndexOf(File.separatorChar) + 1,
                edifFileName.lastIndexOf('.'));
        String className = baseEdifEnvironmentName + BuildWrapper.CLASSNAME_EXTENSION;
        String newFileName = className + ".java";
        String writeWrapperTopEdifFileName = edifFileName;
        EdifLibrary xilinxLib = edu.byu.ece.edif.arch.xilinx.XilinxLibrary.library;
        EdifEnvironment edif_file = null;

        // actually parse the EDIF and create a JHDL file
        try {
            if (subFiles == null)
                edif_file = EdifParser.translate(edifFileName, xilinxLib);
            else
                edif_file = edu.byu.ece.edif.util.merge.EdifMergeParser.parseAndMergeEdif(edifFileName, dirs, subFiles,
                        xilinxLib);
        } catch (Exception e) {
            _parseErrors++;
            System.err.println("ERROR: Parsing EDIF FILE - " + edifFileName);
        }
        if (edif_file == null)
            System.out.println("ERROR: NULL EdifEnvironment");
        else {
            EdifDesign edif_design = edif_file.getTopDesign();
            EdifCellInstance topCellInstance = edif_design.getTopCellInstance();
            EdifCell topCell = topCellInstance.getCellType();

            try {
                BuildWrapper.createJHDLdotJavaFile(newFileName, technologyName, topCell, writeWrapperTopEdifFileName,
                        subFiles);
            } catch (Exception e) {
                _wrapperErrors++;
                System.err.println("ERROR: Creating EDIF FILE - " + edifFileName);
            }
        }
    }

    /**
     * This function parses and builds a JHDL wrapper for the given EDIF file.
     * 
     * @param edifFileName
     * @param technologyName
     * @param dirs
     * @param subFiles
     */
    protected void _parseEdifFileCommandline(File currDir, String technologyName, Collection dirs, Set subFiles) {

        System.out.print("PARSING: " + currDir.getPath() + "  TECHNOLOGY: " + technologyName);
        if (dirs == null)
            System.out.print("  NO DIRS");
        else
            System.out.print("  DIRS: " + dirs);
        if (subFiles == null)
            System.out.println("  NO SUBFILES");
        else
            System.out.println("  SUBFILES: " + subFiles);

        String opts = "";
        if (dirs != null) {
            for (Iterator dirIt = dirs.iterator(); dirIt.hasNext();) {
                String dir = (String) dirIt.next();
                opts += " -L " + dir;
            }
        }
        if (subFiles != null) {
            for (Iterator fileIt = subFiles.iterator(); fileIt.hasNext();) {
                String file = (String) fileIt.next();
                opts += " -f " + file;
            }
        }
        opts += " -nocvt";
        opts += " -w -b2EDIF";
        String cmd = "java -Xmx800M edu.byu.ece.edif.util.export.jhdl.BuildWrapper " + currDir.getPath() + " -t "
                + technologyName + opts;
        int parseErr = _runCommand(cmd, currDir, false);
        if (parseErr > 0) {
            _parseErrors++;
        } else {
            _simulateDesign(currDir, technologyName);
        }
    }

    protected int _runCommand(String cmd, File currDir, boolean printOutput) {
        int retval = 0;

        String s = "";
        try {
            Process p = Runtime.getRuntime().exec(cmd, null, currDir.getParentFile());
            int i = p.waitFor();
            if (i == 0) {
                if (printOutput) {
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    //	    			read the output from the command
                    while ((s = stdInput.readLine()) != null) {
                        System.out.println(s);
                    }
                }
            } else {
                retval++;
                BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                //	    		read the output from the command
                while ((s = stdErr.readLine()) != null) {
                    System.out.println(s);
                }

            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return retval;
    }

    protected void _simulateDesign(File currDir, String technologyName) {
        StringBuffer sb = new StringBuffer();
        String cmdFile = "cmdFile.cmd";
        String baseDesignName = currDir.getPath().substring(currDir.getPath().lastIndexOf(File.separatorChar) + 1,
                currDir.getPath().lastIndexOf('.'));

        sb.append("load " + baseDesignName + "_wrapper\n");
        sb.append("target " + technologyName + "\n");
        sb.append("cycle\n");
        sb.append("cycle\n");
        sb.append("exit\n");

        String path = currDir.getParent() + "/" + cmdFile;
        printFile(path, sb.toString());
        String cmd = "javac " + currDir.getParent() + "/" + baseDesignName + "_wrapper.java";
        _runCommand(cmd, currDir, false);
        cmd = "java -Xmx800M jab -nw -s " + path;
        _wrapperErrors += _runCommand(cmd, currDir, false);

    }

    protected void _runSpecificTests() throws IOException {
        String virtex = "Virtex";
        String virtex2 = "Virtex2";
        Set subfiles = null;

        _runTestsAll(null, _srcDir + "hitachi/", virtex2, subfiles);
        //_runTestsAll(null, _srcDir+"misc/", virtex2, subfiles);
        _runTestsAll(null, _srcDir + "netlist_testarray/", virtex2, subfiles);
        _runTestsAll(null, _srcDir + "signalGen/", virtex2, subfiles);

        _runTestsAll(null, _srcDir + "isi/conf_body/", virtex2, subfiles);
        _runTestsAll(null, _srcDir + "isi/osiris_chip/", virtex2, subfiles);
        _runTestsAll(null, _srcDir + "isi/pci_conftest/", virtex2, subfiles);

        subfiles = new TreeSet();
        subfiles.add(_srcDir + "fft/afifo4kx32.edn");
        subfiles.add(_srcDir + "fft/inafifo2kx32.edn");
        subfiles.add(_srcDir + "fft/sfft_1k_32.edn");
        _runTestsAll(null, _srcDir + "fft/fftcore.edf", virtex2, subfiles);

        subfiles.clear();
        subfiles.add(_srcDir + "isi/bench/fifo8x512.edn");
        subfiles.add(_srcDir + "isi/bench/mult12x8.edn");
        _runTestsAll(null, _srcDir + "isi/bench/osiris_chip_3x3conv.edf", virtex2, subfiles);

        subfiles.clear();
        subfiles.add(_srcDir + "isi/pama_net/icon.edn");
        subfiles.add(_srcDir + "isi/pama_net/ila.edn");
        _runTestsAll(null, _srcDir + "isi/pama_net/net.edf", virtex2, subfiles);

        subfiles.clear();
        subfiles.add(_srcDir + "isi/processor_fpga/cmd_table.edn");
        subfiles.add(_srcDir + "isi/processor_fpga/debug_table.edn");
        subfiles.add(_srcDir + "isi/processor_fpga/prog_table32.edn");
        subfiles.add(_srcDir + "isi/processor_fpga/prog_table4.edn");
        _runTestsAll(null, _srcDir + "isi/processor_fpga/processor_fpga.edf", virtex2, subfiles);

    }

    /**
     * This static method will write a dotty file with the given file text.
     * 
     * @param filename - the name of the file to create
     * @param data - the file text body
     */
    public static void printFile(String filename, String data) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write(data);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * This function recursively calls itself when #fileName refers to a
     * directory (unless the directory is the CVS directory). If $fileName
     * refers to an EDIF file the file is parsed.
     * 
     * @param parent - directory of the file or directory
     * @param fileName - the file or directory
     * @throws IOException
     */
    protected void _runTestsAll(String parent, String fileName, String technologyName, Set subfiles) throws IOException {
        Collection dirs = EdifMergeParser.createDefaultDirs();
        File currDir = new File(parent, fileName);

        if (!currDir.isDirectory()) {
            if (currDir.getPath().endsWith(".edn") || currDir.getPath().endsWith(".edf")) {
                _parseNum++;
                //_parseEdifFile(currDir.getPath(), technologyName, dirs, subfiles);
                _parseEdifFileCommandline(currDir, technologyName, dirs, subfiles);
            }
        } else {
            String[] files = currDir.list();
            for (int i = 0; i < files.length; i++) {
                if (!files[i].startsWith("CVS")) {
                    _runTestsAll(currDir.getPath(), files[i], technologyName, subfiles);
                }
            }
        }
    }

    /**
     * Number of EDIF Parsing Errors.
     */
    private static int _parseErrors = 0;

    /**
     * Number of EDIF files attempted to parse
     */
    private static int _parseNum = 0;

    /**
     * Number of EDIF wrapper creation Errors.
     */
    private static int _wrapperErrors = 0;

    /**
     * Location of the EDIF test file directories
     */
    private String _srcDir = "../../edifsrc/";
}
