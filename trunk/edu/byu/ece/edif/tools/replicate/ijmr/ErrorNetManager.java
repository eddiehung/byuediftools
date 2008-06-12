/*
 * This class is responsible for managing the nets that are outputs of
 * comparators used for error checking in a design with duplication with
 * compare (DWC). The nets are managed according to comparator type
 * (persistent or non-persistent for now) and rail (either a single rail
 * or two rails for dual-rail comparators).
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
package edu.byu.ece.edif.tools.replicate.ijmr;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.byu.ece.edif.core.EdifNet;

/**
 * The purpose of this class is to manage all of the comparator output nets
 * created when inserting comparators while applying duplication for error
 * detection (i.e. in TMRDWCEdifCell).
 * 
 * The class will manage error nets based on two criteria:
 * <pre>
 *  1. type (i.e. persistent or non-persistent)
 *  2. rail (single, 0, or 1)
 * </pre>
 * 
 * The data structure is as follows:
 * Map&lt;DWCComparatorType, Map&lt;DWCRailType, List&lt;EdifNet&gt;&gt;&gt;
 */
public class ErrorNetManager {

	/**
	 * Create an empty ErrorNetManager
	 */
	public ErrorNetManager() {
		_errorNetMap = new LinkedHashMap<DWCComparatorType, Map<DWCRailType, List<EdifNet>>>();
		_typeStringMap = new LinkedHashMap<DWCComparatorType, String>();
		_railSuffixMap = new LinkedHashMap<DWCRailType, String>();
	}

	/**
	 * Add a net to the error net manager and categorize it according to
	 * comparator type and rail.
	 * 
	 * @param type comparator type
	 * @param rail comparator rail
	 * @param net net to add
	 */
	public void addNet(DWCComparatorType type, DWCRailType rail, EdifNet net) {
		List<EdifNet> netList = getNetList(type, rail);
		netList.add(net);
	}
	
	/**
	 * Set the string used for a particular comparator type. This can be
	 * accessed later.
	 * 
	 * @param type the comparator type to set a string for
	 * @param string the string to set
	 */
	public void setComparatorString(DWCComparatorType type, String string) {
		_typeStringMap.put(type, string);
	}
	
	/**
	 * Set the suffix used for a particular rail type. This can be accessed
	 * later.
	 * 
	 * @param rail the rail type to set a suffix for
	 * @param suffix the suffix to set
	 */
	public void setRailSuffix(DWCRailType rail, String suffix) {
		_railSuffixMap.put(rail, suffix);
	}
	
	/**
	 * Get the comparator string for the given comparator type
	 * 
	 * @param type the comparator type
	 * @return the string
	 */
	public String getComparatorString(DWCComparatorType type) {
		return _typeStringMap.get(type);
	}
	
	/**
	 * Get the rail suffix for the given rail type
	 * 
	 * @param rail the rail type
	 * @return the suffix
	 */
	public String getRailSuffix(DWCRailType rail) {
		return _railSuffixMap.get(rail);
	}
	
	/**
	 * Get the mapping from rail type to List&lt;EdifNet&gt; for the given
	 * comparator type
	 * 
	 * @param type the comparator type
	 * @return the mapping from rail type to List&lt;EdifNet&gt;
	 */
	protected Map<DWCRailType, List<EdifNet>> getRailMap(DWCComparatorType type) {
		Map<DWCRailType, List<EdifNet>> railMap = _errorNetMap.get(type);
		if (railMap == null) {
			railMap = new LinkedHashMap<DWCRailType, List<EdifNet>>();
			_errorNetMap.put(type, railMap);
		}
		return railMap;		
	}
	
	/**
	 * Tet the list of nets that match the given comparator type and
	 * rail type combination.
	 * 
	 * @param type comparator type
	 * @param rail rail type
	 * @return list of nets
	 */
	public List<EdifNet> getNetList(DWCComparatorType type, DWCRailType rail) {
		Map<DWCRailType, List<EdifNet>> railMap = getRailMap(type);
		List<EdifNet> netList = railMap.get(rail);
		if (netList == null) {
			netList = new ArrayList<EdifNet>();
			railMap.put(rail, netList);
		}
		return netList;
	}
	
	/**
	 * Storage structure for nets
	 */
	protected Map<DWCComparatorType, Map<DWCRailType, List<EdifNet>>> _errorNetMap;

	/**
	 * Mapping from comparator types to comparator strings (used for names in
	 * generation of EDIF)
	 */
	protected Map<DWCComparatorType, String> _typeStringMap;
	
	/**
	 * Mapping from rail types to rail suffixes (used for names in generation
	 * of EDIF)
	 */
	protected Map<DWCRailType, String> _railSuffixMap;
}

