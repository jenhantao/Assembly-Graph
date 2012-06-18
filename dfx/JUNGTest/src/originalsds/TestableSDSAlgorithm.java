package originalsds;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author vvasilev
 */
public class TestableSDSAlgorithm {

    HashMap<String, CompositePart> _substitutes = new HashMap<String, CompositePart>();
    ArrayList<String> _basicParts = new ArrayList<String>();
    private SecureRandom random = new SecureRandom();

    private String nextId() {
        return new BigInteger(130, random).toString(32);
    }

    private CompositePart substitute(CompositePart goalPart, String id, int start, int end) {
        CompositePart preIntermediate = goalPart.subpart(0, start);
        CompositePart postIntermediate = goalPart.subpart(end, goalPart.size());

        CompositePart cp = new CompositePart();
        BasicPart bp = new BasicPart(id, 0, "SUB");
        cp.add(bp);

        CompositePart newGoalPart = new CompositePart();
        ArrayList newParts = new ArrayList();
        newParts.add(preIntermediate);
        newParts.add(cp);
        newParts.add(postIntermediate);
        newGoalPart.combine(newParts);
        return newGoalPart;
    }

    private void removeDuplicateEntries(ArrayList list) {
        HashSet hs = new HashSet();
        hs.addAll(list);
        list.clear();
        list.addAll(hs);
    }

    private void removeRedundantParts(ArrayList<CompositePart> goalParts, ArrayList<CompositePart> required, ArrayList<CompositePart> recommended) {
        // Remove duplicate entries from goalParts
        removeDuplicateEntries(goalParts);

        // Remove duplicate entries from required
        removeDuplicateEntries(required);

        // Remove duplicate entries from recommended
        removeDuplicateEntries(recommended);

        // Remove all required parts which are in the goal parts list
        for (int index = 0; index < required.size(); index++) {
            CompositePart req = required.get(index);
            if (goalParts.contains(req)) {
                required.remove(req);
                index--;
            }
        }
        // Remove all recommended parts which are in the goal parts list
        for (int index = 0; index < recommended.size(); index++) {
            CompositePart rec = recommended.get(index);
            if (goalParts.contains(rec)) {
                recommended.remove(rec);
                index--;
            }
        }
        // Remove all recommended duplicate parts from the recommended list
        for (int index = 0; index < required.size(); index++) {
            CompositePart req = required.get(index);
            if (recommended.contains(req)) {
                recommended.remove(req);
            }
        }
    }

    private String getKeyByValue(HashMap<String, CompositePart> hash, CompositePart intermediate) {
        for (Map.Entry<String, CompositePart> e : _substitutes.entrySet()) {
            String key = e.getKey();
            CompositePart value = e.getValue();

            if (value.equals(intermediate)) {
                return key;
            }
        }
        return null;
    }

    private ArrayList<CompositePart> makeSubstitutions(ArrayList<CompositePart> goalParts, ArrayList<CompositePart> required, ArrayList<CompositePart> recommended) throws Exception {

        ArrayList<CompositePart> newGoalParts = new ArrayList<CompositePart>();

        // Save a list of all basic parts that appear in the goalPart list
        // to avoid naming conflict during the substitution
        for (CompositePart goalPart : goalParts) {
            for (int index = 0; index < goalPart.size(); index++) {
                _basicParts.add(goalPart.get(index).getName());
            }
        }

        removeRedundantParts(goalParts, required, recommended);

        // Create uniques ID's for all recommended parts and save them
        for (CompositePart req : required) {
            String id = null;
            do {
                id = nextId();
            } while (_substitutes.containsKey(id) || _basicParts.contains(id));
            _substitutes.put(id, req);
        }

        for (CompositePart goalPart : goalParts) {
            CompositePart newGoalPart = new CompositePart();
            for (int start = 0; start < goalPart.size(); start++) {
                boolean foundIt = false;
                String id = null;
                for (int end = start + 2; end <= goalPart.size(); end++) {
                    CompositePart intermediate = goalPart.subpart(start, end);

                    id = getKeyByValue(_substitutes, intermediate);

                    // The current intermediate part was not in the required list
                    // therefore was not found in _substitutes either
                    if (id == null) {
                        continue;
                    }

                    foundIt = true;

                    start = end - 1;
                    break;
                }

                if (foundIt == false) {
                    newGoalPart.addBasicPart(goalPart.get(start));
                } else {
                    newGoalPart.addBasicPart(new BasicPart(id, 0, "SUB"));
                }
            }
            newGoalParts.add(newGoalPart);
        }
        return newGoalParts;
    }

    public JointBinaryForest createAsmTreeMultipleGoalParts(ArrayList<CompositePart> goalPartsOrig, HashMem hashPartLibrary, ArrayList<CompositePart> requiredOrig, ArrayList<CompositePart> recommendedOrig) throws Exception {
        ArrayList<CompositePart> goalParts = (ArrayList<CompositePart>) goalPartsOrig.clone();
        ArrayList<CompositePart> recommended = (ArrayList<CompositePart>) recommendedOrig.clone();
        ArrayList<CompositePart> required = (ArrayList<CompositePart>) requiredOrig.clone();


        ArrayList<CompositePart> mappedGoalParts = makeSubstitutions(goalParts, required, recommended);
        ArrayList<CompositePart> allParts = new ArrayList<CompositePart>();

        allParts.addAll(mappedGoalParts);
        allParts.addAll(required);
        allParts.addAll(recommended);

        SDSAlgorithm sds = new SDSAlgorithm();
        ArrayList<Tree> trees = sds.createAsmTreeMultipleGoalParts(allParts, hashPartLibrary);

        HashMap<String, Tree> requiredTrees = new HashMap<String, Tree>();

        for (int ind = 0; ind < trees.size(); ind++) {
            Tree tree = trees.get(ind);
            CompositePart part = tree.getPart();
            String id = getKeyByValue(_substitutes, part);
            if (id != null) {
                requiredTrees.put(id, tree);
            }
            if (!mappedGoalParts.contains(part)) {
                trees.remove(tree);
                ind--;
            }
        }

        for (Tree tree : trees) {
            CompositePart goalPart = tree.getPart();

            for (int index = 0; index < goalPart.size(); index++) {
                String id = goalPart.get(index).getName();

                if (!_substitutes.containsKey(id)) {
                    continue;
                }

                tree.substituteNode(id, requiredTrees.get(id));
            }
        }

        return sds.convertTo2ab(trees);
    }
}
