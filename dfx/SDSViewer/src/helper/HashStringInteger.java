/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.util.HashMap;

/**
 *
 * @author vvasilev
 */
public class HashStringInteger extends HashMap<String, Integer> {

    public boolean containsPart(String part) {
        return this.containsKey(part);
    }

    public int getSharing(String part) {
        if (this.containsKey(part)) {
            return this.get(part);
        } else {
            return 0;
        }
    }
}
