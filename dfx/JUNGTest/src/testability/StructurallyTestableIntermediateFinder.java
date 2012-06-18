/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testability;

import java.util.ArrayList;
import originalsds.CompositePart;
import originalsds.HashMotifs;

/**
 *
 * @author vvasilev
 */
public class StructurallyTestableIntermediateFinder implements Finder {

    private int _minLength = 200;

    public HashMotifs getWeightedCompositeParts(ArrayList<CompositePart> input) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLength(int length) {
        _minLength = length;
    }

    public ArrayList<CompositePart> getCompositeParts(ArrayList<CompositePart> input) {
        ArrayList<CompositePart> result = new ArrayList<CompositePart>();
        for (int ind = 0; ind < input.size(); ind++) {
            CompositePart current = input.get(ind);
            for (int start = 0; start < current.size(); start++) {
                for (int end = current.size(); end > start + 1; end--) {
                    CompositePart subpart = current.subpart(start, end);
                    if (subpart.getLength() < _minLength) break;

                    result.add(subpart);
                }
            }
        }
        return result;
    }

}
