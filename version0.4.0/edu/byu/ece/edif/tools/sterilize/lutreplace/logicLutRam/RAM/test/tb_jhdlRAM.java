/*
 * Testbench used to compare logic RAMs with JHDL Xilinx RAM primitives.
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
 * Testbench used to compare logic RAM implementation with JHDL Xilinx RAM
 * primitives.
 * 
 * @author Nathan Rollins
 * @version $Id:tb_jhdlRAM.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class tb_jhdlRAM extends Logic implements TestBench {

    // input values to RAMs as integers
    static int _count;

    static int _indata;

    static int _ra;

    static int _wa;

    static int _we;

    // RAM output values as integers
    static int _o16x1djhdl;

    static int _o16x1d1jhdl;

    static int _o16x1sjhdl;

    static int _o16x1s1jhdl;

    static int _o32x1djhdl;

    static int _o32x1d1jhdl;

    static int _o32x1sjhdl;

    static int _o32x1s1jhdl;

    static int _o64x1djhdl;

    static int _o64x1d1jhdl;

    static int _o64x1sjhdl;

    static int _o64x1s1jhdl;

    static int _o64x2sjhdl;

    static int _o128x1sjhdl;

    static int _o128x1s1jhdl;

    static int _o16x1dlogic;

    static int _o16x1d1logic;

    static int _o16x1slogic;

    static int _o16x1s1logic;

    static int _o32x1dlogic;

    static int _o32x1d1logic;

    static int _o32x1slogic;

    static int _o32x1s1logic;

    static int _o64x1dlogic;

    static int _o64x1d1logic;

    static int _o64x1slogic;

    static int _o64x1s1logic;

    static int _o64x2slogic;

    static int _o128x1slogic;

    static int _o128x1s1logic;

    // Stream to output results to
    static PrintStream _outfile;

    // random number generator
    static Random _rand;

    // wires to connect design
    Wire indatajhdl;

    Wire wejhdl;

    Wire clk;

    Wire wajhdl;

    Wire rajhdl;

    Wire o16x1djhdl;

    Wire o16x1d1jhdl;

    Wire o16x1sjhdl;

    Wire o16x1s1jhdl;

    Wire o32x1djhdl;

    Wire o32x1d1jhdl;

    Wire o32x1sjhdl;

    Wire o32x1s1jhdl;

    Wire o64x1djhdl;

    Wire o64x1d1jhdl;

    Wire o64x1sjhdl;

    Wire o64x1s1jhdl;

    Wire o64x2sjhdl;

    Wire o128x1sjhdl;

    Wire o128x1s1jhdl;

    Wire indatalogic;

    Wire welogic;

    Wire walogic;

    Wire ralogic;

    Wire o16x1dlogic;

    Wire o16x1d1logic;

    Wire o16x1slogic;

    Wire o16x1s1logic;

    Wire o32x1dlogic;

    Wire o32x1d1logic;

    Wire o32x1slogic;

    Wire o32x1s1logic;

    Wire o64x1dlogic;

    Wire o64x1d1logic;

    Wire o64x1slogic;

    Wire o64x1s1logic;

    Wire o64x2slogic;

    Wire o128x1slogic;

    Wire o128x1s1logic;

    /**
     * Default constructor
     * 
     * @param parent parent node
     */
    public tb_jhdlRAM(Node parent) {
        super(parent);

        indatajhdl = wire(8, "indatajhdl");
        wejhdl = wire(1, "wejhdl");
        clk = wire(1, "clk");
        wajhdl = wire(7, "wajhdl");
        rajhdl = wire(7, "rajhdl");
        o16x1djhdl = wire(2, "o16x1djhdl");
        o16x1d1jhdl = wire(2, "o16x1d1jhdl");
        o16x1sjhdl = wire(1, "o16x1sjhdl");
        o16x1s1jhdl = wire(1, "o16x1s1jhdl");
        o32x1djhdl = wire(2, "o32x1djhdl");
        o32x1d1jhdl = wire(2, "o32x1d1jhdl");
        o32x1sjhdl = wire(1, "o32x1sjhdl");
        o32x1s1jhdl = wire(1, "o32x1s1jhdl");
        o64x1djhdl = wire(2, "o64x1djhdl");
        o64x1d1jhdl = wire(2, "o64x1d1jhdl");
        o64x1sjhdl = wire(1, "o64x1sjhdl");
        o64x1s1jhdl = wire(1, "o64x1s1jhdl");
        o64x2sjhdl = wire(2, "o64x2sjhdl");
        o128x1sjhdl = wire(1, "o128x1sjhdl");
        o128x1s1jhdl = wire(1, "o128x1s1jhdl");

        clockDriver(clk, "01");
        jhdlramtest realram = new jhdlramtest(this, indatajhdl, wejhdl, clk, wajhdl, rajhdl, o16x1djhdl, o16x1d1jhdl,
                o16x1sjhdl, o16x1s1jhdl, o32x1djhdl, o32x1d1jhdl, o32x1sjhdl, o32x1s1jhdl, o64x1djhdl, o64x1d1jhdl,
                o64x1sjhdl, o64x1s1jhdl, o64x2sjhdl, o128x1sjhdl, o128x1s1jhdl);

        indatalogic = wire(8, "indatalogic");
        welogic = wire(1, "welogic");
        walogic = wire(7, "walogic");
        ralogic = wire(7, "ralogic");
        o16x1dlogic = wire(2, "o16x1dlogic");
        o16x1d1logic = wire(2, "o16x1d1logic");
        o16x1slogic = wire(1, "o16x1slogic");
        o16x1s1logic = wire(1, "o16x1s1logic");
        o32x1dlogic = wire(2, "o32x1dlogic");
        o32x1d1logic = wire(2, "o32x1d1logic");
        o32x1slogic = wire(1, "o32x1slogic");
        o32x1s1logic = wire(1, "o32x1s1logic");
        o64x1dlogic = wire(2, "o64x1dlogic");
        o64x1d1logic = wire(2, "o64x1d1logic");
        o64x1slogic = wire(1, "o64x1slogic");
        o64x1s1logic = wire(1, "o64x1s1logic");
        o64x2slogic = wire(2, "o64x2slogic");
        o128x1slogic = wire(1, "o128x1slogic");
        o128x1s1logic = wire(1, "o128x1s1logic");

        jhdllogicramtest logicram = new jhdllogicramtest(this, indatalogic, welogic, clk, walogic, ralogic,
                o16x1dlogic, o16x1d1logic, o16x1slogic, o16x1s1logic, o32x1dlogic, o32x1d1logic, o32x1slogic,
                o32x1s1logic, o64x1dlogic, o64x1d1logic, o64x1slogic, o64x1s1logic, o64x2slogic, o128x1slogic,
                o128x1s1logic);
    }

    /**
     * functions to be performed on each clock transition
     */
    public void clock() {
        setInputs();
        if (_count > 0)
            getResults();
        _count++;
    }

    /**
     * grab the integer version of the outputs
     */
    public void getResults() {
        _o16x1djhdl = o16x1djhdl.get(this);
        _o16x1d1jhdl = o16x1d1jhdl.get(this);
        _o16x1sjhdl = o16x1sjhdl.get(this);
        _o16x1s1jhdl = o16x1s1jhdl.get(this);
        _o32x1djhdl = o32x1djhdl.get(this);
        _o32x1d1jhdl = o32x1d1jhdl.get(this);
        _o32x1sjhdl = o32x1sjhdl.get(this);
        _o32x1s1jhdl = o32x1s1jhdl.get(this);
        _o64x1djhdl = o64x1djhdl.get(this);
        _o64x1d1jhdl = o64x1d1jhdl.get(this);
        _o64x1sjhdl = o64x1sjhdl.get(this);
        _o64x1s1jhdl = o64x1s1jhdl.get(this);
        _o64x2sjhdl = o64x2sjhdl.get(this);
        _o128x1sjhdl = o128x1sjhdl.get(this);
        _o128x1s1jhdl = o128x1s1jhdl.get(this);

        _o16x1dlogic = o16x1dlogic.get(this);
        _o16x1d1logic = o16x1d1logic.get(this);
        _o16x1slogic = o16x1slogic.get(this);
        _o16x1s1logic = o16x1s1logic.get(this);
        _o32x1dlogic = o32x1dlogic.get(this);
        _o32x1d1logic = o32x1d1logic.get(this);
        _o32x1slogic = o32x1slogic.get(this);
        _o32x1s1logic = o32x1s1logic.get(this);
        _o64x1dlogic = o64x1dlogic.get(this);
        _o64x1d1logic = o64x1d1logic.get(this);
        _o64x1slogic = o64x1slogic.get(this);
        _o64x1s1logic = o64x1s1logic.get(this);
        _o64x2slogic = o64x2slogic.get(this);
        _o128x1slogic = o128x1slogic.get(this);
        _o128x1s1logic = o128x1s1logic.get(this);

    }

    /**
     * default startup conditions
     */
    public void reset() {
        indatajhdl.put(this, 0);
        wejhdl.put(this, 0);
        wajhdl.put(this, 0);
        rajhdl.put(this, 0);

        indatalogic.put(this, 0);
        welogic.put(this, 0);
        walogic.put(this, 0);
        ralogic.put(this, 0);

        _count = 0;
        _rand = new Random();
    }

    /**
     * drive the inputs of the design
     */
    public void setInputs() {
        _indata = (_count % 2 == 1) ? _rand.nextInt(1 << 8) : _indata;
        _ra = (_count % 2 == 1) ? _rand.nextInt(1 << 7) : _ra;
        _wa = (_count % 2 == 1) ? _rand.nextInt(1 << 7) : _wa;
        _we = (_count % 2 == 1) ? _rand.nextInt(2) : _we;

        indatajhdl.put(this, _indata);
        wejhdl.put(this, _we);
        wajhdl.put(this, _wa);
        rajhdl.put(this, _ra);

        indatalogic.put(this, _indata);
        welogic.put(this, _we);
        walogic.put(this, _wa);
        ralogic.put(this, _ra);

    }

    public static void main(String argv[]) throws Exception {

        HWSystem hw = new HWSystem();
        tb_jhdlRAM tb = new tb_jhdlRAM(hw);

        int cnt = 0;
        int MAX = 100;
        String file = "results.dat";

        //_outfile = new PrintStream(new FileWriter(file));
        _outfile = new PrintStream(System.out);

        _outfile.print("INDATA\tWA\tRA\tWE\tr16x1d\tr16x1d1\tr16x1s\tr16x1s1\t");
        _outfile.print("r32x1d\tr32x1d1\tr32x1s\tr32x1s1\tr64x1d\tr64x1d1\t");
        _outfile.println("r64x1s\tr64x1s1\tr64x2s\tr128x1s\tr128x1s1");
        _outfile.print("======\t==\t==\t==\t======\t=======\t======\t=======\t");
        _outfile.print("======\t=======\t======\t=======\t======\t=======\t");
        _outfile.println("======\t=======\t======\t=======\t========");

        while (cnt < MAX) {
            _outfile.print(_indata + "\t" + _wa + "\t" + _ra + "\t" + _we + "\t");
            _outfile.print(_o16x1djhdl + " " + _o16x1dlogic + "\t");
            _outfile.print(_o16x1d1jhdl + " " + _o16x1d1logic + "\t");
            _outfile.print(_o16x1sjhdl + " " + _o16x1slogic + "\t");
            _outfile.print(_o16x1s1jhdl + " " + _o16x1s1logic + "\t");
            _outfile.print(_o32x1djhdl + " " + _o32x1dlogic + "\t");
            _outfile.print(_o32x1d1jhdl + " " + _o32x1d1logic + "\t");
            _outfile.print(_o32x1sjhdl + " " + _o32x1slogic + "\t");
            _outfile.print(_o32x1s1jhdl + " " + _o32x1s1logic + "\t");
            _outfile.print(_o64x1djhdl + " " + _o64x1dlogic + "\t");
            _outfile.print(_o64x1d1jhdl + " " + _o64x1d1logic + "\t");
            _outfile.print(_o64x1sjhdl + " " + _o64x1slogic + "\t");
            _outfile.print(_o64x1s1jhdl + " " + _o64x1s1logic + "\t");
            _outfile.print(_o64x2sjhdl + " " + _o64x2slogic + "\t");
            _outfile.print(_o128x1sjhdl + " " + _o128x1slogic + "\t");
            _outfile.println(_o128x1s1jhdl + " " + _o128x1s1logic);

            /*
             * if (_o16x1dlogic != _o16x1djhdl) { _outfile.print("o16x1d "); }
             * if (_o16x1d1logic != _o16x1d1jhdl) { _outfile.print("o16x1d1 "); }
             * if (_o16x1slogic != _o16x1sjhdl) { _outfile.print("o16x1s "); }
             * if (_o16x1s1logic != _o16x1s1jhdl) { _outfile.print("o16x1s1 "); }
             * if (_o32x1dlogic != _o32x1djhdl) { _outfile.print("o32x1d "); }
             * if (_o32x1d1logic != _o32x1d1jhdl) { _outfile.print("o32x1d1 "); }
             * if (_o32x1slogic != _o32x1sjhdl) { _outfile.print("o32x1s "); }
             * if (_o32x1s1logic != _o32x1s1jhdl) { _outfile.print("o32x1s1 "); }
             * if (_o64x1dlogic != _o64x1djhdl) { _outfile.print("o64x1d "); }
             * if (_o64x1d1logic != _o64x1d1jhdl) { _outfile.print("o64x1d1 "); }
             * if (_o64x1slogic != _o64x1sjhdl) { _outfile.print("o64x1s "); }
             * if (_o64x1s1logic != _o64x1s1jhdl) { _outfile.print("o64x1s1 "); }
             * if (_o64x2slogic != _o64x2sjhdl) { _outfile.print("o64x2s "); }
             * if (_o128x1slogic != _o128x1sjhdl) { _outfile.print("o128x1s "); }
             * if (_o128x1s1logic != _o128x1s1jhdl) { _outfile.print("o128x1s1
             * "); }
             */
            _outfile.println();
            cnt++;
            hw.cycle(1);
        }
    }

}
