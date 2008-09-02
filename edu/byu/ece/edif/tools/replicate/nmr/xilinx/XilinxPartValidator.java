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
package edu.byu.ece.edif.tools.replicate.nmr.xilinx;

public class XilinxPartValidator {

    protected String archName;

    protected String[] devNames;

    protected String[][] packageNames;

    public XilinxPartValidator(String arch_Name, String[] dev_Names, String[][] package_Names) {
        this.archName = arch_Name;
        this.devNames = dev_Names;
        this.packageNames = package_Names;
    }

    public String validate(String part) throws IllegalArgumentException {
        //System.out.println("........................................................................................................................");

        //handle radhard parts by ignoring the rad-hard part.
        part = part.replace("QR", "C");

        String scrambled = part;

        scrambled = scrambled.toUpperCase();
        //System.out.println( "The following configuration will now be validated: " + scrambled );		
        String inarchitecture = "", indevice = "", inpackage = "", inspeed = "";

        int index = 0;
        int count = 0;
        char letter;
        int numdashes = 0;

        for (int k = 0; k < scrambled.length(); k++) {
            if (scrambled.charAt(k) == '-')
                numdashes++;
        }
        if (numdashes == 2)
            count = 4;
        else if (numdashes == 1)
            count = 3;
        else
            count = 2;

        for (int j = 0; j <= count; j++) {
            while (index < scrambled.length()) {
                //System.out.println("Checking the letter for charAt("+index+") = "+scrambled.charAt(index));
                letter = scrambled.charAt(index);
                switch (j) {//which iteration of the parse we're on
                case 0:
                    if (letter == 'X') {
                        inarchitecture = assembler(scrambled.substring(index, scrambled.length()));
                        index += inarchitecture.length();
                    } else if (Character.isDigit(letter)
                            || ((letter == 'F' || letter == 'S' || letter == 'L') && scrambled.charAt(index + 1) == 'X')) {
                        indevice = assembler(scrambled.substring(index, scrambled.length()));
                        index += indevice.length();
                    } else if (letter == '-' && Character.isDigit(scrambled.charAt(index + 1))) {
                        inspeed = assembler(scrambled.substring(index, scrambled.length()));
                        index += inspeed.length();
                    } else {
                        inpackage = assembler(scrambled.substring(index, scrambled.length()));
                        if (numdashes > 0)
                            index += inpackage.length() + (numdashes - 1);
                        else
                            index += inpackage.length();
                    }
                    //System.out.println("Architecture="+inarchitecture+"\tDevice="+indevice+"\tPackage="+inpackage);
                    break;
                case 1:
                    if (letter == 'X') {
                        inarchitecture = assembler(scrambled.substring(index, scrambled.length()));
                        index += inarchitecture.length();
                    } else if (Character.isDigit(letter)
                            || ((letter == 'F' || letter == 'S' || letter == 'L') && scrambled.charAt(index + 1) == 'X')) {
                        indevice = assembler(scrambled.substring(index, scrambled.length()));
                        index += indevice.length();
                    } else if (letter == '-' && Character.isDigit(scrambled.charAt(index + 1))) {
                        inspeed = assembler(scrambled.substring(index, scrambled.length()));
                        index += inspeed.length();
                    } else {
                        inpackage = assembler(scrambled.substring(index, scrambled.length()));
                        if (numdashes > 0)
                            index += inpackage.length() + (numdashes - 1);
                        else
                            index += inpackage.length();
                    }
                    //System.out.println("Architecture="+inarchitecture+"\tDevice="+indevice+"\tPackage="+inpackage);
                    break;
                case 2:
                    if (letter == 'X') {
                        inarchitecture = assembler(scrambled.substring(index, scrambled.length()));
                        index += inarchitecture.length();
                    } else if (Character.isDigit(letter)
                            || ((letter == 'F' || letter == 'S' || letter == 'L') && scrambled.charAt(index + 1) == 'X')) {
                        indevice = assembler(scrambled.substring(index, scrambled.length()));
                        index += indevice.length();
                    } else if (letter == '-' && Character.isDigit(scrambled.charAt(index + 1))) {
                        inspeed = assembler(scrambled.substring(index, scrambled.length()));
                        index += inspeed.length();
                    } else {
                        inpackage = assembler(scrambled.substring(index, scrambled.length()));
                        if (numdashes > 0)
                            index += inpackage.length() + (numdashes - 1);
                        else
                            index += inpackage.length();
                    }
                    //System.out.println("Architecture="+inarchitecture+"\tDevice="+indevice+"\tPackage="+inpackage);
                    break;
                case 3:
                    if (letter == 'X') {
                        inarchitecture = assembler(scrambled.substring(index, scrambled.length()));
                        index += inarchitecture.length();
                    } else if (Character.isDigit(letter)
                            || ((letter == 'F' || letter == 'S' || letter == 'L') && scrambled.charAt(index + 1) == 'X')) {
                        indevice = assembler(scrambled.substring(index, scrambled.length()));
                        index += indevice.length();
                    } else if (letter == '-' && Character.isDigit(scrambled.charAt(index + 1))) {
                        inspeed = assembler(scrambled.substring(index, scrambled.length()));
                        index += inspeed.length();
                    } else {
                        inpackage = assembler(scrambled.substring(index, scrambled.length()));
                        if (numdashes > 0)
                            index += inpackage.length() + (numdashes - 1);
                        else
                            index += inpackage.length();
                    }
                    //System.out.println("Architecture="+inarchitecture+"\tDevice="+indevice+"\tPackage="+inpackage);
                    break;
                default:
                    break;
                }//j switch
            }//while
        }//j for loop

        //System.out.println("Checking the validity of the following configuration:  Architecture="+inarchitecture+"\tDevice="+indevice+"\tPackage="+inpackage+"\tSpeed="+inspeed);

        setArchName(inarchitecture);
        setDevName(indevice);
        setPackageName(inpackage);

        if (!comboIsCorrect(inarchitecture, indevice, inpackage)) {
            backwardslist(inpackage);
            throw new IllegalArgumentException(
                    "ERROR: The architecture/device/package/speed configuration you entered is NOT valid!\n");
        }

        //System.out.println("........................................................................................................................");
        return inarchitecture + indevice + inpackage;
    }

