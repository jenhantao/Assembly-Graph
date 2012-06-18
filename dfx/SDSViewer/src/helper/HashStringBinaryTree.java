/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.util.HashMap;
import newsds.datastructures.SDSBinaryTree;

/**
 *
 * @author vvasilev
 */
public class HashStringBinaryTree extends HashMap<String, SDSBinaryTree> {

    public HashStringBinaryTree() {
        super();
    }

    public HashStringBinaryTree(HashStringBinaryTree hash1, HashStringBinaryTree hash2) {
        super();
        this.putAll(hash1);
        this.putAll(hash2);
    }
}
