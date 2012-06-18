/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package originalsds;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author vvasilev
 */
public class HashMotifs extends HashMap<String, Double> {
    public HashMotifs() {
        super();
    }

    public boolean containsPart(String part) {
        return this.containsKey(part);
    }

    public double getSharing(String part) {
        if (this.containsKey(part))
            return this.get(part);
        else
            return 0;
    }

    @Override
    public String toString() {
        String result = "";
        Iterator it = this.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (this.get(key) > 0) result += key + "=" + this.get(key) + ", ";
        }
        return result;
    }
}
