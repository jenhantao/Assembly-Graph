/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jungtest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import originalsds.BasicPart;
import originalsds.CompositePart;

/**
 *
 * @author vvasilev
 */
public class CPFReader {

    ArrayList<CompositePart> _goalParts = new ArrayList<CompositePart>();
    ArrayList<String[]> _partList = new ArrayList<String[]>();
    HashMap<String, PartAttributes> _basicParts = new HashMap<String, PartAttributes>();
    String _fileContents = "";

    public CPFReader(String fileName) throws Exception {
        try {
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(fileName);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                _fileContents += strLine + "\n";
                parseLine(strLine);
            }
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        initGoalParts();
    }

    public ArrayList<CompositePart> getGoalParts() {
        return _goalParts;
    }

    public String getFileContents() {
        return _fileContents;
    }

    private void parseLine(String line) {
        line = line.trim();
        if (line.length() == 0) {
            return;
        }
        if (line.startsWith("#")) {
            _partList.add(line.substring(1).split("\\."));
            return;
        }

        String[] partAttr = line.split(",");
        _basicParts.put(partAttr[0], new PartAttributes(partAttr[1], Integer.parseInt(partAttr[2])));
    }

    private void initGoalParts() throws Exception {
        for (int ind = 0; ind < _partList.size(); ind++) {
            CompositePart compPart = new CompositePart();
            for (int pos = 0; pos < _partList.get(ind).length; pos++) {
                String name = _partList.get(ind)[pos];

                if (!_basicParts.containsKey(name)) {
                    throw new Exception("Missing basic part attributes from input file: " + name);
                }

                String type = _basicParts.get(name).getType();
                int length = _basicParts.get(name).getLength();
                compPart.add(new BasicPart(name, length, type));
            }
            _goalParts.add(compPart);
        }
    }
}
