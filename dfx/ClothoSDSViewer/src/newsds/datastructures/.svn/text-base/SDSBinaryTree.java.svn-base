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
public class SDSBinaryTree extends SDSBasicGraph {

    private SDSBinaryTree _leftChild;
    private SDSBinaryTree _rightChild;
    private SDSBinaryTree _link;
    private SDSBinaryTree _parent;
    private ArrayList<SDSBinaryTree> _subtrees;
    private ArrayList<SDSBinaryTree> _instances;
    
    public SDSBinaryTree() {
        super();
        _leftChild = null;
        _rightChild = null;
        _parent = null;
    }

    public SDSBinaryTree(StringList part) {
        this();
        _node.setPart(part);
    }

    public SDSBinaryTree(SDSBinaryTree leftTree, SDSBinaryTree rightTree) {
        super();
        _leftChild = leftTree;
        _rightChild = rightTree;
        _leftChild.setParent(this);
        _rightChild.setParent(this);
        _parent = null;
        StringList part = new StringList();
        part.addAll(leftTree.getNode().getPart());
        part.addAll(rightTree.getNode().getPart());
        _node.setPart(part);
    }

//    public void substituteNode(String nodeName, SDSBinaryTree tree) {
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

    public void resetColorRec(SDSBinaryTree tree) {
        if (tree == null) {
            return;
        }
        tree.getNode().resetColor();
        resetColorRec(tree.getLeft());
        resetColorRec(tree.getRight());
    }

    public void resetColorRec() {
        resetColorRec(this);
    }

    public SDSBinaryTree getParent() {
        return _parent;
    }

    public SDSBinaryTree getLeft() {
        return _leftChild;
    }

    public SDSBinaryTree getRight() {
        return _rightChild;
    }

    public ArrayList<SDSBinaryTree> getInstances() {
        return _instances;
    }

    public void setParent(SDSBinaryTree parent) {
        _parent = parent;
    }

    public void setLeft(SDSBinaryTree left) {
        _leftChild = left;
        _leftChild.setParent(this);
    }

    public void setRight(SDSBinaryTree right) {
        _rightChild = right;
        _rightChild.setParent(this);
    }

    @Override
    public String toString() {
        String left = "";
        String right = "";
        if (Debug.SHOW_TREE_STRUCTURE) {
            if (_leftChild == null) {
                left = "-";
            } else {
                left = _leftChild.toString();
            }

            if (_rightChild == null) {
                right = "-";
            } else {
                right = _rightChild.toString();
            }
        }

        if (left.equals("-") && right.equals("-")) {
            String res = _node.getPart().toString();
            if (Debug.SHOW_NODE_ID) {
                res += "[" + _node.getId() + "]";
            }
            return res;
        } else {
            String res = "";//_node.getCompositePart().toString();
            if (Debug.SHOW_NODE_ID) {
                res += "[" + _node.getId() + "]";
            }
            if (Debug.SHOW_TREE_STRUCTURE) {
                res += "(" + left + " + " + right + ")";
            }
            return res;
        }
    }

    @Override
    public boolean equals(Object o) {
        return (((SDSBinaryTree) o).getNode().getPart().toString().equals(this.getNode().getPart().toString()));
    }

    private void subtreesRec(SDSBinaryTree tree) {
        if (tree == null) {
            return;
        }
        _subtrees.add(tree);
        subtreesRec(tree.getLeft());
        subtreesRec(tree.getRight());
    }

    public ArrayList<SDSBinaryTree> getSubtrees() {
        if (_subtrees != null) return _subtrees;
        _subtrees = new ArrayList<SDSBinaryTree>();
        subtreesRec(this);
        Collections.sort(_subtrees, new Comparator<SDSBinaryTree>() {

            @Override
            public int compare(SDSBinaryTree t1, SDSBinaryTree t2) {
                return t2.getNode().getPart().size() - t1.getNode().getPart().size();
            }
        });
        return _subtrees;
    }

    public boolean color2ab(String color) {
        return true;
    }

    public void copyAndLink(SDSBinaryTree tree) {
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

        if (tree.getLeft() != null) {
            _leftChild = new SDSBinaryTree();
            _leftChild.copyAndLink(tree.getLeft());
            _leftChild.setParent(this);
        } else {
            _leftChild = null;
        }

        if (tree.getRight() != null) {
            _rightChild = new SDSBinaryTree();
            _rightChild.copyAndLink(tree.getRight());
            _rightChild.setParent(this);
        } else {
            _rightChild = null;
        }
    }
}
