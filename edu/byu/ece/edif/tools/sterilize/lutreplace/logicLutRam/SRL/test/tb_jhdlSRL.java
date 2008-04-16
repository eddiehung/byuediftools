/*
 * TODO: Insert class description here.
 *
 *

 * Copyright (c) 2008 Brigham Young University
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
package edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.SRL.test;
/*

*/

import byucc.jhdl.base.*;
import byucc.jhdl.Logic.*;

import java.io.*;
import java.util.*;

import edu.byu.ece.edif.tools.sterilize.lutreplace.logicLutRam.SRL.*;

/**
 *
 * Testbench used to compare logic SRL16 implementation with JHDL XIlinx
 * SRL16 primitives.
 *
 *  @author Nathan Rollins
 *  @version $Id:tb_jhdlSRL.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class tb_jhdlSRL extends Logic implements TestBench {

    // input values to SRL16s as integers
    static int _count;
    static int _a;
    static int _ce;
    static int _indata;

    // SRL16 output values as integers
    static int _osrl16jhdl;
    static int _osrl161jhdl;
    static int _osrl16ejhdl;
    static int _osrl16e1jhdl;
    static int _osrlc16jhdl;
    static int _osrlc161jhdl;
    static int _osrlc16ejhdl;
    static int _osrlc16e1jhdl;

    static int _osrl16logic;
    static int _osrl161logic;
    static int _osrl16elogic;
    static int _osrl16e1logic;
    static int _osrlc16logic;
    static int _osrlc161logic;
    static int _osrlc16elogic;
    static int _osrlc16e1logic;
    
    // Stream to output results to
    static PrintStream _outfile;
    
    // random number generator
    static  Random _rand;

    // wires to connect design
    Wire a;
    Wire ce;
    Wire clk;
    Wire indata;
    Wire osrl16jhdl;
    Wire osrl161jhdl;
    Wire osrl16ejhdl;
    Wire osrl16e1jhdl;
    Wire osrlc16jhdl;
    Wire osrlc161jhdl;
    Wire osrlc16ejhdl;
    Wire osrlc16e1jhdl;
    Wire osrl16logic;
    Wire osrl161logic;
    Wire osrl16elogic;
    Wire osrl16e1logic;
    Wire osrlc16logic;
    Wire osrlc161logic;
    Wire osrlc16elogic;
    Wire osrlc16e1logic;

    /**
     * Default constructor
     *
     * @param parent parent node
     */
    public tb_jhdlSRL(Node parent) {
        super(parent);

        a = wire(4, "a");
        ce = wire(1, "ce");
        clk = wire(1, "clk");
        indata = wire(1, "indata");
        osrl16jhdl = wire(1, "osrl16jhdl");
        osrl161jhdl = wire(1, "osrl161jhdl");
        osrl16ejhdl = wire(1, "osrl16ejhdl");
        osrl16e1jhdl = wire(1, "osrl16e1jhdl");
        osrlc16jhdl = wire(2, "osrlc16jhdl");
        osrlc161jhdl = wire(2, "osrlc161jhdl");
        osrlc16ejhdl = wire(2, "osrlc16ejhdl");
        osrlc16e1jhdl = wire(2, "osrlc16e1jhdl");
        osrl16logic = wire(1, "osrl16logic");
        osrl161logic = wire(1, "osrl161logic");
        osrl16elogic = wire(1, "osrl16elogic");
        osrl16e1logic = wire(1, "osrl16e1logic");
        osrlc16logic = wire(2, "osrlc16logic");
        osrlc161logic = wire(2, "osrlc161logic");
        osrlc16elogic = wire(2, "osrlc16elogic");
        osrlc16e1logic = wire(2, "osrlc16e1logic");

        clockDriver(clk, "01");
        new allsrltest(this,
                       indata,
                       ce,
                       clk,
                       a,
                       osrl16jhdl,
                       osrl161jhdl,
                       osrl16ejhdl,
                       osrl16e1jhdl,
                       osrlc16jhdl,
                       osrlc161jhdl,
                       osrlc16ejhdl,
                       osrlc16e1jhdl);
        new jhdlsrltest(this,
                        indata,
                        ce, 
                        clk,
                        a,
                        osrl16logic,
                        osrl161logic,
                        osrl16elogic,
                        osrl16e1logic,
                        osrlc16logic,
                        osrlc161logic,
                        osrlc16elogic,
                        osrlc16e1logic);
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
        _osrl16jhdl = osrl16jhdl.get(this);
        _osrl161jhdl = osrl161jhdl.get(this);
        _osrl16ejhdl = osrl16ejhdl.get(this);
        _osrl16e1jhdl = osrl16e1jhdl.get(this);
        _osrlc16jhdl = osrlc16jhdl.get(this);
        _osrlc161jhdl = osrlc161jhdl.get(this);
        _osrlc16ejhdl = osrlc16ejhdl.get(this);
        _osrlc16e1jhdl = osrlc16e1jhdl.get(this);

        _osrl16logic = osrl16logic.get(this);
        _osrl161logic = osrl161logic.get(this);
        _osrl16elogic = osrl16elogic.get(this);
        _osrl16e1logic = osrl16e1logic.get(this);
        _osrlc16logic = osrlc16logic.get(this);
        _osrlc161logic = osrlc161logic.get(this);
        _osrlc16elogic = osrlc16elogic.get(this);
        _osrlc16e1logic = osrlc16e1logic.get(this);
        
    }

    /**
     * default startup conditions
     */    
    public void reset() {
        indata.put(this, 0);
        ce.put(this, 0);
        a.put(this, 0);
        
        _count = 0;
        _rand = new Random();
    }

    /**
     * drive the inputs of the design
     */
    public void setInputs() {
        _indata = (_count % 2 == 1) ? _rand.nextInt(2) : _indata;
        _a = (_count % 2 == 1) ? _rand.nextInt(1 << 7) : _a;
        _ce = (_count % 2 == 1) ? _rand.nextInt(2) : _ce;

        indata.put(this, _indata);
        ce.put(this, _ce);
        a.put(this, _a);
    }

    public static void main(String argv[]) throws Exception {
        
        HWSystem hw = new HWSystem();
        tb_jhdlSRL tb = new tb_jhdlSRL(hw);
        
        int cnt = 0;
        int MAX = 100;
        String file = "results.dat";

        //_outfile = new PrintStream(new FileWriter(file));
        _outfile = new PrintStream(System.out);
        
        _outfile.print("INDATA\tA\tCE\tsrl16\tsrl161\tsrl16e\tsrl16e1\t");
        _outfile.println("srlc16\tsrlc161\tsrlc16e\tsrlc16e1");
        _outfile.print("======\t=\t==\t=====\t======\t======\t=======\t");
        _outfile.println("======\t=======\t=======\t========");

        while (cnt < MAX) {
            _outfile.print(_indata+"\t"+_a+"\t"+_ce+"\t");
            _outfile.print(_osrl16jhdl+" "+_osrl16logic+"\t");
            _outfile.print(_osrl161jhdl+" "+_osrl161logic+"\t");
            _outfile.print(_osrl16ejhdl+" "+_osrl16elogic+"\t");
            _outfile.print(_osrl16e1jhdl+" "+_osrl16e1logic+"\t");
            _outfile.print(_osrlc16jhdl+" "+_osrlc16logic+"\t");
            _outfile.print(_osrlc161jhdl+" "+_osrlc161logic+"\t");
            _outfile.print(_osrlc16ejhdl+" "+_osrlc16elogic+"\t");
            _outfile.println(_osrlc16e1jhdl+" "+_osrlc16e1logic);
            _outfile.println();
            cnt++;
            hw.cycle(1);
        }
    }

}









