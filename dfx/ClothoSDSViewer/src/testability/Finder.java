/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testability;

import java.util.ArrayList;
import newsds.datastructures.CompositePart;
import newsds.datastructures.HashMotifs;

/**
 *
 * @author vvasilev
 */
public interface Finder {
    public HashMotifs getWeightedCompositeParts(ArrayList<CompositePart> input);
    public ArrayList<CompositePart> getCompositeParts(ArrayList<CompositePart> input);
}
