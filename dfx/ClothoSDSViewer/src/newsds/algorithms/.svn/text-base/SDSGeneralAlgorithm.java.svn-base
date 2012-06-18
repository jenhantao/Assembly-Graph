package newsds.algorithms;

import newsds.datastructures.SDSJointForest;
import newsds.datastructures.SDSGraph;
import newsds.datastructures.SDSTree;
import helper.HashStringInteger;
import helper.HashStringTree;
import helper.ListPartitioner;
import helper.StringList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vvasilev
 */
public class SDSGeneralAlgorithm {

    private HashStringInteger _requiredParts;
    private HashStringInteger _recommendedParts;
    private int _minChildCount;
    private int _maxChildCount;

    public SDSGeneralAlgorithm(int minChildCount, int maxChildCount) {
        _minChildCount = minChildCount;
        _maxChildCount = maxChildCount;
    }

    /***************************************************************************
     *
     * Public methods
     *
     **************************************************************************/
    /**
     * Runs the modified SDS algorithm for a set of goal parts.
     * @param goalParts A list of goal parts.
     * @param hashPartLibrary Library parts.
     * @return Returns a list of assembly trees for all corresponding goal parts.
     */
    public ArrayList<SDSTree> createAsmTreeMultipleGoalParts(ArrayList<StringList> goalPartsOrig, ArrayList<StringList> requiredPartsOrig, ArrayList<StringList> recommendedPartsOrig, HashStringTree hashPartLibrary) {
        ArrayList<StringList> goalParts = (ArrayList<StringList>) goalPartsOrig.clone();
        _requiredParts = new HashStringInteger();
        _recommendedParts = new HashStringInteger();

        for (StringList part : requiredPartsOrig) {
            _requiredParts.put(part.toString(), 1);
        }
        for (StringList part : recommendedPartsOrig) {
            _recommendedParts.put(part.toString(), 1);
        }

        // Run single goal part algorithm on each goal part to find max stages
        int maxStages = computeMaxstage(goalParts, hashPartLibrary);
//        if (goalParts.size() == 1) {
//            maxStages = 0;
//        }
        // Calculate the sharing factors for each possible intermediate part
        HashStringInteger hashSharing = computeSharing(goalParts);
        // Iterate across all goal parts until we have a result tree for each goal part
        ArrayList<SDSTree> resultTrees = new ArrayList<SDSTree>();
        HashStringTree hashPinned = new HashStringTree();

        while (!goalParts.isEmpty()) {
            System.out.println("Goal Parts" + Integer.toString(goalParts.size()));
            // Reinitialize memoization hash with part library and pinned trees each with zero cost
            HashStringTree hashMem = new HashStringTree(hashPartLibrary, hashPinned);
            // Call single-goal-part algorithm for each goal part and determine which of tree to pin
            SDSTree treePinned = new SDSTree();

            for (int index = 0; index < goalParts.size(); index++) {
//                System.out.println(Integer.toString(index));
                StringList part = goalParts.get(index);
                SDSTree treeNew = createAsmTreeSingleGoalPart(part, hashMem, maxStages, hashSharing);

                // Pin tree with most stages
                if (treeNew.getNode().getStages() > treePinned.getNode().getStages()) {
                    treePinned = treeNew;
                }
            }

            // Add pinned tree and tree for each intermediate part to our hash of pinned trees
            ArrayList<SDSTree> subtrees = treePinned.getSubtrees();
            //System.out.println(subtrees.toString());
            int subtreesSize = subtrees.size();
            for (int index = 0; index < subtreesSize; index++) {
                SDSTree subtree = subtrees.get(index);
                //subtree.resetCost();
                hashPinned.put(subtree.getNode().getPart().toString(), subtree);
            }

            // Remove pinned tree from goal part list and add to result list
            goalParts.remove(treePinned.getNode().getPart());
            resultTrees.add(treePinned);
        }

        return resultTrees;
    }

