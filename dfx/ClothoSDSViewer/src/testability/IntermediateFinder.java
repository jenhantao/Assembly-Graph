/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testability;

import helper.StringList;
import java.util.ArrayList;

/**
 *
 * @author vvasilev
 */
public class IntermediateFinder {

    public ArrayList<StringList> getCompositeParts(ArrayList<StringList> input) {
        ArrayList<StringList> result = new ArrayList<StringList>();

        for (int ind = 0; ind < input.size(); ind++) {
            StringList currentInput = input.get(ind);
            int size = currentInput.size();
            for (int start = 0; start < size; start++) {
                for (int end = start + 2; end <= size; end++) {
                    StringList inter = currentInput.subList(start, end);
                    if (!result.contains(inter)) result.add(inter);
                }
            }
        }

        return result;
    }
}
