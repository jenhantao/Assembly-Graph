/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jungtest;

import java.text.DecimalFormat;

/**
 *
 * @author vvasilev
 */
public class Statistics {
    private int _stages;
    private int _steps;
    private int _asmTime;
    private int _cost;
    private int _goalParts;
    private int _stIntermediates;
    private int _ftIntermediates;
    private long _executionTime;

    public void setStages(int stages) {
        _stages = stages;
    }
    public void setSteps(int steps) {
        _steps = steps;
    }
    public void setAsmTime(int asmTime) {
        _asmTime = asmTime;
    }
    public void setCost(int cost) {
        _cost = cost;
    }
    public void setGoalParts(int goalParts) {
        _goalParts = goalParts;
    }
    public void setStIntermediates(int stIntermediates) {
        _stIntermediates = stIntermediates;
    }
    public void setFtIntermediates(int ftIntermediates) {
        _ftIntermediates = ftIntermediates;
    }
    public void setExecutionTime(long executionTime) {
        _executionTime = executionTime;
    }

    public String getStages() {
        return new Integer(_stages).toString();
    }
    public String getSteps() {
        return new Integer(_steps).toString();
    }
    public String getAsmTime() {
        return new Integer(_asmTime).toString();
    }
    public String getCost() {
        return new Integer(_cost).toString();
    }
    public String getGoalParts() {
        return new Integer(_goalParts).toString();
    }
    public String getStIntermediates() {
        return new Integer(_stIntermediates).toString();
    }
    public String getFtIntermediates() {
        return new Integer(_ftIntermediates).toString();
    }
    public String getExecutionTime() {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(_executionTime) + "ms";
    }
}
