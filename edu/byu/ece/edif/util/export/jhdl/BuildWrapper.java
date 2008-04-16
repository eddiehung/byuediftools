/*
 * TODO: Insert class description here.
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
package edu.byu.ece.edif.util.export.jhdl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import de.uni_frankfurt.grimm.vhdl.InstanceHierarchy;

import byucc.jhdl.apps.Viewers.Event.JHDLHostWidgetInterface;
import byucc.jhdl.apps.Viewers.cvt.cvtFrame;
import byucc.jhdl.base.Cell;
import byucc.jhdl.base.CellList;
import byucc.jhdl.base.HWSystem;
import byucc.jhdl.base.HelperLibrary;
import byucc.jhdl.base.NodeList;
import byucc.jhdl.base.Wire;
import byucc.jhdl.base.WireList;
import edu.byu.ece.edif.arch.xilinx.XilinxGenLib;
import edu.byu.ece.edif.arch.xilinx.XilinxLibrary;
import edu.byu.ece.edif.core.BasicEdifBusNamingPolicy;
import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifPrintWriter;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.util.parse.EdifParser;
import edu.byu.ece.edif.util.parse.ParseException;

/**
 * Includes functionality for loading a design directly into cvt.
 * <p>
 * TODO:
 * <ul>
 * <li> Allow user to specify name of top-level design (default: top-level
 * design is the top design in first edif file
 * </ul>
 * 
 * @author Welson Sun, Tyler Anderson
 * @version $Id$
 */
public class BuildWrapper extends JHDLWrapper {

    public static void connectDanglingPorts(InstanceHierarchy hier) {

        EdifCell topCell = hier.getInstanceCellType();
        Map<EdifCellInstance, Collection<EdifPort>> uPortMap = topCell.getUnconnectedInnerPorts();
        if (uPortMap != null) {
            for (EdifCellInstance eci : uPortMap.keySet()) {
                Collection uPorts = uPortMap.get(eci);

                for (Iterator portIt = uPorts.iterator(); portIt.hasNext();) {
                    EdifPort port = (EdifPort) portIt.next();
                    System.err.println("WARNING: Dangling port - "
                            + BasicEdifBusNamingPolicy.getBusBaseNameStatic(port.getName()) + " Connecting GND.");
                    gndPort(topCell, port);
                }
            }

            InstanceHierarchy[] children = hier.getChildren();
            for (int i = 0; i < children.length; i++) {
                connectDanglingPorts(children[i]);
            }
        }
    }

    /**
     * Prints out a *_wrapper.java file for the passed-in EdifCell.
     * 
     * @param newFileName The file name for the wrapper
     * @param technologyName The technology this wrapper will use
     * @param topcell The Top cell of the design for the wrapper file
     * @param subFiles The sub files, if any, needed by the top EdifFile
     */
    public static void createJHDLdotJavaFile(String newFileName, String technologyName, EdifCell topcell,
            String topFileName, Collection subFiles) {
        String className = newFileName.substring(0, newFileName.lastIndexOf('.'));

        // Create the wrapper file for writing
        PrintWriter fp;
        try {
            fp = new PrintWriter(new FileOutputStream(newFileName));
        } catch (IOException e) {
            throw new RuntimeException("ERROR: Cannot create file:" + newFileName + " for writing");
        }

        // Generate File
        writeClassHeader(fp, technologyName, className);
        writeCellInterface(fp, topcell);
        writeConstructor(fp, topcell, className);
        writePortWires(fp, topcell);
        writeClassGuts(fp, topcell, technologyName, className, topFileName, subFiles);

        fp.close();
    }

    public static Collection findTrueInputPortRefs(EdifNet net) {
        ArrayList retval = new ArrayList();

        for (Iterator portRefIt = net.getInputPortRefs().iterator(); portRefIt.hasNext();) {
            EdifPortRef portRef = (EdifPortRef) portRefIt.next();
            EdifPort port = portRef.getPort();

            if (port.getDirection() != EdifPort.INOUT)
                retval.add(portRef);
        }

        return retval;
    }

    public static Collection findTrueOutputPortRefs(EdifNet net) {
        ArrayList retval = new ArrayList();

        for (Iterator portRefIt = net.getOutputPortRefs().iterator(); portRefIt.hasNext();) {
            EdifPortRef portRef = (EdifPortRef) portRefIt.next();
            EdifPort port = portRef.getPort();
            EdifCellInstance cellInst = portRef.getCellInstance();
            EdifCell cell = XilinxGenLib.BUF();
            if (cellInst != null) {
                cell = cellInst.getCellType();
            }

            if (port.getDirection() != EdifPort.INOUT)
                //				(cell.getName().equalsIgnoreCase("PULLUP")) || cell.getName().equalsIgnoreCase("PULLDOWN")) 
                retval.add(portRef);
        }

        return retval;
    }

