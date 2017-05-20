package com.nesvadba.tomas.cct.domain;

import java.util.HashSet;
import java.util.Set;
/**
 * Strukutura pro komponenty masky
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 *
 */
public class Component {
    private int label;
    private Set<Point> points = new HashSet<>();;
    private int top = Integer.MAX_VALUE;
    private int bottom = Integer.MIN_VALUE;
    private int right = Integer.MIN_VALUE;
    private int left = Integer.MAX_VALUE;

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
    public int getTop() {
        return top;
    }
    public void setTop(int top) {
        this.top = top;
    }
    public int getBottom() {
        return bottom;
    }
    public void setBottom(int bottom) {
        this.bottom = bottom;
    }
    public int getRight() {
        return right;
    }
    public void setRight(int right) {
        this.right = right;
    }
    public int getLeft() {
        return left;
    }
    public void setLeft(int left) {
        this.left = left;
    }
    @Override
    public String toString() {
        return "Component [label=" + label + ", points=" + points.size() + ", top=" + top + ", bottom=" + bottom + ", right=" + right + ", left=" + left + "]";
    }
    public void addPoint(int x, int y) {

        if (x > right) {
            right = x;
        }

        if (x < left) {
            left = x;
        }

        if (y > bottom) {
            bottom = y;
        }

        if (y < top) {
            top = y;
        }

        points.add(new Point(x, y));
    }

}