    /**
     * Runs the modified SDS algorithm for a single goal part.
     * @param goalPart A list of goal parts.
     * @param hashMem A set of parts and their corresponding assembly trees.
     * @param slack Slack.
     * @param hashSharing Sub-parts and count of how many times they are shared.
     * @return An assembly tree for the goal part.
     */
    public SDSTree createAsmTreeSingleGoalPart(StringList goalPart, HashStringTree hashMem, int slack, HashStringInteger hashSharing) {
        // Memoization Case : memoization hash already has desired part
        String goalPartString = goalPart.toString();

        if (hashMem.containsKey(goalPartString)) {
            return hashMem.get(goalPartString);
        }

        // Base Case : part is primitive part
        if (goalPart.size() == 1) {
            SDSTree returnTree = new SDSTree(goalPart);
            hashMem.put(goalPartString, returnTree);
            return returnTree;
        }

        // Recursive Step : iteratively partition part and recurse
        SDSTree treeBest = new SDSTree();
        //TODO: should be smarter
        treeBest.getNode().setStages(1000);

        ArrayList<Integer> indexes = new ArrayList<Integer>();
        int goalPartSize = goalPart.size();
        for (int i = 0; i < goalPartSize; i++) {
            indexes.add(i);
        }

        //if (!_requiredParts.contains(goalPart)) {
        for (int start = 0; start < goalPartSize; start++) {
            for (int end = start + 2; end < goalPartSize + 1; end++) {
                if (start == 0 && end == goalPartSize) {
                    continue;
                }
                StringList part = goalPart.subList(start, end);

                if (_requiredParts.containsKey(part.toString())) {
                    for (int i = start + 1; i < end; i++) {
                        indexes.remove(new Integer(i));
                    }
                }
            }
        }

        ListPartitioner partitioner = new ListPartitioner();
        List<List> partitionList = new ArrayList<List>();
        for (int partitionCount = _minChildCount; partitionCount <= _maxChildCount; partitionCount++) {
            if (indexes.size() < partitionCount) {
                continue;
            }
            partitionList.addAll(partitioner.getPartitions(indexes, partitionCount));
        }

        for (List partition : partitionList) {
            treeBest = findPartitionSolution(partition, treeBest, goalPart, hashMem, slack, hashSharing);
        }

        hashMem.put(goalPartString, treeBest);

        return treeBest;
    }

    private SDSTree findPartitionSolution(List partition, SDSTree treeBest, StringList goalPart, HashStringTree hashMem, int slack, HashStringInteger hashSharing) {
        ArrayList<Integer> newIndexes = new ArrayList<Integer>();
        for (int index = 0; index < partition.size(); index++) {
            newIndexes.add(((List<Integer>) partition.get(index)).get(0));
        }
        newIndexes.add(goalPart.size());

        ArrayList<SDSTree> allChildren = new ArrayList<SDSTree>();
        int lastIndex = newIndexes.size() - 1;

        for (int i = 0; i < lastIndex; i++) {
            // Find best tree for left and right partitions
            StringList subpart = goalPart.subList(newIndexes.get(i), newIndexes.get(i + 1));
            SDSTree child = createAsmTreeSingleGoalPart(subpart, hashMem, slack - 1, hashSharing);
            allChildren.add(child);
        }
        // Combine child trees into new tree for intermediate part
        SDSTree treeNew = combineTrees(allChildren, hashSharing);

        // If cost of new tree is the best so far save the tree
        if (treeBest.getNode().getPart().size() == 0) {
            treeBest = treeNew;
        } else {
            treeBest = minCost(treeNew, treeBest, slack);
        }
        return treeBest;
    }

    /**
     * Joins a list of trees into a SDSJointForest in a 3A format.
     * @param goalPartTrees A list of trees.
     * @return The combined trees into a SDSJointForest.
     */
    public SDSJointForest convertTo3A(ArrayList<SDSTree> goalPartTrees) {
        SDSJointForest result = new SDSJointForest();
        result.populate(convertTreesToGraphs(goalPartTrees));
        result.collapse();

        return result;
    }

    /**
     * Joins a list of trees into a SDSJointForest in a 2ab format.
     * @param goalPartTrees A list of trees.
     * @return The combined trees into a SDSJointForest.
     */
    public SDSJointForest convertTo2ab(ArrayList<SDSTree> goalPartTrees) {
        SDSJointForest result = new SDSJointForest();
        result.populate(convertTreesToGraphs(goalPartTrees));
        result.collapse();
        result.color2ab();

        return result;
    }

