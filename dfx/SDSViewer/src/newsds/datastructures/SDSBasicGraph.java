/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package newsds.datastructures;

import helper.StringList;

/**
 *
 * @author vvasilev
 */
public class SDSBasicGraph {

    protected SDSNode _node;

    public SDSBasicGraph() {
        _node = new SDSNode();
        _node.resetCost();
    }

    public SDSNode getNode() {
        return _node;
    }
}
