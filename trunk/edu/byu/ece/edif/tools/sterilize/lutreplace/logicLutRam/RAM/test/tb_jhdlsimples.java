/*
 * Testbench to compare logic RAMX1S to Xilinx RAMX1S primitive.
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
package edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.RAM.test;

import java.io.PrintStream;
import java.util.Random;

import byucc.jhdl.Logic.Logic;
import byucc.jhdl.base.HWSystem;
import byucc.jhdl.base.Node;
import byucc.jhdl.base.TestBench;
import byucc.jhdl.base.Wire;

/**
 * Testbench used to compare a single logic implementation of a RAMX1S with a
 * single Xilinx RAMX1S primitive.
 * 
 * @author Nathan Rollins
 * @version $Id:tb_jhdlsimples.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class tb_jhdlsimples extends Logic implements TestBench {

    static PrintStream outfile;

    static int _count;

    static int _indata;

    static int _ra;

    static int _we;

    static int _o16x1djhdl;

    static int _o16x1dlogic;

    static Random _rand;

    Wire indata;

    Wire clk;

    Wire we;

    Wire ra;

    Wire o16x1djhdl;

    Wire o16x1dlogic;

    public tb_jhdlsimples(Node parent) {
        super(parent);

        indata = wire(8, "indata");
        we = wire(1, "we");
        clk = wire(1, "clk");
        ra = wire(7, "ra");
        o16x1djhdl = wire(1, "o16x1djhdl");
        o16x1dlogic = wire(1, "o16x1dlogic");

        clockDriver(clk, "01");
        jhdlsimpletests logicram = new jhdlsimpletests(this, indata, we, clk, ra, o16x1dlogic, o16x1djhdl);
    }

    public void clock() {
        setInputs();
        if (_count > 0)
            getResults();
        _count++;
    }

    public void getResults() {
        _o16x1djhdl = o16x1djhdl.get(this);
        _o16x1dlogic = o16x1dlogic.get(this);
    }

    public void reset() {
        indata.put(this, 0);
        we.put(this, 0);
        ra.put(this, 0);

        _count = 0;
        _rand = new Random();
    }

    public void setInputs() {
        _indata = (_count % 2 == 1) ? _rand.nextInt(1 << 8) : _indata;
        _ra = (_count % 2 == 1) ? _rand.nextInt(1 << 7) : _ra;
        _we = (_count % 2 == 1) ? _rand.nextInt(2) : _we;

        indata.put(this, _indata);
        we.put(this, _we);
        ra.put(this, _ra);

    }

    public static void main(String argv[]) throws Exception {

        HWSystem hw = new HWSystem();
        tb_jhdlRAM tb = new tb_jhdlRAM(hw);

        int cnt = 0;
        int MAX = 100;
        String file = "results.dat";

        //outfile = new PrintStream(new FileWriter(file));
        outfile = new PrintStream(System.out);

        outfile.print("INDATA\tRA\tWE\tr16x1d\tr16x1d1\tr16x1s\tr16x1s1\t");
        outfile.print("r32x1d\tr32x1d1\tr32x1s\tr32x1s1\tr64x1d\tr64x1d1\t");
        outfile.println("r64x1s\tr64x1s1\tr64x2s\tr128x1s\tr128x1s1");
        outfile.print("======\t==\t==\t======\t=======\t======\t=======\t");
        outfile.print("======\t=======\t======\t=======\t======\t=======\t");
        outfile.println("======\t=======\t======\t=======\t========");

        while (cnt < MAX) {
            outfile.print(_indata + "\t" + _ra + "\t" + _we + "\t");
            outfile.print(_o16x1djhdl + " " + _o16x1dlogic + "\t");

            /*
             * if (_o16x1dlogic != _o16x1djhdl) { outfile.print("o16x1d "); } if
             * (_o16x1d1logic != _o16x1d1jhdl) { outfile.print("o16x1d1 "); } if
             * (_o16x1slogic != _o16x1sjhdl) { outfile.print("o16x1s "); } if
             * (_o16x1s1logic != _o16x1s1jhdl) { outfile.print("o16x1s1 "); } if
             * (_o32x1dlogic != _o32x1djhdl) { outfile.print("o32x1d "); } if
             * (_o32x1d1logic != _o32x1d1jhdl) { outfile.print("o32x1d1 "); } if
             * (_o32x1slogic != _o32x1sjhdl) { outfile.print("o32x1s "); } if
             * (_o32x1s1logic != _o32x1s1jhdl) { outfile.print("o32x1s1 "); } if
             * (_o64x1dlogic != _o64x1djhdl) { outfile.print("o64x1d "); } if
             * (_o64x1d1logic != _o64x1d1jhdl) { outfile.print("o64x1d1 "); } if
             * (_o64x1slogic != _o64x1sjhdl) { outfile.print("o64x1s "); } if
             * (_o64x1s1logic != _o64x1s1jhdl) { outfile.print("o64x1s1 "); } if
             * (_o64x2slogic != _o64x2sjhdl) { outfile.print("o64x2s "); } if
             * (_o128x1slogic != _o128x1sjhdl) { outfile.print("o128x1s "); } if
             * (_o128x1s1logic != _o128x1s1jhdl) { outfile.print("o128x1s1 "); }
             */
            outfile.println();
            cnt++;
            hw.cycle(1);
        }
    }

}
