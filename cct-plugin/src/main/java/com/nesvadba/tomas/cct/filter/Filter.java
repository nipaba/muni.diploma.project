package com.nesvadba.tomas.cct.filter;

import java.util.Map;

import com.nesvadba.tomas.cct.enums.ComponentProperty;
import com.nesvadba.tomas.cct.enums.FilterProps;
/**
 * Trida pro filtrovani stromu
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 *
 */
public class Filter {

    /**
     * Prevod filtrovacich parametru na konkretni nastaveni
     * 
     * @param property
     * @param isMinSelected
     * @return
     */
    protected static FilterProps getProperty(ComponentProperty property, boolean isMinSelected) {
        switch (property) {
            case SIZE :
                return (isMinSelected) ? FilterProps.SIZE_MIN : FilterProps.SIZE_MAX;
            case G_HEIGHT :
                return (isMinSelected) ? FilterProps.HEIGHT_MIN : FilterProps.HEIGHT_MAX;
            case INTENSITY :
                return (isMinSelected) ? FilterProps.INTENSITY_MIN : FilterProps.INTENSITY_MAX;
            case ELONGATION :
                return (isMinSelected) ? FilterProps.ELONGATION_MIN : FilterProps.ELONGATION_MAX;
            case ROUND :
                return (isMinSelected) ? FilterProps.ROUND_MIN : FilterProps.ROUND_MAX;

            // BOUNDING BOX
            case LEFT :
                return (isMinSelected) ? FilterProps.LEFT : FilterProps.MAX;
            case RIGHT :
                return (isMinSelected) ? FilterProps.MIN : FilterProps.RIGHT;
            case TOP :
                return (isMinSelected) ? FilterProps.UP : FilterProps.MAX;
            case BOTTOM :
                return (isMinSelected) ? FilterProps.MIN : FilterProps.BOTTOM;

            case AVG_INTENSITY :
                return (isMinSelected) ? FilterProps.AVG_INT_MIN : FilterProps.AVG_INT_MAX;
            default :
                break;
        }

        System.err.println("[Filter] Property :  " + property + " NOT RECOGNIZED");
        return null;
    }
    /**
     * Kontrola dostupnosti nastroju
     * 
     * @param property
     * @param nodeProperties
     * @param filterProperties
     * @return
     */
    protected static boolean isAviable(ComponentProperty property, Map<ComponentProperty, Integer> nodeProperties, Map<FilterProps, Integer> filterProperties) {
        if (nodeProperties.get(property) == null) {
            System.err.println("Property " + property.name() + " není dostupná" + nodeProperties);
            return false;
        }
        if (filterProperties.get(getProperty(property, true)) == null) {
            System.err.println("Property MIN FILTER =" + property.name() + " není dostupná" + filterProperties);
            return false;
        }

        if (filterProperties.get(getProperty(property, false)) == null) {
            System.err.println("Property MAX FILTER = " + property.name() + " není dostupná" + filterProperties);
            return false;
        }
        return true;
    }
}
