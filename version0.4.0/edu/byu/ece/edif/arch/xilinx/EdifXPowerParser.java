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
package edu.byu.ece.edif.arch.xilinx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author nhr2
 * @since Created on Jun 14, 2005
 */
public class EdifXPowerParser {

    public EdifXPowerParser(String fileName, String section) {

        _section = xpowerSections[1];
        _numLineElements = xpowerLineElements[1];
        for (int i = 0; i < xpowerSections.length; i++) {
            if (section.equalsIgnoreCase(xpowerSections[i])) {
                _section = xpowerSections[i];
                _numLineElements = xpowerLineElements[i];
                break;
            }
        }
        _xpowerNames = (ArrayList) parseXPowerFileSection(fileName, _section, _numLineElements);
    }

    public EdifXPowerParser(String fileName) {

        int length = xpowerSections.length;
        _names = new ArrayList<ArrayList<String>>(length);
        _capacitances = new ArrayList<ArrayList<Double>>(length);
        _frequencies = new ArrayList<ArrayList<Double>>(length);
        _currents = new ArrayList<ArrayList<Double>>(length);
        _powers = new ArrayList<ArrayList<Double>>(length);

        //String newFile = "xpowerOut.dat";
        //PrintWriter fp = Edif2XilinxName.openWriteFile(newFile);

        for (int section = 0; section < xpowerSections.length; section++) {
            _parseXPowerFileSectionFull(fileName, section);
            //_printXPowerSection(fp, section);
        }

        //fp.close();
    }

    public ArrayList<ArrayList<String>> getNames() {
        return _names;
    }

    public ArrayList<ArrayList<Double>> getCapactiances() {
        return _capacitances;
    }

    public ArrayList<ArrayList<Double>> getFrequencies() {
        return _frequencies;
    }

    public ArrayList<ArrayList<Double>> getCurrents() {
        return _currents;
    }

    public ArrayList<ArrayList<Double>> getPowers() {
        return _powers;
    }

    public ArrayList getXPowerNames() {
        return _xpowerNames;
    }

    public static void main(String[] argv) {

        new EdifXPowerParser(argv[0]);
    }

    /**
     * This method will open the given "power" file and parse the contents to
     * create a List of String objects where each String is a Net name or
     * instance name in the file.
     * 
     * @param fileName
     * @return List of names
     */
    public static List parseXPowerFile(String fileName) {

        List xilinxNames = new ArrayList();

        for (int i = 0; i < xpowerSections.length; i++) {
            ArrayList section = (ArrayList) parseXPowerFileSection(fileName, xpowerSections[i], xpowerLineElements[i]);
            xilinxNames.addAll(section);
        }

        return xilinxNames;
    }

    public static void printXPowerFile(String xpowerFile) {
        String fileName = "xpowerNames.txt";
        PrintWriter fp = Edif2XilinxName.openWriteFile(fileName);
        ArrayList xpNames = (ArrayList) parseXPowerFile(xpowerFile);

        fp.println("XPower Names:\n");
        for (Iterator it = xpNames.iterator(); it.hasNext();) {
            String xpowerName = (String) it.next();
            fp.println(xpowerName);
        }
        fp.close();
    }

    public static void printXPowerFileSection(String xpowerFile, String section, int elements) {

        String fileName = "xpower" + section + "Names.txt";
        PrintWriter fp = Edif2XilinxName.openWriteFile(fileName);
        ArrayList xpNames = (ArrayList) parseXPowerFileSection(xpowerFile, section, elements);

        fp.println("XPower Names For Section " + section + ":\n");
        for (Iterator it = xpNames.iterator(); it.hasNext();) {
            String xpowerName = (String) it.next();
            fp.println(xpowerName);
        }
        fp.close();
    }

    /**
     * This method prints the signal names parsed from the XPower file
     */
    public void printXPowerNames() {

        String fileName = "xpower" + _section + "Names.txt";
        PrintWriter fp = Edif2XilinxName.openWriteFile(fileName);

        fp.println("XPower Names For Section " + _section + ":\n");
        for (Iterator it = _xpowerNames.iterator(); it.hasNext();) {
            String xpowerName = (String) it.next();
            fp.println(xpowerName);
        }
        fp.close();
    }

