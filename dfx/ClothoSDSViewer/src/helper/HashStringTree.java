/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.util.HashMap;
import newsds.datastructures.SDSTree;

/**
 *
 * @author vvasilev
 */
public class HashStringTree extends HashMap<String, SDSTree> {

    public HashStringTree() {
        super();
    }

    public HashStringTree(HashStringTree hash1, HashStringTree hash2) {
        super();
        this.putAll(hash1);
        this.putAll(hash2);
    }
}
