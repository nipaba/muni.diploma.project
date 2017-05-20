package com.nesvadba.tomas.cct;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nesvadba.tomas.cct.domain.CCT;
import com.nesvadba.tomas.cct.domain.Point;
import com.nesvadba.tomas.cct.enums.ComponentProperty;
/**
 * Třída pro vyhodnocování vlastností komponet
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 * 
 */
public class CCTEvaluator {

    /**Vyhodnocení přidáné bodu do do komponenty
     * 
     * @param cct - uzel cct
     * @param p - novy bod
     */
    public static void evaluateNewPoint(CCT cct, Point p) {
        Set<Point> points = cct.getPoints();
        Map<ComponentProperty, Integer> properties = cct.getProperties();
        List<CCT> nodes = cct.getNodes();

        // init
        int temp, left, right, up, down, height;

        int centerX = 0;
        int centerY = 0;
        int size = 0;
        height = 1;

        if (points.size() == 1) {
            left = p.x;
            right = p.x;
            up = p.y;
            down = p.y;

        } else {
            left = properties.get(ComponentProperty.LEFT);
            right = properties.get(ComponentProperty.RIGHT);
            up = properties.get(ComponentProperty.TOP);
            down = properties.get(ComponentProperty.BOTTOM);
        }

        // process SUB
        for (CCT node : nodes) {

            // Size
            size += node.getProperties().get(ComponentProperty.SIZE);

            // BB
            temp = node.getProperties().get(ComponentProperty.LEFT);
            if (temp < left) {
                left = temp;
            }

            temp = node.getProperties().get(ComponentProperty.RIGHT);
            if (temp > right) {
                right = temp;
            }

            temp = node.getProperties().get(ComponentProperty.TOP);
            if (temp < up) {
                up = temp;
            }

            temp = node.getProperties().get(ComponentProperty.BOTTOM);
            if (temp > down) {
                down = temp;
            }
            // Height
            height = Integer.max(height, node.getProperties().get(ComponentProperty.G_HEIGHT) + 1);

            // CENTER
            centerX += node.getProperties().get(ComponentProperty.CENTERX);
            centerY += node.getProperties().get(ComponentProperty.CENTERY);

        }

        // BB Calc
        if (p.x < left) {
            left = p.x;
        }
        if (p.x > right) {
            right = p.x;
        }
        if (p.y < up) {
            up = p.y;
        }
        if (p.y > down) {
            down = p.y;
        }

        centerX += p.x;
        centerY += p.y;

        properties.put(ComponentProperty.LEFT, left);
        properties.put(ComponentProperty.RIGHT, right);
        properties.put(ComponentProperty.TOP, up);
        properties.put(ComponentProperty.BOTTOM, down);

        // HEIGHT
        properties.put(ComponentProperty.G_HEIGHT, height);

        // SIZE
        properties.put(ComponentProperty.SIZE, points.size() + size);

        // INTENSITY
        properties.put(ComponentProperty.INTENSITY, p.value);

        // BOunding box
        properties.put(ComponentProperty.LEFT, left);
        properties.put(ComponentProperty.RIGHT, right);
        properties.put(ComponentProperty.TOP, up);
        properties.put(ComponentProperty.BOTTOM, down);

        // Center X,Y
        properties.put(ComponentProperty.CENTERX, centerX);
        properties.put(ComponentProperty.CENTERY, centerY);
    }

}
