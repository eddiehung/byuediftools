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

import java.util.ArrayList;
import java.util.Collection;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.Parameter;

import edu.byu.ece.edif.util.jsap.EdifCommandParser;

/**
 * This class provides a way to easily group and maintain command line options
 * for the edif tools, using {@link JSAP}. Classes that inherit from this class
 * should be used in conjunction with the EdifCommandParser
 * 
 * @see EdifCommandParser
 * @see <a href="http://www.martiansoftware.com/jsap/doc/javadoc/index.html">
 * JSAP API</a>
 * @see <a href="http://www.martiansoftware.com/jsap/">JSAP Homepage</a>
 * @see JSAP
 * @author dsgib
 */

public abstract class AbstractCommandGroup implements CommandGroup {

    public AbstractCommandGroup() {
        _options = new ArrayList<Parameter>();
    }

    public Parameter addCommand(Parameter o) {
        _options.add(o);
        return o;
    }

    public void addCommandsToParser(EdifCommandParser ecp) {
    	ecp.addCommands(this);
    }
    
    
    public Collection<Parameter> getCommands() {
        return _options;
    }

    public static final String FALSE = "false";
	
	public static final String TRUE = "true";

    Collection<Parameter> _options;

}