    /**
     * This is a helper method to help parse the different sections of the
     * XPower file.
     * 
     * @param fileName
     * @param section
     * @param numLineElements
     * @return
     */
    public static List parseXPowerFileSection(String fileName, String section, int numLineElements) {
        ArrayList sectionNames = new ArrayList();
        BufferedReader file = null;
        String[] ignoreLines = { "Vcco33", "$BEL", "PhysOnlyGnd" };
        String finishLine = "---------------------";
        String line = "";
        boolean getName = true;
        boolean parse = false;

        try {
            file = new BufferedReader(new FileReader(new File(fileName)));
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            return null;
        }

        try {

            // begin reading lines from the file
            while ((line = file.readLine()) != null) {
                getName = true;

                // signal to stop parsing Xilinx names
                if (line.indexOf(finishLine) > -1) {
                    if (parse == true)
                        break;
                }

                // make sure we don't parse lines we want to ignore
                for (int i = 0; i < ignoreLines.length; i++)
                    if (line.startsWith(ignoreLines[i])) {
                        getName = false;
                        break;
                    }

                // see if we've found what we want to parse yet
                if (parse && getName) {
                    boolean gotName = false;
                    StringBuffer sb = new StringBuffer();

                    // sometimes xpower results span more than one line
                    // make sure we read one result completely
                    while (!gotName) {
                        String[] words = (line.trim()).split("\\s+");
                        gotName = (words.length == 1) ? false : true;
                        if (words.length == numLineElements) {
                            sb.append(words[0]);
                        } else if (words.length == numLineElements - 1) {
                            try {
                                int val = Integer.parseInt(words[0]);
                            } catch (NumberFormatException e) {
                                sb.append(words[0]);
                                line = file.readLine();
                            }
                        } else {
                            sb.append(words[0]);
                            line = file.readLine();
                        }
                    }
                    sectionNames.add(sb.toString());
                }

                // signal to begin parsing xpower names
                if (line.startsWith(section)) {
                    parse = true;
                    line = file.readLine();
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        try {
            file.close();
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        return sectionNames;
    }

    /**
     * This is a helper method to help parse the different sections of the
     * XPower file.
     * 
     * @param fileName
     * @param section
     * @param numLineElements
     * @return
     */
    protected List _parseXPowerFileSectionFull(String fileName, int sectionIdx) {
        ArrayList sectionNames = new ArrayList();
        BufferedReader file = null;
        String[] ignoreLines = { "Vcco33", "$BEL", "PhysOnlyGnd" };
        String finishLine = "---------------------";
        String line = "";
        boolean getName = true;
        boolean parse = false;
        String section = xpowerSections[sectionIdx];
        int numLineElements = xpowerLineElements[sectionIdx];
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<Double> capacitances = new ArrayList<Double>();
        ArrayList<Double> frequencies = new ArrayList<Double>();
        ArrayList<Double> currents = new ArrayList<Double>();
        ArrayList<Double> powers = new ArrayList<Double>();
        int idx = (numLineElements == 6) ? 2 : 1;

        try {
            file = new BufferedReader(new FileReader(new File(fileName)));
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            return null;
        }

        try {

            // begin reading lines from the file
            while ((line = file.readLine()) != null) {
                getName = true;

                // signal to stop parsing Xilinx names
                if (line.indexOf(finishLine) > -1) {
                    if (parse == true)
                        break;
                }

                // make sure we don't parse lines we want to ignore
                for (int i = 0; i < ignoreLines.length; i++)
                    if (line.startsWith(ignoreLines[i])) {
                        getName = false;
                        break;
                    }

                // see if we've found what we want to parse yet
                if (parse && getName) {
                    boolean gotName = false;
                    StringBuffer sb = new StringBuffer();

                    // sometimes xpower results span more than one line
                    // make sure we read one result completely
                    while (!gotName) {
                        String[] words = (line.trim()).split("\\s+");
                        gotName = (words.length == 1) ? false : true;
                        if (words.length == numLineElements) {
                            sb.append(words[0]);
                        } else if (words.length == numLineElements - 1) {
                            try {
                                Integer.parseInt(words[0]);
                                idx--;
                            } catch (NumberFormatException e) {
                                sb.append(words[0]);
                                line = file.readLine();
                            }
                        } else {
                            sb.append(words[0]);
                            line = file.readLine();
                        }
                    }
                    sectionNames.add(sb.toString());
                    names.add(sb.toString());
                    String[] words = (line.trim()).split("\\s+");
                    capacitances.add(Double.parseDouble(words[idx]));
                    frequencies.add(Double.parseDouble(words[idx + 1]));
                    currents.add(Double.parseDouble(words[idx + 2]));
                    powers.add(Double.parseDouble(words[idx + 3]));

                }

                // signal to begin parsing xpower names
                if (line.startsWith(section)) {
                    parse = true;
                    line = file.readLine();
                }
            }

            _names.add(sectionIdx, names);
            _capacitances.add(sectionIdx, capacitances);
            _frequencies.add(sectionIdx, frequencies);
            _currents.add(sectionIdx, currents);
            _powers.add(sectionIdx, powers);

        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        try {
            file.close();
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        return sectionNames;
    }

    protected void _printXPowerSection(PrintWriter pw, int section) {
        pw.append("----------------------------------\n");
        pw.append("SECTION: " + xpowerSections[section] + "\n");
        pw.append("----------------------------------\n");

        ArrayList<String> names = _names.get(section);
        ArrayList<Double> capacitances = _capacitances.get(section);
        ArrayList<Double> frequencies = _frequencies.get(section);
        ArrayList<Double> currents = _currents.get(section);
        ArrayList<Double> powers = _powers.get(section);

        for (int i = 0; i < names.size(); i++) {
            pw.append(names.get(i) + "\t\t" + capacitances.get(i) + "\t" + frequencies.get(i));
            pw.append("\t" + currents.get(i) + "\t" + powers.get(i) + "\n");
        }

    }

    public static int[] xpowerLineElements = { 6, 6, 5, 5, 6 };

    /**
     * This is the list of possible XPower file sections
     */
    public static String[] xpowerSections = { "Outputs", "Signals", "Logic", "Inputs", "IOs" };

    /**
     * This is the number of items in an entry of the given file section
     */
    private int _numLineElements;

    /**
     * This string represents the XPower file section to parse
     */
    private String _section;

    /**
     * This List contains the XPower names for a given file section
     */
    private ArrayList _xpowerNames;

    private ArrayList<ArrayList<String>> _names;

    private ArrayList<ArrayList<Double>> _capacitances;

    private ArrayList<ArrayList<Double>> _frequencies;

    private ArrayList<ArrayList<Double>> _currents;

    private ArrayList<ArrayList<Double>> _powers;
}
