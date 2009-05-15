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
package edu.byu.ece.edif.util.clockdomain;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxResourceMapper;

public class ClockResource {

    EdifCellInstance _driver;

    EdifNet _feedback;

    Set<EdifNet> _clocks;

    public ClockResource(EdifCellInstance eci) {
        _driver = eci;
        _clocks = new LinkedHashSet<EdifNet>();
        if (XilinxResourceMapper.getInstance().getResourceType(_driver).equals(XilinxResourceMapper.DCM)
                || XilinxResourceMapper.getInstance().getResourceType(_driver).equals(XilinxResourceMapper.DLL)) {
            for (EdifPortRef epr : _driver.getInputEPRs()) {
                if (epr.getPort().getName().toLowerCase().equals("clkfb"))
                    _feedback = epr.getNet();
            }
            for (EdifPortRef epr : _driver.getOutputEPRs()) {
                String portName = epr.getPort().getName().toLowerCase();
                if (portName.contains("clk")) {
                    _clocks.add(epr.getNet());
                }
            }
        } else {
            _clocks.add(_driver.getOutputEPRs().iterator().next().getNet());
        }
    }

    public EdifNet getFeedbackNet() {
        return _feedback;
    }

    public boolean hasFeedbackNet() {
        return _feedback != null;
    }

    public String toString() {
        String str = "";
        str += "Driver: " + _driver + ", Clocks: " + _clocks;
        if (_feedback != null)
            str += ", Feedback: " + _feedback;
        return str;
    }
}