    public static void gndPort(EdifCell parent, EdifPort inPort) {
        EdifCell type = XilinxGenLib.GND();
        String gndName = "GND";
        String netName = "NET";
        String portName = "G";
        try {
            EdifCellInstance gndeci = new EdifCellInstance(gndName + (_gndcnt++), parent, type);
            EdifPort gndPort = parent.addPortUniqueName(portName, 1, EdifPort.OUT);
            EdifNet gndNet = new EdifNet(netName + (_netcnt++));
            parent.addSubCell(gndeci);

            EdifPortRef gndPortRefSrc = new EdifPortRef(gndNet, gndPort.getSingleBitPort(0), null);
            gndNet.addPortConnection(gndPortRefSrc);
            for (EdifSingleBitPort esbp : inPort.getSingleBitPortList()) {
                EdifPortRef gndPortRefSink = new EdifPortRef(gndNet, esbp, null);
                gndNet.addPortConnection(gndPortRefSink);
            }
            parent.addNet(gndNet);
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }
    }

    public static void main(String[] args) {
        // 1. Process the command line arguments

        /** Argument specifying whether or not to merge. * */
        boolean merge = true;
        /**
         * Argument holding the Collection of search directories for merging.
         */
        Collection<String> dirs = null;
        /** Argument holding the Collection of sub files for merging. * */
        Set subFiles = null;
        /**
         * Argument specifying whether or not to write the wrapper.java file.
         */
        boolean writeWrapper = false;
        /** Argument specifying the technology. * */
        String technologyName = null;
        /** Argument specifying the topEdifFile name for parsing. * */
        String edifFileName = null;
        /**
         * Argument specifying the filename to write the completed/merged Edif
         * file to.
         */
        String writeEdifFileName = null;
        /** Argument specifying whether or not to load the circuit in cvt. * */
        boolean nocvt = false;
        /**
         * Arg specifying whether to have the option of going from EDIF to JHDL,
         * and back to EDIF again.
         */
        boolean addEdifCellInstanceAsProperty = false;

        try {
            // If the option has help in it then show the options.
            if (args[0].equals("--help")) {
                printHelp();
                System.exit(1);
            }
            // ensure that the first argument is the edif file name
            if (!(args[0].endsWith(".edn") || args[0].endsWith(".edf"))) {
                System.err.println("ERROR: First argument must be the EDIF file name.");
                printUsage();
                System.exit(1);
            }
            // If the wrong # of args was delivered then print usage.
            if (args.length < MINARGNUMBER) {
                printUsage();
                System.exit(1);
            }
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-w"))// The write wrapper flag.
                    writeWrapper = true;
                else if (args[i].equals("-t")) {// Technology flag.
                    i++;
                    if (!checkTechnologyValidity(args[i])) {
                        System.err.println("Invalid technology: " + args[i]);
                        System.err.println(listValidTechnologies());
                        System.exit(1);
                    }
                    technologyName = args[i];
                } else if (args[i].equals("-nomerge"))
                    merge = false;
                else if (args[i].equals("-nocvt"))
                    nocvt = true;
                else if (args[i].equals("-b2EDIF"))
                    addEdifCellInstanceAsProperty = true;
                else if (args[i].equals("-wedif")) {
                    i++;
                    writeEdifFileName = args[i];
                }
            }
            //dirs = EdifMergeParser.parseDirectories(args);
            dirs = edu.byu.ece.edif.util.merge.EdifMergeParser.parseArguments(args, "-L");

            //EdifMergeParser.SubFiles subfiles = EdifMergeParser.parseFiles(args);
            //edifFileName = subfiles.getTopFilename();
            edifFileName = args[0];
            //subFiles = subfiles.getSubFiles();
            subFiles = edu.byu.ece.edif.util.merge.EdifMergeParser.parseArguments(args, "-f");
            //edifFileName = (String)subFiles.get(0);

            // If we don't have a technology or a filename then print usage.
            if (edifFileName == null || technologyName == null) {
                printUsage();
                System.exit(1);
            }
        } catch (Throwable t) {
            System.err.println("Error parsing args");
            System.exit(1);
        }

        String baseEdifEnvironmentName = edifFileName.substring(edifFileName.lastIndexOf(File.separatorChar) + 1,
                edifFileName.lastIndexOf('.'));
        String className = baseEdifEnvironmentName + CLASSNAME_EXTENSION;
        String newFileName = className + ".java";

        // 2. Parse the edif file
        EdifEnvironment edif_file = null;

