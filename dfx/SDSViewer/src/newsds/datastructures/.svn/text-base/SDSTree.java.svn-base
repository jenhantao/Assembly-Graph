/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newsds.datastructures;

import helper.StringList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author vvasilev
 */
public class SDSTree extends SDSBasicGraph {

    private ArrayList<SDSTree> _children;
    private SDSTree _link;
    private SDSTree _parent;
    private ArrayList<SDSTree> _subtrees;
    private ArrayList<SDSTree> _instances;

    public SDSTree() {
        super();
        _children = new ArrayList<SDSTree>();
        _parent = null;
    }

    public SDSTree(StringList part) {
        this();
        _node.setPart(part);
    }

    public SDSTree(ArrayList<SDSTree> children) {
        super();
        _children = children;
        StringList part = new StringList();
        for (SDSTree child : _children) {
            child.setParent(this);
            part.addAll(child.getNode().getPart());
        }
        _node.setPart(part);
        _parent = null;
    }

//    public void substituteNode(String nodeName, SDSTree tree) {
//        if (_leftChild != null) {
//            if (_leftChild.toString().equals(nodeName)) {
//                _leftChild = tree;
//            }
//            _leftChild.substituteNode(nodeName, tree);
//        }
//        if (_rightChild != null) {
//            if (_rightChild.toString().equals(nodeName)) {
//                _rightChild = tree;
//            }
//            _rightChild.substituteNode(nodeName, tree);
//        }
//    }
    public void resetColorRec(SDSTree tree) {
        if (tree == null) {
            return;
        }
        tree.getNode().resetColor();
        ArrayList<SDSTree> children = tree.getChildren();
        for (SDSTree child : children) {
            resetColorRec(child);
        }
    }

    public void resetColorRec() {
        resetColorRec(this);
    }

    public SDSTree getParent() {
        return _parent;
    }

    public ArrayList<SDSTree> getChildren() {
        return _children;
    }

    public ArrayList<SDSTree> getInstances() {
        return _instances;
    }

    public void setParent(SDSTree parent) {
        _parent = parent;
    }

    public void setChild(int n, SDSTree child) {
        if (_children.size() == n) {
            _children.add(child);
            child.setParent(this);
        } else if (_children.size() > n) {
            _children.set(n, child);
            child.setParent(this);
        } else {
            System.err.println("Too large child index.");
        }
    }

//    @Override
//    public String toString() {
//        String left = "";
//        String right = "";
//        if (Debug.SHOW_TREE_STRUCTURE) {
//            if (_leftChild == null) {
//                left = "-";
//            } else {
//                left = _leftChild.toString();
//            }
//
//            if (_rightChild == null) {
//                right = "-";
//            } else {
//                right = _rightChild.toString();
//            }
//        }
//
//        if (left.equals("-") && right.equals("-")) {
//            String res = _node.getPart().toString();
//            if (Debug.SHOW_NODE_ID) {
//                res += "[" + _node.getId() + "]";
//            }
//            return res;
//        } else {
//            String res = "";//_node.getCompositePart().toString();
//            if (Debug.SHOW_NODE_ID) {
//                res += "[" + _node.getId() + "]";
//            }
//            if (Debug.SHOW_TREE_STRUCTURE) {
//                res += "(" + left + " + " + right + ")";
//            }
//            return res;
//        }
//    }
    @Override
    public boolean equals(Object o) {
        return (((SDSTree) o).getNode().getPart().toString().equals(this.getNode().getPart().toString()));
    }

    private void subtreesRec(SDSTree tree) {
        if (tree == null) {
            return;
        }
        _subtrees.add(tree);
        ArrayList<SDSTree> children = tree.getChildren();
        for (SDSTree child : children) {
            subtreesRec(child);
        }
    }

    public ArrayList<SDSTree> getSubtrees() {
        if (_subtrees != null) {
            return _subtrees;
        }
        _subtrees = new ArrayList<SDSTree>();
        subtreesRec(this);
        Collections.sort(_subtrees, new Comparator<SDSTree>() {

            @Override
            public int compare(SDSTree t1, SDSTree t2) {
                return t2.getNode().getPart().size() - t1.getNode().getPart().size();
            }
        });
        return _subtrees;
    }

    public boolean color2ab(String color) {
        return true;
    }

    public void copyAndLink(SDSTree tree) {
        if (tree == null) {
            return;
        }

        _link = tree;

        _node = new SDSNode();
        _node.setPart(tree.getNode().getPart());
        _node.setStages(tree.getNode().getStages());
        _node.setSteps(tree.getNode().getSteps());
        _node.setSharing(tree.getNode().getSharing());
        _node.setRecommended(tree.getNode().getRecommended());

        ArrayList<SDSTree> children = tree.getChildren();
        int childCount = children.size();
        for (int index = 0; index < childCount; index++) {
            SDSTree child = children.get(index);
            if (child != null) {
                SDSTree tmpChild = new SDSTree();
                tmpChild.copyAndLink(child);
                tmpChild.setParent(this);
                this.setChild(index, tmpChild);
            } else {
                this.setChild(index, null);
            }
        }
    }
}
