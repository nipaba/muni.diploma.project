package com.nesvadba.tomas.cct.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.nesvadba.tomas.cct.domain.ShapeTree;
import com.nesvadba.tomas.cct.enums.ComponentProperty;
import com.nesvadba.tomas.cct.enums.FilterProps;
/**
 * Filtr pro strom tvaru
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 *
 */
public class ShapeTreeFilter extends Filter {

    public static List<ShapeTree> filterByProperties(ShapeTree shapeTree, Map<FilterProps, Integer> filterProperties, Map<ComponentProperty, Boolean> selectedFilters, boolean keepOnlyOne) {

        List<ShapeTree> result = new ArrayList<>();
        Queue<ShapeTree> que = new LinkedList<>();
        que.add(shapeTree);

        boolean minSelected = true;

        ShapeTree onlyNode = null;

        while (!que.isEmpty()) {
            ShapeTree node = que.poll();

            boolean tempInRANGE = true;
            for (ComponentProperty property : selectedFilters.keySet()) {

                if (selectedFilters.get(property)) {

                    if (!isAviable(property, node.getProperties(), filterProperties)) {
                        return new ArrayList<>();
                    }
                    Integer propValue = node.getProperties().get(property);

                    int propertyVal = propValue.intValue();

                    int min = filterProperties.get(getProperty(property, minSelected));
                    int max = filterProperties.get(getProperty(property, !minSelected));

                    tempInRANGE = tempInRANGE & (propertyVal <= max && propertyVal >= min);
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
            result = new ArrayList<>();
            result.add(onlyNode);
        }

        Collections.sort(result, ShapeTree.getComparator());
        return result;
    }

}
