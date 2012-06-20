/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package accessibility;

import java.util.ArrayList;
import java.util.HashMap;
import org.clothocore.api.data.Part;

/**
 *Provides utility methods for interpreting Clotho composite parts
 * @author Tao
 */
public class ClothoReader {

    public static ArrayList<Part> getComposition(org.clothocore.api.data.Part compositePart) throws Exception {
        ArrayList<Part> toReturn = new ArrayList<Part>();
        if (compositePart.getPartType().equals(Part.partType.Basic)) {
            throw (new Exception("parseForBasicPartAttributes should only be invoked using a composite Clotho part"));
        } else {
            ArrayList<org.clothocore.api.data.Part> composition = compositePart.getCompositeParts();
            for (int i = 0; i < composition.size(); i++) {
                Part currentPart = composition.get(i);
                if (currentPart.getPartType().equals(Part.partType.Basic)) {
                    toReturn.add(currentPart);
                } else {
                    toReturn = getCompositionHelper(currentPart, toReturn);
                }
            }
        }
        return toReturn;
    }
    //helper for recursion method to discover all basic parts

    private static ArrayList<Part> getCompositionHelper(org.clothocore.api.data.Part somePart, ArrayList<Part> partsList) throws Exception {
        ArrayList<Part> toReturn = partsList;
        Part compositePart = somePart;
        if (compositePart.getPartType().equals(Part.partType.Basic)) {
            throw (new Exception("parseForBasicPartAttributesHelper should only be invoked using a composite Clotho part"));
        } else {
            ArrayList<org.clothocore.api.data.Part> composition = compositePart.getCompositeParts();
            for (int i = 0; i < composition.size(); i++) {
                Part currentPart = composition.get(i);
                if (currentPart.getPartType().equals(Part.partType.Basic)) {
                    toReturn.add(currentPart);
                } else {
                    toReturn = getCompositionHelper(currentPart, toReturn);
                }
            }

            return toReturn;
        }
    }

    public static HashMap<String, PartAttributes> getPartAttributes(ArrayList<Part> compositeParts){
        HashMap<String, PartAttributes> toReturn = new HashMap<String, PartAttributes>();
        for (int i = 0; i < compositeParts.size(); i++) {
            Part currentPart = compositeParts.get(i);
            PartAttributes currentAttributes = new PartAttributes(currentPart.getShortDescription(), currentPart.getSeq().seqLength());
            toReturn.put(currentPart.getName(), currentAttributes);
        }



        return toReturn;
    }
}
