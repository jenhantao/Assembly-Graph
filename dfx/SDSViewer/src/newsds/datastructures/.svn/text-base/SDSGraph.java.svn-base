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
public class SDSGraph extends SDSBasicGraph {

    private ArrayList<SDSGraph> _children;
    private SDSGraph _link;
    private ArrayList<SDSGraph> _parents;
    private ArrayList<SDSGraph> _subtrees;
    private ArrayList<SDSGraph> _instances;

    public SDSGraph() {
        super();
        _children = new ArrayList<SDSGraph>();
        _parents = new ArrayList<SDSGraph>();
    }

    public SDSGraph(StringList part) {
        this();
        _node.setPart(part);
    }

    public SDSGraph(String part) {
        this();
        _node.getPart().add(part);
    }

    public SDSGraph(ArrayList<SDSGraph> children) {
        super();
        _children = children;

        StringList part = new StringList();
        for (SDSGraph child : _children) {
            child.addParent(this);
            part.addAll(child.getNode().getPart());
        }
        _parents = new ArrayList<SDSGraph>();
        _node.setPart(part);
    }

    public void resetColorRec(SDSGraph graph) {
        if (graph == null) {
            return;
        }
        graph.getNode().resetColor();
        ArrayList<SDSGraph> children = graph.getChildren();
        for (SDSGraph child : children) {
            resetColorRec(child);
        }
    }

    public void resetColorRec() {
        resetColorRec(this);
    }

    public boolean parentExists(SDSGraph parent) {
        return _parents.contains(parent);
    }

    public ArrayList<SDSGraph> getParents() {
        return _parents;
    }

    public SDSGraph getParent(int index) {
        if (_parents.size() <= index) {
            return null;
        }
        return _parents.get(index);
    }

    public int getParentCount() {
        return _parents.size();
    }

    public ArrayList<SDSGraph> getChildren() {
        return _children;
    }

    public int getChildrenCount() {
        return _children.size();
    }

    public ArrayList<SDSGraph> getInstances() {
        return _instances;
    }

    public void addParent(SDSGraph parent) {
        if (!parentExists(parent)) {
            _parents.add(parent);
        }
    }

    public void setChild(int n, SDSGraph child) {
        if (_children.size() == n) {
            _children.add(child);
            child.addParent(this);
        } else if (_children.size() > n) {
            _children.set(n, child);
            child.addParent(this);
        } else {
            System.err.println("Too large child index.");
        }
    }

    public void removeParent(SDSGraph parent) {
        if (!parentExists(parent)) {
            return;
        }
        _parents.remove(parent);
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
        if (o instanceof SDSGraph) {
            return (((SDSGraph) o).getNode().getPart().toString().equals(this.getNode().getPart().toString()));
        }
        return false;
    }

    private void subgraphsRec(SDSGraph sdsGraph, ArrayList<SDSGraph> exclusionList) {
        if (sdsGraph == null) {
            return;
        }
        _subtrees.add(sdsGraph);
        if (exclusionList.contains(sdsGraph)) {
            return;
        }
        _subtrees.add(sdsGraph);
        ArrayList<SDSGraph> children = sdsGraph.getChildren();
        for (SDSGraph child : children) {
            subgraphsRec(child, exclusionList);
        }
    }

    public ArrayList<SDSGraph> getSubgraphs(ArrayList<SDSGraph> exclusionList) {
        _subtrees = new ArrayList<SDSGraph>();
        subgraphsRec(this, exclusionList);
        Collections.sort(_subtrees, new Comparator<SDSGraph>() {

            @Override
            public int compare(SDSGraph g1, SDSGraph g2) {
                return g2.getNode().getPart().size() - g1.getNode().getPart().size();
            }
        });
        return _subtrees;
    }

    public boolean color2ab(String color) {
        return true;
    }

    public void copyAndLink(SDSGraph sdsGraph) {
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

        ArrayList<SDSGraph> children = sdsGraph.getChildren();
        int childCount = children.size();
        for (int index = 0; index < childCount; index++) {
            SDSGraph child = children.get(index);
            if (child != null) {
                SDSGraph tmpChild = new SDSGraph();
                tmpChild.copyAndLink(child);
                tmpChild.addParent(this);
                this.setChild(index, tmpChild);
            } else {
                this.setChild(index, null);
            }
        }
    }

    public void copyFromTree(SDSTree tree) {
        if (tree == null) {
            return;
        }

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
                SDSGraph tmpChild = new SDSGraph();
                tmpChild.copyFromTree(child);
                tmpChild.addParent(this);
                this.setChild(index, tmpChild);
            } else {
                this.setChild(index, null);
            }
        }
    }
}
