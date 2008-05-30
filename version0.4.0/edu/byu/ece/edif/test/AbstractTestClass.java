/*
 * An abstract test class.
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
package edu.byu.ece.edif.test;

//////////////////////////////////////////////////////////////////////////
//// AbstractTestClass
/**
 * An abstract test class. Test classes should extend this class. The overridden
 * constructor should call super(). Any public methods annotated with
 * 
 * @Test will be run by the TestSuite.
 * @author Jonathan Johnson
 * @version $Id$
 */
public class AbstractTestClass {

    public AbstractTestClass(TestSuite suite) {
        _testSuite = suite;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         protected methods                 ////

    /**
     * Add an error if the passed in objects are not equal by the
     * <code>==</code> operator.
     * 
     * @param obj1 first object to compare
     * @param obj2 second object to compare
     */
    protected void assertEqual(Object obj1, Object obj2) {
        _testSuite.assertEqual(obj1, obj2);
    }

    /**
     * Add an error if the passed in boolean is not <code>false</code>.
     * 
     * @param condition the boolean to test
     */
    protected void assertFalse(boolean condition) {
        _testSuite.assertFalse(condition);
    }

    /**
     * Add an error if the passed in boolean is not <code>true</code>.
     * 
     * @param condition the boolean to test
     */
    protected void assertTrue(boolean condition) {
        _testSuite.assertTrue(condition);
    }

    /**
     * Add an error if the passed in objects are not equal by the
     * <code>==</code> operator.
     * 
     * @param obj1 first object to compare
     * @param obj2 second object to compare
     */
    protected void assertUnequal(Object obj1, Object obj2) {
        _testSuite.assertUnequal(obj1, obj2);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /**
     * The TestSuite that is running the tests in an extended class.
     */
    TestSuite _testSuite;
}
