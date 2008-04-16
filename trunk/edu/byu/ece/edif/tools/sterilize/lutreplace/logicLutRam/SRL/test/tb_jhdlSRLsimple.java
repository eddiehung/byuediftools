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
 * Testbench used to compare a single logic implementation of an 
 * SRL16 with a single Xilinx SRL16 primitive.
 *
 *  @author Nathan Rollins
 *  @version $Id:tb_jhdlSRLsimple.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */

public class tb_jhdlSRLsimple extends Logic implements TestBench {

    static PrintStream outfile;
    
    static int _count;
    static int _a;
    static int _ce;
    static int _indata;

    static int _osrl16jhdl;
    static int _osrl16logic;
    
    static  Random _rand;

    Wire a;
    Wire ce;
    Wire clk;
    Wire indata;
    Wire osrl16jhdl;
    Wire osrl16logic;

    public tb_jhdlSRLsimple(Node parent) {
        super(parent);

        a = wire(4, "a");
        ce = wire(1, "ce");
        clk = wire(1, "clk");
        indata = wire(1, "indata");
        osrl16jhdl = wire(1, "osrl16jhdl");
        osrl16logic = wire(1, "osrl16logic");

        clockDriver(clk, "01");
        new jhdlsrltestsimple(this,
                              indata,
                              ce,
                              clk,
                              a,
                              osrl16jhdl,
                              osrl16logic);
    }

    public void clock() {
        setInputs();
        if (_count > 0)
            getResults();
        _count++;
    }

    public void getResults() {
        _osrl16jhdl = osrl16jhdl.get(this);
        _osrl16logic = osrl16logic.get(this);
    }

    public void reset() {
        indata.put(this, 0);
        ce.put(this, 0);
        a.put(this, 0);
        
        _count = 0;
        _rand = new Random();
    }
    
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

        //outfile = new PrintStream(new FileWriter(file));
        outfile = new PrintStream(System.out);
        
        outfile.print("INDATA\tA\tCE\tsrl16\tsrl161\tsrl16e\tsrl16e1\t");
        outfile.println("srlc16\tsrlc161\tsrlc16e\tsrlc16e1");
        outfile.print("======\t=\t==\t=====\t======\t======\t=======\t");
        outfile.println("======\t=======\t=======\t========");

        while (cnt < MAX) {
            outfile.print(_indata+"\t"+_a+"\t"+_ce+"\t");
            outfile.print(_osrl16jhdl+" "+_osrl16logic+"\t");
            outfile.println();
            cnt++;
            hw.cycle(1);
        }
    }

}









