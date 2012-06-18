package helper;

import java.util.HashMap;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vvasilev
 */
public class HashStringListInteger extends HashMap<StringList, Integer> {

    public boolean containsPart(StringList parts) {
        return this.containsKey(parts);
    }

    public int get(StringList parts) {
        if (this.containsKey(parts)) {
            return this.get(parts);
        } else {
            return 0;
        }
    }
}
