/*
 * A StringParser for parsing Integers with optional minimum and maximum values.
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
package edu.byu.ece.edif.util.jsap;

import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;
import com.martiansoftware.jsap.stringparsers.IntegerStringParser;

/**
 * A {@link com.martiansoftware.jsap.StringParser} for parsing Integers with
 * optional minimum and maximum values. The {@link #parse(String)} method
 * delegates the actual parsing to <code>new Integer(String)</code>. If
 * <code>new Integer(String)</code> throws a {@link NumberFormatException},
 * it is encapsulated in a {@link ParseException} and re-thrown. Also, if the
 * parameter is lower than the minimum or higher than the maximum, a
 * <code>ParseException</code> is thrown.
 * <p>
 * The default minimum limit is {@link Integer#MIN_VALUE} and the default
 * maximum limit is {@link Integer#MAX_VALUE}.
 * 
 * @author <a href="mailto:jcarroll@byu.net">James Carroll</a>, Brigham Young
 * University
 * @see com.martiansoftware.jsap.StringParser
 * @see IntegerStringParser
 * @see java.lang.Integer
 */
public class BoundedIntegerStringParser extends IntegerStringParser {

    /**
     * Returns a {@link BoundedIntegerStringParser} with the specified minimum
     * and maximum values. Also, the minimum and maximum can each optionally be
     * inclusive or non-inclusive.
     * 
     * @param min Minimum acceptable value. If null, the minimum will be set to
     * Integer.MIN_VALUE
     * @param max Maximum acceptable value. If null, the maximum will be set to
     * Integer.MAX_VALUE
     * @param includeMax Determines if max should be considered part of the
     * valid range. If true, the valid range includes max. If null, includeMax
     * will default to true.
     * @throws IllegalArgumentException if min is greater than max.
     * @deprecated Use {@link #getParser(Integer, Integer, Boolean, Boolean)}.
     */
    @Deprecated
    public BoundedIntegerStringParser(Integer min, Integer max, Boolean includeMin, Boolean includeMax)
            throws IllegalArgumentException {

        // Set min
        if (min == null)
            this.min = Integer.MIN_VALUE;
        else
            this.min = min;

        // Set max
        if (max == null)
            this.max = Integer.MAX_VALUE;
        else
            this.max = max;

        // Ensure min < max
        if (this.min > this.max) {
            throw new IllegalArgumentException("Invalid limits. Minimum " + min + " is larger than the maximum " + max);
        }

        // Set lower bound inclusive / non-inclusive
        if (includeMin == null)
            this.includeMin = true;
        else
            this.includeMin = includeMin;

        // Set upper bound inclusive / non-inclusive
        if (includeMax == null)
            this.includeMax = true;
        else
            this.includeMax = includeMax;

        // Create the text representation of the range
        this.range = (this.includeMin ? "[" : "(") + this.min + "," + this.max + (this.includeMax ? "]" : ")");
    }

    /**
     * @param min Minimum acceptable value. If null, the minimum will be set to
     * Integer.MIN_VALUE
     * @param max Maximum acceptable value. If null, the maximum will be set to
     * Integer.MAX_VALUE
     * @param includeMin Determines if min should be considered part of the
     * valid range. If true, the valid range includes min. If null, includeMin
     * will default to true.
     * @param includeMax Determines if max should be considered part of the
     * valid range. If true, the valid range includes max. If null, includeMax
     * will default to true.
     * @return A BoundedIntegerStringParser with the specified lower and upper
     * bounds and specified inclusion or non-inclusion for each of the bounds.
     * @throws IllegalArgumentException if min is greater than max.
     */
    public static BoundedIntegerStringParser getParser(Integer min, Integer max, Boolean includeMin, Boolean includeMax)
            throws IllegalArgumentException {
        return new BoundedIntegerStringParser(min, max, includeMin, includeMax);
    }

    /**
     * @param min Minimum acceptable value. If null, the minimum will be set to
     * Integer.MIN_VALUE
     * @param max Maximum acceptable value. If null, the maximum will be set to
     * Integer.MAX_VALUE
     * @return A BoundedIntegerStringParser with the specified lower and upper
     * bounds, inclusive.
     * @throws IllegalArgumentException if min is greater than max.
     */
    public static BoundedIntegerStringParser getParser(Integer min, Integer max) throws IllegalArgumentException {
        return getParser(min, max, null, null);
    }

    /**
     * @throws IllegalArgumentException Only if getParser(Integer, Integer,
     * Boolean, Boolean) does so.
     * @return A BoundedIntegerStringParser with no lower or upper limit (other
     * than Integer.MIN_VALUE and Integer.MAX_VALUE). This should behave the
     * same as {@link com.martiansoftware.jsap.JSAP#INTEGER_PARSER} and is
     * provided for complete flexibility.
     * @see com.martiansoftware.jsap.stringparsers.IntegerStringParser#getParser()
     */
    public static BoundedIntegerStringParser getParser() throws IllegalArgumentException {
        return getParser(null, null, null, null);
    }

    /**
     * @return A String representation of the range, as illustrated in
     * {@link #range}.
     */
    public String getRange() {
        return this.range;
    }

    /**
     * Parses the specified argument into a Integer. This method delegates the
     * actual parsing to <code>new Integer(String)</code>. If
     * <code>new Integer(String)</code> throws a
     * <code>NumberFormatException</code>, it is encapsulated in a
     * <code>ParseException</code> and re-thrown. Also ensures that the value
     * specified is in the range for this parser.<br>
     * Overrides {@link IntegerStringParser#parse(String)}
     * 
     * @param arg the argument to parse
     * @return a Integer object with the value contained in the specified
     * argument.
     * @throws ParseException if the specified value is outside the valid range
     * or if <code>new Integer(arg)</code> throws a NumberFormatException.
     * @see java.lang.Integer
     * @see StringParser#parse(String)
     */
    @Override
    public Object parse(String arg) throws ParseException {
        Integer result = null;

        // Convert String to Integer
        try {
            result = new Integer(arg);
        } catch (NumberFormatException e) {
            throw (new ParseException("Unable to convert '" + arg + "' to a Integer.", e));
        }

        // Check lower bound
        if (result < min || (!includeMin && result <= min))
            throw new ParseException(arg + " is out of range: " + getRange());

        // Check upper bound
        if (result > max || (!includeMax && result >= max))
            throw new ParseException(arg + " is out of range: " + getRange());

        return (result);
    }

    /**
     * If true, the valid range includes the minimum value.
     */
    protected Boolean includeMin = true;

    /**
     * If true, the valid range includes the maximum value.
     */
    protected Boolean includeMax = true;

    /**
     * Minimum acceptable value.
     */
    protected Integer min = Integer.MIN_VALUE;

    /**
     * Maximum acceptable value.
     */
    protected Integer max = Integer.MAX_VALUE;

    /**
     * <p>
     * Text representation of the range.
     * <p>
     * Examples:
     * <dl>
     * <dt>[0,1]
     * <dd> Zero to one, inclusive
     * <dt>(-100,100)
     * <dd> -100 to 100, non-inclusive
     * <dt>[3.14,6.28)
     * <dd> 3.14 to 6.28, including 3.14 and not including 6.28
     * </dl>
     */
    protected String range;
}
