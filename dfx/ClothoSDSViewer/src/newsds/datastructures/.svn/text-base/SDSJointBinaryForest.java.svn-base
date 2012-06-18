/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newsds.datastructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author vvasilev
 */
public class SDSJointBinaryForest extends SDSBasicGraph {

    private ArrayList<SDSBinaryGraph> _goalParts;
    private ArrayList<SDSBinaryGraph> _visited;

    public SDSJointBinaryForest() {
        super();
        _goalParts = new ArrayList<SDSBinaryGraph>();
    }

    public void addGoalPart(SDSBinaryGraph goalPart) {
        _goalParts.add(goalPart);
    }

    public void collapse() {
        ArrayList<SDSBinaryGraph> subgraphs = null;
        for (int index = 0; index < _goalParts.size(); index++) {
            SDSBinaryGraph goalPart = _goalParts.get(index);

            if (subgraphs == null) {
                subgraphs = goalPart.getSubgraphs(new ArrayList<SDSBinaryGraph>());
                continue;
            } else {
                ArrayList<SDSBinaryGraph> tempSubtrees = goalPart.getSubgraphs(subgraphs);

                for (int tempIndex = 0; tempIndex < tempSubtrees.size(); tempIndex++) {
                    SDSBinaryGraph tempGraph = tempSubtrees.get(tempIndex);
                    if (subgraphs.contains(tempGraph)) {
                        // Determine if tempGraph is lefty or righty
                        SDSBinaryGraph parent = tempGraph.getParent(0);
                        if (parent == null) {
                            // TODO: decide what to do if parent does not exist
                        } else if (parent.getLeft().equals(tempGraph)) {
                            parent.setLeft(subgraphs.get(subgraphs.indexOf(tempGraph)));
                        } else {
                            parent.setRight(subgraphs.get(subgraphs.indexOf(tempGraph)));
                        }
                    } else {
                        subgraphs.add(tempGraph);
                        subgraphs = sortList(subgraphs);
                    }
                }
            }
        }
        calculateStagesSteps();
    }

    private void calculateStagesSteps() {
        _visited = new ArrayList<SDSBinaryGraph>();
        _node.setStages(0);
        _node.setSteps(0);

        for (SDSBinaryGraph parent : _goalParts) {
            int stage = traverse(parent);
            int stages = _node.getStages();
            stages = stage > stages ? stage : stages;
            _node.setStages(stages);
        }
    }

    private int traverse(SDSBinaryGraph graph) {
        if (graph.getLeft() == null && graph.getRight() == null) {
            return 0;
        }
        if (!_visited.contains(graph)) {
            _visited.add(graph);
            if (graph.getLeft() != null && graph.getRight() != null) {
                this._node.setSteps(this._node.getSteps() + 1);
            }
        }
        return Math.max(traverse(graph.getLeft()), traverse(graph.getRight())) + 1;
    }

    public ArrayList<SDSBinaryGraph> getGraphs() {
        return _goalParts;
    }

    public void color2ab() {
        ArrayList<String> colors = Colors.getColors();
        int colorCount = Colors.getColorCount();

        for (int index = 0; index < _goalParts.size(); index++) {
            boolean success = _goalParts.get(index).color2ab(colors.get(index % colorCount));
            if (success == false) {
            }
        }
    }

    @Override
    public String toString() {
        return _goalParts.toString();
    }

    private ArrayList<SDSBinaryGraph> sortList(ArrayList<SDSBinaryGraph> list) {
        Collections.sort(list, new Comparator<SDSBinaryGraph>() {

            @Override
            public int compare(SDSBinaryGraph t1, SDSBinaryGraph t2) {
                return t2.getNode().getPart().size() - t1.getNode().getPart().size();
            }
        });
        return list;
    }

    public void populate(ArrayList<SDSBinaryGraph> bGraphs) {

        for (int index = 0; index < bGraphs.size(); index++) {
            SDSBinaryGraph bGraph = new SDSBinaryGraph();
            bGraph.copyAndLink(bGraphs.get(index));
            this.addGoalPart(bGraph);
        }
    }
}
//class TreeList extends ArrayList<Tree> {
//    public void contains(Tree tree) {
//        for (int index = 0; index < this.size(); index++) {
//            if (tree.getPart().equals(tree))
//        }
//        return false;
//    }
//}

