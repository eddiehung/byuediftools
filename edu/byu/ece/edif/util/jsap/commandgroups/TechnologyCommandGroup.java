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
package edu.byu.ece.edif.util.jsap.commandgroups;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.tools.replicate.nmr.NMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxNMRArchitecture;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxPartValidator;

/**
 * @author Derrick Gibelyou
 */
public class TechnologyCommandGroup extends AbstractCommandGroup {

    ///////////////////////////////////////////////////////////////////
    //  Constructors

    /**
     * Creates the Technology Commandline options. Make sure to call
     * getPartFromEDIF() after the commandline has been parsed.
     */
    public TechnologyCommandGroup() {
        super();

        _part = new FlaggedOption(PART);
        _part.setStringParser(JSAP.STRING_PARSER);
        _part.setRequired(JSAP.NOT_REQUIRED);
        _part.setShortFlag('p');
        _part.setLongFlag(PART);
        //_part.setDefault("xcv1000fg680");
        _part.setHelp("Target architecture for the replicated design. "
                + "Valid parts include all parts from the Virtex, Virtex2, "
                + "and Virtex4 product lines. Not case-sensitive.");
        this.addCommand(_part);

    }

    ///////////////////////////////////////////////////////////////////
    //  Public fields

    public static final String PART = "part";

    ///////////////////////////////////////////////////////////////////
    //  Public methods

    static public NMRArchitecture getArch(JSAPResult result) {
        return getArchitecture(tech_str);
    }

    static public String getPart(JSAPResult result) {
        //return result.getString(PART);
        return part_str;
    }

    /**
     * Scans the EDIF Environment for the PART string. if it exists and is
     * valid, the default is assigned. Otherwise throw an exception. If you do
     * not require the part to be set, then you can catch and ignore the
     * exception.
     * <p>
     * After calling this function, getPart(), getTech(), and getArch() can be
     * called normally.
     * 
     * @param result
     * @param env
     * @throws IllegalArgumentException
     */
    public static void getPartFromEDIF(JSAPResult result, EdifEnvironment env) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        if (result.getString(PART) == null) {
            try {
                Property part = env.getTopDesign().getProperty("PART");
                part_str = part.getValue().toString();
                //_part.setDefault(p);

                tech_str = XilinxPartValidator.getTechnologyFromPart(part.getValue().toString());
                //XilinxPartValidator  xpv =  new XilinxPartValidator(part.getValue().toString());
                //_tech.setDefault(t);
                //System.out.println("got part "+ p	+ " and tech "+ t	+" from edif file");
                //System.out.println("got part "+ _part.getDefault()[0] + " and tech "+_tech.getDefault()[0]+" from edif file");
            } catch (Exception ex) {
                throw new IllegalArgumentException("\nERROR: Can't load part info from edif file. Please specify "
                        + "part on the command line.\n");
            }
        } else {
            //String partstr=getPart(result);
            part_str = result.getString(PART);
            env.getTopDesign().addProperty(new Property("PART", part_str));
            tech_str = XilinxPartValidator.getTechnologyFromPart(part_str);
            //System.out.println("got part "+ _part.getDefault() + " and tech "+_tech.getDefault()+" from command line");
        }

    }

    static public String getTech(JSAPResult result) {
        //return result.getString(TECHNOLOGY);
        return tech_str;
    }

    ///////////////////////////////////////////////////////////////////
    //  Protected methods

    /**
     * Return a TMRArchitecture object for the specified technology.
     * 
     * @param technologyString The specified technology
     * @return A TMRArchtecture object
     */
    protected static XilinxNMRArchitecture getArchitecture(String technologyString) {
        return new XilinxNMRArchitecture();
    }

    ///////////////////////////////////////////////////////////////////
    //  Protected fields

    static protected FlaggedOption _part;

    static protected String part_str;

    static protected String tech_str;

    protected static final String VIRTEX = "virtex";

    protected static final String VIRTEX2 = "virtex2";

    protected static final String VIRTEX4 = "virtex4";

}
