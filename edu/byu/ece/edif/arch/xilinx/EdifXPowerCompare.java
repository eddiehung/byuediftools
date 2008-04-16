/*
 * Created on Jun 14, 2005
 * 
 */
/*
 * 
 * 
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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * @author nhr2
 */
public class EdifXPowerCompare {

    /**
     * 
     */
    public EdifXPowerCompare(String EDIFFile, String xpowerFile) {
        super();
        _edifFound = new LinkedHashMap();
        _edifNotFound = new LinkedHashMap();

        checkNameMatch(EDIFFile, xpowerFile);
    }

    /**
     * For each XPower file section listed in the sections array list the
     * signals that: 1) appear in the XPower list but not the EDIF list 2)
     * appear in the EDIF list but not the XPower list
     * 
     * @param EDIFFile the name of the EDIF file to parse
     * @param xpowerFile the name of the XPower file to parse
     * @param section an array of XPower file section names to parse and match
     */
    public void checkNameMatch(String EDIFFile, String xpowerFile) {
        String fileName1 = "xpowerMatchResults.txt";
        PrintWriter fp1 = Edif2XilinxName.openWriteFile(fileName1);
        String fileName2 = "EDIFMatchResults.txt";
        PrintWriter fp2 = Edif2XilinxName.openWriteFile(fileName2);
        String section = EdifXPowerParser.xpowerSections[1];
        EdifXPowerParser exp = new EdifXPowerParser(xpowerFile, section);
        Edif2XilinxName e2xon = new Edif2XilinxName(EDIFFile);
        int[] xpMatches = new int[2];
        int[] edifMatches = new int[2];

        fp1.println("XPower Signals with no corresponding EDIF name match:\n");
        fp2.println("EDIF Signals with no corresponding XPower name match:\n");

        xpMatches = _checkXPowerNameMatch(fp1, e2xon.getNameObjectMap(), exp.getXPowerNames());
        edifMatches = _checkEDIFNameMatch(fp2, e2xon.getNameObjectMap(), exp.getXPowerNames());
        fp1.println("\n" + section + " SECTION SUMMARY:");
        fp1.println("\tMATCHES: " + xpMatches[0] + "  MISMATCHES: " + xpMatches[1] + "\n");
        fp2.println("\n" + section + " SECTION SUMMARY:");
        fp2.println("\tMATCHES: " + edifMatches[0] + "  MISMATCHES: " + edifMatches[1] + "\n");

        fp1.close();
        fp2.close();
    }

    public static List getAllKeysWithSameValue(Map map, Object value) {
        List retval = new ArrayList();

        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
            Object obj = it.next();
            if (map.get(obj).equals(value)) {
                retval.add(obj);
            }
        }

        return retval;
    }

    public Map getFoundEdifNamesMap() {
        return _edifFound;
    }

    public Map getNotFoundEdifNamesMap() {
        return _edifNotFound;
    }

    protected int[] _checkEDIFNameMatch(PrintWriter fp, Map EDIFMap, List xpowerNames) {
        int[] retval = new int[2];
        int matches = 0;
        int mismatches = 0;
        boolean matchFound = false;
        Map missNamesMap = new LinkedHashMap();

        for (Iterator it = EDIFMap.keySet().iterator(); it.hasNext();) {
            String EDIFName = (String) it.next();

            matchFound = false;
            if (xpowerNames.contains(EDIFName)) {
                matches++;
                matchFound = true;
            }
            if (!matchFound) {
                if (!EDIFName.endsWith("_rt") && !EDIFName.endsWith("/O")) {
                    _edifNotFound.put(EDIFName, EDIFMap.get(EDIFName));
                    fp.println(EDIFName);
                    mismatches++;
                }
            } else {
                _edifFound.put(EDIFName, EDIFMap.get(EDIFName));
            }
        }
        retval[0] = matches;
        retval[1] = mismatches;

        return retval;
    }

    /**
     * This method ensures that all the signals parsed from the XPower file have
     * corresponding data structures built from the EDIF file.
     */
    protected int[] _checkXPowerNameMatch(PrintWriter fp, Map EDIFMap, List xpowerNames) {
        int[] retval = new int[2];
        int matches = 0;
        int mismatches = 0;

        for (Iterator it = xpowerNames.iterator(); it.hasNext();) {
            String xpName = (String) it.next();
            if (EDIFMap.keySet().contains(xpName))
                matches++;
            else {
                mismatches++;
                fp.println(xpName);
            }
        }
        retval[0] = matches;
        retval[1] = mismatches;

        return retval;
    }

    private Map _edifFound;

    private Map _edifNotFound;
}
