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
package edu.byu.ece.edif.tools.replicate;

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

public class PartialReplicationStringDescription implements Serializable {
    public static final long serialVersionUID = 42L;

    public PartialReplicationStringDescription(PartialReplicationDescription ptmr) throws ReplicationException {

        //cell=ptmr.cellToReplicate;
        _cellToReplicate = new EdifNameableParentStringReference(ptmr.cellToReplicate);
        _instancesToReplicate = new ArrayList<Replication>();
        _portsToReplicate = new ArrayList<Replication>();

        // copy instances to replicate
        if (ptmr.instancesToReplicate != null) {
            for (Replication rep : ptmr.instancesToReplicate)
                _instancesToReplicate.add(rep.toStringReference());
        }

        if (ptmr.portsToReplicate != null) {
            for (Replication rep : ptmr.portsToReplicate)
                _portsToReplicate.add(rep.toStringReference());
        }

        // Copy EPRS to cut
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

    public PartialReplicationDescription getDescription(EdifEnvironment env) throws ReplicationException {
        PartialReplicationDescription desc = new PartialReplicationDescription();

        desc.cellToReplicate = _cellToReplicate.getEdifCell(env);
        EdifCell flatCell = desc.cellToReplicate;
        //		desc.cellToTriplicate=cell;
        EdifCellInstance tmpeci = null;
        EdifPort tmpport = null;
        EdifNameableStringReference newref = null;

        for (Replication rep : _instancesToReplicate) {
            newref = rep.getStringRef();
            tmpeci = newref.getEdifCellInstance(desc.cellToReplicate);
            rep.setObjectToBeReplicated(tmpeci);
            desc.instancesToReplicate.add(rep);
        }

        for (Replication rep : _portsToReplicate) {
            newref = rep.getStringRef();
            tmpport = newref.getEdifPort(desc.cellToReplicate);
            rep.setObjectToBeReplicated(tmpport);
            desc.portsToReplicate.add(rep);
        }

        //System.out.print("starting portRefs: . . .");
        for (EdifPortRefStringReference epr : _portRefsToCut) {
            desc.portRefsToCut.add(epr.getEPRFromReference(flatCell));
        }

        return desc;
    }

    //  protected EdifCell cell;
    protected EdifNameableParentStringReference _cellToReplicate;

    protected Collection<Replication> _portsToReplicate;

    protected Collection<Replication> _instancesToReplicate;

    protected Collection<EdifPortRefStringReference> _portRefsToCut;

    protected Collection<EdifNameableStringReference> _feedbackPlusInput;

    /**
     * @return the _cellToReplicate
     */
    public EdifNameableParentStringReference get_cellToReplicate() {
        return _cellToReplicate;
    }

    /**
     * @return the _portsToReplicate
     */
    public Collection<Replication> get_portsToReplicate() {
        return _portsToReplicate;
    }

    /**
     * @return the _instancesToReplicate
     */
    public Collection<Replication> get_instancesToReplicate() {
        return _instancesToReplicate;
    }

    /**
     * @return the _portRefsToCut
     */
    public Collection<EdifPortRefStringReference> get_portRefsToCut() {
        return _portRefsToCut;
    }

    /**
     * @return the _feedbackPlusInput
     */
    public Collection<EdifNameableStringReference> get_feedbackPlusInput() {
        return _feedbackPlusInput;
    }

    public static void main(String args[]) throws ReplicationException {

        FileInputStream fis = null;
        ObjectInputStream in = null;
        EdifEnvironment new_top = null;
        String filename = ("enigma.jedif");
        String output_filename = "smallptmr.ptmr";
        //String filename = ("hl_dut.jedif");
        //String output_filename = "bigptmr.ptmr";

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
        PartialReplicationDescription ptrm1 = new PartialReplicationDescription();
        ptrm1.cellToReplicate = flatCell;
        ptrm1.portRefsToCut = eprc;
        for (EdifPort port : flatCell.getPortList())
            ptrm1.add(new Triplication(port));
        for (Object obj : flatCell.getSortedSubCellList()) {
            EdifCellInstance eci = (EdifCellInstance) obj;
            ptrm1.add(new Triplication(eci));
        }
        stream.println(ptrm1);

        System.out.print("Done.\nOutputing " + output_filename + ". . .");

        PartialReplicationStringDescription ptmrs1 = new PartialReplicationStringDescription(ptrm1);
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
            PartialReplicationDescription ptrm = new PartialReplicationDescription();
            FileInputStream fos = null;
            ObjectInputStream out_object = null;
            PartialReplicationStringDescription ptmrs = null;
            try {
                fos = new FileInputStream(output_filename);
                out_object = new ObjectInputStream(fos);
                ptmrs = (PartialReplicationStringDescription) out_object.readObject();
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
