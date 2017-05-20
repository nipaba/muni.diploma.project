package com.nesvadba.tomas.cct.gui;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.plaf.metal.MetalFileChooserUI.FilterComboBoxRenderer;

import com.nesvadba.tomas.cct.domain.CCT;
import com.nesvadba.tomas.cct.domain.Component;
import com.nesvadba.tomas.cct.domain.Point;
import com.nesvadba.tomas.cct.domain.ShapeTree;
import com.nesvadba.tomas.cct.enums.ComponentProperty;
import com.nesvadba.tomas.cct.enums.FilterProps;
import com.nesvadba.tomas.cct.filter.CCTFilter;
import com.nesvadba.tomas.cct.filter.ShapeTreeFilter;

import ij.IJ;
import ij.ImagePlus;
import ij.process.FloodFiller;
import ij.process.ImageProcessor;
/**
 * Nastroj pro porovnani masky a segmentace
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 *
 */
public class SegComparator2 {

    private static final int MIN_LABEL = 5;
    private static final int VAR_BORDER = 2;

    public static final int MEASURE_HEIGHT = 1;
    public static final int MEASURE_SIZE = 2;
    public static final int MEASURE_INTENSITY = 3;
    public static final int MEASURE_ROUND = 4;
    public static final int MEASURE_ENLONGATION = 5;

    private static final String MAX = "MAX";
    private static final String MIN = "MIN";
    private static final String SHAPE = "SHAPE";
    private static final String CUST_MAX = "CUST_MAX";
    private static final String CUST_MIN = "CUST_MIN";
    private static final String CUST_SHAPE = "CUST_SHAPE";

    private static final String SEP = ";";
    private static final String EOL = "\r\n";

    private int totalCompPix = 0;

    private ImagePlus mask;
    private Map<Integer, Component> maskComponents = new HashMap<>();
    private Map<String, Integer> TPs = new HashMap<>();
    private Map<String, Integer> FPs = new HashMap<>();
    private Map<String, Integer> FNs = new HashMap<>();

    private CCT maxTree;
    private CCT minTree;
    private ShapeTree shapeTree;

    private List<RegistrationMeasurement> measures = new ArrayList<>();
    private List<RegistrationMeasurement> concreateMeasures = new ArrayList<>();
    private int labelFount = 0;

    private StringBuilder jaccardData = new StringBuilder();
    private StringBuilder customJaccardData = new StringBuilder();
    private DecimalFormat formatter;
    public SegComparator2(String maskPath, CCT minTree, CCT maxTree, ShapeTree shapeTree) {
        // Prepare Mask Components
        prepareMask(maskPath);
        this.maxTree = maxTree;
        this.minTree = minTree;
        this.shapeTree = shapeTree;

        formatter = new DecimalFormat("#.#####");
        formatter.setRoundingMode(RoundingMode.DOWN);

    }

    public double concreteJaccard(Map<FilterProps, Integer> filterProperties, Map<ComponentProperty, Boolean> selectedFilters, boolean isMax, boolean isMin, boolean isShape) {

        int label = 0;
        String treeType = "";
        int tp = 0;
        int fp = 0;
        int fn = 0;
        double jaccard = 0;

        String type = "";
        if (isMax) {
            type = CUST_MAX;
        }
        if (isMin) {
            type = CUST_MIN;
        }
        if (isShape) {
            type = CUST_SHAPE;
        }
        for (Component c : maskComponents.values()) {

            if (isMax) {
                Set<CCT> maxTreeNode = CCTFilter.filterByProperties(maxTree, filterProperties, selectedFilters, true);
                calculateCoefs(c, maxTreeNode, type);
            }
            if (isMin) {
                Set<CCT> minTreeNode = CCTFilter.filterByProperties(minTree, filterProperties, selectedFilters, true);
                calculateCoefs(c, minTreeNode, type);
            }
            if (isShape) {
                List<ShapeTree> shapeTreeNode = ShapeTreeFilter.filterByProperties(shapeTree, filterProperties, selectedFilters, true);
                calculateCoefs(c, shapeTreeNode, type);

            }

            int tempTp = TPs.get(c.getLabel() + type);
            int tempFp = FPs.get(c.getLabel() + type);
            int tempFN = FNs.get(c.getLabel() + type);

            double tempJaccard = jaccard(tempTp, tempFp, tempFN);
            if (tempJaccard > jaccard) {
                label = c.getLabel();
                treeType = type;
                tp = tempTp;
                fp = tempFp;
                fn = tempFN;
                jaccard = tempJaccard;
            }
        }
        labelFount = label;
        return jaccard;

    }

