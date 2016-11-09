package com.example.shand.herbarium.detector.lines;

import org.opencv.core.Point;

import java.util.Iterator;
import java.util.NoSuchElementException;


class HorizontalLine implements Line {
    private double y, leftX, rightX;

    public HorizontalLine(Point p1, Point p2) {
        y = p1.y;

        if (p1.x < p2.x) {
            leftX = p1.x;
            rightX = p2.x;
        } else {
            leftX = p2.x;
            rightX = p1.x;
        }
    }

    public double length() {
        return rightX - leftX;
    }

    public Point getStart() {
        return new Point(leftX, y);
    }

    public Point getEnd() {
        return new Point(rightX, y);
    }

    public Iterable<Point> moveAlongLine(final int numSteps) {
        return new Iterable<Point>() {
            public Iterator<Point> iterator() {
                return new Iterator<Point>() {
                    double length = length();
                    double step = length / numSteps;
                    double currentX = leftX;
                    int i = 0;
                    int num = numSteps;

                    @Override
                    public boolean hasNext() {
                        return (i + 1 < num);
                    }

                    @Override
                    public Point next() throws NoSuchElementException {
                        if (hasNext()) {
                            currentX += step;
                            i ++;
                            return new Point(currentX, y);
                        }
                        throw new NoSuchElementException();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Line perpendicular(Point point, Point p1, Point p2) {
        return new VerticalLine(new Point(point.x, p1.y), new Point(point.x, p2.y));
    }
}

