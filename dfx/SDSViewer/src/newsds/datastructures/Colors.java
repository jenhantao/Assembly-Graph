/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newsds.datastructures;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author vvasilev
 */
public class Colors {
    private static ArrayList<String> _colors = null;

    public Colors() {
    }

    public static String getLeftColor(String color) {
        if (color.equals("CK")) {
            return "CA";
        } else if (color.equals("CA")) {
            return "CK";
        } else if (color.equals("KA")) {
            return "KC";
        } else if (color.equals("KC")) {
            return "KA";
        } else if (color.equals("AC")) {
            return "AK";
        } else if (color.equals("AK")) {
            return "AC";
        } else {
            return null;
        }
    }

    public static String getRightColor(String color) {
        if (color.equals("CK")) {
            return "AK";
        } else if (color.equals("CA")) {
            return "KA";
        } else if (color.equals("KA")) {
            return "CA";
        } else if (color.equals("KC")) {
            return "AC";
        } else if (color.equals("AC")) {
            return "KC";
        } else if (color.equals("AK")) {
            return "CK";
        } else {
            return null;
        }
    }

    public static void createColors() {
        if (_colors != null) return;
        _colors = new ArrayList<String>(Arrays.asList("CK", "CA", "KC", "KA", "AC", "AK"));
    }

    public static ArrayList<String> getColors() {
        createColors();
        return _colors;
    }

    public static int getColorCount() {
        createColors();
        return _colors.size();
    }
}