    public Double concreteRegistration(Map<FilterProps, Integer> filterProperties, Map<ComponentProperty, Boolean> selectedFilters, boolean isMax, boolean isMin, boolean isShape) {
        int tempTotalCompPix = totalCompPix;
        Map<Integer, Component> tempMaskComponents = maskComponents;

        if (selectedFilters.get(ComponentProperty.LEFT) != null && selectedFilters.get(ComponentProperty.LEFT)) {

            maskComponents = new HashMap<>();
            ImageProcessor proc = mask.getProcessor();
            int minY = filterProperties.get(FilterProps.LEFT);
            int maxY = filterProperties.get(FilterProps.RIGHT);
            int minX = filterProperties.get(FilterProps.UP);
            int maxX = filterProperties.get(FilterProps.BOTTOM);

            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    int color = proc.get(x, y);
                    if (color >= MIN_LABEL && color < 255) {
                        if (!maskComponents.containsKey(color)) {
                            Component c = new Component();
                            c.setLabel(color);
                            maskComponents.put(color, c);
                        }
                    }
                }
            }
            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    int label = proc.get(x, y);
                    if (label >= MIN_LABEL && label < 255) {
                        totalCompPix++;
                        maskComponents.get(label).addPoint(x, y);
                    }
                }
            }

        }

        RegistrationMeasurement m = null;
        if (isMax) {
            Set<CCT> maxTreeNode = CCTFilter.filterByProperties(maxTree, filterProperties, selectedFilters, false);
            m = calculateRegistration(maxTreeNode);
        }
        if (isMin) {
            Set<CCT> minTreeNode = CCTFilter.filterByProperties(minTree, filterProperties, selectedFilters, false);
            m = calculateRegistration(minTreeNode);
        }
        if (isShape) {
            List<ShapeTree> shapeTreeNode = ShapeTreeFilter.filterByProperties(shapeTree, filterProperties, selectedFilters, false);
            m = calculateRegistration(shapeTreeNode);
        }

        int tp = m.getFilteredSize() - m.getFilteredMiss();
        int fp = m.getFilteredMiss();
        int fn = m.getMaskMiss()+ Math.abs(m.getMaskHits()-tp) ;
        
        totalCompPix = tempTotalCompPix;
        maskComponents = tempMaskComponents;

        return jaccard(tp, fn, fp);

    }

    // ===========================================================================================
    // ===========================================================================================

    public StringBuilder compareRegistration() {

        System.out.println("Registration : Size param");
        regSize();
        System.out.println("Registration : intenzity param");
        regIntensity();
        System.out.println("Registration : round param");
        regRound();
        System.out.println("Registration : elongation param");
        regElongation();
        System.out.println("Registration : height param");
        regHeight();

        System.out.println("Registration : size,height param");
        regSizeHeight();

        System.out.println("Registration : size,intensity param");
        regSizeIntensity();

        return printRegistrationHeader().append(printRegistrationResults());

    }
    // ===========================================================================================
    // ===========================================================================================
    private void regSize() {
        Map<FilterProps, Integer> filterProperties = new HashMap<>();
        Map<ComponentProperty, Boolean> selectedFilters = new HashMap<>();
        int pixels = mask.getProcessor().getPixelCount();

        for (int size = 2; size < pixels; size *= 2) {
            selectedFilters.put(ComponentProperty.SIZE, true);
            filterProperties.put(FilterProps.SIZE_MIN, size / 2);
            filterProperties.put(FilterProps.SIZE_MAX, size + size / 2);

            proccessRegistrationCalculation(MEASURE_SIZE, 0, size, -1, filterProperties, selectedFilters, false);
        }
    }

    private void regHeight() {
        Map<FilterProps, Integer> filterProperties = new HashMap<>();
        Map<ComponentProperty, Boolean> selectedFilters = new HashMap<>();

        for (int height = 0; height < 255; height += 5) {
            selectedFilters.put(ComponentProperty.G_HEIGHT, true);
            filterProperties.put(FilterProps.HEIGHT_MIN, height - 10);
            filterProperties.put(FilterProps.HEIGHT_MAX, height + 10);

            proccessRegistrationCalculation(MEASURE_HEIGHT, 0, height, -1, filterProperties, selectedFilters, false);
        }
    }

    private void regIntensity() {
        Map<FilterProps, Integer> filterProperties = new HashMap<>();
        Map<ComponentProperty, Boolean> selectedFilters = new HashMap<>();

        for (int intensity = 0; intensity < 255; intensity += 5) {
            selectedFilters.put(ComponentProperty.INTENSITY, true);
            filterProperties.put(FilterProps.INTENSITY_MIN, intensity - 10);
            filterProperties.put(FilterProps.INTENSITY_MAX, intensity + 10);

            proccessRegistrationCalculation(MEASURE_INTENSITY, 0, intensity, -1, filterProperties, selectedFilters, false);
        }
    }

    private void regRound() {
        Map<FilterProps, Integer> filterProperties = new HashMap<>();
        Map<ComponentProperty, Boolean> selectedFilters = new HashMap<>();
        for (int round = 0; round <= 100; round += 5) {
            selectedFilters.put(ComponentProperty.ROUND, true);
            filterProperties.put(FilterProps.ROUND_MIN, round - 5);
            filterProperties.put(FilterProps.ROUND_MAX, round + 5);

            proccessRegistrationCalculation(MEASURE_ROUND, 0, round, -1, filterProperties, selectedFilters, false);
        }
    }

    private void regElongation() {
        Map<FilterProps, Integer> filterProperties = new HashMap<>();
        Map<ComponentProperty, Boolean> selectedFilters = new HashMap<>();
        for (int elong = 100; elong <= 5000; elong += 100) {
            selectedFilters.put(ComponentProperty.ELONGATION, true);
            filterProperties.put(FilterProps.ELONGATION_MIN, elong - 10);
            filterProperties.put(FilterProps.ELONGATION_MAX, elong + 10);

            proccessRegistrationCalculation(MEASURE_ENLONGATION, 0, elong, -1, filterProperties, selectedFilters, false);
        }

    }

    private void regSizeHeight() {
        Map<FilterProps, Integer> filterProperties = new HashMap<>();
        Map<ComponentProperty, Boolean> selectedFilters = new HashMap<>();
        int pixels = mask.getProcessor().getPixelCount();

        for (int size = 2; size < pixels; size *= 2) {
            for (int height = 0; height < 255; height += 5) {
                selectedFilters.put(ComponentProperty.SIZE, true);
                filterProperties.put(FilterProps.SIZE_MIN, size / 2);
                filterProperties.put(FilterProps.SIZE_MAX, size + size / 2);
                selectedFilters.put(ComponentProperty.G_HEIGHT, true);
                filterProperties.put(FilterProps.HEIGHT_MIN, height - 10);
                filterProperties.put(FilterProps.HEIGHT_MAX, height + 10);
                proccessRegistrationCalculation(MEASURE_SIZE, MEASURE_HEIGHT, size, height, filterProperties, selectedFilters, false);
            }

        }
    }

    private void regSizeIntensity() {
        Map<FilterProps, Integer> filterProperties = new HashMap<>();
        Map<ComponentProperty, Boolean> selectedFilters = new HashMap<>();
        int pixels = mask.getProcessor().getPixelCount();

        for (int size = 2; size < pixels; size *= 2) {
            for (int intensity = 0; intensity < 255; intensity += 5) {
                selectedFilters.put(ComponentProperty.SIZE, true);
                filterProperties.put(FilterProps.SIZE_MIN, size / 2);
                filterProperties.put(FilterProps.SIZE_MAX, size + size / 2);

                selectedFilters.put(ComponentProperty.INTENSITY, true);
                filterProperties.put(FilterProps.INTENSITY_MIN, intensity - 10);
                filterProperties.put(FilterProps.INTENSITY_MAX, intensity + 10);
                proccessRegistrationCalculation(MEASURE_SIZE, MEASURE_INTENSITY, size, intensity, filterProperties, selectedFilters, false);
            }

        }
    }

    // ===========================================================================================
    // ===========================================================================================
    private void proccessRegistrationCalculation(int measure1, int measure2, int param1, int param2, Map<FilterProps, Integer> filterProperties, Map<ComponentProperty, Boolean> selectedFilters, boolean isConcrete) {
        Set<CCT> maxTreeNode = CCTFilter.filterByProperties(maxTree, filterProperties, selectedFilters, false);
        Set<CCT> minTreeNode = CCTFilter.filterByProperties(minTree, filterProperties, selectedFilters, false);
        List<ShapeTree> shapeTreeNode = ShapeTreeFilter.filterByProperties(shapeTree, filterProperties, selectedFilters, false);

        RegistrationMeasurement m1 = calculateRegistration(maxTreeNode);
        m1.setTree(MAX);
        m1.setMeasureType1(measure1);
        m1.setMeasureType2(measure2);
        m1.setParam1(param1);
        m1.setParam2(param2);

        RegistrationMeasurement m2 = calculateRegistration(minTreeNode);
        m2.setTree(MIN);
        m2.setMeasureType1(measure1);
        m2.setMeasureType2(measure2);
        m2.setParam1(param1);
        m2.setParam2(param2);

        RegistrationMeasurement m3 = calculateRegistration(shapeTreeNode);
        m3.setTree(SHAPE);
        m3.setMeasureType1(measure1);
        m3.setMeasureType2(measure2);
        m3.setParam1(param1);
        m3.setParam2(param2);

        if (isConcrete) {
            concreateMeasures.add(m1);
            concreateMeasures.add(m2);
            concreateMeasures.add(m3);
        } else {
            measures.add(m1);
            measures.add(m2);
            measures.add(m3);
        }

    }

    private RegistrationMeasurement calculateRegistration(List<ShapeTree> nodes) {
        Set<Integer> componentHits = new HashSet<>();

        int misCount = 0;
        int tp = 0;
        int fp = 0;
        for (ShapeTree component : nodes) {

            int left = component.getOrigNode().getProperties().get(ComponentProperty.LEFT);
            int up = component.getOrigNode().getProperties().get(ComponentProperty.TOP);

            ImageProcessor proc = mask.getProcessor();
            ImageProcessor maskProc = component.getImageProcessor();
            boolean hit = false;

            for (int x = 0; x < maskProc.getWidth(); x++) {
                for (int y = 0; y < maskProc.getHeight(); y++) {
                    if (maskProc.get(x, y) < 127) {
                        int value = proc.get(x + up - 1, y + left - 1);
                        if (value >= MIN_LABEL && value < 255) {
                            componentHits.add(value);
                            tp++;
                            hit = true;
                        } else {
                            fp++;
                        }
                    }

                }
            }

            if (!hit) {
                misCount++;
            }

        }
        RegistrationMeasurement m = new RegistrationMeasurement();
        m.setFilteredMiss(misCount);
        m.setFilteredSize(nodes.size());
        m.setMaskHits(componentHits.size());
        m.setMaskMiss((maskComponents.size() - componentHits.size()));

        m.setFn((totalCompPix - tp));
        m.setFp(fp);
        m.setTp(tp);

        return m;
    }

    private RegistrationMeasurement calculateRegistration(Set<CCT> nodes) {

        Set<Integer> componentHits = new HashSet<>();

        ImageProcessor proc = mask.getProcessor();
        int misCount = 0;
        int tp = 0;
        int fp = 0;

        for (CCT component : nodes) {
            boolean hit = false;
            for (Set<Point> points : component.getAllPoints()) {
                for (Point p : points) {
                    int value = proc.get(p.y, p.x);
                    if (value >= MIN_LABEL && value < 255) {
                        componentHits.add(value);
                        tp++;
                        hit = true;
                    } else {
                        fp++;
                    }
                }
            }

            if (!hit) {
                misCount++;
            }
        }

        RegistrationMeasurement m = new RegistrationMeasurement();
        m.setFilteredMiss(misCount);
        m.setFilteredSize(nodes.size());
        m.setMaskHits(componentHits.size());
        m.setFn((totalCompPix - tp));
        m.setFp(fp);
        m.setTp(tp);
        m.setMaskMiss((maskComponents.size() - componentHits.size()));

        return m;
    }

    // -----------------------------------------------------------------------------------------------------------------
    public StringBuilder compareJaccards() {

        Map<FilterProps, Integer> filterProperties = new HashMap<>();
        Map<ComponentProperty, Boolean> selectedFilters = new HashMap<>();

        selectedFilters.put(ComponentProperty.LEFT, true);
        selectedFilters.put(ComponentProperty.RIGHT, true);
        selectedFilters.put(ComponentProperty.TOP, true);
        selectedFilters.put(ComponentProperty.BOTTOM, true);
        filterProperties.put(FilterProps.MAX, Integer.MAX_VALUE);
        filterProperties.put(FilterProps.MIN, Integer.MIN_VALUE);

        for (Component c : maskComponents.values()) {

            filterProperties.put(FilterProps.LEFT, c.getTop() - VAR_BORDER);
            filterProperties.put(FilterProps.RIGHT, c.getBottom() + VAR_BORDER);
            filterProperties.put(FilterProps.UP, c.getLeft() - VAR_BORDER);
            filterProperties.put(FilterProps.BOTTOM, c.getRight() + VAR_BORDER);

            Set<CCT> maxTreeNode = CCTFilter.filterByProperties(maxTree, filterProperties, selectedFilters, true);
            Set<CCT> minTreeNode = CCTFilter.filterByProperties(minTree, filterProperties, selectedFilters, true);
            List<ShapeTree> shapeTreeNode = ShapeTreeFilter.filterByProperties(shapeTree, filterProperties, selectedFilters, true);

            calculateCoefs(c, maxTreeNode, MAX);
            calculateCoefs(c, minTreeNode, MIN);
            calculateCoefs(c, shapeTreeNode, SHAPE);

            jaccardData.append(printJaccardResults(c.getLabel(), MAX));
            jaccardData.append(printJaccardResults(c.getLabel(), MIN));
            jaccardData.append(printJaccardResults(c.getLabel(), SHAPE));
        }

        return pringJaccardHeader().append(jaccardData);
    }

    private void calculateCoefs(Component c, List<ShapeTree> node, String type) {
        if (node == null || node.contains(null)) {
            TPs.put(c.getLabel() + type, 0);
            FPs.put(c.getLabel() + type, 0);
            FNs.put(c.getLabel() + type, 0);
            return;
        }

        ShapeTree component = node.get(0);

        int left = component.getOrigNode().getProperties().get(ComponentProperty.LEFT);
        int up = component.getOrigNode().getProperties().get(ComponentProperty.TOP);

        ImageProcessor proc = mask.getProcessor();
        ImageProcessor maskProc = component.getImageProcessor();

        int tp = 0;
        int fp = 0;
        for (int x = 0; x < maskProc.getWidth(); x++) {
            for (int y = 0; y < maskProc.getHeight(); y++) {
                if (maskProc.get(x, y) < 127) {
                    int label = proc.get(x + up - 1, y + left - 1);
                    if (label >= MIN_LABEL) {
                        tp++;
                    } else {
                        fp++;
                    }
                }

            }
        }

        TPs.put(c.getLabel() + type, tp);
        FPs.put(c.getLabel() + type, fp);
        FNs.put(c.getLabel() + type, Integer.max(c.getPoints().size() - tp, 0));

    }

    private void calculateCoefs(Component c, Set<CCT> node, String type) {

        if (node == null || node.contains(null)) {
            TPs.put(c.getLabel() + type, 0);
            FPs.put(c.getLabel() + type, 0);
            FNs.put(c.getLabel() + type, 0);
            return;
        }

        ImageProcessor proc = mask.getProcessor();
        int tp = 0;
        int fp = 0;
        for (CCT component : node) {
            for (Set<Point> points : component.getAllPoints()) {
                for (Point p : points) {
                    if (proc.get(p.y, p.x) == c.getLabel()) {
                        tp++;
                    } else {
                        fp++;
                    }
                }
            }
        }

        TPs.put(c.getLabel() + type, tp);
        FPs.put(c.getLabel() + type, fp);
        FNs.put(c.getLabel() + type, Integer.max(c.getPoints().size() - tp, 0));

    }

    private void prepareMask(String maskPath) {
        mask = IJ.openImage(maskPath);
        mask.show();
        ImageProcessor proc = mask.getProcessor();
        FloodFiller ff = new FloodFiller(proc);
        int color = MIN_LABEL;

        for (int x = 0; x < proc.getWidth(); x++) {
            for (int y = 0; y < proc.getHeight(); y++) {
                if (proc.get(x, y) == 255) {
                    proc.setValue(color);
                    Component c = new Component();
                    c.setLabel(color);
                    maskComponents.put(color, c);
                    ff.fill(x, y);
                    color++;
                    if (color == 255) {
                        IJ.log("Mask contains too many komponents - please use smaller part of image and mask");
                    }
                }
            }
        }
        for (int x = 0; x < proc.getWidth(); x++) {
            for (int y = 0; y < proc.getHeight(); y++) {
                int label = proc.get(x, y);
                if (label >= MIN_LABEL && label < 255) {
                    totalCompPix++;
                    maskComponents.get(label).addPoint(x, y);
                }
            }
        }
    }

    private double jaccard(int tp, int fp, int fn) {
        if (tp == 0)
            return 0;
        else {
            return tp * 1.0 / (tp + fp + fn);
        }
    }

    // ------------------------------------------------------------------
    // PRINT RESULTS
    // ------------------------------------------------------------------
    private StringBuilder pringJaccardHeader() {
        StringBuilder str = new StringBuilder();
        str.append("componentLabel;treeType;tp;fp;fn;jaccardCoef" + EOL);
        return str;
    }

    private StringBuilder printJaccardResults(int label, String type) {
        String key = label + type;
        StringBuilder str = new StringBuilder();
        str.append(label + SEP);
        str.append(type + SEP);
        str.append(TPs.get(key) + SEP);
        str.append(FPs.get(key) + SEP);
        str.append(FNs.get(key) + SEP);
        Double jaccard = jaccard(TPs.get(key), FPs.get(key), FNs.get(key));
        str.append(formatter.format(jaccard).replace(".", ",") + EOL);

        return str;

    }

    public String getJaccardData() {
        return pringJaccardHeader().append(jaccardData).toString();
    }

    // ------------------------------------------------------------------
    // PRINT RESULTS REGISTRATION
    // ------------------------------------------------------------------

    public StringBuilder printRegistrationHeader() {
        StringBuilder str = new StringBuilder();
        str.append("tree;measureType1;param1;measureType2;param2;filteredSize;filteredMiss;maskHits;maskMiss;tp;fp;fn;jaccardPixels;acuracy" + EOL);
        return str;
    }

    public StringBuilder printRegistrationResults() {

        StringBuilder str = new StringBuilder();
        for (RegistrationMeasurement m : measures) {

            str.append(m.getTree()).append(SEP);
            str.append(getType(m.getMeasureType1())).append(SEP);
            str.append(m.getParam1()).append(SEP);
            str.append(getType(m.getMeasureType2())).append(SEP);
            str.append(m.getParam2()).append(SEP);
            str.append(m.getFilteredSize()).append(SEP);
            str.append(m.getFilteredMiss()).append(SEP);
            str.append(m.getMaskHits()).append(SEP);
            str.append(m.getMaskMiss()).append(SEP);
            str.append(m.getTp()).append(SEP);
            str.append(m.getFp()).append(SEP);
            str.append(m.getFn()).append(SEP);
            str.append(formatter.format(jaccard(m.getTp(), m.getFp(), m.getFn())).replace(".", ",")).append(SEP);

            int tp = m.getFilteredSize() - m.getFilteredMiss();
            int fp = m.getFilteredMiss();
            int fn = m.getMaskMiss()+ Math.abs(m.getMaskHits()-tp) ;

            str.append(formatter.format(jaccard(tp, fn, fp))).append(EOL);

        }

        return str;
    }

    private String getType(int type) {
        switch (type) {
            case MEASURE_HEIGHT :
                return "height";
            case MEASURE_ENLONGATION :
                return "elongation";
            case MEASURE_INTENSITY :
                return "intensity";
            case MEASURE_ROUND :
                return "roundness";
            case MEASURE_SIZE :
                return "size";
            default :
                break;
        }

        return "empty";
    }

    public int getLabelFount() {
        return labelFount;
    }

    public void setLabelFount(int labelFount) {
        this.labelFount = labelFount;
    }

}
