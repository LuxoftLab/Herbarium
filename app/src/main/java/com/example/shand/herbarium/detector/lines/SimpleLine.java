package com.example.shand.herbarium.detector.lines;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.opencv.core.Point;

class SimpleLine implements Line {
    private double k, b;
    private Point right, left;

    public SimpleLine(Point p1, Point p2) {
        if (p1.x < p2.x) {
            left = p1;
            right = p2;
        } else {
            left = p2;
            right = p1;
        }

        k = (p1.y - p2.y) / (p1.x - p2.x);
        b = -(p1.y - p2.y) * p2.x / (p1.x - p2.x) + p2.y;
    }

    public SimpleLine(double k, double b, Point p1, Point p2) {
        this.k = k;
        this.b = b;

        if (p1.x < p2.x) {
            left = p1;
            right = p2;
        } else {
            left = p2;
            right = p1;
        }

        left = new Point(left.x, k * left.x + b);
        right = new Point(right.x, k * right.x + b);
    }

    public double length() {
        return Lines.distance(left, right);
    }

    public double getY(double x) {
        return k * x + b;
    }

    public Point getStart() {
        return left.clone();
    }

    public Point getEnd() {
        return right.clone();
    }

    public Iterable<Point> moveAlongLine(final int numSteps) {
        return new Iterable<Point>() {
            public Iterator<Point> iterator() {
                return new Iterator<Point>() {
                    double length = length();
                    double step = length / numSteps;
                    int num = numSteps;
                    Point currentPoint = left;
                    int i = 0;

                    @Override
                    public boolean hasNext() {
                        return (i + 1 < num);
                    }

                    @Override
                    public Point next() throws NoSuchElementException {
                        if (hasNext()) {
                            double dx = step / (Math.sqrt(k * k + 1));
                            double dy = dx * k;
                            currentPoint = new Point(currentPoint.x + dx, currentPoint.y + dy);
                            i ++;
                            return currentPoint;
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

    public Point[] lineCircleIntersection(Point center, double rad) {
        double A = (k * k + 1);
        double B = -2 * (center.x + k * (b - center.y));
        double C = (center.x * center.x + (b - center.y) * (b - center.y) - rad * rad);

        double D = B * B - 4 * A * C;
        if (D < 0) {
            return new Point[]{};
        }
        if (D == 0) {
            double x = (-B / (2 * A));
            double y = getY(x);
            return new Point[]{new Point(x, y)};
        }
        double x1 = (-B + Math.sqrt(D)) / (2 * A);
        double x2 = (-B - Math.sqrt(D)) / (2 * A);
        double y1 = getY(x1);
        double y2 = getY(x2);
        return new Point[]{new Point(x1, y1), new Point(x2, y2)};
    }

    public Line perpendicular(Point point, Point p1, Point p2) {
        double k1 = -1 / k;
        double b1 = point.y - k1 * point.x;
        return new SimpleLine(k1, b1, p1, p2);
    }
}