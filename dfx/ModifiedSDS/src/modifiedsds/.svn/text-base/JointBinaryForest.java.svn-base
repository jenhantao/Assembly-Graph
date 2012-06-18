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
public class JointBinaryForest extends BasicGraph {

    private ArrayList<BinaryGraph> _goalParts;

    public JointBinaryForest() {
        super();
        _goalParts = new ArrayList<BinaryGraph>();
    }

    public void addGoalPart(BinaryGraph goalPart) {
        _goalParts.add(goalPart);
    }

    public void collapse() {
        ArrayList<BinaryGraph> subgraphs = null;
        for (int index = 0; index < _goalParts.size(); index++) {
            BinaryGraph goalPart = _goalParts.get(index);

            if (subgraphs == null) {
                subgraphs = goalPart.getSubgraphs(new ArrayList<BinaryGraph>());
                continue;
            } else {
                ArrayList<BinaryGraph> tempSubtrees = goalPart.getSubgraphs(subgraphs);

                for (int tempIndex = 0; tempIndex < tempSubtrees.size(); tempIndex++) {
                    BinaryGraph tempGraph = tempSubtrees.get(tempIndex);
                    if (subgraphs.contains(tempGraph)) {
                        // Determine if tempGraph is lefty or righty
                        BinaryGraph parent = tempGraph.getParent(0);
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

    private ArrayList<BinaryGraph> sortList(ArrayList<BinaryGraph> list) {
        Collections.sort(list, new Comparator<BinaryGraph>() {

            @Override
            public int compare(BinaryGraph t1, BinaryGraph t2) {
                return t2.getPart().size() - t1.getPart().size();
            }
        });
        return list;
    }

    public void populate(ArrayList<BinaryGraph> bGraphs) {
        
        for (int index = 0; index < bGraphs.size(); index++) {
            BinaryGraph bGraph = new BinaryGraph();
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