        try {

            EdifLibrary xilinxLib = edu.byu.ece.edif.arch.xilinx.XilinxLibrary.library;

            if (!merge) {
                edif_file = EdifParser.translate(edifFileName, xilinxLib);
            } else
                // edif_file = EdifMergeParser.translate(edifFileName,
                // dirs, subFiles, xilinxLib);	            	
                edif_file = edu.byu.ece.edif.util.merge.EdifMergeParser.parseAndMergeEdif(edifFileName, dirs, subFiles,
                        xilinxLib);
        } catch (ParseException e) {
            System.err.println("Parsing error!\n" + e);
            e.printStackTrace();
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println("File " + edifFileName + " not found");
            e.printStackTrace();
            System.exit(1);
        }

        EdifDesign edif_design = edif_file.getTopDesign();
        EdifCellInstance topCellInstance = edif_design.getTopCellInstance();
        EdifCell topCell = topCellInstance.getCellType();
        InstanceHierarchy hier = new InstanceHierarchy(topCellInstance);
        pullupTopIOPorts(hier);
        pullupFloatingOutputs(hier);

        //	        if (findAsynchronousLoops(hier))
        //	        	System.exit(-1);

        if (writeEdifFileName != null) {
            try {
                System.err.println("Writing completed EDIF file to " + writeEdifFileName);
                edif_file.toEdif(new EdifPrintWriter(writeEdifFileName));
            } catch (IOException e) {
                System.err.println("Error writing completed EDIF file");
            }
        }
        if (writeWrapper) {
            System.err.println("Writing wrapper to " + newFileName);

            String writeWrapperTopEdifFileName;
            if (writeEdifFileName == null)
                writeWrapperTopEdifFileName = edifFileName;
            else {
                writeWrapperTopEdifFileName = writeEdifFileName;
                subFiles = null;
            }
            createJHDLdotJavaFile(newFileName, technologyName, topCell, writeWrapperTopEdifFileName, subFiles);
        }

