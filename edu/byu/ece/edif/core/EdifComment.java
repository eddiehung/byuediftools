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
package edu.byu.ece.edif.core;

import java.util.ArrayList;
import java.util.List;

public class EdifComment implements EdifOut {

    public EdifComment() {
        _comments = new ArrayList();
    }

    public EdifComment(List commentLines) {
        this();
        _comments.addAll(commentLines);
    }

    public EdifComment(String commentLine) {
        this();
        addCommentLine(commentLine);
    }

    public void addCommentLine(String line) {
        _comments.add(line);
    }

    public void toEdif(EdifPrintWriter epw) {
        if (_comments.size() == 0)
            return;
        epw.printIndent("(comment \"");
        if (_comments.size() == 1) {
            epw.print(getLine(0));
        } else {
            for (int i = 0; i < _comments.size() - 1; i++)
                epw.println(getLine(i));
            epw.print(getLine(_comments.size() - 1));
        }
        epw.println("\")");
    }

    public String getLine(int i) {
        if (i < _comments.size())
            return (String) _comments.get(i);
        return null;
    }

    /**
     * A list of String objects that represent the comment.
     */
    protected List _comments;

}
