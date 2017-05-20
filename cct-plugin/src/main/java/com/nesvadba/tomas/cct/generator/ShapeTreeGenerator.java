package com.nesvadba.tomas.cct.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.nesvadba.tomas.cct.Convertor;
import com.nesvadba.tomas.cct.Filling;
import com.nesvadba.tomas.cct.domain.CCT;
import com.nesvadba.tomas.cct.domain.Point;
import com.nesvadba.tomas.cct.domain.ShapeTree;
import com.nesvadba.tomas.cct.enums.ComponentProperty;

import ij.ImagePlus;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.process.ImageProcessor;
/**
 * Generator stromu tvaru
 * 
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 *
 */
public class ShapeTreeGenerator {

    private static final int BORDER = 1;

    public static ShapeTree createShapeTree(List<CCT> ccts) {

        System.out.println("ShapeTreeGenerator - Building Start");
        List<ShapeTree> shapeQue = new ArrayList<>();
        int counter = 0;
        long start = System.currentTimeMillis();

        for (CCT cct : ccts) {
            System.out.println("Proccessing " + cct.getName());
            for (CCT node : cct.getAllNodes()) {

                long nodeStart = System.currentTimeMillis();
                counter++;
                ImageProcessor proc = Convertor.createBorderedImage(node, BORDER);
                node.setProc(proc.duplicate());

                Filling.fill(proc, 0, 255);
                ResultsTable rt = getStats(proc);

                ShapeTree shapeTree = createShapeThreeNode(rt, node.getLevel(), node);

                shapeTree.setLabel(counter);
                shapeTree.setImageProcessor(proc);

                shapeQue.add(shapeTree);

                long duration = System.currentTimeMillis() - nodeStart;
                if (duration > 1000) {
                    System.out.println("ShapeTreeGenerator : createShapeTree := " + duration + "/" + node.getProperties());

                }
            }
        }

        System.out.println("QUE SIZE : " + shapeQue.size());

        long sortTime = System.currentTimeMillis();
        System.out.println("Sorting que:");
        Collections.sort(shapeQue, ShapeTree.getComparator());
        System.out.println("Sorting que time:" + (System.currentTimeMillis() - sortTime));

        System.out.println("Building tree from que:");
        ShapeTree root = buildShapeThree(shapeQue);
        root.evalTreeAvgIntensity();

        System.out.println("ShapeTreeGenerator - Building finish with time :" + (System.currentTimeMillis() - start));
        return root;
    }

    private static ShapeTree buildShapeThree(List<ShapeTree> shapeQue) {
        long start = System.currentTimeMillis();
        System.out.println("Building shapeTree from Que");

        ShapeTree root = shapeQue.get(0);
        int counter = 1;
        int maxHeight = 0;
        int temp = 0;

        for (int i = 1; i < shapeQue.size(); i++) {

            ShapeTree node = shapeQue.get(i);

            Queue<ShapeTree> tempQue = new LinkedList<>();
            boolean isDuplicate = false;

            int childLeft = node.getProperties().get(ComponentProperty.LEFT);
            int childRight = node.getProperties().get(ComponentProperty.RIGHT);
            int childUp = node.getProperties().get(ComponentProperty.TOP);
            int childDown = node.getProperties().get(ComponentProperty.BOTTOM);
            int childSize = node.getProperties().get(ComponentProperty.SIZE);
            ShapeTree parent = root;

            tempQue.add(root);
            int heightCounter = 0;

            while (!tempQue.isEmpty()) {

                ShapeTree searchedParent = tempQue.poll();

                int parentLeft = searchedParent.getProperties().get(ComponentProperty.LEFT);
                int parentRight = searchedParent.getProperties().get(ComponentProperty.RIGHT);
                int parentUp = searchedParent.getProperties().get(ComponentProperty.TOP);
                int parentDown = searchedParent.getProperties().get(ComponentProperty.BOTTOM);
                int parentSize = searchedParent.getProperties().get(ComponentProperty.SIZE);

                if (parentLeft <= childLeft && parentUp <= childUp && parentDown >= childDown && parentRight >= childRight && isPointInParent(searchedParent, node)) {
                    // CHILD
                    if (childSize == parentSize && parentLeft == childLeft && parentUp == childUp && parentDown == childDown && parentRight == childRight) {
                        // DUPLIKACE
                        isDuplicate = true;
                    }
                    // NADA jiny BB

                    else {
                        // JSEM MENSI
                        parent = searchedParent;
                        heightCounter++;
                        tempQue.clear();
                        tempQue.addAll(searchedParent.getNodes());

                    }
                }
            }

            if (!isDuplicate) {
                parent.getNodes().add(node);
                if (heightCounter > maxHeight) {
                    maxHeight = heightCounter;
                }
                counter++;
                // root.print("");
            }

        }
        root.getProperties().put(ComponentProperty.G_HEIGHT, maxHeight);
        System.out.println("Building shapeTree from Que nodes[" + counter + "],time [" + (System.currentTimeMillis() - start) + "]");
        System.out.println("Counter:" + counter);
        root.setNodeCount(counter);
        return root;
    }

