/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package originalsds;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author vvasilev
 */
public class PuppeteerWriter {
    public HashMap<Tree, String> _names = new HashMap<Tree, String>();
    public ArrayList<ArrayList<Tree>> stages = new ArrayList<ArrayList<Tree>>();

    public String rdName(String sampleName, boolean isLefty) {
        if (isLefty) {
            return "l.rd." + sampleName;
        } else {
            return "r.rd." + sampleName;
        }
    }

    public String lgName(String leftName, String rightName) {
        return "lg(" + leftName + "," + rightName + ")";
    }

    public String ligate(String leftName, String rightName, int factor) {
        String newName = lgName(leftName, rightName);
        float waterAmount = 22 * factor;
        float bufferAmount = 2 * factor;
        float leftAmount = 10 * factor;
        float rightAmount = 10 * factor;
        float ligaseAmount = 2 * factor;

        return "ligate(" + Float.toString(waterAmount) + ",'10x Ligase Buffer'," + Float.toString(bufferAmount) + ",'" + leftName + "'," + Float.toString(leftAmount) + ",'" + rightName + "'," + Float.toString(rightAmount) + ",'Ligase'," + Float.toString(ligaseAmount) + ",'" + newName + "')";
    }

    public String restrictionDigest(String sampleName, int factor, boolean isLefty) {
        String newName = rdName(sampleName, isLefty);
        float waterAmount;
        float buffer1Amount = 5 * factor;
        float buffer2Amount = 1 * factor;
        float sampleAmount = 20 * factor;
        float ecoriAmount = 1 * factor;
        float speiAmount = 2 * factor;
        float xbalAmount = 1 * factor;

        if (isLefty) {
            waterAmount = 21 * factor;
            return "res_dig(" + Float.toString(waterAmount) + ",'NEB Buffer 2'," + Float.toString(buffer1Amount) + ",'100x BSA'," + Float.toString(buffer2Amount) + ",'" + sampleName + "'," + Float.toString(sampleAmount) + ",'EcoRI'," + Float.toString(ecoriAmount) + ",'SpeI'," + Float.toString(speiAmount) + ",'" + newName + "')";
        } else {
            waterAmount = 22 * factor;
            return "res_dig(" + Float.toString(waterAmount) + ",'NEB Buffer 2'," + Float.toString(buffer1Amount) + ",'100x BSA'," + Float.toString(buffer2Amount) + ",'" + sampleName + "'," + Float.toString(sampleAmount) + ",'EcoRI'," + Float.toString(ecoriAmount) + ",'Xbal'," + Float.toString(xbalAmount) + ",'" + newName + "')";
        }
    }

    public void traverseTree(Tree tree, int stage) {
        if (tree.getLeft() == null && tree.getRight() == null) {
            return;
        }

        if (stages.size() <= stage) {
            stages.add(new ArrayList<Tree>());
        }

        stages.get(stage).add(tree);

        traverseTree(tree.getLeft(), stage + 1);
        traverseTree(tree.getRight(), stage + 1);

        System.out.println(Integer.toString(stage) + " " + tree.getPart().toString());
    }

    public String getRdString(ArrayList<Tree> stage) {
        String result = "";

        for (int index = 0; index < stage.size(); index++) {
            Tree currentTree = stage.get(index);
            Tree leftTree = currentTree.getLeft();
            Tree rightTree = currentTree.getRight();
            String leftName = leftTree.getPart().toString();
            String rightName = rightTree.getPart().toString();
            if (!_names.containsKey(leftTree)) {
                _names.put(leftTree, leftTree.getPart().toString());
            } else {
               leftName = _names.get(leftTree);
            }
            if (!_names.containsKey(rightTree)) {
                _names.put(rightTree, rightTree.getPart().toString());
            } else {
               rightName = _names.get(rightTree);
            }

            //String leftName = rdName(currentTree.getLeft().getPart().toString(), true);
            //String rightName = rdName(currentTree.getRight().getPart().toString(), false);

            result += restrictionDigest(leftName, 1, true) + "\n";
            result += restrictionDigest(rightName, 1, false) + "\n";
        }

        return result;
    }



    public String getLgString(ArrayList<Tree> stage) {
        String result = "";

        for (int index = 0; index < stage.size(); index++) {
            Tree currentTree = stage.get(index);
            Tree leftTree = currentTree.getLeft();
            Tree rightTree = currentTree.getRight();
            String leftName = rdName(_names.get(leftTree), true);
            String rightName = rdName(_names.get(rightTree), false);
            String newName = lgName(leftName, rightName);
            result += ligate(leftName, rightName, 1) + "\n";
            _names.put(currentTree, newName);
        }

        return result;
    }

    public void createFile(String fileName, String text) {
        try {
            // Create file
            FileWriter fstream = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(text);
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void writeToFile(Tree tree, String fileName) {
        String result = "";
        traverseTree(tree, 0);
        Collections.reverse(stages);
        for (int stage = 0; stage < stages.size(); stage++) {
            String rdString = getRdString(stages.get(stage));
            String lgString = getLgString(stages.get(stage));
            result += rdString + "\n";
            result += lgString + "\n";

            createFile(fileName + ".stage" + Integer.toString(stage + 1) + ".rd.py", rdString);
            createFile(fileName + ".stage" + Integer.toString(stage + 1) + ".lg.py", lgString);

            //break;
        }
        createFile(fileName + ".py", result);
    }
}
