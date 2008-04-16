/*
 * Example of a test runner. See ExampleTest.java for more information.
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

//////////////////////////////////////////////////////////////////////////
//// TestRunner
/**
 * Example of a test runner. See ExampleTest.java for descriptions of what is
 * supposed to happen with each test.
 * 
 * @author Jonathan Johnson
 * @version $Id$
 */
public class ExampleTestRunner {

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Add all appropriate tests to a new TestSuite and print the test results.
     */
    public static void main(String[] args) {
        TestSuite suite = new TestSuite();

        suite.addTest(ExampleTest.class);

        Collection results = suite.run();

        for (Object i : results) {
            System.out.println((TestResult) i);
        }

        Map exceptions = suite.getExceptions();
        if (exceptions != null) {
            Iterator it = exceptions.keySet().iterator();
            System.out.println("\nThe following exceptions were thrown:");

            while (it.hasNext()) {
                String method = (String) it.next();
                String exception = (String) exceptions.get(method);
                System.out.println("\nThrown by method: " + method);
                System.out.println(exception);
            }
        }
    }
}
