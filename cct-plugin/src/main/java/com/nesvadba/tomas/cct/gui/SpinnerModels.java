package com.nesvadba.tomas.cct.gui;

import java.util.Map;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.nesvadba.tomas.cct.enums.FilterProps;
/**
 * Iniciator spinnner modelu
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 *
 */
public class SpinnerModels {

    private SpinnerNumberModel minSizeSM = new SpinnerNumberModel();
    private SpinnerNumberModel maxSizeSM = new SpinnerNumberModel();

    private SpinnerNumberModel minHeightSM = new SpinnerNumberModel();
    private SpinnerNumberModel maxHeightSM = new SpinnerNumberModel();

    private SpinnerNumberModel minIntensitySM = new SpinnerNumberModel();
    private SpinnerNumberModel maxIntensitySM = new SpinnerNumberModel();

    private SpinnerNumberModel minElongationSM = new SpinnerNumberModel();
    private SpinnerNumberModel maxElongationSM = new SpinnerNumberModel();

    private SpinnerNumberModel minRoundSM = new SpinnerNumberModel();
    private SpinnerNumberModel maxRoundSM = new SpinnerNumberModel();

    private SpinnerNumberModel minAvgIntSM = new SpinnerNumberModel();
    private SpinnerNumberModel maxAvgIntSM = new SpinnerNumberModel();

    // SIZE MODELS
    public void initSizeSM(JSpinner minSpinner, JSpinner maxSpinner, Map<FilterProps, Integer> filterProperties) {
        minSizeSM.setMinimum(0);
        minSizeSM.setMaximum(999999999);
        minSizeSM.setValue(500);
        minSizeSM.setStepSize(100);
        filterProperties.put(FilterProps.SIZE_MIN, 500);
        minSpinner.setModel(minSizeSM);

        maxSizeSM.setMinimum(0);
        maxSizeSM.setMaximum(999999999);
        maxSizeSM.setValue(1000);
        maxSizeSM.setStepSize(100);
        filterProperties.put(FilterProps.SIZE_MAX, 1000);
        maxSpinner.setModel(maxSizeSM);

    }

    // HEIGHT MODELS
    public void initHeightSM(JSpinner minSpinner, JSpinner maxSpinner, Map<FilterProps, Integer> filterProperties) {

        minHeightSM.setMinimum(0);
        minHeightSM.setMaximum(999999999);
        minHeightSM.setValue(15);
        filterProperties.put(FilterProps.HEIGHT_MIN, 15);
        minSpinner.setModel(minHeightSM);

        maxHeightSM.setMinimum(0);
        maxHeightSM.setMaximum(999999999);
        maxHeightSM.setValue(100);
        filterProperties.put(FilterProps.HEIGHT_MAX, 100);
        maxSpinner.setModel(maxHeightSM);
    }

    // InTENSITY
    public void initIntensitytSM(JSpinner minSpinner, JSpinner maxSpinner, Map<FilterProps, Integer> filterProperties) {

        minIntensitySM.setMinimum(0);
        minIntensitySM.setMaximum(999999999);
        minIntensitySM.setValue(0);
        minIntensitySM.setStepSize(5);
        filterProperties.put(FilterProps.INTENSITY_MIN, 0);
        minSpinner.setModel(minIntensitySM);

        maxIntensitySM.setMinimum(0);
        maxIntensitySM.setMaximum(999999999);
        maxIntensitySM.setValue(255);
        maxIntensitySM.setStepSize(5);
        filterProperties.put(FilterProps.INTENSITY_MAX, 255);
        maxSpinner.setModel(maxIntensitySM);

    }
    // ELONGATION
    public void initElongationSM(JSpinner minSpinner, JSpinner maxSpinner, Map<FilterProps, Integer> filterProperties) {

        minElongationSM.setMinimum(0);
        minElongationSM.setMaximum(999999999);
        minElongationSM.setValue(100);

        filterProperties.put(FilterProps.ELONGATION_MIN, 100);
        minSpinner.setModel(minElongationSM);

        maxElongationSM.setMinimum(0);
        maxElongationSM.setMaximum(999999999);
        maxElongationSM.setValue(9000);
        filterProperties.put(FilterProps.ELONGATION_MAX, 9000);
        maxSpinner.setModel(maxElongationSM);

    }

