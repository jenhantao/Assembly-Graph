/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package modifiedsds;
import java.io.*;
import java.util.Scanner;


/**
 *
 * @author evanappleton
 */
public class ConvertFormat {

  /** Constructor. */
  ConvertFormat(String inFile, String outFile){
    iFile = inFile;
    oFile = outFile;
    textin = "";
  }

  /** Read the contents of the original SDS format file. */
  void readOriginal() throws IOException {
    log("Reading from file.");
    StringBuilder text = new StringBuilder();
    String NL = System.getProperty("line.separator");
    Scanner scanner = new Scanner(new FileInputStream(iFile));
    try {
        while (scanner.hasNextLine()){
            text.append(scanner.nextLine() + NL);
        }
    }
    finally{
        scanner.close();
    }
    textin = text.toString();
    //log("Text read in: " + textin);
  }

  /** Convert from the read-in text to the new out format */
  void convert() {
    String t1 = textin.replaceAll("#","").replaceAll("\\{","").replaceAll("\\}","");
    StringBuilder text2 = new StringBuilder();
    String NL = System.getProperty("line.separator");
    String[] eachLine = t1.split(NL);      
    for (int i = 0; i < eachLine.length; i++){
        text2.append("#" + eachLine[i] + NL);
        String[] eachPart = eachLine[i].split("\\.");
        for (int j = 0; j < eachPart.length; j++){
            if (eachPart[j].toUpperCase().startsWith("P")){
                type = "Promoter";
                size = "80";
            }
            else if(eachPart[j].toUpperCase().startsWith("R")) {
                type = "RBS";
                size = "12";
            }
            else if(eachPart[j].toUpperCase().startsWith("B")) {
                type = "Terminator";
                size = "100";
            }
            else{
                type = "Gene";
                size = "1000";
            }
        text2.append(eachPart[j] + "," + type + "," + size + NL);
        }
        text2.append(NL);
    }
    textout = text2.toString();
  }

    /** Write fixed content to the given file. */
  void writeNew() throws IOException  {
    log("Writing to file named " + oFile);
    Writer out = new OutputStreamWriter(new FileOutputStream(oFile));
    try {
        out.write(textout);
    }
    finally {
        out.close();
    }
  }

  // PRIVATE
  private final String iFile;
  private final String oFile;
  private String textin;
  private String textout;
  private String type;
  private String size;

  private void log(String aMessage){
    System.out.println(aMessage);
  }
}