    /**
     * Converts list of trees to binary graphs.
     * @param trees A list of trees which will be converted to binary graphs.
     * @return A list of corresponding binary graphs.
     */
    public ArrayList<SDSGraph> convertTreesToGraphs(ArrayList<SDSTree> trees) {
        ArrayList<SDSGraph> result = new ArrayList<SDSGraph>();

        for (int index = 0; index
                < trees.size(); index++) {
            SDSGraph bGraph = new SDSGraph();
            bGraph.copyFromTree(trees.get(index));
            result.add(bGraph);
        }

        return result;
    }

    /***************************************************************************
     *
     * Private methods
     *
     **************************************************************************/
    /**
     * Return tree created from combining two child trees factoring in sharing
     * @param leftTree Left tree.
     * @param rightTree Right tree.
     * @param hashSharing Shared parts and corresponding sharing count.
     * @return A tree which combines leftTree and rightTree by a single parent
     * node.
     */
    private SDSTree combineTrees(ArrayList<SDSTree> children, HashStringInteger hashSharing) {
        // Call original combineTrees function from Listing 2
        SDSTree treeNew = combineTrees(children);
        // Calculate sharing value of new tree
        int sharing = 0;
        for (SDSTree child : children) {
            sharing += child.getNode().getSharing();
        }
        treeNew.getNode().setSharing(sharing + hashSharing.getSharing(treeNew.getNode().getPart().toString()));

        return treeNew;
    }

    /**
     * Return tree created from combining two child trees.
     * @param leftTree Left tree.
     * @param rightTree Right tree.
     * @return A tree which combines leftTree and rightTree by a single parent
     * node.
     */
    private SDSTree combineTrees(ArrayList<SDSTree> children) {
        SDSTree treeNew = new SDSTree(children);

        // Calculate the cost of new tree
        int stages = -1;
        int steps = 0;
        int recommended = 0;
        for (SDSTree child : children) {
            stages = stages < child.getNode().getStages() ? child.getNode().getStages() : stages;
            steps += child.getNode().getSteps();
            recommended += child.getNode().getRecommended();
        }

        treeNew.getNode().setStages(stages + 1);
        treeNew.getNode().setSteps(steps + 1);

        int recommendedCount = 0;
        if (_recommendedParts.containsKey(treeNew.getNode().getPart().toString())) {
            recommendedCount = 1;
        }
        treeNew.getNode().setRecommended(recommended + recommendedCount);

        return treeNew;
    }

    /**
     * Return tree with minimum cost factoring in slack.
     * @param tree0 First tree.
     * @param tree1 Second tree.
     * @param slack Slack.
     * @return Returns the tree with the better cost.
     */
    private SDSTree minCost(SDSTree tree0, SDSTree tree1, int slack) {

        // If either tree has more stages than slack, then call original minCost function from Listing 2
        if ((tree0.getNode().getStages() > slack) || (tree1.getNode().getStages() > slack)) {
            return minCost(tree0, tree1);
        } // Trees have fewer stages than slack so no need to consider stages.
        // Allow sharing to compensate for a tree with more steps.
        int steps0 = tree0.getNode().getSteps() - tree0.getNode().getSharing();
        int steps1 = tree1.getNode().getSteps() - tree1.getNode().getSharing();

        if (steps0 < steps1) {
            return tree0;
        }
        if (steps1 < steps0) {
            return tree1;
        }

        if (tree1.getNode().getRecommended() < tree0.getNode().getRecommended()) {
            return tree0;
        }
        if (tree0.getNode().getRecommended() < tree1.getNode().getRecommended()) {
            return tree1;
        } // Trees have identical cost so arbitrarily choose one
        return tree0;
    }