        // 4. Open the file in cvt.
        if (!nocvt)
            openCircuitInCVT(technologyName, topCellInstance, addEdifCellInstanceAsProperty);
    }

    public static String oneUnderScorize(String name) {
        int underScores = 0;
        String newString = new String();
        for (int i = 0; i < name.length(); i++)
            if (name.charAt(i) == '_' && name.charAt(i - 1) != '_' || name.charAt(i) != '_')
                newString += name.charAt(i);
        return newString;
    }

    public static void openCircuitInCVT(String technologyName, EdifCellInstance topCellInstance) {
        openCircuitInCVT(technologyName, topCellInstance, false);
    }

    /**
     * Opens the given EdifCellInstance in a cvtFrame. First a JHDL cell will be
     * created, and then it will be passed to cvtFrame's constructor.
     * 
     * @param technologyName The technology of the circuit for library finding
     * purposes.
     * @param topCellInstance The cell instance that will be the root to the
     * root cell to the cvtFrame.
     */
    public static void openCircuitInCVT(String technologyName, EdifCellInstance topCellInstance,
            boolean addEdifCellInstanceAsProperty) {
        // Create the parent cell.
        //topCellInstance.modifyCellRef(EdifCell.flatten(topCellInstance.getCellType()));
        Cell parent = buildWrapper(new HWSystem(), topCellInstance, technologyName, addEdifCellInstanceAsProperty);
        //testNaming(parent);
        //testLoadCount(parent);
        //System.out.println(topCellInstance.getCellType().netCount());
        //testNetCount(topCellInstance);
        //testWireToAllWires(parent);
        //testWireXilinxName(parent);
        // View the cell in cvt.
        final JHDLHostWidgetInterface hwi = (new cvtFrame(parent)).getJHDLHostWidgetInterface();

        if (addEdifCellInstanceAsProperty) {
            JMenuBar menuBar = hwi.getJMenuBar();
            int menu_count = menuBar.getComponentCount();
            int first_separator = menu_count;
            for (int i = 1; i < menu_count - 1; i++)
                if (menuBar.getMenu(i) == null) {
                    first_separator = i;
                    break;
                }

            boolean last_item_is_help = false;
            JMenu last_menu = menuBar.getMenu(menu_count - 1);
            String last_name = last_menu.toString();
            if (last_name.equalsIgnoreCase("help") || last_name.equalsIgnoreCase("about"))
                last_item_is_help = true;
            int menu_pos = (first_separator < menu_count) ? first_separator : (last_item_is_help ? menu_count - 1
                    : menu_count);

            JMenu menu = new JMenu(MENU_EDIF);
            menu.setMnemonic(KeyEvent.VK_D);

            edifMenu = new JMenuItem(BACK_TO_EDIF);
            edifMenu.setMnemonic(KeyEvent.VK_G);
            edifMenu.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String cmd = e.getActionCommand();
                    if (cmd.equals(BACK_TO_EDIF)) { // sort
                        // ascending
                        Cell topCell = hwi.getRootCell();
                        EdifCellInstance topInstance = (EdifCellInstance) topCell
                                .getPropertyValue(Edi2JHDL.EDIF_CELL_INSTANCE_PROPERTY);
                        // Now we need to close the cvtFrame, and
                        // open up some kind of EDIF frame, or
                        // EDIF command prompt.
                        System.out.println("COMMAND NOT YET IMPLEMENTED!!! (when ready, "
                                + "implement this method in BuildWrapper." + "actionPerformed)");
                    }
                }
            });
            menu.add(edifMenu);

            menuBar.add(menu, menu_pos);
            hwi.refreshGUI();
        }
    }

    public static void pullupFloatingOutputs(InstanceHierarchy hier) {
        EdifCell topCell = hier.getInstanceCellType();
        String[] pullupTypes = { "BUFT", "BUFE" };
        List pullupList = Arrays.asList(pullupTypes);

        InstanceHierarchy[] children = hier.getChildren();
        if (children != null)
            for (int i = 0; i < children.length; i++) {
                pullupFloatingOutputs(children[i]);
            }

        //System.out.println("TOP CELL: "+topCell);
        for (Iterator netIt = topCell.netListIterator(); netIt.hasNext();) {
            EdifNet net = (EdifNet) netIt.next();

            //EdifPortRef[] portRefs = (EdifPortRef[])net.getConnectedPortRefs().toArray();
            //for (int i = 0; i < portRefs.length; i++) {
            for (Iterator portRefIt = net.getConnectedPortRefs().iterator(); portRefIt.hasNext();) {
                EdifPortRef portRef = (EdifPortRef) portRefIt.next();
                EdifCellInstance cellInst = portRef.getCellInstance();
                EdifCell cell = XilinxGenLib.PULLUP();
                if (cellInst != null)
                    cell = cellInst.getCellType();
                EdifPort port = portRef.getPort();
                String debugString = "inner_bdo_7_m1_2";

                // if (cellInst != null && cellInst.getName().equalsIgnoreCase(debugString)) {
                //     System.out.println("HIT: "+portRef);
                //     System.out.println("\tDIR: "+port.getDirectionString());
                //     System.out.println("\tCELL: "+cell.getName()); 
                // }

                if (pullupList.contains(cell.getName().toUpperCase())
                        && (port.getDirection() == EdifPort.OUT || port.getDirection() == EdifPort.INOUT)) {
                    pullupPort(topCell, net);
                    //System.out.println("PULLUP: "+net);
                    //System.out.println("\tDIR: "+port.getDirectionString());
                    //System.out.println("\tCELL: "+cell.toString().trim());
                    //System.out.println("\tCELL INST: "+cellInst);
                    //System.out.println("\tPORTREF: "+portRef);
                    //System.out.println("\tDRIVERS: "+net.getNetDriven());
                    //System.out.println("\tDRIVEN: "+net.getNetDriven());
                    break;
                }
            }
        }
    }

    public static void pullupPort(EdifCell parent, EdifNet net) {
        EdifLibrary lib = parent.getLibrary();
        EdifLibraryManager elm = lib.getLibraryManager();
        lib = (EdifLibrary) elm.getLibraries().iterator().next();

        elm = lib.getLibraryManager();
        String pullupName = "PULLUP";
        EdifCell type = XilinxLibrary.findOrAddXilinxPrimitive(elm, pullupName);
        //EdifCell type = XilinxGenLib.PULLUP();
        String portName = "O";
        EdifCellInstance pullupeci = null;
        try {
            pullupeci = new EdifCellInstance(pullupName + (_pullupcnt++), parent, type);

            parent.addSubCell(pullupeci);
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        }

        EdifPortRef pullupPortRefSrc = new EdifPortRef(net, pullupeci.getCellType().getPort(portName).getSingleBitPort(
                0), pullupeci);
        net.addPortConnection(pullupPortRefSrc);
    }

    public static void pullupTopIOPorts(InstanceHierarchy heir) {
        EdifCell topCell = heir.getInstanceCellType();

        for (Iterator portIt = topCell.getPortList().iterator(); portIt.hasNext();) {
            EdifPort port = (EdifPort) portIt.next();

            if (port.getDirection() == EdifPort.INOUT) {

                EdifNet[] nets = null;

                EdifCell parentCell = port.getEdifCell();

                if (parentCell == null || parentCell.isLeafCell())
                    nets = null;

                else {
                    nets = new EdifNet[port.getWidth()];

                    // if any of these nets are still null after this method call
                    // then there are some ports without ports connected to them.
                    for (int i = 0; i < nets.length; i++)
                        nets[i] = null;

                    // Iterate each EdifNet inside the EdifCell
                    if (!parentCell.isLeafCell()) {

                        Iterator itNet = parentCell.getNetList().iterator();
                        while (itNet.hasNext()) {
                            EdifNet net = (EdifNet) itNet.next();

                            // Iterate each EdifPortRef of the EdifNet
                            Iterator itPortRef = net.getConnectedPortRefs().iterator();
                            while (itPortRef.hasNext()) {
                                EdifPortRef portRef = (EdifPortRef) itPortRef.next();

                                // Check the connection
                                if (portRef.getCellInstance() != null)
                                    continue;

                                if (port.getWidth() == 1) {
                                    if (portRef.getPort() == port) {
                                        nets[0] = net;
                                        return;
                                    }
                                } else {
                                    if (portRef.getPort() == port) {
                                        nets[portRef.getBusMember()] = net;
                                    }
                                }
                            }
                        }
                    }
                }

                for (int i = 0; i < nets.length; i++) {

                    //					if (nets[i].getOutputPortRefs().size() == 1) {
                    if ((findTrueInputPortRefs(nets[i]).size() > 0)
                            || (findTrueInputPortRefs(nets[i]).size() == 0 && findTrueOutputPortRefs(nets[i]).size() == 0)) {
                        pullupPort(topCell, nets[i]);
                        //System.out.println("PULLUP TOP: "+nets[i]);
                        //System.out.println("\tINPUTS: "+nets[i].getInputPortRefs());
                        //System.out.println("\tOUTPUTS: "+nets[i].getOutputPortRefs());
                        //System.out.println("\tTRUE INPUTS: "+findTrueInputPortRefs(nets[i]));
                    } else {
                        //System.out.println("NOT PULLUP TOP: "+nets[i]);
                        //System.out.println("\tINPUTS: "+nets[i].getInputPortRefs());
                        //System.out.println("\tOUTPUTS: "+nets[i].getOutputPortRefs());	
                        //System.out.println("\tTRUE INPUTS: "+findTrueInputPortRefs(nets[i]));
                    }
                }
            }
        }
    }

    /**
     * Parses the arguments passed to the wrapper.
     * 
     * @param args The passed arguments.
     */
    private static void parseArgs(String[] args) {
    }

    /**
     * Prints a help message, describing the options to pass to this class.
     */
    private static void printHelp() {
        System.out.println("Options:");
        System.out.println("-wedif filename\t\tWrites the merged/completed edif file to the specified filename.");
        System.out.println("-w:\t\t\tWrites the *_wrapper.java file.");
        System.out.println("-nocvt:\t\t\tSpecifies not to display the circuit in cvt.");
        System.out.println("-b2EDIF:\t\t\tSpecifies to have the ability to go back to EDIF once in JHDL.");
        System.out.println("-t technology:\t\tSpecifies the technology.");
        System.out.println("-nomerge:\t\tDisables merging.");
        System.out.println("(-f fileName)+:\t\tThe edif file(s).");
        System.out.println("(-L directory)*:\tSearch directory(s) for " + "finding black boxes.");
    }

    /**
     * Prints out information on how to pass the options to this class.
     */
    private static void printUsage() {
        System.out.println("Use --help flag for a description of options.");
        System.out.println("Usage:");
        System.out.println("java BuildWrapper <top edif filename> [-wedif filename] [-w] "
                + "[-nocvt] [-b2EDIF] -t Technology" + " [-nomerge] (-f fileName)+ (-L directory)*");
    }

    //	    private static void testNetCount(EdifCellInstance top){
    //	        System.out.println(top.getCellType().getLibrary().getLibraryManager().netCount());
    //	        System.out.println(top.netCountRecursive());
    //	//        EdifCell flat = EdifCell.flatten(top.getCellType());
    //	        EdifCell flat = FlattenEdifCell.flatten_mjw(top.getCellType());
    //	        System.out.println(flat.netCount());
    //	        System.exit(1);
    //	    }

    private static void testLoadCount(Cell top) {
        NodeList nl = top.getChildren();
        Cell c = (Cell) top.getSystem().findNamed("Edif_wrapper/conf/i_flashinterface/i_writefifo/g_120");
        Wire w = (Wire) top.getSystem().findNamed("Edif_wrapper/conf/i_flashinterface/i_writefifo/g_120/o");
        if (w != null) {
            System.err.println("Matched wire OK");
            System.err.println(w.getLeafName());
            Cell cell;
            int count = 0;
            CellList cl = w.getNetlistLeafSinkCellsNonUnique();
            for (cl.init(), cell = cl.getCell(); !cl.atEnd(); cl.next(), cell = cl.getCell()) {
                System.out.println(cell.getFullName());
                count++;
            }
            System.err.println(count);
        }
        System.out.println("EXITING....");
        System.exit(1);
    }

    private static void testNaming(Cell test) {
        // Test Name
        String name = "i_SelectMap/i_SelectMapController/write_count_cry_5/O";
        //String name = "i_RegBusTester/i_source1/G_63_0";
        //String name = "i_FlashInterface/N_1955";
        //String name = "i_SelectMap/i_SelectMapController/N_114_i_0";
        //String name = "i_FlashInterface/count_4_cry_1/O";
        //String name = "i_Slaac1VBus/we(61)";
        //String name = "result/FPMantissaDivide/embedded_multiplier__4/multiplier_block_a1_b0_out<15>";
        //String name = "FPMult/Round/sum_out<14>";
        String wireName = name.substring(name.lastIndexOf('/') + 1).toLowerCase();
        String hierarchichalCellName = oneUnderScorize(name.substring(0, name.lastIndexOf('/')).toLowerCase());
        // Name to search for in the HWSystem
        String testName = test.getFullName() + "/conf/" + hierarchichalCellName;
        Cell hcn = (Cell) test.getSystem().findNamed(testName);
        if (hcn == null) {// Didn't find a cell in the HWSystem
            System.err.println(testName + " did not match anything");
            return;
        }
        // Retreive the corresponding EdifCellInstance
        EdifCellInstance eci = (EdifCellInstance) hcn.getPropertyValue(Edi2JHDL.EDIF_CELL_INSTANCE_PROPERTY);
        if (eci == null) {// Didn't find a corresponding EdifCellInstance
            System.err.println("Could not retrieve " + Edi2JHDL.EDIF_CELL_INSTANCE_PROPERTY + " property from cell");
            System.out.println(hcn);
            return;
        }
        // Iterate through the nets to find a match
        System.out.println("test: " + testName);
        System.out.println("hier: " + hierarchichalCellName);
        System.out.println("wire: " + wireName);
        for (Iterator i = eci.getCellType().netListIterator(); i.hasNext();) {
            EdifNet net = (EdifNet) i.next();
            String nameToCompare;
            nameToCompare = net.getOldName().toLowerCase();

            //	        if(net.getEdifNameable() instanceof RenamedObject)
            //	            nameToCompare = ((RenamedObject)net.getEdifNameable()).getOldName().toLowerCase();
            //	        else
            //	            nameToCompare = net.getName().toLowerCase();

            if (nameToCompare.equalsIgnoreCase(wireName)) {
                // Match found!
                String testWireName = testName + '/' + oneUnderScorize(net.getName().toLowerCase());
                Wire wire = (Wire) test.getSystem().findNamed(testWireName);
                if (wire == null) { // Couldn't find a wire in the HWSystem
                    System.err.println("Couldn't find a corresponding Wire for " + testWireName);
                    return;
                }
                System.out.println("MATCH: " + wire);
                System.exit(1);
            }
            System.out.println(nameToCompare);
        }
        Wire wire = (Wire) test.getSystem().findNamed(testName + '/' + oneUnderScorize(wireName.toLowerCase()));
        if (wire == null) {
            System.err
                    .println("2nd: Couldn't find a corresponding Wire for " + oneUnderScorize(wireName.toLowerCase()));
            return;
        }
        System.out.println("MATCH2: " + wire);
    }

    //	    private static void testNetCount(EdifCellInstance top){
    //	        System.out.println(top.getCellType().getLibrary().getLibraryManager().netCount());
    //	        System.out.println(top.netCountRecursive());
    //	//        EdifCell flat = EdifCell.flatten(top.getCellType());
    //	        EdifCell flat = FlattenEdifCell.flatten_mjw(top.getCellType());
    //	        System.out.println(flat.netCount());
    //	        System.exit(1);
    //	    }

    private static void testWireToAllWires(Cell top) {
        HashMap map = HelperLibrary.createVPtoWiresMapping(top);

        NodeList nl = top.getChildren();
        Cell c;
        Wire w;
        for (nl.init(), c = nl.getCell(); !nl.atEnd(); nl.next(), c = nl.getCell()) {
            if (c.getLeafName().equalsIgnoreCase("conf")) {
                System.err.println("Matched Cell OK");
                System.err.println(c.getLeafName());
                WireList wl = c.getWires();
                for (wl.init(), w = wl.getWire(); !wl.atEnd(); wl.next(), w = wl.getWire()) {
                    if (w.getLeafName().equalsIgnoreCase("bdo_bus_c_17")) {
                        System.err.println("Matched wire OK");
                        System.err.println(w.getLeafName());
                        int count = 0;
                        CellList cl = w.getNetlistLeafSinkCellsNonUnique();
                        Cell cell;
                        for (cl.init(), cell = cl.getCell(); !cl.atEnd(); cl.next(), cell = cl.getCell())
                            count++;

                        ArrayList al;
                        int count2 = 0;
                        al = HelperLibrary.getWiresAttachedToVP(w, map);
                        for (int j = 0; j < al.size(); j++) {
                            System.out.println((Wire) al.get(j));
                            count2++;
                        }
                        System.err.println(count);
                        System.err.println(count2);
                        break;
                    }
                }
                break;
            }
        }
        System.out.println(map.size());
        System.out.println("EXITING....");
        System.exit(1);
    }

    private static void testWireXilinxName(Cell top) {
        String xilinxName;
        WireList wl = top.getWires();
        NodeList nl = top.getChildren();
        Cell c;
        Wire w;
        //do root
        for (wl.init(), w = wl.getWire(); !wl.atEnd(); wl.next(), w = wl.getWire()) {
            if (w.getWidth() > 1) {
                Wire sw;
                for (int i = 0; i < w.getWidth(); i++) {
                    sw = w.gw(i);
                    xilinxName = "NO WIRE FOUND";
                    EdifNet net = (EdifNet) HelperLibrary.getPropertyFromTopWire(sw, Edi2JHDL.EDIF_NET_PROPERTY);
                    if (net != null)
                        xilinxName = net.getOldName();
                    //System.out.println("Wire: "+sw.getCanonicalName()+"; Xilinx Name: " + xilinxName);
                }
            } else {
                xilinxName = "NO WIRE FOUND";
                EdifNet net = (EdifNet) HelperLibrary.getPropertyFromTopWire(w, Edi2JHDL.EDIF_NET_PROPERTY);
                if (net != null)
                    xilinxName = net.getOldName();
                //System.out.println("Wire: "+w.getCanonicalName()+"; Xilinx Name: " + xilinxName);
            }
        }
        //do children of root
        for (nl.init(), c = nl.getCell(); !nl.atEnd(); nl.next(), c = nl.getCell()) {
            wl = c.getWires();
            for (wl.init(), w = wl.getWire(); !wl.atEnd(); wl.next(), w = wl.getWire()) {
                if (w.getWidth() > 1) {
                    Wire sw;
                    for (int i = 0; i < w.getWidth(); i++) {
                        sw = w.gw(i);
                        xilinxName = "NO WIRE FOUND";
                        EdifNet net = (EdifNet) HelperLibrary.getPropertyFromTopWire(sw, Edi2JHDL.EDIF_NET_PROPERTY);
                        if (net != null)
                            xilinxName = net.getOldName();
                        //System.out.println("Wire: "+sw.getCanonicalName()+"; Xilinx Name: " + xilinxName);
                    }
                } else {
                    xilinxName = "NO WIRE FOUND";
                    EdifNet net = (EdifNet) HelperLibrary.getPropertyFromTopWire(w, Edi2JHDL.EDIF_NET_PROPERTY);
                    if (net != null)
                        xilinxName = net.getOldName();
                    //System.out.println("Wire: "+w.getCanonicalName()+"; Xilinx Name: " + xilinxName);

                }
            }
        }
        System.out.println("EXITING....");
        System.exit(1);
    }

    /**
     * Writes the wrapper's CellInterface.
     * 
     * @param fp The PrintWriter Object used to print the wrapper
     * @param topcell The Top cell of the design for the wrapper file
     */
    private static void writeCellInterface(PrintWriter fp, EdifCell topcell) {
        fp.println("\tpublic static CellInterface[] cell_interface = {");

        Iterator it = topcell.getSortedPortList().iterator();
        while (it.hasNext()) {
            EdifPort port = (EdifPort) it.next();
            String dir;
            switch (port.getDirection()) {
            case EdifPort.IN:
                dir = "in";
                break;
            case EdifPort.OUT:
                dir = "out";
                break;
            case EdifPort.INOUT:
                dir = "inout";
                break;
            default:
                dir = "UNKNOWN";
                break;
            }
            fp.println("\t\t" + dir + "(\"" + JHDL_ID(port.getName()) + "\", " + port.getWidth() + "),");
        }
        fp.println("\t};");
        fp.println("");
    }

    /**
     * Writes the wrapper's body.
     * 
     * @param fp The PrintWriter Object used to print the wrapper
     * @param topcell The Top cell of the design for the wrapper
     * @param technologyName The technology this wrapper will use
     * @param className The name of the class file
     * @param subFiles The sub files, if any, needed by the top EdifFile
     */
    private static void writeClassGuts(PrintWriter fp, EdifCell topcell, String technologyName, String className,
            String topFileName, Collection subFiles) {
        fp.println("\t\tbyucc.edif.EdifEnvironment edif_file = null;");

        fp.println("\t\tString topFileName = \"" + topFileName + "\";");
        fp.print("\t\tCollection subFiles = ");
        if (subFiles != null && subFiles.size() != 0) {
            fp.println("new ArrayList();");
            for (Iterator i = subFiles.iterator(); i.hasNext();) {
                String filename = (String) i.next();
                fp.println("\t\tsubFiles.add(\"" + filename + "\");");
            }
        } else
            fp.println("null;");

        fp.println("\t\tEdifLibrary xilinxLib = edu.byu.ece.edif.arch.xilinx.XilinxLibrary.library;");
        fp.println("\t\ttry {");
        fp
                .println("\t\t\tedif_file = EdifMergeParser.parseAndMergeEdif(topFileName, null, subFiles, xilinxLib, System.out);");
        fp.println("\t\t} catch (Exception e) {");
        fp.println("\t\t\te.printStackTrace();");
        fp.println("\t\t\tSystem.exit(-1);");
        fp.println("\t\t}");

        fp.println("\t\tbyucc.edif.EdifDesign edif_design = edif_file.getTopDesign();");
        fp.println("\t\tbyucc.edif.EdifCellInstance topCellInstance = " + "edif_design.getTopCellInstance();");
        fp.println("\t\tEdi2JHDL jhdl_gen = new Edi2JHDL(" + "\"" + technologyName + "\");");
        fp.println("\t\tCell cel = jhdl_gen.toJHDLCircuit(this, topCellInstance, portWires);");
        fp.println("\t} //End constructor");
        fp.println("} //End " + className + " class");

    }

    /**
     * Writes the wrapper's class header.
     * 
     * @param fp The PrintWriter Object used to print the wrapper
     * @param technology The technology used by the wrapper
     * @param className The name of the class
     */
    private static void writeClassHeader(PrintWriter fp, String technology, String className) {
        fp.println("/* Generated by edu.byu.ece.edif.util.export.jhdl.BuildWrapper.createJHDLdotJavaFile */");
        fp.println();
        fp.println();
        fp.println();
        fp.println("import byucc.edif.*;");
        fp.println("import edu.byu.ece.edif.util.export.jhdl.*;");
        fp.println("import edu.byu.ece.edif.util.parse.*;");
        fp.println("import edu.byu.ece.edif.util.merge.*;");
        fp.println("import java.io.*;");
        fp.println("import java.util.*;");
        fp.println("import byucc.jhdl.base.*;");
        fp.println("import byucc.jhdl.Logic.*;");
        fp.println("import byucc.jhdl.Xilinx.*;");
        fp.println("import byucc.jhdl.Xilinx." + technology + ".*;");
        fp.println("");
        fp.println("public class " + className + " extends Logic {");
        fp.println("");
    }

    /**
     * Writes the wrapper's Constructor.
     * 
     * @param fp The PrintWriter Object used to print the wrapper
     * @param topcell The Top cell of the design for the wrapper
     * @param className The name of the class file
     */
    private static void writeConstructor(PrintWriter fp, EdifCell topcell, String className) {

        fp.print("\tpublic " + className + "(Node parent");

        Iterator it = topcell.getSortedPortList().iterator();
        while (it.hasNext()) {
            EdifPort port = (EdifPort) it.next();
            fp.println(",");
            fp.print("\t\t\t\tWire " + JHDL_ID(port.getName()));
            if (!it.hasNext()) {
                fp.println("){");
            }
        }
        fp.println();
        fp.println("\t\tsuper(parent);");
        fp.println();

        it = topcell.getSortedPortList().iterator();
        while (it.hasNext()) {
            EdifPort port = (EdifPort) it.next();
            fp.println("\t\tconnect(\"" + JHDL_ID(port.getName()) + "\", " + JHDL_ID(port.getName()) + ");");
        }
        fp.println();
    }

    /**
     * Writes the wrapper's port wires.
     * 
     * @param fp The PrintWriter Object used to print the wrapper
     * @param topcell The Top cell of the design for the wrapper
     */
    private static void writePortWires(PrintWriter fp, EdifCell topcell) {
        fp.println("\t\tWire[] portWires = {");
        Iterator it = topcell.getSortedPortList().iterator();

        int no = 0;
        while (it.hasNext()) {
            EdifPort port = (EdifPort) it.next();
            no++;
            if (no == 1)
                fp.print("\t\t\t " + JHDL_ID(port.getName()));
            else
                fp.print(",\n\t\t\t " + JHDL_ID(port.getName()));
        }
        fp.println("\n\t\t};");

    }

    public static final String BACK_TO_EDIF = "Go back to EDIF";

    /** Extension to add onto the created .java file for the wrapper. * */
    public static final String CLASSNAME_EXTENSION = "_wrapper";

    public static JMenuItem edifMenu = null;

    public static final String MENU_EDIF = "EDIF";

    /** The minimum argument number for this class. * */
    private static final int MINARGNUMBER = 3;

    private static int _gndcnt;

    private static int _pullupcnt;

    private static int _netcnt;
}
