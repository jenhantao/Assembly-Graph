package testability;

import java.util.ArrayList;
import newsds.datastructures.HashMotifs;
import newsds.datastructures.CompositePart;

/**
 *
 * @author vvasilev
 */
public class MotifFinder implements Finder {

    /**
     * Goes over all possible combinations of subparts in the goal part dataset
     * and computes which and how many times are shared.
     * @param goalParts Dataset of goal parts
     * @return Returns all parts which are shared among goal parts and how many
     * times they are shared.
     */
    public HashMotifs getWeightedCompositeParts(ArrayList<CompositePart> input) {
        //Determine subpart sharing numbers for all goal parts
        HashMotifs motifs = new HashMotifs();
        Double maxSharing = 0.0;

        for (int i = 0; i < input.size(); i++) {
            CompositePart part = input.get(i);

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

    public ArrayList<CompositePart> getCompositeParts(ArrayList<CompositePart> input) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
