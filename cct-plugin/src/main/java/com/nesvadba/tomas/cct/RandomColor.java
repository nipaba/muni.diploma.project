package com.nesvadba.tomas.cct;

import java.awt.Color;
import java.util.Random;
/**
 * Náhodná barva
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 * 
 */
public class RandomColor {
    private final Random randomGenerator = new Random();

    public Color getRandColor() {
        boolean allTheSame = true;
        int r = 0, g = 0, b = 0;
        while (allTheSame) {
            r = randomGenerator.nextInt(256);
            g = randomGenerator.nextInt(256);
            b = randomGenerator.nextInt(256);

            allTheSame = (r == g && g == b && b == r);
        }
        return new Color(r, g, b);
    }
}
