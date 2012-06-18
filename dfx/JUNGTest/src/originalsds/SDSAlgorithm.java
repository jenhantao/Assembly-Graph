package originalsds;

import java.util.ArrayList;
import testability.Finder;

/**
 *
 * @author vvasilev
 */
public class SDSAlgorithm {

    public HashMotifs _motifs;

    public SDSAlgorithm() {
        _motifs = new HashMotifs();
    }

    public void addMotifs(HashMotifs motifs) {
        _motifs = motifs;
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
    public ArrayList<Tree> createAsmTreeMultipleGoalParts(ArrayList<CompositePart> goalPartsOrig, HashMem hashPartLibrary) {
        ArrayList<CompositePart> goalParts = (ArrayList<CompositePart>) goalPartsOrig.clone();

        // Run single goal part algorithm on each goal part to find max stages
        int maxStages = computeMaxstage(goalParts, hashPartLibrary);
//        if (goalParts.size() == 1) {
//            maxStages = 0;
//        }
        // Calculate the sharing factors for each possible intermediate part
        HashSharing hashSharing = computeSharing(goalParts);
        // Iterate across all goal parts until we have a result tree for each goal part
        ArrayList<Tree> resultTrees = new ArrayList<Tree>();
        HashMem hashPinned = new HashMem();

        while (!goalParts.isEmpty()) {
            System.out.println("Goal Parts" + Integer.toString(goalParts.size()));
            // Reinitialize memoization hash with part library and pinned trees each with zero cost
            HashMem hashMem = new HashMem(hashPartLibrary, hashPinned);
            // Call single-goal-part algorithm for each goal part and determine which of tree to pin
            Tree treePinned = new Tree();

            for (int index = 0; index < goalParts.size(); index++) {

                System.out.println(Integer.toString(index));
                CompositePart part = goalParts.get(index);
                Tree treeNew = createAsmTreeSingleGoalPart(part, hashMem, maxStages, hashSharing);

                // Pin tree with most stages
                if (treeNew.getStages() > treePinned.getStages()) {
                    treePinned = treeNew;
                }
            }

            // Add pinned tree and tree for each intermediate part to our hash of pinned trees
            ArrayList<Tree> subtrees = treePinned.getSubtrees();
            //System.out.println(subtrees.toString());

            for (int index = 0; index < subtrees.size(); index++) {
                Tree subtree = subtrees.get(index);
                //subtree.resetCost();
                hashPinned.insert(subtree.getPart(), subtree);
            }

            // Remove pinned tree from goal part list and add to result list
            goalParts.remove(treePinned.getPart());
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
    public Tree createAsmTreeSingleGoalPart(CompositePart goalPart, HashMem hashMem, int slack, HashSharing hashSharing) {
        // Memoization Case : memoization hash already has desired part
        if (hashMem.hasTree(goalPart, slack)) {
            return hashMem.getTree(goalPart);
        }

        // Base Case : part is primitive part
        if (goalPart.size() == 1) {
            Tree returnTree = new Tree(goalPart);
            hashMem.insert(goalPart, returnTree);
            return returnTree;
        }

        // Recursive Step : iteratively partition part and recurse
        Tree treeBest = new Tree();
        //TODO: should be smarter
        treeBest.setStages(1000);
        
        for (int i = 1; i < goalPart.size(); i++) {
            // Find best tree for left and right partitions
            CompositePart leftSubpart = goalPart.subpart(0, i);
            CompositePart rightSubpart = goalPart.subpart(i, goalPart.size());
            Tree leftTree = createAsmTreeSingleGoalPart(leftSubpart, hashMem, slack - 1, hashSharing);
            Tree rightTree = createAsmTreeSingleGoalPart(rightSubpart, hashMem, slack - 1, hashSharing);
            // Combine left and right trees into new tree for intermediate part
            Tree treeNew = combineTrees(leftTree, rightTree, hashSharing);
            // If cost of new tree is the best so far save the tree
            if (treeBest.getPart().size() == 0) {
                treeBest = treeNew;
            } else {
                treeBest = minCost(treeNew, treeBest, slack);
            }
        }

        // Add best tree to hash table and return
        hashMem.insert(goalPart, treeBest);

        return treeBest;
    }

    /**
     * Joins a list of trees into a JointBinaryForest in a 3A format.
     * @param goalPartTrees A list of trees.
     * @return The combined trees into a JointBinaryForest.
     */
    public JointBinaryForest convertTo3A(ArrayList<Tree> goalPartTrees) {
        JointBinaryForest result = new JointBinaryForest();
        result.populate(convertTreesToBinaryGraphs(goalPartTrees));
        result.collapse();
        return result;
    }

    /**
     * Joins a list of trees into a JointBinaryForest in a 2ab format.
     * @param goalPartTrees A list of trees.
     * @return The combined trees into a JointBinaryForest.
     */
    public JointBinaryForest convertTo2ab(ArrayList<Tree> goalPartTrees) {
        JointBinaryForest result = new JointBinaryForest();
        result.populate(convertTreesToBinaryGraphs(goalPartTrees));
        result.collapse();
        result.color2ab();
        return result;
    }

    /**
     * Converts list of trees to binary graphs.
     * @param trees A list of trees which will be converted to binary graphs.
     * @return A list of corresponding binary graphs.
     */
    public ArrayList<BinaryGraph> convertTreesToBinaryGraphs(ArrayList<Tree> trees) {
        ArrayList<BinaryGraph> result = new ArrayList<BinaryGraph>();

        for (int index = 0; index < trees.size(); index++) {
            BinaryGraph bGraph = new BinaryGraph();
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
    private Tree combineTrees(Tree leftTree, Tree rightTree, HashSharing hashSharing) {
        // Call original combineTrees function from Listing 2
        Tree treeNew = combineTrees(leftTree, rightTree);
        // Calculate sharing value of new tree
        treeNew.setSharing(leftTree.getSharing() + rightTree.getSharing() + hashSharing.getSharing(treeNew.getPart().toString()));

        return treeNew;
    }

    /**
     * Return tree created from combining two child trees.
     * @param leftTree Left tree.
     * @param rightTree Right tree.
     * @return A tree which combines leftTree and rightTree by a single parent
     * node.
     */
    private Tree combineTrees(Tree leftTree, Tree rightTree) {
        Tree treeNew = new Tree(leftTree, rightTree);

        // Calculate the cost of new tree
        treeNew.setStages(Math.max(leftTree.getStages(), rightTree.getStages()) + 1);
        treeNew.setSteps(leftTree.getSteps() + rightTree.getSteps() + 1);

        return treeNew;
    }

    /**
     * Return tree with minimum cost factoring in slack.
     * @param tree0 First tree.
     * @param tree1 Second tree.
     * @param slack Slack.
     * @return Returns the tree with the better cost.
     */
    private Tree minCost(Tree tree0, Tree tree1, int slack) {

        // If either tree has more stages than slack, then call original minCost function from Listing 2
        if ((tree0.getStages() > slack) || (tree1.getStages() > slack)) {
            return minCost(tree0, tree1);
        }

        // Trees have fewer stages than slack so no need to consider stages.
        // Allow sharing to compensate for a tree with more steps.
        int steps0 = tree0.getSteps() - tree0.getSharing();
        int steps1 = tree1.getSteps() - tree1.getSharing();

        if (steps0 < steps1) {
            return tree0;
        }
        if (steps1 < steps0) {
            return tree1;
        }

        // Trees have identical cost so arbitrarily choose one
        return tree0;
    }

    /**
     * Return tree with minimum cost.
     * @param tree0 First tree.
     * @param tree1 Second tree.
     * @return Returns the tree with the better cost.
     */
    private Tree minCost(Tree tree0, Tree tree1) {
        int stages0 = tree0.getStages();// - tree0._tuCount / (2*SDSAlgorithm.FUDGE_FACTOR);
        int stages1 = tree1.getStages();// - tree1._tuCount / (2*SDSAlgorithm.FUDGE_FACTOR);

        // Number of stages always take priority
        if (stages0 < stages1) {
            return tree0;
        }

        if (stages1 < stages0) {
            return tree1;
        }

        int steps0 = tree0.getSteps();
        int steps1 = tree1.getSteps();

        // If number of stages equal, then tree with less steps is lower cost
        if (steps0 < steps1) {
            return tree0;
        }

        if (steps1 < steps0) {
            return tree1;
        }

        // Trees have identical cost so arbitrarily choose one
        return tree0;
    }

    /**
     * Computes the maximum number of stages that a set of goal parts requires
     * to be assembled.
     * @param goalParts Dataset of goal parts.
     * @return A single integer representing the maximum number of stages among
     * goal part assembly trees.
     */
    private int computeMaxstage(ArrayList<CompositePart> goalParts, HashMem partLibrary) {
        int maxStage = 0;

        for (int index = 0; index < goalParts.size(); index++) {
            HashMem hashMem = (HashMem) partLibrary.clone();
            Tree tree = createAsmTreeSingleGoalPart(goalParts.get(index), hashMem, 0, new HashSharing());
            maxStage = maxStage > tree.getStages() ? maxStage : tree.getStages();
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
    private HashSharing computeSharing(ArrayList<CompositePart> goalParts) {
        //Determine subpart sharing numbers for all goal parts
        HashSharing sharing = new HashSharing();

        for (int i = 0; i < goalParts.size(); i++) {
            CompositePart part = goalParts.get(i);

            for (int start = 0; start < part.size(); start++) {
                for (int position = start + 1; position < part.size(); position++) {
                    CompositePart subpart = part.subpart(start, position + 1);
                    //System.out.print(subpart + "\n");

                    if (sharing.containsPart(subpart.toString())) {
                        Integer currentSharing = sharing.get(subpart.toString());
                        sharing.put(subpart.toString(), currentSharing + 1);
                    } else {
                        sharing.put(subpart.toString(), 1);
                    }
                }
            }
        }

        return sharing;
    }
//
//
//    public static ArrayList<Tree> 2ab_coloring(ArrayList<Tree> listassemby_tree ) {
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
//                Tree tree = listassemby_tree.get(index);
//                boolean color_conflictlocal = color_assignment(tree);
//
//                if (color_conflictlocal) {
//                    // Find a cycle in the tree
//                    cycle = determine_cycle(tree);
//
//                    // Determine the low cost part in the cycle
//                    CompositePart low_cost_part = determine_low_cost_part(cycle);
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
