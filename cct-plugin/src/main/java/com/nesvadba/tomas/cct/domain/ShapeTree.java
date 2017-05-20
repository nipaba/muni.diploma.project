package com.nesvadba.tomas.cct.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nesvadba.tomas.cct.enums.ComponentProperty;

import ij.process.ImageProcessor;
/**
 * Stromová struktura pro strom tvarů
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 *
 */
public class ShapeTree {

    private int label;
    private int level;
    private int nodeCount = 0;
    private Set<Point> points = new HashSet<>();
    private List<ShapeTree> nodes = new ArrayList<>();

    private ImageProcessor imageProcessor;

    private CCT origNode;

    public CCT getOrigNode() {
        return origNode;
    }

    public void setOrigNode(CCT origNode) {
        this.origNode = origNode;
    }

    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    public void setImageProcessor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }

    private Map<ComponentProperty, Integer> properties = new HashMap<>();

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public Set<Point> getPoints() {
        return points;
    }

    public void setPoints(Set<Point> points) {
        this.points = points;
    }

    public List<ShapeTree> getNodes() {
        return nodes;
    }

    public void setNodes(List<ShapeTree> nodes) {
        this.nodes = nodes;
    }

    public Map<ComponentProperty, Integer> getProperties() {
        return properties;
    }

    public void setProperties(Map<ComponentProperty, Integer> properties) {
        this.properties = properties;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    // Comparator pro prioritníí frontu
    public static Comparator<ShapeTree> getComparator() {

        return new Comparator<ShapeTree>() {

            @Override
            public int compare(ShapeTree o1, ShapeTree o2) {

                return o2.getProperties().get(ComponentProperty.SIZE) - o1.getProperties().get(ComponentProperty.SIZE);

            }
        };

    }

    @Override
    public String toString() {
        return "ShapeTree [level=" + level + ", label=" + label + ", points=" + points + ", nodes=" + nodes + ", properties=" + properties + "]";
    }

    public void print(String str) {
        String msg = str + origNode.getCode() + "$" + properties + "& ->";
        System.out.println(msg);
        if (!nodes.isEmpty()) {
            for (ShapeTree node : nodes) {
                node.print(msg);
            }
        }
    }

    public static Comparator<ShapeTree> getInvertComparator() {
        return new Comparator<ShapeTree>() {

            @Override
            public int compare(ShapeTree o1, ShapeTree o2) {

                return o1.getProperties().get(ComponentProperty.SIZE) - o2.getProperties().get(ComponentProperty.SIZE);

            }
        };
    }

    public void evalTreeAvgIntensity() {

        long intensitySum = 0;
        long subSize = 0;

        int tempMax = 0;
        for (ShapeTree node : nodes) {
            node.evalTreeAvgIntensity();
            tempMax = Integer.max(tempMax, node.getProperties().get(ComponentProperty.G_HEIGHT));
            intensitySum += node.getProperties().get(ComponentProperty.SIZE) * node.getProperties().get(ComponentProperty.AVG_INTENSITY);
            subSize += node.getProperties().get(ComponentProperty.SIZE);
        }

        int size = properties.get(ComponentProperty.SIZE);
        int intensity = properties.get(ComponentProperty.INTENSITY);
        int avgIntensity = (int) ((size - subSize) * intensity + intensitySum) / size;

        properties.put(ComponentProperty.AVG_INTENSITY, avgIntensity);
        properties.put(ComponentProperty.G_HEIGHT, tempMax + 1);
    }

}
