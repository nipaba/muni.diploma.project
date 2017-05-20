package com.nesvadba.tomas.cct.gui;
/**
 * Vysledky porovnani s maskou
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 *
 */
public class RegistrationMeasurement {

    private int measureType1;
    private int measureType2;

    private int param1;
    private int param2;

    private String tree;

    private int filteredSize;
    private int filteredMiss;
    private int maskHits;
    private int maskMiss;
    private int tp;
    private int fp;
    private int fn;

    public int getMeasureType1() {
        return measureType1;
    }
    public void setMeasureType1(int measureType1) {
        this.measureType1 = measureType1;
    }
    public int getMeasureType2() {
        return measureType2;
    }
    public void setMeasureType2(int measureType2) {
        this.measureType2 = measureType2;
    }
    public String getTree() {
        return tree;
    }
    public void setTree(String tree) {
        this.tree = tree;
    }
    public int getFilteredSize() {
        return filteredSize;
    }
    public void setFilteredSize(int filteredSize) {
        this.filteredSize = filteredSize;
    }
    public int getFilteredMiss() {
        return filteredMiss;
    }
    public void setFilteredMiss(int filteredMiss) {
        this.filteredMiss = filteredMiss;
    }
    public int getMaskHits() {
        return maskHits;
    }
    public void setMaskHits(int maskHits) {
        this.maskHits = maskHits;
    }
    public int getMaskMiss() {
        return maskMiss;
    }
    public void setMaskMiss(int maskMiss) {
        this.maskMiss = maskMiss;
    }
    public int getTp() {
        return tp;
    }
    public void setTp(int tp) {
        this.tp = tp;
    }
    public int getFp() {
        return fp;
    }
    public void setFp(int fp) {
        this.fp = fp;
    }
    public int getFn() {
        return fn;
    }
    public void setFn(int fn) {
        this.fn = fn;
    }

    public int getParam1() {
        return param1;
    }
    public void setParam1(int param1) {
        this.param1 = param1;
    }
    public int getParam2() {
        return param2;
    }
    public void setParam2(int param2) {
        this.param2 = param2;
    }
    @Override
    public String toString() {
        return "RegistrationMeasurement [measureType1=" + measureType1 + ", measureType2=" + measureType2 + ", tree=" + tree + ", filteredSize=" + filteredSize + ", filteredMiss=" + filteredMiss + ", maskHits=" + maskHits + ", maskMiss=" + maskMiss + ", tp=" + tp + ", fp=" + fp + ", fn=" + fn + "]";
    }
}
