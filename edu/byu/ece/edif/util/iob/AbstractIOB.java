/* * Copyright (c) 2008 Brigham Young University
 *
 * This file is part of the BYU EDIF Tools.
 * 
 * BYU EDIF Tools is free software: you may redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * BYU EDIF Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License is included with the BYU
 * EDIF Tools. It can be found at /edu/byu/edif/doc/gpl2.txt. You may
 * also get a copy of the license at <http://www.gnu.org/licenses/>.
 *
 */
package edu.byu.ece.edif.util.iob;

import java.util.ArrayList;
import java.util.Collection;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifSingleBitPort;


//AbstractIOB
/**
 * An abstract implementation of a device utilization tracker.
 * 
 * @author Gavin Ransom
 */
public abstract class AbstractIOB {
	
	protected EdifSingleBitPort _esbp;
	protected EdifCellInstance _ibuf = null;
	protected EdifCellInstance _obuf = null;
	protected EdifCellInstance _iobuf = null;
	protected EdifCellInstance _inReg = null;
	protected EdifCellInstance _outReg = null;
	protected EdifCellInstance _triReg = null;
	protected EdifCellInstance _resistor = null;

	public AbstractIOB(){
		
	}
	
	abstract boolean isClockIOB();
	
	abstract public void setResistor(EdifCellInstance resistor);
	
	abstract public void setIBUF(EdifCellInstance ibuf);
	
	abstract public void setOBUF(EdifCellInstance obuf);
	
	abstract public void setIOBUF(EdifCellInstance iobuf);
	
	abstract public void setInputReg(EdifCellInstance inputReg);
	
	abstract public void setOutputReg(EdifCellInstance outputReg);
	
	abstract public void setTristateReg(EdifCellInstance tristateReg);

	public EdifSingleBitPort getSingleBitPort() {
		return _esbp;
	}

	public EdifCellInstance getIBUF() {
		return _ibuf;
	}

	public EdifCellInstance getInputReg() {
		return _inReg;
	}

	public EdifCellInstance getOutputReg() {
		return _outReg;
	}

	public EdifCellInstance getTristateReg() {
		return _triReg;
	}

	public EdifCellInstance getIOBUF() {
		return _iobuf;
	}

	public EdifCellInstance getOBUF() {
		return _obuf;
	}

	public String toString() {
		return _esbp.toString();
	}

	public EdifCellInstance getResistor() {
		return _resistor;
	}

	public Collection<EdifCellInstance> getRegs() {
		Collection<EdifCellInstance> regs = new ArrayList<EdifCellInstance>();
		if (_inReg != null) regs.add(_inReg);
		if (_outReg != null) regs.add(_outReg);
		if (_triReg != null) regs.add(_triReg);
		
		return regs;
	}

	public Collection<EdifCellInstance> getAllInstances() {
		Collection<EdifCellInstance> ecis = new ArrayList<EdifCellInstance>();
		ecis.addAll(getRegs());
		if (_ibuf != null) ecis.add(_ibuf);
		if (_obuf != null) ecis.add(_obuf);
		if (_iobuf != null) ecis.add(_iobuf);
		if (_resistor != null) ecis.add(_resistor);
		
		return ecis;
	}
}
