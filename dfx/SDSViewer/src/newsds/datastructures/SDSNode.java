/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package newsds.datastructures;

import helper.StringList;

/**
 *
 * @author vvasilev
 */
public class SDSNode {
    private StringList _part;
    private int _stages;
    private int _steps;
    private int _sharing;
    private int _recommended;
    private String _color = null;
    private boolean _isColored = false;
    private int _id = 0;

    public SDSNode() {
        resetCost();
        _part = new StringList();
        _id = 1 + (int)(Math.random()*1000);
    }

    public boolean isColored() {
        return _color != null;
    }

    public int getId() {
        return _id;
    }

    public final void resetCost() {
        _stages = 0;
        _steps = 0;
        _sharing = 0;
    }

    public void setColor(String color) {
        _color = color;
    }

    public void resetColor() {
        _color = null;
    }

    public String getColor() {
        return _color;
    }

    public int getStages() {
        return _stages;
    }

    public int getSteps() {
        return _steps;
    }

    public int getSharing() {
        return _sharing;
    }

    public int getRecommended() {
        return _recommended;
    }

    public StringList getPart() {
        return _part;
    }

    public void setStages(int stages) {
        _stages = stages;
    }

    public void setSteps(int steps) {
        _steps = steps;
    }

    public void setSharing(int sharing) {
        _sharing = sharing;
    }

    public void setRecommended(int recommended) {
        _recommended = recommended;
    }

    public void setPart(StringList part) {
        _part = part;
    }
}
