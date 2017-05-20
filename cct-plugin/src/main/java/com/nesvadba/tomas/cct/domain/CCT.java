package com.nesvadba.tomas.cct.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nesvadba.tomas.cct.enums.ComponentProperty;

import ij.process.ImageProcessor;
/**
 * Stromová struktura pro maximový a minimový strom
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 *
 */
public class CCT {

    private String name;

    private int level;
    private int label;

    private Set<Point> points = new HashSet<>();
    private List<Set<Point>> subPoints = new ArrayList<>();

    private List<CCT> nodes = new ArrayList<>();

    private Map<ComponentProperty, Integer> properties = new HashMap<>();
    private ImageProcessor proc;
    

    public CCT(int nodeLevel, Integer nodeLabel) {
        label = nodeLabel ==null ? 0 : nodeLabel;
        level = nodeLevel;

        initProperties();
    }

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

    public List<CCT> getNodes() {
        return nodes;
    }

    public void setNodes(List<CCT> nodes) {
        this.nodes = nodes;
    }

    public Map<ComponentProperty, Integer> getProperties() {
        return properties;
    }

    public void setProperties(Map<ComponentProperty, Integer> properties) {
        this.properties = properties;
    }

    public List<Set<Point>> getSubPoints() {
        return subPoints;
    }

    public void setSubPoints(List<Set<Point>> subPoints) {
        this.subPoints = subPoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ====================================================================================================
    // Vlastní fuknce
    // ====================================================================================================

    public List<Set<Point>> getAllPoints() {
        List<Set<Point>> list = new ArrayList<>(subPoints);
        list.add(points);
        return list;
    }

    public String getCode() {
        return level + "#" + label;
    }

    public Set<CCT> getAllNodes() {
        Set<CCT> allNodes = new HashSet<>();
        allNodes.add(this);
        if (!nodes.isEmpty()) {
            for (CCT node : nodes) {
                allNodes.addAll(node.getAllNodes());
            }
        }
        return allNodes;
    }

    public int getNodeCount() {
        int count = 1;
        if (!nodes.isEmpty()) {
            for (CCT node : nodes) {
                count += node.getNodeCount();
            }
        }
        return count;
    }

    
    public void addPoint(Point p) {
        points.add(p);
    }

    private void initProperties() {

        for (ComponentProperty cp : ComponentProperty.values()) {
            properties.put(cp, -1);
        }

    }

    public void print(String str) {
        String thisLevel = str + "[" + level + "#" + label + "/" + nodes.size() + "]";
        for (CCT node : nodes) {
            thisLevel += node.getCode() + ",";
        }
        System.out.println(thisLevel);

    }

    public ImageProcessor getProc() {
        return proc;
    }

    public void setProc(ImageProcessor proc) {
        this.proc = proc;
    }
    
    
    
}
