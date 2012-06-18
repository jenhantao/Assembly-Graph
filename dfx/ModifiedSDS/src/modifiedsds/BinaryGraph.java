/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package modifiedsds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author vvasilev
 */
public class BinaryGraph extends BasicGraph {
    private BinaryGraph _leftChild;
    private BinaryGraph _rightChild;
    private BinaryGraph _link;

    private ArrayList<BinaryGraph> _parents;
    private ArrayList<BinaryGraph> _subtrees;
    private ArrayList<BinaryGraph> _instances;

    public BinaryGraph() {
        super();
        _leftChild = null;
        _rightChild = null;
        _parents = null;
    }

    public BinaryGraph(CompositePart part) {
        this();
        _node.setCompositePart(part);
    }

    public BinaryGraph(BasicPart part) {
        this();
        _node.getCompositePart().add(part);
    }

    public BinaryGraph(BinaryGraph leftTree, BinaryGraph rightTree) {
        super();
        _leftChild = leftTree;
        _rightChild = rightTree;
        _leftChild.addParent(this);
        _rightChild.addParent(this);
        _parents = new ArrayList<BinaryGraph>();
        _node.setCompositePart(new CompositePart(leftTree.getPart(), rightTree.getPart()));
    }

    public void resetColorRec(BinaryGraph sdsGraph) {
        if (sdsGraph == null) return;
        sdsGraph.resetNodeColor();
        resetColorRec(sdsGraph.getLeft());
        resetColorRec(sdsGraph.getRight());
    }

    public void resetColorRec() {
        resetColorRec(this);
    }

    public boolean parentExists(BinaryGraph parent) {
        if (_parents == null) {
            _parents = new ArrayList<BinaryGraph>();
            return false;
        }
        return _parents.contains(parent);
    }

    public ArrayList<BinaryGraph> getParents() {
        return _parents;
    }

    public BinaryGraph getParent(int index) {
        if (_parents.size() <= index) return null;
        return _parents.get(index);
    }

    public int getParentCount() {
        return _parents.size();
    }

    public BinaryGraph getLeft() {
        return _leftChild;
    }

    public BinaryGraph getRight() {
        return _rightChild;
    }

    public ArrayList<BinaryGraph> getInstances() {
        return _instances;
    }

    public void addParent(BinaryGraph parent) {
        if (!parentExists(parent)) _parents.add(parent);
    }

    public void removeParent(BinaryGraph parent) {
        if (!parentExists(parent)) return;
        _parents.remove(parent);
    }

    public void setLeft(BinaryGraph left) {
        _leftChild = left;
        _leftChild.addParent(this);
    }

    public void setRight(BinaryGraph right) {
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
        if (o instanceof BinaryGraph) {
            return (((BinaryGraph) o).getPart().toString().equals(this.getPart().toString()));
        }
        return false;
    }

    private void subgraphsRec(BinaryGraph sdsGraph, ArrayList<BinaryGraph> exclusionList) {
        if (sdsGraph == null) return;
        _subtrees.add(sdsGraph);
        if (exclusionList.contains(sdsGraph)) return;
        subgraphsRec(sdsGraph.getLeft(), exclusionList);
        subgraphsRec(sdsGraph.getRight(), exclusionList);
    }

    public ArrayList<BinaryGraph> getSubgraphs(ArrayList<BinaryGraph> exclusionList) {
        _subtrees = new ArrayList<BinaryGraph>();
        subgraphsRec(this, exclusionList);
        Collections.sort(_subtrees, new Comparator<BinaryGraph>() {
            @Override
            public int compare(BinaryGraph g1, BinaryGraph g2) {
                return g2.getPart().size() - g1.getPart().size();
            }
        });
        return _subtrees;
    }

    public boolean color2ab(String color) {
        return true;
    }

    public void copyAndLink(BinaryGraph sdsGraph) {
        if (sdsGraph == null) return;

        _link = sdsGraph;

        _node = new Node();
        _node.setCompositePart(sdsGraph.getPart());
        _node.setStages(sdsGraph.getStages());
        _node.setSteps(sdsGraph.getSteps());
        _node.setSharing(sdsGraph.getSharing());

        if (sdsGraph.getLeft() != null) {
            _leftChild = new BinaryGraph();
            _leftChild.copyAndLink(sdsGraph.getLeft());
            _leftChild.addParent(this);
        } else {
            _leftChild = null;
        }

        if (sdsGraph.getRight() != null) {
            _rightChild = new BinaryGraph();
            _rightChild.copyAndLink(sdsGraph.getRight());
            _rightChild.addParent(this);
        } else {
            _rightChild = null;
        }
    }

    public void copyFromTree(Tree tree) {
        if (tree == null) return;

        _node = new Node();
        _node.setCompositePart(tree.getPart());
        _node.setStages(tree.getStages());
        _node.setSteps(tree.getSteps());
        _node.setSharing(tree.getSharing());

        if (tree.getLeft() != null) {
            _leftChild = new BinaryGraph();
            _leftChild.copyFromTree(tree.getLeft());
            _leftChild.addParent(this);
        } else {
            _leftChild = null;
        }

        if (tree.getRight() != null) {
            _rightChild = new BinaryGraph();
            _rightChild.copyFromTree(tree.getRight());
            _rightChild.addParent(this);
        } else {
            _rightChild = null;
        }
    }
}
