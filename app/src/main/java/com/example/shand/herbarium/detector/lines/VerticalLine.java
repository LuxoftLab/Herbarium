package com.example.shand.herbarium.detector.lines;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.opencv.core.Point;

class VerticalLine implements Line {
    private double x, topY, bottomY;

    public VerticalLine(Point p1, Point p2) {
        x = p1.x;

        if (p1.y < p2.y) {
            bottomY = p1.y;
            topY = p2.y;
        } else {
            bottomY = p2.y;
            topY = p1.y;
        }
    }

    public double length() {
        return topY - bottomY;
    }

    public Point getStart() {
        return new Point(x, bottomY);
    }

    public Point getEnd() {
        return new Point(x, topY);
    }

    public Iterable<Point> moveAlongLine(final int numSteps) {
        return new Iterable<Point>() {
            public Iterator<Point> iterator() {
                return new Iterator<Point>() {
                    double length = length();
                    double step = length / numSteps;
                    double currentY = bottomY;
                    int i = 0;
                    int num = numSteps;

                    @Override
                    public boolean hasNext() {
                        return (i + 1 < num);
                    }

                    @Override
                    public Point next() throws NoSuchElementException {
                        if (hasNext()) {
                            currentY += step;
                            i ++;
                            return new Point(x, currentY);
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
        return new HorizontalLine(new Point(p1.x, point.y), new Point(p2.x, point.y));
    }
}