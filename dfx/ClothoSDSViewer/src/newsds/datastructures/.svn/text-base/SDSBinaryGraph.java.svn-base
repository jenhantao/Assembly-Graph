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
public class SDSBinaryGraph extends SDSBasicGraph {

    private SDSBinaryGraph _leftChild;
    private SDSBinaryGraph _rightChild;
    private SDSBinaryGraph _link;
    private ArrayList<SDSBinaryGraph> _parents;
    private ArrayList<SDSBinaryGraph> _subtrees;
    private ArrayList<SDSBinaryGraph> _instances;

    public SDSBinaryGraph() {
        super();
        _leftChild = null;
        _rightChild = null;
        _parents = new ArrayList<SDSBinaryGraph>();
    }

    public SDSBinaryGraph(StringList part) {
        this();
        _node.setPart(part);
    }

    public SDSBinaryGraph(String part) {
        this();
        _node.getPart().add(part);
    }

    public SDSBinaryGraph(SDSBinaryGraph leftTree, SDSBinaryGraph rightTree) {
        super();
        _leftChild = leftTree;
        _rightChild = rightTree;
        _leftChild.addParent(this);
        _rightChild.addParent(this);
        _parents = new ArrayList<SDSBinaryGraph>();
        StringList part = new StringList();
        part.addAll(leftTree.getNode().getPart());
        part.addAll(rightTree.getNode().getPart());
        _node.setPart(part);
    }

    public void resetColorRec(SDSBinaryGraph sdsGraph) {
        if (sdsGraph == null) {
            return;
        }
        sdsGraph.getNode().resetColor();
        resetColorRec(sdsGraph.getLeft());
        resetColorRec(sdsGraph.getRight());
    }

    public void resetColorRec() {
        resetColorRec(this);
    }

    public boolean parentExists(SDSBinaryGraph parent) {
        return _parents.contains(parent);
    }

    public ArrayList<SDSBinaryGraph> getParents() {
        return _parents;
    }

    public SDSBinaryGraph getParent(int index) {
        if (_parents.size() <= index) {
            return null;
        }
        return _parents.get(index);
    }

    public int getParentCount() {
        return _parents.size();
    }

    public SDSBinaryGraph getLeft() {
        return _leftChild;
    }

    public SDSBinaryGraph getRight() {
        return _rightChild;
    }

    public ArrayList<SDSBinaryGraph> getInstances() {
        return _instances;
    }

    public void addParent(SDSBinaryGraph parent) {
        if (!parentExists(parent)) {
            _parents.add(parent);
        }
    }

    public void removeParent(SDSBinaryGraph parent) {
        if (!parentExists(parent)) {
            return;
        }
        _parents.remove(parent);
    }

    public void setLeft(SDSBinaryGraph left) {
        _leftChild = left;
        _leftChild.addParent(this);
    }

    public void setRight(SDSBinaryGraph right) {
        _rightChild = right;
        _rightChild.addParent(this);
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
        if (o instanceof SDSBinaryGraph) {
            return (((SDSBinaryGraph) o).getNode().getPart().toString().equals(this.getNode().getPart().toString()));
        }
        return false;
    }

    private void subgraphsRec(SDSBinaryGraph sdsGraph, ArrayList<SDSBinaryGraph> exclusionList) {
        if (sdsGraph == null) {
            return;
        }
        _subtrees.add(sdsGraph);
        if (exclusionList.contains(sdsGraph)) {
            return;
        }
        subgraphsRec(sdsGraph.getLeft(), exclusionList);
        subgraphsRec(sdsGraph.getRight(), exclusionList);
    }

    public ArrayList<SDSBinaryGraph> getSubgraphs(ArrayList<SDSBinaryGraph> exclusionList) {
        _subtrees = new ArrayList<SDSBinaryGraph>();
        subgraphsRec(this, exclusionList);
        Collections.sort(_subtrees, new Comparator<SDSBinaryGraph>() {

            @Override
            public int compare(SDSBinaryGraph g1, SDSBinaryGraph g2) {
                return g2.getNode().getPart().size() - g1.getNode().getPart().size();
            }
        });
        return _subtrees;
    }

    public boolean color2ab(String color) {
        return true;
    }

    public void copyAndLink(SDSBinaryGraph sdsGraph) {
        if (sdsGraph == null) {
            return;
        }

        _link = sdsGraph;

        _node = new SDSNode();
        _node.setPart(sdsGraph.getNode().getPart());
        _node.setStages(sdsGraph.getNode().getStages());
        _node.setSteps(sdsGraph.getNode().getSteps());
        _node.setSharing(sdsGraph.getNode().getSharing());
        _node.setRecommended(sdsGraph.getNode().getRecommended());

        if (sdsGraph.getLeft() != null) {
            _leftChild = new SDSBinaryGraph();
            _leftChild.copyAndLink(sdsGraph.getLeft());
            _leftChild.addParent(this);
        } else {
            _leftChild = null;
        }

        if (sdsGraph.getRight() != null) {
            _rightChild = new SDSBinaryGraph();
            _rightChild.copyAndLink(sdsGraph.getRight());
            _rightChild.addParent(this);
        } else {
            _rightChild = null;
        }
    }

    public void copyFromTree(SDSBinaryTree tree) {
        if (tree == null) {
            return;
        }

        _node = new SDSNode();
        _node.setPart(tree.getNode().getPart());
        _node.setStages(tree.getNode().getStages());
        _node.setSteps(tree.getNode().getSteps());
        _node.setSharing(tree.getNode().getSharing());

        if (tree.getLeft() != null) {
            _leftChild = new SDSBinaryGraph();
            _leftChild.copyFromTree(tree.getLeft());
            _leftChild.addParent(this);
        } else {
            _leftChild = null;
        }

        if (tree.getRight() != null) {
            _rightChild = new SDSBinaryGraph();
            _rightChild.copyFromTree(tree.getRight());
            _rightChild.addParent(this);
        } else {
            _rightChild = null;
        }
    }
}
