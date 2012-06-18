/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testability;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import originalsds.HashMotifs;
import originalsds.CompositePart;

/**
 *
 * @author vvasilev
 */
public class FunctionallyTestableIntermediateFinder implements Finder {

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
    
    public HashMotifs getWeightedCompositeParts(ArrayList<CompositePart> input) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ArrayList<CompositePart> rec(CompositePart input) {
        String last = "";
        for (int ind = 0; ind < input.size(); ind++) {
            String type = input.get(ind).getType();
            if ((last.equals("") && !type.equals("Promoter"))
                    || (last.equals("Promoter") && !type.equals("RBS"))
                    || (last.equals("RBS") && !type.equals("Gene"))
                    || (last.equals("Gene") && !type.equals("RBS") && !type.equals("Terminator"))) {
                return rec(input.subpart(1, input.size()));
            }

            if (type.equals("Terminator")) {
                ArrayList<CompositePart> list = new ArrayList<CompositePart>();
                CompositePart cp = new CompositePart();
                cp.copy(input.subpart(0, ind + 1));
                list.add(cp);
                list.addAll(rec(input.subpart(1, input.size())));
                return list;
            }
            last = type;
        }
        return new ArrayList<CompositePart>();
    }

    public ArrayList<CompositePart> getCompositeParts(ArrayList<CompositePart> input) {
       ArrayList<CompositePart> result = new ArrayList<CompositePart>();

       for (int ind = 0; ind < input.size(); ind++) {
           result.addAll(rec(input.get(ind)));
       }

       return result;
    }

}
