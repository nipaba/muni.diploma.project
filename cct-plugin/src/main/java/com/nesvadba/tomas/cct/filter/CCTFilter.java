package com.nesvadba.tomas.cct.filter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.nesvadba.tomas.cct.domain.CCT;
import com.nesvadba.tomas.cct.enums.ComponentProperty;
import com.nesvadba.tomas.cct.enums.FilterProps;

import ij.ImagePlus;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.process.ImageProcessor;
/**
 * Filtr pro CCT stromy
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 *
 */
public class CCTFilter extends Filter {
    /**
     * Filtr nad minimovym nebo maximovym stromem
     * 
     * @param cct
     * @param filterProperties
     * @param selectedFilters
     * @param keepOnlyOne
     * @return
     */
    public static Set<CCT> filterByProperties(CCT cct, Map<FilterProps, Integer> filterProperties, Map<ComponentProperty, Boolean> selectedFilters, boolean keepOnlyOne) {

        Set<CCT> result = new HashSet<>();
        Queue<CCT> que = new LinkedList<>();
        que.add(cct);

        boolean minSelected = true;

        CCT onlyNode = null;

        while (!que.isEmpty()) {
            CCT node = que.poll();

            if (node.getProc() != null) {
                updateOrig(node, getStats(node.getProc()));
                node.setProc(null);
            }

            boolean tempInRANGE = true;
            for (ComponentProperty property : selectedFilters.keySet()) {
                if (selectedFilters.get(property)) {

                    if (!isAviable(property, node.getProperties(), filterProperties)) {
                        return new HashSet<>();
                    }
                    int propertyVal = node.getProperties().get(property).intValue();
                    int min = filterProperties.get(getProperty(property, minSelected));
                    int max = filterProperties.get(getProperty(property, !minSelected));

                    tempInRANGE = tempInRANGE & (propertyVal < max && propertyVal > min);
                }
            }
            if (tempInRANGE) {
                result.add(node);
                if (onlyNode == null) {
                    onlyNode = node;
                } else if (onlyNode.getProperties().get(ComponentProperty.SIZE) < node.getProperties().get(ComponentProperty.SIZE)) {
                    onlyNode = node;
                }
            } else {
                que.addAll(node.getNodes());
            }
        }

        if (keepOnlyOne) {
            result = new HashSet<>();
            result.add(onlyNode);
        }
        return result;
    }
    /**
     * Dopocet atributu Round,Perim, Elongation
     * 
     * @param node
     * @param rt
     */
    private static void updateOrig(CCT node, ResultsTable rt) {

        Map<ComponentProperty, Integer> props = node.getProperties();
        int round, perim, elongation, size = 0;
        int index;

        if ((index = rt.getColumnIndex("Area")) != ResultsTable.COLUMN_NOT_FOUND) {
            size = Double.valueOf(rt.getValueAsDouble(index, 0)).intValue();
        }

        if ((index = rt.getColumnIndex("Round")) != ResultsTable.COLUMN_NOT_FOUND) {
            round = Double.valueOf(rt.getValueAsDouble(index, 0) * 100).intValue();
            props.put(ComponentProperty.ROUND, round);
        }

        if ((index = rt.getColumnIndex("Perim.")) != ResultsTable.COLUMN_NOT_FOUND) {
            perim = Double.valueOf(rt.getValueAsDouble(index, 0)).intValue();
            props.put(ComponentProperty.PERIMETER, perim);

            elongation = (int) (100 * perim * perim / (4 * Math.PI * size));

            props.put(ComponentProperty.ELONGATION, elongation);
        }

    }
    /**
     * Statistiky komomponent pomoci imageJ
     * 
     * @param proc
     * @return
     */
    private static ResultsTable getStats(ImageProcessor proc) {
        int options = ParticleAnalyzer.SHOW_PROGRESS;
        int measurements = Measurements.ALL_STATS;
        int minSize = 1;
        int maxSize = Integer.MAX_VALUE;
        ResultsTable rt = new ResultsTable();
        ParticleAnalyzer pa = new ParticleAnalyzer(options, measurements, rt, minSize, maxSize);
        pa.analyze(new ImagePlus("", proc));

        StringBuilder str = new StringBuilder();
        for (int i = 0; i <= rt.getLastColumn(); i++) {
            if (rt.columnExists(i)) {
                str.append(rt.getColumnHeading(i) + ":" + rt.getValueAsDouble(i, 0) + ",");
            }
        }

        return rt;
    }

}
