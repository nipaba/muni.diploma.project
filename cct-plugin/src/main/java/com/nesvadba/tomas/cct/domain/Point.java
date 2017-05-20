package com.nesvadba.tomas.cct.domain;

import com.nesvadba.tomas.cct.enums.PointStatus;
/**
 * Bod obrazu
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 *
 */
public class Point {

    public int x;
    public int y;
    public PointStatus status;
    public int value;

    public Point() {

    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Point [x=" + x + ", y=" + y + ", value=" + value + "]";
    }

}
