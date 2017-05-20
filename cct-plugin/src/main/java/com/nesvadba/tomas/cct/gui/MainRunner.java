package com.nesvadba.tomas.cct.gui;

import ij.IJ;
import ij.ImagePlus;
/**
 * Třida pro spuštění aplikace bez ImageJ
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 *
 */
public class MainRunner {

    public static void main(String[] args) {

        ImagePlus img = IJ.openImage("C:\\Users\\nipaba\\Documents\\img\\NAK\\qpi01.tif");

        img.show();

        CCT_PluginFrame frame = new CCT_PluginFrame();
        frame.show();
        frame.run("run");
    }
}
