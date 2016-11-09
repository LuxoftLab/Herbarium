package com.example.shand.herbarium.detector.lines;

import org.opencv.core.Point;

public interface Line {
    Iterable<Point> moveAlongLine(int numSteps); //get numSteps points of line in regular intervals
    double length(); //get line length
    Line perpendicular(Point point, Point p1, Point p2); //get perpendicular line
    Point getStart(); //get point - start of line
    Point getEnd(); //get point - end of line
}
