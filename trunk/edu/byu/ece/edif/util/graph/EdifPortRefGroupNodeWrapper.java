package edu.byu.ece.edif.util.graph;

import java.util.List;

public class EdifPortRefGroupNodeWrapper {
    public EdifPortRefGroupNodeWrapper(EdifPortRefGroupNode node) {
        _node = node;
        _nodes = null;
        _isSingleNode = true;
    }
    public EdifPortRefGroupNodeWrapper(List<EdifPortRefGroupNode> nodes) {
        _nodes = nodes;
        _node = null;
        _isSingleNode = false;
    }
    public boolean isSingleNode() {
        return _isSingleNode;
    }
    public EdifPortRefGroupNode getNode() {
        return _node;
    }
    public List<EdifPortRefGroupNode> getNodes() {
        return _nodes;
    }
    
    private EdifPortRefGroupNode _node;
    private List<EdifPortRefGroupNode> _nodes;
    private boolean _isSingleNode;
}
