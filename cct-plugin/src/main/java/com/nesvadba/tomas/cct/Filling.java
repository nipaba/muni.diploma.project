package com.nesvadba.tomas.cct;

import ij.process.FloodFiller;
import ij.process.ImageProcessor;
/**
 * Záplava pro imageJ
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 * 
 */
public class Filling {

    public static void fill(ImageProcessor ip, int foreground, int background) {
        int width = ip.getWidth();
        int height = ip.getHeight();
        FloodFiller ff = new FloodFiller(ip);
        ip.setColor(127);

        for (int y = 0; y < height; y++) {

            if (ip.getPixel(0, y) == background)
                ff.fill(0, y);

            if (ip.getPixel(width - 1, y) == background)
                ff.fill(width - 1, y);

        }

        for (int x = 0; x < width; x++) {

            if (ip.getPixel(x, 0) == background)
                ff.fill(x, 0);

            if (ip.getPixel(x, height - 1) == background)
                ff.fill(x, height - 1);
        }

        byte[] pixels = (byte[]) ip.getPixels();

        int n = width * height;

        for (int i = 0; i < n; i++) {
            if (pixels[i] == 127)
                pixels[i] = (byte) background;
            else
                pixels[i] = (byte) foreground;
        }

    }
}
