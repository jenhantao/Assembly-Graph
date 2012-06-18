/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testability;

import java.util.ArrayList;
import originalsds.HashMotifs;
import originalsds.CompositePart;

/**
 *
 * @author vvasilev
 */
public class IntermediateFinder implements Finder {

    public HashMotifs getWeightedCompositeParts(ArrayList<CompositePart> input) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ArrayList<CompositePart> getCompositeParts(ArrayList<CompositePart> input) {
        ArrayList<CompositePart> result = new ArrayList<CompositePart>();

        for (int ind = 0; ind < input.size(); ind++) {
            for (int start = 0; start < input.get(ind).size(); start++) {
                for (int end = start + 2; end <= input.get(ind).size(); end++) {
                    CompositePart inter = input.get(ind).subpart(start, end);
                    if (!result.contains(inter)) result.add(inter);
                }
            }
        }

        return result;
    }
}