    // ROUNDNESS
    public void initRoundnesSM(JSpinner minSpinner, JSpinner maxSpinner, Map<FilterProps, Integer> filterProperties) {

        minRoundSM.setMinimum(0);
        minRoundSM.setMaximum(999999999);
        minRoundSM.setValue(0);
        filterProperties.put(FilterProps.ROUND_MIN, 0);
        minSpinner.setModel(minRoundSM);

        maxRoundSM.setMinimum(0);
        maxRoundSM.setMaximum(100);
        maxRoundSM.setValue(100);
        filterProperties.put(FilterProps.ROUND_MAX, 100);
        maxSpinner.setModel(maxRoundSM);

    }

    public void initAvgIntSM(JSpinner minSpinner, JSpinner maxSpinner, Map<FilterProps, Integer> filterProperties) {
        minAvgIntSM.setMinimum(0);
        minAvgIntSM.setMaximum(255);
        minAvgIntSM.setValue(127);
        minAvgIntSM.setStepSize(5);
        filterProperties.put(FilterProps.AVG_INT_MIN, 0);
        minSpinner.setModel(minAvgIntSM);

        maxAvgIntSM.setMinimum(0);
        maxAvgIntSM.setMaximum(255);
        maxAvgIntSM.setValue(255);
        maxAvgIntSM.setStepSize(5);
        filterProperties.put(FilterProps.AVG_INT_MAX, 255);
        maxSpinner.setModel(maxAvgIntSM);

    }

    // --------------------------------------------------------------------------
    // GETTERS / SETTERS
    // --------------------------------------------------------------------------

    public SpinnerNumberModel getMinSizeSM() {
        return minSizeSM;
    }

    public SpinnerNumberModel getMinElongationSM() {
        return minElongationSM;
    }

    public void setMinElongationSM(SpinnerNumberModel minElongationSM) {
        this.minElongationSM = minElongationSM;
    }

    public SpinnerNumberModel getMaxElongationSM() {
        return maxElongationSM;
    }

    public void setMaxElongationSM(SpinnerNumberModel maxElongationSM) {
        this.maxElongationSM = maxElongationSM;
    }

    public SpinnerNumberModel getMinRoundSM() {
        return minRoundSM;
    }

    public void setMinRoundSM(SpinnerNumberModel minRoundSM) {
        this.minRoundSM = minRoundSM;
    }

    public SpinnerNumberModel getMaxRoundSM() {
        return maxRoundSM;
    }

    public void setMaxRoundSM(SpinnerNumberModel maxRoundSM) {
        this.maxRoundSM = maxRoundSM;
    }

    public void setMinSizeSM(SpinnerNumberModel minSizeSM) {
        this.minSizeSM = minSizeSM;
    }

    public SpinnerNumberModel getMaxSizeSM() {
        return maxSizeSM;
    }

    public void setMaxSizeSM(SpinnerNumberModel maxSizeSM) {
        this.maxSizeSM = maxSizeSM;
    }

    public SpinnerNumberModel getMinHeightSM() {
        return minHeightSM;
    }

    public void setMinHeightSM(SpinnerNumberModel minHeightSM) {
        this.minHeightSM = minHeightSM;
    }

    public SpinnerNumberModel getMaxHeightSM() {
        return maxHeightSM;
    }

    public void setMaxHeightSM(SpinnerNumberModel maxHeightSM) {
        this.maxHeightSM = maxHeightSM;
    }

    public SpinnerNumberModel getMinIntensitySM() {
        return minIntensitySM;
    }

    public void setMinIntensitySM(SpinnerNumberModel minIntensitySM) {
        this.minIntensitySM = minIntensitySM;
    }

    public SpinnerNumberModel getMaxIntensitySM() {
        return maxIntensitySM;
    }

    public void setMaxIntensitySM(SpinnerNumberModel maxIntensitySM) {
        this.maxIntensitySM = maxIntensitySM;
    }

    public SpinnerNumberModel getMinAvgIntSM() {
        return minAvgIntSM;
    }

    public void setMinAvgIntSM(SpinnerNumberModel minAvgIntSM) {
        this.minAvgIntSM = minAvgIntSM;
    }

    public SpinnerNumberModel getMaxAvgIntSM() {
        return maxAvgIntSM;
    }

    public void setMaxAvgIntSM(SpinnerNumberModel maxAvgIntSM) {
        this.maxAvgIntSM = maxAvgIntSM;
    }

}
