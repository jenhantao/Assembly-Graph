package modifiedsds;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author vvasilev
 */
public class SDSAlgorithm {

    public static int FUDGE_FACTOR = 3;
    public static int TEST_LENGTH = 200;
    public static double MOTIF_THRESHOLD = 0.8;

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
    public static ArrayList<Tree> createAsmTreeMultipleGoalParts(ArrayList<CompositePart> goalParts, HashMem hashPartLibrary, HashMotifs motifs) {
        // Run single goal part algorithm on each goal part to find max stages
        int maxStages = computeMaxstage(goalParts);
        // Calculate the sharing factors for each possible intermediate part
        HashSharing hashSharing = computeSharing(goalParts);
        // Iterate across all goal parts until we have a result tree for each goal part
        ArrayList<Tree> resultTrees = new ArrayList<Tree>();
        HashMem hashPinned = new HashMem();

        while (!goalParts.isEmpty()) {
            // Reinitialize memoization hash with part library and pinned trees each with zero cost
            HashMem hashMem = new HashMem(hashPartLibrary, hashPinned);
            // Call single-goal-part algorithm for each goal part and determine which of tree to pin
            Tree treePinned = new Tree();

            for (int index = 0; index < goalParts.size(); index++) {
                CompositePart part = goalParts.get(index);
                Tree treeNew = createAsmTreeSingleGoalPart(part, hashMem, maxStages, hashSharing, motifs);

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
                subtree.resetCost();
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
    public static Tree createAsmTreeSingleGoalPart(CompositePart goalPart, HashMem hashMem, int slack, HashSharing hashSharing, HashMotifs motifs) {
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
            Tree leftTree = createAsmTreeSingleGoalPart(leftSubpart, hashMem, slack - 1, hashSharing, motifs);
            Tree rightTree = createAsmTreeSingleGoalPart(rightSubpart, hashMem, slack - 1, hashSharing, motifs);
            // Combine left and right trees into new tree for intermediate part
            Tree treeNew = combineTrees(leftTree, rightTree, hashSharing);
            // If cost of new tree is the best so far save the tree
            if (treeBest.getPart().size() == 0) {
                treeBest = treeNew;
            } else {
                treeBest = minCost(treeNew, treeBest, slack, motifs);
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
    public static JointBinaryForest convertTo3A(ArrayList<Tree> goalPartTrees) {
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
    public static JointBinaryForest convertTo2ab(ArrayList<Tree> goalPartTrees) {
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
    public static ArrayList<BinaryGraph> convertTreesToBinaryGraphs(ArrayList<Tree> trees) {
        ArrayList<BinaryGraph> result = new ArrayList<BinaryGraph>();

        for (int index = 0; index < trees.size(); index++) {
            BinaryGraph bGraph = new BinaryGraph();
            bGraph.copyFromTree(trees.get(index));
            result.add(bGraph);
        }

        return result;
    }
    
    /**
     * Goes over all possible combinations of subparts in the goal part dataset
     * and computes which and how many times are shared.
     * @param goalParts Dataset of goal parts
     * @return Returns all parts which are shared among goal parts and how many
     * times they are shared.
     */
    public static HashMotifs computeMotifs(ArrayList<CompositePart> trainingDatabase) {
        //Determine subpart sharing numbers for all goal parts
        HashMotifs motifs = new HashMotifs();
        Double maxSharing = 0.0;

        for (int i = 0; i < trainingDatabase.size(); i++) {
            CompositePart part = trainingDatabase.get(i);

            for (int start = 0; start < part.size(); start++) {
                for (int position = start + 1; position < part.size(); position++) {
                    CompositePart subpart = part.subpart(start, position + 1);
                    //System.out.print(subpart + "\n");

                    if (motifs.containsPart(subpart.getType())) {
                        Double currentSharing = motifs.get(subpart.getType());
                        motifs.put(subpart.getType(), currentSharing + 1);
                        if (maxSharing < currentSharing + 1) {
                            maxSharing = currentSharing + 1;
                        }
                    } else {
                        motifs.put(subpart.getType(), 1.0);
                        if (maxSharing < 1) {
                            maxSharing = 1.0;
                        }
                    }
                }
            }
        }

        for (String key: motifs.keySet()) {
            motifs.put(key, motifs.get(key) / maxSharing);
        }

        return motifs;
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
    private static Tree combineTrees(Tree leftTree, Tree rightTree, HashSharing hashSharing) {
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
    private static Tree combineTrees(Tree leftTree, Tree rightTree) {
        Tree treeNew = new Tree(leftTree, rightTree);

        // Calculate the cost of new tree
        treeNew.setStages(Math.max(leftTree.getStages(), rightTree.getStages()) + 1);
        treeNew.setSteps(leftTree.getSteps() + rightTree.getSteps() + 1);

        treeNew._badLength = leftTree._badLength + rightTree._badLength;

        if (treeNew.getPart().getLength() < SDSAlgorithm.TEST_LENGTH) {
            treeNew._badLength += 1;
        }

        treeNew._tuCount = (leftTree._tuCount + rightTree._tuCount);
        if (countTUs(treeNew.getPart()) >= 1) {
            treeNew._tuCount += 1;
        }

        return treeNew;
    }

    /**
     * Counts the number of transcriptional unit (TU) motifs which appear in a
     * composite part.
     * @param part Composite part which will be checked for TUs.
     * @return Number of TUs.
     */
    private static int countTUs(CompositePart part) {
        Pattern pattern = Pattern.compile("Promoter(\\.RBS\\.Gene\\.Terminator)+\\.?");
        Matcher matcher = pattern.matcher(part.getType());

        int count = 0;
        while (matcher.find()) {
            count++;
        }

        return count;
    }

    /**
     * Return tree with minimum cost factoring in slack.
     * @param tree0 First tree.
     * @param tree1 Second tree.
     * @param slack Slack.
     * @return Returns the tree with the better cost.
     */
    private static Tree minCost(Tree tree0, Tree tree1, int slack, HashMotifs motifs) {
        if (tree0._badLength < tree1._badLength) {
            return tree0;
        }
        if (tree0._badLength > tree1._badLength) {
            return tree1;
        }

        String type0 = tree0.getPart().getType();
        String type1 = tree1.getPart().getType();

        if (motifs.containsKey(type0) && !motifs.containsKey(type1)) {
            return tree0;
        }

        if (motifs.containsKey(type1) && !motifs.containsKey(type0)) {
            return tree1;
        }

        if (motifs.containsKey(type0) && motifs.containsKey(type1)) {

            Double mCount0 = motifs.get(type0);
            Double mCount1 = motifs.get(type1);

            if (mCount0 > mCount1 && mCount0 > SDSAlgorithm.MOTIF_THRESHOLD) {
                return tree0;
            }
            if (mCount1 > mCount0 && mCount1 > SDSAlgorithm.MOTIF_THRESHOLD) {
                return tree1;
            }
        }

        // If either tree has more stages than slack, then call original minCost function from Listing 2
        int adjustedSlack0 = slack + tree0._tuCount / (3 * SDSAlgorithm.FUDGE_FACTOR);
        int adjustedSlack1 = slack + tree1._tuCount / (3 * SDSAlgorithm.FUDGE_FACTOR);
        if ((tree0.getStages() > adjustedSlack0) || (tree1.getStages() > adjustedSlack1)) {
            return minCost(tree0, tree1);
        }

        // Trees have fewer stages than slack so no need to consider stages.
        // Allow sharing to compensate for a tree with more steps.
        int adjustedSteps0 = tree0.getSteps() - tree0.getSharing() - tree0._tuCount / SDSAlgorithm.FUDGE_FACTOR;
        int adjustedSteps1 = tree1.getSteps() - tree1.getSharing() - tree1._tuCount / SDSAlgorithm.FUDGE_FACTOR;

        if (adjustedSteps0 < adjustedSteps1) {
            return tree0;
        }
        if (adjustedSteps1 < adjustedSteps0) {
            return tree1;
        }

        if (tree0._tuCount > tree1._tuCount) {
            return tree0;
        }
        if (tree1._tuCount > tree0._tuCount) {
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
    private static Tree minCost(Tree tree0, Tree tree1) {
        int adjustedStages0 = tree0.getStages();// - tree0._tuCount / (2*SDSAlgorithm.FUDGE_FACTOR);
        int adjustedStages1 = tree1.getStages();// - tree1._tuCount / (2*SDSAlgorithm.FUDGE_FACTOR);

        // Number of stages always take priority
        if (adjustedStages0 < adjustedStages1) {
            return tree0;
        }

        if (adjustedStages1 < adjustedStages0) {
            return tree1;
        }

        int adjustedSteps0 = tree0.getSteps() - tree0._tuCount / SDSAlgorithm.FUDGE_FACTOR;
        int adjustedSteps1 = tree1.getSteps() - tree1._tuCount / SDSAlgorithm.FUDGE_FACTOR;

        // If number of stages equal, then tree with less steps is lower cost
        if (adjustedSteps0 < adjustedSteps1) {
            return tree0;
        }

        if (adjustedSteps1 < adjustedSteps0) {
            return tree1;
        }

        if (tree0._tuCount > tree1._tuCount) {
            return tree0;
        }
        if (tree1._tuCount > tree0._tuCount) {
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
    private static int computeMaxstage(ArrayList<CompositePart> goalParts) {
        int maxStage = 0;

        for (int index = 0; index < goalParts.size(); index++) {
            Tree tree = createAsmTreeSingleGoalPart(goalParts.get(index), new HashMem(), 0, new HashSharing(), new HashMotifs());
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
    private static HashSharing computeSharing(ArrayList<CompositePart> goalParts) {
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
