package com.nesvadba.tomas.cct;

import java.util.List;
import java.util.Set;

import com.nesvadba.tomas.cct.domain.CCT;
import com.nesvadba.tomas.cct.domain.Point;
import com.nesvadba.tomas.cct.enums.ComponentProperty;
import com.nesvadba.tomas.cct.enums.PointStatus;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
/** 
 * Třída pro převody ImagePlus na pole bodu
 * @author Nesvadba Tomáš, učo 395902 - Diplomová práce
 * 
 */
public class Convertor {

    public static Point[][] initPoints(ImagePlus image) {
        Point[][] points = new Point[image.getHeight()][image.getWidth()];
        int h = image.getHeight();
        int w = image.getWidth();
        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {

                Point p = new Point();
                p.x = row;
                p.y = col;
                p.status = PointStatus.U;

                if (row == 0 || col == 0 || row == h - 1 || col == w - 1) {
                    p.value = 0;
                } else {
                    p.value = Double.valueOf(image.getPixel(col, row)[0]).intValue();
                }
                points[row][col] = p;
            }
        }
        return points;

    }

    public static void reinitStaturs(Point[][] imgPoints) {
        for (int row = 0; row < imgPoints.length; row++) {
            for (int col = 0; col < imgPoints[0].length; col++) {
                imgPoints[row][col].status = PointStatus.U;
            }
        }

    }

    public static ImageProcessor createBorderedImage(CCT node, int border) {

        List<Set<Point>> points = node.getAllPoints();

        int left = node.getProperties().get(ComponentProperty.LEFT);
        int right = node.getProperties().get(ComponentProperty.RIGHT);
        int up = node.getProperties().get(ComponentProperty.TOP);
        int down = node.getProperties().get(ComponentProperty.BOTTOM);

        ImageProcessor proc = new ByteProcessor(down - up + 2 * border, right - left + 2 * border);

        proc.set(255);// Black
        proc.setColor(0);

        for (Set<Point> subPoints : points) {
            for (Point p : subPoints) {
                proc.drawPixel(p.y - up + border, p.x - left + border);
            }
        }

        return proc;
    }

}
