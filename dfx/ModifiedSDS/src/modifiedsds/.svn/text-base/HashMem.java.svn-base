/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package modifiedsds;

import java.util.HashMap;

/**
 *
 * @author vvasilev
 */
public class HashMem extends HashMap<CompositePart, Tree> {

    public HashMem() {
        super();
    }

    public HashMem(HashMem mem1, HashMem mem2) {
        super();
        combine(mem1, mem2);
    }

    public void insert(CompositePart part, Tree tree) {
        this.put(part, tree);
    }

    public final void combine(HashMem mem1, HashMem mem2) {
        this.putAll(mem1);
        this.putAll(mem2);
    }

    public boolean hasTree(CompositePart part, int slack) {
        return this.containsKey(part);
    }

    public Tree getTree(CompositePart part) {
        return this.get(part);
    }
}
