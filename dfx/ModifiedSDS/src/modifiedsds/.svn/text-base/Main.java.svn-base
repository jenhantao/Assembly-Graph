package modifiedsds;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author vvasilev
 */
public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        //CompositePart part = new CompositePart("abcde");

        ConvertFormat filetoconvert  = new ConvertFormat("complete_test_setSDS.txt","complete_test_setSDS2_mod.txt");
        filetoconvert.readOriginal();
        filetoconvert.convert();
        filetoconvert.writeNew();

        ArrayList<CompositePart> goalParts = new ArrayList<CompositePart>();
//        goalParts.add(new CompositePart("abcde"));
//        goalParts.add(new CompositePart("bcdede"));
//        goalParts.add(new CompositePart("bcdeg"));

        CompositePart cPart1 = new CompositePart();
        cPart1.add(new BasicPart("p1", 70, "Promoter"));
        cPart1.add(new BasicPart("p2", 80, "Promoter"));
        cPart1.add(new BasicPart("r1", 16, "RBS"));
        cPart1.add(new BasicPart("g1", 1000, "Gene"));
        cPart1.add(new BasicPart("t1", 40, "Terminator"));
        cPart1.add(new BasicPart("p3", 90, "Promoter"));
        cPart1.add(new BasicPart("p4", 65, "Promoter"));
        cPart1.add(new BasicPart("r2", 18, "RBS"));
        cPart1.add(new BasicPart("g2", 2000, "Gene"));
        cPart1.add(new BasicPart("t2", 45, "Terminator"));
        cPart1.add(new BasicPart("p5", 75, "Promoter"));
        cPart1.add(new BasicPart("r3", 16, "RBS"));
        cPart1.add(new BasicPart("g3", 1000, "Gene"));
        cPart1.add(new BasicPart("t3", 40, "Terminator"));

        // pc araC pBad rbs TetR rbs GFP term pTet rbs RFP term
        CompositePart cPart2 = new CompositePart();
        cPart2.add(new BasicPart("pC", 70, "Promoter"));
        cPart2.add(new BasicPart("araC", 1000, "RBS.Gene.Terminator"));
        cPart2.add(new BasicPart("pBad", 70, "Promoter"));
        cPart2.add(new BasicPart("rbs", 16, "RBS"));
        cPart2.add(new BasicPart("TetR", 1000, "Gene"));
        cPart2.add(new BasicPart("rbs", 16, "RBS"));
        cPart2.add(new BasicPart("GFP", 1000, "Gene"));
        cPart2.add(new BasicPart("term", 40, "Terminator"));
        cPart2.add(new BasicPart("pTet", 70, "Promoter"));
        cPart2.add(new BasicPart("rbs", 16, "RBS"));
        cPart2.add(new BasicPart("RFP", 1000, "Gene"));
        cPart2.add(new BasicPart("term", 40, "Terminator"));

        CompositePart rgt = new CompositePart();
        rgt.add(new BasicPart("pC", 70, "RBS"));
        rgt.add(new BasicPart("araC", 1000, "Gene"));
        rgt.add(new BasicPart("pBad", 70, "Terminator"));

        ArrayList<CompositePart> database = new ArrayList<CompositePart>();
        database.add(rgt);
        database.add(rgt);
        database.add(rgt);
        database.add(rgt);

        HashMotifs motifs = SDSAlgorithm.computeMotifs(database);
        //HashMotifs motifs = new HashMotifs();

        goalParts.add(cPart1);
        //ArrayList<Tree> result1 = SDSAlgorithm.createAsmTreeMultipleGoalParts(goalParts, new HashMem());
        //JointBinaryForest graph1 = SDSAlgorithm.convertTo3A(result1);
        //Tree tree1 = new Tree();
        //tree1.copyAndLink(result1.get(0));
        //System.out.println(tree1);

       // goalParts.clear();
        goalParts.add(cPart2);
        ArrayList<Tree> result2 = SDSAlgorithm.createAsmTreeMultipleGoalParts(goalParts, new HashMem(), motifs);
        JointBinaryForest graph2 = SDSAlgorithm.convertTo3A(result2);
        //Tree tree2 = new Tree();
        //tree2.copyAndLink(result2.get(0));
        //System.out.println(tree2);
        System.out.println(graph2);

//        PuppeteerWriter pw = new PuppeteerWriter();
//        pw.writeToFile(tree, "pTet");
    }

}
