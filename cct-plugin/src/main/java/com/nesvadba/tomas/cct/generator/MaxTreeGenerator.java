package com.nesvadba.tomas.cct.generator;

import java.util.Queue;

import com.nesvadba.tomas.cct.CCTEvaluator;
import com.nesvadba.tomas.cct.domain.CCT;
import com.nesvadba.tomas.cct.domain.Point;
import com.nesvadba.tomas.cct.enums.ComponentProperty;
import com.nesvadba.tomas.cct.enums.PointStatus;

/**
 * Generator maximoveho stromu
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 *
 */
public class MaxTreeGenerator extends CCTGenerator {

    @Override
    public CCT createCCT(Point[][] imgpoints) {
        long start = System.currentTimeMillis();
        
        points=imgpoints;
        initNumberNodes(256);

        proccess(points[0][0], 0);

        //Finished 
        StringBuilder str = new StringBuilder();
        str.append("MaxTreeGenerator - Building");
        str.append("Proccessing time: " + (System.currentTimeMillis() - start) + "\n");
        str.append("Image Size    : " + imgpoints.length * imgpoints[0].length +"\n");
        str.append("Pix in tree   : " + cct.getProperties().get(ComponentProperty.SIZE) + "\n");
        str.append("Nodes in tree : " + cct.getNodeCount() + "\n");

        System.out.println(str);
        
        cct.setName("MaxTree");
        
        return cct;
    }

    @Override
    protected void proccess(Point p, int level) {

        int m = p.value;
        addToQueue(p);
        while (m >= level) {
            m = flood(m);
        }
    }

    @Override
    protected int flood(int level) {
        Queue<Point> queue = quegeMap.get(level);

        CCT node = getNode(level);
        while (!queue.isEmpty()) {
            Point p = queue.peek();
            
            node.addPoint(p);

            for (Point n : getNeigb(p)) {

                if (n.value > p.value && n.status == PointStatus.U) {
                    proccess(n, level);

                } else if (n.status == PointStatus.U) {
                    addToQueue(n);
                }
            }
            p.status = PointStatus.P;
            CCTEvaluator.evaluateNewPoint(node, p);
            queue.remove(p);

        }
  
        int m = level - 1;

        while (m >= 0 && (quegeMap.get(m) == null || quegeMap.get(m).isEmpty())) {
            m--;
        }
        if (m >= 0) {

            CCT parent = getNode(m);
            CCT child = getNode(level);

            if (!child.getPoints().isEmpty()) {
                parent.getNodes().add(child);
                parent.getSubPoints().addAll(child.getSubPoints());
                parent.getSubPoints().add(child.getPoints());
            }
            cct = parent;

        } else {
        }

        increaseNumberNodes(level);
        return m;
    }

}
