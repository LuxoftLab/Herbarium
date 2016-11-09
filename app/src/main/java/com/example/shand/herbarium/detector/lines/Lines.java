package com.example.shand.herbarium.detector.lines;

import org.opencv.core.Point;

public class Lines {
    //create line between points p1 and p2
    public static Line createLine(Point p1, Point p2) {
        if (p1.x == p2.x) {
            return new VerticalLine(p1, p2);
        }
        if (p1.y == p2.y) {
            return new HorizontalLine(p1, p2);
        }

        double k = (p1.y - p2.y) / (p1.x - p2.x);
        if (Math.abs(k) <= 0.05) {
            return new HorizontalLine(p1, p2);
        }
        return new SimpleLine(p1, p2);
    }

    //calculate distance between two points
    public static double distance(Point p1, Point p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }
}
