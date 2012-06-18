/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vvasilev
 */
public class ListPartitioner {

    private List _partitions;

    public ListPartitioner() {
    }

    private void partition(List list, Tree tree, int count) {

        if (count == 1) {
            tree.children.add(new Tree(list));
            return;
        }

        int listSize = list.size();
        for (int index = 1; index <= listSize - count + 1; index++) {
            Tree child = new Tree(list.subList(0, index));
            tree.children.add(child);
            partition(list.subList(index, listSize), child, count - 1);
        }
    }

    private void traverse(Tree tree, List list) {
        list.add(tree.value);
        if (!tree.children.isEmpty()) {
            for (Tree child : tree.children) {
                traverse(child, new ArrayList(list));
            }
        } else {
            _partitions.add(list);
        }
//        list.remove(list.size() - 1);
    }

    public List getPartitions(List list, int count) {
        _partitions = new ArrayList();
        Tree root = new Tree();
        partition(list, root, count);
        for (Tree child : root.children) {
            traverse(child, new ArrayList());
        }
        return _partitions;
    }

    class Tree {

        public List value = null;
        public ArrayList<Tree> children = new ArrayList<Tree>();

        public Tree() {
        }

        public Tree(List val) {
            value = val;
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }

    public static void main(String[] argv) {
        ArrayList list = new ArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);

        ListPartitioner part = new ListPartitioner();

        List results = part.getPartitions(list, 2);
    }
}
