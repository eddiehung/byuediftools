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
package edu.byu.ece.edif.util.iob;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifSingleBitPort;

public class XilinxVirtex4IOB extends XilinxVirtexIOB {

    public XilinxVirtex4IOB(EdifSingleBitPort esbp) {
        super(esbp);
    }

    //    Inheirited members: 
    //    protected EdifCellInstance _ibuf = null;
    //    protected EdifCellInstance _obuf = null;
    //    protected EdifCellInstance _iobuf = null;
    //    protected EdifCellInstance _inReg = null;
    //    protected EdifCellInstance _outReg = null;
    //    protected EdifCellInstance _triReg = null;//obuft
    //    protected EdifCellInstance _resistor = null;
    protected EdifCellInstance _ibufDS;

    protected EdifCellInstance _obufDS;

    protected EdifCellInstance _iobufDS;

    protected EdifCellInstance _obufTriDS;

}
