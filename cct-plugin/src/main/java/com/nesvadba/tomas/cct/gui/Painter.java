package com.nesvadba.tomas.cct.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nesvadba.tomas.cct.Filling;
import com.nesvadba.tomas.cct.RandomColor;
import com.nesvadba.tomas.cct.domain.CCT;
import com.nesvadba.tomas.cct.domain.Point;
import com.nesvadba.tomas.cct.domain.ShapeTree;
import com.nesvadba.tomas.cct.enums.ComponentProperty;
import com.nesvadba.tomas.cct.enums.FilterProps;
import com.nesvadba.tomas.cct.filter.CCTFilter;
import com.nesvadba.tomas.cct.filter.ShapeTreeFilter;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.process.ImageProcessor;
/**
 * Kreslici nastroj
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 *
 */
public class Painter {

    private ImagePlus image;
    private ImagePlus orig;
    private ImagePlus mask;

    private RandomColor randomColor = new RandomColor();

    private Map<FilterProps, Integer> filterProperties;
    private Map<ComponentProperty, Boolean> selectedFilters;

    public Painter(ImagePlus imageIn, ImagePlus origIn, ImagePlus maskIn, Map<FilterProps, Integer> filterPropertiesIn, Map<ComponentProperty, Boolean> selectedFiltersIn) {
        image = imageIn;
        orig = origIn;
        filterProperties = filterPropertiesIn;
        selectedFilters = selectedFiltersIn;
        mask = maskIn;
    }

    public List<ShapeTree> filterShapeTreeImage(ShapeTree shapeTree, boolean keepOnlyOne, boolean similar, int procent, boolean isMask) {

        if (!isFilter()) {
            return null;
        }

        maskOrOrig(isMask);

        ImageProcessor proc = image.getProcessor();

        image.repaintWindow();

        Map<FilterProps, Integer> props = filterProperties;
        Map<ComponentProperty, Boolean> filters = selectedFilters;

        List<ShapeTree> nodes = ShapeTreeFilter.filterByProperties(shapeTree, props, filters, keepOnlyOne);

        for (ShapeTree node : nodes) {
            if (node == null)
                return null;

            proc.setColor(isMask ? Color.WHITE : randomColor.getRandColor());

            int left = node.getOrigNode().getProperties().get(ComponentProperty.LEFT);
            int up = node.getOrigNode().getProperties().get(ComponentProperty.TOP);

            ImageProcessor mask = node.getImageProcessor();

            for (int x = 0; x < mask.getWidth(); x++) {
                for (int y = 0; y < mask.getHeight(); y++) {
                    if (mask.get(x, y) < 127) {
                        proc.drawDot(x + up - 1, y + left - 1);// -1 is BORDER
                    }

                }
            }

            image.repaintWindow();
        }

        return nodes;

    }

    public Set<CCT> filterCCTImage(CCT cct, boolean keepOnlyOne, boolean similar, boolean isMask) {

        if (!isFilter()) {
            return null;
        }

        maskOrOrig(isMask);

        ImageProcessor proc = image.getProcessor();

        image.repaintWindow();
        Set<CCT> nodes = CCTFilter.filterByProperties(cct, filterProperties, selectedFilters, keepOnlyOne);
        for (CCT node : nodes) {
            if (node == null)
                return null;
            proc.setColor(isMask ? Color.WHITE : randomColor.getRandColor());

            for (Set<Point> subnode : node.getAllPoints()) {
                for (Point point : subnode) {
                    proc.drawDot(point.y, point.x);
                }
            }
            image.repaintWindow();
        }
        return nodes;
    }

    private boolean isFilter() {
        boolean isAnyFilterOn = false;
        for (boolean filterOn : selectedFilters.values()) {
            isAnyFilterOn = isAnyFilterOn || filterOn;
        }
        return isAnyFilterOn;
    }

    public void reprintOrig() {
        reprintOrig(image, orig);
    }

    private void maskOrOrig(boolean isMask) {
        if (isMask) {
            reprintOrig(image, mask);
            printBlack(image);
        } else {
            reprintOrig(image, orig);
        }

    }

    private void reprintOrig(ImagePlus image, ImagePlus orig) {

        ImageProcessor proc = image.getProcessor();
        ImageProcessor origProc = orig.getProcessor();
        // Reprint Orig
        for (int x = 0; x < proc.getWidth(); x++) {
            for (int y = 0; y < proc.getHeight(); y++) {
                int gray = origProc.get(x, y);
                proc.setColor(new Color(gray, gray, gray));
                proc.drawDot(x, y);
            }
        }

        image.repaintWindow();
    }

    private void printBlack(ImagePlus image) {

        ImageProcessor proc = image.getProcessor();
        for (int x = 0; x < proc.getWidth(); x++) {
            for (int y = 0; y < proc.getHeight(); y++) {
                proc.setColor(Color.BLACK);
                proc.drawDot(x, y);
            }
        }

        image.repaintWindow();
    }

}
