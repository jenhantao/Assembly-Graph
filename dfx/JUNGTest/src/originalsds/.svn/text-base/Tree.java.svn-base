/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package originalsds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author vvasilev
 */
public class Tree extends BasicGraph {

    private Tree _leftChild;
    private Tree _rightChild;
    private Tree _link;
    private Tree _parent;
    private ArrayList<Tree> _subtrees;
    private ArrayList<Tree> _instances;
    public int _badLength = 0;
    public int _tuCount = 0;

    public Tree() {
        super();
        _leftChild = null;
        _rightChild = null;
        _parent = null;
    }

    public Tree(CompositePart part) {
        this();
        _node.setCompositePart(part);
    }

    public Tree(Tree leftTree, Tree rightTree) {
        super();
        _leftChild = leftTree;
        _rightChild = rightTree;
        _leftChild.setParent(this);
        _rightChild.setParent(this);
        _parent = null;
        _node.setCompositePart(new CompositePart(leftTree.getPart(), rightTree.getPart()));
    }

    public void substituteNode(String nodeName, Tree tree) {
        if (_leftChild != null) {
            if (_leftChild.toString().equals(nodeName)) {
                _leftChild = tree;
            }
            _leftChild.substituteNode(nodeName, tree);
        }
        if (_rightChild != null) {
            if (_rightChild.toString().equals(nodeName)) {
                _rightChild = tree;
            }
            _rightChild.substituteNode(nodeName, tree);
        }
    }

    public void resetColorRec(Tree tree) {
        if (tree == null) {
            return;
        }
        tree.resetNodeColor();
        resetColorRec(tree.getLeft());
        resetColorRec(tree.getRight());
    }

    public void resetColorRec() {
        resetColorRec(this);
    }

    public Tree getParent() {
        return _parent;
    }

    public Tree getLeft() {
        return _leftChild;
    }

    public Tree getRight() {
        return _rightChild;
    }

    public ArrayList<Tree> getInstances() {
        return _instances;
    }

    public void setParent(Tree parent) {
        _parent = parent;
    }

    public void setLeft(Tree left) {
        _leftChild = left;
        _leftChild.setParent(this);
    }

    public void setRight(Tree right) {
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
            String res = _node.getCompositePart().toString();
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
        return (((Tree) o).getPart().toString().equals(this.getPart().toString()));
    }

    private void subtreesRec(Tree tree) {
        if (tree == null) {
            return;
        }
        _subtrees.add(tree);
        subtreesRec(tree.getLeft());
        subtreesRec(tree.getRight());
    }

    public ArrayList<Tree> getSubtrees() {
        if (_subtrees != null) return _subtrees;
        _subtrees = new ArrayList<Tree>();
        subtreesRec(this);
        Collections.sort(_subtrees, new Comparator<Tree>() {

            @Override
            public int compare(Tree t1, Tree t2) {
                return t2.getPart().size() - t1.getPart().size();
            }
        });
        return _subtrees;
    }

    public boolean color2ab(String color) {
        return true;
    }

    public void copyAndLink(Tree tree) {
        if (tree == null) {
            return;
        }

        _link = tree;

        _node = new Node();
        _node.setCompositePart(tree.getPart());
        _node.setStages(tree.getStages());
        _node.setSteps(tree.getSteps());
        _node.setSharing(tree.getSharing());

        if (tree.getLeft() != null) {
            _leftChild = new Tree();
            _leftChild.copyAndLink(tree.getLeft());
            _leftChild.setParent(this);
        } else {
            _leftChild = null;
        }

        if (tree.getRight() != null) {
            _rightChild = new Tree();
            _rightChild.copyAndLink(tree.getRight());
            _rightChild.setParent(this);
        } else {
            _rightChild = null;
        }
    }
}