    public String assembler(String portion) {
        int i = 0;
        String output = "";

        while (i < portion.length() && !Character.isDigit(portion.charAt(i))
                && !(output.equalsIgnoreCase("xcv") || output.equalsIgnoreCase("xqrv"))) {
            output = output + portion.charAt(i);
            i++;
        }//this while loop collects letters/nondigits
        //System.out.println("1:output is: "+output);
        if (output.length() > 1 && output.charAt(0) == '-' && !Character.isDigit(output.charAt(1))) {
            output = output.substring(1);//checks to see if there is a dash in front of the package
        }
        //System.out.println("2:output is: "+output);
        if (output.equalsIgnoreCase("xcv") || output.equalsIgnoreCase("xqrv"))
            return "XCV";
        while (i < portion.length() && Character.isDigit(portion.charAt(i))) {
            output = output + portion.charAt(i);
            i++;
        }//this while loop collects digits
        //System.out.println("3:output is: "+output);
        if (output.startsWith("XC2") || output.equals("XC4") || output.startsWith("XQR2") || output.equals("XQR4")) {
            output += 'V';
            i++;
            if (portion.charAt(i) == 'P') {
                output += "P";
                i++;
            }
        }

        //System.out.println("4:output is: "+output);
        return output;
    }

    public static final String XCV = "xcv", XC2V = "xc2v", XC2VP = "xc2vp", XC4V = "xc4v";

    public static final String XQRV = "xqrv", XQR2V = "xqr2v", XQR2VP = "xqr2vp", XQR4V = "xqr4v";

    public static final String[] archNames = { XCV, XC2VP, XC2V, XC4V, XQRV, XQR2VP, XQR2V, XQR4V };

