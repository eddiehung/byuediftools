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
package edu.byu.ece.edif.test;

public class ExampleTest extends AbstractTestClass {

    public ExampleTest(TestSuite suite) {
        super(suite);
    }

    /**
     * This method is run by the TestSuite class before each test method. It can
     * be used to setup a common test infrastructure for the test methods in the
     * class.
     */
    public void setup() {
        _testString0 = "abcdefg";
        _testString1 = "ABCDEFG";
        _testBoolean0 = true;
        _testBoolean1 = false;
    }

    /**
     * This test should pass
     */
    @Test
    public void example_1() {
        assertEqual(_testString0, _testString1.toLowerCase());
    }

    /**
     * This test should pass
     */
    @Test
    public void example_2() {
        assertUnequal(_testString0, _testString1);
    }

    /**
     * This test should pass
     */
    @Test
    public void example_3() {
        assertTrue(_testBoolean0);
    }

    /**
     * This test should pass
     */
    @Test
    public void example_4() {
        assertFalse(_testBoolean1);
    }

    /**
     * This test demonstrates exception catching behavior
     */
    @Test
    public void example_5() {
        throw new RuntimeException("something went wrong!");
    }

    /**
     * This test should fail
     */
    @Test
    public void example_6() {
        assertEqual(4, 5);
    }

    private String _testString0;

    private String _testString1;

    private boolean _testBoolean0;

    private boolean _testBoolean1;

}
