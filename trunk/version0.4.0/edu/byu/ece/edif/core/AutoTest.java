/*
 * Designed for auto testing the functionality of the whole EDIF package.
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
package edu.byu.ece.edif.core;

/**
 * Designed for auto testing the functionality of the whole EDIF package.
 * <ol>
 * <li>What should be auto tested?<br>
 * Ideally, it should be all the classes and all the methods inside this
 * package.
 * <li>Where to start with? What is the input?<br>
 * We should have several standard benchmarking circuits as the input
 * <li>How can we do the test?<br>
 * <ol>
 * <li> Parsing the test circuit, generate one {@link EdifEnvironment} for this
 * circuit. This tests the edu.byu.ece.edif.util.parse.EdifParser and also the
 * construction of all the edu.byu.ece.edif.core classes.
 * <li> Generate the EDIF output for the newly created {@link EdifEnvironment}
 * object.
 * <li> Parsing the generated EDIF file, generate another
 * {@link EdifEnvironment}.
 * <li> Compare the two {@link EdifEnvironment} Objects
 * <li> Create the JHDL class from the {@link EdifEnvironment} object
 * <li> Simulate the circuit automatically with standard input vector, and
 * compare the results with anticipated results.
 * <li> According to the API, test each method one by one. If it is not
 * automatically testable, print out some warning message.
 * </ol>
 * </ol>
 * 
 * @version $Id:AutoTest.java 198 2008-04-16 21:14:21Z jamesfcarroll $
 */
public class AutoTest {

}
