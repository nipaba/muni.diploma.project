package com.nesvadba.tomas.cct.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.nesvadba.tomas.cct.domain.CCT;
import com.nesvadba.tomas.cct.domain.Point;
import com.nesvadba.tomas.cct.enums.PointStatus;

/**
 * Abstraktni trida pro generovani stromu
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 *
 */
public abstract class CCTGenerator {

    protected Map<Integer, Queue<Point>> quegeMap = new HashMap<>();

    protected Point[][] points;
    protected Map<Integer, Integer> labels = new HashMap<>();
    protected Map<String, CCT> createdNodes = new HashMap<>();

    protected CCT cct = null;

    public abstract CCT createCCT(Point[][] points);

    abstract void proccess(Point p, int level);

    abstract int flood(int level);

    protected CCT getNode(int level) {
        String key = level + "#" + labels.get(level);
        CCT node = createdNodes.get(key);
        if (node == null) {
            node = new CCT(level, labels.get(level));
            createdNodes.put(key, node);
        }
        return node;
    }

    // -------------------------HELP_FUNCTIONS--------------------------------------
    /**
     * 4 Neigborow
     * 
     * @param p
     * @return
     */
    protected List<Point> getNeigb(Point p) {

        List<Point> neigb = new ArrayList<>();
        if (p.x - 1 >= 0) {
            neigb.add(points[p.x - 1][p.y]);
        }
        if (p.y - 1 >= 0) {
            neigb.add(points[p.x][p.y - 1]);
        }
        if (p.x + 1 < points.length) {
            neigb.add(points[p.x + 1][p.y]);
        }
        if (p.y + 1 < points[0].length) {
            neigb.add(points[p.x][p.y + 1]);
        }

        // LT
        if (p.x - 1 >= 0 && p.y - 1 >= 0) {
            neigb.add(points[p.x - 1][p.y - 1]);
        }
        // RT
        if (p.x + 1 < points.length && p.y - 1 >= 0) {
            neigb.add(points[p.x + 1][p.y - 1]);
        }

        // LB
        if (p.x - 1 >= 0 && p.y + 1 < points[0].length) {
            neigb.add(points[p.x - 1][p.y + 1]);
        }
        // RB
        if (p.x + 1 < points.length && p.y + 1 < points[0].length) {
            neigb.add(points[p.x + 1][p.y + 1]);
        }

        return neigb;
    }

    protected void increaseNumberNodes(int level) {
        if (labels.get(level) == null) {
            labels.put(level, 1);
        } else {
            labels.put(level, labels.get(level) + 1);
        }

    }

    protected void addToQueue(Point n) {
        Queue<Point> queqe = quegeMap.get(n.value);
        if (queqe == null) {
            queqe = new LinkedList<>();
            quegeMap.put(n.value, queqe);
        }
        queqe.add(n);
        n.status = PointStatus.I;

    }

    // -------------------------------INIT-------------------------------

    protected void initNumberNodes(int layerCount) {
        for (int i = 0; i < layerCount; i++) {
            labels.put(i, 0);
        }

    }

}
