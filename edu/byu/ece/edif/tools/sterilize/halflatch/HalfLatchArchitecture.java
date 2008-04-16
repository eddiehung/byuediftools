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
package edu.byu.ece.edif.tools.sterilize.halflatch;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifLibraryManager;
import edu.byu.ece.edif.core.EdifPort;

/**
 * @since Created on Oct 27, 2005
 */
public interface HalfLatchArchitecture {

    public EdifCellInstance addConstantCellInstance(EdifCell cell, int safeConstantPolarity);

    public String getSafeConstantCellOutputPortName();

    public boolean cellRequiresReplacement(EdifCellInstance edifCellInstance);

    public boolean isConstantCell(String edifCellInstanceType);

    public int getConstantCellValue(String edifCellInstanceType);

    public String getPrimitiveReplacementType(EdifCellInstance edifCellInstance);

    public String[] getPrimitiveReplacementFloatingPorts(EdifCellInstance edifCellInstance);

    public int getPrimitiveReplacementFloatingPortDefaultValue(EdifCellInstance edifCellInstance, String floatingPort);

    public EdifCell findOrAddPrimitiveReplacementCell(EdifLibraryManager elm, String safePrimitiveType);

    public EdifCell findOrAddPrimitiveInverterCell(EdifLibraryManager elm);

    public String getPrimitiveInverterCellInputPortName();

    public String getPrimitiveInverterCellOutputPortName();

    public EdifCell findOrAddPrimitiveInputBufferCell(EdifLibraryManager elm);

    public String getPrimitiveInputBufferCellInputPortName();

    public String getPrimitiveInputBufferOutputBufferName();

    public boolean isBadCutPin(EdifPort port);

}
