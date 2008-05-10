/*
 * Basic Edge implementation, for the creation of simple, on-the-fly graphs.
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
package edu.byu.ece.graph;

/**
 * The most basic implementation of Edge. This class allows simple on-the-fly
 * graphs to be created.
 * 
 * @author nhr2
 */
public class BasicEdge implements Edge {

    /**
     * Main Constructor.
     * 
     * @param src - the source of the edge
     * @param sink - the sink of the edge
     */
    public BasicEdge(Object src, Object sink) {
        super();

        _src = src;
        _sink = sink;
    }

    /**
     * @return - the edge's source node
     */
    public Object getSource() {
        return _src;
    }

    /**
     * @return - the edge's sink node
     */
    public Object getSink() {
        return _sink;
    }

    /**
     * @return - the inverted form of this edge
     */
    public Edge invert() {
        return new BasicEdge(_sink, _src);
    }

    public String toString() {

        //		StringBuffer sb = new StringBuffer();
        //
        //        sb.append("SRC: " + _src + "\n");
        //        sb.append("SINK: "+_sink+"\n");
        //		
        //		return sb.toString();

        return _src.toString() + "->" + _sink.toString();
    }

    /**
     * The edge's source node.
     */
    Object _src;

    /**
     * The edge's sink node.
     */
    Object _sink;
}
