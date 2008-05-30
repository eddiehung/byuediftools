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
package edu.byu.ece.edif.tools.replicate.nmr.tmr;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.tools.replicate.nmr.EdifNameableParentStringReference;
import edu.byu.ece.edif.tools.replicate.nmr.EdifNameableStringReference;
import edu.byu.ece.edif.tools.replicate.nmr.EdifPortRefStringReference;

public class PartialTMRStringDescription implements Serializable {
    public static final long serialVersionUID = 42L;

    public PartialTMRStringDescription(PartialTMRDescription ptmr) {

        //cell=ptmr.cellToTriplicate;
        _cellToTriplicate = new EdifNameableParentStringReference(ptmr.cellToTriplicate);

        // copy instances to triplicate
        if (ptmr.instancesToTriplicate != null) {
            _instancesToTriplicate = new ArrayList<EdifNameableStringReference>(ptmr.instancesToTriplicate.size());

            for (EdifCellInstance eci : ptmr.instancesToTriplicate) {
                EdifNameableStringReference eciString = new EdifNameableStringReference(eci);
                _instancesToTriplicate.add(eciString);
            }
        } else
            _instancesToTriplicate = new ArrayList<EdifNameableStringReference>();

        // Copy ports to triplicate
        if (ptmr.portsToTriplicate != null) {
            _portsToTriplicate = new ArrayList<EdifNameableStringReference>(ptmr.portsToTriplicate.size());
            for (EdifPort ep : ptmr.portsToTriplicate) {
                EdifNameableStringReference epString = new EdifNameableStringReference(ep);
                _portsToTriplicate.add(epString);
            }
        } else
            _portsToTriplicate = new ArrayList<EdifNameableStringReference>();

        // Copy EPRS to triplicate
        if (ptmr.portRefsToCut != null) {

            _portRefsToCut = new ArrayList<EdifPortRefStringReference>(ptmr.portRefsToCut.size());
            for (EdifPortRef epr : ptmr.portRefsToCut) {
                EdifPortRefStringReference erpString = new EdifPortRefStringReference(epr);
                _portRefsToCut.add(erpString);
                //System.out.println(erpString.getEPRFromReference(ptmr.cellToTriplicate));
            }
        } else
            _portRefsToCut = new ArrayList<EdifPortRefStringReference>();
    }

    public PartialTMRDescription getDescription(EdifEnvironment env) {
        PartialTMRDescription desc = new PartialTMRDescription();

        desc.cellToTriplicate = _cellToTriplicate.getEdifCell(env);
        EdifCell flatCell = desc.cellToTriplicate;
        //		desc.cellToTriplicate=cell;
        EdifCellInstance tmp = null;
        for (EdifNameableStringReference itr : _instancesToTriplicate) {
            tmp = itr.getEdifCellInstance(desc.cellToTriplicate);
            desc.instancesToTriplicate.add(tmp);
        }

        for (EdifNameableStringReference ptt : _portsToTriplicate) {
            desc.portsToTriplicate.add(ptt.getEdifPort(desc.cellToTriplicate));
        }

        //System.out.print("starting portRefs: . . .");
        for (EdifPortRefStringReference epr : _portRefsToCut) {
            desc.portRefsToCut.add(epr.getEPRFromReference(flatCell));
        }

        return desc;
    }

    //	protected EdifCell cell;
    protected EdifNameableParentStringReference _cellToTriplicate;

    protected Collection<EdifNameableStringReference> _portsToTriplicate;

    protected Collection<EdifNameableStringReference> _instancesToTriplicate;

    protected Collection<EdifPortRefStringReference> _portRefsToCut;

    public static void main(String args[]) {

        FileInputStream fis = null;
        ObjectInputStream in = null;
        EdifEnvironment new_top = null;
        //String filename = ("enigma.jedif");
        //String output_filename = "smallptmr.ptmr";
        String filename = ("hl_dut.jedif");
        String output_filename = "bigptmr.ptmr";

        System.out.print("Loading file " + filename + " . . .");
        try {
            fis = new FileInputStream(filename);
            in = new ObjectInputStream(fis);
            new_top = (EdifEnvironment) in.readObject();
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        System.out.print("Done\nStarting beforeptmr.txt...");

        PrintStream stream = null;
        try {
            stream = new PrintStream(new FileOutputStream("beforeptmr.txt"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }

        EdifCell flatCell = new_top.getTopCell();
        Collection<EdifPortRef> eprc = flatCell.getPortRefs();
        PartialTMRDescription ptrm1 = new PartialTMRDescription();
        ptrm1.cellToTriplicate = flatCell;
        ptrm1.portRefsToCut = eprc;
        ptrm1.portsToTriplicate = flatCell.getPortList();
        ptrm1.instancesToTriplicate = flatCell.getSortedSubCellList();
        stream.println(ptrm1);

        System.out.print("Done.\nOutputing " + output_filename + ". . .");

        PartialTMRStringDescription ptmrs1 = new PartialTMRStringDescription(ptrm1);
        {
            FileOutputStream fos = null;
            ObjectOutputStream out_object = null;
            try {
                fos = new FileOutputStream(output_filename);
                out_object = new ObjectOutputStream(fos);
                out_object.writeObject(ptmrs1);
                out_object.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        System.out.print("Done\nReading " + output_filename + ". . .");

        {
            PartialTMRDescription ptrm = new PartialTMRDescription();
            FileInputStream fos = null;
            ObjectInputStream out_object = null;
            PartialTMRStringDescription ptmrs = null;
            try {
                fos = new FileInputStream(output_filename);
                out_object = new ObjectInputStream(fos);
                ptmrs = (PartialTMRStringDescription) out_object.readObject();
                out_object.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            System.out.print("Done\nLoading file . . .");
            ptrm = ptmrs.getDescription(new_top);
            System.out.print("Done\nWriting afterptmr . . .");

            stream = null;
            try {
                stream = new PrintStream(new FileOutputStream("afterptmr.txt"));
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e);
            }

            stream.println(ptrm);
            System.out.println("done.");
        }
    }

}
