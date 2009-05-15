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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.tools.replicate.nmr.xilinx.XilinxResourceMapper;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;

/**
 * This class is currently written for Virtex devices only, though other
 * architectures will probably work to some degree. Virtex-II and Virtex-II Pro
 * work with this as far as we have been able to tell
 */
public class XilinxVirtexIOB extends XilinxIOB {

    private Collection<String> _iob_elements = new ArrayList<String>();

    protected Collection<EdifCellInstance> _elements = new ArrayList<EdifCellInstance>();

    //protected Collection<EdifCellInstance> _ologic = new ArrayList<EdifCellInstance>();

    public XilinxVirtexIOB(EdifSingleBitPort esbp) {
        _iob_elements.add("IDELAY");
        _iob_elements.add("ODELAY");
        _iob_elements.add("ILOGIC");
        _iob_elements.add("OLOGIC");
        _iob_elements.add("ISERDES");
        _iob_elements.add("OSERDES");
        _iob_elements.add("IDDR");
        _iob_elements.add("ODDR");
        _esbp = esbp;
    }

    public void setIBUF(EdifCellInstance ibuf, EdifCellInstanceGraph graph) {
    	String resourceType = XilinxResourceMapper.getInstance().getResourceType(ibuf);
        if (ibuf != null && !(resourceType.equals(XilinxResourceMapper.IO) || resourceType.equals(XilinxResourceMapper.IBUFG)))
            throw new EdifRuntimeException("Attempting to add non-I/O Buffer as an input buffer");
        _ibuf = ibuf;
        iobComponentsDFS(ibuf, graph);
    }

    private void iobComponentsDFS(EdifCellInstance ibuf, EdifCellInstanceGraph graph) {
        //depth first search on ibuf to find any ilogic
        Collection<?> successors = graph.getSuccessors(ibuf);
        Stack<Object> dfs_stack = new Stack<Object>();
        dfs_stack.addAll(successors);
        //for (Object successor : dfs_stack) {
        while (dfs_stack.size() > 0) {
            Object successor = dfs_stack.pop();
            if (!(successor instanceof EdifCellInstance))
                continue;
            EdifCellInstance eci = (EdifCellInstance) successor;
            String cellType = eci.getCellType().getName();
            //If this is an IOB element, then keep going. 
            //Otherwise we are done with this branch.
            if (_iob_elements.contains(cellType)) {
                //System.out.println("packing logic:" + eci.getName());
                _elements.add(eci);
                dfs_stack.addAll(graph.getSuccessors(successor));
            }
        }
    }

    public void setOBUF(EdifCellInstance obuf, EdifCellInstanceGraph graph) {
        if (obuf != null && !XilinxResourceMapper.getInstance().getResourceType(obuf).equals(XilinxResourceMapper.IO))
            throw new EdifRuntimeException("Attempting to add non-I/O Buffer as an input buffer");
        _obuf = obuf;
        iobComponentsDFS(obuf, graph);
    }

    public Collection<EdifCellInstance> getAllInstances() {
        Collection<EdifCellInstance> ecis = super.getAllInstances();
        ecis.addAll(_elements);
        return ecis;
    }

    public boolean canPack() {
        if (_ibuf != null)
            if (_ibuf.getType().startsWith("IBUFG") || containsDDR())
                return false;
        return true;
    }

    public boolean containsDDR() {
        return false;
    }

}
