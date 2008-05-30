/*
 * Represents the result of a test case.
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
//// TestResult
/**
 * Represents the result of a test case. Contains the test class name, the test
 * method name, and the test status.
 * 
 * @author Jonathan Johnson
 * @version $Id$
 */

public class TestResult {

    /**
     * Construct a new TestResult.
     * 
     * @param className name of the test class
     * @param methodName name of the test method
     * @param testStatus <code>true</code> if the test passed or
     * <code>false</code> if it failed.
     */
    public TestResult(String className, String methodName, TestStatus testStatus) {
        _className = className;
        _methodName = methodName;
        _testStatus = testStatus;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * @return the name of the test class
     */
    public String getClassName() {
        return _className;
    }

    /**
     * @return the name of the test method
     */
    public String getMethodName() {
        return _methodName;
    }

    /**
     * @return <code>true</code> if the test passed or <code>false</code> if
     * it failed.
     */
    public boolean getPassed() {
        if (_testStatus == TestStatus.PASSED)
            return true;
        return false;
    }

    /**
     * @return a String representation of the test result.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(_className);
        sb.append(", ");
        sb.append(_methodName);
        sb.append(", ");
        switch (_testStatus) {
        case PASSED:
            sb.append("PASSED");
            break;
        case FAILED:
            sb.append("FAILED");
            break;
        case UNTESTED:
            sb.append("UNTESTED");
            break;
        case EXCEPTION:
            sb.append("EXCEPTION");
        }
        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /**
     * Name of the test class
     */
    private String _className;

    /**
     * Name of the method from which the test was run
     */
    private String _methodName;

    /**
     * Specifies whether or not the test case passed
     */
    private TestStatus _testStatus;
}