    public static final String[] techNames = { "Virtex", "Virtex2Pro", "Virtex2", "Virtex4", "Virtex", "Virtex2Pro",
            "Virtex2", "Virtex4" };

    static public String getTechnologyFromPart(String part) {
        String technology = "";
        part = part.toLowerCase();
        for (int i = 0; i < archNames.length; i++) {
            if (part.contains(archNames[i])) {
                technology = techNames[i];
                break;
            }
        }
        return technology;
    }

    public void setArchName(String name) {
        if (!archNameIsValid(name)) {
            System.out.println("*******************************************************************");
            for (int i = 0; i < archNames.length; i++) {
                System.out.print(archNames[i] + " ");
            }
            System.out.println();
            throw new IllegalArgumentException("ERROR: Architecture name '" + name + "' is invalid.\n"
                    + "Valid architectures for Xilinx are above.\n"
                    + "*******************************************************************");
        }
    }

    public boolean archNameIsValid(String name) {
        boolean valid = false;
        for (int i = 0; i < archNames.length; i++) {
            if (name.equalsIgnoreCase(archNames[i]))
                valid = true;
        }
        return valid;
    }

    public void setDevName(String name) {
        if (!devNameIsValid(name)) {
            System.out.println("*******************************************************************");
            for (int i = 0; i < devNames.length; i++) {
                System.out.print(devNames[i] + " ");
            }
            System.out.println();
            throw new IllegalArgumentException("ERROR: Device name '" + name + "' is invalid.\n"
                    + "Valid devices for the Xilinx Architecture '" + archName + "' are above.\n"
                    + "*******************************************************************");
        }
    }

    public boolean devNameIsValid(String name) {
        boolean valid = false;
        for (int i = 0; i < devNames.length; i++) {
            if (name.equalsIgnoreCase(devNames[i]))
                valid = true;
        }
        return valid;
    }

    public void setPackageName(String name) {
        if (!packageNameIsValid(name)) {
            System.out.println("*******************************************************************");
            for (int i = 0; i < packageNames.length; i++) {//goes through the devices
                System.out.print(devNames[i] + ": ");
                for (int j = 0; j < packageNames[i].length; j++) {//goes through the packages
                    System.out.print(packageNames[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println();
            throw new IllegalArgumentException("ERROR: Package name '" + name + "' is invalid.\n"
                    + "Valid packages for the Xilinx Architecture '" + archName + "' are above.\n"
                    + "*******************************************************************");
        }
    }

    public boolean packageNameIsValid(String name) {
        boolean valid = false;
        for (int i = 0; i < packageNames.length; i++) {//goes through the devices
            for (int j = 0; j < packageNames[i].length; j++) {//goes through the packages
                if (name.equalsIgnoreCase(packageNames[i][j]))
                    valid = true;
            }
        }
        return valid;
    }

    public boolean comboIsCorrect(String arc, String dev, String pac) {
        for (int i = 0; i < archNames.length; i++) {
            if (arc.equalsIgnoreCase(archNames[i])) {
                for (int j = 0; j < devNames.length; j++) {
                    if (dev.equalsIgnoreCase(devNames[j])) {
                        for (int k = 0; k < packageNames[j].length; k++) {
                            if (pac.equalsIgnoreCase(packageNames[j][k]))
                                return true;
                        }
                        throw new IllegalArgumentException("ERROR: Architecture '" + arc + "' and Device '" + dev
                                + "' do NOT have an Package named '" + pac + "' associated with them!");
                    }
                }
                throw new IllegalArgumentException("ERROR: Architecture '" + arc + "' does NOT have a Device named '"
                        + dev + "' associated with it!");
            }
        }
        return false;
    }

    public void backwardslist(String pac) {
        for (int i = 0; i < devNames.length; i++) {//goes through the devices
            for (int j = 0; j < packageNames[i].length; j++) {//goes through the packages
                if (pac.equalsIgnoreCase(packageNames[i][j]))
                    System.out.println("The Package '" + packageNames[i][j] + "' is associated with the Architecture '"
                            + archName + "' and Device '" + devNames[i] + ".'");
            }
        }
    }

}
