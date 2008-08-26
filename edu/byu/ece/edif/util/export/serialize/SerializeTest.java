package edu.byu.ece.edif.util.export.serialize;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifLibrary;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.jedif.EDIFMain;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.MergeParserCommandGroup;

/**
 * This class can be used to test the functionality of the custom serialization
 * implemented in NameReferenceObjectInputStream and NameReferenceObjectOutputStream.
 * 
 * An .edf file is needed for the test.
 * 
 * Run the test as follows:
 * java edu.byu.ece.edif.util.export.serialize.SerializeTest test.edf 
 */
public class SerializeTest extends EDIFMain {

    public static void main(String[] args) {
        printProgramExecutableString(System.out);
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new MergeParserCommandGroup());
        JSAPResult result = parser.parse(args, System.err);
        EdifEnvironment env = MergeParserCommandGroup.getEdifEnvironment(result);
        
        List<EdifCell> cells = new ArrayList<EdifCell>();
        List<EdifCellInstance> instances = new ArrayList<EdifCellInstance>();
        List<EdifPort> ports = new ArrayList<EdifPort>();
        List<EdifPortRef> portRefs = new ArrayList<EdifPortRef>();
        List<EdifNet> nets = new ArrayList<EdifNet>();
        List<EdifLibrary> libraries = new ArrayList<EdifLibrary>();
        List<EdifSingleBitPort> singleBitPorts = new ArrayList<EdifSingleBitPort>();

        //traverse environment, adding all elements to the lists
        System.out.println("Traversing EDIF structure, collecting objects...");
        for (EdifLibrary lib : env.getLibraryManager().getLibraries()) {
            libraries.add(lib);
            for (EdifCell cell : lib.getCells()) {
                cells.add(cell);
                for (EdifCellInstance instance : cell.getSubCellList()) {
                    instances.add(instance);
                }
                Collection<EdifNet> netList = cell.getNetList();
                if (netList != null)
                    for (EdifNet net : netList) {
                        nets.add(net);
                        Collection<EdifPortRef> eprList = net.getConnectedPortRefs();
                        if (eprList != null)
                            for (EdifPortRef epr : eprList) {
                                portRefs.add(epr);
                            }
                    }
                Collection<EdifPort> portList = cell.getPortList();
                if (portList != null)
                    for (EdifPort port : portList) {
                        ports.add(port);
                        for (EdifSingleBitPort esbp : port.getSingleBitPortList()) {
                            singleBitPorts.add(esbp);
                        }
                    }
            }
            break;
        }

        // serialize the lists
        System.out.println("Serializing objects...");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("serializeTest.dat");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        NameReferenceObjectOutputStream objectOutput = null;
        try {
            objectOutput = new NameReferenceObjectOutputStream(fos, env);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            objectOutput.writeObject(cells);
            objectOutput.writeObject(instances);
            objectOutput.writeObject(ports);
            objectOutput.writeObject(portRefs);
            objectOutput.writeObject(nets);
            objectOutput.writeObject(libraries);
            objectOutput.writeObject(singleBitPorts);
            objectOutput.flush();
            objectOutput.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // deserialize the lists
        System.out.println("Deserializing objects...");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("serializeTest.dat");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        NameReferenceObjectInputStream objectInput = null;
        try {
            objectInput = new NameReferenceObjectInputStream(fis, env);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<EdifCell> cells2 = null;
        List<EdifCellInstance> instances2 = null;
        List<EdifPort> ports2 = null;
        List<EdifPortRef> portRefs2 = null;
        List<EdifNet> nets2 = null;
        List<EdifLibrary> libraries2 = null;
        List<EdifSingleBitPort> singleBitPorts2 = null;
        try {
            cells2 = (List<EdifCell>) objectInput.readObject();
            instances2 = (List<EdifCellInstance>) objectInput.readObject();
            ports2 = (List<EdifPort>) objectInput.readObject();
            portRefs2 = (List<EdifPortRef>) objectInput.readObject();
            nets2 = (List<EdifNet>) objectInput.readObject();
            libraries2 = (List<EdifLibrary>) objectInput.readObject();
            singleBitPorts2 = (List<EdifSingleBitPort>) objectInput.readObject();
            objectInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // compare to original lists
        System.out.println("Comparing objects to originals...");
        boolean passed = true;
        passed = passed && compareLists(instances, instances2, "instances");
        passed = passed && compareLists(cells, cells2, "cells");
        passed = passed && compareLists(ports, ports2, "ports");
        passed = passed && compareLists(portRefs, portRefs2, "portRefs");
        passed = passed && compareLists(nets, nets2, "nets");
        passed = passed && compareLists(libraries, libraries2, "libraries");
        passed = passed && compareLists(singleBitPorts, singleBitPorts2, "singleBitPorts");
        System.out.println("\nSummary:");
        if (passed)
        	System.out.println("All tests passed");
        else
        	System.out.println("At least one test failed");
        	
    }
    
    public static boolean compareLists(List list1, List list2, String name) {
        boolean match = true;
        Iterator it1 = list1.iterator();
        Iterator it2 = list2.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            Object ob1 = it1.next();
            Object ob2 = it2.next();
            if (ob1 != ob2)
                match = false;
        }
        if (it1.hasNext() != it2.hasNext())
            match = false;
        if (match) {
            System.out.println(name + ": match");
            return true;
        }
        else {
            System.out.println(name + ": no match");
            return false;
        }
    }
}
