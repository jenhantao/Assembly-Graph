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
public class SDSJointForest extends SDSBasicGraph {

    private ArrayList<SDSGraph> _goalParts;
    private ArrayList<SDSGraph> _visited;

    public SDSJointForest() {
        super();
        _goalParts = new ArrayList<SDSGraph>();
    }

    public void addGoalPart(SDSGraph goalPart) {
        _goalParts.add(goalPart);
    }

    public void collapse() {
        ArrayList<SDSGraph> subgraphs = null;
        for (int index = 0; index < _goalParts.size(); index++) {
            SDSGraph goalPart = _goalParts.get(index);

            if (subgraphs == null) {
                subgraphs = goalPart.getSubgraphs(new ArrayList<SDSGraph>());
                continue;
            } else {
                ArrayList<SDSGraph> tempSubtrees = goalPart.getSubgraphs(subgraphs);

                for (int tempIndex = 0; tempIndex < tempSubtrees.size(); tempIndex++) {
                    SDSGraph tempGraph = tempSubtrees.get(tempIndex);
                    if (subgraphs.contains(tempGraph)) {
                        // Determine if tempGraph is lefty or righty
                        SDSGraph parent = tempGraph.getParent(0);
                        if (parent == null) {
                            // TODO: decide what to do if parent does not exist
                        } else {
                            ArrayList<SDSGraph> children = parent.getChildren();
                            int childCount = children.size();
                            for (int chIndex = 0; chIndex < childCount; chIndex++) {
                                SDSGraph child = children.get(chIndex);
                                if (child.equals(tempGraph)) {
                                    parent.setChild(chIndex, subgraphs.get(subgraphs.indexOf(tempGraph)));
                                }
                            }
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
        _visited = new ArrayList<SDSGraph>();
        _node.setStages(0);
        _node.setSteps(0);

        for (SDSGraph parent : _goalParts) {
            int stage = traverse(parent);
            int stages = _node.getStages();
            stages = stage > stages ? stage : stages;
            _node.setStages(stages);
        }
    }

    private int traverse(SDSGraph graph) {
        if (graph.getChildrenCount() == 0) {
            return 0;
        }
        if (!_visited.contains(graph)) {
            _visited.add(graph);
            if (graph.getChildrenCount() > 0) {
                this._node.setSteps(this._node.getSteps() + 1);
            }
        }
        
        int max = -1;
        ArrayList<SDSGraph> children = graph.getChildren();
        for (SDSGraph child : children) {
            int result = traverse(child);
            max = max < result ? result : max;
        }

        return max + 1;
    }

    public ArrayList<SDSGraph> getGraphs() {
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

    private ArrayList<SDSGraph> sortList(ArrayList<SDSGraph> list) {
        Collections.sort(list, new Comparator<SDSGraph>() {

            @Override
            public int compare(SDSGraph t1, SDSGraph t2) {
                return t2.getNode().getPart().size() - t1.getNode().getPart().size();
            }
        });
        return list;
    }

    public void populate(ArrayList<SDSGraph> bGraphs) {

        for (int index = 0; index < bGraphs.size(); index++) {
            SDSGraph bGraph = new SDSGraph();
            bGraph.copyAndLink(bGraphs.get(index));
            this.addGoalPart(bGraph);
        }
    }
}