    private static boolean isPointInParent(ShapeTree parent, ShapeTree node) {

        for (Point p : node.getOrigNode().getPoints()) {
            int left = parent.getProperties().get(ComponentProperty.LEFT);
            int up = parent.getProperties().get(ComponentProperty.TOP);

            int parentVal = parent.getImageProcessor().get(p.y - up + 1, p.x - left + 1);
            return parentVal < 127;
        }

        return false;
    }

    private static ResultsTable getStats(ImageProcessor proc) {
        int options = ParticleAnalyzer.SHOW_PROGRESS;
        int measurements = Measurements.ALL_STATS;
        int minSize = 1;
        int maxSize = Integer.MAX_VALUE;
        ResultsTable rt = new ResultsTable();
        ParticleAnalyzer pa = new ParticleAnalyzer(options, measurements, rt, minSize, maxSize);
        pa.analyze(new ImagePlus("", proc));

        StringBuilder str = new StringBuilder();
        for (int i = 0; i <= rt.getLastColumn(); i++) {
            if (rt.columnExists(i)) {
                str.append(rt.getColumnHeading(i) + ":" + rt.getValueAsDouble(i, 0) + ",");
            }
        }

        return rt;
    }

    private static ShapeTree createShapeThreeNode(ResultsTable rt, int level, CCT node) {

        ShapeTree shapeTree = new ShapeTree();
        int size = 0;
        int round, perim, elongation;

        shapeTree.setLevel(level);
        shapeTree.setOrigNode(node);
        Map<ComponentProperty, Integer> props = shapeTree.getProperties();
        int index;

        props.put(ComponentProperty.LEFT, node.getProperties().get(ComponentProperty.LEFT));
        props.put(ComponentProperty.RIGHT, node.getProperties().get(ComponentProperty.RIGHT));
        props.put(ComponentProperty.TOP, node.getProperties().get(ComponentProperty.TOP));
        props.put(ComponentProperty.BOTTOM, node.getProperties().get(ComponentProperty.BOTTOM));

        props.put(ComponentProperty.INTENSITY, node.getProperties().get(ComponentProperty.INTENSITY));
        props.put(ComponentProperty.G_HEIGHT, 0);

        if ((index = rt.getColumnIndex("Area")) != ResultsTable.COLUMN_NOT_FOUND) {
            size = Double.valueOf(rt.getValueAsDouble(index, 0)).intValue();
            props.put(ComponentProperty.SIZE, size);
        }

        if ((index = rt.getColumnIndex("Round")) != ResultsTable.COLUMN_NOT_FOUND) {
            round = Double.valueOf(rt.getValueAsDouble(index, 0) * 100).intValue();
            props.put(ComponentProperty.ROUND, round);
        }

        if ((index = rt.getColumnIndex("Perim.")) != ResultsTable.COLUMN_NOT_FOUND) {
            perim = Double.valueOf(rt.getValueAsDouble(index, 0)).intValue();
            props.put(ComponentProperty.PERIMETER, perim);

            elongation = (int) (100 * perim * perim / (4 * Math.PI * size));

            props.put(ComponentProperty.ELONGATION, elongation);
        }

        return shapeTree;
    }

}
