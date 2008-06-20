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
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxResourceMapper;

public abstract class XilinxIOB extends AbstractIOB {

    public XilinxIOB() {

    }

    /**
     * Determines whether this XilinxVirtexIOB object corresponds to a clock
     * IOB. This is done simply by checking the input buffer object. If the
     * input buffer is an IBUFG*, then this is a clock IOB.
     * 
     * @return true if this XilinxVirtexIOB object corresponds to a clock IOB,
     * false otherwise.
     */
    public boolean canPack() {
        if (_ibuf != null)
            if (_ibuf.getType().startsWith("IBUFG"))
                return false;
        return true;
    }

    public void setResistor(EdifCellInstance resistor) {
        if (resistor != null && !XilinxResourceMapper.getResourceType(resistor).equals(XilinxResourceMapper.RES))
            throw new EdifRuntimeException("Attempting to add non-resistor/keeper element as an IOB resistor/keeper");

        _resistor = resistor;
    }

    public void setIBUF(EdifCellInstance ibuf) {
        if (ibuf != null && !XilinxResourceMapper.getResourceType(ibuf).equals(XilinxResourceMapper.IO))
            throw new EdifRuntimeException("Attempting to add non-I/O Buffer as an input buffer");

        _ibuf = ibuf;
    }

    public void setOBUF(EdifCellInstance obuf) {
        if (obuf != null && !XilinxResourceMapper.getResourceType(obuf).equals(XilinxResourceMapper.IO))
            throw new EdifRuntimeException("Attempting to add non-I/O Buffer as an output buffer");

        _obuf = obuf;
    }

    public void setIOBUF(EdifCellInstance iobuf) {
        if (iobuf != null && !XilinxResourceMapper.getResourceType(iobuf).equals(XilinxResourceMapper.IO))
            throw new EdifRuntimeException("Attempting to add non-I/O Buffer as an input/output buffer");

        _iobuf = iobuf;
    }

    public void setInputReg(EdifCellInstance inputReg) {
        if (inputReg != null && !XilinxResourceMapper.getResourceType(inputReg).equals(XilinxResourceMapper.FF))
            throw new EdifRuntimeException("Attempting to add non-flip-flop element as an IOB input register");

        _inReg = inputReg;
    }

    public void setOutputReg(EdifCellInstance outputReg) {
        if (outputReg != null && !XilinxResourceMapper.getResourceType(outputReg).equals(XilinxResourceMapper.FF))
            throw new EdifRuntimeException("Attempting to add non-flip-flop element as an IOB output register");

        _outReg = outputReg;
    }

    public void setTristateReg(EdifCellInstance tristateReg) {
        if (tristateReg != null && !XilinxResourceMapper.getResourceType(tristateReg).equals(XilinxResourceMapper.FF))
            throw new EdifRuntimeException("Attempting to add non-flip-flop element as an IOB tristate register");

        _triReg = tristateReg;
    }

}