    /**
     * Return tree with minimum cost.
     * @param tree0 First tree.
     * @param tree1 Second tree.
     * @return Returns the tree with the better cost.
     */
    private SDSTree minCost(SDSTree tree0, SDSTree tree1) {
        int stages0 = tree0.getNode().getStages();// - tree0._tuCount / (2*SDSAlgorithm.FUDGE_FACTOR);
        int stages1 = tree1.getNode().getStages();// - tree1._tuCount / (2*SDSAlgorithm.FUDGE_FACTOR);

        // Number of stages always take priority
        if (stages0 < stages1) {
            return tree0;
        }

        if (stages1 < stages0) {
            return tree1;
        }

        int steps0 = tree0.getNode().getSteps();
        int steps1 = tree1.getNode().getSteps();
        // If number of stages equal, then tree with less steps is lower cost
        if (steps0 < steps1) {
            return tree0;
        }
        if (steps1 < steps0) {
            return tree1;
        }

        if (tree1.getNode().getRecommended() < tree0.getNode().getRecommended()) {
            return tree0;
        }
        if (tree0.getNode().getRecommended() < tree1.getNode().getRecommended()) {
            return tree1;
        } // Trees have identical cost so arbitrarily choose one
        return tree0;
    }

    /**
     * Computes the maximum number of stages that a set of goal parts requires
     * to be assembled.
     * @param goalParts Dataset of goal parts.
     * @return A single integer representing the maximum number of stages among
     * goal part assembly trees.
     */
    private int computeMaxstage(ArrayList<StringList> goalParts, HashStringTree partLibrary) {
        int maxStage = 0;

        for (int index = 0; index
                < goalParts.size(); index++) {
            HashStringTree hashMem = (HashStringTree) partLibrary.clone();
            SDSTree tree = createAsmTreeSingleGoalPart(goalParts.get(index), hashMem, 0, new HashStringInteger());
            maxStage = maxStage > tree.getNode().getStages() ? maxStage : tree.getNode().getStages();
        }

        return maxStage;
    }

    /**
     * Goes over all possible combinations of subparts in the goal part dataset
     * and computes which and how many times are shared.
     * @param goalParts Dataset of goal parts
     * @return Returns all parts which are shared among goal parts and how many
     * times they are shared.
     */
    private HashStringInteger computeSharing(ArrayList<StringList> goalParts) {
        //Determine subpart sharing numbers for all goal parts
        HashStringInteger sharing = new HashStringInteger();

        for (int i = 0; i < goalParts.size(); i++) {
            StringList part = goalParts.get(i);

            for (int start = 0; start
                    < part.size(); start++) {
                for (int position = start + 1; position
                        < part.size(); position++) {
                    StringList subpart = part.subList(start, position + 1);
                    //System.out.print(subpart + "\n");
                    String subpartString = subpart.toString();
                    if (sharing.containsPart(subpartString)) {
                        Integer currentSharing = sharing.get(subpartString);
                        sharing.put(subpartString, currentSharing + 1);
                    } else {
                        sharing.put(subpartString, 1);
                    }
                }
            }
        }
        return sharing;
    }
//
//
//    public static ArrayList<SDSTree> 2ab_coloring(ArrayList<SDSTree> listassemby_tree ) {
//
//  boolean color_conflictglobal = true;
//
//        while (color_conflictglobal) {
//
//            // Reset color conflict flag
//            color_conflictglobal = false;
//
//            // Attempt to color the trees checking for a conflict
//            for (int index = 0; index < listassemby_tree.size(); index++ ) {
//                SDSTree tree = listassemby_tree.get(index);
//                boolean color_conflictlocal = color_assignment(tree);
//
//                if (color_conflictlocal) {
//                    // Find a cycle in the tree
//                    cycle = determine_cycle(tree);
//
//                    // Determine the low cost part in the cycle
//                    StringList low_cost_part = determine_low_cost_part(cycle);
//
//                    // Break the cycle
//                    break_cycle(tree, low_cost_part);
//                }
//
//                // Any local conflict will result in a global conflict and the loop will run again
//                color_conflictglobal =  color_conflictglobal || color_conflictlocal;
//            }
//        }
//
//        // Merge duplicate nodes created during cycle breaking
//        merge(listassemby_tree);
//
//        return listassemby_tree;
//
//    }
}
