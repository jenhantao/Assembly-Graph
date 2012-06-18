package originalsds;

import java.util.ArrayList;

/**
 *
 * @author vvasilev
 */
public class SDSPlusAlgorithm {

    public JointBinaryForest createAsmTreeMultipleGoalParts(ArrayList<CompositePart> goalPartsOrig, ArrayList<CompositePart> requiredOrig, ArrayList<CompositePart> recommendedOrig) throws Exception {
        ArrayList<CompositePart> goalParts = (ArrayList<CompositePart>) goalPartsOrig.clone();
        ArrayList<CompositePart> recommended = (ArrayList<CompositePart>) recommendedOrig.clone();
        ArrayList<CompositePart> required = (ArrayList<CompositePart>) requiredOrig.clone();

        SDSAlgorithm sds = new SDSAlgorithm();
        ArrayList<Tree> requiredTrees = sds.createAsmTreeMultipleGoalParts(required, new HashMem());

        HashMem requiredHash = new HashMem();
        for (Tree tree: requiredTrees) {
            tree.resetCost();
            requiredHash.put(tree.getPart(), tree);
        }

        ArrayList<Tree> finalTrees = sds.createAsmTreeMultipleGoalParts(goalParts, requiredHash);
        return sds.convertTo2ab(finalTrees);
    }

}
